package org.telegram.ui.Supergram.Theming.ColorPicker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import java.io.PrintStream;
import org.telegram.ui.Components.ColorPickerView.OnColorChangedListener;
import org.telegram.ui.Supergram.Theming.MihanTheme;

public class ColorSelectorDialog
  extends Dialog
  implements ColorPickerView.OnColorChangedListener, View.OnClickListener
{
  public static final int BOTTOM = 1;
  public static final int CENTER = 0;
  public static final int LEFT = 4;
  public static final int RIGHT = 2;
  public static final int TOP = 3;
  private boolean alpha;
  private Button btnNew;
  private Button btnOld;
  private int color;
  private ColorSelectorView content;
  private HistorySelectorView history;
  private int initColor;
  private OnColorChangedListener listener;
  private int offset;
  private int side;
  
  public ColorSelectorDialog(Context paramContext, int paramInt1, int paramInt2)
  {
    super(paramContext, 2131362211);
    this.initColor = paramInt1;
    this.side = paramInt2;
    this.offset = this.offset;
  }
  
  public ColorSelectorDialog(Context paramContext, OnColorChangedListener paramOnColorChangedListener, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    super(paramContext, 2131362211);
    this.listener = paramOnColorChangedListener;
    this.initColor = paramInt1;
    this.side = paramInt2;
    this.offset = paramInt3;
    this.alpha = paramBoolean;
  }
  
  private void colorChangedInternal(int paramInt)
  {
    this.btnNew.setBackgroundColor(paramInt);
    this.btnNew.setTextColor(paramInt ^ 0xFFFFFFFF | 0xFF000000);
    this.color = getLighterColor(paramInt, this.alpha);
  }
  
  private int getLighterColor(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = Color.alpha(paramInt);; i = 255) {
      return Color.argb(i, Color.red(paramInt), Color.green(paramInt), Color.blue(paramInt));
    }
  }
  
  public int getColor()
  {
    return this.color;
  }
  
  public void onClick(View paramView)
  {
    if (this.listener != null) {
      this.listener.colorChanged(this.color);
    }
    this.history.selectColor(this.color);
    dismiss();
  }
  
  public void onColorChanged(int paramInt)
  {
    if (this.listener != null) {
      this.listener.colorChanged(getColor());
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903072);
    if (this.side == 2)
    {
      getWindow().setGravity(5);
      paramBundle = getWindow().getAttributes();
      paramBundle.x = this.offset;
      getWindow().setAttributes(paramBundle);
    }
    for (;;)
    {
      this.btnOld = ((Button)findViewById(2131624109));
      this.btnOld.setTypeface(MihanTheme.getMihanTypeFace());
      this.btnOld.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          ColorSelectorDialog.this.dismiss();
        }
      });
      this.btnNew = ((Button)findViewById(2131624110));
      this.btnNew.setTypeface(MihanTheme.getMihanTypeFace());
      this.btnNew.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (ColorSelectorDialog.this.listener != null) {
            ColorSelectorDialog.this.listener.colorChanged(ColorSelectorDialog.this.color);
          }
          ColorSelectorDialog.this.history.selectColor(ColorSelectorDialog.this.color);
          ColorSelectorDialog.this.dismiss();
        }
      });
      this.content = ((ColorSelectorView)findViewById(2131624107));
      this.content.setDialog(this);
      this.content.setOnColorChangedListener(new ColorSelectorView.OnColorChangedListener()
      {
        public void colorChanged(int paramAnonymousInt)
        {
          ColorSelectorDialog.this.colorChangedInternal(paramAnonymousInt);
        }
      });
      this.history = ((HistorySelectorView)findViewById(2131624108));
      this.history.setOnColorChangedListener(new HistorySelectorView.OnColorChangedListener()
      {
        public void colorChanged(int paramAnonymousInt)
        {
          ColorSelectorDialog.this.colorChangedInternal(paramAnonymousInt);
          ColorSelectorDialog.this.content.setColor(paramAnonymousInt);
        }
      });
      this.btnOld.setBackgroundColor(this.initColor);
      this.btnOld.setTextColor(this.initColor ^ 0xFFFFFFFF | 0xFF000000);
      this.content.setColor(this.initColor);
      return;
      if (this.side != 1) {}
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getAction() == 4)
    {
      System.out.println("TOuch outside the dialog ******************** ");
      dismiss();
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void setColor(int paramInt)
  {
    this.content.setColor(paramInt);
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


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\Theming\ColorPicker\ColorSelectorDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */