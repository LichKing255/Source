package org.telegram.ui.Supergram.DownloadManager.SQLite;

public class ElementDownload
{
  public String access_hash = "";
  public boolean check = true;
  public String date = "";
  public int dc_id = 0;
  public int duration = 0;
  public int f6327h = 0;
  public int f6328w = 0;
  public String file_name = "";
  public String id = "";
  public String mime_type = "";
  public float prog = 0.0F;
  public String real_name = "";
  public int size = 0;
  public boolean state = false;
  public int type = -1;
  public int user_id = 0;
  
  public ElementDownload() {}
  
  public ElementDownload(String paramString1, String paramString2, String paramString3, int paramInt1, String paramString4, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, float paramFloat, boolean paramBoolean)
  {
    this.id = paramString1;
    this.access_hash = paramString2;
    this.date = paramString3;
    this.duration = paramInt1;
    this.mime_type = paramString4;
    this.size = paramInt2;
    this.dc_id = paramInt3;
    this.f6328w = paramInt4;
    this.f6327h = paramInt5;
    this.user_id = paramInt6;
    this.prog = paramFloat;
    this.check = paramBoolean;
  }
  
  public String getAccess_hash()
  {
    return this.access_hash;
  }
  
  public String getDate()
  {
    return this.date;
  }
  
  public int getDc_id()
  {
    return this.dc_id;
  }
  
  public int getDuration()
  {
    return this.duration;
  }
  
  public String getFile_name()
  {
    return this.file_name;
  }
  
  public int getH()
  {
    return this.f6327h;
  }
  
  public String getId()
  {
    return this.id;
  }
  
  public String getMime_type()
  {
    return this.mime_type;
  }
  
  public float getProg()
  {
    return this.prog;
  }
  
  public String getReal_name()
  {
    return this.real_name;
  }
  
  public int getSize()
  {
    return this.size;
  }
  
  public int getType()
  {
    return this.type;
  }
  
  public int getUser_id()
  {
    return this.user_id;
  }
  
  public int getW()
  {
    return this.f6328w;
  }
  
  public boolean isCheck()
  {
    return this.check;
  }
  
  public boolean isState()
  {
    return this.state;
  }
  
  public void setAccess_hash(String paramString)
  {
    this.access_hash = paramString;
  }
  
  public void setCheck(boolean paramBoolean)
  {
    this.check = paramBoolean;
  }
  
  public void setDate(String paramString)
  {
    this.date = paramString;
  }
  
  public void setDc_id(int paramInt)
  {
    this.dc_id = paramInt;
  }
  
  public void setDuration(int paramInt)
  {
    this.duration = paramInt;
  }
  
  public void setFile_name(String paramString)
  {
    this.file_name = paramString;
  }
  
  public void setH(int paramInt)
  {
    this.f6327h = paramInt;
  }
  
  public void setId(String paramString)
  {
    this.id = paramString;
  }
  
  public void setMime_type(String paramString)
  {
    this.mime_type = paramString;
  }
  
  public void setProg(float paramFloat)
  {
    this.prog = paramFloat;
  }
  
  public void setReal_name(String paramString)
  {
    this.real_name = paramString;
  }
  
  public void setSize(int paramInt)
  {
    this.size = paramInt;
  }
  
  public void setState(boolean paramBoolean)
  {
    this.state = paramBoolean;
  }
  
  public void setType(int paramInt)
  {
    this.type = paramInt;
  }
  
  public void setUser_id(int paramInt)
  {
    this.user_id = paramInt;
  }
  
  public void setW(int paramInt)
  {
    this.f6328w = paramInt;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\DownloadManager\SQLite\ElementDownload.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */