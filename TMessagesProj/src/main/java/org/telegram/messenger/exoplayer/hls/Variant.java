package org.telegram.messenger.exoplayer.hls;

import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.chunk.FormatWrapper;

public final class Variant
  implements FormatWrapper
{
  public final Format format;
  public final String url;
  
  public Variant(String paramString, Format paramFormat)
  {
    this.url = paramString;
    this.format = paramFormat;
  }
  
  public Format getFormat()
  {
    return this.format;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\hls\Variant.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */