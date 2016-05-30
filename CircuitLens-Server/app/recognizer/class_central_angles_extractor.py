import class_feature_processable_data_extractor as fpde
import common.class_basic_functions as bf

class CentralAnglesExtractor(fpde.FeatureProcessableDataExtractor):

    def __init__(self):
        self.__arguments = None
        self.__neededArguments = ['corners', 'centroid']
        self.__extractedData = None
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def setArguments(self, args):
        self.__arguments = args
        
        return self
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------

    def getExtractedData(self, reextract=False):
        if not self.__extractedData or reextract:
            self.extract()
        
        return self.__extractedData
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def extract(self):
        if not self.__arguments or not self.argumentsMet():
            return None
        
        corners = self.__arguments['corners']
        centroid = self.__arguments['centroid']
        
        unsortedCentralAngles = [corner - centroid for corner in corners]
    
        origin = np.array([1.0, 0.0, 0])
        
        angleVectorMap = {bf.BasicFunctions.calculateAngle(origin, v): v for v in unsortedCentralAngles}
        centralAngles = sorted(angleVectorMap)
        centralAngles.append(centralAngles[0])
        
        self.__extractedData = (centralAngles, angleVectorMap)
        
        return self
    
    def argumentsMet(self):
        return len(self.__arguments) > 0 and all(neededArg in self.__arguments for neededArg in self.__neededArguments)