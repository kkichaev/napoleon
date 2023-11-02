export

Variant = Himbytorg
# Variant = HBTLipezk
#Variant = HBTKaluga

ifeq ($(Variant),)
$(error "Set variant before")
endif

include make.vars 
include Variants\$(Variant)\make.vars

VPATH += 

include ..\..\make.rull