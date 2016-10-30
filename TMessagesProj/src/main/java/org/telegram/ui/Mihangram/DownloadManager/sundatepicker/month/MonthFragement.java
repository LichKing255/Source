package org.telegram.ui.Mihangram.DownloadManager.sundatepicker.month;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class MonthFragement
  extends Fragment
{
  int month;
  
  public MonthFragement(int paramInt)
  {
    this.month = paramInt;
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
    return paramLayoutInflater.inflate(2130903073, null);
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    GridView localGridView = (GridView)paramView.findViewById(2131624111);
    localGridView.setSelector(getResources().getDrawable(2130838195));
    localGridView.setAdapter(new MonthAdapter(getActivity(), this.month));
    super.onViewCreated(paramView, paramBundle);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\DownloadManager\sundatepicker\month\MonthFragement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */