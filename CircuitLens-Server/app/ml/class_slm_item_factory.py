from abc import ABCMeta, abstractmethod

class SLMItemFactory:
    __metaclass__ = ABCMeta
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    @abstractmethod
    def create(self, itemName, itemArgs):
        pass