export

Variant = Nikolaevka
#Variant = Urupinsk
#---- не работают Variant = FWMich

ifeq ($(Variant),)
$(error "Set variant before")
endif

include make.vars 
include Variants\$(Variant)\make.vars

VPATH += 

include ..\..\make.rull