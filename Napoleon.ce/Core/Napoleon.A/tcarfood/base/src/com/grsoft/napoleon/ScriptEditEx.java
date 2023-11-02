package com.grsoft.napoleon;

import com.grsoft.script.ScriptEdit;


public class ScriptEditEx extends ScriptEdit {
	//private boolean block = false;
	
//	protected void initSrciptImpl(int scriptdefid) {
//		if (scriptdefid != ExtrasConst.INVALID_ID){
//			ScriptImplEx scriptImpl = new ScriptImplEx();
//			ScriptDefImpl scriptDefImpl = new ScriptDefImpl();
//			scriptDefImpl.getData().id = scriptdefid;
//			
//			if (scriptImpl.read(docRowId) && scriptDefImpl.read() && scriptImpl.getData().items.size() == 0){
//				OrgImpl org = new OrgImpl();
//				org.read("id", scriptImpl.getId());
//				block = DocumentsEx.hasNotPayedDelivery(org);
//				
//				for(int i = 0; i < scriptDefImpl.getData().items.size(); i++) {
//					ScriptDefItem item = scriptDefImpl.getData().items.get(i);
//					ScriptItem si = new ScriptItem();
//					si.type = item.curType;
//					scriptImpl.getData().items.add(si);
//					boolean skipped = false;
//					
//					if (block && si.type.equals(OrderDoc.instance().getObjectName())){
//						skipped = true;
//						scriptImpl.setBlocked(i);
//					}
//					
//					if( !inited && !skipped) {
//						DocType dt = (DocType) DocType.getDocType(item.curType);
//						if( dt != null ) {
//							CreatableDocument<?> doc = openFirstItem(scriptImpl, scriptDefImpl.getData(), item, dt);
//							
//							if(doc != null){
//								si.date = doc.getData().created;
//								si.state = ScriptItem.DOC_INITED;
//								inited = true;
//							}
//						}
//					}
//				}
//				
//				scriptImpl.write();
//			}
//			
//			scriptImpl.close();
//			scriptDefImpl.close();
//		}
//	}
//	
//	@Override
//	protected void openDoc(int position) {
//		if(!(block && doc.getData().items.get(position).type.equals(OrderDoc.instance().getObjectName())))
//				super.openDoc(position);
//	}
}
