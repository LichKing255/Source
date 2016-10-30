package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.MemberSignature;

abstract class MemberSignatureImpl
  extends SignatureImpl
  implements MemberSignature
{
  MemberSignatureImpl(int paramInt, String paramString, Class paramClass)
  {
    super(paramInt, paramString, paramClass);
  }
  
  public MemberSignatureImpl(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\runtime\reflect\MemberSignatureImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */