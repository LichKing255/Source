package org.telegram.messenger.exoplayer.hls;

import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.chunk.MediaChunk;
import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.upstream.DataSpec;

public final class TsChunk
  extends MediaChunk
{
  private int bytesLoaded;
  public final int discontinuitySequenceNumber;
  public final HlsExtractorWrapper extractorWrapper;
  private final boolean isEncrypted;
  private volatile boolean loadCanceled;
  
  public TsChunk(DataSource paramDataSource, DataSpec paramDataSpec, int paramInt1, Format paramFormat, long paramLong1, long paramLong2, int paramInt2, int paramInt3, HlsExtractorWrapper paramHlsExtractorWrapper, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    super(buildDataSource(paramDataSource, paramArrayOfByte1, paramArrayOfByte2), paramDataSpec, paramInt1, paramFormat, paramLong1, paramLong2, paramInt2);
    this.discontinuitySequenceNumber = paramInt3;
    this.extractorWrapper = paramHlsExtractorWrapper;
    this.isEncrypted = (this.dataSource instanceof Aes128DataSource);
  }
  
  private static DataSource buildDataSource(DataSource paramDataSource, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if ((paramArrayOfByte1 == null) || (paramArrayOfByte2 == null)) {
      return paramDataSource;
    }
    return new Aes128DataSource(paramDataSource, paramArrayOfByte1, paramArrayOfByte2);
  }
  
  public long bytesLoaded()
  {
    return this.bytesLoaded;
  }
  
  public void cancelLoad()
  {
    this.loadCanceled = true;
  }
  
  public boolean isLoadCanceled()
  {
    return this.loadCanceled;
  }
  
  /* Error */
  public void load()
    throws java.io.IOException, java.lang.InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 33	org/telegram/messenger/exoplayer/hls/TsChunk:isEncrypted	Z
    //   4: ifeq +87 -> 91
    //   7: aload_0
    //   8: getfield 55	org/telegram/messenger/exoplayer/hls/TsChunk:dataSpec	Lorg/telegram/messenger/exoplayer/upstream/DataSpec;
    //   11: astore_2
    //   12: aload_0
    //   13: getfield 40	org/telegram/messenger/exoplayer/hls/TsChunk:bytesLoaded	I
    //   16: ifeq +70 -> 86
    //   19: iconst_1
    //   20: istore_1
    //   21: new 57	org/telegram/messenger/exoplayer/extractor/DefaultExtractorInput
    //   24: dup
    //   25: aload_0
    //   26: getfield 29	org/telegram/messenger/exoplayer/hls/TsChunk:dataSource	Lorg/telegram/messenger/exoplayer/upstream/DataSource;
    //   29: aload_2
    //   30: getfield 63	org/telegram/messenger/exoplayer/upstream/DataSpec:absoluteStreamPosition	J
    //   33: aload_0
    //   34: getfield 29	org/telegram/messenger/exoplayer/hls/TsChunk:dataSource	Lorg/telegram/messenger/exoplayer/upstream/DataSource;
    //   37: aload_2
    //   38: invokeinterface 69 2 0
    //   43: invokespecial 72	org/telegram/messenger/exoplayer/extractor/DefaultExtractorInput:<init>	(Lorg/telegram/messenger/exoplayer/upstream/DataSource;JJ)V
    //   46: astore_2
    //   47: iload_1
    //   48: ifeq +13 -> 61
    //   51: aload_2
    //   52: aload_0
    //   53: getfield 40	org/telegram/messenger/exoplayer/hls/TsChunk:bytesLoaded	I
    //   56: invokeinterface 78 2 0
    //   61: iconst_0
    //   62: istore_1
    //   63: iload_1
    //   64: ifne +44 -> 108
    //   67: aload_0
    //   68: getfield 44	org/telegram/messenger/exoplayer/hls/TsChunk:loadCanceled	Z
    //   71: ifne +37 -> 108
    //   74: aload_0
    //   75: getfield 25	org/telegram/messenger/exoplayer/hls/TsChunk:extractorWrapper	Lorg/telegram/messenger/exoplayer/hls/HlsExtractorWrapper;
    //   78: aload_2
    //   79: invokevirtual 84	org/telegram/messenger/exoplayer/hls/HlsExtractorWrapper:read	(Lorg/telegram/messenger/exoplayer/extractor/ExtractorInput;)I
    //   82: istore_1
    //   83: goto -20 -> 63
    //   86: iconst_0
    //   87: istore_1
    //   88: goto -67 -> 21
    //   91: aload_0
    //   92: getfield 55	org/telegram/messenger/exoplayer/hls/TsChunk:dataSpec	Lorg/telegram/messenger/exoplayer/upstream/DataSpec;
    //   95: aload_0
    //   96: getfield 40	org/telegram/messenger/exoplayer/hls/TsChunk:bytesLoaded	I
    //   99: invokestatic 90	org/telegram/messenger/exoplayer/util/Util:getRemainderDataSpec	(Lorg/telegram/messenger/exoplayer/upstream/DataSpec;I)Lorg/telegram/messenger/exoplayer/upstream/DataSpec;
    //   102: astore_2
    //   103: iconst_0
    //   104: istore_1
    //   105: goto -84 -> 21
    //   108: aload_0
    //   109: aload_2
    //   110: invokeinterface 93 1 0
    //   115: aload_0
    //   116: getfield 55	org/telegram/messenger/exoplayer/hls/TsChunk:dataSpec	Lorg/telegram/messenger/exoplayer/upstream/DataSpec;
    //   119: getfield 63	org/telegram/messenger/exoplayer/upstream/DataSpec:absoluteStreamPosition	J
    //   122: lsub
    //   123: l2i
    //   124: putfield 40	org/telegram/messenger/exoplayer/hls/TsChunk:bytesLoaded	I
    //   127: aload_0
    //   128: getfield 29	org/telegram/messenger/exoplayer/hls/TsChunk:dataSource	Lorg/telegram/messenger/exoplayer/upstream/DataSource;
    //   131: invokeinterface 96 1 0
    //   136: return
    //   137: astore_3
    //   138: aload_0
    //   139: aload_2
    //   140: invokeinterface 93 1 0
    //   145: aload_0
    //   146: getfield 55	org/telegram/messenger/exoplayer/hls/TsChunk:dataSpec	Lorg/telegram/messenger/exoplayer/upstream/DataSpec;
    //   149: getfield 63	org/telegram/messenger/exoplayer/upstream/DataSpec:absoluteStreamPosition	J
    //   152: lsub
    //   153: l2i
    //   154: putfield 40	org/telegram/messenger/exoplayer/hls/TsChunk:bytesLoaded	I
    //   157: aload_3
    //   158: athrow
    //   159: astore_2
    //   160: aload_0
    //   161: getfield 29	org/telegram/messenger/exoplayer/hls/TsChunk:dataSource	Lorg/telegram/messenger/exoplayer/upstream/DataSource;
    //   164: invokeinterface 96 1 0
    //   169: aload_2
    //   170: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	171	0	this	TsChunk
    //   20	85	1	i	int
    //   11	129	2	localObject1	Object
    //   159	11	2	localObject2	Object
    //   137	21	3	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   67	83	137	finally
    //   21	47	159	finally
    //   51	61	159	finally
    //   108	127	159	finally
    //   138	159	159	finally
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\hls\TsChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */