package org.telegram.messenger.exoplayer.text.eia608;

final class ClosedCaptionList
  implements Comparable<ClosedCaptionList>
{
  public final ClosedCaption[] captions;
  public final boolean decodeOnly;
  public final long timeUs;
  
  public ClosedCaptionList(long paramLong, boolean paramBoolean, ClosedCaption[] paramArrayOfClosedCaption)
  {
    this.timeUs = paramLong;
    this.decodeOnly = paramBoolean;
    this.captions = paramArrayOfClosedCaption;
  }
  
  public int compareTo(ClosedCaptionList paramClosedCaptionList)
  {
    long l = this.timeUs - paramClosedCaptionList.timeUs;
    if (l == 0L) {
      return 0;
    }
    if (l > 0L) {
      return 1;
    }
    return -1;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\text\eia608\ClosedCaptionList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */