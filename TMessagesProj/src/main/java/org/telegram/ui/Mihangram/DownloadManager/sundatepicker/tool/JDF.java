package org.telegram.ui.Mihangram.DownloadManager.sundatepicker.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class JDF
{
  public static String[] iranianDayNames = { "شنبه", "یکشنبه", "دوشنبه", "سه شنبه", "چهارشنبه", "پنجشنبه", "جمعه" };
  public static String[] monthNames = { "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند" };
  private int JDN;
  private int gDay;
  private int gMonth;
  private int gYear;
  private int irDay;
  private int irMonth;
  private int irYear;
  private int juDay;
  private int juMonth;
  private int juYear;
  private int leap;
  private int march;
  
  public JDF()
  {
    GregorianCalendar localGregorianCalendar = new GregorianCalendar();
    setGregorianDate(localGregorianCalendar.get(1), localGregorianCalendar.get(2) + 1, localGregorianCalendar.get(5));
  }
  
  public JDF(int paramInt1, int paramInt2, int paramInt3)
  {
    setGregorianDate(paramInt1, paramInt2, paramInt3);
  }
  
  private void IranianCalendar()
  {
    int[] arrayOfInt = new int[20];
    int[] tmp8_6 = arrayOfInt;
    tmp8_6[0] = -61;
    int[] tmp13_8 = tmp8_6;
    tmp13_8[1] = 9;
    int[] tmp18_13 = tmp13_8;
    tmp18_13[2] = 38;
    int[] tmp23_18 = tmp18_13;
    tmp23_18[3] = 'Ç';
    int[] tmp29_23 = tmp23_18;
    tmp29_23[4] = 'ƪ';
    int[] tmp35_29 = tmp29_23;
    tmp35_29[5] = 'ʮ';
    int[] tmp41_35 = tmp35_29;
    tmp41_35[6] = '˴';
    int[] tmp48_41 = tmp41_35;
    tmp48_41[7] = '̲';
    int[] tmp55_48 = tmp48_41;
    tmp55_48[8] = 'ї';
    int[] tmp62_55 = tmp55_48;
    tmp62_55[9] = 'ҝ';
    int[] tmp69_62 = tmp62_55;
    tmp69_62[10] = 'Һ';
    int[] tmp76_69 = tmp69_62;
    tmp76_69[11] = '٣';
    int[] tmp83_76 = tmp76_69;
    tmp83_76[12] = 'ࠌ';
    int[] tmp90_83 = tmp83_76;
    tmp90_83[13] = '࠱';
    int[] tmp97_90 = tmp90_83;
    tmp97_90[14] = '࢐';
    int[] tmp104_97 = tmp97_90;
    tmp104_97[15] = 'ࣖ';
    int[] tmp111_104 = tmp104_97;
    tmp111_104[16] = 'औ';
    int[] tmp118_111 = tmp111_104;
    tmp118_111[17] = 'ग़';
    int[] tmp125_118 = tmp118_111;
    tmp125_118[18] = 'ঘ';
    int[] tmp132_125 = tmp125_118;
    tmp132_125[19] = '౪';
    tmp132_125;
    this.gYear = (this.irYear + 621);
    int k = -14;
    int m = arrayOfInt[0];
    int j = 1;
    int i1;
    int i2;
    int n;
    do
    {
      i1 = arrayOfInt[j];
      i2 = i1 - m;
      n = m;
      i = k;
      if (this.irYear >= i1)
      {
        i = k + (i2 / 33 * 8 + i2 % 33 / 4);
        n = i1;
      }
      j += 1;
      if (j >= 20) {
        break;
      }
      m = n;
      k = i;
    } while (this.irYear >= i1);
    j = this.irYear - n;
    k = i + (j / 33 * 8 + (j % 33 + 3) / 4);
    int i = k;
    if (i2 % 33 == 4)
    {
      i = k;
      if (i2 - j == 4) {
        i = k + 1;
      }
    }
    this.march = (i + 20 - (this.gYear / 4 - (this.gYear / 100 + 1) * 3 / 4 - 150));
    i = j;
    if (i2 - j < 6) {
      i = j - i2 + (i2 + 4) / 33 * 33;
    }
    this.leap = (((i + 1) % 33 - 1) % 4);
    if (this.leap == -1) {
      this.leap = 4;
    }
  }
  
  private int IranianDateToJDN()
  {
    IranianCalendar();
    return gregorianDateToJDN(this.gYear, 3, this.march) + (this.irMonth - 1) * 31 - this.irMonth / 7 * (this.irMonth - 7) + this.irDay - 1;
  }
  
  private void JDNToGregorian()
  {
    int i = this.JDN * 4 + 139361631 + ((this.JDN * 4 + 183187720) / 146097 * 3 / 4 * 4 - 3908);
    int j = i % 1461 / 4 * 5 + 308;
    this.gDay = (j % 153 / 5 + 1);
    this.gMonth = (j / 153 % 12 + 1);
    this.gYear = (i / 1461 - 100100 + (8 - this.gMonth) / 6);
  }
  
  private void JDNToIranian()
  {
    JDNToGregorian();
    this.irYear = (this.gYear - 621);
    IranianCalendar();
    int i = gregorianDateToJDN(this.gYear, 3, this.march);
    i = this.JDN - i;
    if (i >= 0)
    {
      if (i <= 185)
      {
        this.irMonth = (i / 31 + 1);
        this.irDay = (i % 31 + 1);
        return;
      }
      i -= 186;
    }
    for (;;)
    {
      this.irMonth = (i / 30 + 7);
      this.irDay = (i % 30 + 1);
      return;
      this.irYear -= 1;
      int j = i + 179;
      i = j;
      if (this.leap == 1) {
        i = j + 1;
      }
    }
  }
  
  private void JDNToJulian()
  {
    int i = this.JDN * 4 + 139361631;
    int j = i % 1461 / 4 * 5 + 308;
    this.juDay = (j % 153 / 5 + 1);
    this.juMonth = (j / 153 % 12 + 1);
    this.juYear = (i / 1461 - 100100 + (8 - this.juMonth) / 6);
  }
  
  private int gregorianDateToJDN(int paramInt1, int paramInt2, int paramInt3)
  {
    return ((paramInt2 - 8) / 6 + paramInt1 + 100100) * 1461 / 4 + ((paramInt2 + 9) % 12 * 153 + 2) / 5 + paramInt3 - 34840408 - (paramInt1 + 100100 + (paramInt2 - 8) / 6) / 100 * 3 / 4 + 752;
  }
  
  private int julianDateToJDN(int paramInt1, int paramInt2, int paramInt3)
  {
    return ((paramInt2 - 8) / 6 + paramInt1 + 100100) * 1461 / 4 + ((paramInt2 + 9) % 12 * 153 + 2) / 5 + paramInt3 - 34840408;
  }
  
  public int getDayOfWeek()
  {
    return this.JDN % 7;
  }
  
  public Calendar getGregorianCalendar(int paramInt1, int paramInt2, int paramInt3)
    throws ParseException
  {
    setIranianDate(paramInt1, paramInt2, paramInt3);
    Date localDate = new SimpleDateFormat("yyyy/M/d", Locale.US).parse(getGregorianDate());
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTime(localDate);
    return localCalendar;
  }
  
  public String getGregorianDate()
  {
    return this.gYear + "/" + this.gMonth + "/" + this.gDay;
  }
  
  public int getGregorianDay()
  {
    return this.gDay;
  }
  
  public int getGregorianMonth()
  {
    return this.gMonth;
  }
  
  public int getGregorianYear()
  {
    return this.gYear;
  }
  
  public String getIranianDate()
  {
    return this.irYear + "/" + this.irMonth + "/" + this.irDay;
  }
  
  public int getIranianDay()
  {
    return this.irDay;
  }
  
  public int getIranianDay(int paramInt1, int paramInt2, int paramInt3)
    throws ParseException
  {
    setIranianDate(paramInt1, paramInt2, paramInt3);
    Date localDate = new SimpleDateFormat("yyyy/M/d", Locale.US).parse(getGregorianDate());
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTime(localDate);
    paramInt2 = localCalendar.get(7);
    if (7 == paramInt2) {
      paramInt1 = 0;
    }
    do
    {
      return paramInt1;
      if (1 == paramInt2) {
        return 1;
      }
      if (2 == paramInt2) {
        return 2;
      }
      if (3 == paramInt2) {
        return 3;
      }
      if (4 == paramInt2) {
        return 4;
      }
      if (5 == paramInt2) {
        return 5;
      }
      paramInt1 = paramInt2;
    } while (6 != paramInt2);
    return 6;
  }
  
  public String getIranianDayName(int paramInt1, int paramInt2, int paramInt3)
    throws ParseException
  {
    setIranianDate(paramInt1, paramInt2, paramInt3);
    Date localDate = new SimpleDateFormat("yyyy/M/d", Locale.US).parse(getGregorianDate());
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTime(localDate);
    paramInt2 = localCalendar.get(7);
    if (7 == paramInt2) {
      paramInt1 = 0;
    }
    for (;;)
    {
      return iranianDayNames[paramInt1];
      if (1 == paramInt2)
      {
        paramInt1 = 1;
      }
      else if (2 == paramInt2)
      {
        paramInt1 = 2;
      }
      else if (3 == paramInt2)
      {
        paramInt1 = 3;
      }
      else if (4 == paramInt2)
      {
        paramInt1 = 4;
      }
      else if (5 == paramInt2)
      {
        paramInt1 = 5;
      }
      else
      {
        paramInt1 = paramInt2;
        if (6 == paramInt2) {
          paramInt1 = 6;
        }
      }
    }
  }
  
  public int getIranianMonth()
  {
    return this.irMonth;
  }
  
  public int getIranianYear()
  {
    return this.irYear;
  }
  
  public String getJulianDate()
  {
    return this.juYear + "/" + this.juMonth + "/" + this.juDay;
  }
  
  public int getJulianDay()
  {
    return this.juDay;
  }
  
  public int getJulianMonth()
  {
    return this.juMonth;
  }
  
  public int getJulianYear()
  {
    return this.juYear;
  }
  
  public String getWeekDayStr()
  {
    int i = getDayOfWeek();
    return new String[] { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" }[i];
  }
  
  public void nextDay()
  {
    this.JDN += 1;
    JDNToIranian();
    JDNToJulian();
    JDNToGregorian();
  }
  
  public void nextDay(int paramInt)
  {
    this.JDN += paramInt;
    JDNToIranian();
    JDNToJulian();
    JDNToGregorian();
  }
  
  public void previousDay()
  {
    this.JDN -= 1;
    JDNToIranian();
    JDNToJulian();
    JDNToGregorian();
  }
  
  public void previousDay(int paramInt)
  {
    this.JDN -= paramInt;
    JDNToIranian();
    JDNToJulian();
    JDNToGregorian();
  }
  
  public void setGregorianDate(int paramInt1, int paramInt2, int paramInt3)
  {
    this.gYear = paramInt1;
    this.gMonth = paramInt2;
    this.gDay = paramInt3;
    this.JDN = gregorianDateToJDN(paramInt1, paramInt2, paramInt3);
    JDNToIranian();
    JDNToJulian();
    JDNToGregorian();
  }
  
  public void setIranianDate(int paramInt1, int paramInt2, int paramInt3)
  {
    this.irYear = paramInt1;
    this.irMonth = paramInt2;
    this.irDay = paramInt3;
    this.JDN = IranianDateToJDN();
    JDNToIranian();
    JDNToJulian();
    JDNToGregorian();
  }
  
  public void setJulianDate(int paramInt1, int paramInt2, int paramInt3)
  {
    this.juYear = paramInt1;
    this.juMonth = paramInt2;
    this.juDay = paramInt3;
    this.JDN = julianDateToJDN(paramInt1, paramInt2, paramInt3);
    JDNToIranian();
    JDNToJulian();
    JDNToGregorian();
  }
  
  public String toString()
  {
    return getWeekDayStr() + ", Gregorian:[" + getGregorianDate() + "], Julian:[" + getJulianDate() + "], Iranian:[" + getIranianDate() + "]";
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\DownloadManager\sundatepicker\tool\JDF.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */