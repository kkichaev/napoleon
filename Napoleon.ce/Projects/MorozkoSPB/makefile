export

include make.vars 

mgr_monitor: FEATURES += MorozkoSPBMonitor 
mgr_monitor: PROGRAM=NapoleonMonitor
mgr_monitor: ASSEMBLY_PROJECT="Napoleon Monitor"
mgr_monitor: 
	$(MAKE) -C ../../$(PCORE) -f manager.mk

include ..\..\make.rull