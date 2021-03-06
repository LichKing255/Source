package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.UnlockSignature;

class UnlockSignatureImpl
  extends SignatureImpl
  implements UnlockSignature
{
  private Class parameterType;
  
  UnlockSignatureImpl(Class paramClass)
  {
    super(8, "unlock", paramClass);
    this.parameterType = paramClass;
  }
  
  UnlockSignatureImpl(String paramString)
  {
    super(paramString);
  }
  
  protected String createToString(StringMaker paramStringMaker)
  {
    if (this.parameterType == null) {
      this.parameterType = extractType(3);
    }
    return "unlock(" + paramStringMaker.makeTypeName(this.parameterType) + ")";
  }
  
  public Class getParameterType()
  {
    if (this.parameterType == null) {
      this.parameterType = extractType(3);
    }
    return this.parameterType;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\runtime\reflect\UnlockSignatureImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */