package org.telegram.ui.Supergram.UserChanges.mobo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings.Secure;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import org.telegram.ui.Supergram.SolarCalendar;

public class bd
{
  public static int a()
  {
    return new Random(System.currentTimeMillis()).nextInt();
  }
  
  public static int a(Context paramContext)
  {
    try
    {
      int i = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionCode;
      return i;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return 1;
  }
  
  public static String a(int paramInt1, int paramInt2)
  {
    char[] arrayOfChar = new char[paramInt2];
    Arrays.fill(arrayOfChar, '0');
    return new DecimalFormat(String.valueOf(arrayOfChar)).format(paramInt1);
  }
  
  public static String a(String paramString)
  {
    return paramString.substring(paramString.lastIndexOf(".") + 1);
  }
  
  public static String a(Date paramDate)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTime(paramDate);
    return new SolarCalendar(localCalendar).getShortDesDate();
  }
  
  public static String a(byte[] paramArrayOfByte)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramArrayOfByte.length * 2);
    int i = 0;
    while (i < paramArrayOfByte.length)
    {
      Object localObject2 = Integer.toHexString(paramArrayOfByte[i]);
      int j = ((String)localObject2).length();
      Object localObject1 = localObject2;
      if (j == 1) {
        localObject1 = "0" + (String)localObject2;
      }
      localObject2 = localObject1;
      if (j > 2) {
        localObject2 = ((String)localObject1).substring(j - 2, j);
      }
      localStringBuilder.append(((String)localObject2).toUpperCase());
      if (i < paramArrayOfByte.length - 1) {
        localStringBuilder.append(':');
      }
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  public static boolean a(Context paramContext, String paramString)
  {
    paramContext = paramContext.getPackageManager();
    try
    {
      paramContext.getPackageInfo(paramString, 1);
      return true;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return false;
  }
  
  /* Error */
  public static File b()
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: ldc 2
    //   5: monitorexit
    //   6: new 139	java/io/File
    //   9: dup
    //   10: new 105	java/lang/StringBuilder
    //   13: dup
    //   14: invokespecial 117	java/lang/StringBuilder:<init>	()V
    //   17: invokestatic 142	org/telegram/ui/Supergram/UserChanges/mobo/bd:c	()Ljava/io/File;
    //   20: invokevirtual 145	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   23: getstatic 149	java/io/File:separator	Ljava/lang/String;
    //   26: invokevirtual 123	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   29: ldc -105
    //   31: invokevirtual 123	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   34: getstatic 149	java/io/File:separator	Ljava/lang/String;
    //   37: invokevirtual 123	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   40: invokevirtual 126	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   43: invokespecial 152	java/io/File:<init>	(Ljava/lang/String;)V
    //   46: astore_0
    //   47: aload_0
    //   48: invokevirtual 156	java/io/File:exists	()Z
    //   51: ifne +8 -> 59
    //   54: aload_0
    //   55: invokevirtual 159	java/io/File:mkdir	()Z
    //   58: pop
    //   59: aload_0
    //   60: areturn
    //   61: astore_0
    //   62: ldc 2
    //   64: monitorexit
    //   65: aload_0
    //   66: athrow
    //   67: astore_0
    //   68: aload_0
    //   69: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   46	14	0	localFile	File
    //   61	5	0	localObject1	Object
    //   67	2	0	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   3	6	61	finally
    //   62	65	61	finally
    //   6	59	67	finally
  }
  
  public static String b(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionName;
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return "";
  }
  
  public static boolean b(String paramString)
  {
    return (paramString == null) || (paramString.length() == 0);
  }
  
  public static File c()
  {
    try
    {
      File localFile2 = Environment.getExternalStorageDirectory();
      File localFile1 = localFile2;
      if (localFile2 != null)
      {
        localFile1 = localFile2;
        if (!localFile2.exists())
        {
          localFile1 = localFile2;
          if (!localFile2.mkdirs()) {
            localFile1 = null;
          }
        }
      }
      return localFile1;
    }
    finally {}
  }
  
  public static String c(Context paramContext)
  {
    return a(paramContext.getPackageName());
  }
  
  public static String c(String paramString)
  {
    char[] arrayOfChar = new char[paramString.length()];
    int k = 0;
    if (k < paramString.length())
    {
      int j = paramString.charAt(k);
      int i;
      if ((j >= 1632) && (j <= 1641)) {
        i = (char)(j - 1584);
      }
      for (;;)
      {
        arrayOfChar[k] = i;
        k += 1;
        break;
        i = j;
        if (j >= 1776)
        {
          i = j;
          if (j <= 1785) {
            i = (char)(j - 1728);
          }
        }
      }
    }
    return new String(arrayOfChar);
  }
  
  public static String d()
  {
    return "com.hanista.mobogram";
  }
  
  public static void d(Context paramContext)
  {
    Intent localIntent = new Intent("android.intent.action.EDIT");
    localIntent.setData(Uri.parse("bazaar://details?id=" + paramContext.getPackageName()));
    paramContext.startActivity(localIntent);
  }
  
  public static String e()
  {
    return "com.hanista.mobogram.two";
  }
  
  public static boolean e(Context paramContext)
  {
    paramContext = ((ConnectivityManager)paramContext.getSystemService("connectivity")).getActiveNetworkInfo();
    return (paramContext != null) && (paramContext.isConnected());
  }
  
  public static String f(Context paramContext)
  {
    return Settings.Secure.getString(paramContext.getContentResolver(), "android_id");
  }
  
  public static void g(Context paramContext)
  {
    Intent localIntent = new Intent("android.intent.action.VIEW");
    localIntent.setData(Uri.parse("http://www.hanista.com"));
    paramContext.startActivity(localIntent);
  }
  
  public static boolean h(Context paramContext)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    try
    {
      localPackageManager.getPackageInfo(j(paramContext), 1);
      return true;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return false;
  }
  
  public static boolean i(Context paramContext)
  {
    return paramContext.getPackageName().equals(d());
  }
  
  public static String j(Context paramContext)
  {
    if (paramContext.getPackageName().equals(d())) {
      return e();
    }
    return d();
  }
  
  public static boolean k(Context paramContext)
  {
    return a(paramContext, d());
  }
  
  public static boolean l(Context paramContext)
  {
    return n(paramContext).equalsIgnoreCase("13:AD:93:96:94:26:2A:DE:5A:23:ED:F3:6E:AA:E3:53:83:DB:2B:EE");
  }
  
  public static boolean m(Context paramContext)
  {
    boolean bool = true;
    paramContext = paramContext.getPackageManager();
    try
    {
      int i = paramContext.getPackageInfo(d(), 1).versionCode;
      if (i < 71951) {
        bool = false;
      }
      return bool;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return false;
  }
  
  /* Error */
  public static String n(Context paramContext)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 34	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   4: astore_0
    //   5: invokestatic 254	org/telegram/ui/Supergram/UserChanges/mobo/bd:d	()Ljava/lang/String;
    //   8: astore_1
    //   9: aload_0
    //   10: aload_1
    //   11: bipush 64
    //   13: invokevirtual 44	android/content/pm/PackageManager:getPackageInfo	(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;
    //   16: astore_0
    //   17: new 282	java/io/ByteArrayInputStream
    //   20: dup
    //   21: aload_0
    //   22: getfield 286	android/content/pm/PackageInfo:signatures	[Landroid/content/pm/Signature;
    //   25: iconst_0
    //   26: aaload
    //   27: invokevirtual 292	android/content/pm/Signature:toByteArray	()[B
    //   30: invokespecial 295	java/io/ByteArrayInputStream:<init>	([B)V
    //   33: astore_0
    //   34: ldc_w 297
    //   37: invokestatic 302	java/security/cert/CertificateFactory:getInstance	(Ljava/lang/String;)Ljava/security/cert/CertificateFactory;
    //   40: astore_1
    //   41: aload_1
    //   42: aload_0
    //   43: invokevirtual 306	java/security/cert/CertificateFactory:generateCertificate	(Ljava/io/InputStream;)Ljava/security/cert/Certificate;
    //   46: checkcast 308	java/security/cert/X509Certificate
    //   49: astore_0
    //   50: ldc_w 310
    //   53: invokestatic 315	java/security/MessageDigest:getInstance	(Ljava/lang/String;)Ljava/security/MessageDigest;
    //   56: aload_0
    //   57: invokevirtual 318	java/security/cert/X509Certificate:getEncoded	()[B
    //   60: invokevirtual 322	java/security/MessageDigest:digest	([B)[B
    //   63: invokestatic 324	org/telegram/ui/Supergram/UserChanges/mobo/bd:a	([B)Ljava/lang/String;
    //   66: astore_0
    //   67: aload_0
    //   68: areturn
    //   69: astore_0
    //   70: ldc_w 326
    //   73: areturn
    //   74: astore_0
    //   75: ldc_w 326
    //   78: areturn
    //   79: astore_0
    //   80: ldc_w 326
    //   83: areturn
    //   84: astore_0
    //   85: ldc_w 326
    //   88: astore_0
    //   89: goto -22 -> 67
    //   92: astore_0
    //   93: ldc_w 326
    //   96: astore_0
    //   97: goto -30 -> 67
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	100	0	paramContext	Context
    //   8	34	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	17	69	android/content/pm/PackageManager$NameNotFoundException
    //   34	41	74	java/security/cert/CertificateException
    //   41	50	79	java/security/cert/CertificateException
    //   50	67	84	java/security/NoSuchAlgorithmException
    //   50	67	92	java/security/cert/CertificateEncodingException
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\UserChanges\mobo\bd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */