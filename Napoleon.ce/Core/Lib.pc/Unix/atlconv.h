#ifndef ATL_CONV_H

#define USES_CONVERSION \
   const wchar_t* _lpw __attribute__ ((unused)); \
   const char* _lpa __attribute__ ((unused)); \
   int _srcb, _destb;

const int CP_OEMCP = 1;
const int CP_ACP = 0;
const int CP_UTF8 = 2;

#define A2W(lpa) \
( ((_lpa = lpa) == NULL) ? NULL : \
  (_srcb = (int)strlen(_lpa) + 1, _destb = _srcb * sizeof(wchar_t), \
   (_lpw = (const wchar_t*)ConvHelper(_lpa, (char*)alloca(_destb), _srcb, _destb, "CP1251", UTF_CP))) \
)

#define A2W_CP(lpa, cp) \
( ((_lpa = lpa) == NULL) ? NULL : \
  (_srcb = strlen(_lpa) + 1, _destb = _srcb * sizeof(wchar_t), \
   (_lpw = (const wchar_t*)ConvHelper(_lpa, (char*)alloca(_destb), _srcb, _destb, ((cp==CP_ACP) ? "CP1251" : (cp==CP_OEMCP) ? "CP866" : "UTF8"), UTF_CP))) \
)

#define W2A(lpw) \
( ((_lpw = lpw) == NULL) ? NULL : \
  (_srcb = (int)wcslen(_lpw) + 1, _destb = _srcb, \
   (_lpa = ConvHelper((const char*)_lpw, (char*)alloca(_destb), _srcb * sizeof(wchar_t), _destb, UTF_CP, "CP1251"))) \
)

#define W2A_CP(lpw, cp) \
( ((_lpw = lpw) == NULL) ? NULL : \
  (_srcb = (int)wcslen(_lpw) + 1, _destb = _srcb, \
   (_lpa = ConvHelper((const char*)_lpw, (char*)alloca(_destb), _srcb * sizeof(wchar_t), _destb, UTF_CP, ((cp==CP_ACP) ? "CP1251" : (cp==CP_OEMCP) ? "CP866" : "UTF8")))) \
)

#endif
