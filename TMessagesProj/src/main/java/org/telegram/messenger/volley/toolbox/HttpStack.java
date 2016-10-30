package org.telegram.messenger.volley.toolbox;

import java.io.IOException;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.telegram.messenger.volley.AuthFailureError;
import org.telegram.messenger.volley.Request;

public abstract interface HttpStack
{
  public abstract HttpResponse performRequest(Request<?> paramRequest, Map<String, String> paramMap)
    throws IOException, AuthFailureError;
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\volley\toolbox\HttpStack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */