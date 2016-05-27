import cv2
import numpy as np
import math
from class_circuit_element_finder import CircuitElementFinder

class RegionGrowingCircuitElementFinder(CircuitElementFinder):
    
    def find(self, img):
        edges = cv2.Canny(img,50,150,apertureSize = 3)
        
        kernelSide = 8
        
        h, w = edges.shape[:2]
        
        cv2.imwrite("cannied.jpg", edges)
        
        labeled = np.zeros(edges.shape[:2], np.uint8)
        
        y = 0
        while y < h:
            x = 0
            while x < w:
                if 255 == edges[y][x]:
                    if self.__isALine(edges, (x, y), kernelSide):
                        labeled[y][x] = 0
                    else:
                        labeled[y][x] = 255
                    
                    # cv2.rectangle(labeled, (x-2, y - 2), (x + 2, y + 2), 255, -1)
                x += 1
            y += 1
        
        cv2.imshow("labeled", labeled)
        cv2.imwrite("lines_removed.jpg", labeled)
        _,contours,_ = cv2.findContours(img,cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)
        
        if not contours or len(contours) == 0:
            return None
        
        return contours
    
    def __isALine(self, img, center, kernelSide):
        intercepts = self.__getIntercepts(img, center, kernelSide)
        
        if 0 == len(intercepts) or 4 < len(intercepts) or 0 != len(intercepts) % 2:
            return False
        
        points = (intercepts[0], intercepts[-1])
        
        r_angle = math.degrees(self.__calculateAngle(center, points))
        
        error = (180 - r_angle) / 1.8
        
        # print "error: %lf angle: %lf x: %d y: %d" % (error, r_angle, center[0], center[1])
        
        return error <= 40
    
    def __getIntercepts(self, img, center, kernelSide):
        h, w = img.shape[:2]
        
        half_size = int(kernelSide / 2)
        
        base_x = [center[0] - half_size, center[0] + half_size]
        base_y = [center[1] - half_size, center[1] + half_size]
        
        base_x[0] = base_x[0] if center[0] - half_size >= 0 else 0
        base_x[1] = base_x[1] if center[0] + half_size < w else w - 1
        
        base_y[0] = base_y[0] if center[1] - half_size >= 0 else 0
        base_y[1] = base_y[1] if center[1] + half_size < h else h - 1
        
        borders = [
            ([base_x[0], base_y[0]], [base_x[1], base_y[0]], (base_x[0], base_y[0] + 1), (1, 0)), #top
            ([base_x[1], base_y[0] + 1], [base_x[1], base_y[1] - 1], (base_x[1], base_y[0]), (0, 1)), #right
            ([base_x[1], base_y[1]], [base_x[0], base_y[1]], (base_x[1], base_y[1] - 1), (-1, 0)), #bottom
            ([base_x[0], base_y[1] - 1], [base_x[0], base_y[0] + 1], (base_x[0], base_y[1]), (0, -1)), #left
        ]
        
        intercepts = []
        
        for border in borders:
            from_pos, to_pos, prev_pixel_loc, step = border
            
            prev_pixel = img[prev_pixel_loc[1]][prev_pixel_loc[0]]
            while to_pos != from_pos:
                if (255 == img[from_pos[1]][from_pos[0]] and 0 == prev_pixel):
                        intercepts.append((from_pos[0], from_pos[1]))
                
                prev_pixel = img[from_pos[1]][from_pos[0]]
                from_pos[0] += step[0]
                from_pos[1] += step[1]
        
        return intercepts
    
    def __calculateAngle(self, center, intercepts):
        x, y = center
        p1, p2 = intercepts
        
        r1 = (p1[0] - x, p1[1] - y)
        r2 = (p2[0] - x, p2[1] - y)
        
        r1_mag = math.sqrt(math.pow(r1[0], 2) + math.pow(r1[1], 2))
        r2_mag = math.sqrt(math.pow(r2[0], 2) + math.pow(r2[1], 2))
        
        if 0 == r1_mag or 0 == r2_mag:
            return 0
        
        r1_dot_r2 = (r1[0] * r2[0]) + (r1[1] * r2[1])

        return math.acos(round(r1_dot_r2 / (r1_mag * r2_mag), 2))