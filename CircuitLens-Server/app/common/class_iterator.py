from abc import ABCMeta, abstractmethod

class Iterator():
    __metaclass__ = ABCMeta
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    @abstractmethod
    def setIterable(self, iterable):
        pass
    
    #-----------------------------------------
    # Getters
    #----------------------------------------- 
    
    @abstractmethod
    def getData(self):
        pass
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    @abstractmethod
    def next(self):
        pass
    
    @abstractmethod
    def prev(self):
        pass
    
    @abstractmethod
    def reset(self):
        pass
    
    @abstractmethod
    def end(self):
        pass
    
    @abstractmethod
    def valid(self):
        pass