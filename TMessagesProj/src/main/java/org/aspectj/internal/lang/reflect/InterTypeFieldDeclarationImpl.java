package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.InterTypeFieldDeclaration;

public class InterTypeFieldDeclarationImpl
  extends InterTypeDeclarationImpl
  implements InterTypeFieldDeclaration
{
  private Type genericType;
  private String name;
  private AjType<?> type;
  
  public InterTypeFieldDeclarationImpl(AjType<?> paramAjType1, String paramString1, int paramInt, String paramString2, AjType<?> paramAjType2, Type paramType)
  {
    super(paramAjType1, paramString1, paramInt);
    this.name = paramString2;
    this.type = paramAjType2;
    this.genericType = paramType;
  }
  
  public InterTypeFieldDeclarationImpl(AjType<?> paramAjType1, AjType<?> paramAjType2, Field paramField)
  {
    super(paramAjType1, paramAjType2, paramField.getModifiers());
    this.name = paramField.getName();
    this.type = AjTypeSystem.getAjType(paramField.getType());
    paramAjType1 = paramField.getGenericType();
    if ((paramAjType1 instanceof Class))
    {
      this.genericType = AjTypeSystem.getAjType((Class)paramAjType1);
      return;
    }
    this.genericType = paramAjType1;
  }
  
  public Type getGenericType()
  {
    return this.genericType;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public AjType<?> getType()
  {
    return this.type;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(Modifier.toString(getModifiers()));
    localStringBuffer.append(" ");
    localStringBuffer.append(getType().toString());
    localStringBuffer.append(" ");
    localStringBuffer.append(this.targetTypeName);
    localStringBuffer.append(".");
    localStringBuffer.append(getName());
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\internal\lang\reflect\InterTypeFieldDeclarationImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */