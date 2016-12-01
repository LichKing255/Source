package org.telegram.ui.Components;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.GridLayoutManager.SpanSizeLookup;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputDocument;
import org.telegram.tgnet.TLRPC.TL_messages_getSavedGifs;
import org.telegram.tgnet.TLRPC.TL_messages_saveGif;
import org.telegram.tgnet.TLRPC.TL_messages_savedGifs;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Supergram.Theming.MihanTheme;
import org.telegram.ui.StickerPreviewViewer;

public class EmojiView
  extends FrameLayout
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final ViewTreeObserver.OnScrollChangedListener NOP = new ViewTreeObserver.OnScrollChangedListener()
  {
    public void onScrollChanged() {}
  };
  private static HashMap<String, String> emojiColor = new HashMap();
  private static final Field superListenerField;
  private ArrayList<EmojiGridAdapter> adapters = new ArrayList();
  private ImageView backspaceButton;
  private boolean backspaceOnce;
  private boolean backspacePressed;
  private int emojiSize;
  private HashMap<String, Integer> emojiUseHistory = new HashMap();
  private ExtendedGridLayoutManager flowLayoutManager;
  private int gifTabBum = -2;
  private GifsAdapter gifsAdapter;
  private RecyclerListView gifsGridView;
  private int[] icons = { 2130837851, 2130837852, 2130837850, 2130837848, 2130837849, 2130837854, 2130837853 };
  private long lastGifLoadTime;
  private int lastNotifyWidth;
  private Listener listener;
  private boolean loadingRecent;
  private boolean loadingRecentGifs;
  private int[] location = new int[2];
  private ArrayList<Long> newRecentStickers = new ArrayList();
  private int oldWidth;
  private ViewPager pager;
  private LinearLayout pagerSlidingTabStripContainer;
  private EmojiColorPickerView pickerView;
  private EmojiPopupWindow pickerViewPopup;
  private int popupHeight;
  private int popupWidth;
  private ArrayList<String> recentEmoji = new ArrayList();
  private ArrayList<MediaController.SearchImage> recentImages;
  private ArrayList<TLRPC.Document> recentStickers = new ArrayList();
  private int recentTabBum = -2;
  private FrameLayout recentsWrap;
  private ScrollSlidingTabStrip scrollSlidingTabStrip;
  private boolean showGifs;
  private boolean showStickers;
  private ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = new ArrayList();
  private TextView stickersEmptyView;
  private StickersGridAdapter stickersGridAdapter;
  private GridView stickersGridView;
  private AdapterView.OnItemClickListener stickersOnItemClickListener;
  private int stickersTabOffset;
  private FrameLayout stickersWrap;
  private boolean switchToGifTab;
  private ArrayList<GridView> views = new ArrayList();
  
  static
  {
    Object localObject = null;
    try
    {
      Field localField = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
      localObject = localField;
      localField.setAccessible(true);
      localObject = localField;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      for (;;) {}
    }
    superListenerField = (Field)localObject;
  }
  
  public EmojiView(boolean paramBoolean1, boolean paramBoolean2, Context paramContext)
  {
    super(paramContext);
    this.showStickers = paramBoolean1;
    this.showGifs = paramBoolean2;
    int i = 0;
    if (i < EmojiData.dataColored.length + 1)
    {
      localObject1 = new GridView(paramContext);
      if (AndroidUtilities.isTablet()) {
        ((GridView)localObject1).setColumnWidth(AndroidUtilities.dp(60.0F));
      }
      for (;;)
      {
        ((GridView)localObject1).setNumColumns(-1);
        this.views.add(localObject1);
        localObject2 = new EmojiGridAdapter(i - 1);
        ((GridView)localObject1).setAdapter((ListAdapter)localObject2);
        AndroidUtilities.setListViewEdgeEffectColor((AbsListView)localObject1, -657673);
        this.adapters.add(localObject2);
        i += 1;
        break;
        ((GridView)localObject1).setColumnWidth(AndroidUtilities.dp(45.0F));
      }
    }
    if (this.showStickers)
    {
      StickersQuery.checkStickers();
      this.stickersGridView = new GridView(paramContext)
      {
        public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          boolean bool = StickerPreviewViewer.getInstance().onInterceptTouchEvent(paramAnonymousMotionEvent, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight());
          return (super.onInterceptTouchEvent(paramAnonymousMotionEvent)) || (bool);
        }
        
        public void setVisibility(int paramAnonymousInt)
        {
          if ((EmojiView.this.gifsGridView != null) && (EmojiView.this.gifsGridView.getVisibility() == 0))
          {
            super.setVisibility(8);
            return;
          }
          super.setVisibility(paramAnonymousInt);
        }
      };
      this.stickersGridView.setSelector(2130838195);
      this.stickersGridView.setColumnWidth(AndroidUtilities.dp(72.0F));
      this.stickersGridView.setNumColumns(-1);
      this.stickersGridView.setPadding(0, AndroidUtilities.dp(4.0F), 0, 0);
      this.stickersGridView.setClipToPadding(false);
      this.views.add(this.stickersGridView);
      this.stickersGridAdapter = new StickersGridAdapter(paramContext);
      this.stickersGridView.setAdapter(this.stickersGridAdapter);
      this.stickersGridView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return StickerPreviewViewer.getInstance().onTouch(paramAnonymousMotionEvent, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight(), EmojiView.this.stickersOnItemClickListener);
        }
      });
      this.stickersOnItemClickListener = new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          if (!(paramAnonymousView instanceof StickerEmojiCell)) {}
          do
          {
            do
            {
              return;
              StickerPreviewViewer.getInstance().reset();
              paramAnonymousAdapterView = (StickerEmojiCell)paramAnonymousView;
            } while (paramAnonymousAdapterView.isDisabled());
            paramAnonymousAdapterView.disable();
            paramAnonymousAdapterView = paramAnonymousAdapterView.getSticker();
            EmojiView.this.addRecentSticker(paramAnonymousAdapterView);
          } while (EmojiView.this.listener == null);
          EmojiView.this.listener.onStickerSelected(paramAnonymousAdapterView);
        }
      };
      this.stickersGridView.setOnItemClickListener(this.stickersOnItemClickListener);
      AndroidUtilities.setListViewEdgeEffectColor(this.stickersGridView, -657673);
      this.stickersWrap = new FrameLayout(paramContext);
      this.stickersWrap.addView(this.stickersGridView);
      if (paramBoolean2)
      {
        this.gifsGridView = new RecyclerListView(paramContext);
        this.gifsGridView.setTag(Integer.valueOf(11));
        localObject1 = this.gifsGridView;
        localObject2 = new ExtendedGridLayoutManager(paramContext, 100)
        {
          private Size size = new Size();
          
          protected Size getSizeForItem(int paramAnonymousInt)
          {
            float f2 = 100.0F;
            TLRPC.Document localDocument = ((MediaController.SearchImage)EmojiView.this.recentImages.get(paramAnonymousInt)).document;
            Object localObject = this.size;
            float f1;
            if ((localDocument.thumb != null) && (localDocument.thumb.w != 0))
            {
              f1 = localDocument.thumb.w;
              ((Size)localObject).width = f1;
              localObject = this.size;
              f1 = f2;
              if (localDocument.thumb != null)
              {
                f1 = f2;
                if (localDocument.thumb.h != 0) {
                  f1 = localDocument.thumb.h;
                }
              }
              ((Size)localObject).height = f1;
              paramAnonymousInt = 0;
            }
            for (;;)
            {
              if (paramAnonymousInt < localDocument.attributes.size())
              {
                localObject = (TLRPC.DocumentAttribute)localDocument.attributes.get(paramAnonymousInt);
                if (((localObject instanceof TLRPC.TL_documentAttributeImageSize)) || ((localObject instanceof TLRPC.TL_documentAttributeVideo)))
                {
                  this.size.width = ((TLRPC.DocumentAttribute)localObject).w;
                  this.size.height = ((TLRPC.DocumentAttribute)localObject).h;
                }
              }
              else
              {
                return this.size;
                f1 = 100.0F;
                break;
              }
              paramAnonymousInt += 1;
            }
          }
        };
        this.flowLayoutManager = ((ExtendedGridLayoutManager)localObject2);
        ((RecyclerListView)localObject1).setLayoutManager((RecyclerView.LayoutManager)localObject2);
        this.flowLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
        {
          public int getSpanSize(int paramAnonymousInt)
          {
            return EmojiView.this.flowLayoutManager.getSpanSizeForItem(paramAnonymousInt);
          }
        });
        this.gifsGridView.addItemDecoration(new RecyclerView.ItemDecoration()
        {
          public void getItemOffsets(Rect paramAnonymousRect, View paramAnonymousView, RecyclerView paramAnonymousRecyclerView, RecyclerView.State paramAnonymousState)
          {
            int i = 0;
            paramAnonymousRect.left = 0;
            paramAnonymousRect.top = 0;
            paramAnonymousRect.bottom = 0;
            int j = paramAnonymousRecyclerView.getChildAdapterPosition(paramAnonymousView);
            if (!EmojiView.this.flowLayoutManager.isFirstRow(j)) {
              paramAnonymousRect.top = AndroidUtilities.dp(2.0F);
            }
            if (EmojiView.this.flowLayoutManager.isLastInRow(j)) {}
            for (;;)
            {
              paramAnonymousRect.right = i;
              return;
              i = AndroidUtilities.dp(2.0F);
            }
          }
        });
        this.gifsGridView.setOverScrollMode(2);
        localObject1 = this.gifsGridView;
        localObject2 = new GifsAdapter(paramContext);
        this.gifsAdapter = ((GifsAdapter)localObject2);
        ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
        this.gifsGridView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
        {
          public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
          {
            if ((paramAnonymousInt < 0) || (paramAnonymousInt >= EmojiView.this.recentImages.size()) || (EmojiView.this.listener == null)) {
              return;
            }
            paramAnonymousView = ((MediaController.SearchImage)EmojiView.this.recentImages.get(paramAnonymousInt)).document;
            EmojiView.this.listener.onGifSelected(paramAnonymousView);
          }
        });
        this.gifsGridView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
        {
          public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
          {
            if ((paramAnonymousInt < 0) || (paramAnonymousInt >= EmojiView.this.recentImages.size())) {
              return false;
            }
            final MediaController.SearchImage localSearchImage = (MediaController.SearchImage)EmojiView.this.recentImages.get(paramAnonymousInt);
            paramAnonymousView = new AlertDialog.Builder(paramAnonymousView.getContext());
            paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165338));
            paramAnonymousView.setMessage(LocaleController.getString("DeleteGif", 2131165613));
            paramAnonymousView.setPositiveButton(LocaleController.getString("OK", 2131166111).toUpperCase(), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                EmojiView.this.recentImages.remove(localSearchImage);
                paramAnonymous2DialogInterface = new TLRPC.TL_messages_saveGif();
                paramAnonymous2DialogInterface.id = new TLRPC.TL_inputDocument();
                paramAnonymous2DialogInterface.id.id = localSearchImage.document.id;
                paramAnonymous2DialogInterface.id.access_hash = localSearchImage.document.access_hash;
                paramAnonymous2DialogInterface.unsave = true;
                ConnectionsManager.getInstance().sendRequest(paramAnonymous2DialogInterface, new RequestDelegate()
                {
                  public void run(TLObject paramAnonymous3TLObject, TLRPC.TL_error paramAnonymous3TL_error) {}
                });
                MessagesStorage.getInstance().removeWebRecent(localSearchImage);
                if (EmojiView.this.gifsAdapter != null) {
                  EmojiView.this.gifsAdapter.notifyDataSetChanged();
                }
                if (EmojiView.this.recentImages.isEmpty()) {
                  EmojiView.this.updateStickerTabs();
                }
              }
            });
            paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
            paramAnonymousView.show().setCanceledOnTouchOutside(true);
            return true;
          }
        });
        this.gifsGridView.setVisibility(8);
        this.stickersWrap.addView(this.gifsGridView);
      }
      this.stickersEmptyView = new TextView(paramContext);
      this.stickersEmptyView.setText(LocaleController.getString("NoStickers", 2131166028));
      this.stickersEmptyView.setTextSize(1, 18.0F);
      this.stickersEmptyView.setTextColor(-7829368);
      this.stickersWrap.addView(this.stickersEmptyView, LayoutHelper.createFrame(-2, -2, 17));
      this.stickersGridView.setEmptyView(this.stickersEmptyView);
      this.scrollSlidingTabStrip = new ScrollSlidingTabStrip(paramContext)
      {
        boolean first = true;
        float lastTranslateX;
        float lastX;
        boolean startedScroll;
        
        public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
          }
          return super.onInterceptTouchEvent(paramAnonymousMotionEvent);
        }
        
        public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          boolean bool = false;
          if (this.first)
          {
            this.first = false;
            this.lastX = paramAnonymousMotionEvent.getX();
          }
          float f = EmojiView.this.scrollSlidingTabStrip.getTranslationX();
          if ((EmojiView.this.scrollSlidingTabStrip.getScrollX() == 0) && (f == 0.0F))
          {
            if ((this.startedScroll) || (this.lastX - paramAnonymousMotionEvent.getX() >= 0.0F)) {
              break label220;
            }
            if (EmojiView.this.pager.beginFakeDrag())
            {
              this.startedScroll = true;
              this.lastTranslateX = EmojiView.this.scrollSlidingTabStrip.getTranslationX();
            }
          }
          int i;
          if (this.startedScroll) {
            i = (int)(paramAnonymousMotionEvent.getX() - this.lastX + f - this.lastTranslateX);
          }
          for (;;)
          {
            label220:
            try
            {
              EmojiView.this.pager.fakeDragBy(i);
              this.lastTranslateX = f;
              this.lastX = paramAnonymousMotionEvent.getX();
              if ((paramAnonymousMotionEvent.getAction() == 3) || (paramAnonymousMotionEvent.getAction() == 1))
              {
                this.first = true;
                if (this.startedScroll)
                {
                  EmojiView.this.pager.endFakeDrag();
                  this.startedScroll = false;
                }
              }
              if ((this.startedScroll) || (super.onTouchEvent(paramAnonymousMotionEvent))) {
                bool = true;
              }
              return bool;
            }
            catch (Exception localException1) {}
            if ((!this.startedScroll) || (this.lastX - paramAnonymousMotionEvent.getX() <= 0.0F) || (!EmojiView.this.pager.isFakeDragging())) {
              break;
            }
            EmojiView.this.pager.endFakeDrag();
            this.startedScroll = false;
            break;
            try
            {
              EmojiView.this.pager.endFakeDrag();
              this.startedScroll = false;
              FileLog.e("tmessages", localException1);
            }
            catch (Exception localException2)
            {
              for (;;) {}
            }
          }
        }
      };
      this.scrollSlidingTabStrip.setUnderlineHeight(AndroidUtilities.dp(1.0F));
      this.scrollSlidingTabStrip.setIndicatorColor(-1907225);
      this.scrollSlidingTabStrip.setUnderlineColor(-1907225);
      this.scrollSlidingTabStrip.setVisibility(4);
      addView(this.scrollSlidingTabStrip, LayoutHelper.createFrame(-1, 48, 51));
      this.scrollSlidingTabStrip.setTranslationX(AndroidUtilities.displaySize.x);
      updateStickerTabs();
      this.scrollSlidingTabStrip.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate()
      {
        public void onPageSelected(int paramAnonymousInt)
        {
          int i = 8;
          if (EmojiView.this.gifsGridView != null)
          {
            if (paramAnonymousInt != EmojiView.this.gifTabBum + 1) {
              break label75;
            }
            if (EmojiView.this.gifsGridView.getVisibility() != 0)
            {
              EmojiView.this.listener.onGifTab(true);
              EmojiView.this.showGifTab();
            }
          }
          label75:
          do
          {
            do
            {
              while (paramAnonymousInt == 0)
              {
                EmojiView.this.pager.setCurrentItem(0);
                return;
                if (EmojiView.this.gifsGridView.getVisibility() == 0)
                {
                  EmojiView.this.listener.onGifTab(false);
                  EmojiView.this.gifsGridView.setVisibility(8);
                  EmojiView.this.stickersGridView.setVisibility(0);
                  TextView localTextView = EmojiView.this.stickersEmptyView;
                  if (EmojiView.this.stickersGridAdapter.getCount() != 0) {}
                  for (;;)
                  {
                    localTextView.setVisibility(i);
                    break;
                    i = 0;
                  }
                }
              }
            } while (paramAnonymousInt == EmojiView.this.gifTabBum + 1);
            if (paramAnonymousInt == EmojiView.this.recentTabBum + 1)
            {
              ((GridView)EmojiView.this.views.get(6)).setSelection(0);
              return;
            }
            i = paramAnonymousInt - 1 - EmojiView.this.stickersTabOffset;
            if (i != EmojiView.this.stickerSets.size()) {
              break;
            }
          } while (EmojiView.this.listener == null);
          EmojiView.this.listener.onStickersSettingsClick();
          return;
          paramAnonymousInt = i;
          if (i >= EmojiView.this.stickerSets.size()) {
            paramAnonymousInt = EmojiView.this.stickerSets.size() - 1;
          }
          ((GridView)EmojiView.this.views.get(6)).setSelection(EmojiView.this.stickersGridAdapter.getPositionForPack((TLRPC.TL_messages_stickerSet)EmojiView.this.stickerSets.get(paramAnonymousInt)));
        }
      });
      this.stickersGridView.setOnScrollListener(new AbsListView.OnScrollListener()
      {
        public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
        {
          EmojiView.this.checkStickersScroll(paramAnonymousInt1);
        }
        
        public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt) {}
      });
    }
    setBackgroundColor(-657673);
    this.pager = new ViewPager(paramContext)
    {
      public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if (getParent() != null) {
          getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(paramAnonymousMotionEvent);
      }
    };
    this.pager.setAdapter(new EmojiPagesAdapter(null));
    this.pagerSlidingTabStripContainer = new LinearLayout(paramContext)
    {
      public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if (getParent() != null) {
          getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(paramAnonymousMotionEvent);
      }
    };
    this.pagerSlidingTabStripContainer.setOrientation(0);
    this.pagerSlidingTabStripContainer.setBackgroundColor(-657673);
    Object localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    i = ((SharedPreferences)localObject1).getInt("theme_emoji_tab_icolor", -5723992);
    int j = ((SharedPreferences)localObject1).getInt("theme_emoji_tab_scolor", -13920542);
    int k = ((SharedPreferences)localObject1).getInt("theme_emoji_tab_ucolor", -1907225);
    Object localObject2 = new StateListDrawable();
    ColorDrawable localColorDrawable = new ColorDrawable(k);
    ((StateListDrawable)localObject2).addState(new int[0], localColorDrawable);
    localColorDrawable = new ColorDrawable(j);
    ((StateListDrawable)localObject2).addState(new int[] { 16842919 }, localColorDrawable);
    updateColors((SharedPreferences)localObject1, this.pagerSlidingTabStripContainer);
    addView(this.pagerSlidingTabStripContainer, LayoutHelper.createFrame(-1, 48.0F));
    localObject1 = new PagerSlidingTabStrip(paramContext);
    ((PagerSlidingTabStrip)localObject1).setViewPager(this.pager);
    ((PagerSlidingTabStrip)localObject1).setShouldExpand(true);
    ((PagerSlidingTabStrip)localObject1).setIndicatorHeight(AndroidUtilities.dp(2.0F));
    ((PagerSlidingTabStrip)localObject1).setUnderlineHeight(AndroidUtilities.dp(1.0F));
    ((PagerSlidingTabStrip)localObject1).setIndicatorColor(j);
    ((PagerSlidingTabStrip)localObject1).setUnderlineColor(k);
    this.pagerSlidingTabStripContainer.addView((View)localObject1, LayoutHelper.createLinear(0, 48, 1.0F));
    ((PagerSlidingTabStrip)localObject1).setOnPageChangeListener(new ViewPager.OnPageChangeListener()
    {
      public void onPageScrollStateChanged(int paramAnonymousInt) {}
      
      public void onPageScrolled(int paramAnonymousInt1, float paramAnonymousFloat, int paramAnonymousInt2)
      {
        EmojiView.this.onPageScrolled(paramAnonymousInt1, EmojiView.this.getMeasuredWidth(), paramAnonymousInt2);
      }
      
      public void onPageSelected(int paramAnonymousInt) {}
    });
    localObject1 = new FrameLayout(paramContext);
    this.pagerSlidingTabStripContainer.addView((View)localObject1, LayoutHelper.createLinear(52, 48));
    this.backspaceButton = new ImageView(paramContext)
    {
      public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if (paramAnonymousMotionEvent.getAction() == 0)
        {
          EmojiView.access$4202(EmojiView.this, true);
          EmojiView.access$4302(EmojiView.this, false);
          EmojiView.this.postBackspaceRunnable(350);
        }
        for (;;)
        {
          super.onTouchEvent(paramAnonymousMotionEvent);
          return true;
          if ((paramAnonymousMotionEvent.getAction() == 3) || (paramAnonymousMotionEvent.getAction() == 1))
          {
            EmojiView.access$4202(EmojiView.this, false);
            if ((!EmojiView.this.backspaceOnce) && (EmojiView.this.listener != null) && (EmojiView.this.listener.onBackspace())) {
              EmojiView.this.backspaceButton.performHapticFeedback(3);
            }
          }
        }
      }
    };
    this.backspaceButton.setImageResource(2130837874);
    MihanTheme.setColorFilter(this.backspaceButton, i);
    this.backspaceButton.setBackgroundResource(2130837847);
    this.backspaceButton.setScaleType(ImageView.ScaleType.CENTER);
    ((FrameLayout)localObject1).addView(this.backspaceButton, LayoutHelper.createFrame(52, 48.0F));
    this.recentsWrap = new FrameLayout(paramContext);
    this.recentsWrap.addView((View)this.views.get(0));
    localObject1 = new TextView(paramContext);
    ((TextView)localObject1).setText(LocaleController.getString("NoRecent", 2131166017));
    ((TextView)localObject1).setTextSize(18.0F);
    ((TextView)localObject1).setTextColor(-7829368);
    ((TextView)localObject1).setGravity(17);
    this.recentsWrap.addView((View)localObject1);
    ((GridView)this.views.get(0)).setEmptyView((View)localObject1);
    addView(this.pager, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 48.0F, 0.0F, 0.0F));
    if (AndroidUtilities.isTablet())
    {
      f = 40.0F;
      this.emojiSize = AndroidUtilities.dp(f);
      this.pickerView = new EmojiColorPickerView(paramContext);
      paramContext = this.pickerView;
      if (!AndroidUtilities.isTablet()) {
        break label1530;
      }
      i = 40;
      label1392:
      i = AndroidUtilities.dp(i * 6 + 10 + 20);
      this.popupWidth = i;
      if (!AndroidUtilities.isTablet()) {
        break label1537;
      }
    }
    label1530:
    label1537:
    for (float f = 64.0F;; f = 56.0F)
    {
      j = AndroidUtilities.dp(f);
      this.popupHeight = j;
      this.pickerViewPopup = new EmojiPopupWindow(paramContext, i, j);
      this.pickerViewPopup.setOutsideTouchable(true);
      this.pickerViewPopup.setClippingEnabled(true);
      this.pickerViewPopup.setInputMethodMode(2);
      this.pickerViewPopup.setSoftInputMode(0);
      this.pickerViewPopup.getContentView().setFocusableInTouchMode(true);
      this.pickerViewPopup.getContentView().setOnKeyListener(new View.OnKeyListener()
      {
        public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if ((paramAnonymousInt == 82) && (paramAnonymousKeyEvent.getRepeatCount() == 0) && (paramAnonymousKeyEvent.getAction() == 1) && (EmojiView.this.pickerViewPopup != null) && (EmojiView.this.pickerViewPopup.isShowing()))
          {
            EmojiView.this.pickerViewPopup.dismiss();
            return true;
          }
          return false;
        }
      });
      loadRecents();
      return;
      f = 32.0F;
      break;
      i = 32;
      break label1392;
    }
  }
  
  private int calcGifsHash(ArrayList<MediaController.SearchImage> paramArrayList)
  {
    if (paramArrayList == null) {
      return 0;
    }
    long l = 0L;
    int i = 0;
    if (i < Math.min(200, paramArrayList.size()))
    {
      MediaController.SearchImage localSearchImage = (MediaController.SearchImage)paramArrayList.get(i);
      if (localSearchImage.document == null) {}
      for (;;)
      {
        i += 1;
        break;
        int j = (int)(localSearchImage.document.id >> 32);
        int k = (int)localSearchImage.document.id;
        l = ((l * 20261L + 2147483648L + j) % 2147483648L * 20261L + 2147483648L + k) % 2147483648L;
      }
    }
    return (int)l;
  }
  
  private void checkStickersScroll(int paramInt)
  {
    if (this.stickersGridView == null) {
      return;
    }
    if (this.stickersGridView.getVisibility() != 0)
    {
      localObject = this.scrollSlidingTabStrip;
      i = this.gifTabBum;
      if (this.recentTabBum > 0) {}
      for (paramInt = this.recentTabBum;; paramInt = this.stickersTabOffset)
      {
        ((ScrollSlidingTabStrip)localObject).onPageScrolled(i + 1, paramInt + 1);
        return;
      }
    }
    int k = this.stickersGridView.getChildCount();
    int j = 0;
    int i = paramInt;
    paramInt = j;
    while (paramInt < k)
    {
      localObject = this.stickersGridView.getChildAt(paramInt);
      if (((View)localObject).getHeight() + ((View)localObject).getTop() >= AndroidUtilities.dp(5.0F)) {
        break;
      }
      i += 1;
      paramInt += 1;
    }
    Object localObject = this.scrollSlidingTabStrip;
    i = this.stickersGridAdapter.getTabForPosition(i);
    if (this.recentTabBum > 0) {}
    for (paramInt = this.recentTabBum;; paramInt = this.stickersTabOffset)
    {
      ((ScrollSlidingTabStrip)localObject).onPageScrolled(i + 1, paramInt + 1);
      return;
    }
  }
  
  private String convert(long paramLong)
  {
    Object localObject1 = "";
    int i = 0;
    for (;;)
    {
      if (i >= 4) {
        return (String)localObject1;
      }
      int j = (int)(0xFFFF & paramLong >> (3 - i) * 16);
      Object localObject2 = localObject1;
      if (j != 0) {
        localObject2 = (String)localObject1 + (char)j;
      }
      i += 1;
      localObject1 = localObject2;
    }
  }
  
  private void onPageScrolled(int paramInt1, int paramInt2, int paramInt3)
  {
    boolean bool = true;
    int j = 0;
    if (this.scrollSlidingTabStrip == null) {
      return;
    }
    int i = paramInt2;
    if (paramInt2 == 0) {
      i = AndroidUtilities.displaySize.x;
    }
    paramInt2 = 0;
    Object localObject;
    if (paramInt1 == 5)
    {
      paramInt2 = -paramInt3;
      paramInt1 = paramInt2;
      if (this.listener != null)
      {
        localObject = this.listener;
        if (paramInt3 != 0)
        {
          label58:
          ((Listener)localObject).onStickersTab(bool);
          paramInt1 = paramInt2;
        }
      }
      else
      {
        label69:
        if (this.pagerSlidingTabStripContainer.getTranslationX() == paramInt1) {
          break label185;
        }
        this.pagerSlidingTabStripContainer.setTranslationX(paramInt1);
        this.scrollSlidingTabStrip.setTranslationX(i + paramInt1);
        localObject = this.scrollSlidingTabStrip;
        if (paramInt1 >= 0) {
          break label187;
        }
      }
    }
    label185:
    label187:
    for (paramInt1 = j;; paramInt1 = 4)
    {
      ((ScrollSlidingTabStrip)localObject).setVisibility(paramInt1);
      return;
      bool = false;
      break label58;
      if (paramInt1 == 6)
      {
        paramInt2 = -i;
        paramInt1 = paramInt2;
        if (this.listener == null) {
          break label69;
        }
        this.listener.onStickersTab(true);
        paramInt1 = paramInt2;
        break label69;
      }
      paramInt1 = paramInt2;
      if (this.listener == null) {
        break label69;
      }
      this.listener.onStickersTab(false);
      paramInt1 = paramInt2;
      break label69;
      break;
    }
  }
  
  private void postBackspaceRunnable(final int paramInt)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (!EmojiView.this.backspacePressed) {
          return;
        }
        if ((EmojiView.this.listener != null) && (EmojiView.this.listener.onBackspace())) {
          EmojiView.this.backspaceButton.performHapticFeedback(3);
        }
        EmojiView.access$4302(EmojiView.this, true);
        EmojiView.this.postBackspaceRunnable(Math.max(50, paramInt - 100));
      }
    }, paramInt);
  }
  
  private void reloadStickersAdapter()
  {
    if (this.stickersGridAdapter != null) {
      this.stickersGridAdapter.notifyDataSetChanged();
    }
    if (StickerPreviewViewer.getInstance().isVisible()) {
      StickerPreviewViewer.getInstance().close();
    }
    StickerPreviewViewer.getInstance().reset();
  }
  
  private void saveEmojiColors()
  {
    SharedPreferences localSharedPreferences = getContext().getSharedPreferences("emoji", 0);
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = emojiColor.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (localStringBuilder.length() != 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append((String)localEntry.getKey());
      localStringBuilder.append("=");
      localStringBuilder.append((String)localEntry.getValue());
    }
    localSharedPreferences.edit().putString("color", localStringBuilder.toString()).commit();
  }
  
  private void saveRecentEmoji()
  {
    SharedPreferences localSharedPreferences = getContext().getSharedPreferences("emoji", 0);
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = this.emojiUseHistory.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (localStringBuilder.length() != 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append((String)localEntry.getKey());
      localStringBuilder.append("=");
      localStringBuilder.append(localEntry.getValue());
    }
    localSharedPreferences.edit().putString("emojis2", localStringBuilder.toString()).commit();
  }
  
  private void saveRecentStickers()
  {
    SharedPreferences.Editor localEditor = getContext().getSharedPreferences("emoji", 0).edit();
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while (i < this.newRecentStickers.size())
    {
      if (localStringBuilder.length() != 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append(this.newRecentStickers.get(i));
      i += 1;
    }
    localEditor.putString("stickers2", localStringBuilder.toString());
    localEditor.commit();
  }
  
  private void showGifTab()
  {
    this.gifsGridView.setVisibility(0);
    this.stickersGridView.setVisibility(8);
    this.stickersEmptyView.setVisibility(8);
    ScrollSlidingTabStrip localScrollSlidingTabStrip = this.scrollSlidingTabStrip;
    int j = this.gifTabBum;
    if (this.recentTabBum > 0) {}
    for (int i = this.recentTabBum;; i = this.stickersTabOffset)
    {
      localScrollSlidingTabStrip.onPageScrolled(j + 1, i + 1);
      return;
    }
  }
  
  private void sortEmoji()
  {
    this.recentEmoji.clear();
    Iterator localIterator = this.emojiUseHistory.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      this.recentEmoji.add(localEntry.getKey());
    }
    Collections.sort(this.recentEmoji, new Comparator()
    {
      public int compare(String paramAnonymousString1, String paramAnonymousString2)
      {
        int i = 0;
        Integer localInteger2 = (Integer)EmojiView.this.emojiUseHistory.get(paramAnonymousString1);
        Integer localInteger1 = (Integer)EmojiView.this.emojiUseHistory.get(paramAnonymousString2);
        paramAnonymousString1 = localInteger2;
        if (localInteger2 == null) {
          paramAnonymousString1 = Integer.valueOf(0);
        }
        paramAnonymousString2 = localInteger1;
        if (localInteger1 == null) {
          paramAnonymousString2 = Integer.valueOf(0);
        }
        if (paramAnonymousString1.intValue() > paramAnonymousString2.intValue()) {
          i = -1;
        }
        while (paramAnonymousString1.intValue() >= paramAnonymousString2.intValue()) {
          return i;
        }
        return 1;
      }
    });
    while (this.recentEmoji.size() > 50) {
      this.recentEmoji.remove(this.recentEmoji.size() - 1);
    }
  }
  
  private void sortStickers()
  {
    if (StickersQuery.getStickerSets().isEmpty()) {
      this.recentStickers.clear();
    }
    do
    {
      return;
      this.recentStickers.clear();
      i = 0;
      while (i < this.newRecentStickers.size())
      {
        TLRPC.Document localDocument = StickersQuery.getStickerById(((Long)this.newRecentStickers.get(i)).longValue());
        if (localDocument != null) {
          this.recentStickers.add(localDocument);
        }
        i += 1;
      }
      while (this.recentStickers.size() > 20) {
        this.recentStickers.remove(this.recentStickers.size() - 1);
      }
    } while (this.newRecentStickers.size() == this.recentStickers.size());
    this.newRecentStickers.clear();
    int i = 0;
    while (i < this.recentStickers.size())
    {
      this.newRecentStickers.add(Long.valueOf(((TLRPC.Document)this.recentStickers.get(i)).id));
      i += 1;
    }
    saveRecentStickers();
  }
  
  private void updateStickerTabs()
  {
    if (this.scrollSlidingTabStrip == null) {}
    do
    {
      return;
      this.recentTabBum = -2;
      this.gifTabBum = -2;
      this.stickersTabOffset = 0;
      j = this.scrollSlidingTabStrip.getCurrentPosition();
      this.scrollSlidingTabStrip.removeTabs();
      this.scrollSlidingTabStrip.addIconTab(2130837887);
      if ((this.showGifs) && (this.recentImages != null) && (!this.recentImages.isEmpty()))
      {
        this.scrollSlidingTabStrip.addIconTab(2130837882);
        this.gifTabBum = this.stickersTabOffset;
        this.stickersTabOffset += 1;
      }
      if (!this.recentStickers.isEmpty())
      {
        this.recentTabBum = this.stickersTabOffset;
        this.stickersTabOffset += 1;
        this.scrollSlidingTabStrip.addIconTab(2130837885);
      }
      this.stickerSets.clear();
      localObject = StickersQuery.getStickerSets();
      i = 0;
      if (i < ((ArrayList)localObject).size())
      {
        TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)((ArrayList)localObject).get(i);
        if ((localTL_messages_stickerSet.set.disabled) || (localTL_messages_stickerSet.documents == null) || (localTL_messages_stickerSet.documents.isEmpty())) {}
        for (;;)
        {
          i += 1;
          break;
          this.stickerSets.add(localTL_messages_stickerSet);
        }
      }
      i = 0;
      while (i < this.stickerSets.size())
      {
        this.scrollSlidingTabStrip.addStickerTab((TLRPC.Document)((TLRPC.TL_messages_stickerSet)this.stickerSets.get(i)).documents.get(0));
        i += 1;
      }
      this.scrollSlidingTabStrip.addIconTab(2130837872);
      this.scrollSlidingTabStrip.updateTabStyles();
      if (j != 0) {
        this.scrollSlidingTabStrip.onPageScrolled(j, j);
      }
      if ((this.switchToGifTab) && (this.gifTabBum >= 0) && (this.gifsGridView.getVisibility() != 0))
      {
        showGifTab();
        this.switchToGifTab = false;
      }
      if ((this.gifTabBum == -2) && (this.gifsGridView != null) && (this.gifsGridView.getVisibility() == 0))
      {
        this.listener.onGifTab(false);
        this.gifsGridView.setVisibility(8);
        this.stickersGridView.setVisibility(0);
        localObject = this.stickersEmptyView;
        if (this.stickersGridAdapter.getCount() != 0) {}
        for (i = 8;; i = 0)
        {
          ((TextView)localObject).setVisibility(i);
          return;
        }
      }
    } while (this.gifTabBum == -2);
    if ((this.gifsGridView != null) && (this.gifsGridView.getVisibility() == 0))
    {
      localObject = this.scrollSlidingTabStrip;
      j = this.gifTabBum;
      if (this.recentTabBum > 0) {}
      for (i = this.recentTabBum;; i = this.stickersTabOffset)
      {
        ((ScrollSlidingTabStrip)localObject).onPageScrolled(j + 1, i + 1);
        return;
      }
    }
    Object localObject = this.scrollSlidingTabStrip;
    int j = this.stickersGridAdapter.getTabForPosition(this.stickersGridView.getFirstVisiblePosition());
    if (this.recentTabBum > 0) {}
    for (int i = this.recentTabBum;; i = this.stickersTabOffset)
    {
      ((ScrollSlidingTabStrip)localObject).onPageScrolled(j + 1, i + 1);
      return;
    }
  }
  
  public void addRecentGif(MediaController.SearchImage paramSearchImage)
  {
    if ((paramSearchImage == null) || (paramSearchImage.document == null) || (this.recentImages == null)) {}
    boolean bool;
    label109:
    do
    {
      return;
      bool = this.recentImages.isEmpty();
      int i = 0;
      for (;;)
      {
        if (i >= this.recentImages.size()) {
          break label109;
        }
        MediaController.SearchImage localSearchImage = (MediaController.SearchImage)this.recentImages.get(i);
        if (localSearchImage.id.equals(paramSearchImage.id))
        {
          this.recentImages.remove(i);
          this.recentImages.add(0, localSearchImage);
          if (this.gifsAdapter == null) {
            break;
          }
          this.gifsAdapter.notifyDataSetChanged();
          return;
        }
        i += 1;
      }
      this.recentImages.add(0, paramSearchImage);
      if (this.gifsAdapter != null) {
        this.gifsAdapter.notifyDataSetChanged();
      }
    } while (!bool);
    updateStickerTabs();
  }
  
  public void addRecentSticker(TLRPC.Document paramDocument)
  {
    if (paramDocument == null) {
      return;
    }
    int i = this.newRecentStickers.indexOf(Long.valueOf(paramDocument.id));
    if (i == -1)
    {
      this.newRecentStickers.add(0, Long.valueOf(paramDocument.id));
      if (this.newRecentStickers.size() > 20) {
        this.newRecentStickers.remove(this.newRecentStickers.size() - 1);
      }
    }
    for (;;)
    {
      saveRecentStickers();
      return;
      if (i != 0)
      {
        this.newRecentStickers.remove(i);
        this.newRecentStickers.add(0, Long.valueOf(paramDocument.id));
      }
    }
  }
  
  public void clearRecentEmoji()
  {
    getContext().getSharedPreferences("emoji", 0).edit().putBoolean("filled_default", true).commit();
    this.emojiUseHistory.clear();
    this.recentEmoji.clear();
    saveRecentEmoji();
    ((EmojiGridAdapter)this.adapters.get(0)).notifyDataSetChanged();
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.stickersDidLoaded)
    {
      updateStickerTabs();
      reloadStickersAdapter();
    }
    while ((paramInt != NotificationCenter.recentImagesDidLoaded) || (((Integer)paramVarArgs[0]).intValue() != 2)) {
      return;
    }
    if (this.recentImages != null) {}
    for (paramInt = this.recentImages.size();; paramInt = 0)
    {
      this.recentImages = ((ArrayList)paramVarArgs[1]);
      this.loadingRecent = false;
      if (this.gifsAdapter != null) {
        this.gifsAdapter.notifyDataSetChanged();
      }
      if (paramInt != this.recentImages.size()) {
        updateStickerTabs();
      }
      loadRecentGif();
      return;
    }
  }
  
  public void invalidateViews()
  {
    Iterator localIterator = this.views.iterator();
    while (localIterator.hasNext())
    {
      GridView localGridView = (GridView)localIterator.next();
      if (localGridView != null) {
        localGridView.invalidateViews();
      }
    }
  }
  
  public void loadGifRecent()
  {
    if ((this.showGifs) && (this.gifsAdapter != null) && (!this.loadingRecent))
    {
      MessagesStorage.getInstance().loadWebRecent(2);
      this.loadingRecent = true;
    }
  }
  
  public void loadRecentGif()
  {
    if ((this.loadingRecentGifs) || (Math.abs(System.currentTimeMillis() - this.lastGifLoadTime) < 3600000L)) {
      return;
    }
    this.loadingRecentGifs = true;
    TLRPC.TL_messages_getSavedGifs localTL_messages_getSavedGifs = new TLRPC.TL_messages_getSavedGifs();
    localTL_messages_getSavedGifs.hash = calcGifsHash(this.recentImages);
    ConnectionsManager.getInstance().sendRequest(localTL_messages_getSavedGifs, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        paramAnonymousTL_error = null;
        if ((paramAnonymousTLObject instanceof TLRPC.TL_messages_savedGifs))
        {
          ArrayList localArrayList = new ArrayList();
          paramAnonymousTLObject = (TLRPC.TL_messages_savedGifs)paramAnonymousTLObject;
          int j = paramAnonymousTLObject.gifs.size();
          int i = 0;
          for (;;)
          {
            paramAnonymousTL_error = localArrayList;
            if (i >= j) {
              break;
            }
            paramAnonymousTL_error = new MediaController.SearchImage();
            paramAnonymousTL_error.type = 2;
            paramAnonymousTL_error.document = ((TLRPC.Document)paramAnonymousTLObject.gifs.get(i));
            paramAnonymousTL_error.date = (j - i);
            paramAnonymousTL_error.id = ("" + paramAnonymousTL_error.document.id);
            localArrayList.add(paramAnonymousTL_error);
            MessagesStorage.getInstance().putWebRecent(localArrayList);
            i += 1;
          }
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (paramAnonymousTL_error != null)
            {
              boolean bool = EmojiView.this.recentImages.isEmpty();
              EmojiView.access$2602(EmojiView.this, paramAnonymousTL_error);
              if (EmojiView.this.gifsAdapter != null) {
                EmojiView.this.gifsAdapter.notifyDataSetChanged();
              }
              EmojiView.access$4702(EmojiView.this, System.currentTimeMillis());
              EmojiView.this.getContext().getSharedPreferences("emoji", 0).edit().putLong("lastGifLoadTime", EmojiView.this.lastGifLoadTime).commit();
              if ((bool) && (!EmojiView.this.recentImages.isEmpty())) {
                EmojiView.this.updateStickerTabs();
              }
            }
            EmojiView.access$4802(EmojiView.this, false);
          }
        });
      }
    });
  }
  
  public void loadRecents()
  {
    SharedPreferences localSharedPreferences = getContext().getSharedPreferences("emoji", 0);
    this.lastGifLoadTime = localSharedPreferences.getLong("lastGifLoadTime", 0L);
    int i;
    int j;
    try
    {
      this.emojiUseHistory.clear();
      Object localObject1;
      String[] arrayOfString;
      Object localObject4;
      long l;
      label130:
      final Object localObject3;
      if (localSharedPreferences.contains("emojis"))
      {
        localObject1 = localSharedPreferences.getString("emojis", "");
        if ((localObject1 != null) && (((String)localObject1).length() > 0))
        {
          arrayOfString = ((String)localObject1).split(",");
          int k = arrayOfString.length;
          i = 0;
          if (i < k)
          {
            localObject4 = arrayOfString[i].split("=");
            l = Utilities.parseLong(localObject4[0]).longValue();
            localObject1 = "";
            j = 0;
            localObject3 = localObject1;
            if (j < 4)
            {
              char c = (char)(int)l;
              localObject1 = String.valueOf(c) + (String)localObject1;
              l >>= 16;
              if (l != 0L) {
                break label1077;
              }
              localObject3 = localObject1;
            }
            if (((String)localObject3).length() <= 0) {
              break label1070;
            }
            this.emojiUseHistory.put(localObject3, Utilities.parseInt(localObject4[1]));
            break label1070;
          }
        }
        localSharedPreferences.edit().remove("emojis").commit();
        saveRecentEmoji();
      }
      else
      {
        while ((this.emojiUseHistory.isEmpty()) && (!localSharedPreferences.getBoolean("filled_default", false)))
        {
          localObject1 = new String[34];
          localObject1[0] = "😂";
          localObject1[1] = "😘";
          localObject1[2] = "❤";
          localObject1[3] = "😍";
          localObject1[4] = "😊";
          localObject1[5] = "😁";
          localObject1[6] = "👍";
          localObject1[7] = "☺";
          localObject1[8] = "😔";
          localObject1[9] = "😄";
          localObject1[10] = "😭";
          localObject1[11] = "💋";
          localObject1[12] = "😒";
          localObject1[13] = "😳";
          localObject1[14] = "😜";
          localObject1[15] = "🙈";
          localObject1[16] = "😉";
          localObject1[17] = "😃";
          localObject1[18] = "😢";
          localObject1[19] = "😝";
          localObject1[20] = "😱";
          localObject1[21] = "😡";
          localObject1[22] = "😏";
          localObject1[23] = "😞";
          localObject1[24] = "😅";
          localObject1[25] = "😚";
          localObject1[26] = "🙊";
          localObject1[27] = "😌";
          localObject1[28] = "😀";
          localObject1[29] = "😋";
          localObject1[30] = "😆";
          localObject1[31] = "👌";
          localObject1[32] = "😐";
          localObject1[33] = "😕";
          i = 0;
          for (;;)
          {
            if (i < localObject1.length)
            {
              this.emojiUseHistory.put(localObject1[i], Integer.valueOf(localObject1.length - i));
              i += 1;
              continue;
              localObject1 = localSharedPreferences.getString("emojis2", "");
              if ((localObject1 == null) || (((String)localObject1).length() <= 0)) {
                break;
              }
              localObject1 = ((String)localObject1).split(",");
              j = localObject1.length;
              i = 0;
              while (i < j)
              {
                localObject3 = localObject1[i].split("=");
                this.emojiUseHistory.put(localObject3[0], Utilities.parseInt(localObject3[1]));
                i += 1;
              }
              break;
            }
          }
          localSharedPreferences.edit().putBoolean("filled_default", true).commit();
          saveRecentEmoji();
        }
        sortEmoji();
        ((EmojiGridAdapter)this.adapters.get(0)).notifyDataSetChanged();
        try
        {
          this.newRecentStickers.clear();
          localObject2 = localSharedPreferences.getString("stickers", "");
          if ((localObject2 != null) && (((String)localObject2).length() > 0))
          {
            localObject2 = ((String)localObject2).split(",");
            localObject3 = new HashMap();
            i = 0;
            while (i < localObject2.length)
            {
              arrayOfString = localObject2[i].split("=");
              localObject4 = Utilities.parseLong(arrayOfString[0]);
              ((HashMap)localObject3).put(localObject4, Utilities.parseInt(arrayOfString[1]));
              this.newRecentStickers.add(localObject4);
              i += 1;
            }
            Collections.sort(this.newRecentStickers, new Comparator()
            {
              public int compare(Long paramAnonymousLong1, Long paramAnonymousLong2)
              {
                int i = 0;
                Integer localInteger2 = (Integer)localObject3.get(paramAnonymousLong1);
                Integer localInteger1 = (Integer)localObject3.get(paramAnonymousLong2);
                paramAnonymousLong1 = localInteger2;
                if (localInteger2 == null) {
                  paramAnonymousLong1 = Integer.valueOf(0);
                }
                paramAnonymousLong2 = localInteger1;
                if (localInteger1 == null) {
                  paramAnonymousLong2 = Integer.valueOf(0);
                }
                if (paramAnonymousLong1.intValue() > paramAnonymousLong2.intValue()) {
                  i = -1;
                }
                while (paramAnonymousLong1.intValue() >= paramAnonymousLong2.intValue()) {
                  return i;
                }
                return 1;
              }
            });
            localSharedPreferences.edit().remove("stickers").commit();
            saveRecentStickers();
          }
          do
          {
            sortStickers();
            updateStickerTabs();
            return;
            localObject2 = localSharedPreferences.getString("stickers2", "").split(",");
            i = 0;
          } while (i >= localObject2.length);
          if (localObject2[i].length() != 0)
          {
            l = Utilities.parseLong(localObject2[i]).longValue();
            if (l != 0L) {
              this.newRecentStickers.add(Long.valueOf(l));
            }
          }
        }
        catch (Exception localException3)
        {
          FileLog.e("tmessages", localException3);
          return;
        }
      }
    }
    catch (Exception localException1)
    {
      try
      {
        for (;;)
        {
          localObject1 = localSharedPreferences.getString("color", "");
          if ((localObject1 == null) || (((String)localObject1).length() <= 0)) {
            break;
          }
          localObject1 = ((String)localObject1).split(",");
          i = 0;
          while (i < localObject1.length)
          {
            localObject3 = localObject1[i].split("=");
            emojiColor.put(localObject3[0], localObject3[1]);
            i += 1;
          }
          localException1 = localException1;
          FileLog.e("tmessages", localException1);
        }
        if (!this.showStickers) {}
      }
      catch (Exception localException2)
      {
        FileLog.e("tmessages", localException2);
      }
    }
    for (;;)
    {
      Object localObject2;
      label1070:
      i += 1;
      break;
      label1077:
      j += 1;
      break label130;
      return;
      i += 1;
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.stickersGridAdapter != null)
    {
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          EmojiView.this.updateStickerTabs();
          EmojiView.this.reloadStickersAdapter();
        }
      });
    }
  }
  
  public void onDestroy()
  {
    if (this.stickersGridAdapter != null)
    {
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stickersDidLoaded);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recentImagesDidLoaded);
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if ((this.pickerViewPopup != null) && (this.pickerViewPopup.isShowing())) {
      this.pickerViewPopup.dismiss();
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.lastNotifyWidth != paramInt3 - paramInt1)
    {
      this.lastNotifyWidth = (paramInt3 - paramInt1);
      reloadStickersAdapter();
    }
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void onMeasure(int paramInt1, int paramInt2)
  {
    FrameLayout.LayoutParams localLayoutParams2 = (FrameLayout.LayoutParams)this.pagerSlidingTabStripContainer.getLayoutParams();
    Object localObject = null;
    localLayoutParams2.width = View.MeasureSpec.getSize(paramInt1);
    if (this.scrollSlidingTabStrip != null)
    {
      FrameLayout.LayoutParams localLayoutParams1 = (FrameLayout.LayoutParams)this.scrollSlidingTabStrip.getLayoutParams();
      localObject = localLayoutParams1;
      if (localLayoutParams1 != null)
      {
        localLayoutParams1.width = localLayoutParams2.width;
        localObject = localLayoutParams1;
      }
    }
    if (localLayoutParams2.width != this.oldWidth)
    {
      if ((this.scrollSlidingTabStrip != null) && (localObject != null))
      {
        onPageScrolled(this.pager.getCurrentItem(), localLayoutParams2.width, 0);
        this.scrollSlidingTabStrip.setLayoutParams((ViewGroup.LayoutParams)localObject);
      }
      this.pagerSlidingTabStripContainer.setLayoutParams(localLayoutParams2);
      this.oldWidth = localLayoutParams2.width;
    }
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(localLayoutParams2.width, 1073741824), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt2), 1073741824));
  }
  
  public void setListener(Listener paramListener)
  {
    this.listener = paramListener;
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    if (paramInt != 8)
    {
      sortEmoji();
      ((EmojiGridAdapter)this.adapters.get(0)).notifyDataSetChanged();
      if (this.stickersGridAdapter != null)
      {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
        sortStickers();
        updateStickerTabs();
        reloadStickersAdapter();
        if ((this.gifsGridView != null) && (this.gifsGridView.getVisibility() == 0) && (this.listener != null)) {
          this.listener.onGifTab(true);
        }
      }
      loadGifRecent();
    }
  }
  
  public void switchToGifRecent()
  {
    this.pager.setCurrentItem(6);
    if ((this.gifTabBum >= 0) && (!this.recentImages.isEmpty()))
    {
      this.scrollSlidingTabStrip.selectTab(this.gifTabBum + 1);
      return;
    }
    this.switchToGifTab = true;
  }
  
  public void updateColors(SharedPreferences paramSharedPreferences, LinearLayout paramLinearLayout)
  {
    int i = paramSharedPreferences.getInt("theme_emoji_color", -657673);
    int j = paramSharedPreferences.getInt("theme_emoji_gradient", 0);
    int k = paramSharedPreferences.getInt("theme_emoji_gcolor", i);
    if (j != 0)
    {
      setBackgroundDrawable(MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j)));
      i = paramSharedPreferences.getInt("theme_emoji_tab_color", -657673);
      j = paramSharedPreferences.getInt("theme_emoji_tab_gradient", 0);
      k = paramSharedPreferences.getInt("theme_emoji_tab_gcolor", i);
      if (j == 0) {
        break label140;
      }
      paramSharedPreferences = MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j));
      paramLinearLayout.setBackgroundDrawable(paramSharedPreferences);
      if (this.scrollSlidingTabStrip != null) {
        this.scrollSlidingTabStrip.setBackgroundDrawable(paramSharedPreferences);
      }
    }
    label140:
    do
    {
      return;
      setBackgroundColor(i);
      break;
      paramLinearLayout.setBackgroundColor(i);
    } while (this.scrollSlidingTabStrip == null);
    this.scrollSlidingTabStrip.setBackgroundColor(i);
  }
  
  private class EmojiColorPickerView
    extends View
  {
    private Drawable arrowDrawable = getResources().getDrawable(2130838136);
    private int arrowX;
    private Drawable backgroundDrawable = getResources().getDrawable(2130838135);
    private String currentEmoji;
    private RectF rect = new RectF();
    private Paint rectPaint = new Paint(1);
    private int selection;
    
    public EmojiColorPickerView(Context paramContext)
    {
      super();
    }
    
    public String getEmoji()
    {
      return this.currentEmoji;
    }
    
    public int getSelection()
    {
      return this.selection;
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      float f2 = 55.5F;
      Object localObject = this.backgroundDrawable;
      int i = getMeasuredWidth();
      float f1;
      int j;
      label73:
      int k;
      if (AndroidUtilities.isTablet())
      {
        f1 = 60.0F;
        ((Drawable)localObject).setBounds(0, 0, i, AndroidUtilities.dp(f1));
        this.backgroundDrawable.draw(paramCanvas);
        localObject = this.arrowDrawable;
        i = this.arrowX;
        j = AndroidUtilities.dp(9.0F);
        if (!AndroidUtilities.isTablet()) {
          break label394;
        }
        f1 = 55.5F;
        k = AndroidUtilities.dp(f1);
        int m = this.arrowX;
        int n = AndroidUtilities.dp(9.0F);
        if (!AndroidUtilities.isTablet()) {
          break label400;
        }
        f1 = f2;
        label100:
        ((Drawable)localObject).setBounds(i - j, k, m + n, AndroidUtilities.dp(f1 + 8.0F));
        this.arrowDrawable.draw(paramCanvas);
        if (this.currentEmoji == null) {
          return;
        }
        i = 0;
        label142:
        if (i >= 6) {
          return;
        }
        j = EmojiView.this.emojiSize * i + AndroidUtilities.dp(i * 4 + 5);
        k = AndroidUtilities.dp(9.0F);
        if (this.selection == i)
        {
          this.rect.set(j, k - (int)AndroidUtilities.dpf2(3.5F), EmojiView.this.emojiSize + j, EmojiView.this.emojiSize + k + AndroidUtilities.dp(3.0F));
          paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F), this.rectPaint);
        }
        String str = this.currentEmoji;
        localObject = str;
        if (i != 0)
        {
          localObject = str + "?";
          switch (i)
          {
          }
        }
      }
      for (;;)
      {
        localObject = Emoji.getEmojiBigDrawable((String)localObject);
        if (localObject != null)
        {
          ((Drawable)localObject).setBounds(j, k, EmojiView.this.emojiSize + j, EmojiView.this.emojiSize + k);
          ((Drawable)localObject).draw(paramCanvas);
        }
        i += 1;
        break label142;
        f1 = 52.0F;
        break;
        label394:
        f1 = 47.5F;
        break label73;
        label400:
        f1 = 47.5F;
        break label100;
        localObject = (String)localObject + "?";
        continue;
        localObject = (String)localObject + "?";
        continue;
        localObject = (String)localObject + "?";
        continue;
        localObject = (String)localObject + "?";
        continue;
        localObject = (String)localObject + "?";
      }
    }
    
    public void setEmoji(String paramString, int paramInt)
    {
      this.currentEmoji = paramString;
      this.arrowX = paramInt;
      this.rectPaint.setColor(788529152);
      invalidate();
    }
    
    public void setSelection(int paramInt)
    {
      if (this.selection == paramInt) {
        return;
      }
      this.selection = paramInt;
      invalidate();
    }
  }
  
  private class EmojiGridAdapter
    extends BaseAdapter
  {
    private int emojiPage;
    
    public EmojiGridAdapter(int paramInt)
    {
      this.emojiPage = paramInt;
    }
    
    public int getCount()
    {
      if (this.emojiPage == -1) {
        return EmojiView.this.recentEmoji.size();
      }
      return EmojiData.dataColored[this.emojiPage].length;
    }
    
    public Object getItem(int paramInt)
    {
      return null;
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      paramView = (EmojiView.ImageViewEmoji)paramView;
      paramViewGroup = paramView;
      if (paramView == null) {
        paramViewGroup = new EmojiView.ImageViewEmoji(EmojiView.this, EmojiView.this.getContext());
      }
      Object localObject;
      if (this.emojiPage == -1)
      {
        localObject = (String)EmojiView.this.recentEmoji.get(paramInt);
        paramView = (View)localObject;
      }
      for (;;)
      {
        paramViewGroup.setImageDrawable(Emoji.getEmojiBigDrawable(paramView));
        paramViewGroup.setTag(localObject);
        return paramViewGroup;
        String str1 = EmojiData.dataColored[this.emojiPage][paramInt];
        String str2 = str1;
        String str3 = (String)EmojiView.emojiColor.get(str1);
        localObject = str1;
        paramView = str2;
        if (str3 != null)
        {
          paramView = str2 + str3;
          localObject = str1;
        }
      }
    }
    
    public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver)
    {
      if (paramDataSetObserver != null) {
        super.unregisterDataSetObserver(paramDataSetObserver);
      }
    }
  }
  
  private class EmojiPagesAdapter
    extends PagerAdapter
    implements PagerSlidingTabStrip.IconTabProvider
  {
    private EmojiPagesAdapter() {}
    
    public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
    {
      if (paramInt == 0) {
        paramObject = EmojiView.this.recentsWrap;
      }
      for (;;)
      {
        paramViewGroup.removeView((View)paramObject);
        return;
        if (paramInt == 6) {
          paramObject = EmojiView.this.stickersWrap;
        } else {
          paramObject = (View)EmojiView.this.views.get(paramInt);
        }
      }
    }
    
    public int getCount()
    {
      return EmojiView.this.views.size();
    }
    
    public int getPageIconResId(int paramInt)
    {
      return EmojiView.this.icons[paramInt];
    }
    
    public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
    {
      Object localObject;
      if (paramInt == 0) {
        localObject = EmojiView.this.recentsWrap;
      }
      for (;;)
      {
        paramViewGroup.addView((View)localObject);
        return localObject;
        if (paramInt == 6) {
          localObject = EmojiView.this.stickersWrap;
        } else {
          localObject = (View)EmojiView.this.views.get(paramInt);
        }
      }
    }
    
    public boolean isViewFromObject(View paramView, Object paramObject)
    {
      return paramView == paramObject;
    }
    
    public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver)
    {
      if (paramDataSetObserver != null) {
        super.unregisterDataSetObserver(paramDataSetObserver);
      }
    }
  }
  
  private class EmojiPopupWindow
    extends PopupWindow
  {
    private ViewTreeObserver.OnScrollChangedListener mSuperScrollListener;
    private ViewTreeObserver mViewTreeObserver;
    
    public EmojiPopupWindow()
    {
      init();
    }
    
    public EmojiPopupWindow(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      init();
    }
    
    public EmojiPopupWindow(Context paramContext)
    {
      super();
      init();
    }
    
    public EmojiPopupWindow(View paramView)
    {
      super();
      init();
    }
    
    public EmojiPopupWindow(View paramView, int paramInt1, int paramInt2)
    {
      super(paramInt1, paramInt2);
      init();
    }
    
    public EmojiPopupWindow(View paramView, int paramInt1, int paramInt2, boolean paramBoolean)
    {
      super(paramInt1, paramInt2, paramBoolean);
      init();
    }
    
    private void init()
    {
      if (EmojiView.superListenerField != null) {}
      try
      {
        this.mSuperScrollListener = ((ViewTreeObserver.OnScrollChangedListener)EmojiView.superListenerField.get(this));
        EmojiView.superListenerField.set(this, EmojiView.NOP);
        return;
      }
      catch (Exception localException)
      {
        this.mSuperScrollListener = null;
      }
    }
    
    private void registerListener(View paramView)
    {
      if (this.mSuperScrollListener != null) {
        if (paramView.getWindowToken() == null) {
          break label73;
        }
      }
      label73:
      for (paramView = paramView.getViewTreeObserver();; paramView = null)
      {
        if (paramView != this.mViewTreeObserver)
        {
          if ((this.mViewTreeObserver != null) && (this.mViewTreeObserver.isAlive())) {
            this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
          }
          this.mViewTreeObserver = paramView;
          if (paramView != null) {
            paramView.addOnScrollChangedListener(this.mSuperScrollListener);
          }
        }
        return;
      }
    }
    
    private void unregisterListener()
    {
      if ((this.mSuperScrollListener != null) && (this.mViewTreeObserver != null))
      {
        if (this.mViewTreeObserver.isAlive()) {
          this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
        }
        this.mViewTreeObserver = null;
      }
    }
    
    public void dismiss()
    {
      setFocusable(false);
      try
      {
        super.dismiss();
        unregisterListener();
        return;
      }
      catch (Exception localException)
      {
        for (;;) {}
      }
    }
    
    public void showAsDropDown(View paramView, int paramInt1, int paramInt2)
    {
      try
      {
        super.showAsDropDown(paramView, paramInt1, paramInt2);
        registerListener(paramView);
        return;
      }
      catch (Exception paramView)
      {
        FileLog.e("tmessages", paramView);
      }
    }
    
    public void showAtLocation(View paramView, int paramInt1, int paramInt2, int paramInt3)
    {
      super.showAtLocation(paramView, paramInt1, paramInt2, paramInt3);
      unregisterListener();
    }
    
    public void update(View paramView, int paramInt1, int paramInt2)
    {
      super.update(paramView, paramInt1, paramInt2);
      registerListener(paramView);
    }
    
    public void update(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.update(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
      registerListener(paramView);
    }
  }
  
  private class GifsAdapter
    extends RecyclerView.Adapter
  {
    private Context mContext;
    
    public GifsAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getItemCount()
    {
      return EmojiView.this.recentImages.size();
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      MediaController.SearchImage localSearchImage = (MediaController.SearchImage)EmojiView.this.recentImages.get(paramInt);
      if (localSearchImage.document != null) {
        ((ContextLinkCell)paramViewHolder.itemView).setGif(localSearchImage.document, false);
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      return new Holder(new ContextLinkCell(this.mContext));
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
  
  private class ImageViewEmoji
    extends ImageView
  {
    private float lastX;
    private float lastY;
    private boolean touched;
    private float touchedX;
    private float touchedY;
    
    public ImageViewEmoji(Context paramContext)
    {
      super();
      setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          EmojiView.ImageViewEmoji.this.sendEmoji(null);
        }
      });
      setOnLongClickListener(new View.OnLongClickListener()
      {
        public boolean onLongClick(View paramAnonymousView)
        {
          int j = 0;
          String str = (String)paramAnonymousView.getTag();
          if (EmojiData.emojiColoredMap.containsKey(str))
          {
            EmojiView.ImageViewEmoji.access$102(EmojiView.ImageViewEmoji.this, true);
            EmojiView.ImageViewEmoji.access$202(EmojiView.ImageViewEmoji.this, EmojiView.ImageViewEmoji.this.lastX);
            EmojiView.ImageViewEmoji.access$402(EmojiView.ImageViewEmoji.this, EmojiView.ImageViewEmoji.this.lastY);
            Object localObject = (String)EmojiView.emojiColor.get(str);
            int i;
            label156:
            int k;
            if (localObject != null)
            {
              i = -1;
              switch (((String)localObject).hashCode())
              {
              default: 
                switch (i)
                {
                default: 
                  paramAnonymousView.getLocationOnScreen(EmojiView.this.location);
                  k = EmojiView.this.emojiSize;
                  int m = EmojiView.this.pickerView.getSelection();
                  int n = EmojiView.this.pickerView.getSelection();
                  if (AndroidUtilities.isTablet())
                  {
                    i = 5;
                    label220:
                    k = m * k + AndroidUtilities.dp(n * 4 - i);
                    if (EmojiView.this.location[0] - k >= AndroidUtilities.dp(5.0F)) {
                      break label607;
                    }
                    i = k + (EmojiView.this.location[0] - k - AndroidUtilities.dp(5.0F));
                    label286:
                    k = -i;
                    i = j;
                    if (paramAnonymousView.getTop() < 0) {
                      i = paramAnonymousView.getTop();
                    }
                    localObject = EmojiView.this.pickerView;
                    if (!AndroidUtilities.isTablet()) {
                      break label697;
                    }
                  }
                  break;
                }
                break;
              }
            }
            label607:
            label697:
            for (float f = 30.0F;; f = 22.0F)
            {
              ((EmojiView.EmojiColorPickerView)localObject).setEmoji(str, AndroidUtilities.dp(f) - k + (int)AndroidUtilities.dpf2(0.5F));
              EmojiView.this.pickerViewPopup.setFocusable(true);
              EmojiView.this.pickerViewPopup.showAsDropDown(paramAnonymousView, k, -paramAnonymousView.getMeasuredHeight() - EmojiView.this.popupHeight + (paramAnonymousView.getMeasuredHeight() - EmojiView.this.emojiSize) / 2 - i);
              paramAnonymousView.getParent().requestDisallowInterceptTouchEvent(true);
              return true;
              if (!((String)localObject).equals("🏻")) {
                break;
              }
              i = 0;
              break;
              if (!((String)localObject).equals("🏼")) {
                break;
              }
              i = 1;
              break;
              if (!((String)localObject).equals("🏽")) {
                break;
              }
              i = 2;
              break;
              if (!((String)localObject).equals("🏾")) {
                break;
              }
              i = 3;
              break;
              if (!((String)localObject).equals("🏿")) {
                break;
              }
              i = 4;
              break;
              EmojiView.this.pickerView.setSelection(1);
              break label156;
              EmojiView.this.pickerView.setSelection(2);
              break label156;
              EmojiView.this.pickerView.setSelection(3);
              break label156;
              EmojiView.this.pickerView.setSelection(4);
              break label156;
              EmojiView.this.pickerView.setSelection(5);
              break label156;
              EmojiView.this.pickerView.setSelection(0);
              break label156;
              i = 1;
              break label220;
              i = k;
              if (EmojiView.this.location[0] - k + EmojiView.this.popupWidth <= AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0F)) {
                break label286;
              }
              i = k + (EmojiView.this.location[0] - k + EmojiView.this.popupWidth - (AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0F)));
              break label286;
            }
          }
          if (EmojiView.this.pager.getCurrentItem() == 0) {
            EmojiView.this.listener.onClearEmojiRecent();
          }
          return false;
        }
      });
      setBackgroundResource(2130837932);
      setScaleType(ImageView.ScaleType.CENTER);
    }
    
    private void sendEmoji(String paramString)
    {
      Object localObject1;
      if (paramString != null)
      {
        localObject1 = paramString;
        if (paramString != null) {
          break label298;
        }
        paramString = (String)localObject1;
        if (EmojiView.this.pager.getCurrentItem() != 0)
        {
          localObject2 = (String)EmojiView.emojiColor.get(localObject1);
          paramString = (String)localObject1;
          if (localObject2 != null) {
            paramString = (String)localObject1 + (String)localObject2;
          }
        }
        localObject2 = (Integer)EmojiView.this.emojiUseHistory.get(paramString);
        localObject1 = localObject2;
        if (localObject2 == null) {
          localObject1 = Integer.valueOf(0);
        }
        if ((((Integer)localObject1).intValue() == 0) && (EmojiView.this.emojiUseHistory.size() > 50))
        {
          i = EmojiView.this.recentEmoji.size() - 1;
          if (i >= 0)
          {
            localObject2 = (String)EmojiView.this.recentEmoji.get(i);
            EmojiView.this.emojiUseHistory.remove(localObject2);
            EmojiView.this.recentEmoji.remove(i);
            if (EmojiView.this.emojiUseHistory.size() > 50) {
              break label291;
            }
          }
        }
        EmojiView.this.emojiUseHistory.put(paramString, Integer.valueOf(((Integer)localObject1).intValue() + 1));
        if (EmojiView.this.pager.getCurrentItem() != 0) {
          EmojiView.this.sortEmoji();
        }
        EmojiView.this.saveRecentEmoji();
        ((EmojiView.EmojiGridAdapter)EmojiView.this.adapters.get(0)).notifyDataSetChanged();
        if (EmojiView.this.listener != null) {
          EmojiView.this.listener.onEmojiSelected(Emoji.fixEmoji(paramString));
        }
      }
      label291:
      label298:
      while (EmojiView.this.listener == null) {
        for (;;)
        {
          Object localObject2;
          int i;
          return;
          localObject1 = (String)getTag();
          break;
          i -= 1;
        }
      }
      EmojiView.this.listener.onEmojiSelected(Emoji.fixEmoji(paramString));
    }
    
    public void onMeasure(int paramInt1, int paramInt2)
    {
      setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), View.MeasureSpec.getSize(paramInt1));
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      if (this.touched)
      {
        if ((paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 3)) {
          break label319;
        }
        if ((EmojiView.this.pickerViewPopup != null) && (EmojiView.this.pickerViewPopup.isShowing()))
        {
          EmojiView.this.pickerViewPopup.dismiss();
          localObject1 = null;
        }
        switch (EmojiView.this.pickerView.getSelection())
        {
        default: 
          localObject2 = (String)getTag();
          if (EmojiView.this.pager.getCurrentItem() != 0) {
            if (localObject1 != null)
            {
              EmojiView.emojiColor.put(localObject2, localObject1);
              localObject1 = (String)localObject2 + (String)localObject1;
              setImageDrawable(Emoji.getEmojiBigDrawable((String)localObject1));
              sendEmoji(null);
              EmojiView.this.saveEmojiColors();
              this.touched = false;
              this.touchedX = -10000.0F;
              this.touchedY = -10000.0F;
            }
          }
          break;
        }
      }
      label319:
      while (paramMotionEvent.getAction() != 2)
      {
        Object localObject1;
        for (;;)
        {
          this.lastX = paramMotionEvent.getX();
          this.lastY = paramMotionEvent.getY();
          return super.onTouchEvent(paramMotionEvent);
          localObject1 = "🏻";
          continue;
          localObject1 = "🏼";
          continue;
          localObject1 = "🏽";
          continue;
          localObject1 = "🏾";
          continue;
          localObject1 = "🏿";
          continue;
          EmojiView.emojiColor.remove(localObject2);
          localObject1 = localObject2;
        }
        Object localObject2 = new StringBuilder().append((String)localObject2);
        if (localObject1 != null) {}
        for (;;)
        {
          sendEmoji((String)localObject1);
          break;
          localObject1 = "";
        }
      }
      int j = 0;
      int i = j;
      if (this.touchedX != -10000.0F)
      {
        if ((Math.abs(this.touchedX - paramMotionEvent.getX()) > AndroidUtilities.getPixelsInCM(0.2F, true)) || (Math.abs(this.touchedY - paramMotionEvent.getY()) > AndroidUtilities.getPixelsInCM(0.2F, false)))
        {
          this.touchedX = -10000.0F;
          this.touchedY = -10000.0F;
          i = j;
        }
      }
      else
      {
        label406:
        if (i != 0) {
          break label522;
        }
        getLocationOnScreen(EmojiView.this.location);
        float f1 = EmojiView.this.location[0];
        float f2 = paramMotionEvent.getX();
        EmojiView.this.pickerView.getLocationOnScreen(EmojiView.this.location);
        j = (int)((f1 + f2 - (EmojiView.this.location[0] + AndroidUtilities.dp(3.0F))) / (EmojiView.this.emojiSize + AndroidUtilities.dp(4.0F)));
        if (j >= 0) {
          break label524;
        }
        i = 0;
      }
      for (;;)
      {
        EmojiView.this.pickerView.setSelection(i);
        break;
        i = 1;
        break label406;
        label522:
        break;
        label524:
        i = j;
        if (j > 5) {
          i = 5;
        }
      }
    }
  }
  
  public static abstract interface Listener
  {
    public abstract boolean onBackspace();
    
    public abstract void onClearEmojiRecent();
    
    public abstract void onEmojiSelected(String paramString);
    
    public abstract void onGifSelected(TLRPC.Document paramDocument);
    
    public abstract void onGifTab(boolean paramBoolean);
    
    public abstract void onStickerSelected(TLRPC.Document paramDocument);
    
    public abstract void onStickersSettingsClick();
    
    public abstract void onStickersTab(boolean paramBoolean);
  }
  
  private class StickersGridAdapter
    extends BaseAdapter
  {
    private HashMap<Integer, TLRPC.Document> cache = new HashMap();
    private Context context;
    private HashMap<TLRPC.TL_messages_stickerSet, Integer> packStartRow = new HashMap();
    private HashMap<Integer, TLRPC.TL_messages_stickerSet> rowStartPack = new HashMap();
    private int stickersPerRow;
    private int totalItems;
    
    public StickersGridAdapter(Context paramContext)
    {
      this.context = paramContext;
    }
    
    public boolean areAllItemsEnabled()
    {
      return false;
    }
    
    public int getCount()
    {
      if (this.totalItems != 0) {
        return this.totalItems + 1;
      }
      return 0;
    }
    
    public Object getItem(int paramInt)
    {
      return this.cache.get(Integer.valueOf(paramInt));
    }
    
    public long getItemId(int paramInt)
    {
      return -1L;
    }
    
    public int getItemViewType(int paramInt)
    {
      if (this.cache.get(Integer.valueOf(paramInt)) != null) {
        return 0;
      }
      return 1;
    }
    
    public int getPositionForPack(TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet)
    {
      return ((Integer)this.packStartRow.get(paramTL_messages_stickerSet)).intValue() * this.stickersPerRow;
    }
    
    public int getTabForPosition(int paramInt)
    {
      if (this.stickersPerRow == 0)
      {
        int j = EmojiView.this.getMeasuredWidth();
        int i = j;
        if (j == 0) {
          i = AndroidUtilities.displaySize.x;
        }
        this.stickersPerRow = (i / AndroidUtilities.dp(72.0F));
      }
      paramInt /= this.stickersPerRow;
      TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)this.rowStartPack.get(Integer.valueOf(paramInt));
      if (localTL_messages_stickerSet == null) {
        return EmojiView.this.recentTabBum;
      }
      return EmojiView.this.stickerSets.indexOf(localTL_messages_stickerSet) + EmojiView.this.stickersTabOffset;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      TLRPC.Document localDocument = (TLRPC.Document)this.cache.get(Integer.valueOf(paramInt));
      if (localDocument != null)
      {
        paramViewGroup = paramView;
        if (paramView == null) {
          paramViewGroup = new StickerEmojiCell(this.context)
          {
            public void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
            {
              super.onMeasure(paramAnonymousInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0F), 1073741824));
            }
          };
        }
        ((StickerEmojiCell)paramViewGroup).setSticker(localDocument, false);
        return paramViewGroup;
      }
      paramViewGroup = paramView;
      if (paramView == null) {
        paramViewGroup = new EmptyCell(this.context);
      }
      if (paramInt == this.totalItems)
      {
        paramInt = (paramInt - 1) / this.stickersPerRow;
        paramView = (TLRPC.TL_messages_stickerSet)this.rowStartPack.get(Integer.valueOf(paramInt));
        if (paramView == null)
        {
          ((EmptyCell)paramViewGroup).setHeight(1);
          return paramViewGroup;
        }
        paramInt = EmojiView.this.pager.getHeight() - (int)Math.ceil(paramView.documents.size() / this.stickersPerRow) * AndroidUtilities.dp(82.0F);
        paramView = (EmptyCell)paramViewGroup;
        if (paramInt > 0) {}
        for (;;)
        {
          paramView.setHeight(paramInt);
          return paramViewGroup;
          paramInt = 1;
        }
      }
      ((EmptyCell)paramViewGroup).setHeight(AndroidUtilities.dp(82.0F));
      return paramViewGroup;
    }
    
    public int getViewTypeCount()
    {
      return 2;
    }
    
    public boolean isEnabled(int paramInt)
    {
      return this.cache.get(Integer.valueOf(paramInt)) != null;
    }
    
    public void notifyDataSetChanged()
    {
      int j = EmojiView.this.getMeasuredWidth();
      int i = j;
      if (j == 0) {
        i = AndroidUtilities.displaySize.x;
      }
      this.stickersPerRow = (i / AndroidUtilities.dp(72.0F));
      this.rowStartPack.clear();
      this.packStartRow.clear();
      this.cache.clear();
      this.totalItems = 0;
      ArrayList localArrayList2 = EmojiView.this.stickerSets;
      i = -1;
      if (i < localArrayList2.size())
      {
        TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = null;
        int k = this.totalItems / this.stickersPerRow;
        ArrayList localArrayList1;
        if (i == -1)
        {
          localArrayList1 = EmojiView.this.recentStickers;
          label105:
          if (!localArrayList1.isEmpty()) {
            break label155;
          }
        }
        for (;;)
        {
          i += 1;
          break;
          localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)localArrayList2.get(i);
          localArrayList1 = localTL_messages_stickerSet.documents;
          this.packStartRow.put(localTL_messages_stickerSet, Integer.valueOf(k));
          break label105;
          label155:
          int m = (int)Math.ceil(localArrayList1.size() / this.stickersPerRow);
          j = 0;
          while (j < localArrayList1.size())
          {
            this.cache.put(Integer.valueOf(this.totalItems + j), localArrayList1.get(j));
            j += 1;
          }
          this.totalItems += this.stickersPerRow * m;
          j = 0;
          while (j < m)
          {
            this.rowStartPack.put(Integer.valueOf(k + j), localTL_messages_stickerSet);
            j += 1;
          }
        }
      }
      super.notifyDataSetChanged();
    }
    
    public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver)
    {
      if (paramDataSetObserver != null) {
        super.unregisterDataSetObserver(paramDataSetObserver);
      }
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Components\EmojiView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */