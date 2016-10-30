package org.telegram.ui.Mihangram.DownloadManager.sundatepicker.year;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.telegram.ui.Mihangram.DownloadManager.sundatepicker.tool.Date;

public class YearMainFragement
  extends Fragment
{
  private static ViewPager mPager;
  public static int yearNumber = 0;
  private YearPageAdapter mAdapter;
  int maxYear;
  int minYear;
  int[] years;
  
  public YearMainFragement(int paramInt1, int paramInt2)
  {
    setRetainInstance(true);
    this.minYear = paramInt1;
    this.maxYear = paramInt2;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return paramLayoutInflater.inflate(2130903080, null);
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    this.years = new int[this.maxYear - this.minYear + 1];
    int i = 0;
    int j = this.minYear;
    while (j <= this.maxYear)
    {
      this.years[i] = j;
      j += 1;
      i += 1;
    }
    if (this.mAdapter == null) {
      this.mAdapter = new YearPageAdapter(getChildFragmentManager(), this.years);
    }
    mPager = (ViewPager)paramView.findViewById(2131624130);
    mPager.setAdapter(this.mAdapter);
    ((TextView)paramView.findViewById(2131624027)).setVisibility(4);
    mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
    {
      public void onPageScrollStateChanged(int paramAnonymousInt) {}
      
      public void onPageScrolled(int paramAnonymousInt1, float paramAnonymousFloat, int paramAnonymousInt2) {}
      
      public void onPageSelected(int paramAnonymousInt)
      {
        Date.setYear(YearMainFragement.this.mAdapter.getYear(paramAnonymousInt));
        Date.updateUI();
      }
    });
    super.onViewCreated(paramView, paramBundle);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\DownloadManager\sundatepicker\year\YearMainFragement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */