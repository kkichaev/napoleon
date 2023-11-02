import cv2 as cv
import sys

fn = sys.argv[1]

src = cv.imread(fn, cv.IMREAD_UNCHANGED)

src[:,:,2] = 109
src[:,:,0] = 109
src[:,:,1] = 109
cv.imwrite(fn, src)

cv.waitKey(0)