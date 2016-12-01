package org.telegram.ui.Supergram;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Calendar;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;

public class SolarCalendar
{
  private Calendar calendar;
  private int date;
  private int month;
  private int weekDay;
  private int year;
  
  public SolarCalendar()
  {
    this.calendar = Calendar.getInstance();
    calSolarCalendar();
  }
  
  public SolarCalendar(Calendar paramCalendar)
  {
    this.calendar = paramCalendar;
    calSolarCalendar();
  }
  
  private void calSolarCalendar()
  {
    int j = this.calendar.get(1);
    int i = this.calendar.get(2) + 1;
    int k = this.calendar.get(5);
    this.weekDay = (this.calendar.get(7) - 1);
    if (j % 4 != 0)
    {
      this.date = (new int[] { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 }[(i - 1)] + k);
      if (this.date > 79)
      {
        this.date -= 79;
        if (this.date <= 186)
        {
          switch (this.date % 31)
          {
          default: 
            this.month = (this.date / 31 + 1);
          }
          for (this.date %= 31;; this.date = 31)
          {
            this.year = (j - 621);
            return;
            this.month = (this.date / 31);
          }
        }
        this.date -= 186;
        switch (this.date % 30)
        {
        default: 
          this.month = (this.date / 30 + 7);
        }
        for (this.date %= 30;; this.date = 30)
        {
          this.year = (j - 621);
          return;
          this.month = (this.date / 30 + 6);
        }
      }
      if ((j > 1996) && (j % 4 == 1))
      {
        i = 11;
        this.date += i;
        switch (this.date % 30)
        {
        default: 
          this.month = (this.date / 30 + 10);
        }
      }
      for (this.date %= 30;; this.date = 30)
      {
        this.year = (j - 622);
        return;
        i = 10;
        break;
        this.month = (this.date / 30 + 9);
      }
    }
    this.date = (new int[] { 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335 }[(i - 1)] + k);
    if (j >= 1996)
    {
      i = 79;
      if (this.date <= i) {
        break label762;
      }
      this.date -= i;
      if (this.date > 186) {
        break label668;
      }
      switch (this.date % 31)
      {
      default: 
        this.month = (this.date / 31 + 1);
      }
    }
    for (this.date %= 31;; this.date = 31)
    {
      this.year = (j - 621);
      return;
      i = 80;
      break;
      this.month = (this.date / 31);
    }
    label668:
    this.date -= 186;
    switch (this.date % 30)
    {
    default: 
      this.month = (this.date / 30 + 7);
    }
    for (this.date %= 30;; this.date = 30)
    {
      this.year = (j - 621);
      return;
      this.month = (this.date / 30 + 6);
    }
    label762:
    this.date += 10;
    switch (this.date % 30)
    {
    default: 
      this.month = (this.date / 30 + 10);
    }
    for (this.date %= 30;; this.date = 30)
    {
      this.year = (j - 622);
      return;
      this.month = (this.date / 30 + 9);
    }
  }
  
  public static void main(String[] paramArrayOfString) {}
  
  public String getDesDate()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(String.valueOf(this.date)).append(" ").append(getMonth()).append(" ").append(String.valueOf(this.year)).append(" ").append(LocaleController.getString("Saat", 2131166250)).append(" ").append(getTime());
    return String.valueOf(localStringBuilder);
  }
  
  public String getDesMonthYear()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(getMonth()).append(" ").append(String.valueOf(this.year));
    return String.valueOf(localStringBuilder);
  }
  
  public String getMonth()
  {
    switch (this.month)
    {
    default: 
      return "";
    case 1: 
      return LocaleController.getString("Farvardin", 2131165679);
    case 2: 
      return LocaleController.getString("Ordibehesht", 2131166128);
    case 3: 
      return LocaleController.getString("Khordad", 2131165851);
    case 4: 
      return LocaleController.getString("Tir", 2131166406);
    case 5: 
      return LocaleController.getString("Mordad", 2131165970);
    case 6: 
      return LocaleController.getString("Shahrivar", 2131166320);
    case 7: 
      return LocaleController.getString("Mehr", 2131165933);
    case 8: 
      return LocaleController.getString("Aban", 2131165247);
    case 9: 
      return LocaleController.getString("Azar", 2131165390);
    case 10: 
      return LocaleController.getString("Dey", 2131165625);
    case 11: 
      return LocaleController.getString("Bahman", 2131165393);
    }
    return LocaleController.getString("Esfand", 2131165673);
  }
  
  public String getNumDate()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(String.valueOf(this.year)).append("/").append(String.valueOf(this.month)).append("/").append(String.valueOf(this.date)).append(" ");
    return String.valueOf(localStringBuilder);
  }
  
  public String getNumDateTime()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(String.valueOf(this.year)).append("/").append(String.valueOf(this.month)).append("/").append(String.valueOf(this.date)).append(" ").append(LocaleController.getString("Saat", 2131166250)).append(" ").append(getTime());
    return String.valueOf(localStringBuilder);
  }
  
  public String getShortDesDate()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(String.valueOf(this.date)).append(" ").append(getMonth());
    return String.valueOf(localStringBuilder);
  }
  
  public String getShortDesDateTime()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(String.valueOf(this.date)).append(" ").append(getMonth()).append(" ").append(LocaleController.getString("Saat", 2131166250)).append(" ").append(getTime());
    return String.valueOf(localStringBuilder);
  }
  
  public String getTime()
  {
    boolean bool = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getBoolean("enable24HourFormat", false);
    int j = this.calendar.get(11);
    int k = this.calendar.get(12);
    StringBuilder localStringBuilder1 = new StringBuilder();
    int i;
    Object localObject;
    if (!bool) {
      if (j < 12)
      {
        i = j;
        StringBuilder localStringBuilder2 = localStringBuilder1.append(i).append(":");
        if (k >= 10) {
          break label177;
        }
        localObject = "0" + k;
        label101:
        localStringBuilder2 = localStringBuilder2.append(localObject);
        if (j >= 12) {
          break label186;
        }
        localObject = " " + LocaleController.getString("AM", 2131165246);
        label143:
        localStringBuilder2.append((String)localObject);
      }
    }
    for (;;)
    {
      return String.valueOf(localStringBuilder1);
      if (j == 12)
      {
        i = 12;
        break;
      }
      i = j - 12;
      break;
      label177:
      localObject = Integer.valueOf(k);
      break label101;
      label186:
      localObject = " " + LocaleController.getString("PM", 2131166131);
      break label143;
      localStringBuilder1.append(j).append(":").append(k);
    }
  }
  
  public long getTimeInMillis()
  {
    return this.calendar.getTimeInMillis();
  }
  
  public String getWeekDay()
  {
    switch (this.weekDay)
    {
    default: 
      return "";
    case 0: 
      return LocaleController.getString("Sunday", 2131166378);
    case 1: 
      return LocaleController.getString("Monday", 2131165963);
    case 2: 
      return LocaleController.getString("Tuesday", 2131166409);
    case 3: 
      return LocaleController.getString("Wednesday", 2131166463);
    case 4: 
      return LocaleController.getString("Thursday", 2131166394);
    case 5: 
      return LocaleController.getString("Friday", 2131165761);
    }
    return LocaleController.getString("Saturday", 2131166253);
  }
  
  public String toString()
  {
    return getDesDate();
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\SolarCalendar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */