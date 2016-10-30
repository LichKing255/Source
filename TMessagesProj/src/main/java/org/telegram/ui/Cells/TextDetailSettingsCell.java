package org.telegram.ui.Cells;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Mihangram.Theming.MihanTheme;

public class TextDetailSettingsCell
  extends FrameLayout
{
  private static Paint paint;
  private TextView DesTextView;
  private boolean multiline;
  private boolean needDivider;
  private TextView textView;
  private TextView valueTextView;
  
  public TextDetailSettingsCell(Context paramContext)
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
    TextView localTextView = this.textView;
    if (LocaleController.isRTL)
    {
      i = 5;
      localTextView.setGravity(i | 0x10);
      localTextView = this.textView;
      if (!LocaleController.isRTL) {
        break label423;
      }
      i = 5;
      label130:
      addView(localTextView, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, 17.0F, 10.0F, 17.0F, 0.0F));
      this.textView.setTypeface(MihanTheme.getMihanTypeFace());
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(-7697782);
      this.valueTextView.setTextSize(1, 13.0F);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label428;
      }
      i = 5;
      label209:
      localTextView.setGravity(i);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setPadding(0, 0, 0, 0);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label433;
      }
      i = 5;
      label264:
      addView(localTextView, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, 17.0F, 35.0F, 17.0F, 0.0F));
      this.valueTextView.setTypeface(MihanTheme.getMihanTypeFace());
      this.DesTextView = new TextView(paramContext);
      this.DesTextView.setTextColor(-6052957);
      this.DesTextView.setTextSize(1, 13.0F);
      paramContext = this.DesTextView;
      if (!LocaleController.isRTL) {
        break label438;
      }
      i = 5;
      label342:
      paramContext.setGravity(i);
      this.valueTextView.setLines(0);
      this.valueTextView.setMaxLines(0);
      this.valueTextView.setSingleLine(false);
      paramContext = this.DesTextView;
      if (!LocaleController.isRTL) {
        break label443;
      }
    }
    label423:
    label428:
    label433:
    label438:
    label443:
    for (int i = j;; i = 3)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, 17.0F, 35.0F, 17.0F, 0.0F));
      this.DesTextView.setTypeface(MihanTheme.getMihanTypeFace());
      return;
      i = 3;
      break;
      i = 3;
      break label130;
      i = 3;
      break label209;
      i = 3;
      break label264;
      i = 3;
      break label342;
    }
  }
  
  private void updateColors()
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    setBackgroundColor(localSharedPreferences.getInt("theme_setting_list_bgcolor", -1));
    paint.setColor(localSharedPreferences.getInt("theme_setting_option_divcolor", -2500135));
    int i = localSharedPreferences.getInt("theme_setting_option_tcolor", -14606047);
    this.textView.setTextColor(i);
    i = localSharedPreferences.getInt("theme_setting_option_descolor", -6052957);
    this.DesTextView.setTextColor(i);
    i = localSharedPreferences.getInt("theme_setting_option_vcolor", MihanTheme.getLighterColor(localSharedPreferences.getInt("theme_setting_section_color", MihanTheme.getThemeColor()), 0.5F));
    this.valueTextView.setTextColor(i);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = 0;
    if (!this.multiline)
    {
      int i = AndroidUtilities.dp(64.0F);
      if (this.needDivider) {
        paramInt2 = 1;
      }
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(paramInt2 + i, 1073741824));
    }
    for (;;)
    {
      updateColors();
      return;
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(0, 0));
    }
  }
  
  public void setMultilineDetail(boolean paramBoolean)
  {
    this.multiline = paramBoolean;
    if (paramBoolean)
    {
      this.valueTextView.setLines(0);
      this.valueTextView.setMaxLines(0);
      this.valueTextView.setSingleLine(false);
      this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(12.0F));
      return;
    }
    this.valueTextView.setLines(1);
    this.valueTextView.setMaxLines(1);
    this.valueTextView.setSingleLine(true);
    this.valueTextView.setPadding(0, 0, 0, 0);
  }
  
  public void setTextAndDes(String paramString1, String paramString2, boolean paramBoolean)
  {
    this.textView.setText(paramString1);
    this.DesTextView.setText(paramString2);
    this.needDivider = paramBoolean;
    if (!paramBoolean) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      setWillNotDraw(paramBoolean);
      return;
    }
  }
  
  public void setTextAndValue(String paramString1, String paramString2, boolean paramBoolean)
  {
    this.textView.setText(paramString1);
    this.valueTextView.setText(paramString2);
    this.needDivider = paramBoolean;
    if (!paramBoolean) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      setWillNotDraw(paramBoolean);
      return;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Cells\TextDetailSettingsCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */