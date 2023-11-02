package com.grsoft.napoleon.adapters;

import java.util.Date;
import java.util.List;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.grsoft.manager.Manager;
import com.grsoft.manager.R;
import com.grsoft.manager.ReportData;
import com.grsoft.manager.view.RowItem;

public abstract class ReportAdapter extends PagerAdapter {

	public interface OnItemClickHandler {
		public void ItemClicked(ReportAdapter owner, RowItem agent);
	}

	List<RowItem> agents;
	ReportData[] data = new ReportData[3];
	View[] pages = new View[3];

	ViewGroup container;
	LayoutInflater inflater;

	OnItemClickHandler clickListener;
	private Context context;

	public ReportAdapter(Context context, List<RowItem> agents,
			LayoutInflater inflater) {
		this.agents = agents;
		this.inflater = inflater;
		this.context = context;

		data[2] = loadData(null, false);
		data[1] = loadData(data[2].getDate(), false);
		data[0] = loadData(data[1].getDate(), false);
	}

	public void setOnItemClickHandler(OnItemClickHandler listener) {
		this.clickListener = listener;
	}

	/**
	 * «агружает данные дл€ даты
	 * 
	 * @param curDate
	 *            - null - загрузить на текущую дату
	 * @param nextDate
	 *            - true - дл€ следующей даты от curDate
	 * @return
	 */
	protected abstract ReportData loadData(Date curDate, boolean nextDate);

	public ReportData getItemData(int pos) {
		return (pos < data.length && pos >= 0) ? data[pos] : null;
	}

	@Override
	public int getCount() {
		return data.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return data[position].getTitle();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		this.container = container;

		View v = inflater.inflate(R.layout.report_page, container, false);

		((Manager) getContext()).adjustListView(v, position);

		pages[position] = v;
		container.addView(v);

		return v;
	}

	@Override
	public int getItemPosition(Object object) {
		for (int i = 0; i < 3; i++)
			if (pages[i] == object)
				return i;

		return POSITION_NONE;
	}

	/**
	 * сдвигаем страницы
	 * 
	 * @param shiftUp
	 *            - сдвигаем вверх по датам (а не по индексу) 1->0, 2->1
	 * @return true shifted
	 */
	public boolean shift(boolean shiftUp) {
		
		/* ѕохоже что это было ограничение что бы нелистать дальше чем сегодн€, нафига это.....
		if (shiftUp && data[2].isLast())
			return false;
		 */
		
		int index;
		if (shiftUp) {
			index = 2;

			data[0] = data[1];
			data[1] = data[2];

			destroyItem(container, 0, pages[0]);
			pages[0] = pages[1];
			pages[1] = pages[2];
		} else {
			index = 0;

			data[2] = data[1];
			data[1] = data[0];

			destroyItem(container, 2, pages[2]);
			pages[2] = pages[1];
			pages[1] = pages[0];
		}

		data[index] = loadData(data[index].getDate(), shiftUp);
		instantiateItem(container, 1);

		notifyDataSetChanged();

		return true;
	}

	public void setDate(Date date) {
		data[0] = loadData(date, false);
		data[1] = loadData(data[0].getDate(), true);
		data[2] = loadData(data[1].getDate(), true);

		instantiateItem(container, 1);
		notifyDataSetChanged();
	}

	public void refresh(List<RowItem> agents) {
		this.agents = agents;
		for (int i = 0; i < 3; i++) {
			if (i == 0)
				data[i] = loadData(data[1].getDate(), false);
			else
				data[i] = loadData(data[i - 1].getDate(), true);

			if (pages[i] != null) {
				ListView list = (ListView) pages[i].findViewById(R.id.lvItems);
				((AgentsAdapter) list.getAdapter()).refresh(agents, data[i]);
			}
		}
	}

	public Context getContext() {
		return context;
	}
}
