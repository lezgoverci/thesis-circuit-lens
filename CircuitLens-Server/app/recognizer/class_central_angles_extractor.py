import class_feature_processable_data_extractor as fpde
import common.class_basic_functions as bf

class CentralAnglesExtractor(fpde.FeatureProcessableDataExtractor):

    def __init__(self):
        self.__arguments = None
        self.__needed_arguments = ['corners', 'centroid']
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def setArguments(self, args):
        self.__arguments = args
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def extract(self):
        if not self.__arguments or not self.argumentsMet():
            return None
        
        corners = self.__arguments['corners']
        centroid = self.__arguments['centroid']
        
        unsorted_central_angles = [corner - centroid for corner in corners]
    
        body_centroid = np.array([1.0, 0.0, 0])
        
        angle_vector_map = {bf.BasicFunctions.calculateAngle(body_centroid, v): v for v in unsorted_central_angles}
        central_angles = sorted(angle_vector_map)
        central_angles.append(central_angles[0])
        
        return central_angles, angle_vector_map
    
    def argumentsMet(self):
        for needed_arg in self.__needed_arguments:
            if None == self.__arguments.get(needed_arg, None):
                return False
        return True