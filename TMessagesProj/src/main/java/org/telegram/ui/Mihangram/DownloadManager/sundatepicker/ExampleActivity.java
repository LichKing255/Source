package org.telegram.ui.Mihangram.DownloadManager.sundatepicker;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import java.util.Calendar;
import org.telegram.ui.Mihangram.DownloadManager.sundatepicker.tool.JDF;

public class ExampleActivity
  extends FragmentActivity
  implements View.OnClickListener, DatePickerDialog.OnDateSetListener
{
  date dateOne;
  Button dateOneBTN;
  date dateTwo;
  Button dateTwoBTN;
  
  public void onClick(View paramView)
  {
    int i = 1;
    if (paramView.getId() == 2131624127) {
      i = 2;
    }
    DatePickerDialog localDatePickerDialog = DatePickerDialog.newInstance(this, i, ((CheckBox)findViewById(2131624125)).isChecked());
    if (!((CheckBox)findViewById(2131624123)).isChecked()) {
      localDatePickerDialog.setTypeFace(Typeface.createFromAsset(getAssets(), "pFont.ttf"));
    }
    if (paramView.getId() == 2131624126) {
      localDatePickerDialog.setInitialDate(this.dateOne.year, this.dateOne.month, this.dateOne.day);
    }
    for (;;)
    {
      if (((CheckBox)findViewById(2131624124)).isChecked()) {
        localDatePickerDialog.setMainColor(-2949011);
      }
      localDatePickerDialog.setFutureDisabled(((CheckBox)findViewById(2131624122)).isChecked());
      localDatePickerDialog.show(getSupportFragmentManager(), "");
      return;
      localDatePickerDialog.setInitialDate(this.dateTwo.calendar);
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    setContentView(2130903078);
    this.dateOneBTN = ((Button)findViewById(2131624126));
    this.dateTwoBTN = ((Button)findViewById(2131624127));
    this.dateOneBTN.setOnClickListener(this);
    this.dateTwoBTN.setOnClickListener(this);
    JDF localJDF = new JDF();
    this.dateOne = new date(localJDF.getIranianYear(), localJDF.getIranianMonth(), localJDF.getIranianDay());
    this.dateTwo = new date(localJDF.getIranianYear(), localJDF.getIranianMonth(), localJDF.getIranianDay());
    super.onCreate(paramBundle);
  }
  
  public void onDateSet(int paramInt1, Calendar paramCalendar, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramInt1 == 1)
    {
      this.dateOne.setDate(paramInt2, paramInt3, paramInt4, paramCalendar);
      this.dateOneBTN.setText(this.dateOne.getDate());
      return;
    }
    this.dateTwo.setDate(paramInt2, paramInt3, paramInt4, paramCalendar);
    this.dateTwoBTN.setText(this.dateTwo.getDate());
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
      return this.year + "/" + this.month + "/" + this.day + "  (" + this.calendar.get(1) + "/" + this.calendar.get(2) + "/" + this.calendar.get(5) + ")";
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


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\DownloadManager\sundatepicker\ExampleActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */