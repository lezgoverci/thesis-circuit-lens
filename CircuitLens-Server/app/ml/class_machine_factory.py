import class_supervised_learning_machine as slm

# Soon
# import class_unsupervised_learning_machine as ulm

class MachineFactory:
    @staticmethod
    def create(machine, args=None):
        if 'sl_machine' == machine:
            return slm.SupervisedLearningMachine(args)
        else:
            raise ValueError