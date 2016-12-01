package org.telegram.ui.Supergram;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.DialogsActivity;

public class SetPasswordActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int change_Pass = 3;
  private static final int disable_hidden = 2;
  private static final int done_button = 1;
  private static final int enable_hidden = 1;
  private static final int unlock_dialogs = 4;
  private int changeenablePasscodeRow;
  private int enablePasscodeRow;
  private String firstPassword;
  private int hiddenInShareAlertDesRow;
  private int hiddenInShareAlertRow;
  private ListAdapter listAdapter;
  private ListView listView;
  private int passcodeDetailRow;
  private int passcodeSetStep = 0;
  private EditText passwordEditText;
  private int rowCount;
  private int showHiddenNotifDesRow;
  private int showHiddenNotifRow;
  private TextView titleTextView;
  private int type;
  
  public SetPasswordActivity(int paramInt)
  {
    this.type = paramInt;
  }
  
  private void onPasscodeError()
  {
    if (getParentActivity() == null) {
      return;
    }
    Vibrator localVibrator = (Vibrator)getParentActivity().getSystemService("vibrator");
    if (localVibrator != null) {
      localVibrator.vibrate(200L);
    }
    AndroidUtilities.shakeView(this.titleTextView, 2.0F, 0);
  }
  
  private void processDone()
  {
    boolean bool1 = false;
    if (this.passwordEditText.getText().length() == 0) {
      onPasscodeError();
    }
    do
    {
      return;
      if (this.type == 1)
      {
        if (!this.firstPassword.equals(this.passwordEditText.getText().toString())) {
          try
          {
            Toast.makeText(getParentActivity(), LocaleController.getString("PasscodeDoNotMatch", 2131166147), 0).show();
            AndroidUtilities.shakeView(this.titleTextView, 2.0F, 0);
            this.passwordEditText.setText("");
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
        localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).edit();
        ((SharedPreferences.Editor)localObject).putString("chat_password", this.firstPassword);
        ((SharedPreferences.Editor)localObject).putBoolean("chat_unlocked", true);
        ((SharedPreferences.Editor)localObject).commit();
        restartApp();
        this.passwordEditText.clearFocus();
        AndroidUtilities.hideKeyboard(this.passwordEditText);
        return;
      }
      if (this.type == 2)
      {
        localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
        if (!((SharedPreferences)localObject).getString("chat_password", "").equals(this.passwordEditText.getText().toString()))
        {
          this.passwordEditText.setText("");
          onPasscodeError();
          return;
        }
        localObject = ((SharedPreferences)localObject).edit();
        ((SharedPreferences.Editor)localObject).putString("chat_password", "");
        ((SharedPreferences.Editor)localObject).commit();
        this.passwordEditText.clearFocus();
        AndroidUtilities.hideKeyboard(this.passwordEditText);
        restartApp();
        return;
      }
      if (this.type == 3)
      {
        if (!ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getString("chat_password", "").equals(this.passwordEditText.getText().toString()))
        {
          this.passwordEditText.setText("");
          onPasscodeError();
          return;
        }
        this.passwordEditText.clearFocus();
        AndroidUtilities.hideKeyboard(this.passwordEditText);
        presentFragment(new SetPasswordActivity(1), true);
        return;
      }
    } while (this.type != 4);
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
    if (!((SharedPreferences)localObject).getString("chat_password", "").equals(this.passwordEditText.getText().toString()))
    {
      this.passwordEditText.setText("");
      onPasscodeError();
      return;
    }
    boolean bool2 = ((SharedPreferences)localObject).getBoolean("chat_unlocked", false);
    localObject = ((SharedPreferences)localObject).edit();
    if (!bool2) {
      bool1 = true;
    }
    ((SharedPreferences.Editor)localObject).putBoolean("chat_unlocked", bool1);
    ((SharedPreferences.Editor)localObject).commit();
    this.passwordEditText.clearFocus();
    AndroidUtilities.hideKeyboard(this.passwordEditText);
    presentFragment(new DialogsActivity(null), true);
  }
  
  private void processNext()
  {
    if (this.passwordEditText.getText().length() == 0)
    {
      onPasscodeError();
      return;
    }
    this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", 2131166204));
    this.firstPassword = this.passwordEditText.getText().toString();
    this.passwordEditText.setText("");
    this.passcodeSetStep = 1;
  }
  
  private void restartApp()
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
  
  private void updateRows()
  {
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.enablePasscodeRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.changeenablePasscodeRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.passcodeDetailRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.hiddenInShareAlertRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.hiddenInShareAlertDesRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.showHiddenNotifRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.showHiddenNotifDesRow = i;
  }
  
  public View createView(Context paramContext)
  {
    if (this.type != 5) {
      this.actionBar.setBackButtonImage(2130837810);
    }
    this.actionBar.setAllowOverlayTitle(false);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          if (SetPasswordActivity.this.type == 4) {
            SetPasswordActivity.this.presentFragment(new DialogsActivity(null), true);
          }
        }
        do
        {
          do
          {
            return;
            SetPasswordActivity.this.finishFragment();
            return;
          } while (paramAnonymousInt != 1);
          if (SetPasswordActivity.this.passcodeSetStep == 0)
          {
            SetPasswordActivity.this.processNext();
            return;
          }
        } while (SetPasswordActivity.this.passcodeSetStep != 1);
        SetPasswordActivity.this.processDone();
      }
    });
    this.fragmentView = new FrameLayout(paramContext);
    Object localObject = (FrameLayout)this.fragmentView;
    if (this.type != 0)
    {
      this.actionBar.createMenu().addItemWithWidth(1, 2130837844, AndroidUtilities.dp(56.0F));
      this.titleTextView = new TextView(paramContext);
      this.titleTextView.setTextColor(-9079435);
      if (this.type == 1) {
        if (ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getString("chat_password", "").length() != 0)
        {
          this.titleTextView.setText(LocaleController.getString("EnterNewPasscode", 2131165670));
          this.titleTextView.setTextSize(1, 18.0F);
          this.titleTextView.setGravity(1);
          this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          ((FrameLayout)localObject).addView(this.titleTextView);
          FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.titleTextView.getLayoutParams();
          localLayoutParams.width = -2;
          localLayoutParams.height = -2;
          localLayoutParams.gravity = 1;
          localLayoutParams.topMargin = AndroidUtilities.dp(38.0F);
          this.titleTextView.setLayoutParams(localLayoutParams);
          this.passwordEditText = new EditText(paramContext);
          this.passwordEditText.setTextSize(1, 20.0F);
          this.passwordEditText.setTextColor(-16777216);
          this.passwordEditText.setMaxLines(1);
          this.passwordEditText.setLines(1);
          this.passwordEditText.setInputType(2);
          this.passwordEditText.setGravity(1);
          this.passwordEditText.setSingleLine(true);
          if (this.type != 1) {
            break label572;
          }
          this.passcodeSetStep = 0;
          this.passwordEditText.setImeOptions(5);
          label339:
          this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
          this.passwordEditText.setTypeface(Typeface.DEFAULT);
          AndroidUtilities.clearCursorDrawable(this.passwordEditText);
          ((FrameLayout)localObject).addView(this.passwordEditText);
          paramContext = (FrameLayout.LayoutParams)this.passwordEditText.getLayoutParams();
          paramContext.topMargin = AndroidUtilities.dp(90.0F);
          paramContext.height = AndroidUtilities.dp(36.0F);
          paramContext.leftMargin = AndroidUtilities.dp(40.0F);
          paramContext.gravity = 51;
          paramContext.rightMargin = AndroidUtilities.dp(40.0F);
          paramContext.width = -1;
          this.passwordEditText.setLayoutParams(paramContext);
          this.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
          {
            public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
            {
              if (SetPasswordActivity.this.passcodeSetStep == 0)
              {
                SetPasswordActivity.this.processNext();
                return true;
              }
              if (SetPasswordActivity.this.passcodeSetStep == 1)
              {
                SetPasswordActivity.this.processDone();
                return true;
              }
              return false;
            }
          });
          this.passwordEditText.addTextChangedListener(new TextWatcher()
          {
            public void afterTextChanged(Editable paramAnonymousEditable)
            {
              if (SetPasswordActivity.this.passwordEditText.length() == 4)
              {
                if (SetPasswordActivity.this.type != 1) {
                  break label62;
                }
                if (SetPasswordActivity.this.passcodeSetStep != 0) {
                  break label43;
                }
                SetPasswordActivity.this.processNext();
              }
              label43:
              while (SetPasswordActivity.this.passcodeSetStep != 1) {
                return;
              }
              SetPasswordActivity.this.processDone();
              return;
              label62:
              SetPasswordActivity.this.processDone();
            }
            
            public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
            
            public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
          });
          if (Build.VERSION.SDK_INT >= 11) {
            break label589;
          }
          this.passwordEditText.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener()
          {
            public void onCreateContextMenu(ContextMenu paramAnonymousContextMenu, View paramAnonymousView, ContextMenu.ContextMenuInfo paramAnonymousContextMenuInfo)
            {
              paramAnonymousContextMenu.clear();
            }
          });
          label497:
          if ((this.type != 3) && (this.type != 4)) {
            break label607;
          }
          this.actionBar.setTitle(LocaleController.getString("Authentication", 2131165381));
        }
      }
    }
    for (;;)
    {
      return this.fragmentView;
      this.titleTextView.setText(LocaleController.getString("EnterNewFirstPasscode", 2131165669));
      break;
      this.titleTextView.setText(LocaleController.getString("EnterCurrentPasscode", 2131165666));
      break;
      label572:
      this.passcodeSetStep = 1;
      this.passwordEditText.setImeOptions(6);
      break label339;
      label589:
      this.passwordEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback()
      {
        public boolean onActionItemClicked(ActionMode paramAnonymousActionMode, MenuItem paramAnonymousMenuItem)
        {
          return false;
        }
        
        public boolean onCreateActionMode(ActionMode paramAnonymousActionMode, Menu paramAnonymousMenu)
        {
          return false;
        }
        
        public void onDestroyActionMode(ActionMode paramAnonymousActionMode) {}
        
        public boolean onPrepareActionMode(ActionMode paramAnonymousActionMode, Menu paramAnonymousMenu)
        {
          return false;
        }
      });
      break label497;
      label607:
      this.actionBar.setTitle(LocaleController.getString("SetPass", 2131166314));
      continue;
      this.actionBar.setTitle(LocaleController.getString("HideChats", 2131165796));
      ((FrameLayout)localObject).setBackgroundColor(-986896);
      this.listView = new ListView(paramContext);
      this.listView.setDivider(null);
      this.listView.setDividerHeight(0);
      this.listView.setVerticalScrollBarEnabled(false);
      this.listView.setDrawSelectorOnTop(true);
      ((FrameLayout)localObject).addView(this.listView);
      localObject = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).width = -1;
      ((FrameLayout.LayoutParams)localObject).height = -1;
      ((FrameLayout.LayoutParams)localObject).gravity = 48;
      this.listView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      localObject = this.listView;
      paramContext = new ListAdapter(paramContext);
      this.listAdapter = paramContext;
      ((ListView)localObject).setAdapter(paramContext);
      this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          boolean bool3 = true;
          boolean bool2 = true;
          if (paramAnonymousInt == SetPasswordActivity.this.changeenablePasscodeRow)
          {
            SetPasswordActivity.this.presentFragment(new SetPasswordActivity(3));
            SetPasswordActivity.this.finishFragment();
          }
          label229:
          do
          {
            do
            {
              do
              {
                return;
                if (paramAnonymousInt == SetPasswordActivity.this.enablePasscodeRow)
                {
                  paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                  if (ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getString("chat_password", "").length() != 0) {
                    SetPasswordActivity.this.presentFragment(new SetPasswordActivity(2));
                  }
                  for (;;)
                  {
                    SetPasswordActivity.this.finishFragment();
                    return;
                    SetPasswordActivity.this.presentFragment(new SetPasswordActivity(1));
                  }
                }
                if (paramAnonymousInt != SetPasswordActivity.this.hiddenInShareAlertRow) {
                  break label229;
                }
                paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                bool3 = paramAnonymousAdapterView.getBoolean("hidden_sharealert", true);
                paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
                if (bool3) {
                  break;
                }
                bool1 = true;
                paramAnonymousAdapterView.putBoolean("hidden_sharealert", bool1);
                paramAnonymousAdapterView.commit();
              } while (!(paramAnonymousView instanceof TextCheckCell));
              paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
              if (!bool3) {}
              for (bool1 = bool2;; bool1 = false)
              {
                paramAnonymousAdapterView.setChecked(bool1);
                return;
                bool1 = false;
                break;
              }
            } while (paramAnonymousInt != SetPasswordActivity.this.showHiddenNotifRow);
            paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
            bool2 = paramAnonymousAdapterView.getBoolean("show_notification", true);
            paramAnonymousAdapterView = paramAnonymousAdapterView.edit();
            if (bool2) {
              break;
            }
            bool1 = true;
            paramAnonymousAdapterView.putBoolean("show_notification", bool1);
            paramAnonymousAdapterView.commit();
          } while (!(paramAnonymousView instanceof TextCheckCell));
          paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
          if (!bool2) {}
          for (boolean bool1 = bool3;; bool1 = false)
          {
            paramAnonymousAdapterView.setChecked(bool1);
            return;
            bool1 = false;
            break;
          }
        }
      });
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if ((paramInt == NotificationCenter.didSetPasscode) && (this.type == 0))
    {
      updateRows();
      if (this.listAdapter != null) {
        this.listAdapter.notifyDataSetChanged();
      }
    }
  }
  
  public boolean onBackPressed()
  {
    if (this.type == 4) {
      presentFragment(new DialogsActivity(null), true);
    }
    return true;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (this.listView != null) {
      this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          SetPasswordActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
          return true;
        }
      });
    }
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    updateRows();
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
    if (this.type != 0) {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (SetPasswordActivity.this.passwordEditText != null)
          {
            SetPasswordActivity.this.passwordEditText.requestFocus();
            AndroidUtilities.showKeyboard(SetPasswordActivity.this.passwordEditText);
          }
        }
      }, 200L);
    }
  }
  
  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (this.type != 0)) {
      AndroidUtilities.showKeyboard(this.passwordEditText);
    }
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
      return SetPasswordActivity.this.rowCount;
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
      if ((paramInt == SetPasswordActivity.this.enablePasscodeRow) || (paramInt == SetPasswordActivity.this.hiddenInShareAlertRow) || (paramInt == SetPasswordActivity.this.showHiddenNotifRow)) {}
      do
      {
        return 0;
        if (paramInt == SetPasswordActivity.this.changeenablePasscodeRow) {
          return 1;
        }
      } while ((paramInt != SetPasswordActivity.this.passcodeDetailRow) && (paramInt != SetPasswordActivity.this.hiddenInShareAlertDesRow) && (paramInt != SetPasswordActivity.this.showHiddenNotifDesRow));
      return 2;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      boolean bool = false;
      int i = getItemViewType(paramInt);
      Object localObject;
      if (i == 0)
      {
        localObject = paramView;
        if (paramView == null)
        {
          localObject = new TextCheckCell(this.mContext);
          ((View)localObject).setBackgroundColor(-1);
        }
        paramView = (TextCheckCell)localObject;
        if (paramInt == SetPasswordActivity.this.enablePasscodeRow)
        {
          paramViewGroup = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getString("chat_password", "");
          String str = LocaleController.getString("EnablePass", 2131165646);
          if (paramViewGroup.length() > 0) {
            bool = true;
          }
          paramView.setTextAndCheck(str, bool, true);
          paramViewGroup = (ViewGroup)localObject;
        }
      }
      do
      {
        do
        {
          do
          {
            do
            {
              return paramViewGroup;
              if (paramInt == SetPasswordActivity.this.hiddenInShareAlertRow)
              {
                paramViewGroup = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
                paramView.setTextAndCheck(LocaleController.getString("HiddenInShareAlert", 2131166641), paramViewGroup.getBoolean("hidden_sharealert", true), true);
                return (View)localObject;
              }
              paramViewGroup = (ViewGroup)localObject;
            } while (paramInt != SetPasswordActivity.this.showHiddenNotifRow);
            paramViewGroup = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
            paramView.setTextAndCheck(LocaleController.getString("HiddenShowNotif", 2131166643), paramViewGroup.getBoolean("show_notification", true), true);
            return (View)localObject;
            if (i != 1) {
              break;
            }
            localObject = paramView;
            if (paramView == null)
            {
              localObject = new TextSettingsCell(this.mContext);
              ((View)localObject).setBackgroundColor(-1);
            }
            paramView = (TextSettingsCell)localObject;
            paramViewGroup = (ViewGroup)localObject;
          } while (paramInt != SetPasswordActivity.this.changeenablePasscodeRow);
          paramViewGroup = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getString("chat_password", "");
          paramView.setText(LocaleController.getString("ChangePass", 2131165429), true);
          if (paramViewGroup.length() == 0) {}
          for (paramInt = -3750202;; paramInt = -16777216)
          {
            paramView.setTextColor(paramInt);
            return (View)localObject;
          }
          paramViewGroup = paramView;
        } while (i != 2);
        localObject = paramView;
        if (paramView == null) {
          localObject = new TextInfoPrivacyCell(this.mContext);
        }
        if (paramInt == SetPasswordActivity.this.passcodeDetailRow)
        {
          ((TextInfoPrivacyCell)localObject).setText(LocaleController.getString("ChangePassInfo", 2131165430));
          ((View)localObject).setBackgroundResource(2130837800);
          return (View)localObject;
        }
        if (paramInt == SetPasswordActivity.this.hiddenInShareAlertDesRow)
        {
          ((TextInfoPrivacyCell)localObject).setText(LocaleController.getString("HiddenInShareAlertDes", 2131166642));
          ((View)localObject).setBackgroundResource(2130837800);
          return (View)localObject;
        }
        paramViewGroup = (ViewGroup)localObject;
      } while (paramInt != SetPasswordActivity.this.showHiddenNotifDesRow);
      ((TextInfoPrivacyCell)localObject).setText(LocaleController.getString("HiddenShowNotifDes", 2131166644));
      ((View)localObject).setBackgroundResource(2130837800);
      return (View)localObject;
    }
    
    public int getViewTypeCount()
    {
      return 3;
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
      String str = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getString("chat_password", "");
      if ((paramInt == SetPasswordActivity.this.enablePasscodeRow) || ((str.length() != 0) && (paramInt == SetPasswordActivity.this.changeenablePasscodeRow)) || (paramInt == SetPasswordActivity.this.hiddenInShareAlertRow) || (paramInt == SetPasswordActivity.this.showHiddenNotifRow)) {
        bool = true;
      }
      return bool;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\SetPasswordActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */