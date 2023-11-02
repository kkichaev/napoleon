package com.grsoft.network;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;

import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.util.UnicodUtils;

import android.content.Context;

public class FileByteStream extends ByteStream {
	
	File src;
	FileInputStream fis;
	byte[] buf;
	Character curSym, nextSym;
	
	private FileByteStream(int received, Context context, File src) {
		super(context, received);
		this.src = src;

		try {
			fis = new FileInputStream(src);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		buf = new byte[2];
		curSym = null;
		nextSym = null;
	}
	
	@Override
	public char current() {
		if(curSym == null)
			curSym = readChar();
		return curSym;
	}
	
	@Override
	public char next() {
		if(nextSym == null)
			nextSym = readChar();
		return nextSym;
	}
	
	@Override
	public boolean moveNext() {
		if(nextSym != null) {
			curSym = nextSym;
			nextSym = null;
			
			return true;
		}
		
		if(isEOS())
			return false;
		
		curSym = readChar();
		return true;
	}
	
	@Override
	public boolean writeBytes(FileOutputStream fos, int size) throws IOException {
		if(size == 0)
			return true;
		
		int avail = fis.available();
		if(avail < size)
			return false;
		
		int needwr = size;  
		byte[] buf = new byte [1024 * 100];
		while(needwr > 0) {
			int curWr = Math.min(needwr, buf.length);
			fis.read(buf, 0, curWr);
			fos.write(buf, 0, curWr);
			needwr -= curWr;
		}
		if(size % 2 != 0) 
			fis.read(buf, 0, 1);

		curSym = null;
		return true;
	}
	
	@Override
	public boolean copyBytes(byte[] dest) {
		int size = dest.length;
		if(size == 0)
			return true;
		
		try {
			int avail = fis.available();
			if(avail < size)
				return false;
			fis.read(dest, 0, size);
			if(size % 2 != 0) {
				byte[] buf = new byte [10];
				fis.read(buf, 0, 1);
			}
			
			curSym = null;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	@Override
	public int getSize() { return (int)src.length(); }
	
	@Override
	public int getPosition() {
		int cp = (int)src.length();
		try {
			cp -= fis.available();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cp;
	}
	
	private char readChar() {
		if(isEOS())
			return END_OF_STRING;
		
		try {
			fis.read(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return UnicodUtils.readChar(buf, 0, END_OF_STRING);
	}
	
	@Override
	public boolean isEOS() {
		int avail = 0;
		try {
			avail = fis.available();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return (avail == 0);
	}
	
	@Override
	public void close() {
		try {
			fis.close();
			src.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static FileByteStream decompress(byte[] src, int received, Context context) {
		ByteArrayInputStream ms = new ByteArrayInputStream(src);
		InflaterInputStream decompress = new InflaterInputStream(ms);
		
		File tmpFile = new File(Path.getCacheDir(context), "pkt.dat");
		FileOutputStream fos = null;
		
		int BUF_SIZE = 1024 * 1024; // 1M
		byte[] buf = new byte[BUF_SIZE];
		try {
			fos = new FileOutputStream(tmpFile);
			while(true) {
				int bytes_read = decompress.read(buf, 0, BUF_SIZE);
				if(bytes_read <= 0)
					break;
				
				fos.write(buf, 0, bytes_read);
			}
			fos.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			if(fos != null)
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			
			return null;
		}
		
		return new FileByteStream(received, context, tmpFile);
	}
}
