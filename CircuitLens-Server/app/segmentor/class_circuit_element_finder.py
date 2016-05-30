from abc import ABCMeta, abstractmethod

class CircuitElementFinder:
    __metaclass__ = ABCMeta
    
    @abstractmethod
    def find(self, img):
        pass