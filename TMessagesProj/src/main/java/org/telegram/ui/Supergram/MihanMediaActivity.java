package org.telegram.ui.Supergram;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.query.SharedMediaQuery;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterDocument;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterMusic;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterUrl;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.TL_webPageEmpty;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Adapters.BaseSectionsAdapter;
import org.telegram.ui.Cells.GreySectionCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.SharedLinkCell;
import org.telegram.ui.Cells.SharedLinkCell.SharedLinkCellDelegate;
import org.telegram.ui.Cells.SharedMediaSectionCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell.SharedPhotoVideoCellDelegate;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.PlayerView;
import org.telegram.ui.Components.SectionsListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.WebFrameLayout;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.Supergram.Theming.MihanTheme;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class MihanMediaActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, PhotoViewer.PhotoViewerProvider
{
  private static final int delete = 4;
  private static final int files_item = 2;
  private static final int filter = 12;
  private static final int forward = 3;
  private static final int links_item = 5;
  private static final int music_item = 6;
  private static final int photo_item = 9;
  private static final int quoteforward = 11;
  private static final int shared_media_item = 1;
  private static final int video_item = 10;
  private ArrayList<View> actionModeViews = new ArrayList();
  private SharedDocumentsAdapter audioAdapter;
  private MediaSearchAdapter audioSearchAdapter;
  private int cantDeleteMessagesCount;
  private ArrayList<SharedPhotoVideoCell> cellCache = new ArrayList(6);
  private int columnsCount = 4;
  private long dialog_id;
  private SharedDocumentsAdapter documentsAdapter;
  private MediaSearchAdapter documentsSearchAdapter;
  private TextView dropDown;
  private ActionBarMenuItem dropDownContainer;
  private ImageView emptyImageView;
  private TextView emptyTextView;
  private LinearLayout emptyView;
  private int filterType = 0;
  protected TLRPC.ChatFull info = null;
  private SharedLinksAdapter linksAdapter;
  private MediaSearchAdapter linksSearchAdapter;
  private SectionsListView listView;
  private long mergeDialogId;
  private MihanPhotoVideoAdapter photoAdapter;
  private SharedPhotoVideoAdapter photoVideoAdapter;
  private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
  private LinearLayout progressView;
  private boolean scrolling;
  private ActionBarMenuItem searchItem;
  private boolean searchWas;
  private boolean searching;
  private HashMap<Integer, MessageObject>[] selectedFiles = { new HashMap(), new HashMap() };
  private NumberTextView selectedMessagesCountTextView;
  private int selectedMode;
  private SharedMediaData[] sharedMediaData = new SharedMediaData[7];
  private MihanPhotoVideoAdapter videoAdapter;
  
  public MihanMediaActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }
  
  private boolean downloaded(MessageObject paramMessageObject)
  {
    boolean bool2 = false;
    boolean bool3 = false;
    if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) || (paramMessageObject.getDocument() != null) || (paramMessageObject.isMusic()) || (paramMessageObject.isVideo()))
    {
      boolean bool1 = bool3;
      if (paramMessageObject.messageOwner.attachPath != null)
      {
        bool1 = bool3;
        if (paramMessageObject.messageOwner.attachPath.length() != 0)
        {
          bool1 = bool3;
          if (new File(paramMessageObject.messageOwner.attachPath).exists()) {
            bool1 = true;
          }
        }
      }
      bool2 = bool1;
      if (!bool1)
      {
        bool2 = bool1;
        if (FileLoader.getPathToMessage(paramMessageObject.messageOwner).exists()) {
          bool2 = true;
        }
      }
    }
    return bool2;
  }
  
  private void fixLayoutInternal()
  {
    int i = 0;
    if (this.listView == null) {
      return;
    }
    int j = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
    if ((!AndroidUtilities.isTablet()) && (ApplicationLoader.applicationContext.getResources().getConfiguration().orientation == 2))
    {
      this.selectedMessagesCountTextView.setTextSize(18);
      label62:
      if (!AndroidUtilities.isTablet()) {
        break label214;
      }
      this.columnsCount = 4;
      this.emptyTextView.setPadding(AndroidUtilities.dp(40.0F), 0, AndroidUtilities.dp(40.0F), AndroidUtilities.dp(128.0F));
    }
    for (;;)
    {
      this.photoVideoAdapter.notifyDataSetChanged();
      this.photoAdapter.notifyDataSetChanged();
      this.videoAdapter.notifyDataSetChanged();
      if (this.dropDownContainer == null) {
        break;
      }
      if (!AndroidUtilities.isTablet())
      {
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.dropDownContainer.getLayoutParams();
        if (Build.VERSION.SDK_INT >= 21) {
          i = AndroidUtilities.statusBarHeight;
        }
        localLayoutParams.topMargin = i;
        this.dropDownContainer.setLayoutParams(localLayoutParams);
      }
      if ((AndroidUtilities.isTablet()) || (ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2)) {
        break label288;
      }
      this.dropDown.setTextSize(18.0F);
      return;
      this.selectedMessagesCountTextView.setTextSize(20);
      break label62;
      label214:
      if ((j == 3) || (j == 1))
      {
        this.columnsCount = 6;
        this.emptyTextView.setPadding(AndroidUtilities.dp(40.0F), 0, AndroidUtilities.dp(40.0F), 0);
      }
      else
      {
        this.columnsCount = 4;
        this.emptyTextView.setPadding(AndroidUtilities.dp(40.0F), 0, AndroidUtilities.dp(40.0F), AndroidUtilities.dp(128.0F));
      }
    }
    label288:
    this.dropDown.setTextSize(20.0F);
  }
  
  private void onItemClick(int paramInt1, View paramView, MessageObject paramMessageObject, int paramInt2)
  {
    if (paramMessageObject == null) {}
    label83:
    label114:
    Object localObject1;
    label186:
    label225:
    label255:
    label262:
    Object localObject2;
    label477:
    do
    {
      do
      {
        do
        {
          do
          {
            return;
            if (!this.actionBar.isActionModeShowed()) {
              break;
            }
            if (paramMessageObject.getDialogId() == this.dialog_id)
            {
              paramInt1 = 0;
              if (!this.selectedFiles[paramInt1].containsKey(Integer.valueOf(paramMessageObject.getId()))) {
                break label186;
              }
              this.selectedFiles[paramInt1].remove(Integer.valueOf(paramMessageObject.getId()));
              if (!paramMessageObject.canDeleteMessage(null)) {
                this.cantDeleteMessagesCount -= 1;
              }
              if ((!this.selectedFiles[0].isEmpty()) || (!this.selectedFiles[1].isEmpty())) {
                break label225;
              }
              this.actionBar.hideActionMode();
              localObject1 = this.actionBar.createActionMode().getItem(4);
              if (this.cantDeleteMessagesCount != 0) {
                break label255;
              }
            }
            for (int i = 0;; i = 8)
            {
              ((ActionBarMenuItem)localObject1).setVisibility(i);
              this.scrolling = false;
              if (!(paramView instanceof SharedDocumentCell)) {
                break label262;
              }
              ((SharedDocumentCell)paramView).setChecked(this.selectedFiles[paramInt1].containsKey(Integer.valueOf(paramMessageObject.getId())), true);
              return;
              paramInt1 = 1;
              break;
              this.selectedFiles[paramInt1].put(Integer.valueOf(paramMessageObject.getId()), paramMessageObject);
              if (paramMessageObject.canDeleteMessage(null)) {
                break label83;
              }
              this.cantDeleteMessagesCount += 1;
              break label83;
              this.selectedMessagesCountTextView.setNumber(this.selectedFiles[0].size() + this.selectedFiles[1].size(), true);
              break label114;
            }
            if ((paramView instanceof SharedPhotoVideoCell))
            {
              ((SharedPhotoVideoCell)paramView).setChecked(paramInt2, this.selectedFiles[paramInt1].containsKey(Integer.valueOf(paramMessageObject.getId())), true);
              return;
            }
          } while (!(paramView instanceof SharedLinkCell));
          ((SharedLinkCell)paramView).setChecked(this.selectedFiles[paramInt1].containsKey(Integer.valueOf(paramMessageObject.getId())), true);
          return;
          if ((this.selectedMode == 0) || (this.selectedMode == 5) || (this.selectedMode == 6))
          {
            PhotoViewer.getInstance().setParentActivity(getParentActivity());
            PhotoViewer.getInstance().openPhoto(this.sharedMediaData[this.selectedMode].messages, paramInt1, this.dialog_id, this.mergeDialogId, this);
            return;
          }
          if ((this.selectedMode != 1) && (this.selectedMode != 4)) {
            break;
          }
        } while (!(paramView instanceof SharedDocumentCell));
        paramView = (SharedDocumentCell)paramView;
        if (!paramView.isLoaded()) {
          break;
        }
      } while ((paramMessageObject.isMusic()) && (MediaController.getInstance().setPlaylist(this.sharedMediaData[this.selectedMode].messages, paramMessageObject)));
      localObject1 = null;
      if (paramMessageObject.messageOwner.media == null) {
        break;
      }
      localObject2 = FileLoader.getAttachFileName(paramMessageObject.getDocument());
      paramView = (View)localObject1;
      if (paramMessageObject.messageOwner.attachPath != null)
      {
        paramView = (View)localObject1;
        if (paramMessageObject.messageOwner.attachPath.length() != 0) {
          paramView = new File(paramMessageObject.messageOwner.attachPath);
        }
      }
      if (paramView != null)
      {
        localObject1 = paramView;
        if (paramView != null)
        {
          localObject1 = paramView;
          if (paramView.exists()) {}
        }
      }
      else
      {
        localObject1 = FileLoader.getPathToMessage(paramMessageObject.messageOwner);
      }
    } while ((localObject1 == null) || (!((File)localObject1).exists()));
    label806:
    label823:
    label840:
    label989:
    label1021:
    for (paramView = null;; paramView = null)
    {
      Intent localIntent;
      for (;;)
      {
        try
        {
          localIntent = new Intent("android.intent.action.VIEW");
          MimeTypeMap localMimeTypeMap = MimeTypeMap.getSingleton();
          paramInt1 = ((String)localObject2).lastIndexOf('.');
          if (paramInt1 == -1) {
            break label823;
          }
          localObject2 = localMimeTypeMap.getMimeTypeFromExtension(((String)localObject2).substring(paramInt1 + 1).toLowerCase());
          paramView = (View)localObject2;
          if (localObject2 == null)
          {
            localObject2 = paramMessageObject.getDocument().mime_type;
            if (localObject2 == null) {
              break label1021;
            }
            paramView = (View)localObject2;
            if (((String)localObject2).length() == 0) {
              break label1021;
            }
          }
          if (paramView == null) {
            break label806;
          }
          localIntent.setDataAndType(Uri.fromFile((File)localObject1), paramView);
          if (paramView == null) {
            break label840;
          }
          try
          {
            getParentActivity().startActivityForResult(localIntent, 500);
            return;
          }
          catch (Exception paramView)
          {
            localIntent.setDataAndType(Uri.fromFile((File)localObject1), "text/plain");
            getParentActivity().startActivityForResult(localIntent, 500);
            return;
          }
          if (getParentActivity() == null) {
            break;
          }
        }
        catch (Exception paramView) {}
        paramView = new AlertDialog.Builder(getParentActivity());
        paramView.setTitle(LocaleController.getString("AppName", 2131165338));
        paramView.setPositiveButton(LocaleController.getString("OK", 2131166111), null);
        paramView.setMessage(LocaleController.formatString("NoHandleAppInstalled", 2131166006, new Object[] { paramMessageObject.getDocument().mime_type }));
        showDialog(paramView.create());
        return;
        localObject2 = "";
        break label477;
        localIntent.setDataAndType(Uri.fromFile((File)localObject1), "text/plain");
        continue;
        localIntent.setDataAndType(Uri.fromFile((File)localObject1), "text/plain");
      }
      getParentActivity().startActivityForResult(localIntent, 500);
      return;
      if (!paramView.isLoading())
      {
        FileLoader.getInstance().loadFile(paramView.getMessage().getDocument(), false, false);
        paramView.updateFileExistIcon();
        return;
      }
      FileLoader.getInstance().cancelLoadFile(paramView.getMessage().getDocument());
      paramView.updateFileExistIcon();
      return;
      if (this.selectedMode != 3) {
        break;
      }
      try
      {
        localObject2 = paramMessageObject.messageOwner.media.webpage;
        localObject1 = null;
        paramMessageObject = (MessageObject)localObject1;
        if (localObject2 == null) {
          break label989;
        }
        paramMessageObject = (MessageObject)localObject1;
        if ((localObject2 instanceof TLRPC.TL_webPageEmpty)) {
          break label989;
        }
        if ((Build.VERSION.SDK_INT >= 16) && (((TLRPC.WebPage)localObject2).embed_url != null) && (((TLRPC.WebPage)localObject2).embed_url.length() != 0))
        {
          openWebView((TLRPC.WebPage)localObject2);
          return;
        }
      }
      catch (Exception paramView)
      {
        FileLog.e("tmessages", paramView);
        return;
      }
      paramMessageObject = ((TLRPC.WebPage)localObject2).url;
      localObject1 = paramMessageObject;
      if (paramMessageObject == null) {
        localObject1 = ((SharedLinkCell)paramView).getLink(0);
      }
      if (localObject1 == null) {
        break;
      }
      Browser.openUrl(getParentActivity(), (String)localObject1);
      return;
    }
  }
  
  private boolean onItemLongClick(MessageObject paramMessageObject, View paramView, int paramInt)
  {
    if (this.actionBar.isActionModeShowed()) {
      return false;
    }
    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
    Object localObject = this.selectedFiles;
    if (paramMessageObject.getDialogId() == this.dialog_id)
    {
      i = 0;
      localObject[i].put(Integer.valueOf(paramMessageObject.getId()), paramMessageObject);
      if (!paramMessageObject.canDeleteMessage(null)) {
        this.cantDeleteMessagesCount += 1;
      }
      paramMessageObject = this.actionBar.createActionMode().getItem(4);
      if (this.cantDeleteMessagesCount != 0) {
        break label208;
      }
    }
    label208:
    for (int i = 0;; i = 8)
    {
      paramMessageObject.setVisibility(i);
      this.selectedMessagesCountTextView.setNumber(1, false);
      paramMessageObject = new AnimatorSet();
      localObject = new ArrayList();
      i = 0;
      while (i < this.actionModeViews.size())
      {
        View localView = (View)this.actionModeViews.get(i);
        AndroidUtilities.clearDrawableAnimation(localView);
        ((ArrayList)localObject).add(ObjectAnimator.ofFloat(localView, "scaleY", new float[] { 0.1F, 1.0F }));
        i += 1;
      }
      i = 1;
      break;
    }
    paramMessageObject.playTogether((Collection)localObject);
    paramMessageObject.setDuration(250L);
    paramMessageObject.start();
    this.scrolling = false;
    if ((paramView instanceof SharedDocumentCell)) {
      ((SharedDocumentCell)paramView).setChecked(true, true);
    }
    for (;;)
    {
      this.actionBar.showActionMode();
      return true;
      if ((paramView instanceof SharedPhotoVideoCell)) {
        ((SharedPhotoVideoCell)paramView).setChecked(paramInt, true, true);
      } else if ((paramView instanceof SharedLinkCell)) {
        ((SharedLinkCell)paramView).setChecked(true, true);
      }
    }
  }
  
  private void openWebView(TLRPC.WebPage paramWebPage)
  {
    BottomSheet.Builder localBuilder = new BottomSheet.Builder(getParentActivity());
    localBuilder.setCustomView(new WebFrameLayout(getParentActivity(), localBuilder.create(), paramWebPage.site_name, paramWebPage.description, paramWebPage.url, paramWebPage.embed_url, paramWebPage.embed_width, paramWebPage.embed_height));
    localBuilder.setUseFullWidth(true);
    showDialog(localBuilder.create());
  }
  
  private void switchToCurrentSelectedMode()
  {
    if ((this.searching) && (this.searchWas)) {
      if (this.listView != null)
      {
        if (this.selectedMode == 1)
        {
          this.listView.setAdapter(this.documentsSearchAdapter);
          this.documentsSearchAdapter.notifyDataSetChanged();
        }
      }
      else if (this.emptyTextView != null)
      {
        this.emptyTextView.setText(LocaleController.getString("NoResult", 2131166020));
        this.emptyTextView.setTextSize(1, 20.0F);
        this.emptyImageView.setVisibility(8);
      }
    }
    label429:
    label715:
    label917:
    ActionBarMenuItem localActionBarMenuItem;
    int i;
    label1051:
    label1136:
    label1343:
    label1349:
    label1354:
    do
    {
      return;
      if (this.selectedMode == 3)
      {
        this.listView.setAdapter(this.linksSearchAdapter);
        this.linksSearchAdapter.notifyDataSetChanged();
        break;
      }
      if (this.selectedMode != 4) {
        break;
      }
      this.listView.setAdapter(this.audioSearchAdapter);
      this.audioSearchAdapter.notifyDataSetChanged();
      break;
      this.emptyTextView.setTextSize(1, 17.0F);
      this.emptyImageView.setVisibility(0);
      if (this.selectedMode == 5)
      {
        this.listView.setAdapter(this.photoAdapter);
        this.dropDown.setText(LocaleController.getString("SharedPhotoTitle", 2131166670));
        this.emptyImageView.setImageResource(2130838174);
        if ((int)this.dialog_id == 0)
        {
          this.emptyTextView.setText(LocaleController.getString("NoSharedPhotoSecret", 2131166658));
          this.searchItem.setVisibility(8);
          if ((!this.sharedMediaData[this.selectedMode].loading) && (this.sharedMediaData[this.selectedMode].endReached[0] == 0) && (this.sharedMediaData[this.selectedMode].messages.isEmpty()))
          {
            SharedMediaData.access$1302(this.sharedMediaData[this.selectedMode], true);
            SharedMediaQuery.loadMedia(this.dialog_id, 0, 50, 0, 5, true, this.classGuid);
          }
          if ((!this.sharedMediaData[this.selectedMode].loading) || (!this.sharedMediaData[this.selectedMode].messages.isEmpty())) {
            break label429;
          }
          this.progressView.setVisibility(0);
          this.listView.setEmptyView(null);
          this.emptyView.setVisibility(8);
        }
        for (;;)
        {
          this.listView.setVisibility(0);
          this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(4.0F));
          return;
          this.emptyTextView.setText(LocaleController.getString("NoSharedPhoto", 2131166657));
          break;
          this.progressView.setVisibility(8);
          this.listView.setEmptyView(this.emptyView);
        }
      }
      if (this.selectedMode == 6)
      {
        this.listView.setAdapter(this.videoAdapter);
        this.dropDown.setText(LocaleController.getString("SharedVideoTitle", 2131166671));
        this.emptyImageView.setImageResource(2130838174);
        if ((int)this.dialog_id == 0)
        {
          this.emptyTextView.setText(LocaleController.getString("NoSharedVideoSecret", 2131166660));
          this.searchItem.setVisibility(8);
          if ((!this.sharedMediaData[this.selectedMode].loading) && (this.sharedMediaData[this.selectedMode].endReached[0] == 0) && (this.sharedMediaData[this.selectedMode].messages.isEmpty()))
          {
            SharedMediaData.access$1302(this.sharedMediaData[this.selectedMode], true);
            SharedMediaQuery.loadMedia(this.dialog_id, 0, 50, 0, 6, true, this.classGuid);
          }
          if ((!this.sharedMediaData[this.selectedMode].loading) || (!this.sharedMediaData[this.selectedMode].messages.isEmpty())) {
            break label715;
          }
          this.progressView.setVisibility(0);
          this.listView.setEmptyView(null);
          this.emptyView.setVisibility(8);
        }
        for (;;)
        {
          this.listView.setVisibility(0);
          this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(4.0F));
          return;
          this.emptyTextView.setText(LocaleController.getString("NoSharedVideo", 2131166659));
          break;
          this.progressView.setVisibility(8);
          this.listView.setEmptyView(this.emptyView);
        }
      }
      if (this.selectedMode == 0)
      {
        this.listView.setAdapter(this.photoVideoAdapter);
        this.dropDown.setText(LocaleController.getString("SharedMediaTitle", 2131166334));
        this.emptyImageView.setImageResource(2130838174);
        if ((int)this.dialog_id == 0)
        {
          this.emptyTextView.setText(LocaleController.getString("NoMediaSecret", 2131166010));
          this.searchItem.setVisibility(8);
          if ((!this.sharedMediaData[this.selectedMode].loading) || (!this.sharedMediaData[this.selectedMode].messages.isEmpty())) {
            break label917;
          }
          this.progressView.setVisibility(0);
          this.listView.setEmptyView(null);
          this.emptyView.setVisibility(8);
        }
        for (;;)
        {
          this.listView.setVisibility(0);
          this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(4.0F));
          return;
          this.emptyTextView.setText(LocaleController.getString("NoMedia", 2131166008));
          break;
          this.progressView.setVisibility(8);
          this.listView.setEmptyView(this.emptyView);
        }
      }
      if ((this.selectedMode == 1) || (this.selectedMode == 4))
      {
        if (this.selectedMode == 1)
        {
          this.listView.setAdapter(this.documentsAdapter);
          this.dropDown.setText(LocaleController.getString("DocumentsTitle", 2131165633));
          this.emptyImageView.setImageResource(2130838175);
          if ((int)this.dialog_id == 0)
          {
            this.emptyTextView.setText(LocaleController.getString("NoSharedFilesSecret", 2131166024));
            localActionBarMenuItem = this.searchItem;
            if (this.sharedMediaData[this.selectedMode].messages.isEmpty()) {
              break label1343;
            }
            i = 0;
            localActionBarMenuItem.setVisibility(i);
            if ((!this.sharedMediaData[this.selectedMode].loading) && (this.sharedMediaData[this.selectedMode].endReached[0] == 0) && (this.sharedMediaData[this.selectedMode].messages.isEmpty()))
            {
              SharedMediaData.access$1302(this.sharedMediaData[this.selectedMode], true);
              long l = this.dialog_id;
              if (this.selectedMode != 1) {
                break label1349;
              }
              i = 1;
              SharedMediaQuery.loadMedia(l, 0, 50, 0, i, true, this.classGuid);
            }
            this.listView.setVisibility(0);
            if ((!this.sharedMediaData[this.selectedMode].loading) || (!this.sharedMediaData[this.selectedMode].messages.isEmpty())) {
              break label1354;
            }
            this.progressView.setVisibility(0);
            this.listView.setEmptyView(null);
            this.emptyView.setVisibility(8);
          }
        }
        for (;;)
        {
          this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(4.0F));
          return;
          this.emptyTextView.setText(LocaleController.getString("NoSharedFiles", 2131166023));
          break;
          if (this.selectedMode != 4) {
            break;
          }
          this.listView.setAdapter(this.audioAdapter);
          this.dropDown.setText(LocaleController.getString("AudioTitle", 2131165378));
          this.emptyImageView.setImageResource(2130838177);
          if ((int)this.dialog_id == 0)
          {
            this.emptyTextView.setText(LocaleController.getString("NoSharedAudioSecret", 2131166022));
            break;
          }
          this.emptyTextView.setText(LocaleController.getString("NoSharedAudio", 2131166021));
          break;
          i = 8;
          break label1051;
          i = 4;
          break label1136;
          this.progressView.setVisibility(8);
          this.listView.setEmptyView(this.emptyView);
        }
      }
    } while (this.selectedMode != 3);
    this.listView.setAdapter(this.linksAdapter);
    this.dropDown.setText(LocaleController.getString("LinksTitle", 2131165904));
    this.emptyImageView.setImageResource(2130838176);
    if ((int)this.dialog_id == 0)
    {
      this.emptyTextView.setText(LocaleController.getString("NoSharedLinksSecret", 2131166026));
      localActionBarMenuItem = this.searchItem;
      if (this.sharedMediaData[3].messages.isEmpty()) {
        break label1658;
      }
      i = 0;
      label1469:
      localActionBarMenuItem.setVisibility(i);
      if ((!this.sharedMediaData[this.selectedMode].loading) && (this.sharedMediaData[this.selectedMode].endReached[0] == 0) && (this.sharedMediaData[this.selectedMode].messages.isEmpty()))
      {
        SharedMediaData.access$1302(this.sharedMediaData[this.selectedMode], true);
        SharedMediaQuery.loadMedia(this.dialog_id, 0, 50, 0, 3, true, this.classGuid);
      }
      this.listView.setVisibility(0);
      if ((!this.sharedMediaData[this.selectedMode].loading) || (!this.sharedMediaData[this.selectedMode].messages.isEmpty())) {
        break label1664;
      }
      this.progressView.setVisibility(0);
      this.listView.setEmptyView(null);
      this.emptyView.setVisibility(8);
    }
    for (;;)
    {
      this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(4.0F));
      return;
      this.emptyTextView.setText(LocaleController.getString("NoSharedLinks", 2131166025));
      break;
      label1658:
      i = 8;
      break label1469;
      label1664:
      this.progressView.setVisibility(8);
      this.listView.setEmptyView(this.emptyView);
    }
  }
  
  public boolean cancelButtonPressed()
  {
    return true;
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonDrawable(new BackDrawable(false));
    this.actionBar.setTitle("");
    this.actionBar.setAllowOverlayTitle(false);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          if (MihanMediaActivity.this.actionBar.isActionModeShowed())
          {
            paramAnonymousInt = 1;
            while (paramAnonymousInt >= 0)
            {
              MihanMediaActivity.this.selectedFiles[paramAnonymousInt].clear();
              paramAnonymousInt -= 1;
            }
            MihanMediaActivity.access$602(MihanMediaActivity.this, 0);
            MihanMediaActivity.this.actionBar.hideActionMode();
            MihanMediaActivity.this.listView.invalidateViews();
          }
        }
        Object localObject2;
        Object localObject3;
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        do
                        {
                          do
                          {
                            return;
                            MihanMediaActivity.this.finishFragment();
                            return;
                            if (paramAnonymousInt == 12)
                            {
                              localObject1 = new BottomSheet.Builder(MihanMediaActivity.this.getParentActivity());
                              localObject2 = LocaleController.getString("MihanAllMedia", 2131166810);
                              localObject3 = LocaleController.getString("MihanDownloadedMedia", 2131166811);
                              String str = LocaleController.getString("MihanNotDownloadedMedia", 2131166812);
                              DialogInterface.OnClickListener local1 = new DialogInterface.OnClickListener()
                              {
                                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                                {
                                  new AlertDialog.Builder(MihanMediaActivity.this.getParentActivity()).setTitle(LocaleController.getString("AppName", 2131165338));
                                  switch (paramAnonymous2Int)
                                  {
                                  default: 
                                    paramAnonymous2Int = 0;
                                    label58:
                                    if (paramAnonymous2Int >= MihanMediaActivity.this.sharedMediaData.length) {
                                      break label284;
                                    }
                                    MihanMediaActivity.this.sharedMediaData[paramAnonymous2Int] = null;
                                    MihanMediaActivity.this.sharedMediaData[paramAnonymous2Int] = new MihanMediaActivity.SharedMediaData(MihanMediaActivity.this, null);
                                    paramAnonymous2DialogInterface = MihanMediaActivity.SharedMediaData.access$200(MihanMediaActivity.this.sharedMediaData[paramAnonymous2Int]);
                                    if ((int)MihanMediaActivity.this.dialog_id != 0) {
                                      break;
                                    }
                                  }
                                  for (int i = Integer.MIN_VALUE;; i = Integer.MAX_VALUE)
                                  {
                                    paramAnonymous2DialogInterface[0] = i;
                                    if ((MihanMediaActivity.this.mergeDialogId != 0L) && (MihanMediaActivity.this.info != null))
                                    {
                                      MihanMediaActivity.SharedMediaData.access$200(MihanMediaActivity.this.sharedMediaData[paramAnonymous2Int])[1] = MihanMediaActivity.this.info.migrated_from_max_id;
                                      MihanMediaActivity.SharedMediaData.access$300(MihanMediaActivity.this.sharedMediaData[paramAnonymous2Int])[1] = 0;
                                    }
                                    paramAnonymous2Int += 1;
                                    break label58;
                                    MihanMediaActivity.access$902(MihanMediaActivity.this, 0);
                                    break;
                                    MihanMediaActivity.access$902(MihanMediaActivity.this, 1);
                                    break;
                                    MihanMediaActivity.access$902(MihanMediaActivity.this, 2);
                                    break;
                                  }
                                  label284:
                                  MihanMediaActivity.SharedMediaData.access$1302(MihanMediaActivity.this.sharedMediaData[MihanMediaActivity.this.selectedMode], true);
                                  SharedMediaQuery.loadMedia(MihanMediaActivity.this.dialog_id, 0, 50, 0, MihanMediaActivity.this.selectedMode, false, MihanMediaActivity.this.classGuid);
                                }
                              };
                              ((BottomSheet.Builder)localObject1).setItems(new CharSequence[] { localObject2, localObject3, str }, local1);
                              MihanMediaActivity.this.showDialog(((BottomSheet.Builder)localObject1).create());
                              return;
                            }
                            if (paramAnonymousInt != 9) {
                              break;
                            }
                          } while (MihanMediaActivity.this.selectedMode == 5);
                          MihanMediaActivity.access$1202(MihanMediaActivity.this, 5);
                          MihanMediaActivity.this.switchToCurrentSelectedMode();
                          return;
                          if (paramAnonymousInt != 10) {
                            break;
                          }
                        } while (MihanMediaActivity.this.selectedMode == 6);
                        MihanMediaActivity.access$1202(MihanMediaActivity.this, 6);
                        MihanMediaActivity.this.switchToCurrentSelectedMode();
                        return;
                        if (paramAnonymousInt != 1) {
                          break;
                        }
                      } while (MihanMediaActivity.this.selectedMode == 0);
                      MihanMediaActivity.access$1202(MihanMediaActivity.this, 0);
                      MihanMediaActivity.this.switchToCurrentSelectedMode();
                      return;
                      if (paramAnonymousInt != 2) {
                        break;
                      }
                    } while (MihanMediaActivity.this.selectedMode == 1);
                    MihanMediaActivity.access$1202(MihanMediaActivity.this, 1);
                    MihanMediaActivity.this.switchToCurrentSelectedMode();
                    return;
                    if (paramAnonymousInt != 5) {
                      break;
                    }
                  } while (MihanMediaActivity.this.selectedMode == 3);
                  MihanMediaActivity.access$1202(MihanMediaActivity.this, 3);
                  MihanMediaActivity.this.switchToCurrentSelectedMode();
                  return;
                  if (paramAnonymousInt != 6) {
                    break;
                  }
                } while (MihanMediaActivity.this.selectedMode == 4);
                MihanMediaActivity.access$1202(MihanMediaActivity.this, 4);
                MihanMediaActivity.this.switchToCurrentSelectedMode();
                return;
                if (paramAnonymousInt != 4) {
                  break;
                }
              } while (MihanMediaActivity.this.getParentActivity() == null);
              localObject1 = new AlertDialog.Builder(MihanMediaActivity.this.getParentActivity());
              ((AlertDialog.Builder)localObject1).setMessage(LocaleController.formatString("AreYouSureDeleteMessages", 2131165350, new Object[] { LocaleController.formatPluralString("items", MihanMediaActivity.this.selectedFiles[0].size() + MihanMediaActivity.this.selectedFiles[1].size()) }));
              ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165338));
              ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  paramAnonymous2Int = 1;
                  while (paramAnonymous2Int >= 0)
                  {
                    ArrayList localArrayList = new ArrayList(MihanMediaActivity.this.selectedFiles[paramAnonymous2Int].keySet());
                    Object localObject1 = null;
                    paramAnonymous2DialogInterface = null;
                    int j = 0;
                    int i = j;
                    Object localObject2;
                    if (!localArrayList.isEmpty())
                    {
                      localObject2 = (MessageObject)MihanMediaActivity.this.selectedFiles[paramAnonymous2Int].get(localArrayList.get(0));
                      i = j;
                      if (0 == 0)
                      {
                        i = j;
                        if (((MessageObject)localObject2).messageOwner.to_id.channel_id != 0) {
                          i = ((MessageObject)localObject2).messageOwner.to_id.channel_id;
                        }
                      }
                    }
                    if ((int)MihanMediaActivity.this.dialog_id == 0) {
                      paramAnonymous2DialogInterface = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int)(MihanMediaActivity.this.dialog_id >> 32)));
                    }
                    if (paramAnonymous2DialogInterface != null)
                    {
                      localObject2 = new ArrayList();
                      Iterator localIterator = MihanMediaActivity.this.selectedFiles[paramAnonymous2Int].entrySet().iterator();
                      for (;;)
                      {
                        localObject1 = localObject2;
                        if (!localIterator.hasNext()) {
                          break;
                        }
                        localObject1 = (MessageObject)((Map.Entry)localIterator.next()).getValue();
                        if ((((MessageObject)localObject1).messageOwner.random_id != 0L) && (((MessageObject)localObject1).type != 10)) {
                          ((ArrayList)localObject2).add(Long.valueOf(((MessageObject)localObject1).messageOwner.random_id));
                        }
                      }
                    }
                    MessagesController.getInstance().deleteMessages(localArrayList, (ArrayList)localObject1, paramAnonymous2DialogInterface, i);
                    MihanMediaActivity.this.selectedFiles[paramAnonymous2Int].clear();
                    paramAnonymous2Int -= 1;
                  }
                  MihanMediaActivity.this.actionBar.hideActionMode();
                  MihanMediaActivity.this.actionBar.closeSearchField();
                  MihanMediaActivity.access$602(MihanMediaActivity.this, 0);
                }
              });
              ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
              MihanMediaActivity.this.showDialog(((AlertDialog.Builder)localObject1).create());
              return;
              if (paramAnonymousInt != 11) {
                break;
              }
              localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
              localObject2 = ((SharedPreferences)localObject1).edit();
              ((SharedPreferences.Editor)localObject2).putInt("forward_type", 1);
              ((SharedPreferences.Editor)localObject2).commit();
              if (!((SharedPreferences)localObject1).getBoolean("multi_forward", false))
              {
                localObject1 = new Bundle();
                ((Bundle)localObject1).putBoolean("onlySelect", true);
                ((Bundle)localObject1).putInt("dialogsType", 0);
                localObject1 = new DialogsActivity((Bundle)localObject1);
                ((DialogsActivity)localObject1).setDelegate(new DialogsActivity.DialogsActivityDelegate()
                {
                  public void didSelectDialog(DialogsActivity paramAnonymous2DialogsActivity, long paramAnonymous2Long, boolean paramAnonymous2Boolean)
                  {
                    int i = (int)paramAnonymous2Long;
                    if (i != 0)
                    {
                      Object localObject1 = new Bundle();
                      ((Bundle)localObject1).putBoolean("scrollToTopOnResume", true);
                      if (i > 0) {
                        ((Bundle)localObject1).putInt("user_id", i);
                      }
                      do
                      {
                        while (!MessagesController.checkCanOpenChat((Bundle)localObject1, paramAnonymous2DialogsActivity))
                        {
                          return;
                          if (i < 0) {
                            ((Bundle)localObject1).putInt("chat_id", -i);
                          }
                        }
                        paramAnonymous2DialogsActivity = new ArrayList();
                        i = 1;
                        while (i >= 0)
                        {
                          Object localObject2 = new ArrayList(MihanMediaActivity.this.selectedFiles[i].keySet());
                          Collections.sort((List)localObject2);
                          localObject2 = ((ArrayList)localObject2).iterator();
                          while (((Iterator)localObject2).hasNext())
                          {
                            Integer localInteger = (Integer)((Iterator)localObject2).next();
                            if (localInteger.intValue() > 0) {
                              paramAnonymous2DialogsActivity.add(MihanMediaActivity.this.selectedFiles[i].get(localInteger));
                            }
                          }
                          MihanMediaActivity.this.selectedFiles[i].clear();
                          i -= 1;
                        }
                        MihanMediaActivity.access$602(MihanMediaActivity.this, 0);
                        MihanMediaActivity.this.actionBar.hideActionMode();
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        localObject1 = new ChatActivity((Bundle)localObject1);
                        MihanMediaActivity.this.presentFragment((BaseFragment)localObject1, true);
                        ((ChatActivity)localObject1).showReplyPanel(true, null, paramAnonymous2DialogsActivity, null, false, false);
                      } while (AndroidUtilities.isTablet());
                      MihanMediaActivity.this.removeSelfFromStack();
                      return;
                    }
                    paramAnonymous2DialogsActivity.finishFragment();
                  }
                });
                MihanMediaActivity.this.presentFragment((BaseFragment)localObject1);
                return;
              }
            } while (MihanMediaActivity.this.getParentActivity() == null);
            localObject1 = new ArrayList();
            paramAnonymousInt = 1;
            while (paramAnonymousInt >= 0)
            {
              localObject2 = new ArrayList(MihanMediaActivity.this.selectedFiles[paramAnonymousInt].keySet());
              Collections.sort((List)localObject2);
              localObject2 = ((ArrayList)localObject2).iterator();
              while (((Iterator)localObject2).hasNext())
              {
                localObject3 = (Integer)((Iterator)localObject2).next();
                if (((Integer)localObject3).intValue() > 0) {
                  ((ArrayList)localObject1).add(MihanMediaActivity.this.selectedFiles[paramAnonymousInt].get(localObject3));
                }
              }
              MihanMediaActivity.this.selectedFiles[paramAnonymousInt].clear();
              paramAnonymousInt -= 1;
            }
            MihanMediaActivity.access$602(MihanMediaActivity.this, 0);
            MihanMediaActivity.this.actionBar.hideActionMode();
            MihanMediaActivity.this.showDialog(new ShareAlert(MihanMediaActivity.this.getParentActivity(), (ArrayList)localObject1, false, null));
            return;
          } while (paramAnonymousInt != 3);
          localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
          localObject2 = ((SharedPreferences)localObject1).edit();
          ((SharedPreferences.Editor)localObject2).putInt("forward_type", 2);
          ((SharedPreferences.Editor)localObject2).commit();
          if (!((SharedPreferences)localObject1).getBoolean("multi_forward", false))
          {
            localObject1 = new Bundle();
            ((Bundle)localObject1).putBoolean("onlySelect", true);
            ((Bundle)localObject1).putInt("dialogsType", 0);
            localObject1 = new DialogsActivity((Bundle)localObject1);
            ((DialogsActivity)localObject1).setDelegate(new DialogsActivity.DialogsActivityDelegate()
            {
              public void didSelectDialog(DialogsActivity paramAnonymous2DialogsActivity, long paramAnonymous2Long, boolean paramAnonymous2Boolean)
              {
                int i = (int)paramAnonymous2Long;
                if (i != 0)
                {
                  Object localObject1 = new Bundle();
                  ((Bundle)localObject1).putBoolean("scrollToTopOnResume", true);
                  if (i > 0) {
                    ((Bundle)localObject1).putInt("user_id", i);
                  }
                  do
                  {
                    while (!MessagesController.checkCanOpenChat((Bundle)localObject1, paramAnonymous2DialogsActivity))
                    {
                      return;
                      if (i < 0) {
                        ((Bundle)localObject1).putInt("chat_id", -i);
                      }
                    }
                    paramAnonymous2DialogsActivity = new ArrayList();
                    i = 1;
                    while (i >= 0)
                    {
                      Object localObject2 = new ArrayList(MihanMediaActivity.this.selectedFiles[i].keySet());
                      Collections.sort((List)localObject2);
                      localObject2 = ((ArrayList)localObject2).iterator();
                      while (((Iterator)localObject2).hasNext())
                      {
                        Integer localInteger = (Integer)((Iterator)localObject2).next();
                        if (localInteger.intValue() > 0) {
                          paramAnonymous2DialogsActivity.add(MihanMediaActivity.this.selectedFiles[i].get(localInteger));
                        }
                      }
                      MihanMediaActivity.this.selectedFiles[i].clear();
                      i -= 1;
                    }
                    MihanMediaActivity.access$602(MihanMediaActivity.this, 0);
                    MihanMediaActivity.this.actionBar.hideActionMode();
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    localObject1 = new ChatActivity((Bundle)localObject1);
                    MihanMediaActivity.this.presentFragment((BaseFragment)localObject1, true);
                    ((ChatActivity)localObject1).showReplyPanel(true, null, paramAnonymous2DialogsActivity, null, false, false);
                  } while (AndroidUtilities.isTablet());
                  MihanMediaActivity.this.removeSelfFromStack();
                  return;
                }
                paramAnonymous2DialogsActivity.finishFragment();
              }
            });
            MihanMediaActivity.this.presentFragment((BaseFragment)localObject1);
            return;
          }
        } while (MihanMediaActivity.this.getParentActivity() == null);
        Object localObject1 = new ArrayList();
        paramAnonymousInt = 1;
        while (paramAnonymousInt >= 0)
        {
          localObject2 = new ArrayList(MihanMediaActivity.this.selectedFiles[paramAnonymousInt].keySet());
          Collections.sort((List)localObject2);
          localObject2 = ((ArrayList)localObject2).iterator();
          while (((Iterator)localObject2).hasNext())
          {
            localObject3 = (Integer)((Iterator)localObject2).next();
            if (((Integer)localObject3).intValue() > 0) {
              ((ArrayList)localObject1).add(MihanMediaActivity.this.selectedFiles[paramAnonymousInt].get(localObject3));
            }
          }
          MihanMediaActivity.this.selectedFiles[paramAnonymousInt].clear();
          paramAnonymousInt -= 1;
        }
        MihanMediaActivity.access$602(MihanMediaActivity.this, 0);
        MihanMediaActivity.this.actionBar.hideActionMode();
        MihanMediaActivity.this.showDialog(new ShareAlert(MihanMediaActivity.this.getParentActivity(), (ArrayList)localObject1, false, null));
      }
    });
    int i = 1;
    while (i >= 0)
    {
      this.selectedFiles[i].clear();
      i -= 1;
    }
    this.cantDeleteMessagesCount = 0;
    this.actionModeViews.clear();
    Object localObject1 = this.actionBar.createMenu();
    this.searchItem = ((ActionBarMenu)localObject1).addItem(0, 2130837826).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
    {
      public void onSearchCollapse()
      {
        MihanMediaActivity.this.dropDownContainer.setVisibility(0);
        if (MihanMediaActivity.this.selectedMode == 1) {
          MihanMediaActivity.this.documentsSearchAdapter.search(null);
        }
        for (;;)
        {
          MihanMediaActivity.access$2302(MihanMediaActivity.this, false);
          MihanMediaActivity.access$2702(MihanMediaActivity.this, false);
          MihanMediaActivity.this.switchToCurrentSelectedMode();
          return;
          if (MihanMediaActivity.this.selectedMode == 3) {
            MihanMediaActivity.this.linksSearchAdapter.search(null);
          } else if (MihanMediaActivity.this.selectedMode == 4) {
            MihanMediaActivity.this.audioSearchAdapter.search(null);
          }
        }
      }
      
      public void onSearchExpand()
      {
        MihanMediaActivity.this.dropDownContainer.setVisibility(8);
        MihanMediaActivity.access$2302(MihanMediaActivity.this, true);
      }
      
      public void onTextChanged(EditText paramAnonymousEditText)
      {
        paramAnonymousEditText = paramAnonymousEditText.getText().toString();
        if (paramAnonymousEditText.length() != 0)
        {
          MihanMediaActivity.access$2702(MihanMediaActivity.this, true);
          MihanMediaActivity.this.switchToCurrentSelectedMode();
        }
        if (MihanMediaActivity.this.selectedMode == 1) {
          if (MihanMediaActivity.this.documentsSearchAdapter != null) {}
        }
        do
        {
          do
          {
            return;
            MihanMediaActivity.this.documentsSearchAdapter.search(paramAnonymousEditText);
            return;
            if (MihanMediaActivity.this.selectedMode != 3) {
              break;
            }
          } while (MihanMediaActivity.this.linksSearchAdapter == null);
          MihanMediaActivity.this.linksSearchAdapter.search(paramAnonymousEditText);
          return;
        } while ((MihanMediaActivity.this.selectedMode != 4) || (MihanMediaActivity.this.audioSearchAdapter == null));
        MihanMediaActivity.this.audioSearchAdapter.search(paramAnonymousEditText);
      }
    });
    this.searchItem.getSearchField().setHint(LocaleController.getString("Search", 2131166261));
    this.searchItem.setVisibility(8);
    ((ActionBarMenu)localObject1).addItem(12, 2130837815);
    this.dropDownContainer = new ActionBarMenuItem(paramContext, (ActionBarMenu)localObject1, 0);
    this.dropDownContainer.setSubMenuOpenSide(1);
    this.dropDownContainer.addSubItem(9, LocaleController.getString("SharedPhotoTitle", 2131166670), 0);
    this.dropDownContainer.addSubItem(10, LocaleController.getString("SharedPhotoTitle", 2131166671), 0);
    this.dropDownContainer.addSubItem(2, LocaleController.getString("DocumentsTitle", 2131165633), 0);
    if ((int)this.dialog_id != 0)
    {
      this.dropDownContainer.addSubItem(5, LocaleController.getString("LinksTitle", 2131165904), 0);
      this.dropDownContainer.addSubItem(6, LocaleController.getString("AudioTitle", 2131165378), 0);
      localObject1 = this.actionBar;
      localObject2 = this.dropDownContainer;
      if (!AndroidUtilities.isTablet()) {
        break label1107;
      }
    }
    label1107:
    for (float f = 64.0F;; f = 56.0F)
    {
      ((ActionBar)localObject1).addView((View)localObject2, 0, LayoutHelper.createFrame(-2, -1.0F, 51, f, 0.0F, 40.0F, 0.0F));
      this.dropDownContainer.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          MihanMediaActivity.this.dropDownContainer.toggleSubMenu();
        }
      });
      this.dropDown = new TextView(paramContext);
      this.dropDown.setGravity(3);
      this.dropDown.setSingleLine(true);
      this.dropDown.setLines(1);
      this.dropDown.setMaxLines(1);
      this.dropDown.setEllipsize(TextUtils.TruncateAt.END);
      localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
      i = MihanTheme.contrastColor(MihanTheme.getThemeColor((SharedPreferences)localObject1));
      this.dropDown.setTextColor(i);
      this.dropDown.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.dropDown.setCompoundDrawablesWithIntrinsicBounds(0, 0, 2130837837, 0);
      MihanTheme.setColorFilter(this.dropDown.getCompoundDrawables()[2], i);
      this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0F));
      this.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0F), 0);
      this.dropDownContainer.addView(this.dropDown, LayoutHelper.createFrame(-2, -2.0F, 16, 16.0F, 0.0F, 0.0F, 0.0F));
      localObject2 = this.actionBar.createActionMode();
      this.selectedMessagesCountTextView = new NumberTextView(((ActionBarMenu)localObject2).getContext());
      this.selectedMessagesCountTextView.setTextSize(18);
      this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.selectedMessagesCountTextView.setTextColor(-9211021);
      this.selectedMessagesCountTextView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      ((ActionBarMenu)localObject2).addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0F, 65, 0, 0, 0));
      if ((int)this.dialog_id != 0)
      {
        this.actionModeViews.add(((ActionBarMenu)localObject2).addItem(11, 2130837820, -986896, null, AndroidUtilities.dp(54.0F)));
        this.actionModeViews.add(((ActionBarMenu)localObject2).addItem(3, 2130837819, -986896, null, AndroidUtilities.dp(54.0F)));
      }
      this.actionModeViews.add(((ActionBarMenu)localObject2).addItem(4, 2130837818, -986896, null, AndroidUtilities.dp(54.0F)));
      i = ((SharedPreferences)localObject1).getInt("theme_chat_action_amicolor", -9276814);
      this.selectedMessagesCountTextView.setTextColor(i);
      MihanTheme.setColorFilter(((ActionBarMenu)localObject2).getItem(11).getImageView(), i);
      MihanTheme.setColorFilter(((ActionBarMenu)localObject2).getItem(3).getImageView(), i);
      MihanTheme.setColorFilter(((ActionBarMenu)localObject2).getItem(4).getImageView(), i);
      this.photoVideoAdapter = new SharedPhotoVideoAdapter(paramContext);
      this.photoAdapter = new MihanPhotoVideoAdapter(paramContext, 5);
      this.videoAdapter = new MihanPhotoVideoAdapter(paramContext, 6);
      this.documentsAdapter = new SharedDocumentsAdapter(paramContext, 1);
      this.audioAdapter = new SharedDocumentsAdapter(paramContext, 4);
      this.documentsSearchAdapter = new MediaSearchAdapter(paramContext, 1);
      this.audioSearchAdapter = new MediaSearchAdapter(paramContext, 4);
      this.linksSearchAdapter = new MediaSearchAdapter(paramContext, 3);
      this.linksAdapter = new SharedLinksAdapter(paramContext);
      localObject1 = new FrameLayout(paramContext);
      this.fragmentView = ((View)localObject1);
      this.listView = new SectionsListView(paramContext);
      this.listView.setDivider(null);
      this.listView.setDividerHeight(0);
      this.listView.setDrawSelectorOnTop(true);
      this.listView.setClipToPadding(false);
      ((FrameLayout)localObject1).addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          if (((MihanMediaActivity.this.selectedMode == 1) || (MihanMediaActivity.this.selectedMode == 4)) && ((paramAnonymousView instanceof SharedDocumentCell))) {
            MihanMediaActivity.this.onItemClick(paramAnonymousInt, paramAnonymousView, ((SharedDocumentCell)paramAnonymousView).getMessage(), 0);
          }
          while ((MihanMediaActivity.this.selectedMode != 3) || (!(paramAnonymousView instanceof SharedLinkCell))) {
            return;
          }
          MihanMediaActivity.this.onItemClick(paramAnonymousInt, paramAnonymousView, ((SharedLinkCell)paramAnonymousView).getMessage(), 0);
        }
      });
      this.listView.setOnScrollListener(new AbsListView.OnScrollListener()
      {
        public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
        {
          if ((MihanMediaActivity.this.searching) && (MihanMediaActivity.this.searchWas)) {}
          do
          {
            do
            {
              return;
            } while ((paramAnonymousInt2 == 0) || (paramAnonymousInt1 + paramAnonymousInt2 <= paramAnonymousInt3 - 2) || (MihanMediaActivity.SharedMediaData.access$1300(MihanMediaActivity.this.sharedMediaData[MihanMediaActivity.this.selectedMode])));
            if (MihanMediaActivity.this.selectedMode == 5) {
              paramAnonymousInt1 = 5;
            }
            while (MihanMediaActivity.SharedMediaData.access$300(MihanMediaActivity.this.sharedMediaData[MihanMediaActivity.this.selectedMode])[0] == 0)
            {
              MihanMediaActivity.SharedMediaData.access$1302(MihanMediaActivity.this.sharedMediaData[MihanMediaActivity.this.selectedMode], true);
              SharedMediaQuery.loadMedia(MihanMediaActivity.this.dialog_id, 0, 50, MihanMediaActivity.SharedMediaData.access$200(MihanMediaActivity.this.sharedMediaData[MihanMediaActivity.this.selectedMode])[0], paramAnonymousInt1, true, MihanMediaActivity.this.classGuid);
              return;
              if (MihanMediaActivity.this.selectedMode == 6) {
                paramAnonymousInt1 = 6;
              } else if (MihanMediaActivity.this.selectedMode == 0) {
                paramAnonymousInt1 = 0;
              } else if (MihanMediaActivity.this.selectedMode == 1) {
                paramAnonymousInt1 = 1;
              } else if (MihanMediaActivity.this.selectedMode == 2) {
                paramAnonymousInt1 = 2;
              } else if (MihanMediaActivity.this.selectedMode == 4) {
                paramAnonymousInt1 = 4;
              } else {
                paramAnonymousInt1 = 3;
              }
            }
          } while ((MihanMediaActivity.this.mergeDialogId == 0L) || (MihanMediaActivity.SharedMediaData.access$300(MihanMediaActivity.this.sharedMediaData[MihanMediaActivity.this.selectedMode])[1] != 0));
          MihanMediaActivity.SharedMediaData.access$1302(MihanMediaActivity.this.sharedMediaData[MihanMediaActivity.this.selectedMode], true);
          SharedMediaQuery.loadMedia(MihanMediaActivity.this.mergeDialogId, 0, 50, MihanMediaActivity.SharedMediaData.access$200(MihanMediaActivity.this.sharedMediaData[MihanMediaActivity.this.selectedMode])[1], paramAnonymousInt1, true, MihanMediaActivity.this.classGuid);
        }
        
        public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt)
        {
          boolean bool = true;
          if ((paramAnonymousInt == 1) && (MihanMediaActivity.this.searching) && (MihanMediaActivity.this.searchWas)) {
            AndroidUtilities.hideKeyboard(MihanMediaActivity.this.getParentActivity().getCurrentFocus());
          }
          paramAnonymousAbsListView = MihanMediaActivity.this;
          if (paramAnonymousInt != 0) {}
          for (;;)
          {
            MihanMediaActivity.access$2902(paramAnonymousAbsListView, bool);
            return;
            bool = false;
          }
        }
      });
      this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
      {
        public boolean onItemLongClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          boolean bool2 = false;
          boolean bool1;
          if (((MihanMediaActivity.this.selectedMode == 1) || (MihanMediaActivity.this.selectedMode == 4)) && ((paramAnonymousView instanceof SharedDocumentCell)))
          {
            paramAnonymousAdapterView = ((SharedDocumentCell)paramAnonymousView).getMessage();
            bool1 = MihanMediaActivity.this.onItemLongClick(paramAnonymousAdapterView, paramAnonymousView, 0);
          }
          do
          {
            do
            {
              return bool1;
              bool1 = bool2;
            } while (MihanMediaActivity.this.selectedMode != 3);
            bool1 = bool2;
          } while (!(paramAnonymousView instanceof SharedLinkCell));
          paramAnonymousAdapterView = ((SharedLinkCell)paramAnonymousView).getMessage();
          return MihanMediaActivity.this.onItemLongClick(paramAnonymousAdapterView, paramAnonymousView, 0);
        }
      });
      i = 0;
      while (i < 6)
      {
        this.cellCache.add(new SharedPhotoVideoCell(paramContext));
        i += 1;
      }
      localObject1 = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int)(this.dialog_id >> 32)));
      if ((localObject1 == null) || (AndroidUtilities.getPeerLayerVersion(((TLRPC.EncryptedChat)localObject1).layer) < 46)) {
        break;
      }
      this.dropDownContainer.addSubItem(6, LocaleController.getString("AudioTitle", 2131165378), 0);
      break;
    }
    this.emptyView = new LinearLayout(paramContext);
    this.emptyView.setOrientation(1);
    this.emptyView.setGravity(17);
    this.emptyView.setVisibility(8);
    this.emptyView.setBackgroundColor(-986896);
    ((FrameLayout)localObject1).addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
    this.emptyView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.emptyImageView = new ImageView(paramContext);
    this.emptyView.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
    this.emptyTextView = new TextView(paramContext);
    this.emptyTextView.setTextColor(-7697782);
    this.emptyTextView.setGravity(17);
    this.emptyTextView.setTextSize(1, 17.0F);
    this.emptyTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.emptyTextView.setPadding(AndroidUtilities.dp(40.0F), 0, AndroidUtilities.dp(40.0F), AndroidUtilities.dp(128.0F));
    this.emptyView.addView(this.emptyTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 24, 0, 0));
    this.progressView = new LinearLayout(paramContext);
    this.progressView.setGravity(17);
    this.progressView.setOrientation(1);
    this.progressView.setVisibility(8);
    this.progressView.setBackgroundColor(-986896);
    ((FrameLayout)localObject1).addView(this.progressView, LayoutHelper.createFrame(-1, -1.0F));
    Object localObject2 = new ProgressBar(paramContext);
    this.progressView.addView((View)localObject2, LayoutHelper.createLinear(-2, -2));
    switchToCurrentSelectedMode();
    if (!AndroidUtilities.isTablet()) {
      ((FrameLayout)localObject1).addView(new PlayerView(paramContext, this), LayoutHelper.createFrame(-1, 39.0F, 51, 0.0F, -36.0F, 0.0F, 0.0F));
    }
    return this.fragmentView;
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    int j;
    Object localObject1;
    boolean bool;
    label108:
    int i;
    label110:
    Object localObject2;
    if (paramInt == NotificationCenter.mediaDidLoaded)
    {
      long l = ((Long)paramVarArgs[0]).longValue();
      if (((Integer)paramVarArgs[3]).intValue() == this.classGuid)
      {
        j = ((Integer)paramVarArgs[4]).intValue();
        SharedMediaData.access$1302(this.sharedMediaData[j], false);
        SharedMediaData.access$3302(this.sharedMediaData[j], ((Integer)paramVarArgs[1]).intValue());
        localObject1 = (ArrayList)paramVarArgs[2];
        if ((int)this.dialog_id == 0)
        {
          bool = true;
          if (l != this.dialog_id) {
            break label165;
          }
          paramInt = 0;
          i = 0;
          if (i >= ((ArrayList)localObject1).size()) {
            break label261;
          }
          localObject2 = (MessageObject)((ArrayList)localObject1).get(i);
          if (j != 3) {
            break label170;
          }
          this.sharedMediaData[j].addMessage((MessageObject)localObject2, false, bool);
        }
        for (;;)
        {
          i += 1;
          break label110;
          bool = false;
          break;
          label165:
          paramInt = 1;
          break label108;
          label170:
          if (this.filterType == 1)
          {
            if (downloaded((MessageObject)localObject2)) {
              this.sharedMediaData[j].addMessage((MessageObject)localObject2, false, bool);
            }
          }
          else if (this.filterType == 2)
          {
            if (!downloaded((MessageObject)localObject2)) {
              this.sharedMediaData[j].addMessage((MessageObject)localObject2, false, bool);
            }
          }
          else {
            this.sharedMediaData[j].addMessage((MessageObject)localObject2, false, bool);
          }
        }
        label261:
        this.sharedMediaData[j].endReached[paramInt] = ((Boolean)paramVarArgs[5]).booleanValue();
        if ((paramInt == 0) && (this.sharedMediaData[this.selectedMode].messages.isEmpty()) && (this.mergeDialogId != 0L))
        {
          SharedMediaData.access$1302(this.sharedMediaData[this.selectedMode], true);
          SharedMediaQuery.loadMedia(this.mergeDialogId, 0, 50, this.sharedMediaData[this.selectedMode].max_id[1], j, true, this.classGuid);
        }
        if (!this.sharedMediaData[this.selectedMode].loading)
        {
          if (this.progressView != null) {
            this.progressView.setVisibility(8);
          }
          if ((this.selectedMode == j) && (this.listView != null) && (this.listView.getEmptyView() == null)) {
            this.listView.setEmptyView(this.emptyView);
          }
        }
        this.scrolling = true;
        if ((this.selectedMode != 5) || (j != 5)) {
          break label521;
        }
        if (this.photoAdapter != null) {
          this.photoAdapter.notifyDataSetChanged();
        }
        if ((this.selectedMode == 1) || (this.selectedMode == 3) || (this.selectedMode == 4))
        {
          paramVarArgs = this.searchItem;
          if ((this.sharedMediaData[this.selectedMode].messages.isEmpty()) || (this.searching)) {
            break label676;
          }
          paramInt = 0;
          label515:
          paramVarArgs.setVisibility(paramInt);
        }
      }
    }
    for (;;)
    {
      return;
      label521:
      if ((this.selectedMode == 6) && (j == 6))
      {
        if (this.videoAdapter == null) {
          break;
        }
        this.videoAdapter.notifyDataSetChanged();
        break;
      }
      if ((this.selectedMode == 0) && (j == 0))
      {
        if (this.photoVideoAdapter == null) {
          break;
        }
        this.photoVideoAdapter.notifyDataSetChanged();
        break;
      }
      if ((this.selectedMode == 1) && (j == 1))
      {
        if (this.documentsAdapter == null) {
          break;
        }
        this.documentsAdapter.notifyDataSetChanged();
        break;
      }
      if ((this.selectedMode == 3) && (j == 3))
      {
        if (this.linksAdapter == null) {
          break;
        }
        this.linksAdapter.notifyDataSetChanged();
        break;
      }
      if ((this.selectedMode != 4) || (j != 4) || (this.audioAdapter == null)) {
        break;
      }
      this.audioAdapter.notifyDataSetChanged();
      break;
      label676:
      paramInt = 8;
      break label515;
      if (paramInt == NotificationCenter.messagesDeleted)
      {
        localObject1 = null;
        if ((int)this.dialog_id < 0) {
          localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(-(int)this.dialog_id));
        }
        i = ((Integer)paramVarArgs[1]).intValue();
        paramInt = 0;
        if (ChatObject.isChannel((TLRPC.Chat)localObject1)) {
          if ((i == 0) && (this.mergeDialogId != 0L)) {
            paramInt = 1;
          }
        }
        while (i == 0) {
          for (;;)
          {
            paramVarArgs = (ArrayList)paramVarArgs[0];
            j = 0;
            paramVarArgs = paramVarArgs.iterator();
            if (!paramVarArgs.hasNext()) {
              break label859;
            }
            localObject1 = (Integer)paramVarArgs.next();
            localObject2 = this.sharedMediaData;
            int m = localObject2.length;
            i = 0;
            int k = j;
            for (;;)
            {
              j = k;
              if (i >= m) {
                break;
              }
              if (localObject2[i].deleteMessage(((Integer)localObject1).intValue(), paramInt)) {
                k = 1;
              }
              i += 1;
            }
            if (i != ((TLRPC.Chat)localObject1).id) {
              break;
            }
            paramInt = 0;
          }
        }
        return;
        label859:
        if (j != 0)
        {
          this.scrolling = true;
          if (this.photoAdapter != null) {
            this.photoAdapter.notifyDataSetChanged();
          }
          if (this.videoAdapter != null) {
            this.videoAdapter.notifyDataSetChanged();
          }
          if (this.photoVideoAdapter != null) {
            this.photoVideoAdapter.notifyDataSetChanged();
          }
          if (this.documentsAdapter != null) {
            this.documentsAdapter.notifyDataSetChanged();
          }
          if (this.linksAdapter != null) {
            this.linksAdapter.notifyDataSetChanged();
          }
          if (this.audioAdapter != null) {
            this.audioAdapter.notifyDataSetChanged();
          }
          if ((this.selectedMode == 1) || (this.selectedMode == 3) || (this.selectedMode == 4))
          {
            paramVarArgs = this.searchItem;
            if ((!this.sharedMediaData[this.selectedMode].messages.isEmpty()) && (!this.searching)) {}
            for (paramInt = 0;; paramInt = 8)
            {
              paramVarArgs.setVisibility(paramInt);
              return;
            }
          }
        }
      }
      else if (paramInt == NotificationCenter.didReceivedNewMessages)
      {
        if (((Long)paramVarArgs[0]).longValue() == this.dialog_id)
        {
          paramVarArgs = (ArrayList)paramVarArgs[1];
          if ((int)this.dialog_id == 0) {}
          for (bool = true;; bool = false)
          {
            paramInt = 0;
            paramVarArgs = paramVarArgs.iterator();
            for (;;)
            {
              if (!paramVarArgs.hasNext()) {
                break label1143;
              }
              localObject1 = (MessageObject)paramVarArgs.next();
              if (((MessageObject)localObject1).messageOwner.media != null)
              {
                i = SharedMediaQuery.getMediaType(((MessageObject)localObject1).messageOwner);
                if (i == -1) {
                  break;
                }
                if (this.sharedMediaData[i].addMessage((MessageObject)localObject1, true, bool)) {
                  paramInt = 1;
                }
              }
            }
          }
          label1143:
          if (paramInt != 0)
          {
            this.scrolling = true;
            if (this.photoAdapter != null) {
              this.photoAdapter.notifyDataSetChanged();
            }
            if (this.videoAdapter != null) {
              this.videoAdapter.notifyDataSetChanged();
            }
            if (this.photoVideoAdapter != null) {
              this.photoVideoAdapter.notifyDataSetChanged();
            }
            if (this.documentsAdapter != null) {
              this.documentsAdapter.notifyDataSetChanged();
            }
            if (this.linksAdapter != null) {
              this.linksAdapter.notifyDataSetChanged();
            }
            if (this.audioAdapter != null) {
              this.audioAdapter.notifyDataSetChanged();
            }
            if ((this.selectedMode == 1) || (this.selectedMode == 3) || (this.selectedMode == 4))
            {
              paramVarArgs = this.searchItem;
              if ((!this.sharedMediaData[this.selectedMode].messages.isEmpty()) && (!this.searching)) {}
              for (paramInt = 0;; paramInt = 8)
              {
                paramVarArgs.setVisibility(paramInt);
                return;
              }
            }
          }
        }
      }
      else if (paramInt == NotificationCenter.messageReceivedByServer)
      {
        localObject1 = (Integer)paramVarArgs[0];
        paramVarArgs = (Integer)paramVarArgs[1];
        localObject2 = this.sharedMediaData;
        i = localObject2.length;
        paramInt = 0;
        while (paramInt < i)
        {
          localObject2[paramInt].replaceMid(((Integer)localObject1).intValue(), paramVarArgs.intValue());
          paramInt += 1;
        }
      }
    }
  }
  
  public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    if ((paramMessageObject == null) || (this.listView == null) || ((this.selectedMode != 0) && (this.selectedMode == 5) && (this.selectedMode == 6))) {
      return null;
    }
    int j = this.listView.getChildCount();
    paramInt = 0;
    label48:
    Object localObject;
    int i;
    if (paramInt < j)
    {
      paramFileLocation = this.listView.getChildAt(paramInt);
      if ((paramFileLocation instanceof SharedPhotoVideoCell))
      {
        localObject = (SharedPhotoVideoCell)paramFileLocation;
        i = 0;
      }
    }
    for (;;)
    {
      MessageObject localMessageObject;
      if (i < 6)
      {
        localMessageObject = ((SharedPhotoVideoCell)localObject).getMessageObject(i);
        if (localMessageObject != null) {}
      }
      else
      {
        paramInt += 1;
        break label48;
        break;
      }
      paramFileLocation = ((SharedPhotoVideoCell)localObject).getImageView(i);
      if (localMessageObject.getId() == paramMessageObject.getId())
      {
        paramMessageObject = new int[2];
        paramFileLocation.getLocationInWindow(paramMessageObject);
        localObject = new PhotoViewer.PlaceProviderObject();
        ((PhotoViewer.PlaceProviderObject)localObject).viewX = paramMessageObject[0];
        ((PhotoViewer.PlaceProviderObject)localObject).viewY = (paramMessageObject[1] - AndroidUtilities.statusBarHeight);
        ((PhotoViewer.PlaceProviderObject)localObject).parentView = this.listView;
        ((PhotoViewer.PlaceProviderObject)localObject).imageReceiver = paramFileLocation.getImageReceiver();
        ((PhotoViewer.PlaceProviderObject)localObject).thumb = ((PhotoViewer.PlaceProviderObject)localObject).imageReceiver.getBitmap();
        ((PhotoViewer.PlaceProviderObject)localObject).parentView.getLocationInWindow(paramMessageObject);
        ((PhotoViewer.PlaceProviderObject)localObject).clipTopAddition = AndroidUtilities.dp(40.0F);
        return (PhotoViewer.PlaceProviderObject)localObject;
      }
      i += 1;
    }
  }
  
  public int getSelectedCount()
  {
    return 0;
  }
  
  public Bitmap getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    return null;
  }
  
  public boolean isPhotoChecked(int paramInt)
  {
    return false;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (this.listView != null) {
      this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          MihanMediaActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
          MihanMediaActivity.this.fixLayoutInternal();
          return true;
        }
      });
    }
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDeleted);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedNewMessages);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
    this.dialog_id = getArguments().getLong("dialog_id", 0L);
    this.selectedMode = getArguments().getInt("selected_mode", 0);
    int i = 0;
    if (i < this.sharedMediaData.length)
    {
      this.sharedMediaData[i] = new SharedMediaData(null);
      int[] arrayOfInt = this.sharedMediaData[i].max_id;
      if ((int)this.dialog_id == 0) {}
      for (int j = Integer.MIN_VALUE;; j = Integer.MAX_VALUE)
      {
        arrayOfInt[0] = j;
        if ((this.mergeDialogId != 0L) && (this.info != null))
        {
          this.sharedMediaData[i].max_id[1] = this.info.migrated_from_max_id;
          this.sharedMediaData[i].endReached[1] = 0;
        }
        i += 1;
        break;
      }
    }
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mediaDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedNewMessages);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDeleted);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
  }
  
  public void onPause()
  {
    super.onPause();
    if (this.dropDownContainer != null) {
      this.dropDownContainer.closeSubMenu();
    }
  }
  
  public void onResume()
  {
    super.onResume();
    this.scrolling = true;
    if (this.photoAdapter != null) {
      this.photoAdapter.notifyDataSetChanged();
    }
    if (this.videoAdapter != null) {
      this.photoAdapter.notifyDataSetChanged();
    }
    if (this.photoVideoAdapter != null) {
      this.photoVideoAdapter.notifyDataSetChanged();
    }
    if (this.documentsAdapter != null) {
      this.documentsAdapter.notifyDataSetChanged();
    }
    if (this.linksAdapter != null) {
      this.linksAdapter.notifyDataSetChanged();
    }
    fixLayoutInternal();
  }
  
  public void sendButtonPressed(int paramInt) {}
  
  public void setChatInfo(TLRPC.ChatFull paramChatFull)
  {
    this.info = paramChatFull;
    if ((this.info != null) && (this.info.migrated_from_chat_id != 0)) {
      this.mergeDialogId = (-this.info.migrated_from_chat_id);
    }
  }
  
  public void setPhotoChecked(int paramInt) {}
  
  public void updatePhotoAtIndex(int paramInt) {}
  
  public void willHidePhotoViewer() {}
  
  public void willSwitchFromPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt) {}
  
  public class MediaSearchAdapter
    extends BaseFragmentAdapter
  {
    private int currentType;
    protected ArrayList<MessageObject> globalSearch = new ArrayList();
    private int lastReqId;
    private Context mContext;
    private int reqId = 0;
    private ArrayList<MessageObject> searchResult = new ArrayList();
    private Timer searchTimer;
    
    public MediaSearchAdapter(Context paramContext, int paramInt)
    {
      this.mContext = paramContext;
      this.currentType = paramInt;
    }
    
    private void processSearch(final String paramString)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          final Object localObject;
          if (!MihanMediaActivity.SharedMediaData.access$3400(MihanMediaActivity.this.sharedMediaData[MihanMediaActivity.MediaSearchAdapter.this.currentType]).isEmpty())
          {
            if ((MihanMediaActivity.MediaSearchAdapter.this.currentType != 1) && (MihanMediaActivity.MediaSearchAdapter.this.currentType != 4)) {
              break label194;
            }
            localObject = (MessageObject)MihanMediaActivity.SharedMediaData.access$3400(MihanMediaActivity.this.sharedMediaData[MihanMediaActivity.MediaSearchAdapter.this.currentType]).get(MihanMediaActivity.SharedMediaData.access$3400(MihanMediaActivity.this.sharedMediaData[MihanMediaActivity.MediaSearchAdapter.this.currentType]).size() - 1);
            MihanMediaActivity.MediaSearchAdapter.this.queryServerSearch(paramString, ((MessageObject)localObject).getId(), ((MessageObject)localObject).getDialogId());
          }
          for (;;)
          {
            if ((MihanMediaActivity.MediaSearchAdapter.this.currentType == 1) || (MihanMediaActivity.MediaSearchAdapter.this.currentType == 4))
            {
              localObject = new ArrayList();
              ((ArrayList)localObject).addAll(MihanMediaActivity.SharedMediaData.access$3400(MihanMediaActivity.this.sharedMediaData[MihanMediaActivity.MediaSearchAdapter.this.currentType]));
              Utilities.searchQueue.postRunnable(new Runnable()
              {
                public void run()
                {
                  Object localObject3 = MihanMediaActivity.MediaSearchAdapter.3.this.val$query.trim().toLowerCase();
                  if (((String)localObject3).length() == 0)
                  {
                    MihanMediaActivity.MediaSearchAdapter.this.updateSearchResults(new ArrayList());
                    return;
                  }
                  Object localObject2 = LocaleController.getInstance().getTranslitString((String)localObject3);
                  Object localObject1;
                  if (!((String)localObject3).equals(localObject2))
                  {
                    localObject1 = localObject2;
                    if (((String)localObject2).length() != 0) {}
                  }
                  else
                  {
                    localObject1 = null;
                  }
                  int i;
                  label128:
                  MessageObject localMessageObject;
                  int j;
                  label154:
                  CharSequence localCharSequence;
                  if (localObject1 != null)
                  {
                    i = 1;
                    localObject2 = new String[i + 1];
                    localObject2[0] = localObject3;
                    if (localObject1 != null) {
                      localObject2[MihanMediaActivity.MediaSearchAdapter.this.currentType] = localObject1;
                    }
                    localObject3 = new ArrayList();
                    i = 0;
                    if (i >= localObject.size()) {
                      break label409;
                    }
                    localMessageObject = (MessageObject)localObject.get(i);
                    j = 0;
                    if (j >= localObject2.length) {
                      break label220;
                    }
                    localCharSequence = localObject2[j];
                    localObject1 = localMessageObject.getDocumentName();
                    if ((localObject1 != null) && (((String)localObject1).length() != 0)) {
                      break label199;
                    }
                  }
                  label199:
                  label220:
                  do
                  {
                    j += 1;
                    break label154;
                    i = 0;
                    break;
                    if (((String)localObject1).toLowerCase().contains(localCharSequence))
                    {
                      ((ArrayList)localObject3).add(localMessageObject);
                      i += 1;
                      break label128;
                    }
                  } while (MihanMediaActivity.MediaSearchAdapter.this.currentType != 4);
                  label265:
                  boolean bool3;
                  boolean bool2;
                  int k;
                  if (localMessageObject.type == 0)
                  {
                    localObject1 = localMessageObject.messageOwner.media.webpage.document;
                    bool3 = false;
                    bool2 = false;
                    k = 0;
                  }
                  for (;;)
                  {
                    boolean bool1 = bool3;
                    if (k < ((TLRPC.Document)localObject1).attributes.size())
                    {
                      TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)((TLRPC.Document)localObject1).attributes.get(k);
                      if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio)) {
                        break label402;
                      }
                      if (localDocumentAttribute.performer != null) {
                        bool2 = localDocumentAttribute.performer.toLowerCase().contains(localCharSequence);
                      }
                      bool1 = bool2;
                      if (!bool2)
                      {
                        bool1 = bool2;
                        if (localDocumentAttribute.title != null) {
                          bool1 = localDocumentAttribute.title.toLowerCase().contains(localCharSequence);
                        }
                      }
                    }
                    if (!bool1) {
                      break;
                    }
                    ((ArrayList)localObject3).add(localMessageObject);
                    break label220;
                    localObject1 = localMessageObject.messageOwner.media.document;
                    break label265;
                    label402:
                    k += 1;
                  }
                  label409:
                  MihanMediaActivity.MediaSearchAdapter.this.updateSearchResults((ArrayList)localObject3);
                }
              });
            }
            return;
            label194:
            if (MihanMediaActivity.MediaSearchAdapter.this.currentType == 3) {
              MihanMediaActivity.MediaSearchAdapter.this.queryServerSearch(paramString, 0, MihanMediaActivity.this.dialog_id);
            }
          }
        }
      });
    }
    
    private void updateSearchResults(final ArrayList<MessageObject> paramArrayList)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          MihanMediaActivity.MediaSearchAdapter.access$5202(MihanMediaActivity.MediaSearchAdapter.this, paramArrayList);
          MihanMediaActivity.MediaSearchAdapter.this.notifyDataSetChanged();
        }
      });
    }
    
    public boolean areAllItemsEnabled()
    {
      return false;
    }
    
    public int getCount()
    {
      int j = this.searchResult.size();
      int k = this.globalSearch.size();
      int i = j;
      if (k != 0) {
        i = j + k;
      }
      return i;
    }
    
    public MessageObject getItem(int paramInt)
    {
      if (paramInt < this.searchResult.size()) {
        return (MessageObject)this.searchResult.get(paramInt);
      }
      return (MessageObject)this.globalSearch.get(paramInt - this.searchResult.size());
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      boolean bool4 = true;
      boolean bool3 = true;
      boolean bool5 = true;
      boolean bool2 = true;
      HashMap[] arrayOfHashMap;
      if ((this.currentType == 1) || (this.currentType == 4))
      {
        paramViewGroup = paramView;
        if (paramView == null) {
          paramViewGroup = new SharedDocumentCell(this.mContext);
        }
        paramView = (SharedDocumentCell)paramViewGroup;
        localMessageObject = getItem(paramInt);
        if (paramInt != getCount() - 1)
        {
          bool1 = true;
          paramView.setDocument(localMessageObject, bool1);
          if (!MihanMediaActivity.this.actionBar.isActionModeShowed()) {
            break label177;
          }
          arrayOfHashMap = MihanMediaActivity.this.selectedFiles;
          if (localMessageObject.getDialogId() != MihanMediaActivity.this.dialog_id) {
            break label166;
          }
          paramInt = 0;
          label119:
          bool3 = arrayOfHashMap[paramInt].containsKey(Integer.valueOf(localMessageObject.getId()));
          if (MihanMediaActivity.this.scrolling) {
            break label171;
          }
          bool1 = bool2;
          label150:
          paramView.setChecked(bool3, bool1);
        }
      }
      label166:
      label171:
      label177:
      do
      {
        return paramViewGroup;
        bool1 = false;
        break;
        paramInt = 1;
        break label119;
        bool1 = false;
        break label150;
        if (!MihanMediaActivity.this.scrolling) {}
        for (bool1 = bool4;; bool1 = false)
        {
          paramView.setChecked(false, bool1);
          return paramViewGroup;
        }
        paramViewGroup = paramView;
      } while (this.currentType != 3);
      paramViewGroup = paramView;
      if (paramView == null)
      {
        paramViewGroup = new SharedLinkCell(this.mContext);
        ((SharedLinkCell)paramViewGroup).setDelegate(new SharedLinkCell.SharedLinkCellDelegate()
        {
          public boolean canPerformActions()
          {
            return !MihanMediaActivity.this.actionBar.isActionModeShowed();
          }
          
          public void needOpenWebView(TLRPC.WebPage paramAnonymousWebPage)
          {
            MihanMediaActivity.this.openWebView(paramAnonymousWebPage);
          }
        });
      }
      paramView = (SharedLinkCell)paramViewGroup;
      MessageObject localMessageObject = getItem(paramInt);
      if (paramInt != getCount() - 1)
      {
        bool1 = true;
        paramView.setLink(localMessageObject, bool1);
        if (!MihanMediaActivity.this.actionBar.isActionModeShowed()) {
          break label380;
        }
        arrayOfHashMap = MihanMediaActivity.this.selectedFiles;
        if (localMessageObject.getDialogId() != MihanMediaActivity.this.dialog_id) {
          break label369;
        }
        paramInt = 0;
        label322:
        bool2 = arrayOfHashMap[paramInt].containsKey(Integer.valueOf(localMessageObject.getId()));
        if (MihanMediaActivity.this.scrolling) {
          break label374;
        }
      }
      label369:
      label374:
      for (boolean bool1 = bool3;; bool1 = false)
      {
        paramView.setChecked(bool2, bool1);
        return paramViewGroup;
        bool1 = false;
        break;
        paramInt = 1;
        break label322;
      }
      label380:
      if (!MihanMediaActivity.this.scrolling) {}
      for (bool1 = bool5;; bool1 = false)
      {
        paramView.setChecked(false, bool1);
        return paramViewGroup;
      }
    }
    
    public int getViewTypeCount()
    {
      return 1;
    }
    
    public boolean hasStableIds()
    {
      return true;
    }
    
    public boolean isEmpty()
    {
      return (this.searchResult.isEmpty()) && (this.globalSearch.isEmpty());
    }
    
    public boolean isEnabled(int paramInt)
    {
      return paramInt != this.searchResult.size() + this.globalSearch.size();
    }
    
    public boolean isGlobalSearch(int paramInt)
    {
      int i = this.searchResult.size();
      int j = this.globalSearch.size();
      if ((paramInt >= 0) && (paramInt < i)) {}
      while ((paramInt <= i) || (paramInt > j + i)) {
        return false;
      }
      return true;
    }
    
    public void queryServerSearch(String paramString, final int paramInt, long paramLong)
    {
      final int i = (int)paramLong;
      if (i == 0) {
        return;
      }
      if (this.reqId != 0)
      {
        ConnectionsManager.getInstance().cancelRequest(this.reqId, true);
        this.reqId = 0;
      }
      if ((paramString == null) || (paramString.length() == 0))
      {
        this.globalSearch.clear();
        this.lastReqId = 0;
        notifyDataSetChanged();
        return;
      }
      TLRPC.TL_messages_search localTL_messages_search = new TLRPC.TL_messages_search();
      localTL_messages_search.offset = 0;
      localTL_messages_search.limit = 50;
      localTL_messages_search.max_id = paramInt;
      if (this.currentType == 1) {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterDocument();
      }
      for (;;)
      {
        localTL_messages_search.q = paramString;
        localTL_messages_search.peer = MessagesController.getInputPeer(i);
        if (localTL_messages_search.peer == null) {
          break;
        }
        i = this.lastReqId + 1;
        this.lastReqId = i;
        this.reqId = ConnectionsManager.getInstance().sendRequest(localTL_messages_search, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            final ArrayList localArrayList = new ArrayList();
            if (paramAnonymousTL_error == null)
            {
              paramAnonymousTLObject = (TLRPC.messages_Messages)paramAnonymousTLObject;
              int i = 0;
              if (i < paramAnonymousTLObject.messages.size())
              {
                paramAnonymousTL_error = (TLRPC.Message)paramAnonymousTLObject.messages.get(i);
                if ((paramInt != 0) && (paramAnonymousTL_error.id > paramInt)) {}
                for (;;)
                {
                  i += 1;
                  break;
                  localArrayList.add(new MessageObject(paramAnonymousTL_error, null, false));
                }
              }
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if (MihanMediaActivity.MediaSearchAdapter.1.this.val$currentReqId == MihanMediaActivity.MediaSearchAdapter.this.lastReqId)
                {
                  MihanMediaActivity.MediaSearchAdapter.this.globalSearch = localArrayList;
                  MihanMediaActivity.MediaSearchAdapter.this.notifyDataSetChanged();
                }
                MihanMediaActivity.MediaSearchAdapter.access$4602(MihanMediaActivity.MediaSearchAdapter.this, 0);
              }
            });
          }
        }, 2);
        ConnectionsManager.getInstance().bindRequestToGuid(this.reqId, MihanMediaActivity.this.classGuid);
        return;
        if (this.currentType == 3) {
          localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterUrl();
        } else if (this.currentType == 4) {
          localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterMusic();
        }
      }
    }
    
    public void search(final String paramString)
    {
      try
      {
        if (this.searchTimer != null) {
          this.searchTimer.cancel();
        }
        if (paramString == null)
        {
          this.searchResult.clear();
          notifyDataSetChanged();
          return;
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new TimerTask()
        {
          public void run()
          {
            try
            {
              MihanMediaActivity.MediaSearchAdapter.this.searchTimer.cancel();
              MihanMediaActivity.MediaSearchAdapter.access$4802(MihanMediaActivity.MediaSearchAdapter.this, null);
              MihanMediaActivity.MediaSearchAdapter.this.processSearch(paramString);
              return;
            }
            catch (Exception localException)
            {
              for (;;)
              {
                FileLog.e("tmessages", localException);
              }
            }
          }
        }, 200L, 300L);
      }
    }
  }
  
  private class MihanPhotoVideoAdapter
    extends BaseSectionsAdapter
  {
    private Context mContext;
    private int mType;
    
    public MihanPhotoVideoAdapter(Context paramContext, int paramInt)
    {
      this.mContext = paramContext;
      this.mType = paramInt;
    }
    
    public int getCountForSection(int paramInt)
    {
      if (paramInt < MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.mType]).size()) {
        return (int)Math.ceil(((ArrayList)MihanMediaActivity.SharedMediaData.access$3700(MihanMediaActivity.this.sharedMediaData[this.mType]).get(MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.mType]).get(paramInt))).size() / MihanMediaActivity.this.columnsCount) + 1;
      }
      return 1;
    }
    
    public Object getItem(int paramInt1, int paramInt2)
    {
      return null;
    }
    
    public View getItemView(int paramInt1, int paramInt2, View paramView, ViewGroup paramViewGroup)
    {
      ArrayList localArrayList;
      if (paramInt1 < MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.mType]).size())
      {
        paramViewGroup = (String)MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.mType]).get(paramInt1);
        localArrayList = (ArrayList)MihanMediaActivity.SharedMediaData.access$3700(MihanMediaActivity.this.sharedMediaData[this.mType]).get(paramViewGroup);
        if (paramInt2 == 0)
        {
          paramViewGroup = paramView;
          if (paramView == null) {
            paramViewGroup = new SharedMediaSectionCell(this.mContext);
          }
          paramView = (MessageObject)localArrayList.get(0);
          ((SharedMediaSectionCell)paramViewGroup).setText(LocaleController.getInstance().formatterMonthYear.MihanFormat(paramView.messageOwner.date * 1000L).toUpperCase());
        }
      }
      label203:
      label217:
      label273:
      label349:
      label380:
      label421:
      label427:
      label433:
      label439:
      label470:
      label482:
      do
      {
        return paramViewGroup;
        int i;
        boolean bool1;
        if (paramView == null) {
          if (!MihanMediaActivity.this.cellCache.isEmpty())
          {
            paramView = (View)MihanMediaActivity.this.cellCache.get(0);
            MihanMediaActivity.this.cellCache.remove(0);
            paramViewGroup = (SharedPhotoVideoCell)paramView;
            paramViewGroup.setDelegate(new SharedPhotoVideoCell.SharedPhotoVideoCellDelegate()
            {
              public void didClickItem(SharedPhotoVideoCell paramAnonymousSharedPhotoVideoCell, int paramAnonymousInt1, MessageObject paramAnonymousMessageObject, int paramAnonymousInt2)
              {
                MihanMediaActivity.this.onItemClick(paramAnonymousInt1, paramAnonymousSharedPhotoVideoCell, paramAnonymousMessageObject, paramAnonymousInt2);
              }
              
              public boolean didLongClickItem(SharedPhotoVideoCell paramAnonymousSharedPhotoVideoCell, int paramAnonymousInt1, MessageObject paramAnonymousMessageObject, int paramAnonymousInt2)
              {
                return MihanMediaActivity.this.onItemLongClick(paramAnonymousMessageObject, paramAnonymousSharedPhotoVideoCell, paramAnonymousInt2);
              }
            });
            paramViewGroup.setItemsCount(MihanMediaActivity.this.columnsCount);
            paramInt1 = 0;
            if (paramInt1 >= MihanMediaActivity.this.columnsCount) {
              break label482;
            }
            i = (paramInt2 - 1) * MihanMediaActivity.this.columnsCount + paramInt1;
            if (i >= localArrayList.size()) {
              break label470;
            }
            MessageObject localMessageObject = (MessageObject)localArrayList.get(i);
            if (paramInt2 != 1) {
              break label421;
            }
            bool1 = true;
            paramViewGroup.setIsFirst(bool1);
            paramViewGroup.setItem(paramInt1, MihanMediaActivity.SharedMediaData.access$3400(MihanMediaActivity.this.sharedMediaData[this.mType]).indexOf(localMessageObject), localMessageObject);
            if (!MihanMediaActivity.this.actionBar.isActionModeShowed()) {
              break label439;
            }
            HashMap[] arrayOfHashMap = MihanMediaActivity.this.selectedFiles;
            if (localMessageObject.getDialogId() != MihanMediaActivity.this.dialog_id) {
              break label427;
            }
            i = 0;
            boolean bool2 = arrayOfHashMap[i].containsKey(Integer.valueOf(localMessageObject.getId()));
            if (MihanMediaActivity.this.scrolling) {
              break label433;
            }
            bool1 = true;
            paramViewGroup.setChecked(paramInt1, bool2, bool1);
          }
        }
        for (;;)
        {
          paramInt1 += 1;
          break label217;
          paramView = new SharedPhotoVideoCell(this.mContext);
          break;
          paramViewGroup = (SharedPhotoVideoCell)paramView;
          break label203;
          bool1 = false;
          break label273;
          i = 1;
          break label349;
          bool1 = false;
          break label380;
          if (!MihanMediaActivity.this.scrolling) {}
          for (bool1 = true;; bool1 = false)
          {
            paramViewGroup.setChecked(paramInt1, false, bool1);
            break;
          }
          paramViewGroup.setItem(paramInt1, i, null);
        }
        paramViewGroup.requestLayout();
        return paramView;
        paramViewGroup = paramView;
      } while (paramView != null);
      return new LoadingCell(this.mContext);
    }
    
    public int getItemViewType(int paramInt1, int paramInt2)
    {
      if (paramInt1 < MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.mType]).size())
      {
        if (paramInt2 == 0) {
          return 0;
        }
        return 1;
      }
      return 2;
    }
    
    public int getSectionCount()
    {
      int j = 1;
      int k = MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.mType]).size();
      int i;
      if (!MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.mType]).isEmpty())
      {
        i = j;
        if (MihanMediaActivity.SharedMediaData.access$300(MihanMediaActivity.this.sharedMediaData[this.mType])[0] != 0)
        {
          i = j;
          if (MihanMediaActivity.SharedMediaData.access$300(MihanMediaActivity.this.sharedMediaData[this.mType])[1] == 0) {}
        }
      }
      else
      {
        i = 0;
      }
      return i + k;
    }
    
    public View getSectionHeaderView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      paramViewGroup = paramView;
      if (paramView == null)
      {
        paramViewGroup = new SharedMediaSectionCell(this.mContext);
        paramViewGroup.setBackgroundColor(-1);
      }
      if (paramInt < MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.mType]).size())
      {
        paramView = (String)MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.mType]).get(paramInt);
        paramView = (MessageObject)((ArrayList)MihanMediaActivity.SharedMediaData.access$3700(MihanMediaActivity.this.sharedMediaData[this.mType]).get(paramView)).get(0);
        ((SharedMediaSectionCell)paramViewGroup).setText(LocaleController.getInstance().formatterMonthYear.MihanFormat(paramView.messageOwner.date * 1000L).toUpperCase());
      }
      return paramViewGroup;
    }
    
    public int getViewTypeCount()
    {
      return 3;
    }
    
    public boolean isRowEnabled(int paramInt1, int paramInt2)
    {
      return false;
    }
  }
  
  private class SharedDocumentsAdapter
    extends BaseSectionsAdapter
  {
    private int currentType;
    private Context mContext;
    
    public SharedDocumentsAdapter(Context paramContext, int paramInt)
    {
      this.mContext = paramContext;
      this.currentType = paramInt;
    }
    
    public int getCountForSection(int paramInt)
    {
      if (paramInt < MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.currentType]).size()) {
        return ((ArrayList)MihanMediaActivity.SharedMediaData.access$3700(MihanMediaActivity.this.sharedMediaData[this.currentType]).get(MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.currentType]).get(paramInt))).size() + 1;
      }
      return 1;
    }
    
    public Object getItem(int paramInt1, int paramInt2)
    {
      return null;
    }
    
    public View getItemView(int paramInt1, int paramInt2, View paramView, ViewGroup paramViewGroup)
    {
      Object localObject;
      if (paramInt1 < MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.currentType]).size())
      {
        paramViewGroup = (String)MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.currentType]).get(paramInt1);
        localObject = (ArrayList)MihanMediaActivity.SharedMediaData.access$3700(MihanMediaActivity.this.sharedMediaData[this.currentType]).get(paramViewGroup);
        if (paramInt2 == 0)
        {
          paramViewGroup = paramView;
          if (paramView == null) {
            paramViewGroup = new GreySectionCell(this.mContext);
          }
          paramView = (MessageObject)((ArrayList)localObject).get(0);
          ((GreySectionCell)paramViewGroup).setText(LocaleController.getInstance().formatterMonthYear.MihanFormat(paramView.messageOwner.date * 1000L).toUpperCase());
        }
      }
      label281:
      label328:
      label333:
      label339:
      do
      {
        return paramViewGroup;
        paramViewGroup = paramView;
        if (paramView == null) {
          paramViewGroup = new SharedDocumentCell(this.mContext);
        }
        paramView = (SharedDocumentCell)paramViewGroup;
        MessageObject localMessageObject = (MessageObject)((ArrayList)localObject).get(paramInt2 - 1);
        boolean bool2;
        if ((paramInt2 != ((ArrayList)localObject).size()) || ((paramInt1 == MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.currentType]).size() - 1) && (MihanMediaActivity.SharedMediaData.access$1300(MihanMediaActivity.this.sharedMediaData[this.currentType]))))
        {
          bool1 = true;
          paramView.setDocument(localMessageObject, bool1);
          if (!MihanMediaActivity.this.actionBar.isActionModeShowed()) {
            break label339;
          }
          localObject = MihanMediaActivity.this.selectedFiles;
          if (localMessageObject.getDialogId() != MihanMediaActivity.this.dialog_id) {
            break label328;
          }
          paramInt1 = 0;
          bool2 = localObject[paramInt1].containsKey(Integer.valueOf(localMessageObject.getId()));
          if (MihanMediaActivity.this.scrolling) {
            break label333;
          }
        }
        for (boolean bool1 = true;; bool1 = false)
        {
          paramView.setChecked(bool2, bool1);
          return paramViewGroup;
          bool1 = false;
          break;
          paramInt1 = 1;
          break label281;
        }
        if (!MihanMediaActivity.this.scrolling) {}
        for (bool1 = true;; bool1 = false)
        {
          paramView.setChecked(false, bool1);
          return paramViewGroup;
        }
        paramViewGroup = paramView;
      } while (paramView != null);
      return new LoadingCell(this.mContext);
    }
    
    public int getItemViewType(int paramInt1, int paramInt2)
    {
      if (paramInt1 < MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.currentType]).size())
      {
        if (paramInt2 == 0) {
          return 0;
        }
        return 1;
      }
      return 2;
    }
    
    public int getSectionCount()
    {
      int j = 1;
      int k = MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.currentType]).size();
      int i;
      if (!MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.currentType]).isEmpty())
      {
        i = j;
        if (MihanMediaActivity.SharedMediaData.access$300(MihanMediaActivity.this.sharedMediaData[this.currentType])[0] != 0)
        {
          i = j;
          if (MihanMediaActivity.SharedMediaData.access$300(MihanMediaActivity.this.sharedMediaData[this.currentType])[1] == 0) {}
        }
      }
      else
      {
        i = 0;
      }
      return i + k;
    }
    
    public View getSectionHeaderView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      paramViewGroup = paramView;
      if (paramView == null) {
        paramViewGroup = new GreySectionCell(this.mContext);
      }
      if (paramInt < MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.currentType]).size())
      {
        paramView = (String)MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[this.currentType]).get(paramInt);
        paramView = (MessageObject)((ArrayList)MihanMediaActivity.SharedMediaData.access$3700(MihanMediaActivity.this.sharedMediaData[this.currentType]).get(paramView)).get(0);
        ((GreySectionCell)paramViewGroup).setText(LocaleController.getInstance().formatterMonthYear.MihanFormat(paramView.messageOwner.date * 1000L).toUpperCase());
      }
      return paramViewGroup;
    }
    
    public int getViewTypeCount()
    {
      return 3;
    }
    
    public boolean isRowEnabled(int paramInt1, int paramInt2)
    {
      return paramInt2 != 0;
    }
  }
  
  private class SharedLinksAdapter
    extends BaseSectionsAdapter
  {
    private Context mContext;
    
    public SharedLinksAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getCountForSection(int paramInt)
    {
      if (paramInt < MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[3]).size()) {
        return ((ArrayList)MihanMediaActivity.SharedMediaData.access$3700(MihanMediaActivity.this.sharedMediaData[3]).get(MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[3]).get(paramInt))).size() + 1;
      }
      return 1;
    }
    
    public Object getItem(int paramInt1, int paramInt2)
    {
      return null;
    }
    
    public View getItemView(int paramInt1, int paramInt2, View paramView, ViewGroup paramViewGroup)
    {
      Object localObject;
      if (paramInt1 < MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[3]).size())
      {
        paramViewGroup = (String)MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[3]).get(paramInt1);
        localObject = (ArrayList)MihanMediaActivity.SharedMediaData.access$3700(MihanMediaActivity.this.sharedMediaData[3]).get(paramViewGroup);
        if (paramInt2 == 0)
        {
          paramViewGroup = paramView;
          if (paramView == null) {
            paramViewGroup = new GreySectionCell(this.mContext);
          }
          paramView = (MessageObject)((ArrayList)localObject).get(0);
          ((GreySectionCell)paramViewGroup).setText(LocaleController.getInstance().formatterMonthYear.MihanFormat(paramView.messageOwner.date * 1000L).toUpperCase());
        }
      }
      label282:
      label329:
      label334:
      label340:
      do
      {
        return paramViewGroup;
        paramViewGroup = paramView;
        if (paramView == null)
        {
          paramViewGroup = new SharedLinkCell(this.mContext);
          ((SharedLinkCell)paramViewGroup).setDelegate(new SharedLinkCell.SharedLinkCellDelegate()
          {
            public boolean canPerformActions()
            {
              return !MihanMediaActivity.this.actionBar.isActionModeShowed();
            }
            
            public void needOpenWebView(TLRPC.WebPage paramAnonymousWebPage)
            {
              MihanMediaActivity.this.openWebView(paramAnonymousWebPage);
            }
          });
        }
        paramView = (SharedLinkCell)paramViewGroup;
        MessageObject localMessageObject = (MessageObject)((ArrayList)localObject).get(paramInt2 - 1);
        boolean bool2;
        if ((paramInt2 != ((ArrayList)localObject).size()) || ((paramInt1 == MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[3]).size() - 1) && (MihanMediaActivity.SharedMediaData.access$1300(MihanMediaActivity.this.sharedMediaData[3]))))
        {
          bool1 = true;
          paramView.setLink(localMessageObject, bool1);
          if (!MihanMediaActivity.this.actionBar.isActionModeShowed()) {
            break label340;
          }
          localObject = MihanMediaActivity.this.selectedFiles;
          if (localMessageObject.getDialogId() != MihanMediaActivity.this.dialog_id) {
            break label329;
          }
          paramInt1 = 0;
          bool2 = localObject[paramInt1].containsKey(Integer.valueOf(localMessageObject.getId()));
          if (MihanMediaActivity.this.scrolling) {
            break label334;
          }
        }
        for (boolean bool1 = true;; bool1 = false)
        {
          paramView.setChecked(bool2, bool1);
          return paramViewGroup;
          bool1 = false;
          break;
          paramInt1 = 1;
          break label282;
        }
        if (!MihanMediaActivity.this.scrolling) {}
        for (bool1 = true;; bool1 = false)
        {
          paramView.setChecked(false, bool1);
          return paramViewGroup;
        }
        paramViewGroup = paramView;
      } while (paramView != null);
      return new LoadingCell(this.mContext);
    }
    
    public int getItemViewType(int paramInt1, int paramInt2)
    {
      if (paramInt1 < MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[3]).size())
      {
        if (paramInt2 == 0) {
          return 0;
        }
        return 1;
      }
      return 2;
    }
    
    public int getSectionCount()
    {
      int j = 1;
      int k = MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[3]).size();
      int i;
      if (!MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[3]).isEmpty())
      {
        i = j;
        if (MihanMediaActivity.SharedMediaData.access$300(MihanMediaActivity.this.sharedMediaData[3])[0] != 0)
        {
          i = j;
          if (MihanMediaActivity.SharedMediaData.access$300(MihanMediaActivity.this.sharedMediaData[3])[1] == 0) {}
        }
      }
      else
      {
        i = 0;
      }
      return i + k;
    }
    
    public View getSectionHeaderView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      paramViewGroup = paramView;
      if (paramView == null) {
        paramViewGroup = new GreySectionCell(this.mContext);
      }
      if (paramInt < MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[3]).size())
      {
        paramView = (String)MihanMediaActivity.SharedMediaData.access$3600(MihanMediaActivity.this.sharedMediaData[3]).get(paramInt);
        paramView = (MessageObject)((ArrayList)MihanMediaActivity.SharedMediaData.access$3700(MihanMediaActivity.this.sharedMediaData[3]).get(paramView)).get(0);
        ((GreySectionCell)paramViewGroup).setText(LocaleController.getInstance().formatterMonthYear.MihanFormat(paramView.messageOwner.date * 1000L).toUpperCase());
      }
      return paramViewGroup;
    }
    
    public int getViewTypeCount()
    {
      return 3;
    }
    
    public boolean isRowEnabled(int paramInt1, int paramInt2)
    {
      return paramInt2 != 0;
    }
  }
  
  private class SharedMediaData
  {
    private boolean[] endReached = { 0, 1 };
    private boolean loading;
    private int[] max_id = { 0, 0 };
    private ArrayList<MessageObject> messages = new ArrayList();
    private HashMap<Integer, MessageObject>[] messagesDict = { new HashMap(), new HashMap() };
    private HashMap<String, ArrayList<MessageObject>> sectionArrays = new HashMap();
    private ArrayList<String> sections = new ArrayList();
    private int totalCount;
    
    private SharedMediaData() {}
    
    public boolean addMessage(MessageObject paramMessageObject, boolean paramBoolean1, boolean paramBoolean2)
    {
      if (paramMessageObject.getDialogId() == MihanMediaActivity.this.dialog_id) {}
      for (int i = 0; this.messagesDict[i].containsKey(Integer.valueOf(paramMessageObject.getId())); i = 1) {
        return false;
      }
      ArrayList localArrayList2 = (ArrayList)this.sectionArrays.get(paramMessageObject.monthKey);
      ArrayList localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        this.sectionArrays.put(paramMessageObject.monthKey, localArrayList1);
        if (paramBoolean1) {
          this.sections.add(0, paramMessageObject.monthKey);
        }
      }
      else
      {
        if (!paramBoolean1) {
          break label198;
        }
        localArrayList1.add(0, paramMessageObject);
        this.messages.add(0, paramMessageObject);
        label130:
        this.messagesDict[i].put(Integer.valueOf(paramMessageObject.getId()), paramMessageObject);
        if (paramBoolean2) {
          break label217;
        }
        if (paramMessageObject.getId() > 0) {
          this.max_id[i] = Math.min(paramMessageObject.getId(), this.max_id[i]);
        }
      }
      for (;;)
      {
        return true;
        this.sections.add(paramMessageObject.monthKey);
        break;
        label198:
        localArrayList1.add(paramMessageObject);
        this.messages.add(paramMessageObject);
        break label130;
        label217:
        this.max_id[i] = Math.max(paramMessageObject.getId(), this.max_id[i]);
      }
    }
    
    public boolean deleteMessage(int paramInt1, int paramInt2)
    {
      MessageObject localMessageObject = (MessageObject)this.messagesDict[paramInt2].get(Integer.valueOf(paramInt1));
      if (localMessageObject == null) {}
      ArrayList localArrayList;
      do
      {
        return false;
        localArrayList = (ArrayList)this.sectionArrays.get(localMessageObject.monthKey);
      } while (localArrayList == null);
      localArrayList.remove(localMessageObject);
      this.messages.remove(localMessageObject);
      this.messagesDict[paramInt2].remove(Integer.valueOf(localMessageObject.getId()));
      if (localArrayList.isEmpty())
      {
        this.sectionArrays.remove(localMessageObject.monthKey);
        this.sections.remove(localMessageObject.monthKey);
      }
      this.totalCount -= 1;
      return true;
    }
    
    public void replaceMid(int paramInt1, int paramInt2)
    {
      MessageObject localMessageObject = (MessageObject)this.messagesDict[0].get(Integer.valueOf(paramInt1));
      if (localMessageObject != null)
      {
        this.messagesDict[0].remove(Integer.valueOf(paramInt1));
        this.messagesDict[0].put(Integer.valueOf(paramInt2), localMessageObject);
        localMessageObject.messageOwner.id = paramInt2;
      }
    }
  }
  
  private class SharedPhotoVideoAdapter
    extends BaseSectionsAdapter
  {
    private Context mContext;
    
    public SharedPhotoVideoAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getCountForSection(int paramInt)
    {
      if (paramInt < MihanMediaActivity.access$1000(MihanMediaActivity.this)[0].sections.size()) {
        return (int)Math.ceil(((ArrayList)MihanMediaActivity.access$1000(MihanMediaActivity.this)[0].sectionArrays.get(MihanMediaActivity.access$1000(MihanMediaActivity.this)[0].sections.get(paramInt))).size() / MihanMediaActivity.this.columnsCount) + 1;
      }
      return 1;
    }
    
    public Object getItem(int paramInt1, int paramInt2)
    {
      return null;
    }
    
    public View getItemView(int paramInt1, int paramInt2, View paramView, ViewGroup paramViewGroup)
    {
      ArrayList localArrayList;
      if (paramInt1 < MihanMediaActivity.access$1000(MihanMediaActivity.this)[0].sections.size())
      {
        paramViewGroup = (String)MihanMediaActivity.access$1000(MihanMediaActivity.this)[0].sections.get(paramInt1);
        localArrayList = (ArrayList)MihanMediaActivity.access$1000(MihanMediaActivity.this)[0].sectionArrays.get(paramViewGroup);
        if (paramInt2 == 0)
        {
          paramViewGroup = paramView;
          if (paramView == null) {
            paramViewGroup = new SharedMediaSectionCell(this.mContext);
          }
          paramView = (MessageObject)localArrayList.get(0);
          ((SharedMediaSectionCell)paramViewGroup).setText(LocaleController.getInstance().formatterMonthYear.MihanFormat(paramView.messageOwner.date * 1000L).toUpperCase());
        }
      }
      label194:
      label208:
      label264:
      label337:
      label368:
      label409:
      label415:
      label421:
      label427:
      label458:
      label470:
      do
      {
        return paramViewGroup;
        int i;
        boolean bool1;
        if (paramView == null) {
          if (!MihanMediaActivity.this.cellCache.isEmpty())
          {
            paramView = (View)MihanMediaActivity.this.cellCache.get(0);
            MihanMediaActivity.this.cellCache.remove(0);
            paramViewGroup = (SharedPhotoVideoCell)paramView;
            paramViewGroup.setDelegate(new SharedPhotoVideoCell.SharedPhotoVideoCellDelegate()
            {
              public void didClickItem(SharedPhotoVideoCell paramAnonymousSharedPhotoVideoCell, int paramAnonymousInt1, MessageObject paramAnonymousMessageObject, int paramAnonymousInt2)
              {
                MihanMediaActivity.this.onItemClick(paramAnonymousInt1, paramAnonymousSharedPhotoVideoCell, paramAnonymousMessageObject, paramAnonymousInt2);
              }
              
              public boolean didLongClickItem(SharedPhotoVideoCell paramAnonymousSharedPhotoVideoCell, int paramAnonymousInt1, MessageObject paramAnonymousMessageObject, int paramAnonymousInt2)
              {
                return MihanMediaActivity.this.onItemLongClick(paramAnonymousMessageObject, paramAnonymousSharedPhotoVideoCell, paramAnonymousInt2);
              }
            });
            paramViewGroup.setItemsCount(MihanMediaActivity.this.columnsCount);
            paramInt1 = 0;
            if (paramInt1 >= MihanMediaActivity.this.columnsCount) {
              break label470;
            }
            i = (paramInt2 - 1) * MihanMediaActivity.this.columnsCount + paramInt1;
            if (i >= localArrayList.size()) {
              break label458;
            }
            MessageObject localMessageObject = (MessageObject)localArrayList.get(i);
            if (paramInt2 != 1) {
              break label409;
            }
            bool1 = true;
            paramViewGroup.setIsFirst(bool1);
            paramViewGroup.setItem(paramInt1, MihanMediaActivity.access$1000(MihanMediaActivity.this)[0].messages.indexOf(localMessageObject), localMessageObject);
            if (!MihanMediaActivity.this.actionBar.isActionModeShowed()) {
              break label427;
            }
            HashMap[] arrayOfHashMap = MihanMediaActivity.this.selectedFiles;
            if (localMessageObject.getDialogId() != MihanMediaActivity.this.dialog_id) {
              break label415;
            }
            i = 0;
            boolean bool2 = arrayOfHashMap[i].containsKey(Integer.valueOf(localMessageObject.getId()));
            if (MihanMediaActivity.this.scrolling) {
              break label421;
            }
            bool1 = true;
            paramViewGroup.setChecked(paramInt1, bool2, bool1);
          }
        }
        for (;;)
        {
          paramInt1 += 1;
          break label208;
          paramView = new SharedPhotoVideoCell(this.mContext);
          break;
          paramViewGroup = (SharedPhotoVideoCell)paramView;
          break label194;
          bool1 = false;
          break label264;
          i = 1;
          break label337;
          bool1 = false;
          break label368;
          if (!MihanMediaActivity.this.scrolling) {}
          for (bool1 = true;; bool1 = false)
          {
            paramViewGroup.setChecked(paramInt1, false, bool1);
            break;
          }
          paramViewGroup.setItem(paramInt1, i, null);
        }
        paramViewGroup.requestLayout();
        return paramView;
        paramViewGroup = paramView;
      } while (paramView != null);
      return new LoadingCell(this.mContext);
    }
    
    public int getItemViewType(int paramInt1, int paramInt2)
    {
      if (paramInt1 < MihanMediaActivity.access$1000(MihanMediaActivity.this)[0].sections.size())
      {
        if (paramInt2 == 0) {
          return 0;
        }
        return 1;
      }
      return 2;
    }
    
    public int getSectionCount()
    {
      int j = 1;
      int k = MihanMediaActivity.access$1000(MihanMediaActivity.this)[0].sections.size();
      int i;
      if (!MihanMediaActivity.access$1000(MihanMediaActivity.this)[0].sections.isEmpty())
      {
        i = j;
        if (MihanMediaActivity.access$1000(MihanMediaActivity.this)[0].endReached[0] != 0)
        {
          i = j;
          if (MihanMediaActivity.access$1000(MihanMediaActivity.this)[0].endReached[1] == 0) {}
        }
      }
      else
      {
        i = 0;
      }
      return i + k;
    }
    
    public View getSectionHeaderView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      paramViewGroup = paramView;
      if (paramView == null)
      {
        paramViewGroup = new SharedMediaSectionCell(this.mContext);
        paramViewGroup.setBackgroundColor(-1);
      }
      if (paramInt < MihanMediaActivity.access$1000(MihanMediaActivity.this)[0].sections.size())
      {
        paramView = (String)MihanMediaActivity.access$1000(MihanMediaActivity.this)[0].sections.get(paramInt);
        paramView = (MessageObject)((ArrayList)MihanMediaActivity.access$1000(MihanMediaActivity.this)[0].sectionArrays.get(paramView)).get(0);
        ((SharedMediaSectionCell)paramViewGroup).setText(LocaleController.getInstance().formatterMonthYear.MihanFormat(paramView.messageOwner.date * 1000L).toUpperCase());
      }
      return paramViewGroup;
    }
    
    public int getViewTypeCount()
    {
      return 3;
    }
    
    public boolean isRowEnabled(int paramInt1, int paramInt2)
    {
      return false;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\MihanMediaActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */