package org.telegram.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Canvas;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_reorderStickerSets;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.StickerSetCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class StickersActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private boolean needReorder;
  private int rowCount;
  private int stickersEndRow;
  private int stickersInfoRow;
  private int stickersStartRow;
  
  private void sendReorder()
  {
    if (!this.needReorder) {
      return;
    }
    StickersQuery.calcNewHash();
    this.needReorder = false;
    TLRPC.TL_messages_reorderStickerSets localTL_messages_reorderStickerSets = new TLRPC.TL_messages_reorderStickerSets();
    ArrayList localArrayList = StickersQuery.getStickerSets();
    int i = 0;
    while (i < localArrayList.size())
    {
      localTL_messages_reorderStickerSets.order.add(Long.valueOf(((TLRPC.TL_messages_stickerSet)localArrayList.get(i)).set.id));
      i += 1;
    }
    ConnectionsManager.getInstance().sendRequest(localTL_messages_reorderStickerSets, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
    });
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[0]);
  }
  
  private void updateRows()
  {
    this.rowCount = 0;
    ArrayList localArrayList = StickersQuery.getStickerSets();
    if (!localArrayList.isEmpty())
    {
      this.stickersStartRow = 0;
      this.stickersEndRow = localArrayList.size();
      this.rowCount += localArrayList.size();
    }
    for (;;)
    {
      int i = this.rowCount;
      this.rowCount = (i + 1);
      this.stickersInfoRow = i;
      if (this.listAdapter != null) {
        this.listAdapter.notifyDataSetChanged();
      }
      return;
      this.stickersStartRow = -1;
      this.stickersEndRow = -1;
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837810);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("Stickers", 2131166364));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          StickersActivity.this.finishFragment();
        }
      }
    });
    this.listAdapter = new ListAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    localFrameLayout.setBackgroundColor(-986896);
    this.listView = new RecyclerListView(paramContext);
    this.listView.setFocusable(true);
    this.listView.setTag(Integer.valueOf(7));
    paramContext = new LinearLayoutManager(paramContext);
    paramContext.setOrientation(1);
    this.listView.setLayoutManager(paramContext);
    new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.listView);
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView.setAdapter(this.listAdapter);
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        if ((paramAnonymousInt >= StickersActivity.this.stickersStartRow) && (paramAnonymousInt < StickersActivity.this.stickersEndRow) && (StickersActivity.this.getParentActivity() != null))
        {
          StickersActivity.this.sendReorder();
          paramAnonymousView = (TLRPC.TL_messages_stickerSet)StickersQuery.getStickerSets().get(paramAnonymousInt);
          ArrayList localArrayList = paramAnonymousView.documents;
          if ((localArrayList != null) && (!localArrayList.isEmpty())) {}
        }
        else
        {
          return;
        }
        StickersActivity.this.showDialog(new StickersAlert(StickersActivity.this.getParentActivity(), null, paramAnonymousView, null));
      }
    });
    return this.fragmentView;
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.stickersDidLoaded) {
      updateRows();
    }
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    StickersQuery.checkStickers();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
    updateRows();
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    sendReorder();
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
  }
  
  private class ListAdapter
    extends RecyclerView.Adapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    private void processSelectionOption(int paramInt, TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet)
    {
      int i = 1;
      Object localObject;
      if (paramInt == 0)
      {
        localObject = StickersActivity.this.getParentActivity();
        TLRPC.StickerSet localStickerSet = paramTL_messages_stickerSet.set;
        if (!paramTL_messages_stickerSet.set.disabled)
        {
          paramInt = i;
          StickersQuery.removeStickersSet((Context)localObject, localStickerSet, paramInt);
        }
      }
      do
      {
        return;
        paramInt = 2;
        break;
        if (paramInt == 1)
        {
          StickersQuery.removeStickersSet(StickersActivity.this.getParentActivity(), paramTL_messages_stickerSet.set, 0);
          return;
        }
        if (paramInt == 2) {
          try
          {
            localObject = new Intent("android.intent.action.SEND");
            ((Intent)localObject).setType("text/plain");
            ((Intent)localObject).putExtra("android.intent.extra.TEXT", String.format(Locale.US, "https://telegram.me/addstickers/%s", new Object[] { paramTL_messages_stickerSet.set.short_name }));
            StickersActivity.this.getParentActivity().startActivityForResult(Intent.createChooser((Intent)localObject, LocaleController.getString("StickersShare", 2131166370)), 500);
            return;
          }
          catch (Exception paramTL_messages_stickerSet)
          {
            FileLog.e("tmessages", paramTL_messages_stickerSet);
            return;
          }
        }
      } while (paramInt != 3);
      try
      {
        ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", String.format(Locale.US, "https://telegram.me/addstickers/%s", new Object[] { paramTL_messages_stickerSet.set.short_name })));
        Toast.makeText(StickersActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", 2131165894), 0).show();
        return;
      }
      catch (Exception paramTL_messages_stickerSet)
      {
        FileLog.e("tmessages", paramTL_messages_stickerSet);
      }
    }
    
    public int getItemCount()
    {
      return StickersActivity.this.rowCount;
    }
    
    public long getItemId(int paramInt)
    {
      if ((paramInt >= StickersActivity.this.stickersStartRow) && (paramInt < StickersActivity.this.stickersEndRow)) {
        return ((TLRPC.TL_messages_stickerSet)StickersQuery.getStickerSets().get(paramInt)).set.id;
      }
      if (paramInt == StickersActivity.this.stickersInfoRow) {
        return -2147483648L;
      }
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((paramInt >= StickersActivity.this.stickersStartRow) && (paramInt < StickersActivity.this.stickersEndRow)) {}
      while (paramInt != StickersActivity.this.stickersInfoRow) {
        return 0;
      }
      return 1;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      TLRPC.TL_messages_stickerSet localTL_messages_stickerSet;
      if (paramViewHolder.getItemViewType() == 0)
      {
        ArrayList localArrayList = StickersQuery.getStickerSets();
        paramViewHolder = (StickerSetCell)paramViewHolder.itemView;
        localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)localArrayList.get(paramInt);
        if (paramInt == localArrayList.size() - 1) {
          break label52;
        }
      }
      label52:
      for (boolean bool = true;; bool = false)
      {
        paramViewHolder.setStickersSet(localTL_messages_stickerSet, bool);
        return;
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = null;
      switch (paramInt)
      {
      default: 
      case 0: 
        for (;;)
        {
          paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
          return new Holder(paramViewGroup);
          paramViewGroup = new StickerSetCell(this.mContext);
          paramViewGroup.setBackgroundColor(-1);
          paramViewGroup.setBackgroundResource(2130837935);
          ((StickerSetCell)paramViewGroup).setOnOptionsClick(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              StickersActivity.this.sendReorder();
              final TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = ((StickerSetCell)paramAnonymousView.getParent()).getStickersSet();
              AlertDialog.Builder localBuilder = new AlertDialog.Builder(StickersActivity.this.getParentActivity());
              localBuilder.setTitle(localTL_messages_stickerSet.set.title);
              if (localTL_messages_stickerSet.set.official)
              {
                arrayOfInt = new int[1];
                arrayOfInt[0] = 0;
                arrayOfCharSequence = new CharSequence[1];
                if (!localTL_messages_stickerSet.set.disabled) {}
                for (paramAnonymousView = LocaleController.getString("StickersHide", 2131166366);; paramAnonymousView = LocaleController.getString("StickersShow", 2131166371))
                {
                  arrayOfCharSequence[0] = paramAnonymousView;
                  paramAnonymousView = arrayOfCharSequence;
                  localBuilder.setItems(paramAnonymousView, new DialogInterface.OnClickListener()
                  {
                    public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                    {
                      StickersActivity.ListAdapter.this.processSelectionOption(arrayOfInt[paramAnonymous2Int], localTL_messages_stickerSet);
                    }
                  });
                  StickersActivity.this.showDialog(localBuilder.create());
                  return;
                }
              }
              final int[] arrayOfInt = new int[4];
              int[] tmp155_154 = arrayOfInt;
              tmp155_154[0] = 0;
              int[] tmp159_155 = tmp155_154;
              tmp159_155[1] = 1;
              int[] tmp163_159 = tmp159_155;
              tmp163_159[2] = 2;
              int[] tmp167_163 = tmp163_159;
              tmp167_163[3] = 3;
              tmp167_163;
              CharSequence[] arrayOfCharSequence = new CharSequence[4];
              if (!localTL_messages_stickerSet.set.disabled) {}
              for (paramAnonymousView = LocaleController.getString("StickersHide", 2131166366);; paramAnonymousView = LocaleController.getString("StickersShow", 2131166371))
              {
                arrayOfCharSequence[0] = paramAnonymousView;
                arrayOfCharSequence[1] = LocaleController.getString("StickersRemove", 2131166368);
                arrayOfCharSequence[2] = LocaleController.getString("StickersShare", 2131166370);
                arrayOfCharSequence[3] = LocaleController.getString("StickersCopy", 2131166365);
                paramAnonymousView = arrayOfCharSequence;
                break;
              }
            }
          });
        }
      }
      paramViewGroup = new TextInfoPrivacyCell(this.mContext);
      String str = LocaleController.getString("StickersInfo", 2131166367);
      paramInt = str.indexOf("@stickers");
      if (paramInt != -1) {}
      for (;;)
      {
        try
        {
          SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(str);
          localSpannableStringBuilder.setSpan(new URLSpanNoUnderline("@stickers")
          {
            public void onClick(View paramAnonymousView)
            {
              MessagesController.openByUserName("stickers", StickersActivity.this, 1);
            }
          }, paramInt, "@stickers".length() + paramInt, 18);
          ((TextInfoPrivacyCell)paramViewGroup).setText(localSpannableStringBuilder);
          paramViewGroup.setBackgroundResource(2130837800);
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
          ((TextInfoPrivacyCell)paramViewGroup).setText(str);
          continue;
        }
        ((TextInfoPrivacyCell)paramViewGroup).setText(str);
      }
    }
    
    public void swapElements(int paramInt1, int paramInt2)
    {
      if (paramInt1 != paramInt2) {
        StickersActivity.access$802(StickersActivity.this, true);
      }
      ArrayList localArrayList = StickersQuery.getStickerSets();
      TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)localArrayList.get(paramInt1);
      localArrayList.set(paramInt1, localArrayList.get(paramInt2));
      localArrayList.set(paramInt2, localTL_messages_stickerSet);
      notifyItemMoved(paramInt1, paramInt2);
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
  
  public class TouchHelperCallback
    extends ItemTouchHelper.Callback
  {
    public static final float ALPHA_FULL = 1.0F;
    
    public TouchHelperCallback() {}
    
    public void clearView(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      super.clearView(paramRecyclerView, paramViewHolder);
      paramViewHolder.itemView.setPressed(false);
    }
    
    public int getMovementFlags(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      if (paramViewHolder.getItemViewType() != 0) {
        return makeMovementFlags(0, 0);
      }
      return makeMovementFlags(3, 0);
    }
    
    public boolean isLongPressDragEnabled()
    {
      return true;
    }
    
    public void onChildDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      super.onChildDraw(paramCanvas, paramRecyclerView, paramViewHolder, paramFloat1, paramFloat2, paramInt, paramBoolean);
    }
    
    public boolean onMove(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2)
    {
      if (paramViewHolder1.getItemViewType() != paramViewHolder2.getItemViewType()) {
        return false;
      }
      StickersActivity.this.listAdapter.swapElements(paramViewHolder1.getAdapterPosition(), paramViewHolder2.getAdapterPosition());
      return true;
    }
    
    public void onSelectedChanged(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      if (paramInt != 0)
      {
        StickersActivity.this.listView.cancelClickRunnables(false);
        paramViewHolder.itemView.setPressed(true);
      }
      super.onSelectedChanged(paramViewHolder, paramInt);
    }
    
    public void onSwiped(RecyclerView.ViewHolder paramViewHolder, int paramInt) {}
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\StickersActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */