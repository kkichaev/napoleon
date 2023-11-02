package com.ashberrysoft.leadertask.interfaces;
import com.ashberrysoft.leadertask.application.LTSettings;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public interface FileHandlerConstants {
    public final String SOAP_NAMESPACE = LTSettings.getInstance().getSyncNamespace();
    public static final String NAME = "name";
    public static final String PASSWORD = "password";
    public static final String METHOD = "method";
    public static final String FILE_UID = "file_uid";
    public static final String EMAIL_CREATOR = "email_creator";
    public static final String FILE_USN_ENTITY = "file_usn_entity";
    public static final String CHARSET_DECODER = "UTF-8";
    public static final String FILE_UID_EMP_FOTO = "foto_uid";
    public static final String FOTO_USN_ENTITY = "foto_usn_entity";
    public final String POST_URI = SOAP_NAMESPACE+"FileHandler.ashx";
    public final String POST_URI_EMP_FOTO = SOAP_NAMESPACE+"FotoHandler.ashx";

}