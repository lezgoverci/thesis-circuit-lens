import cv2
import math
import numpy as np

class BasicFunctions:
    @staticmethod
    def crop(img, boundingRect):
        (x, y, w, h) = boundingRect
        return img[y: y + h, x: x + w]
    
    @staticmethod
    def contourMarker(img, contours, color=(0, 255, 0), thickness=2):
        if None == contours:
            return
        
        for cnt in contours:
            x,y,w,h = cv2.boundingRect(cnt)
            cv2.rectangle(img, (x, y), (x + w, y + h), color, thickness)
    
    @staticmethod
    def getAngleBetweenTwoVectors(a, b):
        dot_product = (a[0] * b[0]) + (a[1] * b[1])
        a_mag = math.sqrt(math.pow(a[0], 2) + math.pow(a[1], 2))
        b_mag = math.sqrt(math.pow(b[0], 2) + math.pow(b[1], 2))
        
        if 0 == a_mag or 0 == b_mag:
            return math.radians(90)
        
        return math.acos(round(dot_product / (a_mag * b_mag), 2))
    
    @staticmethod
    def thinningIteration(im, num_iter):
        I, M = im, np.zeros(im.shape, np.uint8)
        
        h, w = im.shape[:2]
        
        i = 0
        while i < h:
            if i - 1 < 0 or i + 1 >= h:
                i += 1
                continue
            j = 0
            while j < w:
                if j - 1 < 0 or j + 1 >= w:
                    j += 1
                    continue
                
                p2 = I[i-1][j]
                p3 = I[i-1][j+1]
                p4 = I[i][j+1]
                p5 = I[i+1][j+1]
                p6 = I[i+1][j]
                p7 = I[i+1][j-1]
                p8 = I[i][j-1]
                p9 = I[i-1][j-1]
                A  = (p2 == 0 and p3 == 1) + (p3 == 0 and p4 == 1) + (p4 == 0 and p5 == 1) + (p5 == 0 and p6 == 1) + (p6 == 0 and p7 == 1) + (p7 == 0 and p8 == 1) + (p8 == 0 and p9 == 1) + (p9 == 0 and p2 == 1)
                B  = p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9
                m1 = (p2 * p4 * p6) if num_iter == 0 else (p2 * p4 * p8)
                m2 = (p4 * p6 * p8) if num_iter == 0 else (p2 * p6 * p8)
                if A == 1 and B >= 2 and B <= 6 and m1 == 0 and m2 == 0:
                    M[i][j] = 1
                    
                j += 1
            i += 1

        return cv2.bitwise_and(I, cv2.bitwise_not(M))

    @staticmethod
    def thinning(src):
        dst = src.copy() / 255
        prev = np.zeros(src.shape[:2], np.uint8)
        diff = None

        while True:
            dst = BasicFunctions.thinningIteration(dst, 0)
            dst = BasicFunctions.thinningIteration(dst, 1)
            diff = np.absolute(dst - prev)
            prev = dst.copy()
            if np.sum(diff) == 0:
                break

        return dst * 255
    
    @staticmethod
    def getBorders(center, half_size, (h, w)):
        base_x = [center[0] - half_size, center[0] + half_size]
        base_y = [center[1] - half_size, center[1] + half_size]
        
        base_x[0] = base_x[0] if base_x[0] >= 0 else 0
        base_x[1] = base_x[1] if base_x[1] < w else w - 1
        
        base_y[0] = base_y[0] if base_y[0] >= 0 else 0
        base_y[1] = base_y[1] if base_y[1] < h else h - 1
        
        return [
            ([base_x[0], base_y[0]], [base_x[1], base_y[0]], (base_x[0], base_y[0] + 1), (1, 0)), #top
            ([base_x[1], base_y[0] + 1], [base_x[1], base_y[1] - 1], (base_x[1], base_y[0]), (0, 1)), #right
            ([base_x[1], base_y[1]], [base_x[0], base_y[1]], (base_x[1], base_y[1] - 1), (-1, 0)), #bottom
            ([base_x[0], base_y[1] - 1], [base_x[0], base_y[0] + 1], (base_x[0], base_y[1]), (0, -1)), #left
        ]
        