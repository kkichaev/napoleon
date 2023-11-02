export

Variant = Bars
#Variant = Pard

ifeq ($(Variant),)
$(error "Set variant before")
endif

include make.vars 
include Variants\$(Variant)\make.vars

include ..\..\make.rull