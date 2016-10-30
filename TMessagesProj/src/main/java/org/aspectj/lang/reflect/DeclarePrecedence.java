package org.aspectj.lang.reflect;

public abstract interface DeclarePrecedence
{
  public abstract AjType getDeclaringType();
  
  public abstract TypePattern[] getPrecedenceOrder();
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\lang\reflect\DeclarePrecedence.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */