from __future__ import division
import cv2
import numpy as np

from class_basic_functions import BasicFunctions as bf
import class_printed_circuit_noise_remover as pcnr
import class_circuit_finder as cf
import class_morphology_user_circuit_element_finder as mucef
import class_region_growing_circuit_element_finder as rgcef
import class_wires_remover2 as wr2
import class_wires_remover as wr

# img = cv2.imread('D://Thesis//test_images//img//b_rot.jpg')
img = cv2.imread('D://Thesis//old circuit lens//test_images//test_004.jpg')
gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

noise_remover = pcnr.PrintedCircuitNoiseRemover()
clean_img = noise_remover.filter(gray)

circuit_finder = cf.CircuitFinder()
circuit = circuit_finder.find(clean_img)

cropped_gray = bf.crop(clean_img, circuit)
cropped_copy = cropped_gray.copy()

# circuit_elements_finder = rgcef.RegionGrowingCircuitElementFinder()

circuit_elements_finder = wr.WiresRemover()
circuit_elements_finder.setKernelSide(6)


# circuit_elements_finder = mucef.MorphologyUserCircuitElementFinder()
# circuit_elements_finder.setLineThickness(circuit_finder.getLineThickness())
# circuit_elements_finder.setKernelSide(circuit_finder.getKernelSide())

cropped_img = bf.crop(img, circuit)

circuit_elements = circuit_elements_finder.find(cropped_gray, cropped_img)

# Draw detected blobs as red circles.
# cv2.DRAW_MATCHES_FLAGS_DRAW_RICH_KEYPOINTS ensures the size of the circle corresponds to the size of blob
# im_with_keypoints = cv2.drawKeypoints(cropped_img, circuit_elements, np.array([]), (0,255,0), cv2.DRAW_MATCHES_FLAGS_DRAW_RICH_KEYPOINTS)

# cropped_img = bf.crop(img, circuit)
# bf.contourMarker(cropped_img, circuit_elements)


cv2.imshow("circuit", img)

cv2.waitKey(0)



