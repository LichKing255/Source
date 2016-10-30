package org.telegram.messenger.exoplayer.text.tx3g;

import org.telegram.messenger.exoplayer.text.Cue;
import org.telegram.messenger.exoplayer.text.Subtitle;
import org.telegram.messenger.exoplayer.text.SubtitleParser;

public final class Tx3gParser
  implements SubtitleParser
{
  public boolean canParse(String paramString)
  {
    return "application/x-quicktime-tx3g".equals(paramString);
  }
  
  public Subtitle parse(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new Tx3gSubtitle(new Cue(new String(paramArrayOfByte, paramInt1, paramInt2)));
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\text\tx3g\Tx3gParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */