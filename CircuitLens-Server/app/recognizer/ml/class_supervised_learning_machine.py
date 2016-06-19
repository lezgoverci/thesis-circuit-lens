import class_slm_item_factory_creator as sifc
import json

class SupervisedLearningMachine:
    def __init__(self, args=None):
        self.__items = {}
        self.__requiredArgs = ['type', 'mathematical_model', 'minimizer']
        self.__allIn = False

        self.init(args)
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def init(self, args={
                        'type': ('regression', None),
                        'mathematical_model': ('linear', None),
                        'minimizer': ('batch_gradient_descent', None)
                    }):
        self.__argsMet(args)

        for itemFactory, (itemName, itemArgs) in args.iteritems():
            itemFactoryObj = sifc.SLMItemFactoryCreator.create(itemFactory)
            self.__items[itemFactory] = itemFactoryObj.create(itemName, itemArgs)
        
        self.setup()

        return self

    def setMathematicalModel(self, mathematicalModel, args):
        itemFactoryObj = sifc.SLMItemFactoryCreator.create('mathematical_model')
        self.__items['mathematical_model'] = itemFactoryObj.create(mathematicalModel, args)

        self.__allIn = False
        return self

    def setMinimizer(self, minimizer, args):
        itemFactoryObj = sifc.SLMItemFactoryCreator.create('minimizer')
        self.__items['minimizer'] = itemFactoryObj.create(minimizer, args)

        self.__allIn = False
        return self
    
    def setType(self, machineType, args):
        itemFactoryObj = sifc.SLMItemFactoryCreator.create('type')
        self.__items['type'] = itemFactoryObj.create(machineType, args)

        self.__allIn = False
        return self
    
    def setThetas(self, thetas):
        if self.__items.get('mathematical_model', None) is not None:
            self.__items['mathematical_model'].setThetas(thetas)

            self.__allIn = False
            return self
        
        raise NameError('mathematical_model')
    
    def setLearningRate(self, alpha):
        if self.__items.get('minimizer', None) is not None:
            self.__items['minimizer'].setLearningRate(alpha)

            self.__allIn = False
            return self
        
        raise NameError('minimizer')

    def setIterations(self, iterations):
        if self.__items.get('minimizer', None) is not None:
            self.__items['minimizer'].setIterations(iterations)

            self.__allIn = False
            return self
        
        raise NameError('minimizer')
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------

    def getThetas(self):
        if self.__items['mathematical_model'] is not None:
            return self.__items['mathematical_model'].getThetas()
        
        raise NameError('mathematicalModel')

    #-----------------------------------------
    # Other Fucntions
    #-----------------------------------------

    def setup(self):
        if not self.__completeRequiredArgs():
            raise ValueError('Missing required argument(s).\nRequired arguments: %s' % (','.join(self.__requiredArgs)))
        
        self.__items['minimizer'].setMathematicalModel(self.__items['mathematical_model'])
        self.__items['type'].setMinimizer(self.__items['minimizer'])

        self.__allIn = True

    def read(self, filename):
        try:
            data = json.load(open(filename))
            self.init(data)
        except IOError:
            pass
    
    def write(self, filename):
        data = {
            'mathematicalModel': self.__items['mathematical_model'].dump(),
            'minimizer': self.__items['minimizer'].dump(),
            'type': self.__items['type'].dump()
        }

        json.dump(data, open(filename, 'w'))

    def train(self, features, responses):
        if not self.__allIn:
            raise Exception('Call setup() function first if you did not set the items via init() function.')

        self.__items['type'].train(features, responses)
        return self
    
    def predict(self, feature):
        if not self.__allIn:
            raise Exception('Call setup() function first if you did not set the items via init() function.')
        
        return self.__items['type'].predict(feature)
    
    def __argsMet(self, args):
        if args is None:
            raise ValueError('Arguments must not be None.')
        
        assert(dict == type(args))
        
        if any(args.get(requiredArg, None) is None for requiredArg in self.__requiredArgs):
            raise ValueError('Missing required argument(s).\nRequired arguments: %s' % (','.join(self.__requiredArgs)))
    
    def __completeRequiredArgs(self):
        return not any(self.__items.get(requiredArg, None) is None for requiredArg in self.__requiredArgs)