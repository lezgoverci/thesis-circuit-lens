from abc import ABCMeta, abstractmethod

class Minimizer(object):
    __metaclass__ = ABCMeta

    #-----------------------------------------
    # Setters
    #-----------------------------------------

    @abstractmethod
    def init(self, args):
        pass

    @abstractmethod
    def setMathematicalModel(self, mathematicalModel):
        pass
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------

    @abstractmethod
    def getMathematicalModel(self):
        pass

    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    @abstractmethod
    def minimize(self):
        pass
    
    @abstractmethod
    def dump(self):
        pass