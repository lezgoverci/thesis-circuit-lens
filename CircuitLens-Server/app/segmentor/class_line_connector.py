from class_basic_functions import BasicFunctions
import cv2
import math

class LineConnector:
    def __init__(self, angularMatrix, edgesMatrix, completeMatrix, maxDst, lineMark=50, elemMark=255):
        self.__angularMatrix = angularMatrix
        self.__edgesMatrix = edgesMatrix
        self.__completeMatrix = completeMatrix
        self.__h, self.__w = self.__edgesMatrix.shape[:2]
        self.__result = edgesMatrix.copy()
        self.__lineMark = lineMark
        self.__elemMark = elemMark
        self.__maxDst = maxDst
    
    def connect(self):
        y = 0
        while y < self.__h:
            x = 0
            while x < self.__w:
                try:
                    if self.__elemMark == self.__edgesMatrix[y][x]:
                        directionAngle = self.__getDirectionAngle((x, y))
                        # loc = self.__getDirectionCoordinates((x, y), directionAngle)
                        
                        # direction = self.__getApproximateDirection(directionAngle)
                        # print "direction: %s angle: %d" % (direction, directionAngle)
                        self.__propagate((x, y), directionAngle)
                except KeyError:
                    pass
                x += 1
            y += 1
        
        return self.__result
    
    def __getDirectionAngle(self, (x, y)):
        angle, _ = self.__angularMatrix[(x, y)]
        lineLength = 1
        
        firstAngle = 90 + angle
        
        f_x = x + math.cos(math.radians(firstAngle))
        f_y = y + math.sin(math.radians(firstAngle))
        
        if 0 != self.__completeMatrix[f_y][f_x]:
            return firstAngle
        
        return 90 - angle if 90 - angle >= 0 else 270 + angle
    
    def __propagate(self, (x, y), direction):
        from_x, from_y = x, y
        lineLength = 3
        
        offset_x = int(lineLength * math.cos(math.radians(direction)))
        offset_y = int(lineLength * math.sin(math.radians(direction)))
        
        dst = 0
        
        while True:
            x = x + offset_x
            y = y + offset_y
            
            if not self.__validCoordinates((x, y)) or not self.__isPartOfCircuit((x, y)) or dst >= self.__maxDst:
                break
            
            # if self.__edgesMatrix[y][x] == self.__elemMark:
            #     cv2.line(self.__result, (from_x, from_y), (x, y), self.__elemMark, 1)
            #     break
            dst += 1
        
        cv2.line(self.__result, (from_x, from_y), (x, y), self.__elemMark, 1)

    def __validCoordinates(self, (x, y)):
        return x >= 0 and y >= 0 and x < self.__w and y < self.__h
        
    def __getDirectionCoordinates(self, (x, y), direction):
        if direction == 'E':
            return (x + 1, y)
        elif direction == 'NE':
            return (x + 1, y - 1)
        elif direction == 'N':
            return (x, y - 1)
        elif direction == 'NW':
            return (x - 1, y - 1)
        elif direction == 'W':
            return (x - 1, y)
        elif direction == 'SW':
            return (x - 1, y + 1)
        elif direction == 'S':
            return (x, y + 1)
        else:
            return (x + 1, y + 1)
    
    def __getApproximateDirection(self, angle):
        angle = angle if angle >= 0 else 360 + angle
        
        if angle < 22.5:
            return 'E'
        elif angle < 67.5:
            return 'NE'
        elif angle < 112.5:
            return 'N'
        elif angle < 157.5:
            return 'NW'
        elif angle < 202.5:
            return 'W'
        elif angle < 247.5:
            return 'SW'
        elif angle < 292.5:
            return 'S'
        else:
            return 'SE'
        
    def __isACurveEnd(self, (x, y)):
        borders = BasicFunctions.getBorders((x, y), 1, self.__edgesMatrix.shape[:2])
        
        hasElemMark = False
        hasLineMark = False
        
        for from_pos, to_pos, _, step in borders:
            while True:
                if self.__validCoordinates((from_pos[0], from_pos[1])):
                    cur_pixel = self.__edgesMatrix[from_pos[1]][from_pos[0]]
                    
                    if self.__elemMark == cur_pixel:
                        hasElemMark = True
                    elif self.__lineMark == cur_pixel:
                        hasLineMark = True
                    
                if to_pos == from_pos:
                    break
                
                from_pos[0] += step[0]
                from_pos[1] += step[1]
        
        return hasElemMark and hasLineMark
    
    def __isPartOfCircuit(self, (x, y)):
        return 0 != self.__completeMatrix[y][x]