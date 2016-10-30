package org.telegram.ui.Mihangram.DownloadManager;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.R.styleable;

public class MultiStateToggleButton
  extends ToggleButton
{
  private static final String KEY_BUTTON_STATES = "button_states";
  private static final String KEY_INSTANCE_STATE = "instance_state";
  private static final String TAG = MultiStateToggleButton.class.getSimpleName();
  List<View> buttons;
  boolean mMultipleChoice = false;
  private LinearLayout mainLayout;
  CharSequence[] texts;
  
  public MultiStateToggleButton(Context paramContext)
  {
    super(paramContext, null);
  }
  
  public MultiStateToggleButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.MultiStateToggleButton, 0, 0);
    try
    {
      paramAttributeSet = paramContext.getTextArray(0);
      this.colorPressed = paramContext.getColor(1, 0);
      this.colorNotPressed = paramContext.getColor(2, 0);
      this.colorPressedText = paramContext.getColor(3, 0);
      this.colorPressedBackground = paramContext.getColor(4, 0);
      this.pressedBackgroundResource = paramContext.getResourceId(5, 0);
      this.colorNotPressedText = paramContext.getColor(6, 0);
      this.colorNotPressedBackground = paramContext.getColor(7, 0);
      this.notPressedBackgroundResource = paramContext.getResourceId(8, 0);
      setElements(paramAttributeSet, null, new boolean[paramAttributeSet.length]);
      return;
    }
    finally
    {
      paramContext.recycle();
    }
  }
  
  private void refresh()
  {
    boolean[] arrayOfBoolean = getStates();
    int i = 0;
    while (i < arrayOfBoolean.length)
    {
      setButtonState((View)this.buttons.get(i), arrayOfBoolean[i]);
      i += 1;
    }
  }
  
  public void enableMultipleChoice(boolean paramBoolean)
  {
    this.mMultipleChoice = paramBoolean;
  }
  
  public boolean[] getStates()
  {
    if (this.buttons == null) {}
    boolean[] arrayOfBoolean;
    for (int i = 0;; i = this.buttons.size())
    {
      arrayOfBoolean = new boolean[i];
      int j = 0;
      while (j < i)
      {
        arrayOfBoolean[j] = ((View)this.buttons.get(j)).isSelected();
        j += 1;
      }
    }
    return arrayOfBoolean;
  }
  
  public CharSequence[] getTexts()
  {
    return this.texts;
  }
  
  public int getValue()
  {
    int i = 0;
    while (i < this.buttons.size())
    {
      if (((View)this.buttons.get(i)).isSelected()) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    Parcelable localParcelable = paramParcelable;
    if ((paramParcelable instanceof Bundle))
    {
      paramParcelable = (Bundle)paramParcelable;
      setStates(paramParcelable.getBooleanArray("button_states"));
      localParcelable = paramParcelable.getParcelable("instance_state");
    }
    super.onRestoreInstanceState(localParcelable);
  }
  
  public Parcelable onSaveInstanceState()
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("instance_state", super.onSaveInstanceState());
    localBundle.putBooleanArray("button_states", getStates());
    return localBundle;
  }
  
  public void setButtonState(View paramView, boolean paramBoolean)
  {
    if (paramView == null) {
      return;
    }
    paramView.setSelected(paramBoolean);
    label17:
    label45:
    label50:
    label64:
    AppCompatButton localAppCompatButton;
    if (paramBoolean)
    {
      i = 2130837662;
      paramView.setBackgroundResource(i);
      if ((this.colorNotPressed == 0) && (this.colorPressed == 0)) {
        break label154;
      }
      if (!paramBoolean) {
        break label146;
      }
      i = this.colorPressed;
      paramView.setBackgroundColor(i);
      if (!(paramView instanceof Button)) {
        break label183;
      }
      if (!paramBoolean) {
        break label193;
      }
      i = 2131362135;
      ((AppCompatButton)paramView).setTextAppearance(getContext(), i);
      if ((this.colorPressed == 0) && (this.colorNotPressed == 0)) {
        break label207;
      }
      localAppCompatButton = (AppCompatButton)paramView;
      if (paramBoolean) {
        break label199;
      }
      i = this.colorPressed;
      label105:
      localAppCompatButton.setTextColor(i);
      label111:
      if ((this.pressedBackgroundResource == 0) && (this.notPressedBackgroundResource == 0)) {
        break label243;
      }
      if (!paramBoolean) {
        break label253;
      }
    }
    label146:
    label154:
    label183:
    label193:
    label199:
    label207:
    label243:
    label253:
    for (int i = this.pressedBackgroundResource;; i = this.notPressedBackgroundResource)
    {
      paramView.setBackgroundResource(i);
      return;
      i = 2130837661;
      break label17;
      i = this.colorNotPressed;
      break label45;
      if ((this.colorNotPressedBackground == 0) && (this.colorNotPressedBackground == 0)) {
        break label50;
      }
      if (paramBoolean) {}
      for (i = this.colorPressedBackground;; i = this.colorNotPressedBackground)
      {
        paramView.setBackgroundColor(i);
        break label50;
        break;
      }
      i = 2131362067;
      break label64;
      i = this.colorNotPressed;
      break label105;
      if ((this.colorPressedText == 0) && (this.colorNotPressedText == 0)) {
        break label111;
      }
      localAppCompatButton = (AppCompatButton)paramView;
      if (paramBoolean) {}
      for (i = this.colorPressedText;; i = this.colorNotPressedText)
      {
        localAppCompatButton.setTextColor(i);
        break label111;
        break;
      }
    }
  }
  
  public void setButtons(View[] paramArrayOfView, boolean[] paramArrayOfBoolean)
  {
    int k = paramArrayOfView.length;
    if (k == 0) {
      throw new IllegalArgumentException("neither texts nor images are setup");
    }
    int i = 1;
    if ((paramArrayOfBoolean == null) || (k != paramArrayOfBoolean.length))
    {
      Log.d(TAG, "Invalid selection array");
      i = 0;
    }
    setOrientation(0);
    setGravity(16);
    Object localObject = (LayoutInflater)this.context.getSystemService("layout_inflater");
    if (this.mainLayout == null) {
      this.mainLayout = ((LinearLayout)((LayoutInflater)localObject).inflate(2130903127, this, true));
    }
    this.mainLayout.removeAllViews();
    this.buttons = new ArrayList();
    final int j = 0;
    while (j < k)
    {
      localObject = paramArrayOfView[j];
      ((View)localObject).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          MultiStateToggleButton.this.setValue(j);
        }
      });
      this.mainLayout.addView((View)localObject);
      if (i != 0) {
        setButtonState((View)localObject, paramArrayOfBoolean[j]);
      }
      this.buttons.add(localObject);
      j += 1;
    }
    this.mainLayout.setBackgroundResource(2130837663);
  }
  
  public void setColors(int paramInt1, int paramInt2)
  {
    super.setColors(paramInt1, paramInt2);
    refresh();
  }
  
  public void setElements(int paramInt1, int paramInt2)
  {
    String[] arrayOfString = getResources().getStringArray(paramInt1);
    if (arrayOfString == null) {}
    for (paramInt1 = 0;; paramInt1 = arrayOfString.length)
    {
      boolean[] arrayOfBoolean = new boolean[paramInt1];
      if ((paramInt2 >= 0) && (paramInt2 < paramInt1)) {
        arrayOfBoolean[paramInt2] = true;
      }
      setElements(arrayOfString, null, arrayOfBoolean);
      return;
    }
  }
  
  public void setElements(int paramInt, boolean[] paramArrayOfBoolean)
  {
    setElements(getResources().getStringArray(paramInt), null, paramArrayOfBoolean);
  }
  
  public void setElements(List<?> paramList)
  {
    if (paramList == null) {}
    for (int i = 0;; i = paramList.size())
    {
      setElements(paramList, new boolean[i]);
      return;
    }
  }
  
  public void setElements(List<?> paramList, Object paramObject)
  {
    int j = 0;
    int i = -1;
    if (paramList != null)
    {
      j = paramList.size();
      i = paramList.indexOf(paramObject);
    }
    paramObject = new boolean[j];
    if ((i != -1) && (i < j)) {
      paramObject[i] = 1;
    }
    setElements(paramList, (boolean[])paramObject);
  }
  
  public void setElements(List<?> paramList, boolean[] paramArrayOfBoolean)
  {
    Object localObject = paramList;
    if (paramList == null) {
      localObject = new ArrayList(0);
    }
    setElements((CharSequence[])((List)localObject).toArray(new String[((List)localObject).size()]), null, paramArrayOfBoolean);
  }
  
  public void setElements(CharSequence[] paramArrayOfCharSequence)
  {
    if (paramArrayOfCharSequence == null) {}
    for (int i = 0;; i = paramArrayOfCharSequence.length)
    {
      setElements(paramArrayOfCharSequence, null, new boolean[i]);
      return;
    }
  }
  
  public void setElements(CharSequence[] paramArrayOfCharSequence, int[] paramArrayOfInt, boolean[] paramArrayOfBoolean)
  {
    this.texts = paramArrayOfCharSequence;
    if (paramArrayOfCharSequence != null)
    {
      i = paramArrayOfCharSequence.length;
      if (paramArrayOfInt == null) {
        break label51;
      }
    }
    int k;
    label51:
    for (final int j = paramArrayOfInt.length;; j = 0)
    {
      k = Math.max(i, j);
      if (k != 0) {
        break label57;
      }
      throw new IllegalArgumentException("neither texts nor images are setup");
      i = 0;
      break;
    }
    label57:
    int i = 1;
    if ((paramArrayOfBoolean == null) || (k != paramArrayOfBoolean.length))
    {
      Log.d(TAG, "Invalid selection array");
      i = 0;
    }
    setOrientation(0);
    setGravity(16);
    LayoutInflater localLayoutInflater = (LayoutInflater)this.context.getSystemService("layout_inflater");
    if (this.mainLayout == null) {
      this.mainLayout = ((LinearLayout)localLayoutInflater.inflate(2130903127, this, true));
    }
    this.mainLayout.removeAllViews();
    this.buttons = new ArrayList(k);
    j = 0;
    if (j < k)
    {
      Button localButton;
      if (j == 0) {
        if (k == 1)
        {
          localButton = (Button)localLayoutInflater.inflate(2130903129, this.mainLayout, false);
          label190:
          if (paramArrayOfCharSequence == null) {
            break label362;
          }
        }
      }
      label362:
      for (Object localObject = paramArrayOfCharSequence[j];; localObject = "")
      {
        localButton.setText((CharSequence)localObject);
        if ((paramArrayOfInt != null) && (paramArrayOfInt[j] != 0)) {
          localButton.setCompoundDrawablesWithIntrinsicBounds(paramArrayOfInt[j], 0, 0, 0);
        }
        localButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            MultiStateToggleButton.this.setValue(j);
          }
        });
        this.mainLayout.addView(localButton);
        if (i != 0) {
          setButtonState(localButton, paramArrayOfBoolean[j]);
        }
        this.buttons.add(localButton);
        j += 1;
        break;
        localButton = (Button)localLayoutInflater.inflate(2130903126, this.mainLayout, false);
        break label190;
        if (j == k - 1)
        {
          localButton = (Button)localLayoutInflater.inflate(2130903128, this.mainLayout, false);
          break label190;
        }
        localButton = (Button)localLayoutInflater.inflate(2130903125, this.mainLayout, false);
        break label190;
      }
    }
    this.mainLayout.setBackgroundResource(2130837663);
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    int i = 0;
    while (i < getChildCount())
    {
      getChildAt(i).setEnabled(paramBoolean);
      i += 1;
    }
  }
  
  public void setStates(boolean[] paramArrayOfBoolean)
  {
    if ((this.buttons == null) || (paramArrayOfBoolean == null) || (this.buttons.size() != paramArrayOfBoolean.length)) {}
    for (;;)
    {
      return;
      int i = 0;
      Iterator localIterator = this.buttons.iterator();
      while (localIterator.hasNext())
      {
        setButtonState((View)localIterator.next(), paramArrayOfBoolean[i]);
        i += 1;
      }
    }
  }
  
  public void setValue(int paramInt)
  {
    int i = 0;
    if (i < this.buttons.size())
    {
      boolean bool;
      if (this.mMultipleChoice) {
        if (i == paramInt)
        {
          View localView = (View)this.buttons.get(i);
          if (localView != null)
          {
            if (localView.isSelected()) {
              break label71;
            }
            bool = true;
            label57:
            setButtonState(localView, bool);
          }
        }
      }
      for (;;)
      {
        i += 1;
        break;
        label71:
        bool = false;
        break label57;
        if (i == paramInt) {
          setButtonState((View)this.buttons.get(i), true);
        } else if (!this.mMultipleChoice) {
          setButtonState((View)this.buttons.get(i), false);
        }
      }
    }
    super.setValue(paramInt);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\DownloadManager\MultiStateToggleButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */