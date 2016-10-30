package org.telegram.ui.Mihangram;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.User;

public class DialogsLoaderShareAlert
{
  ArrayList<TLRPC.TL_dialog> allDialogs = new ArrayList();
  ArrayList<TLRPC.TL_dialog> dialogs = new ArrayList();
  SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
  int selectedTab = this.preferences.getInt("share_alert_selected_tab", 6);
  boolean showInList = this.preferences.getBoolean("hidden_sharealert", true);
  
  public ArrayList<TLRPC.TL_dialog> shareAlertGetDialogsArray()
  {
    this.allDialogs.addAll(MessagesController.getInstance().dialogsServerOnly);
    int i;
    TLRPC.TL_dialog localTL_dialog;
    int j;
    int k;
    label105:
    Object localObject;
    if (this.showInList)
    {
      if (this.selectedTab == 6)
      {
        i = 0;
        if (i < this.allDialogs.size())
        {
          localTL_dialog = (TLRPC.TL_dialog)MessagesController.getInstance().dialogsServerOnly.get(i);
          j = (int)localTL_dialog.id;
          k = (int)(localTL_dialog.id >> 32);
          if ((j != 0) && (k != 1))
          {
            if (j <= 0) {
              break label105;
            }
            this.dialogs.add(localTL_dialog);
          }
          for (;;)
          {
            i += 1;
            break;
            localObject = MessagesController.getInstance().getChat(Integer.valueOf(-j));
            if ((localObject != null) && (!ChatObject.isNotInChat((TLRPC.Chat)localObject)) && ((!ChatObject.isChannel((TLRPC.Chat)localObject)) || (((TLRPC.Chat)localObject).creator) || (((TLRPC.Chat)localObject).editor) || (((TLRPC.Chat)localObject).megagroup))) {
              this.dialogs.add(localTL_dialog);
            }
          }
        }
        return this.dialogs;
      }
      if (this.selectedTab == 5)
      {
        i = 0;
        if (i < this.allDialogs.size())
        {
          localTL_dialog = (TLRPC.TL_dialog)MessagesController.getInstance().dialogsServerOnly.get(i);
          j = (int)localTL_dialog.id;
          k = (int)(localTL_dialog.id >> 32);
          if ((this.preferences.contains("fav_" + String.valueOf(localTL_dialog.id))) && (j != 0) && (k != 1))
          {
            if (j <= 0) {
              break label302;
            }
            this.dialogs.add(localTL_dialog);
          }
          for (;;)
          {
            i += 1;
            break;
            label302:
            localObject = MessagesController.getInstance().getChat(Integer.valueOf(-j));
            if ((localObject != null) && (!ChatObject.isNotInChat((TLRPC.Chat)localObject)) && ((!ChatObject.isChannel((TLRPC.Chat)localObject)) || (((TLRPC.Chat)localObject).creator) || (((TLRPC.Chat)localObject).editor) || (((TLRPC.Chat)localObject).megagroup))) {
              this.dialogs.add(localTL_dialog);
            }
          }
        }
        return this.dialogs;
      }
      if (this.selectedTab == 4)
      {
        i = 0;
        if (i < this.allDialogs.size())
        {
          localTL_dialog = (TLRPC.TL_dialog)MessagesController.getInstance().dialogsServerOnly.get(i);
          j = (int)localTL_dialog.id;
          k = (int)(localTL_dialog.id >> 32);
          if ((j != 0) && (k != 1) && (j > 0))
          {
            localObject = MessagesController.getInstance().getUser(Integer.valueOf(j));
            if ((localObject == null) || (!((TLRPC.User)localObject).bot)) {
              break label492;
            }
          }
          label492:
          for (j = 1;; j = 0)
          {
            if (j == 0) {
              this.dialogs.add(localTL_dialog);
            }
            i += 1;
            break;
          }
        }
        return this.dialogs;
      }
      if (this.selectedTab == 3)
      {
        i = 0;
        while (i < this.allDialogs.size())
        {
          localTL_dialog = (TLRPC.TL_dialog)MessagesController.getInstance().dialogsServerOnly.get(i);
          j = (int)localTL_dialog.id;
          k = (int)(localTL_dialog.id >> 32);
          if ((j != 0) && (k != 1))
          {
            localObject = MessagesController.getInstance().getChat(Integer.valueOf(-j));
            if ((localObject != null) && (!ChatObject.isNotInChat((TLRPC.Chat)localObject)) && ((!ChatObject.isChannel((TLRPC.Chat)localObject)) || (((TLRPC.Chat)localObject).creator) || (((TLRPC.Chat)localObject).editor) || (((TLRPC.Chat)localObject).megagroup)) && (!DialogObject.isChannel(localTL_dialog))) {
              this.dialogs.add(localTL_dialog);
            }
          }
          i += 1;
        }
        return this.dialogs;
      }
      if (this.selectedTab == 2)
      {
        i = 0;
        while (i < this.allDialogs.size())
        {
          localTL_dialog = (TLRPC.TL_dialog)MessagesController.getInstance().dialogsServerOnly.get(i);
          j = (int)localTL_dialog.id;
          k = (int)(localTL_dialog.id >> 32);
          if ((j != 0) && (k != 1))
          {
            localObject = MessagesController.getInstance().getChat(Integer.valueOf(-j));
            if ((localObject != null) && (!ChatObject.isNotInChat((TLRPC.Chat)localObject)) && ((!ChatObject.isChannel((TLRPC.Chat)localObject)) || (((TLRPC.Chat)localObject).creator) || (((TLRPC.Chat)localObject).editor) || (((TLRPC.Chat)localObject).megagroup)) && (((TLRPC.Chat)localObject).megagroup)) {
              this.dialogs.add(localTL_dialog);
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
          localTL_dialog = (TLRPC.TL_dialog)MessagesController.getInstance().dialogsServerOnly.get(i);
          j = (int)localTL_dialog.id;
          k = (int)(localTL_dialog.id >> 32);
          if ((j != 0) && (k != 1))
          {
            localObject = MessagesController.getInstance().getChat(Integer.valueOf(-j));
            if ((localObject != null) && (!ChatObject.isNotInChat((TLRPC.Chat)localObject)) && ((!ChatObject.isChannel((TLRPC.Chat)localObject)) || (((TLRPC.Chat)localObject).creator) || (((TLRPC.Chat)localObject).editor) || (((TLRPC.Chat)localObject).megagroup)) && (!((TLRPC.Chat)localObject).megagroup)) {
              this.dialogs.add(localTL_dialog);
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
          localTL_dialog = (TLRPC.TL_dialog)MessagesController.getInstance().dialogsServerOnly.get(i);
          j = (int)localTL_dialog.id;
          k = (int)(localTL_dialog.id >> 32);
          if ((j != 0) && (k != 1) && (j > 0))
          {
            localObject = MessagesController.getInstance().getUser(Integer.valueOf(j));
            if ((localObject == null) || (!((TLRPC.User)localObject).bot)) {
              break label1065;
            }
          }
          label1065:
          for (j = 1;; j = 0)
          {
            if (j != 0) {
              this.dialogs.add(localTL_dialog);
            }
            i += 1;
            break;
          }
        }
        return this.dialogs;
      }
    }
    else
    {
      if (this.selectedTab == 6)
      {
        i = 0;
        if (i < this.allDialogs.size())
        {
          localTL_dialog = (TLRPC.TL_dialog)MessagesController.getInstance().dialogsServerOnly.get(i);
          if (!this.preferences.contains("hide_" + String.valueOf(localTL_dialog.id)))
          {
            j = (int)localTL_dialog.id;
            k = (int)(localTL_dialog.id >> 32);
            if ((j != 0) && (k != 1))
            {
              if (j <= 0) {
                break label1197;
              }
              this.dialogs.add(localTL_dialog);
            }
          }
          for (;;)
          {
            i += 1;
            break;
            label1197:
            localObject = MessagesController.getInstance().getChat(Integer.valueOf(-j));
            if ((localObject != null) && (!ChatObject.isNotInChat((TLRPC.Chat)localObject)) && ((!ChatObject.isChannel((TLRPC.Chat)localObject)) || (((TLRPC.Chat)localObject).creator) || (((TLRPC.Chat)localObject).editor) || (((TLRPC.Chat)localObject).megagroup))) {
              this.dialogs.add(localTL_dialog);
            }
          }
        }
        return this.dialogs;
      }
      if (this.selectedTab == 5)
      {
        i = 0;
        if (i < this.allDialogs.size())
        {
          localTL_dialog = (TLRPC.TL_dialog)MessagesController.getInstance().dialogsServerOnly.get(i);
          if (!this.preferences.contains("hide_" + String.valueOf(localTL_dialog.id)))
          {
            j = (int)localTL_dialog.id;
            k = (int)(localTL_dialog.id >> 32);
            if ((this.preferences.contains("fav_" + String.valueOf(localTL_dialog.id))) && (j != 0) && (k != 1))
            {
              if (j <= 0) {
                break label1432;
              }
              this.dialogs.add(localTL_dialog);
            }
          }
          for (;;)
          {
            i += 1;
            break;
            label1432:
            localObject = MessagesController.getInstance().getChat(Integer.valueOf(-j));
            if ((localObject != null) && (!ChatObject.isNotInChat((TLRPC.Chat)localObject)) && ((!ChatObject.isChannel((TLRPC.Chat)localObject)) || (((TLRPC.Chat)localObject).creator) || (((TLRPC.Chat)localObject).editor) || (((TLRPC.Chat)localObject).megagroup))) {
              this.dialogs.add(localTL_dialog);
            }
          }
        }
        return this.dialogs;
      }
      if (this.selectedTab == 4)
      {
        i = 0;
        if (i < this.allDialogs.size())
        {
          localTL_dialog = (TLRPC.TL_dialog)MessagesController.getInstance().dialogsServerOnly.get(i);
          if (!this.preferences.contains("hide_" + String.valueOf(localTL_dialog.id)))
          {
            j = (int)localTL_dialog.id;
            k = (int)(localTL_dialog.id >> 32);
            if ((j != 0) && (k != 1) && (j > 0))
            {
              localObject = MessagesController.getInstance().getUser(Integer.valueOf(j));
              if ((localObject == null) || (!((TLRPC.User)localObject).bot)) {
                break label1660;
              }
            }
          }
          label1660:
          for (j = 1;; j = 0)
          {
            if (j == 0) {
              this.dialogs.add(localTL_dialog);
            }
            i += 1;
            break;
          }
        }
        return this.dialogs;
      }
      if (this.selectedTab == 3)
      {
        i = 0;
        while (i < this.allDialogs.size())
        {
          localTL_dialog = (TLRPC.TL_dialog)MessagesController.getInstance().dialogsServerOnly.get(i);
          if (!this.preferences.contains("hide_" + String.valueOf(localTL_dialog.id)))
          {
            j = (int)localTL_dialog.id;
            k = (int)(localTL_dialog.id >> 32);
            if ((j != 0) && (k != 1))
            {
              localObject = MessagesController.getInstance().getChat(Integer.valueOf(-j));
              if ((localObject != null) && (!ChatObject.isNotInChat((TLRPC.Chat)localObject)) && ((!ChatObject.isChannel((TLRPC.Chat)localObject)) || (((TLRPC.Chat)localObject).creator) || (((TLRPC.Chat)localObject).editor) || (((TLRPC.Chat)localObject).megagroup)) && (!DialogObject.isChannel(localTL_dialog))) {
                this.dialogs.add(localTL_dialog);
              }
            }
          }
          i += 1;
        }
        return this.dialogs;
      }
      if (this.selectedTab == 2)
      {
        i = 0;
        while (i < this.allDialogs.size())
        {
          localTL_dialog = (TLRPC.TL_dialog)MessagesController.getInstance().dialogsServerOnly.get(i);
          if (!this.preferences.contains("hide_" + String.valueOf(localTL_dialog.id)))
          {
            j = (int)localTL_dialog.id;
            k = (int)(localTL_dialog.id >> 32);
            if ((j != 0) && (k != 1))
            {
              localObject = MessagesController.getInstance().getChat(Integer.valueOf(-j));
              if ((!ChatObject.isNotInChat((TLRPC.Chat)localObject)) && ((!ChatObject.isChannel((TLRPC.Chat)localObject)) || (((TLRPC.Chat)localObject).creator) || (((TLRPC.Chat)localObject).editor) || (((TLRPC.Chat)localObject).megagroup)) && (((TLRPC.Chat)localObject).megagroup)) {
                this.dialogs.add(localTL_dialog);
              }
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
          localTL_dialog = (TLRPC.TL_dialog)MessagesController.getInstance().dialogsServerOnly.get(i);
          if (!this.preferences.contains("hide_" + String.valueOf(localTL_dialog.id)))
          {
            j = (int)localTL_dialog.id;
            k = (int)(localTL_dialog.id >> 32);
            if ((j != 0) && (k != 1))
            {
              localObject = MessagesController.getInstance().getChat(Integer.valueOf(-j));
              if ((!ChatObject.isNotInChat((TLRPC.Chat)localObject)) && ((!ChatObject.isChannel((TLRPC.Chat)localObject)) || (((TLRPC.Chat)localObject).creator) || (((TLRPC.Chat)localObject).editor) || (((TLRPC.Chat)localObject).megagroup)) && (!((TLRPC.Chat)localObject).megagroup)) {
                this.dialogs.add(localTL_dialog);
              }
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
          localTL_dialog = (TLRPC.TL_dialog)MessagesController.getInstance().dialogsServerOnly.get(i);
          if (!this.preferences.contains("hide_" + String.valueOf(localTL_dialog.id)))
          {
            j = (int)localTL_dialog.id;
            k = (int)(localTL_dialog.id >> 32);
            if ((j != 0) && (k != 1) && (j > 0))
            {
              localObject = MessagesController.getInstance().getUser(Integer.valueOf(j));
              if ((localObject == null) || (!((TLRPC.User)localObject).bot)) {
                break label2375;
              }
            }
          }
          label2375:
          for (j = 1;; j = 0)
          {
            if (j != 0) {
              this.dialogs.add(localTL_dialog);
            }
            i += 1;
            break;
          }
        }
        return this.dialogs;
      }
    }
    return null;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\DialogsLoaderShareAlert.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */