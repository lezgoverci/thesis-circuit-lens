import class_minimizer as m
import numpy as np

class NormalEquation(m.Minimizer):

    def __init__(self, args=None):
        self.__mathematicalModel = None

        self.init(args)

    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def init(self, args):
        if args is None:
            return self
        
        if 'mathematical_model' in args:
            self.setMathematicalModel(args['mathematical_model'])

        return self
    
    def setMathematicalModel(self, mathematicalModel):
        self.__mathematicalModel = mathematicalModel
        return self
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------

    def getMathematicalModel(self):
        return self.__mathematicalModel

    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def minimize(self):
        if self.__mathematicalModel is None:
            raise Exception('No mathematical model')
        
        features = self.__mathematicalModel.getFeatures()
        transposedFeatures = np.transpose(features)
        responses = self.__mathematicalModel.getResponses()

        thetas = np.dot(np.dot(np.linalg.inv(np.dot(transposedFeatures, features)), transposedFeatures), responses)
        self.__mathematicalModel.setThetas(thetas)
    
    def dump(self):
        return {}