import sys
import os

root_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

if root_dir not in sys.path:
    sys.path.append(root_dir)

import recognizer.class_recognizer_factory as rf
import cv2
import common.class_basic_functions as bf
import unittest
import numpy as np

class FeaturesTest(unittest.TestCase):
    def test_symmetrical_feature(self):
        recognizer = rf.RecognizerFactory.create('free_features_using')
        
        imagesDir = 'D:\\Thesis\\Recognizer\\training_set\\'

        queryImgName1 = 'symbol_capacitor_small'
        queryImg1 = bf.BasicFunctions.loadImage(imagesDir + queryImgName1 + '.png')
        
        m1 = cv2.moments(queryImg1)
        centroid1 = np.array([m1['m10'] / m1['m00'], m1['m01'] / m1['m00'], 0])
        
        queryImgName2 = 'symbol_capacitor'
        queryImg2 = bf.BasicFunctions.loadImage(imagesDir + queryImgName2 + '.png')
        
        m2 = cv2.moments(queryImg2)
        centroid2 = np.array([m2['m10'] / m2['m00'], m2['m01'] / m2['m00'], 0])

        recognizer.setFeatures([{
                                    'name': 'symmetrical',
                                    'arguments': {
                                        'centroid': centroid1,
                                        'img': queryImg1
                                    }
                                }])
        
        f1 = recognizer.setImage(queryImg1).recognize(True).getCalculatedFeature()
        
        recognizer.setFeatures([{
                                    'name': 'symmetrical',
                                    'arguments': {
                                        'centroid': centroid2,
                                        'img': queryImg2
                                    }
                                }])
        
        f2 = recognizer.setImage(queryImg2).recognize(True).getCalculatedFeature()
        
        print "Symmetry feature: %s: %s %s: %s" % (queryImgName1, str(np.linalg.norm(f1)), queryImgName2, str(np.linalg.norm(f2)))
        
        self.assertTrue(True)
        
    def test_corner_density_feature(self):
        recognizer = rf.RecognizerFactory.create('free_features_using')
        
        imagesDir = 'D:\\Thesis\\Recognizer\\training_set\\'

        queryImgName1 = 'symbol_capacitor'
        queryImg1 = bf.BasicFunctions.loadImage(imagesDir + queryImgName1 + '.png')
        
        m1 = cv2.moments(queryImg1)
        centroid1 = np.array([m1['m10'] / m1['m00'], m1['m01'] / m1['m00'], 0])
        
        queryImgName2 = 'symbol_resistor'
        queryImg2 = bf.BasicFunctions.loadImage(imagesDir + queryImgName2 + '.png')
        
        m2 = cv2.moments(queryImg2)
        centroid2 = np.array([m2['m10'] / m2['m00'], m2['m01'] / m2['m00'], 0])

        recognizer.setFeatures([{
                                    'name': 'corner_density',
                                    'arguments': {
                                        'centroid': centroid1,
                                        'img': queryImg1,
                                        'area': m1['m00']
                                    }
                                }])
        
        f1 = recognizer.setImage(queryImg1).recognize(True).getCalculatedFeature()
        
        recognizer.setFeatures([{
                                    'name': 'corner_density',
                                    'arguments': {
                                        'centroid': centroid2,
                                        'img': queryImg2,
                                        'area': m2['m00']
                                    }
                                }])
        
        f2 = recognizer.setImage(queryImg2).recognize(True).getCalculatedFeature()
        
        print "Corner density feature: %s: %s %s: %s" % (queryImgName1, str(np.linalg.norm(f1)), queryImgName2, str(np.linalg.norm(f2)))
        
        self.assertTrue(True)
    
    
    def test_num_contours_feature(self):
        recognizer = rf.RecognizerFactory.create('free_features_using')
        
        imagesDir = 'D:\\Thesis\\Recognizer\\training_set\\'

        queryImgName1 = 'symbol_capacitor'
        queryImg1 = bf.BasicFunctions.loadImage(imagesDir + queryImgName1 + '.png')
        
        m1 = cv2.moments(queryImg1)
        centroid1 = np.array([m1['m10'] / m1['m00'], m1['m01'] / m1['m00'], 0])
        
        queryImgName2 = 'symbol_resistor'
        queryImg2 = bf.BasicFunctions.loadImage(imagesDir + queryImgName2 + '.png')
        
        m2 = cv2.moments(queryImg2)
        centroid2 = np.array([m2['m10'] / m2['m00'], m2['m01'] / m2['m00'], 0])

        recognizer.setFeatures([{
                                    'name': 'num_contours',
                                    'arguments': {
                                        'img': queryImg1
                                    }
                                }])
        
        f1 = recognizer.setImage(queryImg1).recognize(True).getCalculatedFeature()
        
        recognizer.setFeatures([{
                                    'name': 'num_contours',
                                    'arguments': {
                                        'img': queryImg2
                                    }
                                }])
        
        f2 = recognizer.setImage(queryImg2).recognize(True).getCalculatedFeature()
        
        print "Num contours feature: %s: %s %s: %s" % (queryImgName1, str(np.linalg.norm(f1)), queryImgName2, str(np.linalg.norm(f2)))
        
        self.assertTrue(True)
    
    
    
    def test_gearness_feature(self):
        recognizer = rf.RecognizerFactory.create('free_features_using')
        
        imagesDir = 'D:\\Thesis\\Recognizer\\training_set\\'

        queryImgName1 = 'test_002'
        queryImg1 = bf.BasicFunctions.loadImage(imagesDir + queryImgName1 + '.png')
        
        m1 = cv2.moments(queryImg1)
        centroid1 = np.array([m1['m10'] / m1['m00'], m1['m01'] / m1['m00'], 0])
        
        queryImgName2 = 'test_001'
        queryImg2 = bf.BasicFunctions.loadImage(imagesDir + queryImgName2 + '.png')
        
        m2 = cv2.moments(queryImg2)
        centroid2 = np.array([m2['m10'] / m2['m00'], m2['m01'] / m2['m00'], 0])

        recognizer.setFeatures([{
                                    'name': 'gearness',
                                    'arguments': {
                                        'centroid': centroid1,
                                        'img': queryImg1,
                                        'area': m1['m00']
                                    }
                                }])
        
        f1 = recognizer.setImage(queryImg1).recognize(True).getCalculatedFeature()
        
        recognizer.setFeatures([{
                                    'name': 'gearness',
                                    'arguments': {
                                        'centroid': centroid2,
                                        'img': queryImg2,
                                        'area': m2['m00']
                                    }
                                }])
        
        f2 = recognizer.setImage(queryImg2).recognize(True).getCalculatedFeature()
        
        print "Gearness feature: %s: %s %s: %s" % (queryImgName1, str(np.linalg.norm(f1)), queryImgName2, str(np.linalg.norm(f2)))
        
        self.assertTrue(True)

def runtests():
    unittest.main()

runtests()