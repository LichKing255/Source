package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.AlbumEntry;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface;
import org.telegram.ui.Cells.PhotoAttachCameraCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class ChatAttachAlert
  extends BottomSheet
  implements NotificationCenter.NotificationCenterDelegate, PhotoViewer.PhotoViewerProvider, BottomSheet.BottomSheetDelegateInterface
{
  private ListAdapter adapter;
  private LinearLayoutManager attachPhotoLayoutManager;
  private RecyclerListView attachPhotoRecyclerView;
  private ViewGroup attachView;
  private ChatActivity baseFragment;
  private AnimatorSet currentHintAnimation;
  private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
  private ChatAttachViewDelegate delegate;
  private boolean deviceHasGoodCamera = false;
  private Runnable hideHintRunnable;
  private boolean hintShowed;
  private TextView hintTextView;
  private boolean ignoreLayout;
  private ArrayList<InnerAnimator> innerAnimators = new ArrayList();
  private LinearLayoutManager layoutManager;
  private View lineView;
  private RecyclerListView listView;
  private boolean loading = true;
  private PhotoAttachAdapter photoAttachAdapter;
  private EmptyTextProgressView progressView;
  private boolean revealAnimationInProgress;
  private float revealRadius;
  private int revealX;
  private int revealY;
  private int scrollOffsetY;
  private AttachButton sendPhotosButton;
  private Drawable shadowDrawable;
  private boolean useRevealAnimation;
  private View[] views = new View[20];
  private ArrayList<Holder> viewsCache = new ArrayList(8);
  
  public ChatAttachAlert(Context paramContext)
  {
    super(paramContext, false);
    setDelegate(this);
    setUseRevealAnimation(true);
    if (this.deviceHasGoodCamera) {}
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.albumsDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.reloadInlineHints);
    this.shadowDrawable = paramContext.getResources().getDrawable(2130838130);
    Object localObject1 = new RecyclerListView(paramContext)
    {
      public void onDraw(Canvas paramAnonymousCanvas)
      {
        if ((ChatAttachAlert.this.useRevealAnimation) && (Build.VERSION.SDK_INT <= 19))
        {
          paramAnonymousCanvas.save();
          paramAnonymousCanvas.clipRect(ChatAttachAlert.backgroundPaddingLeft, ChatAttachAlert.this.scrollOffsetY, getMeasuredWidth() - ChatAttachAlert.backgroundPaddingLeft, getMeasuredHeight());
          if (ChatAttachAlert.this.revealAnimationInProgress) {
            paramAnonymousCanvas.drawCircle(ChatAttachAlert.this.revealX, ChatAttachAlert.this.revealY, ChatAttachAlert.this.revealRadius, ChatAttachAlert.this.ciclePaint);
          }
          for (;;)
          {
            paramAnonymousCanvas.restore();
            return;
            paramAnonymousCanvas.drawRect(ChatAttachAlert.backgroundPaddingLeft, ChatAttachAlert.this.scrollOffsetY, getMeasuredWidth() - ChatAttachAlert.backgroundPaddingLeft, getMeasuredHeight(), ChatAttachAlert.this.ciclePaint);
          }
        }
        ChatAttachAlert.this.shadowDrawable.setBounds(0, ChatAttachAlert.this.scrollOffsetY - ChatAttachAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
        ChatAttachAlert.this.shadowDrawable.draw(paramAnonymousCanvas);
      }
      
      public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if ((paramAnonymousMotionEvent.getAction() == 0) && (ChatAttachAlert.this.scrollOffsetY != 0) && (paramAnonymousMotionEvent.getY() < ChatAttachAlert.this.scrollOffsetY))
        {
          ChatAttachAlert.this.dismiss();
          return true;
        }
        return super.onInterceptTouchEvent(paramAnonymousMotionEvent);
      }
      
      protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        super.onLayout(paramAnonymousBoolean, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
        ChatAttachAlert.this.updateLayout();
      }
      
      protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        paramAnonymousInt2 = View.MeasureSpec.getSize(paramAnonymousInt2);
        int j = paramAnonymousInt2;
        if (Build.VERSION.SDK_INT >= 21) {
          j = paramAnonymousInt2 - AndroidUtilities.statusBarHeight;
        }
        int i = ChatAttachAlert.backgroundPaddingTop;
        int k = AndroidUtilities.dp(294.0F);
        if (SearchQuery.inlineBots.isEmpty())
        {
          paramAnonymousInt2 = 0;
          k = k + i + paramAnonymousInt2;
          if (k != AndroidUtilities.dp(294.0F)) {
            break label185;
          }
        }
        label185:
        for (i = 0;; i = j - AndroidUtilities.dp(294.0F))
        {
          paramAnonymousInt2 = i;
          if (i != 0)
          {
            paramAnonymousInt2 = i;
            if (k < j) {
              paramAnonymousInt2 = i - (j - k);
            }
          }
          i = paramAnonymousInt2;
          if (paramAnonymousInt2 == 0) {
            i = ChatAttachAlert.backgroundPaddingTop;
          }
          if (getPaddingTop() != i)
          {
            ChatAttachAlert.access$902(ChatAttachAlert.this, true);
            setPadding(ChatAttachAlert.backgroundPaddingLeft, i, ChatAttachAlert.backgroundPaddingLeft, 0);
            ChatAttachAlert.access$902(ChatAttachAlert.this, false);
          }
          super.onMeasure(paramAnonymousInt1, View.MeasureSpec.makeMeasureSpec(Math.min(k, j), 1073741824));
          return;
          paramAnonymousInt2 = (int)Math.ceil(SearchQuery.inlineBots.size() / 4.0F) * AndroidUtilities.dp(100.0F) + AndroidUtilities.dp(12.0F);
          break;
        }
      }
      
      public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        return (!ChatAttachAlert.this.isDismissed()) && (super.onTouchEvent(paramAnonymousMotionEvent));
      }
      
      public void requestLayout()
      {
        if (ChatAttachAlert.this.ignoreLayout) {
          return;
        }
        super.requestLayout();
      }
    };
    this.listView = ((RecyclerListView)localObject1);
    this.containerView = ((ViewGroup)localObject1);
    this.listView.setTag(Integer.valueOf(10));
    this.containerView.setWillNotDraw(false);
    this.listView.setClipToPadding(false);
    localObject1 = this.listView;
    Object localObject2 = new LinearLayoutManager(getContext());
    this.layoutManager = ((LinearLayoutManager)localObject2);
    ((RecyclerListView)localObject1).setLayoutManager((RecyclerView.LayoutManager)localObject2);
    this.layoutManager.setOrientation(1);
    localObject1 = this.listView;
    localObject2 = new ListAdapter(paramContext);
    this.adapter = ((ListAdapter)localObject2);
    ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setEnabled(true);
    this.listView.setGlowColor(-657673);
    this.listView.addItemDecoration(new RecyclerView.ItemDecoration()
    {
      public void getItemOffsets(Rect paramAnonymousRect, View paramAnonymousView, RecyclerView paramAnonymousRecyclerView, RecyclerView.State paramAnonymousState)
      {
        paramAnonymousRect.left = 0;
        paramAnonymousRect.right = 0;
        paramAnonymousRect.top = 0;
        paramAnonymousRect.bottom = 0;
      }
    });
    this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if (ChatAttachAlert.this.listView.getChildCount() <= 0) {
          return;
        }
        if ((ChatAttachAlert.this.hintShowed) && (ChatAttachAlert.this.layoutManager.findLastVisibleItemPosition() > 1))
        {
          ChatAttachAlert.this.hideHint();
          ChatAttachAlert.access$3002(ChatAttachAlert.this, false);
          ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putBoolean("bothint", true).commit();
        }
        ChatAttachAlert.this.updateLayout();
      }
    });
    this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
    this.attachView = new FrameLayout(paramContext)
    {
      protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        paramAnonymousInt1 = paramAnonymousInt3 - paramAnonymousInt1;
        paramAnonymousInt2 = paramAnonymousInt4 - paramAnonymousInt2;
        paramAnonymousInt3 = AndroidUtilities.dp(8.0F);
        ChatAttachAlert.this.attachPhotoRecyclerView.layout(0, paramAnonymousInt3, paramAnonymousInt1, ChatAttachAlert.this.attachPhotoRecyclerView.getMeasuredHeight() + paramAnonymousInt3);
        ChatAttachAlert.this.progressView.layout(0, paramAnonymousInt3, paramAnonymousInt1, ChatAttachAlert.this.progressView.getMeasuredHeight() + paramAnonymousInt3);
        ChatAttachAlert.this.lineView.layout(0, AndroidUtilities.dp(96.0F), paramAnonymousInt1, AndroidUtilities.dp(96.0F) + ChatAttachAlert.this.lineView.getMeasuredHeight());
        ChatAttachAlert.this.hintTextView.layout(paramAnonymousInt1 - ChatAttachAlert.this.hintTextView.getMeasuredWidth() - AndroidUtilities.dp(5.0F), paramAnonymousInt2 - ChatAttachAlert.this.hintTextView.getMeasuredHeight() - AndroidUtilities.dp(5.0F), paramAnonymousInt1 - AndroidUtilities.dp(5.0F), paramAnonymousInt2 - AndroidUtilities.dp(5.0F));
        paramAnonymousInt2 = (paramAnonymousInt1 - AndroidUtilities.dp(360.0F)) / 3;
        paramAnonymousInt1 = 0;
        while (paramAnonymousInt1 < 8)
        {
          paramAnonymousInt3 = AndroidUtilities.dp(paramAnonymousInt1 / 4 * 95 + 105);
          paramAnonymousInt4 = AndroidUtilities.dp(10.0F) + paramAnonymousInt1 % 4 * (AndroidUtilities.dp(85.0F) + paramAnonymousInt2);
          ChatAttachAlert.this.views[paramAnonymousInt1].layout(paramAnonymousInt4, paramAnonymousInt3, ChatAttachAlert.this.views[paramAnonymousInt1].getMeasuredWidth() + paramAnonymousInt4, ChatAttachAlert.this.views[paramAnonymousInt1].getMeasuredHeight() + paramAnonymousInt3);
          paramAnonymousInt1 += 1;
        }
      }
      
      protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        super.onMeasure(paramAnonymousInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(294.0F), 1073741824));
      }
    };
    localObject1 = this.views;
    localObject2 = new RecyclerListView(paramContext);
    this.attachPhotoRecyclerView = ((RecyclerListView)localObject2);
    localObject1[8] = localObject2;
    this.attachPhotoRecyclerView.setVerticalScrollBarEnabled(true);
    localObject1 = this.attachPhotoRecyclerView;
    localObject2 = new PhotoAttachAdapter(paramContext);
    this.photoAttachAdapter = ((PhotoAttachAdapter)localObject2);
    ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
    this.attachPhotoRecyclerView.setClipToPadding(false);
    this.attachPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0F), 0, AndroidUtilities.dp(8.0F), 0);
    this.attachPhotoRecyclerView.setItemAnimator(null);
    this.attachPhotoRecyclerView.setLayoutAnimation(null);
    this.attachPhotoRecyclerView.setOverScrollMode(2);
    this.attachView.addView(this.attachPhotoRecyclerView, LayoutHelper.createFrame(-1, 80.0F));
    this.attachPhotoLayoutManager = new LinearLayoutManager(paramContext)
    {
      public boolean supportsPredictiveItemAnimations()
      {
        return false;
      }
    };
    this.attachPhotoLayoutManager.setOrientation(0);
    this.attachPhotoRecyclerView.setLayoutManager(this.attachPhotoLayoutManager);
    this.attachPhotoRecyclerView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        if ((ChatAttachAlert.this.baseFragment == null) || (ChatAttachAlert.this.baseFragment.getParentActivity() == null)) {}
        int i;
        do
        {
          return;
          if ((ChatAttachAlert.this.deviceHasGoodCamera) && (paramAnonymousInt == 0)) {
            break;
          }
          i = paramAnonymousInt;
          if (ChatAttachAlert.this.deviceHasGoodCamera) {
            i = paramAnonymousInt - 1;
          }
          paramAnonymousView = MediaController.allPhotosAlbumEntry.photos;
        } while ((i < 0) || (i >= paramAnonymousView.size()));
        PhotoViewer.getInstance().setParentActivity(ChatAttachAlert.this.baseFragment.getParentActivity());
        PhotoViewer.getInstance().openPhotoForSelect(paramAnonymousView, i, 0, ChatAttachAlert.this, ChatAttachAlert.this.baseFragment);
        AndroidUtilities.hideKeyboard(ChatAttachAlert.this.baseFragment.getFragmentView().findFocus());
        return;
        AndroidUtilities.generatePicturePath();
      }
    });
    localObject1 = this.views;
    localObject2 = new EmptyTextProgressView(paramContext);
    this.progressView = ((EmptyTextProgressView)localObject2);
    localObject1[9] = localObject2;
    if ((Build.VERSION.SDK_INT >= 23) && (getContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0))
    {
      this.progressView.setText(LocaleController.getString("PermissionStorage", 2131166164));
      this.progressView.setTextSize(16);
    }
    for (;;)
    {
      this.attachView.addView(this.progressView, LayoutHelper.createFrame(-1, 80.0F));
      this.attachPhotoRecyclerView.setEmptyView(this.progressView);
      localObject1 = this.views;
      localObject2 = new View(getContext())
      {
        public boolean hasOverlappingRendering()
        {
          return false;
        }
      };
      this.lineView = ((View)localObject2);
      localObject1[10] = localObject2;
      this.lineView.setBackgroundColor(-2960686);
      this.attachView.addView(this.lineView, new FrameLayout.LayoutParams(-1, 1, 51));
      localObject1 = LocaleController.getString("ChatCamera", 2131165532);
      localObject2 = LocaleController.getString("ChatGallery", 2131165534);
      String str1 = LocaleController.getString("ChatVideo", 2131165539);
      String str2 = LocaleController.getString("AttachMusic", 2131165374);
      String str3 = LocaleController.getString("ChatDocument", 2131165533);
      String str4 = LocaleController.getString("AttachContact", 2131165370);
      String str5 = LocaleController.getString("ChatLocation", 2131165537);
      i = 0;
      while (i < 8)
      {
        AttachButton localAttachButton = new AttachButton(paramContext);
        localAttachButton.setTextAndIcon(new CharSequence[] { localObject1, localObject2, str1, str2, str3, str4, str5, "" }[i], org.telegram.ui.ActionBar.Theme.attachButtonDrawables[i]);
        this.attachView.addView(localAttachButton, LayoutHelper.createFrame(85, 90, 51));
        localAttachButton.setTag(Integer.valueOf(i));
        this.views[i] = localAttachButton;
        if (i == 7)
        {
          this.sendPhotosButton = localAttachButton;
          this.sendPhotosButton.imageView.setPadding(0, AndroidUtilities.dp(4.0F), 0, 0);
        }
        localAttachButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            ChatAttachAlert.this.delegate.didPressedButton(((Integer)paramAnonymousView.getTag()).intValue());
          }
        });
        i += 1;
      }
      this.progressView.setText(LocaleController.getString("NoPhotos", 2131166014));
      this.progressView.setTextSize(20);
    }
    this.hintTextView = new TextView(paramContext);
    this.hintTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.hintTextView.setBackgroundResource(2130838194);
    this.hintTextView.setTextColor(-1);
    this.hintTextView.setTextSize(1, 14.0F);
    this.hintTextView.setPadding(AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F), 0);
    this.hintTextView.setText(LocaleController.getString("AttachBotsHelp", 2131165369));
    this.hintTextView.setGravity(16);
    this.hintTextView.setVisibility(4);
    this.hintTextView.setCompoundDrawablesWithIntrinsicBounds(2130838113, 0, 0, 0);
    this.hintTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0F));
    this.attachView.addView(this.hintTextView, LayoutHelper.createFrame(-2, 32.0F, 85, 5.0F, 0.0F, 5.0F, 5.0F));
    int i = 0;
    while (i < 8)
    {
      this.viewsCache.add(this.photoAttachAdapter.createHolder());
      i += 1;
    }
    if (this.loading)
    {
      this.progressView.showProgress();
      return;
    }
    this.progressView.showTextView();
  }
  
  private PhotoAttachPhotoCell getCellForIndex(int paramInt)
  {
    if (MediaController.allPhotosAlbumEntry == null) {
      return null;
    }
    int j = this.attachPhotoRecyclerView.getChildCount();
    int i = 0;
    if (i < j)
    {
      Object localObject = this.attachPhotoRecyclerView.getChildAt(i);
      int k;
      if ((localObject instanceof PhotoAttachPhotoCell))
      {
        localObject = (PhotoAttachPhotoCell)localObject;
        k = ((Integer)((PhotoAttachPhotoCell)localObject).getImageView().getTag()).intValue();
        if ((k >= 0) && (k < MediaController.allPhotosAlbumEntry.photos.size())) {
          break label90;
        }
      }
      label90:
      while (k != paramInt)
      {
        i += 1;
        break;
      }
      return (PhotoAttachPhotoCell)localObject;
    }
    return null;
  }
  
  private void hideHint()
  {
    if (this.hideHintRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.hideHintRunnable);
      this.hideHintRunnable = null;
    }
    if (this.hintTextView == null) {
      return;
    }
    this.currentHintAnimation = new AnimatorSet();
    this.currentHintAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[] { 0.0F }) });
    this.currentHintAnimation.setInterpolator(this.decelerateInterpolator);
    this.currentHintAnimation.addListener(new AnimatorListenerAdapterProxy()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        if ((ChatAttachAlert.this.currentHintAnimation != null) && (ChatAttachAlert.this.currentHintAnimation.equals(paramAnonymousAnimator))) {
          ChatAttachAlert.access$4002(ChatAttachAlert.this, null);
        }
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if ((ChatAttachAlert.this.currentHintAnimation == null) || (!ChatAttachAlert.this.currentHintAnimation.equals(paramAnonymousAnimator))) {}
        do
        {
          return;
          ChatAttachAlert.access$4002(ChatAttachAlert.this, null);
        } while (ChatAttachAlert.this.hintTextView == null);
        ChatAttachAlert.this.hintTextView.setVisibility(4);
      }
    });
    this.currentHintAnimation.setDuration(300L);
    this.currentHintAnimation.start();
  }
  
  private void onRevealAnimationEnd(boolean paramBoolean)
  {
    NotificationCenter.getInstance().setAnimationInProgress(false);
    this.revealAnimationInProgress = false;
    if ((paramBoolean) && (Build.VERSION.SDK_INT <= 19) && (MediaController.allPhotosAlbumEntry == null)) {
      MediaController.loadGalleryPhotosAlbums(0);
    }
    if (paramBoolean) {
      showHint();
    }
  }
  
  private void setUseRevealAnimation(boolean paramBoolean)
  {
    if ((!paramBoolean) || ((paramBoolean) && (Build.VERSION.SDK_INT >= 18) && (!AndroidUtilities.isTablet()))) {
      this.useRevealAnimation = paramBoolean;
    }
  }
  
  private void showHint()
  {
    if (SearchQuery.inlineBots.isEmpty()) {}
    while (ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("bothint", false)) {
      return;
    }
    this.hintShowed = true;
    this.hintTextView.setVisibility(0);
    this.currentHintAnimation = new AnimatorSet();
    this.currentHintAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[] { 0.0F, 1.0F }) });
    this.currentHintAnimation.setInterpolator(this.decelerateInterpolator);
    this.currentHintAnimation.addListener(new AnimatorListenerAdapterProxy()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        if ((ChatAttachAlert.this.currentHintAnimation != null) && (ChatAttachAlert.this.currentHintAnimation.equals(paramAnonymousAnimator))) {
          ChatAttachAlert.access$4002(ChatAttachAlert.this, null);
        }
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if ((ChatAttachAlert.this.currentHintAnimation == null) || (!ChatAttachAlert.this.currentHintAnimation.equals(paramAnonymousAnimator))) {
          return;
        }
        ChatAttachAlert.access$4002(ChatAttachAlert.this, null);
        AndroidUtilities.runOnUIThread(ChatAttachAlert.access$4102(ChatAttachAlert.this, new Runnable()
        {
          public void run()
          {
            if (ChatAttachAlert.this.hideHintRunnable != this) {
              return;
            }
            ChatAttachAlert.access$4102(ChatAttachAlert.this, null);
            ChatAttachAlert.this.hideHint();
          }
        }), 2000L);
      }
    });
    this.currentHintAnimation.setDuration(300L);
    this.currentHintAnimation.start();
  }
  
  @SuppressLint({"NewApi"})
  private void startRevealAnimation(final boolean paramBoolean)
  {
    this.containerView.setTranslationY(0.0F);
    final AnimatorSet localAnimatorSet = new AnimatorSet();
    final Object localObject1 = this.delegate.getRevealView();
    Object localObject2;
    float f1;
    if ((((View)localObject1).getVisibility() == 0) && (((ViewGroup)((View)localObject1).getParent()).getVisibility() == 0))
    {
      localObject2 = new int[2];
      ((View)localObject1).getLocationInWindow((int[])localObject2);
      if (Build.VERSION.SDK_INT <= 19)
      {
        f1 = AndroidUtilities.displaySize.y - this.containerView.getMeasuredHeight() - AndroidUtilities.statusBarHeight;
        this.revealX = (localObject2[0] + ((View)localObject1).getMeasuredWidth() / 2);
        this.revealY = ((int)(localObject2[1] + ((View)localObject1).getMeasuredHeight() / 2 - f1));
        if (Build.VERSION.SDK_INT > 19) {}
      }
    }
    int i;
    int k;
    int j;
    for (this.revealY -= AndroidUtilities.statusBarHeight;; this.revealY = ((int)(AndroidUtilities.displaySize.y - this.containerView.getY())))
    {
      localObject1 = new int[4][];
      localObject1[0] = { 0, 0 };
      localObject1[1] = { 0, AndroidUtilities.dp(304.0F) };
      localObject1[2] = { this.containerView.getMeasuredWidth(), 0 };
      localObject1[3] = { this.containerView.getMeasuredWidth(), AndroidUtilities.dp(304.0F) };
      i = 0;
      k = this.revealY - this.scrollOffsetY + backgroundPaddingTop;
      j = 0;
      while (j < 4)
      {
        i = Math.max(i, (int)Math.ceil(Math.sqrt((this.revealX - localObject1[j][0]) * (this.revealX - localObject1[j][0]) + (k - localObject1[j][1]) * (k - localObject1[j][1]))));
        j += 1;
      }
      f1 = this.containerView.getY();
      break;
      this.revealX = (AndroidUtilities.displaySize.x / 2 + backgroundPaddingLeft);
    }
    label418:
    float f2;
    if (this.revealX <= this.containerView.getMeasuredWidth())
    {
      j = this.revealX;
      localObject1 = new ArrayList(3);
      if (!paramBoolean) {
        break label1214;
      }
      f1 = 0.0F;
      if (!paramBoolean) {
        break label1221;
      }
      f2 = i;
      label426:
      ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this, "revealRadius", new float[] { f1, f2 }));
      localObject2 = this.backDrawable;
      if (!paramBoolean) {
        break label1226;
      }
      k = 51;
      label464:
      ((ArrayList)localObject1).add(ObjectAnimator.ofInt(localObject2, "alpha", new int[] { k }));
      if (Build.VERSION.SDK_INT < 21) {
        break label1257;
      }
      this.containerView.setElevation(AndroidUtilities.dp(10.0F));
    }
    for (;;)
    {
      try
      {
        localObject2 = this.containerView;
        k = this.revealY;
        if (!paramBoolean) {
          continue;
        }
        f1 = 0.0F;
      }
      catch (Exception localException)
      {
        int m;
        float f3;
        ArrayList localArrayList;
        label1214:
        label1221:
        label1226:
        FileLog.e("tmessages", localException);
        continue;
      }
      ((ArrayList)localObject1).add(ViewAnimationUtils.createCircularReveal((View)localObject2, j, k, f1, f2));
      localAnimatorSet.setDuration(320L);
      localAnimatorSet.playTogether((Collection)localObject1);
      localAnimatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          if ((ChatAttachAlert.this.currentSheetAnimation != null) && (localAnimatorSet.equals(paramAnonymousAnimator))) {
            ChatAttachAlert.access$5702(ChatAttachAlert.this, null);
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((ChatAttachAlert.this.currentSheetAnimation != null) && (ChatAttachAlert.this.currentSheetAnimation.equals(paramAnonymousAnimator)))
          {
            ChatAttachAlert.access$5002(ChatAttachAlert.this, null);
            ChatAttachAlert.this.onRevealAnimationEnd(paramBoolean);
            ChatAttachAlert.this.containerView.invalidate();
            ChatAttachAlert.this.containerView.setLayerType(0, null);
            if (!paramBoolean) {
              ChatAttachAlert.this.containerView.setVisibility(4);
            }
          }
          try
          {
            ChatAttachAlert.this.dismissInternal();
            return;
          }
          catch (Exception paramAnonymousAnimator)
          {
            FileLog.e("tmessages", paramAnonymousAnimator);
          }
        }
      });
      if (paramBoolean)
      {
        this.innerAnimators.clear();
        NotificationCenter.getInstance().setAllowedNotificationsDutingAnimation(new int[] { NotificationCenter.dialogsNeedReload });
        NotificationCenter.getInstance().setAnimationInProgress(true);
        this.revealAnimationInProgress = true;
        if (Build.VERSION.SDK_INT <= 19)
        {
          i = 11;
          j = 0;
          if (j >= i) {
            break label1492;
          }
          if (Build.VERSION.SDK_INT > 19) {
            break label1457;
          }
          if (j < 8)
          {
            this.views[j].setScaleX(0.1F);
            this.views[j].setScaleY(0.1F);
          }
          this.views[j].setAlpha(0.0F);
          localObject2 = new InnerAnimator(null);
          k = this.views[j].getLeft() + this.views[j].getMeasuredWidth() / 2;
          m = this.views[j].getTop() + this.attachView.getTop() + this.views[j].getMeasuredHeight() / 2;
          f1 = (float)Math.sqrt((this.revealX - k) * (this.revealX - k) + (this.revealY - m) * (this.revealY - m));
          f2 = (this.revealX - k) / f1;
          f3 = (this.revealY - m) / f1;
          this.views[j].setPivotX(this.views[j].getMeasuredWidth() / 2 + AndroidUtilities.dp(20.0F) * f2);
          this.views[j].setPivotY(this.views[j].getMeasuredHeight() / 2 + AndroidUtilities.dp(20.0F) * f3);
          InnerAnimator.access$4602((InnerAnimator)localObject2, f1 - AndroidUtilities.dp(81.0F));
          this.views[j].setTag(2131165338, Integer.valueOf(1));
          localArrayList = new ArrayList();
          if (j >= 8) {
            break label1486;
          }
          localArrayList.add(ObjectAnimator.ofFloat(this.views[j], "scaleX", new float[] { 0.7F, 1.05F }));
          localArrayList.add(ObjectAnimator.ofFloat(this.views[j], "scaleY", new float[] { 0.7F, 1.05F }));
          localObject1 = new AnimatorSet();
          ((AnimatorSet)localObject1).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.views[j], "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.views[j], "scaleY", new float[] { 1.0F }) });
          ((AnimatorSet)localObject1).setDuration(100L);
          ((AnimatorSet)localObject1).setInterpolator(this.decelerateInterpolator);
          if (Build.VERSION.SDK_INT <= 19) {
            localArrayList.add(ObjectAnimator.ofFloat(this.views[j], "alpha", new float[] { 1.0F }));
          }
          InnerAnimator.access$4702((InnerAnimator)localObject2, new AnimatorSet());
          ((InnerAnimator)localObject2).animatorSet.playTogether(localArrayList);
          ((InnerAnimator)localObject2).animatorSet.setDuration(150L);
          ((InnerAnimator)localObject2).animatorSet.setInterpolator(this.decelerateInterpolator);
          ((InnerAnimator)localObject2).animatorSet.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if (localObject1 != null) {
                localObject1.start();
              }
            }
          });
          this.innerAnimators.add(localObject2);
          j += 1;
          continue;
          j = this.containerView.getMeasuredWidth();
          break;
          f1 = i;
          break label418;
          f2 = 0.0F;
          break label426;
          k = 0;
          break label464;
          f1 = i;
          break label1504;
          f2 = 0.0F;
          continue;
          label1257:
          if (!paramBoolean)
          {
            localAnimatorSet.setDuration(200L);
            ViewGroup localViewGroup = this.containerView;
            if (this.revealX <= this.containerView.getMeasuredWidth()) {}
            for (f1 = this.revealX;; f1 = this.containerView.getMeasuredWidth())
            {
              localViewGroup.setPivotX(f1);
              this.containerView.setPivotY(this.revealY);
              ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.containerView, "scaleX", new float[] { 0.0F }));
              ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.containerView, "scaleY", new float[] { 0.0F }));
              ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.containerView, "alpha", new float[] { 0.0F }));
              break;
            }
          }
          localAnimatorSet.setDuration(250L);
          this.containerView.setScaleX(1.0F);
          this.containerView.setScaleY(1.0F);
          this.containerView.setAlpha(1.0F);
          if (Build.VERSION.SDK_INT > 19) {
            continue;
          }
          localAnimatorSet.setStartDelay(20L);
          continue;
        }
        i = 8;
        continue;
        label1457:
        this.views[j].setScaleX(0.7F);
        this.views[j].setScaleY(0.7F);
        continue;
        label1486:
        localObject1 = null;
        continue;
      }
      label1492:
      this.currentSheetAnimation = localAnimatorSet;
      localAnimatorSet.start();
      return;
      label1504:
      if (paramBoolean) {
        f2 = i;
      }
    }
  }
  
  @SuppressLint({"NewApi"})
  private void updateLayout()
  {
    int i;
    if (this.listView.getChildCount() <= 0)
    {
      localObject = this.listView;
      i = this.listView.getPaddingTop();
      this.scrollOffsetY = i;
      ((RecyclerListView)localObject).setTopGlowOffset(i);
      this.containerView.invalidate();
    }
    do
    {
      return;
      localObject = this.listView.getChildAt(0);
      Holder localHolder = (Holder)this.listView.findContainingViewHolder((View)localObject);
      int j = ((View)localObject).getTop();
      int k = 0;
      i = k;
      if (j >= 0)
      {
        i = k;
        if (localHolder != null)
        {
          i = k;
          if (localHolder.getAdapterPosition() == 0) {
            i = j;
          }
        }
      }
    } while (this.scrollOffsetY == i);
    Object localObject = this.listView;
    this.scrollOffsetY = i;
    ((RecyclerListView)localObject).setTopGlowOffset(i);
    this.containerView.invalidate();
  }
  
  protected boolean canDismissWithSwipe()
  {
    return false;
  }
  
  public boolean cancelButtonPressed()
  {
    return false;
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.albumsDidLoaded) {
      if (this.photoAttachAdapter != null)
      {
        this.loading = false;
        this.progressView.showTextView();
        this.photoAttachAdapter.notifyDataSetChanged();
      }
    }
    while ((paramInt != NotificationCenter.reloadInlineHints) || (this.adapter == null)) {
      return;
    }
    this.adapter.notifyDataSetChanged();
  }
  
  public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    int i = 0;
    paramMessageObject = getCellForIndex(paramInt);
    if (paramMessageObject != null)
    {
      paramFileLocation = new int[2];
      paramMessageObject.getImageView().getLocationInWindow(paramFileLocation);
      PhotoViewer.PlaceProviderObject localPlaceProviderObject = new PhotoViewer.PlaceProviderObject();
      localPlaceProviderObject.viewX = paramFileLocation[0];
      int j = paramFileLocation[1];
      if (Build.VERSION.SDK_INT >= 21)
      {
        paramInt = AndroidUtilities.statusBarHeight;
        localPlaceProviderObject.viewY = (j - paramInt);
        localPlaceProviderObject.parentView = this.attachPhotoRecyclerView;
        localPlaceProviderObject.imageReceiver = paramMessageObject.getImageView().getImageReceiver();
        localPlaceProviderObject.thumb = localPlaceProviderObject.imageReceiver.getBitmap();
        localPlaceProviderObject.scale = paramMessageObject.getImageView().getScaleX();
        if (Build.VERSION.SDK_INT < 21) {
          break label148;
        }
      }
      label148:
      for (paramInt = i;; paramInt = -AndroidUtilities.statusBarHeight)
      {
        localPlaceProviderObject.clipBottomAddition = paramInt;
        paramMessageObject.getCheckBox().setVisibility(8);
        return localPlaceProviderObject;
        paramInt = 0;
        break;
      }
    }
    return null;
  }
  
  protected float getRevealRadius()
  {
    return this.revealRadius;
  }
  
  public int getSelectedCount()
  {
    return this.photoAttachAdapter.getSelectedPhotos().size();
  }
  
  public HashMap<Integer, MediaController.PhotoEntry> getSelectedPhotos()
  {
    return this.photoAttachAdapter.getSelectedPhotos();
  }
  
  public Bitmap getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    paramMessageObject = getCellForIndex(paramInt);
    if (paramMessageObject != null) {
      return paramMessageObject.getImageView().getImageReceiver().getBitmap();
    }
    return null;
  }
  
  public void init(ChatActivity paramChatActivity)
  {
    if (MediaController.allPhotosAlbumEntry != null)
    {
      int i = 0;
      while (i < Math.min(100, MediaController.allPhotosAlbumEntry.photos.size()))
      {
        MediaController.PhotoEntry localPhotoEntry = (MediaController.PhotoEntry)MediaController.allPhotosAlbumEntry.photos.get(i);
        localPhotoEntry.caption = null;
        localPhotoEntry.imagePath = null;
        localPhotoEntry.thumbPath = null;
        i += 1;
      }
    }
    if (this.currentHintAnimation != null)
    {
      this.currentHintAnimation.cancel();
      this.currentHintAnimation = null;
    }
    this.hintTextView.setAlpha(0.0F);
    this.hintTextView.setVisibility(4);
    this.attachPhotoLayoutManager.scrollToPositionWithOffset(0, 1000000);
    this.photoAttachAdapter.clearSelectedPhotos();
    this.baseFragment = paramChatActivity;
    this.layoutManager.scrollToPositionWithOffset(0, 1000000);
    updatePhotosButton();
  }
  
  public boolean isPhotoChecked(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < MediaController.allPhotosAlbumEntry.photos.size()) && (this.photoAttachAdapter.getSelectedPhotos().containsKey(Integer.valueOf(((MediaController.PhotoEntry)MediaController.allPhotosAlbumEntry.photos.get(paramInt)).imageId)));
  }
  
  public void loadGalleryPhotos()
  {
    if ((MediaController.allPhotosAlbumEntry == null) && (Build.VERSION.SDK_INT >= 21)) {
      MediaController.loadGalleryPhotosAlbums(0);
    }
  }
  
  protected boolean onCustomCloseAnimation()
  {
    boolean bool = false;
    if (this.useRevealAnimation)
    {
      this.backDrawable.setAlpha(51);
      startRevealAnimation(false);
      bool = true;
    }
    return bool;
  }
  
  protected boolean onCustomOpenAnimation()
  {
    if (this.useRevealAnimation)
    {
      startRevealAnimation(true);
      return true;
    }
    return false;
  }
  
  public void onDestroy()
  {
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.albumsDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.reloadInlineHints);
    this.baseFragment = null;
  }
  
  public void onOpenAnimationEnd()
  {
    onRevealAnimationEnd(true);
  }
  
  public void onOpenAnimationStart() {}
  
  public void sendButtonPressed(int paramInt)
  {
    if (this.photoAttachAdapter.getSelectedPhotos().isEmpty())
    {
      if ((paramInt < 0) || (paramInt >= MediaController.allPhotosAlbumEntry.photos.size())) {
        return;
      }
      MediaController.PhotoEntry localPhotoEntry = (MediaController.PhotoEntry)MediaController.allPhotosAlbumEntry.photos.get(paramInt);
      this.photoAttachAdapter.getSelectedPhotos().put(Integer.valueOf(localPhotoEntry.imageId), localPhotoEntry);
    }
    this.delegate.didPressedButton(7);
  }
  
  public void setDelegate(ChatAttachViewDelegate paramChatAttachViewDelegate)
  {
    this.delegate = paramChatAttachViewDelegate;
  }
  
  public void setPhotoChecked(int paramInt)
  {
    boolean bool = true;
    if ((paramInt < 0) || (paramInt >= MediaController.allPhotosAlbumEntry.photos.size())) {
      return;
    }
    Object localObject = (MediaController.PhotoEntry)MediaController.allPhotosAlbumEntry.photos.get(paramInt);
    int j;
    int i;
    if (this.photoAttachAdapter.getSelectedPhotos().containsKey(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId)))
    {
      this.photoAttachAdapter.getSelectedPhotos().remove(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId));
      bool = false;
      j = this.attachPhotoRecyclerView.getChildCount();
      i = 0;
    }
    for (;;)
    {
      if (i < j)
      {
        localObject = this.attachPhotoRecyclerView.getChildAt(i);
        if (((Integer)((View)localObject).getTag()).intValue() == paramInt) {
          ((PhotoAttachPhotoCell)localObject).setChecked(bool, false);
        }
      }
      else
      {
        updatePhotosButton();
        return;
        this.photoAttachAdapter.getSelectedPhotos().put(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId), localObject);
        break;
      }
      i += 1;
    }
  }
  
  @SuppressLint({"NewApi"})
  protected void setRevealRadius(float paramFloat)
  {
    this.revealRadius = paramFloat;
    if (Build.VERSION.SDK_INT <= 19) {
      this.containerView.invalidate();
    }
    if (!isDismissed())
    {
      int i = 0;
      if (i < this.innerAnimators.size())
      {
        InnerAnimator localInnerAnimator = (InnerAnimator)this.innerAnimators.get(i);
        if (localInnerAnimator.startRadius > paramFloat) {}
        for (;;)
        {
          i += 1;
          break;
          localInnerAnimator.animatorSet.start();
          this.innerAnimators.remove(i);
          i -= 1;
        }
      }
    }
  }
  
  public void updatePhotoAtIndex(int paramInt)
  {
    PhotoAttachPhotoCell localPhotoAttachPhotoCell = getCellForIndex(paramInt);
    MediaController.PhotoEntry localPhotoEntry;
    if (localPhotoAttachPhotoCell != null)
    {
      localPhotoAttachPhotoCell.getImageView().setOrientation(0, true);
      localPhotoEntry = (MediaController.PhotoEntry)MediaController.allPhotosAlbumEntry.photos.get(paramInt);
      if (localPhotoEntry.thumbPath != null) {
        localPhotoAttachPhotoCell.getImageView().setImage(localPhotoEntry.thumbPath, null, localPhotoAttachPhotoCell.getContext().getResources().getDrawable(2130838013));
      }
    }
    else
    {
      return;
    }
    if (localPhotoEntry.path != null)
    {
      localPhotoAttachPhotoCell.getImageView().setOrientation(localPhotoEntry.orientation, true);
      localPhotoAttachPhotoCell.getImageView().setImage("thumb://" + localPhotoEntry.imageId + ":" + localPhotoEntry.path, null, localPhotoAttachPhotoCell.getContext().getResources().getDrawable(2130838013));
      return;
    }
    localPhotoAttachPhotoCell.getImageView().setImageResource(2130838013);
  }
  
  public void updatePhotosButton()
  {
    int i = this.photoAttachAdapter.getSelectedPhotos().size();
    if (i == 0)
    {
      this.sendPhotosButton.imageView.setPadding(0, AndroidUtilities.dp(4.0F), 0, 0);
      this.sendPhotosButton.imageView.setBackgroundResource(2130837602);
      this.sendPhotosButton.imageView.setImageResource(2130837601);
      this.sendPhotosButton.textView.setText("");
    }
    while ((Build.VERSION.SDK_INT >= 23) && (getContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0))
    {
      this.progressView.setText(LocaleController.getString("PermissionStorage", 2131166164));
      this.progressView.setTextSize(16);
      return;
      this.sendPhotosButton.imageView.setPadding(AndroidUtilities.dp(2.0F), 0, 0, 0);
      this.sendPhotosButton.imageView.setBackgroundResource(2130837609);
      this.sendPhotosButton.imageView.setImageResource(2130837608);
      this.sendPhotosButton.textView.setText(LocaleController.formatString("SendItems", 2131166286, new Object[] { String.format("(%d)", new Object[] { Integer.valueOf(i) }) }));
    }
    this.progressView.setText(LocaleController.getString("NoPhotos", 2131166014));
    this.progressView.setTextSize(20);
  }
  
  public void willHidePhotoViewer()
  {
    int j = this.attachPhotoRecyclerView.getChildCount();
    int i = 0;
    while (i < j)
    {
      Object localObject = this.attachPhotoRecyclerView.getChildAt(i);
      if ((localObject instanceof PhotoAttachPhotoCell))
      {
        localObject = (PhotoAttachPhotoCell)localObject;
        if (((PhotoAttachPhotoCell)localObject).getCheckBox().getVisibility() != 0) {
          ((PhotoAttachPhotoCell)localObject).getCheckBox().setVisibility(0);
        }
      }
      i += 1;
    }
  }
  
  public void willSwitchFromPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    paramMessageObject = getCellForIndex(paramInt);
    if (paramMessageObject != null) {
      paramMessageObject.getCheckBox().setVisibility(0);
    }
  }
  
  private class AttachBotButton
    extends FrameLayout
  {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private boolean checkingForLongPress = false;
    private TLRPC.User currentUser;
    private BackupImageView imageView;
    private TextView nameTextView;
    private CheckForLongPress pendingCheckForLongPress = null;
    private CheckForTap pendingCheckForTap = null;
    private int pressCount = 0;
    private boolean pressed;
    
    public AttachBotButton(Context paramContext)
    {
      super();
      this.imageView = new BackupImageView(paramContext);
      this.imageView.setRoundRadius(AndroidUtilities.dp(27.0F));
      addView(this.imageView, LayoutHelper.createFrame(54, 54.0F, 49, 0.0F, 7.0F, 0.0F, 0.0F));
      this.nameTextView = new TextView(paramContext);
      this.nameTextView.setTextColor(-9079435);
      this.nameTextView.setTextSize(1, 12.0F);
      this.nameTextView.setMaxLines(2);
      this.nameTextView.setGravity(49);
      this.nameTextView.setLines(2);
      this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0F, 51, 6.0F, 65.0F, 6.0F, 0.0F));
    }
    
    private void onLongPress()
    {
      if ((ChatAttachAlert.this.baseFragment == null) || (this.currentUser == null)) {
        return;
      }
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(getContext());
      localBuilder.setTitle(LocaleController.getString("AppName", 2131165338));
      localBuilder.setMessage(LocaleController.formatString("ChatHintsDelete", 2131165536, new Object[] { ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name) }));
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          SearchQuery.removeInline(ChatAttachAlert.AttachBotButton.this.currentUser.id);
        }
      });
      localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
      localBuilder.show();
    }
    
    protected void cancelCheckLongPress()
    {
      this.checkingForLongPress = false;
      if (this.pendingCheckForLongPress != null) {
        removeCallbacks(this.pendingCheckForLongPress);
      }
      if (this.pendingCheckForTap != null) {
        removeCallbacks(this.pendingCheckForTap);
      }
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(85.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0F), 1073741824));
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramMotionEvent.getAction() == 0)
      {
        this.pressed = true;
        invalidate();
        bool1 = true;
        if (bool1) {
          break label190;
        }
        bool2 = super.onTouchEvent(paramMotionEvent);
      }
      for (;;)
      {
        if ((paramMotionEvent.getAction() != 0) && (paramMotionEvent.getAction() != 2)) {
          cancelCheckLongPress();
        }
        return bool2;
        bool1 = bool2;
        if (!this.pressed) {
          break;
        }
        if (paramMotionEvent.getAction() == 1)
        {
          getParent().requestDisallowInterceptTouchEvent(true);
          this.pressed = false;
          playSoundEffect(0);
          ChatAttachAlert.this.delegate.didSelectBot(MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_topPeer)SearchQuery.inlineBots.get(((Integer)getTag()).intValue())).peer.user_id)));
          ChatAttachAlert.this.setUseRevealAnimation(false);
          ChatAttachAlert.this.dismiss();
          ChatAttachAlert.this.setUseRevealAnimation(true);
          invalidate();
          bool1 = bool2;
          break;
        }
        bool1 = bool2;
        if (paramMotionEvent.getAction() != 3) {
          break;
        }
        this.pressed = false;
        invalidate();
        bool1 = bool2;
        break;
        label190:
        bool2 = bool1;
        if (paramMotionEvent.getAction() == 0)
        {
          startCheckLongPress();
          bool2 = bool1;
        }
      }
    }
    
    public void setUser(TLRPC.User paramUser)
    {
      if (paramUser == null) {
        return;
      }
      this.currentUser = paramUser;
      Object localObject2 = null;
      this.nameTextView.setText(ContactsController.formatName(paramUser.first_name, paramUser.last_name));
      this.avatarDrawable.setInfo(paramUser);
      Object localObject1 = localObject2;
      if (paramUser != null)
      {
        localObject1 = localObject2;
        if (paramUser.photo != null) {
          localObject1 = paramUser.photo.photo_small;
        }
      }
      this.imageView.setImage((TLObject)localObject1, "50_50", this.avatarDrawable);
      requestLayout();
    }
    
    protected void startCheckLongPress()
    {
      if (this.checkingForLongPress) {
        return;
      }
      this.checkingForLongPress = true;
      if (this.pendingCheckForTap == null) {
        this.pendingCheckForTap = new CheckForTap(null);
      }
      postDelayed(this.pendingCheckForTap, ViewConfiguration.getTapTimeout());
    }
    
    class CheckForLongPress
      implements Runnable
    {
      public int currentPressCount;
      
      CheckForLongPress() {}
      
      public void run()
      {
        if ((ChatAttachAlert.AttachBotButton.this.checkingForLongPress) && (ChatAttachAlert.AttachBotButton.this.getParent() != null) && (this.currentPressCount == ChatAttachAlert.AttachBotButton.this.pressCount))
        {
          ChatAttachAlert.AttachBotButton.access$202(ChatAttachAlert.AttachBotButton.this, false);
          ChatAttachAlert.AttachBotButton.this.performHapticFeedback(0);
          ChatAttachAlert.AttachBotButton.this.onLongPress();
          MotionEvent localMotionEvent = MotionEvent.obtain(0L, 0L, 3, 0.0F, 0.0F, 0);
          ChatAttachAlert.AttachBotButton.this.onTouchEvent(localMotionEvent);
          localMotionEvent.recycle();
        }
      }
    }
    
    private final class CheckForTap
      implements Runnable
    {
      private CheckForTap() {}
      
      public void run()
      {
        if (ChatAttachAlert.AttachBotButton.this.pendingCheckForLongPress == null) {
          ChatAttachAlert.AttachBotButton.access$002(ChatAttachAlert.AttachBotButton.this, new ChatAttachAlert.AttachBotButton.CheckForLongPress(ChatAttachAlert.AttachBotButton.this));
        }
        ChatAttachAlert.AttachBotButton.this.pendingCheckForLongPress.currentPressCount = ChatAttachAlert.AttachBotButton.access$104(ChatAttachAlert.AttachBotButton.this);
        ChatAttachAlert.AttachBotButton.this.postDelayed(ChatAttachAlert.AttachBotButton.this.pendingCheckForLongPress, ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout());
      }
    }
  }
  
  private class AttachButton
    extends FrameLayout
  {
    private ImageView imageView;
    private TextView textView;
    
    public AttachButton(Context paramContext)
    {
      super();
      this.imageView = new ImageView(paramContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      addView(this.imageView, LayoutHelper.createFrame(64, 64, 49));
      this.textView = new TextView(paramContext);
      this.textView.setLines(1);
      this.textView.setSingleLine(true);
      this.textView.setGravity(1);
      this.textView.setEllipsize(TextUtils.TruncateAt.END);
      this.textView.setTextColor(-9079435);
      this.textView.setTextSize(1, 12.0F);
      this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      addView(this.textView, LayoutHelper.createFrame(-1, -2.0F, 51, 0.0F, 64.0F, 0.0F, 0.0F));
    }
    
    public boolean hasOverlappingRendering()
    {
      return false;
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(85.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0F), 1073741824));
    }
    
    public void setTextAndIcon(CharSequence paramCharSequence, Drawable paramDrawable)
    {
      this.textView.setText(paramCharSequence);
      this.imageView.setBackgroundDrawable(paramDrawable);
    }
  }
  
  public static abstract interface ChatAttachViewDelegate
  {
    public abstract void didPressedButton(int paramInt);
    
    public abstract void didSelectBot(TLRPC.User paramUser);
    
    public abstract View getRevealView();
  }
  
  private class Holder
    extends RecyclerView.ViewHolder
  {
    public Holder(View paramView)
    {
      super();
    }
  }
  
  private class InnerAnimator
  {
    private AnimatorSet animatorSet;
    private float startRadius;
    
    private InnerAnimator() {}
  }
  
  private class ListAdapter
    extends RecyclerView.Adapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getItemCount()
    {
      if (!SearchQuery.inlineBots.isEmpty()) {}
      for (int i = (int)Math.ceil(SearchQuery.inlineBots.size() / 4.0F) + 1;; i = 0) {
        return i + 1;
      }
    }
    
    public int getItemViewType(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return 2;
      case 0: 
        return 0;
      }
      return 1;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      if (paramInt > 1)
      {
        int i = (paramInt - 2) * 4;
        paramViewHolder = (FrameLayout)paramViewHolder.itemView;
        paramInt = 0;
        if (paramInt < 4)
        {
          ChatAttachAlert.AttachBotButton localAttachBotButton = (ChatAttachAlert.AttachBotButton)paramViewHolder.getChildAt(paramInt);
          if (i + paramInt >= SearchQuery.inlineBots.size()) {
            localAttachBotButton.setVisibility(4);
          }
          for (;;)
          {
            paramInt += 1;
            break;
            localAttachBotButton.setVisibility(0);
            localAttachBotButton.setTag(Integer.valueOf(i + paramInt));
            localAttachBotButton.setUser(MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_topPeer)SearchQuery.inlineBots.get(i + paramInt)).peer.user_id)));
          }
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      FrameLayout local1;
      switch (paramInt)
      {
      default: 
        local1 = new FrameLayout(this.mContext)
        {
          protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
          {
            paramAnonymousInt2 = (paramAnonymousInt3 - paramAnonymousInt1 - AndroidUtilities.dp(360.0F)) / 3;
            paramAnonymousInt1 = 0;
            while (paramAnonymousInt1 < 4)
            {
              paramAnonymousInt3 = AndroidUtilities.dp(10.0F) + paramAnonymousInt1 % 4 * (AndroidUtilities.dp(85.0F) + paramAnonymousInt2);
              View localView = getChildAt(paramAnonymousInt1);
              localView.layout(paramAnonymousInt3, 0, localView.getMeasuredWidth() + paramAnonymousInt3, localView.getMeasuredHeight());
              paramAnonymousInt1 += 1;
            }
          }
        };
        paramInt = 0;
      case 0: 
        while (paramInt < 4)
        {
          local1.addView(new ChatAttachAlert.AttachBotButton(ChatAttachAlert.this, this.mContext));
          paramInt += 1;
          continue;
          paramViewGroup = ChatAttachAlert.this.attachView;
        }
      }
      for (;;)
      {
        return new ChatAttachAlert.Holder(ChatAttachAlert.this, paramViewGroup);
        paramViewGroup = new ShadowSectionCell(this.mContext);
        continue;
        paramViewGroup = local1;
        local1.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0F)));
      }
    }
  }
  
  private class PhotoAttachAdapter
    extends RecyclerView.Adapter
  {
    private Context mContext;
    private HashMap<Integer, MediaController.PhotoEntry> selectedPhotos = new HashMap();
    
    public PhotoAttachAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public void clearSelectedPhotos()
    {
      if (!this.selectedPhotos.isEmpty())
      {
        Iterator localIterator = this.selectedPhotos.entrySet().iterator();
        while (localIterator.hasNext())
        {
          MediaController.PhotoEntry localPhotoEntry = (MediaController.PhotoEntry)((Map.Entry)localIterator.next()).getValue();
          localPhotoEntry.imagePath = null;
          localPhotoEntry.thumbPath = null;
          localPhotoEntry.caption = null;
        }
        this.selectedPhotos.clear();
        ChatAttachAlert.this.updatePhotosButton();
        notifyDataSetChanged();
      }
    }
    
    public ChatAttachAlert.Holder createHolder()
    {
      PhotoAttachPhotoCell localPhotoAttachPhotoCell = new PhotoAttachPhotoCell(this.mContext);
      localPhotoAttachPhotoCell.setDelegate(new PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate()
      {
        public void onCheckClick(PhotoAttachPhotoCell paramAnonymousPhotoAttachPhotoCell)
        {
          MediaController.PhotoEntry localPhotoEntry = paramAnonymousPhotoAttachPhotoCell.getPhotoEntry();
          boolean bool;
          if (ChatAttachAlert.PhotoAttachAdapter.this.selectedPhotos.containsKey(Integer.valueOf(localPhotoEntry.imageId)))
          {
            ChatAttachAlert.PhotoAttachAdapter.this.selectedPhotos.remove(Integer.valueOf(localPhotoEntry.imageId));
            paramAnonymousPhotoAttachPhotoCell.setChecked(false, true);
            localPhotoEntry.imagePath = null;
            localPhotoEntry.thumbPath = null;
            if (((Integer)paramAnonymousPhotoAttachPhotoCell.getTag()).intValue() == MediaController.allPhotosAlbumEntry.photos.size() - 1)
            {
              bool = true;
              paramAnonymousPhotoAttachPhotoCell.setPhotoEntry(localPhotoEntry, bool);
            }
          }
          for (;;)
          {
            ChatAttachAlert.this.updatePhotosButton();
            return;
            bool = false;
            break;
            ChatAttachAlert.PhotoAttachAdapter.this.selectedPhotos.put(Integer.valueOf(localPhotoEntry.imageId), localPhotoEntry);
            paramAnonymousPhotoAttachPhotoCell.setChecked(true, true);
          }
        }
      });
      return new ChatAttachAlert.Holder(ChatAttachAlert.this, localPhotoAttachPhotoCell);
    }
    
    public int getItemCount()
    {
      int i = 0;
      if (ChatAttachAlert.this.deviceHasGoodCamera) {
        i = 0 + 1;
      }
      int j = i;
      if (MediaController.allPhotosAlbumEntry != null) {
        j = i + MediaController.allPhotosAlbumEntry.photos.size();
      }
      return j;
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((ChatAttachAlert.this.deviceHasGoodCamera) && (paramInt == 0)) {
        return 1;
      }
      return 0;
    }
    
    public HashMap<Integer, MediaController.PhotoEntry> getSelectedPhotos()
    {
      return this.selectedPhotos;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      int i;
      MediaController.PhotoEntry localPhotoEntry;
      if ((!ChatAttachAlert.this.deviceHasGoodCamera) || (paramInt != 0))
      {
        i = paramInt;
        if (ChatAttachAlert.this.deviceHasGoodCamera) {
          i = paramInt - 1;
        }
        paramViewHolder = (PhotoAttachPhotoCell)paramViewHolder.itemView;
        localPhotoEntry = (MediaController.PhotoEntry)MediaController.allPhotosAlbumEntry.photos.get(i);
        if (i != MediaController.allPhotosAlbumEntry.photos.size() - 1) {
          break label119;
        }
      }
      label119:
      for (boolean bool = true;; bool = false)
      {
        paramViewHolder.setPhotoEntry(localPhotoEntry, bool);
        paramViewHolder.setChecked(this.selectedPhotos.containsKey(Integer.valueOf(localPhotoEntry.imageId)), false);
        paramViewHolder.getImageView().setTag(Integer.valueOf(i));
        paramViewHolder.setTag(Integer.valueOf(i));
        return;
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        if (!ChatAttachAlert.this.viewsCache.isEmpty())
        {
          paramViewGroup = (ChatAttachAlert.Holder)ChatAttachAlert.this.viewsCache.get(0);
          ChatAttachAlert.this.viewsCache.remove(0);
          return paramViewGroup;
        }
        break;
      case 1: 
        return new ChatAttachAlert.Holder(ChatAttachAlert.this, new PhotoAttachCameraCell(this.mContext));
      }
      return createHolder();
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Components\ChatAttachAlert.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */