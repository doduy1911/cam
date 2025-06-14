package com.example.retime_cam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import java.time.Duration;

@RestController
@RequestMapping("/api")
public class FrameController {
    @Autowired
    private FrameStorage frameStorage;

    @Autowired
    private FrameWebSocketHandler webSocketHandler;

    @GetMapping(value = "/stream-frames/{userId}/{deviceId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public Mono<byte[]> streamFrames(@PathVariable String userId, @PathVariable String deviceId) {
        webSocketHandler.requestFrame(userId, deviceId);
        return Mono.justOrEmpty(frameStorage.getFrame(userId, deviceId))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No frame available")))
                .delayElement(Duration.ofMillis(50));
    }

    @PostMapping(value = "/upload-frame/{userId}/{deviceId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> uploadFrame(@PathVariable String userId, @PathVariable String deviceId,
                                    @RequestPart("frame") MultipartFile frame) {
        return Mono.fromCallable(() -> {
            frameStorage.saveFrame(userId, deviceId, frame.getBytes());
            return "OK";
        }).onErrorResume(e -> Mono.just("ERROR"));
    }
}
