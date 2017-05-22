package com.data.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

/**
 * Created by qzzhu on 17-3-28.
 */

public class AccountBean {

    public final static String TABLE_NAME = "accounts";
    public final static String TABLE_NAME2 = "userdb";
    public final static String TABLE_AUTHORITY = "com.views.simpleutils.authority";
    public final static Uri URI_TABEL1 = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + TABLE_AUTHORITY + "/" + TABLE_NAME);
    public final static Uri URI_TABEL2 = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + TABLE_AUTHORITY + "/" + TABLE_NAME);

    public final static Uri URI_BASE = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + TABLE_AUTHORITY + "/" + TABLE_NAME + "/");
    public final static Uri URI_BASE2 = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + TABLE_AUTHORITY + "/" + TABLE_NAME2 + "/");



    public final static String FIELD_ID="_id";
    /**
     * buddy account
     */
    public final static String FIELD_USERNAEM="user_name";
    public final static int USERNAME_INDEX =1;
    /**
     * buddy nickname
     */
    public final static String FIELD_NAME="name";
    //	public final static int NAME_INDEX =2;
    public final static String FIELD_IMPU="impu";
//	public final static int IMPU_INDEX =3;

    /**
     * buddy status
     */
    public final static String FIELD_SIP_STATUE="sip_status";
    public final static int SIPSTATUS_INDEX =4;


    /**
     * sortModel first alpha
     */
    public final static String FIELD_SORT="sortletter";
    public final static int SORT_INDEX =5;

    public final static String FIELD_BELONG="belong"; //author preference.getauthor.split"@"[0]
    public final static int BELONG_INDEX =6;


    /**
     * sortModel foreign
     */
    public final static String FIELD_FOREIGN="foriegn";
    public final static int FOREIGN_INDEX =7;



    public final static String FIELD_HEADIMAG_PATH ="head_img";
//	public final static int HEADIMAG_INDEX =8;

    public final static String QueryWhere=FIELD_BELONG+"=?";
    public final static String DeleteAll_Where=FIELD_BELONG+"=?";
    public final static String DeleteItem_Where=FIELD_BELONG+"=?"+" and "+FIELD_USERNAEM+"=?";
    //	public final static String InsertWhere=FIELD_BELONG+"=?";
    public final static String UpdateWhere=FIELD_BELONG+"=?"+" and "+FIELD_USERNAEM+"=?";
    public final static String QueryItemWhere = QueryWhere+" and "+FIELD_NAME+"=? and "+FIELD_IMPU+"=?";

    public final static String FIELD_BUDDY_CREATE="CREATE TABLE IF NOT EXISTS "+ TABLE_NAME + " (" +
            AccountBean.FIELD_ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
            FIELD_USERNAEM +" text,"+
            FIELD_NAME +" text,"+
            FIELD_IMPU +" text,"+
            FIELD_SIP_STATUE +" text,"+
            FIELD_SORT +" text,"+
            FIELD_BELONG +" text,"+
            FIELD_FOREIGN +" text,"+
            FIELD_HEADIMAG_PATH +" text"+
            " );";


    private final String username;
    private final String name;
    private final String impu;
    private final String sip_statue;
    private final String belong; //author
    private final String sortLetter;
    private final String foreign;
    public AccountBean(String username, String name, String impu, String sip_statue, String belong, String sortLetter, String foreign){
        this.username=username;
        this.name=name;
        this.impu=impu;
        this.sip_statue=sip_statue;
        this.belong=belong;
        this.sortLetter=sortLetter;
        this.foreign=foreign;
    }

    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(FIELD_USERNAEM,username);
        values.put(FIELD_NAME,name);
        values.put(FIELD_IMPU,impu);
        values.put(FIELD_SORT,sortLetter);
        values.put(FIELD_BELONG,belong);
        values.put(FIELD_HEADIMAG_PATH,"");
        return values;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getImpu() {
        return impu;
    }


    private static String QueryExist =FIELD_USERNAEM+"=? and "+FIELD_NAME+"=? and "+FIELD_IMPU+"=?";

}
