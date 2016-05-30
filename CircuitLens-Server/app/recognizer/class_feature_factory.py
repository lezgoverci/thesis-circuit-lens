import class_disperseness_from_centroid_feature as dfcf
import class_gearness_feature as gf
import class_null_feature as nf

class FeatureFactory:
    @staticmethod
    def create(feature):
        if 'disperseness_from_centroid' == feature:
            return dfcf.DispersenessFromCentroidFeature()
        elif 'gearness' == feature:
            return gf.GearnessFeature()
        else:
            return nf.NullFeature()