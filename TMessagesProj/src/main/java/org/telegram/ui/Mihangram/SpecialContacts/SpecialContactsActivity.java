package org.telegram.ui.Mihangram.SpecialContacts;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Mihangram.TextDescriptionCell;
import org.telegram.ui.Mihangram.Theming.MihanTheme;

public class SpecialContactsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private boolean checked = false;
  private TextView emptyTextView;
  TextCheckCell enableSCSTextCheck;
  TextCheckCell enableSCTextCheck;
  private ListView listView;
  private BaseAdapter listViewAdapter;
  
  private void updateColors(ActionBarMenu paramActionBarMenu)
  {
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    int i = ((SharedPreferences)localObject).getInt("theme_contact_action_color", MihanTheme.getActionBarColor((SharedPreferences)localObject));
    int j = ((SharedPreferences)localObject).getInt("theme_contact_action_gradient", MihanTheme.getActionBarGradientFlag((SharedPreferences)localObject));
    int k = ((SharedPreferences)localObject).getInt("theme_contact_action_gcolor", MihanTheme.getActionBarGradientColor((SharedPreferences)localObject));
    if (j != 0)
    {
      GradientDrawable localGradientDrawable = MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j));
      this.actionBar.setBackgroundDrawable(localGradientDrawable);
    }
    for (;;)
    {
      i = ((SharedPreferences)localObject).getInt("theme_contact_action_icolor", MihanTheme.getActionBarIconColor((SharedPreferences)localObject));
      this.actionBar.setTitleColor(((SharedPreferences)localObject).getInt("theme_contact_action_tcolor", MihanTheme.getActionBarTitleColor((SharedPreferences)localObject)));
      localObject = ApplicationLoader.applicationContext.getResources().getDrawable(2130837829);
      MihanTheme.setColorFilter((Drawable)localObject, i);
      this.actionBar.setBackButtonDrawable((Drawable)localObject);
      MihanTheme.setColorFilter(paramActionBarMenu.getItem(1).getImageView().getDrawable(), i);
      MihanTheme.setColorFilter(paramActionBarMenu.getItem(2).getImageView().getDrawable(), i);
      return;
      this.actionBar.setBackgroundColor(i);
    }
  }
  
  private void updateVisibleRows(int paramInt)
  {
    if (this.listView != null)
    {
      int j = this.listView.getChildCount();
      int i = 0;
      while (i < j)
      {
        View localView = this.listView.getChildAt(i);
        if ((localView instanceof SpecialContactCell)) {
          ((SpecialContactCell)localView).update(paramInt);
        }
        i += 1;
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837829);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("SpecialContacts", 2131166674));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1)
        {
          SpecialContactsActivity.this.finishFragment();
          return;
        }
        if (paramAnonymousInt == 1)
        {
          SpecialContactsActivity.this.presentFragment(new SpecialSelectActivity());
          return;
        }
        SpecialContactsActivity.this.presentFragment(new SpecialNotificationsActivity());
      }
    });
    ActionBarMenu localActionBarMenu = this.actionBar.createMenu();
    localActionBarMenu.addItem(1, 2130837807);
    localActionBarMenu.addItem(2, 2130837827);
    this.listViewAdapter = new SpecialContactAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext);
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    localLinearLayout.setOrientation(1);
    ((FrameLayout)this.fragmentView).addView(localLinearLayout);
    final Object localObject1 = (FrameLayout.LayoutParams)localLinearLayout.getLayoutParams();
    ((FrameLayout.LayoutParams)localObject1).width = -1;
    ((FrameLayout.LayoutParams)localObject1).height = -1;
    ((FrameLayout.LayoutParams)localObject1).gravity = 49;
    ((FrameLayout.LayoutParams)localObject1).topMargin = 0;
    localLinearLayout.setLayoutParams((ViewGroup.LayoutParams)localObject1);
    localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
    this.enableSCTextCheck = new TextCheckCell(paramContext);
    this.enableSCTextCheck.setBackgroundColor(-1);
    this.enableSCTextCheck.setTextAndCheck(LocaleController.getString("EnableSpecialContacts", 2131166633), ((SharedPreferences)localObject1).getBoolean("specific_contact", false), true);
    this.enableSCTextCheck.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        boolean bool2 = true;
        boolean bool3 = localObject1.getBoolean("specific_contact", false);
        Object localObject = SpecialContactsActivity.this;
        if (!bool3)
        {
          bool1 = true;
          SpecialContactsActivity.access$002((SpecialContactsActivity)localObject, bool1);
          localObject = localObject1.edit();
          if (bool3) {
            break label135;
          }
          bool1 = true;
          label54:
          ((SharedPreferences.Editor)localObject).putBoolean("specific_contact", bool1);
          if (!SpecialContactsActivity.this.checked)
          {
            ((SharedPreferences.Editor)localObject).putBoolean("specific_contact_service", false);
            SpecialContactsActivity.this.enableSCSTextCheck.setChecked(false);
          }
          ((SharedPreferences.Editor)localObject).commit();
          if ((paramAnonymousView instanceof TextCheckCell))
          {
            paramAnonymousView = (TextCheckCell)paramAnonymousView;
            if (bool3) {
              break label140;
            }
          }
        }
        label135:
        label140:
        for (boolean bool1 = bool2;; bool1 = false)
        {
          paramAnonymousView.setChecked(bool1);
          return;
          bool1 = false;
          break;
          bool1 = false;
          break label54;
        }
      }
    });
    localLinearLayout.addView(this.enableSCTextCheck);
    this.enableSCSTextCheck = new TextCheckCell(paramContext);
    this.enableSCSTextCheck.setVisibility(8);
    this.enableSCSTextCheck.setBackgroundColor(-1);
    this.enableSCSTextCheck.setTextAndCheck(LocaleController.getString("EnableSpecialContactsService", 2131166634), ((SharedPreferences)localObject1).getBoolean("specific_contact_service", false), false);
    this.enableSCSTextCheck.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        boolean bool2 = true;
        if (SpecialContactsActivity.this.checked)
        {
          boolean bool3 = localObject1.getBoolean("specific_contact_service", false);
          SharedPreferences.Editor localEditor = localObject1.edit();
          if (bool3) {
            break label88;
          }
          bool1 = true;
          localEditor.putBoolean("specific_contact_service", bool1);
          localEditor.commit();
          if ((paramAnonymousView instanceof TextCheckCell))
          {
            paramAnonymousView = (TextCheckCell)paramAnonymousView;
            if (bool3) {
              break label93;
            }
          }
        }
        label88:
        label93:
        for (boolean bool1 = bool2;; bool1 = false)
        {
          paramAnonymousView.setChecked(bool1);
          return;
          bool1 = false;
          break;
        }
      }
    });
    localLinearLayout.addView(this.enableSCSTextCheck);
    Object localObject2 = new TextDescriptionCell(paramContext);
    ((TextDescriptionCell)localObject2).setVisibility(8);
    ((TextDescriptionCell)localObject2).setBackgroundColor(-1);
    ((TextDescriptionCell)localObject2).setText(LocaleController.getString("EnableSCServiceDes", 2131166632), true);
    localLinearLayout.addView((View)localObject2);
    localObject2 = new LinearLayout(paramContext);
    ((LinearLayout)localObject2).setVisibility(4);
    ((LinearLayout)localObject2).setOrientation(1);
    localLinearLayout.addView((View)localObject2);
    Object localObject3 = (LinearLayout.LayoutParams)((LinearLayout)localObject2).getLayoutParams();
    ((LinearLayout.LayoutParams)localObject3).width = -1;
    ((LinearLayout.LayoutParams)localObject3).height = -1;
    ((LinearLayout.LayoutParams)localObject3).gravity = 48;
    ((LinearLayout)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject3);
    ((LinearLayout)localObject2).setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.emptyTextView = new TextView(paramContext);
    this.emptyTextView.setTextColor(-8355712);
    this.emptyTextView.setTextSize(1, 20.0F);
    this.emptyTextView.setGravity(17);
    this.emptyTextView.setText(LocaleController.getString("NoSpecialContacts", 2131166661));
    this.emptyTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    ((LinearLayout)localObject2).addView(this.emptyTextView);
    localObject3 = (LinearLayout.LayoutParams)this.emptyTextView.getLayoutParams();
    ((LinearLayout.LayoutParams)localObject3).width = -1;
    ((LinearLayout.LayoutParams)localObject3).height = -1;
    ((LinearLayout.LayoutParams)localObject3).weight = 0.5F;
    this.emptyTextView.setLayoutParams((ViewGroup.LayoutParams)localObject3);
    localObject3 = new FrameLayout(paramContext);
    ((LinearLayout)localObject2).addView((View)localObject3);
    LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)((FrameLayout)localObject3).getLayoutParams();
    localLayoutParams.width = -1;
    localLayoutParams.height = -1;
    localLayoutParams.weight = 0.5F;
    ((FrameLayout)localObject3).setLayoutParams(localLayoutParams);
    this.listView = new ListView(paramContext);
    this.listView.setEmptyView((View)localObject2);
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setDivider(null);
    this.listView.setDividerHeight(0);
    this.listView.setFastScrollEnabled(true);
    this.listView.setScrollBarStyle(33554432);
    this.listView.setAdapter(this.listViewAdapter);
    this.listView.setFastScrollAlwaysVisible(true);
    paramContext = this.listView;
    if (LocaleController.isRTL) {}
    for (int i = 1;; i = 2)
    {
      paramContext.setVerticalScrollbarPosition(i);
      localLinearLayout.addView(this.listView);
      paramContext = (LinearLayout.LayoutParams)this.listView.getLayoutParams();
      paramContext.width = -1;
      paramContext.height = -1;
      this.listView.setLayoutParams(paramContext);
      this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
      {
        public boolean onItemLongClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, final int paramAnonymousInt, long paramAnonymousLong)
        {
          paramAnonymousAdapterView = new BottomSheet.Builder(SpecialContactsActivity.this.getParentActivity());
          paramAnonymousView = LocaleController.getString("Delete", 2131165600);
          DialogInterface.OnClickListener local1 = new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              if (paramAnonymous2Int == 0)
              {
                paramAnonymous2DialogInterface = new AlertDialog.Builder(SpecialContactsActivity.this.getParentActivity());
                paramAnonymous2DialogInterface.setTitle(LocaleController.getString("AppName", 2131165338));
                paramAnonymous2DialogInterface.setMessage(LocaleController.getString("AreYouSureToContinue", 2131166617));
                paramAnonymous2DialogInterface.setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymous3DialogInterface, int paramAnonymous3Int)
                  {
                    paramAnonymous3Int = ((TLRPC.User)((SpecialContactAdapter)SpecialContactsActivity.this.listView.getAdapter()).getItem(SpecialContactsActivity.5.1.this.val$position)).id;
                    if (SpecialContactsActivity.5.this.val$preferences.contains("specific_c" + paramAnonymous3Int))
                    {
                      paramAnonymous3DialogInterface = SpecialContactsActivity.5.this.val$preferences.edit();
                      paramAnonymous3DialogInterface.remove("specific_c" + paramAnonymous3Int);
                      paramAnonymous3DialogInterface.commit();
                      if (SpecialContactsActivity.this.listViewAdapter != null) {
                        SpecialContactsActivity.this.listViewAdapter.notifyDataSetChanged();
                      }
                    }
                  }
                });
                paramAnonymous2DialogInterface.setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
                SpecialContactsActivity.this.showDialog(paramAnonymous2DialogInterface.create());
              }
            }
          };
          paramAnonymousAdapterView.setItems(new CharSequence[] { paramAnonymousView }, local1);
          SpecialContactsActivity.this.showDialog(paramAnonymousAdapterView.create());
          return true;
        }
      });
      this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          paramAnonymousAdapterView = ((SpecialContactAdapter)SpecialContactsActivity.this.listView.getAdapter()).getItem(paramAnonymousInt);
          if (paramAnonymousAdapterView == null) {}
          do
          {
            return;
            paramAnonymousView = new Bundle();
            paramAnonymousView.putInt("user_id", ((TLRPC.User)paramAnonymousAdapterView).id);
          } while (!MessagesController.checkCanOpenChat(paramAnonymousView, SpecialContactsActivity.this));
          SpecialContactsActivity.this.presentFragment(new ChatActivity(paramAnonymousView), true);
        }
      });
      updateColors(localActionBarMenu);
      return this.fragmentView;
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.updateInterfaces)
    {
      paramInt = ((Integer)paramVarArgs[0]).intValue();
      if (((paramInt & 0x2) != 0) || ((paramInt & 0x1) != 0) || ((paramInt & 0x4) != 0)) {
        updateVisibleRows(paramInt);
      }
    }
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null) {
      this.listViewAdapter.notifyDataSetChanged();
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\SpecialContacts\SpecialContactsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */