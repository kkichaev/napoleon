export

#Variant=NBtlAM
#Variant=NBtlLebedev
#Variant=NBtlBochk
#Variant=NBtlSladomir
#Variant=NBtlAntonov
#Variant=NBtlZagetskaya
Variant=NBtlKudrMK
#Variant=NBtlBochkBrnl
#Variant=NBtlSibGruman
#Variant=NBtlGoldenShelf

include make.vars 

mgr_monitor: FEATURES += NbtlMonitor 
mgr_monitor: PROGRAM=NapoleonMonitor
mgr_monitor: ASSEMBLY_PROJECT="NBTL Monitor"
mgr_monitor: 
	$(MAKE) -C ../../$(PCORE) -f manager.mk

include ..\..\make.rull

