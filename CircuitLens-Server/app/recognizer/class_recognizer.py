from abc import ABCMeta, abstractmethod

class Recognizer:

    #-----------------------------------------
    # Setters
    #-----------------------------------------

    @abstractmethod
    def setImage(self, img):
        pass
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    @abstractmethod
    def getClass(self, recalculate):
        pass
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    @abstractmethod
    def recognize(self):
        pass