package org.telegram.messenger.exoplayer.upstream;

public abstract interface TransferListener
{
  public abstract void onBytesTransferred(int paramInt);
  
  public abstract void onTransferEnd();
  
  public abstract void onTransferStart();
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\upstream\TransferListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */