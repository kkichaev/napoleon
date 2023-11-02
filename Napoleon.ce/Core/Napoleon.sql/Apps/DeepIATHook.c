#include <windows.h>
#include "undoc.h"
#include "DeepIATHook.h"


// Macro for adding pointers/DWORDs together without C arithmetic interfering
#define MakePtr( cast, ptr, addValue ) (cast)( (DWORD)(ptr)+(DWORD)(addValue))

typedef BOOL (*_SetKMode)(BOOL);

BOOL HookImportTable(PIMAGE_THUNK_DATA pThunk, PROC pfnOriginalProc, PROC pfnNewProc)
{
	// iterate through imported functions
	while ( pThunk->u1.Function )
	{
		// check against GetProcAddress pointer
		if ( pThunk->u1.Function == (PDWORD)pfnOriginalProc )
		{
			// found it, hook the function
			pThunk->u1.Function = (PDWORD)pfnNewProc;
			return (TRUE);
		}

		// next pointer
		pThunk++;
	}
	return (FALSE);
}

PIMAGE_IMPORT_DESCRIPTOR GetImportDesc(LPCTSTR pwszModuleToHook, LPVOID BasePtr, DWORD rva)
{
	// We know have a valid pointer to the module's PE header.  Now go
	// get a pointer to its imports section
	PIMAGE_IMPORT_DESCRIPTOR pImportDesc = MakePtr(PIMAGE_IMPORT_DESCRIPTOR, BasePtr, rva);

	// Iterate through the array of imported module descriptors, looking
	// for the module whose name matches the pwszModuleToHook parameter
	while ( pImportDesc->Name )
	{
		TCHAR wszMainExclude[1024];
		char* pszModName = MakePtr(char*, BasePtr, pImportDesc->Name);
		MultiByteToWideChar(CP_ACP, 0, pszModName, -1, wszMainExclude, 1024);

		// found the import descriptor we are looking for?
		if ( wcsicmp(wszMainExclude, pwszModuleToHook) == 0 )
			break;

		pImportDesc++;  // Advance to next imported module descriptor
	}

	// end of list == Import Descriptor not found :(
	if ( pImportDesc->Name == 0 )
		return (NULL);

	return (pImportDesc);
}

BOOL HookInternal(LPCWSTR pwszModuleToHook, PROC pfnOriginalProc, PROC pfnNewProc, LPWSTR* ppwszExcludeList)
{
   PIMAGE_IMPORT_DESCRIPTOR pImportDesc;
	PIMAGE_THUNK_DATA pThunk;
	PPROCESS pProcess;
	struct info inf;
	PMODULE pmods;
	LPVOID baseptr;
	BOOL bHooked = FALSE;
	_SetKMode SetKMode;

	SetKMode = (_SetKMode)GetProcAddress(GetModuleHandle(L"coredll.dll"), L"SetKMode");

	SetKMode(TRUE);
	// Get current process struct from KData
	pProcess = KData.pCurPrc;

	// Get process import descriptor
	inf = pProcess->e32.e32_unit[IMP];

	// Get process base pointer
	baseptr = pProcess->BasePtr;
	// Get a pointer to the process' imports section
	pImportDesc = MakePtr(PIMAGE_IMPORT_DESCRIPTOR, baseptr, inf.rva);

	// find the import descriptor table
	pImportDesc = GetImportDesc(pwszModuleToHook, baseptr, inf.rva);
	if ( !pImportDesc )
		goto EXIT;

	// Get a pointer to the process' module's import address table (IAT)
	pThunk = MakePtr(PIMAGE_THUNK_DATA, baseptr, pImportDesc->FirstThunk);

	// HOOK! :)
	if (HookImportTable(pThunk, pfnOriginalProc, pfnNewProc))
		bHooked = TRUE; // flag to indicate that we hooked at least once

	// Iterate through a list of all modules
	for (pmods = (PMODULE) KData.aInfo[KINX_MODULES]; pmods; pmods = pmods->pMod)
	{
		LPWSTR* ppwszTmp = ppwszExcludeList;
		// check to see if our process references this module, if not skip
		if (!HasModRefProcPtr(pmods, pProcess))
			continue;

		// check to see if this is the DLL whose function we want to hook if so skip
		if (!wcsicmp(pmods->lpszModName, pwszModuleToHook))
			continue;

		if (ppwszTmp)
		{
			while (*ppwszTmp)
			{
				// check the exclusion list to see if we should skip this module
				if (!wcsicmp(pmods->lpszModName, *ppwszTmp))
					break;
				ppwszTmp++;
			}
			// found an excluded module, so skip
			if (*ppwszTmp)
				continue;
		}
		// fix the module's base pointer to a process relative address
		baseptr = (LPVOID)ZeroPtr(pmods->BasePtr);

		// if the base pointer does not change we have arom module, 
		// better skip this, we can only hook user modules.
		if (baseptr == pmods->BasePtr)
			continue;
		// get the import description table for the module
		inf = pmods->e32.e32_unit[IMP];
		pImportDesc = GetImportDesc(pwszModuleToHook, baseptr, inf.rva);
		if ( !pImportDesc )
			continue;

		pThunk = MakePtr(PIMAGE_THUNK_DATA, baseptr, pImportDesc->FirstThunk);
		if (HookImportTable(pThunk, pfnOriginalProc, pfnNewProc))
			bHooked = TRUE; // flag to indicate that we hooked at least once
	}

EXIT:
	SetKMode(FALSE);
	return bHooked;
}

PROC WINAPI DeepHookImportedFunction(LPCWSTR    pwszModuleToHook,		// Module to intercept calls to
																		 LPCWSTR    pwszFunctionToHook, // Function to intercept calls to
																		 PROC				pfnNewProc,         // New function (replaces old function)
																		 LPWSTR*    ppwszExcludeList		// List of module names to exclude from the hook
																		 )
{
	BOOL bHooked = FALSE;
	PROC pfnOriginalProc;
	if ( IsBadCodePtr(pfnNewProc) ) // Verify that a valid pfn was passed
		goto EXIT;

	// First, verify the the module and function names passed to use are valid
	pfnOriginalProc = GetProcAddress( GetModuleHandle(pwszModuleToHook), pwszFunctionToHook );
	if ( !pfnOriginalProc )
		goto EXIT;

   bHooked = HookInternal(pwszModuleToHook, pfnOriginalProc, pfnNewProc, ppwszExcludeList);

EXIT:
	return bHooked ? pfnOriginalProc : 0;   // 0 == Function not found
}

BOOL WINAPI DeepHookRestoreFunction(LPCWSTR    pwszModuleToHook,		// Module to intercept calls to
																		 LPCWSTR    pwszFunctionToHook, // Function to intercept calls to
																		 PROC				pfnNewProc,         // New function (replaces old function)
																		 LPWSTR*    ppwszExcludeList		// List of module names to exclude from the hook
																		 )
{
	BOOL bHooked = FALSE;
	PROC pfnOriginalProc;
	if ( IsBadCodePtr(pfnNewProc) ) // Verify that a valid pfn was passed
		goto EXIT;

	// First, verify the the module and function names passed to use are valid
	pfnOriginalProc = GetProcAddress( GetModuleHandle(pwszModuleToHook), pwszFunctionToHook );
	if ( !pfnOriginalProc )
		goto EXIT;

   bHooked = HookInternal(pwszModuleToHook, pfnNewProc, pfnOriginalProc, ppwszExcludeList);

EXIT:
	return bHooked;
}


/*
typedef struct tagExcludeNode
{
	LPWSTR pwszExcludeModule;
	struct tagExcludeNode* pNext;

} EXCLUDE_NODE, *PEXCLUDENODE;


PEXCLUDENODE pRootExclude = NULL;

PEXCLUDENODE createNode(LPWSTR pwszName, PEXCLUDENODE pNext)
{
	PEXCLUDENODE pNode = (PEXCLUDENODE) malloc(sizeof(EXCLUDE_NODE));
	pNode->pNext = pNext;
	pNode->pwszExcludeModule = malloc(wcslen(pwszName) * sizeof(TCHAR) + 1);
	wcscpy(pNode->pwszExcludeModule, pwszName);
	return (pNode);
}


PVOID DeepIATHookBegin(LPWSTR* ppwszExcludeModuleList)
{
	TCHAR wszCurModule[MAX_PATH];
	TCHAR* wszMainExclude;
	GetModuleFileName(NULL, wszCurModule, MAX_PATH);
	wszMainExclude = wcsrchr(wszCurModule, _T('\\'));
	if (wszMainExclude)
	{
		*(wszMainExclude++) = 0;
	}
	else
	{
		wszMainExclude = wszCurModule;
	}
	pRootExclude = createNode(wszMainExclude, NULL);
	while (*ppwszExcludeModuleList)
	{
		pRootExclude = createNode(*ppwszExcludeModuleList, pRootExclude);
	}
	return (pRootExclude);
}
*/