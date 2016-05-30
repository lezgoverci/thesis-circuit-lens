from abc import ABCMeta, abstractmethod
import common.class_iterable as i
import common.class_list_iterable_iterator as lii

class ClassesDatabase(i.Iterable):
    
    def __init__(self):
        self.__classes_values_map = {}
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------

    def getData(self, n):
        if not self.accessible():
            raise LookupError
        
        return self.__classes_values_map[n]
    
    def getIterator(self):
        return lii.ListIterableIterator(self)
    
    def size(self):
        return len(self.__classes_values_map)
    
    #-----------------------------------------
    # Other Functions
    #----------------------------------------- 
    
    def accessible(self, n):
        return None == self.__classes_values_map.get(n, None)
    
    @abstractmethod
    def train(self, classes_images_map):
        pass