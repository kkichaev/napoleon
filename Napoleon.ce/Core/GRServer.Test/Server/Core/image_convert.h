#ifndef __IMAGE_CONVERT_H
#define __IMAGE_CONVERT_H

enum ImageFormat
{
	IF_UNKNOWN = 0,
	IF_JPEG,
	IF_PNG,
};

IBinary* Scale(IBinary* _src, int maxSize, int *_w, int *_h, ImageFormat* format);
bool IsJPEG(const BYTE* pb);
bool IsPNG(const BYTE* pb);

#endif