package org.telegram.ui.Supergram.DownloadManager.sundatepicker.tool;

import android.widget.TextView;
import org.telegram.ui.Supergram.DownloadManager.sundatepicker.DatePickerDialog;
import org.telegram.ui.Supergram.DownloadManager.sundatepicker.month.MonthMainFragement;

public class Date
{
  static int day;
  static TextView dayText = null;
  static int month;
  static TextView todayText = null;
  static int year;
  static TextView yearText = null;
  
  public static int getDay()
  {
    return day;
  }
  
  public static TextView getDayText()
  {
    return dayText;
  }
  
  public static int getMonth()
  {
    return month;
  }
  
  public static TextView getTodayText()
  {
    return todayText;
  }
  
  public static int getYear()
  {
    return year;
  }
  
  public static TextView getYearText()
  {
    return yearText;
  }
  
  public static void setDate(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    year = paramInt1;
    month = paramInt2;
    day = paramInt3;
    if (paramBoolean) {
      updateUI();
    }
  }
  
  public static void setDay(int paramInt)
  {
    day = paramInt;
  }
  
  public static void setDayText(TextView paramTextView)
  {
    dayText = paramTextView;
  }
  
  public static void setMonth(int paramInt)
  {
    month = paramInt;
  }
  
  public static void setTodayText(TextView paramTextView)
  {
    todayText = paramTextView;
  }
  
  public static void setYear(int paramInt)
  {
    year = paramInt;
  }
  
  public static void setYearText(TextView paramTextView)
  {
    yearText = paramTextView;
  }
  
  public static void updateUI()
  {
    DatePickerDialog.updateDisplay(year, month, day);
    try
    {
      MonthMainFragement.title.setText(JDF.monthNames[(month - 1)] + " " + year);
      return;
    }
    catch (Exception localException) {}
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\DownloadManager\sundatepicker\tool\Date.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */