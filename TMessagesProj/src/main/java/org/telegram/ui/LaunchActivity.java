package org.telegram.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NativeCrashManager;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.query.DraftQuery;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatInvite;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_messages_checkChatInvite;
import org.telegram.tgnet.TLRPC.TL_messages_importChatInvite;
import org.telegram.tgnet.TLRPC.TL_userContact_old2;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DrawerLayoutAdapter;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.PasscodeView.PasscodeViewDelegate;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Supergram.DownloadManager.Download;
import org.telegram.ui.Supergram.IdFinderActivity;
import org.telegram.ui.Supergram.MihanAuthenticaion;
import org.telegram.ui.Supergram.MihanSettingsActivity;
import org.telegram.ui.Supergram.OnlineContactsActivity;
import org.telegram.ui.Supergram.SpecialContacts.SpecialContactsActivity;
import org.telegram.ui.Supergram.UserChanges.UpdateActivity;

public class LaunchActivity
  extends Activity
  implements ActionBarLayout.ActionBarLayoutDelegate, NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate
{
  private static ArrayList<BaseFragment> layerFragmentsStack = new ArrayList();
  private static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList();
  private static ArrayList<BaseFragment> rightFragmentsStack = new ArrayList();
  private MihanAuthenticaion MihanAuthenticaion;
  private ActionBarLayout actionBarLayout;
  private ImageView backgroundTablet;
  private ArrayList<TLRPC.User> contactsToSend;
  private int currentConnectionState;
  private String documentsMimeType;
  private ArrayList<String> documentsOriginalPathsArray;
  private ArrayList<String> documentsPathsArray;
  private ArrayList<Uri> documentsUrisArray;
  private DrawerLayoutAdapter drawerLayoutAdapter;
  protected DrawerLayoutContainer drawerLayoutContainer;
  private boolean finished;
  private ActionBarLayout layersActionBarLayout;
  private Runnable lockRunnable;
  private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
  private Intent passcodeSaveIntent;
  private boolean passcodeSaveIntentIsNew;
  private boolean passcodeSaveIntentIsRestore;
  private PasscodeView passcodeView;
  private ArrayList<Uri> photoPathsArray;
  private ActionBarLayout rightActionBarLayout;
  private String sendingText;
  private FrameLayout shadowTablet;
  private FrameLayout shadowTabletSide;
  private boolean tabletFullSize;
  private String videoPath;
  private AlertDialog visibleDialog;
  
  private boolean handleIntent(Intent paramIntent, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    int i = paramIntent.getFlags();
    if ((!paramBoolean3) && ((AndroidUtilities.needShowPasscode(true)) || (UserConfig.isWaitingForPasscodeEnter)))
    {
      showPasscodeActivity();
      this.passcodeSaveIntent = paramIntent;
      this.passcodeSaveIntentIsNew = paramBoolean1;
      this.passcodeSaveIntentIsRestore = paramBoolean2;
      UserConfig.saveConfig(false);
      return false;
    }
    boolean bool1 = false;
    Integer localInteger1 = Integer.valueOf(0);
    Integer localInteger2 = Integer.valueOf(0);
    Integer localInteger3 = Integer.valueOf(0);
    Integer localInteger4 = Integer.valueOf(0);
    long l;
    int m;
    int n;
    Object localObject7;
    Object localObject8;
    Object localObject9;
    int j;
    int k;
    Object localObject10;
    Object localObject11;
    Object localObject12;
    Object localObject6;
    String[] arrayOfString1;
    label552:
    String[] arrayOfString2;
    label626:
    label678:
    label711:
    label911:
    label1018:
    label1076:
    label1142:
    label1199:
    label1278:
    label1393:
    Object localObject3;
    if ((paramIntent != null) && (paramIntent.getExtras() != null))
    {
      l = paramIntent.getExtras().getLong("dialogId", 0L);
      m = 0;
      n = 0;
      this.photoPathsArray = null;
      this.videoPath = null;
      this.sendingText = null;
      this.documentsPathsArray = null;
      this.documentsOriginalPathsArray = null;
      this.documentsMimeType = null;
      this.documentsUrisArray = null;
      this.contactsToSend = null;
      localObject7 = localInteger4;
      localObject8 = localInteger2;
      localObject9 = localInteger3;
      Object localObject1 = localInteger1;
      j = m;
      k = n;
      if (UserConfig.isClientActivated())
      {
        localObject7 = localInteger4;
        localObject8 = localInteger2;
        localObject9 = localInteger3;
        localObject1 = localInteger1;
        j = m;
        k = n;
        if ((0x100000 & i) == 0)
        {
          localObject7 = localInteger4;
          localObject8 = localInteger2;
          localObject9 = localInteger3;
          localObject1 = localInteger1;
          j = m;
          k = n;
          if (paramIntent != null)
          {
            localObject7 = localInteger4;
            localObject8 = localInteger2;
            localObject9 = localInteger3;
            localObject1 = localInteger1;
            j = m;
            k = n;
            if (paramIntent.getAction() != null)
            {
              localObject7 = localInteger4;
              localObject8 = localInteger2;
              localObject9 = localInteger3;
              localObject1 = localInteger1;
              j = m;
              k = n;
              if (!paramBoolean2)
              {
                if (!"android.intent.action.SEND".equals(paramIntent.getAction())) {
                  break label1898;
                }
                i = 0;
                k = 0;
                localObject7 = paramIntent.getType();
                if ((localObject7 == null) || (!((String)localObject7).equals("text/x-vcard"))) {
                  break label1393;
                }
                try
                {
                  localObject1 = (Uri)paramIntent.getExtras().get("android.intent.extra.STREAM");
                  if (localObject1 != null)
                  {
                    localObject10 = getContentResolver().openInputStream((Uri)localObject1);
                    localObject9 = new ArrayList();
                    localObject8 = null;
                    localObject11 = new BufferedReader(new InputStreamReader((InputStream)localObject10, "UTF-8"));
                    for (;;)
                    {
                      localObject1 = ((BufferedReader)localObject11).readLine();
                      if (localObject1 == null) {
                        break label1199;
                      }
                      FileLog.e("tmessages", (String)localObject1);
                      localObject12 = ((String)localObject1).split(":");
                      if (localObject12.length == 2)
                      {
                        if ((localObject12[0].equals("BEGIN")) && (localObject12[1].equals("VCARD")))
                        {
                          localObject1 = new VcardData(null);
                          ((ArrayList)localObject9).add(localObject1);
                        }
                        for (;;)
                        {
                          localObject8 = localObject1;
                          if (localObject1 == null) {
                            break;
                          }
                          if ((!localObject12[0].startsWith("FN")) && ((!localObject12[0].startsWith("ORG")) || (!TextUtils.isEmpty(((VcardData)localObject1).name)))) {
                            break label1142;
                          }
                          localObject7 = null;
                          localObject6 = null;
                          arrayOfString1 = localObject12[0].split(";");
                          j = arrayOfString1.length;
                          i = 0;
                          if (i >= j) {
                            break label678;
                          }
                          arrayOfString2 = arrayOfString1[i].split("=");
                          if (arrayOfString2.length == 2) {
                            break label626;
                          }
                          localObject8 = localObject6;
                          break label5468;
                          localObject1 = localObject8;
                          if (localObject12[0].equals("END"))
                          {
                            localObject1 = localObject8;
                            if (localObject12[1].equals("VCARD")) {
                              localObject1 = null;
                            }
                          }
                        }
                        if (arrayOfString2[0].equals("CHARSET"))
                        {
                          localObject8 = arrayOfString2[1];
                          break label5468;
                        }
                        localObject8 = localObject6;
                        if (!arrayOfString2[0].equals("ENCODING")) {
                          break label5468;
                        }
                        localObject7 = arrayOfString2[1];
                        localObject8 = localObject6;
                        break label5468;
                        ((VcardData)localObject1).name = localObject12[1];
                        localObject8 = localObject1;
                        if (localObject7 != null)
                        {
                          localObject8 = localObject1;
                          if (((String)localObject7).equalsIgnoreCase("QUOTED-PRINTABLE"))
                          {
                            if ((((VcardData)localObject1).name.endsWith("=")) && (localObject7 != null))
                            {
                              ((VcardData)localObject1).name = ((VcardData)localObject1).name.substring(0, ((VcardData)localObject1).name.length() - 1);
                              localObject8 = ((BufferedReader)localObject11).readLine();
                              if (localObject8 != null) {
                                break;
                              }
                            }
                            localObject7 = AndroidUtilities.decodeQuotedPrintable(((VcardData)localObject1).name.getBytes());
                            localObject8 = localObject1;
                            if (localObject7 != null)
                            {
                              localObject8 = localObject1;
                              if (localObject7.length != 0)
                              {
                                localObject6 = new String((byte[])localObject7, (String)localObject6);
                                localObject8 = localObject1;
                                if (localObject6 != null)
                                {
                                  ((VcardData)localObject1).name = ((String)localObject6);
                                  localObject8 = localObject1;
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                    localObject7 = localInteger4;
                  }
                }
                catch (Exception localException1)
                {
                  FileLog.e("tmessages", localException1);
                  i = 1;
                }
              }
            }
          }
        }
      }
      for (;;)
      {
        localObject8 = localInteger2;
        localObject9 = localInteger3;
        Object localObject2 = localInteger1;
        j = m;
        k = n;
        if (i != 0)
        {
          Toast.makeText(this, "Unsupported content", 0).show();
          k = n;
          j = m;
          localObject2 = localInteger1;
          localObject9 = localInteger3;
          localObject8 = localInteger2;
          localObject7 = localInteger4;
        }
        if (((Integer)localObject2).intValue() == 0) {
          break label4412;
        }
        localObject6 = new Bundle();
        ((Bundle)localObject6).putInt("user_id", ((Integer)localObject2).intValue());
        if (!mainFragmentsStack.isEmpty())
        {
          paramBoolean2 = bool1;
          paramBoolean3 = paramBoolean1;
          if (!MessagesController.checkCanOpenChat((Bundle)localObject6, (BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1))) {}
        }
        else
        {
          localObject2 = new ChatActivity((Bundle)localObject6);
          paramBoolean2 = bool1;
          paramBoolean3 = paramBoolean1;
          if (this.actionBarLayout.presentFragment((BaseFragment)localObject2, false, true, true))
          {
            paramBoolean2 = true;
            paramBoolean3 = paramBoolean1;
          }
        }
        if ((!paramBoolean2) && (!paramBoolean3))
        {
          if (!AndroidUtilities.isTablet()) {
            break label5394;
          }
          if (UserConfig.isClientActivated()) {
            break label5353;
          }
          if (this.layersActionBarLayout.fragmentsStack.isEmpty())
          {
            this.layersActionBarLayout.addFragmentToStack(new LoginActivity());
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
          }
          this.actionBarLayout.showLastFragment();
          if (AndroidUtilities.isTablet())
          {
            this.layersActionBarLayout.showLastFragment();
            this.rightActionBarLayout.showLastFragment();
          }
        }
        paramIntent.setAction(null);
        return paramBoolean2;
        ((VcardData)localObject2).name += (String)localObject8;
        break label711;
        localObject8 = localObject2;
        if (!localObject12[0].startsWith("TEL")) {
          break;
        }
        localObject6 = PhoneFormat.stripExceptNumbers(localObject12[1], true);
        localObject8 = localObject2;
        if (((String)localObject6).length() <= 0) {
          break;
        }
        ((VcardData)localObject2).phones.add(localObject6);
        localObject8 = localObject2;
        break;
        try
        {
          ((BufferedReader)localObject11).close();
          ((InputStream)localObject10).close();
          j = 0;
          i = k;
          if (j >= ((ArrayList)localObject9).size()) {
            continue;
          }
          localObject2 = (VcardData)((ArrayList)localObject9).get(j);
          if ((((VcardData)localObject2).name != null) && (!((VcardData)localObject2).phones.isEmpty()))
          {
            if (this.contactsToSend != null) {
              break label5487;
            }
            this.contactsToSend = new ArrayList();
            break label5487;
            while (i < ((VcardData)localObject2).phones.size())
            {
              localObject6 = (String)((VcardData)localObject2).phones.get(i);
              localObject7 = new TLRPC.TL_userContact_old2();
              ((TLRPC.User)localObject7).phone = ((String)localObject6);
              ((TLRPC.User)localObject7).first_name = ((VcardData)localObject2).name;
              ((TLRPC.User)localObject7).last_name = "";
              ((TLRPC.User)localObject7).id = 0;
              this.contactsToSend.add(localObject7);
              i += 1;
            }
          }
        }
        catch (Exception localException2)
        {
          for (;;)
          {
            FileLog.e("tmessages", localException2);
            continue;
            j += 1;
          }
        }
        i = 1;
        continue;
        localObject6 = paramIntent.getStringExtra("android.intent.extra.TEXT");
        localObject3 = localObject6;
        if (localObject6 == null)
        {
          localObject8 = paramIntent.getCharSequenceExtra("android.intent.extra.TEXT");
          localObject3 = localObject6;
          if (localObject8 != null) {
            localObject3 = ((CharSequence)localObject8).toString();
          }
        }
        localObject8 = paramIntent.getStringExtra("android.intent.extra.SUBJECT");
        if ((localObject3 != null) && (((String)localObject3).length() != 0)) {
          if (!((String)localObject3).startsWith("http://"))
          {
            localObject6 = localObject3;
            if (!((String)localObject3).startsWith("https://")) {}
          }
          else
          {
            localObject6 = localObject3;
            if (localObject8 != null)
            {
              localObject6 = localObject3;
              if (((String)localObject8).length() != 0) {
                localObject6 = (String)localObject8 + "\n" + (String)localObject3;
              }
            }
          }
        }
        for (this.sendingText = ((String)localObject6);; this.sendingText = ((String)localObject8)) {
          do
          {
            localObject6 = paramIntent.getParcelableExtra("android.intent.extra.STREAM");
            if (localObject6 == null) {
              break label1881;
            }
            localObject3 = localObject6;
            if (!(localObject6 instanceof Uri)) {
              localObject3 = Uri.parse(localObject6.toString());
            }
            localObject8 = (Uri)localObject3;
            j = i;
            if (localObject8 != null)
            {
              j = i;
              if (AndroidUtilities.isInternalUri((Uri)localObject8)) {
                j = 1;
              }
            }
            i = j;
            if (j != 0) {
              break;
            }
            if ((localObject8 == null) || (((localObject7 == null) || (!((String)localObject7).startsWith("image/"))) && (!((Uri)localObject8).toString().toLowerCase().endsWith(".jpg")))) {
              break label1712;
            }
            if (this.photoPathsArray == null) {
              this.photoPathsArray = new ArrayList();
            }
            this.photoPathsArray.add(localObject8);
            i = j;
            break;
          } while ((localObject8 == null) || (((String)localObject8).length() <= 0));
        }
        label1712:
        localObject6 = AndroidUtilities.getPath((Uri)localObject8);
        if (localObject6 != null)
        {
          localObject3 = localObject6;
          if (((String)localObject6).startsWith("file:")) {
            localObject3 = ((String)localObject6).replace("file://", "");
          }
          if ((localObject7 != null) && (((String)localObject7).startsWith("video/")))
          {
            this.videoPath = ((String)localObject3);
            i = j;
          }
          else
          {
            if (this.documentsPathsArray == null)
            {
              this.documentsPathsArray = new ArrayList();
              this.documentsOriginalPathsArray = new ArrayList();
            }
            this.documentsPathsArray.add(localObject3);
            this.documentsOriginalPathsArray.add(((Uri)localObject8).toString());
            i = j;
          }
        }
        else
        {
          if (this.documentsUrisArray == null) {
            this.documentsUrisArray = new ArrayList();
          }
          this.documentsUrisArray.add(localObject8);
          this.documentsMimeType = ((String)localObject7);
          i = j;
          continue;
          label1881:
          i = k;
          if (this.sendingText == null) {
            i = 1;
          }
        }
      }
      label1898:
      if (paramIntent.getAction().equals("android.intent.action.SEND_MULTIPLE")) {
        k = 0;
      }
    }
    for (;;)
    {
      try
      {
        localObject6 = paramIntent.getParcelableArrayListExtra("android.intent.extra.STREAM");
        localObject8 = paramIntent.getType();
        localObject3 = localObject6;
        if (localObject6 != null)
        {
          i = 0;
          if (i < ((ArrayList)localObject6).size())
          {
            localObject7 = (Parcelable)((ArrayList)localObject6).get(i);
            localObject3 = localObject7;
            if (!(localObject7 instanceof Uri)) {
              localObject3 = Uri.parse(localObject7.toString());
            }
            localObject3 = (Uri)localObject3;
            j = i;
            if (localObject3 == null) {
              break label5493;
            }
            j = i;
            if (!AndroidUtilities.isInternalUri((Uri)localObject3)) {
              break label5493;
            }
            ((ArrayList)localObject6).remove(i);
            j = i - 1;
            break label5493;
          }
          localObject3 = localObject6;
          if (((ArrayList)localObject6).isEmpty()) {
            localObject3 = null;
          }
        }
        if (localObject3 != null)
        {
          if ((localObject8 == null) || (!((String)localObject8).startsWith("image/"))) {
            break label5502;
          }
          j = 0;
          i = k;
          if (j < ((ArrayList)localObject3).size())
          {
            localObject7 = (Parcelable)((ArrayList)localObject3).get(j);
            localObject6 = localObject7;
            if (!(localObject7 instanceof Uri)) {
              localObject6 = Uri.parse(localObject7.toString());
            }
            localObject6 = (Uri)localObject6;
            if (this.photoPathsArray == null) {
              this.photoPathsArray = new ArrayList();
            }
            this.photoPathsArray.add(localObject6);
            j += 1;
            continue;
            i = k;
            if (j < ((ArrayList)localObject3).size())
            {
              localObject6 = (Parcelable)((ArrayList)localObject3).get(j);
              localObject7 = localObject6;
              if (!(localObject6 instanceof Uri)) {
                localObject7 = Uri.parse(localObject6.toString());
              }
              localObject6 = AndroidUtilities.getPath((Uri)localObject7);
              localObject8 = localObject7.toString();
              localObject7 = localObject8;
              if (localObject8 == null) {
                localObject7 = localObject6;
              }
              if (localObject6 != null)
              {
                localObject8 = localObject6;
                if (((String)localObject6).startsWith("file:")) {
                  localObject8 = ((String)localObject6).replace("file://", "");
                }
                if (this.documentsPathsArray == null)
                {
                  this.documentsPathsArray = new ArrayList();
                  this.documentsOriginalPathsArray = new ArrayList();
                }
                this.documentsPathsArray.add(localObject8);
                this.documentsOriginalPathsArray.add(localObject7);
              }
              j += 1;
              continue;
            }
          }
        }
        else
        {
          i = 1;
        }
      }
      catch (Exception localException3)
      {
        FileLog.e("tmessages", localException3);
        i = 1;
        continue;
      }
      localObject7 = localInteger4;
      localObject8 = localInteger2;
      localObject9 = localInteger3;
      localObject3 = localInteger1;
      j = m;
      k = n;
      if (i == 0) {
        break label911;
      }
      Toast.makeText(this, "Unsupported content", 0).show();
      localObject7 = localInteger4;
      localObject8 = localInteger2;
      localObject9 = localInteger3;
      localObject3 = localInteger1;
      j = m;
      k = n;
      break label911;
      if ("android.intent.action.VIEW".equals(paramIntent.getAction()))
      {
        Uri localUri = paramIntent.getData();
        localObject7 = localInteger4;
        localObject8 = localInteger2;
        localObject9 = localInteger3;
        Object localObject4 = localInteger1;
        j = m;
        k = n;
        if (localUri == null) {
          break label911;
        }
        Object localObject14 = null;
        String str1 = null;
        arrayOfString1 = null;
        arrayOfString2 = null;
        String str2 = null;
        String str3 = null;
        localObject7 = null;
        Object localObject13 = null;
        Integer localInteger5 = null;
        boolean bool2 = false;
        boolean bool3 = false;
        paramBoolean3 = false;
        String str4 = localUri.getScheme();
        localObject8 = str1;
        localObject9 = arrayOfString1;
        localObject10 = arrayOfString2;
        localObject11 = str2;
        localObject12 = str3;
        localObject4 = localObject7;
        paramBoolean2 = paramBoolean3;
        localObject6 = localInteger5;
        if (str4 != null)
        {
          if ((!str4.equals("http")) && (!str4.equals("https"))) {
            break label3248;
          }
          str4 = localUri.getHost().toLowerCase();
          if (!str4.equals("telegram.me"))
          {
            localObject8 = str1;
            localObject9 = arrayOfString1;
            localObject10 = arrayOfString2;
            localObject11 = str2;
            localObject12 = str3;
            localObject4 = localObject7;
            paramBoolean2 = paramBoolean3;
            localObject6 = localInteger5;
            if (!str4.equals("telegram.dog")) {}
          }
          else
          {
            str4 = localUri.getPath();
            localObject8 = str1;
            localObject9 = arrayOfString1;
            localObject10 = arrayOfString2;
            localObject11 = str2;
            localObject12 = str3;
            localObject4 = localObject7;
            paramBoolean2 = paramBoolean3;
            localObject6 = localInteger5;
            if (str4 != null)
            {
              localObject8 = str1;
              localObject9 = arrayOfString1;
              localObject10 = arrayOfString2;
              localObject11 = str2;
              localObject12 = str3;
              localObject4 = localObject7;
              paramBoolean2 = paramBoolean3;
              localObject6 = localInteger5;
              if (str4.length() > 1)
              {
                str4 = str4.substring(1);
                if (!str4.startsWith("joinchat/")) {
                  break label2837;
                }
                localObject9 = str4.replace("joinchat/", "");
                localObject6 = localInteger5;
                paramBoolean2 = paramBoolean3;
                localObject4 = localObject7;
                localObject12 = str3;
                localObject11 = str2;
                localObject10 = arrayOfString2;
                localObject8 = str1;
              }
            }
          }
        }
        for (;;)
        {
          if ((localObject8 == null) && (localObject9 == null) && (localObject10 == null) && (localObject4 == null)) {
            break label3904;
          }
          runLinkRequest((String)localObject8, (String)localObject9, (String)localObject10, (String)localObject11, (String)localObject12, (String)localObject4, paramBoolean2, (Integer)localObject6, 0);
          localObject7 = localInteger4;
          localObject8 = localInteger2;
          localObject9 = localInteger3;
          localObject4 = localInteger1;
          j = m;
          k = n;
          break;
          label2837:
          if (str4.startsWith("addstickers/"))
          {
            localObject10 = str4.replace("addstickers/", "");
            localObject8 = str1;
            localObject9 = arrayOfString1;
            localObject11 = str2;
            localObject12 = str3;
            localObject4 = localObject7;
            paramBoolean2 = paramBoolean3;
            localObject6 = localInteger5;
          }
          else if ((str4.startsWith("msg/")) || (str4.startsWith("share/")))
          {
            localObject4 = localUri.getQueryParameter("url");
            localObject7 = localObject4;
            if (localObject4 == null) {
              localObject7 = "";
            }
            localObject8 = str1;
            localObject9 = arrayOfString1;
            localObject10 = arrayOfString2;
            localObject11 = str2;
            localObject12 = str3;
            localObject4 = localObject7;
            paramBoolean2 = paramBoolean3;
            localObject6 = localInteger5;
            if (localUri.getQueryParameter("text") != null)
            {
              localObject4 = localObject7;
              paramBoolean2 = bool2;
              if (((String)localObject7).length() > 0)
              {
                paramBoolean2 = true;
                localObject4 = (String)localObject7 + "\n";
              }
              localObject4 = (String)localObject4 + localUri.getQueryParameter("text");
              localObject8 = str1;
              localObject9 = arrayOfString1;
              localObject10 = arrayOfString2;
              localObject11 = str2;
              localObject12 = str3;
              localObject6 = localInteger5;
            }
          }
          else
          {
            localObject8 = str1;
            localObject9 = arrayOfString1;
            localObject10 = arrayOfString2;
            localObject11 = str2;
            localObject12 = str3;
            localObject4 = localObject7;
            paramBoolean2 = paramBoolean3;
            localObject6 = localInteger5;
            if (str4.length() >= 1)
            {
              localObject9 = localUri.getPathSegments();
              localObject8 = localObject14;
              localObject6 = localObject13;
              if (((List)localObject9).size() > 0)
              {
                localObject4 = (String)((List)localObject9).get(0);
                localObject8 = localObject4;
                localObject6 = localObject13;
                if (((List)localObject9).size() > 1)
                {
                  localObject9 = Utilities.parseInt((String)((List)localObject9).get(1));
                  localObject8 = localObject4;
                  localObject6 = localObject9;
                  if (((Integer)localObject9).intValue() == 0)
                  {
                    localObject6 = null;
                    localObject8 = localObject4;
                  }
                }
              }
              localObject11 = localUri.getQueryParameter("start");
              localObject12 = localUri.getQueryParameter("startgroup");
              localObject9 = arrayOfString1;
              localObject10 = arrayOfString2;
              localObject4 = localObject7;
              paramBoolean2 = paramBoolean3;
              continue;
              label3248:
              localObject8 = str1;
              localObject9 = arrayOfString1;
              localObject10 = arrayOfString2;
              localObject11 = str2;
              localObject12 = str3;
              localObject4 = localObject7;
              paramBoolean2 = paramBoolean3;
              localObject6 = localInteger5;
              if (str4.equals("tg"))
              {
                localObject13 = localUri.toString();
                if ((((String)localObject13).startsWith("tg:resolve")) || (((String)localObject13).startsWith("tg://resolve")))
                {
                  localObject4 = Uri.parse(((String)localObject13).replace("tg:resolve", "tg://telegram.org").replace("tg://resolve", "tg://telegram.org"));
                  str1 = ((Uri)localObject4).getQueryParameter("domain");
                  str2 = ((Uri)localObject4).getQueryParameter("start");
                  str3 = ((Uri)localObject4).getQueryParameter("startgroup");
                  localInteger5 = Utilities.parseInt(((Uri)localObject4).getQueryParameter("post"));
                  localObject8 = str1;
                  localObject9 = arrayOfString1;
                  localObject10 = arrayOfString2;
                  localObject11 = str2;
                  localObject12 = str3;
                  localObject4 = localObject7;
                  paramBoolean2 = paramBoolean3;
                  localObject6 = localInteger5;
                  if (localInteger5.intValue() == 0)
                  {
                    localObject6 = null;
                    localObject8 = str1;
                    localObject9 = arrayOfString1;
                    localObject10 = arrayOfString2;
                    localObject11 = str2;
                    localObject12 = str3;
                    localObject4 = localObject7;
                    paramBoolean2 = paramBoolean3;
                  }
                }
                else if ((((String)localObject13).startsWith("tg:join")) || (((String)localObject13).startsWith("tg://join")))
                {
                  localObject9 = Uri.parse(((String)localObject13).replace("tg:join", "tg://telegram.org").replace("tg://join", "tg://telegram.org")).getQueryParameter("invite");
                  localObject8 = str1;
                  localObject10 = arrayOfString2;
                  localObject11 = str2;
                  localObject12 = str3;
                  localObject4 = localObject7;
                  paramBoolean2 = paramBoolean3;
                  localObject6 = localInteger5;
                }
                else if ((((String)localObject13).startsWith("tg:addstickers")) || (((String)localObject13).startsWith("tg://addstickers")))
                {
                  localObject10 = Uri.parse(((String)localObject13).replace("tg:addstickers", "tg://telegram.org").replace("tg://addstickers", "tg://telegram.org")).getQueryParameter("set");
                  localObject8 = str1;
                  localObject9 = arrayOfString1;
                  localObject11 = str2;
                  localObject12 = str3;
                  localObject4 = localObject7;
                  paramBoolean2 = paramBoolean3;
                  localObject6 = localInteger5;
                }
                else if ((!((String)localObject13).startsWith("tg:msg")) && (!((String)localObject13).startsWith("tg://msg")) && (!((String)localObject13).startsWith("tg://share")))
                {
                  localObject8 = str1;
                  localObject9 = arrayOfString1;
                  localObject10 = arrayOfString2;
                  localObject11 = str2;
                  localObject12 = str3;
                  localObject4 = localObject7;
                  paramBoolean2 = paramBoolean3;
                  localObject6 = localInteger5;
                  if (!((String)localObject13).startsWith("tg:share")) {}
                }
                else
                {
                  localObject13 = Uri.parse(((String)localObject13).replace("tg:msg", "tg://telegram.org").replace("tg://msg", "tg://telegram.org").replace("tg://share", "tg://telegram.org").replace("tg:share", "tg://telegram.org"));
                  localObject4 = ((Uri)localObject13).getQueryParameter("url");
                  localObject7 = localObject4;
                  if (localObject4 == null) {
                    localObject7 = "";
                  }
                  localObject8 = str1;
                  localObject9 = arrayOfString1;
                  localObject10 = arrayOfString2;
                  localObject11 = str2;
                  localObject12 = str3;
                  localObject4 = localObject7;
                  paramBoolean2 = paramBoolean3;
                  localObject6 = localInteger5;
                  if (((Uri)localObject13).getQueryParameter("text") != null)
                  {
                    localObject4 = localObject7;
                    paramBoolean2 = bool3;
                    if (((String)localObject7).length() > 0)
                    {
                      paramBoolean2 = true;
                      localObject4 = (String)localObject7 + "\n";
                    }
                    localObject4 = (String)localObject4 + ((Uri)localObject13).getQueryParameter("text");
                    localObject8 = str1;
                    localObject9 = arrayOfString1;
                    localObject10 = arrayOfString2;
                    localObject11 = str2;
                    localObject12 = str3;
                    localObject6 = localInteger5;
                  }
                }
              }
            }
          }
        }
        label3904:
        localObject6 = localInteger1;
        try
        {
          localObject10 = getContentResolver().query(paramIntent.getData(), null, null, null, null);
          localObject7 = localInteger4;
          localObject8 = localInteger2;
          localObject9 = localInteger3;
          localObject4 = localInteger1;
          j = m;
          k = n;
          if (localObject10 == null) {
            break label911;
          }
          localObject4 = localInteger1;
          localObject6 = localInteger1;
          if (((Cursor)localObject10).moveToFirst())
          {
            localObject6 = localInteger1;
            i = ((Cursor)localObject10).getInt(((Cursor)localObject10).getColumnIndex("DATA4"));
            localObject6 = localInteger1;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            localObject6 = localInteger1;
            localObject4 = Integer.valueOf(i);
          }
          localObject6 = localObject4;
          ((Cursor)localObject10).close();
          localObject7 = localInteger4;
          localObject8 = localInteger2;
          localObject9 = localInteger3;
          j = m;
          k = n;
        }
        catch (Exception localException4)
        {
          FileLog.e("tmessages", localException4);
          localObject7 = localInteger4;
          localObject8 = localInteger2;
          localObject9 = localInteger3;
          localObject5 = localObject6;
          j = m;
          k = n;
        }
        break label911;
      }
      if (paramIntent.getAction().equals("org.telegram.messenger.OPEN_ACCOUNT"))
      {
        localObject7 = Integer.valueOf(1);
        localObject8 = localInteger2;
        localObject9 = localInteger3;
        localObject5 = localInteger1;
        j = m;
        k = n;
        break label911;
      }
      if (paramIntent.getAction().startsWith("com.tmessages.openchat"))
      {
        i = paramIntent.getIntExtra("chatId", 0);
        j = paramIntent.getIntExtra("userId", 0);
        k = paramIntent.getIntExtra("encId", 0);
        if (i != 0)
        {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
          localObject8 = Integer.valueOf(i);
          localObject7 = localInteger4;
          localObject9 = localInteger3;
          localObject5 = localInteger1;
          j = m;
          k = n;
          break label911;
        }
        if (j != 0)
        {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
          localObject5 = Integer.valueOf(j);
          localObject7 = localInteger4;
          localObject8 = localInteger2;
          localObject9 = localInteger3;
          j = m;
          k = n;
          break label911;
        }
        if (k != 0)
        {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
          localObject9 = Integer.valueOf(k);
          localObject7 = localInteger4;
          localObject8 = localInteger2;
          localObject5 = localInteger1;
          j = m;
          k = n;
          break label911;
        }
        j = 1;
        localObject7 = localInteger4;
        localObject8 = localInteger2;
        localObject9 = localInteger3;
        localObject5 = localInteger1;
        k = n;
        break label911;
      }
      localObject7 = localInteger4;
      localObject8 = localInteger2;
      localObject9 = localInteger3;
      Object localObject5 = localInteger1;
      j = m;
      k = n;
      if (!paramIntent.getAction().equals("com.tmessages.openplayer")) {
        break label911;
      }
      k = 1;
      localObject7 = localInteger4;
      localObject8 = localInteger2;
      localObject9 = localInteger3;
      localObject5 = localInteger1;
      j = m;
      break label911;
      label4412:
      if (((Integer)localObject8).intValue() != 0)
      {
        localObject5 = new Bundle();
        ((Bundle)localObject5).putInt("chat_id", ((Integer)localObject8).intValue());
        if (!mainFragmentsStack.isEmpty())
        {
          paramBoolean2 = bool1;
          paramBoolean3 = paramBoolean1;
          if (!MessagesController.checkCanOpenChat((Bundle)localObject5, (BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1))) {
            break label1018;
          }
        }
        localObject5 = new ChatActivity((Bundle)localObject5);
        paramBoolean2 = bool1;
        paramBoolean3 = paramBoolean1;
        if (!this.actionBarLayout.presentFragment((BaseFragment)localObject5, false, true, true)) {
          break label1018;
        }
        paramBoolean2 = true;
        paramBoolean3 = paramBoolean1;
        break label1018;
      }
      if (((Integer)localObject9).intValue() != 0)
      {
        localObject5 = new Bundle();
        ((Bundle)localObject5).putInt("enc_id", ((Integer)localObject9).intValue());
        localObject5 = new ChatActivity((Bundle)localObject5);
        paramBoolean2 = bool1;
        paramBoolean3 = paramBoolean1;
        if (!this.actionBarLayout.presentFragment((BaseFragment)localObject5, false, true, true)) {
          break label1018;
        }
        paramBoolean2 = true;
        paramBoolean3 = paramBoolean1;
        break label1018;
      }
      if (j != 0)
      {
        if (!AndroidUtilities.isTablet()) {
          this.actionBarLayout.removeAllFragments();
        }
        for (;;)
        {
          paramBoolean2 = false;
          paramBoolean3 = false;
          break;
          if (!this.layersActionBarLayout.fragmentsStack.isEmpty())
          {
            for (i = 0; this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
              this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
            }
            this.layersActionBarLayout.closeLastFragment(false);
          }
        }
      }
      if (k != 0)
      {
        if (AndroidUtilities.isTablet())
        {
          i = 0;
          for (;;)
          {
            if (i < this.layersActionBarLayout.fragmentsStack.size())
            {
              localObject5 = (BaseFragment)this.layersActionBarLayout.fragmentsStack.get(i);
              if ((localObject5 instanceof AudioPlayerActivity)) {
                this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)localObject5);
              }
            }
            else
            {
              this.actionBarLayout.showLastFragment();
              this.rightActionBarLayout.showLastFragment();
              this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
              this.actionBarLayout.presentFragment(new AudioPlayerActivity(), false, true, true);
              paramBoolean2 = true;
              paramBoolean3 = paramBoolean1;
              break;
            }
            i += 1;
          }
        }
        i = 0;
        for (;;)
        {
          if (i < this.actionBarLayout.fragmentsStack.size())
          {
            localObject5 = (BaseFragment)this.actionBarLayout.fragmentsStack.get(i);
            if ((localObject5 instanceof AudioPlayerActivity)) {
              this.actionBarLayout.removeFragmentFromStack((BaseFragment)localObject5);
            }
          }
          else
          {
            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            break;
          }
          i += 1;
        }
      }
      if ((this.videoPath != null) || (this.photoPathsArray != null) || (this.sendingText != null) || (this.documentsPathsArray != null) || (this.contactsToSend != null) || (this.documentsUrisArray != null))
      {
        if (!AndroidUtilities.isTablet()) {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        if (l == 0L)
        {
          localObject5 = new Bundle();
          ((Bundle)localObject5).putBoolean("onlySelect", true);
          if (this.contactsToSend != null)
          {
            ((Bundle)localObject5).putString("selectAlertString", LocaleController.getString("SendContactTo", 2131166289));
            ((Bundle)localObject5).putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", 2131166285));
            label5013:
            localObject5 = new DialogsActivity((Bundle)localObject5);
            ((DialogsActivity)localObject5).setDelegate(this);
            if (!AndroidUtilities.isTablet()) {
              break label5188;
            }
            if ((this.layersActionBarLayout.fragmentsStack.size() <= 0) || (!(this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity))) {
              break label5183;
            }
          }
          label5183:
          for (paramBoolean2 = true;; paramBoolean2 = false)
          {
            this.actionBarLayout.presentFragment((BaseFragment)localObject5, paramBoolean2, true, true);
            paramBoolean2 = true;
            if (PhotoViewer.getInstance().isVisible()) {
              PhotoViewer.getInstance().closePhoto(false, true);
            }
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            if (!AndroidUtilities.isTablet()) {
              break label5240;
            }
            this.actionBarLayout.showLastFragment();
            this.rightActionBarLayout.showLastFragment();
            paramBoolean3 = paramBoolean1;
            break;
            ((Bundle)localObject5).putString("selectAlertString", LocaleController.getString("SendMessagesTo", 2131166289));
            ((Bundle)localObject5).putString("selectAlertStringGroup", LocaleController.getString("SendMessagesToGroup", 2131166290));
            break label5013;
          }
          label5188:
          if ((this.actionBarLayout.fragmentsStack.size() > 1) && ((this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity))) {}
          for (paramBoolean2 = true;; paramBoolean2 = false) {
            break;
          }
          label5240:
          this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
          paramBoolean3 = paramBoolean1;
          break label1018;
        }
        didSelectDialog(null, l, false);
        paramBoolean2 = bool1;
        paramBoolean3 = paramBoolean1;
        break label1018;
      }
      paramBoolean2 = bool1;
      paramBoolean3 = paramBoolean1;
      if (((Integer)localObject7).intValue() == 0) {
        break label1018;
      }
      this.actionBarLayout.presentFragment(new SettingsActivity(), false, true, true);
      if (AndroidUtilities.isTablet())
      {
        this.actionBarLayout.showLastFragment();
        this.rightActionBarLayout.showLastFragment();
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
      }
      for (;;)
      {
        paramBoolean2 = true;
        paramBoolean3 = paramBoolean1;
        break;
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
      }
      label5353:
      if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
        break label1076;
      }
      this.actionBarLayout.addFragmentToStack(new DialogsActivity(null));
      this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
      break label1076;
      label5394:
      if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
        break label1076;
      }
      if (!UserConfig.isClientActivated())
      {
        this.actionBarLayout.addFragmentToStack(new LoginActivity());
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        break label1076;
      }
      this.actionBarLayout.addFragmentToStack(new DialogsActivity(null));
      this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
      break label1076;
      label5468:
      i += 1;
      localObject6 = localObject8;
      break label552;
      l = 0L;
      break;
      label5487:
      i = 0;
      break label1278;
      label5493:
      i = j + 1;
      continue;
      label5502:
      j = 0;
    }
  }
  
  private void onFinish()
  {
    if (this.finished) {
      return;
    }
    this.finished = true;
    if (this.lockRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
      this.lockRunnable = null;
    }
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mainUserInfoChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeOtherAppActivities);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didUpdatedConnectionState);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.needShowAlert);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
  }
  
  private void onPasscodePause()
  {
    if (this.lockRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
      this.lockRunnable = null;
    }
    if (UserConfig.passcodeHash.length() != 0)
    {
      UserConfig.lastPauseTime = ConnectionsManager.getInstance().getCurrentTime();
      this.lockRunnable = new Runnable()
      {
        public void run()
        {
          if (LaunchActivity.this.lockRunnable == this)
          {
            if (!AndroidUtilities.needShowPasscode(true)) {
              break label42;
            }
            FileLog.e("tmessages", "lock app");
            LaunchActivity.this.showPasscodeActivity();
          }
          for (;;)
          {
            LaunchActivity.access$1102(LaunchActivity.this, null);
            return;
            label42:
            FileLog.e("tmessages", "didn't pass lock check");
          }
        }
      };
      if (UserConfig.appLocked) {
        AndroidUtilities.runOnUIThread(this.lockRunnable, 1000L);
      }
    }
    for (;;)
    {
      UserConfig.saveConfig(false);
      return;
      if (UserConfig.autoLockIn != 0)
      {
        AndroidUtilities.runOnUIThread(this.lockRunnable, UserConfig.autoLockIn * 1000L + 1000L);
        continue;
        UserConfig.lastPauseTime = 0;
      }
    }
  }
  
  private void onPasscodeResume()
  {
    if (this.lockRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
      this.lockRunnable = null;
    }
    if (AndroidUtilities.needShowPasscode(true)) {
      showPasscodeActivity();
    }
    if (UserConfig.lastPauseTime != 0)
    {
      UserConfig.lastPauseTime = 0;
      UserConfig.saveConfig(false);
    }
  }
  
  private void runLinkRequest(final String paramString1, final String paramString2, final String paramString3, final String paramString4, final String paramString5, final String paramString6, final boolean paramBoolean, final Integer paramInteger, int paramInt)
  {
    final ProgressDialog localProgressDialog = new ProgressDialog(this);
    localProgressDialog.setMessage(LocaleController.getString("Loading", 2131165905));
    localProgressDialog.setCanceledOnTouchOutside(false);
    localProgressDialog.setCancelable(false);
    int j = 0;
    final int i;
    if (paramString1 != null)
    {
      paramString2 = new TLRPC.TL_contacts_resolveUsername();
      paramString2.username = paramString1;
      i = ConnectionsManager.getInstance().sendRequest(paramString2, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (!LaunchActivity.this.isFinishing()) {}
              label200:
              Object localObject4;
              label295:
              label576:
              label580:
              for (;;)
              {
                try
                {
                  LaunchActivity.7.this.val$progressDialog.dismiss();
                  if ((paramAnonymousTL_error != null) || (LaunchActivity.this.actionBarLayout == null)) {
                    break label626;
                  }
                  Object localObject1 = (TLRPC.TL_contacts_resolvedPeer)paramAnonymousTLObject;
                  MessagesController.getInstance().putUsers(((TLRPC.TL_contacts_resolvedPeer)localObject1).users, false);
                  MessagesController.getInstance().putChats(((TLRPC.TL_contacts_resolvedPeer)localObject1).chats, false);
                  MessagesStorage.getInstance().putUsersAndChats(((TLRPC.TL_contacts_resolvedPeer)localObject1).users, ((TLRPC.TL_contacts_resolvedPeer)localObject1).chats, false, true);
                  if (LaunchActivity.7.this.val$botChat == null) {
                    break label295;
                  }
                  if (!((TLRPC.TL_contacts_resolvedPeer)localObject1).users.isEmpty())
                  {
                    localObject1 = (TLRPC.User)((TLRPC.TL_contacts_resolvedPeer)localObject1).users.get(0);
                    if (localObject1 != null) {
                      if ((!((TLRPC.User)localObject1).bot) || (!((TLRPC.User)localObject1).bot_nochats)) {
                        break label200;
                      }
                    }
                  }
                }
                catch (Exception localException1)
                {
                  try
                  {
                    Toast.makeText(LaunchActivity.this, LocaleController.getString("BotCantJoinGroups", 2131165406), 0).show();
                    return;
                  }
                  catch (Exception localException2)
                  {
                    Object localObject2;
                    FileLog.e("tmessages", localException2);
                    return;
                  }
                  localException1 = localException1;
                  FileLog.e("tmessages", localException1);
                  continue;
                  localObject2 = null;
                  continue;
                }
                localObject4 = new Bundle();
                ((Bundle)localObject4).putBoolean("onlySelect", true);
                ((Bundle)localObject4).putInt("dialogsType", 2);
                ((Bundle)localObject4).putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", 2131165311, new Object[] { UserObject.getUserName(localException2), "%1$s" }));
                localObject4 = new DialogsActivity((Bundle)localObject4);
                ((DialogsActivity)localObject4).setDelegate(new DialogsActivity.DialogsActivityDelegate()
                {
                  public void didSelectDialog(DialogsActivity paramAnonymous3DialogsActivity, long paramAnonymous3Long, boolean paramAnonymous3Boolean)
                  {
                    paramAnonymous3DialogsActivity = new Bundle();
                    paramAnonymous3DialogsActivity.putBoolean("scrollToTopOnResume", true);
                    paramAnonymous3DialogsActivity.putInt("chat_id", -(int)paramAnonymous3Long);
                    if ((LaunchActivity.mainFragmentsStack.isEmpty()) || (MessagesController.checkCanOpenChat(paramAnonymous3DialogsActivity, (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))))
                    {
                      NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                      MessagesController.getInstance().addUserToChat(-(int)paramAnonymous3Long, localException2, null, 0, LaunchActivity.7.this.val$botChat, null);
                      LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(paramAnonymous3DialogsActivity), true, false, true);
                    }
                  }
                });
                LaunchActivity.this.presentFragment((BaseFragment)localObject4);
                return;
                int j = 0;
                localObject4 = new Bundle();
                long l;
                int i;
                if (!localException2.chats.isEmpty())
                {
                  ((Bundle)localObject4).putInt("chat_id", ((TLRPC.Chat)localException2.chats.get(0)).id);
                  l = -((TLRPC.Chat)localException2.chats.get(0)).id;
                  i = j;
                  if (LaunchActivity.7.this.val$botUser != null)
                  {
                    i = j;
                    if (localException2.users.size() > 0)
                    {
                      i = j;
                      if (((TLRPC.User)localException2.users.get(0)).bot)
                      {
                        ((Bundle)localObject4).putString("botUser", LaunchActivity.7.this.val$botUser);
                        i = 1;
                      }
                    }
                  }
                  if (LaunchActivity.7.this.val$messageId != null) {
                    ((Bundle)localObject4).putInt("message_id", LaunchActivity.7.this.val$messageId.intValue());
                  }
                  if (LaunchActivity.mainFragmentsStack.isEmpty()) {
                    break label576;
                  }
                }
                for (localObject3 = (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1);; localObject3 = null)
                {
                  if ((localObject3 != null) && (!MessagesController.checkCanOpenChat((Bundle)localObject4, (BaseFragment)localObject3))) {
                    break label580;
                  }
                  if ((i == 0) || (localObject3 == null) || (!(localObject3 instanceof ChatActivity)) || (((ChatActivity)localObject3).getDialogId() != l)) {
                    break label582;
                  }
                  ((ChatActivity)localObject3).setBotUser(LaunchActivity.7.this.val$botUser);
                  return;
                  ((Bundle)localObject4).putInt("user_id", ((TLRPC.User)((TLRPC.TL_contacts_resolvedPeer)localObject3).users.get(0)).id);
                  l = ((TLRPC.User)((TLRPC.TL_contacts_resolvedPeer)localObject3).users.get(0)).id;
                  break;
                }
              }
              label582:
              Object localObject3 = new ChatActivity((Bundle)localObject4);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
              LaunchActivity.this.actionBarLayout.presentFragment((BaseFragment)localObject3, false, true, true);
              return;
              try
              {
                label626:
                Toast.makeText(LaunchActivity.this, LocaleController.getString("NoUsernameFound", 2131166029), 0).show();
                return;
              }
              catch (Exception localException3)
              {
                FileLog.e("tmessages", localException3);
              }
            }
          });
        }
      });
    }
    for (;;)
    {
      if (i != 0)
      {
        localProgressDialog.setButton(-2, LocaleController.getString("Cancel", 2131165426), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            ConnectionsManager.getInstance().cancelRequest(i, true);
            try
            {
              paramAnonymousDialogInterface.dismiss();
              return;
            }
            catch (Exception paramAnonymousDialogInterface)
            {
              FileLog.e("tmessages", paramAnonymousDialogInterface);
            }
          }
        });
        localProgressDialog.show();
      }
      do
      {
        return;
        if (paramString2 != null)
        {
          if (paramInt == 0)
          {
            TLRPC.TL_messages_checkChatInvite localTL_messages_checkChatInvite = new TLRPC.TL_messages_checkChatInvite();
            localTL_messages_checkChatInvite.hash = paramString2;
            i = ConnectionsManager.getInstance().sendRequest(localTL_messages_checkChatInvite, new RequestDelegate()
            {
              public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
              {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    if (!LaunchActivity.this.isFinishing()) {}
                    Object localObject2;
                    try
                    {
                      LaunchActivity.8.this.val$progressDialog.dismiss();
                      if ((paramAnonymousTL_error == null) && (LaunchActivity.this.actionBarLayout != null))
                      {
                        Object localObject1 = (TLRPC.ChatInvite)paramAnonymousTLObject;
                        if ((((TLRPC.ChatInvite)localObject1).chat != null) && (!ChatObject.isLeftFromChat(((TLRPC.ChatInvite)localObject1).chat)))
                        {
                          MessagesController.getInstance().putChat(((TLRPC.ChatInvite)localObject1).chat, false);
                          localObject3 = new ArrayList();
                          ((ArrayList)localObject3).add(((TLRPC.ChatInvite)localObject1).chat);
                          MessagesStorage.getInstance().putUsersAndChats(null, (ArrayList)localObject3, false, true);
                          localObject3 = new Bundle();
                          ((Bundle)localObject3).putInt("chat_id", ((TLRPC.ChatInvite)localObject1).chat.id);
                          if ((LaunchActivity.mainFragmentsStack.isEmpty()) || (MessagesController.checkCanOpenChat((Bundle)localObject3, (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))))
                          {
                            localObject1 = new ChatActivity((Bundle)localObject3);
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            LaunchActivity.this.actionBarLayout.presentFragment((BaseFragment)localObject1, false, true, true);
                          }
                          return;
                        }
                      }
                    }
                    catch (Exception localException)
                    {
                      for (;;)
                      {
                        FileLog.e("tmessages", localException);
                      }
                      Object localObject3 = new AlertDialog.Builder(LaunchActivity.this);
                      ((AlertDialog.Builder)localObject3).setTitle(LocaleController.getString("AppName", 2131165338));
                      if (((!localException.megagroup) && (localException.channel)) || ((ChatObject.isChannel(localException.chat)) && (!localException.chat.megagroup)))
                      {
                        if (localException.chat != null) {}
                        for (localObject2 = localException.chat.title;; localObject2 = ((TLRPC.ChatInvite)localObject2).title)
                        {
                          ((AlertDialog.Builder)localObject3).setMessage(LocaleController.formatString("ChannelJoinTo", 2131165470, new Object[] { localObject2 }));
                          ((AlertDialog.Builder)localObject3).setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
                          {
                            public void onClick(DialogInterface paramAnonymous3DialogInterface, int paramAnonymous3Int)
                            {
                              LaunchActivity.this.runLinkRequest(LaunchActivity.8.this.val$username, LaunchActivity.8.this.val$group, LaunchActivity.8.this.val$sticker, LaunchActivity.8.this.val$botUser, LaunchActivity.8.this.val$botChat, LaunchActivity.8.this.val$message, LaunchActivity.8.this.val$hasUrl, LaunchActivity.8.this.val$messageId, 1);
                            }
                          });
                          ((AlertDialog.Builder)localObject3).setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
                          LaunchActivity.this.showAlertDialog((AlertDialog.Builder)localObject3);
                          return;
                        }
                      }
                      if (((TLRPC.ChatInvite)localObject2).chat != null) {}
                      for (localObject2 = ((TLRPC.ChatInvite)localObject2).chat.title;; localObject2 = ((TLRPC.ChatInvite)localObject2).title)
                      {
                        ((AlertDialog.Builder)localObject3).setMessage(LocaleController.formatString("JoinToGroup", 2131165844, new Object[] { localObject2 }));
                        break;
                      }
                      localObject2 = new AlertDialog.Builder(LaunchActivity.this);
                      ((AlertDialog.Builder)localObject2).setTitle(LocaleController.getString("AppName", 2131165338));
                      if (!paramAnonymousTL_error.text.startsWith("FLOOD_WAIT")) {
                        break label487;
                      }
                    }
                    ((AlertDialog.Builder)localObject2).setMessage(LocaleController.getString("FloodWait", 2131165691));
                    for (;;)
                    {
                      ((AlertDialog.Builder)localObject2).setPositiveButton(LocaleController.getString("OK", 2131166111), null);
                      LaunchActivity.this.showAlertDialog((AlertDialog.Builder)localObject2);
                      return;
                      label487:
                      ((AlertDialog.Builder)localObject2).setMessage(LocaleController.getString("JoinToGroupErrorNotExist", 2131165846));
                    }
                  }
                });
              }
            }, 2);
            break;
          }
          i = j;
          if (paramInt != 1) {
            break;
          }
          paramString1 = new TLRPC.TL_messages_importChatInvite();
          paramString1.hash = paramString2;
          ConnectionsManager.getInstance().sendRequest(paramString1, new RequestDelegate()
          {
            public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
            {
              if (paramAnonymousTL_error == null)
              {
                TLRPC.Updates localUpdates = (TLRPC.Updates)paramAnonymousTLObject;
                MessagesController.getInstance().processUpdates(localUpdates, false);
              }
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if (!LaunchActivity.this.isFinishing()) {}
                  AlertDialog.Builder localBuilder;
                  try
                  {
                    LaunchActivity.9.this.val$progressDialog.dismiss();
                    if (paramAnonymousTL_error == null)
                    {
                      if (LaunchActivity.this.actionBarLayout != null)
                      {
                        Object localObject2 = (TLRPC.Updates)paramAnonymousTLObject;
                        if (!((TLRPC.Updates)localObject2).chats.isEmpty())
                        {
                          Object localObject1 = (TLRPC.Chat)((TLRPC.Updates)localObject2).chats.get(0);
                          ((TLRPC.Chat)localObject1).left = false;
                          ((TLRPC.Chat)localObject1).kicked = false;
                          MessagesController.getInstance().putUsers(((TLRPC.Updates)localObject2).users, false);
                          MessagesController.getInstance().putChats(((TLRPC.Updates)localObject2).chats, false);
                          localObject2 = new Bundle();
                          ((Bundle)localObject2).putInt("chat_id", ((TLRPC.Chat)localObject1).id);
                          if ((LaunchActivity.mainFragmentsStack.isEmpty()) || (MessagesController.checkCanOpenChat((Bundle)localObject2, (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))))
                          {
                            localObject1 = new ChatActivity((Bundle)localObject2);
                            if (!ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getBoolean("keep_chat_open", true)) {
                              NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            }
                            LaunchActivity.this.actionBarLayout.presentFragment((BaseFragment)localObject1, false, true, true);
                          }
                        }
                      }
                      return;
                    }
                  }
                  catch (Exception localException)
                  {
                    for (;;)
                    {
                      FileLog.e("tmessages", localException);
                    }
                    localBuilder = new AlertDialog.Builder(LaunchActivity.this);
                    localBuilder.setTitle(LocaleController.getString("AppName", 2131165338));
                    if (!paramAnonymousTL_error.text.startsWith("FLOOD_WAIT")) {
                      break label307;
                    }
                  }
                  localBuilder.setMessage(LocaleController.getString("FloodWait", 2131165691));
                  for (;;)
                  {
                    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166111), null);
                    LaunchActivity.this.showAlertDialog(localBuilder);
                    return;
                    label307:
                    if (paramAnonymousTL_error.text.equals("USERS_TOO_MUCH")) {
                      localBuilder.setMessage(LocaleController.getString("JoinToGroupErrorFull", 2131165845));
                    } else {
                      localBuilder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", 2131165846));
                    }
                  }
                }
              });
            }
          }, 2);
          i = j;
          break;
        }
        if (paramString3 == null) {
          break label287;
        }
      } while (mainFragmentsStack.isEmpty());
      paramString1 = new TLRPC.TL_inputStickerSetShortName();
      paramString1.short_name = paramString3;
      ((BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(new StickersAlert(this, paramString1, null, null));
      return;
      label287:
      i = j;
      if (paramString6 != null)
      {
        paramString1 = new Bundle();
        paramString1.putBoolean("onlySelect", true);
        paramString1 = new DialogsActivity(paramString1);
        paramString1.setDelegate(new DialogsActivity.DialogsActivityDelegate()
        {
          public void didSelectDialog(DialogsActivity paramAnonymousDialogsActivity, long paramAnonymousLong, boolean paramAnonymousBoolean)
          {
            Bundle localBundle = new Bundle();
            localBundle.putBoolean("scrollToTopOnResume", true);
            localBundle.putBoolean("hasUrl", paramBoolean);
            int i = (int)paramAnonymousLong;
            int j = (int)(paramAnonymousLong >> 32);
            if (i != 0) {
              if (j == 1) {
                localBundle.putInt("chat_id", i);
              }
            }
            for (;;)
            {
              if (MessagesController.checkCanOpenChat(localBundle, paramAnonymousDialogsActivity))
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                DraftQuery.saveDraft(paramAnonymousLong, paramString6, null, null, true);
                LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(localBundle), true, false, true);
              }
              return;
              if (i > 0)
              {
                localBundle.putInt("user_id", i);
              }
              else if (i < 0)
              {
                localBundle.putInt("chat_id", -i);
                continue;
                localBundle.putInt("enc_id", j);
              }
            }
          }
        });
        presentFragment(paramString1, false, true);
        i = j;
      }
    }
  }
  
  private void showPasscodeActivity()
  {
    if (this.passcodeView == null) {
      return;
    }
    UserConfig.appLocked = true;
    if (PhotoViewer.getInstance().isVisible()) {
      PhotoViewer.getInstance().closePhoto(false, true);
    }
    this.passcodeView.onShow();
    UserConfig.isWaitingForPasscodeEnter = true;
    this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
    this.passcodeView.setDelegate(new PasscodeView.PasscodeViewDelegate()
    {
      public void didAcceptedPassword()
      {
        UserConfig.isWaitingForPasscodeEnter = false;
        if (LaunchActivity.this.passcodeSaveIntent != null)
        {
          LaunchActivity.this.handleIntent(LaunchActivity.this.passcodeSaveIntent, LaunchActivity.this.passcodeSaveIntentIsNew, LaunchActivity.this.passcodeSaveIntentIsRestore, true);
          LaunchActivity.access$202(LaunchActivity.this, null);
        }
        LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        LaunchActivity.this.actionBarLayout.showLastFragment();
        if (AndroidUtilities.isTablet())
        {
          LaunchActivity.this.layersActionBarLayout.showLastFragment();
          LaunchActivity.this.rightActionBarLayout.showLastFragment();
        }
      }
    });
  }
  
  private void updateCurrentConnectionState()
  {
    String str = null;
    if (this.currentConnectionState == 2) {
      str = LocaleController.getString("WaitingForNetwork", 2131166461);
    }
    for (;;)
    {
      this.actionBarLayout.setTitleOverlayText(str);
      return;
      if (this.currentConnectionState == 1) {
        str = LocaleController.getString("Connecting", 2131165559);
      } else if (this.currentConnectionState == 4) {
        str = LocaleController.getString("Updating", 2131166430);
      }
    }
  }
  
  public void didReceivedNotification(int paramInt, final Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.appDidLogout)
    {
      if (this.drawerLayoutAdapter != null) {
        this.drawerLayoutAdapter.notifyDataSetChanged();
      }
      paramVarArgs = this.actionBarLayout.fragmentsStack.iterator();
      while (paramVarArgs.hasNext()) {
        ((BaseFragment)paramVarArgs.next()).onFragmentDestroy();
      }
      this.actionBarLayout.fragmentsStack.clear();
      if (AndroidUtilities.isTablet())
      {
        paramVarArgs = this.layersActionBarLayout.fragmentsStack.iterator();
        while (paramVarArgs.hasNext()) {
          ((BaseFragment)paramVarArgs.next()).onFragmentDestroy();
        }
        this.layersActionBarLayout.fragmentsStack.clear();
        paramVarArgs = this.rightActionBarLayout.fragmentsStack.iterator();
        while (paramVarArgs.hasNext()) {
          ((BaseFragment)paramVarArgs.next()).onFragmentDestroy();
        }
        this.rightActionBarLayout.fragmentsStack.clear();
      }
      startActivity(new Intent(this, IntroActivity.class));
      onFinish();
      finish();
    }
    Object localObject;
    do
    {
      do
      {
        for (;;)
        {
          return;
          if (paramInt == NotificationCenter.closeOtherAppActivities)
          {
            if (paramVarArgs[0] != this)
            {
              onFinish();
              finish();
            }
          }
          else if (paramInt == NotificationCenter.didUpdatedConnectionState)
          {
            paramInt = ConnectionsManager.getInstance().getConnectionState();
            if (this.currentConnectionState != paramInt)
            {
              FileLog.d("tmessages", "switch to state " + paramInt);
              this.currentConnectionState = paramInt;
              updateCurrentConnectionState();
            }
          }
          else
          {
            if (paramInt == NotificationCenter.mainUserInfoChanged)
            {
              this.drawerLayoutAdapter.notifyDataSetChanged();
              return;
            }
            if (paramInt != NotificationCenter.needShowAlert) {
              break;
            }
            localObject = (Integer)paramVarArgs[0];
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
            localBuilder.setTitle(LocaleController.getString("AppName", 2131165338));
            localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166111), null);
            if (((Integer)localObject).intValue() != 2) {
              localBuilder.setNegativeButton(LocaleController.getString("MoreInfo", 2131165971), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                {
                  if (!LaunchActivity.mainFragmentsStack.isEmpty()) {
                    MessagesController.openByUserName("spambot", (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1), 1);
                  }
                }
              });
            }
            if (((Integer)localObject).intValue() == 0) {
              localBuilder.setMessage(LocaleController.getString("NobodyLikesSpam1", 2131166031));
            }
            while (!mainFragmentsStack.isEmpty())
            {
              ((BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(localBuilder.create());
              return;
              if (((Integer)localObject).intValue() == 1) {
                localBuilder.setMessage(LocaleController.getString("NobodyLikesSpam2", 2131166032));
              } else if (((Integer)localObject).intValue() == 2) {
                localBuilder.setMessage((String)paramVarArgs[1]);
              }
            }
          }
        }
      } while (paramInt != NotificationCenter.wasUnableToFindCurrentLocation);
      paramVarArgs = (HashMap)paramVarArgs[0];
      localObject = new AlertDialog.Builder(this);
      ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165338));
      ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166111), null);
      ((AlertDialog.Builder)localObject).setNegativeButton(LocaleController.getString("ShareYouLocationUnableManually", 2131166331), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if (LaunchActivity.mainFragmentsStack.isEmpty()) {}
          while (!AndroidUtilities.isGoogleMapsInstalled((BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))) {
            return;
          }
          paramAnonymousDialogInterface = new LocationActivity();
          paramAnonymousDialogInterface.setDelegate(new LocationActivity.LocationActivityDelegate()
          {
            public void didSelectLocation(TLRPC.MessageMedia paramAnonymous2MessageMedia)
            {
              Iterator localIterator = LaunchActivity.16.this.val$waitingForLocation.entrySet().iterator();
              while (localIterator.hasNext())
              {
                MessageObject localMessageObject = (MessageObject)((Map.Entry)localIterator.next()).getValue();
                SendMessagesHelper.getInstance().sendMessage(paramAnonymous2MessageMedia, localMessageObject.getDialogId(), localMessageObject, null, null);
              }
            }
          });
          LaunchActivity.this.presentFragment(paramAnonymousDialogInterface);
        }
      });
      ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("ShareYouLocationUnable", 2131166330));
    } while (mainFragmentsStack.isEmpty());
    ((BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(((AlertDialog.Builder)localObject).create());
  }
  
  public void didSelectDialog(DialogsActivity paramDialogsActivity, long paramLong, boolean paramBoolean)
  {
    int i;
    int j;
    if (paramLong != 0L)
    {
      i = (int)paramLong;
      j = (int)(paramLong >> 32);
      localObject1 = new Bundle();
      ((Bundle)localObject1).putBoolean("scrollToTopOnResume", true);
      if (!AndroidUtilities.isTablet()) {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
      }
      if (i == 0) {
        break label122;
      }
      if (j != 1) {
        break label85;
      }
      ((Bundle)localObject1).putInt("chat_id", i);
    }
    while (!MessagesController.checkCanOpenChat((Bundle)localObject1, paramDialogsActivity))
    {
      return;
      label85:
      if (i > 0)
      {
        ((Bundle)localObject1).putInt("user_id", i);
      }
      else if (i < 0)
      {
        ((Bundle)localObject1).putInt("chat_id", -i);
        continue;
        label122:
        ((Bundle)localObject1).putInt("enc_id", j);
      }
    }
    Object localObject1 = new ChatActivity((Bundle)localObject1);
    if (this.videoPath != null)
    {
      if (Build.VERSION.SDK_INT >= 16)
      {
        if (AndroidUtilities.isTablet())
        {
          this.actionBarLayout.presentFragment((BaseFragment)localObject1, false, true, true);
          localObject2 = this.videoPath;
          if (paramDialogsActivity == null) {
            break label277;
          }
        }
        label277:
        for (paramBoolean = true;; paramBoolean = false)
        {
          if ((!((ChatActivity)localObject1).openVideoEditor((String)localObject2, paramBoolean, false)) && (paramDialogsActivity != null) && (!AndroidUtilities.isTablet())) {
            paramDialogsActivity.finishFragment(true);
          }
          this.photoPathsArray = null;
          this.videoPath = null;
          this.sendingText = null;
          this.documentsPathsArray = null;
          this.documentsOriginalPathsArray = null;
          this.contactsToSend = null;
          return;
          this.actionBarLayout.addFragmentToStack((BaseFragment)localObject1, this.actionBarLayout.fragmentsStack.size() - 1);
          break;
        }
      }
      localObject2 = this.actionBarLayout;
      if (paramDialogsActivity != null)
      {
        paramBoolean = true;
        label296:
        if (paramDialogsActivity != null) {
          break label339;
        }
      }
      label339:
      for (bool = true;; bool = false)
      {
        ((ActionBarLayout)localObject2).presentFragment((BaseFragment)localObject1, paramBoolean, bool, true);
        SendMessagesHelper.prepareSendingVideo(this.videoPath, 0L, 0L, 0, 0, null, paramLong, null);
        break;
        paramBoolean = false;
        break label296;
      }
    }
    Object localObject2 = this.actionBarLayout;
    if (paramDialogsActivity != null)
    {
      paramBoolean = true;
      label358:
      if (paramDialogsActivity != null) {
        break label561;
      }
    }
    label561:
    for (boolean bool = true;; bool = false)
    {
      ((ActionBarLayout)localObject2).presentFragment((BaseFragment)localObject1, paramBoolean, bool, true);
      if (this.photoPathsArray != null)
      {
        localObject1 = null;
        paramDialogsActivity = (DialogsActivity)localObject1;
        if (this.sendingText != null)
        {
          paramDialogsActivity = (DialogsActivity)localObject1;
          if (this.photoPathsArray.size() == 1)
          {
            paramDialogsActivity = new ArrayList();
            paramDialogsActivity.add(this.sendingText);
            this.sendingText = null;
          }
        }
        SendMessagesHelper.prepareSendingPhotos(null, this.photoPathsArray, paramLong, null, paramDialogsActivity);
      }
      if (this.sendingText != null) {
        SendMessagesHelper.prepareSendingText(this.sendingText, paramLong);
      }
      if ((this.documentsPathsArray != null) || (this.documentsUrisArray != null)) {
        SendMessagesHelper.prepareSendingDocuments(this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, this.documentsMimeType, paramLong, null);
      }
      if ((this.contactsToSend == null) || (this.contactsToSend.isEmpty())) {
        break;
      }
      paramDialogsActivity = this.contactsToSend.iterator();
      while (paramDialogsActivity.hasNext())
      {
        localObject1 = (TLRPC.User)paramDialogsActivity.next();
        SendMessagesHelper.getInstance().sendMessage((TLRPC.User)localObject1, paramLong, null, null, null);
      }
      break;
      paramBoolean = false;
      break label358;
    }
  }
  
  public void fixLayout()
  {
    if (!AndroidUtilities.isTablet()) {}
    while (this.actionBarLayout == null) {
      return;
    }
    this.actionBarLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
    {
      public void onGlobalLayout()
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            LaunchActivity.this.needLayout();
          }
        });
        if (LaunchActivity.this.actionBarLayout != null)
        {
          if (Build.VERSION.SDK_INT < 16) {
            LaunchActivity.this.actionBarLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
          }
        }
        else {
          return;
        }
        LaunchActivity.this.actionBarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
      }
    });
  }
  
  public boolean needAddFragmentToStack(BaseFragment paramBaseFragment, ActionBarLayout paramActionBarLayout)
  {
    if (AndroidUtilities.isTablet())
    {
      DrawerLayoutContainer localDrawerLayoutContainer = this.drawerLayoutContainer;
      if ((!(paramBaseFragment instanceof LoginActivity)) && (!(paramBaseFragment instanceof CountrySelectActivity)) && (this.layersActionBarLayout.getVisibility() != 0))
      {
        bool = true;
        localDrawerLayoutContainer.setAllowOpenDrawer(bool, true);
        if (!(paramBaseFragment instanceof DialogsActivity)) {
          break label157;
        }
        if ((!((DialogsActivity)paramBaseFragment).isMainDialogList()) || (paramActionBarLayout == this.actionBarLayout)) {
          break label457;
        }
        this.actionBarLayout.removeAllFragments();
        this.actionBarLayout.addFragmentToStack(paramBaseFragment);
        this.layersActionBarLayout.removeAllFragments();
        this.layersActionBarLayout.setVisibility(8);
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        if (!this.tabletFullSize)
        {
          this.shadowTabletSide.setVisibility(0);
          if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
            this.backgroundTablet.setVisibility(0);
          }
        }
      }
      label157:
      label282:
      do
      {
        do
        {
          return false;
          bool = false;
          break;
          if (!(paramBaseFragment instanceof ChatActivity)) {
            break label376;
          }
          if ((this.tabletFullSize) || (paramActionBarLayout == this.rightActionBarLayout)) {
            break label282;
          }
          this.rightActionBarLayout.setVisibility(0);
          this.backgroundTablet.setVisibility(8);
          this.rightActionBarLayout.removeAllFragments();
          this.rightActionBarLayout.addFragmentToStack(paramBaseFragment);
        } while (this.layersActionBarLayout.fragmentsStack.isEmpty());
        for (i = 0; this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
          this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
        }
        this.layersActionBarLayout.closeLastFragment(true);
        return false;
        if ((!this.tabletFullSize) || (paramActionBarLayout == this.actionBarLayout)) {
          break label457;
        }
        this.actionBarLayout.addFragmentToStack(paramBaseFragment);
      } while (this.layersActionBarLayout.fragmentsStack.isEmpty());
      for (int i = 0; this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
      }
      this.layersActionBarLayout.closeLastFragment(true);
      return false;
      label376:
      if (paramActionBarLayout != this.layersActionBarLayout)
      {
        this.layersActionBarLayout.setVisibility(0);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
        if ((paramBaseFragment instanceof LoginActivity))
        {
          this.backgroundTablet.setVisibility(0);
          this.shadowTabletSide.setVisibility(8);
          this.shadowTablet.setBackgroundColor(0);
        }
        for (;;)
        {
          this.layersActionBarLayout.addFragmentToStack(paramBaseFragment);
          return false;
          this.shadowTablet.setBackgroundColor(2130706432);
        }
      }
      label457:
      return true;
    }
    paramActionBarLayout = this.drawerLayoutContainer;
    if ((!(paramBaseFragment instanceof LoginActivity)) && (!(paramBaseFragment instanceof CountrySelectActivity))) {}
    for (boolean bool = true;; bool = false)
    {
      paramActionBarLayout.setAllowOpenDrawer(bool, false);
      return true;
    }
  }
  
  public boolean needCloseLastFragment(ActionBarLayout paramActionBarLayout)
  {
    if (AndroidUtilities.isTablet())
    {
      if ((paramActionBarLayout == this.actionBarLayout) && (paramActionBarLayout.fragmentsStack.size() <= 1))
      {
        onFinish();
        finish();
        return false;
      }
      if (paramActionBarLayout == this.rightActionBarLayout) {
        if (!this.tabletFullSize) {
          this.backgroundTablet.setVisibility(0);
        }
      }
    }
    while (paramActionBarLayout.fragmentsStack.size() > 1)
    {
      do
      {
        return true;
      } while ((paramActionBarLayout != this.layersActionBarLayout) || (!this.actionBarLayout.fragmentsStack.isEmpty()) || (this.layersActionBarLayout.fragmentsStack.size() != 1));
      onFinish();
      finish();
      return false;
    }
    onFinish();
    finish();
    return false;
  }
  
  public void needLayout()
  {
    int k = 8;
    int j = 0;
    Object localObject;
    int i;
    if (AndroidUtilities.isTablet())
    {
      localObject = (RelativeLayout.LayoutParams)this.layersActionBarLayout.getLayoutParams();
      ((RelativeLayout.LayoutParams)localObject).leftMargin = ((AndroidUtilities.displaySize.x - ((RelativeLayout.LayoutParams)localObject).width) / 2);
      if (Build.VERSION.SDK_INT >= 21) {}
      for (i = AndroidUtilities.statusBarHeight;; i = 0)
      {
        ((RelativeLayout.LayoutParams)localObject).topMargin = ((AndroidUtilities.displaySize.y - ((RelativeLayout.LayoutParams)localObject).height - i) / 2 + i);
        this.layersActionBarLayout.setLayoutParams((ViewGroup.LayoutParams)localObject);
        if ((AndroidUtilities.isSmallTablet()) && (getResources().getConfiguration().orientation != 2)) {
          break label469;
        }
        this.tabletFullSize = false;
        k = AndroidUtilities.displaySize.x / 100 * 35;
        i = k;
        if (k < AndroidUtilities.dp(320.0F)) {
          i = AndroidUtilities.dp(320.0F);
        }
        localObject = (RelativeLayout.LayoutParams)this.actionBarLayout.getLayoutParams();
        ((RelativeLayout.LayoutParams)localObject).width = i;
        ((RelativeLayout.LayoutParams)localObject).height = -1;
        this.actionBarLayout.setLayoutParams((ViewGroup.LayoutParams)localObject);
        localObject = (RelativeLayout.LayoutParams)this.shadowTabletSide.getLayoutParams();
        ((RelativeLayout.LayoutParams)localObject).leftMargin = i;
        this.shadowTabletSide.setLayoutParams((ViewGroup.LayoutParams)localObject);
        localObject = (RelativeLayout.LayoutParams)this.rightActionBarLayout.getLayoutParams();
        ((RelativeLayout.LayoutParams)localObject).width = (AndroidUtilities.displaySize.x - i);
        ((RelativeLayout.LayoutParams)localObject).height = -1;
        ((RelativeLayout.LayoutParams)localObject).leftMargin = i;
        this.rightActionBarLayout.setLayoutParams((ViewGroup.LayoutParams)localObject);
        if ((!AndroidUtilities.isSmallTablet()) || (this.actionBarLayout.fragmentsStack.size() < 2)) {
          break label369;
        }
        for (i = 1; i < this.actionBarLayout.fragmentsStack.size(); i = i - 1 + 1)
        {
          localObject = (BaseFragment)this.actionBarLayout.fragmentsStack.get(i);
          ((BaseFragment)localObject).onPause();
          this.actionBarLayout.fragmentsStack.remove(i);
          this.rightActionBarLayout.fragmentsStack.add(localObject);
        }
      }
      if (this.passcodeView.getVisibility() != 0)
      {
        this.actionBarLayout.showLastFragment();
        this.rightActionBarLayout.showLastFragment();
      }
      label369:
      localObject = this.rightActionBarLayout;
      if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
        break label452;
      }
      i = 8;
      ((ActionBarLayout)localObject).setVisibility(i);
      localObject = this.backgroundTablet;
      if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
        break label457;
      }
      i = 0;
      label418:
      ((ImageView)localObject).setVisibility(i);
      localObject = this.shadowTabletSide;
      if (this.actionBarLayout.fragmentsStack.isEmpty()) {
        break label463;
      }
      i = j;
      label445:
      ((FrameLayout)localObject).setVisibility(i);
    }
    label452:
    label457:
    label463:
    label469:
    do
    {
      return;
      i = 0;
      break;
      i = 8;
      break label418;
      i = 8;
      break label445;
      this.tabletFullSize = true;
      localObject = (RelativeLayout.LayoutParams)this.actionBarLayout.getLayoutParams();
      ((RelativeLayout.LayoutParams)localObject).width = -1;
      ((RelativeLayout.LayoutParams)localObject).height = -1;
      this.actionBarLayout.setLayoutParams((ViewGroup.LayoutParams)localObject);
      this.shadowTabletSide.setVisibility(8);
      this.rightActionBarLayout.setVisibility(8);
      localObject = this.backgroundTablet;
      if (!this.actionBarLayout.fragmentsStack.isEmpty()) {}
      for (i = k;; i = 0)
      {
        ((ImageView)localObject).setVisibility(i);
        if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
          break;
        }
        for (i = 0; this.rightActionBarLayout.fragmentsStack.size() > 0; i = i - 1 + 1)
        {
          localObject = (BaseFragment)this.rightActionBarLayout.fragmentsStack.get(i);
          ((BaseFragment)localObject).onPause();
          this.rightActionBarLayout.fragmentsStack.remove(i);
          this.actionBarLayout.fragmentsStack.add(localObject);
        }
      }
    } while (this.passcodeView.getVisibility() == 0);
    this.actionBarLayout.showLastFragment();
  }
  
  public boolean needPresentFragment(BaseFragment paramBaseFragment, boolean paramBoolean1, boolean paramBoolean2, ActionBarLayout paramActionBarLayout)
  {
    boolean bool5 = true;
    boolean bool3 = true;
    boolean bool4 = true;
    boolean bool2 = true;
    if (AndroidUtilities.isTablet())
    {
      DrawerLayoutContainer localDrawerLayoutContainer = this.drawerLayoutContainer;
      boolean bool1;
      if ((!(paramBaseFragment instanceof LoginActivity)) && (!(paramBaseFragment instanceof CountrySelectActivity)) && (this.layersActionBarLayout.getVisibility() != 0))
      {
        bool1 = true;
        localDrawerLayoutContainer.setAllowOpenDrawer(bool1, true);
        if ((!(paramBaseFragment instanceof DialogsActivity)) || (!((DialogsActivity)paramBaseFragment).isMainDialogList()) || (paramActionBarLayout == this.actionBarLayout)) {
          break label173;
        }
        this.actionBarLayout.removeAllFragments();
        this.actionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, false);
        this.layersActionBarLayout.removeAllFragments();
        this.layersActionBarLayout.setVisibility(8);
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        if (!this.tabletFullSize)
        {
          this.shadowTabletSide.setVisibility(0);
          if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
            this.backgroundTablet.setVisibility(0);
          }
        }
      }
      label173:
      label351:
      do
      {
        return false;
        bool1 = false;
        break;
        if (!(paramBaseFragment instanceof ChatActivity)) {
          break label762;
        }
        if (((!this.tabletFullSize) && (paramActionBarLayout == this.rightActionBarLayout)) || ((this.tabletFullSize) && (paramActionBarLayout == this.actionBarLayout)))
        {
          if ((!this.tabletFullSize) || (paramActionBarLayout != this.actionBarLayout) || (this.actionBarLayout.fragmentsStack.size() != 1)) {
            paramBoolean1 = true;
          }
          while (!this.layersActionBarLayout.fragmentsStack.isEmpty())
          {
            i = 0;
            for (;;)
            {
              if (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0)
              {
                this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
                i = i - 1 + 1;
                continue;
                paramBoolean1 = false;
                break;
              }
            }
            paramActionBarLayout = this.layersActionBarLayout;
            if (paramBoolean2) {
              break label351;
            }
          }
          for (bool1 = bool2;; bool1 = false)
          {
            paramActionBarLayout.closeLastFragment(bool1);
            if (!paramBoolean1) {
              this.actionBarLayout.presentFragment(paramBaseFragment, false, paramBoolean2, false);
            }
            return paramBoolean1;
          }
        }
        if ((this.tabletFullSize) || (paramActionBarLayout == this.rightActionBarLayout)) {
          break label496;
        }
        this.rightActionBarLayout.setVisibility(0);
        this.backgroundTablet.setVisibility(8);
        this.rightActionBarLayout.removeAllFragments();
        this.rightActionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, true, false);
      } while (this.layersActionBarLayout.fragmentsStack.isEmpty());
      for (int i = 0; this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
      }
      paramBaseFragment = this.layersActionBarLayout;
      if (!paramBoolean2) {}
      for (paramBoolean1 = bool5;; paramBoolean1 = false)
      {
        paramBaseFragment.closeLastFragment(paramBoolean1);
        return false;
      }
      label496:
      if ((this.tabletFullSize) && (paramActionBarLayout != this.actionBarLayout))
      {
        paramActionBarLayout = this.actionBarLayout;
        if (this.actionBarLayout.fragmentsStack.size() > 1) {}
        for (paramBoolean1 = true;; paramBoolean1 = false)
        {
          paramActionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, false);
          if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
            break;
          }
          for (i = 0; this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
          }
        }
        paramBaseFragment = this.layersActionBarLayout;
        if (!paramBoolean2) {}
        for (paramBoolean1 = bool3;; paramBoolean1 = false)
        {
          paramBaseFragment.closeLastFragment(paramBoolean1);
          return false;
        }
      }
      if (!this.layersActionBarLayout.fragmentsStack.isEmpty())
      {
        for (i = 0; this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
          this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
        }
        paramActionBarLayout = this.layersActionBarLayout;
        if (!paramBoolean2)
        {
          paramBoolean1 = true;
          paramActionBarLayout.closeLastFragment(paramBoolean1);
        }
      }
      else
      {
        paramActionBarLayout = this.actionBarLayout;
        if (this.actionBarLayout.fragmentsStack.size() <= 1) {
          break label757;
        }
      }
      label757:
      for (paramBoolean1 = bool4;; paramBoolean1 = false)
      {
        paramActionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, false);
        return false;
        paramBoolean1 = false;
        break;
      }
      label762:
      if (paramActionBarLayout != this.layersActionBarLayout)
      {
        this.layersActionBarLayout.setVisibility(0);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
        if ((paramBaseFragment instanceof LoginActivity))
        {
          this.backgroundTablet.setVisibility(0);
          this.shadowTabletSide.setVisibility(8);
          this.shadowTablet.setBackgroundColor(0);
        }
        for (;;)
        {
          this.layersActionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, false);
          return false;
          this.shadowTablet.setBackgroundColor(2130706432);
        }
      }
      return true;
    }
    paramActionBarLayout = this.drawerLayoutContainer;
    if ((!(paramBaseFragment instanceof LoginActivity)) && (!(paramBaseFragment instanceof CountrySelectActivity))) {}
    for (paramBoolean1 = true;; paramBoolean1 = false)
    {
      paramActionBarLayout.setAllowOpenDrawer(paramBoolean1, false);
      return true;
    }
  }
  
  public void onActionModeFinished(ActionMode paramActionMode)
  {
    super.onActionModeFinished(paramActionMode);
    if ((Build.VERSION.SDK_INT >= 23) && (paramActionMode.getType() == 1)) {}
    do
    {
      return;
      this.actionBarLayout.onActionModeFinished(paramActionMode);
    } while (!AndroidUtilities.isTablet());
    this.rightActionBarLayout.onActionModeFinished(paramActionMode);
    this.layersActionBarLayout.onActionModeFinished(paramActionMode);
  }
  
  public void onActionModeStarted(ActionMode paramActionMode)
  {
    super.onActionModeStarted(paramActionMode);
    if ((Build.VERSION.SDK_INT >= 23) && (paramActionMode.getType() == 1)) {}
    do
    {
      return;
      this.actionBarLayout.onActionModeStarted(paramActionMode);
    } while (!AndroidUtilities.isTablet());
    this.rightActionBarLayout.onActionModeStarted(paramActionMode);
    this.layersActionBarLayout.onActionModeStarted(paramActionMode);
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if ((UserConfig.passcodeHash.length() != 0) && (UserConfig.lastPauseTime != 0))
    {
      UserConfig.lastPauseTime = 0;
      UserConfig.saveConfig(false);
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (this.actionBarLayout.fragmentsStack.size() != 0) {
      ((BaseFragment)this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(paramInt1, paramInt2, paramIntent);
    }
    if (AndroidUtilities.isTablet())
    {
      if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
        ((BaseFragment)this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(paramInt1, paramInt2, paramIntent);
      }
      if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
        ((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(paramInt1, paramInt2, paramIntent);
      }
    }
  }
  
  public void onBackPressed()
  {
    if (this.passcodeView.getVisibility() == 0) {
      finish();
    }
    for (;;)
    {
      return;
      if (PhotoViewer.getInstance().isVisible())
      {
        PhotoViewer.getInstance().closePhoto(true, false);
        return;
      }
      if (this.drawerLayoutContainer.isDrawerOpened())
      {
        this.drawerLayoutContainer.closeDrawer(false);
        return;
      }
      if (!AndroidUtilities.isTablet()) {
        break;
      }
      if (this.layersActionBarLayout.getVisibility() == 0)
      {
        this.layersActionBarLayout.onBackPressed();
        return;
      }
      int j = 0;
      int i = j;
      if (this.rightActionBarLayout.getVisibility() == 0)
      {
        i = j;
        if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
          if (((BaseFragment)this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onBackPressed()) {
            break label150;
          }
        }
      }
      label150:
      for (i = 1; i == 0; i = 0)
      {
        this.actionBarLayout.onBackPressed();
        return;
      }
    }
    this.actionBarLayout.onBackPressed();
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    AndroidUtilities.checkDisplaySize();
    super.onConfigurationChanged(paramConfiguration);
    fixLayout();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    final Object localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).edit();
    ((SharedPreferences.Editor)localObject1).putBoolean("chat_unlocked", false);
    ((SharedPreferences.Editor)localObject1).commit();
    this.MihanAuthenticaion = new MihanAuthenticaion(this);
    this.MihanAuthenticaion.bazarInitService();
    ApplicationLoader.postInitApplication();
    NativeCrashManager.handleDumpFiles(this);
    if (!UserConfig.isClientActivated())
    {
      localObject1 = getIntent();
      if ((localObject1 != null) && (((Intent)localObject1).getAction() != null) && (("android.intent.action.SEND".equals(((Intent)localObject1).getAction())) || (((Intent)localObject1).getAction().equals("android.intent.action.SEND_MULTIPLE"))))
      {
        super.onCreate(paramBundle);
        finish();
        return;
      }
      if ((localObject1 != null) && (!((Intent)localObject1).getBooleanExtra("fromIntro", false)) && (ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).getAll().isEmpty()))
      {
        startActivity(new Intent(this, IntroActivity.class));
        super.onCreate(paramBundle);
        finish();
        return;
      }
    }
    requestWindowFeature(1);
    setTheme(2131361926);
    getWindow().setBackgroundDrawableResource(2130838195);
    super.onCreate(paramBundle);
    Theme.loadRecources(this);
    if ((UserConfig.passcodeHash.length() != 0) && (UserConfig.appLocked)) {
      UserConfig.lastPauseTime = ConnectionsManager.getInstance().getCurrentTime();
    }
    int i = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (i > 0) {
      AndroidUtilities.statusBarHeight = getResources().getDimensionPixelSize(i);
    }
    this.actionBarLayout = new ActionBarLayout(this);
    this.drawerLayoutContainer = new DrawerLayoutContainer(this);
    setContentView(this.drawerLayoutContainer, new ViewGroup.LayoutParams(-1, -1));
    Object localObject3;
    Point localPoint;
    if (AndroidUtilities.isTablet())
    {
      getWindow().setSoftInputMode(16);
      localObject1 = new RelativeLayout(this);
      this.drawerLayoutContainer.addView((View)localObject1);
      localObject3 = (FrameLayout.LayoutParams)((RelativeLayout)localObject1).getLayoutParams();
      ((FrameLayout.LayoutParams)localObject3).width = -1;
      ((FrameLayout.LayoutParams)localObject3).height = -1;
      ((RelativeLayout)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject3);
      this.backgroundTablet = new ImageView(this);
      this.backgroundTablet.setScaleType(ImageView.ScaleType.CENTER_CROP);
      this.backgroundTablet.setImageResource(2130837666);
      ((RelativeLayout)localObject1).addView(this.backgroundTablet);
      localObject3 = (RelativeLayout.LayoutParams)this.backgroundTablet.getLayoutParams();
      ((RelativeLayout.LayoutParams)localObject3).width = -1;
      ((RelativeLayout.LayoutParams)localObject3).height = -1;
      this.backgroundTablet.setLayoutParams((ViewGroup.LayoutParams)localObject3);
      ((RelativeLayout)localObject1).addView(this.actionBarLayout);
      localObject3 = (RelativeLayout.LayoutParams)this.actionBarLayout.getLayoutParams();
      ((RelativeLayout.LayoutParams)localObject3).width = -1;
      ((RelativeLayout.LayoutParams)localObject3).height = -1;
      this.actionBarLayout.setLayoutParams((ViewGroup.LayoutParams)localObject3);
      this.rightActionBarLayout = new ActionBarLayout(this);
      ((RelativeLayout)localObject1).addView(this.rightActionBarLayout);
      localObject3 = (RelativeLayout.LayoutParams)this.rightActionBarLayout.getLayoutParams();
      ((RelativeLayout.LayoutParams)localObject3).width = AndroidUtilities.dp(320.0F);
      ((RelativeLayout.LayoutParams)localObject3).height = -1;
      this.rightActionBarLayout.setLayoutParams((ViewGroup.LayoutParams)localObject3);
      this.rightActionBarLayout.init(rightFragmentsStack);
      this.rightActionBarLayout.setDelegate(this);
      this.shadowTabletSide = new FrameLayout(this);
      this.shadowTabletSide.setBackgroundColor(1076449908);
      ((RelativeLayout)localObject1).addView(this.shadowTabletSide);
      localObject3 = (RelativeLayout.LayoutParams)this.shadowTabletSide.getLayoutParams();
      ((RelativeLayout.LayoutParams)localObject3).width = AndroidUtilities.dp(1.0F);
      ((RelativeLayout.LayoutParams)localObject3).height = -1;
      this.shadowTabletSide.setLayoutParams((ViewGroup.LayoutParams)localObject3);
      this.shadowTablet = new FrameLayout(this);
      this.shadowTablet.setVisibility(8);
      this.shadowTablet.setBackgroundColor(2130706432);
      ((RelativeLayout)localObject1).addView(this.shadowTablet);
      localObject3 = (RelativeLayout.LayoutParams)this.shadowTablet.getLayoutParams();
      ((RelativeLayout.LayoutParams)localObject3).width = -1;
      ((RelativeLayout.LayoutParams)localObject3).height = -1;
      this.shadowTablet.setLayoutParams((ViewGroup.LayoutParams)localObject3);
      this.shadowTablet.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          if ((!LaunchActivity.this.actionBarLayout.fragmentsStack.isEmpty()) && (paramAnonymousMotionEvent.getAction() == 1))
          {
            float f1 = paramAnonymousMotionEvent.getX();
            float f2 = paramAnonymousMotionEvent.getY();
            paramAnonymousView = new int[2];
            LaunchActivity.this.layersActionBarLayout.getLocationOnScreen(paramAnonymousView);
            int i = paramAnonymousView[0];
            int j = paramAnonymousView[1];
            if ((LaunchActivity.this.layersActionBarLayout.checkTransitionAnimation()) || ((f1 > i) && (f1 < LaunchActivity.this.layersActionBarLayout.getWidth() + i) && (f2 > j) && (f2 < LaunchActivity.this.layersActionBarLayout.getHeight() + j))) {
              return false;
            }
            if (!LaunchActivity.this.layersActionBarLayout.fragmentsStack.isEmpty())
            {
              for (i = 0; LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
                LaunchActivity.this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)LaunchActivity.this.layersActionBarLayout.fragmentsStack.get(0));
              }
              LaunchActivity.this.layersActionBarLayout.closeLastFragment(true);
            }
            return true;
          }
          return false;
        }
      });
      this.shadowTablet.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView) {}
      });
      this.layersActionBarLayout = new ActionBarLayout(this);
      this.layersActionBarLayout.setRemoveActionBarExtraHeight(true);
      this.layersActionBarLayout.setBackgroundView(this.shadowTablet);
      this.layersActionBarLayout.setUseAlphaAnimations(true);
      this.layersActionBarLayout.setBackgroundResource(2130837646);
      ((RelativeLayout)localObject1).addView(this.layersActionBarLayout);
      localObject1 = (RelativeLayout.LayoutParams)this.layersActionBarLayout.getLayoutParams();
      ((RelativeLayout.LayoutParams)localObject1).width = AndroidUtilities.dp(530.0F);
      ((RelativeLayout.LayoutParams)localObject1).height = AndroidUtilities.dp(528.0F);
      this.layersActionBarLayout.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      this.layersActionBarLayout.init(layerFragmentsStack);
      this.layersActionBarLayout.setDelegate(this);
      this.layersActionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
      this.layersActionBarLayout.setVisibility(8);
      localObject1 = new ListView(this)
      {
        public boolean hasOverlappingRendering()
        {
          return false;
        }
      };
      ((ListView)localObject1).setBackgroundColor(-1);
      localObject3 = new DrawerLayoutAdapter(this);
      this.drawerLayoutAdapter = ((DrawerLayoutAdapter)localObject3);
      ((ListView)localObject1).setAdapter((ListAdapter)localObject3);
      ((ListView)localObject1).setChoiceMode(1);
      ((ListView)localObject1).setDivider(null);
      ((ListView)localObject1).setDividerHeight(0);
      ((ListView)localObject1).setVerticalScrollBarEnabled(false);
      this.drawerLayoutContainer.setDrawerLayout((ViewGroup)localObject1);
      localObject3 = (FrameLayout.LayoutParams)((ListView)localObject1).getLayoutParams();
      localPoint = AndroidUtilities.getRealScreenSize();
      if (!AndroidUtilities.isTablet()) {
        break label1486;
      }
      i = AndroidUtilities.dp(320.0F);
      label985:
      ((FrameLayout.LayoutParams)localObject3).width = i;
      ((FrameLayout.LayoutParams)localObject3).height = -1;
      ((ListView)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject3);
      ((ListView)localObject1).setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          if (paramAnonymousInt == 2) {
            if (MessagesController.isFeatureEnabled("chat_create", (BaseFragment)LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1))) {}
          }
          do
          {
            do
            {
              return;
              LaunchActivity.this.presentFragment(new GroupCreateActivity());
              LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
              return;
              if (paramAnonymousInt == 3)
              {
                paramAnonymousAdapterView = new Bundle();
                paramAnonymousAdapterView.putBoolean("onlyUsers", true);
                paramAnonymousAdapterView.putBoolean("destroyAfterSelect", true);
                paramAnonymousAdapterView.putBoolean("createSecretChat", true);
                paramAnonymousAdapterView.putBoolean("allowBots", false);
                LaunchActivity.this.presentFragment(new ContactsActivity(paramAnonymousAdapterView));
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                return;
              }
              if (paramAnonymousInt != 4) {
                break;
              }
            } while (!MessagesController.isFeatureEnabled("broadcast_create", (BaseFragment)LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1)));
            paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            if (paramAnonymousAdapterView.getBoolean("channel_intro", false))
            {
              paramAnonymousAdapterView = new Bundle();
              paramAnonymousAdapterView.putInt("step", 0);
              LaunchActivity.this.presentFragment(new ChannelCreateActivity(paramAnonymousAdapterView));
            }
            for (;;)
            {
              LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
              return;
              LaunchActivity.this.presentFragment(new ChannelIntroActivity());
              paramAnonymousAdapterView.edit().putBoolean("channel_intro", true).commit();
            }
            if (paramAnonymousInt == 6)
            {
              LaunchActivity.this.presentFragment(new ContactsActivity(null));
              LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
              return;
            }
            if (paramAnonymousInt == 7)
            {
              LaunchActivity.this.presentFragment(new OnlineContactsActivity(null));
              LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
              return;
            }
            if (paramAnonymousInt == 8)
            {
              LaunchActivity.this.presentFragment(new SpecialContactsActivity());
              LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
              return;
            }
            if (paramAnonymousInt == 9)
            {
              LaunchActivity.this.presentFragment(new UpdateActivity(null));
              LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
              return;
            }
            if (paramAnonymousInt == 10)
            {
              LaunchActivity.this.presentFragment(new IdFinderActivity());
              LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
              return;
            }
            if (paramAnonymousInt == 11)
            {
              LaunchActivity.this.startActivity(new Intent(LaunchActivity.this, Download.class));
              LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
              return;
            }
            if (paramAnonymousInt == 12) {
              try
              {
                paramAnonymousAdapterView = new Intent("android.intent.action.SEND");
                paramAnonymousAdapterView.setType("text/plain");
                paramAnonymousAdapterView.putExtra("android.intent.extra.TEXT", ContactsController.getInstance().getInviteText());
                LaunchActivity.this.startActivityForResult(Intent.createChooser(paramAnonymousAdapterView, LocaleController.getString("InviteFriends", 2131165827)), 500);
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                return;
              }
              catch (Exception paramAnonymousAdapterView)
              {
                for (;;)
                {
                  FileLog.e("tmessages", paramAnonymousAdapterView);
                }
              }
            }
            if (paramAnonymousInt == 13)
            {
              LaunchActivity.this.presentFragment(new SettingsActivity());
              LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
              return;
            }
            if (paramAnonymousInt == 14)
            {
              LaunchActivity.this.presentFragment(new MihanSettingsActivity());
              LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
              return;
            }
          } while (paramAnonymousInt != 15);
          Browser.openUrl(LaunchActivity.this, LocaleController.getString("TelegramFaqUrl", 2131166388));
          LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
        }
      });
      this.drawerLayoutContainer.setParentActionBarLayout(this.actionBarLayout);
      this.actionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
      this.actionBarLayout.init(mainFragmentsStack);
      this.actionBarLayout.setDelegate(this);
      ApplicationLoader.loadWallpaper();
      this.passcodeView = new PasscodeView(this);
      this.drawerLayoutContainer.addView(this.passcodeView);
      localObject1 = (FrameLayout.LayoutParams)this.passcodeView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject1).width = -1;
      ((FrameLayout.LayoutParams)localObject1).height = -1;
      this.passcodeView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeOtherAppActivities, new Object[] { this });
      this.currentConnectionState = ConnectionsManager.getInstance().getConnectionState();
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.mainUserInfoChanged);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeOtherAppActivities);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.didUpdatedConnectionState);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.needShowAlert);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
      if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
        break label1914;
      }
      if (UserConfig.isClientActivated()) {
        break label1519;
      }
      this.actionBarLayout.addFragmentToStack(new LoginActivity());
      this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
      label1246:
      if (paramBundle == null) {}
    }
    try
    {
      localObject1 = paramBundle.getString("fragment");
      if (localObject1 != null)
      {
        localObject3 = paramBundle.getBundle("args");
        i = -1;
        int j = ((String)localObject1).hashCode();
        switch (j)
        {
        default: 
          label1348:
          switch (i)
          {
          }
          break;
        }
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        label1392:
        label1486:
        label1519:
        FileLog.e("tmessages", localException);
        continue;
        localObject2 = new SettingsActivity();
        this.actionBarLayout.addFragmentToStack((BaseFragment)localObject2);
        ((SettingsActivity)localObject2).restoreSelfArgs(paramBundle);
        continue;
        if (localObject3 != null)
        {
          localObject2 = new GroupCreateFinalActivity((Bundle)localObject3);
          if (this.actionBarLayout.addFragmentToStack((BaseFragment)localObject2))
          {
            ((GroupCreateFinalActivity)localObject2).restoreSelfArgs(paramBundle);
            continue;
            if (localObject3 != null)
            {
              localObject2 = new ChannelCreateActivity((Bundle)localObject3);
              if (this.actionBarLayout.addFragmentToStack((BaseFragment)localObject2))
              {
                ((ChannelCreateActivity)localObject2).restoreSelfArgs(paramBundle);
                continue;
                if (localObject3 != null)
                {
                  localObject2 = new ChannelEditActivity((Bundle)localObject3);
                  if (this.actionBarLayout.addFragmentToStack((BaseFragment)localObject2))
                  {
                    ((ChannelEditActivity)localObject2).restoreSelfArgs(paramBundle);
                    continue;
                    if (localObject3 != null)
                    {
                      localObject2 = new ProfileActivity((Bundle)localObject3);
                      if (this.actionBarLayout.addFragmentToStack((BaseFragment)localObject2))
                      {
                        ((ProfileActivity)localObject2).restoreSelfArgs(paramBundle);
                        continue;
                        localObject2 = new WallpapersActivity();
                        this.actionBarLayout.addFragmentToStack((BaseFragment)localObject2);
                        ((WallpapersActivity)localObject2).restoreSelfArgs(paramBundle);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    localObject1 = getIntent();
    if (paramBundle != null) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      handleIntent((Intent)localObject1, false, bool1, false);
      needLayout();
      localObject1 = getWindow().getDecorView().getRootView();
      paramBundle = ((View)localObject1).getViewTreeObserver();
      localObject1 = new ViewTreeObserver.OnGlobalLayoutListener()
      {
        public void onGlobalLayout()
        {
          int i = localObject1.getMeasuredHeight();
          if ((i > AndroidUtilities.dp(100.0F)) && (i < AndroidUtilities.displaySize.y) && (AndroidUtilities.dp(100.0F) + i > AndroidUtilities.displaySize.y))
          {
            AndroidUtilities.displaySize.y = i;
            FileLog.e("tmessages", "fix display size y to " + AndroidUtilities.displaySize.y);
          }
        }
      };
      this.onGlobalLayoutListener = ((ViewTreeObserver.OnGlobalLayoutListener)localObject1);
      paramBundle.addOnGlobalLayoutListener((ViewTreeObserver.OnGlobalLayoutListener)localObject1);
      return;
      this.drawerLayoutContainer.addView(this.actionBarLayout, new ViewGroup.LayoutParams(-1, -1));
      break;
      i = Math.min(AndroidUtilities.dp(320.0F), Math.min(localPoint.x, localPoint.y) - AndroidUtilities.dp(56.0F));
      break label985;
      this.actionBarLayout.addFragmentToStack(new DialogsActivity(null));
      this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
      break label1246;
      if (!((String)localObject1).equals("chat")) {
        break label1348;
      }
      i = 0;
      break label1348;
      if (!((String)localObject1).equals("settings")) {
        break label1348;
      }
      i = 1;
      break label1348;
      if (!((String)localObject1).equals("group")) {
        break label1348;
      }
      i = 2;
      break label1348;
      if (!((String)localObject1).equals("channel")) {
        break label1348;
      }
      i = 3;
      break label1348;
      if (!((String)localObject1).equals("edit")) {
        break label1348;
      }
      i = 4;
      break label1348;
      if (!((String)localObject1).equals("chat_profile")) {
        break label1348;
      }
      i = 5;
      break label1348;
      if (!((String)localObject1).equals("wallpapers")) {
        break label1348;
      }
      i = 6;
      break label1348;
      if (localObject3 == null) {
        break label1392;
      }
      localObject1 = new ChatActivity((Bundle)localObject3);
      if (!this.actionBarLayout.addFragmentToStack((BaseFragment)localObject1)) {
        break label1392;
      }
      ((ChatActivity)localObject1).restoreSelfArgs(paramBundle);
      break label1392;
      Object localObject2;
      label1914:
      bool1 = true;
      if (AndroidUtilities.isTablet()) {
        if ((this.actionBarLayout.fragmentsStack.size() > 1) || (!this.layersActionBarLayout.fragmentsStack.isEmpty())) {
          break label2050;
        }
      }
      label2050:
      for (boolean bool2 = true;; bool2 = false)
      {
        bool1 = bool2;
        if (this.layersActionBarLayout.fragmentsStack.size() == 1)
        {
          bool1 = bool2;
          if ((this.layersActionBarLayout.fragmentsStack.get(0) instanceof LoginActivity)) {
            bool1 = false;
          }
        }
        bool2 = bool1;
        if (this.actionBarLayout.fragmentsStack.size() == 1)
        {
          bool2 = bool1;
          if ((this.actionBarLayout.fragmentsStack.get(0) instanceof LoginActivity)) {
            bool2 = false;
          }
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(bool2, false);
        break;
      }
    }
  }
  
  protected void onDestroy()
  {
    this.MihanAuthenticaion.bazarReleaseService();
    PhotoViewer.getInstance().destroyPhotoViewer();
    SecretPhotoViewer.getInstance().destroyPhotoViewer();
    StickerPreviewViewer.getInstance().destroy();
    try
    {
      if (this.visibleDialog != null)
      {
        this.visibleDialog.dismiss();
        this.visibleDialog = null;
      }
    }
    catch (Exception localException1)
    {
      try
      {
        if (this.onGlobalLayoutListener != null)
        {
          View localView = getWindow().getDecorView().getRootView();
          if (Build.VERSION.SDK_INT >= 16) {
            break label101;
          }
          localView.getViewTreeObserver().removeGlobalOnLayoutListener(this.onGlobalLayoutListener);
        }
        for (;;)
        {
          super.onDestroy();
          onFinish();
          return;
          localException1 = localException1;
          FileLog.e("tmessages", localException1);
          break;
          label101:
          localException1.getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
        }
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException2);
        }
      }
    }
  }
  
  public boolean onKeyUp(int paramInt, @NonNull KeyEvent paramKeyEvent)
  {
    if ((paramInt == 82) && (!UserConfig.isWaitingForPasscodeEnter))
    {
      if (!AndroidUtilities.isTablet()) {
        break label107;
      }
      if ((this.layersActionBarLayout.getVisibility() != 0) || (this.layersActionBarLayout.fragmentsStack.isEmpty())) {
        break label58;
      }
      this.layersActionBarLayout.onKeyUp(paramInt, paramKeyEvent);
    }
    for (;;)
    {
      return super.onKeyUp(paramInt, paramKeyEvent);
      label58:
      if ((this.rightActionBarLayout.getVisibility() == 0) && (!this.rightActionBarLayout.fragmentsStack.isEmpty()))
      {
        this.rightActionBarLayout.onKeyUp(paramInt, paramKeyEvent);
      }
      else
      {
        this.actionBarLayout.onKeyUp(paramInt, paramKeyEvent);
        continue;
        label107:
        if (this.actionBarLayout.fragmentsStack.size() == 1)
        {
          if (!this.drawerLayoutContainer.isDrawerOpened())
          {
            if (getCurrentFocus() != null) {
              AndroidUtilities.hideKeyboard(getCurrentFocus());
            }
            this.drawerLayoutContainer.openDrawer(false);
          }
          else
          {
            this.drawerLayoutContainer.closeDrawer(false);
          }
        }
        else {
          this.actionBarLayout.onKeyUp(paramInt, paramKeyEvent);
        }
      }
    }
  }
  
  public void onLowMemory()
  {
    super.onLowMemory();
    this.actionBarLayout.onLowMemory();
    if (AndroidUtilities.isTablet())
    {
      this.rightActionBarLayout.onLowMemory();
      this.layersActionBarLayout.onLowMemory();
    }
  }
  
  protected void onNewIntent(Intent paramIntent)
  {
    super.onNewIntent(paramIntent);
    handleIntent(paramIntent, true, false, false);
  }
  
  protected void onPause()
  {
    super.onPause();
    ApplicationLoader.mainInterfacePaused = true;
    onPasscodePause();
    this.actionBarLayout.onPause();
    if (AndroidUtilities.isTablet())
    {
      this.rightActionBarLayout.onPause();
      this.layersActionBarLayout.onPause();
    }
    if (this.passcodeView != null) {
      this.passcodeView.onPause();
    }
    ConnectionsManager.getInstance().setAppPaused(true, false);
    AndroidUtilities.unregisterUpdates();
    if (PhotoViewer.getInstance().isVisible()) {
      PhotoViewer.getInstance().onPause();
    }
  }
  
  public boolean onPreIme()
  {
    if (PhotoViewer.getInstance().isVisible())
    {
      PhotoViewer.getInstance().closePhoto(true, false);
      return true;
    }
    return false;
  }
  
  public void onRebuildAllFragments(ActionBarLayout paramActionBarLayout)
  {
    if ((AndroidUtilities.isTablet()) && (paramActionBarLayout == this.layersActionBarLayout))
    {
      this.rightActionBarLayout.rebuildAllFragmentViews(true);
      this.rightActionBarLayout.showLastFragment();
      this.actionBarLayout.rebuildAllFragmentViews(true);
      this.actionBarLayout.showLastFragment();
    }
    this.drawerLayoutAdapter.notifyDataSetChanged();
  }
  
  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    super.onRequestPermissionsResult(paramInt, paramArrayOfString, paramArrayOfInt);
    if ((paramInt == 3) || (paramInt == 4) || (paramInt == 5)) {
      if ((paramArrayOfInt.length > 0) && (paramArrayOfInt[0] == 0)) {
        if (paramInt == 4) {
          ImageLoader.getInstance().checkMediaPaths();
        }
      }
    }
    do
    {
      do
      {
        do
        {
          return;
        } while (paramInt != 5);
        ContactsController.getInstance().readContacts();
        return;
        paramArrayOfString = new AlertDialog.Builder(this);
        paramArrayOfString.setTitle(LocaleController.getString("AppName", 2131165338));
        if (paramInt == 3) {
          paramArrayOfString.setMessage(LocaleController.getString("PermissionNoAudio", 2131166160));
        }
        for (;;)
        {
          paramArrayOfString.setNegativeButton(LocaleController.getString("PermissionOpenSettings", 2131166163), new DialogInterface.OnClickListener()
          {
            @TargetApi(9)
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
              try
              {
                paramAnonymousDialogInterface = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                paramAnonymousDialogInterface.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                LaunchActivity.this.startActivity(paramAnonymousDialogInterface);
                return;
              }
              catch (Exception paramAnonymousDialogInterface)
              {
                FileLog.e("tmessages", paramAnonymousDialogInterface);
              }
            }
          });
          paramArrayOfString.setPositiveButton(LocaleController.getString("OK", 2131166111), null);
          paramArrayOfString.show();
          return;
          if (paramInt == 4) {
            paramArrayOfString.setMessage(LocaleController.getString("PermissionStorage", 2131166164));
          } else if (paramInt == 5) {
            paramArrayOfString.setMessage(LocaleController.getString("PermissionContacts", 2131166159));
          }
        }
        if ((paramInt == 2) && (paramArrayOfInt.length > 0) && (paramArrayOfInt[0] == 0)) {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.locationPermissionGranted, new Object[0]);
        }
        if (this.actionBarLayout.fragmentsStack.size() != 0) {
          ((BaseFragment)this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(paramInt, paramArrayOfString, paramArrayOfInt);
        }
      } while (!AndroidUtilities.isTablet());
      if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
        ((BaseFragment)this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(paramInt, paramArrayOfString, paramArrayOfInt);
      }
    } while (this.layersActionBarLayout.fragmentsStack.size() == 0);
    ((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(paramInt, paramArrayOfString, paramArrayOfInt);
  }
  
  protected void onResume()
  {
    super.onResume();
    ApplicationLoader.mainInterfacePaused = false;
    onPasscodeResume();
    if (this.passcodeView.getVisibility() != 0)
    {
      this.actionBarLayout.onResume();
      if (AndroidUtilities.isTablet())
      {
        this.rightActionBarLayout.onResume();
        this.layersActionBarLayout.onResume();
      }
    }
    for (;;)
    {
      AndroidUtilities.checkForCrashes(this);
      AndroidUtilities.checkForUpdates(this);
      ConnectionsManager.getInstance().setAppPaused(false, false);
      updateCurrentConnectionState();
      if (PhotoViewer.getInstance().isVisible()) {
        PhotoViewer.getInstance().onResume();
      }
      return;
      this.passcodeView.onResume();
    }
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    for (;;)
    {
      BaseFragment localBaseFragment;
      Bundle localBundle;
      try
      {
        super.onSaveInstanceState(paramBundle);
        localBaseFragment = null;
        if (AndroidUtilities.isTablet())
        {
          if (!this.layersActionBarLayout.fragmentsStack.isEmpty())
          {
            localBaseFragment = (BaseFragment)this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1);
            if (localBaseFragment == null) {
              break;
            }
            localBundle = localBaseFragment.getArguments();
            if (((localBaseFragment instanceof ChatActivity)) && (localBundle != null))
            {
              paramBundle.putBundle("args", localBundle);
              paramBundle.putString("fragment", "chat");
              localBaseFragment.saveSelfArgs(paramBundle);
            }
          }
          else
          {
            if (!this.rightActionBarLayout.fragmentsStack.isEmpty())
            {
              localBaseFragment = (BaseFragment)this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1);
              continue;
            }
            if (this.actionBarLayout.fragmentsStack.isEmpty()) {
              continue;
            }
            localBaseFragment = (BaseFragment)this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
            continue;
          }
        }
        else
        {
          if (this.actionBarLayout.fragmentsStack.isEmpty()) {
            continue;
          }
          localBaseFragment = (BaseFragment)this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
          continue;
        }
        if ((localBaseFragment instanceof SettingsActivity))
        {
          paramBundle.putString("fragment", "settings");
          continue;
        }
        if (!(localBaseFragment instanceof GroupCreateFinalActivity)) {
          break label283;
        }
      }
      catch (Exception paramBundle)
      {
        FileLog.e("tmessages", paramBundle);
        return;
      }
      if (localBundle != null)
      {
        paramBundle.putBundle("args", localBundle);
        paramBundle.putString("fragment", "group");
      }
      else
      {
        label283:
        if ((localBaseFragment instanceof WallpapersActivity))
        {
          paramBundle.putString("fragment", "wallpapers");
        }
        else if (((localBaseFragment instanceof ProfileActivity)) && (((ProfileActivity)localBaseFragment).isChat()) && (localBundle != null))
        {
          paramBundle.putBundle("args", localBundle);
          paramBundle.putString("fragment", "chat_profile");
        }
        else if (((localBaseFragment instanceof ChannelCreateActivity)) && (localBundle != null) && (localBundle.getInt("step") == 0))
        {
          paramBundle.putBundle("args", localBundle);
          paramBundle.putString("fragment", "channel");
        }
        else if (((localBaseFragment instanceof ChannelEditActivity)) && (localBundle != null))
        {
          paramBundle.putBundle("args", localBundle);
          paramBundle.putString("fragment", "edit");
        }
      }
    }
  }
  
  protected void onStart()
  {
    super.onStart();
    Browser.bindCustomTabsService(this);
  }
  
  protected void onStop()
  {
    super.onStop();
    Browser.unbindCustomTabsService(this);
  }
  
  public void presentFragment(BaseFragment paramBaseFragment)
  {
    this.actionBarLayout.presentFragment(paramBaseFragment);
  }
  
  public boolean presentFragment(BaseFragment paramBaseFragment, boolean paramBoolean1, boolean paramBoolean2)
  {
    return this.actionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, true);
  }
  
  public AlertDialog showAlertDialog(AlertDialog.Builder paramBuilder)
  {
    try
    {
      if (this.visibleDialog != null)
      {
        this.visibleDialog.dismiss();
        this.visibleDialog = null;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        try
        {
          this.visibleDialog = paramBuilder.show();
          this.visibleDialog.setCanceledOnTouchOutside(true);
          this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
          {
            public void onDismiss(DialogInterface paramAnonymousDialogInterface)
            {
              LaunchActivity.access$1002(LaunchActivity.this, null);
            }
          });
          paramBuilder = this.visibleDialog;
          return paramBuilder;
        }
        catch (Exception paramBuilder)
        {
          FileLog.e("tmessages", paramBuilder);
        }
        localException = localException;
        FileLog.e("tmessages", localException);
      }
    }
    return null;
  }
  
  private class VcardData
  {
    String name;
    ArrayList<String> phones = new ArrayList();
    
    private VcardData() {}
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\LaunchActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */