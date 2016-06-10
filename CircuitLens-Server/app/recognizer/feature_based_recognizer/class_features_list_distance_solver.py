from abc import ABCMeta, abstractmethod

class FeaturesListDistanceSolver(object):

    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    @abstractmethod
    def solve(self, f1, f2):
        pass