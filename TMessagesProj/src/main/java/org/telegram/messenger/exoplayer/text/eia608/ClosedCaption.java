package org.telegram.messenger.exoplayer.text.eia608;

abstract class ClosedCaption
{
  public static final int TYPE_CTRL = 0;
  public static final int TYPE_TEXT = 1;
  public final int type;
  
  protected ClosedCaption(int paramInt)
  {
    this.type = paramInt;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\text\eia608\ClosedCaption.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */