package org.telegram.messenger.exoplayer;

public abstract interface MediaCodecSelector
{
  public static final MediaCodecSelector DEFAULT = new MediaCodecSelector()
  {
    public DecoderInfo getDecoderInfo(String paramAnonymousString, boolean paramAnonymousBoolean)
      throws MediaCodecUtil.DecoderQueryException
    {
      return MediaCodecUtil.getDecoderInfo(paramAnonymousString, paramAnonymousBoolean);
    }
    
    public DecoderInfo getPassthroughDecoderInfo()
      throws MediaCodecUtil.DecoderQueryException
    {
      return MediaCodecUtil.getPassthroughDecoderInfo();
    }
  };
  
  public abstract DecoderInfo getDecoderInfo(String paramString, boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException;
  
  public abstract DecoderInfo getPassthroughDecoderInfo()
    throws MediaCodecUtil.DecoderQueryException;
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\MediaCodecSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */