package org.telegram.ui.Mihangram.SpecialContacts;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.ui.Adapters.BaseFragmentAdapter;

public class SpecialContactAdapter
  extends BaseFragmentAdapter
{
  private Context mContext;
  ArrayList<Integer> specialContactsArray = new ArrayList();
  
  public SpecialContactAdapter(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private void getSpecialContactsArray()
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(ContactsController.getInstance().contacts);
    int i = 0;
    while (i < localArrayList.size())
    {
      int j = ((TLRPC.TL_contact)localArrayList.get(i)).user_id;
      if (localSharedPreferences.contains("specific_c" + j)) {
        this.specialContactsArray.add(Integer.valueOf(j));
      }
      i += 1;
    }
  }
  
  public int getCount()
  {
    return this.specialContactsArray.size();
  }
  
  public Object getItem(int paramInt)
  {
    return MessagesController.getInstance().getUser((Integer)this.specialContactsArray.get(paramInt));
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    paramViewGroup = paramView;
    if (paramView == null) {
      paramViewGroup = new SpecialContactCell(this.mContext, 10);
    }
    paramView = MessagesController.getInstance().getUser((Integer)this.specialContactsArray.get(paramInt));
    ((SpecialContactCell)paramViewGroup).setData(paramView);
    return paramViewGroup;
  }
  
  public void notifyDataSetChanged()
  {
    this.specialContactsArray.clear();
    getSpecialContactsArray();
    super.notifyDataSetChanged();
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\SpecialContacts\SpecialContactAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */