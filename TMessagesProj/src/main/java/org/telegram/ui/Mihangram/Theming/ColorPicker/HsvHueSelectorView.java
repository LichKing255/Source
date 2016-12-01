package org.telegram.ui.Supergram.Theming.ColorPicker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class HsvHueSelectorView
  extends LinearLayout
{
  private boolean down = false;
  private float hue = 0.0F;
  private ImageView imgHue;
  private ImageView imgSeekSelector;
  private OnHueChangedListener listener;
  private int minOffset = 0;
  private Drawable seekSelector;
  
  public HsvHueSelectorView(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public HsvHueSelectorView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  private void buildUI()
  {
    setOrientation(0);
    setGravity(1);
    this.imgSeekSelector = new ImageView(getContext());
    this.imgSeekSelector.setImageDrawable(this.seekSelector);
    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(this.seekSelector.getIntrinsicWidth(), this.seekSelector.getIntrinsicHeight());
    addView(this.imgSeekSelector, localLayoutParams);
    this.imgHue = new ImageView(getContext());
    this.imgHue.setImageDrawable(getContext().getResources().getDrawable(2130837678));
    this.imgHue.setScaleType(ImageView.ScaleType.FIT_XY);
    localLayoutParams = new LinearLayout.LayoutParams(-1, -1);
    localLayoutParams.setMargins(0, getOffset(), 0, getSelectorOffset());
    addView(this.imgHue, localLayoutParams);
  }
  
  private int getOffset()
  {
    return Math.max(this.minOffset, getSelectorOffset());
  }
  
  private int getSelectorOffset()
  {
    return (int)Math.ceil(this.seekSelector.getIntrinsicHeight() / 2.0F);
  }
  
  private void init()
  {
    this.seekSelector = getContext().getResources().getDrawable(2130837679);
    buildUI();
  }
  
  private void onHueChanged()
  {
    if (this.listener != null) {
      this.listener.hueChanged(this, this.hue);
    }
  }
  
  private void placeSelector()
  {
    int i = (int)((360.0F - this.hue) / 360.0F * this.imgHue.getHeight());
    this.imgSeekSelector.layout(0, getOffset() + i - getSelectorOffset(), this.imgSeekSelector.getWidth(), getOffset() + i - getSelectorOffset() + this.imgSeekSelector.getHeight());
  }
  
  private void setPosition(int paramInt)
  {
    this.hue = Math.max(Math.min(360.0F - (paramInt - getOffset()) / this.imgHue.getHeight() * 360.0F, 360.0F), 0.0F);
    placeSelector();
    onHueChanged();
  }
  
  public float getHue()
  {
    return this.hue;
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
  
  public void setHue(float paramFloat)
  {
    if (this.hue == paramFloat) {
      return;
    }
    this.hue = paramFloat;
    placeSelector();
  }
  
  public void setMinContentOffset(int paramInt)
  {
    this.minOffset = paramInt;
    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(this.imgHue.getLayoutParams());
    localLayoutParams.setMargins(0, getOffset(), 0, getSelectorOffset());
    this.imgHue.setLayoutParams(localLayoutParams);
  }
  
  public void setOnHueChangedListener(OnHueChangedListener paramOnHueChangedListener)
  {
    this.listener = paramOnHueChangedListener;
  }
  
  public static abstract interface OnHueChangedListener
  {
    public abstract void hueChanged(HsvHueSelectorView paramHsvHueSelectorView, float paramFloat);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\Theming\ColorPicker\HsvHueSelectorView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */