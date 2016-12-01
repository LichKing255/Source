package org.telegram.ui.Supergram;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputGeoPointEmpty;
import org.telegram.tgnet.TLRPC.TL_inputPhotoCropAuto;
import org.telegram.tgnet.TLRPC.TL_photos_photo;
import org.telegram.tgnet.TLRPC.TL_photos_uploadProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_userProfilePhoto;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.DocumentSelectActivity;
import org.telegram.ui.DocumentSelectActivity.DocumentSelectActivityDelegate;
import org.telegram.ui.Supergram.Theming.MihanTheme;
import org.telegram.ui.Supergram.Theming.ThemingActivity;
import org.telegram.ui.Supergram.Theming.XmlUtils.XmlUtils;
import org.telegram.ui.PhotoViewer;

public class MihanSettingsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private int activeTabsRow;
  private BackupImageView avatarImage;
  private AvatarUpdater avatarUpdater = new AvatarUpdater();
  private int chatBarRow;
  private int confirmatinAudioRow;
  private int countChatsRow;
  private int countNotMutedRow;
  private int defaultTabRow;
  private int directForwardRow;
  private int emptyRow;
  private int enableTabsInDirectFRow;
  private int enableTabsRow;
  private int extraHeight;
  private View extraHeightView;
  private int favAutoDownloadTabRow;
  private int floatingDateRow;
  private int forwardSectionRow;
  private int forwardSectionRow2;
  private int ghostModeDesRow;
  private int ghostModeRow;
  private int hidePhoneDesRow;
  private int hidePhoneRow;
  private int is24HoursRow;
  private int keepChatDesRow;
  private int keepChatRow;
  private ListAdapter listAdapter;
  private ListView listView;
  private int moreSectionRow;
  private int moreSectionRow2;
  private int multiForwardDesRow;
  private int multiForwardRow;
  private TextView nameTextView;
  private TextView onlineTextView;
  private int overscrollRow;
  private int passwordRow;
  private int persianDateRow;
  private int previewStickerRow;
  private int privacySectionRow;
  private int privacySectionRow2;
  private int resetSettingRow;
  private int restoreSettingRow;
  private int rowCount;
  private int saveInSDDesRow;
  private int saveInSDRow;
  private int saveSectionRow;
  private int saveSectionRow2;
  private int saveSettingRow;
  private View shadowView;
  private int showChatUserStatusDesRow;
  private int showChatUserStatusRow;
  private int showExactCountRow;
  private int showMutualDesRow;
  private int showMutualRow;
  private int showPaintingRow;
  private int showTabsCounterRow;
  private int showUserStatusDesRow;
  private int showUserStatusRow;
  private int swipeTabRow;
  private int tabSectionRow;
  private int tabSectionRow2;
  private int tabletModeDesRow;
  private int tabletModeRow;
  private int themeRow;
  private int toolBarRow;
  private int typingStatusRow;
  private int versionRow;
  private int viewSectionRow2;
  
  private void fixLayout()
  {
    if (this.fragmentView == null) {
      return;
    }
    this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        if (MihanSettingsActivity.this.fragmentView != null)
        {
          MihanSettingsActivity.this.needLayout();
          MihanSettingsActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
        }
        return true;
      }
    });
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    int i = localSharedPreferences.getInt("theme_setting_action_color", MihanTheme.getThemeColor(localSharedPreferences));
    int j = localSharedPreferences.getInt("theme_setting_action_gradient", 0);
    int k = localSharedPreferences.getInt("theme_setting_action_gcolor", i);
    Object localObject;
    if (j != 0)
    {
      localObject = MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j));
      this.actionBar.setBackgroundDrawable((Drawable)localObject);
      this.extraHeightView.setBackgroundColor(k);
    }
    for (;;)
    {
      this.avatarImage.setRoundRadius(AndroidUtilities.dp(localSharedPreferences.getInt("theme_setting_action_aradius", 21)));
      this.listView.setBackgroundColor(localSharedPreferences.getInt("theme_setting_list_bgcolor", -1));
      i = localSharedPreferences.getInt("theme_setting_action_icolor", -1);
      localObject = ApplicationLoader.applicationContext.getResources().getDrawable(2130837829);
      MihanTheme.setColorFilter((Drawable)localObject, i);
      this.actionBar.setBackButtonDrawable((Drawable)localObject);
      i = localSharedPreferences.getInt("theme_setting_action_tcolor", i);
      this.nameTextView.setTextColor(i);
      i = MihanTheme.getLighterColor(i, 0.8F);
      this.onlineTextView.setTextColor(localSharedPreferences.getInt("theme_setting_action_stcolor", i));
      return;
      this.actionBar.setBackgroundColor(i);
      this.extraHeightView.setBackgroundColor(i);
    }
  }
  
  private void loadSettings()
  {
    DocumentSelectActivity localDocumentSelectActivity = new DocumentSelectActivity();
    localDocumentSelectActivity.setDelegate(new DocumentSelectActivity.DocumentSelectActivityDelegate()
    {
      public void didSelectFiles(final DocumentSelectActivity paramAnonymousDocumentSelectActivity, final ArrayList<String> paramAnonymousArrayList)
      {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(MihanSettingsActivity.this.getParentActivity());
        localBuilder.setTitle(LocaleController.getString("AppName", 2131165338));
        localBuilder.setMessage(LocaleController.getString("AreYouSureToContinue", 2131166617));
        localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
          {
            paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).edit();
            Object localObject1 = new File((String)paramAnonymousArrayList.get(0));
            for (;;)
            {
              try
              {
                localObject1 = new FileInputStream((File)localObject1);
                try
                {
                  paramAnonymous2DialogInterface.clear();
                  localObject1 = XmlUtils.readMapXml((InputStream)localObject1).entrySet().iterator();
                  if (!((Iterator)localObject1).hasNext()) {
                    continue;
                  }
                  localObject3 = (Map.Entry)((Iterator)localObject1).next();
                  localObject2 = ((Map.Entry)localObject3).getValue();
                  localObject3 = (String)((Map.Entry)localObject3).getKey();
                  if (!(localObject2 instanceof Boolean)) {
                    continue;
                  }
                  paramAnonymous2DialogInterface.putBoolean((String)localObject3, ((Boolean)localObject2).booleanValue());
                  continue;
                  paramAnonymousDocumentSelectActivity.finishFragment();
                }
                catch (Exception paramAnonymous2DialogInterface) {}
              }
              catch (Exception paramAnonymous2DialogInterface)
              {
                Object localObject3;
                Object localObject2;
                continue;
              }
              return;
              if ((localObject2 instanceof Float))
              {
                paramAnonymous2DialogInterface.putFloat((String)localObject3, ((Float)localObject2).floatValue());
              }
              else if ((localObject2 instanceof Integer))
              {
                paramAnonymous2DialogInterface.putInt((String)localObject3, ((Integer)localObject2).intValue());
              }
              else if ((localObject2 instanceof Long))
              {
                paramAnonymous2DialogInterface.putLong((String)localObject3, ((Long)localObject2).longValue());
              }
              else if ((localObject2 instanceof String))
              {
                paramAnonymous2DialogInterface.putString((String)localObject3, (String)localObject2);
                continue;
                paramAnonymous2DialogInterface.commit();
              }
            }
          }
        });
        localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
        MihanSettingsActivity.this.showDialog(localBuilder.create());
      }
      
      public void startDocumentSelectActivity() {}
    });
    presentFragment(localDocumentSelectActivity);
  }
  
  private void needLayout()
  {
    float f1;
    if (this.actionBar.getOccupyStatusBar())
    {
      i = AndroidUtilities.statusBarHeight;
      i += ActionBar.getCurrentActionBarHeight();
      if (this.listView != null)
      {
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
        if (localLayoutParams.topMargin != i)
        {
          localLayoutParams.topMargin = i;
          this.listView.setLayoutParams(localLayoutParams);
          this.extraHeightView.setTranslationY(i);
        }
      }
      if (this.avatarImage != null)
      {
        f1 = this.extraHeight / AndroidUtilities.dp(88.0F);
        this.extraHeightView.setScaleY(f1);
        this.shadowView.setTranslationY(this.extraHeight + i);
        this.avatarImage.setScaleX((18.0F * f1 + 42.0F) / 42.0F);
        this.avatarImage.setScaleY((18.0F * f1 + 42.0F) / 42.0F);
        if (!this.actionBar.getOccupyStatusBar()) {
          break label370;
        }
      }
    }
    label370:
    for (int i = AndroidUtilities.statusBarHeight;; i = 0)
    {
      float f2 = i + ActionBar.getCurrentActionBarHeight() / 2.0F * (1.0F + f1) - 21.0F * AndroidUtilities.density + 27.0F * AndroidUtilities.density * f1;
      this.avatarImage.setTranslationX(-AndroidUtilities.dp(47.0F) * f1);
      this.avatarImage.setTranslationY((float)Math.ceil(f2));
      this.nameTextView.setTranslationX(AndroidUtilities.density * -21.0F * f1);
      this.nameTextView.setTranslationY((float)Math.floor(f2) - (float)Math.ceil(AndroidUtilities.density) + (float)Math.floor(7.0F * AndroidUtilities.density * f1));
      this.onlineTextView.setTranslationX(AndroidUtilities.density * -21.0F * f1);
      this.onlineTextView.setTranslationY((float)Math.floor(f2) + AndroidUtilities.dp(22.0F) + (float)Math.floor(11.0F * AndroidUtilities.density) * f1);
      this.nameTextView.setScaleX(0.12F * f1 + 1.0F);
      this.nameTextView.setScaleY(0.12F * f1 + 1.0F);
      return;
      i = 0;
      break;
    }
  }
  
  private void processSelectedOption(int paramInt)
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
    SharedPreferences.Editor localEditor = localSharedPreferences.edit();
    switch (paramInt)
    {
    }
    for (;;)
    {
      localEditor.commit();
      return;
      if (localSharedPreferences.getBoolean("tab_bot", true))
      {
        localEditor.putInt("default_tab", paramInt);
      }
      else
      {
        localEditor.putInt("default_tab", 6);
        continue;
        if (localSharedPreferences.getBoolean("tab_channel", true))
        {
          localEditor.putInt("default_tab", paramInt);
        }
        else
        {
          localEditor.putInt("default_tab", 6);
          continue;
          if (localSharedPreferences.getBoolean("tab_supergroup", true))
          {
            localEditor.putInt("default_tab", paramInt);
          }
          else
          {
            localEditor.putInt("default_tab", 6);
            continue;
            if (localSharedPreferences.getBoolean("tab_group", true))
            {
              localEditor.putInt("default_tab", paramInt);
            }
            else
            {
              localEditor.putInt("default_tab", 6);
              continue;
              if (localSharedPreferences.getBoolean("tab_contact", true))
              {
                localEditor.putInt("default_tab", paramInt);
              }
              else
              {
                localEditor.putInt("default_tab", 6);
                continue;
                if (localSharedPreferences.getBoolean("tab_favorite", true))
                {
                  localEditor.putInt("default_tab", paramInt);
                }
                else
                {
                  localEditor.putInt("default_tab", 6);
                  continue;
                  localEditor.putInt("default_tab", 6);
                  continue;
                  if (localSharedPreferences.getBoolean("tab_unread", true)) {
                    localEditor.putInt("default_tab", paramInt);
                  } else {
                    localEditor.putInt("default_tab", 6);
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  private void reLunchApp()
  {
    Context localContext = getParentActivity().getBaseContext();
    Object localObject = localContext.getPackageManager().getLaunchIntentForPackage(localContext.getPackageName());
    ((Intent)localObject).addFlags(67108864);
    ((Intent)localObject).addFlags(268435456);
    if (Build.VERSION.SDK_INT >= 11) {
      ((Intent)localObject).addFlags(32768);
    }
    localObject = PendingIntent.getActivity(localContext, 0, (Intent)localObject, 268435456);
    ((AlarmManager)localContext.getSystemService("alarm")).set(1, System.currentTimeMillis() + 1L, (PendingIntent)localObject);
    System.exit(2);
  }
  
  private void resetSettings()
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165338));
    localBuilder.setMessage(LocaleController.getString("AreYouSureToContinue", 2131166617));
    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).edit();
        paramAnonymousDialogInterface.clear();
        paramAnonymousDialogInterface.commit();
        paramAnonymousDialogInterface = Toast.makeText(MihanSettingsActivity.this.getParentActivity(), LocaleController.getString("Done", 2131165634), 1);
        ((TextView)((LinearLayout)paramAnonymousDialogInterface.getView()).getChildAt(0)).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        paramAnonymousDialogInterface.show();
        MihanSettingsActivity.this.restartApp();
      }
    });
    localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
    showDialog(localBuilder.create());
  }
  
  private void restartApp()
  {
    Intent localIntent = getParentActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getParentActivity().getPackageName());
    localIntent.addFlags(67108864);
    localIntent.addFlags(32768);
    getParentActivity().startActivity(localIntent);
  }
  
  private void saveSettings()
  {
    Object localObject1 = new AlertDialog.Builder(getParentActivity());
    ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165338));
    final Object localObject2 = new EditText(getParentActivity());
    ((EditText)localObject2).setTypeface(MihanTheme.getMihanTypeFace());
    ((EditText)localObject2).setHint(LocaleController.getString("MihanSettingsFileName", 2131166817));
    ((EditText)localObject2).setPadding(25, 20, 25, 10);
    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-2, -2);
    localLayoutParams.setMargins(10, 20, 10, 2);
    ((EditText)localObject2).setLayoutParams(localLayoutParams);
    ((AlertDialog.Builder)localObject1).setView((View)localObject2);
    ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
    {
      /* Error */
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        // Byte code:
        //   0: getstatic 34	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   3: ldc 36
        //   5: iconst_0
        //   6: invokevirtual 42	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
        //   9: astore_1
        //   10: new 44	java/io/File
        //   13: dup
        //   14: new 46	java/lang/StringBuilder
        //   17: dup
        //   18: invokespecial 47	java/lang/StringBuilder:<init>	()V
        //   21: invokestatic 53	android/os/Environment:getExternalStorageDirectory	()Ljava/io/File;
        //   24: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   27: ldc 59
        //   29: invokevirtual 62	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   32: invokevirtual 66	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   35: invokespecial 69	java/io/File:<init>	(Ljava/lang/String;)V
        //   38: astore_3
        //   39: aload_3
        //   40: invokevirtual 73	java/io/File:exists	()Z
        //   43: ifne +8 -> 51
        //   46: aload_3
        //   47: invokevirtual 76	java/io/File:mkdirs	()Z
        //   50: pop
        //   51: new 78	java/io/FileOutputStream
        //   54: dup
        //   55: new 44	java/io/File
        //   58: dup
        //   59: aload_3
        //   60: aload_0
        //   61: getfield 21	org/telegram/ui/Supergram/MihanSettingsActivity$7:val$editText	Landroid/widget/EditText;
        //   64: invokevirtual 84	android/widget/EditText:getText	()Landroid/text/Editable;
        //   67: invokevirtual 85	java/lang/Object:toString	()Ljava/lang/String;
        //   70: invokespecial 88	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
        //   73: invokespecial 91	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
        //   76: astore_3
        //   77: aload_1
        //   78: invokeinterface 97 1 0
        //   83: aload_3
        //   84: invokestatic 103	org/telegram/ui/Supergram/Theming/XmlUtils/XmlUtils:writeMapXml	(Ljava/util/Map;Ljava/io/OutputStream;)V
        //   87: aload_0
        //   88: getfield 19	org/telegram/ui/Supergram/MihanSettingsActivity$7:this$0	Lorg/telegram/ui/Supergram/MihanSettingsActivity;
        //   91: invokevirtual 107	org/telegram/ui/Supergram/MihanSettingsActivity:getParentActivity	()Landroid/app/Activity;
        //   94: ldc 109
        //   96: ldc 110
        //   98: invokestatic 116	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
        //   101: iconst_1
        //   102: invokestatic 122	android/widget/Toast:makeText	(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;
        //   105: astore_1
        //   106: aload_1
        //   107: invokevirtual 126	android/widget/Toast:getView	()Landroid/view/View;
        //   110: checkcast 128	android/widget/LinearLayout
        //   113: iconst_0
        //   114: invokevirtual 132	android/widget/LinearLayout:getChildAt	(I)Landroid/view/View;
        //   117: checkcast 134	android/widget/TextView
        //   120: ldc -120
        //   122: invokestatic 142	org/telegram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
        //   125: invokevirtual 146	android/widget/TextView:setTypeface	(Landroid/graphics/Typeface;)V
        //   128: aload_1
        //   129: invokevirtual 149	android/widget/Toast:show	()V
        //   132: return
        //   133: astore_1
        //   134: goto -47 -> 87
        //   137: astore_1
        //   138: goto -51 -> 87
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	141	0	this	7
        //   0	141	1	paramAnonymousDialogInterface	DialogInterface
        //   0	141	2	paramAnonymousInt	int
        //   38	46	3	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   10	51	133	java/lang/Exception
        //   51	77	133	java/lang/Exception
        //   77	87	137	java/lang/Exception
      }
    });
    ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
    localObject2 = ((AlertDialog.Builder)localObject1).create();
    ((Dialog)localObject2).show();
    localObject1 = (Button)((Dialog)localObject2).findViewById(16908313);
    localObject2 = (Button)((Dialog)localObject2).findViewById(16908314);
    ((Button)localObject1).setTypeface(MihanTheme.getMihanTypeFace());
    ((Button)localObject2).setTypeface(MihanTheme.getMihanTypeFace());
  }
  
  private void updateUserData()
  {
    boolean bool2 = true;
    TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
    Object localObject = null;
    TLRPC.FileLocation localFileLocation = null;
    if (localUser.photo != null)
    {
      localObject = localUser.photo.photo_small;
      localFileLocation = localUser.photo.photo_big;
    }
    AvatarDrawable localAvatarDrawable = new AvatarDrawable(localUser, true);
    localAvatarDrawable.setColor(-10708787);
    if (this.avatarImage != null)
    {
      this.avatarImage.setImage((TLObject)localObject, "50_50", localAvatarDrawable);
      localObject = this.avatarImage.getImageReceiver();
      if (PhotoViewer.getInstance().isShowingImage(localFileLocation)) {
        break label174;
      }
      bool1 = true;
      ((ImageReceiver)localObject).setVisible(bool1, false);
      this.nameTextView.setText(UserObject.getUserName(localUser));
      this.onlineTextView.setText(LocaleController.getString("Online", 2131166113));
      localObject = this.avatarImage.getImageReceiver();
      if (PhotoViewer.getInstance().isShowingImage(localFileLocation)) {
        break label179;
      }
    }
    label174:
    label179:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      ((ImageReceiver)localObject).setVisible(bool1, false);
      return;
      bool1 = false;
      break;
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setTitle("");
    this.actionBar.setBackgroundColor(AvatarDrawable.getProfileBackColorForId(5));
    this.actionBar.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId(5));
    this.actionBar.setBackButtonImage(2130837829);
    this.actionBar.setAddToContainer(false);
    this.extraHeight = 88;
    if (AndroidUtilities.isTablet()) {
      this.actionBar.setOccupyStatusBar(false);
    }
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          MihanSettingsActivity.this.finishFragment();
        }
      }
    });
    this.listAdapter = new ListAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext)
    {
      protected boolean drawChild(@NonNull Canvas paramAnonymousCanvas, @NonNull View paramAnonymousView, long paramAnonymousLong)
      {
        if (paramAnonymousView == MihanSettingsActivity.this.listView)
        {
          boolean bool = super.drawChild(paramAnonymousCanvas, paramAnonymousView, paramAnonymousLong);
          if (MihanSettingsActivity.this.parentLayout != null)
          {
            int k = 0;
            int m = getChildCount();
            int i = 0;
            int j = k;
            if (i < m)
            {
              View localView = getChildAt(i);
              if (localView == paramAnonymousView) {}
              while ((!(localView instanceof ActionBar)) || (localView.getVisibility() != 0))
              {
                i += 1;
                break;
              }
              j = k;
              if (((ActionBar)localView).getCastShadows()) {
                j = localView.getMeasuredHeight();
              }
            }
            MihanSettingsActivity.this.parentLayout.drawHeaderShadow(paramAnonymousCanvas, j);
          }
          return bool;
        }
        return super.drawChild(paramAnonymousCanvas, paramAnonymousView, paramAnonymousLong);
      }
    };
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.listView = new ListView(paramContext);
    this.listView.setDivider(null);
    this.listView.setDividerHeight(0);
    this.listView.setVerticalScrollBarEnabled(false);
    AndroidUtilities.setListViewEdgeEffectColor(this.listView, AvatarDrawable.getProfileBackColorForId(5));
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
    this.listView.setAdapter(this.listAdapter);
    this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, final View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        boolean bool2;
        if (paramAnonymousInt == MihanSettingsActivity.this.enableTabsRow)
        {
          paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
          bool2 = paramAnonymousAdapterView.getBoolean("tabs", true);
          paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
          if (!bool2)
          {
            bool1 = true;
            paramAnonymousAdapterView.putBoolean("tabs", bool1);
            if (bool2)
            {
              if (bool2) {
                break label142;
              }
              bool1 = true;
              label71:
              paramAnonymousAdapterView.putBoolean("fav_auto_download", bool1);
            }
            paramAnonymousAdapterView.commit();
            if ((paramAnonymousView instanceof TextCheckCell))
            {
              paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
              if (bool2) {
                break label148;
              }
              bool1 = true;
              label109:
              paramAnonymousAdapterView.setChecked(bool1);
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.MihanUpdateInterface, new Object[] { Integer.valueOf(1) });
          }
        }
        label142:
        label148:
        label623:
        label1091:
        label1144:
        label1150:
        label1250:
        label1262:
        label1356:
        label1368:
        label1460:
        label1472:
        label1585:
        label1591:
        label1720:
        label1732:
        label1826:
        label1838:
        label1932:
        label1944:
        label2326:
        label2420:
        label2432:
        label2526:
        label2538:
        label2653:
        label2694:
        label2700:
        label2800:
        label2812:
        label2906:
        label2918:
        label3118:
        label3130:
        label3224:
        label3236:
        label3330:
        label3342:
        label3436:
        label3448:
        label3542:
        label3554:
        label3648:
        label3660:
        label3754:
        label3766:
        label3879:
        label3885:
        label3987:
        label3999:
        label4093:
        label4105:
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
                              final Object localObject1;
                              Object localObject2;
                              final Object localObject3;
                              do
                              {
                                do
                                {
                                  do
                                  {
                                    do
                                    {
                                      CheckBoxCell localCheckBoxCell;
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
                                                  do
                                                  {
                                                    do
                                                    {
                                                      return;
                                                      bool1 = false;
                                                      break;
                                                      bool1 = false;
                                                      break label71;
                                                      bool1 = false;
                                                      break label109;
                                                      if (paramAnonymousInt != MihanSettingsActivity.this.activeTabsRow) {
                                                        break label623;
                                                      }
                                                    } while (MihanSettingsActivity.this.getParentActivity() == null);
                                                    paramAnonymousView = new boolean[7];
                                                    localObject1 = new BottomSheet.Builder(MihanSettingsActivity.this.getParentActivity());
                                                    ((BottomSheet.Builder)localObject1).setApplyTopPadding(false);
                                                    ((BottomSheet.Builder)localObject1).setApplyBottomPadding(false);
                                                    localObject2 = new LinearLayout(MihanSettingsActivity.this.getParentActivity());
                                                    ((LinearLayout)localObject2).setOrientation(1);
                                                    localObject3 = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                                                    paramAnonymousInt = 0;
                                                    if (paramAnonymousInt < 7)
                                                    {
                                                      paramAnonymousAdapterView = null;
                                                      if (paramAnonymousInt == 0)
                                                      {
                                                        paramAnonymousAdapterView = LocaleController.getString("UnreadTab", 2131166427);
                                                        paramAnonymousView[paramAnonymousInt] = ((SharedPreferences)localObject3).getBoolean("tab_unread", true);
                                                      }
                                                      for (;;)
                                                      {
                                                        localCheckBoxCell = new CheckBoxCell(MihanSettingsActivity.this.getParentActivity());
                                                        localCheckBoxCell.setTag(Integer.valueOf(paramAnonymousInt));
                                                        localCheckBoxCell.setBackgroundResource(2130837932);
                                                        ((LinearLayout)localObject2).addView(localCheckBoxCell, LayoutHelper.createLinear(-1, 48));
                                                        localCheckBoxCell.setText(paramAnonymousAdapterView, "", paramAnonymousView[paramAnonymousInt], true);
                                                        localCheckBoxCell.setOnClickListener(new View.OnClickListener()
                                                        {
                                                          public void onClick(View paramAnonymous2View)
                                                          {
                                                            paramAnonymous2View = (CheckBoxCell)paramAnonymous2View;
                                                            int i = ((Integer)paramAnonymous2View.getTag()).intValue();
                                                            boolean[] arrayOfBoolean = paramAnonymousView;
                                                            if (paramAnonymousView[i] == 0) {}
                                                            for (int j = 1;; j = 0)
                                                            {
                                                              arrayOfBoolean[i] = j;
                                                              paramAnonymous2View.setChecked(paramAnonymousView[i], true);
                                                              return;
                                                            }
                                                          }
                                                        });
                                                        paramAnonymousInt += 1;
                                                        break;
                                                        if (paramAnonymousInt == 1)
                                                        {
                                                          paramAnonymousAdapterView = LocaleController.getString("FavoriteTab", 2131165684);
                                                          paramAnonymousView[paramAnonymousInt] = ((SharedPreferences)localObject3).getBoolean("tab_favorite", true);
                                                        }
                                                        else if (paramAnonymousInt == 2)
                                                        {
                                                          paramAnonymousAdapterView = LocaleController.getString("ContactTab", 2131165562);
                                                          paramAnonymousView[paramAnonymousInt] = ((SharedPreferences)localObject3).getBoolean("tab_contact", true);
                                                        }
                                                        else if (paramAnonymousInt == 3)
                                                        {
                                                          paramAnonymousAdapterView = LocaleController.getString("GroupsTab", 2131165791);
                                                          paramAnonymousView[paramAnonymousInt] = ((SharedPreferences)localObject3).getBoolean("tab_group", true);
                                                        }
                                                        else if (paramAnonymousInt == 4)
                                                        {
                                                          paramAnonymousAdapterView = LocaleController.getString("SuperGroupsTab", 2131166675);
                                                          paramAnonymousView[paramAnonymousInt] = ((SharedPreferences)localObject3).getBoolean("tab_supergroup", true);
                                                        }
                                                        else if (paramAnonymousInt == 5)
                                                        {
                                                          paramAnonymousAdapterView = LocaleController.getString("ChannelTab", 2131165518);
                                                          paramAnonymousView[paramAnonymousInt] = ((SharedPreferences)localObject3).getBoolean("tab_channel", true);
                                                        }
                                                        else if (paramAnonymousInt == 6)
                                                        {
                                                          paramAnonymousAdapterView = LocaleController.getString("RobotTab", 2131166248);
                                                          paramAnonymousView[paramAnonymousInt] = ((SharedPreferences)localObject3).getBoolean("tab_bot", true);
                                                        }
                                                      }
                                                    }
                                                    paramAnonymousAdapterView = new BottomSheet.BottomSheetCell(MihanSettingsActivity.this.getParentActivity(), 1);
                                                    paramAnonymousAdapterView.setBackgroundResource(2130837932);
                                                    paramAnonymousAdapterView.setTextAndIcon(LocaleController.getString("Save", 2131166254).toUpperCase(), 0);
                                                    paramAnonymousAdapterView.setTextColor(-12940081);
                                                    paramAnonymousAdapterView.setOnClickListener(new View.OnClickListener()
                                                    {
                                                      public void onClick(View paramAnonymous2View)
                                                      {
                                                        try
                                                        {
                                                          if (MihanSettingsActivity.this.visibleDialog != null) {
                                                            MihanSettingsActivity.this.visibleDialog.dismiss();
                                                          }
                                                          k = 1;
                                                          j = 0;
                                                          for (;;)
                                                          {
                                                            if (j >= 7) {
                                                              break label401;
                                                            }
                                                            paramAnonymous2View = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).edit();
                                                            if (j != 0) {
                                                              break;
                                                            }
                                                            paramAnonymous2View.putBoolean("tab_unread", paramAnonymousView[j]);
                                                            paramAnonymous2View.commit();
                                                            i = k;
                                                            if (paramAnonymousView[j] != 0) {
                                                              i = k + 1;
                                                            }
                                                            j += 1;
                                                            k = i;
                                                          }
                                                        }
                                                        catch (Exception paramAnonymous2View)
                                                        {
                                                          int k;
                                                          for (;;)
                                                          {
                                                            int j;
                                                            int i;
                                                            FileLog.e("tmessages", paramAnonymous2View);
                                                            continue;
                                                            if (j == 1)
                                                            {
                                                              paramAnonymous2View.putBoolean("tab_favorite", paramAnonymousView[j]);
                                                              paramAnonymous2View.commit();
                                                              i = k;
                                                              if (paramAnonymousView[j] != 0) {
                                                                i = k + 1;
                                                              }
                                                            }
                                                            else if (j == 2)
                                                            {
                                                              paramAnonymous2View.putBoolean("tab_contact", paramAnonymousView[j]);
                                                              paramAnonymous2View.commit();
                                                              i = k;
                                                              if (paramAnonymousView[j] != 0) {
                                                                i = k + 1;
                                                              }
                                                            }
                                                            else if (j == 3)
                                                            {
                                                              paramAnonymous2View.putBoolean("tab_group", paramAnonymousView[j]);
                                                              paramAnonymous2View.commit();
                                                              i = k;
                                                              if (paramAnonymousView[j] != 0) {
                                                                i = k + 1;
                                                              }
                                                            }
                                                            else if (j == 4)
                                                            {
                                                              paramAnonymous2View.putBoolean("tab_supergroup", paramAnonymousView[j]);
                                                              paramAnonymous2View.commit();
                                                              i = k;
                                                              if (paramAnonymousView[j] != 0) {
                                                                i = k + 1;
                                                              }
                                                            }
                                                            else if (j == 5)
                                                            {
                                                              paramAnonymous2View.putBoolean("tab_channel", paramAnonymousView[j]);
                                                              paramAnonymous2View.commit();
                                                              i = k;
                                                              if (paramAnonymousView[j] != 0) {
                                                                i = k + 1;
                                                              }
                                                            }
                                                            else
                                                            {
                                                              i = k;
                                                              if (j == 6)
                                                              {
                                                                paramAnonymous2View.putBoolean("tab_bot", paramAnonymousView[j]);
                                                                paramAnonymous2View.commit();
                                                                i = k;
                                                                if (paramAnonymousView[j] != 0) {
                                                                  i = k + 1;
                                                                }
                                                              }
                                                            }
                                                          }
                                                          label401:
                                                          paramAnonymous2View = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).edit();
                                                          paramAnonymous2View.putInt("tab_count", k);
                                                          paramAnonymous2View.commit();
                                                          MihanSettingsActivity.this.processSelectedOption(localObject3.getInt("default_tab", 6));
                                                          MihanSettingsActivity.this.restartApp();
                                                        }
                                                      }
                                                    });
                                                    ((LinearLayout)localObject2).addView(paramAnonymousAdapterView, LayoutHelper.createLinear(-1, 48));
                                                    ((BottomSheet.Builder)localObject1).setCustomView((View)localObject2);
                                                    MihanSettingsActivity.this.showDialog(((BottomSheet.Builder)localObject1).create());
                                                    return;
                                                    if (paramAnonymousInt == MihanSettingsActivity.this.defaultTabRow)
                                                    {
                                                      paramAnonymousAdapterView = new AlertDialog.Builder(MihanSettingsActivity.this.getParentActivity());
                                                      paramAnonymousView = new ArrayList();
                                                      localObject1 = new ArrayList();
                                                      localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                                                      if (((SharedPreferences)localObject2).getBoolean("tab_unread", true))
                                                      {
                                                        paramAnonymousView.add(LocaleController.getString("UnreadTab", 2131166427));
                                                        ((ArrayList)localObject1).add(Integer.valueOf(7));
                                                      }
                                                      paramAnonymousView.add(LocaleController.getString("AllTab", 2131165315));
                                                      ((ArrayList)localObject1).add(Integer.valueOf(6));
                                                      if (((SharedPreferences)localObject2).getBoolean("tab_favorite", true))
                                                      {
                                                        paramAnonymousView.add(LocaleController.getString("FavoriteTab", 2131165684));
                                                        ((ArrayList)localObject1).add(Integer.valueOf(5));
                                                      }
                                                      if (((SharedPreferences)localObject2).getBoolean("tab_contact", true))
                                                      {
                                                        paramAnonymousView.add(LocaleController.getString("ContactTab", 2131165562));
                                                        ((ArrayList)localObject1).add(Integer.valueOf(4));
                                                      }
                                                      if (((SharedPreferences)localObject2).getBoolean("tab_group", true))
                                                      {
                                                        paramAnonymousView.add(LocaleController.getString("GroupsTab", 2131165791));
                                                        ((ArrayList)localObject1).add(Integer.valueOf(3));
                                                      }
                                                      if (((SharedPreferences)localObject2).getBoolean("tab_supergroup", true))
                                                      {
                                                        paramAnonymousView.add(LocaleController.getString("SuperGroupsTab", 2131166675));
                                                        ((ArrayList)localObject1).add(Integer.valueOf(2));
                                                      }
                                                      if (((SharedPreferences)localObject2).getBoolean("tab_channel", true))
                                                      {
                                                        paramAnonymousView.add(LocaleController.getString("ChannelTab", 2131165518));
                                                        ((ArrayList)localObject1).add(Integer.valueOf(1));
                                                      }
                                                      if (((SharedPreferences)localObject2).getBoolean("tab_bot", true))
                                                      {
                                                        paramAnonymousView.add(LocaleController.getString("RobotTab", 2131166248));
                                                        ((ArrayList)localObject1).add(Integer.valueOf(0));
                                                      }
                                                      paramAnonymousAdapterView.setItems((CharSequence[])paramAnonymousView.toArray(new CharSequence[paramAnonymousView.size()]), new DialogInterface.OnClickListener()
                                                      {
                                                        public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                                                        {
                                                          if ((paramAnonymous2Int < 0) || (paramAnonymous2Int >= localObject1.size())) {}
                                                          do
                                                          {
                                                            return;
                                                            MihanSettingsActivity.this.processSelectedOption(((Integer)localObject1.get(paramAnonymous2Int)).intValue());
                                                          } while (MihanSettingsActivity.this.listView == null);
                                                          MihanSettingsActivity.this.listView.invalidateViews();
                                                        }
                                                      });
                                                      paramAnonymousAdapterView.setTitle(LocaleController.getString("DefaultTab", 2131165598));
                                                      MihanSettingsActivity.this.showDialog(paramAnonymousAdapterView.create());
                                                      return;
                                                    }
                                                    if (paramAnonymousInt == MihanSettingsActivity.this.swipeTabRow)
                                                    {
                                                      paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                                                      bool2 = paramAnonymousAdapterView.getBoolean("swipe_tabs", true);
                                                      paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                                                      if (!bool2)
                                                      {
                                                        bool1 = true;
                                                        paramAnonymousAdapterView.putBoolean("swipe_tabs", bool1);
                                                        paramAnonymousAdapterView.commit();
                                                        if ((paramAnonymousView instanceof TextCheckCell))
                                                        {
                                                          paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                                                          if (bool2) {
                                                            break label1144;
                                                          }
                                                          bool1 = true;
                                                          paramAnonymousAdapterView.setChecked(bool1);
                                                        }
                                                        paramAnonymousAdapterView = NotificationCenter.getInstance();
                                                        paramAnonymousInt = NotificationCenter.MihanUpdateInterface;
                                                        if (bool2) {
                                                          break label1150;
                                                        }
                                                      }
                                                      for (bool1 = true;; bool1 = false)
                                                      {
                                                        paramAnonymousAdapterView.postNotificationName(paramAnonymousInt, new Object[] { Integer.valueOf(2), Boolean.valueOf(bool1) });
                                                        return;
                                                        bool1 = false;
                                                        break;
                                                        bool1 = false;
                                                        break label1091;
                                                      }
                                                    }
                                                    if (paramAnonymousInt != MihanSettingsActivity.this.showTabsCounterRow) {
                                                      break label1262;
                                                    }
                                                    paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                                                    bool2 = paramAnonymousAdapterView.getBoolean("tabs_counter", true);
                                                    paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                                                    if (bool2) {
                                                      break label1250;
                                                    }
                                                    bool1 = true;
                                                    paramAnonymousAdapterView.putBoolean("tabs_counter", bool1);
                                                    paramAnonymousAdapterView.commit();
                                                  } while (!(paramAnonymousView instanceof TextCheckCell));
                                                  paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                                                  if (!bool2) {}
                                                  for (bool1 = true;; bool1 = false)
                                                  {
                                                    paramAnonymousAdapterView.setChecked(bool1);
                                                    return;
                                                    bool1 = false;
                                                    break;
                                                  }
                                                  if (paramAnonymousInt != MihanSettingsActivity.this.countChatsRow) {
                                                    break label1368;
                                                  }
                                                  paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                                                  bool2 = paramAnonymousAdapterView.getBoolean("tabs_count_chats", false);
                                                  paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                                                  if (bool2) {
                                                    break label1356;
                                                  }
                                                  bool1 = true;
                                                  paramAnonymousAdapterView.putBoolean("tabs_count_chats", bool1);
                                                  paramAnonymousAdapterView.commit();
                                                } while (!(paramAnonymousView instanceof TextCheckCell));
                                                paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                                                if (!bool2) {}
                                                for (bool1 = true;; bool1 = false)
                                                {
                                                  paramAnonymousAdapterView.setChecked(bool1);
                                                  return;
                                                  bool1 = false;
                                                  break;
                                                }
                                                if (paramAnonymousInt != MihanSettingsActivity.this.favAutoDownloadTabRow) {
                                                  break label1472;
                                                }
                                                paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                                                bool2 = paramAnonymousAdapterView.getBoolean("fav_auto_download", false);
                                                paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                                                if (bool2) {
                                                  break label1460;
                                                }
                                                bool1 = true;
                                                paramAnonymousAdapterView.putBoolean("fav_auto_download", bool1);
                                                paramAnonymousAdapterView.commit();
                                              } while (!(paramAnonymousView instanceof TextCheckCell));
                                              paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                                              if (!bool2) {}
                                              for (bool1 = true;; bool1 = false)
                                              {
                                                paramAnonymousAdapterView.setChecked(bool1);
                                                return;
                                                bool1 = false;
                                                break;
                                              }
                                              if (paramAnonymousInt == MihanSettingsActivity.this.tabletModeRow)
                                              {
                                                paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                                                bool2 = paramAnonymousAdapterView.getBoolean("tablet_mode", true);
                                                paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                                                if (!bool2)
                                                {
                                                  bool1 = true;
                                                  paramAnonymousAdapterView.putBoolean("tablet_mode", bool1);
                                                  paramAnonymousAdapterView.commit();
                                                  if ((paramAnonymousView instanceof TextCheckCell))
                                                  {
                                                    paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                                                    if (bool2) {
                                                      break label1585;
                                                    }
                                                  }
                                                }
                                                for (bool1 = true;; bool1 = false)
                                                {
                                                  paramAnonymousAdapterView.setChecked(bool1);
                                                  if (!AndroidUtilities.isTablet()) {
                                                    break label1591;
                                                  }
                                                  MihanSettingsActivity.this.reLunchApp();
                                                  return;
                                                  bool1 = false;
                                                  break;
                                                }
                                                MihanSettingsActivity.this.restartApp();
                                                return;
                                              }
                                              if (paramAnonymousInt == MihanSettingsActivity.this.themeRow)
                                              {
                                                MihanSettingsActivity.this.presentFragment(new ThemingActivity());
                                                return;
                                              }
                                              if (paramAnonymousInt != MihanSettingsActivity.this.persianDateRow) {
                                                break label1732;
                                              }
                                              paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                                              bool2 = paramAnonymousAdapterView.getBoolean("persian_date", false);
                                              paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                                              if (bool2) {
                                                break label1720;
                                              }
                                              bool1 = true;
                                              paramAnonymousAdapterView.putBoolean("persian_date", bool1);
                                              paramAnonymousAdapterView.commit();
                                            } while (!(paramAnonymousView instanceof TextCheckCell));
                                            paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                                            if (!bool2) {}
                                            for (bool1 = true;; bool1 = false)
                                            {
                                              paramAnonymousAdapterView.setChecked(bool1);
                                              return;
                                              bool1 = false;
                                              break;
                                            }
                                            if (paramAnonymousInt != MihanSettingsActivity.this.is24HoursRow) {
                                              break label1838;
                                            }
                                            paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                                            bool2 = paramAnonymousAdapterView.getBoolean("enable24HourFormat", false);
                                            paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                                            if (bool2) {
                                              break label1826;
                                            }
                                            bool1 = true;
                                            paramAnonymousAdapterView.putBoolean("enable24HourFormat", bool1);
                                            paramAnonymousAdapterView.commit();
                                          } while (!(paramAnonymousView instanceof TextCheckCell));
                                          paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                                          if (!bool2) {}
                                          for (bool1 = true;; bool1 = false)
                                          {
                                            paramAnonymousAdapterView.setChecked(bool1);
                                            return;
                                            bool1 = false;
                                            break;
                                          }
                                          if (paramAnonymousInt != MihanSettingsActivity.this.multiForwardRow) {
                                            break label1944;
                                          }
                                          paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                                          bool2 = paramAnonymousAdapterView.getBoolean("multi_forward", false);
                                          paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                                          if (bool2) {
                                            break label1932;
                                          }
                                          bool1 = true;
                                          paramAnonymousAdapterView.putBoolean("multi_forward", bool1);
                                          paramAnonymousAdapterView.commit();
                                        } while (!(paramAnonymousView instanceof TextCheckCell));
                                        paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                                        if (!bool2) {}
                                        for (bool1 = true;; bool1 = false)
                                        {
                                          paramAnonymousAdapterView.setChecked(bool1);
                                          return;
                                          bool1 = false;
                                          break;
                                        }
                                        if (paramAnonymousInt != MihanSettingsActivity.this.directForwardRow) {
                                          break label2326;
                                        }
                                      } while (MihanSettingsActivity.this.getParentActivity() == null);
                                      paramAnonymousView = new boolean[6];
                                      localObject1 = new BottomSheet.Builder(MihanSettingsActivity.this.getParentActivity());
                                      ((BottomSheet.Builder)localObject1).setApplyTopPadding(false);
                                      ((BottomSheet.Builder)localObject1).setApplyBottomPadding(false);
                                      localObject2 = new LinearLayout(MihanSettingsActivity.this.getParentActivity());
                                      ((LinearLayout)localObject2).setOrientation(1);
                                      localObject3 = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                                      paramAnonymousInt = 0;
                                      if (paramAnonymousInt < 4)
                                      {
                                        paramAnonymousAdapterView = null;
                                        if (paramAnonymousInt == 0)
                                        {
                                          paramAnonymousAdapterView = LocaleController.getString("ContactTab", 2131165562);
                                          paramAnonymousView[paramAnonymousInt] = ((SharedPreferences)localObject3).getBoolean("direct_contact", false);
                                        }
                                        for (;;)
                                        {
                                          localCheckBoxCell = new CheckBoxCell(MihanSettingsActivity.this.getParentActivity());
                                          localCheckBoxCell.setTag(Integer.valueOf(paramAnonymousInt));
                                          localCheckBoxCell.setBackgroundResource(2130837932);
                                          ((LinearLayout)localObject2).addView(localCheckBoxCell, LayoutHelper.createLinear(-1, 48));
                                          localCheckBoxCell.setText(paramAnonymousAdapterView, "", paramAnonymousView[paramAnonymousInt], true);
                                          localCheckBoxCell.setOnClickListener(new View.OnClickListener()
                                          {
                                            public void onClick(View paramAnonymous2View)
                                            {
                                              paramAnonymous2View = (CheckBoxCell)paramAnonymous2View;
                                              int i = ((Integer)paramAnonymous2View.getTag()).intValue();
                                              boolean[] arrayOfBoolean = paramAnonymousView;
                                              if (paramAnonymousView[i] == 0) {}
                                              for (int j = 1;; j = 0)
                                              {
                                                arrayOfBoolean[i] = j;
                                                paramAnonymous2View.setChecked(paramAnonymousView[i], true);
                                                return;
                                              }
                                            }
                                          });
                                          paramAnonymousInt += 1;
                                          break;
                                          if (paramAnonymousInt == 1)
                                          {
                                            paramAnonymousAdapterView = LocaleController.getString("GroupsTab", 2131165791);
                                            paramAnonymousView[paramAnonymousInt] = ((SharedPreferences)localObject3).getBoolean("direct_group", false);
                                          }
                                          else if (paramAnonymousInt == 2)
                                          {
                                            paramAnonymousAdapterView = LocaleController.getString("ChannelTab", 2131165518);
                                            paramAnonymousView[paramAnonymousInt] = ((SharedPreferences)localObject3).getBoolean("direct_channel", true);
                                          }
                                          else if (paramAnonymousInt == 3)
                                          {
                                            paramAnonymousAdapterView = LocaleController.getString("RobotTab", 2131166248);
                                            paramAnonymousView[paramAnonymousInt] = ((SharedPreferences)localObject3).getBoolean("direct_bot", true);
                                          }
                                        }
                                      }
                                      paramAnonymousAdapterView = new BottomSheet.BottomSheetCell(MihanSettingsActivity.this.getParentActivity(), 1);
                                      paramAnonymousAdapterView.setBackgroundResource(2130837932);
                                      paramAnonymousAdapterView.setTextAndIcon(LocaleController.getString("Save", 2131166254).toUpperCase(), 0);
                                      paramAnonymousAdapterView.setTextColor(-12940081);
                                      paramAnonymousAdapterView.setOnClickListener(new View.OnClickListener()
                                      {
                                        public void onClick(View paramAnonymous2View)
                                        {
                                          try
                                          {
                                            if (MihanSettingsActivity.this.visibleDialog != null) {
                                              MihanSettingsActivity.this.visibleDialog.dismiss();
                                            }
                                            i = 0;
                                            for (;;)
                                            {
                                              if (i >= 4) {
                                                break label181;
                                              }
                                              paramAnonymous2View = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).edit();
                                              if (i != 0) {
                                                break;
                                              }
                                              paramAnonymous2View.putBoolean("direct_contact", paramAnonymousView[i]);
                                              paramAnonymous2View.commit();
                                              i += 1;
                                            }
                                          }
                                          catch (Exception paramAnonymous2View)
                                          {
                                            for (;;)
                                            {
                                              int i;
                                              FileLog.e("tmessages", paramAnonymous2View);
                                              continue;
                                              if (i == 1)
                                              {
                                                paramAnonymous2View.putBoolean("direct_group", paramAnonymousView[i]);
                                                paramAnonymous2View.commit();
                                              }
                                              else if (i == 2)
                                              {
                                                paramAnonymous2View.putBoolean("direct_channel", paramAnonymousView[i]);
                                                paramAnonymous2View.commit();
                                              }
                                              else if (i == 3)
                                              {
                                                paramAnonymous2View.putBoolean("direct_bot", paramAnonymousView[i]);
                                                paramAnonymous2View.commit();
                                              }
                                            }
                                            label181:
                                            if (MihanSettingsActivity.this.listView != null) {
                                              MihanSettingsActivity.this.listView.invalidateViews();
                                            }
                                          }
                                        }
                                      });
                                      ((LinearLayout)localObject2).addView(paramAnonymousAdapterView, LayoutHelper.createLinear(-1, 48));
                                      ((BottomSheet.Builder)localObject1).setCustomView((View)localObject2);
                                      MihanSettingsActivity.this.showDialog(((BottomSheet.Builder)localObject1).create());
                                      return;
                                      if (paramAnonymousInt != MihanSettingsActivity.this.enableTabsInDirectFRow) {
                                        break label2432;
                                      }
                                      paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                                      bool2 = paramAnonymousAdapterView.getBoolean("multi_forward_tabs", true);
                                      paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                                      if (bool2) {
                                        break label2420;
                                      }
                                      bool1 = true;
                                      paramAnonymousAdapterView.putBoolean("multi_forward_tabs", bool1);
                                      paramAnonymousAdapterView.commit();
                                    } while (!(paramAnonymousView instanceof TextCheckCell));
                                    paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                                    if (!bool2) {}
                                    for (bool1 = true;; bool1 = false)
                                    {
                                      paramAnonymousAdapterView.setChecked(bool1);
                                      return;
                                      bool1 = false;
                                      break;
                                    }
                                    if (paramAnonymousInt != MihanSettingsActivity.this.typingStatusRow) {
                                      break label2538;
                                    }
                                    paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                                    bool2 = paramAnonymousAdapterView.getBoolean("hide_typing", false);
                                    paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                                    if (bool2) {
                                      break label2526;
                                    }
                                    bool1 = true;
                                    paramAnonymousAdapterView.putBoolean("hide_typing", bool1);
                                    paramAnonymousAdapterView.commit();
                                  } while (!(paramAnonymousView instanceof TextCheckCell));
                                  paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                                  if (!bool2) {}
                                  for (bool1 = true;; bool1 = false)
                                  {
                                    paramAnonymousAdapterView.setChecked(bool1);
                                    return;
                                    bool1 = false;
                                    break;
                                  }
                                  if (paramAnonymousInt == MihanSettingsActivity.this.passwordRow)
                                  {
                                    MihanSettingsActivity.this.presentFragment(new SetPasswordActivity(0));
                                    return;
                                  }
                                  if (paramAnonymousInt == MihanSettingsActivity.this.ghostModeRow)
                                  {
                                    paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                                    bool2 = paramAnonymousAdapterView.getBoolean("ghost_mode", false);
                                    paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                                    if (!bool2)
                                    {
                                      bool1 = true;
                                      paramAnonymousAdapterView.putBoolean("ghost_mode", bool1);
                                      paramAnonymousAdapterView.commit();
                                      if ((paramAnonymousView instanceof TextCheckCell))
                                      {
                                        paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                                        if (bool2) {
                                          break label2694;
                                        }
                                        bool1 = true;
                                        paramAnonymousAdapterView.setChecked(bool1);
                                      }
                                      paramAnonymousAdapterView = MihanSettingsActivity.this.actionBar;
                                      if (bool2) {
                                        break label2700;
                                      }
                                    }
                                    for (bool1 = true;; bool1 = false)
                                    {
                                      paramAnonymousAdapterView.setGhostImage(bool1);
                                      MessagesController.getInstance().reRunUpdateTimerProc();
                                      return;
                                      bool1 = false;
                                      break;
                                      bool1 = false;
                                      break label2653;
                                    }
                                  }
                                  if (paramAnonymousInt != MihanSettingsActivity.this.hidePhoneRow) {
                                    break label2812;
                                  }
                                  paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                                  bool2 = paramAnonymousAdapterView.getBoolean("hide_phone", false);
                                  paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                                  if (bool2) {
                                    break label2800;
                                  }
                                  bool1 = true;
                                  paramAnonymousAdapterView.putBoolean("hide_phone", bool1);
                                  paramAnonymousAdapterView.commit();
                                } while (!(paramAnonymousView instanceof TextCheckCell));
                                paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                                if (!bool2) {}
                                for (bool1 = true;; bool1 = false)
                                {
                                  paramAnonymousAdapterView.setChecked(bool1);
                                  return;
                                  bool1 = false;
                                  break;
                                }
                                if (paramAnonymousInt != MihanSettingsActivity.this.showExactCountRow) {
                                  break label2918;
                                }
                                paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                                bool2 = paramAnonymousAdapterView.getBoolean("exact_count", false);
                                paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                                if (bool2) {
                                  break label2906;
                                }
                                bool1 = true;
                                paramAnonymousAdapterView.putBoolean("exact_count", bool1);
                                paramAnonymousAdapterView.commit();
                              } while (!(paramAnonymousView instanceof TextCheckCell));
                              paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                              if (!bool2) {}
                              for (bool1 = true;; bool1 = false)
                              {
                                paramAnonymousAdapterView.setChecked(bool1);
                                return;
                                bool1 = false;
                                break;
                              }
                              if (paramAnonymousInt == MihanSettingsActivity.this.chatBarRow)
                              {
                                paramAnonymousAdapterView = new BottomSheet.Builder(MihanSettingsActivity.this.getParentActivity());
                                paramAnonymousView = LocaleController.getString("ChatBarDisabled", 2131166620);
                                localObject1 = LocaleController.getString("ChatBarOlwaysOpen", 2131166622);
                                localObject2 = LocaleController.getString("ChatBarOlwaysClose", 2131166621);
                                localObject3 = new DialogInterface.OnClickListener()
                                {
                                  public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                                  {
                                    paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).edit();
                                    switch (paramAnonymous2Int)
                                    {
                                    }
                                    for (;;)
                                    {
                                      if (MihanSettingsActivity.this.listView != null) {
                                        MihanSettingsActivity.this.listView.invalidateViews();
                                      }
                                      return;
                                      paramAnonymous2DialogInterface.putInt("chat_bar_status", 1);
                                      paramAnonymous2DialogInterface.commit();
                                      continue;
                                      paramAnonymous2DialogInterface.putInt("chat_bar_status", 2);
                                      paramAnonymous2DialogInterface.commit();
                                      continue;
                                      paramAnonymous2DialogInterface.putInt("chat_bar_status", 3);
                                      paramAnonymous2DialogInterface.commit();
                                    }
                                  }
                                };
                                paramAnonymousAdapterView.setItems(new CharSequence[] { paramAnonymousView, localObject1, localObject2 }, (DialogInterface.OnClickListener)localObject3);
                                MihanSettingsActivity.this.showDialog(paramAnonymousAdapterView.create());
                                return;
                              }
                              if (paramAnonymousInt != MihanSettingsActivity.this.showPaintingRow) {
                                break label3130;
                              }
                              paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                              bool2 = paramAnonymousAdapterView.getBoolean("painting_icon", true);
                              paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                              if (bool2) {
                                break label3118;
                              }
                              bool1 = true;
                              paramAnonymousAdapterView.putBoolean("painting_icon", bool1);
                              paramAnonymousAdapterView.commit();
                            } while (!(paramAnonymousView instanceof TextCheckCell));
                            paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                            if (!bool2) {}
                            for (bool1 = true;; bool1 = false)
                            {
                              paramAnonymousAdapterView.setChecked(bool1);
                              return;
                              bool1 = false;
                              break;
                            }
                            if (paramAnonymousInt != MihanSettingsActivity.this.floatingDateRow) {
                              break label3236;
                            }
                            paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                            bool2 = paramAnonymousAdapterView.getBoolean("floating_date", true);
                            paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                            if (bool2) {
                              break label3224;
                            }
                            bool1 = true;
                            paramAnonymousAdapterView.putBoolean("floating_date", bool1);
                            paramAnonymousAdapterView.commit();
                          } while (!(paramAnonymousView instanceof TextCheckCell));
                          paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                          if (!bool2) {}
                          for (bool1 = true;; bool1 = false)
                          {
                            paramAnonymousAdapterView.setChecked(bool1);
                            return;
                            bool1 = false;
                            break;
                          }
                          if (paramAnonymousInt != MihanSettingsActivity.this.previewStickerRow) {
                            break label3342;
                          }
                          paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                          bool2 = paramAnonymousAdapterView.getBoolean("preview_sticker", false);
                          paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                          if (bool2) {
                            break label3330;
                          }
                          bool1 = true;
                          paramAnonymousAdapterView.putBoolean("preview_sticker", bool1);
                          paramAnonymousAdapterView.commit();
                        } while (!(paramAnonymousView instanceof TextCheckCell));
                        paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                        if (!bool2) {}
                        for (bool1 = true;; bool1 = false)
                        {
                          paramAnonymousAdapterView.setChecked(bool1);
                          return;
                          bool1 = false;
                          break;
                        }
                        if (paramAnonymousInt != MihanSettingsActivity.this.confirmatinAudioRow) {
                          break label3448;
                        }
                        paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                        bool2 = paramAnonymousAdapterView.getBoolean("confirmatin_audio", false);
                        paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                        if (bool2) {
                          break label3436;
                        }
                        bool1 = true;
                        paramAnonymousAdapterView.putBoolean("confirmatin_audio", bool1);
                        paramAnonymousAdapterView.commit();
                      } while (!(paramAnonymousView instanceof TextCheckCell));
                      paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                      if (!bool2) {}
                      for (bool1 = true;; bool1 = false)
                      {
                        paramAnonymousAdapterView.setChecked(bool1);
                        return;
                        bool1 = false;
                        break;
                      }
                      if (paramAnonymousInt != MihanSettingsActivity.this.showMutualRow) {
                        break label3554;
                      }
                      paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                      bool2 = paramAnonymousAdapterView.getBoolean("mutual_contact", false);
                      paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                      if (bool2) {
                        break label3542;
                      }
                      bool1 = true;
                      paramAnonymousAdapterView.putBoolean("mutual_contact", bool1);
                      paramAnonymousAdapterView.commit();
                    } while (!(paramAnonymousView instanceof TextCheckCell));
                    paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                    if (!bool2) {}
                    for (bool1 = true;; bool1 = false)
                    {
                      paramAnonymousAdapterView.setChecked(bool1);
                      return;
                      bool1 = false;
                      break;
                    }
                    if (paramAnonymousInt != MihanSettingsActivity.this.showUserStatusRow) {
                      break label3660;
                    }
                    paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                    bool2 = paramAnonymousAdapterView.getBoolean("contact_status", false);
                    paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                    if (bool2) {
                      break label3648;
                    }
                    bool1 = true;
                    paramAnonymousAdapterView.putBoolean("contact_status", bool1);
                    paramAnonymousAdapterView.commit();
                  } while (!(paramAnonymousView instanceof TextCheckCell));
                  paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                  if (!bool2) {}
                  for (bool1 = true;; bool1 = false)
                  {
                    paramAnonymousAdapterView.setChecked(bool1);
                    return;
                    bool1 = false;
                    break;
                  }
                  if (paramAnonymousInt != MihanSettingsActivity.this.showChatUserStatusRow) {
                    break label3766;
                  }
                  paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                  bool2 = paramAnonymousAdapterView.getBoolean("chat_contact_status", false);
                  paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                  if (bool2) {
                    break label3754;
                  }
                  bool1 = true;
                  paramAnonymousAdapterView.putBoolean("chat_contact_status", bool1);
                  paramAnonymousAdapterView.commit();
                } while (!(paramAnonymousView instanceof TextCheckCell));
                paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                if (!bool2) {}
                for (bool1 = true;; bool1 = false)
                {
                  paramAnonymousAdapterView.setChecked(bool1);
                  return;
                  bool1 = false;
                  break;
                }
                if (paramAnonymousInt == MihanSettingsActivity.this.toolBarRow)
                {
                  paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                  bool2 = paramAnonymousAdapterView.getBoolean("tool_bar", true);
                  paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                  if (!bool2)
                  {
                    bool1 = true;
                    paramAnonymousAdapterView.putBoolean("tool_bar", bool1);
                    paramAnonymousAdapterView.commit();
                    if ((paramAnonymousView instanceof TextCheckCell))
                    {
                      paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                      if (bool2) {
                        break label3879;
                      }
                    }
                  }
                  for (bool1 = true;; bool1 = false)
                  {
                    paramAnonymousAdapterView.setChecked(bool1);
                    if (!AndroidUtilities.isTablet()) {
                      break label3885;
                    }
                    MihanSettingsActivity.this.reLunchApp();
                    return;
                    bool1 = false;
                    break;
                  }
                  MihanSettingsActivity.this.restartApp();
                  return;
                }
                if (paramAnonymousInt != MihanSettingsActivity.this.countNotMutedRow) {
                  break label3999;
                }
                paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                bool2 = paramAnonymousAdapterView.getBoolean("tabs_only_not_muted", false);
                paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                if (bool2) {
                  break label3987;
                }
                bool1 = true;
                paramAnonymousAdapterView.putBoolean("tabs_only_not_muted", bool1);
                paramAnonymousAdapterView.commit();
              } while (!(paramAnonymousView instanceof TextCheckCell));
              paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
              if (!bool2) {}
              for (bool1 = true;; bool1 = false)
              {
                paramAnonymousAdapterView.setChecked(bool1);
                return;
                bool1 = false;
                break;
              }
              if (paramAnonymousInt != MihanSettingsActivity.this.keepChatRow) {
                break label4105;
              }
              paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
              bool2 = paramAnonymousAdapterView.getBoolean("keep_chat_open", true);
              paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
              if (bool2) {
                break label4093;
              }
              bool1 = true;
              paramAnonymousAdapterView.putBoolean("keep_chat_open", bool1);
              paramAnonymousAdapterView.commit();
            } while (!(paramAnonymousView instanceof TextCheckCell));
            paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
            if (!bool2) {}
            for (bool1 = true;; bool1 = false)
            {
              paramAnonymousAdapterView.setChecked(bool1);
              return;
              bool1 = false;
              break;
            }
            if (paramAnonymousInt == MihanSettingsActivity.this.saveSettingRow)
            {
              MihanSettingsActivity.this.saveSettings();
              return;
            }
            if (paramAnonymousInt == MihanSettingsActivity.this.restoreSettingRow)
            {
              MihanSettingsActivity.this.loadSettings();
              return;
            }
            if (paramAnonymousInt == MihanSettingsActivity.this.resetSettingRow)
            {
              MihanSettingsActivity.this.resetSettings();
              return;
            }
          } while (paramAnonymousInt != MihanSettingsActivity.this.saveInSDRow);
          paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
          bool2 = paramAnonymousAdapterView.getBoolean("save_in_sd", false);
          paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
          if (bool2) {
            break label4256;
          }
          bool1 = true;
          paramAnonymousAdapterView.putBoolean("keep_chat_open", bool1);
          paramAnonymousAdapterView.commit();
        } while (!(paramAnonymousView instanceof TextCheckCell));
        paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
        if (!bool2) {}
        for (boolean bool1 = true;; bool1 = false)
        {
          paramAnonymousAdapterView.setChecked(bool1);
          return;
          label4256:
          bool1 = false;
          break;
        }
      }
    });
    localFrameLayout.addView(this.actionBar);
    this.extraHeightView = new View(paramContext);
    this.extraHeightView.setPivotY(0.0F);
    localFrameLayout.addView(this.extraHeightView, LayoutHelper.createFrame(-1, 88.0F));
    this.shadowView = new View(paramContext);
    this.shadowView.setBackgroundResource(2130837802);
    localFrameLayout.addView(this.shadowView, LayoutHelper.createFrame(-1, 3.0F));
    this.avatarImage = new BackupImageView(paramContext);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0F));
    this.avatarImage.setPivotX(0.0F);
    this.avatarImage.setPivotY(0.0F);
    localFrameLayout.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0F, 51, 64.0F, 0.0F, 0.0F, 0.0F));
    this.nameTextView = new TextView(paramContext);
    this.nameTextView.setTextColor(-1);
    this.nameTextView.setTextSize(1, 18.0F);
    this.nameTextView.setLines(1);
    this.nameTextView.setMaxLines(1);
    this.nameTextView.setSingleLine(true);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.nameTextView.setGravity(3);
    this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.nameTextView.setPivotX(0.0F);
    this.nameTextView.setPivotY(0.0F);
    localFrameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0F, 51, 118.0F, 0.0F, 48.0F, 0.0F));
    this.onlineTextView = new TextView(paramContext);
    this.onlineTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(5));
    this.onlineTextView.setTextSize(1, 14.0F);
    this.onlineTextView.setLines(1);
    this.onlineTextView.setMaxLines(1);
    this.onlineTextView.setSingleLine(true);
    this.onlineTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.onlineTextView.setGravity(3);
    localFrameLayout.addView(this.onlineTextView, LayoutHelper.createFrame(-2, -2.0F, 51, 118.0F, 0.0F, 48.0F, 0.0F));
    this.onlineTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    needLayout();
    this.listView.setOnScrollListener(new AbsListView.OnScrollListener()
    {
      public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
      {
        int i = 0;
        if (paramAnonymousInt3 == 0) {}
        do
        {
          do
          {
            return;
            paramAnonymousInt2 = 0;
            paramAnonymousAbsListView = paramAnonymousAbsListView.getChildAt(0);
          } while (paramAnonymousAbsListView == null);
          if (paramAnonymousInt1 == 0)
          {
            paramAnonymousInt2 = AndroidUtilities.dp(88.0F);
            paramAnonymousInt1 = i;
            if (paramAnonymousAbsListView.getTop() < 0) {
              paramAnonymousInt1 = paramAnonymousAbsListView.getTop();
            }
            paramAnonymousInt2 += paramAnonymousInt1;
          }
        } while (MihanSettingsActivity.this.extraHeight == paramAnonymousInt2);
        MihanSettingsActivity.access$4802(MihanSettingsActivity.this, paramAnonymousInt2);
        MihanSettingsActivity.this.needLayout();
      }
      
      public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt) {}
    });
    return this.fragmentView;
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.updateInterfaces)
    {
      paramInt = ((Integer)paramVarArgs[0]).intValue();
      if (((paramInt & 0x2) != 0) || ((paramInt & 0x1) != 0)) {
        updateUserData();
      }
    }
  }
  
  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.avatarUpdater.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    fixLayout();
  }
  
  protected void onDialogDismiss(Dialog paramDialog)
  {
    MediaController.getInstance().checkAutodownloadSettings();
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.avatarUpdater.parentFragment = this;
    this.avatarUpdater.delegate = new AvatarUpdater.AvatarUpdaterDelegate()
    {
      public void didUploadedPhoto(TLRPC.InputFile paramAnonymousInputFile, TLRPC.PhotoSize paramAnonymousPhotoSize1, TLRPC.PhotoSize paramAnonymousPhotoSize2)
      {
        paramAnonymousPhotoSize1 = new TLRPC.TL_photos_uploadProfilePhoto();
        paramAnonymousPhotoSize1.caption = "";
        paramAnonymousPhotoSize1.crop = new TLRPC.TL_inputPhotoCropAuto();
        paramAnonymousPhotoSize1.file = paramAnonymousInputFile;
        paramAnonymousPhotoSize1.geo_point = new TLRPC.TL_inputGeoPointEmpty();
        ConnectionsManager.getInstance().sendRequest(paramAnonymousPhotoSize1, new RequestDelegate()
        {
          public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
          {
            if (paramAnonymous2TL_error == null)
            {
              paramAnonymous2TL_error = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
              if (paramAnonymous2TL_error != null) {
                break label174;
              }
              paramAnonymous2TL_error = UserConfig.getCurrentUser();
              if (paramAnonymous2TL_error != null) {}
            }
            else
            {
              return;
            }
            MessagesController.getInstance().putUser(paramAnonymous2TL_error, false);
            paramAnonymous2TLObject = (TLRPC.TL_photos_photo)paramAnonymous2TLObject;
            Object localObject = paramAnonymous2TLObject.photo.sizes;
            TLRPC.PhotoSize localPhotoSize = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject, 100);
            localObject = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject, 1000);
            paramAnonymous2TL_error.photo = new TLRPC.TL_userProfilePhoto();
            paramAnonymous2TL_error.photo.photo_id = paramAnonymous2TLObject.photo.id;
            if (localPhotoSize != null) {
              paramAnonymous2TL_error.photo.photo_small = localPhotoSize.location;
            }
            if (localObject != null) {
              paramAnonymous2TL_error.photo.photo_big = ((TLRPC.PhotoSize)localObject).location;
            }
            for (;;)
            {
              MessagesStorage.getInstance().clearUserPhotos(paramAnonymous2TL_error.id);
              paramAnonymous2TLObject = new ArrayList();
              paramAnonymous2TLObject.add(paramAnonymous2TL_error);
              MessagesStorage.getInstance().putUsersAndChats(paramAnonymous2TLObject, null, false, true);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1535) });
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                  UserConfig.saveConfig(true);
                }
              });
              return;
              label174:
              UserConfig.setCurrentUser(paramAnonymous2TL_error);
              break;
              if (localPhotoSize != null) {
                paramAnonymous2TL_error.photo.photo_small = localPhotoSize.location;
              }
            }
          }
        });
      }
    };
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.overscrollRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.emptyRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.viewSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.tabletModeRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.tabletModeDesRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.themeRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.persianDateRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.is24HoursRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.showExactCountRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.showUserStatusRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.showUserStatusDesRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.showChatUserStatusRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.showChatUserStatusDesRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.toolBarRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.floatingDateRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.chatBarRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.showPaintingRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.tabSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.tabSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.enableTabsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.activeTabsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.defaultTabRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.swipeTabRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.showTabsCounterRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.countNotMutedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.countChatsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.favAutoDownloadTabRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.forwardSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.forwardSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.multiForwardRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.multiForwardDesRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.directForwardRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.enableTabsInDirectFRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.privacySectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.privacySectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.passwordRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.ghostModeRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.ghostModeDesRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.typingStatusRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.hidePhoneRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.hidePhoneDesRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.moreSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.moreSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.previewStickerRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.confirmatinAudioRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.showMutualRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.showMutualDesRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.keepChatRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.keepChatDesRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.saveSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.saveSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.saveSettingRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.restoreSettingRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.resetSettingRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.versionRow = i;
    MessagesController.getInstance().loadFullUser(UserConfig.getCurrentUser(), this.classGuid, true);
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    if (this.avatarImage != null) {
      this.avatarImage.setImageDrawable(null);
    }
    MessagesController.getInstance().cancelLoadFullUser(UserConfig.getClientUserId());
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    this.avatarUpdater.clear();
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
    updateUserData();
    fixLayout();
  }
  
  private class ListAdapter
    extends BaseFragmentAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public boolean areAllItemsEnabled()
    {
      return false;
    }
    
    public int getCount()
    {
      return MihanSettingsActivity.this.rowCount;
    }
    
    public Object getItem(int paramInt)
    {
      return null;
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      int j = 2;
      int i;
      if ((paramInt == MihanSettingsActivity.this.emptyRow) || (paramInt == MihanSettingsActivity.this.overscrollRow)) {
        i = 0;
      }
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
                  do
                  {
                    do
                    {
                      return i;
                      if ((paramInt == MihanSettingsActivity.this.tabSectionRow) || (paramInt == MihanSettingsActivity.this.forwardSectionRow) || (paramInt == MihanSettingsActivity.this.privacySectionRow) || (paramInt == MihanSettingsActivity.this.moreSectionRow) || (paramInt == MihanSettingsActivity.this.saveSectionRow)) {
                        return 1;
                      }
                      if ((paramInt == MihanSettingsActivity.this.enableTabsRow) || (paramInt == MihanSettingsActivity.this.tabletModeRow) || (paramInt == MihanSettingsActivity.this.multiForwardRow) || (paramInt == MihanSettingsActivity.this.typingStatusRow) || (paramInt == MihanSettingsActivity.this.toolBarRow) || (paramInt == MihanSettingsActivity.this.ghostModeRow) || (paramInt == MihanSettingsActivity.this.hidePhoneRow) || (paramInt == MihanSettingsActivity.this.persianDateRow) || (paramInt == MihanSettingsActivity.this.is24HoursRow) || (paramInt == MihanSettingsActivity.this.showMutualRow) || (paramInt == MihanSettingsActivity.this.showUserStatusRow) || (paramInt == MihanSettingsActivity.this.previewStickerRow) || (paramInt == MihanSettingsActivity.this.swipeTabRow) || (paramInt == MihanSettingsActivity.this.showChatUserStatusRow) || (paramInt == MihanSettingsActivity.this.confirmatinAudioRow) || (paramInt == MihanSettingsActivity.this.countChatsRow) || (paramInt == MihanSettingsActivity.this.favAutoDownloadTabRow) || (paramInt == MihanSettingsActivity.this.showExactCountRow) || (paramInt == MihanSettingsActivity.this.enableTabsInDirectFRow) || (paramInt == MihanSettingsActivity.this.showTabsCounterRow) || (paramInt == MihanSettingsActivity.this.countNotMutedRow) || (paramInt == MihanSettingsActivity.this.floatingDateRow) || (paramInt == MihanSettingsActivity.this.showPaintingRow) || (paramInt == MihanSettingsActivity.this.keepChatRow)) {
                        return 3;
                      }
                      i = j;
                    } while (paramInt == MihanSettingsActivity.this.defaultTabRow);
                    i = j;
                  } while (paramInt == MihanSettingsActivity.this.passwordRow);
                  i = j;
                } while (paramInt == MihanSettingsActivity.this.chatBarRow);
                i = j;
              } while (paramInt == MihanSettingsActivity.this.themeRow);
              i = j;
            } while (paramInt == MihanSettingsActivity.this.saveSettingRow);
            i = j;
          } while (paramInt == MihanSettingsActivity.this.restoreSettingRow);
          i = j;
        } while (paramInt == MihanSettingsActivity.this.resetSettingRow);
        if (paramInt == MihanSettingsActivity.this.versionRow) {
          return 5;
        }
        if ((paramInt == MihanSettingsActivity.this.activeTabsRow) || (paramInt == MihanSettingsActivity.this.directForwardRow)) {
          return 6;
        }
        if ((paramInt == MihanSettingsActivity.this.tabletModeDesRow) || (paramInt == MihanSettingsActivity.this.multiForwardDesRow) || (paramInt == MihanSettingsActivity.this.ghostModeDesRow) || (paramInt == MihanSettingsActivity.this.hidePhoneDesRow) || (paramInt == MihanSettingsActivity.this.showMutualDesRow) || (paramInt == MihanSettingsActivity.this.showUserStatusDesRow) || (paramInt == MihanSettingsActivity.this.showChatUserStatusDesRow) || (paramInt == MihanSettingsActivity.this.keepChatDesRow)) {
          return 7;
        }
        if ((paramInt == MihanSettingsActivity.this.tabSectionRow2) || (paramInt == MihanSettingsActivity.this.viewSectionRow2) || (paramInt == MihanSettingsActivity.this.forwardSectionRow2) || (paramInt == MihanSettingsActivity.this.privacySectionRow2) || (paramInt == MihanSettingsActivity.this.moreSectionRow2) || (paramInt == MihanSettingsActivity.this.saveSectionRow2)) {
          break;
        }
        i = j;
      } while (paramInt != MihanSettingsActivity.this.saveSectionRow2);
      return 4;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      int i = getItemViewType(paramInt);
      if (i == 0)
      {
        paramViewGroup = paramView;
        if (paramView == null) {
          paramViewGroup = new EmptyCell(this.mContext);
        }
        if (paramInt == MihanSettingsActivity.this.overscrollRow) {
          ((EmptyCell)paramViewGroup).setHeight(AndroidUtilities.dp(88.0F));
        }
      }
      Object localObject1;
      do
      {
        do
        {
          Object localObject2;
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
                      return paramViewGroup;
                      ((EmptyCell)paramViewGroup).setHeight(AndroidUtilities.dp(0.0F));
                      return paramViewGroup;
                      if (i != 1) {
                        break;
                      }
                      paramViewGroup = paramView;
                    } while (paramView != null);
                    return new ShadowSectionCell(this.mContext);
                    if (i != 2) {
                      break;
                    }
                    localObject1 = paramView;
                    if (paramView == null) {
                      localObject1 = new TextSettingsCell(this.mContext);
                    }
                    localObject2 = (TextSettingsCell)localObject1;
                    if (paramInt == MihanSettingsActivity.this.defaultTabRow)
                    {
                      switch (ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getInt("default_tab", 6))
                      {
                      default: 
                        ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("DefaultTab", 2131165598), LocaleController.getString("AllTab", 2131165315), true);
                        return (View)localObject1;
                      case 7: 
                        ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("DefaultTab", 2131165598), LocaleController.getString("UnreadTab", 2131166427), true);
                        return (View)localObject1;
                      case 6: 
                        ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("DefaultTab", 2131165598), LocaleController.getString("AllTab", 2131165315), true);
                        return (View)localObject1;
                      case 5: 
                        ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("DefaultTab", 2131165598), LocaleController.getString("FavoriteTab", 2131165684), true);
                        return (View)localObject1;
                      case 4: 
                        ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("DefaultTab", 2131165598), LocaleController.getString("ContactTab", 2131165562), true);
                        return (View)localObject1;
                      case 3: 
                        ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("DefaultTab", 2131165598), LocaleController.getString("GroupsTab", 2131165791), true);
                        return (View)localObject1;
                      case 2: 
                        ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("DefaultTab", 2131165598), LocaleController.getString("SuperGroupsTab", 2131166675), true);
                        return (View)localObject1;
                      case 1: 
                        ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("DefaultTab", 2131165598), LocaleController.getString("ChannelTab", 2131165518), true);
                        return (View)localObject1;
                      }
                      ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("DefaultTab", 2131165598), LocaleController.getString("RobotTab", 2131166248), true);
                      return (View)localObject1;
                    }
                    if (paramInt == MihanSettingsActivity.this.passwordRow)
                    {
                      ((TextSettingsCell)localObject2).setText(LocaleController.getString("HideChats", 2131165796), true);
                      return (View)localObject1;
                    }
                    if (paramInt == MihanSettingsActivity.this.themeRow)
                    {
                      ((TextSettingsCell)localObject2).setText(LocaleController.getString("Themes", 2131166393), true);
                      return (View)localObject1;
                    }
                    if (paramInt == MihanSettingsActivity.this.chatBarRow)
                    {
                      paramInt = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getInt("chat_bar_status", 3);
                      if (paramInt == 1) {
                        paramView = LocaleController.getString("ChatBarDisabled", 2131166620);
                      }
                      for (;;)
                      {
                        ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("ChatBar", 2131166619), paramView, true);
                        return (View)localObject1;
                        if (paramInt == 2) {
                          paramView = LocaleController.getString("ChatBarOlwaysOpen", 2131166622);
                        } else {
                          paramView = LocaleController.getString("ChatBarOlwaysClose", 2131166621);
                        }
                      }
                    }
                    if (paramInt == MihanSettingsActivity.this.saveSettingRow)
                    {
                      ((TextSettingsCell)localObject2).setText(LocaleController.getString("MihanSaveSetting", 2131166816), true);
                      return (View)localObject1;
                    }
                    if (paramInt == MihanSettingsActivity.this.restoreSettingRow)
                    {
                      ((TextSettingsCell)localObject2).setText(LocaleController.getString("MihanRestoreSetting", 2131166814), true);
                      return (View)localObject1;
                    }
                    paramViewGroup = (ViewGroup)localObject1;
                  } while (paramInt != MihanSettingsActivity.this.resetSettingRow);
                  ((TextSettingsCell)localObject2).setText(LocaleController.getString("MihanResetSetting", 2131166813), true);
                  return (View)localObject1;
                  if (i != 3) {
                    break;
                  }
                  localObject1 = paramView;
                  if (paramView == null) {
                    localObject1 = new TextCheckCell(this.mContext);
                  }
                  paramView = (TextCheckCell)localObject1;
                  localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                  if (paramInt == MihanSettingsActivity.this.enableTabsRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("EnableTabs", 2131165647), ((SharedPreferences)localObject2).getBoolean("tabs", true), true);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.swipeTabRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("SwipeTabs", 2131166380), ((SharedPreferences)localObject2).getBoolean("swipe_tabs", true), true);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.favAutoDownloadTabRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("FavAutoDownload", 2131165683), ((SharedPreferences)localObject2).getBoolean("fav_auto_download", false), false);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.tabletModeRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("TabletMode", 2131166383), ((SharedPreferences)localObject2).getBoolean("tablet_mode", true), false);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.multiForwardRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("MultiForward", 2131165975), ((SharedPreferences)localObject2).getBoolean("multi_forward", false), false);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.enableTabsInDirectFRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("EnableTabsForMultiForward", 2131166635), ((SharedPreferences)localObject2).getBoolean("multi_forward_tabs", true), false);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.typingStatusRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("HideTypingStatus", 2131165799), ((SharedPreferences)localObject2).getBoolean("hide_typing", false), true);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.ghostModeRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("GhostMode", 2131165775), ((SharedPreferences)localObject2).getBoolean("ghost_mode", false), false);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.hidePhoneRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("HidePhone", 2131165798), ((SharedPreferences)localObject2).getBoolean("hide_phone", false), false);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.persianDateRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("UsePersianDate", 2131166433), ((SharedPreferences)localObject2).getBoolean("persian_date", false), true);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.is24HoursRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("Is24Hours", 2131166646), ((SharedPreferences)localObject2).getBoolean("enable24HourFormat", false), false);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.showExactCountRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("ShowExactCount", 2131166344), ((SharedPreferences)localObject2).getBoolean("exact_count", false), true);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.previewStickerRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("PreviewSticker", 2131166195), ((SharedPreferences)localObject2).getBoolean("preview_sticker", false), true);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.confirmatinAudioRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("ConfirmatinAudio", 2131165558), ((SharedPreferences)localObject2).getBoolean("confirmatin_audio", false), true);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.showMutualRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("ShowMutualContacts", 2131166345), ((SharedPreferences)localObject2).getBoolean("mutual_contact", false), false);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.showUserStatusRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("ShowContactStatus", 2131166341), ((SharedPreferences)localObject2).getBoolean("contact_status", false), false);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.showChatUserStatusRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("ShowContactStatusGroup", 2131166342), ((SharedPreferences)localObject2).getBoolean("chat_contact_status", false), false);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.showTabsCounterRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("TabsCounter", 2131166679), ((SharedPreferences)localObject2).getBoolean("tabs_counter", true), true);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.toolBarRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("ToolBar", 2131166808), ((SharedPreferences)localObject2).getBoolean("tool_bar", true), true);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.countChatsRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("TabsCountChats", 2131166677), ((SharedPreferences)localObject2).getBoolean("tabs_count_chats", false), true);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.countNotMutedRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("TabsCountNotMuted", 2131166678), ((SharedPreferences)localObject2).getBoolean("tabs_only_not_muted", false), true);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.floatingDateRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("FloatingDate", 2131166636), ((SharedPreferences)localObject2).getBoolean("floating_date", true), true);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.showPaintingRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("PaintingIcon", 2131166665), ((SharedPreferences)localObject2).getBoolean("painting_icon", true), false);
                    return (View)localObject1;
                  }
                  if (paramInt == MihanSettingsActivity.this.keepChatRow)
                  {
                    paramView.setTextAndCheck(LocaleController.getString("KeepChatPage", 2131166647), ((SharedPreferences)localObject2).getBoolean("keep_chat_open", true), false);
                    return (View)localObject1;
                  }
                  paramViewGroup = (ViewGroup)localObject1;
                } while (paramInt != MihanSettingsActivity.this.saveInSDRow);
                paramView.setTextAndCheck(LocaleController.getString("SaveInSd", 2131166668), ((SharedPreferences)localObject2).getBoolean("save_in_sd", false), false);
                return (View)localObject1;
                if (i != 4) {
                  break;
                }
                localObject1 = paramView;
                if (paramView == null) {
                  localObject1 = new HeaderCell(this.mContext);
                }
                if (paramInt == MihanSettingsActivity.this.tabSectionRow2)
                {
                  ((HeaderCell)localObject1).setText(LocaleController.getString("TabsSettings", 2131166385));
                  return (View)localObject1;
                }
                if (paramInt == MihanSettingsActivity.this.viewSectionRow2)
                {
                  ((HeaderCell)localObject1).setText(LocaleController.getString("ViewSettings", 2131166459));
                  return (View)localObject1;
                }
                if (paramInt == MihanSettingsActivity.this.forwardSectionRow2)
                {
                  ((HeaderCell)localObject1).setText(LocaleController.getString("ForwardSetting", 2131165702));
                  return (View)localObject1;
                }
                if (paramInt == MihanSettingsActivity.this.privacySectionRow2)
                {
                  ((HeaderCell)localObject1).setText(LocaleController.getString("MihanPrivacySettings", 2131166411));
                  return (View)localObject1;
                }
                if (paramInt == MihanSettingsActivity.this.moreSectionRow2)
                {
                  ((HeaderCell)localObject1).setText(LocaleController.getString("MoreSettings", 2131165972));
                  return (View)localObject1;
                }
                paramViewGroup = (ViewGroup)localObject1;
              } while (paramInt != MihanSettingsActivity.this.saveSectionRow2);
              ((HeaderCell)localObject1).setText(LocaleController.getString("MihanSaveSHeader", 2131166815));
              return (View)localObject1;
              if (i != 5) {
                break;
              }
              paramViewGroup = paramView;
            } while (paramView != null);
            paramView = new TextInfoCell(this.mContext);
            try
            {
              paramViewGroup = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
              ((TextInfoCell)paramView).setText(String.format(Locale.US, "Supergram for Android v%s (%d)", new Object[] { paramViewGroup.versionName, Integer.valueOf(paramViewGroup.versionCode) }));
              return paramView;
            }
            catch (Exception paramViewGroup)
            {
              FileLog.e("tmessages", paramViewGroup);
              return paramView;
            }
            if (i != 6) {
              break;
            }
            localObject1 = paramView;
            if (paramView == null) {
              localObject1 = new TextDetailSettingsCell(this.mContext);
            }
            localObject2 = (TextDetailSettingsCell)localObject1;
            if (paramInt == MihanSettingsActivity.this.activeTabsRow)
            {
              localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
              paramViewGroup = "";
              paramInt = 0;
              if (paramInt < 7)
              {
                if (paramInt == 0)
                {
                  paramView = paramViewGroup;
                  if (localSharedPreferences.getBoolean("tab_unread", true)) {
                    paramView = paramViewGroup + LocaleController.getString("UnreadTab", 2131166427) + ", ";
                  }
                }
                for (;;)
                {
                  paramInt += 1;
                  paramViewGroup = paramView;
                  break;
                  if (paramInt == 1)
                  {
                    paramView = paramViewGroup;
                    if (localSharedPreferences.getBoolean("tab_favorite", true)) {
                      paramView = paramViewGroup + LocaleController.getString("FavoriteTab", 2131165684) + ", ";
                    }
                  }
                  else if (paramInt == 2)
                  {
                    paramView = paramViewGroup;
                    if (localSharedPreferences.getBoolean("tab_contact", true)) {
                      paramView = paramViewGroup + LocaleController.getString("ContactTab", 2131165562) + ", ";
                    }
                  }
                  else if (paramInt == 3)
                  {
                    paramView = paramViewGroup;
                    if (localSharedPreferences.getBoolean("tab_group", true)) {
                      paramView = paramViewGroup + LocaleController.getString("GroupsTab", 2131165791) + ", ";
                    }
                  }
                  else if (paramInt == 4)
                  {
                    paramView = paramViewGroup;
                    if (localSharedPreferences.getBoolean("tab_supergroup", true)) {
                      paramView = paramViewGroup + LocaleController.getString("SuperGroupsTab", 2131166675) + ", ";
                    }
                  }
                  else if (paramInt == 5)
                  {
                    paramView = paramViewGroup;
                    if (localSharedPreferences.getBoolean("tab_channel", true)) {
                      paramView = paramViewGroup + LocaleController.getString("ChannelTab", 2131165518) + ", ";
                    }
                  }
                  else
                  {
                    paramView = paramViewGroup;
                    if (paramInt == 6)
                    {
                      paramView = paramViewGroup;
                      if (localSharedPreferences.getBoolean("tab_bot", true)) {
                        paramView = paramViewGroup + LocaleController.getString("RobotTab", 2131166248) + ", ";
                      }
                    }
                  }
                }
              }
              paramView = new StringBuilder(paramViewGroup);
              if (paramView.length() != 0) {
                paramView.setCharAt(paramView.length() - 2, ' ');
              }
              ((TextDetailSettingsCell)localObject2).setTextAndValue(LocaleController.getString("ActiveTabs", 2131165292), String.valueOf(paramView), true);
              ((TextDetailSettingsCell)localObject2).setMultilineDetail(false);
              return (View)localObject1;
            }
            paramViewGroup = (ViewGroup)localObject1;
          } while (paramInt != MihanSettingsActivity.this.directForwardRow);
          SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
          paramViewGroup = "";
          paramInt = 0;
          if (paramInt < 4)
          {
            if (paramInt == 0)
            {
              paramView = paramViewGroup;
              if (localSharedPreferences.getBoolean("direct_contact", false)) {
                paramView = paramViewGroup + LocaleController.getString("ContactTab", 2131165562) + ", ";
              }
            }
            for (;;)
            {
              paramInt += 1;
              paramViewGroup = paramView;
              break;
              if (paramInt == 1)
              {
                paramView = paramViewGroup;
                if (localSharedPreferences.getBoolean("direct_group", false)) {
                  paramView = paramViewGroup + LocaleController.getString("GroupsTab", 2131165791) + ", ";
                }
              }
              else if (paramInt == 2)
              {
                paramView = paramViewGroup;
                if (localSharedPreferences.getBoolean("direct_channel", true)) {
                  paramView = paramViewGroup + LocaleController.getString("ChannelTab", 2131165518) + ", ";
                }
              }
              else
              {
                paramView = paramViewGroup;
                if (paramInt == 3)
                {
                  paramView = paramViewGroup;
                  if (localSharedPreferences.getBoolean("direct_bot", true)) {
                    paramView = paramViewGroup + LocaleController.getString("RobotTab", 2131166248) + ", ";
                  }
                }
              }
            }
          }
          paramView = new StringBuilder(paramViewGroup);
          if (paramView.length() != 0) {
            paramView.setCharAt(paramView.length() - 2, ' ');
          }
          ((TextDetailSettingsCell)localObject2).setTextAndValue(LocaleController.getString("DirectForward", 2131165627), String.valueOf(paramView), true);
          return (View)localObject1;
          paramViewGroup = paramView;
        } while (i != 7);
        localObject1 = paramView;
        if (paramView == null) {
          localObject1 = new TextDescriptionCell(this.mContext);
        }
        if (paramInt == MihanSettingsActivity.this.tabletModeDesRow)
        {
          ((TextDescriptionCell)localObject1).setText(LocaleController.getString("TabletModeDescription", 2131166384), true);
          return (View)localObject1;
        }
        if (paramInt == MihanSettingsActivity.this.multiForwardDesRow)
        {
          ((TextDescriptionCell)localObject1).setText(LocaleController.getString("MultiForwardDescription", 2131165976), true);
          return (View)localObject1;
        }
        if (paramInt == MihanSettingsActivity.this.ghostModeDesRow)
        {
          ((TextDescriptionCell)localObject1).setText(LocaleController.getString("GhostModeDescription", 2131165776), true);
          return (View)localObject1;
        }
        if (paramInt == MihanSettingsActivity.this.hidePhoneDesRow)
        {
          ((TextDescriptionCell)localObject1).setText(LocaleController.getString("HideNumberDescription", 2131165797), false);
          return (View)localObject1;
        }
        if (paramInt == MihanSettingsActivity.this.showMutualDesRow)
        {
          ((TextDescriptionCell)localObject1).setText(LocaleController.getString("MutualContactDescription", 2131165980), true);
          return (View)localObject1;
        }
        if (paramInt == MihanSettingsActivity.this.showUserStatusDesRow)
        {
          ((TextDescriptionCell)localObject1).setText(LocaleController.getString("ContactStatusDescription", 2131165561), true);
          return (View)localObject1;
        }
        if (paramInt == MihanSettingsActivity.this.showChatUserStatusDesRow)
        {
          ((TextDescriptionCell)localObject1).setText(LocaleController.getString("GroupContactStatusDescription", 2131165780), true);
          return (View)localObject1;
        }
        if (paramInt == MihanSettingsActivity.this.keepChatDesRow)
        {
          ((TextDescriptionCell)localObject1).setText(LocaleController.getString("keepChatDescription", 2131166833), true);
          return (View)localObject1;
        }
        paramViewGroup = (ViewGroup)localObject1;
      } while (paramInt != MihanSettingsActivity.this.saveInSDDesRow);
      ((TextDescriptionCell)localObject1).setText(LocaleController.getString("SaveInSDDes", 2131166667), false);
      return (View)localObject1;
    }
    
    public int getViewTypeCount()
    {
      return 8;
    }
    
    public boolean hasStableIds()
    {
      return false;
    }
    
    public boolean isEmpty()
    {
      return false;
    }
    
    public boolean isEnabled(int paramInt)
    {
      boolean bool = false;
      if (ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getBoolean("tabs", true)) {
        if ((paramInt == MihanSettingsActivity.this.enableTabsRow) || (paramInt == MihanSettingsActivity.this.defaultTabRow) || (paramInt == MihanSettingsActivity.this.activeTabsRow) || (paramInt == MihanSettingsActivity.this.tabletModeRow) || (paramInt == MihanSettingsActivity.this.showTabsCounterRow) || (paramInt == MihanSettingsActivity.this.countChatsRow) || (paramInt == MihanSettingsActivity.this.themeRow) || (paramInt == MihanSettingsActivity.this.persianDateRow) || (paramInt == MihanSettingsActivity.this.is24HoursRow) || (paramInt == MihanSettingsActivity.this.multiForwardRow) || (paramInt == MihanSettingsActivity.this.versionRow) || (paramInt == MihanSettingsActivity.this.typingStatusRow) || (paramInt == MihanSettingsActivity.this.ghostModeRow) || (paramInt == MihanSettingsActivity.this.hidePhoneRow) || (paramInt == MihanSettingsActivity.this.previewStickerRow) || (paramInt == MihanSettingsActivity.this.showMutualRow) || (paramInt == MihanSettingsActivity.this.showUserStatusRow) || (paramInt == MihanSettingsActivity.this.countNotMutedRow) || (paramInt == MihanSettingsActivity.this.swipeTabRow) || (paramInt == MihanSettingsActivity.this.showChatUserStatusRow) || (paramInt == MihanSettingsActivity.this.confirmatinAudioRow) || (paramInt == MihanSettingsActivity.this.passwordRow) || (paramInt == MihanSettingsActivity.this.toolBarRow) || (paramInt == MihanSettingsActivity.this.floatingDateRow) || (paramInt == MihanSettingsActivity.this.favAutoDownloadTabRow) || (paramInt == MihanSettingsActivity.this.directForwardRow) || (paramInt == MihanSettingsActivity.this.showExactCountRow) || (paramInt == MihanSettingsActivity.this.enableTabsInDirectFRow) || (paramInt == MihanSettingsActivity.this.chatBarRow) || (paramInt == MihanSettingsActivity.this.showPaintingRow) || (paramInt == MihanSettingsActivity.this.keepChatRow) || (paramInt == MihanSettingsActivity.this.saveSettingRow) || (paramInt == MihanSettingsActivity.this.restoreSettingRow) || (paramInt == MihanSettingsActivity.this.resetSettingRow) || (paramInt == MihanSettingsActivity.this.saveInSDRow)) {
          bool = true;
        }
      }
      while ((paramInt != MihanSettingsActivity.this.enableTabsRow) && (paramInt != MihanSettingsActivity.this.tabletModeRow) && (paramInt != MihanSettingsActivity.this.themeRow) && (paramInt != MihanSettingsActivity.this.persianDateRow) && (paramInt != MihanSettingsActivity.this.multiForwardRow) && (paramInt != MihanSettingsActivity.this.toolBarRow) && (paramInt != MihanSettingsActivity.this.versionRow) && (paramInt != MihanSettingsActivity.this.typingStatusRow) && (paramInt != MihanSettingsActivity.this.ghostModeRow) && (paramInt != MihanSettingsActivity.this.hidePhoneRow) && (paramInt != MihanSettingsActivity.this.previewStickerRow) && (paramInt != MihanSettingsActivity.this.showMutualRow) && (paramInt != MihanSettingsActivity.this.showUserStatusRow) && (paramInt != MihanSettingsActivity.this.showChatUserStatusRow) && (paramInt != MihanSettingsActivity.this.confirmatinAudioRow) && (paramInt != MihanSettingsActivity.this.floatingDateRow) && (paramInt != MihanSettingsActivity.this.passwordRow) && (paramInt != MihanSettingsActivity.this.directForwardRow) && (paramInt != MihanSettingsActivity.this.showExactCountRow) && (paramInt != MihanSettingsActivity.this.enableTabsInDirectFRow) && (paramInt != MihanSettingsActivity.this.chatBarRow) && (paramInt != MihanSettingsActivity.this.showPaintingRow) && (paramInt != MihanSettingsActivity.this.keepChatRow) && (paramInt != MihanSettingsActivity.this.saveSettingRow) && (paramInt != MihanSettingsActivity.this.restoreSettingRow) && (paramInt != MihanSettingsActivity.this.resetSettingRow) && (paramInt != MihanSettingsActivity.this.saveInSDRow)) {
        return bool;
      }
      return true;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\MihanSettingsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */