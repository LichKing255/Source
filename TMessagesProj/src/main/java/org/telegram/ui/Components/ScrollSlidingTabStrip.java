package org.telegram.ui.Components;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Build.VERSION;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.Supergram.Theming.MihanTheme;

public class ScrollSlidingTabStrip
  extends HorizontalScrollView
{
  private int currentPosition = 0;
  private LinearLayout.LayoutParams defaultTabLayoutParams;
  private ScrollSlidingTabStripDelegate delegate;
  private int dividerPadding = AndroidUtilities.dp(12.0F);
  private int indicatorColor = -10066330;
  private int lastScrollX = 0;
  private Paint rectPaint;
  private int scrollOffset = AndroidUtilities.dp(52.0F);
  private int tabCount;
  private int tabPadding = AndroidUtilities.dp(24.0F);
  private LinearLayout tabsContainer;
  private int underlineColor = 436207616;
  private int underlineHeight = AndroidUtilities.dp(2.0F);
  
  public ScrollSlidingTabStrip(Context paramContext)
  {
    super(paramContext);
    setFillViewport(true);
    setWillNotDraw(false);
    setHorizontalScrollBarEnabled(false);
    this.tabsContainer = new LinearLayout(paramContext);
    this.tabsContainer.setOrientation(0);
    this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
    addView(this.tabsContainer);
    this.rectPaint = new Paint();
    this.rectPaint.setAntiAlias(true);
    this.rectPaint.setStyle(Paint.Style.FILL);
    this.defaultTabLayoutParams = new LinearLayout.LayoutParams(AndroidUtilities.dp(52.0F), -1);
  }
  
  private void scrollToChild(int paramInt)
  {
    if ((this.tabCount == 0) || (this.tabsContainer.getChildAt(paramInt) == null)) {}
    int i;
    do
    {
      do
      {
        return;
        int j = this.tabsContainer.getChildAt(paramInt).getLeft();
        i = j;
        if (paramInt > 0) {
          i = j - this.scrollOffset;
        }
        paramInt = getScrollX();
      } while (i == this.lastScrollX);
      if (i < paramInt)
      {
        this.lastScrollX = i;
        smoothScrollTo(this.lastScrollX, 0);
        return;
      }
    } while (this.scrollOffset + i <= getWidth() + paramInt - this.scrollOffset * 2);
    this.lastScrollX = (i - getWidth() + this.scrollOffset * 3);
    smoothScrollTo(this.lastScrollX, 0);
  }
  
  public void addIconTab(int paramInt)
  {
    boolean bool = true;
    final int i = this.tabCount;
    this.tabCount = (i + 1);
    ImageView localImageView = new ImageView(getContext());
    localImageView.setFocusable(true);
    localImageView.setImageResource(paramInt);
    int j = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0).getInt("theme_emoji_tab_icolor", -5723992);
    MihanTheme.setColorFilter(getResources().getDrawable(paramInt), j);
    localImageView.setScaleType(ImageView.ScaleType.CENTER);
    localImageView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ScrollSlidingTabStrip.this.delegate.onPageSelected(i);
      }
    });
    this.tabsContainer.addView(localImageView);
    if (i == this.currentPosition) {}
    for (;;)
    {
      localImageView.setSelected(bool);
      return;
      bool = false;
    }
  }
  
  public void addStickerTab(TLRPC.Document paramDocument)
  {
    final int i = this.tabCount;
    this.tabCount = (i + 1);
    FrameLayout localFrameLayout = new FrameLayout(getContext());
    localFrameLayout.setFocusable(true);
    localFrameLayout.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ScrollSlidingTabStrip.this.delegate.onPageSelected(i);
      }
    });
    this.tabsContainer.addView(localFrameLayout);
    if (i == this.currentPosition) {}
    for (boolean bool = true;; bool = false)
    {
      localFrameLayout.setSelected(bool);
      BackupImageView localBackupImageView = new BackupImageView(getContext());
      if ((paramDocument != null) && (paramDocument.thumb != null)) {
        localBackupImageView.setImage(paramDocument.thumb.location, null, "webp", null);
      }
      localBackupImageView.setAspectFit(true);
      localFrameLayout.addView(localBackupImageView, LayoutHelper.createFrame(30, 30, 17));
      return;
    }
  }
  
  public int getCurrentPosition()
  {
    return this.currentPosition;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if ((isInEditMode()) || (this.tabCount == 0)) {
      return;
    }
    int i = getHeight();
    this.rectPaint.setColor(this.underlineColor);
    paramCanvas.drawRect(0.0F, i - this.underlineHeight, this.tabsContainer.getWidth(), i, this.rectPaint);
    View localView = this.tabsContainer.getChildAt(this.currentPosition);
    float f1 = 0.0F;
    float f2 = 0.0F;
    if (localView != null)
    {
      f1 = localView.getLeft();
      f2 = localView.getRight();
    }
    this.rectPaint.setColor(this.indicatorColor);
    paramCanvas.drawRect(f1, 0.0F, f2, i, this.rectPaint);
  }
  
  public void onPageScrolled(int paramInt1, int paramInt2)
  {
    if (this.currentPosition == paramInt1) {}
    do
    {
      return;
      this.currentPosition = paramInt1;
    } while (paramInt1 >= this.tabsContainer.getChildCount());
    int i = 0;
    if (i < this.tabsContainer.getChildCount())
    {
      View localView = this.tabsContainer.getChildAt(i);
      if (i == paramInt1) {}
      for (boolean bool = true;; bool = false)
      {
        localView.setSelected(bool);
        i += 1;
        break;
      }
    }
    if ((paramInt2 == paramInt1) && (paramInt1 > 1)) {
      scrollToChild(paramInt1 - 1);
    }
    for (;;)
    {
      invalidate();
      return;
      scrollToChild(paramInt1);
    }
  }
  
  protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void removeTabs()
  {
    this.tabsContainer.removeAllViews();
    this.tabCount = 0;
    this.currentPosition = 0;
  }
  
  public void selectTab(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.tabCount)) {
      return;
    }
    View localView = this.tabsContainer.getChildAt(paramInt);
    if (Build.VERSION.SDK_INT >= 15)
    {
      localView.callOnClick();
      return;
    }
    localView.performClick();
  }
  
  public void setDelegate(ScrollSlidingTabStripDelegate paramScrollSlidingTabStripDelegate)
  {
    this.delegate = paramScrollSlidingTabStripDelegate;
  }
  
  public void setIndicatorColor(int paramInt)
  {
    this.indicatorColor = paramInt;
    invalidate();
  }
  
  public void setUnderlineColor(int paramInt)
  {
    this.underlineColor = paramInt;
    invalidate();
  }
  
  public void setUnderlineColorResource(int paramInt)
  {
    this.underlineColor = getResources().getColor(paramInt);
    invalidate();
  }
  
  public void setUnderlineHeight(int paramInt)
  {
    this.underlineHeight = paramInt;
    invalidate();
  }
  
  public void updateTabStyles()
  {
    int i = 0;
    while (i < this.tabCount)
    {
      this.tabsContainer.getChildAt(i).setLayoutParams(this.defaultTabLayoutParams);
      i += 1;
    }
  }
  
  public static abstract interface ScrollSlidingTabStripDelegate
  {
    public abstract void onPageSelected(int paramInt);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Components\ScrollSlidingTabStrip.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */