from abc import ABCMeta, abstractmethod

class NoiseRemover:
    __metaclass__ = ABCMeta
    
    @abstractmethod
    def filter(self, img):
        pass