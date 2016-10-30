package org.aspectj.lang.reflect;

import org.aspectj.lang.Signature;

public abstract interface CatchClauseSignature
  extends Signature
{
  public abstract String getParameterName();
  
  public abstract Class getParameterType();
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\lang\reflect\CatchClauseSignature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */