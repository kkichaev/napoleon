export

Variant=ClassicSpb
#Variant=ClassicMsk
#Variant=ClassicMonitor

include make.vars 

mgr_monitor: FEATURES += ClassicMonitor 
mgr_monitor: PROGRAM=NapoleonMonitor
mgr_monitor: ASSEMBLY_PROJECT="Classic Monitor"
mgr_monitor: 
	$(MAKE) -C ../../$(PCORE) -f manager.mk

include ..\..\make.rull
