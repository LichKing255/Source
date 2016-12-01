package org.telegram.ui.Supergram.BatchWorks;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PlayerView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Supergram.DialogsLoader;
import org.telegram.ui.Supergram.Theming.MihanTheme;

public class BatchDialogsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static boolean dialogsLoaded;
  private boolean checkPermission = true;
  private BatchDialogsAdapter dialogsAdapter;
  float downX;
  float downY;
  private LinearLayout emptyView;
  boolean hasSelected;
  private int iconColor;
  private LinearLayoutManager layoutManager;
  int lightColor;
  private RecyclerListView listView;
  private long openedDialogId;
  private ActionBarMenuItem passcodeItem;
  private AlertDialog permissionDialog;
  private ProgressBar progressView;
  private int sIconColor;
  int sTab;
  private EmptyTextProgressView searchEmptyView;
  private String searchString;
  private boolean searchWas;
  private boolean searching;
  ArrayList<Long> selectedDialogIds = new ArrayList();
  ArrayList<TLRPC.TL_dialog> selectedDialogs = new ArrayList();
  private SlidingTabView slidingTabView;
  private int tabsHeight;
  float upX;
  float upY;
  
  @TargetApi(23)
  private void askForPermissons()
  {
    Activity localActivity = getParentActivity();
    if (localActivity == null) {
      return;
    }
    ArrayList localArrayList = new ArrayList();
    if (localActivity.checkSelfPermission("android.permission.READ_CONTACTS") != 0)
    {
      localArrayList.add("android.permission.READ_CONTACTS");
      localArrayList.add("android.permission.WRITE_CONTACTS");
      localArrayList.add("android.permission.GET_ACCOUNTS");
    }
    if (localActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0)
    {
      localArrayList.add("android.permission.READ_EXTERNAL_STORAGE");
      localArrayList.add("android.permission.WRITE_EXTERNAL_STORAGE");
    }
    localActivity.requestPermissions((String[])localArrayList.toArray(new String[localArrayList.size()]), 1);
  }
  
  private void createTabs(Context paramContext, FrameLayout paramFrameLayout)
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
    int i = localSharedPreferences.getInt("selected_tab", 6);
    this.sTab = i;
    if (localSharedPreferences.getInt("last_tab", 8) == 8)
    {
      SharedPreferences.Editor localEditor = localSharedPreferences.edit();
      localEditor.putInt("selected_tab", i);
      localEditor.commit();
    }
    this.slidingTabView = new SlidingTabView(paramContext, i);
    if (localSharedPreferences.getBoolean("tab_bot", true)) {
      this.slidingTabView.addImageTab(0);
    }
    if (localSharedPreferences.getBoolean("tab_channel", true)) {
      this.slidingTabView.addImageTab(1);
    }
    if (localSharedPreferences.getBoolean("tab_supergroup", true)) {
      this.slidingTabView.addImageTab(2);
    }
    if (localSharedPreferences.getBoolean("tab_group", true)) {
      this.slidingTabView.addImageTab(3);
    }
    if (localSharedPreferences.getBoolean("tab_contact", true)) {
      this.slidingTabView.addImageTab(4);
    }
    if (localSharedPreferences.getBoolean("tab_favorite", true)) {
      this.slidingTabView.addImageTab(5);
    }
    this.slidingTabView.addImageTab(6);
    if (localSharedPreferences.getBoolean("tab_unread", true)) {
      this.slidingTabView.addImageTab(7);
    }
    paramFrameLayout.addView(this.slidingTabView, LayoutHelper.createFrame(-1, this.tabsHeight));
  }
  
  private ArrayList<TLRPC.TL_dialog> getDialogsArray()
  {
    return new DialogsLoader().getDialogsArray();
  }
  
  private void onSwipe(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getAction())
    {
    }
    label232:
    label241:
    for (;;)
    {
      return;
      this.downX = paramMotionEvent.getX();
      this.downY = paramMotionEvent.getY();
      return;
      if (ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getBoolean("tabs", true))
      {
        this.upX = paramMotionEvent.getX();
        this.upY = paramMotionEvent.getY();
        float f1 = this.downX - this.upX;
        float f2 = this.downY;
        float f3 = this.upY;
        if ((Math.abs(f1) > 40.0F) && (Math.abs(f2 - f3) < 100.0F))
        {
          int i;
          if (f1 < 0.0F)
          {
            i = this.slidingTabView.getSeletedTab() - 1;
            if (i >= 0) {
              this.slidingTabView.didSelectTab(i);
            }
          }
          else if (f1 > 0.0F)
          {
            i = this.slidingTabView.getSeletedTab() + 1;
            if (i >= this.slidingTabView.getTabCount()) {
              break label232;
            }
            this.slidingTabView.didSelectTab(i);
          }
          for (;;)
          {
            if (this.dialogsAdapter == null) {
              break label241;
            }
            this.dialogsAdapter.notifyDataSetChanged();
            return;
            this.slidingTabView.didSelectTab(this.slidingTabView.getTabCount() - 1);
            break;
            this.slidingTabView.didSelectTab(0);
          }
        }
      }
    }
  }
  
  private void updateColors()
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    int i = MihanTheme.getActionBarColor(localSharedPreferences);
    int j = MihanTheme.getActionBarGradientFlag(localSharedPreferences);
    int k = MihanTheme.getActionBarGradientColor(localSharedPreferences);
    if (j != 0)
    {
      GradientDrawable localGradientDrawable = MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j));
      this.actionBar.setBackgroundDrawable(localGradientDrawable);
      i = MihanTheme.getTabsBackgroundColor(localSharedPreferences);
      j = MihanTheme.getTabsGradientFlag(localSharedPreferences);
      k = MihanTheme.getTabsGradientColor(localSharedPreferences);
      if (j == 0) {
        break label142;
      }
      localGradientDrawable = MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j));
      this.slidingTabView.setBackgroundDrawable(localGradientDrawable);
    }
    for (;;)
    {
      this.sIconColor = MihanTheme.getSelectedTabIconColor(localSharedPreferences);
      this.iconColor = MihanTheme.getTabsIconColor(localSharedPreferences);
      i = this.slidingTabView.getSeletedTab();
      this.slidingTabView.didSelectTab(i);
      return;
      this.actionBar.setBackgroundColor(i);
      break;
      label142:
      this.slidingTabView.setBackgroundColor(i);
    }
  }
  
  private void updateLayout()
  {
    Object localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
    Object localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    boolean bool1 = ((SharedPreferences)localObject1).getBoolean("tabs", true);
    boolean bool2 = ((SharedPreferences)localObject2).getBoolean("move_tabs", false);
    localObject1 = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
    localObject2 = (FrameLayout.LayoutParams)this.slidingTabView.getLayoutParams();
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.emptyView.getLayoutParams();
    if (!bool1)
    {
      if (this.slidingTabView.getVisibility() == 0) {
        this.slidingTabView.setVisibility(8);
      }
      ((FrameLayout.LayoutParams)localObject1).setMargins(AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F));
      this.listView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      localLayoutParams.setMargins(AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F));
      this.emptyView.setLayoutParams(localLayoutParams);
      return;
    }
    if (bool2)
    {
      if (this.slidingTabView.getVisibility() == 8) {
        this.slidingTabView.setVisibility(0);
      }
      ((FrameLayout.LayoutParams)localObject2).gravity = 80;
      this.slidingTabView.setLayoutParams((ViewGroup.LayoutParams)localObject2);
      ((FrameLayout.LayoutParams)localObject2).setMargins(AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F));
      this.slidingTabView.setLayoutParams((ViewGroup.LayoutParams)localObject2);
      ((FrameLayout.LayoutParams)localObject1).setMargins(AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(this.tabsHeight));
      this.listView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      localLayoutParams.setMargins(AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(this.tabsHeight));
      this.emptyView.setLayoutParams(localLayoutParams);
      return;
    }
    if (this.slidingTabView.getVisibility() == 8) {
      this.slidingTabView.setVisibility(0);
    }
    ((FrameLayout.LayoutParams)localObject2).gravity = 48;
    this.slidingTabView.setLayoutParams((ViewGroup.LayoutParams)localObject2);
    ((FrameLayout.LayoutParams)localObject1).setMargins(AndroidUtilities.dp(0.0F), AndroidUtilities.dp(this.tabsHeight), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F));
    this.listView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
    localLayoutParams.setMargins(AndroidUtilities.dp(0.0F), AndroidUtilities.dp(this.tabsHeight), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F));
    this.emptyView.setLayoutParams(localLayoutParams);
  }
  
  private void updatePasscodeButton()
  {
    if (this.passcodeItem == null) {
      return;
    }
    if ((UserConfig.passcodeHash.length() != 0) && (!this.searching))
    {
      this.passcodeItem.setVisibility(0);
      if (UserConfig.appLocked)
      {
        this.passcodeItem.setIcon(2130837944);
        return;
      }
      this.passcodeItem.setIcon(2130837945);
      return;
    }
    this.passcodeItem.setVisibility(8);
  }
  
  private void updateVisibleRows(int paramInt)
  {
    if (this.listView == null) {
      return;
    }
    int k = this.listView.getChildCount();
    int i = 0;
    label19:
    Object localObject;
    boolean bool;
    if (i < k)
    {
      localObject = this.listView.getChildAt(i);
      if (!(localObject instanceof BatchDialogCell)) {
        break label174;
      }
      if (this.listView.getAdapter() == this.dialogsAdapter)
      {
        localObject = (BatchDialogCell)localObject;
        if ((paramInt & 0x800) == 0) {
          break label119;
        }
        ((BatchDialogCell)localObject).checkCurrentDialogIndex();
        if (AndroidUtilities.isTablet())
        {
          if (((BatchDialogCell)localObject).getDialogId() != this.openedDialogId) {
            break label113;
          }
          bool = true;
          label99:
          ((BatchDialogCell)localObject).setDialogSelected(bool);
        }
      }
    }
    for (;;)
    {
      i += 1;
      break label19;
      break;
      label113:
      bool = false;
      break label99;
      label119:
      if ((paramInt & 0x200) != 0)
      {
        if (AndroidUtilities.isTablet())
        {
          if (((BatchDialogCell)localObject).getDialogId() == this.openedDialogId) {}
          for (bool = true;; bool = false)
          {
            ((BatchDialogCell)localObject).setDialogSelected(bool);
            break;
          }
        }
      }
      else
      {
        ((BatchDialogCell)localObject).update(paramInt);
        continue;
        label174:
        if ((localObject instanceof UserCell))
        {
          ((UserCell)localObject).update(paramInt);
        }
        else if ((localObject instanceof ProfileSearchCell))
        {
          ((ProfileSearchCell)localObject).update(paramInt);
        }
        else if ((localObject instanceof RecyclerListView))
        {
          localObject = (RecyclerListView)localObject;
          int m = ((RecyclerListView)localObject).getChildCount();
          int j = 0;
          while (j < m)
          {
            View localView = ((RecyclerListView)localObject).getChildAt(j);
            if ((localView instanceof HintDialogCell)) {
              ((HintDialogCell)localView).checkUnreadCounter(paramInt);
            }
            j += 1;
          }
        }
      }
    }
  }
  
  protected ActionBar createActionBar(Context paramContext)
  {
    paramContext = new ActionBar(paramContext);
    paramContext.setItemsBackgroundColor(-12554860);
    return paramContext;
  }
  
  public View createView(final Context paramContext)
  {
    this.searching = false;
    this.searchWas = false;
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        Theme.loadRecources(paramContext);
      }
    });
    Object localObject2 = this.actionBar.createMenu();
    if (this.searchString == null)
    {
      this.passcodeItem = ((ActionBarMenu)localObject2).addItem(1, 2130837944);
      updatePasscodeButton();
    }
    ((ActionBarMenu)localObject2).addItem(2, 2130837901);
    ((ActionBarMenu)localObject2).addItem(3, 2130837902);
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    final Object localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
    final Object localObject3 = ((SharedPreferences)localObject1).edit();
    this.lightColor = -52;
    this.actionBar.setBackButtonImage(2130837829);
    this.actionBar.setTitle(LocaleController.getString("SelectChat", 2131166279) + " ( " + this.selectedDialogIds.size() + " )");
    this.actionBar.setGhostImage(false);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1)
        {
          localObject3.putInt("selected_tab", BatchDialogsActivity.this.sTab);
          localObject3.commit();
          BatchDialogsActivity.this.finishFragment();
        }
        label191:
        label246:
        label434:
        do
        {
          return;
          if (paramAnonymousInt == 1)
          {
            if (!UserConfig.appLocked) {}
            for (boolean bool = true;; bool = false)
            {
              UserConfig.appLocked = bool;
              UserConfig.saveConfig(false);
              BatchDialogsActivity.this.updatePasscodeButton();
              return;
            }
          }
          if (paramAnonymousInt == 2)
          {
            if (!BatchDialogsActivity.this.hasSelected)
            {
              paramAnonymousInt = 0;
              while (paramAnonymousInt < BatchDialogsActivity.this.getDialogsArray().size())
              {
                localObject = (TLRPC.TL_dialog)BatchDialogsActivity.this.getDialogsArray().get(paramAnonymousInt);
                if (!BatchDialogsActivity.this.selectedDialogIds.contains(Long.valueOf(((TLRPC.TL_dialog)localObject).id)))
                {
                  BatchDialogsActivity.this.selectedDialogs.add(localObject);
                  BatchDialogsActivity.this.selectedDialogIds.add(Long.valueOf(((TLRPC.TL_dialog)localObject).id));
                }
                paramAnonymousInt += 1;
              }
              BatchDialogsActivity.this.hasSelected = true;
              BatchDialogsActivity.this.actionBar.setTitle(LocaleController.getString("SelectChat", 2131166279) + " ( " + BatchDialogsActivity.this.selectedDialogIds.size() + " )");
              paramAnonymousInt = 0;
              if (paramAnonymousInt < BatchDialogsActivity.this.listView.getAdapter().getItemCount())
              {
                localObject = BatchDialogsActivity.this.listView.getChildAt(paramAnonymousInt);
                if ((localObject instanceof BatchDialogCell))
                {
                  localObject = (BatchDialogCell)localObject;
                  if (!BatchDialogsActivity.this.selectedDialogIds.contains(Long.valueOf(((BatchDialogCell)localObject).getDialogId()))) {
                    break label434;
                  }
                  ((BatchDialogCell)localObject).setBackgroundColor(BatchDialogsActivity.this.lightColor);
                }
              }
            }
            for (;;)
            {
              paramAnonymousInt += 1;
              break label246;
              break;
              paramAnonymousInt = 0;
              while (paramAnonymousInt < BatchDialogsActivity.this.getDialogsArray().size())
              {
                localObject = (TLRPC.TL_dialog)BatchDialogsActivity.this.getDialogsArray().get(paramAnonymousInt);
                if (BatchDialogsActivity.this.selectedDialogIds.contains(Long.valueOf(((TLRPC.TL_dialog)localObject).id)))
                {
                  BatchDialogsActivity.this.selectedDialogs.remove(localObject);
                  BatchDialogsActivity.this.selectedDialogIds.remove(Long.valueOf(((TLRPC.TL_dialog)localObject).id));
                }
                paramAnonymousInt += 1;
              }
              BatchDialogsActivity.this.hasSelected = false;
              break label191;
              ((BatchDialogCell)localObject).setBackgroundColor(-1);
            }
          }
        } while (paramAnonymousInt != 3);
        localObject3.putInt("selected_tab", BatchDialogsActivity.this.sTab);
        localObject3.putBoolean("multi_task_activity", false);
        localObject3.commit();
        long l;
        switch (localObject1.getInt("op_type", 1))
        {
        default: 
          return;
        case 1: 
          paramAnonymousInt = 0;
          while (paramAnonymousInt < BatchDialogsActivity.this.selectedDialogIds.size())
          {
            l = ((Long)BatchDialogsActivity.this.selectedDialogIds.get(paramAnonymousInt)).longValue();
            localObject = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(l));
            MessagesController.getInstance().markDialogAsRead(l, Math.max(0, ((TLRPC.TL_dialog)localObject).top_message), Math.max(0, ((TLRPC.TL_dialog)localObject).top_message), ((TLRPC.TL_dialog)localObject).last_message_date, true, false);
            paramAnonymousInt += 1;
          }
          BatchDialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
          BatchDialogsActivity.this.finishFragment();
          return;
        case 2: 
          paramAnonymousInt = 0;
          while (paramAnonymousInt < BatchDialogsActivity.this.selectedDialogIds.size())
          {
            l = ((Long)BatchDialogsActivity.this.selectedDialogIds.get(paramAnonymousInt)).longValue();
            if (!MessagesController.getInstance().isDialogMuted(l))
            {
              localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
              ((SharedPreferences.Editor)localObject).putInt("notify2_" + l, 2);
              MessagesStorage.getInstance().setDialogFlags(l, 1L);
              ((SharedPreferences.Editor)localObject).commit();
              localObject = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(l));
              if (localObject != null)
              {
                ((TLRPC.TL_dialog)localObject).notify_settings = new TLRPC.TL_peerNotifySettings();
                ((TLRPC.TL_dialog)localObject).notify_settings.mute_until = Integer.MAX_VALUE;
              }
              NotificationsController.updateServerNotificationsSettings(l);
              NotificationsController.getInstance().removeNotificationsForDialog(l);
            }
            paramAnonymousInt += 1;
          }
          BatchDialogsActivity.this.finishFragment();
          return;
        case 6: 
          paramAnonymousInt = 0;
          while (paramAnonymousInt < BatchDialogsActivity.this.selectedDialogIds.size())
          {
            l = ((Long)BatchDialogsActivity.this.selectedDialogIds.get(paramAnonymousInt)).longValue();
            if (MessagesController.getInstance().isDialogMuted(l))
            {
              localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
              ((SharedPreferences.Editor)localObject).putInt("notify2_" + l, 0);
              MessagesStorage.getInstance().setDialogFlags(l, 0L);
              ((SharedPreferences.Editor)localObject).commit();
              localObject = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(l));
              if (localObject != null) {
                ((TLRPC.TL_dialog)localObject).notify_settings = new TLRPC.TL_peerNotifySettings();
              }
              NotificationsController.updateServerNotificationsSettings(l);
            }
            paramAnonymousInt += 1;
          }
          BatchDialogsActivity.this.finishFragment();
          return;
        case 3: 
          paramAnonymousInt = 0;
          while (paramAnonymousInt < BatchDialogsActivity.this.selectedDialogIds.size())
          {
            l = ((Long)BatchDialogsActivity.this.selectedDialogIds.get(paramAnonymousInt)).longValue();
            if (!localObject1.contains("fav_" + String.valueOf(l)))
            {
              localObject3.putInt("fav_" + String.valueOf(l), (int)l);
              localObject3.commit();
            }
            paramAnonymousInt += 1;
          }
          BatchDialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
          BatchDialogsActivity.this.finishFragment();
          return;
        case 4: 
          localObject = new AlertDialog.Builder(BatchDialogsActivity.this.getParentActivity());
          ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165338));
          ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("AreYouSureToContinue", 2131166617));
          ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              paramAnonymous2Int = 0;
              if (paramAnonymous2Int < BatchDialogsActivity.this.selectedDialogIds.size())
              {
                long l = ((Long)BatchDialogsActivity.this.selectedDialogIds.get(paramAnonymous2Int)).longValue();
                if (DialogObject.isChannel((TLRPC.TL_dialog)BatchDialogsActivity.this.selectedDialogs.get(paramAnonymous2Int))) {
                  MessagesController.getInstance().deleteDialog(l, 2);
                }
                for (;;)
                {
                  paramAnonymous2Int += 1;
                  break;
                  MessagesController.getInstance().deleteDialog(l, 1);
                }
              }
              BatchDialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
              BatchDialogsActivity.this.finishFragment();
            }
          });
          ((AlertDialog.Builder)localObject).setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
          BatchDialogsActivity.this.showDialog(((AlertDialog.Builder)localObject).create());
          return;
        }
        Object localObject = new AlertDialog.Builder(BatchDialogsActivity.this.getParentActivity());
        ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165338));
        ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("AreYouSureToContinue", 2131166617));
        ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
          {
            paramAnonymous2Int = 0;
            while (paramAnonymous2Int < BatchDialogsActivity.this.selectedDialogIds.size())
            {
              long l = ((Long)BatchDialogsActivity.this.selectedDialogIds.get(paramAnonymous2Int)).longValue();
              if (DialogObject.isChannel((TLRPC.TL_dialog)BatchDialogsActivity.this.selectedDialogs.get(paramAnonymous2Int)))
              {
                MessagesController.getInstance().deleteUserFromChat((int)-l, UserConfig.getCurrentUser(), null);
                if (AndroidUtilities.isTablet()) {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[] { Long.valueOf(l) });
                }
                paramAnonymous2Int += 1;
              }
              else
              {
                int j = (int)l;
                int k = (int)(l >> 32);
                int i;
                if ((j < 0) && (k != 1))
                {
                  i = 1;
                  label138:
                  Object localObject = null;
                  paramAnonymous2DialogInterface = (DialogInterface)localObject;
                  if (i == 0)
                  {
                    paramAnonymous2DialogInterface = (DialogInterface)localObject;
                    if (j > 0)
                    {
                      paramAnonymous2DialogInterface = (DialogInterface)localObject;
                      if (k != 1) {
                        paramAnonymous2DialogInterface = MessagesController.getInstance().getUser(Integer.valueOf(j));
                      }
                    }
                  }
                  if ((paramAnonymous2DialogInterface == null) || (!paramAnonymous2DialogInterface.bot)) {
                    break label278;
                  }
                  j = 1;
                  label191:
                  if (i == 0) {
                    break label319;
                  }
                  paramAnonymous2DialogInterface = MessagesController.getInstance().getChat(Integer.valueOf((int)-l));
                  if ((paramAnonymous2DialogInterface == null) || (!ChatObject.isNotInChat(paramAnonymous2DialogInterface))) {
                    break label284;
                  }
                  MessagesController.getInstance().deleteDialog(l, 0);
                }
                for (;;)
                {
                  if (j != 0) {
                    MessagesController.getInstance().blockUser((int)l);
                  }
                  if (!AndroidUtilities.isTablet()) {
                    break;
                  }
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[] { Long.valueOf(l) });
                  break;
                  i = 0;
                  break label138;
                  label278:
                  j = 0;
                  break label191;
                  label284:
                  MessagesController.getInstance().deleteUserFromChat((int)-l, MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), null);
                  MessagesController.getInstance().deleteDialog(l, 0);
                  continue;
                  label319:
                  MessagesController.getInstance().deleteDialog(l, 0);
                }
              }
            }
            BatchDialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
            BatchDialogsActivity.this.finishFragment();
          }
        });
        ((AlertDialog.Builder)localObject).setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
        BatchDialogsActivity.this.showDialog(((AlertDialog.Builder)localObject).create());
      }
    });
    MihanTheme.setColorFilter(ApplicationLoader.applicationContext.getResources().getDrawable(2130837829), localSharedPreferences.getInt("theme_action_icon_color", -1));
    MihanTheme.setColorFilter(ApplicationLoader.applicationContext.getResources().getDrawable(2130837901), localSharedPreferences.getInt("theme_action_icon_color", -1));
    MihanTheme.setColorFilter(ApplicationLoader.applicationContext.getResources().getDrawable(2130837902), localSharedPreferences.getInt("theme_action_icon_color", -1));
    this.actionBar.setTitleColor(MihanTheme.getActionBarTitleColor(localSharedPreferences));
    int i = 0;
    while (i < 4)
    {
      if (((ActionBarMenu)localObject2).getItem(i) != null) {
        MihanTheme.setColorFilter(((ActionBarMenu)localObject2).getItem(i).getImageView().getDrawable(), localSharedPreferences.getInt("theme_action_icon_color", -1));
      }
      i += 1;
    }
    localObject3 = new FrameLayout(paramContext);
    this.fragmentView = ((View)localObject3);
    boolean bool = ((SharedPreferences)localObject1).getBoolean("swipe_tabs", false);
    this.tabsHeight = localSharedPreferences.getInt("theme_tabs_height", 42);
    createTabs(paramContext, (FrameLayout)localObject3);
    this.listView = new RecyclerListView(paramContext);
    this.listView.setVerticalScrollBarEnabled(true);
    this.listView.setItemAnimator(null);
    this.listView.setInstantClick(true);
    this.listView.setLayoutAnimation(null);
    this.layoutManager = new LinearLayoutManager(paramContext)
    {
      public boolean supportsPredictiveItemAnimations()
      {
        return false;
      }
    };
    this.layoutManager.setOrientation(1);
    this.listView.setLayoutManager(this.layoutManager);
    ((FrameLayout)localObject3).addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        if ((BatchDialogsActivity.this.listView == null) || (BatchDialogsActivity.this.listView.getAdapter() == null)) {}
        TLRPC.TL_dialog localTL_dialog;
        do
        {
          do
          {
            return;
          } while (BatchDialogsActivity.this.listView.getAdapter() != BatchDialogsActivity.this.dialogsAdapter);
          localTL_dialog = BatchDialogsActivity.this.dialogsAdapter.getItem(paramAnonymousInt);
        } while (localTL_dialog == null);
        if (BatchDialogsActivity.this.selectedDialogIds.contains(Long.valueOf(localTL_dialog.id)))
        {
          BatchDialogsActivity.this.selectedDialogs.remove(localTL_dialog);
          BatchDialogsActivity.this.selectedDialogIds.remove(Long.valueOf(localTL_dialog.id));
          paramAnonymousView.setBackgroundColor(-1);
        }
        for (;;)
        {
          BatchDialogsActivity.this.actionBar.setTitle(LocaleController.getString("SelectChat", 2131166279) + " ( " + BatchDialogsActivity.this.selectedDialogIds.size() + " )");
          return;
          BatchDialogsActivity.this.selectedDialogs.add(localTL_dialog);
          BatchDialogsActivity.this.selectedDialogIds.add(Long.valueOf(localTL_dialog.id));
          paramAnonymousView.setBackgroundColor(BatchDialogsActivity.this.lightColor);
        }
      }
    });
    this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
      {
        if ((paramAnonymousInt == 1) && (BatchDialogsActivity.this.searching) && (BatchDialogsActivity.this.searchWas)) {
          AndroidUtilities.hideKeyboard(BatchDialogsActivity.this.getParentActivity().getCurrentFocus());
        }
      }
      
      public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        paramAnonymousInt1 = BatchDialogsActivity.this.layoutManager.findFirstVisibleItemPosition();
        paramAnonymousInt1 = Math.abs(BatchDialogsActivity.this.layoutManager.findLastVisibleItemPosition() - paramAnonymousInt1);
        paramAnonymousInt2 = paramAnonymousRecyclerView.getAdapter().getItemCount();
        if (paramAnonymousInt1 + 1 > 0)
        {
          boolean bool;
          if (BatchDialogsActivity.this.layoutManager.findLastVisibleItemPosition() >= BatchDialogsActivity.this.getDialogsArray().size() - 10)
          {
            paramAnonymousRecyclerView = MessagesController.getInstance();
            if (!MessagesController.getInstance().dialogsEndReached)
            {
              bool = true;
              paramAnonymousRecyclerView.loadDialogs(-1, 100, bool);
            }
          }
          else
          {
            paramAnonymousInt1 = 0;
            label94:
            if (paramAnonymousInt1 >= paramAnonymousInt2) {
              return;
            }
            paramAnonymousRecyclerView = BatchDialogsActivity.this.listView.getChildAt(paramAnonymousInt1);
            if ((paramAnonymousRecyclerView instanceof BatchDialogCell))
            {
              paramAnonymousRecyclerView = (BatchDialogCell)paramAnonymousRecyclerView;
              if (!BatchDialogsActivity.this.selectedDialogIds.contains(Long.valueOf(paramAnonymousRecyclerView.getDialogId()))) {
                break label167;
              }
              paramAnonymousRecyclerView.setBackgroundColor(BatchDialogsActivity.this.lightColor);
            }
          }
          for (;;)
          {
            paramAnonymousInt1 += 1;
            break label94;
            bool = false;
            break;
            label167:
            paramAnonymousRecyclerView.setBackgroundColor(-1);
          }
        }
      }
    });
    if (bool) {
      this.listView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          BatchDialogsActivity.this.onSwipe(paramAnonymousMotionEvent);
          return false;
        }
      });
    }
    this.searchEmptyView = new EmptyTextProgressView(paramContext);
    this.searchEmptyView.setVisibility(8);
    this.searchEmptyView.setShowAtCenter(true);
    this.searchEmptyView.setText(LocaleController.getString("NoResult", 2131166020));
    this.emptyView = new LinearLayout(paramContext);
    this.emptyView.setOrientation(1);
    this.emptyView.setVisibility(8);
    this.emptyView.setGravity(17);
    this.emptyView.setClickable(true);
    ((FrameLayout)localObject3).addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
    if (bool)
    {
      this.emptyView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          BatchDialogsActivity.this.onSwipe(paramAnonymousMotionEvent);
          return false;
        }
      });
      localObject1 = new TextView(paramContext);
      ((TextView)localObject1).setText(LocaleController.getString("NoChats", 2131166002));
      ((TextView)localObject1).setTextColor(MihanTheme.getDialogNameColor(localSharedPreferences));
      ((TextView)localObject1).setGravity(17);
      ((TextView)localObject1).setTextSize(1, 20.0F);
      this.emptyView.addView((View)localObject1, LayoutHelper.createLinear(-2, -2));
      ((TextView)localObject1).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      TextView localTextView = new TextView(paramContext);
      localObject2 = LocaleController.getString("NoChatsHelp", 2131166003);
      localObject1 = localObject2;
      if (AndroidUtilities.isTablet())
      {
        localObject1 = localObject2;
        if (!AndroidUtilities.isSmallTablet()) {
          localObject1 = ((String)localObject2).replace('\n', ' ');
        }
      }
      localTextView.setText((CharSequence)localObject1);
      localTextView.setTextColor(MihanTheme.getDialogNameColor(localSharedPreferences));
      localTextView.setTextSize(1, 15.0F);
      localTextView.setGravity(17);
      localTextView.setPadding(AndroidUtilities.dp(8.0F), AndroidUtilities.dp(6.0F), AndroidUtilities.dp(8.0F), 0);
      localTextView.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
      this.emptyView.addView(localTextView, LayoutHelper.createLinear(-2, -2));
      localTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.progressView = new ProgressBar(paramContext);
      this.progressView.setVisibility(8);
      ((FrameLayout)localObject3).addView(this.progressView, LayoutHelper.createFrame(-2, -2, 17));
      updateLayout();
      if (this.searchString == null)
      {
        this.dialogsAdapter = new BatchDialogsAdapter(paramContext, 0);
        if ((AndroidUtilities.isTablet()) && (this.openedDialogId != 0L)) {
          this.dialogsAdapter.setOpenedDialogId(this.openedDialogId);
        }
        this.listView.setAdapter(this.dialogsAdapter);
      }
      if ((!MessagesController.getInstance().loadingDialogs) || (!MessagesController.getInstance().dialogs.isEmpty())) {
        break label1102;
      }
      this.searchEmptyView.setVisibility(8);
      this.emptyView.setVisibility(8);
      this.listView.setEmptyView(this.progressView);
    }
    for (;;)
    {
      if (this.searchString != null) {
        this.actionBar.openSearchField(this.searchString);
      }
      ((FrameLayout)localObject3).addView(new PlayerView(paramContext, this), LayoutHelper.createFrame(-1, 39.0F, 51, 0.0F, -36.0F, 0.0F, 0.0F));
      return this.fragmentView;
      this.emptyView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      break;
      label1102:
      this.searchEmptyView.setVisibility(8);
      this.progressView.setVisibility(8);
      this.listView.setEmptyView(this.emptyView);
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.dialogsNeedReload) {
      if (this.dialogsAdapter != null)
      {
        if (this.dialogsAdapter.isDataSetChanged()) {
          this.dialogsAdapter.notifyDataSetChanged();
        }
      }
      else if (this.listView == null) {}
    }
    label418:
    do
    {
      for (;;)
      {
        try
        {
          if ((MessagesController.getInstance().loadingDialogs) && (MessagesController.getInstance().dialogs.isEmpty()))
          {
            this.searchEmptyView.setVisibility(8);
            this.emptyView.setVisibility(8);
            this.listView.setEmptyView(this.progressView);
            if (paramInt != NotificationCenter.needReloadRecentDialogsSearch) {
              break label418;
            }
            return;
            updateVisibleRows(2048);
            break;
          }
          this.progressView.setVisibility(8);
          if ((this.searching) && (this.searchWas))
          {
            this.emptyView.setVisibility(8);
            this.listView.setEmptyView(this.searchEmptyView);
            continue;
          }
        }
        catch (Exception paramVarArgs)
        {
          FileLog.e("tmessages", paramVarArgs);
          continue;
          this.searchEmptyView.setVisibility(8);
          this.listView.setEmptyView(this.emptyView);
          continue;
        }
        if (paramInt == NotificationCenter.emojiDidLoaded) {
          updateVisibleRows(0);
        } else if (paramInt == NotificationCenter.updateInterfaces) {
          updateVisibleRows(((Integer)paramVarArgs[0]).intValue());
        } else if (paramInt == NotificationCenter.appDidLogout) {
          dialogsLoaded = false;
        } else if (paramInt == NotificationCenter.encryptedChatUpdated) {
          updateVisibleRows(0);
        } else if (paramInt == NotificationCenter.contactsDidLoaded) {
          updateVisibleRows(0);
        } else if (paramInt == NotificationCenter.openedChatChanged)
        {
          if (AndroidUtilities.isTablet())
          {
            boolean bool = ((Boolean)paramVarArgs[1]).booleanValue();
            long l = ((Long)paramVarArgs[0]).longValue();
            if (bool) {
              if (l != this.openedDialogId) {}
            }
            for (this.openedDialogId = 0L;; this.openedDialogId = l)
            {
              if (this.dialogsAdapter != null) {
                this.dialogsAdapter.setOpenedDialogId(this.openedDialogId);
              }
              updateVisibleRows(512);
              break;
            }
          }
        }
        else if (paramInt == NotificationCenter.notificationsSettingsUpdated) {
          updateVisibleRows(0);
        } else if ((paramInt == NotificationCenter.messageReceivedByAck) || (paramInt == NotificationCenter.messageReceivedByServer) || (paramInt == NotificationCenter.messageSendError)) {
          updateVisibleRows(4096);
        } else if (paramInt == NotificationCenter.didSetPasscode) {
          updatePasscodeButton();
        }
      }
    } while (paramInt != NotificationCenter.didLoadedReplyMessages);
    updateVisibleRows(0);
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
  }
  
  protected void onDialogDismiss(Dialog paramDialog)
  {
    super.onDialogDismiss(paramDialog);
    if ((this.permissionDialog != null) && (paramDialog == this.permissionDialog) && (getParentActivity() != null)) {
      askForPermissons();
    }
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.swipeBackEnabled = false;
    if (this.searchString == null)
    {
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatUpdated);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.openedChatChanged);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByAck);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageSendError);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetPasscode);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.didLoadedReplyMessages);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.reloadHints);
    }
    if (!dialogsLoaded)
    {
      MessagesController.getInstance().loadDialogs(0, 100, true);
      ContactsController.getInstance().checkInviteText();
      dialogsLoaded = true;
    }
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    if (this.searchString == null)
    {
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatUpdated);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.openedChatChanged);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByAck);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageSendError);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetPasscode);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didLoadedReplyMessages);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.reloadHints);
    }
  }
  
  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramInt == 1)
    {
      int i = 0;
      if (i < paramArrayOfString.length)
      {
        if ((paramArrayOfInt.length <= i) || (paramArrayOfInt[i] != 0)) {}
        for (;;)
        {
          i += 1;
          break;
          String str = paramArrayOfString[i];
          paramInt = -1;
          switch (str.hashCode())
          {
          }
          for (;;)
          {
            switch (paramInt)
            {
            default: 
              break;
            case 0: 
              ContactsController.getInstance().readContacts();
              break;
              if (str.equals("android.permission.READ_CONTACTS"))
              {
                paramInt = 0;
                continue;
                if (str.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                  paramInt = 1;
                }
              }
              break;
            }
          }
          ImageLoader.getInstance().checkMediaPaths();
        }
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
    int i = ((SharedPreferences)localObject).getInt("last_tab", 8);
    if (i != 8)
    {
      localObject = ((SharedPreferences)localObject).edit();
      ((SharedPreferences.Editor)localObject).putInt("selected_tab", i);
      ((SharedPreferences.Editor)localObject).putInt("last_tab", 8);
      ((SharedPreferences.Editor)localObject).commit();
    }
    if (this.dialogsAdapter != null) {
      this.dialogsAdapter.notifyDataSetChanged();
    }
    if ((this.checkPermission) && (Build.VERSION.SDK_INT >= 23))
    {
      localObject = getParentActivity();
      if (localObject != null)
      {
        this.checkPermission = false;
        if ((((Activity)localObject).checkSelfPermission("android.permission.READ_CONTACTS") != 0) || (((Activity)localObject).checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0))
        {
          if (!((Activity)localObject).shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
            break label209;
          }
          localObject = new AlertDialog.Builder((Context)localObject);
          ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165338));
          ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("PermissionContacts", 2131166159));
          ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166111), null);
          localObject = ((AlertDialog.Builder)localObject).create();
          this.permissionDialog = ((AlertDialog)localObject);
          showDialog((Dialog)localObject);
        }
      }
    }
    for (;;)
    {
      updateColors();
      return;
      label209:
      if (((Activity)localObject).shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE"))
      {
        localObject = new AlertDialog.Builder((Context)localObject);
        ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165338));
        ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("PermissionStorage", 2131166164));
        ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166111), null);
        localObject = ((AlertDialog.Builder)localObject).create();
        this.permissionDialog = ((AlertDialog)localObject);
        showDialog((Dialog)localObject);
      }
      else
      {
        askForPermissons();
      }
    }
  }
  
  class SlidingTabView
    extends LinearLayout
  {
    private float animateTabXTo = 0.0F;
    private DecelerateInterpolator interpolator;
    private Paint paint = new Paint();
    HashMap<Integer, Integer> positionOfTab = new HashMap();
    private int selectedTab = 0;
    private long startAnimationTime = 0L;
    private float startAnimationX = 0.0F;
    private int tabCount = 0;
    HashMap<Integer, Integer> tabInPosition = new HashMap();
    private float tabWidth = 0.0F;
    private float tabX = 0.0F;
    HashMap<Integer, ImageView> tabs = new HashMap();
    private long totalAnimationDiff = 0L;
    
    public SlidingTabView(Context paramContext, int paramInt)
    {
      super();
      setOrientation(0);
      paramContext = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
      BatchDialogsActivity.access$1102(BatchDialogsActivity.this, MihanTheme.getSelectedTabIconColor(paramContext));
      BatchDialogsActivity.access$1202(BatchDialogsActivity.this, MihanTheme.getTabsIconColor(paramContext));
      setWeightSum(ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getInt("tab_count", 8));
      this.paint.setColor(BatchDialogsActivity.this.sIconColor);
      setWillNotDraw(false);
      this.interpolator = new DecelerateInterpolator();
      this.selectedTab = paramInt;
    }
    
    private void animateToTab(int paramInt)
    {
      this.animateTabXTo = (paramInt * this.tabWidth);
      this.startAnimationX = this.tabX;
      this.totalAnimationDiff = 0L;
      this.startAnimationTime = System.currentTimeMillis();
      invalidate();
    }
    
    private void didSelectTab(int paramInt)
    {
      int i = 0;
      if (i < BatchDialogsActivity.this.listView.getAdapter().getItemCount())
      {
        localObject = BatchDialogsActivity.this.listView.getChildAt(i);
        if ((localObject instanceof BatchDialogCell))
        {
          localObject = (BatchDialogCell)localObject;
          if (!BatchDialogsActivity.this.selectedDialogIds.contains(Long.valueOf(((BatchDialogCell)localObject).getDialogId()))) {
            break label81;
          }
          ((BatchDialogCell)localObject).setBackgroundColor(BatchDialogsActivity.this.lightColor);
        }
        for (;;)
        {
          i += 1;
          break;
          label81:
          ((BatchDialogCell)localObject).setBackgroundColor(-1);
        }
      }
      i = ((Integer)this.tabInPosition.get(Integer.valueOf(paramInt))).intValue();
      if (this.selectedTab == i) {
        return;
      }
      this.selectedTab = i;
      setTabsLayout(i);
      Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).edit();
      ((SharedPreferences.Editor)localObject).putInt("selected_tab", this.selectedTab);
      ((SharedPreferences.Editor)localObject).commit();
      animateToTab(paramInt);
    }
    
    public void addImageTab(int paramInt)
    {
      final int i = this.tabCount;
      ImageView localImageView = new ImageView(getContext());
      localImageView.setFocusable(true);
      localImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      switch (paramInt)
      {
      default: 
        if (paramInt == this.selectedTab) {
          MihanTheme.setColorFilter(localImageView.getDrawable(), BatchDialogsActivity.this.sIconColor);
        }
        break;
      }
      for (;;)
      {
        this.tabs.put(Integer.valueOf(paramInt), localImageView);
        this.tabInPosition.put(Integer.valueOf(i), Integer.valueOf(paramInt));
        this.positionOfTab.put(Integer.valueOf(paramInt), Integer.valueOf(i));
        localImageView.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            BatchDialogsActivity.SlidingTabView.this.didSelectTab(i);
            if (BatchDialogsActivity.this.dialogsAdapter != null) {
              BatchDialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
            }
          }
        });
        addView(localImageView);
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)localImageView.getLayoutParams();
        localLayoutParams.height = -1;
        localLayoutParams.width = 0;
        localLayoutParams.weight = 1.0F;
        localImageView.setLayoutParams(localLayoutParams);
        this.tabCount += 1;
        return;
        localImageView.setImageResource(2130838152);
        break;
        localImageView.setImageResource(2130838154);
        break;
        localImageView.setImageResource(2130838160);
        break;
        localImageView.setImageResource(2130838158);
        break;
        localImageView.setImageResource(2130838164);
        break;
        localImageView.setImageResource(2130838156);
        break;
        localImageView.setImageResource(2130838147);
        break;
        localImageView.setImageResource(2130838163);
        break;
        MihanTheme.setColorFilter(localImageView.getDrawable(), BatchDialogsActivity.this.iconColor);
      }
    }
    
    public int getSeletedTab()
    {
      return ((Integer)this.positionOfTab.get(Integer.valueOf(this.selectedTab))).intValue();
    }
    
    public int getTabCount()
    {
      return this.tabCount;
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      if (this.tabX != this.animateTabXTo)
      {
        long l1 = System.currentTimeMillis();
        long l2 = this.startAnimationTime;
        this.startAnimationTime = System.currentTimeMillis();
        this.totalAnimationDiff += l1 - l2;
        if (this.totalAnimationDiff <= 1L) {
          break label113;
        }
        this.totalAnimationDiff = 1L;
        this.tabX = this.animateTabXTo;
      }
      for (;;)
      {
        float f1 = this.tabX;
        float f2 = getHeight() - AndroidUtilities.dp(4.0F);
        float f3 = this.tabX;
        paramCanvas.drawRect(f1, f2, this.tabWidth + f3, getHeight(), this.paint);
        return;
        label113:
        this.tabX = (this.startAnimationX + this.interpolator.getInterpolation((float)this.totalAnimationDiff / 1.0F) * (this.animateTabXTo - this.startAnimationX));
        invalidate();
      }
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      this.tabWidth = ((paramInt3 - paramInt1) / this.tabCount);
      float f = this.tabWidth;
      f = ((Integer)this.positionOfTab.get(Integer.valueOf(this.selectedTab))).intValue() * f;
      this.tabX = f;
      this.animateTabXTo = f;
    }
    
    public void setTabsLayout(int paramInt)
    {
      Iterator localIterator = this.tabs.keySet().iterator();
      while (localIterator.hasNext())
      {
        int i = ((Integer)localIterator.next()).intValue();
        MihanTheme.setColorFilter(((ImageView)this.tabs.get(Integer.valueOf(i))).getDrawable(), BatchDialogsActivity.this.iconColor);
      }
      MihanTheme.setColorFilter(((ImageView)this.tabs.get(Integer.valueOf(paramInt))).getDrawable(), BatchDialogsActivity.this.sIconColor);
      switch (paramInt)
      {
      default: 
        return;
      case 0: 
        BatchDialogsActivity.this.actionBar.setTitle(LocaleController.getString("RobotTab", 2131166248));
        return;
      case 1: 
        BatchDialogsActivity.this.actionBar.setTitle(LocaleController.getString("ChannelTab", 2131165518));
        return;
      case 2: 
        BatchDialogsActivity.this.actionBar.setTitle(LocaleController.getString("SuperGroupsTab", 2131166675));
        return;
      case 3: 
        BatchDialogsActivity.this.actionBar.setTitle(LocaleController.getString("GroupsTab", 2131165791));
        return;
      case 4: 
        BatchDialogsActivity.this.actionBar.setTitle(LocaleController.getString("ContactTab", 2131165562));
        return;
      case 5: 
        BatchDialogsActivity.this.actionBar.setTitle(LocaleController.getString("FavoriteTab", 2131165684));
        return;
      case 6: 
        BatchDialogsActivity.this.actionBar.setTitle(LocaleController.getString("AppName", 2131165338));
        return;
      }
      BatchDialogsActivity.this.actionBar.setTitle(LocaleController.getString("UnreadTab", 2131166427));
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\BatchWorks\BatchDialogsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */