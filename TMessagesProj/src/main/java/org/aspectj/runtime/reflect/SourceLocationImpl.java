package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.SourceLocation;

class SourceLocationImpl
  implements SourceLocation
{
  String fileName;
  int line;
  Class withinType;
  
  SourceLocationImpl(Class paramClass, String paramString, int paramInt)
  {
    this.withinType = paramClass;
    this.fileName = paramString;
    this.line = paramInt;
  }
  
  public int getColumn()
  {
    return -1;
  }
  
  public String getFileName()
  {
    return this.fileName;
  }
  
  public int getLine()
  {
    return this.line;
  }
  
  public Class getWithinType()
  {
    return this.withinType;
  }
  
  public String toString()
  {
    return getFileName() + ":" + getLine();
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\runtime\reflect\SourceLocationImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */