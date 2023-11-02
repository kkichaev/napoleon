#ifdef __cplusplus
extern "C"
{
#endif

   // Returns: Original address of intercepted function (for chaining on)
PROC WINAPI DeepHookImportedFunction(LPCWSTR    pwszModuleToHook,		// Module to intercept calls to
      LPCWSTR    pwszFunctionToHook,   // Function to intercept calls to
      PROC				pfnNewProc,       // New function (replaces old function)
      LPWSTR*    ppwszExcludeList		// List of module names to exclude from the hook
      );

BOOL WINAPI DeepHookRestoreFunction(LPCWSTR    pwszModuleToHook,		// Module to intercept calls to
      LPCWSTR    pwszFunctionToHook,   // Function to intercept calls to
      PROC			pfnNewProc,          // New function (replaces old function)
      LPWSTR*    ppwszExcludeList		// List of module names to exclude from the hook
      );

#ifdef __cplusplus
};
#endif
