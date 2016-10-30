package org.telegram.messenger.exoplayer.metadata.id3;

public final class PrivFrame
  extends Id3Frame
{
  public static final String ID = "PRIV";
  public final String owner;
  public final byte[] privateData;
  
  public PrivFrame(String paramString, byte[] paramArrayOfByte)
  {
    super("PRIV");
    this.owner = paramString;
    this.privateData = paramArrayOfByte;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\metadata\id3\PrivFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */