import class_slm_type as st

class SLMRegressionType(st.SLMType):
    def __init__(self, args=None):
        self.__minimizer = None

        self.init(args)

    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def init(self, args):
        if args is None:
            return self
        
        if 'minimizer' in args:
            self.setMinimizer(args['minimizer'])
        
        return self

    def setMinimizer(self, minimizer):
        self.__minimizer = minimizer
        return self
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def train(self, features, responses):
        self.__minimizer.getMathematicalModel().setFeatures(features).setResponses(responses)
        self.__minimizer.minimize()

        return self
    
    def predict(self, feature):
        return self.__minimizer.getMathematicalModel().getHypothesis(feature)

    def dump(self):
        return {}