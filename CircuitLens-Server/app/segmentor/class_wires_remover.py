import cv2
import numpy as np
import math
from class_circuit_element_finder import CircuitElementFinder
from class_basic_functions import BasicFunctions
from class_matrix_looper import MatrixLooper
from class_pixel_processor import PixelProcessor
from class_four_connectedness_remover import FourConnectednessRemover
from class_propagator import Propagator
from class_curve_connector import CurveConnector
from class_line_connector import LineConnector
from class_circuit_part_connector import CircuitPartConnector

class WiresRemover(CircuitElementFinder):
    __kernelSide = 8
    
    def setKernelSide(self, kernelSide):
        self.__kernelSide = kernelSide
    
    def find(self, img, orig):
        # kernel_half_size = int(self.__kernelSide / 2)
        # kernel = np.ones((kernel_half_size, kernel_half_size), np.uint8)
        # fat = cv2.dilate(img, kernel)
        
        edges = cv2.Canny(img, 50, 150, apertureSize = 5)
        edges[edges > 0] = 255
        
        h, w = edges.shape[:2]

        cleared_matrix = self.__removeFourConnectedness(edges)
        
        # matrixLooper = MatrixLooper()
        # fourConnectednessRemover = FourConnectednessRemover()
        # fourConnectednessRemover.setMatrix(edges)
        # matrixLooper.setMatrixDimensions(edges.shape[:2]).setPixelProcessor(fourConnectednessRemover)
        # matrixLooper.loop()
        # cleared_matrix = fourConnectednessRemover.getProcessedMatrix()
        
        # cleared_matrix = self.__attemptConnection(cleared_matrix)
        
        cv2.imwrite("cannied.jpg", cleared_matrix)
        
        labeled = cleared_matrix.copy()
        angularMatrix = self.__createAngularMatrix(cleared_matrix, self.__kernelSide)
        
        # self.__logAngularMatrix(angularMatrix, edges.shape[:2], "D://Thesis//CircuitLens//angularmatrix.txt")

        labeled = np.float32(labeled)
        
        labeled[labeled == 0] = -0.1
        
        self.__markLinePixels(angularMatrix, labeled, -255)

        k = np.ones((3, 3), np.float32) * 0.11
        k[1][1] = 0.12
        
        h, w = labeled.shape[:2]
        
        y = 1
        while y < h - 1:
            x = 1
            while x < w - 1:
                cur_matrix = labeled[y-1: y+2, x-1:x+2]
                s = sum(sum(cur_matrix * k))
                
                labeled[y][x] = s
        
                x +=1
            y += 1        

        labeled[labeled >= 0] = 255
        labeled[labeled < 0] = 0
        
        cv2.imshow("fasfsad", labeled)
        
        labeled = np.uint8(labeled)
        
        # test = labeled.copy()
        
        # img[img > 0] = 255
        # t = self.__mergeSkinAndBones(img, labeled)
        # lines = labeled.copy()
        # possible_elements = self.__getPossibleElements(test, t)
        
        # for i in possible_elements:
        #     cv2.rectangle(orig, i[0], i[1], (0, 255, 0), 2)
        
        # connector = CurveConnector(t)
        # connector.connect()
        
        # test[t == 100] = 255
        
        # kernelHalfSize = self.__detectKernelHalfSize(img)
        
        # lineConnector = LineConnector(angularMatrix, labeled, t, kernelHalfSize)
        # labeled = lineConnector.connect()
        
        # lines = np.zeros(img.shape[:2], np.uint8)
        # lines[labeled > 100] = 255
        # lines[lines <= 100] = 0
        
        # l = lines.copy()
        
        kernelHalfSize = 2
        
        connect_kernel = np.ones((kernelHalfSize, kernelHalfSize),np.uint8)
        labeled = cv2.morphologyEx(labeled, cv2.MORPH_CLOSE, connect_kernel, iterations=2)
        
        
        # circuitPartConnector = CircuitPartConnector(labeled, img)
        # circuitPartConnector.connect(1)
        
        # connect_kernel = np.ones((circuitPartConnector.getMaxDst(), circuitPartConnector.getMaxDst()),np.uint8)
        # labeled = cv2.morphologyEx(labeled, cv2.MORPH_CLOSE, connect_kernel, iterations=1)

        cv2.imwrite("lines_removed.jpg", labeled)
        _,contours, [h] = cv2.findContours(labeled,cv2.RETR_CCOMP,cv2.CHAIN_APPROX_SIMPLE)
        
        final_contours = []
        
        c = 0
        for contour in contours:
            if h[c][3] < 0:
                final_contours.append(contour)
            c += 1
        
        if not contours or len(contours) == 0:
            return None
        
        return final_contours
        
        # Setup SimpleBlobDetector parameters.
        params = cv2.SimpleBlobDetector_Params()

        # Change thresholds
        params.minThreshold = 50
        params.maxThreshold = 100
        
        # Set up the detector with default parameters.
        detector = cv2.SimpleBlobDetector_create(params)
        
        # Detect blobs.
        return detector.detect(labeled)

    def __removeUnwantedPixels(self, base, final):
        h, w = final.shape[:2]
        
        new = np.zeros(final.shape[:2], np.uint8)
        
        y = 0
        while y < h:
            x = 0
            while x < w:
                if 255 == final[y][x] and 255 == base[y][x]:
                    new[y][x] = 255
                x += 1
            y += 1
        
        return new
        
    def __detectKernelHalfSize(self, matrix):
        h, w = matrix.shape[:2]
        kernelHalfSize = 1
        
        y = 0
        while y < h:
            x = 0
            while x < w:
                if 0 != matrix[y][x]:
                    while self.__isFilled(matrix, (x, y), kernelHalfSize):
                        kernelHalfSize += 1
                x += 1
            y += 1
        
        return kernelHalfSize
    
    def __isFilled(self, matrix, center, kernelHalfSize):
        fromPos, toPos = self.__getSubMatrixCoordinates(matrix.shape[:2], center, kernelHalfSize)
        
        h = toPos[1] - fromPos[1] + 1
        w = toPos[0] - fromPos[0] + 1
        
        fullSize = h * w
        
        realSum = self.__getSum(matrix, (fromPos, toPos))
        
        return fullSize == realSum
    
    def __getSubMatrixCoordinates(self, (h, w), (x, y), kernelHalfSize):
        x_low = x - kernelHalfSize if x - kernelHalfSize >= 0 else 0
        y_low = y - kernelHalfSize if y - kernelHalfSize >= 0 else 0
        
        x_high = x + kernelHalfSize if x + kernelHalfSize < w else w - 1
        y_high = y + kernelHalfSize if y + kernelHalfSize < h else h - 1
        
        return ((x_low, y_low), (x_high, y_high))
    
    def __getSum(self, matrix, (fromPos, toPos)):
        w, h = toPos
        
        sum_pixels = 0
        
        y = fromPos[1]
        while y <= h:
            x = fromPos[0]
            while x <= w:
                if 0 != matrix[y][x]:
                    sum_pixels += 1
                x += 1
            y += 1
        
        return sum_pixels
    
    def __mergeSkinAndBones(self, bones, skin):
        result = np.zeros(bones.shape[:2], np.uint8)
        
        h, w = result.shape[:2]
        
        y = 0
        while y < h:
            x = 0
            while x < w:
                if 0 != skin[y][x]:
                    result[y][x] = 150 if 1 != skin[y][x] else 1
                elif 0 != bones[y][x]:
                    result[y][x] = bones[y][x]
                
                x += 1
            y += 1
        
        return result
    
    def __compute(self, edges, flesh):
        h, w = edges.shape[:2]
        
        y = 1
        while y < h - 1:
            x = 1
            while x < w - 1:
                if 255 == flesh[y][x]:
                    neighbours = [
                                    edges[y-1][x-1], edges[y-1][x], edges[y-1][x+1], \
                                    edges[y][x+1], \
                                    edges[y+1][x+1], edges[y+1][x], edges[y+1][x-1], \
                                    edges[y][x-1]
                                 ]
                    
                    non_zeros = [i for i in neighbours if i != 0]

                    s = sum(neighbours)
                    n = len(non_zeros)
                    
                    if 0 != n:
                        edges[y][x] = int(s / n)
                x += 1
            y += 1
        
        
    def __getPossibleElements(self, matrix, merged, elemMark=255):
        h, w = matrix.shape[:2]
        
        possible_elements = []
        
        y = 0
        while y < h:
            x = 0
            while x < w:
                if elemMark == matrix[y][x]:
                    propagator = Propagator((x, y), matrix.shape[:2])

                    while propagator.propagatable():
                        propagator.checkBorders(merged)
                        propagator.propagate()
                        propagator.updateStartersAndStoppers()
                    
                    boundingRect = propagator.getBoundingRect()

                    possible_elements.append(boundingRect)
                    
                    cv2.rectangle(matrix, boundingRect[0], boundingRect[1], 0, -1)
                    cv2.rectangle(merged, boundingRect[0], boundingRect[1], 0, -1)

                x += 1
            y += 1
        
        return possible_elements
    
    def __logAngularMatrix(self, angularMatrix, dim, filename):
        h, w = dim
        
        outputFile = open(filename, 'w')
        divider = " "
        
        i = 0
        while i < w:
            divider += '--- '
            i += 1
        
        y = 0
        while y < h:
            x = 0
            while x < w:
                try:
                    outputFile.write("|")
                    angle, _ = angularMatrix[(x, y)]
                    outputFile.write("%3d" % (angle))
                except KeyError:
                    outputFile.write("   ")
                
                x += 1
            outputFile.write("\n")
            outputFile.write(divider)
            outputFile.write("\n")
            y += 1
        
        print "done writing"
        outputFile.close()
    
    def __removeFourConnectedness(self, matrix):
        h, w = matrix.shape[:2]
        
        y = 0
        cleared_matrix = matrix.copy()
        while y < h:
            prev_y = y - 1
            x = 0
            while x < w:
                if 255 == matrix[y][x]:
                    next_x = x + 1
                    prev_x = x - 1
                    if prev_y >=0 and prev_x >= 0 and 255 == matrix[prev_y][x] and 255 == matrix[y][prev_x]:
                        cleared_matrix[y][x] = 0
                    elif prev_y >= 0 and next_x < w and 255 == matrix[prev_y][x] and 255 == matrix[y][next_x]:
                        cleared_matrix[y][x] = 0
                x += 1
            y += 1
        
        return cleared_matrix
    
    def __createAngularMatrix(self, matrix, kernelSide):
        h, w = matrix.shape[:2]
        
        angularMatrix = {}
        negRefVector = (2 * kernelSide + 1, 0)
        criterion = lambda x: 255 == x
        
        y = 0
        while y < h:
            x = 0
            while x < w:
                if 255 == matrix[y][x]:
                    intercepts = self.__getIntercepts(matrix, (x, y), kernelSide, criterion)
                    num_intercepts = len(intercepts)

                    if 4 >= num_intercepts and num_intercepts > 0:
                        point = (intercepts[0], intercepts[-1])
                        
                        dirVector = (intercepts[0][0] - intercepts[-1][0], intercepts[0][1] - intercepts[-1][1])

                        angle = math.degrees(BasicFunctions.getAngleBetweenTwoVectors(negRefVector, dirVector))
                        
                        if angle > 90 and 0 != dirVector[0] and 0 != dirVector[1] and dirVector[1] / dirVector[0] >= 0:
                            angle = 180 - angle

                        angularMatrix[(x, y)] = (angle, point)
                x += 1
            y += 1
        
        return angularMatrix
    
    def __markLinePixels(self, angularMatrix, matrix, mark=1):
        for (x, y), (angle, intercepts) in angularMatrix.iteritems():
            try:
                iAngleOne = angularMatrix[intercepts[0]][0]
                iAngleTwo = angularMatrix[intercepts[1]][0]
                
                diff = abs(iAngleOne - iAngleTwo)
                
                max_deviation_angle = int(90 / self.__kernelSide)
                
                if max_deviation_angle >= diff:
                    matrix[y][x] = mark

            except KeyError:
                continue

    def __getIntercepts(self, matrix, center, kernelSide, criterion):
        h, w = matrix.shape[:2]
        
        half_size = int(kernelSide / 2)
        
        borders = BasicFunctions.getBorders(center, half_size, matrix.shape[:2])

        intercepts = []

        for from_pos, to_pos, prev_pixel_loc, step in borders:
            prev_pixel = matrix[prev_pixel_loc[1]][prev_pixel_loc[0]]
            while True:
                if (criterion(matrix[from_pos[1]][from_pos[0]]) and 0 == prev_pixel):
                        intercepts.append((from_pos[0], from_pos[1]))
                
                prev_pixel = matrix[from_pos[1]][from_pos[0]]
                
                if to_pos == from_pos:
                    break
                
                from_pos[0] += step[0]
                from_pos[1] += step[1]
        
        return intercepts