package org.telegram.ui.Mihangram.SpecialContacts;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.AndroidUtilities;
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
import org.telegram.ui.Components.LayoutHelper;

public class SpecialContactCell
  extends FrameLayout
{
  private ImageView adminImage;
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private BackupImageView avatarImageView;
  private CheckBox checkBox;
  private int currentDrawable;
  private CharSequence currentName;
  private TLObject currentObject;
  private TLRPC.User currentUser;
  private CharSequence currrntStatus;
  private ImageView imageView;
  private TLRPC.FileLocation lastAvatar;
  private String lastName;
  private int lastStatus;
  private SimpleTextView nameTextView;
  private int statusColor = -5723992;
  private int statusOnlineColor = -12876608;
  private SimpleTextView statusTextView;
  
  public SpecialContactCell(Context paramContext, int paramInt)
  {
    super(paramContext);
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0F));
    Object localObject = this.avatarImageView;
    int i;
    float f1;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label511;
      }
      f1 = 0.0F;
      label78:
      if (!LocaleController.isRTL) {
        break label520;
      }
      f2 = paramInt + 7;
      label91:
      addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      this.nameTextView = new SimpleTextView(paramContext);
      this.nameTextView.setTextColor(-14606047);
      this.nameTextView.setTextSize(17);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label526;
      }
      i = 5;
      label160:
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label532;
      }
      i = 5;
      label185:
      if (!LocaleController.isRTL) {
        break label538;
      }
      f1 = 28.0F;
      label194:
      if (!LocaleController.isRTL) {
        break label547;
      }
      f2 = paramInt + 68;
      label207:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 11.5F, f2, 0.0F));
      this.statusTextView = new SimpleTextView(paramContext);
      this.statusTextView.setTextSize(14);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL) {
        break label554;
      }
      i = 5;
      label266:
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL) {
        break label560;
      }
      i = 5;
      label291:
      if (!LocaleController.isRTL) {
        break label566;
      }
      f1 = 28.0F;
      label300:
      if (!LocaleController.isRTL) {
        break label575;
      }
      f2 = paramInt + 68;
      label313:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 34.5F, f2, 0.0F));
      this.imageView = new ImageView(paramContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      this.imageView.setVisibility(8);
      localObject = this.imageView;
      if (!LocaleController.isRTL) {
        break label582;
      }
      i = 5;
      label382:
      if (!LocaleController.isRTL) {
        break label588;
      }
      f1 = 0.0F;
      label390:
      if (!LocaleController.isRTL) {
        break label594;
      }
      f2 = 16.0F;
      label400:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x10, f1, 0.0F, f2, 0.0F));
      this.checkBox = new CheckBox(paramContext, 2130838105);
      this.checkBox.setVisibility(4);
      paramContext = this.checkBox;
      if (!LocaleController.isRTL) {
        break label600;
      }
      i = j;
      label460:
      if (!LocaleController.isRTL) {
        break label606;
      }
      f1 = 0.0F;
      label468:
      if (!LocaleController.isRTL) {
        break label615;
      }
    }
    label511:
    label520:
    label526:
    label532:
    label538:
    label547:
    label554:
    label560:
    label566:
    label575:
    label582:
    label588:
    label594:
    label600:
    label606:
    label615:
    for (float f2 = paramInt + 37;; f2 = 0.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(22, 22.0F, i | 0x30, f1, 38.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      f1 = paramInt + 7;
      break label78;
      f2 = 0.0F;
      break label91;
      i = 3;
      break label160;
      i = 3;
      break label185;
      f1 = paramInt + 68;
      break label194;
      f2 = 28.0F;
      break label207;
      i = 3;
      break label266;
      i = 3;
      break label291;
      f1 = paramInt + 68;
      break label300;
      f2 = 28.0F;
      break label313;
      i = 3;
      break label382;
      f1 = 16.0F;
      break label390;
      f2 = 0.0F;
      break label400;
      i = 3;
      break label460;
      f1 = paramInt + 37;
      break label468;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0F), 1073741824));
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
  }
  
  public void setData(TLRPC.User paramUser)
  {
    if (paramUser == null)
    {
      this.currrntStatus = null;
      this.currentName = null;
      this.currentObject = null;
      this.nameTextView.setText("");
      this.statusTextView.setText("");
      this.avatarImageView.setImageDrawable(null);
    }
    this.currrntStatus = null;
    this.currentName = null;
    this.currentObject = paramUser;
    this.currentDrawable = 0;
    update(0);
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
        break label487;
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
                break label533;
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
          break label563;
        }
        this.lastName = null;
        this.nameTextView.setText(this.currentName);
        if (this.currrntStatus == null) {
          break label629;
        }
        this.statusTextView.setTextColor(this.statusColor);
        this.statusTextView.setText(this.currrntStatus);
        label402:
        if (((this.imageView.getVisibility() == 0) && (this.currentDrawable == 0)) || ((this.imageView.getVisibility() == 8) && (this.currentDrawable != 0)))
        {
          localObject1 = this.imageView;
          if (this.currentDrawable != 0) {
            break label828;
          }
        }
      }
    }
    label487:
    label533:
    label563:
    label629:
    label828:
    for (paramInt = m;; paramInt = 0)
    {
      ((ImageView)localObject1).setVisibility(paramInt);
      this.imageView.setImageResource(this.currentDrawable);
      this.avatarImageView.setImage(localFileLocation, "50_50", this.avatarDrawable);
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


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\SpecialContacts\SpecialContactCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */