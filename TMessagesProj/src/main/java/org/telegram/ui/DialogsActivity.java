package org.telegram.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputChannel;
import org.telegram.tgnet.TLRPC.TL_channelForbidden;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PlayerView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Supergram.BatchWorks.BatchDialogsActivity;
import org.telegram.ui.Supergram.DialogsLoader;
import org.telegram.ui.Supergram.IdFinderActivity;
import org.telegram.ui.Supergram.MihanSettingsActivity;
import org.telegram.ui.Supergram.OnlineContactsActivity;
import org.telegram.ui.Supergram.SetPasswordActivity;
import org.telegram.ui.Supergram.Theming.MihanTheme;
import org.telegram.ui.Supergram.UserChanges.UpdateActivity;

public class DialogsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static boolean dialogsLoaded;
  private String addToGroupAlertString;
  boolean allHasU;
  private ImageView button1;
  private ImageView button2;
  private ImageView button3;
  private ImageView button4;
  private ImageView button5;
  private ImageView button6;
  private ImageView button7;
  boolean cHasU;
  boolean chHasU;
  private boolean checkPermission = true;
  private TextView countAll;
  private LinearLayout countBar;
  private TextView countCh;
  private TextView countCon;
  private TextView countG;
  private TextView countSG;
  private TextView countUnread;
  private TextView countfav;
  private DialogsActivityDelegate delegate;
  private DialogsAdapter dialogsAdapter;
  private DialogsSearchAdapter dialogsSearchAdapter;
  private int dialogsType;
  private float downX;
  private float downY;
  private LinearLayout emptyView;
  boolean favHasU;
  private ImageView floatingButton;
  private ImageView floatingButton1;
  private boolean floatingHidden;
  private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
  boolean gHasU;
  private int iconColor;
  private LinearLayoutManager layoutManager;
  private RecyclerListView listView;
  ActionBarMenu menu;
  private boolean onlySelect;
  private long openedDialogId;
  private ActionBarMenuItem passcodeItem;
  private AlertDialog permissionDialog;
  private int prevPosition;
  private int prevTop;
  private ProgressBar progressView;
  private int sIconColor;
  private boolean scrollUpdated;
  private EmptyTextProgressView searchEmptyView;
  private String searchString;
  private boolean searchWas;
  private boolean searching;
  private String selectAlertString;
  private String selectAlertStringGroup;
  private long selectedDialog;
  boolean sgHasU;
  private SlidingTabView slidingTabView;
  private boolean swipe;
  private int tabsHeight;
  private boolean tabsHidden;
  private LinearLayout toolBar;
  private float upX;
  private float upY;
  
  public DialogsActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }
  
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
    int j = localSharedPreferences.getInt("default_tab", 6);
    int i;
    SharedPreferences.Editor localEditor;
    if (this.onlySelect)
    {
      i = localSharedPreferences.getInt("selected_tab", 6);
      localEditor = localSharedPreferences.edit();
      localEditor.putInt("last_tab", i);
      i = 6;
      localEditor.putInt("selected_tab", 6);
      localEditor.commit();
    }
    for (;;)
    {
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
      this.countBar = new LinearLayout(paramContext);
      this.countBar.setGravity(80);
      this.countBar.setWeightSum(localSharedPreferences.getInt("tab_count", 8));
      this.countBar.setPadding(0, 0, 0, 10);
      paramFrameLayout.addView(this.countBar, LayoutHelper.createFrame(-1, this.tabsHeight, 48));
      if (localSharedPreferences.getBoolean("tabs_counter", true))
      {
        if (localSharedPreferences.getBoolean("tab_bot", true))
        {
          paramFrameLayout = new TextView(paramContext);
          this.countBar.addView(paramFrameLayout, LayoutHelper.createLinear(0, -2, 1.0F));
        }
        if (localSharedPreferences.getBoolean("tab_channel", true))
        {
          this.countCh = new TextView(paramContext);
          newCounterItem(paramContext, this.countCh);
        }
        if (localSharedPreferences.getBoolean("tab_supergroup", true))
        {
          this.countSG = new TextView(paramContext);
          newCounterItem(paramContext, this.countSG);
        }
        if (localSharedPreferences.getBoolean("tab_group", true))
        {
          this.countG = new TextView(paramContext);
          newCounterItem(paramContext, this.countG);
        }
        if (localSharedPreferences.getBoolean("tab_contact", true))
        {
          this.countCon = new TextView(paramContext);
          newCounterItem(paramContext, this.countCon);
        }
        if (localSharedPreferences.getBoolean("tab_favorite", true))
        {
          this.countfav = new TextView(paramContext);
          newCounterItem(paramContext, this.countfav);
        }
        if (localSharedPreferences.getBoolean("tab_all", true))
        {
          this.countAll = new TextView(paramContext);
          newCounterItem(paramContext, this.countAll);
        }
        if (localSharedPreferences.getBoolean("tab_unread", true))
        {
          this.countUnread = new TextView(paramContext);
          newCounterItem(paramContext, this.countUnread);
        }
      }
      return;
      i = j;
      if (localSharedPreferences.getInt("last_tab", 8) == 8)
      {
        localEditor = localSharedPreferences.edit();
        localEditor.putInt("selected_tab", j);
        localEditor.commit();
        i = j;
      }
    }
  }
  
  private void createToolBar(Context paramContext, FrameLayout paramFrameLayout)
  {
    this.toolBar = new LinearLayout(paramContext);
    this.toolBar.setGravity(17);
    this.toolBar.setPadding(0, AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F));
    this.toolBar.setWeightSum(7.0F);
    paramFrameLayout.addView(this.toolBar, LayoutHelper.createFrame(-1, 56, 80));
    this.button1 = new ImageView(getParentActivity());
    this.button1.setFocusable(true);
    this.button1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    this.button1.setImageResource(2130838171);
    this.toolBar.addView(this.button1, LayoutHelper.createLinear(0, 35, 1.0F));
    this.button1.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramAnonymousView = new Bundle();
        paramAnonymousView.putBoolean("destroyAfterSelect", true);
        DialogsActivity.this.presentFragment(new ContactsActivity(paramAnonymousView));
      }
    });
    this.button2 = new ImageView(getParentActivity());
    this.button2.setFocusable(true);
    this.button2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    this.button2.setImageResource(2130838172);
    this.toolBar.addView(this.button2, LayoutHelper.createLinear(0, -1, 1.0F));
    this.button2.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        DialogsActivity.this.presentFragment(new MihanSettingsActivity());
      }
    });
    this.button3 = new ImageView(getParentActivity());
    this.button3.setFocusable(true);
    this.button3.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    this.button3.setImageResource(2130838167);
    this.toolBar.addView(this.button3, LayoutHelper.createLinear(0, -1, 1.0F));
    this.button3.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        DialogsActivity.this.presentFragment(new IdFinderActivity());
      }
    });
    this.button4 = new ImageView(getParentActivity());
    this.button4.setFocusable(true);
    this.button4.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    this.button4.setImageResource(2130838173);
    this.toolBar.addView(this.button4, LayoutHelper.createLinear(0, -1, 1.0F));
    this.button4.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        DialogsActivity.this.presentFragment(new UpdateActivity(null));
      }
    });
    this.button5 = new ImageView(getParentActivity());
    this.button5.setFocusable(true);
    this.button5.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    this.button5.setImageResource(2130838170);
    this.toolBar.addView(this.button5, LayoutHelper.createLinear(0, -1, 1.0F));
    this.button5.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        DialogsActivity.this.presentFragment(new OnlineContactsActivity(null));
      }
    });
    this.button6 = new ImageView(getParentActivity());
    this.button6.setFocusable(true);
    this.button6.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    if (ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getBoolean("chat_unlocked", false)) {
      this.button6.setImageResource(2130838169);
    }
    for (;;)
    {
      this.toolBar.addView(this.button6, LayoutHelper.createLinear(0, -1, 1.0F));
      this.button6.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getString("chat_password", "").length() == 0)
          {
            DialogsActivity.this.presentFragment(new SetPasswordActivity(0));
            return;
          }
          DialogsActivity.this.presentFragment(new SetPasswordActivity(4), true);
        }
      });
      this.button7 = new ImageView(getParentActivity());
      this.button7.setFocusable(true);
      this.button7.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      this.button7.setImageResource(2130838166);
      this.toolBar.addView(this.button7, LayoutHelper.createLinear(0, -1, 1.0F));
      this.button7.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
          String str1 = LocaleController.getString("MultiMarkAsRead", 2131166653);
          String str2 = LocaleController.getString("MultiMuteChats", 2131166654);
          String str3 = LocaleController.getString("MultiUnMuteChats", 2131166655);
          String str4 = LocaleController.getString("MultiAddToFavorites", 2131166650);
          String str5 = LocaleController.getString("MultiClearHistory", 2131166651);
          String str6 = LocaleController.getString("MultiDeleteAndLeave", 2131166652);
          DialogInterface.OnClickListener local1 = new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).edit();
              if (paramAnonymous2Int == 0)
              {
                paramAnonymous2DialogInterface.putInt("op_type", 1);
                paramAnonymous2DialogInterface.commit();
                DialogsActivity.this.presentFragment(new BatchDialogsActivity());
              }
              do
              {
                return;
                if (paramAnonymous2Int == 1)
                {
                  paramAnonymous2DialogInterface.putInt("op_type", 2);
                  paramAnonymous2DialogInterface.commit();
                  DialogsActivity.this.presentFragment(new BatchDialogsActivity());
                  return;
                }
                if (paramAnonymous2Int == 2)
                {
                  paramAnonymous2DialogInterface.putInt("op_type", 6);
                  paramAnonymous2DialogInterface.commit();
                  DialogsActivity.this.presentFragment(new BatchDialogsActivity());
                  return;
                }
                if (paramAnonymous2Int == 3)
                {
                  paramAnonymous2DialogInterface.putInt("op_type", 3);
                  paramAnonymous2DialogInterface.commit();
                  DialogsActivity.this.presentFragment(new BatchDialogsActivity());
                  return;
                }
                if (paramAnonymous2Int == 4)
                {
                  paramAnonymous2DialogInterface.putInt("op_type", 4);
                  paramAnonymous2DialogInterface.commit();
                  DialogsActivity.this.presentFragment(new BatchDialogsActivity());
                  return;
                }
              } while (paramAnonymous2Int != 5);
              paramAnonymous2DialogInterface.putInt("op_type", 5);
              paramAnonymous2DialogInterface.commit();
              DialogsActivity.this.presentFragment(new BatchDialogsActivity());
            }
          };
          paramAnonymousView.setItems(new CharSequence[] { str1, str2, str3, str4, str5, str6 }, local1);
          DialogsActivity.this.showDialog(paramAnonymousView.create());
        }
      });
      return;
      this.button6.setImageResource(2130838168);
    }
  }
  
  private void didSelectResult(final long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    AlertDialog.Builder localBuilder;
    if ((this.addToGroupAlertString == null) && ((int)paramLong < 0) && (ChatObject.isChannel(-(int)paramLong)) && (!ChatObject.isCanWriteToChannel(-(int)paramLong)))
    {
      localBuilder = new AlertDialog.Builder(getParentActivity());
      localBuilder.setTitle(LocaleController.getString("AppName", 2131165338));
      localBuilder.setMessage(LocaleController.getString("ChannelCantSendMessage", 2131165457));
      localBuilder.setNegativeButton(LocaleController.getString("OK", 2131166111), null);
      showDialog(localBuilder.create());
    }
    int i;
    int j;
    Object localObject;
    do
    {
      do
      {
        return;
        if ((!paramBoolean1) || (((this.selectAlertString == null) || (this.selectAlertStringGroup == null)) && (this.addToGroupAlertString == null))) {
          break;
        }
      } while (getParentActivity() == null);
      localBuilder = new AlertDialog.Builder(getParentActivity());
      localBuilder.setTitle(LocaleController.getString("AppName", 2131165338));
      i = (int)paramLong;
      j = (int)(paramLong >> 32);
      if (i == 0) {
        break label414;
      }
      if (j != 1) {
        break;
      }
      localObject = MessagesController.getInstance().getChat(Integer.valueOf(i));
    } while (localObject == null);
    localBuilder.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, new Object[] { ((TLRPC.Chat)localObject).title }));
    for (;;)
    {
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          DialogsActivity.this.didSelectResult(paramLong, false, false);
        }
      });
      localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
      showDialog(localBuilder.create());
      return;
      if (i > 0)
      {
        localObject = MessagesController.getInstance().getUser(Integer.valueOf(i));
        if (localObject == null) {
          break;
        }
        localBuilder.setMessage(LocaleController.formatStringSimple(this.selectAlertString, new Object[] { UserObject.getUserName((TLRPC.User)localObject) }));
        continue;
      }
      if (i < 0)
      {
        localObject = MessagesController.getInstance().getChat(Integer.valueOf(-i));
        if (localObject == null) {
          break;
        }
        if (this.addToGroupAlertString != null)
        {
          localBuilder.setMessage(LocaleController.formatStringSimple(this.addToGroupAlertString, new Object[] { ((TLRPC.Chat)localObject).title }));
        }
        else
        {
          localBuilder.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, new Object[] { ((TLRPC.Chat)localObject).title }));
          continue;
          label414:
          localObject = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(j));
          localObject = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.EncryptedChat)localObject).user_id));
          if (localObject == null) {
            break;
          }
          localBuilder.setMessage(LocaleController.formatStringSimple(this.selectAlertString, new Object[] { UserObject.getUserName((TLRPC.User)localObject) }));
        }
      }
    }
    if (this.delegate != null)
    {
      this.delegate.didSelectDialog(this, paramLong, paramBoolean2);
      this.delegate = null;
      return;
    }
    finishFragment();
  }
  
  private ArrayList<TLRPC.TL_dialog> getDialogsArray()
  {
    if (this.dialogsType == 0) {
      return new DialogsLoader().getDialogsArray();
    }
    if (this.dialogsType == 1) {
      return MessagesController.getInstance().dialogsServerOnly;
    }
    if (this.dialogsType == 2) {
      return MessagesController.getInstance().dialogsGroupsOnly;
    }
    return null;
  }
  
  private void hideFloatingButton(boolean paramBoolean)
  {
    if (this.floatingHidden == paramBoolean) {
      return;
    }
    this.floatingHidden = paramBoolean;
    Object localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    boolean bool1 = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getBoolean("tool_bar", true);
    boolean bool2 = ((SharedPreferences)localObject1).getBoolean("move_tabs", false);
    int i = 100;
    if (bool2) {
      i = 125;
    }
    float f;
    Object localObject2;
    if (!bool1)
    {
      localObject1 = this.floatingButton;
      label139:
      ImageView localImageView;
      if (this.floatingHidden)
      {
        f = AndroidUtilities.dp(i);
        localObject1 = ObjectAnimator.ofFloat(localObject1, "translationY", new float[] { f }).setDuration(300L);
        localObject2 = this.floatingButton1;
        if (!this.floatingHidden) {
          break label234;
        }
        f = AndroidUtilities.dp(i);
        localObject2 = ObjectAnimator.ofFloat(localObject2, "translationY", new float[] { f }).setDuration(300L);
        ((ObjectAnimator)localObject1).setInterpolator(this.floatingInterpolator);
        ((ObjectAnimator)localObject2).setInterpolator(this.floatingInterpolator);
        localImageView = this.floatingButton;
        if (paramBoolean) {
          break label239;
        }
        bool1 = true;
        label193:
        localImageView.setClickable(bool1);
        localImageView = this.floatingButton1;
        if (paramBoolean) {
          break label245;
        }
      }
      label234:
      label239:
      label245:
      for (paramBoolean = true;; paramBoolean = false)
      {
        localImageView.setClickable(paramBoolean);
        ((ObjectAnimator)localObject1).start();
        ((ObjectAnimator)localObject2).start();
        return;
        f = 0.0F;
        break;
        f = 0.0F;
        break label139;
        bool1 = false;
        break label193;
      }
    }
    localObject1 = this.toolBar;
    if (this.floatingHidden)
    {
      f = AndroidUtilities.dp(i);
      localObject1 = ObjectAnimator.ofFloat(localObject1, "translationY", new float[] { f }).setDuration(300L);
      ((ObjectAnimator)localObject1).setInterpolator(this.floatingInterpolator);
      localObject2 = this.toolBar;
      if (paramBoolean) {
        break label331;
      }
    }
    label331:
    for (paramBoolean = true;; paramBoolean = false)
    {
      ((LinearLayout)localObject2).setClickable(paramBoolean);
      ((ObjectAnimator)localObject1).start();
      return;
      f = 0.0F;
      break;
    }
  }
  
  private void init()
  {
    TLRPC.TL_contacts_resolveUsername localTL_contacts_resolveUsername = new TLRPC.TL_contacts_resolveUsername();
    localTL_contacts_resolveUsername.username = "Supergram";
    ConnectionsManager.getInstance().sendRequest(localTL_contacts_resolveUsername, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (paramAnonymousTL_error == null)
            {
              Object localObject = (TLRPC.TL_contacts_resolvedPeer)paramAnonymousTLObject;
              MessagesController.getInstance();
              MessagesController.getInstance().putChats(((TLRPC.TL_contacts_resolvedPeer)localObject).chats, false);
              MessagesStorage.getInstance().putUsersAndChats(((TLRPC.TL_contacts_resolvedPeer)localObject).users, ((TLRPC.TL_contacts_resolvedPeer)localObject).chats, false, true);
              if (!((TLRPC.TL_contacts_resolvedPeer)localObject).chats.isEmpty())
              {
                localObject = (TLRPC.Chat)((TLRPC.TL_contacts_resolvedPeer)localObject).chats.get(0);
                if ((ChatObject.isChannel((TLRPC.Chat)localObject)) && (!(localObject instanceof TLRPC.TL_channelForbidden)) && (ChatObject.isNotInChat((TLRPC.Chat)localObject))) {
                  MessagesController.getInstance().addUserToChat(((TLRPC.Chat)localObject).id, UserConfig.getCurrentUser(), null, 0, null, null);
                }
              }
            }
          }
        });
      }
    });
  }
  
  private void newCounterItem(Context paramContext, TextView paramTextView)
  {
    paramContext = new LinearLayout(paramContext);
    paramContext.setGravity(81);
    this.countBar.addView(paramContext, LayoutHelper.createLinear(0, 14, 1.0F, 0, 0, 0, 2));
    paramTextView.setGravity(1);
    paramTextView.setTextSize(11.0F);
    paramTextView.setTypeface(MihanTheme.getMihanTypeFace());
    paramTextView.setBackgroundResource(2130837734);
    paramTextView.setMinWidth(AndroidUtilities.dp(13.0F));
    paramTextView.setPadding(5, -2, 5, 0);
    paramContext.addView(paramTextView, LayoutHelper.createLinear(-2, -2, 1, 10, 0, 0, 0));
  }
  
  private void onSwipe(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getAction())
    {
    }
    label233:
    label242:
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
              break label233;
            }
            this.slidingTabView.didSelectTab(i);
          }
          for (;;)
          {
            if (this.dialogsAdapter == null) {
              break label242;
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
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    int i = MihanTheme.getActionBarColor(localSharedPreferences);
    int j = MihanTheme.getActionBarGradientFlag(localSharedPreferences);
    int k = MihanTheme.getActionBarGradientColor(localSharedPreferences);
    GradientDrawable localGradientDrawable;
    if (j != 0)
    {
      localGradientDrawable = MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j));
      this.actionBar.setBackgroundDrawable(localGradientDrawable);
    }
    for (;;)
    {
      MihanTheme.setColorFilter(ApplicationLoader.applicationContext.getResources().getDrawable(2130837829), MihanTheme.getActionBarIconColor(localSharedPreferences));
      this.actionBar.setTitleColor(MihanTheme.getActionBarTitleColor(localSharedPreferences));
      i = 0;
      while (i < 5)
      {
        if (this.menu.getItem(i) != null) {
          MihanTheme.setColorFilter(this.menu.getItem(i).getImageView().getDrawable(), MihanTheme.getActionBarIconColor(localSharedPreferences));
        }
        i += 1;
      }
      this.actionBar.setBackgroundColor(i);
    }
    i = MihanTheme.getTabsBackgroundColor(localSharedPreferences);
    j = MihanTheme.getTabsGradientFlag(localSharedPreferences);
    k = MihanTheme.getTabsGradientColor(localSharedPreferences);
    if (j != 0)
    {
      localGradientDrawable = MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j));
      this.slidingTabView.setBackgroundDrawable(localGradientDrawable);
      this.sIconColor = MihanTheme.getSelectedTabIconColor(localSharedPreferences);
      this.iconColor = MihanTheme.getTabsIconColor(localSharedPreferences);
      i = this.slidingTabView.getSeletedTab();
      this.slidingTabView.didSelectTab(i);
      updateTabCounterColor();
      i = localSharedPreferences.getInt("theme_list_color", -1);
      j = localSharedPreferences.getInt("theme_list_gradient", 0);
      k = localSharedPreferences.getInt("theme_list_gradient_color", i);
      if (j == 0) {
        break label470;
      }
      localGradientDrawable = MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j));
      this.listView.setBackgroundDrawable(localGradientDrawable);
      this.emptyView.setBackgroundDrawable(localGradientDrawable);
      label305:
      if (!((SharedPreferences)localObject).getBoolean("tool_bar", true)) {
        break label526;
      }
      i = MihanTheme.getToolBarBGColor(localSharedPreferences);
      j = MihanTheme.getToolBarGradientFlag(localSharedPreferences);
      k = MihanTheme.getToolBarGradientColor(localSharedPreferences);
      if (j == 0) {
        break label489;
      }
      localObject = MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j), 8.0F);
      ((GradientDrawable)localObject).setStroke(2, MihanTheme.getDialogDividerColor(localSharedPreferences));
      this.toolBar.setBackgroundDrawable((Drawable)localObject);
    }
    for (;;)
    {
      i = MihanTheme.getToolBarIconColor(localSharedPreferences);
      MihanTheme.setColorFilter(this.button1.getDrawable(), i);
      MihanTheme.setColorFilter(this.button2.getDrawable(), i);
      MihanTheme.setColorFilter(this.button3.getDrawable(), i);
      MihanTheme.setColorFilter(this.button4.getDrawable(), i);
      MihanTheme.setColorFilter(this.button5.getDrawable(), i);
      MihanTheme.setColorFilter(this.button6.getDrawable(), i);
      MihanTheme.setColorFilter(this.button7.getDrawable(), i);
      return;
      this.slidingTabView.setBackgroundColor(i);
      break;
      label470:
      this.listView.setBackgroundColor(i);
      this.emptyView.setBackgroundColor(i);
      break label305;
      label489:
      localObject = MihanTheme.setGradiant(i, i, MihanTheme.getGradientOrientation(j), 8.0F);
      ((GradientDrawable)localObject).setStroke(2, MihanTheme.getDialogDividerColor(localSharedPreferences));
      this.toolBar.setBackgroundDrawable((Drawable)localObject);
    }
    label526:
    i = localSharedPreferences.getInt("theme_float_color", MihanTheme.getThemeColor());
    localObject = getParentActivity().getResources().getDrawable(2130837796);
    ((Drawable)localObject).setColorFilter(i, PorterDuff.Mode.MULTIPLY);
    if (localObject != null)
    {
      this.floatingButton.setBackgroundDrawable((Drawable)localObject);
      this.floatingButton1.setBackgroundDrawable((Drawable)localObject);
    }
    i = localSharedPreferences.getInt("theme_float_icon_color", -1);
    MihanTheme.setColorFilter(this.floatingButton.getDrawable(), i);
    MihanTheme.setColorFilter(this.floatingButton1.getDrawable(), i);
  }
  
  private void updateLayout()
  {
    Object localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
    Object localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    boolean bool1 = ((SharedPreferences)localObject1).getBoolean("tabs", true);
    boolean bool2 = ((SharedPreferences)localObject2).getBoolean("move_tabs", false);
    boolean bool3 = ((SharedPreferences)localObject1).getBoolean("tool_bar", true);
    FrameLayout.LayoutParams localLayoutParams2 = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
    FrameLayout.LayoutParams localLayoutParams3 = (FrameLayout.LayoutParams)this.slidingTabView.getLayoutParams();
    FrameLayout.LayoutParams localLayoutParams4 = (FrameLayout.LayoutParams)this.countBar.getLayoutParams();
    FrameLayout.LayoutParams localLayoutParams5 = (FrameLayout.LayoutParams)this.emptyView.getLayoutParams();
    localObject1 = null;
    localObject2 = null;
    FrameLayout.LayoutParams localLayoutParams1 = null;
    int i;
    label251:
    int k;
    if (!bool3)
    {
      localObject1 = (FrameLayout.LayoutParams)this.floatingButton.getLayoutParams();
      localObject2 = (FrameLayout.LayoutParams)this.floatingButton1.getLayoutParams();
      if (bool1) {
        break label431;
      }
      if (this.slidingTabView.getVisibility() == 0) {
        this.slidingTabView.setVisibility(8);
      }
      localLayoutParams2.setMargins(AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F));
      this.listView.setLayoutParams(localLayoutParams2);
      localLayoutParams5.setMargins(AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F));
      this.emptyView.setLayoutParams(localLayoutParams5);
      if (bool3) {
        break label394;
      }
      if (!LocaleController.isRTL) {
        break label358;
      }
      i = AndroidUtilities.dp(14.0F);
      k = AndroidUtilities.dp(0.0F);
      if (!LocaleController.isRTL) {
        break label366;
      }
      j = AndroidUtilities.dp(0.0F);
      label267:
      ((FrameLayout.LayoutParams)localObject1).setMargins(i, k, j, AndroidUtilities.dp(14.0F));
      this.floatingButton.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      if (!LocaleController.isRTL) {
        break label376;
      }
      i = AndroidUtilities.dp(82.0F);
      label303:
      k = AndroidUtilities.dp(0.0F);
      if (!LocaleController.isRTL) {
        break label384;
      }
    }
    label358:
    label366:
    label376:
    label384:
    for (int j = AndroidUtilities.dp(0.0F);; j = AndroidUtilities.dp(82.0F))
    {
      ((FrameLayout.LayoutParams)localObject2).setMargins(i, k, j, AndroidUtilities.dp(14.0F));
      this.floatingButton1.setLayoutParams((ViewGroup.LayoutParams)localObject2);
      return;
      localLayoutParams1 = (FrameLayout.LayoutParams)this.toolBar.getLayoutParams();
      break;
      i = AndroidUtilities.dp(0.0F);
      break label251;
      j = AndroidUtilities.dp(14.0F);
      break label267;
      i = AndroidUtilities.dp(0.0F);
      break label303;
    }
    label394:
    localLayoutParams1.setMargins(AndroidUtilities.dp(9.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(9.0F), AndroidUtilities.dp(14.0F));
    this.toolBar.setLayoutParams(localLayoutParams1);
    return;
    label431:
    if (bool2)
    {
      if (this.slidingTabView.getVisibility() == 8) {
        this.slidingTabView.setVisibility(0);
      }
      localLayoutParams3.gravity = 80;
      this.slidingTabView.setLayoutParams(localLayoutParams3);
      localLayoutParams4.gravity = 80;
      this.countBar.setLayoutParams(localLayoutParams4);
      localLayoutParams3.setMargins(AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F));
      this.slidingTabView.setLayoutParams(localLayoutParams3);
      localLayoutParams4.setMargins(AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F));
      this.countBar.setLayoutParams(localLayoutParams4);
      localLayoutParams2.setMargins(AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(this.tabsHeight));
      this.listView.setLayoutParams(localLayoutParams2);
      localLayoutParams5.setMargins(AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(this.tabsHeight));
      this.emptyView.setLayoutParams(localLayoutParams5);
      if (!bool3)
      {
        if (LocaleController.isRTL)
        {
          i = AndroidUtilities.dp(14.0F);
          k = AndroidUtilities.dp(0.0F);
          if (!LocaleController.isRTL) {
            break label744;
          }
          j = AndroidUtilities.dp(0.0F);
          label650:
          ((FrameLayout.LayoutParams)localObject1).setMargins(i, k, j, AndroidUtilities.dp(this.tabsHeight + 14));
          this.floatingButton.setLayoutParams((ViewGroup.LayoutParams)localObject1);
          if (!LocaleController.isRTL) {
            break label754;
          }
          i = AndroidUtilities.dp(82.0F);
          label691:
          k = AndroidUtilities.dp(0.0F);
          if (!LocaleController.isRTL) {
            break label762;
          }
        }
        label744:
        label754:
        label762:
        for (j = AndroidUtilities.dp(0.0F);; j = AndroidUtilities.dp(82.0F))
        {
          ((FrameLayout.LayoutParams)localObject2).setMargins(i, k, j, AndroidUtilities.dp(this.tabsHeight + 14));
          this.floatingButton1.setLayoutParams((ViewGroup.LayoutParams)localObject2);
          return;
          i = AndroidUtilities.dp(0.0F);
          break;
          j = AndroidUtilities.dp(14.0F);
          break label650;
          i = AndroidUtilities.dp(0.0F);
          break label691;
        }
      }
      localLayoutParams1.setMargins(AndroidUtilities.dp(9.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(9.0F), AndroidUtilities.dp(this.tabsHeight + 14));
      this.toolBar.setLayoutParams(localLayoutParams1);
      return;
    }
    if (this.slidingTabView.getVisibility() == 8) {
      this.slidingTabView.setVisibility(0);
    }
    localLayoutParams3.gravity = 48;
    this.slidingTabView.setLayoutParams(localLayoutParams3);
    localLayoutParams4.gravity = 48;
    this.countBar.setLayoutParams(localLayoutParams4);
    localLayoutParams2.setMargins(AndroidUtilities.dp(0.0F), AndroidUtilities.dp(this.tabsHeight), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F));
    this.listView.setLayoutParams(localLayoutParams2);
    localLayoutParams5.setMargins(AndroidUtilities.dp(0.0F), AndroidUtilities.dp(this.tabsHeight), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(0.0F));
    this.emptyView.setLayoutParams(localLayoutParams5);
    if (!bool3)
    {
      if (LocaleController.isRTL)
      {
        i = AndroidUtilities.dp(14.0F);
        k = AndroidUtilities.dp(0.0F);
        if (!LocaleController.isRTL) {
          break label1052;
        }
        j = AndroidUtilities.dp(0.0F);
        label968:
        ((FrameLayout.LayoutParams)localObject1).setMargins(i, k, j, AndroidUtilities.dp(14.0F));
        this.floatingButton.setLayoutParams((ViewGroup.LayoutParams)localObject1);
        if (!LocaleController.isRTL) {
          break label1062;
        }
        i = AndroidUtilities.dp(82.0F);
        label1004:
        k = AndroidUtilities.dp(0.0F);
        if (!LocaleController.isRTL) {
          break label1070;
        }
      }
      label1052:
      label1062:
      label1070:
      for (j = AndroidUtilities.dp(0.0F);; j = AndroidUtilities.dp(82.0F))
      {
        ((FrameLayout.LayoutParams)localObject2).setMargins(i, k, j, AndroidUtilities.dp(14.0F));
        this.floatingButton1.setLayoutParams((ViewGroup.LayoutParams)localObject2);
        return;
        i = AndroidUtilities.dp(0.0F);
        break;
        j = AndroidUtilities.dp(14.0F);
        break label968;
        i = AndroidUtilities.dp(0.0F);
        break label1004;
      }
    }
    localLayoutParams1.setMargins(AndroidUtilities.dp(9.0F), AndroidUtilities.dp(0.0F), AndroidUtilities.dp(9.0F), AndroidUtilities.dp(14.0F));
    this.toolBar.setLayoutParams(localLayoutParams1);
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
  
  private void updatePrinting()
  {
    Object localObject1 = "";
    Iterator localIterator = MessagesController.getInstance().MihanPrintingStrings.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject2 = (Long)localIterator.next();
      localObject2 = (CharSequence)MessagesController.getInstance().MihanPrintingStrings.get(localObject2);
      localObject2 = (String)localObject1 + localObject2;
      localObject1 = localObject2;
      if (localIterator.hasNext()) {
        localObject1 = (String)localObject2 + "\n";
      }
    }
    if (!((String)localObject1).equals(""))
    {
      localObject1 = Toast.makeText(getParentActivity(), (CharSequence)localObject1, 0);
      ((Toast)localObject1).setGravity(49, 0, 0);
      ((TextView)((LinearLayout)((Toast)localObject1).getView()).getChildAt(0)).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      ((Toast)localObject1).show();
    }
  }
  
  private void updateTabCounterColor()
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    int i = MihanTheme.getTabsCounterBGColor(localSharedPreferences);
    int j = MihanTheme.getTabsCounterTextColor(localSharedPreferences);
    int k = MihanTheme.getTabsMCounterBGColor(localSharedPreferences);
    int m = MihanTheme.getTabsMCounterTextColor(localSharedPreferences);
    if (this.countCh != null)
    {
      if (this.chHasU)
      {
        this.countCh.setTextColor(j);
        ((GradientDrawable)this.countCh.getBackground()).setColor(i);
      }
    }
    else
    {
      if (this.countSG != null)
      {
        if (!this.sgHasU) {
          break label316;
        }
        this.countSG.setTextColor(j);
        ((GradientDrawable)this.countSG.getBackground()).setColor(i);
      }
      label109:
      if (this.countG != null)
      {
        if (!this.gHasU) {
          break label342;
        }
        this.countG.setTextColor(j);
        ((GradientDrawable)this.countG.getBackground()).setColor(i);
      }
      label145:
      if (this.countCon != null)
      {
        if (!this.cHasU) {
          break label368;
        }
        this.countCon.setTextColor(j);
        ((GradientDrawable)this.countCon.getBackground()).setColor(i);
      }
      label181:
      if (this.countfav != null)
      {
        if (!this.favHasU) {
          break label394;
        }
        this.countfav.setTextColor(j);
        ((GradientDrawable)this.countfav.getBackground()).setColor(i);
      }
      label217:
      if (this.countAll != null)
      {
        if (!this.allHasU) {
          break label420;
        }
        this.countAll.setTextColor(j);
        ((GradientDrawable)this.countAll.getBackground()).setColor(i);
      }
    }
    for (;;)
    {
      if (this.countUnread != null)
      {
        if (!this.allHasU) {
          break label446;
        }
        this.countUnread.setTextColor(j);
        ((GradientDrawable)this.countUnread.getBackground()).setColor(i);
      }
      return;
      this.countCh.setTextColor(m);
      ((GradientDrawable)this.countCh.getBackground()).setColor(k);
      break;
      label316:
      this.countSG.setTextColor(m);
      ((GradientDrawable)this.countSG.getBackground()).setColor(k);
      break label109;
      label342:
      this.countG.setTextColor(m);
      ((GradientDrawable)this.countG.getBackground()).setColor(k);
      break label145;
      label368:
      this.countCon.setTextColor(m);
      ((GradientDrawable)this.countCon.getBackground()).setColor(k);
      break label181;
      label394:
      this.countfav.setTextColor(m);
      ((GradientDrawable)this.countfav.getBackground()).setColor(k);
      break label217;
      label420:
      this.countAll.setTextColor(m);
      ((GradientDrawable)this.countAll.getBackground()).setColor(k);
    }
    label446:
    this.countUnread.setTextColor(m);
    ((GradientDrawable)this.countUnread.getBackground()).setColor(k);
  }
  
  private void updateUnreadCount(ArrayList<TLRPC.TL_dialog> paramArrayList)
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
    boolean bool1 = localSharedPreferences.getBoolean("chat_unlocked", false);
    boolean bool2 = localSharedPreferences.getBoolean("tabs", true);
    boolean bool3 = localSharedPreferences.getBoolean("tabs_only_not_muted", false);
    this.chHasU = false;
    this.sgHasU = false;
    this.gHasU = false;
    this.cHasU = false;
    this.favHasU = false;
    this.allHasU = false;
    int i26 = 0;
    int i5 = 0;
    int i18 = 0;
    int i31 = 0;
    int i10 = 0;
    int i16 = 0;
    int i30 = 0;
    int i7 = 0;
    int i = 0;
    int i24 = 0;
    int i1 = 0;
    int i8 = 0;
    int i28 = 0;
    int i3 = 0;
    int i12 = 0;
    int i22 = 0;
    int m = 0;
    int i14 = 0;
    int i25 = 0;
    int i4 = 0;
    int i19 = 0;
    int i20 = 0;
    int i9 = 0;
    int i17 = 0;
    int i29 = 0;
    int i6 = 0;
    int k = 0;
    int i23 = 0;
    int n = 0;
    int i11 = 0;
    int i27 = 0;
    int i2 = 0;
    int i13 = 0;
    int i21 = 0;
    int j = 0;
    int i15 = 0;
    if (bool2)
    {
      TLRPC.TL_dialog localTL_dialog;
      label470:
      label560:
      label590:
      Object localObject;
      if (bool1)
      {
        i20 = 0;
        j = i15;
        m = i14;
        n = i11;
        i1 = i8;
        i4 = i19;
        i5 = i18;
        i2 = i13;
        i3 = i12;
        i6 = k;
        i7 = i;
        i9 = i17;
        i10 = i16;
        if (i20 < paramArrayList.size())
        {
          localTL_dialog = (TLRPC.TL_dialog)paramArrayList.get(i20);
          i21 = i15;
          i22 = i14;
          i23 = i11;
          i24 = i8;
          i6 = i19;
          i7 = i18;
          i25 = i13;
          i26 = i12;
          i27 = k;
          i28 = i;
          i9 = i17;
          i10 = i16;
          if (localSharedPreferences.contains("hide_" + String.valueOf(localTL_dialog.id)))
          {
            i21 = i15;
            i22 = i14;
            i23 = i11;
            i24 = i8;
            i6 = i19;
            i7 = i18;
            i25 = i13;
            i26 = i12;
            i27 = k;
            i28 = i;
            i9 = i17;
            i10 = i16;
            if (localTL_dialog.unread_count > 0)
            {
              i29 = (int)localTL_dialog.id;
              i5 = (int)(localTL_dialog.id >> 32);
              if (!bool3) {
                break label1066;
              }
              j = i15;
              m = i14;
              if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id))
              {
                j = i15 + localTL_dialog.unread_count;
                m = i14 + 1;
                this.allHasU = true;
              }
              n = i13;
              i1 = i12;
              if (localSharedPreferences.contains("fav_" + String.valueOf(i29)))
              {
                if (!bool3) {
                  break label1103;
                }
                n = i13;
                i1 = i12;
                if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id))
                {
                  n = i13 + localTL_dialog.unread_count;
                  i1 = i12 + 1;
                  this.favHasU = true;
                }
              }
              i2 = i11;
              i3 = i8;
              if (!DialogObject.isChannel(localTL_dialog))
              {
                if ((i29 >= 0) || (i5 == 1)) {
                  break label1141;
                }
                i4 = 1;
                i2 = i11;
                i3 = i8;
                if (i4 == 0)
                {
                  i2 = i11;
                  i3 = i8;
                  if (i29 > 0)
                  {
                    i2 = i11;
                    i3 = i8;
                    if (i5 != 1)
                    {
                      localObject = MessagesController.getInstance().getUser(Integer.valueOf(i29));
                      if ((localObject == null) || (!((TLRPC.User)localObject).bot)) {
                        break label1147;
                      }
                      i4 = 1;
                      label659:
                      i2 = i11;
                      i3 = i8;
                      if (i4 == 0)
                      {
                        if (!bool3) {
                          break label1153;
                        }
                        i2 = i11;
                        i3 = i8;
                        if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id))
                        {
                          i2 = i11 + localTL_dialog.unread_count;
                          i3 = i8 + 1;
                          this.cHasU = true;
                        }
                      }
                    }
                  }
                }
              }
              label720:
              if ((i29 >= 0) || (i5 == 1)) {
                break label1191;
              }
              i6 = 1;
              label734:
              i4 = k;
              i5 = i;
              if (!DialogObject.isChannel(localTL_dialog))
              {
                i4 = k;
                i5 = i;
                if (i6 != 0)
                {
                  if (!bool3) {
                    break label1197;
                  }
                  i4 = k;
                  i5 = i;
                  if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id))
                  {
                    i4 = k + localTL_dialog.unread_count;
                    i5 = i + 1;
                    this.gHasU = true;
                  }
                }
              }
              label807:
              i21 = j;
              i22 = m;
              i23 = i2;
              i24 = i3;
              i6 = i19;
              i7 = i18;
              i25 = n;
              i26 = i1;
              i27 = i4;
              i28 = i5;
              i9 = i17;
              i10 = i16;
              if (DialogObject.isChannel(localTL_dialog))
              {
                localObject = MessagesController.getInstance().getChat(Integer.valueOf(-i29));
                if (!bool3) {
                  break label1297;
                }
                i21 = j;
                i22 = m;
                i23 = i2;
                i24 = i3;
                i6 = i19;
                i7 = i18;
                i25 = n;
                i26 = i1;
                i27 = i4;
                i28 = i5;
                i9 = i17;
                i10 = i16;
                if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id))
                {
                  if (!((TLRPC.Chat)localObject).megagroup) {
                    break label1234;
                  }
                  i9 = i17 + localTL_dialog.unread_count;
                  i10 = i16 + 1;
                  this.sgHasU = true;
                  i28 = i5;
                  i27 = i4;
                  i26 = i1;
                  i25 = n;
                  i7 = i18;
                  i6 = i19;
                  i24 = i3;
                  i23 = i2;
                  i22 = m;
                  i21 = j;
                }
              }
            }
          }
          for (;;)
          {
            i20 += 1;
            i15 = i21;
            i14 = i22;
            i11 = i23;
            i8 = i24;
            i19 = i6;
            i18 = i7;
            i13 = i25;
            i12 = i26;
            k = i27;
            i = i28;
            i17 = i9;
            i16 = i10;
            break;
            label1066:
            if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id)) {
              this.allHasU = true;
            }
            j = i15 + localTL_dialog.unread_count;
            m = i14 + 1;
            break label470;
            label1103:
            if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id)) {
              this.favHasU = true;
            }
            n = i13 + localTL_dialog.unread_count;
            i1 = i12 + 1;
            break label560;
            label1141:
            i4 = 0;
            break label590;
            label1147:
            i4 = 0;
            break label659;
            label1153:
            if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id)) {
              this.cHasU = true;
            }
            i2 = i11 + localTL_dialog.unread_count;
            i3 = i8 + 1;
            break label720;
            label1191:
            i6 = 0;
            break label734;
            label1197:
            if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id)) {
              this.gHasU = true;
            }
            i4 = k + localTL_dialog.unread_count;
            i5 = i + 1;
            break label807;
            label1234:
            i6 = i19 + localTL_dialog.unread_count;
            i7 = i18 + 1;
            this.chHasU = true;
            i21 = j;
            i22 = m;
            i23 = i2;
            i24 = i3;
            i25 = n;
            i26 = i1;
            i27 = i4;
            i28 = i5;
            i9 = i17;
            i10 = i16;
            continue;
            label1297:
            if (((TLRPC.Chat)localObject).megagroup)
            {
              if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id)) {
                this.sgHasU = true;
              }
              i9 = i17 + localTL_dialog.unread_count;
              i10 = i16 + 1;
              i21 = j;
              i22 = m;
              i23 = i2;
              i24 = i3;
              i6 = i19;
              i7 = i18;
              i25 = n;
              i26 = i1;
              i27 = i4;
              i28 = i5;
            }
            else
            {
              if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id)) {
                this.chHasU = true;
              }
              i6 = i19 + localTL_dialog.unread_count;
              i7 = i18 + 1;
              i21 = j;
              i22 = m;
              i23 = i2;
              i24 = i3;
              i25 = n;
              i26 = i1;
              i27 = i4;
              i28 = i5;
              i9 = i17;
              i10 = i16;
            }
          }
        }
      }
      else
      {
        i = 0;
        i16 = i31;
        i17 = i20;
        i20 = i;
        i = i30;
        k = i29;
        i12 = i28;
        i13 = i27;
        i18 = i26;
        i19 = i25;
        i8 = i24;
        i11 = i23;
        i14 = i22;
        i15 = i21;
        j = i15;
        m = i14;
        n = i11;
        i1 = i8;
        i4 = i19;
        i5 = i18;
        i2 = i13;
        i3 = i12;
        i6 = k;
        i7 = i;
        i9 = i17;
        i10 = i16;
        if (i20 < paramArrayList.size())
        {
          localTL_dialog = (TLRPC.TL_dialog)paramArrayList.get(i20);
          i21 = i15;
          i22 = i14;
          i23 = i11;
          i24 = i8;
          i6 = i19;
          i7 = i18;
          i25 = i13;
          i26 = i12;
          i27 = k;
          i28 = i;
          i9 = i17;
          i10 = i16;
          if (!localSharedPreferences.contains("hide_" + String.valueOf(localTL_dialog.id)))
          {
            i21 = i15;
            i22 = i14;
            i23 = i11;
            i24 = i8;
            i6 = i19;
            i7 = i18;
            i25 = i13;
            i26 = i12;
            i27 = k;
            i28 = i;
            i9 = i17;
            i10 = i16;
            if (localTL_dialog.unread_count > 0)
            {
              i29 = (int)localTL_dialog.id;
              i5 = (int)(localTL_dialog.id >> 32);
              if (!bool3) {
                break label2377;
              }
              j = i15;
              m = i14;
              if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id))
              {
                j = i15 + localTL_dialog.unread_count;
                m = i14 + 1;
                this.allHasU = true;
              }
              label1781:
              n = i13;
              i1 = i12;
              if (localSharedPreferences.contains("fav_" + String.valueOf(i29)))
              {
                if (!bool3) {
                  break label2414;
                }
                n = i13;
                i1 = i12;
                if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id))
                {
                  n = i13 + localTL_dialog.unread_count;
                  i1 = i12 + 1;
                  this.favHasU = true;
                }
              }
              label1871:
              i2 = i11;
              i3 = i8;
              if (!DialogObject.isChannel(localTL_dialog))
              {
                if ((i29 >= 0) || (i5 == 1)) {
                  break label2452;
                }
                i4 = 1;
                label1901:
                i2 = i11;
                i3 = i8;
                if (i4 == 0)
                {
                  i2 = i11;
                  i3 = i8;
                  if (i29 > 0)
                  {
                    i2 = i11;
                    i3 = i8;
                    if (i5 != 1)
                    {
                      localObject = MessagesController.getInstance().getUser(Integer.valueOf(i29));
                      if ((localObject == null) || (!((TLRPC.User)localObject).bot)) {
                        break label2458;
                      }
                      i4 = 1;
                      label1970:
                      i2 = i11;
                      i3 = i8;
                      if (i4 == 0)
                      {
                        if (!bool3) {
                          break label2464;
                        }
                        i2 = i11;
                        i3 = i8;
                        if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id))
                        {
                          i2 = i11 + localTL_dialog.unread_count;
                          i3 = i8 + 1;
                          this.cHasU = true;
                        }
                      }
                    }
                  }
                }
              }
              label2031:
              if ((i29 >= 0) || (i5 == 1)) {
                break label2502;
              }
              i6 = 1;
              label2045:
              i4 = k;
              i5 = i;
              if (!DialogObject.isChannel(localTL_dialog))
              {
                i4 = k;
                i5 = i;
                if (i6 != 0)
                {
                  if (!bool3) {
                    break label2508;
                  }
                  i4 = k;
                  i5 = i;
                  if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id))
                  {
                    i4 = k + localTL_dialog.unread_count;
                    i5 = i + 1;
                    this.gHasU = true;
                  }
                }
              }
              label2118:
              i21 = j;
              i22 = m;
              i23 = i2;
              i24 = i3;
              i6 = i19;
              i7 = i18;
              i25 = n;
              i26 = i1;
              i27 = i4;
              i28 = i5;
              i9 = i17;
              i10 = i16;
              if (DialogObject.isChannel(localTL_dialog))
              {
                localObject = MessagesController.getInstance().getChat(Integer.valueOf(-i29));
                if (!bool3) {
                  break label2608;
                }
                i21 = j;
                i22 = m;
                i23 = i2;
                i24 = i3;
                i6 = i19;
                i7 = i18;
                i25 = n;
                i26 = i1;
                i27 = i4;
                i28 = i5;
                i9 = i17;
                i10 = i16;
                if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id))
                {
                  if (!((TLRPC.Chat)localObject).megagroup) {
                    break label2545;
                  }
                  i9 = i17 + localTL_dialog.unread_count;
                  i10 = i16 + 1;
                  this.sgHasU = true;
                  i28 = i5;
                  i27 = i4;
                  i26 = i1;
                  i25 = n;
                  i7 = i18;
                  i6 = i19;
                  i24 = i3;
                  i23 = i2;
                  i22 = m;
                  i21 = j;
                }
              }
            }
          }
          for (;;)
          {
            i20 += 1;
            i15 = i21;
            i14 = i22;
            i11 = i23;
            i8 = i24;
            i19 = i6;
            i18 = i7;
            i13 = i25;
            i12 = i26;
            k = i27;
            i = i28;
            i17 = i9;
            i16 = i10;
            break;
            label2377:
            if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id)) {
              this.allHasU = true;
            }
            j = i15 + localTL_dialog.unread_count;
            m = i14 + 1;
            break label1781;
            label2414:
            if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id)) {
              this.favHasU = true;
            }
            n = i13 + localTL_dialog.unread_count;
            i1 = i12 + 1;
            break label1871;
            label2452:
            i4 = 0;
            break label1901;
            label2458:
            i4 = 0;
            break label1970;
            label2464:
            if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id)) {
              this.cHasU = true;
            }
            i2 = i11 + localTL_dialog.unread_count;
            i3 = i8 + 1;
            break label2031;
            label2502:
            i6 = 0;
            break label2045;
            label2508:
            if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id)) {
              this.gHasU = true;
            }
            i4 = k + localTL_dialog.unread_count;
            i5 = i + 1;
            break label2118;
            label2545:
            i6 = i19 + localTL_dialog.unread_count;
            i7 = i18 + 1;
            this.chHasU = true;
            i21 = j;
            i22 = m;
            i23 = i2;
            i24 = i3;
            i25 = n;
            i26 = i1;
            i27 = i4;
            i28 = i5;
            i9 = i17;
            i10 = i16;
            continue;
            label2608:
            if (((TLRPC.Chat)localObject).megagroup)
            {
              if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id)) {
                this.sgHasU = true;
              }
              i9 = i17 + localTL_dialog.unread_count;
              i10 = i16 + 1;
              i21 = j;
              i22 = m;
              i23 = i2;
              i24 = i3;
              i6 = i19;
              i7 = i18;
              i25 = n;
              i26 = i1;
              i27 = i4;
              i28 = i5;
            }
            else
            {
              if (!MessagesController.getInstance().isDialogMuted(localTL_dialog.id)) {
                this.chHasU = true;
              }
              i6 = i19 + localTL_dialog.unread_count;
              i7 = i18 + 1;
              i21 = j;
              i22 = m;
              i23 = i2;
              i24 = i3;
              i25 = n;
              i26 = i1;
              i27 = i4;
              i28 = i5;
              i9 = i17;
              i10 = i16;
            }
          }
        }
      }
    }
    if (localSharedPreferences.getBoolean("tabs_count_chats", false))
    {
      i9 = i10;
      i6 = i7;
      i = i3;
      i4 = i5;
      if (this.countCh != null)
      {
        if (i4 == 0) {
          break label3203;
        }
        if (this.countCh.getVisibility() == 4) {
          this.countCh.setVisibility(0);
        }
        this.countCh.setText(String.format("%d", new Object[] { Integer.valueOf(i4) }));
      }
      label2855:
      if (this.countSG != null)
      {
        if (i9 == 0) {
          break label3214;
        }
        if (this.countSG.getVisibility() == 4) {
          this.countSG.setVisibility(0);
        }
        this.countSG.setText(String.format("%d", new Object[] { Integer.valueOf(i9) }));
      }
      label2911:
      if (this.countG != null)
      {
        if (i6 == 0) {
          break label3225;
        }
        if (this.countG.getVisibility() == 4) {
          this.countG.setVisibility(0);
        }
        this.countG.setText(String.format("%d", new Object[] { Integer.valueOf(i6) }));
      }
      label2967:
      if (this.countCon != null)
      {
        if (i1 == 0) {
          break label3236;
        }
        if (this.countCon.getVisibility() == 4) {
          this.countCon.setVisibility(0);
        }
        this.countCon.setText(String.format("%d", new Object[] { Integer.valueOf(i1) }));
      }
      label3023:
      if (this.countfav != null)
      {
        if (i == 0) {
          break label3247;
        }
        if (this.countfav.getVisibility() == 4) {
          this.countfav.setVisibility(0);
        }
        this.countfav.setText(String.format("%d", new Object[] { Integer.valueOf(i) }));
      }
      label3077:
      if (this.countAll != null)
      {
        if (m == 0) {
          break label3258;
        }
        if (this.countAll.getVisibility() == 4) {
          this.countAll.setVisibility(0);
        }
        this.countAll.setText(String.format("%d", new Object[] { Integer.valueOf(m) }));
      }
    }
    for (;;)
    {
      if (this.countUnread != null)
      {
        if (m == 0) {
          break label3269;
        }
        if (this.countUnread.getVisibility() == 4) {
          this.countUnread.setVisibility(0);
        }
        this.countUnread.setText(String.format("%d", new Object[] { Integer.valueOf(m) }));
      }
      return;
      i = i2;
      m = j;
      i1 = n;
      break;
      label3203:
      this.countCh.setVisibility(4);
      break label2855;
      label3214:
      this.countSG.setVisibility(4);
      break label2911;
      label3225:
      this.countG.setVisibility(4);
      break label2967;
      label3236:
      this.countCon.setVisibility(4);
      break label3023;
      label3247:
      this.countfav.setVisibility(4);
      break label3077;
      label3258:
      this.countAll.setVisibility(4);
    }
    label3269:
    this.countUnread.setVisibility(4);
  }
  
  private void updateVisibleRows(int paramInt)
  {
    if (this.listView == null) {
      return;
    }
    int k = this.listView.getChildCount();
    int i = 0;
    if (i < k)
    {
      Object localObject = this.listView.getChildAt(i);
      boolean bool;
      if ((localObject instanceof DialogCell)) {
        if (this.listView.getAdapter() != this.dialogsSearchAdapter)
        {
          localObject = (DialogCell)localObject;
          if ((paramInt & 0x800) == 0) {
            break label126;
          }
          ((DialogCell)localObject).checkCurrentDialogIndex();
          if ((this.dialogsType == 0) && (AndroidUtilities.isTablet()))
          {
            if (((DialogCell)localObject).getDialogId() != this.openedDialogId) {
              break label120;
            }
            bool = true;
            label106:
            ((DialogCell)localObject).setDialogSelected(bool);
          }
        }
      }
      for (;;)
      {
        i += 1;
        break;
        label120:
        bool = false;
        break label106;
        label126:
        if ((paramInt & 0x200) != 0)
        {
          if ((this.dialogsType == 0) && (AndroidUtilities.isTablet()))
          {
            if (((DialogCell)localObject).getDialogId() == this.openedDialogId) {}
            for (bool = true;; bool = false)
            {
              ((DialogCell)localObject).setDialogSelected(bool);
              break;
            }
          }
        }
        else
        {
          ((DialogCell)localObject).update(paramInt);
          continue;
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
    updateUnreadCount(MessagesController.getInstance().dialogs);
    updateTabCounterColor();
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
    this.menu = this.actionBar.createMenu();
    if ((!this.onlySelect) && (this.searchString == null))
    {
      this.passcodeItem = this.menu.addItem(1, 2130837944);
      updatePasscodeButton();
    }
    final Object localObject3 = this.menu.addItem(4, 2130837864);
    final Object localObject4 = this.menu.addItem(3, 2130837821);
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    final Object localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
    init();
    final Object localObject2 = ((SharedPreferences)localObject1).getString("chat_password", "");
    final boolean bool1 = ((SharedPreferences)localObject1).getBoolean("chat_unlocked", false);
    if (!((SharedPreferences)localObject1).contains("swipe_tabs"))
    {
      localObject5 = ((SharedPreferences)localObject1).edit();
      ((SharedPreferences.Editor)localObject5).putBoolean("swipe_tabs", true);
      ((SharedPreferences.Editor)localObject5).commit();
    }
    this.swipe = ((SharedPreferences)localObject1).getBoolean("swipe_tabs", true);
    this.tabsHeight = localSharedPreferences.getInt("theme_tabs_height", 42);
    final boolean bool2 = ((SharedPreferences)localObject1).getBoolean("tool_bar", true);
    Object localObject5 = this.menu.addItem(0, 2130837831).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
    {
      public boolean canCollapseSearch()
      {
        if (DialogsActivity.this.searchString != null)
        {
          DialogsActivity.this.finishFragment();
          return false;
        }
        return true;
      }
      
      public void onSearchCollapse()
      {
        localObject3.setVisibility(0);
        localObject4.setVisibility(0);
        DialogsActivity.access$002(DialogsActivity.this, false);
        DialogsActivity.access$1102(DialogsActivity.this, false);
        if (DialogsActivity.this.listView != null)
        {
          DialogsActivity.this.searchEmptyView.setVisibility(8);
          if ((!MessagesController.getInstance().loadingDialogs) || (!MessagesController.getInstance().dialogs.isEmpty())) {
            break label270;
          }
          DialogsActivity.this.emptyView.setVisibility(8);
          DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.progressView);
          if (!DialogsActivity.this.onlySelect)
          {
            if (bool2) {
              break label302;
            }
            DialogsActivity.this.floatingButton.setVisibility(0);
            DialogsActivity.this.floatingButton1.setVisibility(0);
            DialogsActivity.access$1202(DialogsActivity.this, true);
            DialogsActivity.this.floatingButton.setTranslationY(AndroidUtilities.dp(100.0F));
            DialogsActivity.this.floatingButton1.setTranslationY(AndroidUtilities.dp(100.0F));
            DialogsActivity.this.hideFloatingButton(false);
          }
        }
        for (;;)
        {
          if (DialogsActivity.this.listView.getAdapter() != DialogsActivity.this.dialogsAdapter)
          {
            DialogsActivity.this.listView.setAdapter(DialogsActivity.this.dialogsAdapter);
            DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
          }
          if (DialogsActivity.this.dialogsSearchAdapter != null) {
            DialogsActivity.this.dialogsSearchAdapter.searchDialogs(null);
          }
          DialogsActivity.this.updatePasscodeButton();
          return;
          label270:
          DialogsActivity.this.progressView.setVisibility(8);
          DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.emptyView);
          break;
          label302:
          DialogsActivity.this.toolBar.setVisibility(0);
          DialogsActivity.access$1202(DialogsActivity.this, true);
          DialogsActivity.this.hideFloatingButton(false);
        }
      }
      
      public void onSearchExpand()
      {
        localObject3.setVisibility(8);
        localObject4.setVisibility(8);
        DialogsActivity.access$002(DialogsActivity.this, true);
        if (DialogsActivity.this.listView != null)
        {
          if (DialogsActivity.this.searchString != null)
          {
            DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.searchEmptyView);
            DialogsActivity.this.progressView.setVisibility(8);
            DialogsActivity.this.emptyView.setVisibility(8);
          }
          if (!DialogsActivity.this.onlySelect)
          {
            if (bool2) {
              break label137;
            }
            DialogsActivity.this.floatingButton.setVisibility(8);
            DialogsActivity.this.floatingButton1.setVisibility(8);
          }
        }
        for (;;)
        {
          DialogsActivity.this.updatePasscodeButton();
          return;
          label137:
          DialogsActivity.this.toolBar.setVisibility(8);
        }
      }
      
      public void onTextChanged(EditText paramAnonymousEditText)
      {
        paramAnonymousEditText = paramAnonymousEditText.getText().toString();
        if ((paramAnonymousEditText.length() != 0) || ((DialogsActivity.this.dialogsSearchAdapter != null) && (DialogsActivity.this.dialogsSearchAdapter.hasRecentRearch())))
        {
          DialogsActivity.access$1102(DialogsActivity.this, true);
          if ((DialogsActivity.this.dialogsSearchAdapter != null) && (DialogsActivity.this.listView.getAdapter() != DialogsActivity.this.dialogsSearchAdapter))
          {
            DialogsActivity.this.listView.setAdapter(DialogsActivity.this.dialogsSearchAdapter);
            DialogsActivity.this.dialogsSearchAdapter.notifyDataSetChanged();
          }
          if ((DialogsActivity.this.searchEmptyView != null) && (DialogsActivity.this.listView.getEmptyView() != DialogsActivity.this.searchEmptyView))
          {
            DialogsActivity.this.emptyView.setVisibility(8);
            DialogsActivity.this.progressView.setVisibility(8);
            DialogsActivity.this.searchEmptyView.showTextView();
            DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.searchEmptyView);
          }
        }
        if (DialogsActivity.this.dialogsSearchAdapter != null) {
          DialogsActivity.this.dialogsSearchAdapter.searchDialogs(paramAnonymousEditText);
        }
      }
    });
    ((ActionBarMenuItem)localObject5).getSearchField().setHint(LocaleController.getString("Search", 2131166261));
    ((ActionBarMenuItem)localObject5).getSearchField().setTypeface(MihanTheme.getMihanTypeFace());
    ((ActionBarMenuItem)localObject5).getSearchField().setTextColor(MihanTheme.getActionBarIconColor(localSharedPreferences));
    ((ActionBarMenuItem)localObject5).getSearchField().setHintTextColor(MihanTheme.getLighterColor(MihanTheme.getActionBarIconColor(localSharedPreferences), 0.5F));
    int i;
    if (this.onlySelect)
    {
      this.actionBar.setBackButtonImage(2130837829);
      this.actionBar.setTitle(LocaleController.getString("SelectChat", 2131166279));
      this.actionBar.setAllowOverlayTitle(true);
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          boolean bool2 = true;
          boolean bool1 = true;
          if (paramAnonymousInt == -1) {
            if (DialogsActivity.this.onlySelect) {
              DialogsActivity.this.finishFragment();
            }
          }
          do
          {
            do
            {
              return;
            } while (DialogsActivity.this.parentLayout == null);
            DialogsActivity.this.parentLayout.getDrawerLayoutContainer().openDrawer(false);
            return;
            if (paramAnonymousInt == 1)
            {
              if (!UserConfig.appLocked) {}
              for (;;)
              {
                UserConfig.appLocked = bool1;
                UserConfig.saveConfig(false);
                DialogsActivity.this.updatePasscodeButton();
                return;
                bool1 = false;
              }
            }
            if (paramAnonymousInt == 2)
            {
              DialogsActivity.this.presentFragment(new SetPasswordActivity(4), true);
              return;
            }
            if (paramAnonymousInt == 3)
            {
              localObject = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
              MessagesController.getInstance();
              MessagesController.openChatOrProfileWith((TLRPC.User)localObject, null, DialogsActivity.this, 1);
              return;
            }
          } while (paramAnonymousInt != 4);
          Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
          boolean bool3 = ((SharedPreferences)localObject).getBoolean("ghost_mode", false);
          localObject = ((SharedPreferences)localObject).edit();
          if (!bool3)
          {
            bool1 = true;
            ((SharedPreferences.Editor)localObject).putBoolean("ghost_mode", bool1);
            ((SharedPreferences.Editor)localObject).commit();
            localObject = DialogsActivity.this.actionBar;
            if (bool3) {
              break label239;
            }
          }
          label239:
          for (bool1 = bool2;; bool1 = false)
          {
            ((ActionBar)localObject).setGhostImage(bool1);
            MessagesController.getInstance().reRunUpdateTimerProc();
            return;
            bool1 = false;
            break;
          }
        }
      });
      localObject3 = new SizeNotifierFrameLayout(paramContext);
      this.fragmentView = ((View)localObject3);
      ((SizeNotifierFrameLayout)localObject3).setBackgroundImage(ApplicationLoader.getCachedWallpaper());
      createTabs(paramContext, (FrameLayout)localObject3);
      this.listView = new RecyclerListView(paramContext);
      this.listView.setVerticalScrollBarEnabled(true);
      this.listView.setItemAnimator(null);
      this.listView.setInstantClick(true);
      this.listView.setLayoutAnimation(null);
      this.listView.setTag(Integer.valueOf(4));
      this.layoutManager = new LinearLayoutManager(paramContext)
      {
        public boolean supportsPredictiveItemAnimations()
        {
          return false;
        }
      };
      this.layoutManager.setOrientation(1);
      this.listView.setLayoutManager(this.layoutManager);
      localObject4 = this.listView;
      if (!LocaleController.isRTL) {
        break label1365;
      }
      i = 1;
      label517:
      ((RecyclerListView)localObject4).setVerticalScrollbarPosition(i);
      ((SizeNotifierFrameLayout)localObject3).addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if ((DialogsActivity.this.listView == null) || (DialogsActivity.this.listView.getAdapter() == null)) {}
          Object localObject1;
          label559:
          label599:
          label837:
          label860:
          do
          {
            long l1;
            int i;
            Object localObject2;
            for (;;)
            {
              return;
              long l2 = 0L;
              j = 0;
              paramAnonymousView = DialogsActivity.this.listView.getAdapter();
              if (paramAnonymousView == DialogsActivity.this.dialogsAdapter)
              {
                localObject1 = DialogsActivity.this.dialogsAdapter.getItem(paramAnonymousInt);
                if (localObject1 != null)
                {
                  l1 = ((TLRPC.TL_dialog)localObject1).id;
                  i = j;
                }
              }
              else
              {
                while (l1 != 0L)
                {
                  if (!DialogsActivity.this.onlySelect) {
                    break label559;
                  }
                  DialogsActivity.this.didSelectResult(l1, true, false);
                  return;
                  l1 = l2;
                  i = j;
                  if (paramAnonymousView == DialogsActivity.this.dialogsSearchAdapter)
                  {
                    localObject1 = DialogsActivity.this.dialogsSearchAdapter.getItem(paramAnonymousInt);
                    if ((localObject1 instanceof TLRPC.User))
                    {
                      l2 = ((TLRPC.User)localObject1).id;
                      if (DialogsActivity.this.dialogsSearchAdapter.isGlobalSearch(paramAnonymousInt))
                      {
                        localObject2 = new ArrayList();
                        ((ArrayList)localObject2).add((TLRPC.User)localObject1);
                        MessagesController.getInstance().putUsers((ArrayList)localObject2, false);
                        MessagesStorage.getInstance().putUsersAndChats((ArrayList)localObject2, null, false, true);
                      }
                      l1 = l2;
                      i = j;
                      if (!DialogsActivity.this.onlySelect)
                      {
                        DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(l2, (TLRPC.User)localObject1);
                        l1 = l2;
                        i = j;
                      }
                    }
                    else
                    {
                      if ((localObject1 instanceof TLRPC.Chat))
                      {
                        if (DialogsActivity.this.dialogsSearchAdapter.isGlobalSearch(paramAnonymousInt))
                        {
                          localObject2 = new ArrayList();
                          ((ArrayList)localObject2).add((TLRPC.Chat)localObject1);
                          MessagesController.getInstance().putChats((ArrayList)localObject2, false);
                          MessagesStorage.getInstance().putUsersAndChats(null, (ArrayList)localObject2, false, true);
                        }
                        if (((TLRPC.Chat)localObject1).id > 0) {}
                        for (l2 = -((TLRPC.Chat)localObject1).id;; l2 = AndroidUtilities.makeBroadcastId(((TLRPC.Chat)localObject1).id))
                        {
                          l1 = l2;
                          i = j;
                          if (DialogsActivity.this.onlySelect) {
                            break;
                          }
                          DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(l2, (TLRPC.Chat)localObject1);
                          l1 = l2;
                          i = j;
                          break;
                        }
                      }
                      if ((localObject1 instanceof TLRPC.EncryptedChat))
                      {
                        l2 = ((TLRPC.EncryptedChat)localObject1).id << 32;
                        l1 = l2;
                        i = j;
                        if (!DialogsActivity.this.onlySelect)
                        {
                          DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(l2, (TLRPC.EncryptedChat)localObject1);
                          l1 = l2;
                          i = j;
                        }
                      }
                      else if ((localObject1 instanceof MessageObject))
                      {
                        localObject1 = (MessageObject)localObject1;
                        l1 = ((MessageObject)localObject1).getDialogId();
                        i = ((MessageObject)localObject1).getId();
                        DialogsActivity.this.dialogsSearchAdapter.addHashtagsFromMessage(DialogsActivity.this.dialogsSearchAdapter.getLastSearchString());
                      }
                      else
                      {
                        l1 = l2;
                        i = j;
                        if ((localObject1 instanceof String))
                        {
                          DialogsActivity.this.actionBar.openSearchField((String)localObject1);
                          l1 = l2;
                          i = j;
                        }
                      }
                    }
                  }
                }
              }
            }
            localObject1 = new Bundle();
            int j = (int)l1;
            paramAnonymousInt = (int)(l1 >> 32);
            if (j != 0) {
              if (paramAnonymousInt == 1)
              {
                ((Bundle)localObject1).putInt("chat_id", j);
                if (i == 0) {
                  break label837;
                }
                ((Bundle)localObject1).putInt("message_id", i);
              }
            }
            for (;;)
            {
              if (AndroidUtilities.isTablet())
              {
                if ((DialogsActivity.this.openedDialogId == l1) && (paramAnonymousView != DialogsActivity.this.dialogsSearchAdapter)) {
                  break;
                }
                if (DialogsActivity.this.dialogsAdapter != null)
                {
                  DialogsActivity.this.dialogsAdapter.setOpenedDialogId(DialogsActivity.access$2302(DialogsActivity.this, l1));
                  DialogsActivity.this.updateVisibleRows(512);
                }
              }
              if (DialogsActivity.this.searchString == null) {
                break label860;
              }
              if (!MessagesController.checkCanOpenChat((Bundle)localObject1, DialogsActivity.this)) {
                break;
              }
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
              DialogsActivity.this.presentFragment(new ChatActivity((Bundle)localObject1));
              return;
              if (j > 0)
              {
                ((Bundle)localObject1).putInt("user_id", j);
                break label599;
              }
              if (j >= 0) {
                break label599;
              }
              paramAnonymousInt = j;
              if (i != 0)
              {
                localObject2 = MessagesController.getInstance().getChat(Integer.valueOf(-j));
                paramAnonymousInt = j;
                if (localObject2 != null)
                {
                  paramAnonymousInt = j;
                  if (((TLRPC.Chat)localObject2).migrated_to != null)
                  {
                    ((Bundle)localObject1).putInt("migrated_to", j);
                    paramAnonymousInt = -((TLRPC.Chat)localObject2).migrated_to.channel_id;
                  }
                }
              }
              ((Bundle)localObject1).putInt("chat_id", -paramAnonymousInt);
              break label599;
              ((Bundle)localObject1).putInt("enc_id", paramAnonymousInt);
              break label599;
              if (DialogsActivity.this.actionBar != null) {
                DialogsActivity.this.actionBar.closeSearchField();
              }
            }
          } while (!MessagesController.checkCanOpenChat((Bundle)localObject1, DialogsActivity.this));
          DialogsActivity.this.presentFragment(new ChatActivity((Bundle)localObject1));
        }
      });
      this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if ((DialogsActivity.this.onlySelect) || ((DialogsActivity.this.searching) && (DialogsActivity.this.searchWas)) || (DialogsActivity.this.getParentActivity() == null))
          {
            if (((DialogsActivity.this.searchWas) && (DialogsActivity.this.searching)) || ((DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) && (DialogsActivity.this.listView.getAdapter() == DialogsActivity.this.dialogsSearchAdapter) && (((DialogsActivity.this.dialogsSearchAdapter.getItem(paramAnonymousInt) instanceof String)) || (DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()))))
            {
              paramAnonymousView = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
              paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165338));
              paramAnonymousView.setMessage(LocaleController.getString("ClearSearch", 2131165550));
              paramAnonymousView.setPositiveButton(LocaleController.getString("ClearButton", 2131165544).toUpperCase(), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  if (DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed())
                  {
                    DialogsActivity.this.dialogsSearchAdapter.clearRecentSearch();
                    return;
                  }
                  DialogsActivity.this.dialogsSearchAdapter.clearRecentHashtags();
                }
              });
              paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
              DialogsActivity.this.showDialog(paramAnonymousView.create());
              return true;
            }
            return false;
          }
          paramAnonymousView = DialogsActivity.this.getDialogsArray();
          if ((paramAnonymousInt < 0) || (paramAnonymousInt >= paramAnonymousView.size())) {
            return false;
          }
          final Object localObject3 = (TLRPC.TL_dialog)paramAnonymousView.get(paramAnonymousInt);
          DialogsActivity.access$2602(DialogsActivity.this, ((TLRPC.TL_dialog)localObject3).id);
          final boolean bool3 = localObject1.contains("fav_" + String.valueOf(DialogsActivity.this.selectedDialog));
          final boolean bool4 = localObject1.contains("hide_" + String.valueOf(DialogsActivity.this.selectedDialog));
          final boolean bool5 = MessagesController.getInstance().isDialogMuted(DialogsActivity.this.selectedDialog);
          BottomSheet.Builder localBuilder = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
          paramAnonymousInt = (int)DialogsActivity.this.selectedDialog;
          int i = (int)(DialogsActivity.this.selectedDialog >> 32);
          final Object localObject2;
          Object localObject1;
          if (DialogObject.isChannel((TLRPC.TL_dialog)localObject3))
          {
            localObject2 = MessagesController.getInstance().getChat(Integer.valueOf(-paramAnonymousInt));
            if ((localObject2 != null) && (((TLRPC.Chat)localObject2).megagroup))
            {
              localObject1 = new CharSequence[6];
              localObject1[0] = LocaleController.getString("MarkAsRead", 2131165920);
              if (bool3)
              {
                paramAnonymousView = LocaleController.getString("RemoveFromFavorites", 2131166209);
                localObject1[1] = paramAnonymousView;
                if (!bool1) {
                  break label590;
                }
                paramAnonymousView = LocaleController.getString("ShowChat", 2131166340);
                label474:
                localObject1[2] = paramAnonymousView;
                if (bool5) {
                  break label603;
                }
                paramAnonymousView = LocaleController.getString("MuteNotifications", 2131165979);
                label492:
                localObject1[3] = paramAnonymousView;
                localObject1[4] = LocaleController.getString("ClearHistoryCache", 2131165546);
                if ((localObject2 != null) && (((TLRPC.Chat)localObject2).creator)) {
                  break label616;
                }
              }
              label590:
              label603:
              label616:
              for (paramAnonymousView = LocaleController.getString("LeaveMegaMenu", 2131165888);; paramAnonymousView = LocaleController.getString("DeleteMegaMenu", 2131165616))
              {
                localObject1[5] = paramAnonymousView;
                paramAnonymousView = (View)localObject1;
                localBuilder.setItems(paramAnonymousView, new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                  {
                    paramAnonymous2DialogInterface = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
                    paramAnonymous2DialogInterface.setTitle(LocaleController.getString("AppName", 2131165338));
                    if (paramAnonymous2Int == 0) {
                      MessagesController.getInstance().markDialogAsRead(DialogsActivity.this.selectedDialog, Math.max(0, localObject3.top_message), Math.max(0, localObject3.top_message), localObject3.last_message_date, true, false);
                    }
                    do
                    {
                      for (;;)
                      {
                        if ((paramAnonymous2Int == 4) || (paramAnonymous2Int == 5))
                        {
                          paramAnonymous2DialogInterface.setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
                          DialogsActivity.this.showDialog(paramAnonymous2DialogInterface.create());
                        }
                        return;
                        Object localObject;
                        if (paramAnonymous2Int == 1)
                        {
                          if (bool3)
                          {
                            localObject = DialogsActivity.6.this.val$preferences.edit();
                            ((SharedPreferences.Editor)localObject).remove("fav_" + String.valueOf(DialogsActivity.this.selectedDialog));
                            ((SharedPreferences.Editor)localObject).commit();
                            DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                          }
                          else
                          {
                            localObject = DialogsActivity.6.this.val$preferences.edit();
                            ((SharedPreferences.Editor)localObject).putInt("fav_" + String.valueOf(DialogsActivity.this.selectedDialog), (int)DialogsActivity.this.selectedDialog);
                            ((SharedPreferences.Editor)localObject).commit();
                          }
                        }
                        else if (paramAnonymous2Int == 2)
                        {
                          if (DialogsActivity.6.this.val$chatPassword.length() == 0)
                          {
                            DialogsActivity.this.presentFragment(new SetPasswordActivity(0));
                          }
                          else if ((DialogsActivity.6.this.val$chatUnlocked) && (bool4))
                          {
                            localObject = DialogsActivity.6.this.val$preferences.edit();
                            ((SharedPreferences.Editor)localObject).remove("hide_" + String.valueOf(DialogsActivity.this.selectedDialog));
                            ((SharedPreferences.Editor)localObject).commit();
                            DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                          }
                          else if ((!DialogsActivity.6.this.val$chatUnlocked) && (!bool4))
                          {
                            localObject = DialogsActivity.6.this.val$preferences.edit();
                            ((SharedPreferences.Editor)localObject).putInt("hide_" + String.valueOf(DialogsActivity.this.selectedDialog), (int)DialogsActivity.this.selectedDialog);
                            ((SharedPreferences.Editor)localObject).commit();
                            DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                          }
                        }
                        else
                        {
                          if (paramAnonymous2Int != 3) {
                            break;
                          }
                          if (!bool5)
                          {
                            localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                            ((SharedPreferences.Editor)localObject).putInt("notify2_" + DialogsActivity.this.selectedDialog, 2);
                            MessagesStorage.getInstance().setDialogFlags(DialogsActivity.this.selectedDialog, 1L);
                            ((SharedPreferences.Editor)localObject).commit();
                            if (localObject3 != null)
                            {
                              localObject3.notify_settings = new TLRPC.TL_peerNotifySettings();
                              localObject3.notify_settings.mute_until = Integer.MAX_VALUE;
                            }
                            NotificationsController.updateServerNotificationsSettings(DialogsActivity.this.selectedDialog);
                            NotificationsController.getInstance().removeNotificationsForDialog(DialogsActivity.this.selectedDialog);
                          }
                          else
                          {
                            localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                            ((SharedPreferences.Editor)localObject).putInt("notify2_" + DialogsActivity.this.selectedDialog, 0);
                            MessagesStorage.getInstance().setDialogFlags(DialogsActivity.this.selectedDialog, 0L);
                            ((SharedPreferences.Editor)localObject).commit();
                            localObject = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(DialogsActivity.this.selectedDialog));
                            if (localObject != null) {
                              ((TLRPC.TL_dialog)localObject).notify_settings = new TLRPC.TL_peerNotifySettings();
                            }
                            NotificationsController.updateServerNotificationsSettings(DialogsActivity.this.selectedDialog);
                          }
                        }
                      }
                      if (paramAnonymous2Int == 4)
                      {
                        if ((localObject2 != null) && (localObject2.megagroup)) {
                          paramAnonymous2DialogInterface.setMessage(LocaleController.getString("AreYouSureClearHistorySuper", 2131165346));
                        }
                        for (;;)
                        {
                          paramAnonymous2DialogInterface.setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
                          {
                            public void onClick(DialogInterface paramAnonymous3DialogInterface, int paramAnonymous3Int)
                            {
                              MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 2);
                            }
                          });
                          break;
                          paramAnonymous2DialogInterface.setMessage(LocaleController.getString("AreYouSureClearHistoryChannel", 2131165345));
                        }
                      }
                    } while (paramAnonymous2Int != 5);
                    if ((localObject2 != null) && (localObject2.megagroup)) {
                      if (!localObject2.creator) {
                        paramAnonymous2DialogInterface.setMessage(LocaleController.getString("MegaLeaveAlert", 2131165925));
                      }
                    }
                    for (;;)
                    {
                      paramAnonymous2DialogInterface.setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
                      {
                        public void onClick(DialogInterface paramAnonymous3DialogInterface, int paramAnonymous3Int)
                        {
                          MessagesController.getInstance().deleteUserFromChat((int)-DialogsActivity.this.selectedDialog, UserConfig.getCurrentUser(), null);
                          if (AndroidUtilities.isTablet()) {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[] { Long.valueOf(DialogsActivity.this.selectedDialog) });
                          }
                        }
                      });
                      break;
                      paramAnonymous2DialogInterface.setMessage(LocaleController.getString("MegaDeleteAlert", 2131165923));
                      continue;
                      if ((localObject2 == null) || (!localObject2.creator)) {
                        paramAnonymous2DialogInterface.setMessage(LocaleController.getString("ChannelLeaveAlert", 2131165472));
                      } else {
                        paramAnonymous2DialogInterface.setMessage(LocaleController.getString("ChannelDeleteAlert", 2131165461));
                      }
                    }
                  }
                });
                DialogsActivity.this.showDialog(localBuilder.create());
                return true;
                paramAnonymousView = LocaleController.getString("AddToFavorites", 2131165307);
                break;
                paramAnonymousView = LocaleController.getString("HideChat", 2131165795);
                break label474;
                paramAnonymousView = LocaleController.getString("UnmuteNotifications", 2131166423);
                break label492;
              }
            }
            localObject1 = new CharSequence[6];
            localObject1[0] = LocaleController.getString("MarkAsRead", 2131165920);
            if (bool3)
            {
              paramAnonymousView = LocaleController.getString("RemoveFromFavorites", 2131166209);
              label660:
              localObject1[1] = paramAnonymousView;
              if (!bool1) {
                break label761;
              }
              paramAnonymousView = LocaleController.getString("ShowChat", 2131166340);
              label680:
              localObject1[2] = paramAnonymousView;
              if (bool5) {
                break label774;
              }
              paramAnonymousView = LocaleController.getString("MuteNotifications", 2131165979);
              label698:
              localObject1[3] = paramAnonymousView;
              localObject1[4] = LocaleController.getString("ClearHistoryCache", 2131165546);
              if ((localObject2 != null) && (((TLRPC.Chat)localObject2).creator)) {
                break label787;
              }
            }
            label761:
            label774:
            label787:
            for (paramAnonymousView = LocaleController.getString("LeaveChannelMenu", 2131165886);; paramAnonymousView = LocaleController.getString("ChannelDeleteMenu", 2131165463))
            {
              localObject1[5] = paramAnonymousView;
              paramAnonymousView = (View)localObject1;
              break;
              paramAnonymousView = LocaleController.getString("AddToFavorites", 2131165307);
              break label660;
              paramAnonymousView = LocaleController.getString("HideChat", 2131165795);
              break label680;
              paramAnonymousView = LocaleController.getString("UnmuteNotifications", 2131166423);
              break label698;
            }
          }
          final boolean bool1;
          label812:
          final boolean bool2;
          label863:
          String str2;
          label886:
          label902:
          String str1;
          label916:
          String str3;
          if ((paramAnonymousInt < 0) && (i != 1))
          {
            bool1 = true;
            localObject1 = null;
            paramAnonymousView = (View)localObject1;
            if (!bool1)
            {
              paramAnonymousView = (View)localObject1;
              if (paramAnonymousInt > 0)
              {
                paramAnonymousView = (View)localObject1;
                if (i != 1) {
                  paramAnonymousView = MessagesController.getInstance().getUser(Integer.valueOf(paramAnonymousInt));
                }
              }
            }
            if ((paramAnonymousView == null) || (!paramAnonymousView.bot)) {
              break label1028;
            }
            bool2 = true;
            str2 = LocaleController.getString("MarkAsRead", 2131165920);
            if (!bool3) {
              break label1034;
            }
            localObject1 = LocaleController.getString("RemoveFromFavorites", 2131166209);
            if (!bool1) {
              break label1048;
            }
            localObject2 = LocaleController.getString("ShowChat", 2131166340);
            if (bool5) {
              break label1062;
            }
            str1 = LocaleController.getString("MuteNotifications", 2131165979);
            str3 = LocaleController.getString("ClearHistory", 2131165545);
            if (!bool1) {
              break label1076;
            }
            paramAnonymousView = LocaleController.getString("DeleteChat", 2131165609);
          }
          for (;;)
          {
            localObject3 = new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, final int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
                paramAnonymous2DialogInterface.setTitle(LocaleController.getString("AppName", 2131165338));
                if (paramAnonymous2Int == 0) {
                  MessagesController.getInstance().markDialogAsRead(DialogsActivity.this.selectedDialog, Math.max(0, localObject3.top_message), Math.max(0, localObject3.top_message), localObject3.last_message_date, true, false);
                }
                for (;;)
                {
                  paramAnonymous2DialogInterface.setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
                  {
                    public void onClick(DialogInterface paramAnonymous3DialogInterface, int paramAnonymous3Int)
                    {
                      if (paramAnonymous2Int == 5) {
                        if (DialogsActivity.6.3.this.val$isChat)
                        {
                          paramAnonymous3DialogInterface = MessagesController.getInstance().getChat(Integer.valueOf((int)-DialogsActivity.this.selectedDialog));
                          if ((paramAnonymous3DialogInterface != null) && (ChatObject.isNotInChat(paramAnonymous3DialogInterface)))
                          {
                            MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 0);
                            if (DialogsActivity.6.3.this.val$isBot) {
                              MessagesController.getInstance().blockUser((int)DialogsActivity.this.selectedDialog);
                            }
                            if (AndroidUtilities.isTablet()) {
                              NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[] { Long.valueOf(DialogsActivity.this.selectedDialog) });
                            }
                          }
                        }
                      }
                      while (paramAnonymous2Int != 4) {
                        for (;;)
                        {
                          return;
                          MessagesController.getInstance().deleteUserFromChat((int)-DialogsActivity.this.selectedDialog, MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), null);
                          MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 0);
                          continue;
                          MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 0);
                        }
                      }
                      MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 1);
                    }
                  });
                  if ((paramAnonymous2Int == 4) || (paramAnonymous2Int == 5))
                  {
                    paramAnonymous2DialogInterface.setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
                    DialogsActivity.this.showDialog(paramAnonymous2DialogInterface.create());
                  }
                  return;
                  Object localObject;
                  if (paramAnonymous2Int == 1)
                  {
                    if (bool3)
                    {
                      localObject = DialogsActivity.6.this.val$preferences.edit();
                      ((SharedPreferences.Editor)localObject).remove("fav_" + String.valueOf(DialogsActivity.this.selectedDialog));
                      ((SharedPreferences.Editor)localObject).commit();
                      DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                      localObject = DialogsActivity.6.this.val$preferences.edit();
                      ((SharedPreferences.Editor)localObject).putInt("fav_" + String.valueOf(DialogsActivity.this.selectedDialog), (int)DialogsActivity.this.selectedDialog);
                      ((SharedPreferences.Editor)localObject).commit();
                    }
                  }
                  else if (paramAnonymous2Int == 2)
                  {
                    if (DialogsActivity.6.this.val$chatPassword.length() == 0)
                    {
                      DialogsActivity.this.presentFragment(new SetPasswordActivity(0));
                    }
                    else if ((DialogsActivity.6.this.val$chatUnlocked) && (bool4))
                    {
                      localObject = DialogsActivity.6.this.val$preferences.edit();
                      ((SharedPreferences.Editor)localObject).remove("hide_" + String.valueOf(DialogsActivity.this.selectedDialog));
                      ((SharedPreferences.Editor)localObject).commit();
                      DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                    }
                    else if ((!DialogsActivity.6.this.val$chatUnlocked) && (!bool4))
                    {
                      localObject = DialogsActivity.6.this.val$preferences.edit();
                      ((SharedPreferences.Editor)localObject).putInt("hide_" + String.valueOf(DialogsActivity.this.selectedDialog), (int)DialogsActivity.this.selectedDialog);
                      ((SharedPreferences.Editor)localObject).commit();
                      DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                    }
                  }
                  else if (paramAnonymous2Int == 3)
                  {
                    if (!bool5)
                    {
                      localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                      ((SharedPreferences.Editor)localObject).putInt("notify2_" + DialogsActivity.this.selectedDialog, 2);
                      MessagesStorage.getInstance().setDialogFlags(DialogsActivity.this.selectedDialog, 1L);
                      ((SharedPreferences.Editor)localObject).commit();
                      if (localObject3 != null)
                      {
                        localObject3.notify_settings = new TLRPC.TL_peerNotifySettings();
                        localObject3.notify_settings.mute_until = Integer.MAX_VALUE;
                      }
                      NotificationsController.updateServerNotificationsSettings(DialogsActivity.this.selectedDialog);
                      NotificationsController.getInstance().removeNotificationsForDialog(DialogsActivity.this.selectedDialog);
                    }
                    else
                    {
                      localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                      ((SharedPreferences.Editor)localObject).putInt("notify2_" + DialogsActivity.this.selectedDialog, 0);
                      MessagesStorage.getInstance().setDialogFlags(DialogsActivity.this.selectedDialog, 0L);
                      ((SharedPreferences.Editor)localObject).commit();
                      localObject = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(DialogsActivity.this.selectedDialog));
                      if (localObject != null) {
                        ((TLRPC.TL_dialog)localObject).notify_settings = new TLRPC.TL_peerNotifySettings();
                      }
                      NotificationsController.updateServerNotificationsSettings(DialogsActivity.this.selectedDialog);
                    }
                  }
                  else if (paramAnonymous2Int == 4) {
                    paramAnonymous2DialogInterface.setMessage(LocaleController.getString("AreYouSureClearHistory", 2131165344));
                  } else if (paramAnonymous2Int == 5) {
                    if (bool1) {
                      paramAnonymous2DialogInterface.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", 2131165347));
                    } else {
                      paramAnonymous2DialogInterface.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", 2131165352));
                    }
                  }
                }
              }
            };
            localBuilder.setItems(new CharSequence[] { str2, localObject1, localObject2, str1, str3, paramAnonymousView }, (DialogInterface.OnClickListener)localObject3);
            DialogsActivity.this.showDialog(localBuilder.create());
            break;
            bool1 = false;
            break label812;
            label1028:
            bool2 = false;
            break label863;
            label1034:
            localObject1 = LocaleController.getString("AddToFavorites", 2131165307);
            break label886;
            label1048:
            localObject2 = LocaleController.getString("HideChat", 2131165795);
            break label902;
            label1062:
            str1 = LocaleController.getString("UnmuteNotifications", 2131166423);
            break label916;
            label1076:
            if (bool2) {
              paramAnonymousView = LocaleController.getString("DeleteAndStop", 2131165607);
            } else {
              paramAnonymousView = LocaleController.getString("Delete", 2131165600);
            }
          }
        }
      });
      if (this.swipe) {
        this.listView.setOnTouchListener(new View.OnTouchListener()
        {
          public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
          {
            DialogsActivity.this.onSwipe(paramAnonymousMotionEvent);
            return false;
          }
        });
      }
      this.searchEmptyView = new EmptyTextProgressView(paramContext);
      this.searchEmptyView.setVisibility(8);
      this.searchEmptyView.setShowAtCenter(true);
      this.searchEmptyView.setText(LocaleController.getString("NoResult", 2131166020));
      ((SizeNotifierFrameLayout)localObject3).addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0F));
      this.emptyView = new LinearLayout(paramContext);
      this.emptyView.setOrientation(1);
      this.emptyView.setVisibility(8);
      this.emptyView.setGravity(17);
      this.emptyView.setClickable(true);
      ((SizeNotifierFrameLayout)localObject3).addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
      if (!this.swipe) {
        break label1371;
      }
      this.emptyView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          DialogsActivity.this.onSwipe(paramAnonymousMotionEvent);
          return false;
        }
      });
      label743:
      localObject1 = new TextView(paramContext);
      ((TextView)localObject1).setText(LocaleController.getString("NoChats", 2131166002));
      ((TextView)localObject1).setTextColor(MihanTheme.getDialogNameColor(localSharedPreferences));
      ((TextView)localObject1).setGravity(17);
      ((TextView)localObject1).setTextSize(1, 20.0F);
      this.emptyView.addView((View)localObject1, LayoutHelper.createLinear(-2, -2));
      ((TextView)localObject1).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      localObject4 = new TextView(paramContext);
      localObject2 = LocaleController.getString("NoChatsHelp", 2131166003);
      localObject1 = localObject2;
      if (AndroidUtilities.isTablet())
      {
        localObject1 = localObject2;
        if (!AndroidUtilities.isSmallTablet()) {
          localObject1 = ((String)localObject2).replace('\n', ' ');
        }
      }
      ((TextView)localObject4).setText((CharSequence)localObject1);
      ((TextView)localObject4).setTextColor(MihanTheme.getDialogNameColor(localSharedPreferences));
      ((TextView)localObject4).setTextSize(1, 15.0F);
      ((TextView)localObject4).setGravity(17);
      ((TextView)localObject4).setPadding(AndroidUtilities.dp(8.0F), AndroidUtilities.dp(6.0F), AndroidUtilities.dp(8.0F), 0);
      ((TextView)localObject4).setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
      ((TextView)localObject4).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.emptyView.addView((View)localObject4, LayoutHelper.createLinear(-2, -2));
      this.progressView = new ProgressBar(paramContext);
      this.progressView.setVisibility(8);
      ((SizeNotifierFrameLayout)localObject3).addView(this.progressView, LayoutHelper.createFrame(-2, -2, 17));
      if (!bool2) {
        break label1389;
      }
      createToolBar(paramContext, (FrameLayout)localObject3);
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
        {
          if ((paramAnonymousInt == 1) && (DialogsActivity.this.searching) && (DialogsActivity.this.searchWas)) {
            AndroidUtilities.hideKeyboard(DialogsActivity.this.getParentActivity().getCurrentFocus());
          }
        }
        
        public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          int i = DialogsActivity.this.layoutManager.findFirstVisibleItemPosition();
          paramAnonymousInt1 = Math.abs(DialogsActivity.this.layoutManager.findLastVisibleItemPosition() - i) + 1;
          paramAnonymousInt2 = paramAnonymousRecyclerView.getAdapter().getItemCount();
          if ((DialogsActivity.this.searching) && (DialogsActivity.this.searchWas)) {
            if ((paramAnonymousInt1 > 0) && (DialogsActivity.this.layoutManager.findLastVisibleItemPosition() == paramAnonymousInt2 - 1) && (!DialogsActivity.this.dialogsSearchAdapter.isMessagesSearchEndReached())) {
              DialogsActivity.this.dialogsSearchAdapter.loadMoreSearchMessages();
            }
          }
          label235:
          label311:
          label340:
          do
          {
            do
            {
              return;
              if ((paramAnonymousInt1 > 0) && (DialogsActivity.this.layoutManager.findLastVisibleItemPosition() >= DialogsActivity.this.getDialogsArray().size() - 10))
              {
                MessagesController localMessagesController = MessagesController.getInstance();
                if (MessagesController.getInstance().dialogsEndReached) {
                  break;
                }
                bool = true;
                localMessagesController.loadDialogs(-1, 100, bool);
              }
              if (bool2) {
                break label340;
              }
            } while (DialogsActivity.this.floatingButton.getVisibility() == 8);
            paramAnonymousRecyclerView = paramAnonymousRecyclerView.getChildAt(0);
            paramAnonymousInt2 = 0;
            if (paramAnonymousRecyclerView != null) {
              paramAnonymousInt2 = paramAnonymousRecyclerView.getTop();
            }
            paramAnonymousInt1 = 1;
            if (DialogsActivity.this.prevPosition == i)
            {
              paramAnonymousInt1 = DialogsActivity.this.prevTop;
              if (paramAnonymousInt2 < DialogsActivity.this.prevTop)
              {
                bool = true;
                if (Math.abs(paramAnonymousInt1 - paramAnonymousInt2) <= 1) {
                  break label311;
                }
              }
              for (paramAnonymousInt1 = 1;; paramAnonymousInt1 = 0)
              {
                if ((paramAnonymousInt1 != 0) && (DialogsActivity.this.scrollUpdated)) {
                  DialogsActivity.this.hideFloatingButton(bool);
                }
                DialogsActivity.access$2902(DialogsActivity.this, i);
                DialogsActivity.access$3002(DialogsActivity.this, paramAnonymousInt2);
                DialogsActivity.access$3102(DialogsActivity.this, true);
                return;
                bool = false;
                break;
                bool = false;
                break label235;
              }
            }
            if (i > DialogsActivity.this.prevPosition) {}
            for (bool = true;; bool = false) {
              break;
            }
          } while (DialogsActivity.this.toolBar.getVisibility() == 8);
          paramAnonymousRecyclerView = paramAnonymousRecyclerView.getChildAt(0);
          paramAnonymousInt2 = 0;
          if (paramAnonymousRecyclerView != null) {
            paramAnonymousInt2 = paramAnonymousRecyclerView.getTop();
          }
          paramAnonymousInt1 = 1;
          if (DialogsActivity.this.prevPosition == i)
          {
            paramAnonymousInt1 = DialogsActivity.this.prevTop;
            if (paramAnonymousInt2 < DialogsActivity.this.prevTop)
            {
              bool = true;
              if (Math.abs(paramAnonymousInt1 - paramAnonymousInt2) <= 1) {
                break label478;
              }
            }
            label478:
            for (paramAnonymousInt1 = 1;; paramAnonymousInt1 = 0)
            {
              if ((paramAnonymousInt1 != 0) && (DialogsActivity.this.scrollUpdated)) {
                DialogsActivity.this.hideFloatingButton(bool);
              }
              DialogsActivity.access$2902(DialogsActivity.this, i);
              DialogsActivity.access$3002(DialogsActivity.this, paramAnonymousInt2);
              DialogsActivity.access$3102(DialogsActivity.this, true);
              return;
              bool = false;
              break;
            }
          }
          if (i > DialogsActivity.this.prevPosition) {}
          for (boolean bool = true;; bool = false) {
            break;
          }
        }
      });
      updateLayout();
      if (this.searchString == null)
      {
        this.dialogsAdapter = new DialogsAdapter(paramContext, this.dialogsType);
        if ((AndroidUtilities.isTablet()) && (this.openedDialogId != 0L)) {
          this.dialogsAdapter.setOpenedDialogId(this.openedDialogId);
        }
        this.listView.setAdapter(this.dialogsAdapter);
      }
      i = 0;
      if (this.searchString == null) {
        break label2025;
      }
      i = 2;
      label1112:
      this.dialogsSearchAdapter = new DialogsSearchAdapter(paramContext, i, this.dialogsType);
      this.dialogsSearchAdapter.setDelegate(new DialogsSearchAdapter.DialogsSearchAdapterDelegate()
      {
        public void didPressedOnSubDialog(int paramAnonymousInt)
        {
          if (DialogsActivity.this.onlySelect) {
            DialogsActivity.this.didSelectResult(paramAnonymousInt, true, false);
          }
          Bundle localBundle;
          label168:
          do
          {
            return;
            localBundle = new Bundle();
            if (paramAnonymousInt > 0) {
              localBundle.putInt("user_id", paramAnonymousInt);
            }
            for (;;)
            {
              if (DialogsActivity.this.actionBar != null) {
                DialogsActivity.this.actionBar.closeSearchField();
              }
              if ((AndroidUtilities.isTablet()) && (DialogsActivity.this.dialogsAdapter != null))
              {
                DialogsActivity.this.dialogsAdapter.setOpenedDialogId(DialogsActivity.access$2302(DialogsActivity.this, paramAnonymousInt));
                DialogsActivity.this.updateVisibleRows(512);
              }
              if (DialogsActivity.this.searchString == null) {
                break label168;
              }
              if (!MessagesController.checkCanOpenChat(localBundle, DialogsActivity.this)) {
                break;
              }
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
              DialogsActivity.this.presentFragment(new ChatActivity(localBundle));
              return;
              localBundle.putInt("chat_id", -paramAnonymousInt);
            }
          } while (!MessagesController.checkCanOpenChat(localBundle, DialogsActivity.this));
          DialogsActivity.this.presentFragment(new ChatActivity(localBundle));
        }
        
        public void needRemoveHint(final int paramAnonymousInt)
        {
          if (DialogsActivity.this.getParentActivity() == null) {}
          TLRPC.User localUser;
          do
          {
            return;
            localUser = MessagesController.getInstance().getUser(Integer.valueOf(paramAnonymousInt));
          } while (localUser == null);
          AlertDialog.Builder localBuilder = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
          localBuilder.setTitle(LocaleController.getString("AppName", 2131165338));
          localBuilder.setMessage(LocaleController.formatString("ChatHintsDelete", 2131165536, new Object[] { ContactsController.formatName(localUser.first_name, localUser.last_name) }));
          localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              SearchQuery.removePeer(paramAnonymousInt);
            }
          });
          localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
          DialogsActivity.this.showDialog(localBuilder.create());
        }
        
        public void searchStateChanged(boolean paramAnonymousBoolean)
        {
          if ((DialogsActivity.this.searching) && (DialogsActivity.this.searchWas) && (DialogsActivity.this.searchEmptyView != null))
          {
            if (paramAnonymousBoolean) {
              DialogsActivity.this.searchEmptyView.showProgress();
            }
          }
          else {
            return;
          }
          DialogsActivity.this.searchEmptyView.showTextView();
        }
      });
      if ((!MessagesController.getInstance().loadingDialogs) || (!MessagesController.getInstance().dialogs.isEmpty())) {
        break label2038;
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
      if ((!this.onlySelect) && (this.dialogsType == 0)) {
        ((SizeNotifierFrameLayout)localObject3).addView(new PlayerView(paramContext, this), LayoutHelper.createFrame(-1, 39.0F, 51, 0.0F, -36.0F, 0.0F, 0.0F));
      }
      return this.fragmentView;
      if (this.searchString != null)
      {
        this.actionBar.setBackButtonImage(2130837829);
        ((ActionBarMenuItem)localObject3).setVisibility(8);
        ((ActionBarMenuItem)localObject4).setVisibility(8);
      }
      for (;;)
      {
        if (!BuildVars.DEBUG_VERSION) {
          break label1346;
        }
        this.actionBar.setTitle(LocaleController.getString("AppNameBeta", 2131165339));
        break;
        this.actionBar.setBackButtonDrawable(new MenuDrawable());
        ((ActionBarMenuItem)localObject3).setVisibility(0);
        ((ActionBarMenuItem)localObject4).setVisibility(0);
      }
      label1346:
      this.actionBar.setTitle(LocaleController.getString("AppName", 2131165338));
      break;
      label1365:
      i = 2;
      break label517;
      label1371:
      this.emptyView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      break label743;
      label1389:
      this.floatingButton = new ImageView(paramContext);
      localObject1 = this.floatingButton;
      label1418:
      label1615:
      float f1;
      if (this.onlySelect)
      {
        i = 8;
        ((ImageView)localObject1).setVisibility(i);
        this.floatingButton.setScaleType(ImageView.ScaleType.CENTER);
        this.floatingButton.setBackgroundResource(2130837794);
        this.floatingButton.setImageResource(2130837792);
        if (Build.VERSION.SDK_INT >= 21)
        {
          localObject1 = new StateListAnimator();
          localObject2 = ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
          ((StateListAnimator)localObject1).addState(new int[] { 16842919 }, (Animator)localObject2);
          localObject2 = ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
          ((StateListAnimator)localObject1).addState(new int[0], (Animator)localObject2);
          this.floatingButton.setStateListAnimator((StateListAnimator)localObject1);
          this.floatingButton.setOutlineProvider(new ViewOutlineProvider()
          {
            @SuppressLint({"NewApi"})
            public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
            {
              paramAnonymousOutline.setOval(0, 0, AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
            }
          });
        }
        localObject1 = this.floatingButton;
        if (!LocaleController.isRTL) {
          break label1983;
        }
        i = 3;
        if (!LocaleController.isRTL) {
          break label1989;
        }
        f1 = 14.0F;
        label1625:
        if (!LocaleController.isRTL) {
          break label1994;
        }
        f2 = 0.0F;
        label1633:
        ((SizeNotifierFrameLayout)localObject3).addView((View)localObject1, LayoutHelper.createFrame(-2, -2.0F, i | 0x50, f1, 0.0F, f2, 14.0F));
        this.floatingButton1 = new ImageView(paramContext);
        localObject1 = this.floatingButton1;
        if (!this.onlySelect) {
          break label2001;
        }
        i = 8;
        label1688:
        ((ImageView)localObject1).setVisibility(i);
        this.floatingButton1.setScaleType(ImageView.ScaleType.CENTER);
        this.floatingButton1.setBackgroundResource(2130837794);
        this.floatingButton1.setImageResource(2130837838);
        if (Build.VERSION.SDK_INT >= 21)
        {
          localObject1 = new StateListAnimator();
          localObject2 = ObjectAnimator.ofFloat(this.floatingButton1, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
          ((StateListAnimator)localObject1).addState(new int[] { 16842919 }, (Animator)localObject2);
          localObject2 = ObjectAnimator.ofFloat(this.floatingButton1, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
          ((StateListAnimator)localObject1).addState(new int[0], (Animator)localObject2);
          this.floatingButton1.setStateListAnimator((StateListAnimator)localObject1);
          this.floatingButton1.setOutlineProvider(new ViewOutlineProvider()
          {
            @SuppressLint({"NewApi"})
            public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
            {
              paramAnonymousOutline.setOval(0, 0, AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
            }
          });
        }
        localObject1 = this.floatingButton1;
        if (!LocaleController.isRTL) {
          break label2007;
        }
        i = 3;
        label1885:
        if (!LocaleController.isRTL) {
          break label2013;
        }
        f1 = 82.0F;
        label1895:
        if (!LocaleController.isRTL) {
          break label2018;
        }
      }
      label1983:
      label1989:
      label1994:
      label2001:
      label2007:
      label2013:
      label2018:
      for (float f2 = 0.0F;; f2 = 82.0F)
      {
        ((SizeNotifierFrameLayout)localObject3).addView((View)localObject1, LayoutHelper.createFrame(-2, -2.0F, i | 0x50, f1, 0.0F, f2, 14.0F));
        this.floatingButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = new Bundle();
            paramAnonymousView.putBoolean("destroyAfterSelect", true);
            DialogsActivity.this.presentFragment(new ContactsActivity(paramAnonymousView));
          }
        });
        this.floatingButton.setOnLongClickListener(new View.OnLongClickListener()
        {
          public boolean onLongClick(View paramAnonymousView)
          {
            if (ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getString("chat_password", "").length() == 0)
            {
              DialogsActivity.this.presentFragment(new SetPasswordActivity(0));
              return false;
            }
            DialogsActivity.this.presentFragment(new SetPasswordActivity(4), true);
            return false;
          }
        });
        this.floatingButton1.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
            String str1 = LocaleController.getString("MultiMarkAsRead", 2131166653);
            String str2 = LocaleController.getString("MultiMuteChats", 2131166654);
            String str3 = LocaleController.getString("MultiUnMuteChats", 2131166655);
            String str4 = LocaleController.getString("MultiAddToFavorites", 2131166650);
            String str5 = LocaleController.getString("MultiClearHistory", 2131166651);
            String str6 = LocaleController.getString("MultiDeleteAndLeave", 2131166652);
            DialogInterface.OnClickListener local1 = new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).edit();
                if (paramAnonymous2Int == 0)
                {
                  paramAnonymous2DialogInterface.putInt("op_type", 1);
                  paramAnonymous2DialogInterface.commit();
                  DialogsActivity.this.presentFragment(new BatchDialogsActivity());
                }
                do
                {
                  return;
                  if (paramAnonymous2Int == 1)
                  {
                    paramAnonymous2DialogInterface.putInt("op_type", 2);
                    paramAnonymous2DialogInterface.commit();
                    DialogsActivity.this.presentFragment(new BatchDialogsActivity());
                    return;
                  }
                  if (paramAnonymous2Int == 2)
                  {
                    paramAnonymous2DialogInterface.putInt("op_type", 6);
                    paramAnonymous2DialogInterface.commit();
                    DialogsActivity.this.presentFragment(new BatchDialogsActivity());
                    return;
                  }
                  if (paramAnonymous2Int == 3)
                  {
                    paramAnonymous2DialogInterface.putInt("op_type", 3);
                    paramAnonymous2DialogInterface.commit();
                    DialogsActivity.this.presentFragment(new BatchDialogsActivity());
                    return;
                  }
                  if (paramAnonymous2Int == 4)
                  {
                    paramAnonymous2DialogInterface.putInt("op_type", 4);
                    paramAnonymous2DialogInterface.commit();
                    DialogsActivity.this.presentFragment(new BatchDialogsActivity());
                    return;
                  }
                } while (paramAnonymous2Int != 5);
                paramAnonymous2DialogInterface.putInt("op_type", 5);
                paramAnonymous2DialogInterface.commit();
                DialogsActivity.this.presentFragment(new BatchDialogsActivity());
              }
            };
            paramAnonymousView.setItems(new CharSequence[] { str1, str2, str3, str4, str5, str6 }, local1);
            DialogsActivity.this.showDialog(paramAnonymousView.create());
          }
        });
        break;
        i = 0;
        break label1418;
        i = 5;
        break label1615;
        f1 = 0.0F;
        break label1625;
        f2 = 14.0F;
        break label1633;
        i = 0;
        break label1688;
        i = 5;
        break label1885;
        f1 = 0.0F;
        break label1895;
      }
      label2025:
      if (this.onlySelect) {
        break label1112;
      }
      i = 1;
      break label1112;
      label2038:
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
      else
      {
        if (this.dialogsSearchAdapter != null) {
          this.dialogsSearchAdapter.notifyDataSetChanged();
        }
        if (this.listView == null) {}
      }
    }
    label455:
    label490:
    do
    {
      do
      {
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
                  break label455;
                }
                if (this.dialogsSearchAdapter != null) {
                  this.dialogsSearchAdapter.loadRecentSearch();
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
            catch (Exception localException)
            {
              FileLog.e("tmessages", localException);
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
              if ((this.dialogsType == 0) && (AndroidUtilities.isTablet()))
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
          if (paramInt == NotificationCenter.didLoadedReplyMessages)
          {
            updateVisibleRows(0);
            return;
          }
          if (paramInt != NotificationCenter.reloadHints) {
            break label490;
          }
        } while (this.dialogsSearchAdapter == null);
        this.dialogsSearchAdapter.notifyDataSetChanged();
        return;
      } while (paramInt != NotificationCenter.MihanUpdateInterface);
      paramInt = ((Integer)paramVarArgs[0]).intValue();
      if (paramInt == 1)
      {
        updateLayout();
        return;
      }
    } while (paramInt != 2);
    if (((Boolean)paramVarArgs[1]).booleanValue())
    {
      this.emptyView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          DialogsActivity.this.onSwipe(paramAnonymousMotionEvent);
          return false;
        }
      });
      this.listView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          DialogsActivity.this.onSwipe(paramAnonymousMotionEvent);
          return false;
        }
      });
      return;
    }
    this.emptyView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
  }
  
  public boolean isMainDialogList()
  {
    return (this.delegate == null) && (this.searchString == null);
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if ((!this.onlySelect) && (this.floatingButton != null) && (this.floatingButton1 != null))
    {
      this.floatingButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
      {
        public void onGlobalLayout()
        {
          ImageView localImageView = DialogsActivity.this.floatingButton;
          float f;
          if (DialogsActivity.this.floatingHidden)
          {
            f = AndroidUtilities.dp(100.0F);
            localImageView.setTranslationY(f);
            localImageView = DialogsActivity.this.floatingButton;
            if (DialogsActivity.this.floatingHidden) {
              break label93;
            }
          }
          label93:
          for (boolean bool = true;; bool = false)
          {
            localImageView.setClickable(bool);
            if (DialogsActivity.this.floatingButton != null)
            {
              if (Build.VERSION.SDK_INT >= 16) {
                break label98;
              }
              DialogsActivity.this.floatingButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
            return;
            f = 0.0F;
            break;
          }
          label98:
          DialogsActivity.this.floatingButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
      });
      this.floatingButton1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
      {
        public void onGlobalLayout()
        {
          ImageView localImageView = DialogsActivity.this.floatingButton1;
          float f;
          if (DialogsActivity.this.floatingHidden)
          {
            f = AndroidUtilities.dp(100.0F);
            localImageView.setTranslationY(f);
            localImageView = DialogsActivity.this.floatingButton1;
            if (DialogsActivity.this.floatingHidden) {
              break label93;
            }
          }
          label93:
          for (boolean bool = true;; bool = false)
          {
            localImageView.setClickable(bool);
            if (DialogsActivity.this.floatingButton1 != null)
            {
              if (Build.VERSION.SDK_INT >= 16) {
                break label98;
              }
              DialogsActivity.this.floatingButton1.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
            return;
            f = 0.0F;
            break;
          }
          label98:
          DialogsActivity.this.floatingButton1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
      });
      this.toolBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
      {
        public void onGlobalLayout()
        {
          LinearLayout localLinearLayout = DialogsActivity.this.toolBar;
          float f;
          if (DialogsActivity.this.floatingHidden)
          {
            f = AndroidUtilities.dp(100.0F);
            localLinearLayout.setTranslationY(f);
            localLinearLayout = DialogsActivity.this.toolBar;
            if (DialogsActivity.this.floatingHidden) {
              break label93;
            }
          }
          label93:
          for (boolean bool = true;; bool = false)
          {
            localLinearLayout.setClickable(bool);
            if (DialogsActivity.this.toolBar != null)
            {
              if (Build.VERSION.SDK_INT >= 16) {
                break label98;
              }
              DialogsActivity.this.toolBar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
            return;
            f = 0.0F;
            break;
          }
          label98:
          DialogsActivity.this.toolBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
      });
    }
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
    if (getArguments() != null)
    {
      this.onlySelect = this.arguments.getBoolean("onlySelect", false);
      this.dialogsType = this.arguments.getInt("dialogsType", 0);
      this.selectAlertString = this.arguments.getString("selectAlertString");
      this.selectAlertStringGroup = this.arguments.getString("selectAlertStringGroup");
      this.addToGroupAlertString = this.arguments.getString("addToGroupAlertString");
    }
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
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.MihanUpdateInterface);
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
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.MihanUpdateInterface);
    }
    this.delegate = null;
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
    Object localObject;
    if (!this.onlySelect)
    {
      localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
      int i = ((SharedPreferences)localObject).getInt("last_tab", 8);
      if (i != 8)
      {
        localObject = ((SharedPreferences)localObject).edit();
        ((SharedPreferences.Editor)localObject).putInt("selected_tab", i);
        ((SharedPreferences.Editor)localObject).putInt("last_tab", 8);
        ((SharedPreferences.Editor)localObject).commit();
      }
    }
    if (this.dialogsAdapter != null) {
      this.dialogsAdapter.notifyDataSetChanged();
    }
    if (this.dialogsSearchAdapter != null) {
      this.dialogsSearchAdapter.notifyDataSetChanged();
    }
    if ((this.checkPermission) && (!this.onlySelect) && (Build.VERSION.SDK_INT >= 23))
    {
      localObject = getParentActivity();
      if (localObject != null)
      {
        this.checkPermission = false;
        if ((((Activity)localObject).checkSelfPermission("android.permission.READ_CONTACTS") != 0) || (((Activity)localObject).checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0))
        {
          if (!((Activity)localObject).shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
            break label254;
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
      updateUnreadCount(MessagesController.getInstance().dialogs);
      updateColors();
      return;
      label254:
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
  
  public void setDelegate(DialogsActivityDelegate paramDialogsActivityDelegate)
  {
    this.delegate = paramDialogsActivityDelegate;
  }
  
  public void setSearchString(String paramString)
  {
    this.searchString = paramString;
  }
  
  public static abstract interface DialogsActivityDelegate
  {
    public abstract void didSelectDialog(DialogsActivity paramDialogsActivity, long paramLong, boolean paramBoolean);
  }
  
  private class SlidingTabView
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
      DialogsActivity.access$3402(DialogsActivity.this, MihanTheme.getSelectedTabIconColor(paramContext));
      DialogsActivity.access$3502(DialogsActivity.this, MihanTheme.getTabsIconColor(paramContext));
      setWeightSum(ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getInt("tab_count", 8));
      this.paint.setColor(DialogsActivity.this.sIconColor);
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
    
    private void tabOnClick(int paramInt)
    {
      final Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
      final SharedPreferences.Editor localEditor = ((SharedPreferences)localObject).edit();
      final int i = ((Integer)this.tabInPosition.get(Integer.valueOf(paramInt))).intValue();
      if (i == ((SharedPreferences)localObject).getInt("selected_tab", this.selectedTab))
      {
        BottomSheet.Builder localBuilder = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
        String str1 = LocaleController.getString("DisableTab", 2131166626);
        String str2 = LocaleController.getString("SetTabAsDefault", 2131166669);
        String str3 = LocaleController.getString("MarkChatsAsRead", 2131166649);
        localObject = new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            new AlertDialog.Builder(DialogsActivity.this.getParentActivity()).setTitle(LocaleController.getString("AppName", 2131165338));
            switch (paramAnonymousInt)
            {
            }
            for (;;)
            {
              return;
              if (i == 6)
              {
                paramAnonymousDialogInterface = Toast.makeText(DialogsActivity.this.getParentActivity(), LocaleController.getString("TabAllDisable", 2131166676), 1);
                ((TextView)((LinearLayout)paramAnonymousDialogInterface.getView()).getChildAt(0)).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                paramAnonymousDialogInterface.show();
                return;
              }
              switch (i)
              {
              }
              for (;;)
              {
                localEditor.putInt("default_tab", 6);
                localEditor.putInt("tab_count", localObject.getInt("tab_count", 8) - 1);
                localEditor.commit();
                paramAnonymousDialogInterface = DialogsActivity.this.getParentActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(DialogsActivity.this.getParentActivity().getPackageName());
                paramAnonymousDialogInterface.addFlags(67108864);
                paramAnonymousDialogInterface.addFlags(32768);
                DialogsActivity.this.getParentActivity().startActivity(paramAnonymousDialogInterface);
                return;
                localEditor.putBoolean("tab_bot", false);
                continue;
                localEditor.putBoolean("tab_channel", false);
                continue;
                localEditor.putBoolean("tab_supergroup", false);
                continue;
                localEditor.putBoolean("tab_group", false);
                continue;
                localEditor.putBoolean("tab_contact", false);
                continue;
                localEditor.putBoolean("tab_favorite", false);
                continue;
                localEditor.putBoolean("tab_unread", false);
              }
              localEditor.putInt("default_tab", DialogsActivity.SlidingTabView.this.selectedTab);
              localEditor.commit();
              return;
              paramAnonymousDialogInterface = DialogsActivity.this.getDialogsArray();
              paramAnonymousInt = 0;
              while (paramAnonymousInt < paramAnonymousDialogInterface.size())
              {
                TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)paramAnonymousDialogInterface.get(paramAnonymousInt);
                if (localTL_dialog.unread_count > 0)
                {
                  long l = localTL_dialog.id;
                  MessagesController.getInstance().markDialogAsRead(l, Math.max(0, localTL_dialog.top_message), Math.max(0, localTL_dialog.top_message), localTL_dialog.last_message_date, true, false);
                }
                paramAnonymousInt += 1;
              }
            }
          }
        };
        localBuilder.setItems(new CharSequence[] { str1, str2, str3 }, (DialogInterface.OnClickListener)localObject);
        DialogsActivity.this.showDialog(localBuilder.create());
      }
      do
      {
        return;
        didSelectTab(paramInt);
      } while (DialogsActivity.this.dialogsAdapter == null);
      DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
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
          MihanTheme.setColorFilter(localImageView.getDrawable(), DialogsActivity.this.sIconColor);
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
            DialogsActivity.SlidingTabView.this.tabOnClick(i);
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
        MihanTheme.setColorFilter(localImageView.getDrawable(), DialogsActivity.this.iconColor);
      }
    }
    
    public void didSelectTab(int paramInt)
    {
      int i = ((Integer)this.tabInPosition.get(Integer.valueOf(paramInt))).intValue();
      if (this.selectedTab == i) {
        return;
      }
      this.selectedTab = i;
      setTabsLayout(i);
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).edit();
      localEditor.putInt("selected_tab", this.selectedTab);
      localEditor.commit();
      animateToTab(paramInt);
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
        MihanTheme.setColorFilter(((ImageView)this.tabs.get(Integer.valueOf(i))).getDrawable(), DialogsActivity.this.iconColor);
      }
      MihanTheme.setColorFilter(((ImageView)this.tabs.get(Integer.valueOf(paramInt))).getDrawable(), DialogsActivity.this.sIconColor);
      switch (paramInt)
      {
      default: 
        return;
      case 0: 
        DialogsActivity.this.actionBar.setTitle(LocaleController.getString("RobotTab", 2131166248));
        return;
      case 1: 
        DialogsActivity.this.actionBar.setTitle(LocaleController.getString("ChannelTab", 2131165518));
        return;
      case 2: 
        DialogsActivity.this.actionBar.setTitle(LocaleController.getString("SuperGroupsTab", 2131166675));
        return;
      case 3: 
        DialogsActivity.this.actionBar.setTitle(LocaleController.getString("GroupsTab", 2131165791));
        return;
      case 4: 
        DialogsActivity.this.actionBar.setTitle(LocaleController.getString("ContactTab", 2131165562));
        return;
      case 5: 
        DialogsActivity.this.actionBar.setTitle(LocaleController.getString("FavoriteTab", 2131165684));
        return;
      case 6: 
        DialogsActivity.this.actionBar.setTitle(LocaleController.getString("AppName", 2131165338));
        return;
      }
      DialogsActivity.this.actionBar.setTitle(LocaleController.getString("UnreadTab", 2131166427));
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\DialogsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */