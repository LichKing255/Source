package org.aspectj.lang.reflect;

public abstract interface CodeSignature
  extends MemberSignature
{
  public abstract Class[] getExceptionTypes();
  
  public abstract String[] getParameterNames();
  
  public abstract Class[] getParameterTypes();
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\lang\reflect\CodeSignature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */