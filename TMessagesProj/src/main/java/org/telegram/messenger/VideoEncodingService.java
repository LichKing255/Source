package org.telegram.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;

public class VideoEncodingService
  extends Service
  implements NotificationCenter.NotificationCenterDelegate
{
  private NotificationCompat.Builder builder = null;
  private int currentProgress = 0;
  private String path = null;
  
  public VideoEncodingService()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileUploadProgressChanged);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.stopEncodingService);
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    boolean bool = true;
    if (paramInt == NotificationCenter.FileUploadProgressChanged)
    {
      Object localObject = (String)paramVarArgs[0];
      if ((this.path != null) && (this.path.equals(localObject)))
      {
        localObject = (Float)paramVarArgs[1];
        paramVarArgs = (Boolean)paramVarArgs[2];
        this.currentProgress = ((int)(((Float)localObject).floatValue() * 100.0F));
        paramVarArgs = this.builder;
        paramInt = this.currentProgress;
        if (this.currentProgress != 0) {
          break label108;
        }
        paramVarArgs.setProgress(100, paramInt, bool);
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
      }
    }
    label108:
    do
    {
      do
      {
        return;
        bool = false;
        break;
      } while (paramInt != NotificationCenter.stopEncodingService);
      paramVarArgs = (String)paramVarArgs[0];
    } while ((paramVarArgs != null) && (!paramVarArgs.equals(this.path)));
    stopSelf();
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return null;
  }
  
  public void onDestroy()
  {
    stopForeground(true);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileUploadProgressChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stopEncodingService);
    FileLog.e("tmessages", "destroy video service");
  }
  
  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    boolean bool = false;
    this.path = paramIntent.getStringExtra("path");
    if (this.path == null)
    {
      stopSelf();
      return 2;
    }
    FileLog.e("tmessages", "start video service");
    if (this.builder == null)
    {
      this.builder = new NotificationCompat.Builder(ApplicationLoader.applicationContext);
      this.builder.setSmallIcon(17301640);
      this.builder.setWhen(System.currentTimeMillis());
      this.builder.setContentTitle(LocaleController.getString("AppName", 2131165338));
      this.builder.setTicker(LocaleController.getString("SendingVideo", 2131166296));
      this.builder.setContentText(LocaleController.getString("SendingVideo", 2131166296));
    }
    this.currentProgress = 0;
    paramIntent = this.builder;
    paramInt1 = this.currentProgress;
    if (this.currentProgress == 0) {
      bool = true;
    }
    paramIntent.setProgress(100, paramInt1, bool);
    startForeground(4, this.builder.build());
    NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
    return 2;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\VideoEncodingService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */