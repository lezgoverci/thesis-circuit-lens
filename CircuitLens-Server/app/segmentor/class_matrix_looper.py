from class_pixel_processor import PixelProcessor
from class_pixel_processor_delegator import PixelProcessorDelegator

class MatrixLooper():
    __pixelProcessor = None
    __matrixDimensions = None
    
    def setMatrixDimensions(self, matrixDimensions):
        self.__matrixDimensions = matrixDimensions
        return self
    
    def setPixelProcessor(self, pixelProcessor):
        self.__pixelProcessor = pixelProcessor
        return self
    
    def loop(self, checker=lambda x: 255 == x):
        if None == self.__pixelProcessor:
            return
        
        h, w = self.__matrixDimensions
        y = 0
        while y < h:
            x = 0
            while x < w:
                if checker(matrix[y][x]):
                    delegator = PixelProcessorDelegator((x, y), self.__pixelProcessor)
                    delegator.start()
                x += 1
            y += 1