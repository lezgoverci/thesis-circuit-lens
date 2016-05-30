from class_basic_functions import BasicFunctions
import cv2
class CurveConnector:
    def __init__(self, matrix, lineMark=1, elemMark=150, connectionMark=100):
        self.__matrix = matrix
        self.__lineMark = lineMark
        self.__elemMark = elemMark
        self.__connectionMark = connectionMark
        self.__foundTheLink = False
        self.__h, self.__w = self.__matrix.shape[:2]
        self.__link = None
    
    def connect(self):
        h, w = self.__matrix.shape[:2]
        
        y = 1
        while y < h - 1:
            x = 1
            while x < w - 1:
                if self.__lineMark == self.__matrix[y][x] and self.__isACurveEnd((x, y)):
                    self.__foundTheLink = False
                    self.__propagate((x, y))
                    cv2.line(self.__matrix, (x, y), self.__link, self.__connectionMark, 1)
                x += 1
            y += 1
    
    def __propagate(self, (x, y)):
        half_size = 1
        
        while not self.__foundTheLink:
            borders = BasicFunctions.getBorders((x, y), half_size, self.__matrix.shape[:2])
            
            for from_pos, to_pos, _, step in borders:
                while True:
                    cur_pixel = self.__matrix[from_pos[1]][from_pos[0]]
                    
                    if self.__isACurveEnd((from_pos[0], from_pos[1])):
                        self.__foundTheLink = True
                        self.__link = (from_pos[0], from_pos[1])
                        return
                    
                    if to_pos == from_pos:
                        break
                    
                    from_pos[0] += step[0]
                    from_pos[1] += step[1]
                
            half_size += 1
    
    def __isACurveEnd(self, (x, y)):
        borders = BasicFunctions.getBorders((x, y), 1, self.__matrix.shape[:2])
        
        hasElemMark = False
        hasConnectionMark = False
        
        for from_pos, to_pos, _, step in borders:
            while True:
                cur_pixel = self.__matrix[from_pos[1]][from_pos[0]]
                
                if self.__elemMark == cur_pixel:
                    hasElemMark = True
                elif self.__connectionMark == cur_pixel:
                    hasConnectionMark = True
                
                if to_pos == from_pos:
                    break
                
                from_pos[0] += step[0]
                from_pos[1] += step[1]
        
        return hasElemMark and not hasConnectionMark