package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_installStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.StickerPreviewViewer;

public class StickersAlert
  extends BottomSheet
  implements NotificationCenter.NotificationCenterDelegate
{
  private GridAdapter adapter;
  private StickersAlertDelegate delegate;
  private FrameLayout emptyView;
  private RecyclerListView gridView;
  private boolean ignoreLayout;
  private TLRPC.InputStickerSet inputStickerSet;
  private GridLayoutManager layoutManager;
  private PickerBottomLayout pickerBottomLayout;
  private TextView previewSendButton;
  private View previewSendButtonShadow;
  private int reqId;
  private int scrollOffsetY;
  private TLRPC.Document selectedSticker;
  private View[] shadow = new View[2];
  private AnimatorSet[] shadowAnimation = new AnimatorSet[2];
  private Drawable shadowDrawable;
  private TextView stickerEmojiTextView;
  private BackupImageView stickerImageView;
  private FrameLayout stickerPreviewLayout;
  private TLRPC.TL_messages_stickerSet stickerSet;
  private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
  private TextView titleTextView;
  
  public StickersAlert(Context paramContext, TLRPC.InputStickerSet paramInputStickerSet, TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet, StickersAlertDelegate paramStickersAlertDelegate)
  {
    super(paramContext, false);
    this.delegate = paramStickersAlertDelegate;
    this.inputStickerSet = paramInputStickerSet;
    this.stickerSet = paramTL_messages_stickerSet;
    this.shadowDrawable = paramContext.getResources().getDrawable(2130838130);
    this.containerView = new FrameLayout(paramContext)
    {
      protected void onDraw(Canvas paramAnonymousCanvas)
      {
        StickersAlert.this.shadowDrawable.setBounds(0, StickersAlert.this.scrollOffsetY - StickersAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
        StickersAlert.this.shadowDrawable.draw(paramAnonymousCanvas);
      }
      
      public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if ((paramAnonymousMotionEvent.getAction() == 0) && (StickersAlert.this.scrollOffsetY != 0) && (paramAnonymousMotionEvent.getY() < StickersAlert.this.scrollOffsetY))
        {
          StickersAlert.this.dismiss();
          return true;
        }
        return super.onInterceptTouchEvent(paramAnonymousMotionEvent);
      }
      
      protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        super.onLayout(paramAnonymousBoolean, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
        StickersAlert.this.updateLayout();
      }
      
      protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        paramAnonymousInt2 = View.MeasureSpec.getSize(paramAnonymousInt2);
        int j = paramAnonymousInt2;
        if (Build.VERSION.SDK_INT >= 21) {
          j = paramAnonymousInt2 - AndroidUtilities.statusBarHeight;
        }
        int i = AndroidUtilities.dp(96.0F);
        int k;
        if (StickersAlert.this.stickerSet != null)
        {
          paramAnonymousInt2 = (int)Math.ceil(StickersAlert.this.stickerSet.documents.size() / 5.0F);
          k = Math.max(3, paramAnonymousInt2) * AndroidUtilities.dp(82.0F) + i + StickersAlert.backgroundPaddingTop;
          if (k >= j / 5 * 3.2D) {
            break label223;
          }
        }
        label223:
        for (i = 0;; i = j / 5 * 2)
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
            i = StickersAlert.backgroundPaddingTop;
          }
          if (StickersAlert.this.gridView.getPaddingTop() != i)
          {
            StickersAlert.access$502(StickersAlert.this, true);
            StickersAlert.this.gridView.setPadding(AndroidUtilities.dp(10.0F), i, AndroidUtilities.dp(10.0F), 0);
            StickersAlert.this.emptyView.setPadding(0, i, 0, 0);
            StickersAlert.access$502(StickersAlert.this, false);
          }
          super.onMeasure(paramAnonymousInt1, View.MeasureSpec.makeMeasureSpec(Math.min(k, j), 1073741824));
          return;
          paramAnonymousInt2 = 0;
          break;
        }
      }
      
      public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        return (!StickersAlert.this.isDismissed()) && (super.onTouchEvent(paramAnonymousMotionEvent));
      }
      
      public void requestLayout()
      {
        if (StickersAlert.this.ignoreLayout) {
          return;
        }
        super.requestLayout();
      }
    };
    this.containerView.setWillNotDraw(false);
    this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
    this.titleTextView = new TextView(paramContext);
    this.titleTextView.setLines(1);
    this.titleTextView.setSingleLine(true);
    this.titleTextView.setTextColor(-14606047);
    this.titleTextView.setTextSize(1, 20.0F);
    this.titleTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
    this.titleTextView.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
    this.titleTextView.setGravity(16);
    this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.containerView.addView(this.titleTextView, LayoutHelper.createLinear(-1, 48));
    this.titleTextView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.shadow[0] = new View(paramContext);
    this.shadow[0].setBackgroundResource(2130837802);
    this.shadow[0].setAlpha(0.0F);
    this.shadow[0].setVisibility(4);
    this.shadow[0].setTag(Integer.valueOf(1));
    this.containerView.addView(this.shadow[0], LayoutHelper.createFrame(-1, 3.0F, 51, 0.0F, 48.0F, 0.0F, 0.0F));
    this.gridView = new RecyclerListView(paramContext)
    {
      public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        boolean bool1 = false;
        boolean bool2 = StickerPreviewViewer.getInstance().onInterceptTouchEvent(paramAnonymousMotionEvent, StickersAlert.this.gridView, 0);
        if ((super.onInterceptTouchEvent(paramAnonymousMotionEvent)) || (bool2)) {
          bool1 = true;
        }
        return bool1;
      }
      
      public void requestLayout()
      {
        if (StickersAlert.this.ignoreLayout) {
          return;
        }
        super.requestLayout();
      }
    };
    this.gridView.setTag(Integer.valueOf(14));
    paramInputStickerSet = this.gridView;
    paramTL_messages_stickerSet = new GridLayoutManager(getContext(), 5);
    this.layoutManager = paramTL_messages_stickerSet;
    paramInputStickerSet.setLayoutManager(paramTL_messages_stickerSet);
    paramInputStickerSet = this.gridView;
    paramTL_messages_stickerSet = new GridAdapter(paramContext);
    this.adapter = paramTL_messages_stickerSet;
    paramInputStickerSet.setAdapter(paramTL_messages_stickerSet);
    this.gridView.setVerticalScrollBarEnabled(false);
    this.gridView.addItemDecoration(new RecyclerView.ItemDecoration()
    {
      public void getItemOffsets(Rect paramAnonymousRect, View paramAnonymousView, RecyclerView paramAnonymousRecyclerView, RecyclerView.State paramAnonymousState)
      {
        paramAnonymousRect.left = 0;
        paramAnonymousRect.right = 0;
        paramAnonymousRect.bottom = 0;
        paramAnonymousRect.top = 0;
      }
    });
    this.gridView.setPadding(AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F), 0);
    this.gridView.setClipToPadding(false);
    this.gridView.setEnabled(true);
    this.gridView.setGlowColor(-657673);
    this.gridView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return StickerPreviewViewer.getInstance().onTouch(paramAnonymousMotionEvent, StickersAlert.this.gridView, 0, StickersAlert.this.stickersOnItemClickListener);
      }
    });
    this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        StickersAlert.this.updateLayout();
      }
    });
    this.stickersOnItemClickListener = new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        if ((StickersAlert.this.stickerSet == null) || (paramAnonymousInt < 0) || (paramAnonymousInt >= StickersAlert.this.stickerSet.documents.size())) {
          return;
        }
        StickersAlert.access$1102(StickersAlert.this, (TLRPC.Document)StickersAlert.this.stickerSet.documents.get(paramAnonymousInt));
        int j = 0;
        paramAnonymousInt = 0;
        for (;;)
        {
          int i = j;
          if (paramAnonymousInt < StickersAlert.this.selectedSticker.attributes.size())
          {
            paramAnonymousView = (TLRPC.DocumentAttribute)StickersAlert.this.selectedSticker.attributes.get(paramAnonymousInt);
            if (!(paramAnonymousView instanceof TLRPC.TL_documentAttributeSticker)) {
              break label357;
            }
            i = j;
            if (paramAnonymousView.alt != null)
            {
              i = j;
              if (paramAnonymousView.alt.length() > 0)
              {
                StickersAlert.this.stickerEmojiTextView.setText(Emoji.replaceEmoji(paramAnonymousView.alt, StickersAlert.this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0F), false));
                i = 1;
              }
            }
          }
          if (i == 0) {
            StickersAlert.this.stickerEmojiTextView.setText(Emoji.replaceEmoji(StickersQuery.getEmojiForSticker(StickersAlert.this.selectedSticker.id), StickersAlert.this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0F), false));
          }
          StickersAlert.this.stickerImageView.getImageReceiver().setImage(StickersAlert.this.selectedSticker, null, StickersAlert.this.selectedSticker.thumb.location, null, "webp", true);
          paramAnonymousView = (FrameLayout.LayoutParams)StickersAlert.this.stickerPreviewLayout.getLayoutParams();
          paramAnonymousView.topMargin = StickersAlert.this.scrollOffsetY;
          StickersAlert.this.stickerPreviewLayout.setLayoutParams(paramAnonymousView);
          StickersAlert.this.stickerPreviewLayout.setVisibility(0);
          paramAnonymousView = new AnimatorSet();
          paramAnonymousView.playTogether(new Animator[] { ObjectAnimator.ofFloat(StickersAlert.this.stickerPreviewLayout, "alpha", new float[] { 0.0F, 1.0F }) });
          paramAnonymousView.setDuration(200L);
          paramAnonymousView.start();
          return;
          label357:
          paramAnonymousInt += 1;
        }
      }
    };
    this.gridView.setOnItemClickListener(this.stickersOnItemClickListener);
    this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 48.0F, 0.0F, 48.0F));
    this.emptyView = new FrameLayout(paramContext)
    {
      public void requestLayout()
      {
        if (StickersAlert.this.ignoreLayout) {
          return;
        }
        super.requestLayout();
      }
    };
    this.containerView.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, 48.0F));
    this.gridView.setEmptyView(this.emptyView);
    this.emptyView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    paramInputStickerSet = new ProgressBar(paramContext);
    this.emptyView.addView(paramInputStickerSet, LayoutHelper.createFrame(-2, -2, 17));
    this.shadow[1] = new View(paramContext);
    this.shadow[1].setBackgroundResource(2130837803);
    this.containerView.addView(this.shadow[1], LayoutHelper.createFrame(-1, 3.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
    this.pickerBottomLayout = new PickerBottomLayout(paramContext, false);
    this.containerView.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
    this.pickerBottomLayout.cancelButton.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
    this.pickerBottomLayout.cancelButton.setTextColor(-12940081);
    this.pickerBottomLayout.cancelButton.setText(LocaleController.getString("Close", 2131165551).toUpperCase());
    this.pickerBottomLayout.cancelButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        StickersAlert.this.dismiss();
      }
    });
    this.pickerBottomLayout.doneButton.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
    this.pickerBottomLayout.doneButtonBadgeTextView.setBackgroundResource(2130838134);
    this.stickerPreviewLayout = new FrameLayout(paramContext);
    this.stickerPreviewLayout.setBackgroundColor(-536870913);
    this.stickerPreviewLayout.setVisibility(8);
    this.stickerPreviewLayout.setSoundEffectsEnabled(false);
    this.containerView.addView(this.stickerPreviewLayout, LayoutHelper.createFrame(-1, -1.0F));
    this.stickerPreviewLayout.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        StickersAlert.this.hidePreview();
      }
    });
    paramInputStickerSet = new ImageView(paramContext);
    paramInputStickerSet.setImageResource(2130837735);
    paramInputStickerSet.setScaleType(ImageView.ScaleType.CENTER);
    if (Build.VERSION.SDK_INT >= 21) {
      paramInputStickerSet.setBackgroundDrawable(Theme.createBarSelectorDrawable(-2697514));
    }
    this.stickerPreviewLayout.addView(paramInputStickerSet, LayoutHelper.createFrame(48, 48, 53));
    paramInputStickerSet.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        StickersAlert.this.hidePreview();
      }
    });
    this.stickerImageView = new BackupImageView(paramContext);
    this.stickerImageView.setAspectFit(true);
    int i = (int)(Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 2 / AndroidUtilities.density);
    this.stickerPreviewLayout.addView(this.stickerImageView, LayoutHelper.createFrame(i, i, 17));
    this.stickerEmojiTextView = new TextView(paramContext);
    this.stickerEmojiTextView.setTextSize(1, 30.0F);
    this.stickerEmojiTextView.setGravity(85);
    this.stickerPreviewLayout.addView(this.stickerEmojiTextView, LayoutHelper.createFrame(i, i, 17));
    this.previewSendButton = new TextView(paramContext);
    this.previewSendButton.setTextSize(1, 14.0F);
    this.previewSendButton.setTextColor(-12940081);
    this.previewSendButton.setGravity(17);
    this.previewSendButton.setBackgroundColor(-1);
    this.previewSendButton.setPadding(AndroidUtilities.dp(29.0F), 0, AndroidUtilities.dp(29.0F), 0);
    this.previewSendButton.setText(LocaleController.getString("Close", 2131165551).toUpperCase());
    this.previewSendButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.previewSendButton.setVisibility(8);
    this.stickerPreviewLayout.addView(this.previewSendButton, LayoutHelper.createFrame(-1, 48, 83));
    this.previewSendButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        StickersAlert.this.delegate.onStickerSelected(StickersAlert.this.selectedSticker);
        StickersAlert.this.dismiss();
      }
    });
    this.previewSendButtonShadow = new View(paramContext);
    this.previewSendButtonShadow.setBackgroundResource(2130837803);
    this.previewSendButtonShadow.setVisibility(8);
    this.stickerPreviewLayout.addView(this.previewSendButtonShadow, LayoutHelper.createFrame(-1, 3.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
    if (this.delegate != null)
    {
      this.previewSendButton.setText(LocaleController.getString("SendSticker", 2131166292).toUpperCase());
      this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(i, i, 17, 0.0F, 0.0F, 0.0F, 30.0F));
      this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(i, i, 17, 0.0F, 0.0F, 0.0F, 30.0F));
      this.previewSendButton.setVisibility(0);
      this.previewSendButtonShadow.setVisibility(0);
    }
    if ((this.stickerSet == null) && (this.inputStickerSet.short_name != null)) {
      this.stickerSet = StickersQuery.getStickerSetByName(this.inputStickerSet.short_name);
    }
    if (this.stickerSet == null) {
      this.stickerSet = StickersQuery.getStickerSetById(Long.valueOf(this.inputStickerSet.id));
    }
    if (this.stickerSet == null)
    {
      paramContext = new TLRPC.TL_messages_getStickerSet();
      paramContext.stickerset = this.inputStickerSet;
      ConnectionsManager.getInstance().sendRequest(paramContext, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              StickersAlert.access$1702(StickersAlert.this, 0);
              if (paramAnonymousTL_error == null)
              {
                StickersAlert.access$102(StickersAlert.this, (TLRPC.TL_messages_stickerSet)paramAnonymousTLObject);
                StickersAlert.this.updateFields();
                StickersAlert.this.adapter.notifyDataSetChanged();
                return;
              }
              Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("AddStickersNotFound", 2131165305), 0).show();
              StickersAlert.this.dismiss();
            }
          });
        }
      });
    }
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
    updateFields();
  }
  
  private void hidePreview()
  {
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.stickerPreviewLayout, "alpha", new float[] { 0.0F }) });
    localAnimatorSet.setDuration(200L);
    localAnimatorSet.addListener(new AnimatorListenerAdapterProxy()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        StickersAlert.this.stickerPreviewLayout.setVisibility(8);
      }
    });
    localAnimatorSet.start();
  }
  
  private void runShadowAnimation(final int paramInt, final boolean paramBoolean)
  {
    View localView;
    Object localObject;
    if (((paramBoolean) && (this.shadow[paramInt].getTag() != null)) || ((!paramBoolean) && (this.shadow[paramInt].getTag() == null)))
    {
      localView = this.shadow[paramInt];
      if (!paramBoolean) {
        break label190;
      }
      localObject = null;
      localView.setTag(localObject);
      if (paramBoolean) {
        this.shadow[paramInt].setVisibility(0);
      }
      if (this.shadowAnimation[paramInt] != null) {
        this.shadowAnimation[paramInt].cancel();
      }
      this.shadowAnimation[paramInt] = new AnimatorSet();
      localObject = this.shadowAnimation[paramInt];
      localView = this.shadow[paramInt];
      if (!paramBoolean) {
        break label199;
      }
    }
    label190:
    label199:
    for (float f = 1.0F;; f = 0.0F)
    {
      ((AnimatorSet)localObject).playTogether(new Animator[] { ObjectAnimator.ofFloat(localView, "alpha", new float[] { f }) });
      this.shadowAnimation[paramInt].setDuration(150L);
      this.shadowAnimation[paramInt].addListener(new AnimatorListenerAdapterProxy()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          if ((StickersAlert.this.shadowAnimation[paramInt] != null) && (StickersAlert.this.shadowAnimation[paramInt].equals(paramAnonymousAnimator))) {
            StickersAlert.this.shadowAnimation[paramInt] = null;
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((StickersAlert.this.shadowAnimation[paramInt] != null) && (StickersAlert.this.shadowAnimation[paramInt].equals(paramAnonymousAnimator)))
          {
            if (!paramBoolean) {
              StickersAlert.this.shadow[paramInt].setVisibility(4);
            }
            StickersAlert.this.shadowAnimation[paramInt] = null;
          }
        }
      });
      this.shadowAnimation[paramInt].start();
      return;
      localObject = Integer.valueOf(1);
      break;
    }
  }
  
  private void setRightButton(View.OnClickListener paramOnClickListener, String paramString, int paramInt, boolean paramBoolean)
  {
    if (paramString == null)
    {
      this.pickerBottomLayout.doneButton.setVisibility(8);
      return;
    }
    this.pickerBottomLayout.doneButton.setVisibility(0);
    if (paramBoolean)
    {
      this.pickerBottomLayout.doneButtonBadgeTextView.setVisibility(0);
      this.pickerBottomLayout.doneButtonBadgeTextView.setText(String.format("%d", new Object[] { Integer.valueOf(this.stickerSet.documents.size()) }));
    }
    for (;;)
    {
      this.pickerBottomLayout.doneButtonTextView.setTextColor(paramInt);
      this.pickerBottomLayout.doneButtonTextView.setText(paramString.toUpperCase());
      this.pickerBottomLayout.doneButton.setOnClickListener(paramOnClickListener);
      return;
      this.pickerBottomLayout.doneButtonBadgeTextView.setVisibility(8);
    }
  }
  
  private void updateFields()
  {
    if (this.titleTextView == null) {
      return;
    }
    if (this.stickerSet != null)
    {
      this.titleTextView.setText(this.stickerSet.set.title);
      if ((this.stickerSet.set == null) || (!StickersQuery.isStickerPackInstalled(this.stickerSet.set.id))) {
        setRightButton(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            StickersAlert.this.dismiss();
            paramAnonymousView = new TLRPC.TL_messages_installStickerSet();
            paramAnonymousView.stickerset = StickersAlert.this.inputStickerSet;
            ConnectionsManager.getInstance().sendRequest(paramAnonymousView, new RequestDelegate()
            {
              public void run(TLObject paramAnonymous2TLObject, final TLRPC.TL_error paramAnonymous2TL_error)
              {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    try
                    {
                      if (paramAnonymous2TL_error == null) {
                        Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("AddStickersInstalled", 2131165304), 0).show();
                      }
                      for (;;)
                      {
                        StickersQuery.loadStickers(false, true);
                        return;
                        if (!paramAnonymous2TL_error.text.equals("STICKERSETS_TOO_MUCH")) {
                          break;
                        }
                        Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("TooMuchStickersets", 2131166408), 0).show();
                      }
                    }
                    catch (Exception localException)
                    {
                      for (;;)
                      {
                        FileLog.e("tmessages", localException);
                        continue;
                        Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("ErrorOccurred", 2131165672), 0).show();
                      }
                    }
                  }
                });
              }
            });
          }
        }, LocaleController.getString("AddStickers", 2131165303), -12940081, true);
      }
      for (;;)
      {
        this.adapter.notifyDataSetChanged();
        return;
        if (this.stickerSet.set.official) {
          setRightButton(null, null, -3319206, false);
        } else {
          setRightButton(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              StickersAlert.this.dismiss();
              StickersQuery.removeStickersSet(StickersAlert.this.getContext(), StickersAlert.this.stickerSet.set, 0);
            }
          }, LocaleController.getString("StickersRemove", 2131166368), -3319206, false);
        }
      }
    }
    setRightButton(null, null, -3319206, false);
  }
  
  @SuppressLint({"NewApi"})
  private void updateLayout()
  {
    Object localObject;
    int i;
    if (this.gridView.getChildCount() <= 0)
    {
      localObject = this.gridView;
      i = this.gridView.getPaddingTop();
      this.scrollOffsetY = i;
      ((RecyclerListView)localObject).setTopGlowOffset(i);
      this.titleTextView.setTranslationY(this.scrollOffsetY);
      this.shadow[0].setTranslationY(this.scrollOffsetY);
      this.containerView.invalidate();
    }
    for (;;)
    {
      return;
      localObject = this.gridView.getChildAt(0);
      StickersAlert.GridAdapter.Holder localHolder = (StickersAlert.GridAdapter.Holder)this.gridView.findContainingViewHolder((View)localObject);
      i = ((View)localObject).getTop();
      int j = 0;
      if ((i >= 0) && (localHolder != null) && (localHolder.getAdapterPosition() == 0)) {
        runShadowAnimation(0, false);
      }
      while (this.scrollOffsetY != i)
      {
        localObject = this.gridView;
        this.scrollOffsetY = i;
        ((RecyclerListView)localObject).setTopGlowOffset(i);
        this.titleTextView.setTranslationY(this.scrollOffsetY);
        this.shadow[0].setTranslationY(this.scrollOffsetY);
        this.containerView.invalidate();
        return;
        runShadowAnimation(0, true);
        i = j;
      }
    }
  }
  
  protected boolean canDismissWithSwipe()
  {
    return false;
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.emojiDidLoaded)
    {
      if (this.gridView != null) {
        this.gridView.invalidateViews();
      }
      if (StickerPreviewViewer.getInstance().isVisible()) {
        StickerPreviewViewer.getInstance().close();
      }
      StickerPreviewViewer.getInstance().reset();
    }
  }
  
  public void dismiss()
  {
    super.dismiss();
    if (this.reqId != 0)
    {
      ConnectionsManager.getInstance().cancelRequest(this.reqId, true);
      this.reqId = 0;
    }
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
  }
  
  private class GridAdapter
    extends RecyclerView.Adapter
  {
    Context context;
    
    public GridAdapter(Context paramContext)
    {
      this.context = paramContext;
    }
    
    public int getItemCount()
    {
      if (StickersAlert.this.stickerSet != null) {
        return StickersAlert.this.stickerSet.documents.size();
      }
      return 0;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      ((StickerEmojiCell)paramViewHolder.itemView).setSticker((TLRPC.Document)StickersAlert.this.stickerSet.documents.get(paramInt), true);
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = new StickerEmojiCell(this.context);
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(82.0F)));
      return new Holder(paramViewGroup);
    }
    
    private class Holder
      extends RecyclerView.ViewHolder
    {
      public Holder(View paramView)
      {
        super();
      }
    }
  }
  
  public static abstract interface StickersAlertDelegate
  {
    public abstract void onStickerSelected(TLRPC.Document paramDocument);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Components\StickersAlert.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */