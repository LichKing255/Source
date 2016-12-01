package org.telegram.ui.Supergram.DownloadManager.Receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.ui.Supergram.DownloadManager.Download;
import org.telegram.ui.Supergram.DownloadManager.SQLite.ElementDownload;
import org.telegram.ui.Supergram.DownloadManager.SQLite.SQLDownload;

public class DownloadReceiver
  extends WakefulBroadcastReceiver
{
  AlarmManager mAlarmManager;
  PendingIntent mPendingIntent;
  PendingIntent mPendingIntent_end;
  SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("sdownload2", 0);
  
  public void cancelAlarm(Context paramContext)
  {
    this.mAlarmManager = ((AlarmManager)paramContext.getSystemService("alarm"));
    this.mPendingIntent = PendingIntent.getBroadcast(paramContext, 100, new Intent(paramContext, DownloadReceiver.class), 0);
    this.mAlarmManager.cancel(this.mPendingIntent);
    this.mPendingIntent_end = PendingIntent.getBroadcast(paramContext, 200, new Intent(paramContext, DownloadReceiver.class), 0);
    this.mAlarmManager.cancel(this.mPendingIntent_end);
    int i = 1;
    while (i < 8)
    {
      this.mPendingIntent = PendingIntent.getBroadcast(paramContext, i + 300, new Intent(paramContext, DownloadReceiver.class), 0);
      this.mAlarmManager.cancel(this.mPendingIntent);
      this.mPendingIntent_end = PendingIntent.getBroadcast(paramContext, i + 300 + 10, new Intent(paramContext, DownloadReceiver.class), 0);
      this.mAlarmManager.cancel(this.mPendingIntent_end);
      i += 1;
    }
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    Log.v("jjj", "alarm recieve");
    Object localObject = (WifiManager)paramContext.getSystemService("wifi");
    int i = paramIntent.getIntExtra("start_end", 0);
    paramIntent = new SQLDownload(paramContext);
    List localList = paramIntent.getAllVideoInDownloadE();
    if (i == 1000)
    {
      if (this.preferences.getBoolean("w_enable", false)) {
        ((WifiManager)localObject).setWifiEnabled(true);
      }
      WakeLocker.acquire(paramContext);
      i = 0;
      while (i < localList.size())
      {
        if (((ElementDownload)localList.get(i)).isCheck())
        {
          paramContext = (ElementDownload)localList.get(i);
          if ((((ElementDownload)localList.get(i)).getType() == 9) || (((ElementDownload)localList.get(i)).getType() == 3))
          {
            localObject = new TLRPC.TL_document();
            ((TLRPC.Document)localObject).access_hash = Long.parseLong(paramContext.getAccess_hash());
            ((TLRPC.Document)localObject).id = Long.parseLong(paramContext.getId());
            ((TLRPC.Document)localObject).date = Integer.parseInt(paramContext.getDate());
            ((TLRPC.Document)localObject).file_name = null;
            ((TLRPC.Document)localObject).mime_type = paramContext.getMime_type();
            ((TLRPC.Document)localObject).size = paramContext.getSize();
            ((TLRPC.Document)localObject).dc_id = paramContext.getDc_id();
            ((TLRPC.Document)localObject).user_id = paramContext.getUser_id();
            ((TLRPC.Document)localObject).thumb = new TLRPC.PhotoSize();
            ((TLRPC.Document)localObject).thumb.type = "";
            ((TLRPC.Document)localObject).attributes.add(new TLRPC.TL_documentAttributeFilename());
            ((TLRPC.DocumentAttribute)((TLRPC.Document)localObject).attributes.get(0)).file_name = ((ElementDownload)localList.get(i)).getFile_name();
            paramIntent.updatestate(paramContext.getId(), 1);
            FileLoader.getInstance().loadFile((TLRPC.Document)localObject, true, false);
          }
        }
        i += 1;
      }
    }
    if (this.preferences.getBoolean("w_disable", false)) {
      ((WifiManager)localObject).setWifiEnabled(false);
    }
    i = 0;
    while (i < localList.size())
    {
      if (((ElementDownload)localList.get(i)).isCheck())
      {
        paramContext = (ElementDownload)localList.get(i);
        if ((paramContext.getType() == 9) || (((ElementDownload)localList.get(i)).getType() == 3))
        {
          localObject = new TLRPC.TL_document();
          ((TLRPC.Document)localObject).access_hash = Long.parseLong(paramContext.getAccess_hash());
          ((TLRPC.Document)localObject).id = Long.parseLong(paramContext.getId());
          ((TLRPC.Document)localObject).date = Integer.parseInt(paramContext.getDate());
          ((TLRPC.Document)localObject).file_name = null;
          ((TLRPC.Document)localObject).mime_type = paramContext.getMime_type();
          ((TLRPC.Document)localObject).size = paramContext.getSize();
          ((TLRPC.Document)localObject).dc_id = paramContext.getDc_id();
          ((TLRPC.Document)localObject).user_id = paramContext.getUser_id();
          ((TLRPC.Document)localObject).thumb = new TLRPC.PhotoSize();
          ((TLRPC.Document)localObject).thumb.type = "";
          ((TLRPC.Document)localObject).attributes.add(new TLRPC.TL_documentAttributeFilename());
          ((TLRPC.DocumentAttribute)((TLRPC.Document)localObject).attributes.get(0)).file_name = paramContext.getFile_name();
          paramIntent.updatestate(paramContext.getId(), 0);
          FileLoader.getInstance().cancelLoadFile((TLRPC.Document)localObject);
        }
      }
      i += 1;
    }
    WakeLocker.release();
    paramContext = Download.download;
    if (paramContext != null) {
      paramContext.start();
    }
  }
  
  public void setAlarm(Context paramContext, Calendar paramCalendar1, Calendar paramCalendar2, int paramInt)
  {
    this.mAlarmManager = ((AlarmManager)paramContext.getSystemService("alarm"));
    Intent localIntent = new Intent(paramContext, DownloadReceiver.class);
    localIntent.putExtra("Reminder_ID", 100);
    localIntent.putExtra("start_end", 1000);
    this.mPendingIntent = PendingIntent.getBroadcast(paramContext, 100, localIntent, 134217728);
    this.mAlarmManager.set(2, paramCalendar1.getTimeInMillis() - Calendar.getInstance().getTimeInMillis() + SystemClock.elapsedRealtime(), this.mPendingIntent);
    paramCalendar1 = new Intent(paramContext, DownloadReceiver.class);
    localIntent.putExtra("Reminder_ID", 200);
    localIntent.putExtra("start_end", 900);
    this.mPendingIntent_end = PendingIntent.getBroadcast(paramContext, 200, paramCalendar1, 134217728);
    this.mAlarmManager.set(2, paramCalendar2.getTimeInMillis() - Calendar.getInstance().getTimeInMillis() + SystemClock.elapsedRealtime(), this.mPendingIntent_end);
  }
  
  public void setRepeatAlarm(Context paramContext, Calendar paramCalendar1, Calendar paramCalendar2, int paramInt)
  {
    this.mAlarmManager = ((AlarmManager)paramContext.getSystemService("alarm"));
    Intent localIntent = new Intent(paramContext, DownloadReceiver.class);
    localIntent.putExtra("start_end", 1000);
    this.mPendingIntent = PendingIntent.getBroadcast(paramContext, paramInt, localIntent, 134217728);
    this.mAlarmManager.setRepeating(2, paramCalendar1.getTimeInMillis() - Calendar.getInstance().getTimeInMillis() + SystemClock.elapsedRealtime(), 604800000L, this.mPendingIntent);
    paramCalendar1 = new Intent(paramContext, DownloadReceiver.class);
    paramCalendar1.putExtra("start_end", 900);
    this.mPendingIntent_end = PendingIntent.getBroadcast(paramContext, paramInt + 10, paramCalendar1, 134217728);
    this.mAlarmManager.setRepeating(2, paramCalendar2.getTimeInMillis() - Calendar.getInstance().getTimeInMillis() + SystemClock.elapsedRealtime(), 604800000L, this.mPendingIntent_end);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\DownloadManager\Receiver\DownloadReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */