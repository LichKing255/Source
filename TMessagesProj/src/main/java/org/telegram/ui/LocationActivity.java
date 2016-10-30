package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewOutlineProvider;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_messageFwdHeader;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseLocationAdapter.BaseLocationAdapterDelegate;
import org.telegram.ui.Adapters.LocationActivityAdapter;
import org.telegram.ui.Adapters.LocationActivitySearchAdapter;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class LocationActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int map_list_menu_hybrid = 4;
  private static final int map_list_menu_map = 2;
  private static final int map_list_menu_satellite = 3;
  private static final int share = 1;
  private LocationActivityAdapter adapter;
  private AnimatorSet animatorSet;
  private BackupImageView avatarImageView;
  private boolean checkPermission = true;
  private CircleOptions circleOptions;
  private LocationActivityDelegate delegate;
  private TextView distanceTextView;
  private LinearLayout emptyTextLayout;
  private boolean firstWas = false;
  private GoogleMap googleMap;
  private ListView listView;
  private ImageView locationButton;
  private MapView mapView;
  private FrameLayout mapViewClip;
  private ImageView markerImageView;
  private int markerTop;
  private ImageView markerXImageView;
  private MessageObject messageObject;
  private Location myLocation;
  private TextView nameTextView;
  private int overScrollHeight = AndroidUtilities.displaySize.x - ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(66.0F);
  private LocationActivitySearchAdapter searchAdapter;
  private ListView searchListView;
  private boolean searchWas;
  private boolean searching;
  private Location userLocation;
  private boolean userLocationMoved = false;
  private boolean wasResults;
  
  private void fixLayoutInternal(boolean paramBoolean)
  {
    if (this.listView != null) {
      if (!this.actionBar.getOccupyStatusBar()) {
        break label40;
      }
    }
    int j;
    label40:
    for (int i = AndroidUtilities.statusBarHeight;; i = 0)
    {
      i += ActionBar.getCurrentActionBarHeight();
      j = this.fragmentView.getMeasuredHeight();
      if (j != 0) {
        break;
      }
      return;
    }
    this.overScrollHeight = (j - AndroidUtilities.dp(66.0F) - i);
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
    localLayoutParams.topMargin = i;
    this.listView.setLayoutParams(localLayoutParams);
    localLayoutParams = (FrameLayout.LayoutParams)this.mapViewClip.getLayoutParams();
    localLayoutParams.topMargin = i;
    localLayoutParams.height = this.overScrollHeight;
    this.mapViewClip.setLayoutParams(localLayoutParams);
    localLayoutParams = (FrameLayout.LayoutParams)this.searchListView.getLayoutParams();
    localLayoutParams.topMargin = i;
    this.searchListView.setLayoutParams(localLayoutParams);
    this.adapter.setOverScrollHeight(this.overScrollHeight);
    localLayoutParams = (FrameLayout.LayoutParams)this.mapView.getLayoutParams();
    if (localLayoutParams != null)
    {
      localLayoutParams.height = (this.overScrollHeight + AndroidUtilities.dp(10.0F));
      if (this.googleMap != null) {
        this.googleMap.setPadding(0, 0, 0, AndroidUtilities.dp(10.0F));
      }
      this.mapView.setLayoutParams(localLayoutParams);
    }
    this.adapter.notifyDataSetChanged();
    if (paramBoolean)
    {
      this.listView.setSelectionFromTop(0, -(int)(AndroidUtilities.dp(56.0F) * 2.5F + AndroidUtilities.dp(102.0F)));
      updateClipView(this.listView.getFirstVisiblePosition());
      this.listView.post(new Runnable()
      {
        public void run()
        {
          LocationActivity.this.listView.setSelectionFromTop(0, -(int)(AndroidUtilities.dp(56.0F) * 2.5F + AndroidUtilities.dp(102.0F)));
          LocationActivity.this.updateClipView(LocationActivity.this.listView.getFirstVisiblePosition());
        }
      });
      return;
    }
    updateClipView(this.listView.getFirstVisiblePosition());
  }
  
  private Location getLastLocation()
  {
    LocationManager localLocationManager = (LocationManager)ApplicationLoader.applicationContext.getSystemService("location");
    List localList = localLocationManager.getProviders(true);
    Location localLocation = null;
    int i = localList.size() - 1;
    for (;;)
    {
      if (i >= 0)
      {
        localLocation = localLocationManager.getLastKnownLocation((String)localList.get(i));
        if (localLocation == null) {}
      }
      else
      {
        return localLocation;
      }
      i -= 1;
    }
  }
  
  private void positionMarker(Location paramLocation)
  {
    if (paramLocation == null) {}
    LatLng localLatLng;
    do
    {
      do
      {
        do
        {
          return;
          this.myLocation = new Location(paramLocation);
          if (this.messageObject == null) {
            break;
          }
        } while ((this.userLocation == null) || (this.distanceTextView == null));
        float f = paramLocation.distanceTo(this.userLocation);
        if (f < 1000.0F)
        {
          this.distanceTextView.setText(String.format("%d %s", new Object[] { Integer.valueOf((int)f), LocaleController.getString("MetersAway", 2131165950) }));
          return;
        }
        this.distanceTextView.setText(String.format("%.2f %s", new Object[] { Float.valueOf(f / 1000.0F), LocaleController.getString("KMetersAway", 2131165847) }));
        return;
      } while (this.googleMap == null);
      localLatLng = new LatLng(paramLocation.getLatitude(), paramLocation.getLongitude());
      if (this.adapter != null)
      {
        this.adapter.searchGooglePlacesWithQuery(null, this.myLocation);
        this.adapter.setGpsLocation(this.myLocation);
      }
    } while (this.userLocationMoved);
    this.userLocation = new Location(paramLocation);
    if (this.firstWas)
    {
      paramLocation = CameraUpdateFactory.newLatLng(localLatLng);
      this.googleMap.animateCamera(paramLocation);
      return;
    }
    this.firstWas = true;
    paramLocation = CameraUpdateFactory.newLatLngZoom(localLatLng, this.googleMap.getMaxZoomLevel() - 4.0F);
    this.googleMap.moveCamera(paramLocation);
  }
  
  private void showPermissionAlert(boolean paramBoolean)
  {
    if (getParentActivity() == null) {
      return;
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165338));
    if (paramBoolean) {
      localBuilder.setMessage(LocaleController.getString("PermissionNoLocationPosition", 2131166162));
    }
    for (;;)
    {
      localBuilder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", 2131166163), new DialogInterface.OnClickListener()
      {
        @TargetApi(9)
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if (LocationActivity.this.getParentActivity() == null) {
            return;
          }
          try
          {
            paramAnonymousDialogInterface = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            paramAnonymousDialogInterface.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            LocationActivity.this.getParentActivity().startActivity(paramAnonymousDialogInterface);
            return;
          }
          catch (Exception paramAnonymousDialogInterface)
          {
            FileLog.e("tmessages", paramAnonymousDialogInterface);
          }
        }
      });
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166111), null);
      showDialog(localBuilder.create());
      return;
      localBuilder.setMessage(LocaleController.getString("PermissionNoLocation", 2131166161));
    }
  }
  
  private void updateClipView(int paramInt)
  {
    int i = 0;
    int j = 0;
    Object localObject = this.listView.getChildAt(0);
    if (localObject != null)
    {
      if (paramInt == 0)
      {
        paramInt = ((View)localObject).getTop();
        j = this.overScrollHeight;
        if (paramInt >= 0) {
          break label256;
        }
        i = paramInt;
        i = j + i;
        j = paramInt;
      }
      if ((FrameLayout.LayoutParams)this.mapViewClip.getLayoutParams() != null)
      {
        if (i > 0) {
          break label261;
        }
        if (this.mapView.getVisibility() == 0)
        {
          this.mapView.setVisibility(4);
          this.mapViewClip.setVisibility(4);
        }
      }
    }
    for (;;)
    {
      this.mapViewClip.setTranslationY(Math.min(0, j));
      this.mapView.setTranslationY(Math.max(0, -j / 2));
      localObject = this.markerImageView;
      paramInt = -j - AndroidUtilities.dp(42.0F) + i / 2;
      this.markerTop = paramInt;
      ((ImageView)localObject).setTranslationY(paramInt);
      this.markerXImageView.setTranslationY(-j - AndroidUtilities.dp(7.0F) + i / 2);
      if (this.googleMap != null)
      {
        localObject = (FrameLayout.LayoutParams)this.mapView.getLayoutParams();
        if ((localObject != null) && (((FrameLayout.LayoutParams)localObject).height != this.overScrollHeight + AndroidUtilities.dp(10.0F)))
        {
          ((FrameLayout.LayoutParams)localObject).height = (this.overScrollHeight + AndroidUtilities.dp(10.0F));
          this.googleMap.setPadding(0, 0, 0, AndroidUtilities.dp(10.0F));
          this.mapView.setLayoutParams((ViewGroup.LayoutParams)localObject);
        }
      }
      return;
      label256:
      i = 0;
      break;
      label261:
      if (this.mapView.getVisibility() == 4)
      {
        this.mapView.setVisibility(0);
        this.mapViewClip.setVisibility(0);
      }
    }
  }
  
  private void updateSearchInterface()
  {
    if (this.adapter != null) {
      this.adapter.notifyDataSetChanged();
    }
  }
  
  private void updateUserData()
  {
    int i;
    String str;
    Object localObject;
    TLRPC.User localUser;
    TLRPC.Chat localChat;
    AvatarDrawable localAvatarDrawable;
    if ((this.messageObject != null) && (this.avatarImageView != null))
    {
      i = this.messageObject.messageOwner.from_id;
      if (this.messageObject.isForwarded())
      {
        if (this.messageObject.messageOwner.fwd_from.channel_id == 0) {
          break label164;
        }
        i = -this.messageObject.messageOwner.fwd_from.channel_id;
      }
      str = "";
      localObject = null;
      localUser = null;
      localChat = null;
      localAvatarDrawable = null;
      if (i <= 0) {
        break label181;
      }
      localUser = MessagesController.getInstance().getUser(Integer.valueOf(i));
      if (localUser != null)
      {
        localObject = localChat;
        if (localUser.photo != null) {
          localObject = localUser.photo.photo_small;
        }
        localAvatarDrawable = new AvatarDrawable(localUser);
        str = UserObject.getUserName(localUser);
      }
    }
    for (;;)
    {
      if (localAvatarDrawable == null) {
        break label240;
      }
      this.avatarImageView.setImage((TLObject)localObject, null, localAvatarDrawable);
      this.nameTextView.setText(str);
      return;
      label164:
      i = this.messageObject.messageOwner.fwd_from.from_id;
      break;
      label181:
      localChat = MessagesController.getInstance().getChat(Integer.valueOf(-i));
      if (localChat != null)
      {
        localObject = localUser;
        if (localChat.photo != null) {
          localObject = localChat.photo.photo_small;
        }
        localAvatarDrawable = new AvatarDrawable(localChat);
        str = localChat.title;
      }
    }
    label240:
    this.avatarImageView.setImageDrawable(null);
  }
  
  /* Error */
  public View createView(Context paramContext)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 239	org/telegram/ui/LocationActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   4: ldc_w 622
    //   7: invokevirtual 625	org/telegram/ui/ActionBar/ActionBar:setBackButtonImage	(I)V
    //   10: aload_0
    //   11: getfield 239	org/telegram/ui/LocationActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   14: iconst_1
    //   15: invokevirtual 628	org/telegram/ui/ActionBar/ActionBar:setAllowOverlayTitle	(Z)V
    //   18: invokestatic 631	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   21: ifeq +11 -> 32
    //   24: aload_0
    //   25: getfield 239	org/telegram/ui/LocationActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   28: iconst_0
    //   29: invokevirtual 634	org/telegram/ui/ActionBar/ActionBar:setOccupyStatusBar	(Z)V
    //   32: aload_0
    //   33: getfield 239	org/telegram/ui/LocationActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   36: astore 8
    //   38: aload_0
    //   39: getfield 143	org/telegram/ui/LocationActivity:messageObject	Lorg/telegram/messenger/MessageObject;
    //   42: ifnull +1445 -> 1487
    //   45: iconst_1
    //   46: istore 7
    //   48: aload 8
    //   50: iload 7
    //   52: invokevirtual 637	org/telegram/ui/ActionBar/ActionBar:setAddToContainer	(Z)V
    //   55: aload_0
    //   56: getfield 239	org/telegram/ui/LocationActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   59: new 8	org/telegram/ui/LocationActivity$1
    //   62: dup
    //   63: aload_0
    //   64: invokespecial 638	org/telegram/ui/LocationActivity$1:<init>	(Lorg/telegram/ui/LocationActivity;)V
    //   67: invokevirtual 642	org/telegram/ui/ActionBar/ActionBar:setActionBarMenuOnItemClick	(Lorg/telegram/ui/ActionBar/ActionBar$ActionBarMenuOnItemClick;)V
    //   70: aload_0
    //   71: getfield 239	org/telegram/ui/LocationActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   74: invokevirtual 646	org/telegram/ui/ActionBar/ActionBar:createMenu	()Lorg/telegram/ui/ActionBar/ActionBarMenu;
    //   77: astore 8
    //   79: aload_0
    //   80: getfield 143	org/telegram/ui/LocationActivity:messageObject	Lorg/telegram/messenger/MessageObject;
    //   83: ifnull +1429 -> 1512
    //   86: aload_0
    //   87: getfield 143	org/telegram/ui/LocationActivity:messageObject	Lorg/telegram/messenger/MessageObject;
    //   90: getfield 533	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   93: getfield 650	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   96: getfield 653	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   99: ifnull +1394 -> 1493
    //   102: aload_0
    //   103: getfield 143	org/telegram/ui/LocationActivity:messageObject	Lorg/telegram/messenger/MessageObject;
    //   106: getfield 533	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   109: getfield 650	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   112: getfield 653	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   115: invokevirtual 656	java/lang/String:length	()I
    //   118: ifle +1375 -> 1493
    //   121: aload_0
    //   122: getfield 239	org/telegram/ui/LocationActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   125: aload_0
    //   126: getfield 143	org/telegram/ui/LocationActivity:messageObject	Lorg/telegram/messenger/MessageObject;
    //   129: getfield 533	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   132: getfield 650	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   135: getfield 653	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   138: invokevirtual 658	org/telegram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   141: aload_0
    //   142: getfield 143	org/telegram/ui/LocationActivity:messageObject	Lorg/telegram/messenger/MessageObject;
    //   145: getfield 533	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   148: getfield 650	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   151: getfield 661	org/telegram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   154: ifnull +42 -> 196
    //   157: aload_0
    //   158: getfield 143	org/telegram/ui/LocationActivity:messageObject	Lorg/telegram/messenger/MessageObject;
    //   161: getfield 533	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   164: getfield 650	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   167: getfield 661	org/telegram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   170: invokevirtual 656	java/lang/String:length	()I
    //   173: ifle +23 -> 196
    //   176: aload_0
    //   177: getfield 239	org/telegram/ui/LocationActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   180: aload_0
    //   181: getfield 143	org/telegram/ui/LocationActivity:messageObject	Lorg/telegram/messenger/MessageObject;
    //   184: getfield 533	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   187: getfield 650	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   190: getfield 661	org/telegram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   193: invokevirtual 664	org/telegram/ui/ActionBar/ActionBar:setSubtitle	(Ljava/lang/CharSequence;)V
    //   196: aload 8
    //   198: iconst_1
    //   199: ldc_w 665
    //   202: invokevirtual 671	org/telegram/ui/ActionBar/ActionBarMenu:addItem	(II)Lorg/telegram/ui/ActionBar/ActionBarMenuItem;
    //   205: pop
    //   206: aload 8
    //   208: iconst_0
    //   209: ldc_w 672
    //   212: invokevirtual 671	org/telegram/ui/ActionBar/ActionBarMenu:addItem	(II)Lorg/telegram/ui/ActionBar/ActionBarMenuItem;
    //   215: astore 8
    //   217: aload 8
    //   219: iconst_2
    //   220: ldc_w 674
    //   223: ldc_w 675
    //   226: invokestatic 382	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   229: iconst_0
    //   230: invokevirtual 681	org/telegram/ui/ActionBar/ActionBarMenuItem:addSubItem	(ILjava/lang/String;I)Landroid/widget/TextView;
    //   233: pop
    //   234: aload 8
    //   236: iconst_3
    //   237: ldc_w 683
    //   240: ldc_w 684
    //   243: invokestatic 382	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   246: iconst_0
    //   247: invokevirtual 681	org/telegram/ui/ActionBar/ActionBarMenuItem:addSubItem	(ILjava/lang/String;I)Landroid/widget/TextView;
    //   250: pop
    //   251: aload 8
    //   253: iconst_4
    //   254: ldc_w 686
    //   257: ldc_w 687
    //   260: invokestatic 382	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   263: iconst_0
    //   264: invokevirtual 681	org/telegram/ui/ActionBar/ActionBarMenuItem:addSubItem	(ILjava/lang/String;I)Landroid/widget/TextView;
    //   267: pop
    //   268: aload_0
    //   269: new 32	org/telegram/ui/LocationActivity$3
    //   272: dup
    //   273: aload_0
    //   274: aload_1
    //   275: invokespecial 690	org/telegram/ui/LocationActivity$3:<init>	(Lorg/telegram/ui/LocationActivity;Landroid/content/Context;)V
    //   278: putfield 250	org/telegram/ui/LocationActivity:fragmentView	Landroid/view/View;
    //   281: aload_0
    //   282: getfield 250	org/telegram/ui/LocationActivity:fragmentView	Landroid/view/View;
    //   285: checkcast 272	android/widget/FrameLayout
    //   288: astore 8
    //   290: aload_0
    //   291: new 521	android/widget/ImageView
    //   294: dup
    //   295: aload_1
    //   296: invokespecial 691	android/widget/ImageView:<init>	(Landroid/content/Context;)V
    //   299: putfield 203	org/telegram/ui/LocationActivity:locationButton	Landroid/widget/ImageView;
    //   302: aload_0
    //   303: getfield 203	org/telegram/ui/LocationActivity:locationButton	Landroid/widget/ImageView;
    //   306: ldc_w 692
    //   309: invokevirtual 695	android/widget/ImageView:setBackgroundResource	(I)V
    //   312: aload_0
    //   313: getfield 203	org/telegram/ui/LocationActivity:locationButton	Landroid/widget/ImageView;
    //   316: ldc_w 696
    //   319: invokevirtual 699	android/widget/ImageView:setImageResource	(I)V
    //   322: aload_0
    //   323: getfield 203	org/telegram/ui/LocationActivity:locationButton	Landroid/widget/ImageView;
    //   326: getstatic 705	android/widget/ImageView$ScaleType:CENTER	Landroid/widget/ImageView$ScaleType;
    //   329: invokevirtual 709	android/widget/ImageView:setScaleType	(Landroid/widget/ImageView$ScaleType;)V
    //   332: getstatic 714	android/os/Build$VERSION:SDK_INT	I
    //   335: bipush 21
    //   337: if_icmplt +140 -> 477
    //   340: new 716	android/animation/StateListAnimator
    //   343: dup
    //   344: invokespecial 717	android/animation/StateListAnimator:<init>	()V
    //   347: astore 9
    //   349: aload_0
    //   350: getfield 203	org/telegram/ui/LocationActivity:locationButton	Landroid/widget/ImageView;
    //   353: ldc_w 719
    //   356: iconst_2
    //   357: newarray <illegal type>
    //   359: dup
    //   360: iconst_0
    //   361: fconst_2
    //   362: invokestatic 132	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   365: i2f
    //   366: fastore
    //   367: dup
    //   368: iconst_1
    //   369: ldc_w 436
    //   372: invokestatic 132	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   375: i2f
    //   376: fastore
    //   377: invokestatic 725	android/animation/ObjectAnimator:ofFloat	(Ljava/lang/Object;Ljava/lang/String;[F)Landroid/animation/ObjectAnimator;
    //   380: ldc2_w 726
    //   383: invokevirtual 731	android/animation/ObjectAnimator:setDuration	(J)Landroid/animation/ObjectAnimator;
    //   386: astore 10
    //   388: aload 9
    //   390: iconst_1
    //   391: newarray <illegal type>
    //   393: dup
    //   394: iconst_0
    //   395: ldc_w 732
    //   398: iastore
    //   399: aload 10
    //   401: invokevirtual 736	android/animation/StateListAnimator:addState	([ILandroid/animation/Animator;)V
    //   404: aload_0
    //   405: getfield 203	org/telegram/ui/LocationActivity:locationButton	Landroid/widget/ImageView;
    //   408: ldc_w 719
    //   411: iconst_2
    //   412: newarray <illegal type>
    //   414: dup
    //   415: iconst_0
    //   416: ldc_w 436
    //   419: invokestatic 132	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   422: i2f
    //   423: fastore
    //   424: dup
    //   425: iconst_1
    //   426: fconst_2
    //   427: invokestatic 132	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   430: i2f
    //   431: fastore
    //   432: invokestatic 725	android/animation/ObjectAnimator:ofFloat	(Ljava/lang/Object;Ljava/lang/String;[F)Landroid/animation/ObjectAnimator;
    //   435: ldc2_w 726
    //   438: invokevirtual 731	android/animation/ObjectAnimator:setDuration	(J)Landroid/animation/ObjectAnimator;
    //   441: astore 10
    //   443: aload 9
    //   445: iconst_0
    //   446: newarray <illegal type>
    //   448: aload 10
    //   450: invokevirtual 736	android/animation/StateListAnimator:addState	([ILandroid/animation/Animator;)V
    //   453: aload_0
    //   454: getfield 203	org/telegram/ui/LocationActivity:locationButton	Landroid/widget/ImageView;
    //   457: aload 9
    //   459: invokevirtual 740	android/widget/ImageView:setStateListAnimator	(Landroid/animation/StateListAnimator;)V
    //   462: aload_0
    //   463: getfield 203	org/telegram/ui/LocationActivity:locationButton	Landroid/widget/ImageView;
    //   466: new 34	org/telegram/ui/LocationActivity$4
    //   469: dup
    //   470: aload_0
    //   471: invokespecial 741	org/telegram/ui/LocationActivity$4:<init>	(Lorg/telegram/ui/LocationActivity;)V
    //   474: invokevirtual 745	android/widget/ImageView:setOutlineProvider	(Landroid/view/ViewOutlineProvider;)V
    //   477: aload_0
    //   478: getfield 143	org/telegram/ui/LocationActivity:messageObject	Lorg/telegram/messenger/MessageObject;
    //   481: ifnull +1221 -> 1702
    //   484: aload_0
    //   485: new 286	com/google/android/gms/maps/MapView
    //   488: dup
    //   489: aload_1
    //   490: invokespecial 746	com/google/android/gms/maps/MapView:<init>	(Landroid/content/Context;)V
    //   493: putfield 284	org/telegram/ui/LocationActivity:mapView	Lcom/google/android/gms/maps/MapView;
    //   496: aload 8
    //   498: new 748	org/telegram/ui/Components/MapPlaceholderDrawable
    //   501: dup
    //   502: invokespecial 749	org/telegram/ui/Components/MapPlaceholderDrawable:<init>	()V
    //   505: invokevirtual 752	android/widget/FrameLayout:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   508: aload_0
    //   509: getfield 284	org/telegram/ui/LocationActivity:mapView	Lcom/google/android/gms/maps/MapView;
    //   512: aconst_null
    //   513: invokevirtual 756	com/google/android/gms/maps/MapView:onCreate	(Landroid/os/Bundle;)V
    //   516: aload_1
    //   517: invokestatic 762	com/google/android/gms/maps/MapsInitializer:initialize	(Landroid/content/Context;)I
    //   520: pop
    //   521: aload_0
    //   522: aload_0
    //   523: getfield 284	org/telegram/ui/LocationActivity:mapView	Lcom/google/android/gms/maps/MapView;
    //   526: invokevirtual 766	com/google/android/gms/maps/MapView:getMap	()Lcom/google/android/gms/maps/GoogleMap;
    //   529: putfield 139	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   532: new 272	android/widget/FrameLayout
    //   535: dup
    //   536: aload_1
    //   537: invokespecial 767	android/widget/FrameLayout:<init>	(Landroid/content/Context;)V
    //   540: astore 9
    //   542: aload 9
    //   544: ldc_w 768
    //   547: invokevirtual 769	android/widget/FrameLayout:setBackgroundResource	(I)V
    //   550: aload 8
    //   552: aload 9
    //   554: iconst_m1
    //   555: bipush 60
    //   557: bipush 83
    //   559: invokestatic 775	org/telegram/ui/Components/LayoutHelper:createFrame	(III)Landroid/widget/FrameLayout$LayoutParams;
    //   562: invokevirtual 779	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   565: aload 9
    //   567: new 36	org/telegram/ui/LocationActivity$5
    //   570: dup
    //   571: aload_0
    //   572: invokespecial 780	org/telegram/ui/LocationActivity$5:<init>	(Lorg/telegram/ui/LocationActivity;)V
    //   575: invokevirtual 784	android/widget/FrameLayout:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   578: aload_0
    //   579: new 587	org/telegram/ui/Components/BackupImageView
    //   582: dup
    //   583: aload_1
    //   584: invokespecial 785	org/telegram/ui/Components/BackupImageView:<init>	(Landroid/content/Context;)V
    //   587: putfield 527	org/telegram/ui/LocationActivity:avatarImageView	Lorg/telegram/ui/Components/BackupImageView;
    //   590: aload_0
    //   591: getfield 527	org/telegram/ui/LocationActivity:avatarImageView	Lorg/telegram/ui/Components/BackupImageView;
    //   594: ldc_w 786
    //   597: invokestatic 132	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   600: invokevirtual 789	org/telegram/ui/Components/BackupImageView:setRoundRadius	(I)V
    //   603: aload_0
    //   604: getfield 527	org/telegram/ui/LocationActivity:avatarImageView	Lorg/telegram/ui/Components/BackupImageView;
    //   607: astore 10
    //   609: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   612: ifeq +971 -> 1583
    //   615: iconst_5
    //   616: istore 5
    //   618: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   621: ifeq +968 -> 1589
    //   624: fconst_0
    //   625: fstore_2
    //   626: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   629: ifeq +967 -> 1596
    //   632: ldc_w 793
    //   635: fstore_3
    //   636: aload 9
    //   638: aload 10
    //   640: bipush 40
    //   642: ldc_w 794
    //   645: iload 5
    //   647: bipush 48
    //   649: ior
    //   650: fload_2
    //   651: ldc_w 793
    //   654: fload_3
    //   655: fconst_0
    //   656: invokestatic 797	org/telegram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   659: invokevirtual 779	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   662: aload_0
    //   663: new 388	android/widget/TextView
    //   666: dup
    //   667: aload_1
    //   668: invokespecial 798	android/widget/TextView:<init>	(Landroid/content/Context;)V
    //   671: putfield 593	org/telegram/ui/LocationActivity:nameTextView	Landroid/widget/TextView;
    //   674: aload_0
    //   675: getfield 593	org/telegram/ui/LocationActivity:nameTextView	Landroid/widget/TextView;
    //   678: iconst_1
    //   679: ldc_w 799
    //   682: invokevirtual 803	android/widget/TextView:setTextSize	(IF)V
    //   685: aload_0
    //   686: getfield 593	org/telegram/ui/LocationActivity:nameTextView	Landroid/widget/TextView;
    //   689: ldc_w 804
    //   692: invokevirtual 807	android/widget/TextView:setTextColor	(I)V
    //   695: aload_0
    //   696: getfield 593	org/telegram/ui/LocationActivity:nameTextView	Landroid/widget/TextView;
    //   699: iconst_1
    //   700: invokevirtual 810	android/widget/TextView:setMaxLines	(I)V
    //   703: aload_0
    //   704: getfield 593	org/telegram/ui/LocationActivity:nameTextView	Landroid/widget/TextView;
    //   707: ldc_w 812
    //   710: invokestatic 816	org/telegram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   713: invokevirtual 820	android/widget/TextView:setTypeface	(Landroid/graphics/Typeface;)V
    //   716: aload_0
    //   717: getfield 593	org/telegram/ui/LocationActivity:nameTextView	Landroid/widget/TextView;
    //   720: getstatic 826	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   723: invokevirtual 830	android/widget/TextView:setEllipsize	(Landroid/text/TextUtils$TruncateAt;)V
    //   726: aload_0
    //   727: getfield 593	org/telegram/ui/LocationActivity:nameTextView	Landroid/widget/TextView;
    //   730: iconst_1
    //   731: invokevirtual 833	android/widget/TextView:setSingleLine	(Z)V
    //   734: aload_0
    //   735: getfield 593	org/telegram/ui/LocationActivity:nameTextView	Landroid/widget/TextView;
    //   738: astore 10
    //   740: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   743: ifeq +858 -> 1601
    //   746: iconst_5
    //   747: istore 5
    //   749: aload 10
    //   751: iload 5
    //   753: invokevirtual 836	android/widget/TextView:setGravity	(I)V
    //   756: aload_0
    //   757: getfield 593	org/telegram/ui/LocationActivity:nameTextView	Landroid/widget/TextView;
    //   760: astore 10
    //   762: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   765: ifeq +842 -> 1607
    //   768: iconst_5
    //   769: istore 5
    //   771: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   774: ifeq +839 -> 1613
    //   777: ldc_w 793
    //   780: fstore_2
    //   781: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   784: ifeq +836 -> 1620
    //   787: ldc_w 837
    //   790: fstore_3
    //   791: aload 9
    //   793: aload 10
    //   795: bipush -2
    //   797: ldc_w 838
    //   800: iload 5
    //   802: bipush 48
    //   804: ior
    //   805: fload_2
    //   806: ldc_w 288
    //   809: fload_3
    //   810: fconst_0
    //   811: invokestatic 797	org/telegram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   814: invokevirtual 779	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   817: aload_0
    //   818: new 388	android/widget/TextView
    //   821: dup
    //   822: aload_1
    //   823: invokespecial 798	android/widget/TextView:<init>	(Landroid/content/Context;)V
    //   826: putfield 358	org/telegram/ui/LocationActivity:distanceTextView	Landroid/widget/TextView;
    //   829: aload_0
    //   830: getfield 358	org/telegram/ui/LocationActivity:distanceTextView	Landroid/widget/TextView;
    //   833: iconst_1
    //   834: ldc_w 839
    //   837: invokevirtual 803	android/widget/TextView:setTextSize	(IF)V
    //   840: aload_0
    //   841: getfield 358	org/telegram/ui/LocationActivity:distanceTextView	Landroid/widget/TextView;
    //   844: ldc_w 840
    //   847: invokevirtual 807	android/widget/TextView:setTextColor	(I)V
    //   850: aload_0
    //   851: getfield 358	org/telegram/ui/LocationActivity:distanceTextView	Landroid/widget/TextView;
    //   854: iconst_1
    //   855: invokevirtual 810	android/widget/TextView:setMaxLines	(I)V
    //   858: aload_0
    //   859: getfield 358	org/telegram/ui/LocationActivity:distanceTextView	Landroid/widget/TextView;
    //   862: getstatic 826	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   865: invokevirtual 830	android/widget/TextView:setEllipsize	(Landroid/text/TextUtils$TruncateAt;)V
    //   868: aload_0
    //   869: getfield 358	org/telegram/ui/LocationActivity:distanceTextView	Landroid/widget/TextView;
    //   872: iconst_1
    //   873: invokevirtual 833	android/widget/TextView:setSingleLine	(Z)V
    //   876: aload_0
    //   877: getfield 358	org/telegram/ui/LocationActivity:distanceTextView	Landroid/widget/TextView;
    //   880: astore 10
    //   882: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   885: ifeq +742 -> 1627
    //   888: iconst_5
    //   889: istore 5
    //   891: aload 10
    //   893: iload 5
    //   895: invokevirtual 836	android/widget/TextView:setGravity	(I)V
    //   898: aload_0
    //   899: getfield 358	org/telegram/ui/LocationActivity:distanceTextView	Landroid/widget/TextView;
    //   902: astore 10
    //   904: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   907: ifeq +726 -> 1633
    //   910: iconst_5
    //   911: istore 5
    //   913: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   916: ifeq +723 -> 1639
    //   919: ldc_w 793
    //   922: fstore_2
    //   923: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   926: ifeq +720 -> 1646
    //   929: ldc_w 837
    //   932: fstore_3
    //   933: aload 9
    //   935: aload 10
    //   937: bipush -2
    //   939: ldc_w 838
    //   942: iload 5
    //   944: bipush 48
    //   946: ior
    //   947: fload_2
    //   948: ldc_w 841
    //   951: fload_3
    //   952: fconst_0
    //   953: invokestatic 797	org/telegram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   956: invokevirtual 779	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   959: aload_0
    //   960: new 354	android/location/Location
    //   963: dup
    //   964: ldc_w 843
    //   967: invokespecial 846	android/location/Location:<init>	(Ljava/lang/String;)V
    //   970: putfield 235	org/telegram/ui/LocationActivity:userLocation	Landroid/location/Location;
    //   973: aload_0
    //   974: getfield 235	org/telegram/ui/LocationActivity:userLocation	Landroid/location/Location;
    //   977: aload_0
    //   978: getfield 143	org/telegram/ui/LocationActivity:messageObject	Lorg/telegram/messenger/MessageObject;
    //   981: getfield 533	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   984: getfield 650	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   987: getfield 850	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   990: getfield 856	org/telegram/tgnet/TLRPC$GeoPoint:lat	D
    //   993: invokevirtual 860	android/location/Location:setLatitude	(D)V
    //   996: aload_0
    //   997: getfield 235	org/telegram/ui/LocationActivity:userLocation	Landroid/location/Location;
    //   1000: aload_0
    //   1001: getfield 143	org/telegram/ui/LocationActivity:messageObject	Lorg/telegram/messenger/MessageObject;
    //   1004: getfield 533	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1007: getfield 650	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1010: getfield 850	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   1013: getfield 863	org/telegram/tgnet/TLRPC$GeoPoint:_long	D
    //   1016: invokevirtual 866	android/location/Location:setLongitude	(D)V
    //   1019: aload_0
    //   1020: getfield 139	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   1023: ifnull +82 -> 1105
    //   1026: new 404	com/google/android/gms/maps/model/LatLng
    //   1029: dup
    //   1030: aload_0
    //   1031: getfield 235	org/telegram/ui/LocationActivity:userLocation	Landroid/location/Location;
    //   1034: invokevirtual 408	android/location/Location:getLatitude	()D
    //   1037: aload_0
    //   1038: getfield 235	org/telegram/ui/LocationActivity:userLocation	Landroid/location/Location;
    //   1041: invokevirtual 411	android/location/Location:getLongitude	()D
    //   1044: invokespecial 414	com/google/android/gms/maps/model/LatLng:<init>	(DD)V
    //   1047: astore 9
    //   1049: aload_0
    //   1050: getfield 139	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   1053: new 868	com/google/android/gms/maps/model/MarkerOptions
    //   1056: dup
    //   1057: invokespecial 869	com/google/android/gms/maps/model/MarkerOptions:<init>	()V
    //   1060: aload 9
    //   1062: invokevirtual 873	com/google/android/gms/maps/model/MarkerOptions:position	(Lcom/google/android/gms/maps/model/LatLng;)Lcom/google/android/gms/maps/model/MarkerOptions;
    //   1065: ldc_w 874
    //   1068: invokestatic 880	com/google/android/gms/maps/model/BitmapDescriptorFactory:fromResource	(I)Lcom/google/android/gms/maps/model/BitmapDescriptor;
    //   1071: invokevirtual 884	com/google/android/gms/maps/model/MarkerOptions:icon	(Lcom/google/android/gms/maps/model/BitmapDescriptor;)Lcom/google/android/gms/maps/model/MarkerOptions;
    //   1074: invokevirtual 888	com/google/android/gms/maps/GoogleMap:addMarker	(Lcom/google/android/gms/maps/model/MarkerOptions;)Lcom/google/android/gms/maps/model/Marker;
    //   1077: pop
    //   1078: aload 9
    //   1080: aload_0
    //   1081: getfield 139	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   1084: invokevirtual 435	com/google/android/gms/maps/GoogleMap:getMaxZoomLevel	()F
    //   1087: ldc_w 436
    //   1090: fsub
    //   1091: invokestatic 440	com/google/android/gms/maps/CameraUpdateFactory:newLatLngZoom	(Lcom/google/android/gms/maps/model/LatLng;F)Lcom/google/android/gms/maps/CameraUpdate;
    //   1094: astore 9
    //   1096: aload_0
    //   1097: getfield 139	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   1100: aload 9
    //   1102: invokevirtual 443	com/google/android/gms/maps/GoogleMap:moveCamera	(Lcom/google/android/gms/maps/CameraUpdate;)V
    //   1105: new 521	android/widget/ImageView
    //   1108: dup
    //   1109: aload_1
    //   1110: invokespecial 691	android/widget/ImageView:<init>	(Landroid/content/Context;)V
    //   1113: astore_1
    //   1114: aload_1
    //   1115: ldc_w 889
    //   1118: invokevirtual 695	android/widget/ImageView:setBackgroundResource	(I)V
    //   1121: aload_1
    //   1122: ldc_w 890
    //   1125: invokevirtual 699	android/widget/ImageView:setImageResource	(I)V
    //   1128: aload_1
    //   1129: getstatic 705	android/widget/ImageView$ScaleType:CENTER	Landroid/widget/ImageView$ScaleType;
    //   1132: invokevirtual 709	android/widget/ImageView:setScaleType	(Landroid/widget/ImageView$ScaleType;)V
    //   1135: getstatic 714	android/os/Build$VERSION:SDK_INT	I
    //   1138: bipush 21
    //   1140: if_icmplt +128 -> 1268
    //   1143: new 716	android/animation/StateListAnimator
    //   1146: dup
    //   1147: invokespecial 717	android/animation/StateListAnimator:<init>	()V
    //   1150: astore 9
    //   1152: aload_1
    //   1153: ldc_w 719
    //   1156: iconst_2
    //   1157: newarray <illegal type>
    //   1159: dup
    //   1160: iconst_0
    //   1161: fconst_2
    //   1162: invokestatic 132	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1165: i2f
    //   1166: fastore
    //   1167: dup
    //   1168: iconst_1
    //   1169: ldc_w 436
    //   1172: invokestatic 132	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1175: i2f
    //   1176: fastore
    //   1177: invokestatic 725	android/animation/ObjectAnimator:ofFloat	(Ljava/lang/Object;Ljava/lang/String;[F)Landroid/animation/ObjectAnimator;
    //   1180: ldc2_w 726
    //   1183: invokevirtual 731	android/animation/ObjectAnimator:setDuration	(J)Landroid/animation/ObjectAnimator;
    //   1186: astore 10
    //   1188: aload 9
    //   1190: iconst_1
    //   1191: newarray <illegal type>
    //   1193: dup
    //   1194: iconst_0
    //   1195: ldc_w 732
    //   1198: iastore
    //   1199: aload 10
    //   1201: invokevirtual 736	android/animation/StateListAnimator:addState	([ILandroid/animation/Animator;)V
    //   1204: aload_1
    //   1205: ldc_w 719
    //   1208: iconst_2
    //   1209: newarray <illegal type>
    //   1211: dup
    //   1212: iconst_0
    //   1213: ldc_w 436
    //   1216: invokestatic 132	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1219: i2f
    //   1220: fastore
    //   1221: dup
    //   1222: iconst_1
    //   1223: fconst_2
    //   1224: invokestatic 132	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1227: i2f
    //   1228: fastore
    //   1229: invokestatic 725	android/animation/ObjectAnimator:ofFloat	(Ljava/lang/Object;Ljava/lang/String;[F)Landroid/animation/ObjectAnimator;
    //   1232: ldc2_w 726
    //   1235: invokevirtual 731	android/animation/ObjectAnimator:setDuration	(J)Landroid/animation/ObjectAnimator;
    //   1238: astore 10
    //   1240: aload 9
    //   1242: iconst_0
    //   1243: newarray <illegal type>
    //   1245: aload 10
    //   1247: invokevirtual 736	android/animation/StateListAnimator:addState	([ILandroid/animation/Animator;)V
    //   1250: aload_1
    //   1251: aload 9
    //   1253: invokevirtual 740	android/widget/ImageView:setStateListAnimator	(Landroid/animation/StateListAnimator;)V
    //   1256: aload_1
    //   1257: new 38	org/telegram/ui/LocationActivity$6
    //   1260: dup
    //   1261: aload_0
    //   1262: invokespecial 891	org/telegram/ui/LocationActivity$6:<init>	(Lorg/telegram/ui/LocationActivity;)V
    //   1265: invokevirtual 745	android/widget/ImageView:setOutlineProvider	(Landroid/view/ViewOutlineProvider;)V
    //   1268: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   1271: ifeq +395 -> 1666
    //   1274: iconst_3
    //   1275: istore 5
    //   1277: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   1280: ifeq +392 -> 1672
    //   1283: ldc_w 839
    //   1286: fstore_2
    //   1287: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   1290: ifeq +387 -> 1677
    //   1293: fconst_0
    //   1294: fstore_3
    //   1295: aload 8
    //   1297: aload_1
    //   1298: bipush -2
    //   1300: ldc_w 838
    //   1303: iload 5
    //   1305: bipush 80
    //   1307: ior
    //   1308: fload_2
    //   1309: fconst_0
    //   1310: fload_3
    //   1311: ldc_w 892
    //   1314: invokestatic 797	org/telegram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   1317: invokevirtual 779	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1320: aload_1
    //   1321: new 40	org/telegram/ui/LocationActivity$7
    //   1324: dup
    //   1325: aload_0
    //   1326: invokespecial 893	org/telegram/ui/LocationActivity$7:<init>	(Lorg/telegram/ui/LocationActivity;)V
    //   1329: invokevirtual 894	android/widget/ImageView:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   1332: aload_0
    //   1333: getfield 203	org/telegram/ui/LocationActivity:locationButton	Landroid/widget/ImageView;
    //   1336: astore_1
    //   1337: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   1340: ifeq +344 -> 1684
    //   1343: iconst_3
    //   1344: istore 5
    //   1346: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   1349: ifeq +341 -> 1690
    //   1352: ldc_w 839
    //   1355: fstore_2
    //   1356: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   1359: ifeq +336 -> 1695
    //   1362: fconst_0
    //   1363: fstore_3
    //   1364: aload 8
    //   1366: aload_1
    //   1367: bipush -2
    //   1369: ldc_w 838
    //   1372: iload 5
    //   1374: bipush 80
    //   1376: ior
    //   1377: fload_2
    //   1378: fconst_0
    //   1379: fload_3
    //   1380: ldc_w 895
    //   1383: invokestatic 797	org/telegram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   1386: invokevirtual 779	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1389: aload_0
    //   1390: getfield 203	org/telegram/ui/LocationActivity:locationButton	Landroid/widget/ImageView;
    //   1393: new 42	org/telegram/ui/LocationActivity$8
    //   1396: dup
    //   1397: aload_0
    //   1398: invokespecial 896	org/telegram/ui/LocationActivity$8:<init>	(Lorg/telegram/ui/LocationActivity;)V
    //   1401: invokevirtual 894	android/widget/ImageView:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   1404: aload_0
    //   1405: getfield 139	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   1408: ifnull +74 -> 1482
    //   1411: aload_0
    //   1412: getfield 139	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   1415: iconst_1
    //   1416: invokevirtual 899	com/google/android/gms/maps/GoogleMap:setMyLocationEnabled	(Z)V
    //   1419: aload_0
    //   1420: getfield 139	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   1423: invokevirtual 903	com/google/android/gms/maps/GoogleMap:getUiSettings	()Lcom/google/android/gms/maps/UiSettings;
    //   1426: iconst_0
    //   1427: invokevirtual 908	com/google/android/gms/maps/UiSettings:setMyLocationButtonEnabled	(Z)V
    //   1430: aload_0
    //   1431: getfield 139	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   1434: invokevirtual 903	com/google/android/gms/maps/GoogleMap:getUiSettings	()Lcom/google/android/gms/maps/UiSettings;
    //   1437: iconst_0
    //   1438: invokevirtual 911	com/google/android/gms/maps/UiSettings:setZoomControlsEnabled	(Z)V
    //   1441: aload_0
    //   1442: getfield 139	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   1445: invokevirtual 903	com/google/android/gms/maps/GoogleMap:getUiSettings	()Lcom/google/android/gms/maps/UiSettings;
    //   1448: iconst_0
    //   1449: invokevirtual 914	com/google/android/gms/maps/UiSettings:setCompassEnabled	(Z)V
    //   1452: aload_0
    //   1453: getfield 139	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   1456: new 24	org/telegram/ui/LocationActivity$17
    //   1459: dup
    //   1460: aload_0
    //   1461: invokespecial 915	org/telegram/ui/LocationActivity$17:<init>	(Lorg/telegram/ui/LocationActivity;)V
    //   1464: invokevirtual 919	com/google/android/gms/maps/GoogleMap:setOnMyLocationChangeListener	(Lcom/google/android/gms/maps/GoogleMap$OnMyLocationChangeListener;)V
    //   1467: aload_0
    //   1468: invokespecial 921	org/telegram/ui/LocationActivity:getLastLocation	()Landroid/location/Location;
    //   1471: astore_1
    //   1472: aload_0
    //   1473: aload_1
    //   1474: putfield 157	org/telegram/ui/LocationActivity:myLocation	Landroid/location/Location;
    //   1477: aload_0
    //   1478: aload_1
    //   1479: invokespecial 209	org/telegram/ui/LocationActivity:positionMarker	(Landroid/location/Location;)V
    //   1482: aload_0
    //   1483: getfield 250	org/telegram/ui/LocationActivity:fragmentView	Landroid/view/View;
    //   1486: areturn
    //   1487: iconst_0
    //   1488: istore 7
    //   1490: goto -1442 -> 48
    //   1493: aload_0
    //   1494: getfield 239	org/telegram/ui/LocationActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   1497: ldc_w 923
    //   1500: ldc_w 924
    //   1503: invokestatic 382	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1506: invokevirtual 658	org/telegram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   1509: goto -1313 -> 196
    //   1512: aload_0
    //   1513: getfield 239	org/telegram/ui/LocationActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   1516: ldc_w 926
    //   1519: ldc_w 927
    //   1522: invokestatic 382	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1525: invokevirtual 658	org/telegram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   1528: aload 8
    //   1530: iconst_0
    //   1531: ldc_w 928
    //   1534: invokevirtual 671	org/telegram/ui/ActionBar/ActionBarMenu:addItem	(II)Lorg/telegram/ui/ActionBar/ActionBarMenuItem;
    //   1537: iconst_1
    //   1538: invokevirtual 932	org/telegram/ui/ActionBar/ActionBarMenuItem:setIsSearchField	(Z)Lorg/telegram/ui/ActionBar/ActionBarMenuItem;
    //   1541: new 30	org/telegram/ui/LocationActivity$2
    //   1544: dup
    //   1545: aload_0
    //   1546: invokespecial 933	org/telegram/ui/LocationActivity$2:<init>	(Lorg/telegram/ui/LocationActivity;)V
    //   1549: invokevirtual 937	org/telegram/ui/ActionBar/ActionBarMenuItem:setActionBarMenuItemSearchListener	(Lorg/telegram/ui/ActionBar/ActionBarMenuItem$ActionBarMenuItemSearchListener;)Lorg/telegram/ui/ActionBar/ActionBarMenuItem;
    //   1552: invokevirtual 941	org/telegram/ui/ActionBar/ActionBarMenuItem:getSearchField	()Landroid/widget/EditText;
    //   1555: ldc_w 943
    //   1558: ldc_w 944
    //   1561: invokestatic 382	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1564: invokevirtual 949	android/widget/EditText:setHint	(Ljava/lang/CharSequence;)V
    //   1567: goto -1361 -> 206
    //   1570: astore 9
    //   1572: ldc_w 951
    //   1575: aload 9
    //   1577: invokestatic 957	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1580: goto -1048 -> 532
    //   1583: iconst_3
    //   1584: istore 5
    //   1586: goto -968 -> 618
    //   1589: ldc_w 793
    //   1592: fstore_2
    //   1593: goto -967 -> 626
    //   1596: fconst_0
    //   1597: fstore_3
    //   1598: goto -962 -> 636
    //   1601: iconst_3
    //   1602: istore 5
    //   1604: goto -855 -> 749
    //   1607: iconst_3
    //   1608: istore 5
    //   1610: goto -839 -> 771
    //   1613: ldc_w 837
    //   1616: fstore_2
    //   1617: goto -836 -> 781
    //   1620: ldc_w 793
    //   1623: fstore_3
    //   1624: goto -833 -> 791
    //   1627: iconst_3
    //   1628: istore 5
    //   1630: goto -739 -> 891
    //   1633: iconst_3
    //   1634: istore 5
    //   1636: goto -723 -> 913
    //   1639: ldc_w 837
    //   1642: fstore_2
    //   1643: goto -720 -> 923
    //   1646: ldc_w 793
    //   1649: fstore_3
    //   1650: goto -717 -> 933
    //   1653: astore 10
    //   1655: ldc_w 951
    //   1658: aload 10
    //   1660: invokestatic 957	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1663: goto -585 -> 1078
    //   1666: iconst_5
    //   1667: istore 5
    //   1669: goto -392 -> 1277
    //   1672: fconst_0
    //   1673: fstore_2
    //   1674: goto -387 -> 1287
    //   1677: ldc_w 839
    //   1680: fstore_3
    //   1681: goto -386 -> 1295
    //   1684: iconst_5
    //   1685: istore 5
    //   1687: goto -341 -> 1346
    //   1690: fconst_0
    //   1691: fstore_2
    //   1692: goto -336 -> 1356
    //   1695: ldc_w 839
    //   1698: fstore_3
    //   1699: goto -335 -> 1364
    //   1702: aload_0
    //   1703: iconst_0
    //   1704: putfield 227	org/telegram/ui/LocationActivity:searchWas	Z
    //   1707: aload_0
    //   1708: iconst_0
    //   1709: putfield 194	org/telegram/ui/LocationActivity:searching	Z
    //   1712: aload_0
    //   1713: new 272	android/widget/FrameLayout
    //   1716: dup
    //   1717: aload_1
    //   1718: invokespecial 767	android/widget/FrameLayout:<init>	(Landroid/content/Context;)V
    //   1721: putfield 217	org/telegram/ui/LocationActivity:mapViewClip	Landroid/widget/FrameLayout;
    //   1724: aload_0
    //   1725: getfield 217	org/telegram/ui/LocationActivity:mapViewClip	Landroid/widget/FrameLayout;
    //   1728: new 748	org/telegram/ui/Components/MapPlaceholderDrawable
    //   1731: dup
    //   1732: invokespecial 749	org/telegram/ui/Components/MapPlaceholderDrawable:<init>	()V
    //   1735: invokevirtual 752	android/widget/FrameLayout:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   1738: aload_0
    //   1739: getfield 171	org/telegram/ui/LocationActivity:adapter	Lorg/telegram/ui/Adapters/LocationActivityAdapter;
    //   1742: ifnull +10 -> 1752
    //   1745: aload_0
    //   1746: getfield 171	org/telegram/ui/LocationActivity:adapter	Lorg/telegram/ui/Adapters/LocationActivityAdapter;
    //   1749: invokevirtual 960	org/telegram/ui/Adapters/LocationActivityAdapter:destroy	()V
    //   1752: aload_0
    //   1753: getfield 232	org/telegram/ui/LocationActivity:searchAdapter	Lorg/telegram/ui/Adapters/LocationActivitySearchAdapter;
    //   1756: ifnull +10 -> 1766
    //   1759: aload_0
    //   1760: getfield 232	org/telegram/ui/LocationActivity:searchAdapter	Lorg/telegram/ui/Adapters/LocationActivitySearchAdapter;
    //   1763: invokevirtual 963	org/telegram/ui/Adapters/LocationActivitySearchAdapter:destroy	()V
    //   1766: aload_0
    //   1767: new 257	android/widget/ListView
    //   1770: dup
    //   1771: aload_1
    //   1772: invokespecial 964	android/widget/ListView:<init>	(Landroid/content/Context;)V
    //   1775: putfield 213	org/telegram/ui/LocationActivity:listView	Landroid/widget/ListView;
    //   1778: aload_0
    //   1779: getfield 213	org/telegram/ui/LocationActivity:listView	Landroid/widget/ListView;
    //   1782: astore 9
    //   1784: new 279	org/telegram/ui/Adapters/LocationActivityAdapter
    //   1787: dup
    //   1788: aload_1
    //   1789: invokespecial 965	org/telegram/ui/Adapters/LocationActivityAdapter:<init>	(Landroid/content/Context;)V
    //   1792: astore 10
    //   1794: aload_0
    //   1795: aload 10
    //   1797: putfield 171	org/telegram/ui/LocationActivity:adapter	Lorg/telegram/ui/Adapters/LocationActivityAdapter;
    //   1800: aload 9
    //   1802: aload 10
    //   1804: invokevirtual 969	android/widget/ListView:setAdapter	(Landroid/widget/ListAdapter;)V
    //   1807: aload_0
    //   1808: getfield 213	org/telegram/ui/LocationActivity:listView	Landroid/widget/ListView;
    //   1811: iconst_0
    //   1812: invokevirtual 972	android/widget/ListView:setVerticalScrollBarEnabled	(Z)V
    //   1815: aload_0
    //   1816: getfield 213	org/telegram/ui/LocationActivity:listView	Landroid/widget/ListView;
    //   1819: iconst_0
    //   1820: invokevirtual 975	android/widget/ListView:setDividerHeight	(I)V
    //   1823: aload_0
    //   1824: getfield 213	org/telegram/ui/LocationActivity:listView	Landroid/widget/ListView;
    //   1827: aconst_null
    //   1828: invokevirtual 978	android/widget/ListView:setDivider	(Landroid/graphics/drawable/Drawable;)V
    //   1831: aload 8
    //   1833: aload_0
    //   1834: getfield 213	org/telegram/ui/LocationActivity:listView	Landroid/widget/ListView;
    //   1837: iconst_m1
    //   1838: iconst_m1
    //   1839: bipush 51
    //   1841: invokestatic 775	org/telegram/ui/Components/LayoutHelper:createFrame	(III)Landroid/widget/FrameLayout$LayoutParams;
    //   1844: invokevirtual 779	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1847: aload_0
    //   1848: getfield 213	org/telegram/ui/LocationActivity:listView	Landroid/widget/ListView;
    //   1851: new 44	org/telegram/ui/LocationActivity$9
    //   1854: dup
    //   1855: aload_0
    //   1856: invokespecial 979	org/telegram/ui/LocationActivity$9:<init>	(Lorg/telegram/ui/LocationActivity;)V
    //   1859: invokevirtual 983	android/widget/ListView:setOnScrollListener	(Landroid/widget/AbsListView$OnScrollListener;)V
    //   1862: aload_0
    //   1863: getfield 213	org/telegram/ui/LocationActivity:listView	Landroid/widget/ListView;
    //   1866: new 10	org/telegram/ui/LocationActivity$10
    //   1869: dup
    //   1870: aload_0
    //   1871: invokespecial 984	org/telegram/ui/LocationActivity$10:<init>	(Lorg/telegram/ui/LocationActivity;)V
    //   1874: invokevirtual 988	android/widget/ListView:setOnItemClickListener	(Landroid/widget/AdapterView$OnItemClickListener;)V
    //   1877: aload_0
    //   1878: getfield 171	org/telegram/ui/LocationActivity:adapter	Lorg/telegram/ui/Adapters/LocationActivityAdapter;
    //   1881: new 12	org/telegram/ui/LocationActivity$11
    //   1884: dup
    //   1885: aload_0
    //   1886: invokespecial 989	org/telegram/ui/LocationActivity$11:<init>	(Lorg/telegram/ui/LocationActivity;)V
    //   1889: invokevirtual 993	org/telegram/ui/Adapters/LocationActivityAdapter:setDelegate	(Lorg/telegram/ui/Adapters/BaseLocationAdapter$BaseLocationAdapterDelegate;)V
    //   1892: aload_0
    //   1893: getfield 171	org/telegram/ui/LocationActivity:adapter	Lorg/telegram/ui/Adapters/LocationActivityAdapter;
    //   1896: aload_0
    //   1897: getfield 134	org/telegram/ui/LocationActivity:overScrollHeight	I
    //   1900: invokevirtual 282	org/telegram/ui/Adapters/LocationActivityAdapter:setOverScrollHeight	(I)V
    //   1903: aload 8
    //   1905: aload_0
    //   1906: getfield 217	org/telegram/ui/LocationActivity:mapViewClip	Landroid/widget/FrameLayout;
    //   1909: iconst_m1
    //   1910: iconst_m1
    //   1911: bipush 51
    //   1913: invokestatic 775	org/telegram/ui/Components/LayoutHelper:createFrame	(III)Landroid/widget/FrameLayout$LayoutParams;
    //   1916: invokevirtual 779	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1919: aload_0
    //   1920: new 14	org/telegram/ui/LocationActivity$12
    //   1923: dup
    //   1924: aload_0
    //   1925: aload_1
    //   1926: invokespecial 994	org/telegram/ui/LocationActivity$12:<init>	(Lorg/telegram/ui/LocationActivity;Landroid/content/Context;)V
    //   1929: putfield 284	org/telegram/ui/LocationActivity:mapView	Lcom/google/android/gms/maps/MapView;
    //   1932: aload_0
    //   1933: getfield 284	org/telegram/ui/LocationActivity:mapView	Lcom/google/android/gms/maps/MapView;
    //   1936: aconst_null
    //   1937: invokevirtual 756	com/google/android/gms/maps/MapView:onCreate	(Landroid/os/Bundle;)V
    //   1940: aload_1
    //   1941: invokestatic 762	com/google/android/gms/maps/MapsInitializer:initialize	(Landroid/content/Context;)I
    //   1944: pop
    //   1945: aload_0
    //   1946: aload_0
    //   1947: getfield 284	org/telegram/ui/LocationActivity:mapView	Lcom/google/android/gms/maps/MapView;
    //   1950: invokevirtual 766	com/google/android/gms/maps/MapView:getMap	()Lcom/google/android/gms/maps/GoogleMap;
    //   1953: putfield 139	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   1956: new 252	android/view/View
    //   1959: dup
    //   1960: aload_1
    //   1961: invokespecial 995	android/view/View:<init>	(Landroid/content/Context;)V
    //   1964: astore 9
    //   1966: aload 9
    //   1968: ldc_w 996
    //   1971: invokevirtual 997	android/view/View:setBackgroundResource	(I)V
    //   1974: aload_0
    //   1975: getfield 217	org/telegram/ui/LocationActivity:mapViewClip	Landroid/widget/FrameLayout;
    //   1978: aload 9
    //   1980: iconst_m1
    //   1981: iconst_3
    //   1982: bipush 83
    //   1984: invokestatic 775	org/telegram/ui/Components/LayoutHelper:createFrame	(III)Landroid/widget/FrameLayout$LayoutParams;
    //   1987: invokevirtual 779	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1990: aload_0
    //   1991: new 521	android/widget/ImageView
    //   1994: dup
    //   1995: aload_1
    //   1996: invokespecial 691	android/widget/ImageView:<init>	(Landroid/content/Context;)V
    //   1999: putfield 187	org/telegram/ui/LocationActivity:markerImageView	Landroid/widget/ImageView;
    //   2002: aload_0
    //   2003: getfield 187	org/telegram/ui/LocationActivity:markerImageView	Landroid/widget/ImageView;
    //   2006: ldc_w 874
    //   2009: invokevirtual 699	android/widget/ImageView:setImageResource	(I)V
    //   2012: aload_0
    //   2013: getfield 217	org/telegram/ui/LocationActivity:mapViewClip	Landroid/widget/FrameLayout;
    //   2016: aload_0
    //   2017: getfield 187	org/telegram/ui/LocationActivity:markerImageView	Landroid/widget/ImageView;
    //   2020: bipush 24
    //   2022: bipush 42
    //   2024: bipush 49
    //   2026: invokestatic 775	org/telegram/ui/Components/LayoutHelper:createFrame	(III)Landroid/widget/FrameLayout$LayoutParams;
    //   2029: invokevirtual 779	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   2032: aload_0
    //   2033: new 521	android/widget/ImageView
    //   2036: dup
    //   2037: aload_1
    //   2038: invokespecial 691	android/widget/ImageView:<init>	(Landroid/content/Context;)V
    //   2041: putfield 197	org/telegram/ui/LocationActivity:markerXImageView	Landroid/widget/ImageView;
    //   2044: aload_0
    //   2045: getfield 197	org/telegram/ui/LocationActivity:markerXImageView	Landroid/widget/ImageView;
    //   2048: fconst_0
    //   2049: invokevirtual 1000	android/widget/ImageView:setAlpha	(F)V
    //   2052: aload_0
    //   2053: getfield 197	org/telegram/ui/LocationActivity:markerXImageView	Landroid/widget/ImageView;
    //   2056: ldc_w 1001
    //   2059: invokevirtual 699	android/widget/ImageView:setImageResource	(I)V
    //   2062: aload_0
    //   2063: getfield 217	org/telegram/ui/LocationActivity:mapViewClip	Landroid/widget/FrameLayout;
    //   2066: aload_0
    //   2067: getfield 197	org/telegram/ui/LocationActivity:markerXImageView	Landroid/widget/ImageView;
    //   2070: bipush 14
    //   2072: bipush 14
    //   2074: bipush 49
    //   2076: invokestatic 775	org/telegram/ui/Components/LayoutHelper:createFrame	(III)Landroid/widget/FrameLayout$LayoutParams;
    //   2079: invokevirtual 779	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   2082: aload_0
    //   2083: getfield 217	org/telegram/ui/LocationActivity:mapViewClip	Landroid/widget/FrameLayout;
    //   2086: astore 9
    //   2088: aload_0
    //   2089: getfield 203	org/telegram/ui/LocationActivity:locationButton	Landroid/widget/ImageView;
    //   2092: astore 10
    //   2094: getstatic 714	android/os/Build$VERSION:SDK_INT	I
    //   2097: bipush 21
    //   2099: if_icmplt +443 -> 2542
    //   2102: bipush 56
    //   2104: istore 5
    //   2106: getstatic 714	android/os/Build$VERSION:SDK_INT	I
    //   2109: bipush 21
    //   2111: if_icmplt +438 -> 2549
    //   2114: ldc_w 299
    //   2117: fstore_2
    //   2118: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   2121: ifeq +435 -> 2556
    //   2124: iconst_3
    //   2125: istore 6
    //   2127: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   2130: ifeq +432 -> 2562
    //   2133: ldc_w 839
    //   2136: fstore_3
    //   2137: getstatic 792	org/telegram/messenger/LocaleController:isRTL	Z
    //   2140: ifeq +427 -> 2567
    //   2143: fconst_0
    //   2144: fstore 4
    //   2146: aload 9
    //   2148: aload 10
    //   2150: iload 5
    //   2152: fload_2
    //   2153: iload 6
    //   2155: bipush 80
    //   2157: ior
    //   2158: fload_3
    //   2159: fconst_0
    //   2160: fload 4
    //   2162: ldc_w 839
    //   2165: invokestatic 797	org/telegram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   2168: invokevirtual 779	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   2171: aload_0
    //   2172: getfield 203	org/telegram/ui/LocationActivity:locationButton	Landroid/widget/ImageView;
    //   2175: new 16	org/telegram/ui/LocationActivity$13
    //   2178: dup
    //   2179: aload_0
    //   2180: invokespecial 1002	org/telegram/ui/LocationActivity$13:<init>	(Lorg/telegram/ui/LocationActivity;)V
    //   2183: invokevirtual 894	android/widget/ImageView:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   2186: aload_0
    //   2187: getfield 203	org/telegram/ui/LocationActivity:locationButton	Landroid/widget/ImageView;
    //   2190: fconst_0
    //   2191: invokevirtual 1000	android/widget/ImageView:setAlpha	(F)V
    //   2194: aload_0
    //   2195: new 1004	android/widget/LinearLayout
    //   2198: dup
    //   2199: aload_1
    //   2200: invokespecial 1005	android/widget/LinearLayout:<init>	(Landroid/content/Context;)V
    //   2203: putfield 224	org/telegram/ui/LocationActivity:emptyTextLayout	Landroid/widget/LinearLayout;
    //   2206: aload_0
    //   2207: getfield 224	org/telegram/ui/LocationActivity:emptyTextLayout	Landroid/widget/LinearLayout;
    //   2210: bipush 8
    //   2212: invokevirtual 1006	android/widget/LinearLayout:setVisibility	(I)V
    //   2215: aload_0
    //   2216: getfield 224	org/telegram/ui/LocationActivity:emptyTextLayout	Landroid/widget/LinearLayout;
    //   2219: iconst_1
    //   2220: invokevirtual 1009	android/widget/LinearLayout:setOrientation	(I)V
    //   2223: aload 8
    //   2225: aload_0
    //   2226: getfield 224	org/telegram/ui/LocationActivity:emptyTextLayout	Landroid/widget/LinearLayout;
    //   2229: iconst_m1
    //   2230: ldc_w 1010
    //   2233: bipush 51
    //   2235: fconst_0
    //   2236: ldc_w 895
    //   2239: fconst_0
    //   2240: fconst_0
    //   2241: invokestatic 797	org/telegram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   2244: invokevirtual 779	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   2247: aload_0
    //   2248: getfield 224	org/telegram/ui/LocationActivity:emptyTextLayout	Landroid/widget/LinearLayout;
    //   2251: new 18	org/telegram/ui/LocationActivity$14
    //   2254: dup
    //   2255: aload_0
    //   2256: invokespecial 1011	org/telegram/ui/LocationActivity$14:<init>	(Lorg/telegram/ui/LocationActivity;)V
    //   2259: invokevirtual 1015	android/widget/LinearLayout:setOnTouchListener	(Landroid/view/View$OnTouchListener;)V
    //   2262: new 388	android/widget/TextView
    //   2265: dup
    //   2266: aload_1
    //   2267: invokespecial 798	android/widget/TextView:<init>	(Landroid/content/Context;)V
    //   2270: astore 9
    //   2272: aload 9
    //   2274: ldc_w 1016
    //   2277: invokevirtual 807	android/widget/TextView:setTextColor	(I)V
    //   2280: aload 9
    //   2282: iconst_1
    //   2283: ldc_w 786
    //   2286: invokevirtual 803	android/widget/TextView:setTextSize	(IF)V
    //   2289: aload 9
    //   2291: bipush 17
    //   2293: invokevirtual 836	android/widget/TextView:setGravity	(I)V
    //   2296: aload 9
    //   2298: ldc_w 1018
    //   2301: ldc_w 1019
    //   2304: invokestatic 382	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2307: invokevirtual 392	android/widget/TextView:setText	(Ljava/lang/CharSequence;)V
    //   2310: aload_0
    //   2311: getfield 224	org/telegram/ui/LocationActivity:emptyTextLayout	Landroid/widget/LinearLayout;
    //   2314: aload 9
    //   2316: iconst_m1
    //   2317: iconst_m1
    //   2318: ldc_w 1020
    //   2321: invokestatic 1024	org/telegram/ui/Components/LayoutHelper:createLinear	(IIF)Landroid/widget/LinearLayout$LayoutParams;
    //   2324: invokevirtual 1025	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   2327: new 272	android/widget/FrameLayout
    //   2330: dup
    //   2331: aload_1
    //   2332: invokespecial 767	android/widget/FrameLayout:<init>	(Landroid/content/Context;)V
    //   2335: astore 9
    //   2337: aload_0
    //   2338: getfield 224	org/telegram/ui/LocationActivity:emptyTextLayout	Landroid/widget/LinearLayout;
    //   2341: aload 9
    //   2343: iconst_m1
    //   2344: iconst_m1
    //   2345: ldc_w 1020
    //   2348: invokestatic 1024	org/telegram/ui/Components/LayoutHelper:createLinear	(IIF)Landroid/widget/LinearLayout$LayoutParams;
    //   2351: invokevirtual 1025	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   2354: aload_0
    //   2355: new 257	android/widget/ListView
    //   2358: dup
    //   2359: aload_1
    //   2360: invokespecial 964	android/widget/ListView:<init>	(Landroid/content/Context;)V
    //   2363: putfield 220	org/telegram/ui/LocationActivity:searchListView	Landroid/widget/ListView;
    //   2366: aload_0
    //   2367: getfield 220	org/telegram/ui/LocationActivity:searchListView	Landroid/widget/ListView;
    //   2370: bipush 8
    //   2372: invokevirtual 1026	android/widget/ListView:setVisibility	(I)V
    //   2375: aload_0
    //   2376: getfield 220	org/telegram/ui/LocationActivity:searchListView	Landroid/widget/ListView;
    //   2379: iconst_0
    //   2380: invokevirtual 975	android/widget/ListView:setDividerHeight	(I)V
    //   2383: aload_0
    //   2384: getfield 220	org/telegram/ui/LocationActivity:searchListView	Landroid/widget/ListView;
    //   2387: aconst_null
    //   2388: invokevirtual 978	android/widget/ListView:setDivider	(Landroid/graphics/drawable/Drawable;)V
    //   2391: aload_0
    //   2392: getfield 220	org/telegram/ui/LocationActivity:searchListView	Landroid/widget/ListView;
    //   2395: astore 9
    //   2397: new 962	org/telegram/ui/Adapters/LocationActivitySearchAdapter
    //   2400: dup
    //   2401: aload_1
    //   2402: invokespecial 1027	org/telegram/ui/Adapters/LocationActivitySearchAdapter:<init>	(Landroid/content/Context;)V
    //   2405: astore_1
    //   2406: aload_0
    //   2407: aload_1
    //   2408: putfield 232	org/telegram/ui/LocationActivity:searchAdapter	Lorg/telegram/ui/Adapters/LocationActivitySearchAdapter;
    //   2411: aload 9
    //   2413: aload_1
    //   2414: invokevirtual 969	android/widget/ListView:setAdapter	(Landroid/widget/ListAdapter;)V
    //   2417: aload 8
    //   2419: aload_0
    //   2420: getfield 220	org/telegram/ui/LocationActivity:searchListView	Landroid/widget/ListView;
    //   2423: iconst_m1
    //   2424: iconst_m1
    //   2425: bipush 51
    //   2427: invokestatic 775	org/telegram/ui/Components/LayoutHelper:createFrame	(III)Landroid/widget/FrameLayout$LayoutParams;
    //   2430: invokevirtual 779	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   2433: aload_0
    //   2434: getfield 220	org/telegram/ui/LocationActivity:searchListView	Landroid/widget/ListView;
    //   2437: new 20	org/telegram/ui/LocationActivity$15
    //   2440: dup
    //   2441: aload_0
    //   2442: invokespecial 1028	org/telegram/ui/LocationActivity$15:<init>	(Lorg/telegram/ui/LocationActivity;)V
    //   2445: invokevirtual 983	android/widget/ListView:setOnScrollListener	(Landroid/widget/AbsListView$OnScrollListener;)V
    //   2448: aload_0
    //   2449: getfield 220	org/telegram/ui/LocationActivity:searchListView	Landroid/widget/ListView;
    //   2452: new 22	org/telegram/ui/LocationActivity$16
    //   2455: dup
    //   2456: aload_0
    //   2457: invokespecial 1029	org/telegram/ui/LocationActivity$16:<init>	(Lorg/telegram/ui/LocationActivity;)V
    //   2460: invokevirtual 988	android/widget/ListView:setOnItemClickListener	(Landroid/widget/AdapterView$OnItemClickListener;)V
    //   2463: aload_0
    //   2464: getfield 139	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   2467: ifnull +37 -> 2504
    //   2470: aload_0
    //   2471: new 354	android/location/Location
    //   2474: dup
    //   2475: ldc_w 843
    //   2478: invokespecial 846	android/location/Location:<init>	(Ljava/lang/String;)V
    //   2481: putfield 235	org/telegram/ui/LocationActivity:userLocation	Landroid/location/Location;
    //   2484: aload_0
    //   2485: getfield 235	org/telegram/ui/LocationActivity:userLocation	Landroid/location/Location;
    //   2488: ldc2_w 1030
    //   2491: invokevirtual 860	android/location/Location:setLatitude	(D)V
    //   2494: aload_0
    //   2495: getfield 235	org/telegram/ui/LocationActivity:userLocation	Landroid/location/Location;
    //   2498: ldc2_w 1032
    //   2501: invokevirtual 866	android/location/Location:setLongitude	(D)V
    //   2504: aload 8
    //   2506: aload_0
    //   2507: getfield 239	org/telegram/ui/LocationActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   2510: invokevirtual 1036	android/widget/FrameLayout:addView	(Landroid/view/View;)V
    //   2513: goto -1109 -> 1404
    //   2516: astore 9
    //   2518: ldc_w 951
    //   2521: aload 9
    //   2523: invokestatic 957	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2526: goto -586 -> 1940
    //   2529: astore 9
    //   2531: ldc_w 951
    //   2534: aload 9
    //   2536: invokestatic 957	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2539: goto -583 -> 1956
    //   2542: bipush 60
    //   2544: istore 5
    //   2546: goto -440 -> 2106
    //   2549: ldc_w 1037
    //   2552: fstore_2
    //   2553: goto -435 -> 2118
    //   2556: iconst_5
    //   2557: istore 6
    //   2559: goto -432 -> 2127
    //   2562: fconst_0
    //   2563: fstore_3
    //   2564: goto -427 -> 2137
    //   2567: ldc_w 839
    //   2570: fstore 4
    //   2572: goto -426 -> 2146
    //   2575: astore_1
    //   2576: ldc_w 951
    //   2579: aload_1
    //   2580: invokestatic 957	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2583: goto -1164 -> 1419
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	2586	0	this	LocationActivity
    //   0	2586	1	paramContext	Context
    //   625	1928	2	f1	float
    //   635	1929	3	f2	float
    //   2144	427	4	f3	float
    //   616	1929	5	i	int
    //   2125	433	6	j	int
    //   46	1443	7	bool	boolean
    //   36	2469	8	localObject1	Object
    //   347	905	9	localObject2	Object
    //   1570	6	9	localException1	Exception
    //   1782	630	9	localObject3	Object
    //   2516	6	9	localException2	Exception
    //   2529	6	9	localException3	Exception
    //   386	860	10	localObject4	Object
    //   1653	6	10	localException4	Exception
    //   1792	357	10	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   516	532	1570	java/lang/Exception
    //   1049	1078	1653	java/lang/Exception
    //   1932	1940	2516	java/lang/Exception
    //   1940	1956	2529	java/lang/Exception
    //   1411	1419	2575	java/lang/Exception
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.updateInterfaces)
    {
      paramInt = ((Integer)paramVarArgs[0]).intValue();
      if (((paramInt & 0x2) != 0) || ((paramInt & 0x1) != 0)) {
        updateUserData();
      }
    }
    do
    {
      return;
      if (paramInt == NotificationCenter.closeChats)
      {
        removeSelfFromStack();
        return;
      }
    } while ((paramInt != NotificationCenter.locationPermissionGranted) || (this.googleMap == null));
    try
    {
      this.googleMap.setMyLocationEnabled(true);
      return;
    }
    catch (Exception paramVarArgs)
    {
      FileLog.e("tmessages", paramVarArgs);
    }
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.swipeBackEnabled = false;
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
    if (this.messageObject != null) {
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    }
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    try
    {
      if (this.mapView != null) {
        this.mapView.onDestroy();
      }
      if (this.adapter != null) {
        this.adapter.destroy();
      }
      if (this.searchAdapter != null) {
        this.searchAdapter.destroy();
      }
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
  
  public void onLowMemory()
  {
    super.onLowMemory();
    if (this.mapView != null) {
      this.mapView.onLowMemory();
    }
  }
  
  public void onPause()
  {
    super.onPause();
    if (this.mapView != null) {}
    try
    {
      this.mapView.onPause();
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  public void onResume()
  {
    super.onResume();
    AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    if (this.mapView != null) {}
    try
    {
      this.mapView.onResume();
      if (this.googleMap == null) {}
    }
    catch (Throwable localThrowable)
    {
      try
      {
        this.googleMap.setMyLocationEnabled(true);
        updateUserData();
        fixLayoutInternal(true);
        if ((this.checkPermission) && (Build.VERSION.SDK_INT >= 23))
        {
          Activity localActivity = getParentActivity();
          if (localActivity != null)
          {
            this.checkPermission = false;
            if (localActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
              localActivity.requestPermissions(new String[] { "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION" }, 2);
            }
          }
        }
        return;
        localThrowable = localThrowable;
        FileLog.e("tmessages", localThrowable);
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
      }
    }
  }
  
  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1) {}
    try
    {
      if ((this.mapView.getParent() instanceof ViewGroup)) {
        ((ViewGroup)this.mapView.getParent()).removeView(this.mapView);
      }
      if (this.mapViewClip != null)
      {
        this.mapViewClip.addView(this.mapView, 0, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0F), 51));
        updateClipView(this.listView.getFirstVisiblePosition());
        return;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
      ((FrameLayout)this.fragmentView).addView(this.mapView, 0, LayoutHelper.createFrame(-1, -1, 51));
    }
  }
  
  public void setDelegate(LocationActivityDelegate paramLocationActivityDelegate)
  {
    this.delegate = paramLocationActivityDelegate;
  }
  
  public void setMessageObject(MessageObject paramMessageObject)
  {
    this.messageObject = paramMessageObject;
  }
  
  public static abstract interface LocationActivityDelegate
  {
    public abstract void didSelectLocation(TLRPC.MessageMedia paramMessageMedia);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\LocationActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */