export

#Variant = BellaNsk
#Variant = BellaBisk
Variant = BellaKr
#Variant = BellaTomsk
# Барнаул сделал Иркутском
#Variant = BellaBarnaul
#Variant = BellaHbr
#Variant = BellIrkutsk


ifeq ($(Variant),)
$(error "Set variant before")
endif

include make.vars 
include Variants\$(Variant)\make.vars

VPATH += 

include ..\..\make.rull