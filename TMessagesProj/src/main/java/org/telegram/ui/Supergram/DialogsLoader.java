package org.telegram.ui.Supergram;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.User;

public class DialogsLoader
{
  ArrayList<TLRPC.TL_dialog> allDialogs = new ArrayList();
  boolean chatUnlocked = this.preferences.getBoolean("chat_unlocked", false);
  int defaulTab = this.preferences.getInt("defaul_tab", 5);
  ArrayList<TLRPC.TL_dialog> dialogs = new ArrayList();
  boolean isTabsEnabled = this.preferences.getBoolean("tabs", true);
  SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
  int selectedTab = this.preferences.getInt("selected_tab", this.defaulTab);
  
  public ArrayList<TLRPC.TL_dialog> getDialogsArray()
  {
    this.allDialogs.addAll(MessagesController.getInstance().dialogs);
    int i;
    Object localObject;
    int k;
    int m;
    int j;
    label554:
    TLRPC.User localUser;
    label616:
    label621:
    label760:
    TLRPC.TL_dialog localTL_dialog;
    if (this.chatUnlocked)
    {
      if (!this.isTabsEnabled)
      {
        i = 0;
        while (i < this.allDialogs.size())
        {
          localObject = (TLRPC.TL_dialog)this.allDialogs.get(i);
          if (this.preferences.contains("hide_" + String.valueOf(((TLRPC.TL_dialog)localObject).id))) {
            this.dialogs.add(localObject);
          }
          i += 1;
        }
        return this.dialogs;
      }
      if (this.selectedTab == 7)
      {
        i = 0;
        while (i < this.allDialogs.size())
        {
          localObject = (TLRPC.TL_dialog)this.allDialogs.get(i);
          if ((((TLRPC.TL_dialog)localObject).unread_count > 0) && (this.preferences.contains("hide_" + String.valueOf(((TLRPC.TL_dialog)localObject).id)))) {
            this.dialogs.add(localObject);
          }
          i += 1;
        }
        return this.dialogs;
      }
      if (this.selectedTab == 6)
      {
        i = 0;
        while (i < this.allDialogs.size())
        {
          localObject = (TLRPC.TL_dialog)this.allDialogs.get(i);
          if (this.preferences.contains("hide_" + String.valueOf(((TLRPC.TL_dialog)localObject).id))) {
            this.dialogs.add(localObject);
          }
          i += 1;
        }
        return this.dialogs;
      }
      if (this.selectedTab == 5)
      {
        i = 0;
        while (i < this.allDialogs.size())
        {
          localObject = (TLRPC.TL_dialog)this.allDialogs.get(i);
          if ((this.preferences.contains("hide_" + String.valueOf(((TLRPC.TL_dialog)localObject).id))) && (this.preferences.contains("fav_" + String.valueOf(((TLRPC.TL_dialog)localObject).id)))) {
            this.dialogs.add(localObject);
          }
          i += 1;
        }
        return this.dialogs;
      }
      if (this.selectedTab == 4)
      {
        i = 0;
        if (i < this.allDialogs.size())
        {
          localObject = (TLRPC.TL_dialog)this.allDialogs.get(i);
          if (this.preferences.contains("hide_" + String.valueOf(((TLRPC.TL_dialog)localObject).id)))
          {
            k = (int)((TLRPC.TL_dialog)localObject).id;
            m = (int)(((TLRPC.TL_dialog)localObject).id >> 32);
            if (!DialogObject.isChannel((TLRPC.TL_dialog)localObject))
            {
              if ((k >= 0) || (m == 1)) {
                break label616;
              }
              j = 1;
              if ((j == 0) && (k > 0) && (m != 1))
              {
                localUser = MessagesController.getInstance().getUser(Integer.valueOf(k));
                if ((localUser == null) || (!localUser.bot)) {
                  break label621;
                }
              }
            }
          }
          for (j = 1;; j = 0)
          {
            if (j == 0) {
              this.dialogs.add(localObject);
            }
            i += 1;
            break;
            j = 0;
            break label554;
          }
        }
        return this.dialogs;
      }
      if (this.selectedTab == 3)
      {
        i = 0;
        if (i < this.allDialogs.size())
        {
          localObject = (TLRPC.TL_dialog)this.allDialogs.get(i);
          if (this.preferences.contains("hide_" + String.valueOf(((TLRPC.TL_dialog)localObject).id)))
          {
            j = (int)((TLRPC.TL_dialog)localObject).id;
            k = (int)(((TLRPC.TL_dialog)localObject).id >> 32);
            if ((j >= 0) || (k == 1)) {
              break label760;
            }
          }
          for (j = 1;; j = 0)
          {
            if ((!DialogObject.isChannel((TLRPC.TL_dialog)localObject)) && (j != 0)) {
              this.dialogs.add(localObject);
            }
            i += 1;
            break;
          }
        }
        return this.dialogs;
      }
      if (this.selectedTab == 2)
      {
        i = 0;
        while (i < this.allDialogs.size())
        {
          localObject = (TLRPC.TL_dialog)this.allDialogs.get(i);
          if (this.preferences.contains("hide_" + String.valueOf(((TLRPC.TL_dialog)localObject).id)))
          {
            j = (int)((TLRPC.TL_dialog)localObject).id;
            if ((DialogObject.isChannel((TLRPC.TL_dialog)localObject)) && (MessagesController.getInstance().getChat(Integer.valueOf(-j)).megagroup)) {
              this.dialogs.add(localObject);
            }
          }
          i += 1;
        }
        return this.dialogs;
      }
      if (this.selectedTab == 1)
      {
        i = 0;
        while (i < this.allDialogs.size())
        {
          localObject = (TLRPC.TL_dialog)this.allDialogs.get(i);
          if (this.preferences.contains("hide_" + String.valueOf(((TLRPC.TL_dialog)localObject).id)))
          {
            j = (int)((TLRPC.TL_dialog)localObject).id;
            if ((DialogObject.isChannel((TLRPC.TL_dialog)localObject)) && (!MessagesController.getInstance().getChat(Integer.valueOf(-j)).megagroup)) {
              this.dialogs.add(localObject);
            }
          }
          i += 1;
        }
        return this.dialogs;
      }
      if (this.selectedTab == 0)
      {
        i = 0;
        if (i < this.allDialogs.size())
        {
          localTL_dialog = (TLRPC.TL_dialog)this.allDialogs.get(i);
          if (this.preferences.contains("hide_" + String.valueOf(localTL_dialog.id)))
          {
            k = (int)localTL_dialog.id;
            m = (int)(localTL_dialog.id >> 32);
            if (!DialogObject.isChannel(localTL_dialog))
            {
              if ((k >= 0) || (m == 1)) {
                break label1208;
              }
              j = 1;
              label1131:
              localUser = null;
              localObject = localUser;
              if (j == 0)
              {
                localObject = localUser;
                if (k > 0)
                {
                  localObject = localUser;
                  if (m != 1) {
                    localObject = MessagesController.getInstance().getUser(Integer.valueOf(k));
                  }
                }
              }
              if ((localObject == null) || (!((TLRPC.User)localObject).bot)) {
                break label1213;
              }
            }
          }
          label1208:
          label1213:
          for (j = 1;; j = 0)
          {
            if (j != 0) {
              this.dialogs.add(localTL_dialog);
            }
            i += 1;
            break;
            j = 0;
            break label1131;
          }
        }
        return this.dialogs;
      }
    }
    else
    {
      if (!this.isTabsEnabled)
      {
        i = 0;
        while (i < this.allDialogs.size())
        {
          localObject = (TLRPC.TL_dialog)this.allDialogs.get(i);
          if (!this.preferences.contains("hide_" + String.valueOf(((TLRPC.TL_dialog)localObject).id))) {
            this.dialogs.add(localObject);
          }
          i += 1;
        }
        return this.dialogs;
      }
      if (this.selectedTab == 7)
      {
        i = 0;
        while (i < this.allDialogs.size())
        {
          localObject = (TLRPC.TL_dialog)this.allDialogs.get(i);
          if ((!this.preferences.contains("hide_" + String.valueOf(((TLRPC.TL_dialog)localObject).id))) && (((TLRPC.TL_dialog)localObject).unread_count > 0)) {
            this.dialogs.add(localObject);
          }
          i += 1;
        }
        return this.dialogs;
      }
      if (this.selectedTab == 6)
      {
        i = 0;
        while (i < this.allDialogs.size())
        {
          localObject = (TLRPC.TL_dialog)this.allDialogs.get(i);
          if (!this.preferences.contains("hide_" + String.valueOf(((TLRPC.TL_dialog)localObject).id))) {
            this.dialogs.add(localObject);
          }
          i += 1;
        }
        return this.dialogs;
      }
      if (this.selectedTab == 5)
      {
        i = 0;
        while (i < this.allDialogs.size())
        {
          localObject = (TLRPC.TL_dialog)this.allDialogs.get(i);
          if ((!this.preferences.contains("hide_" + String.valueOf(((TLRPC.TL_dialog)localObject).id))) && (this.preferences.contains("fav_" + String.valueOf(((TLRPC.TL_dialog)localObject).id)))) {
            this.dialogs.add(localObject);
          }
          i += 1;
        }
        return this.dialogs;
      }
      if (this.selectedTab == 4)
      {
        i = 0;
        if (i < this.allDialogs.size())
        {
          localObject = (TLRPC.TL_dialog)this.allDialogs.get(i);
          if (!this.preferences.contains("hide_" + String.valueOf(((TLRPC.TL_dialog)localObject).id)))
          {
            k = (int)((TLRPC.TL_dialog)localObject).id;
            m = (int)(((TLRPC.TL_dialog)localObject).id >> 32);
            if (!DialogObject.isChannel((TLRPC.TL_dialog)localObject))
            {
              if ((k >= 0) || (m == 1)) {
                break label1818;
              }
              j = 1;
              label1756:
              if ((j == 0) && (k > 0) && (m != 1))
              {
                localUser = MessagesController.getInstance().getUser(Integer.valueOf(k));
                if ((localUser == null) || (!localUser.bot)) {
                  break label1823;
                }
              }
            }
          }
          label1818:
          label1823:
          for (j = 1;; j = 0)
          {
            if (j == 0) {
              this.dialogs.add(localObject);
            }
            i += 1;
            break;
            j = 0;
            break label1756;
          }
        }
        return this.dialogs;
      }
      if (this.selectedTab == 3)
      {
        i = 0;
        if (i < this.allDialogs.size())
        {
          localObject = (TLRPC.TL_dialog)this.allDialogs.get(i);
          if (!this.preferences.contains("hide_" + String.valueOf(((TLRPC.TL_dialog)localObject).id)))
          {
            j = (int)((TLRPC.TL_dialog)localObject).id;
            k = (int)(((TLRPC.TL_dialog)localObject).id >> 32);
            if ((j >= 0) || (k == 1)) {
              break label1962;
            }
          }
          label1962:
          for (j = 1;; j = 0)
          {
            if ((!DialogObject.isChannel((TLRPC.TL_dialog)localObject)) && (j != 0)) {
              this.dialogs.add(localObject);
            }
            i += 1;
            break;
          }
        }
        return this.dialogs;
      }
      if (this.selectedTab == 2)
      {
        i = 0;
        while (i < this.allDialogs.size())
        {
          localObject = (TLRPC.TL_dialog)this.allDialogs.get(i);
          if (!this.preferences.contains("hide_" + String.valueOf(((TLRPC.TL_dialog)localObject).id)))
          {
            j = (int)((TLRPC.TL_dialog)localObject).id;
            if ((DialogObject.isChannel((TLRPC.TL_dialog)localObject)) && (MessagesController.getInstance().getChat(Integer.valueOf(-j)).megagroup)) {
              this.dialogs.add(localObject);
            }
          }
          i += 1;
        }
        return this.dialogs;
      }
      if (this.selectedTab == 1)
      {
        i = 0;
        while (i < this.allDialogs.size())
        {
          localObject = (TLRPC.TL_dialog)this.allDialogs.get(i);
          if (!this.preferences.contains("hide_" + String.valueOf(((TLRPC.TL_dialog)localObject).id)))
          {
            j = (int)((TLRPC.TL_dialog)localObject).id;
            if ((DialogObject.isChannel((TLRPC.TL_dialog)localObject)) && (!MessagesController.getInstance().getChat(Integer.valueOf(-j)).megagroup)) {
              this.dialogs.add(localObject);
            }
          }
          i += 1;
        }
        return this.dialogs;
      }
      if (this.selectedTab == 0)
      {
        i = 0;
        if (i < this.allDialogs.size())
        {
          localTL_dialog = (TLRPC.TL_dialog)this.allDialogs.get(i);
          if (!this.preferences.contains("hide_" + String.valueOf(localTL_dialog.id)))
          {
            k = (int)localTL_dialog.id;
            m = (int)(localTL_dialog.id >> 32);
            if (!DialogObject.isChannel(localTL_dialog))
            {
              if ((k >= 0) || (m == 1)) {
                break label2410;
              }
              j = 1;
              label2333:
              localUser = null;
              localObject = localUser;
              if (j == 0)
              {
                localObject = localUser;
                if (k > 0)
                {
                  localObject = localUser;
                  if (m != 1) {
                    localObject = MessagesController.getInstance().getUser(Integer.valueOf(k));
                  }
                }
              }
              if ((localObject == null) || (!((TLRPC.User)localObject).bot)) {
                break label2415;
              }
            }
          }
          label2410:
          label2415:
          for (j = 1;; j = 0)
          {
            if (j != 0) {
              this.dialogs.add(localTL_dialog);
            }
            i += 1;
            break;
            j = 0;
            break label2333;
          }
        }
        return this.dialogs;
      }
    }
    return null;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\DialogsLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */