package org.telegram.ui.Supergram.Theming.ColorPicker;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class ColorSelectorView
  extends LinearLayout
{
  private static final String HEX_TAG = "HEX";
  private static final String HSV_TAG = "HSV";
  private static final String RGB_TAG = "RGB";
  private int color;
  private HexSelectorView hexSelector;
  private HsvSelectorView hsvSelector;
  private OnColorChangedListener listener;
  private int maxHeight = 0;
  private int maxWidth = 0;
  private RgbSelectorView rgbSelector;
  private TabHost tabs;
  
  public ColorSelectorView(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public ColorSelectorView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  private static View createTabView(Context paramContext, String paramString)
  {
    paramContext = LayoutInflater.from(paramContext).inflate(2130903122, null);
    ((TextView)paramContext.findViewById(2131624232)).setText(paramString);
    return paramContext;
  }
  
  private void init()
  {
    Object localObject1 = ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(2130903066, null);
    addView((View)localObject1, new LinearLayout.LayoutParams(-1, -1));
    this.hsvSelector = new HsvSelectorView(getContext());
    this.hsvSelector.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
    this.hsvSelector.setOnColorChangedListener(new HsvSelectorView.OnColorChangedListener()
    {
      public void colorChanged(int paramAnonymousInt)
      {
        ColorSelectorView.this.setColor(paramAnonymousInt);
      }
    });
    this.rgbSelector = new RgbSelectorView(getContext());
    this.rgbSelector.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
    this.rgbSelector.setOnColorChangedListener(new RgbSelectorView.OnColorChangedListener()
    {
      public void colorChanged(int paramAnonymousInt)
      {
        ColorSelectorView.this.setColor(paramAnonymousInt);
      }
    });
    this.hexSelector = new HexSelectorView(getContext());
    this.hexSelector.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
    this.hexSelector.setOnColorChangedListener(new HexSelectorView.OnColorChangedListener()
    {
      public void colorChanged(int paramAnonymousInt)
      {
        ColorSelectorView.this.setColor(paramAnonymousInt);
      }
    });
    this.tabs = ((TabHost)((View)localObject1).findViewById(2131624086));
    this.tabs.setup();
    Object localObject2 = new ColorTabContentFactory();
    localObject1 = this.tabs.newTabSpec("HSV").setIndicator(createTabView(this.tabs.getContext(), "HSV")).setContent((TabHost.TabContentFactory)localObject2);
    TabHost.TabSpec localTabSpec = this.tabs.newTabSpec("RGB").setIndicator(createTabView(this.tabs.getContext(), "RGB")).setContent((TabHost.TabContentFactory)localObject2);
    localObject2 = this.tabs.newTabSpec("HEX").setIndicator(createTabView(this.tabs.getContext(), "HEX")).setContent((TabHost.TabContentFactory)localObject2);
    this.tabs.addTab((TabHost.TabSpec)localObject1);
    this.tabs.addTab(localTabSpec);
    this.tabs.addTab((TabHost.TabSpec)localObject2);
  }
  
  private void onColorChanged()
  {
    if (this.listener != null) {
      this.listener.colorChanged(getColor());
    }
  }
  
  private void setColor(int paramInt, View paramView)
  {
    if (this.color == paramInt) {
      return;
    }
    this.color = paramInt;
    if (paramView != this.hsvSelector) {
      this.hsvSelector.setColor(paramInt);
    }
    if (paramView != this.rgbSelector) {
      this.rgbSelector.setColor(paramInt);
    }
    if (paramView != this.hexSelector) {
      this.hexSelector.setColor(paramInt);
    }
    onColorChanged();
  }
  
  public int getColor()
  {
    return this.color;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if ("HSV".equals(this.tabs.getCurrentTabTag()))
    {
      this.maxHeight = getMeasuredHeight();
      this.maxWidth = getMeasuredWidth();
    }
    setMeasuredDimension(this.maxWidth, this.maxHeight);
  }
  
  public void setColor(int paramInt)
  {
    setColor(paramInt, null);
  }
  
  public void setDialog(Dialog paramDialog)
  {
    this.hexSelector.setDialog(paramDialog);
  }
  
  public void setOnColorChangedListener(OnColorChangedListener paramOnColorChangedListener)
  {
    this.listener = paramOnColorChangedListener;
  }
  
  class ColorTabContentFactory
    implements TabHost.TabContentFactory
  {
    ColorTabContentFactory() {}
    
    public View createTabContent(String paramString)
    {
      if ("HSV".equals(paramString)) {
        return ColorSelectorView.this.hsvSelector;
      }
      if ("RGB".equals(paramString)) {
        return ColorSelectorView.this.rgbSelector;
      }
      if ("HEX".equals(paramString)) {
        return ColorSelectorView.this.hexSelector;
      }
      return null;
    }
  }
  
  public static abstract interface OnColorChangedListener
  {
    public abstract void colorChanged(int paramInt);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\Theming\ColorPicker\ColorSelectorView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */