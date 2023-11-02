package com.grsoft.util;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;

public class FPOperation
{
	//
	// Функции для расчета без переполнения
	//
	
	/**
	 * Сумма цены все пунктов
	 * выходное значение имеет масштаб цены (cost)
	 */
	public static long itemMul(long cost, long qty, int qty_scale)
	{
		long v = (long)cost * qty;
		return v / qty_scale;
	}
	
	/**
	 * Присвоить целочисленное значение полю объекта 
	 * @param val новое значение поля
	 * @param dataObject объетк
	 * @param fieldName поле, которому будет присвоено значение
	 */
	public static void assignToScaleValue(int val, DataObject dataObject,
			String fieldName)
	{
		try
		{
			int scale = DataObjectInfo.getInstance().getScale( dataObject.getClass(), fieldName);
			val *= scale;
			dataObject.getClass().getField(fieldName).setInt(dataObject, val);
		}
		catch(Exception e)
		{
			Debug.dbgPrint(e.getMessage());
			e.printStackTrace();
		}
	}
	
//	public static int decPow(int pow)
//	{
//		final int DEC = 10;
//		
//		int result = 1;
//		
//		if(pow > 0)
//			result = decPow(--pow) * DEC;
//		
//		return result;
//		
//	}

//	inline DWORD ItemWeight(DWORD weight, DWORD qty)
//	{
//	   return (DWORD)(((__int64)weight * qty)/WEIGHT_SCALE);
//	}
//
//	inline DWORD ItemCost(DWORD sum, DWORD qty)
//	{
//	   if( qty == 0 ) return 0;
//	   return (DWORD)((__int64)sum * QTY_SCALE) / qty;
//	}
//
//	const DWORD MAX_VALUE = 2000000000l;
//
//	inline DWORD MulInPack(DWORD val, DWORD inPack, DWORD scale)
//	{
//	   if( inPack == 0 || scale == 0 ) return 0;
//	   DWORD sign = 1;
//	   if( (int)val < 0 )
//	   {
//	      sign = -1;
//	      val = (DWORD)-(int)val;
//	   }
//
//	   return sign * (DWORD)((__int64)val * inPack / scale);
//	}
//
//	inline DWORD DivideInPack(DWORD val, DWORD inPack, DWORD scale)
//	{
//	   if( inPack == 0 || scale == 0 ) return 0;
//	   DWORD sign = 1;
//	   if( (int)val < 0 )
//	   {
//	      sign = -1;
//	      val = (DWORD)-(int)val;
//	   }
//	   return sign * (DWORD)(((__int64)val * scale) / inPack);
//	}
}
