package org.telegram.ui.Mihangram.UserChanges;

public class UpdateModel
{
  private String changeDate;
  private Long id;
  private boolean isNew;
  private String newValue;
  private String oldValue;
  private int type;
  private int userId;
  
  public UpdateModel() {}
  
  public UpdateModel(Long paramLong, int paramInt1, String paramString1, String paramString2, int paramInt2, boolean paramBoolean, String paramString3)
  {
    this.id = paramLong;
    this.type = paramInt1;
    this.oldValue = paramString1;
    this.newValue = paramString2;
    this.userId = paramInt2;
    this.isNew = paramBoolean;
    this.changeDate = paramString3;
  }
  
  public String getChangeDate()
  {
    return this.changeDate;
  }
  
  public Long getId()
  {
    return this.id;
  }
  
  public int getMessage()
  {
    if (this.type == 1)
    {
      if (this.newValue.equals("1")) {
        return 2131166832;
      }
      return 2131166831;
    }
    if (this.type == 2) {
      return 2131166827;
    }
    if (this.type == 3) {
      return 2131166829;
    }
    if (this.type == 4) {
      return 2131166828;
    }
    return 2131166826;
  }
  
  public String getNewValue()
  {
    return this.newValue;
  }
  
  public String getOldValue()
  {
    return this.oldValue;
  }
  
  public int getType()
  {
    return this.type;
  }
  
  public int getUserId()
  {
    return this.userId;
  }
  
  public boolean isNew()
  {
    return this.isNew;
  }
  
  public void setChangeDate(String paramString)
  {
    this.changeDate = paramString;
  }
  
  public void setId(Long paramLong)
  {
    this.id = paramLong;
  }
  
  public void setNew(boolean paramBoolean)
  {
    this.isNew = paramBoolean;
  }
  
  public void setNewValue(String paramString)
  {
    this.newValue = paramString;
  }
  
  public void setOldValue(String paramString)
  {
    this.oldValue = paramString;
  }
  
  public void setType(int paramInt)
  {
    this.type = paramInt;
  }
  
  public void setUserId(int paramInt)
  {
    this.userId = paramInt;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\UserChanges\UpdateModel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */