package org.telegram.messenger.exoplayer;

public final class ExoPlaybackException
  extends Exception
{
  public final boolean caughtAtTopLevel;
  
  public ExoPlaybackException(String paramString)
  {
    super(paramString);
    this.caughtAtTopLevel = false;
  }
  
  public ExoPlaybackException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    this.caughtAtTopLevel = false;
  }
  
  public ExoPlaybackException(Throwable paramThrowable)
  {
    super(paramThrowable);
    this.caughtAtTopLevel = false;
  }
  
  ExoPlaybackException(Throwable paramThrowable, boolean paramBoolean)
  {
    super(paramThrowable);
    this.caughtAtTopLevel = paramBoolean;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\ExoPlaybackException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */