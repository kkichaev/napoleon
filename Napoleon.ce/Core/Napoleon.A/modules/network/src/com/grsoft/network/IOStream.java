package com.grsoft.network;

import java.io.InputStream;
import java.io.OutputStream;

import com.grsoft.network.exception.RuntimeException;

public interface IOStream
{
	OutputStream getOutputStream()throws RuntimeException;
	InputStream getInputStream() throws RuntimeException;
}
