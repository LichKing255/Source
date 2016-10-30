package org.telegram.messenger;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.QuickAckDelegate;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineMessage;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.InputMedia;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.TL_botInlineMediaResult;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaAuto;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaContact;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageText;
import org.telegram.tgnet.TLRPC.TL_decryptedMessage;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionAbortKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionAcceptKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionCommitKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionDeleteMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionFlushHistory;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionNoop;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionNotifyLayer;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionReadMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionRequestKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionResend;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionTyping;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio_old;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageFwdHeader;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.tgnet.TLRPC.TL_messages_forwardMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getBotCallbackAnswer;
import org.telegram.tgnet.TLRPC.TL_messages_sendBroadcast;
import org.telegram.tgnet.TLRPC.TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendMessage;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_updateMessageID;
import org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewMessage;
import org.telegram.tgnet.TLRPC.TL_updateShortSentMessage;
import org.telegram.tgnet.TLRPC.TL_user;
import org.telegram.tgnet.TLRPC.TL_userContact_old2;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;

public class SendMessagesHelper
  implements NotificationCenter.NotificationCenterDelegate
{
  private static volatile SendMessagesHelper Instance = null;
  private TLRPC.ChatFull currentChatInfo = null;
  private HashMap<String, ArrayList<DelayedMessage>> delayedMessages = new HashMap();
  private LocationProvider locationProvider = new LocationProvider(new SendMessagesHelper.LocationProvider.LocationProviderDelegate()
  {
    public void onLocationAcquired(Location paramAnonymousLocation)
    {
      SendMessagesHelper.this.sendLocation(paramAnonymousLocation);
      SendMessagesHelper.this.waitingForLocation.clear();
    }
    
    public void onUnableLocationAcquire()
    {
      HashMap localHashMap = new HashMap(SendMessagesHelper.this.waitingForLocation);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.wasUnableToFindCurrentLocation, new Object[] { localHashMap });
      SendMessagesHelper.this.waitingForLocation.clear();
    }
  });
  private HashMap<Integer, TLRPC.Message> sendingMessages = new HashMap();
  private HashMap<Integer, MessageObject> unsentMessages = new HashMap();
  private HashMap<String, MessageObject> waitingForCallback = new HashMap();
  private HashMap<String, MessageObject> waitingForLocation = new HashMap();
  
  public SendMessagesHelper()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidUpload);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailUpload);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FilePreparingStarted);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileNewChunkAvailable);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FilePreparingFailed);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.httpFileDidFailedLoad);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.httpFileDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
  }
  
  public static void MihanPrepareSendingVideo(final String paramString1, final long paramLong1, final long paramLong2, int paramInt1, final int paramInt2, VideoEditedInfo paramVideoEditedInfo, long paramLong3, final MessageObject paramMessageObject, final String paramString2)
  {
    if ((paramString1 == null) || (paramString1.length() == 0)) {
      return;
    }
    new Thread(new Runnable()
    {
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 31	org/telegram/messenger/SendMessagesHelper$19:val$dialog_id	J
        //   4: l2i
        //   5: ifne +516 -> 521
        //   8: iconst_1
        //   9: istore_3
        //   10: aload_0
        //   11: getfield 33	org/telegram/messenger/SendMessagesHelper$19:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   14: ifnonnull +15 -> 29
        //   17: aload_0
        //   18: getfield 35	org/telegram/messenger/SendMessagesHelper$19:val$videoPath	Ljava/lang/String;
        //   21: ldc 56
        //   23: invokevirtual 62	java/lang/String:endsWith	(Ljava/lang/String;)Z
        //   26: ifeq +890 -> 916
        //   29: aload_0
        //   30: getfield 35	org/telegram/messenger/SendMessagesHelper$19:val$videoPath	Ljava/lang/String;
        //   33: astore 8
        //   35: aload_0
        //   36: getfield 35	org/telegram/messenger/SendMessagesHelper$19:val$videoPath	Ljava/lang/String;
        //   39: astore 4
        //   41: new 64	java/io/File
        //   44: dup
        //   45: aload 4
        //   47: invokespecial 67	java/io/File:<init>	(Ljava/lang/String;)V
        //   50: astore 7
        //   52: new 69	java/lang/StringBuilder
        //   55: dup
        //   56: invokespecial 70	java/lang/StringBuilder:<init>	()V
        //   59: aload 4
        //   61: invokevirtual 74	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   64: aload 7
        //   66: invokevirtual 78	java/io/File:length	()J
        //   69: invokevirtual 81	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
        //   72: ldc 83
        //   74: invokevirtual 74	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   77: aload 7
        //   79: invokevirtual 86	java/io/File:lastModified	()J
        //   82: invokevirtual 81	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
        //   85: invokevirtual 90	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   88: astore 5
        //   90: aload 5
        //   92: astore 4
        //   94: aload_0
        //   95: getfield 33	org/telegram/messenger/SendMessagesHelper$19:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   98: ifnull +110 -> 208
        //   101: new 69	java/lang/StringBuilder
        //   104: dup
        //   105: invokespecial 70	java/lang/StringBuilder:<init>	()V
        //   108: aload 5
        //   110: invokevirtual 74	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   113: aload_0
        //   114: getfield 37	org/telegram/messenger/SendMessagesHelper$19:val$duration	J
        //   117: invokevirtual 81	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
        //   120: ldc 83
        //   122: invokevirtual 74	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   125: aload_0
        //   126: getfield 33	org/telegram/messenger/SendMessagesHelper$19:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   129: getfield 95	org/telegram/messenger/VideoEditedInfo:startTime	J
        //   132: invokevirtual 81	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
        //   135: ldc 83
        //   137: invokevirtual 74	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   140: aload_0
        //   141: getfield 33	org/telegram/messenger/SendMessagesHelper$19:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   144: getfield 98	org/telegram/messenger/VideoEditedInfo:endTime	J
        //   147: invokevirtual 81	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
        //   150: invokevirtual 90	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   153: astore 5
        //   155: aload 5
        //   157: astore 4
        //   159: aload_0
        //   160: getfield 33	org/telegram/messenger/SendMessagesHelper$19:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   163: getfield 101	org/telegram/messenger/VideoEditedInfo:resultWidth	I
        //   166: aload_0
        //   167: getfield 33	org/telegram/messenger/SendMessagesHelper$19:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   170: getfield 104	org/telegram/messenger/VideoEditedInfo:originalWidth	I
        //   173: if_icmpne +35 -> 208
        //   176: new 69	java/lang/StringBuilder
        //   179: dup
        //   180: invokespecial 70	java/lang/StringBuilder:<init>	()V
        //   183: aload 5
        //   185: invokevirtual 74	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   188: ldc 83
        //   190: invokevirtual 74	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   193: aload_0
        //   194: getfield 33	org/telegram/messenger/SendMessagesHelper$19:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   197: getfield 101	org/telegram/messenger/VideoEditedInfo:resultWidth	I
        //   200: invokevirtual 107	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   203: invokevirtual 90	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   206: astore 4
        //   208: aconst_null
        //   209: astore 5
        //   211: iload_3
        //   212: ifne +3 -> 215
        //   215: aload 8
        //   217: astore 6
        //   219: iconst_0
        //   220: ifne +243 -> 463
        //   223: aload_0
        //   224: getfield 35	org/telegram/messenger/SendMessagesHelper$19:val$videoPath	Ljava/lang/String;
        //   227: iconst_1
        //   228: invokestatic 113	android/media/ThumbnailUtils:createVideoThumbnail	(Ljava/lang/String;I)Landroid/graphics/Bitmap;
        //   231: ldc 114
        //   233: ldc 114
        //   235: bipush 55
        //   237: iload_3
        //   238: invokestatic 120	org/telegram/messenger/ImageLoader:scaleAndSaveImage	(Landroid/graphics/Bitmap;FFIZ)Lorg/telegram/tgnet/TLRPC$PhotoSize;
        //   241: astore 5
        //   243: new 122	org/telegram/tgnet/TLRPC$TL_document
        //   246: dup
        //   247: invokespecial 123	org/telegram/tgnet/TLRPC$TL_document:<init>	()V
        //   250: astore 9
        //   252: aload 9
        //   254: aload 5
        //   256: putfield 127	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
        //   259: aload 9
        //   261: getfield 127	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
        //   264: ifnonnull +262 -> 526
        //   267: aload 9
        //   269: new 129	org/telegram/tgnet/TLRPC$TL_photoSizeEmpty
        //   272: dup
        //   273: invokespecial 130	org/telegram/tgnet/TLRPC$TL_photoSizeEmpty:<init>	()V
        //   276: putfield 127	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
        //   279: aload 9
        //   281: getfield 127	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
        //   284: ldc -124
        //   286: putfield 137	org/telegram/tgnet/TLRPC$PhotoSize:type	Ljava/lang/String;
        //   289: aload 9
        //   291: ldc -117
        //   293: putfield 142	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
        //   296: iconst_0
        //   297: invokestatic 148	org/telegram/messenger/UserConfig:saveConfig	(Z)V
        //   300: new 150	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo
        //   303: dup
        //   304: invokespecial 151	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:<init>	()V
        //   307: astore 10
        //   309: aload 9
        //   311: getfield 155	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
        //   314: aload 10
        //   316: invokevirtual 161	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   319: pop
        //   320: aload_0
        //   321: getfield 33	org/telegram/messenger/SendMessagesHelper$19:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   324: ifnull +236 -> 560
        //   327: aload 10
        //   329: aload_0
        //   330: getfield 37	org/telegram/messenger/SendMessagesHelper$19:val$duration	J
        //   333: ldc2_w 162
        //   336: ldiv
        //   337: l2i
        //   338: putfield 166	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:duration	I
        //   341: aload_0
        //   342: getfield 33	org/telegram/messenger/SendMessagesHelper$19:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   345: getfield 169	org/telegram/messenger/VideoEditedInfo:rotationValue	I
        //   348: bipush 90
        //   350: if_icmpeq +16 -> 366
        //   353: aload_0
        //   354: getfield 33	org/telegram/messenger/SendMessagesHelper$19:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   357: getfield 169	org/telegram/messenger/VideoEditedInfo:rotationValue	I
        //   360: sipush 270
        //   363: if_icmpne +176 -> 539
        //   366: aload 10
        //   368: aload_0
        //   369: getfield 39	org/telegram/messenger/SendMessagesHelper$19:val$height	I
        //   372: putfield 172	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
        //   375: aload 10
        //   377: aload_0
        //   378: getfield 41	org/telegram/messenger/SendMessagesHelper$19:val$width	I
        //   381: putfield 175	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
        //   384: aload 9
        //   386: aload_0
        //   387: getfield 43	org/telegram/messenger/SendMessagesHelper$19:val$estimatedSize	J
        //   390: l2i
        //   391: putfield 178	org/telegram/tgnet/TLRPC$TL_document:size	I
        //   394: new 69	java/lang/StringBuilder
        //   397: dup
        //   398: invokespecial 70	java/lang/StringBuilder:<init>	()V
        //   401: ldc -76
        //   403: invokevirtual 74	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   406: getstatic 183	org/telegram/messenger/UserConfig:lastLocalId	I
        //   409: invokevirtual 107	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   412: ldc -71
        //   414: invokevirtual 74	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   417: invokevirtual 90	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   420: astore 5
        //   422: getstatic 183	org/telegram/messenger/UserConfig:lastLocalId	I
        //   425: iconst_1
        //   426: isub
        //   427: putstatic 183	org/telegram/messenger/UserConfig:lastLocalId	I
        //   430: new 64	java/io/File
        //   433: dup
        //   434: invokestatic 191	org/telegram/messenger/FileLoader:getInstance	()Lorg/telegram/messenger/FileLoader;
        //   437: iconst_4
        //   438: invokevirtual 195	org/telegram/messenger/FileLoader:getDirectory	(I)Ljava/io/File;
        //   441: aload 5
        //   443: invokespecial 198	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
        //   446: astore 5
        //   448: iconst_0
        //   449: invokestatic 148	org/telegram/messenger/UserConfig:saveConfig	(Z)V
        //   452: aload 5
        //   454: invokevirtual 201	java/io/File:getAbsolutePath	()Ljava/lang/String;
        //   457: astore 6
        //   459: aload 9
        //   461: astore 5
        //   463: aload_0
        //   464: getfield 45	org/telegram/messenger/SendMessagesHelper$19:val$caption	Ljava/lang/String;
        //   467: ifnull +12 -> 479
        //   470: aload 5
        //   472: aload_0
        //   473: getfield 45	org/telegram/messenger/SendMessagesHelper$19:val$caption	Ljava/lang/String;
        //   476: putfield 204	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
        //   479: new 206	java/util/HashMap
        //   482: dup
        //   483: invokespecial 207	java/util/HashMap:<init>	()V
        //   486: astore 7
        //   488: aload 4
        //   490: ifnull +13 -> 503
        //   493: aload 7
        //   495: ldc -47
        //   497: aload 4
        //   499: invokevirtual 213	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   502: pop
        //   503: new 13	org/telegram/messenger/SendMessagesHelper$19$1
        //   506: dup
        //   507: aload_0
        //   508: aload 5
        //   510: aload 6
        //   512: aload 7
        //   514: invokespecial 216	org/telegram/messenger/SendMessagesHelper$19$1:<init>	(Lorg/telegram/messenger/SendMessagesHelper$19;Lorg/telegram/tgnet/TLRPC$TL_document;Ljava/lang/String;Ljava/util/HashMap;)V
        //   517: invokestatic 222	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
        //   520: return
        //   521: iconst_0
        //   522: istore_3
        //   523: goto -513 -> 10
        //   526: aload 9
        //   528: getfield 127	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
        //   531: ldc -124
        //   533: putfield 137	org/telegram/tgnet/TLRPC$PhotoSize:type	Ljava/lang/String;
        //   536: goto -247 -> 289
        //   539: aload 10
        //   541: aload_0
        //   542: getfield 41	org/telegram/messenger/SendMessagesHelper$19:val$width	I
        //   545: putfield 172	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
        //   548: aload 10
        //   550: aload_0
        //   551: getfield 39	org/telegram/messenger/SendMessagesHelper$19:val$height	I
        //   554: putfield 175	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
        //   557: goto -173 -> 384
        //   560: aload 7
        //   562: invokevirtual 226	java/io/File:exists	()Z
        //   565: ifeq +14 -> 579
        //   568: aload 9
        //   570: aload 7
        //   572: invokevirtual 78	java/io/File:length	()J
        //   575: l2i
        //   576: putfield 178	org/telegram/tgnet/TLRPC$TL_document:size	I
        //   579: iconst_0
        //   580: istore_2
        //   581: aconst_null
        //   582: astore 5
        //   584: aconst_null
        //   585: astore 7
        //   587: new 228	android/media/MediaMetadataRetriever
        //   590: dup
        //   591: invokespecial 229	android/media/MediaMetadataRetriever:<init>	()V
        //   594: astore 6
        //   596: aload 6
        //   598: aload_0
        //   599: getfield 35	org/telegram/messenger/SendMessagesHelper$19:val$videoPath	Ljava/lang/String;
        //   602: invokevirtual 232	android/media/MediaMetadataRetriever:setDataSource	(Ljava/lang/String;)V
        //   605: aload 6
        //   607: bipush 18
        //   609: invokevirtual 236	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
        //   612: astore 5
        //   614: aload 5
        //   616: ifnull +13 -> 629
        //   619: aload 10
        //   621: aload 5
        //   623: invokestatic 242	java/lang/Integer:parseInt	(Ljava/lang/String;)I
        //   626: putfield 172	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
        //   629: aload 6
        //   631: bipush 19
        //   633: invokevirtual 236	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
        //   636: astore 5
        //   638: aload 5
        //   640: ifnull +13 -> 653
        //   643: aload 10
        //   645: aload 5
        //   647: invokestatic 242	java/lang/Integer:parseInt	(Ljava/lang/String;)I
        //   650: putfield 175	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
        //   653: aload 6
        //   655: bipush 9
        //   657: invokevirtual 236	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
        //   660: astore 5
        //   662: aload 5
        //   664: ifnull +22 -> 686
        //   667: aload 10
        //   669: aload 5
        //   671: invokestatic 248	java/lang/Long:parseLong	(Ljava/lang/String;)J
        //   674: l2f
        //   675: ldc -7
        //   677: fdiv
        //   678: f2d
        //   679: invokestatic 255	java/lang/Math:ceil	(D)D
        //   682: d2i
        //   683: putfield 166	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:duration	I
        //   686: iconst_1
        //   687: istore_1
        //   688: aload 6
        //   690: ifnull +8 -> 698
        //   693: aload 6
        //   695: invokevirtual 258	android/media/MediaMetadataRetriever:release	()V
        //   698: aload 9
        //   700: astore 5
        //   702: aload 8
        //   704: astore 6
        //   706: iload_1
        //   707: ifne -244 -> 463
        //   710: getstatic 264	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   713: new 64	java/io/File
        //   716: dup
        //   717: aload_0
        //   718: getfield 35	org/telegram/messenger/SendMessagesHelper$19:val$videoPath	Ljava/lang/String;
        //   721: invokespecial 67	java/io/File:<init>	(Ljava/lang/String;)V
        //   724: invokestatic 270	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
        //   727: invokestatic 276	android/media/MediaPlayer:create	(Landroid/content/Context;Landroid/net/Uri;)Landroid/media/MediaPlayer;
        //   730: astore 7
        //   732: aload 9
        //   734: astore 5
        //   736: aload 8
        //   738: astore 6
        //   740: aload 7
        //   742: ifnull -279 -> 463
        //   745: aload 10
        //   747: aload 7
        //   749: invokevirtual 280	android/media/MediaPlayer:getDuration	()I
        //   752: i2f
        //   753: ldc -7
        //   755: fdiv
        //   756: f2d
        //   757: invokestatic 255	java/lang/Math:ceil	(D)D
        //   760: d2i
        //   761: putfield 166	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:duration	I
        //   764: aload 10
        //   766: aload 7
        //   768: invokevirtual 283	android/media/MediaPlayer:getVideoWidth	()I
        //   771: putfield 172	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
        //   774: aload 10
        //   776: aload 7
        //   778: invokevirtual 286	android/media/MediaPlayer:getVideoHeight	()I
        //   781: putfield 175	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
        //   784: aload 7
        //   786: invokevirtual 287	android/media/MediaPlayer:release	()V
        //   789: aload 9
        //   791: astore 5
        //   793: aload 8
        //   795: astore 6
        //   797: goto -334 -> 463
        //   800: astore 5
        //   802: ldc_w 289
        //   805: aload 5
        //   807: invokestatic 295	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   810: aload 9
        //   812: astore 5
        //   814: aload 8
        //   816: astore 6
        //   818: goto -355 -> 463
        //   821: astore 5
        //   823: ldc_w 289
        //   826: aload 5
        //   828: invokestatic 295	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   831: goto -133 -> 698
        //   834: astore 5
        //   836: aload 7
        //   838: astore 6
        //   840: aload 5
        //   842: astore 7
        //   844: aload 6
        //   846: astore 5
        //   848: ldc_w 289
        //   851: aload 7
        //   853: invokestatic 295	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   856: iload_2
        //   857: istore_1
        //   858: aload 6
        //   860: ifnull -162 -> 698
        //   863: aload 6
        //   865: invokevirtual 258	android/media/MediaMetadataRetriever:release	()V
        //   868: iload_2
        //   869: istore_1
        //   870: goto -172 -> 698
        //   873: astore 5
        //   875: ldc_w 289
        //   878: aload 5
        //   880: invokestatic 295	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   883: iload_2
        //   884: istore_1
        //   885: goto -187 -> 698
        //   888: astore 4
        //   890: aload 5
        //   892: ifnull +8 -> 900
        //   895: aload 5
        //   897: invokevirtual 258	android/media/MediaMetadataRetriever:release	()V
        //   900: aload 4
        //   902: athrow
        //   903: astore 5
        //   905: ldc_w 289
        //   908: aload 5
        //   910: invokestatic 295	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   913: goto -13 -> 900
        //   916: aload_0
        //   917: getfield 35	org/telegram/messenger/SendMessagesHelper$19:val$videoPath	Ljava/lang/String;
        //   920: aload_0
        //   921: getfield 35	org/telegram/messenger/SendMessagesHelper$19:val$videoPath	Ljava/lang/String;
        //   924: aconst_null
        //   925: aconst_null
        //   926: aload_0
        //   927: getfield 31	org/telegram/messenger/SendMessagesHelper$19:val$dialog_id	J
        //   930: aload_0
        //   931: getfield 47	org/telegram/messenger/SendMessagesHelper$19:val$reply_to_msg	Lorg/telegram/messenger/MessageObject;
        //   934: aconst_null
        //   935: invokestatic 299	org/telegram/messenger/SendMessagesHelper:access$1000	(Ljava/lang/String;Ljava/lang/String;Landroid/net/Uri;Ljava/lang/String;JLorg/telegram/messenger/MessageObject;Ljava/lang/String;)Z
        //   938: pop
        //   939: return
        //   940: astore 4
        //   942: aload 6
        //   944: astore 5
        //   946: goto -56 -> 890
        //   949: astore 7
        //   951: goto -107 -> 844
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	954	0	this	19
        //   687	198	1	i	int
        //   580	304	2	j	int
        //   9	514	3	bool	boolean
        //   39	459	4	localObject1	Object
        //   888	13	4	localObject2	Object
        //   940	1	4	localObject3	Object
        //   88	704	5	localObject4	Object
        //   800	6	5	localException1	Exception
        //   812	1	5	localObject5	Object
        //   821	6	5	localException2	Exception
        //   834	7	5	localException3	Exception
        //   846	1	5	localObject6	Object
        //   873	23	5	localException4	Exception
        //   903	6	5	localException5	Exception
        //   944	1	5	localObject7	Object
        //   217	726	6	localObject8	Object
        //   50	802	7	localObject9	Object
        //   949	1	7	localException6	Exception
        //   33	782	8	str	String
        //   250	561	9	localTL_document	TLRPC.TL_document
        //   307	468	10	localTL_documentAttributeVideo	TLRPC.TL_documentAttributeVideo
        // Exception table:
        //   from	to	target	type
        //   710	732	800	java/lang/Exception
        //   745	789	800	java/lang/Exception
        //   693	698	821	java/lang/Exception
        //   587	596	834	java/lang/Exception
        //   863	868	873	java/lang/Exception
        //   587	596	888	finally
        //   848	856	888	finally
        //   895	900	903	java/lang/Exception
        //   596	614	940	finally
        //   619	629	940	finally
        //   629	638	940	finally
        //   643	653	940	finally
        //   653	662	940	finally
        //   667	686	940	finally
        //   596	614	949	java/lang/Exception
        //   619	629	949	java/lang/Exception
        //   629	638	949	java/lang/Exception
        //   643	653	949	java/lang/Exception
        //   653	662	949	java/lang/Exception
        //   667	686	949	java/lang/Exception
      }
    }).start();
  }
  
  /* Error */
  private void MihanSendMessage(CharSequence paramCharSequence, String paramString1, TLRPC.MessageMedia paramMessageMedia, TLRPC.TL_photo paramTL_photo, VideoEditedInfo paramVideoEditedInfo, TLRPC.User paramUser, TLRPC.TL_document paramTL_document, long paramLong, String paramString2, MessageObject paramMessageObject1, TLRPC.WebPage paramWebPage, boolean paramBoolean, MessageObject paramMessageObject2, ArrayList<TLRPC.MessageEntity> paramArrayList, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    // Byte code:
    //   0: lload 8
    //   2: lconst_0
    //   3: lcmp
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aconst_null
    //   9: astore 25
    //   11: aload 25
    //   13: astore 32
    //   15: aload 17
    //   17: ifnull +29 -> 46
    //   20: aload 25
    //   22: astore 32
    //   24: aload 17
    //   26: ldc -34
    //   28: invokevirtual 226	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   31: ifeq +15 -> 46
    //   34: aload 17
    //   36: ldc -34
    //   38: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   41: checkcast 201	java/lang/String
    //   44: astore 32
    //   46: aconst_null
    //   47: astore 26
    //   49: aconst_null
    //   50: astore 27
    //   52: iconst_m1
    //   53: istore 18
    //   55: lload 8
    //   57: l2i
    //   58: istore 23
    //   60: lload 8
    //   62: bipush 32
    //   64: lshr
    //   65: l2i
    //   66: istore 22
    //   68: iconst_0
    //   69: istore 20
    //   71: aconst_null
    //   72: astore 25
    //   74: iload 23
    //   76: ifeq +99 -> 175
    //   79: iload 23
    //   81: invokestatic 236	org/telegram/messenger/MessagesController:getInputPeer	(I)Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   84: astore 33
    //   86: aconst_null
    //   87: astore 38
    //   89: iload 23
    //   91: ifne +90 -> 181
    //   94: invokestatic 239	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   97: iload 22
    //   99: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   102: invokevirtual 249	org/telegram/messenger/MessagesController:getEncryptedChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   105: astore 25
    //   107: aload 25
    //   109: astore 35
    //   111: aload 25
    //   113: ifnonnull +116 -> 229
    //   116: aload 14
    //   118: ifnull -111 -> 7
    //   121: invokestatic 254	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
    //   124: aload 14
    //   126: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   129: invokevirtual 264	org/telegram/messenger/MessagesStorage:markMessageAsSendError	(Lorg/telegram/tgnet/TLRPC$Message;)V
    //   132: aload 14
    //   134: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   137: iconst_2
    //   138: putfield 269	org/telegram/tgnet/TLRPC$Message:send_state	I
    //   141: invokestatic 165	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   144: getstatic 272	org/telegram/messenger/NotificationCenter:messageSendError	I
    //   147: iconst_1
    //   148: anewarray 4	java/lang/Object
    //   151: dup
    //   152: iconst_0
    //   153: aload 14
    //   155: invokevirtual 275	org/telegram/messenger/MessageObject:getId	()I
    //   158: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   161: aastore
    //   162: invokevirtual 279	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   165: aload_0
    //   166: aload 14
    //   168: invokevirtual 275	org/telegram/messenger/MessageObject:getId	()I
    //   171: invokevirtual 283	org/telegram/messenger/SendMessagesHelper:processSentMessage	(I)V
    //   174: return
    //   175: aconst_null
    //   176: astore 33
    //   178: goto -92 -> 86
    //   181: aload 25
    //   183: astore 35
    //   185: aload 33
    //   187: instanceof 285
    //   190: ifeq +39 -> 229
    //   193: invokestatic 239	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   196: aload 33
    //   198: getfield 290	org/telegram/tgnet/TLRPC$InputPeer:channel_id	I
    //   201: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   204: invokevirtual 294	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   207: astore 28
    //   209: aload 28
    //   211: ifnull +597 -> 808
    //   214: aload 28
    //   216: getfield 300	org/telegram/tgnet/TLRPC$Chat:megagroup	Z
    //   219: ifne +589 -> 808
    //   222: iconst_1
    //   223: istore 20
    //   225: aload 25
    //   227: astore 35
    //   229: aload 14
    //   231: ifnull +1129 -> 1360
    //   234: aload 27
    //   236: astore 25
    //   238: aload 14
    //   240: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   243: astore 27
    //   245: aload 27
    //   247: astore 25
    //   249: aload 14
    //   251: invokevirtual 304	org/telegram/messenger/MessageObject:isForwarded	()Z
    //   254: ifeq +564 -> 818
    //   257: iconst_4
    //   258: istore 19
    //   260: aload 12
    //   262: astore 37
    //   264: aload 7
    //   266: astore 34
    //   268: aload 6
    //   270: astore 36
    //   272: aload 27
    //   274: astore 26
    //   276: aload 26
    //   278: astore 25
    //   280: aload 26
    //   282: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   285: lconst_0
    //   286: lcmp
    //   287: ifne +16 -> 303
    //   290: aload 26
    //   292: astore 25
    //   294: aload 26
    //   296: aload_0
    //   297: invokevirtual 312	org/telegram/messenger/SendMessagesHelper:getNextRandomId	()J
    //   300: putfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   303: aload 17
    //   305: ifnull +85 -> 390
    //   308: aload 26
    //   310: astore 25
    //   312: aload 17
    //   314: ldc_w 314
    //   317: invokevirtual 226	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   320: ifeq +70 -> 390
    //   323: aload 35
    //   325: ifnull +2663 -> 2988
    //   328: aload 26
    //   330: astore 25
    //   332: aload 26
    //   334: aload 17
    //   336: ldc_w 316
    //   339: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   342: checkcast 201	java/lang/String
    //   345: putfield 320	org/telegram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   348: aload 26
    //   350: astore 25
    //   352: aload 26
    //   354: getfield 320	org/telegram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   357: ifnonnull +15 -> 372
    //   360: aload 26
    //   362: astore 25
    //   364: aload 26
    //   366: ldc_w 322
    //   369: putfield 320	org/telegram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   372: aload 26
    //   374: astore 25
    //   376: aload 26
    //   378: aload 26
    //   380: getfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   383: sipush 2048
    //   386: ior
    //   387: putfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   390: aload 26
    //   392: astore 25
    //   394: aload 26
    //   396: aload 17
    //   398: putfield 328	org/telegram/tgnet/TLRPC$Message:params	Ljava/util/HashMap;
    //   401: aload 26
    //   403: astore 25
    //   405: aload 26
    //   407: invokestatic 333	org/telegram/tgnet/ConnectionsManager:getInstance	()Lorg/telegram/tgnet/ConnectionsManager;
    //   410: invokevirtual 336	org/telegram/tgnet/ConnectionsManager:getCurrentTime	()I
    //   413: putfield 339	org/telegram/tgnet/TLRPC$Message:date	I
    //   416: aload 26
    //   418: astore 25
    //   420: aload 26
    //   422: aload 26
    //   424: getfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   427: sipush 512
    //   430: ior
    //   431: putfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   434: aload 26
    //   436: astore 25
    //   438: aload 33
    //   440: instanceof 285
    //   443: ifeq +2611 -> 3054
    //   446: iload 20
    //   448: ifeq +31 -> 479
    //   451: aload 26
    //   453: astore 25
    //   455: aload 26
    //   457: iconst_1
    //   458: putfield 342	org/telegram/tgnet/TLRPC$Message:views	I
    //   461: aload 26
    //   463: astore 25
    //   465: aload 26
    //   467: aload 26
    //   469: getfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   472: sipush 1024
    //   475: ior
    //   476: putfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   479: aload 26
    //   481: astore 25
    //   483: invokestatic 239	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   486: aload 33
    //   488: getfield 290	org/telegram/tgnet/TLRPC$InputPeer:channel_id	I
    //   491: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   494: invokevirtual 294	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   497: astore 6
    //   499: aload 6
    //   501: ifnull +43 -> 544
    //   504: aload 26
    //   506: astore 25
    //   508: aload 6
    //   510: getfield 300	org/telegram/tgnet/TLRPC$Chat:megagroup	Z
    //   513: ifeq +2504 -> 3017
    //   516: aload 26
    //   518: astore 25
    //   520: aload 26
    //   522: aload 26
    //   524: getfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   527: ldc_w 343
    //   530: ior
    //   531: putfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   534: aload 26
    //   536: astore 25
    //   538: aload 26
    //   540: iconst_1
    //   541: putfield 346	org/telegram/tgnet/TLRPC$Message:unread	Z
    //   544: aload 26
    //   546: astore 25
    //   548: aload 26
    //   550: lload 8
    //   552: putfield 349	org/telegram/tgnet/TLRPC$Message:dialog_id	J
    //   555: aload 11
    //   557: ifnull +73 -> 630
    //   560: aload 35
    //   562: ifnull +2505 -> 3067
    //   565: aload 26
    //   567: astore 25
    //   569: aload 11
    //   571: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   574: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   577: lconst_0
    //   578: lcmp
    //   579: ifeq +2488 -> 3067
    //   582: aload 26
    //   584: astore 25
    //   586: aload 26
    //   588: aload 11
    //   590: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   593: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   596: putfield 352	org/telegram/tgnet/TLRPC$Message:reply_to_random_id	J
    //   599: aload 26
    //   601: astore 25
    //   603: aload 26
    //   605: aload 26
    //   607: getfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   610: bipush 8
    //   612: ior
    //   613: putfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   616: aload 26
    //   618: astore 25
    //   620: aload 26
    //   622: aload 11
    //   624: invokevirtual 275	org/telegram/messenger/MessageObject:getId	()I
    //   627: putfield 355	org/telegram/tgnet/TLRPC$Message:reply_to_msg_id	I
    //   630: aload 16
    //   632: ifnull +36 -> 668
    //   635: aload 35
    //   637: ifnonnull +31 -> 668
    //   640: aload 26
    //   642: astore 25
    //   644: aload 26
    //   646: aload 26
    //   648: getfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   651: bipush 64
    //   653: ior
    //   654: putfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   657: aload 26
    //   659: astore 25
    //   661: aload 26
    //   663: aload 16
    //   665: putfield 359	org/telegram/tgnet/TLRPC$Message:reply_markup	Lorg/telegram/tgnet/TLRPC$ReplyMarkup;
    //   668: iload 23
    //   670: ifeq +2955 -> 3625
    //   673: iload 22
    //   675: iconst_1
    //   676: if_icmpne +2857 -> 3533
    //   679: aload 26
    //   681: astore 25
    //   683: aload_0
    //   684: getfield 138	org/telegram/messenger/SendMessagesHelper:currentChatInfo	Lorg/telegram/tgnet/TLRPC$ChatFull;
    //   687: ifnonnull +2400 -> 3087
    //   690: aload 26
    //   692: astore 25
    //   694: invokestatic 254	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
    //   697: aload 26
    //   699: invokevirtual 264	org/telegram/messenger/MessagesStorage:markMessageAsSendError	(Lorg/telegram/tgnet/TLRPC$Message;)V
    //   702: aload 26
    //   704: astore 25
    //   706: invokestatic 165	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   709: getstatic 272	org/telegram/messenger/NotificationCenter:messageSendError	I
    //   712: iconst_1
    //   713: anewarray 4	java/lang/Object
    //   716: dup
    //   717: iconst_0
    //   718: aload 26
    //   720: getfield 362	org/telegram/tgnet/TLRPC$Message:id	I
    //   723: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   726: aastore
    //   727: invokevirtual 279	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   730: aload 26
    //   732: astore 25
    //   734: aload_0
    //   735: aload 26
    //   737: getfield 362	org/telegram/tgnet/TLRPC$Message:id	I
    //   740: invokevirtual 283	org/telegram/messenger/SendMessagesHelper:processSentMessage	(I)V
    //   743: return
    //   744: astore_2
    //   745: aconst_null
    //   746: astore_1
    //   747: ldc_w 364
    //   750: aload_2
    //   751: invokestatic 370	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   754: invokestatic 254	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
    //   757: aload 25
    //   759: invokevirtual 264	org/telegram/messenger/MessagesStorage:markMessageAsSendError	(Lorg/telegram/tgnet/TLRPC$Message;)V
    //   762: aload_1
    //   763: ifnull +11 -> 774
    //   766: aload_1
    //   767: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   770: iconst_2
    //   771: putfield 269	org/telegram/tgnet/TLRPC$Message:send_state	I
    //   774: invokestatic 165	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   777: getstatic 272	org/telegram/messenger/NotificationCenter:messageSendError	I
    //   780: iconst_1
    //   781: anewarray 4	java/lang/Object
    //   784: dup
    //   785: iconst_0
    //   786: aload 25
    //   788: getfield 362	org/telegram/tgnet/TLRPC$Message:id	I
    //   791: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   794: aastore
    //   795: invokevirtual 279	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   798: aload_0
    //   799: aload 25
    //   801: getfield 362	org/telegram/tgnet/TLRPC$Message:id	I
    //   804: invokevirtual 283	org/telegram/messenger/SendMessagesHelper:processSentMessage	(I)V
    //   807: return
    //   808: iconst_0
    //   809: istore 20
    //   811: aload 25
    //   813: astore 35
    //   815: goto -586 -> 229
    //   818: aload 27
    //   820: astore 25
    //   822: aload 14
    //   824: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   827: ifne +141 -> 968
    //   830: aload 27
    //   832: astore 25
    //   834: aload 27
    //   836: getfield 376	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   839: astore 28
    //   841: iconst_0
    //   842: istore 18
    //   844: aload 6
    //   846: astore 31
    //   848: aload 4
    //   850: astore 30
    //   852: aload_3
    //   853: astore 29
    //   855: aload 27
    //   857: astore 26
    //   859: iload 18
    //   861: istore 19
    //   863: aload 28
    //   865: astore_2
    //   866: aload 29
    //   868: astore_3
    //   869: aload 30
    //   871: astore 4
    //   873: aload 31
    //   875: astore 36
    //   877: aload 7
    //   879: astore 34
    //   881: aload 12
    //   883: astore 37
    //   885: aload 17
    //   887: ifnull -611 -> 276
    //   890: aload 27
    //   892: astore 26
    //   894: iload 18
    //   896: istore 19
    //   898: aload 28
    //   900: astore_2
    //   901: aload 29
    //   903: astore_3
    //   904: aload 30
    //   906: astore 4
    //   908: aload 31
    //   910: astore 36
    //   912: aload 7
    //   914: astore 34
    //   916: aload 12
    //   918: astore 37
    //   920: aload 27
    //   922: astore 25
    //   924: aload 17
    //   926: ldc_w 378
    //   929: invokevirtual 226	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   932: ifeq -656 -> 276
    //   935: bipush 9
    //   937: istore 19
    //   939: aload 27
    //   941: astore 26
    //   943: aload 28
    //   945: astore_2
    //   946: aload 29
    //   948: astore_3
    //   949: aload 30
    //   951: astore 4
    //   953: aload 31
    //   955: astore 36
    //   957: aload 7
    //   959: astore 34
    //   961: aload 12
    //   963: astore 37
    //   965: goto -689 -> 276
    //   968: aload 27
    //   970: astore 25
    //   972: aload 14
    //   974: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   977: iconst_4
    //   978: if_icmpne +31 -> 1009
    //   981: aload 27
    //   983: astore 25
    //   985: aload 27
    //   987: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   990: astore 29
    //   992: iconst_1
    //   993: istore 18
    //   995: aload_2
    //   996: astore 28
    //   998: aload 4
    //   1000: astore 30
    //   1002: aload 6
    //   1004: astore 31
    //   1006: goto -151 -> 855
    //   1009: aload 27
    //   1011: astore 25
    //   1013: aload 14
    //   1015: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   1018: iconst_1
    //   1019: if_icmpne +36 -> 1055
    //   1022: aload 27
    //   1024: astore 25
    //   1026: aload 27
    //   1028: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1031: getfield 388	org/telegram/tgnet/TLRPC$MessageMedia:photo	Lorg/telegram/tgnet/TLRPC$Photo;
    //   1034: checkcast 390	org/telegram/tgnet/TLRPC$TL_photo
    //   1037: astore 30
    //   1039: iconst_2
    //   1040: istore 18
    //   1042: aload_2
    //   1043: astore 28
    //   1045: aload_3
    //   1046: astore 29
    //   1048: aload 6
    //   1050: astore 31
    //   1052: goto -197 -> 855
    //   1055: aload 27
    //   1057: astore 25
    //   1059: aload 14
    //   1061: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   1064: iconst_3
    //   1065: if_icmpne +40 -> 1105
    //   1068: iconst_3
    //   1069: istore 18
    //   1071: aload 27
    //   1073: astore 25
    //   1075: aload 27
    //   1077: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1080: getfield 394	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   1083: checkcast 396	org/telegram/tgnet/TLRPC$TL_document
    //   1086: astore 7
    //   1088: aload_2
    //   1089: astore 28
    //   1091: aload_3
    //   1092: astore 29
    //   1094: aload 4
    //   1096: astore 30
    //   1098: aload 6
    //   1100: astore 31
    //   1102: goto -247 -> 855
    //   1105: aload 27
    //   1107: astore 25
    //   1109: aload 14
    //   1111: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   1114: bipush 12
    //   1116: if_icmpne +85 -> 1201
    //   1119: aload 27
    //   1121: astore 25
    //   1123: new 398	org/telegram/tgnet/TLRPC$TL_userRequest_old2
    //   1126: dup
    //   1127: invokespecial 399	org/telegram/tgnet/TLRPC$TL_userRequest_old2:<init>	()V
    //   1130: astore 31
    //   1132: aload 31
    //   1134: aload 27
    //   1136: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1139: getfield 402	org/telegram/tgnet/TLRPC$MessageMedia:phone_number	Ljava/lang/String;
    //   1142: putfield 407	org/telegram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   1145: aload 31
    //   1147: aload 27
    //   1149: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1152: getfield 410	org/telegram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   1155: putfield 411	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   1158: aload 31
    //   1160: aload 27
    //   1162: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1165: getfield 414	org/telegram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   1168: putfield 415	org/telegram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   1171: aload 31
    //   1173: aload 27
    //   1175: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1178: getfield 418	org/telegram/tgnet/TLRPC$MessageMedia:user_id	I
    //   1181: putfield 419	org/telegram/tgnet/TLRPC$User:id	I
    //   1184: bipush 6
    //   1186: istore 18
    //   1188: aload_2
    //   1189: astore 28
    //   1191: aload_3
    //   1192: astore 29
    //   1194: aload 4
    //   1196: astore 30
    //   1198: goto -343 -> 855
    //   1201: aload 27
    //   1203: astore 25
    //   1205: aload 14
    //   1207: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   1210: bipush 8
    //   1212: if_icmpeq +45 -> 1257
    //   1215: aload 27
    //   1217: astore 25
    //   1219: aload 14
    //   1221: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   1224: bipush 9
    //   1226: if_icmpeq +31 -> 1257
    //   1229: aload 27
    //   1231: astore 25
    //   1233: aload 14
    //   1235: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   1238: bipush 13
    //   1240: if_icmpeq +17 -> 1257
    //   1243: aload 27
    //   1245: astore 25
    //   1247: aload 14
    //   1249: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   1252: bipush 14
    //   1254: if_icmpne +41 -> 1295
    //   1257: aload 27
    //   1259: astore 25
    //   1261: aload 27
    //   1263: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1266: getfield 394	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   1269: checkcast 396	org/telegram/tgnet/TLRPC$TL_document
    //   1272: astore 7
    //   1274: bipush 7
    //   1276: istore 18
    //   1278: aload_2
    //   1279: astore 28
    //   1281: aload_3
    //   1282: astore 29
    //   1284: aload 4
    //   1286: astore 30
    //   1288: aload 6
    //   1290: astore 31
    //   1292: goto -437 -> 855
    //   1295: aload 27
    //   1297: astore 25
    //   1299: aload_2
    //   1300: astore 28
    //   1302: aload_3
    //   1303: astore 29
    //   1305: aload 4
    //   1307: astore 30
    //   1309: aload 6
    //   1311: astore 31
    //   1313: aload 14
    //   1315: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   1318: iconst_2
    //   1319: if_icmpne -464 -> 855
    //   1322: aload 27
    //   1324: astore 25
    //   1326: aload 27
    //   1328: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1331: getfield 394	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   1334: checkcast 396	org/telegram/tgnet/TLRPC$TL_document
    //   1337: astore 7
    //   1339: bipush 8
    //   1341: istore 18
    //   1343: aload_2
    //   1344: astore 28
    //   1346: aload_3
    //   1347: astore 29
    //   1349: aload 4
    //   1351: astore 30
    //   1353: aload 6
    //   1355: astore 31
    //   1357: goto -502 -> 855
    //   1360: aload_2
    //   1361: ifnull +355 -> 1716
    //   1364: aload 35
    //   1366: ifnull +301 -> 1667
    //   1369: aload 27
    //   1371: astore 25
    //   1373: aload 35
    //   1375: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   1378: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   1381: bipush 17
    //   1383: if_icmplt +284 -> 1667
    //   1386: aload 27
    //   1388: astore 25
    //   1390: new 432	org/telegram/tgnet/TLRPC$TL_message_secret
    //   1393: dup
    //   1394: invokespecial 433	org/telegram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   1397: astore 26
    //   1399: aload 15
    //   1401: ifnull +26 -> 1427
    //   1404: aload 26
    //   1406: astore 25
    //   1408: aload 15
    //   1410: invokevirtual 438	java/util/ArrayList:isEmpty	()Z
    //   1413: ifne +14 -> 1427
    //   1416: aload 26
    //   1418: astore 25
    //   1420: aload 26
    //   1422: aload 15
    //   1424: putfield 442	org/telegram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   1427: aload 12
    //   1429: astore 27
    //   1431: aload 35
    //   1433: ifnull +58 -> 1491
    //   1436: aload 26
    //   1438: astore 25
    //   1440: aload 12
    //   1442: astore 27
    //   1444: aload 12
    //   1446: instanceof 444
    //   1449: ifeq +42 -> 1491
    //   1452: aload 26
    //   1454: astore 25
    //   1456: aload 12
    //   1458: getfield 449	org/telegram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   1461: ifnull +7395 -> 8856
    //   1464: aload 26
    //   1466: astore 25
    //   1468: new 451	org/telegram/tgnet/TLRPC$TL_webPageUrlPending
    //   1471: dup
    //   1472: invokespecial 452	org/telegram/tgnet/TLRPC$TL_webPageUrlPending:<init>	()V
    //   1475: astore 27
    //   1477: aload 26
    //   1479: astore 25
    //   1481: aload 27
    //   1483: aload 12
    //   1485: getfield 449	org/telegram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   1488: putfield 449	org/telegram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   1491: aload 27
    //   1493: ifnonnull +190 -> 1683
    //   1496: aload 26
    //   1498: astore 25
    //   1500: aload 26
    //   1502: new 454	org/telegram/tgnet/TLRPC$TL_messageMediaEmpty
    //   1505: dup
    //   1506: invokespecial 455	org/telegram/tgnet/TLRPC$TL_messageMediaEmpty:<init>	()V
    //   1509: putfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1512: aload 17
    //   1514: ifnull +7348 -> 8862
    //   1517: aload 26
    //   1519: astore 25
    //   1521: aload 17
    //   1523: ldc_w 378
    //   1526: invokevirtual 226	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   1529: ifeq +7333 -> 8862
    //   1532: bipush 9
    //   1534: istore 18
    //   1536: aload 26
    //   1538: astore 25
    //   1540: aload 26
    //   1542: aload_2
    //   1543: putfield 376	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   1546: aload 27
    //   1548: astore 28
    //   1550: aload 26
    //   1552: astore 25
    //   1554: aload 26
    //   1556: getfield 458	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   1559: ifnonnull +15 -> 1574
    //   1562: aload 26
    //   1564: astore 25
    //   1566: aload 26
    //   1568: ldc_w 322
    //   1571: putfield 458	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   1574: aload 26
    //   1576: astore 25
    //   1578: invokestatic 463	org/telegram/messenger/UserConfig:getNewMessageId	()I
    //   1581: istore 19
    //   1583: aload 26
    //   1585: astore 25
    //   1587: aload 26
    //   1589: iload 19
    //   1591: putfield 362	org/telegram/tgnet/TLRPC$Message:id	I
    //   1594: aload 26
    //   1596: astore 25
    //   1598: aload 26
    //   1600: iload 19
    //   1602: putfield 466	org/telegram/tgnet/TLRPC$Message:local_id	I
    //   1605: aload 26
    //   1607: astore 25
    //   1609: aload 26
    //   1611: iconst_1
    //   1612: putfield 469	org/telegram/tgnet/TLRPC$Message:out	Z
    //   1615: iload 20
    //   1617: ifeq +1338 -> 2955
    //   1620: aload 33
    //   1622: ifnull +1333 -> 2955
    //   1625: aload 26
    //   1627: astore 25
    //   1629: aload 26
    //   1631: aload 33
    //   1633: getfield 290	org/telegram/tgnet/TLRPC$InputPeer:channel_id	I
    //   1636: ineg
    //   1637: putfield 472	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   1640: aload 26
    //   1642: astore 25
    //   1644: iconst_0
    //   1645: invokestatic 476	org/telegram/messenger/UserConfig:saveConfig	(Z)V
    //   1648: iload 18
    //   1650: istore 19
    //   1652: aload 6
    //   1654: astore 36
    //   1656: aload 7
    //   1658: astore 34
    //   1660: aload 28
    //   1662: astore 37
    //   1664: goto -1388 -> 276
    //   1667: aload 27
    //   1669: astore 25
    //   1671: new 478	org/telegram/tgnet/TLRPC$TL_message
    //   1674: dup
    //   1675: invokespecial 479	org/telegram/tgnet/TLRPC$TL_message:<init>	()V
    //   1678: astore 26
    //   1680: goto -281 -> 1399
    //   1683: aload 26
    //   1685: astore 25
    //   1687: aload 26
    //   1689: new 481	org/telegram/tgnet/TLRPC$TL_messageMediaWebPage
    //   1692: dup
    //   1693: invokespecial 482	org/telegram/tgnet/TLRPC$TL_messageMediaWebPage:<init>	()V
    //   1696: putfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1699: aload 26
    //   1701: astore 25
    //   1703: aload 26
    //   1705: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1708: aload 27
    //   1710: putfield 486	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   1713: goto -201 -> 1512
    //   1716: aload_3
    //   1717: ifnull +107 -> 1824
    //   1720: aload 35
    //   1722: ifnull +86 -> 1808
    //   1725: aload 27
    //   1727: astore 25
    //   1729: aload 35
    //   1731: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   1734: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   1737: bipush 17
    //   1739: if_icmplt +69 -> 1808
    //   1742: aload 27
    //   1744: astore 25
    //   1746: new 432	org/telegram/tgnet/TLRPC$TL_message_secret
    //   1749: dup
    //   1750: invokespecial 433	org/telegram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   1753: astore 26
    //   1755: aload 26
    //   1757: astore 25
    //   1759: aload 26
    //   1761: aload_3
    //   1762: putfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1765: aload 26
    //   1767: astore 25
    //   1769: aload 26
    //   1771: ldc_w 322
    //   1774: putfield 376	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   1777: aload 17
    //   1779: ifnull +7089 -> 8868
    //   1782: aload 26
    //   1784: astore 25
    //   1786: aload 17
    //   1788: ldc_w 378
    //   1791: invokevirtual 226	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   1794: ifeq +7074 -> 8868
    //   1797: bipush 9
    //   1799: istore 18
    //   1801: aload 12
    //   1803: astore 28
    //   1805: goto -255 -> 1550
    //   1808: aload 27
    //   1810: astore 25
    //   1812: new 478	org/telegram/tgnet/TLRPC$TL_message
    //   1815: dup
    //   1816: invokespecial 479	org/telegram/tgnet/TLRPC$TL_message:<init>	()V
    //   1819: astore 26
    //   1821: goto -66 -> 1755
    //   1824: aload 4
    //   1826: ifnull +255 -> 2081
    //   1829: aload 35
    //   1831: ifnull +187 -> 2018
    //   1834: aload 27
    //   1836: astore 25
    //   1838: aload 35
    //   1840: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   1843: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   1846: bipush 17
    //   1848: if_icmplt +170 -> 2018
    //   1851: aload 27
    //   1853: astore 25
    //   1855: new 432	org/telegram/tgnet/TLRPC$TL_message_secret
    //   1858: dup
    //   1859: invokespecial 433	org/telegram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   1862: astore 26
    //   1864: aload 26
    //   1866: astore 25
    //   1868: aload 26
    //   1870: new 488	org/telegram/tgnet/TLRPC$TL_messageMediaPhoto
    //   1873: dup
    //   1874: invokespecial 489	org/telegram/tgnet/TLRPC$TL_messageMediaPhoto:<init>	()V
    //   1877: putfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1880: aload 26
    //   1882: astore 25
    //   1884: aload 26
    //   1886: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1889: astore 28
    //   1891: aload_1
    //   1892: ifnull +6986 -> 8878
    //   1895: aload 26
    //   1897: astore 25
    //   1899: aload_1
    //   1900: invokeinterface 495 1 0
    //   1905: astore 27
    //   1907: aload 26
    //   1909: astore 25
    //   1911: aload 28
    //   1913: aload 27
    //   1915: putfield 498	org/telegram/tgnet/TLRPC$MessageMedia:caption	Ljava/lang/String;
    //   1918: aload 26
    //   1920: astore 25
    //   1922: aload 26
    //   1924: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1927: aload 4
    //   1929: putfield 388	org/telegram/tgnet/TLRPC$MessageMedia:photo	Lorg/telegram/tgnet/TLRPC$Photo;
    //   1932: aload 17
    //   1934: ifnull +6952 -> 8886
    //   1937: aload 26
    //   1939: astore 25
    //   1941: aload 17
    //   1943: ldc_w 378
    //   1946: invokevirtual 226	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   1949: ifeq +6937 -> 8886
    //   1952: bipush 9
    //   1954: istore 18
    //   1956: aload 26
    //   1958: astore 25
    //   1960: aload 26
    //   1962: ldc_w 500
    //   1965: putfield 376	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   1968: aload 10
    //   1970: ifnull +64 -> 2034
    //   1973: aload 26
    //   1975: astore 25
    //   1977: aload 10
    //   1979: invokevirtual 205	java/lang/String:length	()I
    //   1982: ifle +52 -> 2034
    //   1985: aload 26
    //   1987: astore 25
    //   1989: aload 10
    //   1991: ldc_w 502
    //   1994: invokevirtual 506	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   1997: ifeq +37 -> 2034
    //   2000: aload 26
    //   2002: astore 25
    //   2004: aload 26
    //   2006: aload 10
    //   2008: putfield 458	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   2011: aload 12
    //   2013: astore 28
    //   2015: goto -465 -> 1550
    //   2018: aload 27
    //   2020: astore 25
    //   2022: new 478	org/telegram/tgnet/TLRPC$TL_message
    //   2025: dup
    //   2026: invokespecial 479	org/telegram/tgnet/TLRPC$TL_message:<init>	()V
    //   2029: astore 26
    //   2031: goto -167 -> 1864
    //   2034: aload 26
    //   2036: astore 25
    //   2038: aload 26
    //   2040: aload 4
    //   2042: getfield 509	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   2045: aload 4
    //   2047: getfield 509	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   2050: invokevirtual 512	java/util/ArrayList:size	()I
    //   2053: iconst_1
    //   2054: isub
    //   2055: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   2058: checkcast 517	org/telegram/tgnet/TLRPC$PhotoSize
    //   2061: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2064: iconst_1
    //   2065: invokestatic 527	org/telegram/messenger/FileLoader:getPathToAttach	(Lorg/telegram/tgnet/TLObject;Z)Ljava/io/File;
    //   2068: invokevirtual 530	java/io/File:toString	()Ljava/lang/String;
    //   2071: putfield 458	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   2074: aload 12
    //   2076: astore 28
    //   2078: goto -528 -> 1550
    //   2081: aload 6
    //   2083: ifnull +265 -> 2348
    //   2086: aload 35
    //   2088: ifnull +244 -> 2332
    //   2091: aload 27
    //   2093: astore 25
    //   2095: aload 35
    //   2097: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   2100: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   2103: bipush 17
    //   2105: if_icmplt +227 -> 2332
    //   2108: aload 27
    //   2110: astore 25
    //   2112: new 432	org/telegram/tgnet/TLRPC$TL_message_secret
    //   2115: dup
    //   2116: invokespecial 433	org/telegram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   2119: astore 26
    //   2121: aload 26
    //   2123: astore 25
    //   2125: aload 26
    //   2127: new 532	org/telegram/tgnet/TLRPC$TL_messageMediaContact
    //   2130: dup
    //   2131: invokespecial 533	org/telegram/tgnet/TLRPC$TL_messageMediaContact:<init>	()V
    //   2134: putfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2137: aload 26
    //   2139: astore 25
    //   2141: aload 26
    //   2143: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2146: aload 6
    //   2148: getfield 407	org/telegram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   2151: putfield 402	org/telegram/tgnet/TLRPC$MessageMedia:phone_number	Ljava/lang/String;
    //   2154: aload 26
    //   2156: astore 25
    //   2158: aload 26
    //   2160: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2163: aload 6
    //   2165: getfield 411	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   2168: putfield 410	org/telegram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   2171: aload 26
    //   2173: astore 25
    //   2175: aload 26
    //   2177: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2180: aload 6
    //   2182: getfield 415	org/telegram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   2185: putfield 414	org/telegram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   2188: aload 26
    //   2190: astore 25
    //   2192: aload 26
    //   2194: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2197: aload 6
    //   2199: getfield 419	org/telegram/tgnet/TLRPC$User:id	I
    //   2202: putfield 418	org/telegram/tgnet/TLRPC$MessageMedia:user_id	I
    //   2205: aload 26
    //   2207: astore 25
    //   2209: aload 26
    //   2211: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2214: getfield 410	org/telegram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   2217: ifnonnull +30 -> 2247
    //   2220: aload 26
    //   2222: astore 25
    //   2224: aload 26
    //   2226: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2229: ldc_w 322
    //   2232: putfield 410	org/telegram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   2235: aload 26
    //   2237: astore 25
    //   2239: aload 6
    //   2241: ldc_w 322
    //   2244: putfield 411	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   2247: aload 26
    //   2249: astore 25
    //   2251: aload 26
    //   2253: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2256: getfield 414	org/telegram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   2259: ifnonnull +30 -> 2289
    //   2262: aload 26
    //   2264: astore 25
    //   2266: aload 26
    //   2268: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2271: ldc_w 322
    //   2274: putfield 414	org/telegram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   2277: aload 26
    //   2279: astore 25
    //   2281: aload 6
    //   2283: ldc_w 322
    //   2286: putfield 415	org/telegram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   2289: aload 26
    //   2291: astore 25
    //   2293: aload 26
    //   2295: ldc_w 322
    //   2298: putfield 376	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   2301: aload 17
    //   2303: ifnull +6589 -> 8892
    //   2306: aload 26
    //   2308: astore 25
    //   2310: aload 17
    //   2312: ldc_w 378
    //   2315: invokevirtual 226	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   2318: ifeq +6574 -> 8892
    //   2321: bipush 9
    //   2323: istore 18
    //   2325: aload 12
    //   2327: astore 28
    //   2329: goto -779 -> 1550
    //   2332: aload 27
    //   2334: astore 25
    //   2336: new 478	org/telegram/tgnet/TLRPC$TL_message
    //   2339: dup
    //   2340: invokespecial 479	org/telegram/tgnet/TLRPC$TL_message:<init>	()V
    //   2343: astore 26
    //   2345: goto -224 -> 2121
    //   2348: aload 12
    //   2350: astore 28
    //   2352: aload 7
    //   2354: ifnull -804 -> 1550
    //   2357: aload 35
    //   2359: ifnull +359 -> 2718
    //   2362: aload 27
    //   2364: astore 25
    //   2366: aload 35
    //   2368: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   2371: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   2374: bipush 17
    //   2376: if_icmplt +342 -> 2718
    //   2379: aload 27
    //   2381: astore 25
    //   2383: new 432	org/telegram/tgnet/TLRPC$TL_message_secret
    //   2386: dup
    //   2387: invokespecial 433	org/telegram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   2390: astore 27
    //   2392: aload 27
    //   2394: astore 25
    //   2396: aload 27
    //   2398: new 535	org/telegram/tgnet/TLRPC$TL_messageMediaDocument
    //   2401: dup
    //   2402: invokespecial 536	org/telegram/tgnet/TLRPC$TL_messageMediaDocument:<init>	()V
    //   2405: putfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2408: aload 27
    //   2410: astore 25
    //   2412: aload 27
    //   2414: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2417: astore 28
    //   2419: aload_1
    //   2420: ifnull +6483 -> 8903
    //   2423: aload 27
    //   2425: astore 25
    //   2427: aload_1
    //   2428: invokeinterface 495 1 0
    //   2433: astore 26
    //   2435: aload 27
    //   2437: astore 25
    //   2439: aload 28
    //   2441: aload 26
    //   2443: putfield 498	org/telegram/tgnet/TLRPC$MessageMedia:caption	Ljava/lang/String;
    //   2446: aload 27
    //   2448: astore 25
    //   2450: aload 27
    //   2452: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2455: aload 7
    //   2457: putfield 394	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   2460: aload 17
    //   2462: ifnull +272 -> 2734
    //   2465: aload 27
    //   2467: astore 25
    //   2469: aload 17
    //   2471: ldc_w 378
    //   2474: invokevirtual 226	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   2477: ifeq +257 -> 2734
    //   2480: bipush 9
    //   2482: istore 19
    //   2484: aload 5
    //   2486: ifnonnull +285 -> 2771
    //   2489: aload 27
    //   2491: astore 25
    //   2493: aload 27
    //   2495: ldc_w 500
    //   2498: putfield 376	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   2501: aload 35
    //   2503: ifnull +285 -> 2788
    //   2506: aload 27
    //   2508: astore 25
    //   2510: aload 7
    //   2512: getfield 539	org/telegram/tgnet/TLRPC$TL_document:dc_id	I
    //   2515: ifle +273 -> 2788
    //   2518: aload 27
    //   2520: astore 25
    //   2522: aload 7
    //   2524: invokestatic 543	org/telegram/messenger/MessageObject:isStickerDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   2527: ifne +261 -> 2788
    //   2530: aload 27
    //   2532: astore 25
    //   2534: aload 27
    //   2536: aload 7
    //   2538: invokestatic 546	org/telegram/messenger/FileLoader:getPathToAttach	(Lorg/telegram/tgnet/TLObject;)Ljava/io/File;
    //   2541: invokevirtual 530	java/io/File:toString	()Ljava/lang/String;
    //   2544: putfield 458	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   2547: aload 27
    //   2549: astore 26
    //   2551: iload 19
    //   2553: istore 18
    //   2555: aload 12
    //   2557: astore 28
    //   2559: aload 35
    //   2561: ifnull -1011 -> 1550
    //   2564: aload 27
    //   2566: astore 25
    //   2568: aload 27
    //   2570: astore 26
    //   2572: iload 19
    //   2574: istore 18
    //   2576: aload 12
    //   2578: astore 28
    //   2580: aload 7
    //   2582: invokestatic 543	org/telegram/messenger/MessageObject:isStickerDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   2585: ifeq -1035 -> 1550
    //   2588: iconst_0
    //   2589: istore 21
    //   2591: aload 27
    //   2593: astore 25
    //   2595: aload 27
    //   2597: astore 26
    //   2599: iload 19
    //   2601: istore 18
    //   2603: aload 12
    //   2605: astore 28
    //   2607: iload 21
    //   2609: aload 7
    //   2611: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   2614: invokevirtual 512	java/util/ArrayList:size	()I
    //   2617: if_icmpge -1067 -> 1550
    //   2620: aload 27
    //   2622: astore 25
    //   2624: aload 7
    //   2626: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   2629: iload 21
    //   2631: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   2634: checkcast 551	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   2637: astore 26
    //   2639: aload 27
    //   2641: astore 25
    //   2643: aload 26
    //   2645: instanceof 553
    //   2648: ifeq +6270 -> 8918
    //   2651: aload 27
    //   2653: astore 25
    //   2655: aload 35
    //   2657: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   2660: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   2663: bipush 46
    //   2665: if_icmpge +137 -> 2802
    //   2668: aload 27
    //   2670: astore 25
    //   2672: aload 7
    //   2674: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   2677: iload 21
    //   2679: invokevirtual 556	java/util/ArrayList:remove	(I)Ljava/lang/Object;
    //   2682: pop
    //   2683: aload 27
    //   2685: astore 25
    //   2687: aload 7
    //   2689: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   2692: new 558	org/telegram/tgnet/TLRPC$TL_documentAttributeSticker_old
    //   2695: dup
    //   2696: invokespecial 559	org/telegram/tgnet/TLRPC$TL_documentAttributeSticker_old:<init>	()V
    //   2699: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   2702: pop
    //   2703: aload 27
    //   2705: astore 26
    //   2707: iload 19
    //   2709: istore 18
    //   2711: aload 12
    //   2713: astore 28
    //   2715: goto -1165 -> 1550
    //   2718: aload 27
    //   2720: astore 25
    //   2722: new 478	org/telegram/tgnet/TLRPC$TL_message
    //   2725: dup
    //   2726: invokespecial 479	org/telegram/tgnet/TLRPC$TL_message:<init>	()V
    //   2729: astore 27
    //   2731: goto -339 -> 2392
    //   2734: aload 27
    //   2736: astore 25
    //   2738: aload 7
    //   2740: invokestatic 565	org/telegram/messenger/MessageObject:isVideoDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   2743: ifeq +9 -> 2752
    //   2746: iconst_3
    //   2747: istore 19
    //   2749: goto -265 -> 2484
    //   2752: aload 27
    //   2754: astore 25
    //   2756: aload 7
    //   2758: invokestatic 568	org/telegram/messenger/MessageObject:isVoiceDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   2761: ifeq +6150 -> 8911
    //   2764: bipush 8
    //   2766: istore 19
    //   2768: goto -284 -> 2484
    //   2771: aload 27
    //   2773: astore 25
    //   2775: aload 27
    //   2777: aload 5
    //   2779: invokevirtual 573	org/telegram/messenger/VideoEditedInfo:getString	()Ljava/lang/String;
    //   2782: putfield 376	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   2785: goto -284 -> 2501
    //   2788: aload 27
    //   2790: astore 25
    //   2792: aload 27
    //   2794: aload 10
    //   2796: putfield 458	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   2799: goto -252 -> 2547
    //   2802: aload 27
    //   2804: astore 25
    //   2806: aload 26
    //   2808: getfield 577	org/telegram/tgnet/TLRPC$DocumentAttribute:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   2811: ifnull +113 -> 2924
    //   2814: aload 27
    //   2816: astore 25
    //   2818: aload 26
    //   2820: getfield 577	org/telegram/tgnet/TLRPC$DocumentAttribute:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   2823: getfield 581	org/telegram/tgnet/TLRPC$InputStickerSet:id	J
    //   2826: invokestatic 587	org/telegram/messenger/query/StickersQuery:getStickerSetName	(J)Ljava/lang/String;
    //   2829: astore 28
    //   2831: aload 28
    //   2833: ifnull +60 -> 2893
    //   2836: aload 27
    //   2838: astore 25
    //   2840: aload 28
    //   2842: invokevirtual 205	java/lang/String:length	()I
    //   2845: ifle +48 -> 2893
    //   2848: aload 27
    //   2850: astore 25
    //   2852: aload 26
    //   2854: new 589	org/telegram/tgnet/TLRPC$TL_inputStickerSetShortName
    //   2857: dup
    //   2858: invokespecial 590	org/telegram/tgnet/TLRPC$TL_inputStickerSetShortName:<init>	()V
    //   2861: putfield 577	org/telegram/tgnet/TLRPC$DocumentAttribute:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   2864: aload 27
    //   2866: astore 25
    //   2868: aload 26
    //   2870: getfield 577	org/telegram/tgnet/TLRPC$DocumentAttribute:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   2873: aload 28
    //   2875: putfield 593	org/telegram/tgnet/TLRPC$InputStickerSet:short_name	Ljava/lang/String;
    //   2878: aload 27
    //   2880: astore 26
    //   2882: iload 19
    //   2884: istore 18
    //   2886: aload 12
    //   2888: astore 28
    //   2890: goto -1340 -> 1550
    //   2893: aload 27
    //   2895: astore 25
    //   2897: aload 26
    //   2899: new 595	org/telegram/tgnet/TLRPC$TL_inputStickerSetEmpty
    //   2902: dup
    //   2903: invokespecial 596	org/telegram/tgnet/TLRPC$TL_inputStickerSetEmpty:<init>	()V
    //   2906: putfield 577	org/telegram/tgnet/TLRPC$DocumentAttribute:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   2909: aload 27
    //   2911: astore 26
    //   2913: iload 19
    //   2915: istore 18
    //   2917: aload 12
    //   2919: astore 28
    //   2921: goto -1371 -> 1550
    //   2924: aload 27
    //   2926: astore 25
    //   2928: aload 26
    //   2930: new 595	org/telegram/tgnet/TLRPC$TL_inputStickerSetEmpty
    //   2933: dup
    //   2934: invokespecial 596	org/telegram/tgnet/TLRPC$TL_inputStickerSetEmpty:<init>	()V
    //   2937: putfield 577	org/telegram/tgnet/TLRPC$DocumentAttribute:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   2940: aload 27
    //   2942: astore 26
    //   2944: iload 19
    //   2946: istore 18
    //   2948: aload 12
    //   2950: astore 28
    //   2952: goto -1402 -> 1550
    //   2955: aload 26
    //   2957: astore 25
    //   2959: aload 26
    //   2961: invokestatic 599	org/telegram/messenger/UserConfig:getClientUserId	()I
    //   2964: putfield 472	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   2967: aload 26
    //   2969: astore 25
    //   2971: aload 26
    //   2973: aload 26
    //   2975: getfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   2978: sipush 256
    //   2981: ior
    //   2982: putfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   2985: goto -1345 -> 1640
    //   2988: aload 26
    //   2990: astore 25
    //   2992: aload 26
    //   2994: aload 17
    //   2996: ldc_w 314
    //   2999: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   3002: checkcast 201	java/lang/String
    //   3005: invokestatic 605	org/telegram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
    //   3008: invokevirtual 608	java/lang/Integer:intValue	()I
    //   3011: putfield 611	org/telegram/tgnet/TLRPC$Message:via_bot_id	I
    //   3014: goto -2642 -> 372
    //   3017: aload 26
    //   3019: astore 25
    //   3021: aload 26
    //   3023: iconst_1
    //   3024: putfield 614	org/telegram/tgnet/TLRPC$Message:post	Z
    //   3027: aload 26
    //   3029: astore 25
    //   3031: aload 6
    //   3033: getfield 617	org/telegram/tgnet/TLRPC$Chat:signatures	Z
    //   3036: ifeq -2492 -> 544
    //   3039: aload 26
    //   3041: astore 25
    //   3043: aload 26
    //   3045: invokestatic 599	org/telegram/messenger/UserConfig:getClientUserId	()I
    //   3048: putfield 472	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   3051: goto -2507 -> 544
    //   3054: aload 26
    //   3056: astore 25
    //   3058: aload 26
    //   3060: iconst_1
    //   3061: putfield 346	org/telegram/tgnet/TLRPC$Message:unread	Z
    //   3064: goto -2520 -> 544
    //   3067: aload 26
    //   3069: astore 25
    //   3071: aload 26
    //   3073: aload 26
    //   3075: getfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   3078: bipush 8
    //   3080: ior
    //   3081: putfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   3084: goto -2468 -> 616
    //   3087: aload 26
    //   3089: astore 25
    //   3091: new 435	java/util/ArrayList
    //   3094: dup
    //   3095: invokespecial 618	java/util/ArrayList:<init>	()V
    //   3098: astore 6
    //   3100: aload_0
    //   3101: getfield 138	org/telegram/messenger/SendMessagesHelper:currentChatInfo	Lorg/telegram/tgnet/TLRPC$ChatFull;
    //   3104: getfield 624	org/telegram/tgnet/TLRPC$ChatFull:participants	Lorg/telegram/tgnet/TLRPC$ChatParticipants;
    //   3107: getfield 628	org/telegram/tgnet/TLRPC$ChatParticipants:participants	Ljava/util/ArrayList;
    //   3110: invokevirtual 632	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   3113: astore 7
    //   3115: aload 7
    //   3117: invokeinterface 637 1 0
    //   3122: ifeq +50 -> 3172
    //   3125: aload 7
    //   3127: invokeinterface 641 1 0
    //   3132: checkcast 643	org/telegram/tgnet/TLRPC$ChatParticipant
    //   3135: astore 12
    //   3137: invokestatic 239	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   3140: aload 12
    //   3142: getfield 644	org/telegram/tgnet/TLRPC$ChatParticipant:user_id	I
    //   3145: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3148: invokevirtual 648	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   3151: invokestatic 652	org/telegram/messenger/MessagesController:getInputUser	(Lorg/telegram/tgnet/TLRPC$User;)Lorg/telegram/tgnet/TLRPC$InputUser;
    //   3154: astore 12
    //   3156: aload 12
    //   3158: ifnull -43 -> 3115
    //   3161: aload 6
    //   3163: aload 12
    //   3165: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   3168: pop
    //   3169: goto -54 -> 3115
    //   3172: aload 26
    //   3174: new 654	org/telegram/tgnet/TLRPC$TL_peerChat
    //   3177: dup
    //   3178: invokespecial 655	org/telegram/tgnet/TLRPC$TL_peerChat:<init>	()V
    //   3181: putfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3184: aload 26
    //   3186: getfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3189: iload 23
    //   3191: putfield 664	org/telegram/tgnet/TLRPC$Peer:chat_id	I
    //   3194: aload 35
    //   3196: ifnull +20 -> 3216
    //   3199: aload 26
    //   3201: astore 25
    //   3203: aload 35
    //   3205: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   3208: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   3211: bipush 46
    //   3213: if_icmplt +46 -> 3259
    //   3216: iload 22
    //   3218: iconst_1
    //   3219: if_icmpeq +40 -> 3259
    //   3222: aload 26
    //   3224: astore 25
    //   3226: aload 26
    //   3228: invokestatic 668	org/telegram/messenger/MessageObject:isVoiceMessage	(Lorg/telegram/tgnet/TLRPC$Message;)Z
    //   3231: ifeq +28 -> 3259
    //   3234: aload 26
    //   3236: astore 25
    //   3238: aload 26
    //   3240: getfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3243: getfield 669	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   3246: ifne +13 -> 3259
    //   3249: aload 26
    //   3251: astore 25
    //   3253: aload 26
    //   3255: iconst_1
    //   3256: putfield 672	org/telegram/tgnet/TLRPC$Message:media_unread	Z
    //   3259: aload 26
    //   3261: astore 25
    //   3263: aload 26
    //   3265: iconst_1
    //   3266: putfield 269	org/telegram/tgnet/TLRPC$Message:send_state	I
    //   3269: aload 26
    //   3271: astore 25
    //   3273: new 256	org/telegram/messenger/MessageObject
    //   3276: dup
    //   3277: aload 26
    //   3279: aconst_null
    //   3280: iconst_1
    //   3281: invokespecial 675	org/telegram/messenger/MessageObject:<init>	(Lorg/telegram/tgnet/TLRPC$Message;Ljava/util/AbstractMap;Z)V
    //   3284: astore 7
    //   3286: aload 7
    //   3288: aload 11
    //   3290: putfield 679	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   3293: aload 7
    //   3295: invokevirtual 304	org/telegram/messenger/MessageObject:isForwarded	()Z
    //   3298: ifne +18 -> 3316
    //   3301: aload 7
    //   3303: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   3306: iconst_3
    //   3307: if_icmpne +9 -> 3316
    //   3310: aload 7
    //   3312: iconst_1
    //   3313: putfield 682	org/telegram/messenger/MessageObject:attachPathExists	Z
    //   3316: new 435	java/util/ArrayList
    //   3319: dup
    //   3320: invokespecial 618	java/util/ArrayList:<init>	()V
    //   3323: astore 12
    //   3325: aload 12
    //   3327: aload 7
    //   3329: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   3332: pop
    //   3333: new 435	java/util/ArrayList
    //   3336: dup
    //   3337: invokespecial 618	java/util/ArrayList:<init>	()V
    //   3340: astore 16
    //   3342: aload 16
    //   3344: aload 26
    //   3346: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   3349: pop
    //   3350: invokestatic 254	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
    //   3353: aload 16
    //   3355: iconst_0
    //   3356: iconst_1
    //   3357: iconst_0
    //   3358: iconst_0
    //   3359: invokevirtual 686	org/telegram/messenger/MessagesStorage:putMessages	(Ljava/util/ArrayList;ZZZI)V
    //   3362: invokestatic 239	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   3365: lload 8
    //   3367: aload 12
    //   3369: invokevirtual 690	org/telegram/messenger/MessagesController:updateInterfaceWithMessages	(JLjava/util/ArrayList;)V
    //   3372: invokestatic 165	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   3375: getstatic 693	org/telegram/messenger/NotificationCenter:dialogsNeedReload	I
    //   3378: iconst_0
    //   3379: anewarray 4	java/lang/Object
    //   3382: invokevirtual 279	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   3385: getstatic 698	org/telegram/messenger/BuildVars:DEBUG_VERSION	Z
    //   3388: ifeq +5549 -> 8937
    //   3391: aload 33
    //   3393: ifnull +5544 -> 8937
    //   3396: ldc_w 364
    //   3399: new 700	java/lang/StringBuilder
    //   3402: dup
    //   3403: invokespecial 701	java/lang/StringBuilder:<init>	()V
    //   3406: ldc_w 703
    //   3409: invokevirtual 707	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3412: aload 33
    //   3414: getfield 708	org/telegram/tgnet/TLRPC$InputPeer:user_id	I
    //   3417: invokevirtual 711	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3420: ldc_w 713
    //   3423: invokevirtual 707	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3426: aload 33
    //   3428: getfield 714	org/telegram/tgnet/TLRPC$InputPeer:chat_id	I
    //   3431: invokevirtual 711	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3434: ldc_w 716
    //   3437: invokevirtual 707	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3440: aload 33
    //   3442: getfield 290	org/telegram/tgnet/TLRPC$InputPeer:channel_id	I
    //   3445: invokevirtual 711	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3448: ldc_w 718
    //   3451: invokevirtual 707	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3454: aload 33
    //   3456: getfield 721	org/telegram/tgnet/TLRPC$InputPeer:access_hash	J
    //   3459: invokevirtual 724	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   3462: invokevirtual 725	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3465: invokestatic 728	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   3468: goto +5469 -> 8937
    //   3471: aload 35
    //   3473: ifnonnull +719 -> 4192
    //   3476: aload 6
    //   3478: ifnull +536 -> 4014
    //   3481: new 730	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast
    //   3484: dup
    //   3485: invokespecial 731	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:<init>	()V
    //   3488: astore_1
    //   3489: new 435	java/util/ArrayList
    //   3492: dup
    //   3493: invokespecial 618	java/util/ArrayList:<init>	()V
    //   3496: astore_3
    //   3497: iconst_0
    //   3498: istore 18
    //   3500: iload 18
    //   3502: aload 6
    //   3504: invokevirtual 512	java/util/ArrayList:size	()I
    //   3507: if_icmpge +471 -> 3978
    //   3510: aload_3
    //   3511: getstatic 735	org/telegram/messenger/Utilities:random	Ljava/security/SecureRandom;
    //   3514: invokevirtual 740	java/security/SecureRandom:nextLong	()J
    //   3517: invokestatic 745	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   3520: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   3523: pop
    //   3524: iload 18
    //   3526: iconst_1
    //   3527: iadd
    //   3528: istore 18
    //   3530: goto -30 -> 3500
    //   3533: aload 26
    //   3535: astore 25
    //   3537: aload 26
    //   3539: iload 23
    //   3541: invokestatic 749	org/telegram/messenger/MessagesController:getPeer	(I)Lorg/telegram/tgnet/TLRPC$Peer;
    //   3544: putfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3547: aload 38
    //   3549: astore 6
    //   3551: iload 23
    //   3553: ifle -359 -> 3194
    //   3556: aload 26
    //   3558: astore 25
    //   3560: invokestatic 239	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   3563: iload 23
    //   3565: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3568: invokevirtual 648	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   3571: astore 7
    //   3573: aload 7
    //   3575: ifnonnull +17 -> 3592
    //   3578: aload 26
    //   3580: astore 25
    //   3582: aload_0
    //   3583: aload 26
    //   3585: getfield 362	org/telegram/tgnet/TLRPC$Message:id	I
    //   3588: invokevirtual 283	org/telegram/messenger/SendMessagesHelper:processSentMessage	(I)V
    //   3591: return
    //   3592: aload 26
    //   3594: astore 25
    //   3596: aload 38
    //   3598: astore 6
    //   3600: aload 7
    //   3602: getfield 751	org/telegram/tgnet/TLRPC$User:bot	Z
    //   3605: ifeq -411 -> 3194
    //   3608: aload 26
    //   3610: astore 25
    //   3612: aload 26
    //   3614: iconst_0
    //   3615: putfield 346	org/telegram/tgnet/TLRPC$Message:unread	Z
    //   3618: aload 38
    //   3620: astore 6
    //   3622: goto -428 -> 3194
    //   3625: aload 26
    //   3627: astore 25
    //   3629: aload 26
    //   3631: new 753	org/telegram/tgnet/TLRPC$TL_peerUser
    //   3634: dup
    //   3635: invokespecial 754	org/telegram/tgnet/TLRPC$TL_peerUser:<init>	()V
    //   3638: putfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3641: aload 26
    //   3643: astore 25
    //   3645: aload 35
    //   3647: getfield 757	org/telegram/tgnet/TLRPC$EncryptedChat:participant_id	I
    //   3650: invokestatic 599	org/telegram/messenger/UserConfig:getClientUserId	()I
    //   3653: if_icmpne +171 -> 3824
    //   3656: aload 26
    //   3658: astore 25
    //   3660: aload 26
    //   3662: getfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3665: aload 35
    //   3667: getfield 760	org/telegram/tgnet/TLRPC$EncryptedChat:admin_id	I
    //   3670: putfield 761	org/telegram/tgnet/TLRPC$Peer:user_id	I
    //   3673: aload 26
    //   3675: astore 25
    //   3677: aload 26
    //   3679: aload 35
    //   3681: getfield 764	org/telegram/tgnet/TLRPC$EncryptedChat:ttl	I
    //   3684: putfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   3687: aload 26
    //   3689: astore 25
    //   3691: aload 38
    //   3693: astore 6
    //   3695: aload 26
    //   3697: getfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   3700: ifeq -506 -> 3194
    //   3703: aload 26
    //   3705: astore 25
    //   3707: aload 26
    //   3709: invokestatic 668	org/telegram/messenger/MessageObject:isVoiceMessage	(Lorg/telegram/tgnet/TLRPC$Message;)Z
    //   3712: ifeq +132 -> 3844
    //   3715: iconst_0
    //   3716: istore 21
    //   3718: iconst_0
    //   3719: istore 18
    //   3721: aload 26
    //   3723: astore 25
    //   3725: iload 21
    //   3727: istore 20
    //   3729: iload 18
    //   3731: aload 26
    //   3733: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3736: getfield 394	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   3739: getfield 768	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3742: invokevirtual 512	java/util/ArrayList:size	()I
    //   3745: if_icmpge +51 -> 3796
    //   3748: aload 26
    //   3750: astore 25
    //   3752: aload 26
    //   3754: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3757: getfield 394	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   3760: getfield 768	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3763: iload 18
    //   3765: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   3768: checkcast 551	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   3771: astore 6
    //   3773: aload 26
    //   3775: astore 25
    //   3777: aload 6
    //   3779: instanceof 770
    //   3782: ifeq +5179 -> 8961
    //   3785: aload 26
    //   3787: astore 25
    //   3789: aload 6
    //   3791: getfield 773	org/telegram/tgnet/TLRPC$DocumentAttribute:duration	I
    //   3794: istore 20
    //   3796: aload 26
    //   3798: astore 25
    //   3800: aload 26
    //   3802: aload 35
    //   3804: getfield 764	org/telegram/tgnet/TLRPC$EncryptedChat:ttl	I
    //   3807: iload 20
    //   3809: iconst_1
    //   3810: iadd
    //   3811: invokestatic 779	java/lang/Math:max	(II)I
    //   3814: putfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   3817: aload 38
    //   3819: astore 6
    //   3821: goto -627 -> 3194
    //   3824: aload 26
    //   3826: astore 25
    //   3828: aload 26
    //   3830: getfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3833: aload 35
    //   3835: getfield 757	org/telegram/tgnet/TLRPC$EncryptedChat:participant_id	I
    //   3838: putfield 761	org/telegram/tgnet/TLRPC$Peer:user_id	I
    //   3841: goto -168 -> 3673
    //   3844: aload 26
    //   3846: astore 25
    //   3848: aload 38
    //   3850: astore 6
    //   3852: aload 26
    //   3854: invokestatic 782	org/telegram/messenger/MessageObject:isVideoMessage	(Lorg/telegram/tgnet/TLRPC$Message;)Z
    //   3857: ifeq -663 -> 3194
    //   3860: iconst_0
    //   3861: istore 21
    //   3863: iconst_0
    //   3864: istore 18
    //   3866: aload 26
    //   3868: astore 25
    //   3870: iload 21
    //   3872: istore 20
    //   3874: iload 18
    //   3876: aload 26
    //   3878: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3881: getfield 394	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   3884: getfield 768	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3887: invokevirtual 512	java/util/ArrayList:size	()I
    //   3890: if_icmpge +51 -> 3941
    //   3893: aload 26
    //   3895: astore 25
    //   3897: aload 26
    //   3899: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3902: getfield 394	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   3905: getfield 768	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3908: iload 18
    //   3910: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   3913: checkcast 551	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   3916: astore 6
    //   3918: aload 26
    //   3920: astore 25
    //   3922: aload 6
    //   3924: instanceof 784
    //   3927: ifeq +42 -> 3969
    //   3930: aload 26
    //   3932: astore 25
    //   3934: aload 6
    //   3936: getfield 773	org/telegram/tgnet/TLRPC$DocumentAttribute:duration	I
    //   3939: istore 20
    //   3941: aload 26
    //   3943: astore 25
    //   3945: aload 26
    //   3947: aload 35
    //   3949: getfield 764	org/telegram/tgnet/TLRPC$EncryptedChat:ttl	I
    //   3952: iload 20
    //   3954: iconst_1
    //   3955: iadd
    //   3956: invokestatic 779	java/lang/Math:max	(II)I
    //   3959: putfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   3962: aload 38
    //   3964: astore 6
    //   3966: goto -772 -> 3194
    //   3969: iload 18
    //   3971: iconst_1
    //   3972: iadd
    //   3973: istore 18
    //   3975: goto -109 -> 3866
    //   3978: aload_1
    //   3979: aload_2
    //   3980: putfield 785	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:message	Ljava/lang/String;
    //   3983: aload_1
    //   3984: aload 6
    //   3986: putfield 788	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:contacts	Ljava/util/ArrayList;
    //   3989: aload_1
    //   3990: new 790	org/telegram/tgnet/TLRPC$TL_inputMediaEmpty
    //   3993: dup
    //   3994: invokespecial 791	org/telegram/tgnet/TLRPC$TL_inputMediaEmpty:<init>	()V
    //   3997: putfield 794	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:media	Lorg/telegram/tgnet/TLRPC$InputMedia;
    //   4000: aload_1
    //   4001: aload_3
    //   4002: putfield 796	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:random_id	Ljava/util/ArrayList;
    //   4005: aload_0
    //   4006: aload_1
    //   4007: aload 7
    //   4009: aconst_null
    //   4010: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   4013: return
    //   4014: new 802	org/telegram/tgnet/TLRPC$TL_messages_sendMessage
    //   4017: dup
    //   4018: invokespecial 803	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:<init>	()V
    //   4021: astore_1
    //   4022: aload_1
    //   4023: aload_2
    //   4024: putfield 804	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:message	Ljava/lang/String;
    //   4027: aload 14
    //   4029: ifnonnull +4952 -> 8981
    //   4032: iconst_1
    //   4033: istore 24
    //   4035: aload_1
    //   4036: iload 24
    //   4038: putfield 807	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:clear_draft	Z
    //   4041: aload 26
    //   4043: getfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   4046: instanceof 809
    //   4049: ifeq +44 -> 4093
    //   4052: aload_1
    //   4053: getstatic 815	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   4056: ldc_w 817
    //   4059: iconst_0
    //   4060: invokevirtual 823	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   4063: new 700	java/lang/StringBuilder
    //   4066: dup
    //   4067: invokespecial 701	java/lang/StringBuilder:<init>	()V
    //   4070: ldc_w 825
    //   4073: invokevirtual 707	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4076: lload 8
    //   4078: invokevirtual 724	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   4081: invokevirtual 725	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4084: iconst_0
    //   4085: invokeinterface 831 3 0
    //   4090: putfield 834	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:silent	Z
    //   4093: aload_1
    //   4094: aload 33
    //   4096: putfield 838	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   4099: aload_1
    //   4100: aload 26
    //   4102: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   4105: putfield 839	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:random_id	J
    //   4108: aload 11
    //   4110: ifnull +22 -> 4132
    //   4113: aload_1
    //   4114: aload_1
    //   4115: getfield 840	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:flags	I
    //   4118: iconst_1
    //   4119: ior
    //   4120: putfield 840	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:flags	I
    //   4123: aload_1
    //   4124: aload 11
    //   4126: invokevirtual 275	org/telegram/messenger/MessageObject:getId	()I
    //   4129: putfield 841	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:reply_to_msg_id	I
    //   4132: iload 13
    //   4134: ifne +8 -> 4142
    //   4137: aload_1
    //   4138: iconst_1
    //   4139: putfield 844	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:no_webpage	Z
    //   4142: aload 15
    //   4144: ifnull +28 -> 4172
    //   4147: aload 15
    //   4149: invokevirtual 438	java/util/ArrayList:isEmpty	()Z
    //   4152: ifne +20 -> 4172
    //   4155: aload_1
    //   4156: aload 15
    //   4158: putfield 845	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:entities	Ljava/util/ArrayList;
    //   4161: aload_1
    //   4162: aload_1
    //   4163: getfield 840	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:flags	I
    //   4166: bipush 8
    //   4168: ior
    //   4169: putfield 840	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:flags	I
    //   4172: aload_0
    //   4173: aload_1
    //   4174: aload 7
    //   4176: aconst_null
    //   4177: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   4180: aload 14
    //   4182: ifnonnull -4175 -> 7
    //   4185: lload 8
    //   4187: iconst_0
    //   4188: invokestatic 851	org/telegram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   4191: return
    //   4192: aload 35
    //   4194: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   4197: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   4200: bipush 46
    //   4202: if_icmplt +233 -> 4435
    //   4205: new 853	org/telegram/tgnet/TLRPC$TL_decryptedMessage
    //   4208: dup
    //   4209: invokespecial 854	org/telegram/tgnet/TLRPC$TL_decryptedMessage:<init>	()V
    //   4212: astore_3
    //   4213: aload_3
    //   4214: aload 26
    //   4216: getfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   4219: putfield 855	org/telegram/tgnet/TLRPC$TL_decryptedMessage:ttl	I
    //   4222: aload 15
    //   4224: ifnull +29 -> 4253
    //   4227: aload 15
    //   4229: invokevirtual 438	java/util/ArrayList:isEmpty	()Z
    //   4232: ifne +21 -> 4253
    //   4235: aload_3
    //   4236: aload 15
    //   4238: putfield 856	org/telegram/tgnet/TLRPC$TL_decryptedMessage:entities	Ljava/util/ArrayList;
    //   4241: aload_3
    //   4242: aload_3
    //   4243: getfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4246: sipush 128
    //   4249: ior
    //   4250: putfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4253: aload_3
    //   4254: astore_1
    //   4255: aload 11
    //   4257: ifnull +43 -> 4300
    //   4260: aload_3
    //   4261: astore_1
    //   4262: aload 11
    //   4264: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4267: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   4270: lconst_0
    //   4271: lcmp
    //   4272: ifeq +28 -> 4300
    //   4275: aload_3
    //   4276: aload 11
    //   4278: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4281: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   4284: putfield 858	org/telegram/tgnet/TLRPC$TL_decryptedMessage:reply_to_random_id	J
    //   4287: aload_3
    //   4288: aload_3
    //   4289: getfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4292: bipush 8
    //   4294: ior
    //   4295: putfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4298: aload_3
    //   4299: astore_1
    //   4300: aload 17
    //   4302: ifnull +41 -> 4343
    //   4305: aload 17
    //   4307: ldc_w 316
    //   4310: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4313: ifnull +30 -> 4343
    //   4316: aload_1
    //   4317: aload 17
    //   4319: ldc_w 316
    //   4322: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4325: checkcast 201	java/lang/String
    //   4328: putfield 859	org/telegram/tgnet/TLRPC$TL_decryptedMessage:via_bot_name	Ljava/lang/String;
    //   4331: aload_1
    //   4332: aload_1
    //   4333: getfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4336: sipush 2048
    //   4339: ior
    //   4340: putfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4343: aload_1
    //   4344: aload 26
    //   4346: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   4349: putfield 860	org/telegram/tgnet/TLRPC$TL_decryptedMessage:random_id	J
    //   4352: aload_1
    //   4353: aload_2
    //   4354: putfield 861	org/telegram/tgnet/TLRPC$TL_decryptedMessage:message	Ljava/lang/String;
    //   4357: aload 37
    //   4359: ifnull +138 -> 4497
    //   4362: aload 37
    //   4364: getfield 449	org/telegram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   4367: ifnull +130 -> 4497
    //   4370: aload_1
    //   4371: new 863	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaWebPage
    //   4374: dup
    //   4375: invokespecial 864	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaWebPage:<init>	()V
    //   4378: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   4381: aload_1
    //   4382: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   4385: aload 37
    //   4387: getfield 449	org/telegram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   4390: putfield 870	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:url	Ljava/lang/String;
    //   4393: aload_1
    //   4394: aload_1
    //   4395: getfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4398: sipush 512
    //   4401: ior
    //   4402: putfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4405: invokestatic 875	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   4408: aload_1
    //   4409: aload 7
    //   4411: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4414: aload 35
    //   4416: aconst_null
    //   4417: aconst_null
    //   4418: aload 7
    //   4420: invokevirtual 879	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   4423: aload 14
    //   4425: ifnonnull -4418 -> 7
    //   4428: lload 8
    //   4430: iconst_0
    //   4431: invokestatic 851	org/telegram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   4434: return
    //   4435: aload 35
    //   4437: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   4440: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   4443: bipush 17
    //   4445: if_icmplt +23 -> 4468
    //   4448: new 881	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer17
    //   4451: dup
    //   4452: invokespecial 882	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer17:<init>	()V
    //   4455: astore_1
    //   4456: aload_1
    //   4457: aload 26
    //   4459: getfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   4462: putfield 855	org/telegram/tgnet/TLRPC$TL_decryptedMessage:ttl	I
    //   4465: goto -165 -> 4300
    //   4468: new 884	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer8
    //   4471: dup
    //   4472: invokespecial 885	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer8:<init>	()V
    //   4475: astore_1
    //   4476: aload_1
    //   4477: bipush 15
    //   4479: newarray <illegal type>
    //   4481: putfield 889	org/telegram/tgnet/TLRPC$TL_decryptedMessage:random_bytes	[B
    //   4484: getstatic 735	org/telegram/messenger/Utilities:random	Ljava/security/SecureRandom;
    //   4487: aload_1
    //   4488: getfield 889	org/telegram/tgnet/TLRPC$TL_decryptedMessage:random_bytes	[B
    //   4491: invokevirtual 893	java/security/SecureRandom:nextBytes	([B)V
    //   4494: goto -194 -> 4300
    //   4497: aload_1
    //   4498: new 895	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaEmpty
    //   4501: dup
    //   4502: invokespecial 896	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaEmpty:<init>	()V
    //   4505: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   4508: goto -103 -> 4405
    //   4511: aload 35
    //   4513: ifnonnull +1484 -> 5997
    //   4516: aconst_null
    //   4517: astore_2
    //   4518: aconst_null
    //   4519: astore 15
    //   4521: aconst_null
    //   4522: astore 12
    //   4524: iload 19
    //   4526: iconst_1
    //   4527: if_icmpne +4500 -> 9027
    //   4530: aload_3
    //   4531: instanceof 898
    //   4534: ifeq +144 -> 4678
    //   4537: new 900	org/telegram/tgnet/TLRPC$TL_inputMediaVenue
    //   4540: dup
    //   4541: invokespecial 901	org/telegram/tgnet/TLRPC$TL_inputMediaVenue:<init>	()V
    //   4544: astore_2
    //   4545: aload_2
    //   4546: aload_3
    //   4547: getfield 904	org/telegram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   4550: putfield 907	org/telegram/tgnet/TLRPC$InputMedia:address	Ljava/lang/String;
    //   4553: aload_2
    //   4554: aload_3
    //   4555: getfield 910	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   4558: putfield 911	org/telegram/tgnet/TLRPC$InputMedia:title	Ljava/lang/String;
    //   4561: aload_2
    //   4562: aload_3
    //   4563: getfield 914	org/telegram/tgnet/TLRPC$MessageMedia:provider	Ljava/lang/String;
    //   4566: putfield 915	org/telegram/tgnet/TLRPC$InputMedia:provider	Ljava/lang/String;
    //   4569: aload_2
    //   4570: aload_3
    //   4571: getfield 918	org/telegram/tgnet/TLRPC$MessageMedia:venue_id	Ljava/lang/String;
    //   4574: putfield 919	org/telegram/tgnet/TLRPC$InputMedia:venue_id	Ljava/lang/String;
    //   4577: aload_2
    //   4578: new 921	org/telegram/tgnet/TLRPC$TL_inputGeoPoint
    //   4581: dup
    //   4582: invokespecial 922	org/telegram/tgnet/TLRPC$TL_inputGeoPoint:<init>	()V
    //   4585: putfield 926	org/telegram/tgnet/TLRPC$InputMedia:geo_point	Lorg/telegram/tgnet/TLRPC$InputGeoPoint;
    //   4588: aload_2
    //   4589: getfield 926	org/telegram/tgnet/TLRPC$InputMedia:geo_point	Lorg/telegram/tgnet/TLRPC$InputGeoPoint;
    //   4592: aload_3
    //   4593: getfield 930	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   4596: getfield 936	org/telegram/tgnet/TLRPC$GeoPoint:lat	D
    //   4599: putfield 939	org/telegram/tgnet/TLRPC$InputGeoPoint:lat	D
    //   4602: aload_2
    //   4603: getfield 926	org/telegram/tgnet/TLRPC$InputMedia:geo_point	Lorg/telegram/tgnet/TLRPC$InputGeoPoint;
    //   4606: aload_3
    //   4607: getfield 930	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   4610: getfield 942	org/telegram/tgnet/TLRPC$GeoPoint:_long	D
    //   4613: putfield 943	org/telegram/tgnet/TLRPC$InputGeoPoint:_long	D
    //   4616: aload 12
    //   4618: astore_3
    //   4619: aload 6
    //   4621: ifnull +1073 -> 5694
    //   4624: new 730	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast
    //   4627: dup
    //   4628: invokespecial 731	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:<init>	()V
    //   4631: astore_1
    //   4632: new 435	java/util/ArrayList
    //   4635: dup
    //   4636: invokespecial 618	java/util/ArrayList:<init>	()V
    //   4639: astore 5
    //   4641: iconst_0
    //   4642: istore 18
    //   4644: iload 18
    //   4646: aload 6
    //   4648: invokevirtual 512	java/util/ArrayList:size	()I
    //   4651: if_icmpge +980 -> 5631
    //   4654: aload 5
    //   4656: getstatic 735	org/telegram/messenger/Utilities:random	Ljava/security/SecureRandom;
    //   4659: invokevirtual 740	java/security/SecureRandom:nextLong	()J
    //   4662: invokestatic 745	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   4665: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   4668: pop
    //   4669: iload 18
    //   4671: iconst_1
    //   4672: iadd
    //   4673: istore 18
    //   4675: goto -31 -> 4644
    //   4678: new 945	org/telegram/tgnet/TLRPC$TL_inputMediaGeoPoint
    //   4681: dup
    //   4682: invokespecial 946	org/telegram/tgnet/TLRPC$TL_inputMediaGeoPoint:<init>	()V
    //   4685: astore_2
    //   4686: goto -109 -> 4577
    //   4689: aload 4
    //   4691: getfield 947	org/telegram/tgnet/TLRPC$TL_photo:access_hash	J
    //   4694: lconst_0
    //   4695: lcmp
    //   4696: ifne +128 -> 4824
    //   4699: new 949	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedPhoto
    //   4702: dup
    //   4703: invokespecial 950	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedPhoto:<init>	()V
    //   4706: astore 5
    //   4708: aload 4
    //   4710: getfield 951	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   4713: ifnull +4335 -> 9048
    //   4716: aload 4
    //   4718: getfield 951	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   4721: astore_2
    //   4722: aload 5
    //   4724: aload_2
    //   4725: putfield 952	org/telegram/tgnet/TLRPC$InputMedia:caption	Ljava/lang/String;
    //   4728: new 100	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   4731: dup
    //   4732: aload_0
    //   4733: invokespecial 953	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   4736: astore_3
    //   4737: aload_3
    //   4738: aload 32
    //   4740: putfield 955	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   4743: aload_3
    //   4744: iconst_0
    //   4745: putfield 956	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   4748: aload_3
    //   4749: aload 7
    //   4751: putfield 959	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   4754: aload 10
    //   4756: ifnull +34 -> 4790
    //   4759: aload 10
    //   4761: invokevirtual 205	java/lang/String:length	()I
    //   4764: ifle +26 -> 4790
    //   4767: aload 10
    //   4769: ldc_w 502
    //   4772: invokevirtual 506	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   4775: ifeq +15 -> 4790
    //   4778: aload_3
    //   4779: aload 10
    //   4781: putfield 962	org/telegram/messenger/SendMessagesHelper$DelayedMessage:httpLocation	Ljava/lang/String;
    //   4784: aload 5
    //   4786: astore_2
    //   4787: goto -168 -> 4619
    //   4790: aload_3
    //   4791: aload 4
    //   4793: getfield 509	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   4796: aload 4
    //   4798: getfield 509	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   4801: invokevirtual 512	java/util/ArrayList:size	()I
    //   4804: iconst_1
    //   4805: isub
    //   4806: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   4809: checkcast 517	org/telegram/tgnet/TLRPC$PhotoSize
    //   4812: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   4815: putfield 963	org/telegram/messenger/SendMessagesHelper$DelayedMessage:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   4818: aload 5
    //   4820: astore_2
    //   4821: goto -202 -> 4619
    //   4824: new 965	org/telegram/tgnet/TLRPC$TL_inputMediaPhoto
    //   4827: dup
    //   4828: invokespecial 966	org/telegram/tgnet/TLRPC$TL_inputMediaPhoto:<init>	()V
    //   4831: astore_3
    //   4832: aload_3
    //   4833: new 968	org/telegram/tgnet/TLRPC$TL_inputPhoto
    //   4836: dup
    //   4837: invokespecial 969	org/telegram/tgnet/TLRPC$TL_inputPhoto:<init>	()V
    //   4840: putfield 972	org/telegram/tgnet/TLRPC$TL_inputMediaPhoto:id	Lorg/telegram/tgnet/TLRPC$InputPhoto;
    //   4843: aload 4
    //   4845: getfield 951	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   4848: ifnull +4207 -> 9055
    //   4851: aload 4
    //   4853: getfield 951	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   4856: astore_2
    //   4857: aload_3
    //   4858: aload_2
    //   4859: putfield 973	org/telegram/tgnet/TLRPC$TL_inputMediaPhoto:caption	Ljava/lang/String;
    //   4862: aload_3
    //   4863: getfield 972	org/telegram/tgnet/TLRPC$TL_inputMediaPhoto:id	Lorg/telegram/tgnet/TLRPC$InputPhoto;
    //   4866: aload 4
    //   4868: getfield 974	org/telegram/tgnet/TLRPC$TL_photo:id	J
    //   4871: putfield 977	org/telegram/tgnet/TLRPC$InputPhoto:id	J
    //   4874: aload_3
    //   4875: getfield 972	org/telegram/tgnet/TLRPC$TL_inputMediaPhoto:id	Lorg/telegram/tgnet/TLRPC$InputPhoto;
    //   4878: aload 4
    //   4880: getfield 947	org/telegram/tgnet/TLRPC$TL_photo:access_hash	J
    //   4883: putfield 978	org/telegram/tgnet/TLRPC$InputPhoto:access_hash	J
    //   4886: aload_3
    //   4887: astore_2
    //   4888: aload 12
    //   4890: astore_3
    //   4891: goto -272 -> 4619
    //   4894: iload 19
    //   4896: iconst_3
    //   4897: if_icmpne +203 -> 5100
    //   4900: aload 34
    //   4902: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   4905: lconst_0
    //   4906: lcmp
    //   4907: ifne +123 -> 5030
    //   4910: aload 34
    //   4912: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   4915: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   4918: ifnull +101 -> 5019
    //   4921: new 985	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedThumbDocument
    //   4924: dup
    //   4925: invokespecial 986	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedThumbDocument:<init>	()V
    //   4928: astore_2
    //   4929: aload 34
    //   4931: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   4934: ifnull +4128 -> 9062
    //   4937: aload 34
    //   4939: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   4942: astore_3
    //   4943: aload_2
    //   4944: aload_3
    //   4945: putfield 952	org/telegram/tgnet/TLRPC$InputMedia:caption	Ljava/lang/String;
    //   4948: aload_2
    //   4949: aload 34
    //   4951: getfield 990	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   4954: putfield 991	org/telegram/tgnet/TLRPC$InputMedia:mime_type	Ljava/lang/String;
    //   4957: aload_2
    //   4958: aload 34
    //   4960: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   4963: putfield 992	org/telegram/tgnet/TLRPC$InputMedia:attributes	Ljava/util/ArrayList;
    //   4966: new 100	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   4969: dup
    //   4970: aload_0
    //   4971: invokespecial 953	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   4974: astore_3
    //   4975: aload_3
    //   4976: aload 32
    //   4978: putfield 955	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   4981: aload_3
    //   4982: iconst_1
    //   4983: putfield 956	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   4986: aload_3
    //   4987: aload 7
    //   4989: putfield 959	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   4992: aload_3
    //   4993: aload 34
    //   4995: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   4998: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5001: putfield 963	org/telegram/messenger/SendMessagesHelper$DelayedMessage:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5004: aload_3
    //   5005: aload 34
    //   5007: putfield 996	org/telegram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/telegram/tgnet/TLRPC$TL_document;
    //   5010: aload_3
    //   5011: aload 5
    //   5013: putfield 1000	org/telegram/messenger/SendMessagesHelper$DelayedMessage:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   5016: goto -397 -> 4619
    //   5019: new 1002	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument
    //   5022: dup
    //   5023: invokespecial 1003	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument:<init>	()V
    //   5026: astore_2
    //   5027: goto -98 -> 4929
    //   5030: new 1005	org/telegram/tgnet/TLRPC$TL_inputMediaDocument
    //   5033: dup
    //   5034: invokespecial 1006	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:<init>	()V
    //   5037: astore_3
    //   5038: aload_3
    //   5039: new 1008	org/telegram/tgnet/TLRPC$TL_inputDocument
    //   5042: dup
    //   5043: invokespecial 1009	org/telegram/tgnet/TLRPC$TL_inputDocument:<init>	()V
    //   5046: putfield 1012	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5049: aload 34
    //   5051: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5054: ifnull +4015 -> 9069
    //   5057: aload 34
    //   5059: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5062: astore_2
    //   5063: aload_3
    //   5064: aload_2
    //   5065: putfield 1013	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:caption	Ljava/lang/String;
    //   5068: aload_3
    //   5069: getfield 1012	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5072: aload 34
    //   5074: getfield 1014	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   5077: putfield 1017	org/telegram/tgnet/TLRPC$InputDocument:id	J
    //   5080: aload_3
    //   5081: getfield 1012	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5084: aload 34
    //   5086: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5089: putfield 1018	org/telegram/tgnet/TLRPC$InputDocument:access_hash	J
    //   5092: aload_3
    //   5093: astore_2
    //   5094: aload 12
    //   5096: astore_3
    //   5097: goto -478 -> 4619
    //   5100: iload 19
    //   5102: bipush 6
    //   5104: if_icmpne +3972 -> 9076
    //   5107: new 1020	org/telegram/tgnet/TLRPC$TL_inputMediaContact
    //   5110: dup
    //   5111: invokespecial 1021	org/telegram/tgnet/TLRPC$TL_inputMediaContact:<init>	()V
    //   5114: astore_2
    //   5115: aload_2
    //   5116: aload 36
    //   5118: getfield 407	org/telegram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   5121: putfield 1022	org/telegram/tgnet/TLRPC$InputMedia:phone_number	Ljava/lang/String;
    //   5124: aload_2
    //   5125: aload 36
    //   5127: getfield 411	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   5130: putfield 1023	org/telegram/tgnet/TLRPC$InputMedia:first_name	Ljava/lang/String;
    //   5133: aload_2
    //   5134: aload 36
    //   5136: getfield 415	org/telegram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   5139: putfield 1024	org/telegram/tgnet/TLRPC$InputMedia:last_name	Ljava/lang/String;
    //   5142: aload 12
    //   5144: astore_3
    //   5145: goto -526 -> 4619
    //   5148: aload 34
    //   5150: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5153: lconst_0
    //   5154: lcmp
    //   5155: ifne +235 -> 5390
    //   5158: aload 35
    //   5160: ifnonnull +139 -> 5299
    //   5163: aload 32
    //   5165: ifnull +134 -> 5299
    //   5168: aload 32
    //   5170: invokevirtual 205	java/lang/String:length	()I
    //   5173: ifle +126 -> 5299
    //   5176: aload 32
    //   5178: ldc_w 502
    //   5181: invokevirtual 506	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   5184: ifeq +115 -> 5299
    //   5187: aload 17
    //   5189: ifnull +110 -> 5299
    //   5192: new 1026	org/telegram/tgnet/TLRPC$TL_inputMediaGifExternal
    //   5195: dup
    //   5196: invokespecial 1027	org/telegram/tgnet/TLRPC$TL_inputMediaGifExternal:<init>	()V
    //   5199: astore 5
    //   5201: aload 17
    //   5203: ldc_w 1028
    //   5206: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   5209: checkcast 201	java/lang/String
    //   5212: ldc_w 1030
    //   5215: invokevirtual 1034	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   5218: astore 10
    //   5220: aload 15
    //   5222: astore_3
    //   5223: aload 5
    //   5225: astore_2
    //   5226: aload 10
    //   5228: arraylength
    //   5229: iconst_2
    //   5230: if_icmpne +27 -> 5257
    //   5233: aload 5
    //   5235: aload 10
    //   5237: iconst_0
    //   5238: aaload
    //   5239: putfield 1035	org/telegram/tgnet/TLRPC$InputMedia:url	Ljava/lang/String;
    //   5242: aload 5
    //   5244: aload 10
    //   5246: iconst_1
    //   5247: aaload
    //   5248: putfield 1038	org/telegram/tgnet/TLRPC$InputMedia:q	Ljava/lang/String;
    //   5251: aload 5
    //   5253: astore_2
    //   5254: aload 15
    //   5256: astore_3
    //   5257: aload_2
    //   5258: aload 34
    //   5260: getfield 990	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   5263: putfield 991	org/telegram/tgnet/TLRPC$InputMedia:mime_type	Ljava/lang/String;
    //   5266: aload_2
    //   5267: aload 34
    //   5269: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   5272: putfield 992	org/telegram/tgnet/TLRPC$InputMedia:attributes	Ljava/util/ArrayList;
    //   5275: aload 34
    //   5277: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5280: ifnull +3813 -> 9093
    //   5283: aload 34
    //   5285: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5288: astore 5
    //   5290: aload_2
    //   5291: aload 5
    //   5293: putfield 952	org/telegram/tgnet/TLRPC$InputMedia:caption	Ljava/lang/String;
    //   5296: goto -677 -> 4619
    //   5299: aload 34
    //   5301: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5304: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5307: ifnull +72 -> 5379
    //   5310: aload 34
    //   5312: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5315: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5318: instanceof 1040
    //   5321: ifeq +58 -> 5379
    //   5324: new 985	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedThumbDocument
    //   5327: dup
    //   5328: invokespecial 986	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedThumbDocument:<init>	()V
    //   5331: astore_2
    //   5332: new 100	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   5335: dup
    //   5336: aload_0
    //   5337: invokespecial 953	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   5340: astore_3
    //   5341: aload_3
    //   5342: aload 32
    //   5344: putfield 955	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   5347: aload_3
    //   5348: iconst_2
    //   5349: putfield 956	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   5352: aload_3
    //   5353: aload 7
    //   5355: putfield 959	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   5358: aload_3
    //   5359: aload 34
    //   5361: putfield 996	org/telegram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/telegram/tgnet/TLRPC$TL_document;
    //   5364: aload_3
    //   5365: aload 34
    //   5367: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5370: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5373: putfield 963	org/telegram/messenger/SendMessagesHelper$DelayedMessage:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5376: goto -119 -> 5257
    //   5379: new 1002	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument
    //   5382: dup
    //   5383: invokespecial 1003	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument:<init>	()V
    //   5386: astore_2
    //   5387: goto -55 -> 5332
    //   5390: new 1005	org/telegram/tgnet/TLRPC$TL_inputMediaDocument
    //   5393: dup
    //   5394: invokespecial 1006	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:<init>	()V
    //   5397: astore_3
    //   5398: aload_3
    //   5399: new 1008	org/telegram/tgnet/TLRPC$TL_inputDocument
    //   5402: dup
    //   5403: invokespecial 1009	org/telegram/tgnet/TLRPC$TL_inputDocument:<init>	()V
    //   5406: putfield 1012	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5409: aload_3
    //   5410: getfield 1012	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5413: aload 34
    //   5415: getfield 1014	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   5418: putfield 1017	org/telegram/tgnet/TLRPC$InputDocument:id	J
    //   5421: aload_3
    //   5422: getfield 1012	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5425: aload 34
    //   5427: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5430: putfield 1018	org/telegram/tgnet/TLRPC$InputDocument:access_hash	J
    //   5433: aload 34
    //   5435: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5438: ifnull +3663 -> 9101
    //   5441: aload 34
    //   5443: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5446: astore_2
    //   5447: aload_3
    //   5448: aload_2
    //   5449: putfield 1013	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:caption	Ljava/lang/String;
    //   5452: aload_3
    //   5453: astore_2
    //   5454: aload 12
    //   5456: astore_3
    //   5457: goto -838 -> 4619
    //   5460: aload 12
    //   5462: astore_3
    //   5463: iload 19
    //   5465: bipush 8
    //   5467: if_icmpne -848 -> 4619
    //   5470: aload 34
    //   5472: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5475: lconst_0
    //   5476: lcmp
    //   5477: ifne +84 -> 5561
    //   5480: new 1002	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument
    //   5483: dup
    //   5484: invokespecial 1003	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument:<init>	()V
    //   5487: astore 5
    //   5489: aload 5
    //   5491: aload 34
    //   5493: getfield 990	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   5496: putfield 991	org/telegram/tgnet/TLRPC$InputMedia:mime_type	Ljava/lang/String;
    //   5499: aload 5
    //   5501: aload 34
    //   5503: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   5506: putfield 992	org/telegram/tgnet/TLRPC$InputMedia:attributes	Ljava/util/ArrayList;
    //   5509: aload 34
    //   5511: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5514: ifnull +3594 -> 9108
    //   5517: aload 34
    //   5519: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5522: astore_2
    //   5523: aload 5
    //   5525: aload_2
    //   5526: putfield 952	org/telegram/tgnet/TLRPC$InputMedia:caption	Ljava/lang/String;
    //   5529: new 100	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   5532: dup
    //   5533: aload_0
    //   5534: invokespecial 953	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   5537: astore_3
    //   5538: aload_3
    //   5539: iconst_3
    //   5540: putfield 956	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   5543: aload_3
    //   5544: aload 7
    //   5546: putfield 959	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   5549: aload_3
    //   5550: aload 34
    //   5552: putfield 996	org/telegram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/telegram/tgnet/TLRPC$TL_document;
    //   5555: aload 5
    //   5557: astore_2
    //   5558: goto -939 -> 4619
    //   5561: new 1005	org/telegram/tgnet/TLRPC$TL_inputMediaDocument
    //   5564: dup
    //   5565: invokespecial 1006	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:<init>	()V
    //   5568: astore_3
    //   5569: aload_3
    //   5570: new 1008	org/telegram/tgnet/TLRPC$TL_inputDocument
    //   5573: dup
    //   5574: invokespecial 1009	org/telegram/tgnet/TLRPC$TL_inputDocument:<init>	()V
    //   5577: putfield 1012	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5580: aload 34
    //   5582: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5585: ifnull +3530 -> 9115
    //   5588: aload 34
    //   5590: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5593: astore_2
    //   5594: aload_3
    //   5595: aload_2
    //   5596: putfield 1013	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:caption	Ljava/lang/String;
    //   5599: aload_3
    //   5600: getfield 1012	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5603: aload 34
    //   5605: getfield 1014	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   5608: putfield 1017	org/telegram/tgnet/TLRPC$InputDocument:id	J
    //   5611: aload_3
    //   5612: getfield 1012	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5615: aload 34
    //   5617: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5620: putfield 1018	org/telegram/tgnet/TLRPC$InputDocument:access_hash	J
    //   5623: aload_3
    //   5624: astore_2
    //   5625: aload 12
    //   5627: astore_3
    //   5628: goto -1009 -> 4619
    //   5631: aload_1
    //   5632: aload 6
    //   5634: putfield 788	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:contacts	Ljava/util/ArrayList;
    //   5637: aload_1
    //   5638: aload_2
    //   5639: putfield 794	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:media	Lorg/telegram/tgnet/TLRPC$InputMedia;
    //   5642: aload_1
    //   5643: aload 5
    //   5645: putfield 796	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:random_id	Ljava/util/ArrayList;
    //   5648: aload_1
    //   5649: ldc_w 322
    //   5652: putfield 785	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:message	Ljava/lang/String;
    //   5655: aload_3
    //   5656: ifnull +8 -> 5664
    //   5659: aload_3
    //   5660: aload_1
    //   5661: putfield 1044	org/telegram/messenger/SendMessagesHelper$DelayedMessage:sendRequest	Lorg/telegram/tgnet/TLObject;
    //   5664: aload_1
    //   5665: astore_2
    //   5666: aload 14
    //   5668: ifnonnull +11 -> 5679
    //   5671: lload 8
    //   5673: iconst_0
    //   5674: invokestatic 851	org/telegram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   5677: aload_1
    //   5678: astore_2
    //   5679: iload 19
    //   5681: iconst_1
    //   5682: if_icmpne +168 -> 5850
    //   5685: aload_0
    //   5686: aload_2
    //   5687: aload 7
    //   5689: aconst_null
    //   5690: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   5693: return
    //   5694: new 1046	org/telegram/tgnet/TLRPC$TL_messages_sendMedia
    //   5697: dup
    //   5698: invokespecial 1047	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:<init>	()V
    //   5701: astore 5
    //   5703: aload 5
    //   5705: aload 33
    //   5707: putfield 1048	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   5710: aload 26
    //   5712: getfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   5715: instanceof 809
    //   5718: ifeq +45 -> 5763
    //   5721: aload 5
    //   5723: getstatic 815	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   5726: ldc_w 817
    //   5729: iconst_0
    //   5730: invokevirtual 823	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   5733: new 700	java/lang/StringBuilder
    //   5736: dup
    //   5737: invokespecial 701	java/lang/StringBuilder:<init>	()V
    //   5740: ldc_w 825
    //   5743: invokevirtual 707	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5746: lload 8
    //   5748: invokevirtual 724	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   5751: invokevirtual 725	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5754: iconst_0
    //   5755: invokeinterface 831 3 0
    //   5760: putfield 1049	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:silent	Z
    //   5763: aload 5
    //   5765: aload 26
    //   5767: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   5770: putfield 1050	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:random_id	J
    //   5773: aload 5
    //   5775: aload_2
    //   5776: putfield 1051	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:media	Lorg/telegram/tgnet/TLRPC$InputMedia;
    //   5779: aload_1
    //   5780: ifnull +30 -> 5810
    //   5783: aload 5
    //   5785: getfield 1051	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:media	Lorg/telegram/tgnet/TLRPC$InputMedia;
    //   5788: astore_2
    //   5789: aload_1
    //   5790: invokeinterface 495 1 0
    //   5795: ifnull +3333 -> 9128
    //   5798: aload_1
    //   5799: invokeinterface 495 1 0
    //   5804: astore_1
    //   5805: aload_2
    //   5806: aload_1
    //   5807: putfield 952	org/telegram/tgnet/TLRPC$InputMedia:caption	Ljava/lang/String;
    //   5810: aload 11
    //   5812: ifnull +25 -> 5837
    //   5815: aload 5
    //   5817: aload 5
    //   5819: getfield 1052	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:flags	I
    //   5822: iconst_1
    //   5823: ior
    //   5824: putfield 1052	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:flags	I
    //   5827: aload 5
    //   5829: aload 11
    //   5831: invokevirtual 275	org/telegram/messenger/MessageObject:getId	()I
    //   5834: putfield 1053	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:reply_to_msg_id	I
    //   5837: aload_3
    //   5838: ifnull +3284 -> 9122
    //   5841: aload_3
    //   5842: aload 5
    //   5844: putfield 1044	org/telegram/messenger/SendMessagesHelper$DelayedMessage:sendRequest	Lorg/telegram/tgnet/TLObject;
    //   5847: goto +3275 -> 9122
    //   5850: iload 19
    //   5852: iconst_2
    //   5853: if_icmpne +28 -> 5881
    //   5856: aload 4
    //   5858: getfield 947	org/telegram/tgnet/TLRPC$TL_photo:access_hash	J
    //   5861: lconst_0
    //   5862: lcmp
    //   5863: ifne +9 -> 5872
    //   5866: aload_0
    //   5867: aload_3
    //   5868: invokespecial 1057	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   5871: return
    //   5872: aload_0
    //   5873: aload_2
    //   5874: aload 7
    //   5876: aconst_null
    //   5877: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   5880: return
    //   5881: iload 19
    //   5883: iconst_3
    //   5884: if_icmpne +28 -> 5912
    //   5887: aload 34
    //   5889: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5892: lconst_0
    //   5893: lcmp
    //   5894: ifne +9 -> 5903
    //   5897: aload_0
    //   5898: aload_3
    //   5899: invokespecial 1057	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   5902: return
    //   5903: aload_0
    //   5904: aload_2
    //   5905: aload 7
    //   5907: aconst_null
    //   5908: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   5911: return
    //   5912: iload 19
    //   5914: bipush 6
    //   5916: if_icmpne +12 -> 5928
    //   5919: aload_0
    //   5920: aload_2
    //   5921: aload 7
    //   5923: aconst_null
    //   5924: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   5927: return
    //   5928: iload 19
    //   5930: bipush 7
    //   5932: if_icmpne +33 -> 5965
    //   5935: aload 34
    //   5937: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5940: lconst_0
    //   5941: lcmp
    //   5942: ifne +13 -> 5955
    //   5945: aload_3
    //   5946: ifnull +9 -> 5955
    //   5949: aload_0
    //   5950: aload_3
    //   5951: invokespecial 1057	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   5954: return
    //   5955: aload_0
    //   5956: aload_2
    //   5957: aload 7
    //   5959: aload 32
    //   5961: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   5964: return
    //   5965: iload 19
    //   5967: bipush 8
    //   5969: if_icmpne -5962 -> 7
    //   5972: aload 34
    //   5974: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5977: lconst_0
    //   5978: lcmp
    //   5979: ifne +9 -> 5988
    //   5982: aload_0
    //   5983: aload_3
    //   5984: invokespecial 1057	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   5987: return
    //   5988: aload_0
    //   5989: aload_2
    //   5990: aload 7
    //   5992: aconst_null
    //   5993: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   5996: return
    //   5997: aload 35
    //   5999: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   6002: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   6005: bipush 46
    //   6007: if_icmplt +302 -> 6309
    //   6010: new 853	org/telegram/tgnet/TLRPC$TL_decryptedMessage
    //   6013: dup
    //   6014: invokespecial 854	org/telegram/tgnet/TLRPC$TL_decryptedMessage:<init>	()V
    //   6017: astore_1
    //   6018: aload_1
    //   6019: aload 26
    //   6021: getfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   6024: putfield 855	org/telegram/tgnet/TLRPC$TL_decryptedMessage:ttl	I
    //   6027: aload 15
    //   6029: ifnull +29 -> 6058
    //   6032: aload 15
    //   6034: invokevirtual 438	java/util/ArrayList:isEmpty	()Z
    //   6037: ifne +21 -> 6058
    //   6040: aload_1
    //   6041: aload 15
    //   6043: putfield 856	org/telegram/tgnet/TLRPC$TL_decryptedMessage:entities	Ljava/util/ArrayList;
    //   6046: aload_1
    //   6047: aload_1
    //   6048: getfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6051: sipush 128
    //   6054: ior
    //   6055: putfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6058: aload 11
    //   6060: ifnull +39 -> 6099
    //   6063: aload 11
    //   6065: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   6068: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   6071: lconst_0
    //   6072: lcmp
    //   6073: ifeq +26 -> 6099
    //   6076: aload_1
    //   6077: aload 11
    //   6079: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   6082: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   6085: putfield 858	org/telegram/tgnet/TLRPC$TL_decryptedMessage:reply_to_random_id	J
    //   6088: aload_1
    //   6089: aload_1
    //   6090: getfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6093: bipush 8
    //   6095: ior
    //   6096: putfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6099: aload_1
    //   6100: aload_1
    //   6101: getfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6104: sipush 512
    //   6107: ior
    //   6108: putfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6111: aload 17
    //   6113: ifnull +41 -> 6154
    //   6116: aload 17
    //   6118: ldc_w 316
    //   6121: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   6124: ifnull +30 -> 6154
    //   6127: aload_1
    //   6128: aload 17
    //   6130: ldc_w 316
    //   6133: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   6136: checkcast 201	java/lang/String
    //   6139: putfield 859	org/telegram/tgnet/TLRPC$TL_decryptedMessage:via_bot_name	Ljava/lang/String;
    //   6142: aload_1
    //   6143: aload_1
    //   6144: getfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6147: sipush 2048
    //   6150: ior
    //   6151: putfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6154: aload_1
    //   6155: aload 26
    //   6157: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   6160: putfield 860	org/telegram/tgnet/TLRPC$TL_decryptedMessage:random_id	J
    //   6163: aload_1
    //   6164: ldc_w 322
    //   6167: putfield 861	org/telegram/tgnet/TLRPC$TL_decryptedMessage:message	Ljava/lang/String;
    //   6170: iload 19
    //   6172: iconst_1
    //   6173: if_icmpne +2962 -> 9135
    //   6176: aload_3
    //   6177: instanceof 898
    //   6180: ifeq +191 -> 6371
    //   6183: aload 35
    //   6185: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   6188: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   6191: bipush 46
    //   6193: if_icmplt +178 -> 6371
    //   6196: aload_1
    //   6197: new 1059	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVenue
    //   6200: dup
    //   6201: invokespecial 1060	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVenue:<init>	()V
    //   6204: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6207: aload_1
    //   6208: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6211: aload_3
    //   6212: getfield 904	org/telegram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   6215: putfield 1061	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:address	Ljava/lang/String;
    //   6218: aload_1
    //   6219: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6222: aload_3
    //   6223: getfield 910	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   6226: putfield 1062	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:title	Ljava/lang/String;
    //   6229: aload_1
    //   6230: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6233: aload_3
    //   6234: getfield 914	org/telegram/tgnet/TLRPC$MessageMedia:provider	Ljava/lang/String;
    //   6237: putfield 1063	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:provider	Ljava/lang/String;
    //   6240: aload_1
    //   6241: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6244: aload_3
    //   6245: getfield 918	org/telegram/tgnet/TLRPC$MessageMedia:venue_id	Ljava/lang/String;
    //   6248: putfield 1064	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:venue_id	Ljava/lang/String;
    //   6251: aload_1
    //   6252: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6255: aload_3
    //   6256: getfield 930	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   6259: getfield 936	org/telegram/tgnet/TLRPC$GeoPoint:lat	D
    //   6262: putfield 1065	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:lat	D
    //   6265: aload_1
    //   6266: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6269: aload_3
    //   6270: getfield 930	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   6273: getfield 942	org/telegram/tgnet/TLRPC$GeoPoint:_long	D
    //   6276: putfield 1066	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:_long	D
    //   6279: invokestatic 875	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   6282: aload_1
    //   6283: aload 7
    //   6285: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   6288: aload 35
    //   6290: aconst_null
    //   6291: aconst_null
    //   6292: aload 7
    //   6294: invokevirtual 879	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   6297: aload 14
    //   6299: ifnonnull -6292 -> 7
    //   6302: lload 8
    //   6304: iconst_0
    //   6305: invokestatic 851	org/telegram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   6308: return
    //   6309: aload 35
    //   6311: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   6314: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   6317: bipush 17
    //   6319: if_icmplt +23 -> 6342
    //   6322: new 881	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer17
    //   6325: dup
    //   6326: invokespecial 882	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer17:<init>	()V
    //   6329: astore_1
    //   6330: aload_1
    //   6331: aload 26
    //   6333: getfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   6336: putfield 855	org/telegram/tgnet/TLRPC$TL_decryptedMessage:ttl	I
    //   6339: goto -228 -> 6111
    //   6342: new 884	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer8
    //   6345: dup
    //   6346: invokespecial 885	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer8:<init>	()V
    //   6349: astore_1
    //   6350: aload_1
    //   6351: bipush 15
    //   6353: newarray <illegal type>
    //   6355: putfield 889	org/telegram/tgnet/TLRPC$TL_decryptedMessage:random_bytes	[B
    //   6358: getstatic 735	org/telegram/messenger/Utilities:random	Ljava/security/SecureRandom;
    //   6361: aload_1
    //   6362: getfield 889	org/telegram/tgnet/TLRPC$TL_decryptedMessage:random_bytes	[B
    //   6365: invokevirtual 893	java/security/SecureRandom:nextBytes	([B)V
    //   6368: goto -257 -> 6111
    //   6371: aload_1
    //   6372: new 1068	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaGeoPoint
    //   6375: dup
    //   6376: invokespecial 1069	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaGeoPoint:<init>	()V
    //   6379: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6382: goto -131 -> 6251
    //   6385: aload 4
    //   6387: getfield 509	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   6390: iconst_0
    //   6391: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   6394: checkcast 517	org/telegram/tgnet/TLRPC$PhotoSize
    //   6397: astore 5
    //   6399: aload 4
    //   6401: getfield 509	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   6404: aload 4
    //   6406: getfield 509	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   6409: invokevirtual 512	java/util/ArrayList:size	()I
    //   6412: iconst_1
    //   6413: isub
    //   6414: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   6417: checkcast 517	org/telegram/tgnet/TLRPC$PhotoSize
    //   6420: astore_3
    //   6421: aload 5
    //   6423: invokestatic 1075	org/telegram/messenger/ImageLoader:fillPhotoSizeWithBytes	(Lorg/telegram/tgnet/TLRPC$PhotoSize;)V
    //   6426: aload 35
    //   6428: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   6431: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   6434: bipush 46
    //   6436: if_icmplt +221 -> 6657
    //   6439: aload_1
    //   6440: new 1077	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto
    //   6443: dup
    //   6444: invokespecial 1078	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto:<init>	()V
    //   6447: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6450: aload_1
    //   6451: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6454: astore 6
    //   6456: aload 4
    //   6458: getfield 951	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   6461: ifnull +2695 -> 9156
    //   6464: aload 4
    //   6466: getfield 951	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   6469: astore_2
    //   6470: aload 6
    //   6472: aload_2
    //   6473: putfield 1079	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:caption	Ljava/lang/String;
    //   6476: aload 5
    //   6478: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6481: ifnull +160 -> 6641
    //   6484: aload_1
    //   6485: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6488: checkcast 1077	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto
    //   6491: aload 5
    //   6493: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6496: putfield 1084	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto:thumb	[B
    //   6499: aload_1
    //   6500: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6503: aload 5
    //   6505: getfield 1087	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   6508: putfield 1090	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   6511: aload_1
    //   6512: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6515: aload 5
    //   6517: getfield 1093	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   6520: putfield 1096	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   6523: aload_1
    //   6524: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6527: aload_3
    //   6528: getfield 1093	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   6531: putfield 1097	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:w	I
    //   6534: aload_1
    //   6535: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6538: aload_3
    //   6539: getfield 1087	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   6542: putfield 1098	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:h	I
    //   6545: aload_1
    //   6546: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6549: aload_3
    //   6550: getfield 1100	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   6553: putfield 1101	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   6556: aload_3
    //   6557: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6560: getfield 1106	org/telegram/tgnet/TLRPC$FileLocation:key	[B
    //   6563: ifnonnull +178 -> 6741
    //   6566: new 100	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   6569: dup
    //   6570: aload_0
    //   6571: invokespecial 953	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   6574: astore_2
    //   6575: aload_2
    //   6576: aload 32
    //   6578: putfield 955	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   6581: aload_2
    //   6582: aload_1
    //   6583: putfield 1110	org/telegram/messenger/SendMessagesHelper$DelayedMessage:sendEncryptedRequest	Lorg/telegram/tgnet/TLRPC$TL_decryptedMessage;
    //   6586: aload_2
    //   6587: iconst_0
    //   6588: putfield 956	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   6591: aload_2
    //   6592: aload 7
    //   6594: putfield 959	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   6597: aload_2
    //   6598: aload 35
    //   6600: putfield 1114	org/telegram/messenger/SendMessagesHelper$DelayedMessage:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   6603: aload 10
    //   6605: ifnull +105 -> 6710
    //   6608: aload 10
    //   6610: invokevirtual 205	java/lang/String:length	()I
    //   6613: ifle +97 -> 6710
    //   6616: aload 10
    //   6618: ldc_w 502
    //   6621: invokevirtual 506	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   6624: ifeq +86 -> 6710
    //   6627: aload_2
    //   6628: aload 10
    //   6630: putfield 962	org/telegram/messenger/SendMessagesHelper$DelayedMessage:httpLocation	Ljava/lang/String;
    //   6633: aload_0
    //   6634: aload_2
    //   6635: invokespecial 1057	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   6638: goto -341 -> 6297
    //   6641: aload_1
    //   6642: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6645: checkcast 1077	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto
    //   6648: iconst_0
    //   6649: newarray <illegal type>
    //   6651: putfield 1084	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto:thumb	[B
    //   6654: goto -155 -> 6499
    //   6657: aload_1
    //   6658: new 1116	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto_layer8
    //   6661: dup
    //   6662: invokespecial 1117	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto_layer8:<init>	()V
    //   6665: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6668: aload 5
    //   6670: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6673: ifnull +21 -> 6694
    //   6676: aload_1
    //   6677: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6680: checkcast 1116	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto_layer8
    //   6683: aload 5
    //   6685: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6688: putfield 1118	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto_layer8:thumb	[B
    //   6691: goto -192 -> 6499
    //   6694: aload_1
    //   6695: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6698: checkcast 1116	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto_layer8
    //   6701: iconst_0
    //   6702: newarray <illegal type>
    //   6704: putfield 1118	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto_layer8:thumb	[B
    //   6707: goto -208 -> 6499
    //   6710: aload_2
    //   6711: aload 4
    //   6713: getfield 509	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   6716: aload 4
    //   6718: getfield 509	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   6721: invokevirtual 512	java/util/ArrayList:size	()I
    //   6724: iconst_1
    //   6725: isub
    //   6726: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   6729: checkcast 517	org/telegram/tgnet/TLRPC$PhotoSize
    //   6732: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6735: putfield 963	org/telegram/messenger/SendMessagesHelper$DelayedMessage:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6738: goto -105 -> 6633
    //   6741: new 1120	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile
    //   6744: dup
    //   6745: invokespecial 1121	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:<init>	()V
    //   6748: astore_2
    //   6749: aload_2
    //   6750: aload_3
    //   6751: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6754: getfield 1124	org/telegram/tgnet/TLRPC$FileLocation:volume_id	J
    //   6757: putfield 1125	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:id	J
    //   6760: aload_2
    //   6761: aload_3
    //   6762: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6765: getfield 1128	org/telegram/tgnet/TLRPC$FileLocation:secret	J
    //   6768: putfield 1129	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:access_hash	J
    //   6771: aload_1
    //   6772: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6775: aload_3
    //   6776: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6779: getfield 1106	org/telegram/tgnet/TLRPC$FileLocation:key	[B
    //   6782: putfield 1130	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:key	[B
    //   6785: aload_1
    //   6786: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6789: aload_3
    //   6790: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6793: getfield 1133	org/telegram/tgnet/TLRPC$FileLocation:iv	[B
    //   6796: putfield 1134	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:iv	[B
    //   6799: invokestatic 875	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   6802: aload_1
    //   6803: aload 7
    //   6805: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   6808: aload 35
    //   6810: aload_2
    //   6811: aconst_null
    //   6812: aload 7
    //   6814: invokevirtual 879	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   6817: goto -520 -> 6297
    //   6820: iload 19
    //   6822: iconst_3
    //   6823: if_icmpne +519 -> 7342
    //   6826: aload 34
    //   6828: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6831: invokestatic 1075	org/telegram/messenger/ImageLoader:fillPhotoSizeWithBytes	(Lorg/telegram/tgnet/TLRPC$PhotoSize;)V
    //   6834: aload 35
    //   6836: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   6839: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   6842: bipush 46
    //   6844: if_icmplt +280 -> 7124
    //   6847: aload_1
    //   6848: new 1136	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo
    //   6851: dup
    //   6852: invokespecial 1137	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo:<init>	()V
    //   6855: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6858: aload 34
    //   6860: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6863: ifnull +245 -> 7108
    //   6866: aload 34
    //   6868: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6871: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6874: ifnull +234 -> 7108
    //   6877: aload_1
    //   6878: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6881: checkcast 1136	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo
    //   6884: aload 34
    //   6886: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6889: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6892: putfield 1138	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo:thumb	[B
    //   6895: aload_1
    //   6896: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6899: astore_3
    //   6900: aload 34
    //   6902: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   6905: ifnull +2258 -> 9163
    //   6908: aload 34
    //   6910: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   6913: astore_2
    //   6914: aload_3
    //   6915: aload_2
    //   6916: putfield 1079	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:caption	Ljava/lang/String;
    //   6919: aload_1
    //   6920: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6923: ldc_w 1140
    //   6926: putfield 1141	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   6929: aload_1
    //   6930: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6933: aload 34
    //   6935: getfield 1142	org/telegram/tgnet/TLRPC$TL_document:size	I
    //   6938: putfield 1101	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   6941: iconst_0
    //   6942: istore 18
    //   6944: iload 18
    //   6946: aload 34
    //   6948: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   6951: invokevirtual 512	java/util/ArrayList:size	()I
    //   6954: if_icmpge +57 -> 7011
    //   6957: aload 34
    //   6959: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   6962: iload 18
    //   6964: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   6967: checkcast 551	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   6970: astore_2
    //   6971: aload_2
    //   6972: instanceof 784
    //   6975: ifeq +2195 -> 9170
    //   6978: aload_1
    //   6979: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6982: aload_2
    //   6983: getfield 1143	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   6986: putfield 1097	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:w	I
    //   6989: aload_1
    //   6990: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6993: aload_2
    //   6994: getfield 1144	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   6997: putfield 1098	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:h	I
    //   7000: aload_1
    //   7001: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7004: aload_2
    //   7005: getfield 773	org/telegram/tgnet/TLRPC$DocumentAttribute:duration	I
    //   7008: putfield 1145	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:duration	I
    //   7011: aload_1
    //   7012: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7015: aload 34
    //   7017: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7020: getfield 1087	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   7023: putfield 1090	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   7026: aload_1
    //   7027: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7030: aload 34
    //   7032: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7035: getfield 1093	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   7038: putfield 1096	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   7041: aload 34
    //   7043: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   7046: lconst_0
    //   7047: lcmp
    //   7048: ifne +223 -> 7271
    //   7051: new 100	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   7054: dup
    //   7055: aload_0
    //   7056: invokespecial 953	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   7059: astore_2
    //   7060: aload_2
    //   7061: aload 32
    //   7063: putfield 955	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   7066: aload_2
    //   7067: aload_1
    //   7068: putfield 1110	org/telegram/messenger/SendMessagesHelper$DelayedMessage:sendEncryptedRequest	Lorg/telegram/tgnet/TLRPC$TL_decryptedMessage;
    //   7071: aload_2
    //   7072: iconst_1
    //   7073: putfield 956	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   7076: aload_2
    //   7077: aload 7
    //   7079: putfield 959	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   7082: aload_2
    //   7083: aload 35
    //   7085: putfield 1114	org/telegram/messenger/SendMessagesHelper$DelayedMessage:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   7088: aload_2
    //   7089: aload 34
    //   7091: putfield 996	org/telegram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/telegram/tgnet/TLRPC$TL_document;
    //   7094: aload_2
    //   7095: aload 5
    //   7097: putfield 1000	org/telegram/messenger/SendMessagesHelper$DelayedMessage:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   7100: aload_0
    //   7101: aload_2
    //   7102: invokespecial 1057	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   7105: goto -808 -> 6297
    //   7108: aload_1
    //   7109: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7112: checkcast 1136	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo
    //   7115: iconst_0
    //   7116: newarray <illegal type>
    //   7118: putfield 1138	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo:thumb	[B
    //   7121: goto -226 -> 6895
    //   7124: aload 35
    //   7126: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   7129: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   7132: bipush 17
    //   7134: if_icmplt +70 -> 7204
    //   7137: aload_1
    //   7138: new 1147	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer17
    //   7141: dup
    //   7142: invokespecial 1148	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer17:<init>	()V
    //   7145: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7148: aload 34
    //   7150: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7153: ifnull +35 -> 7188
    //   7156: aload 34
    //   7158: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7161: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7164: ifnull +24 -> 7188
    //   7167: aload_1
    //   7168: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7171: checkcast 1147	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer17
    //   7174: aload 34
    //   7176: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7179: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7182: putfield 1149	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer17:thumb	[B
    //   7185: goto -290 -> 6895
    //   7188: aload_1
    //   7189: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7192: checkcast 1147	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer17
    //   7195: iconst_0
    //   7196: newarray <illegal type>
    //   7198: putfield 1149	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer17:thumb	[B
    //   7201: goto -306 -> 6895
    //   7204: aload_1
    //   7205: new 1151	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer8
    //   7208: dup
    //   7209: invokespecial 1152	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer8:<init>	()V
    //   7212: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7215: aload 34
    //   7217: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7220: ifnull +35 -> 7255
    //   7223: aload 34
    //   7225: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7228: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7231: ifnull +24 -> 7255
    //   7234: aload_1
    //   7235: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7238: checkcast 1151	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer8
    //   7241: aload 34
    //   7243: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7246: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7249: putfield 1153	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer8:thumb	[B
    //   7252: goto -357 -> 6895
    //   7255: aload_1
    //   7256: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7259: checkcast 1151	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer8
    //   7262: iconst_0
    //   7263: newarray <illegal type>
    //   7265: putfield 1153	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer8:thumb	[B
    //   7268: goto -373 -> 6895
    //   7271: new 1120	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile
    //   7274: dup
    //   7275: invokespecial 1121	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:<init>	()V
    //   7278: astore_2
    //   7279: aload_2
    //   7280: aload 34
    //   7282: getfield 1014	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   7285: putfield 1125	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:id	J
    //   7288: aload_2
    //   7289: aload 34
    //   7291: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   7294: putfield 1129	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:access_hash	J
    //   7297: aload_1
    //   7298: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7301: aload 34
    //   7303: getfield 1154	org/telegram/tgnet/TLRPC$TL_document:key	[B
    //   7306: putfield 1130	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:key	[B
    //   7309: aload_1
    //   7310: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7313: aload 34
    //   7315: getfield 1155	org/telegram/tgnet/TLRPC$TL_document:iv	[B
    //   7318: putfield 1134	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:iv	[B
    //   7321: invokestatic 875	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   7324: aload_1
    //   7325: aload 7
    //   7327: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   7330: aload 35
    //   7332: aload_2
    //   7333: aconst_null
    //   7334: aload 7
    //   7336: invokevirtual 879	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   7339: goto -1042 -> 6297
    //   7342: iload 19
    //   7344: bipush 6
    //   7346: if_icmpne +1833 -> 9179
    //   7349: aload_1
    //   7350: new 1157	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaContact
    //   7353: dup
    //   7354: invokespecial 1158	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaContact:<init>	()V
    //   7357: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7360: aload_1
    //   7361: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7364: aload 36
    //   7366: getfield 407	org/telegram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   7369: putfield 1159	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:phone_number	Ljava/lang/String;
    //   7372: aload_1
    //   7373: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7376: aload 36
    //   7378: getfield 411	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   7381: putfield 1160	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:first_name	Ljava/lang/String;
    //   7384: aload_1
    //   7385: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7388: aload 36
    //   7390: getfield 415	org/telegram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   7393: putfield 1161	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:last_name	Ljava/lang/String;
    //   7396: aload_1
    //   7397: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7400: aload 36
    //   7402: getfield 419	org/telegram/tgnet/TLRPC$User:id	I
    //   7405: putfield 1162	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:user_id	I
    //   7408: invokestatic 875	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   7411: aload_1
    //   7412: aload 7
    //   7414: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   7417: aload 35
    //   7419: aconst_null
    //   7420: aconst_null
    //   7421: aload 7
    //   7423: invokevirtual 879	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   7426: goto -1129 -> 6297
    //   7429: aload 34
    //   7431: invokestatic 543	org/telegram/messenger/MessageObject:isStickerDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   7434: ifeq +178 -> 7612
    //   7437: aload_1
    //   7438: new 1164	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument
    //   7441: dup
    //   7442: invokespecial 1165	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument:<init>	()V
    //   7445: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7448: aload_1
    //   7449: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7452: aload 34
    //   7454: getfield 1014	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   7457: putfield 1166	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:id	J
    //   7460: aload_1
    //   7461: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7464: aload 34
    //   7466: getfield 1167	org/telegram/tgnet/TLRPC$TL_document:date	I
    //   7469: putfield 1168	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:date	I
    //   7472: aload_1
    //   7473: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7476: aload 34
    //   7478: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   7481: putfield 1169	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:access_hash	J
    //   7484: aload_1
    //   7485: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7488: aload 34
    //   7490: getfield 990	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   7493: putfield 1141	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   7496: aload_1
    //   7497: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7500: aload 34
    //   7502: getfield 1142	org/telegram/tgnet/TLRPC$TL_document:size	I
    //   7505: putfield 1101	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   7508: aload_1
    //   7509: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7512: aload 34
    //   7514: getfield 539	org/telegram/tgnet/TLRPC$TL_document:dc_id	I
    //   7517: putfield 1170	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:dc_id	I
    //   7520: aload_1
    //   7521: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7524: aload 34
    //   7526: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   7529: putfield 1171	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:attributes	Ljava/util/ArrayList;
    //   7532: aload 34
    //   7534: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7537: ifnonnull +57 -> 7594
    //   7540: aload_1
    //   7541: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7544: checkcast 1164	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument
    //   7547: new 1173	org/telegram/tgnet/TLRPC$TL_photoSizeEmpty
    //   7550: dup
    //   7551: invokespecial 1174	org/telegram/tgnet/TLRPC$TL_photoSizeEmpty:<init>	()V
    //   7554: putfield 1175	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7557: aload_1
    //   7558: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7561: checkcast 1164	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument
    //   7564: getfield 1175	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7567: ldc_w 1177
    //   7570: putfield 1179	org/telegram/tgnet/TLRPC$PhotoSize:type	Ljava/lang/String;
    //   7573: invokestatic 875	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   7576: aload_1
    //   7577: aload 7
    //   7579: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   7582: aload 35
    //   7584: aconst_null
    //   7585: aconst_null
    //   7586: aload 7
    //   7588: invokevirtual 879	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   7591: goto -1294 -> 6297
    //   7594: aload_1
    //   7595: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7598: checkcast 1164	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument
    //   7601: aload 34
    //   7603: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7606: putfield 1175	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7609: goto -36 -> 7573
    //   7612: aload 34
    //   7614: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7617: invokestatic 1075	org/telegram/messenger/ImageLoader:fillPhotoSizeWithBytes	(Lorg/telegram/tgnet/TLRPC$PhotoSize;)V
    //   7620: aload 35
    //   7622: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   7625: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   7628: bipush 46
    //   7630: if_icmplt +262 -> 7892
    //   7633: aload_1
    //   7634: new 1181	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   7637: dup
    //   7638: invokespecial 1182	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:<init>	()V
    //   7641: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7644: aload_1
    //   7645: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7648: aload 34
    //   7650: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   7653: putfield 1171	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:attributes	Ljava/util/ArrayList;
    //   7656: aload_1
    //   7657: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7660: astore_3
    //   7661: aload 34
    //   7663: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   7666: ifnull +1535 -> 9201
    //   7669: aload 34
    //   7671: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   7674: astore_2
    //   7675: aload_3
    //   7676: aload_2
    //   7677: putfield 1079	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:caption	Ljava/lang/String;
    //   7680: aload 34
    //   7682: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7685: ifnull +175 -> 7860
    //   7688: aload 34
    //   7690: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7693: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7696: ifnull +164 -> 7860
    //   7699: aload_1
    //   7700: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7703: checkcast 1181	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   7706: aload 34
    //   7708: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7711: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7714: putfield 1183	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   7717: aload_1
    //   7718: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7721: aload 34
    //   7723: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7726: getfield 1087	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   7729: putfield 1090	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   7732: aload_1
    //   7733: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7736: aload 34
    //   7738: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7741: getfield 1093	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   7744: putfield 1096	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   7747: aload_1
    //   7748: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7751: aload 34
    //   7753: getfield 1142	org/telegram/tgnet/TLRPC$TL_document:size	I
    //   7756: putfield 1101	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   7759: aload_1
    //   7760: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7763: aload 34
    //   7765: getfield 990	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   7768: putfield 1141	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   7771: aload 34
    //   7773: getfield 1154	org/telegram/tgnet/TLRPC$TL_document:key	[B
    //   7776: ifnonnull +241 -> 8017
    //   7779: new 100	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   7782: dup
    //   7783: aload_0
    //   7784: invokespecial 953	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   7787: astore_2
    //   7788: aload_2
    //   7789: aload 32
    //   7791: putfield 955	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   7794: aload_2
    //   7795: aload_1
    //   7796: putfield 1110	org/telegram/messenger/SendMessagesHelper$DelayedMessage:sendEncryptedRequest	Lorg/telegram/tgnet/TLRPC$TL_decryptedMessage;
    //   7799: aload_2
    //   7800: iconst_2
    //   7801: putfield 956	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   7804: aload_2
    //   7805: aload 7
    //   7807: putfield 959	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   7810: aload_2
    //   7811: aload 35
    //   7813: putfield 1114	org/telegram/messenger/SendMessagesHelper$DelayedMessage:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   7816: aload 10
    //   7818: ifnull +28 -> 7846
    //   7821: aload 10
    //   7823: invokevirtual 205	java/lang/String:length	()I
    //   7826: ifle +20 -> 7846
    //   7829: aload 10
    //   7831: ldc_w 502
    //   7834: invokevirtual 506	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   7837: ifeq +9 -> 7846
    //   7840: aload_2
    //   7841: aload 10
    //   7843: putfield 962	org/telegram/messenger/SendMessagesHelper$DelayedMessage:httpLocation	Ljava/lang/String;
    //   7846: aload_2
    //   7847: aload 34
    //   7849: putfield 996	org/telegram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/telegram/tgnet/TLRPC$TL_document;
    //   7852: aload_0
    //   7853: aload_2
    //   7854: invokespecial 1057	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   7857: goto -1560 -> 6297
    //   7860: aload_1
    //   7861: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7864: checkcast 1181	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   7867: iconst_0
    //   7868: newarray <illegal type>
    //   7870: putfield 1183	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   7873: aload_1
    //   7874: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7877: iconst_0
    //   7878: putfield 1090	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   7881: aload_1
    //   7882: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7885: iconst_0
    //   7886: putfield 1096	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   7889: goto -142 -> 7747
    //   7892: aload_1
    //   7893: new 1185	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument_layer8
    //   7896: dup
    //   7897: invokespecial 1186	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument_layer8:<init>	()V
    //   7900: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7903: aload_1
    //   7904: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7907: aload 34
    //   7909: invokestatic 1190	org/telegram/messenger/FileLoader:getDocumentFileName	(Lorg/telegram/tgnet/TLRPC$Document;)Ljava/lang/String;
    //   7912: putfield 1193	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:file_name	Ljava/lang/String;
    //   7915: aload 34
    //   7917: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7920: ifnull +65 -> 7985
    //   7923: aload 34
    //   7925: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7928: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7931: ifnull +54 -> 7985
    //   7934: aload_1
    //   7935: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7938: checkcast 1185	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument_layer8
    //   7941: aload 34
    //   7943: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7946: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7949: putfield 1194	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument_layer8:thumb	[B
    //   7952: aload_1
    //   7953: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7956: aload 34
    //   7958: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7961: getfield 1087	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   7964: putfield 1090	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   7967: aload_1
    //   7968: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7971: aload 34
    //   7973: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7976: getfield 1093	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   7979: putfield 1096	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   7982: goto -235 -> 7747
    //   7985: aload_1
    //   7986: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7989: checkcast 1185	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument_layer8
    //   7992: iconst_0
    //   7993: newarray <illegal type>
    //   7995: putfield 1194	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument_layer8:thumb	[B
    //   7998: aload_1
    //   7999: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8002: iconst_0
    //   8003: putfield 1090	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   8006: aload_1
    //   8007: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8010: iconst_0
    //   8011: putfield 1096	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   8014: goto -267 -> 7747
    //   8017: new 1120	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile
    //   8020: dup
    //   8021: invokespecial 1121	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:<init>	()V
    //   8024: astore_2
    //   8025: aload_2
    //   8026: aload 34
    //   8028: getfield 1014	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   8031: putfield 1125	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:id	J
    //   8034: aload_2
    //   8035: aload 34
    //   8037: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   8040: putfield 1129	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:access_hash	J
    //   8043: aload_1
    //   8044: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8047: aload 34
    //   8049: getfield 1154	org/telegram/tgnet/TLRPC$TL_document:key	[B
    //   8052: putfield 1130	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:key	[B
    //   8055: aload_1
    //   8056: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8059: aload 34
    //   8061: getfield 1155	org/telegram/tgnet/TLRPC$TL_document:iv	[B
    //   8064: putfield 1134	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:iv	[B
    //   8067: invokestatic 875	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   8070: aload_1
    //   8071: aload 7
    //   8073: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8076: aload 35
    //   8078: aload_2
    //   8079: aconst_null
    //   8080: aload 7
    //   8082: invokevirtual 879	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   8085: goto -1788 -> 6297
    //   8088: iload 19
    //   8090: bipush 8
    //   8092: if_icmpne -1795 -> 6297
    //   8095: new 100	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   8098: dup
    //   8099: aload_0
    //   8100: invokespecial 953	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   8103: astore_3
    //   8104: aload_3
    //   8105: aload 35
    //   8107: putfield 1114	org/telegram/messenger/SendMessagesHelper$DelayedMessage:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   8110: aload_3
    //   8111: aload_1
    //   8112: putfield 1110	org/telegram/messenger/SendMessagesHelper$DelayedMessage:sendEncryptedRequest	Lorg/telegram/tgnet/TLRPC$TL_decryptedMessage;
    //   8115: aload_3
    //   8116: aload 7
    //   8118: putfield 959	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   8121: aload_3
    //   8122: aload 34
    //   8124: putfield 996	org/telegram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/telegram/tgnet/TLRPC$TL_document;
    //   8127: aload_3
    //   8128: iconst_3
    //   8129: putfield 956	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   8132: aload 35
    //   8134: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   8137: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   8140: bipush 46
    //   8142: if_icmplt +189 -> 8331
    //   8145: aload_1
    //   8146: new 1181	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   8149: dup
    //   8150: invokespecial 1182	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:<init>	()V
    //   8153: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8156: aload_1
    //   8157: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8160: aload 34
    //   8162: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   8165: putfield 1171	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:attributes	Ljava/util/ArrayList;
    //   8168: aload_1
    //   8169: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8172: astore 4
    //   8174: aload 34
    //   8176: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   8179: ifnull +1029 -> 9208
    //   8182: aload 34
    //   8184: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   8187: astore_2
    //   8188: aload 4
    //   8190: aload_2
    //   8191: putfield 1079	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:caption	Ljava/lang/String;
    //   8194: aload 34
    //   8196: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8199: ifnull +100 -> 8299
    //   8202: aload 34
    //   8204: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8207: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   8210: ifnull +89 -> 8299
    //   8213: aload_1
    //   8214: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8217: checkcast 1181	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   8220: aload 34
    //   8222: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8225: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   8228: putfield 1183	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   8231: aload_1
    //   8232: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8235: aload 34
    //   8237: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8240: getfield 1087	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   8243: putfield 1090	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   8246: aload_1
    //   8247: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8250: aload 34
    //   8252: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8255: getfield 1093	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   8258: putfield 1096	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   8261: aload_1
    //   8262: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8265: aload 34
    //   8267: getfield 990	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   8270: putfield 1141	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   8273: aload_1
    //   8274: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8277: aload 34
    //   8279: getfield 1142	org/telegram/tgnet/TLRPC$TL_document:size	I
    //   8282: putfield 1101	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   8285: aload_3
    //   8286: aload 32
    //   8288: putfield 955	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   8291: aload_0
    //   8292: aload_3
    //   8293: invokespecial 1057	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   8296: goto -1999 -> 6297
    //   8299: aload_1
    //   8300: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8303: checkcast 1181	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   8306: iconst_0
    //   8307: newarray <illegal type>
    //   8309: putfield 1183	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   8312: aload_1
    //   8313: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8316: iconst_0
    //   8317: putfield 1090	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   8320: aload_1
    //   8321: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8324: iconst_0
    //   8325: putfield 1096	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   8328: goto -67 -> 8261
    //   8331: aload 35
    //   8333: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   8336: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   8339: bipush 17
    //   8341: if_icmplt +92 -> 8433
    //   8344: aload_1
    //   8345: new 1196	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaAudio
    //   8348: dup
    //   8349: invokespecial 1197	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaAudio:<init>	()V
    //   8352: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8355: goto +860 -> 9215
    //   8358: iload 18
    //   8360: aload 34
    //   8362: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   8365: invokevirtual 512	java/util/ArrayList:size	()I
    //   8368: if_icmpge +35 -> 8403
    //   8371: aload 34
    //   8373: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   8376: iload 18
    //   8378: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   8381: checkcast 551	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   8384: astore_2
    //   8385: aload_2
    //   8386: instanceof 770
    //   8389: ifeq +832 -> 9221
    //   8392: aload_1
    //   8393: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8396: aload_2
    //   8397: getfield 773	org/telegram/tgnet/TLRPC$DocumentAttribute:duration	I
    //   8400: putfield 1145	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:duration	I
    //   8403: aload_1
    //   8404: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8407: ldc_w 1199
    //   8410: putfield 1141	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   8413: aload_1
    //   8414: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8417: aload 34
    //   8419: getfield 1142	org/telegram/tgnet/TLRPC$TL_document:size	I
    //   8422: putfield 1101	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   8425: aload_3
    //   8426: iconst_3
    //   8427: putfield 956	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   8430: goto -139 -> 8291
    //   8433: aload_1
    //   8434: new 1201	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaAudio_layer8
    //   8437: dup
    //   8438: invokespecial 1202	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaAudio_layer8:<init>	()V
    //   8441: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8444: goto +771 -> 9215
    //   8447: iload 19
    //   8449: iconst_4
    //   8450: if_icmpne +229 -> 8679
    //   8453: new 1204	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages
    //   8456: dup
    //   8457: invokespecial 1205	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:<init>	()V
    //   8460: astore_1
    //   8461: aload_1
    //   8462: aload 33
    //   8464: putfield 1208	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:to_peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   8467: aload 14
    //   8469: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8472: getfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   8475: ifeq +168 -> 8643
    //   8478: invokestatic 239	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   8481: aload 14
    //   8483: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8486: getfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   8489: ineg
    //   8490: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   8493: invokevirtual 294	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   8496: astore_2
    //   8497: aload_1
    //   8498: new 285	org/telegram/tgnet/TLRPC$TL_inputPeerChannel
    //   8501: dup
    //   8502: invokespecial 1209	org/telegram/tgnet/TLRPC$TL_inputPeerChannel:<init>	()V
    //   8505: putfield 1212	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:from_peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   8508: aload_1
    //   8509: getfield 1212	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:from_peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   8512: aload 14
    //   8514: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8517: getfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   8520: ineg
    //   8521: putfield 290	org/telegram/tgnet/TLRPC$InputPeer:channel_id	I
    //   8524: aload_2
    //   8525: ifnull +14 -> 8539
    //   8528: aload_1
    //   8529: getfield 1212	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:from_peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   8532: aload_2
    //   8533: getfield 1213	org/telegram/tgnet/TLRPC$Chat:access_hash	J
    //   8536: putfield 721	org/telegram/tgnet/TLRPC$InputPeer:access_hash	J
    //   8539: aload 14
    //   8541: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8544: getfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   8547: instanceof 809
    //   8550: ifeq +44 -> 8594
    //   8553: aload_1
    //   8554: getstatic 815	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   8557: ldc_w 817
    //   8560: iconst_0
    //   8561: invokevirtual 823	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   8564: new 700	java/lang/StringBuilder
    //   8567: dup
    //   8568: invokespecial 701	java/lang/StringBuilder:<init>	()V
    //   8571: ldc_w 825
    //   8574: invokevirtual 707	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   8577: lload 8
    //   8579: invokevirtual 724	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   8582: invokevirtual 725	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   8585: iconst_0
    //   8586: invokeinterface 831 3 0
    //   8591: putfield 1214	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:silent	Z
    //   8594: aload_1
    //   8595: getfield 1215	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:random_id	Ljava/util/ArrayList;
    //   8598: aload 26
    //   8600: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   8603: invokestatic 745	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   8606: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   8609: pop
    //   8610: aload 14
    //   8612: invokevirtual 275	org/telegram/messenger/MessageObject:getId	()I
    //   8615: iflt +42 -> 8657
    //   8618: aload_1
    //   8619: getfield 1217	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:id	Ljava/util/ArrayList;
    //   8622: aload 14
    //   8624: invokevirtual 275	org/telegram/messenger/MessageObject:getId	()I
    //   8627: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   8630: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   8633: pop
    //   8634: aload_0
    //   8635: aload_1
    //   8636: aload 7
    //   8638: aconst_null
    //   8639: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   8642: return
    //   8643: aload_1
    //   8644: new 1219	org/telegram/tgnet/TLRPC$TL_inputPeerEmpty
    //   8647: dup
    //   8648: invokespecial 1220	org/telegram/tgnet/TLRPC$TL_inputPeerEmpty:<init>	()V
    //   8651: putfield 1212	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:from_peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   8654: goto -115 -> 8539
    //   8657: aload_1
    //   8658: getfield 1217	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:id	Ljava/util/ArrayList;
    //   8661: aload 14
    //   8663: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8666: getfield 1223	org/telegram/tgnet/TLRPC$Message:fwd_msg_id	I
    //   8669: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   8672: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   8675: pop
    //   8676: goto -42 -> 8634
    //   8679: iload 19
    //   8681: bipush 9
    //   8683: if_icmpne -8676 -> 7
    //   8686: new 1225	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult
    //   8689: dup
    //   8690: invokespecial 1226	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:<init>	()V
    //   8693: astore_1
    //   8694: aload_1
    //   8695: aload 33
    //   8697: putfield 1227	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   8700: aload_1
    //   8701: aload 26
    //   8703: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   8706: putfield 1228	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:random_id	J
    //   8709: aload 11
    //   8711: ifnull +22 -> 8733
    //   8714: aload_1
    //   8715: aload_1
    //   8716: getfield 1229	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:flags	I
    //   8719: iconst_1
    //   8720: ior
    //   8721: putfield 1229	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:flags	I
    //   8724: aload_1
    //   8725: aload 11
    //   8727: invokevirtual 275	org/telegram/messenger/MessageObject:getId	()I
    //   8730: putfield 1230	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:reply_to_msg_id	I
    //   8733: aload 26
    //   8735: getfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   8738: instanceof 809
    //   8741: ifeq +44 -> 8785
    //   8744: aload_1
    //   8745: getstatic 815	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   8748: ldc_w 817
    //   8751: iconst_0
    //   8752: invokevirtual 823	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   8755: new 700	java/lang/StringBuilder
    //   8758: dup
    //   8759: invokespecial 701	java/lang/StringBuilder:<init>	()V
    //   8762: ldc_w 825
    //   8765: invokevirtual 707	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   8768: lload 8
    //   8770: invokevirtual 724	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   8773: invokevirtual 725	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   8776: iconst_0
    //   8777: invokeinterface 831 3 0
    //   8782: putfield 1231	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:silent	Z
    //   8785: aload_1
    //   8786: aload 17
    //   8788: ldc_w 378
    //   8791: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   8794: checkcast 201	java/lang/String
    //   8797: invokestatic 1235	org/telegram/messenger/Utilities:parseLong	(Ljava/lang/String;)Ljava/lang/Long;
    //   8800: invokevirtual 1238	java/lang/Long:longValue	()J
    //   8803: putfield 1240	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:query_id	J
    //   8806: aload_1
    //   8807: aload 17
    //   8809: ldc_w 1241
    //   8812: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   8815: checkcast 201	java/lang/String
    //   8818: putfield 1243	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:id	Ljava/lang/String;
    //   8821: aload 14
    //   8823: ifnonnull +14 -> 8837
    //   8826: aload_1
    //   8827: iconst_1
    //   8828: putfield 1244	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:clear_draft	Z
    //   8831: lload 8
    //   8833: iconst_0
    //   8834: invokestatic 851	org/telegram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   8837: aload_0
    //   8838: aload_1
    //   8839: aload 7
    //   8841: aconst_null
    //   8842: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   8845: return
    //   8846: astore_2
    //   8847: aconst_null
    //   8848: astore_1
    //   8849: aload 27
    //   8851: astore 25
    //   8853: goto -8106 -> 747
    //   8856: aconst_null
    //   8857: astore 27
    //   8859: goto -7368 -> 1491
    //   8862: iconst_0
    //   8863: istore 18
    //   8865: goto -7329 -> 1536
    //   8868: iconst_1
    //   8869: istore 18
    //   8871: aload 12
    //   8873: astore 28
    //   8875: goto -7325 -> 1550
    //   8878: ldc_w 322
    //   8881: astore 27
    //   8883: goto -6976 -> 1907
    //   8886: iconst_2
    //   8887: istore 18
    //   8889: goto -6933 -> 1956
    //   8892: bipush 6
    //   8894: istore 18
    //   8896: aload 12
    //   8898: astore 28
    //   8900: goto -7350 -> 1550
    //   8903: ldc_w 322
    //   8906: astore 26
    //   8908: goto -6473 -> 2435
    //   8911: bipush 7
    //   8913: istore 19
    //   8915: goto -6431 -> 2484
    //   8918: iload 21
    //   8920: iconst_1
    //   8921: iadd
    //   8922: istore 21
    //   8924: goto -6333 -> 2591
    //   8927: astore_2
    //   8928: aconst_null
    //   8929: astore_1
    //   8930: aload 26
    //   8932: astore 25
    //   8934: goto -8187 -> 747
    //   8937: iload 19
    //   8939: ifeq -5468 -> 3471
    //   8942: iload 19
    //   8944: bipush 9
    //   8946: if_icmpne +41 -> 8987
    //   8949: aload_2
    //   8950: ifnull +37 -> 8987
    //   8953: aload 35
    //   8955: ifnull +32 -> 8987
    //   8958: goto -5487 -> 3471
    //   8961: iload 18
    //   8963: iconst_1
    //   8964: iadd
    //   8965: istore 18
    //   8967: goto -5246 -> 3721
    //   8970: astore_2
    //   8971: aload 7
    //   8973: astore_1
    //   8974: aload 26
    //   8976: astore 25
    //   8978: goto -8231 -> 747
    //   8981: iconst_0
    //   8982: istore 24
    //   8984: goto -4949 -> 4035
    //   8987: iload 19
    //   8989: iconst_1
    //   8990: if_icmplt +9 -> 8999
    //   8993: iload 19
    //   8995: iconst_3
    //   8996: if_icmple -4485 -> 4511
    //   8999: iload 19
    //   9001: iconst_5
    //   9002: if_icmplt +10 -> 9012
    //   9005: iload 19
    //   9007: bipush 8
    //   9009: if_icmple -4498 -> 4511
    //   9012: iload 19
    //   9014: bipush 9
    //   9016: if_icmpne -569 -> 8447
    //   9019: aload 35
    //   9021: ifnull -574 -> 8447
    //   9024: goto -4513 -> 4511
    //   9027: iload 19
    //   9029: iconst_2
    //   9030: if_icmpeq -4341 -> 4689
    //   9033: iload 19
    //   9035: bipush 9
    //   9037: if_icmpne -4143 -> 4894
    //   9040: aload 4
    //   9042: ifnull -4148 -> 4894
    //   9045: goto -4356 -> 4689
    //   9048: ldc_w 322
    //   9051: astore_2
    //   9052: goto -4330 -> 4722
    //   9055: ldc_w 322
    //   9058: astore_2
    //   9059: goto -4202 -> 4857
    //   9062: ldc_w 322
    //   9065: astore_3
    //   9066: goto -4123 -> 4943
    //   9069: ldc_w 322
    //   9072: astore_2
    //   9073: goto -4010 -> 5063
    //   9076: iload 19
    //   9078: bipush 7
    //   9080: if_icmpeq -3932 -> 5148
    //   9083: iload 19
    //   9085: bipush 9
    //   9087: if_icmpne -3627 -> 5460
    //   9090: goto -3942 -> 5148
    //   9093: ldc_w 322
    //   9096: astore 5
    //   9098: goto -3808 -> 5290
    //   9101: ldc_w 322
    //   9104: astore_2
    //   9105: goto -3658 -> 5447
    //   9108: ldc_w 322
    //   9111: astore_2
    //   9112: goto -3589 -> 5523
    //   9115: ldc_w 322
    //   9118: astore_2
    //   9119: goto -3525 -> 5594
    //   9122: aload 5
    //   9124: astore_2
    //   9125: goto -3446 -> 5679
    //   9128: ldc_w 322
    //   9131: astore_1
    //   9132: goto -3327 -> 5805
    //   9135: iload 19
    //   9137: iconst_2
    //   9138: if_icmpeq -2753 -> 6385
    //   9141: iload 19
    //   9143: bipush 9
    //   9145: if_icmpne -2325 -> 6820
    //   9148: aload 4
    //   9150: ifnull -2330 -> 6820
    //   9153: goto -2768 -> 6385
    //   9156: ldc_w 322
    //   9159: astore_2
    //   9160: goto -2690 -> 6470
    //   9163: ldc_w 322
    //   9166: astore_2
    //   9167: goto -2253 -> 6914
    //   9170: iload 18
    //   9172: iconst_1
    //   9173: iadd
    //   9174: istore 18
    //   9176: goto -2232 -> 6944
    //   9179: iload 19
    //   9181: bipush 7
    //   9183: if_icmpeq -1754 -> 7429
    //   9186: iload 19
    //   9188: bipush 9
    //   9190: if_icmpne -1102 -> 8088
    //   9193: aload 34
    //   9195: ifnull -1107 -> 8088
    //   9198: goto -1769 -> 7429
    //   9201: ldc_w 322
    //   9204: astore_2
    //   9205: goto -1530 -> 7675
    //   9208: ldc_w 322
    //   9211: astore_2
    //   9212: goto -1024 -> 8188
    //   9215: iconst_0
    //   9216: istore 18
    //   9218: goto -860 -> 8358
    //   9221: iload 18
    //   9223: iconst_1
    //   9224: iadd
    //   9225: istore 18
    //   9227: goto -869 -> 8358
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	9230	0	this	SendMessagesHelper
    //   0	9230	1	paramCharSequence	CharSequence
    //   0	9230	2	paramString1	String
    //   0	9230	3	paramMessageMedia	TLRPC.MessageMedia
    //   0	9230	4	paramTL_photo	TLRPC.TL_photo
    //   0	9230	5	paramVideoEditedInfo	VideoEditedInfo
    //   0	9230	6	paramUser	TLRPC.User
    //   0	9230	7	paramTL_document	TLRPC.TL_document
    //   0	9230	8	paramLong	long
    //   0	9230	10	paramString2	String
    //   0	9230	11	paramMessageObject1	MessageObject
    //   0	9230	12	paramWebPage	TLRPC.WebPage
    //   0	9230	13	paramBoolean	boolean
    //   0	9230	14	paramMessageObject2	MessageObject
    //   0	9230	15	paramArrayList	ArrayList<TLRPC.MessageEntity>
    //   0	9230	16	paramReplyMarkup	TLRPC.ReplyMarkup
    //   0	9230	17	paramHashMap	HashMap<String, String>
    //   53	9173	18	i	int
    //   258	8933	19	j	int
    //   69	3887	20	k	int
    //   2589	6334	21	m	int
    //   66	3154	22	n	int
    //   58	3506	23	i1	int
    //   4033	4950	24	bool	boolean
    //   9	8968	25	localObject1	Object
    //   47	8928	26	localObject2	Object
    //   50	8832	27	localObject3	Object
    //   207	8692	28	localObject4	Object
    //   853	495	29	localMessageMedia	TLRPC.MessageMedia
    //   850	502	30	localTL_photo	TLRPC.TL_photo
    //   846	510	31	localObject5	Object
    //   13	8274	32	localObject6	Object
    //   84	8612	33	localInputPeer	TLRPC.InputPeer
    //   266	8928	34	localTL_document	TLRPC.TL_document
    //   109	8911	35	localObject7	Object
    //   270	7131	36	localObject8	Object
    //   262	4124	37	localObject9	Object
    //   87	3876	38	localObject10	Object
    // Exception table:
    //   from	to	target	type
    //   238	245	744	java/lang/Exception
    //   249	257	744	java/lang/Exception
    //   280	290	744	java/lang/Exception
    //   294	303	744	java/lang/Exception
    //   312	323	744	java/lang/Exception
    //   332	348	744	java/lang/Exception
    //   352	360	744	java/lang/Exception
    //   364	372	744	java/lang/Exception
    //   376	390	744	java/lang/Exception
    //   394	401	744	java/lang/Exception
    //   405	416	744	java/lang/Exception
    //   420	434	744	java/lang/Exception
    //   438	446	744	java/lang/Exception
    //   455	461	744	java/lang/Exception
    //   465	479	744	java/lang/Exception
    //   483	499	744	java/lang/Exception
    //   508	516	744	java/lang/Exception
    //   520	534	744	java/lang/Exception
    //   538	544	744	java/lang/Exception
    //   548	555	744	java/lang/Exception
    //   569	582	744	java/lang/Exception
    //   586	599	744	java/lang/Exception
    //   603	616	744	java/lang/Exception
    //   620	630	744	java/lang/Exception
    //   644	657	744	java/lang/Exception
    //   661	668	744	java/lang/Exception
    //   683	690	744	java/lang/Exception
    //   694	702	744	java/lang/Exception
    //   706	730	744	java/lang/Exception
    //   734	743	744	java/lang/Exception
    //   822	830	744	java/lang/Exception
    //   834	841	744	java/lang/Exception
    //   924	935	744	java/lang/Exception
    //   972	981	744	java/lang/Exception
    //   985	992	744	java/lang/Exception
    //   1013	1022	744	java/lang/Exception
    //   1026	1039	744	java/lang/Exception
    //   1059	1068	744	java/lang/Exception
    //   1075	1088	744	java/lang/Exception
    //   1109	1119	744	java/lang/Exception
    //   1123	1132	744	java/lang/Exception
    //   1205	1215	744	java/lang/Exception
    //   1219	1229	744	java/lang/Exception
    //   1233	1243	744	java/lang/Exception
    //   1247	1257	744	java/lang/Exception
    //   1261	1274	744	java/lang/Exception
    //   1313	1322	744	java/lang/Exception
    //   1326	1339	744	java/lang/Exception
    //   1373	1386	744	java/lang/Exception
    //   1390	1399	744	java/lang/Exception
    //   1408	1416	744	java/lang/Exception
    //   1420	1427	744	java/lang/Exception
    //   1444	1452	744	java/lang/Exception
    //   1456	1464	744	java/lang/Exception
    //   1468	1477	744	java/lang/Exception
    //   1481	1491	744	java/lang/Exception
    //   1500	1512	744	java/lang/Exception
    //   1521	1532	744	java/lang/Exception
    //   1540	1546	744	java/lang/Exception
    //   1554	1562	744	java/lang/Exception
    //   1566	1574	744	java/lang/Exception
    //   1578	1583	744	java/lang/Exception
    //   1587	1594	744	java/lang/Exception
    //   1598	1605	744	java/lang/Exception
    //   1609	1615	744	java/lang/Exception
    //   1629	1640	744	java/lang/Exception
    //   1644	1648	744	java/lang/Exception
    //   1671	1680	744	java/lang/Exception
    //   1687	1699	744	java/lang/Exception
    //   1703	1713	744	java/lang/Exception
    //   1729	1742	744	java/lang/Exception
    //   1746	1755	744	java/lang/Exception
    //   1759	1765	744	java/lang/Exception
    //   1769	1777	744	java/lang/Exception
    //   1786	1797	744	java/lang/Exception
    //   1812	1821	744	java/lang/Exception
    //   1838	1851	744	java/lang/Exception
    //   1855	1864	744	java/lang/Exception
    //   1868	1880	744	java/lang/Exception
    //   1884	1891	744	java/lang/Exception
    //   1899	1907	744	java/lang/Exception
    //   1911	1918	744	java/lang/Exception
    //   1922	1932	744	java/lang/Exception
    //   1941	1952	744	java/lang/Exception
    //   1960	1968	744	java/lang/Exception
    //   1977	1985	744	java/lang/Exception
    //   1989	2000	744	java/lang/Exception
    //   2004	2011	744	java/lang/Exception
    //   2022	2031	744	java/lang/Exception
    //   2038	2074	744	java/lang/Exception
    //   2095	2108	744	java/lang/Exception
    //   2112	2121	744	java/lang/Exception
    //   2125	2137	744	java/lang/Exception
    //   2141	2154	744	java/lang/Exception
    //   2158	2171	744	java/lang/Exception
    //   2175	2188	744	java/lang/Exception
    //   2192	2205	744	java/lang/Exception
    //   2209	2220	744	java/lang/Exception
    //   2224	2235	744	java/lang/Exception
    //   2239	2247	744	java/lang/Exception
    //   2251	2262	744	java/lang/Exception
    //   2266	2277	744	java/lang/Exception
    //   2281	2289	744	java/lang/Exception
    //   2293	2301	744	java/lang/Exception
    //   2310	2321	744	java/lang/Exception
    //   2336	2345	744	java/lang/Exception
    //   2366	2379	744	java/lang/Exception
    //   2383	2392	744	java/lang/Exception
    //   2396	2408	744	java/lang/Exception
    //   2412	2419	744	java/lang/Exception
    //   2427	2435	744	java/lang/Exception
    //   2439	2446	744	java/lang/Exception
    //   2450	2460	744	java/lang/Exception
    //   2469	2480	744	java/lang/Exception
    //   2493	2501	744	java/lang/Exception
    //   2510	2518	744	java/lang/Exception
    //   2522	2530	744	java/lang/Exception
    //   2534	2547	744	java/lang/Exception
    //   2580	2588	744	java/lang/Exception
    //   2607	2620	744	java/lang/Exception
    //   2624	2639	744	java/lang/Exception
    //   2643	2651	744	java/lang/Exception
    //   2655	2668	744	java/lang/Exception
    //   2672	2683	744	java/lang/Exception
    //   2687	2703	744	java/lang/Exception
    //   2722	2731	744	java/lang/Exception
    //   2738	2746	744	java/lang/Exception
    //   2756	2764	744	java/lang/Exception
    //   2775	2785	744	java/lang/Exception
    //   2792	2799	744	java/lang/Exception
    //   2806	2814	744	java/lang/Exception
    //   2818	2831	744	java/lang/Exception
    //   2840	2848	744	java/lang/Exception
    //   2852	2864	744	java/lang/Exception
    //   2868	2878	744	java/lang/Exception
    //   2897	2909	744	java/lang/Exception
    //   2928	2940	744	java/lang/Exception
    //   2959	2967	744	java/lang/Exception
    //   2971	2985	744	java/lang/Exception
    //   2992	3014	744	java/lang/Exception
    //   3021	3027	744	java/lang/Exception
    //   3031	3039	744	java/lang/Exception
    //   3043	3051	744	java/lang/Exception
    //   3058	3064	744	java/lang/Exception
    //   3071	3084	744	java/lang/Exception
    //   3091	3100	744	java/lang/Exception
    //   3203	3216	744	java/lang/Exception
    //   3226	3234	744	java/lang/Exception
    //   3238	3249	744	java/lang/Exception
    //   3253	3259	744	java/lang/Exception
    //   3263	3269	744	java/lang/Exception
    //   3273	3286	744	java/lang/Exception
    //   3537	3547	744	java/lang/Exception
    //   3560	3573	744	java/lang/Exception
    //   3582	3591	744	java/lang/Exception
    //   3600	3608	744	java/lang/Exception
    //   3612	3618	744	java/lang/Exception
    //   3629	3641	744	java/lang/Exception
    //   3645	3656	744	java/lang/Exception
    //   3660	3673	744	java/lang/Exception
    //   3677	3687	744	java/lang/Exception
    //   3695	3703	744	java/lang/Exception
    //   3707	3715	744	java/lang/Exception
    //   3729	3748	744	java/lang/Exception
    //   3752	3773	744	java/lang/Exception
    //   3777	3785	744	java/lang/Exception
    //   3789	3796	744	java/lang/Exception
    //   3800	3817	744	java/lang/Exception
    //   3828	3841	744	java/lang/Exception
    //   3852	3860	744	java/lang/Exception
    //   3874	3893	744	java/lang/Exception
    //   3897	3918	744	java/lang/Exception
    //   3922	3930	744	java/lang/Exception
    //   3934	3941	744	java/lang/Exception
    //   3945	3962	744	java/lang/Exception
    //   1132	1184	8846	java/lang/Exception
    //   3100	3115	8927	java/lang/Exception
    //   3115	3156	8927	java/lang/Exception
    //   3161	3169	8927	java/lang/Exception
    //   3172	3194	8927	java/lang/Exception
    //   3286	3316	8970	java/lang/Exception
    //   3316	3391	8970	java/lang/Exception
    //   3396	3468	8970	java/lang/Exception
    //   3481	3497	8970	java/lang/Exception
    //   3500	3524	8970	java/lang/Exception
    //   3978	4013	8970	java/lang/Exception
    //   4014	4027	8970	java/lang/Exception
    //   4035	4093	8970	java/lang/Exception
    //   4093	4108	8970	java/lang/Exception
    //   4113	4132	8970	java/lang/Exception
    //   4137	4142	8970	java/lang/Exception
    //   4147	4172	8970	java/lang/Exception
    //   4172	4180	8970	java/lang/Exception
    //   4185	4191	8970	java/lang/Exception
    //   4192	4222	8970	java/lang/Exception
    //   4227	4253	8970	java/lang/Exception
    //   4262	4298	8970	java/lang/Exception
    //   4305	4343	8970	java/lang/Exception
    //   4343	4357	8970	java/lang/Exception
    //   4362	4405	8970	java/lang/Exception
    //   4405	4423	8970	java/lang/Exception
    //   4428	4434	8970	java/lang/Exception
    //   4435	4465	8970	java/lang/Exception
    //   4468	4494	8970	java/lang/Exception
    //   4497	4508	8970	java/lang/Exception
    //   4530	4577	8970	java/lang/Exception
    //   4577	4616	8970	java/lang/Exception
    //   4624	4641	8970	java/lang/Exception
    //   4644	4669	8970	java/lang/Exception
    //   4678	4686	8970	java/lang/Exception
    //   4689	4722	8970	java/lang/Exception
    //   4722	4754	8970	java/lang/Exception
    //   4759	4784	8970	java/lang/Exception
    //   4790	4818	8970	java/lang/Exception
    //   4824	4857	8970	java/lang/Exception
    //   4857	4886	8970	java/lang/Exception
    //   4900	4929	8970	java/lang/Exception
    //   4929	4943	8970	java/lang/Exception
    //   4943	5016	8970	java/lang/Exception
    //   5019	5027	8970	java/lang/Exception
    //   5030	5063	8970	java/lang/Exception
    //   5063	5092	8970	java/lang/Exception
    //   5107	5142	8970	java/lang/Exception
    //   5148	5158	8970	java/lang/Exception
    //   5168	5187	8970	java/lang/Exception
    //   5192	5220	8970	java/lang/Exception
    //   5226	5251	8970	java/lang/Exception
    //   5257	5290	8970	java/lang/Exception
    //   5290	5296	8970	java/lang/Exception
    //   5299	5332	8970	java/lang/Exception
    //   5332	5376	8970	java/lang/Exception
    //   5379	5387	8970	java/lang/Exception
    //   5390	5447	8970	java/lang/Exception
    //   5447	5452	8970	java/lang/Exception
    //   5470	5523	8970	java/lang/Exception
    //   5523	5555	8970	java/lang/Exception
    //   5561	5594	8970	java/lang/Exception
    //   5594	5623	8970	java/lang/Exception
    //   5631	5655	8970	java/lang/Exception
    //   5659	5664	8970	java/lang/Exception
    //   5671	5677	8970	java/lang/Exception
    //   5685	5693	8970	java/lang/Exception
    //   5694	5763	8970	java/lang/Exception
    //   5763	5779	8970	java/lang/Exception
    //   5783	5805	8970	java/lang/Exception
    //   5805	5810	8970	java/lang/Exception
    //   5815	5837	8970	java/lang/Exception
    //   5841	5847	8970	java/lang/Exception
    //   5856	5871	8970	java/lang/Exception
    //   5872	5880	8970	java/lang/Exception
    //   5887	5902	8970	java/lang/Exception
    //   5903	5911	8970	java/lang/Exception
    //   5919	5927	8970	java/lang/Exception
    //   5935	5945	8970	java/lang/Exception
    //   5949	5954	8970	java/lang/Exception
    //   5955	5964	8970	java/lang/Exception
    //   5972	5987	8970	java/lang/Exception
    //   5988	5996	8970	java/lang/Exception
    //   5997	6027	8970	java/lang/Exception
    //   6032	6058	8970	java/lang/Exception
    //   6063	6099	8970	java/lang/Exception
    //   6099	6111	8970	java/lang/Exception
    //   6116	6154	8970	java/lang/Exception
    //   6154	6170	8970	java/lang/Exception
    //   6176	6251	8970	java/lang/Exception
    //   6251	6297	8970	java/lang/Exception
    //   6302	6308	8970	java/lang/Exception
    //   6309	6339	8970	java/lang/Exception
    //   6342	6368	8970	java/lang/Exception
    //   6371	6382	8970	java/lang/Exception
    //   6385	6470	8970	java/lang/Exception
    //   6470	6499	8970	java/lang/Exception
    //   6499	6603	8970	java/lang/Exception
    //   6608	6633	8970	java/lang/Exception
    //   6633	6638	8970	java/lang/Exception
    //   6641	6654	8970	java/lang/Exception
    //   6657	6691	8970	java/lang/Exception
    //   6694	6707	8970	java/lang/Exception
    //   6710	6738	8970	java/lang/Exception
    //   6741	6817	8970	java/lang/Exception
    //   6826	6895	8970	java/lang/Exception
    //   6895	6914	8970	java/lang/Exception
    //   6914	6941	8970	java/lang/Exception
    //   6944	7011	8970	java/lang/Exception
    //   7011	7105	8970	java/lang/Exception
    //   7108	7121	8970	java/lang/Exception
    //   7124	7185	8970	java/lang/Exception
    //   7188	7201	8970	java/lang/Exception
    //   7204	7252	8970	java/lang/Exception
    //   7255	7268	8970	java/lang/Exception
    //   7271	7339	8970	java/lang/Exception
    //   7349	7426	8970	java/lang/Exception
    //   7429	7573	8970	java/lang/Exception
    //   7573	7591	8970	java/lang/Exception
    //   7594	7609	8970	java/lang/Exception
    //   7612	7675	8970	java/lang/Exception
    //   7675	7747	8970	java/lang/Exception
    //   7747	7816	8970	java/lang/Exception
    //   7821	7846	8970	java/lang/Exception
    //   7846	7857	8970	java/lang/Exception
    //   7860	7889	8970	java/lang/Exception
    //   7892	7982	8970	java/lang/Exception
    //   7985	8014	8970	java/lang/Exception
    //   8017	8085	8970	java/lang/Exception
    //   8095	8188	8970	java/lang/Exception
    //   8188	8261	8970	java/lang/Exception
    //   8261	8291	8970	java/lang/Exception
    //   8291	8296	8970	java/lang/Exception
    //   8299	8328	8970	java/lang/Exception
    //   8331	8355	8970	java/lang/Exception
    //   8358	8403	8970	java/lang/Exception
    //   8403	8430	8970	java/lang/Exception
    //   8433	8444	8970	java/lang/Exception
    //   8453	8524	8970	java/lang/Exception
    //   8528	8539	8970	java/lang/Exception
    //   8539	8594	8970	java/lang/Exception
    //   8594	8634	8970	java/lang/Exception
    //   8634	8642	8970	java/lang/Exception
    //   8643	8654	8970	java/lang/Exception
    //   8657	8676	8970	java/lang/Exception
    //   8686	8709	8970	java/lang/Exception
    //   8714	8733	8970	java/lang/Exception
    //   8733	8785	8970	java/lang/Exception
    //   8785	8821	8970	java/lang/Exception
    //   8826	8837	8970	java/lang/Exception
    //   8837	8845	8970	java/lang/Exception
  }
  
  public static SendMessagesHelper getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          SendMessagesHelper localSendMessagesHelper2 = Instance;
          localObject1 = localSendMessagesHelper2;
          if (localSendMessagesHelper2 == null) {
            localObject1 = new SendMessagesHelper();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (SendMessagesHelper)localObject1;
          return (SendMessagesHelper)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localSendMessagesHelper1;
  }
  
  private static String getTrimmedString(String paramString)
  {
    String str = paramString.trim();
    if (str.length() == 0) {
      return str;
    }
    for (;;)
    {
      str = paramString;
      if (!paramString.startsWith("\n")) {
        break;
      }
      paramString = paramString.substring(1);
    }
    while (str.endsWith("\n")) {
      str = str.substring(0, str.length() - 1);
    }
    return str;
  }
  
  private void performSendDelayedMessage(DelayedMessage paramDelayedMessage)
  {
    if (paramDelayedMessage.type == 0) {
      if (paramDelayedMessage.httpLocation != null)
      {
        putToDelayedMessages(paramDelayedMessage.httpLocation, paramDelayedMessage);
        ImageLoader.getInstance().loadHttpFile(paramDelayedMessage.httpLocation, "file");
      }
    }
    label882:
    do
    {
      do
      {
        return;
        if (paramDelayedMessage.sendRequest != null)
        {
          localObject = FileLoader.getPathToAttach(paramDelayedMessage.location).toString();
          putToDelayedMessages((String)localObject, paramDelayedMessage);
          FileLoader.getInstance().uploadFile((String)localObject, false, true);
          return;
        }
        localObject = FileLoader.getPathToAttach(paramDelayedMessage.location).toString();
        if ((paramDelayedMessage.sendEncryptedRequest != null) && (paramDelayedMessage.location.dc_id != 0) && (!new File((String)localObject).exists()))
        {
          putToDelayedMessages(FileLoader.getAttachFileName(paramDelayedMessage.location), paramDelayedMessage);
          FileLoader.getInstance().loadFile(paramDelayedMessage.location, "jpg", 0, false);
          return;
        }
        putToDelayedMessages((String)localObject, paramDelayedMessage);
        FileLoader.getInstance().uploadFile((String)localObject, true, true);
        return;
        if (paramDelayedMessage.type == 1)
        {
          if (paramDelayedMessage.videoEditedInfo != null)
          {
            str = paramDelayedMessage.obj.messageOwner.attachPath;
            localObject = str;
            if (str == null) {
              localObject = FileLoader.getInstance().getDirectory(4) + "/" + paramDelayedMessage.documentLocation.id + ".mp4";
            }
            putToDelayedMessages((String)localObject, paramDelayedMessage);
            MediaController.getInstance().scheduleVideoConvert(paramDelayedMessage.obj);
            return;
          }
          if (paramDelayedMessage.sendRequest != null)
          {
            if ((paramDelayedMessage.sendRequest instanceof TLRPC.TL_messages_sendMedia)) {
              localObject = ((TLRPC.TL_messages_sendMedia)paramDelayedMessage.sendRequest).media;
            }
            while (((TLRPC.InputMedia)localObject).file == null)
            {
              str = paramDelayedMessage.obj.messageOwner.attachPath;
              localObject = str;
              if (str == null) {
                localObject = FileLoader.getInstance().getDirectory(4) + "/" + paramDelayedMessage.documentLocation.id + ".mp4";
              }
              putToDelayedMessages((String)localObject, paramDelayedMessage);
              if (paramDelayedMessage.obj.videoEditedInfo != null)
              {
                FileLoader.getInstance().uploadFile((String)localObject, false, false, paramDelayedMessage.documentLocation.size);
                return;
                localObject = ((TLRPC.TL_messages_sendBroadcast)paramDelayedMessage.sendRequest).media;
              }
              else
              {
                FileLoader.getInstance().uploadFile((String)localObject, false, false);
                return;
              }
            }
            localObject = FileLoader.getInstance().getDirectory(4) + "/" + paramDelayedMessage.location.volume_id + "_" + paramDelayedMessage.location.local_id + ".jpg";
            putToDelayedMessages((String)localObject, paramDelayedMessage);
            FileLoader.getInstance().uploadFile((String)localObject, false, true);
            return;
          }
          String str = paramDelayedMessage.obj.messageOwner.attachPath;
          localObject = str;
          if (str == null) {
            localObject = FileLoader.getInstance().getDirectory(4) + "/" + paramDelayedMessage.documentLocation.id + ".mp4";
          }
          putToDelayedMessages((String)localObject, paramDelayedMessage);
          if (paramDelayedMessage.obj.videoEditedInfo != null)
          {
            FileLoader.getInstance().uploadFile((String)localObject, true, false, paramDelayedMessage.documentLocation.size);
            return;
          }
          FileLoader.getInstance().uploadFile((String)localObject, true, false);
          return;
        }
        if (paramDelayedMessage.type != 2) {
          break label882;
        }
        if (paramDelayedMessage.httpLocation != null)
        {
          putToDelayedMessages(paramDelayedMessage.httpLocation, paramDelayedMessage);
          ImageLoader.getInstance().loadHttpFile(paramDelayedMessage.httpLocation, "gif");
          return;
        }
        if (paramDelayedMessage.sendRequest == null) {
          break;
        }
        if ((paramDelayedMessage.sendRequest instanceof TLRPC.TL_messages_sendMedia)) {
          localObject = ((TLRPC.TL_messages_sendMedia)paramDelayedMessage.sendRequest).media;
        }
        while (((TLRPC.InputMedia)localObject).file == null)
        {
          localObject = paramDelayedMessage.obj.messageOwner.attachPath;
          putToDelayedMessages((String)localObject, paramDelayedMessage);
          if (paramDelayedMessage.sendRequest != null)
          {
            FileLoader.getInstance().uploadFile((String)localObject, false, false);
            return;
            localObject = ((TLRPC.TL_messages_sendBroadcast)paramDelayedMessage.sendRequest).media;
          }
          else
          {
            FileLoader.getInstance().uploadFile((String)localObject, true, false);
            return;
          }
        }
      } while ((((TLRPC.InputMedia)localObject).thumb != null) || (paramDelayedMessage.location == null));
      localObject = FileLoader.getInstance().getDirectory(4) + "/" + paramDelayedMessage.location.volume_id + "_" + paramDelayedMessage.location.local_id + ".jpg";
      putToDelayedMessages((String)localObject, paramDelayedMessage);
      FileLoader.getInstance().uploadFile((String)localObject, false, true);
      return;
      localObject = paramDelayedMessage.obj.messageOwner.attachPath;
      if ((paramDelayedMessage.sendEncryptedRequest != null) && (paramDelayedMessage.documentLocation.dc_id != 0) && (!new File((String)localObject).exists()))
      {
        putToDelayedMessages(FileLoader.getAttachFileName(paramDelayedMessage.documentLocation), paramDelayedMessage);
        FileLoader.getInstance().loadFile(paramDelayedMessage.documentLocation, true, false);
        return;
      }
      putToDelayedMessages((String)localObject, paramDelayedMessage);
      FileLoader.getInstance().uploadFile((String)localObject, true, false);
      return;
    } while (paramDelayedMessage.type != 3);
    Object localObject = paramDelayedMessage.obj.messageOwner.attachPath;
    putToDelayedMessages((String)localObject, paramDelayedMessage);
    if (paramDelayedMessage.sendRequest != null)
    {
      FileLoader.getInstance().uploadFile((String)localObject, false, true);
      return;
    }
    FileLoader.getInstance().uploadFile((String)localObject, true, true);
  }
  
  private void performSendMessageRequest(final TLObject paramTLObject, final MessageObject paramMessageObject, final String paramString)
  {
    final TLRPC.Message localMessage = paramMessageObject.messageOwner;
    putToSendingMessages(localMessage);
    ConnectionsManager localConnectionsManager = ConnectionsManager.getInstance();
    paramMessageObject = new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            int j = 0;
            final ArrayList localArrayList;
            final Object localObject1;
            final Object localObject2;
            Object localObject3;
            int i;
            if (paramAnonymousTL_error == null)
            {
              final int k = SendMessagesHelper.8.this.val$newMsgObj.id;
              final boolean bool2 = SendMessagesHelper.8.this.val$req instanceof TLRPC.TL_messages_sendBroadcast;
              localArrayList = new ArrayList();
              final String str = SendMessagesHelper.8.this.val$newMsgObj.attachPath;
              if ((paramAnonymousTLObject instanceof TLRPC.TL_updateShortSentMessage))
              {
                localObject1 = (TLRPC.TL_updateShortSentMessage)paramAnonymousTLObject;
                localObject2 = SendMessagesHelper.8.this.val$newMsgObj;
                localObject3 = SendMessagesHelper.8.this.val$newMsgObj;
                i = ((TLRPC.TL_updateShortSentMessage)localObject1).id;
                ((TLRPC.Message)localObject3).id = i;
                ((TLRPC.Message)localObject2).local_id = i;
                SendMessagesHelper.8.this.val$newMsgObj.date = ((TLRPC.TL_updateShortSentMessage)localObject1).date;
                SendMessagesHelper.8.this.val$newMsgObj.entities = ((TLRPC.TL_updateShortSentMessage)localObject1).entities;
                SendMessagesHelper.8.this.val$newMsgObj.out = ((TLRPC.TL_updateShortSentMessage)localObject1).out;
                if (((TLRPC.TL_updateShortSentMessage)localObject1).media != null)
                {
                  SendMessagesHelper.8.this.val$newMsgObj.media = ((TLRPC.TL_updateShortSentMessage)localObject1).media;
                  localObject2 = SendMessagesHelper.8.this.val$newMsgObj;
                  ((TLRPC.Message)localObject2).flags |= 0x200;
                }
                if (!SendMessagesHelper.8.this.val$newMsgObj.entities.isEmpty())
                {
                  localObject2 = SendMessagesHelper.8.this.val$newMsgObj;
                  ((TLRPC.Message)localObject2).flags |= 0x80;
                }
                Utilities.stageQueue.postRunnable(new Runnable()
                {
                  public void run()
                  {
                    MessagesController.getInstance().processNewDifferenceParams(-1, localObject1.pts, localObject1.date, localObject1.pts_count);
                  }
                });
                localArrayList.add(SendMessagesHelper.8.this.val$newMsgObj);
                i = j;
                j = i;
                if (i == 0)
                {
                  SendMessagesHelper.8.this.val$newMsgObj.send_state = 0;
                  localObject1 = NotificationCenter.getInstance();
                  int m = NotificationCenter.messageReceivedByServer;
                  if (!bool2) {
                    break label878;
                  }
                  j = k;
                  label303:
                  ((NotificationCenter)localObject1).postNotificationName(m, new Object[] { Integer.valueOf(k), Integer.valueOf(j), SendMessagesHelper.8.this.val$newMsgObj, Long.valueOf(SendMessagesHelper.8.this.val$newMsgObj.dialog_id) });
                  MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      Object localObject = MessagesStorage.getInstance();
                      long l = SendMessagesHelper.8.this.val$newMsgObj.random_id;
                      int j = k;
                      if (bool2) {}
                      for (int i = k;; i = SendMessagesHelper.8.this.val$newMsgObj.id)
                      {
                        ((MessagesStorage)localObject).updateMessageStateAndId(l, Integer.valueOf(j), i, 0, false, SendMessagesHelper.8.this.val$newMsgObj.to_id.channel_id);
                        MessagesStorage.getInstance().putMessages(localArrayList, true, false, bool2, 0);
                        if (bool2)
                        {
                          localObject = new ArrayList();
                          ((ArrayList)localObject).add(SendMessagesHelper.8.this.val$newMsgObj);
                          MessagesStorage.getInstance().putMessages((ArrayList)localObject, true, false, false, 0);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                          public void run()
                          {
                            if (SendMessagesHelper.8.1.4.this.val$isBroadcast)
                            {
                              i = 0;
                              while (i < SendMessagesHelper.8.1.4.this.val$sentMessages.size())
                              {
                                Object localObject2 = (TLRPC.Message)SendMessagesHelper.8.1.4.this.val$sentMessages.get(i);
                                localObject1 = new ArrayList();
                                localObject2 = new MessageObject((TLRPC.Message)localObject2, null, false);
                                ((ArrayList)localObject1).add(localObject2);
                                MessagesController.getInstance().updateInterfaceWithMessages(((MessageObject)localObject2).getDialogId(), (ArrayList)localObject1, true);
                                i += 1;
                              }
                              NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                            }
                            SearchQuery.increasePeerRaiting(SendMessagesHelper.8.this.val$newMsgObj.dialog_id);
                            Object localObject1 = NotificationCenter.getInstance();
                            int j = NotificationCenter.messageReceivedByServer;
                            int k = SendMessagesHelper.8.1.4.this.val$oldId;
                            if (SendMessagesHelper.8.1.4.this.val$isBroadcast) {}
                            for (int i = SendMessagesHelper.8.1.4.this.val$oldId;; i = SendMessagesHelper.8.this.val$newMsgObj.id)
                            {
                              ((NotificationCenter)localObject1).postNotificationName(j, new Object[] { Integer.valueOf(k), Integer.valueOf(i), SendMessagesHelper.8.this.val$newMsgObj, Long.valueOf(SendMessagesHelper.8.this.val$newMsgObj.dialog_id) });
                              SendMessagesHelper.this.processSentMessage(SendMessagesHelper.8.1.4.this.val$oldId);
                              SendMessagesHelper.this.removeFromSendingMessages(SendMessagesHelper.8.1.4.this.val$oldId);
                              return;
                            }
                          }
                        });
                        if (MessageObject.isVideoMessage(SendMessagesHelper.8.this.val$newMsgObj)) {
                          SendMessagesHelper.this.stopVideoService(str);
                        }
                        return;
                      }
                    }
                  });
                }
              }
            }
            for (j = i;; j = 1)
            {
              if (j != 0)
              {
                MessagesStorage.getInstance().markMessageAsSendError(SendMessagesHelper.8.this.val$newMsgObj);
                SendMessagesHelper.8.this.val$newMsgObj.send_state = 2;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(SendMessagesHelper.8.this.val$newMsgObj.id) });
                SendMessagesHelper.this.processSentMessage(SendMessagesHelper.8.this.val$newMsgObj.id);
                if (MessageObject.isVideoMessage(SendMessagesHelper.8.this.val$newMsgObj)) {
                  SendMessagesHelper.this.stopVideoService(SendMessagesHelper.8.this.val$newMsgObj.attachPath);
                }
                SendMessagesHelper.this.removeFromSendingMessages(SendMessagesHelper.8.this.val$newMsgObj.id);
              }
              return;
              i = j;
              if (!(paramAnonymousTLObject instanceof TLRPC.Updates)) {
                break;
              }
              localObject3 = ((TLRPC.Updates)paramAnonymousTLObject).updates;
              localObject2 = null;
              i = 0;
              label540:
              localObject1 = localObject2;
              if (i < ((ArrayList)localObject3).size())
              {
                localObject1 = (TLRPC.Update)((ArrayList)localObject3).get(i);
                if ((localObject1 instanceof TLRPC.TL_updateNewMessage))
                {
                  localObject2 = (TLRPC.TL_updateNewMessage)localObject1;
                  localObject1 = ((TLRPC.TL_updateNewMessage)localObject2).message;
                  localArrayList.add(localObject1);
                  SendMessagesHelper.8.this.val$newMsgObj.id = ((TLRPC.TL_updateNewMessage)localObject2).message.id;
                  Utilities.stageQueue.postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      MessagesController.getInstance().processNewDifferenceParams(-1, localObject2.pts, -1, localObject2.pts_count);
                    }
                  });
                }
              }
              else
              {
                label628:
                if (localObject1 == null) {
                  break label873;
                }
                localObject3 = (Integer)MessagesController.getInstance().dialogs_read_outbox_max.get(Long.valueOf(((TLRPC.Message)localObject1).dialog_id));
                localObject2 = localObject3;
                if (localObject3 == null)
                {
                  localObject2 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(((TLRPC.Message)localObject1).out, ((TLRPC.Message)localObject1).dialog_id));
                  MessagesController.getInstance().dialogs_read_outbox_max.put(Long.valueOf(((TLRPC.Message)localObject1).dialog_id), localObject2);
                }
                if (((Integer)localObject2).intValue() >= ((TLRPC.Message)localObject1).id) {
                  break label867;
                }
              }
              label867:
              for (boolean bool1 = true;; bool1 = false)
              {
                ((TLRPC.Message)localObject1).unread = bool1;
                SendMessagesHelper.8.this.val$newMsgObj.id = ((TLRPC.Message)localObject1).id;
                SendMessagesHelper.this.updateMediaPaths(SendMessagesHelper.8.this.val$msgObj, (TLRPC.Message)localObject1, SendMessagesHelper.8.this.val$originalPath, false);
                i = j;
                break;
                if ((localObject1 instanceof TLRPC.TL_updateNewChannelMessage))
                {
                  localObject2 = (TLRPC.TL_updateNewChannelMessage)localObject1;
                  localObject1 = ((TLRPC.TL_updateNewChannelMessage)localObject2).message;
                  localArrayList.add(localObject1);
                  if ((SendMessagesHelper.8.this.val$newMsgObj.flags & 0x80000000) != 0)
                  {
                    localObject3 = ((TLRPC.TL_updateNewChannelMessage)localObject2).message;
                    ((TLRPC.Message)localObject3).flags |= 0x80000000;
                  }
                  Utilities.stageQueue.postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      MessagesController.getInstance().processNewChannelDifferenceParams(localObject2.pts, localObject2.pts_count, localObject2.message.to_id.channel_id);
                    }
                  });
                  break label628;
                }
                i += 1;
                break label540;
              }
              label873:
              i = 1;
              break;
              label878:
              j = SendMessagesHelper.8.this.val$newMsgObj.id;
              break label303;
              if (paramAnonymousTL_error.text.equals("PEER_FLOOD")) {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.needShowAlert, new Object[] { Integer.valueOf(0) });
              }
            }
          }
        });
      }
    };
    paramString = new QuickAckDelegate()
    {
      public void run()
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            SendMessagesHelper.9.this.val$newMsgObj.send_state = 0;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByAck, new Object[] { Integer.valueOf(this.val$msg_id) });
          }
        });
      }
    };
    if ((paramTLObject instanceof TLRPC.TL_messages_sendMessage)) {}
    for (int i = 128;; i = 0)
    {
      localConnectionsManager.sendRequest(paramTLObject, paramMessageObject, paramString, i | 0x44);
      return;
    }
  }
  
  public static void prepareSendingAudioDocuments(ArrayList<MessageObject> paramArrayList, final long paramLong, MessageObject paramMessageObject)
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        int m = this.val$messageObjects.size();
        int i = 0;
        final MessageObject localMessageObject;
        final Object localObject1;
        final Object localObject3;
        int j;
        Object localObject2;
        if (i < m)
        {
          localMessageObject = (MessageObject)this.val$messageObjects.get(i);
          localObject1 = localMessageObject.messageOwner.attachPath;
          localObject3 = new File((String)localObject1);
          if ((int)paramLong != 0) {
            break label187;
          }
          j = 1;
          localObject2 = localObject1;
          if (localObject1 != null) {
            localObject2 = (String)localObject1 + "audio" + ((File)localObject3).length();
          }
          localObject1 = null;
          if (j == 0)
          {
            localObject1 = MessagesStorage.getInstance();
            if (j != 0) {
              break label192;
            }
          }
        }
        label187:
        label192:
        for (int k = 1;; k = 4)
        {
          localObject1 = (TLRPC.TL_document)((MessagesStorage)localObject1).getSentFile((String)localObject2, k);
          localObject3 = localObject1;
          if (localObject1 == null) {
            localObject3 = (TLRPC.TL_document)localMessageObject.messageOwner.media.document;
          }
          if (j == 0) {
            break label289;
          }
          j = (int)(paramLong >> 32);
          localObject1 = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(j));
          if (localObject1 != null) {
            break label197;
          }
          return;
          j = 0;
          break;
        }
        label197:
        if (AndroidUtilities.getPeerLayerVersion(((TLRPC.EncryptedChat)localObject1).layer) < 46) {
          j = 0;
        }
        for (;;)
        {
          if (j < ((TLRPC.TL_document)localObject3).attributes.size())
          {
            if ((((TLRPC.TL_document)localObject3).attributes.get(j) instanceof TLRPC.TL_documentAttributeAudio))
            {
              localObject1 = new TLRPC.TL_documentAttributeAudio_old();
              ((TLRPC.TL_documentAttributeAudio_old)localObject1).duration = ((TLRPC.DocumentAttribute)((TLRPC.TL_document)localObject3).attributes.get(j)).duration;
              ((TLRPC.TL_document)localObject3).attributes.remove(j);
              ((TLRPC.TL_document)localObject3).attributes.add(localObject1);
            }
          }
          else
          {
            label289:
            localObject1 = new HashMap();
            if (localObject2 != null) {
              ((HashMap)localObject1).put("originalPath", localObject2);
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                SendMessagesHelper.getInstance().sendMessage(localObject3, null, localMessageObject.messageOwner.attachPath, SendMessagesHelper.12.this.val$dialog_id, SendMessagesHelper.12.this.val$reply_to_msg, null, localObject1);
              }
            });
            i += 1;
            break;
          }
          j += 1;
        }
      }
    }).start();
  }
  
  public static void prepareSendingBotContextResult(TLRPC.BotInlineResult paramBotInlineResult, final HashMap<String, String> paramHashMap, final long paramLong, MessageObject paramMessageObject)
  {
    if (paramBotInlineResult == null) {}
    do
    {
      return;
      if ((paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaAuto))
      {
        new Thread(new Runnable()
        {
          public void run()
          {
            String str1 = null;
            TLRPC.TL_document localTL_document = null;
            Object localObject7 = null;
            Object localObject6 = null;
            final Object localObject5;
            final String str2;
            final Object localObject1;
            if ((this.val$result instanceof TLRPC.TL_botInlineMediaResult)) {
              if (this.val$result.document != null)
              {
                localObject5 = localTL_document;
                str2 = str1;
                localObject1 = localObject6;
                if ((this.val$result.document instanceof TLRPC.TL_document))
                {
                  localObject5 = (TLRPC.TL_document)this.val$result.document;
                  localObject1 = localObject6;
                  str2 = str1;
                }
              }
            }
            for (;;)
            {
              if ((paramHashMap != null) && (this.val$result.content_url != null)) {
                paramHashMap.put("originalPath", this.val$result.content_url);
              }
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if (localObject5 != null)
                  {
                    localObject5.caption = SendMessagesHelper.14.this.val$result.send_message.caption;
                    SendMessagesHelper.getInstance().sendMessage(localObject5, null, str2, SendMessagesHelper.14.this.val$dialog_id, SendMessagesHelper.14.this.val$reply_to_msg, SendMessagesHelper.14.this.val$result.send_message.reply_markup, SendMessagesHelper.14.this.val$params);
                  }
                  while (localObject1 == null) {
                    return;
                  }
                  localObject1.caption = SendMessagesHelper.14.this.val$result.send_message.caption;
                  SendMessagesHelper.getInstance().sendMessage(localObject1, SendMessagesHelper.14.this.val$result.content_url, SendMessagesHelper.14.this.val$dialog_id, SendMessagesHelper.14.this.val$reply_to_msg, SendMessagesHelper.14.this.val$result.send_message.reply_markup, SendMessagesHelper.14.this.val$params);
                }
              });
              return;
              localObject5 = localTL_document;
              str2 = str1;
              localObject1 = localObject6;
              if (this.val$result.photo != null)
              {
                localObject5 = localTL_document;
                str2 = str1;
                localObject1 = localObject6;
                if ((this.val$result.photo instanceof TLRPC.TL_photo))
                {
                  localObject1 = (TLRPC.TL_photo)this.val$result.photo;
                  localObject5 = localTL_document;
                  str2 = str1;
                  continue;
                  localObject5 = localTL_document;
                  str2 = str1;
                  localObject1 = localObject6;
                  if (this.val$result.content_url != null)
                  {
                    localObject1 = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.val$result.content_url) + "." + ImageLoader.getHttpUrlExtension(this.val$result.content_url, "file"));
                    label277:
                    int i;
                    if (((File)localObject1).exists())
                    {
                      str1 = ((File)localObject1).getAbsolutePath();
                      localObject5 = this.val$result.type;
                      i = -1;
                      switch (((String)localObject5).hashCode())
                      {
                      default: 
                        switch (i)
                        {
                        default: 
                          localObject5 = localTL_document;
                          str2 = str1;
                          localObject1 = localObject6;
                          break;
                        case 0: 
                        case 1: 
                        case 2: 
                        case 3: 
                        case 4: 
                        case 5: 
                          label360:
                          localTL_document = new TLRPC.TL_document();
                          localTL_document.id = 0L;
                          localTL_document.size = 0;
                          localTL_document.dc_id = 0;
                          localTL_document.mime_type = this.val$result.content_type;
                          localTL_document.date = ConnectionsManager.getInstance().getCurrentTime();
                          localObject5 = new TLRPC.TL_documentAttributeFilename();
                          localTL_document.attributes.add(localObject5);
                          localObject1 = this.val$result.type;
                          i = -1;
                          switch (((String)localObject1).hashCode())
                          {
                          default: 
                            label560:
                            switch (i)
                            {
                            }
                            break;
                          }
                          break;
                        }
                        break;
                      }
                    }
                    for (;;)
                    {
                      if (((TLRPC.TL_documentAttributeFilename)localObject5).file_name == null) {
                        ((TLRPC.TL_documentAttributeFilename)localObject5).file_name = "file";
                      }
                      if (localTL_document.mime_type == null) {
                        localTL_document.mime_type = "application/octet-stream";
                      }
                      localObject5 = localTL_document;
                      str2 = str1;
                      localObject1 = localObject6;
                      if (localTL_document.thumb != null) {
                        break;
                      }
                      localTL_document.thumb = new TLRPC.TL_photoSize();
                      localTL_document.thumb.w = this.val$result.w;
                      localTL_document.thumb.h = this.val$result.h;
                      localTL_document.thumb.size = 0;
                      localTL_document.thumb.location = new TLRPC.TL_fileLocationUnavailable();
                      localTL_document.thumb.type = "x";
                      localObject5 = localTL_document;
                      str2 = str1;
                      localObject1 = localObject6;
                      break;
                      str1 = this.val$result.content_url;
                      break label277;
                      if (!((String)localObject5).equals("audio")) {
                        break label360;
                      }
                      i = 0;
                      break label360;
                      if (!((String)localObject5).equals("voice")) {
                        break label360;
                      }
                      i = 1;
                      break label360;
                      if (!((String)localObject5).equals("file")) {
                        break label360;
                      }
                      i = 2;
                      break label360;
                      if (!((String)localObject5).equals("video")) {
                        break label360;
                      }
                      i = 3;
                      break label360;
                      if (!((String)localObject5).equals("sticker")) {
                        break label360;
                      }
                      i = 4;
                      break label360;
                      if (!((String)localObject5).equals("gif")) {
                        break label360;
                      }
                      i = 5;
                      break label360;
                      if (!((String)localObject5).equals("photo")) {
                        break label360;
                      }
                      i = 6;
                      break label360;
                      if (!((String)localObject1).equals("gif")) {
                        break label560;
                      }
                      i = 0;
                      break label560;
                      if (!((String)localObject1).equals("voice")) {
                        break label560;
                      }
                      i = 1;
                      break label560;
                      if (!((String)localObject1).equals("audio")) {
                        break label560;
                      }
                      i = 2;
                      break label560;
                      if (!((String)localObject1).equals("file")) {
                        break label560;
                      }
                      i = 3;
                      break label560;
                      if (!((String)localObject1).equals("video")) {
                        break label560;
                      }
                      i = 4;
                      break label560;
                      if (!((String)localObject1).equals("sticker")) {
                        break label560;
                      }
                      i = 5;
                      break label560;
                      ((TLRPC.TL_documentAttributeFilename)localObject5).file_name = "animation.gif";
                      if (str1.endsWith("mp4"))
                      {
                        localTL_document.mime_type = "video/mp4";
                        localTL_document.attributes.add(new TLRPC.TL_documentAttributeAnimated());
                      }
                      for (;;)
                      {
                        try
                        {
                          if (!str1.endsWith("mp4")) {
                            break label1040;
                          }
                          localObject1 = ThumbnailUtils.createVideoThumbnail(str1, 1);
                          if (localObject1 == null) {
                            break;
                          }
                          localTL_document.thumb = ImageLoader.scaleAndSaveImage((Bitmap)localObject1, 90.0F, 90.0F, 55, false);
                          ((Bitmap)localObject1).recycle();
                        }
                        catch (Throwable localThrowable1)
                        {
                          FileLog.e("tmessages", localThrowable1);
                        }
                        break;
                        localTL_document.mime_type = "image/gif";
                        continue;
                        label1040:
                        localObject2 = ImageLoader.loadBitmap(str1, null, 90.0F, 90.0F, true);
                      }
                      Object localObject2 = new TLRPC.TL_documentAttributeAudio();
                      ((TLRPC.TL_documentAttributeAudio)localObject2).duration = this.val$result.duration;
                      ((TLRPC.TL_documentAttributeAudio)localObject2).voice = true;
                      ((TLRPC.TL_documentAttributeFilename)localObject5).file_name = "audio.ogg";
                      localTL_document.attributes.add(localObject2);
                      localTL_document.thumb = new TLRPC.TL_photoSizeEmpty();
                      localTL_document.thumb.type = "s";
                      continue;
                      localObject2 = new TLRPC.TL_documentAttributeAudio();
                      ((TLRPC.TL_documentAttributeAudio)localObject2).duration = this.val$result.duration;
                      ((TLRPC.TL_documentAttributeAudio)localObject2).title = this.val$result.title;
                      ((TLRPC.TL_documentAttributeAudio)localObject2).flags |= 0x1;
                      if (this.val$result.description != null)
                      {
                        ((TLRPC.TL_documentAttributeAudio)localObject2).performer = this.val$result.description;
                        ((TLRPC.TL_documentAttributeAudio)localObject2).flags |= 0x2;
                      }
                      ((TLRPC.TL_documentAttributeFilename)localObject5).file_name = "audio.mp3";
                      localTL_document.attributes.add(localObject2);
                      localTL_document.thumb = new TLRPC.TL_photoSizeEmpty();
                      localTL_document.thumb.type = "s";
                      continue;
                      i = this.val$result.content_type.indexOf('/');
                      if (i != -1)
                      {
                        ((TLRPC.TL_documentAttributeFilename)localObject5).file_name = ("file." + this.val$result.content_type.substring(i + 1));
                      }
                      else
                      {
                        ((TLRPC.TL_documentAttributeFilename)localObject5).file_name = "file";
                        continue;
                        ((TLRPC.TL_documentAttributeFilename)localObject5).file_name = "video.mp4";
                        localObject2 = new TLRPC.TL_documentAttributeVideo();
                        ((TLRPC.TL_documentAttributeVideo)localObject2).w = this.val$result.w;
                        ((TLRPC.TL_documentAttributeVideo)localObject2).h = this.val$result.h;
                        ((TLRPC.TL_documentAttributeVideo)localObject2).duration = this.val$result.duration;
                        localTL_document.attributes.add(localObject2);
                        try
                        {
                          localObject2 = ImageLoader.loadBitmap(new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.val$result.thumb_url) + "." + ImageLoader.getHttpUrlExtension(this.val$result.thumb_url, "jpg")).getAbsolutePath(), null, 90.0F, 90.0F, true);
                          if (localObject2 == null) {
                            continue;
                          }
                          localTL_document.thumb = ImageLoader.scaleAndSaveImage((Bitmap)localObject2, 90.0F, 90.0F, 55, false);
                          ((Bitmap)localObject2).recycle();
                        }
                        catch (Throwable localThrowable2)
                        {
                          FileLog.e("tmessages", localThrowable2);
                        }
                        continue;
                        Object localObject3 = new TLRPC.TL_documentAttributeSticker();
                        ((TLRPC.TL_documentAttributeSticker)localObject3).alt = "";
                        ((TLRPC.TL_documentAttributeSticker)localObject3).stickerset = new TLRPC.TL_inputStickerSetEmpty();
                        localTL_document.attributes.add(localObject3);
                        localObject3 = new TLRPC.TL_documentAttributeImageSize();
                        ((TLRPC.TL_documentAttributeImageSize)localObject3).w = this.val$result.w;
                        ((TLRPC.TL_documentAttributeImageSize)localObject3).h = this.val$result.h;
                        localTL_document.attributes.add(localObject3);
                        ((TLRPC.TL_documentAttributeFilename)localObject5).file_name = "sticker.webp";
                        try
                        {
                          localObject3 = ImageLoader.loadBitmap(new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.val$result.thumb_url) + "." + ImageLoader.getHttpUrlExtension(this.val$result.thumb_url, "webp")).getAbsolutePath(), null, 90.0F, 90.0F, true);
                          if (localObject3 != null)
                          {
                            localTL_document.thumb = ImageLoader.scaleAndSaveImage((Bitmap)localObject3, 90.0F, 90.0F, 55, false);
                            ((Bitmap)localObject3).recycle();
                          }
                        }
                        catch (Throwable localThrowable3)
                        {
                          FileLog.e("tmessages", localThrowable3);
                        }
                      }
                    }
                    localObject6 = localObject7;
                    if (localThrowable3.exists()) {
                      localObject6 = SendMessagesHelper.getInstance().generatePhotoSizes(str1, null);
                    }
                    localObject5 = localTL_document;
                    str2 = str1;
                    Object localObject4 = localObject6;
                    if (localObject6 == null)
                    {
                      localObject4 = new TLRPC.TL_photo();
                      ((TLRPC.TL_photo)localObject4).date = ConnectionsManager.getInstance().getCurrentTime();
                      localObject5 = new TLRPC.TL_photoSize();
                      ((TLRPC.TL_photoSize)localObject5).w = this.val$result.w;
                      ((TLRPC.TL_photoSize)localObject5).h = this.val$result.h;
                      ((TLRPC.TL_photoSize)localObject5).size = 1;
                      ((TLRPC.TL_photoSize)localObject5).location = new TLRPC.TL_fileLocationUnavailable();
                      ((TLRPC.TL_photoSize)localObject5).type = "x";
                      ((TLRPC.TL_photo)localObject4).sizes.add(localObject5);
                      localObject5 = localTL_document;
                      str2 = str1;
                    }
                  }
                }
              }
            }
          }
        }).run();
        return;
      }
      if ((paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageText))
      {
        localObject = getInstance();
        String str = paramBotInlineResult.send_message.message;
        if (!paramBotInlineResult.send_message.no_webpage) {}
        for (boolean bool = true;; bool = false)
        {
          ((SendMessagesHelper)localObject).sendMessage(str, paramLong, paramMessageObject, null, bool, paramBotInlineResult.send_message.entities, paramBotInlineResult.send_message.reply_markup, paramHashMap);
          return;
        }
      }
      if ((paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue))
      {
        localObject = new TLRPC.TL_messageMediaVenue();
        ((TLRPC.TL_messageMediaVenue)localObject).geo = paramBotInlineResult.send_message.geo;
        ((TLRPC.TL_messageMediaVenue)localObject).address = paramBotInlineResult.send_message.address;
        ((TLRPC.TL_messageMediaVenue)localObject).title = paramBotInlineResult.send_message.title;
        ((TLRPC.TL_messageMediaVenue)localObject).provider = paramBotInlineResult.send_message.provider;
        ((TLRPC.TL_messageMediaVenue)localObject).venue_id = paramBotInlineResult.send_message.venue_id;
        getInstance().sendMessage((TLRPC.MessageMedia)localObject, paramLong, paramMessageObject, paramBotInlineResult.send_message.reply_markup, paramHashMap);
        return;
      }
      if ((paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaGeo))
      {
        localObject = new TLRPC.TL_messageMediaGeo();
        ((TLRPC.TL_messageMediaGeo)localObject).geo = paramBotInlineResult.send_message.geo;
        getInstance().sendMessage((TLRPC.MessageMedia)localObject, paramLong, paramMessageObject, paramBotInlineResult.send_message.reply_markup, paramHashMap);
        return;
      }
    } while (!(paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaContact));
    Object localObject = new TLRPC.TL_user();
    ((TLRPC.User)localObject).phone = paramBotInlineResult.send_message.phone_number;
    ((TLRPC.User)localObject).first_name = paramBotInlineResult.send_message.first_name;
    ((TLRPC.User)localObject).last_name = paramBotInlineResult.send_message.last_name;
    getInstance().sendMessage((TLRPC.User)localObject, paramLong, paramMessageObject, paramBotInlineResult.send_message.reply_markup, paramHashMap);
  }
  
  public static void prepareSendingDocument(String paramString1, String paramString2, Uri paramUri, String paramString3, long paramLong, MessageObject paramMessageObject)
  {
    if (((paramString1 == null) || (paramString2 == null)) && (paramUri == null)) {
      return;
    }
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    ArrayList localArrayList1 = null;
    if (paramUri != null) {
      localArrayList1 = new ArrayList();
    }
    localArrayList2.add(paramString1);
    localArrayList3.add(paramString2);
    prepareSendingDocuments(localArrayList2, localArrayList3, localArrayList1, paramString3, paramLong, paramMessageObject);
  }
  
  private static boolean prepareSendingDocumentInternal(final String paramString1, String paramString2, Uri paramUri, String paramString3, final long paramLong, MessageObject paramMessageObject, String paramString4)
  {
    if (((paramString1 == null) || (paramString1.length() == 0)) && (paramUri == null)) {
      return false;
    }
    if ((paramUri != null) && (AndroidUtilities.isInternalUri(paramUri))) {
      return false;
    }
    if ((paramString1 != null) && (AndroidUtilities.isInternalUri(Uri.fromFile(new File(paramString1))))) {
      return false;
    }
    MimeTypeMap localMimeTypeMap = MimeTypeMap.getSingleton();
    Object localObject2 = null;
    final String str1 = paramString1;
    if (paramUri != null)
    {
      paramString1 = null;
      if (paramString3 != null) {
        paramString1 = localMimeTypeMap.getExtensionFromMimeType(paramString3);
      }
      paramString3 = paramString1;
      if (paramString1 == null) {
        paramString3 = "txt";
      }
      paramString1 = MediaController.copyFileToCache(paramUri, paramString3);
      str1 = paramString1;
      if (paramString1 == null) {
        return false;
      }
    }
    File localFile = new File(str1);
    if ((!localFile.exists()) || (localFile.length() == 0L)) {
      return false;
    }
    boolean bool;
    if ((int)paramLong == 0)
    {
      bool = true;
      if (bool) {
        break label284;
      }
    }
    String str2;
    int j;
    Object localObject1;
    label284:
    for (int i = 1;; i = 0)
    {
      str2 = localFile.getName();
      paramString3 = "";
      j = str1.lastIndexOf('.');
      if (j != -1) {
        paramString3 = str1.substring(j + 1);
      }
      if (!paramString3.toLowerCase().equals("mp3"))
      {
        localObject1 = localObject2;
        if (!paramString3.toLowerCase().equals("m4a")) {
          break label393;
        }
      }
      paramUri = AudioInfo.getAudioInfo(localFile);
      localObject1 = localObject2;
      if (paramUri == null) {
        break label393;
      }
      localObject1 = localObject2;
      if (paramUri.getDuration() == 0L) {
        break label393;
      }
      if (!bool) {
        break label978;
      }
      j = (int)(paramLong >> 32);
      paramString1 = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(j));
      if (paramString1 != null) {
        break label290;
      }
      return false;
      bool = false;
      break;
    }
    label290:
    if (AndroidUtilities.getPeerLayerVersion(paramString1.layer) >= 46) {
      paramString1 = new TLRPC.TL_documentAttributeAudio();
    }
    for (;;)
    {
      paramString1.duration = ((int)(paramUri.getDuration() / 1000L));
      paramString1.title = paramUri.getTitle();
      paramString1.performer = paramUri.getArtist();
      if (paramString1.title == null)
      {
        paramString1.title = "";
        paramString1.flags |= 0x1;
      }
      localObject1 = paramString1;
      if (paramString1.performer == null)
      {
        paramString1.performer = "";
        paramString1.flags |= 0x2;
        localObject1 = paramString1;
      }
      label393:
      paramUri = paramString2;
      if (paramString2 != null)
      {
        if (localObject1 != null) {
          paramUri = paramString2 + "audio" + localFile.length();
        }
      }
      else
      {
        label433:
        paramString1 = null;
        if (!bool)
        {
          paramString1 = MessagesStorage.getInstance();
          if (bool) {
            break label1021;
          }
          j = 1;
          label452:
          paramString2 = (TLRPC.TL_document)paramString1.getSentFile(paramUri, j);
          paramString1 = paramString2;
          if (paramString2 == null)
          {
            paramString1 = paramString2;
            if (!str1.equals(paramUri))
            {
              paramString1 = paramString2;
              if (!bool)
              {
                paramString1 = MessagesStorage.getInstance();
                paramString2 = str1 + localFile.length();
                if (bool) {
                  break label1027;
                }
                j = 1;
                label523:
                paramString1 = (TLRPC.TL_document)paramString1.getSentFile(paramString2, j);
              }
            }
          }
        }
        paramString2 = paramString1;
        if (paramString1 == null)
        {
          paramString1 = new TLRPC.TL_document();
          paramString1.id = 0L;
          paramString1.date = ConnectionsManager.getInstance().getCurrentTime();
          paramString2 = new TLRPC.TL_documentAttributeFilename();
          paramString2.file_name = str2;
          paramString1.attributes.add(paramString2);
          paramString1.size = ((int)localFile.length());
          paramString1.dc_id = 0;
          if (localObject1 != null) {
            paramString1.attributes.add(localObject1);
          }
          if (paramString3.length() == 0) {
            break label1065;
          }
          if (!paramString3.toLowerCase().equals("webp")) {
            break label1033;
          }
          paramString1.mime_type = "image/webp";
          label643:
          if (!paramString1.mime_type.equals("image/gif")) {}
        }
      }
      try
      {
        paramString3 = ImageLoader.loadBitmap(localFile.getAbsolutePath(), null, 90.0F, 90.0F, true);
        if (paramString3 != null)
        {
          paramString2.file_name = "animation.gif";
          paramString1.thumb = ImageLoader.scaleAndSaveImage(paramString3, 90.0F, 90.0F, 55, bool);
          paramString3.recycle();
        }
        if ((paramString1.mime_type.equals("image/webp")) && (i != 0)) {
          paramString2 = new BitmapFactory.Options();
        }
      }
      catch (Exception paramString2)
      {
        try
        {
          paramString2.inJustDecodeBounds = true;
          paramString3 = new RandomAccessFile(str1, "r");
          localObject1 = paramString3.getChannel().map(FileChannel.MapMode.READ_ONLY, 0L, str1.length());
          Utilities.loadWebpImage(null, (ByteBuffer)localObject1, ((ByteBuffer)localObject1).limit(), paramString2, true);
          paramString3.close();
          if ((paramString2.outWidth != 0) && (paramString2.outHeight != 0) && (paramString2.outWidth <= 800) && (paramString2.outHeight <= 800))
          {
            paramString3 = new TLRPC.TL_documentAttributeSticker();
            paramString3.alt = "";
            paramString3.stickerset = new TLRPC.TL_inputStickerSetEmpty();
            paramString1.attributes.add(paramString3);
            paramString3 = new TLRPC.TL_documentAttributeImageSize();
            paramString3.w = paramString2.outWidth;
            paramString3.h = paramString2.outHeight;
            paramString1.attributes.add(paramString3);
          }
          paramString2 = paramString1;
          if (paramString1.thumb == null)
          {
            paramString1.thumb = new TLRPC.TL_photoSizeEmpty();
            paramString1.thumb.type = "s";
            paramString2 = paramString1;
          }
          paramString2.caption = paramString4;
          paramString1 = new HashMap();
          if (paramUri != null) {
            paramString1.put("originalPath", paramUri);
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              SendMessagesHelper.getInstance().sendMessage(this.val$documentFinal, null, str1, paramLong, paramString1, null, this.val$params);
            }
          });
          return true;
          paramString1 = new TLRPC.TL_documentAttributeAudio_old();
          continue;
          label978:
          paramString1 = new TLRPC.TL_documentAttributeAudio();
          continue;
          paramUri = paramString2 + "" + localFile.length();
          break label433;
          label1021:
          j = 4;
          break label452;
          label1027:
          j = 4;
          break label523;
          label1033:
          paramString3 = localMimeTypeMap.getMimeTypeFromExtension(paramString3.toLowerCase());
          if (paramString3 != null)
          {
            paramString1.mime_type = paramString3;
            break label643;
          }
          paramString1.mime_type = "application/octet-stream";
          break label643;
          label1065:
          paramString1.mime_type = "application/octet-stream";
          break label643;
          paramString2 = paramString2;
          FileLog.e("tmessages", paramString2);
        }
        catch (Exception paramString3)
        {
          for (;;)
          {
            FileLog.e("tmessages", paramString3);
          }
        }
      }
    }
  }
  
  public static void prepareSendingDocuments(ArrayList<String> paramArrayList1, final ArrayList<String> paramArrayList2, final ArrayList<Uri> paramArrayList, final String paramString, final long paramLong, MessageObject paramMessageObject)
  {
    if (((paramArrayList1 == null) && (paramArrayList2 == null) && (paramArrayList == null)) || ((paramArrayList1 != null) && (paramArrayList2 != null) && (paramArrayList1.size() != paramArrayList2.size()))) {
      return;
    }
    new Thread(new Runnable()
    {
      public void run()
      {
        int i = 0;
        int j = 0;
        if (this.val$paths != null)
        {
          k = 0;
          for (;;)
          {
            i = j;
            if (k >= this.val$paths.size()) {
              break;
            }
            if (!SendMessagesHelper.prepareSendingDocumentInternal((String)this.val$paths.get(k), (String)paramArrayList2.get(k), null, paramString, paramLong, paramArrayList, null)) {
              j = 1;
            }
            k += 1;
          }
        }
        int k = i;
        if (this.val$uris != null)
        {
          j = 0;
          for (;;)
          {
            k = i;
            if (j >= this.val$uris.size()) {
              break;
            }
            if (!SendMessagesHelper.prepareSendingDocumentInternal(null, null, (Uri)this.val$uris.get(j), paramString, paramLong, paramArrayList, null)) {
              i = 1;
            }
            j += 1;
          }
        }
        if (k != 0) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              try
              {
                Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("UnsupportedAttachment", 2131166428), 0).show();
                return;
              }
              catch (Exception localException)
              {
                FileLog.e("tmessages", localException);
              }
            }
          });
        }
      }
    }).start();
  }
  
  public static void prepareSendingPhoto(String paramString, Uri paramUri, long paramLong, MessageObject paramMessageObject, CharSequence paramCharSequence)
  {
    Object localObject4 = null;
    Object localObject3 = null;
    Object localObject2 = null;
    Object localObject1 = localObject4;
    if (paramString != null)
    {
      localObject1 = localObject4;
      if (paramString.length() != 0)
      {
        localObject1 = new ArrayList();
        ((ArrayList)localObject1).add(paramString);
      }
    }
    paramString = (String)localObject3;
    if (paramUri != null)
    {
      paramString = new ArrayList();
      paramString.add(paramUri);
    }
    paramUri = (Uri)localObject2;
    if (paramCharSequence != null)
    {
      paramUri = new ArrayList();
      paramUri.add(paramCharSequence.toString());
    }
    prepareSendingPhotos((ArrayList)localObject1, paramString, paramLong, paramMessageObject, paramUri);
  }
  
  public static void prepareSendingPhotos(ArrayList<String> paramArrayList1, ArrayList<Uri> paramArrayList, long paramLong, final MessageObject paramMessageObject, final ArrayList<String> paramArrayList2)
  {
    if (((paramArrayList1 == null) && (paramArrayList == null)) || ((paramArrayList1 != null) && (paramArrayList1.isEmpty())) || ((paramArrayList != null) && (paramArrayList.isEmpty()))) {
      return;
    }
    ArrayList localArrayList1 = new ArrayList();
    final ArrayList localArrayList2 = new ArrayList();
    if (paramArrayList1 != null) {
      localArrayList1.addAll(paramArrayList1);
    }
    if (paramArrayList != null) {
      localArrayList2.addAll(paramArrayList);
    }
    new Thread(new Runnable()
    {
      public void run()
      {
        int j;
        Object localObject5;
        Object localObject4;
        Object localObject6;
        int k;
        label37:
        Object localObject2;
        Uri localUri;
        final Object localObject10;
        int m;
        label49:
        Object localObject1;
        label79:
        Object localObject9;
        Object localObject8;
        int n;
        label169:
        Object localObject7;
        if ((int)this.val$dialog_id == 0)
        {
          j = 1;
          localObject5 = null;
          localObject4 = null;
          localObject6 = null;
          if (localArrayList2.isEmpty()) {
            break label306;
          }
          k = localArrayList2.size();
          localObject2 = null;
          localUri = null;
          localObject10 = null;
          m = 0;
          if (m >= k) {
            break label752;
          }
          if (localArrayList2.isEmpty()) {
            break label317;
          }
          localObject1 = (String)localArrayList2.get(m);
          localObject2 = localObject1;
          localObject3 = localObject1;
          localObject9 = localObject2;
          localObject8 = localObject3;
          if (localObject3 == null)
          {
            localObject9 = localObject2;
            localObject8 = localObject3;
            if (localUri != null)
            {
              localObject8 = AndroidUtilities.getPath(localUri);
              localObject9 = localUri.toString();
            }
          }
          n = 0;
          if ((localObject8 == null) || ((!((String)localObject8).endsWith(".gif")) && (!((String)localObject8).endsWith(".webp")))) {
            break label359;
          }
          if (!((String)localObject8).endsWith(".gif")) {
            break label352;
          }
          localObject2 = "gif";
          i = 1;
          localObject7 = localObject8;
          localObject3 = localObject9;
          label179:
          if (i == 0) {
            break label486;
          }
          localObject8 = localObject5;
          if (localObject5 == null)
          {
            localObject8 = new ArrayList();
            localObject4 = new ArrayList();
            localObject6 = new ArrayList();
          }
          ((ArrayList)localObject8).add(localObject7);
          ((ArrayList)localObject4).add(localObject3);
          if (paramMessageObject == null) {
            break label480;
          }
        }
        label306:
        label317:
        label352:
        label359:
        label480:
        for (final Object localObject3 = (String)paramMessageObject.get(m);; localObject3 = null)
        {
          ((ArrayList)localObject6).add(localObject3);
          localObject3 = localObject4;
          localObject9 = localObject6;
          m += 1;
          localObject10 = localObject2;
          localObject2 = localObject1;
          localObject5 = localObject8;
          localObject6 = localObject9;
          localObject4 = localObject3;
          break label49;
          j = 0;
          break;
          k = paramArrayList2.size();
          break label37;
          localObject1 = localObject2;
          if (paramArrayList2.isEmpty()) {
            break label79;
          }
          localUri = (Uri)paramArrayList2.get(m);
          localObject1 = localObject2;
          break label79;
          localObject2 = "webp";
          break label169;
          localObject2 = localObject10;
          i = n;
          localObject3 = localObject9;
          localObject7 = localObject8;
          if (localObject8 != null) {
            break label179;
          }
          localObject2 = localObject10;
          i = n;
          localObject3 = localObject9;
          localObject7 = localObject8;
          if (localUri == null) {
            break label179;
          }
          if (MediaController.isGif(localUri))
          {
            i = 1;
            localObject3 = localUri.toString();
            localObject7 = MediaController.copyFileToCache(localUri, "gif");
            localObject2 = "gif";
            break label179;
          }
          localObject2 = localObject10;
          i = n;
          localObject3 = localObject9;
          localObject7 = localObject8;
          if (!MediaController.isWebp(localUri)) {
            break label179;
          }
          i = 1;
          localObject3 = localUri.toString();
          localObject7 = MediaController.copyFileToCache(localUri, "webp");
          localObject2 = "webp";
          break label179;
        }
        label486:
        if (localObject7 != null)
        {
          localObject7 = new File((String)localObject7);
          localObject7 = (String)localObject3 + ((File)localObject7).length() + "_" + ((File)localObject7).lastModified();
          label540:
          localObject3 = null;
          if (j == 0)
          {
            localObject3 = MessagesStorage.getInstance();
            if (j != 0) {
              break label742;
            }
            i = 0;
            label558:
            localObject8 = (TLRPC.TL_photo)((MessagesStorage)localObject3).getSentFile((String)localObject7, i);
            localObject3 = localObject8;
            if (localObject8 == null)
            {
              localObject3 = localObject8;
              if (localUri != null)
              {
                localObject3 = MessagesStorage.getInstance();
                localObject8 = AndroidUtilities.getPath(localUri);
                if (j != 0) {
                  break label747;
                }
              }
            }
          }
        }
        label742:
        label747:
        for (int i = 0;; i = 3)
        {
          localObject3 = (TLRPC.TL_photo)((MessagesStorage)localObject3).getSentFile((String)localObject8, i);
          localObject10 = localObject3;
          if (localObject3 == null) {
            localObject10 = SendMessagesHelper.getInstance().generatePhotoSizes((String)localObject1, localUri);
          }
          localObject8 = localObject5;
          localObject9 = localObject6;
          localObject3 = localObject4;
          if (localObject10 == null) {
            break;
          }
          if (paramMessageObject != null) {
            ((TLRPC.TL_photo)localObject10).caption = ((String)paramMessageObject.get(m));
          }
          localObject3 = new HashMap();
          if (localObject7 != null) {
            ((HashMap)localObject3).put("originalPath", localObject7);
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              SendMessagesHelper.getInstance().sendMessage(localObject10, null, SendMessagesHelper.17.this.val$dialog_id, SendMessagesHelper.17.this.val$reply_to_msg, null, localObject3);
            }
          });
          localObject8 = localObject5;
          localObject9 = localObject6;
          localObject3 = localObject4;
          break;
          localObject7 = null;
          break label540;
          i = 3;
          break label558;
        }
        label752:
        if ((localObject5 != null) && (!((ArrayList)localObject5).isEmpty()))
        {
          i = 0;
          while (i < ((ArrayList)localObject5).size())
          {
            SendMessagesHelper.prepareSendingDocumentInternal((String)((ArrayList)localObject5).get(i), (String)((ArrayList)localObject4).get(i), null, (String)localObject10, this.val$dialog_id, this.val$reply_to_msg, (String)((ArrayList)localObject6).get(i));
            i += 1;
          }
        }
      }
    }).start();
  }
  
  public static void prepareSendingPhotosSearch(ArrayList<MediaController.SearchImage> paramArrayList, long paramLong, final MessageObject paramMessageObject)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {
      return;
    }
    new Thread(new Runnable()
    {
      public void run()
      {
        boolean bool2;
        int i;
        label13:
        final MediaController.SearchImage localSearchImage;
        final HashMap localHashMap;
        final Object localObject1;
        Object localObject2;
        label87:
        Object localObject5;
        final Object localObject4;
        TLRPC.TL_document localTL_document;
        if ((int)this.val$dialog_id == 0)
        {
          bool2 = true;
          i = 0;
          if (i >= paramMessageObject.size()) {
            return;
          }
          localSearchImage = (MediaController.SearchImage)paramMessageObject.get(i);
          if (localSearchImage.type != 1) {
            break label761;
          }
          localHashMap = new HashMap();
          localObject1 = null;
          if (!(localSearchImage.document instanceof TLRPC.TL_document)) {
            break label575;
          }
          localObject2 = (TLRPC.TL_document)localSearchImage.document;
          localObject1 = FileLoader.getPathToAttach((TLObject)localObject2, true);
          localObject5 = localObject1;
          localObject4 = localObject2;
          if (localObject2 == null)
          {
            if (localSearchImage.localUrl != null) {
              localHashMap.put("url", localSearchImage.localUrl);
            }
            localObject2 = null;
            localTL_document = new TLRPC.TL_document();
            localTL_document.id = 0L;
            localTL_document.date = ConnectionsManager.getInstance().getCurrentTime();
            localObject4 = new TLRPC.TL_documentAttributeFilename();
            ((TLRPC.TL_documentAttributeFilename)localObject4).file_name = "animation.gif";
            localTL_document.attributes.add(localObject4);
            localTL_document.size = localSearchImage.size;
            localTL_document.dc_id = 0;
            if (!((File)localObject1).toString().endsWith("mp4")) {
              break label702;
            }
            localTL_document.mime_type = "video/mp4";
            localTL_document.attributes.add(new TLRPC.TL_documentAttributeAnimated());
            label229:
            if (!((File)localObject1).exists()) {
              break label713;
            }
            localObject2 = localObject1;
            label241:
            localObject4 = localObject2;
            if (localObject2 == null)
            {
              localObject2 = Utilities.MD5(localSearchImage.thumbUrl) + "." + ImageLoader.getHttpUrlExtension(localSearchImage.thumbUrl, "jpg");
              localObject2 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject2);
              localObject4 = localObject2;
              if (!((File)localObject2).exists()) {
                localObject4 = null;
              }
            }
            if (localObject4 == null) {}
          }
        }
        for (;;)
        {
          try
          {
            if (!((File)localObject4).getAbsolutePath().endsWith("mp4")) {
              continue;
            }
            localObject2 = ThumbnailUtils.createVideoThumbnail(((File)localObject4).getAbsolutePath(), 1);
            if (localObject2 != null)
            {
              localTL_document.thumb = ImageLoader.scaleAndSaveImage((Bitmap)localObject2, 90.0F, 90.0F, 55, bool2);
              ((Bitmap)localObject2).recycle();
            }
          }
          catch (Exception localException)
          {
            label575:
            label702:
            label713:
            FileLog.e("tmessages", localException);
            continue;
            localObject1 = ((File)localObject5).toString();
            continue;
          }
          localObject5 = localObject1;
          localObject4 = localTL_document;
          if (localTL_document.thumb == null)
          {
            localTL_document.thumb = new TLRPC.TL_photoSize();
            localTL_document.thumb.w = localSearchImage.width;
            localTL_document.thumb.h = localSearchImage.height;
            localTL_document.thumb.size = 0;
            localTL_document.thumb.location = new TLRPC.TL_fileLocationUnavailable();
            localTL_document.thumb.type = "x";
            localObject4 = localTL_document;
            localObject5 = localObject1;
          }
          if (localSearchImage.caption != null) {
            ((TLRPC.TL_document)localObject4).caption = localSearchImage.caption.toString();
          }
          localObject1 = localSearchImage.imageUrl;
          if (localObject5 != null) {
            continue;
          }
          localObject1 = localSearchImage.imageUrl;
          if ((localHashMap != null) && (localSearchImage.imageUrl != null)) {
            localHashMap.put("originalPath", localSearchImage.imageUrl);
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              SendMessagesHelper.getInstance().sendMessage(localObject4, null, localObject1, SendMessagesHelper.15.this.val$dialog_id, SendMessagesHelper.15.this.val$reply_to_msg, null, localHashMap);
            }
          });
          i += 1;
          break label13;
          bool2 = false;
          break;
          localObject2 = localObject1;
          if (!bool2)
          {
            localObject2 = MessagesStorage.getInstance();
            localObject4 = localSearchImage.imageUrl;
            if (bool2) {
              continue;
            }
            j = 1;
            localObject4 = (TLRPC.Document)((MessagesStorage)localObject2).getSentFile((String)localObject4, j);
            localObject2 = localObject1;
            if ((localObject4 instanceof TLRPC.TL_document)) {
              localObject2 = (TLRPC.TL_document)localObject4;
            }
          }
          localObject1 = Utilities.MD5(localSearchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(localSearchImage.imageUrl, "jpg");
          localObject1 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject1);
          break label87;
          j = 4;
          continue;
          localTL_document.mime_type = "image/gif";
          break label229;
          localObject1 = null;
          break label241;
          localObject2 = ImageLoader.loadBitmap(((File)localObject4).getAbsolutePath(), null, 90.0F, 90.0F, true);
        }
        label761:
        final boolean bool3 = true;
        boolean bool4 = true;
        Object localObject3 = null;
        if (!bool2)
        {
          localObject1 = MessagesStorage.getInstance();
          localObject3 = localSearchImage.imageUrl;
          if (bool2) {
            break label1212;
          }
        }
        label1212:
        for (int j = 0;; j = 3)
        {
          localObject3 = (TLRPC.TL_photo)((MessagesStorage)localObject1).getSentFile((String)localObject3, j);
          localObject4 = localObject3;
          if (localObject3 == null)
          {
            localObject1 = Utilities.MD5(localSearchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(localSearchImage.imageUrl, "jpg");
            localObject4 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject1);
            boolean bool1 = bool4;
            localObject1 = localObject3;
            if (((File)localObject4).exists())
            {
              bool1 = bool4;
              localObject1 = localObject3;
              if (((File)localObject4).length() != 0L)
              {
                localObject3 = SendMessagesHelper.getInstance().generatePhotoSizes(((File)localObject4).toString(), null);
                bool1 = bool4;
                localObject1 = localObject3;
                if (localObject3 != null)
                {
                  bool1 = false;
                  localObject1 = localObject3;
                }
              }
            }
            bool3 = bool1;
            localObject4 = localObject1;
            if (localObject1 == null)
            {
              localObject3 = Utilities.MD5(localSearchImage.thumbUrl) + "." + ImageLoader.getHttpUrlExtension(localSearchImage.thumbUrl, "jpg");
              localObject3 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject3);
              if (((File)localObject3).exists()) {
                localObject1 = SendMessagesHelper.getInstance().generatePhotoSizes(((File)localObject3).toString(), null);
              }
              bool3 = bool1;
              localObject4 = localObject1;
              if (localObject1 == null)
              {
                localObject4 = new TLRPC.TL_photo();
                ((TLRPC.TL_photo)localObject4).date = ConnectionsManager.getInstance().getCurrentTime();
                localObject1 = new TLRPC.TL_photoSize();
                ((TLRPC.TL_photoSize)localObject1).w = localSearchImage.width;
                ((TLRPC.TL_photoSize)localObject1).h = localSearchImage.height;
                ((TLRPC.TL_photoSize)localObject1).size = 0;
                ((TLRPC.TL_photoSize)localObject1).location = new TLRPC.TL_fileLocationUnavailable();
                ((TLRPC.TL_photoSize)localObject1).type = "x";
                ((TLRPC.TL_photo)localObject4).sizes.add(localObject1);
                bool3 = bool1;
              }
            }
          }
          if (localObject4 == null) {
            break;
          }
          if (localSearchImage.caption != null) {
            ((TLRPC.TL_photo)localObject4).caption = localSearchImage.caption.toString();
          }
          localObject1 = new HashMap();
          if (localSearchImage.imageUrl != null) {
            ((HashMap)localObject1).put("originalPath", localSearchImage.imageUrl);
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              SendMessagesHelper localSendMessagesHelper = SendMessagesHelper.getInstance();
              TLRPC.TL_photo localTL_photo = localObject4;
              if (bool3) {}
              for (String str = localSearchImage.imageUrl;; str = null)
              {
                localSendMessagesHelper.sendMessage(localTL_photo, str, SendMessagesHelper.15.this.val$dialog_id, SendMessagesHelper.15.this.val$reply_to_msg, null, localObject1);
                return;
              }
            }
          });
          break;
        }
      }
    }).start();
  }
  
  public static void prepareSendingText(String paramString, final long paramLong)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        Utilities.stageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                String str1 = SendMessagesHelper.getTrimmedString(SendMessagesHelper.16.this.val$text);
                if (str1.length() != 0)
                {
                  int j = (int)Math.ceil(str1.length() / 4096.0F);
                  int i = 0;
                  while (i < j)
                  {
                    String str2 = str1.substring(i * 4096, Math.min((i + 1) * 4096, str1.length()));
                    SendMessagesHelper.getInstance().sendMessage(str2, SendMessagesHelper.16.this.val$dialog_id, null, null, true, null, null, null);
                    i += 1;
                  }
                }
              }
            });
          }
        });
      }
    });
  }
  
  public static void prepareSendingVideo(final String paramString, final long paramLong1, final long paramLong2, int paramInt1, final int paramInt2, VideoEditedInfo paramVideoEditedInfo, long paramLong3, final MessageObject paramMessageObject)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return;
    }
    new Thread(new Runnable()
    {
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 30	org/telegram/messenger/SendMessagesHelper$18:val$dialog_id	J
        //   4: l2i
        //   5: ifne +500 -> 505
        //   8: iconst_1
        //   9: istore_3
        //   10: aload_0
        //   11: getfield 32	org/telegram/messenger/SendMessagesHelper$18:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   14: ifnonnull +15 -> 29
        //   17: aload_0
        //   18: getfield 34	org/telegram/messenger/SendMessagesHelper$18:val$videoPath	Ljava/lang/String;
        //   21: ldc 53
        //   23: invokevirtual 59	java/lang/String:endsWith	(Ljava/lang/String;)Z
        //   26: ifeq +874 -> 900
        //   29: aload_0
        //   30: getfield 34	org/telegram/messenger/SendMessagesHelper$18:val$videoPath	Ljava/lang/String;
        //   33: astore 8
        //   35: aload_0
        //   36: getfield 34	org/telegram/messenger/SendMessagesHelper$18:val$videoPath	Ljava/lang/String;
        //   39: astore 4
        //   41: new 61	java/io/File
        //   44: dup
        //   45: aload 4
        //   47: invokespecial 64	java/io/File:<init>	(Ljava/lang/String;)V
        //   50: astore 7
        //   52: new 66	java/lang/StringBuilder
        //   55: dup
        //   56: invokespecial 67	java/lang/StringBuilder:<init>	()V
        //   59: aload 4
        //   61: invokevirtual 71	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   64: aload 7
        //   66: invokevirtual 75	java/io/File:length	()J
        //   69: invokevirtual 78	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
        //   72: ldc 80
        //   74: invokevirtual 71	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   77: aload 7
        //   79: invokevirtual 83	java/io/File:lastModified	()J
        //   82: invokevirtual 78	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
        //   85: invokevirtual 87	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   88: astore 5
        //   90: aload 5
        //   92: astore 4
        //   94: aload_0
        //   95: getfield 32	org/telegram/messenger/SendMessagesHelper$18:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   98: ifnull +110 -> 208
        //   101: new 66	java/lang/StringBuilder
        //   104: dup
        //   105: invokespecial 67	java/lang/StringBuilder:<init>	()V
        //   108: aload 5
        //   110: invokevirtual 71	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   113: aload_0
        //   114: getfield 36	org/telegram/messenger/SendMessagesHelper$18:val$duration	J
        //   117: invokevirtual 78	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
        //   120: ldc 80
        //   122: invokevirtual 71	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   125: aload_0
        //   126: getfield 32	org/telegram/messenger/SendMessagesHelper$18:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   129: getfield 92	org/telegram/messenger/VideoEditedInfo:startTime	J
        //   132: invokevirtual 78	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
        //   135: ldc 80
        //   137: invokevirtual 71	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   140: aload_0
        //   141: getfield 32	org/telegram/messenger/SendMessagesHelper$18:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   144: getfield 95	org/telegram/messenger/VideoEditedInfo:endTime	J
        //   147: invokevirtual 78	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
        //   150: invokevirtual 87	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   153: astore 5
        //   155: aload 5
        //   157: astore 4
        //   159: aload_0
        //   160: getfield 32	org/telegram/messenger/SendMessagesHelper$18:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   163: getfield 98	org/telegram/messenger/VideoEditedInfo:resultWidth	I
        //   166: aload_0
        //   167: getfield 32	org/telegram/messenger/SendMessagesHelper$18:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   170: getfield 101	org/telegram/messenger/VideoEditedInfo:originalWidth	I
        //   173: if_icmpne +35 -> 208
        //   176: new 66	java/lang/StringBuilder
        //   179: dup
        //   180: invokespecial 67	java/lang/StringBuilder:<init>	()V
        //   183: aload 5
        //   185: invokevirtual 71	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   188: ldc 80
        //   190: invokevirtual 71	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   193: aload_0
        //   194: getfield 32	org/telegram/messenger/SendMessagesHelper$18:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   197: getfield 98	org/telegram/messenger/VideoEditedInfo:resultWidth	I
        //   200: invokevirtual 104	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   203: invokevirtual 87	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   206: astore 4
        //   208: aconst_null
        //   209: astore 5
        //   211: iload_3
        //   212: ifne +3 -> 215
        //   215: aload 8
        //   217: astore 6
        //   219: iconst_0
        //   220: ifne +243 -> 463
        //   223: aload_0
        //   224: getfield 34	org/telegram/messenger/SendMessagesHelper$18:val$videoPath	Ljava/lang/String;
        //   227: iconst_1
        //   228: invokestatic 110	android/media/ThumbnailUtils:createVideoThumbnail	(Ljava/lang/String;I)Landroid/graphics/Bitmap;
        //   231: ldc 111
        //   233: ldc 111
        //   235: bipush 55
        //   237: iload_3
        //   238: invokestatic 117	org/telegram/messenger/ImageLoader:scaleAndSaveImage	(Landroid/graphics/Bitmap;FFIZ)Lorg/telegram/tgnet/TLRPC$PhotoSize;
        //   241: astore 5
        //   243: new 119	org/telegram/tgnet/TLRPC$TL_document
        //   246: dup
        //   247: invokespecial 120	org/telegram/tgnet/TLRPC$TL_document:<init>	()V
        //   250: astore 9
        //   252: aload 9
        //   254: aload 5
        //   256: putfield 124	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
        //   259: aload 9
        //   261: getfield 124	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
        //   264: ifnonnull +246 -> 510
        //   267: aload 9
        //   269: new 126	org/telegram/tgnet/TLRPC$TL_photoSizeEmpty
        //   272: dup
        //   273: invokespecial 127	org/telegram/tgnet/TLRPC$TL_photoSizeEmpty:<init>	()V
        //   276: putfield 124	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
        //   279: aload 9
        //   281: getfield 124	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
        //   284: ldc -127
        //   286: putfield 134	org/telegram/tgnet/TLRPC$PhotoSize:type	Ljava/lang/String;
        //   289: aload 9
        //   291: ldc -120
        //   293: putfield 139	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
        //   296: iconst_0
        //   297: invokestatic 145	org/telegram/messenger/UserConfig:saveConfig	(Z)V
        //   300: new 147	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo
        //   303: dup
        //   304: invokespecial 148	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:<init>	()V
        //   307: astore 10
        //   309: aload 9
        //   311: getfield 152	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
        //   314: aload 10
        //   316: invokevirtual 158	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   319: pop
        //   320: aload_0
        //   321: getfield 32	org/telegram/messenger/SendMessagesHelper$18:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   324: ifnull +220 -> 544
        //   327: aload 10
        //   329: aload_0
        //   330: getfield 36	org/telegram/messenger/SendMessagesHelper$18:val$duration	J
        //   333: ldc2_w 159
        //   336: ldiv
        //   337: l2i
        //   338: putfield 163	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:duration	I
        //   341: aload_0
        //   342: getfield 32	org/telegram/messenger/SendMessagesHelper$18:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   345: getfield 166	org/telegram/messenger/VideoEditedInfo:rotationValue	I
        //   348: bipush 90
        //   350: if_icmpeq +16 -> 366
        //   353: aload_0
        //   354: getfield 32	org/telegram/messenger/SendMessagesHelper$18:val$videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
        //   357: getfield 166	org/telegram/messenger/VideoEditedInfo:rotationValue	I
        //   360: sipush 270
        //   363: if_icmpne +160 -> 523
        //   366: aload 10
        //   368: aload_0
        //   369: getfield 38	org/telegram/messenger/SendMessagesHelper$18:val$height	I
        //   372: putfield 169	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
        //   375: aload 10
        //   377: aload_0
        //   378: getfield 40	org/telegram/messenger/SendMessagesHelper$18:val$width	I
        //   381: putfield 172	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
        //   384: aload 9
        //   386: aload_0
        //   387: getfield 42	org/telegram/messenger/SendMessagesHelper$18:val$estimatedSize	J
        //   390: l2i
        //   391: putfield 175	org/telegram/tgnet/TLRPC$TL_document:size	I
        //   394: new 66	java/lang/StringBuilder
        //   397: dup
        //   398: invokespecial 67	java/lang/StringBuilder:<init>	()V
        //   401: ldc -79
        //   403: invokevirtual 71	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   406: getstatic 180	org/telegram/messenger/UserConfig:lastLocalId	I
        //   409: invokevirtual 104	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   412: ldc -74
        //   414: invokevirtual 71	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   417: invokevirtual 87	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   420: astore 5
        //   422: getstatic 180	org/telegram/messenger/UserConfig:lastLocalId	I
        //   425: iconst_1
        //   426: isub
        //   427: putstatic 180	org/telegram/messenger/UserConfig:lastLocalId	I
        //   430: new 61	java/io/File
        //   433: dup
        //   434: invokestatic 188	org/telegram/messenger/FileLoader:getInstance	()Lorg/telegram/messenger/FileLoader;
        //   437: iconst_4
        //   438: invokevirtual 192	org/telegram/messenger/FileLoader:getDirectory	(I)Ljava/io/File;
        //   441: aload 5
        //   443: invokespecial 195	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
        //   446: astore 5
        //   448: iconst_0
        //   449: invokestatic 145	org/telegram/messenger/UserConfig:saveConfig	(Z)V
        //   452: aload 5
        //   454: invokevirtual 198	java/io/File:getAbsolutePath	()Ljava/lang/String;
        //   457: astore 6
        //   459: aload 9
        //   461: astore 5
        //   463: new 200	java/util/HashMap
        //   466: dup
        //   467: invokespecial 201	java/util/HashMap:<init>	()V
        //   470: astore 7
        //   472: aload 4
        //   474: ifnull +13 -> 487
        //   477: aload 7
        //   479: ldc -53
        //   481: aload 4
        //   483: invokevirtual 207	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   486: pop
        //   487: new 13	org/telegram/messenger/SendMessagesHelper$18$1
        //   490: dup
        //   491: aload_0
        //   492: aload 5
        //   494: aload 6
        //   496: aload 7
        //   498: invokespecial 210	org/telegram/messenger/SendMessagesHelper$18$1:<init>	(Lorg/telegram/messenger/SendMessagesHelper$18;Lorg/telegram/tgnet/TLRPC$TL_document;Ljava/lang/String;Ljava/util/HashMap;)V
        //   501: invokestatic 216	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
        //   504: return
        //   505: iconst_0
        //   506: istore_3
        //   507: goto -497 -> 10
        //   510: aload 9
        //   512: getfield 124	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
        //   515: ldc -127
        //   517: putfield 134	org/telegram/tgnet/TLRPC$PhotoSize:type	Ljava/lang/String;
        //   520: goto -231 -> 289
        //   523: aload 10
        //   525: aload_0
        //   526: getfield 40	org/telegram/messenger/SendMessagesHelper$18:val$width	I
        //   529: putfield 169	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
        //   532: aload 10
        //   534: aload_0
        //   535: getfield 38	org/telegram/messenger/SendMessagesHelper$18:val$height	I
        //   538: putfield 172	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
        //   541: goto -157 -> 384
        //   544: aload 7
        //   546: invokevirtual 220	java/io/File:exists	()Z
        //   549: ifeq +14 -> 563
        //   552: aload 9
        //   554: aload 7
        //   556: invokevirtual 75	java/io/File:length	()J
        //   559: l2i
        //   560: putfield 175	org/telegram/tgnet/TLRPC$TL_document:size	I
        //   563: iconst_0
        //   564: istore_2
        //   565: aconst_null
        //   566: astore 5
        //   568: aconst_null
        //   569: astore 7
        //   571: new 222	android/media/MediaMetadataRetriever
        //   574: dup
        //   575: invokespecial 223	android/media/MediaMetadataRetriever:<init>	()V
        //   578: astore 6
        //   580: aload 6
        //   582: aload_0
        //   583: getfield 34	org/telegram/messenger/SendMessagesHelper$18:val$videoPath	Ljava/lang/String;
        //   586: invokevirtual 226	android/media/MediaMetadataRetriever:setDataSource	(Ljava/lang/String;)V
        //   589: aload 6
        //   591: bipush 18
        //   593: invokevirtual 230	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
        //   596: astore 5
        //   598: aload 5
        //   600: ifnull +13 -> 613
        //   603: aload 10
        //   605: aload 5
        //   607: invokestatic 236	java/lang/Integer:parseInt	(Ljava/lang/String;)I
        //   610: putfield 169	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
        //   613: aload 6
        //   615: bipush 19
        //   617: invokevirtual 230	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
        //   620: astore 5
        //   622: aload 5
        //   624: ifnull +13 -> 637
        //   627: aload 10
        //   629: aload 5
        //   631: invokestatic 236	java/lang/Integer:parseInt	(Ljava/lang/String;)I
        //   634: putfield 172	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
        //   637: aload 6
        //   639: bipush 9
        //   641: invokevirtual 230	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
        //   644: astore 5
        //   646: aload 5
        //   648: ifnull +22 -> 670
        //   651: aload 10
        //   653: aload 5
        //   655: invokestatic 242	java/lang/Long:parseLong	(Ljava/lang/String;)J
        //   658: l2f
        //   659: ldc -13
        //   661: fdiv
        //   662: f2d
        //   663: invokestatic 249	java/lang/Math:ceil	(D)D
        //   666: d2i
        //   667: putfield 163	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:duration	I
        //   670: iconst_1
        //   671: istore_1
        //   672: aload 6
        //   674: ifnull +8 -> 682
        //   677: aload 6
        //   679: invokevirtual 252	android/media/MediaMetadataRetriever:release	()V
        //   682: aload 9
        //   684: astore 5
        //   686: aload 8
        //   688: astore 6
        //   690: iload_1
        //   691: ifne -228 -> 463
        //   694: getstatic 258	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   697: new 61	java/io/File
        //   700: dup
        //   701: aload_0
        //   702: getfield 34	org/telegram/messenger/SendMessagesHelper$18:val$videoPath	Ljava/lang/String;
        //   705: invokespecial 64	java/io/File:<init>	(Ljava/lang/String;)V
        //   708: invokestatic 264	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
        //   711: invokestatic 270	android/media/MediaPlayer:create	(Landroid/content/Context;Landroid/net/Uri;)Landroid/media/MediaPlayer;
        //   714: astore 7
        //   716: aload 9
        //   718: astore 5
        //   720: aload 8
        //   722: astore 6
        //   724: aload 7
        //   726: ifnull -263 -> 463
        //   729: aload 10
        //   731: aload 7
        //   733: invokevirtual 274	android/media/MediaPlayer:getDuration	()I
        //   736: i2f
        //   737: ldc -13
        //   739: fdiv
        //   740: f2d
        //   741: invokestatic 249	java/lang/Math:ceil	(D)D
        //   744: d2i
        //   745: putfield 163	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:duration	I
        //   748: aload 10
        //   750: aload 7
        //   752: invokevirtual 277	android/media/MediaPlayer:getVideoWidth	()I
        //   755: putfield 169	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
        //   758: aload 10
        //   760: aload 7
        //   762: invokevirtual 280	android/media/MediaPlayer:getVideoHeight	()I
        //   765: putfield 172	org/telegram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
        //   768: aload 7
        //   770: invokevirtual 281	android/media/MediaPlayer:release	()V
        //   773: aload 9
        //   775: astore 5
        //   777: aload 8
        //   779: astore 6
        //   781: goto -318 -> 463
        //   784: astore 5
        //   786: ldc_w 283
        //   789: aload 5
        //   791: invokestatic 289	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   794: aload 9
        //   796: astore 5
        //   798: aload 8
        //   800: astore 6
        //   802: goto -339 -> 463
        //   805: astore 5
        //   807: ldc_w 283
        //   810: aload 5
        //   812: invokestatic 289	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   815: goto -133 -> 682
        //   818: astore 5
        //   820: aload 7
        //   822: astore 6
        //   824: aload 5
        //   826: astore 7
        //   828: aload 6
        //   830: astore 5
        //   832: ldc_w 283
        //   835: aload 7
        //   837: invokestatic 289	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   840: iload_2
        //   841: istore_1
        //   842: aload 6
        //   844: ifnull -162 -> 682
        //   847: aload 6
        //   849: invokevirtual 252	android/media/MediaMetadataRetriever:release	()V
        //   852: iload_2
        //   853: istore_1
        //   854: goto -172 -> 682
        //   857: astore 5
        //   859: ldc_w 283
        //   862: aload 5
        //   864: invokestatic 289	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   867: iload_2
        //   868: istore_1
        //   869: goto -187 -> 682
        //   872: astore 4
        //   874: aload 5
        //   876: ifnull +8 -> 884
        //   879: aload 5
        //   881: invokevirtual 252	android/media/MediaMetadataRetriever:release	()V
        //   884: aload 4
        //   886: athrow
        //   887: astore 5
        //   889: ldc_w 283
        //   892: aload 5
        //   894: invokestatic 289	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   897: goto -13 -> 884
        //   900: aload_0
        //   901: getfield 34	org/telegram/messenger/SendMessagesHelper$18:val$videoPath	Ljava/lang/String;
        //   904: aload_0
        //   905: getfield 34	org/telegram/messenger/SendMessagesHelper$18:val$videoPath	Ljava/lang/String;
        //   908: aconst_null
        //   909: aconst_null
        //   910: aload_0
        //   911: getfield 30	org/telegram/messenger/SendMessagesHelper$18:val$dialog_id	J
        //   914: aload_0
        //   915: getfield 44	org/telegram/messenger/SendMessagesHelper$18:val$reply_to_msg	Lorg/telegram/messenger/MessageObject;
        //   918: aconst_null
        //   919: invokestatic 293	org/telegram/messenger/SendMessagesHelper:access$1000	(Ljava/lang/String;Ljava/lang/String;Landroid/net/Uri;Ljava/lang/String;JLorg/telegram/messenger/MessageObject;Ljava/lang/String;)Z
        //   922: pop
        //   923: return
        //   924: astore 4
        //   926: aload 6
        //   928: astore 5
        //   930: goto -56 -> 874
        //   933: astore 7
        //   935: goto -107 -> 828
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	938	0	this	18
        //   671	198	1	i	int
        //   564	304	2	j	int
        //   9	498	3	bool	boolean
        //   39	443	4	localObject1	Object
        //   872	13	4	localObject2	Object
        //   924	1	4	localObject3	Object
        //   88	688	5	localObject4	Object
        //   784	6	5	localException1	Exception
        //   796	1	5	localObject5	Object
        //   805	6	5	localException2	Exception
        //   818	7	5	localException3	Exception
        //   830	1	5	localObject6	Object
        //   857	23	5	localException4	Exception
        //   887	6	5	localException5	Exception
        //   928	1	5	localObject7	Object
        //   217	710	6	localObject8	Object
        //   50	786	7	localObject9	Object
        //   933	1	7	localException6	Exception
        //   33	766	8	str	String
        //   250	545	9	localTL_document	TLRPC.TL_document
        //   307	452	10	localTL_documentAttributeVideo	TLRPC.TL_documentAttributeVideo
        // Exception table:
        //   from	to	target	type
        //   694	716	784	java/lang/Exception
        //   729	773	784	java/lang/Exception
        //   677	682	805	java/lang/Exception
        //   571	580	818	java/lang/Exception
        //   847	852	857	java/lang/Exception
        //   571	580	872	finally
        //   832	840	872	finally
        //   879	884	887	java/lang/Exception
        //   580	598	924	finally
        //   603	613	924	finally
        //   613	622	924	finally
        //   627	637	924	finally
        //   637	646	924	finally
        //   651	670	924	finally
        //   580	598	933	java/lang/Exception
        //   603	613	933	java/lang/Exception
        //   613	622	933	java/lang/Exception
        //   627	637	933	java/lang/Exception
        //   637	646	933	java/lang/Exception
        //   651	670	933	java/lang/Exception
      }
    }).start();
  }
  
  private void putToDelayedMessages(String paramString, DelayedMessage paramDelayedMessage)
  {
    ArrayList localArrayList2 = (ArrayList)this.delayedMessages.get(paramString);
    ArrayList localArrayList1 = localArrayList2;
    if (localArrayList2 == null)
    {
      localArrayList1 = new ArrayList();
      this.delayedMessages.put(paramString, localArrayList1);
    }
    localArrayList1.add(paramDelayedMessage);
  }
  
  private void sendLocation(Location paramLocation)
  {
    TLRPC.TL_messageMediaGeo localTL_messageMediaGeo = new TLRPC.TL_messageMediaGeo();
    localTL_messageMediaGeo.geo = new TLRPC.TL_geoPoint();
    localTL_messageMediaGeo.geo.lat = paramLocation.getLatitude();
    localTL_messageMediaGeo.geo._long = paramLocation.getLongitude();
    paramLocation = this.waitingForLocation.entrySet().iterator();
    while (paramLocation.hasNext())
    {
      MessageObject localMessageObject = (MessageObject)((Map.Entry)paramLocation.next()).getValue();
      getInstance().sendMessage(localTL_messageMediaGeo, localMessageObject.getDialogId(), localMessageObject, null, null);
    }
  }
  
  /* Error */
  private void sendMessage(String paramString1, TLRPC.MessageMedia paramMessageMedia, TLRPC.TL_photo paramTL_photo, VideoEditedInfo paramVideoEditedInfo, TLRPC.User paramUser, TLRPC.TL_document paramTL_document, long paramLong, String paramString2, MessageObject paramMessageObject1, TLRPC.WebPage paramWebPage, boolean paramBoolean, MessageObject paramMessageObject2, ArrayList<TLRPC.MessageEntity> paramArrayList, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    // Byte code:
    //   0: lload 7
    //   2: lconst_0
    //   3: lcmp
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aconst_null
    //   9: astore 24
    //   11: aload 24
    //   13: astore 31
    //   15: aload 16
    //   17: ifnull +29 -> 46
    //   20: aload 24
    //   22: astore 31
    //   24: aload 16
    //   26: ldc -34
    //   28: invokevirtual 226	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   31: ifeq +15 -> 46
    //   34: aload 16
    //   36: ldc -34
    //   38: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   41: checkcast 201	java/lang/String
    //   44: astore 31
    //   46: aconst_null
    //   47: astore 25
    //   49: aconst_null
    //   50: astore 26
    //   52: iconst_m1
    //   53: istore 17
    //   55: lload 7
    //   57: l2i
    //   58: istore 22
    //   60: lload 7
    //   62: bipush 32
    //   64: lshr
    //   65: l2i
    //   66: istore 21
    //   68: iconst_0
    //   69: istore 19
    //   71: aconst_null
    //   72: astore 24
    //   74: iload 22
    //   76: ifeq +99 -> 175
    //   79: iload 22
    //   81: invokestatic 236	org/telegram/messenger/MessagesController:getInputPeer	(I)Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   84: astore 32
    //   86: aconst_null
    //   87: astore 38
    //   89: iload 22
    //   91: ifne +90 -> 181
    //   94: invokestatic 239	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   97: iload 21
    //   99: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   102: invokevirtual 249	org/telegram/messenger/MessagesController:getEncryptedChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   105: astore 24
    //   107: aload 24
    //   109: astore 34
    //   111: aload 24
    //   113: ifnonnull +116 -> 229
    //   116: aload 13
    //   118: ifnull -111 -> 7
    //   121: invokestatic 254	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
    //   124: aload 13
    //   126: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   129: invokevirtual 264	org/telegram/messenger/MessagesStorage:markMessageAsSendError	(Lorg/telegram/tgnet/TLRPC$Message;)V
    //   132: aload 13
    //   134: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   137: iconst_2
    //   138: putfield 269	org/telegram/tgnet/TLRPC$Message:send_state	I
    //   141: invokestatic 165	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   144: getstatic 272	org/telegram/messenger/NotificationCenter:messageSendError	I
    //   147: iconst_1
    //   148: anewarray 4	java/lang/Object
    //   151: dup
    //   152: iconst_0
    //   153: aload 13
    //   155: invokevirtual 275	org/telegram/messenger/MessageObject:getId	()I
    //   158: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   161: aastore
    //   162: invokevirtual 279	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   165: aload_0
    //   166: aload 13
    //   168: invokevirtual 275	org/telegram/messenger/MessageObject:getId	()I
    //   171: invokevirtual 283	org/telegram/messenger/SendMessagesHelper:processSentMessage	(I)V
    //   174: return
    //   175: aconst_null
    //   176: astore 32
    //   178: goto -92 -> 86
    //   181: aload 24
    //   183: astore 34
    //   185: aload 32
    //   187: instanceof 285
    //   190: ifeq +39 -> 229
    //   193: invokestatic 239	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   196: aload 32
    //   198: getfield 290	org/telegram/tgnet/TLRPC$InputPeer:channel_id	I
    //   201: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   204: invokevirtual 294	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   207: astore 27
    //   209: aload 27
    //   211: ifnull +597 -> 808
    //   214: aload 27
    //   216: getfield 300	org/telegram/tgnet/TLRPC$Chat:megagroup	Z
    //   219: ifne +589 -> 808
    //   222: iconst_1
    //   223: istore 19
    //   225: aload 24
    //   227: astore 34
    //   229: aload 13
    //   231: ifnull +1122 -> 1353
    //   234: aload 26
    //   236: astore 24
    //   238: aload 13
    //   240: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   243: astore 26
    //   245: aload 26
    //   247: astore 24
    //   249: aload 13
    //   251: invokevirtual 304	org/telegram/messenger/MessageObject:isForwarded	()Z
    //   254: ifeq +564 -> 818
    //   257: iconst_4
    //   258: istore 18
    //   260: aload 11
    //   262: astore 37
    //   264: aload 6
    //   266: astore 33
    //   268: aload 5
    //   270: astore 35
    //   272: aload_1
    //   273: astore 36
    //   275: aload 26
    //   277: astore 25
    //   279: aload 25
    //   281: astore 24
    //   283: aload 25
    //   285: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   288: lconst_0
    //   289: lcmp
    //   290: ifne +16 -> 306
    //   293: aload 25
    //   295: astore 24
    //   297: aload 25
    //   299: aload_0
    //   300: invokevirtual 312	org/telegram/messenger/SendMessagesHelper:getNextRandomId	()J
    //   303: putfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   306: aload 16
    //   308: ifnull +85 -> 393
    //   311: aload 25
    //   313: astore 24
    //   315: aload 16
    //   317: ldc_w 314
    //   320: invokevirtual 226	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   323: ifeq +70 -> 393
    //   326: aload 34
    //   328: ifnull +2664 -> 2992
    //   331: aload 25
    //   333: astore 24
    //   335: aload 25
    //   337: aload 16
    //   339: ldc_w 316
    //   342: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   345: checkcast 201	java/lang/String
    //   348: putfield 320	org/telegram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   351: aload 25
    //   353: astore 24
    //   355: aload 25
    //   357: getfield 320	org/telegram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   360: ifnonnull +15 -> 375
    //   363: aload 25
    //   365: astore 24
    //   367: aload 25
    //   369: ldc_w 322
    //   372: putfield 320	org/telegram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   375: aload 25
    //   377: astore 24
    //   379: aload 25
    //   381: aload 25
    //   383: getfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   386: sipush 2048
    //   389: ior
    //   390: putfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   393: aload 25
    //   395: astore 24
    //   397: aload 25
    //   399: aload 16
    //   401: putfield 328	org/telegram/tgnet/TLRPC$Message:params	Ljava/util/HashMap;
    //   404: aload 25
    //   406: astore 24
    //   408: aload 25
    //   410: invokestatic 333	org/telegram/tgnet/ConnectionsManager:getInstance	()Lorg/telegram/tgnet/ConnectionsManager;
    //   413: invokevirtual 336	org/telegram/tgnet/ConnectionsManager:getCurrentTime	()I
    //   416: putfield 339	org/telegram/tgnet/TLRPC$Message:date	I
    //   419: aload 25
    //   421: astore 24
    //   423: aload 25
    //   425: aload 25
    //   427: getfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   430: sipush 512
    //   433: ior
    //   434: putfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   437: aload 25
    //   439: astore 24
    //   441: aload 32
    //   443: instanceof 285
    //   446: ifeq +2611 -> 3057
    //   449: iload 19
    //   451: ifeq +31 -> 482
    //   454: aload 25
    //   456: astore 24
    //   458: aload 25
    //   460: iconst_1
    //   461: putfield 342	org/telegram/tgnet/TLRPC$Message:views	I
    //   464: aload 25
    //   466: astore 24
    //   468: aload 25
    //   470: aload 25
    //   472: getfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   475: sipush 1024
    //   478: ior
    //   479: putfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   482: aload 25
    //   484: astore 24
    //   486: invokestatic 239	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   489: aload 32
    //   491: getfield 290	org/telegram/tgnet/TLRPC$InputPeer:channel_id	I
    //   494: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   497: invokevirtual 294	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   500: astore_1
    //   501: aload_1
    //   502: ifnull +42 -> 544
    //   505: aload 25
    //   507: astore 24
    //   509: aload_1
    //   510: getfield 300	org/telegram/tgnet/TLRPC$Chat:megagroup	Z
    //   513: ifeq +2508 -> 3021
    //   516: aload 25
    //   518: astore 24
    //   520: aload 25
    //   522: aload 25
    //   524: getfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   527: ldc_w 343
    //   530: ior
    //   531: putfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   534: aload 25
    //   536: astore 24
    //   538: aload 25
    //   540: iconst_1
    //   541: putfield 346	org/telegram/tgnet/TLRPC$Message:unread	Z
    //   544: aload 25
    //   546: astore 24
    //   548: aload 25
    //   550: lload 7
    //   552: putfield 349	org/telegram/tgnet/TLRPC$Message:dialog_id	J
    //   555: aload 10
    //   557: ifnull +73 -> 630
    //   560: aload 34
    //   562: ifnull +2508 -> 3070
    //   565: aload 25
    //   567: astore 24
    //   569: aload 10
    //   571: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   574: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   577: lconst_0
    //   578: lcmp
    //   579: ifeq +2491 -> 3070
    //   582: aload 25
    //   584: astore 24
    //   586: aload 25
    //   588: aload 10
    //   590: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   593: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   596: putfield 352	org/telegram/tgnet/TLRPC$Message:reply_to_random_id	J
    //   599: aload 25
    //   601: astore 24
    //   603: aload 25
    //   605: aload 25
    //   607: getfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   610: bipush 8
    //   612: ior
    //   613: putfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   616: aload 25
    //   618: astore 24
    //   620: aload 25
    //   622: aload 10
    //   624: invokevirtual 275	org/telegram/messenger/MessageObject:getId	()I
    //   627: putfield 355	org/telegram/tgnet/TLRPC$Message:reply_to_msg_id	I
    //   630: aload 15
    //   632: ifnull +36 -> 668
    //   635: aload 34
    //   637: ifnonnull +31 -> 668
    //   640: aload 25
    //   642: astore 24
    //   644: aload 25
    //   646: aload 25
    //   648: getfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   651: bipush 64
    //   653: ior
    //   654: putfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   657: aload 25
    //   659: astore 24
    //   661: aload 25
    //   663: aload 15
    //   665: putfield 359	org/telegram/tgnet/TLRPC$Message:reply_markup	Lorg/telegram/tgnet/TLRPC$ReplyMarkup;
    //   668: iload 22
    //   670: ifeq +2949 -> 3619
    //   673: iload 21
    //   675: iconst_1
    //   676: if_icmpne +2854 -> 3530
    //   679: aload 25
    //   681: astore 24
    //   683: aload_0
    //   684: getfield 138	org/telegram/messenger/SendMessagesHelper:currentChatInfo	Lorg/telegram/tgnet/TLRPC$ChatFull;
    //   687: ifnonnull +2403 -> 3090
    //   690: aload 25
    //   692: astore 24
    //   694: invokestatic 254	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
    //   697: aload 25
    //   699: invokevirtual 264	org/telegram/messenger/MessagesStorage:markMessageAsSendError	(Lorg/telegram/tgnet/TLRPC$Message;)V
    //   702: aload 25
    //   704: astore 24
    //   706: invokestatic 165	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   709: getstatic 272	org/telegram/messenger/NotificationCenter:messageSendError	I
    //   712: iconst_1
    //   713: anewarray 4	java/lang/Object
    //   716: dup
    //   717: iconst_0
    //   718: aload 25
    //   720: getfield 362	org/telegram/tgnet/TLRPC$Message:id	I
    //   723: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   726: aastore
    //   727: invokevirtual 279	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   730: aload 25
    //   732: astore 24
    //   734: aload_0
    //   735: aload 25
    //   737: getfield 362	org/telegram/tgnet/TLRPC$Message:id	I
    //   740: invokevirtual 283	org/telegram/messenger/SendMessagesHelper:processSentMessage	(I)V
    //   743: return
    //   744: astore_2
    //   745: aconst_null
    //   746: astore_1
    //   747: ldc_w 364
    //   750: aload_2
    //   751: invokestatic 370	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   754: invokestatic 254	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
    //   757: aload 24
    //   759: invokevirtual 264	org/telegram/messenger/MessagesStorage:markMessageAsSendError	(Lorg/telegram/tgnet/TLRPC$Message;)V
    //   762: aload_1
    //   763: ifnull +11 -> 774
    //   766: aload_1
    //   767: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   770: iconst_2
    //   771: putfield 269	org/telegram/tgnet/TLRPC$Message:send_state	I
    //   774: invokestatic 165	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   777: getstatic 272	org/telegram/messenger/NotificationCenter:messageSendError	I
    //   780: iconst_1
    //   781: anewarray 4	java/lang/Object
    //   784: dup
    //   785: iconst_0
    //   786: aload 24
    //   788: getfield 362	org/telegram/tgnet/TLRPC$Message:id	I
    //   791: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   794: aastore
    //   795: invokevirtual 279	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   798: aload_0
    //   799: aload 24
    //   801: getfield 362	org/telegram/tgnet/TLRPC$Message:id	I
    //   804: invokevirtual 283	org/telegram/messenger/SendMessagesHelper:processSentMessage	(I)V
    //   807: return
    //   808: iconst_0
    //   809: istore 19
    //   811: aload 24
    //   813: astore 34
    //   815: goto -586 -> 229
    //   818: aload 26
    //   820: astore 24
    //   822: aload 13
    //   824: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   827: ifne +140 -> 967
    //   830: aload 26
    //   832: astore 24
    //   834: aload 26
    //   836: getfield 376	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   839: astore 27
    //   841: iconst_0
    //   842: istore 17
    //   844: aload 5
    //   846: astore 30
    //   848: aload_3
    //   849: astore 29
    //   851: aload_2
    //   852: astore 28
    //   854: aload 26
    //   856: astore 25
    //   858: iload 17
    //   860: istore 18
    //   862: aload 27
    //   864: astore 36
    //   866: aload 28
    //   868: astore_2
    //   869: aload 29
    //   871: astore_3
    //   872: aload 30
    //   874: astore 35
    //   876: aload 6
    //   878: astore 33
    //   880: aload 11
    //   882: astore 37
    //   884: aload 16
    //   886: ifnull -607 -> 279
    //   889: aload 26
    //   891: astore 25
    //   893: iload 17
    //   895: istore 18
    //   897: aload 27
    //   899: astore 36
    //   901: aload 28
    //   903: astore_2
    //   904: aload 29
    //   906: astore_3
    //   907: aload 30
    //   909: astore 35
    //   911: aload 6
    //   913: astore 33
    //   915: aload 11
    //   917: astore 37
    //   919: aload 26
    //   921: astore 24
    //   923: aload 16
    //   925: ldc_w 378
    //   928: invokevirtual 226	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   931: ifeq -652 -> 279
    //   934: bipush 9
    //   936: istore 18
    //   938: aload 26
    //   940: astore 25
    //   942: aload 27
    //   944: astore 36
    //   946: aload 28
    //   948: astore_2
    //   949: aload 29
    //   951: astore_3
    //   952: aload 30
    //   954: astore 35
    //   956: aload 6
    //   958: astore 33
    //   960: aload 11
    //   962: astore 37
    //   964: goto -685 -> 279
    //   967: aload 26
    //   969: astore 24
    //   971: aload 13
    //   973: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   976: iconst_4
    //   977: if_icmpne +30 -> 1007
    //   980: aload 26
    //   982: astore 24
    //   984: aload 26
    //   986: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   989: astore 28
    //   991: iconst_1
    //   992: istore 17
    //   994: aload_1
    //   995: astore 27
    //   997: aload_3
    //   998: astore 29
    //   1000: aload 5
    //   1002: astore 30
    //   1004: goto -150 -> 854
    //   1007: aload 26
    //   1009: astore 24
    //   1011: aload 13
    //   1013: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   1016: iconst_1
    //   1017: if_icmpne +36 -> 1053
    //   1020: aload 26
    //   1022: astore 24
    //   1024: aload 26
    //   1026: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1029: getfield 388	org/telegram/tgnet/TLRPC$MessageMedia:photo	Lorg/telegram/tgnet/TLRPC$Photo;
    //   1032: checkcast 390	org/telegram/tgnet/TLRPC$TL_photo
    //   1035: astore 29
    //   1037: iconst_2
    //   1038: istore 17
    //   1040: aload_1
    //   1041: astore 27
    //   1043: aload_2
    //   1044: astore 28
    //   1046: aload 5
    //   1048: astore 30
    //   1050: goto -196 -> 854
    //   1053: aload 26
    //   1055: astore 24
    //   1057: aload 13
    //   1059: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   1062: iconst_3
    //   1063: if_icmpne +39 -> 1102
    //   1066: iconst_3
    //   1067: istore 17
    //   1069: aload 26
    //   1071: astore 24
    //   1073: aload 26
    //   1075: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1078: getfield 394	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   1081: checkcast 396	org/telegram/tgnet/TLRPC$TL_document
    //   1084: astore 6
    //   1086: aload_1
    //   1087: astore 27
    //   1089: aload_2
    //   1090: astore 28
    //   1092: aload_3
    //   1093: astore 29
    //   1095: aload 5
    //   1097: astore 30
    //   1099: goto -245 -> 854
    //   1102: aload 26
    //   1104: astore 24
    //   1106: aload 13
    //   1108: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   1111: bipush 12
    //   1113: if_icmpne +84 -> 1197
    //   1116: aload 26
    //   1118: astore 24
    //   1120: new 398	org/telegram/tgnet/TLRPC$TL_userRequest_old2
    //   1123: dup
    //   1124: invokespecial 399	org/telegram/tgnet/TLRPC$TL_userRequest_old2:<init>	()V
    //   1127: astore 30
    //   1129: aload 30
    //   1131: aload 26
    //   1133: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1136: getfield 402	org/telegram/tgnet/TLRPC$MessageMedia:phone_number	Ljava/lang/String;
    //   1139: putfield 407	org/telegram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   1142: aload 30
    //   1144: aload 26
    //   1146: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1149: getfield 410	org/telegram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   1152: putfield 411	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   1155: aload 30
    //   1157: aload 26
    //   1159: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1162: getfield 414	org/telegram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   1165: putfield 415	org/telegram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   1168: aload 30
    //   1170: aload 26
    //   1172: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1175: getfield 418	org/telegram/tgnet/TLRPC$MessageMedia:user_id	I
    //   1178: putfield 419	org/telegram/tgnet/TLRPC$User:id	I
    //   1181: bipush 6
    //   1183: istore 17
    //   1185: aload_1
    //   1186: astore 27
    //   1188: aload_2
    //   1189: astore 28
    //   1191: aload_3
    //   1192: astore 29
    //   1194: goto -340 -> 854
    //   1197: aload 26
    //   1199: astore 24
    //   1201: aload 13
    //   1203: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   1206: bipush 8
    //   1208: if_icmpeq +45 -> 1253
    //   1211: aload 26
    //   1213: astore 24
    //   1215: aload 13
    //   1217: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   1220: bipush 9
    //   1222: if_icmpeq +31 -> 1253
    //   1225: aload 26
    //   1227: astore 24
    //   1229: aload 13
    //   1231: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   1234: bipush 13
    //   1236: if_icmpeq +17 -> 1253
    //   1239: aload 26
    //   1241: astore 24
    //   1243: aload 13
    //   1245: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   1248: bipush 14
    //   1250: if_icmpne +40 -> 1290
    //   1253: aload 26
    //   1255: astore 24
    //   1257: aload 26
    //   1259: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1262: getfield 394	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   1265: checkcast 396	org/telegram/tgnet/TLRPC$TL_document
    //   1268: astore 6
    //   1270: bipush 7
    //   1272: istore 17
    //   1274: aload_1
    //   1275: astore 27
    //   1277: aload_2
    //   1278: astore 28
    //   1280: aload_3
    //   1281: astore 29
    //   1283: aload 5
    //   1285: astore 30
    //   1287: goto -433 -> 854
    //   1290: aload 26
    //   1292: astore 24
    //   1294: aload_1
    //   1295: astore 27
    //   1297: aload_2
    //   1298: astore 28
    //   1300: aload_3
    //   1301: astore 29
    //   1303: aload 5
    //   1305: astore 30
    //   1307: aload 13
    //   1309: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   1312: iconst_2
    //   1313: if_icmpne -459 -> 854
    //   1316: aload 26
    //   1318: astore 24
    //   1320: aload 26
    //   1322: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1325: getfield 394	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   1328: checkcast 396	org/telegram/tgnet/TLRPC$TL_document
    //   1331: astore 6
    //   1333: bipush 8
    //   1335: istore 17
    //   1337: aload_1
    //   1338: astore 27
    //   1340: aload_2
    //   1341: astore 28
    //   1343: aload_3
    //   1344: astore 29
    //   1346: aload 5
    //   1348: astore 30
    //   1350: goto -496 -> 854
    //   1353: aload_1
    //   1354: ifnull +358 -> 1712
    //   1357: aload 34
    //   1359: ifnull +304 -> 1663
    //   1362: aload 26
    //   1364: astore 24
    //   1366: aload 34
    //   1368: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   1371: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   1374: bipush 17
    //   1376: if_icmplt +287 -> 1663
    //   1379: aload 26
    //   1381: astore 24
    //   1383: new 432	org/telegram/tgnet/TLRPC$TL_message_secret
    //   1386: dup
    //   1387: invokespecial 433	org/telegram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   1390: astore 25
    //   1392: aload 14
    //   1394: ifnull +26 -> 1420
    //   1397: aload 25
    //   1399: astore 24
    //   1401: aload 14
    //   1403: invokevirtual 438	java/util/ArrayList:isEmpty	()Z
    //   1406: ifne +14 -> 1420
    //   1409: aload 25
    //   1411: astore 24
    //   1413: aload 25
    //   1415: aload 14
    //   1417: putfield 442	org/telegram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   1420: aload 11
    //   1422: astore 26
    //   1424: aload 34
    //   1426: ifnull +58 -> 1484
    //   1429: aload 25
    //   1431: astore 24
    //   1433: aload 11
    //   1435: astore 26
    //   1437: aload 11
    //   1439: instanceof 444
    //   1442: ifeq +42 -> 1484
    //   1445: aload 25
    //   1447: astore 24
    //   1449: aload 11
    //   1451: getfield 449	org/telegram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   1454: ifnull +7371 -> 8825
    //   1457: aload 25
    //   1459: astore 24
    //   1461: new 451	org/telegram/tgnet/TLRPC$TL_webPageUrlPending
    //   1464: dup
    //   1465: invokespecial 452	org/telegram/tgnet/TLRPC$TL_webPageUrlPending:<init>	()V
    //   1468: astore 26
    //   1470: aload 25
    //   1472: astore 24
    //   1474: aload 26
    //   1476: aload 11
    //   1478: getfield 449	org/telegram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   1481: putfield 449	org/telegram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   1484: aload 26
    //   1486: ifnonnull +193 -> 1679
    //   1489: aload 25
    //   1491: astore 24
    //   1493: aload 25
    //   1495: new 454	org/telegram/tgnet/TLRPC$TL_messageMediaEmpty
    //   1498: dup
    //   1499: invokespecial 455	org/telegram/tgnet/TLRPC$TL_messageMediaEmpty:<init>	()V
    //   1502: putfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1505: aload 16
    //   1507: ifnull +7324 -> 8831
    //   1510: aload 25
    //   1512: astore 24
    //   1514: aload 16
    //   1516: ldc_w 378
    //   1519: invokevirtual 226	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   1522: ifeq +7309 -> 8831
    //   1525: bipush 9
    //   1527: istore 17
    //   1529: aload 25
    //   1531: astore 24
    //   1533: aload 25
    //   1535: aload_1
    //   1536: putfield 376	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   1539: aload 26
    //   1541: astore 27
    //   1543: aload 25
    //   1545: astore 24
    //   1547: aload 25
    //   1549: getfield 458	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   1552: ifnonnull +15 -> 1567
    //   1555: aload 25
    //   1557: astore 24
    //   1559: aload 25
    //   1561: ldc_w 322
    //   1564: putfield 458	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   1567: aload 25
    //   1569: astore 24
    //   1571: invokestatic 463	org/telegram/messenger/UserConfig:getNewMessageId	()I
    //   1574: istore 18
    //   1576: aload 25
    //   1578: astore 24
    //   1580: aload 25
    //   1582: iload 18
    //   1584: putfield 362	org/telegram/tgnet/TLRPC$Message:id	I
    //   1587: aload 25
    //   1589: astore 24
    //   1591: aload 25
    //   1593: iload 18
    //   1595: putfield 466	org/telegram/tgnet/TLRPC$Message:local_id	I
    //   1598: aload 25
    //   1600: astore 24
    //   1602: aload 25
    //   1604: iconst_1
    //   1605: putfield 469	org/telegram/tgnet/TLRPC$Message:out	Z
    //   1608: iload 19
    //   1610: ifeq +1349 -> 2959
    //   1613: aload 32
    //   1615: ifnull +1344 -> 2959
    //   1618: aload 25
    //   1620: astore 24
    //   1622: aload 25
    //   1624: aload 32
    //   1626: getfield 290	org/telegram/tgnet/TLRPC$InputPeer:channel_id	I
    //   1629: ineg
    //   1630: putfield 472	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   1633: aload 25
    //   1635: astore 24
    //   1637: iconst_0
    //   1638: invokestatic 476	org/telegram/messenger/UserConfig:saveConfig	(Z)V
    //   1641: iload 17
    //   1643: istore 18
    //   1645: aload_1
    //   1646: astore 36
    //   1648: aload 5
    //   1650: astore 35
    //   1652: aload 6
    //   1654: astore 33
    //   1656: aload 27
    //   1658: astore 37
    //   1660: goto -1381 -> 279
    //   1663: aload 26
    //   1665: astore 24
    //   1667: new 478	org/telegram/tgnet/TLRPC$TL_message
    //   1670: dup
    //   1671: invokespecial 479	org/telegram/tgnet/TLRPC$TL_message:<init>	()V
    //   1674: astore 25
    //   1676: goto -284 -> 1392
    //   1679: aload 25
    //   1681: astore 24
    //   1683: aload 25
    //   1685: new 481	org/telegram/tgnet/TLRPC$TL_messageMediaWebPage
    //   1688: dup
    //   1689: invokespecial 482	org/telegram/tgnet/TLRPC$TL_messageMediaWebPage:<init>	()V
    //   1692: putfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1695: aload 25
    //   1697: astore 24
    //   1699: aload 25
    //   1701: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1704: aload 26
    //   1706: putfield 486	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   1709: goto -204 -> 1505
    //   1712: aload_2
    //   1713: ifnull +107 -> 1820
    //   1716: aload 34
    //   1718: ifnull +86 -> 1804
    //   1721: aload 26
    //   1723: astore 24
    //   1725: aload 34
    //   1727: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   1730: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   1733: bipush 17
    //   1735: if_icmplt +69 -> 1804
    //   1738: aload 26
    //   1740: astore 24
    //   1742: new 432	org/telegram/tgnet/TLRPC$TL_message_secret
    //   1745: dup
    //   1746: invokespecial 433	org/telegram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   1749: astore 25
    //   1751: aload 25
    //   1753: astore 24
    //   1755: aload 25
    //   1757: aload_2
    //   1758: putfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1761: aload 25
    //   1763: astore 24
    //   1765: aload 25
    //   1767: ldc_w 322
    //   1770: putfield 376	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   1773: aload 16
    //   1775: ifnull +7062 -> 8837
    //   1778: aload 25
    //   1780: astore 24
    //   1782: aload 16
    //   1784: ldc_w 378
    //   1787: invokevirtual 226	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   1790: ifeq +7047 -> 8837
    //   1793: bipush 9
    //   1795: istore 17
    //   1797: aload 11
    //   1799: astore 27
    //   1801: goto -258 -> 1543
    //   1804: aload 26
    //   1806: astore 24
    //   1808: new 478	org/telegram/tgnet/TLRPC$TL_message
    //   1811: dup
    //   1812: invokespecial 479	org/telegram/tgnet/TLRPC$TL_message:<init>	()V
    //   1815: astore 25
    //   1817: goto -66 -> 1751
    //   1820: aload_3
    //   1821: ifnull +257 -> 2078
    //   1824: aload 34
    //   1826: ifnull +191 -> 2017
    //   1829: aload 26
    //   1831: astore 24
    //   1833: aload 34
    //   1835: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   1838: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   1841: bipush 17
    //   1843: if_icmplt +174 -> 2017
    //   1846: aload 26
    //   1848: astore 24
    //   1850: new 432	org/telegram/tgnet/TLRPC$TL_message_secret
    //   1853: dup
    //   1854: invokespecial 433	org/telegram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   1857: astore 25
    //   1859: aload 25
    //   1861: astore 24
    //   1863: aload 25
    //   1865: new 488	org/telegram/tgnet/TLRPC$TL_messageMediaPhoto
    //   1868: dup
    //   1869: invokespecial 489	org/telegram/tgnet/TLRPC$TL_messageMediaPhoto:<init>	()V
    //   1872: putfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1875: aload 25
    //   1877: astore 24
    //   1879: aload 25
    //   1881: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1884: astore 27
    //   1886: aload 25
    //   1888: astore 24
    //   1890: aload_3
    //   1891: getfield 951	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   1894: ifnull +6953 -> 8847
    //   1897: aload 25
    //   1899: astore 24
    //   1901: aload_3
    //   1902: getfield 951	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   1905: astore 26
    //   1907: aload 25
    //   1909: astore 24
    //   1911: aload 27
    //   1913: aload 26
    //   1915: putfield 498	org/telegram/tgnet/TLRPC$MessageMedia:caption	Ljava/lang/String;
    //   1918: aload 25
    //   1920: astore 24
    //   1922: aload 25
    //   1924: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1927: aload_3
    //   1928: putfield 388	org/telegram/tgnet/TLRPC$MessageMedia:photo	Lorg/telegram/tgnet/TLRPC$Photo;
    //   1931: aload 16
    //   1933: ifnull +6922 -> 8855
    //   1936: aload 25
    //   1938: astore 24
    //   1940: aload 16
    //   1942: ldc_w 378
    //   1945: invokevirtual 226	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   1948: ifeq +6907 -> 8855
    //   1951: bipush 9
    //   1953: istore 17
    //   1955: aload 25
    //   1957: astore 24
    //   1959: aload 25
    //   1961: ldc_w 500
    //   1964: putfield 376	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   1967: aload 9
    //   1969: ifnull +64 -> 2033
    //   1972: aload 25
    //   1974: astore 24
    //   1976: aload 9
    //   1978: invokevirtual 205	java/lang/String:length	()I
    //   1981: ifle +52 -> 2033
    //   1984: aload 25
    //   1986: astore 24
    //   1988: aload 9
    //   1990: ldc_w 502
    //   1993: invokevirtual 506	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   1996: ifeq +37 -> 2033
    //   1999: aload 25
    //   2001: astore 24
    //   2003: aload 25
    //   2005: aload 9
    //   2007: putfield 458	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   2010: aload 11
    //   2012: astore 27
    //   2014: goto -471 -> 1543
    //   2017: aload 26
    //   2019: astore 24
    //   2021: new 478	org/telegram/tgnet/TLRPC$TL_message
    //   2024: dup
    //   2025: invokespecial 479	org/telegram/tgnet/TLRPC$TL_message:<init>	()V
    //   2028: astore 25
    //   2030: goto -171 -> 1859
    //   2033: aload 25
    //   2035: astore 24
    //   2037: aload 25
    //   2039: aload_3
    //   2040: getfield 509	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   2043: aload_3
    //   2044: getfield 509	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   2047: invokevirtual 512	java/util/ArrayList:size	()I
    //   2050: iconst_1
    //   2051: isub
    //   2052: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   2055: checkcast 517	org/telegram/tgnet/TLRPC$PhotoSize
    //   2058: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2061: iconst_1
    //   2062: invokestatic 527	org/telegram/messenger/FileLoader:getPathToAttach	(Lorg/telegram/tgnet/TLObject;Z)Ljava/io/File;
    //   2065: invokevirtual 530	java/io/File:toString	()Ljava/lang/String;
    //   2068: putfield 458	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   2071: aload 11
    //   2073: astore 27
    //   2075: goto -532 -> 1543
    //   2078: aload 5
    //   2080: ifnull +265 -> 2345
    //   2083: aload 34
    //   2085: ifnull +244 -> 2329
    //   2088: aload 26
    //   2090: astore 24
    //   2092: aload 34
    //   2094: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   2097: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   2100: bipush 17
    //   2102: if_icmplt +227 -> 2329
    //   2105: aload 26
    //   2107: astore 24
    //   2109: new 432	org/telegram/tgnet/TLRPC$TL_message_secret
    //   2112: dup
    //   2113: invokespecial 433	org/telegram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   2116: astore 25
    //   2118: aload 25
    //   2120: astore 24
    //   2122: aload 25
    //   2124: new 532	org/telegram/tgnet/TLRPC$TL_messageMediaContact
    //   2127: dup
    //   2128: invokespecial 533	org/telegram/tgnet/TLRPC$TL_messageMediaContact:<init>	()V
    //   2131: putfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2134: aload 25
    //   2136: astore 24
    //   2138: aload 25
    //   2140: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2143: aload 5
    //   2145: getfield 407	org/telegram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   2148: putfield 402	org/telegram/tgnet/TLRPC$MessageMedia:phone_number	Ljava/lang/String;
    //   2151: aload 25
    //   2153: astore 24
    //   2155: aload 25
    //   2157: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2160: aload 5
    //   2162: getfield 411	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   2165: putfield 410	org/telegram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   2168: aload 25
    //   2170: astore 24
    //   2172: aload 25
    //   2174: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2177: aload 5
    //   2179: getfield 415	org/telegram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   2182: putfield 414	org/telegram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   2185: aload 25
    //   2187: astore 24
    //   2189: aload 25
    //   2191: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2194: aload 5
    //   2196: getfield 419	org/telegram/tgnet/TLRPC$User:id	I
    //   2199: putfield 418	org/telegram/tgnet/TLRPC$MessageMedia:user_id	I
    //   2202: aload 25
    //   2204: astore 24
    //   2206: aload 25
    //   2208: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2211: getfield 410	org/telegram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   2214: ifnonnull +30 -> 2244
    //   2217: aload 25
    //   2219: astore 24
    //   2221: aload 25
    //   2223: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2226: ldc_w 322
    //   2229: putfield 410	org/telegram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   2232: aload 25
    //   2234: astore 24
    //   2236: aload 5
    //   2238: ldc_w 322
    //   2241: putfield 411	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   2244: aload 25
    //   2246: astore 24
    //   2248: aload 25
    //   2250: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2253: getfield 414	org/telegram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   2256: ifnonnull +30 -> 2286
    //   2259: aload 25
    //   2261: astore 24
    //   2263: aload 25
    //   2265: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2268: ldc_w 322
    //   2271: putfield 414	org/telegram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   2274: aload 25
    //   2276: astore 24
    //   2278: aload 5
    //   2280: ldc_w 322
    //   2283: putfield 415	org/telegram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   2286: aload 25
    //   2288: astore 24
    //   2290: aload 25
    //   2292: ldc_w 322
    //   2295: putfield 376	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   2298: aload 16
    //   2300: ifnull +6561 -> 8861
    //   2303: aload 25
    //   2305: astore 24
    //   2307: aload 16
    //   2309: ldc_w 378
    //   2312: invokevirtual 226	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   2315: ifeq +6546 -> 8861
    //   2318: bipush 9
    //   2320: istore 17
    //   2322: aload 11
    //   2324: astore 27
    //   2326: goto -783 -> 1543
    //   2329: aload 26
    //   2331: astore 24
    //   2333: new 478	org/telegram/tgnet/TLRPC$TL_message
    //   2336: dup
    //   2337: invokespecial 479	org/telegram/tgnet/TLRPC$TL_message:<init>	()V
    //   2340: astore 25
    //   2342: goto -224 -> 2118
    //   2345: aload 11
    //   2347: astore 27
    //   2349: aload 6
    //   2351: ifnull -808 -> 1543
    //   2354: aload 34
    //   2356: ifnull +366 -> 2722
    //   2359: aload 26
    //   2361: astore 24
    //   2363: aload 34
    //   2365: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   2368: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   2371: bipush 17
    //   2373: if_icmplt +349 -> 2722
    //   2376: aload 26
    //   2378: astore 24
    //   2380: new 432	org/telegram/tgnet/TLRPC$TL_message_secret
    //   2383: dup
    //   2384: invokespecial 433	org/telegram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   2387: astore 26
    //   2389: aload 26
    //   2391: astore 24
    //   2393: aload 26
    //   2395: new 535	org/telegram/tgnet/TLRPC$TL_messageMediaDocument
    //   2398: dup
    //   2399: invokespecial 536	org/telegram/tgnet/TLRPC$TL_messageMediaDocument:<init>	()V
    //   2402: putfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2405: aload 26
    //   2407: astore 24
    //   2409: aload 26
    //   2411: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2414: astore 27
    //   2416: aload 26
    //   2418: astore 24
    //   2420: aload 6
    //   2422: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   2425: ifnull +6447 -> 8872
    //   2428: aload 26
    //   2430: astore 24
    //   2432: aload 6
    //   2434: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   2437: astore 25
    //   2439: aload 26
    //   2441: astore 24
    //   2443: aload 27
    //   2445: aload 25
    //   2447: putfield 498	org/telegram/tgnet/TLRPC$MessageMedia:caption	Ljava/lang/String;
    //   2450: aload 26
    //   2452: astore 24
    //   2454: aload 26
    //   2456: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2459: aload 6
    //   2461: putfield 394	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   2464: aload 16
    //   2466: ifnull +272 -> 2738
    //   2469: aload 26
    //   2471: astore 24
    //   2473: aload 16
    //   2475: ldc_w 378
    //   2478: invokevirtual 226	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   2481: ifeq +257 -> 2738
    //   2484: bipush 9
    //   2486: istore 18
    //   2488: aload 4
    //   2490: ifnonnull +285 -> 2775
    //   2493: aload 26
    //   2495: astore 24
    //   2497: aload 26
    //   2499: ldc_w 500
    //   2502: putfield 376	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   2505: aload 34
    //   2507: ifnull +285 -> 2792
    //   2510: aload 26
    //   2512: astore 24
    //   2514: aload 6
    //   2516: getfield 539	org/telegram/tgnet/TLRPC$TL_document:dc_id	I
    //   2519: ifle +273 -> 2792
    //   2522: aload 26
    //   2524: astore 24
    //   2526: aload 6
    //   2528: invokestatic 543	org/telegram/messenger/MessageObject:isStickerDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   2531: ifne +261 -> 2792
    //   2534: aload 26
    //   2536: astore 24
    //   2538: aload 26
    //   2540: aload 6
    //   2542: invokestatic 546	org/telegram/messenger/FileLoader:getPathToAttach	(Lorg/telegram/tgnet/TLObject;)Ljava/io/File;
    //   2545: invokevirtual 530	java/io/File:toString	()Ljava/lang/String;
    //   2548: putfield 458	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   2551: aload 26
    //   2553: astore 25
    //   2555: iload 18
    //   2557: istore 17
    //   2559: aload 11
    //   2561: astore 27
    //   2563: aload 34
    //   2565: ifnull -1022 -> 1543
    //   2568: aload 26
    //   2570: astore 24
    //   2572: aload 26
    //   2574: astore 25
    //   2576: iload 18
    //   2578: istore 17
    //   2580: aload 11
    //   2582: astore 27
    //   2584: aload 6
    //   2586: invokestatic 543	org/telegram/messenger/MessageObject:isStickerDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   2589: ifeq -1046 -> 1543
    //   2592: iconst_0
    //   2593: istore 20
    //   2595: aload 26
    //   2597: astore 24
    //   2599: aload 26
    //   2601: astore 25
    //   2603: iload 18
    //   2605: istore 17
    //   2607: aload 11
    //   2609: astore 27
    //   2611: iload 20
    //   2613: aload 6
    //   2615: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   2618: invokevirtual 512	java/util/ArrayList:size	()I
    //   2621: if_icmpge -1078 -> 1543
    //   2624: aload 26
    //   2626: astore 24
    //   2628: aload 6
    //   2630: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   2633: iload 20
    //   2635: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   2638: checkcast 551	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   2641: astore 25
    //   2643: aload 26
    //   2645: astore 24
    //   2647: aload 25
    //   2649: instanceof 553
    //   2652: ifeq +6235 -> 8887
    //   2655: aload 26
    //   2657: astore 24
    //   2659: aload 34
    //   2661: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   2664: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   2667: bipush 46
    //   2669: if_icmpge +137 -> 2806
    //   2672: aload 26
    //   2674: astore 24
    //   2676: aload 6
    //   2678: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   2681: iload 20
    //   2683: invokevirtual 556	java/util/ArrayList:remove	(I)Ljava/lang/Object;
    //   2686: pop
    //   2687: aload 26
    //   2689: astore 24
    //   2691: aload 6
    //   2693: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   2696: new 558	org/telegram/tgnet/TLRPC$TL_documentAttributeSticker_old
    //   2699: dup
    //   2700: invokespecial 559	org/telegram/tgnet/TLRPC$TL_documentAttributeSticker_old:<init>	()V
    //   2703: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   2706: pop
    //   2707: aload 26
    //   2709: astore 25
    //   2711: iload 18
    //   2713: istore 17
    //   2715: aload 11
    //   2717: astore 27
    //   2719: goto -1176 -> 1543
    //   2722: aload 26
    //   2724: astore 24
    //   2726: new 478	org/telegram/tgnet/TLRPC$TL_message
    //   2729: dup
    //   2730: invokespecial 479	org/telegram/tgnet/TLRPC$TL_message:<init>	()V
    //   2733: astore 26
    //   2735: goto -346 -> 2389
    //   2738: aload 26
    //   2740: astore 24
    //   2742: aload 6
    //   2744: invokestatic 565	org/telegram/messenger/MessageObject:isVideoDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   2747: ifeq +9 -> 2756
    //   2750: iconst_3
    //   2751: istore 18
    //   2753: goto -265 -> 2488
    //   2756: aload 26
    //   2758: astore 24
    //   2760: aload 6
    //   2762: invokestatic 568	org/telegram/messenger/MessageObject:isVoiceDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   2765: ifeq +6115 -> 8880
    //   2768: bipush 8
    //   2770: istore 18
    //   2772: goto -284 -> 2488
    //   2775: aload 26
    //   2777: astore 24
    //   2779: aload 26
    //   2781: aload 4
    //   2783: invokevirtual 573	org/telegram/messenger/VideoEditedInfo:getString	()Ljava/lang/String;
    //   2786: putfield 376	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   2789: goto -284 -> 2505
    //   2792: aload 26
    //   2794: astore 24
    //   2796: aload 26
    //   2798: aload 9
    //   2800: putfield 458	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   2803: goto -252 -> 2551
    //   2806: aload 26
    //   2808: astore 24
    //   2810: aload 25
    //   2812: getfield 577	org/telegram/tgnet/TLRPC$DocumentAttribute:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   2815: ifnull +113 -> 2928
    //   2818: aload 26
    //   2820: astore 24
    //   2822: aload 25
    //   2824: getfield 577	org/telegram/tgnet/TLRPC$DocumentAttribute:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   2827: getfield 581	org/telegram/tgnet/TLRPC$InputStickerSet:id	J
    //   2830: invokestatic 587	org/telegram/messenger/query/StickersQuery:getStickerSetName	(J)Ljava/lang/String;
    //   2833: astore 27
    //   2835: aload 27
    //   2837: ifnull +60 -> 2897
    //   2840: aload 26
    //   2842: astore 24
    //   2844: aload 27
    //   2846: invokevirtual 205	java/lang/String:length	()I
    //   2849: ifle +48 -> 2897
    //   2852: aload 26
    //   2854: astore 24
    //   2856: aload 25
    //   2858: new 589	org/telegram/tgnet/TLRPC$TL_inputStickerSetShortName
    //   2861: dup
    //   2862: invokespecial 590	org/telegram/tgnet/TLRPC$TL_inputStickerSetShortName:<init>	()V
    //   2865: putfield 577	org/telegram/tgnet/TLRPC$DocumentAttribute:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   2868: aload 26
    //   2870: astore 24
    //   2872: aload 25
    //   2874: getfield 577	org/telegram/tgnet/TLRPC$DocumentAttribute:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   2877: aload 27
    //   2879: putfield 593	org/telegram/tgnet/TLRPC$InputStickerSet:short_name	Ljava/lang/String;
    //   2882: aload 26
    //   2884: astore 25
    //   2886: iload 18
    //   2888: istore 17
    //   2890: aload 11
    //   2892: astore 27
    //   2894: goto -1351 -> 1543
    //   2897: aload 26
    //   2899: astore 24
    //   2901: aload 25
    //   2903: new 595	org/telegram/tgnet/TLRPC$TL_inputStickerSetEmpty
    //   2906: dup
    //   2907: invokespecial 596	org/telegram/tgnet/TLRPC$TL_inputStickerSetEmpty:<init>	()V
    //   2910: putfield 577	org/telegram/tgnet/TLRPC$DocumentAttribute:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   2913: aload 26
    //   2915: astore 25
    //   2917: iload 18
    //   2919: istore 17
    //   2921: aload 11
    //   2923: astore 27
    //   2925: goto -1382 -> 1543
    //   2928: aload 26
    //   2930: astore 24
    //   2932: aload 25
    //   2934: new 595	org/telegram/tgnet/TLRPC$TL_inputStickerSetEmpty
    //   2937: dup
    //   2938: invokespecial 596	org/telegram/tgnet/TLRPC$TL_inputStickerSetEmpty:<init>	()V
    //   2941: putfield 577	org/telegram/tgnet/TLRPC$DocumentAttribute:stickerset	Lorg/telegram/tgnet/TLRPC$InputStickerSet;
    //   2944: aload 26
    //   2946: astore 25
    //   2948: iload 18
    //   2950: istore 17
    //   2952: aload 11
    //   2954: astore 27
    //   2956: goto -1413 -> 1543
    //   2959: aload 25
    //   2961: astore 24
    //   2963: aload 25
    //   2965: invokestatic 599	org/telegram/messenger/UserConfig:getClientUserId	()I
    //   2968: putfield 472	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   2971: aload 25
    //   2973: astore 24
    //   2975: aload 25
    //   2977: aload 25
    //   2979: getfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   2982: sipush 256
    //   2985: ior
    //   2986: putfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   2989: goto -1356 -> 1633
    //   2992: aload 25
    //   2994: astore 24
    //   2996: aload 25
    //   2998: aload 16
    //   3000: ldc_w 314
    //   3003: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   3006: checkcast 201	java/lang/String
    //   3009: invokestatic 605	org/telegram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
    //   3012: invokevirtual 608	java/lang/Integer:intValue	()I
    //   3015: putfield 611	org/telegram/tgnet/TLRPC$Message:via_bot_id	I
    //   3018: goto -2643 -> 375
    //   3021: aload 25
    //   3023: astore 24
    //   3025: aload 25
    //   3027: iconst_1
    //   3028: putfield 614	org/telegram/tgnet/TLRPC$Message:post	Z
    //   3031: aload 25
    //   3033: astore 24
    //   3035: aload_1
    //   3036: getfield 617	org/telegram/tgnet/TLRPC$Chat:signatures	Z
    //   3039: ifeq -2495 -> 544
    //   3042: aload 25
    //   3044: astore 24
    //   3046: aload 25
    //   3048: invokestatic 599	org/telegram/messenger/UserConfig:getClientUserId	()I
    //   3051: putfield 472	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   3054: goto -2510 -> 544
    //   3057: aload 25
    //   3059: astore 24
    //   3061: aload 25
    //   3063: iconst_1
    //   3064: putfield 346	org/telegram/tgnet/TLRPC$Message:unread	Z
    //   3067: goto -2523 -> 544
    //   3070: aload 25
    //   3072: astore 24
    //   3074: aload 25
    //   3076: aload 25
    //   3078: getfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   3081: bipush 8
    //   3083: ior
    //   3084: putfield 325	org/telegram/tgnet/TLRPC$Message:flags	I
    //   3087: goto -2471 -> 616
    //   3090: aload 25
    //   3092: astore 24
    //   3094: new 435	java/util/ArrayList
    //   3097: dup
    //   3098: invokespecial 618	java/util/ArrayList:<init>	()V
    //   3101: astore 5
    //   3103: aload_0
    //   3104: getfield 138	org/telegram/messenger/SendMessagesHelper:currentChatInfo	Lorg/telegram/tgnet/TLRPC$ChatFull;
    //   3107: getfield 624	org/telegram/tgnet/TLRPC$ChatFull:participants	Lorg/telegram/tgnet/TLRPC$ChatParticipants;
    //   3110: getfield 628	org/telegram/tgnet/TLRPC$ChatParticipants:participants	Ljava/util/ArrayList;
    //   3113: invokevirtual 632	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   3116: astore_1
    //   3117: aload_1
    //   3118: invokeinterface 637 1 0
    //   3123: ifeq +49 -> 3172
    //   3126: aload_1
    //   3127: invokeinterface 641 1 0
    //   3132: checkcast 643	org/telegram/tgnet/TLRPC$ChatParticipant
    //   3135: astore 6
    //   3137: invokestatic 239	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   3140: aload 6
    //   3142: getfield 644	org/telegram/tgnet/TLRPC$ChatParticipant:user_id	I
    //   3145: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3148: invokevirtual 648	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   3151: invokestatic 652	org/telegram/messenger/MessagesController:getInputUser	(Lorg/telegram/tgnet/TLRPC$User;)Lorg/telegram/tgnet/TLRPC$InputUser;
    //   3154: astore 6
    //   3156: aload 6
    //   3158: ifnull -41 -> 3117
    //   3161: aload 5
    //   3163: aload 6
    //   3165: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   3168: pop
    //   3169: goto -52 -> 3117
    //   3172: aload 25
    //   3174: new 654	org/telegram/tgnet/TLRPC$TL_peerChat
    //   3177: dup
    //   3178: invokespecial 655	org/telegram/tgnet/TLRPC$TL_peerChat:<init>	()V
    //   3181: putfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3184: aload 25
    //   3186: getfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3189: iload 22
    //   3191: putfield 664	org/telegram/tgnet/TLRPC$Peer:chat_id	I
    //   3194: aload 34
    //   3196: ifnull +20 -> 3216
    //   3199: aload 25
    //   3201: astore 24
    //   3203: aload 34
    //   3205: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   3208: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   3211: bipush 46
    //   3213: if_icmplt +46 -> 3259
    //   3216: iload 21
    //   3218: iconst_1
    //   3219: if_icmpeq +40 -> 3259
    //   3222: aload 25
    //   3224: astore 24
    //   3226: aload 25
    //   3228: invokestatic 668	org/telegram/messenger/MessageObject:isVoiceMessage	(Lorg/telegram/tgnet/TLRPC$Message;)Z
    //   3231: ifeq +28 -> 3259
    //   3234: aload 25
    //   3236: astore 24
    //   3238: aload 25
    //   3240: getfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3243: getfield 669	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   3246: ifne +13 -> 3259
    //   3249: aload 25
    //   3251: astore 24
    //   3253: aload 25
    //   3255: iconst_1
    //   3256: putfield 672	org/telegram/tgnet/TLRPC$Message:media_unread	Z
    //   3259: aload 25
    //   3261: astore 24
    //   3263: aload 25
    //   3265: iconst_1
    //   3266: putfield 269	org/telegram/tgnet/TLRPC$Message:send_state	I
    //   3269: aload 25
    //   3271: astore 24
    //   3273: new 256	org/telegram/messenger/MessageObject
    //   3276: dup
    //   3277: aload 25
    //   3279: aconst_null
    //   3280: iconst_1
    //   3281: invokespecial 675	org/telegram/messenger/MessageObject:<init>	(Lorg/telegram/tgnet/TLRPC$Message;Ljava/util/AbstractMap;Z)V
    //   3284: astore 6
    //   3286: aload 6
    //   3288: aload 10
    //   3290: putfield 679	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   3293: aload 6
    //   3295: invokevirtual 304	org/telegram/messenger/MessageObject:isForwarded	()Z
    //   3298: ifne +18 -> 3316
    //   3301: aload 6
    //   3303: getfield 373	org/telegram/messenger/MessageObject:type	I
    //   3306: iconst_3
    //   3307: if_icmpne +9 -> 3316
    //   3310: aload 6
    //   3312: iconst_1
    //   3313: putfield 682	org/telegram/messenger/MessageObject:attachPathExists	Z
    //   3316: new 435	java/util/ArrayList
    //   3319: dup
    //   3320: invokespecial 618	java/util/ArrayList:<init>	()V
    //   3323: astore_1
    //   3324: aload_1
    //   3325: aload 6
    //   3327: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   3330: pop
    //   3331: new 435	java/util/ArrayList
    //   3334: dup
    //   3335: invokespecial 618	java/util/ArrayList:<init>	()V
    //   3338: astore 11
    //   3340: aload 11
    //   3342: aload 25
    //   3344: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   3347: pop
    //   3348: invokestatic 254	org/telegram/messenger/MessagesStorage:getInstance	()Lorg/telegram/messenger/MessagesStorage;
    //   3351: aload 11
    //   3353: iconst_0
    //   3354: iconst_1
    //   3355: iconst_0
    //   3356: iconst_0
    //   3357: invokevirtual 686	org/telegram/messenger/MessagesStorage:putMessages	(Ljava/util/ArrayList;ZZZI)V
    //   3360: invokestatic 239	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   3363: lload 7
    //   3365: aload_1
    //   3366: invokevirtual 690	org/telegram/messenger/MessagesController:updateInterfaceWithMessages	(JLjava/util/ArrayList;)V
    //   3369: invokestatic 165	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   3372: getstatic 693	org/telegram/messenger/NotificationCenter:dialogsNeedReload	I
    //   3375: iconst_0
    //   3376: anewarray 4	java/lang/Object
    //   3379: invokevirtual 279	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   3382: getstatic 698	org/telegram/messenger/BuildVars:DEBUG_VERSION	Z
    //   3385: ifeq +5521 -> 8906
    //   3388: aload 32
    //   3390: ifnull +5516 -> 8906
    //   3393: ldc_w 364
    //   3396: new 700	java/lang/StringBuilder
    //   3399: dup
    //   3400: invokespecial 701	java/lang/StringBuilder:<init>	()V
    //   3403: ldc_w 703
    //   3406: invokevirtual 707	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3409: aload 32
    //   3411: getfield 708	org/telegram/tgnet/TLRPC$InputPeer:user_id	I
    //   3414: invokevirtual 711	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3417: ldc_w 713
    //   3420: invokevirtual 707	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3423: aload 32
    //   3425: getfield 714	org/telegram/tgnet/TLRPC$InputPeer:chat_id	I
    //   3428: invokevirtual 711	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3431: ldc_w 716
    //   3434: invokevirtual 707	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3437: aload 32
    //   3439: getfield 290	org/telegram/tgnet/TLRPC$InputPeer:channel_id	I
    //   3442: invokevirtual 711	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3445: ldc_w 718
    //   3448: invokevirtual 707	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3451: aload 32
    //   3453: getfield 721	org/telegram/tgnet/TLRPC$InputPeer:access_hash	J
    //   3456: invokevirtual 724	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   3459: invokevirtual 725	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3462: invokestatic 728	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   3465: goto +5441 -> 8906
    //   3468: aload 34
    //   3470: ifnonnull +712 -> 4182
    //   3473: aload 5
    //   3475: ifnull +528 -> 4003
    //   3478: new 730	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast
    //   3481: dup
    //   3482: invokespecial 731	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:<init>	()V
    //   3485: astore_1
    //   3486: new 435	java/util/ArrayList
    //   3489: dup
    //   3490: invokespecial 618	java/util/ArrayList:<init>	()V
    //   3493: astore_2
    //   3494: iconst_0
    //   3495: istore 17
    //   3497: iload 17
    //   3499: aload 5
    //   3501: invokevirtual 512	java/util/ArrayList:size	()I
    //   3504: if_icmpge +462 -> 3966
    //   3507: aload_2
    //   3508: getstatic 735	org/telegram/messenger/Utilities:random	Ljava/security/SecureRandom;
    //   3511: invokevirtual 740	java/security/SecureRandom:nextLong	()J
    //   3514: invokestatic 745	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   3517: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   3520: pop
    //   3521: iload 17
    //   3523: iconst_1
    //   3524: iadd
    //   3525: istore 17
    //   3527: goto -30 -> 3497
    //   3530: aload 25
    //   3532: astore 24
    //   3534: aload 25
    //   3536: iload 22
    //   3538: invokestatic 749	org/telegram/messenger/MessagesController:getPeer	(I)Lorg/telegram/tgnet/TLRPC$Peer;
    //   3541: putfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3544: aload 38
    //   3546: astore 5
    //   3548: iload 22
    //   3550: ifle -356 -> 3194
    //   3553: aload 25
    //   3555: astore 24
    //   3557: invokestatic 239	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   3560: iload 22
    //   3562: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3565: invokevirtual 648	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   3568: astore_1
    //   3569: aload_1
    //   3570: ifnonnull +17 -> 3587
    //   3573: aload 25
    //   3575: astore 24
    //   3577: aload_0
    //   3578: aload 25
    //   3580: getfield 362	org/telegram/tgnet/TLRPC$Message:id	I
    //   3583: invokevirtual 283	org/telegram/messenger/SendMessagesHelper:processSentMessage	(I)V
    //   3586: return
    //   3587: aload 25
    //   3589: astore 24
    //   3591: aload 38
    //   3593: astore 5
    //   3595: aload_1
    //   3596: getfield 751	org/telegram/tgnet/TLRPC$User:bot	Z
    //   3599: ifeq -405 -> 3194
    //   3602: aload 25
    //   3604: astore 24
    //   3606: aload 25
    //   3608: iconst_0
    //   3609: putfield 346	org/telegram/tgnet/TLRPC$Message:unread	Z
    //   3612: aload 38
    //   3614: astore 5
    //   3616: goto -422 -> 3194
    //   3619: aload 25
    //   3621: astore 24
    //   3623: aload 25
    //   3625: new 753	org/telegram/tgnet/TLRPC$TL_peerUser
    //   3628: dup
    //   3629: invokespecial 754	org/telegram/tgnet/TLRPC$TL_peerUser:<init>	()V
    //   3632: putfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3635: aload 25
    //   3637: astore 24
    //   3639: aload 34
    //   3641: getfield 757	org/telegram/tgnet/TLRPC$EncryptedChat:participant_id	I
    //   3644: invokestatic 599	org/telegram/messenger/UserConfig:getClientUserId	()I
    //   3647: if_icmpne +168 -> 3815
    //   3650: aload 25
    //   3652: astore 24
    //   3654: aload 25
    //   3656: getfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3659: aload 34
    //   3661: getfield 760	org/telegram/tgnet/TLRPC$EncryptedChat:admin_id	I
    //   3664: putfield 761	org/telegram/tgnet/TLRPC$Peer:user_id	I
    //   3667: aload 25
    //   3669: astore 24
    //   3671: aload 25
    //   3673: aload 34
    //   3675: getfield 764	org/telegram/tgnet/TLRPC$EncryptedChat:ttl	I
    //   3678: putfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   3681: aload 25
    //   3683: astore 24
    //   3685: aload 38
    //   3687: astore 5
    //   3689: aload 25
    //   3691: getfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   3694: ifeq -500 -> 3194
    //   3697: aload 25
    //   3699: astore 24
    //   3701: aload 25
    //   3703: invokestatic 668	org/telegram/messenger/MessageObject:isVoiceMessage	(Lorg/telegram/tgnet/TLRPC$Message;)Z
    //   3706: ifeq +129 -> 3835
    //   3709: iconst_0
    //   3710: istore 20
    //   3712: iconst_0
    //   3713: istore 17
    //   3715: aload 25
    //   3717: astore 24
    //   3719: iload 20
    //   3721: istore 19
    //   3723: iload 17
    //   3725: aload 25
    //   3727: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3730: getfield 394	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   3733: getfield 768	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3736: invokevirtual 512	java/util/ArrayList:size	()I
    //   3739: if_icmpge +48 -> 3787
    //   3742: aload 25
    //   3744: astore 24
    //   3746: aload 25
    //   3748: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3751: getfield 394	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   3754: getfield 768	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3757: iload 17
    //   3759: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   3762: checkcast 551	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   3765: astore_1
    //   3766: aload 25
    //   3768: astore 24
    //   3770: aload_1
    //   3771: instanceof 770
    //   3774: ifeq +5157 -> 8931
    //   3777: aload 25
    //   3779: astore 24
    //   3781: aload_1
    //   3782: getfield 773	org/telegram/tgnet/TLRPC$DocumentAttribute:duration	I
    //   3785: istore 19
    //   3787: aload 25
    //   3789: astore 24
    //   3791: aload 25
    //   3793: aload 34
    //   3795: getfield 764	org/telegram/tgnet/TLRPC$EncryptedChat:ttl	I
    //   3798: iload 19
    //   3800: iconst_1
    //   3801: iadd
    //   3802: invokestatic 779	java/lang/Math:max	(II)I
    //   3805: putfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   3808: aload 38
    //   3810: astore 5
    //   3812: goto -618 -> 3194
    //   3815: aload 25
    //   3817: astore 24
    //   3819: aload 25
    //   3821: getfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3824: aload 34
    //   3826: getfield 757	org/telegram/tgnet/TLRPC$EncryptedChat:participant_id	I
    //   3829: putfield 761	org/telegram/tgnet/TLRPC$Peer:user_id	I
    //   3832: goto -165 -> 3667
    //   3835: aload 25
    //   3837: astore 24
    //   3839: aload 38
    //   3841: astore 5
    //   3843: aload 25
    //   3845: invokestatic 782	org/telegram/messenger/MessageObject:isVideoMessage	(Lorg/telegram/tgnet/TLRPC$Message;)Z
    //   3848: ifeq -654 -> 3194
    //   3851: iconst_0
    //   3852: istore 20
    //   3854: iconst_0
    //   3855: istore 17
    //   3857: aload 25
    //   3859: astore 24
    //   3861: iload 20
    //   3863: istore 19
    //   3865: iload 17
    //   3867: aload 25
    //   3869: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3872: getfield 394	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   3875: getfield 768	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3878: invokevirtual 512	java/util/ArrayList:size	()I
    //   3881: if_icmpge +48 -> 3929
    //   3884: aload 25
    //   3886: astore 24
    //   3888: aload 25
    //   3890: getfield 382	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3893: getfield 394	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   3896: getfield 768	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3899: iload 17
    //   3901: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   3904: checkcast 551	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   3907: astore_1
    //   3908: aload 25
    //   3910: astore 24
    //   3912: aload_1
    //   3913: instanceof 784
    //   3916: ifeq +41 -> 3957
    //   3919: aload 25
    //   3921: astore 24
    //   3923: aload_1
    //   3924: getfield 773	org/telegram/tgnet/TLRPC$DocumentAttribute:duration	I
    //   3927: istore 19
    //   3929: aload 25
    //   3931: astore 24
    //   3933: aload 25
    //   3935: aload 34
    //   3937: getfield 764	org/telegram/tgnet/TLRPC$EncryptedChat:ttl	I
    //   3940: iload 19
    //   3942: iconst_1
    //   3943: iadd
    //   3944: invokestatic 779	java/lang/Math:max	(II)I
    //   3947: putfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   3950: aload 38
    //   3952: astore 5
    //   3954: goto -760 -> 3194
    //   3957: iload 17
    //   3959: iconst_1
    //   3960: iadd
    //   3961: istore 17
    //   3963: goto -106 -> 3857
    //   3966: aload_1
    //   3967: aload 36
    //   3969: putfield 785	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:message	Ljava/lang/String;
    //   3972: aload_1
    //   3973: aload 5
    //   3975: putfield 788	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:contacts	Ljava/util/ArrayList;
    //   3978: aload_1
    //   3979: new 790	org/telegram/tgnet/TLRPC$TL_inputMediaEmpty
    //   3982: dup
    //   3983: invokespecial 791	org/telegram/tgnet/TLRPC$TL_inputMediaEmpty:<init>	()V
    //   3986: putfield 794	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:media	Lorg/telegram/tgnet/TLRPC$InputMedia;
    //   3989: aload_1
    //   3990: aload_2
    //   3991: putfield 796	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:random_id	Ljava/util/ArrayList;
    //   3994: aload_0
    //   3995: aload_1
    //   3996: aload 6
    //   3998: aconst_null
    //   3999: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   4002: return
    //   4003: new 802	org/telegram/tgnet/TLRPC$TL_messages_sendMessage
    //   4006: dup
    //   4007: invokespecial 803	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:<init>	()V
    //   4010: astore_1
    //   4011: aload_1
    //   4012: aload 36
    //   4014: putfield 804	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:message	Ljava/lang/String;
    //   4017: aload 13
    //   4019: ifnonnull +4932 -> 8951
    //   4022: iconst_1
    //   4023: istore 23
    //   4025: aload_1
    //   4026: iload 23
    //   4028: putfield 807	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:clear_draft	Z
    //   4031: aload 25
    //   4033: getfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   4036: instanceof 809
    //   4039: ifeq +44 -> 4083
    //   4042: aload_1
    //   4043: getstatic 815	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   4046: ldc_w 817
    //   4049: iconst_0
    //   4050: invokevirtual 823	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   4053: new 700	java/lang/StringBuilder
    //   4056: dup
    //   4057: invokespecial 701	java/lang/StringBuilder:<init>	()V
    //   4060: ldc_w 825
    //   4063: invokevirtual 707	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4066: lload 7
    //   4068: invokevirtual 724	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   4071: invokevirtual 725	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4074: iconst_0
    //   4075: invokeinterface 831 3 0
    //   4080: putfield 834	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:silent	Z
    //   4083: aload_1
    //   4084: aload 32
    //   4086: putfield 838	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   4089: aload_1
    //   4090: aload 25
    //   4092: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   4095: putfield 839	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:random_id	J
    //   4098: aload 10
    //   4100: ifnull +22 -> 4122
    //   4103: aload_1
    //   4104: aload_1
    //   4105: getfield 840	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:flags	I
    //   4108: iconst_1
    //   4109: ior
    //   4110: putfield 840	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:flags	I
    //   4113: aload_1
    //   4114: aload 10
    //   4116: invokevirtual 275	org/telegram/messenger/MessageObject:getId	()I
    //   4119: putfield 841	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:reply_to_msg_id	I
    //   4122: iload 12
    //   4124: ifne +8 -> 4132
    //   4127: aload_1
    //   4128: iconst_1
    //   4129: putfield 844	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:no_webpage	Z
    //   4132: aload 14
    //   4134: ifnull +28 -> 4162
    //   4137: aload 14
    //   4139: invokevirtual 438	java/util/ArrayList:isEmpty	()Z
    //   4142: ifne +20 -> 4162
    //   4145: aload_1
    //   4146: aload 14
    //   4148: putfield 845	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:entities	Ljava/util/ArrayList;
    //   4151: aload_1
    //   4152: aload_1
    //   4153: getfield 840	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:flags	I
    //   4156: bipush 8
    //   4158: ior
    //   4159: putfield 840	org/telegram/tgnet/TLRPC$TL_messages_sendMessage:flags	I
    //   4162: aload_0
    //   4163: aload_1
    //   4164: aload 6
    //   4166: aconst_null
    //   4167: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   4170: aload 13
    //   4172: ifnonnull -4165 -> 7
    //   4175: lload 7
    //   4177: iconst_0
    //   4178: invokestatic 851	org/telegram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   4181: return
    //   4182: aload 34
    //   4184: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   4187: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   4190: bipush 46
    //   4192: if_icmplt +234 -> 4426
    //   4195: new 853	org/telegram/tgnet/TLRPC$TL_decryptedMessage
    //   4198: dup
    //   4199: invokespecial 854	org/telegram/tgnet/TLRPC$TL_decryptedMessage:<init>	()V
    //   4202: astore_2
    //   4203: aload_2
    //   4204: aload 25
    //   4206: getfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   4209: putfield 855	org/telegram/tgnet/TLRPC$TL_decryptedMessage:ttl	I
    //   4212: aload 14
    //   4214: ifnull +29 -> 4243
    //   4217: aload 14
    //   4219: invokevirtual 438	java/util/ArrayList:isEmpty	()Z
    //   4222: ifne +21 -> 4243
    //   4225: aload_2
    //   4226: aload 14
    //   4228: putfield 856	org/telegram/tgnet/TLRPC$TL_decryptedMessage:entities	Ljava/util/ArrayList;
    //   4231: aload_2
    //   4232: aload_2
    //   4233: getfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4236: sipush 128
    //   4239: ior
    //   4240: putfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4243: aload_2
    //   4244: astore_1
    //   4245: aload 10
    //   4247: ifnull +43 -> 4290
    //   4250: aload_2
    //   4251: astore_1
    //   4252: aload 10
    //   4254: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4257: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   4260: lconst_0
    //   4261: lcmp
    //   4262: ifeq +28 -> 4290
    //   4265: aload_2
    //   4266: aload 10
    //   4268: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4271: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   4274: putfield 858	org/telegram/tgnet/TLRPC$TL_decryptedMessage:reply_to_random_id	J
    //   4277: aload_2
    //   4278: aload_2
    //   4279: getfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4282: bipush 8
    //   4284: ior
    //   4285: putfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4288: aload_2
    //   4289: astore_1
    //   4290: aload 16
    //   4292: ifnull +41 -> 4333
    //   4295: aload 16
    //   4297: ldc_w 316
    //   4300: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4303: ifnull +30 -> 4333
    //   4306: aload_1
    //   4307: aload 16
    //   4309: ldc_w 316
    //   4312: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4315: checkcast 201	java/lang/String
    //   4318: putfield 859	org/telegram/tgnet/TLRPC$TL_decryptedMessage:via_bot_name	Ljava/lang/String;
    //   4321: aload_1
    //   4322: aload_1
    //   4323: getfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4326: sipush 2048
    //   4329: ior
    //   4330: putfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4333: aload_1
    //   4334: aload 25
    //   4336: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   4339: putfield 860	org/telegram/tgnet/TLRPC$TL_decryptedMessage:random_id	J
    //   4342: aload_1
    //   4343: aload 36
    //   4345: putfield 861	org/telegram/tgnet/TLRPC$TL_decryptedMessage:message	Ljava/lang/String;
    //   4348: aload 37
    //   4350: ifnull +138 -> 4488
    //   4353: aload 37
    //   4355: getfield 449	org/telegram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   4358: ifnull +130 -> 4488
    //   4361: aload_1
    //   4362: new 863	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaWebPage
    //   4365: dup
    //   4366: invokespecial 864	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaWebPage:<init>	()V
    //   4369: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   4372: aload_1
    //   4373: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   4376: aload 37
    //   4378: getfield 449	org/telegram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   4381: putfield 870	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:url	Ljava/lang/String;
    //   4384: aload_1
    //   4385: aload_1
    //   4386: getfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4389: sipush 512
    //   4392: ior
    //   4393: putfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   4396: invokestatic 875	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   4399: aload_1
    //   4400: aload 6
    //   4402: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4405: aload 34
    //   4407: aconst_null
    //   4408: aconst_null
    //   4409: aload 6
    //   4411: invokevirtual 879	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   4414: aload 13
    //   4416: ifnonnull -4409 -> 7
    //   4419: lload 7
    //   4421: iconst_0
    //   4422: invokestatic 851	org/telegram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   4425: return
    //   4426: aload 34
    //   4428: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   4431: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   4434: bipush 17
    //   4436: if_icmplt +23 -> 4459
    //   4439: new 881	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer17
    //   4442: dup
    //   4443: invokespecial 882	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer17:<init>	()V
    //   4446: astore_1
    //   4447: aload_1
    //   4448: aload 25
    //   4450: getfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   4453: putfield 855	org/telegram/tgnet/TLRPC$TL_decryptedMessage:ttl	I
    //   4456: goto -166 -> 4290
    //   4459: new 884	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer8
    //   4462: dup
    //   4463: invokespecial 885	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer8:<init>	()V
    //   4466: astore_1
    //   4467: aload_1
    //   4468: bipush 15
    //   4470: newarray <illegal type>
    //   4472: putfield 889	org/telegram/tgnet/TLRPC$TL_decryptedMessage:random_bytes	[B
    //   4475: getstatic 735	org/telegram/messenger/Utilities:random	Ljava/security/SecureRandom;
    //   4478: aload_1
    //   4479: getfield 889	org/telegram/tgnet/TLRPC$TL_decryptedMessage:random_bytes	[B
    //   4482: invokevirtual 893	java/security/SecureRandom:nextBytes	([B)V
    //   4485: goto -195 -> 4290
    //   4488: aload_1
    //   4489: new 895	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaEmpty
    //   4492: dup
    //   4493: invokespecial 896	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaEmpty:<init>	()V
    //   4496: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   4499: goto -103 -> 4396
    //   4502: aload 34
    //   4504: ifnonnull +1460 -> 5964
    //   4507: aconst_null
    //   4508: astore_1
    //   4509: aconst_null
    //   4510: astore 14
    //   4512: aconst_null
    //   4513: astore 11
    //   4515: iload 18
    //   4517: iconst_1
    //   4518: if_icmpne +4479 -> 8997
    //   4521: aload_2
    //   4522: instanceof 898
    //   4525: ifeq +145 -> 4670
    //   4528: new 900	org/telegram/tgnet/TLRPC$TL_inputMediaVenue
    //   4531: dup
    //   4532: invokespecial 901	org/telegram/tgnet/TLRPC$TL_inputMediaVenue:<init>	()V
    //   4535: astore_1
    //   4536: aload_1
    //   4537: aload_2
    //   4538: getfield 904	org/telegram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   4541: putfield 907	org/telegram/tgnet/TLRPC$InputMedia:address	Ljava/lang/String;
    //   4544: aload_1
    //   4545: aload_2
    //   4546: getfield 910	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   4549: putfield 911	org/telegram/tgnet/TLRPC$InputMedia:title	Ljava/lang/String;
    //   4552: aload_1
    //   4553: aload_2
    //   4554: getfield 914	org/telegram/tgnet/TLRPC$MessageMedia:provider	Ljava/lang/String;
    //   4557: putfield 915	org/telegram/tgnet/TLRPC$InputMedia:provider	Ljava/lang/String;
    //   4560: aload_1
    //   4561: aload_2
    //   4562: getfield 918	org/telegram/tgnet/TLRPC$MessageMedia:venue_id	Ljava/lang/String;
    //   4565: putfield 919	org/telegram/tgnet/TLRPC$InputMedia:venue_id	Ljava/lang/String;
    //   4568: aload_1
    //   4569: new 921	org/telegram/tgnet/TLRPC$TL_inputGeoPoint
    //   4572: dup
    //   4573: invokespecial 922	org/telegram/tgnet/TLRPC$TL_inputGeoPoint:<init>	()V
    //   4576: putfield 926	org/telegram/tgnet/TLRPC$InputMedia:geo_point	Lorg/telegram/tgnet/TLRPC$InputGeoPoint;
    //   4579: aload_1
    //   4580: getfield 926	org/telegram/tgnet/TLRPC$InputMedia:geo_point	Lorg/telegram/tgnet/TLRPC$InputGeoPoint;
    //   4583: aload_2
    //   4584: getfield 930	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   4587: getfield 936	org/telegram/tgnet/TLRPC$GeoPoint:lat	D
    //   4590: putfield 939	org/telegram/tgnet/TLRPC$InputGeoPoint:lat	D
    //   4593: aload_1
    //   4594: getfield 926	org/telegram/tgnet/TLRPC$InputMedia:geo_point	Lorg/telegram/tgnet/TLRPC$InputGeoPoint;
    //   4597: aload_2
    //   4598: getfield 930	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   4601: getfield 942	org/telegram/tgnet/TLRPC$GeoPoint:_long	D
    //   4604: putfield 943	org/telegram/tgnet/TLRPC$InputGeoPoint:_long	D
    //   4607: aload 11
    //   4609: astore_2
    //   4610: aload 5
    //   4612: ifnull +1076 -> 5688
    //   4615: new 730	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast
    //   4618: dup
    //   4619: invokespecial 731	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:<init>	()V
    //   4622: astore 4
    //   4624: new 435	java/util/ArrayList
    //   4627: dup
    //   4628: invokespecial 618	java/util/ArrayList:<init>	()V
    //   4631: astore 9
    //   4633: iconst_0
    //   4634: istore 17
    //   4636: iload 17
    //   4638: aload 5
    //   4640: invokevirtual 512	java/util/ArrayList:size	()I
    //   4643: if_icmpge +971 -> 5614
    //   4646: aload 9
    //   4648: getstatic 735	org/telegram/messenger/Utilities:random	Ljava/security/SecureRandom;
    //   4651: invokevirtual 740	java/security/SecureRandom:nextLong	()J
    //   4654: invokestatic 745	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   4657: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   4660: pop
    //   4661: iload 17
    //   4663: iconst_1
    //   4664: iadd
    //   4665: istore 17
    //   4667: goto -31 -> 4636
    //   4670: new 945	org/telegram/tgnet/TLRPC$TL_inputMediaGeoPoint
    //   4673: dup
    //   4674: invokespecial 946	org/telegram/tgnet/TLRPC$TL_inputMediaGeoPoint:<init>	()V
    //   4677: astore_1
    //   4678: goto -110 -> 4568
    //   4681: aload_3
    //   4682: getfield 947	org/telegram/tgnet/TLRPC$TL_photo:access_hash	J
    //   4685: lconst_0
    //   4686: lcmp
    //   4687: ifne +124 -> 4811
    //   4690: new 949	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedPhoto
    //   4693: dup
    //   4694: invokespecial 950	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedPhoto:<init>	()V
    //   4697: astore 4
    //   4699: aload_3
    //   4700: getfield 951	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   4703: ifnull +4314 -> 9017
    //   4706: aload_3
    //   4707: getfield 951	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   4710: astore_1
    //   4711: aload 4
    //   4713: aload_1
    //   4714: putfield 952	org/telegram/tgnet/TLRPC$InputMedia:caption	Ljava/lang/String;
    //   4717: new 100	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   4720: dup
    //   4721: aload_0
    //   4722: invokespecial 953	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   4725: astore_2
    //   4726: aload_2
    //   4727: aload 31
    //   4729: putfield 955	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   4732: aload_2
    //   4733: iconst_0
    //   4734: putfield 956	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   4737: aload_2
    //   4738: aload 6
    //   4740: putfield 959	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   4743: aload 9
    //   4745: ifnull +34 -> 4779
    //   4748: aload 9
    //   4750: invokevirtual 205	java/lang/String:length	()I
    //   4753: ifle +26 -> 4779
    //   4756: aload 9
    //   4758: ldc_w 502
    //   4761: invokevirtual 506	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   4764: ifeq +15 -> 4779
    //   4767: aload_2
    //   4768: aload 9
    //   4770: putfield 962	org/telegram/messenger/SendMessagesHelper$DelayedMessage:httpLocation	Ljava/lang/String;
    //   4773: aload 4
    //   4775: astore_1
    //   4776: goto -166 -> 4610
    //   4779: aload_2
    //   4780: aload_3
    //   4781: getfield 509	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   4784: aload_3
    //   4785: getfield 509	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   4788: invokevirtual 512	java/util/ArrayList:size	()I
    //   4791: iconst_1
    //   4792: isub
    //   4793: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   4796: checkcast 517	org/telegram/tgnet/TLRPC$PhotoSize
    //   4799: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   4802: putfield 963	org/telegram/messenger/SendMessagesHelper$DelayedMessage:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   4805: aload 4
    //   4807: astore_1
    //   4808: goto -198 -> 4610
    //   4811: new 965	org/telegram/tgnet/TLRPC$TL_inputMediaPhoto
    //   4814: dup
    //   4815: invokespecial 966	org/telegram/tgnet/TLRPC$TL_inputMediaPhoto:<init>	()V
    //   4818: astore_2
    //   4819: aload_2
    //   4820: new 968	org/telegram/tgnet/TLRPC$TL_inputPhoto
    //   4823: dup
    //   4824: invokespecial 969	org/telegram/tgnet/TLRPC$TL_inputPhoto:<init>	()V
    //   4827: putfield 972	org/telegram/tgnet/TLRPC$TL_inputMediaPhoto:id	Lorg/telegram/tgnet/TLRPC$InputPhoto;
    //   4830: aload_3
    //   4831: getfield 951	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   4834: ifnull +4190 -> 9024
    //   4837: aload_3
    //   4838: getfield 951	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   4841: astore_1
    //   4842: aload_2
    //   4843: aload_1
    //   4844: putfield 973	org/telegram/tgnet/TLRPC$TL_inputMediaPhoto:caption	Ljava/lang/String;
    //   4847: aload_2
    //   4848: getfield 972	org/telegram/tgnet/TLRPC$TL_inputMediaPhoto:id	Lorg/telegram/tgnet/TLRPC$InputPhoto;
    //   4851: aload_3
    //   4852: getfield 974	org/telegram/tgnet/TLRPC$TL_photo:id	J
    //   4855: putfield 977	org/telegram/tgnet/TLRPC$InputPhoto:id	J
    //   4858: aload_2
    //   4859: getfield 972	org/telegram/tgnet/TLRPC$TL_inputMediaPhoto:id	Lorg/telegram/tgnet/TLRPC$InputPhoto;
    //   4862: aload_3
    //   4863: getfield 947	org/telegram/tgnet/TLRPC$TL_photo:access_hash	J
    //   4866: putfield 978	org/telegram/tgnet/TLRPC$InputPhoto:access_hash	J
    //   4869: aload_2
    //   4870: astore_1
    //   4871: aload 11
    //   4873: astore_2
    //   4874: goto -264 -> 4610
    //   4877: iload 18
    //   4879: iconst_3
    //   4880: if_icmpne +203 -> 5083
    //   4883: aload 33
    //   4885: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   4888: lconst_0
    //   4889: lcmp
    //   4890: ifne +123 -> 5013
    //   4893: aload 33
    //   4895: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   4898: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   4901: ifnull +101 -> 5002
    //   4904: new 985	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedThumbDocument
    //   4907: dup
    //   4908: invokespecial 986	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedThumbDocument:<init>	()V
    //   4911: astore_1
    //   4912: aload 33
    //   4914: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   4917: ifnull +4114 -> 9031
    //   4920: aload 33
    //   4922: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   4925: astore_2
    //   4926: aload_1
    //   4927: aload_2
    //   4928: putfield 952	org/telegram/tgnet/TLRPC$InputMedia:caption	Ljava/lang/String;
    //   4931: aload_1
    //   4932: aload 33
    //   4934: getfield 990	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   4937: putfield 991	org/telegram/tgnet/TLRPC$InputMedia:mime_type	Ljava/lang/String;
    //   4940: aload_1
    //   4941: aload 33
    //   4943: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   4946: putfield 992	org/telegram/tgnet/TLRPC$InputMedia:attributes	Ljava/util/ArrayList;
    //   4949: new 100	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   4952: dup
    //   4953: aload_0
    //   4954: invokespecial 953	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   4957: astore_2
    //   4958: aload_2
    //   4959: aload 31
    //   4961: putfield 955	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   4964: aload_2
    //   4965: iconst_1
    //   4966: putfield 956	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   4969: aload_2
    //   4970: aload 6
    //   4972: putfield 959	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   4975: aload_2
    //   4976: aload 33
    //   4978: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   4981: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   4984: putfield 963	org/telegram/messenger/SendMessagesHelper$DelayedMessage:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   4987: aload_2
    //   4988: aload 33
    //   4990: putfield 996	org/telegram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/telegram/tgnet/TLRPC$TL_document;
    //   4993: aload_2
    //   4994: aload 4
    //   4996: putfield 1000	org/telegram/messenger/SendMessagesHelper$DelayedMessage:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   4999: goto -389 -> 4610
    //   5002: new 1002	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument
    //   5005: dup
    //   5006: invokespecial 1003	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument:<init>	()V
    //   5009: astore_1
    //   5010: goto -98 -> 4912
    //   5013: new 1005	org/telegram/tgnet/TLRPC$TL_inputMediaDocument
    //   5016: dup
    //   5017: invokespecial 1006	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:<init>	()V
    //   5020: astore_2
    //   5021: aload_2
    //   5022: new 1008	org/telegram/tgnet/TLRPC$TL_inputDocument
    //   5025: dup
    //   5026: invokespecial 1009	org/telegram/tgnet/TLRPC$TL_inputDocument:<init>	()V
    //   5029: putfield 1012	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5032: aload 33
    //   5034: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5037: ifnull +4001 -> 9038
    //   5040: aload 33
    //   5042: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5045: astore_1
    //   5046: aload_2
    //   5047: aload_1
    //   5048: putfield 1013	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:caption	Ljava/lang/String;
    //   5051: aload_2
    //   5052: getfield 1012	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5055: aload 33
    //   5057: getfield 1014	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   5060: putfield 1017	org/telegram/tgnet/TLRPC$InputDocument:id	J
    //   5063: aload_2
    //   5064: getfield 1012	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5067: aload 33
    //   5069: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5072: putfield 1018	org/telegram/tgnet/TLRPC$InputDocument:access_hash	J
    //   5075: aload_2
    //   5076: astore_1
    //   5077: aload 11
    //   5079: astore_2
    //   5080: goto -470 -> 4610
    //   5083: iload 18
    //   5085: bipush 6
    //   5087: if_icmpne +3958 -> 9045
    //   5090: new 1020	org/telegram/tgnet/TLRPC$TL_inputMediaContact
    //   5093: dup
    //   5094: invokespecial 1021	org/telegram/tgnet/TLRPC$TL_inputMediaContact:<init>	()V
    //   5097: astore_1
    //   5098: aload_1
    //   5099: aload 35
    //   5101: getfield 407	org/telegram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   5104: putfield 1022	org/telegram/tgnet/TLRPC$InputMedia:phone_number	Ljava/lang/String;
    //   5107: aload_1
    //   5108: aload 35
    //   5110: getfield 411	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   5113: putfield 1023	org/telegram/tgnet/TLRPC$InputMedia:first_name	Ljava/lang/String;
    //   5116: aload_1
    //   5117: aload 35
    //   5119: getfield 415	org/telegram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   5122: putfield 1024	org/telegram/tgnet/TLRPC$InputMedia:last_name	Ljava/lang/String;
    //   5125: aload 11
    //   5127: astore_2
    //   5128: goto -518 -> 4610
    //   5131: aload 33
    //   5133: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5136: lconst_0
    //   5137: lcmp
    //   5138: ifne +235 -> 5373
    //   5141: aload 34
    //   5143: ifnonnull +139 -> 5282
    //   5146: aload 31
    //   5148: ifnull +134 -> 5282
    //   5151: aload 31
    //   5153: invokevirtual 205	java/lang/String:length	()I
    //   5156: ifle +126 -> 5282
    //   5159: aload 31
    //   5161: ldc_w 502
    //   5164: invokevirtual 506	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   5167: ifeq +115 -> 5282
    //   5170: aload 16
    //   5172: ifnull +110 -> 5282
    //   5175: new 1026	org/telegram/tgnet/TLRPC$TL_inputMediaGifExternal
    //   5178: dup
    //   5179: invokespecial 1027	org/telegram/tgnet/TLRPC$TL_inputMediaGifExternal:<init>	()V
    //   5182: astore 4
    //   5184: aload 16
    //   5186: ldc_w 1028
    //   5189: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   5192: checkcast 201	java/lang/String
    //   5195: ldc_w 1030
    //   5198: invokevirtual 1034	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   5201: astore 9
    //   5203: aload 14
    //   5205: astore_2
    //   5206: aload 4
    //   5208: astore_1
    //   5209: aload 9
    //   5211: arraylength
    //   5212: iconst_2
    //   5213: if_icmpne +27 -> 5240
    //   5216: aload 4
    //   5218: aload 9
    //   5220: iconst_0
    //   5221: aaload
    //   5222: putfield 1035	org/telegram/tgnet/TLRPC$InputMedia:url	Ljava/lang/String;
    //   5225: aload 4
    //   5227: aload 9
    //   5229: iconst_1
    //   5230: aaload
    //   5231: putfield 1038	org/telegram/tgnet/TLRPC$InputMedia:q	Ljava/lang/String;
    //   5234: aload 4
    //   5236: astore_1
    //   5237: aload 14
    //   5239: astore_2
    //   5240: aload_1
    //   5241: aload 33
    //   5243: getfield 990	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   5246: putfield 991	org/telegram/tgnet/TLRPC$InputMedia:mime_type	Ljava/lang/String;
    //   5249: aload_1
    //   5250: aload 33
    //   5252: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   5255: putfield 992	org/telegram/tgnet/TLRPC$InputMedia:attributes	Ljava/util/ArrayList;
    //   5258: aload 33
    //   5260: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5263: ifnull +3799 -> 9062
    //   5266: aload 33
    //   5268: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5271: astore 4
    //   5273: aload_1
    //   5274: aload 4
    //   5276: putfield 952	org/telegram/tgnet/TLRPC$InputMedia:caption	Ljava/lang/String;
    //   5279: goto -669 -> 4610
    //   5282: aload 33
    //   5284: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5287: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5290: ifnull +72 -> 5362
    //   5293: aload 33
    //   5295: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5298: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5301: instanceof 1040
    //   5304: ifeq +58 -> 5362
    //   5307: new 985	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedThumbDocument
    //   5310: dup
    //   5311: invokespecial 986	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedThumbDocument:<init>	()V
    //   5314: astore_1
    //   5315: new 100	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   5318: dup
    //   5319: aload_0
    //   5320: invokespecial 953	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   5323: astore_2
    //   5324: aload_2
    //   5325: aload 31
    //   5327: putfield 955	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   5330: aload_2
    //   5331: iconst_2
    //   5332: putfield 956	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   5335: aload_2
    //   5336: aload 6
    //   5338: putfield 959	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   5341: aload_2
    //   5342: aload 33
    //   5344: putfield 996	org/telegram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/telegram/tgnet/TLRPC$TL_document;
    //   5347: aload_2
    //   5348: aload 33
    //   5350: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5353: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5356: putfield 963	org/telegram/messenger/SendMessagesHelper$DelayedMessage:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   5359: goto -119 -> 5240
    //   5362: new 1002	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument
    //   5365: dup
    //   5366: invokespecial 1003	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument:<init>	()V
    //   5369: astore_1
    //   5370: goto -55 -> 5315
    //   5373: new 1005	org/telegram/tgnet/TLRPC$TL_inputMediaDocument
    //   5376: dup
    //   5377: invokespecial 1006	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:<init>	()V
    //   5380: astore_2
    //   5381: aload_2
    //   5382: new 1008	org/telegram/tgnet/TLRPC$TL_inputDocument
    //   5385: dup
    //   5386: invokespecial 1009	org/telegram/tgnet/TLRPC$TL_inputDocument:<init>	()V
    //   5389: putfield 1012	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5392: aload_2
    //   5393: getfield 1012	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5396: aload 33
    //   5398: getfield 1014	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   5401: putfield 1017	org/telegram/tgnet/TLRPC$InputDocument:id	J
    //   5404: aload_2
    //   5405: getfield 1012	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5408: aload 33
    //   5410: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5413: putfield 1018	org/telegram/tgnet/TLRPC$InputDocument:access_hash	J
    //   5416: aload 33
    //   5418: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5421: ifnull +3649 -> 9070
    //   5424: aload 33
    //   5426: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5429: astore_1
    //   5430: aload_2
    //   5431: aload_1
    //   5432: putfield 1013	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:caption	Ljava/lang/String;
    //   5435: aload_2
    //   5436: astore_1
    //   5437: aload 11
    //   5439: astore_2
    //   5440: goto -830 -> 4610
    //   5443: aload 11
    //   5445: astore_2
    //   5446: iload 18
    //   5448: bipush 8
    //   5450: if_icmpne -840 -> 4610
    //   5453: aload 33
    //   5455: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5458: lconst_0
    //   5459: lcmp
    //   5460: ifne +84 -> 5544
    //   5463: new 1002	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument
    //   5466: dup
    //   5467: invokespecial 1003	org/telegram/tgnet/TLRPC$TL_inputMediaUploadedDocument:<init>	()V
    //   5470: astore 4
    //   5472: aload 4
    //   5474: aload 33
    //   5476: getfield 990	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   5479: putfield 991	org/telegram/tgnet/TLRPC$InputMedia:mime_type	Ljava/lang/String;
    //   5482: aload 4
    //   5484: aload 33
    //   5486: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   5489: putfield 992	org/telegram/tgnet/TLRPC$InputMedia:attributes	Ljava/util/ArrayList;
    //   5492: aload 33
    //   5494: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5497: ifnull +3580 -> 9077
    //   5500: aload 33
    //   5502: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5505: astore_1
    //   5506: aload 4
    //   5508: aload_1
    //   5509: putfield 952	org/telegram/tgnet/TLRPC$InputMedia:caption	Ljava/lang/String;
    //   5512: new 100	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   5515: dup
    //   5516: aload_0
    //   5517: invokespecial 953	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   5520: astore_2
    //   5521: aload_2
    //   5522: iconst_3
    //   5523: putfield 956	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   5526: aload_2
    //   5527: aload 6
    //   5529: putfield 959	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   5532: aload_2
    //   5533: aload 33
    //   5535: putfield 996	org/telegram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/telegram/tgnet/TLRPC$TL_document;
    //   5538: aload 4
    //   5540: astore_1
    //   5541: goto -931 -> 4610
    //   5544: new 1005	org/telegram/tgnet/TLRPC$TL_inputMediaDocument
    //   5547: dup
    //   5548: invokespecial 1006	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:<init>	()V
    //   5551: astore_2
    //   5552: aload_2
    //   5553: new 1008	org/telegram/tgnet/TLRPC$TL_inputDocument
    //   5556: dup
    //   5557: invokespecial 1009	org/telegram/tgnet/TLRPC$TL_inputDocument:<init>	()V
    //   5560: putfield 1012	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5563: aload 33
    //   5565: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5568: ifnull +3516 -> 9084
    //   5571: aload 33
    //   5573: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   5576: astore_1
    //   5577: aload_2
    //   5578: aload_1
    //   5579: putfield 1013	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:caption	Ljava/lang/String;
    //   5582: aload_2
    //   5583: getfield 1012	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5586: aload 33
    //   5588: getfield 1014	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   5591: putfield 1017	org/telegram/tgnet/TLRPC$InputDocument:id	J
    //   5594: aload_2
    //   5595: getfield 1012	org/telegram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/telegram/tgnet/TLRPC$InputDocument;
    //   5598: aload 33
    //   5600: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5603: putfield 1018	org/telegram/tgnet/TLRPC$InputDocument:access_hash	J
    //   5606: aload_2
    //   5607: astore_1
    //   5608: aload 11
    //   5610: astore_2
    //   5611: goto -1001 -> 4610
    //   5614: aload 4
    //   5616: aload 5
    //   5618: putfield 788	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:contacts	Ljava/util/ArrayList;
    //   5621: aload 4
    //   5623: aload_1
    //   5624: putfield 794	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:media	Lorg/telegram/tgnet/TLRPC$InputMedia;
    //   5627: aload 4
    //   5629: aload 9
    //   5631: putfield 796	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:random_id	Ljava/util/ArrayList;
    //   5634: aload 4
    //   5636: ldc_w 322
    //   5639: putfield 785	org/telegram/tgnet/TLRPC$TL_messages_sendBroadcast:message	Ljava/lang/String;
    //   5642: aload_2
    //   5643: ifnull +9 -> 5652
    //   5646: aload_2
    //   5647: aload 4
    //   5649: putfield 1044	org/telegram/messenger/SendMessagesHelper$DelayedMessage:sendRequest	Lorg/telegram/tgnet/TLObject;
    //   5652: aload 4
    //   5654: astore_1
    //   5655: aload_1
    //   5656: astore 4
    //   5658: aload 13
    //   5660: ifnonnull +12 -> 5672
    //   5663: lload 7
    //   5665: iconst_0
    //   5666: invokestatic 851	org/telegram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   5669: aload_1
    //   5670: astore 4
    //   5672: iload 18
    //   5674: iconst_1
    //   5675: if_icmpne +138 -> 5813
    //   5678: aload_0
    //   5679: aload 4
    //   5681: aload 6
    //   5683: aconst_null
    //   5684: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   5687: return
    //   5688: new 1046	org/telegram/tgnet/TLRPC$TL_messages_sendMedia
    //   5691: dup
    //   5692: invokespecial 1047	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:<init>	()V
    //   5695: astore 4
    //   5697: aload 4
    //   5699: aload 32
    //   5701: putfield 1048	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   5704: aload 25
    //   5706: getfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   5709: instanceof 809
    //   5712: ifeq +45 -> 5757
    //   5715: aload 4
    //   5717: getstatic 815	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   5720: ldc_w 817
    //   5723: iconst_0
    //   5724: invokevirtual 823	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   5727: new 700	java/lang/StringBuilder
    //   5730: dup
    //   5731: invokespecial 701	java/lang/StringBuilder:<init>	()V
    //   5734: ldc_w 825
    //   5737: invokevirtual 707	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5740: lload 7
    //   5742: invokevirtual 724	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   5745: invokevirtual 725	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5748: iconst_0
    //   5749: invokeinterface 831 3 0
    //   5754: putfield 1049	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:silent	Z
    //   5757: aload 4
    //   5759: aload 25
    //   5761: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   5764: putfield 1050	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:random_id	J
    //   5767: aload 4
    //   5769: aload_1
    //   5770: putfield 1051	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:media	Lorg/telegram/tgnet/TLRPC$InputMedia;
    //   5773: aload 10
    //   5775: ifnull +25 -> 5800
    //   5778: aload 4
    //   5780: aload 4
    //   5782: getfield 1052	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:flags	I
    //   5785: iconst_1
    //   5786: ior
    //   5787: putfield 1052	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:flags	I
    //   5790: aload 4
    //   5792: aload 10
    //   5794: invokevirtual 275	org/telegram/messenger/MessageObject:getId	()I
    //   5797: putfield 1053	org/telegram/tgnet/TLRPC$TL_messages_sendMedia:reply_to_msg_id	I
    //   5800: aload_2
    //   5801: ifnull +3290 -> 9091
    //   5804: aload_2
    //   5805: aload 4
    //   5807: putfield 1044	org/telegram/messenger/SendMessagesHelper$DelayedMessage:sendRequest	Lorg/telegram/tgnet/TLObject;
    //   5810: goto +3281 -> 9091
    //   5813: iload 18
    //   5815: iconst_2
    //   5816: if_icmpne +28 -> 5844
    //   5819: aload_3
    //   5820: getfield 947	org/telegram/tgnet/TLRPC$TL_photo:access_hash	J
    //   5823: lconst_0
    //   5824: lcmp
    //   5825: ifne +9 -> 5834
    //   5828: aload_0
    //   5829: aload_2
    //   5830: invokespecial 1057	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   5833: return
    //   5834: aload_0
    //   5835: aload 4
    //   5837: aload 6
    //   5839: aconst_null
    //   5840: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   5843: return
    //   5844: iload 18
    //   5846: iconst_3
    //   5847: if_icmpne +29 -> 5876
    //   5850: aload 33
    //   5852: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5855: lconst_0
    //   5856: lcmp
    //   5857: ifne +9 -> 5866
    //   5860: aload_0
    //   5861: aload_2
    //   5862: invokespecial 1057	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   5865: return
    //   5866: aload_0
    //   5867: aload 4
    //   5869: aload 6
    //   5871: aconst_null
    //   5872: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   5875: return
    //   5876: iload 18
    //   5878: bipush 6
    //   5880: if_icmpne +13 -> 5893
    //   5883: aload_0
    //   5884: aload 4
    //   5886: aload 6
    //   5888: aconst_null
    //   5889: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   5892: return
    //   5893: iload 18
    //   5895: bipush 7
    //   5897: if_icmpne +34 -> 5931
    //   5900: aload 33
    //   5902: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5905: lconst_0
    //   5906: lcmp
    //   5907: ifne +13 -> 5920
    //   5910: aload_2
    //   5911: ifnull +9 -> 5920
    //   5914: aload_0
    //   5915: aload_2
    //   5916: invokespecial 1057	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   5919: return
    //   5920: aload_0
    //   5921: aload 4
    //   5923: aload 6
    //   5925: aload 31
    //   5927: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   5930: return
    //   5931: iload 18
    //   5933: bipush 8
    //   5935: if_icmpne -5928 -> 7
    //   5938: aload 33
    //   5940: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   5943: lconst_0
    //   5944: lcmp
    //   5945: ifne +9 -> 5954
    //   5948: aload_0
    //   5949: aload_2
    //   5950: invokespecial 1057	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   5953: return
    //   5954: aload_0
    //   5955: aload 4
    //   5957: aload 6
    //   5959: aconst_null
    //   5960: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   5963: return
    //   5964: aload 34
    //   5966: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   5969: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   5972: bipush 46
    //   5974: if_icmplt +302 -> 6276
    //   5977: new 853	org/telegram/tgnet/TLRPC$TL_decryptedMessage
    //   5980: dup
    //   5981: invokespecial 854	org/telegram/tgnet/TLRPC$TL_decryptedMessage:<init>	()V
    //   5984: astore_1
    //   5985: aload_1
    //   5986: aload 25
    //   5988: getfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   5991: putfield 855	org/telegram/tgnet/TLRPC$TL_decryptedMessage:ttl	I
    //   5994: aload 14
    //   5996: ifnull +29 -> 6025
    //   5999: aload 14
    //   6001: invokevirtual 438	java/util/ArrayList:isEmpty	()Z
    //   6004: ifne +21 -> 6025
    //   6007: aload_1
    //   6008: aload 14
    //   6010: putfield 856	org/telegram/tgnet/TLRPC$TL_decryptedMessage:entities	Ljava/util/ArrayList;
    //   6013: aload_1
    //   6014: aload_1
    //   6015: getfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6018: sipush 128
    //   6021: ior
    //   6022: putfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6025: aload 10
    //   6027: ifnull +39 -> 6066
    //   6030: aload 10
    //   6032: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   6035: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   6038: lconst_0
    //   6039: lcmp
    //   6040: ifeq +26 -> 6066
    //   6043: aload_1
    //   6044: aload 10
    //   6046: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   6049: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   6052: putfield 858	org/telegram/tgnet/TLRPC$TL_decryptedMessage:reply_to_random_id	J
    //   6055: aload_1
    //   6056: aload_1
    //   6057: getfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6060: bipush 8
    //   6062: ior
    //   6063: putfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6066: aload_1
    //   6067: aload_1
    //   6068: getfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6071: sipush 512
    //   6074: ior
    //   6075: putfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6078: aload 16
    //   6080: ifnull +41 -> 6121
    //   6083: aload 16
    //   6085: ldc_w 316
    //   6088: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   6091: ifnull +30 -> 6121
    //   6094: aload_1
    //   6095: aload 16
    //   6097: ldc_w 316
    //   6100: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   6103: checkcast 201	java/lang/String
    //   6106: putfield 859	org/telegram/tgnet/TLRPC$TL_decryptedMessage:via_bot_name	Ljava/lang/String;
    //   6109: aload_1
    //   6110: aload_1
    //   6111: getfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6114: sipush 2048
    //   6117: ior
    //   6118: putfield 857	org/telegram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   6121: aload_1
    //   6122: aload 25
    //   6124: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   6127: putfield 860	org/telegram/tgnet/TLRPC$TL_decryptedMessage:random_id	J
    //   6130: aload_1
    //   6131: ldc_w 322
    //   6134: putfield 861	org/telegram/tgnet/TLRPC$TL_decryptedMessage:message	Ljava/lang/String;
    //   6137: iload 18
    //   6139: iconst_1
    //   6140: if_icmpne +2954 -> 9094
    //   6143: aload_2
    //   6144: instanceof 898
    //   6147: ifeq +191 -> 6338
    //   6150: aload 34
    //   6152: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   6155: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   6158: bipush 46
    //   6160: if_icmplt +178 -> 6338
    //   6163: aload_1
    //   6164: new 1059	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVenue
    //   6167: dup
    //   6168: invokespecial 1060	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVenue:<init>	()V
    //   6171: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6174: aload_1
    //   6175: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6178: aload_2
    //   6179: getfield 904	org/telegram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   6182: putfield 1061	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:address	Ljava/lang/String;
    //   6185: aload_1
    //   6186: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6189: aload_2
    //   6190: getfield 910	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   6193: putfield 1062	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:title	Ljava/lang/String;
    //   6196: aload_1
    //   6197: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6200: aload_2
    //   6201: getfield 914	org/telegram/tgnet/TLRPC$MessageMedia:provider	Ljava/lang/String;
    //   6204: putfield 1063	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:provider	Ljava/lang/String;
    //   6207: aload_1
    //   6208: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6211: aload_2
    //   6212: getfield 918	org/telegram/tgnet/TLRPC$MessageMedia:venue_id	Ljava/lang/String;
    //   6215: putfield 1064	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:venue_id	Ljava/lang/String;
    //   6218: aload_1
    //   6219: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6222: aload_2
    //   6223: getfield 930	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   6226: getfield 936	org/telegram/tgnet/TLRPC$GeoPoint:lat	D
    //   6229: putfield 1065	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:lat	D
    //   6232: aload_1
    //   6233: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6236: aload_2
    //   6237: getfield 930	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   6240: getfield 942	org/telegram/tgnet/TLRPC$GeoPoint:_long	D
    //   6243: putfield 1066	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:_long	D
    //   6246: invokestatic 875	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   6249: aload_1
    //   6250: aload 6
    //   6252: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   6255: aload 34
    //   6257: aconst_null
    //   6258: aconst_null
    //   6259: aload 6
    //   6261: invokevirtual 879	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   6264: aload 13
    //   6266: ifnonnull -6259 -> 7
    //   6269: lload 7
    //   6271: iconst_0
    //   6272: invokestatic 851	org/telegram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   6275: return
    //   6276: aload 34
    //   6278: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   6281: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   6284: bipush 17
    //   6286: if_icmplt +23 -> 6309
    //   6289: new 881	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer17
    //   6292: dup
    //   6293: invokespecial 882	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer17:<init>	()V
    //   6296: astore_1
    //   6297: aload_1
    //   6298: aload 25
    //   6300: getfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   6303: putfield 855	org/telegram/tgnet/TLRPC$TL_decryptedMessage:ttl	I
    //   6306: goto -228 -> 6078
    //   6309: new 884	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer8
    //   6312: dup
    //   6313: invokespecial 885	org/telegram/tgnet/TLRPC$TL_decryptedMessage_layer8:<init>	()V
    //   6316: astore_1
    //   6317: aload_1
    //   6318: bipush 15
    //   6320: newarray <illegal type>
    //   6322: putfield 889	org/telegram/tgnet/TLRPC$TL_decryptedMessage:random_bytes	[B
    //   6325: getstatic 735	org/telegram/messenger/Utilities:random	Ljava/security/SecureRandom;
    //   6328: aload_1
    //   6329: getfield 889	org/telegram/tgnet/TLRPC$TL_decryptedMessage:random_bytes	[B
    //   6332: invokevirtual 893	java/security/SecureRandom:nextBytes	([B)V
    //   6335: goto -257 -> 6078
    //   6338: aload_1
    //   6339: new 1068	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaGeoPoint
    //   6342: dup
    //   6343: invokespecial 1069	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaGeoPoint:<init>	()V
    //   6346: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6349: goto -131 -> 6218
    //   6352: aload_3
    //   6353: getfield 509	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   6356: iconst_0
    //   6357: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   6360: checkcast 517	org/telegram/tgnet/TLRPC$PhotoSize
    //   6363: astore 5
    //   6365: aload_3
    //   6366: getfield 509	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   6369: aload_3
    //   6370: getfield 509	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   6373: invokevirtual 512	java/util/ArrayList:size	()I
    //   6376: iconst_1
    //   6377: isub
    //   6378: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   6381: checkcast 517	org/telegram/tgnet/TLRPC$PhotoSize
    //   6384: astore 4
    //   6386: aload 5
    //   6388: invokestatic 1075	org/telegram/messenger/ImageLoader:fillPhotoSizeWithBytes	(Lorg/telegram/tgnet/TLRPC$PhotoSize;)V
    //   6391: aload 34
    //   6393: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   6396: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   6399: bipush 46
    //   6401: if_icmplt +223 -> 6624
    //   6404: aload_1
    //   6405: new 1077	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto
    //   6408: dup
    //   6409: invokespecial 1078	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto:<init>	()V
    //   6412: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6415: aload_1
    //   6416: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6419: astore 10
    //   6421: aload_3
    //   6422: getfield 951	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   6425: ifnull +2689 -> 9114
    //   6428: aload_3
    //   6429: getfield 951	org/telegram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   6432: astore_2
    //   6433: aload 10
    //   6435: aload_2
    //   6436: putfield 1079	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:caption	Ljava/lang/String;
    //   6439: aload 5
    //   6441: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6444: ifnull +164 -> 6608
    //   6447: aload_1
    //   6448: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6451: checkcast 1077	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto
    //   6454: aload 5
    //   6456: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6459: putfield 1084	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto:thumb	[B
    //   6462: aload_1
    //   6463: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6466: aload 5
    //   6468: getfield 1087	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   6471: putfield 1090	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   6474: aload_1
    //   6475: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6478: aload 5
    //   6480: getfield 1093	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   6483: putfield 1096	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   6486: aload_1
    //   6487: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6490: aload 4
    //   6492: getfield 1093	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   6495: putfield 1097	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:w	I
    //   6498: aload_1
    //   6499: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6502: aload 4
    //   6504: getfield 1087	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   6507: putfield 1098	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:h	I
    //   6510: aload_1
    //   6511: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6514: aload 4
    //   6516: getfield 1100	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   6519: putfield 1101	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   6522: aload 4
    //   6524: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6527: getfield 1106	org/telegram/tgnet/TLRPC$FileLocation:key	[B
    //   6530: ifnonnull +176 -> 6706
    //   6533: new 100	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   6536: dup
    //   6537: aload_0
    //   6538: invokespecial 953	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   6541: astore_2
    //   6542: aload_2
    //   6543: aload 31
    //   6545: putfield 955	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   6548: aload_2
    //   6549: aload_1
    //   6550: putfield 1110	org/telegram/messenger/SendMessagesHelper$DelayedMessage:sendEncryptedRequest	Lorg/telegram/tgnet/TLRPC$TL_decryptedMessage;
    //   6553: aload_2
    //   6554: iconst_0
    //   6555: putfield 956	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   6558: aload_2
    //   6559: aload 6
    //   6561: putfield 959	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   6564: aload_2
    //   6565: aload 34
    //   6567: putfield 1114	org/telegram/messenger/SendMessagesHelper$DelayedMessage:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   6570: aload 9
    //   6572: ifnull +105 -> 6677
    //   6575: aload 9
    //   6577: invokevirtual 205	java/lang/String:length	()I
    //   6580: ifle +97 -> 6677
    //   6583: aload 9
    //   6585: ldc_w 502
    //   6588: invokevirtual 506	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   6591: ifeq +86 -> 6677
    //   6594: aload_2
    //   6595: aload 9
    //   6597: putfield 962	org/telegram/messenger/SendMessagesHelper$DelayedMessage:httpLocation	Ljava/lang/String;
    //   6600: aload_0
    //   6601: aload_2
    //   6602: invokespecial 1057	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   6605: goto -341 -> 6264
    //   6608: aload_1
    //   6609: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6612: checkcast 1077	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto
    //   6615: iconst_0
    //   6616: newarray <illegal type>
    //   6618: putfield 1084	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto:thumb	[B
    //   6621: goto -159 -> 6462
    //   6624: aload_1
    //   6625: new 1116	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto_layer8
    //   6628: dup
    //   6629: invokespecial 1117	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto_layer8:<init>	()V
    //   6632: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6635: aload 5
    //   6637: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6640: ifnull +21 -> 6661
    //   6643: aload_1
    //   6644: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6647: checkcast 1116	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto_layer8
    //   6650: aload 5
    //   6652: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6655: putfield 1118	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto_layer8:thumb	[B
    //   6658: goto -196 -> 6462
    //   6661: aload_1
    //   6662: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6665: checkcast 1116	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto_layer8
    //   6668: iconst_0
    //   6669: newarray <illegal type>
    //   6671: putfield 1118	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto_layer8:thumb	[B
    //   6674: goto -212 -> 6462
    //   6677: aload_2
    //   6678: aload_3
    //   6679: getfield 509	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   6682: aload_3
    //   6683: getfield 509	org/telegram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   6686: invokevirtual 512	java/util/ArrayList:size	()I
    //   6689: iconst_1
    //   6690: isub
    //   6691: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   6694: checkcast 517	org/telegram/tgnet/TLRPC$PhotoSize
    //   6697: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6700: putfield 963	org/telegram/messenger/SendMessagesHelper$DelayedMessage:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6703: goto -103 -> 6600
    //   6706: new 1120	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile
    //   6709: dup
    //   6710: invokespecial 1121	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:<init>	()V
    //   6713: astore_2
    //   6714: aload_2
    //   6715: aload 4
    //   6717: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6720: getfield 1124	org/telegram/tgnet/TLRPC$FileLocation:volume_id	J
    //   6723: putfield 1125	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:id	J
    //   6726: aload_2
    //   6727: aload 4
    //   6729: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6732: getfield 1128	org/telegram/tgnet/TLRPC$FileLocation:secret	J
    //   6735: putfield 1129	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:access_hash	J
    //   6738: aload_1
    //   6739: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6742: aload 4
    //   6744: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6747: getfield 1106	org/telegram/tgnet/TLRPC$FileLocation:key	[B
    //   6750: putfield 1130	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:key	[B
    //   6753: aload_1
    //   6754: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6757: aload 4
    //   6759: getfield 521	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6762: getfield 1133	org/telegram/tgnet/TLRPC$FileLocation:iv	[B
    //   6765: putfield 1134	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:iv	[B
    //   6768: invokestatic 875	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   6771: aload_1
    //   6772: aload 6
    //   6774: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   6777: aload 34
    //   6779: aload_2
    //   6780: aconst_null
    //   6781: aload 6
    //   6783: invokevirtual 879	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   6786: goto -522 -> 6264
    //   6789: iload 18
    //   6791: iconst_3
    //   6792: if_icmpne +519 -> 7311
    //   6795: aload 33
    //   6797: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6800: invokestatic 1075	org/telegram/messenger/ImageLoader:fillPhotoSizeWithBytes	(Lorg/telegram/tgnet/TLRPC$PhotoSize;)V
    //   6803: aload 34
    //   6805: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   6808: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   6811: bipush 46
    //   6813: if_icmplt +280 -> 7093
    //   6816: aload_1
    //   6817: new 1136	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo
    //   6820: dup
    //   6821: invokespecial 1137	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo:<init>	()V
    //   6824: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6827: aload 33
    //   6829: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6832: ifnull +245 -> 7077
    //   6835: aload 33
    //   6837: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6840: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6843: ifnull +234 -> 7077
    //   6846: aload_1
    //   6847: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6850: checkcast 1136	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo
    //   6853: aload 33
    //   6855: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6858: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6861: putfield 1138	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo:thumb	[B
    //   6864: aload_1
    //   6865: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6868: astore_3
    //   6869: aload 33
    //   6871: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   6874: ifnull +2247 -> 9121
    //   6877: aload 33
    //   6879: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   6882: astore_2
    //   6883: aload_3
    //   6884: aload_2
    //   6885: putfield 1079	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:caption	Ljava/lang/String;
    //   6888: aload_1
    //   6889: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6892: ldc_w 1140
    //   6895: putfield 1141	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   6898: aload_1
    //   6899: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6902: aload 33
    //   6904: getfield 1142	org/telegram/tgnet/TLRPC$TL_document:size	I
    //   6907: putfield 1101	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   6910: iconst_0
    //   6911: istore 17
    //   6913: iload 17
    //   6915: aload 33
    //   6917: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   6920: invokevirtual 512	java/util/ArrayList:size	()I
    //   6923: if_icmpge +57 -> 6980
    //   6926: aload 33
    //   6928: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   6931: iload 17
    //   6933: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   6936: checkcast 551	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   6939: astore_2
    //   6940: aload_2
    //   6941: instanceof 784
    //   6944: ifeq +2184 -> 9128
    //   6947: aload_1
    //   6948: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6951: aload_2
    //   6952: getfield 1143	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   6955: putfield 1097	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:w	I
    //   6958: aload_1
    //   6959: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6962: aload_2
    //   6963: getfield 1144	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   6966: putfield 1098	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:h	I
    //   6969: aload_1
    //   6970: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6973: aload_2
    //   6974: getfield 773	org/telegram/tgnet/TLRPC$DocumentAttribute:duration	I
    //   6977: putfield 1145	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:duration	I
    //   6980: aload_1
    //   6981: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6984: aload 33
    //   6986: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6989: getfield 1087	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   6992: putfield 1090	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   6995: aload_1
    //   6996: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6999: aload 33
    //   7001: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7004: getfield 1093	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   7007: putfield 1096	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   7010: aload 33
    //   7012: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   7015: lconst_0
    //   7016: lcmp
    //   7017: ifne +223 -> 7240
    //   7020: new 100	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   7023: dup
    //   7024: aload_0
    //   7025: invokespecial 953	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   7028: astore_2
    //   7029: aload_2
    //   7030: aload 31
    //   7032: putfield 955	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   7035: aload_2
    //   7036: aload_1
    //   7037: putfield 1110	org/telegram/messenger/SendMessagesHelper$DelayedMessage:sendEncryptedRequest	Lorg/telegram/tgnet/TLRPC$TL_decryptedMessage;
    //   7040: aload_2
    //   7041: iconst_1
    //   7042: putfield 956	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   7045: aload_2
    //   7046: aload 6
    //   7048: putfield 959	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   7051: aload_2
    //   7052: aload 34
    //   7054: putfield 1114	org/telegram/messenger/SendMessagesHelper$DelayedMessage:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   7057: aload_2
    //   7058: aload 33
    //   7060: putfield 996	org/telegram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/telegram/tgnet/TLRPC$TL_document;
    //   7063: aload_2
    //   7064: aload 4
    //   7066: putfield 1000	org/telegram/messenger/SendMessagesHelper$DelayedMessage:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   7069: aload_0
    //   7070: aload_2
    //   7071: invokespecial 1057	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   7074: goto -810 -> 6264
    //   7077: aload_1
    //   7078: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7081: checkcast 1136	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo
    //   7084: iconst_0
    //   7085: newarray <illegal type>
    //   7087: putfield 1138	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo:thumb	[B
    //   7090: goto -226 -> 6864
    //   7093: aload 34
    //   7095: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   7098: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   7101: bipush 17
    //   7103: if_icmplt +70 -> 7173
    //   7106: aload_1
    //   7107: new 1147	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer17
    //   7110: dup
    //   7111: invokespecial 1148	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer17:<init>	()V
    //   7114: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7117: aload 33
    //   7119: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7122: ifnull +35 -> 7157
    //   7125: aload 33
    //   7127: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7130: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7133: ifnull +24 -> 7157
    //   7136: aload_1
    //   7137: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7140: checkcast 1147	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer17
    //   7143: aload 33
    //   7145: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7148: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7151: putfield 1149	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer17:thumb	[B
    //   7154: goto -290 -> 6864
    //   7157: aload_1
    //   7158: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7161: checkcast 1147	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer17
    //   7164: iconst_0
    //   7165: newarray <illegal type>
    //   7167: putfield 1149	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer17:thumb	[B
    //   7170: goto -306 -> 6864
    //   7173: aload_1
    //   7174: new 1151	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer8
    //   7177: dup
    //   7178: invokespecial 1152	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer8:<init>	()V
    //   7181: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7184: aload 33
    //   7186: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7189: ifnull +35 -> 7224
    //   7192: aload 33
    //   7194: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7197: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7200: ifnull +24 -> 7224
    //   7203: aload_1
    //   7204: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7207: checkcast 1151	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer8
    //   7210: aload 33
    //   7212: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7215: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7218: putfield 1153	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer8:thumb	[B
    //   7221: goto -357 -> 6864
    //   7224: aload_1
    //   7225: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7228: checkcast 1151	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer8
    //   7231: iconst_0
    //   7232: newarray <illegal type>
    //   7234: putfield 1153	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaVideo_layer8:thumb	[B
    //   7237: goto -373 -> 6864
    //   7240: new 1120	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile
    //   7243: dup
    //   7244: invokespecial 1121	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:<init>	()V
    //   7247: astore_2
    //   7248: aload_2
    //   7249: aload 33
    //   7251: getfield 1014	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   7254: putfield 1125	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:id	J
    //   7257: aload_2
    //   7258: aload 33
    //   7260: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   7263: putfield 1129	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:access_hash	J
    //   7266: aload_1
    //   7267: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7270: aload 33
    //   7272: getfield 1154	org/telegram/tgnet/TLRPC$TL_document:key	[B
    //   7275: putfield 1130	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:key	[B
    //   7278: aload_1
    //   7279: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7282: aload 33
    //   7284: getfield 1155	org/telegram/tgnet/TLRPC$TL_document:iv	[B
    //   7287: putfield 1134	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:iv	[B
    //   7290: invokestatic 875	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   7293: aload_1
    //   7294: aload 6
    //   7296: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   7299: aload 34
    //   7301: aload_2
    //   7302: aconst_null
    //   7303: aload 6
    //   7305: invokevirtual 879	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   7308: goto -1044 -> 6264
    //   7311: iload 18
    //   7313: bipush 6
    //   7315: if_icmpne +1822 -> 9137
    //   7318: aload_1
    //   7319: new 1157	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaContact
    //   7322: dup
    //   7323: invokespecial 1158	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaContact:<init>	()V
    //   7326: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7329: aload_1
    //   7330: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7333: aload 35
    //   7335: getfield 407	org/telegram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   7338: putfield 1159	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:phone_number	Ljava/lang/String;
    //   7341: aload_1
    //   7342: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7345: aload 35
    //   7347: getfield 411	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   7350: putfield 1160	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:first_name	Ljava/lang/String;
    //   7353: aload_1
    //   7354: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7357: aload 35
    //   7359: getfield 415	org/telegram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   7362: putfield 1161	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:last_name	Ljava/lang/String;
    //   7365: aload_1
    //   7366: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7369: aload 35
    //   7371: getfield 419	org/telegram/tgnet/TLRPC$User:id	I
    //   7374: putfield 1162	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:user_id	I
    //   7377: invokestatic 875	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   7380: aload_1
    //   7381: aload 6
    //   7383: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   7386: aload 34
    //   7388: aconst_null
    //   7389: aconst_null
    //   7390: aload 6
    //   7392: invokevirtual 879	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   7395: goto -1131 -> 6264
    //   7398: aload 33
    //   7400: invokestatic 543	org/telegram/messenger/MessageObject:isStickerDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   7403: ifeq +178 -> 7581
    //   7406: aload_1
    //   7407: new 1164	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument
    //   7410: dup
    //   7411: invokespecial 1165	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument:<init>	()V
    //   7414: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7417: aload_1
    //   7418: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7421: aload 33
    //   7423: getfield 1014	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   7426: putfield 1166	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:id	J
    //   7429: aload_1
    //   7430: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7433: aload 33
    //   7435: getfield 1167	org/telegram/tgnet/TLRPC$TL_document:date	I
    //   7438: putfield 1168	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:date	I
    //   7441: aload_1
    //   7442: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7445: aload 33
    //   7447: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   7450: putfield 1169	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:access_hash	J
    //   7453: aload_1
    //   7454: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7457: aload 33
    //   7459: getfield 990	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   7462: putfield 1141	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   7465: aload_1
    //   7466: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7469: aload 33
    //   7471: getfield 1142	org/telegram/tgnet/TLRPC$TL_document:size	I
    //   7474: putfield 1101	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   7477: aload_1
    //   7478: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7481: aload 33
    //   7483: getfield 539	org/telegram/tgnet/TLRPC$TL_document:dc_id	I
    //   7486: putfield 1170	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:dc_id	I
    //   7489: aload_1
    //   7490: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7493: aload 33
    //   7495: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   7498: putfield 1171	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:attributes	Ljava/util/ArrayList;
    //   7501: aload 33
    //   7503: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7506: ifnonnull +57 -> 7563
    //   7509: aload_1
    //   7510: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7513: checkcast 1164	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument
    //   7516: new 1173	org/telegram/tgnet/TLRPC$TL_photoSizeEmpty
    //   7519: dup
    //   7520: invokespecial 1174	org/telegram/tgnet/TLRPC$TL_photoSizeEmpty:<init>	()V
    //   7523: putfield 1175	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7526: aload_1
    //   7527: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7530: checkcast 1164	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument
    //   7533: getfield 1175	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7536: ldc_w 1177
    //   7539: putfield 1179	org/telegram/tgnet/TLRPC$PhotoSize:type	Ljava/lang/String;
    //   7542: invokestatic 875	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   7545: aload_1
    //   7546: aload 6
    //   7548: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   7551: aload 34
    //   7553: aconst_null
    //   7554: aconst_null
    //   7555: aload 6
    //   7557: invokevirtual 879	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   7560: goto -1296 -> 6264
    //   7563: aload_1
    //   7564: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7567: checkcast 1164	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument
    //   7570: aload 33
    //   7572: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7575: putfield 1175	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7578: goto -36 -> 7542
    //   7581: aload 33
    //   7583: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7586: invokestatic 1075	org/telegram/messenger/ImageLoader:fillPhotoSizeWithBytes	(Lorg/telegram/tgnet/TLRPC$PhotoSize;)V
    //   7589: aload 34
    //   7591: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   7594: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   7597: bipush 46
    //   7599: if_icmplt +262 -> 7861
    //   7602: aload_1
    //   7603: new 1181	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   7606: dup
    //   7607: invokespecial 1182	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:<init>	()V
    //   7610: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7613: aload_1
    //   7614: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7617: aload 33
    //   7619: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   7622: putfield 1171	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:attributes	Ljava/util/ArrayList;
    //   7625: aload_1
    //   7626: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7629: astore_3
    //   7630: aload 33
    //   7632: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   7635: ifnull +1524 -> 9159
    //   7638: aload 33
    //   7640: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   7643: astore_2
    //   7644: aload_3
    //   7645: aload_2
    //   7646: putfield 1079	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:caption	Ljava/lang/String;
    //   7649: aload 33
    //   7651: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7654: ifnull +175 -> 7829
    //   7657: aload 33
    //   7659: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7662: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7665: ifnull +164 -> 7829
    //   7668: aload_1
    //   7669: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7672: checkcast 1181	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   7675: aload 33
    //   7677: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7680: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7683: putfield 1183	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   7686: aload_1
    //   7687: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7690: aload 33
    //   7692: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7695: getfield 1087	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   7698: putfield 1090	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   7701: aload_1
    //   7702: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7705: aload 33
    //   7707: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7710: getfield 1093	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   7713: putfield 1096	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   7716: aload_1
    //   7717: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7720: aload 33
    //   7722: getfield 1142	org/telegram/tgnet/TLRPC$TL_document:size	I
    //   7725: putfield 1101	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   7728: aload_1
    //   7729: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7732: aload 33
    //   7734: getfield 990	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   7737: putfield 1141	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   7740: aload 33
    //   7742: getfield 1154	org/telegram/tgnet/TLRPC$TL_document:key	[B
    //   7745: ifnonnull +241 -> 7986
    //   7748: new 100	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   7751: dup
    //   7752: aload_0
    //   7753: invokespecial 953	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   7756: astore_2
    //   7757: aload_2
    //   7758: aload 31
    //   7760: putfield 955	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   7763: aload_2
    //   7764: aload_1
    //   7765: putfield 1110	org/telegram/messenger/SendMessagesHelper$DelayedMessage:sendEncryptedRequest	Lorg/telegram/tgnet/TLRPC$TL_decryptedMessage;
    //   7768: aload_2
    //   7769: iconst_2
    //   7770: putfield 956	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   7773: aload_2
    //   7774: aload 6
    //   7776: putfield 959	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   7779: aload_2
    //   7780: aload 34
    //   7782: putfield 1114	org/telegram/messenger/SendMessagesHelper$DelayedMessage:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   7785: aload 9
    //   7787: ifnull +28 -> 7815
    //   7790: aload 9
    //   7792: invokevirtual 205	java/lang/String:length	()I
    //   7795: ifle +20 -> 7815
    //   7798: aload 9
    //   7800: ldc_w 502
    //   7803: invokevirtual 506	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   7806: ifeq +9 -> 7815
    //   7809: aload_2
    //   7810: aload 9
    //   7812: putfield 962	org/telegram/messenger/SendMessagesHelper$DelayedMessage:httpLocation	Ljava/lang/String;
    //   7815: aload_2
    //   7816: aload 33
    //   7818: putfield 996	org/telegram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/telegram/tgnet/TLRPC$TL_document;
    //   7821: aload_0
    //   7822: aload_2
    //   7823: invokespecial 1057	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   7826: goto -1562 -> 6264
    //   7829: aload_1
    //   7830: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7833: checkcast 1181	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   7836: iconst_0
    //   7837: newarray <illegal type>
    //   7839: putfield 1183	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   7842: aload_1
    //   7843: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7846: iconst_0
    //   7847: putfield 1090	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   7850: aload_1
    //   7851: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7854: iconst_0
    //   7855: putfield 1096	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   7858: goto -142 -> 7716
    //   7861: aload_1
    //   7862: new 1185	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument_layer8
    //   7865: dup
    //   7866: invokespecial 1186	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument_layer8:<init>	()V
    //   7869: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7872: aload_1
    //   7873: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7876: aload 33
    //   7878: invokestatic 1190	org/telegram/messenger/FileLoader:getDocumentFileName	(Lorg/telegram/tgnet/TLRPC$Document;)Ljava/lang/String;
    //   7881: putfield 1193	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:file_name	Ljava/lang/String;
    //   7884: aload 33
    //   7886: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7889: ifnull +65 -> 7954
    //   7892: aload 33
    //   7894: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7897: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7900: ifnull +54 -> 7954
    //   7903: aload_1
    //   7904: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7907: checkcast 1185	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument_layer8
    //   7910: aload 33
    //   7912: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7915: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7918: putfield 1194	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument_layer8:thumb	[B
    //   7921: aload_1
    //   7922: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7925: aload 33
    //   7927: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7930: getfield 1087	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   7933: putfield 1090	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   7936: aload_1
    //   7937: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7940: aload 33
    //   7942: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7945: getfield 1093	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   7948: putfield 1096	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   7951: goto -235 -> 7716
    //   7954: aload_1
    //   7955: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7958: checkcast 1185	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument_layer8
    //   7961: iconst_0
    //   7962: newarray <illegal type>
    //   7964: putfield 1194	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument_layer8:thumb	[B
    //   7967: aload_1
    //   7968: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7971: iconst_0
    //   7972: putfield 1090	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   7975: aload_1
    //   7976: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7979: iconst_0
    //   7980: putfield 1096	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   7983: goto -267 -> 7716
    //   7986: new 1120	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile
    //   7989: dup
    //   7990: invokespecial 1121	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:<init>	()V
    //   7993: astore_2
    //   7994: aload_2
    //   7995: aload 33
    //   7997: getfield 1014	org/telegram/tgnet/TLRPC$TL_document:id	J
    //   8000: putfield 1125	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:id	J
    //   8003: aload_2
    //   8004: aload 33
    //   8006: getfield 979	org/telegram/tgnet/TLRPC$TL_document:access_hash	J
    //   8009: putfield 1129	org/telegram/tgnet/TLRPC$TL_inputEncryptedFile:access_hash	J
    //   8012: aload_1
    //   8013: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8016: aload 33
    //   8018: getfield 1154	org/telegram/tgnet/TLRPC$TL_document:key	[B
    //   8021: putfield 1130	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:key	[B
    //   8024: aload_1
    //   8025: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8028: aload 33
    //   8030: getfield 1155	org/telegram/tgnet/TLRPC$TL_document:iv	[B
    //   8033: putfield 1134	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:iv	[B
    //   8036: invokestatic 875	org/telegram/messenger/SecretChatHelper:getInstance	()Lorg/telegram/messenger/SecretChatHelper;
    //   8039: aload_1
    //   8040: aload 6
    //   8042: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8045: aload 34
    //   8047: aload_2
    //   8048: aconst_null
    //   8049: aload 6
    //   8051: invokevirtual 879	org/telegram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/telegram/tgnet/TLRPC$DecryptedMessage;Lorg/telegram/tgnet/TLRPC$Message;Lorg/telegram/tgnet/TLRPC$EncryptedChat;Lorg/telegram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/telegram/messenger/MessageObject;)V
    //   8054: goto -1790 -> 6264
    //   8057: iload 18
    //   8059: bipush 8
    //   8061: if_icmpne -1797 -> 6264
    //   8064: new 100	org/telegram/messenger/SendMessagesHelper$DelayedMessage
    //   8067: dup
    //   8068: aload_0
    //   8069: invokespecial 953	org/telegram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/telegram/messenger/SendMessagesHelper;)V
    //   8072: astore_3
    //   8073: aload_3
    //   8074: aload 34
    //   8076: putfield 1114	org/telegram/messenger/SendMessagesHelper$DelayedMessage:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   8079: aload_3
    //   8080: aload_1
    //   8081: putfield 1110	org/telegram/messenger/SendMessagesHelper$DelayedMessage:sendEncryptedRequest	Lorg/telegram/tgnet/TLRPC$TL_decryptedMessage;
    //   8084: aload_3
    //   8085: aload 6
    //   8087: putfield 959	org/telegram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/telegram/messenger/MessageObject;
    //   8090: aload_3
    //   8091: aload 33
    //   8093: putfield 996	org/telegram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/telegram/tgnet/TLRPC$TL_document;
    //   8096: aload_3
    //   8097: iconst_3
    //   8098: putfield 956	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   8101: aload 34
    //   8103: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   8106: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   8109: bipush 46
    //   8111: if_icmplt +189 -> 8300
    //   8114: aload_1
    //   8115: new 1181	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   8118: dup
    //   8119: invokespecial 1182	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:<init>	()V
    //   8122: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8125: aload_1
    //   8126: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8129: aload 33
    //   8131: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   8134: putfield 1171	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:attributes	Ljava/util/ArrayList;
    //   8137: aload_1
    //   8138: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8141: astore 4
    //   8143: aload 33
    //   8145: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   8148: ifnull +1018 -> 9166
    //   8151: aload 33
    //   8153: getfield 987	org/telegram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   8156: astore_2
    //   8157: aload 4
    //   8159: aload_2
    //   8160: putfield 1079	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:caption	Ljava/lang/String;
    //   8163: aload 33
    //   8165: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8168: ifnull +100 -> 8268
    //   8171: aload 33
    //   8173: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8176: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   8179: ifnull +89 -> 8268
    //   8182: aload_1
    //   8183: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8186: checkcast 1181	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   8189: aload 33
    //   8191: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8194: getfield 1082	org/telegram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   8197: putfield 1183	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   8200: aload_1
    //   8201: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8204: aload 33
    //   8206: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8209: getfield 1087	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   8212: putfield 1090	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   8215: aload_1
    //   8216: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8219: aload 33
    //   8221: getfield 983	org/telegram/tgnet/TLRPC$TL_document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8224: getfield 1093	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   8227: putfield 1096	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   8230: aload_1
    //   8231: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8234: aload 33
    //   8236: getfield 990	org/telegram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   8239: putfield 1141	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   8242: aload_1
    //   8243: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8246: aload 33
    //   8248: getfield 1142	org/telegram/tgnet/TLRPC$TL_document:size	I
    //   8251: putfield 1101	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   8254: aload_3
    //   8255: aload 31
    //   8257: putfield 955	org/telegram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   8260: aload_0
    //   8261: aload_3
    //   8262: invokespecial 1057	org/telegram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/telegram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   8265: goto -2001 -> 6264
    //   8268: aload_1
    //   8269: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8272: checkcast 1181	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   8275: iconst_0
    //   8276: newarray <illegal type>
    //   8278: putfield 1183	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   8281: aload_1
    //   8282: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8285: iconst_0
    //   8286: putfield 1090	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   8289: aload_1
    //   8290: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8293: iconst_0
    //   8294: putfield 1096	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   8297: goto -67 -> 8230
    //   8300: aload 34
    //   8302: getfield 424	org/telegram/tgnet/TLRPC$EncryptedChat:layer	I
    //   8305: invokestatic 430	org/telegram/messenger/AndroidUtilities:getPeerLayerVersion	(I)I
    //   8308: bipush 17
    //   8310: if_icmplt +92 -> 8402
    //   8313: aload_1
    //   8314: new 1196	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaAudio
    //   8317: dup
    //   8318: invokespecial 1197	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaAudio:<init>	()V
    //   8321: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8324: goto +849 -> 9173
    //   8327: iload 17
    //   8329: aload 33
    //   8331: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   8334: invokevirtual 512	java/util/ArrayList:size	()I
    //   8337: if_icmpge +35 -> 8372
    //   8340: aload 33
    //   8342: getfield 549	org/telegram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   8345: iload 17
    //   8347: invokevirtual 515	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   8350: checkcast 551	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   8353: astore_2
    //   8354: aload_2
    //   8355: instanceof 770
    //   8358: ifeq +821 -> 9179
    //   8361: aload_1
    //   8362: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8365: aload_2
    //   8366: getfield 773	org/telegram/tgnet/TLRPC$DocumentAttribute:duration	I
    //   8369: putfield 1145	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:duration	I
    //   8372: aload_1
    //   8373: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8376: ldc_w 1199
    //   8379: putfield 1141	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   8382: aload_1
    //   8383: getfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8386: aload 33
    //   8388: getfield 1142	org/telegram/tgnet/TLRPC$TL_document:size	I
    //   8391: putfield 1101	org/telegram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   8394: aload_3
    //   8395: iconst_3
    //   8396: putfield 956	org/telegram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   8399: goto -139 -> 8260
    //   8402: aload_1
    //   8403: new 1201	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaAudio_layer8
    //   8406: dup
    //   8407: invokespecial 1202	org/telegram/tgnet/TLRPC$TL_decryptedMessageMediaAudio_layer8:<init>	()V
    //   8410: putfield 867	org/telegram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/telegram/tgnet/TLRPC$DecryptedMessageMedia;
    //   8413: goto +760 -> 9173
    //   8416: iload 18
    //   8418: iconst_4
    //   8419: if_icmpne +229 -> 8648
    //   8422: new 1204	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages
    //   8425: dup
    //   8426: invokespecial 1205	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:<init>	()V
    //   8429: astore_1
    //   8430: aload_1
    //   8431: aload 32
    //   8433: putfield 1208	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:to_peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   8436: aload 13
    //   8438: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8441: getfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   8444: ifeq +168 -> 8612
    //   8447: invokestatic 239	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   8450: aload 13
    //   8452: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8455: getfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   8458: ineg
    //   8459: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   8462: invokevirtual 294	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   8465: astore_2
    //   8466: aload_1
    //   8467: new 285	org/telegram/tgnet/TLRPC$TL_inputPeerChannel
    //   8470: dup
    //   8471: invokespecial 1209	org/telegram/tgnet/TLRPC$TL_inputPeerChannel:<init>	()V
    //   8474: putfield 1212	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:from_peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   8477: aload_1
    //   8478: getfield 1212	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:from_peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   8481: aload 13
    //   8483: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8486: getfield 765	org/telegram/tgnet/TLRPC$Message:ttl	I
    //   8489: ineg
    //   8490: putfield 290	org/telegram/tgnet/TLRPC$InputPeer:channel_id	I
    //   8493: aload_2
    //   8494: ifnull +14 -> 8508
    //   8497: aload_1
    //   8498: getfield 1212	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:from_peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   8501: aload_2
    //   8502: getfield 1213	org/telegram/tgnet/TLRPC$Chat:access_hash	J
    //   8505: putfield 721	org/telegram/tgnet/TLRPC$InputPeer:access_hash	J
    //   8508: aload 13
    //   8510: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8513: getfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   8516: instanceof 809
    //   8519: ifeq +44 -> 8563
    //   8522: aload_1
    //   8523: getstatic 815	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   8526: ldc_w 817
    //   8529: iconst_0
    //   8530: invokevirtual 823	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   8533: new 700	java/lang/StringBuilder
    //   8536: dup
    //   8537: invokespecial 701	java/lang/StringBuilder:<init>	()V
    //   8540: ldc_w 825
    //   8543: invokevirtual 707	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   8546: lload 7
    //   8548: invokevirtual 724	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   8551: invokevirtual 725	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   8554: iconst_0
    //   8555: invokeinterface 831 3 0
    //   8560: putfield 1214	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:silent	Z
    //   8563: aload_1
    //   8564: getfield 1215	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:random_id	Ljava/util/ArrayList;
    //   8567: aload 25
    //   8569: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   8572: invokestatic 745	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   8575: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   8578: pop
    //   8579: aload 13
    //   8581: invokevirtual 275	org/telegram/messenger/MessageObject:getId	()I
    //   8584: iflt +42 -> 8626
    //   8587: aload_1
    //   8588: getfield 1217	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:id	Ljava/util/ArrayList;
    //   8591: aload 13
    //   8593: invokevirtual 275	org/telegram/messenger/MessageObject:getId	()I
    //   8596: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   8599: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   8602: pop
    //   8603: aload_0
    //   8604: aload_1
    //   8605: aload 6
    //   8607: aconst_null
    //   8608: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   8611: return
    //   8612: aload_1
    //   8613: new 1219	org/telegram/tgnet/TLRPC$TL_inputPeerEmpty
    //   8616: dup
    //   8617: invokespecial 1220	org/telegram/tgnet/TLRPC$TL_inputPeerEmpty:<init>	()V
    //   8620: putfield 1212	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:from_peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   8623: goto -115 -> 8508
    //   8626: aload_1
    //   8627: getfield 1217	org/telegram/tgnet/TLRPC$TL_messages_forwardMessages:id	Ljava/util/ArrayList;
    //   8630: aload 13
    //   8632: getfield 260	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8635: getfield 1223	org/telegram/tgnet/TLRPC$Message:fwd_msg_id	I
    //   8638: invokestatic 245	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   8641: invokevirtual 562	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   8644: pop
    //   8645: goto -42 -> 8603
    //   8648: iload 18
    //   8650: bipush 9
    //   8652: if_icmpne -8645 -> 7
    //   8655: new 1225	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult
    //   8658: dup
    //   8659: invokespecial 1226	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:<init>	()V
    //   8662: astore_1
    //   8663: aload_1
    //   8664: aload 32
    //   8666: putfield 1227	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:peer	Lorg/telegram/tgnet/TLRPC$InputPeer;
    //   8669: aload_1
    //   8670: aload 25
    //   8672: getfield 308	org/telegram/tgnet/TLRPC$Message:random_id	J
    //   8675: putfield 1228	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:random_id	J
    //   8678: aload 10
    //   8680: ifnull +22 -> 8702
    //   8683: aload_1
    //   8684: aload_1
    //   8685: getfield 1229	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:flags	I
    //   8688: iconst_1
    //   8689: ior
    //   8690: putfield 1229	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:flags	I
    //   8693: aload_1
    //   8694: aload 10
    //   8696: invokevirtual 275	org/telegram/messenger/MessageObject:getId	()I
    //   8699: putfield 1230	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:reply_to_msg_id	I
    //   8702: aload 25
    //   8704: getfield 659	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   8707: instanceof 809
    //   8710: ifeq +44 -> 8754
    //   8713: aload_1
    //   8714: getstatic 815	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   8717: ldc_w 817
    //   8720: iconst_0
    //   8721: invokevirtual 823	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   8724: new 700	java/lang/StringBuilder
    //   8727: dup
    //   8728: invokespecial 701	java/lang/StringBuilder:<init>	()V
    //   8731: ldc_w 825
    //   8734: invokevirtual 707	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   8737: lload 7
    //   8739: invokevirtual 724	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   8742: invokevirtual 725	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   8745: iconst_0
    //   8746: invokeinterface 831 3 0
    //   8751: putfield 1231	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:silent	Z
    //   8754: aload_1
    //   8755: aload 16
    //   8757: ldc_w 378
    //   8760: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   8763: checkcast 201	java/lang/String
    //   8766: invokestatic 1235	org/telegram/messenger/Utilities:parseLong	(Ljava/lang/String;)Ljava/lang/Long;
    //   8769: invokevirtual 1238	java/lang/Long:longValue	()J
    //   8772: putfield 1240	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:query_id	J
    //   8775: aload_1
    //   8776: aload 16
    //   8778: ldc_w 1241
    //   8781: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   8784: checkcast 201	java/lang/String
    //   8787: putfield 1243	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:id	Ljava/lang/String;
    //   8790: aload 13
    //   8792: ifnonnull +14 -> 8806
    //   8795: aload_1
    //   8796: iconst_1
    //   8797: putfield 1244	org/telegram/tgnet/TLRPC$TL_messages_sendInlineBotResult:clear_draft	Z
    //   8800: lload 7
    //   8802: iconst_0
    //   8803: invokestatic 851	org/telegram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   8806: aload_0
    //   8807: aload_1
    //   8808: aload 6
    //   8810: aconst_null
    //   8811: invokespecial 800	org/telegram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/telegram/tgnet/TLObject;Lorg/telegram/messenger/MessageObject;Ljava/lang/String;)V
    //   8814: return
    //   8815: astore_2
    //   8816: aconst_null
    //   8817: astore_1
    //   8818: aload 26
    //   8820: astore 24
    //   8822: goto -8075 -> 747
    //   8825: aconst_null
    //   8826: astore 26
    //   8828: goto -7344 -> 1484
    //   8831: iconst_0
    //   8832: istore 17
    //   8834: goto -7305 -> 1529
    //   8837: iconst_1
    //   8838: istore 17
    //   8840: aload 11
    //   8842: astore 27
    //   8844: goto -7301 -> 1543
    //   8847: ldc_w 322
    //   8850: astore 26
    //   8852: goto -6945 -> 1907
    //   8855: iconst_2
    //   8856: istore 17
    //   8858: goto -6903 -> 1955
    //   8861: bipush 6
    //   8863: istore 17
    //   8865: aload 11
    //   8867: astore 27
    //   8869: goto -7326 -> 1543
    //   8872: ldc_w 322
    //   8875: astore 25
    //   8877: goto -6438 -> 2439
    //   8880: bipush 7
    //   8882: istore 18
    //   8884: goto -6396 -> 2488
    //   8887: iload 20
    //   8889: iconst_1
    //   8890: iadd
    //   8891: istore 20
    //   8893: goto -6298 -> 2595
    //   8896: astore_2
    //   8897: aconst_null
    //   8898: astore_1
    //   8899: aload 25
    //   8901: astore 24
    //   8903: goto -8156 -> 747
    //   8906: iload 18
    //   8908: ifeq -5440 -> 3468
    //   8911: iload 18
    //   8913: bipush 9
    //   8915: if_icmpne +42 -> 8957
    //   8918: aload 36
    //   8920: ifnull +37 -> 8957
    //   8923: aload 34
    //   8925: ifnull +32 -> 8957
    //   8928: goto -5460 -> 3468
    //   8931: iload 17
    //   8933: iconst_1
    //   8934: iadd
    //   8935: istore 17
    //   8937: goto -5222 -> 3715
    //   8940: astore_2
    //   8941: aload 6
    //   8943: astore_1
    //   8944: aload 25
    //   8946: astore 24
    //   8948: goto -8201 -> 747
    //   8951: iconst_0
    //   8952: istore 23
    //   8954: goto -4929 -> 4025
    //   8957: iload 18
    //   8959: iconst_1
    //   8960: if_icmplt +9 -> 8969
    //   8963: iload 18
    //   8965: iconst_3
    //   8966: if_icmple -4464 -> 4502
    //   8969: iload 18
    //   8971: iconst_5
    //   8972: if_icmplt +10 -> 8982
    //   8975: iload 18
    //   8977: bipush 8
    //   8979: if_icmple -4477 -> 4502
    //   8982: iload 18
    //   8984: bipush 9
    //   8986: if_icmpne -570 -> 8416
    //   8989: aload 34
    //   8991: ifnull -575 -> 8416
    //   8994: goto -4492 -> 4502
    //   8997: iload 18
    //   8999: iconst_2
    //   9000: if_icmpeq -4319 -> 4681
    //   9003: iload 18
    //   9005: bipush 9
    //   9007: if_icmpne -4130 -> 4877
    //   9010: aload_3
    //   9011: ifnull -4134 -> 4877
    //   9014: goto -4333 -> 4681
    //   9017: ldc_w 322
    //   9020: astore_1
    //   9021: goto -4310 -> 4711
    //   9024: ldc_w 322
    //   9027: astore_1
    //   9028: goto -4186 -> 4842
    //   9031: ldc_w 322
    //   9034: astore_2
    //   9035: goto -4109 -> 4926
    //   9038: ldc_w 322
    //   9041: astore_1
    //   9042: goto -3996 -> 5046
    //   9045: iload 18
    //   9047: bipush 7
    //   9049: if_icmpeq -3918 -> 5131
    //   9052: iload 18
    //   9054: bipush 9
    //   9056: if_icmpne -3613 -> 5443
    //   9059: goto -3928 -> 5131
    //   9062: ldc_w 322
    //   9065: astore 4
    //   9067: goto -3794 -> 5273
    //   9070: ldc_w 322
    //   9073: astore_1
    //   9074: goto -3644 -> 5430
    //   9077: ldc_w 322
    //   9080: astore_1
    //   9081: goto -3575 -> 5506
    //   9084: ldc_w 322
    //   9087: astore_1
    //   9088: goto -3511 -> 5577
    //   9091: goto -3419 -> 5672
    //   9094: iload 18
    //   9096: iconst_2
    //   9097: if_icmpeq -2745 -> 6352
    //   9100: iload 18
    //   9102: bipush 9
    //   9104: if_icmpne -2315 -> 6789
    //   9107: aload_3
    //   9108: ifnull -2319 -> 6789
    //   9111: goto -2759 -> 6352
    //   9114: ldc_w 322
    //   9117: astore_2
    //   9118: goto -2685 -> 6433
    //   9121: ldc_w 322
    //   9124: astore_2
    //   9125: goto -2242 -> 6883
    //   9128: iload 17
    //   9130: iconst_1
    //   9131: iadd
    //   9132: istore 17
    //   9134: goto -2221 -> 6913
    //   9137: iload 18
    //   9139: bipush 7
    //   9141: if_icmpeq -1743 -> 7398
    //   9144: iload 18
    //   9146: bipush 9
    //   9148: if_icmpne -1091 -> 8057
    //   9151: aload 33
    //   9153: ifnull -1096 -> 8057
    //   9156: goto -1758 -> 7398
    //   9159: ldc_w 322
    //   9162: astore_2
    //   9163: goto -1519 -> 7644
    //   9166: ldc_w 322
    //   9169: astore_2
    //   9170: goto -1013 -> 8157
    //   9173: iconst_0
    //   9174: istore 17
    //   9176: goto -849 -> 8327
    //   9179: iload 17
    //   9181: iconst_1
    //   9182: iadd
    //   9183: istore 17
    //   9185: goto -858 -> 8327
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	9188	0	this	SendMessagesHelper
    //   0	9188	1	paramString1	String
    //   0	9188	2	paramMessageMedia	TLRPC.MessageMedia
    //   0	9188	3	paramTL_photo	TLRPC.TL_photo
    //   0	9188	4	paramVideoEditedInfo	VideoEditedInfo
    //   0	9188	5	paramUser	TLRPC.User
    //   0	9188	6	paramTL_document	TLRPC.TL_document
    //   0	9188	7	paramLong	long
    //   0	9188	9	paramString2	String
    //   0	9188	10	paramMessageObject1	MessageObject
    //   0	9188	11	paramWebPage	TLRPC.WebPage
    //   0	9188	12	paramBoolean	boolean
    //   0	9188	13	paramMessageObject2	MessageObject
    //   0	9188	14	paramArrayList	ArrayList<TLRPC.MessageEntity>
    //   0	9188	15	paramReplyMarkup	TLRPC.ReplyMarkup
    //   0	9188	16	paramHashMap	HashMap<String, String>
    //   53	9131	17	i	int
    //   258	8891	18	j	int
    //   69	3875	19	k	int
    //   2593	6299	20	m	int
    //   66	3154	21	n	int
    //   58	3503	22	i1	int
    //   4023	4930	23	bool	boolean
    //   9	8938	24	localObject1	Object
    //   47	8898	25	localObject2	Object
    //   50	8801	26	localObject3	Object
    //   207	8661	27	localObject4	Object
    //   852	490	28	localMessageMedia	TLRPC.MessageMedia
    //   849	496	29	localTL_photo	TLRPC.TL_photo
    //   846	503	30	localObject5	Object
    //   13	8243	31	localObject6	Object
    //   84	8581	32	localInputPeer	TLRPC.InputPeer
    //   266	8886	33	localTL_document	TLRPC.TL_document
    //   109	8881	34	localObject7	Object
    //   270	7100	35	localObject8	Object
    //   273	8646	36	localObject9	Object
    //   262	4115	37	localObject10	Object
    //   87	3864	38	localObject11	Object
    // Exception table:
    //   from	to	target	type
    //   238	245	744	java/lang/Exception
    //   249	257	744	java/lang/Exception
    //   283	293	744	java/lang/Exception
    //   297	306	744	java/lang/Exception
    //   315	326	744	java/lang/Exception
    //   335	351	744	java/lang/Exception
    //   355	363	744	java/lang/Exception
    //   367	375	744	java/lang/Exception
    //   379	393	744	java/lang/Exception
    //   397	404	744	java/lang/Exception
    //   408	419	744	java/lang/Exception
    //   423	437	744	java/lang/Exception
    //   441	449	744	java/lang/Exception
    //   458	464	744	java/lang/Exception
    //   468	482	744	java/lang/Exception
    //   486	501	744	java/lang/Exception
    //   509	516	744	java/lang/Exception
    //   520	534	744	java/lang/Exception
    //   538	544	744	java/lang/Exception
    //   548	555	744	java/lang/Exception
    //   569	582	744	java/lang/Exception
    //   586	599	744	java/lang/Exception
    //   603	616	744	java/lang/Exception
    //   620	630	744	java/lang/Exception
    //   644	657	744	java/lang/Exception
    //   661	668	744	java/lang/Exception
    //   683	690	744	java/lang/Exception
    //   694	702	744	java/lang/Exception
    //   706	730	744	java/lang/Exception
    //   734	743	744	java/lang/Exception
    //   822	830	744	java/lang/Exception
    //   834	841	744	java/lang/Exception
    //   923	934	744	java/lang/Exception
    //   971	980	744	java/lang/Exception
    //   984	991	744	java/lang/Exception
    //   1011	1020	744	java/lang/Exception
    //   1024	1037	744	java/lang/Exception
    //   1057	1066	744	java/lang/Exception
    //   1073	1086	744	java/lang/Exception
    //   1106	1116	744	java/lang/Exception
    //   1120	1129	744	java/lang/Exception
    //   1201	1211	744	java/lang/Exception
    //   1215	1225	744	java/lang/Exception
    //   1229	1239	744	java/lang/Exception
    //   1243	1253	744	java/lang/Exception
    //   1257	1270	744	java/lang/Exception
    //   1307	1316	744	java/lang/Exception
    //   1320	1333	744	java/lang/Exception
    //   1366	1379	744	java/lang/Exception
    //   1383	1392	744	java/lang/Exception
    //   1401	1409	744	java/lang/Exception
    //   1413	1420	744	java/lang/Exception
    //   1437	1445	744	java/lang/Exception
    //   1449	1457	744	java/lang/Exception
    //   1461	1470	744	java/lang/Exception
    //   1474	1484	744	java/lang/Exception
    //   1493	1505	744	java/lang/Exception
    //   1514	1525	744	java/lang/Exception
    //   1533	1539	744	java/lang/Exception
    //   1547	1555	744	java/lang/Exception
    //   1559	1567	744	java/lang/Exception
    //   1571	1576	744	java/lang/Exception
    //   1580	1587	744	java/lang/Exception
    //   1591	1598	744	java/lang/Exception
    //   1602	1608	744	java/lang/Exception
    //   1622	1633	744	java/lang/Exception
    //   1637	1641	744	java/lang/Exception
    //   1667	1676	744	java/lang/Exception
    //   1683	1695	744	java/lang/Exception
    //   1699	1709	744	java/lang/Exception
    //   1725	1738	744	java/lang/Exception
    //   1742	1751	744	java/lang/Exception
    //   1755	1761	744	java/lang/Exception
    //   1765	1773	744	java/lang/Exception
    //   1782	1793	744	java/lang/Exception
    //   1808	1817	744	java/lang/Exception
    //   1833	1846	744	java/lang/Exception
    //   1850	1859	744	java/lang/Exception
    //   1863	1875	744	java/lang/Exception
    //   1879	1886	744	java/lang/Exception
    //   1890	1897	744	java/lang/Exception
    //   1901	1907	744	java/lang/Exception
    //   1911	1918	744	java/lang/Exception
    //   1922	1931	744	java/lang/Exception
    //   1940	1951	744	java/lang/Exception
    //   1959	1967	744	java/lang/Exception
    //   1976	1984	744	java/lang/Exception
    //   1988	1999	744	java/lang/Exception
    //   2003	2010	744	java/lang/Exception
    //   2021	2030	744	java/lang/Exception
    //   2037	2071	744	java/lang/Exception
    //   2092	2105	744	java/lang/Exception
    //   2109	2118	744	java/lang/Exception
    //   2122	2134	744	java/lang/Exception
    //   2138	2151	744	java/lang/Exception
    //   2155	2168	744	java/lang/Exception
    //   2172	2185	744	java/lang/Exception
    //   2189	2202	744	java/lang/Exception
    //   2206	2217	744	java/lang/Exception
    //   2221	2232	744	java/lang/Exception
    //   2236	2244	744	java/lang/Exception
    //   2248	2259	744	java/lang/Exception
    //   2263	2274	744	java/lang/Exception
    //   2278	2286	744	java/lang/Exception
    //   2290	2298	744	java/lang/Exception
    //   2307	2318	744	java/lang/Exception
    //   2333	2342	744	java/lang/Exception
    //   2363	2376	744	java/lang/Exception
    //   2380	2389	744	java/lang/Exception
    //   2393	2405	744	java/lang/Exception
    //   2409	2416	744	java/lang/Exception
    //   2420	2428	744	java/lang/Exception
    //   2432	2439	744	java/lang/Exception
    //   2443	2450	744	java/lang/Exception
    //   2454	2464	744	java/lang/Exception
    //   2473	2484	744	java/lang/Exception
    //   2497	2505	744	java/lang/Exception
    //   2514	2522	744	java/lang/Exception
    //   2526	2534	744	java/lang/Exception
    //   2538	2551	744	java/lang/Exception
    //   2584	2592	744	java/lang/Exception
    //   2611	2624	744	java/lang/Exception
    //   2628	2643	744	java/lang/Exception
    //   2647	2655	744	java/lang/Exception
    //   2659	2672	744	java/lang/Exception
    //   2676	2687	744	java/lang/Exception
    //   2691	2707	744	java/lang/Exception
    //   2726	2735	744	java/lang/Exception
    //   2742	2750	744	java/lang/Exception
    //   2760	2768	744	java/lang/Exception
    //   2779	2789	744	java/lang/Exception
    //   2796	2803	744	java/lang/Exception
    //   2810	2818	744	java/lang/Exception
    //   2822	2835	744	java/lang/Exception
    //   2844	2852	744	java/lang/Exception
    //   2856	2868	744	java/lang/Exception
    //   2872	2882	744	java/lang/Exception
    //   2901	2913	744	java/lang/Exception
    //   2932	2944	744	java/lang/Exception
    //   2963	2971	744	java/lang/Exception
    //   2975	2989	744	java/lang/Exception
    //   2996	3018	744	java/lang/Exception
    //   3025	3031	744	java/lang/Exception
    //   3035	3042	744	java/lang/Exception
    //   3046	3054	744	java/lang/Exception
    //   3061	3067	744	java/lang/Exception
    //   3074	3087	744	java/lang/Exception
    //   3094	3103	744	java/lang/Exception
    //   3203	3216	744	java/lang/Exception
    //   3226	3234	744	java/lang/Exception
    //   3238	3249	744	java/lang/Exception
    //   3253	3259	744	java/lang/Exception
    //   3263	3269	744	java/lang/Exception
    //   3273	3286	744	java/lang/Exception
    //   3534	3544	744	java/lang/Exception
    //   3557	3569	744	java/lang/Exception
    //   3577	3586	744	java/lang/Exception
    //   3595	3602	744	java/lang/Exception
    //   3606	3612	744	java/lang/Exception
    //   3623	3635	744	java/lang/Exception
    //   3639	3650	744	java/lang/Exception
    //   3654	3667	744	java/lang/Exception
    //   3671	3681	744	java/lang/Exception
    //   3689	3697	744	java/lang/Exception
    //   3701	3709	744	java/lang/Exception
    //   3723	3742	744	java/lang/Exception
    //   3746	3766	744	java/lang/Exception
    //   3770	3777	744	java/lang/Exception
    //   3781	3787	744	java/lang/Exception
    //   3791	3808	744	java/lang/Exception
    //   3819	3832	744	java/lang/Exception
    //   3843	3851	744	java/lang/Exception
    //   3865	3884	744	java/lang/Exception
    //   3888	3908	744	java/lang/Exception
    //   3912	3919	744	java/lang/Exception
    //   3923	3929	744	java/lang/Exception
    //   3933	3950	744	java/lang/Exception
    //   1129	1181	8815	java/lang/Exception
    //   3103	3117	8896	java/lang/Exception
    //   3117	3156	8896	java/lang/Exception
    //   3161	3169	8896	java/lang/Exception
    //   3172	3194	8896	java/lang/Exception
    //   3286	3316	8940	java/lang/Exception
    //   3316	3388	8940	java/lang/Exception
    //   3393	3465	8940	java/lang/Exception
    //   3478	3494	8940	java/lang/Exception
    //   3497	3521	8940	java/lang/Exception
    //   3966	4002	8940	java/lang/Exception
    //   4003	4017	8940	java/lang/Exception
    //   4025	4083	8940	java/lang/Exception
    //   4083	4098	8940	java/lang/Exception
    //   4103	4122	8940	java/lang/Exception
    //   4127	4132	8940	java/lang/Exception
    //   4137	4162	8940	java/lang/Exception
    //   4162	4170	8940	java/lang/Exception
    //   4175	4181	8940	java/lang/Exception
    //   4182	4212	8940	java/lang/Exception
    //   4217	4243	8940	java/lang/Exception
    //   4252	4288	8940	java/lang/Exception
    //   4295	4333	8940	java/lang/Exception
    //   4333	4348	8940	java/lang/Exception
    //   4353	4396	8940	java/lang/Exception
    //   4396	4414	8940	java/lang/Exception
    //   4419	4425	8940	java/lang/Exception
    //   4426	4456	8940	java/lang/Exception
    //   4459	4485	8940	java/lang/Exception
    //   4488	4499	8940	java/lang/Exception
    //   4521	4568	8940	java/lang/Exception
    //   4568	4607	8940	java/lang/Exception
    //   4615	4633	8940	java/lang/Exception
    //   4636	4661	8940	java/lang/Exception
    //   4670	4678	8940	java/lang/Exception
    //   4681	4711	8940	java/lang/Exception
    //   4711	4743	8940	java/lang/Exception
    //   4748	4773	8940	java/lang/Exception
    //   4779	4805	8940	java/lang/Exception
    //   4811	4842	8940	java/lang/Exception
    //   4842	4869	8940	java/lang/Exception
    //   4883	4912	8940	java/lang/Exception
    //   4912	4926	8940	java/lang/Exception
    //   4926	4999	8940	java/lang/Exception
    //   5002	5010	8940	java/lang/Exception
    //   5013	5046	8940	java/lang/Exception
    //   5046	5075	8940	java/lang/Exception
    //   5090	5125	8940	java/lang/Exception
    //   5131	5141	8940	java/lang/Exception
    //   5151	5170	8940	java/lang/Exception
    //   5175	5203	8940	java/lang/Exception
    //   5209	5234	8940	java/lang/Exception
    //   5240	5273	8940	java/lang/Exception
    //   5273	5279	8940	java/lang/Exception
    //   5282	5315	8940	java/lang/Exception
    //   5315	5359	8940	java/lang/Exception
    //   5362	5370	8940	java/lang/Exception
    //   5373	5430	8940	java/lang/Exception
    //   5430	5435	8940	java/lang/Exception
    //   5453	5506	8940	java/lang/Exception
    //   5506	5538	8940	java/lang/Exception
    //   5544	5577	8940	java/lang/Exception
    //   5577	5606	8940	java/lang/Exception
    //   5614	5642	8940	java/lang/Exception
    //   5646	5652	8940	java/lang/Exception
    //   5663	5669	8940	java/lang/Exception
    //   5678	5687	8940	java/lang/Exception
    //   5688	5757	8940	java/lang/Exception
    //   5757	5773	8940	java/lang/Exception
    //   5778	5800	8940	java/lang/Exception
    //   5804	5810	8940	java/lang/Exception
    //   5819	5833	8940	java/lang/Exception
    //   5834	5843	8940	java/lang/Exception
    //   5850	5865	8940	java/lang/Exception
    //   5866	5875	8940	java/lang/Exception
    //   5883	5892	8940	java/lang/Exception
    //   5900	5910	8940	java/lang/Exception
    //   5914	5919	8940	java/lang/Exception
    //   5920	5930	8940	java/lang/Exception
    //   5938	5953	8940	java/lang/Exception
    //   5954	5963	8940	java/lang/Exception
    //   5964	5994	8940	java/lang/Exception
    //   5999	6025	8940	java/lang/Exception
    //   6030	6066	8940	java/lang/Exception
    //   6066	6078	8940	java/lang/Exception
    //   6083	6121	8940	java/lang/Exception
    //   6121	6137	8940	java/lang/Exception
    //   6143	6218	8940	java/lang/Exception
    //   6218	6264	8940	java/lang/Exception
    //   6269	6275	8940	java/lang/Exception
    //   6276	6306	8940	java/lang/Exception
    //   6309	6335	8940	java/lang/Exception
    //   6338	6349	8940	java/lang/Exception
    //   6352	6433	8940	java/lang/Exception
    //   6433	6462	8940	java/lang/Exception
    //   6462	6570	8940	java/lang/Exception
    //   6575	6600	8940	java/lang/Exception
    //   6600	6605	8940	java/lang/Exception
    //   6608	6621	8940	java/lang/Exception
    //   6624	6658	8940	java/lang/Exception
    //   6661	6674	8940	java/lang/Exception
    //   6677	6703	8940	java/lang/Exception
    //   6706	6786	8940	java/lang/Exception
    //   6795	6864	8940	java/lang/Exception
    //   6864	6883	8940	java/lang/Exception
    //   6883	6910	8940	java/lang/Exception
    //   6913	6980	8940	java/lang/Exception
    //   6980	7074	8940	java/lang/Exception
    //   7077	7090	8940	java/lang/Exception
    //   7093	7154	8940	java/lang/Exception
    //   7157	7170	8940	java/lang/Exception
    //   7173	7221	8940	java/lang/Exception
    //   7224	7237	8940	java/lang/Exception
    //   7240	7308	8940	java/lang/Exception
    //   7318	7395	8940	java/lang/Exception
    //   7398	7542	8940	java/lang/Exception
    //   7542	7560	8940	java/lang/Exception
    //   7563	7578	8940	java/lang/Exception
    //   7581	7644	8940	java/lang/Exception
    //   7644	7716	8940	java/lang/Exception
    //   7716	7785	8940	java/lang/Exception
    //   7790	7815	8940	java/lang/Exception
    //   7815	7826	8940	java/lang/Exception
    //   7829	7858	8940	java/lang/Exception
    //   7861	7951	8940	java/lang/Exception
    //   7954	7983	8940	java/lang/Exception
    //   7986	8054	8940	java/lang/Exception
    //   8064	8157	8940	java/lang/Exception
    //   8157	8230	8940	java/lang/Exception
    //   8230	8260	8940	java/lang/Exception
    //   8260	8265	8940	java/lang/Exception
    //   8268	8297	8940	java/lang/Exception
    //   8300	8324	8940	java/lang/Exception
    //   8327	8372	8940	java/lang/Exception
    //   8372	8399	8940	java/lang/Exception
    //   8402	8413	8940	java/lang/Exception
    //   8422	8493	8940	java/lang/Exception
    //   8497	8508	8940	java/lang/Exception
    //   8508	8563	8940	java/lang/Exception
    //   8563	8603	8940	java/lang/Exception
    //   8603	8611	8940	java/lang/Exception
    //   8612	8623	8940	java/lang/Exception
    //   8626	8645	8940	java/lang/Exception
    //   8655	8678	8940	java/lang/Exception
    //   8683	8702	8940	java/lang/Exception
    //   8702	8754	8940	java/lang/Exception
    //   8754	8790	8940	java/lang/Exception
    //   8795	8806	8940	java/lang/Exception
    //   8806	8814	8940	java/lang/Exception
  }
  
  private void updateMediaPaths(MessageObject paramMessageObject, TLRPC.Message paramMessage, String paramString, boolean paramBoolean)
  {
    TLRPC.Message localMessage = paramMessageObject.messageOwner;
    if (paramMessage == null) {}
    label198:
    label269:
    label272:
    label342:
    label1000:
    label1258:
    label1348:
    label1605:
    do
    {
      Object localObject2;
      do
      {
        return;
        int i;
        String str;
        Object localObject3;
        if (((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && (paramMessage.media.photo != null) && ((localMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && (localMessage.media.photo != null))
        {
          MessagesStorage.getInstance().putSentFile(paramString, paramMessage.media.photo, 0);
          if ((localMessage.media.photo.sizes.size() == 1) && ((((TLRPC.PhotoSize)localMessage.media.photo.sizes.get(0)).location instanceof TLRPC.TL_fileLocationUnavailable)))
          {
            localMessage.media.photo.sizes = paramMessage.media.photo.sizes;
            paramMessage.message = localMessage.message;
            paramMessage.attachPath = localMessage.attachPath;
            localMessage.media.photo.id = paramMessage.media.photo.id;
            localMessage.media.photo.access_hash = paramMessage.media.photo.access_hash;
            return;
          }
          i = 0;
          if (i < paramMessage.media.photo.sizes.size())
          {
            paramString = (TLRPC.PhotoSize)paramMessage.media.photo.sizes.get(i);
            if ((paramString != null) && (paramString.location != null) && (!(paramString instanceof TLRPC.TL_photoSizeEmpty)) && (paramString.type != null)) {
              break label269;
            }
          }
          do
          {
            i += 1;
            break label198;
            break;
            int j = 0;
            if (j < localMessage.media.photo.sizes.size())
            {
              localObject1 = (TLRPC.PhotoSize)localMessage.media.photo.sizes.get(j);
              if ((localObject1 != null) && (((TLRPC.PhotoSize)localObject1).location != null) && (((TLRPC.PhotoSize)localObject1).type != null)) {
                break label342;
              }
            }
            while (((((TLRPC.PhotoSize)localObject1).location.volume_id != -2147483648L) || (!paramString.type.equals(((TLRPC.PhotoSize)localObject1).type))) && ((paramString.w != ((TLRPC.PhotoSize)localObject1).w) || (paramString.h != ((TLRPC.PhotoSize)localObject1).h)))
            {
              j += 1;
              break label272;
              break;
            }
            localObject2 = ((TLRPC.PhotoSize)localObject1).location.volume_id + "_" + ((TLRPC.PhotoSize)localObject1).location.local_id;
            str = paramString.location.volume_id + "_" + paramString.location.local_id;
          } while (((String)localObject2).equals(str));
          localObject3 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject2 + ".jpg");
          if ((paramMessage.media.photo.sizes.size() == 1) || (paramString.w > 90) || (paramString.h > 90)) {}
          for (paramMessageObject = FileLoader.getPathToAttach(paramString);; paramMessageObject = new File(FileLoader.getInstance().getDirectory(4), str + ".jpg"))
          {
            ((File)localObject3).renameTo(paramMessageObject);
            ImageLoader.getInstance().replaceImageInCache((String)localObject2, str, paramString.location, paramBoolean);
            ((TLRPC.PhotoSize)localObject1).location = paramString.location;
            ((TLRPC.PhotoSize)localObject1).size = paramString.size;
            break;
          }
        }
        if ((!(paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) || (paramMessage.media.document == null) || (!(localMessage.media instanceof TLRPC.TL_messageMediaDocument)) || (localMessage.media.document == null)) {
          break label1605;
        }
        if (MessageObject.isVideoMessage(paramMessage))
        {
          MessagesStorage.getInstance().putSentFile(paramString, paramMessage.media.document, 2);
          paramMessage.attachPath = localMessage.attachPath;
          localObject1 = localMessage.media.document.thumb;
          localObject2 = paramMessage.media.document.thumb;
          if ((localObject1 == null) || (((TLRPC.PhotoSize)localObject1).location == null) || (((TLRPC.PhotoSize)localObject1).location.volume_id != -2147483648L) || (localObject2 == null) || (((TLRPC.PhotoSize)localObject2).location == null) || ((localObject2 instanceof TLRPC.TL_photoSizeEmpty)) || ((localObject1 instanceof TLRPC.TL_photoSizeEmpty))) {
            break label1258;
          }
          str = ((TLRPC.PhotoSize)localObject1).location.volume_id + "_" + ((TLRPC.PhotoSize)localObject1).location.local_id;
          localObject3 = ((TLRPC.PhotoSize)localObject2).location.volume_id + "_" + ((TLRPC.PhotoSize)localObject2).location.local_id;
          if (!str.equals(localObject3))
          {
            new File(FileLoader.getInstance().getDirectory(4), str + ".jpg").renameTo(new File(FileLoader.getInstance().getDirectory(4), (String)localObject3 + ".jpg"));
            ImageLoader.getInstance().replaceImageInCache(str, (String)localObject3, ((TLRPC.PhotoSize)localObject2).location, paramBoolean);
            ((TLRPC.PhotoSize)localObject1).location = ((TLRPC.PhotoSize)localObject2).location;
            ((TLRPC.PhotoSize)localObject1).size = ((TLRPC.PhotoSize)localObject2).size;
          }
          localMessage.media.document.dc_id = paramMessage.media.document.dc_id;
          localMessage.media.document.id = paramMessage.media.document.id;
          localMessage.media.document.access_hash = paramMessage.media.document.access_hash;
          localObject2 = null;
          i = 0;
        }
        for (;;)
        {
          localObject1 = localObject2;
          if (i < localMessage.media.document.attributes.size())
          {
            localObject1 = (TLRPC.DocumentAttribute)localMessage.media.document.attributes.get(i);
            if ((localObject1 instanceof TLRPC.TL_documentAttributeAudio)) {
              localObject1 = ((TLRPC.DocumentAttribute)localObject1).waveform;
            }
          }
          else
          {
            localMessage.media.document.attributes = paramMessage.media.document.attributes;
            if (localObject1 == null) {
              break label1348;
            }
            i = 0;
            while (i < localMessage.media.document.attributes.size())
            {
              localObject2 = (TLRPC.DocumentAttribute)localMessage.media.document.attributes.get(i);
              if ((localObject2 instanceof TLRPC.TL_documentAttributeAudio))
              {
                ((TLRPC.DocumentAttribute)localObject2).waveform = ((byte[])localObject1);
                ((TLRPC.DocumentAttribute)localObject2).flags |= 0x4;
              }
              i += 1;
            }
            if (MessageObject.isVoiceMessage(paramMessage)) {
              break;
            }
            MessagesStorage.getInstance().putSentFile(paramString, paramMessage.media.document, 1);
            break;
            if ((localObject1 != null) && (MessageObject.isStickerMessage(paramMessage)) && (((TLRPC.PhotoSize)localObject1).location != null))
            {
              ((TLRPC.PhotoSize)localObject2).location = ((TLRPC.PhotoSize)localObject1).location;
              break label1000;
            }
            if (((localObject1 == null) || (!(((TLRPC.PhotoSize)localObject1).location instanceof TLRPC.TL_fileLocationUnavailable))) && (!(localObject1 instanceof TLRPC.TL_photoSizeEmpty))) {
              break label1000;
            }
            localMessage.media.document.thumb = paramMessage.media.document.thumb;
            break label1000;
          }
          i += 1;
        }
        localMessage.media.document.size = paramMessage.media.document.size;
        localMessage.media.document.mime_type = paramMessage.media.document.mime_type;
        if (((paramMessage.flags & 0x4) == 0) && (MessageObject.isOut(paramMessage)) && (MessageObject.isNewGifDocument(paramMessage.media.document))) {
          MessagesController.addNewGifToRecent(paramMessage.media.document, paramMessage.date);
        }
        if ((localMessage.attachPath == null) || (!localMessage.attachPath.startsWith(FileLoader.getInstance().getDirectory(4).getAbsolutePath()))) {
          break;
        }
        Object localObject1 = new File(localMessage.attachPath);
        localObject2 = FileLoader.getPathToAttach(paramMessage.media.document);
        if (!((File)localObject1).renameTo((File)localObject2))
        {
          paramMessage.attachPath = localMessage.attachPath;
          paramMessage.message = localMessage.message;
          return;
        }
        if (MessageObject.isVideoMessage(paramMessage))
        {
          paramMessageObject.attachPathExists = true;
          return;
        }
        paramMessageObject.mediaExists = paramMessageObject.attachPathExists;
        paramMessageObject.attachPathExists = false;
        localMessage.attachPath = "";
      } while ((paramString == null) || (!paramString.startsWith("http")));
      MessagesStorage.getInstance().addRecentLocalFile(paramString, ((File)localObject2).toString(), localMessage.media.document);
      return;
      paramMessage.attachPath = localMessage.attachPath;
      paramMessage.message = localMessage.message;
      return;
      if (((paramMessage.media instanceof TLRPC.TL_messageMediaContact)) && ((localMessage.media instanceof TLRPC.TL_messageMediaContact)))
      {
        localMessage.media = paramMessage.media;
        return;
      }
    } while (!(paramMessage.media instanceof TLRPC.TL_messageMediaWebPage));
    localMessage.media = paramMessage.media;
  }
  
  public void MihanProcessForwardFromMyName(CharSequence paramCharSequence, MessageObject paramMessageObject, long paramLong)
  {
    if (paramMessageObject == null) {
      return;
    }
    if ((paramMessageObject.messageOwner.media != null) && (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty)) && (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)))
    {
      if ((paramMessageObject.messageOwner.media.photo instanceof TLRPC.TL_photo))
      {
        MihanSendPhotoMessage(paramCharSequence, (TLRPC.TL_photo)paramMessageObject.messageOwner.media.photo, null, paramLong, paramMessageObject.replyMessageObject, null, null);
        return;
      }
      if ((paramMessageObject.messageOwner.media.document instanceof TLRPC.TL_document))
      {
        MihanSendDocumentMessage(paramCharSequence, (TLRPC.TL_document)paramMessageObject.messageOwner.media.document, null, paramMessageObject.messageOwner.attachPath, paramLong, paramMessageObject.replyMessageObject, null, null);
        return;
      }
      if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)))
      {
        sendMessage(paramMessageObject.messageOwner.media, paramLong, paramMessageObject.replyMessageObject, null, null);
        return;
      }
      if (paramMessageObject.messageOwner.media.phone_number != null)
      {
        paramCharSequence = new TLRPC.TL_userContact_old2();
        paramCharSequence.phone = paramMessageObject.messageOwner.media.phone_number;
        paramCharSequence.first_name = paramMessageObject.messageOwner.media.first_name;
        paramCharSequence.last_name = paramMessageObject.messageOwner.media.last_name;
        paramCharSequence.id = paramMessageObject.messageOwner.media.user_id;
        sendMessage(paramCharSequence, paramLong, paramMessageObject.replyMessageObject, null, null);
        return;
      }
      paramCharSequence = new ArrayList();
      paramCharSequence.add(paramMessageObject);
      sendMessage(paramCharSequence, paramLong);
      return;
    }
    if (paramMessageObject.messageOwner.message != null)
    {
      paramCharSequence = null;
      if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) {
        paramCharSequence = paramMessageObject.messageOwner.media.webpage;
      }
      sendMessage(paramMessageObject.messageOwner.message, paramLong, paramMessageObject.replyMessageObject, paramCharSequence, true, paramMessageObject.messageOwner.entities, null, null);
      return;
    }
    paramCharSequence = new ArrayList();
    paramCharSequence.add(paramMessageObject);
    sendMessage(paramCharSequence, paramLong);
  }
  
  public void MihanSendDocumentMessage(CharSequence paramCharSequence, TLRPC.TL_document paramTL_document, VideoEditedInfo paramVideoEditedInfo, String paramString, long paramLong, MessageObject paramMessageObject, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    MihanSendMessage(paramCharSequence, null, null, null, paramVideoEditedInfo, null, paramTL_document, paramLong, paramString, paramMessageObject, null, true, null, null, paramReplyMarkup, paramHashMap);
  }
  
  public void MihanSendPhotoMessage(CharSequence paramCharSequence, TLRPC.TL_photo paramTL_photo, String paramString, long paramLong, MessageObject paramMessageObject, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    MihanSendMessage(paramCharSequence, null, null, paramTL_photo, null, null, null, paramLong, paramString, paramMessageObject, null, true, null, null, paramReplyMarkup, paramHashMap);
  }
  
  public void cancelSendingMessage(MessageObject paramMessageObject)
  {
    Object localObject1 = null;
    boolean bool = false;
    Iterator localIterator = this.delayedMessages.entrySet().iterator();
    label154:
    while (localIterator.hasNext())
    {
      Object localObject2 = (Map.Entry)localIterator.next();
      ArrayList localArrayList = (ArrayList)((Map.Entry)localObject2).getValue();
      int i = 0;
      for (;;)
      {
        if (i >= localArrayList.size()) {
          break label154;
        }
        DelayedMessage localDelayedMessage = (DelayedMessage)localArrayList.get(i);
        if (localDelayedMessage.obj.getId() == paramMessageObject.getId())
        {
          localArrayList.remove(i);
          MediaController.getInstance().cancelVideoConvert(localDelayedMessage.obj);
          if (localArrayList.size() != 0) {
            break;
          }
          localObject2 = (String)((Map.Entry)localObject2).getKey();
          localObject1 = localObject2;
          if (localDelayedMessage.sendEncryptedRequest == null) {
            break;
          }
          bool = true;
          localObject1 = localObject2;
          break;
        }
        i += 1;
      }
    }
    if (localObject1 != null)
    {
      if (!((String)localObject1).startsWith("http")) {
        break label229;
      }
      ImageLoader.getInstance().cancelLoadHttpFile((String)localObject1);
    }
    for (;;)
    {
      stopVideoService((String)localObject1);
      localObject1 = new ArrayList();
      ((ArrayList)localObject1).add(Integer.valueOf(paramMessageObject.getId()));
      MessagesController.getInstance().deleteMessages((ArrayList)localObject1, null, null, paramMessageObject.messageOwner.to_id.channel_id);
      return;
      label229:
      FileLoader.getInstance().cancelUploadFile((String)localObject1, bool);
    }
  }
  
  public void checkUnsentMessages()
  {
    MessagesStorage.getInstance().getUnsentMessages(1000);
  }
  
  public void cleanup()
  {
    this.delayedMessages.clear();
    this.unsentMessages.clear();
    this.sendingMessages.clear();
    this.waitingForLocation.clear();
    this.waitingForCallback.clear();
    this.currentChatInfo = null;
    this.locationProvider.stop();
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    final Object localObject2;
    final Object localObject3;
    label99:
    label143:
    int i;
    label188:
    label426:
    long l;
    if (paramInt == NotificationCenter.FileDidUpload)
    {
      localObject2 = (String)paramVarArgs[0];
      localObject3 = (TLRPC.InputFile)paramVarArgs[1];
      TLRPC.InputEncryptedFile localInputEncryptedFile = (TLRPC.InputEncryptedFile)paramVarArgs[2];
      ArrayList localArrayList = (ArrayList)this.delayedMessages.get(localObject2);
      if (localArrayList != null)
      {
        paramInt = 0;
        if (paramInt < localArrayList.size())
        {
          DelayedMessage localDelayedMessage = (DelayedMessage)localArrayList.get(paramInt);
          localObject1 = null;
          if ((localDelayedMessage.sendRequest instanceof TLRPC.TL_messages_sendMedia))
          {
            localObject1 = ((TLRPC.TL_messages_sendMedia)localDelayedMessage.sendRequest).media;
            if ((localObject3 == null) || (localObject1 == null)) {
              break label426;
            }
            if (localDelayedMessage.type != 0) {
              break label188;
            }
            ((TLRPC.InputMedia)localObject1).file = ((TLRPC.InputFile)localObject3);
            performSendMessageRequest(localDelayedMessage.sendRequest, localDelayedMessage.obj, localDelayedMessage.originalPath);
            localArrayList.remove(paramInt);
            i = paramInt - 1;
          }
          for (;;)
          {
            paramInt = i + 1;
            break;
            if (!(localDelayedMessage.sendRequest instanceof TLRPC.TL_messages_sendBroadcast)) {
              break label99;
            }
            localObject1 = ((TLRPC.TL_messages_sendBroadcast)localDelayedMessage.sendRequest).media;
            break label99;
            if (localDelayedMessage.type == 1)
            {
              if (((TLRPC.InputMedia)localObject1).file == null)
              {
                ((TLRPC.InputMedia)localObject1).file = ((TLRPC.InputFile)localObject3);
                if ((((TLRPC.InputMedia)localObject1).thumb == null) && (localDelayedMessage.location != null))
                {
                  performSendDelayedMessage(localDelayedMessage);
                  break label143;
                }
                performSendMessageRequest(localDelayedMessage.sendRequest, localDelayedMessage.obj, localDelayedMessage.originalPath);
                break label143;
              }
              ((TLRPC.InputMedia)localObject1).thumb = ((TLRPC.InputFile)localObject3);
              performSendMessageRequest(localDelayedMessage.sendRequest, localDelayedMessage.obj, localDelayedMessage.originalPath);
              break label143;
            }
            if (localDelayedMessage.type == 2)
            {
              if (((TLRPC.InputMedia)localObject1).file == null)
              {
                ((TLRPC.InputMedia)localObject1).file = ((TLRPC.InputFile)localObject3);
                if ((((TLRPC.InputMedia)localObject1).thumb == null) && (localDelayedMessage.location != null))
                {
                  performSendDelayedMessage(localDelayedMessage);
                  break label143;
                }
                performSendMessageRequest(localDelayedMessage.sendRequest, localDelayedMessage.obj, localDelayedMessage.originalPath);
                break label143;
              }
              ((TLRPC.InputMedia)localObject1).thumb = ((TLRPC.InputFile)localObject3);
              performSendMessageRequest(localDelayedMessage.sendRequest, localDelayedMessage.obj, localDelayedMessage.originalPath);
              break label143;
            }
            if (localDelayedMessage.type != 3) {
              break label143;
            }
            ((TLRPC.InputMedia)localObject1).file = ((TLRPC.InputFile)localObject3);
            performSendMessageRequest(localDelayedMessage.sendRequest, localDelayedMessage.obj, localDelayedMessage.originalPath);
            break label143;
            i = paramInt;
            if (localInputEncryptedFile != null)
            {
              i = paramInt;
              if (localDelayedMessage.sendEncryptedRequest != null)
              {
                if (((localDelayedMessage.sendEncryptedRequest.media instanceof TLRPC.TL_decryptedMessageMediaVideo)) || ((localDelayedMessage.sendEncryptedRequest.media instanceof TLRPC.TL_decryptedMessageMediaPhoto)))
                {
                  l = ((Long)paramVarArgs[5]).longValue();
                  localDelayedMessage.sendEncryptedRequest.media.size = ((int)l);
                }
                localDelayedMessage.sendEncryptedRequest.media.key = ((byte[])paramVarArgs[3]);
                localDelayedMessage.sendEncryptedRequest.media.iv = ((byte[])paramVarArgs[4]);
                SecretChatHelper.getInstance().performSendEncryptedRequest(localDelayedMessage.sendEncryptedRequest, localDelayedMessage.obj.messageOwner, localDelayedMessage.encryptedChat, localInputEncryptedFile, localDelayedMessage.originalPath, localDelayedMessage.obj);
                localArrayList.remove(paramInt);
                i = paramInt - 1;
              }
            }
          }
        }
        if (localArrayList.isEmpty()) {
          this.delayedMessages.remove(localObject2);
        }
      }
    }
    label968:
    label1155:
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                boolean bool;
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        do
                        {
                          return;
                          if (paramInt != NotificationCenter.FileDidFailUpload) {
                            break;
                          }
                          localObject1 = (String)paramVarArgs[0];
                          bool = ((Boolean)paramVarArgs[1]).booleanValue();
                          paramVarArgs = (ArrayList)this.delayedMessages.get(localObject1);
                        } while (paramVarArgs == null);
                        for (paramInt = 0; paramInt < paramVarArgs.size(); paramInt = i + 1)
                        {
                          localObject2 = (DelayedMessage)paramVarArgs.get(paramInt);
                          if ((!bool) || (((DelayedMessage)localObject2).sendEncryptedRequest == null))
                          {
                            i = paramInt;
                            if (!bool)
                            {
                              i = paramInt;
                              if (((DelayedMessage)localObject2).sendRequest == null) {}
                            }
                          }
                          else
                          {
                            MessagesStorage.getInstance().markMessageAsSendError(((DelayedMessage)localObject2).obj.messageOwner);
                            ((DelayedMessage)localObject2).obj.messageOwner.send_state = 2;
                            paramVarArgs.remove(paramInt);
                            i = paramInt - 1;
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(((DelayedMessage)localObject2).obj.getId()) });
                            processSentMessage(((DelayedMessage)localObject2).obj.getId());
                          }
                        }
                      } while (!paramVarArgs.isEmpty());
                      this.delayedMessages.remove(localObject1);
                      return;
                      if (paramInt != NotificationCenter.FilePreparingStarted) {
                        break;
                      }
                      localObject1 = (MessageObject)paramVarArgs[0];
                      paramVarArgs = (String)paramVarArgs[1];
                      paramVarArgs = (ArrayList)this.delayedMessages.get(((MessageObject)localObject1).messageOwner.attachPath);
                    } while (paramVarArgs == null);
                    paramInt = 0;
                    for (;;)
                    {
                      if (paramInt < paramVarArgs.size())
                      {
                        localObject2 = (DelayedMessage)paramVarArgs.get(paramInt);
                        if (((DelayedMessage)localObject2).obj == localObject1)
                        {
                          ((DelayedMessage)localObject2).videoEditedInfo = null;
                          performSendDelayedMessage((DelayedMessage)localObject2);
                          paramVarArgs.remove(paramInt);
                        }
                      }
                      else
                      {
                        if (!paramVarArgs.isEmpty()) {
                          break;
                        }
                        this.delayedMessages.remove(((MessageObject)localObject1).messageOwner.attachPath);
                        return;
                      }
                      paramInt += 1;
                    }
                    if (paramInt != NotificationCenter.FileNewChunkAvailable) {
                      break label1155;
                    }
                    localObject1 = (MessageObject)paramVarArgs[0];
                    localObject2 = (String)paramVarArgs[1];
                    l = ((Long)paramVarArgs[2]).longValue();
                    if ((int)((MessageObject)localObject1).getDialogId() != 0) {
                      break;
                    }
                    bool = true;
                    FileLoader.getInstance().checkUploadNewDataAvailable((String)localObject2, bool, l);
                  } while (l == 0L);
                  paramVarArgs = (ArrayList)this.delayedMessages.get(((MessageObject)localObject1).messageOwner.attachPath);
                } while (paramVarArgs == null);
                paramInt = 0;
                for (;;)
                {
                  if (paramInt < paramVarArgs.size())
                  {
                    localObject2 = (DelayedMessage)paramVarArgs.get(paramInt);
                    if (((DelayedMessage)localObject2).obj == localObject1)
                    {
                      ((DelayedMessage)localObject2).obj.videoEditedInfo = null;
                      ((DelayedMessage)localObject2).obj.messageOwner.message = "-1";
                      ((DelayedMessage)localObject2).obj.messageOwner.media.document.size = ((int)l);
                      localObject3 = new ArrayList();
                      ((ArrayList)localObject3).add(((DelayedMessage)localObject2).obj.messageOwner);
                      MessagesStorage.getInstance().putMessages((ArrayList)localObject3, false, true, false, 0);
                    }
                  }
                  else
                  {
                    if (!paramVarArgs.isEmpty()) {
                      break;
                    }
                    this.delayedMessages.remove(((MessageObject)localObject1).messageOwner.attachPath);
                    return;
                    bool = false;
                    break label968;
                  }
                  paramInt += 1;
                }
                if (paramInt != NotificationCenter.FilePreparingFailed) {
                  break;
                }
                localObject1 = (MessageObject)paramVarArgs[0];
                paramVarArgs = (String)paramVarArgs[1];
                stopVideoService(((MessageObject)localObject1).messageOwner.attachPath);
                localObject2 = (ArrayList)this.delayedMessages.get(paramVarArgs);
              } while (localObject2 == null);
              for (paramInt = 0; paramInt < ((ArrayList)localObject2).size(); paramInt = i + 1)
              {
                localObject3 = (DelayedMessage)((ArrayList)localObject2).get(paramInt);
                i = paramInt;
                if (((DelayedMessage)localObject3).obj == localObject1)
                {
                  MessagesStorage.getInstance().markMessageAsSendError(((DelayedMessage)localObject3).obj.messageOwner);
                  ((DelayedMessage)localObject3).obj.messageOwner.send_state = 2;
                  ((ArrayList)localObject2).remove(paramInt);
                  i = paramInt - 1;
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(((DelayedMessage)localObject3).obj.getId()) });
                  processSentMessage(((DelayedMessage)localObject3).obj.getId());
                }
              }
            } while (!((ArrayList)localObject2).isEmpty());
            this.delayedMessages.remove(paramVarArgs);
            return;
            if (paramInt != NotificationCenter.httpFileDidLoaded) {
              break;
            }
            paramVarArgs = (String)paramVarArgs[0];
            localObject1 = (ArrayList)this.delayedMessages.get(paramVarArgs);
          } while (localObject1 == null);
          paramInt = 0;
          if (paramInt < ((ArrayList)localObject1).size())
          {
            localObject2 = (DelayedMessage)((ArrayList)localObject1).get(paramInt);
            if (((DelayedMessage)localObject2).type == 0)
            {
              localObject3 = Utilities.MD5(((DelayedMessage)localObject2).httpLocation) + "." + ImageLoader.getHttpUrlExtension(((DelayedMessage)localObject2).httpLocation, "file");
              localObject3 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject3);
              Utilities.globalQueue.postRunnable(new Runnable()
              {
                public void run()
                {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      if (this.val$photo != null)
                      {
                        SendMessagesHelper.2.this.val$message.httpLocation = null;
                        SendMessagesHelper.2.this.val$message.obj.messageOwner.media.photo = this.val$photo;
                        SendMessagesHelper.2.this.val$message.obj.messageOwner.attachPath = SendMessagesHelper.2.this.val$cacheFile.toString();
                        SendMessagesHelper.2.this.val$message.location = ((TLRPC.PhotoSize)this.val$photo.sizes.get(this.val$photo.sizes.size() - 1)).location;
                        ArrayList localArrayList = new ArrayList();
                        localArrayList.add(SendMessagesHelper.2.this.val$message.obj.messageOwner);
                        MessagesStorage.getInstance().putMessages(localArrayList, false, true, false, 0);
                        SendMessagesHelper.this.performSendDelayedMessage(SendMessagesHelper.2.this.val$message);
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateMessageMedia, new Object[] { SendMessagesHelper.2.this.val$message.obj });
                        return;
                      }
                      FileLog.e("tmessages", "can't load image " + SendMessagesHelper.2.this.val$message.httpLocation + " to file " + SendMessagesHelper.2.this.val$cacheFile.toString());
                      MessagesStorage.getInstance().markMessageAsSendError(SendMessagesHelper.2.this.val$message.obj.messageOwner);
                      SendMessagesHelper.2.this.val$message.obj.messageOwner.send_state = 2;
                      NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(SendMessagesHelper.2.this.val$message.obj.getId()) });
                      SendMessagesHelper.this.processSentMessage(SendMessagesHelper.2.this.val$message.obj.getId());
                    }
                  });
                }
              });
            }
            for (;;)
            {
              paramInt += 1;
              break;
              if (((DelayedMessage)localObject2).type == 2)
              {
                localObject3 = Utilities.MD5(((DelayedMessage)localObject2).httpLocation) + ".gif";
                localObject3 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject3);
                Utilities.globalQueue.postRunnable(new Runnable()
                {
                  public void run()
                  {
                    boolean bool = true;
                    if ((localObject2.documentLocation.thumb.location instanceof TLRPC.TL_fileLocationUnavailable)) {}
                    for (;;)
                    {
                      try
                      {
                        Bitmap localBitmap = ImageLoader.loadBitmap(localObject3.getAbsolutePath(), null, 90.0F, 90.0F, true);
                        if (localBitmap != null)
                        {
                          TLRPC.TL_document localTL_document = localObject2.documentLocation;
                          if (localObject2.sendEncryptedRequest == null) {
                            continue;
                          }
                          localTL_document.thumb = ImageLoader.scaleAndSaveImage(localBitmap, 90.0F, 90.0F, 55, bool);
                          localBitmap.recycle();
                        }
                      }
                      catch (Exception localException)
                      {
                        localObject2.documentLocation.thumb = null;
                        FileLog.e("tmessages", localException);
                        continue;
                      }
                      if (localObject2.documentLocation.thumb == null)
                      {
                        localObject2.documentLocation.thumb = new TLRPC.TL_photoSizeEmpty();
                        localObject2.documentLocation.thumb.type = "s";
                      }
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          SendMessagesHelper.3.this.val$message.httpLocation = null;
                          SendMessagesHelper.3.this.val$message.obj.messageOwner.attachPath = SendMessagesHelper.3.this.val$cacheFile.toString();
                          SendMessagesHelper.3.this.val$message.location = SendMessagesHelper.3.this.val$message.documentLocation.thumb.location;
                          ArrayList localArrayList = new ArrayList();
                          localArrayList.add(SendMessagesHelper.3.this.val$message.obj.messageOwner);
                          MessagesStorage.getInstance().putMessages(localArrayList, false, true, false, 0);
                          SendMessagesHelper.this.performSendDelayedMessage(SendMessagesHelper.3.this.val$message);
                          NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateMessageMedia, new Object[] { SendMessagesHelper.3.this.val$message.obj });
                        }
                      });
                      return;
                      bool = false;
                    }
                  }
                });
              }
            }
          }
          this.delayedMessages.remove(paramVarArgs);
          return;
          if (paramInt != NotificationCenter.FileDidLoaded) {
            break;
          }
          paramVarArgs = (String)paramVarArgs[0];
          localObject1 = (ArrayList)this.delayedMessages.get(paramVarArgs);
        } while (localObject1 == null);
        paramInt = 0;
        while (paramInt < ((ArrayList)localObject1).size())
        {
          performSendDelayedMessage((DelayedMessage)((ArrayList)localObject1).get(paramInt));
          paramInt += 1;
        }
        this.delayedMessages.remove(paramVarArgs);
        return;
      } while ((paramInt != NotificationCenter.httpFileDidFailedLoad) && (paramInt != NotificationCenter.FileDidFailedLoad));
      paramVarArgs = (String)paramVarArgs[0];
      localObject1 = (ArrayList)this.delayedMessages.get(paramVarArgs);
    } while (localObject1 == null);
    Object localObject1 = ((ArrayList)localObject1).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (DelayedMessage)((Iterator)localObject1).next();
      MessagesStorage.getInstance().markMessageAsSendError(((DelayedMessage)localObject2).obj.messageOwner);
      ((DelayedMessage)localObject2).obj.messageOwner.send_state = 2;
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(((DelayedMessage)localObject2).obj.getId()) });
      processSentMessage(((DelayedMessage)localObject2).obj.getId());
    }
    this.delayedMessages.remove(paramVarArgs);
  }
  
  public int editMessage(MessageObject paramMessageObject, String paramString, boolean paramBoolean, final BaseFragment paramBaseFragment, ArrayList<TLRPC.MessageEntity> paramArrayList, final Runnable paramRunnable)
  {
    boolean bool = false;
    if ((paramBaseFragment == null) || (paramBaseFragment.getParentActivity() == null) || (paramRunnable == null)) {
      return 0;
    }
    TLRPC.TL_messages_editMessage localTL_messages_editMessage = new TLRPC.TL_messages_editMessage();
    localTL_messages_editMessage.peer = MessagesController.getInputPeer((int)paramMessageObject.getDialogId());
    localTL_messages_editMessage.message = paramString;
    localTL_messages_editMessage.flags |= 0x800;
    localTL_messages_editMessage.id = paramMessageObject.getId();
    if (!paramBoolean) {
      bool = true;
    }
    localTL_messages_editMessage.no_webpage = bool;
    if (paramArrayList != null)
    {
      localTL_messages_editMessage.entities = paramArrayList;
      localTL_messages_editMessage.flags |= 0x8;
    }
    ConnectionsManager.getInstance().sendRequest(localTL_messages_editMessage, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            SendMessagesHelper.5.this.val$callback.run();
          }
        });
        if (paramAnonymousTL_error == null) {
          MessagesController.getInstance().processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
        }
        while (paramAnonymousTL_error.text.equals("MESSAGE_NOT_MODIFIED")) {
          return;
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(SendMessagesHelper.5.this.val$fragment.getParentActivity());
            localBuilder.setTitle(LocaleController.getString("AppName", 2131165338));
            localBuilder.setMessage(LocaleController.getString("EditMessageError", 2131165638));
            localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166111), null);
            SendMessagesHelper.5.this.val$fragment.showDialog(localBuilder.create());
          }
        });
      }
    });
  }
  
  public TLRPC.TL_photo generatePhotoSizes(String paramString, Uri paramUri)
  {
    Bitmap localBitmap2 = ImageLoader.loadBitmap(paramString, paramUri, AndroidUtilities.getPhotoSize(), AndroidUtilities.getPhotoSize(), true);
    Bitmap localBitmap1 = localBitmap2;
    if (localBitmap2 == null)
    {
      localBitmap1 = localBitmap2;
      if (AndroidUtilities.getPhotoSize() != 800) {
        localBitmap1 = ImageLoader.loadBitmap(paramString, paramUri, 800.0F, 800.0F, true);
      }
    }
    paramString = new ArrayList();
    paramUri = ImageLoader.scaleAndSaveImage(localBitmap1, 90.0F, 90.0F, 55, true);
    if (paramUri != null) {
      paramString.add(paramUri);
    }
    paramUri = ImageLoader.scaleAndSaveImage(localBitmap1, AndroidUtilities.getPhotoSize(), AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
    if (paramUri != null) {
      paramString.add(paramUri);
    }
    if (localBitmap1 != null) {
      localBitmap1.recycle();
    }
    if (paramString.isEmpty()) {
      return null;
    }
    UserConfig.saveConfig(false);
    paramUri = new TLRPC.TL_photo();
    paramUri.date = ConnectionsManager.getInstance().getCurrentTime();
    paramUri.sizes = paramString;
    return paramUri;
  }
  
  protected ArrayList<DelayedMessage> getDelayedMessages(String paramString)
  {
    return (ArrayList)this.delayedMessages.get(paramString);
  }
  
  protected long getNextRandomId()
  {
    for (long l = 0L; l == 0L; l = Utilities.random.nextLong()) {}
    return l;
  }
  
  public boolean isSendingCallback(MessageObject paramMessageObject, TLRPC.KeyboardButton paramKeyboardButton)
  {
    return (paramMessageObject != null) && (paramKeyboardButton != null) && (this.waitingForCallback.containsKey(paramMessageObject.getId() + "_" + Utilities.bytesToHex(paramKeyboardButton.data)));
  }
  
  public boolean isSendingCurrentLocation(MessageObject paramMessageObject, TLRPC.KeyboardButton paramKeyboardButton)
  {
    return (paramMessageObject != null) && (paramKeyboardButton != null) && (this.waitingForLocation.containsKey(paramMessageObject.getId() + "_" + Utilities.bytesToHex(paramKeyboardButton.data)));
  }
  
  public boolean isSendingMessage(int paramInt)
  {
    return this.sendingMessages.containsKey(Integer.valueOf(paramInt));
  }
  
  public void processForwardFromMyName(MessageObject paramMessageObject, long paramLong)
  {
    if (paramMessageObject == null) {
      return;
    }
    if ((paramMessageObject.messageOwner.media != null) && (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty)) && (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)))
    {
      if ((paramMessageObject.messageOwner.media.photo instanceof TLRPC.TL_photo))
      {
        sendMessage((TLRPC.TL_photo)paramMessageObject.messageOwner.media.photo, null, paramLong, paramMessageObject.replyMessageObject, null, null);
        return;
      }
      if ((paramMessageObject.messageOwner.media.document instanceof TLRPC.TL_document))
      {
        sendMessage((TLRPC.TL_document)paramMessageObject.messageOwner.media.document, null, paramMessageObject.messageOwner.attachPath, paramLong, paramMessageObject.replyMessageObject, null, null);
        return;
      }
      if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)))
      {
        sendMessage(paramMessageObject.messageOwner.media, paramLong, paramMessageObject.replyMessageObject, null, null);
        return;
      }
      if (paramMessageObject.messageOwner.media.phone_number != null)
      {
        localObject = new TLRPC.TL_userContact_old2();
        ((TLRPC.User)localObject).phone = paramMessageObject.messageOwner.media.phone_number;
        ((TLRPC.User)localObject).first_name = paramMessageObject.messageOwner.media.first_name;
        ((TLRPC.User)localObject).last_name = paramMessageObject.messageOwner.media.last_name;
        ((TLRPC.User)localObject).id = paramMessageObject.messageOwner.media.user_id;
        sendMessage((TLRPC.User)localObject, paramLong, paramMessageObject.replyMessageObject, null, null);
        return;
      }
      localObject = new ArrayList();
      ((ArrayList)localObject).add(paramMessageObject);
      sendMessage((ArrayList)localObject, paramLong);
      return;
    }
    if (paramMessageObject.messageOwner.message != null)
    {
      localObject = null;
      if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) {
        localObject = paramMessageObject.messageOwner.media.webpage;
      }
      sendMessage(paramMessageObject.messageOwner.message, paramLong, paramMessageObject.replyMessageObject, (TLRPC.WebPage)localObject, true, paramMessageObject.messageOwner.entities, null, null);
      return;
    }
    Object localObject = new ArrayList();
    ((ArrayList)localObject).add(paramMessageObject);
    sendMessage((ArrayList)localObject, paramLong);
  }
  
  protected void processSentMessage(int paramInt)
  {
    int i = this.unsentMessages.size();
    this.unsentMessages.remove(Integer.valueOf(paramInt));
    if ((i != 0) && (this.unsentMessages.size() == 0)) {
      checkUnsentMessages();
    }
  }
  
  protected void processUnsentMessages(final ArrayList<TLRPC.Message> paramArrayList, final ArrayList<TLRPC.User> paramArrayList1, final ArrayList<TLRPC.Chat> paramArrayList2, final ArrayList<TLRPC.EncryptedChat> paramArrayList3)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        MessagesController.getInstance().putUsers(paramArrayList1, true);
        MessagesController.getInstance().putChats(paramArrayList2, true);
        MessagesController.getInstance().putEncryptedChats(paramArrayList3, true);
        int i = 0;
        while (i < paramArrayList.size())
        {
          MessageObject localMessageObject = new MessageObject((TLRPC.Message)paramArrayList.get(i), null, false);
          SendMessagesHelper.this.retrySendMessage(localMessageObject, true);
          i += 1;
        }
      }
    });
  }
  
  protected void putToSendingMessages(TLRPC.Message paramMessage)
  {
    this.sendingMessages.put(Integer.valueOf(paramMessage.id), paramMessage);
  }
  
  protected void removeFromSendingMessages(int paramInt)
  {
    this.sendingMessages.remove(Integer.valueOf(paramInt));
  }
  
  public boolean retrySendMessage(MessageObject paramMessageObject, boolean paramBoolean)
  {
    if (paramMessageObject.getId() >= 0) {
      return false;
    }
    if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageEncryptedAction))
    {
      int i = (int)(paramMessageObject.getDialogId() >> 32);
      TLRPC.EncryptedChat localEncryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(i));
      if (localEncryptedChat == null)
      {
        MessagesStorage.getInstance().markMessageAsSendError(paramMessageObject.messageOwner);
        paramMessageObject.messageOwner.send_state = 2;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(paramMessageObject.getId()) });
        processSentMessage(paramMessageObject.getId());
        return false;
      }
      if (paramMessageObject.messageOwner.random_id == 0L) {
        paramMessageObject.messageOwner.random_id = getNextRandomId();
      }
      if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)) {
        SecretChatHelper.getInstance().sendTTLMessage(localEncryptedChat, paramMessageObject.messageOwner);
      }
      for (;;)
      {
        return true;
        if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionDeleteMessages)) {
          SecretChatHelper.getInstance().sendMessagesDeleteMessage(localEncryptedChat, null, paramMessageObject.messageOwner);
        } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionFlushHistory)) {
          SecretChatHelper.getInstance().sendClearHistoryMessage(localEncryptedChat, paramMessageObject.messageOwner);
        } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionNotifyLayer)) {
          SecretChatHelper.getInstance().sendNotifyLayerMessage(localEncryptedChat, paramMessageObject.messageOwner);
        } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionReadMessages)) {
          SecretChatHelper.getInstance().sendMessagesReadMessage(localEncryptedChat, null, paramMessageObject.messageOwner);
        } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)) {
          SecretChatHelper.getInstance().sendScreenshotMessage(localEncryptedChat, null, paramMessageObject.messageOwner);
        } else if ((!(paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionTyping)) && (!(paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionResend))) {
          if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionCommitKey)) {
            SecretChatHelper.getInstance().sendCommitKeyMessage(localEncryptedChat, paramMessageObject.messageOwner);
          } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionAbortKey)) {
            SecretChatHelper.getInstance().sendAbortKeyMessage(localEncryptedChat, paramMessageObject.messageOwner, 0L);
          } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionRequestKey)) {
            SecretChatHelper.getInstance().sendRequestKeyMessage(localEncryptedChat, paramMessageObject.messageOwner);
          } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionAcceptKey)) {
            SecretChatHelper.getInstance().sendAcceptKeyMessage(localEncryptedChat, paramMessageObject.messageOwner);
          } else if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionNoop)) {
            SecretChatHelper.getInstance().sendNoopMessage(localEncryptedChat, paramMessageObject.messageOwner);
          }
        }
      }
    }
    if (paramBoolean) {
      this.unsentMessages.put(Integer.valueOf(paramMessageObject.getId()), paramMessageObject);
    }
    sendMessage(paramMessageObject);
    return true;
  }
  
  public void sendCallback(final MessageObject paramMessageObject, TLRPC.KeyboardButton paramKeyboardButton, final ChatActivity paramChatActivity)
  {
    if ((paramMessageObject == null) || (paramKeyboardButton == null) || (paramChatActivity == null)) {
      return;
    }
    final String str = paramMessageObject.getId() + "_" + Utilities.bytesToHex(paramKeyboardButton.data);
    this.waitingForCallback.put(str, paramMessageObject);
    TLRPC.TL_messages_getBotCallbackAnswer localTL_messages_getBotCallbackAnswer = new TLRPC.TL_messages_getBotCallbackAnswer();
    localTL_messages_getBotCallbackAnswer.peer = MessagesController.getInputPeer((int)paramMessageObject.getDialogId());
    localTL_messages_getBotCallbackAnswer.msg_id = paramMessageObject.getId();
    localTL_messages_getBotCallbackAnswer.data = paramKeyboardButton.data;
    ConnectionsManager.getInstance().sendRequest(localTL_messages_getBotCallbackAnswer, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            TLRPC.TL_messages_botCallbackAnswer localTL_messages_botCallbackAnswer;
            Object localObject;
            if (paramAnonymousTLObject != null)
            {
              localTL_messages_botCallbackAnswer = (TLRPC.TL_messages_botCallbackAnswer)paramAnonymousTLObject;
              if (localTL_messages_botCallbackAnswer.message != null)
              {
                if (!localTL_messages_botCallbackAnswer.alert) {
                  break label132;
                }
                if (SendMessagesHelper.6.this.val$parentFragment.getParentActivity() == null) {
                  return;
                }
                localObject = new AlertDialog.Builder(SendMessagesHelper.6.this.val$parentFragment.getParentActivity());
                ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165338));
                ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166111), null);
                ((AlertDialog.Builder)localObject).setMessage(localTL_messages_botCallbackAnswer.message);
                SendMessagesHelper.6.this.val$parentFragment.showDialog(((AlertDialog.Builder)localObject).create());
              }
            }
            for (;;)
            {
              SendMessagesHelper.this.waitingForCallback.remove(SendMessagesHelper.6.this.val$key);
              return;
              label132:
              int i = SendMessagesHelper.6.this.val$messageObject.messageOwner.from_id;
              if (SendMessagesHelper.6.this.val$messageObject.messageOwner.via_bot_id != 0) {
                i = SendMessagesHelper.6.this.val$messageObject.messageOwner.via_bot_id;
              }
              localObject = MessagesController.getInstance().getUser(Integer.valueOf(i));
              if (localObject == null) {
                break;
              }
              SendMessagesHelper.6.this.val$parentFragment.showAlert((TLRPC.User)localObject, localTL_messages_botCallbackAnswer.message);
            }
          }
        });
      }
    }, 2);
  }
  
  public void sendCurrentLocation(MessageObject paramMessageObject, TLRPC.KeyboardButton paramKeyboardButton)
  {
    paramKeyboardButton = paramMessageObject.getId() + "_" + Utilities.bytesToHex(paramKeyboardButton.data);
    this.waitingForLocation.put(paramKeyboardButton, paramMessageObject);
    this.locationProvider.start();
  }
  
  public void sendMessage(String paramString, long paramLong, MessageObject paramMessageObject, TLRPC.WebPage paramWebPage, boolean paramBoolean, ArrayList<TLRPC.MessageEntity> paramArrayList, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(paramString, null, null, null, null, null, paramLong, null, paramMessageObject, paramWebPage, paramBoolean, null, paramArrayList, paramReplyMarkup, paramHashMap);
  }
  
  public void sendMessage(ArrayList<MessageObject> paramArrayList, final long paramLong)
  {
    if (((int)paramLong == 0) || (paramArrayList == null) || (paramArrayList.isEmpty())) {}
    final TLRPC.Peer localPeer;
    boolean bool1;
    boolean bool2;
    do
    {
      return;
      i = (int)paramLong;
      localPeer = MessagesController.getPeer((int)paramLong);
      bool1 = false;
      bool2 = false;
      if (i <= 0) {
        break;
      }
    } while (MessagesController.getInstance().getUser(Integer.valueOf(i)) == null);
    label53:
    final Object localObject2 = new ArrayList();
    final Object localObject5 = new ArrayList();
    Object localObject1 = new ArrayList();
    Object localObject4 = new ArrayList();
    final Object localObject3 = new HashMap();
    TLRPC.InputPeer localInputPeer = MessagesController.getInputPeer(i);
    int i = 0;
    label108:
    MessageObject localMessageObject;
    Object localObject10;
    Object localObject9;
    Object localObject8;
    Object localObject7;
    if (i < paramArrayList.size())
    {
      localMessageObject = (MessageObject)paramArrayList.get(i);
      if (localMessageObject.getId() > 0) {
        break label224;
      }
      localObject10 = localObject1;
      localObject9 = localObject2;
      localObject8 = localObject3;
      localObject7 = localObject4;
      localObject6 = localObject5;
    }
    label224:
    int j;
    label490:
    label503:
    label610:
    do
    {
      do
      {
        i += 1;
        localObject5 = localObject6;
        localObject4 = localObject7;
        localObject3 = localObject8;
        localObject2 = localObject9;
        localObject1 = localObject10;
        break label108;
        break;
        localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(-i));
        if (!ChatObject.isChannel((TLRPC.Chat)localObject1)) {
          break label53;
        }
        bool1 = ((TLRPC.Chat)localObject1).megagroup;
        bool2 = ((TLRPC.Chat)localObject1).signatures;
        break label53;
        localObject6 = new TLRPC.TL_message();
        if (!localMessageObject.isForwarded()) {
          break label1217;
        }
        ((TLRPC.Message)localObject6).fwd_from = localMessageObject.messageOwner.fwd_from;
        ((TLRPC.Message)localObject6).media = localMessageObject.messageOwner.media;
        ((TLRPC.Message)localObject6).flags = 4;
        if (((TLRPC.Message)localObject6).media != null) {
          ((TLRPC.Message)localObject6).flags |= 0x200;
        }
        if (bool1) {
          ((TLRPC.Message)localObject6).flags |= 0x80000000;
        }
        if (localMessageObject.messageOwner.via_bot_id != 0)
        {
          ((TLRPC.Message)localObject6).via_bot_id = localMessageObject.messageOwner.via_bot_id;
          ((TLRPC.Message)localObject6).flags |= 0x800;
        }
        ((TLRPC.Message)localObject6).message = localMessageObject.messageOwner.message;
        ((TLRPC.Message)localObject6).fwd_msg_id = localMessageObject.getId();
        ((TLRPC.Message)localObject6).attachPath = localMessageObject.messageOwner.attachPath;
        ((TLRPC.Message)localObject6).entities = localMessageObject.messageOwner.entities;
        if (!((TLRPC.Message)localObject6).entities.isEmpty()) {
          ((TLRPC.Message)localObject6).flags |= 0x80;
        }
        if (((TLRPC.Message)localObject6).attachPath == null) {
          ((TLRPC.Message)localObject6).attachPath = "";
        }
        j = UserConfig.getNewMessageId();
        ((TLRPC.Message)localObject6).id = j;
        ((TLRPC.Message)localObject6).local_id = j;
        ((TLRPC.Message)localObject6).out = true;
        if ((localPeer.channel_id == 0) || (bool1)) {
          break label1429;
        }
        if (!bool2) {
          break label1418;
        }
        j = UserConfig.getClientUserId();
        ((TLRPC.Message)localObject6).from_id = j;
        ((TLRPC.Message)localObject6).post = true;
        if (((TLRPC.Message)localObject6).random_id == 0L) {
          ((TLRPC.Message)localObject6).random_id = getNextRandomId();
        }
        ((ArrayList)localObject1).add(Long.valueOf(((TLRPC.Message)localObject6).random_id));
        ((HashMap)localObject3).put(Long.valueOf(((TLRPC.Message)localObject6).random_id), localObject6);
        ((ArrayList)localObject4).add(Integer.valueOf(((TLRPC.Message)localObject6).fwd_msg_id));
        ((TLRPC.Message)localObject6).date = ConnectionsManager.getInstance().getCurrentTime();
        if (!(localInputPeer instanceof TLRPC.TL_inputPeerChannel)) {
          break label1463;
        }
        if (bool1) {
          break label1454;
        }
        ((TLRPC.Message)localObject6).views = 1;
        ((TLRPC.Message)localObject6).flags |= 0x400;
        ((TLRPC.Message)localObject6).dialog_id = paramLong;
        ((TLRPC.Message)localObject6).to_id = localPeer;
        if ((MessageObject.isVoiceMessage((TLRPC.Message)localObject6)) && (((TLRPC.Message)localObject6).to_id.channel_id == 0)) {
          ((TLRPC.Message)localObject6).media_unread = true;
        }
        if ((localMessageObject.messageOwner.to_id instanceof TLRPC.TL_peerChannel)) {
          ((TLRPC.Message)localObject6).ttl = (-localMessageObject.messageOwner.to_id.channel_id);
        }
        localObject7 = new MessageObject((TLRPC.Message)localObject6, null, true);
        ((MessageObject)localObject7).messageOwner.send_state = 1;
        ((ArrayList)localObject2).add(localObject7);
        ((ArrayList)localObject5).add(localObject6);
        putToSendingMessages((TLRPC.Message)localObject6);
        if (BuildVars.DEBUG_VERSION) {
          FileLog.e("tmessages", "forward message user_id = " + localInputPeer.user_id + " chat_id = " + localInputPeer.chat_id + " channel_id = " + localInputPeer.channel_id + " access_hash = " + localInputPeer.access_hash);
        }
        if ((((ArrayList)localObject5).size() == 100) || (i == paramArrayList.size() - 1)) {
          break label896;
        }
        localObject6 = localObject5;
        localObject7 = localObject4;
        localObject8 = localObject3;
        localObject9 = localObject2;
        localObject10 = localObject1;
      } while (i == paramArrayList.size() - 1);
      localObject6 = localObject5;
      localObject7 = localObject4;
      localObject8 = localObject3;
      localObject9 = localObject2;
      localObject10 = localObject1;
    } while (((MessageObject)paramArrayList.get(i + 1)).getDialogId() == localMessageObject.getDialogId());
    label896:
    MessagesStorage.getInstance().putMessages(new ArrayList((Collection)localObject5), false, true, false, 0);
    MessagesController.getInstance().updateInterfaceWithMessages(paramLong, (ArrayList)localObject2);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    UserConfig.saveConfig(false);
    Object localObject6 = new TLRPC.TL_messages_forwardMessages();
    ((TLRPC.TL_messages_forwardMessages)localObject6).to_peer = localInputPeer;
    if ((((TLRPC.TL_messages_forwardMessages)localObject6).to_peer instanceof TLRPC.TL_inputPeerChannel)) {
      ((TLRPC.TL_messages_forwardMessages)localObject6).silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + paramLong, false);
    }
    if ((localMessageObject.messageOwner.to_id instanceof TLRPC.TL_peerChannel))
    {
      localObject7 = MessagesController.getInstance().getChat(Integer.valueOf(localMessageObject.messageOwner.to_id.channel_id));
      ((TLRPC.TL_messages_forwardMessages)localObject6).from_peer = new TLRPC.TL_inputPeerChannel();
      ((TLRPC.TL_messages_forwardMessages)localObject6).from_peer.channel_id = localMessageObject.messageOwner.to_id.channel_id;
      if (localObject7 != null) {
        ((TLRPC.TL_messages_forwardMessages)localObject6).from_peer.access_hash = ((TLRPC.Chat)localObject7).access_hash;
      }
    }
    for (;;)
    {
      ((TLRPC.TL_messages_forwardMessages)localObject6).random_id = ((ArrayList)localObject1);
      ((TLRPC.TL_messages_forwardMessages)localObject6).id = ((ArrayList)localObject4);
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject6, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            HashMap localHashMap = new HashMap();
            TLRPC.Updates localUpdates = (TLRPC.Updates)paramAnonymousTLObject;
            final int j;
            for (i = 0; i < localUpdates.updates.size(); i = j + 1)
            {
              paramAnonymousTLObject = (TLRPC.Update)localUpdates.updates.get(i);
              j = i;
              if ((paramAnonymousTLObject instanceof TLRPC.TL_updateMessageID))
              {
                paramAnonymousTLObject = (TLRPC.TL_updateMessageID)paramAnonymousTLObject;
                localHashMap.put(Integer.valueOf(paramAnonymousTLObject.id), Long.valueOf(paramAnonymousTLObject.random_id));
                localUpdates.updates.remove(i);
                j = i - 1;
              }
            }
            paramAnonymousTL_error = (Integer)MessagesController.getInstance().dialogs_read_outbox_max.get(Long.valueOf(paramLong));
            paramAnonymousTLObject = paramAnonymousTL_error;
            if (paramAnonymousTL_error == null)
            {
              paramAnonymousTLObject = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, paramLong));
              MessagesController.getInstance().dialogs_read_outbox_max.put(Long.valueOf(paramLong), paramAnonymousTLObject);
            }
            i = 0;
            if (i < localUpdates.updates.size())
            {
              Object localObject2 = (TLRPC.Update)localUpdates.updates.get(i);
              label242:
              boolean bool;
              label256:
              final Object localObject1;
              if (((localObject2 instanceof TLRPC.TL_updateNewMessage)) || ((localObject2 instanceof TLRPC.TL_updateNewChannelMessage)))
              {
                if (!(localObject2 instanceof TLRPC.TL_updateNewMessage)) {
                  break label310;
                }
                paramAnonymousTL_error = ((TLRPC.TL_updateNewMessage)localObject2).message;
                MessagesController.getInstance().processNewDifferenceParams(-1, ((TLRPC.Update)localObject2).pts, -1, ((TLRPC.Update)localObject2).pts_count);
                if (paramAnonymousTLObject.intValue() >= paramAnonymousTL_error.id) {
                  break label373;
                }
                bool = true;
                paramAnonymousTL_error.unread = bool;
                localObject1 = (Long)localHashMap.get(Integer.valueOf(paramAnonymousTL_error.id));
                if (localObject1 != null)
                {
                  localObject1 = (TLRPC.Message)localObject2.get(localObject1);
                  if (localObject1 != null) {
                    break label379;
                  }
                }
              }
              for (;;)
              {
                i += 1;
                break;
                label310:
                localObject1 = ((TLRPC.TL_updateNewChannelMessage)localObject2).message;
                MessagesController.getInstance().processNewChannelDifferenceParams(((TLRPC.Update)localObject2).pts, ((TLRPC.Update)localObject2).pts_count, ((TLRPC.Message)localObject1).to_id.channel_id);
                paramAnonymousTL_error = (TLRPC.TL_error)localObject1;
                if (!localObject3) {
                  break label242;
                }
                ((TLRPC.Message)localObject1).flags |= 0x80000000;
                paramAnonymousTL_error = (TLRPC.TL_error)localObject1;
                break label242;
                label373:
                bool = false;
                break label256;
                label379:
                localObject2 = (MessageObject)localObject5.get(localPeer.indexOf(localObject1));
                localPeer.remove(localObject1);
                j = ((TLRPC.Message)localObject1).id;
                final ArrayList localArrayList = new ArrayList();
                localArrayList.add(paramAnonymousTL_error);
                ((TLRPC.Message)localObject1).id = paramAnonymousTL_error.id;
                SendMessagesHelper.this.updateMediaPaths((MessageObject)localObject2, paramAnonymousTL_error, null, true);
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
                {
                  public void run()
                  {
                    MessagesStorage.getInstance().updateMessageStateAndId(localObject1.random_id, Integer.valueOf(j), localObject1.id, 0, false, SendMessagesHelper.4.this.val$to_id.channel_id);
                    MessagesStorage.getInstance().putMessages(localArrayList, true, false, false, 0);
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        SendMessagesHelper.4.1.this.val$newMsgObj.send_state = 0;
                        SearchQuery.increasePeerRaiting(SendMessagesHelper.4.this.val$peer);
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByServer, new Object[] { Integer.valueOf(SendMessagesHelper.4.1.this.val$oldId), Integer.valueOf(SendMessagesHelper.4.1.this.val$newMsgObj.id), SendMessagesHelper.4.1.this.val$newMsgObj, Long.valueOf(SendMessagesHelper.4.this.val$peer) });
                        SendMessagesHelper.this.processSentMessage(SendMessagesHelper.4.1.this.val$oldId);
                        SendMessagesHelper.this.removeFromSendingMessages(SendMessagesHelper.4.1.this.val$oldId);
                      }
                    });
                    if (MessageObject.isVideoMessage(localObject1)) {
                      SendMessagesHelper.this.stopVideoService(localObject1.attachPath);
                    }
                  }
                });
              }
            }
          }
          else
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if (paramAnonymousTL_error.text.equals("PEER_FLOOD")) {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.needShowAlert, new Object[] { Integer.valueOf(0) });
                }
              }
            });
          }
          int i = 0;
          while (i < localPeer.size())
          {
            paramAnonymousTLObject = (TLRPC.Message)localPeer.get(i);
            MessagesStorage.getInstance().markMessageAsSendError(paramAnonymousTLObject);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                paramAnonymousTLObject.send_state = 2;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(paramAnonymousTLObject.id) });
                SendMessagesHelper.this.processSentMessage(paramAnonymousTLObject.id);
                if (MessageObject.isVideoMessage(paramAnonymousTLObject)) {
                  SendMessagesHelper.this.stopVideoService(paramAnonymousTLObject.attachPath);
                }
                SendMessagesHelper.this.removeFromSendingMessages(paramAnonymousTLObject.id);
              }
            });
            i += 1;
          }
        }
      }, 68);
      localObject6 = localObject5;
      localObject7 = localObject4;
      localObject8 = localObject3;
      localObject9 = localObject2;
      localObject10 = localObject1;
      if (i == paramArrayList.size() - 1) {
        break;
      }
      localObject9 = new ArrayList();
      localObject6 = new ArrayList();
      localObject10 = new ArrayList();
      localObject7 = new ArrayList();
      localObject8 = new HashMap();
      break;
      label1217:
      ((TLRPC.Message)localObject6).fwd_from = new TLRPC.TL_messageFwdHeader();
      if (localMessageObject.isFromUser())
      {
        ((TLRPC.Message)localObject6).fwd_from.from_id = localMessageObject.messageOwner.from_id;
        localObject7 = ((TLRPC.Message)localObject6).fwd_from;
        ((TLRPC.TL_messageFwdHeader)localObject7).flags |= 0x1;
      }
      for (;;)
      {
        ((TLRPC.Message)localObject6).date = localMessageObject.messageOwner.date;
        break;
        ((TLRPC.Message)localObject6).fwd_from.channel_id = localMessageObject.messageOwner.to_id.channel_id;
        localObject7 = ((TLRPC.Message)localObject6).fwd_from;
        ((TLRPC.TL_messageFwdHeader)localObject7).flags |= 0x2;
        if (localMessageObject.messageOwner.post)
        {
          ((TLRPC.Message)localObject6).fwd_from.channel_post = localMessageObject.getId();
          localObject7 = ((TLRPC.Message)localObject6).fwd_from;
          ((TLRPC.TL_messageFwdHeader)localObject7).flags |= 0x4;
          if (localMessageObject.messageOwner.from_id > 0)
          {
            ((TLRPC.Message)localObject6).fwd_from.from_id = localMessageObject.messageOwner.from_id;
            localObject7 = ((TLRPC.Message)localObject6).fwd_from;
            ((TLRPC.TL_messageFwdHeader)localObject7).flags |= 0x1;
          }
        }
      }
      label1418:
      j = -localPeer.channel_id;
      break label490;
      label1429:
      ((TLRPC.Message)localObject6).from_id = UserConfig.getClientUserId();
      ((TLRPC.Message)localObject6).flags |= 0x100;
      break label503;
      label1454:
      ((TLRPC.Message)localObject6).unread = true;
      break label610;
      label1463:
      if ((localMessageObject.messageOwner.flags & 0x400) != 0)
      {
        ((TLRPC.Message)localObject6).views = localMessageObject.messageOwner.views;
        ((TLRPC.Message)localObject6).flags |= 0x400;
      }
      ((TLRPC.Message)localObject6).unread = true;
      break label610;
      ((TLRPC.TL_messages_forwardMessages)localObject6).from_peer = new TLRPC.TL_inputPeerEmpty();
    }
  }
  
  public void sendMessage(MessageObject paramMessageObject)
  {
    sendMessage(null, null, null, null, null, null, paramMessageObject.getDialogId(), paramMessageObject.messageOwner.attachPath, null, null, true, paramMessageObject, null, paramMessageObject.messageOwner.reply_markup, paramMessageObject.messageOwner.params);
  }
  
  public void sendMessage(TLRPC.MessageMedia paramMessageMedia, long paramLong, MessageObject paramMessageObject, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(null, paramMessageMedia, null, null, null, null, paramLong, null, paramMessageObject, null, true, null, null, paramReplyMarkup, paramHashMap);
  }
  
  public void sendMessage(TLRPC.TL_document paramTL_document, VideoEditedInfo paramVideoEditedInfo, String paramString, long paramLong, MessageObject paramMessageObject, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(null, null, null, paramVideoEditedInfo, null, paramTL_document, paramLong, paramString, paramMessageObject, null, true, null, null, paramReplyMarkup, paramHashMap);
  }
  
  public void sendMessage(TLRPC.TL_photo paramTL_photo, String paramString, long paramLong, MessageObject paramMessageObject, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(null, null, paramTL_photo, null, null, null, paramLong, paramString, paramMessageObject, null, true, null, null, paramReplyMarkup, paramHashMap);
  }
  
  public void sendMessage(TLRPC.User paramUser, long paramLong, MessageObject paramMessageObject, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(null, null, null, null, paramUser, null, paramLong, null, paramMessageObject, null, true, null, null, paramReplyMarkup, paramHashMap);
  }
  
  public void sendSticker(TLRPC.Document paramDocument, long paramLong, MessageObject paramMessageObject)
  {
    if (paramDocument == null) {}
    int i;
    do
    {
      return;
      localObject = paramDocument;
      if ((int)paramLong != 0) {
        break;
      }
      i = (int)(paramLong >> 32);
    } while (MessagesController.getInstance().getEncryptedChat(Integer.valueOf(i)) == null);
    Object localObject = paramDocument;
    File localFile;
    if ((paramDocument.thumb instanceof TLRPC.TL_photoSize))
    {
      localFile = FileLoader.getPathToAttach(paramDocument.thumb, true);
      localObject = paramDocument;
      if (!localFile.exists()) {}
    }
    try
    {
      i = (int)localFile.length();
      byte[] arrayOfByte = new byte[(int)localFile.length()];
      new RandomAccessFile(localFile, "r").readFully(arrayOfByte);
      localObject = new TLRPC.TL_document();
      ((TLRPC.TL_document)localObject).thumb = new TLRPC.TL_photoCachedSize();
      ((TLRPC.TL_document)localObject).thumb.location = paramDocument.thumb.location;
      ((TLRPC.TL_document)localObject).thumb.size = paramDocument.thumb.size;
      ((TLRPC.TL_document)localObject).thumb.w = paramDocument.thumb.w;
      ((TLRPC.TL_document)localObject).thumb.h = paramDocument.thumb.h;
      ((TLRPC.TL_document)localObject).thumb.type = paramDocument.thumb.type;
      ((TLRPC.TL_document)localObject).thumb.bytes = arrayOfByte;
      ((TLRPC.TL_document)localObject).id = paramDocument.id;
      ((TLRPC.TL_document)localObject).access_hash = paramDocument.access_hash;
      ((TLRPC.TL_document)localObject).date = paramDocument.date;
      ((TLRPC.TL_document)localObject).mime_type = paramDocument.mime_type;
      ((TLRPC.TL_document)localObject).size = paramDocument.size;
      ((TLRPC.TL_document)localObject).dc_id = paramDocument.dc_id;
      ((TLRPC.TL_document)localObject).attributes = paramDocument.attributes;
      if (((TLRPC.TL_document)localObject).mime_type == null) {
        ((TLRPC.TL_document)localObject).mime_type = "";
      }
      getInstance().sendMessage((TLRPC.TL_document)localObject, null, null, paramLong, paramMessageObject, null, null);
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
        TLRPC.Document localDocument = paramDocument;
      }
    }
  }
  
  public void setCurrentChatInfo(TLRPC.ChatFull paramChatFull)
  {
    this.currentChatInfo = paramChatFull;
  }
  
  protected void stopVideoService(final String paramString)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.stopEncodingService, new Object[] { SendMessagesHelper.7.this.val$path });
          }
        });
      }
    });
  }
  
  protected class DelayedMessage
  {
    public TLRPC.TL_document documentLocation;
    public TLRPC.EncryptedChat encryptedChat;
    public String httpLocation;
    public TLRPC.FileLocation location;
    public MessageObject obj;
    public String originalPath;
    public TLRPC.TL_decryptedMessage sendEncryptedRequest;
    public TLObject sendRequest;
    public int type;
    public VideoEditedInfo videoEditedInfo;
    
    protected DelayedMessage() {}
  }
  
  public static class LocationProvider
  {
    private LocationProviderDelegate delegate;
    private GpsLocationListener gpsLocationListener = new GpsLocationListener(null);
    private Location lastKnownLocation;
    private LocationManager locationManager;
    private Runnable locationQueryCancelRunnable;
    private GpsLocationListener networkLocationListener = new GpsLocationListener(null);
    
    public LocationProvider() {}
    
    public LocationProvider(LocationProviderDelegate paramLocationProviderDelegate)
    {
      this.delegate = paramLocationProviderDelegate;
    }
    
    private void cleanup()
    {
      this.locationManager.removeUpdates(this.gpsLocationListener);
      this.locationManager.removeUpdates(this.networkLocationListener);
      this.lastKnownLocation = null;
      this.locationQueryCancelRunnable = null;
    }
    
    public void setDelegate(LocationProviderDelegate paramLocationProviderDelegate)
    {
      this.delegate = paramLocationProviderDelegate;
    }
    
    public void start()
    {
      if (this.locationManager == null) {
        this.locationManager = ((LocationManager)ApplicationLoader.applicationContext.getSystemService("location"));
      }
      try
      {
        this.locationManager.requestLocationUpdates("gps", 1L, 0.0F, this.gpsLocationListener);
      }
      catch (Exception localException2)
      {
        try
        {
          this.locationManager.requestLocationUpdates("network", 1L, 0.0F, this.networkLocationListener);
        }
        catch (Exception localException2)
        {
          try
          {
            for (;;)
            {
              this.lastKnownLocation = this.locationManager.getLastKnownLocation("gps");
              if (this.lastKnownLocation == null) {
                this.lastKnownLocation = this.locationManager.getLastKnownLocation("network");
              }
              if (this.locationQueryCancelRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.locationQueryCancelRunnable);
              }
              this.locationQueryCancelRunnable = new Runnable()
              {
                public void run()
                {
                  if (SendMessagesHelper.LocationProvider.this.locationQueryCancelRunnable != this) {
                    return;
                  }
                  if (SendMessagesHelper.LocationProvider.this.delegate != null)
                  {
                    if (SendMessagesHelper.LocationProvider.this.lastKnownLocation == null) {
                      break label59;
                    }
                    SendMessagesHelper.LocationProvider.this.delegate.onLocationAcquired(SendMessagesHelper.LocationProvider.this.lastKnownLocation);
                  }
                  for (;;)
                  {
                    SendMessagesHelper.LocationProvider.this.cleanup();
                    return;
                    label59:
                    SendMessagesHelper.LocationProvider.this.delegate.onUnableLocationAcquire();
                  }
                }
              };
              AndroidUtilities.runOnUIThread(this.locationQueryCancelRunnable, 5000L);
              return;
              localException1 = localException1;
              FileLog.e("tmessages", localException1);
              continue;
              localException2 = localException2;
              FileLog.e("tmessages", localException2);
            }
          }
          catch (Exception localException3)
          {
            for (;;)
            {
              FileLog.e("tmessages", localException3);
            }
          }
        }
      }
    }
    
    public void stop()
    {
      if (this.locationManager == null) {
        return;
      }
      if (this.locationQueryCancelRunnable != null) {
        AndroidUtilities.cancelRunOnUIThread(this.locationQueryCancelRunnable);
      }
      cleanup();
    }
    
    private class GpsLocationListener
      implements LocationListener
    {
      private GpsLocationListener() {}
      
      public void onLocationChanged(Location paramLocation)
      {
        if ((paramLocation == null) || (SendMessagesHelper.LocationProvider.this.locationQueryCancelRunnable == null)) {}
        do
        {
          return;
          FileLog.e("tmessages", "found location " + paramLocation);
          SendMessagesHelper.LocationProvider.access$402(SendMessagesHelper.LocationProvider.this, paramLocation);
        } while (paramLocation.getAccuracy() >= 100.0F);
        if (SendMessagesHelper.LocationProvider.this.delegate != null) {
          SendMessagesHelper.LocationProvider.this.delegate.onLocationAcquired(paramLocation);
        }
        if (SendMessagesHelper.LocationProvider.this.locationQueryCancelRunnable != null) {
          AndroidUtilities.cancelRunOnUIThread(SendMessagesHelper.LocationProvider.this.locationQueryCancelRunnable);
        }
        SendMessagesHelper.LocationProvider.this.cleanup();
      }
      
      public void onProviderDisabled(String paramString) {}
      
      public void onProviderEnabled(String paramString) {}
      
      public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {}
    }
    
    public static abstract interface LocationProviderDelegate
    {
      public abstract void onLocationAcquired(Location paramLocation);
      
      public abstract void onUnableLocationAcquire();
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\SendMessagesHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */