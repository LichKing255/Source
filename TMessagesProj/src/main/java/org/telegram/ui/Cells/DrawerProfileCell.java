package org.telegram.ui.Cells;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Mihangram.Theming.MihanTheme;

public class DrawerProfileCell
  extends FrameLayout
{
  private BackupImageView avatarImageView;
  private int currentColor;
  private Rect destRect = new Rect();
  private TextView nameTextView;
  private Paint paint = new Paint();
  private TextView phoneTextView;
  private ImageView shadowView;
  private Rect srcRect = new Rect();
  
  public DrawerProfileCell(Context paramContext)
  {
    super(paramContext);
    setBackgroundColor(-10907718);
    this.shadowView = new ImageView(paramContext);
    this.shadowView.setVisibility(4);
    this.shadowView.setScaleType(ImageView.ScaleType.FIT_XY);
    this.shadowView.setImageResource(2130837645);
    addView(this.shadowView, LayoutHelper.createFrame(-1, 70, 83));
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(32.0F));
    addView(this.avatarImageView, LayoutHelper.createFrame(64, 64.0F, 83, 16.0F, 0.0F, 0.0F, 67.0F));
    this.nameTextView = new TextView(paramContext);
    this.nameTextView.setTextColor(-1);
    this.nameTextView.setTextSize(1, 15.0F);
    this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.nameTextView.setLines(1);
    this.nameTextView.setMaxLines(1);
    this.nameTextView.setSingleLine(true);
    this.nameTextView.setGravity(3);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0F, 83, 16.0F, 0.0F, 16.0F, 28.0F));
    this.phoneTextView = new TextView(paramContext);
    this.phoneTextView.setTextColor(-4004353);
    this.phoneTextView.setTextSize(1, 13.0F);
    this.phoneTextView.setLines(1);
    this.phoneTextView.setMaxLines(1);
    this.phoneTextView.setSingleLine(true);
    this.phoneTextView.setGravity(3);
    this.phoneTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    addView(this.phoneTextView, LayoutHelper.createFrame(-1, -2.0F, 83, 16.0F, 0.0F, 16.0F, 9.0F));
    updateColors();
  }
  
  private void updateColors()
  {
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    int i = ((SharedPreferences)localObject).getInt("theme_drawer_header_color", MihanTheme.getThemeColor((SharedPreferences)localObject));
    int j = ((SharedPreferences)localObject).getInt("theme_drawer_header_gradient", MihanTheme.getActionBarGradientFlag((SharedPreferences)localObject));
    int k = ((SharedPreferences)localObject).getInt("theme_drawer_header_gcolor", MihanTheme.getActionBarGradientColor((SharedPreferences)localObject));
    if (j != 0)
    {
      setBackgroundDrawable(MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j)));
      this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(((SharedPreferences)localObject).getInt("theme_drawer_avatar_radius", 32)));
      i = ((SharedPreferences)localObject).getInt("theme_drawer_name_color", MihanTheme.getActionBarTitleColor((SharedPreferences)localObject));
      j = ((SharedPreferences)localObject).getInt("theme_drawer_phone_color", MihanTheme.getActionBarTitleColor((SharedPreferences)localObject));
      this.nameTextView.setTextColor(i);
      this.phoneTextView.setTextColor(j);
      if (!((SharedPreferences)localObject).getBoolean("theme_drawer_center_info", false)) {
        break label419;
      }
      localObject = (FrameLayout.LayoutParams)this.avatarImageView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).width = AndroidUtilities.dp(64.0F);
      ((FrameLayout.LayoutParams)localObject).height = AndroidUtilities.dp(64.0F);
      ((FrameLayout.LayoutParams)localObject).gravity = 81;
      ((FrameLayout.LayoutParams)localObject).leftMargin = AndroidUtilities.dp(0.0F);
      ((FrameLayout.LayoutParams)localObject).bottomMargin = AndroidUtilities.dp(67.0F);
      this.avatarImageView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      this.nameTextView.setGravity(17);
      localObject = (FrameLayout.LayoutParams)this.nameTextView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).width = -1;
      ((FrameLayout.LayoutParams)localObject).height = -2;
      ((FrameLayout.LayoutParams)localObject).gravity = 81;
      ((FrameLayout.LayoutParams)localObject).leftMargin = AndroidUtilities.dp(0.0F);
      ((FrameLayout.LayoutParams)localObject).bottomMargin = AndroidUtilities.dp(28.0F);
      ((FrameLayout.LayoutParams)localObject).rightMargin = AndroidUtilities.dp(0.0F);
      this.nameTextView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      this.phoneTextView.setGravity(17);
      localObject = (FrameLayout.LayoutParams)this.phoneTextView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).width = -1;
      ((FrameLayout.LayoutParams)localObject).height = -2;
      ((FrameLayout.LayoutParams)localObject).gravity = 81;
      ((FrameLayout.LayoutParams)localObject).leftMargin = AndroidUtilities.dp(0.0F);
      ((FrameLayout.LayoutParams)localObject).bottomMargin = AndroidUtilities.dp(9.0F);
      ((FrameLayout.LayoutParams)localObject).rightMargin = AndroidUtilities.dp(0.0F);
      this.phoneTextView.setLayoutParams((ViewGroup.LayoutParams)localObject);
    }
    for (;;)
    {
      if (!ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getBoolean("hide_phone", false)) {
        break label648;
      }
      this.phoneTextView.setVisibility(8);
      return;
      setBackgroundColor(i);
      break;
      label419:
      localObject = (FrameLayout.LayoutParams)this.avatarImageView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).width = AndroidUtilities.dp(64.0F);
      ((FrameLayout.LayoutParams)localObject).height = AndroidUtilities.dp(64.0F);
      ((FrameLayout.LayoutParams)localObject).gravity = 83;
      ((FrameLayout.LayoutParams)localObject).leftMargin = AndroidUtilities.dp(16.0F);
      ((FrameLayout.LayoutParams)localObject).bottomMargin = AndroidUtilities.dp(67.0F);
      this.avatarImageView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      this.nameTextView.setGravity(3);
      localObject = (FrameLayout.LayoutParams)this.nameTextView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).width = -1;
      ((FrameLayout.LayoutParams)localObject).height = -2;
      ((FrameLayout.LayoutParams)localObject).gravity = 83;
      ((FrameLayout.LayoutParams)localObject).leftMargin = AndroidUtilities.dp(16.0F);
      ((FrameLayout.LayoutParams)localObject).bottomMargin = AndroidUtilities.dp(28.0F);
      ((FrameLayout.LayoutParams)localObject).rightMargin = AndroidUtilities.dp(16.0F);
      this.nameTextView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      this.phoneTextView.setGravity(3);
      localObject = (FrameLayout.LayoutParams)this.phoneTextView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).width = -1;
      ((FrameLayout.LayoutParams)localObject).height = -2;
      ((FrameLayout.LayoutParams)localObject).gravity = 83;
      ((FrameLayout.LayoutParams)localObject).leftMargin = AndroidUtilities.dp(16.0F);
      ((FrameLayout.LayoutParams)localObject).bottomMargin = AndroidUtilities.dp(9.0F);
      ((FrameLayout.LayoutParams)localObject).rightMargin = AndroidUtilities.dp(16.0F);
      this.phoneTextView.setLayoutParams((ViewGroup.LayoutParams)localObject);
    }
    label648:
    this.phoneTextView.setVisibility(0);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    Object localObject = ApplicationLoader.getCachedWallpaper();
    int i = ApplicationLoader.getServiceMessageColor();
    if (this.currentColor != i)
    {
      this.currentColor = i;
      this.shadowView.getDrawable().setColorFilter(new PorterDuffColorFilter(0xFF000000 | i, PorterDuff.Mode.MULTIPLY));
    }
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    if ((ApplicationLoader.isCustomTheme()) && (localObject != null)) {
      if (!localSharedPreferences.getBoolean("theme_drawer_hide_cbg", false))
      {
        this.phoneTextView.setTextColor(-1);
        if (localSharedPreferences.getBoolean("theme_drawer_hide_cbgs", false))
        {
          this.shadowView.setVisibility(4);
          if (!(localObject instanceof ColorDrawable)) {
            break label162;
          }
          ((Drawable)localObject).setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
          ((Drawable)localObject).draw(paramCanvas);
        }
      }
    }
    for (;;)
    {
      updateColors();
      return;
      this.shadowView.setVisibility(0);
      break;
      label162:
      if ((localObject instanceof BitmapDrawable))
      {
        localObject = ((BitmapDrawable)localObject).getBitmap();
        float f1 = getMeasuredWidth() / ((Bitmap)localObject).getWidth();
        float f2 = getMeasuredHeight() / ((Bitmap)localObject).getHeight();
        if (f1 < f2) {
          f1 = f2;
        }
        for (;;)
        {
          i = (int)(getMeasuredWidth() / f1);
          int j = (int)(getMeasuredHeight() / f1);
          int k = (((Bitmap)localObject).getWidth() - i) / 2;
          int m = (((Bitmap)localObject).getHeight() - j) / 2;
          this.srcRect.set(k, m, k + i, m + j);
          this.destRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
          paramCanvas.drawBitmap((Bitmap)localObject, this.srcRect, this.destRect, this.paint);
          break;
        }
        this.shadowView.setVisibility(4);
        continue;
        this.shadowView.setVisibility(4);
        this.phoneTextView.setTextColor(-4004353);
        super.onDraw(paramCanvas);
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0F) + AndroidUtilities.statusBarHeight, 1073741824));
      return;
    }
    try
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0F), 1073741824));
      return;
    }
    catch (Exception localException)
    {
      setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(148.0F));
      FileLog.e("tmessages", localException);
    }
  }
  
  public void setUser(TLRPC.User paramUser)
  {
    if (paramUser == null) {
      return;
    }
    TLRPC.FileLocation localFileLocation = null;
    if (paramUser.photo != null) {
      localFileLocation = paramUser.photo.photo_small;
    }
    this.nameTextView.setText(UserObject.getUserName(paramUser));
    this.phoneTextView.setText(PhoneFormat.getInstance().format("+" + paramUser.phone));
    paramUser = new AvatarDrawable(paramUser);
    paramUser.setColor(-11500111);
    this.avatarImageView.setImage(localFileLocation, "50_50", paramUser);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Cells\DrawerProfileCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */