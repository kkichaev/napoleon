package com.grsoft.napoleon;

import java.util.Collection;
import com.grsoft.database.AgentOrgHitching;
import com.grsoft.database.PotenzialOrgHitching;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.ObjectListener;

public class VisitEditEx extends VisitEdit {
	protected void send() {
		new DocumentSender(VisitEditEx.this, btnSend, VisitDoc.OBJ_NAME, visit, visit.getRowid(), VisitEditEx.this){
			
			protected Collection<ObjectListener> getObjectsToSend() {
				Collection<ObjectListener> result = super.getObjectsToSend();
				
				PotenzialOrgHitching poh = new PotenzialOrgHitching("Org");
				if( poh.size() > 0 ){
					result.add(poh);
					result.add(new AgentOrgHitching(poh));
				}
				
				return result;
			};
			
			protected com.grsoft.database.PotenzialOrgHitching createPotenzialOrgHitching() {
				return null;	};
		}.execute((Void[])null);
	}
}
