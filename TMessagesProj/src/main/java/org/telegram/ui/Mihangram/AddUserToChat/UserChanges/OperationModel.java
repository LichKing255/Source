package org.telegram.ui.Mihangram.AddUserToChat.UserChanges;

public class OperationModel
{
  private int date;
  private String operation;
  private int userId;
  
  public OperationModel(int paramInt1, String paramString, int paramInt2)
  {
    this.userId = paramInt1;
    this.operation = paramString;
    this.date = paramInt2;
  }
  
  public int getDate()
  {
    return this.date;
  }
  
  public String getOperation()
  {
    return this.operation;
  }
  
  public int getUser()
  {
    return this.userId;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\AddUserToChat\UserChanges\OperationModel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */