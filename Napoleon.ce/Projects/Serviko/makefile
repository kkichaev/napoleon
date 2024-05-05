export

include make.vars 

Variant=SVKIrk
#Variant=MorePivoIrk
#Variant=SVKUUde
#Variant=SVKSayansk
#Variant=SVKKYarsk
#Variant=SVKBratsk
#Variant=SVKTulun
#Variant=SVKFenixUUde
#Variant=SVKAngarsk
#Variant=SVKChita
#Variant=VIKOChita
#Variant=SVKChrmh
#Variant=SVKPtrZbkl
#Variant=SVKBaikalsk
#Variant=SVKSlyudyanka
#Variant=SVKUstKut
#Variant=SVKGlgIlim
#Variant=SVKUstIlimsk
#Variant=SVKTayshet
#Variant=VIKOBorsya
#Variant=SVKNigneudinsk
#Variant=SVKZlng
#Variant=SVKLesosib
#Variant=SVKKansk
#Variant=SVKAchinsk
#Variant=SVKKamensk
#Variant=SVKAbakan
#Variant=SVKKyzyl

ifneq ($(Variant),SVKIrk)
FEATURES+= PRESENTATION_EDITOR 
endif

include ..\..\make.rull


put_mgr:
	# echo $(Variant) $(ManagerVersion)
	python upload_mgr_version.py $(Variant) $(ManagerVersion)