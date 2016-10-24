package com.github.chanwookpark.literx;

import org.junit.Test;
import reactor.core.publisher.Flux;

/**
 * @author chanwook
 */
public class Step1Test {

    @Test
    public void createEmptyFlux() throws Exception {

        final Flux<String> empty = Flux.empty();
        TestSubscriber
                .subscribe(empty)
                .assertValueCount(0)
                .assertComplete();
    }
}
