package org.telegram.ui.Mihangram.Theming;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.ColorPickerView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Mihangram.TextDescriptionCell;
import org.telegram.ui.Mihangram.Theming.Fonts.FontSelectActivity;

public class ThemingActivity
  extends BaseFragment
{
  private int chatScreenRow;
  private int contactScreenRow;
  private int drawerScreenRow;
  private int fontDesRow;
  private int fontRow;
  private int generalSectionRow2;
  private ListAdapter listAdapter;
  private ListView listView;
  private int loadThemeRow;
  private int mainScreenRow;
  private int resetRow;
  private int rowCount = 0;
  private int saveThemeRow;
  private int screensSectionRow;
  private int screensSectionRow2;
  private int settingsScreenRow;
  private int themeColorRow;
  private int themesRow;
  private int themesRow2;
  
  private void resetPage()
  {
    Intent localIntent = getParentActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getParentActivity().getPackageName());
    localIntent.addFlags(67108864);
    localIntent.addFlags(32768);
    getParentActivity().startActivity(localIntent);
  }
  
  private void saveTheme()
  {
    Object localObject1 = new AlertDialog.Builder(getParentActivity());
    ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165338));
    final Object localObject2 = new EditText(getParentActivity());
    ((EditText)localObject2).setTypeface(MihanTheme.getMihanTypeFace());
    ((EditText)localObject2).setHint(LocaleController.getString("ThemingThemeName", 2131166799));
    ((EditText)localObject2).setPadding(25, 20, 25, 10);
    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-2, -2);
    localLayoutParams.setMargins(10, 20, 10, 2);
    ((EditText)localObject2).setLayoutParams(localLayoutParams);
    ((AlertDialog.Builder)localObject1).setView((View)localObject2);
    ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
    {
      /* Error */
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        // Byte code:
        //   0: getstatic 34	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   3: ldc 36
        //   5: iconst_0
        //   6: invokevirtual 42	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
        //   9: astore_1
        //   10: new 44	java/io/File
        //   13: dup
        //   14: new 46	java/lang/StringBuilder
        //   17: dup
        //   18: invokespecial 47	java/lang/StringBuilder:<init>	()V
        //   21: invokestatic 53	android/os/Environment:getExternalStorageDirectory	()Ljava/io/File;
        //   24: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   27: ldc 59
        //   29: invokevirtual 62	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   32: invokevirtual 66	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   35: invokespecial 69	java/io/File:<init>	(Ljava/lang/String;)V
        //   38: astore_3
        //   39: aload_3
        //   40: invokevirtual 73	java/io/File:exists	()Z
        //   43: ifne +8 -> 51
        //   46: aload_3
        //   47: invokevirtual 76	java/io/File:mkdirs	()Z
        //   50: pop
        //   51: new 78	java/io/FileOutputStream
        //   54: dup
        //   55: new 44	java/io/File
        //   58: dup
        //   59: aload_3
        //   60: new 46	java/lang/StringBuilder
        //   63: dup
        //   64: invokespecial 47	java/lang/StringBuilder:<init>	()V
        //   67: aload_0
        //   68: getfield 21	org/telegram/ui/Mihangram/Theming/ThemingActivity$3:val$editText	Landroid/widget/EditText;
        //   71: invokevirtual 84	android/widget/EditText:getText	()Landroid/text/Editable;
        //   74: invokevirtual 85	java/lang/Object:toString	()Ljava/lang/String;
        //   77: invokevirtual 62	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   80: ldc 87
        //   82: invokevirtual 62	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   85: invokevirtual 66	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   88: invokespecial 90	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
        //   91: invokespecial 93	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
        //   94: astore_3
        //   95: aload_1
        //   96: invokeinterface 99 1 0
        //   101: aload_3
        //   102: invokestatic 105	org/telegram/ui/Mihangram/Theming/XmlUtils/XmlUtils:writeMapXml	(Ljava/util/Map;Ljava/io/OutputStream;)V
        //   105: aload_0
        //   106: getfield 19	org/telegram/ui/Mihangram/Theming/ThemingActivity$3:this$0	Lorg/telegram/ui/Mihangram/Theming/ThemingActivity;
        //   109: invokevirtual 109	org/telegram/ui/Mihangram/Theming/ThemingActivity:getParentActivity	()Landroid/app/Activity;
        //   112: ldc 111
        //   114: ldc 112
        //   116: invokestatic 118	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
        //   119: iconst_1
        //   120: invokestatic 124	android/widget/Toast:makeText	(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;
        //   123: astore_1
        //   124: aload_1
        //   125: invokevirtual 128	android/widget/Toast:getView	()Landroid/view/View;
        //   128: checkcast 130	android/widget/LinearLayout
        //   131: iconst_0
        //   132: invokevirtual 134	android/widget/LinearLayout:getChildAt	(I)Landroid/view/View;
        //   135: checkcast 136	android/widget/TextView
        //   138: ldc -118
        //   140: invokestatic 144	org/telegram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
        //   143: invokevirtual 148	android/widget/TextView:setTypeface	(Landroid/graphics/Typeface;)V
        //   146: aload_1
        //   147: invokevirtual 151	android/widget/Toast:show	()V
        //   150: return
        //   151: astore_1
        //   152: goto -47 -> 105
        //   155: astore_1
        //   156: goto -51 -> 105
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	159	0	this	3
        //   0	159	1	paramAnonymousDialogInterface	DialogInterface
        //   0	159	2	paramAnonymousInt	int
        //   38	64	3	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   10	51	151	java/lang/Exception
        //   51	95	151	java/lang/Exception
        //   95	105	155	java/lang/Exception
      }
    });
    ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
    localObject2 = ((AlertDialog.Builder)localObject1).create();
    ((Dialog)localObject2).show();
    localObject1 = (Button)((Dialog)localObject2).findViewById(16908313);
    localObject2 = (Button)((Dialog)localObject2).findViewById(16908314);
    ((Button)localObject1).setTypeface(MihanTheme.getMihanTypeFace());
    ((Button)localObject2).setTypeface(MihanTheme.getMihanTypeFace());
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
      MihanTheme.setColorFilter(ApplicationLoader.applicationContext.getResources().getDrawable(2130837869), i);
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
          ThemingActivity.this.finishFragment();
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
    paramContext = new LinearLayout(getParentActivity());
    paramContext.setOrientation(1);
    paramContext.addView(new ColorPickerView(getParentActivity()), LayoutHelper.createLinear(-2, -2, 17));
    this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        if (paramAnonymousInt == ThemingActivity.this.themeColorRow) {
          ThemingActivity.this.presentFragment(new ThemingSetThemeColorActivity());
        }
        do
        {
          return;
          if (paramAnonymousInt == ThemingActivity.this.fontRow)
          {
            ThemingActivity.this.presentFragment(new FontSelectActivity());
            return;
          }
          if (paramAnonymousInt == ThemingActivity.this.mainScreenRow)
          {
            ThemingActivity.this.presentFragment(new ThemingMainPageActivity());
            return;
          }
          if (paramAnonymousInt == ThemingActivity.this.chatScreenRow)
          {
            ThemingActivity.this.presentFragment(new ThemingChatActivity());
            return;
          }
          if (paramAnonymousInt == ThemingActivity.this.drawerScreenRow)
          {
            ThemingActivity.this.presentFragment(new ThemingDrawerActivity());
            return;
          }
          if (paramAnonymousInt == ThemingActivity.this.settingsScreenRow)
          {
            ThemingActivity.this.presentFragment(new ThemingSettingsActivity());
            return;
          }
          if (paramAnonymousInt == ThemingActivity.this.contactScreenRow)
          {
            ThemingActivity.this.presentFragment(new ThemingContactActivity());
            return;
          }
          if (paramAnonymousInt == ThemingActivity.this.saveThemeRow)
          {
            ThemingActivity.this.saveTheme();
            return;
          }
          if (paramAnonymousInt == ThemingActivity.this.loadThemeRow)
          {
            ThemingActivity.this.presentFragment(new LoadThemesActivity());
            return;
          }
        } while (paramAnonymousInt != ThemingActivity.this.resetRow);
        paramAnonymousAdapterView = new AlertDialog.Builder(ThemingActivity.this.getParentActivity());
        paramAnonymousAdapterView.setTitle(LocaleController.getString("AppName", 2131165338));
        paramAnonymousAdapterView.setMessage(LocaleController.getString("AreYouSureToContinue", 2131166617));
        paramAnonymousAdapterView.setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
          {
            paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0).edit();
            paramAnonymous2DialogInterface.clear();
            paramAnonymous2DialogInterface.commit();
            paramAnonymous2DialogInterface = Toast.makeText(ThemingActivity.this.getParentActivity(), LocaleController.getString("Done", 2131165634), 1);
            ((TextView)((LinearLayout)paramAnonymous2DialogInterface.getView()).getChildAt(0)).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            paramAnonymous2DialogInterface.show();
            ThemingActivity.this.resetPage();
          }
        });
        paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
        ThemingActivity.this.showDialog(paramAnonymousAdapterView.create());
      }
    });
    return this.fragmentView;
  }
  
  public boolean onFragmentCreate()
  {
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.generalSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.themeColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.fontRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.fontDesRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.screensSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.screensSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.mainScreenRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.chatScreenRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.drawerScreenRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.contactScreenRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.settingsScreenRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.themesRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.themesRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.saveThemeRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.loadThemeRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.resetRow = i;
    return super.onFragmentCreate();
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
      return ThemingActivity.this.rowCount;
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
      if ((paramInt == ThemingActivity.this.screensSectionRow) || (paramInt == ThemingActivity.this.themesRow)) {}
      do
      {
        return 0;
        if ((paramInt == ThemingActivity.this.generalSectionRow2) || (paramInt == ThemingActivity.this.screensSectionRow2) || (paramInt == ThemingActivity.this.themesRow2)) {
          return 1;
        }
        if (paramInt == ThemingActivity.this.themeColorRow) {
          return 2;
        }
        if ((paramInt == ThemingActivity.this.mainScreenRow) || (paramInt == ThemingActivity.this.chatScreenRow) || (paramInt == ThemingActivity.this.drawerScreenRow) || (paramInt == ThemingActivity.this.settingsScreenRow) || (paramInt == ThemingActivity.this.contactScreenRow) || (paramInt == ThemingActivity.this.fontRow)) {
          return 3;
        }
        if ((paramInt == ThemingActivity.this.saveThemeRow) || (paramInt == ThemingActivity.this.loadThemeRow) || (paramInt == ThemingActivity.this.resetRow)) {
          return 4;
        }
      } while (paramInt != ThemingActivity.this.fontDesRow);
      return 5;
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
      do
      {
        do
        {
          do
          {
            TextSettingsCell localTextSettingsCell;
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
                if (paramInt == ThemingActivity.this.generalSectionRow2)
                {
                  ((HeaderCell)localObject).setText(LocaleController.getString("ThemingGeneral", 2131166734));
                  return (View)localObject;
                }
                if (paramInt == ThemingActivity.this.screensSectionRow2)
                {
                  ((HeaderCell)localObject).setText(LocaleController.getString("ThemingScreens", 2131166771));
                  return (View)localObject;
                }
                paramViewGroup = (ViewGroup)localObject;
              } while (paramInt != ThemingActivity.this.themesRow2);
              ((HeaderCell)localObject).setText(LocaleController.getString("ThemingThemes", 2131166803));
              return (View)localObject;
              if (i == 2)
              {
                paramViewGroup = paramView;
                if (paramView == null) {
                  paramViewGroup = new TextColorCell(this.mContext);
                }
                ((TextColorCell)paramViewGroup).setTextAndColor(LocaleController.getString("ThemingThemeColor", 2131166796), MihanTheme.getThemeColor(), true);
                return paramViewGroup;
              }
              if (i != 3) {
                break;
              }
              localObject = paramView;
              if (paramView == null) {
                localObject = new TextSettingsCell(this.mContext);
              }
              localTextSettingsCell = (TextSettingsCell)localObject;
              if (paramInt == ThemingActivity.this.mainScreenRow)
              {
                localTextSettingsCell.setText(LocaleController.getString("ThemingMainScreen", 2131166753), true);
                return (View)localObject;
              }
              if (paramInt == ThemingActivity.this.chatScreenRow)
              {
                localTextSettingsCell.setText(LocaleController.getString("ThemingChatScreen", 2131166693), true);
                return (View)localObject;
              }
              if (paramInt == ThemingActivity.this.drawerScreenRow)
              {
                localTextSettingsCell.setText(LocaleController.getString("ThemingDrawerScreen", 2131166719), true);
                return (View)localObject;
              }
              if (paramInt == ThemingActivity.this.settingsScreenRow)
              {
                localTextSettingsCell.setText(LocaleController.getString("ThemingSettingsScreen", 2131166784), false);
                return (View)localObject;
              }
              if (paramInt == ThemingActivity.this.contactScreenRow)
              {
                localTextSettingsCell.setText(LocaleController.getString("ThemingContactScreen", 2131166695), true);
                return (View)localObject;
              }
              paramViewGroup = (ViewGroup)localObject;
            } while (paramInt != ThemingActivity.this.fontRow);
            paramView = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
            if (LocaleController.isRTL)
            {
              localTextSettingsCell.setTextAndValue(LocaleController.getString("FontType", 2131165695), paramView.getString("font_type", "ایران سانس متوسط"), false);
              return (View)localObject;
            }
            paramView = paramView.getString("font_type", "ایران سانس متوسط");
            paramInt = -1;
            switch (paramView.hashCode())
            {
            default: 
              switch (paramInt)
              {
              }
              break;
            }
            for (;;)
            {
              localTextSettingsCell.setTextAndValue(LocaleController.getString("FontType", 2131165695), paramView, false);
              return (View)localObject;
              if (!paramView.equals("تلگرام")) {
                break;
              }
              paramInt = 0;
              break;
              if (!paramView.equals("ایران سانس نازک")) {
                break;
              }
              paramInt = 1;
              break;
              if (!paramView.equals("ایران سانس معمولی")) {
                break;
              }
              paramInt = 2;
              break;
              if (!paramView.equals("ایران سانس متوسط")) {
                break;
              }
              paramInt = 3;
              break;
              if (!paramView.equals("ایران سانس ضخیم")) {
                break;
              }
              paramInt = 4;
              break;
              if (!paramView.equals("افسانه")) {
                break;
              }
              paramInt = 5;
              break;
              if (!paramView.equals("دست نویس")) {
                break;
              }
              paramInt = 6;
              break;
              if (!paramView.equals("هما")) {
                break;
              }
              paramInt = 7;
              break;
              if (!paramView.equals("مروارید")) {
                break;
              }
              paramInt = 8;
              break;
              if (!paramView.equals("یکان")) {
                break;
              }
              paramInt = 9;
              break;
              if (!paramView.equals("تیتر")) {
                break;
              }
              paramInt = 10;
              break;
              if (!paramView.equals("کودک")) {
                break;
              }
              paramInt = 11;
              break;
              paramView = "Telegram";
              continue;
              paramView = "IransansLight";
              continue;
              paramView = "Iransans";
              continue;
              paramView = "IransansMedium";
              continue;
              paramView = "IransansBold";
              continue;
              paramView = "Afsaneh";
              continue;
              paramView = "Dastnevis";
              continue;
              paramView = "Hama";
              continue;
              paramView = "Morvarid";
              continue;
              paramView = "Yekan";
              continue;
              paramView = "Titr";
              continue;
              paramView = "Koodak";
            }
            if (i != 4) {
              break;
            }
            localObject = paramView;
            if (paramView == null) {
              localObject = new TextDetailSettingsCell(this.mContext);
            }
            paramView = (TextDetailSettingsCell)localObject;
            if (paramInt == ThemingActivity.this.saveThemeRow)
            {
              paramView.setTextAndDes(LocaleController.getString("ThemingSaveTheme", 2131166769), LocaleController.getString("ThemingThemeSaveDes", 2131166801), true);
              return (View)localObject;
            }
            if (paramInt == ThemingActivity.this.loadThemeRow)
            {
              paramView.setTextAndDes(LocaleController.getString("ThemingLoadTheme", 2131166751), LocaleController.getString("ThemingThemeLoadDes", 2131166797), true);
              return (View)localObject;
            }
            paramViewGroup = (ViewGroup)localObject;
          } while (paramInt != ThemingActivity.this.resetRow);
          paramView.setTextAndDes(LocaleController.getString("ThemingResetAll", 2131166759), LocaleController.getString("ThemingThemeResetDes", 2131166800), false);
          return (View)localObject;
          paramViewGroup = paramView;
        } while (i != 5);
        localObject = paramView;
        if (paramView == null) {
          localObject = new TextDescriptionCell(this.mContext);
        }
        paramView = (TextDescriptionCell)localObject;
        paramViewGroup = (ViewGroup)localObject;
      } while (paramInt != ThemingActivity.this.fontDesRow);
      ((TextDescriptionCell)localObject).setText(LocaleController.getString("FontDescription", 2131165694), false);
      return (View)localObject;
    }
    
    public int getViewTypeCount()
    {
      return 6;
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
      return (paramInt == ThemingActivity.this.themeColorRow) || (paramInt == ThemingActivity.this.mainScreenRow) || (paramInt == ThemingActivity.this.chatScreenRow) || (paramInt == ThemingActivity.this.drawerScreenRow) || (paramInt == ThemingActivity.this.settingsScreenRow) || (paramInt == ThemingActivity.this.contactScreenRow) || (paramInt == ThemingActivity.this.saveThemeRow) || (paramInt == ThemingActivity.this.loadThemeRow) || (paramInt == ThemingActivity.this.resetRow) || (paramInt == ThemingActivity.this.fontRow);
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\Theming\ThemingActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */