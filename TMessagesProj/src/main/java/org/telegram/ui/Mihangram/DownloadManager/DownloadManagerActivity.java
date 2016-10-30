package org.telegram.ui.Mihangram.DownloadManager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import com.rey.material.widget.LinearLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.FileDownloadProgressListener;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Mihangram.DownloadManager.Adapter.RVAdapter;
import org.telegram.ui.Mihangram.DownloadManager.Adapter.RVAdapter.OnCheckedChangeListener;
import org.telegram.ui.Mihangram.DownloadManager.Adapter.RVAdapter.OnItemClickListener;
import org.telegram.ui.Mihangram.DownloadManager.SQLite.ElementDownload;
import org.telegram.ui.Mihangram.DownloadManager.SQLite.SQLDownload;

public class DownloadManagerActivity
  extends BaseFragment
  implements MediaController.FileDownloadProgressListener
{
  public static final int MEDIA_DIR_VIDEO = 2;
  private int TAG;
  final SQLDownload db = new SQLDownload(ApplicationLoader.applicationContext);
  private List elementDownloadList;
  LinearLayoutManager layoutManager;
  private RVAdapter mAdapter;
  String name = "";
  float percent = 0.0F;
  RecyclerView recyclerView;
  
  private List getDownloadArray()
  {
    return new SQLDownload(ApplicationLoader.applicationContext).getAllVideoInDownloadE();
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837810);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle("Download Manager");
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          DownloadManagerActivity.this.finishFragment();
        }
      }
    });
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    localLinearLayout.setBackgroundColor(-11959891);
    localFrameLayout.addView(localLinearLayout, LayoutHelper.createLinear(-1, -1));
    this.recyclerView = new RecyclerView(paramContext);
    this.recyclerView.setPadding(10, 10, 10, 10);
    localLinearLayout.addView(this.recyclerView, LayoutHelper.createFrame(-1, -2.0F));
    this.layoutManager = new LinearLayoutManager(paramContext);
    this.recyclerView.setLayoutManager(this.layoutManager);
    this.elementDownloadList = getDownloadArray();
    this.TAG = MediaController.getInstance().generateObserverTag();
    this.mAdapter = new RVAdapter(ApplicationLoader.applicationContext, this.elementDownloadList, new RVAdapter.OnItemClickListener()new RVAdapter.OnCheckedChangeListener
    {
      public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        if (paramAnonymousView.getId() == 2131624176) {
          if (DownloadManagerActivity.this.elementDownloadList.size() > 0)
          {
            localObject = (ElementDownload)DownloadManagerActivity.this.elementDownloadList.get(paramAnonymousInt);
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
            if (!((ElementDownload)DownloadManagerActivity.this.elementDownloadList.get(paramAnonymousInt)).state)
            {
              DownloadManagerActivity.this.db.updatestate(((ElementDownload)localObject).getId(), 1);
              ((ElementDownload)DownloadManagerActivity.this.elementDownloadList.get(paramAnonymousInt)).setState(true);
              ((ElementDownload)DownloadManagerActivity.this.mAdapter.ED.get(paramAnonymousInt)).setState(true);
              DownloadManagerActivity.this.mAdapter.notifyItemChanged(paramAnonymousInt);
              if ((((ElementDownload)localObject).getType() == 9) || (((ElementDownload)localObject).getType() == 3))
              {
                localObject = ((ElementDownload)localObject).getReal_name();
                FileLoader.getInstance().loadFile(paramAnonymousView, true, false);
                MediaController.getInstance().addLoadingFileObserver2((String)localObject, null, DownloadManagerActivity.this);
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
              ((ElementDownload)DownloadManagerActivity.this.elementDownloadList.get(paramAnonymousInt)).setState(false);
              ((ElementDownload)DownloadManagerActivity.this.mAdapter.ED.get(paramAnonymousInt)).setState(false);
              DownloadManagerActivity.this.db.updatestate(((ElementDownload)DownloadManagerActivity.this.elementDownloadList.get(paramAnonymousInt)).getId(), 0);
              FileLoader.getInstance().cancelLoadFile(paramAnonymousView);
              DownloadManagerActivity.this.mAdapter.notifyItemChanged(paramAnonymousInt);
              break label369;
              if (paramAnonymousView.getId() == 2131624177)
              {
                if (DownloadManagerActivity.this.elementDownloadList.size() <= 0) {
                  break;
                }
                DownloadManagerActivity.this.db.deleteDownload(((ElementDownload)DownloadManagerActivity.this.elementDownloadList.get(paramAnonymousInt)).id);
                DownloadManagerActivity.this.elementDownloadList.remove(paramAnonymousInt);
                DownloadManagerActivity.this.mAdapter.ED.remove(paramAnonymousInt);
                DownloadManagerActivity.this.mAdapter.notifyDataSetChanged();
                return;
              }
              if (paramAnonymousView.getId() != 2131624167) {
                break;
              }
              Log.v("jjj", "cv click position : " + paramAnonymousInt);
              if ((DownloadManagerActivity.this.elementDownloadList.size() <= 0) || (((ElementDownload)DownloadManagerActivity.this.elementDownloadList.get(paramAnonymousInt)).isState())) {
                break;
              }
              try
              {
                if (((ElementDownload)DownloadManagerActivity.this.elementDownloadList.get(paramAnonymousInt)).getType() == 3)
                {
                  paramAnonymousView = new File(FileLoader.getInstance().getDirectory(2), ((ElementDownload)DownloadManagerActivity.this.elementDownloadList.get(paramAnonymousInt)).getReal_name());
                  if (!paramAnonymousView.exists()) {
                    continue;
                  }
                  localObject = new Intent("android.intent.action.VIEW");
                  ((Intent)localObject).setDataAndType(Uri.fromFile(paramAnonymousView), "video/mp4");
                  DownloadManagerActivity.this.startActivityForResult((Intent)localObject, 500);
                }
              }
              catch (Exception paramAnonymousView)
              {
                paramAnonymousView.printStackTrace();
                return;
              }
            }
          } while (((ElementDownload)DownloadManagerActivity.this.elementDownloadList.get(paramAnonymousInt)).getType() != 9);
          paramAnonymousView = new File(FileLoader.getInstance().getDirectory(3), ((ElementDownload)DownloadManagerActivity.this.elementDownloadList.get(paramAnonymousInt)).getReal_name());
        } while (!paramAnonymousView.exists());
        Object localObject = new Intent("android.intent.action.VIEW");
        ((Intent)localObject).setDataAndType(Uri.fromFile(paramAnonymousView), ((ElementDownload)DownloadManagerActivity.this.elementDownloadList.get(paramAnonymousInt)).getMime_type());
        DownloadManagerActivity.this.startActivityForResult((Intent)localObject, 500);
      }
    }, new RVAdapter.OnCheckedChangeListener()
    {
      public void onItemCh(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean, int paramAnonymousInt)
      {
        paramAnonymousCompoundButton = DownloadManagerActivity.this.db;
        String str = ((ElementDownload)DownloadManagerActivity.this.elementDownloadList.get(paramAnonymousInt)).id;
        if (paramAnonymousBoolean) {}
        for (int i = 1;; i = 0)
        {
          paramAnonymousCompoundButton.updateCheckState(str, i);
          ((ElementDownload)DownloadManagerActivity.this.elementDownloadList.get(paramAnonymousInt)).setCheck(paramAnonymousBoolean);
          ((ElementDownload)DownloadManagerActivity.this.mAdapter.ED.get(paramAnonymousInt)).setCheck(paramAnonymousBoolean);
          return;
        }
      }
    });
    this.recyclerView.setAdapter(this.mAdapter);
    return this.fragmentView;
  }
  
  public int getObserverTag()
  {
    return this.TAG;
  }
  
  public void onFailedDownload(String paramString)
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
          this.mAdapter.notifyItemChanged(i);
        }
      }
      else {
        return;
      }
      i += 1;
    }
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
      }
      i += 1;
    }
  }
  
  public void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean) {}
  
  public void onResume()
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


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\DownloadManager\DownloadManagerActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */