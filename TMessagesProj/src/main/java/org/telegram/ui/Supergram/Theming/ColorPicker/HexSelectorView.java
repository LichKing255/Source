package org.telegram.ui.Supergram.Theming.ColorPicker;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.ui.Supergram.Theming.MihanTheme;

public class HexSelectorView
  extends LinearLayout
{
  private Button btnSave;
  private int color;
  private Dialog dialog;
  private EditText edit;
  private OnColorChangedListener listener;
  private TextView txtError;
  
  public HexSelectorView(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public HexSelectorView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  private void init()
  {
    View localView = ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(2130903067, null);
    addView(localView, new LinearLayout.LayoutParams(-1, -1));
    this.txtError = ((TextView)localView.findViewById(2131624089));
    this.edit = ((EditText)localView.findViewById(2131624087));
    this.edit.setTypeface(MihanTheme.getMihanTypeFace());
    this.edit.setOnFocusChangeListener(new View.OnFocusChangeListener()
    {
      public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
      {
        if ((paramAnonymousBoolean) && (HexSelectorView.this.dialog != null)) {
          HexSelectorView.this.dialog.getWindow().setSoftInputMode(5);
        }
      }
    });
    this.edit.setOnKeyListener(new View.OnKeyListener()
    {
      public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
      {
        Log.d("HexSelector", "onKey: keyCode" + paramAnonymousInt + " event: " + paramAnonymousKeyEvent);
        HexSelectorView.this.validateColorInTextView();
        return false;
      }
    });
    this.edit.addTextChangedListener(new TextWatcher()
    {
      public void afterTextChanged(Editable paramAnonymousEditable)
      {
        HexSelectorView.this.validateColorInTextView();
      }
      
      public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      
      public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
    });
    this.edit.setOnEditorActionListener(new TextView.OnEditorActionListener()
    {
      public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
      {
        if ((paramAnonymousInt == 6) || (paramAnonymousInt == 0))
        {
          HexSelectorView.this.validateColorInTextView();
          ((InputMethodManager)HexSelectorView.this.getContext().getSystemService("input_method")).hideSoftInputFromWindow(HexSelectorView.this.edit.getApplicationWindowToken(), 2);
          return true;
        }
        return false;
      }
    });
    this.btnSave = ((Button)localView.findViewById(2131624088));
    this.btnSave.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        HexSelectorView.this.validateColorInTextView();
      }
    });
  }
  
  private void onColorChanged()
  {
    Log.d("HexSelector", "String parsing succeeded. changing to " + this.color);
    if (this.listener != null) {
      this.listener.colorChanged(getColor());
    }
  }
  
  private String padLeft(String paramString, char paramChar, int paramInt)
  {
    if (paramString.length() >= paramInt) {
      return paramString;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int i = paramString.length();
    while (i < paramInt)
    {
      localStringBuilder.append(paramChar);
      i += 1;
    }
    localStringBuilder.append(paramString);
    return localStringBuilder.toString();
  }
  
  public int getColor()
  {
    return this.color;
  }
  
  public void setColor(int paramInt)
  {
    if (paramInt == this.color) {
      return;
    }
    this.color = paramInt;
    this.edit.setText(padLeft(Integer.toHexString(paramInt).toUpperCase(), '0', 8));
    this.txtError.setVisibility(8);
  }
  
  public void setDialog(Dialog paramDialog)
  {
    this.dialog = paramDialog;
  }
  
  public void setOnColorChangedListener(OnColorChangedListener paramOnColorChangedListener)
  {
    this.listener = paramOnColorChangedListener;
  }
  
  public void validateColorInTextView()
  {
    Object localObject2;
    try
    {
      localObject2 = this.edit.getText().toString().toUpperCase().trim();
      Log.d("HexSelector", "String parsing: " + (String)localObject2);
      Object localObject1 = localObject2;
      if (((String)localObject2).startsWith("0x")) {
        localObject1 = ((String)localObject2).substring(2);
      }
      localObject2 = localObject1;
      if (((String)localObject1).startsWith("#")) {
        localObject2 = ((String)localObject1).substring(1);
      }
      if (((String)localObject2).length() != 8) {
        throw new Exception();
      }
    }
    catch (Exception localException)
    {
      Log.d("HexSelector", "String parsing died");
      localException.printStackTrace();
      this.txtError.setVisibility(0);
      return;
    }
    this.color = ((int)Long.parseLong((String)localObject2, 16));
    this.txtError.setVisibility(8);
    onColorChanged();
  }
  
  public static abstract interface OnColorChangedListener
  {
    public abstract void colorChanged(int paramInt);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\Theming\ColorPicker\HexSelectorView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */