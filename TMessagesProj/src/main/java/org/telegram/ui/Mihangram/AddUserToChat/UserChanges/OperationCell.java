package org.telegram.ui.Supergram.AddUserToChat.UserChanges;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import org.telegram.messenger.AndroidUtilities;
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
import org.telegram.ui.Supergram.SolarCalendar;

public class OperationCell
  extends FrameLayout
{
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private BackupImageView avatarImageView;
  private CheckBox checkBox;
  private SimpleTextView dateTextView;
  private ImageView imageView;
  private String mDate;
  private String mOperation;
  private TLRPC.User mUser = null;
  private SimpleTextView nameTextView;
  private int newValueColor = -12876608;
  private int oldValueColor = -5723992;
  private OperationModel operationModel;
  private SimpleTextView operationTextView;
  
  @SuppressLint({"RtlHardcoded"})
  public OperationCell(Context paramContext, int paramInt)
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
        break label657;
      }
      f1 = 0.0F;
      label83:
      if (!LocaleController.isRTL) {
        break label666;
      }
      f2 = paramInt + 7;
      label96:
      addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      this.nameTextView = new SimpleTextView(paramContext);
      this.nameTextView.setTextColor(-14606047);
      this.nameTextView.setTextSize(17);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label672;
      }
      i = 5;
      label165:
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label678;
      }
      i = 5;
      label202:
      if (!LocaleController.isRTL) {
        break label684;
      }
      f1 = 28.0F;
      label211:
      if (!LocaleController.isRTL) {
        break label693;
      }
      f2 = paramInt + 68;
      label224:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 11.5F, f2, 0.0F));
      this.operationTextView = new SimpleTextView(paramContext);
      this.operationTextView.setTextSize(14);
      localObject = this.operationTextView;
      if (!LocaleController.isRTL) {
        break label700;
      }
      i = 5;
      label283:
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      this.operationTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      localObject = this.operationTextView;
      if (!LocaleController.isRTL) {
        break label706;
      }
      i = 5;
      label320:
      if (!LocaleController.isRTL) {
        break label712;
      }
      f1 = 28.0F;
      label329:
      if (!LocaleController.isRTL) {
        break label721;
      }
      f2 = paramInt + 68;
      label342:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 34.5F, f2, 0.0F));
      this.dateTextView = new SimpleTextView(paramContext);
      this.dateTextView.setTextSize(14);
      localObject = this.dateTextView;
      if (!LocaleController.isRTL) {
        break label728;
      }
      i = 3;
      label401:
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      this.dateTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      localObject = this.dateTextView;
      if (!LocaleController.isRTL) {
        break label734;
      }
      i = 3;
      label438:
      if (!LocaleController.isRTL) {
        break label740;
      }
      f1 = paramInt + 5;
      label449:
      if (!LocaleController.isRTL) {
        break label746;
      }
      f2 = 28.0F;
      label459:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 60.5F, f2, 0.0F));
      this.imageView = new ImageView(paramContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      this.imageView.setVisibility(8);
      localObject = this.imageView;
      if (!LocaleController.isRTL) {
        break label756;
      }
      i = 5;
      label528:
      if (!LocaleController.isRTL) {
        break label762;
      }
      f1 = 0.0F;
      label536:
      if (!LocaleController.isRTL) {
        break label768;
      }
      f2 = 16.0F;
      label546:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x10, f1, 0.0F, f2, 0.0F));
      this.checkBox = new CheckBox(paramContext, 2130838105);
      this.checkBox.setVisibility(4);
      paramContext = this.checkBox;
      if (!LocaleController.isRTL) {
        break label774;
      }
      i = j;
      label606:
      if (!LocaleController.isRTL) {
        break label780;
      }
      f1 = 0.0F;
      label614:
      if (!LocaleController.isRTL) {
        break label789;
      }
    }
    label657:
    label666:
    label672:
    label678:
    label684:
    label693:
    label700:
    label706:
    label712:
    label721:
    label728:
    label734:
    label740:
    label746:
    label756:
    label762:
    label768:
    label774:
    label780:
    label789:
    for (float f2 = paramInt + 37;; f2 = 0.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(22, 22.0F, i | 0x30, f1, 38.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      f1 = paramInt + 7;
      break label83;
      f2 = 0.0F;
      break label96;
      i = 3;
      break label165;
      i = 3;
      break label202;
      f1 = paramInt + 68;
      break label211;
      f2 = 28.0F;
      break label224;
      i = 3;
      break label283;
      i = 3;
      break label320;
      f1 = paramInt + 68;
      break label329;
      f2 = 28.0F;
      break label342;
      i = 5;
      break label401;
      i = 5;
      break label438;
      f1 = 28.0F;
      break label449;
      f2 = paramInt + 10;
      break label459;
      i = 3;
      break label528;
      f1 = 16.0F;
      break label536;
      f2 = 0.0F;
      break label546;
      i = 3;
      break label606;
      f1 = paramInt + 37;
      break label614;
    }
  }
  
  public static String a(int paramInt1, int paramInt2)
  {
    char[] arrayOfChar = new char[paramInt2];
    Arrays.fill(arrayOfChar, '0');
    return new DecimalFormat(String.valueOf(arrayOfChar)).format(paramInt1);
  }
  
  public static String getStringDate(Date paramDate)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTime(paramDate);
    return new SolarCalendar(localCalendar).getShortDesDate();
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
  
  public void setData(OperationModel paramOperationModel)
  {
    if (paramOperationModel == null)
    {
      this.nameTextView.setText("");
      this.avatarImageView.setImageDrawable(null);
    }
    this.mUser = MessagesController.getInstance().getUser(Integer.valueOf(paramOperationModel.getUser()));
    this.mOperation = paramOperationModel.getOperation();
    this.mDate = String.valueOf(paramOperationModel.getDate());
    update();
  }
  
  public void update()
  {
    if (this.mUser == null) {
      return;
    }
    Object localObject = null;
    if (this.mUser.photo != null) {
      localObject = this.mUser.photo.photo_small;
    }
    this.avatarDrawable.setInfo(this.mUser);
    this.avatarImageView.setImage((TLObject)localObject, "50_50", this.avatarDrawable);
    this.nameTextView.setText(ContactsController.formatName(this.mUser.first_name, this.mUser.last_name));
    this.operationTextView.setText(this.mOperation);
    this.operationTextView.setTextColor(this.oldValueColor);
    localObject = new Date(this.mDate);
    getStringDate((Date)localObject);
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTime((Date)localObject);
    this.dateTextView.setText(a(localCalendar.get(11), 2) + ":" + a(localCalendar.get(12), 2));
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\AddUserToChat\UserChanges\OperationCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */