package org.telegram.ui.Supergram.DownloadManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.FileDownloadProgressListener;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.ui.Supergram.DownloadManager.Adapter.RVAdapter;
import org.telegram.ui.Supergram.DownloadManager.Adapter.RVAdapter.OnCheckedChangeListener;
import org.telegram.ui.Supergram.DownloadManager.Adapter.RVAdapter.OnItemClickListener;
import org.telegram.ui.Supergram.DownloadManager.SQLite.ElementDownload;
import org.telegram.ui.Supergram.DownloadManager.SQLite.SQLDownload;
import org.telegram.ui.Supergram.Theming.MihanTheme;

public class Download
  extends AppCompatActivity
  implements MediaController.FileDownloadProgressListener
{
  public static final int MEDIA_DIR_VIDEO = 2;
  public static Download download;
  private int TAG;
  public ElementDownload VD;
  final SQLDownload db = new SQLDownload(this);
  private List elementDownloadList;
  Float f6358x;
  LinearLayoutManager llm;
  private RVAdapter mAdapter;
  String name = "";
  float percent = 0.0F;
  private ArrayList prog;
  private Toolbar toolbar;
  
  private List getDownloadArray()
  {
    return new SQLDownload(this).getAllVideoInDownloadE();
  }
  
  public int getObserverTag()
  {
    return this.TAG;
  }
  
  public void mStart()
  {
    this.elementDownloadList = new SQLDownload(this).getAllVideoInDownloadM();
    int i = 0;
    if (i < this.elementDownloadList.size())
    {
      Object localObject = (ElementDownload)this.elementDownloadList.get(i);
      if (((ElementDownload)localObject).getType() == 3) {
        if (new File(FileLoader.getInstance().getDirectory(2), ((ElementDownload)this.elementDownloadList.get(i)).getReal_name()).exists())
        {
          this.db.updatedetails(((ElementDownload)this.elementDownloadList.get(i)).getId(), String.valueOf(1));
          this.db.updatestate(((ElementDownload)this.elementDownloadList.get(i)).getId(), 0);
          if (this.mAdapter != null)
          {
            ((ElementDownload)this.elementDownloadList.get(i)).setState(false);
            ((ElementDownload)this.mAdapter.ED.get(i)).setState(false);
            ((ElementDownload)this.elementDownloadList.get(i)).setProg(1.0F);
            ((ElementDownload)this.mAdapter.ED.get(i)).setProg(1.0F);
            this.mAdapter.notifyItemChanged(i);
          }
        }
      }
      for (;;)
      {
        i += 1;
        break;
        if (((ElementDownload)this.elementDownloadList.get(i)).state)
        {
          TLRPC.TL_document localTL_document = new TLRPC.TL_document();
          localTL_document.access_hash = Long.parseLong(((ElementDownload)localObject).getAccess_hash());
          localTL_document.id = Long.parseLong(((ElementDownload)localObject).getId());
          localTL_document.date = Integer.parseInt(((ElementDownload)localObject).getDate());
          localTL_document.file_name = null;
          localTL_document.mime_type = ((ElementDownload)localObject).getMime_type();
          localTL_document.size = ((ElementDownload)localObject).getSize();
          localTL_document.dc_id = ((ElementDownload)localObject).getDc_id();
          localTL_document.user_id = ((ElementDownload)localObject).getUser_id();
          localTL_document.thumb = new TLRPC.PhotoSize();
          localTL_document.thumb.type = "";
          localTL_document.attributes.add(new TLRPC.TL_documentAttributeVideo());
          ((TLRPC.DocumentAttribute)localTL_document.attributes.get(0)).file_name = ((ElementDownload)localObject).getFile_name();
          ((TLRPC.DocumentAttribute)localTL_document.attributes.get(0)).w = ((ElementDownload)localObject).getW();
          ((TLRPC.DocumentAttribute)localTL_document.attributes.get(0)).h = ((ElementDownload)localObject).getH();
          ((TLRPC.DocumentAttribute)localTL_document.attributes.get(0)).duration = ((ElementDownload)localObject).duration;
          localObject = ((ElementDownload)localObject).getReal_name();
          FileLoader.getInstance().loadFile(localTL_document, true, false);
          MediaController.getInstance().addLoadingFileObserver2((String)localObject, null, this);
          continue;
          if (((ElementDownload)localObject).getType() == 9) {
            if (new File(FileLoader.getInstance().getDirectory(3), ((ElementDownload)this.elementDownloadList.get(i)).getReal_name()).exists())
            {
              this.db.updatedetails(((ElementDownload)this.elementDownloadList.get(i)).getId(), String.valueOf(1));
              this.db.updatestate(((ElementDownload)this.elementDownloadList.get(i)).getId(), 0);
              if (this.mAdapter != null)
              {
                ((ElementDownload)this.elementDownloadList.get(i)).setState(false);
                ((ElementDownload)this.mAdapter.ED.get(i)).setState(false);
                ((ElementDownload)this.elementDownloadList.get(i)).setProg(1.0F);
                ((ElementDownload)this.mAdapter.ED.get(i)).setProg(1.0F);
                this.mAdapter.notifyItemChanged(i);
              }
            }
            else if (((ElementDownload)this.elementDownloadList.get(i)).state)
            {
              localTL_document = new TLRPC.TL_document();
              localTL_document.access_hash = Long.parseLong(((ElementDownload)localObject).getAccess_hash());
              localTL_document.id = Long.parseLong(((ElementDownload)localObject).getId());
              localTL_document.date = Integer.parseInt(((ElementDownload)localObject).getDate());
              localTL_document.file_name = null;
              localTL_document.mime_type = ((ElementDownload)localObject).getMime_type();
              localTL_document.size = ((ElementDownload)localObject).getSize();
              localTL_document.dc_id = ((ElementDownload)localObject).getDc_id();
              localTL_document.user_id = ((ElementDownload)localObject).getUser_id();
              localTL_document.thumb = new TLRPC.PhotoSize();
              localTL_document.thumb.type = "";
              localTL_document.attributes.add(new TLRPC.TL_documentAttributeFilename());
              ((TLRPC.DocumentAttribute)localTL_document.attributes.get(0)).file_name = ((ElementDownload)localObject).getFile_name();
              localObject = ((ElementDownload)localObject).getReal_name();
              FileLoader.getInstance().loadFile(localTL_document, true, false);
              MediaController.getInstance().addLoadingFileObserver2((String)localObject, null, this);
            }
          }
        }
      }
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    download = this;
    setContentView(2130903077);
    this.toolbar = ((Toolbar)findViewById(2131624120));
    setSupportActionBar(this.toolbar);
    getSupportActionBar().setTitle("Download Manager");
    this.toolbar.setTitleTextColor(MihanTheme.contrastColor(MihanTheme.getThemeColor()));
    this.toolbar.setBackgroundColor(MihanTheme.getThemeColor());
    this.toolbar.setNavigationIcon(2130837810);
    this.toolbar.setNavigationOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Download.this.onBackPressed();
      }
    });
    paramBundle = (RecyclerView)findViewById(2131624121);
    this.llm = new LinearLayoutManager(this);
    paramBundle.setLayoutManager(this.llm);
    this.elementDownloadList = getDownloadArray();
    this.TAG = MediaController.getInstance().generateObserverTag();
    this.mAdapter = new RVAdapter(this, this.elementDownloadList, new RVAdapter.OnItemClickListener()new RVAdapter.OnCheckedChangeListener
    {
      public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        if (paramAnonymousView.getId() == 2131624176) {
          if (Download.this.elementDownloadList.size() > 0)
          {
            localObject = (ElementDownload)Download.this.elementDownloadList.get(paramAnonymousInt);
            paramAnonymousView = new TLRPC.TL_document();
            if (((ElementDownload)localObject).getType() != 3) {
              break label395;
            }
            paramAnonymousView.access_hash = Long.parseLong(((ElementDownload)localObject).getAccess_hash());
            paramAnonymousView.id = Long.parseLong(((ElementDownload)localObject).getId());
            paramAnonymousView.date = Integer.parseInt(((ElementDownload)localObject).getDate());
            paramAnonymousView.file_name = null;
            paramAnonymousView.mime_type = ((ElementDownload)localObject).getMime_type();
            paramAnonymousView.size = ((ElementDownload)localObject).getSize();
            paramAnonymousView.dc_id = ((ElementDownload)localObject).getDc_id();
            paramAnonymousView.user_id = ((ElementDownload)localObject).getUser_id();
            paramAnonymousView.thumb = new TLRPC.PhotoSize();
            paramAnonymousView.thumb.type = "";
            paramAnonymousView.attributes.add(new TLRPC.TL_documentAttributeVideo());
            ((TLRPC.DocumentAttribute)paramAnonymousView.attributes.get(0)).file_name = ((ElementDownload)localObject).getFile_name();
            ((TLRPC.DocumentAttribute)paramAnonymousView.attributes.get(0)).w = ((ElementDownload)localObject).getW();
            ((TLRPC.DocumentAttribute)paramAnonymousView.attributes.get(0)).h = ((ElementDownload)localObject).getH();
            ((TLRPC.DocumentAttribute)paramAnonymousView.attributes.get(0)).duration = ((ElementDownload)localObject).duration;
          }
        }
        label234:
        label369:
        label394:
        label395:
        do
        {
          do
          {
            break label394;
            break label394;
            break label394;
            break label394;
            break label394;
            if (!((ElementDownload)Download.this.elementDownloadList.get(paramAnonymousInt)).state)
            {
              Download.this.db.updatestate(((ElementDownload)localObject).getId(), 1);
              ((ElementDownload)Download.this.elementDownloadList.get(paramAnonymousInt)).setState(true);
              ((ElementDownload)Download.this.mAdapter.ED.get(paramAnonymousInt)).setState(true);
              Download.this.mAdapter.notifyItemChanged(paramAnonymousInt);
              if ((((ElementDownload)localObject).getType() == 9) || (((ElementDownload)localObject).getType() == 3))
              {
                localObject = ((ElementDownload)localObject).getReal_name();
                FileLoader.getInstance().loadFile(paramAnonymousView, true, false);
                MediaController.getInstance().addLoadingFileObserver2((String)localObject, null, Download.this);
              }
              Log.v("jjj", "you click" + paramAnonymousInt);
            }
            for (;;)
            {
              return;
              if (((ElementDownload)localObject).getType() != 9) {
                break label234;
              }
              paramAnonymousView.access_hash = Long.parseLong(((ElementDownload)localObject).getAccess_hash());
              paramAnonymousView.id = Long.parseLong(((ElementDownload)localObject).getId());
              paramAnonymousView.date = Integer.parseInt(((ElementDownload)localObject).getDate());
              paramAnonymousView.file_name = null;
              paramAnonymousView.mime_type = ((ElementDownload)localObject).getMime_type();
              paramAnonymousView.size = ((ElementDownload)localObject).getSize();
              paramAnonymousView.dc_id = ((ElementDownload)localObject).getDc_id();
              paramAnonymousView.user_id = ((ElementDownload)localObject).getUser_id();
              paramAnonymousView.thumb = new TLRPC.PhotoSize();
              paramAnonymousView.thumb.type = "";
              paramAnonymousView.attributes.add(new TLRPC.TL_documentAttributeFilename());
              ((TLRPC.DocumentAttribute)paramAnonymousView.attributes.get(0)).file_name = ((ElementDownload)localObject).getFile_name();
              break label234;
              if ((((ElementDownload)localObject).getType() != 9) && (((ElementDownload)localObject).getType() != 3)) {
                break label369;
              }
              ((ElementDownload)Download.this.elementDownloadList.get(paramAnonymousInt)).setState(false);
              ((ElementDownload)Download.this.mAdapter.ED.get(paramAnonymousInt)).setState(false);
              Download.this.db.updatestate(((ElementDownload)Download.this.elementDownloadList.get(paramAnonymousInt)).getId(), 0);
              FileLoader.getInstance().cancelLoadFile(paramAnonymousView);
              Download.this.mAdapter.notifyItemChanged(paramAnonymousInt);
              break label369;
              if (paramAnonymousView.getId() == 2131624177)
              {
                if (Download.this.elementDownloadList.size() <= 0) {
                  break;
                }
                Download.this.db.deleteDownload(((ElementDownload)Download.this.elementDownloadList.get(paramAnonymousInt)).id);
                Download.this.elementDownloadList.remove(paramAnonymousInt);
                Download.this.mAdapter.ED.remove(paramAnonymousInt);
                Download.this.mAdapter.notifyDataSetChanged();
                return;
              }
              if (paramAnonymousView.getId() != 2131624167) {
                break;
              }
              Log.v("jjj", "cv click position : " + paramAnonymousInt);
              if ((Download.this.elementDownloadList.size() <= 0) || (((ElementDownload)Download.this.elementDownloadList.get(paramAnonymousInt)).isState())) {
                break;
              }
              try
              {
                if (((ElementDownload)Download.this.elementDownloadList.get(paramAnonymousInt)).getType() == 3)
                {
                  paramAnonymousView = new File(FileLoader.getInstance().getDirectory(2), ((ElementDownload)Download.this.elementDownloadList.get(paramAnonymousInt)).getReal_name());
                  if (!paramAnonymousView.exists()) {
                    continue;
                  }
                  localObject = new Intent("android.intent.action.VIEW");
                  ((Intent)localObject).setDataAndType(Uri.fromFile(paramAnonymousView), "video/mp4");
                  Download.this.startActivityForResult((Intent)localObject, 500);
                }
              }
              catch (Exception paramAnonymousView)
              {
                paramAnonymousView.printStackTrace();
                return;
              }
            }
          } while (((ElementDownload)Download.this.elementDownloadList.get(paramAnonymousInt)).getType() != 9);
          paramAnonymousView = new File(FileLoader.getInstance().getDirectory(3), ((ElementDownload)Download.this.elementDownloadList.get(paramAnonymousInt)).getReal_name());
        } while (!paramAnonymousView.exists());
        Object localObject = new Intent("android.intent.action.VIEW");
        ((Intent)localObject).setDataAndType(Uri.fromFile(paramAnonymousView), ((ElementDownload)Download.this.elementDownloadList.get(paramAnonymousInt)).getMime_type());
        Download.this.startActivityForResult((Intent)localObject, 500);
      }
    }, new RVAdapter.OnCheckedChangeListener()
    {
      public void onItemCh(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean, int paramAnonymousInt)
      {
        Log.v("jjj", "you check" + paramAnonymousInt + " " + paramAnonymousBoolean);
        paramAnonymousCompoundButton = Download.this.db;
        String str = ((ElementDownload)Download.this.elementDownloadList.get(paramAnonymousInt)).id;
        if (paramAnonymousBoolean) {}
        for (int i = 1;; i = 0)
        {
          paramAnonymousCompoundButton.updateCheckState(str, i);
          ((ElementDownload)Download.this.elementDownloadList.get(paramAnonymousInt)).setCheck(paramAnonymousBoolean);
          ((ElementDownload)Download.this.mAdapter.ED.get(paramAnonymousInt)).setCheck(paramAnonymousBoolean);
          return;
        }
      }
    });
    paramBundle.setAdapter(this.mAdapter);
  }
  
  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    getMenuInflater().inflate(2131689473, paramMenu);
    return true;
  }
  
  public void onFailedDownload(String paramString)
  {
    Log.v("jjj", "  onFailedDownload----->  " + paramString);
    int i = 0;
    for (;;)
    {
      if (i < this.elementDownloadList.size())
      {
        if (paramString.equals(((ElementDownload)this.elementDownloadList.get(i)).getReal_name()))
        {
          this.db.updatestate(((ElementDownload)this.elementDownloadList.get(i)).getId(), 0);
          ((ElementDownload)this.elementDownloadList.get(i)).setState(false);
          ((ElementDownload)this.mAdapter.ED.get(i)).setState(false);
          this.mAdapter.notifyItemChanged(i);
        }
      }
      else {
        return;
      }
      i += 1;
    }
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    }
    startActivity(new Intent(this, ReminderAddActivity.class));
    return true;
  }
  
  public void onProgressDownload(String paramString, float paramFloat)
  {
    int i = 0;
    while (i < this.elementDownloadList.size())
    {
      if (paramString.equals(((ElementDownload)this.elementDownloadList.get(i)).getReal_name()))
      {
        this.db.updatedetails(((ElementDownload)this.elementDownloadList.get(i)).getId(), String.valueOf(paramFloat));
        ((ElementDownload)this.mAdapter.ED.get(i)).setProg(paramFloat);
        if (!((ElementDownload)this.mAdapter.ED.get(i)).isState())
        {
          ((ElementDownload)this.mAdapter.ED.get(i)).setState(true);
          ((ElementDownload)this.elementDownloadList.get(i)).setState(true);
        }
        ((ElementDownload)this.elementDownloadList.get(i)).setProg(paramFloat);
        this.mAdapter.notifyDataSetChanged();
        Log.v("jjj", "  fileName----->  " + paramString + "  progress----->  " + paramFloat);
      }
      i += 1;
    }
    Log.v("jjj", "  fileName----->  " + paramString + "  progress----->  " + paramFloat);
  }
  
  public void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean) {}
  
  protected void onResume()
  {
    super.onResume();
    start();
  }
  
  public void onSuccessDownload(String paramString)
  {
    int i = 0;
    for (;;)
    {
      if (i < this.elementDownloadList.size())
      {
        if (paramString.equals(((ElementDownload)this.elementDownloadList.get(i)).getReal_name()))
        {
          this.db.updatestate(((ElementDownload)this.elementDownloadList.get(i)).getId(), 0);
          ((ElementDownload)this.elementDownloadList.get(i)).setState(false);
          ((ElementDownload)this.mAdapter.ED.get(i)).setState(false);
          this.db.updatedetails(((ElementDownload)this.elementDownloadList.get(i)).getId(), String.valueOf(1));
          ((ElementDownload)this.elementDownloadList.get(i)).setProg(1.0F);
          ((ElementDownload)this.mAdapter.ED.get(i)).setProg(1.0F);
          this.mAdapter.notifyItemChanged(i);
          Log.v("jjj", "  onSuccessDownload----->  " + paramString);
        }
      }
      else {
        return;
      }
      i += 1;
    }
  }
  
  public void start()
  {
    this.elementDownloadList = getDownloadArray();
    int i = 0;
    if (i < this.elementDownloadList.size())
    {
      Object localObject = (ElementDownload)this.elementDownloadList.get(i);
      if (((ElementDownload)localObject).getType() == 3) {
        if (new File(FileLoader.getInstance().getDirectory(2), ((ElementDownload)this.elementDownloadList.get(i)).getReal_name()).exists())
        {
          this.db.updatedetails(((ElementDownload)this.elementDownloadList.get(i)).getId(), String.valueOf(1));
          this.db.updatestate(((ElementDownload)this.elementDownloadList.get(i)).getId(), 0);
          if (this.mAdapter != null)
          {
            ((ElementDownload)this.elementDownloadList.get(i)).setState(false);
            ((ElementDownload)this.mAdapter.ED.get(i)).setState(false);
            ((ElementDownload)this.elementDownloadList.get(i)).setProg(1.0F);
            ((ElementDownload)this.mAdapter.ED.get(i)).setProg(1.0F);
            this.mAdapter.notifyItemChanged(i);
          }
        }
      }
      for (;;)
      {
        i += 1;
        break;
        if (((ElementDownload)this.elementDownloadList.get(i)).state)
        {
          TLRPC.TL_document localTL_document = new TLRPC.TL_document();
          localTL_document.access_hash = Long.parseLong(((ElementDownload)localObject).getAccess_hash());
          localTL_document.id = Long.parseLong(((ElementDownload)localObject).getId());
          localTL_document.date = Integer.parseInt(((ElementDownload)localObject).getDate());
          localTL_document.file_name = null;
          localTL_document.mime_type = ((ElementDownload)localObject).getMime_type();
          localTL_document.size = ((ElementDownload)localObject).getSize();
          localTL_document.dc_id = ((ElementDownload)localObject).getDc_id();
          localTL_document.user_id = ((ElementDownload)localObject).getUser_id();
          localTL_document.thumb = new TLRPC.PhotoSize();
          localTL_document.thumb.type = "";
          localTL_document.attributes.add(new TLRPC.TL_documentAttributeVideo());
          ((TLRPC.DocumentAttribute)localTL_document.attributes.get(0)).file_name = ((ElementDownload)localObject).getFile_name();
          ((TLRPC.DocumentAttribute)localTL_document.attributes.get(0)).w = ((ElementDownload)localObject).getW();
          ((TLRPC.DocumentAttribute)localTL_document.attributes.get(0)).h = ((ElementDownload)localObject).getH();
          ((TLRPC.DocumentAttribute)localTL_document.attributes.get(0)).duration = ((ElementDownload)localObject).duration;
          localObject = ((ElementDownload)localObject).getReal_name();
          FileLoader.getInstance().loadFile(localTL_document, true, false);
          MediaController.getInstance().addLoadingFileObserver2((String)localObject, null, this);
          continue;
          if (((ElementDownload)localObject).getType() == 9) {
            if (new File(FileLoader.getInstance().getDirectory(3), ((ElementDownload)this.elementDownloadList.get(i)).getReal_name()).exists())
            {
              this.db.updatedetails(((ElementDownload)this.elementDownloadList.get(i)).getId(), String.valueOf(1));
              this.db.updatestate(((ElementDownload)this.elementDownloadList.get(i)).getId(), 0);
              if (this.mAdapter != null)
              {
                ((ElementDownload)this.elementDownloadList.get(i)).setState(false);
                ((ElementDownload)this.mAdapter.ED.get(i)).setState(false);
                ((ElementDownload)this.elementDownloadList.get(i)).setProg(1.0F);
                ((ElementDownload)this.mAdapter.ED.get(i)).setProg(1.0F);
                this.mAdapter.notifyItemChanged(i);
              }
            }
            else if (((ElementDownload)this.elementDownloadList.get(i)).state)
            {
              localTL_document = new TLRPC.TL_document();
              localTL_document.access_hash = Long.parseLong(((ElementDownload)localObject).getAccess_hash());
              localTL_document.id = Long.parseLong(((ElementDownload)localObject).getId());
              localTL_document.date = Integer.parseInt(((ElementDownload)localObject).getDate());
              localTL_document.file_name = null;
              localTL_document.mime_type = ((ElementDownload)localObject).getMime_type();
              localTL_document.size = ((ElementDownload)localObject).getSize();
              localTL_document.dc_id = ((ElementDownload)localObject).getDc_id();
              localTL_document.user_id = ((ElementDownload)localObject).getUser_id();
              localTL_document.thumb = new TLRPC.PhotoSize();
              localTL_document.thumb.type = "";
              localTL_document.attributes.add(new TLRPC.TL_documentAttributeFilename());
              ((TLRPC.DocumentAttribute)localTL_document.attributes.get(0)).file_name = ((ElementDownload)localObject).getFile_name();
              localObject = ((ElementDownload)localObject).getReal_name();
              FileLoader.getInstance().loadFile(localTL_document, true, false);
              MediaController.getInstance().addLoadingFileObserver2((String)localObject, null, this);
            }
          }
        }
      }
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\DownloadManager\Download.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */