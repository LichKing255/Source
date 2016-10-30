package org.telegram.ui.Mihangram.DownloadManager.sundatepicker.month;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.telegram.ui.Mihangram.DownloadManager.sundatepicker.DatePickerDialog;
import org.telegram.ui.Mihangram.DownloadManager.sundatepicker.tool.Date;

public class MonthMainFragement
  extends Fragment
{
  private static ViewPager mPager;
  public static int monthNumber = 0;
  public static TextView title;
  private MonthPageAdapter mAdapter;
  
  public MonthMainFragement()
  {
    setRetainInstance(true);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return paramLayoutInflater.inflate(2130903080, null);
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    if (this.mAdapter == null) {
      this.mAdapter = new MonthPageAdapter(getChildFragmentManager());
    }
    mPager = (ViewPager)paramView.findViewById(2131624130);
    mPager.setAdapter(this.mAdapter);
    title = (TextView)paramView.findViewById(2131624027);
    if (DatePickerDialog.getTypeFace() != null) {
      title.setTypeface(DatePickerDialog.getTypeFace());
    }
    mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
    {
      public void onPageScrollStateChanged(int paramAnonymousInt) {}
      
      public void onPageScrolled(int paramAnonymousInt1, float paramAnonymousFloat, int paramAnonymousInt2) {}
      
      public void onPageSelected(int paramAnonymousInt)
      {
        MonthMainFragement.title.setText(MonthMainFragement.this.mAdapter.getPageTitle(paramAnonymousInt) + " " + Date.getYear());
      }
    });
    mPager.setCurrentItem(Date.getMonth() - 1);
    Date.updateUI();
    super.onViewCreated(paramView, paramBundle);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\DownloadManager\sundatepicker\month\MonthMainFragement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */