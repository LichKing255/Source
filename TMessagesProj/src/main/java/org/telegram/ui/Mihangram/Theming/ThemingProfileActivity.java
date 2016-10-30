package org.telegram.ui.Mihangram.Theming;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.Mihangram.Theming.ColorPicker.ColorSelectorDialog;
import org.telegram.ui.Mihangram.Theming.ColorPicker.ColorSelectorDialog.OnColorChangedListener;

public class ThemingProfileActivity
  extends BaseFragment
{
  private int actionColorRow;
  private int actionGradientColorRow;
  private int actionGradientRow;
  private int actionIconColorRow;
  private int actionSectionRow2;
  private int actionSubTitleColorRow;
  private int actionTitleColorRow;
  private ListAdapter listAdapter;
  private ListView listView;
  private int rowCount = 0;
  private int userInfoRow;
  private int userInfoRow2;
  
  private void selectColor(View paramView, final SharedPreferences paramSharedPreferences, final String paramString)
  {
    int j = 0;
    int i = -1;
    switch (paramString.hashCode())
    {
    default: 
      switch (i)
      {
      default: 
        i = j;
      }
      break;
    }
    for (;;)
    {
      ((LayoutInflater)getParentActivity().getSystemService("layout_inflater")).inflate(2130903072, null, false);
      new ColorSelectorDialog(getParentActivity(), new ColorSelectorDialog.OnColorChangedListener()
      {
        public void colorChanged(int paramAnonymousInt)
        {
          paramSharedPreferences.edit().putInt(paramString, paramAnonymousInt).commit();
          if (ThemingProfileActivity.this.listView != null) {
            ThemingProfileActivity.this.listView.invalidateViews();
          }
        }
      }, i, 0, 0, true).show();
      return;
      if (!paramString.equals("theme_profile_action_color")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("theme_profile_action_gcolor")) {
        break;
      }
      i = 1;
      break;
      i = paramSharedPreferences.getInt(paramString, getActionBarColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getActionBarGradientcolor(paramSharedPreferences));
    }
  }
  
  private void selectGradient(final SharedPreferences paramSharedPreferences, final String paramString)
  {
    BottomSheet.Builder localBuilder = new BottomSheet.Builder(getParentActivity());
    localBuilder.setItems(MihanTheme.items, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramSharedPreferences.edit().putInt(paramString, paramAnonymousInt).commit();
        if (ThemingProfileActivity.this.listView != null) {
          ThemingProfileActivity.this.listView.invalidateViews();
        }
      }
    });
    showDialog(localBuilder.create());
  }
  
  private void setActionBarColors()
  {
    Object localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    this.actionBar.setBackButtonImage(2130837829);
    int i = ((SharedPreferences)localObject1).getInt("theme_setting_action_color", MihanTheme.getThemeColor((SharedPreferences)localObject1));
    int j = ((SharedPreferences)localObject1).getInt("theme_setting_action_gradient", 0);
    int k = ((SharedPreferences)localObject1).getInt("theme_setting_action_gcolor", i);
    Object localObject2;
    if (j != 0)
    {
      localObject2 = MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j));
      this.actionBar.setBackgroundDrawable((Drawable)localObject2);
    }
    for (;;)
    {
      i = ((SharedPreferences)localObject1).getInt("theme_setting_action_icolor", -1);
      this.actionBar.setTitleColor(((SharedPreferences)localObject1).getInt("theme_setting_action_tcolor", i));
      localObject1 = ApplicationLoader.applicationContext.getResources().getDrawable(2130837829);
      localObject2 = ApplicationLoader.applicationContext.getResources().getDrawable(2130837869);
      MihanTheme.setColorFilter((Drawable)localObject1, i);
      MihanTheme.setColorFilter((Drawable)localObject2, i);
      return;
      this.actionBar.setBackgroundColor(i);
    }
  }
  
  public View createView(final Context paramContext)
  {
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("ThemingProfileScreen", 2131166757));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1)
        {
          ThemingProfileActivity.this.finishFragment();
          return;
        }
        ThemingProfileActivity.this.presentFragment(new DialogsActivity(null));
      }
    });
    this.actionBar.createMenu().addItemWithWidth(1, 2130837869, AndroidUtilities.dp(56.0F));
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.listAdapter = new ListAdapter(paramContext);
    this.listView = new ListView(paramContext);
    this.listView.setDivider(null);
    this.listView.setDividerHeight(0);
    this.listView.setVerticalScrollBarEnabled(false);
    AndroidUtilities.setListViewEdgeEffectColor(this.listView, AvatarDrawable.getProfileBackColorForId(5));
    localFrameLayout.addView(this.listView);
    paramContext = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
    paramContext.width = -1;
    paramContext.height = -1;
    this.listView.setLayoutParams(paramContext);
    this.listView.setAdapter(this.listAdapter);
    paramContext = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        if (ThemingProfileActivity.this.getParentActivity() == null) {}
        do
        {
          return;
          if (paramAnonymousInt == ThemingProfileActivity.this.actionColorRow)
          {
            ThemingProfileActivity.this.selectColor(paramAnonymousView, paramContext, "theme_profile_action_color");
            return;
          }
          if (paramAnonymousInt == ThemingProfileActivity.this.actionGradientRow)
          {
            ThemingProfileActivity.this.selectGradient(paramContext, "theme_profile_action_gradient");
            return;
          }
          if (paramAnonymousInt == ThemingProfileActivity.this.actionGradientColorRow)
          {
            ThemingProfileActivity.this.selectColor(paramAnonymousView, paramContext, "theme_profile_action_gcolor");
            return;
          }
          if (paramAnonymousInt == ThemingProfileActivity.this.actionIconColorRow)
          {
            ThemingProfileActivity.this.selectColor(paramAnonymousView, paramContext, "theme_profile_action_icolor");
            return;
          }
          if (paramAnonymousInt == ThemingProfileActivity.this.actionTitleColorRow)
          {
            ThemingProfileActivity.this.selectColor(paramAnonymousView, paramContext, "theme_profile_action_tcolor");
            return;
          }
        } while (paramAnonymousInt != ThemingProfileActivity.this.actionSubTitleColorRow);
        ThemingProfileActivity.this.selectColor(paramAnonymousView, paramContext, "theme_profile_action_stcolor");
      }
    });
    this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
    {
      public boolean onItemLongClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        if (ThemingProfileActivity.this.getParentActivity() == null) {
          return false;
        }
        if (paramAnonymousInt == ThemingProfileActivity.this.actionColorRow) {
          MihanTheme.resetPreference("theme_profile_action_color", ThemingProfileActivity.this.listView);
        }
        for (;;)
        {
          return true;
          if (paramAnonymousInt == ThemingProfileActivity.this.actionGradientRow) {
            MihanTheme.resetPreference("theme_profile_action_gradient", ThemingProfileActivity.this.listView);
          } else if (paramAnonymousInt == ThemingProfileActivity.this.actionGradientColorRow) {
            MihanTheme.resetPreference("theme_profile_action_gcolor", ThemingProfileActivity.this.listView);
          } else if (paramAnonymousInt == ThemingProfileActivity.this.actionIconColorRow) {
            MihanTheme.resetPreference("theme_profile_action_icolor", ThemingProfileActivity.this.listView);
          } else if (paramAnonymousInt == ThemingProfileActivity.this.actionTitleColorRow) {
            MihanTheme.resetPreference("theme_profile_action_tcolor", ThemingProfileActivity.this.listView);
          } else if (paramAnonymousInt == ThemingProfileActivity.this.actionSubTitleColorRow) {
            MihanTheme.resetPreference("theme_profile_action_stcolor", ThemingProfileActivity.this.listView);
          }
        }
      }
    });
    return this.fragmentView;
  }
  
  public int getActionBarColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_profile_action_color", MihanTheme.getThemeColor(paramSharedPreferences));
  }
  
  public int getActionBarGradientFlag(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_profile_action_gradient", 0);
  }
  
  public int getActionBarGradientcolor(SharedPreferences paramSharedPreferences)
  {
    if (getActionBarGradientFlag(paramSharedPreferences) == 0) {
      return 0;
    }
    return paramSharedPreferences.getInt("theme_profile_action_gcolor", getActionBarColor(paramSharedPreferences));
  }
  
  public int getActionBarIconColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_profile_action_icolor", -1);
  }
  
  public int getActionBarSubTitleColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_profile_action_stcolor", MihanTheme.getLighterColor(getActionBarTitleColor(paramSharedPreferences), 0.8F));
  }
  
  public int getActionBarTitleColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_profile_action_tcolor", getActionBarIconColor(paramSharedPreferences));
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.actionSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.actionColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.actionGradientRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.actionGradientColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.actionIconColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.actionTitleColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.actionSubTitleColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.userInfoRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.userInfoRow2 = i;
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
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
      return ThemingProfileActivity.this.rowCount;
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
      if (paramInt == ThemingProfileActivity.this.userInfoRow) {}
      do
      {
        return 0;
        if ((paramInt == ThemingProfileActivity.this.actionSectionRow2) || (paramInt == ThemingProfileActivity.this.userInfoRow2)) {
          return 1;
        }
        if ((paramInt == ThemingProfileActivity.this.actionColorRow) || (paramInt == ThemingProfileActivity.this.actionGradientColorRow) || (paramInt == ThemingProfileActivity.this.actionIconColorRow) || (paramInt == ThemingProfileActivity.this.actionTitleColorRow) || (paramInt == ThemingProfileActivity.this.actionSubTitleColorRow)) {
          return 2;
        }
      } while (paramInt != ThemingProfileActivity.this.actionGradientRow);
      return 3;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      int i = getItemViewType(paramInt);
      if (i == 0)
      {
        paramViewGroup = paramView;
        if (paramView == null) {
          paramViewGroup = new ShadowSectionCell(this.mContext);
        }
      }
      Object localObject;
      SharedPreferences localSharedPreferences;
      do
      {
        do
        {
          do
          {
            do
            {
              return paramViewGroup;
              if (i != 1) {
                break;
              }
              localObject = paramView;
              if (paramView == null) {
                localObject = new HeaderCell(this.mContext);
              }
              paramViewGroup = (ViewGroup)localObject;
            } while (paramInt != ThemingProfileActivity.this.actionSectionRow2);
            ((HeaderCell)localObject).setText(LocaleController.getString("ThemingHeader", 2131166741));
            return (View)localObject;
            if (i != 2) {
              break;
            }
            localObject = paramView;
            if (paramView == null) {
              localObject = new TextColorCell(this.mContext);
            }
            paramView = (TextColorCell)localObject;
            localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
            if (paramInt == ThemingProfileActivity.this.actionColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingColor", 2131166694), ThemingProfileActivity.this.getActionBarColor(localSharedPreferences), false);
              return (View)localObject;
            }
            if (paramInt == ThemingProfileActivity.this.actionGradientColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingGradientColor", 2131166737), ThemingProfileActivity.this.getActionBarGradientcolor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingProfileActivity.this.actionIconColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingActionIconColor", 2131166683), ThemingProfileActivity.this.getActionBarIconColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingProfileActivity.this.actionTitleColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingTitleColor", 2131166805), ThemingProfileActivity.this.getActionBarTitleColor(localSharedPreferences), true);
              return (View)localObject;
            }
            paramViewGroup = (ViewGroup)localObject;
          } while (paramInt != ThemingProfileActivity.this.actionSubTitleColorRow);
          paramView.setTextAndColor(LocaleController.getString("ThemingSubTitleColor", 2131166785), ThemingProfileActivity.this.getActionBarSubTitleColor(localSharedPreferences), true);
          return (View)localObject;
          paramViewGroup = paramView;
        } while (i != 3);
        localObject = paramView;
        if (paramView == null) {
          localObject = new TextSettingsCell(this.mContext);
        }
        paramView = (TextSettingsCell)localObject;
        localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
        paramViewGroup = (ViewGroup)localObject;
      } while (paramInt != ThemingProfileActivity.this.actionGradientRow);
      paramView.setTextAndValue(LocaleController.getString("ThemingGradient", 2131166736), MihanTheme.getGradientString(localSharedPreferences, "theme_profile_action_gradient"), false);
      return (View)localObject;
    }
    
    public int getViewTypeCount()
    {
      return 4;
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
      SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
      if ((paramInt == ThemingProfileActivity.this.actionGradientColorRow) && (ThemingProfileActivity.this.getActionBarGradientFlag(localSharedPreferences) == 0)) {}
      while ((paramInt != ThemingProfileActivity.this.actionColorRow) && (paramInt != ThemingProfileActivity.this.actionGradientRow) && (paramInt != ThemingProfileActivity.this.actionGradientColorRow) && (paramInt != ThemingProfileActivity.this.actionIconColorRow) && (paramInt != ThemingProfileActivity.this.actionTitleColorRow) && (paramInt != ThemingProfileActivity.this.actionSubTitleColorRow)) {
        return false;
      }
      return true;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\Theming\ThemingProfileActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */