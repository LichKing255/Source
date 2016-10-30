package org.aspectj.runtime.internal.cflowstack;

public abstract interface ThreadStackFactory
{
  public abstract ThreadCounter getNewThreadCounter();
  
  public abstract ThreadStack getNewThreadStack();
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\runtime\internal\cflowstack\ThreadStackFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */