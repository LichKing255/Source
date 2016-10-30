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
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.ContactsActivity;
import org.telegram.ui.Mihangram.Theming.ColorPicker.ColorSelectorDialog;
import org.telegram.ui.Mihangram.Theming.ColorPicker.ColorSelectorDialog.OnColorChangedListener;

public class ThemingContactActivity
  extends BaseFragment
{
  private int actionColorRow;
  private int actionGradientColorRow;
  private int actionGradientRow;
  private int actionIconColorRow;
  private int actionSectionRow2;
  private int actionTitleColorRow;
  private int avatarRadiusRow;
  private ListAdapter listAdapter;
  private ListView listView;
  private int rowCount = 0;
  private int userColorRow;
  private int userGradientColorRow;
  private int userGradientRow;
  private int userInfoRow;
  private int userInfoRow2;
  private int userNameColorRow;
  private int userOnlineStatusColorRow;
  private int userStatusColorRow;
  private int userTitleBGColorRow;
  private int userTitleColorRow;
  
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
          if (ThemingContactActivity.this.listView != null) {
            ThemingContactActivity.this.listView.invalidateViews();
          }
        }
      }, i, 0, 0, true).show();
      return;
      if (!paramString.equals("theme_contact_action_color")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("theme_contact_action_gcolor")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("theme_contact_action_icolor")) {
        break;
      }
      i = 2;
      break;
      if (!paramString.equals("theme_contact_action_tcolor")) {
        break;
      }
      i = 3;
      break;
      i = paramSharedPreferences.getInt(paramString, getActionBarColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getActionBarGradientcolor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getActionBarIconColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getActionBarTitleColor(paramSharedPreferences));
    }
  }
  
  private void selectContactColor(View paramView, final SharedPreferences paramSharedPreferences, final String paramString)
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
          if (ThemingContactActivity.this.listView != null) {
            ThemingContactActivity.this.listView.invalidateViews();
          }
        }
      }, i, 0, 0, true).show();
      return;
      if (!paramString.equals("theme_contact_list_color")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("theme_contact_list_gcolor")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("theme_contact_list_tbgcolor")) {
        break;
      }
      i = 2;
      break;
      if (!paramString.equals("theme_contact_list_tcolor")) {
        break;
      }
      i = 3;
      break;
      if (!paramString.equals("theme_contact_list_ncolor")) {
        break;
      }
      i = 4;
      break;
      if (!paramString.equals("theme_contact_list_scolor")) {
        break;
      }
      i = 5;
      break;
      if (!paramString.equals("theme_contact_list_oscolor")) {
        break;
      }
      i = 6;
      break;
      i = paramSharedPreferences.getInt(paramString, getContactBGColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getContactGradientcolor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getContactTitleBGColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getContactTitleTextColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getContactNameColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getContactStatusColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getContactOnlineStatusColor(paramSharedPreferences));
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
        if (ThemingContactActivity.this.listView != null) {
          ThemingContactActivity.this.listView.invalidateViews();
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
    this.actionBar.setTitle(LocaleController.getString("ThemingContactScreen", 2131166695));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1)
        {
          ThemingContactActivity.this.finishFragment();
          return;
        }
        ThemingContactActivity.this.presentFragment(new ContactsActivity(null));
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
        if (ThemingContactActivity.this.getParentActivity() == null) {}
        do
        {
          return;
          if (paramAnonymousInt == ThemingContactActivity.this.actionColorRow)
          {
            ThemingContactActivity.this.selectColor(paramAnonymousView, paramContext, "theme_contact_action_color");
            return;
          }
          if (paramAnonymousInt == ThemingContactActivity.this.actionGradientRow)
          {
            ThemingContactActivity.this.selectGradient(paramContext, "theme_contact_action_gradient");
            return;
          }
          if (paramAnonymousInt == ThemingContactActivity.this.actionGradientColorRow)
          {
            ThemingContactActivity.this.selectColor(paramAnonymousView, paramContext, "theme_contact_action_gcolor");
            return;
          }
          if (paramAnonymousInt == ThemingContactActivity.this.actionIconColorRow)
          {
            ThemingContactActivity.this.selectColor(paramAnonymousView, paramContext, "theme_contact_action_icolor");
            return;
          }
          if (paramAnonymousInt == ThemingContactActivity.this.actionTitleColorRow)
          {
            ThemingContactActivity.this.selectColor(paramAnonymousView, paramContext, "theme_contact_action_tcolor");
            return;
          }
          if (paramAnonymousInt == ThemingContactActivity.this.userColorRow)
          {
            ThemingContactActivity.this.selectContactColor(paramAnonymousView, paramContext, "theme_contact_list_color");
            return;
          }
          if (paramAnonymousInt == ThemingContactActivity.this.userGradientRow)
          {
            ThemingContactActivity.this.selectGradient(paramContext, "theme_contact_list_gradient");
            return;
          }
          if (paramAnonymousInt == ThemingContactActivity.this.userGradientColorRow)
          {
            ThemingContactActivity.this.selectContactColor(paramAnonymousView, paramContext, "theme_contact_list_gcolor");
            return;
          }
          if (paramAnonymousInt == ThemingContactActivity.this.userTitleBGColorRow)
          {
            ThemingContactActivity.this.selectContactColor(paramAnonymousView, paramContext, "theme_contact_list_tbgcolor");
            return;
          }
          if (paramAnonymousInt == ThemingContactActivity.this.userTitleColorRow)
          {
            ThemingContactActivity.this.selectContactColor(paramAnonymousView, paramContext, "theme_contact_list_tcolor");
            return;
          }
          if (paramAnonymousInt == ThemingContactActivity.this.userNameColorRow)
          {
            ThemingContactActivity.this.selectContactColor(paramAnonymousView, paramContext, "theme_contact_list_ncolor");
            return;
          }
          if (paramAnonymousInt == ThemingContactActivity.this.userStatusColorRow)
          {
            ThemingContactActivity.this.selectContactColor(paramAnonymousView, paramContext, "theme_contact_list_scolor");
            return;
          }
          if (paramAnonymousInt == ThemingContactActivity.this.userOnlineStatusColorRow)
          {
            ThemingContactActivity.this.selectContactColor(paramAnonymousView, paramContext, "theme_contact_list_oscolor");
            return;
          }
        } while (paramAnonymousInt != ThemingContactActivity.this.avatarRadiusRow);
        paramAnonymousAdapterView = new AlertDialog.Builder(ThemingContactActivity.this.getParentActivity());
        paramAnonymousAdapterView.setTitle(LocaleController.getString("AppName", 2131165338));
        paramAnonymousView = new NumberPicker(ThemingContactActivity.this.getParentActivity());
        paramAnonymousView.setMinValue(1);
        paramAnonymousView.setMaxValue(32);
        paramAnonymousView.setValue(ThemingContactActivity.this.getContactAvatarRadius(paramContext));
        paramAnonymousAdapterView.setView(paramAnonymousView);
        paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Done", 2131165634), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
          {
            paramAnonymous2DialogInterface = ThemingContactActivity.2.this.val$preferences.edit();
            paramAnonymous2DialogInterface.putInt("theme_contact_avatar_radius", paramAnonymousView.getValue());
            paramAnonymous2DialogInterface.commit();
            if (ThemingContactActivity.this.listView != null) {
              ThemingContactActivity.this.listView.invalidateViews();
            }
          }
        });
        ThemingContactActivity.this.showDialog(paramAnonymousAdapterView.create());
      }
    });
    this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
    {
      public boolean onItemLongClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        if (ThemingContactActivity.this.getParentActivity() == null) {
          return false;
        }
        if (paramAnonymousInt == ThemingContactActivity.this.actionColorRow) {
          MihanTheme.resetPreference("theme_contact_action_color", ThemingContactActivity.this.listView);
        }
        for (;;)
        {
          return true;
          if (paramAnonymousInt == ThemingContactActivity.this.actionGradientRow) {
            MihanTheme.resetPreference("theme_contact_action_gradient", ThemingContactActivity.this.listView);
          } else if (paramAnonymousInt == ThemingContactActivity.this.actionGradientColorRow) {
            MihanTheme.resetPreference("theme_contact_action_gcolor", ThemingContactActivity.this.listView);
          } else if (paramAnonymousInt == ThemingContactActivity.this.actionIconColorRow) {
            MihanTheme.resetPreference("theme_contact_action_icolor", ThemingContactActivity.this.listView);
          } else if (paramAnonymousInt == ThemingContactActivity.this.actionTitleColorRow) {
            MihanTheme.resetPreference("theme_contact_action_tcolor", ThemingContactActivity.this.listView);
          } else if (paramAnonymousInt == ThemingContactActivity.this.userColorRow) {
            MihanTheme.resetPreference("theme_contact_list_color", ThemingContactActivity.this.listView);
          } else if (paramAnonymousInt == ThemingContactActivity.this.userGradientRow) {
            MihanTheme.resetPreference("theme_contact_list_gradient", ThemingContactActivity.this.listView);
          } else if (paramAnonymousInt == ThemingContactActivity.this.userGradientColorRow) {
            MihanTheme.resetPreference("theme_contact_list_gcolor", ThemingContactActivity.this.listView);
          } else if (paramAnonymousInt == ThemingContactActivity.this.userTitleBGColorRow) {
            MihanTheme.resetPreference("theme_contact_list_tbgcolor", ThemingContactActivity.this.listView);
          } else if (paramAnonymousInt == ThemingContactActivity.this.userTitleColorRow) {
            MihanTheme.resetPreference("theme_contact_list_tcolor", ThemingContactActivity.this.listView);
          } else if (paramAnonymousInt == ThemingContactActivity.this.userNameColorRow) {
            MihanTheme.resetPreference("theme_contact_list_ncolor", ThemingContactActivity.this.listView);
          } else if (paramAnonymousInt == ThemingContactActivity.this.userStatusColorRow) {
            MihanTheme.resetPreference("theme_contact_list_scolor", ThemingContactActivity.this.listView);
          } else if (paramAnonymousInt == ThemingContactActivity.this.userOnlineStatusColorRow) {
            MihanTheme.resetPreference("theme_contact_list_oscolor", ThemingContactActivity.this.listView);
          } else if (paramAnonymousInt == ThemingContactActivity.this.avatarRadiusRow) {
            MihanTheme.resetPreference("theme_contact_avatar_radius", ThemingContactActivity.this.listView);
          }
        }
      }
    });
    return this.fragmentView;
  }
  
  public int getActionBarColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_contact_action_color", MihanTheme.getActionBarColor(paramSharedPreferences));
  }
  
  public int getActionBarGradientFlag(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_contact_action_gradient", MihanTheme.getActionBarGradientFlag(paramSharedPreferences));
  }
  
  public int getActionBarGradientcolor(SharedPreferences paramSharedPreferences)
  {
    if (getActionBarGradientFlag(paramSharedPreferences) == 0) {
      return 0;
    }
    return paramSharedPreferences.getInt("theme_contact_action_gcolor", MihanTheme.getActionBarGradientColor(paramSharedPreferences));
  }
  
  public int getActionBarIconColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_contact_action_icolor", MihanTheme.getActionBarIconColor(paramSharedPreferences));
  }
  
  public int getActionBarTitleColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_contact_action_tcolor", getActionBarIconColor(paramSharedPreferences));
  }
  
  public int getContactAvatarRadius(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_contact_avatar_radius", 32);
  }
  
  public int getContactBGColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_contact_list_color", MihanTheme.getListViewColor(paramSharedPreferences));
  }
  
  public int getContactGradientFlag(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_contact_list_gradient", MihanTheme.getListViewGradientFlag(paramSharedPreferences));
  }
  
  public int getContactGradientcolor(SharedPreferences paramSharedPreferences)
  {
    if (getContactGradientFlag(paramSharedPreferences) == 0) {
      return 0;
    }
    return paramSharedPreferences.getInt("theme_contact_list_gcolor", MihanTheme.getListViewGradientColor(paramSharedPreferences));
  }
  
  public int getContactNameColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_contact_list_ncolor", MihanTheme.getDialogNameColor(paramSharedPreferences));
  }
  
  public int getContactOnlineStatusColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_contact_list_oscolor", MihanTheme.getDialogMessageColor(paramSharedPreferences));
  }
  
  public int getContactStatusColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_contact_list_scolor", MihanTheme.getDialogMessageColor(paramSharedPreferences));
  }
  
  public int getContactTitleBGColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_contact_list_tbgcolor", -855310);
  }
  
  public int getContactTitleTextColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_contact_list_tcolor", -7697782);
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
    this.userInfoRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.userInfoRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.userColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.userGradientRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.userGradientColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.userTitleBGColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.userTitleColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.avatarRadiusRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.userNameColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.userStatusColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.userOnlineStatusColorRow = i;
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
      return ThemingContactActivity.this.rowCount;
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
      if (paramInt == ThemingContactActivity.this.userInfoRow) {}
      do
      {
        return 0;
        if ((paramInt == ThemingContactActivity.this.actionSectionRow2) || (paramInt == ThemingContactActivity.this.userInfoRow2)) {
          return 1;
        }
        if ((paramInt == ThemingContactActivity.this.actionColorRow) || (paramInt == ThemingContactActivity.this.actionGradientColorRow) || (paramInt == ThemingContactActivity.this.actionIconColorRow) || (paramInt == ThemingContactActivity.this.actionTitleColorRow) || (paramInt == ThemingContactActivity.this.userColorRow) || (paramInt == ThemingContactActivity.this.userGradientColorRow) || (paramInt == ThemingContactActivity.this.userTitleBGColorRow) || (paramInt == ThemingContactActivity.this.userTitleColorRow) || (paramInt == ThemingContactActivity.this.userNameColorRow) || (paramInt == ThemingContactActivity.this.userStatusColorRow) || (paramInt == ThemingContactActivity.this.userOnlineStatusColorRow)) {
          return 2;
        }
      } while ((paramInt != ThemingContactActivity.this.actionGradientRow) && (paramInt != ThemingContactActivity.this.userGradientRow) && (paramInt != ThemingContactActivity.this.avatarRadiusRow));
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
              if (paramInt == ThemingContactActivity.this.actionSectionRow2)
              {
                ((HeaderCell)localObject).setText(LocaleController.getString("ThemingHeader", 2131166741));
                return (View)localObject;
              }
              paramViewGroup = (ViewGroup)localObject;
            } while (paramInt != ThemingContactActivity.this.userInfoRow2);
            ((HeaderCell)localObject).setText(LocaleController.getString("ThemingContacts", 2131166696));
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
            if (paramInt == ThemingContactActivity.this.actionColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingColor", 2131166694), ThemingContactActivity.this.getActionBarColor(localSharedPreferences), false);
              return (View)localObject;
            }
            if (paramInt == ThemingContactActivity.this.actionGradientColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingGradientColor", 2131166737), ThemingContactActivity.this.getActionBarGradientcolor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingContactActivity.this.actionIconColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingActionIconColor", 2131166683), ThemingContactActivity.this.getActionBarIconColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingContactActivity.this.actionTitleColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingTitleColor", 2131166805), ThemingContactActivity.this.getActionBarTitleColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingContactActivity.this.userColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingColor", 2131166694), ThemingContactActivity.this.getContactBGColor(localSharedPreferences), false);
              return (View)localObject;
            }
            if (paramInt == ThemingContactActivity.this.userGradientColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingGradientColor", 2131166737), ThemingContactActivity.this.getContactGradientcolor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingContactActivity.this.userTitleBGColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingContactsTitleBGColor", 2131166700), ThemingContactActivity.this.getContactTitleBGColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingContactActivity.this.userTitleColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingContactsTitleTextColor", 2131166701), ThemingContactActivity.this.getContactTitleTextColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingContactActivity.this.userNameColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingContactsNameColor", 2131166697), ThemingContactActivity.this.getContactNameColor(localSharedPreferences), true);
              return (View)localObject;
            }
            if (paramInt == ThemingContactActivity.this.userStatusColorRow)
            {
              paramView.setTextAndColor(LocaleController.getString("ThemingContactsStatusColor", 2131166699), ThemingContactActivity.this.getContactStatusColor(localSharedPreferences), true);
              return (View)localObject;
            }
            paramViewGroup = (ViewGroup)localObject;
          } while (paramInt != ThemingContactActivity.this.userOnlineStatusColorRow);
          paramView.setTextAndColor(LocaleController.getString("ThemingContactsOStatusColor", 2131166698), ThemingContactActivity.this.getContactOnlineStatusColor(localSharedPreferences), true);
          return (View)localObject;
          paramViewGroup = paramView;
        } while (i != 3);
        localObject = paramView;
        if (paramView == null) {
          localObject = new TextSettingsCell(this.mContext);
        }
        paramView = (TextSettingsCell)localObject;
        localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
        if (paramInt == ThemingContactActivity.this.actionGradientRow)
        {
          paramView.setTextAndValue(LocaleController.getString("ThemingGradient", 2131166736), MihanTheme.getGradientString(localSharedPreferences, "theme_contact_action_gradient"), false);
          return (View)localObject;
        }
        if (paramInt == ThemingContactActivity.this.userGradientRow)
        {
          paramView.setTextAndValue(LocaleController.getString("ThemingGradient", 2131166736), MihanTheme.getGradientString(localSharedPreferences, "theme_contact_list_gradient"), false);
          return (View)localObject;
        }
        paramViewGroup = (ViewGroup)localObject;
      } while (paramInt != ThemingContactActivity.this.avatarRadiusRow);
      paramView.setTextAndValue(LocaleController.getString("ThemingAvatarRadius", 2131166684), String.valueOf(ThemingContactActivity.this.getContactAvatarRadius(localSharedPreferences)), true);
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
      if ((paramInt == ThemingContactActivity.this.actionGradientColorRow) && (ThemingContactActivity.this.getActionBarGradientFlag(localSharedPreferences) == 0)) {}
      while (((paramInt == ThemingContactActivity.this.userGradientColorRow) && (ThemingContactActivity.this.getContactGradientFlag(localSharedPreferences) == 0)) || ((paramInt != ThemingContactActivity.this.actionColorRow) && (paramInt != ThemingContactActivity.this.actionGradientRow) && (paramInt != ThemingContactActivity.this.actionGradientColorRow) && (paramInt != ThemingContactActivity.this.actionIconColorRow) && (paramInt != ThemingContactActivity.this.actionTitleColorRow) && (paramInt != ThemingContactActivity.this.userColorRow) && (paramInt != ThemingContactActivity.this.userGradientRow) && (paramInt != ThemingContactActivity.this.userGradientColorRow) && (paramInt != ThemingContactActivity.this.userTitleBGColorRow) && (paramInt != ThemingContactActivity.this.userTitleColorRow) && (paramInt != ThemingContactActivity.this.userNameColorRow) && (paramInt != ThemingContactActivity.this.userStatusColorRow) && (paramInt != ThemingContactActivity.this.userOnlineStatusColorRow) && (paramInt != ThemingContactActivity.this.avatarRadiusRow))) {
        return false;
      }
      return true;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\Theming\ThemingContactActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */