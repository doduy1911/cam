import cv2
import requests
import time

user_id = "1"
device_id = "1"
upload_url = f"http://42.116.105.110:3000/api/upload-frame/{user_id}/{device_id}"

cap = cv2.VideoCapture(0)
cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)
cap.set(cv2.CAP_PROP_FPS, 30)

try:
    while True:
        ret, frame = cap.read()
        if not ret:
            break
        _, img_encoded = cv2.imencode('.jpg', frame, [int(cv2.IMWRITE_JPEG_QUALITY), 80])
        files = {'frame': ('frame.jpg', img_encoded.tobytes(), 'image/jpeg')}
        try:
            requests.post(upload_url, files=files)
        except:
            time.sleep(1)
        time.sleep(0.033)
except KeyboardInterrupt:
    pass
finally:
    cap.release()