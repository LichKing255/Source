package org.telegram.messenger;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Base64;
import com.google.android.gms.common.GooglePlayServicesUtil;
import java.io.File;
import java.io.RandomAccessFile;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Components.ForegroundDetector;

public class ApplicationLoader
  extends Application
{
  public static volatile Context applicationContext;
  public static volatile Handler applicationHandler;
  private static volatile boolean applicationInited = false;
  private static Drawable cachedWallpaper;
  private static boolean isCustomTheme;
  public static volatile boolean isScreenOn = false;
  public static volatile boolean mainInterfacePaused = true;
  private static int selectedColor;
  private static int serviceMessageColor;
  private static int serviceSelectedMessageColor;
  private static final Object sync = new Object();
  
  private static void calcBackgroundColor()
  {
    int[] arrayOfInt = AndroidUtilities.calcDrawableColor(cachedWallpaper);
    serviceMessageColor = arrayOfInt[0];
    serviceSelectedMessageColor = arrayOfInt[1];
    applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("serviceMessageColor", serviceMessageColor).putInt("serviceSelectedMessageColor", serviceSelectedMessageColor).commit();
  }
  
  private boolean checkPlayServices()
  {
    try
    {
      int i = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
      return i == 0;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return true;
  }
  
  private static void convertConfig()
  {
    SharedPreferences localSharedPreferences = applicationContext.getSharedPreferences("dataconfig", 0);
    SerializedData localSerializedData;
    boolean bool;
    if (localSharedPreferences.contains("currentDatacenterId"))
    {
      localSerializedData = new SerializedData(32768);
      localSerializedData.writeInt32(2);
      if (localSharedPreferences.getInt("datacenterSetId", 0) == 0) {
        break label251;
      }
      bool = true;
    }
    for (;;)
    {
      localSerializedData.writeBool(bool);
      localSerializedData.writeBool(true);
      localSerializedData.writeInt32(localSharedPreferences.getInt("currentDatacenterId", 0));
      localSerializedData.writeInt32(localSharedPreferences.getInt("timeDifference", 0));
      localSerializedData.writeInt32(localSharedPreferences.getInt("lastDcUpdateTime", 0));
      localSerializedData.writeInt64(localSharedPreferences.getLong("pushSessionId", 0L));
      localSerializedData.writeBool(false);
      localSerializedData.writeInt32(0);
      try
      {
        localObject1 = localSharedPreferences.getString("datacenters", null);
        if (localObject1 != null)
        {
          localObject1 = Base64.decode((String)localObject1, 0);
          if (localObject1 != null)
          {
            localObject2 = new SerializedData((byte[])localObject1);
            localSerializedData.writeInt32(((SerializedData)localObject2).readInt32(false));
            localSerializedData.writeBytes((byte[])localObject1, 4, localObject1.length - 4);
            ((SerializedData)localObject2).cleanup();
          }
        }
      }
      catch (Exception localException1)
      {
        try
        {
          for (;;)
          {
            Object localObject1 = new RandomAccessFile(new File(getFilesDirFixed(), "tgnet.dat"), "rws");
            Object localObject2 = localSerializedData.toByteArray();
            ((RandomAccessFile)localObject1).writeInt(Integer.reverseBytes(localObject2.length));
            ((RandomAccessFile)localObject1).write((byte[])localObject2);
            ((RandomAccessFile)localObject1).close();
            localSerializedData.cleanup();
            localSharedPreferences.edit().clear().commit();
            return;
            label251:
            bool = false;
            break;
            localException1 = localException1;
            FileLog.e("tmessages", localException1);
          }
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
  }
  
  public static Drawable getCachedWallpaper()
  {
    synchronized (sync)
    {
      Drawable localDrawable = cachedWallpaper;
      return localDrawable;
    }
  }
  
  public static File getFilesDirFixed()
  {
    int i = 0;
    File localFile;
    while (i < 10)
    {
      localFile = applicationContext.getFilesDir();
      if (localFile != null) {
        return localFile;
      }
      i += 1;
    }
    try
    {
      localFile = new File(applicationContext.getApplicationInfo().dataDir, "files");
      localFile.mkdirs();
      return localFile;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return new File("/data/data/org.telegram.messenger/files");
  }
  
  public static int getSelectedColor()
  {
    return selectedColor;
  }
  
  public static int getServiceMessageColor()
  {
    return serviceMessageColor;
  }
  
  public static int getServiceSelectedMessageColor()
  {
    return serviceSelectedMessageColor;
  }
  
  private void initPlayServices()
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (ApplicationLoader.this.checkPlayServices())
        {
          if ((UserConfig.pushString != null) && (UserConfig.pushString.length() != 0)) {
            FileLog.d("tmessages", "GCM regId = " + UserConfig.pushString);
          }
          for (;;)
          {
            Intent localIntent = new Intent(ApplicationLoader.applicationContext, GcmRegistrationIntentService.class);
            ApplicationLoader.this.startService(localIntent);
            return;
            FileLog.d("tmessages", "GCM Registration not found.");
          }
        }
        FileLog.d("tmessages", "No valid Google Play Services APK found.");
      }
    }, 1000L);
  }
  
  public static boolean isCustomTheme()
  {
    return isCustomTheme;
  }
  
  public static void loadWallpaper()
  {
    if (cachedWallpaper != null) {
      return;
    }
    Utilities.searchQueue.postRunnable(new Runnable()
    {
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: invokestatic 22	org/telegram/messenger/ApplicationLoader:access$000	()Ljava/lang/Object;
        //   3: astore 4
        //   5: aload 4
        //   7: monitorenter
        //   8: iconst_0
        //   9: istore_2
        //   10: iload_2
        //   11: istore_1
        //   12: getstatic 26	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   15: ldc 28
        //   17: iconst_0
        //   18: invokevirtual 34	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
        //   21: astore 5
        //   23: iload_2
        //   24: istore_1
        //   25: aload 5
        //   27: ldc 36
        //   29: ldc 37
        //   31: invokeinterface 43 3 0
        //   36: istore_3
        //   37: iload_2
        //   38: istore_1
        //   39: aload 5
        //   41: ldc 45
        //   43: iconst_0
        //   44: invokeinterface 43 3 0
        //   49: istore_2
        //   50: iload_2
        //   51: istore_1
        //   52: aload 5
        //   54: ldc 47
        //   56: iconst_0
        //   57: invokeinterface 43 3 0
        //   62: invokestatic 51	org/telegram/messenger/ApplicationLoader:access$102	(I)I
        //   65: pop
        //   66: iload_2
        //   67: istore_1
        //   68: aload 5
        //   70: ldc 53
        //   72: iconst_0
        //   73: invokeinterface 43 3 0
        //   78: invokestatic 56	org/telegram/messenger/ApplicationLoader:access$202	(I)I
        //   81: pop
        //   82: iload_2
        //   83: istore_1
        //   84: iload_2
        //   85: ifne +35 -> 120
        //   88: iload_3
        //   89: ldc 37
        //   91: if_icmpne +69 -> 160
        //   94: iload_2
        //   95: istore_1
        //   96: getstatic 26	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   99: invokevirtual 60	android/content/Context:getResources	()Landroid/content/res/Resources;
        //   102: ldc 61
        //   104: invokevirtual 67	android/content/res/Resources:getDrawable	(I)Landroid/graphics/drawable/Drawable;
        //   107: invokestatic 71	org/telegram/messenger/ApplicationLoader:access$302	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
        //   110: pop
        //   111: iload_2
        //   112: istore_1
        //   113: iconst_0
        //   114: invokestatic 75	org/telegram/messenger/ApplicationLoader:access$402	(Z)Z
        //   117: pop
        //   118: iload_2
        //   119: istore_1
        //   120: invokestatic 79	org/telegram/messenger/ApplicationLoader:access$300	()Landroid/graphics/drawable/Drawable;
        //   123: ifnonnull +24 -> 147
        //   126: iload_1
        //   127: istore_2
        //   128: iload_1
        //   129: ifne +6 -> 135
        //   132: ldc 80
        //   134: istore_2
        //   135: new 82	android/graphics/drawable/ColorDrawable
        //   138: dup
        //   139: iload_2
        //   140: invokespecial 85	android/graphics/drawable/ColorDrawable:<init>	(I)V
        //   143: invokestatic 71	org/telegram/messenger/ApplicationLoader:access$302	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
        //   146: pop
        //   147: invokestatic 89	org/telegram/messenger/ApplicationLoader:access$100	()I
        //   150: ifne +6 -> 156
        //   153: invokestatic 92	org/telegram/messenger/ApplicationLoader:access$500	()V
        //   156: aload 4
        //   158: monitorexit
        //   159: return
        //   160: iload_2
        //   161: istore_1
        //   162: new 94	java/io/File
        //   165: dup
        //   166: invokestatic 98	org/telegram/messenger/ApplicationLoader:getFilesDirFixed	()Ljava/io/File;
        //   169: ldc 100
        //   171: invokespecial 103	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
        //   174: astore 5
        //   176: iload_2
        //   177: istore_1
        //   178: aload 5
        //   180: invokevirtual 107	java/io/File:exists	()Z
        //   183: ifeq +29 -> 212
        //   186: iload_2
        //   187: istore_1
        //   188: aload 5
        //   190: invokevirtual 111	java/io/File:getAbsolutePath	()Ljava/lang/String;
        //   193: invokestatic 117	android/graphics/drawable/Drawable:createFromPath	(Ljava/lang/String;)Landroid/graphics/drawable/Drawable;
        //   196: invokestatic 71	org/telegram/messenger/ApplicationLoader:access$302	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
        //   199: pop
        //   200: iload_2
        //   201: istore_1
        //   202: iconst_1
        //   203: invokestatic 75	org/telegram/messenger/ApplicationLoader:access$402	(Z)Z
        //   206: pop
        //   207: iload_2
        //   208: istore_1
        //   209: goto -89 -> 120
        //   212: iload_2
        //   213: istore_1
        //   214: getstatic 26	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   217: invokevirtual 60	android/content/Context:getResources	()Landroid/content/res/Resources;
        //   220: ldc 61
        //   222: invokevirtual 67	android/content/res/Resources:getDrawable	(I)Landroid/graphics/drawable/Drawable;
        //   225: invokestatic 71	org/telegram/messenger/ApplicationLoader:access$302	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
        //   228: pop
        //   229: iload_2
        //   230: istore_1
        //   231: iconst_0
        //   232: invokestatic 75	org/telegram/messenger/ApplicationLoader:access$402	(Z)Z
        //   235: pop
        //   236: iload_2
        //   237: istore_1
        //   238: goto -118 -> 120
        //   241: astore 5
        //   243: aload 4
        //   245: monitorexit
        //   246: aload 5
        //   248: athrow
        //   249: astore 5
        //   251: goto -131 -> 120
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	254	0	this	1
        //   11	227	1	i	int
        //   9	228	2	j	int
        //   36	56	3	k	int
        //   3	241	4	localObject1	Object
        //   21	168	5	localObject2	Object
        //   241	6	5	localObject3	Object
        //   249	1	5	localThrowable	Throwable
        // Exception table:
        //   from	to	target	type
        //   12	23	241	finally
        //   25	37	241	finally
        //   39	50	241	finally
        //   52	66	241	finally
        //   68	82	241	finally
        //   96	111	241	finally
        //   113	118	241	finally
        //   120	126	241	finally
        //   135	147	241	finally
        //   147	156	241	finally
        //   156	159	241	finally
        //   162	176	241	finally
        //   178	186	241	finally
        //   188	200	241	finally
        //   202	207	241	finally
        //   214	229	241	finally
        //   231	236	241	finally
        //   243	246	241	finally
        //   12	23	249	java/lang/Throwable
        //   25	37	249	java/lang/Throwable
        //   39	50	249	java/lang/Throwable
        //   52	66	249	java/lang/Throwable
        //   68	82	249	java/lang/Throwable
        //   96	111	249	java/lang/Throwable
        //   113	118	249	java/lang/Throwable
        //   162	176	249	java/lang/Throwable
        //   178	186	249	java/lang/Throwable
        //   188	200	249	java/lang/Throwable
        //   202	207	249	java/lang/Throwable
        //   214	229	249	java/lang/Throwable
        //   231	236	249	java/lang/Throwable
      }
    });
  }
  
  public static void postInitApplication()
  {
    if (applicationInited) {
      return;
    }
    applicationInited = true;
    convertConfig();
    try
    {
      LocaleController.getInstance();
    }
    catch (Exception localException3)
    {
      try
      {
        localObject1 = new IntentFilter("android.intent.action.SCREEN_ON");
        ((IntentFilter)localObject1).addAction("android.intent.action.SCREEN_OFF");
        localObject2 = new ScreenReceiver();
        applicationContext.registerReceiver((BroadcastReceiver)localObject2, (IntentFilter)localObject1);
      }
      catch (Exception localException3)
      {
        try
        {
          isScreenOn = ((PowerManager)applicationContext.getSystemService("power")).isScreenOn();
          FileLog.e("tmessages", "screen state = " + isScreenOn);
          UserConfig.loadConfig();
          str2 = getFilesDirFixed().toString();
        }
        catch (Exception localException3)
        {
          try
          {
            for (;;)
            {
              String str2;
              localObject4 = LocaleController.getLocaleStringIso639();
              localObject3 = Build.MANUFACTURER + Build.MODEL;
              Object localObject1 = applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0);
              localObject2 = ((PackageInfo)localObject1).versionName + " (" + ((PackageInfo)localObject1).versionCode + ")";
              localObject1 = "SDK " + Build.VERSION.SDK_INT;
              Object localObject5 = localObject4;
              if (((String)localObject4).trim().length() == 0) {
                localObject5 = "en";
              }
              localObject4 = localObject3;
              if (((String)localObject3).trim().length() == 0) {
                localObject4 = "Android unknown";
              }
              localObject3 = localObject2;
              if (((String)localObject2).trim().length() == 0) {
                localObject3 = "App version unknown";
              }
              localObject2 = localObject1;
              if (((String)localObject1).trim().length() == 0) {
                localObject2 = "SDK Unknown";
              }
              boolean bool = applicationContext.getSharedPreferences("Notifications", 0).getBoolean("pushConnection", true);
              MessagesController.getInstance();
              ConnectionsManager.getInstance().init(BuildVars.BUILD_VERSION, 53, BuildVars.APP_ID, (String)localObject4, (String)localObject2, (String)localObject3, (String)localObject5, str2, FileLog.getNetworkLogPath(), UserConfig.getClientUserId(), bool);
              if (UserConfig.getCurrentUser() != null)
              {
                MessagesController.getInstance().putUser(UserConfig.getCurrentUser(), true);
                ConnectionsManager.getInstance().applyCountryPortNumber(UserConfig.getCurrentUser().phone);
                MessagesController.getInstance().getBlockedUsers(true);
                SendMessagesHelper.getInstance().checkUnsentMessages();
              }
              ((ApplicationLoader)applicationContext).initPlayServices();
              FileLog.e("tmessages", "app initied");
              ContactsController.getInstance().checkAppAccount();
              MediaController.getInstance();
              return;
              localException1 = localException1;
              localException1.printStackTrace();
              continue;
              localException2 = localException2;
              localException2.printStackTrace();
            }
            localException3 = localException3;
            FileLog.e("tmessages", localException3);
          }
          catch (Exception localException4)
          {
            for (;;)
            {
              Object localObject4 = "en";
              Object localObject3 = "Android unknown";
              Object localObject2 = "App version unknown";
              String str1 = "SDK " + Build.VERSION.SDK_INT;
            }
          }
        }
      }
    }
  }
  
  public static void reloadWallpaper()
  {
    cachedWallpaper = null;
    serviceMessageColor = 0;
    applicationContext.getSharedPreferences("mainconfig", 0).edit().remove("serviceMessageColor").commit();
    loadWallpaper();
  }
  
  public static void startPushService()
  {
    if (applicationContext.getSharedPreferences("Notifications", 0).getBoolean("pushService", true))
    {
      applicationContext.startService(new Intent(applicationContext, NotificationsService.class));
      return;
    }
    stopPushService();
  }
  
  public static void stopPushService()
  {
    applicationContext.stopService(new Intent(applicationContext, NotificationsService.class));
    PendingIntent localPendingIntent = PendingIntent.getService(applicationContext, 0, new Intent(applicationContext, NotificationsService.class), 0);
    ((AlarmManager)applicationContext.getSystemService("alarm")).cancel(localPendingIntent);
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    try
    {
      LocaleController.getInstance().onDeviceConfigurationChange(paramConfiguration);
      AndroidUtilities.checkDisplaySize();
      return;
    }
    catch (Exception paramConfiguration)
    {
      paramConfiguration.printStackTrace();
    }
  }
  
  public void onCreate()
  {
    super.onCreate();
    applicationContext = getApplicationContext();
    NativeLoader.initNativeLibs(applicationContext);
    if ((Build.VERSION.SDK_INT == 14) || (Build.VERSION.SDK_INT == 15)) {}
    for (boolean bool = true;; bool = false)
    {
      ConnectionsManager.native_setJava(bool);
      new ForegroundDetector(this);
      applicationHandler = new Handler(applicationContext.getMainLooper());
      startPushService();
      return;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\ApplicationLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */