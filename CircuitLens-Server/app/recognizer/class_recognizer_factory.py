import class_features_using_recognizer as fu
import class_null_recognizer as nr

class RecognizerFactory:
    @staticmethod
    def create(recognizer):
        if 'features_using' == recognizer:
            return fu.FeaturesUsingRecognizer()
        else:
            return nr.NullRecognizer()