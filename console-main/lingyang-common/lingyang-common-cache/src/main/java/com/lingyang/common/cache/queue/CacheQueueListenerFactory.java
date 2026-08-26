package com.lingyang.common.cache.queue;
import com.lingyang.common.cache.CacheQueueService;
import com.lingyang.common.cache.config.CacheQueueConfig;
import com.lingyang.common.core.lock.DistributedLock;
import com.lingyang.common.core.lock.distributed.DistributedExecute;
import com.lingyang.common.core.utils.Optional;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
/**
 * @Description: 队列消费者
 * @Author: 王小龙
 * @Date: 2024/11/18 18:53
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "cache.queue.enable", havingValue = "true")
public class CacheQueueListenerFactory implements ApplicationListener<ContextClosedEvent> {
    @Resource
    private CacheQueueConfig cacheQueueConfig;
    @Resource
    private DistributedLock distributedLock;
    @Resource(name = "cache-queue-consumer-executor")
    private ThreadPoolTaskExecutor executor;
    @Resource
    private CacheQueueService cacheQueueService;
    private Thread mainThread;
    private volatile boolean start;
    private final Map<String, CacheQueueConsumer> cacheQueueConsumerMap;
    // 默认轮询间隔：100ms
    private static final long DEFAULT_POLL_INTERVAL = 100;
    // 空队列时轮询间隔：1s
    private static final long EMPTY_QUEUE_INTERVAL = 1000;
    public CacheQueueListenerFactory(@Autowired(required = false) List<CacheQueueConsumer> cacheQueueConsumers) {
        start = true;
        if (ObjectUtils.isNotEmpty(cacheQueueConsumers)) {
            cacheQueueConsumerMap = new ConcurrentHashMap<>(cacheQueueConsumers.size());
            for (CacheQueueConsumer cacheQueueConsumer : cacheQueueConsumers) {
                cacheQueueConsumerMap.put(cacheQueueConsumer.messageType(), cacheQueueConsumer);
            }
        } else {
            cacheQueueConsumerMap = new HashMap<>(0);
        }
    }
    @PostConstruct
    public void init() {
        this.mainThread = new Thread(() -> {
            log.info("CacheQueueListenerFactory start");
            while (start) {
                try {
                    // 1. 先轻量级检查队列中是否有到期消息
                    Set<Object> messageList = cacheQueueService.getQueue();
                    boolean hasMessage = ObjectUtils.isNotEmpty(messageList);
                    if (hasMessage) {
                        // 2. 只有在队列有消息时，才尝试获取分布式锁
                        DistributedExecute.of(distributedLock)
                                .lock(cacheQueueConfig.getQueueKey() + "-lock", 3000L, TimeUnit.MILLISECONDS)
                                .onSuccessFunction(o -> {
                                    // 3. 获取锁成功后，再次获取并处理消息，防止锁竞争期间消息被其他实例处理
                                    Set<Object> freshMessageList = cacheQueueService.getQueue();
                                    if (ObjectUtils.isNotEmpty(freshMessageList)) {
                                        processMessages(freshMessageList);
                                    }
                                });
                        // 如果锁获取失败，循环会继续，并在下面进行休眠
                    }
                    // 4. 根据队列状态动态调整休眠时间
                    long sleepTime = hasMessage ? DEFAULT_POLL_INTERVAL : EMPTY_QUEUE_INTERVAL;
                    TimeUnit.MILLISECONDS.sleep(sleepTime);
                } catch (InterruptedException e) {
                    log.info("CacheQueueListenerFactory thread interrupted");
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    if (!start || isConnectionFactoryDestroyed(e)) {
                        log.info("CacheQueueListenerFactory stop due to context shutdown");
                        break;
                    }
                    log.error("CacheQueueListenerFactory error: ", e);
                    try {
                        TimeUnit.MILLISECONDS.sleep(EMPTY_QUEUE_INTERVAL);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }, "CacheQueue-Listener");
        this.mainThread.start();
    }
    private boolean isConnectionFactoryDestroyed(Throwable e) {
        Throwable cur = e;
        while (cur != null) {
            String msg = cur.getMessage();
            if (msg != null && msg.contains("LettuceConnectionFactory was destroyed")) {
                return true;
            }
            cur = cur.getCause();
        }
        return false;
    }
    private void processMessages(Set<Object> messageList) {
        for (Object message : messageList) {
            if (message instanceof QueueMessageBody queueMessageBody) {
                Optional.of(cacheQueueConsumerMap.get(queueMessageBody.getMessageType()))
                        .ifPresent(cacheQueueConsumer -> {
                            // 先移除消息再处理，确保消息不丢失
                            cacheQueueService.remove(queueMessageBody);
                            executor.execute(() -> {
                                try {
                                    cacheQueueConsumer.invoke(queueMessageBody.getBody());
                                } catch (Exception e) {
                                    // 异常时重新加入延迟队列
                                    cacheQueueService.addDelayQueue(
                                            queueMessageBody.getMessageType(),
                                            queueMessageBody.getBody(),
                                            300
                                    );
                                    log.error("{} 队列执行异常，重新进入队列：", queueMessageBody.getMessageType(), e);
                                }
                            });
                        });
            }
        }
    }
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("CacheQueueListenerFactory closing ...");
        start = false;
        // 关键修改：获取底层的 JDK ThreadPoolExecutor 来执行强制关闭和等待操作
        if (executor != null) {
            executor.getThreadPoolExecutor();// 1. 调用 Spring 封装的 shutdown() 方法，停止接收新任务
            executor.shutdown();
            try {
                // 2. 获取底层的 ThreadPoolExecutor 实例
                java.util.concurrent.ThreadPoolExecutor jdkExecutor = executor.getThreadPoolExecutor();
                // 3. 使用 jdkExecutor 的 awaitTermination 等待任务完成
                if (!jdkExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    log.warn("CacheQueueListenerFactory executor did not terminate in 10 seconds, proceeding to shutdown now.");
                    // 4. 如果超时，调用 jdkExecutor 的 shutdownNow 强制关闭
                    jdkExecutor.shutdownNow();
                    if (!jdkExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                        log.error("CacheQueueListenerFactory executor pool did not terminate after shutdownNow.");
                    }
                }
            } catch (InterruptedException e) {
                log.error("CacheQueueListenerFactory executor shutdown interrupted.", e);
                // 如果等待过程被中断，也尝试强制关闭
                executor.getThreadPoolExecutor().shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        if (mainThread != null) {
            mainThread.interrupt();
            try {
                mainThread.join(5000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        log.info("CacheQueueListenerFactory closed ...");
    }
}