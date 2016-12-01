package org.telegram.ui.Supergram.Theming;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Supergram.Theming.XmlUtils.XmlUtils;

public class LoadThemesActivity
  extends BaseFragment
{
  private ListView listView;
  private int selectedBackground;
  private int selectedColor;
  
  private void applyWallpaper(String paramString)
  {
    File localFile;
    if ((this.selectedBackground != -1) && (this.selectedBackground != 1000001))
    {
      paramString = new File(Environment.getExternalStorageDirectory() + "/Supergram/Theme", paramString);
      localFile = new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg");
    }
    try
    {
      AndroidUtilities.copyFile(paramString, localFile);
      ApplicationLoader.reloadWallpaper();
      return;
    }
    catch (Exception paramString)
    {
      FileLog.e("tmessages", paramString);
    }
  }
  
  private void listFiles()
  {
    Object localObject1 = new ArrayList();
    Object localObject2 = new File(Environment.getExternalStorageDirectory() + "/Supergram/Theme");
    if (!((File)localObject2).exists()) {
      ((File)localObject2).mkdirs();
    }
    localObject2 = ((File)localObject2).listFiles();
    if (localObject2.length > 0)
    {
      int i = 0;
      while (i < localObject2.length)
      {
        String str = localObject2[i].getName();
        if ((localObject2[i].isFile()) && (str.substring(str.lastIndexOf(".") + 1).equals("txt"))) {
          ((ArrayList)localObject1).add(str);
        }
        i += 1;
      }
    }
    localObject1 = new ArrayAdapter(getParentActivity(), 17367043, (List)localObject1);
    this.listView.setAdapter((ListAdapter)localObject1);
  }
  
  private void loadTheme(String paramString)
    throws Throwable
  {
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0).edit();
    for (;;)
    {
      try
      {
        Object localObject2 = new FileInputStream(new File(Environment.getExternalStorageDirectory() + "/Supergram/Theme/", paramString));
        try
        {
          localEditor.clear();
          localObject2 = XmlUtils.readMapXml((InputStream)localObject2).entrySet().iterator();
          if (!((Iterator)localObject2).hasNext()) {
            continue;
          }
          localObject4 = (Map.Entry)((Iterator)localObject2).next();
          localObject3 = ((Map.Entry)localObject4).getValue();
          localObject4 = (String)((Map.Entry)localObject4).getKey();
          if (!(localObject3 instanceof Boolean)) {
            continue;
          }
          localEditor.putBoolean((String)localObject4, ((Boolean)localObject3).booleanValue());
          continue;
          paramString = paramString.substring(0, paramString.lastIndexOf(".")) + ".jpg";
        }
        catch (Exception localException1) {}
      }
      catch (Exception localException2)
      {
        Object localObject4;
        Object localObject3;
        Object localObject1;
        continue;
      }
      if (new File(Environment.getExternalStorageDirectory() + "/Supergram/Theme/" + paramString).exists())
      {
        localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        if (((SharedPreferences)localObject1).getInt("selectedBackground", 1000001) == 1000001)
        {
          this.selectedBackground = 113;
          this.selectedColor = 0;
          localObject1 = ((SharedPreferences)localObject1).edit();
          ((SharedPreferences.Editor)localObject1).putInt("selectedBackground", this.selectedBackground);
          ((SharedPreferences.Editor)localObject1).putInt("selectedColor", this.selectedColor);
          ((SharedPreferences.Editor)localObject1).commit();
        }
        applyWallpaper(paramString);
      }
      paramString = Toast.makeText(getParentActivity(), LocaleController.getString("ThemingThemeLoaded", 2131166798), 1);
      ((TextView)((LinearLayout)paramString.getView()).getChildAt(0)).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      paramString.show();
      return;
      if ((localObject3 instanceof Float))
      {
        ((SharedPreferences.Editor)localObject1).putFloat((String)localObject4, ((Float)localObject3).floatValue());
      }
      else if ((localObject3 instanceof Integer))
      {
        ((SharedPreferences.Editor)localObject1).putInt((String)localObject4, ((Integer)localObject3).intValue());
      }
      else if ((localObject3 instanceof Long))
      {
        ((SharedPreferences.Editor)localObject1).putLong((String)localObject4, ((Long)localObject3).longValue());
      }
      else if ((localObject3 instanceof String))
      {
        ((SharedPreferences.Editor)localObject1).putString((String)localObject4, (String)localObject3);
        continue;
        ((SharedPreferences.Editor)localObject1).commit();
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("ThemingThemes", 2131166803));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          LoadThemesActivity.this.finishFragment();
        }
      }
    });
    Object localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
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
      MihanTheme.setColorFilter((Drawable)localObject1, i);
      this.actionBar.setBackButtonDrawable((Drawable)localObject1);
      localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
      localObject2 = ((SharedPreferences)localObject1).edit();
      if (!((SharedPreferences)localObject1).contains("theme_action_color"))
      {
        ((SharedPreferences.Editor)localObject2).putInt("theme_action_color", MihanTheme.getThemeColor()).commit();
        ((SharedPreferences.Editor)localObject2).putInt("theme_tabs_color", MihanTheme.getThemeColor()).commit();
        ((SharedPreferences.Editor)localObject2).putInt("theme_float_color", MihanTheme.getThemeColor()).commit();
      }
      this.fragmentView = new FrameLayout(paramContext);
      localObject1 = (FrameLayout)this.fragmentView;
      this.listView = new ListView(paramContext);
      this.listView.setDivider(null);
      this.listView.setDividerHeight(0);
      this.listView.setVerticalScrollBarEnabled(false);
      AndroidUtilities.setListViewEdgeEffectColor(this.listView, AvatarDrawable.getProfileBackColorForId(5));
      ((FrameLayout)localObject1).addView(this.listView);
      paramContext = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
      paramContext.width = -1;
      paramContext.height = -1;
      this.listView.setLayoutParams(paramContext);
      listFiles();
      this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          paramAnonymousAdapterView = LoadThemesActivity.this.listView.getItemAtPosition(paramAnonymousInt).toString();
          try
          {
            LoadThemesActivity.this.loadTheme(paramAnonymousAdapterView);
            paramAnonymousAdapterView = LoadThemesActivity.this.getParentActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(LoadThemesActivity.this.getParentActivity().getPackageName());
            paramAnonymousAdapterView.addFlags(67108864);
            paramAnonymousAdapterView.addFlags(32768);
            LoadThemesActivity.this.getParentActivity().startActivity(paramAnonymousAdapterView);
            return;
          }
          catch (Throwable paramAnonymousAdapterView)
          {
            for (;;)
            {
              paramAnonymousAdapterView.printStackTrace();
            }
          }
        }
      });
      this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
      {
        public boolean onItemLongClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, final int paramAnonymousInt, long paramAnonymousLong)
        {
          paramAnonymousAdapterView = new BottomSheet.Builder(LoadThemesActivity.this.getParentActivity());
          paramAnonymousView = LocaleController.getString("Delete", 2131165600);
          DialogInterface.OnClickListener local1 = new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              paramAnonymous2DialogInterface = new AlertDialog.Builder(LoadThemesActivity.this.getParentActivity());
              paramAnonymous2DialogInterface.setTitle(LocaleController.getString("AppName", 2131165338));
              paramAnonymous2DialogInterface.setMessage(LocaleController.getString("AreYouSureToContinue", 2131166617));
              paramAnonymous2DialogInterface.setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous3DialogInterface, int paramAnonymous3Int)
                {
                  paramAnonymous3DialogInterface = LoadThemesActivity.this.listView.getItemAtPosition(LoadThemesActivity.3.1.this.val$position).toString();
                  new File(Environment.getExternalStorageDirectory() + "/Supergram/Theme", paramAnonymous3DialogInterface).delete();
                  paramAnonymous3DialogInterface = Toast.makeText(LoadThemesActivity.this.getParentActivity(), LocaleController.getString("ThemingFileDeleted", 2131166730), 1);
                  ((TextView)((LinearLayout)paramAnonymous3DialogInterface.getView()).getChildAt(0)).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                  paramAnonymous3DialogInterface.show();
                  LoadThemesActivity.this.listFiles();
                }
              });
              paramAnonymous2DialogInterface.setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
              LoadThemesActivity.this.showDialog(paramAnonymous2DialogInterface.create());
            }
          };
          paramAnonymousAdapterView.setItems(new CharSequence[] { paramAnonymousView }, local1);
          LoadThemesActivity.this.showDialog(paramAnonymousAdapterView.create());
          return true;
        }
      });
      return (View)localObject1;
      this.actionBar.setBackgroundColor(i);
    }
  }
  
  public void onResume()
  {
    super.onResume();
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\Theming\LoadThemesActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */