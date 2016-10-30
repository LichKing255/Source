package org.telegram.ui.Mihangram.UserChanges.mobo.b;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.Mihangram.UserChanges.mobo.bd;

public class b
  extends SQLiteOpenHelper
{
  public b(Context paramContext)
  {
    super(paramContext, bd.c(paramContext) + ".db", (SQLiteDatabase.CursorFactory)null, bd.a(paramContext));
  }
  
  private void a(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("create table tbl_update ( _id integer primary key autoincrement, type integer,old_value text,new_value text,user_id integer,is_new integer,change_date integer default (strftime('%s','now') * 1000))");
  }
  
  private void b(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("create table tbl_setting ( _id integer primary key autoincrement, key text, value text)");
    paramSQLiteDatabase.execSQL("INSERT INTO tbl_setting VALUES (1,'notifyChanges','true')");
    paramSQLiteDatabase.execSQL("INSERT INTO tbl_setting VALUES (2,'notifyNameChanges','true')");
    paramSQLiteDatabase.execSQL("INSERT INTO tbl_setting VALUES (3,'notifyStatusChanges','true')");
    paramSQLiteDatabase.execSQL("INSERT INTO tbl_setting VALUES (4,'notifyPhotoChanges','true')");
    paramSQLiteDatabase.execSQL("INSERT INTO tbl_setting VALUES (5,'notifyPhoneChanges','true')");
  }
  
  private void c(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("create table tbl_alarm ( _id integer primary key autoincrement, title text,message text,imageUrl text,positiveBtnText text,positiveBtnAction text,positiveBtnUrl text,negativeBtnText text,negativeBtnAction text,negativeBtnUrl text,showCount integer,exitOnDismiss integer,targetNetwork integer,displayCount integer,targetVersion integer)");
  }
  
  private void d(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("create table tbl_favorite ( _id integer primary key autoincrement, chatID integer)");
  }
  
  private void e(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("create table tbl_hidden ( _id integer primary key autoincrement, dialogID integer)");
  }
  
  private void f(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("create table tbl_category ( _id integer primary key autoincrement, name text,priority integer)");
  }
  
  private void g(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("create table tbl_cat_dlg_info ( _id integer primary key autoincrement, dialogId integer,categoryId integer, foreign key( categoryId ) references tbl_category ( _id ) ON DELETE CASCADE )");
  }
  
  private void h(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("CREATE TRIGGER trg_category_priority_from_id AFTER INSERT ON tbl_category FOR EACH ROW  WHEN NEW.priority IS NULL  BEGIN  UPDATE tbl_category SET priority= NEW._id WHERE rowid = NEW.rowid;END;");
  }
  
  private void i(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("create table tbl_favorite_stickers ( _id integer primary key autoincrement, doc_id integer,priority integer)");
  }
  
  private void j(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("CREATE TRIGGER trg_fav_stickers_priority_from_id AFTER INSERT ON tbl_favorite_stickers FOR EACH ROW  WHEN NEW.priority IS NULL  BEGIN  UPDATE tbl_favorite_stickers SET priority= NEW._id WHERE rowid = NEW.rowid;END;");
  }
  
  boolean a(SQLiteDatabase paramSQLiteDatabase, String paramString)
  {
    if ((paramString == null) || (paramSQLiteDatabase == null) || (!paramSQLiteDatabase.isOpen())) {}
    do
    {
      return false;
      paramSQLiteDatabase = paramSQLiteDatabase.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] { "table", paramString });
    } while (!paramSQLiteDatabase.moveToFirst());
    int i = paramSQLiteDatabase.getInt(0);
    paramSQLiteDatabase.close();
    return i > 0;
  }
  
  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    a(paramSQLiteDatabase);
    b(paramSQLiteDatabase);
    c(paramSQLiteDatabase);
    d(paramSQLiteDatabase);
    e(paramSQLiteDatabase);
    f(paramSQLiteDatabase);
    g(paramSQLiteDatabase);
    h(paramSQLiteDatabase);
    i(paramSQLiteDatabase);
    j(paramSQLiteDatabase);
  }
  
  public void onOpen(SQLiteDatabase paramSQLiteDatabase)
  {
    super.onOpen(paramSQLiteDatabase);
    if (!paramSQLiteDatabase.isReadOnly()) {
      paramSQLiteDatabase.execSQL("PRAGMA foreign_keys=ON;");
    }
  }
  
  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    paramInt2 = paramInt1 + 1;
    paramInt1 = paramInt2;
    if (paramInt2 == 1) {
      paramInt1 = paramInt2 + 1;
    }
    paramInt2 = paramInt1;
    if (paramInt1 <= 65420) {
      paramInt2 = 65421;
    }
    paramInt1 = paramInt2;
    if (paramInt2 == 65421)
    {
      paramInt1 = paramInt2 + 1;
      c(paramSQLiteDatabase);
    }
    paramInt2 = paramInt1;
    if (paramInt1 == 65422) {
      paramInt2 = paramInt1 + 1;
    }
    paramInt1 = paramInt2;
    if (paramInt2 == 65423) {
      paramInt1 = paramInt2 + 1;
    }
    paramInt2 = paramInt1;
    if (paramInt1 == 65424)
    {
      paramInt2 = paramInt1 + 1;
      paramSQLiteDatabase.execSQL("drop table tbl_alarm");
      c(paramSQLiteDatabase);
      d(paramSQLiteDatabase);
    }
    int i = paramInt2;
    if (paramInt2 <= 68528) {
      i = 68529;
    }
    paramInt1 = i;
    if (i == 68529)
    {
      paramInt2 = i + 1;
      SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
      paramInt1 = paramInt2;
      if (localSharedPreferences.getInt("default_tab", 0) == 2)
      {
        localSharedPreferences.edit().putInt("default_tab", 7).commit();
        paramInt1 = paramInt2;
      }
    }
    paramInt2 = paramInt1;
    if (paramInt1 <= 71944) {
      paramInt2 = 71945;
    }
    paramInt1 = paramInt2;
    if (paramInt2 == 71945)
    {
      paramInt1 = paramInt2 + 1;
      e(paramSQLiteDatabase);
    }
    paramInt2 = paramInt1;
    if (paramInt1 <= 71955) {
      paramInt2 = 71956;
    }
    paramInt1 = paramInt2;
    if (paramInt2 == 71956)
    {
      paramInt1 = paramInt2 + 1;
      f(paramSQLiteDatabase);
      g(paramSQLiteDatabase);
      h(paramSQLiteDatabase);
    }
    paramInt2 = paramInt1;
    if (paramInt1 == 71957)
    {
      paramInt2 = paramInt1 + 1;
      i(paramSQLiteDatabase);
      j(paramSQLiteDatabase);
    }
    paramInt1 = paramInt2;
    if (paramInt2 <= 71963) {
      paramInt1 = 71964;
    }
    if (paramInt1 == 71964)
    {
      if (!a(paramSQLiteDatabase, "tbl_category"))
      {
        f(paramSQLiteDatabase);
        g(paramSQLiteDatabase);
        h(paramSQLiteDatabase);
      }
      if (!a(paramSQLiteDatabase, "tbl_favorite_stickers"))
      {
        i(paramSQLiteDatabase);
        j(paramSQLiteDatabase);
      }
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\UserChanges\mobo\b\b.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */