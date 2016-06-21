import class_slm_item_factory as sif
import class_linear_mathematical_model as lmm
import class_quadratic_mathematical_model as qmm

class MathematicalModelFactory(sif.SLMItemFactory):
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def create(self, mathematicalModel, args=None):
        if 'linear' == mathematicalModel:
            return lmm.LinearMathematicalModel(args)
        elif 'quadratic' == mathematicalModel:
            return qmm.QuadraticMathematicalModel(args)
        else:
            raise ValueError('Invalid mathematical model.')
