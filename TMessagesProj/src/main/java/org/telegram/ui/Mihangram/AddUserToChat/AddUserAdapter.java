package org.telegram.ui.Mihangram.AddUserToChat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.ui.Cells.LoadingCell;

public class AddUserAdapter
  extends RecyclerView.Adapter
{
  private int currentCount;
  private Context mContext;
  
  public AddUserAdapter(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private ArrayList<TLRPC.TL_dialog> getDialogsArray()
  {
    ArrayList localArrayList1 = new ArrayList();
    localArrayList1.addAll(MessagesController.getInstance().dialogs);
    ArrayList localArrayList2 = new ArrayList();
    int i = 0;
    if (i < localArrayList1.size())
    {
      TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)localArrayList1.get(i);
      int k = (int)localTL_dialog.id;
      int j = (int)(localTL_dialog.id >> 32);
      if ((k < 0) && (j != 1)) {}
      for (j = 1;; j = 0)
      {
        TLRPC.Chat localChat = MessagesController.getInstance().getChat(Integer.valueOf(-k));
        if ((j != 0) && ((localChat.creator) || (localChat.editor))) {
          localArrayList2.add(localTL_dialog);
        }
        i += 1;
        break;
      }
    }
    return localArrayList2;
  }
  
  public TLRPC.TL_dialog getItem(int paramInt)
  {
    ArrayList localArrayList = getDialogsArray();
    if ((paramInt < 0) || (paramInt >= localArrayList.size())) {
      return null;
    }
    return (TLRPC.TL_dialog)localArrayList.get(paramInt);
  }
  
  public int getItemCount()
  {
    int j = getDialogsArray().size();
    if ((j == 0) && (MessagesController.getInstance().loadingDialogs)) {
      return 0;
    }
    int i = j;
    if (!MessagesController.getInstance().dialogsEndReached) {
      i = j + 1;
    }
    this.currentCount = i;
    return i;
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public int getItemViewType(int paramInt)
  {
    if (paramInt == getDialogsArray().size()) {
      return 1;
    }
    return 0;
  }
  
  public boolean isDataSetChanged()
  {
    int i = this.currentCount;
    return (i != getItemCount()) || (i == 1);
  }
  
  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    if (paramViewHolder.getItemViewType() == 0)
    {
      paramViewHolder = (AddUserDialogCell)paramViewHolder.itemView;
      if (paramInt == getItemCount() - 1) {
        break label43;
      }
    }
    label43:
    for (boolean bool = true;; bool = false)
    {
      paramViewHolder.useSeparator = bool;
      paramViewHolder.setDialog(getItem(paramInt), paramInt);
      return;
    }
  }
  
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    paramViewGroup = null;
    if (paramInt == 0) {
      paramViewGroup = new AddUserDialogCell(this.mContext);
    }
    for (;;)
    {
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
      return new Holder(paramViewGroup);
      if (paramInt == 1) {
        paramViewGroup = new LoadingCell(this.mContext);
      }
    }
  }
  
  public void onViewAttachedToWindow(RecyclerView.ViewHolder paramViewHolder)
  {
    if ((paramViewHolder.itemView instanceof AddUserDialogCell)) {
      ((AddUserDialogCell)paramViewHolder.itemView).checkCurrentDialogIndex();
    }
  }
  
  private class Holder
    extends RecyclerView.ViewHolder
  {
    public Holder(View paramView)
    {
      super();
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\AddUserToChat\AddUserAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */