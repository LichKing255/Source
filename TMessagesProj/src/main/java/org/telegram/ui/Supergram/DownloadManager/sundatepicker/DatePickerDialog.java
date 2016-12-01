package org.telegram.ui.Supergram.DownloadManager.sundatepicker;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Calendar;
import org.telegram.ui.Supergram.DownloadManager.sundatepicker.month.MonthMainFragement;
import org.telegram.ui.Supergram.DownloadManager.sundatepicker.tool.Date;
import org.telegram.ui.Supergram.DownloadManager.sundatepicker.tool.JDF;
import org.telegram.ui.Supergram.DownloadManager.sundatepicker.tool.Util;
import org.telegram.ui.Supergram.DownloadManager.sundatepicker.year.YearMainFragement;

public class DatePickerDialog
  extends DialogFragment
  implements View.OnClickListener
{
  private static GradientDrawable circle;
  public static LinearLayout dayMonth;
  private static TextView dayNameTV;
  private static TextView dayTV;
  static int id;
  private static int mBlue = 0;
  private static OnDateSetListener mCallBack;
  static boolean mDarkTheme;
  private static int mGry = 0;
  private static Typeface mTypeFace = null;
  private static boolean mVibrate;
  public static int maxMonth;
  static int maxYear;
  static int minYear;
  private static TextView monthTV;
  private static TextView yearTV;
  TextView doneTV;
  FragmentManager fragmentManager;
  FrameLayout frameLayout;
  
  public static boolean checkVibrate()
  {
    return mVibrate;
  }
  
  public static int getBlueColor()
  {
    return mBlue;
  }
  
  public static GradientDrawable getCircle()
  {
    return circle;
  }
  
  public static int getGrayColor()
  {
    return mGry;
  }
  
  public static int getRequestID()
  {
    return id;
  }
  
  public static Typeface getTypeFace()
  {
    return mTypeFace;
  }
  
  public static DatePickerDialog newInstance(OnDateSetListener paramOnDateSetListener, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    DatePickerDialog localDatePickerDialog = new DatePickerDialog();
    mDarkTheme = paramBoolean;
    if (mDarkTheme) {
      localDatePickerDialog.setStyle(2, 16973938);
    }
    for (;;)
    {
      mCallBack = paramOnDateSetListener;
      Date.setDate(paramInt2, paramInt3, paramInt4, false);
      minYear = new JDF().getIranianYear();
      maxYear = minYear + 2;
      mVibrate = true;
      id = paramInt1;
      mBlue = 0;
      mGry = 0;
      mTypeFace = null;
      maxMonth = 0;
      return localDatePickerDialog;
      localDatePickerDialog.setStyle(2, 16973942);
    }
  }
  
  public static DatePickerDialog newInstance(OnDateSetListener paramOnDateSetListener, int paramInt, boolean paramBoolean)
  {
    JDF localJDF = new JDF();
    return newInstance(paramOnDateSetListener, paramInt, localJDF.getIranianYear(), localJDF.getIranianMonth(), localJDF.getIranianDay(), paramBoolean);
  }
  
  public static DatePickerDialog newInstance(OnDateSetListener paramOnDateSetListener, boolean paramBoolean)
  {
    return newInstance(paramOnDateSetListener, 0, paramBoolean);
  }
  
  public static void updateDisplay(int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      dayTV.setText("" + paramInt3);
      monthTV.setText(JDF.monthNames[(paramInt2 - 1)]);
      yearTV.setText("" + paramInt1);
      dayNameTV.setText(new JDF().getIranianDayName(paramInt1, paramInt2, paramInt3));
      return;
    }
    catch (Exception localException) {}
  }
  
  public void onClick(View paramView)
  {
    if (paramView.getId() == 2131624113)
    {
      yearTV.setTextColor(mBlue);
      dayTV.setTextColor(mGry);
      monthTV.setTextColor(mGry);
      switchFragment(new YearMainFragement(minYear, maxYear));
    }
    do
    {
      return;
      if (paramView.getId() == 2131624179)
      {
        yearTV.setTextColor(mGry);
        dayTV.setTextColor(mBlue);
        monthTV.setTextColor(mBlue);
        switchFragment(new MonthMainFragement());
        return;
      }
    } while (paramView.getId() != 2131624115);
    if (mCallBack != null)
    {
      paramView = Calendar.getInstance();
      JDF localJDF = new JDF();
      localJDF.setIranianDate(Date.getYear(), Date.getMonth(), Date.getDay());
      paramView.set(localJDF.getGregorianYear(), localJDF.getGregorianMonth(), localJDF.getGregorianDay());
      mCallBack.onDateSet(id, paramView, Date.getYear(), Date.getMonth(), Date.getDay());
      Util.tryVibrate(getActivity());
    }
    dismiss();
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = paramLayoutInflater.inflate(2130903093, null);
    if (mBlue == 0) {
      mBlue = -16737844;
    }
    if (mGry == 0) {
      mGry = -8355712;
    }
    circle = new GradientDrawable();
    circle.setCornerRadius(80.0F);
    circle.setColor(mBlue);
    circle.setAlpha(80);
    this.frameLayout = ((FrameLayout)paramLayoutInflater.findViewById(2131624117));
    this.fragmentManager = getChildFragmentManager();
    dayTV = (TextView)paramLayoutInflater.findViewById(2131624112);
    monthTV = (TextView)paramLayoutInflater.findViewById(2131624180);
    yearTV = (TextView)paramLayoutInflater.findViewById(2131624113);
    dayNameTV = (TextView)paramLayoutInflater.findViewById(2131624178);
    this.doneTV = ((TextView)paramLayoutInflater.findViewById(2131624115));
    this.doneTV.setTextColor(Color.parseColor("#FF424242"));
    dayMonth = (LinearLayout)paramLayoutInflater.findViewById(2131624179);
    if (mTypeFace != null)
    {
      dayTV.setTypeface(mTypeFace);
      monthTV.setTypeface(mTypeFace);
      yearTV.setTypeface(mTypeFace);
      dayNameTV.setTypeface(mTypeFace);
      this.doneTV.setTypeface(mTypeFace);
    }
    if (mDarkTheme)
    {
      dayNameTV.setTextColor(-16777216);
      dayNameTV.setBackgroundColor(-2046820353);
    }
    for (;;)
    {
      dayMonth.setOnClickListener(this);
      yearTV.setOnClickListener(this);
      this.doneTV.setOnClickListener(this);
      updateDisplay(Date.getYear(), Date.getMonth(), Date.getDay());
      ((LinearLayout)paramLayoutInflater.findViewById(2131624179)).performClick();
      return paramLayoutInflater;
      dayNameTV.setTextColor(-1);
      dayNameTV.setBackgroundColor(-8355712);
    }
  }
  
  public void onStart()
  {
    DisplayMetrics localDisplayMetrics = getResources().getDisplayMetrics();
    int i = (int)(localDisplayMetrics.widthPixels * 0.75D);
    int j = (int)(localDisplayMetrics.heightPixels * 0.9D);
    getDialog().getWindow().setLayout(i, j);
    setRetainInstance(true);
    super.onStart();
  }
  
  public void setFutureDisabled(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      JDF localJDF = new JDF();
      maxMonth = localJDF.getIranianMonth();
      maxYear = localJDF.getIranianYear();
      if (minYear > maxYear) {
        minYear = maxYear - 1;
      }
      if (Date.getMonth() > localJDF.getIranianMonth()) {
        Date.setMonth(localJDF.getIranianMonth());
      }
      if (Date.getDay() > localJDF.getIranianDay()) {
        Date.setDay(localJDF.getIranianDay());
      }
      if (Date.getYear() > localJDF.getIranianYear()) {
        Date.setYear(localJDF.getIranianYear());
      }
      return;
    }
    maxMonth = 0;
  }
  
  public void setInitialDate(int paramInt1, int paramInt2, int paramInt3)
  {
    Date.setDate(paramInt1, paramInt2, paramInt3, false);
  }
  
  public void setInitialDate(Calendar paramCalendar)
  {
    JDF localJDF = new JDF();
    localJDF.setGregorianDate(paramCalendar.get(1), paramCalendar.get(2) + 1, paramCalendar.get(5));
    Date.setDate(localJDF.getIranianYear(), localJDF.getIranianMonth(), localJDF.getIranianDay(), false);
  }
  
  public void setMainColor(int paramInt)
  {
    mBlue = paramInt;
  }
  
  public void setRequestID(int paramInt)
  {
    id = paramInt;
  }
  
  public void setSecondColor(int paramInt)
  {
    mGry = paramInt;
  }
  
  public void setTypeFace(Typeface paramTypeface)
  {
    mTypeFace = paramTypeface;
  }
  
  public void setVibrate(boolean paramBoolean)
  {
    mVibrate = paramBoolean;
  }
  
  public void setYearRange(int paramInt1, int paramInt2)
  {
    minYear = paramInt1;
    maxYear = paramInt2;
  }
  
  void switchFragment(Fragment paramFragment)
  {
    FragmentTransaction localFragmentTransaction = this.fragmentManager.beginTransaction();
    localFragmentTransaction.setCustomAnimations(17432576, 17432577);
    localFragmentTransaction.replace(2131624117, paramFragment);
    localFragmentTransaction.addToBackStack(null);
    localFragmentTransaction.commit();
  }
  
  public static abstract interface OnDateSetListener
  {
    public abstract void onDateSet(int paramInt1, Calendar paramCalendar, int paramInt2, int paramInt3, int paramInt4);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\DownloadManager\sundatepicker\DatePickerDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */