package org.telegram.ui.Supergram.UserChanges;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.Calendar;
import java.util.Date;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Supergram.Theming.MihanTheme;
import org.telegram.ui.Supergram.UserChanges.mobo.bd;

public class UpdateCell
  extends FrameLayout
{
  private AvatarDrawable avatarDrawable;
  private BackupImageView avatarImageView;
  private CheckBox checkBox;
  private TLRPC.User currentUser;
  private SimpleTextView dateTextView;
  private ImageView imageView;
  private String lastName;
  private SimpleTextView nameTextView;
  private int newValueColor;
  private SimpleTextView newValueTextView;
  private int oldValueColor;
  private SimpleTextView oldValueTextView;
  private UpdateModel updateModel;
  
  @SuppressLint({"RtlHardcoded"})
  public UpdateCell(Context paramContext, int paramInt)
  {
    super(paramContext);
    int j = 5;
    this.currentUser = null;
    this.lastName = null;
    this.oldValueColor = -5723992;
    this.newValueColor = -12876608;
    this.avatarDrawable = new AvatarDrawable();
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0F));
    boolean bool = LocaleController.isRTL;
    Object localObject = this.avatarImageView;
    int i;
    float f1;
    if (bool)
    {
      i = 5;
      if (!bool) {
        break label862;
      }
      f1 = 0.0F;
      label91:
      if (!bool) {
        break label871;
      }
      f2 = paramInt + 7;
      label103:
      addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      this.nameTextView = new SimpleTextView(paramContext);
      localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
      i = ((SharedPreferences)localObject).getInt("theme_contact_list_ncolor", MihanTheme.getDialogNameColor((SharedPreferences)localObject));
      int m = ((SharedPreferences)localObject).getInt("theme_contact_list_scolor", MihanTheme.getDialogMessageColor((SharedPreferences)localObject));
      int n = ((SharedPreferences)localObject).getInt("theme_contact_list_oscolor", MihanTheme.getDialogMessageColor((SharedPreferences)localObject));
      int k = ((SharedPreferences)localObject).getInt("theme_dialog_tik_color", -12080585);
      this.nameTextView.setTextColor(i);
      this.nameTextView.setTextSize(17);
      localObject = this.nameTextView;
      if (!bool) {
        break label877;
      }
      i = 5;
      label243:
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      localObject = this.nameTextView;
      if (!bool) {
        break label883;
      }
      i = 5;
      label279:
      if (!bool) {
        break label889;
      }
      f1 = 28.0F;
      label287:
      if (!bool) {
        break label898;
      }
      f2 = paramInt + 68;
      label299:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 11.5F, f2, 0.0F));
      this.newValueTextView = new SimpleTextView(paramContext);
      this.newValueTextView.setTextColor(n);
      this.newValueTextView.setTextSize(14);
      localObject = this.newValueTextView;
      if (!bool) {
        break label905;
      }
      i = 5;
      label366:
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      this.newValueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      localObject = this.newValueTextView;
      if (!bool) {
        break label911;
      }
      i = 5;
      label402:
      if (!bool) {
        break label917;
      }
      f1 = 28.0F;
      label410:
      if (!bool) {
        break label926;
      }
      f2 = paramInt + 68;
      label422:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 36.0F, f2, 0.0F));
      this.oldValueTextView = new SimpleTextView(paramContext);
      this.oldValueTextView.setTextColor(m);
      this.oldValueTextView.setTextSize(14);
      localObject = this.oldValueTextView;
      if (!bool) {
        break label933;
      }
      i = 5;
      label489:
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      this.oldValueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      localObject = this.oldValueTextView;
      if (!bool) {
        break label939;
      }
      i = 5;
      label525:
      if (!bool) {
        break label945;
      }
      f1 = 28.0F;
      label533:
      if (!bool) {
        break label954;
      }
      f2 = paramInt + 68;
      label545:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 58.0F, f2, 0.0F));
      this.dateTextView = new SimpleTextView(paramContext);
      this.dateTextView.setTextColor(k);
      this.dateTextView.setTextSize(14);
      localObject = this.dateTextView;
      if (!bool) {
        break label961;
      }
      i = 3;
      label612:
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      this.dateTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      localObject = this.dateTextView;
      if (!bool) {
        break label967;
      }
      i = 3;
      label648:
      if (!bool) {
        break label973;
      }
      f1 = paramInt + 5;
      label658:
      if (!bool) {
        break label979;
      }
      f2 = 28.0F;
      label667:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 77.5F, f2, 0.0F));
      this.imageView = new ImageView(paramContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      this.imageView.setVisibility(8);
      localObject = this.imageView;
      if (!bool) {
        break label989;
      }
      i = 5;
      label735:
      if (!bool) {
        break label995;
      }
      f1 = 0.0F;
      label742:
      if (!bool) {
        break label1001;
      }
      f2 = 16.0F;
      label751:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x10, f1, 0.0F, f2, 0.0F));
      this.checkBox = new CheckBox(paramContext, 2130838105);
      this.checkBox.setVisibility(4);
      paramContext = this.checkBox;
      i = j;
      if (!bool) {
        i = 3;
      }
      if (!bool) {
        break label1007;
      }
      f1 = 0.0F;
      label820:
      if (!bool) {
        break label1016;
      }
    }
    label862:
    label871:
    label877:
    label883:
    label889:
    label898:
    label905:
    label911:
    label917:
    label926:
    label933:
    label939:
    label945:
    label954:
    label961:
    label967:
    label973:
    label979:
    label989:
    label995:
    label1001:
    label1007:
    label1016:
    for (float f2 = paramInt + 37;; f2 = 0.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(22, 22.0F, i | 0x30, f1, 38.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      f1 = paramInt + 7;
      break label91;
      f2 = 0.0F;
      break label103;
      i = 3;
      break label243;
      i = 3;
      break label279;
      f1 = paramInt + 68;
      break label287;
      f2 = 28.0F;
      break label299;
      i = 3;
      break label366;
      i = 3;
      break label402;
      f1 = paramInt + 68;
      break label410;
      f2 = 28.0F;
      break label422;
      i = 3;
      break label489;
      i = 3;
      break label525;
      f1 = paramInt + 68;
      break label533;
      f2 = 28.0F;
      break label545;
      i = 5;
      break label612;
      i = 5;
      break label648;
      f1 = 28.0F;
      break label658;
      f2 = paramInt + 10;
      break label667;
      i = 3;
      break label735;
      f1 = 16.0F;
      break label742;
      f2 = 0.0F;
      break label751;
      f1 = paramInt + 37;
      break label820;
    }
  }
  
  public BackupImageView getAvatarImageView()
  {
    return this.avatarImageView;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(104.0F), 1073741824));
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.checkBox.getVisibility() != 0) {
      this.checkBox.setVisibility(0);
    }
    this.checkBox.setChecked(paramBoolean1, paramBoolean2);
  }
  
  public void setData(UpdateModel paramUpdateModel)
  {
    TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(paramUpdateModel.getUserId()));
    if (localUser == null)
    {
      this.nameTextView.setText("");
      this.avatarImageView.setImageDrawable(null);
    }
    this.currentUser = localUser;
    this.updateModel = paramUpdateModel;
    update();
  }
  
  public void update()
  {
    Object localObject;
    if (this.currentUser != null)
    {
      localObject = null;
      if (this.currentUser.photo != null) {
        localObject = this.currentUser.photo.photo_small;
      }
      this.avatarDrawable.setInfo(this.currentUser);
      this.avatarImageView.setImage((TLObject)localObject, "50_50", this.avatarDrawable);
      this.lastName = ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name);
      this.nameTextView.setText(this.lastName);
    }
    this.oldValueTextView.setTextColor(this.oldValueColor);
    this.newValueTextView.setTextColor(this.newValueColor);
    if (this.updateModel.getType() == 1)
    {
      this.oldValueTextView.setText("");
      if (this.updateModel.getNewValue().equals("1")) {
        this.newValueTextView.setText(getContext().getString(2131166832));
      }
    }
    for (;;)
    {
      localObject = Long.valueOf(Long.parseLong(this.updateModel.getChangeDate()));
      if (((Long)localObject).longValue() != 0L)
      {
        localObject = new Date(((Long)localObject).longValue());
        String str = bd.a((Date)localObject);
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTime((Date)localObject);
        this.dateTextView.setText(str + " - " + bd.a(localCalendar.get(11), 2) + ":" + bd.a(localCalendar.get(12), 2));
      }
      return;
      this.newValueTextView.setText(getContext().getString(2131166831));
      continue;
      if (this.updateModel.getType() == 2)
      {
        this.oldValueTextView.setText(getContext().getString(2131166838) + " " + this.updateModel.getOldValue().replace(";;;", " - "));
        this.newValueTextView.setText(getContext().getString(2131166835) + " " + this.updateModel.getNewValue().replace(";;;", " - "));
      }
      else if (this.updateModel.getType() == 3)
      {
        this.oldValueTextView.setText("");
        this.newValueTextView.setText(getContext().getString(2131166829));
      }
      else if (this.updateModel.getType() == 4)
      {
        this.oldValueTextView.setText(getContext().getString(2131166839) + " " + this.updateModel.getOldValue());
        this.newValueTextView.setText(getContext().getString(2131166836) + " " + this.updateModel.getNewValue());
      }
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\UserChanges\UpdateCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */