import class_central_angles_extractor as cae
import class_edges_keypoints_extractor as eke
import class_corners_keypoints_extractor as cke
import class_null_feature_data_extractor as nfde

class FeatureDataExtractorFactory:
    @staticmethod
    def create(featureDataExtractor):
        if 'central_angles' == featureDataExtractor:
            return cae.CentralAnglesExtractor()
        elif 'edges_keypoints' == featureDataExtractor:
            return eke.EdgesKeypointsExtractor()
        elif 'corners_keypoints' == featureDataExtractor:
            return cke.CornersKeypointsExtractor()
        else:
            return nfde.NullFeatureDataExtractor()