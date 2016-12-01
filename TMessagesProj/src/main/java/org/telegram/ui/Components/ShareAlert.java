package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_channels_exportMessageLink;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_exportedMessageLink;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.Supergram.DialogsLoaderShareAlert;
import org.telegram.ui.Supergram.Theming.MihanTheme;

public class ShareAlert
  extends BottomSheet
{
  private Switch captionCheck;
  private LinearLayout captionContainer;
  private TextView captionTextView;
  private Switch checkAllCheck;
  private LinearLayout checkAllContainer;
  private TextView checkAllTextView;
  private boolean copyLinkOnEnd;
  private LinearLayout doneButton;
  private TextView doneButtonBadgeTextView;
  private TextView doneButtonTextView;
  private TLRPC.TL_exportedMessageLink exportedMessageLink;
  private int forwardType = 1;
  private FrameLayout frameLayout;
  private RecyclerListView gridView;
  private boolean isPublicChannel;
  private GridLayoutManager layoutManager;
  private ShareDialogsAdapter listAdapter;
  private boolean loadingLink;
  private EditText nameTextView;
  private Switch qouteCheck;
  private LinearLayout qouteContainer;
  private TextView qouteTextView;
  private int scrollOffsetY;
  private ShareSearchAdapter searchAdapter;
  private EmptyTextProgressView searchEmptyView;
  private HashMap<Long, TLRPC.TL_dialog> selectedDialogs = new HashMap();
  private ArrayList<MessageObject> sendingMessageObject;
  private View shadow;
  private View shadow2;
  private Drawable shadowDrawable;
  private SlidingTabView slidingTabView;
  private ArrayList<TLRPC.TL_dialog> tabDialogs = new ArrayList();
  private int topBeforeSwitch;
  
  public ShareAlert(final Context paramContext, final ArrayList<MessageObject> paramArrayList, boolean paramBoolean, final ChatActivityEnterView paramChatActivityEnterView)
  {
    super(paramContext, true);
    this.shadowDrawable = paramContext.getResources().getDrawable(2130838130);
    this.sendingMessageObject = paramArrayList;
    this.searchAdapter = new ShareSearchAdapter(paramContext);
    this.isPublicChannel = paramBoolean;
    int i;
    if (paramChatActivityEnterView != null)
    {
      paramArrayList = paramChatActivityEnterView.getFieldText();
      paramChatActivityEnterView.setFieldText("");
      paramChatActivityEnterView.showSendButton();
      if (paramBoolean)
      {
        this.loadingLink = true;
        localObject = new TLRPC.TL_channels_exportMessageLink();
        ((TLRPC.TL_channels_exportMessageLink)localObject).id = ((MessageObject)this.sendingMessageObject.get(0)).getId();
        ((TLRPC.TL_channels_exportMessageLink)localObject).channel = MessagesController.getInputChannel(((MessageObject)this.sendingMessageObject.get(0)).messageOwner.to_id.channel_id);
        ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if (paramAnonymousTLObject != null)
                {
                  ShareAlert.access$002(ShareAlert.this, (TLRPC.TL_exportedMessageLink)paramAnonymousTLObject);
                  if (ShareAlert.this.copyLinkOnEnd) {
                    ShareAlert.this.copyLink(ShareAlert.1.this.val$context);
                  }
                }
                ShareAlert.access$302(ShareAlert.this, false);
              }
            });
          }
        });
      }
      this.containerView = new FrameLayout(paramContext)
      {
        private boolean ignoreLayout = false;
        
        protected void onDraw(Canvas paramAnonymousCanvas)
        {
          ShareAlert.this.shadowDrawable.setBounds(0, ShareAlert.this.scrollOffsetY - ShareAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
          ShareAlert.this.shadowDrawable.draw(paramAnonymousCanvas);
        }
        
        public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if ((paramAnonymousMotionEvent.getAction() == 0) && (ShareAlert.this.scrollOffsetY != 0) && (paramAnonymousMotionEvent.getY() < ShareAlert.this.scrollOffsetY))
          {
            ShareAlert.this.dismiss();
            return true;
          }
          return super.onInterceptTouchEvent(paramAnonymousMotionEvent);
        }
        
        protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
        {
          super.onLayout(paramAnonymousBoolean, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
          ShareAlert.this.updateLayout();
        }
        
        protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          int i = View.MeasureSpec.getSize(paramAnonymousInt2);
          paramAnonymousInt2 = i;
          if (Build.VERSION.SDK_INT >= 21) {
            paramAnonymousInt2 = i - AndroidUtilities.statusBarHeight;
          }
          i = Math.max(ShareAlert.this.searchAdapter.getItemCount(), ShareAlert.this.listAdapter.getItemCount());
          int j = AndroidUtilities.dp(48.0F) + Math.max(3, (int)Math.ceil(i / 4.0F)) * AndroidUtilities.dp(100.0F) + ShareAlert.backgroundPaddingTop;
          if (j < paramAnonymousInt2) {}
          for (i = 0;; i = paramAnonymousInt2 - paramAnonymousInt2 / 5 * 3 + AndroidUtilities.dp(8.0F))
          {
            if (ShareAlert.this.gridView.getPaddingTop() != i)
            {
              this.ignoreLayout = true;
              ShareAlert.this.gridView.setPadding(0, i, 0, AndroidUtilities.dp(8.0F));
              this.ignoreLayout = false;
            }
            super.onMeasure(paramAnonymousInt1, View.MeasureSpec.makeMeasureSpec(Math.min(j, paramAnonymousInt2), 1073741824));
            return;
          }
        }
        
        public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          return (!ShareAlert.this.isDismissed()) && (super.onTouchEvent(paramAnonymousMotionEvent));
        }
        
        public void requestLayout()
        {
          if (this.ignoreLayout) {
            return;
          }
          super.requestLayout();
        }
      };
      this.containerView.setWillNotDraw(false);
      this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
      this.frameLayout = new FrameLayout(paramContext);
      this.frameLayout.setBackgroundColor(-1);
      this.frameLayout.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
      this.forwardType = ((SharedPreferences)localObject).getInt("forward_type", 1);
      this.doneButton = new LinearLayout(paramContext);
      this.doneButton.setOrientation(0);
      this.doneButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(788529152, false));
      this.doneButton.setPadding(AndroidUtilities.dp(21.0F), 0, AndroidUtilities.dp(21.0F), 0);
      this.frameLayout.addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
      this.doneButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if ((ShareAlert.this.selectedDialogs.isEmpty()) && (ShareAlert.this.isPublicChannel))
          {
            if (ShareAlert.this.loadingLink)
            {
              ShareAlert.access$102(ShareAlert.this, true);
              Toast.makeText(ShareAlert.this.getContext(), LocaleController.getString("Loading", 2131165905), 0).show();
            }
            for (;;)
            {
              ShareAlert.this.dismiss();
              return;
              ShareAlert.this.copyLink(ShareAlert.this.getContext());
            }
          }
          Map.Entry localEntry;
          Iterator localIterator;
          MessageObject localMessageObject;
          if (paramChatActivityEnterView == null)
          {
            if (ShareAlert.this.forwardType == 1)
            {
              paramAnonymousView = ShareAlert.this.selectedDialogs.entrySet().iterator();
              while (paramAnonymousView.hasNext())
              {
                localEntry = (Map.Entry)paramAnonymousView.next();
                SendMessagesHelper.getInstance().sendMessage(ShareAlert.this.sendingMessageObject, ((Long)localEntry.getKey()).longValue());
              }
            }
            if (ShareAlert.this.forwardType == 2)
            {
              paramAnonymousView = ShareAlert.this.selectedDialogs.entrySet().iterator();
              while (paramAnonymousView.hasNext())
              {
                localEntry = (Map.Entry)paramAnonymousView.next();
                localIterator = ShareAlert.this.sendingMessageObject.iterator();
                while (localIterator.hasNext())
                {
                  localMessageObject = (MessageObject)localIterator.next();
                  SendMessagesHelper.getInstance().MihanProcessForwardFromMyName(localMessageObject.caption, localMessageObject, ((Long)localEntry.getKey()).longValue());
                }
              }
            }
            paramAnonymousView = ShareAlert.this.selectedDialogs.entrySet().iterator();
            while (paramAnonymousView.hasNext())
            {
              localEntry = (Map.Entry)paramAnonymousView.next();
              localIterator = ShareAlert.this.sendingMessageObject.iterator();
              while (localIterator.hasNext())
              {
                localMessageObject = (MessageObject)localIterator.next();
                SendMessagesHelper.getInstance().processForwardFromMyName(localMessageObject, ((Long)localEntry.getKey()).longValue());
              }
            }
          }
          paramAnonymousView = ShareAlert.this.selectedDialogs.entrySet().iterator();
          while (paramAnonymousView.hasNext())
          {
            localEntry = (Map.Entry)paramAnonymousView.next();
            localIterator = ShareAlert.this.sendingMessageObject.iterator();
            while (localIterator.hasNext())
            {
              localMessageObject = (MessageObject)localIterator.next();
              if ((localMessageObject.messageOwner.media != null) && (!(localMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty)) && (!(localMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)))
              {
                SendMessagesHelper.getInstance().MihanProcessForwardFromMyName(paramArrayList, localMessageObject, ((Long)localEntry.getKey()).longValue());
              }
              else
              {
                localMessageObject.messageOwner.message = paramArrayList.toString();
                SendMessagesHelper.getInstance().MihanProcessForwardFromMyName(null, localMessageObject, ((Long)localEntry.getKey()).longValue());
              }
            }
          }
          ShareAlert.this.dismiss();
        }
      });
      this.doneButtonBadgeTextView = new TextView(paramContext);
      this.doneButtonBadgeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.doneButtonBadgeTextView.setTextSize(1, 13.0F);
      this.doneButtonBadgeTextView.setTextColor(-1);
      this.doneButtonBadgeTextView.setGravity(17);
      this.doneButtonBadgeTextView.setBackgroundResource(2130837620);
      this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0F));
      this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0F), 0, AndroidUtilities.dp(8.0F), AndroidUtilities.dp(1.0F));
      this.doneButton.addView(this.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
      this.doneButtonTextView = new TextView(paramContext);
      this.doneButtonTextView.setTextSize(1, 14.0F);
      this.doneButtonTextView.setGravity(17);
      this.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0F));
      this.doneButtonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.doneButton.addView(this.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
      this.checkAllContainer = new LinearLayout(paramContext);
      this.checkAllContainer.setOrientation(1);
      this.frameLayout.addView(this.checkAllContainer, LayoutHelper.createFrame(50, 48, 19));
      this.checkAllContainer.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = ShareAlert.this.checkAllCheck;
          if (!ShareAlert.this.checkAllCheck.isChecked()) {}
          for (boolean bool = true;; bool = false)
          {
            paramAnonymousView.setChecked(bool);
            return;
          }
        }
      });
      this.checkAllTextView = new TextView(paramContext);
      this.checkAllTextView.setTextSize(1, 11.0F);
      this.checkAllTextView.setGravity(17);
      this.checkAllTextView.setText(LocaleController.getString("ToAll", 2131166807));
      this.checkAllTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.checkAllContainer.addView(this.checkAllTextView, LayoutHelper.createLinear(-2, -2, 49));
      this.checkAllCheck = new Switch(paramContext);
      this.checkAllCheck.setDuplicateParentStateEnabled(false);
      this.checkAllCheck.setFocusable(false);
      this.checkAllCheck.setFocusableInTouchMode(false);
      this.checkAllContainer.addView(this.checkAllCheck, LayoutHelper.createFrame(-2, -2, 49));
      this.checkAllCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
      {
        public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
        {
          if (paramAnonymousBoolean)
          {
            i = 0;
            while (i < ShareAlert.this.tabDialogs.size())
            {
              paramAnonymousCompoundButton = (TLRPC.TL_dialog)ShareAlert.this.tabDialogs.get(i);
              if (!ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(paramAnonymousCompoundButton.id))) {
                ShareAlert.this.selectedDialogs.put(Long.valueOf(paramAnonymousCompoundButton.id), paramAnonymousCompoundButton);
              }
              i += 1;
            }
          }
          int i = 0;
          while (i < ShareAlert.this.tabDialogs.size())
          {
            paramAnonymousCompoundButton = (TLRPC.TL_dialog)ShareAlert.this.tabDialogs.get(i);
            if (ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(paramAnonymousCompoundButton.id))) {
              ShareAlert.this.selectedDialogs.remove(Long.valueOf(paramAnonymousCompoundButton.id));
            }
            i += 1;
          }
          i = 0;
          if (i < ShareAlert.this.gridView.getAdapter().getItemCount())
          {
            if (ShareAlert.this.gridView.getAdapter() == ShareAlert.this.listAdapter) {}
            for (paramAnonymousCompoundButton = ShareAlert.this.listAdapter.getItem(i); paramAnonymousCompoundButton == null; paramAnonymousCompoundButton = ShareAlert.this.searchAdapter.getItem(i)) {
              return;
            }
            Object localObject = ShareAlert.this.gridView.getChildAt(i);
            if ((localObject instanceof ShareDialogCell))
            {
              localObject = (ShareDialogCell)localObject;
              if (!ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(paramAnonymousCompoundButton.id))) {
                break label290;
              }
              ((ShareDialogCell)localObject).setChecked(true, true);
            }
            for (;;)
            {
              i += 1;
              break;
              label290:
              ((ShareDialogCell)localObject).setChecked(false, true);
            }
          }
          ShareAlert.this.updateSelectedCount();
        }
      });
      this.qouteContainer = new LinearLayout(paramContext);
      this.qouteContainer.setOrientation(1);
      this.frameLayout.addView(this.qouteContainer, LayoutHelper.createFrame(50, 48.0F, 19, 50.0F, 0.0F, 0.0F, 0.0F));
      this.qouteContainer.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = ShareAlert.this.qouteCheck;
          if (!ShareAlert.this.qouteCheck.isChecked()) {}
          for (boolean bool = true;; bool = false)
          {
            paramAnonymousView.setChecked(bool);
            return;
          }
        }
      });
      this.qouteTextView = new TextView(paramContext);
      this.qouteTextView.setTextSize(1, 11.0F);
      this.qouteTextView.setGravity(17);
      this.qouteTextView.setText(LocaleController.getString("WithQoute", 2131166820));
      this.qouteTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.qouteContainer.addView(this.qouteTextView, LayoutHelper.createLinear(-2, -2, 49));
      this.qouteCheck = new Switch(paramContext);
      this.qouteCheck.setDuplicateParentStateEnabled(false);
      this.qouteCheck.setFocusable(false);
      this.qouteCheck.setFocusableInTouchMode(false);
      this.qouteContainer.addView(this.qouteCheck, LayoutHelper.createFrame(-2, -2, 49));
      this.qouteCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
      {
        public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
        {
          if ((paramAnonymousBoolean) && (ShareAlert.this.captionCheck.isChecked()))
          {
            ShareAlert.access$1402(ShareAlert.this, 1);
            return;
          }
          if ((!paramAnonymousBoolean) && (ShareAlert.this.captionCheck.isChecked()))
          {
            ShareAlert.access$1402(ShareAlert.this, 2);
            return;
          }
          ShareAlert.access$1402(ShareAlert.this, 3);
        }
      });
      this.captionContainer = new LinearLayout(paramContext);
      this.captionContainer.setOrientation(1);
      this.frameLayout.addView(this.captionContainer, LayoutHelper.createFrame(50, 48.0F, 19, 100.0F, 0.0F, 0.0F, 0.0F));
      this.captionContainer.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = ShareAlert.this.captionCheck;
          if (!ShareAlert.this.captionCheck.isChecked()) {}
          for (boolean bool = true;; bool = false)
          {
            paramAnonymousView.setChecked(bool);
            return;
          }
        }
      });
      this.captionTextView = new TextView(paramContext);
      this.captionTextView.setTextSize(1, 11.0F);
      this.captionTextView.setGravity(17);
      this.captionTextView.setText(LocaleController.getString("Caption", 2131165428));
      this.captionTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.captionContainer.addView(this.captionTextView, LayoutHelper.createLinear(-2, -2, 49));
      this.captionCheck = new Switch(paramContext);
      this.captionCheck.setDuplicateParentStateEnabled(false);
      this.captionCheck.setFocusable(false);
      this.captionCheck.setFocusableInTouchMode(false);
      this.captionContainer.addView(this.captionCheck, LayoutHelper.createFrame(-2, -2, 49));
      this.qouteCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
      {
        public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
        {
          if ((paramAnonymousBoolean) && (ShareAlert.this.qouteCheck.isChecked()))
          {
            ShareAlert.access$1402(ShareAlert.this, 1);
            return;
          }
          if ((paramAnonymousBoolean) && (!ShareAlert.this.qouteCheck.isChecked()))
          {
            ShareAlert.access$1402(ShareAlert.this, 2);
            return;
          }
          ShareAlert.access$1402(ShareAlert.this, 3);
        }
      });
      paramBoolean = ((SharedPreferences)localObject).getBoolean("multi_forward_tabs", true);
      i = 48;
      if (paramBoolean) {
        i = 96;
      }
      if (this.forwardType != 1) {
        break label2007;
      }
      this.qouteCheck.setChecked(true);
      this.captionCheck.setChecked(true);
      label1251:
      paramArrayList = new ImageView(paramContext);
      paramArrayList.setImageResource(2130838121);
      paramArrayList.setScaleType(ImageView.ScaleType.CENTER);
      paramArrayList.setPadding(0, AndroidUtilities.dp(2.0F), 0, 0);
      this.frameLayout.addView(paramArrayList, LayoutHelper.createFrame(48, 48.0F, 19, 150.0F, 0.0F, 0.0F, 0.0F));
      this.nameTextView = new EditText(paramContext);
      this.nameTextView.setHint(LocaleController.getString("ShareSendTo", 2131166326));
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setSingleLine(true);
      paramArrayList = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label2053;
      }
    }
    label2007:
    label2053:
    for (int j = 5;; j = 3)
    {
      paramArrayList.setGravity(j | 0x10);
      this.nameTextView.setTextSize(1, 16.0F);
      this.nameTextView.setBackgroundDrawable(null);
      this.nameTextView.setHintTextColor(-6842473);
      this.nameTextView.setImeOptions(268435456);
      this.nameTextView.setInputType(16385);
      AndroidUtilities.clearCursorDrawable(this.nameTextView);
      this.nameTextView.setTextColor(-14606047);
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -1.0F, 51, 198.0F, 2.0F, 96.0F, 0.0F));
      this.nameTextView.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramAnonymousEditable)
        {
          paramAnonymousEditable = ShareAlert.this.nameTextView.getText().toString();
          if (paramAnonymousEditable.length() != 0)
          {
            if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.searchAdapter)
            {
              ShareAlert.access$2102(ShareAlert.this, ShareAlert.this.getCurrentTop());
              ShareAlert.this.gridView.setAdapter(ShareAlert.this.searchAdapter);
              ShareAlert.this.searchAdapter.notifyDataSetChanged();
            }
            if (ShareAlert.this.searchEmptyView != null) {
              ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", 2131166020));
            }
          }
          for (;;)
          {
            if (ShareAlert.this.searchAdapter != null) {
              ShareAlert.this.searchAdapter.searchDialogs(paramAnonymousEditable);
            }
            return;
            if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.listAdapter)
            {
              int i = ShareAlert.this.getCurrentTop();
              ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", 2131166002));
              ShareAlert.this.gridView.setAdapter(ShareAlert.this.listAdapter);
              ShareAlert.this.listAdapter.notifyDataSetChanged();
              if (i > 0) {
                ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -i);
              }
            }
          }
        }
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      });
      this.gridView = new RecyclerListView(paramContext);
      this.gridView.setTag(Integer.valueOf(13));
      this.gridView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0F));
      this.gridView.setClipToPadding(false);
      paramArrayList = this.gridView;
      paramChatActivityEnterView = new GridLayoutManager(getContext(), 4);
      this.layoutManager = paramChatActivityEnterView;
      paramArrayList.setLayoutManager(paramChatActivityEnterView);
      this.gridView.setHorizontalScrollBarEnabled(false);
      this.gridView.setVerticalScrollBarEnabled(false);
      this.gridView.addItemDecoration(new RecyclerView.ItemDecoration()
      {
        public void getItemOffsets(Rect paramAnonymousRect, View paramAnonymousView, RecyclerView paramAnonymousRecyclerView, RecyclerView.State paramAnonymousState)
        {
          int j = 0;
          paramAnonymousView = (ShareAlert.Holder)paramAnonymousRecyclerView.getChildViewHolder(paramAnonymousView);
          if (paramAnonymousView != null)
          {
            int k = paramAnonymousView.getAdapterPosition();
            if (k % 4 == 0)
            {
              i = 0;
              paramAnonymousRect.left = i;
              if (k % 4 != 3) {
                break label67;
              }
            }
            label67:
            for (int i = j;; i = AndroidUtilities.dp(4.0F))
            {
              paramAnonymousRect.right = i;
              return;
              i = AndroidUtilities.dp(4.0F);
              break;
            }
          }
          paramAnonymousRect.left = AndroidUtilities.dp(4.0F);
          paramAnonymousRect.right = AndroidUtilities.dp(4.0F);
        }
      });
      this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, i, 0.0F, 0.0F));
      paramArrayList = this.gridView;
      paramChatActivityEnterView = new ShareDialogsAdapter(paramContext);
      this.listAdapter = paramChatActivityEnterView;
      paramArrayList.setAdapter(paramChatActivityEnterView);
      this.gridView.setGlowColor(-657673);
      this.gridView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if (ShareAlert.this.gridView.getAdapter() == ShareAlert.this.listAdapter) {}
          for (TLRPC.TL_dialog localTL_dialog = ShareAlert.this.listAdapter.getItem(paramAnonymousInt); localTL_dialog == null; localTL_dialog = ShareAlert.this.searchAdapter.getItem(paramAnonymousInt)) {
            return;
          }
          paramAnonymousView = (ShareDialogCell)paramAnonymousView;
          if (ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(localTL_dialog.id)))
          {
            ShareAlert.this.selectedDialogs.remove(Long.valueOf(localTL_dialog.id));
            paramAnonymousView.setChecked(false, true);
          }
          for (;;)
          {
            ShareAlert.this.updateSelectedCount();
            return;
            ShareAlert.this.selectedDialogs.put(Long.valueOf(localTL_dialog.id), localTL_dialog);
            paramAnonymousView.setChecked(true, true);
          }
        }
      });
      this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          ShareAlert.this.updateLayout();
        }
      });
      this.searchEmptyView = new EmptyTextProgressView(paramContext);
      this.searchEmptyView.setShowAtCenter(true);
      this.searchEmptyView.showTextView();
      this.searchEmptyView.setText(LocaleController.getString("NoChats", 2131166002));
      this.gridView.setEmptyView(this.searchEmptyView);
      this.containerView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, i, 0.0F, 0.0F));
      this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, 48, 51));
      this.shadow = new View(paramContext);
      this.shadow.setBackgroundResource(2130837802);
      this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0F, 51, 0.0F, 48.0F, 0.0F, 0.0F));
      if (paramBoolean)
      {
        this.slidingTabView = new SlidingTabView(paramContext);
        this.slidingTabView.addImageTab(0);
        this.slidingTabView.addImageTab(1);
        this.slidingTabView.addImageTab(2);
        this.slidingTabView.addImageTab(3);
        this.slidingTabView.addImageTab(4);
        this.slidingTabView.addImageTab(5);
        this.slidingTabView.addImageTab(6);
        this.containerView.addView(this.slidingTabView, LayoutHelper.createFrame(-1, 46.0F, 3, 0.0F, 48.0F, 0.0F, 0.0F));
        this.shadow2 = new View(paramContext);
        this.shadow2.setBackgroundResource(2130837802);
        this.containerView.addView(this.shadow2, LayoutHelper.createFrame(-1, 3.0F, 51, 0.0F, 94.0F, 0.0F, 0.0F));
      }
      updateSelectedCount();
      return;
      paramArrayList = "";
      break;
      if (this.forwardType == 2)
      {
        this.qouteCheck.setChecked(false);
        this.captionCheck.setChecked(true);
        break label1251;
      }
      this.qouteCheck.setChecked(false);
      this.captionCheck.setChecked(false);
      break label1251;
    }
  }
  
  private void copyLink(Context paramContext)
  {
    if (this.exportedMessageLink == null) {
      return;
    }
    try
    {
      ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.exportedMessageLink.link));
      Toast.makeText(paramContext, LocaleController.getString("LinkCopied", 2131165894), 0).show();
      return;
    }
    catch (Exception paramContext)
    {
      FileLog.e("tmessages", paramContext);
    }
  }
  
  private int getCurrentTop()
  {
    int j = 0;
    if (this.gridView.getChildCount() != 0)
    {
      View localView = this.gridView.getChildAt(0);
      Holder localHolder = (Holder)this.gridView.findContainingViewHolder(localView);
      if (localHolder != null)
      {
        int k = this.gridView.getPaddingTop();
        int i = j;
        if (localHolder.getAdapterPosition() == 0)
        {
          i = j;
          if (localView.getTop() >= 0) {
            i = localView.getTop();
          }
        }
        return k - i;
      }
    }
    return 64536;
  }
  
  @SuppressLint({"NewApi"})
  private void updateLayout()
  {
    int j = 0;
    if (this.gridView.getChildCount() <= 0) {}
    int i;
    do
    {
      return;
      localObject = this.gridView.getChildAt(0);
      Holder localHolder = (Holder)this.gridView.findContainingViewHolder((View)localObject);
      int k = ((View)localObject).getTop() - AndroidUtilities.dp(8.0F);
      i = j;
      if (k > 0)
      {
        i = j;
        if (localHolder != null)
        {
          i = j;
          if (localHolder.getAdapterPosition() == 0) {
            i = k;
          }
        }
      }
    } while (this.scrollOffsetY == i);
    Object localObject = this.gridView;
    this.scrollOffsetY = i;
    ((RecyclerListView)localObject).setTopGlowOffset(i);
    this.frameLayout.setTranslationY(this.scrollOffsetY);
    if (this.slidingTabView != null)
    {
      this.slidingTabView.setTranslationY(this.scrollOffsetY);
      this.shadow2.setTranslationY(this.scrollOffsetY);
    }
    this.shadow.setTranslationY(this.scrollOffsetY);
    this.searchEmptyView.setTranslationY(this.scrollOffsetY);
    this.containerView.invalidate();
  }
  
  protected boolean canDismissWithSwipe()
  {
    return false;
  }
  
  public void updateSelectedCount()
  {
    if (this.selectedDialogs.isEmpty())
    {
      this.doneButtonBadgeTextView.setVisibility(8);
      if (!this.isPublicChannel)
      {
        this.doneButtonTextView.setTextColor(-5000269);
        this.doneButton.setEnabled(false);
        this.doneButtonTextView.setText(LocaleController.getString("Send", 2131166282).toUpperCase());
        return;
      }
      this.doneButtonTextView.setTextColor(-12940081);
      this.doneButton.setEnabled(true);
      this.doneButtonTextView.setText(LocaleController.getString("CopyLink", 2131165574).toUpperCase());
      return;
    }
    this.doneButtonTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    this.doneButtonBadgeTextView.setVisibility(0);
    this.doneButtonBadgeTextView.setText(String.format("%d", new Object[] { Integer.valueOf(this.selectedDialogs.size()) }));
    this.doneButtonTextView.setTextColor(-12664327);
    this.doneButton.setEnabled(true);
    this.doneButtonTextView.setText(LocaleController.getString("Send", 2131166282).toUpperCase());
  }
  
  private class Holder
    extends RecyclerView.ViewHolder
  {
    public Holder(View paramView)
    {
      super();
    }
  }
  
  private class ShareDialogsAdapter
    extends RecyclerView.Adapter
  {
    private Context context;
    private int currentCount;
    private ArrayList<TLRPC.TL_dialog> dialogs = new ArrayList();
    
    public ShareDialogsAdapter(Context paramContext)
    {
      this.context = paramContext;
      getDialogsArray();
    }
    
    private void getDialogsArray()
    {
      this.dialogs.clear();
      ShareAlert.this.tabDialogs.clear();
      this.dialogs.addAll(new DialogsLoaderShareAlert().shareAlertGetDialogsArray());
      ShareAlert.this.tabDialogs.addAll(this.dialogs);
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).edit();
      localEditor.putInt("share_alert_selected_tab", 6);
      localEditor.commit();
    }
    
    public TLRPC.TL_dialog getItem(int paramInt)
    {
      if ((paramInt < 0) || (paramInt >= this.dialogs.size())) {
        return null;
      }
      return (TLRPC.TL_dialog)this.dialogs.get(paramInt);
    }
    
    public int getItemCount()
    {
      return this.dialogs.size();
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public void notifyDataSetChanged()
    {
      super.notifyDataSetChanged();
      getDialogsArray();
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      paramViewHolder = (ShareDialogCell)paramViewHolder.itemView;
      TLRPC.TL_dialog localTL_dialog = getItem(paramInt);
      paramViewHolder.setDialog((int)localTL_dialog.id, ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(localTL_dialog.id)), null);
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = new ShareDialogCell(this.context);
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0F)));
      return new ShareAlert.Holder(ShareAlert.this, paramViewGroup);
    }
  }
  
  public class ShareSearchAdapter
    extends RecyclerView.Adapter
  {
    private Context context;
    private int lastReqId;
    private int lastSearchId = 0;
    private String lastSearchText;
    private int reqId = 0;
    private ArrayList<DialogSearchResult> searchResult = new ArrayList();
    private Timer searchTimer;
    
    public ShareSearchAdapter(Context paramContext)
    {
      this.context = paramContext;
    }
    
    private void searchDialogsInternal(final String paramString, final int paramInt)
    {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          int k;
          try
          {
            localObject4 = paramString.trim().toLowerCase();
            if (((String)localObject4).length() == 0)
            {
              ShareAlert.ShareSearchAdapter.access$2502(ShareAlert.ShareSearchAdapter.this, -1);
              ShareAlert.ShareSearchAdapter.this.updateSearchResults(new ArrayList(), ShareAlert.ShareSearchAdapter.this.lastSearchId);
              return;
            }
            localObject3 = LocaleController.getInstance().getTranslitString((String)localObject4);
            if (((String)localObject4).equals(localObject3)) {
              break label1615;
            }
            localObject1 = localObject3;
            if (((String)localObject3).length() != 0) {
              break label1618;
            }
          }
          catch (Exception localException)
          {
            Object localObject1;
            FileLog.e("tmessages", localException);
            return;
          }
          String[] arrayOfString = new String[i + 1];
          arrayOfString[0] = localObject4;
          if (localObject1 != null) {
            arrayOfString[1] = localObject1;
          }
          localObject1 = new ArrayList();
          Object localObject5 = new ArrayList();
          int i = 0;
          int j = 0;
          Object localObject4 = new HashMap();
          Object localObject3 = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400", new Object[0]);
          int m;
          for (;;)
          {
            if (!((SQLiteCursor)localObject3).next()) {
              break label310;
            }
            long l = ((SQLiteCursor)localObject3).longValue(0);
            localObject6 = new ShareAlert.ShareSearchAdapter.DialogSearchResult(ShareAlert.ShareSearchAdapter.this, null);
            ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject6).date = ((SQLiteCursor)localObject3).intValue(1);
            ((HashMap)localObject4).put(Long.valueOf(l), localObject6);
            k = (int)l;
            m = (int)(l >> 32);
            if ((k != 0) && (m != 1))
            {
              if (k > 0)
              {
                if (((ArrayList)localObject1).contains(Integer.valueOf(k))) {
                  continue;
                }
                ((ArrayList)localObject1).add(Integer.valueOf(k));
                continue;
                label274:
                i = 0;
                break;
              }
              m = -k;
              if (!((ArrayList)localObject5).contains(Integer.valueOf(m))) {
                ((ArrayList)localObject5).add(Integer.valueOf(-k));
              }
            }
          }
          label310:
          ((SQLiteCursor)localObject3).dispose();
          label362:
          String str1;
          label441:
          String str2;
          if (!localException.isEmpty())
          {
            localObject6 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, status, name FROM users WHERE uid IN(%s)", new Object[] { TextUtils.join(",", localException) }), new Object[0]);
            i = j;
            while (((SQLiteCursor)localObject6).next())
            {
              str1 = ((SQLiteCursor)localObject6).stringValue(2);
              localObject3 = LocaleController.getInstance().getTranslitString(str1);
              localObject2 = localObject3;
              if (str1.equals(localObject3)) {
                localObject2 = null;
              }
              localObject3 = null;
              j = str1.lastIndexOf(";;;");
              if (j != -1) {
                localObject3 = str1.substring(j + 3);
              }
              m = 0;
              int n = arrayOfString.length;
              k = 0;
              if (k >= n) {
                break label1641;
              }
              str2 = arrayOfString[k];
              if ((str1.startsWith(str2)) || (str1.contains(" " + str2))) {
                break label1628;
              }
              if (localObject2 != null)
              {
                if (((String)localObject2).startsWith(str2)) {
                  break label1628;
                }
                if (((String)localObject2).contains(" " + str2))
                {
                  break label1628;
                  label537:
                  if (j == 0) {
                    break label1633;
                  }
                  localObject3 = ((SQLiteCursor)localObject6).byteBufferValue(0);
                  if (localObject3 == null) {
                    continue;
                  }
                  localObject2 = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
                  ((NativeByteBuffer)localObject3).reuse();
                  localObject3 = (ShareAlert.ShareSearchAdapter.DialogSearchResult)((HashMap)localObject4).get(Long.valueOf(((TLRPC.User)localObject2).id));
                  if (((TLRPC.User)localObject2).status != null) {
                    ((TLRPC.User)localObject2).status.expires = ((SQLiteCursor)localObject6).intValue(1);
                  }
                  if (j != 1) {
                    break label693;
                  }
                }
              }
              label693:
              for (((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).name = AndroidUtilities.generateSearchName(((TLRPC.User)localObject2).first_name, ((TLRPC.User)localObject2).last_name, str2);; ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).name = AndroidUtilities.generateSearchName("@" + ((TLRPC.User)localObject2).username, null, "@" + str2))
              {
                ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).object = ((TLObject)localObject2);
                ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).dialog.id = ((TLRPC.User)localObject2).id;
                i += 1;
                break;
                j = m;
                if (localObject3 == null) {
                  break label537;
                }
                j = m;
                if (!((String)localObject3).startsWith(str2)) {
                  break label537;
                }
                j = 2;
                break label537;
              }
            }
            ((SQLiteCursor)localObject6).dispose();
          }
          j = i;
          if (!((ArrayList)localObject5).isEmpty())
          {
            localObject5 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, name FROM chats WHERE uid IN(%s)", new Object[] { TextUtils.join(",", (Iterable)localObject5) }), new Object[0]);
            label803:
            while (((SQLiteCursor)localObject5).next())
            {
              localObject6 = ((SQLiteCursor)localObject5).stringValue(1);
              localObject3 = LocaleController.getInstance().getTranslitString((String)localObject6);
              localObject2 = localObject3;
              if (!((String)localObject6).equals(localObject3)) {
                break label1643;
              }
              localObject2 = null;
              break label1643;
              label849:
              if (j >= arrayOfString.length) {
                break label1653;
              }
              localObject3 = arrayOfString[j];
              if ((!((String)localObject6).startsWith((String)localObject3)) && (!((String)localObject6).contains(" " + (String)localObject3)) && ((localObject2 == null) || ((!((String)localObject2).startsWith((String)localObject3)) && (!((String)localObject2).contains(" " + (String)localObject3))))) {
                break label1648;
              }
              localObject6 = ((SQLiteCursor)localObject5).byteBufferValue(0);
              if (localObject6 != null)
              {
                localObject2 = TLRPC.Chat.TLdeserialize((AbstractSerializedData)localObject6, ((NativeByteBuffer)localObject6).readInt32(false), false);
                ((NativeByteBuffer)localObject6).reuse();
                if ((localObject2 != null) && (!ChatObject.isNotInChat((TLRPC.Chat)localObject2)) && ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).creator) || (((TLRPC.Chat)localObject2).editor) || (((TLRPC.Chat)localObject2).megagroup)))
                {
                  localObject6 = (ShareAlert.ShareSearchAdapter.DialogSearchResult)((HashMap)localObject4).get(Long.valueOf(-((TLRPC.Chat)localObject2).id));
                  ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject6).name = AndroidUtilities.generateSearchName(((TLRPC.Chat)localObject2).title, null, (String)localObject3);
                  ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject6).object = ((TLObject)localObject2);
                  ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject6).dialog.id = (-((TLRPC.Chat)localObject2).id);
                  i += 1;
                }
              }
            }
            ((SQLiteCursor)localObject5).dispose();
            j = i;
          }
          localObject5 = new ArrayList(j);
          Object localObject2 = ((HashMap)localObject4).values().iterator();
          while (((Iterator)localObject2).hasNext())
          {
            localObject3 = (ShareAlert.ShareSearchAdapter.DialogSearchResult)((Iterator)localObject2).next();
            if ((((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).object != null) && (((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).name != null)) {
              ((ArrayList)localObject5).add(localObject3);
            }
          }
          Object localObject6 = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid", new Object[0]);
          label1181:
          label1373:
          label1615:
          label1618:
          label1628:
          label1633:
          label1641:
          label1643:
          label1648:
          label1653:
          label1655:
          label1660:
          label1667:
          for (;;)
          {
            if (((SQLiteCursor)localObject6).next())
            {
              if (!((HashMap)localObject4).containsKey(Long.valueOf(((SQLiteCursor)localObject6).intValue(3))))
              {
                str1 = ((SQLiteCursor)localObject6).stringValue(2);
                localObject3 = LocaleController.getInstance().getTranslitString(str1);
                localObject2 = localObject3;
                if (str1.equals(localObject3)) {
                  localObject2 = null;
                }
                localObject3 = null;
                i = str1.lastIndexOf(";;;");
                if (i != -1) {
                  localObject3 = str1.substring(i + 3);
                }
                k = 0;
                m = arrayOfString.length;
                j = 0;
              }
            }
            else {
              for (;;)
              {
                if (j >= m) {
                  break label1667;
                }
                str2 = arrayOfString[j];
                if ((!str1.startsWith(str2)) && (!str1.contains(" " + str2))) {
                  if (localObject2 != null)
                  {
                    if (((String)localObject2).startsWith(str2)) {
                      break label1655;
                    }
                    if (((String)localObject2).contains(" " + str2)) {
                      break label1655;
                    }
                  }
                }
                for (;;)
                {
                  if (i == 0) {
                    break label1660;
                  }
                  localObject3 = ((SQLiteCursor)localObject6).byteBufferValue(0);
                  if (localObject3 == null) {
                    break label1181;
                  }
                  localObject2 = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
                  ((NativeByteBuffer)localObject3).reuse();
                  localObject3 = new ShareAlert.ShareSearchAdapter.DialogSearchResult(ShareAlert.ShareSearchAdapter.this, null);
                  if (((TLRPC.User)localObject2).status != null) {
                    ((TLRPC.User)localObject2).status.expires = ((SQLiteCursor)localObject6).intValue(1);
                  }
                  ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).dialog.id = ((TLRPC.User)localObject2).id;
                  ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).object = ((TLObject)localObject2);
                  if (i == 1) {}
                  for (((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).name = AndroidUtilities.generateSearchName(((TLRPC.User)localObject2).first_name, ((TLRPC.User)localObject2).last_name, str2);; ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).name = AndroidUtilities.generateSearchName("@" + ((TLRPC.User)localObject2).username, null, "@" + str2))
                  {
                    ((ArrayList)localObject5).add(localObject3);
                    break;
                    i = k;
                    if (localObject3 == null) {
                      break label1373;
                    }
                    i = k;
                    if (!((String)localObject3).startsWith(str2)) {
                      break label1373;
                    }
                    i = 2;
                    break label1373;
                  }
                  ((SQLiteCursor)localObject6).dispose();
                  Collections.sort((List)localObject5, new Comparator()
                  {
                    public int compare(ShareAlert.ShareSearchAdapter.DialogSearchResult paramAnonymous2DialogSearchResult1, ShareAlert.ShareSearchAdapter.DialogSearchResult paramAnonymous2DialogSearchResult2)
                    {
                      if (paramAnonymous2DialogSearchResult1.date < paramAnonymous2DialogSearchResult2.date) {
                        return 1;
                      }
                      if (paramAnonymous2DialogSearchResult1.date > paramAnonymous2DialogSearchResult2.date) {
                        return -1;
                      }
                      return 0;
                    }
                  });
                  ShareAlert.ShareSearchAdapter.this.updateSearchResults((ArrayList)localObject5, paramInt);
                  return;
                  localObject2 = null;
                  if (localObject2 == null) {
                    break label274;
                  }
                  i = 1;
                  break;
                  j = 1;
                  break label537;
                  k += 1;
                  m = j;
                  break label441;
                  break label362;
                  j = 0;
                  break label849;
                  j += 1;
                  break label849;
                  break label803;
                  i = 1;
                }
                j += 1;
                k = i;
              }
            }
          }
        }
      });
    }
    
    private void updateSearchResults(final ArrayList<DialogSearchResult> paramArrayList, final int paramInt)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (paramInt != ShareAlert.ShareSearchAdapter.this.lastSearchId) {
            return;
          }
          int i = 0;
          if (i < paramArrayList.size())
          {
            Object localObject = (ShareAlert.ShareSearchAdapter.DialogSearchResult)paramArrayList.get(i);
            if ((((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject).object instanceof TLRPC.User))
            {
              localObject = (TLRPC.User)((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject).object;
              MessagesController.getInstance().putUser((TLRPC.User)localObject, true);
            }
            for (;;)
            {
              i += 1;
              break;
              if ((((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject).object instanceof TLRPC.Chat))
              {
                localObject = (TLRPC.Chat)((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject).object;
                MessagesController.getInstance().putChat((TLRPC.Chat)localObject, true);
              }
            }
          }
          if ((!ShareAlert.ShareSearchAdapter.this.searchResult.isEmpty()) && (paramArrayList.isEmpty()))
          {
            i = 1;
            label128:
            if ((!ShareAlert.ShareSearchAdapter.this.searchResult.isEmpty()) || (!paramArrayList.isEmpty())) {
              break label263;
            }
          }
          label263:
          for (int j = 1;; j = 0)
          {
            if (i != 0) {
              ShareAlert.access$2102(ShareAlert.this, ShareAlert.this.getCurrentTop());
            }
            ShareAlert.ShareSearchAdapter.access$2802(ShareAlert.ShareSearchAdapter.this, paramArrayList);
            ShareAlert.ShareSearchAdapter.this.notifyDataSetChanged();
            if ((j != 0) || (i != 0) || (ShareAlert.this.topBeforeSwitch <= 0)) {
              break;
            }
            ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -ShareAlert.this.topBeforeSwitch);
            ShareAlert.access$2102(ShareAlert.this, 64536);
            return;
            i = 0;
            break label128;
          }
        }
      });
    }
    
    public TLRPC.TL_dialog getItem(int paramInt)
    {
      return ((DialogSearchResult)this.searchResult.get(paramInt)).dialog;
    }
    
    public int getItemCount()
    {
      return this.searchResult.size();
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      paramViewHolder = (ShareDialogCell)paramViewHolder.itemView;
      DialogSearchResult localDialogSearchResult = (DialogSearchResult)this.searchResult.get(paramInt);
      paramViewHolder.setDialog((int)localDialogSearchResult.dialog.id, ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(localDialogSearchResult.dialog.id)), localDialogSearchResult.name);
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = new ShareDialogCell(this.context);
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0F)));
      return new ShareAlert.Holder(ShareAlert.this, paramViewGroup);
    }
    
    public void searchDialogs(final String paramString)
    {
      if ((paramString != null) && (this.lastSearchText != null) && (paramString.equals(this.lastSearchText))) {
        return;
      }
      this.lastSearchText = paramString;
      try
      {
        if (this.searchTimer != null)
        {
          this.searchTimer.cancel();
          this.searchTimer = null;
        }
        if ((paramString == null) || (paramString.length() == 0))
        {
          this.searchResult.clear();
          ShareAlert.access$2102(ShareAlert.this, ShareAlert.this.getCurrentTop());
          notifyDataSetChanged();
          return;
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
        final int i = this.lastSearchId + 1;
        this.lastSearchId = i;
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new TimerTask()
        {
          public void run()
          {
            try
            {
              cancel();
              ShareAlert.ShareSearchAdapter.this.searchTimer.cancel();
              ShareAlert.ShareSearchAdapter.access$2902(ShareAlert.ShareSearchAdapter.this, null);
              ShareAlert.ShareSearchAdapter.this.searchDialogsInternal(paramString, i);
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
        }, 200L, 300L);
      }
    }
    
    private class DialogSearchResult
    {
      public int date;
      public TLRPC.TL_dialog dialog = new TLRPC.TL_dialog();
      public CharSequence name;
      public TLObject object;
      
      private DialogSearchResult() {}
    }
  }
  
  class SlidingTabView
    extends LinearLayout
  {
    private float animateTabXTo = 0.0F;
    private int iconColor;
    private DecelerateInterpolator interpolator;
    private Paint paint = new Paint();
    HashMap<Integer, Integer> positionOfTab = new HashMap();
    private int sIconColor;
    private int selectedTab = 6;
    private long startAnimationTime = 0L;
    private float startAnimationX = 0.0F;
    private int tabCount = 0;
    HashMap<Integer, Integer> tabInPosition = new HashMap();
    private float tabWidth = 0.0F;
    private float tabX = 0.0F;
    HashMap<Integer, ImageView> tabs = new HashMap();
    private long totalAnimationDiff = 0L;
    
    public SlidingTabView(Context paramContext)
    {
      super();
      setOrientation(0);
      setBackgroundColor(-1118482);
      setWeightSum(7.0F);
      this.sIconColor = -16740097;
      this.iconColor = -11974327;
      this.paint.setColor(this.sIconColor);
      setWillNotDraw(false);
      this.interpolator = new DecelerateInterpolator();
    }
    
    private void animateToTab(int paramInt)
    {
      this.animateTabXTo = (paramInt * this.tabWidth);
      this.startAnimationX = this.tabX;
      this.totalAnimationDiff = 0L;
      this.startAnimationTime = System.currentTimeMillis();
      invalidate();
    }
    
    private void didSelectTab(int paramInt)
    {
      if (this.selectedTab == paramInt) {}
      do
      {
        return;
        this.selectedTab = paramInt;
        animateToTab(paramInt);
        SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).edit();
        localEditor.putInt("share_alert_selected_tab", paramInt);
        localEditor.commit();
      } while (ShareAlert.this.listAdapter == null);
      ShareAlert.this.listAdapter.notifyDataSetChanged();
    }
    
    public void addImageTab(int paramInt)
    {
      final int i = this.tabCount;
      ImageView localImageView = new ImageView(getContext());
      localImageView.setFocusable(true);
      localImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      switch (paramInt)
      {
      default: 
        if (paramInt == this.selectedTab) {
          MihanTheme.setColorFilter(localImageView.getDrawable(), this.sIconColor);
        }
        break;
      }
      for (;;)
      {
        this.tabs.put(Integer.valueOf(paramInt), localImageView);
        this.tabInPosition.put(Integer.valueOf(i), Integer.valueOf(paramInt));
        this.positionOfTab.put(Integer.valueOf(paramInt), Integer.valueOf(i));
        localImageView.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            ShareAlert.SlidingTabView.this.didSelectTab(i);
            ShareAlert.SlidingTabView.this.setTabsLayout(i);
          }
        });
        addView(localImageView);
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)localImageView.getLayoutParams();
        localLayoutParams.height = -1;
        localLayoutParams.width = 0;
        localLayoutParams.weight = 1.0F;
        localImageView.setLayoutParams(localLayoutParams);
        this.tabCount += 1;
        return;
        localImageView.setImageResource(2130838153);
        break;
        localImageView.setImageResource(2130838155);
        break;
        localImageView.setImageResource(2130838161);
        break;
        localImageView.setImageResource(2130838159);
        break;
        localImageView.setImageResource(2130838165);
        break;
        localImageView.setImageResource(2130838157);
        break;
        localImageView.setImageResource(2130838148);
        break;
        MihanTheme.setColorFilter(localImageView.getDrawable(), this.iconColor);
      }
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      if (this.tabX != this.animateTabXTo)
      {
        long l1 = System.currentTimeMillis();
        long l2 = this.startAnimationTime;
        this.startAnimationTime = System.currentTimeMillis();
        this.totalAnimationDiff += l1 - l2;
        if (this.totalAnimationDiff <= 200L) {
          break label116;
        }
        this.totalAnimationDiff = 200L;
        this.tabX = this.animateTabXTo;
      }
      for (;;)
      {
        float f1 = this.tabX;
        float f2 = getHeight() - AndroidUtilities.dp(4.0F);
        float f3 = this.tabX;
        paramCanvas.drawRect(f1, f2, this.tabWidth + f3, getHeight(), this.paint);
        return;
        label116:
        this.tabX = (this.startAnimationX + this.interpolator.getInterpolation((float)this.totalAnimationDiff / 200.0F) * (this.animateTabXTo - this.startAnimationX));
        invalidate();
      }
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      this.tabWidth = ((paramInt3 - paramInt1) / this.tabCount);
      float f = this.tabWidth * this.selectedTab;
      this.tabX = f;
      this.animateTabXTo = f;
    }
    
    public void setTabsLayout(int paramInt)
    {
      Iterator localIterator = this.tabs.keySet().iterator();
      while (localIterator.hasNext())
      {
        int i = ((Integer)localIterator.next()).intValue();
        MihanTheme.setColorFilter(((ImageView)this.tabs.get(Integer.valueOf(i))).getDrawable(), this.iconColor);
      }
      MihanTheme.setColorFilter(((ImageView)this.tabs.get(Integer.valueOf(paramInt))).getDrawable(), this.sIconColor);
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Components\ShareAlert.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */