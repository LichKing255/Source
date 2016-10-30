package org.aspectj.lang.reflect;

import java.lang.reflect.Type;

public abstract interface InterTypeConstructorDeclaration
  extends InterTypeDeclaration
{
  public abstract AjType<?>[] getExceptionTypes();
  
  public abstract Type[] getGenericParameterTypes();
  
  public abstract AjType<?>[] getParameterTypes();
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\lang\reflect\InterTypeConstructorDeclaration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */