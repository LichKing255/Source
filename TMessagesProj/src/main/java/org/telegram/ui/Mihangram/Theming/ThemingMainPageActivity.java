package org.telegram.ui.Mihangram.Theming;

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
import org.telegram.messenger.NotificationCenter;
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
import org.telegram.ui.Mihangram.Theming.ColorPicker.ColorSelectorDialog;
import org.telegram.ui.Mihangram.Theming.ColorPicker.ColorSelectorDialog.OnColorChangedListener;

public class ThemingMainPageActivity
  extends BaseFragment
{
  private int actionColorRow;
  private int actionGradientColorRow;
  private int actionGradientRow;
  private int actionIconColorRow;
  private int actionSectionRow2;
  private int actionTitleColorRow;
  private int avatarRadiusRow;
  private int countColorRow;
  private int countTextColorRow;
  private int dateColorRow;
  private int dividerColorRow;
  private int fileColorRow;
  private int floatButtonColorRow;
  private int floatButtonIconColorRow;
  private int floatButtonSectionRow;
  private int floatButtonSectionRow2;
  private ListAdapter listAdapter;
  private int listColorRow;
  private int listGradientColorRow;
  private int listGradientRow;
  private int listSectionRow;
  private int listSectionRow2;
  private ListView listView;
  private int messageColorRow;
  private int moveTabsRow;
  private int muteCountColorRow;
  private int nameColorRow;
  private int rowCount = 0;
  private int secretNameColorRow;
  private int tabsColorRow;
  private int tabsCounterColorRow;
  private int tabsCounterTextColorRow;
  private int tabsGradientColorRow;
  private int tabsGradientRow;
  private int tabsHeightRow;
  private int tabsIconColorRow;
  private int tabsMuteCounterColorRow;
  private int tabsMuteCounterTextColorRow;
  private int tabsSIconColorRow;
  private int tabsSectionRow;
  private int tabsSectionRow2;
  private int tikColorRow;
  private int toolBarBGColorRow;
  private int toolBarGradienColorRow;
  private int toolBarGradientRow;
  private int toolBarIconColorRow;
  private int toolBarSectionRow;
  private int toolBarSectionRow2;
  
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
          if (ThemingMainPageActivity.this.listView != null) {
            ThemingMainPageActivity.this.listView.invalidateViews();
          }
        }
      }, i, 0, 0, true).show();
      return;
      if (!paramString.equals("theme_action_color")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("theme_action_gradient_color")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("theme_tabs_color")) {
        break;
      }
      i = 2;
      break;
      if (!paramString.equals("theme_tabs_gradient_color")) {
        break;
      }
      i = 3;
      break;
      if (!paramString.equals("theme_tabs_counter_color")) {
        break;
      }
      i = 4;
      break;
      if (!paramString.equals("theme_tabs_counter_tcolor")) {
        break;
      }
      i = 5;
      break;
      if (!paramString.equals("theme_tabs_mcounter_color")) {
        break;
      }
      i = 6;
      break;
      if (!paramString.equals("theme_tabs_mcounter_tcolor")) {
        break;
      }
      i = 7;
      break;
      if (!paramString.equals("theme_list_color")) {
        break;
      }
      i = 8;
      break;
      if (!paramString.equals("theme_list_gradient_color")) {
        break;
      }
      i = 9;
      break;
      if (!paramString.equals("theme_float_color")) {
        break;
      }
      i = 10;
      break;
      if (!paramString.equals("theme_action_title_color")) {
        break;
      }
      i = 11;
      break;
      if (!paramString.equals("theme_toolbar_color")) {
        break;
      }
      i = 12;
      break;
      if (!paramString.equals("theme_toolbar_gradient_color")) {
        break;
      }
      i = 13;
      break;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getActionBarColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getActionBarGradientColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getTabsBackgroundColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getTabsGradientColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getTabsCounterBGColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getTabsCounterTextColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getTabsMCounterBGColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getTabsMCounterTextColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getListViewColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getListViewGradientColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getFloatingButtonColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getActionBarTitleColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getToolBarBGColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getToolBarGradientColor(paramSharedPreferences));
    }
  }
  
  private void selectDialogDetailsColor(View paramView, final SharedPreferences paramSharedPreferences, final String paramString)
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
          if (ThemingMainPageActivity.this.listView != null) {
            ThemingMainPageActivity.this.listView.invalidateViews();
          }
        }
      }, i, 0, 0, true).show();
      return;
      if (!paramString.equals("theme_dialog_divider_color")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("theme_dialog_name_color")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("theme_dialog_sname_color")) {
        break;
      }
      i = 2;
      break;
      if (!paramString.equals("theme_dialog_date_color")) {
        break;
      }
      i = 3;
      break;
      if (!paramString.equals("theme_dialog_message_color")) {
        break;
      }
      i = 4;
      break;
      if (!paramString.equals("theme_dialog_tik_color")) {
        break;
      }
      i = 5;
      break;
      if (!paramString.equals("theme_dialog_count_color")) {
        break;
      }
      i = 6;
      break;
      if (!paramString.equals("theme_dialog_count_text_color")) {
        break;
      }
      i = 7;
      break;
      if (!paramString.equals("theme_dialog_mcount_color")) {
        break;
      }
      i = 8;
      break;
      if (!paramString.equals("theme_dialog_mcount_tcolor")) {
        break;
      }
      i = 9;
      break;
      if (!paramString.equals("theme_dialog_file_color")) {
        break;
      }
      i = 10;
      break;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getDialogDividerColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getDialogNameColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getDialogSNameColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getDialogDateColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getDialogMessageColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getDialogTikColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getDialogCountColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getDialogCountTextColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getDialogMuteCountColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getDialogMCountTextColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getDialogFileColor(paramSharedPreferences));
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
        if (ThemingMainPageActivity.this.listView != null) {
          ThemingMainPageActivity.this.listView.invalidateViews();
        }
      }
    });
    showDialog(localBuilder.create());
  }
  
  private void selectIconColor(View paramView, final SharedPreferences paramSharedPreferences, final String paramString)
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
          if (ThemingMainPageActivity.this.listView != null) {
            ThemingMainPageActivity.this.listView.invalidateViews();
          }
        }
      }, i, 0, 0, true).show();
      return;
      if (!paramString.equals("theme_action_icon_color")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("theme_tabss_icon_color")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("theme_tabs_icon_color")) {
        break;
      }
      i = 2;
      break;
      if (!paramString.equals("theme_float_icon_color")) {
        break;
      }
      i = 3;
      break;
      if (!paramString.equals("theme_toolbar_icon_color")) {
        break;
      }
      i = 4;
      break;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getActionBarIconColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getSelectedTabIconColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getTabsIconColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getFloatingButtonIconColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, MihanTheme.getToolBarIconColor(paramSharedPreferences));
    }
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
    this.actionBar.setTitle(LocaleController.getString("ThemingMainScreen", 2131166753));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1)
        {
          ThemingMainPageActivity.this.finishFragment();
          return;
        }
        ThemingMainPageActivity.this.presentFragment(new DialogsActivity(null));
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
        if (ThemingMainPageActivity.this.getParentActivity() == null) {}
        label272:
        do
        {
          return;
          if (paramAnonymousInt == ThemingMainPageActivity.this.actionColorRow)
          {
            ThemingMainPageActivity.this.selectColor(paramAnonymousView, paramContext, "theme_action_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.actionGradientRow)
          {
            ThemingMainPageActivity.this.selectGradient(paramContext, "theme_action_gradient");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.actionGradientColorRow)
          {
            ThemingMainPageActivity.this.selectColor(paramAnonymousView, paramContext, "theme_action_gradient_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.actionIconColorRow)
          {
            ThemingMainPageActivity.this.selectIconColor(paramAnonymousView, paramContext, "theme_action_icon_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.actionTitleColorRow)
          {
            ThemingMainPageActivity.this.selectColor(paramAnonymousView, paramContext, "theme_action_title_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.moveTabsRow)
          {
            boolean bool2 = paramContext.getBoolean("move_tabs", false);
            paramAnonymousAdapterView = paramContext.edit();
            if (!bool2)
            {
              bool1 = true;
              paramAnonymousAdapterView.putBoolean("move_tabs", bool1);
              paramAnonymousAdapterView.commit();
              if ((paramAnonymousView instanceof TextCheckCell))
              {
                paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                if (bool2) {
                  break label272;
                }
              }
            }
            for (boolean bool1 = true;; bool1 = false)
            {
              paramAnonymousAdapterView.setChecked(bool1);
              ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getBoolean("tabs", true);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.MihanUpdateInterface, new Object[] { Integer.valueOf(1) });
              return;
              bool1 = false;
              break;
            }
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.tabsHeightRow)
          {
            paramAnonymousAdapterView = new AlertDialog.Builder(ThemingMainPageActivity.this.getParentActivity());
            paramAnonymousAdapterView.setTitle(LocaleController.getString("AppName", 2131165338));
            paramAnonymousView = new NumberPicker(ThemingMainPageActivity.this.getParentActivity());
            paramAnonymousView.setMinValue(40);
            paramAnonymousView.setMaxValue(54);
            paramAnonymousView.setValue(MihanTheme.getTabsHeight(paramContext));
            paramAnonymousAdapterView.setView(paramAnonymousView);
            paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Done", 2131165634), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = ThemingMainPageActivity.2.this.val$preferences.edit();
                paramAnonymous2DialogInterface.putInt("theme_tabs_height", paramAnonymousView.getValue());
                paramAnonymous2DialogInterface.commit();
                if (ThemingMainPageActivity.this.listView != null) {
                  ThemingMainPageActivity.this.listView.invalidateViews();
                }
              }
            });
            ThemingMainPageActivity.this.showDialog(paramAnonymousAdapterView.create());
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.MihanUpdateInterface, new Object[] { Integer.valueOf(1) });
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.tabsColorRow)
          {
            ThemingMainPageActivity.this.selectColor(paramAnonymousView, paramContext, "theme_tabs_color");
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.MihanUpdateInterface, new Object[] { Integer.valueOf(1) });
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.tabsGradientRow)
          {
            ThemingMainPageActivity.this.selectGradient(paramContext, "theme_tabs_gradient");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.tabsGradientColorRow)
          {
            ThemingMainPageActivity.this.selectColor(paramAnonymousView, paramContext, "theme_tabs_gradient_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.tabsSIconColorRow)
          {
            ThemingMainPageActivity.this.selectIconColor(paramAnonymousView, paramContext, "theme_tabss_icon_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.tabsIconColorRow)
          {
            ThemingMainPageActivity.this.selectIconColor(paramAnonymousView, paramContext, "theme_tabs_icon_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.tabsCounterColorRow)
          {
            ThemingMainPageActivity.this.selectColor(paramAnonymousView, paramContext, "theme_tabs_counter_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.tabsMuteCounterColorRow)
          {
            ThemingMainPageActivity.this.selectColor(paramAnonymousView, paramContext, "theme_tabs_mcounter_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.tabsCounterTextColorRow)
          {
            ThemingMainPageActivity.this.selectColor(paramAnonymousView, paramContext, "theme_tabs_counter_tcolor");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.tabsMuteCounterTextColorRow)
          {
            ThemingMainPageActivity.this.selectColor(paramAnonymousView, paramContext, "theme_tabs_mcounter_tcolor");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.listColorRow)
          {
            ThemingMainPageActivity.this.selectColor(paramAnonymousView, paramContext, "theme_list_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.listGradientRow)
          {
            ThemingMainPageActivity.this.selectGradient(paramContext, "theme_list_gradient");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.listGradientColorRow)
          {
            ThemingMainPageActivity.this.selectColor(paramAnonymousView, paramContext, "theme_list_gradient_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.floatButtonColorRow)
          {
            ThemingMainPageActivity.this.selectColor(paramAnonymousView, paramContext, "theme_float_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.floatButtonIconColorRow)
          {
            ThemingMainPageActivity.this.selectIconColor(paramAnonymousView, paramContext, "theme_float_icon_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.dividerColorRow)
          {
            ThemingMainPageActivity.this.selectDialogDetailsColor(paramAnonymousView, paramContext, "theme_dialog_divider_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.nameColorRow)
          {
            ThemingMainPageActivity.this.selectDialogDetailsColor(paramAnonymousView, paramContext, "theme_dialog_name_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.secretNameColorRow)
          {
            ThemingMainPageActivity.this.selectDialogDetailsColor(paramAnonymousView, paramContext, "theme_dialog_sname_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.dateColorRow)
          {
            ThemingMainPageActivity.this.selectDialogDetailsColor(paramAnonymousView, paramContext, "theme_dialog_date_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.messageColorRow)
          {
            ThemingMainPageActivity.this.selectDialogDetailsColor(paramAnonymousView, paramContext, "theme_dialog_message_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.tikColorRow)
          {
            ThemingMainPageActivity.this.selectDialogDetailsColor(paramAnonymousView, paramContext, "theme_dialog_tik_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.countTextColorRow)
          {
            ThemingMainPageActivity.this.selectDialogDetailsColor(paramAnonymousView, paramContext, "theme_dialog_count_text_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.countColorRow)
          {
            ThemingMainPageActivity.this.selectDialogDetailsColor(paramAnonymousView, paramContext, "theme_dialog_count_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.muteCountColorRow)
          {
            ThemingMainPageActivity.this.selectDialogDetailsColor(paramAnonymousView, paramContext, "theme_dialog_mcount_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.fileColorRow)
          {
            ThemingMainPageActivity.this.selectDialogDetailsColor(paramAnonymousView, paramContext, "theme_dialog_file_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.avatarRadiusRow)
          {
            paramAnonymousAdapterView = new AlertDialog.Builder(ThemingMainPageActivity.this.getParentActivity());
            paramAnonymousAdapterView.setTitle(LocaleController.getString("AppName", 2131165338));
            paramAnonymousView = new NumberPicker(ThemingMainPageActivity.this.getParentActivity());
            paramAnonymousView.setMinValue(1);
            paramAnonymousView.setMaxValue(26);
            paramAnonymousView.setValue(MihanTheme.getAvatarRadius(paramContext));
            paramAnonymousAdapterView.setView(paramAnonymousView);
            paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Done", 2131165634), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = ThemingMainPageActivity.2.this.val$preferences.edit();
                paramAnonymous2DialogInterface.putInt("theme_avatar_radius", paramAnonymousView.getValue());
                paramAnonymous2DialogInterface.commit();
                if (ThemingMainPageActivity.this.listView != null) {
                  ThemingMainPageActivity.this.listView.invalidateViews();
                }
              }
            });
            ThemingMainPageActivity.this.showDialog(paramAnonymousAdapterView.create());
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.toolBarBGColorRow)
          {
            ThemingMainPageActivity.this.selectColor(paramAnonymousView, paramContext, "theme_toolbar_color");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.toolBarGradientRow)
          {
            ThemingMainPageActivity.this.selectGradient(paramContext, "theme_toolbar_gradient");
            return;
          }
          if (paramAnonymousInt == ThemingMainPageActivity.this.toolBarGradienColorRow)
          {
            ThemingMainPageActivity.this.selectColor(paramAnonymousView, paramContext, "theme_toolbar_gradient_color");
            return;
          }
        } while (paramAnonymousInt != ThemingMainPageActivity.this.toolBarIconColorRow);
        ThemingMainPageActivity.this.selectIconColor(paramAnonymousView, paramContext, "theme_toolbar_icon_color");
      }
    });
    this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
    {
      public boolean onItemLongClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        if (ThemingMainPageActivity.this.getParentActivity() == null) {
          return false;
        }
        if (paramAnonymousInt == ThemingMainPageActivity.this.actionColorRow) {
          MihanTheme.resetPreference("theme_action_color", ThemingMainPageActivity.this.listView);
        }
        for (;;)
        {
          return true;
          if (paramAnonymousInt == ThemingMainPageActivity.this.actionGradientRow) {
            MihanTheme.resetPreference("theme_action_gradient", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.actionGradientColorRow) {
            MihanTheme.resetPreference("theme_action_gradient_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.actionIconColorRow) {
            MihanTheme.resetPreference("theme_action_icon_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.actionTitleColorRow) {
            MihanTheme.resetPreference("theme_action_title_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.moveTabsRow) {
            MihanTheme.resetPreference("move_tabs", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.tabsHeightRow) {
            MihanTheme.resetPreference("theme_tabs_height", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.tabsColorRow) {
            MihanTheme.resetPreference("theme_tabs_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.tabsGradientRow) {
            MihanTheme.resetPreference("theme_tabs_gradient", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.tabsGradientColorRow) {
            MihanTheme.resetPreference("theme_tabs_gradient_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.tabsSIconColorRow) {
            MihanTheme.resetPreference("theme_tabss_icon_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.tabsIconColorRow) {
            MihanTheme.resetPreference("theme_tabs_icon_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.tabsCounterColorRow) {
            MihanTheme.resetPreference("theme_tabs_counter_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.tabsMuteCounterColorRow) {
            MihanTheme.resetPreference("theme_tabs_mcounter_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.tabsCounterTextColorRow) {
            MihanTheme.resetPreference("theme_tabs_counter_tcolor", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.tabsMuteCounterTextColorRow) {
            MihanTheme.resetPreference("theme_tabs_mcounter_tcolor", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.listColorRow) {
            MihanTheme.resetPreference("theme_list_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.listGradientRow) {
            MihanTheme.resetPreference("theme_list_gradient", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.listGradientColorRow) {
            MihanTheme.resetPreference("theme_list_gradient_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.floatButtonColorRow) {
            MihanTheme.resetPreference("theme_float_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.floatButtonIconColorRow) {
            MihanTheme.resetPreference("theme_float_icon_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.dividerColorRow) {
            MihanTheme.resetPreference("theme_dialog_divider_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.nameColorRow) {
            MihanTheme.resetPreference("theme_dialog_name_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.secretNameColorRow) {
            MihanTheme.resetPreference("theme_dialog_sname_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.dateColorRow) {
            MihanTheme.resetPreference("theme_dialog_date_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.messageColorRow) {
            MihanTheme.resetPreference("theme_dialog_message_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.tikColorRow) {
            MihanTheme.resetPreference("theme_dialog_tik_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.countTextColorRow) {
            MihanTheme.resetPreference("theme_dialog_count_text_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.countColorRow) {
            MihanTheme.resetPreference("theme_dialog_count_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.muteCountColorRow) {
            MihanTheme.resetPreference("theme_dialog_mcount_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.fileColorRow) {
            MihanTheme.resetPreference("theme_dialog_file_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.avatarRadiusRow) {
            MihanTheme.resetPreference("theme_avatar_radius", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.toolBarBGColorRow) {
            MihanTheme.resetPreference("theme_toolbar_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.toolBarGradientRow) {
            MihanTheme.resetPreference("theme_toolbar_gradient", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.toolBarGradienColorRow) {
            MihanTheme.resetPreference("theme_toolbar_gradient_color", ThemingMainPageActivity.this.listView);
          } else if (paramAnonymousInt == ThemingMainPageActivity.this.toolBarGradienColorRow) {
            MihanTheme.resetPreference("theme_toolbar_icon_color", ThemingMainPageActivity.this.listView);
          }
        }
      }
    });
    return this.fragmentView;
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
    this.tabsSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.tabsSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.moveTabsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.tabsHeightRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.tabsColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.tabsGradientRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.tabsGradientColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.tabsSIconColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.tabsIconColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.tabsCounterColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.tabsCounterTextColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.tabsMuteCounterColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.tabsMuteCounterTextColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.listSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.listSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.listColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.listGradientRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.listGradientColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.avatarRadiusRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.dividerColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.nameColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.secretNameColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.dateColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messageColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.tikColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.countColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.muteCountColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.countTextColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.fileColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.floatButtonSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.floatButtonSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.floatButtonColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.floatButtonIconColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.toolBarSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.toolBarSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.toolBarBGColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.toolBarGradientRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.toolBarGradienColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.toolBarIconColorRow = i;
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
      return ThemingMainPageActivity.this.rowCount;
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
      if ((paramInt == ThemingMainPageActivity.this.tabsSectionRow) || (paramInt == ThemingMainPageActivity.this.listSectionRow) || (paramInt == ThemingMainPageActivity.this.floatButtonSectionRow) || (paramInt == ThemingMainPageActivity.this.toolBarSectionRow)) {}
      do
      {
        return 0;
        if ((paramInt == ThemingMainPageActivity.this.actionSectionRow2) || (paramInt == ThemingMainPageActivity.this.tabsSectionRow2) || (paramInt == ThemingMainPageActivity.this.listSectionRow2) || (paramInt == ThemingMainPageActivity.this.floatButtonSectionRow2) || (paramInt == ThemingMainPageActivity.this.toolBarSectionRow2)) {
          return 1;
        }
        if ((paramInt == ThemingMainPageActivity.this.actionColorRow) || (paramInt == ThemingMainPageActivity.this.actionGradientColorRow) || (paramInt == ThemingMainPageActivity.this.tabsColorRow) || (paramInt == ThemingMainPageActivity.this.tabsColorRow) || (paramInt == ThemingMainPageActivity.this.tabsGradientColorRow) || (paramInt == ThemingMainPageActivity.this.listColorRow) || (paramInt == ThemingMainPageActivity.this.listGradientColorRow) || (paramInt == ThemingMainPageActivity.this.actionIconColorRow) || (paramInt == ThemingMainPageActivity.this.tabsIconColorRow) || (paramInt == ThemingMainPageActivity.this.tabsSIconColorRow) || (paramInt == ThemingMainPageActivity.this.floatButtonColorRow) || (paramInt == ThemingMainPageActivity.this.floatButtonIconColorRow) || (paramInt == ThemingMainPageActivity.this.dividerColorRow) || (paramInt == ThemingMainPageActivity.this.nameColorRow) || (paramInt == ThemingMainPageActivity.this.dateColorRow) || (paramInt == ThemingMainPageActivity.this.messageColorRow) || (paramInt == ThemingMainPageActivity.this.tikColorRow) || (paramInt == ThemingMainPageActivity.this.countColorRow) || (paramInt == ThemingMainPageActivity.this.muteCountColorRow) || (paramInt == ThemingMainPageActivity.this.countTextColorRow) || (paramInt == ThemingMainPageActivity.this.fileColorRow) || (paramInt == ThemingMainPageActivity.this.actionTitleColorRow) || (paramInt == ThemingMainPageActivity.this.tabsCounterColorRow) || (paramInt == ThemingMainPageActivity.this.tabsMuteCounterColorRow) || (paramInt == ThemingMainPageActivity.this.tabsMuteCounterTextColorRow) || (paramInt == ThemingMainPageActivity.this.tabsCounterTextColorRow) || (paramInt == ThemingMainPageActivity.this.toolBarBGColorRow) || (paramInt == ThemingMainPageActivity.this.toolBarGradienColorRow) || (paramInt == ThemingMainPageActivity.this.toolBarIconColorRow) || (paramInt == ThemingMainPageActivity.this.secretNameColorRow)) {
          return 2;
        }
        if ((paramInt == ThemingMainPageActivity.this.actionGradientRow) || (paramInt == ThemingMainPageActivity.this.tabsGradientRow) || (paramInt == ThemingMainPageActivity.this.listGradientRow) || (paramInt == ThemingMainPageActivity.this.avatarRadiusRow) || (paramInt == ThemingMainPageActivity.this.tabsHeightRow) || (paramInt == ThemingMainPageActivity.this.toolBarGradientRow)) {
          return 3;
        }
      } while (paramInt != ThemingMainPageActivity.this.moveTabsRow);
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
              if (paramInt == ThemingMainPageActivity.this.actionSectionRow2)
              {
                ((HeaderCell)localObject).setText(LocaleController.getString("ThemingHeader", 2131166741));
                return (View)localObject;
              }
              if (paramInt == ThemingMainPageActivity.this.tabsSectionRow2)
              {
                ((HeaderCell)localObject).setText(LocaleController.getString("ThemingTabs", 2131166789));
                return (View)localObject;
              }
              if (paramInt == ThemingMainPageActivity.this.listSectionRow2)
              {
                ((HeaderCell)localObject).setText(LocaleController.getString("ThemingList", 2131166750));
                return (View)localObject;
              }
              paramViewGroup = (ViewGroup)localObject;
            } while (paramInt != ThemingMainPageActivity.this.floatButtonSectionRow2);
            ((HeaderCell)localObject).setText(LocaleController.getString("ThemingFloatButton", 2131166731));
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
            if (paramInt == ThemingMainPageActivity.this.actionColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingColor", 2131166694), MihanTheme.getActionBarColor(localSharedPreferences), false);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.actionGradientColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingGradientColor", 2131166737), MihanTheme.getActionBarGradientColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.actionIconColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingActionIconColor", 2131166683), MihanTheme.getActionBarIconColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.actionTitleColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingTitleColor", 2131166805), MihanTheme.getActionBarTitleColor(localSharedPreferences), false);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.tabsColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingColor", 2131166694), MihanTheme.getTabsBackgroundColor(localSharedPreferences), false);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.tabsGradientColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingGradientColor", 2131166737), MihanTheme.getTabsGradientColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.listColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingColor", 2131166694), MihanTheme.getListViewColor(localSharedPreferences), false);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.listGradientColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingGradientColor", 2131166737), MihanTheme.getListViewGradientColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.tabsSIconColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingSTabIconColor", 2131166768), MihanTheme.getSelectedTabIconColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.tabsIconColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingTabsIconColor", 2131166793), MihanTheme.getTabsIconColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.tabsCounterColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingTabsCounterColor", 2131166790), MihanTheme.getTabsCounterBGColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.tabsCounterTextColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingTabsCounterTColor", 2131166791), MihanTheme.getTabsCounterTextColor(localSharedPreferences), false);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.tabsMuteCounterColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingTabsMCounterColor", 2131166794), MihanTheme.getTabsMCounterBGColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.tabsMuteCounterTextColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingTabsMCounterTColor", 2131166795), MihanTheme.getTabsMCounterTextColor(localSharedPreferences), false);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.floatButtonColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingFloatColor", 2131166732), MihanTheme.getFloatingButtonColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.floatButtonIconColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingFloatIconColor", 2131166733), MihanTheme.getFloatingButtonIconColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.dividerColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingDividerColor", 2131166709), MihanTheme.getDialogDividerColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.nameColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingDialogNameColor", 2131166707), MihanTheme.getDialogNameColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.secretNameColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingDialogSNameColor", 2131166708), MihanTheme.getDialogSNameColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.dateColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingDateColor", 2131166705), MihanTheme.getDialogDateColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.messageColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingMessageColor", 2131166755), MihanTheme.getDialogMessageColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.tikColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingTikColor", 2131166804), MihanTheme.getDialogTikColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.countTextColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingCountTextColor", 2131166703), MihanTheme.getDialogCountTextColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.countColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingCountColor", 2131166702), MihanTheme.getDialogCountColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.muteCountColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingMuteCountColor", 2131166756), MihanTheme.getDialogMuteCountColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.fileColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingFileColor", 2131166729), MihanTheme.getDialogFileColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.toolBarBGColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingColor", 2131166694), MihanTheme.getToolBarBGColor(localSharedPreferences), false);
              return (View)localObject;
            }
            if (paramInt == ThemingMainPageActivity.this.toolBarGradienColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingGradientColor", 2131166737), MihanTheme.getToolBarGradientColor(localSharedPreferences), true);
              return (View)localObject;
            }
            paramViewGroup = (ViewGroup)localObject;
          } while (paramInt != ThemingMainPageActivity.this.toolBarIconColorRow);
          paramView.setTextAndColor(LocaleController.getString("ThemingActionIconColor", 2131166683), MihanTheme.getToolBarIconColor(localSharedPreferences), false);
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
          if (paramInt == ThemingMainPageActivity.this.actionGradientRow)
          {
            paramView.setTextAndValue(LocaleController.getString("ThemingGradient", 2131166736), MihanTheme.getGradientString(localSharedPreferences, "theme_action_gradient"), false);
            return (View)localObject;
          }
          if (paramInt == ThemingMainPageActivity.this.tabsGradientRow)
          {
            paramView.setTextAndValue(LocaleController.getString("ThemingGradient", 2131166736), MihanTheme.getGradientString(localSharedPreferences, "theme_tabs_gradient"), false);
            return (View)localObject;
          }
          if (paramInt == ThemingMainPageActivity.this.listGradientRow)
          {
            paramView.setTextAndValue(LocaleController.getString("ThemingGradient", 2131166736), MihanTheme.getGradientString(localSharedPreferences, "theme_list_gradient"), false);
            return (View)localObject;
          }
          if (paramInt == ThemingMainPageActivity.this.avatarRadiusRow)
          {
            paramView.setTextAndValue(LocaleController.getString("ThemingAvatarRadius", 2131166684), String.valueOf(MihanTheme.getAvatarRadius(localSharedPreferences)), true);
            return (View)localObject;
          }
          if (paramInt == ThemingMainPageActivity.this.tabsHeightRow)
          {
            paramView.setTextAndValue(LocaleController.getString("ThemingTabsHeight", 2131166792), String.valueOf(MihanTheme.getTabsHeight(localSharedPreferences)), true);
            return (View)localObject;
          }
          paramViewGroup = (ViewGroup)localObject;
        } while (paramInt != ThemingMainPageActivity.this.toolBarGradientRow);
        paramView.setTextAndValue(LocaleController.getString("ThemingGradient", 2131166736), MihanTheme.getGradientString(localSharedPreferences, "theme_toolbar_gradient"), false);
        return (View)localObject;
        paramViewGroup = paramView;
      } while (i != 4);
      paramViewGroup = paramView;
      if (paramView == null) {
        paramViewGroup = new TextCheckCell(this.mContext);
      }
      paramView = (TextCheckCell)paramViewGroup;
      Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
      paramView.setTextAndCheck(LocaleController.getString("MoveTabs", 2131165974), ((SharedPreferences)localObject).getBoolean("move_tabs", false), true);
      return paramViewGroup;
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
      if (paramInt == ThemingMainPageActivity.this.actionGradientColorRow) {
        if (MihanTheme.getActionBarGradientFlag(localSharedPreferences) != 0) {
          break label48;
        }
      }
      label48:
      do
      {
        do
        {
          while ((paramInt != ThemingMainPageActivity.this.actionColorRow) && (paramInt != ThemingMainPageActivity.this.actionGradientRow) && (paramInt != ThemingMainPageActivity.this.actionGradientColorRow) && (paramInt != ThemingMainPageActivity.this.tabsColorRow) && (paramInt != ThemingMainPageActivity.this.tabsGradientRow) && (paramInt != ThemingMainPageActivity.this.tabsGradientColorRow) && (paramInt != ThemingMainPageActivity.this.listColorRow) && (paramInt != ThemingMainPageActivity.this.listGradientRow) && (paramInt != ThemingMainPageActivity.this.listGradientColorRow) && (paramInt != ThemingMainPageActivity.this.actionIconColorRow) && (paramInt != ThemingMainPageActivity.this.tabsIconColorRow) && (paramInt != ThemingMainPageActivity.this.tabsSIconColorRow) && (paramInt != ThemingMainPageActivity.this.floatButtonColorRow) && (paramInt != ThemingMainPageActivity.this.floatButtonIconColorRow) && (paramInt != ThemingMainPageActivity.this.moveTabsRow) && (paramInt != ThemingMainPageActivity.this.dividerColorRow) && (paramInt != ThemingMainPageActivity.this.nameColorRow) && (paramInt != ThemingMainPageActivity.this.dateColorRow) && (paramInt != ThemingMainPageActivity.this.messageColorRow) && (paramInt != ThemingMainPageActivity.this.tikColorRow) && (paramInt != ThemingMainPageActivity.this.countColorRow) && (paramInt != ThemingMainPageActivity.this.muteCountColorRow) && (paramInt != ThemingMainPageActivity.this.countTextColorRow) && (paramInt != ThemingMainPageActivity.this.avatarRadiusRow) && (paramInt != ThemingMainPageActivity.this.fileColorRow) && (paramInt != ThemingMainPageActivity.this.actionTitleColorRow) && (paramInt != ThemingMainPageActivity.this.tabsHeightRow) && (paramInt != ThemingMainPageActivity.this.tabsCounterColorRow) && (paramInt != ThemingMainPageActivity.this.tabsCounterTextColorRow) && (paramInt != ThemingMainPageActivity.this.tabsMuteCounterColorRow) && (paramInt != ThemingMainPageActivity.this.tabsMuteCounterTextColorRow) && (paramInt != ThemingMainPageActivity.this.toolBarBGColorRow) && (paramInt != ThemingMainPageActivity.this.toolBarGradientRow) && (paramInt != ThemingMainPageActivity.this.toolBarGradienColorRow) && (paramInt != ThemingMainPageActivity.this.toolBarIconColorRow) && (paramInt != ThemingMainPageActivity.this.secretNameColorRow)) {
            do
            {
              return false;
              if (paramInt != ThemingMainPageActivity.this.tabsGradientColorRow) {
                break;
              }
            } while (MihanTheme.getTabsGradientFlag(localSharedPreferences) == 0);
          }
          return true;
          if (paramInt != ThemingMainPageActivity.this.listGradientColorRow) {
            break;
          }
        } while (MihanTheme.getListViewGradientFlag(localSharedPreferences) != 0);
        return false;
      } while ((paramInt != ThemingMainPageActivity.this.toolBarGradienColorRow) || (MihanTheme.getToolBarGradientFlag(localSharedPreferences) != 0));
      return false;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\Theming\ThemingMainPageActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */