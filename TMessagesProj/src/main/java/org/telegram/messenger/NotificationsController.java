package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.provider.Settings.System;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationCompat.Action.Builder;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat.CarExtender;
import android.support.v4.app.NotificationCompat.CarExtender.UnreadConversation.Builder;
import android.support.v4.app.NotificationCompat.Extender;
import android.support.v4.app.NotificationCompat.InboxStyle;
import android.support.v4.app.NotificationCompat.Style;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.RemoteInput.Builder;
import android.util.SparseArray;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_account_updateNotifySettings;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputNotifyPeer;
import org.telegram.tgnet.TLRPC.TL_inputPeerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle;
import org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
import org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
import org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PopupNotificationActivity;

public class NotificationsController
{
  public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
  private static volatile NotificationsController Instance = null;
  private AlarmManager alarmManager;
  protected AudioManager audioManager;
  private int autoNotificationId = 20000;
  private HashMap<Long, Integer> autoNotificationsIds = new HashMap();
  private ArrayList<MessageObject> delayedPushMessages = new ArrayList();
  private boolean inChatSoundEnabled = true;
  private int lastBadgeCount;
  private int lastOnlineFromOtherDevice = 0;
  private long lastSoundOutPlay;
  private long lastSoundPlay;
  private String launcherClassName;
  private Runnable notificationDelayRunnable;
  private PowerManager.WakeLock notificationDelayWakelock;
  private NotificationManagerCompat notificationManager = null;
  private DispatchQueue notificationsQueue = new DispatchQueue("notificationsQueue");
  private boolean notifyCheck = false;
  private long opened_dialog_id = 0L;
  private int personal_count = 0;
  public ArrayList<MessageObject> popupMessages = new ArrayList();
  private HashMap<Long, Integer> pushDialogs = new HashMap();
  private HashMap<Long, Integer> pushDialogsOverrideMention = new HashMap();
  private ArrayList<MessageObject> pushMessages = new ArrayList();
  private HashMap<Long, MessageObject> pushMessagesDict = new HashMap();
  private HashMap<Long, Point> smartNotificationsDialogs = new HashMap();
  private int soundIn;
  private boolean soundInLoaded;
  private int soundOut;
  private boolean soundOutLoaded;
  private SoundPool soundPool;
  private int soundRecord;
  private boolean soundRecordLoaded;
  private int total_unread_count = 0;
  private int wearNotificationId = 10000;
  private HashMap<Long, Integer> wearNotificationsIds = new HashMap();
  
  public NotificationsController()
  {
    try
    {
      this.audioManager = ((AudioManager)ApplicationLoader.applicationContext.getSystemService("audio"));
    }
    catch (Exception localException2)
    {
      try
      {
        this.alarmManager = ((AlarmManager)ApplicationLoader.applicationContext.getSystemService("alarm"));
      }
      catch (Exception localException2)
      {
        try
        {
          for (;;)
          {
            this.notificationDelayWakelock = ((PowerManager)ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, "lock");
            this.notificationDelayWakelock.setReferenceCounted(false);
            this.notificationDelayRunnable = new Runnable()
            {
              public void run()
              {
                FileLog.e("tmessages", "delay reached");
                if (!NotificationsController.this.delayedPushMessages.isEmpty())
                {
                  NotificationsController.this.showOrUpdateNotification(true);
                  NotificationsController.this.delayedPushMessages.clear();
                }
                try
                {
                  if (NotificationsController.this.notificationDelayWakelock.isHeld()) {
                    NotificationsController.this.notificationDelayWakelock.release();
                  }
                  return;
                }
                catch (Exception localException)
                {
                  FileLog.e("tmessages", localException);
                }
              }
            };
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
  
  private void dismissNotification()
  {
    Map.Entry localEntry;
    try
    {
      this.notificationManager.cancel(1);
      this.pushMessages.clear();
      this.pushMessagesDict.clear();
      Iterator localIterator1 = this.autoNotificationsIds.entrySet().iterator();
      while (localIterator1.hasNext())
      {
        localEntry = (Map.Entry)localIterator1.next();
        this.notificationManager.cancel(((Integer)localEntry.getValue()).intValue());
      }
      this.autoNotificationsIds.clear();
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
      return;
    }
    Iterator localIterator2 = this.wearNotificationsIds.entrySet().iterator();
    while (localIterator2.hasNext())
    {
      localEntry = (Map.Entry)localIterator2.next();
      this.notificationManager.cancel(((Integer)localEntry.getValue()).intValue());
    }
    this.wearNotificationsIds.clear();
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
      }
    });
  }
  
  public static NotificationsController getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          NotificationsController localNotificationsController2 = Instance;
          localObject1 = localNotificationsController2;
          if (localNotificationsController2 == null) {
            localObject1 = new NotificationsController();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (NotificationsController)localObject1;
          return (NotificationsController)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localNotificationsController1;
  }
  
  private static String getLauncherClassName(Context paramContext)
  {
    try
    {
      Object localObject1 = paramContext.getPackageManager();
      Object localObject2 = new Intent("android.intent.action.MAIN");
      ((Intent)localObject2).addCategory("android.intent.category.LAUNCHER");
      localObject1 = ((PackageManager)localObject1).queryIntentActivities((Intent)localObject2, 0).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (ResolveInfo)((Iterator)localObject1).next();
        if (((ResolveInfo)localObject2).activityInfo.applicationInfo.packageName.equalsIgnoreCase(paramContext.getPackageName()))
        {
          paramContext = ((ResolveInfo)localObject2).activityInfo.name;
          return paramContext;
        }
      }
    }
    catch (Throwable paramContext)
    {
      FileLog.e("tmessages", paramContext);
    }
    return null;
  }
  
  private int getNotifyOverride(SharedPreferences paramSharedPreferences, long paramLong)
  {
    int j = paramSharedPreferences.getInt("notify2_" + paramLong, 0);
    int i = j;
    if (j == 3)
    {
      i = j;
      if (paramSharedPreferences.getInt("notifyuntil_" + paramLong, 0) >= ConnectionsManager.getInstance().getCurrentTime()) {
        i = 2;
      }
    }
    return i;
  }
  
  private String getStringForMessage(MessageObject paramMessageObject, boolean paramBoolean)
  {
    long l2 = paramMessageObject.messageOwner.dialog_id;
    int j;
    int k;
    int i;
    label73:
    long l1;
    label95:
    Object localObject2;
    Object localObject1;
    if (paramMessageObject.messageOwner.to_id.chat_id != 0)
    {
      j = paramMessageObject.messageOwner.to_id.chat_id;
      k = paramMessageObject.messageOwner.to_id.user_id;
      if (k != 0) {
        break label157;
      }
      if ((!paramMessageObject.isFromUser()) && (paramMessageObject.getId() >= 0)) {
        break label150;
      }
      i = paramMessageObject.messageOwner.from_id;
      l1 = l2;
      if (l2 == 0L)
      {
        if (j == 0) {
          break label179;
        }
        l1 = -j;
      }
      localObject2 = null;
      if (i <= 0) {
        break label194;
      }
      localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(i));
      if (localObject1 != null) {
        localObject2 = UserObject.getUserName((TLRPC.User)localObject1);
      }
    }
    for (;;)
    {
      if (localObject2 != null) {
        break label222;
      }
      paramMessageObject = null;
      return paramMessageObject;
      j = paramMessageObject.messageOwner.to_id.channel_id;
      break;
      label150:
      i = -j;
      break label73;
      label157:
      i = k;
      if (k != UserConfig.getClientUserId()) {
        break label73;
      }
      i = paramMessageObject.messageOwner.from_id;
      break label73;
      label179:
      l1 = l2;
      if (i == 0) {
        break label95;
      }
      l1 = i;
      break label95;
      label194:
      localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(-i));
      if (localObject1 != null) {
        localObject2 = ((TLRPC.Chat)localObject1).title;
      }
    }
    label222:
    Object localObject3 = null;
    if (j != 0)
    {
      localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(j));
      localObject3 = localObject1;
      if (localObject1 == null) {
        return null;
      }
    }
    Object localObject4 = null;
    if (((int)l1 == 0) || (AndroidUtilities.needShowPasscode(false)) || (UserConfig.isWaitingForPasscodeEnter)) {
      localObject1 = LocaleController.getString("YouHaveNewMessage", 2131166494);
    }
    for (;;)
    {
      localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
      paramMessageObject = (MessageObject)localObject1;
      if (((SharedPreferences)localObject2).getBoolean("chat_unlocked", false)) {
        break;
      }
      paramMessageObject = (MessageObject)localObject1;
      if (!((SharedPreferences)localObject2).contains("hide_" + String.valueOf(l1))) {
        break;
      }
      if (((SharedPreferences)localObject2).getBoolean("show_notification", true)) {
        break label4973;
      }
      return null;
      if ((j == 0) && (i != 0))
      {
        if (ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("EnablePreviewAll", true))
        {
          if ((paramMessageObject.messageOwner instanceof TLRPC.TL_messageService))
          {
            if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionUserJoined))
            {
              localObject1 = LocaleController.formatString("NotificationContactJoined", 2131166059, new Object[] { localObject2 });
            }
            else if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto))
            {
              localObject1 = LocaleController.formatString("NotificationContactNewPhoto", 2131166060, new Object[] { localObject2 });
            }
            else
            {
              localObject1 = localObject4;
              if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionLoginUnknownLocation))
              {
                localObject1 = LocaleController.formatString("formatDateAtTime", 2131166518, new Object[] { LocaleController.getInstance().formatterYear.format(paramMessageObject.messageOwner.date * 1000L), LocaleController.getInstance().formatterDay.format(paramMessageObject.messageOwner.date * 1000L) });
                localObject1 = LocaleController.formatString("NotificationUnrecognizedDevice", 2131166096, new Object[] { UserConfig.getCurrentUser().first_name, localObject1, paramMessageObject.messageOwner.action.title, paramMessageObject.messageOwner.action.address });
              }
            }
          }
          else if (paramMessageObject.isMediaEmpty())
          {
            if (!paramBoolean)
            {
              if ((paramMessageObject.messageOwner.message != null) && (paramMessageObject.messageOwner.message.length() != 0)) {
                localObject1 = LocaleController.formatString("NotificationMessageText", 2131166093, new Object[] { localObject2, paramMessageObject.messageOwner.message });
              } else {
                localObject1 = LocaleController.formatString("NotificationMessageNoText", 2131166089, new Object[] { localObject2 });
              }
            }
            else {
              localObject1 = LocaleController.formatString("NotificationMessageNoText", 2131166089, new Object[] { localObject2 });
            }
          }
          else if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
          {
            localObject1 = LocaleController.formatString("NotificationMessagePhoto", 2131166090, new Object[] { localObject2 });
          }
          else if (paramMessageObject.isVideo())
          {
            localObject1 = LocaleController.formatString("NotificationMessageVideo", 2131166094, new Object[] { localObject2 });
          }
          else if (paramMessageObject.isVoice())
          {
            localObject1 = LocaleController.formatString("NotificationMessageAudio", 2131166071, new Object[] { localObject2 });
          }
          else if (paramMessageObject.isMusic())
          {
            localObject1 = LocaleController.formatString("NotificationMessageMusic", 2131166088, new Object[] { localObject2 });
          }
          else if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
          {
            localObject1 = LocaleController.formatString("NotificationMessageContact", 2131166072, new Object[] { localObject2 });
          }
          else if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)))
          {
            localObject1 = LocaleController.formatString("NotificationMessageMap", 2131166087, new Object[] { localObject2 });
          }
          else
          {
            localObject1 = localObject4;
            if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) {
              if (paramMessageObject.isSticker())
              {
                paramMessageObject = paramMessageObject.getStickerEmoji();
                if (paramMessageObject != null) {
                  localObject1 = LocaleController.formatString("NotificationMessageStickerEmoji", 2131166092, new Object[] { localObject2, paramMessageObject });
                } else {
                  localObject1 = LocaleController.formatString("NotificationMessageSticker", 2131166091, new Object[] { localObject2 });
                }
              }
              else if (paramMessageObject.isGif())
              {
                localObject1 = LocaleController.formatString("NotificationMessageGif", 2131166074, new Object[] { localObject2 });
              }
              else
              {
                localObject1 = LocaleController.formatString("NotificationMessageDocument", 2131166073, new Object[] { localObject2 });
              }
            }
          }
        }
        else {
          localObject1 = LocaleController.formatString("NotificationMessageNoText", 2131166089, new Object[] { localObject2 });
        }
      }
      else
      {
        localObject1 = localObject4;
        if (j != 0) {
          if (ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("EnablePreviewGroup", true))
          {
            if ((paramMessageObject.messageOwner instanceof TLRPC.TL_messageService))
            {
              if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatAddUser))
              {
                k = paramMessageObject.messageOwner.action.user_id;
                j = k;
                if (k == 0)
                {
                  j = k;
                  if (paramMessageObject.messageOwner.action.users.size() == 1) {
                    j = ((Integer)paramMessageObject.messageOwner.action.users.get(0)).intValue();
                  }
                }
                if (j != 0)
                {
                  if ((paramMessageObject.messageOwner.to_id.channel_id != 0) && (!paramMessageObject.isMegagroup()))
                  {
                    localObject1 = LocaleController.formatString("ChannelAddedByNotification", 2131165445, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                  }
                  else if (j == UserConfig.getClientUserId())
                  {
                    localObject1 = LocaleController.formatString("NotificationInvitedToGroup", 2131166069, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                  }
                  else
                  {
                    localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(j));
                    if (localObject1 == null) {
                      return null;
                    }
                    if (i == ((TLRPC.User)localObject1).id)
                    {
                      if (paramMessageObject.isMegagroup()) {
                        localObject1 = LocaleController.formatString("NotificationGroupAddSelfMega", 2131166065, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                      } else {
                        localObject1 = LocaleController.formatString("NotificationGroupAddSelf", 2131166064, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                      }
                    }
                    else {
                      localObject1 = LocaleController.formatString("NotificationGroupAddMember", 2131166063, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title, UserObject.getUserName((TLRPC.User)localObject1) });
                    }
                  }
                }
                else
                {
                  localObject1 = new StringBuilder("");
                  i = 0;
                  while (i < paramMessageObject.messageOwner.action.users.size())
                  {
                    localObject4 = MessagesController.getInstance().getUser((Integer)paramMessageObject.messageOwner.action.users.get(i));
                    if (localObject4 != null)
                    {
                      localObject4 = UserObject.getUserName((TLRPC.User)localObject4);
                      if (((StringBuilder)localObject1).length() != 0) {
                        ((StringBuilder)localObject1).append(", ");
                      }
                      ((StringBuilder)localObject1).append((String)localObject4);
                    }
                    i += 1;
                  }
                  localObject1 = LocaleController.formatString("NotificationGroupAddMember", 2131166063, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title, ((StringBuilder)localObject1).toString() });
                }
              }
              else if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatJoinedByLink))
              {
                localObject1 = LocaleController.formatString("NotificationInvitedToGroupByLink", 2131166070, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
              }
              else if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatEditTitle))
              {
                localObject1 = LocaleController.formatString("NotificationEditedGroupName", 2131166061, new Object[] { localObject2, paramMessageObject.messageOwner.action.title });
              }
              else if (((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatEditPhoto)) || ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatDeletePhoto)))
              {
                if ((paramMessageObject.messageOwner.to_id.channel_id != 0) && (!paramMessageObject.isMegagroup())) {
                  localObject1 = LocaleController.formatString("ChannelPhotoEditNotification", 2131165504, new Object[] { ((TLRPC.Chat)localObject3).title });
                } else {
                  localObject1 = LocaleController.formatString("NotificationEditedGroupPhoto", 2131166062, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                }
              }
              else if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatDeleteUser))
              {
                if (paramMessageObject.messageOwner.action.user_id == UserConfig.getClientUserId())
                {
                  localObject1 = LocaleController.formatString("NotificationGroupKickYou", 2131166067, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                }
                else if (paramMessageObject.messageOwner.action.user_id == i)
                {
                  localObject1 = LocaleController.formatString("NotificationGroupLeftMember", 2131166068, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                }
                else
                {
                  paramMessageObject = MessagesController.getInstance().getUser(Integer.valueOf(paramMessageObject.messageOwner.action.user_id));
                  if (paramMessageObject == null) {
                    return null;
                  }
                  localObject1 = LocaleController.formatString("NotificationGroupKickMember", 2131166066, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title, UserObject.getUserName(paramMessageObject) });
                }
              }
              else if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatCreate))
              {
                localObject1 = paramMessageObject.messageText.toString();
              }
              else if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChannelCreate))
              {
                localObject1 = paramMessageObject.messageText.toString();
              }
              else if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatMigrateTo))
              {
                localObject1 = LocaleController.formatString("ActionMigrateFromGroupNotify", 2131165270, new Object[] { ((TLRPC.Chat)localObject3).title });
              }
              else if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChannelMigrateFrom))
              {
                localObject1 = LocaleController.formatString("ActionMigrateFromGroupNotify", 2131165270, new Object[] { paramMessageObject.messageOwner.action.title });
              }
              else
              {
                localObject1 = localObject4;
                if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage)) {
                  if (paramMessageObject.replyMessageObject == null)
                  {
                    if ((!ChatObject.isChannel((TLRPC.Chat)localObject3)) || (((TLRPC.Chat)localObject3).megagroup)) {
                      localObject1 = LocaleController.formatString("NotificationActionPinnedNoText", 2131166045, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                    } else {
                      localObject1 = LocaleController.formatString("NotificationActionPinnedNoTextChannel", 2131166046, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                    }
                  }
                  else
                  {
                    localObject1 = paramMessageObject.replyMessageObject;
                    if (((MessageObject)localObject1).isMusic())
                    {
                      if ((!ChatObject.isChannel((TLRPC.Chat)localObject3)) || (((TLRPC.Chat)localObject3).megagroup)) {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedMusic", 2131166043, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                      } else {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedMusicChannel", 2131166044, new Object[] { ((TLRPC.Chat)localObject3).title });
                      }
                    }
                    else if (((MessageObject)localObject1).isVideo())
                    {
                      if ((!ChatObject.isChannel((TLRPC.Chat)localObject3)) || (((TLRPC.Chat)localObject3).megagroup)) {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedVideo", 2131166055, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                      } else {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedVideoChannel", 2131166056, new Object[] { ((TLRPC.Chat)localObject3).title });
                      }
                    }
                    else if (((MessageObject)localObject1).isGif())
                    {
                      if ((!ChatObject.isChannel((TLRPC.Chat)localObject3)) || (((TLRPC.Chat)localObject3).megagroup)) {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedGif", 2131166041, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                      } else {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedGifChannel", 2131166042, new Object[] { ((TLRPC.Chat)localObject3).title });
                      }
                    }
                    else if (((MessageObject)localObject1).isVoice())
                    {
                      if ((!ChatObject.isChannel((TLRPC.Chat)localObject3)) || (((TLRPC.Chat)localObject3).megagroup)) {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedVoice", 2131166057, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                      } else {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedVoiceChannel", 2131166058, new Object[] { ((TLRPC.Chat)localObject3).title });
                      }
                    }
                    else if (((MessageObject)localObject1).isSticker())
                    {
                      paramMessageObject = paramMessageObject.getStickerEmoji();
                      if (paramMessageObject != null)
                      {
                        if ((!ChatObject.isChannel((TLRPC.Chat)localObject3)) || (((TLRPC.Chat)localObject3).megagroup)) {
                          localObject1 = LocaleController.formatString("NotificationActionPinnedStickerEmoji", 2131166051, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title, paramMessageObject });
                        } else {
                          localObject1 = LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", 2131166052, new Object[] { ((TLRPC.Chat)localObject3).title, paramMessageObject });
                        }
                      }
                      else if ((!ChatObject.isChannel((TLRPC.Chat)localObject3)) || (((TLRPC.Chat)localObject3).megagroup)) {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedSticker", 2131166049, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                      } else {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedStickerChannel", 2131166050, new Object[] { ((TLRPC.Chat)localObject3).title });
                      }
                    }
                    else if ((((MessageObject)localObject1).messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
                    {
                      if ((!ChatObject.isChannel((TLRPC.Chat)localObject3)) || (((TLRPC.Chat)localObject3).megagroup)) {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedFile", 2131166037, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                      } else {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedFileChannel", 2131166038, new Object[] { ((TLRPC.Chat)localObject3).title });
                      }
                    }
                    else if ((((MessageObject)localObject1).messageOwner.media instanceof TLRPC.TL_messageMediaGeo))
                    {
                      if ((!ChatObject.isChannel((TLRPC.Chat)localObject3)) || (((TLRPC.Chat)localObject3).megagroup)) {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedGeo", 2131166039, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                      } else {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedGeoChannel", 2131166040, new Object[] { ((TLRPC.Chat)localObject3).title });
                      }
                    }
                    else if ((((MessageObject)localObject1).messageOwner.media instanceof TLRPC.TL_messageMediaContact))
                    {
                      if ((!ChatObject.isChannel((TLRPC.Chat)localObject3)) || (((TLRPC.Chat)localObject3).megagroup)) {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedContact", 2131166035, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                      } else {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedContactChannel", 2131166036, new Object[] { ((TLRPC.Chat)localObject3).title });
                      }
                    }
                    else if ((((MessageObject)localObject1).messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
                    {
                      if ((!ChatObject.isChannel((TLRPC.Chat)localObject3)) || (((TLRPC.Chat)localObject3).megagroup)) {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedPhoto", 2131166047, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                      } else {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedPhotoChannel", 2131166048, new Object[] { ((TLRPC.Chat)localObject3).title });
                      }
                    }
                    else if ((((MessageObject)localObject1).messageText != null) && (((MessageObject)localObject1).messageText.length() > 0))
                    {
                      localObject1 = ((MessageObject)localObject1).messageText;
                      paramMessageObject = (MessageObject)localObject1;
                      if (((CharSequence)localObject1).length() > 20) {
                        paramMessageObject = ((CharSequence)localObject1).subSequence(0, 20) + "...";
                      }
                      if ((!ChatObject.isChannel((TLRPC.Chat)localObject3)) || (((TLRPC.Chat)localObject3).megagroup)) {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedText", 2131166053, new Object[] { localObject2, paramMessageObject, ((TLRPC.Chat)localObject3).title });
                      } else {
                        localObject1 = LocaleController.formatString("NotificationActionPinnedTextChannel", 2131166054, new Object[] { ((TLRPC.Chat)localObject3).title, paramMessageObject });
                      }
                    }
                    else if ((!ChatObject.isChannel((TLRPC.Chat)localObject3)) || (((TLRPC.Chat)localObject3).megagroup))
                    {
                      localObject1 = LocaleController.formatString("NotificationActionPinnedNoText", 2131166045, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                    }
                    else
                    {
                      localObject1 = LocaleController.formatString("NotificationActionPinnedNoTextChannel", 2131166046, new Object[] { ((TLRPC.Chat)localObject3).title });
                    }
                  }
                }
              }
            }
            else if ((ChatObject.isChannel((TLRPC.Chat)localObject3)) && (!((TLRPC.Chat)localObject3).megagroup))
            {
              if (paramMessageObject.messageOwner.post)
              {
                if (paramMessageObject.isMediaEmpty())
                {
                  if ((!paramBoolean) && (paramMessageObject.messageOwner.message != null) && (paramMessageObject.messageOwner.message.length() != 0)) {
                    localObject1 = LocaleController.formatString("NotificationMessageGroupText", 2131166085, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title, paramMessageObject.messageOwner.message });
                  } else {
                    localObject1 = LocaleController.formatString("ChannelMessageNoText", 2131165495, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                  }
                }
                else if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
                {
                  localObject1 = LocaleController.formatString("ChannelMessagePhoto", 2131165496, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                }
                else if (paramMessageObject.isVideo())
                {
                  localObject1 = LocaleController.formatString("ChannelMessageVideo", 2131165499, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                }
                else if (paramMessageObject.isVoice())
                {
                  localObject1 = LocaleController.formatString("ChannelMessageAudio", 2131165478, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                }
                else if (paramMessageObject.isMusic())
                {
                  localObject1 = LocaleController.formatString("ChannelMessageMusic", 2131165494, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                }
                else if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
                {
                  localObject1 = LocaleController.formatString("ChannelMessageContact", 2131165479, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                }
                else if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)))
                {
                  localObject1 = LocaleController.formatString("ChannelMessageMap", 2131165493, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                }
                else
                {
                  localObject1 = localObject4;
                  if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) {
                    if (paramMessageObject.isSticker())
                    {
                      paramMessageObject = paramMessageObject.getStickerEmoji();
                      if (paramMessageObject != null) {
                        localObject1 = LocaleController.formatString("ChannelMessageStickerEmoji", 2131165498, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title, paramMessageObject });
                      } else {
                        localObject1 = LocaleController.formatString("ChannelMessageSticker", 2131165497, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                      }
                    }
                    else if (paramMessageObject.isGif())
                    {
                      localObject1 = LocaleController.formatString("ChannelMessageGIF", 2131165481, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                    }
                    else
                    {
                      localObject1 = LocaleController.formatString("ChannelMessageDocument", 2131165480, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                    }
                  }
                }
              }
              else if (paramMessageObject.isMediaEmpty())
              {
                if ((!paramBoolean) && (paramMessageObject.messageOwner.message != null) && (paramMessageObject.messageOwner.message.length() != 0)) {
                  localObject1 = LocaleController.formatString("NotificationMessageGroupText", 2131166085, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title, paramMessageObject.messageOwner.message });
                } else {
                  localObject1 = LocaleController.formatString("ChannelMessageGroupNoText", 2131165488, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                }
              }
              else if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
              {
                localObject1 = LocaleController.formatString("ChannelMessageGroupPhoto", 2131165489, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
              }
              else if (paramMessageObject.isVideo())
              {
                localObject1 = LocaleController.formatString("ChannelMessageGroupVideo", 2131165492, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
              }
              else if (paramMessageObject.isVoice())
              {
                localObject1 = LocaleController.formatString("ChannelMessageGroupAudio", 2131165482, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
              }
              else if (paramMessageObject.isMusic())
              {
                localObject1 = LocaleController.formatString("ChannelMessageGroupMusic", 2131165487, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
              }
              else if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
              {
                localObject1 = LocaleController.formatString("ChannelMessageGroupContact", 2131165483, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
              }
              else if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)))
              {
                localObject1 = LocaleController.formatString("ChannelMessageGroupMap", 2131165486, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
              }
              else
              {
                localObject1 = localObject4;
                if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) {
                  if (paramMessageObject.isSticker())
                  {
                    paramMessageObject = paramMessageObject.getStickerEmoji();
                    if (paramMessageObject != null) {
                      localObject1 = LocaleController.formatString("ChannelMessageGroupStickerEmoji", 2131165491, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title, paramMessageObject });
                    } else {
                      localObject1 = LocaleController.formatString("ChannelMessageGroupSticker", 2131165490, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                    }
                  }
                  else if (paramMessageObject.isGif())
                  {
                    localObject1 = LocaleController.formatString("ChannelMessageGroupGif", 2131165485, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                  }
                  else
                  {
                    localObject1 = LocaleController.formatString("ChannelMessageGroupDocument", 2131165484, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                  }
                }
              }
            }
            else if (paramMessageObject.isMediaEmpty())
            {
              if ((!paramBoolean) && (paramMessageObject.messageOwner.message != null) && (paramMessageObject.messageOwner.message.length() != 0)) {
                localObject1 = LocaleController.formatString("NotificationMessageGroupText", 2131166085, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title, paramMessageObject.messageOwner.message });
              } else {
                localObject1 = LocaleController.formatString("NotificationMessageGroupNoText", 2131166081, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
              }
            }
            else if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
            {
              localObject1 = LocaleController.formatString("NotificationMessageGroupPhoto", 2131166082, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
            }
            else if (paramMessageObject.isVideo())
            {
              localObject1 = LocaleController.formatString("NotificationMessageGroupVideo", 2131166086, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
            }
            else if (paramMessageObject.isVoice())
            {
              localObject1 = LocaleController.formatString("NotificationMessageGroupAudio", 2131166075, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
            }
            else if (paramMessageObject.isMusic())
            {
              localObject1 = LocaleController.formatString("NotificationMessageGroupMusic", 2131166080, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
            }
            else if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
            {
              localObject1 = LocaleController.formatString("NotificationMessageGroupContact", 2131166076, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
            }
            else if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)))
            {
              localObject1 = LocaleController.formatString("NotificationMessageGroupMap", 2131166079, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
            }
            else
            {
              localObject1 = localObject4;
              if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) {
                if (paramMessageObject.isSticker())
                {
                  paramMessageObject = paramMessageObject.getStickerEmoji();
                  if (paramMessageObject != null) {
                    localObject1 = LocaleController.formatString("NotificationMessageGroupStickerEmoji", 2131166084, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title, paramMessageObject });
                  } else {
                    localObject1 = LocaleController.formatString("NotificationMessageGroupSticker", 2131166083, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                  }
                }
                else if (paramMessageObject.isGif())
                {
                  localObject1 = LocaleController.formatString("NotificationMessageGroupGif", 2131166078, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                }
                else
                {
                  localObject1 = LocaleController.formatString("NotificationMessageGroupDocument", 2131166077, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
                }
              }
            }
          }
          else if ((ChatObject.isChannel((TLRPC.Chat)localObject3)) && (!((TLRPC.Chat)localObject3).megagroup)) {
            localObject1 = LocaleController.formatString("ChannelMessageNoText", 2131165495, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
          } else {
            localObject1 = LocaleController.formatString("NotificationMessageGroupNoText", 2131166081, new Object[] { localObject2, ((TLRPC.Chat)localObject3).title });
          }
        }
      }
    }
    label4973:
    return LocaleController.getString("ShowNewHiddenMessage", 2131166673);
  }
  
  private boolean isPersonalMessage(MessageObject paramMessageObject)
  {
    return (paramMessageObject.messageOwner.to_id != null) && (paramMessageObject.messageOwner.to_id.chat_id == 0) && (paramMessageObject.messageOwner.to_id.channel_id == 0) && ((paramMessageObject.messageOwner.action == null) || ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionEmpty)));
  }
  
  private void playInChatSound()
  {
    if ((!this.inChatSoundEnabled) || (MediaController.getInstance().isRecordingAudio())) {}
    for (;;)
    {
      return;
      for (;;)
      {
        try
        {
          int i = this.audioManager.getRingerMode();
          if (i == 0) {
            break;
          }
        }
        catch (Exception localException2)
        {
          FileLog.e("tmessages", localException2);
          continue;
        }
        try
        {
          if (getNotifyOverride(ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0), this.opened_dialog_id) == 2) {
            break;
          }
          this.notificationsQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              if (Math.abs(System.currentTimeMillis() - NotificationsController.this.lastSoundPlay) <= 500L) {}
              for (;;)
              {
                return;
                try
                {
                  if (NotificationsController.this.soundPool == null)
                  {
                    NotificationsController.access$2402(NotificationsController.this, new SoundPool(3, 1, 0));
                    NotificationsController.this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
                    {
                      public void onLoadComplete(SoundPool paramAnonymous2SoundPool, int paramAnonymous2Int1, int paramAnonymous2Int2)
                      {
                        if (paramAnonymous2Int2 == 0) {
                          paramAnonymous2SoundPool.play(paramAnonymous2Int1, 1.0F, 1.0F, 1, 0, 1.0F);
                        }
                      }
                    });
                  }
                  if ((NotificationsController.this.soundIn == 0) && (!NotificationsController.this.soundInLoaded))
                  {
                    NotificationsController.access$2602(NotificationsController.this, true);
                    NotificationsController.access$2502(NotificationsController.this, NotificationsController.this.soundPool.load(ApplicationLoader.applicationContext, 2131099648, 1));
                  }
                  if (NotificationsController.this.soundIn != 0)
                  {
                    NotificationsController.this.soundPool.play(NotificationsController.this.soundIn, 1.0F, 1.0F, 1, 0, 1.0F);
                    return;
                  }
                }
                catch (Exception localException)
                {
                  FileLog.e("tmessages", localException);
                }
              }
            }
          });
          return;
        }
        catch (Exception localException1)
        {
          FileLog.e("tmessages", localException1);
          return;
        }
      }
    }
  }
  
  private void scheduleNotificationDelay(boolean paramBoolean)
  {
    try
    {
      FileLog.e("tmessages", "delay notification start, onlineReason = " + paramBoolean);
      this.notificationDelayWakelock.acquire(10000L);
      AndroidUtilities.cancelRunOnUIThread(this.notificationDelayRunnable);
      Runnable localRunnable = this.notificationDelayRunnable;
      if (paramBoolean) {}
      for (int i = 3000;; i = 1000)
      {
        AndroidUtilities.runOnUIThread(localRunnable, i);
        return;
      }
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
      showOrUpdateNotification(this.notifyCheck);
    }
  }
  
  private void scheduleNotificationRepeat()
  {
    try
    {
      PendingIntent localPendingIntent = PendingIntent.getService(ApplicationLoader.applicationContext, 0, new Intent(ApplicationLoader.applicationContext, NotificationRepeat.class), 0);
      int i = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getInt("repeat_messages", 60);
      if ((i > 0) && (this.personal_count > 0))
      {
        this.alarmManager.set(2, SystemClock.elapsedRealtime() + i * 60 * 1000, localPendingIntent);
        return;
      }
      this.alarmManager.cancel(localPendingIntent);
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  private void setBadge(final int paramInt)
  {
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (NotificationsController.this.lastBadgeCount == paramInt) {}
        for (;;)
        {
          return;
          NotificationsController.access$1202(NotificationsController.this, paramInt);
          try
          {
            ContentValues localContentValues = new ContentValues();
            localContentValues.put("tag", "org.telegram.messenger/org.telegram.ui.LaunchActivity");
            localContentValues.put("count", Integer.valueOf(paramInt));
            ApplicationLoader.applicationContext.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"), localContentValues);
            try
            {
              if (NotificationsController.this.launcherClassName == null) {
                NotificationsController.access$2102(NotificationsController.this, NotificationsController.getLauncherClassName(ApplicationLoader.applicationContext));
              }
              if (NotificationsController.this.launcherClassName == null) {
                continue;
              }
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  try
                  {
                    Intent localIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
                    localIntent.putExtra("badge_count", NotificationsController.10.this.val$count);
                    localIntent.putExtra("badge_count_package_name", ApplicationLoader.applicationContext.getPackageName());
                    localIntent.putExtra("badge_count_class_name", NotificationsController.this.launcherClassName);
                    ApplicationLoader.applicationContext.sendBroadcast(localIntent);
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
            catch (Throwable localThrowable1)
            {
              FileLog.e("tmessages", localThrowable1);
              return;
            }
          }
          catch (Throwable localThrowable2)
          {
            for (;;) {}
          }
        }
      }
    });
  }
  
  @SuppressLint({"InlinedApi"})
  private void showExtraNotifications(NotificationCompat.Builder paramBuilder, boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT < 18) {}
    for (;;)
    {
      return;
      ArrayList localArrayList = new ArrayList();
      HashMap localHashMap1 = new HashMap();
      int i = 0;
      Object localObject2;
      long l;
      Object localObject1;
      if (i < this.pushMessages.size())
      {
        localObject2 = (MessageObject)this.pushMessages.get(i);
        l = ((MessageObject)localObject2).getDialogId();
        if ((int)l == 0) {}
        for (;;)
        {
          i += 1;
          break;
          localObject1 = (ArrayList)localHashMap1.get(Long.valueOf(l));
          paramBuilder = (NotificationCompat.Builder)localObject1;
          if (localObject1 == null)
          {
            paramBuilder = new ArrayList();
            localHashMap1.put(Long.valueOf(l), paramBuilder);
            localArrayList.add(0, Long.valueOf(l));
          }
          paramBuilder.add(localObject2);
        }
      }
      HashMap localHashMap2 = new HashMap();
      localHashMap2.putAll(this.wearNotificationsIds);
      this.wearNotificationsIds.clear();
      HashMap localHashMap3 = new HashMap();
      localHashMap3.putAll(this.autoNotificationsIds);
      this.autoNotificationsIds.clear();
      i = 0;
      if (i < localArrayList.size())
      {
        l = ((Long)localArrayList.get(i)).longValue();
        Object localObject6 = (ArrayList)localHashMap1.get(Long.valueOf(l));
        int j = ((MessageObject)((ArrayList)localObject6).get(0)).getId();
        int k = ((MessageObject)((ArrayList)localObject6).get(0)).messageOwner.date;
        TLRPC.Chat localChat = null;
        NotificationCompat.Builder localBuilder = null;
        if (l > 0L)
        {
          paramBuilder = MessagesController.getInstance().getUser(Integer.valueOf((int)l));
          localBuilder = paramBuilder;
          if (paramBuilder != null) {}
        }
        else
        {
          do
          {
            i += 1;
            break;
            localChat = MessagesController.getInstance().getChat(Integer.valueOf(-(int)l));
          } while (localChat == null);
        }
        Integer localInteger = null;
        label389:
        label430:
        NotificationCompat.CarExtender.UnreadConversation.Builder localBuilder1;
        Object localObject3;
        if ((AndroidUtilities.needShowPasscode(false)) || (UserConfig.isWaitingForPasscodeEnter))
        {
          localObject1 = LocaleController.getString("AppName", 2131165338);
          paramBuilder = localInteger;
          localInteger = (Integer)localHashMap2.get(Long.valueOf(l));
          if (localInteger != null) {
            break label1044;
          }
          int m = this.wearNotificationId;
          this.wearNotificationId = (m + 1);
          localInteger = Integer.valueOf(m);
          localObject2 = (Integer)localHashMap3.get(Long.valueOf(l));
          if (localObject2 != null) {
            break label1058;
          }
          m = this.autoNotificationId;
          this.autoNotificationId = (m + 1);
          localObject2 = Integer.valueOf(m);
          localBuilder1 = new NotificationCompat.CarExtender.UnreadConversation.Builder((String)localObject1).setLatestTimestamp(k * 1000L);
          localObject3 = new Intent();
          ((Intent)localObject3).addFlags(32);
          ((Intent)localObject3).setAction("org.telegram.messenger.ACTION_MESSAGE_HEARD");
          ((Intent)localObject3).putExtra("dialog_id", l);
          ((Intent)localObject3).putExtra("max_id", j);
          localBuilder1.setReadPendingIntent(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, ((Integer)localObject2).intValue(), (Intent)localObject3, 134217728));
          localObject4 = null;
          localObject3 = localObject4;
          if (!ChatObject.isChannel(localChat))
          {
            localObject3 = localObject4;
            if (!AndroidUtilities.needShowPasscode(false))
            {
              localObject3 = localObject4;
              if (!UserConfig.isWaitingForPasscodeEnter)
              {
                localObject3 = new Intent();
                ((Intent)localObject3).addFlags(32);
                ((Intent)localObject3).setAction("org.telegram.messenger.ACTION_MESSAGE_REPLY");
                ((Intent)localObject3).putExtra("dialog_id", l);
                ((Intent)localObject3).putExtra("max_id", j);
                localBuilder1.setReplyAction(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, ((Integer)localObject2).intValue(), (Intent)localObject3, 134217728), new RemoteInput.Builder("extra_voice_reply").setLabel(LocaleController.getString("Reply", 2131166214)).build());
                localObject2 = new Intent(ApplicationLoader.applicationContext, WearReplyReceiver.class);
                ((Intent)localObject2).putExtra("dialog_id", l);
                ((Intent)localObject2).putExtra("max_id", j);
                localObject3 = PendingIntent.getBroadcast(ApplicationLoader.applicationContext, localInteger.intValue(), (Intent)localObject2, 134217728);
                localObject4 = new RemoteInput.Builder("extra_voice_reply").setLabel(LocaleController.getString("Reply", 2131166214)).build();
                if (localChat == null) {
                  break label1072;
                }
              }
            }
          }
        }
        label951:
        label1044:
        label1058:
        label1072:
        for (localObject2 = LocaleController.formatString("ReplyToGroup", 2131166215, new Object[] { localObject1 });; localObject2 = LocaleController.formatString("ReplyToUser", 2131166216, new Object[] { localObject1 }))
        {
          localObject3 = new NotificationCompat.Action.Builder(2130837870, (CharSequence)localObject2, (PendingIntent)localObject3).addRemoteInput((RemoteInput)localObject4).build();
          localObject2 = "";
          j = ((ArrayList)localObject6).size() - 1;
          for (;;)
          {
            if (j < 0) {
              break label1260;
            }
            localObject4 = getStringForMessage((MessageObject)((ArrayList)localObject6).get(j), false);
            if (localObject4 != null) {
              break;
            }
            j -= 1;
          }
          if (localChat != null) {}
          for (localObject2 = localChat.title;; localObject2 = UserObject.getUserName(localBuilder))
          {
            if (localChat == null) {
              break label951;
            }
            localObject1 = localObject2;
            paramBuilder = localInteger;
            if (localChat.photo == null) {
              break;
            }
            localObject1 = localObject2;
            paramBuilder = localInteger;
            if (localChat.photo.photo_small == null) {
              break;
            }
            localObject1 = localObject2;
            paramBuilder = localInteger;
            if (localChat.photo.photo_small.volume_id == 0L) {
              break;
            }
            localObject1 = localObject2;
            paramBuilder = localInteger;
            if (localChat.photo.photo_small.local_id == 0) {
              break;
            }
            paramBuilder = localChat.photo.photo_small;
            localObject1 = localObject2;
            break;
          }
          localObject1 = localObject2;
          paramBuilder = localInteger;
          if (localBuilder.photo == null) {
            break;
          }
          localObject1 = localObject2;
          paramBuilder = localInteger;
          if (localBuilder.photo.photo_small == null) {
            break;
          }
          localObject1 = localObject2;
          paramBuilder = localInteger;
          if (localBuilder.photo.photo_small.volume_id == 0L) {
            break;
          }
          localObject1 = localObject2;
          paramBuilder = localInteger;
          if (localBuilder.photo.photo_small.local_id == 0) {
            break;
          }
          paramBuilder = localBuilder.photo.photo_small;
          localObject1 = localObject2;
          break;
          localHashMap2.remove(Long.valueOf(l));
          break label389;
          localHashMap3.remove(Long.valueOf(l));
          break label430;
        }
        if (localChat != null) {}
        Object localObject5;
        for (Object localObject4 = ((String)localObject4).replace(" @ " + (String)localObject1, "");; localObject4 = ((String)localObject4).replace((String)localObject1 + ": ", "").replace((String)localObject1 + " ", ""))
        {
          localObject5 = localObject2;
          if (((String)localObject2).length() > 0) {
            localObject5 = (String)localObject2 + "\n\n";
          }
          localObject2 = (String)localObject5 + (String)localObject4;
          localBuilder1.addMessage((String)localObject4);
          break;
        }
        label1260:
        localObject4 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        ((Intent)localObject4).setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
        ((Intent)localObject4).setFlags(32768);
        if (localChat != null)
        {
          ((Intent)localObject4).putExtra("chatId", localChat.id);
          label1337:
          localObject5 = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, (Intent)localObject4, 1073741824);
          localObject6 = new NotificationCompat.WearableExtender();
          if (localObject3 != null) {
            ((NotificationCompat.WearableExtender)localObject6).addAction((NotificationCompat.Action)localObject3);
          }
          SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
          paramBoolean = localSharedPreferences.contains("hide_" + String.valueOf(l));
          localObject4 = localObject5;
          localObject3 = localObject1;
          if (paramBoolean)
          {
            localObject4 = localObject5;
            localObject3 = localObject1;
            if (!localSharedPreferences.getBoolean("chat_unlocked", false))
            {
              localObject3 = LocaleController.getString("AppName", 2131165338);
              localObject4 = null;
            }
          }
          localObject1 = new NotificationCompat.Builder(ApplicationLoader.applicationContext).setContentTitle((CharSequence)localObject3).setSmallIcon(2130838014).setGroup("messages").setContentText((CharSequence)localObject2).setAutoCancel(true).setColor(-13851168).setGroupSummary(false).setContentIntent((PendingIntent)localObject4).extend((NotificationCompat.Extender)localObject6).extend(new NotificationCompat.CarExtender().setUnreadConversation(localBuilder1.build())).setCategory("msg");
          if ((!paramBoolean) || (localSharedPreferences.getBoolean("chat_unlocked", false))) {
            break label1699;
          }
          ((NotificationCompat.Builder)localObject1).setLargeIcon(BitmapFactory.decodeResource(ApplicationLoader.applicationContext.getResources(), 2130837860));
        }
        for (;;)
        {
          if ((localChat == null) && (localBuilder != null) && (localBuilder.phone != null) && (localBuilder.phone.length() > 0)) {
            ((NotificationCompat.Builder)localObject1).addPerson("tel:+" + localBuilder.phone);
          }
          this.notificationManager.notify(localInteger.intValue(), ((NotificationCompat.Builder)localObject1).build());
          this.wearNotificationsIds.put(Long.valueOf(l), localInteger);
          break;
          if (localBuilder == null) {
            break label1337;
          }
          ((Intent)localObject4).putExtra("userId", localBuilder.id);
          break label1337;
          label1699:
          if (paramBuilder != null)
          {
            paramBuilder = ImageLoader.getInstance().getImageFromMemory(paramBuilder, null, "50_50");
            if (paramBuilder != null) {
              ((NotificationCompat.Builder)localObject1).setLargeIcon(paramBuilder.getBitmap());
            }
          }
        }
      }
      paramBuilder = localHashMap2.entrySet().iterator();
      while (paramBuilder.hasNext())
      {
        localObject1 = (Map.Entry)paramBuilder.next();
        this.notificationManager.cancel(((Integer)((Map.Entry)localObject1).getValue()).intValue());
      }
    }
  }
  
  private void showOrUpdateNotification(boolean paramBoolean)
  {
    if ((!UserConfig.isClientActivated()) || (this.pushMessages.isEmpty()))
    {
      dismissNotification();
      return;
    }
    MessageObject localMessageObject1;
    Object localObject7;
    int i7;
    try
    {
      ConnectionsManager.getInstance().resumeNetworkMaybe();
      localMessageObject1 = (MessageObject)this.pushMessages.get(0);
      localObject7 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
      i7 = ((SharedPreferences)localObject7).getInt("dismissDate", 0);
      if (localMessageObject1.messageOwner.date <= i7)
      {
        dismissNotification();
        return;
      }
    }
    catch (Exception localException1)
    {
      FileLog.e("tmessages", localException1);
      return;
    }
    long l2 = localMessageObject1.getDialogId();
    long l1 = l2;
    if (localMessageObject1.messageOwner.mentioned) {
      l1 = localMessageObject1.messageOwner.from_id;
    }
    localMessageObject1.getId();
    int i3;
    int i;
    int m;
    label184:
    Object localObject10;
    TLRPC.Chat localChat;
    Object localObject5;
    int i1;
    int k;
    int i6;
    Object localObject6;
    int j;
    boolean bool1;
    int i5;
    int n;
    int i2;
    label309:
    int i4;
    Object localObject1;
    label465:
    String str;
    boolean bool2;
    if (localMessageObject1.messageOwner.to_id.chat_id != 0)
    {
      i3 = localMessageObject1.messageOwner.to_id.chat_id;
      i = localMessageObject1.messageOwner.to_id.user_id;
      if (i != 0) {
        break label1798;
      }
      m = localMessageObject1.messageOwner.from_id;
      localObject10 = MessagesController.getInstance().getUser(Integer.valueOf(m));
      localChat = null;
      if (i3 != 0) {
        localChat = MessagesController.getInstance().getChat(Integer.valueOf(i3));
      }
      localObject5 = null;
      i1 = 0;
      k = 0;
      i6 = 0;
      localObject6 = null;
      j = -16711936;
      bool1 = false;
      i5 = 0;
      n = 0;
      i2 = getNotifyOverride((SharedPreferences)localObject7, l1);
      if ((!paramBoolean) || (i2 == 2)) {
        break label3005;
      }
      if (!((SharedPreferences)localObject7).getBoolean("EnableAll", true)) {
        break label2997;
      }
      i = i1;
      if (i3 != 0)
      {
        i = i1;
        if (!((SharedPreferences)localObject7).getBoolean("EnableGroup", true)) {
          break label2997;
        }
      }
      i2 = i;
      if (i == 0)
      {
        i2 = i;
        if (l2 == l1)
        {
          i2 = i;
          if (localChat != null)
          {
            i1 = ((SharedPreferences)localObject7).getInt("smart_max_count_" + l2, 2);
            i4 = ((SharedPreferences)localObject7).getInt("smart_delay_" + l2, 180);
            i2 = i;
            if (i1 != 0)
            {
              localObject1 = (Point)this.smartNotificationsDialogs.get(Long.valueOf(l2));
              if (localObject1 != null) {
                break label1821;
              }
              localObject1 = new Point(1, (int)(System.currentTimeMillis() / 1000L));
              this.smartNotificationsDialogs.put(Long.valueOf(l2), localObject1);
              i2 = i;
            }
          }
        }
      }
      str = Settings.System.DEFAULT_NOTIFICATION_URI.getPath();
      i4 = j;
      if (i2 == 0)
      {
        bool1 = ((SharedPreferences)localObject7).getBoolean("EnableInAppSounds", true);
        boolean bool3 = ((SharedPreferences)localObject7).getBoolean("EnableInAppVibrate", true);
        bool2 = ((SharedPreferences)localObject7).getBoolean("EnableInAppPreview", true);
        boolean bool4 = ((SharedPreferences)localObject7).getBoolean("EnableInAppPriority", false);
        i1 = ((SharedPreferences)localObject7).getInt("vibrate_" + l2, 0);
        i5 = ((SharedPreferences)localObject7).getInt("priority_" + l2, 3);
        i4 = 0;
        localObject4 = ((SharedPreferences)localObject7).getString("sound_path_" + l2, null);
        if (i3 == 0) {
          break label1924;
        }
        if ((localObject4 == null) || (!((String)localObject4).equals(str))) {
          break label1898;
        }
        localObject1 = null;
        label653:
        i = ((SharedPreferences)localObject7).getInt("vibrate_group", 0);
        k = ((SharedPreferences)localObject7).getInt("priority_group", 1);
        j = ((SharedPreferences)localObject7).getInt("GroupLed", -16711936);
        label693:
        n = j;
        if (!((SharedPreferences)localObject7).contains("color_" + l2)) {
          break label3010;
        }
        n = ((SharedPreferences)localObject7).getInt("color_" + l2, 0);
        break label3010;
        label762:
        boolean bool5 = ApplicationLoader.mainInterfacePaused;
        localObject4 = localObject1;
        i1 = j;
        i = k;
        if (!bool5)
        {
          if (!bool1) {
            localObject1 = null;
          }
          if (!bool3) {
            j = 2;
          }
          if (bool4) {
            break label3110;
          }
          i = 0;
          i1 = j;
          localObject4 = localObject1;
        }
        label814:
        localObject6 = localObject4;
        bool1 = bool2;
        i4 = n;
        k = i1;
        i5 = i;
        if (i6 != 0)
        {
          localObject6 = localObject4;
          bool1 = bool2;
          i4 = n;
          k = i1;
          i5 = i;
          if (i1 == 2) {}
        }
      }
    }
    try
    {
      j = this.audioManager.getRingerMode();
      localObject6 = localObject4;
      bool1 = bool2;
      i4 = n;
      k = i1;
      i5 = i;
      if (j != 0)
      {
        localObject6 = localObject4;
        bool1 = bool2;
        i4 = n;
        k = i1;
        i5 = i;
        if (j != 1)
        {
          k = 2;
          i5 = i;
          i4 = n;
          bool1 = bool2;
          localObject6 = localObject4;
        }
      }
    }
    catch (Exception localException2)
    {
      for (;;)
      {
        label1046:
        SharedPreferences localSharedPreferences;
        label1402:
        label1482:
        label1572:
        label1579:
        label1706:
        label1756:
        label1757:
        label1798:
        label1821:
        label1898:
        label1924:
        FileLog.e("tmessages", localException2);
        localObject6 = localObject4;
        bool1 = bool2;
        i4 = n;
        k = i1;
        i5 = i;
        continue;
        if (m != 0)
        {
          ((Intent)localObject4).putExtra("userId", m);
          continue;
          label2077:
          localObject2 = localObject5;
          if (this.pushDialogs.size() == 1) {
            if (localChat != null)
            {
              localObject2 = localObject5;
              if (localChat.photo != null)
              {
                localObject2 = localObject5;
                if (localChat.photo.photo_small != null)
                {
                  localObject2 = localObject5;
                  if (localChat.photo.photo_small.volume_id != 0L)
                  {
                    localObject2 = localObject5;
                    if (localChat.photo.photo_small.local_id != 0) {
                      localObject2 = localChat.photo.photo_small;
                    }
                  }
                }
              }
            }
            else
            {
              localObject2 = localObject5;
              if (localObject10 != null)
              {
                localObject2 = localObject5;
                if (((TLRPC.User)localObject10).photo != null)
                {
                  localObject2 = localObject5;
                  if (((TLRPC.User)localObject10).photo.photo_small != null)
                  {
                    localObject2 = localObject5;
                    if (((TLRPC.User)localObject10).photo.photo_small.volume_id != 0L)
                    {
                      localObject2 = localObject5;
                      if (((TLRPC.User)localObject10).photo.photo_small.local_id != 0)
                      {
                        localObject2 = ((TLRPC.User)localObject10).photo.photo_small;
                        continue;
                        localObject2 = localObject5;
                        if (this.pushDialogs.size() == 1)
                        {
                          ((Intent)localObject4).putExtra("encId", (int)(l2 >> 32));
                          localObject2 = localObject5;
                          continue;
                          label2299:
                          if (localChat != null)
                          {
                            localObject4 = localChat.title;
                          }
                          else
                          {
                            localObject4 = UserObject.getUserName((TLRPC.User)localObject10);
                            continue;
                            label2324:
                            localObject8 = LocaleController.formatString("NotificationMessagesPeopleDisplayOrder", 2131166095, new Object[] { LocaleController.formatPluralString("NewMessages", this.total_unread_count), LocaleController.formatPluralString("FromChats", this.pushDialogs.size()) });
                            continue;
                            label2371:
                            localObject4 = ((String)localObject8).replace((String)localObject7 + ": ", "").replace((String)localObject7 + " ", "");
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      label2432:
      localBuilder.setContentText((CharSequence)localObject8);
      localObject10 = new NotificationCompat.InboxStyle();
      ((NotificationCompat.InboxStyle)localObject10).setBigContentTitle((CharSequence)localObject7);
      i1 = Math.min(10, this.pushMessages.size());
      n = 0;
    }
    Object localObject4 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
    ((Intent)localObject4).setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
    ((Intent)localObject4).setFlags(32768);
    Object localObject9;
    Object localObject8;
    NotificationCompat.Builder localBuilder;
    if ((int)l2 != 0) {
      if (this.pushDialogs.size() == 1)
      {
        if (i3 != 0) {
          ((Intent)localObject4).putExtra("chatId", i3);
        }
      }
      else
      {
        if (AndroidUtilities.needShowPasscode(false)) {
          break label3086;
        }
        if (!UserConfig.isWaitingForPasscodeEnter) {
          break label2077;
        }
        break label3086;
        localObject9 = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, (Intent)localObject4, 1073741824);
        m = 1;
        if (((int)l2 != 0) && (this.pushDialogs.size() <= 1) && (!AndroidUtilities.needShowPasscode(false)) && (!UserConfig.isWaitingForPasscodeEnter)) {
          break label2299;
        }
        localObject4 = LocaleController.getString("AppName", 2131165338);
        m = 0;
        if (this.pushDialogs.size() != 1) {
          break label2324;
        }
        localObject8 = LocaleController.formatPluralString("NewMessages", this.total_unread_count);
        localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
        bool2 = localSharedPreferences.contains("hide_" + String.valueOf(l2));
        localObject5 = localObject9;
        localObject7 = localObject4;
        if (bool2)
        {
          localObject5 = localObject9;
          localObject7 = localObject4;
          if (!localSharedPreferences.getBoolean("chat_unlocked", false))
          {
            localObject7 = LocaleController.getString("AppName", 2131165338);
            localObject5 = null;
          }
        }
        localBuilder = new NotificationCompat.Builder(ApplicationLoader.applicationContext).setContentTitle((CharSequence)localObject7).setSmallIcon(2130838014).setAutoCancel(true).setNumber(this.total_unread_count).setContentIntent((PendingIntent)localObject5).setGroup("messages").setGroupSummary(true).setColor(-13851168);
        localBuilder.setCategory("msg");
        if ((localChat == null) && (localObject10 != null) && (((TLRPC.User)localObject10).phone != null) && (((TLRPC.User)localObject10).phone.length() > 0)) {
          localBuilder.addPerson("tel:+" + ((TLRPC.User)localObject10).phone);
        }
        i = 2;
        localObject4 = null;
        if (this.pushMessages.size() != 1) {
          break label2432;
        }
        localObject4 = (MessageObject)this.pushMessages.get(0);
        localObject5 = getStringForMessage((MessageObject)localObject4, false);
        localObject8 = localObject5;
        if (!((MessageObject)localObject4).messageOwner.silent) {
          break label3140;
        }
        i = 1;
        if (localObject8 == null) {
          break label3143;
        }
        localObject4 = localObject8;
        if (m != 0)
        {
          if (localChat == null) {
            break label2371;
          }
          localObject4 = ((String)localObject8).replace(" @ " + (String)localObject7, "");
        }
        localBuilder.setContentText((CharSequence)localObject4);
        localBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText((CharSequence)localObject4));
        localObject4 = localObject5;
        localObject5 = new Intent(ApplicationLoader.applicationContext, NotificationDismissReceiver.class);
        ((Intent)localObject5).putExtra("messageDate", localMessageObject1.messageOwner.date);
        localBuilder.setDeleteIntent(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 1, (Intent)localObject5, 134217728));
        if ((!bool2) || (localSharedPreferences.getBoolean("chat_unlocked", false))) {
          break label2720;
        }
        localBuilder.setLargeIcon(BitmapFactory.decodeResource(ApplicationLoader.applicationContext.getResources(), 2130837860));
        break label3092;
        localBuilder.setPriority(-1);
        if ((i == 1) || (i2 != 0)) {
          break label2977;
        }
        if ((ApplicationLoader.mainInterfacePaused) || (bool1))
        {
          localObject1 = localObject4;
          if (((String)localObject4).length() > 100) {
            localObject1 = ((String)localObject4).substring(0, 100).replace('\n', ' ').trim() + "...";
          }
          localBuilder.setTicker((CharSequence)localObject1);
        }
        if ((!MediaController.getInstance().isRecordingAudio()) && (localObject6 != null) && (!((String)localObject6).equals("NoSound")))
        {
          if (!((String)localObject6).equals(str)) {
            break label2886;
          }
          localBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, 5);
        }
        if (i4 != 0) {
          localBuilder.setLights(i4, 1000, 1000);
        }
        if ((k != 2) && (!MediaController.getInstance().isRecordingAudio())) {
          break label2901;
        }
        localBuilder.setVibrate(new long[] { 0L, 0L });
        break label2949;
      }
    }
    for (;;)
    {
      showExtraNotifications(localBuilder, paramBoolean);
      this.notificationManager.notify(1, localBuilder.build());
      scheduleNotificationRepeat();
      return;
      i3 = localMessageObject1.messageOwner.to_id.channel_id;
      break;
      m = i;
      if (i != UserConfig.getClientUserId()) {
        break label184;
      }
      m = localMessageObject1.messageOwner.from_id;
      break label184;
      if (((Point)localObject1).y + i4 < System.currentTimeMillis() / 1000L)
      {
        ((Point)localObject1).set(1, (int)(System.currentTimeMillis() / 1000L));
        i2 = i;
        break label465;
      }
      i2 = ((Point)localObject1).x;
      if (i2 >= i1) {
        break label3104;
      }
      ((Point)localObject1).set(i2 + 1, (int)(System.currentTimeMillis() / 1000L));
      i2 = i;
      break label465;
      localObject1 = localObject4;
      if (localObject4 != null) {
        break label653;
      }
      localObject1 = ((SharedPreferences)localObject7).getString("GroupSoundPath", str);
      break label653;
      localObject1 = localObject4;
      i = i6;
      k = n;
      if (m == 0) {
        break label693;
      }
      if ((localObject4 != null) && (((String)localObject4).equals(str))) {
        localObject1 = null;
      }
      for (;;)
      {
        i = ((SharedPreferences)localObject7).getInt("vibrate_messages", 0);
        k = ((SharedPreferences)localObject7).getInt("priority_group", 1);
        j = ((SharedPreferences)localObject7).getInt("MessagesLed", -16711936);
        break;
        localObject1 = localObject4;
        if (localObject4 == null) {
          localObject1 = ((SharedPreferences)localObject7).getString("GlobalSoundPath", str);
        }
      }
      Object localObject2;
      label2474:
      if (n < i1)
      {
        MessageObject localMessageObject2 = (MessageObject)this.pushMessages.get(n);
        localObject9 = getStringForMessage(localMessageObject2, false);
        localObject5 = localObject4;
        j = i;
        if (localObject9 == null) {
          break label3145;
        }
        if (localMessageObject2.messageOwner.date <= i7)
        {
          localObject5 = localObject4;
          j = i;
          break label3145;
        }
        j = i;
        if (i == 2)
        {
          localObject4 = localObject9;
          if (!localMessageObject2.messageOwner.silent) {
            break label3161;
          }
          j = 1;
        }
        label2565:
        localObject5 = localObject9;
        if (this.pushDialogs.size() == 1)
        {
          localObject5 = localObject9;
          if (m != 0) {
            if (localChat == null) {
              break label2640;
            }
          }
        }
        label2640:
        for (localObject5 = ((String)localObject9).replace(" @ " + (String)localObject7, "");; localObject5 = ((String)localObject9).replace((String)localObject7 + ": ", "").replace((String)localObject7 + " ", ""))
        {
          ((NotificationCompat.InboxStyle)localObject10).addLine((CharSequence)localObject5);
          localObject5 = localObject4;
          break;
        }
      }
      ((NotificationCompat.InboxStyle)localObject10).setSummaryText((CharSequence)localObject8);
      localBuilder.setStyle((NotificationCompat.Style)localObject10);
      break label1482;
      label2720:
      if (localObject2 == null) {
        break label3092;
      }
      localObject5 = ImageLoader.getInstance().getImageFromMemory((TLObject)localObject2, null, "50_50");
      if (localObject5 != null)
      {
        localBuilder.setLargeIcon(((BitmapDrawable)localObject5).getBitmap());
        break label3092;
      }
      for (;;)
      {
        float f;
        try
        {
          f = 160.0F / AndroidUtilities.dp(50.0F);
          localObject5 = new BitmapFactory.Options();
          if (f < 1.0F)
          {
            j = 1;
            ((BitmapFactory.Options)localObject5).inSampleSize = j;
            localObject2 = BitmapFactory.decodeFile(FileLoader.getPathToAttach((TLObject)localObject2, true).toString(), (BitmapFactory.Options)localObject5);
            if (localObject2 == null) {
              break;
            }
            localBuilder.setLargeIcon((Bitmap)localObject2);
          }
        }
        catch (Throwable localThrowable) {}
        j = (int)f;
      }
      label2839:
      if (i5 == 0)
      {
        localBuilder.setPriority(0);
        break label1579;
      }
      if (i5 == 1)
      {
        localBuilder.setPriority(1);
        break label1579;
      }
      if (i5 != 2) {
        break label1579;
      }
      localBuilder.setPriority(2);
      break label1579;
      label2886:
      localBuilder.setSound(Uri.parse((String)localObject6), 5);
      break label1706;
      label2901:
      if (k != 1) {
        break label3167;
      }
      localBuilder.setVibrate(new long[] { 0L, 100L, 0L, 100L });
    }
    for (;;)
    {
      localBuilder.setDefaults(2);
      break label1757;
      label2949:
      if (k != 3) {
        break label1757;
      }
      localBuilder.setVibrate(new long[] { 0L, 1000L });
      break label1757;
      label2977:
      localBuilder.setVibrate(new long[] { 0L, 0L });
      break label1757;
      label2997:
      i = i1;
      if (i2 != 0) {
        break label309;
      }
      label3005:
      i = 1;
      break label309;
      label3010:
      if (i5 != 3) {
        k = i5;
      }
      j = i;
      i6 = i4;
      if (i == 4)
      {
        i6 = 1;
        j = 0;
      }
      if (((j != 2) || ((i1 != 1) && (i1 != 3) && (i1 != 5))) && ((j == 2) || (i1 != 2)) && (i1 == 0)) {
        break label762;
      }
      j = i1;
      break label762;
      label3086:
      Object localObject3 = null;
      break label1046;
      label3092:
      if (!paramBoolean) {
        break label1572;
      }
      if (i != 1) {
        break label2839;
      }
      break label1572;
      label3104:
      i2 = 1;
      break label465;
      label3110:
      localObject4 = localObject3;
      i1 = j;
      i = k;
      if (k != 2) {
        break label814;
      }
      i = 1;
      localObject4 = localObject3;
      i1 = j;
      break label814;
      label3140:
      i = 0;
      break label1402;
      label3143:
      break;
      label3145:
      n += 1;
      localObject4 = localObject5;
      i = j;
      break label2474;
      label3161:
      j = 0;
      break label2565;
      label3167:
      if (k != 0) {
        if (k != 4) {
          break label1756;
        }
      }
    }
  }
  
  public static void updateServerNotificationsSettings(long paramLong)
  {
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
    if ((int)paramLong == 0) {
      return;
    }
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
    TLRPC.TL_account_updateNotifySettings localTL_account_updateNotifySettings = new TLRPC.TL_account_updateNotifySettings();
    localTL_account_updateNotifySettings.settings = new TLRPC.TL_inputPeerNotifySettings();
    localTL_account_updateNotifySettings.settings.sound = "default";
    int i = localSharedPreferences.getInt("notify2_" + paramLong, 0);
    if (i == 3)
    {
      localTL_account_updateNotifySettings.settings.mute_until = localSharedPreferences.getInt("notifyuntil_" + paramLong, 0);
      localTL_account_updateNotifySettings.settings.show_previews = localSharedPreferences.getBoolean("preview_" + paramLong, true);
      localTL_account_updateNotifySettings.settings.silent = localSharedPreferences.getBoolean("silent_" + paramLong, false);
      localTL_account_updateNotifySettings.peer = new TLRPC.TL_inputNotifyPeer();
      ((TLRPC.TL_inputNotifyPeer)localTL_account_updateNotifySettings.peer).peer = MessagesController.getInputPeer((int)paramLong);
      ConnectionsManager.getInstance().sendRequest(localTL_account_updateNotifySettings, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
      });
      return;
    }
    TLRPC.TL_inputPeerNotifySettings localTL_inputPeerNotifySettings = localTL_account_updateNotifySettings.settings;
    if (i != 2) {}
    for (i = 0;; i = Integer.MAX_VALUE)
    {
      localTL_inputPeerNotifySettings.mute_until = i;
      break;
    }
  }
  
  public void cleanup()
  {
    this.popupMessages.clear();
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        NotificationsController.access$302(NotificationsController.this, 0L);
        NotificationsController.access$402(NotificationsController.this, 0);
        NotificationsController.access$502(NotificationsController.this, 0);
        NotificationsController.this.pushMessages.clear();
        NotificationsController.this.pushMessagesDict.clear();
        NotificationsController.this.pushDialogs.clear();
        NotificationsController.this.wearNotificationsIds.clear();
        NotificationsController.this.autoNotificationsIds.clear();
        NotificationsController.this.delayedPushMessages.clear();
        NotificationsController.access$1102(NotificationsController.this, false);
        NotificationsController.access$1202(NotificationsController.this, 0);
        try
        {
          if (NotificationsController.this.notificationDelayWakelock.isHeld()) {
            NotificationsController.this.notificationDelayWakelock.release();
          }
          NotificationsController.this.setBadge(0);
          SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
          localEditor.clear();
          localEditor.commit();
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
    });
  }
  
  public void playOutChatSound()
  {
    if ((!this.inChatSoundEnabled) || (MediaController.getInstance().isRecordingAudio())) {}
    for (;;)
    {
      return;
      try
      {
        int i = this.audioManager.getRingerMode();
        if (i == 0) {}
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
      }
    }
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          if (Math.abs(System.currentTimeMillis() - NotificationsController.this.lastSoundOutPlay) <= 100L) {
            return;
          }
          NotificationsController.access$2902(NotificationsController.this, System.currentTimeMillis());
          if (NotificationsController.this.soundPool == null)
          {
            NotificationsController.access$2402(NotificationsController.this, new SoundPool(3, 1, 0));
            NotificationsController.this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
            {
              public void onLoadComplete(SoundPool paramAnonymous2SoundPool, int paramAnonymous2Int1, int paramAnonymous2Int2)
              {
                if (paramAnonymous2Int2 == 0) {
                  paramAnonymous2SoundPool.play(paramAnonymous2Int1, 1.0F, 1.0F, 1, 0, 1.0F);
                }
              }
            });
          }
          if ((NotificationsController.this.soundOut == 0) && (!NotificationsController.this.soundOutLoaded))
          {
            NotificationsController.access$3102(NotificationsController.this, true);
            NotificationsController.access$3002(NotificationsController.this, NotificationsController.this.soundPool.load(ApplicationLoader.applicationContext, 2131099649, 1));
          }
          if (NotificationsController.this.soundOut != 0)
          {
            NotificationsController.this.soundPool.play(NotificationsController.this.soundOut, 1.0F, 1.0F, 1, 0, 1.0F);
            return;
          }
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public void processDialogsUpdateRead(final HashMap<Long, Integer> paramHashMap)
  {
    if (this.popupMessages.isEmpty()) {}
    for (final ArrayList localArrayList = null;; localArrayList = new ArrayList(this.popupMessages))
    {
      this.notificationsQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          int k = NotificationsController.this.total_unread_count;
          SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
          Iterator localIterator = paramHashMap.entrySet().iterator();
          while (localIterator.hasNext())
          {
            localObject = (Map.Entry)localIterator.next();
            long l3 = ((Long)((Map.Entry)localObject).getKey()).longValue();
            int j = NotificationsController.this.getNotifyOverride(localSharedPreferences, l3);
            int i = j;
            Integer localInteger1;
            if (NotificationsController.this.notifyCheck)
            {
              localInteger1 = (Integer)NotificationsController.this.pushDialogsOverrideMention.get(Long.valueOf(l3));
              i = j;
              if (localInteger1 != null)
              {
                i = j;
                if (localInteger1.intValue() == 1)
                {
                  NotificationsController.this.pushDialogsOverrideMention.put(Long.valueOf(l3), Integer.valueOf(0));
                  i = 1;
                }
              }
            }
            if ((i != 2) && (((localSharedPreferences.getBoolean("EnableAll", true)) && (((int)l3 >= 0) || (localSharedPreferences.getBoolean("EnableGroup", true)))) || (i != 0))) {}
            for (i = 1;; i = 0)
            {
              Integer localInteger2 = (Integer)NotificationsController.this.pushDialogs.get(Long.valueOf(l3));
              localInteger1 = (Integer)((Map.Entry)localObject).getValue();
              if (localInteger1.intValue() == 0) {
                NotificationsController.this.smartNotificationsDialogs.remove(Long.valueOf(l3));
              }
              localObject = localInteger1;
              if (localInteger1.intValue() < 0)
              {
                if (localInteger2 == null) {
                  break;
                }
                localObject = Integer.valueOf(localInteger2.intValue() + localInteger1.intValue());
              }
              if (((i != 0) || (((Integer)localObject).intValue() == 0)) && (localInteger2 != null)) {
                NotificationsController.access$420(NotificationsController.this, localInteger2.intValue());
              }
              if (((Integer)localObject).intValue() != 0) {
                break label588;
              }
              NotificationsController.this.pushDialogs.remove(Long.valueOf(l3));
              NotificationsController.this.pushDialogsOverrideMention.remove(Long.valueOf(l3));
              for (i = 0; i < NotificationsController.this.pushMessages.size(); i = j + 1)
              {
                localObject = (MessageObject)NotificationsController.this.pushMessages.get(i);
                j = i;
                if (((MessageObject)localObject).getDialogId() == l3)
                {
                  if (NotificationsController.this.isPersonalMessage((MessageObject)localObject)) {
                    NotificationsController.access$510(NotificationsController.this);
                  }
                  NotificationsController.this.pushMessages.remove(i);
                  i -= 1;
                  NotificationsController.this.delayedPushMessages.remove(localObject);
                  long l2 = ((MessageObject)localObject).messageOwner.id;
                  long l1 = l2;
                  if (((MessageObject)localObject).messageOwner.to_id.channel_id != 0) {
                    l1 = l2 | ((MessageObject)localObject).messageOwner.to_id.channel_id << 32;
                  }
                  NotificationsController.this.pushMessagesDict.remove(Long.valueOf(l1));
                  j = i;
                  if (localArrayList != null)
                  {
                    localArrayList.remove(localObject);
                    j = i;
                  }
                }
              }
            }
            if ((localArrayList != null) && (NotificationsController.this.pushMessages.isEmpty()) && (!localArrayList.isEmpty()))
            {
              localArrayList.clear();
              continue;
              label588:
              if (i != 0)
              {
                NotificationsController.access$412(NotificationsController.this, ((Integer)localObject).intValue());
                NotificationsController.this.pushDialogs.put(Long.valueOf(l3), localObject);
              }
            }
          }
          if (localArrayList != null) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationsController.this.popupMessages = NotificationsController.8.this.val$popupArray;
              }
            });
          }
          if (k != NotificationsController.this.total_unread_count)
          {
            if (!NotificationsController.this.notifyCheck)
            {
              NotificationsController.this.delayedPushMessages.clear();
              NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
            }
          }
          else
          {
            NotificationsController.access$1102(NotificationsController.this, false);
            if (localSharedPreferences.getBoolean("badgeNumber", true)) {
              NotificationsController.this.setBadge(NotificationsController.this.total_unread_count);
            }
            return;
          }
          Object localObject = NotificationsController.this;
          if (NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance().getCurrentTime()) {}
          for (boolean bool = true;; bool = false)
          {
            ((NotificationsController)localObject).scheduleNotificationDelay(bool);
            break;
          }
        }
      });
      return;
    }
  }
  
  public void processLoadedUnreadMessages(final HashMap<Long, Integer> paramHashMap, final ArrayList<TLRPC.Message> paramArrayList, ArrayList<TLRPC.User> paramArrayList1, ArrayList<TLRPC.Chat> paramArrayList2, ArrayList<TLRPC.EncryptedChat> paramArrayList3)
  {
    MessagesController.getInstance().putUsers(paramArrayList1, true);
    MessagesController.getInstance().putChats(paramArrayList2, true);
    MessagesController.getInstance().putEncryptedChats(paramArrayList3, true);
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        NotificationsController.this.pushDialogs.clear();
        NotificationsController.this.pushMessages.clear();
        NotificationsController.this.pushMessagesDict.clear();
        NotificationsController.access$402(NotificationsController.this, 0);
        NotificationsController.access$502(NotificationsController.this, 0);
        SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
        HashMap localHashMap = new HashMap();
        long l1;
        Object localObject2;
        Boolean localBoolean;
        int i;
        if (paramArrayList != null)
        {
          localIterator = paramArrayList.iterator();
          while (localIterator.hasNext())
          {
            localObject1 = (TLRPC.Message)localIterator.next();
            l1 = ((TLRPC.Message)localObject1).id;
            long l2 = l1;
            if (((TLRPC.Message)localObject1).to_id.channel_id != 0) {
              l2 = l1 | ((TLRPC.Message)localObject1).to_id.channel_id << 32;
            }
            if (!NotificationsController.this.pushMessagesDict.containsKey(Long.valueOf(l2)))
            {
              localObject2 = new MessageObject((TLRPC.Message)localObject1, null, false);
              if (NotificationsController.this.isPersonalMessage((MessageObject)localObject2)) {
                NotificationsController.access$508(NotificationsController.this);
              }
              long l3 = ((MessageObject)localObject2).getDialogId();
              l1 = l3;
              if (((MessageObject)localObject2).messageOwner.mentioned) {
                l1 = ((MessageObject)localObject2).messageOwner.from_id;
              }
              localBoolean = (Boolean)localHashMap.get(Long.valueOf(l1));
              localObject1 = localBoolean;
              if (localBoolean == null)
              {
                i = NotificationsController.this.getNotifyOverride(localSharedPreferences, l1);
                if ((i == 2) || (((!localSharedPreferences.getBoolean("EnableAll", true)) || (((int)l1 < 0) && (!localSharedPreferences.getBoolean("EnableGroup", true)))) && (i == 0))) {
                  break label408;
                }
              }
              label408:
              for (bool = true;; bool = false)
              {
                localObject1 = Boolean.valueOf(bool);
                localHashMap.put(Long.valueOf(l1), localObject1);
                if ((!((Boolean)localObject1).booleanValue()) || ((l1 == NotificationsController.this.opened_dialog_id) && (ApplicationLoader.isScreenOn))) {
                  break;
                }
                NotificationsController.this.pushMessagesDict.put(Long.valueOf(l2), localObject2);
                NotificationsController.this.pushMessages.add(0, localObject2);
                if (l3 == l1) {
                  break;
                }
                NotificationsController.this.pushDialogsOverrideMention.put(Long.valueOf(l3), Integer.valueOf(1));
                break;
              }
            }
          }
        }
        Iterator localIterator = paramHashMap.entrySet().iterator();
        if (localIterator.hasNext())
        {
          localObject2 = (Map.Entry)localIterator.next();
          l1 = ((Long)((Map.Entry)localObject2).getKey()).longValue();
          localBoolean = (Boolean)localHashMap.get(Long.valueOf(l1));
          localObject1 = localBoolean;
          if (localBoolean == null)
          {
            int j = NotificationsController.this.getNotifyOverride(localSharedPreferences, l1);
            localObject1 = (Integer)NotificationsController.this.pushDialogsOverrideMention.get(Long.valueOf(l1));
            i = j;
            if (localObject1 != null)
            {
              i = j;
              if (((Integer)localObject1).intValue() == 1)
              {
                NotificationsController.this.pushDialogsOverrideMention.put(Long.valueOf(l1), Integer.valueOf(0));
                i = 1;
              }
            }
            if ((i == 2) || (((!localSharedPreferences.getBoolean("EnableAll", true)) || (((int)l1 < 0) && (!localSharedPreferences.getBoolean("EnableGroup", true)))) && (i == 0))) {
              break label671;
            }
          }
          label671:
          for (bool = true;; bool = false)
          {
            localObject1 = Boolean.valueOf(bool);
            localHashMap.put(Long.valueOf(l1), localObject1);
            if (!((Boolean)localObject1).booleanValue()) {
              break;
            }
            i = ((Integer)((Map.Entry)localObject2).getValue()).intValue();
            NotificationsController.this.pushDialogs.put(Long.valueOf(l1), Integer.valueOf(i));
            NotificationsController.access$412(NotificationsController.this, i);
            break;
          }
        }
        if (NotificationsController.this.total_unread_count == 0) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationsController.this.popupMessages.clear();
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
            }
          });
        }
        Object localObject1 = NotificationsController.this;
        if (SystemClock.uptimeMillis() / 1000L < 60L) {}
        for (boolean bool = true;; bool = false)
        {
          ((NotificationsController)localObject1).showOrUpdateNotification(bool);
          if (localSharedPreferences.getBoolean("badgeNumber", true)) {
            NotificationsController.this.setBadge(NotificationsController.this.total_unread_count);
          }
          return;
        }
      }
    });
  }
  
  public void processNewMessages(final ArrayList<MessageObject> paramArrayList, final boolean paramBoolean)
  {
    if (paramArrayList.isEmpty()) {
      return;
    }
    final ArrayList localArrayList = new ArrayList(this.popupMessages);
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = 0;
        int i1 = localArrayList.size();
        HashMap localHashMap = new HashMap();
        SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
        boolean bool2 = localSharedPreferences.getBoolean("PinnedMessages", true);
        final int m = 0;
        int k = 0;
        if (k < paramArrayList.size())
        {
          MessageObject localMessageObject = (MessageObject)paramArrayList.get(k);
          long l1 = localMessageObject.messageOwner.id;
          long l2 = l1;
          if (localMessageObject.messageOwner.to_id.channel_id != 0) {
            l2 = l1 | localMessageObject.messageOwner.to_id.channel_id << 32;
          }
          if (NotificationsController.this.pushMessagesDict.containsKey(Long.valueOf(l2))) {}
          long l3;
          label182:
          do
          {
            for (;;)
            {
              k += 1;
              break;
              l3 = localMessageObject.getDialogId();
              if ((l3 != NotificationsController.this.opened_dialog_id) || (!ApplicationLoader.isScreenOn)) {
                break label182;
              }
              NotificationsController.this.playInChatSound();
            }
            l1 = l3;
            if (!localMessageObject.messageOwner.mentioned) {
              break label227;
            }
          } while ((!bool2) && ((localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage)));
          l1 = localMessageObject.messageOwner.from_id;
          label227:
          if (NotificationsController.this.isPersonalMessage(localMessageObject)) {
            NotificationsController.access$508(NotificationsController.this);
          }
          int n = 1;
          Boolean localBoolean = (Boolean)localHashMap.get(Long.valueOf(l1));
          int j;
          label273:
          Object localObject;
          if ((int)l1 < 0)
          {
            j = 1;
            if ((int)l1 != 0) {
              break label521;
            }
            i = 0;
            localObject = localBoolean;
            if (localBoolean == null)
            {
              m = NotificationsController.this.getNotifyOverride(localSharedPreferences, l1);
              if ((m == 2) || (((!localSharedPreferences.getBoolean("EnableAll", true)) || ((j != 0) && (!localSharedPreferences.getBoolean("EnableGroup", true)))) && (m == 0))) {
                break label550;
              }
            }
          }
          label521:
          label550:
          for (boolean bool1 = true;; bool1 = false)
          {
            localObject = Boolean.valueOf(bool1);
            localHashMap.put(Long.valueOf(l1), localObject);
            j = i;
            if (i != 0)
            {
              j = i;
              if (localMessageObject.messageOwner.to_id.channel_id != 0)
              {
                j = i;
                if (!localMessageObject.isMegagroup()) {
                  j = 0;
                }
              }
            }
            i = n;
            m = j;
            if (!((Boolean)localObject).booleanValue()) {
              break;
            }
            if (j != 0) {
              localArrayList.add(0, localMessageObject);
            }
            NotificationsController.this.delayedPushMessages.add(localMessageObject);
            NotificationsController.this.pushMessages.add(0, localMessageObject);
            NotificationsController.this.pushMessagesDict.put(Long.valueOf(l2), localMessageObject);
            i = n;
            m = j;
            if (l3 == l1) {
              break;
            }
            NotificationsController.this.pushDialogsOverrideMention.put(Long.valueOf(l3), Integer.valueOf(1));
            i = n;
            m = j;
            break;
            j = 0;
            break label273;
            if (j != 0) {}
            for (localObject = "popupGroup";; localObject = "popupAll")
            {
              i = localSharedPreferences.getInt((String)localObject, 0);
              break;
            }
          }
        }
        if (i != 0) {
          NotificationsController.access$1102(NotificationsController.this, paramBoolean);
        }
        if ((!localArrayList.isEmpty()) && (i1 != localArrayList.size()) && (!AndroidUtilities.needShowPasscode(false))) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationsController.this.popupMessages = NotificationsController.7.this.val$popupArray;
              if ((ApplicationLoader.mainInterfacePaused) || ((!ApplicationLoader.isScreenOn) && (!UserConfig.isWaitingForPasscodeEnter)))
              {
                Object localObject = (MessageObject)NotificationsController.7.this.val$messageObjects.get(0);
                if ((m == 3) || ((m == 1) && (ApplicationLoader.isScreenOn)) || ((m == 2) && (!ApplicationLoader.isScreenOn)))
                {
                  localObject = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
                  ((Intent)localObject).setFlags(268763140);
                  ApplicationLoader.applicationContext.startActivity((Intent)localObject);
                }
              }
            }
          });
        }
      }
    });
  }
  
  public void processReadMessages(final SparseArray<Long> paramSparseArray, final long paramLong, final int paramInt1, int paramInt2, final boolean paramBoolean)
  {
    if (this.popupMessages.isEmpty()) {}
    for (final ArrayList localArrayList = null;; localArrayList = new ArrayList(this.popupMessages))
    {
      this.notificationsQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          int k;
          int j;
          int i;
          MessageObject localMessageObject;
          int m;
          long l2;
          long l1;
          if (localArrayList != null)
          {
            k = localArrayList.size();
            if (paramSparseArray != null) {
              j = 0;
            }
          }
          else
          {
            for (;;)
            {
              if (j >= paramSparseArray.size()) {
                break label275;
              }
              int n = paramSparseArray.keyAt(j);
              long l3 = ((Long)paramSparseArray.get(n)).longValue();
              i = 0;
              for (;;)
              {
                if (i < NotificationsController.this.pushMessages.size())
                {
                  localMessageObject = (MessageObject)NotificationsController.this.pushMessages.get(i);
                  m = i;
                  if (localMessageObject.getDialogId() == n)
                  {
                    m = i;
                    if (localMessageObject.getId() <= (int)l3)
                    {
                      if (NotificationsController.this.isPersonalMessage(localMessageObject)) {
                        NotificationsController.access$510(NotificationsController.this);
                      }
                      if (localArrayList != null) {
                        localArrayList.remove(localMessageObject);
                      }
                      l2 = localMessageObject.messageOwner.id;
                      l1 = l2;
                      if (localMessageObject.messageOwner.to_id.channel_id != 0) {
                        l1 = l2 | localMessageObject.messageOwner.to_id.channel_id << 32;
                      }
                      NotificationsController.this.pushMessagesDict.remove(Long.valueOf(l1));
                      NotificationsController.this.delayedPushMessages.remove(localMessageObject);
                      NotificationsController.this.pushMessages.remove(i);
                      m = i - 1;
                    }
                  }
                  i = m + 1;
                  continue;
                  k = 0;
                  break;
                }
              }
              j += 1;
            }
            label275:
            if ((localArrayList != null) && (NotificationsController.this.pushMessages.isEmpty()) && (!localArrayList.isEmpty())) {
              localArrayList.clear();
            }
          }
          if ((paramLong != 0L) && ((paramInt1 != 0) || (paramBoolean != 0)))
          {
            j = 0;
            if (j < NotificationsController.this.pushMessages.size())
            {
              localMessageObject = (MessageObject)NotificationsController.this.pushMessages.get(j);
              m = j;
              if (localMessageObject.getDialogId() == paramLong)
              {
                i = 0;
                if (paramBoolean == 0) {
                  break label556;
                }
                if (localMessageObject.messageOwner.date <= paramBoolean) {
                  i = 1;
                }
              }
              for (;;)
              {
                m = j;
                if (i != 0)
                {
                  if (NotificationsController.this.isPersonalMessage(localMessageObject)) {
                    NotificationsController.access$510(NotificationsController.this);
                  }
                  NotificationsController.this.pushMessages.remove(j);
                  NotificationsController.this.delayedPushMessages.remove(localMessageObject);
                  if (localArrayList != null) {
                    localArrayList.remove(localMessageObject);
                  }
                  l2 = localMessageObject.messageOwner.id;
                  l1 = l2;
                  if (localMessageObject.messageOwner.to_id.channel_id != 0) {
                    l1 = l2 | localMessageObject.messageOwner.to_id.channel_id << 32;
                  }
                  NotificationsController.this.pushMessagesDict.remove(Long.valueOf(l1));
                  m = j - 1;
                }
                j = m + 1;
                break;
                label556:
                if (!this.val$isPopup)
                {
                  if ((localMessageObject.getId() <= paramInt1) || (paramInt1 < 0)) {
                    i = 1;
                  }
                }
                else if ((localMessageObject.getId() == paramInt1) || (paramInt1 < 0)) {
                  i = 1;
                }
              }
            }
            if ((localArrayList != null) && (NotificationsController.this.pushMessages.isEmpty()) && (!localArrayList.isEmpty())) {
              localArrayList.clear();
            }
          }
          if ((localArrayList != null) && (k != localArrayList.size())) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationsController.this.popupMessages = NotificationsController.6.this.val$popupArray;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
              }
            });
          }
        }
      });
      return;
    }
  }
  
  public void removeDeletedMessagesFromNotifications(final SparseArray<ArrayList<Integer>> paramSparseArray)
  {
    if (this.popupMessages.isEmpty()) {}
    for (final ArrayList localArrayList = null;; localArrayList = new ArrayList(this.popupMessages))
    {
      this.notificationsQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          int k = NotificationsController.this.total_unread_count;
          SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
          int i = 0;
          while (i < paramSparseArray.size())
          {
            int m = paramSparseArray.keyAt(i);
            long l1 = -m;
            ArrayList localArrayList = (ArrayList)paramSparseArray.get(m);
            Object localObject2 = (Integer)NotificationsController.this.pushDialogs.get(Long.valueOf(l1));
            localObject1 = localObject2;
            if (localObject2 == null) {
              localObject1 = Integer.valueOf(0);
            }
            localObject2 = localObject1;
            int j = 0;
            while (j < localArrayList.size())
            {
              long l2 = ((Integer)localArrayList.get(j)).intValue() | m << 32;
              MessageObject localMessageObject = (MessageObject)NotificationsController.this.pushMessagesDict.get(Long.valueOf(l2));
              localObject3 = localObject2;
              if (localMessageObject != null)
              {
                NotificationsController.this.pushMessagesDict.remove(Long.valueOf(l2));
                NotificationsController.this.delayedPushMessages.remove(localMessageObject);
                NotificationsController.this.pushMessages.remove(localMessageObject);
                if (NotificationsController.this.isPersonalMessage(localMessageObject)) {
                  NotificationsController.access$510(NotificationsController.this);
                }
                if (localArrayList != null) {
                  localArrayList.remove(localMessageObject);
                }
                localObject3 = Integer.valueOf(((Integer)localObject2).intValue() - 1);
              }
              j += 1;
              localObject2 = localObject3;
            }
            Object localObject3 = localObject2;
            if (((Integer)localObject2).intValue() <= 0)
            {
              localObject3 = Integer.valueOf(0);
              NotificationsController.this.smartNotificationsDialogs.remove(Long.valueOf(l1));
            }
            if (!((Integer)localObject3).equals(localObject1))
            {
              NotificationsController.access$420(NotificationsController.this, ((Integer)localObject1).intValue());
              NotificationsController.access$412(NotificationsController.this, ((Integer)localObject3).intValue());
              NotificationsController.this.pushDialogs.put(Long.valueOf(l1), localObject3);
            }
            if (((Integer)localObject3).intValue() == 0)
            {
              NotificationsController.this.pushDialogs.remove(Long.valueOf(l1));
              NotificationsController.this.pushDialogsOverrideMention.remove(Long.valueOf(l1));
              if ((localArrayList != null) && (NotificationsController.this.pushMessages.isEmpty()) && (!localArrayList.isEmpty())) {
                localArrayList.clear();
              }
            }
            i += 1;
          }
          if (localArrayList != null) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationsController.this.popupMessages = NotificationsController.5.this.val$popupArray;
              }
            });
          }
          if (k != NotificationsController.this.total_unread_count)
          {
            if (!NotificationsController.this.notifyCheck)
            {
              NotificationsController.this.delayedPushMessages.clear();
              NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
            }
          }
          else
          {
            NotificationsController.access$1102(NotificationsController.this, false);
            if (localSharedPreferences.getBoolean("badgeNumber", true)) {
              NotificationsController.this.setBadge(NotificationsController.this.total_unread_count);
            }
            return;
          }
          Object localObject1 = NotificationsController.this;
          if (NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance().getCurrentTime()) {}
          for (boolean bool = true;; bool = false)
          {
            ((NotificationsController)localObject1).scheduleNotificationDelay(bool);
            break;
          }
        }
      });
      return;
    }
  }
  
  public void removeNotificationsForDialog(long paramLong)
  {
    getInstance().processReadMessages(null, paramLong, 0, Integer.MAX_VALUE, false);
    HashMap localHashMap = new HashMap();
    localHashMap.put(Long.valueOf(paramLong), Integer.valueOf(0));
    getInstance().processDialogsUpdateRead(localHashMap);
  }
  
  protected void repeatNotificationMaybe()
  {
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = Calendar.getInstance().get(11);
        if ((i >= 11) && (i <= 22))
        {
          NotificationsController.this.notificationManager.cancel(1);
          NotificationsController.this.showOrUpdateNotification(true);
          return;
        }
        NotificationsController.this.scheduleNotificationRepeat();
      }
    });
  }
  
  public void setBadgeEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = this.total_unread_count;; i = 0)
    {
      setBadge(i);
      return;
    }
  }
  
  public void setInChatSoundEnabled(boolean paramBoolean)
  {
    this.inChatSoundEnabled = paramBoolean;
  }
  
  public void setLastOnlineFromOtherDevice(final int paramInt)
  {
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        FileLog.e("tmessages", "set last online from other device = " + paramInt);
        NotificationsController.access$1402(NotificationsController.this, paramInt);
      }
    });
  }
  
  public void setOpenedDialogId(final long paramLong)
  {
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        NotificationsController.access$302(NotificationsController.this, paramLong);
      }
    });
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\NotificationsController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */