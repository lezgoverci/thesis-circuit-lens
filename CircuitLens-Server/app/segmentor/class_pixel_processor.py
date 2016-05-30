from abc import ABCMeta, abstractmethod

class PixelProcessor:
    __metaclass__ = ABCMeta
    
    @abstractmethod
    def process(self, center):
        pass
    
    @abstractmethod
    def getProcessedMatrix(self):
        pass