export

include make.vars 

mgr_monitor: FEATURES+=EUROASIA_MONTOR
mgr_monitor: PROGRAM=NapoleonMonitor
mgr_monitor: ASSEMBLY_PROJECT="EuroasiaTD Monitor"
mgr_monitor: 
	$(MAKE) -C ../../$(PCORE) -f manager.mk

include ..\..\make.rull
