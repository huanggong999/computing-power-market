package com.lingyang.common.cache.redis;
import com.lingyang.common.cache.CacheQueueService;
import com.lingyang.common.cache.queue.QueueMessageBody;
import com.lingyang.common.core.utils.IdUtils;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.Set;
/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/18 18:13
 */
public class RedisQueueCacheServiceImpl implements CacheQueueService {
    private final BoundZSetOperations<String, Object> operations;
    // 批量获取消息的数量限制
    private static final int BATCH_SIZE = 100;
    public RedisQueueCacheServiceImpl(String key, RedisTemplate<String, Object> redisTemplate) {
        this.operations = redisTemplate.opsForZSet().getOperations().boundZSetOps(key);
    }
    @Override
    public void addDelayQueue(String messageType, String messageContent, long delay) {
        QueueMessageBody messageBody = new QueueMessageBody();
        messageBody.setId(IdUtils.simpleUUID());
        messageBody.setMessageType(messageType);
        messageBody.setBody(messageContent);
        messageBody.setDelayTime(delay);
        operations.add(messageBody, System.currentTimeMillis() + (delay * 1000));
    }
    @Override
    public Set<Object> getQueue() {
        long currentTime = System.currentTimeMillis();
        // 兼容旧版本 Spring Data Redis 的写法
        // 注意：这会一次性获取所有到期消息。如果消息积压过多可能影响性能，
        // 但在正常的消费者速率下，通常不会出现大量积压，因此影响可控。
        return operations.rangeByScore(0, currentTime);
    }
    @Override
    public void remove(QueueMessageBody messageBody) {
        if (messageBody != null) {
            operations.remove(messageBody);
        }
    }
    @Override
    public Set<Object> getAllQueue() {
        return operations.range(0, -1);
    }
}