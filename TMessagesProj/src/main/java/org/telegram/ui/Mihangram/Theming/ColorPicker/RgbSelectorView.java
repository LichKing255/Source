package org.telegram.ui.Supergram.Theming.ColorPicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class RgbSelectorView
  extends LinearLayout
{
  private ImageView imgPreview;
  private OnColorChangedListener listener;
  private SeekBar seekAlpha;
  private SeekBar seekBlue;
  private SeekBar seekGreen;
  private SeekBar seekRed;
  
  public RgbSelectorView(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public RgbSelectorView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  private void init()
  {
    View localView = ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(2130903071, null);
    addView(localView, new LinearLayout.LayoutParams(-1, -1));
    SeekBar.OnSeekBarChangeListener local1 = new SeekBar.OnSeekBarChangeListener()
    {
      public void onProgressChanged(SeekBar paramAnonymousSeekBar, int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        RgbSelectorView.this.setPreviewImage();
        RgbSelectorView.this.onColorChanged();
      }
      
      public void onStartTrackingTouch(SeekBar paramAnonymousSeekBar) {}
      
      public void onStopTrackingTouch(SeekBar paramAnonymousSeekBar) {}
    };
    this.seekRed = ((SeekBar)localView.findViewById(2131624099));
    this.seekRed.setOnSeekBarChangeListener(local1);
    this.seekGreen = ((SeekBar)localView.findViewById(2131624101));
    this.seekGreen.setOnSeekBarChangeListener(local1);
    this.seekBlue = ((SeekBar)localView.findViewById(2131624103));
    this.seekBlue.setOnSeekBarChangeListener(local1);
    this.seekAlpha = ((SeekBar)localView.findViewById(2131624105));
    this.seekAlpha.setOnSeekBarChangeListener(local1);
    this.imgPreview = ((ImageView)localView.findViewById(2131624097));
    setColor(-16777216);
  }
  
  private void onColorChanged()
  {
    if (this.listener != null) {
      this.listener.colorChanged(getColor());
    }
  }
  
  private void setPreviewImage()
  {
    Bitmap localBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    localBitmap.setPixel(0, 0, getColor());
    this.imgPreview.setImageBitmap(localBitmap);
  }
  
  public int getColor()
  {
    return Color.argb(this.seekAlpha.getProgress(), this.seekRed.getProgress(), this.seekGreen.getProgress(), this.seekBlue.getProgress());
  }
  
  public void setColor(int paramInt)
  {
    this.seekAlpha.setProgress(Color.alpha(paramInt));
    this.seekRed.setProgress(Color.red(paramInt));
    this.seekGreen.setProgress(Color.green(paramInt));
    this.seekBlue.setProgress(Color.blue(paramInt));
    setPreviewImage();
  }
  
  public void setOnColorChangedListener(OnColorChangedListener paramOnColorChangedListener)
  {
    this.listener = paramOnColorChangedListener;
  }
  
  public static abstract interface OnColorChangedListener
  {
    public abstract void colorChanged(int paramInt);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\Theming\ColorPicker\RgbSelectorView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */