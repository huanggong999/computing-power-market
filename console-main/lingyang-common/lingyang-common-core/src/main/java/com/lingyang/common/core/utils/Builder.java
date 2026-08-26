package com.lingyang.common.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/4/23 10:34
 */
public class Builder<T> {

    protected final Supplier<T> instancing;

    protected List<Consumer<T>> modifiers = new ArrayList<>();

    public Builder(Supplier<T> instancing) {
        this.instancing = instancing;
    }

    public static <T> Builder<T> of(Supplier<T> instancing) {
        return new Builder<>(instancing);
    }

    public static <T> Builder<T> of(T instancing) {
        return new Builder<>(() -> instancing);
    }

    public <P> Builder<T> set(BuildConsumer<T, P> consumer, P param) {
        modifiers.add(instancing -> consumer.accept(instancing, param));
        return this;
    }
    public <P> Builder<T> set(BuildConsumer<T, P> consumer, BuildParamFunction<P> param) {
        return set(true, consumer, param);
    }

    public <P> Builder<T> set(boolean condition, BuildConsumer<T, P> consumer, BuildParamFunction<P> param) {
        if (condition) {
            modifiers.add(instancing -> consumer.accept(instancing, param.accept()));
        }
        return this;
    }

    public T build() {
        T t = instancing.get();
        modifiers.forEach(consumer -> consumer.accept(t));
        return t;
    }

    @FunctionalInterface
    public interface BuildConsumer<T, P1> {
        void accept(T t, P1 p1);
    }

    @FunctionalInterface
    public interface BuildParamFunction<T> {
        T accept();
    }
}
