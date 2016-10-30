package org.telegram.messenger.exoplayer.extractor;

import org.telegram.messenger.exoplayer.drm.DrmInitData;

public abstract interface ExtractorOutput
{
  public abstract void drmInitData(DrmInitData paramDrmInitData);
  
  public abstract void endTracks();
  
  public abstract void seekMap(SeekMap paramSeekMap);
  
  public abstract TrackOutput track(int paramInt);
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\extractor\ExtractorOutput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */