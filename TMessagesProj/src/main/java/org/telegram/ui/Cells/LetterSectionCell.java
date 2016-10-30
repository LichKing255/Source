package org.telegram.ui.Cells;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Mihangram.Theming.MihanTheme;

public class LetterSectionCell
  extends FrameLayout
{
  private TextView textView;
  
  public LetterSectionCell(Context paramContext)
  {
    super(paramContext);
    setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(54.0F), AndroidUtilities.dp(64.0F)));
    this.textView = new TextView(getContext());
    this.textView.setTextSize(1, 22.0F);
    this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    paramContext = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    int i = paramContext.getInt("theme_contact_list_ncolor", MihanTheme.getDialogNameColor(paramContext));
    this.textView.setTextColor(i);
    this.textView.setGravity(17);
    addView(this.textView, LayoutHelper.createFrame(-1, -1.0F));
  }
  
  public void setCellHeight(int paramInt)
  {
    setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(54.0F), paramInt));
  }
  
  public void setLetter(String paramString)
  {
    this.textView.setText(paramString.toUpperCase());
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Cells\LetterSectionCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */