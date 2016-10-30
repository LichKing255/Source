package org.telegram.ui.Mihangram.AddUserToChat;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

public class AddUserActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private AddUserAdapter dialogsAdapter;
  private LinearLayout emptyView;
  private LinearLayoutManager layoutManager;
  private RecyclerListView listView;
  private ProgressBar progressView;
  private int userId;
  
  public AddUserActivity(Bundle paramBundle)
  {
    super(paramBundle);
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
  
  private void updateVisibleRows(int paramInt)
  {
    if (this.listView == null) {
      return;
    }
    int k = this.listView.getChildCount();
    int i = 0;
    label19:
    Object localObject;
    if (i < k)
    {
      localObject = this.listView.getChildAt(i);
      if (!(localObject instanceof DialogCell)) {
        break label87;
      }
      localObject = (DialogCell)localObject;
      if ((paramInt & 0x800) == 0) {
        break label70;
      }
      ((DialogCell)localObject).checkCurrentDialogIndex();
    }
    for (;;)
    {
      i += 1;
      break label19;
      break;
      label70:
      if ((paramInt & 0x200) == 0)
      {
        ((DialogCell)localObject).update(paramInt);
        continue;
        label87:
        if ((localObject instanceof UserCell))
        {
          ((UserCell)localObject).update(paramInt);
        }
        else if ((localObject instanceof ProfileSearchCell))
        {
          ((ProfileSearchCell)localObject).update(paramInt);
        }
        else if ((localObject instanceof RecyclerListView))
        {
          localObject = (RecyclerListView)localObject;
          int m = ((RecyclerListView)localObject).getChildCount();
          int j = 0;
          while (j < m)
          {
            View localView = ((RecyclerListView)localObject).getChildAt(j);
            if ((localView instanceof HintDialogCell)) {
              ((HintDialogCell)localView).checkUnreadCounter(paramInt);
            }
            j += 1;
          }
        }
      }
    }
  }
  
  public View createView(final Context paramContext)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        Theme.loadRecources(paramContext);
      }
    });
    this.actionBar.setTitle(LocaleController.getString("SelectChat", 2131166279));
    this.actionBar.setBackButtonImage(2130837810);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          AddUserActivity.this.finishFragment();
        }
        while (paramAnonymousInt != 1) {
          return;
        }
      }
    });
    FrameLayout localFrameLayout = new FrameLayout(paramContext);
    this.fragmentView = localFrameLayout;
    this.listView = new RecyclerListView(paramContext);
    this.listView.setVerticalScrollBarEnabled(true);
    this.listView.setItemAnimator(null);
    this.listView.setInstantClick(true);
    this.listView.setLayoutAnimation(null);
    this.listView.setTag(Integer.valueOf(4));
    this.layoutManager = new LinearLayoutManager(paramContext)
    {
      public boolean supportsPredictiveItemAnimations()
      {
        return false;
      }
    };
    this.layoutManager.setOrientation(1);
    this.listView.setLayoutManager(this.layoutManager);
    Object localObject = this.listView;
    int i;
    if (LocaleController.isRTL)
    {
      i = 1;
      ((RecyclerListView)localObject).setVerticalScrollbarPosition(i);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if ((AddUserActivity.this.listView == null) || (AddUserActivity.this.listView.getAdapter() == null)) {}
          do
          {
            do
            {
              return;
            } while (AddUserActivity.this.listView.getAdapter() != AddUserActivity.this.dialogsAdapter);
            paramAnonymousView = AddUserActivity.this.dialogsAdapter.getItem(paramAnonymousInt);
          } while (paramAnonymousView == null);
          TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(AddUserActivity.this.userId));
          MessagesController.getInstance().addUserToChat(-(int)paramAnonymousView.id, localUser, null, 0, null, null);
          paramAnonymousView = Toast.makeText(AddUserActivity.this.getParentActivity(), LocaleController.getString("Done", 2131165634), 1);
          ((TextView)((LinearLayout)paramAnonymousView.getView()).getChildAt(0)).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          paramAnonymousView.show();
          AddUserActivity.this.finishFragment();
        }
      });
      this.emptyView = new LinearLayout(paramContext);
      this.emptyView.setOrientation(1);
      this.emptyView.setVisibility(8);
      this.emptyView.setGravity(17);
      localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
      this.emptyView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      localObject = new TextView(paramContext);
      ((TextView)localObject).setText(LocaleController.getString("NoChats", 2131166002));
      ((TextView)localObject).setTextColor(-6974059);
      ((TextView)localObject).setGravity(17);
      ((TextView)localObject).setTextSize(1, 20.0F);
      ((TextView)localObject).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.emptyView.addView((View)localObject, LayoutHelper.createLinear(-2, -2));
      TextView localTextView = new TextView(paramContext);
      String str = LocaleController.getString("NoChatsHelp", 2131166003);
      localObject = str;
      if (AndroidUtilities.isTablet())
      {
        localObject = str;
        if (!AndroidUtilities.isSmallTablet()) {
          localObject = str.replace('\n', ' ');
        }
      }
      localTextView.setText((CharSequence)localObject);
      localTextView.setTextColor(-6974059);
      localTextView.setTextSize(1, 15.0F);
      localTextView.setGravity(17);
      localTextView.setPadding(AndroidUtilities.dp(8.0F), AndroidUtilities.dp(6.0F), AndroidUtilities.dp(8.0F), 0);
      localTextView.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
      localTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.emptyView.addView(localTextView, LayoutHelper.createLinear(-2, -2));
      this.progressView = new ProgressBar(paramContext);
      this.progressView.setVisibility(8);
      localFrameLayout.addView(this.progressView, LayoutHelper.createFrame(-2, -2, 17));
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt) {}
        
        public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2) {}
      });
      this.dialogsAdapter = new AddUserAdapter(paramContext);
      this.listView.setAdapter(this.dialogsAdapter);
      if ((!MessagesController.getInstance().loadingDialogs) || (!MessagesController.getInstance().dialogs.isEmpty())) {
        break label606;
      }
      this.emptyView.setVisibility(8);
      this.listView.setEmptyView(this.progressView);
    }
    for (;;)
    {
      return this.fragmentView;
      i = 2;
      break;
      label606:
      this.progressView.setVisibility(8);
      this.listView.setEmptyView(this.emptyView);
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.dialogsNeedReload) {
      if (this.dialogsAdapter != null)
      {
        if (this.dialogsAdapter.isDataSetChanged()) {
          this.dialogsAdapter.notifyDataSetChanged();
        }
      }
      else if (this.listView == null) {}
    }
    for (;;)
    {
      try
      {
        if ((MessagesController.getInstance().loadingDialogs) && (MessagesController.getInstance().dialogs.isEmpty()))
        {
          this.emptyView.setVisibility(8);
          this.listView.setEmptyView(this.progressView);
          if (paramInt == NotificationCenter.didLoadedReplyMessages) {
            updateVisibleRows(0);
          }
          return;
          updateVisibleRows(2048);
          break;
        }
        this.progressView.setVisibility(8);
        this.listView.setEmptyView(this.emptyView);
        continue;
      }
      catch (Exception paramVarArgs)
      {
        FileLog.e("tmessages", paramVarArgs);
        continue;
      }
      if (paramInt == NotificationCenter.emojiDidLoaded) {
        updateVisibleRows(0);
      } else if (paramInt == NotificationCenter.updateInterfaces) {
        updateVisibleRows(((Integer)paramVarArgs[0]).intValue());
      } else if (paramInt != NotificationCenter.appDidLogout) {
        if (paramInt == NotificationCenter.encryptedChatUpdated) {
          updateVisibleRows(0);
        } else if (paramInt == NotificationCenter.contactsDidLoaded) {
          updateVisibleRows(0);
        } else if (paramInt != NotificationCenter.openedChatChanged) {
          if (paramInt == NotificationCenter.notificationsSettingsUpdated) {
            updateVisibleRows(0);
          } else if ((paramInt == NotificationCenter.messageReceivedByAck) || (paramInt == NotificationCenter.messageReceivedByServer) || (paramInt == NotificationCenter.messageSendError)) {
            updateVisibleRows(4096);
          }
        }
      }
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
  }
  
  protected void onDialogDismiss(Dialog paramDialog)
  {
    super.onDialogDismiss(paramDialog);
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    if (getArguments() != null) {
      this.userId = this.arguments.getInt("userId", 0);
    }
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
  }
  
  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramInt == 1)
    {
      int i = 0;
      if (i < paramArrayOfString.length)
      {
        if ((paramArrayOfInt.length <= i) || (paramArrayOfInt[i] != 0)) {}
        for (;;)
        {
          i += 1;
          break;
          String str = paramArrayOfString[i];
          paramInt = -1;
          switch (str.hashCode())
          {
          }
          for (;;)
          {
            switch (paramInt)
            {
            default: 
              break;
            case 0: 
              ContactsController.getInstance().readContacts();
              break;
              if (str.equals("android.permission.READ_CONTACTS"))
              {
                paramInt = 0;
                continue;
                if (str.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                  paramInt = 1;
                }
              }
              break;
            }
          }
          ImageLoader.getInstance().checkMediaPaths();
        }
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.dialogsAdapter != null) {
      this.dialogsAdapter.notifyDataSetChanged();
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\AddUserToChat\AddUserActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */