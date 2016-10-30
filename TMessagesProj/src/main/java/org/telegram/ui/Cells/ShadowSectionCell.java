package org.telegram.ui.Cells;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Mihangram.Theming.MihanTheme;

public class ShadowSectionCell
  extends FrameLayout
{
  private int size = 12;
  
  public ShadowSectionCell(Context paramContext)
  {
    super(paramContext);
    paramContext = new View(paramContext);
    addView(paramContext, LayoutHelper.createFrame(-1, 3, 48));
    paramContext.setBackgroundDrawable(MihanTheme.setGradiant(654311424, 0, GradientDrawable.Orientation.TOP_BOTTOM));
  }
  
  private void updateColors()
  {
    setBackgroundColor(ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0).getInt("theme_setting_shadow_color", -1052689));
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    updateColors();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.size), 1073741824));
    updateColors();
  }
  
  public void setSize(int paramInt)
  {
    this.size = paramInt;
    setWillNotDraw(false);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Cells\ShadowSectionCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */