package org.telegram.ui.Mihangram.DownloadManager.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.rey.material.widget.CheckBox;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.ui.Mihangram.DownloadManager.SQLite.ElementDownload;

public class RVAdapter
  extends RecyclerView.Adapter<downloadItemViewHolder>
{
  public List ED;
  Context context;
  private OnItemClickListener mOnItemClickListener;
  private OnCheckedChangeListener mOnchClickListener;
  
  public RVAdapter(Context paramContext, List paramList, OnItemClickListener paramOnItemClickListener, OnCheckedChangeListener paramOnCheckedChangeListener)
  {
    this.context = paramContext;
    this.mOnItemClickListener = paramOnItemClickListener;
    this.mOnchClickListener = paramOnCheckedChangeListener;
    this.ED = paramList;
  }
  
  public void dataSetChanged()
  {
    notifyDataSetChanged();
  }
  
  public int getItemCount()
  {
    return this.ED.size();
  }
  
  public void onBindViewHolder(downloadItemViewHolder paramdownloadItemViewHolder, final int paramInt)
  {
    ElementDownload localElementDownload = (ElementDownload)this.ED.get(paramInt);
    TLRPC.TL_document localTL_document = new TLRPC.TL_document();
    localTL_document.id = Long.parseLong(localElementDownload.getId());
    localTL_document.access_hash = Long.parseLong(localElementDownload.getAccess_hash());
    localTL_document.date = Integer.parseInt(localElementDownload.getDate());
    localTL_document.mime_type = localElementDownload.getMime_type();
    localTL_document.size = localElementDownload.getSize();
    localTL_document.dc_id = localElementDownload.getDc_id();
    localTL_document.user_id = localElementDownload.getUser_id();
    String str1;
    label150:
    TextView localTextView;
    if ((!localElementDownload.state) || (localElementDownload.getProg() == 1.0F))
    {
      paramdownloadItemViewHolder.item_play.setImageResource(2130838064);
      if (localElementDownload.getProg() != 1.0F) {
        break label358;
      }
      str1 = AndroidUtilities.formatFileSize(localTL_document.size);
      localTextView = paramdownloadItemViewHolder.file_name;
      if (localElementDownload.getType() != 3) {
        break label371;
      }
    }
    label358:
    label371:
    for (String str2 = FileLoader.getAttachFileName(localTL_document);; str2 = localElementDownload.file_name)
    {
      localTextView.setText(str2);
      paramdownloadItemViewHolder.item_play.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          RVAdapter.this.mOnItemClickListener.onItemClick(paramAnonymousView, paramInt);
        }
      });
      paramdownloadItemViewHolder.item_delete.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          RVAdapter.this.mOnItemClickListener.onItemClick(paramAnonymousView, paramInt);
        }
      });
      paramdownloadItemViewHolder.cv.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          RVAdapter.this.mOnItemClickListener.onItemClick(paramAnonymousView, paramInt);
        }
      });
      paramdownloadItemViewHolder.itemCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
      {
        public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
        {
          RVAdapter.this.mOnchClickListener.onItemCh(paramAnonymousCompoundButton, paramAnonymousBoolean, paramInt);
        }
      });
      paramdownloadItemViewHolder.itemCheck.setChecked(localElementDownload.isCheck());
      paramdownloadItemViewHolder.totalSize.setText(str1);
      paramdownloadItemViewHolder.progressbar.setProgress((int)(localElementDownload.getProg() * 100.0F));
      paramdownloadItemViewHolder.downloadedSize.setText(AndroidUtilities.formatFileSize((int)(localElementDownload.getProg() * 100.0F) * localTL_document.size / 100));
      paramdownloadItemViewHolder.percentage.setText(String.valueOf((int)(localElementDownload.getProg() * 100.0F)) + "%");
      return;
      paramdownloadItemViewHolder.item_play.setImageResource(2130838062);
      break;
      str1 = AndroidUtilities.formatFileSize(localTL_document.size);
      break label150;
    }
  }
  
  public downloadItemViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    return new downloadItemViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(2130903092, paramViewGroup, false));
  }
  
  public static abstract interface OnCheckedChangeListener
  {
    public abstract void onItemCh(CompoundButton paramCompoundButton, boolean paramBoolean, int paramInt);
  }
  
  public static abstract interface OnItemClickListener
  {
    public abstract void onItemClick(View paramView, int paramInt);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\DownloadManager\Adapter\RVAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */