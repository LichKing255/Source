package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import org.telegram.messenger.AndroidUtilities;

public class VideoSeekBarView
  extends View
{
  private static Paint innerPaint1 = new Paint();
  private static Drawable thumbDrawable1;
  private static int thumbHeight;
  private static int thumbWidth;
  public SeekBarDelegate delegate;
  private boolean pressed = false;
  private float progress = 0.0F;
  private int thumbDX = 0;
  
  public VideoSeekBarView(Context paramContext)
  {
    super(paramContext);
    init(paramContext);
  }
  
  public VideoSeekBarView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext);
  }
  
  public VideoSeekBarView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramContext);
  }
  
  private void init(Context paramContext)
  {
    if (thumbDrawable1 == null)
    {
      thumbDrawable1 = paramContext.getResources().getDrawable(2130838200);
      innerPaint1.setColor(-1717986919);
      thumbWidth = thumbDrawable1.getIntrinsicWidth();
      thumbHeight = thumbDrawable1.getIntrinsicHeight();
    }
  }
  
  public float getProgress()
  {
    return this.progress;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int i = (getMeasuredHeight() - thumbHeight) / 2;
    int j = (int)((getMeasuredWidth() - thumbWidth) * this.progress);
    paramCanvas.drawRect(thumbWidth / 2, getMeasuredHeight() / 2 - AndroidUtilities.dp(1.0F), getMeasuredWidth() - thumbWidth / 2, getMeasuredHeight() / 2 + AndroidUtilities.dp(1.0F), innerPaint1);
    thumbDrawable1.setBounds(j, i, thumbWidth + j, thumbHeight + i);
    thumbDrawable1.draw(paramCanvas);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent == null) {}
    float f1;
    do
    {
      float f3;
      do
      {
        int i;
        do
        {
          return false;
          f1 = paramMotionEvent.getX();
          f2 = paramMotionEvent.getY();
          f3 = (int)((getMeasuredWidth() - thumbWidth) * this.progress);
          if (paramMotionEvent.getAction() != 0) {
            break;
          }
          i = (getMeasuredHeight() - thumbWidth) / 2;
        } while ((f3 - i > f1) || (f1 > thumbWidth + f3 + i) || (f2 < 0.0F) || (f2 > getMeasuredHeight()));
        this.pressed = true;
        this.thumbDX = ((int)(f1 - f3));
        getParent().requestDisallowInterceptTouchEvent(true);
        invalidate();
        return true;
        if ((paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 3)) {
          break;
        }
      } while (!this.pressed);
      if ((paramMotionEvent.getAction() == 1) && (this.delegate != null)) {
        this.delegate.onSeekBarDrag(f3 / (getMeasuredWidth() - thumbWidth));
      }
      this.pressed = false;
      invalidate();
      return true;
    } while ((paramMotionEvent.getAction() != 2) || (!this.pressed));
    float f2 = (int)(f1 - this.thumbDX);
    if (f2 < 0.0F) {
      f1 = 0.0F;
    }
    for (;;)
    {
      this.progress = (f1 / (getMeasuredWidth() - thumbWidth));
      invalidate();
      return true;
      f1 = f2;
      if (f2 > getMeasuredWidth() - thumbWidth) {
        f1 = getMeasuredWidth() - thumbWidth;
      }
    }
  }
  
  public void setProgress(float paramFloat)
  {
    float f;
    if (paramFloat < 0.0F) {
      f = 0.0F;
    }
    for (;;)
    {
      this.progress = f;
      invalidate();
      return;
      f = paramFloat;
      if (paramFloat > 1.0F) {
        f = 1.0F;
      }
    }
  }
  
  public static abstract interface SeekBarDelegate
  {
    public abstract void onSeekBarDrag(float paramFloat);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Components\VideoSeekBarView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */