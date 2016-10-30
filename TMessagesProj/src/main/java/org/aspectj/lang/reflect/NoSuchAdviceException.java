package org.aspectj.lang.reflect;

public class NoSuchAdviceException
  extends Exception
{
  private static final long serialVersionUID = 3256444698657634352L;
  private String name;
  
  public NoSuchAdviceException(String paramString)
  {
    this.name = paramString;
  }
  
  public String getName()
  {
    return this.name;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\lang\reflect\NoSuchAdviceException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */