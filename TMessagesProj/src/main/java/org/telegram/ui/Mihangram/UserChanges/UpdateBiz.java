package org.telegram.ui.Mihangram.UserChanges;

import android.annotation.SuppressLint;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.TL_updateUserName;
import org.telegram.tgnet.TLRPC.TL_updateUserPhone;
import org.telegram.tgnet.TLRPC.TL_updateUserPhoto;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.User;

public class UpdateBiz
{
  private a dba = new a();
  
  @SuppressLint({"DefaultLocale"})
  private String formatUserSearchName(String paramString1, String paramString2, String paramString3)
  {
    StringBuilder localStringBuilder = new StringBuilder("");
    if ((paramString2 != null) && (paramString2.length() > 0)) {
      localStringBuilder.append(paramString2);
    }
    if ((paramString3 != null) && (paramString3.length() > 0))
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(" ");
      }
      localStringBuilder.append(paramString3);
    }
    if ((paramString1 != null) && (paramString1.length() > 0))
    {
      localStringBuilder.append(";;;");
      localStringBuilder.append(paramString1);
    }
    return localStringBuilder.toString().toLowerCase();
  }
  
  public boolean insertUpdate(TLRPC.User paramUser, TLRPC.Update paramUpdate)
  {
    boolean bool = false;
    if (paramUpdate.user_id != UserConfig.getClientUserId())
    {
      if (paramUser == null) {
        bool = false;
      }
    }
    else {
      return bool;
    }
    UpdateModel localUpdateModel = new UpdateModel();
    localUpdateModel.setUserId(paramUser.id);
    localUpdateModel.setNew(true);
    if ((paramUpdate instanceof TLRPC.TL_updateUserName))
    {
      localUpdateModel.setOldValue(formatUserSearchName(paramUser.username, paramUser.first_name, paramUser.last_name));
      localUpdateModel.setNewValue(formatUserSearchName(paramUpdate.username, paramUpdate.first_name, paramUpdate.last_name));
      localUpdateModel.setType(2);
    }
    for (;;)
    {
      this.dba.a(localUpdateModel);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
      bool = true;
      break;
      if ((paramUpdate instanceof TLRPC.TL_updateUserPhone))
      {
        localUpdateModel.setOldValue(paramUser.phone);
        localUpdateModel.setNewValue(paramUpdate.phone);
        localUpdateModel.setType(4);
      }
      else
      {
        if (!(paramUpdate instanceof TLRPC.TL_updateUserPhoto)) {
          return false;
        }
        localUpdateModel.setType(3);
      }
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\UserChanges\UpdateBiz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */