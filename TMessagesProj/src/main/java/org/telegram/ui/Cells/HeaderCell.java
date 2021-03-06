package org.telegram.ui.Cells;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Supergram.Theming.MihanTheme;

public class HeaderCell
  extends FrameLayout
{
  private TextView textView = new TextView(getContext());
  
  public HeaderCell(Context paramContext)
  {
    super(paramContext);
    this.textView.setTextSize(1, 15.0F);
    this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.textView.setTextColor(-12676913);
    paramContext = this.textView;
    if (LocaleController.isRTL)
    {
      i = 5;
      paramContext.setGravity(i | 0x10);
      paramContext = this.textView;
      if (!LocaleController.isRTL) {
        break label115;
      }
    }
    label115:
    for (int i = j;; i = 3)
    {
      addView(paramContext, LayoutHelper.createFrame(-1, -1.0F, i | 0x30, 17.0F, 15.0F, 17.0F, 0.0F));
      return;
      i = 3;
      break;
    }
  }
  
  private void updateColors()
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    setBackgroundColor(localSharedPreferences.getInt("theme_setting_list_bgcolor", -1));
    this.textView.setTextColor(localSharedPreferences.getInt("theme_setting_section_color", MihanTheme.getThemeColor()));
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    updateColors();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(38.0F), 1073741824));
  }
  
  public void setText(String paramString)
  {
    this.textView.setText(paramString);
    setWillNotDraw(false);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Cells\HeaderCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */