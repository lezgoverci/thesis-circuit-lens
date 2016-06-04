from __future__ import division
import numpy as np
import class_features_list_distance_solver as flds

class FURAbsoluteMagnitudeDifferenceSolver(flds.FeaturesListDistanceSolver):
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    def solve(self, f1, f2):
        f1_mag = np.linalg.norm(f1)
        f2_mag = np.linalg.norm(f2)
        
        ave_mag = f1_mag + f2_mag / 2
        
        return 100 - (abs(f1_mag - f2_mag) * 100) / ave_mag