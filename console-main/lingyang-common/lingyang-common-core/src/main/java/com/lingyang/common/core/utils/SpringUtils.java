package com.lingyang.common.core.utils;

import com.lingyang.common.core.exception.MethodExecutionException;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/7 13:50
 */
@Component
public class SpringUtils implements BeanFactoryPostProcessor, ApplicationContextAware {
    /**
     * Spring应用上下文环境
     */
    private static ConfigurableListableBeanFactory beanFactory;

    @Getter
    private static ApplicationContext applicationContext;

    /**
     * 获取当前环境
     * @return 环境， dev - 开发环境，test - 测试环境，prod - 生产环境
     */
    public static String getActive() {
        return applicationContext.getEnvironment().getActiveProfiles()[applicationContext.getEnvironment().getActiveProfiles().length - 1];
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringUtils.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    /**
     * 获取对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) beanFactory.getBean(name);
    }

    /**
     * 获取bean
     * @param beanClass bean类型
     * @param defaultBean 默认bean对象
     * @return 当容器中不存在当前bean类型时，返回默认 defaultBean， 并将defaultBean添加到容器
     * @param <T> bean类型
     */
    public static <T> T getBean(Class<T> beanClass, T defaultBean) {
        T bean;
       try {
           bean = getBean(beanClass);
       }catch (BeansException e) {
           bean = defaultBean;
           registerBean(beanClass.getName(), defaultBean);
       }
       return bean;
    }

    /**
     * 获取类型为requiredType的对象
     */
    public static <T> T getBean(Class<T> clz) throws BeansException {
        return beanFactory.getBean(clz);
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     */
    public static boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.isSingleton(name);
    }


    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getType(name);
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getAliases(name);
    }

    /**
     * 获取aop代理对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker) {
        return (T) AopContext.currentProxy();
    }


    public static void registerBean(String beanName, Object bean) {
        Objects.requireNonNull(bean);
        Class<?> beanClass = bean.getClass();
        if (StringUtils.isEmpty(beanName)) {
            beanName = beanClass.getName();
        }
        if (beanFactory.containsBean(beanName)) {
            Object springBean = beanFactory.getBean(beanName);
            if (!springBean.getClass().equals(beanClass)) {
                throw new MethodExecutionException("BeanName 重复 " + beanName);
            }
            return;
        }
        beanFactory.registerSingleton(beanName, bean);
    }
}
