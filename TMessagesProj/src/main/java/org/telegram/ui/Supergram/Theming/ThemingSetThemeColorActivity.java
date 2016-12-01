package org.telegram.ui.Supergram.Theming;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Components.ColorPickerView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Supergram.Theming.ColorPicker.ColorSelectorDialog;
import org.telegram.ui.Supergram.Theming.ColorPicker.ColorSelectorDialog.OnColorChangedListener;

public class ThemingSetThemeColorActivity
  extends BaseFragment
{
  private Button newColorButton(Context paramContext, String paramString, final int paramInt)
  {
    paramContext = new Button(paramContext);
    paramContext.setText(paramString);
    paramContext.setTextSize(AndroidUtilities.dp(7.0F));
    paramContext.setTextColor(-1);
    paramContext.setBackgroundColor(paramInt);
    paramContext.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramAnonymousView = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0).edit();
        paramAnonymousView.putInt("theme_color", paramInt);
        paramAnonymousView.commit();
        ThemingSetThemeColorActivity.this.restartApp();
      }
    });
    return paramContext;
  }
  
  private LinearLayout newRow(Context paramContext, LinearLayout paramLinearLayout)
  {
    paramContext = new LinearLayout(paramContext);
    paramContext.setOrientation(0);
    paramContext.setBackgroundColor(-1);
    paramContext.setWeightSum(3.0F);
    paramLinearLayout.addView(paramContext);
    paramLinearLayout = (LinearLayout.LayoutParams)paramContext.getLayoutParams();
    paramLinearLayout.width = -1;
    paramLinearLayout.height = -1;
    paramLinearLayout.gravity = 49;
    paramLinearLayout.topMargin = AndroidUtilities.dp(4.0F);
    paramLinearLayout.bottomMargin = AndroidUtilities.dp(4.0F);
    paramContext.setLayoutParams(paramLinearLayout);
    return paramContext;
  }
  
  private void restartApp()
  {
    Intent localIntent = getParentActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getParentActivity().getPackageName());
    localIntent.addFlags(67108864);
    localIntent.addFlags(32768);
    getParentActivity().startActivity(localIntent);
  }
  
  private void setCenterButtonLayout(View paramView)
  {
    LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)paramView.getLayoutParams();
    localLayoutParams.width = AndroidUtilities.dp(0.0F);
    localLayoutParams.height = AndroidUtilities.dp(50.0F);
    localLayoutParams.weight = 1.0F;
    localLayoutParams.leftMargin = AndroidUtilities.dp(3.0F);
    localLayoutParams.rightMargin = AndroidUtilities.dp(3.0F);
    paramView.setLayoutParams(localLayoutParams);
  }
  
  private void setLeftButtonLayout(View paramView)
  {
    LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)paramView.getLayoutParams();
    localLayoutParams.width = AndroidUtilities.dp(0.0F);
    localLayoutParams.height = AndroidUtilities.dp(50.0F);
    localLayoutParams.weight = 1.0F;
    localLayoutParams.leftMargin = AndroidUtilities.dp(13.0F);
    localLayoutParams.rightMargin = AndroidUtilities.dp(3.0F);
    paramView.setLayoutParams(localLayoutParams);
  }
  
  private void setRightButtonLayout(View paramView)
  {
    LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)paramView.getLayoutParams();
    localLayoutParams.width = AndroidUtilities.dp(0.0F);
    localLayoutParams.height = AndroidUtilities.dp(50.0F);
    localLayoutParams.weight = 1.0F;
    localLayoutParams.leftMargin = AndroidUtilities.dp(3.0F);
    localLayoutParams.rightMargin = AndroidUtilities.dp(13.0F);
    paramView.setLayoutParams(localLayoutParams);
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
  
  public View createView(Context paramContext)
  {
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("Themes", 2131166393));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          ThemingSetThemeColorActivity.this.finishFragment();
        }
      }
    });
    updateColors();
    this.fragmentView = new FrameLayout(paramContext);
    Object localObject2 = (FrameLayout)this.fragmentView;
    Object localObject1 = new LinearLayout(paramContext);
    ((LinearLayout)localObject1).setOrientation(1);
    ((FrameLayout)localObject2).addView((View)localObject1);
    localObject2 = (FrameLayout.LayoutParams)((LinearLayout)localObject1).getLayoutParams();
    ((FrameLayout.LayoutParams)localObject2).width = -1;
    ((FrameLayout.LayoutParams)localObject2).height = -1;
    ((FrameLayout.LayoutParams)localObject2).gravity = 49;
    ((LinearLayout)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject2);
    ((LinearLayout)localObject1).addView(new ShadowSectionCell(paramContext));
    localObject2 = new TextColorCell(paramContext);
    ((TextColorCell)localObject2).setTextAndColor(LocaleController.getString("CustomThemes", 2131165589), MihanTheme.getThemeColor(), true);
    ((LinearLayout)localObject1).addView((View)localObject2);
    Object localObject3 = new LinearLayout(getParentActivity());
    ((LinearLayout)localObject3).setOrientation(1);
    ((LinearLayout)localObject3).addView(new ColorPickerView(getParentActivity()), LayoutHelper.createLinear(-2, -2, 17));
    ((TextColorCell)localObject2).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(final View paramAnonymousView)
      {
        paramAnonymousView = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
        ((LayoutInflater)ThemingSetThemeColorActivity.this.getParentActivity().getSystemService("layout_inflater")).inflate(2130903072, null, false);
        new ColorSelectorDialog(ThemingSetThemeColorActivity.this.getParentActivity(), new ColorSelectorDialog.OnColorChangedListener()
        {
          public void colorChanged(int paramAnonymous2Int)
          {
            paramAnonymousView.edit().putInt("theme_color", paramAnonymous2Int).commit();
            ThemingSetThemeColorActivity.this.restartApp();
          }
        }, MihanTheme.getThemeColor(paramAnonymousView), 0, 0, false).show();
      }
    });
    ((LinearLayout)localObject1).addView(new ShadowSectionCell(paramContext));
    localObject2 = new HeaderCell(paramContext);
    ((HeaderCell)localObject2).setText(LocaleController.getString("DefaultThemes", 2131165599));
    ((LinearLayout)localObject1).addView((View)localObject2);
    localObject2 = new LinearLayout(paramContext);
    ((LinearLayout)localObject2).setOrientation(1);
    ((LinearLayout)localObject1).addView((View)localObject2);
    localObject1 = (LinearLayout.LayoutParams)((LinearLayout)localObject2).getLayoutParams();
    ((LinearLayout.LayoutParams)localObject1).width = -1;
    ((LinearLayout.LayoutParams)localObject1).height = -1;
    ((LinearLayout.LayoutParams)localObject1).gravity = 49;
    ((LinearLayout.LayoutParams)localObject1).topMargin = AndroidUtilities.dp(5.0F);
    ((LinearLayout.LayoutParams)localObject1).bottomMargin = AndroidUtilities.dp(11.0F);
    ((LinearLayout)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject1);
    localObject3 = new ScrollView(paramContext);
    ((LinearLayout)localObject2).addView((View)localObject3);
    localObject1 = new LinearLayout(paramContext);
    ((LinearLayout)localObject1).setOrientation(1);
    ((LinearLayout)localObject1).setBackgroundColor(-1);
    ((ScrollView)localObject3).addView((View)localObject1);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "Black", -16777216);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Telegram", -11371101);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Supergram", -16744769);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "Red 3", -4776932);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Red 2", -2937041);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Red 1", -769226);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "Pink 3", -7860657);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Pink 2", -4056997);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Pink 1", -1499549);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "Purple 3", -11922292);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Purple 2", -8708190);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Purple 1", -6543440);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "D Purple 3", -13558894);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "D Purple 2", -11457112);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "D Purple 1", -10011977);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "Indigo 3", -15064194);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Indigo 2", -13615201);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Indigo 1", -12627531);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "Blue 3", -15906911);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Blue 2", -15108398);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Blue 1", -14575885);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "Light Blue 3", -16689253);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Light Blue 2", -16611119);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Light Blue 1", -16537100);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "Cyan 3", -16752540);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Cyan 2", -16738393);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Cyan 1", -16728876);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "Teal 3", -16757440);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Teal 2", -16746133);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Teal 1", -16738680);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "Green 3", -14983648);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Green 2", -13070788);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Green 1", -11751600);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "Light Green 3", -13407970);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Light Green 2", -9920712);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Light Green 1", -7617718);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "Lime 3", -8227049);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Lime 2", -5262293);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Lime 1", -3285959);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "Yellow 3", -688361);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Yellow 2", -278483);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Yellow 1", 60219);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "Amber 3", -37120);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Amber 2", 40960);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Amber 1", 49415);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "Orange 3", -1683200);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Orange 2", -689152);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Orange 1", 38912);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "D Orange 3", -4246004);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "D Orange 2", -1684967);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "D Orange 1", -43230);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "Brown 3", -12703965);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Brown 2", -10665929);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Brown 1", -8825528);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject2 = newRow(paramContext, (LinearLayout)localObject1);
    localObject3 = newColorButton(paramContext, "Grey 3", -14606047);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setLeftButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Grey 2", -10395295);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setCenterButtonLayout((View)localObject3);
    localObject3 = newColorButton(paramContext, "Grey 1", -6381922);
    ((LinearLayout)localObject2).addView((View)localObject3);
    setRightButtonLayout((View)localObject3);
    localObject1 = newRow(paramContext, (LinearLayout)localObject1);
    localObject2 = newColorButton(paramContext, "Blue Grey 3", -14273992);
    ((LinearLayout)localObject1).addView((View)localObject2);
    setLeftButtonLayout((View)localObject2);
    localObject2 = newColorButton(paramContext, "Blue Grey 2", -12232092);
    ((LinearLayout)localObject1).addView((View)localObject2);
    setCenterButtonLayout((View)localObject2);
    paramContext = newColorButton(paramContext, "Blue Grey 1", -10453621);
    ((LinearLayout)localObject1).addView(paramContext);
    setRightButtonLayout(paramContext);
    return this.fragmentView;
  }
  
  public void onResume()
  {
    super.onResume();
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\Theming\ThemingSetThemeColorActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */