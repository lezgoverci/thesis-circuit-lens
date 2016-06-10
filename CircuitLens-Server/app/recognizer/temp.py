import cv2
import numpy as np
import math

def perp( a ) :
    b = np.empty_like(a)
    b[0] = -a[1]
    b[1] = a[0]
    return b

# line segment a given by endpoints a1, a2
# line segment b given by endpoints b1, b2
# return 
def segmentsIntersect((a1,a2), (b1,b2)) :
    da = a2-a1
    db = b2-b1
    dp = a1-b1
    
    dap = perp(da)
    denom = np.dot( dap, db)
    num = np.dot( dap, dp )
    
    print "denom: " + str(denom.astype(float)) + " mult: " + str(1000 * denom.astype(float))
    return ((num * 1000) / (1000 * denom))*db + b1

def lineIntersection(line1, line2):
    xdiff = (line1[0][0] - line1[1][0], line2[0][0] - line2[1][0])
    ydiff = (line1[0][1] - line1[1][1], line2[0][1] - line2[1][1]) #Typo was here

    def det(a, b):
        return a[0] * b[1] - a[1] * b[0]

    div = det(xdiff, ydiff)
    if div == 0:
       raise Exception('lines do not intersect')

    d = (det(*line1), det(*line2))
    x = det(d, xdiff) / div
    y = det(d, ydiff) / div
    
    return np.array([x, y])

def calculateAngle(a, b, directionMatters=True):
    a_mag = np.linalg.norm(a)
    b_mag = np.linalg.norm(b)
    
    temp_angle = math.degrees(math.acos(round(np.vdot(a, b) / (a_mag * b_mag), 4)))
    
    if directionMatters and b[1] - a[1] < 0:
        temp_angle = 360 - temp_angle
    
    return temp_angle

def calculateTriangleCentroid((a, b, c)):
    l1 = ((a + b) / 2, c)
    l2 = ((b + c) / 2, a)
    
    # return segmentsIntersect(l1, l2)
    return lineIntersection(l1, l2)

def calculatePointsDistance(a, b):
    return np.linalg.norm(a - b)

def calculatePointLineDistance(p, (vA, vB)):
    vC = vB - vA

    mag = np.linalg.norm(vC)
    numerator = abs((vC[1] * p[0]) - (vC[0] * p[1]) + ((vB[0] * vA[1]) - (vB[1] * vA[0])))
    
    return numerator / mag
    
def calculateSumOfRatios(corners, centroid, area):
    unsorted_central_angles = [corner - centroid for corner in corners]
    
    body_centroid = np.array([1.0, 0.0, 0])
    
    angle_vector_map = {calculateAngle(body_centroid, v): v for v in unsorted_central_angles}
    central_angles = sorted(angle_vector_map)
    central_angles.append(central_angles[0])
    
    disperseness_from_centroid = 0.0
    
    prev_vector = angle_vector_map[central_angles[0]]
    i = 1
    while i < len(central_angles):
        try:
            current_vector = angle_vector_map[central_angles[i]]
            normalizer = calculatePointsDistance(current_vector, prev_vector)
            
            if 0 != normalizer:
                disperseness_from_centroid += np.linalg.norm(np.cross(prev_vector, current_vector)) / normalizer
        except Exception as e:
            print e
        
        prev_vector = current_vector
        
        i += 1
    
    #Gearness feature
    prev_vector = angle_vector_map[central_angles[0]]
    gearness_temp = 0
    
    i = 1
    while i < len(central_angles):
        try:
            current_vector = angle_vector_map[central_angles[i]]
            
            next_vector_index = i + 1 if i + 1 < len(central_angles) else 0
            next_vector = angle_vector_map[central_angles[next_vector_index]]
            
            rB = prev_vector - current_vector
            rA = next_vector - current_vector

            normalizer = calculatePointsDistance(rB, rA)

            if 0 != normalizer:
                gearness_temp += np.linalg.norm(np.cross(rB, rA)) / normalizer
        except Exception as e:
            print e
        
        prev_vector = current_vector
        
        i += 1
    
    gearness = math.pow(gearness_temp, 2) / (2 * np.pi * area)
    
    return [disperseness_from_centroid, gearness]

def calculateUnitVector(v):
    return v / np.linalg.norm(v)

def calculateResultantVector(img):
    m = cv2.moments(img)
        
    xbar = int(m['m10'] / m['m00'])
    ybar = int(m['m01'] / m['m00'])
 
    h, w= img.shape[:2]
    
    xs, ys = 0, 0
    
    y = 0
    while y < h:
        x = 0
        while x < w:
            if 0 != img[y][x] and x != xbar and y != ybar:
                v = np.array([float(x - xbar), float(y - ybar)])
                xsub, ysub = calculateUnitVector(v)
                
                xs += xsub
                ys += ysub
            x += 1
        y += 1
    

    
    return np.array([xs, ys])

def hull(img):
    _, o, h = cv2.findContours(img.copy(), cv2.RETR_CCOMP, cv2.CHAIN_APPROX_SIMPLE)
    
    color = (255, 0, 0)
    thickness = 2
    max_o = 0
    
    area = 0
    for cnt in o:
        x,y,w,h = cv2.boundingRect(cnt)
        if w * h > area:
            max_o = cnt
            area = w * h
    
    hull1 = cv2.convexHull(max_o)
        
    poly = []
    for [h] in hull1:
        cv2.circle(img, (h[0], h[1]), 3, 255)
        poly.append(h)
    
    polygon = np.array(poly, dtype=np.int32)
    z = np.int32(np.zeros((100, 100), dtype=np.uint32))
    cv2.fillConvexPoly(z, polygon, 255)
    
    return np.uint8(z)

def toComparable(img):
    m = cv2.moments(img)
        
    xbar = int(m['m10'] / m['m00'])
    ybar = int(m['m01'] / m['m00'])
    
    img = np.float32(img)
    dst = cv2.cornerHarris(img,2,3,0.04)
    
    img = np.uint8(img)
    h, w = img.shape[:2]
    
    corners = []
    
    d = np.zeros(img.shape, np.uint8)
    y = 0
    while y < h:
        x = 0
        while x < w:
            if 0 != img[y][x] and dst[y][x] > 0.01 * dst.max():
                corners.append(np.array([float(x), float(y), 0.0]))
                cv2.line(d, (xbar, ybar), (x, y), 255, 2)
            x += 1
        y += 1
    
    return d, corners, np.array([xbar, ybar, 0.0])
    
    goodFeatures = cv2.goodFeaturesToTrack(img, 25, 0.01, 10)
    goodFeatures = np.int0(goodFeatures)
    
    for i in goodFeatures:
        x, y = i.ravel()
        corners.append(np.array([float(x), float(y), 0.0]))
        cv2.line(d, (xbar, ybar), (x, y), 255, 2)
    
    return d, corners, np.array([xbar, ybar, 0])

def fillHoles(img):
    _, cntrs, _ = cv2.findContours(img.copy(),cv2.RETR_CCOMP,cv2.CHAIN_APPROX_SIMPLE)
    
    for cnt in cntrs:
        cv2.drawContours(img,[cnt],0,255,-1)
    
    return img

def matchShapes(img1, img2):
    h1 = cv2.HuMoments(cv2.moments(img1)).flatten()
    h2 = cv2.HuMoments(cv2.moments(img2)).flatten()
    
    s = 0
    for i in range(7):
        mi1 = h1[i]
        mi2 = h2[i]
        
        s += abs(mi1 - mi2)
    
    return s

def featureDistance(f1, f2):
    distance = 0
    for i in range(len(f1)):
        distance += abs(f1[i] - f2[i])
    
    return np.linalg.norm(distance)

def calculateFeatures(img):
    g, corners, centroid = toComparable(img)
    moments = cv2.moments(g)
    s = calculateSumOfRatios(corners, centroid, moments['m00'])

    # second_order_diff = moments['nu20'] - moments['nu02']
    # second_order_diff_pow = math.pow(second_order_diff, 2)
    # second_order_sum = moments['nu20'] + moments['nu02']
    # second_order_sum_pow = math.pow(second_order_sum, 2)
    # first_order_prod_pow = 4 * math.pow(moments['nu11'], 2)
    
    # e = (second_order_diff_pow - first_order_prod_pow) / second_order_sum_pow
    
    # edges = cv2.Canny(img,100,200)
    # k = math.pow(sum(sum(edges)), 2) / (2 * np.pi * moments['m00'])
    
    # egv_pos = math.sqrt(0.5 * second_order_sum + math.sqrt(first_order_prod_pow - second_order_diff_pow))
    # egv_neg = math.sqrt(0.5 * second_order_sum - math.sqrt(first_order_prod_pow - second_order_diff_pow))

    return s, g
    
class Recognizer:
    def train(self, name, theClass):
        n = [
            'symbol_capacitor.png',
            'symbol_resistor.png',
            'symbol_voltage_source.png',
            'symbol_diode.png',
            'symbol_inductor.png'
        ]
    
        img = cv2.imread("D:\\Thesis\\Recognizer\\training_set\\" + name)
        # img = cv2.resize(img, (100, 100), interpolation = cv2.INTER_CUBIC)
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        gray = cv2.bitwise_not(gray)
        gray[gray > 100] = 255
        gray[gray <= 100] = 0
        
        # v1 = calculateResultantVector(gray)
        # gray, c, cen = toComparable(gray)
        # val = calculateSumOfRatios(c, cen)
        val, gray = calculateFeatures(gray)
        # gray = fillHoles(gray)
        min_val = float('inf')
        # print "value 1: " + str(val)
        # gray = hull(gray)
        
        for q in n:
            img2 = cv2.imread("D:\\Thesis\\Recognizer\\training_set\\" + q)
            # img2 = cv2.resize(img2, (100, 100), interpolation = cv2.INTER_CUBIC)
            gray2 = cv2.cvtColor(img2, cv2.COLOR_BGR2GRAY)
            gray2 = cv2.bitwise_not(gray2)
            gray2[gray2 > 100] = 255
            gray2[gray2 <= 100] = 0
            
            # v2 = calculateResultantVector(gray2)
            # gray2, c2, cen2 = toComparable(gray2)
            val2, gray2 = calculateFeatures(gray2)
            
            # val2 = calculateSumOfRatios(c2, cen2)
            # gray2 = fillHoles(gray2)
            
            # cv2.circle(img, (x, y), 3, (0,255,0), -1)
            
            # cv2.circle(img, (xbar, ybar), 3, 255, -1)
            
                
            # area = 0
            # for cnt in o2:
            #     x,y,w,h = cv2.boundingRect(cnt)
            #     if w * h > area:
            #        max_o2 = cnt
            #        area = w * h
                
            # x,y,w,h = cv2.boundingRect(max_o)
            # cv2.rectangle(img, (x, y), (x + w, y + h), color, thickness)
            
            # x,y,w,h = cv2.boundingRect(max_o2)
            # cv2.rectangle(img2, (x, y), (x + w, y + h), color, thickness)
            # gray2 = hull(gray2)
            # matchVal = cv2.matchShapes(gray / 255, gray2 / 255, 2, 0.0)
            # matchVal = calculateAngle(v1, v2)
            
            matchVal = np.linalg.norm(featureDistance(val, val2))
            
            if min_val > matchVal or math.isinf(min_val):
                min_val = matchVal
                target = q
                last = gray2
            
            print q, matchVal, np.linalg.norm(val2)

        print name + " and " + target + " matched! " + str(np.linalg.norm(val))
        
        cv2.imshow("Good Features", gray);
        cv2.imshow("Good Features2", last);
        # cv2.imshow("Polygon", np.int32(hull));
        cv2.waitKey(0)

r = Recognizer()
r.train("resistor.png", "D:\\Thesis\\Recognizer\\training_set\\resistor.jpg")
