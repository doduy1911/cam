package com.example.retime_cam;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FrameWebSocketHandler {
    private final ConcurrentHashMap<String, Sinks.Many<String>> clientSinks = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String query = session.getHandshakeInfo().getUri().getQuery();
        String userId = null, deviceId = null;
        for (String param : query.split("&")) {
            String[] parts = param.split("=");
            if (parts[0].equals("userId")) userId = parts[1];
            if (parts[0].equals("deviceId")) deviceId = parts[1];
        }
        if (userId == null || deviceId == null) {
            return Mono.empty();
        }
        String clientKey = userId + ":" + deviceId;

        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        clientSinks.put(clientKey, sink);

        return session.receive()
                .then()
                .doFinally(signal -> clientSinks.remove(clientKey))
                .then(sink.asFlux()
                        .map(session::textMessage)
                        .flatMap(message -> session.send(Mono.just(message)))
                        .then());
    }

    public void requestFrame(String userId, String deviceId) {
        String clientKey = userId + ":" + deviceId;
        Sinks.Many<String> sink = clientSinks.get(clientKey);
        if (sink != null) {
            sink.tryEmitNext("request_frame");
        }
    }
}
