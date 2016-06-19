import class_slm_item_factory as sif
import class_slm_regression_type as srt

class SLMTypeFactory(sif.SLMItemFactory):
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    def create(self, slm, args=None):
        if 'regression' == slm:
            return srt.SLMRegressionType(args)
        else:
            raise ValueError('Invalid slm type')