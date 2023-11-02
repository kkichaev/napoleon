#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sun Mar 14 17:22:46 2021

@author: user
"""

import numpy as np
import matplotlib.pyplot as plt
import cv2 as cv
from scipy.sparse.linalg import spsolve
from scipy.sparse import csr_matrix

import time

def makeContour(i1, i2):
    diff = abs(i2 - i1) > 5
    diff = diff.astype(np.uint8)[:, :, 0]
    
    ctr, _ = cv.findContours(diff, cv.RETR_EXTERNAL, cv.CHAIN_APPROX_NONE)
    
    cntr = np.reshape(ctr[0], (-1, 2))
    
    lt_ = cntr.min(axis=0)
    wh_ = cntr.max(axis=0) - lt_
    
    return cntr, lt_, wh_

def getLine(row, plane, img, destY, dest, contour, maskImg) :
    ctrRow = contour[contour[:, 1] == row, 0]

    mask = maskImg[row, :, plane] != 0 
    line = img[row, :, plane]
    # line = np.zeros(img.shape[1])
    # line[mask] = img[row, mask, plane]
    
    destRow = dest[destY, :, plane]
    
    boundMask = np.ones_like(mask)
    boundMask[ctrRow]= 0

    return (boundMask, line, destRow, mask, ctrRow)
    
            
def setMatrix(indptr, indices, data, B, rows, cur, boundaryOffset):
    prevBoundMask, prvRow, prvDest, prvMask, _ = (None, None, None, None, None) if rows[0] == None else rows[0]
    nextBoundMask, nextRow, nextDest, nextMask, _ = (None, None, None, None, None) if rows[2] == None else rows[2]
    curBoundMask, curRow, curDest, curMask, curCtr = rows[1]
        
    curRow = curRow.astype(np.int16)
    curLen = curMask.sum()
    prvLen = 0
    if rows[0] != None: 
        prvRow = prvRow.astype(np.int16)
        prvLen = prvMask.sum()
    
    if rows[2] != None: 
        nextRow = nextRow.astype(np.int16)
    
    for pos in range(curCtr.min(), curCtr.max() + 1):
        curNh = 0
        curB = 0
        
        if not curMask[pos] : continue
        
        curI = curRow[pos]
        
        if rows[0] != None and prvMask[pos]:
            curB -= (prvRow[pos] - curI)
            curNh += 1
            if prevBoundMask[pos]:
                indices.append(cur - prvLen)
                data.append(-1)
                # A[cur][cur - prvMask.sum()] = -1
            else:
                curB += prvDest[pos + boundaryOffset]
            
        if curMask[pos-1]:
            curB -= (curRow[pos - 1] - curI)
            curNh += 1
            if curBoundMask[pos-1]:
                indices.append(cur - 1)
                data.append(-1)
                # A[cur][cur - 1] = -1
            else:
                curB += curDest[pos - 1 + boundaryOffset] 
            
        if curMask[pos+1]:
            curNh += 1
            curB -= (curRow[pos + 1] - curI)
            if curBoundMask[pos+1]:
                indices.append(cur + 1)
                data.append(-1)
                # A[cur][cur + 1] = -1
            else:
                curB += curDest[pos + 1 + boundaryOffset]

        if rows[2] != None and nextMask[pos]:
            curNh += 1
            curB -= (nextRow[pos] - curI)
            if nextBoundMask[pos]:
                indices.append(cur + curLen)
                data.append(-1)
                # A[cur][cur + curMask.sum()] = -1
            else:
                curB += nextDest[pos + boundaryOffset]
            
        B[cur] = curB

        indices.append(cur)
        data.append(curNh)
        indptr.append(len(indices))        
        # A[cur][cur] = curNh
        cur += 1
        
    return cur

def solver(destY, destX, mask, src1, plane, asize, lt, wh) :
    # A = np.zeros((asize, asize))
    B = np.zeros(asize)
    
    indptr = [0]
    indices = []
    data = []
    
    offset = 0
    prevRow = None
    curRow = None
    nextRow = None
    
    lastRow = lt[1] + wh[1]
    for row in range(lt[1], lastRow + 1) :
            
        prevRow = curRow
        if nextRow != None: 
            curRow = nextRow
        else:
            curRow = getLine(row, plane, src1, destY, dest, contour, mask)
    
        if row + 1 < lastRow:
            nextRow = getLine(row + 1, plane, src1, destY, dest, contour, mask)
        else :
            nextRow = None
    
        offset = setMatrix(indptr, indices, data,  B, (prevRow, curRow, nextRow), offset, destX - lt[0])
        
        destY += 1
    
    
    A = csr_matrix((data, indices, indptr))
    
    X = spsolve(A, B)
    X[X<0] = 0
    X[X>255] = 255
    X =  X.astype(np.uint8)
    X1 = mask[:, :, 0].copy()
    np.place(X1, X1 != 0, X)
    
    X1 = X1[lt[1]:wh[1] + lt[1], lt[0]:wh[0] + lt[0]]
    return X1.reshape(X1.shape[0], X1.shape[1], 1)


IMG1 = 'img1.png'
IMG2 = 'img2.png'

DEST = 'dest.png'

src1 = cv.imread(IMG1)
# src1 = cv.cvtColor(src1, cv.COLOR_BGR2RGB)
src2 = cv.imread(IMG2)
# src2 = cv.cvtColor(src2, cv.COLOR_BGR2RGB)

dest = cv.imread(DEST)
# dest = cv.cvtColor(dest, cv.COLOR_BGR2RGB)
# plt.imshow(dest)

contour, lt, wh = makeContour(src1, src2)

img = np.zeros_like(src1)
mask = cv.drawContours(img, [contour], 0, (1, 1, 1), cv.FILLED)


asize = mask[:,:,0].sum()

destY = 200
destX = 380

plane = 0

t0 = time.clock_gettime(time.CLOCK_BOOTTIME)

out = None
for plane in range(3):
    tx = solver(destY, destX, mask, src1, plane, asize, lt, wh)
        
    if plane == 0 : out = tx
    else: out = np.concatenate((out, tx), axis=2)

t1 = time.clock_gettime(time.CLOCK_BOOTTIME) - t0
print ( 'Elapsed', t1)

out1 = dest[destY:wh[1]+destY, destX:wh[0]+destX]
mask0 = mask[lt[1]:wh[1] + lt[1], lt[0]:wh[0] + lt[0], :] != 0
np.copyto(out1, out, where=mask0)

dest[destY:wh[1]+destY, destX:wh[0]+destX] = out1

plt.imshow(dest)
cv.imwrite('dest1.png', dest)
