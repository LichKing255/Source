package org.telegram.ui.Supergram.DownloadManager.sundatepicker.year;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class YearFragement
  extends Fragment
{
  int[] years;
  
  public YearFragement(int[] paramArrayOfInt)
  {
    this.years = paramArrayOfInt;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return paramLayoutInflater.inflate(2130903130, paramViewGroup, false);
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    ListView localListView = (ListView)paramView.findViewById(16908298);
    localListView.setSelector(getResources().getDrawable(2130838195));
    localListView.setAdapter(new YearAdapter(getActivity(), this.years));
    super.onViewCreated(paramView, paramBundle);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\DownloadManager\sundatepicker\year\YearFragement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */