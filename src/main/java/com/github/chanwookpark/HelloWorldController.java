package com.github.chanwookpark;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author chanwook
 */
@RestController
public class HelloWorldController {

    @RequestMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("hello~")
                .log()
                .map(s -> s.toUpperCase())
                .publishOn(Schedulers.newSingle("new-single"));
    }
}
