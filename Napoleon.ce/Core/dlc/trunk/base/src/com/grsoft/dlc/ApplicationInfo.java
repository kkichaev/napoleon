/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grsoft.dlc;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;

import com.grsoft.dlc.database.AllowedApp;

/**
 * Represents a launchable application. An application is made of a name (or title), an intent
 * and an icon.
 */
public class ApplicationInfo {
    /**
     * The application name.
     */
    CharSequence title;

    /**
     * The intent used to start the application.
     */
    Intent intent;

    /**
     * The application icon.
     */
    Drawable icon;

    /**
     * When set to true, indicates that the icon has been resized.
     */
    boolean filtered;
    
    private boolean allowed;
    private boolean cached = false;
    DLCApp context;
    
    private boolean prot = false;

	public int index = 0;
    
   
	public ApplicationInfo(DLCApp context) {
		this.context = context;
	}
	
    final void setActivity(ComponentName className, int launchFlags) {
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(launchFlags);
    }
    
    public void setActivity(String action){
    	intent = new Intent(action);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationInfo)) {
            return false;
        }

        ApplicationInfo that = (ApplicationInfo) o;
        return title.equals(that.title) &&
                intent.getComponent().getClassName().equals(
                        that.intent.getComponent().getClassName());
    }

    @Override
    public int hashCode() {
        int result;
        result = (title != null ? title.hashCode() : 0);
        final String name = intent.getComponent().getClassName();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
    
    public void setAllowed(boolean value){
    	SQLiteDatabase database = context.dbManager.getWritableDatabase();
    	String classname = intent.getComponent().getClassName();
    	
    	if (value){
    		ContentValues cv = new ContentValues();
        	cv.put(AllowedApp.CLASSNAME, classname);
        	
    		database.insert(AllowedApp.TABLE_NAME, null, cv);
    	}else{
    		database.delete(AllowedApp.TABLE_NAME, 
    				String.format("%s=?", AllowedApp.CLASSNAME),
    				new String[]{classname});
    	}
    	
    	allowed = value;
    	cached = true;
    }
    
    public boolean isAllowed(){
    	if (!cached){
    		cached = true;
    		
    		SQLiteDatabase database = context.dbManager.getReadableDatabase();
        	Cursor cursor = database.query(AllowedApp.TABLE_NAME, 
        			AllowedApp.PROJECTION, String.format("%s=?", AllowedApp.CLASSNAME), 
        			new String[]{intent.getComponent().getClassName()}, null, null, null);
        	allowed = cursor.moveToFirst();
        	cursor.close();
    	}
    	
    	return allowed;
    }
    
    public void setProtected(boolean value){
    	prot = value;
    }
    
    public boolean isProtected(){
    	return prot;
    }
    
    public void init() {
		allowed = isAllowed();
	}
    
    public Intent getIntent(){
    	return intent;
    }
}
