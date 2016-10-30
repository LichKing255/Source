package org.telegram.messenger.exoplayer.metadata.id3;

public final class TxxxFrame
  extends Id3Frame
{
  public static final String ID = "TXXX";
  public final String description;
  public final String value;
  
  public TxxxFrame(String paramString1, String paramString2)
  {
    super("TXXX");
    this.description = paramString1;
    this.value = paramString2;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\metadata\id3\TxxxFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */