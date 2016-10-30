package org.telegram.ui.Cells;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class RadioCell
  extends FrameLayout
{
  private static Paint paint;
  private boolean needDivider;
  private RadioButton radioButton;
  private TextView textView;
  
  public RadioCell(Context paramContext)
  {
    super(paramContext);
    if (paint == null)
    {
      paint = new Paint();
      paint.setColor(-2500135);
      paint.setStrokeWidth(1.0F);
    }
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(-14606047);
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setEllipsize(TextUtils.TruncateAt.END);
    TextView localTextView = this.textView;
    int i;
    label141:
    label224:
    label234:
    float f;
    if (LocaleController.isRTL)
    {
      i = 5;
      localTextView.setGravity(i | 0x10);
      localTextView = this.textView;
      if (!LocaleController.isRTL) {
        break label276;
      }
      i = 5;
      addView(localTextView, LayoutHelper.createFrame(-1, -1.0F, i | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.radioButton = new RadioButton(paramContext);
      this.radioButton.setSize(AndroidUtilities.dp(20.0F));
      this.radioButton.setColor(-5000269, -13129232);
      paramContext = this.radioButton;
      if (!LocaleController.isRTL) {
        break label281;
      }
      i = j;
      if (!LocaleController.isRTL) {
        break label286;
      }
      j = 18;
      f = j;
      if (!LocaleController.isRTL) {
        break label292;
      }
    }
    label276:
    label281:
    label286:
    label292:
    for (j = 0;; j = 18)
    {
      addView(paramContext, LayoutHelper.createFrame(22, 22.0F, i | 0x30, f, 13.0F, j, 0.0F));
      return;
      i = 3;
      break;
      i = 3;
      break label141;
      i = 5;
      break label224;
      j = 0;
      break label234;
    }
  }
  
  private void updateColors()
  {
    setBackgroundColor(ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0).getInt("theme_setting_list_bgcolor", -1));
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
    if (this.needDivider) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      setMeasuredDimension(paramInt2, paramInt1 + i);
      paramInt1 = getMeasuredWidth();
      paramInt2 = getPaddingLeft();
      i = getPaddingRight();
      int j = AndroidUtilities.dp(34.0F);
      this.radioButton.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0F), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0F), 1073741824));
      this.textView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1 - paramInt2 - i - j, 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
      updateColors();
      return;
    }
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.radioButton.setChecked(paramBoolean1, paramBoolean2);
  }
  
  public void setText(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = false;
    this.textView.setText(paramString);
    this.radioButton.setChecked(paramBoolean1, false);
    this.needDivider = paramBoolean2;
    paramBoolean1 = bool;
    if (!paramBoolean2) {
      paramBoolean1 = true;
    }
    setWillNotDraw(paramBoolean1);
  }
  
  public void setTextColor(int paramInt)
  {
    this.textView.setTextColor(paramInt);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Cells\RadioCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */