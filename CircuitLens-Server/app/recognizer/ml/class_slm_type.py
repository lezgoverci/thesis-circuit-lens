from abc import ABCMeta, abstractmethod

class SLMType(object):
    __metaclass__ = ABCMeta
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------

    @abstractmethod
    def init(self, args):
        pass

    @abstractmethod
    def setMinimizer(self, minimizer):
        pass
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    @abstractmethod
    def train(self, features, responses):
        pass
    
    @abstractmethod
    def predict(self, feature):
        pass