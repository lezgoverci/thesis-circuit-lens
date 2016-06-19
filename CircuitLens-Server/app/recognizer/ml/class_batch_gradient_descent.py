import class_minimizer as m

class BatchGradientDescent(m.Minimizer):

    def __init__(self, args=None):
        self.__mathematicalModel = None
        self.__alpha = 1
        self.__iterations = 1

        self.init(args)

    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def init(self, args):
        if args is None:
            return self
        
        if 'mathematical_model' in args:
            self.setMathematicalModel(args['mathematical_model'])
        
        if 'learning_rate' in args:
            self.setLearningRate(args['learning_rate'])
        
        if 'iterations' in args:
            self.setIterations(args['iterations'])
        
        return self
    
    def setMathematicalModel(self, mathematicalModel):
        self.__mathematicalModel = mathematicalModel
        return self

    def setLearningRate(self, alpha):
        self.__alpha = alpha
        return self
    
    def setIterations(self, iterations):
        self.__iterations = iterations
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

        counter = 1
        while True:
            oldThetas = self.__mathematicalModel.getThetas()
            c = self.__mathematicalModel.getCostDerivative()

            # print "cost derivative: " + str(c)
            # print "subtractor: " + str(self.__alpha * c)

            newThetas = oldThetas - (self.__alpha * c)

            print "new thetas: " + str(newThetas)
            # newThetas = oldThetas - (self.__alpha * self.__mathematicalModel.getCostDerivative())
            self.__mathematicalModel.setThetas(newThetas)

            counter += 1

            if counter >= self.__iterations or 0 == sum(oldThetas - newThetas):
                break
    
    def dump(self):
        return {
            'learning_rate': self.__alpha,
            'iterations': self.__iterations
        }