import cv2
import requests
import websocket
import threading
import time

user_id = "1"
device_id = "1"
upload_url = f"http://42.116.105.110:3000/api/upload-frame/{user_id}/{device_id}"
ws_url = f"ws://42.116.105.110:3000/ws/frames?userId={user_id}&deviceId={device_id}"

cap = cv2.VideoCapture(0)
cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)
cap.set(cv2.CAP_PROP_FPS, 30)

def on_message(ws, message):
    if message == "request_frame":
        ret, frame = cap.read()
        if ret:
            _, img_encoded = cv2.imencode('.jpg', frame, [int(cv2.IMWRITE_JPEG_QUALITY), 80])
            files = {'frame': ('frame.jpg', img_encoded.tobytes(), 'image/jpeg')}
            try:
                requests.post(upload_url, files=files)
            except:
                pass

def on_error(ws, error):
    pass

def on_close(ws, code, reason):
    time.sleep(5)
    ws.run_forever()

def on_open(ws):
    pass

ws = websocket.WebSocketApp(ws_url,
                            on_message=on_message,
                            on_error=on_error,
                            on_close=on_close,
                            on_open=on_open)

thread = threading.Thread(target=ws.run_forever)
thread.daemon = True
thread.start()

try:
    while True:
        time.sleep(1)  # Giữ chương trình chạy
except KeyboardInterrupt:
    pass
finally:
    cap.release()
    ws.close()
