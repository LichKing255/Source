package org.telegram.ui.Mihangram.DownloadManager.sundatepicker.month;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.text.ParseException;
import org.telegram.ui.Mihangram.DownloadManager.sundatepicker.DatePickerDialog;
import org.telegram.ui.Mihangram.DownloadManager.sundatepicker.tool.Date;
import org.telegram.ui.Mihangram.DownloadManager.sundatepicker.tool.JDF;
import org.telegram.ui.Mihangram.DownloadManager.sundatepicker.tool.Util;

public class MonthAdapter
  extends BaseAdapter
{
  private Context context;
  int month;
  int startDay;
  JDF today;
  Typeface typeface;
  
  public MonthAdapter(Context paramContext, int paramInt)
  {
    this.context = paramContext;
    this.month = paramInt;
    this.today = new JDF();
    this.typeface = DatePickerDialog.getTypeFace();
    try
    {
      this.startDay = new JDF().getIranianDay(Date.getYear(), paramInt + 1, 1);
      return;
    }
    catch (ParseException paramContext) {}
  }
  
  public int getCount()
  {
    int i = 30;
    if (this.month < 6) {
      i = 31;
    }
    if (DatePickerDialog.maxMonth == this.month + 1) {
      i = this.today.getIranianDay();
    }
    return i + 7 + this.startDay;
  }
  
  public Object getItem(int paramInt)
  {
    return null;
  }
  
  public long getItemId(int paramInt)
  {
    return 0L;
  }
  
  public View getView(int paramInt, final View paramView, ViewGroup paramViewGroup)
  {
    paramInt -= 7;
    View localView = paramView;
    if (paramView == null) {
      localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(2130903074, null);
    }
    paramView = (TextView)localView.findViewById(2131624112);
    if (this.typeface != null) {
      paramView.setTypeface(this.typeface);
    }
    paramView.setBackgroundColor(this.context.getResources().getColor(17170445));
    paramView.setTextColor(DatePickerDialog.getGrayColor());
    if ((paramInt >= 0) && (paramInt - this.startDay >= 0))
    {
      paramInt -= this.startDay;
      paramView.setText(String.valueOf(paramInt + 1));
      if ((this.month + 1 == this.today.getIranianMonth()) && (paramInt + 1 == this.today.getIranianDay()) && (Date.getYear() == this.today.getIranianYear()))
      {
        paramView.setBackgroundColor(this.context.getResources().getColor(17170445));
        paramView.setTextColor(DatePickerDialog.getBlueColor());
        Date.setTodayText(paramView);
      }
      if ((Date.getMonth() == this.month + 1) && (Date.getDay() == paramInt + 1))
      {
        Date.setDayText(paramView);
        paramView.setBackgroundDrawable(DatePickerDialog.getCircle());
        paramView.setTextColor(DatePickerDialog.getGrayColor());
      }
      paramView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (Date.getDayText() != null)
          {
            Date.getDayText().setBackgroundColor(MonthAdapter.this.context.getResources().getColor(17170445));
            Date.getDayText().setTextColor(DatePickerDialog.getGrayColor());
          }
          if (Date.getTodayText() != null)
          {
            Date.getTodayText().setBackgroundColor(MonthAdapter.this.context.getResources().getColor(17170445));
            Date.getTodayText().setTextColor(DatePickerDialog.getBlueColor());
          }
          Date.setDay(this.val$day);
          Date.setMonth(MonthAdapter.this.month + 1);
          Date.setDayText(paramView);
          Date.updateUI();
          paramView.setBackgroundDrawable(DatePickerDialog.getCircle());
          paramView.setTextColor(DatePickerDialog.getGrayColor());
          Util.tryVibrate(MonthAdapter.this.context);
        }
      });
    }
    while (paramInt >= 0) {
      return localView;
    }
    paramView.setText(JDF.iranianDayNames[(paramInt + 7)].substring(0, 1));
    return localView;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\DownloadManager\sundatepicker\month\MonthAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */