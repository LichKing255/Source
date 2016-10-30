package org.telegram.ui.Cells;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Mihangram.Theming.MihanTheme;

public class TextInfoPrivacyCell
  extends FrameLayout
{
  private TextView textView;
  
  public TextInfoPrivacyCell(Context paramContext)
  {
    super(paramContext);
    View localView = new View(paramContext);
    addView(localView, LayoutHelper.createFrame(-1, 3, 48));
    localView.setBackgroundDrawable(MihanTheme.setGradiant(654311424, 0, GradientDrawable.Orientation.TOP_BOTTOM));
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(-8355712);
    this.textView.setLinkTextColor(-14255946);
    this.textView.setTextSize(1, 14.0F);
    paramContext = this.textView;
    if (LocaleController.isRTL)
    {
      i = 5;
      paramContext.setGravity(i);
      this.textView.setPadding(0, AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(17.0F));
      this.textView.setMovementMethod(LinkMovementMethod.getInstance());
      paramContext = this.textView;
      if (!LocaleController.isRTL) {
        break label184;
      }
    }
    label184:
    for (int i = j;; i = 3)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      return;
      i = 3;
      break;
    }
  }
  
  private void updateColors()
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    setBackgroundColor(localSharedPreferences.getInt("theme_setting_shadow_color", -1052689));
    int i = localSharedPreferences.getInt("theme_setting_option_descolor", -6052957);
    this.textView.setTextColor(i);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
    updateColors();
  }
  
  public void setText(CharSequence paramCharSequence)
  {
    this.textView.setText(paramCharSequence);
  }
  
  public void setTextColor(int paramInt)
  {
    this.textView.setTextColor(paramInt);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Cells\TextInfoPrivacyCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */