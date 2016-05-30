from abc import ABCMeta, abstractmethod

class ClassesDatabase():
    
    def __init__(self):
        self._classes_values_map = {}
    
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
        self._classes_values_map = {}
    
    @abstractmethod
    def train(self, classes_images_map):
        pass