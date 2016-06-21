import class_feature as f
import numpy as np
import common.class_basic_functions as bf
import recognizer.feature_based_recognizer.feature_data_extractors.class_feature_processable_data_extractor as fpde
import cv2
import math

class HullFeature(f.Feature):
    def __init__(self):
        self.__arguments = None
        self.__neededArguments = ['centroid', 'img', 'feature_data_extractors']
        self.__neededFeatureDataExtractors = ['edges_keypoints']
        self.__calculatedFeature = None
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def setArguments(self, args):
        self.__arguments = args
        
        return self

    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getCalculatedFeature(self, recalculate=False):
        if self.__calculatedFeature is None or recalculate:
            self.calculate(True)
        
        return self.__calculatedFeature
    
    def getNeededFeatureDataExtractors(self):
        return self.__neededFeatureDataExtractors
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def calculate(self, recalculate=False):
        if not self.argumentsMet():
            return None
        
        centroid = np.int0(self.__arguments['centroid'])
        img = self.__arguments['img']

        _,contours, [h] = cv2.findContours(img.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

        if len(contours) > 1:
            for cnt in contours:
                cntM = cv2.moments(cnt)

                if 0 == cntM["m00"]:
                    cv2.drawContours(img, [cnt], 0, 0, -1)
                    continue

                cX = int(cntM["m10"] / cntM["m00"])
                cY = int(cntM["m01"] / cntM["m00"])

                cv2.line(img, (centroid[0], centroid[1]), (cX, cY), 255, 1)
            
            _,contours, [h] = cv2.findContours(img.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        
        processableContour = contours[0]
        cntArea = cv2.contourArea(processableContour)

        hull = cv2.convexHull(processableContour)

        prevIndex = -1
        nextIndex = 1
        maxIndex = len(hull)

        cv2.fillConvexPoly(img, hull, 255)

        _,contours, [h] = cv2.findContours(img.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        
        bias  = 100
        cnt = contours[0]
        perimeter = cv2.arcLength(cnt, True)
        hullArea = cv2.contourArea(cnt)
        roundness = math.pow(perimeter, 2) * bias / (4 * np.pi * hullArea)

        rect = cv2.minAreaRect(cnt)
        box = cv2.boxPoints(rect)
        box = np.int0(box)

        l1 = bf.BasicFunctions.calculatePointsDistance(np.array(box[0]), np.array(box[1]))
        l2 = bf.BasicFunctions.calculatePointsDistance(np.array(box[1]), np.array(box[2]))

        solidity = cntArea * bias / hullArea
        squareNess = min(l1, l2) * bias / max(l1, l2)

        # self.__calculatedFeature = np.array([solidity, roundness, squareNess], dtype=np.float32)
        self.__calculatedFeature = np.array([solidity, roundness], dtype=np.float32)

        # print "-------------------------"

        # for [[x, y]] in hull:
        #     try:
        #         firstPoint = hull[prevIndex][0]
        #         secondPoint = hull[nextIndex][0]

        #         firstDst = bf.BasicFunctions.calculateAbsoluteDistance(centroid, firstPoint)
        #         secondDst = bf.BasicFunctions.calculateAbsoluteDistance(centroid, (x, y))

        #         angle = bf.BasicFunctions.calculateAngle(np.array([firstPoint[0] - centroid[0], firstPoint[1] - centroid[1]]), \
        #                                                 np.array([x - centroid[0], y - centroid[1]]), False)
                
        #         divider = max(firstDst, secondDst)
        #         tempSum = 0

                # if 0 != divider:
                #     self.__calculatedFeature[3] += angle * min(firstDst, secondDst) / divider

            #     cv2.circle(img,(x,y),3, 200,-1)
            # except Exception:
            #     continue
            
            # prevIndex += 1
            # nextIndex += 1

            # if nextIndex == maxIndex:
            #     nextIndex = 0
        
        # cv2.imshow("Hull", img)
        # cv2.waitKey(0)

        # self.__calculatedFeature[3] /= bias

        return self

    def argumentsMet(self):
        return self.__arguments is not None and \
               all(neededArg in self.__arguments for neededArg in self.__neededArguments) and \
               all(isinstance(self.__arguments['feature_data_extractors'][featureDataExtractor], fpde.FeatureProcessableDataExtractor) \
                    for featureDataExtractor in self.__neededFeatureDataExtractors)
