package org.telegram.ui.Cells;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;
import org.telegram.ui.Mihangram.Theming.MihanTheme;

public class TextCheckCell
  extends FrameLayout
{
  private static Paint paint;
  private TextView DesTextView;
  private Switch checkBox;
  private boolean isMultiline;
  private boolean needDivider;
  private TextView textView;
  private TextView valueTextView;
  
  public TextCheckCell(Context paramContext)
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
    Object localObject = this.textView;
    label141:
    float f1;
    label150:
    float f2;
    if (LocaleController.isRTL)
    {
      i = 5;
      ((TextView)localObject).setGravity(i | 0x10);
      this.textView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label578;
      }
      i = 5;
      if (!LocaleController.isRTL) {
        break label584;
      }
      f1 = 64.0F;
      if (!LocaleController.isRTL) {
        break label590;
      }
      f2 = 17.0F;
      label159:
      addView((View)localObject, LayoutHelper.createFrame(-1, -1.0F, i | 0x30, f1, 0.0F, f2, 0.0F));
      this.textView.setTypeface(MihanTheme.getMihanTypeFace());
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(-7697782);
      this.valueTextView.setTextSize(1, 13.0F);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label596;
      }
      i = 5;
      label236:
      ((TextView)localObject).setGravity(i);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setPadding(0, 0, 0, 0);
      this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label602;
      }
      i = 5;
      label303:
      if (!LocaleController.isRTL) {
        break label608;
      }
      f1 = 64.0F;
      label312:
      if (!LocaleController.isRTL) {
        break label614;
      }
      f2 = 17.0F;
      label321:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 35.0F, f2, 0.0F));
      this.valueTextView.setTypeface(MihanTheme.getMihanTypeFace());
      this.checkBox = new Switch(paramContext);
      this.checkBox.setDuplicateParentStateEnabled(false);
      this.checkBox.setFocusable(false);
      this.checkBox.setFocusableInTouchMode(false);
      this.checkBox.setClickable(false);
      localObject = this.checkBox;
      if (!LocaleController.isRTL) {
        break label620;
      }
      i = 3;
      label413:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x10, 14.0F, 0.0F, 14.0F, 0.0F));
      this.DesTextView = new TextView(paramContext);
      this.DesTextView.setTextColor(-6052957);
      this.DesTextView.setTextSize(1, 13.0F);
      paramContext = this.DesTextView;
      if (!LocaleController.isRTL) {
        break label626;
      }
      i = 5;
      label482:
      paramContext.setGravity(i);
      this.DesTextView.setLines(0);
      this.DesTextView.setMaxLines(0);
      this.DesTextView.setSingleLine(false);
      this.DesTextView.setPadding(0, 0, 0, 0);
      paramContext = this.DesTextView;
      if (!LocaleController.isRTL) {
        break label632;
      }
    }
    label578:
    label584:
    label590:
    label596:
    label602:
    label608:
    label614:
    label620:
    label626:
    label632:
    for (int i = 5;; i = 3)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, 17.0F, 35.0F, 17.0F, 0.0F));
      this.DesTextView.setTypeface(MihanTheme.getMihanTypeFace());
      return;
      i = 3;
      break;
      i = 3;
      break label141;
      f1 = 17.0F;
      break label150;
      f2 = 64.0F;
      break label159;
      i = 3;
      break label236;
      i = 3;
      break label303;
      f1 = 17.0F;
      break label312;
      f2 = 64.0F;
      break label321;
      i = 5;
      break label413;
      i = 3;
      break label482;
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
    if (this.isMultiline)
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(0, 0));
      updateColors();
      return;
    }
    float f;
    label35:
    int i;
    if (this.valueTextView.getVisibility() == 0)
    {
      f = 64.0F;
      i = AndroidUtilities.dp(f);
      if (!this.needDivider) {
        break label73;
      }
    }
    label73:
    for (paramInt2 = 1;; paramInt2 = 0)
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(paramInt2 + i, 1073741824));
      break;
      f = 48.0F;
      break label35;
    }
  }
  
  public void setChecked(boolean paramBoolean)
  {
    this.checkBox.setChecked(paramBoolean);
  }
  
  public void setTextAndCheck(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = false;
    this.textView.setText(paramString);
    this.isMultiline = false;
    this.checkBox.setChecked(paramBoolean1);
    this.needDivider = paramBoolean2;
    this.valueTextView.setVisibility(8);
    paramString = (FrameLayout.LayoutParams)this.textView.getLayoutParams();
    paramString.height = -1;
    paramString.topMargin = 0;
    this.textView.setLayoutParams(paramString);
    paramBoolean1 = bool;
    if (!paramBoolean2) {
      paramBoolean1 = true;
    }
    setWillNotDraw(paramBoolean1);
  }
  
  public void setTextAndDesAndCheck(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = false;
    this.textView.setText(paramString1);
    this.DesTextView.setText(paramString2);
    this.checkBox.setChecked(paramBoolean1);
    this.needDivider = paramBoolean2;
    this.valueTextView.setVisibility(8);
    this.DesTextView.setVisibility(0);
    paramBoolean1 = bool;
    if (!paramBoolean2) {
      paramBoolean1 = true;
    }
    setWillNotDraw(paramBoolean1);
  }
  
  public void setTextAndValueAndCheck(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    boolean bool = true;
    this.textView.setText(paramString1);
    this.valueTextView.setText(paramString2);
    this.checkBox.setChecked(paramBoolean1);
    this.needDivider = paramBoolean3;
    this.valueTextView.setVisibility(0);
    this.DesTextView.setVisibility(8);
    this.isMultiline = paramBoolean2;
    if (paramBoolean2)
    {
      this.valueTextView.setLines(0);
      this.valueTextView.setMaxLines(0);
      this.valueTextView.setSingleLine(false);
      this.valueTextView.setEllipsize(null);
      this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(11.0F));
      paramString1 = (FrameLayout.LayoutParams)this.textView.getLayoutParams();
      paramString1.height = -2;
      paramString1.topMargin = AndroidUtilities.dp(10.0F);
      this.textView.setLayoutParams(paramString1);
      if (paramBoolean3) {
        break label204;
      }
    }
    label204:
    for (paramBoolean1 = bool;; paramBoolean1 = false)
    {
      setWillNotDraw(paramBoolean1);
      return;
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
      this.valueTextView.setPadding(0, 0, 0, 0);
      break;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Cells\TextCheckCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */