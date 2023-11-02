// dllmain.h: объ€вление класса модул€.

class CNapoleon1cModule : public CAtlDllModuleT< CNapoleon1cModule >
{
public :
	DECLARE_LIBID(LIBID_Napoleon1cLib)
	DECLARE_REGISTRY_APPID_RESOURCEID(IDR_NAPOLEON1C, "{C7FF0FA4-720A-4F1C-896F-AEF9E4808EDE}")
};

extern class CNapoleon1cModule _AtlModule;
