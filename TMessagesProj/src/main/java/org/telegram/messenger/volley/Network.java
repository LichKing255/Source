package org.telegram.messenger.volley;

public abstract interface Network
{
  public abstract NetworkResponse performRequest(Request<?> paramRequest)
    throws VolleyError;
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\volley\Network.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */