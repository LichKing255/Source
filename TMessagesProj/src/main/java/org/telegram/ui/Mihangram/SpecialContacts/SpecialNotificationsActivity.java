package org.telegram.ui.Mihangram.SpecialContacts;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings.System;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView<*>;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.ColorPickerView;
import org.telegram.ui.Components.LayoutHelper;

public class SpecialNotificationsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private ListView listView;
  private int rowCount = 0;
  private int settingsLedRow;
  private int settingsSoundRow;
  private int settingsVibrateRow;
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837810);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("NotificationsAndSounds", 2131166098));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          SpecialNotificationsActivity.this.finishFragment();
        }
      }
    });
    this.fragmentView = new FrameLayout(paramContext);
    Object localObject = (FrameLayout)this.fragmentView;
    this.listView = new ListView(paramContext);
    this.listView.setDivider(null);
    this.listView.setDividerHeight(0);
    this.listView.setVerticalScrollBarEnabled(false);
    AndroidUtilities.setListViewEdgeEffectColor(this.listView, AvatarDrawable.getProfileBackColorForId(5));
    ((FrameLayout)localObject).addView(this.listView);
    localObject = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
    ((FrameLayout.LayoutParams)localObject).width = -1;
    ((FrameLayout.LayoutParams)localObject).height = -1;
    this.listView.setLayoutParams((ViewGroup.LayoutParams)localObject);
    this.listView.setAdapter(new ListAdapter(paramContext));
    this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, final View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        Object localObject2;
        Object localObject3;
        String str;
        if (paramAnonymousInt == SpecialNotificationsActivity.this.settingsVibrateRow)
        {
          paramAnonymousAdapterView = new AlertDialog.Builder(SpecialNotificationsActivity.this.getParentActivity());
          paramAnonymousAdapterView.setTitle(LocaleController.getString("Vibrate", 2131166455));
          paramAnonymousView = LocaleController.getString("VibrationDisabled", 2131166457);
          localObject1 = LocaleController.getString("SettingsDefault", 2131166318);
          localObject2 = LocaleController.getString("SystemDefault", 2131166381);
          localObject3 = LocaleController.getString("Short", 2131166336);
          str = LocaleController.getString("Long", 2131165918);
          DialogInterface.OnClickListener local1 = new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("SpecialNotifications", 0).edit();
              if (paramAnonymous2Int == 0) {
                paramAnonymous2DialogInterface.putInt("vibrate_sc", 2);
              }
              for (;;)
              {
                paramAnonymous2DialogInterface.commit();
                if (SpecialNotificationsActivity.this.listView != null) {
                  SpecialNotificationsActivity.this.listView.invalidateViews();
                }
                return;
                if (paramAnonymous2Int == 1) {
                  paramAnonymous2DialogInterface.putInt("vibrate_sc", 0);
                } else if (paramAnonymous2Int == 2) {
                  paramAnonymous2DialogInterface.putInt("vibrate_sc", 4);
                } else if (paramAnonymous2Int == 3) {
                  paramAnonymous2DialogInterface.putInt("vibrate_sc", 1);
                } else if (paramAnonymous2Int == 4) {
                  paramAnonymous2DialogInterface.putInt("vibrate_sc", 3);
                }
              }
            }
          };
          paramAnonymousAdapterView.setItems(new CharSequence[] { paramAnonymousView, localObject1, localObject2, localObject3, str }, local1);
          paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
          SpecialNotificationsActivity.this.showDialog(paramAnonymousAdapterView.create());
        }
        label309:
        do
        {
          return;
          if (paramAnonymousInt == SpecialNotificationsActivity.this.settingsSoundRow) {
            for (;;)
            {
              try
              {
                localObject3 = new Intent("android.intent.action.RINGTONE_PICKER");
                ((Intent)localObject3).putExtra("android.intent.extra.ringtone.TYPE", 2);
                ((Intent)localObject3).putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                ((Intent)localObject3).putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("SpecialNotifications", 0);
                localObject1 = null;
                paramAnonymousView = null;
                localObject2 = Settings.System.DEFAULT_NOTIFICATION_URI;
                if (localObject2 != null) {
                  paramAnonymousView = ((Uri)localObject2).getPath();
                }
                str = paramAnonymousAdapterView.getString("sound_path_sc", paramAnonymousView);
                paramAnonymousAdapterView = (AdapterView<?>)localObject1;
                if (str != null)
                {
                  paramAnonymousAdapterView = (AdapterView<?>)localObject1;
                  if (!str.equals("NoSound"))
                  {
                    if (!str.equals(paramAnonymousView)) {
                      break label309;
                    }
                    paramAnonymousAdapterView = (AdapterView<?>)localObject2;
                  }
                }
                ((Intent)localObject3).putExtra("android.intent.extra.ringtone.EXISTING_URI", paramAnonymousAdapterView);
                SpecialNotificationsActivity.this.startActivityForResult((Intent)localObject3, 12);
                return;
              }
              catch (Exception paramAnonymousAdapterView)
              {
                FileLog.e("tmessages", paramAnonymousAdapterView);
                return;
              }
              paramAnonymousAdapterView = Uri.parse(str);
            }
          }
        } while ((paramAnonymousInt != SpecialNotificationsActivity.this.settingsLedRow) || (SpecialNotificationsActivity.this.getParentActivity() == null));
        paramAnonymousAdapterView = new LinearLayout(SpecialNotificationsActivity.this.getParentActivity());
        paramAnonymousAdapterView.setOrientation(1);
        paramAnonymousView = new ColorPickerView(SpecialNotificationsActivity.this.getParentActivity());
        paramAnonymousAdapterView.addView(paramAnonymousView, LayoutHelper.createLinear(-2, -2, 17));
        Object localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("SpecialNotifications", 0);
        if (((SharedPreferences)localObject1).contains("color_sc")) {
          paramAnonymousView.setOldCenterColor(((SharedPreferences)localObject1).getInt("color_sc", -16711936));
        }
        for (;;)
        {
          localObject1 = new AlertDialog.Builder(SpecialNotificationsActivity.this.getParentActivity());
          ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("LedColor", 2131165890));
          ((AlertDialog.Builder)localObject1).setView(paramAnonymousAdapterView);
          ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("Set", 2131166305), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("SpecialNotifications", 0).edit();
              paramAnonymous2DialogInterface.putInt("color_sc", paramAnonymousView.getColor());
              paramAnonymous2DialogInterface.commit();
              SpecialNotificationsActivity.this.listView.invalidateViews();
            }
          });
          ((AlertDialog.Builder)localObject1).setNeutralButton(LocaleController.getString("LedDisabled", 2131165891), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("SpecialNotifications", 0).edit();
              paramAnonymous2DialogInterface.putInt("color_sc", 0);
              paramAnonymous2DialogInterface.commit();
              SpecialNotificationsActivity.this.listView.invalidateViews();
            }
          });
          ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("Default", 2131165597), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("SpecialNotifications", 0).edit();
              paramAnonymous2DialogInterface.remove("color_sc");
              paramAnonymous2DialogInterface.commit();
              SpecialNotificationsActivity.this.listView.invalidateViews();
            }
          });
          SpecialNotificationsActivity.this.showDialog(((AlertDialog.Builder)localObject1).create());
          return;
          paramAnonymousView.setOldCenterColor(((SharedPreferences)localObject1).getInt("MessagesLed", -16711936));
        }
      }
    });
    return this.fragmentView;
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.notificationsSettingsUpdated) {
      this.listView.invalidateViews();
    }
  }
  
  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if ((paramInt2 != -1) || (paramIntent == null)) {
      return;
    }
    Uri localUri = (Uri)paramIntent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
    SharedPreferences.Editor localEditor = null;
    paramIntent = localEditor;
    Ringtone localRingtone;
    if (localUri != null)
    {
      localRingtone = RingtoneManager.getRingtone(ApplicationLoader.applicationContext, localUri);
      paramIntent = localEditor;
      if (localRingtone != null)
      {
        if (!localUri.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
          break label142;
        }
        paramIntent = LocaleController.getString("SoundDefault", 2131166360);
        localRingtone.stop();
      }
    }
    localEditor = ApplicationLoader.applicationContext.getSharedPreferences("SpecialNotifications", 0).edit();
    if (paramInt1 == 12)
    {
      if (paramIntent == null) {
        break label155;
      }
      localEditor.putString("sound_sc", paramIntent);
      localEditor.putString("sound_path_sc", localUri.toString());
    }
    for (;;)
    {
      localEditor.commit();
      this.listView.invalidateViews();
      return;
      label142:
      paramIntent = localRingtone.getTitle(getParentActivity());
      break;
      label155:
      localEditor.putString("sound_sc", "NoSound");
      localEditor.putString("sound_path_sc", "NoSound");
    }
  }
  
  public boolean onFragmentCreate()
  {
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.settingsLedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.settingsVibrateRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.settingsSoundRow = i;
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
    return super.onFragmentCreate();
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
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
      return true;
    }
    
    public int getCount()
    {
      return SpecialNotificationsActivity.this.rowCount;
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
      if ((paramInt == SpecialNotificationsActivity.this.settingsVibrateRow) || (paramInt == SpecialNotificationsActivity.this.settingsSoundRow)) {}
      while (paramInt != SpecialNotificationsActivity.this.settingsLedRow) {
        return 0;
      }
      return 1;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      int i = getItemViewType(paramInt);
      TextDetailSettingsCell localTextDetailSettingsCell;
      if (i == 0)
      {
        paramViewGroup = paramView;
        if (paramView == null) {
          paramViewGroup = new TextDetailSettingsCell(this.mContext);
        }
        localTextDetailSettingsCell = (TextDetailSettingsCell)paramViewGroup;
        paramView = this.mContext.getSharedPreferences("SpecialNotifications", 0);
        if (paramInt == SpecialNotificationsActivity.this.settingsVibrateRow)
        {
          paramInt = paramView.getInt("vibrate_sc", 3);
          if (paramInt == 0)
          {
            localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", 2131166455), LocaleController.getString("SettingsDefault", 2131166318), true);
            localObject = paramViewGroup;
          }
        }
      }
      do
      {
        do
        {
          do
          {
            return (View)localObject;
            if (paramInt == 1)
            {
              localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", 2131166455), LocaleController.getString("Short", 2131166336), true);
              return paramViewGroup;
            }
            if (paramInt == 2)
            {
              localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", 2131166455), LocaleController.getString("VibrationDisabled", 2131166457), true);
              return paramViewGroup;
            }
            if (paramInt == 3)
            {
              localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", 2131166455), LocaleController.getString("Long", 2131165918), true);
              return paramViewGroup;
            }
            localObject = paramViewGroup;
          } while (paramInt != 4);
          localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", 2131166455), LocaleController.getString("SystemDefault", 2131166381), true);
          return paramViewGroup;
          localObject = paramViewGroup;
        } while (paramInt != SpecialNotificationsActivity.this.settingsSoundRow);
        localObject = paramView.getString("sound_sc", LocaleController.getString("SoundDefault", 2131166360));
        paramView = (View)localObject;
        if (((String)localObject).equals("NoSound")) {
          paramView = LocaleController.getString("NoSound", 2131166027);
        }
        localTextDetailSettingsCell.setTextAndValue(LocaleController.getString("Sound", 2131166359), paramView, false);
        return paramViewGroup;
        localObject = paramView;
      } while (i != 1);
      paramViewGroup = paramView;
      if (paramView == null) {
        paramViewGroup = new TextColorCell(this.mContext);
      }
      paramView = (TextColorCell)paramViewGroup;
      Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("SpecialNotifications", 0);
      if (((SharedPreferences)localObject).contains("color_sc"))
      {
        paramView.setTextAndColor(LocaleController.getString("LedColor", 2131165890), ((SharedPreferences)localObject).getInt("color_sc", -16711936), true);
        return paramViewGroup;
      }
      paramView.setTextAndColor(LocaleController.getString("LedColor", 2131165890), ((SharedPreferences)localObject).getInt("MessagesLed", -16711936), true);
      return paramViewGroup;
    }
    
    public int getViewTypeCount()
    {
      return 2;
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
      return true;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\SpecialContacts\SpecialNotificationsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */