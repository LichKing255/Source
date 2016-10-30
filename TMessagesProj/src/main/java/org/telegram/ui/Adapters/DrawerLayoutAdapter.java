package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.EmptyCell;

public class DrawerLayoutAdapter
  extends BaseAdapter
{
  private Context mContext;
  
  public DrawerLayoutAdapter(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public boolean areAllItemsEnabled()
  {
    return false;
  }
  
  public int getCount()
  {
    if (UserConfig.isClientActivated()) {
      return 16;
    }
    return 0;
  }
  
  public Object getItem(int paramInt)
  {
    return null;
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public int getItemViewType(int paramInt)
  {
    int i = 1;
    if (paramInt == 0) {
      i = 0;
    }
    while (paramInt == 1) {
      return i;
    }
    if (paramInt == 5) {
      return 2;
    }
    return 3;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    int i = getItemViewType(paramInt);
    if (i == 0)
    {
      paramViewGroup = paramView;
      if (paramView == null) {
        paramViewGroup = new DrawerProfileCell(this.mContext);
      }
      ((DrawerProfileCell)paramViewGroup).setUser(MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())));
    }
    Object localObject;
    do
    {
      do
      {
        do
        {
          do
          {
            return paramViewGroup;
            if (i != 1) {
              break;
            }
            paramViewGroup = paramView;
          } while (paramView != null);
          return new EmptyCell(this.mContext, AndroidUtilities.dp(8.0F));
          if (i != 2) {
            break;
          }
          paramViewGroup = paramView;
        } while (paramView != null);
        paramView = new DividerCell(this.mContext);
        paramView.setTag("theme_drawer_divider_color");
        return paramView;
        paramViewGroup = paramView;
      } while (i != 3);
      localObject = paramView;
      if (paramView == null) {
        localObject = new DrawerActionCell(this.mContext);
      }
      paramView = (DrawerActionCell)localObject;
      if (paramInt == 2)
      {
        paramView.setTextAndIcon(LocaleController.getString("NewGroup", 2131165989), 2130837972);
        return (View)localObject;
      }
      if (paramInt == 3)
      {
        paramView.setTextAndIcon(LocaleController.getString("NewSecretChat", 2131165997), 2130837977);
        return (View)localObject;
      }
      if (paramInt == 4)
      {
        paramView.setTextAndIcon(LocaleController.getString("NewChannel", 2131165988), 2130837960);
        return (View)localObject;
      }
      if (paramInt == 6)
      {
        paramView.setTextAndIcon(LocaleController.getString("Contacts", 2131165563), 2130837962);
        return (View)localObject;
      }
      if (paramInt == 7)
      {
        paramView.setTextAndIcon(LocaleController.getString("OnlineContacs", 2131166114), 2130837974);
        return (View)localObject;
      }
      if (paramInt == 8)
      {
        paramView.setTextAndIcon(LocaleController.getString("SpecialContacts", 2131166674), 2130837975);
        return (View)localObject;
      }
      if (paramInt == 9)
      {
        paramView.setTextAndIcon(LocaleController.getString("UserChanges", 2131166819), 2130837981);
        return (View)localObject;
      }
      if (paramInt == 10)
      {
        paramView.setTextAndIcon(LocaleController.getString("IdFinder", 2131165808), 2130837968);
        return (View)localObject;
      }
      if (paramInt == 11)
      {
        paramView.setTextAndIcon(LocaleController.getString("DownloadManager", 2131166629), 2130837964);
        return (View)localObject;
      }
      if (paramInt == 12)
      {
        paramView.setTextAndIcon(LocaleController.getString("InviteFriends", 2131165827), 2130837970);
        return (View)localObject;
      }
      if (paramInt == 13)
      {
        paramView.setTextAndIcon(LocaleController.getString("Settings", 2131166317), 2130837979);
        return (View)localObject;
      }
      if (paramInt == 14)
      {
        paramView.setTextAndIcon(LocaleController.getString("MihanSettings", 2131166412), 2130837979);
        return (View)localObject;
      }
      paramViewGroup = (ViewGroup)localObject;
    } while (paramInt != 15);
    paramView.setTextAndIcon(LocaleController.getString("TelegramFaq", 2131166387), 2130837966);
    return (View)localObject;
  }
  
  public int getViewTypeCount()
  {
    return 4;
  }
  
  public boolean hasStableIds()
  {
    return true;
  }
  
  public boolean isEmpty()
  {
    return !UserConfig.isClientActivated();
  }
  
  public boolean isEnabled(int paramInt)
  {
    return (paramInt != 0) && (paramInt != 1) && (paramInt != 5);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Adapters\DrawerLayoutAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */