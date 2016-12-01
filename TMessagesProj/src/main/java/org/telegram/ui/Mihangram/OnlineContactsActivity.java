package org.telegram.ui.Supergram;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseSectionsAdapter;
import org.telegram.ui.Adapters.SearchAdapter;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.GreySectionCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.LetterSectionsListView;
import org.telegram.ui.Supergram.Theming.MihanTheme;

public class OnlineContactsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private boolean allowBots = true;
  private boolean allowUsernameSearch = true;
  private int chat_id;
  private boolean createSecretChat;
  private boolean creatingChat = false;
  private ContactsActivityDelegate delegate;
  private boolean destroyAfterSelect;
  private TextView emptyTextView;
  private HashMap<Integer, TLRPC.User> ignoreUsers;
  private LetterSectionsListView listView;
  private BaseSectionsAdapter listViewAdapter;
  private boolean needForwardCount = true;
  private boolean needPhonebook;
  private boolean onlyUsers;
  private boolean returnAsResult;
  private SearchAdapter searchListViewAdapter;
  private boolean searchWas;
  private boolean searching;
  private String selectAlertString = null;
  
  public OnlineContactsActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }
  
  private void didSelectResult(final TLRPC.User paramUser, boolean paramBoolean, final String paramString)
  {
    if ((paramBoolean) && (this.selectAlertString != null))
    {
      if (getParentActivity() == null) {}
      do
      {
        return;
        if ((paramUser.bot) && (paramUser.bot_nochats)) {
          try
          {
            Toast.makeText(getParentActivity(), LocaleController.getString("BotCantJoinGroups", 2131165406), 0).show();
            return;
          }
          catch (Exception paramUser)
          {
            FileLog.e("tmessages", paramUser);
            return;
          }
        }
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
        localBuilder.setTitle(LocaleController.getString("AppName", 2131165338));
        String str2 = LocaleController.formatStringSimple(this.selectAlertString, new Object[] { UserObject.getUserName(paramUser) });
        Object localObject = null;
        paramString = (String)localObject;
        String str1 = str2;
        if (!paramUser.bot)
        {
          paramString = (String)localObject;
          str1 = str2;
          if (this.needForwardCount)
          {
            str1 = String.format("%s\n\n%s", new Object[] { str2, LocaleController.getString("AddToTheGroupForwardCount", 2131165310) });
            paramString = new EditText(getParentActivity());
            if (Build.VERSION.SDK_INT < 11) {
              paramString.setBackgroundResource(17301529);
            }
            paramString.setTextSize(18.0F);
            paramString.setText("50");
            paramString.setGravity(17);
            paramString.setInputType(2);
            paramString.setImeOptions(6);
            paramString.addTextChangedListener(new TextWatcher()
            {
              public void afterTextChanged(Editable paramAnonymousEditable)
              {
                int i;
                try
                {
                  paramAnonymousEditable = paramAnonymousEditable.toString();
                  if (paramAnonymousEditable.length() == 0) {
                    return;
                  }
                  i = Utilities.parseInt(paramAnonymousEditable).intValue();
                  if (i < 0)
                  {
                    paramString.setText("0");
                    paramString.setSelection(paramString.length());
                    return;
                  }
                  if (i > 300)
                  {
                    paramString.setText("300");
                    paramString.setSelection(paramString.length());
                    return;
                  }
                }
                catch (Exception paramAnonymousEditable)
                {
                  FileLog.e("tmessages", paramAnonymousEditable);
                  return;
                }
                if (!paramAnonymousEditable.equals("" + i))
                {
                  paramString.setText("" + i);
                  paramString.setSelection(paramString.length());
                }
              }
              
              public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
              
              public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
            });
            localBuilder.setView(paramString);
          }
        }
        localBuilder.setMessage(str1);
        localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            OnlineContactsActivity localOnlineContactsActivity = OnlineContactsActivity.this;
            TLRPC.User localUser = paramUser;
            if (paramString != null) {}
            for (paramAnonymousDialogInterface = paramString.getText().toString();; paramAnonymousDialogInterface = "0")
            {
              localOnlineContactsActivity.didSelectResult(localUser, false, paramAnonymousDialogInterface);
              return;
            }
          }
        });
        localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
        showDialog(localBuilder.create());
      } while (paramString == null);
      paramUser = (ViewGroup.MarginLayoutParams)paramString.getLayoutParams();
      if (paramUser != null)
      {
        if ((paramUser instanceof FrameLayout.LayoutParams)) {
          ((FrameLayout.LayoutParams)paramUser).gravity = 1;
        }
        int i = AndroidUtilities.dp(10.0F);
        paramUser.leftMargin = i;
        paramUser.rightMargin = i;
        paramString.setLayoutParams(paramUser);
      }
      paramString.setSelection(paramString.getText().length());
      return;
    }
    if (this.delegate != null)
    {
      this.delegate.didSelectContact(paramUser, paramString);
      this.delegate = null;
    }
    finishFragment();
  }
  
  private void updateColors(ActionBarMenu paramActionBarMenu)
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    int i = localSharedPreferences.getInt("theme_contact_action_color", MihanTheme.getActionBarColor(localSharedPreferences));
    int j = localSharedPreferences.getInt("theme_contact_action_gradient", MihanTheme.getActionBarGradientFlag(localSharedPreferences));
    int k = localSharedPreferences.getInt("theme_contact_action_gcolor", MihanTheme.getActionBarGradientColor(localSharedPreferences));
    Object localObject;
    if (j != 0)
    {
      localObject = MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j));
      this.actionBar.setBackgroundDrawable((Drawable)localObject);
    }
    for (;;)
    {
      i = localSharedPreferences.getInt("theme_contact_action_icolor", MihanTheme.getActionBarIconColor(localSharedPreferences));
      this.actionBar.setTitleColor(localSharedPreferences.getInt("theme_contact_action_tcolor", MihanTheme.getActionBarTitleColor(localSharedPreferences)));
      localObject = ApplicationLoader.applicationContext.getResources().getDrawable(2130837829);
      MihanTheme.setColorFilter((Drawable)localObject, i);
      this.actionBar.setBackButtonDrawable((Drawable)localObject);
      MihanTheme.setColorFilter(paramActionBarMenu.getItem(0).getImageView().getDrawable(), i);
      i = localSharedPreferences.getInt("theme_contact_list_color", MihanTheme.getListViewColor(localSharedPreferences));
      j = localSharedPreferences.getInt("theme_contact_list_gradient", MihanTheme.getListViewGradientFlag(localSharedPreferences));
      k = localSharedPreferences.getInt("theme_contact_list_gcolor", MihanTheme.getListViewGradientColor(localSharedPreferences));
      if (j == 0) {
        break;
      }
      paramActionBarMenu = MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j));
      this.listView.setBackgroundDrawable(paramActionBarMenu);
      return;
      this.actionBar.setBackgroundColor(i);
    }
    this.listView.setBackgroundColor(i);
  }
  
  private void updateVisibleRows(int paramInt)
  {
    if (this.listView != null)
    {
      int j = this.listView.getChildCount();
      int i = 0;
      while (i < j)
      {
        View localView = this.listView.getChildAt(i);
        if ((localView instanceof UserCell)) {
          ((UserCell)localView).update(paramInt);
        }
        i += 1;
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    this.searching = false;
    this.searchWas = false;
    this.actionBar.setBackButtonImage(2130837812);
    this.actionBar.setAllowOverlayTitle(true);
    ActionBarMenu localActionBarMenu;
    if (this.destroyAfterSelect) {
      if (this.returnAsResult)
      {
        this.actionBar.setTitle(LocaleController.getString("SelectContact", 2131166280));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
        {
          public void onItemClick(int paramAnonymousInt)
          {
            if (paramAnonymousInt == -1) {
              OnlineContactsActivity.this.finishFragment();
            }
            while (paramAnonymousInt != 0) {
              return;
            }
            new ContactsController().readContacts();
          }
        });
        localActionBarMenu = this.actionBar.createMenu();
        localActionBarMenu.addItem(0, 2130837906);
        this.searchListViewAdapter = new SearchAdapter(paramContext, this.ignoreUsers, this.allowUsernameSearch, false, false, this.allowBots);
        this.listViewAdapter = new OnlineContactsAdapter(paramContext, 1, false, null, false);
        this.fragmentView = new FrameLayout(paramContext);
        LinearLayout localLinearLayout = new LinearLayout(paramContext);
        localLinearLayout.setVisibility(4);
        localLinearLayout.setOrientation(1);
        ((FrameLayout)this.fragmentView).addView(localLinearLayout);
        Object localObject = (FrameLayout.LayoutParams)localLinearLayout.getLayoutParams();
        ((FrameLayout.LayoutParams)localObject).width = -1;
        ((FrameLayout.LayoutParams)localObject).height = -1;
        ((FrameLayout.LayoutParams)localObject).gravity = 48;
        localLinearLayout.setLayoutParams((ViewGroup.LayoutParams)localObject);
        localLinearLayout.setOnTouchListener(new View.OnTouchListener()
        {
          public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
          {
            return true;
          }
        });
        this.emptyTextView = new TextView(paramContext);
        this.emptyTextView.setTypeface(MihanTheme.getMihanTypeFace());
        this.emptyTextView.setTextColor(-8355712);
        this.emptyTextView.setTextSize(1, 20.0F);
        this.emptyTextView.setGravity(17);
        this.emptyTextView.setText(LocaleController.getString("NoContacts", 2131166004));
        localLinearLayout.addView(this.emptyTextView);
        localObject = (LinearLayout.LayoutParams)this.emptyTextView.getLayoutParams();
        ((LinearLayout.LayoutParams)localObject).width = -1;
        ((LinearLayout.LayoutParams)localObject).height = -1;
        ((LinearLayout.LayoutParams)localObject).weight = 0.5F;
        this.emptyTextView.setLayoutParams((ViewGroup.LayoutParams)localObject);
        localObject = new FrameLayout(paramContext);
        localLinearLayout.addView((View)localObject);
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)((FrameLayout)localObject).getLayoutParams();
        localLayoutParams.width = -1;
        localLayoutParams.height = -1;
        localLayoutParams.weight = 0.5F;
        ((FrameLayout)localObject).setLayoutParams(localLayoutParams);
        this.listView = new LetterSectionsListView(paramContext);
        this.listView.setEmptyView(localLinearLayout);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setFastScrollEnabled(true);
        this.listView.setScrollBarStyle(33554432);
        this.listView.setAdapter(this.listViewAdapter);
        this.listView.setFastScrollAlwaysVisible(true);
        paramContext = this.listView;
        if (!LocaleController.isRTL) {
          break label647;
        }
      }
    }
    label647:
    for (int i = 1;; i = 2)
    {
      paramContext.setVerticalScrollbarPosition(i);
      ((FrameLayout)this.fragmentView).addView(this.listView);
      paramContext = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
      paramContext.width = -1;
      paramContext.height = -1;
      this.listView.setLayoutParams(paramContext);
      this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(final AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          int i = OnlineContactsActivity.this.listViewAdapter.getSectionForPosition(paramAnonymousInt);
          paramAnonymousInt = OnlineContactsActivity.this.listViewAdapter.getPositionInSectionForPosition(paramAnonymousInt);
          paramAnonymousAdapterView = OnlineContactsActivity.this.listViewAdapter.getItem(i, paramAnonymousInt);
          if ((paramAnonymousAdapterView instanceof TLRPC.User))
          {
            paramAnonymousAdapterView = (TLRPC.User)paramAnonymousAdapterView;
            if (OnlineContactsActivity.this.returnAsResult) {
              if ((OnlineContactsActivity.this.ignoreUsers == null) || (!OnlineContactsActivity.this.ignoreUsers.containsKey(Integer.valueOf(paramAnonymousAdapterView.id)))) {}
            }
          }
          do
          {
            do
            {
              do
              {
                return;
                OnlineContactsActivity.this.didSelectResult(paramAnonymousAdapterView, true, null);
                return;
                if (OnlineContactsActivity.this.createSecretChat)
                {
                  OnlineContactsActivity.access$502(OnlineContactsActivity.this, true);
                  SecretChatHelper.getInstance().startSecretChat(OnlineContactsActivity.this.getParentActivity(), paramAnonymousAdapterView);
                  return;
                }
                paramAnonymousView = new Bundle();
                paramAnonymousView.putInt("user_id", paramAnonymousAdapterView.id);
              } while ((!MessagesController.checkCanOpenChat(paramAnonymousView, OnlineContactsActivity.this)) || (ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).contains("hide_" + String.valueOf(paramAnonymousAdapterView.id))));
              OnlineContactsActivity.this.presentFragment(new ChatActivity(paramAnonymousView), true);
              return;
            } while (!(paramAnonymousAdapterView instanceof ContactsController.Contact));
            paramAnonymousView = (ContactsController.Contact)paramAnonymousAdapterView;
            paramAnonymousAdapterView = null;
            if (!paramAnonymousView.phones.isEmpty()) {
              paramAnonymousAdapterView = (String)paramAnonymousView.phones.get(0);
            }
          } while ((paramAnonymousAdapterView == null) || (OnlineContactsActivity.this.getParentActivity() == null));
          paramAnonymousView = new AlertDialog.Builder(OnlineContactsActivity.this.getParentActivity());
          paramAnonymousView.setMessage(LocaleController.getString("InviteUser", 2131165833));
          paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165338));
          paramAnonymousView.setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              try
              {
                paramAnonymous2DialogInterface = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", paramAnonymousAdapterView, null));
                paramAnonymous2DialogInterface.putExtra("sms_body", LocaleController.getString("InviteText", 2131165829));
                OnlineContactsActivity.this.getParentActivity().startActivityForResult(paramAnonymous2DialogInterface, 500);
                return;
              }
              catch (Exception paramAnonymous2DialogInterface)
              {
                FileLog.e("tmessages", paramAnonymous2DialogInterface);
              }
            }
          });
          paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
          OnlineContactsActivity.this.showDialog(paramAnonymousView.create());
        }
      });
      this.listView.setOnScrollListener(new AbsListView.OnScrollListener()
      {
        public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
        {
          if (paramAnonymousAbsListView.isFastScrollEnabled()) {
            AndroidUtilities.clearDrawableAnimation(paramAnonymousAbsListView);
          }
        }
        
        public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt)
        {
          if ((paramAnonymousInt == 1) && (OnlineContactsActivity.this.searching) && (OnlineContactsActivity.this.searchWas)) {
            AndroidUtilities.hideKeyboard(OnlineContactsActivity.this.getParentActivity().getCurrentFocus());
          }
        }
      });
      updateColors(localActionBarMenu);
      return this.fragmentView;
      if (this.createSecretChat)
      {
        this.actionBar.setTitle(LocaleController.getString("NewSecretChat", 2131165997));
        break;
      }
      this.actionBar.setTitle(LocaleController.getString("NewMessageTitle", 2131165990));
      break;
      this.actionBar.setTitle(LocaleController.getString("OnlineContacs", 2131166114));
      break;
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.contactsDidLoaded) {
      if (this.listViewAdapter != null) {
        this.listViewAdapter.notifyDataSetChanged();
      }
    }
    do
    {
      do
      {
        do
        {
          return;
          if (paramInt != NotificationCenter.updateInterfaces) {
            break;
          }
          paramInt = ((Integer)paramVarArgs[0]).intValue();
        } while (((paramInt & 0x2) == 0) && ((paramInt & 0x1) == 0) && ((paramInt & 0x4) == 0));
        updateVisibleRows(paramInt);
        return;
        if (paramInt != NotificationCenter.encryptedChatCreated) {
          break;
        }
      } while ((!this.createSecretChat) || (!this.creatingChat));
      paramVarArgs = (TLRPC.EncryptedChat)paramVarArgs[0];
      Bundle localBundle = new Bundle();
      localBundle.putInt("enc_id", paramVarArgs.id);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
      presentFragment(new ChatActivity(localBundle), true);
      return;
    } while ((paramInt != NotificationCenter.closeChats) || (this.creatingChat));
    removeSelfFromStack();
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatCreated);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
    if (this.arguments != null)
    {
      this.onlyUsers = getArguments().getBoolean("onlyUsers", false);
      this.destroyAfterSelect = this.arguments.getBoolean("destroyAfterSelect", false);
      this.returnAsResult = this.arguments.getBoolean("returnAsResult", false);
      this.createSecretChat = this.arguments.getBoolean("createSecretChat", false);
      this.selectAlertString = this.arguments.getString("selectAlertString");
      this.allowUsernameSearch = this.arguments.getBoolean("allowUsernameSearch", true);
      this.needForwardCount = this.arguments.getBoolean("needForwardCount", true);
      this.allowBots = this.arguments.getBoolean("allowBots", true);
      this.chat_id = this.arguments.getInt("chat_id", 0);
    }
    for (;;)
    {
      ContactsController.getInstance().checkInviteText();
      return true;
      this.needPhonebook = true;
    }
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatCreated);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    this.delegate = null;
  }
  
  public void onPause()
  {
    super.onPause();
    if (this.actionBar != null) {
      this.actionBar.closeSearchField();
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null) {
      this.listViewAdapter.notifyDataSetChanged();
    }
  }
  
  public void setDelegate(ContactsActivityDelegate paramContactsActivityDelegate)
  {
    this.delegate = paramContactsActivityDelegate;
  }
  
  public void setIgnoreUsers(HashMap<Integer, TLRPC.User> paramHashMap)
  {
    this.ignoreUsers = paramHashMap;
  }
  
  public static abstract interface ContactsActivityDelegate
  {
    public abstract void didSelectContact(TLRPC.User paramUser, String paramString);
  }
  
  class OnlineContactsAdapter
    extends BaseSectionsAdapter
  {
    private HashMap<Integer, ?> checkedMap;
    private HashMap<Integer, TLRPC.User> ignoreUsers;
    private boolean isAdmin;
    private Context mContext;
    private boolean needPhonebook;
    private int onlyUsers;
    private boolean scrolling;
    
    public OnlineContactsAdapter(int paramInt, boolean paramBoolean1, HashMap<Integer, TLRPC.User> paramHashMap, boolean paramBoolean2)
    {
      this.mContext = paramInt;
      this.onlyUsers = paramBoolean1;
      this.needPhonebook = paramHashMap;
      this.ignoreUsers = paramBoolean2;
      boolean bool;
      this.isAdmin = bool;
    }
    
    public int getCountForSection(int paramInt)
    {
      int i = 2;
      HashMap localHashMap;
      ArrayList localArrayList;
      if (this.onlyUsers == 2)
      {
        localHashMap = ContactsController.getInstance().onlineUsersMutualSectionsDict;
        if (this.onlyUsers != 2) {
          break label109;
        }
        localArrayList = ContactsController.getInstance().onlineSortedUsersMutualSectionsArray;
        label33:
        if ((this.onlyUsers == 0) || (this.isAdmin)) {
          break label120;
        }
        if (paramInt >= localArrayList.size()) {
          break label200;
        }
        i = ((ArrayList)localHashMap.get(localArrayList.get(paramInt))).size();
        if (paramInt == localArrayList.size() - 1)
        {
          paramInt = i;
          if (!this.needPhonebook) {}
        }
        else
        {
          paramInt = i + 1;
        }
      }
      label109:
      label120:
      label144:
      do
      {
        do
        {
          do
          {
            return paramInt;
            localHashMap = ContactsController.getInstance().onlineUsersSectionsDict;
            break;
            localArrayList = ContactsController.getInstance().onlineSortedUsersSectionsArray;
            break label33;
            if (paramInt != 0) {
              break label144;
            }
            paramInt = i;
          } while (this.needPhonebook);
          paramInt = i;
        } while (this.isAdmin);
        return 4;
        if (paramInt - 1 >= localArrayList.size()) {
          break label200;
        }
        i = ((ArrayList)localHashMap.get(localArrayList.get(paramInt - 1))).size();
        if (paramInt - 1 != localArrayList.size() - 1) {
          break label196;
        }
        paramInt = i;
      } while (!this.needPhonebook);
      label196:
      return i + 1;
      label200:
      if (this.needPhonebook) {
        return ContactsController.getInstance().phoneBookContacts.size();
      }
      return 0;
    }
    
    public Object getItem(int paramInt1, int paramInt2)
    {
      Object localObject3 = null;
      Object localObject1;
      ArrayList localArrayList;
      label34:
      Object localObject2;
      if (this.onlyUsers == 2)
      {
        localObject1 = ContactsController.getInstance().onlineUsersMutualSectionsDict;
        if (this.onlyUsers != 2) {
          break label122;
        }
        localArrayList = ContactsController.getInstance().onlineSortedUsersMutualSectionsArray;
        if ((this.onlyUsers == 0) || (this.isAdmin)) {
          break label133;
        }
        localObject2 = localObject3;
        if (paramInt1 < localArrayList.size())
        {
          localObject1 = (ArrayList)((HashMap)localObject1).get(localArrayList.get(paramInt1));
          localObject2 = localObject3;
          if (paramInt2 < ((ArrayList)localObject1).size()) {
            localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)((ArrayList)localObject1).get(paramInt2)).user_id));
          }
        }
      }
      label122:
      label133:
      do
      {
        do
        {
          do
          {
            return localObject2;
            localObject1 = ContactsController.getInstance().onlineUsersSectionsDict;
            break;
            localArrayList = ContactsController.getInstance().onlineSortedUsersSectionsArray;
            break label34;
            localObject2 = localObject3;
          } while (paramInt1 == 0);
          localObject2 = localObject3;
        } while (paramInt1 - 1 >= localArrayList.size());
        localObject1 = (ArrayList)((HashMap)localObject1).get(localArrayList.get(paramInt1 - 1));
        localObject2 = localObject3;
      } while (paramInt2 >= ((ArrayList)localObject1).size());
      return MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)((ArrayList)localObject1).get(paramInt2)).user_id));
    }
    
    public View getItemView(int paramInt1, int paramInt2, View paramView, ViewGroup paramViewGroup)
    {
      int i = getItemViewType(paramInt1, paramInt2);
      Object localObject;
      float f;
      if (i == 4)
      {
        localObject = paramView;
        if (paramView == null)
        {
          localObject = new DividerCell(this.mContext);
          if (!LocaleController.isRTL) {
            break label76;
          }
          f = 28.0F;
          paramInt1 = AndroidUtilities.dp(f);
          if (!LocaleController.isRTL) {
            break label83;
          }
          f = 72.0F;
          label60:
          ((View)localObject).setPadding(paramInt1, 0, AndroidUtilities.dp(f), 0);
        }
      }
      label76:
      label83:
      do
      {
        return (View)localObject;
        f = 72.0F;
        break;
        f = 28.0F;
        break label60;
        if (i == 3)
        {
          paramViewGroup = paramView;
          if (paramView == null)
          {
            paramViewGroup = new GreySectionCell(this.mContext);
            ((GreySectionCell)paramViewGroup).setText(LocaleController.getString("Contacts", 2131165563).toUpperCase());
          }
          paramView = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
          paramInt1 = paramView.getInt("theme_contact_list_tbgcolor", MihanTheme.getTabsBackgroundColor(paramView));
          paramInt2 = paramView.getInt("theme_contact_list_tcolor", MihanTheme.getDialogNameColor(paramView));
          ((GreySectionCell)paramViewGroup).setTextAndBGColor(paramInt2, paramInt1);
          return paramViewGroup;
        }
        if (i == 2)
        {
          paramViewGroup = paramView;
          if (paramView == null) {
            paramViewGroup = new TextCell(this.mContext);
          }
          paramView = (TextCell)paramViewGroup;
          paramView.setTextAndIcon(LocaleController.getString("InviteFriends", 2131165827), 2130837969);
          localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
          paramInt1 = ((SharedPreferences)localObject).getInt("theme_contact_list_ncolor", MihanTheme.getDialogNameColor((SharedPreferences)localObject));
          paramView.setTextColor(paramInt1);
          paramView.setIconColor(paramInt1);
          return paramViewGroup;
        }
        if (i == 1)
        {
          paramViewGroup = paramView;
          if (paramView == null) {
            paramViewGroup = new TextCell(this.mContext);
          }
          paramView = (ContactsController.Contact)ContactsController.getInstance().phoneBookContacts.get(paramInt2);
          localObject = (TextCell)paramViewGroup;
          if ((paramView.first_name != null) && (paramView.last_name != null)) {
            ((TextCell)localObject).setText(paramView.first_name + " " + paramView.last_name);
          }
          for (;;)
          {
            paramViewGroup.setVisibility(8);
            return paramViewGroup;
            if ((paramView.first_name != null) && (paramView.last_name == null)) {
              ((TextCell)localObject).setText(paramView.first_name);
            } else {
              ((TextCell)localObject).setText(paramView.last_name);
            }
          }
        }
        localObject = paramView;
      } while (i != 0);
      paramViewGroup = paramView;
      if (paramView == null)
      {
        paramViewGroup = new UserCell(this.mContext, 58, 1, false);
        paramView = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
        i = paramView.getInt("theme_contact_list_ncolor", MihanTheme.getDialogNameColor(paramView));
        int j = paramView.getInt("theme_contact_list_scolor", MihanTheme.getDialogMessageColor(paramView));
        int k = paramView.getInt("theme_contact_list_oscolor", MihanTheme.getDialogMessageColor(paramView));
        ((UserCell)paramViewGroup).setStatusColors(j, k);
        ((UserCell)paramViewGroup).setNameTextColor(i);
      }
      label531:
      label547:
      label564:
      boolean bool2;
      if (this.onlyUsers == 2)
      {
        paramView = ContactsController.getInstance().onlineUsersMutualSectionsDict;
        if (this.onlyUsers != 2) {
          break label712;
        }
        localObject = ContactsController.getInstance().onlineSortedUsersMutualSectionsArray;
        if ((this.onlyUsers == 0) || (this.isAdmin)) {
          break label723;
        }
        i = 0;
        paramView = (ArrayList)paramView.get(((ArrayList)localObject).get(paramInt1 - i));
        paramView = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)paramView.get(paramInt2)).user_id));
        ((UserCell)paramViewGroup).setData(paramView, null, null, 0);
        if (this.checkedMap != null)
        {
          localObject = (UserCell)paramViewGroup;
          bool2 = this.checkedMap.containsKey(Integer.valueOf(paramView.id));
          if (this.scrolling) {
            break label729;
          }
        }
      }
      label712:
      label723:
      label729:
      for (boolean bool1 = true;; bool1 = false)
      {
        ((UserCell)localObject).setChecked(bool2, bool1);
        localObject = paramViewGroup;
        if (this.ignoreUsers == null) {
          break;
        }
        if (!this.ignoreUsers.containsKey(Integer.valueOf(paramView.id))) {
          break label735;
        }
        paramViewGroup.setAlpha(0.5F);
        return paramViewGroup;
        paramView = ContactsController.getInstance().onlineUsersSectionsDict;
        break label531;
        localObject = ContactsController.getInstance().onlineSortedUsersSectionsArray;
        break label547;
        i = 1;
        break label564;
      }
      label735:
      paramViewGroup.setAlpha(1.0F);
      return paramViewGroup;
    }
    
    public int getItemViewType(int paramInt1, int paramInt2)
    {
      HashMap localHashMap;
      ArrayList localArrayList;
      if (this.onlyUsers == 2)
      {
        localHashMap = ContactsController.getInstance().onlineUsersMutualSectionsDict;
        if (this.onlyUsers != 2) {
          break label77;
        }
        localArrayList = ContactsController.getInstance().onlineSortedUsersMutualSectionsArray;
        label31:
        if ((this.onlyUsers == 0) || (this.isAdmin)) {
          break label90;
        }
        if (paramInt2 >= ((ArrayList)localHashMap.get(localArrayList.get(paramInt1))).size()) {
          break label88;
        }
      }
      label77:
      label88:
      label90:
      do
      {
        return 0;
        localHashMap = ContactsController.getInstance().onlineUsersSectionsDict;
        break;
        localArrayList = ContactsController.getInstance().onlineSortedUsersSectionsArray;
        break label31;
        return 4;
        if (paramInt1 == 0)
        {
          if ((this.needPhonebook) || (this.isAdmin))
          {
            if (paramInt2 == 1) {
              return 3;
            }
          }
          else if (paramInt2 == 3) {
            return 3;
          }
          return 2;
        }
      } while ((paramInt1 - 1 >= localArrayList.size()) || (paramInt2 < ((ArrayList)localHashMap.get(localArrayList.get(paramInt1 - 1))).size()));
      return 4;
    }
    
    public int getSectionCount()
    {
      if (this.onlyUsers == 2) {}
      for (ArrayList localArrayList = ContactsController.getInstance().onlineSortedUsersMutualSectionsArray;; localArrayList = ContactsController.getInstance().onlineSortedUsersSectionsArray)
      {
        int j = localArrayList.size();
        int i = j;
        if (this.onlyUsers == 0) {
          i = j + 1;
        }
        return i;
      }
    }
    
    public View getSectionHeaderView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (this.onlyUsers == 2)
      {
        paramViewGroup = ContactsController.getInstance().onlineUsersMutualSectionsDict;
        if (this.onlyUsers != 2) {
          break label101;
        }
      }
      Object localObject;
      label101:
      for (paramViewGroup = ContactsController.getInstance().onlineSortedUsersMutualSectionsArray;; paramViewGroup = ContactsController.getInstance().onlineSortedUsersSectionsArray)
      {
        localObject = paramView;
        if (paramView == null) {
          localObject = new LetterSectionCell(this.mContext);
        }
        if ((this.onlyUsers == 0) || (this.isAdmin)) {
          break label125;
        }
        if (paramInt >= paramViewGroup.size()) {
          break label111;
        }
        ((LetterSectionCell)localObject).setLetter((String)paramViewGroup.get(paramInt));
        return (View)localObject;
        paramViewGroup = ContactsController.getInstance().onlineUsersSectionsDict;
        break;
      }
      label111:
      ((LetterSectionCell)localObject).setLetter("");
      return (View)localObject;
      label125:
      if (paramInt == 0)
      {
        ((LetterSectionCell)localObject).setLetter("");
        return (View)localObject;
      }
      if (paramInt - 1 < paramViewGroup.size())
      {
        ((LetterSectionCell)localObject).setLetter((String)paramViewGroup.get(paramInt - 1));
        return (View)localObject;
      }
      ((LetterSectionCell)localObject).setLetter("");
      return (View)localObject;
    }
    
    public int getViewTypeCount()
    {
      return 5;
    }
    
    public boolean isRowEnabled(int paramInt1, int paramInt2)
    {
      HashMap localHashMap;
      ArrayList localArrayList;
      if (this.onlyUsers == 2)
      {
        localHashMap = ContactsController.getInstance().onlineUsersMutualSectionsDict;
        if (this.onlyUsers != 2) {
          break label77;
        }
        localArrayList = ContactsController.getInstance().onlineSortedUsersMutualSectionsArray;
        label31:
        if ((this.onlyUsers == 0) || (this.isAdmin)) {
          break label90;
        }
        if (paramInt2 >= ((ArrayList)localHashMap.get(localArrayList.get(paramInt1))).size()) {
          break label88;
        }
      }
      label77:
      label88:
      label90:
      while ((paramInt1 == 0) || (paramInt1 - 1 >= localArrayList.size()) || (paramInt2 < ((ArrayList)localHashMap.get(localArrayList.get(paramInt1 - 1))).size()))
      {
        return true;
        localHashMap = ContactsController.getInstance().onlineUsersSectionsDict;
        break;
        localArrayList = ContactsController.getInstance().onlineSortedUsersSectionsArray;
        break label31;
        return false;
      }
      return false;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\OnlineContactsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */