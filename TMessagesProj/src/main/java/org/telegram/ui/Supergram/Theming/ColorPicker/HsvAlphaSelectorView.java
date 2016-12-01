package org.telegram.ui.Supergram.Theming.ColorPicker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class HsvAlphaSelectorView
  extends LinearLayout
{
  private int alpha = 0;
  private int color = -1;
  private boolean dirty = true;
  private boolean down = false;
  private ImageView imgAlpha;
  private ImageView imgSeekSelector;
  private OnAlphaChangedListener listener;
  private int minOffset = 0;
  private Drawable seekSelector;
  
  public HsvAlphaSelectorView(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public HsvAlphaSelectorView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  private void buildUI()
  {
    setOrientation(0);
    setGravity(1);
    setWillNotDraw(false);
    this.imgSeekSelector = new ImageView(getContext());
    this.imgSeekSelector.setImageDrawable(this.seekSelector);
    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(this.seekSelector.getIntrinsicWidth(), this.seekSelector.getIntrinsicHeight());
    addView(this.imgSeekSelector, localLayoutParams);
    this.imgAlpha = new ImageView(getContext());
    this.imgAlpha.setBackgroundDrawable(getContext().getResources().getDrawable(2130838197));
    this.imgAlpha.setScaleType(ImageView.ScaleType.FIT_XY);
    localLayoutParams = new LinearLayout.LayoutParams(-1, -1);
    localLayoutParams.setMargins(0, getOffset(), 0, getSelectorOffset());
    addView(this.imgAlpha, localLayoutParams);
  }
  
  private int getOffset()
  {
    return Math.max(this.minOffset, (int)Math.ceil(this.seekSelector.getIntrinsicHeight() / 2.0D));
  }
  
  private int getSelectorOffset()
  {
    return (int)Math.ceil(this.imgSeekSelector.getHeight() / 2.0F);
  }
  
  private void init()
  {
    this.seekSelector = getContext().getResources().getDrawable(2130837679);
    buildUI();
  }
  
  private void onAlphaChanged()
  {
    if (this.listener != null) {
      this.listener.alphaChanged(this, this.alpha);
    }
  }
  
  private void placeSelector()
  {
    int i = (int)((255 - this.alpha) / 255.0F * this.imgAlpha.getHeight());
    int j = getSelectorOffset();
    int k = this.imgAlpha.getTop();
    this.imgSeekSelector.layout(0, i + k - j, this.imgSeekSelector.getWidth(), i + k - j + this.imgSeekSelector.getHeight());
  }
  
  private void setAlphaImage()
  {
    if (this.imgAlpha.getHeight() <= 0)
    {
      this.dirty = true;
      invalidate();
    }
    Paint localPaint;
    do
    {
      return;
      localPaint = new Paint();
    } while (0 != 0);
    int i = this.color;
    int j = this.color;
    localPaint.setShader(new LinearGradient(0.0F, this.imgAlpha.getHeight(), 0.0F, 0.0F, j & 0xFFFFFF, i | 0xFF000000, Shader.TileMode.CLAMP));
    Bitmap localBitmap = Bitmap.createBitmap(this.imgAlpha.getWidth(), this.imgAlpha.getHeight(), Bitmap.Config.ARGB_8888);
    new Canvas(localBitmap).drawRect(0.0F, 0.0F, this.imgAlpha.getWidth(), this.imgAlpha.getHeight(), localPaint);
    this.imgAlpha.setImageBitmap(localBitmap);
  }
  
  private void setPosition(int paramInt)
  {
    this.alpha = (255 - Math.min(255, Math.max(0, (int)((paramInt - this.imgAlpha.getTop()) / this.imgAlpha.getHeight() * 255.0F))));
    placeSelector();
    onAlphaChanged();
  }
  
  public float getAlpha()
  {
    return this.alpha;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.dirty)
    {
      this.dirty = false;
      setAlphaImage();
    }
    super.onDraw(paramCanvas);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    placeSelector();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getAction() == 0)
    {
      this.down = true;
      setPosition((int)paramMotionEvent.getY());
      return true;
    }
    if (paramMotionEvent.getAction() == 1)
    {
      this.down = false;
      return true;
    }
    if ((this.down) && (paramMotionEvent.getAction() == 2))
    {
      setPosition((int)paramMotionEvent.getY());
      return true;
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void setAlpha(int paramInt)
  {
    if (this.alpha == paramInt) {
      return;
    }
    this.alpha = paramInt;
    placeSelector();
  }
  
  public void setColor(int paramInt)
  {
    if (this.color == paramInt) {
      return;
    }
    this.color = paramInt;
    setAlphaImage();
  }
  
  public void setMinContentOffset(int paramInt)
  {
    this.minOffset = paramInt;
    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(this.imgAlpha.getLayoutParams());
    localLayoutParams.setMargins(0, getOffset(), 0, getSelectorOffset());
    this.imgAlpha.setLayoutParams(localLayoutParams);
  }
  
  public void setOnAlphaChangedListener(OnAlphaChangedListener paramOnAlphaChangedListener)
  {
    this.listener = paramOnAlphaChangedListener;
  }
  
  public static abstract interface OnAlphaChangedListener
  {
    public abstract void alphaChanged(HsvAlphaSelectorView paramHsvAlphaSelectorView, int paramInt);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\Theming\ColorPicker\HsvAlphaSelectorView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */