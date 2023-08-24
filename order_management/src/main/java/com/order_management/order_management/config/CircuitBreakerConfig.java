// package com.order_management.order_management.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import io.github.resilience4j.circuitbreaker.CircuitBreaker;
// import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
// import io.github.resilience4j.retry.Retry;
// import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Supplier;
// import io.vavr.control.Try;

// public class CircuitBreakerConfig {
//     CircuitBreakerConfig circuitBreakerConfig(){
//         // Create a CircuitBreaker (use default configuration)
//         CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("backendName");
//         // Create a Retry with at most 3 retries and a fixed time interval between retries of 500ms
//         Retry retry = Retry.ofDefaults("backendName");

//         // Decorate your call to BackendService.doSomething() with a CircuitBreaker
//         Supplier<String> decoratedSupplier = CircuitBreaker
//             .decorateSupplier(circuitBreaker, backendService::doSomething);

//         // Decorate your call with automatic retry
//         decoratedSupplier = Retry
//             .decorateSupplier(retry, decoratedSupplier);

//         // Use of Vavr's Try to
//         // execute the decorated supplier and recover from any exception
//         String result = Try.ofSupplier(decoratedSupplier)
//             .recover(throwable -> "Hello from Recovery").get();

//         // When you don't want to decorate your lambda expression,
//         // but just execute it and protect the call by a CircuitBreaker.
//         String result = circuitBreaker.executeSupplier(backendService::doSomething);
//     }
// }
