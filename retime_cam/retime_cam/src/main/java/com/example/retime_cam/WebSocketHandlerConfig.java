package com.example.retime_cam;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocketHandlerConfig {
    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    public SimpleUrlHandlerMapping handlerMapping(FrameWebSocketHandler frameWebSocketHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/ws/frames", frameWebSocketHandler);
        map.put("/ws/frames/**", frameWebSocketHandler);
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(-1);
        mapping.setCorsConfigurations(Map.of("*", new CorsConfiguration().applyPermitDefaultValues()));
        return mapping;
    }
}
