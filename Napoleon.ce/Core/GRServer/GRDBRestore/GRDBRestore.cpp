// GRDBRestore.cpp : Defines the entry point for the application.
//

#include "stdafx.h"
#include "GRDBRestore.h"

#include <string>
#include <shlobj.h> // set default path for exchnagefolder
#include <commdlg.h>

const char SERVER_BASE[] = "serverBase";
const char BACKUP_FOLDER[] = "backupFolder";
HINSTANCE hInst;								// current instance

std::string backupFile;

class ServerConfig
{
public:
	static const char* BackupExtention;
	static const char* BackupPrefix;
	static const char* BackupFileTag;

	ServerConfig() {}

	bool Read(const std::string fileName);

	std::string serverBase;
	std::string backupFolder;
	std::string fileName;

	bool SetValue(const std::string& key, const std::string& value);
};

struct BkcpHeader
{
	char tag[5];
	char bkTime[16];
};

const char* ServerConfig::BackupExtention = ".bkp";
const char* ServerConfig::BackupPrefix = "grs";
const char* ServerConfig::BackupFileTag = "GRBKP";


bool ReadLine(std::string* v, FILE *f)
{
	char buf[100];

	if (fgets(buf, sizeof(buf), f) == NULL) return false;

	v->clear();

	do
	{
		char *p = strchr(buf, '\n');
		if (p != NULL)
		{
			*p = '\0';
			v->append(buf, p - buf);
			break;
		}

		v->append(buf, strlen(buf));
	} while (fgets(buf, sizeof(buf), f) != NULL);

	return true;
}

void Trim(std::string* res, const std::string& _src, size_t offset, size_t size)
{
	const std::string& src = _src.substr(offset, size);

	size_t es = 0, ss = 0;
	std::string::const_reverse_iterator rb = src.rbegin();
	while (rb != src.rend())
	{
		if (*rb != ' ') break;
		rb++;
		es++;
	}

	std::string::const_iterator b = src.begin();
	while (b != src.end())
	{
		if (*b != ' ') break;
		b++;
		ss++;
	}

	res->assign(src.substr(ss, src.size() - es - ss));
}

static void SetFolder(std::string* str, const std::string& value)
{
	*str = value;
#ifdef UNIX
	if (str->empty() || *str->rbegin() != '/')
		str->append(1, '/');
	ConvertPath(*str, str);
#else
	if (str->empty() || *str->rbegin() != '\\')
		str->append(1, '\\');
#endif
}

bool ServerConfig::SetValue(const std::string& key, const std::string& value)
{
	bool res = true;

	const char *k = key.c_str();

	if (strcmp(k, SERVER_BASE) == 0) serverBase = value;
	else if (strcmp(k, BACKUP_FOLDER) == 0) SetFolder(&backupFolder, value);

	return res;
}

bool ServerConfig::Read(const std::string fileName)
{
	this->fileName = fileName;

	serverBase.clear();
	backupFolder.clear();

	FILE *rd = fopen(fileName.c_str(), "rt");
	if (rd != NULL)
	{
		std::string line;
		while (ReadLine(&line, rd))
		{
			size_t pos = line.find('=');
			if (pos != std::string::npos)
			{
				std::string key, value;

				Trim(&key, line, 0, pos);
				Trim(&value, line, pos + 1, -1);

				SetValue(key, value);
			}
		}
		fclose(rd);
	}
	return true;
}

static int CALLBACK InitialSetFolder(HWND hWnd, UINT iMsg, LPARAM, LPARAM lData)
{
	if (iMsg == BFFM_INITIALIZED)
		SendMessage(hWnd, BFFM_SETSELECTION, TRUE, lData);

	return 0;
}

static bool SetFolder(HWND hwndDlg, int id, int idsTitle)
{
	bool ret = false;

	LPMALLOC pMalloc;
	LPWSTR lpBuf;
	BROWSEINFO bi;
	LPITEMIDLIST pidlBrowse;

	wchar_t title[100];
	LoadString(hInst, idsTitle, title, sizeof(title) / sizeof(title[0]));

	SHGetMalloc(&pMalloc);

	lpBuf = (LPWSTR)pMalloc->Alloc(MAX_PATH * sizeof(wchar_t));

	bi.hwndOwner = NULL;
	bi.pidlRoot = NULL;
	bi.pszDisplayName = lpBuf;
	bi.lpszTitle = title;
	bi.ulFlags = 0;

	GetDlgItemText(hwndDlg, id, lpBuf, MAX_PATH);
	if (*lpBuf)
	{
		bi.lpfn = (BFFCALLBACK)InitialSetFolder;
		bi.lParam = (LPARAM)lpBuf;
	}
	else
	{
		bi.lpfn = NULL;
		bi.lParam = 0;
	}

	pidlBrowse = SHBrowseForFolder(&bi);
	if (pidlBrowse != NULL)
	{
		if (SHGetPathFromIDList(pidlBrowse, lpBuf))
		{
			SetDlgItemText(hwndDlg, id, lpBuf);
			ret = true;
		}

		pMalloc->Free(pidlBrowse);
	}

	pMalloc->Free(lpBuf);
	pMalloc->Release();

	return ret;
}

ServerConfig sc;

void UpdateDialog(HWND hWnd)
{

}

static void FreeList(HWND hList)
{
	while (SendMessage(hList, LB_GETCOUNT, 0, 0) > 0)
	{
		std::string* fStr = (std::string*)SendMessage(hList, LB_GETITEMDATA, 0, 0);
		delete fStr;
		SendMessage(hList, LB_DELETESTRING, 0, 0);
	}
}

static void RefreshCopies(HWND hWnd)
{
	char buf[MAX_PATH + 100];
	GetDlgItemTextA(hWnd, IDC_EXCHANGE_FOLDER, buf, sizeof(buf));

	HWND hList = GetDlgItem(hWnd, IDC_ITEMS);
	FreeList(hList);

	
	std::string folder(buf);
	if (folder.empty())
		return;

	if (*folder.rbegin() != '\\')
		folder += "\\";

	std::string ffile(folder);
	ffile.append(ServerConfig::BackupPrefix).append("*").append(ServerConfig::BackupExtention);

	WIN32_FIND_DATAA ffd;
	HANDLE hFind = FindFirstFileA(ffile.c_str(), &ffd);
	if (hFind != INVALID_HANDLE_VALUE)
	{
		while (true)
		{
			bool used = false;
			std::string *fname = new std::string(folder + ffd.cFileName);
			FILE *f = fopen(fname->c_str(), "rb");
			if (f != NULL)
			{
				BkcpHeader header;

				fread(&header, sizeof(header), 1, f);
				fclose(f);

				if (memcmp(header.tag, ServerConfig::BackupFileTag, sizeof(header.tag)) == 0)
				{
					char str[200];

					SYSTEMTIME st;
					FILETIME ft;
					sscanf(header.bkTime, "%08X%08X", &ft.dwHighDateTime, &ft.dwLowDateTime);

					FileTimeToSystemTime(&ft, &st);
					sprintf(str, "%02d.%02d.%d %02d:%02d", st.wDay, st.wMonth, st.wYear, st.wHour, st.wMinute);
					int idx = SendMessageA(hList, LB_ADDSTRING, 0, (LPARAM)str);
					SendMessage(hList, LB_SETITEMDATA, idx, (LPARAM)fname);

					used = true;
				}
			}

			if (!used)
			{
				delete fname;
			}


			if (FindNextFileA(hFind, &ffd) == FALSE)
				break;
		}
		FindClose(hFind);
	}
}

static void Init(HWND hWnd)
{
	SetDlgItemTextA(hWnd, IDC_EXCHANGE_FOLDER, sc.backupFolder.c_str());
	SetDlgItemTextA(hWnd, IDC_CONFIG_FILE, sc.fileName.c_str());

	RefreshCopies(hWnd);
}

void ReadIni(HWND hWnd)
{
	OPENFILENAMEA ofn;
	char szFile[260];

	strcpy(szFile, sc.fileName.c_str());

	ZeroMemory(&ofn, sizeof(ofn));
	ofn.lStructSize = sizeof(ofn);
	ofn.hwndOwner = hWnd;
	ofn.lpstrFile = szFile;

	ofn.nMaxFile = sizeof(szFile);
	ofn.lpstrFilter = "All\0*.*\0ini файлы\0*.ini\0";
	ofn.nFilterIndex = 1;
	ofn.lpstrFileTitle = NULL;
	ofn.nMaxFileTitle = 0;
	ofn.lpstrInitialDir = NULL;
	ofn.Flags = OFN_PATHMUSTEXIST | OFN_FILEMUSTEXIST;

	if (GetOpenFileNameA(&ofn) == TRUE)
	{
		sc.Read(ofn.lpstrFile);
		Init(hWnd);
	}
}

void PutSelectedFile(HWND hWnd)
{
	HWND hList = GetDlgItem(hWnd, IDC_ITEMS);
	int cs = SendMessage(hList, LB_GETCURSEL, 0, 0);
	if (cs >= 0)
	{
		std::string* val = (std::string*)SendMessage(hList, LB_GETITEMDATA, cs, 0);
		if (val != NULL)
			backupFile = (*val);
	}
}

static INT_PTR CALLBACK BackupRestoreDialog(HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
	switch (uMsg)
	{
	case WM_INITDIALOG:
	{
		HICON hIcon = (HICON)LoadImage(hInst, MAKEINTRESOURCE(IDI_ICON1), IMAGE_ICON,
			GetSystemMetrics(SM_CXSMICON),
			GetSystemMetrics(SM_CYSMICON),
			0);
		if (hIcon)
		{
			SendMessage(hWnd, WM_SETICON, ICON_SMALL, (LPARAM)hIcon);
		}
		Init(hWnd);
		break;
	}
	case WM_COMMAND:
		switch (LOWORD(wParam))
		{
		case IDC_ITEMS:
			if (HIWORD(wParam) == LBN_DBLCLK)
			{
				PutSelectedFile(hWnd);
				EndDialog(hWnd, LOWORD(IDOK));
			}
			break;
		case IDC_BROWSE_FOLDER:
			if (SetFolder(hWnd, IDC_EXCHANGE_FOLDER, IDS_BROWSE_FOLDER))
				RefreshCopies(hWnd);
			break;
		case IDC_SET_CONFIG_FILE:
			ReadIni(hWnd);
			break;
		case IDC_REFRESH:
			RefreshCopies(hWnd);
			break;
		case IDOK:
		{
			PutSelectedFile(hWnd);
			EndDialog(hWnd, LOWORD(wParam));
			break;
		}
		case IDCANCEL:
			EndDialog(hWnd, LOWORD(wParam));
			break;
		}
		break;

	case WM_DESTROY:
		FreeList(GetDlgItem(hWnd, IDC_ITEMS));
		break;
	}
	return FALSE;
}

// Global Variables:
int APIENTRY _tWinMain(_In_ HINSTANCE hInstance,
                     _In_opt_ HINSTANCE hPrevInstance,
                     _In_ LPTSTR    lpCmdLine,
                     _In_ int       nCmdShow)
{
	UNREFERENCED_PARAMETER(hPrevInstance);
	UNREFERENCED_PARAMETER(lpCmdLine);

	hInst = hInstance;

	sc.Read("GRServer.ini");

	if (DialogBox(hInstance, MAKEINTRESOURCE(IDD_RESTORE_BASE), NULL, (DLGPROC)BackupRestoreDialog) == IDOK)
	{
		if (!backupFile.empty())
		{
			std::string text;
			FILE *src = fopen(backupFile.c_str(), "rb");
			if (src == NULL)
			{
				text = "Не могу открыть файл ";
				text += backupFile.c_str();
				MessageBoxA(NULL, text.c_str(), "Ошибка", MB_ICONSTOP);
				return 1;
			}
			FILE *dst;
			errno_t res = fopen_s(&dst, sc.serverBase.c_str(), "wb");
			if (res != 0)
			{
				fclose(src);
				text = "Не могу открыть файл ";
				text += sc.serverBase.c_str();
				MessageBoxA(NULL, text.c_str(), "Ошибка", MB_ICONSTOP);
				return 1;
			}

			BkcpHeader header;
			fread(&header, sizeof(header), 1, src);

			int cb = 1024 * 1024;
			char* buf = (char*)malloc(cb);
			while (true)
			{
				int rc = fread(buf, 1, cb, src);
				if (rc <= 0)
					break;

				fwrite(buf, 1, rc, dst);
			}

			fclose(dst);
			fclose(src);
			MessageBoxA(NULL, "База восстановлена", "Иняормация", MB_ICONINFORMATION);
		}
	}
	return 0;
}

