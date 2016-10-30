package org.telegram.ui.Mihangram.Theming.ColorPicker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

public class HsvColorValueView
  extends FrameLayout
{
  private Drawable colorSelector;
  private boolean down = false;
  private Bitmap drawCache = null;
  private float hue = 0.0F;
  private Shader innerShader;
  private int lastMeasuredSize = -1;
  private OnSaturationOrValueChanged listener;
  private Shader outerShader;
  private Paint paint;
  private float saturation = 0.0F;
  private ImageView selectorView;
  private float value = 1.0F;
  
  public HsvColorValueView(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public HsvColorValueView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  public HsvColorValueView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init();
  }
  
  private void ensureCache()
  {
    if (this.paint == null) {
      this.paint = new Paint();
    }
    int j = getHeight();
    int i = j;
    if (j <= 0) {
      i = getMeasuredHeight();
    }
    j = i;
    if (i <= 0) {
      j = this.lastMeasuredSize;
    }
    i = getBackgroundSize(j);
    if ((this.drawCache == null) && (i > 0))
    {
      this.outerShader = new LinearGradient(0.0F, 0.0F, 0.0F, i, -1, -16777216, Shader.TileMode.CLAMP);
      j = Color.HSVToColor(new float[] { this.hue, 1.0F, 1.0F });
      this.innerShader = new LinearGradient(0.0F, 0.0F, i, 0.0F, -1, j, Shader.TileMode.CLAMP);
      ComposeShader localComposeShader = new ComposeShader(this.outerShader, this.innerShader, PorterDuff.Mode.MULTIPLY);
      this.paint.setShader(localComposeShader);
      this.drawCache = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
      new Canvas(this.drawCache).drawRect(0.0F, 0.0F, i, i, this.paint);
    }
  }
  
  private int getBackgroundSize(int paramInt)
  {
    return paramInt - getBackgroundOffset() * 2;
  }
  
  private void init()
  {
    this.colorSelector = getContext().getResources().getDrawable(2130837680);
    this.selectorView = new ImageView(getContext());
    this.selectorView.setImageDrawable(this.colorSelector);
    addView(this.selectorView, new FrameLayout.LayoutParams(this.colorSelector.getIntrinsicWidth(), this.colorSelector.getIntrinsicHeight()));
    setWillNotDraw(false);
  }
  
  private void onSaturationOrValueChanged(boolean paramBoolean)
  {
    if (this.listener != null) {
      this.listener.saturationOrValueChanged(this, this.saturation, this.value, paramBoolean);
    }
  }
  
  private void placeSelector()
  {
    int i = getBackgroundOffset();
    int j = (int)Math.ceil(this.selectorView.getHeight() / 2.0F);
    int m = (int)(getBackgroundSize() * this.saturation);
    int k = (int)(getBackgroundSize() * (1.0F - this.value));
    m = Math.max(0, Math.min(getBackgroundSize(), m)) + i - j;
    i = Math.max(0, Math.min(getBackgroundSize(), k)) + i - j;
    this.selectorView.layout(m, i, this.selectorView.getWidth() + m, this.selectorView.getHeight() + i);
  }
  
  private void setPosFromSatAndValue()
  {
    if (this.drawCache != null) {
      placeSelector();
    }
  }
  
  private void setSatAndValueFromPos(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = getBackgroundOffset();
    this.saturation = ((paramInt1 - i) / getBackgroundSize());
    this.value = (1.0F - (paramInt2 - i) / getBackgroundSize());
    onSaturationOrValueChanged(paramBoolean);
  }
  
  private void setSelectorPosition(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    setSatAndValueFromPos(paramInt1, paramInt2, paramBoolean);
    placeSelector();
  }
  
  public int getBackgroundOffset()
  {
    return (int)Math.ceil(this.colorSelector.getIntrinsicHeight() / 2.0F);
  }
  
  public int getBackgroundSize()
  {
    ensureCache();
    if (this.drawCache != null) {
      return this.drawCache.getHeight();
    }
    return 0;
  }
  
  public float getSaturation()
  {
    return this.saturation;
  }
  
  public float getValue()
  {
    return this.value;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    ensureCache();
    paramCanvas.drawBitmap(this.drawCache, getBackgroundOffset(), getBackgroundOffset(), this.paint);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    placeSelector();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    this.lastMeasuredSize = Math.min(getMeasuredHeight(), getMeasuredWidth());
    setMeasuredDimension(this.lastMeasuredSize, this.lastMeasuredSize);
    if ((this.drawCache != null) && (this.drawCache.getHeight() != getBackgroundSize(this.lastMeasuredSize)))
    {
      this.drawCache.recycle();
      this.drawCache = null;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getAction() == 0)
    {
      this.down = true;
      return true;
    }
    if (paramMotionEvent.getAction() == 1)
    {
      this.down = false;
      setSelectorPosition((int)paramMotionEvent.getX() - getBackgroundOffset(), (int)paramMotionEvent.getY() - getBackgroundOffset(), true);
      return true;
    }
    if ((paramMotionEvent.getAction() == 2) && (this.down))
    {
      setSelectorPosition((int)paramMotionEvent.getX() - getBackgroundOffset(), (int)paramMotionEvent.getY() - getBackgroundOffset(), false);
      return true;
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void setHue(float paramFloat)
  {
    this.hue = paramFloat;
    this.drawCache = null;
    invalidate();
  }
  
  public void setOnSaturationOrValueChanged(OnSaturationOrValueChanged paramOnSaturationOrValueChanged)
  {
    this.listener = paramOnSaturationOrValueChanged;
  }
  
  public void setSaturation(float paramFloat)
  {
    this.saturation = paramFloat;
    setPosFromSatAndValue();
  }
  
  public void setValue(float paramFloat)
  {
    this.value = paramFloat;
    setPosFromSatAndValue();
  }
  
  public static abstract interface OnSaturationOrValueChanged
  {
    public abstract void saturationOrValueChanged(HsvColorValueView paramHsvColorValueView, float paramFloat1, float paramFloat2, boolean paramBoolean);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\Theming\ColorPicker\HsvColorValueView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */