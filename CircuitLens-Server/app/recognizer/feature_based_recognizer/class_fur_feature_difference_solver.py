from __future__ import division
import numpy as np
import class_features_list_distance_solver as flds

class FURFeatureDifferenceSolver(flds.FeaturesListDistanceSolver):
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    def solve(self, f1, f2):
        r = f1 - f2
        ave_vector = (f1 + f2) / 2
        diff = r - ave_vector
        
        return ((np.linalg.norm(diff) * 100) / np.linalg.norm(ave_vector))