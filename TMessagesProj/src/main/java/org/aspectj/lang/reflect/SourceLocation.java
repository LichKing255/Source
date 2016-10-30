package org.aspectj.lang.reflect;

public abstract interface SourceLocation
{
  public abstract int getColumn();
  
  public abstract String getFileName();
  
  public abstract int getLine();
  
  public abstract Class getWithinType();
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\lang\reflect\SourceLocation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */