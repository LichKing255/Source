package org.telegram.ui.ActionBar;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.Supergram.Theming.MihanTheme;

public class BaseFragment
{
  protected ActionBar actionBar;
  protected Bundle arguments;
  protected int classGuid = 0;
  protected View fragmentView;
  protected boolean hasOwnBackground = false;
  private boolean isFinished = false;
  protected ActionBarLayout parentLayout;
  protected boolean swipeBackEnabled = true;
  protected Dialog visibleDialog = null;
  
  public BaseFragment()
  {
    this.classGuid = ConnectionsManager.getInstance().generateClassGuid();
  }
  
  public BaseFragment(Bundle paramBundle)
  {
    this.arguments = paramBundle;
    this.classGuid = ConnectionsManager.getInstance().generateClassGuid();
  }
  
  protected void clearViews()
  {
    ViewGroup localViewGroup;
    if (this.fragmentView != null)
    {
      localViewGroup = (ViewGroup)this.fragmentView.getParent();
      if (localViewGroup == null) {}
    }
    try
    {
      localViewGroup.removeView(this.fragmentView);
      this.fragmentView = null;
      if (this.actionBar != null)
      {
        localViewGroup = (ViewGroup)this.actionBar.getParent();
        if (localViewGroup == null) {}
      }
    }
    catch (Exception localException1)
    {
      try
      {
        localViewGroup.removeView(this.actionBar);
        this.actionBar = null;
        this.parentLayout = null;
        return;
        localException1 = localException1;
        FileLog.e("tmessages", localException1);
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException2);
        }
      }
    }
  }
  
  protected ActionBar createActionBar(Context paramContext)
  {
    paramContext = new ActionBar(paramContext);
    paramContext.setBackgroundColor(MihanTheme.getThemeColor());
    paramContext.setItemsBackgroundColor(-12554860);
    return paramContext;
  }
  
  public View createView(Context paramContext)
  {
    return null;
  }
  
  public boolean dismissDialogOnPause(Dialog paramDialog)
  {
    return true;
  }
  
  public void finishFragment()
  {
    finishFragment(true);
  }
  
  public void finishFragment(boolean paramBoolean)
  {
    if ((this.isFinished) || (this.parentLayout == null)) {
      return;
    }
    this.parentLayout.closeLastFragment(paramBoolean);
  }
  
  public ActionBar getActionBar()
  {
    return this.actionBar;
  }
  
  public Bundle getArguments()
  {
    return this.arguments;
  }
  
  public View getFragmentView()
  {
    return this.fragmentView;
  }
  
  public Activity getParentActivity()
  {
    if (this.parentLayout != null) {
      return this.parentLayout.parentActivity;
    }
    return null;
  }
  
  public Dialog getVisibleDialog()
  {
    return this.visibleDialog;
  }
  
  public boolean needDelayOpenAnimation()
  {
    return false;
  }
  
  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent) {}
  
  public boolean onBackPressed()
  {
    return true;
  }
  
  protected void onBecomeFullyVisible() {}
  
  public void onBeginSlide()
  {
    try
    {
      if ((this.visibleDialog != null) && (this.visibleDialog.isShowing()))
      {
        this.visibleDialog.dismiss();
        this.visibleDialog = null;
      }
      if (this.actionBar != null) {
        this.actionBar.onPause();
      }
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration) {}
  
  protected AnimatorSet onCustomTransitionAnimation(boolean paramBoolean, Runnable paramRunnable)
  {
    return null;
  }
  
  protected void onDialogDismiss(Dialog paramDialog) {}
  
  public boolean onFragmentCreate()
  {
    return true;
  }
  
  public void onFragmentDestroy()
  {
    ConnectionsManager.getInstance().cancelRequestsForGuid(this.classGuid);
    this.isFinished = true;
    if (this.actionBar != null) {
      this.actionBar.setEnabled(false);
    }
  }
  
  public void onLowMemory() {}
  
  public void onPause()
  {
    if (this.actionBar != null) {
      this.actionBar.onPause();
    }
    try
    {
      if ((this.visibleDialog != null) && (this.visibleDialog.isShowing()) && (dismissDialogOnPause(this.visibleDialog)))
      {
        this.visibleDialog.dismiss();
        this.visibleDialog = null;
      }
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt) {}
  
  public void onResume() {}
  
  protected void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2) {}
  
  protected void onTransitionAnimationStart(boolean paramBoolean1, boolean paramBoolean2) {}
  
  public boolean presentFragment(BaseFragment paramBaseFragment)
  {
    return (this.parentLayout != null) && (this.parentLayout.presentFragment(paramBaseFragment));
  }
  
  public boolean presentFragment(BaseFragment paramBaseFragment, boolean paramBoolean)
  {
    return (this.parentLayout != null) && (this.parentLayout.presentFragment(paramBaseFragment, paramBoolean));
  }
  
  public boolean presentFragment(BaseFragment paramBaseFragment, boolean paramBoolean1, boolean paramBoolean2)
  {
    return (this.parentLayout != null) && (this.parentLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, true));
  }
  
  public void removeSelfFromStack()
  {
    if ((this.isFinished) || (this.parentLayout == null)) {
      return;
    }
    this.parentLayout.removeFragmentFromStack(this);
  }
  
  public void restoreSelfArgs(Bundle paramBundle) {}
  
  public void saveSelfArgs(Bundle paramBundle) {}
  
  protected void setParentLayout(ActionBarLayout paramActionBarLayout)
  {
    if (this.parentLayout != paramActionBarLayout)
    {
      this.parentLayout = paramActionBarLayout;
      if (this.fragmentView != null)
      {
        paramActionBarLayout = (ViewGroup)this.fragmentView.getParent();
        if (paramActionBarLayout == null) {}
      }
    }
    try
    {
      paramActionBarLayout.removeView(this.fragmentView);
      if ((this.parentLayout != null) && (this.parentLayout.getContext() != this.fragmentView.getContext())) {
        this.fragmentView = null;
      }
      if (this.actionBar != null)
      {
        paramActionBarLayout = (ViewGroup)this.actionBar.getParent();
        if (paramActionBarLayout == null) {}
      }
    }
    catch (Exception paramActionBarLayout)
    {
      try
      {
        paramActionBarLayout.removeView(this.actionBar);
        if ((this.parentLayout != null) && (this.parentLayout.getContext() != this.actionBar.getContext())) {
          this.actionBar = null;
        }
        if ((this.parentLayout != null) && (this.actionBar == null))
        {
          this.actionBar = createActionBar(this.parentLayout.getContext());
          this.actionBar.parentFragment = this;
        }
        return;
        paramActionBarLayout = paramActionBarLayout;
        FileLog.e("tmessages", paramActionBarLayout);
      }
      catch (Exception paramActionBarLayout)
      {
        for (;;)
        {
          FileLog.e("tmessages", paramActionBarLayout);
        }
      }
    }
  }
  
  public void setVisibleDialog(Dialog paramDialog)
  {
    this.visibleDialog = paramDialog;
  }
  
  public Dialog showDialog(Dialog paramDialog)
  {
    if (!(paramDialog instanceof BottomSheet)) {
      paramDialog.setOnShowListener(new DialogInterface.OnShowListener()
      {
        public void onShow(DialogInterface paramAnonymousDialogInterface)
        {
          paramAnonymousDialogInterface = ((AlertDialog)paramAnonymousDialogInterface).getListView();
          if (paramAnonymousDialogInterface != null) {
            paramAnonymousDialogInterface.setAdapter(new ListAdapter()
            {
              public boolean areAllItemsEnabled()
              {
                return this.val$originalAdapter.areAllItemsEnabled();
              }
              
              public int getCount()
              {
                return this.val$originalAdapter.getCount();
              }
              
              public Object getItem(int paramAnonymous2Int)
              {
                return this.val$originalAdapter.getItem(paramAnonymous2Int);
              }
              
              public long getItemId(int paramAnonymous2Int)
              {
                return this.val$originalAdapter.getItemId(paramAnonymous2Int);
              }
              
              public int getItemViewType(int paramAnonymous2Int)
              {
                return this.val$originalAdapter.getItemViewType(paramAnonymous2Int);
              }
              
              public View getView(int paramAnonymous2Int, View paramAnonymous2View, ViewGroup paramAnonymous2ViewGroup)
              {
                paramAnonymous2View = this.val$originalAdapter.getView(paramAnonymous2Int, paramAnonymous2View, paramAnonymous2ViewGroup);
                ((TextView)paramAnonymous2View).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                return paramAnonymous2View;
              }
              
              public int getViewTypeCount()
              {
                return this.val$originalAdapter.getViewTypeCount();
              }
              
              public boolean hasStableIds()
              {
                return this.val$originalAdapter.hasStableIds();
              }
              
              public boolean isEmpty()
              {
                return this.val$originalAdapter.isEmpty();
              }
              
              public boolean isEnabled(int paramAnonymous2Int)
              {
                return this.val$originalAdapter.isEnabled(paramAnonymous2Int);
              }
              
              public void registerDataSetObserver(DataSetObserver paramAnonymous2DataSetObserver)
              {
                this.val$originalAdapter.registerDataSetObserver(paramAnonymous2DataSetObserver);
              }
              
              public void unregisterDataSetObserver(DataSetObserver paramAnonymous2DataSetObserver)
              {
                this.val$originalAdapter.unregisterDataSetObserver(paramAnonymous2DataSetObserver);
              }
            });
          }
        }
      });
    }
    return showDialog(paramDialog, false);
  }
  
  public Dialog showDialog(Dialog paramDialog, boolean paramBoolean)
  {
    if ((paramDialog == null) || (this.parentLayout == null) || (this.parentLayout.animationInProgress) || (this.parentLayout.startedTracking) || ((!paramBoolean) && (this.parentLayout.checkTransitionAnimation()))) {
      return null;
    }
    try
    {
      if (this.visibleDialog != null)
      {
        this.visibleDialog.dismiss();
        this.visibleDialog = null;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        try
        {
          this.visibleDialog = paramDialog;
          this.visibleDialog.setCanceledOnTouchOutside(true);
          this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
          {
            public void onDismiss(DialogInterface paramAnonymousDialogInterface)
            {
              BaseFragment.this.onDialogDismiss(BaseFragment.this.visibleDialog);
              BaseFragment.this.visibleDialog = null;
            }
          });
          this.visibleDialog.show();
          paramDialog = (TextView)this.visibleDialog.findViewById(16908299);
          Button localButton1 = (Button)this.visibleDialog.findViewById(16908313);
          Button localButton2 = (Button)this.visibleDialog.findViewById(16908314);
          paramDialog.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          localButton1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          localButton2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          paramDialog = this.visibleDialog;
          return paramDialog;
        }
        catch (Exception paramDialog)
        {
          FileLog.e("tmessages", paramDialog);
        }
        localException = localException;
        FileLog.e("tmessages", localException);
      }
    }
    return null;
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt)
  {
    if (this.parentLayout != null) {
      this.parentLayout.startActivityForResult(paramIntent, paramInt);
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\ActionBar\BaseFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */