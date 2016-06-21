import class_slm_type as st

class SLMClassificationType(st.SLMType):
    def __init__(self, args=None):
        self.__minimizer = None
        self.__limitPercentage = 0.5
        self.__max = 1
        self.init(args)

    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def init(self, args):
        if args is None:
            return self
        
        if 'minimizer' in args:
            self.setMinimizer(args['minimizer'])
        
        if 'limit_percentage' in args:
            self.setLimit(args['limit'])
        
        return self

    def setMinimizer(self, minimizer):
        self.__minimizer = minimizer
        return self
    
    def setLimitPercentage(self, limitPercentage):
        self.__limitPercentage = limitPercentage
        return self

    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def train(self, features, responses):
        responses[responses < responses.max()] = 0
        self.__max = responses.max()
        self.__minimizer.getMathematicalModel().setFeatures(features).setResponses(responses)
        self.__minimizer.minimize()

        return self
    
    def predict(self, feature):
        return self.__max if self.__minimizer.getMathematicalModel().getHypothesis(feature) >= self.__limitPercentage * self.__max else 0

    def dump(self):
        return {
            'limit_percentage': self.__limitPercentage
        }