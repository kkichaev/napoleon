package com.grsoft.napoleon;

import java.io.File;
import java.io.FileWriter;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnEmail).setOnClickListener(new View.OnClickListener() {
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override public void onClick(View arg0) { sendToEmail(); }
		});
	}
	
	protected void sendToEmail() {
		OrgEx o = (OrgEx)org.getData();
		OrderEx order = (OrderEx) doc.getData();
		
		FirmImpl fi = new FirmImpl();
		FirmEx firm = (FirmEx) fi.getData();
		firm.id = order.firmCode;
		fi.read();
		fi.close();
		
		String subject = String.format("Счет для %s от %s", o.name, firm.name);
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SENDTO);
		//emailIntent.setType("text/rfc822");
		emailIntent.setData(Uri.parse("mailto:"));
		if(o.email.length() > 0)
			emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { o.email });
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, subject);//Html.fromHtml(makeOrderBody(order, o, firm)));
		
		File cacheDir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + getPackageName() +"/files/");
		if(!cacheDir.exists())
			cacheDir.mkdirs();
		File f = new File(cacheDir, "order.html");
		FileWriter fw;
		try {
			fw = new FileWriter(f);
			fw.append(makeOrderBody(order, o, firm));
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
		
		startActivity(Intent.createChooser(emailIntent, "Отправить email:"));
	}

	private String makeOrderBody(OrderEx order, OrgEx org, FirmEx firm) {
		
		String fullName = "ИНН " + firm.inn;
		if(firm.kpp.length() > 0) {
			fullName += " КПП " + firm.kpp;
		}
		fullName += firm.fullName + " " + firm.address;

		String ret = OrderTemplate.getTop().
				replace("%BankBic%", firm.bic).replace("%Bank%", firm.bank).replace("%BankAccount%", firm.bankAccount).
				replace("%INN%", firm.inn).replace("%KPP%", firm.kpp).replace("%Account%", firm.account).replace("%FirmName%", firm.fullName).
				replace("%Date%", Util.simpleDateFormat.format(order.date)).replace("%Number%", "1").replace("%FirmFullName%", fullName).
				replace("%OrgFullName%", org.fullName);

		PriceImpl pi = new PriceImpl();
		PriceEx prc = (PriceEx)pi.getData();
		
		int i = 1;
		
		long taxSum = 0, totalSum = 0;
		
		for(OrderItem item : order.items) {
			prc.id = item.id;
			pi.read();
			
			long sum = ((long)item.cost * item.qty /Consts.QTY_SCALE);
			double val = (double)item.cost * 100 / (100 + prc.tax);
			long itaxSum = (long)(sum - val * item.qty / Consts.QTY_SCALE + 0.5);
			
			taxSum += itaxSum;
			totalSum += sum;
			
			ret += OrderTemplate.getItem().replace("%OrdNum%", Integer.toString(i)).replace("%Code%", item.id).replace("%Name%", prc.name).
					replace("%Qty%", Util.IntToScaleStr(item.qty, Consts.QTY_SCALE)).replace("%Unit%", prc.unit).
					replace("%Cost%", Util.IntToScaleStr(item.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false)).
					replace("%ItemSum%", Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			
			i++;
		}
		pi.close();
		
		String sumText = Util.IntToScaleStr(totalSum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		String ndsText = "Без налогоа (НДС)", ndsSum = "-";
		if(taxSum != 0) {
			ndsText = "В том числе НДС";
			ndsSum = Util.IntToScaleStr(taxSum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		}
		ret += OrderTemplate.getBottom().replace("%Sum%", sumText).
				replace("%NdsText%", ndsText).replace("%NdsSum%", ndsSum).replace("%ItemCount%", Integer.toString(i-1)).
				replace("%SumText%", Integer2Str.digToText(totalSum)).replace("%Chif%", firm.chief).replace("%Buh%", firm.buh);

		return ret;
	}
}

class OrderTemplate {
	static public String getTop() {
		return 
"<!doctype html><html><head>" +
"<meta http-equiv='Content-Type' content='text/html; charset=utf-8'><style>" +
"        body { width: 210mm; margin-left: auto; margin-right: auto; border: 1px #efefef solid; font-size: 11pt;}" +
"        table.invoice_bank_rekv { border-collapse: collapse; border: 1px solid; }" +
"        table.invoice_bank_rekv > tbody > tr > td, table.invoice_bank_rekv > tr > td { border: 1px solid; }" +
"        table.invoice_items { border: 1px solid; border-collapse: collapse;}" +
"        table.invoice_items td, table.invoice_items th { border: 1px solid;}" +
"</style></head><body>" +
"<table width='100%'>" +
"<tr><td>&nbsp;</td><td style='width: 155mm;'>" +
"<div style='width:155mm; '>Внимание! Оплата данного счета означает согласие с условиями поставки товара." + 
"Уведомление об оплате  обязательно, в противном случае не гарантируется наличие товара на складе. " +
"Товар отпускается по факту прихода денег на р/с Поставщика, самовывозом, при наличии доверенности и паспорта.</div>" +
"</td></tr>" +
"<tr><td colspan='2'><div style='text-align:center;  font-weight:bold;'>Образец заполнения платежного поручения</div></td></tr></table>" +
"<table width='100%' cellpadding='2' cellspacing='2' class='invoice_bank_rekv'>" +
"<tr><td colspan='2' rowspan='2' style='min-height:13mm; width: 105mm;'>" +
"<table width='100%' border='0' cellpadding='0' cellspacing='0' style='height: 13mm;'>" +
"<tr><td valign='top'><div>%Bank%</div></td></tr>" +
"<tr><td valign='bottom' style='height: 3mm;'><div style='font-size:10pt;'>Банк получателя</div></td></tr>" +
"</table></td>" +
"<td style='min-height:7mm;height:auto; width: 25mm;'><div>БИK</div></td>" +
"<td rowspan='2' style='vertical-align: top; width: 60mm;'><div style=' height: 7mm; line-height: 7mm; vertical-align: middle;'>%BankBic%</div><div>%BankAccount%</div></td></tr>" +
"<tr><td style='width: 25mm;'><div>Сч. №</div></td></tr>" +
"<tr><td style='min-height:6mm; height:auto; width: 50mm;'><div>ИНН %INN%</div></td>" +
"<td style='min-height:6mm; height:auto; width: 55mm;'><div>КПП %KPP%</div></td>" +
"<td rowspan='2' style='min-height:19mm; height:auto; vertical-align: top; width: 25mm;'><div>Сч. №</div></td>" +
"<td rowspan='2' style='min-height:19mm; height:auto; vertical-align: top; width: 60mm;'><div>%Account%</div></td></tr>" +
"<tr><td colspan='2' style='min-height:13mm; height:auto;'>" +
"<table border='0' cellpadding='0' cellspacing='0' style='height: 13mm; width: 105mm;'>" +
"<tr><td valign='top'><div>%FirmName%</div></td></tr>" +
"<tr><td valign='bottom' style='height: 3mm;'><div style='font-size: 10pt;'>Получатель</div></td></tr>" +
"</table></td></tr></table>" +
"<br/><div style='font-weight: bold; font-size: 16pt; padding-left:5px;'>Счет № %Number% от %Date%</div>" +
"<br/><div style='background-color:#000000; width:100%; font-size:1px; height:2px;'>&nbsp;</div>" +
"<table width='100%'>" +
"<tr><td style='width: 30mm;'><div style=' padding-left:2px;'>Поставщик:</div></td><td><div style='font-weight:bold;  padding-left:2px;'>%FirmFullName%</div></td></tr>" +
"<tr><td style='width: 30mm;'><div style=' padding-left:2px;'>Покупатель:</div></td><td><div style='font-weight:bold;  padding-left:2px;'>%OrgFullName%</div></td></tr></table>" +
"<table class='invoice_items' width='100%' cellpadding='2' cellspacing='2'>" +
"<thead>" +
"<tr><th style='width:13mm;'>№</th><th style='width:20mm;'>Код</th><th>Товар</th><th style='width:20mm;'>Кол-во</th><th style='width:17mm;'>Ед.</th><th style='width:27mm;'>Цена</th><th style='width:27mm;'>Сумма</th></tr>" +
"</thead>" +
"<tbody>";				
	}
	
	public static String getItem() {
		return "<tr><td>%OrdNum%</td><td>%Code%</td><td>%Name%</td><td>%Qty%</td><td>%Unit%</td><td>%Cost%</td><td>%ItemSum%</td></tr>";
	}
	
	public static String getBottom() {
		return
"</tbody></table>" +
"<table border='0' width='100%' cellpadding='1' cellspacing='1'><tbody>" + 
"<tr><td></td><td style='width:67mm; font-weight:bold;  text-align:right;'>Итого:</td><td style='width:27mm; font-weight:bold;  text-align:right;'>%Sum%</td></tr>" + 
"<tr><td></td><td style='width:67mm; font-weight:bold;  text-align:right;'>%NdsText%</td><td style='width:27mm; font-weight:bold;  text-align:right;'>%NdsSum%</td></tr>" +  
"</tbody></table>" +
"<br /><div>Всего наименований %ItemCount% на сумму %Sum%." +
"<br />%SumText%</div>" +
"<br /><br /><div style='background-color:#000000; width:100%; font-size:1px; height:2px;'>&nbsp;</div><br/>" +
"<div>Руководитель ______________________ (%Chif%)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Главный бухгалтер ______________________ (%Buh%)</div><br/>" +
"<div style='width: 85mm;text-align:center;'>М.П.</div>" +
"<br/><div style='width:800px;text-align:left;font-size:10pt;'>Счет действителен к оплате в течении трех дней.</div>"
+"</body></html>";		
	}
}

class Integer2Str {
	protected static String[] digSet = {" один", " два", " три", " четыре", " пять", " шесть", " семь", " восемь",
		  " девять", " десять", " одиннадцать", " двенадцать", " тринадцать",
		  " четырнадцать", " пятнадцать", " шестнадцать", " семнадцать",
		  " восемнадцать", " девятнадцать"};
	protected static String[] decDigSet = {" двадцать", " тридцать", " сорок", " пятьдесят", " шестьдесят",
		  " семьдесят", " восемьдесят", " девяносто"};
	protected static String[] hunDigSet =	{" сто", " двести", " триста", " четыреста", " пятьсот", " шестьсот",
		  " семьсот", " восемьсот", " девятьсот"};
	protected static String[] othDig = {"", " тысяч", " миллион", " миллиард"};
	protected static String[] shortOth = {"", " тыс", " млн", " млрд" };
	protected static String[] firstRest =	{"", "а", "и"};
	protected static String[] lastRest = {"ов", "", "а"};
	protected static String[] shortRest =	{".", ".", "." };


	private static String addRest( long val, String str, String base, String[] restSet)
	{
		  int rVal = (int)(val%100);
		  str += base;
		
		  if( rVal > 10 && rVal < 20 )
		  {
		     str += restSet[0];
		     return str;
		  }
		
		  rVal %= 10;
		  switch ( rVal )
		  {
		     case 1:
		        str += restSet[1];
		        return str;
		     case 2:
		     case 3:
		     case 4:
		        str += restSet[2];
		        return str;
		  }
		  str += restSet[0];
		  
		  return str;
	}


	private static String conv1000( int val, int step, String str) {
	  int rest = val % 100;
	  val /= 100;
	
	  if ( val > 0 ) 
		  str = hunDigSet[val-1];
	  else 
		  str = "";
	
	  if ( rest == 0 ) 
		  return str;
	  
	  if ( rest < 20 ) {
	     if ( step == 1 )
	        switch ( rest ) {
	           case 1:
	              str += " одна";
	              return str;
	           case 2:
	              str += " две";
	              return str;
	        }
	     str += digSet[rest-1];
	  } else {
	     str += decDigSet[(rest/10)-2];
	     if ( rest % 10  > 0) {
	        rest %= 10;
	        if ( step == 1 )
	           switch ( rest ) {
	              case 1:
	                 str += " одна";
	                 return str;
	              case 2:
	                 str += " две";
	              return str;
	           }
	        str += digSet[rest-1];
	    }
	  }
	  
	  return str;
	}

	public static String digToText(long dig)
	{
		int step = 0;
		long lastDig;
		String result = new String();
		
		if ( dig == 0 ) 
			return " ноль";
		
		do
		{
			int rest = (int)(dig%1000);
		    lastDig = dig;
		    dig /= 1000;
		    
		    if ( rest != 0 ) {
		    	String curDig = conv1000(rest, step, "");
		
		        if ( step != 0 )
		           curDig = addRest( lastDig, curDig, othDig[step], (step == 1) ? firstRest : lastRest );
		
		        curDig += result;
		        result = curDig;
		    }
		    
		    step++;
		} while ( dig > 0 );
		
		return result;
	} 
}
