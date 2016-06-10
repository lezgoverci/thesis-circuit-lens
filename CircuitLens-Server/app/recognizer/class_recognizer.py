from abc import ABCMeta, abstractmethod

class Recognizer(object):

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
    
    @abstractmethod
    def getMatchPercentage(self, recalculate=False):
        pass
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    @abstractmethod
    def recognize(self):
        pass
    
    @abstractmethod
    def train(self, classesImagesMap):
        pass