package org.telegram.ui.Supergram;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.method.LinkMovementMethod;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Supergram.Theming.MihanTheme;

public class TextDescriptionCell
  extends FrameLayout
{
  private static Paint paint;
  private boolean needDivider;
  private TextView textView;
  
  public TextDescriptionCell(Context paramContext)
  {
    super(paramContext);
    if (paint == null)
    {
      paint = new Paint();
      paint.setColor(-2500135);
      paint.setStrokeWidth(1.0F);
    }
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(-6052957);
    this.textView.setLinkTextColor(-13537377);
    this.textView.setTextSize(1, 14.0F);
    paramContext = this.textView;
    if (LocaleController.isRTL)
    {
      i = 5;
      paramContext.setGravity(i);
      this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(17.0F));
      this.textView.setMovementMethod(LinkMovementMethod.getInstance());
      paramContext = this.textView;
      if (!LocaleController.isRTL) {
        break label173;
      }
    }
    label173:
    for (int i = j;; i = 3)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, 17.0F, -5.0F, 17.0F, 0.0F));
      this.textView.setTypeface(MihanTheme.getMihanTypeFace());
      return;
      i = 3;
      break;
    }
  }
  
  private void updateColors()
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    setBackgroundColor(localSharedPreferences.getInt("theme_setting_list_bgcolor", -1));
    paint.setColor(localSharedPreferences.getInt("theme_setting_option_divcolor", -2500135));
    int i = localSharedPreferences.getInt("theme_setting_option_descolor", -6052957);
    this.textView.setTextColor(i);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
    }
    updateColors();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
  }
  
  public void setText(CharSequence paramCharSequence, boolean paramBoolean)
  {
    this.textView.setText(paramCharSequence);
    this.needDivider = paramBoolean;
    setWillNotDraw(false);
  }
  
  public void setTextColor(int paramInt)
  {
    this.textView.setTextColor(paramInt);
    setWillNotDraw(false);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\TextDescriptionCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */