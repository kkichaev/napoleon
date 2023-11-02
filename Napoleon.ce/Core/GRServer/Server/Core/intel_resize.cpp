/*
* Copyright (C), 2009 - 2018, Денис Мосягин
*
* Resize Image
*
* ert   22/10/2018   creating
*/
#include "stdafx.h"

#include "binary.h"
#include "srvutility.h"
#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

#include <member.h>
#include "image_convert.h"

#include <ipp.h>

//#include "lancir.h"
//#include "avir.h"

#include <jpeglib.h>
#include <png.h>
#include <setjmp.h>

using namespace GRServer;

extern "C" int omp_get_max_threads()
{
	return 1;
}

extern "C" int omp_get_num_procs()
{
	return 1;
}

template<class T> inline T alignValue(T iValue, T iAlignValue)
{
	return (T)((iValue + (iAlignValue - 1)) & ~(iAlignValue - 1));
}

struct Size
{
	Size()
	{
		width = 0;
		height = 0;
	}
	Size(long long _width, long long _height)
	{
		width = _width;
		height = _height;
	}

	long long width;
	long long height;
};

static const char jpegSignature[] = "\xFF\xD8\xFF";
static const char pngSignature[] = "\x89\x50\x4E\x47\x0D\x0A\x1A\x0A";

bool IsJPEG(const BYTE* pb)
{
	return (memcmp(pb, jpegSignature, sizeof(jpegSignature) - 1) == 0);
}

bool IsPNG(const BYTE* pb)
{
	return (memcmp(pb, pngSignature, sizeof(pngSignature) - 1) == 0);
}

struct Point
{
	Point()
	{
		x = y = 0;
	}
	Point(long long _x, long long _y)
	{
		x = _x;
		y = _y;
	}

	long long x;
	long long y;
}; struct Rect
{
	Rect()
	{
		x = y = 0;
		width = height = 0;
	}
	Rect(long long _width, long long _height)
	{
		x = 0;
		y = 0;
		width = _width;
		height = _height;
	}
	Rect(long long _x, long long _y, long long _width, long long _height)
	{
		x = _x;
		y = _y;
		width = _width;
		height = _height;
	}

	long long x;
	long long y;
	long long width;
	long long height;
};

enum ColorFormat
{
	CF_UNKNOWN = 0,
	CF_GRAY,
	CF_RGB,
	CF_BGR,
	CF_RGBA,
	CF_BGRA
};
enum SampleFormat
{
	ST_UNKNOWN = 0,
	ST_8U,
	ST_8S,
	ST_16U,
	ST_16S,
	ST_32U,
	ST_32S,
	ST_32F,
	ST_64F
};

struct BorderSize
{
	BorderSize()
	{
		left = top = right = bottom = 0;
	}
	BorderSize(long long _size)
	{
		left = top = right = bottom = _size;
	}
	BorderSize(long long _left, long long _top, long long _right, long long _bottom)
	{
		left = _left;
		top = _top;
		right = _right;
		bottom = _bottom;
	}

	long long left;
	long long top;
	long long right;
	long long bottom;
};

class SharedObject
{
public:
	SharedObject();
	~SharedObject();
	SharedObject(const SharedObject& right);

	SharedObject& operator = (const SharedObject& right);

	void  attach(void* obj);
	void* deattach();

	inline void* get() { return pInst->pObj; }

	inline int getCounter() { return pInst->counter; }

private:
	struct ObjContainer
	{
		void        *pObj;
		volatile int counter;
	};

	ObjContainer *pInst;
};


class Image
{
public:
	Image();
	Image(Size size, unsigned int samples = 1, SampleFormat sampleFormat = ST_8U);
	Image(Size size, ColorFormat color, SampleFormat sampleFormat = ST_8U);
	virtual ~Image();


	size_t         m_imageSize;         // actual size of image buffer relative to m_pPointer
	size_t         m_bufferSize;        // full size of buffer
	unsigned int   m_bufferAlignment;   // buffer allocation alignment
	size_t         m_step;              // size of buffer row in bytes
	unsigned int   m_stepAlignment;     // alignment of step value
	Size           m_size;              // Image size in pixels
	ColorFormat    m_color;             // samples pattern
	unsigned int   m_samples;           // amount of samples in pixel
	SampleFormat   m_sampleFormat;      // data format of a sample
	unsigned int   m_sampleSize;        // size of one sample
	BorderSize     m_border;            // size of border around the current ROI

	ImageFormat    m_srcFormat;

	inline void* ptr(long long y = 0, long long x = 0, unsigned int c = 0, int pattern = 0)
	{
		return (this->m_pPointer + y*this->m_step + x*this->m_sampleSize*this->m_samples + c*this->m_sampleSize);
	}

	bool Compare(const Image &ref);
	bool operator==(const Image& ref) { return Compare(ref); }
	bool operator!=(const Image& ref) { return !Compare(ref); }

	Image(const Image&);
	Image& operator=(const Image&);
	
	bool Release();
	bool Reset();

	bool Alloc() { return Alloc(m_size, m_samples, m_sampleFormat); }
	bool Alloc(Size size, unsigned int samples, SampleFormat sampleFormat);

	inline bool IsInitialized()
	{
		if (!m_pPointer || !m_size.width || !m_size.height || !m_samples || !m_step || !m_sampleSize || m_sampleFormat == ST_UNKNOWN)
			return false;
		return true;
	}

	bool Read(IBinary* src, size_t maxSize);
	
	IBinary* MakeBinary();

	IBinary* MakeJPEG();
	IBinary* MakePNG();

	bool ReadJPEG(BYTE* src, size_t size, size_t maxSize);
	bool ReadPNG(BYTE* src, size_t size, size_t maxSize);
private:
	SharedObject   m_buffer;
	unsigned char *m_pPointer;         // pointer to ROI start point

};

unsigned int GetSampleSize(SampleFormat sampleFormat)
{
	if (sampleFormat == ST_8U || sampleFormat == ST_8S)
		return 1;
	else if (sampleFormat == ST_16U || sampleFormat == ST_16S)
		return 2;
	else if (sampleFormat == ST_32U || sampleFormat == ST_32S || sampleFormat == ST_32F)
		return 4;
	else if (sampleFormat == ST_64F)
		return 8;

	return 0;
}

unsigned int GetSamplesNum(ColorFormat colorFormat)
{
	if (colorFormat == CF_GRAY)
		return 1;
	else if (colorFormat == CF_RGB || colorFormat == CF_BGR)
		return 3;
	else if (colorFormat == CF_RGBA || colorFormat == CF_BGRA)
		return 4;

	return 0;
}

Image::Image()
{
	m_bufferAlignment = 64;
	m_stepAlignment = 64;
	Reset();
}

Image::Image(Size size, ColorFormat color, SampleFormat sampleFormat)
{
	m_bufferAlignment = 64;
	m_stepAlignment = 64;

	Reset();

	m_size = size;
	m_sampleFormat = sampleFormat;
	m_sampleSize = GetSampleSize(sampleFormat);
	m_color = color;
	m_samples = GetSamplesNum(m_color);
}

Image::Image(Size size, unsigned int samples, SampleFormat sampleFormat)
{
	m_bufferAlignment = 64;
	m_stepAlignment = 64;

	Reset();

	m_size = size;
	m_sampleFormat = sampleFormat;
	m_sampleSize = GetSampleSize(sampleFormat);
	m_samples = samples;
}

Image::~Image()
{
	Release();
}

bool Image::Reset()
{
	Release();

	m_pPointer = 0;
	m_imageSize = 0;
	m_bufferSize = 0;
	m_step = 0;
	m_samples = 0;
	m_size.width = 0;
	m_size.height = 0;
	m_sampleSize = 0;
	m_color = CF_UNKNOWN;
	m_sampleFormat = ST_UNKNOWN;
	m_border = BorderSize();

	m_srcFormat = IF_UNKNOWN;

	return true;
}
bool Image::Compare(const Image &image)
{
	if (m_sampleSize != image.m_sampleSize ||
		m_sampleFormat != image.m_sampleFormat ||
		m_size.width != image.m_size.width ||
		m_size.height != image.m_size.height ||
		m_samples != image.m_samples)
		return false;

	if (m_color != image.m_color && m_color != CF_UNKNOWN && image.m_color != CF_UNKNOWN)
		return false;

	return true;
}

bool Image::Release()
{
	void *pBuffer = m_buffer.deattach();
	if (pBuffer)
		_aligned_free(pBuffer);

	m_pPointer = NULL;
	m_imageSize = 0;
	m_bufferSize = 0;
	m_border = BorderSize();

	return true;
}

Image& Image::operator=(const Image &image)
{
	Release();

	m_buffer = image.m_buffer;
	m_pPointer = image.m_pPointer;
	m_imageSize = image.m_imageSize;
	m_bufferSize = image.m_bufferSize;
	m_step = image.m_step;
	m_sampleFormat = image.m_sampleFormat;
	m_sampleSize = image.m_sampleSize;
	m_samples = image.m_samples;
	m_size.width = image.m_size.width;
	m_size.height = image.m_size.height;
	m_color = image.m_color;
	m_border = image.m_border;

	m_srcFormat = image.m_srcFormat;

	if (m_pPointer)
	{
		m_stepAlignment = image.m_stepAlignment;
		m_bufferAlignment = image.m_bufferAlignment;
	}

	return *this;
}

bool Image::Alloc(Size size, unsigned int samples, SampleFormat sampleFormat)
{
	if (size.width < 0 || size.height < 0 || !GetSampleSize(sampleFormat) || !m_stepAlignment)
		return false;

	if (m_buffer.get() && m_buffer.getCounter() == 1 &&
		m_size.width == size.width && m_size.height == size.height && m_samples == samples && m_sampleFormat == sampleFormat)
		return true;

	Release();

	m_samples = samples;
	m_sampleSize = GetSampleSize(sampleFormat);
	m_size = size;
	m_sampleFormat = sampleFormat;

	m_step = (size_t)((m_size.width + m_border.left + m_border.right)*m_samples*m_sampleSize);
	m_step = alignValue<size_t>(m_step, m_stepAlignment);

	m_bufferSize = (size_t)(m_step*(m_size.height + m_border.top + m_border.bottom));
	if (!m_bufferSize)
		return true;

	m_buffer.attach((unsigned char*)_aligned_malloc(m_bufferSize, m_bufferAlignment));
	if (!m_buffer.get())
		return false;

	m_pPointer = (unsigned char*)m_buffer.get();
	m_pPointer = (unsigned char*)ptr(m_border.top, m_border.left);
	m_imageSize = (size_t)(m_step*m_size.height);

	return true;
}

bool Image::Read(IBinary* src, size_t maxSize)
{
	const BYTE* pb = src->Bytes();
	if (IsJPEG(pb))
	{
		return ReadJPEG((BYTE*)pb, src->Size(), maxSize);
	}
	if (IsPNG(pb))
	{
		return ReadPNG((BYTE*)pb, src->Size(), maxSize);
	}

	return false;
}

bool Image::ReadPNG(BYTE* src, size_t size, size_t maxSize)
{
	m_srcFormat = IF_UNKNOWN;
	png_image image; /* The control structure used by libpng */

	/* Initialize the 'png_image' structure. */
	memset(&image, 0, (sizeof image));
	image.version = PNG_IMAGE_VERSION;

	png_image_begin_read_from_memory(&image, src, size);
	image.format = PNG_FORMAT_RGBA;


	m_size.width = image.width;
	m_size.height = image.height;

	m_sampleFormat = (PNG_IMAGE_PIXEL_COMPONENT_SIZE(image.format) == 1) ? ST_8U : ST_16U;
	m_color = CF_RGBA;
	m_samples = PNG_IMAGE_PIXEL_CHANNELS(image.format);
	m_sampleSize = GetSampleSize(m_sampleFormat);

	if (!Alloc())
	{
		return false;
	}
	png_image_finish_read(&image, NULL/*background*/, ptr(0), 0/*row_stride*/, NULL/*colormap*/);

	m_srcFormat = IF_PNG;
	return true;
}

struct jpegErrorManager {
	/* "public" fields */
	struct jpeg_error_mgr pub;
	/* for return to caller */
	jmp_buf setjmp_buffer;
};
void jpegErrorExit(j_common_ptr cinfo)
{
	/* cinfo->err actually points to a jpegErrorManager struct */
	jpegErrorManager* myerr = (jpegErrorManager*)cinfo->err;
	/* note : *(cinfo->err) is now equivalent to myerr->pub */

	/* output_message is a method to print an error message */
	/*(* (cinfo->err->output_message) ) (cinfo);*/

	/* Create the message */
	//(*(cinfo->err->format_message)) (cinfo, jpegLastErrorMsg);

	/* Jump to the setjmp point */
	longjmp(myerr->setjmp_buffer, 1);

}

bool Image::ReadJPEG(BYTE* src, size_t size, size_t maxSize)
{
	m_srcFormat = IF_UNKNOWN;

	struct jpeg_decompress_struct cinfo;
	jpegErrorManager jerr;
	cinfo.err = jpeg_std_error(&jerr.pub);
	jerr.pub.error_exit = jpegErrorExit;

	if (setjmp(jerr.setjmp_buffer)) {
		jpeg_destroy_decompress(&cinfo);
		return false;
	}

	jpeg_create_decompress(&cinfo);

	jpeg_mem_src(&cinfo, src, (unsigned long)size);
	int rc = jpeg_read_header(&cinfo, TRUE);
	if (rc != 1)
	{
		jpeg_destroy_decompress(&cinfo);
		return false;
	}

	int scale = 1;
	size_t wdh = max(cinfo.image_width, cinfo.image_height);
	while (true)
	{
		size_t newW = wdh / 2;
		if (newW < maxSize)
			break;
		if (scale == 8)
			break;
		scale *= 2;
		wdh = newW;
	}
	cinfo.scale_num = 1;
	cinfo.scale_denom = scale;

	jpeg_start_decompress(&cinfo);

	m_size.width = cinfo.output_width;
	m_size.height = cinfo.output_height;
	m_sampleFormat = ST_8U;
	m_color = CF_RGB;
	m_samples = cinfo.output_components;
	m_sampleSize = GetSampleSize(m_sampleFormat);

	if (!Alloc())
	{
		return false;
	}

	while (cinfo.output_scanline < cinfo.output_height) {
		unsigned char *pb[1];
		pb[0]= (unsigned char *)ptr(cinfo.output_scanline);
		jpeg_read_scanlines(&cinfo, pb, 1);
	}

	jpeg_finish_decompress(&cinfo);
	jpeg_destroy_decompress(&cinfo);

	m_srcFormat = IF_JPEG;
	return true;
}

void init_buffer(jpeg_compress_struct* cinfo) {}
boolean empty_buffer(jpeg_compress_struct* cinfo) {
	return TRUE;
}
void term_buffer(jpeg_compress_struct* cinfo) {}

IBinary* Image::MakeBinary()
{
	if (m_srcFormat == IF_JPEG)
		return MakeJPEG();

	if (m_srcFormat == IF_PNG)
		return MakePNG();
	return NULL;
}

typedef unsigned char ui8;
#define ASSERT_EX(cond, error_message) do { if (!(cond)) { std::cerr << error_message; exit(1);} } while(0)

static void PngWriteCallback(png_structp  png_ptr, png_bytep data, png_size_t length) {
	std::vector<ui8> *p = (std::vector<ui8>*)png_get_io_ptr(png_ptr);
	p->insert(p->end(), data, data + length);
}

IBinary* Image::MakePNG()
{
	std::vector<ui8> out;
	png_structp p = png_create_write_struct(PNG_LIBPNG_VER_STRING, NULL, NULL, NULL);

	png_infop info_ptr = png_create_info_struct(p);
	setjmp(png_jmpbuf(p));

	png_set_IHDR(p, info_ptr, (png_uint_32)m_size.width, (png_uint_32)m_size.height, 8,
		PNG_COLOR_TYPE_RGBA,
		PNG_INTERLACE_NONE,
		PNG_COMPRESSION_TYPE_DEFAULT,
		PNG_FILTER_TYPE_DEFAULT);
	//png_set_compression_level(p, 1);

	std::vector<ui8*> rows((size_t)m_size.height);
	for (size_t y = 0; y < (size_t)m_size.height; ++y)
		rows[y] = (ui8*)ptr(y);
	png_set_rows(p, info_ptr, &rows[0]);
	png_set_write_fn(p, &out, PngWriteCallback, NULL);
	png_write_png(p, info_ptr, PNG_TRANSFORM_IDENTITY, NULL);

	png_destroy_write_struct(&p, &info_ptr);

	Binary *wrb = new Binary();
	memcpy(wrb->Alloc((DWORD)out.size()), &(*out.begin()), out.size());
	MemoryBinary *mb = new MemoryBinary(wrb);

	return mb;
}

IBinary* Image::MakeJPEG()
{
	struct jpeg_destination_mgr dmgr;

	size_t size = (size_t)(m_size.width * m_size.height * 3);
	JOCTET * out_buffer = new JOCTET[size];

	/* here is the magic */
	dmgr.init_destination = init_buffer;
	dmgr.empty_output_buffer = empty_buffer;
	dmgr.term_destination = term_buffer;
	dmgr.next_output_byte = out_buffer;
	dmgr.free_in_buffer = size;

	struct jpeg_compress_struct cinfo;
	jpegErrorManager jerr;
	cinfo.err = jpeg_std_error(&jerr.pub);
	jerr.pub.error_exit = jpegErrorExit;

	if (setjmp(jerr.setjmp_buffer)) {
		jpeg_destroy_compress(&cinfo);
		return false;
	}

	jpeg_create_compress(&cinfo);

	/* make sure we tell it about our manager */
	cinfo.dest = &dmgr;

	cinfo.image_width = (JDIMENSION)m_size.width;
	cinfo.image_height = (JDIMENSION)m_size.height;
	cinfo.input_components = 3;
	cinfo.in_color_space = JCS_RGB;

	jpeg_set_defaults(&cinfo);
	jpeg_set_quality(&cinfo, 75, true);
	jpeg_start_compress(&cinfo, true);

	while (cinfo.next_scanline < cinfo.image_height) {
		JSAMPROW rp = (JSAMPROW)ptr(cinfo.next_scanline);
		jpeg_write_scanlines(&cinfo, &rp, 1);
	}
	jpeg_finish_compress(&cinfo);
	jpeg_destroy_compress(&cinfo);

	size_t bytes = cinfo.dest->next_output_byte - out_buffer;

	Binary *wrb = new Binary();
	memcpy(wrb->Alloc((DWORD)bytes), out_buffer, bytes);
	MemoryBinary *mb = new MemoryBinary(wrb);

	delete out_buffer;
	return mb;
}

class Resize
{
public:
	Resize()
	{
		//m_iThreads = 0;

		m_interpolation = ippLinear;
		m_pSpec = 0;
		m_pInitBuffer = 0;

		m_fBVal = 1;
		m_fCVal = 0;
		m_iLobes = 3;
	}

	virtual ~Resize()
	{
		Close();
	}

	void Close()
	{
		if (m_pSpec)
		{
			ippsFree(m_pSpec);
			m_pSpec = 0;
		}

		if (m_pInitBuffer)
		{
			ippsFree(m_pInitBuffer);
			m_pInitBuffer = 0;
		}
	}

	virtual bool Init(Image *pSrcImage, Image *pDstImage)
	{
		IppStatus       ippSts;
		IppiBorderSize  borderSize;
		int             iSpecSize = 0;
		int             iInitSize = 0;

		if (!pSrcImage || !pSrcImage->ptr() || !pDstImage)
			return false;

		if (pSrcImage->m_samples != 1 && pSrcImage->m_samples != 3 && pSrcImage->m_samples != 4)
			return false;

		Close();

		IppiSize srcSize = { static_cast<int>(pSrcImage->m_size.width), static_cast<int>(pSrcImage->m_size.height) };
		IppiSize dstSize = { static_cast<int>(pDstImage->m_size.width), static_cast<int>(pDstImage->m_size.height) };

		// Get sizes for internal and initialization buffers
		ippSts = ippiResizeGetSize_8u(srcSize, dstSize, m_interpolation, 0, &iSpecSize, &iInitSize);
		//CHECK_STATUS_PRINT_AC(ippSts, "ippiResizeGetSize_8u()", ippGetStatusString(ippSts), return STS_ERR_FAILED);

		// allocate internal buffer
		
		m_pSpec = (IppiResizeSpec_32f*)ippsMalloc_8u(iSpecSize);
		if (!m_pSpec)
		{
			//PRINT_MESSAGE("Cannot allocate memory for resize spec");
			return false;
		}

		// allocate initialization buffer
		if (iInitSize)
		{
			m_pInitBuffer = ippsMalloc_8u(iInitSize);
			if (!m_pInitBuffer)
			{
				//PRINT_MESSAGE("Cannot allocate memory for resize init buffer");
				return false;
			}
		}

		// init ipp resizer
		if (m_interpolation == ippNearest)
		{
			ippSts = ippiResizeNearestInit_8u(srcSize, dstSize, m_pSpec);
			//CHECK_STATUS_PRINT_AC(ippSts, "ippiResizeNearestInit_8u()", ippGetStatusString(ippSts), return STS_ERR_FAILED);
		}
		else if (m_interpolation == ippLinear)
		{
			ippSts = ippiResizeLinearInit_8u(srcSize, dstSize, m_pSpec);
			//CHECK_STATUS_PRINT_AC(ippSts, "ippiResizeLinearInit_8u()", ippGetStatusString(ippSts), return STS_ERR_FAILED);
		}
		//else if (m_interpolation == ippCubic)
		//{
		//	ippSts = ippiResizeCubicInit_8u(srcSize, dstSize, m_fBVal, m_fCVal, m_pSpec, m_pInitBuffer);
		//	//CHECK_STATUS_PRINT_AC(ippSts, "ippiResizeCubicInit_8u()", ippGetStatusString(ippSts), return STS_ERR_FAILED);
		//}
		//else if (m_interpolation == ippLanczos)
		//{
		//	ippSts = ippiResizeLanczosInit_8u(srcSize, dstSize, m_iLobes, m_pSpec, m_pInitBuffer);
		//	//CHECK_STATUS_PRINT_AC(ippSts, "ippiResizeLanczosInit_8u()", ippGetStatusString(ippSts), return STS_ERR_FAILED);
		//}

		ippSts = ippiResizeGetBorderSize_8u(m_pSpec, &borderSize);
		//CHECK_STATUS_PRINT_AC(ippSts, "ippiResizeGetBorderSize_8u()", ippGetStatusString(ippSts), return STS_ERR_FAILED);

		m_templ = *pSrcImage;

		return true;
	}

	bool ResizeBlock(Image *pSrcImage, Image *pDstImage, Rect roi, IppiBorderType border, unsigned char *pExtBuffer = 0)
	{
		IppStatus   ippSts;
		IppiPoint   dstRoiOffset = { (int)roi.x, (int)roi.y };
		IppiSize    dstRoiSize = { (int)roi.width, (int)roi.height };
		IppiPoint   srcRoiOffset;
		IppiSize    srcRoiSize;

		unsigned char *pSrcPtr = 0;
		unsigned char *pDstPtr = 0;
		unsigned char *pBuffer = 0;
		int            iBufferSize = 0;

		if (!pSrcImage || !pDstImage)
			return false;

		if (!m_pSpec)
			return false;

		// Zero size mean full size
		if (!dstRoiSize.width)
			dstRoiSize.width = (int)pDstImage->m_size.width;
		if (!dstRoiSize.height)
			dstRoiSize.height = (int)pDstImage->m_size.height;

		if (m_templ != *pSrcImage)
		{
			if (!Init(pSrcImage, pDstImage))
				return false;
			//CHECK_STATUS_PRINT_RS(status, "Resize::Init()", GetBaseStatusString(status));
		}

		// get src ROI from dst ROI
		ippSts = ippiResizeGetSrcRoi_8u(m_pSpec, dstRoiOffset, dstRoiSize, &srcRoiOffset, &srcRoiSize);
		//CHECK_STATUS_PRINT_AC(ippSts, "ippiResizeGetSrcRoi_8u()", ippGetStatusString(ippSts), return STS_ERR_FAILED);

		// adjust input and output buffers to current ROI
		pSrcPtr = (unsigned char*)pSrcImage->ptr(srcRoiOffset.y, srcRoiOffset.x);
		pDstPtr = (unsigned char*)pDstImage->ptr(dstRoiOffset.y, dstRoiOffset.x);

		if (!pExtBuffer)
		{
			ippSts = ippiResizeGetBufferSize_8u(m_pSpec, dstRoiSize, pSrcImage->m_samples, &iBufferSize);
			//CHECK_STATUS_PRINT_AC(ippSts, "ippiResizeGetBufferSize_8u()", ippGetStatusString(ippSts), return STS_ERR_FAILED);

			pBuffer = ippsMalloc_8u(iBufferSize);
			if (!pBuffer)
			{
				//PRINT_MESSAGE("Cannot allocate memory for resize buffer");
				return false;
			}
		}
		else
			pBuffer = pExtBuffer;

		// perform resize
		if (m_interpolation == ippNearest)
		{
			if (pSrcImage->m_samples == 1)
				ippSts = ippiResizeNearest_8u_C1R(pSrcPtr, (int)pSrcImage->m_step, pDstPtr, (int)pDstImage->m_step, dstRoiOffset, dstRoiSize, m_pSpec, pBuffer);
			else if (pSrcImage->m_samples == 3)
				ippSts = ippiResizeNearest_8u_C3R(pSrcPtr, (int)pSrcImage->m_step, pDstPtr, (int)pDstImage->m_step, dstRoiOffset, dstRoiSize, m_pSpec, pBuffer);
			else if (pSrcImage->m_samples == 4)
				ippSts = ippiResizeNearest_8u_C4R(pSrcPtr, (int)pSrcImage->m_step, pDstPtr, (int)pDstImage->m_step, dstRoiOffset, dstRoiSize, m_pSpec, pBuffer);
		}
		else if (m_interpolation == ippLinear)
		{
			if (pSrcImage->m_samples == 1)
				ippSts = ippiResizeLinear_8u_C1R(pSrcPtr, (int)pSrcImage->m_step, pDstPtr, (int)pDstImage->m_step, dstRoiOffset, dstRoiSize, border, 0, m_pSpec, pBuffer);
			else if (pSrcImage->m_samples == 3)
				ippSts = ippiResizeLinear_8u_C3R(pSrcPtr, (int)pSrcImage->m_step, pDstPtr, (int)pDstImage->m_step, dstRoiOffset, dstRoiSize, border, 0, m_pSpec, pBuffer);
			else if (pSrcImage->m_samples == 4)
				ippSts = ippiResizeLinear_8u_C4R(pSrcPtr, (int)pSrcImage->m_step, pDstPtr, (int)pDstImage->m_step, dstRoiOffset, dstRoiSize, border, 0, m_pSpec, pBuffer);
		}
		//else if (m_interpolation == ippCubic)
		//{
		//	if (pSrcImage->m_samples == 1)
		//		ippSts = ippiResizeCubic_8u_C1R(pSrcPtr, (int)pSrcImage->m_step, pDstPtr, (int)pDstImage->m_step, dstRoiOffset, dstRoiSize, border, 0, m_pSpec, pBuffer);
		//	else if (pSrcImage->m_samples == 3)
		//		ippSts = ippiResizeCubic_8u_C3R(pSrcPtr, (int)pSrcImage->m_step, pDstPtr, (int)pDstImage->m_step, dstRoiOffset, dstRoiSize, border, 0, m_pSpec, pBuffer);
		//	else if (pSrcImage->m_samples == 4)
		//		ippSts = ippiResizeCubic_8u_C4R(pSrcPtr, (int)pSrcImage->m_step, pDstPtr, (int)pDstImage->m_step, dstRoiOffset, dstRoiSize, border, 0, m_pSpec, pBuffer);
		//}
		//else if (m_interpolation == ippLanczos)
		//{
		//	if (pSrcImage->m_samples == 1)
		//		ippSts = ippiResizeLanczos_8u_C1R(pSrcPtr, (int)pSrcImage->m_step, pDstPtr, (int)pDstImage->m_step, dstRoiOffset, dstRoiSize, border, 0, m_pSpec, pBuffer);
		//	else if (pSrcImage->m_samples == 3)
		//		ippSts = ippiResizeLanczos_8u_C3R(pSrcPtr, (int)pSrcImage->m_step, pDstPtr, (int)pDstImage->m_step, dstRoiOffset, dstRoiSize, border, 0, m_pSpec, pBuffer);
		//	else if (pSrcImage->m_samples == 4)
		//		ippSts = ippiResizeLanczos_8u_C4R(pSrcPtr, (int)pSrcImage->m_step, pDstPtr, (int)pDstImage->m_step, dstRoiOffset, dstRoiSize, border, 0, m_pSpec, pBuffer);
		//}
		//CHECK_STATUS_PRINT_AC(ippSts, "ippiResizeX_8u_CXR()", ippGetStatusString(ippSts), return STS_ERR_FAILED);

		if (!pExtBuffer)
			ippsFree(pBuffer);

		return true;
	}

	virtual bool ResizeImage(Image *pSrcImage, Image *pDstImage)
	{
		if (!pSrcImage || !pDstImage)
			return false;

		Rect roi(pDstImage->m_size.width, pDstImage->m_size.height);

		return ResizeBlock(pSrcImage, pDstImage, roi, ippBorderRepl);
	}

public:
	//unsigned int m_iThreads;

	IppiInterpolationType m_interpolation;
	float        m_fBVal;
	float        m_fCVal;
	unsigned int m_iLobes;

protected:
	Image m_templ;

	IppiResizeSpec_32f *m_pSpec;
	unsigned char      *m_pInitBuffer;
};

/*
// Class to safely share arbitrary buffer between several objects
*/
SharedObject::SharedObject()
{
	pInst = new ObjContainer;
	pInst->counter = 1;
	pInst->pObj = NULL;
}
SharedObject::~SharedObject()
{
	pInst->counter--;
	if (pInst->counter == 0)
		delete pInst;
	pInst = NULL;
}
SharedObject::SharedObject(const SharedObject& right)
{
	pInst = right.pInst;
	pInst->counter++;
}
SharedObject& SharedObject::operator = (const SharedObject& right)
{
	right.pInst->counter++;
	pInst->counter--;
	if (pInst->counter == 0)
		delete pInst;
	pInst = right.pInst;
	return *this;
}

void SharedObject::attach(void* obj)
{
	if (pInst->counter > 1)
	{
		pInst->counter--;
		pInst = new ObjContainer();
		pInst->counter = 1;
		pInst->pObj = NULL;
	}
	//assert(pInst->pObj == NULL);
	pInst->pObj = obj;
}
void* SharedObject::deattach()
{
	if (pInst->counter > 1)
	{
		pInst->counter--;
		pInst = new ObjContainer();
		pInst->counter = 1;
		pInst->pObj = NULL;
		return NULL;
	}
	else
	{
		void* tmp = pInst->pObj;
		pInst->pObj = NULL;
		return tmp;
	}
}

static IBinary* MakeCopy(IBinary* b)
{
	Binary *wrb = new Binary();
	const BYTE* pb = b->Bytes();
	memcpy(wrb->Alloc(b->Size()), pb, b->Size());
	MemoryBinary *mb = new MemoryBinary(wrb);
	return mb;
}

IBinary* Scale(IBinary* _src, int maxSize, int *_w, int *_h, ImageFormat* format)
{
	Image src, dest;
	*format = IF_UNKNOWN;

	if (!src.Read(_src, maxSize))
	{
		return MakeCopy(_src);
	}

	dest = src;

	int w = (int)src.m_size.width;
	int h = (int)src.m_size.height;
	int maxD = max(w, h);

	*_w = w;
	*_h = h;
	if (maxD <= maxSize)
	{
		return MakeCopy(_src);
	}

	double coef = (double)maxSize / maxD;
	dest.m_size.width = (int)(w * coef);
	dest.m_size.height = (int)(h * coef);
	if (!dest.Alloc())
	{
		return MakeCopy(_src);
	}

	////avir::CLancIR resizer;
	//avir::CImageResizer<> resizer(8);
	//resizer.resizeImage((char*)src.ptr(), w, h, src.m_step, 
	//	(char*)dest.ptr(), dest.m_size.width, dest.m_size.height, 
	//	dest.m_samples, coef);

	Resize* pResize = new Resize();

	pResize->m_interpolation = ippLinear;
	pResize->m_fBVal = 1;
	pResize->m_fCVal = 0;
	pResize->m_iLobes = 3;

	if (!pResize->Init(&src, &dest))
	{
		delete pResize;
		return MakeCopy(_src);
	}

	if (!pResize->ResizeImage(&src, &dest))
	{
		delete pResize;
		return MakeCopy(_src);
	}

	delete pResize;

	*_w = (int)dest.m_size.width;
	*_h = (int)dest.m_size.height;
	*format = dest.m_srcFormat;

	return dest.MakeBinary();
}