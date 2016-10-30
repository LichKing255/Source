package org.telegram.ui.Mihangram;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import com.farsitel.bazaar.ILoginCheckService;
import com.farsitel.bazaar.ILoginCheckService.Stub;
import com.farsitel.bazaar.IUpdateCheckService;
import com.farsitel.bazaar.IUpdateCheckService.Stub;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;

public class MihanAuthenticaion
{
  String PackageName = "com.mihan.mihangram";
  Activity activity;
  LoginCheckServiceConnection loginConnection;
  ILoginCheckService loginService;
  UpdateServiceConnection updateConnection;
  IUpdateCheckService updateService;
  
  public MihanAuthenticaion(Activity paramActivity)
  {
    this.activity = paramActivity;
  }
  
  private boolean isBazarInstalled()
  {
    boolean bool = false;
    if (isPackageInstalled("com.farsitel.bazaar")) {
      bool = true;
    }
    if (!bool) {
      showBazarDialog();
    }
    return bool;
  }
  
  private void loginInitService()
  {
    this.loginConnection = new LoginCheckServiceConnection();
    Intent localIntent = new Intent("com.farsitel.bazaar.service.LoginCheckService.BIND");
    localIntent.setPackage("com.farsitel.bazaar");
    this.activity.bindService(localIntent, this.loginConnection, 1);
  }
  
  private void loginReleaseService()
  {
    this.activity.unbindService(this.loginConnection);
    this.loginConnection = null;
  }
  
  private void showBazarDialog()
  {
    Object localObject1 = new AlertDialog.Builder(this.activity);
    ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165338));
    ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("DownloadBazarDes", 2131166628));
    ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("DownloadMihangram", 2131166630), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface = new Intent("android.intent.action.VIEW");
        paramAnonymousDialogInterface.setData(Uri.parse("bazaar://details?id=" + ApplicationLoader.applicationContext.getPackageManager()));
        paramAnonymousDialogInterface.setPackage("com.farsitel.bazaar");
        MihanAuthenticaion.this.activity.startActivity(paramAnonymousDialogInterface);
        System.exit(0);
      }
    });
    ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("DownloadBazar", 2131166627), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface = new Intent("android.intent.action.VIEW", Uri.parse("http://0up.ir/up10/mihangram313.apk"));
        MihanAuthenticaion.this.activity.startActivity(paramAnonymousDialogInterface);
        System.exit(0);
      }
    });
    ((AlertDialog.Builder)localObject1).setOnKeyListener(new DialogInterface.OnKeyListener()
    {
      public boolean onKey(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
      {
        if ((paramAnonymousInt == 4) && (paramAnonymousKeyEvent.getAction() == 1)) {
          System.exit(0);
        }
        return false;
      }
    });
    localObject1 = ((AlertDialog.Builder)localObject1).create();
    ((Dialog)localObject1).show();
    Object localObject2 = (TextView)((Dialog)localObject1).findViewById(16908299);
    Button localButton1 = (Button)((Dialog)localObject1).findViewById(16908313);
    Button localButton2 = (Button)((Dialog)localObject1).findViewById(16908314);
    ((TextView)localObject2).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    localButton1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    localButton2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    localObject1 = ((Dialog)localObject1).getWindow();
    localObject2 = ((Window)localObject1).getAttributes();
    ((WindowManager.LayoutParams)localObject2).width = -1;
    ((WindowManager.LayoutParams)localObject2).height = -1;
    ((WindowManager.LayoutParams)localObject2).gravity = 16;
    ((WindowManager.LayoutParams)localObject2).y = 100;
    ((Window)localObject1).setAttributes((WindowManager.LayoutParams)localObject2);
  }
  
  private void showMainPackageDialog()
  {
    Object localObject1 = new AlertDialog.Builder(this.activity);
    ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165338));
    ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("CheckMainPackageInstalled", 2131166623));
    ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("DownloadMihangram", 2131166630), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface = new Intent("android.intent.action.VIEW");
        paramAnonymousDialogInterface.setData(Uri.parse("bazaar://details?id=" + ApplicationLoader.applicationContext.getPackageManager()));
        paramAnonymousDialogInterface.setPackage("com.farsitel.bazaar");
        MihanAuthenticaion.this.activity.startActivity(paramAnonymousDialogInterface);
        System.exit(0);
      }
    });
    ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("DownloadBazar", 2131166627), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface = new Intent("android.intent.action.VIEW", Uri.parse("http://0up.ir/up10/mihangram313.apk"));
        MihanAuthenticaion.this.activity.startActivity(paramAnonymousDialogInterface);
        System.exit(0);
      }
    });
    ((AlertDialog.Builder)localObject1).setOnKeyListener(new DialogInterface.OnKeyListener()
    {
      public boolean onKey(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
      {
        if ((paramAnonymousInt == 4) && (paramAnonymousKeyEvent.getAction() == 1)) {
          System.exit(0);
        }
        return false;
      }
    });
    localObject1 = ((AlertDialog.Builder)localObject1).create();
    ((Dialog)localObject1).show();
    Object localObject2 = (TextView)((Dialog)localObject1).findViewById(16908299);
    Button localButton1 = (Button)((Dialog)localObject1).findViewById(16908313);
    Button localButton2 = (Button)((Dialog)localObject1).findViewById(16908314);
    ((TextView)localObject2).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    localButton1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    localButton2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    localObject1 = ((Dialog)localObject1).getWindow();
    localObject2 = ((Window)localObject1).getAttributes();
    ((WindowManager.LayoutParams)localObject2).width = -1;
    ((WindowManager.LayoutParams)localObject2).height = -1;
    ((WindowManager.LayoutParams)localObject2).gravity = 16;
    ((WindowManager.LayoutParams)localObject2).y = 100;
    ((Window)localObject1).setAttributes((WindowManager.LayoutParams)localObject2);
  }
  
  private void updateInitService()
  {
    this.updateConnection = new UpdateServiceConnection();
    Intent localIntent = new Intent("com.farsitel.bazaar.service.UpdateCheckService.BIND");
    localIntent.setPackage("com.farsitel.bazaar");
    this.activity.bindService(localIntent, this.updateConnection, 1);
  }
  
  private void updateReleaseService()
  {
    this.activity.unbindService(this.updateConnection);
    this.updateConnection = null;
  }
  
  public void bazarInitService()
  {
    isBazarInstalled();
    loginInitService();
    updateInitService();
  }
  
  public void bazarReleaseService()
  {
    loginReleaseService();
    updateReleaseService();
  }
  
  public boolean isMainPackageInstalled()
  {
    boolean bool = false;
    if (isPackageInstalled("com.mihan.mihangram")) {
      bool = true;
    }
    if (!bool) {
      showMainPackageDialog();
    }
    return bool;
  }
  
  public boolean isPackageInstalled(String paramString)
  {
    PackageManager localPackageManager = ApplicationLoader.applicationContext.getPackageManager();
    try
    {
      localPackageManager.getPackageInfo(paramString, 1);
      return true;
    }
    catch (PackageManager.NameNotFoundException paramString) {}
    return false;
  }
  
  public class LoginCheckServiceConnection
    implements ServiceConnection
  {
    public LoginCheckServiceConnection() {}
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      MihanAuthenticaion.this.loginService = ILoginCheckService.Stub.asInterface(paramIBinder);
      try
      {
        if (!MihanAuthenticaion.this.loginService.isLoggedIn())
        {
          paramComponentName = new AlertDialog.Builder(MihanAuthenticaion.this.activity);
          paramComponentName.setTitle(LocaleController.getString("AppName", 2131165338));
          paramComponentName.setMessage(LocaleController.getString("DownloadBazarDes", 2131166628));
          paramComponentName.setPositiveButton(LocaleController.getString("DownloadMihangram", 2131166630), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
              paramAnonymousDialogInterface = new Intent("android.intent.action.VIEW");
              paramAnonymousDialogInterface.setData(Uri.parse("bazaar://details?id=" + ApplicationLoader.applicationContext.getPackageManager()));
              paramAnonymousDialogInterface.setPackage("com.farsitel.bazaar");
              MihanAuthenticaion.this.activity.startActivity(paramAnonymousDialogInterface);
              System.exit(0);
            }
          });
          paramComponentName.setNegativeButton(LocaleController.getString("DownloadBazar", 2131166627), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
              paramAnonymousDialogInterface = new Intent("android.intent.action.VIEW", Uri.parse("http://0up.ir/up10/mihangram313.apk"));
              MihanAuthenticaion.this.activity.startActivity(paramAnonymousDialogInterface);
              System.exit(0);
            }
          });
          paramComponentName.setOnKeyListener(new DialogInterface.OnKeyListener()
          {
            public boolean onKey(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
            {
              if ((paramAnonymousInt == 4) && (paramAnonymousKeyEvent.getAction() == 1)) {
                System.exit(0);
              }
              return false;
            }
          });
          paramComponentName = paramComponentName.create();
          paramComponentName.show();
          paramIBinder = (TextView)paramComponentName.findViewById(16908299);
          Button localButton1 = (Button)paramComponentName.findViewById(16908313);
          Button localButton2 = (Button)paramComponentName.findViewById(16908314);
          paramIBinder.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          localButton1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          localButton2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          paramComponentName = paramComponentName.getWindow();
          paramIBinder = paramComponentName.getAttributes();
          paramIBinder.width = -1;
          paramIBinder.height = -1;
          paramIBinder.gravity = 16;
          paramIBinder.y = 100;
          paramComponentName.setAttributes(paramIBinder);
        }
        return;
      }
      catch (Exception paramComponentName)
      {
        paramComponentName.printStackTrace();
      }
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      MihanAuthenticaion.this.loginService = null;
    }
  }
  
  class UpdateServiceConnection
    implements ServiceConnection
  {
    UpdateServiceConnection() {}
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      MihanAuthenticaion.this.updateService = IUpdateCheckService.Stub.asInterface(paramIBinder);
      try
      {
        if (MihanAuthenticaion.this.updateService.getVersionCode(MihanAuthenticaion.this.PackageName) != -1L)
        {
          paramComponentName = new AlertDialog.Builder(MihanAuthenticaion.this.activity);
          paramComponentName.setTitle(LocaleController.getString("AppName", 2131165338));
          paramComponentName.setMessage(LocaleController.getString("DownloadBazarDes", 2131166628));
          paramComponentName.setPositiveButton(LocaleController.getString("DownloadMihangram", 2131166630), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
              paramAnonymousDialogInterface = new Intent("android.intent.action.VIEW");
              paramAnonymousDialogInterface.setData(Uri.parse("bazaar://details?id=" + ApplicationLoader.applicationContext.getPackageManager()));
              paramAnonymousDialogInterface.setPackage("com.farsitel.bazaar");
              MihanAuthenticaion.this.activity.startActivity(paramAnonymousDialogInterface);
              System.exit(0);
            }
          });
          paramComponentName.setNegativeButton(LocaleController.getString("DownloadBazar", 2131166627), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
              paramAnonymousDialogInterface = new Intent("android.intent.action.VIEW", Uri.parse("http://0up.ir/up10/mihangram313.apk"));
              MihanAuthenticaion.this.activity.startActivity(paramAnonymousDialogInterface);
              System.exit(0);
            }
          });
          paramComponentName.setOnKeyListener(new DialogInterface.OnKeyListener()
          {
            public boolean onKey(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
            {
              if ((paramAnonymousInt == 4) && (paramAnonymousKeyEvent.getAction() == 1)) {
                System.exit(0);
              }
              return false;
            }
          });
          paramComponentName = paramComponentName.create();
          paramComponentName.show();
          paramIBinder = (TextView)paramComponentName.findViewById(16908299);
          Button localButton1 = (Button)paramComponentName.findViewById(16908313);
          Button localButton2 = (Button)paramComponentName.findViewById(16908314);
          paramIBinder.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          localButton1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          localButton2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          paramComponentName = paramComponentName.getWindow();
          paramIBinder = paramComponentName.getAttributes();
          paramIBinder.width = -1;
          paramIBinder.height = -1;
          paramIBinder.gravity = 16;
          paramIBinder.y = 100;
          paramComponentName.setAttributes(paramIBinder);
        }
        return;
      }
      catch (Exception paramComponentName)
      {
        paramComponentName.printStackTrace();
      }
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      MihanAuthenticaion.this.updateService = null;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\MihanAuthenticaion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */