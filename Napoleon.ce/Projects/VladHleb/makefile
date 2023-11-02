export

Variant = VladHleb
#Variant = VladHlebDom -- закрыт

ifeq ($(Variant),)
$(error "Set variant before")
endif

include make.vars 
include Variants\$(Variant)\make.vars

VPATH += 

include ..\..\make.rull