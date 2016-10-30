package org.telegram.messenger.exoplayer.text.ttml;

final class TtmlRegion
{
  public final float line;
  public final float position;
  public final float width;
  
  public TtmlRegion()
  {
    this(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
  }
  
  public TtmlRegion(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.position = paramFloat1;
    this.line = paramFloat2;
    this.width = paramFloat3;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\text\ttml\TtmlRegion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */