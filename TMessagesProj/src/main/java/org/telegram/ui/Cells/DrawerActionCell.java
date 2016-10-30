package org.telegram.ui.Cells;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Mihangram.Theming.MihanTheme;

public class DrawerActionCell
  extends FrameLayout
{
  private Drawable drawable;
  private TextView textView;
  
  public DrawerActionCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(-12303292);
    this.textView.setTextSize(1, 15.0F);
    this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setGravity(19);
    this.textView.setCompoundDrawablePadding(AndroidUtilities.dp(34.0F));
    addView(this.textView, LayoutHelper.createFrame(-1, -1.0F, 51, 14.0F, 0.0F, 16.0F, 0.0F));
  }
  
  private void updateColors()
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    this.textView.setTextColor(localSharedPreferences.getInt("theme_drawer_menu_tcolor", MihanTheme.getThemeColor(localSharedPreferences)));
    MihanTheme.setColorFilter(this.drawable, localSharedPreferences.getInt("theme_drawer_menu_icolor", MihanTheme.getThemeColor(localSharedPreferences)));
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    updateColors();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0F), 1073741824));
  }
  
  public void setTextAndIcon(String paramString, int paramInt)
  {
    try
    {
      this.textView.setText(paramString);
      this.drawable = ApplicationLoader.applicationContext.getResources().getDrawable(paramInt);
      this.textView.setCompoundDrawablesWithIntrinsicBounds(this.drawable, null, null, null);
      setWillNotDraw(false);
      return;
    }
    catch (Throwable paramString)
    {
      FileLog.e("tmessages", paramString);
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Cells\DrawerActionCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */