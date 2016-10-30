package org.telegram.ui.Mihangram.Theming;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.Mihangram.Theming.ColorPicker.ColorSelectorDialog;
import org.telegram.ui.Mihangram.Theming.ColorPicker.ColorSelectorDialog.OnColorChangedListener;

public class ThemingChatActivity
  extends BaseFragment
{
  private int BGColorRow;
  private int BGGradientColorRow;
  private int BGGradientRow;
  private int actionAMIconColorRow;
  private int actionAvatarRadiusRow;
  private int actionColorRow;
  private int actionGradientColorRow;
  private int actionGradientRow;
  private int actionIconColorRow;
  private int actionSectionRow2;
  private int actionSubTitleColorRow;
  private int actionTitleColorRow;
  private int dateBgColorRow;
  private int dateTextColorRow;
  private int editorColorRow;
  private int editorGradientColorRow;
  private int editorGradientRow;
  private int editorIconColorRow;
  private int editorSectionRow;
  private int editorSectionRow2;
  private int editorSendIconColorRow;
  private int editorTextColorRow;
  private int emojiColorRow;
  private int emojiGradientColorRow;
  private int emojiGradientRow;
  private int emojiSectionRow;
  private int emojiSectionRow2;
  private int emojiSelectedTabColorRow;
  private int emojiTabColorRow;
  private int emojiTabGradientColorRow;
  private int emojiTabGradientRow;
  private int emojiTabIconColorRow;
  private int emojiTabUnderlineColorRow;
  private int groupAvatarRadiusRow;
  private int groupMemberColorRow;
  private int leftBubbleColorRow;
  private int leftForwardedNameColorRow;
  private int leftLinkColorRow;
  private int leftTextColorRow;
  private int leftTimeColorRow;
  private ListAdapter listAdapter;
  private ListView listView;
  private int messageSectionRow;
  private int messageSectionRow2;
  private int rightBubbleColorRow;
  private int rightForwardedNameColorRow;
  private int rightLinkColorRow;
  private int rightTextColorRow;
  private int rightTimeColorRow;
  private int rowCount = 0;
  private int selectedMessageBGColorRow;
  private int setBGColorRow;
  private int setGroupMemberColorRow;
  
  private void selectChatBGColor(View paramView, final SharedPreferences paramSharedPreferences, final String paramString)
  {
    int j = 0;
    int i = -1;
    switch (paramString.hashCode())
    {
    default: 
      switch (i)
      {
      default: 
        i = j;
      }
      break;
    }
    for (;;)
    {
      ((LayoutInflater)getParentActivity().getSystemService("layout_inflater")).inflate(2130903072, null, false);
      new ColorSelectorDialog(getParentActivity(), new ColorSelectorDialog.OnColorChangedListener()
      {
        public void colorChanged(int paramAnonymousInt)
        {
          paramSharedPreferences.edit().putInt(paramString, paramAnonymousInt).commit();
          if (ThemingChatActivity.this.listView != null) {
            ThemingChatActivity.this.listView.invalidateViews();
          }
        }
      }, i, 0, 0, true).show();
      return;
      if (!paramString.equals("theme_chat_bg_color")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("theme_chat_bg_gcolor")) {
        break;
      }
      i = 1;
      break;
      i = paramSharedPreferences.getInt(paramString, getBGColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getBGGradientColor(paramSharedPreferences));
    }
  }
  
  private void selectColor(View paramView, final SharedPreferences paramSharedPreferences, final String paramString)
  {
    int j = 0;
    int i = -1;
    switch (paramString.hashCode())
    {
    default: 
      switch (i)
      {
      default: 
        i = j;
      }
      break;
    }
    for (;;)
    {
      ((LayoutInflater)getParentActivity().getSystemService("layout_inflater")).inflate(2130903072, null, false);
      new ColorSelectorDialog(getParentActivity(), new ColorSelectorDialog.OnColorChangedListener()
      {
        public void colorChanged(int paramAnonymousInt)
        {
          paramSharedPreferences.edit().putInt(paramString, paramAnonymousInt).commit();
          if (ThemingChatActivity.this.listView != null) {
            ThemingChatActivity.this.listView.invalidateViews();
          }
        }
      }, i, 0, 0, true).show();
      return;
      if (!paramString.equals("theme_chat_action_color")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("theme_chat_action_gcolor")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("theme_chat_action_icolor")) {
        break;
      }
      i = 2;
      break;
      if (!paramString.equals("theme_chat_action_amicolor")) {
        break;
      }
      i = 3;
      break;
      if (!paramString.equals("theme_chat_action_tcolor")) {
        break;
      }
      i = 4;
      break;
      if (!paramString.equals("theme_chat_action_stcolor")) {
        break;
      }
      i = 5;
      break;
      if (!paramString.equals("theme_chat_editor_color")) {
        break;
      }
      i = 6;
      break;
      if (!paramString.equals("theme_chat_editor_gcolor")) {
        break;
      }
      i = 7;
      break;
      if (!paramString.equals("theme_chat_editor_tcolor")) {
        break;
      }
      i = 8;
      break;
      if (!paramString.equals("theme_chat_editor_icolor")) {
        break;
      }
      i = 9;
      break;
      if (!paramString.equals("theme_chat_editor_sicolor")) {
        break;
      }
      i = 10;
      break;
      i = paramSharedPreferences.getInt(paramString, getChatActionBarColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getChatActionBarGradientcolor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getChatActionBarIconColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getChatAModeIconColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getChatActionBarTitleColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getChatActionBarSubTitleColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getChatEditorColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getChatEditorGradientColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getChatEditorTextColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getChatEditorIconColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getChatEditorSendIconColor(paramSharedPreferences));
    }
  }
  
  private void selectEmojiColor(View paramView, final SharedPreferences paramSharedPreferences, final String paramString)
  {
    int j = 0;
    int i = -1;
    switch (paramString.hashCode())
    {
    default: 
      switch (i)
      {
      default: 
        i = j;
      }
      break;
    }
    for (;;)
    {
      ((LayoutInflater)getParentActivity().getSystemService("layout_inflater")).inflate(2130903072, null, false);
      new ColorSelectorDialog(getParentActivity(), new ColorSelectorDialog.OnColorChangedListener()
      {
        public void colorChanged(int paramAnonymousInt)
        {
          paramSharedPreferences.edit().putInt(paramString, paramAnonymousInt).commit();
          if (ThemingChatActivity.this.listView != null) {
            ThemingChatActivity.this.listView.invalidateViews();
          }
        }
      }, i, 0, 0, true).show();
      return;
      if (!paramString.equals("theme_emoji_color")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("theme_emoji_gcolor")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("theme_emoji_tab_color")) {
        break;
      }
      i = 2;
      break;
      if (!paramString.equals("theme_emoji_tab_gcolor")) {
        break;
      }
      i = 3;
      break;
      if (!paramString.equals("theme_emoji_tab_ucolor")) {
        break;
      }
      i = 4;
      break;
      if (!paramString.equals("theme_emoji_tab_scolor")) {
        break;
      }
      i = 5;
      break;
      if (!paramString.equals("theme_emoji_tab_icolor")) {
        break;
      }
      i = 6;
      break;
      i = paramSharedPreferences.getInt(paramString, getEmojiColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getEmojiGradientColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getEmojiTabColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getEmojiTabGradientColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getEmojiTabUnderlineColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getEmojiSelectedTabColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getEmojiTabIconColor(paramSharedPreferences));
    }
  }
  
  private void selectGradient(final SharedPreferences paramSharedPreferences, final String paramString)
  {
    BottomSheet.Builder localBuilder = new BottomSheet.Builder(getParentActivity());
    localBuilder.setItems(MihanTheme.items, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramSharedPreferences.edit().putInt(paramString, paramAnonymousInt).commit();
        if (ThemingChatActivity.this.listView != null) {
          ThemingChatActivity.this.listView.invalidateViews();
        }
      }
    });
    showDialog(localBuilder.create());
  }
  
  private void selectMessageColor(View paramView, final SharedPreferences paramSharedPreferences, final String paramString)
  {
    int j = 0;
    int i = -1;
    switch (paramString.hashCode())
    {
    default: 
      switch (i)
      {
      default: 
        i = j;
      }
      break;
    }
    for (;;)
    {
      ((LayoutInflater)getParentActivity().getSystemService("layout_inflater")).inflate(2130903072, null, false);
      new ColorSelectorDialog(getParentActivity(), new ColorSelectorDialog.OnColorChangedListener()
      {
        public void colorChanged(int paramAnonymousInt)
        {
          paramSharedPreferences.edit().putInt(paramString, paramAnonymousInt).commit();
          if (ThemingChatActivity.this.listView != null) {
            ThemingChatActivity.this.listView.invalidateViews();
          }
        }
      }, i, 0, 0, true).show();
      return;
      if (!paramString.equals("theme_rbubble_color")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("theme_lbubble_color")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("theme_rtext_color")) {
        break;
      }
      i = 2;
      break;
      if (!paramString.equals("theme_ltext_color")) {
        break;
      }
      i = 3;
      break;
      if (!paramString.equals("theme_rtime_color")) {
        break;
      }
      i = 4;
      break;
      if (!paramString.equals("theme_ltime_color")) {
        break;
      }
      i = 5;
      break;
      if (!paramString.equals("theme_rlink_color")) {
        break;
      }
      i = 6;
      break;
      if (!paramString.equals("theme_llink_color")) {
        break;
      }
      i = 7;
      break;
      if (!paramString.equals("theme_rfname_color")) {
        break;
      }
      i = 8;
      break;
      if (!paramString.equals("theme_lfname_color")) {
        break;
      }
      i = 9;
      break;
      if (!paramString.equals("theme_member_color")) {
        break;
      }
      i = 10;
      break;
      if (!paramString.equals("theme_date_bgcolor")) {
        break;
      }
      i = 11;
      break;
      if (!paramString.equals("theme_date_tcolor")) {
        break;
      }
      i = 12;
      break;
      if (!paramString.equals("theme_smsg_bgcolor")) {
        break;
      }
      i = 13;
      break;
      i = paramSharedPreferences.getInt(paramString, getRightBubbleColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getLeftBubbleColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getRightTextColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getLeftTextColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getRightTimeColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getLeftTimeColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getRightLinkColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getLeftLinkColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getRightFNameColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getLeftFNameColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getGroupMemberColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getDateBGColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getDateTextColor(paramSharedPreferences));
      continue;
      i = paramSharedPreferences.getInt(paramString, getSMessagesBGColor(paramSharedPreferences));
    }
  }
  
  private void updateColors(ActionBarMenu paramActionBarMenu)
  {
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    int i = ((SharedPreferences)localObject).getInt("theme_setting_action_color", MihanTheme.getThemeColor((SharedPreferences)localObject));
    int j = ((SharedPreferences)localObject).getInt("theme_setting_action_gradient", 0);
    int k = ((SharedPreferences)localObject).getInt("theme_setting_action_gcolor", i);
    if (j != 0)
    {
      GradientDrawable localGradientDrawable = MihanTheme.setGradiant(i, k, MihanTheme.getGradientOrientation(j));
      this.actionBar.setBackgroundDrawable(localGradientDrawable);
    }
    for (;;)
    {
      i = ((SharedPreferences)localObject).getInt("theme_setting_action_icolor", -1);
      this.actionBar.setTitleColor(((SharedPreferences)localObject).getInt("theme_setting_action_tcolor", i));
      localObject = ApplicationLoader.applicationContext.getResources().getDrawable(2130837829);
      MihanTheme.setColorFilter((Drawable)localObject, i);
      this.actionBar.setBackButtonDrawable((Drawable)localObject);
      MihanTheme.setColorFilter(paramActionBarMenu.getItem(1).getImageView().getDrawable(), i);
      return;
      this.actionBar.setBackgroundColor(i);
    }
  }
  
  public View createView(final Context paramContext)
  {
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("ThemingChatScreen", 2131166693));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1)
        {
          ThemingChatActivity.this.finishFragment();
          return;
        }
        ThemingChatActivity.this.presentFragment(new DialogsActivity(null));
      }
    });
    Object localObject = this.actionBar.createMenu();
    ((ActionBarMenu)localObject).addItemWithWidth(1, 2130837869, AndroidUtilities.dp(56.0F));
    updateColors((ActionBarMenu)localObject);
    this.fragmentView = new FrameLayout(paramContext);
    localObject = (FrameLayout)this.fragmentView;
    this.listAdapter = new ListAdapter(paramContext);
    this.listView = new ListView(paramContext);
    this.listView.setDivider(null);
    this.listView.setDividerHeight(0);
    this.listView.setVerticalScrollBarEnabled(false);
    AndroidUtilities.setListViewEdgeEffectColor(this.listView, AvatarDrawable.getProfileBackColorForId(5));
    ((FrameLayout)localObject).addView(this.listView);
    paramContext = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
    paramContext.width = -1;
    paramContext.height = -1;
    this.listView.setLayoutParams(paramContext);
    this.listView.setAdapter(this.listAdapter);
    paramContext = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, final View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        if (ThemingChatActivity.this.getParentActivity() == null) {}
        label622:
        label693:
        label1083:
        label1155:
        do
        {
          return;
          if (paramAnonymousInt == ThemingChatActivity.this.actionColorRow)
          {
            ThemingChatActivity.this.selectColor(paramAnonymousView, paramContext, "theme_chat_action_color");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.actionGradientRow)
          {
            ThemingChatActivity.this.selectGradient(paramContext, "theme_chat_action_gradient");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.actionGradientColorRow)
          {
            ThemingChatActivity.this.selectColor(paramAnonymousView, paramContext, "theme_chat_action_gcolor");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.actionIconColorRow)
          {
            ThemingChatActivity.this.selectColor(paramAnonymousView, paramContext, "theme_chat_action_icolor");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.actionTitleColorRow)
          {
            ThemingChatActivity.this.selectColor(paramAnonymousView, paramContext, "theme_chat_action_tcolor");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.actionSubTitleColorRow)
          {
            ThemingChatActivity.this.selectColor(paramAnonymousView, paramContext, "theme_chat_action_stcolor");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.actionAMIconColorRow)
          {
            ThemingChatActivity.this.selectColor(paramAnonymousView, paramContext, "theme_chat_action_amicolor");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.editorColorRow)
          {
            ThemingChatActivity.this.selectColor(paramAnonymousView, paramContext, "theme_chat_editor_color");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.editorGradientRow)
          {
            ThemingChatActivity.this.selectGradient(paramContext, "theme_chat_editor_gradient");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.editorGradientColorRow)
          {
            ThemingChatActivity.this.selectColor(paramAnonymousView, paramContext, "theme_chat_editor_gcolor");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.editorTextColorRow)
          {
            ThemingChatActivity.this.selectColor(paramAnonymousView, paramContext, "theme_chat_editor_tcolor");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.editorIconColorRow)
          {
            ThemingChatActivity.this.selectColor(paramAnonymousView, paramContext, "theme_chat_editor_icolor");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.editorSendIconColorRow)
          {
            ThemingChatActivity.this.selectColor(paramAnonymousView, paramContext, "theme_chat_editor_sicolor");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.emojiColorRow)
          {
            ThemingChatActivity.this.selectEmojiColor(paramAnonymousView, paramContext, "theme_emoji_color");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.emojiGradientRow)
          {
            ThemingChatActivity.this.selectGradient(paramContext, "theme_emoji_gradient");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.emojiGradientColorRow)
          {
            ThemingChatActivity.this.selectEmojiColor(paramAnonymousView, paramContext, "theme_emoji_gcolor");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.emojiTabColorRow)
          {
            ThemingChatActivity.this.selectEmojiColor(paramAnonymousView, paramContext, "theme_emoji_tab_color");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.emojiTabGradientRow)
          {
            ThemingChatActivity.this.selectGradient(paramContext, "theme_emoji_tab_gradient");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.emojiTabGradientColorRow)
          {
            ThemingChatActivity.this.selectEmojiColor(paramAnonymousView, paramContext, "theme_emoji_tab_gcolor");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.emojiTabIconColorRow)
          {
            ThemingChatActivity.this.selectEmojiColor(paramAnonymousView, paramContext, "theme_emoji_tab_icolor");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.emojiTabUnderlineColorRow)
          {
            ThemingChatActivity.this.selectEmojiColor(paramAnonymousView, paramContext, "theme_emoji_tab_ucolor");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.emojiSelectedTabColorRow)
          {
            ThemingChatActivity.this.selectEmojiColor(paramAnonymousView, paramContext, "theme_emoji_tab_scolor");
            return;
          }
          boolean bool2;
          boolean bool1;
          if (paramAnonymousInt == ThemingChatActivity.this.setBGColorRow)
          {
            bool2 = paramContext.getBoolean("theme_set_chat_bgcolor", false);
            paramAnonymousAdapterView = paramContext.edit();
            if (!bool2)
            {
              bool1 = true;
              paramAnonymousAdapterView.putBoolean("theme_set_chat_bgcolor", bool1);
              paramAnonymousAdapterView.commit();
              if ((paramAnonymousView instanceof TextCheckCell))
              {
                paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                if (bool2) {
                  break label693;
                }
              }
            }
            for (bool1 = true;; bool1 = false)
            {
              paramAnonymousAdapterView.setChecked(bool1);
              if (ThemingChatActivity.this.listView == null) {
                break;
              }
              ThemingChatActivity.this.listView.invalidateViews();
              return;
              bool1 = false;
              break label622;
            }
          }
          if (paramAnonymousInt == ThemingChatActivity.this.BGColorRow)
          {
            ThemingChatActivity.this.selectChatBGColor(paramAnonymousView, paramContext, "theme_chat_bg_color");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.BGGradientRow)
          {
            ThemingChatActivity.this.selectGradient(paramContext, "theme_chat_bg_gradient");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.BGGradientColorRow)
          {
            ThemingChatActivity.this.selectChatBGColor(paramAnonymousView, paramContext, "theme_chat_bg_gcolor");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.rightBubbleColorRow)
          {
            ThemingChatActivity.this.selectMessageColor(paramAnonymousView, paramContext, "theme_rbubble_color");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.leftBubbleColorRow)
          {
            ThemingChatActivity.this.selectMessageColor(paramAnonymousView, paramContext, "theme_lbubble_color");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.rightTextColorRow)
          {
            ThemingChatActivity.this.selectMessageColor(paramAnonymousView, paramContext, "theme_rtext_color");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.leftTextColorRow)
          {
            ThemingChatActivity.this.selectMessageColor(paramAnonymousView, paramContext, "theme_ltext_color");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.rightTimeColorRow)
          {
            ThemingChatActivity.this.selectMessageColor(paramAnonymousView, paramContext, "theme_rtime_color");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.leftTimeColorRow)
          {
            ThemingChatActivity.this.selectMessageColor(paramAnonymousView, paramContext, "theme_ltime_color");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.rightLinkColorRow)
          {
            ThemingChatActivity.this.selectMessageColor(paramAnonymousView, paramContext, "theme_rlink_color");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.leftLinkColorRow)
          {
            ThemingChatActivity.this.selectMessageColor(paramAnonymousView, paramContext, "theme_llink_color");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.rightForwardedNameColorRow)
          {
            ThemingChatActivity.this.selectMessageColor(paramAnonymousView, paramContext, "theme_rfname_color");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.leftForwardedNameColorRow)
          {
            ThemingChatActivity.this.selectMessageColor(paramAnonymousView, paramContext, "theme_lfname_color");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.setGroupMemberColorRow)
          {
            bool2 = paramContext.getBoolean("theme_set_gmcolor", false);
            paramAnonymousAdapterView = paramContext.edit();
            if (!bool2)
            {
              bool1 = true;
              paramAnonymousAdapterView.putBoolean("theme_set_gmcolor", bool1);
              paramAnonymousAdapterView.commit();
              if ((paramAnonymousView instanceof TextCheckCell))
              {
                paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                if (bool2) {
                  break label1155;
                }
              }
            }
            for (bool1 = true;; bool1 = false)
            {
              paramAnonymousAdapterView.setChecked(bool1);
              if (ThemingChatActivity.this.listView == null) {
                break;
              }
              ThemingChatActivity.this.listView.invalidateViews();
              return;
              bool1 = false;
              break label1083;
            }
          }
          if (paramAnonymousInt == ThemingChatActivity.this.groupMemberColorRow)
          {
            ThemingChatActivity.this.selectMessageColor(paramAnonymousView, paramContext, "theme_member_color");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.actionAvatarRadiusRow)
          {
            paramAnonymousAdapterView = new AlertDialog.Builder(ThemingChatActivity.this.getParentActivity());
            paramAnonymousAdapterView.setTitle(LocaleController.getString("AppName", 2131165338));
            paramAnonymousView = new NumberPicker(ThemingChatActivity.this.getParentActivity());
            paramAnonymousView.setMinValue(1);
            paramAnonymousView.setMaxValue(21);
            paramAnonymousView.setValue(ThemingChatActivity.this.getChatActionAvatarRadius(paramContext));
            paramAnonymousAdapterView.setView(paramAnonymousView);
            paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Done", 2131165634), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = ThemingChatActivity.2.this.val$preferences.edit();
                paramAnonymous2DialogInterface.putInt("theme_chat_action_aradius", paramAnonymousView.getValue());
                paramAnonymous2DialogInterface.commit();
                if (ThemingChatActivity.this.listView != null) {
                  ThemingChatActivity.this.listView.invalidateViews();
                }
              }
            });
            ThemingChatActivity.this.showDialog(paramAnonymousAdapterView.create());
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.groupAvatarRadiusRow)
          {
            paramAnonymousAdapterView = new AlertDialog.Builder(ThemingChatActivity.this.getParentActivity());
            paramAnonymousAdapterView.setTitle(LocaleController.getString("AppName", 2131165338));
            paramAnonymousView = new NumberPicker(ThemingChatActivity.this.getParentActivity());
            paramAnonymousView.setMinValue(1);
            paramAnonymousView.setMaxValue(21);
            paramAnonymousView.setValue(ThemingChatActivity.this.getChatGroupAvatarRadius(paramContext));
            paramAnonymousAdapterView.setView(paramAnonymousView);
            paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Done", 2131165634), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = ThemingChatActivity.2.this.val$preferences.edit();
                paramAnonymous2DialogInterface.putInt("theme_chat_group_aradius", paramAnonymousView.getValue());
                paramAnonymous2DialogInterface.commit();
                if (ThemingChatActivity.this.listView != null) {
                  ThemingChatActivity.this.listView.invalidateViews();
                }
              }
            });
            ThemingChatActivity.this.showDialog(paramAnonymousAdapterView.create());
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.dateBgColorRow)
          {
            ThemingChatActivity.this.selectMessageColor(paramAnonymousView, paramContext, "theme_date_bgcolor");
            return;
          }
          if (paramAnonymousInt == ThemingChatActivity.this.dateTextColorRow)
          {
            ThemingChatActivity.this.selectMessageColor(paramAnonymousView, paramContext, "theme_date_tcolor");
            return;
          }
        } while (paramAnonymousInt != ThemingChatActivity.this.selectedMessageBGColorRow);
        ThemingChatActivity.this.selectMessageColor(paramAnonymousView, paramContext, "theme_smsg_bgcolor");
      }
    });
    this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
    {
      public boolean onItemLongClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        if (ThemingChatActivity.this.getParentActivity() == null) {
          return false;
        }
        if (paramAnonymousInt == ThemingChatActivity.this.actionColorRow) {
          MihanTheme.resetPreference("theme_chat_action_color", ThemingChatActivity.this.listView);
        }
        for (;;)
        {
          return true;
          if (paramAnonymousInt == ThemingChatActivity.this.actionGradientRow) {
            MihanTheme.resetPreference("theme_chat_action_gradient", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.actionGradientColorRow) {
            MihanTheme.resetPreference("theme_chat_action_gcolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.actionIconColorRow) {
            MihanTheme.resetPreference("theme_chat_action_icolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.actionTitleColorRow) {
            MihanTheme.resetPreference("theme_chat_action_tcolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.actionSubTitleColorRow) {
            MihanTheme.resetPreference("theme_chat_action_stcolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.actionAvatarRadiusRow) {
            MihanTheme.resetPreference("theme_chat_action_aradius", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.actionAMIconColorRow) {
            MihanTheme.resetPreference("theme_chat_action_amicolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.editorColorRow) {
            MihanTheme.resetPreference("theme_chat_editor_color", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.editorGradientRow) {
            MihanTheme.resetPreference("theme_chat_editor_gradient", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.editorGradientColorRow) {
            MihanTheme.resetPreference("theme_chat_editor_gcolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.editorTextColorRow) {
            MihanTheme.resetPreference("theme_chat_editor_tcolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.editorIconColorRow) {
            MihanTheme.resetPreference("theme_chat_editor_icolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.editorSendIconColorRow) {
            MihanTheme.resetPreference("theme_chat_editor_sicolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.emojiColorRow) {
            MihanTheme.resetPreference("theme_emoji_color", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.emojiGradientRow) {
            MihanTheme.resetPreference("theme_emoji_gradient", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.emojiGradientColorRow) {
            MihanTheme.resetPreference("theme_emoji_gcolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.emojiTabColorRow) {
            MihanTheme.resetPreference("theme_emoji_tab_color", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.emojiTabGradientRow) {
            MihanTheme.resetPreference("theme_emoji_tab_gradient", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.emojiTabGradientColorRow) {
            MihanTheme.resetPreference("theme_emoji_tab_gcolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.emojiTabIconColorRow) {
            MihanTheme.resetPreference("theme_emoji_tab_icolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.emojiTabUnderlineColorRow) {
            MihanTheme.resetPreference("theme_emoji_tab_ucolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.emojiSelectedTabColorRow) {
            MihanTheme.resetPreference("theme_emoji_tab_scolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.setBGColorRow) {
            MihanTheme.resetPreference("theme_set_chat_bgcolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.BGColorRow) {
            MihanTheme.resetPreference("theme_chat_bg_color", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.BGGradientRow) {
            MihanTheme.resetPreference("theme_chat_bg_gradient", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.BGGradientColorRow) {
            MihanTheme.resetPreference("theme_chat_bg_gcolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.rightBubbleColorRow) {
            MihanTheme.resetPreference("theme_rbubble_color", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.leftBubbleColorRow) {
            MihanTheme.resetPreference("theme_lbubble_color", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.rightTextColorRow) {
            MihanTheme.resetPreference("theme_rtext_color", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.leftTextColorRow) {
            MihanTheme.resetPreference("theme_ltext_color", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.rightTimeColorRow) {
            MihanTheme.resetPreference("theme_rtime_color", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.leftTimeColorRow) {
            MihanTheme.resetPreference("theme_ltime_color", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.rightLinkColorRow) {
            MihanTheme.resetPreference("theme_rlink_color", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.leftLinkColorRow) {
            MihanTheme.resetPreference("theme_llink_color", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.rightForwardedNameColorRow) {
            MihanTheme.resetPreference("theme_rfname_color", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.leftForwardedNameColorRow) {
            MihanTheme.resetPreference("theme_lfname_color", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.setGroupMemberColorRow) {
            MihanTheme.resetPreference("theme_set_gmcolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.groupMemberColorRow) {
            MihanTheme.resetPreference("theme_member_color", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.groupAvatarRadiusRow) {
            MihanTheme.resetPreference("theme_chat_group_aradius", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.dateBgColorRow) {
            MihanTheme.resetPreference("theme_date_bgcolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.dateTextColorRow) {
            MihanTheme.resetPreference("theme_date_tcolor", ThemingChatActivity.this.listView);
          } else if (paramAnonymousInt == ThemingChatActivity.this.selectedMessageBGColorRow) {
            MihanTheme.resetPreference("theme_smsg_bgcolor", ThemingChatActivity.this.listView);
          }
        }
      }
    });
    return this.fragmentView;
  }
  
  public int getBGColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_chat_bg_color", -1);
  }
  
  public int getBGGradientColor(SharedPreferences paramSharedPreferences)
  {
    if (getBGGradientFlag(paramSharedPreferences) == 0) {
      return 0;
    }
    return paramSharedPreferences.getInt("theme_chat_bg_gcolor", getBGColor(paramSharedPreferences));
  }
  
  public int getBGGradientFlag(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_chat_bg_gradient", 0);
  }
  
  public int getChatAModeIconColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_chat_action_amicolor", -9276814);
  }
  
  public int getChatActionAvatarRadius(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_chat_action_aradius", 21);
  }
  
  public int getChatActionBarColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_chat_action_color", MihanTheme.getActionBarColor(paramSharedPreferences));
  }
  
  public int getChatActionBarGradientFlag(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_chat_action_gradient", MihanTheme.getActionBarGradientFlag(paramSharedPreferences));
  }
  
  public int getChatActionBarGradientcolor(SharedPreferences paramSharedPreferences)
  {
    if (getChatActionBarGradientFlag(paramSharedPreferences) == 0) {
      return 0;
    }
    return paramSharedPreferences.getInt("theme_chat_action_gcolor", MihanTheme.getActionBarGradientColor(paramSharedPreferences));
  }
  
  public int getChatActionBarIconColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_chat_action_icolor", MihanTheme.getActionBarIconColor(paramSharedPreferences));
  }
  
  public int getChatActionBarSubTitleColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_chat_action_stcolor", MihanTheme.getLighterColor(getChatActionBarTitleColor(paramSharedPreferences), 0.8F));
  }
  
  public int getChatActionBarTitleColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_chat_action_tcolor", getChatActionBarIconColor(paramSharedPreferences));
  }
  
  public int getChatEditorColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_chat_editor_color", -1);
  }
  
  public int getChatEditorGradientColor(SharedPreferences paramSharedPreferences)
  {
    if (getChatEditorGradientFlag(paramSharedPreferences) == 0) {
      return 0;
    }
    return paramSharedPreferences.getInt("theme_chat_editor_gcolor", getChatEditorColor(paramSharedPreferences));
  }
  
  public int getChatEditorGradientFlag(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_chat_editor_gradient", 0);
  }
  
  public int getChatEditorIconColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_chat_editor_icolor", -5066062);
  }
  
  public int getChatEditorSendIconColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_chat_editor_sicolor", -10243861);
  }
  
  public int getChatEditorTextColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_chat_editor_tcolor", -16777216);
  }
  
  public int getChatGroupAvatarRadius(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_chat_group_aradius", 21);
  }
  
  public int getDateBGColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_date_bgcolor", ApplicationLoader.getServiceMessageColor());
  }
  
  public int getDateTextColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_date_tcolor", -1);
  }
  
  public int getEmojiColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_emoji_color", -657673);
  }
  
  public int getEmojiGradientColor(SharedPreferences paramSharedPreferences)
  {
    if (getEmojiGradientFlag(paramSharedPreferences) == 0) {
      return 0;
    }
    return paramSharedPreferences.getInt("theme_emoji_gcolor", getEmojiColor(paramSharedPreferences));
  }
  
  public int getEmojiGradientFlag(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_emoji_gradient", 0);
  }
  
  public int getEmojiSelectedTabColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_emoji_tab_scolor", -13920542);
  }
  
  public int getEmojiTabColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_emoji_tab_color", -657673);
  }
  
  public int getEmojiTabGradientColor(SharedPreferences paramSharedPreferences)
  {
    if (getEmojiTabGradientFlag(paramSharedPreferences) == 0) {
      return 0;
    }
    return paramSharedPreferences.getInt("theme_emoji_tab_gcolor", getEmojiTabColor(paramSharedPreferences));
  }
  
  public int getEmojiTabGradientFlag(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_emoji_tab_gradient", 0);
  }
  
  public int getEmojiTabIconColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_emoji_tab_icolor", -5723992);
  }
  
  public int getEmojiTabUnderlineColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_emoji_tab_ucolor", -1907225);
  }
  
  public int getGroupMemberColor(SharedPreferences paramSharedPreferences)
  {
    int i = 0;
    if (paramSharedPreferences.getBoolean("theme_set_gmcolor", false)) {
      i = paramSharedPreferences.getInt("theme_member_color", MihanTheme.getThemeColor());
    }
    return i;
  }
  
  public int getLeftBubbleColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_lbubble_color", -1);
  }
  
  public int getLeftFNameColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_lfname_color", -12940081);
  }
  
  public int getLeftLinkColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_llink_color", -14255946);
  }
  
  public int getLeftTextColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_ltext_color", -16777216);
  }
  
  public int getLeftTimeColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_ltime_color", -6182221);
  }
  
  public int getRightBubbleColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_rbubble_color", -1048610);
  }
  
  public int getRightFNameColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_rfname_color", -11162801);
  }
  
  public int getRightLinkColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_rlink_color", -14255946);
  }
  
  public int getRightTextColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_rtext_color", -16777216);
  }
  
  public int getRightTimeColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_rtime_color", -9391780);
  }
  
  public int getSMessagesBGColor(SharedPreferences paramSharedPreferences)
  {
    return paramSharedPreferences.getInt("theme_smsg_bgcolor", 1714664933);
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.actionSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.actionColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.actionGradientRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.actionGradientColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.actionIconColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.actionTitleColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.actionSubTitleColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.actionAvatarRadiusRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.actionAMIconColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.editorSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.editorSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.editorColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.editorGradientRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.editorGradientColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.editorTextColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.editorIconColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.editorSendIconColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.emojiSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.emojiSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.emojiTabColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.emojiTabGradientRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.emojiTabGradientColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.emojiTabIconColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.emojiSelectedTabColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.emojiTabUnderlineColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.emojiColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.emojiGradientRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.emojiGradientColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messageSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messageSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.setBGColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.BGColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.BGGradientRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.BGGradientColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.rightBubbleColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.leftBubbleColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.rightTextColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.leftTextColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.rightLinkColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.leftLinkColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.rightTimeColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.leftTimeColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.rightForwardedNameColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.leftForwardedNameColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.setGroupMemberColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.groupMemberColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.groupAvatarRadiusRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.dateBgColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.dateTextColorRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.selectedMessageBGColorRow = i;
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
  }
  
  private class ListAdapter
    extends BaseFragmentAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public boolean areAllItemsEnabled()
    {
      return true;
    }
    
    public int getCount()
    {
      return ThemingChatActivity.this.rowCount;
    }
    
    public Object getItem(int paramInt)
    {
      return null;
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((paramInt == ThemingChatActivity.this.editorSectionRow) || (paramInt == ThemingChatActivity.this.emojiSectionRow) || (paramInt == ThemingChatActivity.this.messageSectionRow)) {}
      do
      {
        return 0;
        if ((paramInt == ThemingChatActivity.this.actionSectionRow2) || (paramInt == ThemingChatActivity.this.editorSectionRow2) || (paramInt == ThemingChatActivity.this.emojiSectionRow2) || (paramInt == ThemingChatActivity.this.messageSectionRow2)) {
          return 1;
        }
        if ((paramInt == ThemingChatActivity.this.actionColorRow) || (paramInt == ThemingChatActivity.this.actionGradientColorRow) || (paramInt == ThemingChatActivity.this.actionIconColorRow) || (paramInt == ThemingChatActivity.this.actionTitleColorRow) || (paramInt == ThemingChatActivity.this.actionSubTitleColorRow) || (paramInt == ThemingChatActivity.this.editorColorRow) || (paramInt == ThemingChatActivity.this.editorGradientColorRow) || (paramInt == ThemingChatActivity.this.editorTextColorRow) || (paramInt == ThemingChatActivity.this.editorIconColorRow) || (paramInt == ThemingChatActivity.this.editorSendIconColorRow) || (paramInt == ThemingChatActivity.this.emojiColorRow) || (paramInt == ThemingChatActivity.this.emojiGradientColorRow) || (paramInt == ThemingChatActivity.this.emojiTabUnderlineColorRow) || (paramInt == ThemingChatActivity.this.emojiSelectedTabColorRow) || (paramInt == ThemingChatActivity.this.emojiTabColorRow) || (paramInt == ThemingChatActivity.this.emojiTabGradientColorRow) || (paramInt == ThemingChatActivity.this.emojiTabIconColorRow) || (paramInt == ThemingChatActivity.this.rightBubbleColorRow) || (paramInt == ThemingChatActivity.this.leftBubbleColorRow) || (paramInt == ThemingChatActivity.this.rightTextColorRow) || (paramInt == ThemingChatActivity.this.leftTextColorRow) || (paramInt == ThemingChatActivity.this.rightTimeColorRow) || (paramInt == ThemingChatActivity.this.leftTimeColorRow) || (paramInt == ThemingChatActivity.this.leftLinkColorRow) || (paramInt == ThemingChatActivity.this.rightLinkColorRow) || (paramInt == ThemingChatActivity.this.rightForwardedNameColorRow) || (paramInt == ThemingChatActivity.this.leftForwardedNameColorRow) || (paramInt == ThemingChatActivity.this.groupMemberColorRow) || (paramInt == ThemingChatActivity.this.BGColorRow) || (paramInt == ThemingChatActivity.this.BGGradientColorRow) || (paramInt == ThemingChatActivity.this.actionAMIconColorRow) || (paramInt == ThemingChatActivity.this.dateBgColorRow) || (paramInt == ThemingChatActivity.this.dateTextColorRow) || (paramInt == ThemingChatActivity.this.selectedMessageBGColorRow)) {
          return 2;
        }
        if ((paramInt == ThemingChatActivity.this.actionGradientRow) || (paramInt == ThemingChatActivity.this.actionAvatarRadiusRow) || (paramInt == ThemingChatActivity.this.editorGradientRow) || (paramInt == ThemingChatActivity.this.emojiGradientRow) || (paramInt == ThemingChatActivity.this.emojiTabGradientRow) || (paramInt == ThemingChatActivity.this.groupAvatarRadiusRow) || (paramInt == ThemingChatActivity.this.BGGradientRow)) {
          return 3;
        }
      } while ((paramInt != ThemingChatActivity.this.setGroupMemberColorRow) && (paramInt != ThemingChatActivity.this.setBGColorRow));
      return 4;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      int i = getItemViewType(paramInt);
      if (i == 0)
      {
        paramViewGroup = paramView;
        if (paramView == null) {
          paramViewGroup = new ShadowSectionCell(this.mContext);
        }
      }
      Object localObject;
      SharedPreferences localSharedPreferences;
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                return paramViewGroup;
                if (i != 1) {
                  break;
                }
                localObject = paramView;
                if (paramView == null) {
                  localObject = new HeaderCell(this.mContext);
                }
                if (paramInt == ThemingChatActivity.this.actionSectionRow2)
                {
                  ((HeaderCell)localObject).setText(LocaleController.getString("ThemingHeader", 2131166741));
                  return (View)localObject;
                }
                if (paramInt == ThemingChatActivity.this.editorSectionRow2)
                {
                  ((HeaderCell)localObject).setText(LocaleController.getString("ThemingChatEditor", 2131166690));
                  return (View)localObject;
                }
                if (paramInt == ThemingChatActivity.this.emojiSectionRow2)
                {
                  ((HeaderCell)localObject).setText(LocaleController.getString("ThemingEmojiPanel", 2131166720));
                  return (View)localObject;
                }
                paramViewGroup = (ViewGroup)localObject;
              } while (paramInt != ThemingChatActivity.this.messageSectionRow2);
              ((HeaderCell)localObject).setText(LocaleController.getString("ThemingMessage", 2131166754));
              return (View)localObject;
              if (i != 2) {
                break;
              }
              localObject = paramView;
              if (paramView == null) {
                localObject = new TextColorCell(this.mContext);
              }
              paramView = (TextColorCell)localObject;
              localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
              if (paramInt == ThemingChatActivity.this.actionColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingColor", 2131166694), ThemingChatActivity.this.getChatActionBarColor(localSharedPreferences), false);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.actionGradientColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingGradientColor", 2131166737), ThemingChatActivity.this.getChatActionBarGradientcolor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.actionIconColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingActionIconColor", 2131166683), ThemingChatActivity.this.getChatActionBarIconColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.actionTitleColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingTitleColor", 2131166805), ThemingChatActivity.this.getChatActionBarTitleColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.actionSubTitleColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingSubTitleColor", 2131166785), ThemingChatActivity.this.getChatActionBarSubTitleColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.actionAMIconColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingAMIconColor", 2131166682), ThemingChatActivity.this.getChatAModeIconColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.editorColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingColor", 2131166694), ThemingChatActivity.this.getChatEditorColor(localSharedPreferences), false);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.editorGradientColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingGradientColor", 2131166737), ThemingChatActivity.this.getChatEditorGradientColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.editorTextColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingChatEditorTextColor", 2131166692), ThemingChatActivity.this.getChatEditorTextColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.editorIconColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingActionIconColor", 2131166683), ThemingChatActivity.this.getChatEditorIconColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.editorSendIconColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingChatEditorSIconColor", 2131166691), ThemingChatActivity.this.getChatEditorSendIconColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.emojiColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingEmojiPanelColor", 2131166721), ThemingChatActivity.this.getEmojiColor(localSharedPreferences), false);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.emojiGradientColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingEmojiPanelGColor", 2131166722), ThemingChatActivity.this.getEmojiGradientColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.emojiTabColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingEmojiTabsColor", 2131166726), ThemingChatActivity.this.getEmojiTabColor(localSharedPreferences), false);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.emojiTabGradientColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingEmojiTabsGColor", 2131166727), ThemingChatActivity.this.getEmojiTabGradientColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.emojiTabIconColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingTabsIconColor", 2131166793), ThemingChatActivity.this.getEmojiTabIconColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.emojiSelectedTabColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingEmojiSelectedTabColor", 2131166724), ThemingChatActivity.this.getEmojiSelectedTabColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.emojiTabUnderlineColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingEmojiTabUnderlineColor", 2131166725), ThemingChatActivity.this.getEmojiTabUnderlineColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.BGColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingBGColor", 2131166685), ThemingChatActivity.this.getBGColor(localSharedPreferences), false);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.BGGradientColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingGradientColor", 2131166737), ThemingChatActivity.this.getBGGradientColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.rightBubbleColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingRightBubbleColor", 2131166761), ThemingChatActivity.this.getRightBubbleColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.leftBubbleColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingLeftBubbleColor", 2131166745), ThemingChatActivity.this.getLeftBubbleColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.rightTextColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingRightTextColor", 2131166764), ThemingChatActivity.this.getRightTextColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.leftTextColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingLeftTextColor", 2131166748), ThemingChatActivity.this.getLeftTextColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.rightTimeColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingRightTimeColor", 2131166765), ThemingChatActivity.this.getRightTimeColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.leftTimeColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingLeftTimeColor", 2131166749), ThemingChatActivity.this.getLeftTimeColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.rightLinkColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingRightLinkColor", 2131166763), ThemingChatActivity.this.getRightLinkColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.leftLinkColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingLeftLinkColor", 2131166747), ThemingChatActivity.this.getLeftLinkColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.rightForwardedNameColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingRightFNameColor", 2131166762), ThemingChatActivity.this.getRightFNameColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.leftForwardedNameColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingLeftFNameColor", 2131166746), ThemingChatActivity.this.getLeftFNameColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.groupMemberColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingGroupMemberColor", 2131166740), ThemingChatActivity.this.getGroupMemberColor(localSharedPreferences), true);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.dateBgColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingDateBGColor", 2131166704), ThemingChatActivity.this.getDateBGColor(localSharedPreferences), false);
                return (View)localObject;
              }
              if (paramInt == ThemingChatActivity.this.dateTextColorRow)
              {
                paramView.setTextAndColor(LocaleController.getString("ThemingDateTextColor", 2131166706), ThemingChatActivity.this.getDateTextColor(localSharedPreferences), true);
                return (View)localObject;
              }
              paramViewGroup = (ViewGroup)localObject;
            } while (paramInt != ThemingChatActivity.this.selectedMessageBGColorRow);
            paramView.setTextAndColor(LocaleController.getString("ThemingSMessegesBGColor", 2131166767), ThemingChatActivity.this.getSMessagesBGColor(localSharedPreferences), false);
            return (View)localObject;
            if (i != 3) {
              break;
            }
            localObject = paramView;
            if (paramView == null) {
              localObject = new TextSettingsCell(this.mContext);
            }
            paramView = (TextSettingsCell)localObject;
            localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
            if (paramInt == ThemingChatActivity.this.actionGradientRow)
            {
              paramView.setTextAndValue(LocaleController.getString("ThemingGradient", 2131166736), MihanTheme.getGradientString(localSharedPreferences, "theme_chat_action_gradient"), false);
              return (View)localObject;
            }
            if (paramInt == ThemingChatActivity.this.actionAvatarRadiusRow)
            {
              paramView.setTextAndValue(LocaleController.getString("ThemingAvatarRadius", 2131166684), String.valueOf(ThemingChatActivity.this.getChatActionAvatarRadius(localSharedPreferences)), true);
              return (View)localObject;
            }
            if (paramInt == ThemingChatActivity.this.editorGradientRow)
            {
              paramView.setTextAndValue(LocaleController.getString("ThemingGradient", 2131166736), MihanTheme.getGradientString(localSharedPreferences, "theme_chat_editor_gradient"), false);
              return (View)localObject;
            }
            if (paramInt == ThemingChatActivity.this.emojiGradientRow)
            {
              paramView.setTextAndValue(LocaleController.getString("ThemingEmojiPanelGradient", 2131166723), MihanTheme.getGradientString(localSharedPreferences, "theme_emoji_gradient"), false);
              return (View)localObject;
            }
            if (paramInt == ThemingChatActivity.this.emojiTabGradientRow)
            {
              paramView.setTextAndValue(LocaleController.getString("ThemingEmojiTabsGradient", 2131166728), MihanTheme.getGradientString(localSharedPreferences, "theme_emoji_tab_gradient"), false);
              return (View)localObject;
            }
            if (paramInt == ThemingChatActivity.this.groupAvatarRadiusRow)
            {
              paramView.setTextAndValue(LocaleController.getString("ThemingGroupMemAvatarRadius", 2131166739), String.valueOf(ThemingChatActivity.this.getChatGroupAvatarRadius(localSharedPreferences)), true);
              return (View)localObject;
            }
            paramViewGroup = (ViewGroup)localObject;
          } while (paramInt != ThemingChatActivity.this.BGGradientRow);
          paramView.setTextAndValue(LocaleController.getString("ThemingGradient", 2131166736), MihanTheme.getGradientString(localSharedPreferences, "theme_chat_bg_gradient"), false);
          return (View)localObject;
          paramViewGroup = paramView;
        } while (i != 4);
        localObject = paramView;
        if (paramView == null) {
          localObject = new TextCheckCell(this.mContext);
        }
        paramView = (TextCheckCell)localObject;
        localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
        if (paramInt == ThemingChatActivity.this.setGroupMemberColorRow)
        {
          paramView.setTextAndCheck(LocaleController.getString("ThemingSetGroupMemberColor", 2131166774), localSharedPreferences.getBoolean("theme_set_gmcolor", false), false);
          return (View)localObject;
        }
        paramViewGroup = (ViewGroup)localObject;
      } while (paramInt != ThemingChatActivity.this.setBGColorRow);
      paramView.setTextAndCheck(LocaleController.getString("ThemingSetBGColor", 2131166773), localSharedPreferences.getBoolean("theme_set_chat_bgcolor", false), true);
      return (View)localObject;
    }
    
    public int getViewTypeCount()
    {
      return 5;
    }
    
    public boolean hasStableIds()
    {
      return false;
    }
    
    public boolean isEmpty()
    {
      return false;
    }
    
    public boolean isEnabled(int paramInt)
    {
      SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
      if ((paramInt == ThemingChatActivity.this.actionGradientColorRow) && (ThemingChatActivity.this.getChatActionBarGradientFlag(localSharedPreferences) == 0)) {}
      while (((paramInt == ThemingChatActivity.this.editorGradientColorRow) && (ThemingChatActivity.this.getChatEditorGradientFlag(localSharedPreferences) == 0)) || ((paramInt == ThemingChatActivity.this.emojiGradientColorRow) && (ThemingChatActivity.this.getEmojiGradientFlag(localSharedPreferences) == 0)) || ((paramInt == ThemingChatActivity.this.emojiTabGradientColorRow) && (ThemingChatActivity.this.getEmojiTabGradientFlag(localSharedPreferences) == 0)) || (((paramInt == ThemingChatActivity.this.BGColorRow) || (paramInt == ThemingChatActivity.this.BGGradientRow)) && ((!localSharedPreferences.getBoolean("theme_set_chat_bgcolor", false)) || ((paramInt == ThemingChatActivity.this.BGGradientColorRow) && (ThemingChatActivity.this.getBGGradientFlag(localSharedPreferences) == 0)) || ((paramInt == ThemingChatActivity.this.groupMemberColorRow) && (!localSharedPreferences.getBoolean("theme_set_gmcolor", false))) || ((paramInt != ThemingChatActivity.this.actionColorRow) && (paramInt != ThemingChatActivity.this.actionGradientRow) && (paramInt != ThemingChatActivity.this.actionGradientColorRow) && (paramInt != ThemingChatActivity.this.actionIconColorRow) && (paramInt != ThemingChatActivity.this.actionTitleColorRow) && (paramInt != ThemingChatActivity.this.actionIconColorRow) && (paramInt != ThemingChatActivity.this.actionTitleColorRow) && (paramInt != ThemingChatActivity.this.actionAvatarRadiusRow) && (paramInt != ThemingChatActivity.this.actionSubTitleColorRow) && (paramInt != ThemingChatActivity.this.editorColorRow) && (paramInt != ThemingChatActivity.this.editorGradientRow) && (paramInt != ThemingChatActivity.this.editorGradientColorRow) && (paramInt != ThemingChatActivity.this.editorTextColorRow) && (paramInt != ThemingChatActivity.this.editorIconColorRow) && (paramInt != ThemingChatActivity.this.editorSendIconColorRow) && (paramInt != ThemingChatActivity.this.emojiColorRow) && (paramInt != ThemingChatActivity.this.emojiGradientRow) && (paramInt != ThemingChatActivity.this.emojiGradientColorRow) && (paramInt != ThemingChatActivity.this.emojiTabUnderlineColorRow) && (paramInt != ThemingChatActivity.this.emojiSelectedTabColorRow) && (paramInt != ThemingChatActivity.this.emojiTabColorRow) && (paramInt != ThemingChatActivity.this.emojiTabGradientRow) && (paramInt != ThemingChatActivity.this.emojiTabGradientColorRow) && (paramInt != ThemingChatActivity.this.emojiTabIconColorRow) && (paramInt != ThemingChatActivity.this.rightBubbleColorRow) && (paramInt != ThemingChatActivity.this.leftBubbleColorRow) && (paramInt != ThemingChatActivity.this.rightTextColorRow) && (paramInt != ThemingChatActivity.this.leftTextColorRow) && (paramInt != ThemingChatActivity.this.rightTimeColorRow) && (paramInt != ThemingChatActivity.this.leftTimeColorRow) && (paramInt != ThemingChatActivity.this.rightLinkColorRow) && (paramInt != ThemingChatActivity.this.leftLinkColorRow) && (paramInt != ThemingChatActivity.this.rightForwardedNameColorRow) && (paramInt != ThemingChatActivity.this.leftForwardedNameColorRow) && (paramInt != ThemingChatActivity.this.setGroupMemberColorRow) && (paramInt != ThemingChatActivity.this.groupMemberColorRow) && (paramInt != ThemingChatActivity.this.groupAvatarRadiusRow) && (paramInt != ThemingChatActivity.this.BGColorRow) && (paramInt != ThemingChatActivity.this.BGGradientRow) && (paramInt != ThemingChatActivity.this.BGGradientColorRow) && (paramInt != ThemingChatActivity.this.setBGColorRow) && (paramInt != ThemingChatActivity.this.actionAMIconColorRow) && (paramInt != ThemingChatActivity.this.dateBgColorRow) && (paramInt != ThemingChatActivity.this.dateTextColorRow) && (paramInt != ThemingChatActivity.this.selectedMessageBGColorRow))))) {
        return false;
      }
      return true;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\Theming\ThemingChatActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */