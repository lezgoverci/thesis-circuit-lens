import class_slm_item_factory as sif
import class_batch_gradient_descent as bgd
import class_normal_equation as ne

class MinimizerFactory(sif.SLMItemFactory):
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    def create(self, minimizer, args=None):
        if 'batch_gradient_descent' == minimizer:
            return bgd.BatchGradientDescent(args)
        elif 'normal_equation' == minimizer:
            return ne.NormalEquation(args)
        else:
            raise ValueError('Invalid minimizer.')