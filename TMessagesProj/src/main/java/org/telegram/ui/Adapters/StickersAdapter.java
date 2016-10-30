package org.telegram.ui.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.Cells.StickerCell;

public class StickersAdapter
  extends RecyclerView.Adapter
  implements NotificationCenter.NotificationCenterDelegate
{
  private StickersAdapterDelegate delegate;
  private String lastSticker;
  private Context mContext;
  private ArrayList<Long> newRecentStickers = new ArrayList();
  private long recentLoadDate;
  private ArrayList<TLRPC.Document> stickers;
  private ArrayList<String> stickersToLoad = new ArrayList();
  private boolean visible;
  
  public StickersAdapter(Context paramContext, StickersAdapterDelegate paramStickersAdapterDelegate)
  {
    this.mContext = paramContext;
    this.delegate = paramStickersAdapterDelegate;
    StickersQuery.checkStickers();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
  }
  
  private boolean checkStickerFilesExistAndDownload()
  {
    if (this.stickers == null) {
      return false;
    }
    this.stickersToLoad.clear();
    int j = Math.min(10, this.stickers.size());
    int i = 0;
    while (i < j)
    {
      TLRPC.Document localDocument = (TLRPC.Document)this.stickers.get(i);
      if (!FileLoader.getPathToAttach(localDocument.thumb, "webp", true).exists())
      {
        this.stickersToLoad.add(FileLoader.getAttachFileName(localDocument.thumb, "webp"));
        FileLoader.getInstance().loadFile(localDocument.thumb.location, "webp", 0, true);
      }
      i += 1;
    }
    return this.stickersToLoad.isEmpty();
  }
  
  public void clearStickers()
  {
    this.lastSticker = null;
    this.stickers = null;
    this.stickersToLoad.clear();
    notifyDataSetChanged();
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    boolean bool2 = false;
    if (((paramInt == NotificationCenter.FileDidLoaded) || (paramInt == NotificationCenter.FileDidFailedLoad)) && (this.stickers != null) && (!this.stickers.isEmpty()) && (!this.stickersToLoad.isEmpty()) && (this.visible))
    {
      paramVarArgs = (String)paramVarArgs[0];
      this.stickersToLoad.remove(paramVarArgs);
      if (this.stickersToLoad.isEmpty())
      {
        paramVarArgs = this.delegate;
        boolean bool1 = bool2;
        if (this.stickers != null)
        {
          bool1 = bool2;
          if (!this.stickers.isEmpty())
          {
            bool1 = bool2;
            if (this.stickersToLoad.isEmpty()) {
              bool1 = true;
            }
          }
        }
        paramVarArgs.needChangePanelVisibility(bool1);
      }
    }
  }
  
  public TLRPC.Document getItem(int paramInt)
  {
    if ((this.stickers != null) && (paramInt >= 0) && (paramInt < this.stickers.size())) {
      return (TLRPC.Document)this.stickers.get(paramInt);
    }
    return null;
  }
  
  public int getItemCount()
  {
    if (this.stickers != null) {
      return this.stickers.size();
    }
    return 0;
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public void loadStikersForEmoji(CharSequence paramCharSequence)
  {
    int i;
    int k;
    int j;
    if ((paramCharSequence != null) && (paramCharSequence.length() > 0) && (paramCharSequence.length() <= 14))
    {
      i = 1;
      if (i != 0)
      {
        k = paramCharSequence.length();
        j = 0;
      }
    }
    for (;;)
    {
      CharSequence localCharSequence = paramCharSequence;
      if (j < k)
      {
        if ((j < k - 1) && (paramCharSequence.charAt(j) == 55356) && (paramCharSequence.charAt(j + 1) >= 57339) && (paramCharSequence.charAt(j + 1) <= 57343)) {
          localCharSequence = TextUtils.concat(new CharSequence[] { paramCharSequence.subSequence(0, j), paramCharSequence.subSequence(j + 2, paramCharSequence.length()) });
        }
      }
      else
      {
        this.lastSticker = localCharSequence.toString();
        paramCharSequence = StickersQuery.getAllStickers();
        if (paramCharSequence != null)
        {
          paramCharSequence = (ArrayList)paramCharSequence.get(this.lastSticker);
          if ((this.stickers == null) || (paramCharSequence != null)) {
            break label315;
          }
          if (this.visible)
          {
            this.delegate.needChangePanelVisibility(false);
            this.visible = false;
          }
        }
        if ((i == 0) && (this.visible) && (this.stickers != null))
        {
          this.visible = false;
          this.delegate.needChangePanelVisibility(false);
        }
        return;
        i = 0;
        break;
      }
      int m = k;
      localCharSequence = paramCharSequence;
      if (paramCharSequence.charAt(j) == 65039)
      {
        localCharSequence = TextUtils.concat(new CharSequence[] { paramCharSequence.subSequence(0, j), paramCharSequence.subSequence(j + 1, paramCharSequence.length()) });
        m = k - 1;
      }
      j += 1;
      k = m;
      paramCharSequence = localCharSequence;
    }
    label315:
    if ((paramCharSequence != null) && (!paramCharSequence.isEmpty())) {}
    for (paramCharSequence = new ArrayList(paramCharSequence);; paramCharSequence = null)
    {
      this.stickers = paramCharSequence;
      if (this.stickers != null) {
        if (Math.abs(this.recentLoadDate - System.currentTimeMillis()) > 10000L) {
          this.recentLoadDate = System.currentTimeMillis();
        }
      }
      for (;;)
      {
        try
        {
          paramCharSequence = this.mContext.getSharedPreferences("emoji", 0).getString("stickers2", "").split(",");
          j = 0;
          if (j < paramCharSequence.length)
          {
            if (paramCharSequence[j].length() == 0) {
              break label550;
            }
            long l = Utilities.parseLong(paramCharSequence[j]).longValue();
            if (l == 0L) {
              break label550;
            }
            this.newRecentStickers.add(Long.valueOf(l));
          }
        }
        catch (Exception paramCharSequence)
        {
          FileLog.e("tmessages", paramCharSequence);
        }
        if (!this.newRecentStickers.isEmpty()) {
          Collections.sort(this.stickers, new Comparator()
          {
            public int compare(TLRPC.Document paramAnonymousDocument1, TLRPC.Document paramAnonymousDocument2)
            {
              int i = StickersAdapter.this.newRecentStickers.indexOf(Long.valueOf(paramAnonymousDocument1.id));
              int j = StickersAdapter.this.newRecentStickers.indexOf(Long.valueOf(paramAnonymousDocument2.id));
              if (i > j) {
                return -1;
              }
              if (i < j) {
                return 1;
              }
              return 0;
            }
          });
        }
        checkStickerFilesExistAndDownload();
        paramCharSequence = this.delegate;
        if ((this.stickers != null) && (!this.stickers.isEmpty()) && (this.stickersToLoad.isEmpty())) {}
        for (boolean bool = true;; bool = false)
        {
          paramCharSequence.needChangePanelVisibility(bool);
          notifyDataSetChanged();
          this.visible = true;
          break;
        }
        label550:
        j += 1;
      }
    }
  }
  
  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    int i = 0;
    if (paramInt == 0) {
      if (this.stickers.size() == 1) {
        i = 2;
      }
    }
    for (;;)
    {
      ((StickerCell)paramViewHolder.itemView).setSticker((TLRPC.Document)this.stickers.get(paramInt), i);
      return;
      i = -1;
      continue;
      if (paramInt == this.stickers.size() - 1) {
        i = 1;
      }
    }
  }
  
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    return new Holder(new StickerCell(this.mContext));
  }
  
  public void onDestroy()
  {
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailedLoad);
  }
  
  private class Holder
    extends RecyclerView.ViewHolder
  {
    public Holder(View paramView)
    {
      super();
    }
  }
  
  public static abstract interface StickersAdapterDelegate
  {
    public abstract void needChangePanelVisibility(boolean paramBoolean);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Adapters\StickersAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */