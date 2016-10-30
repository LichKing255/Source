package org.aspectj.lang.reflect;

public abstract interface Pointcut
{
  public abstract AjType getDeclaringType();
  
  public abstract int getModifiers();
  
  public abstract String getName();
  
  public abstract String[] getParameterNames();
  
  public abstract AjType<?>[] getParameterTypes();
  
  public abstract PointcutExpression getPointcutExpression();
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\lang\reflect\Pointcut.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */