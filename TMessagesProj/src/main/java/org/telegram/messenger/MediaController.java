package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.provider.MediaStore.Images.Media;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.query.SharedMediaQuery;
import org.telegram.messenger.video.MP4Builder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.PhotoViewer;

public class MediaController
  implements AudioManager.OnAudioFocusChangeListener, NotificationCenter.NotificationCenterDelegate, SensorEventListener
{
  private static final int AUDIO_FOCUSED = 2;
  private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
  private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
  public static final int AUTODOWNLOAD_MASK_AUDIO = 2;
  public static final int AUTODOWNLOAD_MASK_DOCUMENT = 8;
  public static final int AUTODOWNLOAD_MASK_GIF = 32;
  public static final int AUTODOWNLOAD_MASK_MUSIC = 16;
  public static final int AUTODOWNLOAD_MASK_PHOTO = 1;
  public static final int AUTODOWNLOAD_MASK_VIDEO = 4;
  private static volatile MediaController Instance = null;
  public static final String MIME_TYPE = "video/avc";
  private static final int PROCESSOR_TYPE_INTEL = 2;
  private static final int PROCESSOR_TYPE_MTK = 3;
  private static final int PROCESSOR_TYPE_OTHER = 0;
  private static final int PROCESSOR_TYPE_QCOM = 1;
  private static final int PROCESSOR_TYPE_SEC = 4;
  private static final int PROCESSOR_TYPE_TI = 5;
  private static final float VOLUME_DUCK = 0.2F;
  private static final float VOLUME_NORMAL = 1.0F;
  public static AlbumEntry allPhotosAlbumEntry;
  private static final String[] projectionPhotos;
  private static final String[] projectionVideo;
  public static int[] readArgs = new int[3];
  private Sensor accelerometerSensor;
  private boolean accelerometerVertical;
  private HashMap<String, FileDownloadProgressListener> addLaterArray = new HashMap();
  private boolean allowStartRecord;
  private ArrayList<DownloadObject> audioDownloadQueue = new ArrayList();
  private int audioFocus = 0;
  private AudioInfo audioInfo;
  private MediaPlayer audioPlayer = null;
  private AudioRecord audioRecorder = null;
  private AudioTrack audioTrackPlayer = null;
  private boolean autoplayGifs = true;
  private int buffersWrited;
  private boolean callInProgress;
  private boolean cancelCurrentVideoConversion = false;
  private int countLess;
  private int currentPlaylistNum;
  private long currentTotalPcmDuration;
  private boolean customTabs = true;
  private boolean decodingFinished = false;
  private ArrayList<FileDownloadProgressListener> deleteLaterArray = new ArrayList();
  private boolean directShare = true;
  private ArrayList<DownloadObject> documentDownloadQueue = new ArrayList();
  private HashMap<String, DownloadObject> downloadQueueKeys = new HashMap();
  private boolean downloadingCurrentMessage;
  private ExternalObserver externalObserver = null;
  private ByteBuffer fileBuffer;
  private DispatchQueue fileDecodingQueue;
  private DispatchQueue fileEncodingQueue;
  private boolean forceLoopCurrentPlaylist;
  private ArrayList<AudioBuffer> freePlayerBuffers = new ArrayList();
  private HashMap<String, MessageObject> generatingWaveform = new HashMap();
  private ArrayList<DownloadObject> gifDownloadQueue = new ArrayList();
  private float[] gravity = new float[3];
  private float[] gravityFast = new float[3];
  private Sensor gravitySensor;
  private int hasAudioFocus;
  private int ignoreFirstProgress = 0;
  private boolean ignoreOnPause;
  private boolean ignoreProximity;
  private boolean inputFieldHasText;
  private InternalObserver internalObserver = null;
  private boolean isPaused = false;
  private int lastCheckMask = 0;
  private long lastMediaCheckTime = 0L;
  private long lastPlayPcm;
  private int lastProgress = 0;
  private float lastProximityValue = -100.0F;
  private TLRPC.EncryptedChat lastSecretChat = null;
  private long lastSecretChatEnterTime = 0L;
  private long lastSecretChatLeaveTime = 0L;
  private ArrayList<Long> lastSecretChatVisibleMessages = null;
  private int lastTag = 0;
  private long lastTimestamp = 0L;
  private float[] linearAcceleration = new float[3];
  private Sensor linearSensor;
  private boolean listenerInProgress = false;
  private HashMap<String, ArrayList<MessageObject>> loadingFileMessagesObservers = new HashMap();
  private HashMap<String, ArrayList<WeakReference<FileDownloadProgressListener>>> loadingFileObservers = new HashMap();
  private String[] mediaProjections = null;
  public int mobileDataDownloadMask = 0;
  private ArrayList<DownloadObject> musicDownloadQueue = new ArrayList();
  private HashMap<Integer, String> observersByTag = new HashMap();
  private ArrayList<DownloadObject> photoDownloadQueue = new ArrayList();
  private boolean playMusicAgain;
  private int playerBufferSize = 0;
  private final Object playerObjectSync = new Object();
  private DispatchQueue playerQueue;
  private final Object playerSync = new Object();
  private MessageObject playingMessageObject;
  private ArrayList<MessageObject> playlist = new ArrayList();
  private float previousAccValue;
  private Timer progressTimer = null;
  private final Object progressTimerSync = new Object();
  private boolean proximityHasDifferentValues;
  private Sensor proximitySensor;
  private boolean proximityTouched;
  private PowerManager.WakeLock proximityWakeLock;
  private ChatActivity raiseChat;
  private boolean raiseToEarRecord;
  private boolean raiseToSpeak = true;
  private int raisedToBack;
  private int raisedToTop;
  private int recordBufferSize;
  private ArrayList<ByteBuffer> recordBuffers = new ArrayList();
  private long recordDialogId;
  private DispatchQueue recordQueue;
  private MessageObject recordReplyingMessageObject;
  private Runnable recordRunnable = new Runnable()
  {
    public void run()
    {
      final ByteBuffer localByteBuffer;
      int n;
      double d2;
      final double d1;
      if (MediaController.this.audioRecorder != null) {
        if (!MediaController.this.recordBuffers.isEmpty())
        {
          localByteBuffer = (ByteBuffer)MediaController.this.recordBuffers.get(0);
          MediaController.this.recordBuffers.remove(0);
          localByteBuffer.rewind();
          n = MediaController.this.audioRecorder.read(localByteBuffer, localByteBuffer.capacity());
          if (n <= 0) {
            break label510;
          }
          localByteBuffer.limit(n);
          d2 = 0.0D;
          d1 = d2;
        }
      }
      for (;;)
      {
        int k;
        int m;
        float f2;
        double d3;
        try
        {
          long l = MediaController.this.samplesCount + n / 2;
          d1 = d2;
          k = (int)(MediaController.this.samplesCount / l * MediaController.this.recordSamples.length);
          d1 = d2;
          m = MediaController.this.recordSamples.length;
          if (k != 0)
          {
            d1 = d2;
            f2 = MediaController.this.recordSamples.length / k;
            f1 = 0.0F;
            j = 0;
            if (j < k)
            {
              d1 = d2;
              MediaController.this.recordSamples[j] = MediaController.this.recordSamples[((int)f1)];
              f1 += f2;
              j += 1;
              continue;
              localByteBuffer = ByteBuffer.allocateDirect(MediaController.this.recordBufferSize);
              localByteBuffer.order(ByteOrder.nativeOrder());
              break;
            }
          }
          j = k;
          f1 = 0.0F;
          float f3 = n / 2.0F / (m - k);
          k = 0;
          d1 = d2;
          if (k < n / 2)
          {
            d1 = d2;
            int i = localByteBuffer.getShort();
            d3 = d2;
            if (i > 2500) {
              d3 = d2 + i * i;
            }
            m = j;
            f2 = f1;
            if (k != (int)f1) {
              break label538;
            }
            d1 = d3;
            m = j;
            f2 = f1;
            if (j >= MediaController.this.recordSamples.length) {
              break label538;
            }
            d1 = d3;
            MediaController.this.recordSamples[j] = i;
            f2 = f1 + f3;
            m = j + 1;
            break label538;
          }
          d1 = d2;
          MediaController.access$302(MediaController.this, l);
          d1 = d2;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
          continue;
          final boolean bool = false;
          continue;
        }
        localByteBuffer.position(0);
        d1 = Math.sqrt(d1 / n / 2.0D);
        if (n != localByteBuffer.capacity())
        {
          bool = true;
          if (n != 0) {
            MediaController.this.fileEncodingQueue.postRunnable(new Runnable()
            {
              public void run()
              {
                if (localByteBuffer.hasRemaining())
                {
                  int i = -1;
                  if (localByteBuffer.remaining() > MediaController.this.fileBuffer.remaining())
                  {
                    i = localByteBuffer.limit();
                    localByteBuffer.limit(MediaController.this.fileBuffer.remaining() + localByteBuffer.position());
                  }
                  MediaController.this.fileBuffer.put(localByteBuffer);
                  MediaController localMediaController;
                  ByteBuffer localByteBuffer;
                  if ((MediaController.this.fileBuffer.position() == MediaController.this.fileBuffer.limit()) || (bool))
                  {
                    localMediaController = MediaController.this;
                    localByteBuffer = MediaController.this.fileBuffer;
                    if (bool) {
                      break label238;
                    }
                  }
                  label238:
                  for (int j = MediaController.this.fileBuffer.limit();; j = localByteBuffer.position())
                  {
                    if (localMediaController.writeFrame(localByteBuffer, j) != 0)
                    {
                      MediaController.this.fileBuffer.rewind();
                      MediaController.access$714(MediaController.this, MediaController.this.fileBuffer.limit() / 2 / 16);
                    }
                    if (i == -1) {
                      break;
                    }
                    localByteBuffer.limit(i);
                    break;
                  }
                }
                MediaController.this.recordQueue.postRunnable(new Runnable()
                {
                  public void run()
                  {
                    MediaController.this.recordBuffers.add(MediaController.1.1.this.val$finalBuffer);
                  }
                });
              }
            });
          }
          MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordProgressChanged, new Object[] { Long.valueOf(System.currentTimeMillis() - MediaController.this.recordStartTime), Double.valueOf(d1) });
            }
          });
          return;
        }
        label510:
        MediaController.this.recordBuffers.add(localByteBuffer);
        MediaController.this.stopRecordingInternal(MediaController.this.sendAfterDone);
        return;
        label538:
        k += 1;
        int j = m;
        float f1 = f2;
        d2 = d3;
      }
    }
  };
  private short[] recordSamples = new short['Ѐ'];
  private Runnable recordStartRunnable;
  private long recordStartTime;
  private long recordTimeCount;
  private TLRPC.TL_document recordingAudio = null;
  private File recordingAudioFile = null;
  private Runnable refreshGalleryRunnable;
  private int repeatMode;
  private boolean resumeAudioOnFocusGain;
  public int roamingDownloadMask = 0;
  private long samplesCount;
  private boolean saveToGallery = true;
  private int sendAfterDone;
  private SensorManager sensorManager;
  private boolean sensorsStarted;
  private boolean shuffleMusic;
  private ArrayList<MessageObject> shuffledPlaylist = new ArrayList();
  private int startObserverToken = 0;
  private StopMediaObserverRunnable stopMediaObserverRunnable = null;
  private final Object sync = new Object();
  private long timeSinceRaise;
  private HashMap<Long, Long> typingTimes = new HashMap();
  private boolean useFrontSpeaker;
  private ArrayList<AudioBuffer> usedPlayerBuffers = new ArrayList();
  private boolean videoConvertFirstWrite = true;
  private ArrayList<MessageObject> videoConvertQueue = new ArrayList();
  private final Object videoConvertSync = new Object();
  private ArrayList<DownloadObject> videoDownloadQueue = new ArrayList();
  private final Object videoQueueSync = new Object();
  private ArrayList<MessageObject> voiceMessagesPlaylist;
  private HashMap<Integer, MessageObject> voiceMessagesPlaylistMap;
  private boolean voiceMessagesPlaylistUnread;
  public int wifiDownloadMask = 0;
  
  static
  {
    projectionPhotos = new String[] { "_id", "bucket_id", "bucket_display_name", "_data", "datetaken", "orientation" };
    projectionVideo = new String[] { "_id", "bucket_id", "bucket_display_name", "_data", "datetaken" };
  }
  
  public MediaController()
  {
    for (;;)
    {
      try
      {
        this.recordBufferSize = AudioRecord.getMinBufferSize(16000, 16, 2);
        if (this.recordBufferSize <= 0) {
          this.recordBufferSize = 1280;
        }
        this.playerBufferSize = AudioTrack.getMinBufferSize(48000, 4, 2);
        if (this.playerBufferSize > 0) {
          continue;
        }
        this.playerBufferSize = 3840;
      }
      catch (Exception localException1)
      {
        ByteBuffer localByteBuffer;
        FileLog.e("tmessages", localException1);
        try
        {
          this.sensorManager = ((SensorManager)ApplicationLoader.applicationContext.getSystemService("sensor"));
          this.linearSensor = this.sensorManager.getDefaultSensor(10);
          this.gravitySensor = this.sensorManager.getDefaultSensor(9);
          if ((this.linearSensor == null) || (this.gravitySensor == null))
          {
            FileLog.e("tmessages", "gravity or linear sensor not found");
            this.accelerometerSensor = this.sensorManager.getDefaultSensor(1);
            this.linearSensor = null;
            this.gravitySensor = null;
          }
          this.proximitySensor = this.sensorManager.getDefaultSensor(8);
          this.proximityWakeLock = ((PowerManager)ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(32, "proximity");
        }
        catch (Exception localException3)
        {
          try
          {
            ApplicationLoader.applicationContext.getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, new GalleryObserverExternal());
          }
          catch (Exception localException3)
          {
            try
            {
              Object localObject1;
              Object localObject2;
              ApplicationLoader.applicationContext.getContentResolver().registerContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, false, new GalleryObserverInternal());
              try
              {
                localObject1 = new PhoneStateListener()
                {
                  public void onCallStateChanged(int paramAnonymousInt, String paramAnonymousString)
                  {
                    if (paramAnonymousInt == 1) {
                      if ((!MediaController.this.isPlayingAudio(MediaController.this.getPlayingMessageObject())) || (MediaController.this.isAudioPaused())) {}
                    }
                    do
                    {
                      MediaController.this.pauseAudio(MediaController.this.getPlayingMessageObject());
                      for (;;)
                      {
                        MediaController.access$2002(MediaController.this, true);
                        return;
                        if ((MediaController.this.recordStartRunnable != null) || (MediaController.this.recordingAudio != null)) {
                          MediaController.this.stopRecording(2);
                        }
                      }
                      if (paramAnonymousInt == 0)
                      {
                        MediaController.access$2002(MediaController.this, false);
                        return;
                      }
                    } while (paramAnonymousInt != 2);
                    MediaController.access$2002(MediaController.this, true);
                  }
                };
                localObject2 = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
                if (localObject2 != null) {
                  ((TelephonyManager)localObject2).listen((PhoneStateListener)localObject1, 32);
                }
                return;
              }
              catch (Exception localException5)
              {
                FileLog.e("tmessages", localException5);
                return;
              }
              localException2 = localException2;
              FileLog.e("tmessages", localException2);
              continue;
              this.mediaProjections = new String[] { "_data", "_display_name", "bucket_display_name", "datetaken", "title" };
              continue;
              localException3 = localException3;
              FileLog.e("tmessages", localException3);
              continue;
            }
            catch (Exception localException4)
            {
              FileLog.e("tmessages", localException4);
              continue;
            }
          }
        }
        this.fileBuffer = ByteBuffer.allocateDirect(1920);
        this.recordQueue = new DispatchQueue("recordQueue");
        this.recordQueue.setPriority(10);
        this.fileEncodingQueue = new DispatchQueue("fileEncodingQueue");
        this.fileEncodingQueue.setPriority(10);
        this.playerQueue = new DispatchQueue("playerQueue");
        this.fileDecodingQueue = new DispatchQueue("fileDecodingQueue");
        localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        this.mobileDataDownloadMask = ((SharedPreferences)localObject1).getInt("mobileDataDownloadMask", 51);
        this.wifiDownloadMask = ((SharedPreferences)localObject1).getInt("wifiDownloadMask", 51);
        this.roamingDownloadMask = ((SharedPreferences)localObject1).getInt("roamingDownloadMask", 0);
        this.saveToGallery = ((SharedPreferences)localObject1).getBoolean("save_gallery", false);
        this.autoplayGifs = ((SharedPreferences)localObject1).getBoolean("autoplay_gif", true);
        this.raiseToSpeak = ((SharedPreferences)localObject1).getBoolean("raise_to_speak", true);
        this.customTabs = ((SharedPreferences)localObject1).getBoolean("custom_tabs", true);
        this.directShare = ((SharedPreferences)localObject1).getBoolean("direct_share", true);
        this.shuffleMusic = ((SharedPreferences)localObject1).getBoolean("shuffleMusic", false);
        this.repeatMode = ((SharedPreferences)localObject1).getInt("repeatMode", 0);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileDidFailedLoad);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.didReceivedNewMessages);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.messagesDeleted);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileDidLoaded);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileLoadProgressChanged);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileUploadProgressChanged);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.removeAllMessagesFromDialog);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.musicDidLoaded);
          }
        });
        localObject1 = new BroadcastReceiver()
        {
          public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
          {
            MediaController.this.checkAutodownloadSettings();
          }
        };
        localObject2 = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        ApplicationLoader.applicationContext.registerReceiver((BroadcastReceiver)localObject1, (IntentFilter)localObject2);
        if (!UserConfig.isClientActivated()) {
          continue;
        }
        checkAutodownloadSettings();
        if (Build.VERSION.SDK_INT < 16) {
          continue;
        }
        this.mediaProjections = new String[] { "_data", "_display_name", "bucket_display_name", "datetaken", "title", "width", "height" };
        int i = 0;
        continue;
        i = 0;
        continue;
      }
      if (i >= 5) {
        continue;
      }
      localByteBuffer = ByteBuffer.allocateDirect(4096);
      localByteBuffer.order(ByteOrder.nativeOrder());
      this.recordBuffers.add(localByteBuffer);
      i += 1;
    }
    while (i < 3)
    {
      this.freePlayerBuffers.add(new AudioBuffer(this.playerBufferSize));
      i += 1;
    }
  }
  
  private void buildShuffledPlayList()
  {
    if (this.playlist.isEmpty()) {}
    for (;;)
    {
      return;
      ArrayList localArrayList = new ArrayList(this.playlist);
      this.shuffledPlaylist.clear();
      MessageObject localMessageObject = (MessageObject)this.playlist.get(this.currentPlaylistNum);
      localArrayList.remove(this.currentPlaylistNum);
      this.shuffledPlaylist.add(localMessageObject);
      int j = localArrayList.size();
      int i = 0;
      while (i < j)
      {
        int k = Utilities.random.nextInt(localArrayList.size());
        this.shuffledPlaylist.add(localArrayList.get(k));
        localArrayList.remove(k);
        i += 1;
      }
    }
  }
  
  private void checkAudioFocus(MessageObject paramMessageObject)
  {
    if (paramMessageObject.isVoice()) {
      if (this.useFrontSpeaker) {
        i = 3;
      }
    }
    for (;;)
    {
      if (this.hasAudioFocus != i)
      {
        this.hasAudioFocus = i;
        if (i != 3) {
          break;
        }
        i = NotificationsController.getInstance().audioManager.requestAudioFocus(this, 0, 1);
        if (i == 1) {
          this.audioFocus = 2;
        }
      }
      return;
      i = 2;
      continue;
      i = 1;
    }
    paramMessageObject = NotificationsController.getInstance().audioManager;
    if (i == 2) {}
    for (int i = 3;; i = 1)
    {
      i = paramMessageObject.requestAudioFocus(this, 3, i);
      break;
    }
  }
  
  private void checkConversionCanceled()
    throws Exception
  {
    synchronized (this.videoConvertSync)
    {
      boolean bool = this.cancelCurrentVideoConversion;
      if (bool) {
        throw new RuntimeException("canceled conversion");
      }
    }
  }
  
  private void checkDecoderQueue()
  {
    this.fileDecodingQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (MediaController.this.decodingFinished) {
          MediaController.this.checkPlayerQueue();
        }
        for (;;)
        {
          return;
          int i = 0;
          for (;;)
          {
            MediaController.AudioBuffer localAudioBuffer = null;
            synchronized (MediaController.this.playerSync)
            {
              if (!MediaController.this.freePlayerBuffers.isEmpty())
              {
                localAudioBuffer = (MediaController.AudioBuffer)MediaController.this.freePlayerBuffers.get(0);
                MediaController.this.freePlayerBuffers.remove(0);
              }
              if (!MediaController.this.usedPlayerBuffers.isEmpty()) {
                i = 1;
              }
              if (localAudioBuffer == null) {
                break label249;
              }
              MediaController.this.readOpusFile(localAudioBuffer.buffer, MediaController.this.playerBufferSize, MediaController.readArgs);
              localAudioBuffer.size = MediaController.readArgs[0];
              localAudioBuffer.pcmOffset = MediaController.readArgs[1];
              localAudioBuffer.finished = MediaController.readArgs[2];
              if (localAudioBuffer.finished == 1) {
                MediaController.access$3102(MediaController.this, true);
              }
              if (localAudioBuffer.size == 0) {
                break;
              }
              localAudioBuffer.buffer.rewind();
              localAudioBuffer.buffer.get(localAudioBuffer.bufferBytes);
            }
            synchronized (MediaController.this.playerSync)
            {
              MediaController.this.usedPlayerBuffers.add(localAudioBuffer);
              i = 1;
              continue;
              localObject1 = finally;
              throw ((Throwable)localObject1);
            }
          }
          synchronized (MediaController.this.playerSync)
          {
            MediaController.this.freePlayerBuffers.add(localObject2);
            label249:
            if (i == 0) {
              continue;
            }
            MediaController.this.checkPlayerQueue();
            return;
          }
        }
      }
    });
  }
  
  private void checkDownloadFinished(String paramString, int paramInt)
  {
    DownloadObject localDownloadObject = (DownloadObject)this.downloadQueueKeys.get(paramString);
    if (localDownloadObject != null)
    {
      this.downloadQueueKeys.remove(paramString);
      if ((paramInt == 0) || (paramInt == 2)) {
        MessagesStorage.getInstance().removeFromDownloadQueue(localDownloadObject.id, localDownloadObject.type, false);
      }
      if (localDownloadObject.type != 1) {
        break label82;
      }
      this.photoDownloadQueue.remove(localDownloadObject);
      if (this.photoDownloadQueue.isEmpty()) {
        newDownloadObjectsAvailable(1);
      }
    }
    label82:
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
                return;
                if (localDownloadObject.type != 2) {
                  break;
                }
                this.audioDownloadQueue.remove(localDownloadObject);
              } while (!this.audioDownloadQueue.isEmpty());
              newDownloadObjectsAvailable(2);
              return;
              if (localDownloadObject.type != 4) {
                break;
              }
              this.videoDownloadQueue.remove(localDownloadObject);
            } while (!this.videoDownloadQueue.isEmpty());
            newDownloadObjectsAvailable(4);
            return;
            if (localDownloadObject.type != 8) {
              break;
            }
            this.documentDownloadQueue.remove(localDownloadObject);
          } while (!this.documentDownloadQueue.isEmpty());
          newDownloadObjectsAvailable(8);
          return;
          if (localDownloadObject.type != 16) {
            break;
          }
          this.musicDownloadQueue.remove(localDownloadObject);
        } while (!this.musicDownloadQueue.isEmpty());
        newDownloadObjectsAvailable(16);
        return;
      } while (localDownloadObject.type != 32);
      this.gifDownloadQueue.remove(localDownloadObject);
    } while (!this.gifDownloadQueue.isEmpty());
    newDownloadObjectsAvailable(32);
  }
  
  private void checkIsNextMusicFileDownloaded()
  {
    if ((getCurrentDownloadMask() & 0x10) == 0) {}
    label23:
    label135:
    label195:
    label197:
    label210:
    label211:
    for (;;)
    {
      return;
      Object localObject1;
      MessageObject localMessageObject;
      Object localObject2;
      if (this.shuffleMusic)
      {
        localObject1 = this.shuffledPlaylist;
        if ((localObject1 == null) || (((ArrayList)localObject1).size() < 2)) {
          break label195;
        }
        int j = this.currentPlaylistNum + 1;
        int i = j;
        if (j >= ((ArrayList)localObject1).size()) {
          i = 0;
        }
        localMessageObject = (MessageObject)((ArrayList)localObject1).get(i);
        localObject2 = null;
        localObject1 = localObject2;
        if (localMessageObject.messageOwner.attachPath != null)
        {
          localObject1 = localObject2;
          if (localMessageObject.messageOwner.attachPath.length() > 0)
          {
            localObject2 = new File(localMessageObject.messageOwner.attachPath);
            localObject1 = localObject2;
            if (!((File)localObject2).exists()) {
              localObject1 = null;
            }
          }
        }
        if (localObject1 == null) {
          break label197;
        }
        localObject2 = localObject1;
        if ((localObject2 == null) || (!((File)localObject2).exists())) {
          break label210;
        }
      }
      for (;;)
      {
        if ((localObject2 == null) || (localObject2 == localObject1) || (((File)localObject2).exists()) || (!localMessageObject.isMusic())) {
          break label211;
        }
        FileLoader.getInstance().loadFile(localMessageObject.getDocument(), false, false);
        return;
        localObject1 = this.playlist;
        break label23;
        break;
        localObject2 = FileLoader.getPathToMessage(localMessageObject.messageOwner);
        break label135;
      }
    }
  }
  
  private void checkPlayerQueue()
  {
    this.playerQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        synchronized (MediaController.this.playerObjectSync)
        {
          if ((MediaController.this.audioTrackPlayer == null) || (MediaController.this.audioTrackPlayer.getPlayState() != 3)) {
            return;
          }
          ??? = null;
        }
        int i;
        synchronized (MediaController.this.playerSync)
        {
          if (!MediaController.this.usedPlayerBuffers.isEmpty())
          {
            ??? = (MediaController.AudioBuffer)MediaController.this.usedPlayerBuffers.get(0);
            MediaController.this.usedPlayerBuffers.remove(0);
          }
          if (??? != null) {
            i = 0;
          }
          try
          {
            int j = MediaController.this.audioTrackPlayer.write(((MediaController.AudioBuffer)???).bufferBytes, 0, ((MediaController.AudioBuffer)???).size);
            i = j;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              final long l;
              FileLog.e("tmessages", localException);
              continue;
              i = -1;
            }
          }
          MediaController.access$3908(MediaController.this);
          if (i > 0)
          {
            l = ((MediaController.AudioBuffer)???).pcmOffset;
            if (((MediaController.AudioBuffer)???).finished == 1) {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MediaController.access$2702(MediaController.this, l);
                  if (this.val$marker != -1)
                  {
                    if (MediaController.this.audioTrackPlayer != null) {
                      MediaController.this.audioTrackPlayer.setNotificationMarkerPosition(1);
                    }
                    if (this.val$finalBuffersWrited == 1) {
                      MediaController.this.cleanupPlayer(true, true, true);
                    }
                  }
                }
              });
            }
          }
          else
          {
            if (((MediaController.AudioBuffer)???).finished != 1) {
              MediaController.this.checkPlayerQueue();
            }
            if ((??? == null) || ((??? != null) && (((MediaController.AudioBuffer)???).finished != 1))) {
              MediaController.this.checkDecoderQueue();
            }
            if (??? == null) {
              return;
            }
            synchronized (MediaController.this.playerSync)
            {
              MediaController.this.freePlayerBuffers.add(???);
              return;
            }
            localObject5 = finally;
            throw ((Throwable)localObject5);
          }
        }
      }
    });
  }
  
  private void checkScreenshots(ArrayList<Long> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()) || (this.lastSecretChatEnterTime == 0L) || (this.lastSecretChat == null) || (!(this.lastSecretChat instanceof TLRPC.TL_encryptedChat))) {}
    int i;
    do
    {
      return;
      i = 0;
      paramArrayList = paramArrayList.iterator();
      while (paramArrayList.hasNext())
      {
        Long localLong = (Long)paramArrayList.next();
        if (((this.lastMediaCheckTime == 0L) || (localLong.longValue() > this.lastMediaCheckTime)) && (localLong.longValue() >= this.lastSecretChatEnterTime) && ((this.lastSecretChatLeaveTime == 0L) || (localLong.longValue() <= this.lastSecretChatLeaveTime + 2000L)))
        {
          this.lastMediaCheckTime = Math.max(this.lastMediaCheckTime, localLong.longValue());
          i = 1;
        }
      }
    } while (i == 0);
    SecretChatHelper.getInstance().sendScreenshotMessage(this.lastSecretChat, this.lastSecretChatVisibleMessages, null);
  }
  
  private native void closeOpusFile();
  
  /* Error */
  @TargetApi(16)
  private boolean convertVideo(MessageObject paramMessageObject)
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 1112	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   4: getfield 1117	org/telegram/messenger/VideoEditedInfo:originalPath	Ljava/lang/String;
    //   7: astore 35
    //   9: aload_1
    //   10: getfield 1112	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   13: getfield 1120	org/telegram/messenger/VideoEditedInfo:startTime	J
    //   16: lstore 22
    //   18: aload_1
    //   19: getfield 1112	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   22: getfield 1123	org/telegram/messenger/VideoEditedInfo:endTime	J
    //   25: lstore 28
    //   27: aload_1
    //   28: getfield 1112	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   31: getfield 1126	org/telegram/messenger/VideoEditedInfo:resultWidth	I
    //   34: istore 7
    //   36: aload_1
    //   37: getfield 1112	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   40: getfield 1129	org/telegram/messenger/VideoEditedInfo:resultHeight	I
    //   43: istore 6
    //   45: aload_1
    //   46: getfield 1112	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   49: getfield 1132	org/telegram/messenger/VideoEditedInfo:rotationValue	I
    //   52: istore 9
    //   54: aload_1
    //   55: getfield 1112	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   58: getfield 1135	org/telegram/messenger/VideoEditedInfo:originalWidth	I
    //   61: istore 11
    //   63: aload_1
    //   64: getfield 1112	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   67: getfield 1138	org/telegram/messenger/VideoEditedInfo:originalHeight	I
    //   70: istore 12
    //   72: aload_1
    //   73: getfield 1112	org/telegram/messenger/MessageObject:videoEditedInfo	Lorg/telegram/messenger/VideoEditedInfo;
    //   76: getfield 1141	org/telegram/messenger/VideoEditedInfo:bitrate	I
    //   79: istore 8
    //   81: iconst_0
    //   82: istore 10
    //   84: new 1040	java/io/File
    //   87: dup
    //   88: aload_1
    //   89: getfield 1030	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   92: getfield 1035	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   95: invokespecial 1041	java/io/File:<init>	(Ljava/lang/String;)V
    //   98: astore 52
    //   100: getstatic 689	android/os/Build$VERSION:SDK_INT	I
    //   103: bipush 18
    //   105: if_icmpge +143 -> 248
    //   108: iload 6
    //   110: iload 7
    //   112: if_icmple +136 -> 248
    //   115: iload 7
    //   117: iload 11
    //   119: if_icmpeq +129 -> 248
    //   122: iload 6
    //   124: iload 12
    //   126: if_icmpeq +122 -> 248
    //   129: iload 7
    //   131: istore 4
    //   133: iload 6
    //   135: istore 5
    //   137: bipush 90
    //   139: istore_2
    //   140: sipush 270
    //   143: istore_3
    //   144: getstatic 559	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   147: ldc_w 1143
    //   150: iconst_0
    //   151: invokevirtual 629	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   154: astore 53
    //   156: aload 53
    //   158: ldc_w 1145
    //   161: iconst_1
    //   162: invokeinterface 644 3 0
    //   167: istore 32
    //   169: aload 53
    //   171: invokeinterface 1149 1 0
    //   176: ldc_w 1145
    //   179: iconst_0
    //   180: invokeinterface 1155 3 0
    //   185: invokeinterface 1158 1 0
    //   190: pop
    //   191: new 1040	java/io/File
    //   194: dup
    //   195: aload 35
    //   197: invokespecial 1041	java/io/File:<init>	(Ljava/lang/String;)V
    //   200: astore 41
    //   202: aload 41
    //   204: invokevirtual 1161	java/io/File:canRead	()Z
    //   207: ifeq +8 -> 215
    //   210: iload 32
    //   212: ifne +145 -> 357
    //   215: aload_0
    //   216: aload_1
    //   217: aload 52
    //   219: iconst_1
    //   220: iconst_1
    //   221: invokespecial 1165	org/telegram/messenger/MediaController:didWriteData	(Lorg/telegram/messenger/MessageObject;Ljava/io/File;ZZ)V
    //   224: aload 53
    //   226: invokeinterface 1149 1 0
    //   231: ldc_w 1145
    //   234: iconst_1
    //   235: invokeinterface 1155 3 0
    //   240: invokeinterface 1158 1 0
    //   245: pop
    //   246: iconst_0
    //   247: ireturn
    //   248: iload 6
    //   250: istore 4
    //   252: iload 7
    //   254: istore 5
    //   256: iload 10
    //   258: istore_3
    //   259: iload 9
    //   261: istore_2
    //   262: getstatic 689	android/os/Build$VERSION:SDK_INT	I
    //   265: bipush 20
    //   267: if_icmple -123 -> 144
    //   270: iload 9
    //   272: bipush 90
    //   274: if_icmpne +20 -> 294
    //   277: iload 7
    //   279: istore 4
    //   281: iload 6
    //   283: istore 5
    //   285: iconst_0
    //   286: istore_2
    //   287: sipush 270
    //   290: istore_3
    //   291: goto -147 -> 144
    //   294: iload 9
    //   296: sipush 180
    //   299: if_icmpne +20 -> 319
    //   302: sipush 180
    //   305: istore_3
    //   306: iconst_0
    //   307: istore_2
    //   308: iload 6
    //   310: istore 4
    //   312: iload 7
    //   314: istore 5
    //   316: goto -172 -> 144
    //   319: iload 6
    //   321: istore 4
    //   323: iload 7
    //   325: istore 5
    //   327: iload 10
    //   329: istore_3
    //   330: iload 9
    //   332: istore_2
    //   333: iload 9
    //   335: sipush 270
    //   338: if_icmpne -194 -> 144
    //   341: iload 7
    //   343: istore 4
    //   345: iload 6
    //   347: istore 5
    //   349: iconst_0
    //   350: istore_2
    //   351: bipush 90
    //   353: istore_3
    //   354: goto -210 -> 144
    //   357: aload_0
    //   358: iconst_1
    //   359: putfield 395	org/telegram/messenger/MediaController:videoConvertFirstWrite	Z
    //   362: iconst_0
    //   363: istore 32
    //   365: iconst_0
    //   366: istore 34
    //   368: invokestatic 1170	java/lang/System:currentTimeMillis	()J
    //   371: lstore 30
    //   373: iload 5
    //   375: ifeq +4959 -> 5334
    //   378: iload 4
    //   380: ifeq +4954 -> 5334
    //   383: aconst_null
    //   384: astore 36
    //   386: aconst_null
    //   387: astore 35
    //   389: aconst_null
    //   390: astore 40
    //   392: aconst_null
    //   393: astore 39
    //   395: aload 35
    //   397: astore 38
    //   399: aload 36
    //   401: astore 37
    //   403: new 1172	android/media/MediaCodec$BufferInfo
    //   406: dup
    //   407: invokespecial 1173	android/media/MediaCodec$BufferInfo:<init>	()V
    //   410: astore 54
    //   412: aload 35
    //   414: astore 38
    //   416: aload 36
    //   418: astore 37
    //   420: new 1175	org/telegram/messenger/video/Mp4Movie
    //   423: dup
    //   424: invokespecial 1176	org/telegram/messenger/video/Mp4Movie:<init>	()V
    //   427: astore 42
    //   429: aload 35
    //   431: astore 38
    //   433: aload 36
    //   435: astore 37
    //   437: aload 42
    //   439: aload 52
    //   441: invokevirtual 1180	org/telegram/messenger/video/Mp4Movie:setCacheFile	(Ljava/io/File;)V
    //   444: aload 35
    //   446: astore 38
    //   448: aload 36
    //   450: astore 37
    //   452: aload 42
    //   454: iload_2
    //   455: invokevirtual 1183	org/telegram/messenger/video/Mp4Movie:setRotation	(I)V
    //   458: aload 35
    //   460: astore 38
    //   462: aload 36
    //   464: astore 37
    //   466: aload 42
    //   468: iload 5
    //   470: iload 4
    //   472: invokevirtual 1187	org/telegram/messenger/video/Mp4Movie:setSize	(II)V
    //   475: aload 35
    //   477: astore 38
    //   479: aload 36
    //   481: astore 37
    //   483: new 1189	org/telegram/messenger/video/MP4Builder
    //   486: dup
    //   487: invokespecial 1190	org/telegram/messenger/video/MP4Builder:<init>	()V
    //   490: aload 42
    //   492: invokevirtual 1194	org/telegram/messenger/video/MP4Builder:createMovie	(Lorg/telegram/messenger/video/Mp4Movie;)Lorg/telegram/messenger/video/MP4Builder;
    //   495: astore 35
    //   497: aload 35
    //   499: astore 38
    //   501: aload 35
    //   503: astore 37
    //   505: new 1196	android/media/MediaExtractor
    //   508: dup
    //   509: invokespecial 1197	android/media/MediaExtractor:<init>	()V
    //   512: astore 36
    //   514: aload 36
    //   516: aload 41
    //   518: invokevirtual 1201	java/io/File:toString	()Ljava/lang/String;
    //   521: invokevirtual 1204	android/media/MediaExtractor:setDataSource	(Ljava/lang/String;)V
    //   524: aload_0
    //   525: invokespecial 1206	org/telegram/messenger/MediaController:checkConversionCanceled	()V
    //   528: iload 5
    //   530: iload 11
    //   532: if_icmpne +10 -> 542
    //   535: iload 4
    //   537: iload 12
    //   539: if_icmpeq +4644 -> 5183
    //   542: aload_0
    //   543: aload 36
    //   545: iconst_0
    //   546: invokespecial 1210	org/telegram/messenger/MediaController:selectTrack	(Landroid/media/MediaExtractor;Z)I
    //   549: istore 20
    //   551: iload 20
    //   553: iflt +4865 -> 5418
    //   556: aconst_null
    //   557: astore 45
    //   559: aconst_null
    //   560: astore 46
    //   562: aconst_null
    //   563: astore 40
    //   565: aconst_null
    //   566: astore 37
    //   568: aconst_null
    //   569: astore 47
    //   571: aconst_null
    //   572: astore 38
    //   574: aconst_null
    //   575: astore 44
    //   577: ldc2_w 1211
    //   580: lstore 24
    //   582: iconst_0
    //   583: istore 15
    //   585: iconst_0
    //   586: istore 9
    //   588: iconst_0
    //   589: istore 16
    //   591: iconst_0
    //   592: istore 11
    //   594: iconst_0
    //   595: istore 7
    //   597: bipush -5
    //   599: istore 14
    //   601: iconst_0
    //   602: istore_2
    //   603: iconst_0
    //   604: istore 6
    //   606: aload 46
    //   608: astore 39
    //   610: aload 40
    //   612: astore 41
    //   614: aload 47
    //   616: astore 42
    //   618: aload 44
    //   620: astore 43
    //   622: getstatic 1217	android/os/Build:MANUFACTURER	Ljava/lang/String;
    //   625: invokevirtual 1220	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   628: astore 48
    //   630: aload 46
    //   632: astore 39
    //   634: aload 40
    //   636: astore 41
    //   638: aload 47
    //   640: astore 42
    //   642: aload 44
    //   644: astore 43
    //   646: getstatic 689	android/os/Build$VERSION:SDK_INT	I
    //   649: bipush 18
    //   651: if_icmpge +4863 -> 5514
    //   654: aload 46
    //   656: astore 39
    //   658: aload 40
    //   660: astore 41
    //   662: aload 47
    //   664: astore 42
    //   666: aload 44
    //   668: astore 43
    //   670: ldc -103
    //   672: invokestatic 1224	org/telegram/messenger/MediaController:selectCodec	(Ljava/lang/String;)Landroid/media/MediaCodecInfo;
    //   675: astore 49
    //   677: aload 46
    //   679: astore 39
    //   681: aload 40
    //   683: astore 41
    //   685: aload 47
    //   687: astore 42
    //   689: aload 44
    //   691: astore 43
    //   693: aload 49
    //   695: ldc -103
    //   697: invokestatic 1228	org/telegram/messenger/MediaController:selectColorFormat	(Landroid/media/MediaCodecInfo;Ljava/lang/String;)I
    //   700: istore 10
    //   702: iload 10
    //   704: ifne +226 -> 930
    //   707: aload 46
    //   709: astore 39
    //   711: aload 40
    //   713: astore 41
    //   715: aload 47
    //   717: astore 42
    //   719: aload 44
    //   721: astore 43
    //   723: new 985	java/lang/RuntimeException
    //   726: dup
    //   727: ldc_w 1230
    //   730: invokespecial 988	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   733: athrow
    //   734: astore 37
    //   736: aload 41
    //   738: astore 40
    //   740: ldc_w 547
    //   743: aload 37
    //   745: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   748: iconst_1
    //   749: istore 32
    //   751: aload 43
    //   753: astore 38
    //   755: aload 42
    //   757: astore 37
    //   759: aload 36
    //   761: iload 20
    //   763: invokevirtual 1233	android/media/MediaExtractor:unselectTrack	(I)V
    //   766: aload 38
    //   768: ifnull +8 -> 776
    //   771: aload 38
    //   773: invokevirtual 1238	org/telegram/messenger/video/OutputSurface:release	()V
    //   776: aload 37
    //   778: ifnull +8 -> 786
    //   781: aload 37
    //   783: invokevirtual 1241	org/telegram/messenger/video/InputSurface:release	()V
    //   786: aload 39
    //   788: ifnull +13 -> 801
    //   791: aload 39
    //   793: invokevirtual 1246	android/media/MediaCodec:stop	()V
    //   796: aload 39
    //   798: invokevirtual 1247	android/media/MediaCodec:release	()V
    //   801: aload 40
    //   803: ifnull +13 -> 816
    //   806: aload 40
    //   808: invokevirtual 1246	android/media/MediaCodec:stop	()V
    //   811: aload 40
    //   813: invokevirtual 1247	android/media/MediaCodec:release	()V
    //   816: aload_0
    //   817: invokespecial 1206	org/telegram/messenger/MediaController:checkConversionCanceled	()V
    //   820: iload 32
    //   822: ifne +22 -> 844
    //   825: aload_0
    //   826: aload_1
    //   827: aload 36
    //   829: aload 35
    //   831: aload 54
    //   833: lload 22
    //   835: lload 28
    //   837: aload 52
    //   839: iconst_1
    //   840: invokespecial 1251	org/telegram/messenger/MediaController:readAndWriteTrack	(Lorg/telegram/messenger/MessageObject;Landroid/media/MediaExtractor;Lorg/telegram/messenger/video/MP4Builder;Landroid/media/MediaCodec$BufferInfo;JJLjava/io/File;Z)J
    //   843: pop2
    //   844: aload 36
    //   846: ifnull +8 -> 854
    //   849: aload 36
    //   851: invokevirtual 1252	android/media/MediaExtractor:release	()V
    //   854: aload 35
    //   856: ifnull +9 -> 865
    //   859: aload 35
    //   861: iconst_0
    //   862: invokevirtual 1255	org/telegram/messenger/video/MP4Builder:finishMovie	(Z)V
    //   865: ldc_w 547
    //   868: new 1257	java/lang/StringBuilder
    //   871: dup
    //   872: invokespecial 1258	java/lang/StringBuilder:<init>	()V
    //   875: ldc_w 1260
    //   878: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   881: invokestatic 1170	java/lang/System:currentTimeMillis	()J
    //   884: lload 30
    //   886: lsub
    //   887: invokevirtual 1267	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   890: invokevirtual 1268	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   893: invokestatic 584	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   896: aload 53
    //   898: invokeinterface 1149 1 0
    //   903: ldc_w 1145
    //   906: iconst_1
    //   907: invokeinterface 1155 3 0
    //   912: invokeinterface 1158 1 0
    //   917: pop
    //   918: aload_0
    //   919: aload_1
    //   920: aload 52
    //   922: iconst_1
    //   923: iload 32
    //   925: invokespecial 1165	org/telegram/messenger/MediaController:didWriteData	(Lorg/telegram/messenger/MessageObject;Ljava/io/File;ZZ)V
    //   928: iconst_1
    //   929: ireturn
    //   930: aload 46
    //   932: astore 39
    //   934: aload 40
    //   936: astore 41
    //   938: aload 47
    //   940: astore 42
    //   942: aload 44
    //   944: astore 43
    //   946: aload 49
    //   948: invokevirtual 1273	android/media/MediaCodecInfo:getName	()Ljava/lang/String;
    //   951: astore 50
    //   953: aload 46
    //   955: astore 39
    //   957: aload 40
    //   959: astore 41
    //   961: aload 47
    //   963: astore 42
    //   965: aload 44
    //   967: astore 43
    //   969: aload 50
    //   971: ldc_w 1275
    //   974: invokevirtual 1279	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   977: ifeq +1485 -> 2462
    //   980: iconst_1
    //   981: istore 11
    //   983: aload 46
    //   985: astore 39
    //   987: aload 40
    //   989: astore 41
    //   991: aload 47
    //   993: astore 42
    //   995: aload 44
    //   997: astore 43
    //   999: iload 11
    //   1001: istore_2
    //   1002: iload 7
    //   1004: istore 6
    //   1006: getstatic 689	android/os/Build$VERSION:SDK_INT	I
    //   1009: bipush 16
    //   1011: if_icmpne +67 -> 1078
    //   1014: aload 46
    //   1016: astore 39
    //   1018: aload 40
    //   1020: astore 41
    //   1022: aload 47
    //   1024: astore 42
    //   1026: aload 44
    //   1028: astore 43
    //   1030: aload 48
    //   1032: ldc_w 1281
    //   1035: invokevirtual 1284	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1038: ifne +4394 -> 5432
    //   1041: aload 46
    //   1043: astore 39
    //   1045: aload 40
    //   1047: astore 41
    //   1049: aload 47
    //   1051: astore 42
    //   1053: aload 44
    //   1055: astore 43
    //   1057: iload 11
    //   1059: istore_2
    //   1060: iload 7
    //   1062: istore 6
    //   1064: aload 48
    //   1066: ldc_w 1286
    //   1069: invokevirtual 1284	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1072: ifeq +6 -> 1078
    //   1075: goto +4357 -> 5432
    //   1078: aload 46
    //   1080: astore 39
    //   1082: aload 40
    //   1084: astore 41
    //   1086: aload 47
    //   1088: astore 42
    //   1090: aload 44
    //   1092: astore 43
    //   1094: ldc_w 547
    //   1097: new 1257	java/lang/StringBuilder
    //   1100: dup
    //   1101: invokespecial 1258	java/lang/StringBuilder:<init>	()V
    //   1104: ldc_w 1288
    //   1107: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1110: aload 49
    //   1112: invokevirtual 1273	android/media/MediaCodecInfo:getName	()Ljava/lang/String;
    //   1115: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1118: ldc_w 1290
    //   1121: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1124: aload 48
    //   1126: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1129: ldc_w 1292
    //   1132: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1135: getstatic 1295	android/os/Build:MODEL	Ljava/lang/String;
    //   1138: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1141: invokevirtual 1268	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1144: invokestatic 584	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   1147: iload 6
    //   1149: istore 11
    //   1151: iload_2
    //   1152: istore 7
    //   1154: aload 46
    //   1156: astore 39
    //   1158: aload 40
    //   1160: astore 41
    //   1162: aload 47
    //   1164: astore 42
    //   1166: aload 44
    //   1168: astore 43
    //   1170: ldc_w 547
    //   1173: new 1257	java/lang/StringBuilder
    //   1176: dup
    //   1177: invokespecial 1258	java/lang/StringBuilder:<init>	()V
    //   1180: ldc_w 1297
    //   1183: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1186: iload 10
    //   1188: invokevirtual 1300	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1191: invokevirtual 1268	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1194: invokestatic 584	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   1197: iconst_0
    //   1198: istore 13
    //   1200: aload 46
    //   1202: astore 39
    //   1204: aload 40
    //   1206: astore 41
    //   1208: aload 47
    //   1210: astore 42
    //   1212: aload 44
    //   1214: astore 43
    //   1216: iload 5
    //   1218: iload 4
    //   1220: imul
    //   1221: iconst_3
    //   1222: imul
    //   1223: iconst_2
    //   1224: idiv
    //   1225: istore 12
    //   1227: iload 7
    //   1229: ifne +1383 -> 2612
    //   1232: iload 12
    //   1234: istore_2
    //   1235: iload 13
    //   1237: istore 6
    //   1239: iload 4
    //   1241: bipush 16
    //   1243: irem
    //   1244: ifeq +48 -> 1292
    //   1247: iload 5
    //   1249: iload 4
    //   1251: bipush 16
    //   1253: iload 4
    //   1255: bipush 16
    //   1257: irem
    //   1258: isub
    //   1259: iadd
    //   1260: iload 4
    //   1262: isub
    //   1263: imul
    //   1264: istore 6
    //   1266: aload 46
    //   1268: astore 39
    //   1270: aload 40
    //   1272: astore 41
    //   1274: aload 47
    //   1276: astore 42
    //   1278: aload 44
    //   1280: astore 43
    //   1282: iload 12
    //   1284: iload 6
    //   1286: iconst_5
    //   1287: imul
    //   1288: iconst_4
    //   1289: idiv
    //   1290: iadd
    //   1291: istore_2
    //   1292: aload 46
    //   1294: astore 39
    //   1296: aload 40
    //   1298: astore 41
    //   1300: aload 47
    //   1302: astore 42
    //   1304: aload 44
    //   1306: astore 43
    //   1308: aload 36
    //   1310: iload 20
    //   1312: invokevirtual 1302	android/media/MediaExtractor:selectTrack	(I)V
    //   1315: lload 22
    //   1317: lconst_0
    //   1318: lcmp
    //   1319: ifle +1474 -> 2793
    //   1322: aload 46
    //   1324: astore 39
    //   1326: aload 40
    //   1328: astore 41
    //   1330: aload 47
    //   1332: astore 42
    //   1334: aload 44
    //   1336: astore 43
    //   1338: aload 36
    //   1340: lload 22
    //   1342: iconst_0
    //   1343: invokevirtual 1306	android/media/MediaExtractor:seekTo	(JI)V
    //   1346: aload 46
    //   1348: astore 39
    //   1350: aload 40
    //   1352: astore 41
    //   1354: aload 47
    //   1356: astore 42
    //   1358: aload 44
    //   1360: astore 43
    //   1362: aload 36
    //   1364: iload 20
    //   1366: invokevirtual 1310	android/media/MediaExtractor:getTrackFormat	(I)Landroid/media/MediaFormat;
    //   1369: astore 48
    //   1371: aload 46
    //   1373: astore 39
    //   1375: aload 40
    //   1377: astore 41
    //   1379: aload 47
    //   1381: astore 42
    //   1383: aload 44
    //   1385: astore 43
    //   1387: ldc -103
    //   1389: iload 5
    //   1391: iload 4
    //   1393: invokestatic 1316	android/media/MediaFormat:createVideoFormat	(Ljava/lang/String;II)Landroid/media/MediaFormat;
    //   1396: astore 49
    //   1398: aload 46
    //   1400: astore 39
    //   1402: aload 40
    //   1404: astore 41
    //   1406: aload 47
    //   1408: astore 42
    //   1410: aload 44
    //   1412: astore 43
    //   1414: aload 49
    //   1416: ldc_w 1318
    //   1419: iload 10
    //   1421: invokevirtual 1321	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1424: iload 8
    //   1426: ifeq +1448 -> 2874
    //   1429: iload 8
    //   1431: istore 7
    //   1433: aload 46
    //   1435: astore 39
    //   1437: aload 40
    //   1439: astore 41
    //   1441: aload 47
    //   1443: astore 42
    //   1445: aload 44
    //   1447: astore 43
    //   1449: aload 49
    //   1451: ldc_w 1322
    //   1454: iload 7
    //   1456: invokevirtual 1321	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1459: aload 46
    //   1461: astore 39
    //   1463: aload 40
    //   1465: astore 41
    //   1467: aload 47
    //   1469: astore 42
    //   1471: aload 44
    //   1473: astore 43
    //   1475: aload 49
    //   1477: ldc_w 1324
    //   1480: bipush 25
    //   1482: invokevirtual 1321	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1485: aload 46
    //   1487: astore 39
    //   1489: aload 40
    //   1491: astore 41
    //   1493: aload 47
    //   1495: astore 42
    //   1497: aload 44
    //   1499: astore 43
    //   1501: aload 49
    //   1503: ldc_w 1326
    //   1506: bipush 10
    //   1508: invokevirtual 1321	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1511: aload 46
    //   1513: astore 39
    //   1515: aload 40
    //   1517: astore 41
    //   1519: aload 47
    //   1521: astore 42
    //   1523: aload 44
    //   1525: astore 43
    //   1527: getstatic 689	android/os/Build$VERSION:SDK_INT	I
    //   1530: bipush 18
    //   1532: if_icmpge +58 -> 1590
    //   1535: aload 46
    //   1537: astore 39
    //   1539: aload 40
    //   1541: astore 41
    //   1543: aload 47
    //   1545: astore 42
    //   1547: aload 44
    //   1549: astore 43
    //   1551: aload 49
    //   1553: ldc_w 1328
    //   1556: iload 5
    //   1558: bipush 32
    //   1560: iadd
    //   1561: invokevirtual 1321	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1564: aload 46
    //   1566: astore 39
    //   1568: aload 40
    //   1570: astore 41
    //   1572: aload 47
    //   1574: astore 42
    //   1576: aload 44
    //   1578: astore 43
    //   1580: aload 49
    //   1582: ldc_w 1330
    //   1585: iload 4
    //   1587: invokevirtual 1321	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1590: aload 46
    //   1592: astore 39
    //   1594: aload 40
    //   1596: astore 41
    //   1598: aload 47
    //   1600: astore 42
    //   1602: aload 44
    //   1604: astore 43
    //   1606: ldc -103
    //   1608: invokestatic 1334	android/media/MediaCodec:createEncoderByType	(Ljava/lang/String;)Landroid/media/MediaCodec;
    //   1611: astore 40
    //   1613: aload 46
    //   1615: astore 39
    //   1617: aload 40
    //   1619: astore 41
    //   1621: aload 47
    //   1623: astore 42
    //   1625: aload 44
    //   1627: astore 43
    //   1629: aload 40
    //   1631: aload 49
    //   1633: aconst_null
    //   1634: aconst_null
    //   1635: iconst_1
    //   1636: invokevirtual 1338	android/media/MediaCodec:configure	(Landroid/media/MediaFormat;Landroid/view/Surface;Landroid/media/MediaCrypto;I)V
    //   1639: aload 46
    //   1641: astore 39
    //   1643: aload 40
    //   1645: astore 41
    //   1647: aload 47
    //   1649: astore 42
    //   1651: aload 44
    //   1653: astore 43
    //   1655: getstatic 689	android/os/Build$VERSION:SDK_INT	I
    //   1658: bipush 18
    //   1660: if_icmplt +38 -> 1698
    //   1663: aload 46
    //   1665: astore 39
    //   1667: aload 40
    //   1669: astore 41
    //   1671: aload 47
    //   1673: astore 42
    //   1675: aload 44
    //   1677: astore 43
    //   1679: new 1240	org/telegram/messenger/video/InputSurface
    //   1682: dup
    //   1683: aload 40
    //   1685: invokevirtual 1342	android/media/MediaCodec:createInputSurface	()Landroid/view/Surface;
    //   1688: invokespecial 1345	org/telegram/messenger/video/InputSurface:<init>	(Landroid/view/Surface;)V
    //   1691: astore 37
    //   1693: aload 37
    //   1695: invokevirtual 1348	org/telegram/messenger/video/InputSurface:makeCurrent	()V
    //   1698: aload 46
    //   1700: astore 39
    //   1702: aload 40
    //   1704: astore 41
    //   1706: aload 37
    //   1708: astore 42
    //   1710: aload 44
    //   1712: astore 43
    //   1714: aload 40
    //   1716: invokevirtual 1351	android/media/MediaCodec:start	()V
    //   1719: aload 46
    //   1721: astore 39
    //   1723: aload 40
    //   1725: astore 41
    //   1727: aload 37
    //   1729: astore 42
    //   1731: aload 44
    //   1733: astore 43
    //   1735: aload 48
    //   1737: ldc_w 1353
    //   1740: invokevirtual 1357	android/media/MediaFormat:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   1743: invokestatic 1360	android/media/MediaCodec:createDecoderByType	(Ljava/lang/String;)Landroid/media/MediaCodec;
    //   1746: astore 45
    //   1748: aload 45
    //   1750: astore 39
    //   1752: aload 40
    //   1754: astore 41
    //   1756: aload 37
    //   1758: astore 42
    //   1760: aload 44
    //   1762: astore 43
    //   1764: getstatic 689	android/os/Build$VERSION:SDK_INT	I
    //   1767: bipush 18
    //   1769: if_icmplt +1113 -> 2882
    //   1772: aload 45
    //   1774: astore 39
    //   1776: aload 40
    //   1778: astore 41
    //   1780: aload 37
    //   1782: astore 42
    //   1784: aload 44
    //   1786: astore 43
    //   1788: new 1235	org/telegram/messenger/video/OutputSurface
    //   1791: dup
    //   1792: invokespecial 1361	org/telegram/messenger/video/OutputSurface:<init>	()V
    //   1795: astore 38
    //   1797: aload 45
    //   1799: astore 39
    //   1801: aload 40
    //   1803: astore 41
    //   1805: aload 37
    //   1807: astore 42
    //   1809: aload 38
    //   1811: astore 43
    //   1813: aload 45
    //   1815: aload 48
    //   1817: aload 38
    //   1819: invokevirtual 1364	org/telegram/messenger/video/OutputSurface:getSurface	()Landroid/view/Surface;
    //   1822: aconst_null
    //   1823: iconst_0
    //   1824: invokevirtual 1338	android/media/MediaCodec:configure	(Landroid/media/MediaFormat;Landroid/view/Surface;Landroid/media/MediaCrypto;I)V
    //   1827: aload 45
    //   1829: astore 39
    //   1831: aload 40
    //   1833: astore 41
    //   1835: aload 37
    //   1837: astore 42
    //   1839: aload 38
    //   1841: astore 43
    //   1843: aload 45
    //   1845: invokevirtual 1351	android/media/MediaCodec:start	()V
    //   1848: aconst_null
    //   1849: astore 46
    //   1851: aconst_null
    //   1852: astore 44
    //   1854: aconst_null
    //   1855: astore 48
    //   1857: aload 45
    //   1859: astore 39
    //   1861: aload 40
    //   1863: astore 41
    //   1865: aload 37
    //   1867: astore 42
    //   1869: aload 38
    //   1871: astore 43
    //   1873: aload 48
    //   1875: astore 47
    //   1877: getstatic 689	android/os/Build$VERSION:SDK_INT	I
    //   1880: bipush 21
    //   1882: if_icmpge +116 -> 1998
    //   1885: aload 45
    //   1887: astore 39
    //   1889: aload 40
    //   1891: astore 41
    //   1893: aload 37
    //   1895: astore 42
    //   1897: aload 38
    //   1899: astore 43
    //   1901: aload 45
    //   1903: invokevirtual 1368	android/media/MediaCodec:getInputBuffers	()[Ljava/nio/ByteBuffer;
    //   1906: astore 49
    //   1908: aload 45
    //   1910: astore 39
    //   1912: aload 40
    //   1914: astore 41
    //   1916: aload 37
    //   1918: astore 42
    //   1920: aload 38
    //   1922: astore 43
    //   1924: aload 40
    //   1926: invokevirtual 1371	android/media/MediaCodec:getOutputBuffers	()[Ljava/nio/ByteBuffer;
    //   1929: astore 50
    //   1931: aload 45
    //   1933: astore 39
    //   1935: aload 40
    //   1937: astore 41
    //   1939: aload 37
    //   1941: astore 42
    //   1943: aload 38
    //   1945: astore 43
    //   1947: aload 49
    //   1949: astore 46
    //   1951: aload 48
    //   1953: astore 47
    //   1955: aload 50
    //   1957: astore 44
    //   1959: getstatic 689	android/os/Build$VERSION:SDK_INT	I
    //   1962: bipush 18
    //   1964: if_icmpge +34 -> 1998
    //   1967: aload 45
    //   1969: astore 39
    //   1971: aload 40
    //   1973: astore 41
    //   1975: aload 37
    //   1977: astore 42
    //   1979: aload 38
    //   1981: astore 43
    //   1983: aload 40
    //   1985: invokevirtual 1368	android/media/MediaCodec:getInputBuffers	()[Ljava/nio/ByteBuffer;
    //   1988: astore 47
    //   1990: aload 50
    //   1992: astore 44
    //   1994: aload 49
    //   1996: astore 46
    //   1998: aload 45
    //   2000: astore 39
    //   2002: aload 40
    //   2004: astore 41
    //   2006: aload 37
    //   2008: astore 42
    //   2010: aload 38
    //   2012: astore 43
    //   2014: aload_0
    //   2015: invokespecial 1206	org/telegram/messenger/MediaController:checkConversionCanceled	()V
    //   2018: iload 9
    //   2020: istore_3
    //   2021: aload 44
    //   2023: astore 48
    //   2025: iload 15
    //   2027: ifne +3132 -> 5159
    //   2030: aload 45
    //   2032: astore 39
    //   2034: aload 40
    //   2036: astore 41
    //   2038: aload 37
    //   2040: astore 42
    //   2042: aload 38
    //   2044: astore 43
    //   2046: aload_0
    //   2047: invokespecial 1206	org/telegram/messenger/MediaController:checkConversionCanceled	()V
    //   2050: iload_3
    //   2051: istore 7
    //   2053: iload_3
    //   2054: ifne +3387 -> 5441
    //   2057: iconst_0
    //   2058: istore 7
    //   2060: aload 45
    //   2062: astore 39
    //   2064: aload 40
    //   2066: astore 41
    //   2068: aload 37
    //   2070: astore 42
    //   2072: aload 38
    //   2074: astore 43
    //   2076: aload 36
    //   2078: invokevirtual 1374	android/media/MediaExtractor:getSampleTrackIndex	()I
    //   2081: istore 12
    //   2083: iload 12
    //   2085: iload 20
    //   2087: if_icmpne +3438 -> 5525
    //   2090: aload 45
    //   2092: astore 39
    //   2094: aload 40
    //   2096: astore 41
    //   2098: aload 37
    //   2100: astore 42
    //   2102: aload 38
    //   2104: astore 43
    //   2106: aload 45
    //   2108: ldc2_w 1375
    //   2111: invokevirtual 1380	android/media/MediaCodec:dequeueInputBuffer	(J)I
    //   2114: istore 12
    //   2116: iload 7
    //   2118: istore 9
    //   2120: iload_3
    //   2121: istore 8
    //   2123: iload 12
    //   2125: iflt +99 -> 2224
    //   2128: aload 45
    //   2130: astore 39
    //   2132: aload 40
    //   2134: astore 41
    //   2136: aload 37
    //   2138: astore 42
    //   2140: aload 38
    //   2142: astore 43
    //   2144: getstatic 689	android/os/Build$VERSION:SDK_INT	I
    //   2147: bipush 21
    //   2149: if_icmpge +766 -> 2915
    //   2152: aload 46
    //   2154: iload 12
    //   2156: aaload
    //   2157: astore 44
    //   2159: aload 45
    //   2161: astore 39
    //   2163: aload 40
    //   2165: astore 41
    //   2167: aload 37
    //   2169: astore 42
    //   2171: aload 38
    //   2173: astore 43
    //   2175: aload 36
    //   2177: aload 44
    //   2179: iconst_0
    //   2180: invokevirtual 1383	android/media/MediaExtractor:readSampleData	(Ljava/nio/ByteBuffer;I)I
    //   2183: istore 8
    //   2185: iload 8
    //   2187: ifge +756 -> 2943
    //   2190: aload 45
    //   2192: astore 39
    //   2194: aload 40
    //   2196: astore 41
    //   2198: aload 37
    //   2200: astore 42
    //   2202: aload 38
    //   2204: astore 43
    //   2206: aload 45
    //   2208: iload 12
    //   2210: iconst_0
    //   2211: iconst_0
    //   2212: lconst_0
    //   2213: iconst_4
    //   2214: invokevirtual 1387	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   2217: iconst_1
    //   2218: istore 8
    //   2220: iload 7
    //   2222: istore 9
    //   2224: iload 8
    //   2226: istore 7
    //   2228: iload 9
    //   2230: ifeq +3211 -> 5441
    //   2233: aload 45
    //   2235: astore 39
    //   2237: aload 40
    //   2239: astore 41
    //   2241: aload 37
    //   2243: astore 42
    //   2245: aload 38
    //   2247: astore 43
    //   2249: aload 45
    //   2251: ldc2_w 1375
    //   2254: invokevirtual 1380	android/media/MediaCodec:dequeueInputBuffer	(J)I
    //   2257: istore_3
    //   2258: iload 8
    //   2260: istore 7
    //   2262: iload_3
    //   2263: iflt +3178 -> 5441
    //   2266: aload 45
    //   2268: astore 39
    //   2270: aload 40
    //   2272: astore 41
    //   2274: aload 37
    //   2276: astore 42
    //   2278: aload 38
    //   2280: astore 43
    //   2282: aload 45
    //   2284: iload_3
    //   2285: iconst_0
    //   2286: iconst_0
    //   2287: lconst_0
    //   2288: iconst_4
    //   2289: invokevirtual 1387	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   2292: iconst_1
    //   2293: istore 7
    //   2295: goto +3146 -> 5441
    //   2298: aload 45
    //   2300: astore 39
    //   2302: aload 40
    //   2304: astore 41
    //   2306: aload 37
    //   2308: astore 42
    //   2310: aload 38
    //   2312: astore 43
    //   2314: aload_0
    //   2315: invokespecial 1206	org/telegram/messenger/MediaController:checkConversionCanceled	()V
    //   2318: aload 45
    //   2320: astore 39
    //   2322: aload 40
    //   2324: astore 41
    //   2326: aload 37
    //   2328: astore 42
    //   2330: aload 38
    //   2332: astore 43
    //   2334: aload 40
    //   2336: aload 54
    //   2338: ldc2_w 1375
    //   2341: invokevirtual 1391	android/media/MediaCodec:dequeueOutputBuffer	(Landroid/media/MediaCodec$BufferInfo;J)I
    //   2344: istore 16
    //   2346: iload 16
    //   2348: iconst_m1
    //   2349: if_icmpne +658 -> 3007
    //   2352: iconst_0
    //   2353: istore 14
    //   2355: iload 9
    //   2357: istore_3
    //   2358: iload 17
    //   2360: istore 15
    //   2362: aload 44
    //   2364: astore 48
    //   2366: iload 14
    //   2368: istore 13
    //   2370: aload 48
    //   2372: astore 44
    //   2374: iload 15
    //   2376: istore 17
    //   2378: iload_3
    //   2379: istore 9
    //   2381: iload 16
    //   2383: iconst_m1
    //   2384: if_icmpne +3094 -> 5478
    //   2387: iload 14
    //   2389: istore 13
    //   2391: aload 48
    //   2393: astore 44
    //   2395: iload 15
    //   2397: istore 17
    //   2399: iload_3
    //   2400: istore 9
    //   2402: iload 12
    //   2404: ifne +3074 -> 5478
    //   2407: aload 45
    //   2409: astore 39
    //   2411: aload 40
    //   2413: astore 41
    //   2415: aload 37
    //   2417: astore 42
    //   2419: aload 38
    //   2421: astore 43
    //   2423: aload 45
    //   2425: aload 54
    //   2427: ldc2_w 1375
    //   2430: invokevirtual 1391	android/media/MediaCodec:dequeueOutputBuffer	(Landroid/media/MediaCodec$BufferInfo;J)I
    //   2433: istore 19
    //   2435: iload 19
    //   2437: iconst_m1
    //   2438: if_icmpne +1545 -> 3983
    //   2441: iconst_0
    //   2442: istore 7
    //   2444: iload 14
    //   2446: istore 13
    //   2448: aload 48
    //   2450: astore 44
    //   2452: iload 15
    //   2454: istore 17
    //   2456: iload_3
    //   2457: istore 9
    //   2459: goto +3019 -> 5478
    //   2462: aload 46
    //   2464: astore 39
    //   2466: aload 40
    //   2468: astore 41
    //   2470: aload 47
    //   2472: astore 42
    //   2474: aload 44
    //   2476: astore 43
    //   2478: aload 50
    //   2480: ldc_w 1393
    //   2483: invokevirtual 1279	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   2486: ifeq +12 -> 2498
    //   2489: iconst_2
    //   2490: istore_2
    //   2491: iload 7
    //   2493: istore 6
    //   2495: goto -1417 -> 1078
    //   2498: aload 46
    //   2500: astore 39
    //   2502: aload 40
    //   2504: astore 41
    //   2506: aload 47
    //   2508: astore 42
    //   2510: aload 44
    //   2512: astore 43
    //   2514: aload 50
    //   2516: ldc_w 1395
    //   2519: invokevirtual 1284	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2522: ifeq +12 -> 2534
    //   2525: iconst_3
    //   2526: istore_2
    //   2527: iload 7
    //   2529: istore 6
    //   2531: goto -1453 -> 1078
    //   2534: aload 46
    //   2536: astore 39
    //   2538: aload 40
    //   2540: astore 41
    //   2542: aload 47
    //   2544: astore 42
    //   2546: aload 44
    //   2548: astore 43
    //   2550: aload 50
    //   2552: ldc_w 1397
    //   2555: invokevirtual 1284	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2558: ifeq +11 -> 2569
    //   2561: iconst_4
    //   2562: istore_2
    //   2563: iconst_1
    //   2564: istore 6
    //   2566: goto -1488 -> 1078
    //   2569: aload 46
    //   2571: astore 39
    //   2573: aload 40
    //   2575: astore 41
    //   2577: aload 47
    //   2579: astore 42
    //   2581: aload 44
    //   2583: astore 43
    //   2585: iload 6
    //   2587: istore_2
    //   2588: iload 7
    //   2590: istore 6
    //   2592: aload 50
    //   2594: ldc_w 1399
    //   2597: invokevirtual 1284	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2600: ifeq -1522 -> 1078
    //   2603: iconst_5
    //   2604: istore_2
    //   2605: iload 7
    //   2607: istore 6
    //   2609: goto -1531 -> 1078
    //   2612: iload 7
    //   2614: iconst_1
    //   2615: if_icmpne +70 -> 2685
    //   2618: aload 46
    //   2620: astore 39
    //   2622: aload 40
    //   2624: astore 41
    //   2626: aload 47
    //   2628: astore 42
    //   2630: aload 44
    //   2632: astore 43
    //   2634: iload 12
    //   2636: istore_2
    //   2637: iload 13
    //   2639: istore 6
    //   2641: aload 48
    //   2643: invokevirtual 1220	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   2646: ldc_w 1281
    //   2649: invokevirtual 1284	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2652: ifne -1360 -> 1292
    //   2655: iload 5
    //   2657: iload 4
    //   2659: imul
    //   2660: sipush 2047
    //   2663: iadd
    //   2664: sipush 63488
    //   2667: iand
    //   2668: iload 5
    //   2670: iload 4
    //   2672: imul
    //   2673: isub
    //   2674: istore 6
    //   2676: iload 12
    //   2678: iload 6
    //   2680: iadd
    //   2681: istore_2
    //   2682: goto -1390 -> 1292
    //   2685: iload 12
    //   2687: istore_2
    //   2688: iload 13
    //   2690: istore 6
    //   2692: iload 7
    //   2694: iconst_5
    //   2695: if_icmpeq -1403 -> 1292
    //   2698: iload 12
    //   2700: istore_2
    //   2701: iload 13
    //   2703: istore 6
    //   2705: iload 7
    //   2707: iconst_3
    //   2708: if_icmpne -1416 -> 1292
    //   2711: aload 46
    //   2713: astore 39
    //   2715: aload 40
    //   2717: astore 41
    //   2719: aload 47
    //   2721: astore 42
    //   2723: aload 44
    //   2725: astore 43
    //   2727: iload 12
    //   2729: istore_2
    //   2730: iload 13
    //   2732: istore 6
    //   2734: aload 48
    //   2736: ldc_w 1401
    //   2739: invokevirtual 1284	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2742: ifeq -1450 -> 1292
    //   2745: iload 5
    //   2747: iload 4
    //   2749: bipush 16
    //   2751: iload 4
    //   2753: bipush 16
    //   2755: irem
    //   2756: isub
    //   2757: iadd
    //   2758: iload 4
    //   2760: isub
    //   2761: imul
    //   2762: istore 6
    //   2764: aload 46
    //   2766: astore 39
    //   2768: aload 40
    //   2770: astore 41
    //   2772: aload 47
    //   2774: astore 42
    //   2776: aload 44
    //   2778: astore 43
    //   2780: iload 12
    //   2782: iload 6
    //   2784: iconst_5
    //   2785: imul
    //   2786: iconst_4
    //   2787: idiv
    //   2788: iadd
    //   2789: istore_2
    //   2790: goto -1498 -> 1292
    //   2793: aload 46
    //   2795: astore 39
    //   2797: aload 40
    //   2799: astore 41
    //   2801: aload 47
    //   2803: astore 42
    //   2805: aload 44
    //   2807: astore 43
    //   2809: aload 36
    //   2811: lconst_0
    //   2812: iconst_0
    //   2813: invokevirtual 1306	android/media/MediaExtractor:seekTo	(JI)V
    //   2816: goto -1470 -> 1346
    //   2819: astore_1
    //   2820: aload 36
    //   2822: ifnull +8 -> 2830
    //   2825: aload 36
    //   2827: invokevirtual 1252	android/media/MediaExtractor:release	()V
    //   2830: aload 35
    //   2832: ifnull +9 -> 2841
    //   2835: aload 35
    //   2837: iconst_0
    //   2838: invokevirtual 1255	org/telegram/messenger/video/MP4Builder:finishMovie	(Z)V
    //   2841: ldc_w 547
    //   2844: new 1257	java/lang/StringBuilder
    //   2847: dup
    //   2848: invokespecial 1258	java/lang/StringBuilder:<init>	()V
    //   2851: ldc_w 1260
    //   2854: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2857: invokestatic 1170	java/lang/System:currentTimeMillis	()J
    //   2860: lload 30
    //   2862: lsub
    //   2863: invokevirtual 1267	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   2866: invokevirtual 1268	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2869: invokestatic 584	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   2872: aload_1
    //   2873: athrow
    //   2874: ldc_w 1402
    //   2877: istore 7
    //   2879: goto -1446 -> 1433
    //   2882: aload 45
    //   2884: astore 39
    //   2886: aload 40
    //   2888: astore 41
    //   2890: aload 37
    //   2892: astore 42
    //   2894: aload 44
    //   2896: astore 43
    //   2898: new 1235	org/telegram/messenger/video/OutputSurface
    //   2901: dup
    //   2902: iload 5
    //   2904: iload 4
    //   2906: iload_3
    //   2907: invokespecial 1405	org/telegram/messenger/video/OutputSurface:<init>	(III)V
    //   2910: astore 38
    //   2912: goto -1115 -> 1797
    //   2915: aload 45
    //   2917: astore 39
    //   2919: aload 40
    //   2921: astore 41
    //   2923: aload 37
    //   2925: astore 42
    //   2927: aload 38
    //   2929: astore 43
    //   2931: aload 45
    //   2933: iload 12
    //   2935: invokevirtual 1408	android/media/MediaCodec:getInputBuffer	(I)Ljava/nio/ByteBuffer;
    //   2938: astore 44
    //   2940: goto -781 -> 2159
    //   2943: aload 45
    //   2945: astore 39
    //   2947: aload 40
    //   2949: astore 41
    //   2951: aload 37
    //   2953: astore 42
    //   2955: aload 38
    //   2957: astore 43
    //   2959: aload 45
    //   2961: iload 12
    //   2963: iconst_0
    //   2964: iload 8
    //   2966: aload 36
    //   2968: invokevirtual 1411	android/media/MediaExtractor:getSampleTime	()J
    //   2971: iconst_0
    //   2972: invokevirtual 1387	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   2975: aload 45
    //   2977: astore 39
    //   2979: aload 40
    //   2981: astore 41
    //   2983: aload 37
    //   2985: astore 42
    //   2987: aload 38
    //   2989: astore 43
    //   2991: aload 36
    //   2993: invokevirtual 1414	android/media/MediaExtractor:advance	()Z
    //   2996: pop
    //   2997: iload 7
    //   2999: istore 9
    //   3001: iload_3
    //   3002: istore 8
    //   3004: goto -780 -> 2224
    //   3007: iload 16
    //   3009: bipush -3
    //   3011: if_icmpne +79 -> 3090
    //   3014: aload 45
    //   3016: astore 39
    //   3018: aload 40
    //   3020: astore 41
    //   3022: aload 37
    //   3024: astore 42
    //   3026: aload 38
    //   3028: astore 43
    //   3030: iload 13
    //   3032: istore 14
    //   3034: aload 44
    //   3036: astore 48
    //   3038: iload 17
    //   3040: istore 15
    //   3042: iload 9
    //   3044: istore_3
    //   3045: getstatic 689	android/os/Build$VERSION:SDK_INT	I
    //   3048: bipush 21
    //   3050: if_icmpge -684 -> 2366
    //   3053: aload 45
    //   3055: astore 39
    //   3057: aload 40
    //   3059: astore 41
    //   3061: aload 37
    //   3063: astore 42
    //   3065: aload 38
    //   3067: astore 43
    //   3069: aload 40
    //   3071: invokevirtual 1371	android/media/MediaCodec:getOutputBuffers	()[Ljava/nio/ByteBuffer;
    //   3074: astore 48
    //   3076: iload 13
    //   3078: istore 14
    //   3080: iload 17
    //   3082: istore 15
    //   3084: iload 9
    //   3086: istore_3
    //   3087: goto -721 -> 2366
    //   3090: iload 16
    //   3092: bipush -2
    //   3094: if_icmpne +88 -> 3182
    //   3097: aload 45
    //   3099: astore 39
    //   3101: aload 40
    //   3103: astore 41
    //   3105: aload 37
    //   3107: astore 42
    //   3109: aload 38
    //   3111: astore 43
    //   3113: aload 40
    //   3115: invokevirtual 1418	android/media/MediaCodec:getOutputFormat	()Landroid/media/MediaFormat;
    //   3118: astore 49
    //   3120: iload 13
    //   3122: istore 14
    //   3124: aload 44
    //   3126: astore 48
    //   3128: iload 17
    //   3130: istore 15
    //   3132: iload 9
    //   3134: istore_3
    //   3135: iload 9
    //   3137: bipush -5
    //   3139: if_icmpne -773 -> 2366
    //   3142: aload 45
    //   3144: astore 39
    //   3146: aload 40
    //   3148: astore 41
    //   3150: aload 37
    //   3152: astore 42
    //   3154: aload 38
    //   3156: astore 43
    //   3158: aload 35
    //   3160: aload 49
    //   3162: iconst_0
    //   3163: invokevirtual 1422	org/telegram/messenger/video/MP4Builder:addTrack	(Landroid/media/MediaFormat;Z)I
    //   3166: istore_3
    //   3167: iload 13
    //   3169: istore 14
    //   3171: aload 44
    //   3173: astore 48
    //   3175: iload 17
    //   3177: istore 15
    //   3179: goto -813 -> 2366
    //   3182: iload 16
    //   3184: ifge +48 -> 3232
    //   3187: aload 45
    //   3189: astore 39
    //   3191: aload 40
    //   3193: astore 41
    //   3195: aload 37
    //   3197: astore 42
    //   3199: aload 38
    //   3201: astore 43
    //   3203: new 985	java/lang/RuntimeException
    //   3206: dup
    //   3207: new 1257	java/lang/StringBuilder
    //   3210: dup
    //   3211: invokespecial 1258	java/lang/StringBuilder:<init>	()V
    //   3214: ldc_w 1424
    //   3217: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3220: iload 16
    //   3222: invokevirtual 1300	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3225: invokevirtual 1268	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3228: invokespecial 988	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   3231: athrow
    //   3232: aload 45
    //   3234: astore 39
    //   3236: aload 40
    //   3238: astore 41
    //   3240: aload 37
    //   3242: astore 42
    //   3244: aload 38
    //   3246: astore 43
    //   3248: getstatic 689	android/os/Build$VERSION:SDK_INT	I
    //   3251: bipush 21
    //   3253: if_icmpge +66 -> 3319
    //   3256: aload 44
    //   3258: iload 16
    //   3260: aaload
    //   3261: astore 48
    //   3263: aload 48
    //   3265: ifnonnull +82 -> 3347
    //   3268: aload 45
    //   3270: astore 39
    //   3272: aload 40
    //   3274: astore 41
    //   3276: aload 37
    //   3278: astore 42
    //   3280: aload 38
    //   3282: astore 43
    //   3284: new 985	java/lang/RuntimeException
    //   3287: dup
    //   3288: new 1257	java/lang/StringBuilder
    //   3291: dup
    //   3292: invokespecial 1258	java/lang/StringBuilder:<init>	()V
    //   3295: ldc_w 1426
    //   3298: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3301: iload 16
    //   3303: invokevirtual 1300	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3306: ldc_w 1428
    //   3309: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3312: invokevirtual 1268	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3315: invokespecial 988	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   3318: athrow
    //   3319: aload 45
    //   3321: astore 39
    //   3323: aload 40
    //   3325: astore 41
    //   3327: aload 37
    //   3329: astore 42
    //   3331: aload 38
    //   3333: astore 43
    //   3335: aload 40
    //   3337: iload 16
    //   3339: invokevirtual 1431	android/media/MediaCodec:getOutputBuffer	(I)Ljava/nio/ByteBuffer;
    //   3342: astore 48
    //   3344: goto -81 -> 3263
    //   3347: aload 45
    //   3349: astore 39
    //   3351: aload 40
    //   3353: astore 41
    //   3355: aload 37
    //   3357: astore 42
    //   3359: aload 38
    //   3361: astore 43
    //   3363: iload 9
    //   3365: istore_3
    //   3366: aload 54
    //   3368: getfield 1433	android/media/MediaCodec$BufferInfo:size	I
    //   3371: iconst_1
    //   3372: if_icmple +91 -> 3463
    //   3375: aload 45
    //   3377: astore 39
    //   3379: aload 40
    //   3381: astore 41
    //   3383: aload 37
    //   3385: astore 42
    //   3387: aload 38
    //   3389: astore 43
    //   3391: aload 54
    //   3393: getfield 1436	android/media/MediaCodec$BufferInfo:flags	I
    //   3396: iconst_2
    //   3397: iand
    //   3398: ifne +133 -> 3531
    //   3401: aload 45
    //   3403: astore 39
    //   3405: aload 40
    //   3407: astore 41
    //   3409: aload 37
    //   3411: astore 42
    //   3413: aload 38
    //   3415: astore 43
    //   3417: iload 9
    //   3419: istore_3
    //   3420: aload 35
    //   3422: iload 9
    //   3424: aload 48
    //   3426: aload 54
    //   3428: iconst_0
    //   3429: invokevirtual 1440	org/telegram/messenger/video/MP4Builder:writeSampleData	(ILjava/nio/ByteBuffer;Landroid/media/MediaCodec$BufferInfo;Z)Z
    //   3432: ifeq +31 -> 3463
    //   3435: aload 45
    //   3437: astore 39
    //   3439: aload 40
    //   3441: astore 41
    //   3443: aload 37
    //   3445: astore 42
    //   3447: aload 38
    //   3449: astore 43
    //   3451: aload_0
    //   3452: aload_1
    //   3453: aload 52
    //   3455: iconst_0
    //   3456: iconst_0
    //   3457: invokespecial 1165	org/telegram/messenger/MediaController:didWriteData	(Lorg/telegram/messenger/MessageObject;Ljava/io/File;ZZ)V
    //   3460: iload 9
    //   3462: istore_3
    //   3463: aload 45
    //   3465: astore 39
    //   3467: aload 40
    //   3469: astore 41
    //   3471: aload 37
    //   3473: astore 42
    //   3475: aload 38
    //   3477: astore 43
    //   3479: aload 54
    //   3481: getfield 1436	android/media/MediaCodec$BufferInfo:flags	I
    //   3484: iconst_4
    //   3485: iand
    //   3486: ifeq +2073 -> 5559
    //   3489: iconst_1
    //   3490: istore 9
    //   3492: aload 45
    //   3494: astore 39
    //   3496: aload 40
    //   3498: astore 41
    //   3500: aload 37
    //   3502: astore 42
    //   3504: aload 38
    //   3506: astore 43
    //   3508: aload 40
    //   3510: iload 16
    //   3512: iconst_0
    //   3513: invokevirtual 1444	android/media/MediaCodec:releaseOutputBuffer	(IZ)V
    //   3516: iload 13
    //   3518: istore 14
    //   3520: aload 44
    //   3522: astore 48
    //   3524: iload 9
    //   3526: istore 15
    //   3528: goto -1162 -> 2366
    //   3531: iload 9
    //   3533: istore_3
    //   3534: iload 9
    //   3536: bipush -5
    //   3538: if_icmpne -75 -> 3463
    //   3541: aload 45
    //   3543: astore 39
    //   3545: aload 40
    //   3547: astore 41
    //   3549: aload 37
    //   3551: astore 42
    //   3553: aload 38
    //   3555: astore 43
    //   3557: aload 54
    //   3559: getfield 1433	android/media/MediaCodec$BufferInfo:size	I
    //   3562: newarray <illegal type>
    //   3564: astore 55
    //   3566: aload 45
    //   3568: astore 39
    //   3570: aload 40
    //   3572: astore 41
    //   3574: aload 37
    //   3576: astore 42
    //   3578: aload 38
    //   3580: astore 43
    //   3582: aload 48
    //   3584: aload 54
    //   3586: getfield 1447	android/media/MediaCodec$BufferInfo:offset	I
    //   3589: aload 54
    //   3591: getfield 1433	android/media/MediaCodec$BufferInfo:size	I
    //   3594: iadd
    //   3595: invokevirtual 1451	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
    //   3598: pop
    //   3599: aload 45
    //   3601: astore 39
    //   3603: aload 40
    //   3605: astore 41
    //   3607: aload 37
    //   3609: astore 42
    //   3611: aload 38
    //   3613: astore 43
    //   3615: aload 48
    //   3617: aload 54
    //   3619: getfield 1447	android/media/MediaCodec$BufferInfo:offset	I
    //   3622: invokevirtual 1454	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   3625: pop
    //   3626: aload 45
    //   3628: astore 39
    //   3630: aload 40
    //   3632: astore 41
    //   3634: aload 37
    //   3636: astore 42
    //   3638: aload 38
    //   3640: astore 43
    //   3642: aload 48
    //   3644: aload 55
    //   3646: invokevirtual 1457	java/nio/ByteBuffer:get	([B)Ljava/nio/ByteBuffer;
    //   3649: pop
    //   3650: aconst_null
    //   3651: astore 50
    //   3653: aconst_null
    //   3654: astore 51
    //   3656: aload 45
    //   3658: astore 39
    //   3660: aload 40
    //   3662: astore 41
    //   3664: aload 37
    //   3666: astore 42
    //   3668: aload 38
    //   3670: astore 43
    //   3672: aload 54
    //   3674: getfield 1433	android/media/MediaCodec$BufferInfo:size	I
    //   3677: iconst_1
    //   3678: isub
    //   3679: istore_3
    //   3680: aload 51
    //   3682: astore 49
    //   3684: aload 50
    //   3686: astore 48
    //   3688: iload_3
    //   3689: iflt +177 -> 3866
    //   3692: aload 51
    //   3694: astore 49
    //   3696: aload 50
    //   3698: astore 48
    //   3700: iload_3
    //   3701: iconst_3
    //   3702: if_icmple +164 -> 3866
    //   3705: aload 55
    //   3707: iload_3
    //   3708: baload
    //   3709: iconst_1
    //   3710: if_icmpne +1842 -> 5552
    //   3713: aload 55
    //   3715: iload_3
    //   3716: iconst_1
    //   3717: isub
    //   3718: baload
    //   3719: ifne +1833 -> 5552
    //   3722: aload 55
    //   3724: iload_3
    //   3725: iconst_2
    //   3726: isub
    //   3727: baload
    //   3728: ifne +1824 -> 5552
    //   3731: aload 55
    //   3733: iload_3
    //   3734: iconst_3
    //   3735: isub
    //   3736: baload
    //   3737: ifne +1815 -> 5552
    //   3740: aload 45
    //   3742: astore 39
    //   3744: aload 40
    //   3746: astore 41
    //   3748: aload 37
    //   3750: astore 42
    //   3752: aload 38
    //   3754: astore 43
    //   3756: iload_3
    //   3757: iconst_3
    //   3758: isub
    //   3759: invokestatic 1460	java/nio/ByteBuffer:allocate	(I)Ljava/nio/ByteBuffer;
    //   3762: astore 48
    //   3764: aload 45
    //   3766: astore 39
    //   3768: aload 40
    //   3770: astore 41
    //   3772: aload 37
    //   3774: astore 42
    //   3776: aload 38
    //   3778: astore 43
    //   3780: aload 54
    //   3782: getfield 1433	android/media/MediaCodec$BufferInfo:size	I
    //   3785: iload_3
    //   3786: iconst_3
    //   3787: isub
    //   3788: isub
    //   3789: invokestatic 1460	java/nio/ByteBuffer:allocate	(I)Ljava/nio/ByteBuffer;
    //   3792: astore 49
    //   3794: aload 45
    //   3796: astore 39
    //   3798: aload 40
    //   3800: astore 41
    //   3802: aload 37
    //   3804: astore 42
    //   3806: aload 38
    //   3808: astore 43
    //   3810: aload 48
    //   3812: aload 55
    //   3814: iconst_0
    //   3815: iload_3
    //   3816: iconst_3
    //   3817: isub
    //   3818: invokevirtual 1464	java/nio/ByteBuffer:put	([BII)Ljava/nio/ByteBuffer;
    //   3821: iconst_0
    //   3822: invokevirtual 1454	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   3825: pop
    //   3826: aload 45
    //   3828: astore 39
    //   3830: aload 40
    //   3832: astore 41
    //   3834: aload 37
    //   3836: astore 42
    //   3838: aload 38
    //   3840: astore 43
    //   3842: aload 49
    //   3844: aload 55
    //   3846: iload_3
    //   3847: iconst_3
    //   3848: isub
    //   3849: aload 54
    //   3851: getfield 1433	android/media/MediaCodec$BufferInfo:size	I
    //   3854: iload_3
    //   3855: iconst_3
    //   3856: isub
    //   3857: isub
    //   3858: invokevirtual 1464	java/nio/ByteBuffer:put	([BII)Ljava/nio/ByteBuffer;
    //   3861: iconst_0
    //   3862: invokevirtual 1454	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   3865: pop
    //   3866: aload 45
    //   3868: astore 39
    //   3870: aload 40
    //   3872: astore 41
    //   3874: aload 37
    //   3876: astore 42
    //   3878: aload 38
    //   3880: astore 43
    //   3882: ldc -103
    //   3884: iload 5
    //   3886: iload 4
    //   3888: invokestatic 1316	android/media/MediaFormat:createVideoFormat	(Ljava/lang/String;II)Landroid/media/MediaFormat;
    //   3891: astore 50
    //   3893: aload 48
    //   3895: ifnull +60 -> 3955
    //   3898: aload 49
    //   3900: ifnull +55 -> 3955
    //   3903: aload 45
    //   3905: astore 39
    //   3907: aload 40
    //   3909: astore 41
    //   3911: aload 37
    //   3913: astore 42
    //   3915: aload 38
    //   3917: astore 43
    //   3919: aload 50
    //   3921: ldc_w 1466
    //   3924: aload 48
    //   3926: invokevirtual 1470	android/media/MediaFormat:setByteBuffer	(Ljava/lang/String;Ljava/nio/ByteBuffer;)V
    //   3929: aload 45
    //   3931: astore 39
    //   3933: aload 40
    //   3935: astore 41
    //   3937: aload 37
    //   3939: astore 42
    //   3941: aload 38
    //   3943: astore 43
    //   3945: aload 50
    //   3947: ldc_w 1472
    //   3950: aload 49
    //   3952: invokevirtual 1470	android/media/MediaFormat:setByteBuffer	(Ljava/lang/String;Ljava/nio/ByteBuffer;)V
    //   3955: aload 45
    //   3957: astore 39
    //   3959: aload 40
    //   3961: astore 41
    //   3963: aload 37
    //   3965: astore 42
    //   3967: aload 38
    //   3969: astore 43
    //   3971: aload 35
    //   3973: aload 50
    //   3975: iconst_0
    //   3976: invokevirtual 1422	org/telegram/messenger/video/MP4Builder:addTrack	(Landroid/media/MediaFormat;Z)I
    //   3979: istore_3
    //   3980: goto -517 -> 3463
    //   3983: iload 14
    //   3985: istore 13
    //   3987: aload 48
    //   3989: astore 44
    //   3991: iload 15
    //   3993: istore 17
    //   3995: iload_3
    //   3996: istore 9
    //   3998: iload 19
    //   4000: bipush -3
    //   4002: if_icmpeq +1476 -> 5478
    //   4005: iload 19
    //   4007: bipush -2
    //   4009: if_icmpne +87 -> 4096
    //   4012: aload 45
    //   4014: astore 39
    //   4016: aload 40
    //   4018: astore 41
    //   4020: aload 37
    //   4022: astore 42
    //   4024: aload 38
    //   4026: astore 43
    //   4028: aload 45
    //   4030: invokevirtual 1418	android/media/MediaCodec:getOutputFormat	()Landroid/media/MediaFormat;
    //   4033: astore 44
    //   4035: aload 45
    //   4037: astore 39
    //   4039: aload 40
    //   4041: astore 41
    //   4043: aload 37
    //   4045: astore 42
    //   4047: aload 38
    //   4049: astore 43
    //   4051: ldc_w 547
    //   4054: new 1257	java/lang/StringBuilder
    //   4057: dup
    //   4058: invokespecial 1258	java/lang/StringBuilder:<init>	()V
    //   4061: ldc_w 1474
    //   4064: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4067: aload 44
    //   4069: invokevirtual 1477	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   4072: invokevirtual 1268	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4075: invokestatic 584	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   4078: iload 14
    //   4080: istore 13
    //   4082: aload 48
    //   4084: astore 44
    //   4086: iload 15
    //   4088: istore 17
    //   4090: iload_3
    //   4091: istore 9
    //   4093: goto +1385 -> 5478
    //   4096: iload 19
    //   4098: ifge +48 -> 4146
    //   4101: aload 45
    //   4103: astore 39
    //   4105: aload 40
    //   4107: astore 41
    //   4109: aload 37
    //   4111: astore 42
    //   4113: aload 38
    //   4115: astore 43
    //   4117: new 985	java/lang/RuntimeException
    //   4120: dup
    //   4121: new 1257	java/lang/StringBuilder
    //   4124: dup
    //   4125: invokespecial 1258	java/lang/StringBuilder:<init>	()V
    //   4128: ldc_w 1479
    //   4131: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4134: iload 19
    //   4136: invokevirtual 1300	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   4139: invokevirtual 1268	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4142: invokespecial 988	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   4145: athrow
    //   4146: aload 45
    //   4148: astore 39
    //   4150: aload 40
    //   4152: astore 41
    //   4154: aload 37
    //   4156: astore 42
    //   4158: aload 38
    //   4160: astore 43
    //   4162: getstatic 689	android/os/Build$VERSION:SDK_INT	I
    //   4165: bipush 18
    //   4167: if_icmplt +549 -> 4716
    //   4170: aload 45
    //   4172: astore 39
    //   4174: aload 40
    //   4176: astore 41
    //   4178: aload 37
    //   4180: astore 42
    //   4182: aload 38
    //   4184: astore 43
    //   4186: aload 54
    //   4188: getfield 1433	android/media/MediaCodec$BufferInfo:size	I
    //   4191: ifeq +1374 -> 5565
    //   4194: iconst_1
    //   4195: istore 32
    //   4197: iload 12
    //   4199: istore 18
    //   4201: iload 32
    //   4203: istore 33
    //   4205: iload 8
    //   4207: istore 16
    //   4209: lload 28
    //   4211: lconst_0
    //   4212: lcmp
    //   4213: ifle +79 -> 4292
    //   4216: aload 45
    //   4218: astore 39
    //   4220: aload 40
    //   4222: astore 41
    //   4224: aload 37
    //   4226: astore 42
    //   4228: aload 38
    //   4230: astore 43
    //   4232: iload 12
    //   4234: istore 18
    //   4236: iload 32
    //   4238: istore 33
    //   4240: iload 8
    //   4242: istore 16
    //   4244: aload 54
    //   4246: getfield 1482	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   4249: lload 28
    //   4251: lcmp
    //   4252: iflt +40 -> 4292
    //   4255: iconst_1
    //   4256: istore 16
    //   4258: iconst_1
    //   4259: istore 18
    //   4261: iconst_0
    //   4262: istore 33
    //   4264: aload 45
    //   4266: astore 39
    //   4268: aload 40
    //   4270: astore 41
    //   4272: aload 37
    //   4274: astore 42
    //   4276: aload 38
    //   4278: astore 43
    //   4280: aload 54
    //   4282: aload 54
    //   4284: getfield 1436	android/media/MediaCodec$BufferInfo:flags	I
    //   4287: iconst_4
    //   4288: ior
    //   4289: putfield 1436	android/media/MediaCodec$BufferInfo:flags	I
    //   4292: iload 33
    //   4294: istore 32
    //   4296: lload 26
    //   4298: lstore 24
    //   4300: lload 22
    //   4302: lconst_0
    //   4303: lcmp
    //   4304: ifle +111 -> 4415
    //   4307: iload 33
    //   4309: istore 32
    //   4311: lload 26
    //   4313: lstore 24
    //   4315: lload 26
    //   4317: ldc2_w 1211
    //   4320: lcmp
    //   4321: ifne +94 -> 4415
    //   4324: aload 45
    //   4326: astore 39
    //   4328: aload 40
    //   4330: astore 41
    //   4332: aload 37
    //   4334: astore 42
    //   4336: aload 38
    //   4338: astore 43
    //   4340: aload 54
    //   4342: getfield 1482	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   4345: lload 22
    //   4347: lcmp
    //   4348: ifge +421 -> 4769
    //   4351: iconst_0
    //   4352: istore 32
    //   4354: aload 45
    //   4356: astore 39
    //   4358: aload 40
    //   4360: astore 41
    //   4362: aload 37
    //   4364: astore 42
    //   4366: aload 38
    //   4368: astore 43
    //   4370: ldc_w 547
    //   4373: new 1257	java/lang/StringBuilder
    //   4376: dup
    //   4377: invokespecial 1258	java/lang/StringBuilder:<init>	()V
    //   4380: ldc_w 1484
    //   4383: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4386: lload 22
    //   4388: invokevirtual 1267	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   4391: ldc_w 1486
    //   4394: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4397: aload 54
    //   4399: getfield 1482	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   4402: invokevirtual 1267	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   4405: invokevirtual 1268	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4408: invokestatic 584	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   4411: lload 26
    //   4413: lstore 24
    //   4415: aload 45
    //   4417: astore 39
    //   4419: aload 40
    //   4421: astore 41
    //   4423: aload 37
    //   4425: astore 42
    //   4427: aload 38
    //   4429: astore 43
    //   4431: aload 45
    //   4433: iload 19
    //   4435: iload 32
    //   4437: invokevirtual 1444	android/media/MediaCodec:releaseOutputBuffer	(IZ)V
    //   4440: iload 32
    //   4442: ifeq +114 -> 4556
    //   4445: iconst_0
    //   4446: istore 8
    //   4448: aload 38
    //   4450: invokevirtual 1489	org/telegram/messenger/video/OutputSurface:awaitNewImage	()V
    //   4453: iload 8
    //   4455: ifne +101 -> 4556
    //   4458: aload 45
    //   4460: astore 39
    //   4462: aload 40
    //   4464: astore 41
    //   4466: aload 37
    //   4468: astore 42
    //   4470: aload 38
    //   4472: astore 43
    //   4474: getstatic 689	android/os/Build$VERSION:SDK_INT	I
    //   4477: bipush 18
    //   4479: if_icmplt +352 -> 4831
    //   4482: aload 45
    //   4484: astore 39
    //   4486: aload 40
    //   4488: astore 41
    //   4490: aload 37
    //   4492: astore 42
    //   4494: aload 38
    //   4496: astore 43
    //   4498: aload 38
    //   4500: iconst_0
    //   4501: invokevirtual 1492	org/telegram/messenger/video/OutputSurface:drawImage	(Z)V
    //   4504: aload 45
    //   4506: astore 39
    //   4508: aload 40
    //   4510: astore 41
    //   4512: aload 37
    //   4514: astore 42
    //   4516: aload 38
    //   4518: astore 43
    //   4520: aload 37
    //   4522: aload 54
    //   4524: getfield 1482	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   4527: ldc2_w 1493
    //   4530: lmul
    //   4531: invokevirtual 1498	org/telegram/messenger/video/InputSurface:setPresentationTime	(J)V
    //   4534: aload 45
    //   4536: astore 39
    //   4538: aload 40
    //   4540: astore 41
    //   4542: aload 37
    //   4544: astore 42
    //   4546: aload 38
    //   4548: astore 43
    //   4550: aload 37
    //   4552: invokevirtual 1501	org/telegram/messenger/video/InputSurface:swapBuffers	()Z
    //   4555: pop
    //   4556: aload 45
    //   4558: astore 39
    //   4560: aload 40
    //   4562: astore 41
    //   4564: aload 37
    //   4566: astore 42
    //   4568: aload 38
    //   4570: astore 43
    //   4572: iload 18
    //   4574: istore 12
    //   4576: iload 14
    //   4578: istore 13
    //   4580: aload 48
    //   4582: astore 44
    //   4584: iload 16
    //   4586: istore 8
    //   4588: iload 15
    //   4590: istore 17
    //   4592: iload_3
    //   4593: istore 9
    //   4595: lload 24
    //   4597: lstore 26
    //   4599: aload 54
    //   4601: getfield 1436	android/media/MediaCodec$BufferInfo:flags	I
    //   4604: iconst_4
    //   4605: iand
    //   4606: ifeq +872 -> 5478
    //   4609: iconst_0
    //   4610: istore 19
    //   4612: aload 45
    //   4614: astore 39
    //   4616: aload 40
    //   4618: astore 41
    //   4620: aload 37
    //   4622: astore 42
    //   4624: aload 38
    //   4626: astore 43
    //   4628: ldc_w 547
    //   4631: ldc_w 1503
    //   4634: invokestatic 584	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   4637: aload 45
    //   4639: astore 39
    //   4641: aload 40
    //   4643: astore 41
    //   4645: aload 37
    //   4647: astore 42
    //   4649: aload 38
    //   4651: astore 43
    //   4653: getstatic 689	android/os/Build$VERSION:SDK_INT	I
    //   4656: bipush 18
    //   4658: if_icmplt +374 -> 5032
    //   4661: aload 45
    //   4663: astore 39
    //   4665: aload 40
    //   4667: astore 41
    //   4669: aload 37
    //   4671: astore 42
    //   4673: aload 38
    //   4675: astore 43
    //   4677: aload 40
    //   4679: invokevirtual 1506	android/media/MediaCodec:signalEndOfInputStream	()V
    //   4682: iload 18
    //   4684: istore 12
    //   4686: iload 19
    //   4688: istore 7
    //   4690: iload 14
    //   4692: istore 13
    //   4694: aload 48
    //   4696: astore 44
    //   4698: iload 16
    //   4700: istore 8
    //   4702: iload 15
    //   4704: istore 17
    //   4706: iload_3
    //   4707: istore 9
    //   4709: lload 24
    //   4711: lstore 26
    //   4713: goto +765 -> 5478
    //   4716: aload 45
    //   4718: astore 39
    //   4720: aload 40
    //   4722: astore 41
    //   4724: aload 37
    //   4726: astore 42
    //   4728: aload 38
    //   4730: astore 43
    //   4732: aload 54
    //   4734: getfield 1433	android/media/MediaCodec$BufferInfo:size	I
    //   4737: ifne +834 -> 5571
    //   4740: aload 45
    //   4742: astore 39
    //   4744: aload 40
    //   4746: astore 41
    //   4748: aload 37
    //   4750: astore 42
    //   4752: aload 38
    //   4754: astore 43
    //   4756: aload 54
    //   4758: getfield 1482	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   4761: lconst_0
    //   4762: lcmp
    //   4763: ifeq +814 -> 5577
    //   4766: goto +805 -> 5571
    //   4769: aload 45
    //   4771: astore 39
    //   4773: aload 40
    //   4775: astore 41
    //   4777: aload 37
    //   4779: astore 42
    //   4781: aload 38
    //   4783: astore 43
    //   4785: aload 54
    //   4787: getfield 1482	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   4790: lstore 24
    //   4792: iload 33
    //   4794: istore 32
    //   4796: goto -381 -> 4415
    //   4799: astore 44
    //   4801: iconst_1
    //   4802: istore 8
    //   4804: aload 45
    //   4806: astore 39
    //   4808: aload 40
    //   4810: astore 41
    //   4812: aload 37
    //   4814: astore 42
    //   4816: aload 38
    //   4818: astore 43
    //   4820: ldc_w 547
    //   4823: aload 44
    //   4825: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4828: goto -375 -> 4453
    //   4831: aload 45
    //   4833: astore 39
    //   4835: aload 40
    //   4837: astore 41
    //   4839: aload 37
    //   4841: astore 42
    //   4843: aload 38
    //   4845: astore 43
    //   4847: aload 40
    //   4849: ldc2_w 1375
    //   4852: invokevirtual 1380	android/media/MediaCodec:dequeueInputBuffer	(J)I
    //   4855: istore 8
    //   4857: iload 8
    //   4859: iflt +145 -> 5004
    //   4862: aload 45
    //   4864: astore 39
    //   4866: aload 40
    //   4868: astore 41
    //   4870: aload 37
    //   4872: astore 42
    //   4874: aload 38
    //   4876: astore 43
    //   4878: aload 38
    //   4880: iconst_1
    //   4881: invokevirtual 1492	org/telegram/messenger/video/OutputSurface:drawImage	(Z)V
    //   4884: aload 45
    //   4886: astore 39
    //   4888: aload 40
    //   4890: astore 41
    //   4892: aload 37
    //   4894: astore 42
    //   4896: aload 38
    //   4898: astore 43
    //   4900: aload 38
    //   4902: invokevirtual 1510	org/telegram/messenger/video/OutputSurface:getFrame	()Ljava/nio/ByteBuffer;
    //   4905: astore 44
    //   4907: aload 47
    //   4909: iload 8
    //   4911: aaload
    //   4912: astore 49
    //   4914: aload 45
    //   4916: astore 39
    //   4918: aload 40
    //   4920: astore 41
    //   4922: aload 37
    //   4924: astore 42
    //   4926: aload 38
    //   4928: astore 43
    //   4930: aload 49
    //   4932: invokevirtual 1513	java/nio/ByteBuffer:clear	()Ljava/nio/Buffer;
    //   4935: pop
    //   4936: aload 45
    //   4938: astore 39
    //   4940: aload 40
    //   4942: astore 41
    //   4944: aload 37
    //   4946: astore 42
    //   4948: aload 38
    //   4950: astore 43
    //   4952: aload 44
    //   4954: aload 49
    //   4956: iload 10
    //   4958: iload 5
    //   4960: iload 4
    //   4962: iload 6
    //   4964: iload 11
    //   4966: invokestatic 1517	org/telegram/messenger/Utilities:convertVideoFrame	(Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;IIIII)I
    //   4969: pop
    //   4970: aload 45
    //   4972: astore 39
    //   4974: aload 40
    //   4976: astore 41
    //   4978: aload 37
    //   4980: astore 42
    //   4982: aload 38
    //   4984: astore 43
    //   4986: aload 40
    //   4988: iload 8
    //   4990: iconst_0
    //   4991: iload_2
    //   4992: aload 54
    //   4994: getfield 1482	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   4997: iconst_0
    //   4998: invokevirtual 1387	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   5001: goto -445 -> 4556
    //   5004: aload 45
    //   5006: astore 39
    //   5008: aload 40
    //   5010: astore 41
    //   5012: aload 37
    //   5014: astore 42
    //   5016: aload 38
    //   5018: astore 43
    //   5020: ldc_w 547
    //   5023: ldc_w 1519
    //   5026: invokestatic 584	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   5029: goto -473 -> 4556
    //   5032: aload 45
    //   5034: astore 39
    //   5036: aload 40
    //   5038: astore 41
    //   5040: aload 37
    //   5042: astore 42
    //   5044: aload 38
    //   5046: astore 43
    //   5048: aload 40
    //   5050: ldc2_w 1375
    //   5053: invokevirtual 1380	android/media/MediaCodec:dequeueInputBuffer	(J)I
    //   5056: istore 21
    //   5058: iload 18
    //   5060: istore 12
    //   5062: iload 19
    //   5064: istore 7
    //   5066: iload 14
    //   5068: istore 13
    //   5070: aload 48
    //   5072: astore 44
    //   5074: iload 16
    //   5076: istore 8
    //   5078: iload 15
    //   5080: istore 17
    //   5082: iload_3
    //   5083: istore 9
    //   5085: lload 24
    //   5087: lstore 26
    //   5089: iload 21
    //   5091: iflt +387 -> 5478
    //   5094: aload 45
    //   5096: astore 39
    //   5098: aload 40
    //   5100: astore 41
    //   5102: aload 37
    //   5104: astore 42
    //   5106: aload 38
    //   5108: astore 43
    //   5110: aload 40
    //   5112: iload 21
    //   5114: iconst_0
    //   5115: iconst_1
    //   5116: aload 54
    //   5118: getfield 1482	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   5121: iconst_4
    //   5122: invokevirtual 1387	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   5125: iload 18
    //   5127: istore 12
    //   5129: iload 19
    //   5131: istore 7
    //   5133: iload 14
    //   5135: istore 13
    //   5137: aload 48
    //   5139: astore 44
    //   5141: iload 16
    //   5143: istore 8
    //   5145: iload 15
    //   5147: istore 17
    //   5149: iload_3
    //   5150: istore 9
    //   5152: lload 24
    //   5154: lstore 26
    //   5156: goto +322 -> 5478
    //   5159: lload 24
    //   5161: ldc2_w 1211
    //   5164: lcmp
    //   5165: ifeq +256 -> 5421
    //   5168: lload 24
    //   5170: lstore 22
    //   5172: aload 45
    //   5174: astore 39
    //   5176: iload 34
    //   5178: istore 32
    //   5180: goto -4421 -> 759
    //   5183: aload_0
    //   5184: aload_1
    //   5185: aload 36
    //   5187: aload 35
    //   5189: aload 54
    //   5191: lload 22
    //   5193: lload 28
    //   5195: aload 52
    //   5197: iconst_0
    //   5198: invokespecial 1251	org/telegram/messenger/MediaController:readAndWriteTrack	(Lorg/telegram/messenger/MessageObject;Landroid/media/MediaExtractor;Lorg/telegram/messenger/video/MP4Builder;Landroid/media/MediaCodec$BufferInfo;JJLjava/io/File;Z)J
    //   5201: lstore 24
    //   5203: lload 24
    //   5205: ldc2_w 1211
    //   5208: lcmp
    //   5209: ifeq +209 -> 5418
    //   5212: lload 24
    //   5214: lstore 22
    //   5216: goto -4396 -> 820
    //   5219: astore 35
    //   5221: ldc_w 547
    //   5224: aload 35
    //   5226: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   5229: goto -4364 -> 865
    //   5232: astore 37
    //   5234: aload 38
    //   5236: astore 35
    //   5238: aload 40
    //   5240: astore 36
    //   5242: iconst_1
    //   5243: istore 32
    //   5245: ldc_w 547
    //   5248: aload 37
    //   5250: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   5253: aload 36
    //   5255: ifnull +8 -> 5263
    //   5258: aload 36
    //   5260: invokevirtual 1252	android/media/MediaExtractor:release	()V
    //   5263: aload 35
    //   5265: ifnull +9 -> 5274
    //   5268: aload 35
    //   5270: iconst_0
    //   5271: invokevirtual 1255	org/telegram/messenger/video/MP4Builder:finishMovie	(Z)V
    //   5274: ldc_w 547
    //   5277: new 1257	java/lang/StringBuilder
    //   5280: dup
    //   5281: invokespecial 1258	java/lang/StringBuilder:<init>	()V
    //   5284: ldc_w 1260
    //   5287: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5290: invokestatic 1170	java/lang/System:currentTimeMillis	()J
    //   5293: lload 30
    //   5295: lsub
    //   5296: invokevirtual 1267	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   5299: invokevirtual 1268	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5302: invokestatic 584	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   5305: goto -4409 -> 896
    //   5308: astore 35
    //   5310: ldc_w 547
    //   5313: aload 35
    //   5315: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   5318: goto -44 -> 5274
    //   5321: astore 35
    //   5323: ldc_w 547
    //   5326: aload 35
    //   5328: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   5331: goto -2490 -> 2841
    //   5334: aload 53
    //   5336: invokeinterface 1149 1 0
    //   5341: ldc_w 1145
    //   5344: iconst_1
    //   5345: invokeinterface 1155 3 0
    //   5350: invokeinterface 1158 1 0
    //   5355: pop
    //   5356: aload_0
    //   5357: aload_1
    //   5358: aload 52
    //   5360: iconst_1
    //   5361: iconst_1
    //   5362: invokespecial 1165	org/telegram/messenger/MediaController:didWriteData	(Lorg/telegram/messenger/MessageObject;Ljava/io/File;ZZ)V
    //   5365: iconst_0
    //   5366: ireturn
    //   5367: astore_1
    //   5368: aload 39
    //   5370: astore 36
    //   5372: aload 37
    //   5374: astore 35
    //   5376: goto -2556 -> 2820
    //   5379: astore_1
    //   5380: goto -2560 -> 2820
    //   5383: astore_1
    //   5384: goto -2564 -> 2820
    //   5387: astore 37
    //   5389: goto -147 -> 5242
    //   5392: astore 37
    //   5394: goto -152 -> 5242
    //   5397: astore 41
    //   5399: aload 37
    //   5401: astore 42
    //   5403: aload 45
    //   5405: astore 39
    //   5407: aload 41
    //   5409: astore 37
    //   5411: aload 38
    //   5413: astore 43
    //   5415: goto -4675 -> 740
    //   5418: goto -4598 -> 820
    //   5421: aload 45
    //   5423: astore 39
    //   5425: iload 34
    //   5427: istore 32
    //   5429: goto -4670 -> 759
    //   5432: iconst_1
    //   5433: istore 6
    //   5435: iload 11
    //   5437: istore_2
    //   5438: goto -4360 -> 1078
    //   5441: iload 16
    //   5443: ifne +104 -> 5547
    //   5446: iconst_1
    //   5447: istore_3
    //   5448: iconst_1
    //   5449: istore 13
    //   5451: lload 24
    //   5453: lstore 26
    //   5455: iload 14
    //   5457: istore 9
    //   5459: iload 15
    //   5461: istore 17
    //   5463: iload 7
    //   5465: istore 8
    //   5467: aload 48
    //   5469: astore 44
    //   5471: iload_3
    //   5472: istore 7
    //   5474: iload 16
    //   5476: istore 12
    //   5478: iload 7
    //   5480: ifne -3182 -> 2298
    //   5483: iload 12
    //   5485: istore 16
    //   5487: aload 44
    //   5489: astore 48
    //   5491: iload 8
    //   5493: istore_3
    //   5494: iload 17
    //   5496: istore 15
    //   5498: iload 9
    //   5500: istore 14
    //   5502: lload 26
    //   5504: lstore 24
    //   5506: iload 13
    //   5508: ifeq -3483 -> 2025
    //   5511: goto -3213 -> 2298
    //   5514: ldc_w 1520
    //   5517: istore 10
    //   5519: iload_2
    //   5520: istore 7
    //   5522: goto -4368 -> 1154
    //   5525: iload 7
    //   5527: istore 9
    //   5529: iload_3
    //   5530: istore 8
    //   5532: iload 12
    //   5534: iconst_m1
    //   5535: if_icmpne -3311 -> 2224
    //   5538: iconst_1
    //   5539: istore 9
    //   5541: iload_3
    //   5542: istore 8
    //   5544: goto -3320 -> 2224
    //   5547: iconst_0
    //   5548: istore_3
    //   5549: goto -101 -> 5448
    //   5552: iload_3
    //   5553: iconst_1
    //   5554: isub
    //   5555: istore_3
    //   5556: goto -1876 -> 3680
    //   5559: iconst_0
    //   5560: istore 9
    //   5562: goto -2070 -> 3492
    //   5565: iconst_0
    //   5566: istore 32
    //   5568: goto -1371 -> 4197
    //   5571: iconst_1
    //   5572: istore 32
    //   5574: goto -1377 -> 4197
    //   5577: iconst_0
    //   5578: istore 32
    //   5580: goto -6 -> 5574
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	5583	0	this	MediaController
    //   0	5583	1	paramMessageObject	MessageObject
    //   139	5381	2	i	int
    //   143	5413	3	j	int
    //   131	4830	4	k	int
    //   135	4824	5	m	int
    //   43	5391	6	n	int
    //   34	5492	7	i1	int
    //   79	5464	8	i2	int
    //   52	5509	9	i3	int
    //   82	5436	10	i4	int
    //   61	5375	11	i5	int
    //   70	5466	12	i6	int
    //   1198	4309	13	i7	int
    //   599	4902	14	i8	int
    //   583	4914	15	i9	int
    //   589	4897	16	i10	int
    //   2358	3137	17	i11	int
    //   4199	927	18	i12	int
    //   2433	2697	19	i13	int
    //   549	1539	20	i14	int
    //   5056	57	21	i15	int
    //   16	5199	22	l1	long
    //   580	4925	24	l2	long
    //   4296	116	26	localObject1	Object
    //   4597	906	26	l3	long
    //   25	5169	28	l4	long
    //   371	4923	30	l5	long
    //   167	5412	32	bool1	boolean
    //   4203	590	33	bool2	boolean
    //   366	5060	34	bool3	boolean
    //   7	5181	35	localObject2	Object
    //   5219	6	35	localException1	Exception
    //   5236	33	35	localObject3	Object
    //   5308	6	35	localException2	Exception
    //   5321	6	35	localException3	Exception
    //   5374	1	35	localObject4	Object
    //   384	4987	36	localObject5	Object
    //   401	166	37	localObject6	Object
    //   734	10	37	localException4	Exception
    //   757	4346	37	localObject7	Object
    //   5232	141	37	localException5	Exception
    //   5387	1	37	localException6	Exception
    //   5392	8	37	localException7	Exception
    //   5409	1	37	localException8	Exception
    //   397	5015	38	localObject8	Object
    //   393	5031	39	localObject9	Object
    //   390	4849	40	localObject10	Object
    //   200	4901	41	localObject11	Object
    //   5397	11	41	localException9	Exception
    //   427	4975	42	localObject12	Object
    //   620	4794	43	localObject13	Object
    //   575	4122	44	localObject14	Object
    //   4799	25	44	localException10	Exception
    //   4905	583	44	localObject15	Object
    //   557	4865	45	localMediaCodec	android.media.MediaCodec
    //   560	2234	46	localObject16	Object
    //   569	4339	47	localObject17	Object
    //   628	4862	48	localObject18	Object
    //   675	4280	49	localObject19	Object
    //   951	3023	50	localObject20	Object
    //   3654	39	51	localObject21	Object
    //   98	5261	52	localFile	File
    //   154	5181	53	localSharedPreferences	SharedPreferences
    //   410	4780	54	localBufferInfo	MediaCodec.BufferInfo
    //   3564	281	55	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   622	630	734	java/lang/Exception
    //   646	654	734	java/lang/Exception
    //   670	677	734	java/lang/Exception
    //   693	702	734	java/lang/Exception
    //   723	734	734	java/lang/Exception
    //   946	953	734	java/lang/Exception
    //   969	980	734	java/lang/Exception
    //   1006	1014	734	java/lang/Exception
    //   1030	1041	734	java/lang/Exception
    //   1064	1075	734	java/lang/Exception
    //   1094	1147	734	java/lang/Exception
    //   1170	1197	734	java/lang/Exception
    //   1216	1227	734	java/lang/Exception
    //   1282	1292	734	java/lang/Exception
    //   1308	1315	734	java/lang/Exception
    //   1338	1346	734	java/lang/Exception
    //   1362	1371	734	java/lang/Exception
    //   1387	1398	734	java/lang/Exception
    //   1414	1424	734	java/lang/Exception
    //   1449	1459	734	java/lang/Exception
    //   1475	1485	734	java/lang/Exception
    //   1501	1511	734	java/lang/Exception
    //   1527	1535	734	java/lang/Exception
    //   1551	1564	734	java/lang/Exception
    //   1580	1590	734	java/lang/Exception
    //   1606	1613	734	java/lang/Exception
    //   1629	1639	734	java/lang/Exception
    //   1655	1663	734	java/lang/Exception
    //   1679	1693	734	java/lang/Exception
    //   1714	1719	734	java/lang/Exception
    //   1735	1748	734	java/lang/Exception
    //   1764	1772	734	java/lang/Exception
    //   1788	1797	734	java/lang/Exception
    //   1813	1827	734	java/lang/Exception
    //   1843	1848	734	java/lang/Exception
    //   1877	1885	734	java/lang/Exception
    //   1901	1908	734	java/lang/Exception
    //   1924	1931	734	java/lang/Exception
    //   1959	1967	734	java/lang/Exception
    //   1983	1990	734	java/lang/Exception
    //   2014	2018	734	java/lang/Exception
    //   2046	2050	734	java/lang/Exception
    //   2076	2083	734	java/lang/Exception
    //   2106	2116	734	java/lang/Exception
    //   2144	2152	734	java/lang/Exception
    //   2175	2185	734	java/lang/Exception
    //   2206	2217	734	java/lang/Exception
    //   2249	2258	734	java/lang/Exception
    //   2282	2292	734	java/lang/Exception
    //   2314	2318	734	java/lang/Exception
    //   2334	2346	734	java/lang/Exception
    //   2423	2435	734	java/lang/Exception
    //   2478	2489	734	java/lang/Exception
    //   2514	2525	734	java/lang/Exception
    //   2550	2561	734	java/lang/Exception
    //   2592	2603	734	java/lang/Exception
    //   2641	2655	734	java/lang/Exception
    //   2734	2745	734	java/lang/Exception
    //   2780	2790	734	java/lang/Exception
    //   2809	2816	734	java/lang/Exception
    //   2898	2912	734	java/lang/Exception
    //   2931	2940	734	java/lang/Exception
    //   2959	2975	734	java/lang/Exception
    //   2991	2997	734	java/lang/Exception
    //   3045	3053	734	java/lang/Exception
    //   3069	3076	734	java/lang/Exception
    //   3113	3120	734	java/lang/Exception
    //   3158	3167	734	java/lang/Exception
    //   3203	3232	734	java/lang/Exception
    //   3248	3256	734	java/lang/Exception
    //   3284	3319	734	java/lang/Exception
    //   3335	3344	734	java/lang/Exception
    //   3366	3375	734	java/lang/Exception
    //   3391	3401	734	java/lang/Exception
    //   3420	3435	734	java/lang/Exception
    //   3451	3460	734	java/lang/Exception
    //   3479	3489	734	java/lang/Exception
    //   3508	3516	734	java/lang/Exception
    //   3557	3566	734	java/lang/Exception
    //   3582	3599	734	java/lang/Exception
    //   3615	3626	734	java/lang/Exception
    //   3642	3650	734	java/lang/Exception
    //   3672	3680	734	java/lang/Exception
    //   3756	3764	734	java/lang/Exception
    //   3780	3794	734	java/lang/Exception
    //   3810	3826	734	java/lang/Exception
    //   3842	3866	734	java/lang/Exception
    //   3882	3893	734	java/lang/Exception
    //   3919	3929	734	java/lang/Exception
    //   3945	3955	734	java/lang/Exception
    //   3971	3980	734	java/lang/Exception
    //   4028	4035	734	java/lang/Exception
    //   4051	4078	734	java/lang/Exception
    //   4117	4146	734	java/lang/Exception
    //   4162	4170	734	java/lang/Exception
    //   4186	4194	734	java/lang/Exception
    //   4244	4255	734	java/lang/Exception
    //   4280	4292	734	java/lang/Exception
    //   4340	4351	734	java/lang/Exception
    //   4370	4411	734	java/lang/Exception
    //   4431	4440	734	java/lang/Exception
    //   4474	4482	734	java/lang/Exception
    //   4498	4504	734	java/lang/Exception
    //   4520	4534	734	java/lang/Exception
    //   4550	4556	734	java/lang/Exception
    //   4599	4609	734	java/lang/Exception
    //   4628	4637	734	java/lang/Exception
    //   4653	4661	734	java/lang/Exception
    //   4677	4682	734	java/lang/Exception
    //   4732	4740	734	java/lang/Exception
    //   4756	4766	734	java/lang/Exception
    //   4785	4792	734	java/lang/Exception
    //   4820	4828	734	java/lang/Exception
    //   4847	4857	734	java/lang/Exception
    //   4878	4884	734	java/lang/Exception
    //   4900	4907	734	java/lang/Exception
    //   4930	4936	734	java/lang/Exception
    //   4952	4970	734	java/lang/Exception
    //   4986	5001	734	java/lang/Exception
    //   5020	5029	734	java/lang/Exception
    //   5048	5058	734	java/lang/Exception
    //   5110	5125	734	java/lang/Exception
    //   514	528	2819	finally
    //   542	551	2819	finally
    //   622	630	2819	finally
    //   646	654	2819	finally
    //   670	677	2819	finally
    //   693	702	2819	finally
    //   723	734	2819	finally
    //   740	748	2819	finally
    //   946	953	2819	finally
    //   969	980	2819	finally
    //   1006	1014	2819	finally
    //   1030	1041	2819	finally
    //   1064	1075	2819	finally
    //   1094	1147	2819	finally
    //   1170	1197	2819	finally
    //   1216	1227	2819	finally
    //   1282	1292	2819	finally
    //   1308	1315	2819	finally
    //   1338	1346	2819	finally
    //   1362	1371	2819	finally
    //   1387	1398	2819	finally
    //   1414	1424	2819	finally
    //   1449	1459	2819	finally
    //   1475	1485	2819	finally
    //   1501	1511	2819	finally
    //   1527	1535	2819	finally
    //   1551	1564	2819	finally
    //   1580	1590	2819	finally
    //   1606	1613	2819	finally
    //   1629	1639	2819	finally
    //   1655	1663	2819	finally
    //   1679	1693	2819	finally
    //   1693	1698	2819	finally
    //   1714	1719	2819	finally
    //   1735	1748	2819	finally
    //   1764	1772	2819	finally
    //   1788	1797	2819	finally
    //   1813	1827	2819	finally
    //   1843	1848	2819	finally
    //   1877	1885	2819	finally
    //   1901	1908	2819	finally
    //   1924	1931	2819	finally
    //   1959	1967	2819	finally
    //   1983	1990	2819	finally
    //   2014	2018	2819	finally
    //   2046	2050	2819	finally
    //   2076	2083	2819	finally
    //   2106	2116	2819	finally
    //   2144	2152	2819	finally
    //   2175	2185	2819	finally
    //   2206	2217	2819	finally
    //   2249	2258	2819	finally
    //   2282	2292	2819	finally
    //   2314	2318	2819	finally
    //   2334	2346	2819	finally
    //   2423	2435	2819	finally
    //   2478	2489	2819	finally
    //   2514	2525	2819	finally
    //   2550	2561	2819	finally
    //   2592	2603	2819	finally
    //   2641	2655	2819	finally
    //   2734	2745	2819	finally
    //   2780	2790	2819	finally
    //   2809	2816	2819	finally
    //   2898	2912	2819	finally
    //   2931	2940	2819	finally
    //   2959	2975	2819	finally
    //   2991	2997	2819	finally
    //   3045	3053	2819	finally
    //   3069	3076	2819	finally
    //   3113	3120	2819	finally
    //   3158	3167	2819	finally
    //   3203	3232	2819	finally
    //   3248	3256	2819	finally
    //   3284	3319	2819	finally
    //   3335	3344	2819	finally
    //   3366	3375	2819	finally
    //   3391	3401	2819	finally
    //   3420	3435	2819	finally
    //   3451	3460	2819	finally
    //   3479	3489	2819	finally
    //   3508	3516	2819	finally
    //   3557	3566	2819	finally
    //   3582	3599	2819	finally
    //   3615	3626	2819	finally
    //   3642	3650	2819	finally
    //   3672	3680	2819	finally
    //   3756	3764	2819	finally
    //   3780	3794	2819	finally
    //   3810	3826	2819	finally
    //   3842	3866	2819	finally
    //   3882	3893	2819	finally
    //   3919	3929	2819	finally
    //   3945	3955	2819	finally
    //   3971	3980	2819	finally
    //   4028	4035	2819	finally
    //   4051	4078	2819	finally
    //   4117	4146	2819	finally
    //   4162	4170	2819	finally
    //   4186	4194	2819	finally
    //   4244	4255	2819	finally
    //   4280	4292	2819	finally
    //   4340	4351	2819	finally
    //   4370	4411	2819	finally
    //   4431	4440	2819	finally
    //   4448	4453	2819	finally
    //   4474	4482	2819	finally
    //   4498	4504	2819	finally
    //   4520	4534	2819	finally
    //   4550	4556	2819	finally
    //   4599	4609	2819	finally
    //   4628	4637	2819	finally
    //   4653	4661	2819	finally
    //   4677	4682	2819	finally
    //   4732	4740	2819	finally
    //   4756	4766	2819	finally
    //   4785	4792	2819	finally
    //   4820	4828	2819	finally
    //   4847	4857	2819	finally
    //   4878	4884	2819	finally
    //   4900	4907	2819	finally
    //   4930	4936	2819	finally
    //   4952	4970	2819	finally
    //   4986	5001	2819	finally
    //   5020	5029	2819	finally
    //   5048	5058	2819	finally
    //   5110	5125	2819	finally
    //   5183	5203	2819	finally
    //   4448	4453	4799	java/lang/Exception
    //   859	865	5219	java/lang/Exception
    //   403	412	5232	java/lang/Exception
    //   420	429	5232	java/lang/Exception
    //   437	444	5232	java/lang/Exception
    //   452	458	5232	java/lang/Exception
    //   466	475	5232	java/lang/Exception
    //   483	497	5232	java/lang/Exception
    //   505	514	5232	java/lang/Exception
    //   5268	5274	5308	java/lang/Exception
    //   2835	2841	5321	java/lang/Exception
    //   403	412	5367	finally
    //   420	429	5367	finally
    //   437	444	5367	finally
    //   452	458	5367	finally
    //   466	475	5367	finally
    //   483	497	5367	finally
    //   505	514	5367	finally
    //   759	766	5379	finally
    //   771	776	5379	finally
    //   781	786	5379	finally
    //   791	801	5379	finally
    //   806	816	5379	finally
    //   816	820	5379	finally
    //   825	844	5379	finally
    //   5245	5253	5383	finally
    //   514	528	5387	java/lang/Exception
    //   542	551	5387	java/lang/Exception
    //   740	748	5387	java/lang/Exception
    //   5183	5203	5387	java/lang/Exception
    //   759	766	5392	java/lang/Exception
    //   771	776	5392	java/lang/Exception
    //   781	786	5392	java/lang/Exception
    //   791	801	5392	java/lang/Exception
    //   806	816	5392	java/lang/Exception
    //   816	820	5392	java/lang/Exception
    //   825	844	5392	java/lang/Exception
    //   1693	1698	5397	java/lang/Exception
  }
  
  /* Error */
  public static String copyFileToCache(Uri paramUri, String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 10
    //   3: aconst_null
    //   4: astore 9
    //   6: aconst_null
    //   7: astore 8
    //   9: aconst_null
    //   10: astore 7
    //   12: aload 9
    //   14: astore_3
    //   15: aload 8
    //   17: astore 4
    //   19: aload 10
    //   21: astore 5
    //   23: aload_0
    //   24: invokestatic 1527	org/telegram/messenger/MediaController:getFileName	(Landroid/net/Uri;)Ljava/lang/String;
    //   27: astore 11
    //   29: aload 11
    //   31: astore 6
    //   33: aload 11
    //   35: ifnonnull +89 -> 124
    //   38: aload 9
    //   40: astore_3
    //   41: aload 8
    //   43: astore 4
    //   45: aload 10
    //   47: astore 5
    //   49: getstatic 1530	org/telegram/messenger/UserConfig:lastLocalId	I
    //   52: istore_2
    //   53: aload 9
    //   55: astore_3
    //   56: aload 8
    //   58: astore 4
    //   60: aload 10
    //   62: astore 5
    //   64: getstatic 1530	org/telegram/messenger/UserConfig:lastLocalId	I
    //   67: iconst_1
    //   68: isub
    //   69: putstatic 1530	org/telegram/messenger/UserConfig:lastLocalId	I
    //   72: aload 9
    //   74: astore_3
    //   75: aload 8
    //   77: astore 4
    //   79: aload 10
    //   81: astore 5
    //   83: iconst_0
    //   84: invokestatic 1533	org/telegram/messenger/UserConfig:saveConfig	(Z)V
    //   87: aload 9
    //   89: astore_3
    //   90: aload 8
    //   92: astore 4
    //   94: aload 10
    //   96: astore 5
    //   98: getstatic 1539	java/util/Locale:US	Ljava/util/Locale;
    //   101: ldc_w 1541
    //   104: iconst_2
    //   105: anewarray 4	java/lang/Object
    //   108: dup
    //   109: iconst_0
    //   110: iload_2
    //   111: invokestatic 1547	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   114: aastore
    //   115: dup
    //   116: iconst_1
    //   117: aload_1
    //   118: aastore
    //   119: invokestatic 1551	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   122: astore 6
    //   124: aload 9
    //   126: astore_3
    //   127: aload 8
    //   129: astore 4
    //   131: aload 10
    //   133: astore 5
    //   135: getstatic 559	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   138: invokevirtual 701	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   141: aload_0
    //   142: invokevirtual 1555	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   145: astore_0
    //   146: aload_0
    //   147: astore_3
    //   148: aload 8
    //   150: astore 4
    //   152: aload_0
    //   153: astore 5
    //   155: new 1040	java/io/File
    //   158: dup
    //   159: invokestatic 1052	org/telegram/messenger/FileLoader:getInstance	()Lorg/telegram/messenger/FileLoader;
    //   162: iconst_4
    //   163: invokevirtual 1559	org/telegram/messenger/FileLoader:getDirectory	(I)Ljava/io/File;
    //   166: aload 6
    //   168: invokespecial 1562	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   171: astore 6
    //   173: aload_0
    //   174: astore_3
    //   175: aload 8
    //   177: astore 4
    //   179: aload_0
    //   180: astore 5
    //   182: new 1564	java/io/FileOutputStream
    //   185: dup
    //   186: aload 6
    //   188: invokespecial 1566	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   191: astore_1
    //   192: sipush 20480
    //   195: newarray <illegal type>
    //   197: astore_3
    //   198: aload_0
    //   199: aload_3
    //   200: invokevirtual 1572	java/io/InputStream:read	([B)I
    //   203: istore_2
    //   204: iload_2
    //   205: iconst_m1
    //   206: if_icmpeq +46 -> 252
    //   209: aload_1
    //   210: aload_3
    //   211: iconst_0
    //   212: iload_2
    //   213: invokevirtual 1576	java/io/FileOutputStream:write	([BII)V
    //   216: goto -18 -> 198
    //   219: astore 6
    //   221: aload_0
    //   222: astore_3
    //   223: aload_1
    //   224: astore 4
    //   226: ldc_w 547
    //   229: aload 6
    //   231: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   234: aload_0
    //   235: ifnull +7 -> 242
    //   238: aload_0
    //   239: invokevirtual 1579	java/io/InputStream:close	()V
    //   242: aload_1
    //   243: ifnull +7 -> 250
    //   246: aload_1
    //   247: invokevirtual 1580	java/io/FileOutputStream:close	()V
    //   250: aconst_null
    //   251: areturn
    //   252: aload 6
    //   254: invokevirtual 1583	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   257: astore_3
    //   258: aload_0
    //   259: ifnull +7 -> 266
    //   262: aload_0
    //   263: invokevirtual 1579	java/io/InputStream:close	()V
    //   266: aload_1
    //   267: ifnull +7 -> 274
    //   270: aload_1
    //   271: invokevirtual 1580	java/io/FileOutputStream:close	()V
    //   274: aload_3
    //   275: areturn
    //   276: astore_0
    //   277: ldc_w 547
    //   280: aload_0
    //   281: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   284: goto -18 -> 266
    //   287: astore_0
    //   288: ldc_w 547
    //   291: aload_0
    //   292: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   295: goto -21 -> 274
    //   298: astore_0
    //   299: ldc_w 547
    //   302: aload_0
    //   303: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   306: goto -64 -> 242
    //   309: astore_0
    //   310: ldc_w 547
    //   313: aload_0
    //   314: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   317: goto -67 -> 250
    //   320: astore_0
    //   321: aload_3
    //   322: ifnull +7 -> 329
    //   325: aload_3
    //   326: invokevirtual 1579	java/io/InputStream:close	()V
    //   329: aload 4
    //   331: ifnull +8 -> 339
    //   334: aload 4
    //   336: invokevirtual 1580	java/io/FileOutputStream:close	()V
    //   339: aload_0
    //   340: athrow
    //   341: astore_1
    //   342: ldc_w 547
    //   345: aload_1
    //   346: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   349: goto -20 -> 329
    //   352: astore_1
    //   353: ldc_w 547
    //   356: aload_1
    //   357: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   360: goto -21 -> 339
    //   363: astore 5
    //   365: aload_0
    //   366: astore_3
    //   367: aload_1
    //   368: astore 4
    //   370: aload 5
    //   372: astore_0
    //   373: goto -52 -> 321
    //   376: astore 6
    //   378: aload 5
    //   380: astore_0
    //   381: aload 7
    //   383: astore_1
    //   384: goto -163 -> 221
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	387	0	paramUri	Uri
    //   0	387	1	paramString	String
    //   52	161	2	i	int
    //   14	353	3	localObject1	Object
    //   17	352	4	localObject2	Object
    //   21	160	5	localObject3	Object
    //   363	16	5	localObject4	Object
    //   31	156	6	localObject5	Object
    //   219	34	6	localException1	Exception
    //   376	1	6	localException2	Exception
    //   10	372	7	localObject6	Object
    //   7	169	8	localObject7	Object
    //   4	121	9	localObject8	Object
    //   1	131	10	localObject9	Object
    //   27	7	11	str	String
    // Exception table:
    //   from	to	target	type
    //   192	198	219	java/lang/Exception
    //   198	204	219	java/lang/Exception
    //   209	216	219	java/lang/Exception
    //   252	258	219	java/lang/Exception
    //   262	266	276	java/lang/Exception
    //   270	274	287	java/lang/Exception
    //   238	242	298	java/lang/Exception
    //   246	250	309	java/lang/Exception
    //   23	29	320	finally
    //   49	53	320	finally
    //   64	72	320	finally
    //   83	87	320	finally
    //   98	124	320	finally
    //   135	146	320	finally
    //   155	173	320	finally
    //   182	192	320	finally
    //   226	234	320	finally
    //   325	329	341	java/lang/Exception
    //   334	339	352	java/lang/Exception
    //   192	198	363	finally
    //   198	204	363	finally
    //   209	216	363	finally
    //   252	258	363	finally
    //   23	29	376	java/lang/Exception
    //   49	53	376	java/lang/Exception
    //   64	72	376	java/lang/Exception
    //   83	87	376	java/lang/Exception
    //   98	124	376	java/lang/Exception
    //   135	146	376	java/lang/Exception
    //   155	173	376	java/lang/Exception
    //   182	192	376	java/lang/Exception
  }
  
  private void didWriteData(final MessageObject paramMessageObject, final File paramFile, final boolean paramBoolean1, final boolean paramBoolean2)
  {
    final boolean bool = this.videoConvertFirstWrite;
    if (bool) {
      this.videoConvertFirstWrite = false;
    }
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (paramBoolean2) {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.FilePreparingFailed, new Object[] { paramMessageObject, paramFile.toString() });
        }
        for (;;)
        {
          if ((paramBoolean2) || (paramBoolean1)) {}
          synchronized (MediaController.this.videoConvertSync)
          {
            MediaController.access$6002(MediaController.this, false);
            MediaController.this.videoConvertQueue.remove(paramMessageObject);
            MediaController.this.startVideoConvertFromQueue();
            return;
            if (bool) {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.FilePreparingStarted, new Object[] { paramMessageObject, paramFile.toString() });
            }
            ??? = NotificationCenter.getInstance();
            int i = NotificationCenter.FileNewChunkAvailable;
            MessageObject localMessageObject = paramMessageObject;
            String str = paramFile.toString();
            if (paramBoolean1)
            {
              l = paramFile.length();
              ((NotificationCenter)???).postNotificationName(i, new Object[] { localMessageObject, str, Long.valueOf(l) });
              continue;
            }
            long l = 0L;
          }
        }
      }
    });
  }
  
  private int getCurrentDownloadMask()
  {
    if (ConnectionsManager.isConnectedToWiFi()) {
      return this.wifiDownloadMask;
    }
    if (ConnectionsManager.isRoaming()) {
      return this.roamingDownloadMask;
    }
    return this.mobileDataDownloadMask;
  }
  
  public static String getFileName(Uri paramUri)
  {
    localObject3 = null;
    String str = null;
    localObject2 = localObject3;
    if (paramUri.getScheme().equals("content"))
    {
      localObject2 = null;
      localObject1 = null;
    }
    try
    {
      Cursor localCursor = ApplicationLoader.applicationContext.getContentResolver().query(paramUri, new String[] { "_display_name" }, null, null, null);
      localObject1 = localCursor;
      localObject2 = localCursor;
      if (localCursor.moveToFirst())
      {
        localObject1 = localCursor;
        localObject2 = localCursor;
        str = localCursor.getString(localCursor.getColumnIndex("_display_name"));
      }
      localObject2 = str;
      if (localCursor != null)
      {
        localCursor.close();
        localObject2 = str;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        int i;
        localObject2 = localObject1;
        FileLog.e("tmessages", localException);
        localObject2 = localObject3;
        if (localObject1 != null)
        {
          ((Cursor)localObject1).close();
          localObject2 = localObject3;
        }
      }
    }
    finally
    {
      if (localObject2 == null) {
        break label187;
      }
      ((Cursor)localObject2).close();
    }
    localObject1 = localObject2;
    if (localObject2 == null)
    {
      paramUri = paramUri.getPath();
      i = paramUri.lastIndexOf('/');
      localObject1 = paramUri;
      if (i != -1) {
        localObject1 = paramUri.substring(i + 1);
      }
    }
    return (String)localObject1;
  }
  
  public static MediaController getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          MediaController localMediaController2 = Instance;
          localObject1 = localMediaController2;
          if (localMediaController2 == null) {
            localObject1 = new MediaController();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (MediaController)localObject1;
          return (MediaController)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localMediaController1;
  }
  
  private native long getTotalPcmDuration();
  
  public static boolean isGif(Uri paramUri)
  {
    boolean bool1 = false;
    Object localObject1 = null;
    Uri localUri = null;
    do
    {
      try
      {
        paramUri = ApplicationLoader.applicationContext.getContentResolver().openInputStream(paramUri);
        localUri = paramUri;
        localObject1 = paramUri;
        Object localObject2 = new byte[3];
        localUri = paramUri;
        localObject1 = paramUri;
        if (paramUri.read((byte[])localObject2, 0, 3) != 3) {
          continue;
        }
        localUri = paramUri;
        localObject1 = paramUri;
        localObject2 = new String((byte[])localObject2);
        if (localObject2 == null) {
          continue;
        }
        localUri = paramUri;
        localObject1 = paramUri;
        bool2 = ((String)localObject2).equalsIgnoreCase("gif");
        if (!bool2) {
          continue;
        }
        bool2 = true;
        bool1 = bool2;
      }
      catch (Exception paramUri)
      {
        do
        {
          boolean bool2;
          localObject1 = localUri;
          FileLog.e("tmessages", paramUri);
        } while (localUri == null);
        try
        {
          localUri.close();
          return false;
        }
        catch (Exception paramUri)
        {
          FileLog.e("tmessages", paramUri);
          return false;
        }
      }
      finally
      {
        if (localObject1 == null) {
          break label172;
        }
      }
      try
      {
        paramUri.close();
        bool1 = bool2;
        return bool1;
      }
      catch (Exception paramUri)
      {
        FileLog.e("tmessages", paramUri);
        return true;
      }
    } while (paramUri == null);
    try
    {
      paramUri.close();
      return false;
    }
    catch (Exception paramUri)
    {
      FileLog.e("tmessages", paramUri);
      return false;
    }
    try
    {
      ((InputStream)localObject1).close();
      label172:
      throw paramUri;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  private boolean isNearToSensor(float paramFloat)
  {
    return (paramFloat < 5.0F) && (paramFloat != this.proximitySensor.getMaximumRange());
  }
  
  private native int isOpusFile(String paramString);
  
  private static boolean isRecognizedFormat(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
  }
  
  public static boolean isWebp(Uri paramUri)
  {
    boolean bool1 = false;
    Object localObject1 = null;
    Uri localUri = null;
    do
    {
      try
      {
        paramUri = ApplicationLoader.applicationContext.getContentResolver().openInputStream(paramUri);
        localUri = paramUri;
        localObject1 = paramUri;
        Object localObject2 = new byte[12];
        localUri = paramUri;
        localObject1 = paramUri;
        if (paramUri.read((byte[])localObject2, 0, 12) != 12) {
          continue;
        }
        localUri = paramUri;
        localObject1 = paramUri;
        localObject2 = new String((byte[])localObject2);
        if (localObject2 == null) {
          continue;
        }
        localUri = paramUri;
        localObject1 = paramUri;
        localObject2 = ((String)localObject2).toLowerCase();
        localUri = paramUri;
        localObject1 = paramUri;
        if (!((String)localObject2).startsWith("riff")) {
          continue;
        }
        localUri = paramUri;
        localObject1 = paramUri;
        bool2 = ((String)localObject2).endsWith("webp");
        if (!bool2) {
          continue;
        }
        bool2 = true;
        bool1 = bool2;
      }
      catch (Exception paramUri)
      {
        do
        {
          boolean bool2;
          localObject1 = localUri;
          FileLog.e("tmessages", paramUri);
        } while (localUri == null);
        try
        {
          localUri.close();
          return false;
        }
        catch (Exception paramUri)
        {
          FileLog.e("tmessages", paramUri);
          return false;
        }
      }
      finally
      {
        if (localObject1 == null) {
          break label203;
        }
      }
      try
      {
        paramUri.close();
        bool1 = bool2;
        return bool1;
      }
      catch (Exception paramUri)
      {
        FileLog.e("tmessages", paramUri);
        return true;
      }
    } while (paramUri == null);
    try
    {
      paramUri.close();
      return false;
    }
    catch (Exception paramUri)
    {
      FileLog.e("tmessages", paramUri);
      return false;
    }
    try
    {
      ((InputStream)localObject1).close();
      label203:
      throw paramUri;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public static void loadGalleryPhotosAlbums(int paramInt)
  {
    Thread localThread = new Thread(new Runnable()
    {
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: new 29	java/util/ArrayList
        //   3: dup
        //   4: invokespecial 30	java/util/ArrayList:<init>	()V
        //   7: astore 30
        //   9: new 29	java/util/ArrayList
        //   12: dup
        //   13: invokespecial 30	java/util/ArrayList:<init>	()V
        //   16: astore 31
        //   18: new 32	java/util/HashMap
        //   21: dup
        //   22: invokespecial 33	java/util/HashMap:<init>	()V
        //   25: astore 32
        //   27: aconst_null
        //   28: astore 25
        //   30: aconst_null
        //   31: astore 24
        //   33: new 35	java/lang/StringBuilder
        //   36: dup
        //   37: invokespecial 36	java/lang/StringBuilder:<init>	()V
        //   40: getstatic 42	android/os/Environment:DIRECTORY_DCIM	Ljava/lang/String;
        //   43: invokestatic 46	android/os/Environment:getExternalStoragePublicDirectory	(Ljava/lang/String;)Ljava/io/File;
        //   46: invokevirtual 52	java/io/File:getAbsolutePath	()Ljava/lang/String;
        //   49: invokevirtual 56	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   52: ldc 58
        //   54: invokevirtual 56	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   57: ldc 60
        //   59: invokevirtual 56	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   62: invokevirtual 63	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   65: astore 33
        //   67: aconst_null
        //   68: astore 26
        //   70: aconst_null
        //   71: astore 27
        //   73: aconst_null
        //   74: astore 20
        //   76: aconst_null
        //   77: astore 22
        //   79: aconst_null
        //   80: astore 23
        //   82: aconst_null
        //   83: astore 21
        //   85: aconst_null
        //   86: astore 28
        //   88: aconst_null
        //   89: astore 29
        //   91: aconst_null
        //   92: astore 12
        //   94: aload 24
        //   96: astore 15
        //   98: aload 26
        //   100: astore 16
        //   102: aload 12
        //   104: astore 18
        //   106: aload 29
        //   108: astore 14
        //   110: getstatic 68	android/os/Build$VERSION:SDK_INT	I
        //   113: bipush 23
        //   115: if_icmplt +78 -> 193
        //   118: aload 24
        //   120: astore 15
        //   122: aload 26
        //   124: astore 16
        //   126: aload 12
        //   128: astore 18
        //   130: aload 25
        //   132: astore 19
        //   134: aload 27
        //   136: astore 13
        //   138: aload 28
        //   140: astore 17
        //   142: aload 29
        //   144: astore 14
        //   146: getstatic 68	android/os/Build$VERSION:SDK_INT	I
        //   149: bipush 23
        //   151: if_icmplt +1566 -> 1717
        //   154: aload 24
        //   156: astore 15
        //   158: aload 26
        //   160: astore 16
        //   162: aload 12
        //   164: astore 18
        //   166: aload 25
        //   168: astore 19
        //   170: aload 27
        //   172: astore 13
        //   174: aload 28
        //   176: astore 17
        //   178: aload 29
        //   180: astore 14
        //   182: getstatic 74	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   185: ldc 76
        //   187: invokevirtual 82	android/content/Context:checkSelfPermission	(Ljava/lang/String;)I
        //   190: ifne +1527 -> 1717
        //   193: aload 24
        //   195: astore 15
        //   197: aload 26
        //   199: astore 16
        //   201: aload 12
        //   203: astore 18
        //   205: aload 29
        //   207: astore 14
        //   209: getstatic 74	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   212: invokevirtual 86	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
        //   215: getstatic 92	android/provider/MediaStore$Images$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
        //   218: invokestatic 96	org/telegram/messenger/MediaController:access$5700	()[Ljava/lang/String;
        //   221: aconst_null
        //   222: aconst_null
        //   223: ldc 98
        //   225: invokestatic 102	android/provider/MediaStore$Images$Media:query	(Landroid/content/ContentResolver;Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        //   228: astore 12
        //   230: aload 25
        //   232: astore 19
        //   234: aload 27
        //   236: astore 13
        //   238: aload 12
        //   240: astore 17
        //   242: aload 12
        //   244: ifnull +1473 -> 1717
        //   247: aload 24
        //   249: astore 15
        //   251: aload 26
        //   253: astore 16
        //   255: aload 12
        //   257: astore 18
        //   259: aload 12
        //   261: astore 14
        //   263: aload 12
        //   265: ldc 104
        //   267: invokeinterface 109 2 0
        //   272: istore_1
        //   273: aload 24
        //   275: astore 15
        //   277: aload 26
        //   279: astore 16
        //   281: aload 12
        //   283: astore 18
        //   285: aload 12
        //   287: astore 14
        //   289: aload 12
        //   291: ldc 111
        //   293: invokeinterface 109 2 0
        //   298: istore_2
        //   299: aload 24
        //   301: astore 15
        //   303: aload 26
        //   305: astore 16
        //   307: aload 12
        //   309: astore 18
        //   311: aload 12
        //   313: astore 14
        //   315: aload 12
        //   317: ldc 113
        //   319: invokeinterface 109 2 0
        //   324: istore_3
        //   325: aload 24
        //   327: astore 15
        //   329: aload 26
        //   331: astore 16
        //   333: aload 12
        //   335: astore 18
        //   337: aload 12
        //   339: astore 14
        //   341: aload 12
        //   343: ldc 115
        //   345: invokeinterface 109 2 0
        //   350: istore 4
        //   352: aload 24
        //   354: astore 15
        //   356: aload 26
        //   358: astore 16
        //   360: aload 12
        //   362: astore 18
        //   364: aload 12
        //   366: astore 14
        //   368: aload 12
        //   370: ldc 117
        //   372: invokeinterface 109 2 0
        //   377: istore 5
        //   379: aload 24
        //   381: astore 15
        //   383: aload 26
        //   385: astore 16
        //   387: aload 12
        //   389: astore 18
        //   391: aload 12
        //   393: astore 14
        //   395: aload 12
        //   397: ldc 119
        //   399: invokeinterface 109 2 0
        //   404: istore 6
        //   406: aconst_null
        //   407: astore 14
        //   409: aload 20
        //   411: astore 13
        //   413: aload 12
        //   415: invokeinterface 123 1 0
        //   420: ifeq +1289 -> 1709
        //   423: aload 12
        //   425: iload_1
        //   426: invokeinterface 127 2 0
        //   431: istore 7
        //   433: aload 12
        //   435: iload_2
        //   436: invokeinterface 127 2 0
        //   441: istore 8
        //   443: aload 12
        //   445: iload_3
        //   446: invokeinterface 131 2 0
        //   451: astore 27
        //   453: aload 12
        //   455: iload 4
        //   457: invokeinterface 131 2 0
        //   462: astore 26
        //   464: aload 12
        //   466: iload 5
        //   468: invokeinterface 135 2 0
        //   473: lstore 10
        //   475: aload 12
        //   477: iload 6
        //   479: invokeinterface 127 2 0
        //   484: istore 9
        //   486: aload 26
        //   488: ifnull -75 -> 413
        //   491: aload 26
        //   493: invokevirtual 141	java/lang/String:length	()I
        //   496: ifeq -83 -> 413
        //   499: new 143	org/telegram/messenger/MediaController$PhotoEntry
        //   502: dup
        //   503: iload 8
        //   505: iload 7
        //   507: lload 10
        //   509: aload 26
        //   511: iload 9
        //   513: iconst_0
        //   514: invokespecial 146	org/telegram/messenger/MediaController$PhotoEntry:<init>	(IIJLjava/lang/String;IZ)V
        //   517: astore 25
        //   519: aload 14
        //   521: ifnonnull +1453 -> 1974
        //   524: new 148	org/telegram/messenger/MediaController$AlbumEntry
        //   527: dup
        //   528: iconst_0
        //   529: ldc -106
        //   531: ldc -105
        //   533: invokestatic 156	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
        //   536: aload 25
        //   538: iconst_0
        //   539: invokespecial 159	org/telegram/messenger/MediaController$AlbumEntry:<init>	(ILjava/lang/String;Lorg/telegram/messenger/MediaController$PhotoEntry;Z)V
        //   542: astore 17
        //   544: aload 17
        //   546: astore 15
        //   548: aload 13
        //   550: astore 16
        //   552: aload 12
        //   554: astore 18
        //   556: aload 12
        //   558: astore 14
        //   560: aload 30
        //   562: iconst_0
        //   563: aload 17
        //   565: invokevirtual 163	java/util/ArrayList:add	(ILjava/lang/Object;)V
        //   568: aload 17
        //   570: ifnull +26 -> 596
        //   573: aload 17
        //   575: astore 15
        //   577: aload 13
        //   579: astore 16
        //   581: aload 12
        //   583: astore 18
        //   585: aload 12
        //   587: astore 14
        //   589: aload 17
        //   591: aload 25
        //   593: invokevirtual 167	org/telegram/messenger/MediaController$AlbumEntry:addPhoto	(Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   596: aload 17
        //   598: astore 15
        //   600: aload 13
        //   602: astore 16
        //   604: aload 12
        //   606: astore 18
        //   608: aload 12
        //   610: astore 14
        //   612: aload 32
        //   614: iload 8
        //   616: invokestatic 173	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   619: invokevirtual 177	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
        //   622: checkcast 148	org/telegram/messenger/MediaController$AlbumEntry
        //   625: astore 24
        //   627: aload 24
        //   629: astore 20
        //   631: aload 13
        //   633: astore 19
        //   635: aload 24
        //   637: ifnonnull +152 -> 789
        //   640: aload 17
        //   642: astore 15
        //   644: aload 13
        //   646: astore 16
        //   648: aload 12
        //   650: astore 18
        //   652: aload 12
        //   654: astore 14
        //   656: new 148	org/telegram/messenger/MediaController$AlbumEntry
        //   659: dup
        //   660: iload 8
        //   662: aload 27
        //   664: aload 25
        //   666: iconst_0
        //   667: invokespecial 159	org/telegram/messenger/MediaController$AlbumEntry:<init>	(ILjava/lang/String;Lorg/telegram/messenger/MediaController$PhotoEntry;Z)V
        //   670: astore 20
        //   672: aload 17
        //   674: astore 15
        //   676: aload 13
        //   678: astore 16
        //   680: aload 12
        //   682: astore 18
        //   684: aload 12
        //   686: astore 14
        //   688: aload 32
        //   690: iload 8
        //   692: invokestatic 173	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   695: aload 20
        //   697: invokevirtual 181	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   700: pop
        //   701: aload 13
        //   703: ifnonnull +120 -> 823
        //   706: aload 33
        //   708: ifnull +115 -> 823
        //   711: aload 26
        //   713: ifnull +110 -> 823
        //   716: aload 17
        //   718: astore 15
        //   720: aload 13
        //   722: astore 16
        //   724: aload 12
        //   726: astore 18
        //   728: aload 12
        //   730: astore 14
        //   732: aload 26
        //   734: aload 33
        //   736: invokevirtual 185	java/lang/String:startsWith	(Ljava/lang/String;)Z
        //   739: ifeq +84 -> 823
        //   742: aload 17
        //   744: astore 15
        //   746: aload 13
        //   748: astore 16
        //   750: aload 12
        //   752: astore 18
        //   754: aload 12
        //   756: astore 14
        //   758: aload 30
        //   760: iconst_0
        //   761: aload 20
        //   763: invokevirtual 163	java/util/ArrayList:add	(ILjava/lang/Object;)V
        //   766: aload 17
        //   768: astore 15
        //   770: aload 13
        //   772: astore 16
        //   774: aload 12
        //   776: astore 18
        //   778: aload 12
        //   780: astore 14
        //   782: iload 8
        //   784: invokestatic 173	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   787: astore 19
        //   789: aload 17
        //   791: astore 15
        //   793: aload 19
        //   795: astore 16
        //   797: aload 12
        //   799: astore 18
        //   801: aload 12
        //   803: astore 14
        //   805: aload 20
        //   807: aload 25
        //   809: invokevirtual 167	org/telegram/messenger/MediaController$AlbumEntry:addPhoto	(Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   812: aload 17
        //   814: astore 14
        //   816: aload 19
        //   818: astore 13
        //   820: goto -407 -> 413
        //   823: aload 17
        //   825: astore 15
        //   827: aload 13
        //   829: astore 16
        //   831: aload 12
        //   833: astore 18
        //   835: aload 12
        //   837: astore 14
        //   839: aload 30
        //   841: aload 20
        //   843: invokevirtual 188	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   846: pop
        //   847: aload 13
        //   849: astore 19
        //   851: goto -62 -> 789
        //   854: astore 12
        //   856: aload 18
        //   858: astore 13
        //   860: aload 13
        //   862: astore 14
        //   864: ldc -66
        //   866: aload 12
        //   868: invokestatic 196	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   871: aload 15
        //   873: astore 18
        //   875: aload 16
        //   877: astore 20
        //   879: aload 13
        //   881: astore 12
        //   883: aload 13
        //   885: ifnull +22 -> 907
        //   888: aload 13
        //   890: invokeinterface 199 1 0
        //   895: aload 13
        //   897: astore 12
        //   899: aload 16
        //   901: astore 20
        //   903: aload 15
        //   905: astore 18
        //   907: aload 22
        //   909: astore 13
        //   911: aload 12
        //   913: astore 14
        //   915: aload 12
        //   917: astore 15
        //   919: getstatic 68	android/os/Build$VERSION:SDK_INT	I
        //   922: bipush 23
        //   924: if_icmplt +62 -> 986
        //   927: aload 22
        //   929: astore 13
        //   931: aload 12
        //   933: astore 14
        //   935: aload 12
        //   937: astore 15
        //   939: aload 23
        //   941: astore 17
        //   943: aload 12
        //   945: astore 19
        //   947: getstatic 68	android/os/Build$VERSION:SDK_INT	I
        //   950: bipush 23
        //   952: if_icmplt +929 -> 1881
        //   955: aload 22
        //   957: astore 13
        //   959: aload 12
        //   961: astore 14
        //   963: aload 12
        //   965: astore 15
        //   967: aload 23
        //   969: astore 17
        //   971: aload 12
        //   973: astore 19
        //   975: getstatic 74	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   978: ldc 76
        //   980: invokevirtual 82	android/content/Context:checkSelfPermission	(Ljava/lang/String;)I
        //   983: ifne +898 -> 1881
        //   986: aload 22
        //   988: astore 13
        //   990: aload 12
        //   992: astore 14
        //   994: aload 12
        //   996: astore 15
        //   998: aload 32
        //   1000: invokevirtual 202	java/util/HashMap:clear	()V
        //   1003: aconst_null
        //   1004: astore 24
        //   1006: aload 22
        //   1008: astore 13
        //   1010: aload 12
        //   1012: astore 14
        //   1014: aload 12
        //   1016: astore 15
        //   1018: getstatic 74	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   1021: invokevirtual 86	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
        //   1024: getstatic 205	android/provider/MediaStore$Video$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
        //   1027: invokestatic 208	org/telegram/messenger/MediaController:access$5800	()[Ljava/lang/String;
        //   1030: aconst_null
        //   1031: aconst_null
        //   1032: ldc 98
        //   1034: invokestatic 102	android/provider/MediaStore$Images$Media:query	(Landroid/content/ContentResolver;Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        //   1037: astore 16
        //   1039: aload 23
        //   1041: astore 17
        //   1043: aload 16
        //   1045: astore 19
        //   1047: aload 16
        //   1049: ifnull +832 -> 1881
        //   1052: aload 22
        //   1054: astore 13
        //   1056: aload 16
        //   1058: astore 14
        //   1060: aload 16
        //   1062: astore 15
        //   1064: aload 16
        //   1066: ldc 104
        //   1068: invokeinterface 109 2 0
        //   1073: istore_1
        //   1074: aload 22
        //   1076: astore 13
        //   1078: aload 16
        //   1080: astore 14
        //   1082: aload 16
        //   1084: astore 15
        //   1086: aload 16
        //   1088: ldc 111
        //   1090: invokeinterface 109 2 0
        //   1095: istore_2
        //   1096: aload 22
        //   1098: astore 13
        //   1100: aload 16
        //   1102: astore 14
        //   1104: aload 16
        //   1106: astore 15
        //   1108: aload 16
        //   1110: ldc 113
        //   1112: invokeinterface 109 2 0
        //   1117: istore_3
        //   1118: aload 22
        //   1120: astore 13
        //   1122: aload 16
        //   1124: astore 14
        //   1126: aload 16
        //   1128: astore 15
        //   1130: aload 16
        //   1132: ldc 115
        //   1134: invokeinterface 109 2 0
        //   1139: istore 4
        //   1141: aload 22
        //   1143: astore 13
        //   1145: aload 16
        //   1147: astore 14
        //   1149: aload 16
        //   1151: astore 15
        //   1153: aload 16
        //   1155: ldc 117
        //   1157: invokeinterface 109 2 0
        //   1162: istore 5
        //   1164: aload 21
        //   1166: astore 12
        //   1168: aload 24
        //   1170: astore 21
        //   1172: aload 12
        //   1174: astore 13
        //   1176: aload 16
        //   1178: astore 14
        //   1180: aload 16
        //   1182: astore 15
        //   1184: aload 12
        //   1186: astore 17
        //   1188: aload 16
        //   1190: astore 19
        //   1192: aload 16
        //   1194: invokeinterface 123 1 0
        //   1199: ifeq +682 -> 1881
        //   1202: aload 12
        //   1204: astore 13
        //   1206: aload 16
        //   1208: astore 14
        //   1210: aload 16
        //   1212: astore 15
        //   1214: aload 16
        //   1216: iload_1
        //   1217: invokeinterface 127 2 0
        //   1222: istore 6
        //   1224: aload 12
        //   1226: astore 13
        //   1228: aload 16
        //   1230: astore 14
        //   1232: aload 16
        //   1234: astore 15
        //   1236: aload 16
        //   1238: iload_2
        //   1239: invokeinterface 127 2 0
        //   1244: istore 7
        //   1246: aload 12
        //   1248: astore 13
        //   1250: aload 16
        //   1252: astore 14
        //   1254: aload 16
        //   1256: astore 15
        //   1258: aload 16
        //   1260: iload_3
        //   1261: invokeinterface 131 2 0
        //   1266: astore 25
        //   1268: aload 12
        //   1270: astore 13
        //   1272: aload 16
        //   1274: astore 14
        //   1276: aload 16
        //   1278: astore 15
        //   1280: aload 16
        //   1282: iload 4
        //   1284: invokeinterface 131 2 0
        //   1289: astore 24
        //   1291: aload 12
        //   1293: astore 13
        //   1295: aload 16
        //   1297: astore 14
        //   1299: aload 16
        //   1301: astore 15
        //   1303: aload 16
        //   1305: iload 5
        //   1307: invokeinterface 135 2 0
        //   1312: lstore 10
        //   1314: aload 24
        //   1316: ifnull -144 -> 1172
        //   1319: aload 12
        //   1321: astore 13
        //   1323: aload 16
        //   1325: astore 14
        //   1327: aload 16
        //   1329: astore 15
        //   1331: aload 24
        //   1333: invokevirtual 141	java/lang/String:length	()I
        //   1336: ifeq -164 -> 1172
        //   1339: aload 12
        //   1341: astore 13
        //   1343: aload 16
        //   1345: astore 14
        //   1347: aload 16
        //   1349: astore 15
        //   1351: new 143	org/telegram/messenger/MediaController$PhotoEntry
        //   1354: dup
        //   1355: iload 7
        //   1357: iload 6
        //   1359: lload 10
        //   1361: aload 24
        //   1363: iconst_0
        //   1364: iconst_1
        //   1365: invokespecial 146	org/telegram/messenger/MediaController$PhotoEntry:<init>	(IIJLjava/lang/String;IZ)V
        //   1368: astore 23
        //   1370: aload 21
        //   1372: astore 19
        //   1374: aload 21
        //   1376: ifnonnull +55 -> 1431
        //   1379: aload 12
        //   1381: astore 13
        //   1383: aload 16
        //   1385: astore 14
        //   1387: aload 16
        //   1389: astore 15
        //   1391: new 148	org/telegram/messenger/MediaController$AlbumEntry
        //   1394: dup
        //   1395: iconst_0
        //   1396: ldc -46
        //   1398: ldc -45
        //   1400: invokestatic 156	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
        //   1403: aload 23
        //   1405: iconst_1
        //   1406: invokespecial 159	org/telegram/messenger/MediaController$AlbumEntry:<init>	(ILjava/lang/String;Lorg/telegram/messenger/MediaController$PhotoEntry;Z)V
        //   1409: astore 19
        //   1411: aload 12
        //   1413: astore 13
        //   1415: aload 16
        //   1417: astore 14
        //   1419: aload 16
        //   1421: astore 15
        //   1423: aload 31
        //   1425: iconst_0
        //   1426: aload 19
        //   1428: invokevirtual 163	java/util/ArrayList:add	(ILjava/lang/Object;)V
        //   1431: aload 19
        //   1433: ifnull +22 -> 1455
        //   1436: aload 12
        //   1438: astore 13
        //   1440: aload 16
        //   1442: astore 14
        //   1444: aload 16
        //   1446: astore 15
        //   1448: aload 19
        //   1450: aload 23
        //   1452: invokevirtual 167	org/telegram/messenger/MediaController$AlbumEntry:addPhoto	(Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   1455: aload 12
        //   1457: astore 13
        //   1459: aload 16
        //   1461: astore 14
        //   1463: aload 16
        //   1465: astore 15
        //   1467: aload 32
        //   1469: iload 7
        //   1471: invokestatic 173	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   1474: invokevirtual 177	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
        //   1477: checkcast 148	org/telegram/messenger/MediaController$AlbumEntry
        //   1480: astore 22
        //   1482: aload 22
        //   1484: astore 21
        //   1486: aload 12
        //   1488: astore 17
        //   1490: aload 22
        //   1492: ifnonnull +132 -> 1624
        //   1495: aload 12
        //   1497: astore 13
        //   1499: aload 16
        //   1501: astore 14
        //   1503: aload 16
        //   1505: astore 15
        //   1507: new 148	org/telegram/messenger/MediaController$AlbumEntry
        //   1510: dup
        //   1511: iload 7
        //   1513: aload 25
        //   1515: aload 23
        //   1517: iconst_1
        //   1518: invokespecial 159	org/telegram/messenger/MediaController$AlbumEntry:<init>	(ILjava/lang/String;Lorg/telegram/messenger/MediaController$PhotoEntry;Z)V
        //   1521: astore 21
        //   1523: aload 12
        //   1525: astore 13
        //   1527: aload 16
        //   1529: astore 14
        //   1531: aload 16
        //   1533: astore 15
        //   1535: aload 32
        //   1537: iload 7
        //   1539: invokestatic 173	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   1542: aload 21
        //   1544: invokevirtual 181	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   1547: pop
        //   1548: aload 12
        //   1550: ifnonnull +287 -> 1837
        //   1553: aload 33
        //   1555: ifnull +282 -> 1837
        //   1558: aload 24
        //   1560: ifnull +277 -> 1837
        //   1563: aload 12
        //   1565: astore 13
        //   1567: aload 16
        //   1569: astore 14
        //   1571: aload 16
        //   1573: astore 15
        //   1575: aload 24
        //   1577: aload 33
        //   1579: invokevirtual 185	java/lang/String:startsWith	(Ljava/lang/String;)Z
        //   1582: ifeq +255 -> 1837
        //   1585: aload 12
        //   1587: astore 13
        //   1589: aload 16
        //   1591: astore 14
        //   1593: aload 16
        //   1595: astore 15
        //   1597: aload 31
        //   1599: iconst_0
        //   1600: aload 21
        //   1602: invokevirtual 163	java/util/ArrayList:add	(ILjava/lang/Object;)V
        //   1605: aload 12
        //   1607: astore 13
        //   1609: aload 16
        //   1611: astore 14
        //   1613: aload 16
        //   1615: astore 15
        //   1617: iload 7
        //   1619: invokestatic 173	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   1622: astore 17
        //   1624: aload 17
        //   1626: astore 13
        //   1628: aload 16
        //   1630: astore 14
        //   1632: aload 16
        //   1634: astore 15
        //   1636: aload 21
        //   1638: aload 23
        //   1640: invokevirtual 167	org/telegram/messenger/MediaController$AlbumEntry:addPhoto	(Lorg/telegram/messenger/MediaController$PhotoEntry;)V
        //   1643: aload 19
        //   1645: astore 21
        //   1647: aload 17
        //   1649: astore 12
        //   1651: goto -479 -> 1172
        //   1654: astore 12
        //   1656: aload 14
        //   1658: astore 15
        //   1660: ldc -66
        //   1662: aload 12
        //   1664: invokestatic 196	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   1667: aload 13
        //   1669: astore 12
        //   1671: aload 14
        //   1673: ifnull +14 -> 1687
        //   1676: aload 14
        //   1678: invokeinterface 199 1 0
        //   1683: aload 13
        //   1685: astore 12
        //   1687: new 13	org/telegram/messenger/MediaController$22$1
        //   1690: dup
        //   1691: aload_0
        //   1692: aload 18
        //   1694: aload 30
        //   1696: aload 20
        //   1698: aload 31
        //   1700: aload 12
        //   1702: invokespecial 214	org/telegram/messenger/MediaController$22$1:<init>	(Lorg/telegram/messenger/MediaController$22;Lorg/telegram/messenger/MediaController$AlbumEntry;Ljava/util/ArrayList;Ljava/lang/Integer;Ljava/util/ArrayList;Ljava/lang/Integer;)V
        //   1705: invokestatic 220	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
        //   1708: return
        //   1709: aload 12
        //   1711: astore 17
        //   1713: aload 14
        //   1715: astore 19
        //   1717: aload 19
        //   1719: astore 18
        //   1721: aload 13
        //   1723: astore 20
        //   1725: aload 17
        //   1727: astore 12
        //   1729: aload 17
        //   1731: ifnull -824 -> 907
        //   1734: aload 17
        //   1736: invokeinterface 199 1 0
        //   1741: aload 19
        //   1743: astore 18
        //   1745: aload 13
        //   1747: astore 20
        //   1749: aload 17
        //   1751: astore 12
        //   1753: goto -846 -> 907
        //   1756: astore 12
        //   1758: ldc -66
        //   1760: aload 12
        //   1762: invokestatic 196	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   1765: aload 19
        //   1767: astore 18
        //   1769: aload 13
        //   1771: astore 20
        //   1773: aload 17
        //   1775: astore 12
        //   1777: goto -870 -> 907
        //   1780: astore 12
        //   1782: ldc -66
        //   1784: aload 12
        //   1786: invokestatic 196	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   1789: aload 15
        //   1791: astore 18
        //   1793: aload 16
        //   1795: astore 20
        //   1797: aload 13
        //   1799: astore 12
        //   1801: goto -894 -> 907
        //   1804: astore 13
        //   1806: aload 14
        //   1808: astore 12
        //   1810: aload 12
        //   1812: ifnull +10 -> 1822
        //   1815: aload 12
        //   1817: invokeinterface 199 1 0
        //   1822: aload 13
        //   1824: athrow
        //   1825: astore 12
        //   1827: ldc -66
        //   1829: aload 12
        //   1831: invokestatic 196	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   1834: goto -12 -> 1822
        //   1837: aload 12
        //   1839: astore 13
        //   1841: aload 16
        //   1843: astore 14
        //   1845: aload 16
        //   1847: astore 15
        //   1849: aload 31
        //   1851: aload 21
        //   1853: invokevirtual 188	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   1856: pop
        //   1857: aload 12
        //   1859: astore 17
        //   1861: goto -237 -> 1624
        //   1864: astore 12
        //   1866: aload 15
        //   1868: ifnull +10 -> 1878
        //   1871: aload 15
        //   1873: invokeinterface 199 1 0
        //   1878: aload 12
        //   1880: athrow
        //   1881: aload 17
        //   1883: astore 12
        //   1885: aload 19
        //   1887: ifnull -200 -> 1687
        //   1890: aload 19
        //   1892: invokeinterface 199 1 0
        //   1897: aload 17
        //   1899: astore 12
        //   1901: goto -214 -> 1687
        //   1904: astore 12
        //   1906: ldc -66
        //   1908: aload 12
        //   1910: invokestatic 196	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   1913: aload 17
        //   1915: astore 12
        //   1917: goto -230 -> 1687
        //   1920: astore 12
        //   1922: ldc -66
        //   1924: aload 12
        //   1926: invokestatic 196	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   1929: aload 13
        //   1931: astore 12
        //   1933: goto -246 -> 1687
        //   1936: astore 13
        //   1938: ldc -66
        //   1940: aload 13
        //   1942: invokestatic 196	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   1945: goto -67 -> 1878
        //   1948: astore 13
        //   1950: goto -140 -> 1810
        //   1953: astore 17
        //   1955: aload 14
        //   1957: astore 15
        //   1959: aload 13
        //   1961: astore 16
        //   1963: aload 12
        //   1965: astore 13
        //   1967: aload 17
        //   1969: astore 12
        //   1971: goto -1111 -> 860
        //   1974: aload 14
        //   1976: astore 17
        //   1978: goto -1410 -> 568
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	1981	0	this	22
        //   272	945	1	i	int
        //   298	941	2	j	int
        //   324	937	3	k	int
        //   350	933	4	m	int
        //   377	929	5	n	int
        //   404	954	6	i1	int
        //   431	1187	7	i2	int
        //   441	342	8	i3	int
        //   484	28	9	i4	int
        //   473	887	10	l	long
        //   92	744	12	localCursor	Cursor
        //   854	13	12	localThrowable1	Throwable
        //   881	769	12	localObject1	Object
        //   1654	9	12	localThrowable2	Throwable
        //   1669	83	12	localObject2	Object
        //   1756	5	12	localException1	Exception
        //   1775	1	12	localObject3	Object
        //   1780	5	12	localException2	Exception
        //   1799	17	12	localObject4	Object
        //   1825	33	12	localException3	Exception
        //   1864	15	12	localObject5	Object
        //   1883	17	12	localObject6	Object
        //   1904	5	12	localException4	Exception
        //   1915	1	12	localObject7	Object
        //   1920	5	12	localException5	Exception
        //   1931	39	12	localObject8	Object
        //   136	1662	13	localObject9	Object
        //   1804	19	13	localObject10	Object
        //   1839	91	13	localException6	Exception
        //   1936	5	13	localException7	Exception
        //   1948	12	13	localObject11	Object
        //   1965	1	13	localObject12	Object
        //   108	1867	14	localObject13	Object
        //   96	1862	15	localObject14	Object
        //   100	1862	16	localObject15	Object
        //   140	1774	17	localObject16	Object
        //   1953	15	17	localThrowable3	Throwable
        //   1976	1	17	localObject17	Object
        //   104	1688	18	localObject18	Object
        //   132	1759	19	localObject19	Object
        //   74	1722	20	localObject20	Object
        //   83	1769	21	localObject21	Object
        //   77	1414	22	localAlbumEntry	MediaController.AlbumEntry
        //   80	1559	23	localPhotoEntry	MediaController.PhotoEntry
        //   31	1545	24	localObject22	Object
        //   28	1486	25	localObject23	Object
        //   68	665	26	str1	String
        //   71	592	27	str2	String
        //   86	89	28	localObject24	Object
        //   89	117	29	localObject25	Object
        //   7	1688	30	localArrayList1	ArrayList
        //   16	1834	31	localArrayList2	ArrayList
        //   25	1511	32	localHashMap	HashMap
        //   65	1513	33	str3	String
        // Exception table:
        //   from	to	target	type
        //   110	118	854	java/lang/Throwable
        //   146	154	854	java/lang/Throwable
        //   182	193	854	java/lang/Throwable
        //   209	230	854	java/lang/Throwable
        //   263	273	854	java/lang/Throwable
        //   289	299	854	java/lang/Throwable
        //   315	325	854	java/lang/Throwable
        //   341	352	854	java/lang/Throwable
        //   368	379	854	java/lang/Throwable
        //   395	406	854	java/lang/Throwable
        //   560	568	854	java/lang/Throwable
        //   589	596	854	java/lang/Throwable
        //   612	627	854	java/lang/Throwable
        //   656	672	854	java/lang/Throwable
        //   688	701	854	java/lang/Throwable
        //   732	742	854	java/lang/Throwable
        //   758	766	854	java/lang/Throwable
        //   782	789	854	java/lang/Throwable
        //   805	812	854	java/lang/Throwable
        //   839	847	854	java/lang/Throwable
        //   919	927	1654	java/lang/Throwable
        //   947	955	1654	java/lang/Throwable
        //   975	986	1654	java/lang/Throwable
        //   998	1003	1654	java/lang/Throwable
        //   1018	1039	1654	java/lang/Throwable
        //   1064	1074	1654	java/lang/Throwable
        //   1086	1096	1654	java/lang/Throwable
        //   1108	1118	1654	java/lang/Throwable
        //   1130	1141	1654	java/lang/Throwable
        //   1153	1164	1654	java/lang/Throwable
        //   1192	1202	1654	java/lang/Throwable
        //   1214	1224	1654	java/lang/Throwable
        //   1236	1246	1654	java/lang/Throwable
        //   1258	1268	1654	java/lang/Throwable
        //   1280	1291	1654	java/lang/Throwable
        //   1303	1314	1654	java/lang/Throwable
        //   1331	1339	1654	java/lang/Throwable
        //   1351	1370	1654	java/lang/Throwable
        //   1391	1411	1654	java/lang/Throwable
        //   1423	1431	1654	java/lang/Throwable
        //   1448	1455	1654	java/lang/Throwable
        //   1467	1482	1654	java/lang/Throwable
        //   1507	1523	1654	java/lang/Throwable
        //   1535	1548	1654	java/lang/Throwable
        //   1575	1585	1654	java/lang/Throwable
        //   1597	1605	1654	java/lang/Throwable
        //   1617	1624	1654	java/lang/Throwable
        //   1636	1643	1654	java/lang/Throwable
        //   1849	1857	1654	java/lang/Throwable
        //   1734	1741	1756	java/lang/Exception
        //   888	895	1780	java/lang/Exception
        //   110	118	1804	finally
        //   146	154	1804	finally
        //   182	193	1804	finally
        //   209	230	1804	finally
        //   263	273	1804	finally
        //   289	299	1804	finally
        //   315	325	1804	finally
        //   341	352	1804	finally
        //   368	379	1804	finally
        //   395	406	1804	finally
        //   560	568	1804	finally
        //   589	596	1804	finally
        //   612	627	1804	finally
        //   656	672	1804	finally
        //   688	701	1804	finally
        //   732	742	1804	finally
        //   758	766	1804	finally
        //   782	789	1804	finally
        //   805	812	1804	finally
        //   839	847	1804	finally
        //   864	871	1804	finally
        //   1815	1822	1825	java/lang/Exception
        //   919	927	1864	finally
        //   947	955	1864	finally
        //   975	986	1864	finally
        //   998	1003	1864	finally
        //   1018	1039	1864	finally
        //   1064	1074	1864	finally
        //   1086	1096	1864	finally
        //   1108	1118	1864	finally
        //   1130	1141	1864	finally
        //   1153	1164	1864	finally
        //   1192	1202	1864	finally
        //   1214	1224	1864	finally
        //   1236	1246	1864	finally
        //   1258	1268	1864	finally
        //   1280	1291	1864	finally
        //   1303	1314	1864	finally
        //   1331	1339	1864	finally
        //   1351	1370	1864	finally
        //   1391	1411	1864	finally
        //   1423	1431	1864	finally
        //   1448	1455	1864	finally
        //   1467	1482	1864	finally
        //   1507	1523	1864	finally
        //   1535	1548	1864	finally
        //   1575	1585	1864	finally
        //   1597	1605	1864	finally
        //   1617	1624	1864	finally
        //   1636	1643	1864	finally
        //   1660	1667	1864	finally
        //   1849	1857	1864	finally
        //   1890	1897	1904	java/lang/Exception
        //   1676	1683	1920	java/lang/Exception
        //   1871	1878	1936	java/lang/Exception
        //   413	486	1948	finally
        //   491	519	1948	finally
        //   524	544	1948	finally
        //   413	486	1953	java/lang/Throwable
        //   491	519	1953	java/lang/Throwable
        //   524	544	1953	java/lang/Throwable
      }
    });
    localThread.setPriority(1);
    localThread.start();
  }
  
  private native int openOpusFile(String paramString);
  
  private void playNextMessage(boolean paramBoolean)
  {
    ArrayList localArrayList;
    if (this.shuffleMusic)
    {
      localArrayList = this.shuffledPlaylist;
      if ((!paramBoolean) || (this.repeatMode != 2) || (this.forceLoopCurrentPlaylist)) {
        break label62;
      }
      cleanupPlayer(false, false);
      playAudio((MessageObject)localArrayList.get(this.currentPlaylistNum));
    }
    label62:
    label353:
    do
    {
      do
      {
        return;
        localArrayList = this.playlist;
        break;
        this.currentPlaylistNum += 1;
        if (this.currentPlaylistNum < localArrayList.size()) {
          break label353;
        }
        this.currentPlaylistNum = 0;
        if ((!paramBoolean) || (this.repeatMode != 0) || (this.forceLoopCurrentPlaylist)) {
          break label353;
        }
      } while ((this.audioPlayer == null) && (this.audioTrackPlayer == null));
      if (this.audioPlayer != null) {}
      for (;;)
      {
        try
        {
          this.audioPlayer.reset();
        }
        catch (Exception localException2)
        {
          try
          {
            this.audioPlayer.stop();
          }
          catch (Exception localException2)
          {
            try
            {
              this.audioPlayer.release();
              this.audioPlayer = null;
              stopProgressTimer();
              this.lastProgress = 0;
              this.buffersWrited = 0;
              this.isPaused = true;
              this.playingMessageObject.audioProgress = 0.0F;
              this.playingMessageObject.audioProgressSec = 0;
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioProgressDidChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0) });
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioPlayStateChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()) });
              return;
              localException1 = localException1;
              FileLog.e("tmessages", localException1);
              continue;
              localException2 = localException2;
              FileLog.e("tmessages", localException2);
              continue;
            }
            catch (Exception localException3)
            {
              FileLog.e("tmessages", localException3);
              continue;
            }
          }
        }
        if (this.audioTrackPlayer != null) {
          try
          {
            synchronized (this.playerObjectSync)
            {
              this.audioTrackPlayer.pause();
              this.audioTrackPlayer.flush();
            }
          }
          catch (Exception localException4)
          {
            try
            {
              for (;;)
              {
                this.audioTrackPlayer.release();
                this.audioTrackPlayer = null;
                break;
                localObject2 = finally;
                throw ((Throwable)localObject2);
                localException4 = localException4;
                FileLog.e("tmessages", localException4);
              }
            }
            catch (Exception localException5)
            {
              for (;;)
              {
                FileLog.e("tmessages", localException5);
              }
            }
          }
        }
      }
    } while ((this.currentPlaylistNum < 0) || (this.currentPlaylistNum >= ((ArrayList)???).size()));
    this.playMusicAgain = true;
    playAudio((MessageObject)((ArrayList)???).get(this.currentPlaylistNum));
  }
  
  private void processLaterArrays()
  {
    Iterator localIterator = this.addLaterArray.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      addLoadingFileObserver((String)localEntry.getKey(), (FileDownloadProgressListener)localEntry.getValue());
    }
    this.addLaterArray.clear();
    localIterator = this.deleteLaterArray.iterator();
    while (localIterator.hasNext()) {
      removeLoadingFileObserver((FileDownloadProgressListener)localIterator.next());
    }
    this.deleteLaterArray.clear();
  }
  
  @TargetApi(16)
  private long readAndWriteTrack(MessageObject paramMessageObject, MediaExtractor paramMediaExtractor, MP4Builder paramMP4Builder, MediaCodec.BufferInfo paramBufferInfo, long paramLong1, long paramLong2, File paramFile, boolean paramBoolean)
    throws Exception
  {
    int m = selectTrack(paramMediaExtractor, paramBoolean);
    if (m >= 0)
    {
      paramMediaExtractor.selectTrack(m);
      Object localObject = paramMediaExtractor.getTrackFormat(m);
      int n = paramMP4Builder.addTrack((MediaFormat)localObject, paramBoolean);
      int i = ((MediaFormat)localObject).getInteger("max-input-size");
      int k = 0;
      long l1;
      long l2;
      label86:
      int j;
      int i1;
      label143:
      long l3;
      label290:
      long l5;
      long l6;
      if (paramLong1 > 0L)
      {
        paramMediaExtractor.seekTo(paramLong1, 0);
        localObject = ByteBuffer.allocateDirect(i);
        l1 = -1L;
        checkConversionCanceled();
        l2 = -100L;
        if (k != 0) {
          break label422;
        }
        checkConversionCanceled();
        i = 0;
        j = 0;
        i1 = paramMediaExtractor.getSampleTrackIndex();
        if (i1 != m) {
          break label382;
        }
        paramBufferInfo.size = paramMediaExtractor.readSampleData((ByteBuffer)localObject, 0);
        if (paramBufferInfo.size < 0) {
          break label360;
        }
        paramBufferInfo.presentationTimeUs = paramMediaExtractor.getSampleTime();
        i = j;
        l3 = l2;
        long l4 = l1;
        if (paramBufferInfo.size > 0)
        {
          i = j;
          l3 = l2;
          l4 = l1;
          if (j == 0)
          {
            l4 = l1;
            if (paramLong1 > 0L)
            {
              l4 = l1;
              if (l1 == -1L) {
                l4 = paramBufferInfo.presentationTimeUs;
              }
            }
            if ((paramLong2 >= 0L) && (paramBufferInfo.presentationTimeUs >= paramLong2)) {
              break label372;
            }
            if (paramBufferInfo.presentationTimeUs > l2)
            {
              paramBufferInfo.offset = 0;
              paramBufferInfo.flags = paramMediaExtractor.getSampleFlags();
              if (paramMP4Builder.writeSampleData(n, (ByteBuffer)localObject, paramBufferInfo, paramBoolean)) {
                didWriteData(paramMessageObject, paramFile, false, false);
              }
            }
            l3 = paramBufferInfo.presentationTimeUs;
            i = j;
          }
        }
        j = i;
        l5 = l3;
        l6 = l4;
        if (i == 0)
        {
          paramMediaExtractor.advance();
          l6 = l4;
          l5 = l3;
          j = i;
        }
      }
      for (;;)
      {
        l2 = l5;
        l1 = l6;
        if (j == 0) {
          break label86;
        }
        k = 1;
        l2 = l5;
        l1 = l6;
        break label86;
        paramMediaExtractor.seekTo(0L, 0);
        break;
        label360:
        paramBufferInfo.size = 0;
        j = 1;
        break label143;
        label372:
        i = 1;
        l3 = l2;
        break label290;
        label382:
        if (i1 == -1)
        {
          j = 1;
          l5 = l2;
          l6 = l1;
        }
        else
        {
          paramMediaExtractor.advance();
          j = i;
          l5 = l2;
          l6 = l1;
        }
      }
      label422:
      paramMediaExtractor.unselectTrack(m);
      return l1;
    }
    return -1L;
  }
  
  private native void readOpusFile(ByteBuffer paramByteBuffer, int paramInt, int[] paramArrayOfInt);
  
  public static void saveFile(final String paramString1, Context paramContext, int paramInt, final String paramString2, final String paramString3)
  {
    if (paramString1 == null) {}
    final Object localObject1;
    do
    {
      return;
      localObject2 = null;
      localObject1 = localObject2;
      if (paramString1 != null)
      {
        localObject1 = localObject2;
        if (paramString1.length() != 0)
        {
          paramString1 = new File(paramString1);
          localObject1 = paramString1;
          if (!paramString1.exists()) {
            localObject1 = null;
          }
        }
      }
    } while ((localObject1 == null) || (!((File)localObject1).exists()));
    Object localObject2 = null;
    paramString1 = null;
    if (paramContext != null) {}
    for (;;)
    {
      try
      {
        paramString1 = new ProgressDialog(paramContext);
        FileLog.e("tmessages", paramContext);
      }
      catch (Exception paramContext)
      {
        try
        {
          paramString1.setMessage(LocaleController.getString("Loading", 2131165905));
          paramString1.setCanceledOnTouchOutside(false);
          paramString1.setCancelable(false);
          paramString1.setProgressStyle(1);
          paramString1.setMax(100);
          paramString1.show();
          new Thread(new Runnable()
          {
            /* Error */
            public void run()
            {
              // Byte code:
              //   0: aconst_null
              //   1: astore 11
              //   3: aload_0
              //   4: getfield 28	org/telegram/messenger/MediaController$21:val$type	I
              //   7: ifne +299 -> 306
              //   10: invokestatic 49	org/telegram/messenger/AndroidUtilities:generatePicturePath	()Ljava/io/File;
              //   13: astore 11
              //   15: aload 11
              //   17: invokevirtual 55	java/io/File:exists	()Z
              //   20: ifne +9 -> 29
              //   23: aload 11
              //   25: invokevirtual 58	java/io/File:createNewFile	()Z
              //   28: pop
              //   29: aconst_null
              //   30: astore 15
              //   32: aconst_null
              //   33: astore 14
              //   35: aconst_null
              //   36: astore 18
              //   38: aconst_null
              //   39: astore 17
              //   41: iconst_1
              //   42: istore_2
              //   43: invokestatic 64	java/lang/System:currentTimeMillis	()J
              //   46: lstore_3
              //   47: lload_3
              //   48: ldc2_w 65
              //   51: lsub
              //   52: lstore 5
              //   54: aload 17
              //   56: astore 12
              //   58: aload 18
              //   60: astore 13
              //   62: new 68	java/io/FileInputStream
              //   65: dup
              //   66: aload_0
              //   67: getfield 32	org/telegram/messenger/MediaController$21:val$sourceFile	Ljava/io/File;
              //   70: invokespecial 71	java/io/FileInputStream:<init>	(Ljava/io/File;)V
              //   73: invokevirtual 75	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
              //   76: astore 16
              //   78: aload 17
              //   80: astore 12
              //   82: aload 16
              //   84: astore 14
              //   86: aload 18
              //   88: astore 13
              //   90: aload 16
              //   92: astore 15
              //   94: new 77	java/io/FileOutputStream
              //   97: dup
              //   98: aload 11
              //   100: invokespecial 78	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
              //   103: invokevirtual 79	java/io/FileOutputStream:getChannel	()Ljava/nio/channels/FileChannel;
              //   106: astore 17
              //   108: aload 17
              //   110: astore 12
              //   112: aload 16
              //   114: astore 14
              //   116: aload 17
              //   118: astore 13
              //   120: aload 16
              //   122: astore 15
              //   124: aload 16
              //   126: invokevirtual 84	java/nio/channels/FileChannel:size	()J
              //   129: lstore 9
              //   131: lconst_0
              //   132: lstore_3
              //   133: lload_3
              //   134: lload 9
              //   136: lcmp
              //   137: ifge +391 -> 528
              //   140: aload 17
              //   142: astore 12
              //   144: aload 16
              //   146: astore 14
              //   148: aload 17
              //   150: astore 13
              //   152: aload 16
              //   154: astore 15
              //   156: aload 17
              //   158: aload 16
              //   160: lload_3
              //   161: ldc2_w 85
              //   164: lload 9
              //   166: lload_3
              //   167: lsub
              //   168: invokestatic 92	java/lang/Math:min	(JJ)J
              //   171: invokevirtual 96	java/nio/channels/FileChannel:transferFrom	(Ljava/nio/channels/ReadableByteChannel;JJ)J
              //   174: pop2
              //   175: lload 5
              //   177: lstore 7
              //   179: aload 17
              //   181: astore 12
              //   183: aload 16
              //   185: astore 14
              //   187: aload 17
              //   189: astore 13
              //   191: aload 16
              //   193: astore 15
              //   195: aload_0
              //   196: getfield 34	org/telegram/messenger/MediaController$21:val$finalProgress	Landroid/app/ProgressDialog;
              //   199: ifnull +94 -> 293
              //   202: lload 5
              //   204: lstore 7
              //   206: aload 17
              //   208: astore 12
              //   210: aload 16
              //   212: astore 14
              //   214: aload 17
              //   216: astore 13
              //   218: aload 16
              //   220: astore 15
              //   222: lload 5
              //   224: invokestatic 64	java/lang/System:currentTimeMillis	()J
              //   227: ldc2_w 65
              //   230: lsub
              //   231: lcmp
              //   232: ifgt +61 -> 293
              //   235: aload 17
              //   237: astore 12
              //   239: aload 16
              //   241: astore 14
              //   243: aload 17
              //   245: astore 13
              //   247: aload 16
              //   249: astore 15
              //   251: invokestatic 64	java/lang/System:currentTimeMillis	()J
              //   254: lstore 7
              //   256: aload 17
              //   258: astore 12
              //   260: aload 16
              //   262: astore 14
              //   264: aload 17
              //   266: astore 13
              //   268: aload 16
              //   270: astore 15
              //   272: new 13	org/telegram/messenger/MediaController$21$1
              //   275: dup
              //   276: aload_0
              //   277: lload_3
              //   278: l2f
              //   279: lload 9
              //   281: l2f
              //   282: fdiv
              //   283: ldc 97
              //   285: fmul
              //   286: f2i
              //   287: invokespecial 100	org/telegram/messenger/MediaController$21$1:<init>	(Lorg/telegram/messenger/MediaController$21;I)V
              //   290: invokestatic 104	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
              //   293: lload_3
              //   294: ldc2_w 85
              //   297: ladd
              //   298: lstore_3
              //   299: lload 7
              //   301: lstore 5
              //   303: goto -170 -> 133
              //   306: aload_0
              //   307: getfield 28	org/telegram/messenger/MediaController$21:val$type	I
              //   310: iconst_1
              //   311: if_icmpne +11 -> 322
              //   314: invokestatic 107	org/telegram/messenger/AndroidUtilities:generateVideoPath	()Ljava/io/File;
              //   317: astore 11
              //   319: goto -304 -> 15
              //   322: aload_0
              //   323: getfield 28	org/telegram/messenger/MediaController$21:val$type	I
              //   326: iconst_2
              //   327: if_icmpne +35 -> 362
              //   330: getstatic 112	android/os/Environment:DIRECTORY_DOWNLOADS	Ljava/lang/String;
              //   333: invokestatic 116	android/os/Environment:getExternalStoragePublicDirectory	(Ljava/lang/String;)Ljava/io/File;
              //   336: astore 11
              //   338: aload 11
              //   340: invokevirtual 119	java/io/File:mkdir	()Z
              //   343: pop
              //   344: new 51	java/io/File
              //   347: dup
              //   348: aload 11
              //   350: aload_0
              //   351: getfield 30	org/telegram/messenger/MediaController$21:val$name	Ljava/lang/String;
              //   354: invokespecial 122	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
              //   357: astore 11
              //   359: goto -344 -> 15
              //   362: aload_0
              //   363: getfield 28	org/telegram/messenger/MediaController$21:val$type	I
              //   366: iconst_3
              //   367: if_icmpne +35 -> 402
              //   370: getstatic 125	android/os/Environment:DIRECTORY_MUSIC	Ljava/lang/String;
              //   373: invokestatic 116	android/os/Environment:getExternalStoragePublicDirectory	(Ljava/lang/String;)Ljava/io/File;
              //   376: astore 11
              //   378: aload 11
              //   380: invokevirtual 128	java/io/File:mkdirs	()Z
              //   383: pop
              //   384: new 51	java/io/File
              //   387: dup
              //   388: aload 11
              //   390: aload_0
              //   391: getfield 30	org/telegram/messenger/MediaController$21:val$name	Ljava/lang/String;
              //   394: invokespecial 122	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
              //   397: astore 11
              //   399: goto -384 -> 15
              //   402: aload_0
              //   403: getfield 28	org/telegram/messenger/MediaController$21:val$type	I
              //   406: bipush 10
              //   408: if_icmpne +57 -> 465
              //   411: new 51	java/io/File
              //   414: dup
              //   415: new 130	java/lang/StringBuilder
              //   418: dup
              //   419: invokespecial 131	java/lang/StringBuilder:<init>	()V
              //   422: invokestatic 134	android/os/Environment:getExternalStorageDirectory	()Ljava/io/File;
              //   425: invokevirtual 138	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
              //   428: ldc -116
              //   430: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
              //   433: invokevirtual 147	java/lang/StringBuilder:toString	()Ljava/lang/String;
              //   436: invokespecial 150	java/io/File:<init>	(Ljava/lang/String;)V
              //   439: astore 11
              //   441: aload 11
              //   443: invokevirtual 128	java/io/File:mkdirs	()Z
              //   446: pop
              //   447: new 51	java/io/File
              //   450: dup
              //   451: aload 11
              //   453: aload_0
              //   454: getfield 30	org/telegram/messenger/MediaController$21:val$name	Ljava/lang/String;
              //   457: invokespecial 122	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
              //   460: astore 11
              //   462: goto -447 -> 15
              //   465: aload_0
              //   466: getfield 28	org/telegram/messenger/MediaController$21:val$type	I
              //   469: bipush 11
              //   471: if_icmpne -456 -> 15
              //   474: new 51	java/io/File
              //   477: dup
              //   478: new 130	java/lang/StringBuilder
              //   481: dup
              //   482: invokespecial 131	java/lang/StringBuilder:<init>	()V
              //   485: invokestatic 134	android/os/Environment:getExternalStorageDirectory	()Ljava/io/File;
              //   488: invokevirtual 138	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
              //   491: ldc -104
              //   493: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
              //   496: invokevirtual 147	java/lang/StringBuilder:toString	()Ljava/lang/String;
              //   499: invokespecial 150	java/io/File:<init>	(Ljava/lang/String;)V
              //   502: astore 11
              //   504: aload 11
              //   506: invokevirtual 128	java/io/File:mkdirs	()Z
              //   509: pop
              //   510: new 51	java/io/File
              //   513: dup
              //   514: aload 11
              //   516: aload_0
              //   517: getfield 30	org/telegram/messenger/MediaController$21:val$name	Ljava/lang/String;
              //   520: invokespecial 122	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
              //   523: astore 11
              //   525: goto -510 -> 15
              //   528: aload 16
              //   530: ifnull +8 -> 538
              //   533: aload 16
              //   535: invokevirtual 155	java/nio/channels/FileChannel:close	()V
              //   538: iload_2
              //   539: istore_1
              //   540: aload 17
              //   542: ifnull +10 -> 552
              //   545: aload 17
              //   547: invokevirtual 155	java/nio/channels/FileChannel:close	()V
              //   550: iload_2
              //   551: istore_1
              //   552: iload_1
              //   553: ifeq +52 -> 605
              //   556: aload_0
              //   557: getfield 28	org/telegram/messenger/MediaController$21:val$type	I
              //   560: iconst_2
              //   561: if_icmpne +146 -> 707
              //   564: getstatic 161	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
              //   567: ldc -93
              //   569: invokevirtual 169	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
              //   572: checkcast 171	android/app/DownloadManager
              //   575: aload 11
              //   577: invokevirtual 174	java/io/File:getName	()Ljava/lang/String;
              //   580: aload 11
              //   582: invokevirtual 174	java/io/File:getName	()Ljava/lang/String;
              //   585: iconst_0
              //   586: aload_0
              //   587: getfield 36	org/telegram/messenger/MediaController$21:val$mime	Ljava/lang/String;
              //   590: aload 11
              //   592: invokevirtual 177	java/io/File:getAbsolutePath	()Ljava/lang/String;
              //   595: aload 11
              //   597: invokevirtual 180	java/io/File:length	()J
              //   600: iconst_1
              //   601: invokevirtual 184	android/app/DownloadManager:addCompletedDownload	(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;JZ)J
              //   604: pop2
              //   605: aload_0
              //   606: getfield 34	org/telegram/messenger/MediaController$21:val$finalProgress	Landroid/app/ProgressDialog;
              //   609: ifnull +14 -> 623
              //   612: new 15	org/telegram/messenger/MediaController$21$2
              //   615: dup
              //   616: aload_0
              //   617: invokespecial 187	org/telegram/messenger/MediaController$21$2:<init>	(Lorg/telegram/messenger/MediaController$21;)V
              //   620: invokestatic 104	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
              //   623: return
              //   624: astore 16
              //   626: aload 12
              //   628: astore 13
              //   630: aload 14
              //   632: astore 15
              //   634: ldc -67
              //   636: aload 16
              //   638: invokestatic 195	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
              //   641: iconst_0
              //   642: istore_2
              //   643: aload 14
              //   645: ifnull +8 -> 653
              //   648: aload 14
              //   650: invokevirtual 155	java/nio/channels/FileChannel:close	()V
              //   653: iload_2
              //   654: istore_1
              //   655: aload 12
              //   657: ifnull -105 -> 552
              //   660: aload 12
              //   662: invokevirtual 155	java/nio/channels/FileChannel:close	()V
              //   665: iload_2
              //   666: istore_1
              //   667: goto -115 -> 552
              //   670: astore 11
              //   672: ldc -67
              //   674: aload 11
              //   676: invokestatic 195	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
              //   679: goto -74 -> 605
              //   682: astore 11
              //   684: aload 15
              //   686: ifnull +8 -> 694
              //   689: aload 15
              //   691: invokevirtual 155	java/nio/channels/FileChannel:close	()V
              //   694: aload 13
              //   696: ifnull +8 -> 704
              //   699: aload 13
              //   701: invokevirtual 155	java/nio/channels/FileChannel:close	()V
              //   704: aload 11
              //   706: athrow
              //   707: aload 11
              //   709: invokestatic 201	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
              //   712: invokestatic 205	org/telegram/messenger/AndroidUtilities:addMediaToGallery	(Landroid/net/Uri;)V
              //   715: goto -110 -> 605
              // Local variable table:
              //   start	length	slot	name	signature
              //   0	718	0	this	21
              //   539	128	1	i	int
              //   42	624	2	j	int
              //   46	253	3	l1	long
              //   52	250	5	l2	long
              //   177	123	7	l3	long
              //   129	151	9	l4	long
              //   1	595	11	localFile1	File
              //   670	5	11	localException1	Exception
              //   682	26	11	localFile2	File
              //   56	605	12	localFileChannel1	java.nio.channels.FileChannel
              //   60	640	13	localObject1	Object
              //   33	616	14	localObject2	Object
              //   30	660	15	localObject3	Object
              //   76	458	16	localFileChannel2	java.nio.channels.FileChannel
              //   624	13	16	localException2	Exception
              //   39	507	17	localFileChannel3	java.nio.channels.FileChannel
              //   36	51	18	localObject4	Object
              // Exception table:
              //   from	to	target	type
              //   62	78	624	java/lang/Exception
              //   94	108	624	java/lang/Exception
              //   124	131	624	java/lang/Exception
              //   156	175	624	java/lang/Exception
              //   195	202	624	java/lang/Exception
              //   222	235	624	java/lang/Exception
              //   251	256	624	java/lang/Exception
              //   272	293	624	java/lang/Exception
              //   3	15	670	java/lang/Exception
              //   15	29	670	java/lang/Exception
              //   43	47	670	java/lang/Exception
              //   306	319	670	java/lang/Exception
              //   322	359	670	java/lang/Exception
              //   362	399	670	java/lang/Exception
              //   402	462	670	java/lang/Exception
              //   465	525	670	java/lang/Exception
              //   533	538	670	java/lang/Exception
              //   545	550	670	java/lang/Exception
              //   556	605	670	java/lang/Exception
              //   648	653	670	java/lang/Exception
              //   660	665	670	java/lang/Exception
              //   689	694	670	java/lang/Exception
              //   699	704	670	java/lang/Exception
              //   704	707	670	java/lang/Exception
              //   707	715	670	java/lang/Exception
              //   62	78	682	finally
              //   94	108	682	finally
              //   124	131	682	finally
              //   156	175	682	finally
              //   195	202	682	finally
              //   222	235	682	finally
              //   251	256	682	finally
              //   272	293	682	finally
              //   634	641	682	finally
            }
          }).start();
          return;
        }
        catch (Exception paramContext)
        {
          for (;;) {}
        }
        paramContext = paramContext;
        paramString1 = (String)localObject2;
      }
    }
  }
  
  private native int seekOpusFile(float paramFloat);
  
  private void seekOpusPlayer(final float paramFloat)
  {
    if (paramFloat == 1.0F) {
      return;
    }
    if (!this.isPaused) {
      this.audioTrackPlayer.pause();
    }
    this.audioTrackPlayer.flush();
    this.fileDecodingQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MediaController.this.seekOpusFile(paramFloat);
        synchronized (MediaController.this.playerSync)
        {
          MediaController.this.freePlayerBuffers.addAll(MediaController.this.usedPlayerBuffers);
          MediaController.this.usedPlayerBuffers.clear();
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (!MediaController.this.isPaused)
              {
                MediaController.access$2502(MediaController.this, 3);
                MediaController.access$2702(MediaController.this, ((float)MediaController.this.currentTotalPcmDuration * MediaController.12.this.val$progress));
                if (MediaController.this.audioTrackPlayer != null) {
                  MediaController.this.audioTrackPlayer.play();
                }
                MediaController.access$2602(MediaController.this, (int)((float)MediaController.this.currentTotalPcmDuration / 48.0F * MediaController.12.this.val$progress));
                MediaController.this.checkPlayerQueue();
              }
            }
          });
          return;
        }
      }
    });
  }
  
  @SuppressLint({"NewApi"})
  public static MediaCodecInfo selectCodec(String paramString)
  {
    int k = MediaCodecList.getCodecCount();
    Object localObject1 = null;
    int i = 0;
    while (i < k)
    {
      MediaCodecInfo localMediaCodecInfo = MediaCodecList.getCodecInfoAt(i);
      Object localObject2;
      if (!localMediaCodecInfo.isEncoder())
      {
        localObject2 = localObject1;
        i += 1;
        localObject1 = localObject2;
      }
      else
      {
        String[] arrayOfString = localMediaCodecInfo.getSupportedTypes();
        int m = arrayOfString.length;
        int j = 0;
        for (;;)
        {
          localObject2 = localObject1;
          if (j >= m) {
            break;
          }
          localObject2 = localObject1;
          if (arrayOfString[j].equalsIgnoreCase(paramString))
          {
            localObject1 = localMediaCodecInfo;
            if (!((MediaCodecInfo)localObject1).getName().equals("OMX.SEC.avc.enc")) {
              return (MediaCodecInfo)localObject1;
            }
            localObject2 = localObject1;
            if (((MediaCodecInfo)localObject1).getName().equals("OMX.SEC.AVC.Encoder")) {
              return (MediaCodecInfo)localObject1;
            }
          }
          j += 1;
          localObject1 = localObject2;
        }
      }
    }
    return (MediaCodecInfo)localObject1;
  }
  
  @SuppressLint({"NewApi"})
  public static int selectColorFormat(MediaCodecInfo paramMediaCodecInfo, String paramString)
  {
    paramString = paramMediaCodecInfo.getCapabilitiesForType(paramString);
    int j = 0;
    int i = 0;
    while (i < paramString.colorFormats.length)
    {
      int k = paramString.colorFormats[i];
      if (isRecognizedFormat(k))
      {
        j = k;
        if ((!paramMediaCodecInfo.getName().equals("OMX.SEC.AVC.Encoder")) || (k != 19)) {
          return k;
        }
      }
      i += 1;
    }
    return j;
  }
  
  @TargetApi(16)
  private int selectTrack(MediaExtractor paramMediaExtractor, boolean paramBoolean)
  {
    int j = paramMediaExtractor.getTrackCount();
    int i = 0;
    while (i < j)
    {
      String str = paramMediaExtractor.getTrackFormat(i).getString("mime");
      if (paramBoolean)
      {
        if (!str.startsWith("audio/")) {}
      }
      else {
        while (str.startsWith("video/")) {
          return i;
        }
      }
      i += 1;
    }
    return -5;
  }
  
  private void setPlayerVolume()
  {
    for (;;)
    {
      try
      {
        if (this.audioFocus == 1) {
          break label54;
        }
        f = 1.0F;
        if (this.audioPlayer != null)
        {
          this.audioPlayer.setVolume(f, f);
          return;
        }
        if (this.audioTrackPlayer != null)
        {
          this.audioTrackPlayer.setStereoVolume(f, f);
          return;
        }
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
      }
      return;
      label54:
      float f = 0.2F;
    }
  }
  
  private void startAudioAgain(boolean paramBoolean)
  {
    if (this.playingMessageObject == null) {
      return;
    }
    if (this.audioPlayer != null) {}
    final MessageObject localMessageObject;
    for (int i = 1;; i = 0)
    {
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioRouteChanged, new Object[] { Boolean.valueOf(this.useFrontSpeaker) });
      localMessageObject = this.playingMessageObject;
      float f = this.playingMessageObject.audioProgress;
      cleanupPlayer(false, true);
      localMessageObject.audioProgress = f;
      playAudio(localMessageObject);
      if (!paramBoolean) {
        break;
      }
      if (i == 0) {
        break label103;
      }
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          MediaController.this.pauseAudio(localMessageObject);
        }
      }, 100L);
      return;
    }
    label103:
    pauseAudio(localMessageObject);
  }
  
  private void startProgressTimer(final MessageObject paramMessageObject)
  {
    synchronized (this.progressTimerSync)
    {
      Timer localTimer = this.progressTimer;
      if (localTimer != null) {}
      try
      {
        this.progressTimer.cancel();
        this.progressTimer = null;
        this.progressTimer = new Timer();
        this.progressTimer.schedule(new TimerTask()
        {
          public void run()
          {
            synchronized (MediaController.this.sync)
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if ((MediaController.5.this.val$currentPlayingMessageObject != null) && ((MediaController.this.audioPlayer != null) || (MediaController.this.audioTrackPlayer != null)) && (!MediaController.this.isPaused))
                  {
                    int j;
                    int k;
                    do
                    {
                      try
                      {
                        if (MediaController.this.ignoreFirstProgress != 0)
                        {
                          MediaController.access$2510(MediaController.this);
                          return;
                        }
                        if (MediaController.this.audioPlayer != null)
                        {
                          i = MediaController.this.audioPlayer.getCurrentPosition();
                          f = MediaController.this.lastProgress / MediaController.this.audioPlayer.getDuration();
                          if (i <= MediaController.this.lastProgress) {
                            break;
                          }
                          MediaController.access$2602(MediaController.this, i);
                          MediaController.5.this.val$currentPlayingMessageObject.audioProgress = f;
                          MediaController.5.this.val$currentPlayingMessageObject.audioProgressSec = (MediaController.this.lastProgress / 1000);
                          NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioProgressDidChanged, new Object[] { Integer.valueOf(MediaController.5.this.val$currentPlayingMessageObject.getId()), Float.valueOf(f) });
                          return;
                        }
                      }
                      catch (Exception localException)
                      {
                        FileLog.e("tmessages", localException);
                        return;
                      }
                      j = (int)((float)MediaController.this.lastPlayPcm / 48.0F);
                      float f = (float)MediaController.this.lastPlayPcm / (float)MediaController.this.currentTotalPcmDuration;
                      k = MediaController.this.lastProgress;
                      int i = j;
                    } while (j != k);
                  }
                }
              });
              return;
            }
          }
        }, 0L, 17L);
        return;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
      }
    }
  }
  
  private native int startRecord(String paramString);
  
  private void startVideoConvertFromQueue()
  {
    if (!this.videoConvertQueue.isEmpty()) {}
    synchronized (this.videoConvertSync)
    {
      this.cancelCurrentVideoConversion = false;
      ??? = (MessageObject)this.videoConvertQueue.get(0);
      Intent localIntent = new Intent(ApplicationLoader.applicationContext, VideoEncodingService.class);
      localIntent.putExtra("path", ((MessageObject)???).messageOwner.attachPath);
      ApplicationLoader.applicationContext.startService(localIntent);
      VideoConvertRunnable.runConversion((MessageObject)???);
      return;
    }
  }
  
  private void stopProgressTimer()
  {
    synchronized (this.progressTimerSync)
    {
      Timer localTimer = this.progressTimer;
      if (localTimer != null) {}
      try
      {
        this.progressTimer.cancel();
        this.progressTimer = null;
        return;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
      }
    }
  }
  
  private native void stopRecord();
  
  private void stopRecordingInternal(final int paramInt)
  {
    if (paramInt != 0)
    {
      final TLRPC.TL_document localTL_document = this.recordingAudio;
      final File localFile = this.recordingAudioFile;
      this.fileEncodingQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          MediaController.this.stopRecord();
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              String str = null;
              MediaController.19.this.val$audioToSend.date = ConnectionsManager.getInstance().getCurrentTime();
              MediaController.19.this.val$audioToSend.size = ((int)MediaController.19.this.val$recordingAudioFileToSend.length());
              Object localObject = new TLRPC.TL_documentAttributeAudio();
              ((TLRPC.TL_documentAttributeAudio)localObject).voice = true;
              ((TLRPC.TL_documentAttributeAudio)localObject).waveform = MediaController.this.getWaveform2(MediaController.this.recordSamples, MediaController.this.recordSamples.length);
              if (((TLRPC.TL_documentAttributeAudio)localObject).waveform != null) {
                ((TLRPC.TL_documentAttributeAudio)localObject).flags |= 0x4;
              }
              long l = MediaController.this.recordTimeCount;
              ((TLRPC.TL_documentAttributeAudio)localObject).duration = ((int)(MediaController.this.recordTimeCount / 1000L));
              MediaController.19.this.val$audioToSend.attributes.add(localObject);
              if (l > 700L)
              {
                if (MediaController.19.this.val$send == 1) {
                  SendMessagesHelper.getInstance().sendMessage(MediaController.19.this.val$audioToSend, null, MediaController.19.this.val$recordingAudioFileToSend.getAbsolutePath(), MediaController.this.recordDialogId, MediaController.this.recordReplyingMessageObject, null, null);
                }
                NotificationCenter localNotificationCenter = NotificationCenter.getInstance();
                int i = NotificationCenter.audioDidSent;
                if (MediaController.19.this.val$send == 2) {}
                for (localObject = MediaController.19.this.val$audioToSend;; localObject = null)
                {
                  if (MediaController.19.this.val$send == 2) {
                    str = MediaController.19.this.val$recordingAudioFileToSend.getAbsolutePath();
                  }
                  localNotificationCenter.postNotificationName(i, new Object[] { localObject, str });
                  return;
                }
              }
              MediaController.19.this.val$recordingAudioFileToSend.delete();
            }
          });
        }
      });
    }
    try
    {
      if (this.audioRecorder != null)
      {
        this.audioRecorder.release();
        this.audioRecorder = null;
      }
      this.recordingAudio = null;
      this.recordingAudioFile = null;
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  private native int writeFrame(ByteBuffer paramByteBuffer, int paramInt);
  
  public void addLoadingFileObserver(String paramString, FileDownloadProgressListener paramFileDownloadProgressListener)
  {
    addLoadingFileObserver(paramString, null, paramFileDownloadProgressListener);
  }
  
  public void addLoadingFileObserver(String paramString, MessageObject paramMessageObject, FileDownloadProgressListener paramFileDownloadProgressListener)
  {
    if (this.listenerInProgress)
    {
      this.addLaterArray.put(paramString, paramFileDownloadProgressListener);
      return;
    }
    removeLoadingFileObserver(paramFileDownloadProgressListener);
    ArrayList localArrayList2 = (ArrayList)this.loadingFileObservers.get(paramString);
    ArrayList localArrayList1 = localArrayList2;
    if (localArrayList2 == null)
    {
      localArrayList1 = new ArrayList();
      this.loadingFileObservers.put(paramString, localArrayList1);
    }
    localArrayList1.add(new WeakReference(paramFileDownloadProgressListener));
    if (paramMessageObject != null)
    {
      localArrayList2 = (ArrayList)this.loadingFileMessagesObservers.get(paramString);
      localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        this.loadingFileMessagesObservers.put(paramString, localArrayList1);
      }
      localArrayList1.add(paramMessageObject);
    }
    this.observersByTag.put(Integer.valueOf(paramFileDownloadProgressListener.getObserverTag()), paramString);
  }
  
  public void addLoadingFileObserver2(String paramString, MessageObject paramMessageObject, FileDownloadProgressListener paramFileDownloadProgressListener)
  {
    if (this.listenerInProgress)
    {
      this.addLaterArray.put(paramString, paramFileDownloadProgressListener);
      return;
    }
    removeLoadingFileObserver(paramFileDownloadProgressListener);
    ArrayList localArrayList2 = (ArrayList)this.loadingFileObservers.get(paramString);
    ArrayList localArrayList1 = localArrayList2;
    if (localArrayList2 == null)
    {
      localArrayList1 = new ArrayList();
      this.loadingFileObservers.put(paramString, localArrayList1);
    }
    localArrayList1.add(new WeakReference(paramFileDownloadProgressListener));
    if (paramMessageObject != null)
    {
      localArrayList2 = (ArrayList)this.loadingFileMessagesObservers.get(paramString);
      localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        this.loadingFileMessagesObservers.put(paramString, localArrayList1);
      }
      localArrayList1.add(paramMessageObject);
    }
    this.observersByTag.put(Integer.valueOf(paramFileDownloadProgressListener.getObserverTag()), paramString);
  }
  
  public boolean canAutoplayGifs()
  {
    return this.autoplayGifs;
  }
  
  public boolean canCustomTabs()
  {
    return this.customTabs;
  }
  
  public boolean canDirectShare()
  {
    return this.directShare;
  }
  
  public boolean canDownloadMedia(int paramInt)
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
    if ((localSharedPreferences.getBoolean("fav_auto_download", false)) && (localSharedPreferences.getInt("selected_tab", 5) == 5)) {}
    while ((getCurrentDownloadMask() & paramInt) != 0) {
      return true;
    }
    return false;
  }
  
  public boolean canRaiseToSpeak()
  {
    return this.raiseToSpeak;
  }
  
  public boolean canSaveToGallery()
  {
    return this.saveToGallery;
  }
  
  public void cancelVideoConvert(MessageObject arg1)
  {
    if (??? == null) {
      synchronized (this.videoConvertSync)
      {
        this.cancelCurrentVideoConversion = true;
        return;
      }
    }
    if (!this.videoConvertQueue.isEmpty())
    {
      if (this.videoConvertQueue.get(0) == ???) {}
      synchronized (this.videoConvertSync)
      {
        this.cancelCurrentVideoConversion = true;
        this.videoConvertQueue.remove(???);
        return;
      }
    }
  }
  
  public void checkAutodownloadSettings()
  {
    int j = getCurrentDownloadMask();
    if (j == this.lastCheckMask) {}
    label61:
    label84:
    label105:
    label128:
    int i;
    label223:
    label278:
    label333:
    label388:
    label443:
    label498:
    do
    {
      return;
      this.lastCheckMask = j;
      if ((j & 0x1) != 0)
      {
        if (this.photoDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(1);
        }
        if ((j & 0x2) == 0) {
          break label223;
        }
        if (this.audioDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(2);
        }
        if ((j & 0x8) == 0) {
          break label278;
        }
        if (this.documentDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(8);
        }
        if ((j & 0x4) == 0) {
          break label333;
        }
        if (this.videoDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(4);
        }
        if ((j & 0x10) == 0) {
          break label388;
        }
        if (this.musicDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(16);
        }
        if ((j & 0x20) == 0) {
          break label443;
        }
        if (this.gifDownloadQueue.isEmpty()) {
          newDownloadObjectsAvailable(32);
        }
      }
      for (;;)
      {
        i = getAutodownloadMask();
        if (i != 0) {
          break label498;
        }
        MessagesStorage.getInstance().clearDownloadQueue(0);
        return;
        i = 0;
        Object localObject;
        while (i < this.photoDownloadQueue.size())
        {
          localObject = (DownloadObject)this.photoDownloadQueue.get(i);
          FileLoader.getInstance().cancelLoadFile((TLRPC.PhotoSize)((DownloadObject)localObject).object);
          i += 1;
        }
        this.photoDownloadQueue.clear();
        break;
        i = 0;
        while (i < this.audioDownloadQueue.size())
        {
          localObject = (DownloadObject)this.audioDownloadQueue.get(i);
          FileLoader.getInstance().cancelLoadFile((TLRPC.Document)((DownloadObject)localObject).object);
          i += 1;
        }
        this.audioDownloadQueue.clear();
        break label61;
        i = 0;
        while (i < this.documentDownloadQueue.size())
        {
          localObject = (TLRPC.Document)((DownloadObject)this.documentDownloadQueue.get(i)).object;
          FileLoader.getInstance().cancelLoadFile((TLRPC.Document)localObject);
          i += 1;
        }
        this.documentDownloadQueue.clear();
        break label84;
        i = 0;
        while (i < this.videoDownloadQueue.size())
        {
          localObject = (DownloadObject)this.videoDownloadQueue.get(i);
          FileLoader.getInstance().cancelLoadFile((TLRPC.Document)((DownloadObject)localObject).object);
          i += 1;
        }
        this.videoDownloadQueue.clear();
        break label105;
        i = 0;
        while (i < this.musicDownloadQueue.size())
        {
          localObject = (TLRPC.Document)((DownloadObject)this.musicDownloadQueue.get(i)).object;
          FileLoader.getInstance().cancelLoadFile((TLRPC.Document)localObject);
          i += 1;
        }
        this.musicDownloadQueue.clear();
        break label128;
        i = 0;
        while (i < this.gifDownloadQueue.size())
        {
          localObject = (TLRPC.Document)((DownloadObject)this.gifDownloadQueue.get(i)).object;
          FileLoader.getInstance().cancelLoadFile((TLRPC.Document)localObject);
          i += 1;
        }
        this.gifDownloadQueue.clear();
      }
      if ((i & 0x1) == 0) {
        MessagesStorage.getInstance().clearDownloadQueue(1);
      }
      if ((i & 0x2) == 0) {
        MessagesStorage.getInstance().clearDownloadQueue(2);
      }
      if ((i & 0x4) == 0) {
        MessagesStorage.getInstance().clearDownloadQueue(4);
      }
      if ((i & 0x8) == 0) {
        MessagesStorage.getInstance().clearDownloadQueue(8);
      }
      if ((i & 0x10) == 0) {
        MessagesStorage.getInstance().clearDownloadQueue(16);
      }
    } while ((i & 0x20) != 0);
    MessagesStorage.getInstance().clearDownloadQueue(32);
  }
  
  public void checkSaveToGalleryFiles()
  {
    try
    {
      File localFile2 = new File(Environment.getExternalStorageDirectory(), "Telegram");
      File localFile1 = new File(localFile2, "Telegram Images");
      localFile1.mkdir();
      localFile2 = new File(localFile2, "Telegram Video");
      localFile2.mkdir();
      if (this.saveToGallery)
      {
        if (localFile1.isDirectory()) {
          new File(localFile1, ".nomedia").delete();
        }
        if (localFile2.isDirectory()) {
          new File(localFile2, ".nomedia").delete();
        }
      }
      else
      {
        if (localFile1.isDirectory()) {
          new File(localFile1, ".nomedia").createNewFile();
        }
        if (localFile2.isDirectory())
        {
          new File(localFile2, ".nomedia").createNewFile();
          return;
        }
      }
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  public void cleanup()
  {
    cleanupPlayer(false, true);
    this.audioInfo = null;
    this.playMusicAgain = false;
    this.photoDownloadQueue.clear();
    this.audioDownloadQueue.clear();
    this.documentDownloadQueue.clear();
    this.videoDownloadQueue.clear();
    this.musicDownloadQueue.clear();
    this.gifDownloadQueue.clear();
    this.downloadQueueKeys.clear();
    this.videoConvertQueue.clear();
    this.playlist.clear();
    this.shuffledPlaylist.clear();
    this.generatingWaveform.clear();
    this.typingTimes.clear();
    this.voiceMessagesPlaylist = null;
    this.voiceMessagesPlaylistMap = null;
    cancelVideoConvert(null);
  }
  
  public void cleanupPlayer(boolean paramBoolean1, boolean paramBoolean2)
  {
    cleanupPlayer(paramBoolean1, paramBoolean2, false);
  }
  
  public void cleanupPlayer(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (this.audioPlayer != null) {}
    for (;;)
    {
      try
      {
        this.audioPlayer.reset();
      }
      catch (Exception localException2)
      {
        try
        {
          this.audioPlayer.stop();
        }
        catch (Exception localException2)
        {
          try
          {
            this.audioPlayer.release();
            this.audioPlayer = null;
            stopProgressTimer();
            this.lastProgress = 0;
            this.buffersWrited = 0;
            this.isPaused = false;
            Object localObject1;
            if (this.playingMessageObject != null)
            {
              if (this.downloadingCurrentMessage) {
                FileLoader.getInstance().cancelLoadFile(this.playingMessageObject.getDocument());
              }
              localObject1 = this.playingMessageObject;
              this.playingMessageObject.audioProgress = 0.0F;
              this.playingMessageObject.audioProgressSec = 0;
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioProgressDidChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0) });
              this.playingMessageObject = null;
              this.downloadingCurrentMessage = false;
              if (paramBoolean1)
              {
                NotificationsController.getInstance().audioManager.abandonAudioFocus(this);
                this.hasAudioFocus = 0;
                if (this.voiceMessagesPlaylist != null)
                {
                  if ((!paramBoolean3) || (this.voiceMessagesPlaylist.get(0) != localObject1)) {
                    break label440;
                  }
                  this.voiceMessagesPlaylist.remove(0);
                  this.voiceMessagesPlaylistMap.remove(Integer.valueOf(((MessageObject)localObject1).getId()));
                  if (this.voiceMessagesPlaylist.isEmpty())
                  {
                    this.voiceMessagesPlaylist = null;
                    this.voiceMessagesPlaylistMap = null;
                  }
                }
                if (this.voiceMessagesPlaylist == null) {
                  break label453;
                }
                playAudio((MessageObject)this.voiceMessagesPlaylist.get(0));
              }
              if (paramBoolean2)
              {
                localObject1 = new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class);
                ApplicationLoader.applicationContext.stopService((Intent)localObject1);
              }
            }
            if ((!this.useFrontSpeaker) && (!this.raiseToSpeak))
            {
              localObject1 = this.raiseChat;
              stopRaiseToEarSensors(this.raiseChat);
              this.raiseChat = ((ChatActivity)localObject1);
            }
            return;
            localException1 = localException1;
            FileLog.e("tmessages", localException1);
            continue;
            localException2 = localException2;
            FileLog.e("tmessages", localException2);
            continue;
          }
          catch (Exception localException3)
          {
            FileLog.e("tmessages", localException3);
            continue;
          }
        }
      }
      if (this.audioTrackPlayer != null)
      {
        try
        {
          synchronized (this.playerObjectSync)
          {
            this.audioTrackPlayer.pause();
            this.audioTrackPlayer.flush();
          }
        }
        catch (Exception localException4)
        {
          try
          {
            for (;;)
            {
              this.audioTrackPlayer.release();
              this.audioTrackPlayer = null;
              break;
              localObject3 = finally;
              throw ((Throwable)localObject3);
              localException4 = localException4;
              FileLog.e("tmessages", localException4);
            }
          }
          catch (Exception localException5)
          {
            for (;;)
            {
              FileLog.e("tmessages", localException5);
            }
          }
        }
        label440:
        this.voiceMessagesPlaylist = null;
        this.voiceMessagesPlaylistMap = null;
        continue;
        label453:
        if ((((MessageObject)???).isVoice()) && (((MessageObject)???).getId() != 0)) {
          startRecordingIfFromSpeaker();
        }
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioDidReset, new Object[] { Integer.valueOf(((MessageObject)???).getId()), Boolean.valueOf(paramBoolean2) });
      }
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    Object localObject1;
    Object localObject2;
    Object localObject3;
    if (paramInt == NotificationCenter.FileDidFailedLoad)
    {
      this.listenerInProgress = true;
      localObject1 = (String)paramVarArgs[0];
      this.loadingFileMessagesObservers.get(localObject1);
      localObject2 = (ArrayList)this.loadingFileObservers.get(localObject1);
      if (localObject2 != null)
      {
        paramInt = 0;
        while (paramInt < ((ArrayList)localObject2).size())
        {
          localObject3 = (WeakReference)((ArrayList)localObject2).get(paramInt);
          if (((WeakReference)localObject3).get() != null)
          {
            ((FileDownloadProgressListener)((WeakReference)localObject3).get()).onFailedDownload((String)localObject1);
            this.observersByTag.remove(Integer.valueOf(((FileDownloadProgressListener)((WeakReference)localObject3).get()).getObserverTag()));
          }
          paramInt += 1;
        }
        this.loadingFileObservers.remove(localObject1);
      }
      this.listenerInProgress = false;
      processLaterArrays();
      checkDownloadFinished((String)localObject1, ((Integer)paramVarArgs[1]).intValue());
      return;
    }
    if (paramInt == NotificationCenter.FileDidLoaded)
    {
      this.listenerInProgress = true;
      paramVarArgs = (String)paramVarArgs[0];
      if ((this.downloadingCurrentMessage) && (this.playingMessageObject != null) && (FileLoader.getAttachFileName(this.playingMessageObject.getDocument()).equals(paramVarArgs)))
      {
        this.playMusicAgain = true;
        playAudio(this.playingMessageObject);
      }
      localObject1 = (ArrayList)this.loadingFileMessagesObservers.get(paramVarArgs);
      if (localObject1 != null)
      {
        paramInt = 0;
        while (paramInt < ((ArrayList)localObject1).size())
        {
          ((MessageObject)((ArrayList)localObject1).get(paramInt)).mediaExists = true;
          paramInt += 1;
        }
        this.loadingFileMessagesObservers.remove(paramVarArgs);
      }
      localObject1 = (ArrayList)this.loadingFileObservers.get(paramVarArgs);
      if (localObject1 != null)
      {
        paramInt = 0;
        while (paramInt < ((ArrayList)localObject1).size())
        {
          localObject2 = (WeakReference)((ArrayList)localObject1).get(paramInt);
          if (((WeakReference)localObject2).get() != null)
          {
            ((FileDownloadProgressListener)((WeakReference)localObject2).get()).onSuccessDownload(paramVarArgs);
            this.observersByTag.remove(Integer.valueOf(((FileDownloadProgressListener)((WeakReference)localObject2).get()).getObserverTag()));
          }
          paramInt += 1;
        }
        this.loadingFileObservers.remove(paramVarArgs);
      }
      this.listenerInProgress = false;
      processLaterArrays();
      checkDownloadFinished(paramVarArgs, 0);
      return;
    }
    if (paramInt == NotificationCenter.FileLoadProgressChanged)
    {
      this.listenerInProgress = true;
      localObject1 = (String)paramVarArgs[0];
      localObject2 = (ArrayList)this.loadingFileObservers.get(localObject1);
      if (localObject2 != null)
      {
        paramVarArgs = (Float)paramVarArgs[1];
        localObject2 = ((ArrayList)localObject2).iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (WeakReference)((Iterator)localObject2).next();
          if (((WeakReference)localObject3).get() != null) {
            ((FileDownloadProgressListener)((WeakReference)localObject3).get()).onProgressDownload((String)localObject1, paramVarArgs.floatValue());
          }
        }
      }
      this.listenerInProgress = false;
      processLaterArrays();
      return;
    }
    if (paramInt == NotificationCenter.FileUploadProgressChanged)
    {
      this.listenerInProgress = true;
      localObject1 = (String)paramVarArgs[0];
      localObject3 = (ArrayList)this.loadingFileObservers.get(localObject1);
      if (localObject3 != null)
      {
        localObject2 = (Float)paramVarArgs[1];
        paramVarArgs = (Boolean)paramVarArgs[2];
        localObject3 = ((ArrayList)localObject3).iterator();
        while (((Iterator)localObject3).hasNext())
        {
          WeakReference localWeakReference = (WeakReference)((Iterator)localObject3).next();
          if (localWeakReference.get() != null) {
            ((FileDownloadProgressListener)localWeakReference.get()).onProgressUpload((String)localObject1, ((Float)localObject2).floatValue(), paramVarArgs.booleanValue());
          }
        }
      }
      this.listenerInProgress = false;
      processLaterArrays();
    }
    for (;;)
    {
      long l;
      try
      {
        paramVarArgs = SendMessagesHelper.getInstance().getDelayedMessages((String)localObject1);
        if (paramVarArgs == null) {
          break;
        }
        paramInt = 0;
        if (paramInt >= paramVarArgs.size()) {
          break;
        }
        localObject1 = (SendMessagesHelper.DelayedMessage)paramVarArgs.get(paramInt);
        if (((SendMessagesHelper.DelayedMessage)localObject1).encryptedChat != null) {
          break label1265;
        }
        l = ((SendMessagesHelper.DelayedMessage)localObject1).obj.getDialogId();
        localObject2 = (Long)this.typingTimes.get(Long.valueOf(l));
        if ((localObject2 != null) && (((Long)localObject2).longValue() + 4000L >= System.currentTimeMillis())) {
          break label1265;
        }
        if (MessageObject.isVideoDocument(((SendMessagesHelper.DelayedMessage)localObject1).documentLocation))
        {
          MessagesController.getInstance().sendTyping(l, 5, 0);
          this.typingTimes.put(Long.valueOf(l), Long.valueOf(System.currentTimeMillis()));
          break label1265;
        }
        if (((SendMessagesHelper.DelayedMessage)localObject1).documentLocation != null)
        {
          MessagesController.getInstance().sendTyping(l, 3, 0);
          continue;
        }
        if (((SendMessagesHelper.DelayedMessage)localObject1).location == null) {
          continue;
        }
      }
      catch (Exception paramVarArgs)
      {
        FileLog.e("tmessages", paramVarArgs);
        return;
      }
      MessagesController.getInstance().sendTyping(l, 4, 0);
      continue;
      if (paramInt == NotificationCenter.messagesDeleted)
      {
        paramInt = ((Integer)paramVarArgs[1]).intValue();
        paramVarArgs = (ArrayList)paramVarArgs[0];
        if ((this.playingMessageObject != null) && (paramInt == this.playingMessageObject.messageOwner.to_id.channel_id) && (paramVarArgs.contains(Integer.valueOf(this.playingMessageObject.getId())))) {
          cleanupPlayer(true, true);
        }
        if ((this.voiceMessagesPlaylist == null) || (this.voiceMessagesPlaylist.isEmpty()) || (paramInt != ((MessageObject)this.voiceMessagesPlaylist.get(0)).messageOwner.to_id.channel_id)) {
          break;
        }
        paramInt = 0;
        while (paramInt < paramVarArgs.size())
        {
          localObject1 = (MessageObject)this.voiceMessagesPlaylistMap.remove(paramVarArgs.get(paramInt));
          if (localObject1 != null) {
            this.voiceMessagesPlaylist.remove(localObject1);
          }
          paramInt += 1;
        }
        break;
      }
      if (paramInt == NotificationCenter.removeAllMessagesFromDialog)
      {
        l = ((Long)paramVarArgs[0]).longValue();
        if ((this.playingMessageObject == null) || (this.playingMessageObject.getDialogId() != l)) {
          break;
        }
        cleanupPlayer(false, true);
        return;
      }
      if (paramInt == NotificationCenter.musicDidLoaded)
      {
        l = ((Long)paramVarArgs[0]).longValue();
        if ((this.playingMessageObject == null) || (!this.playingMessageObject.isMusic()) || (this.playingMessageObject.getDialogId() != l)) {
          break;
        }
        paramVarArgs = (ArrayList)paramVarArgs[1];
        this.playlist.addAll(0, paramVarArgs);
        if (this.shuffleMusic)
        {
          buildShuffledPlayList();
          this.currentPlaylistNum = 0;
          return;
        }
        this.currentPlaylistNum += paramVarArgs.size();
        return;
      }
      if ((paramInt != NotificationCenter.didReceivedNewMessages) || (this.voiceMessagesPlaylist == null) || (this.voiceMessagesPlaylist.isEmpty())) {
        break;
      }
      localObject1 = (MessageObject)this.voiceMessagesPlaylist.get(0);
      if (((Long)paramVarArgs[0]).longValue() != ((MessageObject)localObject1).getDialogId()) {
        break;
      }
      paramVarArgs = (ArrayList)paramVarArgs[1];
      paramInt = 0;
      while (paramInt < paramVarArgs.size())
      {
        localObject1 = (MessageObject)paramVarArgs.get(paramInt);
        if ((((MessageObject)localObject1).isVoice()) && ((!this.voiceMessagesPlaylistUnread) || ((((MessageObject)localObject1).isContentUnread()) && (!((MessageObject)localObject1).isOut()))))
        {
          this.voiceMessagesPlaylist.add(localObject1);
          this.voiceMessagesPlaylistMap.put(Integer.valueOf(((MessageObject)localObject1).getId()), localObject1);
        }
        paramInt += 1;
      }
      break;
      label1265:
      paramInt += 1;
    }
  }
  
  public int generateObserverTag()
  {
    int i = this.lastTag;
    this.lastTag = (i + 1);
    return i;
  }
  
  public void generateWaveform(MessageObject paramMessageObject)
  {
    final String str1 = paramMessageObject.getId() + "_" + paramMessageObject.getDialogId();
    final String str2 = FileLoader.getPathToMessage(paramMessageObject.messageOwner).getAbsolutePath();
    if (this.generatingWaveform.containsKey(str1)) {
      return;
    }
    this.generatingWaveform.put(str1, paramMessageObject);
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            MessageObject localMessageObject = (MessageObject)MediaController.this.generatingWaveform.remove(MediaController.18.this.val$id);
            if (localMessageObject == null) {}
            while (this.val$waveform == null) {
              return;
            }
            int i = 0;
            for (;;)
            {
              Object localObject;
              if (i < localMessageObject.getDocument().attributes.size())
              {
                localObject = (TLRPC.DocumentAttribute)localMessageObject.getDocument().attributes.get(i);
                if ((localObject instanceof TLRPC.TL_documentAttributeAudio))
                {
                  ((TLRPC.DocumentAttribute)localObject).waveform = this.val$waveform;
                  ((TLRPC.DocumentAttribute)localObject).flags |= 0x4;
                }
              }
              else
              {
                localObject = new TLRPC.TL_messages_messages();
                ((TLRPC.TL_messages_messages)localObject).messages.add(localMessageObject.messageOwner);
                MessagesStorage.getInstance().putMessages((TLRPC.messages_Messages)localObject, localMessageObject.getDialogId(), -1, 0, false);
                localObject = new ArrayList();
                ((ArrayList)localObject).add(localMessageObject);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.replaceMessagesObjects, new Object[] { Long.valueOf(localMessageObject.getDialogId()), localObject });
                return;
              }
              i += 1;
            }
          }
        });
      }
    });
  }
  
  public AudioInfo getAudioInfo()
  {
    return this.audioInfo;
  }
  
  protected int getAutodownloadMask()
  {
    int j = 0;
    if (((this.mobileDataDownloadMask & 0x1) != 0) || ((this.wifiDownloadMask & 0x1) != 0) || ((this.roamingDownloadMask & 0x1) != 0)) {
      j = 0x0 | 0x1;
    }
    int i;
    if (((this.mobileDataDownloadMask & 0x2) == 0) && ((this.wifiDownloadMask & 0x2) == 0))
    {
      i = j;
      if ((this.roamingDownloadMask & 0x2) == 0) {}
    }
    else
    {
      i = j | 0x2;
    }
    if (((this.mobileDataDownloadMask & 0x4) == 0) && ((this.wifiDownloadMask & 0x4) == 0))
    {
      j = i;
      if ((this.roamingDownloadMask & 0x4) == 0) {}
    }
    else
    {
      j = i | 0x4;
    }
    if (((this.mobileDataDownloadMask & 0x8) == 0) && ((this.wifiDownloadMask & 0x8) == 0))
    {
      i = j;
      if ((this.roamingDownloadMask & 0x8) == 0) {}
    }
    else
    {
      i = j | 0x8;
    }
    if (((this.mobileDataDownloadMask & 0x10) == 0) && ((this.wifiDownloadMask & 0x10) == 0))
    {
      j = i;
      if ((this.roamingDownloadMask & 0x10) == 0) {}
    }
    else
    {
      j = i | 0x10;
    }
    if (((this.mobileDataDownloadMask & 0x20) == 0) && ((this.wifiDownloadMask & 0x20) == 0))
    {
      i = j;
      if ((this.roamingDownloadMask & 0x20) == 0) {}
    }
    else
    {
      i = j | 0x20;
    }
    return i;
  }
  
  public MessageObject getPlayingMessageObject()
  {
    return this.playingMessageObject;
  }
  
  public int getPlayingMessageObjectNum()
  {
    return this.currentPlaylistNum;
  }
  
  public int getRepeatMode()
  {
    return this.repeatMode;
  }
  
  public native byte[] getWaveform(String paramString);
  
  public native byte[] getWaveform2(short[] paramArrayOfShort, int paramInt);
  
  public boolean isAudioPaused()
  {
    return (this.isPaused) || (this.downloadingCurrentMessage);
  }
  
  public boolean isDownloadingCurrentMessage()
  {
    return this.downloadingCurrentMessage;
  }
  
  public boolean isPlayingAudio(MessageObject paramMessageObject)
  {
    return ((this.audioTrackPlayer != null) || (this.audioPlayer != null)) && (paramMessageObject != null) && (this.playingMessageObject != null) && ((this.playingMessageObject == null) || ((this.playingMessageObject.getId() == paramMessageObject.getId()) && (!this.downloadingCurrentMessage)));
  }
  
  protected boolean isRecordingAudio()
  {
    return (this.recordStartRunnable != null) || (this.recordingAudio != null);
  }
  
  public boolean isShuffleMusic()
  {
    return this.shuffleMusic;
  }
  
  protected void newDownloadObjectsAvailable(int paramInt)
  {
    int i = getCurrentDownloadMask();
    if (((i & 0x1) != 0) && ((paramInt & 0x1) != 0) && (this.photoDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance().getDownloadQueue(1);
    }
    if (((i & 0x2) != 0) && ((paramInt & 0x2) != 0) && (this.audioDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance().getDownloadQueue(2);
    }
    if (((i & 0x4) != 0) && ((paramInt & 0x4) != 0) && (this.videoDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance().getDownloadQueue(4);
    }
    if (((i & 0x8) != 0) && ((paramInt & 0x8) != 0) && (this.documentDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance().getDownloadQueue(8);
    }
    if (((i & 0x10) != 0) && ((paramInt & 0x10) != 0) && (this.musicDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance().getDownloadQueue(16);
    }
    if (((i & 0x20) != 0) && ((paramInt & 0x20) != 0) && (this.gifDownloadQueue.isEmpty())) {
      MessagesStorage.getInstance().getDownloadQueue(32);
    }
  }
  
  public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}
  
  public void onAudioFocusChange(int paramInt)
  {
    if (paramInt == -1)
    {
      if ((isPlayingAudio(getPlayingMessageObject())) && (!isAudioPaused())) {
        pauseAudio(getPlayingMessageObject());
      }
      this.hasAudioFocus = 0;
      this.audioFocus = 0;
    }
    for (;;)
    {
      setPlayerVolume();
      return;
      if (paramInt == 1)
      {
        this.audioFocus = 2;
        if (this.resumeAudioOnFocusGain)
        {
          this.resumeAudioOnFocusGain = false;
          if ((isPlayingAudio(getPlayingMessageObject())) && (isAudioPaused())) {
            playAudio(getPlayingMessageObject());
          }
        }
      }
      else if (paramInt == -3)
      {
        this.audioFocus = 1;
      }
      else if (paramInt == -2)
      {
        this.audioFocus = 0;
        if ((isPlayingAudio(getPlayingMessageObject())) && (!isAudioPaused()))
        {
          pauseAudio(getPlayingMessageObject());
          this.resumeAudioOnFocusGain = true;
        }
      }
    }
  }
  
  public void onSensorChanged(SensorEvent paramSensorEvent)
  {
    if (!this.sensorsStarted) {}
    label92:
    label249:
    label303:
    label506:
    label1039:
    label1101:
    label1245:
    label1251:
    label1333:
    label1557:
    for (;;)
    {
      return;
      float f;
      boolean bool;
      if (paramSensorEvent.sensor == this.proximitySensor)
      {
        FileLog.e("tmessages", "proximity changed to " + paramSensorEvent.values[0]);
        if (this.lastProximityValue == -100.0F)
        {
          this.lastProximityValue = paramSensorEvent.values[0];
          if (this.proximityHasDifferentValues) {
            this.proximityTouched = isNearToSensor(paramSensorEvent.values[0]);
          }
          if ((paramSensorEvent.sensor == this.linearSensor) || (paramSensorEvent.sensor == this.gravitySensor) || (paramSensorEvent.sensor == this.accelerometerSensor))
          {
            f = this.gravity[0] * this.linearAcceleration[0] + this.gravity[1] * this.linearAcceleration[1] + this.gravity[2] * this.linearAcceleration[2];
            if (this.raisedToBack != 6)
            {
              if ((f <= 0.0F) || (this.previousAccValue <= 0.0F)) {
                break label1101;
              }
              if ((f <= 15.0F) || (this.raisedToBack != 0)) {
                break label1039;
              }
              if ((this.raisedToTop < 6) && (!this.proximityTouched))
              {
                this.raisedToTop += 1;
                if (this.raisedToTop == 6) {
                  this.countLess = 0;
                }
              }
            }
            this.previousAccValue = f;
            if ((this.gravityFast[1] <= 2.5F) || (Math.abs(this.gravityFast[2]) >= 4.0F) || (Math.abs(this.gravityFast[0]) <= 1.5F)) {
              break label1245;
            }
            bool = true;
            this.accelerometerVertical = bool;
          }
          if ((this.raisedToBack != 6) || (!this.accelerometerVertical) || (!this.proximityTouched) || (NotificationsController.getInstance().audioManager.isWiredHeadsetOn())) {
            break label1333;
          }
          FileLog.e("tmessages", "sensor values reached");
          if ((this.playingMessageObject != null) || (this.recordStartRunnable != null) || (this.recordingAudio != null) || (PhotoViewer.getInstance().isVisible()) || (!ApplicationLoader.isScreenOn) || (this.inputFieldHasText) || (!this.allowStartRecord) || (this.raiseChat == null) || (this.callInProgress)) {
            break label1251;
          }
          if (!this.raiseToEarRecord)
          {
            FileLog.e("tmessages", "start record");
            this.useFrontSpeaker = true;
            if (!this.raiseChat.playFirstUnreadVoiceMessage())
            {
              this.raiseToEarRecord = true;
              this.useFrontSpeaker = false;
              startRecording(this.raiseChat.getDialogId(), null);
            }
            this.ignoreOnPause = true;
            if ((this.proximityHasDifferentValues) && (this.proximityWakeLock != null) && (!this.proximityWakeLock.isHeld())) {
              this.proximityWakeLock.acquire();
            }
          }
          this.raisedToBack = 0;
          this.raisedToTop = 0;
          this.countLess = 0;
        }
      }
      for (;;)
      {
        if ((this.timeSinceRaise == 0L) || (this.raisedToBack != 6) || (Math.abs(System.currentTimeMillis() - this.timeSinceRaise) <= 1000L)) {
          break label1557;
        }
        this.raisedToBack = 0;
        this.raisedToTop = 0;
        this.countLess = 0;
        this.timeSinceRaise = 0L;
        return;
        if (this.lastProximityValue == paramSensorEvent.values[0]) {
          break;
        }
        this.proximityHasDifferentValues = true;
        break;
        if (paramSensorEvent.sensor == this.accelerometerSensor)
        {
          if (this.lastTimestamp == 0L) {}
          for (double d = 0.9800000190734863D;; d = 1.0D / (1.0D + (paramSensorEvent.timestamp - this.lastTimestamp) / 1.0E9D))
          {
            this.lastTimestamp = paramSensorEvent.timestamp;
            this.gravity[0] = ((float)(this.gravity[0] * d + (1.0D - d) * paramSensorEvent.values[0]));
            this.gravity[1] = ((float)(this.gravity[1] * d + (1.0D - d) * paramSensorEvent.values[1]));
            this.gravity[2] = ((float)(this.gravity[2] * d + (1.0D - d) * paramSensorEvent.values[2]));
            this.gravityFast[0] = (0.8F * this.gravity[0] + 0.19999999F * paramSensorEvent.values[0]);
            this.gravityFast[1] = (0.8F * this.gravity[1] + 0.19999999F * paramSensorEvent.values[1]);
            this.gravityFast[2] = (0.8F * this.gravity[2] + 0.19999999F * paramSensorEvent.values[2]);
            this.linearAcceleration[0] = (paramSensorEvent.values[0] - this.gravity[0]);
            this.linearAcceleration[1] = (paramSensorEvent.values[1] - this.gravity[1]);
            this.linearAcceleration[2] = (paramSensorEvent.values[2] - this.gravity[2]);
            break;
          }
        }
        if (paramSensorEvent.sensor == this.linearSensor)
        {
          this.linearAcceleration[0] = paramSensorEvent.values[0];
          this.linearAcceleration[1] = paramSensorEvent.values[1];
          this.linearAcceleration[2] = paramSensorEvent.values[2];
          break label92;
        }
        if (paramSensorEvent.sensor != this.gravitySensor) {
          break label92;
        }
        float[] arrayOfFloat1 = this.gravityFast;
        float[] arrayOfFloat2 = this.gravity;
        f = paramSensorEvent.values[0];
        arrayOfFloat2[0] = f;
        arrayOfFloat1[0] = f;
        arrayOfFloat1 = this.gravityFast;
        arrayOfFloat2 = this.gravity;
        f = paramSensorEvent.values[1];
        arrayOfFloat2[1] = f;
        arrayOfFloat1[1] = f;
        arrayOfFloat1 = this.gravityFast;
        arrayOfFloat2 = this.gravity;
        f = paramSensorEvent.values[2];
        arrayOfFloat2[2] = f;
        arrayOfFloat1[2] = f;
        break label92;
        if (f < 15.0F) {
          this.countLess += 1;
        }
        if ((this.countLess != 10) && (this.raisedToTop == 6) && (this.raisedToBack == 0)) {
          break label249;
        }
        this.raisedToBack = 0;
        this.raisedToTop = 0;
        this.countLess = 0;
        break label249;
        if ((f >= 0.0F) || (this.previousAccValue >= 0.0F)) {
          break label249;
        }
        if ((this.raisedToTop == 6) && (f < -15.0F))
        {
          if (this.raisedToBack >= 6) {
            break label249;
          }
          this.raisedToBack += 1;
          if (this.raisedToBack != 6) {
            break label249;
          }
          this.raisedToTop = 0;
          this.countLess = 0;
          this.timeSinceRaise = System.currentTimeMillis();
          break label249;
        }
        if (f > -15.0F) {
          this.countLess += 1;
        }
        if ((this.countLess != 10) && (this.raisedToTop == 6) && (this.raisedToBack == 0)) {
          break label249;
        }
        this.raisedToTop = 0;
        this.raisedToBack = 0;
        this.countLess = 0;
        break label249;
        bool = false;
        break label303;
        if ((this.playingMessageObject == null) || (!this.playingMessageObject.isVoice()) || (this.useFrontSpeaker)) {
          break label506;
        }
        FileLog.e("tmessages", "start listen");
        if ((this.proximityHasDifferentValues) && (this.proximityWakeLock != null) && (!this.proximityWakeLock.isHeld())) {
          this.proximityWakeLock.acquire();
        }
        this.useFrontSpeaker = true;
        startAudioAgain(false);
        this.ignoreOnPause = true;
        break label506;
        if (this.proximityTouched)
        {
          if ((this.playingMessageObject != null) && (this.playingMessageObject.isVoice()) && (!this.useFrontSpeaker))
          {
            FileLog.e("tmessages", "start listen by proximity only");
            if ((this.proximityHasDifferentValues) && (this.proximityWakeLock != null) && (!this.proximityWakeLock.isHeld())) {
              this.proximityWakeLock.acquire();
            }
            this.useFrontSpeaker = true;
            startAudioAgain(false);
            this.ignoreOnPause = true;
          }
        }
        else if (!this.proximityTouched) {
          if (this.raiseToEarRecord)
          {
            FileLog.e("tmessages", "stop record");
            stopRecording(2);
            this.raiseToEarRecord = false;
            this.ignoreOnPause = false;
            if ((this.proximityHasDifferentValues) && (this.proximityWakeLock != null) && (this.proximityWakeLock.isHeld())) {
              this.proximityWakeLock.release();
            }
          }
          else if (this.useFrontSpeaker)
          {
            FileLog.e("tmessages", "stop listen");
            this.useFrontSpeaker = false;
            startAudioAgain(true);
            this.ignoreOnPause = false;
            if ((this.proximityHasDifferentValues) && (this.proximityWakeLock != null) && (this.proximityWakeLock.isHeld())) {
              this.proximityWakeLock.release();
            }
          }
        }
      }
    }
  }
  
  public boolean pauseAudio(MessageObject paramMessageObject)
  {
    if (((this.audioTrackPlayer == null) && (this.audioPlayer == null)) || (paramMessageObject == null) || (this.playingMessageObject == null) || ((this.playingMessageObject != null) && (this.playingMessageObject.getId() != paramMessageObject.getId()))) {
      return false;
    }
    stopProgressTimer();
    try
    {
      if (this.audioPlayer != null) {
        this.audioPlayer.pause();
      }
      for (;;)
      {
        this.isPaused = true;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioPlayStateChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()) });
        return true;
        if (this.audioTrackPlayer != null) {
          this.audioTrackPlayer.pause();
        }
      }
      return false;
    }
    catch (Exception paramMessageObject)
    {
      FileLog.e("tmessages", paramMessageObject);
      this.isPaused = false;
    }
  }
  
  /* Error */
  public boolean playAudio(final MessageObject paramMessageObject)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +5 -> 6
    //   4: iconst_0
    //   5: ireturn
    //   6: aload_0
    //   7: getfield 449	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   10: ifnonnull +10 -> 20
    //   13: aload_0
    //   14: getfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   17: ifnull +54 -> 71
    //   20: aload_0
    //   21: getfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   24: ifnull +47 -> 71
    //   27: aload_1
    //   28: invokevirtual 1711	org/telegram/messenger/MessageObject:getId	()I
    //   31: aload_0
    //   32: getfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   35: invokevirtual 1711	org/telegram/messenger/MessageObject:getId	()I
    //   38: if_icmpne +33 -> 71
    //   41: aload_0
    //   42: getfield 445	org/telegram/messenger/MediaController:isPaused	Z
    //   45: ifeq +9 -> 54
    //   48: aload_0
    //   49: aload_1
    //   50: invokevirtual 2314	org/telegram/messenger/MediaController:resumeAudio	(Lorg/telegram/messenger/MessageObject;)Z
    //   53: pop
    //   54: aload_0
    //   55: getfield 425	org/telegram/messenger/MediaController:raiseToSpeak	Z
    //   58: ifne +11 -> 69
    //   61: aload_0
    //   62: aload_0
    //   63: getfield 2010	org/telegram/messenger/MediaController:raiseChat	Lorg/telegram/ui/ChatActivity;
    //   66: invokevirtual 2317	org/telegram/messenger/MediaController:startRaiseToEarSensors	(Lorg/telegram/ui/ChatActivity;)V
    //   69: iconst_1
    //   70: ireturn
    //   71: aload_1
    //   72: invokevirtual 2153	org/telegram/messenger/MessageObject:isOut	()Z
    //   75: ifne +30 -> 105
    //   78: aload_1
    //   79: invokevirtual 2150	org/telegram/messenger/MessageObject:isContentUnread	()Z
    //   82: ifeq +23 -> 105
    //   85: aload_1
    //   86: getfield 1030	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   89: getfield 2123	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   92: getfield 2128	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   95: ifne +10 -> 105
    //   98: invokestatic 2108	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   101: aload_1
    //   102: invokevirtual 2320	org/telegram/messenger/MessagesController:markMessageContentAsRead	(Lorg/telegram/messenger/MessageObject;)V
    //   105: aload_0
    //   106: getfield 1727	org/telegram/messenger/MediaController:playMusicAgain	Z
    //   109: ifne +231 -> 340
    //   112: iconst_1
    //   113: istore 4
    //   115: aload_0
    //   116: getfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   119: ifnull +6 -> 125
    //   122: iconst_0
    //   123: istore 4
    //   125: aload_0
    //   126: iload 4
    //   128: iconst_0
    //   129: invokevirtual 1681	org/telegram/messenger/MediaController:cleanupPlayer	(ZZ)V
    //   132: aload_0
    //   133: iconst_0
    //   134: putfield 1727	org/telegram/messenger/MediaController:playMusicAgain	Z
    //   137: aconst_null
    //   138: astore 6
    //   140: aload 6
    //   142: astore 5
    //   144: aload_1
    //   145: getfield 1030	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   148: getfield 1035	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   151: ifnull +51 -> 202
    //   154: aload 6
    //   156: astore 5
    //   158: aload_1
    //   159: getfield 1030	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   162: getfield 1035	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   165: invokevirtual 1038	java/lang/String:length	()I
    //   168: ifle +34 -> 202
    //   171: new 1040	java/io/File
    //   174: dup
    //   175: aload_1
    //   176: getfield 1030	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   179: getfield 1035	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   182: invokespecial 1041	java/io/File:<init>	(Ljava/lang/String;)V
    //   185: astore 6
    //   187: aload 6
    //   189: astore 5
    //   191: aload 6
    //   193: invokevirtual 1044	java/io/File:exists	()Z
    //   196: ifne +6 -> 202
    //   199: aconst_null
    //   200: astore 5
    //   202: aload 5
    //   204: ifnull +142 -> 346
    //   207: aload 5
    //   209: astore 6
    //   211: aload 6
    //   213: ifnull +170 -> 383
    //   216: aload 6
    //   218: aload 5
    //   220: if_acmpeq +163 -> 383
    //   223: aload 6
    //   225: invokevirtual 1044	java/io/File:exists	()Z
    //   228: ifne +155 -> 383
    //   231: aload_1
    //   232: invokevirtual 1047	org/telegram/messenger/MessageObject:isMusic	()Z
    //   235: ifeq +148 -> 383
    //   238: invokestatic 1052	org/telegram/messenger/FileLoader:getInstance	()Lorg/telegram/messenger/FileLoader;
    //   241: aload_1
    //   242: invokevirtual 1056	org/telegram/messenger/MessageObject:getDocument	()Lorg/telegram/tgnet/TLRPC$Document;
    //   245: iconst_0
    //   246: iconst_0
    //   247: invokevirtual 1060	org/telegram/messenger/FileLoader:loadFile	(Lorg/telegram/tgnet/TLRPC$Document;ZZ)V
    //   250: aload_0
    //   251: iconst_1
    //   252: putfield 1998	org/telegram/messenger/MediaController:downloadingCurrentMessage	Z
    //   255: aload_0
    //   256: iconst_0
    //   257: putfield 445	org/telegram/messenger/MediaController:isPaused	Z
    //   260: aload_0
    //   261: iconst_0
    //   262: putfield 451	org/telegram/messenger/MediaController:lastProgress	I
    //   265: aload_0
    //   266: lconst_0
    //   267: putfield 794	org/telegram/messenger/MediaController:lastPlayPcm	J
    //   270: aload_0
    //   271: aconst_null
    //   272: putfield 1987	org/telegram/messenger/MediaController:audioInfo	Lorg/telegram/messenger/audioinfo/AudioInfo;
    //   275: aload_0
    //   276: aload_1
    //   277: putfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   280: aload_0
    //   281: getfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   284: invokevirtual 1056	org/telegram/messenger/MessageObject:getDocument	()Lorg/telegram/tgnet/TLRPC$Document;
    //   287: ifnull +71 -> 358
    //   290: new 1886	android/content/Intent
    //   293: dup
    //   294: getstatic 559	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   297: ldc_w 2004
    //   300: invokespecial 1891	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   303: astore_1
    //   304: getstatic 559	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   307: aload_1
    //   308: invokevirtual 1901	android/content/Context:startService	(Landroid/content/Intent;)Landroid/content/ComponentName;
    //   311: pop
    //   312: invokestatic 1705	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   315: getstatic 1718	org/telegram/messenger/NotificationCenter:audioPlayStateChanged	I
    //   318: iconst_1
    //   319: anewarray 4	java/lang/Object
    //   322: dup
    //   323: iconst_0
    //   324: aload_0
    //   325: getfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   328: invokevirtual 1711	org/telegram/messenger/MessageObject:getId	()I
    //   331: invokestatic 1547	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   334: aastore
    //   335: invokevirtual 1715	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   338: iconst_1
    //   339: ireturn
    //   340: iconst_0
    //   341: istore 4
    //   343: goto -228 -> 115
    //   346: aload_1
    //   347: getfield 1030	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   350: invokestatic 1064	org/telegram/messenger/FileLoader:getPathToMessage	(Lorg/telegram/tgnet/TLRPC$Message;)Ljava/io/File;
    //   353: astore 6
    //   355: goto -144 -> 211
    //   358: new 1886	android/content/Intent
    //   361: dup
    //   362: getstatic 559	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   365: ldc_w 2004
    //   368: invokespecial 1891	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   371: astore_1
    //   372: getstatic 559	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   375: aload_1
    //   376: invokevirtual 2008	android/content/Context:stopService	(Landroid/content/Intent;)Z
    //   379: pop
    //   380: goto -68 -> 312
    //   383: aload_0
    //   384: iconst_0
    //   385: putfield 1998	org/telegram/messenger/MediaController:downloadingCurrentMessage	Z
    //   388: aload_1
    //   389: invokevirtual 1047	org/telegram/messenger/MessageObject:isMusic	()Z
    //   392: ifeq +7 -> 399
    //   395: aload_0
    //   396: invokespecial 2322	org/telegram/messenger/MediaController:checkIsNextMusicFileDownloaded	()V
    //   399: aload_0
    //   400: aload 6
    //   402: invokevirtual 1583	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   405: invokespecial 2324	org/telegram/messenger/MediaController:isOpusFile	(Ljava/lang/String;)I
    //   408: iconst_1
    //   409: if_icmpne +372 -> 781
    //   412: aload_0
    //   413: getfield 463	org/telegram/messenger/MediaController:playlist	Ljava/util/ArrayList;
    //   416: invokevirtual 930	java/util/ArrayList:clear	()V
    //   419: aload_0
    //   420: getfield 465	org/telegram/messenger/MediaController:shuffledPlaylist	Ljava/util/ArrayList;
    //   423: invokevirtual 930	java/util/ArrayList:clear	()V
    //   426: aload_0
    //   427: getfield 479	org/telegram/messenger/MediaController:playerObjectSync	Ljava/lang/Object;
    //   430: astore 5
    //   432: aload 5
    //   434: monitorenter
    //   435: aload_0
    //   436: iconst_3
    //   437: putfield 457	org/telegram/messenger/MediaController:ignoreFirstProgress	I
    //   440: new 2326	java/util/concurrent/Semaphore
    //   443: dup
    //   444: iconst_0
    //   445: invokespecial 2327	java/util/concurrent/Semaphore:<init>	(I)V
    //   448: astore 7
    //   450: iconst_1
    //   451: anewarray 1856	java/lang/Boolean
    //   454: astore 8
    //   456: aload_0
    //   457: getfield 623	org/telegram/messenger/MediaController:fileDecodingQueue	Lorg/telegram/messenger/DispatchQueue;
    //   460: new 28	org/telegram/messenger/MediaController$13
    //   463: dup
    //   464: aload_0
    //   465: aload 8
    //   467: aload 6
    //   469: aload 7
    //   471: invokespecial 2330	org/telegram/messenger/MediaController$13:<init>	(Lorg/telegram/messenger/MediaController;[Ljava/lang/Boolean;Ljava/io/File;Ljava/util/concurrent/Semaphore;)V
    //   474: invokevirtual 993	org/telegram/messenger/DispatchQueue:postRunnable	(Ljava/lang/Runnable;)V
    //   477: aload 7
    //   479: invokevirtual 2331	java/util/concurrent/Semaphore:acquire	()V
    //   482: aload 8
    //   484: iconst_0
    //   485: aaload
    //   486: invokevirtual 2067	java/lang/Boolean:booleanValue	()Z
    //   489: istore 4
    //   491: iload 4
    //   493: ifne +14 -> 507
    //   496: aload 5
    //   498: monitorexit
    //   499: iconst_0
    //   500: ireturn
    //   501: astore_1
    //   502: aload 5
    //   504: monitorexit
    //   505: aload_1
    //   506: athrow
    //   507: aload_0
    //   508: aload_0
    //   509: invokespecial 2333	org/telegram/messenger/MediaController:getTotalPcmDuration	()J
    //   512: putfield 798	org/telegram/messenger/MediaController:currentTotalPcmDuration	J
    //   515: aload_0
    //   516: getfield 964	org/telegram/messenger/MediaController:useFrontSpeaker	Z
    //   519: ifeq +210 -> 729
    //   522: iconst_0
    //   523: istore_2
    //   524: aload_0
    //   525: new 521	android/media/AudioTrack
    //   528: dup
    //   529: iload_2
    //   530: ldc_w 519
    //   533: iconst_4
    //   534: iconst_2
    //   535: aload_0
    //   536: getfield 453	org/telegram/messenger/MediaController:playerBufferSize	I
    //   539: iconst_1
    //   540: invokespecial 2336	android/media/AudioTrack:<init>	(IIIIII)V
    //   543: putfield 449	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   546: aload_0
    //   547: getfield 449	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   550: fconst_1
    //   551: fconst_1
    //   552: invokevirtual 1850	android/media/AudioTrack:setStereoVolume	(FF)I
    //   555: pop
    //   556: aload_0
    //   557: getfield 449	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   560: new 30	org/telegram/messenger/MediaController$14
    //   563: dup
    //   564: aload_0
    //   565: invokespecial 2337	org/telegram/messenger/MediaController$14:<init>	(Lorg/telegram/messenger/MediaController;)V
    //   568: invokevirtual 2341	android/media/AudioTrack:setPlaybackPositionUpdateListener	(Landroid/media/AudioTrack$OnPlaybackPositionUpdateListener;)V
    //   571: aload_0
    //   572: getfield 449	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   575: invokevirtual 2344	android/media/AudioTrack:play	()V
    //   578: aload 5
    //   580: monitorexit
    //   581: aload_0
    //   582: aload_1
    //   583: invokespecial 2346	org/telegram/messenger/MediaController:checkAudioFocus	(Lorg/telegram/messenger/MessageObject;)V
    //   586: aload_0
    //   587: invokespecial 2195	org/telegram/messenger/MediaController:setPlayerVolume	()V
    //   590: aload_0
    //   591: iconst_0
    //   592: putfield 445	org/telegram/messenger/MediaController:isPaused	Z
    //   595: aload_0
    //   596: iconst_0
    //   597: putfield 451	org/telegram/messenger/MediaController:lastProgress	I
    //   600: aload_0
    //   601: lconst_0
    //   602: putfield 794	org/telegram/messenger/MediaController:lastPlayPcm	J
    //   605: aload_0
    //   606: aload_1
    //   607: putfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   610: aload_0
    //   611: getfield 425	org/telegram/messenger/MediaController:raiseToSpeak	Z
    //   614: ifne +11 -> 625
    //   617: aload_0
    //   618: aload_0
    //   619: getfield 2010	org/telegram/messenger/MediaController:raiseChat	Lorg/telegram/ui/ChatActivity;
    //   622: invokevirtual 2317	org/telegram/messenger/MediaController:startRaiseToEarSensors	(Lorg/telegram/ui/ChatActivity;)V
    //   625: aload_0
    //   626: aload_0
    //   627: getfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   630: invokespecial 2348	org/telegram/messenger/MediaController:startProgressTimer	(Lorg/telegram/messenger/MessageObject;)V
    //   633: invokestatic 1705	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   636: getstatic 2351	org/telegram/messenger/NotificationCenter:audioDidStarted	I
    //   639: iconst_1
    //   640: anewarray 4	java/lang/Object
    //   643: dup
    //   644: iconst_0
    //   645: aload_1
    //   646: aastore
    //   647: invokevirtual 1715	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   650: aload_0
    //   651: getfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   654: ifnull +408 -> 1062
    //   657: aload_0
    //   658: getfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   661: getfield 1697	org/telegram/messenger/MessageObject:audioProgress	F
    //   664: fconst_0
    //   665: fcmpl
    //   666: ifeq +29 -> 695
    //   669: aload_0
    //   670: getfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   673: invokevirtual 2354	android/media/MediaPlayer:getDuration	()I
    //   676: i2f
    //   677: aload_0
    //   678: getfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   681: getfield 1697	org/telegram/messenger/MessageObject:audioProgress	F
    //   684: fmul
    //   685: f2i
    //   686: istore_2
    //   687: aload_0
    //   688: getfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   691: iload_2
    //   692: invokevirtual 2356	android/media/MediaPlayer:seekTo	(I)V
    //   695: aload_0
    //   696: getfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   699: invokevirtual 1047	org/telegram/messenger/MessageObject:isMusic	()Z
    //   702: ifeq +405 -> 1107
    //   705: new 1886	android/content/Intent
    //   708: dup
    //   709: getstatic 559	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   712: ldc_w 2004
    //   715: invokespecial 1891	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   718: astore_1
    //   719: getstatic 559	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   722: aload_1
    //   723: invokevirtual 1901	android/content/Context:startService	(Landroid/content/Intent;)Landroid/content/ComponentName;
    //   726: pop
    //   727: iconst_1
    //   728: ireturn
    //   729: iconst_3
    //   730: istore_2
    //   731: goto -207 -> 524
    //   734: astore_1
    //   735: ldc_w 547
    //   738: aload_1
    //   739: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   742: aload_0
    //   743: getfield 449	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   746: ifnull +30 -> 776
    //   749: aload_0
    //   750: getfield 449	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   753: invokevirtual 1725	android/media/AudioTrack:release	()V
    //   756: aload_0
    //   757: aconst_null
    //   758: putfield 449	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   761: aload_0
    //   762: iconst_0
    //   763: putfield 445	org/telegram/messenger/MediaController:isPaused	Z
    //   766: aload_0
    //   767: aconst_null
    //   768: putfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   771: aload_0
    //   772: iconst_0
    //   773: putfield 1998	org/telegram/messenger/MediaController:downloadingCurrentMessage	Z
    //   776: aload 5
    //   778: monitorexit
    //   779: iconst_0
    //   780: ireturn
    //   781: aload_0
    //   782: new 1686	android/media/MediaPlayer
    //   785: dup
    //   786: invokespecial 2357	android/media/MediaPlayer:<init>	()V
    //   789: putfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   792: aload_0
    //   793: getfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   796: astore 5
    //   798: aload_0
    //   799: getfield 964	org/telegram/messenger/MediaController:useFrontSpeaker	Z
    //   802: ifeq +165 -> 967
    //   805: iconst_0
    //   806: istore_2
    //   807: aload 5
    //   809: iload_2
    //   810: invokevirtual 2360	android/media/MediaPlayer:setAudioStreamType	(I)V
    //   813: aload_0
    //   814: getfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   817: aload 6
    //   819: invokevirtual 1583	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   822: invokevirtual 2361	android/media/MediaPlayer:setDataSource	(Ljava/lang/String;)V
    //   825: aload_0
    //   826: getfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   829: new 32	org/telegram/messenger/MediaController$15
    //   832: dup
    //   833: aload_0
    //   834: aload_1
    //   835: invokespecial 2362	org/telegram/messenger/MediaController$15:<init>	(Lorg/telegram/messenger/MediaController;Lorg/telegram/messenger/MessageObject;)V
    //   838: invokevirtual 2366	android/media/MediaPlayer:setOnCompletionListener	(Landroid/media/MediaPlayer$OnCompletionListener;)V
    //   841: aload_0
    //   842: getfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   845: invokevirtual 2369	android/media/MediaPlayer:prepare	()V
    //   848: aload_0
    //   849: getfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   852: invokevirtual 2370	android/media/MediaPlayer:start	()V
    //   855: aload_1
    //   856: invokevirtual 962	org/telegram/messenger/MessageObject:isVoice	()Z
    //   859: ifeq +113 -> 972
    //   862: aload_0
    //   863: aconst_null
    //   864: putfield 1987	org/telegram/messenger/MediaController:audioInfo	Lorg/telegram/messenger/audioinfo/AudioInfo;
    //   867: aload_0
    //   868: getfield 463	org/telegram/messenger/MediaController:playlist	Ljava/util/ArrayList;
    //   871: invokevirtual 930	java/util/ArrayList:clear	()V
    //   874: aload_0
    //   875: getfield 465	org/telegram/messenger/MediaController:shuffledPlaylist	Ljava/util/ArrayList;
    //   878: invokevirtual 930	java/util/ArrayList:clear	()V
    //   881: goto -300 -> 581
    //   884: astore_1
    //   885: ldc_w 547
    //   888: aload_1
    //   889: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   892: invokestatic 1705	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   895: astore_1
    //   896: getstatic 1718	org/telegram/messenger/NotificationCenter:audioPlayStateChanged	I
    //   899: istore_3
    //   900: aload_0
    //   901: getfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   904: ifnull +93 -> 997
    //   907: aload_0
    //   908: getfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   911: invokevirtual 1711	org/telegram/messenger/MessageObject:getId	()I
    //   914: istore_2
    //   915: aload_1
    //   916: iload_3
    //   917: iconst_1
    //   918: anewarray 4	java/lang/Object
    //   921: dup
    //   922: iconst_0
    //   923: iload_2
    //   924: invokestatic 1547	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   927: aastore
    //   928: invokevirtual 1715	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   931: aload_0
    //   932: getfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   935: ifnull +30 -> 965
    //   938: aload_0
    //   939: getfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   942: invokevirtual 1691	android/media/MediaPlayer:release	()V
    //   945: aload_0
    //   946: aconst_null
    //   947: putfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   950: aload_0
    //   951: iconst_0
    //   952: putfield 445	org/telegram/messenger/MediaController:isPaused	Z
    //   955: aload_0
    //   956: aconst_null
    //   957: putfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   960: aload_0
    //   961: iconst_0
    //   962: putfield 1998	org/telegram/messenger/MediaController:downloadingCurrentMessage	Z
    //   965: iconst_0
    //   966: ireturn
    //   967: iconst_3
    //   968: istore_2
    //   969: goto -162 -> 807
    //   972: aload_0
    //   973: aload 6
    //   975: invokestatic 2375	org/telegram/messenger/audioinfo/AudioInfo:getAudioInfo	(Ljava/io/File;)Lorg/telegram/messenger/audioinfo/AudioInfo;
    //   978: putfield 1987	org/telegram/messenger/MediaController:audioInfo	Lorg/telegram/messenger/audioinfo/AudioInfo;
    //   981: goto -400 -> 581
    //   984: astore 5
    //   986: ldc_w 547
    //   989: aload 5
    //   991: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   994: goto -413 -> 581
    //   997: iconst_0
    //   998: istore_2
    //   999: goto -84 -> 915
    //   1002: astore_1
    //   1003: aload_0
    //   1004: getfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1007: fconst_0
    //   1008: putfield 1697	org/telegram/messenger/MessageObject:audioProgress	F
    //   1011: aload_0
    //   1012: getfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1015: iconst_0
    //   1016: putfield 1700	org/telegram/messenger/MessageObject:audioProgressSec	I
    //   1019: invokestatic 1705	org/telegram/messenger/NotificationCenter:getInstance	()Lorg/telegram/messenger/NotificationCenter;
    //   1022: getstatic 1708	org/telegram/messenger/NotificationCenter:audioProgressDidChanged	I
    //   1025: iconst_2
    //   1026: anewarray 4	java/lang/Object
    //   1029: dup
    //   1030: iconst_0
    //   1031: aload_0
    //   1032: getfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1035: invokevirtual 1711	org/telegram/messenger/MessageObject:getId	()I
    //   1038: invokestatic 1547	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1041: aastore
    //   1042: dup
    //   1043: iconst_1
    //   1044: iconst_0
    //   1045: invokestatic 1547	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1048: aastore
    //   1049: invokevirtual 1715	org/telegram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   1052: ldc_w 547
    //   1055: aload_1
    //   1056: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1059: goto -364 -> 695
    //   1062: aload_0
    //   1063: getfield 449	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   1066: ifnull -371 -> 695
    //   1069: aload_0
    //   1070: getfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1073: getfield 1697	org/telegram/messenger/MessageObject:audioProgress	F
    //   1076: fconst_1
    //   1077: fcmpl
    //   1078: ifne +11 -> 1089
    //   1081: aload_0
    //   1082: getfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1085: fconst_0
    //   1086: putfield 1697	org/telegram/messenger/MessageObject:audioProgress	F
    //   1089: aload_0
    //   1090: getfield 623	org/telegram/messenger/MediaController:fileDecodingQueue	Lorg/telegram/messenger/DispatchQueue;
    //   1093: new 34	org/telegram/messenger/MediaController$16
    //   1096: dup
    //   1097: aload_0
    //   1098: invokespecial 2376	org/telegram/messenger/MediaController$16:<init>	(Lorg/telegram/messenger/MediaController;)V
    //   1101: invokevirtual 993	org/telegram/messenger/DispatchQueue:postRunnable	(Ljava/lang/Runnable;)V
    //   1104: goto -409 -> 695
    //   1107: new 1886	android/content/Intent
    //   1110: dup
    //   1111: getstatic 559	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   1114: ldc_w 2004
    //   1117: invokespecial 1891	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   1120: astore_1
    //   1121: getstatic 559	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   1124: aload_1
    //   1125: invokevirtual 2008	android/content/Context:stopService	(Landroid/content/Intent;)Z
    //   1128: pop
    //   1129: goto -402 -> 727
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1132	0	this	MediaController
    //   0	1132	1	paramMessageObject	MessageObject
    //   523	476	2	i	int
    //   899	18	3	j	int
    //   113	379	4	bool	boolean
    //   984	6	5	localException	Exception
    //   138	836	6	localObject2	Object
    //   448	30	7	localSemaphore	Semaphore
    //   454	29	8	arrayOfBoolean	Boolean[]
    // Exception table:
    //   from	to	target	type
    //   435	491	501	finally
    //   496	499	501	finally
    //   502	505	501	finally
    //   507	522	501	finally
    //   524	578	501	finally
    //   578	581	501	finally
    //   735	776	501	finally
    //   776	779	501	finally
    //   435	491	734	java/lang/Exception
    //   507	522	734	java/lang/Exception
    //   524	578	734	java/lang/Exception
    //   781	805	884	java/lang/Exception
    //   807	881	884	java/lang/Exception
    //   986	994	884	java/lang/Exception
    //   972	981	984	java/lang/Exception
    //   657	695	1002	java/lang/Exception
  }
  
  public void playMessageAtIndex(int paramInt)
  {
    if ((this.currentPlaylistNum < 0) || (this.currentPlaylistNum >= this.playlist.size())) {
      return;
    }
    this.currentPlaylistNum = paramInt;
    this.playMusicAgain = true;
    playAudio((MessageObject)this.playlist.get(this.currentPlaylistNum));
  }
  
  public void playNextMessage()
  {
    playNextMessage(false);
  }
  
  public void playPreviousMessage()
  {
    if (this.shuffleMusic) {}
    for (ArrayList localArrayList = this.shuffledPlaylist;; localArrayList = this.playlist)
    {
      this.currentPlaylistNum -= 1;
      if (this.currentPlaylistNum < 0) {
        this.currentPlaylistNum = (localArrayList.size() - 1);
      }
      if ((this.currentPlaylistNum >= 0) && (this.currentPlaylistNum < localArrayList.size())) {
        break;
      }
      return;
    }
    this.playMusicAgain = true;
    playAudio((MessageObject)localArrayList.get(this.currentPlaylistNum));
  }
  
  protected void processDownloadObjects(int paramInt, ArrayList<DownloadObject> paramArrayList)
  {
    if (paramArrayList.isEmpty()) {
      return;
    }
    ArrayList localArrayList = null;
    label22:
    label24:
    DownloadObject localDownloadObject;
    String str;
    if (paramInt == 1)
    {
      localArrayList = this.photoDownloadQueue;
      paramInt = 0;
      if (paramInt < paramArrayList.size())
      {
        localDownloadObject = (DownloadObject)paramArrayList.get(paramInt);
        if (!(localDownloadObject.object instanceof TLRPC.Document)) {
          break label158;
        }
        str = FileLoader.getAttachFileName((TLRPC.Document)localDownloadObject.object);
        label66:
        if (!this.downloadQueueKeys.containsKey(str)) {
          break label171;
        }
      }
    }
    label158:
    label171:
    label264:
    for (;;)
    {
      paramInt += 1;
      break label24;
      break;
      if (paramInt == 2)
      {
        localArrayList = this.audioDownloadQueue;
        break label22;
      }
      if (paramInt == 4)
      {
        localArrayList = this.videoDownloadQueue;
        break label22;
      }
      if (paramInt == 8)
      {
        localArrayList = this.documentDownloadQueue;
        break label22;
      }
      if (paramInt == 16)
      {
        localArrayList = this.musicDownloadQueue;
        break label22;
      }
      if (paramInt != 32) {
        break label22;
      }
      localArrayList = this.gifDownloadQueue;
      break label22;
      str = FileLoader.getAttachFileName(localDownloadObject.object);
      break label66;
      int i = 1;
      if ((localDownloadObject.object instanceof TLRPC.PhotoSize)) {
        FileLoader.getInstance().loadFile((TLRPC.PhotoSize)localDownloadObject.object, null, false);
      }
      for (;;)
      {
        if (i == 0) {
          break label264;
        }
        localArrayList.add(localDownloadObject);
        this.downloadQueueKeys.put(str, localDownloadObject);
        break;
        if ((localDownloadObject.object instanceof TLRPC.Document))
        {
          TLRPC.Document localDocument = (TLRPC.Document)localDownloadObject.object;
          FileLoader.getInstance().loadFile(localDocument, false, false);
        }
        else
        {
          i = 0;
        }
      }
    }
  }
  
  public void processMediaObserver(Uri paramUri)
  {
    final ArrayList localArrayList;
    label248:
    do
    {
      try
      {
        Point localPoint = AndroidUtilities.getRealScreenSize();
        paramUri = ApplicationLoader.applicationContext.getContentResolver().query(paramUri, this.mediaProjections, null, null, "date_added DESC LIMIT 1");
        localArrayList = new ArrayList();
        if (paramUri != null)
        {
          while (paramUri.moveToNext())
          {
            String str1 = paramUri.getString(0);
            Object localObject = paramUri.getString(1);
            String str2 = paramUri.getString(2);
            long l = paramUri.getLong(3);
            String str3 = paramUri.getString(4);
            int j = 0;
            int i = 0;
            if (Build.VERSION.SDK_INT >= 16)
            {
              j = paramUri.getInt(5);
              i = paramUri.getInt(6);
            }
            if (((str1 == null) || (!str1.toLowerCase().contains("screenshot"))) && ((localObject == null) || (!((String)localObject).toLowerCase().contains("screenshot"))) && ((str2 == null) || (!str2.toLowerCase().contains("screenshot"))))
            {
              if (str3 != null)
              {
                boolean bool = str3.toLowerCase().contains("screenshot");
                if (!bool) {}
              }
            }
            else
            {
              int k;
              if (j != 0)
              {
                k = i;
                if (i != 0) {
                  break label248;
                }
              }
              try
              {
                localObject = new BitmapFactory.Options();
                ((BitmapFactory.Options)localObject).inJustDecodeBounds = true;
                BitmapFactory.decodeFile(str1, (BitmapFactory.Options)localObject);
                j = ((BitmapFactory.Options)localObject).outWidth;
                k = ((BitmapFactory.Options)localObject).outHeight;
                if ((j <= 0) || (k <= 0) || ((j == localPoint.x) && (k == localPoint.y)) || ((k == localPoint.x) && (j == localPoint.y))) {
                  localArrayList.add(Long.valueOf(l));
                }
              }
              catch (Exception localException)
              {
                localArrayList.add(Long.valueOf(l));
              }
            }
          }
          paramUri.close();
        }
      }
      catch (Exception paramUri)
      {
        FileLog.e("tmessages", paramUri);
        return;
      }
    } while (localArrayList.isEmpty());
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.screenshotTook, new Object[0]);
        MediaController.this.checkScreenshots(localArrayList);
      }
    });
  }
  
  public void removeLoadingFileObserver(FileDownloadProgressListener paramFileDownloadProgressListener)
  {
    if (this.listenerInProgress) {
      this.deleteLaterArray.add(paramFileDownloadProgressListener);
    }
    String str;
    do
    {
      return;
      str = (String)this.observersByTag.get(Integer.valueOf(paramFileDownloadProgressListener.getObserverTag()));
    } while (str == null);
    ArrayList localArrayList = (ArrayList)this.loadingFileObservers.get(str);
    if (localArrayList != null)
    {
      int j;
      for (int i = 0; i < localArrayList.size(); i = j + 1)
      {
        WeakReference localWeakReference = (WeakReference)localArrayList.get(i);
        if (localWeakReference.get() != null)
        {
          j = i;
          if (localWeakReference.get() != paramFileDownloadProgressListener) {}
        }
        else
        {
          localArrayList.remove(i);
          j = i - 1;
        }
      }
      if (localArrayList.isEmpty()) {
        this.loadingFileObservers.remove(str);
      }
    }
    this.observersByTag.remove(Integer.valueOf(paramFileDownloadProgressListener.getObserverTag()));
  }
  
  public boolean resumeAudio(MessageObject paramMessageObject)
  {
    if (((this.audioTrackPlayer == null) && (this.audioPlayer == null)) || (paramMessageObject == null) || (this.playingMessageObject == null) || ((this.playingMessageObject != null) && (this.playingMessageObject.getId() != paramMessageObject.getId()))) {
      return false;
    }
    try
    {
      startProgressTimer(paramMessageObject);
      if (this.audioPlayer != null) {
        this.audioPlayer.start();
      }
      for (;;)
      {
        checkAudioFocus(paramMessageObject);
        this.isPaused = false;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioPlayStateChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()) });
        return true;
        if (this.audioTrackPlayer != null)
        {
          this.audioTrackPlayer.play();
          checkPlayerQueue();
        }
      }
      return false;
    }
    catch (Exception paramMessageObject)
    {
      FileLog.e("tmessages", paramMessageObject);
    }
  }
  
  public void scheduleVideoConvert(MessageObject paramMessageObject)
  {
    this.videoConvertQueue.add(paramMessageObject);
    if (this.videoConvertQueue.size() == 1) {
      startVideoConvertFromQueue();
    }
  }
  
  public boolean seekToProgress(MessageObject paramMessageObject, float paramFloat)
  {
    if (((this.audioTrackPlayer == null) && (this.audioPlayer == null)) || (paramMessageObject == null) || (this.playingMessageObject == null) || ((this.playingMessageObject != null) && (this.playingMessageObject.getId() != paramMessageObject.getId()))) {
      return false;
    }
    try
    {
      if (this.audioPlayer != null)
      {
        int i = (int)(this.audioPlayer.getDuration() * paramFloat);
        this.audioPlayer.seekTo(i);
        this.lastProgress = i;
      }
      else if (this.audioTrackPlayer != null)
      {
        seekOpusPlayer(paramFloat);
      }
    }
    catch (Exception paramMessageObject)
    {
      FileLog.e("tmessages", paramMessageObject);
      return false;
    }
    return true;
  }
  
  public void setAllowStartRecord(boolean paramBoolean)
  {
    this.allowStartRecord = paramBoolean;
  }
  
  public void setInputFieldHasText(boolean paramBoolean)
  {
    this.inputFieldHasText = paramBoolean;
  }
  
  public void setLastEncryptedChatParams(long paramLong1, long paramLong2, TLRPC.EncryptedChat paramEncryptedChat, ArrayList<Long> paramArrayList)
  {
    this.lastSecretChatEnterTime = paramLong1;
    this.lastSecretChatLeaveTime = paramLong2;
    this.lastSecretChat = paramEncryptedChat;
    this.lastSecretChatVisibleMessages = paramArrayList;
  }
  
  public boolean setPlaylist(ArrayList<MessageObject> paramArrayList, MessageObject paramMessageObject)
  {
    return setPlaylist(paramArrayList, paramMessageObject, true);
  }
  
  public boolean setPlaylist(ArrayList<MessageObject> paramArrayList, MessageObject paramMessageObject, boolean paramBoolean)
  {
    boolean bool2 = true;
    if (this.playingMessageObject == paramMessageObject) {
      return playAudio(paramMessageObject);
    }
    if (!paramBoolean)
    {
      bool1 = true;
      this.forceLoopCurrentPlaylist = bool1;
      if (this.playlist.isEmpty()) {
        break label114;
      }
    }
    label114:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.playMusicAgain = bool1;
      this.playlist.clear();
      int i = paramArrayList.size() - 1;
      while (i >= 0)
      {
        MessageObject localMessageObject = (MessageObject)paramArrayList.get(i);
        if (localMessageObject.isMusic()) {
          this.playlist.add(localMessageObject);
        }
        i -= 1;
      }
      bool1 = false;
      break;
    }
    this.currentPlaylistNum = this.playlist.indexOf(paramMessageObject);
    if (this.currentPlaylistNum == -1)
    {
      this.playlist.clear();
      this.shuffledPlaylist.clear();
      this.currentPlaylistNum = this.playlist.size();
      this.playlist.add(paramMessageObject);
    }
    if (paramMessageObject.isMusic())
    {
      if (this.shuffleMusic)
      {
        buildShuffledPlayList();
        this.currentPlaylistNum = 0;
      }
      if (paramBoolean) {
        SharedMediaQuery.loadMusic(paramMessageObject.getDialogId(), ((MessageObject)this.playlist.get(0)).getId());
      }
    }
    return playAudio(paramMessageObject);
  }
  
  public void setVoiceMessagesPlaylist(ArrayList<MessageObject> paramArrayList, boolean paramBoolean)
  {
    this.voiceMessagesPlaylist = paramArrayList;
    if (this.voiceMessagesPlaylist != null)
    {
      this.voiceMessagesPlaylistUnread = paramBoolean;
      this.voiceMessagesPlaylistMap = new HashMap();
      int i = 0;
      while (i < this.voiceMessagesPlaylist.size())
      {
        paramArrayList = (MessageObject)this.voiceMessagesPlaylist.get(i);
        this.voiceMessagesPlaylistMap.put(Integer.valueOf(paramArrayList.getId()), paramArrayList);
        i += 1;
      }
    }
  }
  
  public void startMediaObserver()
  {
    ApplicationLoader.applicationHandler.removeCallbacks(this.stopMediaObserverRunnable);
    this.startObserverToken += 1;
    try
    {
      if (this.internalObserver == null)
      {
        localContentResolver = ApplicationLoader.applicationContext.getContentResolver();
        localUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        localObject = new ExternalObserver();
        this.externalObserver = ((ExternalObserver)localObject);
        localContentResolver.registerContentObserver(localUri, false, (ContentObserver)localObject);
      }
    }
    catch (Exception localException1)
    {
      for (;;)
      {
        try
        {
          ContentResolver localContentResolver;
          Uri localUri;
          Object localObject;
          if (this.externalObserver == null)
          {
            localContentResolver = ApplicationLoader.applicationContext.getContentResolver();
            localUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
            localObject = new InternalObserver();
            this.internalObserver = ((InternalObserver)localObject);
            localContentResolver.registerContentObserver(localUri, false, (ContentObserver)localObject);
          }
          return;
        }
        catch (Exception localException2)
        {
          FileLog.e("tmessages", localException2);
        }
        localException1 = localException1;
        FileLog.e("tmessages", localException1);
      }
    }
  }
  
  public void startRaiseToEarSensors(ChatActivity paramChatActivity)
  {
    if ((paramChatActivity == null) || ((this.accelerometerSensor == null) && ((this.gravitySensor == null) || (this.linearAcceleration == null))) || (this.proximitySensor == null)) {}
    do
    {
      return;
      this.raiseChat = paramChatActivity;
    } while (((!this.raiseToSpeak) && ((this.playingMessageObject == null) || (!this.playingMessageObject.isVoice()))) || (this.sensorsStarted));
    paramChatActivity = this.gravity;
    float[] arrayOfFloat = this.gravity;
    this.gravity[2] = 0.0F;
    arrayOfFloat[1] = 0.0F;
    paramChatActivity[0] = 0.0F;
    paramChatActivity = this.linearAcceleration;
    arrayOfFloat = this.linearAcceleration;
    this.linearAcceleration[2] = 0.0F;
    arrayOfFloat[1] = 0.0F;
    paramChatActivity[0] = 0.0F;
    paramChatActivity = this.gravityFast;
    arrayOfFloat = this.gravityFast;
    this.gravityFast[2] = 0.0F;
    arrayOfFloat[1] = 0.0F;
    paramChatActivity[0] = 0.0F;
    this.lastTimestamp = 0L;
    this.previousAccValue = 0.0F;
    this.raisedToTop = 0;
    this.countLess = 0;
    this.raisedToBack = 0;
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (MediaController.this.gravitySensor != null) {
          MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.gravitySensor, 30000);
        }
        if (MediaController.this.linearSensor != null) {
          MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.linearSensor, 30000);
        }
        if (MediaController.this.accelerometerSensor != null) {
          MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.accelerometerSensor, 30000);
        }
        MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.proximitySensor, 3);
      }
    });
    this.sensorsStarted = true;
  }
  
  public void startRecording(final long paramLong, MessageObject paramMessageObject)
  {
    long l = 50L;
    int j = 0;
    int i = j;
    if (this.playingMessageObject != null)
    {
      i = j;
      if (isPlayingAudio(this.playingMessageObject))
      {
        i = j;
        if (!isAudioPaused())
        {
          i = 1;
          pauseAudio(this.playingMessageObject);
        }
      }
    }
    try
    {
      ((Vibrator)ApplicationLoader.applicationContext.getSystemService("vibrator")).vibrate(50L);
      DispatchQueue localDispatchQueue = this.recordQueue;
      paramMessageObject = new Runnable()
      {
        public void run()
        {
          if (MediaController.this.audioRecorder != null)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MediaController.access$1802(MediaController.this, null);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStartError, new Object[0]);
              }
            });
            return;
          }
          MediaController.access$1902(MediaController.this, new TLRPC.TL_document());
          MediaController.this.recordingAudio.dc_id = Integer.MIN_VALUE;
          MediaController.this.recordingAudio.id = UserConfig.lastLocalId;
          MediaController.this.recordingAudio.user_id = UserConfig.getClientUserId();
          MediaController.this.recordingAudio.mime_type = "audio/ogg";
          MediaController.this.recordingAudio.thumb = new TLRPC.TL_photoSizeEmpty();
          MediaController.this.recordingAudio.thumb.type = "s";
          UserConfig.lastLocalId -= 1;
          UserConfig.saveConfig(false);
          MediaController.access$5102(MediaController.this, new File(FileLoader.getInstance().getDirectory(4), FileLoader.getAttachFileName(MediaController.this.recordingAudio)));
          try
          {
            if (MediaController.this.startRecord(MediaController.this.recordingAudioFile.getAbsolutePath()) == 0)
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MediaController.access$1802(MediaController.this, null);
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStartError, new Object[0]);
                }
              });
              return;
            }
          }
          catch (Exception localException1)
          {
            FileLog.e("tmessages", localException1);
            MediaController.access$1902(MediaController.this, null);
            MediaController.this.stopRecord();
            MediaController.this.recordingAudioFile.delete();
            MediaController.access$5102(MediaController.this, null);
          }
          try
          {
            MediaController.this.audioRecorder.release();
            MediaController.access$002(MediaController.this, null);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MediaController.access$1802(MediaController.this, null);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStartError, new Object[0]);
              }
            });
            return;
            MediaController.access$002(MediaController.this, new AudioRecord(1, 16000, 16, 2, MediaController.this.recordBufferSize * 10));
            MediaController.access$1102(MediaController.this, System.currentTimeMillis());
            MediaController.access$702(MediaController.this, 0L);
            MediaController.access$302(MediaController.this, 0L);
            MediaController.access$5302(MediaController.this, paramLong);
            MediaController.access$5402(MediaController.this, this.val$reply_to_msg);
            MediaController.this.fileBuffer.rewind();
            MediaController.this.audioRecorder.startRecording();
            MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MediaController.access$1802(MediaController.this, null);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStarted, new Object[0]);
              }
            });
            return;
          }
          catch (Exception localException2)
          {
            for (;;)
            {
              FileLog.e("tmessages", localException2);
            }
          }
        }
      };
      this.recordStartRunnable = paramMessageObject;
      paramLong = l;
      if (i != 0) {
        paramLong = 500L;
      }
      localDispatchQueue.postRunnable(paramMessageObject, paramLong);
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public void startRecordingIfFromSpeaker()
  {
    if ((!this.useFrontSpeaker) || (this.raiseChat == null) || (!this.allowStartRecord)) {
      return;
    }
    this.raiseToEarRecord = true;
    startRecording(this.raiseChat.getDialogId(), null);
    this.ignoreOnPause = true;
  }
  
  /* Error */
  public void stopAudio()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 449	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   4: ifnonnull +10 -> 14
    //   7: aload_0
    //   8: getfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   11: ifnull +10 -> 21
    //   14: aload_0
    //   15: getfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   18: ifnonnull +4 -> 22
    //   21: return
    //   22: aload_0
    //   23: getfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   26: astore_1
    //   27: aload_1
    //   28: ifnull +100 -> 128
    //   31: aload_0
    //   32: getfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   35: invokevirtual 1689	android/media/MediaPlayer:reset	()V
    //   38: aload_0
    //   39: getfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   42: invokevirtual 1690	android/media/MediaPlayer:stop	()V
    //   45: aload_0
    //   46: getfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   49: ifnull +103 -> 152
    //   52: aload_0
    //   53: getfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   56: invokevirtual 1691	android/media/MediaPlayer:release	()V
    //   59: aload_0
    //   60: aconst_null
    //   61: putfield 447	org/telegram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   64: aload_0
    //   65: invokespecial 1694	org/telegram/messenger/MediaController:stopProgressTimer	()V
    //   68: aload_0
    //   69: aconst_null
    //   70: putfield 867	org/telegram/messenger/MediaController:playingMessageObject	Lorg/telegram/messenger/MessageObject;
    //   73: aload_0
    //   74: iconst_0
    //   75: putfield 1998	org/telegram/messenger/MediaController:downloadingCurrentMessage	Z
    //   78: aload_0
    //   79: iconst_0
    //   80: putfield 445	org/telegram/messenger/MediaController:isPaused	Z
    //   83: new 1886	android/content/Intent
    //   86: dup
    //   87: getstatic 559	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   90: ldc_w 2004
    //   93: invokespecial 1891	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   96: astore_1
    //   97: getstatic 559	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   100: aload_1
    //   101: invokevirtual 2008	android/content/Context:stopService	(Landroid/content/Intent;)Z
    //   104: pop
    //   105: return
    //   106: astore_1
    //   107: ldc_w 547
    //   110: aload_1
    //   111: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   114: goto -76 -> 38
    //   117: astore_1
    //   118: ldc_w 547
    //   121: aload_1
    //   122: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   125: goto -80 -> 45
    //   128: aload_0
    //   129: getfield 449	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   132: ifnull -87 -> 45
    //   135: aload_0
    //   136: getfield 449	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   139: invokevirtual 1721	android/media/AudioTrack:pause	()V
    //   142: aload_0
    //   143: getfield 449	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   146: invokevirtual 1724	android/media/AudioTrack:flush	()V
    //   149: goto -104 -> 45
    //   152: aload_0
    //   153: getfield 449	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   156: ifnull -92 -> 64
    //   159: aload_0
    //   160: getfield 479	org/telegram/messenger/MediaController:playerObjectSync	Ljava/lang/Object;
    //   163: astore_1
    //   164: aload_1
    //   165: monitorenter
    //   166: aload_0
    //   167: getfield 449	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   170: invokevirtual 1725	android/media/AudioTrack:release	()V
    //   173: aload_0
    //   174: aconst_null
    //   175: putfield 449	org/telegram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   178: aload_1
    //   179: monitorexit
    //   180: goto -116 -> 64
    //   183: astore_2
    //   184: aload_1
    //   185: monitorexit
    //   186: aload_2
    //   187: athrow
    //   188: astore_1
    //   189: ldc_w 547
    //   192: aload_1
    //   193: invokestatic 553	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   196: goto -132 -> 64
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	199	0	this	MediaController
    //   26	75	1	localObject1	Object
    //   106	5	1	localException1	Exception
    //   117	5	1	localException2	Exception
    //   188	5	1	localException3	Exception
    //   183	4	2	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   31	38	106	java/lang/Exception
    //   22	27	117	java/lang/Exception
    //   38	45	117	java/lang/Exception
    //   107	114	117	java/lang/Exception
    //   128	149	117	java/lang/Exception
    //   166	180	183	finally
    //   184	186	183	finally
    //   45	64	188	java/lang/Exception
    //   152	166	188	java/lang/Exception
    //   186	188	188	java/lang/Exception
  }
  
  public void stopMediaObserver()
  {
    if (this.stopMediaObserverRunnable == null) {
      this.stopMediaObserverRunnable = new StopMediaObserverRunnable(null);
    }
    this.stopMediaObserverRunnable.currentObserverToken = this.startObserverToken;
    ApplicationLoader.applicationHandler.postDelayed(this.stopMediaObserverRunnable, 5000L);
  }
  
  public void stopRaiseToEarSensors(ChatActivity paramChatActivity)
  {
    if (this.ignoreOnPause) {
      this.ignoreOnPause = false;
    }
    do
    {
      do
      {
        return;
      } while ((!this.sensorsStarted) || (this.ignoreOnPause) || ((this.accelerometerSensor == null) && ((this.gravitySensor == null) || (this.linearAcceleration == null))) || (this.proximitySensor == null) || (this.raiseChat != paramChatActivity));
      this.raiseChat = null;
      stopRecording(0);
      this.sensorsStarted = false;
      this.accelerometerVertical = false;
      this.proximityTouched = false;
      this.raiseToEarRecord = false;
      this.useFrontSpeaker = false;
      Utilities.globalQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          if (MediaController.this.linearSensor != null) {
            MediaController.this.sensorManager.unregisterListener(MediaController.this, MediaController.this.linearSensor);
          }
          if (MediaController.this.gravitySensor != null) {
            MediaController.this.sensorManager.unregisterListener(MediaController.this, MediaController.this.gravitySensor);
          }
          if (MediaController.this.accelerometerSensor != null) {
            MediaController.this.sensorManager.unregisterListener(MediaController.this, MediaController.this.accelerometerSensor);
          }
          MediaController.this.sensorManager.unregisterListener(MediaController.this, MediaController.this.proximitySensor);
        }
      });
    } while ((!this.proximityHasDifferentValues) || (this.proximityWakeLock == null) || (!this.proximityWakeLock.isHeld()));
    this.proximityWakeLock.release();
  }
  
  public void stopRecording(final int paramInt)
  {
    if (this.recordStartRunnable != null)
    {
      this.recordQueue.cancelRunnable(this.recordStartRunnable);
      this.recordStartRunnable = null;
    }
    this.recordQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (MediaController.this.audioRecorder == null) {
          return;
        }
        try
        {
          MediaController.access$1202(MediaController.this, paramInt);
          MediaController.this.audioRecorder.stop();
          if (paramInt == 0) {
            MediaController.this.stopRecordingInternal(0);
          }
        }
        catch (Exception localException1)
        {
          try
          {
            do
            {
              ((Vibrator)ApplicationLoader.applicationContext.getSystemService("vibrator")).vibrate(50L);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStopped, new Object[0]);
                }
              });
              return;
              localException1 = localException1;
              FileLog.e("tmessages", localException1);
            } while (MediaController.this.recordingAudioFile == null);
            MediaController.this.recordingAudioFile.delete();
          }
          catch (Exception localException2)
          {
            for (;;)
            {
              FileLog.e("tmessages", localException2);
            }
          }
        }
      }
    });
  }
  
  public void toggleAutoplayGifs()
  {
    if (!this.autoplayGifs) {}
    for (boolean bool = true;; bool = false)
    {
      this.autoplayGifs = bool;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      localEditor.putBoolean("autoplay_gif", this.autoplayGifs);
      localEditor.commit();
      return;
    }
  }
  
  public void toggleCustomTabs()
  {
    if (!this.customTabs) {}
    for (boolean bool = true;; bool = false)
    {
      this.customTabs = bool;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      localEditor.putBoolean("custom_tabs", this.customTabs);
      localEditor.commit();
      return;
    }
  }
  
  public void toggleDirectShare()
  {
    if (!this.directShare) {}
    for (boolean bool = true;; bool = false)
    {
      this.directShare = bool;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      localEditor.putBoolean("direct_share", this.directShare);
      localEditor.commit();
      return;
    }
  }
  
  public void toggleRepeatMode()
  {
    this.repeatMode += 1;
    if (this.repeatMode > 2) {
      this.repeatMode = 0;
    }
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
    localEditor.putInt("repeatMode", this.repeatMode);
    localEditor.commit();
  }
  
  public void toggleSaveToGallery()
  {
    if (!this.saveToGallery) {}
    for (boolean bool = true;; bool = false)
    {
      this.saveToGallery = bool;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      localEditor.putBoolean("save_gallery", this.saveToGallery);
      localEditor.commit();
      checkSaveToGalleryFiles();
      return;
    }
  }
  
  public void toggleShuffleMusic()
  {
    boolean bool;
    if (!this.shuffleMusic)
    {
      bool = true;
      this.shuffleMusic = bool;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      localEditor.putBoolean("shuffleMusic", this.shuffleMusic);
      localEditor.commit();
      if (!this.shuffleMusic) {
        break label73;
      }
      buildShuffledPlayList();
      this.currentPlaylistNum = 0;
    }
    label73:
    do
    {
      do
      {
        return;
        bool = false;
        break;
      } while (this.playingMessageObject == null);
      this.currentPlaylistNum = this.playlist.indexOf(this.playingMessageObject);
    } while (this.currentPlaylistNum != -1);
    this.playlist.clear();
    this.shuffledPlaylist.clear();
    cleanupPlayer(true, true);
  }
  
  public void toogleRaiseToSpeak()
  {
    if (!this.raiseToSpeak) {}
    for (boolean bool = true;; bool = false)
    {
      this.raiseToSpeak = bool;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      localEditor.putBoolean("raise_to_speak", this.raiseToSpeak);
      localEditor.commit();
      return;
    }
  }
  
  public static class AlbumEntry
  {
    public int bucketId;
    public String bucketName;
    public MediaController.PhotoEntry coverPhoto;
    public boolean isVideo;
    public ArrayList<MediaController.PhotoEntry> photos = new ArrayList();
    public HashMap<Integer, MediaController.PhotoEntry> photosByIds = new HashMap();
    
    public AlbumEntry(int paramInt, String paramString, MediaController.PhotoEntry paramPhotoEntry, boolean paramBoolean)
    {
      this.bucketId = paramInt;
      this.bucketName = paramString;
      this.coverPhoto = paramPhotoEntry;
      this.isVideo = paramBoolean;
    }
    
    public void addPhoto(MediaController.PhotoEntry paramPhotoEntry)
    {
      this.photos.add(paramPhotoEntry);
      this.photosByIds.put(Integer.valueOf(paramPhotoEntry.imageId), paramPhotoEntry);
    }
  }
  
  private class AudioBuffer
  {
    ByteBuffer buffer;
    byte[] bufferBytes;
    int finished;
    long pcmOffset;
    int size;
    
    public AudioBuffer(int paramInt)
    {
      this.buffer = ByteBuffer.allocateDirect(paramInt);
      this.bufferBytes = new byte[paramInt];
    }
  }
  
  public static class AudioEntry
  {
    public String author;
    public int duration;
    public String genre;
    public long id;
    public MessageObject messageObject;
    public String path;
    public String title;
  }
  
  private class ExternalObserver
    extends ContentObserver
  {
    public ExternalObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      MediaController.this.processMediaObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }
  }
  
  public static abstract interface FileDownloadProgressListener
  {
    public abstract int getObserverTag();
    
    public abstract void onFailedDownload(String paramString);
    
    public abstract void onProgressDownload(String paramString, float paramFloat);
    
    public abstract void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean);
    
    public abstract void onSuccessDownload(String paramString);
  }
  
  private class GalleryObserverExternal
    extends ContentObserver
  {
    public GalleryObserverExternal()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      if (MediaController.this.refreshGalleryRunnable != null) {
        AndroidUtilities.cancelRunOnUIThread(MediaController.this.refreshGalleryRunnable);
      }
      AndroidUtilities.runOnUIThread(MediaController.access$1402(MediaController.this, new Runnable()
      {
        public void run()
        {
          MediaController.access$1402(MediaController.this, null);
          MediaController.loadGalleryPhotosAlbums(0);
        }
      }), 2000L);
    }
  }
  
  private class GalleryObserverInternal
    extends ContentObserver
  {
    public GalleryObserverInternal()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      if (MediaController.this.refreshGalleryRunnable != null) {
        AndroidUtilities.cancelRunOnUIThread(MediaController.this.refreshGalleryRunnable);
      }
      AndroidUtilities.runOnUIThread(MediaController.access$1402(MediaController.this, new Runnable()
      {
        public void run()
        {
          MediaController.access$1402(MediaController.this, null);
          MediaController.loadGalleryPhotosAlbums(0);
        }
      }), 2000L);
    }
  }
  
  private class InternalObserver
    extends ContentObserver
  {
    public InternalObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      MediaController.this.processMediaObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
    }
  }
  
  public static class PhotoEntry
  {
    public int bucketId;
    public CharSequence caption;
    public long dateTaken;
    public int imageId;
    public String imagePath;
    public boolean isVideo;
    public int orientation;
    public String path;
    public String thumbPath;
    
    public PhotoEntry(int paramInt1, int paramInt2, long paramLong, String paramString, int paramInt3, boolean paramBoolean)
    {
      this.bucketId = paramInt1;
      this.imageId = paramInt2;
      this.dateTaken = paramLong;
      this.path = paramString;
      this.orientation = paramInt3;
      this.isVideo = paramBoolean;
    }
  }
  
  public static class SearchImage
  {
    public CharSequence caption;
    public int date;
    public TLRPC.Document document;
    public int height;
    public String id;
    public String imagePath;
    public String imageUrl;
    public String localUrl;
    public int size;
    public String thumbPath;
    public String thumbUrl;
    public int type;
    public int width;
  }
  
  private final class StopMediaObserverRunnable
    implements Runnable
  {
    public int currentObserverToken = 0;
    
    private StopMediaObserverRunnable() {}
    
    public void run()
    {
      if (this.currentObserverToken == MediaController.this.startObserverToken) {}
      try
      {
        if (MediaController.this.internalObserver != null)
        {
          ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.internalObserver);
          MediaController.access$1602(MediaController.this, null);
        }
      }
      catch (Exception localException1)
      {
        for (;;)
        {
          try
          {
            if (MediaController.this.externalObserver != null)
            {
              ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.externalObserver);
              MediaController.access$1702(MediaController.this, null);
            }
            return;
          }
          catch (Exception localException2)
          {
            FileLog.e("tmessages", localException2);
          }
          localException1 = localException1;
          FileLog.e("tmessages", localException1);
        }
      }
    }
  }
  
  private static class VideoConvertRunnable
    implements Runnable
  {
    private MessageObject messageObject;
    
    private VideoConvertRunnable(MessageObject paramMessageObject)
    {
      this.messageObject = paramMessageObject;
    }
    
    public static void runConversion(MessageObject paramMessageObject)
    {
      new Thread(new Runnable()
      {
        public void run()
        {
          try
          {
            Thread localThread = new Thread(new MediaController.VideoConvertRunnable(this.val$obj, null), "VideoConvertRunnable");
            localThread.start();
            localThread.join();
            return;
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
          }
        }
      }).start();
    }
    
    public void run()
    {
      MediaController.getInstance().convertVideo(this.messageObject);
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\MediaController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */