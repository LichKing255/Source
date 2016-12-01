package org.telegram.ui.Supergram;

import android.os.Environment;

public class MihanExternalStorage
{
  public static final String EXTERNAL_SD_CARD = "externalSdCard";
  public static final String SD_CARD = "sdCard";
  
  /* Error */
  public static java.util.Map<String, java.io.File> getAllStorageLocations()
  {
    // Byte code:
    //   0: new 22	java/util/HashMap
    //   3: dup
    //   4: bipush 10
    //   6: invokespecial 25	java/util/HashMap:<init>	(I)V
    //   9: astore 4
    //   11: new 27	java/util/ArrayList
    //   14: dup
    //   15: bipush 10
    //   17: invokespecial 28	java/util/ArrayList:<init>	(I)V
    //   20: astore 5
    //   22: new 27	java/util/ArrayList
    //   25: dup
    //   26: bipush 10
    //   28: invokespecial 28	java/util/ArrayList:<init>	(I)V
    //   31: astore 6
    //   33: aload 5
    //   35: ldc 30
    //   37: invokeinterface 36 2 0
    //   42: pop
    //   43: aload 6
    //   45: ldc 30
    //   47: invokeinterface 36 2 0
    //   52: pop
    //   53: new 38	java/io/File
    //   56: dup
    //   57: ldc 40
    //   59: invokespecial 43	java/io/File:<init>	(Ljava/lang/String;)V
    //   62: astore_2
    //   63: aload_2
    //   64: invokevirtual 47	java/io/File:exists	()Z
    //   67: ifeq +68 -> 135
    //   70: new 49	java/util/Scanner
    //   73: dup
    //   74: aload_2
    //   75: invokespecial 52	java/util/Scanner:<init>	(Ljava/io/File;)V
    //   78: astore_2
    //   79: aload_2
    //   80: invokevirtual 55	java/util/Scanner:hasNext	()Z
    //   83: ifeq +52 -> 135
    //   86: aload_2
    //   87: invokevirtual 59	java/util/Scanner:nextLine	()Ljava/lang/String;
    //   90: astore_3
    //   91: aload_3
    //   92: ldc 61
    //   94: invokevirtual 67	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   97: ifeq -18 -> 79
    //   100: aload_3
    //   101: ldc 69
    //   103: invokevirtual 73	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   106: iconst_1
    //   107: aaload
    //   108: astore_3
    //   109: aload_3
    //   110: ldc 30
    //   112: invokevirtual 76	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   115: ifne -36 -> 79
    //   118: aload 5
    //   120: aload_3
    //   121: invokeinterface 36 2 0
    //   126: pop
    //   127: goto -48 -> 79
    //   130: astore_2
    //   131: aload_2
    //   132: invokevirtual 79	java/lang/Exception:printStackTrace	()V
    //   135: new 38	java/io/File
    //   138: dup
    //   139: ldc 81
    //   141: invokespecial 43	java/io/File:<init>	(Ljava/lang/String;)V
    //   144: astore_2
    //   145: aload_2
    //   146: invokevirtual 47	java/io/File:exists	()Z
    //   149: ifeq +94 -> 243
    //   152: new 49	java/util/Scanner
    //   155: dup
    //   156: aload_2
    //   157: invokespecial 52	java/util/Scanner:<init>	(Ljava/io/File;)V
    //   160: astore 7
    //   162: aload 7
    //   164: invokevirtual 55	java/util/Scanner:hasNext	()Z
    //   167: ifeq +76 -> 243
    //   170: aload 7
    //   172: invokevirtual 59	java/util/Scanner:nextLine	()Ljava/lang/String;
    //   175: astore_2
    //   176: aload_2
    //   177: ldc 83
    //   179: invokevirtual 67	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   182: ifeq -20 -> 162
    //   185: aload_2
    //   186: ldc 69
    //   188: invokevirtual 73	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   191: iconst_2
    //   192: aaload
    //   193: astore_3
    //   194: aload_3
    //   195: astore_2
    //   196: aload_3
    //   197: ldc 85
    //   199: invokevirtual 89	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   202: ifeq +15 -> 217
    //   205: aload_3
    //   206: iconst_0
    //   207: aload_3
    //   208: ldc 85
    //   210: invokevirtual 93	java/lang/String:indexOf	(Ljava/lang/String;)I
    //   213: invokevirtual 97	java/lang/String:substring	(II)Ljava/lang/String;
    //   216: astore_2
    //   217: aload_2
    //   218: ldc 30
    //   220: invokevirtual 76	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   223: ifne -61 -> 162
    //   226: aload 6
    //   228: aload_2
    //   229: invokeinterface 36 2 0
    //   234: pop
    //   235: goto -73 -> 162
    //   238: astore_2
    //   239: aload_2
    //   240: invokevirtual 79	java/lang/Exception:printStackTrace	()V
    //   243: iconst_0
    //   244: istore_0
    //   245: iload_0
    //   246: aload 5
    //   248: invokeinterface 101 1 0
    //   253: if_icmpge +46 -> 299
    //   256: iload_0
    //   257: istore_1
    //   258: aload 6
    //   260: aload 5
    //   262: iload_0
    //   263: invokeinterface 105 2 0
    //   268: checkcast 63	java/lang/String
    //   271: invokeinterface 107 2 0
    //   276: ifne +16 -> 292
    //   279: aload 5
    //   281: iload_0
    //   282: invokeinterface 110 2 0
    //   287: pop
    //   288: iload_0
    //   289: iconst_1
    //   290: isub
    //   291: istore_1
    //   292: iload_1
    //   293: iconst_1
    //   294: iadd
    //   295: istore_0
    //   296: goto -51 -> 245
    //   299: aload 6
    //   301: invokeinterface 113 1 0
    //   306: new 27	java/util/ArrayList
    //   309: dup
    //   310: bipush 10
    //   312: invokespecial 28	java/util/ArrayList:<init>	(I)V
    //   315: astore 6
    //   317: aload 5
    //   319: invokeinterface 117 1 0
    //   324: astore 7
    //   326: aload 7
    //   328: invokeinterface 120 1 0
    //   333: ifeq +240 -> 573
    //   336: new 38	java/io/File
    //   339: dup
    //   340: aload 7
    //   342: invokeinterface 124 1 0
    //   347: checkcast 63	java/lang/String
    //   350: invokespecial 43	java/io/File:<init>	(Ljava/lang/String;)V
    //   353: astore 8
    //   355: aload 8
    //   357: invokevirtual 47	java/io/File:exists	()Z
    //   360: ifeq -34 -> 326
    //   363: aload 8
    //   365: invokevirtual 127	java/io/File:isDirectory	()Z
    //   368: ifeq -42 -> 326
    //   371: aload 8
    //   373: invokevirtual 130	java/io/File:canWrite	()Z
    //   376: ifeq -50 -> 326
    //   379: aload 8
    //   381: invokevirtual 134	java/io/File:listFiles	()[Ljava/io/File;
    //   384: astore 9
    //   386: ldc -120
    //   388: astore_2
    //   389: aload_2
    //   390: astore_3
    //   391: aload 9
    //   393: ifnull +70 -> 463
    //   396: aload 9
    //   398: arraylength
    //   399: istore_1
    //   400: iconst_0
    //   401: istore_0
    //   402: aload_2
    //   403: astore_3
    //   404: iload_0
    //   405: iload_1
    //   406: if_icmpge +57 -> 463
    //   409: aload 9
    //   411: iload_0
    //   412: aaload
    //   413: astore_3
    //   414: new 138	java/lang/StringBuilder
    //   417: dup
    //   418: invokespecial 139	java/lang/StringBuilder:<init>	()V
    //   421: aload_2
    //   422: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   425: aload_3
    //   426: invokevirtual 146	java/io/File:getName	()Ljava/lang/String;
    //   429: invokevirtual 149	java/lang/String:hashCode	()I
    //   432: invokevirtual 152	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   435: ldc 85
    //   437: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   440: aload_3
    //   441: invokevirtual 156	java/io/File:length	()J
    //   444: invokevirtual 159	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   447: ldc -95
    //   449: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   452: invokevirtual 164	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   455: astore_2
    //   456: iload_0
    //   457: iconst_1
    //   458: iadd
    //   459: istore_0
    //   460: goto -58 -> 402
    //   463: new 138	java/lang/StringBuilder
    //   466: dup
    //   467: invokespecial 139	java/lang/StringBuilder:<init>	()V
    //   470: aload_3
    //   471: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   474: ldc -90
    //   476: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   479: invokevirtual 164	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   482: astore_3
    //   483: aload 6
    //   485: aload_3
    //   486: invokeinterface 107 2 0
    //   491: ifne -165 -> 326
    //   494: new 138	java/lang/StringBuilder
    //   497: dup
    //   498: invokespecial 139	java/lang/StringBuilder:<init>	()V
    //   501: ldc -88
    //   503: invokevirtual 143	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   506: aload 4
    //   508: invokeinterface 171 1 0
    //   513: invokevirtual 152	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   516: invokevirtual 164	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   519: astore_2
    //   520: aload 4
    //   522: invokeinterface 171 1 0
    //   527: ifne +29 -> 556
    //   530: ldc 11
    //   532: astore_2
    //   533: aload 6
    //   535: aload_3
    //   536: invokeinterface 36 2 0
    //   541: pop
    //   542: aload 4
    //   544: aload_2
    //   545: aload 8
    //   547: invokeinterface 175 3 0
    //   552: pop
    //   553: goto -227 -> 326
    //   556: aload 4
    //   558: invokeinterface 171 1 0
    //   563: iconst_1
    //   564: if_icmpne -31 -> 533
    //   567: ldc 8
    //   569: astore_2
    //   570: goto -37 -> 533
    //   573: aload 5
    //   575: invokeinterface 113 1 0
    //   580: aload 4
    //   582: invokeinterface 178 1 0
    //   587: ifeq +16 -> 603
    //   590: aload 4
    //   592: ldc 11
    //   594: invokestatic 184	android/os/Environment:getExternalStorageDirectory	()Ljava/io/File;
    //   597: invokeinterface 175 3 0
    //   602: pop
    //   603: aload 4
    //   605: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   244	216	0	i	int
    //   257	150	1	j	int
    //   62	25	2	localObject1	Object
    //   130	2	2	localException1	Exception
    //   144	85	2	localObject2	Object
    //   238	2	2	localException2	Exception
    //   388	182	2	str1	String
    //   90	446	3	str2	String
    //   9	595	4	localHashMap	java.util.HashMap
    //   20	554	5	localArrayList1	java.util.ArrayList
    //   31	503	6	localArrayList2	java.util.ArrayList
    //   160	181	7	localObject3	Object
    //   353	193	8	localFile	java.io.File
    //   384	26	9	arrayOfFile	java.io.File[]
    // Exception table:
    //   from	to	target	type
    //   53	79	130	java/lang/Exception
    //   79	127	130	java/lang/Exception
    //   135	162	238	java/lang/Exception
    //   162	194	238	java/lang/Exception
    //   196	217	238	java/lang/Exception
    //   217	235	238	java/lang/Exception
  }
  
  public static boolean isAvailable()
  {
    String str = Environment.getExternalStorageState();
    return ("mounted".equals(str)) || ("mounted_ro".equals(str));
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\MihanExternalStorage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */