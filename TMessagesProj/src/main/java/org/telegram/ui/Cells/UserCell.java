package org.telegram.ui.Cells;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Mihangram.Theming.MihanTheme;

public class UserCell
  extends FrameLayout
{
  private ImageView adminImage;
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private BackupImageView avatarImageView;
  private CheckBox checkBox;
  private CheckBoxSquare checkBoxBig;
  private ImageView creatorImage;
  private int currentDrawable;
  private CharSequence currentName;
  private TLObject currentObject;
  private CharSequence currrntStatus;
  private ImageView hasMyPhoneImageView;
  private ImageView imageView;
  private TLRPC.FileLocation lastAvatar;
  private String lastName;
  private int lastStatus;
  private SimpleTextView nameTextView;
  private int statusColor = -5723992;
  private int statusOnlineColor = -12876608;
  private SimpleTextView statusTextView;
  
  public UserCell(Context paramContext, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramContext);
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0F));
    Object localObject = this.avatarImageView;
    int i;
    float f1;
    label76:
    float f2;
    label89:
    label159:
    label184:
    int j;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label763;
      }
      f1 = 0.0F;
      if (!LocaleController.isRTL) {
        break label773;
      }
      f2 = paramInt1 + 7;
      addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      this.nameTextView = new SimpleTextView(paramContext);
      this.nameTextView.setTextColor(-14606047);
      this.nameTextView.setTextSize(17);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label779;
      }
      i = 5;
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label785;
      }
      i = 5;
      if (!LocaleController.isRTL) {
        break label797;
      }
      if (paramInt2 != 2) {
        break label791;
      }
      j = 18;
      label199:
      f1 = j + 28;
      label207:
      if (!LocaleController.isRTL) {
        break label807;
      }
      f2 = paramInt1 + 68;
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 11.5F, f2, 0.0F));
      this.statusTextView = new SimpleTextView(paramContext);
      this.statusTextView.setTextSize(14);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL) {
        break label833;
      }
      i = 5;
      label280:
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL) {
        break label839;
      }
      i = 5;
      label305:
      if (!LocaleController.isRTL) {
        break label845;
      }
      f1 = 28.0F;
      label315:
      if (!LocaleController.isRTL) {
        break label855;
      }
      f2 = paramInt1 + 68;
      label328:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 34.5F, f2, 0.0F));
      this.imageView = new ImageView(paramContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      this.imageView.setVisibility(8);
      localObject = this.imageView;
      if (!LocaleController.isRTL) {
        break label862;
      }
      i = 5;
      label398:
      if (!LocaleController.isRTL) {
        break label868;
      }
      f1 = 0.0F;
      label407:
      if (!LocaleController.isRTL) {
        break label875;
      }
      f2 = 16.0F;
      label417:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x10, f1, 0.0F, f2, 0.0F));
      this.hasMyPhoneImageView = new ImageView(paramContext);
      this.hasMyPhoneImageView.setScaleType(ImageView.ScaleType.CENTER);
      this.hasMyPhoneImageView.setImageResource(2130837971);
      localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
      i = ((SharedPreferences)localObject).getInt("theme_contact_list_ncolor", MihanTheme.getDialogNameColor((SharedPreferences)localObject));
      MihanTheme.setColorFilter(this.hasMyPhoneImageView.getDrawable(), i);
      this.hasMyPhoneImageView.setVisibility(8);
      localObject = this.hasMyPhoneImageView;
      if (!LocaleController.isRTL) {
        break label881;
      }
      i = 3;
      label535:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x10, 16.0F, 0.0F, 16.0F, 0.0F));
      if (paramInt2 != 2) {
        break label905;
      }
      this.checkBoxBig = new CheckBoxSquare(paramContext);
      localObject = this.checkBoxBig;
      if (!LocaleController.isRTL) {
        break label887;
      }
      paramInt1 = 3;
      label590:
      if (!LocaleController.isRTL) {
        break label892;
      }
      f1 = 19.0F;
      label600:
      if (!LocaleController.isRTL) {
        break label898;
      }
      f2 = 0.0F;
      label609:
      addView((View)localObject, LayoutHelper.createFrame(18, 18.0F, paramInt1 | 0x10, f1, 0.0F, f2, 0.0F));
      label632:
      if (paramBoolean)
      {
        this.creatorImage = new ImageView(paramContext);
        this.creatorImage.setImageResource(2130837905);
        localObject = this.creatorImage;
        if (!LocaleController.isRTL) {
          break label1016;
        }
        paramInt1 = 5;
        label672:
        addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, paramInt1 | 0x10, 16.0F, 0.0F, 16.0F, 0.0F));
      }
      if (paramBoolean)
      {
        this.adminImage = new ImageView(paramContext);
        this.adminImage.setImageResource(2130837904);
        paramContext = this.adminImage;
        if (!LocaleController.isRTL) {
          break label1021;
        }
      }
    }
    label763:
    label773:
    label779:
    label785:
    label791:
    label797:
    label807:
    label833:
    label839:
    label845:
    label855:
    label862:
    label868:
    label875:
    label881:
    label887:
    label892:
    label898:
    label905:
    label946:
    label955:
    label1000:
    label1010:
    label1016:
    label1021:
    for (paramInt1 = 5;; paramInt1 = 3)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, paramInt1 | 0x10, 16.0F, 0.0F, 16.0F, 0.0F));
      return;
      i = 3;
      break;
      f1 = paramInt1 + 7;
      break label76;
      f2 = 0.0F;
      break label89;
      i = 3;
      break label159;
      i = 3;
      break label184;
      j = 0;
      break label199;
      f1 = paramInt1 + 68;
      break label207;
      if (paramInt2 == 2) {}
      for (j = 18;; j = 0)
      {
        f2 = j + 28;
        break;
      }
      i = 3;
      break label280;
      i = 3;
      break label305;
      f1 = paramInt1 + 68;
      break label315;
      f2 = 28.0F;
      break label328;
      i = 3;
      break label398;
      f1 = 16.0F;
      break label407;
      f2 = 0.0F;
      break label417;
      i = 5;
      break label535;
      paramInt1 = 5;
      break label590;
      f1 = 0.0F;
      break label600;
      f2 = 19.0F;
      break label609;
      if (paramInt2 != 1) {
        break label632;
      }
      this.checkBox = new CheckBox(paramContext, 2130838105);
      this.checkBox.setVisibility(4);
      localObject = this.checkBox;
      if (LocaleController.isRTL)
      {
        paramInt2 = 5;
        if (!LocaleController.isRTL) {
          break label1000;
        }
        f1 = 0.0F;
        if (!LocaleController.isRTL) {
          break label1010;
        }
      }
      for (f2 = paramInt1 + 37;; f2 = 0.0F)
      {
        addView((View)localObject, LayoutHelper.createFrame(22, 22.0F, paramInt2 | 0x30, f1, 38.0F, f2, 0.0F));
        break;
        paramInt2 = 3;
        break label946;
        f1 = paramInt1 + 37;
        break label955;
      }
      paramInt1 = 3;
      break label672;
    }
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0F), 1073741824));
  }
  
  public void setCheckDisabled(boolean paramBoolean)
  {
    if (this.checkBoxBig != null) {
      this.checkBoxBig.setDisabled(paramBoolean);
    }
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.checkBox != null)
    {
      if (this.checkBox.getVisibility() != 0) {
        this.checkBox.setVisibility(0);
      }
      this.checkBox.setChecked(paramBoolean1, paramBoolean2);
    }
    while (this.checkBoxBig == null) {
      return;
    }
    if (this.checkBoxBig.getVisibility() != 0) {
      this.checkBoxBig.setVisibility(0);
    }
    this.checkBoxBig.setChecked(paramBoolean1, paramBoolean2);
  }
  
  public void setData(TLObject paramTLObject, CharSequence paramCharSequence1, CharSequence paramCharSequence2, int paramInt)
  {
    if (paramTLObject == null)
    {
      this.currrntStatus = null;
      this.currentName = null;
      this.currentObject = null;
      this.nameTextView.setText("");
      this.statusTextView.setText("");
      this.avatarImageView.setImageDrawable(null);
      return;
    }
    if (ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getBoolean("mutual_contact", false)) {
      if (((TLRPC.User)paramTLObject).mutual_contact) {
        this.hasMyPhoneImageView.setVisibility(0);
      }
    }
    for (;;)
    {
      this.currrntStatus = paramCharSequence2;
      this.currentName = paramCharSequence1;
      this.currentObject = paramTLObject;
      this.currentDrawable = paramInt;
      update(0);
      return;
      this.hasMyPhoneImageView.setVisibility(8);
      continue;
      this.hasMyPhoneImageView.setVisibility(8);
    }
  }
  
  public void setIsAdmin(boolean paramBoolean)
  {
    if (this.adminImage == null) {
      return;
    }
    Object localObject = this.adminImage;
    int i;
    if (paramBoolean)
    {
      i = 0;
      ((ImageView)localObject).setVisibility(i);
      localObject = this.nameTextView;
      if (LocaleController.isRTL) {
        break label72;
      }
      i = AndroidUtilities.dp(16.0F);
      label44:
      if (LocaleController.isRTL) {
        break label77;
      }
    }
    label72:
    label77:
    for (int j = AndroidUtilities.dp(16.0F);; j = 0)
    {
      ((SimpleTextView)localObject).setPadding(i, 0, j, 0);
      return;
      i = 8;
      break;
      i = 0;
      break label44;
    }
  }
  
  public void setIsCreator(boolean paramBoolean)
  {
    if (this.creatorImage == null) {
      return;
    }
    Object localObject = this.creatorImage;
    int i;
    if (paramBoolean)
    {
      i = 0;
      ((ImageView)localObject).setVisibility(i);
      localObject = this.nameTextView;
      if (LocaleController.isRTL) {
        break label72;
      }
      i = AndroidUtilities.dp(16.0F);
      label44:
      if (LocaleController.isRTL) {
        break label77;
      }
    }
    label72:
    label77:
    for (int j = AndroidUtilities.dp(16.0F);; j = 0)
    {
      ((SimpleTextView)localObject).setPadding(i, 0, j, 0);
      return;
      i = 8;
      break;
      i = 0;
      break label44;
    }
  }
  
  public void setNameTextColor(int paramInt)
  {
    this.nameTextView.setTextColor(paramInt);
  }
  
  public void setStatusColors(int paramInt1, int paramInt2)
  {
    this.statusColor = paramInt1;
    this.statusOnlineColor = paramInt2;
  }
  
  public void update(int paramInt)
  {
    int m = 8;
    if (this.currentObject == null) {}
    TLRPC.FileLocation localFileLocation;
    Object localObject1;
    Object localObject2;
    TLRPC.User localUser2;
    TLRPC.Chat localChat2;
    TLRPC.Chat localChat1;
    TLRPC.User localUser1;
    int i;
    label173:
    label288:
    do
    {
      return;
      localFileLocation = null;
      localObject1 = null;
      localObject2 = null;
      localUser2 = null;
      localChat2 = null;
      if (!(this.currentObject instanceof TLRPC.User)) {
        break label528;
      }
      localUser2 = (TLRPC.User)this.currentObject;
      localChat1 = localChat2;
      localUser1 = localUser2;
      if (localUser2.photo != null)
      {
        localFileLocation = localUser2.photo.photo_small;
        localUser1 = localUser2;
        localChat1 = localChat2;
      }
      if (paramInt == 0) {
        break;
      }
      int j = 0;
      i = j;
      if ((paramInt & 0x2) != 0)
      {
        if ((this.lastAvatar == null) || (localFileLocation != null))
        {
          i = j;
          if (this.lastAvatar != null) {
            break label173;
          }
          i = j;
          if (localFileLocation == null) {
            break label173;
          }
          i = j;
          if (this.lastAvatar == null) {
            break label173;
          }
          i = j;
          if (localFileLocation == null) {
            break label173;
          }
          if (this.lastAvatar.volume_id == localFileLocation.volume_id)
          {
            i = j;
            if (this.lastAvatar.local_id == localFileLocation.local_id) {
              break label173;
            }
          }
        }
        i = 1;
      }
      j = i;
      if (localUser1 != null)
      {
        j = i;
        if (i == 0)
        {
          j = i;
          if ((paramInt & 0x4) != 0)
          {
            int k = 0;
            if (localUser1.status != null) {
              k = localUser1.status.expires;
            }
            j = i;
            if (k != this.lastStatus) {
              j = 1;
            }
          }
        }
      }
      i = j;
      localObject1 = localObject2;
      if (j == 0)
      {
        i = j;
        localObject1 = localObject2;
        if (this.currentName == null)
        {
          i = j;
          localObject1 = localObject2;
          if (this.lastName != null)
          {
            i = j;
            localObject1 = localObject2;
            if ((paramInt & 0x1) != 0)
            {
              if (localUser1 == null) {
                break label574;
              }
              localObject2 = UserObject.getUserName(localUser1);
              i = j;
              localObject1 = localObject2;
              if (!((String)localObject2).equals(this.lastName))
              {
                i = 1;
                localObject1 = localObject2;
              }
            }
          }
        }
      }
    } while (i == 0);
    if (localUser1 != null)
    {
      this.avatarDrawable.setInfo(localUser1);
      if (localUser1.status != null)
      {
        this.lastStatus = localUser1.status.expires;
        label350:
        if (this.currentName == null) {
          break label604;
        }
        this.lastName = null;
        this.nameTextView.setText(this.currentName);
        if (this.currrntStatus == null) {
          break label670;
        }
        this.statusTextView.setTextColor(this.statusColor);
        this.statusTextView.setText(this.currrntStatus);
        label402:
        if (((this.imageView.getVisibility() == 0) && (this.currentDrawable == 0)) || ((this.imageView.getVisibility() == 8) && (this.currentDrawable != 0)))
        {
          localObject1 = this.imageView;
          if (this.currentDrawable != 0) {
            break label869;
          }
        }
      }
    }
    label528:
    label574:
    label604:
    label670:
    label869:
    for (paramInt = m;; paramInt = 0)
    {
      ((ImageView)localObject1).setVisibility(paramInt);
      this.imageView.setImageResource(this.currentDrawable);
      this.avatarImageView.setImage(localFileLocation, "50_50", this.avatarDrawable);
      paramInt = AndroidUtilities.dp(ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0).getInt("theme_contact_avatar_radius", 32));
      this.avatarImageView.setRoundRadius(paramInt);
      this.avatarDrawable.setRoundRadius(paramInt);
      return;
      localChat2 = (TLRPC.Chat)this.currentObject;
      localChat1 = localChat2;
      localUser1 = localUser2;
      if (localChat2.photo == null) {
        break;
      }
      localFileLocation = localChat2.photo.photo_small;
      localChat1 = localChat2;
      localUser1 = localUser2;
      break;
      localObject2 = localChat1.title;
      break label288;
      this.lastStatus = 0;
      break label350;
      this.avatarDrawable.setInfo(localChat1);
      break label350;
      if (localUser1 != null)
      {
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = UserObject.getUserName(localUser1);
        }
      }
      for (this.lastName = ((String)localObject2);; this.lastName = ((String)localObject2))
      {
        this.nameTextView.setText(this.lastName);
        break;
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = localChat1.title;
        }
      }
      if (localUser1 == null) {
        break label402;
      }
      if (localUser1.bot)
      {
        this.statusTextView.setTextColor(this.statusColor);
        if ((localUser1.bot_chat_history) || ((this.adminImage != null) && (this.adminImage.getVisibility() == 0)))
        {
          this.statusTextView.setText(LocaleController.getString("BotStatusRead", 2131165416));
          break label402;
        }
        this.statusTextView.setText(LocaleController.getString("BotStatusCantRead", 2131165415));
        break label402;
      }
      if ((localUser1.id == UserConfig.getClientUserId()) || ((localUser1.status != null) && (localUser1.status.expires > ConnectionsManager.getInstance().getCurrentTime())) || (MessagesController.getInstance().onlinePrivacy.containsKey(Integer.valueOf(localUser1.id))))
      {
        this.statusTextView.setTextColor(this.statusOnlineColor);
        this.statusTextView.setText(LocaleController.getString("Online", 2131166113));
        break label402;
      }
      this.statusTextView.setTextColor(this.statusColor);
      this.statusTextView.setText(LocaleController.formatUserStatus(localUser1));
      break label402;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Cells\UserCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */