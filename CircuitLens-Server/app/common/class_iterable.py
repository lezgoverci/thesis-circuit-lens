from abc import ABCMeta, abstractmethod

class Iterable:
    __metaclass__ = ABCMeta
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
     
    @abstractmethod
    def getData(self, n):
        pass
    
    @abstractmethod
    def getIterator(self):
        pass
    
    @abstractmethod
    def size(self):
        pass
    
    #-----------------------------------------
    # Other Functions
    #----------------------------------------- 
    
    @abstractmethod
    def accessible(self, n):
        pass