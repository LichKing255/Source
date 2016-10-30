package org.telegram.ui.Mihangram.DownloadManager;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.rey.material.widget.CheckBox;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener;
import java.util.Calendar;
import java.util.Locale;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Mihangram.DownloadManager.Receiver.DownloadReceiver;
import org.telegram.ui.Mihangram.DownloadManager.sundatepicker.DatePickerDialog;
import org.telegram.ui.Mihangram.DownloadManager.sundatepicker.DatePickerDialog.OnDateSetListener;
import org.telegram.ui.Mihangram.DownloadManager.sundatepicker.tool.JDF;
import org.telegram.ui.Mihangram.Theming.MihanTheme;

public class ReminderAddActivity
  extends AppCompatActivity
  implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener
{
  View Date_Relative;
  boolean[] day = { 0, 0, 0, 0, 0, 0, 0 };
  SharedPreferences.Editor editor = this.preferences.edit();
  View end_Date_Relative;
  private Calendar mCalendar;
  private Calendar mCalendarEnd;
  private Calendar mCalendarEnd_r;
  private Calendar mCalendar_r;
  private String mDate;
  private String mDateEnd;
  private TextView mDateText;
  private TextView mDateTextEnd;
  private int mDay;
  private int mDayEnd;
  private date mEndDate;
  private int mHour;
  private int mHourEnd;
  private int mMinute;
  private int mMinuteEnd;
  private int mMonth;
  private int mMonthEnd;
  private TextView mRepeatText;
  private date mStartDate;
  private String mTime;
  private String mTimeEnd;
  private TextView mTimeText;
  private TextView mTimeTextEnd;
  private EditText mTitleText;
  private Toolbar mToolbar;
  private int mYear;
  private int mYearEnd;
  private MultiStateToggleButton mstb;
  private int pDay;
  private int pDayEnd;
  private int pMonth;
  private int pMonthEnd;
  private int pYear;
  private int pYearEnd;
  SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("sdownload2", 0);
  private CheckBox w_disable;
  private CheckBox w_enable;
  
  public void onBackPressed()
  {
    super.onBackPressed();
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    setLocale("fa");
  }
  
  protected void onCreate(final Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903065);
    paramBundle = (RelativeLayout)findViewById(2131624070);
    final RelativeLayout localRelativeLayout = (RelativeLayout)findViewById(2131624078);
    this.mToolbar = ((Toolbar)findViewById(2131624065));
    this.Date_Relative = findViewById(2131624066);
    this.end_Date_Relative = findViewById(2131624074);
    this.mDateText = ((TextView)findViewById(2131624069));
    this.mTimeText = ((TextView)findViewById(2131624073));
    this.mDateTextEnd = ((TextView)findViewById(2131624077));
    this.mTimeTextEnd = ((TextView)findViewById(2131624081));
    this.mstb = ((MultiStateToggleButton)findViewById(2131624083));
    this.w_enable = ((CheckBox)findViewById(2131624084));
    this.w_disable = ((CheckBox)findViewById(2131624085));
    this.w_enable.setChecked(this.preferences.getBoolean("w_enable", false));
    this.w_disable.setChecked(this.preferences.getBoolean("w_disable", false));
    this.mstb.enableMultipleChoice(true);
    int j = 1;
    int i = 0;
    while (i < 7)
    {
      if (this.preferences.getBoolean(String.valueOf(i), false))
      {
        this.day[i] = true;
        j = 0;
      }
      i += 1;
    }
    if (j != 0)
    {
      paramBundle.setVisibility(0);
      localRelativeLayout.setVisibility(0);
    }
    for (;;)
    {
      this.mstb.setStates(this.day);
      this.mstb.setOnValueChangedListener(new ToggleButton.OnValueChangedListener()
      {
        public void onValueChanged(int paramAnonymousInt)
        {
          boolean bool2 = true;
          Object localObject = ReminderAddActivity.this.day;
          if (ReminderAddActivity.this.day[paramAnonymousInt] == 0)
          {
            bool1 = true;
            localObject[paramAnonymousInt] = bool1;
            localObject = ReminderAddActivity.this.editor;
            if (ReminderAddActivity.this.preferences.getBoolean(String.valueOf(paramAnonymousInt), false)) {
              break label125;
            }
          }
          int i;
          label125:
          for (boolean bool1 = bool2;; bool1 = false)
          {
            ((SharedPreferences.Editor)localObject).putBoolean(String.valueOf(paramAnonymousInt), bool1).commit();
            i = 1;
            paramAnonymousInt = 0;
            while (paramAnonymousInt < 7)
            {
              if (ReminderAddActivity.this.preferences.getBoolean(String.valueOf(paramAnonymousInt), false)) {
                i = 0;
              }
              paramAnonymousInt += 1;
            }
            bool1 = false;
            break;
          }
          if (i != 0)
          {
            paramBundle.setVisibility(0);
            localRelativeLayout.setVisibility(0);
            return;
          }
          paramBundle.setVisibility(8);
          localRelativeLayout.setVisibility(8);
        }
      });
      setSupportActionBar(this.mToolbar);
      this.mToolbar.setNavigationIcon(2130837810);
      this.mToolbar.setTitle("Reminder");
      this.mToolbar.setBackgroundColor(MihanTheme.getThemeColor());
      this.mToolbar.setNavigationOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          ReminderAddActivity.this.onBackPressed();
        }
      });
      paramBundle = new JDF();
      this.mCalendar = Calendar.getInstance();
      this.mCalendarEnd = Calendar.getInstance();
      this.mHour = this.preferences.getInt("HOUR_OF_DAY", this.mCalendar.get(11));
      this.mMinute = this.preferences.getInt("MINUTE", this.mCalendar.get(12));
      this.pYear = this.preferences.getInt("p_YEAR", paramBundle.getIranianYear());
      this.mYear = this.preferences.getInt("YEAR", this.mCalendar.get(1));
      this.pMonth = this.preferences.getInt("p_MONTH", paramBundle.getIranianMonth() + 1);
      this.mMonth = this.preferences.getInt("MONTH", this.mCalendar.get(2) + 1);
      this.mDay = this.preferences.getInt("DATE", this.mCalendar.get(5));
      this.pDay = this.preferences.getInt("p_DATE", paramBundle.getIranianDay());
      this.mHourEnd = this.preferences.getInt("HOUR_OF_DAY_END", this.mCalendar.get(11));
      this.mMinuteEnd = this.preferences.getInt("MINUTE_END", this.mCalendar.get(12));
      this.pYearEnd = this.preferences.getInt("p_YEAR_END", paramBundle.getIranianYear());
      this.mYearEnd = this.preferences.getInt("YEAR_END", this.mCalendar.get(1));
      this.mMonthEnd = this.preferences.getInt("MONTH_END", this.mCalendar.get(2) + 1);
      this.pMonthEnd = this.preferences.getInt("p_MONTH_END", paramBundle.getIranianMonth() + 1);
      this.mDayEnd = this.preferences.getInt("DATE_END", this.mCalendar.get(5));
      this.pDayEnd = this.preferences.getInt("p_DATE_END", paramBundle.getIranianDay());
      this.mDate = (this.pDay + "/" + this.pMonth + "/" + this.pYear);
      this.mTime = (this.mHour + ":" + this.mMinute);
      this.mDateEnd = (this.pDayEnd + "/" + this.pMonthEnd + "/" + this.pYearEnd);
      this.mTimeEnd = (this.mHourEnd + ":" + this.mMinuteEnd);
      this.mTimeText.setText(this.mTime);
      this.mTimeTextEnd.setText(this.mTimeEnd);
      this.mStartDate = new date(paramBundle.getIranianYear(), paramBundle.getIranianMonth(), paramBundle.getIranianDay());
      this.mEndDate = new date(paramBundle.getIranianYear(), paramBundle.getIranianMonth(), paramBundle.getIranianDay());
      this.mDateText.setText(this.mDate);
      this.mDateTextEnd.setText(this.mDateEnd);
      this.Date_Relative.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = DatePickerDialog.newInstance(ReminderAddActivity.this, 1, false);
          paramAnonymousView.setInitialDate(ReminderAddActivity.this.preferences.getInt("YEAR", ReminderAddActivity.this.mStartDate.year), ReminderAddActivity.this.preferences.getInt("MONTH", ReminderAddActivity.this.mStartDate.month), ReminderAddActivity.this.preferences.getInt("DATE", ReminderAddActivity.this.mStartDate.day));
          paramAnonymousView.setInitialDate(ReminderAddActivity.this.mEndDate.calendar);
          paramAnonymousView.setMainColor(-2949011);
          paramAnonymousView.setFutureDisabled(false);
          paramAnonymousView.show(ReminderAddActivity.this.getSupportFragmentManager(), "");
        }
      });
      this.end_Date_Relative.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = DatePickerDialog.newInstance(ReminderAddActivity.this, 2, false);
          paramAnonymousView.setInitialDate(ReminderAddActivity.this.preferences.getInt("YEAR_END", ReminderAddActivity.this.mEndDate.year), ReminderAddActivity.this.preferences.getInt("MONTH_END", ReminderAddActivity.this.mEndDate.month), ReminderAddActivity.this.preferences.getInt("DATE_END", ReminderAddActivity.this.mEndDate.day));
          paramAnonymousView.setInitialDate(ReminderAddActivity.this.mEndDate.calendar);
          paramAnonymousView.setMainColor(-2949011);
          paramAnonymousView.setFutureDisabled(false);
          paramAnonymousView.show(ReminderAddActivity.this.getSupportFragmentManager(), "");
        }
      });
      return;
      paramBundle.setVisibility(8);
      localRelativeLayout.setVisibility(8);
    }
  }
  
  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    getMenuInflater().inflate(2131689472, paramMenu);
    return true;
  }
  
  public void onDateSet(int paramInt1, @Nullable Calendar paramCalendar, int paramInt2, int paramInt3, int paramInt4)
  {
    paramCalendar = Calendar.getInstance();
    if (paramInt1 == 1)
    {
      this.mDay = paramCalendar.get(5);
      this.mMonth = (paramCalendar.get(2) + 1);
      this.mYear = paramCalendar.get(1);
      this.editor.putInt("YEAR", paramCalendar.get(1)).commit();
      this.editor.putInt("MONTH", paramCalendar.get(2) + 1).commit();
      this.editor.putInt("DATE", paramCalendar.get(5)).commit();
      this.editor.putInt("p_YEAR", paramInt4).commit();
      this.editor.putInt("p_MONTH", paramInt3).commit();
      this.editor.putInt("p_DATE", paramInt2).commit();
      this.mStartDate.setDate(paramInt2, paramInt3, paramInt4, paramCalendar);
      this.mDateText.setText(this.mStartDate.getDate());
      return;
    }
    this.mDayEnd = paramCalendar.get(5);
    this.mMonthEnd = (paramCalendar.get(2) + 1);
    this.mYearEnd = paramCalendar.get(1);
    this.editor.putInt("YEAR_END", paramCalendar.get(1)).commit();
    this.editor.putInt("MONTH_END", paramCalendar.get(2) + 1).commit();
    this.editor.putInt("DATE_END", paramCalendar.get(5)).commit();
    this.editor.putInt("p_YEAR_END", paramInt4).commit();
    this.editor.putInt("p_MONTH_END", paramInt3).commit();
    this.editor.putInt("p_DATE_END", paramInt2).commit();
    this.mEndDate.setDate(paramInt4, paramInt3, paramInt2, paramCalendar);
    this.mDateTextEnd.setText(this.mEndDate.getDate());
  }
  
  public void onDateSet(DatePickerDialog paramDatePickerDialog, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramDatePickerDialog.getTag() == "Datepickerdialog")
    {
      paramInt2 += 1;
      this.mDay = paramInt3;
      this.mMonth = paramInt2;
      this.mYear = paramInt1;
      this.editor.putInt("YEAR", paramInt1).commit();
      this.editor.putInt("MONTH", paramInt2).commit();
      this.editor.putInt("DATE", paramInt3).commit();
      this.mDate = (paramInt3 + "/" + paramInt2 + "/" + paramInt1);
      this.mDateText.setText(this.mDate);
    }
    while (paramDatePickerDialog.getTag() != "Datepickerdialog_end") {
      return;
    }
    paramInt2 += 1;
    this.mDayEnd = paramInt3;
    this.mMonthEnd = paramInt2;
    this.mYearEnd = paramInt1;
    this.editor.putInt("YEAR_END", paramInt1).commit();
    this.editor.putInt("MONTH_END", paramInt2).commit();
    this.editor.putInt("DATE_END", paramInt3).commit();
    this.mDateEnd = (paramInt3 + "/" + paramInt2 + "/" + paramInt1);
    this.mDateTextEnd.setText(this.mDateEnd);
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    case 2131624247: 
      paramMenuItem = new AlertDialog.Builder(this);
      paramMenuItem.setMessage(LocaleController.formatString("AreYouSureDeleteVideo", 2131165338, new Object[0]));
      paramMenuItem.setTitle(LocaleController.getString("AppName", 2131165338));
      paramMenuItem.setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          ReminderAddActivity.this.editor.clear().commit();
          new DownloadReceiver().cancelAlarm(ReminderAddActivity.this.getApplicationContext());
          ReminderAddActivity.this.onBackPressed();
        }
      });
      paramMenuItem.setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
      paramMenuItem.show();
      return true;
    }
    saveReminder();
    return true;
  }
  
  public void onTimeSet(RadialPickerLayout paramRadialPickerLayout, int paramInt1, int paramInt2, int paramInt3) {}
  
  public void saveReminder()
  {
    new DownloadReceiver().cancelAlarm(getApplicationContext());
    int j = 0;
    int i = 0;
    while (i < 7)
    {
      if (this.preferences.getBoolean(String.valueOf(i), false))
      {
        this.mCalendar_r = Calendar.getInstance();
        this.mCalendarEnd_r = Calendar.getInstance();
        this.mCalendar_r.set(7, i + 1);
        this.mCalendar_r.set(11, this.mHour);
        this.mCalendar_r.set(12, this.mMinute);
        this.mCalendar_r.set(13, 0);
        this.mCalendar_r.set(14, 0);
        this.mCalendarEnd_r.set(7, i + 1);
        this.mCalendarEnd_r.set(11, this.mHourEnd);
        this.mCalendarEnd_r.set(12, this.mMinuteEnd);
        this.mCalendarEnd_r.set(13, 0);
        this.mCalendarEnd_r.set(14, 0);
        Log.v("jjj", this.mCalendar_r.toString());
        new DownloadReceiver().setRepeatAlarm(getApplicationContext(), this.mCalendar_r, this.mCalendarEnd_r, i + 1 + 300);
        j = 1;
      }
      i += 1;
    }
    if (j == 0)
    {
      Calendar localCalendar = this.mCalendar;
      i = this.mMonth - 1;
      this.mMonth = i;
      localCalendar.set(2, i);
      this.mCalendar.set(1, this.mYear);
      this.mCalendar.set(5, this.mDay);
      this.mCalendar.set(11, this.mHour);
      this.mCalendar.set(12, this.mMinute);
      this.mCalendar.set(13, 0);
      localCalendar = this.mCalendarEnd;
      i = this.mMonthEnd - 1;
      this.mMonthEnd = i;
      localCalendar.set(2, i);
      this.mCalendarEnd.set(1, this.mYearEnd);
      this.mCalendarEnd.set(5, this.mDayEnd);
      this.mCalendarEnd.set(11, this.mHourEnd);
      this.mCalendarEnd.set(12, this.mMinuteEnd);
      this.mCalendarEnd.set(13, 0);
      new DownloadReceiver().setAlarm(getApplicationContext(), this.mCalendar, this.mCalendarEnd, 100);
    }
    this.editor.putBoolean("w_enable", this.w_enable.isChecked()).commit();
    this.editor.putBoolean("w_disable", this.w_disable.isChecked()).commit();
    Toast.makeText(getApplicationContext(), "Saved", 1).show();
    onBackPressed();
  }
  
  public void setDate_end(View paramView) {}
  
  public void setLocale(String paramString)
  {
    paramString = new Locale(paramString);
    Locale.setDefault(paramString);
    Configuration localConfiguration = new Configuration();
    localConfiguration.locale = paramString;
    getBaseContext().getResources().updateConfiguration(localConfiguration, getBaseContext().getResources().getDisplayMetrics());
  }
  
  public void setTime(View paramView)
  {
    Calendar.getInstance();
    TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener()
    {
      public void onTimeSet(RadialPickerLayout paramAnonymousRadialPickerLayout, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
      {
        ReminderAddActivity.access$202(ReminderAddActivity.this, paramAnonymousInt1);
        ReminderAddActivity.access$302(ReminderAddActivity.this, paramAnonymousInt2);
        ReminderAddActivity.this.editor.putInt("HOUR_OF_DAY", paramAnonymousInt1).commit();
        ReminderAddActivity.this.editor.putInt("MINUTE", paramAnonymousInt2).commit();
        if (paramAnonymousInt2 < 10) {
          ReminderAddActivity.access$402(ReminderAddActivity.this, paramAnonymousInt1 + ":" + "0" + paramAnonymousInt2);
        }
        for (;;)
        {
          ReminderAddActivity.this.mTimeText.setText(ReminderAddActivity.this.mTime);
          return;
          ReminderAddActivity.access$402(ReminderAddActivity.this, paramAnonymousInt1 + ":" + paramAnonymousInt2);
        }
      }
    }, this.preferences.getInt("HOUR_OF_DAY", this.mCalendar.get(11)), this.preferences.getInt("MINUTE", this.mCalendar.get(12)), false).show(getFragmentManager(), "Timepickerdialog");
  }
  
  public void setTime_end(View paramView)
  {
    Calendar.getInstance();
    TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener()
    {
      public void onTimeSet(RadialPickerLayout paramAnonymousRadialPickerLayout, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
      {
        ReminderAddActivity.access$602(ReminderAddActivity.this, paramAnonymousInt1);
        ReminderAddActivity.access$702(ReminderAddActivity.this, paramAnonymousInt2);
        ReminderAddActivity.this.editor.putInt("HOUR_OF_DAY_END", paramAnonymousInt1).commit();
        ReminderAddActivity.this.editor.putInt("MINUTE_END", paramAnonymousInt2).commit();
        if (paramAnonymousInt2 < 10) {
          ReminderAddActivity.access$802(ReminderAddActivity.this, paramAnonymousInt1 + ":" + "0" + paramAnonymousInt2);
        }
        for (;;)
        {
          ReminderAddActivity.this.mTimeTextEnd.setText(ReminderAddActivity.this.mTimeEnd);
          return;
          ReminderAddActivity.access$802(ReminderAddActivity.this, paramAnonymousInt1 + ":" + paramAnonymousInt2);
        }
      }
    }, this.preferences.getInt("HOUR_OF_DAY", this.mCalendar.get(11)), this.preferences.getInt("MINUTE", this.mCalendar.get(12)), false).show(getFragmentManager(), "Timepickerdialog_end");
  }
  
  class date
  {
    Calendar calendar;
    int day;
    int month;
    int year;
    
    date(int paramInt1, int paramInt2, int paramInt3)
    {
      this.year = paramInt1;
      this.month = paramInt2;
      this.day = paramInt3;
      this.calendar = Calendar.getInstance();
    }
    
    String getDate()
    {
      return this.year + "/" + this.month + "/" + this.day + "  (" + this.calendar.get(1) + "/" + (this.calendar.get(2) + 1) + "/" + this.calendar.get(5) + ")";
    }
    
    void setDate(int paramInt1, int paramInt2, int paramInt3, Calendar paramCalendar)
    {
      this.year = paramInt1;
      this.month = paramInt2;
      this.day = paramInt3;
      this.calendar = paramCalendar;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\DownloadManager\ReminderAddActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */