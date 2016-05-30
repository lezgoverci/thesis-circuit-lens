from __future__ import division
import np
import class_features_list_distance_solver as flds

class FURFeatureDifferenceSolver(flds.FeaturesListDistanceSolver):
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    def solve(self, f1, f2):
        r = f1 - f2
        ave_vector = (f1 + f2) / 2

        return (np.linalg.norm(r) * 100) / np.linalg.norm(ave_vector)