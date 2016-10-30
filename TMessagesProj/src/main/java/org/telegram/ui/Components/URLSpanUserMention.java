package org.telegram.ui.Components;

import android.text.TextPaint;

public class URLSpanUserMention
  extends URLSpanNoUnderline
{
  public URLSpanUserMention(String paramString)
  {
    super(paramString);
  }
  
  public void updateDrawState(TextPaint paramTextPaint)
  {
    super.updateDrawState(paramTextPaint);
    paramTextPaint.setColor(-14255946);
    paramTextPaint.setUnderlineText(false);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Components\URLSpanUserMention.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */