package org.telegram.ui.Supergram.UserChanges;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Supergram.Theming.MihanTheme;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class UpdateActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, PhotoViewer.PhotoViewerProvider
{
  private static final int delete = 2;
  private static final int filter = 3;
  private int currentFilterType = 0;
  private UpdateCursorAdapter cursorAdapter;
  private a dataBaseAccess;
  private TextView emptyTextView;
  private ActionBarMenuItem filterItem;
  private ListView listView;
  private boolean paused;
  private TLRPC.User selectedUser;
  protected BackupImageView selectedUserAvatar;
  
  public UpdateActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }
  
  private void forceReload()
  {
    this.cursorAdapter = new UpdateCursorAdapter(getParentActivity(), new a().a(this.currentFilterType, 500));
    this.listView.setAdapter(this.cursorAdapter);
  }
  
  private void openChatActivity()
  {
    Bundle localBundle = new Bundle();
    localBundle.putInt("user_id", this.selectedUser.id);
    if ((MessagesController.checkCanOpenChat(localBundle, this)) && (!ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).contains("hide_" + String.valueOf(this.selectedUser.id)))) {
      presentFragment(new ChatActivity(localBundle), false);
    }
  }
  
  private void showDeleteHistoryConfirmation()
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setMessage(LocaleController.getString("AreYouSureDeleteChanges", 2131166616));
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165338));
    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        UpdateActivity.this.dataBaseAccess.b();
        UpdateActivity.this.forceReload();
      }
    });
    localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
    showDialog(localBuilder.create());
  }
  
  private void updateColors(ActionBarMenu paramActionBarMenu)
  {
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    int i = ((SharedPreferences)localObject).getInt("theme_contact_action_color", MihanTheme.getActionBarColor((SharedPreferences)localObject));
    int j = ((SharedPreferences)localObject).getInt("theme_contact_action_gradient", MihanTheme.getActionBarGradientFlag((SharedPreferences)localObject));
    int k = ((SharedPreferences)localObject).getInt("theme_contact_action_gcolor", MihanTheme.getActionBarGradientColor((SharedPreferences)localObject));
    if (j != 0)
    {
      GradientDrawable localGradientDrawable = MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j));
      this.actionBar.setBackgroundDrawable(localGradientDrawable);
    }
    for (;;)
    {
      i = ((SharedPreferences)localObject).getInt("theme_contact_action_icolor", MihanTheme.getActionBarIconColor((SharedPreferences)localObject));
      this.actionBar.setTitleColor(((SharedPreferences)localObject).getInt("theme_contact_action_tcolor", MihanTheme.getActionBarTitleColor((SharedPreferences)localObject)));
      localObject = ApplicationLoader.applicationContext.getResources().getDrawable(2130837829);
      MihanTheme.setColorFilter((Drawable)localObject, i);
      this.actionBar.setBackButtonDrawable((Drawable)localObject);
      MihanTheme.setColorFilter(paramActionBarMenu.getItem(2).getImageView().getDrawable(), i);
      MihanTheme.setColorFilter(paramActionBarMenu.getItem(3).getImageView().getDrawable(), i);
      return;
      this.actionBar.setBackgroundColor(i);
    }
  }
  
  public boolean cancelButtonPressed()
  {
    return false;
  }
  
  public View createView(Context paramContext)
  {
    this.fragmentView = new FrameLayout(paramContext);
    this.actionBar.setBackButtonImage(2130837812);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("ContactsChanges", 2131166624));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          UpdateActivity.this.finishFragment();
        }
        do
        {
          return;
          if (paramAnonymousInt == 2)
          {
            UpdateActivity.this.showDeleteHistoryConfirmation();
            return;
          }
        } while (paramAnonymousInt != 3);
        UpdateActivity.this.showFilterDialog();
      }
    });
    ActionBarMenu localActionBarMenu = this.actionBar.createMenu();
    localActionBarMenu.addItem(2, 2130837842);
    this.filterItem = localActionBarMenu.addItem(3, 2130837815);
    this.dataBaseAccess = new a();
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    localLinearLayout.setVisibility(4);
    localLinearLayout.setOrientation(1);
    ((FrameLayout)this.fragmentView).addView(localLinearLayout);
    Object localObject = (FrameLayout.LayoutParams)localLinearLayout.getLayoutParams();
    ((FrameLayout.LayoutParams)localObject).width = -1;
    ((FrameLayout.LayoutParams)localObject).height = -1;
    ((FrameLayout.LayoutParams)localObject).gravity = 48;
    localLinearLayout.setLayoutParams((ViewGroup.LayoutParams)localObject);
    localLinearLayout.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.emptyTextView = new TextView(paramContext);
    this.emptyTextView.setTextColor(-8355712);
    this.emptyTextView.setTextSize(1, 20.0F);
    this.emptyTextView.setGravity(17);
    this.emptyTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.emptyTextView.setText(LocaleController.getString("NoContactChanges", 2131166656));
    localLinearLayout.addView(this.emptyTextView);
    localObject = (LinearLayout.LayoutParams)this.emptyTextView.getLayoutParams();
    ((LinearLayout.LayoutParams)localObject).width = -1;
    ((LinearLayout.LayoutParams)localObject).height = -1;
    ((LinearLayout.LayoutParams)localObject).weight = 0.5F;
    this.emptyTextView.setLayoutParams((ViewGroup.LayoutParams)localObject);
    localObject = new FrameLayout(paramContext);
    localLinearLayout.addView((View)localObject);
    LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)((View)localObject).getLayoutParams();
    localLayoutParams.width = -1;
    localLayoutParams.height = -1;
    localLayoutParams.weight = 0.5F;
    ((View)localObject).setLayoutParams(localLayoutParams);
    this.cursorAdapter = new UpdateCursorAdapter(paramContext, new a().a(this.currentFilterType, 500));
    this.listView = new ListView(paramContext);
    paramContext = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    int i = paramContext.getInt("theme_contact_list_color", -1);
    int j = paramContext.getInt("theme_contact_list_gradient", 0);
    int k = paramContext.getInt("theme_contact_list_gcolor", i);
    if (j != 0)
    {
      paramContext = MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j));
      this.listView.setBackgroundDrawable(paramContext);
      localLinearLayout.setBackgroundDrawable(paramContext);
    }
    for (;;)
    {
      this.listView.setEmptyView(localLinearLayout);
      this.listView.setVerticalScrollBarEnabled(false);
      this.listView.setDivider(null);
      this.listView.setDividerHeight(0);
      this.listView.setFastScrollEnabled(true);
      this.listView.setScrollBarStyle(33554432);
      this.listView.setCacheColorHint(0);
      this.listView.setScrollingCacheEnabled(false);
      this.listView.setAdapter(this.cursorAdapter);
      ((FrameLayout)this.fragmentView).addView(this.listView);
      paramContext = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
      paramContext.width = -1;
      paramContext.height = -1;
      this.listView.setLayoutParams(paramContext);
      this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          UpdateActivity.access$302(UpdateActivity.this, MessagesController.getInstance().getUser(Integer.valueOf(UpdateActivity.this.dataBaseAccess.a((Cursor)UpdateActivity.this.cursorAdapter.getItem(paramAnonymousInt)).getUserId())));
          if (UpdateActivity.this.selectedUser != null)
          {
            UpdateActivity.this.selectedUserAvatar = ((UpdateCell)paramAnonymousView).getAvatarImageView();
            UpdateActivity.this.openChatActivity();
          }
        }
      });
      updateColors(localActionBarMenu);
      return this.fragmentView;
      this.listView.setBackgroundColor(i);
      localLinearLayout.setBackgroundColor(i);
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (!this.paused)
    {
      UpdateNotificationUtil.dismissNotification();
      this.dataBaseAccess.a();
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
    }
    forceReload();
  }
  
  public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    if (paramFileLocation == null) {}
    do
    {
      do
      {
        return null;
        if ((this.selectedUser == null) || (this.selectedUser.id == 0)) {
          break;
        }
        paramMessageObject = MessagesController.getInstance().getUser(Integer.valueOf(this.selectedUser.id));
        if ((paramMessageObject == null) || (paramMessageObject.photo == null) || (paramMessageObject.photo.photo_big == null)) {
          break;
        }
        paramMessageObject = paramMessageObject.photo.photo_big;
      } while ((paramMessageObject != null) || (paramMessageObject.local_id != paramFileLocation.local_id) || (paramMessageObject.volume_id != paramFileLocation.volume_id) || (paramMessageObject.dc_id != paramFileLocation.dc_id));
      paramMessageObject = new int[2];
      this.selectedUserAvatar.getLocationInWindow(paramMessageObject);
      paramFileLocation = new PhotoViewer.PlaceProviderObject();
      paramFileLocation.viewX = paramMessageObject[0];
      paramFileLocation.viewY = (paramMessageObject[1] - AndroidUtilities.statusBarHeight);
      paramFileLocation.parentView = this.selectedUserAvatar;
      paramFileLocation.imageReceiver = this.selectedUserAvatar.getImageReceiver();
      paramFileLocation.thumb = paramFileLocation.imageReceiver.getBitmap();
      paramFileLocation.size = -1;
      paramFileLocation.radius = this.selectedUserAvatar.getImageReceiver().getRoundRadius();
      return paramFileLocation;
    } while (0 == 0);
    return null;
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
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
  }
  
  public void onPause()
  {
    super.onPause();
    this.paused = true;
  }
  
  public void onResume()
  {
    super.onResume();
    this.paused = false;
    UpdateNotificationUtil.dismissNotification();
    this.dataBaseAccess.a();
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
  }
  
  public void sendButtonPressed(int paramInt) {}
  
  public void setPhotoChecked(int paramInt) {}
  
  protected void showFilterDialog()
  {
    int i = 0;
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(2131166830);
    String str1 = getParentActivity().getString(2131166615);
    String str2 = getParentActivity().getString(2131166823);
    String str3 = getParentActivity().getString(2131166825);
    String str4 = getParentActivity().getString(2131166824);
    if (this.currentFilterType != 0) {
      i = this.currentFilterType - 1;
    }
    DialogInterface.OnClickListener local5 = new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        if (paramAnonymousInt == 0)
        {
          UpdateActivity.this.filterItem.setIcon(2130837815);
          if (paramAnonymousInt != 0) {
            break label58;
          }
          UpdateActivity.access$702(UpdateActivity.this, 0);
        }
        for (;;)
        {
          UpdateActivity.this.forceReload();
          paramAnonymousDialogInterface.dismiss();
          return;
          UpdateActivity.this.filterItem.setIcon(2130837816);
          break;
          label58:
          if (paramAnonymousInt == 1) {
            UpdateActivity.access$702(UpdateActivity.this, 2);
          } else if (paramAnonymousInt == 2) {
            UpdateActivity.access$702(UpdateActivity.this, 3);
          } else if (paramAnonymousInt == 3) {
            UpdateActivity.access$702(UpdateActivity.this, 4);
          }
        }
      }
    };
    localBuilder.setSingleChoiceItems(new CharSequence[] { str1, str2, str3, str4 }, i, local5);
    showDialog(localBuilder.create());
  }
  
  protected void showUserActionsDialog()
  {
    if ((this.selectedUser.photo == null) || (this.selectedUser.photo.photo_big == null))
    {
      openChatActivity();
      return;
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(ContactsController.formatName(this.selectedUser.first_name, this.selectedUser.last_name));
    String str1 = getParentActivity().getString(2131166841);
    String str2 = getParentActivity().getString(2131166842);
    DialogInterface.OnClickListener local6 = new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        if (paramAnonymousInt == 0) {
          UpdateActivity.this.openChatActivity();
        }
        for (;;)
        {
          paramAnonymousDialogInterface.dismiss();
          return;
          if (paramAnonymousInt == 1)
          {
            PhotoViewer.getInstance().setParentActivity(UpdateActivity.this.getParentActivity());
            PhotoViewer.getInstance().openPhoto(UpdateActivity.this.selectedUser.photo.photo_big, UpdateActivity.this);
          }
        }
      }
    };
    localBuilder.setItems(new CharSequence[] { str1, str2 }, local6);
    showDialog(localBuilder.create());
  }
  
  public void updatePhotoAtIndex(int paramInt) {}
  
  public void willHidePhotoViewer()
  {
    if (this.selectedUserAvatar != null) {
      this.selectedUserAvatar.getImageReceiver().setVisible(true, true);
    }
  }
  
  public void willSwitchFromPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt) {}
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\UserChanges\UpdateActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */