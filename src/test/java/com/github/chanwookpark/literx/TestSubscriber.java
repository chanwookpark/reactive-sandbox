package com.github.chanwookpark.literx;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

/**
 * @author chanwook
 */
public class TestSubscriber<T> implements Subscriber<T> {

    private volatile int valueCount = 0;

    private int completionCount = 0;

    /**
     * 전달 받은 Flux가 publisher고, 이 publisher의 subscriber로 TestSubscriber 인스턴스를 등록
     *
     * @param publisher
     * @param <T>
     * @return
     */
    public static <T> TestSubscriber<T> subscribe(Flux<T> publisher) {
        final TestSubscriber<T> subscriber = new TestSubscriber<>();
        publisher.subscribe(subscriber);
        return subscriber;
    }

    @Override
    public void onSubscribe(Subscription subscription) {

    }

    @Override
    public void onNext(T t) {
        ++valueCount;
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {
        ++completionCount;
    }

    public TestSubscriber<T> assertValueCount(int expected) {
        if (valueCount != expected) {
            throw new AssertionError("Difference value count (real: " + valueCount + ", expected: " + expected + ")");
        }
        return this;
    }

    public TestSubscriber<T> assertComplete() {

        int currentCompletionCount = completionCount;
        if (currentCompletionCount == 0) {
            throw new AssertionError("Not completed!");
        } else if (currentCompletionCount > 1) {
            throw new AssertionError("Multiple completions! (count: " + currentCompletionCount + ")");
        }
        return this;
    }
}
