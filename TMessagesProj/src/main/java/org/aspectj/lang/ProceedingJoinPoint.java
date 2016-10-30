package org.aspectj.lang;

import org.aspectj.runtime.internal.AroundClosure;

public abstract interface ProceedingJoinPoint
  extends JoinPoint
{
  public abstract Object proceed()
    throws Throwable;
  
  public abstract Object proceed(Object[] paramArrayOfObject)
    throws Throwable;
  
  public abstract void set$AroundClosure(AroundClosure paramAroundClosure);
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\lang\ProceedingJoinPoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */