package org.aspectj.lang.reflect;

import java.lang.reflect.Method;

public abstract interface AdviceSignature
  extends CodeSignature
{
  public abstract Method getAdvice();
  
  public abstract Class getReturnType();
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\lang\reflect\AdviceSignature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */