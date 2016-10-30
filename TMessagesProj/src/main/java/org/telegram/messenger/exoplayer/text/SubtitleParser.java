package org.telegram.messenger.exoplayer.text;

import org.telegram.messenger.exoplayer.ParserException;

public abstract interface SubtitleParser
{
  public abstract boolean canParse(String paramString);
  
  public abstract Subtitle parse(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ParserException;
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\text\SubtitleParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */