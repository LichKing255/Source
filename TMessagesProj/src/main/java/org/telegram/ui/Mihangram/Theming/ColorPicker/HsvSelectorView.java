package org.telegram.ui.Supergram.Theming.ColorPicker;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class HsvSelectorView
  extends LinearLayout
{
  private HsvAlphaSelectorView alphaSelector;
  private int color;
  private HsvColorValueView hsvColorValueView;
  private HsvHueSelectorView hueSelector;
  private OnColorChangedListener listener;
  
  public HsvSelectorView(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public HsvSelectorView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  private void buildUI()
  {
    View localView = ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(2130903070, null);
    addView(localView, new LinearLayout.LayoutParams(-1, -1));
    this.alphaSelector = ((HsvAlphaSelectorView)localView.findViewById(2131624094));
    this.hsvColorValueView = ((HsvColorValueView)localView.findViewById(2131624095));
    this.hueSelector = ((HsvHueSelectorView)localView.findViewById(2131624096));
    this.alphaSelector.setOnAlphaChangedListener(new HsvAlphaSelectorView.OnAlphaChangedListener()
    {
      public void alphaChanged(HsvAlphaSelectorView paramAnonymousHsvAlphaSelectorView, int paramAnonymousInt)
      {
        HsvSelectorView.this.internalSetColor(HsvSelectorView.access$000(HsvSelectorView.this, true), true);
      }
    });
    this.hsvColorValueView.setOnSaturationOrValueChanged(new HsvColorValueView.OnSaturationOrValueChanged()
    {
      public void saturationOrValueChanged(HsvColorValueView paramAnonymousHsvColorValueView, float paramAnonymousFloat1, float paramAnonymousFloat2, boolean paramAnonymousBoolean)
      {
        HsvSelectorView.this.alphaSelector.setColor(HsvSelectorView.this.getCurrentColor(false));
        HsvSelectorView.this.internalSetColor(HsvSelectorView.access$000(HsvSelectorView.this, true), paramAnonymousBoolean);
      }
    });
    this.hueSelector.setOnHueChangedListener(new HsvHueSelectorView.OnHueChangedListener()
    {
      public void hueChanged(HsvHueSelectorView paramAnonymousHsvHueSelectorView, float paramAnonymousFloat)
      {
        HsvSelectorView.this.hsvColorValueView.setHue(paramAnonymousFloat);
        HsvSelectorView.this.alphaSelector.setColor(HsvSelectorView.this.getCurrentColor(false));
        HsvSelectorView.this.internalSetColor(HsvSelectorView.access$000(HsvSelectorView.this, true), true);
      }
    });
    setColor(-16777216);
  }
  
  private int getCurrentColor(boolean paramBoolean)
  {
    float f1 = this.hueSelector.getHue();
    float f2 = this.hsvColorValueView.getSaturation();
    float f3 = this.hsvColorValueView.getValue();
    if (paramBoolean) {}
    for (int i = (int)this.alphaSelector.getAlpha();; i = 255) {
      return Color.HSVToColor(i, new float[] { f1, f2, f3 });
    }
  }
  
  private void init()
  {
    buildUI();
  }
  
  private void internalSetColor(int paramInt, boolean paramBoolean)
  {
    this.color = paramInt;
    if (paramBoolean) {
      onColorChanged();
    }
  }
  
  private void onColorChanged()
  {
    if (this.listener != null) {
      this.listener.colorChanged(this.color);
    }
  }
  
  public int getColor()
  {
    return this.color;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    LinearLayout.LayoutParams localLayoutParams1 = new LinearLayout.LayoutParams(this.alphaSelector.getLayoutParams());
    LinearLayout.LayoutParams localLayoutParams2 = new LinearLayout.LayoutParams(this.hueSelector.getLayoutParams());
    localLayoutParams1.height = this.hsvColorValueView.getHeight();
    localLayoutParams2.height = this.hsvColorValueView.getHeight();
    this.hueSelector.setMinContentOffset(this.hsvColorValueView.getBackgroundOffset());
    this.alphaSelector.setMinContentOffset(this.hsvColorValueView.getBackgroundOffset());
    this.alphaSelector.setLayoutParams(localLayoutParams1);
    this.hueSelector.setLayoutParams(localLayoutParams2);
    super.onMeasure(paramInt1, paramInt2);
  }
  
  public void setColor(int paramInt)
  {
    boolean bool = true;
    int i = Color.alpha(paramInt);
    this.alphaSelector.setAlpha(i);
    float[] arrayOfFloat = new float[3];
    Color.colorToHSV(paramInt | 0xFF000000, arrayOfFloat);
    this.hueSelector.setHue(arrayOfFloat[0]);
    this.hsvColorValueView.setHue(arrayOfFloat[0]);
    this.hsvColorValueView.setSaturation(arrayOfFloat[1]);
    this.hsvColorValueView.setValue(arrayOfFloat[2]);
    this.alphaSelector.setColor(paramInt);
    if (this.color != paramInt) {}
    for (;;)
    {
      internalSetColor(paramInt, bool);
      return;
      bool = false;
    }
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


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\Theming\ColorPicker\HsvSelectorView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */