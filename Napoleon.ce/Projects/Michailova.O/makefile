export

Variant = MichO
#Variant = MichA
#Variant=MichTDJogurt #закрыли лицензии перенесли в МиахйловуО

ifeq ($(Variant),)
$(error "Set variant before")
endif

include make.vars 
include Variants\$(Variant)\make.vars

VPATH += 

include ..\..\make.rull