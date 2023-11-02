// BinaryField.cpp: реализация CBinaryField

#include "stdafx.h"
#include "BinaryField.h"


// CBinaryField


STDMETHODIMP CBinaryField::Write(BSTR name)
{
	IBinary *b = data->binary;
   DWORD size = b->Size();
   if( size > 0 )
   {
      FILE *f = _wfopen(name, L"wb");
      if( f != NULL )
      {
         fwrite(b->Bytes(), sizeof(BYTE), size, f);
         fclose(f);
      }
   }
   return S_OK;
}

STDMETHODIMP CBinaryField::get_Size(double* pVal)
{
	IBinary *b = data->binary;
	*pVal = b->Size();
   return S_OK;
}

STDMETHODIMP CBinaryField::SetFrom(IDispatch* pVal)
{
	IBinaryField *bf;
	if (pVal->QueryInterface(IID_IBinaryField, (void**)&bf) == S_OK)
	{
		IBinary *data = ((CBinaryField*)bf)->data->binary;
		if (data != NULL && data->Size() > 0)
		{
			Binary *b = new Binary();
			BYTE *pb = b->Alloc(data->Size());
			memcpy(pb, data->Bytes(), data->Size());
			this->data->binary->Assign(b);
		}
		bf->Release();
	}
	return S_OK;
}

STDMETHODIMP CBinaryField::Read(BSTR name)
{
   HRESULT res = S_FALSE;
   HANDLE h = CreateFile(name, GENERIC_READ, FILE_SHARE_WRITE|FILE_SHARE_READ, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
   if( h && h != INVALID_HANDLE_VALUE )
   {
      DWORD loSize, value;
      loSize = GetFileSize(h, &value);
      if( value == 0 && loSize > 0 )
      {
         Binary *b = new Binary();
         BYTE *pb = b->Alloc(loSize);
         value = 0;
         ReadFile(h, pb, loSize, &value, NULL);
         if( value == loSize )
         {
            data->binary->Assign(b);
            res = S_OK;
         }
      }
      CloseHandle(h);      
   }
   return res;
}
