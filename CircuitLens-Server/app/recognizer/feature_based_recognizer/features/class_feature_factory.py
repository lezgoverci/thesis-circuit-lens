import class_disperseness_from_centroid_feature as dfcf
import class_gearness_feature as gf
import class_null_feature as nf
import class_corner_distance_feature as cdf
import class_corner_density_feature as cordf
import class_hu_moments_feature as hm
import class_symmetrical_feature as sf
import class_eccentricity_feature as ef
import class_roundness_feature as rf
import class_semi_minor_major_axes_feature as smmaf
import class_num_contours_feature as ncf
import class_black_white_feature as bwf

class FeatureFactory:
    @staticmethod
    def create(feature):
        if 'disperseness_from_centroid' == feature:
            return dfcf.DispersenessFromCentroidFeature()
        elif 'gearness' == feature:
            return gf.GearnessFeature()
        elif 'corner_distance' == feature:
            return cdf.CornerDistanceFeature()
        elif 'corner_density' == feature:
            return cordf.CornerDensityFeature()
        elif 'hu_moments' == feature:
            return hm.HuMomentsFeature()
        elif 'symmetrical' == feature:
            return sf.SymmetricalFeature()
        elif 'eccentricity' == feature:
            return ef.EccentricityFeature()
        elif 'roundness' == feature:
            return rf.RoundnessFeature()
        elif 'minor_major_axes' == feature:
            return smmaf.SemiMinorMajorAxesFeature()
        elif 'num_contours' == feature:
            return ncf.NumContoursFeature()
        elif 'black_white' == feature:
            return bwf.BlackWhiteFeature()
        else:
            return nf.NullFeature()