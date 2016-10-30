package org.telegram.ui.Mihangram.UserChanges;

public class AlarmResponse
{
  private Integer displayCount;
  private Boolean exitOnDismiss;
  private Long id;
  private String imageUrl;
  private String message;
  private String negativeBtnAction;
  private String negativeBtnText;
  private String negativeBtnUrl;
  private String positiveBtnAction;
  private String positiveBtnText;
  private String positiveBtnUrl;
  private Integer showCount;
  private Integer targetNetwork;
  private Integer targetVersion;
  private String title;
  
  public AlarmResponse(Long paramLong, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, Integer paramInteger1, Boolean paramBoolean, Integer paramInteger2, Integer paramInteger3, Integer paramInteger4)
  {
    this.id = paramLong;
    this.title = paramString1;
    this.message = paramString2;
    this.imageUrl = paramString3;
    this.positiveBtnText = paramString4;
    this.positiveBtnAction = paramString5;
    this.positiveBtnUrl = paramString6;
    this.negativeBtnText = paramString7;
    this.negativeBtnAction = paramString8;
    this.negativeBtnUrl = paramString9;
    this.showCount = paramInteger1;
    this.exitOnDismiss = paramBoolean;
    this.targetNetwork = paramInteger2;
    this.displayCount = paramInteger3;
    this.targetVersion = paramInteger4;
  }
  
  public Integer getDisplayCount()
  {
    return this.displayCount;
  }
  
  public Boolean getExitOnDismiss()
  {
    return this.exitOnDismiss;
  }
  
  public Long getId()
  {
    return this.id;
  }
  
  public String getImageUrl()
  {
    return this.imageUrl;
  }
  
  public String getMessage()
  {
    return this.message;
  }
  
  public String getNegativeBtnAction()
  {
    return this.negativeBtnAction;
  }
  
  public String getNegativeBtnText()
  {
    return this.negativeBtnText;
  }
  
  public String getNegativeBtnUrl()
  {
    return this.negativeBtnUrl;
  }
  
  public String getPositiveBtnAction()
  {
    return this.positiveBtnAction;
  }
  
  public String getPositiveBtnText()
  {
    return this.positiveBtnText;
  }
  
  public String getPositiveBtnUrl()
  {
    return this.positiveBtnUrl;
  }
  
  public Integer getShowCount()
  {
    return this.showCount;
  }
  
  public Integer getTargetNetwork()
  {
    return this.targetNetwork;
  }
  
  public Integer getTargetVersion()
  {
    return this.targetVersion;
  }
  
  public String getTitle()
  {
    return this.title;
  }
  
  public void setDisplayCount(Integer paramInteger)
  {
    this.displayCount = paramInteger;
  }
  
  public void setExitOnDismiss(Boolean paramBoolean)
  {
    this.exitOnDismiss = paramBoolean;
  }
  
  public void setId(Long paramLong)
  {
    this.id = paramLong;
  }
  
  public void setImageUrl(String paramString)
  {
    this.imageUrl = paramString;
  }
  
  public void setMessage(String paramString)
  {
    this.message = paramString;
  }
  
  public void setNegativeBtnAction(String paramString)
  {
    this.negativeBtnAction = paramString;
  }
  
  public void setNegativeBtnText(String paramString)
  {
    this.negativeBtnText = paramString;
  }
  
  public void setNegativeBtnUrl(String paramString)
  {
    this.negativeBtnUrl = paramString;
  }
  
  public void setPositiveBtnAction(String paramString)
  {
    this.positiveBtnAction = paramString;
  }
  
  public void setPositiveBtnText(String paramString)
  {
    this.positiveBtnText = paramString;
  }
  
  public void setPositiveBtnUrl(String paramString)
  {
    this.positiveBtnUrl = paramString;
  }
  
  public void setShowCount(Integer paramInteger)
  {
    this.showCount = paramInteger;
  }
  
  public void setTargetNetwork(Integer paramInteger)
  {
    this.targetNetwork = paramInteger;
  }
  
  public void setTargetVersion(Integer paramInteger)
  {
    this.targetVersion = paramInteger;
  }
  
  public void setTitle(String paramString)
  {
    this.title = paramString;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\UserChanges\AlarmResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */