package com.example.retime_cam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class FrameController {
    @Autowired
    private FrameStorage frameStorage;

    @GetMapping(value = "/stream-frames/{userId}/{deviceId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public Mono<byte[]> streamFrames(@PathVariable String userId, @PathVariable String deviceId) {
        return Mono.justOrEmpty(frameStorage.getFrame(userId, deviceId))
                .switchIfEmpty(Mono.error(new RuntimeException("Không có frame")))
                .delayElement(Duration.ofMillis(50));
    }

    @PostMapping(value = "/upload-frame/{userId}/{deviceId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> uploadFrame(@PathVariable String userId, @PathVariable String deviceId,
                                    @RequestPart("frame") MultipartFile frame) {
        return Mono.fromCallable(() -> {
            byte[] frameBytes = frame.getBytes();
            frameStorage.saveFrame(userId, deviceId, frameBytes);
            return "Frame uploaded successfully";
        }).onErrorResume(e -> Mono.just("Error uploading frame"));
    }
}

