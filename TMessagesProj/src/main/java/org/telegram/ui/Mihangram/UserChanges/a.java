package org.telegram.ui.Mihangram.UserChanges;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.ApplicationLoader;

public class a
{
  private b a = new b(ApplicationLoader.applicationContext);
  
  public Cursor a(int paramInt1, int paramInt2)
  {
    if (paramInt1 != 0) {}
    for (String str = "type=" + paramInt1;; str = null) {
      return this.a.getReadableDatabase().query("tbl_update", (String[])null, str, (String[])null, (String)null, (String)null, "_id DESC", paramInt2 + "");
    }
  }
  
  public Long a(Long paramLong1, Long paramLong2)
  {
    SQLiteDatabase localSQLiteDatabase = this.a.getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    try
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("categoryId", paramLong1);
      localContentValues.put("dialogId", paramLong2);
      long l = localSQLiteDatabase.insertOrThrow("tbl_cat_dlg_info", (String)null, localContentValues);
      localSQLiteDatabase.setTransactionSuccessful();
      return Long.valueOf(l);
    }
    finally
    {
      localSQLiteDatabase.endTransaction();
    }
  }
  
  public Long a(AlarmResponse paramAlarmResponse)
  {
    SQLiteDatabase localSQLiteDatabase = this.a.getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    try
    {
      ContentValues localContentValues = new ContentValues();
      if (paramAlarmResponse.getId() != null) {
        localContentValues.put("_id", paramAlarmResponse.getId());
      }
      localContentValues.put("title", paramAlarmResponse.getTitle());
      localContentValues.put("message", paramAlarmResponse.getMessage());
      localContentValues.put("imageUrl", paramAlarmResponse.getImageUrl());
      localContentValues.put("positiveBtnText", paramAlarmResponse.getPositiveBtnText());
      localContentValues.put("positiveBtnAction", paramAlarmResponse.getPositiveBtnAction());
      localContentValues.put("positiveBtnUrl", paramAlarmResponse.getPositiveBtnUrl());
      localContentValues.put("negativeBtnText", paramAlarmResponse.getNegativeBtnText());
      localContentValues.put("negativeBtnAction", paramAlarmResponse.getNegativeBtnAction());
      localContentValues.put("negativeBtnUrl", paramAlarmResponse.getNegativeBtnUrl());
      localContentValues.put("showCount", paramAlarmResponse.getShowCount());
      if (paramAlarmResponse.getExitOnDismiss().booleanValue()) {}
      for (int i = 1;; i = 0)
      {
        localContentValues.put("exitOnDismiss", Integer.valueOf(i));
        localContentValues.put("targetNetwork", paramAlarmResponse.getTargetNetwork());
        if (paramAlarmResponse.getDisplayCount() != null) {
          localContentValues.put("displayCount", paramAlarmResponse.getDisplayCount());
        }
        localContentValues.put("targetVersion", paramAlarmResponse.getTargetVersion());
        if ((paramAlarmResponse.getId() != null) && (a(paramAlarmResponse.getId().longValue()) != null)) {
          break;
        }
        long l = localSQLiteDatabase.insertOrThrow("tbl_alarm", null, localContentValues);
        localSQLiteDatabase.setTransactionSuccessful();
        return Long.valueOf(l);
      }
      localSQLiteDatabase.update("tbl_alarm", localContentValues, "_id=" + paramAlarmResponse.getId().longValue(), null);
      localSQLiteDatabase.setTransactionSuccessful();
      paramAlarmResponse = paramAlarmResponse.getId();
      localSQLiteDatabase.endTransaction();
      return paramAlarmResponse;
    }
    finally
    {
      localSQLiteDatabase.endTransaction();
    }
  }
  
  /* Error */
  public Long a(UpdateModel paramUpdateModel)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 23	org/telegram/ui/Mihangram/UserChanges/a:a	Lorg/telegram/ui/Mihangram/UserChanges/b;
    //   4: invokevirtual 65	org/telegram/ui/Mihangram/UserChanges/b:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   7: astore 5
    //   9: aload 5
    //   11: invokevirtual 68	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   14: new 70	android/content/ContentValues
    //   17: dup
    //   18: invokespecial 71	android/content/ContentValues:<init>	()V
    //   21: astore 6
    //   23: aload 6
    //   25: ldc -40
    //   27: aload_1
    //   28: invokevirtual 222	org/telegram/ui/Mihangram/UserChanges/UpdateModel:getType	()I
    //   31: invokestatic 180	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   34: invokevirtual 163	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   37: aload 6
    //   39: ldc -32
    //   41: aload_1
    //   42: invokevirtual 227	org/telegram/ui/Mihangram/UserChanges/UpdateModel:getOldValue	()Ljava/lang/String;
    //   45: invokevirtual 114	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   48: aload 6
    //   50: ldc -27
    //   52: aload_1
    //   53: invokevirtual 232	org/telegram/ui/Mihangram/UserChanges/UpdateModel:getNewValue	()Ljava/lang/String;
    //   56: invokevirtual 114	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   59: aload 6
    //   61: ldc -22
    //   63: aload_1
    //   64: invokevirtual 237	org/telegram/ui/Mihangram/UserChanges/UpdateModel:getUserId	()I
    //   67: invokestatic 180	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   70: invokevirtual 163	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   73: aload_1
    //   74: invokevirtual 240	org/telegram/ui/Mihangram/UserChanges/UpdateModel:isNew	()Z
    //   77: ifeq +69 -> 146
    //   80: iconst_1
    //   81: istore_2
    //   82: aload 6
    //   84: ldc -14
    //   86: iload_2
    //   87: invokestatic 180	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   90: invokevirtual 163	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   93: aload_1
    //   94: invokevirtual 245	org/telegram/ui/Mihangram/UserChanges/UpdateModel:getChangeDate	()Ljava/lang/String;
    //   97: ifnull +14 -> 111
    //   100: aload 6
    //   102: ldc -9
    //   104: aload_1
    //   105: invokevirtual 245	org/telegram/ui/Mihangram/UserChanges/UpdateModel:getChangeDate	()Ljava/lang/String;
    //   108: invokevirtual 114	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   111: aload_1
    //   112: invokevirtual 248	org/telegram/ui/Mihangram/UserChanges/UpdateModel:getId	()Ljava/lang/Long;
    //   115: ifnonnull +36 -> 151
    //   118: aload 5
    //   120: ldc 47
    //   122: aconst_null
    //   123: aload 6
    //   125: invokevirtual 85	android/database/sqlite/SQLiteDatabase:insertOrThrow	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;)J
    //   128: lstore_3
    //   129: aload 5
    //   131: invokevirtual 88	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   134: lload_3
    //   135: invokestatic 97	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   138: astore_1
    //   139: aload 5
    //   141: invokevirtual 91	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   144: aload_1
    //   145: areturn
    //   146: iconst_0
    //   147: istore_2
    //   148: goto -66 -> 82
    //   151: aload 5
    //   153: ldc 47
    //   155: aload 6
    //   157: new 27	java/lang/StringBuilder
    //   160: dup
    //   161: invokespecial 28	java/lang/StringBuilder:<init>	()V
    //   164: ldc -50
    //   166: invokevirtual 34	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   169: aload_1
    //   170: invokevirtual 248	org/telegram/ui/Mihangram/UserChanges/UpdateModel:getId	()Ljava/lang/Long;
    //   173: invokevirtual 199	java/lang/Long:longValue	()J
    //   176: invokevirtual 209	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   179: invokevirtual 41	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   182: aconst_null
    //   183: invokevirtual 213	android/database/sqlite/SQLiteDatabase:update	(Ljava/lang/String;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    //   186: pop
    //   187: aload 5
    //   189: invokevirtual 88	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   192: aload_1
    //   193: invokevirtual 248	org/telegram/ui/Mihangram/UserChanges/UpdateModel:getId	()Ljava/lang/Long;
    //   196: astore_1
    //   197: aload 5
    //   199: invokevirtual 91	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   202: goto -63 -> 139
    //   205: astore_1
    //   206: aload 5
    //   208: invokevirtual 91	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   211: aload_1
    //   212: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	213	0	this	a
    //   0	213	1	paramUpdateModel	UpdateModel
    //   81	67	2	i	int
    //   128	7	3	l	long
    //   7	200	5	localSQLiteDatabase	SQLiteDatabase
    //   21	135	6	localContentValues	ContentValues
    // Exception table:
    //   from	to	target	type
    //   14	80	205	finally
    //   82	111	205	finally
    //   111	134	205	finally
    //   151	202	205	finally
  }
  
  public Long a(org.telegram.ui.Mihangram.UserChanges.mobo.a.a parama)
  {
    SQLiteDatabase localSQLiteDatabase = this.a.getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    try
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("name", parama.b());
      localContentValues.put("priority", parama.c());
      if (parama.a() == null)
      {
        long l = localSQLiteDatabase.insertOrThrow("tbl_category", (String)null, localContentValues);
        localSQLiteDatabase.setTransactionSuccessful();
        if (0 != 0) {
          localSQLiteDatabase.endTransaction();
        }
        localSQLiteDatabase.endTransaction();
        return Long.valueOf(l);
      }
      localSQLiteDatabase.update("tbl_category", localContentValues, "_id=" + parama.a().longValue(), (String[])null);
      localSQLiteDatabase.setTransactionSuccessful();
      parama = parama.a();
      if (0 != 0) {
        localSQLiteDatabase.endTransaction();
      }
      localSQLiteDatabase.endTransaction();
      return parama;
    }
    finally
    {
      if (1 != 0) {
        localSQLiteDatabase.endTransaction();
      }
    }
  }
  
  public Long a(org.telegram.ui.Mihangram.UserChanges.mobo.c.a parama)
  {
    SQLiteDatabase localSQLiteDatabase = this.a.getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    try
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("chatID", parama.a());
      long l = localSQLiteDatabase.insertOrThrow("tbl_favorite", (String)null, localContentValues);
      localSQLiteDatabase.setTransactionSuccessful();
      return Long.valueOf(l);
    }
    finally
    {
      localSQLiteDatabase.endTransaction();
    }
  }
  
  public Long a(org.telegram.ui.Mihangram.UserChanges.mobo.e.a parama)
  {
    SQLiteDatabase localSQLiteDatabase = this.a.getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    try
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("dialogID", parama.a());
      long l = localSQLiteDatabase.insertOrThrow("tbl_hidden", (String)null, localContentValues);
      localSQLiteDatabase.setTransactionSuccessful();
      return Long.valueOf(l);
    }
    finally
    {
      localSQLiteDatabase.endTransaction();
    }
  }
  
  public Long a(org.telegram.ui.Mihangram.UserChanges.mobo.f.a parama)
  {
    SQLiteDatabase localSQLiteDatabase = this.a.getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    try
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("doc_id", parama.c());
      localContentValues.put("priority", parama.b());
      if (parama.a() == null)
      {
        long l = localSQLiteDatabase.insertOrThrow("tbl_favorite_stickers", (String)null, localContentValues);
        localSQLiteDatabase.setTransactionSuccessful();
        if (0 != 0) {
          localSQLiteDatabase.endTransaction();
        }
        localSQLiteDatabase.endTransaction();
        return Long.valueOf(l);
      }
      localSQLiteDatabase.update("tbl_favorite_stickers", localContentValues, "_id=" + parama.a().longValue(), (String[])null);
      localSQLiteDatabase.setTransactionSuccessful();
      parama = parama.a();
      if (0 != 0) {
        localSQLiteDatabase.endTransaction();
      }
      localSQLiteDatabase.endTransaction();
      return parama;
    }
    finally
    {
      if (1 != 0) {
        localSQLiteDatabase.endTransaction();
      }
    }
  }
  
  public List a(String paramString)
  {
    Object localObject = this.a.getReadableDatabase();
    ArrayList localArrayList = new ArrayList();
    try
    {
      localObject = ((SQLiteDatabase)localObject).query("tbl_favorite", null, paramString, null, null, null, "_id");
      for (;;)
      {
        boolean bool = ((Cursor)localObject).moveToNext();
        if (!bool) {
          break;
        }
        try
        {
          localArrayList.add(c((Cursor)localObject));
        }
        catch (Throwable paramString) {}
      }
      if (localObject != null) {
        ((Cursor)localObject).close();
      }
      return localArrayList;
    }
    catch (Throwable paramString)
    {
      if (0 != 0) {
        throw new NullPointerException();
      }
      throw paramString;
    }
  }
  
  public List a(String paramString, boolean paramBoolean)
  {
    Object localObject = this.a.getReadableDatabase();
    ArrayList localArrayList = new ArrayList();
    try
    {
      paramString = ((SQLiteDatabase)localObject).query("tbl_category", null, paramString, null, null, null, "priority");
      for (;;)
      {
        boolean bool = paramString.moveToNext();
        if (!bool) {
          break;
        }
        try
        {
          localObject = c(paramString);
          if (paramBoolean) {
            d().addAll(f(((org.telegram.ui.Mihangram.UserChanges.mobo.c.a)localObject).a()));
          }
          localArrayList.add(localObject);
        }
        catch (Throwable localThrowable) {}
      }
      if (paramString != null) {
        paramString.close();
      }
      return localArrayList;
    }
    catch (Throwable paramString)
    {
      if (0 != 0) {
        throw new NullPointerException();
      }
      throw paramString;
    }
  }
  
  /* Error */
  public AlarmResponse a(long paramLong)
  {
    // Byte code:
    //   0: new 27	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 28	java/lang/StringBuilder:<init>	()V
    //   7: ldc -50
    //   9: invokevirtual 34	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   12: lload_1
    //   13: invokevirtual 209	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   16: invokevirtual 41	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   19: astore 4
    //   21: aload_0
    //   22: getfield 23	org/telegram/ui/Mihangram/UserChanges/a:a	Lorg/telegram/ui/Mihangram/UserChanges/b;
    //   25: invokevirtual 45	org/telegram/ui/Mihangram/UserChanges/b:getReadableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   28: astore 5
    //   30: aload 5
    //   32: ldc -52
    //   34: aconst_null
    //   35: checkcast 49	[Ljava/lang/String;
    //   38: aload 4
    //   40: aconst_null
    //   41: checkcast 49	[Ljava/lang/String;
    //   44: aconst_null
    //   45: checkcast 51	java/lang/String
    //   48: aconst_null
    //   49: checkcast 51	java/lang/String
    //   52: ldc 106
    //   54: invokevirtual 302	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   57: astore 4
    //   59: iconst_0
    //   60: ifeq +7 -> 67
    //   63: iconst_0
    //   64: ifeq +3 -> 67
    //   67: aload 4
    //   69: invokeinterface 338 1 0
    //   74: istore_3
    //   75: iload_3
    //   76: ifne +30 -> 106
    //   79: aload 4
    //   81: ifnull +10 -> 91
    //   84: aload 4
    //   86: invokeinterface 319 1 0
    //   91: aconst_null
    //   92: areturn
    //   93: astore 4
    //   95: iconst_1
    //   96: ifeq +7 -> 103
    //   99: iconst_0
    //   100: ifeq +3 -> 103
    //   103: aload 4
    //   105: athrow
    //   106: aload_0
    //   107: aload 4
    //   109: invokevirtual 341	org/telegram/ui/Mihangram/UserChanges/a:b	(Landroid/database/Cursor;)Lorg/telegram/ui/Mihangram/UserChanges/AlarmResponse;
    //   112: astore 5
    //   114: aload 4
    //   116: ifnull +10 -> 126
    //   119: aload 4
    //   121: invokeinterface 319 1 0
    //   126: aload 5
    //   128: areturn
    //   129: astore 4
    //   131: aload 4
    //   133: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	134	0	this	a
    //   0	134	1	paramLong	long
    //   74	2	3	bool	boolean
    //   19	66	4	localObject1	Object
    //   93	27	4	localCursor	Cursor
    //   129	3	4	localObject2	Object
    //   28	99	5	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   30	59	93	finally
    //   67	75	129	finally
    //   106	114	129	finally
  }
  
  public UpdateModel a(Cursor paramCursor)
  {
    boolean bool2 = false;
    long l = paramCursor.getLong(paramCursor.getColumnIndex("_id"));
    int i = paramCursor.getInt(paramCursor.getColumnIndex("type"));
    String str1 = paramCursor.getString(paramCursor.getColumnIndex("old_value"));
    String str2 = paramCursor.getString(paramCursor.getColumnIndex("new_value"));
    int j = paramCursor.getInt(paramCursor.getColumnIndex("user_id"));
    boolean bool1 = bool2;
    if (!paramCursor.isNull(paramCursor.getColumnIndex("is_new")))
    {
      bool1 = bool2;
      if (paramCursor.getLong(paramCursor.getColumnIndex("is_new")) > 0L) {
        bool1 = true;
      }
    }
    return new UpdateModel(Long.valueOf(l), i, str1, str2, j, bool1, paramCursor.getString(paramCursor.getColumnIndex("change_date")));
  }
  
  public org.telegram.ui.Mihangram.UserChanges.mobo.a.a a(Long paramLong, boolean paramBoolean)
  {
    paramLong = a("_id=" + paramLong, paramBoolean);
    if (paramLong.size() > 0) {
      return (org.telegram.ui.Mihangram.UserChanges.mobo.a.a)paramLong.get(0);
    }
    return null;
  }
  
  public org.telegram.ui.Mihangram.UserChanges.mobo.c.a a(Long paramLong)
  {
    paramLong = a("chatID=" + paramLong);
    if (paramLong.size() > 0) {
      return (org.telegram.ui.Mihangram.UserChanges.mobo.c.a)paramLong.get(0);
    }
    return null;
  }
  
  public void a()
  {
    SQLiteDatabase localSQLiteDatabase = this.a.getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    try
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.putNull("is_new");
      localSQLiteDatabase.update("tbl_update", localContentValues, (String)null, (String[])null);
      localSQLiteDatabase.setTransactionSuccessful();
      return;
    }
    finally
    {
      localSQLiteDatabase.endTransaction();
    }
  }
  
  public void a(int paramInt)
  {
    SQLiteDatabase localSQLiteDatabase = this.a.getWritableDatabase();
    String str = "user_id = " + paramInt;
    localSQLiteDatabase.beginTransaction();
    try
    {
      localSQLiteDatabase.delete("tbl_update", str, (String[])null);
      localSQLiteDatabase.setTransactionSuccessful();
      return;
    }
    finally
    {
      localSQLiteDatabase.endTransaction();
    }
  }
  
  /* Error */
  public List b(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 23	org/telegram/ui/Mihangram/UserChanges/a:a	Lorg/telegram/ui/Mihangram/UserChanges/b;
    //   4: invokevirtual 45	org/telegram/ui/Mihangram/UserChanges/b:getReadableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   7: astore 4
    //   9: new 298	java/util/ArrayList
    //   12: dup
    //   13: invokespecial 299	java/util/ArrayList:<init>	()V
    //   16: astore_3
    //   17: aload 4
    //   19: ldc_w 281
    //   22: aconst_null
    //   23: checkcast 49	[Ljava/lang/String;
    //   26: aload_1
    //   27: aconst_null
    //   28: checkcast 49	[Ljava/lang/String;
    //   31: aconst_null
    //   32: checkcast 51	java/lang/String
    //   35: aconst_null
    //   36: checkcast 51	java/lang/String
    //   39: ldc 106
    //   41: invokevirtual 302	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   44: astore_1
    //   45: aload_1
    //   46: invokeinterface 307 1 0
    //   51: istore_2
    //   52: iload_2
    //   53: ifne +18 -> 71
    //   56: aload_1
    //   57: ifnull +9 -> 66
    //   60: aload_1
    //   61: invokeinterface 319 1 0
    //   66: aload_3
    //   67: areturn
    //   68: astore_1
    //   69: aload_1
    //   70: athrow
    //   71: aload_3
    //   72: aload_0
    //   73: aload_1
    //   74: invokevirtual 397	org/telegram/ui/Mihangram/UserChanges/a:d	(Landroid/database/Cursor;)Lorg/telegram/ui/Mihangram/UserChanges/mobo/e/a;
    //   77: invokevirtual 398	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   80: pop
    //   81: goto -36 -> 45
    //   84: astore_3
    //   85: aload_1
    //   86: ifnull +9 -> 95
    //   89: aload_1
    //   90: invokeinterface 319 1 0
    //   95: aload_3
    //   96: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	97	0	this	a
    //   0	97	1	paramString	String
    //   51	2	2	bool	boolean
    //   16	56	3	localArrayList	ArrayList
    //   84	12	3	localThrowable	Throwable
    //   7	11	4	localSQLiteDatabase	SQLiteDatabase
    // Exception table:
    //   from	to	target	type
    //   17	45	68	finally
    //   45	52	84	java/lang/Throwable
    //   71	81	84	java/lang/Throwable
  }
  
  /* Error */
  public AlarmResponse b(int paramInt)
  {
    // Byte code:
    //   0: new 27	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 28	java/lang/StringBuilder:<init>	()V
    //   7: ldc_w 401
    //   10: invokevirtual 34	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   13: iload_1
    //   14: invokevirtual 37	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   17: invokevirtual 41	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   20: astore_3
    //   21: aload_0
    //   22: getfield 23	org/telegram/ui/Mihangram/UserChanges/a:a	Lorg/telegram/ui/Mihangram/UserChanges/b;
    //   25: invokevirtual 45	org/telegram/ui/Mihangram/UserChanges/b:getReadableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   28: astore 4
    //   30: aload 4
    //   32: ldc -52
    //   34: aconst_null
    //   35: checkcast 49	[Ljava/lang/String;
    //   38: aload_3
    //   39: aconst_null
    //   40: checkcast 49	[Ljava/lang/String;
    //   43: aconst_null
    //   44: checkcast 51	java/lang/String
    //   47: aconst_null
    //   48: checkcast 51	java/lang/String
    //   51: ldc 106
    //   53: invokevirtual 302	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   56: astore_3
    //   57: iconst_0
    //   58: ifeq +7 -> 65
    //   61: iconst_0
    //   62: ifeq +3 -> 65
    //   65: aload_3
    //   66: invokeinterface 404 1 0
    //   71: istore_2
    //   72: iload_2
    //   73: ifne +26 -> 99
    //   76: aload_3
    //   77: ifnull +9 -> 86
    //   80: aload_3
    //   81: invokeinterface 319 1 0
    //   86: aconst_null
    //   87: areturn
    //   88: astore_3
    //   89: iconst_1
    //   90: ifeq +7 -> 97
    //   93: iconst_0
    //   94: ifeq +3 -> 97
    //   97: aload_3
    //   98: athrow
    //   99: aload_0
    //   100: aload_3
    //   101: invokevirtual 341	org/telegram/ui/Mihangram/UserChanges/a:b	(Landroid/database/Cursor;)Lorg/telegram/ui/Mihangram/UserChanges/AlarmResponse;
    //   104: astore 4
    //   106: aload_3
    //   107: ifnull +9 -> 116
    //   110: aload_3
    //   111: invokeinterface 319 1 0
    //   116: aload 4
    //   118: areturn
    //   119: astore_3
    //   120: aload_3
    //   121: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	122	0	this	a
    //   0	122	1	paramInt	int
    //   71	2	2	bool	boolean
    //   20	61	3	localObject1	Object
    //   88	23	3	localCursor	Cursor
    //   119	2	3	localObject2	Object
    //   28	89	4	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   30	57	88	finally
    //   65	72	119	finally
    //   99	106	119	finally
  }
  
  public AlarmResponse b(Cursor paramCursor)
  {
    long l = paramCursor.getLong(paramCursor.getColumnIndex("_id"));
    String str1 = paramCursor.getString(paramCursor.getColumnIndex("title"));
    String str2 = paramCursor.getString(paramCursor.getColumnIndex("message"));
    String str3 = paramCursor.getString(paramCursor.getColumnIndex("imageUrl"));
    String str4 = paramCursor.getString(paramCursor.getColumnIndex("positiveBtnText"));
    String str5 = paramCursor.getString(paramCursor.getColumnIndex("positiveBtnAction"));
    String str6 = paramCursor.getString(paramCursor.getColumnIndex("positiveBtnUrl"));
    String str7 = paramCursor.getString(paramCursor.getColumnIndex("negativeBtnText"));
    String str8 = paramCursor.getString(paramCursor.getColumnIndex("negativeBtnAction"));
    String str9 = paramCursor.getString(paramCursor.getColumnIndex("negativeBtnUrl"));
    int i = paramCursor.getInt(paramCursor.getColumnIndex("showCount"));
    boolean bool;
    if (paramCursor.isNull(paramCursor.getColumnIndex("exitOnDismiss"))) {
      bool = false;
    }
    for (;;)
    {
      return new AlarmResponse(Long.valueOf(l), str1, str2, str3, str4, str5, str6, str7, str8, str9, Integer.valueOf(i), Boolean.valueOf(bool), Integer.valueOf(paramCursor.getInt(paramCursor.getColumnIndex("targetNetwork"))), Integer.valueOf(paramCursor.getInt(paramCursor.getColumnIndex("displayCount"))), Integer.valueOf(paramCursor.getInt(paramCursor.getColumnIndex("targetVersion"))));
      if (paramCursor.getLong(paramCursor.getColumnIndex("exitOnDismiss")) > 0L) {
        bool = true;
      } else {
        bool = false;
      }
    }
  }
  
  public void b()
  {
    SQLiteDatabase localSQLiteDatabase = this.a.getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    try
    {
      localSQLiteDatabase.delete("tbl_update", (String)null, (String[])null);
      localSQLiteDatabase.setTransactionSuccessful();
      return;
    }
    finally
    {
      localSQLiteDatabase.endTransaction();
    }
  }
  
  public void b(Long paramLong)
  {
    SQLiteDatabase localSQLiteDatabase = this.a.getWritableDatabase();
    paramLong = "chatID = " + paramLong;
    localSQLiteDatabase.beginTransaction();
    try
    {
      localSQLiteDatabase.delete("tbl_favorite", paramLong, (String[])null);
      localSQLiteDatabase.setTransactionSuccessful();
      return;
    }
    finally
    {
      localSQLiteDatabase.endTransaction();
    }
  }
  
  public int c()
  {
    Object localObject = null;
    j = 0;
    int i = 0;
    try
    {
      Cursor localCursor = this.a.getReadableDatabase().query("tbl_update", null, "is_new=1", null, null, null, "_id");
      try
      {
        j = localCursor.getCount();
        if (localCursor != null)
        {
          i = j;
          localCursor.close();
        }
        return j;
      }
      catch (Throwable localThrowable2)
      {
        if (localCursor != null)
        {
          j = i;
          localObject = localCursor;
          localCursor.close();
        }
        j = i;
        localObject = localCursor;
        throw localThrowable2;
      }
      return j;
    }
    catch (Throwable localThrowable1)
    {
      if (localObject != null) {
        ((Cursor)localObject).close();
      }
    }
  }
  
  /* Error */
  public List c(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 23	org/telegram/ui/Mihangram/UserChanges/a:a	Lorg/telegram/ui/Mihangram/UserChanges/b;
    //   4: invokevirtual 45	org/telegram/ui/Mihangram/UserChanges/b:getReadableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   7: astore 4
    //   9: new 298	java/util/ArrayList
    //   12: dup
    //   13: invokespecial 299	java/util/ArrayList:<init>	()V
    //   16: astore_3
    //   17: aload 4
    //   19: ldc_w 293
    //   22: aconst_null
    //   23: checkcast 49	[Ljava/lang/String;
    //   26: aload_1
    //   27: aconst_null
    //   28: checkcast 49	[Ljava/lang/String;
    //   31: aconst_null
    //   32: checkcast 51	java/lang/String
    //   35: aconst_null
    //   36: checkcast 51	java/lang/String
    //   39: ldc_w 258
    //   42: invokevirtual 302	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   45: astore_1
    //   46: aload_1
    //   47: invokeinterface 307 1 0
    //   52: istore_2
    //   53: iload_2
    //   54: ifne +18 -> 72
    //   57: aload_1
    //   58: ifnull +9 -> 67
    //   61: aload_1
    //   62: invokeinterface 319 1 0
    //   67: aload_3
    //   68: areturn
    //   69: astore_1
    //   70: aload_1
    //   71: athrow
    //   72: aload_3
    //   73: aload_0
    //   74: aload_1
    //   75: invokevirtual 421	org/telegram/ui/Mihangram/UserChanges/a:f	(Landroid/database/Cursor;)Lorg/telegram/ui/Mihangram/UserChanges/mobo/f/a;
    //   78: invokevirtual 398	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   81: pop
    //   82: goto -36 -> 46
    //   85: astore_3
    //   86: aload_1
    //   87: ifnull +9 -> 96
    //   90: aload_1
    //   91: invokeinterface 319 1 0
    //   96: aload_3
    //   97: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	98	0	this	a
    //   0	98	1	paramString	String
    //   52	2	2	bool	boolean
    //   16	57	3	localArrayList	ArrayList
    //   85	12	3	localThrowable	Throwable
    //   7	11	4	localSQLiteDatabase	SQLiteDatabase
    // Exception table:
    //   from	to	target	type
    //   17	46	69	finally
    //   46	53	85	java/lang/Throwable
    //   72	82	85	java/lang/Throwable
  }
  
  public org.telegram.ui.Mihangram.UserChanges.mobo.c.a c(Cursor paramCursor)
  {
    return new org.telegram.ui.Mihangram.UserChanges.mobo.c.a(Long.valueOf(paramCursor.getLong(paramCursor.getColumnIndex("_id"))), Long.valueOf(paramCursor.getLong(paramCursor.getColumnIndex("chatID"))));
  }
  
  public org.telegram.ui.Mihangram.UserChanges.mobo.e.a c(Long paramLong)
  {
    paramLong = b("dialogID=" + paramLong);
    if (paramLong.size() > 0) {
      return (org.telegram.ui.Mihangram.UserChanges.mobo.e.a)paramLong.get(0);
    }
    return null;
  }
  
  public List d()
  {
    return a((String)null);
  }
  
  public org.telegram.ui.Mihangram.UserChanges.mobo.e.a d(Cursor paramCursor)
  {
    return new org.telegram.ui.Mihangram.UserChanges.mobo.e.a(Long.valueOf(paramCursor.getLong(paramCursor.getColumnIndex("_id"))), Long.valueOf(paramCursor.getLong(paramCursor.getColumnIndex("dialogID"))));
  }
  
  public void d(Long paramLong)
  {
    SQLiteDatabase localSQLiteDatabase = this.a.getWritableDatabase();
    paramLong = "dialogID = " + paramLong;
    localSQLiteDatabase.beginTransaction();
    try
    {
      localSQLiteDatabase.delete("tbl_hidden", paramLong, (String[])null);
      localSQLiteDatabase.setTransactionSuccessful();
      return;
    }
    finally
    {
      localSQLiteDatabase.endTransaction();
    }
  }
  
  public List e()
  {
    return b((String)null);
  }
  
  public org.telegram.ui.Mihangram.UserChanges.mobo.a.a e(Cursor paramCursor)
  {
    return new org.telegram.ui.Mihangram.UserChanges.mobo.a.a(Long.valueOf(paramCursor.getLong(paramCursor.getColumnIndex("_id"))), paramCursor.getString(paramCursor.getColumnIndex("name")), Integer.valueOf(paramCursor.getInt(paramCursor.getColumnIndex("priority"))));
  }
  
  public void e(Long paramLong)
  {
    SQLiteDatabase localSQLiteDatabase = this.a.getWritableDatabase();
    paramLong = "_id = " + paramLong;
    localSQLiteDatabase.beginTransaction();
    try
    {
      localSQLiteDatabase.delete("tbl_category", paramLong, (String[])null);
      localSQLiteDatabase.setTransactionSuccessful();
      return;
    }
    finally
    {
      localSQLiteDatabase.endTransaction();
    }
  }
  
  public List f(Long paramLong)
  {
    Object localObject = this.a.getReadableDatabase();
    ArrayList localArrayList = new ArrayList();
    try
    {
      localObject = ((SQLiteDatabase)localObject).query("tbl_cat_dlg_info", null, "categoryId=" + paramLong, null, null, null, "_id");
      for (;;)
      {
        boolean bool = ((Cursor)localObject).moveToNext();
        if (!bool) {
          break;
        }
        try
        {
          localArrayList.add(Long.valueOf(((Cursor)localObject).getLong(((Cursor)localObject).getColumnIndex("dialogId"))));
        }
        catch (Throwable paramLong) {}
      }
      if (localObject != null) {
        ((Cursor)localObject).close();
      }
      return localArrayList;
    }
    catch (Throwable paramLong)
    {
      if (0 != 0) {
        throw new NullPointerException();
      }
      throw paramLong;
    }
  }
  
  public org.telegram.ui.Mihangram.UserChanges.mobo.f.a f(Cursor paramCursor)
  {
    return new org.telegram.ui.Mihangram.UserChanges.mobo.f.a(Long.valueOf(paramCursor.getLong(paramCursor.getColumnIndex("_id"))), Long.valueOf(paramCursor.getLong(paramCursor.getColumnIndex("doc_id"))), Integer.valueOf(paramCursor.getInt(paramCursor.getColumnIndex("priority"))));
  }
  
  public void f()
  {
    SQLiteDatabase localSQLiteDatabase = this.a.getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    try
    {
      localSQLiteDatabase.delete("tbl_hidden", (String)null, (String[])null);
      localSQLiteDatabase.setTransactionSuccessful();
      return;
    }
    finally
    {
      localSQLiteDatabase.endTransaction();
    }
  }
  
  public List g()
  {
    return a((String)null, false);
  }
  
  public void g(Long paramLong)
  {
    SQLiteDatabase localSQLiteDatabase = this.a.getWritableDatabase();
    paramLong = "dialogId = " + paramLong;
    localSQLiteDatabase.beginTransaction();
    try
    {
      localSQLiteDatabase.delete("tbl_cat_dlg_info", paramLong, (String[])null);
      localSQLiteDatabase.setTransactionSuccessful();
      return;
    }
    finally
    {
      localSQLiteDatabase.endTransaction();
    }
  }
  
  /* Error */
  public int h()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 23	org/telegram/ui/Mihangram/UserChanges/a:a	Lorg/telegram/ui/Mihangram/UserChanges/b;
    //   4: invokevirtual 45	org/telegram/ui/Mihangram/UserChanges/b:getReadableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   7: astore_2
    //   8: aload_2
    //   9: ldc_w 265
    //   12: aconst_null
    //   13: checkcast 49	[Ljava/lang/String;
    //   16: aconst_null
    //   17: checkcast 51	java/lang/String
    //   20: aconst_null
    //   21: checkcast 49	[Ljava/lang/String;
    //   24: aconst_null
    //   25: checkcast 51	java/lang/String
    //   28: aconst_null
    //   29: checkcast 51	java/lang/String
    //   32: ldc 106
    //   34: invokevirtual 302	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   37: astore_2
    //   38: aload_2
    //   39: invokeinterface 418 1 0
    //   44: istore_1
    //   45: aload_2
    //   46: ifnull +9 -> 55
    //   49: aload_2
    //   50: invokeinterface 319 1 0
    //   55: iload_1
    //   56: ireturn
    //   57: astore_2
    //   58: iconst_0
    //   59: ifeq +12 -> 71
    //   62: aconst_null
    //   63: checkcast 304	android/database/Cursor
    //   66: invokeinterface 319 1 0
    //   71: aload_2
    //   72: athrow
    //   73: astore_2
    //   74: aload_2
    //   75: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	76	0	this	a
    //   44	12	1	i	int
    //   7	43	2	localObject1	Object
    //   57	15	2	localThrowable	Throwable
    //   73	2	2	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   8	38	57	java/lang/Throwable
    //   38	45	73	finally
  }
  
  public boolean h(Long paramLong)
  {
    return i(paramLong) != null;
  }
  
  /* Error */
  public Long i(Long paramLong)
  {
    // Byte code:
    //   0: new 27	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 28	java/lang/StringBuilder:<init>	()V
    //   7: ldc_w 447
    //   10: invokevirtual 34	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   13: aload_1
    //   14: invokevirtual 369	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   17: invokevirtual 41	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   20: astore_1
    //   21: aload_0
    //   22: getfield 23	org/telegram/ui/Mihangram/UserChanges/a:a	Lorg/telegram/ui/Mihangram/UserChanges/b;
    //   25: invokevirtual 45	org/telegram/ui/Mihangram/UserChanges/b:getReadableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   28: astore 5
    //   30: aload 5
    //   32: ldc 81
    //   34: aconst_null
    //   35: checkcast 49	[Ljava/lang/String;
    //   38: aload_1
    //   39: aconst_null
    //   40: checkcast 49	[Ljava/lang/String;
    //   43: aconst_null
    //   44: checkcast 51	java/lang/String
    //   47: aconst_null
    //   48: checkcast 51	java/lang/String
    //   51: ldc 106
    //   53: invokevirtual 302	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   56: astore_1
    //   57: iconst_0
    //   58: ifeq +16 -> 74
    //   61: iconst_0
    //   62: ifeq +12 -> 74
    //   65: aconst_null
    //   66: checkcast 304	android/database/Cursor
    //   69: invokeinterface 319 1 0
    //   74: aload_1
    //   75: invokeinterface 307 1 0
    //   80: istore_2
    //   81: iload_2
    //   82: ifne +35 -> 117
    //   85: aload_1
    //   86: ifnull +9 -> 95
    //   89: aload_1
    //   90: invokeinterface 319 1 0
    //   95: aconst_null
    //   96: areturn
    //   97: astore_1
    //   98: iconst_1
    //   99: ifeq +16 -> 115
    //   102: iconst_0
    //   103: ifeq +12 -> 115
    //   106: aconst_null
    //   107: checkcast 304	android/database/Cursor
    //   110: invokeinterface 319 1 0
    //   115: aload_1
    //   116: athrow
    //   117: aload_1
    //   118: aload_1
    //   119: ldc 73
    //   121: invokeinterface 346 2 0
    //   126: invokeinterface 350 2 0
    //   131: lstore_3
    //   132: lload_3
    //   133: invokestatic 97	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   136: astore 5
    //   138: aload_1
    //   139: ifnull +9 -> 148
    //   142: aload_1
    //   143: invokeinterface 319 1 0
    //   148: aload 5
    //   150: areturn
    //   151: astore_1
    //   152: aload_1
    //   153: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	154	0	this	a
    //   0	154	1	paramLong	Long
    //   80	2	2	bool	boolean
    //   131	2	3	l	long
    //   28	121	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   30	57	97	finally
    //   74	81	151	finally
    //   117	132	151	finally
  }
  
  public List i()
  {
    return a((String)null, true);
  }
  
  public List j()
  {
    SQLiteDatabase localSQLiteDatabase = this.a.getReadableDatabase();
    ArrayList localArrayList = new ArrayList();
    try
    {
      Cursor localCursor = localSQLiteDatabase.query("tbl_cat_dlg_info", null, null, null, null, null, "_id");
      for (;;)
      {
        boolean bool = localCursor.moveToNext();
        if (!bool) {
          break;
        }
        try
        {
          localArrayList.add(Long.valueOf(localCursor.getLong(localCursor.getColumnIndex("dialogId"))));
        }
        catch (Throwable localThrowable2) {}
      }
      if (localCursor != null) {
        localCursor.close();
      }
      return localArrayList;
    }
    catch (Throwable localThrowable1)
    {
      if (0 != 0) {
        throw new NullPointerException();
      }
      throw localThrowable1;
    }
  }
  
  public void j(Long paramLong)
  {
    SQLiteDatabase localSQLiteDatabase = this.a.getWritableDatabase();
    paramLong = "doc_id = " + paramLong;
    localSQLiteDatabase.beginTransaction();
    try
    {
      localSQLiteDatabase.delete("tbl_favorite_stickers", paramLong, (String[])null);
      localSQLiteDatabase.setTransactionSuccessful();
      return;
    }
    finally
    {
      localSQLiteDatabase.endTransaction();
    }
  }
  
  public List k()
  {
    return c((String)null);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\UserChanges\a.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */