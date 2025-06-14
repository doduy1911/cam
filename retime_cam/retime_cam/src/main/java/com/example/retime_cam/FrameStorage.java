package com.example.retime_cam;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class FrameStorage {
    private final ConcurrentHashMap<String, byte[]> frameMap = new ConcurrentHashMap<>();

    public void saveFrame(String userId, String deviceId, byte[] frame) {
        frameMap.put(userId + ":" + deviceId, frame);
    }

    public byte[] getFrame(String userId, String deviceId) {
        return frameMap.get(userId + ":" + deviceId);
    }
}
