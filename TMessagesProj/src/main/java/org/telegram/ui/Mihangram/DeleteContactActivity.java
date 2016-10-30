package org.telegram.ui.Mihangram;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.ContactsAdapter;
import org.telegram.ui.Adapters.SearchAdapter;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.ChipSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LetterSectionsListView;
import org.telegram.ui.Mihangram.Theming.MihanTheme;

public class DeleteContactActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int done_button = 1;
  private ArrayList<ChipSpan> allSpans = new ArrayList();
  private int beforeChangeIndex;
  private CharSequence changeString;
  private int chatType = 0;
  private GroupCreateActivityDelegate delegate;
  private TextView emptyTextView;
  private boolean ignoreChange;
  private boolean isAlwaysShare;
  private boolean isGroup;
  private boolean isNeverShare;
  private LetterSectionsListView listView;
  private ContactsAdapter listViewAdapter;
  private int maxCount = 1000;
  private SearchAdapter searchListViewAdapter;
  private boolean searchWas;
  private boolean searching;
  private HashMap<Integer, ChipSpan> selectedContacts = new HashMap();
  ArrayList<TLRPC.User> selectedUsers = new ArrayList();
  private EditText userSelectEditText;
  
  public DeleteContactActivity() {}
  
  public DeleteContactActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.chatType = paramBundle.getInt("chatType", 0);
    this.isAlwaysShare = paramBundle.getBoolean("isAlwaysShare", false);
    this.isNeverShare = paramBundle.getBoolean("isNeverShare", false);
    this.isGroup = paramBundle.getBoolean("isGroup", false);
    if (this.chatType == 0) {}
    for (int i = MessagesController.getInstance().maxMegagroupCount;; i = MessagesController.getInstance().maxBroadcastCount)
    {
      this.maxCount = i;
      return;
    }
  }
  
  private ChipSpan createAndPutChipForUser(TLRPC.User paramUser)
  {
    this.selectedUsers.add(paramUser);
    Object localObject3 = ((LayoutInflater)ApplicationLoader.applicationContext.getSystemService("layout_inflater")).inflate(2130903081, null);
    TextView localTextView = (TextView)((View)localObject3).findViewById(2131624131);
    Object localObject2 = UserObject.getUserName(paramUser);
    Object localObject1 = localObject2;
    if (((String)localObject2).length() == 0)
    {
      localObject1 = localObject2;
      if (paramUser.phone != null)
      {
        localObject1 = localObject2;
        if (paramUser.phone.length() != 0) {
          localObject1 = PhoneFormat.getInstance().format("+" + paramUser.phone);
        }
      }
    }
    localTextView.setText((String)localObject1 + ", ");
    int i = View.MeasureSpec.makeMeasureSpec(0, 0);
    ((View)localObject3).measure(i, i);
    ((View)localObject3).layout(0, 0, ((View)localObject3).getMeasuredWidth(), ((View)localObject3).getMeasuredHeight());
    localObject1 = Bitmap.createBitmap(((View)localObject3).getWidth(), ((View)localObject3).getHeight(), Bitmap.Config.ARGB_8888);
    localObject2 = new Canvas((Bitmap)localObject1);
    ((Canvas)localObject2).translate(-((View)localObject3).getScrollX(), -((View)localObject3).getScrollY());
    ((View)localObject3).draw((Canvas)localObject2);
    ((View)localObject3).setDrawingCacheEnabled(true);
    ((View)localObject3).getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);
    ((View)localObject3).destroyDrawingCache();
    localObject2 = new BitmapDrawable((Bitmap)localObject1);
    ((BitmapDrawable)localObject2).setBounds(0, 0, ((Bitmap)localObject1).getWidth(), ((Bitmap)localObject1).getHeight());
    localObject1 = new SpannableStringBuilder("");
    localObject2 = new ChipSpan((Drawable)localObject2, 1);
    this.allSpans.add(localObject2);
    this.selectedContacts.put(Integer.valueOf(paramUser.id), localObject2);
    paramUser = this.allSpans.iterator();
    while (paramUser.hasNext())
    {
      localObject3 = (ImageSpan)paramUser.next();
      ((SpannableStringBuilder)localObject1).append("<<");
      ((SpannableStringBuilder)localObject1).setSpan(localObject3, ((SpannableStringBuilder)localObject1).length() - 2, ((SpannableStringBuilder)localObject1).length(), 33);
    }
    this.userSelectEditText.setText((CharSequence)localObject1);
    this.userSelectEditText.setSelection(((SpannableStringBuilder)localObject1).length());
    return (ChipSpan)localObject2;
  }
  
  private void updateColors()
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
      this.actionBar.setTitleColor(((SharedPreferences)localObject).getInt("theme_contact_action_tcolor", i));
      MihanTheme.setColorFilter(ApplicationLoader.applicationContext.getResources().getDrawable(2130837829), i);
      MihanTheme.setColorFilter(ApplicationLoader.applicationContext.getResources().getDrawable(2130837902), i);
      i = ((SharedPreferences)localObject).getInt("theme_contact_list_color", MihanTheme.getListViewColor((SharedPreferences)localObject));
      j = ((SharedPreferences)localObject).getInt("theme_contact_list_gradient", MihanTheme.getListViewGradientFlag((SharedPreferences)localObject));
      k = ((SharedPreferences)localObject).getInt("theme_contact_list_gcolor", MihanTheme.getListViewGradientColor((SharedPreferences)localObject));
      if (j == 0) {
        break;
      }
      localObject = MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j));
      this.listView.setBackgroundDrawable((Drawable)localObject);
      return;
      this.actionBar.setBackgroundColor(i);
    }
    this.listView.setBackgroundColor(i);
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
        if ((localView instanceof UserCell)) {
          ((UserCell)localView).update(paramInt);
        }
        i += 1;
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    this.searching = false;
    this.searchWas = false;
    this.actionBar.setBackButtonImage(2130837829);
    this.actionBar.setAllowOverlayTitle(true);
    LinearLayout localLinearLayout;
    if (this.isAlwaysShare) {
      if (this.isGroup)
      {
        this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", 2131165320));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
        {
          public void onItemClick(int paramAnonymousInt)
          {
            if (paramAnonymousInt == -1) {
              DeleteContactActivity.this.finishFragment();
            }
            while ((paramAnonymousInt != 1) || (DeleteContactActivity.this.selectedUsers.isEmpty())) {
              return;
            }
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(DeleteContactActivity.this.getParentActivity());
            localBuilder.setMessage(LocaleController.getString("AreYouSureDeleteContact", 2131165348));
            localBuilder.setTitle(LocaleController.getString("AppName", 2131165338));
            localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166111), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                ContactsController.getInstance().deleteContact(DeleteContactActivity.this.selectedUsers);
                DeleteContactActivity.this.finishFragment();
              }
            });
            localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165426), null);
            DeleteContactActivity.this.showDialog(localBuilder.create());
          }
        });
        this.actionBar.createMenu().addItemWithWidth(1, 2130837902, AndroidUtilities.dp(56.0F));
        this.searchListViewAdapter = new SearchAdapter(paramContext, null, false, false, false, false);
        this.searchListViewAdapter.setCheckedMap(this.selectedContacts);
        this.searchListViewAdapter.setUseUserCell(true);
        this.listViewAdapter = new ContactsAdapter(paramContext, 1, false, null, false);
        this.listViewAdapter.setCheckedMap(this.selectedContacts);
        this.fragmentView = new LinearLayout(paramContext);
        localLinearLayout = (LinearLayout)this.fragmentView;
        localLinearLayout.setOrientation(1);
        Object localObject = new FrameLayout(paramContext);
        localLinearLayout.addView((View)localObject, LayoutHelper.createLinear(-1, -2));
        this.userSelectEditText = new EditText(paramContext);
        this.userSelectEditText.setTypeface(MihanTheme.getMihanTypeFace());
        this.userSelectEditText.setTextSize(1, 16.0F);
        this.userSelectEditText.setHintTextColor(-6842473);
        this.userSelectEditText.setTextColor(-14606047);
        this.userSelectEditText.setInputType(655536);
        this.userSelectEditText.setMinimumHeight(AndroidUtilities.dp(54.0F));
        this.userSelectEditText.setSingleLine(false);
        this.userSelectEditText.setLines(2);
        this.userSelectEditText.setMaxLines(2);
        this.userSelectEditText.setVerticalScrollBarEnabled(true);
        this.userSelectEditText.setHorizontalScrollBarEnabled(false);
        this.userSelectEditText.setPadding(0, 0, 0, 0);
        this.userSelectEditText.setImeOptions(268435462);
        EditText localEditText = this.userSelectEditText;
        if (!LocaleController.isRTL) {
          break label845;
        }
        i = 5;
        label355:
        localEditText.setGravity(i | 0x10);
        AndroidUtilities.clearCursorDrawable(this.userSelectEditText);
        ((FrameLayout)localObject).addView(this.userSelectEditText, LayoutHelper.createFrame(-1, -2.0F, 51, 10.0F, 0.0F, 10.0F, 0.0F));
        if (!this.isAlwaysShare) {
          break label869;
        }
        if (!this.isGroup) {
          break label850;
        }
        this.userSelectEditText.setHint(LocaleController.getString("AlwaysAllowPlaceholder", 2131165321));
        label427:
        if (Build.VERSION.SDK_INT >= 11) {
          this.userSelectEditText.setTextIsSelectable(false);
        }
        this.userSelectEditText.addTextChangedListener(new TextWatcher()
        {
          public void afterTextChanged(Editable paramAnonymousEditable)
          {
            int i;
            if (!DeleteContactActivity.this.ignoreChange)
            {
              int j = 0;
              i = DeleteContactActivity.this.userSelectEditText.getSelectionEnd();
              if (paramAnonymousEditable.toString().length() >= DeleteContactActivity.this.changeString.toString().length()) {
                break label400;
              }
              paramAnonymousEditable = "";
              try
              {
                localObject = DeleteContactActivity.this.changeString.toString().substring(i, DeleteContactActivity.this.beforeChangeIndex);
                paramAnonymousEditable = (Editable)localObject;
              }
              catch (Exception localException)
              {
                for (;;)
                {
                  Object localObject;
                  FileLog.e("tmessages", localException);
                }
                DeleteContactActivity.this.listView.invalidateViews();
              }
              if (paramAnonymousEditable.length() <= 0) {
                break label395;
              }
              i = j;
              if (DeleteContactActivity.this.searching)
              {
                i = j;
                if (DeleteContactActivity.this.searchWas) {
                  i = 1;
                }
              }
              paramAnonymousEditable = DeleteContactActivity.this.userSelectEditText.getText();
              j = 0;
              while (j < DeleteContactActivity.this.allSpans.size())
              {
                localObject = (ChipSpan)DeleteContactActivity.this.allSpans.get(j);
                if (paramAnonymousEditable.getSpanStart(localObject) == -1)
                {
                  DeleteContactActivity.this.allSpans.remove(localObject);
                  DeleteContactActivity.this.selectedContacts.remove(Integer.valueOf(((ChipSpan)localObject).uid));
                }
                j += 1;
              }
            }
            for (;;)
            {
              if (i != 0)
              {
                paramAnonymousEditable = DeleteContactActivity.this.userSelectEditText.getText().toString().replace("<", "");
                if (paramAnonymousEditable.length() == 0) {
                  break;
                }
                DeleteContactActivity.access$402(DeleteContactActivity.this, true);
                DeleteContactActivity.access$502(DeleteContactActivity.this, true);
                if (DeleteContactActivity.this.listView != null)
                {
                  DeleteContactActivity.this.listView.setAdapter(DeleteContactActivity.this.searchListViewAdapter);
                  DeleteContactActivity.this.searchListViewAdapter.notifyDataSetChanged();
                  if (Build.VERSION.SDK_INT >= 11) {
                    DeleteContactActivity.this.listView.setFastScrollAlwaysVisible(false);
                  }
                  DeleteContactActivity.this.listView.setFastScrollEnabled(false);
                  DeleteContactActivity.this.listView.setVerticalScrollBarEnabled(true);
                }
                if (DeleteContactActivity.this.emptyTextView != null) {
                  DeleteContactActivity.this.emptyTextView.setText(LocaleController.getString("NoResult", 2131166020));
                }
                DeleteContactActivity.this.searchListViewAdapter.searchDialogs(paramAnonymousEditable);
              }
              return;
              label395:
              i = 1;
              continue;
              label400:
              i = 1;
            }
            DeleteContactActivity.this.searchListViewAdapter.searchDialogs(null);
            DeleteContactActivity.access$402(DeleteContactActivity.this, false);
            DeleteContactActivity.access$502(DeleteContactActivity.this, false);
            DeleteContactActivity.this.listView.setAdapter(DeleteContactActivity.this.listViewAdapter);
            DeleteContactActivity.this.listViewAdapter.notifyDataSetChanged();
            if (Build.VERSION.SDK_INT >= 11) {
              DeleteContactActivity.this.listView.setFastScrollAlwaysVisible(true);
            }
            DeleteContactActivity.this.listView.setFastScrollEnabled(true);
            DeleteContactActivity.this.listView.setVerticalScrollBarEnabled(false);
            DeleteContactActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", 2131166004));
          }
          
          public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
          {
            if (!DeleteContactActivity.this.ignoreChange)
            {
              DeleteContactActivity.access$102(DeleteContactActivity.this, DeleteContactActivity.this.userSelectEditText.getSelectionStart());
              DeleteContactActivity.access$302(DeleteContactActivity.this, new SpannableString(paramAnonymousCharSequence));
            }
          }
          
          public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        });
        localObject = new LinearLayout(paramContext);
        ((LinearLayout)localObject).setVisibility(4);
        ((LinearLayout)localObject).setOrientation(1);
        localLinearLayout.addView((View)localObject, LayoutHelper.createLinear(-1, -1));
        ((LinearLayout)localObject).setOnTouchListener(new View.OnTouchListener()
        {
          public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
          {
            return true;
          }
        });
        this.emptyTextView = new TextView(paramContext);
        this.emptyTextView.setTextColor(-8355712);
        this.emptyTextView.setTextSize(20.0F);
        this.emptyTextView.setGravity(17);
        this.emptyTextView.setText(LocaleController.getString("NoContacts", 2131166004));
        ((LinearLayout)localObject).addView(this.emptyTextView, LayoutHelper.createLinear(-1, -1, 0.5F));
        ((LinearLayout)localObject).addView(new FrameLayout(paramContext), LayoutHelper.createLinear(-1, -1, 0.5F));
        this.listView = new LetterSectionsListView(paramContext);
        this.listView.setEmptyView((View)localObject);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setFastScrollEnabled(true);
        this.listView.setScrollBarStyle(33554432);
        this.listView.setAdapter(this.listViewAdapter);
        if (Build.VERSION.SDK_INT >= 11)
        {
          this.listView.setFastScrollAlwaysVisible(true);
          paramContext = this.listView;
          if (!LocaleController.isRTL) {
            break label940;
          }
        }
      }
    }
    label845:
    label850:
    label869:
    label940:
    for (int i = 1;; i = 2)
    {
      paramContext.setVerticalScrollbarPosition(i);
      localLinearLayout.addView(this.listView, LayoutHelper.createLinear(-1, -1));
      this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          if ((DeleteContactActivity.this.searching) && (DeleteContactActivity.this.searchWas))
          {
            paramAnonymousAdapterView = (TLRPC.User)DeleteContactActivity.this.searchListViewAdapter.getItem(paramAnonymousInt);
            if (paramAnonymousAdapterView != null) {
              break label94;
            }
          }
          label94:
          boolean bool;
          label521:
          label672:
          do
          {
            int i;
            do
            {
              return;
              i = DeleteContactActivity.this.listViewAdapter.getSectionForPosition(paramAnonymousInt);
              paramAnonymousInt = DeleteContactActivity.this.listViewAdapter.getPositionInSectionForPosition(paramAnonymousInt);
            } while ((paramAnonymousInt < 0) || (i < 0));
            paramAnonymousAdapterView = (TLRPC.User)DeleteContactActivity.this.listViewAdapter.getItem(i, paramAnonymousInt);
            break;
            bool = true;
            if (DeleteContactActivity.this.selectedContacts.containsKey(Integer.valueOf(paramAnonymousAdapterView.id))) {
              bool = false;
            }
            for (;;)
            {
              try
              {
                Object localObject = (ChipSpan)DeleteContactActivity.this.selectedContacts.get(Integer.valueOf(paramAnonymousAdapterView.id));
                DeleteContactActivity.this.selectedContacts.remove(Integer.valueOf(paramAnonymousAdapterView.id));
                paramAnonymousAdapterView = new SpannableStringBuilder(DeleteContactActivity.this.userSelectEditText.getText());
                paramAnonymousAdapterView.delete(paramAnonymousAdapterView.getSpanStart(localObject), paramAnonymousAdapterView.getSpanEnd(localObject));
                DeleteContactActivity.this.allSpans.remove(localObject);
                DeleteContactActivity.access$002(DeleteContactActivity.this, true);
                DeleteContactActivity.this.userSelectEditText.setText(paramAnonymousAdapterView);
                DeleteContactActivity.this.userSelectEditText.setSelection(paramAnonymousAdapterView.length());
                DeleteContactActivity.access$002(DeleteContactActivity.this, false);
                if ((!DeleteContactActivity.this.searching) && (!DeleteContactActivity.this.searchWas)) {
                  break label672;
                }
                DeleteContactActivity.access$002(DeleteContactActivity.this, true);
                paramAnonymousAdapterView = new SpannableStringBuilder("");
                paramAnonymousView = DeleteContactActivity.this.allSpans.iterator();
                if (!paramAnonymousView.hasNext()) {
                  break label521;
                }
                localObject = (ImageSpan)paramAnonymousView.next();
                paramAnonymousAdapterView.append("<<");
                paramAnonymousAdapterView.setSpan(localObject, paramAnonymousAdapterView.length() - 2, paramAnonymousAdapterView.length(), 33);
                continue;
              }
              catch (Exception paramAnonymousAdapterView)
              {
                FileLog.e("tmessages", paramAnonymousAdapterView);
                continue;
              }
              if ((DeleteContactActivity.this.maxCount != 0) && (DeleteContactActivity.this.selectedContacts.size() == DeleteContactActivity.this.maxCount)) {
                break;
              }
              if ((DeleteContactActivity.this.chatType == 0) && (DeleteContactActivity.this.selectedContacts.size() == MessagesController.getInstance().maxGroupCount - 1))
              {
                paramAnonymousAdapterView = new AlertDialog.Builder(DeleteContactActivity.this.getParentActivity());
                paramAnonymousAdapterView.setTitle(LocaleController.getString("AppName", 2131165338));
                paramAnonymousAdapterView.setMessage(LocaleController.getString("SoftUserLimitAlert", 2131166355));
                paramAnonymousAdapterView.setPositiveButton(LocaleController.getString("OK", 2131166111), null);
                DeleteContactActivity.this.showDialog(paramAnonymousAdapterView.create());
                return;
              }
              DeleteContactActivity.access$002(DeleteContactActivity.this, true);
              DeleteContactActivity.this.createAndPutChipForUser(paramAnonymousAdapterView).uid = paramAnonymousAdapterView.id;
              DeleteContactActivity.access$002(DeleteContactActivity.this, false);
            }
            DeleteContactActivity.this.userSelectEditText.setText(paramAnonymousAdapterView);
            DeleteContactActivity.this.userSelectEditText.setSelection(paramAnonymousAdapterView.length());
            DeleteContactActivity.access$002(DeleteContactActivity.this, false);
            DeleteContactActivity.this.searchListViewAdapter.searchDialogs(null);
            DeleteContactActivity.access$402(DeleteContactActivity.this, false);
            DeleteContactActivity.access$502(DeleteContactActivity.this, false);
            DeleteContactActivity.this.listView.setAdapter(DeleteContactActivity.this.listViewAdapter);
            DeleteContactActivity.this.listViewAdapter.notifyDataSetChanged();
            if (Build.VERSION.SDK_INT >= 11) {
              DeleteContactActivity.this.listView.setFastScrollAlwaysVisible(true);
            }
            DeleteContactActivity.this.listView.setFastScrollEnabled(true);
            DeleteContactActivity.this.listView.setVerticalScrollBarEnabled(false);
            DeleteContactActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", 2131166004));
            return;
          } while (!(paramAnonymousView instanceof UserCell));
          ((UserCell)paramAnonymousView).setChecked(bool, true);
        }
      });
      this.listView.setOnScrollListener(new AbsListView.OnScrollListener()
      {
        public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
        {
          if (paramAnonymousAbsListView.isFastScrollEnabled()) {
            AndroidUtilities.clearDrawableAnimation(paramAnonymousAbsListView);
          }
        }
        
        public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt)
        {
          boolean bool = true;
          if (paramAnonymousInt == 1) {
            AndroidUtilities.hideKeyboard(DeleteContactActivity.this.userSelectEditText);
          }
          if (DeleteContactActivity.this.listViewAdapter != null)
          {
            paramAnonymousAbsListView = DeleteContactActivity.this.listViewAdapter;
            if (paramAnonymousInt == 0) {
              break label45;
            }
          }
          for (;;)
          {
            paramAnonymousAbsListView.setIsScrolling(bool);
            return;
            label45:
            bool = false;
          }
        }
      });
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", 2131165324));
      break;
      if (this.isNeverShare)
      {
        if (this.isGroup)
        {
          this.actionBar.setTitle(LocaleController.getString("NeverAllow", 2131165982));
          break;
        }
        this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", 2131165986));
        break;
      }
      this.actionBar.setTitle(LocaleController.getString("DeleteContacts", 2131165612));
      break;
      i = 3;
      break label355;
      this.userSelectEditText.setHint(LocaleController.getString("AlwaysShareWithPlaceholder", 2131165323));
      break label427;
      if (this.isNeverShare)
      {
        if (this.isGroup)
        {
          this.userSelectEditText.setHint(LocaleController.getString("NeverAllowPlaceholder", 2131165983));
          break label427;
        }
        this.userSelectEditText.setHint(LocaleController.getString("NeverShareWithPlaceholder", 2131165985));
        break label427;
      }
      this.userSelectEditText.setHint(LocaleController.getString("DeleteContacts", 2131165612));
      break label427;
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.contactsDidLoaded) {
      if (this.listViewAdapter != null) {
        this.listViewAdapter.notifyDataSetChanged();
      }
    }
    do
    {
      do
      {
        return;
        if (paramInt != NotificationCenter.updateInterfaces) {
          break;
        }
        paramInt = ((Integer)paramVarArgs[0]).intValue();
      } while (((paramInt & 0x2) == 0) && ((paramInt & 0x1) == 0) && ((paramInt & 0x4) == 0));
      updateVisibleRows(paramInt);
      return;
    } while (paramInt != NotificationCenter.chatDidCreated);
    removeSelfFromStack();
  }
  
  public boolean onFragmentCreate()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidCreated);
    return super.onFragmentCreate();
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidCreated);
  }
  
  public void onResume()
  {
    super.onResume();
    updateColors();
  }
  
  public void setDelegate(GroupCreateActivityDelegate paramGroupCreateActivityDelegate)
  {
    this.delegate = paramGroupCreateActivityDelegate;
  }
  
  public static abstract interface GroupCreateActivityDelegate
  {
    public abstract void didSelectUsers(ArrayList<Integer> paramArrayList);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\DeleteContactActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */