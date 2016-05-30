from class_pixel_processor import PixelProcessor

class FourConnectednessRemover(PixelProcessor):
    __matrix = None
    __cleared_matrix = None
    __w = 0
    
    def setMatrix(self, matrix):
        self.__matrix = matrix
        self.__cleared_matrix = matrix.copy()
        _, self.__w = matrix.shape[:2]
        return self
    
    def process(self, (x, y)):
        # print "processing: x: %d y: %d" % (x, y)
        if 255 != self.__matrix[y][x]:
            return
        
        prev_y = y - 1
        next_x = x + 1
        prev_x = x - 1
        
        if (prev_y >=0 and prev_x >= 0 and 255 == self.__matrix[prev_y][x] and 255 == self.__matrix[y][prev_x]) or \
            (prev_y >= 0 and next_x < self.__w and 255 == self.__matrix[prev_y][x] and 255 == self.__matrix[y][next_x]):
                self.__cleared_matrix[y][x] = 0

    def getProcessedMatrix(self):
        return self.__cleared_matrix