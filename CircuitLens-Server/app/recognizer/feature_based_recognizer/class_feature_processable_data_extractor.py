from abc import ABCMeta, abstractmethod

class FeatureProcessableDataExtractor(object):

    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    @abstractmethod
    def setArguments(self, args):
        pass
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    @abstractmethod
    def getExtractedData(self, reextract=False):
        pass
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    @abstractmethod
    def extract(self):
        pass
    
    @abstractmethod
    def argumentsMet(self):
        pass