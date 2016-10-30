package org.telegram.messenger.query;

import android.content.Context;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_messages_allStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getAllStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_installStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_uninstallStickerSet;
import org.telegram.tgnet.TLRPC.TL_stickerPack;

public class StickersQuery
{
  private static HashMap<String, ArrayList<TLRPC.Document>> allStickers = new HashMap();
  private static int loadDate;
  private static int loadHash;
  private static boolean loadingStickers;
  private static ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = new ArrayList();
  private static HashMap<Long, TLRPC.TL_messages_stickerSet> stickerSetsById = new HashMap();
  private static HashMap<String, TLRPC.TL_messages_stickerSet> stickerSetsByName = new HashMap();
  private static HashMap<Long, String> stickersByEmoji = new HashMap();
  private static HashMap<Long, TLRPC.Document> stickersById = new HashMap();
  private static boolean stickersLoaded;
  
  public static void addNewStickerSet(TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet)
  {
    if ((stickerSetsById.containsKey(Long.valueOf(paramTL_messages_stickerSet.set.id))) || (stickerSetsByName.containsKey(paramTL_messages_stickerSet.set.short_name))) {
      return;
    }
    stickerSets.add(0, paramTL_messages_stickerSet);
    stickerSetsById.put(Long.valueOf(paramTL_messages_stickerSet.set.id), paramTL_messages_stickerSet);
    stickerSetsByName.put(paramTL_messages_stickerSet.set.short_name, paramTL_messages_stickerSet);
    int i = 0;
    Object localObject1;
    while (i < paramTL_messages_stickerSet.documents.size())
    {
      localObject1 = (TLRPC.Document)paramTL_messages_stickerSet.documents.get(i);
      stickersById.put(Long.valueOf(((TLRPC.Document)localObject1).id), localObject1);
      i += 1;
    }
    i = 0;
    while (i < paramTL_messages_stickerSet.packs.size())
    {
      TLRPC.TL_stickerPack localTL_stickerPack = (TLRPC.TL_stickerPack)paramTL_messages_stickerSet.packs.get(i);
      localTL_stickerPack.emoticon = localTL_stickerPack.emoticon.replace("️", "");
      Object localObject2 = (ArrayList)allStickers.get(localTL_stickerPack.emoticon);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new ArrayList();
        allStickers.put(localTL_stickerPack.emoticon, localObject1);
      }
      int j = 0;
      while (j < localTL_stickerPack.documents.size())
      {
        localObject2 = (Long)localTL_stickerPack.documents.get(j);
        if (!stickersByEmoji.containsKey(localObject2)) {
          stickersByEmoji.put(localObject2, localTL_stickerPack.emoticon);
        }
        localObject2 = (TLRPC.Document)stickersById.get(localObject2);
        if (localObject2 != null) {
          ((ArrayList)localObject1).add(localObject2);
        }
        j += 1;
      }
      i += 1;
    }
    loadHash = calcStickersHash(stickerSets);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[0]);
    loadStickers(false, true);
  }
  
  public static void calcNewHash()
  {
    loadHash = calcStickersHash(stickerSets);
  }
  
  private static int calcStickersHash(ArrayList<TLRPC.TL_messages_stickerSet> paramArrayList)
  {
    long l = 0L;
    int i = 0;
    if (i < paramArrayList.size())
    {
      TLRPC.StickerSet localStickerSet = ((TLRPC.TL_messages_stickerSet)paramArrayList.get(i)).set;
      if (localStickerSet.disabled) {}
      for (;;)
      {
        i += 1;
        break;
        l = (20261L * l + 2147483648L + localStickerSet.hash) % 2147483648L;
      }
    }
    return (int)l;
  }
  
  public static void checkStickers()
  {
    if ((!loadingStickers) && ((!stickersLoaded) || (Math.abs(System.currentTimeMillis() / 1000L - loadDate) >= 3600L))) {
      loadStickers(true, false);
    }
  }
  
  public static void cleanup()
  {
    loadHash = 0;
    loadDate = 0;
    allStickers.clear();
    stickerSets.clear();
    stickersByEmoji.clear();
    stickerSetsById.clear();
    stickerSetsByName.clear();
    loadingStickers = false;
    stickersLoaded = false;
  }
  
  public static HashMap<String, ArrayList<TLRPC.Document>> getAllStickers()
  {
    return allStickers;
  }
  
  public static String getEmojiForSticker(long paramLong)
  {
    String str = (String)stickersByEmoji.get(Long.valueOf(paramLong));
    if (str != null) {
      return str;
    }
    return "";
  }
  
  public static TLRPC.Document getStickerById(long paramLong)
  {
    TLRPC.Document localDocument2 = (TLRPC.Document)stickersById.get(Long.valueOf(paramLong));
    TLRPC.Document localDocument1 = localDocument2;
    if (localDocument2 != null)
    {
      paramLong = getStickerSetId(localDocument2);
      TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)stickerSetsById.get(Long.valueOf(paramLong));
      localDocument1 = localDocument2;
      if (localTL_messages_stickerSet != null)
      {
        localDocument1 = localDocument2;
        if (localTL_messages_stickerSet.set.disabled) {
          localDocument1 = null;
        }
      }
    }
    return localDocument1;
  }
  
  public static TLRPC.TL_messages_stickerSet getStickerSetById(Long paramLong)
  {
    return (TLRPC.TL_messages_stickerSet)stickerSetsById.get(paramLong);
  }
  
  public static TLRPC.TL_messages_stickerSet getStickerSetByName(String paramString)
  {
    return (TLRPC.TL_messages_stickerSet)stickerSetsByName.get(paramString);
  }
  
  public static long getStickerSetId(TLRPC.Document paramDocument)
  {
    int i = 0;
    while (i < paramDocument.attributes.size())
    {
      TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
      if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker))
      {
        if (!(localDocumentAttribute.stickerset instanceof TLRPC.TL_inputStickerSetID)) {
          break;
        }
        return localDocumentAttribute.stickerset.id;
      }
      i += 1;
    }
    return -1L;
  }
  
  public static String getStickerSetName(long paramLong)
  {
    TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)stickerSetsById.get(Long.valueOf(paramLong));
    if (localTL_messages_stickerSet != null) {
      return localTL_messages_stickerSet.set.short_name;
    }
    return null;
  }
  
  public static ArrayList<TLRPC.TL_messages_stickerSet> getStickerSets()
  {
    return stickerSets;
  }
  
  public static boolean isLoadingStickers()
  {
    return loadingStickers;
  }
  
  public static boolean isStickerPackInstalled(long paramLong)
  {
    return stickerSetsById.containsKey(Long.valueOf(paramLong));
  }
  
  public static boolean isStickerPackInstalled(String paramString)
  {
    return stickerSetsByName.containsKey(paramString);
  }
  
  public static void loadStickers(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (loadingStickers) {
      return;
    }
    loadingStickers = true;
    if (paramBoolean1)
    {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        /* Error */
        public void run()
        {
          // Byte code:
          //   0: aconst_null
          //   1: astore 9
          //   3: aconst_null
          //   4: astore 12
          //   6: aconst_null
          //   7: astore 13
          //   9: iconst_0
          //   10: istore 4
          //   12: iconst_0
          //   13: istore 5
          //   15: iconst_0
          //   16: istore_2
          //   17: iconst_0
          //   18: istore 6
          //   20: iconst_0
          //   21: istore_3
          //   22: aconst_null
          //   23: astore 8
          //   25: aconst_null
          //   26: astore 10
          //   28: iload 4
          //   30: istore_1
          //   31: aload 12
          //   33: astore 11
          //   35: invokestatic 25	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
          //   38: invokevirtual 29	org/telegram/messenger/MessagesStorage:getDatabase	()Lorg/telegram/SQLite/SQLiteDatabase;
          //   41: ldc 31
          //   43: iconst_0
          //   44: anewarray 4	java/lang/Object
          //   47: invokevirtual 37	org/telegram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/telegram/SQLite/SQLiteCursor;
          //   50: astore 7
          //   52: aload 7
          //   54: astore 10
          //   56: iload 4
          //   58: istore_1
          //   59: aload 12
          //   61: astore 11
          //   63: aload 7
          //   65: astore 8
          //   67: aload 7
          //   69: invokevirtual 43	org/telegram/SQLite/SQLiteCursor:next	()Z
          //   72: ifeq +145 -> 217
          //   75: aload 7
          //   77: astore 10
          //   79: iload 4
          //   81: istore_1
          //   82: aload 12
          //   84: astore 11
          //   86: aload 7
          //   88: astore 8
          //   90: aload 7
          //   92: iconst_0
          //   93: invokevirtual 47	org/telegram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/telegram/tgnet/NativeByteBuffer;
          //   96: astore 14
          //   98: aload 13
          //   100: astore 9
          //   102: aload 14
          //   104: ifnull +71 -> 175
          //   107: aload 7
          //   109: astore 10
          //   111: iload 4
          //   113: istore_1
          //   114: aload 12
          //   116: astore 11
          //   118: aload 7
          //   120: astore 8
          //   122: new 49	java/util/ArrayList
          //   125: dup
          //   126: invokespecial 50	java/util/ArrayList:<init>	()V
          //   129: astore 9
          //   131: aload 14
          //   133: iconst_0
          //   134: invokevirtual 56	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
          //   137: istore_2
          //   138: iconst_0
          //   139: istore_1
          //   140: iload_1
          //   141: iload_2
          //   142: if_icmpge +28 -> 170
          //   145: aload 9
          //   147: aload 14
          //   149: aload 14
          //   151: iconst_0
          //   152: invokevirtual 56	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
          //   155: iconst_0
          //   156: invokestatic 62	org/telegram/tgnet/TLRPC$TL_messages_stickerSet:TLdeserialize	(Lorg/telegram/tgnet/AbstractSerializedData;IZ)Lorg/telegram/tgnet/TLRPC$TL_messages_stickerSet;
          //   159: invokevirtual 66	java/util/ArrayList:add	(Ljava/lang/Object;)Z
          //   162: pop
          //   163: iload_1
          //   164: iconst_1
          //   165: iadd
          //   166: istore_1
          //   167: goto -27 -> 140
          //   170: aload 14
          //   172: invokevirtual 69	org/telegram/tgnet/NativeByteBuffer:reuse	()V
          //   175: aload 7
          //   177: astore 10
          //   179: iload 4
          //   181: istore_1
          //   182: aload 9
          //   184: astore 11
          //   186: aload 7
          //   188: astore 8
          //   190: aload 7
          //   192: iconst_1
          //   193: invokevirtual 73	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
          //   196: istore_2
          //   197: aload 7
          //   199: astore 10
          //   201: iload_2
          //   202: istore_1
          //   203: aload 9
          //   205: astore 11
          //   207: aload 7
          //   209: astore 8
          //   211: aload 9
          //   213: invokestatic 77	org/telegram/messenger/query/StickersQuery:access$000	(Ljava/util/ArrayList;)I
          //   216: istore_3
          //   217: iload_2
          //   218: istore 4
          //   220: iload_3
          //   221: istore 5
          //   223: aload 9
          //   225: astore 8
          //   227: aload 7
          //   229: ifnull +18 -> 247
          //   232: aload 7
          //   234: invokevirtual 80	org/telegram/SQLite/SQLiteCursor:dispose	()V
          //   237: aload 9
          //   239: astore 8
          //   241: iload_3
          //   242: istore 5
          //   244: iload_2
          //   245: istore 4
          //   247: aload 8
          //   249: iconst_1
          //   250: iload 4
          //   252: iload 5
          //   254: invokestatic 84	org/telegram/messenger/query/StickersQuery:access$100	(Ljava/util/ArrayList;ZII)V
          //   257: return
          //   258: astore 9
          //   260: aload 10
          //   262: astore 7
          //   264: aload 7
          //   266: astore 8
          //   268: ldc 86
          //   270: aload 9
          //   272: invokestatic 92	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
          //   275: iload_1
          //   276: istore 4
          //   278: iload 6
          //   280: istore 5
          //   282: aload 11
          //   284: astore 8
          //   286: aload 7
          //   288: ifnull -41 -> 247
          //   291: aload 7
          //   293: invokevirtual 80	org/telegram/SQLite/SQLiteCursor:dispose	()V
          //   296: iload_1
          //   297: istore 4
          //   299: iload 6
          //   301: istore 5
          //   303: aload 11
          //   305: astore 8
          //   307: goto -60 -> 247
          //   310: astore 9
          //   312: aload 8
          //   314: astore 7
          //   316: aload 7
          //   318: ifnull +8 -> 326
          //   321: aload 7
          //   323: invokevirtual 80	org/telegram/SQLite/SQLiteCursor:dispose	()V
          //   326: aload 9
          //   328: athrow
          //   329: astore 9
          //   331: goto -15 -> 316
          //   334: astore 8
          //   336: aload 9
          //   338: astore 11
          //   340: iload 5
          //   342: istore_1
          //   343: aload 8
          //   345: astore 9
          //   347: goto -83 -> 264
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	350	0	this	2
          //   30	313	1	i	int
          //   16	229	2	j	int
          //   21	221	3	k	int
          //   10	288	4	m	int
          //   13	328	5	n	int
          //   18	282	6	i1	int
          //   50	272	7	localObject1	Object
          //   23	290	8	localObject2	Object
          //   334	10	8	localThrowable1	Throwable
          //   1	237	9	localObject3	Object
          //   258	13	9	localThrowable2	Throwable
          //   310	17	9	localObject4	Object
          //   329	8	9	localObject5	Object
          //   345	1	9	localObject6	Object
          //   26	235	10	localObject7	Object
          //   33	306	11	localObject8	Object
          //   4	111	12	localObject9	Object
          //   7	92	13	localObject10	Object
          //   96	75	14	localNativeByteBuffer	NativeByteBuffer
          // Exception table:
          //   from	to	target	type
          //   35	52	258	java/lang/Throwable
          //   67	75	258	java/lang/Throwable
          //   90	98	258	java/lang/Throwable
          //   122	131	258	java/lang/Throwable
          //   190	197	258	java/lang/Throwable
          //   211	217	258	java/lang/Throwable
          //   35	52	310	finally
          //   67	75	310	finally
          //   90	98	310	finally
          //   122	131	310	finally
          //   190	197	310	finally
          //   211	217	310	finally
          //   268	275	310	finally
          //   131	138	329	finally
          //   145	163	329	finally
          //   170	175	329	finally
          //   131	138	334	java/lang/Throwable
          //   145	163	334	java/lang/Throwable
          //   170	175	334	java/lang/Throwable
        }
      });
      return;
    }
    TLRPC.TL_messages_getAllStickers localTL_messages_getAllStickers = new TLRPC.TL_messages_getAllStickers();
    if (paramBoolean2) {}
    for (int i = 0;; i = loadHash)
    {
      localTL_messages_getAllStickers.hash = i;
      ConnectionsManager.getInstance().sendRequest(localTL_messages_getAllStickers, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if ((paramAnonymousTLObject instanceof TLRPC.TL_messages_allStickers))
              {
                final HashMap localHashMap = new HashMap();
                final ArrayList localArrayList = new ArrayList();
                final TLRPC.TL_messages_allStickers localTL_messages_allStickers = (TLRPC.TL_messages_allStickers)paramAnonymousTLObject;
                final int i = 0;
                if (i < localTL_messages_allStickers.sets.size())
                {
                  final TLRPC.StickerSet localStickerSet = (TLRPC.StickerSet)localTL_messages_allStickers.sets.get(i);
                  Object localObject = (TLRPC.TL_messages_stickerSet)StickersQuery.stickerSetsById.get(Long.valueOf(localStickerSet.id));
                  if ((localObject != null) && (((TLRPC.TL_messages_stickerSet)localObject).set.hash == localStickerSet.hash))
                  {
                    ((TLRPC.TL_messages_stickerSet)localObject).set.disabled = localStickerSet.disabled;
                    ((TLRPC.TL_messages_stickerSet)localObject).set.installed = localStickerSet.installed;
                    ((TLRPC.TL_messages_stickerSet)localObject).set.official = localStickerSet.official;
                    localHashMap.put(Long.valueOf(((TLRPC.TL_messages_stickerSet)localObject).set.id), localObject);
                    localArrayList.add(localObject);
                    if (localHashMap.size() == localTL_messages_allStickers.sets.size()) {
                      StickersQuery.processLoadedStickers(localArrayList, false, (int)(System.currentTimeMillis() / 1000L), localTL_messages_allStickers.hash);
                    }
                  }
                  for (;;)
                  {
                    i += 1;
                    break;
                    localArrayList.add(null);
                    localObject = new TLRPC.TL_messages_getStickerSet();
                    ((TLRPC.TL_messages_getStickerSet)localObject).stickerset = new TLRPC.TL_inputStickerSetID();
                    ((TLRPC.TL_messages_getStickerSet)localObject).stickerset.id = localStickerSet.id;
                    ((TLRPC.TL_messages_getStickerSet)localObject).stickerset.access_hash = localStickerSet.access_hash;
                    ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
                    {
                      public void run(final TLObject paramAnonymous3TLObject, TLRPC.TL_error paramAnonymous3TL_error)
                      {
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                          public void run()
                          {
                            TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)paramAnonymous3TLObject;
                            StickersQuery.3.1.1.this.val$newStickerArray.set(StickersQuery.3.1.1.this.val$index, localTL_messages_stickerSet);
                            StickersQuery.3.1.1.this.val$newStickerSets.put(Long.valueOf(StickersQuery.3.1.1.this.val$stickerSet.id), localTL_messages_stickerSet);
                            if (StickersQuery.3.1.1.this.val$newStickerSets.size() == StickersQuery.3.1.1.this.val$res.sets.size()) {
                              StickersQuery.processLoadedStickers(StickersQuery.3.1.1.this.val$newStickerArray, false, (int)(System.currentTimeMillis() / 1000L), StickersQuery.3.1.1.this.val$res.hash);
                            }
                          }
                        });
                      }
                    });
                  }
                }
              }
              else
              {
                StickersQuery.processLoadedStickers(null, false, (int)(System.currentTimeMillis() / 1000L), StickersQuery.3.this.val$req.hash);
              }
            }
          });
        }
      });
      return;
    }
  }
  
  private static void processLoadedStickers(final ArrayList<TLRPC.TL_messages_stickerSet> paramArrayList, boolean paramBoolean, final int paramInt1, final int paramInt2)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        StickersQuery.access$302(false);
        StickersQuery.access$402(true);
      }
    });
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject1;
        if (((this.val$cache) && ((paramArrayList == null) || (Math.abs(System.currentTimeMillis() / 1000L - paramInt1) >= 3600L))) || ((!this.val$cache) && (paramArrayList == null) && (paramInt2 == 0)))
        {
          localObject1 = new Runnable()
          {
            public void run()
            {
              if ((StickersQuery.6.this.val$res != null) && (StickersQuery.6.this.val$hash != 0)) {
                StickersQuery.access$502(StickersQuery.6.this.val$hash);
              }
              StickersQuery.loadStickers(false, false);
            }
          };
          if ((paramArrayList == null) && (!this.val$cache)) {}
          for (long l = 1000L;; l = 0L)
          {
            AndroidUtilities.runOnUIThread((Runnable)localObject1, l);
            if (paramArrayList != null) {
              break;
            }
            return;
          }
        }
        final ArrayList localArrayList;
        final HashMap localHashMap1;
        final HashMap localHashMap2;
        final HashMap localHashMap3;
        final HashMap localHashMap4;
        final HashMap localHashMap5;
        int i;
        label171:
        int j;
        label248:
        label332:
        TLRPC.TL_stickerPack localTL_stickerPack;
        Object localObject3;
        Object localObject2;
        if (paramArrayList != null)
        {
          TLRPC.TL_messages_stickerSet localTL_messages_stickerSet;
          try
          {
            localArrayList = new ArrayList();
            localHashMap1 = new HashMap();
            localHashMap2 = new HashMap();
            localHashMap3 = new HashMap();
            localHashMap4 = new HashMap();
            localHashMap5 = new HashMap();
            i = 0;
            if (i >= paramArrayList.size()) {
              break label517;
            }
            localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)paramArrayList.get(i);
            if (localTL_messages_stickerSet == null) {
              break label589;
            }
            localArrayList.add(localTL_messages_stickerSet);
            localHashMap1.put(Long.valueOf(localTL_messages_stickerSet.set.id), localTL_messages_stickerSet);
            localHashMap2.put(localTL_messages_stickerSet.set.short_name, localTL_messages_stickerSet);
            j = 0;
            if (j < localTL_messages_stickerSet.documents.size())
            {
              localObject1 = (TLRPC.Document)localTL_messages_stickerSet.documents.get(j);
              if ((localObject1 == null) || ((localObject1 instanceof TLRPC.TL_documentEmpty))) {
                break label596;
              }
              localHashMap4.put(Long.valueOf(((TLRPC.Document)localObject1).id), localObject1);
            }
          }
          catch (Throwable localThrowable)
          {
            FileLog.e("tmessages", localThrowable);
            return;
          }
          if (localTL_messages_stickerSet.set.disabled) {
            break label589;
          }
          j = 0;
          if (j >= localTL_messages_stickerSet.packs.size()) {
            break label589;
          }
          localTL_stickerPack = (TLRPC.TL_stickerPack)localTL_messages_stickerSet.packs.get(j);
          if ((localTL_stickerPack == null) || (localTL_stickerPack.emoticon == null)) {
            break label603;
          }
          localTL_stickerPack.emoticon = localTL_stickerPack.emoticon.replace("️", "");
          localObject3 = (ArrayList)localHashMap5.get(localTL_stickerPack.emoticon);
          localObject2 = localObject3;
          if (localObject3 != null) {
            break label610;
          }
          localObject2 = new ArrayList();
          localHashMap5.put(localTL_stickerPack.emoticon, localObject2);
          break label610;
        }
        for (;;)
        {
          if (k < localTL_stickerPack.documents.size())
          {
            localObject3 = (Long)localTL_stickerPack.documents.get(k);
            if (!localHashMap3.containsKey(localObject3)) {
              localHashMap3.put(localObject3, localTL_stickerPack.emoticon);
            }
            localObject3 = (TLRPC.Document)localHashMap4.get(localObject3);
            if (localObject3 == null) {
              break label615;
            }
            ((ArrayList)localObject2).add(localObject3);
            break label615;
            label517:
            if (!this.val$cache) {
              StickersQuery.putStickersToCache(localArrayList, paramInt1, paramInt2);
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                StickersQuery.access$702(localHashMap4);
                StickersQuery.access$202(localHashMap1);
                StickersQuery.access$802(localHashMap2);
                StickersQuery.access$902(localArrayList);
                StickersQuery.access$1002(localHashMap5);
                StickersQuery.access$1102(localHashMap3);
                StickersQuery.access$502(StickersQuery.6.this.val$hash);
                StickersQuery.access$1202(StickersQuery.6.this.val$date);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[0]);
              }
            });
            return;
            if (this.val$cache) {
              break;
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                StickersQuery.access$1202(StickersQuery.6.this.val$date);
              }
            });
            StickersQuery.putStickersToCache(null, paramInt1, 0);
            return;
            label589:
            i += 1;
            break label171;
            label596:
            j += 1;
            break label248;
          }
          label603:
          j += 1;
          break label332;
          label610:
          int k = 0;
          continue;
          label615:
          k += 1;
        }
      }
    });
  }
  
  private static void putStickersToCache(ArrayList<TLRPC.TL_messages_stickerSet> paramArrayList, final int paramInt1, final int paramInt2)
  {
    if (paramArrayList != null) {}
    for (paramArrayList = new ArrayList(paramArrayList);; paramArrayList = null)
    {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            if (this.val$stickersFinal != null)
            {
              localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
              localSQLitePreparedStatement.requery();
              int j = 4;
              int i = 0;
              while (i < this.val$stickersFinal.size())
              {
                j += ((TLRPC.TL_messages_stickerSet)this.val$stickersFinal.get(i)).getObjectSize();
                i += 1;
              }
              NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(j);
              localNativeByteBuffer.writeInt32(this.val$stickersFinal.size());
              i = 0;
              while (i < this.val$stickersFinal.size())
              {
                ((TLRPC.TL_messages_stickerSet)this.val$stickersFinal.get(i)).serializeToStream(localNativeByteBuffer);
                i += 1;
              }
              localSQLitePreparedStatement.bindInteger(1, 1);
              localSQLitePreparedStatement.bindByteBuffer(2, localNativeByteBuffer);
              localSQLitePreparedStatement.bindInteger(3, paramInt1);
              localSQLitePreparedStatement.bindInteger(4, paramInt2);
              localSQLitePreparedStatement.step();
              localNativeByteBuffer.reuse();
              localSQLitePreparedStatement.dispose();
              return;
            }
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
            localSQLitePreparedStatement.requery();
            localSQLitePreparedStatement.bindInteger(1, paramInt1);
            localSQLitePreparedStatement.step();
            localSQLitePreparedStatement.dispose();
            return;
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
          }
        }
      });
      return;
    }
  }
  
  public static void removeStickersSet(Context paramContext, TLRPC.StickerSet paramStickerSet, int paramInt)
  {
    boolean bool2 = true;
    TLRPC.TL_inputStickerSetID localTL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
    localTL_inputStickerSetID.access_hash = paramStickerSet.access_hash;
    localTL_inputStickerSetID.id = paramStickerSet.id;
    if (paramInt != 0)
    {
      int i;
      if (paramInt == 1)
      {
        bool1 = true;
        paramStickerSet.disabled = bool1;
        i = 0;
        label50:
        if (i < stickerSets.size())
        {
          paramContext = (TLRPC.TL_messages_stickerSet)stickerSets.get(i);
          if (paramContext.set.id != paramStickerSet.id) {
            break label244;
          }
          stickerSets.remove(i);
          if (paramInt != 2) {
            break label192;
          }
          stickerSets.add(0, paramContext);
        }
        label107:
        loadHash = calcStickersHash(stickerSets);
        putStickersToCache(stickerSets, loadDate, loadHash);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[0]);
        paramContext = new TLRPC.TL_messages_installStickerSet();
        paramContext.stickerset = localTL_inputStickerSetID;
        if (paramInt != 1) {
          break label251;
        }
      }
      label192:
      label231:
      label244:
      label251:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        paramContext.disabled = bool1;
        ConnectionsManager.getInstance().sendRequest(paramContext, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                StickersQuery.loadStickers(false, false);
              }
            }, 1000L);
          }
        });
        return;
        bool1 = false;
        break;
        i = stickerSets.size() - 1;
        while (i >= 0)
        {
          if (!((TLRPC.TL_messages_stickerSet)stickerSets.get(i)).set.disabled) {
            break label231;
          }
          i -= 1;
        }
        break label107;
        stickerSets.add(i + 1, paramContext);
        break label107;
        i += 1;
        break label50;
      }
    }
    paramStickerSet = new TLRPC.TL_messages_uninstallStickerSet();
    paramStickerSet.stickerset = localTL_inputStickerSetID;
    ConnectionsManager.getInstance().sendRequest(paramStickerSet, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            try
            {
              if (paramAnonymousTL_error == null) {
                Toast.makeText(StickersQuery.8.this.val$context, LocaleController.getString("StickersRemoved", 2131166369), 0).show();
              }
              for (;;)
              {
                StickersQuery.loadStickers(false, true);
                return;
                Toast.makeText(StickersQuery.8.this.val$context, LocaleController.getString("ErrorOccurred", 2131165672), 0).show();
              }
            }
            catch (Exception localException)
            {
              for (;;)
              {
                FileLog.e("tmessages", localException);
              }
            }
          }
        });
      }
    });
  }
  
  public static void reorderStickers(ArrayList<Long> paramArrayList)
  {
    Collections.sort(stickerSets, new Comparator()
    {
      public int compare(TLRPC.TL_messages_stickerSet paramAnonymousTL_messages_stickerSet1, TLRPC.TL_messages_stickerSet paramAnonymousTL_messages_stickerSet2)
      {
        int i = this.val$order.indexOf(Long.valueOf(paramAnonymousTL_messages_stickerSet1.set.id));
        int j = this.val$order.indexOf(Long.valueOf(paramAnonymousTL_messages_stickerSet2.set.id));
        if (i > j) {
          return 1;
        }
        if (i < j) {
          return -1;
        }
        return 0;
      }
    });
    loadHash = calcStickersHash(stickerSets);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[0]);
    loadStickers(false, true);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\query\StickersQuery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */