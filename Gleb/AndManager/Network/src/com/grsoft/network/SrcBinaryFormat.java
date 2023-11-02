/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 *
 * kki   26/04/2011   creating
 */
package com.grsoft.network;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.SrcDataCounter;

/**
 * Бинарные данные, которые хранятся отдельно от БД
 * @author kki
 *
 */
public class SrcBinaryFormat extends BinaryFormat {

	public SrcBinaryFormat(String name) {
		super(name);
	}

	@Override
	public byte[] valueToBinary(Object value) {
		try{
			File file = new File(new String((byte[])value));
			InputStream is = new BufferedInputStream(new FileInputStream(file)); 
			byte[] buf = new byte[(int) file.length()];
			is.read(buf);
			is.close();
			return super.valueToBinary(buf);
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public boolean readMember(Member m, ByteStream stream) {
		byte[] data = toBytes(stream);
		
		if (data == null)
			return false;
		
		try{
			File file = new File(Path.getDataDir(), Integer.toString(SrcDataCounter.getValue())); 
			OutputStream fos = new BufferedOutputStream(new FileOutputStream(file));
			fos.write(data);
			fos.close();
			m.setValue( file.getAbsolutePath().toString().getBytes());
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
