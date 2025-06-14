package com.example.retime_cam;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class FrameStorage {
    private static final Logger logger = LoggerFactory.getLogger(FrameStorage.class);
    private final ConcurrentHashMap<String, byte[]> frameMap = new ConcurrentHashMap<>();

    public void saveFrame(String userId, String deviceId, byte[] frame) {
        String key = userId + ":" + deviceId;
        frameMap.put(key, frame);
        logger.info("Đã lưu frame cho key: {}, kích thước: {}", key, frame.length);
    }

    public byte[] getFrame(String userId, String deviceId) {
        String key = userId + ":" + deviceId;
        byte[] frame = frameMap.get(key);
        logger.info("Lấy frame cho key: {}, frame: {}", key, frame != null ? "có" : "null");
        return frame;
    }
}
