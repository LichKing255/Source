package org.aspectj.lang.reflect;

import java.lang.reflect.Type;

public abstract interface Advice
{
  public abstract AjType getDeclaringType();
  
  public abstract AjType<?>[] getExceptionTypes();
  
  public abstract Type[] getGenericParameterTypes();
  
  public abstract AdviceKind getKind();
  
  public abstract String getName();
  
  public abstract AjType<?>[] getParameterTypes();
  
  public abstract PointcutExpression getPointcutExpression();
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\lang\reflect\Advice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */