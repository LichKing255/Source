package org.telegram.ui.Supergram.AddUserToChat.UserChanges;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.Supergram.SpecialContacts.SpecialContactCell;

public class OperationAdapter
  extends BaseAdapter
{
  private Context mContext;
  ArrayList<OperationModel> specialContactsModel = new ArrayList();
  
  public OperationAdapter(Context paramContext)
  {
    this.mContext = paramContext;
    getSpecialContactsArray();
  }
  
  private void getSpecialContactsArray()
  {
    SC_DBHelper localSC_DBHelper = new SC_DBHelper(ApplicationLoader.applicationContext);
    int i = 0;
    while (i < localSC_DBHelper.getAllSContacts().size()) {
      i += 1;
    }
  }
  
  public int getCount()
  {
    return this.specialContactsModel.size();
  }
  
  public Object getItem(int paramInt)
  {
    if (this.specialContactsModel != null) {
      return this.specialContactsModel.get(paramInt);
    }
    return null;
  }
  
  public long getItemId(int paramInt)
  {
    return 0L;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    paramViewGroup = paramView;
    if (paramView == null) {
      paramViewGroup = new SpecialContactCell(this.mContext, 10);
    }
    paramView = (OperationModel)this.specialContactsModel.get(paramInt);
    return paramViewGroup;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\AddUserToChat\UserChanges\OperationAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */