package org.telegram.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.Semaphore;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_channels_checkUsername;
import org.telegram.tgnet.TLRPC.TL_channels_exportInvite;
import org.telegram.tgnet.TLRPC.TL_chatInviteExported;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;

public class ChannelEditTypeActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int done_button = 1;
  private int chatId;
  private int checkReqId = 0;
  private Runnable checkRunnable = null;
  private TextView checkTextView;
  private TLRPC.Chat currentChat;
  private boolean donePressed;
  private HeaderCell headerCell;
  private TLRPC.ExportedChatInvite invite;
  private boolean isPrivate = false;
  private String lastCheckName = null;
  private boolean lastNameAvailable = false;
  private LinearLayout linkContainer;
  private boolean loadingInvite;
  private EditText nameTextView;
  private TextBlockCell privateContainer;
  private LinearLayout publicContainer;
  private RadioButtonCell radioButtonCell1;
  private RadioButtonCell radioButtonCell2;
  private TextInfoPrivacyCell typeInfoCell;
  
  public ChannelEditTypeActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.chatId = paramBundle.getInt("chat_id", 0);
  }
  
  private boolean checkUserName(final String paramString, boolean paramBoolean)
  {
    if ((paramString != null) && (paramString.length() > 0))
    {
      this.checkTextView.setVisibility(0);
      if ((!paramBoolean) || (paramString.length() != 0)) {
        break label44;
      }
    }
    label44:
    do
    {
      return true;
      this.checkTextView.setVisibility(8);
      break;
      if (this.checkRunnable != null)
      {
        AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
        this.checkRunnable = null;
        this.lastCheckName = null;
        if (this.checkReqId != 0) {
          ConnectionsManager.getInstance().cancelRequest(this.checkReqId, true);
        }
      }
      this.lastNameAvailable = false;
      if (paramString != null)
      {
        if ((paramString.startsWith("_")) || (paramString.endsWith("_")))
        {
          this.checkTextView.setText(LocaleController.getString("LinkInvalid", 2131165897));
          this.checkTextView.setTextColor(-3198928);
          return false;
        }
        int i = 0;
        while (i < paramString.length())
        {
          int j = paramString.charAt(i);
          if ((i == 0) && (j >= 48) && (j <= 57))
          {
            if (this.currentChat.megagroup) {
              if (paramBoolean) {
                showErrorAlert(LocaleController.getString("LinkInvalidStartNumberMega", 2131165902));
              }
            }
            for (;;)
            {
              return false;
              this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumberMega", 2131165902));
              this.checkTextView.setTextColor(-3198928);
              continue;
              if (paramBoolean)
              {
                showErrorAlert(LocaleController.getString("LinkInvalidStartNumber", 2131165901));
              }
              else
              {
                this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", 2131165901));
                this.checkTextView.setTextColor(-3198928);
              }
            }
          }
          if (((j < 48) || (j > 57)) && ((j < 97) || (j > 122)) && ((j < 65) || (j > 90)) && (j != 95))
          {
            if (paramBoolean) {
              showErrorAlert(LocaleController.getString("LinkInvalid", 2131165897));
            }
            for (;;)
            {
              return false;
              this.checkTextView.setText(LocaleController.getString("LinkInvalid", 2131165897));
              this.checkTextView.setTextColor(-3198928);
            }
          }
          i += 1;
        }
      }
      if ((paramString == null) || (paramString.length() < 5))
      {
        if (this.currentChat.megagroup) {
          if (paramBoolean) {
            showErrorAlert(LocaleController.getString("LinkInvalidShortMega", 2131165900));
          }
        }
        for (;;)
        {
          return false;
          this.checkTextView.setText(LocaleController.getString("LinkInvalidShortMega", 2131165900));
          this.checkTextView.setTextColor(-3198928);
          continue;
          if (paramBoolean)
          {
            showErrorAlert(LocaleController.getString("LinkInvalidShort", 2131165899));
          }
          else
          {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", 2131165899));
            this.checkTextView.setTextColor(-3198928);
          }
        }
      }
      if (paramString.length() > 32)
      {
        if (paramBoolean) {
          showErrorAlert(LocaleController.getString("LinkInvalidLong", 2131165898));
        }
        for (;;)
        {
          return false;
          this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", 2131165898));
          this.checkTextView.setTextColor(-3198928);
        }
      }
    } while (paramBoolean);
    this.checkTextView.setText(LocaleController.getString("LinkChecking", 2131165893));
    this.checkTextView.setTextColor(-9605774);
    this.lastCheckName = paramString;
    this.checkRunnable = new Runnable()
    {
      public void run()
      {
        TLRPC.TL_channels_checkUsername localTL_channels_checkUsername = new TLRPC.TL_channels_checkUsername();
        localTL_channels_checkUsername.username = paramString;
        localTL_channels_checkUsername.channel = MessagesController.getInputChannel(ChannelEditTypeActivity.this.chatId);
        ChannelEditTypeActivity.access$1002(ChannelEditTypeActivity.this, ConnectionsManager.getInstance().sendRequest(localTL_channels_checkUsername, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymous2TLObject, final TLRPC.TL_error paramAnonymous2TL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                ChannelEditTypeActivity.access$1002(ChannelEditTypeActivity.this, 0);
                if ((ChannelEditTypeActivity.this.lastCheckName != null) && (ChannelEditTypeActivity.this.lastCheckName.equals(ChannelEditTypeActivity.7.this.val$name)))
                {
                  if ((paramAnonymous2TL_error == null) && ((paramAnonymous2TLObject instanceof TLRPC.TL_boolTrue)))
                  {
                    ChannelEditTypeActivity.this.checkTextView.setText(LocaleController.formatString("LinkAvailable", 2131165892, new Object[] { ChannelEditTypeActivity.7.this.val$name }));
                    ChannelEditTypeActivity.this.checkTextView.setTextColor(-14248148);
                    ChannelEditTypeActivity.access$502(ChannelEditTypeActivity.this, true);
                  }
                }
                else {
                  return;
                }
                if ((paramAnonymous2TL_error != null) && (paramAnonymous2TL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH"))) {
                  ChannelEditTypeActivity.this.checkTextView.setText(LocaleController.getString("ChangePublicLimitReached", 2131165438));
                }
                for (;;)
                {
                  ChannelEditTypeActivity.this.checkTextView.setTextColor(-3198928);
                  ChannelEditTypeActivity.access$502(ChannelEditTypeActivity.this, false);
                  return;
                  ChannelEditTypeActivity.this.checkTextView.setText(LocaleController.getString("LinkInUse", 2131165895));
                }
              }
            });
          }
        }, 2));
      }
    };
    AndroidUtilities.runOnUIThread(this.checkRunnable, 300L);
    return true;
  }
  
  private void generateLink()
  {
    if ((this.loadingInvite) || (this.invite != null)) {
      return;
    }
    this.loadingInvite = true;
    TLRPC.TL_channels_exportInvite localTL_channels_exportInvite = new TLRPC.TL_channels_exportInvite();
    localTL_channels_exportInvite.channel = MessagesController.getInputChannel(this.chatId);
    ConnectionsManager.getInstance().sendRequest(localTL_channels_exportInvite, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (paramAnonymousTL_error == null) {
              ChannelEditTypeActivity.access$902(ChannelEditTypeActivity.this, (TLRPC.ExportedChatInvite)paramAnonymousTLObject);
            }
            ChannelEditTypeActivity.access$1202(ChannelEditTypeActivity.this, false);
            TextBlockCell localTextBlockCell = ChannelEditTypeActivity.this.privateContainer;
            if (ChannelEditTypeActivity.this.invite != null) {}
            for (String str = ChannelEditTypeActivity.this.invite.link;; str = LocaleController.getString("Loading", 2131165905))
            {
              localTextBlockCell.setText(str, false);
              return;
            }
          }
        });
      }
    });
  }
  
  private void showErrorAlert(String paramString)
  {
    if (getParentActivity() == null) {
      return;
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165338));
    int i = -1;
    switch (paramString.hashCode())
    {
    default: 
      switch (i)
      {
      default: 
        localBuilder.setMessage(LocaleController.getString("ErrorOccurred", 2131165672));
      }
      break;
    }
    for (;;)
    {
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166111), null);
      showDialog(localBuilder.create());
      return;
      if (!paramString.equals("USERNAME_INVALID")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("USERNAME_OCCUPIED")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("USERNAMES_UNAVAILABLE")) {
        break;
      }
      i = 2;
      break;
      localBuilder.setMessage(LocaleController.getString("LinkInvalid", 2131165897));
      continue;
      localBuilder.setMessage(LocaleController.getString("LinkInUse", 2131165895));
      continue;
      localBuilder.setMessage(LocaleController.getString("FeatureUnavailable", 2131165685));
    }
  }
  
  private void updatePrivatePublic()
  {
    int j = 0;
    Object localObject1 = this.radioButtonCell1;
    boolean bool;
    Object localObject2;
    if (!this.isPrivate)
    {
      bool = true;
      ((RadioButtonCell)localObject1).setChecked(bool, true);
      this.radioButtonCell2.setChecked(this.isPrivate, true);
      if (!this.currentChat.megagroup) {
        break label284;
      }
      localObject2 = this.typeInfoCell;
      if (!this.isPrivate) {
        break label256;
      }
      localObject1 = LocaleController.getString("MegaPrivateLinkHelp", 2131165928);
      label70:
      ((TextInfoPrivacyCell)localObject2).setText((CharSequence)localObject1);
      localObject2 = this.headerCell;
      if (!this.isPrivate) {
        break label270;
      }
      localObject1 = LocaleController.getString("ChannelInviteLinkTitle", 2131165466);
      label101:
      ((HeaderCell)localObject2).setText((String)localObject1);
      localObject1 = this.publicContainer;
      if (!this.isPrivate) {
        break label377;
      }
      i = 8;
      label124:
      ((LinearLayout)localObject1).setVisibility(i);
      localObject1 = this.privateContainer;
      if (!this.isPrivate) {
        break label382;
      }
      i = 0;
      label145:
      ((TextBlockCell)localObject1).setVisibility(i);
      localObject1 = this.linkContainer;
      if (!this.isPrivate) {
        break label388;
      }
      i = 0;
      label166:
      ((LinearLayout)localObject1).setPadding(0, 0, 0, i);
      localObject2 = this.privateContainer;
      if (this.invite == null) {
        break label398;
      }
      localObject1 = this.invite.link;
      label197:
      ((TextBlockCell)localObject2).setText((String)localObject1, false);
      this.nameTextView.clearFocus();
      localObject1 = this.checkTextView;
      if ((this.isPrivate) || (this.checkTextView.length() == 0)) {
        break label412;
      }
    }
    label256:
    label270:
    label284:
    label308:
    label363:
    label377:
    label382:
    label388:
    label398:
    label412:
    for (int i = j;; i = 8)
    {
      ((TextView)localObject1).setVisibility(i);
      AndroidUtilities.hideKeyboard(this.nameTextView);
      return;
      bool = false;
      break;
      localObject1 = LocaleController.getString("MegaUsernameHelp", 2131165931);
      break label70;
      localObject1 = LocaleController.getString("ChannelLinkTitle", 2131165474);
      break label101;
      localObject2 = this.typeInfoCell;
      if (this.isPrivate)
      {
        localObject1 = LocaleController.getString("ChannelPrivateLinkHelp", 2131165507);
        ((TextInfoPrivacyCell)localObject2).setText((CharSequence)localObject1);
        localObject2 = this.headerCell;
        if (!this.isPrivate) {
          break label363;
        }
      }
      for (localObject1 = LocaleController.getString("ChannelInviteLinkTitle", 2131165466);; localObject1 = LocaleController.getString("ChannelLinkTitle", 2131165474))
      {
        ((HeaderCell)localObject2).setText((String)localObject1);
        break;
        localObject1 = LocaleController.getString("ChannelUsernameHelp", 2131165528);
        break label308;
      }
      i = 0;
      break label124;
      i = 8;
      break label145;
      i = AndroidUtilities.dp(7.0F);
      break label166;
      localObject1 = LocaleController.getString("Loading", 2131165905);
      break label197;
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837810);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          ChannelEditTypeActivity.this.finishFragment();
        }
        while ((paramAnonymousInt != 1) || (ChannelEditTypeActivity.this.donePressed)) {
          return;
        }
        Object localObject;
        if ((!ChannelEditTypeActivity.this.isPrivate) && (((ChannelEditTypeActivity.this.currentChat.username == null) && (ChannelEditTypeActivity.this.nameTextView.length() != 0)) || ((ChannelEditTypeActivity.this.currentChat.username != null) && (!ChannelEditTypeActivity.this.currentChat.username.equalsIgnoreCase(ChannelEditTypeActivity.this.nameTextView.getText().toString())) && (ChannelEditTypeActivity.this.nameTextView.length() != 0) && (!ChannelEditTypeActivity.this.lastNameAvailable))))
        {
          localObject = (Vibrator)ChannelEditTypeActivity.this.getParentActivity().getSystemService("vibrator");
          if (localObject != null) {
            ((Vibrator)localObject).vibrate(200L);
          }
          AndroidUtilities.shakeView(ChannelEditTypeActivity.this.checkTextView, 2.0F, 0);
          return;
        }
        ChannelEditTypeActivity.access$202(ChannelEditTypeActivity.this, true);
        if (ChannelEditTypeActivity.this.currentChat.username != null)
        {
          localObject = ChannelEditTypeActivity.this.currentChat.username;
          if (!ChannelEditTypeActivity.this.isPrivate) {
            break label251;
          }
        }
        label251:
        for (String str = "";; str = ChannelEditTypeActivity.this.nameTextView.getText().toString())
        {
          if (!((String)localObject).equals(str)) {
            MessagesController.getInstance().updateChannelUserName(ChannelEditTypeActivity.this.chatId, str);
          }
          ChannelEditTypeActivity.this.finishFragment();
          return;
          localObject = "";
          break;
        }
      }
    });
    this.actionBar.createMenu().addItemWithWidth(1, 2130837844, AndroidUtilities.dp(56.0F));
    this.fragmentView = new ScrollView(paramContext);
    this.fragmentView.setBackgroundColor(-986896);
    Object localObject1 = (ScrollView)this.fragmentView;
    ((ScrollView)localObject1).setFillViewport(true);
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    ((ScrollView)localObject1).addView(localLinearLayout, new FrameLayout.LayoutParams(-1, -2));
    localLinearLayout.setOrientation(1);
    Object localObject2;
    String str1;
    String str2;
    boolean bool;
    if (this.currentChat.megagroup)
    {
      this.actionBar.setTitle(LocaleController.getString("GroupType", 2131165783));
      localObject1 = new LinearLayout(paramContext);
      ((LinearLayout)localObject1).setOrientation(1);
      ((LinearLayout)localObject1).setBackgroundColor(-1);
      localLinearLayout.addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
      this.radioButtonCell1 = new RadioButtonCell(paramContext);
      this.radioButtonCell1.setBackgroundResource(2130837932);
      if (!this.currentChat.megagroup) {
        break label1033;
      }
      localObject2 = this.radioButtonCell1;
      str1 = LocaleController.getString("MegaPublic", 2131165929);
      str2 = LocaleController.getString("MegaPublicInfo", 2131165930);
      if (this.isPrivate) {
        break label1028;
      }
      bool = true;
      label254:
      ((RadioButtonCell)localObject2).setTextAndValue(str1, str2, bool, false);
      ((LinearLayout)localObject1).addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
      this.radioButtonCell1.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (!ChannelEditTypeActivity.this.isPrivate) {
            return;
          }
          ChannelEditTypeActivity.access$302(ChannelEditTypeActivity.this, false);
          ChannelEditTypeActivity.this.updatePrivatePublic();
        }
      });
      this.radioButtonCell2 = new RadioButtonCell(paramContext);
      this.radioButtonCell2.setBackgroundResource(2130837932);
      if (!this.currentChat.megagroup) {
        break label1089;
      }
      this.radioButtonCell2.setTextAndValue(LocaleController.getString("MegaPrivate", 2131165926), LocaleController.getString("MegaPrivateInfo", 2131165927), this.isPrivate, false);
      label357:
      ((LinearLayout)localObject1).addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
      this.radioButtonCell2.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (ChannelEditTypeActivity.this.isPrivate) {
            return;
          }
          ChannelEditTypeActivity.access$302(ChannelEditTypeActivity.this, true);
          ChannelEditTypeActivity.this.updatePrivatePublic();
        }
      });
      localLinearLayout.addView(new ShadowSectionCell(paramContext), LayoutHelper.createLinear(-1, -2));
      this.linkContainer = new LinearLayout(paramContext);
      this.linkContainer.setOrientation(1);
      this.linkContainer.setBackgroundColor(-1);
      localLinearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
      this.headerCell = new HeaderCell(paramContext);
      this.headerCell.setBackgroundColor(-1);
      this.linkContainer.addView(this.headerCell);
      this.publicContainer = new LinearLayout(paramContext);
      this.publicContainer.setOrientation(0);
      this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 17.0F, 7.0F, 17.0F, 0.0F));
      localObject1 = new EditText(paramContext);
      ((EditText)localObject1).setText("telegram.me/");
      ((EditText)localObject1).setTextSize(1, 18.0F);
      ((EditText)localObject1).setHintTextColor(-6842473);
      ((EditText)localObject1).setTextColor(-14606047);
      ((EditText)localObject1).setMaxLines(1);
      ((EditText)localObject1).setLines(1);
      ((EditText)localObject1).setEnabled(false);
      ((EditText)localObject1).setBackgroundDrawable(null);
      ((EditText)localObject1).setPadding(0, 0, 0, 0);
      ((EditText)localObject1).setSingleLine(true);
      ((EditText)localObject1).setInputType(163840);
      ((EditText)localObject1).setImeOptions(6);
      this.publicContainer.addView((View)localObject1, LayoutHelper.createLinear(-2, 36));
      this.nameTextView = new EditText(paramContext);
      this.nameTextView.setTextSize(1, 18.0F);
      if (!this.isPrivate) {
        this.nameTextView.setText(this.currentChat.username);
      }
      this.nameTextView.setHintTextColor(-6842473);
      this.nameTextView.setTextColor(-14606047);
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setLines(1);
      this.nameTextView.setBackgroundDrawable(null);
      this.nameTextView.setPadding(0, 0, 0, 0);
      this.nameTextView.setSingleLine(true);
      this.nameTextView.setInputType(163872);
      this.nameTextView.setImeOptions(6);
      this.nameTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", 2131165529));
      AndroidUtilities.clearCursorDrawable(this.nameTextView);
      this.publicContainer.addView(this.nameTextView, LayoutHelper.createLinear(-1, 36));
      this.nameTextView.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramAnonymousEditable) {}
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
        {
          ChannelEditTypeActivity.this.checkUserName(ChannelEditTypeActivity.this.nameTextView.getText().toString(), false);
        }
      });
      this.privateContainer = new TextBlockCell(paramContext);
      this.privateContainer.setBackgroundResource(2130837932);
      this.linkContainer.addView(this.privateContainer);
      this.privateContainer.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (ChannelEditTypeActivity.this.invite == null) {
            return;
          }
          try
          {
            ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", ChannelEditTypeActivity.this.invite.link));
            Toast.makeText(ChannelEditTypeActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", 2131165894), 0).show();
            return;
          }
          catch (Exception paramAnonymousView)
          {
            FileLog.e("tmessages", paramAnonymousView);
          }
        }
      });
      this.checkTextView = new TextView(paramContext);
      this.checkTextView.setTextSize(1, 15.0F);
      localObject1 = this.checkTextView;
      if (!LocaleController.isRTL) {
        break label1122;
      }
      i = 5;
      label906:
      ((TextView)localObject1).setGravity(i);
      this.checkTextView.setVisibility(8);
      localObject1 = this.linkContainer;
      localObject2 = this.checkTextView;
      if (!LocaleController.isRTL) {
        break label1127;
      }
    }
    label1028:
    label1033:
    label1089:
    label1122:
    label1127:
    for (int i = 5;; i = 3)
    {
      ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(-2, -2, i, 17, 3, 17, 7));
      this.typeInfoCell = new TextInfoPrivacyCell(paramContext);
      this.typeInfoCell.setBackgroundResource(2130837800);
      localLinearLayout.addView(this.typeInfoCell, LayoutHelper.createLinear(-1, -2));
      updatePrivatePublic();
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("ChannelType", 2131165519));
      break;
      bool = false;
      break label254;
      localObject2 = this.radioButtonCell1;
      str1 = LocaleController.getString("ChannelPublic", 2131165508);
      str2 = LocaleController.getString("ChannelPublicInfo", 2131165510);
      if (!this.isPrivate) {}
      for (bool = true;; bool = false)
      {
        ((RadioButtonCell)localObject2).setTextAndValue(str1, str2, bool, false);
        break;
      }
      this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", 2131165505), LocaleController.getString("ChannelPrivateInfo", 2131165506), this.isPrivate, false);
      break label357;
      i = 3;
      break label906;
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.chatInfoDidLoaded)
    {
      paramVarArgs = (TLRPC.ChatFull)paramVarArgs[0];
      if (paramVarArgs.id == this.chatId)
      {
        this.invite = paramVarArgs.exported_invite;
        updatePrivatePublic();
      }
    }
  }
  
  public boolean onFragmentCreate()
  {
    boolean bool1 = false;
    boolean bool2 = false;
    this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(this.chatId));
    final Semaphore localSemaphore;
    if (this.currentChat == null)
    {
      localSemaphore = new Semaphore(0);
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          ChannelEditTypeActivity.access$002(ChannelEditTypeActivity.this, MessagesStorage.getInstance().getChat(ChannelEditTypeActivity.this.chatId));
          localSemaphore.release();
        }
      });
    }
    try
    {
      localSemaphore.acquire();
      if (this.currentChat != null)
      {
        MessagesController.getInstance().putChat(this.currentChat, true);
        if (this.currentChat.username != null)
        {
          bool1 = bool2;
          if (this.currentChat.username.length() != 0) {}
        }
        else
        {
          bool1 = true;
        }
        this.isPrivate = bool1;
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        bool1 = super.onFragmentCreate();
      }
      return bool1;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
  }
  
  public void onResume()
  {
    super.onResume();
    AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
  }
  
  public void setInfo(TLRPC.ChatFull paramChatFull)
  {
    if (paramChatFull != null)
    {
      if ((paramChatFull.exported_invite instanceof TLRPC.TL_chatInviteExported)) {
        this.invite = paramChatFull.exported_invite;
      }
    }
    else {
      return;
    }
    generateLink();
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\ChannelEditTypeActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */