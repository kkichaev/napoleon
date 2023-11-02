package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.util.OrgInfoClickListener;

import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class DocumentsEx extends Documents {
	@Override
	protected void onResume() {
		super.onResume();
		findViewById(R.id.tvTotalSum).setVisibility(View.GONE);
	}
	
	@Override
	protected OnClickListener createInfoClickListener() {
		return new OrgInfoClickListener(org.getData(), getContactViewid(), this) {
			@Override
			protected void adjustDialogView(View view) {
				super.adjustDialogView(view);
				OrgEx oe = (OrgEx)o;
				TextView tvAddress = (TextView) view.findViewById(R.id.tvAddress);
				
				StringBuilder sb = new StringBuilder();
				sb.append("<i>Адрес:</i> ").append(oe.address).append("<br>");
				sb.append("<i>ФИО ЛПР:</i> ").append(oe.nameLPR).append("<br>");
				sb.append("<i>Контакты ЛПР:</i> ").append(oe.contactsLPR).append("<br>");
				sb.append("<i>Д.Р ЛПР:</i> ").append(oe.bithdayLPR).append("<br>");
				sb.append("<i>Дни посещения ТТ:</i> ").append(oe.visitDays).append("<br>");
				sb.append("<i>Кто закреплен за данной ТТ:</i> ").append(oe.responsible).append("<br>");
				sb.append("<i>Наличие оборудования:</i> ").append(oe.equipment).append("<br>");
				sb.append("<i>Количество СКЮ по матрице:</i> ").append(oe.matrixsku).append("<br>");
				sb.append("<i>День поставки/заказа:</i> ").append(oe.orderday).append("<br>");
				sb.append("<i>Промо-план:</i> ").append(oe.promoplan).append("<br>");
				sb.append("<i>Номер поставщика:</i> ").append(oe.providerNumber).append("<br>");
				
				tvAddress.setText(Html.fromHtml(sb.toString()));
			}
		};
	}
}
