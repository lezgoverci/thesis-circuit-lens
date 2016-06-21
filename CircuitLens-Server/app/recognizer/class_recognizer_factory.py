import feature_based_recognizer.class_features_using_recognizer as fu
import class_null_recognizer as nr
import feature_based_recognizer.class_free_features_using_recognizer as ffur
import feature_based_recognizer.class_ml_quadratic_recognizer as mqr

class RecognizerFactory:
    @staticmethod
    def create(recognizer):
        if 'features_using' == recognizer:
            return fu.FeaturesUsingRecognizer()
        elif 'free_features_using' == recognizer:
            return ffur.FreeFeaturesUsingRecognizer()
        elif 'ml_quadratic' == recognizer:
            return mqr.MLQuadraticRecognizer()
        else:
            return nr.NullRecognizer()