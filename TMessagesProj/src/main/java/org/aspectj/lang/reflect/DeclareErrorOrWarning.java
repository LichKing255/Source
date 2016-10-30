package org.aspectj.lang.reflect;

public abstract interface DeclareErrorOrWarning
{
  public abstract AjType getDeclaringType();
  
  public abstract String getMessage();
  
  public abstract PointcutExpression getPointcutExpression();
  
  public abstract boolean isError();
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\lang\reflect\DeclareErrorOrWarning.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */