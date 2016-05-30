import class_disperseness_from_centroid_feature as dfcf
import class_gearness_feature as gf
import class_null_feature as nf
import class_corner_distance_feature as cdf

class FeatureFactory:
    @staticmethod
    def create(feature):
        if 'disperseness_from_centroid' == feature:
            return dfcf.DispersenessFromCentroidFeature()
        elif 'gearness' == feature:
            return gf.GearnessFeature()
        elif 'corner_distance' == feature:
            return cdf.CornerDistanceFeature()
        else:
            return nf.NullFeature()