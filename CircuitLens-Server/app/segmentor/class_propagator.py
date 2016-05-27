class Propagator:
    def __init__(self, seed, maxMatrixDim, lineMark=127, elemMark=255):
        self.__seed = seed
        self.__maxY, self.__maxX = maxMatrixDim
        
        self.__lowerX, self.__lowerY = seed
        self.__upperX, self.__upperY = seed
        
        self.__lineMark = lineMark
        self.__elemMark = elemMark
        
        # Top, Right, Bottom, Left
        self.__expandables = [True, True, True, True]
        self.__steps = [(1, 0), (0, 1), (-1, 0), (0, -1)]
        
        self.propagate()
        self.updateStartersAndStoppers()
    
    def updateStartersAndStoppers(self):
        self.__starters = [
                            (self.__lowerX, self.__lowerY), \
                            (self.__upperX, self.__lowerY), \
                            (self.__upperX, self.__upperY), \
                            (self.__lowerX, self.__upperY)
                          ]
        self.__stoppers = [
                            (self.__upperX, self.__lowerY), \
                            (self.__upperX, self.__upperY), \
                            (self.__lowerX, self.__upperY), \
                            (self.__lowerX, self.__lowerY)
                          ]
        
        # print "propagates: %s" % (str(self.__expandables))
        # print "rect: %s" % (str(self.getBoundingRect()))
        
    def propagate(self):
        if self.__expandables[0]:
            if self.__lowerY - 1 < 0:
                self.__expandables[0] = False
            else:
                self.__lowerY -= 1
        
        if self.__expandables[1]:
            if self.__upperX + 1 >= self.__maxX:
                self.__expandables[1] = False
            else:
                self.__upperX += 1
        
        if self.__expandables[2]:
            if self.__upperY + 1 >= self.__maxY:
                self.__expandables[2] = False
            else:
                self.__upperY += 1
        
        if self.__expandables[3]:
            if self.__lowerX - 1 < 0:
                self.__expandables[3] = False
            else:
                self.__lowerX -= 1
    
    def checkBorders(self, matrix):
        elemCounters = [0, 0, 0, 0]
        lineMarkCounters = {0:0, 1:0, 2:0, 3:0}
        lineLen = [0, 0, 0, 0]
        
        for i in range(4):
            x, y = self.__starters[i]
            
            lineMarks = []
            
            while (x, y) != self.__stoppers[i]:
                if 0 != matrix[y][x]:
                    if self.__lineMark == matrix[y][x]:
                        lineMarkCounters[i] += 1
                        lineMarks.append((x, y))
                    else:
                        elemCounters[i] += 1
                    
                x += self.__steps[i][0]
                y += self.__steps[i][1]
            
            if 2 <= len(lineMarks):
                lineLen[i] = abs((lineMarks[-1][0] - lineMarks[-2][0]) + (lineMarks[-1][1] - lineMarks[-2][1]))
            
            if 0 == elemCounters[i] + lineMarkCounters[i] or \
               (2 <= lineMarkCounters[i] and 0 != elemCounters[i]):
                    self.__expandables[i] = False
            elif 1 == lineMarkCounters[i]:
                matrix[lineMarks[0][1]][lineMarks[0][0]] = self.__elemMark
            else:
                self.__expandables[i] = True
        
        if not self.propagatable():
            not_zero_borders = [i for i, e in lineMarkCounters.iteritems() if e >= 2]
            
            print "num: %d" % (len(not_zero_borders))
            
            #Check if there's only one line of all borders
            if 1 == len(not_zero_borders):
                self.__expandables[not_zero_borders[0]] = True
            elif 1 < len(not_zero_borders):
                one_lens = [i for i in not_zero_borders if lineLen[i] == 1]
                
                for i in one_lens:
                    self.__expandables[i] = True
                
                # len_a = lineLen[not_zero_borders[0]]
                # len_b = lineLen[not_zero_borders[-1]]
                
                # print "a: %d b:%d" %(len_a, len_b)
                
                # if 1 < len_a and 1 < len_b:
                #     error = ((len_a - len_b) * 200) / (len_a + len_b)
                    
                #     if 30 < error:
                #         if len_a > len_b:
                #             self.__expandables[not_zero_borders[0]] = True
                #         else:
                #             self.__expandables[not_zero_borders[-1]] = True
                # else:
                #     self.__expandables[not_zero_borders[0]] = True
                #     self.__expandables[not_zero_borders[-1]] = True
            # counter = 0
            # target_i = 0
            
            # twoLineCounter = 0
            # targetTwo = []
            
            # affected = [1, 2, 1, 2]
            
            # for i in range(4):
            #     if 1 == lineMarkCounters[i]:
            #         self.__expandables[affected[i]] = True
                
            #     if 1 <= lineMarkCounters[i]:
            #         counter += 1
            #         target_i = i
                
            #     if 2 <= lineMarkCounters[i]:
            #         twoLineCounter += 1
            #         targetTwo.append(i)
                
            # if 1 == counter:
            #     self.__expandables[target_i] = True
            
            # if 2 == twoLineCounter:
            #     a = targetTwo[0]
            #     b = targetTwo[-1]
                
            #     ave = lineLen[a] + lineLen[b]
            #     diff = (lineLen[a] - lineLen[b]) * 200
                
            #     error = abs(diff / ave)
                
            #     if 30 <= error:
            #         self.__expandables[a] = True
            #         self.__expandables[b] = True
                
            
    def propagatable(self):
        return 0 < sum(self.__expandables)
    
    def getBoundingRect(self):
        return ((self.__lowerX, self.__lowerY), (self.__upperX, self.__upperY))