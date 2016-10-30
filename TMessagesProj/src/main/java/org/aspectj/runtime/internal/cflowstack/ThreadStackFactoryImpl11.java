package org.aspectj.runtime.internal.cflowstack;

public class ThreadStackFactoryImpl11
  implements ThreadStackFactory
{
  public ThreadCounter getNewThreadCounter()
  {
    return new ThreadCounterImpl11();
  }
  
  public ThreadStack getNewThreadStack()
  {
    return new ThreadStackImpl11();
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\runtime\internal\cflowstack\ThreadStackFactoryImpl11.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */