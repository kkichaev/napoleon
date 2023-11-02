export

Variant = Incotek
#Variant = IncYouth
#Variant = Habarovskiy
#Variant = Bagira
#Variant = AmurskijTD
#Variant = TDSever
#Variant = YarinovSpassky

ifeq ($(Variant),)
$(error "Set variant before")
endif

include make.vars 
include Variants\$(Variant)\make.vars

VPATH += 

include ..\..\make.rull