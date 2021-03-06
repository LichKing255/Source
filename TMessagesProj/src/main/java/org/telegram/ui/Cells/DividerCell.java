package org.telegram.ui.Cells;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;

public class DividerCell
  extends BaseCell
{
  private static Paint paint;
  
  public DividerCell(Context paramContext)
  {
    super(paramContext);
    if (paint == null)
    {
      paint = new Paint();
      paint.setColor(-2500135);
      paint.setStrokeWidth(1.0F);
    }
  }
  
  private void setPaintColor()
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    if (getTag() != null) {}
    for (String str = getTag().toString();; str = null)
    {
      if (str != null) {
        paint.setColor(localSharedPreferences.getInt(str, -2500135));
      }
      return;
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    paramCanvas.drawLine(getPaddingLeft(), AndroidUtilities.dp(8.0F), getWidth() - getPaddingRight(), AndroidUtilities.dp(8.0F), paint);
    setPaintColor();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(16.0F) + 1);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Cells\DividerCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */