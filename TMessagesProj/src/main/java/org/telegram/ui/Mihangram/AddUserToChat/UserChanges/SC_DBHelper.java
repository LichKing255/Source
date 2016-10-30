package org.telegram.ui.Mihangram.AddUserToChat.UserChanges;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class SC_DBHelper
  extends SQLiteOpenHelper
{
  private static final String _id = "_id";
  private static final String database_NAME = "MihanSContactsDB";
  private static final int database_VERSION = 3;
  private static final String table_operation = "SContactsOperationTB";
  private static final String table_scontacts = "SContactsTB";
  private String date = "date";
  private String operation = "operation";
  private String userId = "userId";
  
  public SC_DBHelper(Context paramContext)
  {
    super(paramContext, "MihanSContactsDB", null, 3);
  }
  
  public void addOperation(int paramInt1, String paramString, int paramInt2)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put(this.userId, Integer.valueOf(paramInt1));
    localContentValues.put(this.operation, paramString);
    localContentValues.put(this.date, Integer.valueOf(paramInt2));
    localSQLiteDatabase.insert("SContactsOperationTB", null, localContentValues);
    localSQLiteDatabase.close();
  }
  
  public void addSContact(int paramInt)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put(this.userId, Integer.valueOf(paramInt));
    localSQLiteDatabase.insertWithOnConflict("SContactsTB", null, localContentValues, 5);
    localSQLiteDatabase.close();
  }
  
  public void deleteAllSContacts()
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.delete("SContactsTB", null, null);
    localSQLiteDatabase.close();
  }
  
  public void deleteAllSOperation()
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.delete("SContactsOperationTB", null, null);
    localSQLiteDatabase.close();
  }
  
  public void deleteSContact(int paramInt)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.delete("SContactsTB", this.userId + "=" + paramInt, null);
    localSQLiteDatabase.close();
  }
  
  public ArrayList<OperationModel> getAllOperation()
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    Cursor localCursor = localSQLiteDatabase.rawQuery("SELECT * FROM SContactsOperationTB", null);
    ArrayList localArrayList = new ArrayList();
    if (localCursor.moveToFirst()) {
      do
      {
        localArrayList.add(new OperationModel(localCursor.getInt(1), localCursor.getString(2), localCursor.getInt(3)));
      } while (localCursor.moveToNext());
    }
    localSQLiteDatabase.close();
    return localArrayList;
  }
  
  public ArrayList<Integer> getAllSContacts()
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    Cursor localCursor = localSQLiteDatabase.rawQuery("SELECT * FROM SContactsTB", null);
    ArrayList localArrayList = new ArrayList();
    if (localCursor.moveToFirst()) {
      do
      {
        localArrayList.add(Integer.valueOf(localCursor.getInt(1)));
      } while (localCursor.moveToNext());
    }
    localSQLiteDatabase.close();
    return localArrayList;
  }
  
  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("CREATE TABLE SContactsTB ( _id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER)");
    paramSQLiteDatabase.execSQL("CREATE TABLE SContactsOperationTB ( _id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER, operation TEXT, date integer default (strftime('%s','now') * 1000))");
  }
  
  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS SContactsTB");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS SContactsOperationTB");
    onCreate(paramSQLiteDatabase);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\AddUserToChat\UserChanges\SC_DBHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */