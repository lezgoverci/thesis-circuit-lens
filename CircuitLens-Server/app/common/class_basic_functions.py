import numpy as np
import math
import cv2

class BasicFunctions:
    @staticmethod
    def calculateAngle(a, b, directionMatters=True):
        a_mag = np.linalg.norm(a)
        b_mag = np.linalg.norm(b)
        
        angle = math.degrees(math.acos(round(np.vdot(a, b) / (a_mag * b_mag), 4)))
        
        if directionMatters and b[1] - a[1] < 0:
            angle = 360 - angle
        
        return angle
    
    @staticmethod
    def lineIntersection(line1, line2):
        xdiff = (line1[0][0] - line1[1][0], line2[0][0] - line2[1][0])
        ydiff = (line1[0][1] - line1[1][1], line2[0][1] - line2[1][1])

        def det(a, b):
            return a[0] * b[1] - a[1] * b[0]

        div = det(xdiff, ydiff)
        if div == 0:
            raise Exception('lines do not intersect')

        d = (det(*line1), det(*line2))
        x = det(d, xdiff) / div
        y = det(d, ydiff) / div
        
        return np.array([x, y, 0.0])
    
    @staticmethod
    def calculateAngle(a, b, directionMatters=True):
        a_mag = np.linalg.norm(a)
        b_mag = np.linalg.norm(b)
        
        temp_angle = math.degrees(math.acos(round(np.vdot(a, b) / (a_mag * b_mag), 4)))
        
        if directionMatters and b[1] - a[1] < 0:
            temp_angle = 360 - temp_angle
        
        return temp_angle
        
    @staticmethod
    def calculateTriangleCentroid((a, b, c)):
        l1 = ((a + b) / 2, c)
        l2 = ((b + c) / 2, a)

        return BasicFunctions.lineIntersection(l1, l2)
    
    @staticmethod
    def calculatePointsDistance(a, b):
        return np.linalg.norm(a - b)

    @staticmethod
    def calculatePointLineDistance(p, (vA, vB)):
        vC = vB - vA

        mag = np.linalg.norm(vC)
        numerator = abs((vC[1] * p[0]) - (vC[0] * p[1]) + ((vB[0] * vA[1]) - (vB[1] * vA[0])))
        
        return numerator / mag
    
    @staticmethod
    def calculateUnitVector(v):
        return v / np.linalg.norm(v)

    @staticmethod
    def calculateResultantVector(img):
        m = cv2.moments(img)
            
        xbar = int(m['m10'] / m['m00'])
        ybar = int(m['m01'] / m['m00'])
    
        h, w= img.shape[:2]
        
        xs, ys = 0, 0
        
        y = 0
        while y < h:
            x = 0
            while x < w:
                if 0 != img[y][x] and x != xbar and y != ybar:
                    v = np.array([float(x - xbar), float(y - ybar)])
                    xsub, ysub = BasicFunctions.calculateUnitVector(v)
                    
                    xs += xsub
                    ys += ysub
                x += 1
            y += 1

        return np.array([xs, ys])
    
    @staticmethod
    def loadImage(img):
        return cv2.bitwise_not(cv2.imread(img, cv2.IMREAD_GRAYSCALE))
    
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
    
    @staticmethod
    def removeFourConnectedness(matrix):
        h, w = matrix.shape[:2]
        
        y = 0
        cleared_matrix = matrix.copy()
        while y < h:
            prev_y = y - 1
            x = 0
            while x < w:
                if 255 == matrix[y][x]:
                    next_x = x + 1
                    prev_x = x - 1
                    if prev_y >=0 and prev_x >= 0 and 255 == matrix[prev_y][x] and 255 == matrix[y][prev_x]:
                        cleared_matrix[y][x] = 0
                    elif prev_y >= 0 and next_x < w and 255 == matrix[prev_y][x] and 255 == matrix[y][next_x]:
                        cleared_matrix[y][x] = 0
                x += 1
            y += 1
        
        return cleared_matrix