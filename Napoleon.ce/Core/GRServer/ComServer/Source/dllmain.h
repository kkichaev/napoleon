// dllmain.h: объ€вление класса модул€.

class CComGRServerModule : public CAtlDllModuleT< CComGRServerModule >
{
public :
	DECLARE_LIBID(LIBID_ComGRServerLib)
	//DECLARE_REGISTRY_APPID_RESOURCEID(IDR_COMGRSERVER, "{3D662191-34EF-4EFB-A47F-069B136CAA70}")
};

extern class CComGRServerModule _AtlModule;
