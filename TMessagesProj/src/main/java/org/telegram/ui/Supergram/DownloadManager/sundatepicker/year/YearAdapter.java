package org.telegram.ui.Supergram.DownloadManager.sundatepicker.year;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.ui.Supergram.DownloadManager.sundatepicker.DatePickerDialog;
import org.telegram.ui.Supergram.DownloadManager.sundatepicker.tool.Date;
import org.telegram.ui.Supergram.DownloadManager.sundatepicker.tool.Util;

public class YearAdapter
  extends BaseAdapter
{
  private Context context;
  Typeface typeface;
  int[] years;
  
  public YearAdapter(Context paramContext, int[] paramArrayOfInt)
  {
    this.context = paramContext;
    this.years = paramArrayOfInt;
    this.typeface = DatePickerDialog.getTypeFace();
  }
  
  public int getCount()
  {
    return this.years.length;
  }
  
  public Object getItem(int paramInt)
  {
    return null;
  }
  
  public long getItemId(int paramInt)
  {
    return 0L;
  }
  
  public View getView(final int paramInt, final View paramView, ViewGroup paramViewGroup)
  {
    View localView = paramView;
    if (paramView == null) {
      localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(2130903131, null);
    }
    paramView = (TextView)localView.findViewById(2131624113);
    if (this.typeface != null) {
      paramView.setTypeface(this.typeface);
    }
    paramView.setText(String.valueOf(this.years[paramInt]));
    paramView.setBackgroundColor(this.context.getResources().getColor(17170445));
    if (this.years[paramInt] == Date.getYear())
    {
      Date.setYearText(paramView);
      paramView.setBackgroundDrawable(DatePickerDialog.getCircle());
    }
    paramView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (Date.getYearText() != null) {
          Date.getYearText().setBackgroundColor(YearAdapter.this.context.getResources().getColor(17170445));
        }
        Date.setYear(YearAdapter.this.years[paramInt]);
        Date.setYearText(paramView);
        Date.updateUI();
        paramView.setBackgroundDrawable(DatePickerDialog.getCircle());
        Util.tryVibrate(YearAdapter.this.context);
        DatePickerDialog.dayMonth.performClick();
      }
    });
    return localView;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\DownloadManager\sundatepicker\year\YearAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */