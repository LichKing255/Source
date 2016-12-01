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
import android.widget.ListView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Supergram.TextDescriptionCell;
import org.telegram.ui.Supergram.Theming.ColorPicker.ColorSelectorDialog;
import org.telegram.ui.Supergram.Theming.ColorPicker.ColorSelectorDialog.OnColorChangedListener;

public class ThemingSettingsActivity
  extends BaseFragment
{
  private int actionColorRow;
  private int actionGradientColorRow;
  private int actionGradientRow;
  private int actionIconColorRow;
  private int actionSectionRow2;
  private int actionSubTitleColorRow;
  private int actionTitleColorRow;
  private int avatarRadiusColorRow;
  private int desTextColorRow;
  private int dividerColorRow;
  private ListAdapter listAdapter;
  private ListView listView;
  private int optionListBGRow;
  private int optionListRow;
  private int optionListRow2;
  private int rowCount = 0;
  private int sectionColorRow;
  private int shadowColorRow;
  private int testDesTextColorRow;
  private int titleColorRow;
  private int valueColorRow;
  
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
          if (ThemingSettingsActivity.this.listView != null) {
            ThemingSettingsActivity.this.listView.invalidateViews();
          }
        }
      }, i, 0, 0, true).show();
      return;
      if (!paramString.equals("theme_setting_action_color")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("theme_setting_action_gcolor")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("theme_setting_action_icolor")) {
        break;
      }
      i = 2;
      break;
      if (!paramString.equals("theme_setting_action_tcolor")) {
        break;
      }
      i = 3;
      break;
      if (!paramString.equals("theme_setting_action_stcolor")) {
        break;
      }
      i = 4;
      break;
      i = paramSharedPreferences.getInt(paramString, getActionBarColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getActionBarGradientcolor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getActionBarIconColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getActionBarTitleColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getActionBarSubTitleColor(paramSharedPreferences));
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
        if (ThemingSettingsActivity.this.listView != null) {
          ThemingSettingsActivity.this.listView.invalidateViews();
        }
      }
    });
    showDialog(localBuilder.create());
  }
  
  private void selectlistColor(View paramView, final SharedPreferences paramSharedPreferences, final String paramString)
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
          if (ThemingSettingsActivity.this.listView != null) {
            ThemingSettingsActivity.this.listView.invalidateViews();
          }
        }
      }, i, 0, 0, true).show();
      return;
      if (!paramString.equals("theme_setting_list_bgcolor")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("theme_setting_shadow_color")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("theme_setting_section_color")) {
        break;
      }
      i = 2;
      break;
      if (!paramString.equals("theme_setting_option_tcolor")) {
        break;
      }
      i = 3;
      break;
      if (!paramString.equals("theme_setting_option_vcolor")) {
        break;
      }
      i = 4;
      break;
      if (!paramString.equals("theme_setting_option_descolor")) {
        break;
      }
      i = 5;
      break;
      if (!paramString.equals("theme_setting_option_divcolor")) {
        break;
      }
      i = 6;
      break;
      i = paramSharedPreferences.getInt(paramString, getOptionListBGColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getShadowColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getSectionColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getOptionTitleColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getOptionValueColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getOptionDesColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getOptionDivColor(paramSharedPreferences));
    }
  }
  
  private void updateColors()
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
      return;
      this.actionBar.setBackgroundColor(i);
    }
  }
  
  public View createView(final Context paramContext)
  {
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("ThemingSettingsScreen", 2131166784));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          ThemingSettingsActivity.this.finishFragment();
        }
      }
    });
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
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, final View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        if (ThemingSettingsActivity.this.getParentActivity() == null) {}
        do
        {
          return;
          if (paramAnonymousInt == ThemingSettingsActivity.this.actionColorRow)
          {
            ThemingSettingsActivity.this.selectColor(paramAnonymousView, paramContext, "theme_setting_action_color");
            return;
          }
          if (paramAnonymousInt == ThemingSettingsActivity.this.actionGradientRow)
          {
            ThemingSettingsActivity.this.selectGradient(paramContext, "theme_setting_action_gradient");
            return;
          }
          if (paramAnonymousInt == ThemingSettingsActivity.this.actionGradientColorRow)
          {
            ThemingSettingsActivity.this.selectColor(paramAnonymousView, paramContext, "theme_setting_action_gcolor");
            return;
          }
          if (paramAnonymousInt == ThemingSettingsActivity.this.actionIconColorRow)
          {
            ThemingSettingsActivity.this.selectColor(paramAnonymousView, paramContext, "theme_setting_action_icolor");
            return;
          }
          if (paramAnonymousInt == ThemingSettingsActivity.this.actionTitleColorRow)
          {
            ThemingSettingsActivity.this.selectColor(paramAnonymousView, paramContext, "theme_setting_action_tcolor");
            return;
          }
          if (paramAnonymousInt == ThemingSettingsActivity.this.actionSubTitleColorRow)
          {
            ThemingSettingsActivity.this.selectColor(paramAnonymousView, paramContext, "theme_setting_action_stcolor");
            return;
          }
          if (paramAnonymousInt == ThemingSettingsActivity.this.avatarRadiusColorRow)
          {
            paramAnonymousAdapterView = new AlertDialog.Builder(ThemingSettingsActivity.this.getParentActivity());
            paramAnonymousAdapterView.setTitle(LocaleController.getString("AppName", 2131165338));
            paramAnonymousView = new NumberPicker(ThemingSettingsActivity.this.getParentActivity());
            paramAnonymousView.setMinValue(1);
            paramAnonymousView.setMaxValue(21);
            paramAnonymousView.setValue(ThemingSettingsActivity.this.getActionAvatarRadius(paramContext));
            paramAnonymousAdapterView.setView(paramAnonymousView);
            paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Done", 2131165634), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = ThemingSettingsActivity.2.this.val$preferences.edit();
                paramAnonymous2DialogInterface.putInt("theme_setting_action_aradius", paramAnonymousView.getValue());
                paramAnonymous2DialogInterface.commit();
                if (ThemingSettingsActivity.this.listView != null) {
                  ThemingSettingsActivity.this.listView.invalidateViews();
                }
              }
            });
            ThemingSettingsActivity.this.showDialog(paramAnonymousAdapterView.create());
            return;
          }
          if (paramAnonymousInt == ThemingSettingsActivity.this.optionListBGRow)
          {
            ThemingSettingsActivity.this.selectlistColor(paramAnonymousView, paramContext, "theme_setting_list_bgcolor");
            return;
          }
          if (paramAnonymousInt == ThemingSettingsActivity.this.shadowColorRow)
          {
            ThemingSettingsActivity.this.selectlistColor(paramAnonymousView, paramContext, "theme_setting_shadow_color");
            return;
          }
          if (paramAnonymousInt == ThemingSettingsActivity.this.sectionColorRow)
          {
            ThemingSettingsActivity.this.selectlistColor(paramAnonymousView, paramContext, "theme_setting_section_color");
            return;
          }
          if (paramAnonymousInt == ThemingSettingsActivity.this.titleColorRow)
          {
            ThemingSettingsActivity.this.selectlistColor(paramAnonymousView, paramContext, "theme_setting_option_tcolor");
            return;
          }
          if (paramAnonymousInt == ThemingSettingsActivity.this.valueColorRow)
          {
            ThemingSettingsActivity.this.selectlistColor(paramAnonymousView, paramContext, "theme_setting_option_vcolor");
            return;
          }
          if (paramAnonymousInt == ThemingSettingsActivity.this.desTextColorRow)
          {
            ThemingSettingsActivity.this.selectlistColor(paramAnonymousView, paramContext, "theme_setting_option_descolor");
            return;
          }
        } while (paramAnonymousInt != ThemingSettingsActivity.this.dividerColorRow);
        ThemingSettingsActivity.this.selectlistColor(paramAnonymousView, paramContext, "theme_setting_option_divcolor");
      }
    });
    this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
    {
      public boolean onItemLongClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        if (ThemingSettingsActivity.this.getParentActivity() == null) {
          return false;
        }
        if (paramAnonymousInt == ThemingSettingsActivity.this.actionColorRow) {
          MihanTheme.resetPreference("theme_setting_action_color", ThemingSettingsActivity.this.listView);
        }
        for (;;)
        {
          return true;
          if (paramAnonymousInt == ThemingSettingsActivity.this.actionGradientRow) {
            MihanTheme.resetPreference("theme_setting_action_gradient", ThemingSettingsActivity.this.listView);
          } else if (paramAnonymousInt == ThemingSettingsActivity.this.actionGradientColorRow) {
            MihanTheme.resetPreference("theme_setting_action_gcolor", ThemingSettingsActivity.this.listView);
          } else if (paramAnonymousInt == ThemingSettingsActivity.this.actionIconColorRow) {
            MihanTheme.resetPreference("theme_setting_action_icolor", ThemingSettingsActivity.this.listView);
          } else if (paramAnonymousInt == ThemingSettingsActivity.this.actionTitleColorRow) {
            MihanTheme.resetPreference("theme_setting_action_tcolor", ThemingSettingsActivity.this.listView);
          } else if (paramAnonymousInt == ThemingSettingsActivity.this.actionSubTitleColorRow) {
            MihanTheme.resetPreference("theme_setting_action_stcolor", ThemingSettingsActivity.this.listView);
          } else if (paramAnonymousInt == ThemingSettingsActivity.this.avatarRadiusColorRow) {
            MihanTheme.resetPreference("theme_setting_action_aradius", ThemingSettingsActivity.this.listView);
          } else if (paramAnonymousInt == ThemingSettingsActivity.this.optionListBGRow) {
            MihanTheme.resetPreference("theme_setting_list_bgcolor", ThemingSettingsActivity.this.listView);
          } else if (paramAnonymousInt == ThemingSettingsActivity.this.shadowColorRow) {
            MihanTheme.resetPreference("theme_setting_shadow_color", ThemingSettingsActivity.this.listView);
          } else if (paramAnonymousInt == ThemingSettingsActivity.this.sectionColorRow) {
            MihanTheme.resetPreference("theme_setting_section_color", ThemingSettingsActivity.this.listView);
          } else if (paramAnonymousInt == ThemingSettingsActivity.this.titleColorRow) {
            MihanTheme.resetPreference("theme_setting_option_tcolor", ThemingSettingsActivity.this.listView);
          } else if (paramAnonymousInt == ThemingSettingsActivity.this.valueColorRow) {
            MihanTheme.resetPreference("theme_setting_option_vcolor", ThemingSettingsActivity.this.listView);
          } else if (paramAnonymousInt == ThemingSettingsActivity.this.desTextColorRow) {
            MihanTheme.resetPreference("theme_setting_option_descolor", ThemingSettingsActivity.this.listView);
          } else if (paramAnonymousInt == ThemingSettingsActivity.this.dividerColorRow) {
            MihanTheme.resetPreference("theme_setting_option_divcolor", ThemingSettingsActivity.this.listView);
          }
        }
      }
    });
    return this.fragmentView;
  }
  
  public int getActionAvatarRadius(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_setting_action_aradius", 21);
  }
  
  public int getActionBarColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_setting_action_color", MihanTheme.getThemeColor(paramSharedPreferences));
  }
  
  public int getActionBarGradientFlag(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_setting_action_gradient", 0);
  }
  
  public int getActionBarGradientcolor(SharedPreferences paramSharedPreferences)
  {
    if (getActionBarGradientFlag(paramSharedPreferences) == 0) {
      return 0;
    }
    return paramSharedPreferences.getInt("theme_setting_action_gcolor", getActionBarColor(paramSharedPreferences));
  }
  
  public int getActionBarIconColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_setting_action_icolor", -1);
  }
  
  public int getActionBarSubTitleColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_setting_action_stcolor", MihanTheme.getLighterColor(getActionBarTitleColor(paramSharedPreferences), 0.8F));
  }
  
  public int getActionBarTitleColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_setting_action_tcolor", getActionBarIconColor(paramSharedPreferences));
  }
  
  public int getOptionDesColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_setting_option_descolor", -6052957);
  }
  
  public int getOptionDivColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_setting_option_divcolor", -2500135);
  }
  
  public int getOptionListBGColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_setting_list_bgcolor", -1);
  }
  
  public int getOptionTitleColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_setting_option_tcolor", -14606047);
  }
  
  public int getOptionValueColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_setting_option_vcolor", MihanTheme.getLighterColor(getSectionColor(paramSharedPreferences), 0.5F));
  }
  
  public int getSectionColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_setting_section_color", MihanTheme.getThemeColor());
  }
  
  public int getShadowColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_setting_shadow_color", -1052689);
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
    this.avatarRadiusColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.optionListRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.optionListRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.optionListBGRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.shadowColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.sectionColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.titleColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.valueColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.desTextColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.testDesTextColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.dividerColorRow = i;
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
    updateColors();
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
      return ThemingSettingsActivity.this.rowCount;
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
      if (paramInt == ThemingSettingsActivity.this.optionListRow) {}
      do
      {
        return 0;
        if ((paramInt == ThemingSettingsActivity.this.actionSectionRow2) || (paramInt == ThemingSettingsActivity.this.optionListRow2)) {
          return 1;
        }
        if ((paramInt == ThemingSettingsActivity.this.actionColorRow) || (paramInt == ThemingSettingsActivity.this.actionGradientColorRow) || (paramInt == ThemingSettingsActivity.this.actionIconColorRow) || (paramInt == ThemingSettingsActivity.this.actionTitleColorRow) || (paramInt == ThemingSettingsActivity.this.actionSubTitleColorRow) || (paramInt == ThemingSettingsActivity.this.optionListBGRow) || (paramInt == ThemingSettingsActivity.this.shadowColorRow) || (paramInt == ThemingSettingsActivity.this.sectionColorRow) || (paramInt == ThemingSettingsActivity.this.titleColorRow) || (paramInt == ThemingSettingsActivity.this.valueColorRow) || (paramInt == ThemingSettingsActivity.this.desTextColorRow) || (paramInt == ThemingSettingsActivity.this.dividerColorRow)) {
          return 2;
        }
        if ((paramInt == ThemingSettingsActivity.this.actionGradientRow) || (paramInt == ThemingSettingsActivity.this.avatarRadiusColorRow)) {
          return 3;
        }
      } while (paramInt != ThemingSettingsActivity.this.testDesTextColorRow);
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
        ((ShadowSectionCell)paramViewGroup).setSize(12);
      }
      Object localObject;
      do
      {
        do
        {
          SharedPreferences localSharedPreferences;
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
                if (paramInt == ThemingSettingsActivity.this.actionSectionRow2)
                {
                  ((HeaderCell)localObject).setText(LocaleController.getString("ThemingHeader", 2131166741));
                  return (View)localObject;
                }
                paramViewGroup = (ViewGroup)localObject;
              } while (paramInt != ThemingSettingsActivity.this.optionListRow2);
              ((HeaderCell)localObject).setText(LocaleController.getString("ThemingSettingOptionList", 2131166777));
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
              if (paramInt == ThemingSettingsActivity.this.actionColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingColor", 2131166694), ThemingSettingsActivity.this.getActionBarColor(localSharedPreferences), false);
                return (View)localObject;
              }
              if (paramInt == ThemingSettingsActivity.this.actionGradientColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingGradientColor", 2131166737), ThemingSettingsActivity.this.getActionBarGradientcolor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingSettingsActivity.this.actionIconColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingActionIconColor", 2131166683), ThemingSettingsActivity.this.getActionBarIconColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingSettingsActivity.this.actionTitleColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingTitleColor", 2131166805), ThemingSettingsActivity.this.getActionBarTitleColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingSettingsActivity.this.actionSubTitleColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingSubTitleColor", 2131166785), ThemingSettingsActivity.this.getActionBarSubTitleColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingSettingsActivity.this.optionListBGRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingColor", 2131166694), ThemingSettingsActivity.this.getOptionListBGColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingSettingsActivity.this.shadowColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingSettingShadowColor", 2131166780), ThemingSettingsActivity.this.getShadowColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingSettingsActivity.this.sectionColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingSettingSectionColor", 2131166779), ThemingSettingsActivity.this.getSectionColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingSettingsActivity.this.titleColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingSettingTitleColor", 2131166782), ThemingSettingsActivity.this.getOptionTitleColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingSettingsActivity.this.valueColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingSettingValueColor", 2131166783), ThemingSettingsActivity.this.getOptionValueColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingSettingsActivity.this.desTextColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingSettingDescriptionColor", 2131166775), ThemingSettingsActivity.this.getOptionDesColor(localSharedPreferences), false);
                return (View)localObject;
              }
              paramViewGroup = (ViewGroup)localObject;
            } while (paramInt != ThemingSettingsActivity.this.dividerColorRow);
            paramView.setTextAndColor(LocaleController.getString("ThemingSettingDividerColor", 2131166776), ThemingSettingsActivity.this.getOptionDivColor(localSharedPreferences), true);
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
            if (paramInt == ThemingSettingsActivity.this.actionGradientRow)
            {
              paramView.setTextAndValue(LocaleController.getString("ThemingGradient", 2131166736), MihanTheme.getGradientString(localSharedPreferences, "theme_setting_action_gradient"), false);
              return (View)localObject;
            }
            paramViewGroup = (ViewGroup)localObject;
          } while (paramInt != ThemingSettingsActivity.this.avatarRadiusColorRow);
          paramView.setTextAndValue(LocaleController.getString("ThemingAvatarRadius", 2131166684), String.valueOf(ThemingSettingsActivity.this.getActionAvatarRadius(localSharedPreferences)), false);
          return (View)localObject;
          paramViewGroup = paramView;
        } while (i != 4);
        localObject = paramView;
        if (paramView == null) {
          localObject = new TextDescriptionCell(this.mContext);
        }
        paramViewGroup = (ViewGroup)localObject;
      } while (paramInt != ThemingSettingsActivity.this.testDesTextColorRow);
      ((TextDescriptionCell)localObject).setText(LocaleController.getString("ThemingSettingTextDesColor", 2131166781), true);
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
      if ((paramInt == ThemingSettingsActivity.this.actionGradientColorRow) && (ThemingSettingsActivity.this.getActionBarGradientFlag(localSharedPreferences) == 0)) {}
      while ((paramInt != ThemingSettingsActivity.this.actionColorRow) && (paramInt != ThemingSettingsActivity.this.actionGradientRow) && (paramInt != ThemingSettingsActivity.this.actionGradientColorRow) && (paramInt != ThemingSettingsActivity.this.actionIconColorRow) && (paramInt != ThemingSettingsActivity.this.actionTitleColorRow) && (paramInt != ThemingSettingsActivity.this.actionSubTitleColorRow) && (paramInt != ThemingSettingsActivity.this.avatarRadiusColorRow) && (paramInt != ThemingSettingsActivity.this.optionListBGRow) && (paramInt != ThemingSettingsActivity.this.shadowColorRow) && (paramInt != ThemingSettingsActivity.this.sectionColorRow) && (paramInt != ThemingSettingsActivity.this.titleColorRow) && (paramInt != ThemingSettingsActivity.this.valueColorRow) && (paramInt != ThemingSettingsActivity.this.desTextColorRow) && (paramInt != ThemingSettingsActivity.this.dividerColorRow)) {
        return false;
      }
      return true;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\Theming\ThemingSettingsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */