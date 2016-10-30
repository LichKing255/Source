package org.telegram.ui.Mihangram;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Mihangram.Theming.MihanTheme;

public class AddContactActivity
  extends BaseFragment
{
  private static final int done_button = 1;
  private TextView detailsTextView;
  private View doneButton;
  private EditText firstNameField;
  private EditText lastNameField;
  private EditText phoneField;
  
  private void onNameFieldError()
  {
    if (getParentActivity() == null) {
      return;
    }
    Vibrator localVibrator = (Vibrator)getParentActivity().getSystemService("vibrator");
    if (localVibrator != null) {
      localVibrator.vibrate(200L);
    }
    AndroidUtilities.shakeView(this.firstNameField, 2.0F, 0);
  }
  
  private void onPhoneFieldError()
  {
    if (getParentActivity() == null) {
      return;
    }
    Vibrator localVibrator = (Vibrator)getParentActivity().getSystemService("vibrator");
    if (localVibrator != null) {
      localVibrator.vibrate(200L);
    }
    AndroidUtilities.shakeView(this.phoneField, 2.0F, 0);
  }
  
  public View createView(Context paramContext)
  {
    int j = 5;
    this.actionBar.setBackButtonImage(2130837810);
    this.actionBar.setTitle(LocaleController.getString("MihanAddContact", 2131166809));
    this.actionBar.setAllowOverlayTitle(false);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          AddContactActivity.this.finishFragment();
        }
        do
        {
          do
          {
            return;
          } while (paramAnonymousInt != 1);
          if ((AddContactActivity.this.firstNameField.getText().length() != 0) && (AddContactActivity.this.phoneField.getText().length() != 0))
          {
            TLRPC.User localUser = new TLRPC.User();
            localUser.first_name = AddContactActivity.this.firstNameField.getText().toString();
            localUser.last_name = AddContactActivity.this.lastNameField.getText().toString();
            localUser.phone = AddContactActivity.this.phoneField.getText().toString();
            ContactsController.getInstance().addContact(localUser);
            AddContactActivity.this.finishFragment();
            return;
          }
          if (AddContactActivity.this.firstNameField.getText().length() == 0)
          {
            AddContactActivity.this.onNameFieldError();
            return;
          }
        } while (AddContactActivity.this.phoneField.getText().length() != 0);
        AddContactActivity.this.onPhoneFieldError();
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837844, AndroidUtilities.dp(56.0F));
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    this.fragmentView = localLinearLayout;
    this.fragmentView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
    ((LinearLayout)this.fragmentView).setOrientation(1);
    this.fragmentView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.firstNameField = new EditText(paramContext);
    this.firstNameField.setTextSize(1, 18.0F);
    this.firstNameField.setHintTextColor(-6842473);
    this.firstNameField.setTextColor(-14606047);
    this.firstNameField.setMaxLines(1);
    this.firstNameField.setLines(1);
    this.firstNameField.setSingleLine(true);
    EditText localEditText = this.firstNameField;
    if (LocaleController.isRTL)
    {
      i = 5;
      localEditText.setGravity(i);
      this.firstNameField.setInputType(49152);
      this.firstNameField.setImeOptions(5);
      this.firstNameField.setHint(LocaleController.getString("FirstName", 2131165690));
      AndroidUtilities.clearCursorDrawable(this.firstNameField);
      this.firstNameField.setTypeface(MihanTheme.getMihanTypeFace());
      localLinearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0F, 24.0F, 24.0F, 0.0F));
      this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if (paramAnonymousInt == 5)
          {
            AddContactActivity.this.lastNameField.requestFocus();
            AddContactActivity.this.lastNameField.setSelection(AddContactActivity.this.lastNameField.length());
            return true;
          }
          return false;
        }
      });
      this.lastNameField = new EditText(paramContext);
      this.lastNameField.setTextSize(1, 18.0F);
      this.lastNameField.setHintTextColor(-6842473);
      this.lastNameField.setTextColor(-14606047);
      this.lastNameField.setMaxLines(1);
      this.lastNameField.setLines(1);
      this.lastNameField.setSingleLine(true);
      localEditText = this.lastNameField;
      if (!LocaleController.isRTL) {
        break label732;
      }
      i = 5;
      label375:
      localEditText.setGravity(i);
      this.lastNameField.setInputType(49152);
      this.lastNameField.setImeOptions(5);
      this.lastNameField.setHint(LocaleController.getString("LastName", 2131165858));
      AndroidUtilities.clearCursorDrawable(this.lastNameField);
      this.lastNameField.setTypeface(MihanTheme.getMihanTypeFace());
      localLinearLayout.addView(this.lastNameField, LayoutHelper.createLinear(-1, 36, 24.0F, 16.0F, 24.0F, 0.0F));
      this.lastNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if (paramAnonymousInt == 5)
          {
            AddContactActivity.this.phoneField.requestFocus();
            AddContactActivity.this.phoneField.setSelection(AddContactActivity.this.lastNameField.length());
            return true;
          }
          return false;
        }
      });
      this.phoneField = new EditText(paramContext);
      this.phoneField.setTextSize(1, 18.0F);
      this.phoneField.setHintTextColor(-6842473);
      this.phoneField.setTextColor(-14606047);
      this.phoneField.setMaxLines(1);
      this.phoneField.setLines(1);
      this.phoneField.setSingleLine(true);
      localEditText = this.phoneField;
      if (!LocaleController.isRTL) {
        break label737;
      }
      i = 5;
      label544:
      localEditText.setGravity(i);
      this.phoneField.setInputType(49152);
      this.phoneField.setImeOptions(6);
      this.phoneField.setHint(LocaleController.getString("AddContactPhone", 2131166611));
      AndroidUtilities.clearCursorDrawable(this.phoneField);
      this.phoneField.setInputType(2);
      this.phoneField.setTypeface(MihanTheme.getMihanTypeFace());
      localLinearLayout.addView(this.phoneField, LayoutHelper.createLinear(-1, 36, 24.0F, 16.0F, 24.0F, 0.0F));
      this.phoneField.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if (paramAnonymousInt == 6)
          {
            AddContactActivity.this.doneButton.performClick();
            return true;
          }
          return false;
        }
      });
      paramContext = new TextView(paramContext);
      paramContext.setTextColor(-9605774);
      paramContext.setText(LocaleController.getString("AddContactDetails", 2131166610));
      paramContext.setTextSize(1, 15.0F);
      if (!LocaleController.isRTL) {
        break label742;
      }
    }
    label732:
    label737:
    label742:
    for (int i = j;; i = 3)
    {
      paramContext.setGravity(i);
      paramContext.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      localLinearLayout.addView(paramContext, LayoutHelper.createLinear(-2, -2, 48, 24, 16, 24, 0));
      return this.fragmentView;
      i = 3;
      break;
      i = 3;
      break label375;
      i = 3;
      break label544;
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (!ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true))
    {
      this.firstNameField.requestFocus();
      AndroidUtilities.showKeyboard(this.firstNameField);
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\AddContactActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */