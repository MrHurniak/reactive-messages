package com.reactive.example.messages.event.publisher;

import com.reactive.example.messages.event.message.MessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageEventPublisher implements ApplicationListener<MessageEvent>, Consumer<FluxSink<MessageEvent>> {

    private final BlockingQueue<MessageEvent> queue = new LinkedBlockingQueue<>();
    private final Executor executor;

    @Override
    public void onApplicationEvent(MessageEvent event) {
        this.queue.offer(event);
    }

    @Override
    public void accept(FluxSink<MessageEvent> sink) {
        this.executor.execute(() -> {
            while (true) {
                try {
                    MessageEvent event = queue.take();
                    sink.next(event);
                } catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
            }
        });
    }
}
