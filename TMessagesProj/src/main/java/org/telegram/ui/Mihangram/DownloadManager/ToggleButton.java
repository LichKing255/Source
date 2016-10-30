package org.telegram.ui.Mihangram.DownloadManager;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public abstract class ToggleButton
  extends LinearLayout
{
  @ColorInt
  int colorNotPressed;
  int colorNotPressedBackground;
  int colorNotPressedText;
  @ColorInt
  int colorPressed;
  int colorPressedBackground;
  int colorPressedText;
  Context context;
  OnValueChangedListener listener;
  int notPressedBackgroundResource;
  int pressedBackgroundResource;
  
  public ToggleButton(Context paramContext)
  {
    super(paramContext, null);
    this.context = paramContext;
  }
  
  public ToggleButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.context = paramContext;
  }
  
  public void setBackgroundResources(@DrawableRes int paramInt1, @DrawableRes int paramInt2)
  {
    this.pressedBackgroundResource = paramInt1;
    this.notPressedBackgroundResource = paramInt2;
  }
  
  public void setColorRes(@ColorRes int paramInt1, @ColorRes int paramInt2)
  {
    setColors(ContextCompat.getColor(this.context, paramInt1), ContextCompat.getColor(this.context, paramInt2));
  }
  
  public void setColors(@ColorInt int paramInt1, @ColorInt int paramInt2)
  {
    this.colorPressed = paramInt1;
    this.colorNotPressed = paramInt2;
  }
  
  public void setForegroundColors(int paramInt1, int paramInt2)
  {
    this.colorPressedText = paramInt1;
    this.colorNotPressedText = paramInt2;
  }
  
  public void setForegroundColorsRes(@ColorRes int paramInt1, @ColorRes int paramInt2)
  {
    setForegroundColors(ContextCompat.getColor(this.context, paramInt1), ContextCompat.getColor(this.context, paramInt2));
  }
  
  public void setNotPressedColorRes(@ColorRes int paramInt1, @ColorRes int paramInt2)
  {
    setNotPressedColors(ContextCompat.getColor(this.context, paramInt1), ContextCompat.getColor(this.context, paramInt2));
  }
  
  public void setNotPressedColors(int paramInt1, int paramInt2)
  {
    this.colorNotPressedText = paramInt1;
    this.colorNotPressedBackground = paramInt2;
  }
  
  public void setOnValueChangedListener(OnValueChangedListener paramOnValueChangedListener)
  {
    this.listener = paramOnValueChangedListener;
  }
  
  public void setPressedColors(@ColorInt int paramInt1, @ColorInt int paramInt2)
  {
    this.colorPressedText = paramInt1;
    this.colorPressedBackground = paramInt2;
  }
  
  public void setPressedColorsRes(@ColorRes int paramInt1, @ColorRes int paramInt2)
  {
    setPressedColors(ContextCompat.getColor(this.context, paramInt1), ContextCompat.getColor(this.context, paramInt2));
  }
  
  public void setValue(int paramInt)
  {
    if (this.listener != null) {
      this.listener.onValueChanged(paramInt);
    }
  }
  
  public static abstract interface OnValueChangedListener
  {
    public abstract void onValueChanged(int paramInt);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\DownloadManager\ToggleButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */