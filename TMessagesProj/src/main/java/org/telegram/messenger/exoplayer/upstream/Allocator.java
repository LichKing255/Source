package org.telegram.messenger.exoplayer.upstream;

public abstract interface Allocator
{
  public abstract Allocation allocate();
  
  public abstract void blockWhileTotalBytesAllocatedExceeds(int paramInt)
    throws InterruptedException;
  
  public abstract int getIndividualAllocationLength();
  
  public abstract int getTotalBytesAllocated();
  
  public abstract void release(Allocation paramAllocation);
  
  public abstract void trim(int paramInt);
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\upstream\Allocator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */