package org.telegram.ui.Mihangram.DownloadManager.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.Video;

public class SQLDownload
  extends SQLiteOpenHelper
{
  private static final String _id = "_id";
  private static final String database_NAME = "SQLDownload21";
  private static final int database_VERSION = 1;
  private static final String table_Favs = "Download";
  public String access_hash = "access_hash";
  public String check_dl = "check_dl";
  public String date = "date";
  public String dc_id = "dc_id";
  public String duration = "duration";
  public String f6329h = "h";
  public String f6330w = "w";
  public String file_name = "file_name";
  public String id = "id";
  public String mime_type = "mime_type";
  public String progs = "progs";
  public String real_name = "real_name";
  public String size = "size";
  public String state = "state";
  public String type_file = "type_file";
  public String user_id = "user_id";
  
  public SQLDownload(Context paramContext)
  {
    super(paramContext, "SQLDownload21", null, 1);
  }
  
  public void createDownload(TLRPC.Document paramDocument, int paramInt, String paramString)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put(this.id, String.valueOf(paramDocument.id));
    localContentValues.put(this.access_hash, String.valueOf(paramDocument.access_hash));
    localContentValues.put(this.date, String.valueOf(paramDocument.date));
    localContentValues.put(this.duration, Integer.valueOf(((TLRPC.DocumentAttribute)paramDocument.attributes.get(0)).w));
    localContentValues.put(this.mime_type, String.valueOf(paramDocument.mime_type));
    localContentValues.put(this.size, String.valueOf(paramDocument.size));
    localContentValues.put(this.dc_id, String.valueOf(paramDocument.dc_id));
    localContentValues.put(this.f6330w, Integer.valueOf(((TLRPC.DocumentAttribute)paramDocument.attributes.get(0)).w));
    localContentValues.put(this.f6329h, Integer.valueOf(((TLRPC.DocumentAttribute)paramDocument.attributes.get(0)).h));
    localContentValues.put(this.user_id, String.valueOf(paramDocument.user_id));
    localContentValues.put(this.state, Integer.valueOf(0));
    localContentValues.put(this.progs, String.valueOf(0));
    localContentValues.put(this.type_file, Integer.valueOf(paramInt));
    localContentValues.put(this.file_name, String.valueOf(((TLRPC.DocumentAttribute)paramDocument.attributes.get(0)).file_name));
    localContentValues.put(this.real_name, paramString);
    localContentValues.put(this.check_dl, Integer.valueOf(1));
    localSQLiteDatabase.insert("Download", null, localContentValues);
    localSQLiteDatabase.close();
  }
  
  public void createDownload(TLRPC.Video paramVideo, int paramInt, String paramString)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put(this.id, String.valueOf(paramVideo.id));
    localContentValues.put(this.access_hash, String.valueOf(paramVideo.access_hash));
    localContentValues.put(this.date, String.valueOf(paramVideo.date));
    localContentValues.put(this.duration, String.valueOf(paramVideo.duration));
    localContentValues.put(this.mime_type, String.valueOf(paramVideo.mime_type));
    localContentValues.put(this.size, String.valueOf(paramVideo.size));
    localContentValues.put(this.dc_id, String.valueOf(paramVideo.dc_id));
    localContentValues.put(this.f6330w, String.valueOf(paramVideo.w));
    localContentValues.put(this.f6329h, String.valueOf(paramVideo.h));
    localContentValues.put(this.user_id, String.valueOf(paramVideo.user_id));
    localContentValues.put(this.state, Integer.valueOf(0));
    localContentValues.put(this.progs, String.valueOf(0));
    localContentValues.put(this.type_file, Integer.valueOf(paramInt));
    localContentValues.put(this.file_name, String.valueOf(""));
    localContentValues.put(this.real_name, paramString);
    localContentValues.put(this.check_dl, Integer.valueOf(1));
    localSQLiteDatabase.insert("Download", null, localContentValues);
    localSQLiteDatabase.close();
  }
  
  public void deleteDownload(String paramString)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.delete("Download", this.id + "=" + paramString, null);
    localSQLiteDatabase.close();
  }
  
  public int findIndex(String paramString)
  {
    Object localObject2 = null;
    Object localObject1 = null;
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    try
    {
      Cursor localCursor = localSQLiteDatabase.rawQuery("SELECT * FROM Download", null);
      localObject1 = localCursor;
      localObject2 = localCursor;
      boolean bool = localCursor.moveToFirst();
      int i = 0;
      if (bool) {
        do
        {
          localObject1 = localCursor;
          localObject2 = localCursor;
          bool = localCursor.getString(1).equals(paramString);
          if (bool)
          {
            if (localCursor != null) {
              localCursor.close();
            }
            localSQLiteDatabase.close();
            return i;
          }
          i += 1;
          localObject1 = localCursor;
          localObject2 = localCursor;
          bool = localCursor.moveToNext();
        } while (bool);
      }
      if (localCursor != null) {
        localCursor.close();
      }
      localSQLiteDatabase.close();
      return -1;
    }
    catch (Exception paramString)
    {
      for (;;)
      {
        localObject2 = localObject1;
        FileLog.e("tmessages", paramString);
        if (localObject1 != null)
        {
          localObject2 = localObject1;
          ((Cursor)localObject1).close();
        }
        localObject2 = localObject1;
        localSQLiteDatabase.close();
      }
    }
    finally
    {
      if (localObject2 != null) {
        ((Cursor)localObject2).close();
      }
      localSQLiteDatabase.close();
    }
  }
  
  public List getAllVideoInDownload()
  {
    ArrayList localArrayList = new ArrayList();
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    Cursor localCursor = localSQLiteDatabase.rawQuery("SELECT * FROM Download", null);
    while ((localCursor.moveToFirst()) && (localCursor.moveToNext())) {}
    localSQLiteDatabase.close();
    return localArrayList;
  }
  
  public List getAllVideoInDownloadE()
  {
    ArrayList localArrayList = new ArrayList();
    SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
    Cursor localCursor = localSQLiteDatabase.rawQuery("SELECT * FROM Download", null);
    ElementDownload localElementDownload;
    if (localCursor.moveToFirst())
    {
      localElementDownload = new ElementDownload();
      localElementDownload.setId(localCursor.getString(1));
      localElementDownload.setId(localCursor.getString(1));
      localElementDownload.setAccess_hash(localCursor.getString(2));
      localElementDownload.setDate(localCursor.getString(3));
      localElementDownload.setDuration(Integer.parseInt(localCursor.getString(4)));
      localElementDownload.setMime_type(localCursor.getString(5));
      localElementDownload.setSize(Integer.parseInt(localCursor.getString(6)));
      localElementDownload.setDc_id(Integer.parseInt(localCursor.getString(7)));
      localElementDownload.setW(Integer.parseInt(localCursor.getString(8)));
      localElementDownload.setH(Integer.parseInt(localCursor.getString(9)));
      localElementDownload.setUser_id(Integer.parseInt(localCursor.getString(10)));
      if (localCursor.getInt(11) == 1) {
        break label307;
      }
      localElementDownload.setCheck(true);
      localElementDownload.setState(Boolean.valueOf(false).booleanValue());
    }
    for (;;)
    {
      localElementDownload.setProg(Float.parseFloat(localCursor.getString(12)));
      localElementDownload.setType(localCursor.getInt(13));
      localElementDownload.setFile_name(localCursor.getString(14));
      localElementDownload.setReal_name(localCursor.getString(15));
      localArrayList.add(localElementDownload);
      if (localCursor.moveToNext()) {
        break;
      }
      localCursor.close();
      localSQLiteDatabase.close();
      return localArrayList;
      label307:
      localElementDownload.setCheck(false);
      localElementDownload.setState(Boolean.valueOf(true).booleanValue());
    }
  }
  
  public List getAllVideoInDownloadEE()
  {
    Object localObject3 = null;
    Object localObject1 = null;
    ArrayList localArrayList = new ArrayList();
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    try
    {
      Cursor localCursor = localSQLiteDatabase.rawQuery("SELECT * FROM Download", null);
      localObject1 = localCursor;
      localObject3 = localCursor;
      if (localCursor.moveToFirst())
      {
        localObject1 = localCursor;
        localObject3 = localCursor;
        ElementDownload localElementDownload = new ElementDownload();
        localObject1 = localCursor;
        localObject3 = localCursor;
        localElementDownload.setId(localCursor.getString(1));
        localObject1 = localCursor;
        localObject3 = localCursor;
        localElementDownload.setAccess_hash(localCursor.getString(2));
        localObject1 = localCursor;
        localObject3 = localCursor;
        localElementDownload.setDate(localCursor.getString(3));
        localObject1 = localCursor;
        localObject3 = localCursor;
        localElementDownload.setDuration(Integer.parseInt(localCursor.getString(4)));
        localObject1 = localCursor;
        localObject3 = localCursor;
        localElementDownload.setMime_type(localCursor.getString(5));
        localObject1 = localCursor;
        localObject3 = localCursor;
        localElementDownload.setSize(Integer.parseInt(localCursor.getString(6)));
        localObject1 = localCursor;
        localObject3 = localCursor;
        localElementDownload.setDc_id(Integer.parseInt(localCursor.getString(7)));
        localObject1 = localCursor;
        localObject3 = localCursor;
        localElementDownload.setW(Integer.parseInt(localCursor.getString(8)));
        localObject1 = localCursor;
        localObject3 = localCursor;
        localElementDownload.setH(Integer.parseInt(localCursor.getString(9)));
        localObject1 = localCursor;
        localObject3 = localCursor;
        localElementDownload.setUser_id(Integer.parseInt(localCursor.getString(10)));
        localObject1 = localCursor;
        localObject3 = localCursor;
        localElementDownload.setState(true);
        localObject1 = localCursor;
        localObject3 = localCursor;
        localElementDownload.setProg(Float.parseFloat(localCursor.getString(12)));
        localObject1 = localCursor;
        localObject3 = localCursor;
        localElementDownload.setType(localCursor.getInt(13));
        localObject1 = localCursor;
        localObject3 = localCursor;
        localElementDownload.setFile_name(localCursor.getString(14));
        localObject1 = localCursor;
        localObject3 = localCursor;
        localElementDownload.setReal_name(localCursor.getString(15));
        localObject1 = localCursor;
        localObject3 = localCursor;
        localElementDownload.setCheck(Boolean.valueOf(true).booleanValue());
        localObject1 = localCursor;
        localObject3 = localCursor;
        localArrayList.add(localElementDownload);
        localObject1 = localCursor;
        localObject3 = localCursor;
        Toast.makeText(ApplicationLoader.applicationContext, localElementDownload.id + "", 1).show();
        if (localCursor != null) {
          localCursor.close();
        }
        localSQLiteDatabase.close();
        return localArrayList;
      }
      if (localCursor != null) {
        localCursor.close();
      }
      localSQLiteDatabase.close();
      return localArrayList;
    }
    catch (Exception localException)
    {
      localObject3 = localObject1;
      FileLog.e("tmessages", localException);
      return localArrayList;
    }
    finally
    {
      if (localObject3 != null) {
        ((Cursor)localObject3).close();
      }
      localSQLiteDatabase.close();
    }
  }
  
  public List getAllVideoInDownloadM()
  {
    ArrayList localArrayList = new ArrayList();
    SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
    Cursor localCursor = localSQLiteDatabase.rawQuery("SELECT * FROM Download", null);
    ElementDownload localElementDownload;
    if (localCursor.moveToFirst())
    {
      localElementDownload = new ElementDownload();
      localElementDownload.setId(localCursor.getString(1));
      localElementDownload.setId(localCursor.getString(1));
      localElementDownload.setAccess_hash(localCursor.getString(2));
      localElementDownload.setDate(localCursor.getString(3));
      localElementDownload.setDuration(Integer.parseInt(localCursor.getString(4)));
      localElementDownload.setMime_type(localCursor.getString(5));
      localElementDownload.setSize(Integer.parseInt(localCursor.getString(6)));
      localElementDownload.setDc_id(Integer.parseInt(localCursor.getString(7)));
      localElementDownload.setW(Integer.parseInt(localCursor.getString(8)));
      localElementDownload.setH(Integer.parseInt(localCursor.getString(9)));
      localElementDownload.setUser_id(Integer.parseInt(localCursor.getString(10)));
      if (localCursor.getInt(11) == 1) {
        break label307;
      }
      localElementDownload.setCheck(true);
      localElementDownload.setState(Boolean.valueOf(false).booleanValue());
    }
    for (;;)
    {
      localElementDownload.setProg(Float.parseFloat(localCursor.getString(12)));
      localElementDownload.setType(localCursor.getInt(13));
      localElementDownload.setFile_name(localCursor.getString(14));
      localElementDownload.setReal_name(localCursor.getString(15));
      localArrayList.add(localElementDownload);
      if (localCursor.moveToNext()) {
        break;
      }
      localCursor.close();
      localSQLiteDatabase.close();
      return localArrayList;
      label307:
      localElementDownload.setCheck(false);
      localElementDownload.setState(Boolean.valueOf(true).booleanValue());
    }
  }
  
  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("CREATE TABLE Download ( _id INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, access_hash TEXT, date TEXT, duration TEXT, mime_type TEXT, size TEXT, dc_id TEXT, w TEXT, h TEXT, user_id TEXT, state INTEGER, progs TEXT, type_file INTEGER, file_name TEXT, real_name TEXT, check_dl INTEGER )");
  }
  
  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS Download");
    onCreate(paramSQLiteDatabase);
  }
  
  public boolean updateCheckState(String paramString, int paramInt)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put(this.check_dl, Integer.valueOf(paramInt));
    paramInt = localSQLiteDatabase.update("Download", localContentValues, this.id + "=" + paramString, null);
    localSQLiteDatabase.close();
    return paramInt > 0;
  }
  
  public boolean updatedetails(String paramString1, String paramString2)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put(this.progs, paramString2);
    int i = localSQLiteDatabase.update("Download", localContentValues, this.id + "=" + paramString1, null);
    localSQLiteDatabase.close();
    return i > 0;
  }
  
  public boolean updatestate(String paramString, int paramInt)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put(this.state, Integer.valueOf(paramInt));
    paramInt = localSQLiteDatabase.update("Download", localContentValues, this.id + "=" + paramString, null);
    localSQLiteDatabase.close();
    return paramInt > 0;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\DownloadManager\SQLite\SQLDownload.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */