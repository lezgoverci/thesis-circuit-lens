import cv2

from class_noise_remover import NoiseRemover

class PrintedCircuitNoiseRemover(NoiseRemover):
    def filter(self, img):
        h, w = img.shape[:2]
        
        max_dim = h if h > w else w
        
        window_side = int((25 * max_dim) / 960)
        
        if 0 == window_side % 2:
            window_side += 1
        
        img = cv2.GaussianBlur(img, (window_side, window_side), 0)
        img = cv2.adaptiveThreshold(img,255,cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY_INV, 11,2)

        return img