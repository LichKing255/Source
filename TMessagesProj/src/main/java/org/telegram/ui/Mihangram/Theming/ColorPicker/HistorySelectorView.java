package org.telegram.ui.Mihangram.Theming.ColorPicker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import java.util.Iterator;
import java.util.LinkedList;
import org.json.JSONArray;
import org.json.JSONException;

public class HistorySelectorView
  extends LinearLayout
{
  private static final String HISTORY = "HISTORY";
  private static final int MAX_COLORS = 30;
  private static final String PREFS_NAME = "RECENT_COLORS";
  int color;
  JSONArray colors;
  OnColorChangedListener listener;
  
  public HistorySelectorView(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public HistorySelectorView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  private int getColor()
  {
    return this.color;
  }
  
  private void init()
  {
    addView(((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(2130903068, null), new LinearLayout.LayoutParams(-1, -1));
    readColors();
    makeColorList();
  }
  
  private void makeColorList()
  {
    LayoutInflater localLayoutInflater = (LayoutInflater)getContext().getSystemService("layout_inflater");
    LinearLayout localLinearLayout = (LinearLayout)findViewById(2131624091);
    if ((this.colors == null) || (this.colors.length() <= 0))
    {
      findViewById(2131624092).setVisibility(0);
      localLinearLayout.setVisibility(8);
      findViewById(2131624090).setVisibility(8);
    }
    for (;;)
    {
      return;
      try
      {
        int i = this.colors.length() - 1;
        while (i >= 0)
        {
          final int j = this.colors.getInt(i);
          ViewGroup localViewGroup = (ViewGroup)localLayoutInflater.inflate(2130903069, localLinearLayout, false);
          TextView localTextView = (TextView)localViewGroup.findViewById(2131624093);
          localTextView.setBackgroundColor(j);
          localLinearLayout.addView(localViewGroup);
          localTextView.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              HistorySelectorView.this.setColor(j);
              HistorySelectorView.this.onColorChanged();
            }
          });
          i -= 1;
        }
        return;
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
    }
  }
  
  private void onColorChanged()
  {
    if (this.listener != null) {
      this.listener.colorChanged(getColor());
    }
  }
  
  private void setColor(int paramInt)
  {
    this.color = paramInt;
  }
  
  public JSONArray moveValueToFront(JSONArray paramJSONArray, int paramInt1, int paramInt2)
    throws JSONException
  {
    Object localObject = new LinkedList();
    int i = 0;
    while (i < paramJSONArray.length())
    {
      ((LinkedList)localObject).add(Integer.valueOf(paramJSONArray.getInt(i)));
      i += 1;
    }
    ((LinkedList)localObject).add(Integer.valueOf(paramInt2));
    ((LinkedList)localObject).remove(paramInt1);
    paramJSONArray = new JSONArray();
    localObject = ((LinkedList)localObject).iterator();
    while (((Iterator)localObject).hasNext()) {
      paramJSONArray.put(((Integer)((Iterator)localObject).next()).intValue());
    }
    return paramJSONArray;
  }
  
  public void readColors()
  {
    SharedPreferences localSharedPreferences = getContext().getSharedPreferences("RECENT_COLORS", 0);
    try
    {
      this.colors = new JSONArray(localSharedPreferences.getString("HISTORY", ""));
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public void selectColor(int paramInt)
  {
    for (;;)
    {
      int j;
      int i;
      try
      {
        localObject = getContext().getSharedPreferences("RECENT_COLORS", 0);
        if (this.colors != null) {
          break label201;
        }
        this.colors = new JSONArray();
      }
      catch (Exception localException)
      {
        Object localObject;
        JSONArray localJSONArray;
        localException.printStackTrace();
        return;
      }
      if (j < this.colors.length())
      {
        if (this.colors.getInt(j) == paramInt)
        {
          i = 1;
          this.colors = moveValueToFront(this.colors, j, paramInt);
        }
      }
      else
      {
        if (i == 0) {
          this.colors.put(paramInt);
        }
        if (this.colors.length() > 30)
        {
          localJSONArray = new JSONArray();
          paramInt = this.colors.length() - 30;
          if (paramInt < this.colors.length())
          {
            localJSONArray.put(this.colors.getInt(paramInt));
            paramInt += 1;
            continue;
          }
          this.colors = localJSONArray;
        }
        localObject = ((SharedPreferences)localObject).edit();
        ((SharedPreferences.Editor)localObject).putString("HISTORY", this.colors.toString());
        ((SharedPreferences.Editor)localObject).commit();
        return;
        label201:
        i = 0;
        j = 0;
        continue;
      }
      j += 1;
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


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\Theming\ColorPicker\HistorySelectorView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */