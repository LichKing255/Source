package org.aspectj.runtime.internal.cflowstack;

import java.util.Stack;

public abstract interface ThreadStack
{
  public abstract Stack getThreadStack();
  
  public abstract void removeThreadStack();
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\runtime\internal\cflowstack\ThreadStack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */