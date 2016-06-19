from abc import ABCMeta, abstractmethod

class MathematicalModel(object):
    __metaclass__ = ABCMeta

    #-----------------------------------------
    # Setters
    #-----------------------------------------

    @abstractmethod
    def init(self, args):
        pass

    @abstractmethod
    def setThetas(self, thetas):
        pass
    
    @abstractmethod
    def setFeatures(self, features):
        pass
    
    @abstractmethod
    def setResponses(self, responses):
        pass

    #-----------------------------------------
    # Getters
    #-----------------------------------------

    @abstractmethod
    def getThetas(self):
        pass
    
    @abstractmethod
    def getFeatures(self):
        pass

    @abstractmethod
    def getResponses(self):
        pass
    
    @abstractmethod
    def getHypothesis(self, sample):
        pass
    
    @abstractmethod
    def getCostDerivative(self):
        pass

    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    @abstractmethod
    def dump(self):
        pass