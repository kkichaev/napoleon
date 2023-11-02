import cv2 as cv
import sys

fn = sys.argv[1]

src = cv.imread(fn, cv.IMREAD_UNCHANGED)

#src[:,:,2] = 0
#src[:,:,0] = 196
#src[:,:,1] = 124
src[:,:,2] = 128
src[:,:,0] = 128
src[:,:,1] = 128
cv.imwrite(fn, src)

cv.waitKey(0)