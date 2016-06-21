import sys
import os

root_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

if root_dir not in sys.path:
    sys.path.append(root_dir)

import unittest

import recognizer.ml.class_machine_factory as mf
import numpy as np

class MachineLearningTest(unittest.TestCase):
    def setUp(self):
        # onedFeatures = [245, 312, 279, 308, 199, 219, 405, 324, 319, 255]
        # self.__features = np.array([[1, x] for x in onedFeatures], dtype=np.float32)
        # self.__responses = np.array([1400, 1600, 1700, 1875, 1100, 1550, 2350, 2450, 1425, 1700], dtype=np.float32)
        
        onedFeatures = [5, 4, 3]
        self.__features = np.array([[1, x] for x in onedFeatures], dtype=np.float32)
        self.__responses = np.array([5, 0, 0], dtype=np.float32)

        # onedFeatures = [95, 85, 80, 70, 60]
        # self.__features = np.array([[1, x] for x in onedFeatures], dtype=np.float32)
        # self.__responses = np.array([85, 95, 70, 65, 70], dtype=np.float32)
    
    # def test_linear_regression_using_batch_gradient_descent(self):
    #     args = {
    #         'type': ('regression', None),
    #         'mathematical_model': ('linear', {
    #             'thetas': np.array([30, 40], dtype=np.float32)
    #         }),
    #         'minimizer': ('batch_gradient_descent', {
    #             'learning_rate': 0.01,
    #             'iterations': 100000
    #         })
    #     }

    #     machine = mf.MachineFactory.create('sl_machine', args)
    #     machine.train(self.__features, self.__responses)

    #     print machine.getThetas()

    #     assert(True)

    # def test_linear_regression_using_normal_equation(self):
    #     args = {
    #         'type': ('regression', None),
    #         'mathematical_model': ('linear', None),
    #         'minimizer': ('normal_equation', None)
    #     }

    #     machine = mf.MachineFactory.create('sl_machine', args)
    #     machine.train(self.__features, self.__responses)

    #     print machine.getThetas()

    #     assert(True)
    
    def test_linear_classification_using_normal_equation(self):
        args = {
            'type': ('classification', None),
            'mathematical_model': ('linear', None),
            'minimizer': ('normal_equation', None)
        }

        machine = mf.MachineFactory.create('sl_machine', args)
        machine.train(self.__features, self.__responses)

        self.assertTrue(5 == machine.predict(np.array([1, 6], dtype=np.float32)))
    
    def test_linear_multivariate_classification_using_normal_equation(self):
        self.__responses = np.array([5, 4, 3], dtype=np.float32)

        args = {
            'type': ('multivariate_classification', None),
            'mathematical_model': ('linear', None),
            'minimizer': ('normal_equation', None)
        }

        machine = mf.MachineFactory.create('sl_machine', args)
        machine.train(self.__features, self.__responses)

        self.assertTrue(5 == machine.predict(np.array([1, 4.8], dtype=np.float32)))

def runtests():
    unittest.main()

runtests()