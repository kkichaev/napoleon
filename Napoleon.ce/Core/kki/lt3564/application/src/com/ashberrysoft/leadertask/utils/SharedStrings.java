package com.ashberrysoft.leadertask.utils;

/**
 * 
 * @since 2014-06-27
 * @author Tregub Artem tregub.artem@gmail.com
 */

public interface SharedStrings {

    static final char QUOTE_C = '\"';
    static final char EQUALS_C = '=';
    static final char BRACE_OPEN_C = '(';
    static final char BRACE_CLOSE_C = ')';
    static final char COMMA_C = ',';
    static final char SPLIT = '/';
    static final char COLON_C = ':';
    static final char SPACE_C = ' ';
    static final char TAB_C = '\t';
    static final char NEW_LINE_C = '\n';
    static final char START_C = '*';
    static final char DOT_C = '.';
    static final char MINUS_C = '-';
    static final char PLUS_C = '+';
    static final char PERCENT_C = '%';

    static final String SPLIT_DOT_DOBLE = "\\.\\.";
    static final String SPLIT_DOT = "\\.";
    static final String EMPTY = "";

    static final String ONE = Integer.toString(1);
    static final String ZERO = Integer.toString(0);
    static final String GMT = "GMT";
    static final String ARROW_RIGHT = " → ";
    static final String EXTENSION_JPG = ".jpg";
    static final String DIEZ = "#";

    static final String MIME_TYPE_IMAGE = "image/*";
    static final String MIME_TYPE_JPEG = "image/jpeg";
    static final String MIME_TYPE_PNG = "image/png";
    static final String MIME_TYPE_AUDIO = "audio/*";
    static final String MIME_TYPE_VIDEO = "video/*";
    static final String MIME_TYPE_TEXT = "text/*";
    static final String MIME_TYPE_APPLICATION = "*/*";
    static final String MIME_TYPE_PLAIN = "text/plain";

    static final String CONTENT_MEDIA = "content://media";
    static final String CONTENT_GOOGLE_PHOTOS = "content://com.google.android.apps.photos.content";
    static final String CONTENT_FILE = "file:///";

    static final String FORMAT_COLOR_STRING = "#%06X";

    static final String UTF_8 = "utf-8";

    static final String NUMBER_999 = "999";
    static final String NUMBER_99 = "99";

    // SQL
    static final String NOT_EQUALS = "<>";
    static final String EQUALS = "=";
    static final String IN = " IN ";
    static final String OR = " OR ";
    static final String AND = " AND ";
    static final String NOT_IN = " NOT IN ";

    static final String ADD = " ADD ";
    static final String INTEGER = " INTEGER ";
    static final String LONG = " LONG ";
    static final String DOUBLE = " DOUBLE ";
    static final String BOOLEAN = " BOOLEAN ";
    static final String TEXT = " TEXT ";
    static final String ALTER_TABLE = "ALTER TABLE ";
    static final String RENAME = " RENAME ";
    static final String TO = " TO ";
    static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";
    static final String SELECT = "SELECT ";
    static final String WHERE = " WHERE ";
    static final String FROM = " FROM ";
    static final String BRACE_OPEN = " ( ";
    static final String BRACE_CLOSE = " ) ";
    static final String BEGIN_TRANSACTION = "BEGIN TRANSACTION";
    static final String COMMIT = "COMMIT";
    static final String DROP_INDEX_IF_EXISTS = "DROP INDEX IF EXISTS ";
    static final String INSERT_INTO = "INSERT INTO ";
    static final String NOT = " NOT ";
    static final String EXISTS = " EXISTS ";
    static final String LENGTH = "LENGTH(";

    static final String ORDER_BY = " ORDER  BY ";

    static final String DELETE = "DELETE ";
    static final String UPDATE = "UPDATE ";
    static final String SET = " SET ";

    static final String CASE_WHEN = " CASE WHEN ";
    static final String THEN_ELSE = " THEN 0 ELSE 1 END ";

    static final String IS_NULL = " IS NULL ";
    static final String IS_NOT_NULL = " IS NOT NULL ";

    static final String COLLATE_NOCASE = " COLLATE NOCASE";
    static final String NULL = "NULL";

    static final String ASC = " ASC ";
    static final String DESC = " DESC ";
    
    static final String LIMIT = " LIMIT ";
    static final String LIKE = " LIKE ";
    static final String QUOTE = "'";
    static final String ARROW = "&#8595;";
    static final String CIRCLE = "&#8226;";
    static final String SPACE = "&#160;";
}