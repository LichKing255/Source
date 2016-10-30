package org.telegram.ui.Mihangram.Theming.Fonts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class FontSettingsCell
  extends FrameLayout
{
  private static Paint paint;
  private int font;
  private boolean needDivider;
  private TextView textView;
  private ImageView valueImageView;
  private TextView valueTextView;
  
  public FontSettingsCell(Context paramContext, int paramInt)
  {
    super(paramContext);
    if (paint == null)
    {
      paint = new Paint();
      paint.setColor(-2500135);
      paint.setStrokeWidth(1.0F);
    }
    this.font = paramInt;
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(-14606047);
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setEllipsize(TextUtils.TruncateAt.END);
    Object localObject = this.textView;
    if (LocaleController.isRTL)
    {
      paramInt = 5;
      ((TextView)localObject).setGravity(paramInt | 0x10);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label464;
      }
      paramInt = 5;
      label145:
      addView((View)localObject, LayoutHelper.createFrame(-1, -1.0F, paramInt | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      switch (this.font)
      {
      default: 
        localObject = Typeface.createFromAsset(paramContext.getAssets(), "fonts/iransans_medium.ttf");
        label247:
        this.textView.setTypeface((Typeface)localObject);
        this.valueTextView = new TextView(paramContext);
        this.valueTextView.setTextColor(-13660983);
        this.valueTextView.setTextSize(1, 16.0F);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        localObject = this.valueTextView;
        if (LocaleController.isRTL)
        {
          paramInt = 3;
          label335:
          ((TextView)localObject).setGravity(paramInt | 0x10);
          localObject = this.valueTextView;
          if (!LocaleController.isRTL) {
            break label650;
          }
          paramInt = 3;
          label358:
          addView((View)localObject, LayoutHelper.createFrame(-2, -1.0F, paramInt | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
          this.valueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          this.valueImageView = new ImageView(paramContext);
          this.valueImageView.setScaleType(ImageView.ScaleType.CENTER);
          this.valueImageView.setVisibility(4);
          paramContext = this.valueImageView;
          if (!LocaleController.isRTL) {
            break label655;
          }
        }
        break;
      }
    }
    label464:
    label650:
    label655:
    for (paramInt = i;; paramInt = 5)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, paramInt | 0x10, 17.0F, 0.0F, 17.0F, 0.0F));
      return;
      paramInt = 3;
      break;
      paramInt = 3;
      break label145;
      localObject = Typeface.DEFAULT;
      break label247;
      localObject = Typeface.createFromAsset(paramContext.getAssets(), "fonts/rmedium.ttf");
      break label247;
      localObject = Typeface.createFromAsset(paramContext.getAssets(), "fonts/iransans_light.ttf");
      break label247;
      localObject = Typeface.createFromAsset(paramContext.getAssets(), "fonts/iransans.ttf");
      break label247;
      localObject = Typeface.createFromAsset(paramContext.getAssets(), "fonts/iransans_medium.ttf");
      break label247;
      localObject = Typeface.createFromAsset(paramContext.getAssets(), "fonts/iransans_bold.ttf");
      break label247;
      localObject = Typeface.createFromAsset(paramContext.getAssets(), "fonts/afsaneh.ttf");
      break label247;
      localObject = Typeface.createFromAsset(paramContext.getAssets(), "fonts/dastnevis.ttf");
      break label247;
      localObject = Typeface.createFromAsset(paramContext.getAssets(), "fonts/hama.ttf");
      break label247;
      localObject = Typeface.createFromAsset(paramContext.getAssets(), "fonts/morvarid.ttf");
      break label247;
      localObject = Typeface.createFromAsset(paramContext.getAssets(), "fonts/yekan.ttf");
      break label247;
      localObject = Typeface.createFromAsset(paramContext.getAssets(), "fonts/titr.ttf");
      break label247;
      localObject = Typeface.createFromAsset(paramContext.getAssets(), "fonts/koodak.ttf");
      break label247;
      paramInt = 5;
      break label335;
      paramInt = 5;
      break label358;
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.getSize(paramInt1);
    int i = AndroidUtilities.dp(48.0F);
    if (this.needDivider)
    {
      paramInt1 = 1;
      setMeasuredDimension(paramInt2, paramInt1 + i);
      paramInt1 = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - AndroidUtilities.dp(34.0F);
      paramInt2 = paramInt1 / 2;
      if (this.valueImageView.getVisibility() == 0) {
        this.valueImageView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
      }
      if (this.valueTextView.getVisibility() != 0) {
        break label161;
      }
      this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
      paramInt1 = paramInt1 - this.valueTextView.getMeasuredWidth() - AndroidUtilities.dp(8.0F);
    }
    label161:
    for (;;)
    {
      this.textView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
      return;
      paramInt1 = 0;
      break;
    }
  }
  
  public void setText(String paramString, boolean paramBoolean)
  {
    this.textView.setText(paramString);
    this.valueTextView.setVisibility(4);
    this.valueImageView.setVisibility(4);
    this.needDivider = paramBoolean;
    if (!paramBoolean) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      setWillNotDraw(paramBoolean);
      return;
    }
  }
  
  public void setTextAndIcon(String paramString, int paramInt, boolean paramBoolean)
  {
    boolean bool = false;
    this.textView.setText(paramString);
    this.valueTextView.setVisibility(4);
    if (paramInt != 0)
    {
      this.valueImageView.setVisibility(0);
      this.valueImageView.setImageResource(paramInt);
    }
    for (;;)
    {
      this.needDivider = paramBoolean;
      if (!paramBoolean) {
        bool = true;
      }
      setWillNotDraw(bool);
      return;
      this.valueImageView.setVisibility(4);
    }
  }
  
  public void setTextAndValue(String paramString1, String paramString2, boolean paramBoolean)
  {
    boolean bool = false;
    this.textView.setText(paramString1);
    this.valueImageView.setVisibility(4);
    if (paramString2 != null)
    {
      this.valueTextView.setText(paramString2);
      this.valueTextView.setVisibility(0);
    }
    for (;;)
    {
      this.needDivider = paramBoolean;
      if (!paramBoolean) {
        bool = true;
      }
      setWillNotDraw(bool);
      requestLayout();
      return;
      this.valueTextView.setVisibility(4);
    }
  }
  
  public void setTextColor(int paramInt)
  {
    this.textView.setTextColor(paramInt);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\Theming\Fonts\FontSettingsCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */