package org.telegram.ui.Supergram;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Supergram.Theming.MihanTheme;

public class IdFinderActivity
  extends BaseFragment
{
  private static final int done_button = 1;
  private EditText IdEditText;
  private TLRPC.Chat cht;
  private TextView desTextView;
  private TextView msgTextView;
  TLRPC.TL_contacts_resolvedPeer res;
  private ScrollView scrollView;
  private TLRPC.User usr;
  
  public void checkId()
  {
    this.cht = null;
    this.usr = null;
    if (this.IdEditText.length() == 0)
    {
      this.msgTextView.setText("");
      this.msgTextView.setVisibility(8);
    }
    String str;
    do
    {
      return;
      if (this.IdEditText.length() < 5) {
        break;
      }
      this.msgTextView.setVisibility(0);
      str = this.IdEditText.getText().toString();
    } while (str == null);
    Object localObject = MessagesController.getInstance().getUser(str);
    if (localObject != null)
    {
      if (((TLRPC.User)localObject).bot) {
        this.msgTextView.setText(LocaleController.getString("BotIsAvailble", 2131165410));
      }
      for (;;)
      {
        this.msgTextView.setTextColor(-16731904);
        this.usr = ((TLRPC.User)localObject);
        return;
        this.msgTextView.setText(LocaleController.getString("UserIsAvailble", 2131166435));
      }
    }
    this.msgTextView.setText(LocaleController.getString("CheckingId", 2131165540));
    this.msgTextView.setTextColor(-3355444);
    localObject = new TLRPC.TL_contacts_resolveUsername();
    ((TLRPC.TL_contacts_resolveUsername)localObject).username = str;
    ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (paramAnonymousTL_error == null)
            {
              IdFinderActivity.this.res = ((TLRPC.TL_contacts_resolvedPeer)paramAnonymousTLObject);
              if (!IdFinderActivity.this.res.chats.isEmpty())
              {
                IdFinderActivity.this.msgTextView.setText(LocaleController.getString("ChannelIsAvailble", 2131165468));
                IdFinderActivity.this.msgTextView.setTextColor(-16731904);
                IdFinderActivity.access$102(IdFinderActivity.this, (TLRPC.Chat)IdFinderActivity.this.res.chats.get(0));
              }
              while (IdFinderActivity.this.res.users.isEmpty()) {
                return;
              }
              IdFinderActivity.this.msgTextView.setText(LocaleController.getString("UserIsAvailble", 2131166435));
              IdFinderActivity.this.msgTextView.setTextColor(-16731904);
              IdFinderActivity.access$202(IdFinderActivity.this, (TLRPC.User)IdFinderActivity.this.res.users.get(0));
              return;
            }
            try
            {
              IdFinderActivity.this.msgTextView.setText(LocaleController.getString("UserIsNotAvailble", 2131166436));
              IdFinderActivity.this.msgTextView.setTextColor(-65536);
              return;
            }
            catch (Exception localException)
            {
              FileLog.e("tmessages", localException);
            }
          }
        });
      }
    });
    return;
    this.msgTextView.setVisibility(0);
    this.msgTextView.setText(LocaleController.getString("IdFinderNotice", 2131165810));
    this.msgTextView.setTextColor(-65536);
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837810);
    this.actionBar.setTitle(LocaleController.getString("IdFinder", 2131165808));
    this.actionBar.setAllowOverlayTitle(false);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          IdFinderActivity.this.finishFragment();
        }
        while (paramAnonymousInt != 1) {
          return;
        }
        IdFinderActivity.this.openChatOrProfile();
      }
    });
    this.actionBar.createMenu().addItemWithWidth(1, 2130837844, AndroidUtilities.dp(56.0F));
    this.fragmentView = new FrameLayout(paramContext);
    Object localObject1 = (FrameLayout)this.fragmentView;
    this.scrollView = new ScrollView(paramContext);
    this.scrollView.setFillViewport(true);
    ((FrameLayout)localObject1).addView(this.scrollView);
    localObject1 = (FrameLayout.LayoutParams)this.scrollView.getLayoutParams();
    ((FrameLayout.LayoutParams)localObject1).width = -1;
    ((FrameLayout.LayoutParams)localObject1).height = -1;
    this.scrollView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
    localObject1 = new LinearLayout(paramContext);
    ((LinearLayout)localObject1).setOrientation(1);
    this.scrollView.addView((View)localObject1);
    Object localObject2 = (FrameLayout.LayoutParams)((LinearLayout)localObject1).getLayoutParams();
    ((FrameLayout.LayoutParams)localObject2).width = -1;
    ((FrameLayout.LayoutParams)localObject2).height = -2;
    ((LinearLayout)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject2);
    this.IdEditText = new EditText(paramContext);
    this.IdEditText.setTextSize(1, 18.0F);
    this.IdEditText.setTextColor(-16777216);
    this.IdEditText.setHint(LocaleController.getString("IdToFind", 2131165811));
    this.IdEditText.setMaxLines(1);
    this.IdEditText.setLines(1);
    this.IdEditText.setGravity(3);
    this.IdEditText.setSingleLine(true);
    this.IdEditText.setTypeface(MihanTheme.getMihanTypeFace());
    this.IdEditText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    AndroidUtilities.clearCursorDrawable(this.IdEditText);
    ((LinearLayout)localObject1).addView(this.IdEditText);
    localObject2 = (LinearLayout.LayoutParams)this.IdEditText.getLayoutParams();
    ((LinearLayout.LayoutParams)localObject2).topMargin = AndroidUtilities.dp(20.0F);
    ((LinearLayout.LayoutParams)localObject2).height = AndroidUtilities.dp(36.0F);
    ((LinearLayout.LayoutParams)localObject2).leftMargin = AndroidUtilities.dp(20.0F);
    ((LinearLayout.LayoutParams)localObject2).gravity = 51;
    ((LinearLayout.LayoutParams)localObject2).rightMargin = AndroidUtilities.dp(20.0F);
    ((LinearLayout.LayoutParams)localObject2).width = -1;
    ((LinearLayout.LayoutParams)localObject2).bottomMargin = AndroidUtilities.dp(15.0F);
    this.IdEditText.setLayoutParams((ViewGroup.LayoutParams)localObject2);
    this.IdEditText.addTextChangedListener(new TextWatcher()
    {
      public void afterTextChanged(Editable paramAnonymousEditable)
      {
        IdFinderActivity.this.checkId();
      }
      
      public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      
      public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
    });
    this.msgTextView = new TextView(paramContext);
    this.msgTextView.setTextColor(-9079435);
    this.msgTextView.setText("");
    this.msgTextView.setTextSize(1, 16.0F);
    this.msgTextView.setGravity(1);
    this.msgTextView.setVisibility(8);
    this.msgTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    ((LinearLayout)localObject1).addView(this.msgTextView);
    localObject2 = (LinearLayout.LayoutParams)this.msgTextView.getLayoutParams();
    ((LinearLayout.LayoutParams)localObject2).width = -2;
    ((LinearLayout.LayoutParams)localObject2).height = -2;
    ((LinearLayout.LayoutParams)localObject2).gravity = 1;
    ((LinearLayout.LayoutParams)localObject2).bottomMargin = AndroidUtilities.dp(7.0F);
    this.msgTextView.setLayoutParams((ViewGroup.LayoutParams)localObject2);
    this.desTextView = new TextView(paramContext);
    this.desTextView.setTextColor(-9079435);
    this.desTextView.setText(LocaleController.getString("IdFinderDescription", 2131165809));
    this.desTextView.setTextSize(1, 15.0F);
    if (LocaleController.isRTL) {
      this.msgTextView.setGravity(5);
    }
    for (;;)
    {
      this.desTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      ((LinearLayout)localObject1).addView(this.desTextView);
      paramContext = (LinearLayout.LayoutParams)this.desTextView.getLayoutParams();
      paramContext.width = -2;
      paramContext.height = -2;
      paramContext.gravity = 1;
      paramContext.leftMargin = AndroidUtilities.dp(20.0F);
      paramContext.rightMargin = AndroidUtilities.dp(20.0F);
      this.desTextView.setLayoutParams(paramContext);
      return this.fragmentView;
      this.msgTextView.setGravity(3);
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (!ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true))
    {
      this.IdEditText.requestFocus();
      AndroidUtilities.showKeyboard(this.IdEditText);
    }
  }
  
  public void openChatOrProfile()
  {
    Object localObject = this.IdEditText.getText().toString();
    if (this.cht != null)
    {
      MessagesController.getInstance();
      MessagesController.openByUserName((String)localObject, this, 1);
      return;
    }
    if (this.usr != null)
    {
      MessagesController.getInstance();
      MessagesController.openByUserName((String)localObject, this, 1);
      return;
    }
    localObject = Toast.makeText(getParentActivity(), LocaleController.getString("UserIsNotAvailble", 2131166436), 1);
    ((TextView)((LinearLayout)((Toast)localObject).getView()).getChildAt(0)).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    ((Toast)localObject).show();
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\IdFinderActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */