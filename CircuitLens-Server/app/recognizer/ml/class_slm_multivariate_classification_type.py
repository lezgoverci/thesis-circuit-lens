import class_slm_type as st

class SLMMultivariateClassificationType(st.SLMType):
    def __init__(self, args=None):
        self.__minimizer = None
        self.__classes = {}
        self.init(args)

    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def init(self, args):
        if args is None:
            return self
        
        if 'minimizer' in args:
            self.setMinimizer(args['minimizer'])
        
        if 'classes' in args:
            self.setClasses(args['classes'])

        return self

    def setMinimizer(self, minimizer):
        self.__minimizer = minimizer
        return self
    
    def setClasses(self, classes):
        self.__classes = classes
        return self

    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def train(self, features, responses):
        mathematicalModel = self.__minimizer.getMathematicalModel()

        maxResponse = responses.max()
        for response in responses:
            if self.__classes.get(response, None) is None:
                currentResponses = responses.copy()
                currentResponses[currentResponses != response] = 0
                currentResponses[currentResponses > 0] = maxResponse

                mathematicalModel.setFeatures(features).setResponses(currentResponses)
                self.__minimizer.minimize()

                self.__classes[response] = mathematicalModel.getThetas()

        return self
    
    def predict(self, feature):
        maxHypothesis = -float('inf')
        mathematicalModel = self.__minimizer.getMathematicalModel()
        finalResponse = None

        for response, thetas in self.__classes.iteritems():
            hypothesis = mathematicalModel.setThetas(thetas).getHypothesis(feature)

            if hypothesis > maxHypothesis:
                maxHypothesis = hypothesis
                finalResponse = response
            
        return finalResponse

    def dump(self):
        return {
            'classes': self.__classes
        }