export

#Variant = Delta
#Variant = DeltaNadym
#Variant = DeltaUrengoy
Variant = DeltaSurgut

ifeq ($(Variant),)
$(error "Set variant before")
endif

include make.vars 
include Variants\$(Variant)\make.vars

VPATH += 

include ..\..\make.rull