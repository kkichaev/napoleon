package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.ActionType;
import com.grsoft.dataobjects.Answer;
import com.grsoft.dataobjects.AnswerEx;
import com.grsoft.dataobjects.AnswerItem;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DocDataObject;
import com.grsoft.dataobjects.QuestionItem;
import com.grsoft.dataobjects.QuestionItemEx;
import com.grsoft.dataobjects.QuestionItemValues;
import com.grsoft.dataobjects.impl.Answerable;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.ExtrasConst;

public class ActionAnswerEdit extends Activity {
	private static final String ACTION = "action";
	private List<AnswerItemVal> answers = new ArrayList<AnswerItemVal>();
	private Document<? extends DocDataObject> document;
	private AnswerEx answer;
	private CheckBox cbActPrezent;
	private String type;

	abstract class AnswerItemVal {
		protected View control;
		protected QuestionItem item;
		protected AnswerItem result = new AnswerItem();

		public AnswerItemVal(View control, QuestionItem item) {
			this.control = control;
			this.item = item;

			result.id = item.id;
			result.iditem = item.id;
			result.type = item.type;
		}

		abstract AnswerItem val();
	}

	public static void open(Context context, long rowid, String itemid,
			String action) {
		Intent intent = new Intent(context, ActionAnswerEdit.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		intent.putExtra(ActionListBase.ITEM_ID, itemid);
		intent.putExtra(ACTION, action);

		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.action_answer_edit);
		LinearLayout container = (LinearLayout) findViewById(R.id.container);
		cbActPrezent = (CheckBox) findViewById(R.id.cbActPrezent);

		TextView tvDesc = (TextView) findViewById(R.id.tvDescription);
		Button btnCommit = (Button) findViewById(R.id.btnCommit);

		Intent intent = getIntent();

		document = createDocumnet();
		document.read(intent.getLongExtra(ExtrasConst.DOC_ROW_ID_STR,
				ExtrasConst.INVALID_ROWID));
		document.close();

		final String action = intent.getStringExtra(ACTION);

		Cursor c = null;

		type = "";
		String phone = "";
		String fio = "";
		String descr = "";
		
		try {
			c = DataBaseManager.getDataBase()
					.query(DataObjectInfo.getInstance().getTableName(
							com.grsoft.dataobjects.Action.class), null,
							"id=? and org=?",
							new String[] { action, document.getId() }, null,
							null, null);
			
			if(c.moveToFirst()){
				type = c.getString(c.getColumnIndex("action"));
				phone = c.getString(c.getColumnIndex("phone"));
				fio = c.getString(c.getColumnIndex("fio"));
				descr = c.getString(c.getColumnIndex("descr"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null)
				c.close();
		}

		DbReader reader = new DbReader();
		final ActionType data = new ActionType();
		StringBuilder where = new StringBuilder();
		where.append("name='").append(type).append("'");
		reader.select(data, DataObjectInfo.getInstance().getTableName(data.getClass()), where.toString());
		reader.close();

		StringBuilder sb = new StringBuilder();
		sb.append(descr).append("<br>").append(fio).append("<br>").append(phone);
		tvDesc.setText(Html.fromHtml(sb.toString()));
		final String itemid = intent.getStringExtra(ActionListBase.ITEM_ID);
		answer = ((Answerable<?>) document).findAnswer(itemid, type);

		int pos = 1;
		for (QuestionItem qitem : data.items) {
			LinearLayout row = new LinearLayout(this);
			row.setOrientation(LinearLayout.VERTICAL);
			row.setBackgroundColor(pos++ % 2 != 0 ? getResources().getColor(
					R.color.white) : getResources().getColor(R.color.even_row));
			TextView text = new TextView(this);
			text.setText(qitem.text);
			text.setTextSize(18);
			QuestionItemEx iex = (QuestionItemEx) qitem;
			text.setTextColor(iex.optional == 0 ? getResources().getColor(
					R.color.black) : getResources().getColor(R.color.grey));
			row.addView(text);

			switch (qitem.type) {
			case QuestionItem.TEXT:
				insertTextView(row, qitem, answer);
				break;
			case QuestionItem.NUMBER:
				insertNumberView(row, qitem, answer);
				break;
			case QuestionItem.LIST:
				insertListView(row, qitem, answer);
				break;
			case QuestionItem.SET:
				insertSetView(row, qitem, answer);
				break;
			case QuestionItem.BOOLEAN:
				insertBooleanView(row, qitem, answer);
				break;
			}

			container.addView(row);
		}

		btnCommit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				List<String> list = new ArrayList<String>();
				for (QuestionItem qi : data.items)
					if (((QuestionItemEx) qi).optional == 0)
						list.add(qi.id);

				if (answer == null) {
					answer = new AnswerEx();
					answer.created = ((CreateDocDataObject)document.getData()).created;
					answer.id = document.getId();
					answer.question = type;
					answer.qname = action;
					answer.price = itemid;
					answer.answerid = UUID.randomUUID().toString()
							.replace("-", "");

					((Answerable<?>) document).add(answer);
				}

				answer.items.clear();
				answer.actprezent = cbActPrezent.isChecked() ? 1 : 0;

				for (AnswerItemVal val : answers) {
					AnswerItem ai = val.val();

					if (ai != null) {
						answer.items.add(ai);
						list.remove(ai.id);
					}
				}

				if (list.size() > 0)
					Toast.makeText(ActionAnswerEdit.this,
							R.string.all_question_should_been_processed,
							Toast.LENGTH_SHORT).show();
				else {
					DbWriter writer = new DbWriter();
					writer.insertRecord(answer);
					document.write();
					
					finish();
				}
			}
		});

		btnCommit.setEnabled(((CreatableDocument<?>) document).isEditable());

		if (answer != null)
			cbActPrezent.setChecked(answer.actprezent > 0);
	}

	private Document<? extends DocDataObject> createDocumnet() {
		return DocType.getCurDoc().create();
	}

	private void insertBooleanView(LinearLayout container, QuestionItem item,
			Answer answer) {
		if (item.values.size() >= 2) {
			String selected = "";

			if (answer != null)
				for (AnswerItem i : answer.items)
					if (i.id.equals(item.id)) {
						selected = i.answer;
						break;
					}

			RadioGroup group = new RadioGroup(this);
			group.setOrientation(LinearLayout.HORIZONTAL);

			RadioButton rb = new RadioButton(this);
			rb.setText(item.values.get(0).value);
			rb.setId(Genid.generate());
			rb.setTextColor(getResources().getColor(R.color.black));
			if (selected.equals(item.values.get(0).value))
				rb.setChecked(true);
			group.addView(rb);

			rb = new RadioButton(this);
			rb.setId(Genid.generate());
			rb.setText(item.values.get(1).value);
			rb.setTextColor(getResources().getColor(R.color.black));
			if (selected.equals(item.values.get(1).value))
				rb.setChecked(true);
			group.addView(rb);

			container.addView(group);

			answers.add(new AnswerItemVal(group, item) {

				@Override
				AnswerItem val() {
					RadioButton rb = (RadioButton) control
							.findViewById(((RadioGroup) control)
									.getCheckedRadioButtonId());

					if (rb != null) {
						result.answer = rb.getText().toString();
						return result;
					} else
						return null;
				}
			});
		}
	}

	private void insertSetView(LinearLayout container, QuestionItem item,
			Answer answer) {
		if (item.values.size() >= 2) {
			String selected = "";

			if (answer != null)
				for (AnswerItem i : answer.items)
					if (i.id.equals(item.id)) {
						selected = i.answer;
						break;
					}

			RadioGroup group = new RadioGroup(this);
			group.setOrientation(LinearLayout.VERTICAL);

			for (QuestionItemValues val : item.values) {
				RadioButton rb = new RadioButton(this);
				rb.setText(val.value);
				rb.setTextColor(getResources().getColor(R.color.black));
				rb.setId(Genid.generate());
				group.addView(rb);

				if (val.value.equals(selected))
					rb.setChecked(true);
			}

			container.addView(group);
			answers.add(new AnswerItemVal(group, item) {

				@Override
				AnswerItem val() {
					RadioButton rb = (RadioButton) control
							.findViewById(((RadioGroup) control)
									.getCheckedRadioButtonId());

					if (rb != null) {
						result.answer = rb.getText().toString();
						return result;
					} else
						return null;
				}

			});
		}
	}

	private void insertListView(LinearLayout container, QuestionItem item,
			Answer answer) {
		Set<String> set = new HashSet<String>();

		if (answer != null)
			for (AnswerItem i : answer.items)
				if (i.id.equals(item.id)) {
					set.add(i.answer);
				}

		for (QuestionItemValues val : item.values) {
			CheckBox cb = new CheckBox(this);
			cb.setText(val.value);
			cb.setTextColor(getResources().getColor(R.color.black));
			container.addView(cb);

			if (set.contains(val.value))
				cb.setChecked(true);

			answers.add(new AnswerItemVal(cb, item) {

				@Override
				AnswerItem val() {
					CheckBox cb = (CheckBox) control;
					if (cb.isChecked()) {
						result.answer = cb.getText().toString();
						return result;
					} else
						return null;
				}
			});
		}
	}

	private void insertNumberView(LinearLayout container, QuestionItem item,
			Answer answer) {
		EditText edit = new EditText(this);
		edit.setInputType(EditorInfo.TYPE_CLASS_NUMBER
				| EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
		container.addView(edit);

		if (answer != null)
			for (AnswerItem i : answer.items)
				if (i.id.equals(item.id)) {
					edit.setText(i.answer);
					break;
				}

		answers.add(new AnswerItemVal(edit, item) {

			@Override
			AnswerItem val() {
				result.answer = ((EditText) control).getText().toString()
						.trim();

				if (result.answer.length() > 0)
					return result;
				else
					return null;
			}
		});
	}

	private void insertTextView(LinearLayout container, QuestionItem item,
			Answer answer) {
		EditText edit = new EditText(this);
		container.addView(edit);

		if (answer != null)
			for (AnswerItem i : answer.items)
				if (i.id.equals(item.id)) {
					edit.setText(i.answer);
					break;
				}

		answers.add(new AnswerItemVal(edit, item) {

			@Override
			AnswerItem val() {
				result.answer = ((EditText) control).getText().toString()
						.trim();

				if (result.answer.length() > 0)
					return result;
				else
					return null;
			}
		});
	}
}

class Genid {
	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	@SuppressLint("NewApi")
	public static int generate() {
		for (;;) {
			final int result = sNextGeneratedId.get();
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF)
				newValue = 1; // Roll over to 1, not 0.
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}
}
