package org.telegram.ui.Mihangram.DownloadManager.sundatepicker.year;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class YearPageAdapter
  extends FragmentPagerAdapter
{
  int[] years;
  
  public YearPageAdapter(FragmentManager paramFragmentManager, int[] paramArrayOfInt)
  {
    super(paramFragmentManager);
    this.years = paramArrayOfInt;
  }
  
  public int getCount()
  {
    return 1;
  }
  
  public Fragment getItem(int paramInt)
  {
    return new YearFragement(this.years);
  }
  
  public CharSequence getPageTitle(int paramInt)
  {
    return "" + this.years[paramInt];
  }
  
  public int getYear(int paramInt)
  {
    return this.years[paramInt];
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\DownloadManager\sundatepicker\year\YearPageAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */