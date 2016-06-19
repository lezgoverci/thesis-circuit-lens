import class_mathematical_model_factory as mmf
import class_minimizer_factory as mf
import class_slm_type_factory as stf

class SLMItemFactoryCreator:
    @staticmethod
    def create(itemFactory):
        if 'mathematical_model' == itemFactory:
            return mmf.MathematicalModelFactory()
        elif 'minimizer' == itemFactory:
            return mf.MinimizerFactory()
        elif 'type' == itemFactory:
            return stf.SLMTypeFactory()
        else:
            raise ValueError('Invalid item factory.')