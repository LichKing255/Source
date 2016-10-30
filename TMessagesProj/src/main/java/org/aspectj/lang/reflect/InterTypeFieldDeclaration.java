package org.aspectj.lang.reflect;

import java.lang.reflect.Type;

public abstract interface InterTypeFieldDeclaration
  extends InterTypeDeclaration
{
  public abstract Type getGenericType();
  
  public abstract String getName();
  
  public abstract AjType<?> getType();
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\lang\reflect\InterTypeFieldDeclaration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */