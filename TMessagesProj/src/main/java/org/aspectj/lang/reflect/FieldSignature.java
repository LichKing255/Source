package org.aspectj.lang.reflect;

import java.lang.reflect.Field;

public abstract interface FieldSignature
  extends MemberSignature
{
  public abstract Field getField();
  
  public abstract Class getFieldType();
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\lang\reflect\FieldSignature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */