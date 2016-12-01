package org.telegram.ui.Supergram.Theming.Fonts;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;

public class FontSelectActivity
  extends BaseFragment
{
  ArrayList<String> fonts = new ArrayList();
  private BaseFragmentAdapter listAdapter;
  private ListView listView;
  
  private void restartApp()
  {
    Context localContext = getParentActivity().getBaseContext();
    Object localObject = localContext.getPackageManager().getLaunchIntentForPackage(localContext.getPackageName());
    ((Intent)localObject).addFlags(67108864);
    ((Intent)localObject).addFlags(268435456);
    if (Build.VERSION.SDK_INT >= 11) {
      ((Intent)localObject).addFlags(32768);
    }
    localObject = PendingIntent.getActivity(localContext, 0, (Intent)localObject, 268435456);
    ((AlarmManager)localContext.getSystemService("alarm")).set(1, System.currentTimeMillis() + 1L, (PendingIntent)localObject);
    System.exit(2);
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837810);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("Fonts", 2131165696));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          FontSelectActivity.this.finishFragment();
        }
      }
    });
    this.fonts.add("پیش فرض سیستم");
    this.fonts.add("تلگرام");
    this.fonts.add("ایران سانس نازک");
    this.fonts.add("ایران سانس معمولی");
    this.fonts.add("ایران سانس متوسط");
    this.fonts.add("ایران سانس ضخیم");
    this.fonts.add("افسانه");
    this.fonts.add("دست نویس");
    this.fonts.add("هما");
    this.fonts.add("مروارید");
    this.fonts.add("یکان");
    this.fonts.add("تیتر");
    this.fonts.add("کودک");
    this.listAdapter = new ListAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext);
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    localLinearLayout.setVisibility(4);
    localLinearLayout.setOrientation(1);
    ((FrameLayout)this.fragmentView).addView(localLinearLayout);
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localLinearLayout.getLayoutParams();
    localLayoutParams.width = -1;
    localLayoutParams.height = -1;
    localLayoutParams.gravity = 48;
    localLinearLayout.setLayoutParams(localLayoutParams);
    localLinearLayout.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.listView = new ListView(paramContext);
    this.listView.setEmptyView(localLinearLayout);
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setDivider(null);
    this.listView.setDividerHeight(0);
    this.listView.setAdapter(this.listAdapter);
    ((FrameLayout)this.fragmentView).addView(this.listView);
    paramContext = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
    paramContext.width = -1;
    paramContext.height = -1;
    this.listView.setLayoutParams(paramContext);
    this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        paramAnonymousAdapterView = ((String)FontSelectActivity.this.fonts.get(paramAnonymousInt)).toString();
        paramAnonymousView = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0).edit();
        paramAnonymousView.putString("font_type", paramAnonymousAdapterView);
        paramAnonymousView.commit();
        FontSelectActivity.this.restartApp();
      }
    });
    this.listView.setOnScrollListener(new AbsListView.OnScrollListener()
    {
      public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      
      public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt)
      {
        if (paramAnonymousInt == 1) {
          AndroidUtilities.hideKeyboard(FontSelectActivity.this.getParentActivity().getCurrentFocus());
        }
      }
    });
    return this.fragmentView;
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
  }
  
  private class ListAdapter
    extends BaseFragmentAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public boolean areAllItemsEnabled()
    {
      return true;
    }
    
    public int getCount()
    {
      return FontSelectActivity.this.fonts.size();
    }
    
    public Object getItem(int paramInt)
    {
      return null;
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
      paramViewGroup = paramView;
      if (paramView == null) {
        paramViewGroup = new FontSettingsCell(this.mContext, paramInt);
      }
      paramView = (FontSettingsCell)paramViewGroup;
      String str = (String)FontSelectActivity.this.fonts.get(paramInt);
      if (paramInt != FontSelectActivity.this.fonts.size() - 1) {}
      for (boolean bool = true;; bool = false)
      {
        paramView.setText(str, bool);
        return paramViewGroup;
      }
    }
    
    public int getViewTypeCount()
    {
      return 1;
    }
    
    public boolean hasStableIds()
    {
      return false;
    }
    
    public boolean isEmpty()
    {
      return false;
    }
    
    public boolean isEnabled(int paramInt)
    {
      return true;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\Theming\Fonts\FontSelectActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */