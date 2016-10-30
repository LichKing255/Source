package org.telegram.messenger;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Scanner;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFileLocation;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentEncrypted;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.TL_inputDocumentFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputFileLocation;
import org.telegram.tgnet.TLRPC.TL_upload_file;
import org.telegram.tgnet.TLRPC.TL_upload_getFile;

public class FileLoadOperation
{
  private static final int bigFileSizeFrom = 1048576;
  private static final int downloadChunkSize = 32768;
  private static final int downloadChunkSizeBig = 131072;
  private static final int maxDownloadRequests = 4;
  private static final int maxDownloadRequestsBig = 2;
  private static final int stateDownloading = 1;
  private static final int stateFailed = 2;
  private static final int stateFinished = 3;
  private static final int stateIdle = 0;
  private int bytesCountPadding;
  private File cacheFileFinal;
  private File cacheFileTemp;
  private File cacheIvTemp;
  private int currentDownloadChunkSize;
  private int currentMaxDownloadRequests;
  private int datacenter_id;
  private ArrayList<RequestInfo> delayedRequestInfos;
  private FileLoadOperationDelegate delegate;
  private int downloadedBytes;
  private String ext;
  private RandomAccessFile fileOutputStream;
  private RandomAccessFile fiv;
  private boolean isForceRequest;
  private byte[] iv;
  private byte[] key;
  private TLRPC.InputFileLocation location;
  private int nextDownloadOffset;
  private String origin;
  private int renameRetryCount;
  private ArrayList<RequestInfo> requestInfos;
  private int requestsCount;
  private volatile int state = 0;
  private File storePath;
  private File tempPath;
  private int totalBytesCount;
  
  public FileLoadOperation(TLRPC.Document paramDocument)
  {
    for (;;)
    {
      try
      {
        int j;
        if ((paramDocument instanceof TLRPC.TL_documentEncrypted))
        {
          this.location = new TLRPC.TL_inputEncryptedFileLocation();
          this.location.id = paramDocument.id;
          this.location.access_hash = paramDocument.access_hash;
          this.datacenter_id = paramDocument.dc_id;
          this.iv = new byte[32];
          System.arraycopy(paramDocument.iv, 0, this.iv, 0, this.iv.length);
          this.key = paramDocument.key;
          this.totalBytesCount = paramDocument.size;
          if ((this.key != null) && (this.totalBytesCount % 16 != 0))
          {
            this.bytesCountPadding = (16 - this.totalBytesCount % 16);
            this.totalBytesCount += this.bytesCountPadding;
          }
          this.ext = FileLoader.getDocumentFileName(paramDocument);
          if (this.ext != null)
          {
            j = this.ext.lastIndexOf('.');
            if (j != -1) {
              break label325;
            }
          }
          this.ext = "";
          if (this.ext.length() > 1) {
            break;
          }
          if (paramDocument.mime_type == null) {
            break label380;
          }
          paramDocument = paramDocument.mime_type;
        }
        switch (paramDocument.hashCode())
        {
        case 1331848029: 
          this.ext = "";
          return;
          if (!(paramDocument instanceof TLRPC.TL_document)) {
            continue;
          }
          this.location = new TLRPC.TL_inputDocumentFileLocation();
          this.location.id = paramDocument.id;
          this.location.access_hash = paramDocument.access_hash;
          this.datacenter_id = paramDocument.dc_id;
          continue;
          this.ext = this.ext.substring(j);
        }
      }
      catch (Exception paramDocument)
      {
        FileLog.e("tmessages", paramDocument);
        this.state = 2;
        cleanup();
        Utilities.stageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, 0);
          }
        });
        return;
      }
      label325:
      continue;
      if (paramDocument.equals("video/mp4"))
      {
        break label389;
        if (paramDocument.equals("audio/ogg"))
        {
          i = 1;
          break label389;
          this.ext = ".mp4";
          return;
          this.ext = ".ogg";
          return;
          label380:
          this.ext = "";
          return;
        }
      }
      i = -1;
      label389:
      switch (i)
      {
      }
    }
  }
  
  public FileLoadOperation(TLRPC.FileLocation paramFileLocation, String paramString, int paramInt)
  {
    this.origin = FileLoader.getAttachFileName(paramFileLocation);
    if ((paramFileLocation instanceof TLRPC.TL_fileEncryptedLocation))
    {
      this.location = new TLRPC.TL_inputEncryptedFileLocation();
      this.location.id = paramFileLocation.volume_id;
      this.location.volume_id = paramFileLocation.volume_id;
      this.location.access_hash = paramFileLocation.secret;
      this.location.local_id = paramFileLocation.local_id;
      this.iv = new byte[32];
      System.arraycopy(paramFileLocation.iv, 0, this.iv, 0, this.iv.length);
      this.key = paramFileLocation.key;
      this.datacenter_id = paramFileLocation.dc_id;
      this.totalBytesCount = paramInt;
      if (paramString == null) {
        break label198;
      }
    }
    for (;;)
    {
      this.ext = paramString;
      return;
      if (!(paramFileLocation instanceof TLRPC.TL_fileLocation)) {
        break;
      }
      this.location = new TLRPC.TL_inputFileLocation();
      this.location.volume_id = paramFileLocation.volume_id;
      this.location.secret = paramFileLocation.secret;
      this.location.local_id = paramFileLocation.local_id;
      this.datacenter_id = paramFileLocation.dc_id;
      break;
      label198:
      paramString = "jpg";
    }
  }
  
  /* Error */
  private void cleanup()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 275	org/telegram/messenger/FileLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   4: astore_2
    //   5: aload_2
    //   6: ifnull +25 -> 31
    //   9: aload_0
    //   10: getfield 275	org/telegram/messenger/FileLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   13: invokevirtual 281	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
    //   16: invokevirtual 286	java/nio/channels/FileChannel:close	()V
    //   19: aload_0
    //   20: getfield 275	org/telegram/messenger/FileLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   23: invokevirtual 287	java/io/RandomAccessFile:close	()V
    //   26: aload_0
    //   27: aconst_null
    //   28: putfield 275	org/telegram/messenger/FileLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   31: aload_0
    //   32: getfield 289	org/telegram/messenger/FileLoadOperation:fiv	Ljava/io/RandomAccessFile;
    //   35: ifnull +15 -> 50
    //   38: aload_0
    //   39: getfield 289	org/telegram/messenger/FileLoadOperation:fiv	Ljava/io/RandomAccessFile;
    //   42: invokevirtual 287	java/io/RandomAccessFile:close	()V
    //   45: aload_0
    //   46: aconst_null
    //   47: putfield 289	org/telegram/messenger/FileLoadOperation:fiv	Ljava/io/RandomAccessFile;
    //   50: aload_0
    //   51: getfield 291	org/telegram/messenger/FileLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   54: ifnull +94 -> 148
    //   57: iconst_0
    //   58: istore_1
    //   59: iload_1
    //   60: aload_0
    //   61: getfield 291	org/telegram/messenger/FileLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   64: invokevirtual 295	java/util/ArrayList:size	()I
    //   67: if_icmpge +74 -> 141
    //   70: aload_0
    //   71: getfield 291	org/telegram/messenger/FileLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   74: iload_1
    //   75: invokevirtual 299	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   78: checkcast 27	org/telegram/messenger/FileLoadOperation$RequestInfo
    //   81: astore_2
    //   82: aload_2
    //   83: invokestatic 303	org/telegram/messenger/FileLoadOperation$RequestInfo:access$900	(Lorg/telegram/messenger/FileLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_file;
    //   86: ifnull +18 -> 104
    //   89: aload_2
    //   90: invokestatic 303	org/telegram/messenger/FileLoadOperation$RequestInfo:access$900	(Lorg/telegram/messenger/FileLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_file;
    //   93: iconst_0
    //   94: putfield 308	org/telegram/tgnet/TLRPC$TL_upload_file:disableFree	Z
    //   97: aload_2
    //   98: invokestatic 303	org/telegram/messenger/FileLoadOperation$RequestInfo:access$900	(Lorg/telegram/messenger/FileLoadOperation$RequestInfo;)Lorg/telegram/tgnet/TLRPC$TL_upload_file;
    //   101: invokevirtual 311	org/telegram/tgnet/TLRPC$TL_upload_file:freeResources	()V
    //   104: iload_1
    //   105: iconst_1
    //   106: iadd
    //   107: istore_1
    //   108: goto -49 -> 59
    //   111: astore_2
    //   112: ldc -89
    //   114: aload_2
    //   115: invokestatic 173	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   118: goto -99 -> 19
    //   121: astore_2
    //   122: ldc -89
    //   124: aload_2
    //   125: invokestatic 173	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   128: goto -97 -> 31
    //   131: astore_2
    //   132: ldc -89
    //   134: aload_2
    //   135: invokestatic 173	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   138: goto -88 -> 50
    //   141: aload_0
    //   142: getfield 291	org/telegram/messenger/FileLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   145: invokevirtual 314	java/util/ArrayList:clear	()V
    //   148: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	149	0	this	FileLoadOperation
    //   58	50	1	i	int
    //   4	94	2	localObject	Object
    //   111	4	2	localException1	Exception
    //   121	4	2	localException2	Exception
    //   131	4	2	localException3	Exception
    // Exception table:
    //   from	to	target	type
    //   9	19	111	java/lang/Exception
    //   0	5	121	java/lang/Exception
    //   19	31	121	java/lang/Exception
    //   112	118	121	java/lang/Exception
    //   31	50	131	java/lang/Exception
  }
  
  private void onFinishLoadingFile()
    throws Exception
  {
    if (this.state != 1) {
      return;
    }
    this.state = 3;
    cleanup();
    if (this.cacheIvTemp != null)
    {
      this.cacheIvTemp.delete();
      this.cacheIvTemp = null;
    }
    if ((this.cacheFileTemp != null) && (!this.cacheFileTemp.renameTo(this.cacheFileFinal)))
    {
      if (BuildVars.DEBUG_VERSION) {
        FileLog.e("tmessages", "unable to rename temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal + " retry = " + this.renameRetryCount);
      }
      this.renameRetryCount += 1;
      if (this.renameRetryCount < 3)
      {
        this.state = 1;
        Utilities.stageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            try
            {
              FileLoadOperation.this.onFinishLoadingFile();
              return;
            }
            catch (Exception localException)
            {
              FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, 0);
            }
          }
        }, 200L);
        return;
      }
      this.cacheFileFinal = this.cacheFileTemp;
    }
    if (BuildVars.DEBUG_VERSION) {
      FileLog.e("tmessages", "finished downloading file to " + this.cacheFileFinal);
    }
    this.delegate.didFinishLoadingFile(this, this.cacheFileFinal);
  }
  
  private void processRequestResult(RequestInfo paramRequestInfo, TLRPC.TL_error paramTL_error)
  {
    this.requestInfos.remove(paramRequestInfo);
    int i;
    if (paramTL_error == null)
    {
      try
      {
        if (this.downloadedBytes != paramRequestInfo.offset)
        {
          if (this.state != 1) {
            break label750;
          }
          this.delayedRequestInfos.add(paramRequestInfo);
          paramRequestInfo.response.disableFree = true;
          return;
        }
        if ((paramRequestInfo.response.bytes == null) || (paramRequestInfo.response.bytes.limit() == 0))
        {
          onFinishLoadingFile();
          return;
        }
      }
      catch (Exception paramRequestInfo)
      {
        cleanup();
        this.delegate.didFailedLoadingFile(this, 0);
        FileLog.e("tmessages", paramRequestInfo);
        return;
      }
      i = paramRequestInfo.response.bytes.limit();
      this.downloadedBytes += i;
      if (i != this.currentDownloadChunkSize) {
        break label751;
      }
      if ((this.totalBytesCount != this.downloadedBytes) && (this.downloadedBytes % this.currentDownloadChunkSize == 0)) {
        break label762;
      }
      if (this.totalBytesCount <= 0) {
        break label751;
      }
      if (this.totalBytesCount > this.downloadedBytes) {
        break label762;
      }
      break label751;
      if (this.key != null)
      {
        Utilities.aesIgeEncryption(paramRequestInfo.response.bytes.buffer, this.key, this.iv, false, true, 0, paramRequestInfo.response.bytes.limit());
        if ((i != 0) && (this.bytesCountPadding != 0)) {
          paramRequestInfo.response.bytes.limit(paramRequestInfo.response.bytes.limit() - this.bytesCountPadding);
        }
      }
      if (this.fileOutputStream != null) {
        this.fileOutputStream.getChannel().write(paramRequestInfo.response.bytes.buffer);
      }
      if (this.fiv != null)
      {
        this.fiv.seek(0L);
        this.fiv.write(this.iv);
      }
      if ((this.totalBytesCount <= 0) || (this.state != 1)) {
        break label756;
      }
      this.delegate.didChangedLoadProgress(this, Math.min(1.0F, this.downloadedBytes / this.totalBytesCount));
      break label756;
    }
    for (;;)
    {
      int j;
      if (j < this.delayedRequestInfos.size())
      {
        paramRequestInfo = (RequestInfo)this.delayedRequestInfos.get(j);
        if (this.downloadedBytes == paramRequestInfo.offset)
        {
          this.delayedRequestInfos.remove(j);
          processRequestResult(paramRequestInfo, null);
          paramRequestInfo.response.disableFree = false;
          paramRequestInfo.response.freeResources();
        }
      }
      else
      {
        if (i != 0)
        {
          onFinishLoadingFile();
          return;
        }
        startDownloadRequest();
        return;
        if (paramTL_error.text.contains("FILE_MIGRATE_"))
        {
          paramRequestInfo = new Scanner(paramTL_error.text.replace("FILE_MIGRATE_", ""));
          paramRequestInfo.useDelimiter("");
          try
          {
            i = paramRequestInfo.nextInt();
            paramRequestInfo = Integer.valueOf(i);
          }
          catch (Exception paramRequestInfo)
          {
            for (;;)
            {
              paramRequestInfo = null;
            }
            this.datacenter_id = paramRequestInfo.intValue();
            this.nextDownloadOffset = 0;
            startDownloadRequest();
            return;
          }
          if (paramRequestInfo == null)
          {
            cleanup();
            this.delegate.didFailedLoadingFile(this, 0);
            return;
          }
        }
        if (paramTL_error.text.contains("OFFSET_INVALID"))
        {
          if (this.downloadedBytes % this.currentDownloadChunkSize == 0) {
            try
            {
              onFinishLoadingFile();
              return;
            }
            catch (Exception paramRequestInfo)
            {
              FileLog.e("tmessages", paramRequestInfo);
              cleanup();
              this.delegate.didFailedLoadingFile(this, 0);
              return;
            }
          }
          cleanup();
          this.delegate.didFailedLoadingFile(this, 0);
          return;
        }
        if (paramTL_error.text.contains("RETRY_LIMIT"))
        {
          cleanup();
          this.delegate.didFailedLoadingFile(this, 2);
          return;
        }
        if (this.location != null) {
          FileLog.e("tmessages", "" + this.location + " id = " + this.location.id + " local_id = " + this.location.local_id + " access_hash = " + this.location.access_hash + " volume_id = " + this.location.volume_id + " secret = " + this.location.secret);
        }
        cleanup();
        this.delegate.didFailedLoadingFile(this, 0);
        label750:
        return;
        label751:
        i = 1;
        break;
        label756:
        j = 0;
        continue;
        label762:
        i = 0;
        break;
      }
      j += 1;
    }
  }
  
  private void startDownloadRequest()
  {
    if ((this.state != 1) || ((this.totalBytesCount > 0) && (this.nextDownloadOffset >= this.totalBytesCount)) || (this.requestInfos.size() + this.delayedRequestInfos.size() >= this.currentMaxDownloadRequests)) {
      return;
    }
    int i = 1;
    if (this.totalBytesCount > 0) {
      i = Math.max(0, this.currentMaxDownloadRequests - this.requestInfos.size() - this.delayedRequestInfos.size());
    }
    int j = 0;
    label85:
    boolean bool;
    label148:
    TLRPC.TL_upload_getFile localTL_upload_getFile;
    final RequestInfo localRequestInfo;
    ConnectionsManager localConnectionsManager;
    RequestDelegate local9;
    int k;
    label255:
    int n;
    if ((j < i) && ((this.totalBytesCount <= 0) || (this.nextDownloadOffset < this.totalBytesCount)))
    {
      if ((this.totalBytesCount > 0) && (j != i - 1) && ((this.totalBytesCount <= 0) || (this.nextDownloadOffset + this.currentDownloadChunkSize < this.totalBytesCount))) {
        break label315;
      }
      bool = true;
      localTL_upload_getFile = new TLRPC.TL_upload_getFile();
      localTL_upload_getFile.location = this.location;
      localTL_upload_getFile.offset = this.nextDownloadOffset;
      localTL_upload_getFile.limit = this.currentDownloadChunkSize;
      this.nextDownloadOffset += this.currentDownloadChunkSize;
      localRequestInfo = new RequestInfo(null);
      this.requestInfos.add(localRequestInfo);
      RequestInfo.access$1002(localRequestInfo, localTL_upload_getFile.offset);
      localConnectionsManager = ConnectionsManager.getInstance();
      local9 = new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          FileLoadOperation.RequestInfo.access$902(localRequestInfo, (TLRPC.TL_upload_file)paramAnonymousTLObject);
          FileLoadOperation.this.processRequestResult(localRequestInfo, paramAnonymousTL_error);
        }
      };
      if (!this.isForceRequest) {
        break label321;
      }
      k = 32;
      n = this.datacenter_id;
      if (this.requestsCount % 2 != 0) {
        break label326;
      }
    }
    label315:
    label321:
    label326:
    for (int m = 2;; m = 65538)
    {
      RequestInfo.access$802(localRequestInfo, localConnectionsManager.sendRequest(localTL_upload_getFile, local9, null, k | 0x2, n, m, bool));
      this.requestsCount += 1;
      j += 1;
      break label85;
      break;
      bool = false;
      break label148;
      k = 0;
      break label255;
    }
  }
  
  public void cancel()
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if ((FileLoadOperation.this.state == 3) || (FileLoadOperation.this.state == 2)) {
          return;
        }
        FileLoadOperation.access$502(FileLoadOperation.this, 2);
        FileLoadOperation.this.cleanup();
        if (FileLoadOperation.this.requestInfos != null)
        {
          int i = 0;
          while (i < FileLoadOperation.this.requestInfos.size())
          {
            FileLoadOperation.RequestInfo localRequestInfo = (FileLoadOperation.RequestInfo)FileLoadOperation.this.requestInfos.get(i);
            if (FileLoadOperation.RequestInfo.access$800(localRequestInfo) != 0) {
              ConnectionsManager.getInstance().cancelRequest(FileLoadOperation.RequestInfo.access$800(localRequestInfo), true);
            }
            i += 1;
          }
        }
        FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, 1);
      }
    });
  }
  
  public boolean isForceRequest()
  {
    return this.isForceRequest;
  }
  
  public void setDelegate(FileLoadOperationDelegate paramFileLoadOperationDelegate)
  {
    this.delegate = paramFileLoadOperationDelegate;
  }
  
  public void setForceRequest(boolean paramBoolean)
  {
    this.isForceRequest = paramBoolean;
  }
  
  public void setPaths(File paramFile1, File paramFile2)
  {
    this.storePath = paramFile1;
    this.tempPath = paramFile2;
  }
  
  public void start()
  {
    if (this.state != 0) {
      return;
    }
    if (this.totalBytesCount >= 1048576)
    {
      i = 131072;
      this.currentDownloadChunkSize = i;
      if (this.totalBytesCount < 1048576) {
        break label110;
      }
    }
    label110:
    for (int i = 2;; i = 4)
    {
      this.currentMaxDownloadRequests = i;
      this.requestInfos = new ArrayList(this.currentMaxDownloadRequests);
      this.delayedRequestInfos = new ArrayList(this.currentMaxDownloadRequests - 1);
      this.state = 1;
      if (this.location != null) {
        break label115;
      }
      cleanup();
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, 0);
        }
      });
      return;
      i = 32768;
      break;
    }
    label115:
    Object localObject2 = null;
    Object localObject1 = null;
    String str2;
    String str1;
    if ((this.location.volume_id != 0L) && (this.location.local_id != 0))
    {
      str2 = this.location.volume_id + "_" + this.location.local_id + ".temp";
      str1 = this.location.volume_id + "_" + this.location.local_id + "." + this.ext;
      if (this.key != null) {
        localObject1 = this.location.volume_id + "_" + this.location.local_id + ".iv";
      }
      if ((this.datacenter_id == Integer.MIN_VALUE) || (this.location.volume_id == -2147483648L) || (this.datacenter_id == 0))
      {
        cleanup();
        Utilities.stageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, 0);
          }
        });
      }
    }
    else
    {
      str2 = this.datacenter_id + "_" + this.location.id + ".temp";
      str1 = this.datacenter_id + "_" + this.location.id + this.ext;
      localObject1 = localObject2;
      if (this.key != null) {
        localObject1 = this.datacenter_id + "_" + this.location.id + ".iv";
      }
      if ((this.datacenter_id == 0) || (this.location.id == 0L))
      {
        cleanup();
        Utilities.stageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, 0);
          }
        });
        return;
      }
    }
    this.cacheFileFinal = new File(this.storePath, str1);
    if ((this.cacheFileFinal.exists()) && (this.totalBytesCount != 0) && (this.totalBytesCount != this.cacheFileFinal.length())) {
      this.cacheFileFinal.delete();
    }
    if (!this.cacheFileFinal.exists())
    {
      this.cacheFileTemp = new File(this.tempPath, str2);
      if (this.cacheFileTemp.exists())
      {
        this.downloadedBytes = ((int)this.cacheFileTemp.length());
        i = this.downloadedBytes / this.currentDownloadChunkSize * this.currentDownloadChunkSize;
        this.downloadedBytes = i;
        this.nextDownloadOffset = i;
      }
      if (BuildVars.DEBUG_VERSION) {
        FileLog.d("tmessages", "start loading file to temp = " + this.cacheFileTemp + " final = " + this.cacheFileFinal);
      }
      if (localObject1 != null) {
        this.cacheIvTemp = new File(this.tempPath, (String)localObject1);
      }
      try
      {
        this.fiv = new RandomAccessFile(this.cacheIvTemp, "rws");
        long l = this.cacheIvTemp.length();
        if ((l > 0L) && (l % 32L == 0L)) {
          this.fiv.read(this.iv, 0, 32);
        }
        try
        {
          onFinishLoadingFile();
          return;
        }
        catch (Exception localException3)
        {
          this.delegate.didFailedLoadingFile(this, 0);
        }
      }
      catch (Exception localException1)
      {
        try
        {
          for (;;)
          {
            this.fileOutputStream = new RandomAccessFile(this.cacheFileTemp, "rws");
            if (this.downloadedBytes != 0) {
              this.fileOutputStream.seek(this.downloadedBytes);
            }
            if (this.fileOutputStream != null) {
              break;
            }
            cleanup();
            Utilities.stageQueue.postRunnable(new Runnable()
            {
              public void run()
              {
                FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, 0);
              }
            });
            return;
            this.downloadedBytes = 0;
            continue;
            localException1 = localException1;
            FileLog.e("tmessages", localException1);
            this.downloadedBytes = 0;
          }
        }
        catch (Exception localException2)
        {
          for (;;)
          {
            FileLog.e("tmessages", localException2);
          }
          Utilities.stageQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              if ((FileLoadOperation.this.totalBytesCount != 0) && (FileLoadOperation.this.downloadedBytes == FileLoadOperation.this.totalBytesCount)) {
                try
                {
                  FileLoadOperation.this.onFinishLoadingFile();
                  return;
                }
                catch (Exception localException)
                {
                  FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, 0);
                  return;
                }
              }
              FileLoadOperation.this.startDownloadRequest();
            }
          });
          return;
        }
      }
    }
  }
  
  public static abstract interface FileLoadOperationDelegate
  {
    public abstract void didChangedLoadProgress(FileLoadOperation paramFileLoadOperation, float paramFloat);
    
    public abstract void didFailedLoadingFile(FileLoadOperation paramFileLoadOperation, int paramInt);
    
    public abstract void didFinishLoadingFile(FileLoadOperation paramFileLoadOperation, File paramFile);
  }
  
  private static class RequestInfo
  {
    private int offset;
    private int requestToken;
    private TLRPC.TL_upload_file response;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\FileLoadOperation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */