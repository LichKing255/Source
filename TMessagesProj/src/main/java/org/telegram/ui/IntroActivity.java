package org.telegram.ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.Theme;

public class IntroActivity
  extends Activity
{
  private ViewGroup bottomPages;
  private int[] icons;
  private boolean justCreated = false;
  private int lastPage = 0;
  private int[] messages;
  private boolean startPressed = false;
  private int[] titles;
  private ImageView topImage1;
  private ImageView topImage2;
  private ViewPager viewPager;
  
  protected void onCreate(Bundle paramBundle)
  {
    setTheme(2131361926);
    super.onCreate(paramBundle);
    Theme.loadRecources(this);
    requestWindowFeature(1);
    if (AndroidUtilities.isTablet())
    {
      setContentView(2130903089);
      if (!LocaleController.isRTL) {
        break label483;
      }
      this.icons = new int[] { 2130837919, 2130837918, 2130837917, 2130837916, 2130837915, 2130837914, 2130837912 };
      this.titles = new int[] { 2131166145, 2131166143, 2131166141, 2131166139, 2131166137, 2131166135, 2131166133 };
    }
    for (this.messages = new int[] { 2131166144, 2131166142, 2131166140, 2131166138, 2131166136, 2131166134, 2131166132 };; this.messages = new int[] { 2131166132, 2131166134, 2131166136, 2131166138, 2131166140, 2131166142, 2131166144 })
    {
      this.viewPager = ((ViewPager)findViewById(2131624162));
      paramBundle = (TextView)findViewById(2131624163);
      paramBundle.setText(LocaleController.getString("StartMessaging", 2131166362).toUpperCase());
      if (LocaleController.isRTL) {
        paramBundle.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      }
      paramBundle.setBackgroundResource(2130837920);
      if (Build.VERSION.SDK_INT >= 21)
      {
        StateListAnimator localStateListAnimator = new StateListAnimator();
        ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(paramBundle, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
        localStateListAnimator.addState(new int[] { 16842919 }, localObjectAnimator);
        localObjectAnimator = ObjectAnimator.ofFloat(paramBundle, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
        localStateListAnimator.addState(new int[0], localObjectAnimator);
        paramBundle.setStateListAnimator(localStateListAnimator);
      }
      this.topImage1 = ((ImageView)findViewById(2131624160));
      this.topImage2 = ((ImageView)findViewById(2131624161));
      this.bottomPages = ((ViewGroup)findViewById(2131624164));
      this.topImage2.setVisibility(8);
      this.viewPager.setAdapter(new IntroAdapter(null));
      this.viewPager.setPageMargin(0);
      this.viewPager.setOffscreenPageLimit(1);
      this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
      {
        public void onPageScrollStateChanged(int paramAnonymousInt)
        {
          final ImageView localImageView2;
          if (((paramAnonymousInt == 0) || (paramAnonymousInt == 2)) && (IntroActivity.this.lastPage != IntroActivity.this.viewPager.getCurrentItem()))
          {
            IntroActivity.access$102(IntroActivity.this, IntroActivity.this.viewPager.getCurrentItem());
            if (IntroActivity.this.topImage1.getVisibility() != 0) {
              break label170;
            }
            localImageView2 = IntroActivity.this.topImage1;
          }
          for (final ImageView localImageView1 = IntroActivity.this.topImage2;; localImageView1 = IntroActivity.this.topImage1)
          {
            localImageView1.bringToFront();
            localImageView1.setImageResource(IntroActivity.this.icons[IntroActivity.this.lastPage]);
            localImageView1.clearAnimation();
            localImageView2.clearAnimation();
            Animation localAnimation1 = AnimationUtils.loadAnimation(IntroActivity.this, 2130968587);
            localAnimation1.setAnimationListener(new Animation.AnimationListener()
            {
              public void onAnimationEnd(Animation paramAnonymous2Animation)
              {
                localImageView2.setVisibility(8);
              }
              
              public void onAnimationRepeat(Animation paramAnonymous2Animation) {}
              
              public void onAnimationStart(Animation paramAnonymous2Animation) {}
            });
            Animation localAnimation2 = AnimationUtils.loadAnimation(IntroActivity.this, 2130968586);
            localAnimation2.setAnimationListener(new Animation.AnimationListener()
            {
              public void onAnimationEnd(Animation paramAnonymous2Animation) {}
              
              public void onAnimationRepeat(Animation paramAnonymous2Animation) {}
              
              public void onAnimationStart(Animation paramAnonymous2Animation)
              {
                localImageView1.setVisibility(0);
              }
            });
            localImageView2.startAnimation(localAnimation1);
            localImageView1.startAnimation(localAnimation2);
            return;
            label170:
            localImageView2 = IntroActivity.this.topImage2;
          }
        }
        
        public void onPageScrolled(int paramAnonymousInt1, float paramAnonymousFloat, int paramAnonymousInt2) {}
        
        public void onPageSelected(int paramAnonymousInt) {}
      });
      paramBundle.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (IntroActivity.this.startPressed) {
            return;
          }
          IntroActivity.access$602(IntroActivity.this, true);
          paramAnonymousView = new Intent(IntroActivity.this, LaunchActivity.class);
          paramAnonymousView.putExtra("fromIntro", true);
          IntroActivity.this.startActivity(paramAnonymousView);
          IntroActivity.this.finish();
        }
      });
      if (BuildVars.DEBUG_VERSION) {
        paramBundle.setOnLongClickListener(new View.OnLongClickListener()
        {
          public boolean onLongClick(View paramAnonymousView)
          {
            ConnectionsManager.getInstance().switchBackend();
            return true;
          }
        });
      }
      this.justCreated = true;
      return;
      setRequestedOrientation(1);
      setContentView(2130903088);
      break;
      label483:
      this.icons = new int[] { 2130837912, 2130837914, 2130837915, 2130837916, 2130837917, 2130837918, 2130837919 };
      this.titles = new int[] { 2131166133, 2131166135, 2131166137, 2131166139, 2131166141, 2131166143, 2131166145 };
    }
  }
  
  protected void onPause()
  {
    super.onPause();
    AndroidUtilities.unregisterUpdates();
  }
  
  protected void onResume()
  {
    super.onResume();
    if (this.justCreated)
    {
      if (!LocaleController.isRTL) {
        break label46;
      }
      this.viewPager.setCurrentItem(6);
    }
    for (this.lastPage = 6;; this.lastPage = 0)
    {
      this.justCreated = false;
      AndroidUtilities.checkForCrashes(this);
      AndroidUtilities.checkForUpdates(this);
      return;
      label46:
      this.viewPager.setCurrentItem(0);
    }
  }
  
  private class IntroAdapter
    extends PagerAdapter
  {
    private IntroAdapter() {}
    
    public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
    {
      paramViewGroup.removeView((View)paramObject);
    }
    
    public int getCount()
    {
      return 7;
    }
    
    public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
    {
      View localView = View.inflate(paramViewGroup.getContext(), 2130903090, null);
      TextView localTextView1 = (TextView)localView.findViewById(2131624165);
      TextView localTextView2 = (TextView)localView.findViewById(2131624166);
      paramViewGroup.addView(localView, 0);
      localTextView1.setText(IntroActivity.this.getString(IntroActivity.this.titles[paramInt]));
      localTextView2.setText(AndroidUtilities.replaceTags(IntroActivity.this.getString(IntroActivity.this.messages[paramInt])));
      if (LocaleController.isRTL)
      {
        localTextView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        localTextView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      }
      return localView;
    }
    
    public boolean isViewFromObject(View paramView, Object paramObject)
    {
      return paramView.equals(paramObject);
    }
    
    public void restoreState(Parcelable paramParcelable, ClassLoader paramClassLoader) {}
    
    public Parcelable saveState()
    {
      return null;
    }
    
    public void setPrimaryItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
    {
      super.setPrimaryItem(paramViewGroup, paramInt, paramObject);
      int j = IntroActivity.this.bottomPages.getChildCount();
      int i = 0;
      if (i < j)
      {
        paramViewGroup = IntroActivity.this.bottomPages.getChildAt(i);
        if (i == paramInt) {
          paramViewGroup.setBackgroundColor(-13851168);
        }
        for (;;)
        {
          i += 1;
          break;
          paramViewGroup.setBackgroundColor(-4473925);
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
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\IntroActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */