export

#Variant = MichelK
#Variant = MichelK2
#Variant = MichelK3
Variant = MichelK4
#Variant = MichelkDost

ifeq ($(Variant),)
$(error "Set variant before")
endif

include make.vars 
include Variants\$(Variant)\make.vars

VPATH += 

include ..\..\make.rull