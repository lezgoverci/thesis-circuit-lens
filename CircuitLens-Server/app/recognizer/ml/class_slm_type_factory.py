import class_slm_item_factory as sif
import class_slm_regression_type as srt
import class_slm_classification_type as sct
import class_slm_multivariate_classification_type as mct

class SLMTypeFactory(sif.SLMItemFactory):
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    def create(self, slm, args=None):
        if 'regression' == slm:
            return srt.SLMRegressionType(args)
        elif 'classification' == slm:
            return sct.SLMClassificationType(args)
        elif 'multivariate_classification' == slm:
            return mct.SLMMultivariateClassificationType(args)
        else:
            raise ValueError('Invalid slm type')