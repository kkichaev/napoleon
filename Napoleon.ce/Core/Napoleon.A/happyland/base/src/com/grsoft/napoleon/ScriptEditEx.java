package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collection;
import com.grsoft.database.TaskInfoHitching;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.network.ObjectListener;
import com.grsoft.script.ScriptEdit;


public class ScriptEditEx extends ScriptEdit {
	protected DocumentSender createDocumentSender() {
		return new DocumentSender(this, findViewById(R.id.btnSend), doc.getSendedDocuments()){
			@Override
			protected Collection<? extends ObjectListener> getObjectsToSend() {
				@SuppressWarnings("unchecked")
				ArrayList<ObjectListener> result = (ArrayList<ObjectListener>) super.getObjectsToSend();
				result.add(new TaskInfoHitching());
				return result;
			}
		};
	}
}
