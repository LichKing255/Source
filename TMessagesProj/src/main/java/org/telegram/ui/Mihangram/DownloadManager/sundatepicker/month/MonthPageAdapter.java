package org.telegram.ui.Supergram.DownloadManager.sundatepicker.month;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import org.telegram.ui.Supergram.DownloadManager.sundatepicker.DatePickerDialog;
import org.telegram.ui.Supergram.DownloadManager.sundatepicker.tool.Date;
import org.telegram.ui.Supergram.DownloadManager.sundatepicker.tool.JDF;

public class MonthPageAdapter
  extends FragmentPagerAdapter
{
  private String[] monthNames = { "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند" };
  
  public MonthPageAdapter(FragmentManager paramFragmentManager)
  {
    super(paramFragmentManager);
  }
  
  public int getCount()
  {
    if ((DatePickerDialog.maxMonth > 0) && (new JDF().getIranianYear() == Date.getYear())) {
      return DatePickerDialog.maxMonth;
    }
    return this.monthNames.length;
  }
  
  public Fragment getItem(int paramInt)
  {
    return new MonthFragement(paramInt);
  }
  
  public CharSequence getPageTitle(int paramInt)
  {
    return this.monthNames[paramInt];
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\DownloadManager\sundatepicker\month\MonthPageAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */