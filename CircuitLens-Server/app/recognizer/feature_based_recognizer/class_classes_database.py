from abc import ABCMeta, abstractmethod

class ClassesDatabase():
    
    def __init__(self):
        self._classesValuesMap = {}
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    @abstractmethod
    def match(self, calculatedValue):
        pass
    
    #-----------------------------------------
    # Other Functions
    #----------------------------------------- 
    
    def clear(self):
        self._classesValuesMap = {}
    
    @abstractmethod
    def train(self, classesImagesMap):
        pass