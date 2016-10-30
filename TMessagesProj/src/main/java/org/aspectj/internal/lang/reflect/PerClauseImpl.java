package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.PerClause;
import org.aspectj.lang.reflect.PerClauseKind;

public class PerClauseImpl
  implements PerClause
{
  private final PerClauseKind kind;
  
  protected PerClauseImpl(PerClauseKind paramPerClauseKind)
  {
    this.kind = paramPerClauseKind;
  }
  
  public PerClauseKind getKind()
  {
    return this.kind;
  }
  
  public String toString()
  {
    return "issingleton()";
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\internal\lang\reflect\PerClauseImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */