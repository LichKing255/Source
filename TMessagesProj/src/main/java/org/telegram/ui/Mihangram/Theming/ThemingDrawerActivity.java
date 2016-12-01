package org.telegram.ui.Supergram.Theming;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.Supergram.Theming.ColorPicker.ColorSelectorDialog;
import org.telegram.ui.Supergram.Theming.ColorPicker.ColorSelectorDialog.OnColorChangedListener;

public class ThemingDrawerActivity
  extends BaseFragment
{
  private int avatarRadiusRow;
  private int centerUserInfoRow;
  private int headerColorRow;
  private int headerGradientColorRow;
  private int headerGradientRow;
  private int headerSectionRow2;
  private int hideCustomBGRow;
  private int hideCustomBGShadowRow;
  private ListAdapter listAdapter;
  private ListView listView;
  private int menuColorRow;
  private int menuDividerColorRow;
  private int menuGradientColorRow;
  private int menuGradientRow;
  private int menuIconColorRow;
  private int menuSectionRow;
  private int menuSectionRow2;
  private int menuTextColorRow;
  private int nameColorRow;
  private int phoneColorRow;
  private int rowCount = 0;
  
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
          if (ThemingDrawerActivity.this.listView != null) {
            ThemingDrawerActivity.this.listView.invalidateViews();
          }
        }
      }, i, 0, 0, true).show();
      return;
      if (!paramString.equals("theme_drawer_header_color")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("theme_drawer_header_gcolor")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("theme_drawer_menu_color")) {
        break;
      }
      i = 2;
      break;
      if (!paramString.equals("theme_drawer_menu_gcolor")) {
        break;
      }
      i = 3;
      break;
      if (!paramString.equals("theme_drawer_name_color")) {
        break;
      }
      i = 4;
      break;
      if (!paramString.equals("theme_drawer_phone_color")) {
        break;
      }
      i = 5;
      break;
      if (!paramString.equals("theme_drawer_menu_tcolor")) {
        break;
      }
      i = 6;
      break;
      if (!paramString.equals("theme_drawer_menu_icolor")) {
        break;
      }
      i = 7;
      break;
      if (!paramString.equals("theme_drawer_divider_color")) {
        break;
      }
      i = 8;
      break;
      i = paramSharedPreferences.getInt(paramString, getHeaderColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getHeaderGradientColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getMenuBGColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getMenuGradientColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getNameColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getPhoneColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getMenuTextColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getMenuIconColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getMenuIconColor(paramSharedPreferences));
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
        if (ThemingDrawerActivity.this.listView != null) {
          ThemingDrawerActivity.this.listView.invalidateViews();
        }
      }
    });
    showDialog(localBuilder.create());
  }
  
  private void updateColors(ActionBarMenu paramActionBarMenu)
  {
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    int i = ((SharedPreferences)localObject).getInt("theme_setting_action_color", MihanTheme.getThemeColor((SharedPreferences)localObject));
    int j = ((SharedPreferences)localObject).getInt("theme_setting_action_gradient", 0);
    int k = ((SharedPreferences)localObject).getInt("theme_setting_action_gcolor", i);
    if (j != 0)
    {
      GradientDrawable localGradientDrawable = MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j));
      this.actionBar.setBackgroundDrawable(localGradientDrawable);
    }
    for (;;)
    {
      i = ((SharedPreferences)localObject).getInt("theme_setting_action_icolor", -1);
      this.actionBar.setTitleColor(((SharedPreferences)localObject).getInt("theme_setting_action_tcolor", i));
      localObject = ApplicationLoader.applicationContext.getResources().getDrawable(2130837829);
      MihanTheme.setColorFilter((Drawable)localObject, i);
      this.actionBar.setBackButtonDrawable((Drawable)localObject);
      MihanTheme.setColorFilter(paramActionBarMenu.getItem(1).getImageView().getDrawable(), i);
      return;
      this.actionBar.setBackgroundColor(i);
    }
  }
  
  public View createView(final Context paramContext)
  {
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("ThemingDrawerScreen", 2131166719));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1)
        {
          ThemingDrawerActivity.this.finishFragment();
          return;
        }
        ThemingDrawerActivity.this.presentFragment(new DialogsActivity(null));
      }
    });
    Object localObject = this.actionBar.createMenu();
    ((ActionBarMenu)localObject).addItemWithWidth(1, 2130837869, AndroidUtilities.dp(56.0F));
    updateColors((ActionBarMenu)localObject);
    this.fragmentView = new FrameLayout(paramContext);
    localObject = (FrameLayout)this.fragmentView;
    this.listAdapter = new ListAdapter(paramContext);
    this.listView = new ListView(paramContext);
    this.listView.setDivider(null);
    this.listView.setDividerHeight(0);
    this.listView.setVerticalScrollBarEnabled(false);
    AndroidUtilities.setListViewEdgeEffectColor(this.listView, AvatarDrawable.getProfileBackColorForId(5));
    ((FrameLayout)localObject).addView(this.listView);
    paramContext = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
    paramContext.width = -1;
    paramContext.height = -1;
    this.listView.setLayoutParams(paramContext);
    this.listView.setAdapter(this.listAdapter);
    paramContext = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, final View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        boolean bool4 = true;
        boolean bool2 = true;
        boolean bool3 = false;
        if (ThemingDrawerActivity.this.getParentActivity() == null) {}
        label369:
        do
        {
          do
          {
            do
            {
              return;
              if (paramAnonymousInt == ThemingDrawerActivity.this.headerColorRow)
              {
                ThemingDrawerActivity.this.selectColor(paramAnonymousView, paramContext, "theme_drawer_header_color");
                return;
              }
              if (paramAnonymousInt == ThemingDrawerActivity.this.headerGradientRow)
              {
                ThemingDrawerActivity.this.selectGradient(paramContext, "theme_drawer_header_gradient");
                return;
              }
              if (paramAnonymousInt == ThemingDrawerActivity.this.headerGradientColorRow)
              {
                ThemingDrawerActivity.this.selectColor(paramAnonymousView, paramContext, "theme_drawer_header_gcolor");
                return;
              }
              if (paramAnonymousInt == ThemingDrawerActivity.this.avatarRadiusRow)
              {
                paramAnonymousAdapterView = new AlertDialog.Builder(ThemingDrawerActivity.this.getParentActivity());
                paramAnonymousAdapterView.setTitle(LocaleController.getString("AppName", 2131165338));
                paramAnonymousView = new NumberPicker(ThemingDrawerActivity.this.getParentActivity());
                paramAnonymousView.setMinValue(1);
                paramAnonymousView.setMaxValue(32);
                paramAnonymousView.setValue(ThemingDrawerActivity.this.getHeaderAvatarRadius(paramContext));
                paramAnonymousAdapterView.setView(paramAnonymousView);
                paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Done", 2131165634), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                  {
                    paramAnonymous2DialogInterface = ThemingDrawerActivity.2.this.val$preferences.edit();
                    paramAnonymous2DialogInterface.putInt("theme_drawer_avatar_radius", paramAnonymousView.getValue());
                    paramAnonymous2DialogInterface.commit();
                    if (ThemingDrawerActivity.this.listView != null) {
                      ThemingDrawerActivity.this.listView.invalidateViews();
                    }
                  }
                });
                ThemingDrawerActivity.this.showDialog(paramAnonymousAdapterView.create());
                return;
              }
              if (paramAnonymousInt == ThemingDrawerActivity.this.nameColorRow)
              {
                ThemingDrawerActivity.this.selectColor(paramAnonymousView, paramContext, "theme_drawer_name_color");
                return;
              }
              if (paramAnonymousInt == ThemingDrawerActivity.this.phoneColorRow)
              {
                ThemingDrawerActivity.this.selectColor(paramAnonymousView, paramContext, "theme_drawer_phone_color");
                return;
              }
              if (paramAnonymousInt != ThemingDrawerActivity.this.centerUserInfoRow) {
                break label369;
              }
              bool3 = paramContext.getBoolean("theme_drawer_center_info", false);
              paramAnonymousAdapterView = paramContext.edit();
              if (bool3) {
                break;
              }
              bool1 = true;
              paramAnonymousAdapterView.putBoolean("theme_drawer_center_info", bool1);
              paramAnonymousAdapterView.commit();
            } while (!(paramAnonymousView instanceof TextCheckCell));
            paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
            if (!bool3) {}
            for (bool1 = bool2;; bool1 = false)
            {
              paramAnonymousAdapterView.setChecked(bool1);
              return;
              bool1 = false;
              break;
            }
            if (paramAnonymousInt == ThemingDrawerActivity.this.menuColorRow)
            {
              ThemingDrawerActivity.this.selectColor(paramAnonymousView, paramContext, "theme_drawer_menu_color");
              return;
            }
            if (paramAnonymousInt == ThemingDrawerActivity.this.menuGradientRow)
            {
              ThemingDrawerActivity.this.selectGradient(paramContext, "theme_drawer_menu_gradient");
              return;
            }
            if (paramAnonymousInt == ThemingDrawerActivity.this.menuGradientColorRow)
            {
              ThemingDrawerActivity.this.selectColor(paramAnonymousView, paramContext, "theme_drawer_menu_gcolor");
              return;
            }
            if (paramAnonymousInt == ThemingDrawerActivity.this.menuTextColorRow)
            {
              ThemingDrawerActivity.this.selectColor(paramAnonymousView, paramContext, "theme_drawer_menu_tcolor");
              return;
            }
            if (paramAnonymousInt == ThemingDrawerActivity.this.menuIconColorRow)
            {
              ThemingDrawerActivity.this.selectColor(paramAnonymousView, paramContext, "theme_drawer_menu_icolor");
              return;
            }
            if (paramAnonymousInt == ThemingDrawerActivity.this.menuDividerColorRow)
            {
              ThemingDrawerActivity.this.selectColor(paramAnonymousView, paramContext, "theme_drawer_divider_color");
              return;
            }
            if (paramAnonymousInt == ThemingDrawerActivity.this.hideCustomBGRow)
            {
              bool2 = paramContext.getBoolean("theme_drawer_hide_cbg", false);
              paramAnonymousAdapterView = paramContext.edit();
              if (!bool2) {}
              for (bool1 = true;; bool1 = false)
              {
                paramAnonymousAdapterView.putBoolean("theme_drawer_hide_cbg", bool1);
                paramAnonymousAdapterView.commit();
                if ((paramAnonymousView instanceof TextCheckCell))
                {
                  paramAnonymousView = (TextCheckCell)paramAnonymousView;
                  bool1 = bool3;
                  if (!bool2) {
                    bool1 = true;
                  }
                  paramAnonymousView.setChecked(bool1);
                }
                if (bool2) {
                  break;
                }
                paramAnonymousAdapterView.putBoolean("theme_drawer_hide_cbgs", true);
                paramAnonymousAdapterView.commit();
                if (ThemingDrawerActivity.this.listView == null) {
                  break;
                }
                ThemingDrawerActivity.this.listView.invalidateViews();
                return;
              }
            }
          } while (paramAnonymousInt != ThemingDrawerActivity.this.hideCustomBGShadowRow);
          bool2 = paramContext.getBoolean("theme_drawer_hide_cbgs", false);
          paramAnonymousAdapterView = paramContext.edit();
          if (bool2) {
            break;
          }
          bool1 = true;
          paramAnonymousAdapterView.putBoolean("theme_drawer_hide_cbgs", bool1);
          paramAnonymousAdapterView.commit();
        } while (!(paramAnonymousView instanceof TextCheckCell));
        paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
        if (!bool2) {}
        for (boolean bool1 = bool4;; bool1 = false)
        {
          paramAnonymousAdapterView.setChecked(bool1);
          return;
          bool1 = false;
          break;
        }
      }
    });
    this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
    {
      public boolean onItemLongClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        if (ThemingDrawerActivity.this.getParentActivity() == null) {
          return false;
        }
        if (paramAnonymousInt == ThemingDrawerActivity.this.headerColorRow) {
          MihanTheme.resetPreference("theme_drawer_header_color", ThemingDrawerActivity.this.listView);
        }
        for (;;)
        {
          return true;
          if (paramAnonymousInt == ThemingDrawerActivity.this.headerGradientRow) {
            MihanTheme.resetPreference("theme_drawer_header_gradient", ThemingDrawerActivity.this.listView);
          } else if (paramAnonymousInt == ThemingDrawerActivity.this.headerGradientColorRow) {
            MihanTheme.resetPreference("theme_drawer_header_gcolor", ThemingDrawerActivity.this.listView);
          } else if (paramAnonymousInt == ThemingDrawerActivity.this.nameColorRow) {
            MihanTheme.resetPreference("theme_drawer_name_color", ThemingDrawerActivity.this.listView);
          } else if (paramAnonymousInt == ThemingDrawerActivity.this.phoneColorRow) {
            MihanTheme.resetPreference("theme_drawer_phone_color", ThemingDrawerActivity.this.listView);
          } else if (paramAnonymousInt == ThemingDrawerActivity.this.centerUserInfoRow) {
            MihanTheme.resetPreference("theme_drawer_center_info", ThemingDrawerActivity.this.listView);
          } else if (paramAnonymousInt == ThemingDrawerActivity.this.hideCustomBGRow) {
            MihanTheme.resetPreference("theme_drawer_hide_cbg", ThemingDrawerActivity.this.listView);
          } else if (paramAnonymousInt == ThemingDrawerActivity.this.hideCustomBGShadowRow) {
            MihanTheme.resetPreference("theme_drawer_hide_cbgs", ThemingDrawerActivity.this.listView);
          } else if (paramAnonymousInt == ThemingDrawerActivity.this.menuColorRow) {
            MihanTheme.resetPreference("theme_drawer_menu_color", ThemingDrawerActivity.this.listView);
          } else if (paramAnonymousInt == ThemingDrawerActivity.this.menuGradientRow) {
            MihanTheme.resetPreference("theme_drawer_menu_gradient", ThemingDrawerActivity.this.listView);
          } else if (paramAnonymousInt == ThemingDrawerActivity.this.menuGradientColorRow) {
            MihanTheme.resetPreference("theme_drawer_menu_gcolor", ThemingDrawerActivity.this.listView);
          } else if (paramAnonymousInt == ThemingDrawerActivity.this.menuTextColorRow) {
            MihanTheme.resetPreference("theme_drawer_menu_tcolor", ThemingDrawerActivity.this.listView);
          } else if (paramAnonymousInt == ThemingDrawerActivity.this.menuIconColorRow) {
            MihanTheme.resetPreference("theme_drawer_menu_icolor", ThemingDrawerActivity.this.listView);
          } else if (paramAnonymousInt == ThemingDrawerActivity.this.menuDividerColorRow) {
            MihanTheme.resetPreference("theme_drawer_divider_color", ThemingDrawerActivity.this.listView);
          } else if (paramAnonymousInt == ThemingDrawerActivity.this.avatarRadiusRow) {
            MihanTheme.resetPreference("theme_drawer_avatar_radius", ThemingDrawerActivity.this.listView);
          }
        }
      }
    });
    return this.fragmentView;
  }
  
  public int getHeaderAvatarRadius(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_drawer_avatar_radius", 32);
  }
  
  public int getHeaderColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_drawer_header_color", MihanTheme.getActionBarColor(paramSharedPreferences));
  }
  
  public int getHeaderGradientColor(SharedPreferences paramSharedPreferences)
  {
    if (getHeaderGradientFlag(paramSharedPreferences) == 0) {
      return 0;
    }
    return paramSharedPreferences.getInt("theme_drawer_header_gcolor", MihanTheme.getActionBarGradientColor(paramSharedPreferences));
  }
  
  public int getHeaderGradientFlag(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_drawer_header_gradient", MihanTheme.getActionBarGradientFlag(paramSharedPreferences));
  }
  
  public int getMenuBGColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_drawer_menu_color", -1);
  }
  
  public int getMenuDividerColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_drawer_divider_color", -2500135);
  }
  
  public int getMenuGradientColor(SharedPreferences paramSharedPreferences)
  {
    if (getMenuGradientFlag(paramSharedPreferences) == 0) {
      return 0;
    }
    return paramSharedPreferences.getInt("theme_drawer_menu_gcolor", getMenuBGColor(paramSharedPreferences));
  }
  
  public int getMenuGradientFlag(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_drawer_menu_gradient", 0);
  }
  
  public int getMenuIconColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_drawer_menu_icolor", MihanTheme.getThemeColor(paramSharedPreferences));
  }
  
  public int getMenuTextColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_drawer_menu_tcolor", MihanTheme.getActionBarIconColor(paramSharedPreferences));
  }
  
  public int getNameColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_drawer_name_color", MihanTheme.getActionBarTitleColor(paramSharedPreferences));
  }
  
  public int getPhoneColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_drawer_phone_color", MihanTheme.getActionBarTitleColor(paramSharedPreferences));
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.headerSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.hideCustomBGRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.hideCustomBGShadowRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.headerColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.headerGradientRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.headerGradientColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.avatarRadiusRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.nameColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.phoneColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.centerUserInfoRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.menuSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.menuSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.menuColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.menuGradientRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.menuGradientColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.menuTextColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.menuIconColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.menuDividerColorRow = i;
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
  
  protected void setDrawerIconColors(int paramInt)
  {
    Drawable localDrawable1 = ApplicationLoader.applicationContext.getResources().getDrawable(2130837972);
    Drawable localDrawable2 = ApplicationLoader.applicationContext.getResources().getDrawable(2130837977);
    Drawable localDrawable3 = ApplicationLoader.applicationContext.getResources().getDrawable(2130837960);
    Drawable localDrawable4 = ApplicationLoader.applicationContext.getResources().getDrawable(2130837962);
    Drawable localDrawable5 = ApplicationLoader.applicationContext.getResources().getDrawable(2130837974);
    Drawable localDrawable6 = ApplicationLoader.applicationContext.getResources().getDrawable(2130837975);
    Drawable localDrawable7 = ApplicationLoader.applicationContext.getResources().getDrawable(2130837981);
    Drawable localDrawable8 = ApplicationLoader.applicationContext.getResources().getDrawable(2130837968);
    Drawable localDrawable9 = ApplicationLoader.applicationContext.getResources().getDrawable(2130837964);
    Drawable localDrawable10 = ApplicationLoader.applicationContext.getResources().getDrawable(2130837970);
    Drawable localDrawable11 = ApplicationLoader.applicationContext.getResources().getDrawable(2130837979);
    Drawable localDrawable12 = ApplicationLoader.applicationContext.getResources().getDrawable(2130837966);
    MihanTheme.setColorFilter(localDrawable1, paramInt);
    MihanTheme.setColorFilter(localDrawable2, paramInt);
    MihanTheme.setColorFilter(localDrawable3, paramInt);
    MihanTheme.setColorFilter(localDrawable4, paramInt);
    MihanTheme.setColorFilter(localDrawable5, paramInt);
    MihanTheme.setColorFilter(localDrawable6, paramInt);
    MihanTheme.setColorFilter(localDrawable7, paramInt);
    MihanTheme.setColorFilter(localDrawable8, paramInt);
    MihanTheme.setColorFilter(localDrawable9, paramInt);
    MihanTheme.setColorFilter(localDrawable10, paramInt);
    MihanTheme.setColorFilter(localDrawable11, paramInt);
    MihanTheme.setColorFilter(localDrawable12, paramInt);
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
      return ThemingDrawerActivity.this.rowCount;
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
      if (paramInt == ThemingDrawerActivity.this.menuSectionRow) {}
      do
      {
        return 0;
        if ((paramInt == ThemingDrawerActivity.this.headerSectionRow2) || (paramInt == ThemingDrawerActivity.this.menuSectionRow2)) {
          return 1;
        }
        if ((paramInt == ThemingDrawerActivity.this.headerColorRow) || (paramInt == ThemingDrawerActivity.this.headerGradientColorRow) || (paramInt == ThemingDrawerActivity.this.menuColorRow) || (paramInt == ThemingDrawerActivity.this.menuGradientColorRow) || (paramInt == ThemingDrawerActivity.this.nameColorRow) || (paramInt == ThemingDrawerActivity.this.phoneColorRow) || (paramInt == ThemingDrawerActivity.this.menuTextColorRow) || (paramInt == ThemingDrawerActivity.this.menuIconColorRow) || (paramInt == ThemingDrawerActivity.this.menuDividerColorRow)) {
          return 2;
        }
        if ((paramInt == ThemingDrawerActivity.this.headerGradientRow) || (paramInt == ThemingDrawerActivity.this.menuGradientRow) || (paramInt == ThemingDrawerActivity.this.avatarRadiusRow)) {
          return 3;
        }
      } while ((paramInt != ThemingDrawerActivity.this.centerUserInfoRow) && (paramInt != ThemingDrawerActivity.this.hideCustomBGRow) && (paramInt != ThemingDrawerActivity.this.hideCustomBGShadowRow));
      return 4;
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
                if (paramInt == ThemingDrawerActivity.this.headerSectionRow2)
                {
                  ((HeaderCell)localObject).setText(LocaleController.getString("ThemingDrawerHeader", 2131166711));
                  return (View)localObject;
                }
                paramViewGroup = (ViewGroup)localObject;
              } while (paramInt != ThemingDrawerActivity.this.menuSectionRow2);
              ((HeaderCell)localObject).setText(LocaleController.getString("ThemingDrawerMenu", 2131166714));
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
              if (paramInt == ThemingDrawerActivity.this.headerColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingColor", 2131166694), ThemingDrawerActivity.this.getHeaderColor(localSharedPreferences), false);
                return (View)localObject;
              }
              if (paramInt == ThemingDrawerActivity.this.headerGradientColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingGradientColor", 2131166737), ThemingDrawerActivity.this.getHeaderGradientColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingDrawerActivity.this.nameColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingDrawerNameColor", 2131166717), ThemingDrawerActivity.this.getNameColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingDrawerActivity.this.phoneColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingDrawerPhoneColor", 2131166718), ThemingDrawerActivity.this.getPhoneColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingDrawerActivity.this.menuColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingColor", 2131166694), ThemingDrawerActivity.this.getMenuBGColor(localSharedPreferences), false);
                return (View)localObject;
              }
              if (paramInt == ThemingDrawerActivity.this.menuGradientColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingGradientColor", 2131166737), ThemingDrawerActivity.this.getMenuGradientColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingDrawerActivity.this.menuTextColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingDrawerMenuTextColor", 2131166716), ThemingDrawerActivity.this.getMenuTextColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingDrawerActivity.this.menuIconColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingDrawerMenuIconColor", 2131166715), ThemingDrawerActivity.this.getMenuIconColor(localSharedPreferences), true);
                return (View)localObject;
              }
              paramViewGroup = (ViewGroup)localObject;
            } while (paramInt != ThemingDrawerActivity.this.menuDividerColorRow);
            paramView.setTextAndColor(LocaleController.getString("ThemingDividerColor", 2131166709), ThemingDrawerActivity.this.getMenuDividerColor(localSharedPreferences), true);
            return (View)localObject;
            if (i != 3) {
              break;
            }
            localObject = paramView;
            if (paramView == null) {
              localObject = new TextSettingsCell(this.mContext);
            }
            paramView = (TextSettingsCell)localObject;
            localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
            if (paramInt == ThemingDrawerActivity.this.headerGradientRow)
            {
              paramView.setTextAndValue(LocaleController.getString("ThemingGradient", 2131166736), MihanTheme.getGradientString(localSharedPreferences, "theme_drawer_header_gradient"), false);
              return (View)localObject;
            }
            if (paramInt == ThemingDrawerActivity.this.menuGradientRow)
            {
              paramView.setTextAndValue(LocaleController.getString("ThemingGradient", 2131166736), MihanTheme.getGradientString(localSharedPreferences, "theme_drawer_menu_gradient"), false);
              return (View)localObject;
            }
            paramViewGroup = (ViewGroup)localObject;
          } while (paramInt != ThemingDrawerActivity.this.avatarRadiusRow);
          paramView.setTextAndValue(LocaleController.getString("ThemingAvatarRadius", 2131166684), String.valueOf(ThemingDrawerActivity.this.getHeaderAvatarRadius(localSharedPreferences)), true);
          return (View)localObject;
          paramViewGroup = paramView;
        } while (i != 4);
        localObject = paramView;
        if (paramView == null) {
          localObject = new TextCheckCell(this.mContext);
        }
        paramView = (TextCheckCell)localObject;
        localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
        if (paramInt == ThemingDrawerActivity.this.centerUserInfoRow)
        {
          paramView.setTextAndCheck(LocaleController.getString("ThemingDrawerCenterInfo", 2131166710), localSharedPreferences.getBoolean("theme_drawer_center_info", false), false);
          return (View)localObject;
        }
        if (paramInt == ThemingDrawerActivity.this.hideCustomBGRow)
        {
          paramView.setTextAndCheck(LocaleController.getString("ThemingDrawerHideCustomBG", 2131166712), localSharedPreferences.getBoolean("theme_drawer_hide_cbg", false), true);
          return (View)localObject;
        }
        paramViewGroup = (ViewGroup)localObject;
      } while (paramInt != ThemingDrawerActivity.this.hideCustomBGShadowRow);
      paramView.setTextAndCheck(LocaleController.getString("ThemingDrawerHideCustomBGShadow", 2131166713), localSharedPreferences.getBoolean("theme_drawer_hide_cbgs", false), true);
      return (View)localObject;
    }
    
    public int getViewTypeCount()
    {
      return 5;
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
      if ((paramInt == ThemingDrawerActivity.this.headerGradientColorRow) && (ThemingDrawerActivity.this.getHeaderGradientFlag(localSharedPreferences) == 0)) {}
      while (((paramInt == ThemingDrawerActivity.this.menuGradientColorRow) && (ThemingDrawerActivity.this.getMenuGradientFlag(localSharedPreferences) == 0)) || ((paramInt == ThemingDrawerActivity.this.hideCustomBGShadowRow) && (localSharedPreferences.getBoolean("theme_drawer_hide_cbg", false))) || ((paramInt != ThemingDrawerActivity.this.headerColorRow) && (paramInt != ThemingDrawerActivity.this.headerGradientRow) && (paramInt != ThemingDrawerActivity.this.headerGradientColorRow) && (paramInt != ThemingDrawerActivity.this.menuColorRow) && (paramInt != ThemingDrawerActivity.this.menuGradientRow) && (paramInt != ThemingDrawerActivity.this.menuGradientColorRow) && (paramInt != ThemingDrawerActivity.this.nameColorRow) && (paramInt != ThemingDrawerActivity.this.phoneColorRow) && (paramInt != ThemingDrawerActivity.this.centerUserInfoRow) && (paramInt != ThemingDrawerActivity.this.menuTextColorRow) && (paramInt != ThemingDrawerActivity.this.menuIconColorRow) && (paramInt != ThemingDrawerActivity.this.menuDividerColorRow) && (paramInt != ThemingDrawerActivity.this.avatarRadiusRow) && (paramInt != ThemingDrawerActivity.this.hideCustomBGRow) && (paramInt != ThemingDrawerActivity.this.hideCustomBGShadowRow))) {
        return false;
      }
      return true;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\Theming\ThemingDrawerActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */