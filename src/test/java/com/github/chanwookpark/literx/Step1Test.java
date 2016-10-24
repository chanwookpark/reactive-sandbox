package com.github.chanwookpark.literx;

import org.junit.Test;
import reactor.core.publisher.Flux;

/**
 * @author chanwook
 */
public class Step1Test {

    /**
     * Part01CreateFlux#empty
     *
     * @throws Exception
     */
    @Test
    public void createEmptyFlux() throws Exception {

        final Flux<String> empty = Flux.empty();
        TestSubscriber
                .subscribe(empty)
                .assertValueCount(0)
                .assertComplete();
    }

    @Test
    public void fromValues() throws Exception {
        final Flux<String> flux = Flux.just("foo", "bar");
        TestSubscriber
                .subscribe(flux)
                .assertValues("foo", "bar")
                .assertComplete();


    }
}
