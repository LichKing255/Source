package org.telegram.ui.Supergram.Theming;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.widget.ImageView;
import android.widget.ListView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;

public class MihanTheme
{
  public static CharSequence[] items = { LocaleController.getString("Disabled", 2131165631), LocaleController.getString("ThemingT_B", 2131166788), LocaleController.getString("ThemingB_T", 2131166688), LocaleController.getString("ThemingL_R", 2131166743), LocaleController.getString("ThemingR_L", 2131166758), LocaleController.getString("ThemingTL_BR", 2131166786), LocaleController.getString("ThemingTR_BL", 2131166787), LocaleController.getString("ThemingBL_TR", 2131166686), LocaleController.getString("ThemingBR_TL", 2131166687) };
  
  public static int contrastColor(int paramInt)
  {
    int i = Color.red(paramInt);
    int j = Color.green(paramInt);
    paramInt = Color.blue(paramInt);
    if (0.299D * i + 0.587D * j + 0.114D * paramInt > 186.0D) {
      return -16777216;
    }
    return -1;
  }
  
  public static int getActionBarColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_action_color", getThemeColor(paramSharedPreferences));
  }
  
  public static int getActionBarGradientColor(SharedPreferences paramSharedPreferences)
  {
    if (getActionBarGradientFlag(paramSharedPreferences) == 0) {
      return 0;
    }
    return paramSharedPreferences.getInt("theme_action_gradient_color", getActionBarColor(paramSharedPreferences));
  }
  
  public static int getActionBarGradientFlag(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_action_gradient", 0);
  }
  
  public static int getActionBarIconColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_action_icon_color", -1);
  }
  
  public static int getActionBarTitleColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_action_title_color", getActionBarIconColor(paramSharedPreferences));
  }
  
  public static int getAvatarRadius(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_avatar_radius", 26);
  }
  
  public static int getDarkerColor(int paramInt, float paramFloat)
  {
    int i = Color.alpha(paramInt);
    int j = Color.red(paramInt);
    int k = Color.green(paramInt);
    paramInt = Color.blue(paramInt);
    return Color.argb(i, Math.max((int)(j * paramFloat), 0), Math.max((int)(k * paramFloat), 0), Math.max((int)(paramInt * paramFloat), 0));
  }
  
  public static int getDialogCountColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_dialog_count_color", -14104523);
  }
  
  public static int getDialogCountTextColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_dialog_count_text_color", -1);
  }
  
  public static int getDialogDateColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_dialog_date_color", -6710887);
  }
  
  public static int getDialogDividerColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_dialog_divider_color", -2302756);
  }
  
  public static int getDialogFileColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_dialog_file_color", getThemeColor());
  }
  
  public static int getDialogMCountTextColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_dialog_mcount_tcolor", -1);
  }
  
  public static int getDialogMessageColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_dialog_message_color", -7368817);
  }
  
  public static int getDialogMuteCountColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_dialog_mcount_color", -3684409);
  }
  
  public static int getDialogNameColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_dialog_name_color", -14606047);
  }
  
  public static int getDialogSNameColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_dialog_sname_color", -16734706);
  }
  
  public static int getDialogTikColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_dialog_tik_color", -12080585);
  }
  
  public static int getFloatingButtonColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_float_color", getThemeColor(paramSharedPreferences));
  }
  
  public static int getFloatingButtonIconColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_float_icon_color", -1);
  }
  
  public static GradientDrawable.Orientation getGradientOrientation(int paramInt)
  {
    if (paramInt == 1) {
      return GradientDrawable.Orientation.TOP_BOTTOM;
    }
    if (paramInt == 2) {
      return GradientDrawable.Orientation.BOTTOM_TOP;
    }
    if (paramInt == 3) {
      return GradientDrawable.Orientation.LEFT_RIGHT;
    }
    if (paramInt == 4) {
      return GradientDrawable.Orientation.RIGHT_LEFT;
    }
    if (paramInt == 5) {
      return GradientDrawable.Orientation.TL_BR;
    }
    if (paramInt == 6) {
      return GradientDrawable.Orientation.TR_BL;
    }
    if (paramInt == 7) {
      return GradientDrawable.Orientation.BL_TR;
    }
    return GradientDrawable.Orientation.BR_TL;
  }
  
  public static String getGradientString(SharedPreferences paramSharedPreferences, String paramString)
  {
    int i = paramSharedPreferences.getInt(paramString, 0);
    if (i == 0) {
      return LocaleController.getString("Disabled", 2131165631);
    }
    if (i == 1) {
      return LocaleController.getString("ThemingT_B", 2131166788);
    }
    if (i == 2) {
      return LocaleController.getString("ThemingB_T", 2131166688);
    }
    if (i == 3) {
      return LocaleController.getString("ThemingL_R", 2131166743);
    }
    if (i == 4) {
      return LocaleController.getString("ThemingR_L", 2131166758);
    }
    if (i == 5) {
      return LocaleController.getString("ThemingTL_BR", 2131166786);
    }
    if (i == 6) {
      return LocaleController.getString("ThemingTR_BL", 2131166787);
    }
    if (i == 7) {
      return LocaleController.getString("ThemingBL_TR", 2131166686);
    }
    return LocaleController.getString("ThemingBR_TL", 2131166687);
  }
  
  public static int getLighterColor(int paramInt, float paramFloat)
  {
    return Color.argb(Math.round(Color.alpha(paramInt) * paramFloat), Color.red(paramInt), Color.green(paramInt), Color.blue(paramInt));
  }
  
  public static int getLighterDarkerColor(int paramInt)
  {
    int i = Color.red(paramInt);
    int j = Color.green(paramInt);
    int k = Color.blue(paramInt);
    if (0.299D * i + 0.587D * j + 0.114D * k > 186.0D) {
      return getDarkerColor(paramInt, 0.8F);
    }
    return getLighterColor(paramInt, 0.7F);
  }
  
  public static int getListViewColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_list_color", -1);
  }
  
  public static int getListViewGradientColor(SharedPreferences paramSharedPreferences)
  {
    if (getListViewGradientFlag(paramSharedPreferences) == 0) {
      return 0;
    }
    return paramSharedPreferences.getInt("theme_list_gradient_color", -1);
  }
  
  public static int getListViewGradientFlag(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_list_gradient", 0);
  }
  
  public static Typeface getMihanTypeFace()
  {
    return AndroidUtilities.getTypeface("fonts/rmedium.ttf");
  }
  
  public static int getSelectedTabIconColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_tabss_icon_color", -1);
  }
  
  public static int getTabsBackgroundColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_tabs_color", getThemeColor(paramSharedPreferences));
  }
  
  public static int getTabsCounterBGColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_tabs_counter_color", -1049906);
  }
  
  public static int getTabsCounterTextColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_tabs_counter_tcolor", -16777216);
  }
  
  public static int getTabsGradientColor(SharedPreferences paramSharedPreferences)
  {
    if (getTabsGradientFlag(paramSharedPreferences) == 0) {
      return 0;
    }
    return paramSharedPreferences.getInt("theme_tabs_gradient_color", getTabsBackgroundColor(paramSharedPreferences));
  }
  
  public static int getTabsGradientFlag(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_tabs_gradient", 0);
  }
  
  public static int getTabsHeight(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_tabs_height", 42);
  }
  
  public static int getTabsIconColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_tabs_icon_color", getLighterColor(getSelectedTabIconColor(paramSharedPreferences), 0.45F));
  }
  
  public static int getTabsMCounterBGColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_tabs_mcounter_color", -3355444);
  }
  
  public static int getTabsMCounterTextColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_tabs_mcounter_tcolor", -16777216);
  }
  
  public static int getThemeColor()
  {
    return ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0).getInt("theme_color", -15035368);
  }
  
  public static int getThemeColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_color", -15035368);
  }
  
  public static int getToolBarBGColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_toolbar_color", -723724);
  }
  
  public static int getToolBarGradientColor(SharedPreferences paramSharedPreferences)
  {
    if (getToolBarGradientFlag(paramSharedPreferences) == 0) {
      return 0;
    }
    return paramSharedPreferences.getInt("theme_toolbar_gradient_color", getToolBarBGColor(paramSharedPreferences));
  }
  
  public static int getToolBarGradientFlag(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_toolbar_gradient", 0);
  }
  
  public static int getToolBarIconColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_toolbar_icon_color", -15035368);
  }
  
  public static void resetPreference(String paramString, ListView paramListView)
  {
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0).edit();
    localEditor.remove(paramString);
    localEditor.commit();
    if (paramListView != null) {
      paramListView.invalidateViews();
    }
  }
  
  public static void setColorFilter(Drawable paramDrawable, int paramInt)
  {
    paramDrawable.setColorFilter(paramInt, PorterDuff.Mode.SRC_IN);
  }
  
  public static void setColorFilter(ImageView paramImageView, int paramInt)
  {
    paramImageView.setColorFilter(paramInt, PorterDuff.Mode.SRC_IN);
  }
  
  public static GradientDrawable setGradiant(int paramInt1, int paramInt2, GradientDrawable.Orientation paramOrientation)
  {
    paramOrientation = new GradientDrawable(paramOrientation, new int[] { paramInt1, paramInt2 });
    paramOrientation.setShape(0);
    return paramOrientation;
  }
  
  public static GradientDrawable setGradiant(int paramInt1, int paramInt2, GradientDrawable.Orientation paramOrientation, float paramFloat)
  {
    paramOrientation = new GradientDrawable(paramOrientation, new int[] { paramInt1, paramInt2 });
    paramOrientation.setShape(0);
    paramOrientation.setCornerRadius(AndroidUtilities.dp(paramFloat));
    return paramOrientation;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\Theming\MihanTheme.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */