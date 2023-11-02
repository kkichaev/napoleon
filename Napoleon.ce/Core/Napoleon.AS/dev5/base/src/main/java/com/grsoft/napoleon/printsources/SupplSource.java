package com.grsoft.napoleon.printsources;
import com.grsoft.aceteam.R;

import java.util.Date;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.util.Util;

public class SupplSource{
	@PrintInfo(name="ИНН")
	public  String supl_inn = "";
	@PrintInfo(name="Наименование")
	public String supl_name = "";
	@PrintInfo(name="КраткоеНаименование")
	public String supl_shortName = "";
	@PrintInfo(name="Банк")
	public String supl_bank = "";
	@PrintInfo(name="Адрес")
	public String supl_address = "";
	@PrintInfo(name="ФактическийАдрес")
	public String supl_fact_address = "";
	@PrintInfo(name="Телефон")
	public String supl_phone = "";
	@PrintInfo(name="ОКПО")
	public String s_okpo = "";
	@PrintInfo(name="Агент")
	public String agentName = "";
	
	@PrintInfo(name="Бухгалтер")
	public String buh = "";

	@PrintInfo(name="ОбщДиректор")
	public String commonChief = "";
	@PrintInfo(name="Директор")
	public String chief = "";
	
	@PrintInfo(name="ДиректорДолжность")
	public String chiefRange = "";
	@PrintInfo(name="ОтпускРазрешилДлж")
	public String shipApprove = "";

	@PrintInfo(name="ПриказАгент")
	public String agentOrder = "";
	
	@PrintInfo(name="Код")
	public String supl_pcode = "Код";
	@PrintInfo(name="Грузополучатель")
	public String supl_ptarget = "Грузополучатель";
	@PrintInfo(name="по ОКПО")
	public String supl_pokpo = "по ОКПО";
	@PrintInfo(name="Поставщик")
	public String supl_psuppl = "Поставщик";
	@PrintInfo(name="Плательщик")
	public String supl_psaller = "Плательщик";
	@PrintInfo(name="Итого")
	public String supl_ptotal = "Итого";
	@PrintInfo(name="Всего по накладной")
	public String supl_ptotalfull = "Всего по накладной";
	@PrintInfo(name="Всего мест")
	public String supl_pvsegomest = "Всего мест";
	@PrintInfo(name="Масса груза (нетто)")
	public String supl_pmassagruza = "Масса груза (нетто)";
	@PrintInfo(name="ОтпускГрузаПроизвел")
	public String shipment = "";
	@PrintInfo(name="Основание")
	public String reason = "";
	@PrintInfo(name="ИПРекв")
	public String certificate = "";
	@PrintInfo(name="ИПИмя")
	public String nameIP = "";
	
	@PrintInfo(name="ПаспортСерия")
	public String pser = "";
	@PrintInfo(name="ПаспортНомер")
	public String pnum = "";
	@PrintInfo(name="ПаспортВыдан")
	public String preg = "";
	@PrintInfo(name="ПаспортДатаВыдачи")
	public String pdata = "";
	
	public SupplSource(){
	}
	   
	public void setSupplyer(String code){
		if (code != null && code.length() > 0){
			FirmImpl firmImpl = new FirmImpl();
			Firm firm = firmImpl.getData();
			firm.id = code;
			
			try{
				if (firmImpl.read()){
					initFirm(firm);
				}
			}finally{
				firmImpl.close();
			}
		}
	}

	protected void initFirm(Firm firm) {
		AgentPrefix ap = AgentPrefix.get();
		if( ap != null ){
			agentName = ap.fullname.length() > 0 ? ap.fullname : ap.name;
			pser = ap.ser;
			pnum = ap.number;
			preg = ap.region;
			agentOrder = ap.order;
			
			Date d = new Date(0, 1, 1);
			
			if(ap.data.getTime() > d.getTime())
				pdata = Util.simpleDateFormat.format(ap.data);
		}
			
		supl_name = (firm.fullName.length() > 0) ? firm.fullName : firm.name;
		supl_bank = firm.bank;
		supl_address = firm.address;
		supl_fact_address = (firm.factAddress.length() == 0) ? firm.address : firm.factAddress;
		supl_phone = firm.phone;
		supl_inn = firm.inn;
		supl_shortName = firm.name;
		s_okpo = firm.okpo;
		
		buh = firm.buh;
		chief = firm.chief;
		commonChief = firm.chief;
		chiefRange = firm.chiefRange;
		shipApprove = firm.shipApprove;
		shipment = firm.shipment;
		if(firm.certificate != null && firm.certificate.length() > 0) {
			certificate =  firm.certificate;
			nameIP = firm.chief;
			chief = "";
			buh = "";
			chiefRange = "";
		}
	}
	
	public boolean getValue(StringBuilder value, String name, String format){
		return SilentReflector.getFieldValue(value, name, this, format);
	}
}
