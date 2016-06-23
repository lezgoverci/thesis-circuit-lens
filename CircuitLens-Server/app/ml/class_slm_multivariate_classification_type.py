import class_slm_type as st
import common.class_basic_functions as bf

class SLMMultivariateClassificationType(st.SLMType):
    def __init__(self, args=None):
        self.__minimizer = None
        self.__classes = {}
        self.__fsAndRsSelector = lambda fs, rs, i: (fs, rs)
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
        
        if 'fs_and_rs_selector' in args:
            self.setFeaturesAndResponsesSelector(args['fs_and_rs_selector'])

        return self

    def setMinimizer(self, minimizer):
        self.__minimizer = minimizer
        return self
    
    def setClasses(self, classes):
        self.__classes = classes
        return self
    
    def setFeaturesAndResponsesSelector(self, f):
        self.__fsAndRsSelector = f
        return self

    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def train(self, features, responses):
        mathematicalModel = self.__minimizer.getMathematicalModel()

        maxResponse = responses.max()
        index = 0
        for response in responses:
            if self.__classes.get(response, None) is None:
                currentResponses = responses.copy()
                currentResponses[currentResponses != response] = 0
                currentResponses[currentResponses > 0] = maxResponse

                selectedFeatures, selectedResponses = self.__fsAndRsSelector(features, currentResponses, index)
                mathematicalModel.setFeatures(selectedFeatures).setResponses(selectedResponses)
                self.__minimizer.minimize()

                self.__classes[response] = mathematicalModel.getThetas()

            index += 1

            #TEMPORARY
            theta = self.__classes[response]
            print "r: %d => y = %+lf%+lfx%+lfx^2" % (response, theta[0], theta[1], theta[2])

        return self
    
    def predict(self, feature):
        maxHypothesis = -float('inf')
        mathematicalModel = self.__minimizer.getMathematicalModel()

        return max(self.__classes, key = lambda x: mathematicalModel.setThetas(self.__classes[x]).getHypothesis(feature))
    
    def dump(self):
        return {
            'classes': self.__classes
        }