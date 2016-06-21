import class_mathematical_model as mm
import numpy as np
import itertools

class QuadraticMathematicalModel(mm.MathematicalModel):
    def __init__(self, args=None):
        self.__thetas = None
        self.__features = None
        self.__responses = None
        self.init(args)

    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def init(self, args):
        if args is None:
            return self
        
        if 'thetas' in args:
            self.setThetas(args['thetas'])
        
        if 'features' in args:
            self.setFeatures(args['features'])
        
        if 'responses' in args:
            self.setResponses(args['responses'])
        
        return self

    def setThetas(self, thetas):
        self.__thetas = thetas
        return self

    def setFeatures(self, features):
        self.__features = []
        multiplier = lambda (x, y): x * y

        for trainingSet in features:
            self.__features.append(self.__toQuadraticFeatures(trainingSet))

        self.__features = np.array(self.__features, np.float32)
        return self
    
    def setResponses(self, responses):
        self.__responses = responses
        return self

    #-----------------------------------------
    # Getters
    #-----------------------------------------

    def getThetas(self):
        self.__setupThetas()
        return self.__thetas
    
    def getFeatures(self):
        return self.__features
    
    def getResponses(self):
        return self.__responses

    def getHypothesis(self, sample):
        self.__setupThetas()
        return np.dot(self.__thetas, self.__toQuadraticFeatures(sample))
    
    def getCostDerivative(self):
        self.__setupThetas()

        return np.dot(np.dot(self.__features, self.__thetas) - self.__responses, self.__features) / len(self.__responses)

    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def dump(self):
        return {
            'thetas': self.__thetas,
            'features': self.__features,
            'responses': self.__responses
        }
    
    def __setupThetas(self):
        if self.__thetas is not None:
            return

        self.__thetas = np.zeros(self.__features.shape[1], dtype=np.float32)
    
    def __toQuadraticFeatures(self, feature, multiplier = lambda (x, y): x * y):
        f = list(feature)
        base = feature[1:]
        f.extend(map(multiplier, list(itertools.combinations_with_replacement(base, 2))))
        return f
