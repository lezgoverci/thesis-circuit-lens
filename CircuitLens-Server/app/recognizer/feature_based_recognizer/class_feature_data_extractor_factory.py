import class_central_angles_extractor as cae
import class_keypoints_extractor as ke
import class_null_feature_data_extractor as nfde

class FeatureDataExtractorFactory:
    @staticmethod
    def create(featureDataExtractor):
        if 'central_angles' == featureDataExtractor:
            return cae.CentralAnglesExtractor()
        elif 'keypoints' == featureDataExtractor:
            return ke.KeypointsExtractor()
        else:
            return nfde.NullFeatureDataExtractor()