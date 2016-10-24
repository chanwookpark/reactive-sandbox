package com.github.chanwookpark.literx;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Operators;

import java.util.*;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @author chanwook
 */
public class TestSubscriber<T> implements Subscriber<T> {

    private volatile int valueCount = 0;

    private int completionCount = 0;

    private int subscriptionCount;

    volatile long requested;

    volatile List<T> values = new LinkedList<>();

    volatile Subscription subscription;

    private static final AtomicReferenceFieldUpdater<TestSubscriber, Subscription> S =
            AtomicReferenceFieldUpdater.newUpdater(TestSubscriber.class, Subscription.class, "subscription");

    private static final AtomicLongFieldUpdater<TestSubscriber> REQUESTED =
            AtomicLongFieldUpdater.newUpdater(TestSubscriber.class, "requested");

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
        subscriptionCount++;

        //FIXME 도대체 아래 로직은 왜 하는걸까..?
        Subscription s = this.subscription;
        if (Operators.cancelledSubscription() == s) {
            subscription.cancel();
            postCancel(subscription);
        } else if (s != null) {
            subscription.cancel();
            Operators.reportSubscriptionSet();
            postCancel(subscription);
        }

        if (S.compareAndSet(this, null, subscription)) {
            long r = REQUESTED.getAndSet(this, 0L);

            if (r != 0L) {
                subscription.request(r);
            }

        }
    }

    private void postCancel(Subscription subscription) {
        //TODO
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
            throw new AssertionError("Difference value count (actual: " + valueCount + ", expected: " + expected + ")");
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

    public TestSubscriber<T> assertValues(T... expectedValues) {

        if (expectedValues.length != values.size()) {
            throw new AssertionError("Not equals of values (expected size: " + expectedValues.length + ", actual size: " + values.size() + ")");
        }

        Iterator<T> actualIterator = values.iterator();
        final Iterator<T> expectedIterator = Arrays.asList(expectedValues).iterator();

        while (actualIterator.hasNext()) {
            T actual = actualIterator.next();
            T expected = expectedIterator.next();

            if (!Objects.equals(actual, expected)) {
                throw new AssertionError("Value does not equals! (expected: " + expected + ", actual: " + actual + ")");
            }
        }

        return this;
    }
}
