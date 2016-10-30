package org.telegram.ui.Mihangram.DownloadManager.Adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.ProgressView;

public class downloadItemViewHolder
  extends RecyclerView.ViewHolder
{
  public View container;
  public CardView cv;
  public TextView downloadedSize;
  public ProgressView f6351c;
  public TextView file_name;
  public CheckBox itemCheck;
  public ImageView item_delete;
  public ImageView item_play;
  public TextView percentage;
  public ProgressBar progressbar;
  public TextView totalSize;
  
  public downloadItemViewHolder(View paramView)
  {
    super(paramView);
    this.cv = ((CardView)paramView.findViewById(2131624167));
    this.file_name = ((TextView)paramView.findViewById(2131624175));
    this.itemCheck = ((CheckBox)paramView.findViewById(2131624084));
    this.progressbar = ((ProgressBar)paramView.findViewById(2131624174));
    this.downloadedSize = ((TextView)paramView.findViewById(2131624171));
    this.totalSize = ((TextView)paramView.findViewById(2131624172));
    this.percentage = ((TextView)paramView.findViewById(2131624173));
    this.item_play = ((ImageView)paramView.findViewById(2131624176));
    this.item_delete = ((ImageView)paramView.findViewById(2131624177));
    this.container = paramView;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\DownloadManager\Adapter\downloadItemViewHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */