package org.telegram.messenger;

import android.graphics.Point;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.util.Linkify;
import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle;
import org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
import org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import org.telegram.tgnet.TLRPC.TL_messageActionCreatedBroadcastList;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageActionTTLChange;
import org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
import org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC.TL_messageEmpty;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageForwarded_old2;
import org.telegram.tgnet.TLRPC.TL_messageFwdHeader;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanNoUnderlineBold;
import org.telegram.ui.Mihangram.Theming.MihanTheme;

public class MessageObject
{
  private static final int LINES_PER_BLOCK = 10;
  public static final int MESSAGE_SEND_STATE_SENDING = 1;
  public static final int MESSAGE_SEND_STATE_SEND_ERROR = 2;
  public static final int MESSAGE_SEND_STATE_SENT = 0;
  private static TextPaint botButtonPaint;
  private static TextPaint textPaint;
  public static Pattern urlPattern;
  public boolean attachPathExists;
  public float audioProgress;
  public int audioProgressSec;
  public CharSequence caption;
  public int contentType;
  public String dateKey;
  public boolean deleted;
  public boolean forceUpdate;
  public int lastLineWidth;
  private boolean layoutCreated;
  public CharSequence linkDescription;
  public boolean mediaExists;
  public TLRPC.Message messageOwner;
  public CharSequence messageText;
  public String monthKey;
  public ArrayList<TLRPC.PhotoSize> photoThumbs;
  public MessageObject replyMessageObject;
  public int textHeight;
  public ArrayList<TextLayoutBlock> textLayoutBlocks;
  public int textWidth;
  public int type = 1000;
  public VideoEditedInfo videoEditedInfo;
  public boolean viewsReloaded;
  public int wantedBotKeyboardWidth;
  
  public MessageObject(TLRPC.Message paramMessage, AbstractMap<Integer, TLRPC.User> paramAbstractMap, AbstractMap<Integer, TLRPC.Chat> paramAbstractMap1, boolean paramBoolean)
  {
    if (textPaint == null)
    {
      textPaint = new TextPaint(1);
      textPaint.setColor(-16777216);
      textPaint.linkColor = -14255946;
    }
    textPaint.setTextSize(AndroidUtilities.dp(MessagesController.getInstance().fontSize));
    textPaint.setTypeface(MihanTheme.getMihanTypeFace());
    this.messageOwner = paramMessage;
    if (paramMessage.replyMessage != null) {
      this.replyMessageObject = new MessageObject(paramMessage.replyMessage, paramAbstractMap, paramAbstractMap1, false);
    }
    Object localObject = null;
    TLRPC.User localUser = null;
    if (paramMessage.from_id > 0)
    {
      if (paramAbstractMap != null) {
        localUser = (TLRPC.User)paramAbstractMap.get(Integer.valueOf(paramMessage.from_id));
      }
      localObject = localUser;
      if (localUser == null) {
        localObject = MessagesController.getInstance().getUser(Integer.valueOf(paramMessage.from_id));
      }
    }
    if ((paramMessage instanceof TLRPC.TL_messageService)) {
      if (paramMessage.action != null)
      {
        if (!(paramMessage.action instanceof TLRPC.TL_messageActionChatCreate)) {
          break label484;
        }
        if (!isOut()) {
          break label462;
        }
        this.messageText = LocaleController.getString("ActionYouCreateGroup", 2131165288);
      }
    }
    for (;;)
    {
      if (this.messageText == null) {
        this.messageText = "";
      }
      setType();
      measureInlineBotButtons();
      paramMessage = new GregorianCalendar();
      paramMessage.setTimeInMillis(this.messageOwner.date * 1000L);
      int i = paramMessage.get(6);
      int j = paramMessage.get(1);
      int k = paramMessage.get(2);
      this.dateKey = String.format("%d_%02d_%02d", new Object[] { Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(i) });
      this.monthKey = String.format("%d_%02d", new Object[] { Integer.valueOf(j), Integer.valueOf(k) });
      if ((this.messageOwner.message != null) && (this.messageOwner.id < 0) && (this.messageOwner.message.length() > 6) && (isVideo()))
      {
        this.videoEditedInfo = new VideoEditedInfo();
        if (!this.videoEditedInfo.parseString(this.messageOwner.message)) {
          this.videoEditedInfo = null;
        }
      }
      generateCaption();
      if (paramBoolean)
      {
        this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
        generateLayout((TLRPC.User)localObject);
      }
      this.layoutCreated = paramBoolean;
      generateThumbs(false);
      checkMediaExistance();
      return;
      label462:
      this.messageText = replaceWithLink(LocaleController.getString("ActionCreateGroup", 2131165262), "un1", (TLObject)localObject);
      continue;
      label484:
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChatDeleteUser))
      {
        if (paramMessage.action.user_id == paramMessage.from_id)
        {
          if (isOut()) {
            this.messageText = LocaleController.getString("ActionYouLeftUser", 2131165290);
          } else {
            this.messageText = replaceWithLink(LocaleController.getString("ActionLeftUser", 2131165268), "un1", (TLObject)localObject);
          }
        }
        else
        {
          paramAbstractMap1 = null;
          if (paramAbstractMap != null) {
            paramAbstractMap1 = (TLRPC.User)paramAbstractMap.get(Integer.valueOf(paramMessage.action.user_id));
          }
          paramAbstractMap = paramAbstractMap1;
          if (paramAbstractMap1 == null) {
            paramAbstractMap = MessagesController.getInstance().getUser(Integer.valueOf(paramMessage.action.user_id));
          }
          if (isOut())
          {
            this.messageText = replaceWithLink(LocaleController.getString("ActionYouKickUser", 2131165289), "un2", paramAbstractMap);
          }
          else if (paramMessage.action.user_id == UserConfig.getClientUserId())
          {
            this.messageText = replaceWithLink(LocaleController.getString("ActionKickUserYou", 2131165267), "un1", (TLObject)localObject);
          }
          else
          {
            this.messageText = replaceWithLink(LocaleController.getString("ActionKickUser", 2131165266), "un2", paramAbstractMap);
            this.messageText = replaceWithLink(this.messageText, "un1", (TLObject)localObject);
          }
        }
      }
      else if ((paramMessage.action instanceof TLRPC.TL_messageActionChatAddUser))
      {
        j = this.messageOwner.action.user_id;
        i = j;
        if (j == 0)
        {
          i = j;
          if (this.messageOwner.action.users.size() == 1) {
            i = ((Integer)this.messageOwner.action.users.get(0)).intValue();
          }
        }
        if (i != 0)
        {
          paramAbstractMap1 = null;
          if (paramAbstractMap != null) {
            paramAbstractMap1 = (TLRPC.User)paramAbstractMap.get(Integer.valueOf(i));
          }
          paramAbstractMap = paramAbstractMap1;
          if (paramAbstractMap1 == null) {
            paramAbstractMap = MessagesController.getInstance().getUser(Integer.valueOf(i));
          }
          if (i == paramMessage.from_id)
          {
            if ((paramMessage.to_id.channel_id != 0) && (!isMegagroup())) {
              this.messageText = LocaleController.getString("ChannelJoined", 2131165471);
            } else if ((paramMessage.to_id.channel_id != 0) && (isMegagroup()))
            {
              if (i == UserConfig.getClientUserId()) {
                this.messageText = LocaleController.getString("ChannelMegaJoined", 2131165475);
              } else {
                this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelfMega", 2131165253), "un1", (TLObject)localObject);
              }
            }
            else if (isOut()) {
              this.messageText = LocaleController.getString("ActionAddUserSelfYou", 2131165254);
            } else {
              this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelf", 2131165252), "un1", (TLObject)localObject);
            }
          }
          else if (isOut())
          {
            this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", 2131165285), "un2", paramAbstractMap);
          }
          else if (i == UserConfig.getClientUserId())
          {
            if (paramMessage.to_id.channel_id != 0)
            {
              if (isMegagroup()) {
                this.messageText = replaceWithLink(LocaleController.getString("MegaAddedBy", 2131165921), "un1", (TLObject)localObject);
              } else {
                this.messageText = replaceWithLink(LocaleController.getString("ChannelAddedBy", 2131165444), "un1", (TLObject)localObject);
              }
            }
            else {
              this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserYou", 2131165255), "un1", (TLObject)localObject);
            }
          }
          else
          {
            this.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", 2131165251), "un2", paramAbstractMap);
            this.messageText = replaceWithLink(this.messageText, "un1", (TLObject)localObject);
          }
        }
        else if (isOut())
        {
          this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", 2131165285), "un2", paramMessage.action.users, paramAbstractMap);
        }
        else
        {
          this.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", 2131165251), "un2", paramMessage.action.users, paramAbstractMap);
          this.messageText = replaceWithLink(this.messageText, "un1", (TLObject)localObject);
        }
      }
      else if ((paramMessage.action instanceof TLRPC.TL_messageActionChatJoinedByLink))
      {
        if (isOut()) {
          this.messageText = LocaleController.getString("ActionInviteYou", 2131165265);
        } else {
          this.messageText = replaceWithLink(LocaleController.getString("ActionInviteUser", 2131165264), "un1", (TLObject)localObject);
        }
      }
      else if ((paramMessage.action instanceof TLRPC.TL_messageActionChatEditPhoto))
      {
        if ((paramMessage.to_id.channel_id != 0) && (!isMegagroup())) {
          this.messageText = LocaleController.getString("ActionChannelChangedPhoto", 2131165258);
        } else if (isOut()) {
          this.messageText = LocaleController.getString("ActionYouChangedPhoto", 2131165286);
        } else {
          this.messageText = replaceWithLink(LocaleController.getString("ActionChangedPhoto", 2131165256), "un1", (TLObject)localObject);
        }
      }
      else if ((paramMessage.action instanceof TLRPC.TL_messageActionChatEditTitle))
      {
        if ((paramMessage.to_id.channel_id != 0) && (!isMegagroup())) {
          this.messageText = LocaleController.getString("ActionChannelChangedTitle", 2131165259).replace("un2", paramMessage.action.title);
        } else if (isOut()) {
          this.messageText = LocaleController.getString("ActionYouChangedTitle", 2131165287).replace("un2", paramMessage.action.title);
        } else {
          this.messageText = replaceWithLink(LocaleController.getString("ActionChangedTitle", 2131165257).replace("un2", paramMessage.action.title), "un1", (TLObject)localObject);
        }
      }
      else if ((paramMessage.action instanceof TLRPC.TL_messageActionChatDeletePhoto))
      {
        if ((paramMessage.to_id.channel_id != 0) && (!isMegagroup())) {
          this.messageText = LocaleController.getString("ActionChannelRemovedPhoto", 2131165260);
        } else if (isOut()) {
          this.messageText = LocaleController.getString("ActionYouRemovedPhoto", 2131165291);
        } else {
          this.messageText = replaceWithLink(LocaleController.getString("ActionRemovedPhoto", 2131165282), "un1", (TLObject)localObject);
        }
      }
      else if ((paramMessage.action instanceof TLRPC.TL_messageActionTTLChange))
      {
        if (paramMessage.action.ttl != 0)
        {
          if (isOut()) {
            this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", 2131165944, new Object[] { AndroidUtilities.formatTTLString(paramMessage.action.ttl) });
          } else {
            this.messageText = LocaleController.formatString("MessageLifetimeChanged", 2131165943, new Object[] { UserObject.getFirstName((TLRPC.User)localObject), AndroidUtilities.formatTTLString(paramMessage.action.ttl) });
          }
        }
        else if (isOut()) {
          this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", 2131165946);
        } else {
          this.messageText = LocaleController.formatString("MessageLifetimeRemoved", 2131165945, new Object[] { UserObject.getFirstName((TLRPC.User)localObject) });
        }
      }
      else
      {
        if ((paramMessage.action instanceof TLRPC.TL_messageActionLoginUnknownLocation))
        {
          long l = paramMessage.date * 1000L;
          String str;
          if ((LocaleController.getInstance().formatterDay != null) && (LocaleController.getInstance().formatterYear != null))
          {
            str = LocaleController.formatString("formatDateAtTime", 2131166518, new Object[] { LocaleController.getInstance().formatterYear.format(l), LocaleController.getInstance().formatterDay.format(l) });
            label1833:
            localUser = UserConfig.getCurrentUser();
            paramAbstractMap1 = localUser;
            if (localUser == null)
            {
              if (paramAbstractMap != null) {
                localUser = (TLRPC.User)paramAbstractMap.get(Integer.valueOf(this.messageOwner.to_id.user_id));
              }
              paramAbstractMap1 = localUser;
              if (localUser == null) {
                paramAbstractMap1 = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.to_id.user_id));
              }
            }
            if (paramAbstractMap1 == null) {
              break label1985;
            }
          }
          label1985:
          for (paramAbstractMap = UserObject.getFirstName(paramAbstractMap1);; paramAbstractMap = "")
          {
            this.messageText = LocaleController.formatString("NotificationUnrecognizedDevice", 2131166096, new Object[] { paramAbstractMap, str, paramMessage.action.title, paramMessage.action.address });
            break;
            str = "" + paramMessage.date;
            break label1833;
          }
        }
        if ((paramMessage.action instanceof TLRPC.TL_messageActionUserJoined))
        {
          this.messageText = LocaleController.formatString("NotificationContactJoined", 2131166059, new Object[] { UserObject.getUserName((TLRPC.User)localObject) });
        }
        else if ((paramMessage.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto))
        {
          this.messageText = LocaleController.formatString("NotificationContactNewPhoto", 2131166060, new Object[] { UserObject.getUserName((TLRPC.User)localObject) });
        }
        else if ((paramMessage.action instanceof TLRPC.TL_messageEncryptedAction))
        {
          if ((paramMessage.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages))
          {
            if (isOut()) {
              this.messageText = LocaleController.formatString("ActionTakeScreenshootYou", 2131165284, new Object[0]);
            } else {
              this.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", 2131165283), "un1", (TLObject)localObject);
            }
          }
          else if ((paramMessage.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL))
          {
            paramMessage = (TLRPC.TL_decryptedMessageActionSetMessageTTL)paramMessage.action.encryptedAction;
            if (paramMessage.ttl_seconds != 0)
            {
              if (isOut()) {
                this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", 2131165944, new Object[] { AndroidUtilities.formatTTLString(paramMessage.ttl_seconds) });
              } else {
                this.messageText = LocaleController.formatString("MessageLifetimeChanged", 2131165943, new Object[] { UserObject.getFirstName((TLRPC.User)localObject), AndroidUtilities.formatTTLString(paramMessage.ttl_seconds) });
              }
            }
            else if (isOut()) {
              this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", 2131165946);
            } else {
              this.messageText = LocaleController.formatString("MessageLifetimeRemoved", 2131165945, new Object[] { UserObject.getFirstName((TLRPC.User)localObject) });
            }
          }
        }
        else if ((paramMessage.action instanceof TLRPC.TL_messageActionCreatedBroadcastList))
        {
          this.messageText = LocaleController.formatString("YouCreatedBroadcastList", 2131166492, new Object[0]);
        }
        else if ((paramMessage.action instanceof TLRPC.TL_messageActionChannelCreate))
        {
          if (isMegagroup()) {
            this.messageText = LocaleController.getString("ActionCreateMega", 2131165263);
          } else {
            this.messageText = LocaleController.getString("ActionCreateChannel", 2131165261);
          }
        }
        else if ((paramMessage.action instanceof TLRPC.TL_messageActionChatMigrateTo))
        {
          this.messageText = LocaleController.getString("ActionMigrateFromGroup", 2131165269);
        }
        else if ((paramMessage.action instanceof TLRPC.TL_messageActionChannelMigrateFrom))
        {
          this.messageText = LocaleController.getString("ActionMigrateFromGroup", 2131165269);
        }
        else
        {
          if ((paramMessage.action instanceof TLRPC.TL_messageActionPinMessage))
          {
            if (localObject == null) {}
            for (paramMessage = (TLRPC.Chat)paramAbstractMap1.get(Integer.valueOf(paramMessage.to_id.channel_id));; paramMessage = null)
            {
              generatePinMessageText((TLRPC.User)localObject, paramMessage);
              break;
            }
          }
          if ((paramMessage.action instanceof TLRPC.TL_messageActionHistoryClear))
          {
            this.messageText = LocaleController.getString("HistoryCleared", 2131166645);
            continue;
            if (!isMediaEmpty())
            {
              if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) {
                this.messageText = LocaleController.getString("AttachPhoto", 2131165375);
              } else if (isVideo()) {
                this.messageText = LocaleController.getString("AttachVideo", 2131165377);
              } else if (isVoice()) {
                this.messageText = LocaleController.getString("AttachAudio", 2131165368);
              } else if (((paramMessage.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessage.media instanceof TLRPC.TL_messageMediaVenue))) {
                this.messageText = LocaleController.getString("AttachLocation", 2131165373);
              } else if ((paramMessage.media instanceof TLRPC.TL_messageMediaContact)) {
                this.messageText = LocaleController.getString("AttachContact", 2131165370);
              } else if ((paramMessage.media instanceof TLRPC.TL_messageMediaUnsupported)) {
                this.messageText = LocaleController.getString("UnsupportedMedia", 2131166429);
              } else if ((paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) {
                if (isSticker())
                {
                  paramMessage = getStrickerChar();
                  if ((paramMessage != null) && (paramMessage.length() > 0)) {
                    this.messageText = String.format("%s %s", new Object[] { paramMessage, LocaleController.getString("AttachSticker", 2131165376) });
                  } else {
                    this.messageText = LocaleController.getString("AttachSticker", 2131165376);
                  }
                }
                else if (isMusic())
                {
                  this.messageText = LocaleController.getString("AttachMusic", 2131165374);
                }
                else if (isGif())
                {
                  this.messageText = LocaleController.getString("AttachGif", 2131165372);
                }
                else
                {
                  paramMessage = FileLoader.getDocumentFileName(paramMessage.media.document);
                  if ((paramMessage != null) && (paramMessage.length() > 0)) {
                    this.messageText = paramMessage;
                  } else {
                    this.messageText = LocaleController.getString("AttachDocument", 2131165371);
                  }
                }
              }
            }
            else {
              this.messageText = paramMessage.message;
            }
          }
        }
      }
    }
  }
  
  public MessageObject(TLRPC.Message paramMessage, AbstractMap<Integer, TLRPC.User> paramAbstractMap, boolean paramBoolean)
  {
    this(paramMessage, paramAbstractMap, null, paramBoolean);
  }
  
  public static void addLinks(CharSequence paramCharSequence)
  {
    addLinks(paramCharSequence, true);
  }
  
  public static void addLinks(CharSequence paramCharSequence, boolean paramBoolean)
  {
    if ((!(paramCharSequence instanceof Spannable)) || (!containsUrls(paramCharSequence)) || (paramCharSequence.length() < 200)) {}
    for (;;)
    {
      try
      {
        Linkify.addLinks((Spannable)paramCharSequence, 5);
        addUsernamesAndHashtags(paramCharSequence, paramBoolean);
        return;
      }
      catch (Exception localException1)
      {
        FileLog.e("tmessages", localException1);
        continue;
      }
      try
      {
        Linkify.addLinks((Spannable)paramCharSequence, 1);
      }
      catch (Exception localException2)
      {
        FileLog.e("tmessages", localException2);
      }
    }
  }
  
  private static void addUsernamesAndHashtags(CharSequence paramCharSequence, boolean paramBoolean)
  {
    for (;;)
    {
      int k;
      int i;
      try
      {
        if (urlPattern == null) {
          urlPattern = Pattern.compile("(^|\\s)/[a-zA-Z@\\d_]{1,255}|(^|\\s)@[a-zA-Z\\d_]{1,32}|(^|\\s)#[\\w\\.]+");
        }
        Matcher localMatcher = urlPattern.matcher(paramCharSequence);
        if (localMatcher.find())
        {
          int j = localMatcher.start();
          k = localMatcher.end();
          i = j;
          if (paramCharSequence.charAt(j) != '@')
          {
            i = j;
            if (paramCharSequence.charAt(j) != '#')
            {
              i = j;
              if (paramCharSequence.charAt(j) != '/') {
                i = j + 1;
              }
            }
          }
          localObject = null;
          if (paramCharSequence.charAt(i) == '/')
          {
            if (paramBoolean) {
              localObject = new URLSpanBotCommand(paramCharSequence.subSequence(i, k).toString());
            }
            if (localObject == null) {
              continue;
            }
            ((Spannable)paramCharSequence).setSpan(localObject, i, k, 0);
          }
        }
        else
        {
          return;
        }
      }
      catch (Exception paramCharSequence)
      {
        FileLog.e("tmessages", paramCharSequence);
      }
      Object localObject = new URLSpanNoUnderline(paramCharSequence.subSequence(i, k).toString());
    }
  }
  
  public static boolean canDeleteMessage(TLRPC.Message paramMessage, TLRPC.Chat paramChat)
  {
    boolean bool = false;
    if (paramMessage.id < 0) {}
    TLRPC.Chat localChat;
    do
    {
      do
      {
        return true;
        localChat = paramChat;
        if (paramChat == null)
        {
          localChat = paramChat;
          if (paramMessage.to_id.channel_id != 0) {
            localChat = MessagesController.getInstance().getChat(Integer.valueOf(paramMessage.to_id.channel_id));
          }
        }
        if (!ChatObject.isChannel(localChat)) {
          break;
        }
        if (paramMessage.id == 1) {
          return false;
        }
      } while (localChat.creator);
      if (!localChat.editor) {
        break label116;
      }
    } while ((isOut(paramMessage)) || ((paramMessage.from_id > 0) && (!paramMessage.post)));
    label116:
    do
    {
      do
      {
        if ((isOut(paramMessage)) || (!ChatObject.isChannel(localChat))) {
          bool = true;
        }
        return bool;
        if (!localChat.moderator) {
          break;
        }
      } while ((paramMessage.from_id <= 0) || (paramMessage.post));
      return true;
    } while ((!isOut(paramMessage)) || (paramMessage.from_id <= 0));
    return true;
  }
  
  public static boolean canEditMessage(TLRPC.Message paramMessage, TLRPC.Chat paramChat)
  {
    boolean bool2 = true;
    if ((paramMessage == null) || (paramMessage.to_id == null) || ((paramMessage.action != null) && (!(paramMessage.action instanceof TLRPC.TL_messageActionEmpty))) || (isForwardedMessage(paramMessage)) || (paramMessage.via_bot_id != 0) || (paramMessage.id < 0) || (Math.abs(paramMessage.date - ConnectionsManager.getInstance().getCurrentTime()) > MessagesController.getInstance().maxEditTime)) {}
    label159:
    TLRPC.Chat localChat;
    do
    {
      do
      {
        return false;
        if (paramMessage.to_id.channel_id == 0)
        {
          if (paramMessage.out)
          {
            bool1 = bool2;
            if (!(paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) {
              if ((paramMessage.media instanceof TLRPC.TL_messageMediaDocument))
              {
                bool1 = bool2;
                if (!isStickerMessage(paramMessage)) {}
              }
              else
              {
                bool1 = bool2;
                if (!(paramMessage.media instanceof TLRPC.TL_messageMediaEmpty))
                {
                  bool1 = bool2;
                  if (!(paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
                    if (paramMessage.media != null) {
                      break label159;
                    }
                  }
                }
              }
            }
          }
          for (boolean bool1 = bool2;; bool1 = false) {
            return bool1;
          }
        }
        localChat = paramChat;
        if (paramChat != null) {
          break;
        }
        localChat = paramChat;
        if (paramMessage.to_id.channel_id == 0) {
          break;
        }
        localChat = MessagesController.getInstance().getChat(Integer.valueOf(paramMessage.to_id.channel_id));
      } while (localChat == null);
    } while (((!localChat.megagroup) || (!paramMessage.out)) && ((localChat.megagroup) || ((!localChat.creator) && ((!localChat.editor) || (!isOut(paramMessage)))) || (!paramMessage.post) || ((!(paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && ((!(paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) || (isStickerMessage(paramMessage))) && (!(paramMessage.media instanceof TLRPC.TL_messageMediaEmpty)) && (!(paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) && (paramMessage.media != null))));
    return true;
  }
  
  private static boolean containsUrls(CharSequence paramCharSequence)
  {
    boolean bool2 = true;
    boolean bool1;
    if ((paramCharSequence == null) || (paramCharSequence.length() < 2) || (paramCharSequence.length() > 20480)) {
      bool1 = false;
    }
    int i3;
    int n;
    int m;
    int i2;
    int i1;
    label58:
    int i4;
    int k;
    int i;
    int j;
    label108:
    do
    {
      do
      {
        do
        {
          do
          {
            return bool1;
            int i5 = paramCharSequence.length();
            i3 = 0;
            n = 0;
            m = 0;
            i2 = 0;
            i1 = 0;
            if (i1 >= i5) {
              break label344;
            }
            i4 = paramCharSequence.charAt(i1);
            if ((i4 < 48) || (i4 > 57)) {
              break;
            }
            k = i3 + 1;
            bool1 = bool2;
          } while (k >= 6);
          i = 0;
          j = 0;
          if ((i4 != 64) && (i4 != 35) && (i4 != 47)) {
            break;
          }
          bool1 = bool2;
        } while (i1 == 0);
        if (i1 == 0) {
          break;
        }
        bool1 = bool2;
      } while (paramCharSequence.charAt(i1 - 1) == ' ');
      bool1 = bool2;
    } while (paramCharSequence.charAt(i1 - 1) == '\n');
    if (i4 == 58) {
      if (i == 0) {
        i = 1;
      }
    }
    for (;;)
    {
      i2 = i4;
      i1 += 1;
      i3 = k;
      m = j;
      n = i;
      break label58;
      if (i4 != 32)
      {
        k = i3;
        j = m;
        i = n;
        if (i3 > 0) {
          break label108;
        }
      }
      k = 0;
      j = m;
      i = n;
      break label108;
      i = 0;
      continue;
      if (i4 == 47)
      {
        bool1 = bool2;
        if (i == 2) {
          break;
        }
        if (i == 1)
        {
          i += 1;
          continue;
        }
        i = 0;
        continue;
      }
      if (i4 == 46)
      {
        if ((j == 0) && (i2 != 32)) {
          j += 1;
        } else {
          j = 0;
        }
      }
      else
      {
        if ((i4 != 32) && (i2 == 46))
        {
          bool1 = bool2;
          if (j == 1) {
            break;
          }
        }
        j = 0;
      }
    }
    label344:
    return false;
  }
  
  /* Error */
  private void generateLayout(TLRPC.User paramUser)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 62	org/telegram/messenger/MessageObject:type	I
    //   4: ifne +32 -> 36
    //   7: aload_0
    //   8: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   11: getfield 307	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   14: ifnull +22 -> 36
    //   17: aload_0
    //   18: getfield 161	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   21: ifnull +15 -> 36
    //   24: aload_0
    //   25: getfield 161	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   28: invokeinterface 636 1 0
    //   33: ifne +4 -> 37
    //   36: return
    //   37: aload_0
    //   38: invokevirtual 768	org/telegram/messenger/MessageObject:generateLinkDescription	()V
    //   41: aload_0
    //   42: new 294	java/util/ArrayList
    //   45: dup
    //   46: invokespecial 769	java/util/ArrayList:<init>	()V
    //   49: putfield 771	org/telegram/messenger/MessageObject:textLayoutBlocks	Ljava/util/ArrayList;
    //   52: aload_0
    //   53: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   56: getfield 774	org/telegram/tgnet/TLRPC$Message:send_state	I
    //   59: ifeq +314 -> 373
    //   62: iconst_0
    //   63: istore 9
    //   65: iconst_0
    //   66: istore 8
    //   68: iload 9
    //   70: istore 7
    //   72: iload 8
    //   74: aload_0
    //   75: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   78: getfield 777	org/telegram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   81: invokevirtual 297	java/util/ArrayList:size	()I
    //   84: if_icmpge +24 -> 108
    //   87: aload_0
    //   88: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   91: getfield 777	org/telegram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   94: iload 8
    //   96: invokevirtual 300	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   99: instanceof 779
    //   102: ifne +262 -> 364
    //   105: iconst_1
    //   106: istore 7
    //   108: iload 7
    //   110: ifne +288 -> 398
    //   113: aload_0
    //   114: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   117: instanceof 781
    //   120: ifne +103 -> 223
    //   123: aload_0
    //   124: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   127: instanceof 783
    //   130: ifne +93 -> 223
    //   133: aload_0
    //   134: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   137: instanceof 785
    //   140: ifne +83 -> 223
    //   143: aload_0
    //   144: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   147: instanceof 787
    //   150: ifne +73 -> 223
    //   153: aload_0
    //   154: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   157: instanceof 789
    //   160: ifne +63 -> 223
    //   163: aload_0
    //   164: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   167: instanceof 791
    //   170: ifne +53 -> 223
    //   173: aload_0
    //   174: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   177: instanceof 793
    //   180: ifne +43 -> 223
    //   183: aload_0
    //   184: invokevirtual 150	org/telegram/messenger/MessageObject:isOut	()Z
    //   187: ifeq +13 -> 200
    //   190: aload_0
    //   191: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   194: getfield 774	org/telegram/tgnet/TLRPC$Message:send_state	I
    //   197: ifne +26 -> 223
    //   200: aload_0
    //   201: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   204: getfield 206	org/telegram/tgnet/TLRPC$Message:id	I
    //   207: iflt +16 -> 223
    //   210: aload_0
    //   211: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   214: getfield 544	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   217: instanceof 572
    //   220: ifeq +178 -> 398
    //   223: iconst_1
    //   224: istore 7
    //   226: iload 7
    //   228: ifeq +176 -> 404
    //   231: aload_0
    //   232: getfield 161	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   235: invokestatic 795	org/telegram/messenger/MessageObject:addLinks	(Ljava/lang/CharSequence;)V
    //   238: aload_0
    //   239: getfield 161	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   242: instanceof 629
    //   245: ifeq +1060 -> 1305
    //   248: aload_0
    //   249: getfield 161	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   252: checkcast 629	android/text/Spannable
    //   255: astore 18
    //   257: aload_0
    //   258: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   261: getfield 777	org/telegram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   264: invokevirtual 297	java/util/ArrayList:size	()I
    //   267: istore 10
    //   269: aload 18
    //   271: iconst_0
    //   272: aload_0
    //   273: getfield 161	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   276: invokeinterface 636 1 0
    //   281: ldc_w 797
    //   284: invokeinterface 801 4 0
    //   289: checkcast 803	[Landroid/text/style/URLSpan;
    //   292: astore 19
    //   294: iconst_0
    //   295: istore 8
    //   297: iload 8
    //   299: iload 10
    //   301: if_icmpge +1004 -> 1305
    //   304: aload_0
    //   305: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   308: getfield 777	org/telegram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   311: iload 8
    //   313: invokevirtual 300	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   316: checkcast 805	org/telegram/tgnet/TLRPC$MessageEntity
    //   319: astore 20
    //   321: aload 20
    //   323: getfield 807	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   326: ifle +29 -> 355
    //   329: aload 20
    //   331: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   334: iflt +21 -> 355
    //   337: aload 20
    //   339: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   342: aload_0
    //   343: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   346: getfield 203	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   349: invokevirtual 210	java/lang/String:length	()I
    //   352: if_icmplt +105 -> 457
    //   355: iload 8
    //   357: iconst_1
    //   358: iadd
    //   359: istore 8
    //   361: goto -64 -> 297
    //   364: iload 8
    //   366: iconst_1
    //   367: iadd
    //   368: istore 8
    //   370: goto -302 -> 68
    //   373: aload_0
    //   374: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   377: getfield 777	org/telegram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   380: invokevirtual 813	java/util/ArrayList:isEmpty	()Z
    //   383: ifne +9 -> 392
    //   386: iconst_1
    //   387: istore 7
    //   389: goto -281 -> 108
    //   392: iconst_0
    //   393: istore 7
    //   395: goto -6 -> 389
    //   398: iconst_0
    //   399: istore 7
    //   401: goto -175 -> 226
    //   404: aload_0
    //   405: getfield 161	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   408: instanceof 629
    //   411: ifeq -173 -> 238
    //   414: aload_0
    //   415: getfield 161	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   418: invokeinterface 636 1 0
    //   423: sipush 200
    //   426: if_icmpge -188 -> 238
    //   429: aload_0
    //   430: getfield 161	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   433: checkcast 629	android/text/Spannable
    //   436: iconst_4
    //   437: invokestatic 641	android/text/util/Linkify:addLinks	(Landroid/text/Spannable;I)Z
    //   440: pop
    //   441: goto -203 -> 238
    //   444: astore 18
    //   446: ldc_w 646
    //   449: aload 18
    //   451: invokestatic 652	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   454: goto -216 -> 238
    //   457: aload 20
    //   459: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   462: aload 20
    //   464: getfield 807	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   467: iadd
    //   468: aload_0
    //   469: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   472: getfield 203	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   475: invokevirtual 210	java/lang/String:length	()I
    //   478: if_icmple +24 -> 502
    //   481: aload 20
    //   483: aload_0
    //   484: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   487: getfield 203	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   490: invokevirtual 210	java/lang/String:length	()I
    //   493: aload 20
    //   495: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   498: isub
    //   499: putfield 807	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   502: aload 19
    //   504: ifnull +138 -> 642
    //   507: aload 19
    //   509: arraylength
    //   510: ifle +132 -> 642
    //   513: iconst_0
    //   514: istore 9
    //   516: iload 9
    //   518: aload 19
    //   520: arraylength
    //   521: if_icmpge +121 -> 642
    //   524: aload 19
    //   526: iload 9
    //   528: aaload
    //   529: ifnonnull +12 -> 541
    //   532: iload 9
    //   534: iconst_1
    //   535: iadd
    //   536: istore 9
    //   538: goto -22 -> 516
    //   541: aload 18
    //   543: aload 19
    //   545: iload 9
    //   547: aaload
    //   548: invokeinterface 817 2 0
    //   553: istore 11
    //   555: aload 18
    //   557: aload 19
    //   559: iload 9
    //   561: aaload
    //   562: invokeinterface 820 2 0
    //   567: istore 12
    //   569: aload 20
    //   571: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   574: iload 11
    //   576: if_icmpgt +19 -> 595
    //   579: aload 20
    //   581: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   584: aload 20
    //   586: getfield 807	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   589: iadd
    //   590: iload 11
    //   592: if_icmpge +29 -> 621
    //   595: aload 20
    //   597: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   600: iload 12
    //   602: if_icmpgt -70 -> 532
    //   605: aload 20
    //   607: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   610: aload 20
    //   612: getfield 807	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   615: iadd
    //   616: iload 12
    //   618: if_icmplt -86 -> 532
    //   621: aload 18
    //   623: aload 19
    //   625: iload 9
    //   627: aaload
    //   628: invokeinterface 824 2 0
    //   633: aload 19
    //   635: iload 9
    //   637: aconst_null
    //   638: aastore
    //   639: goto -107 -> 532
    //   642: aload 20
    //   644: instanceof 826
    //   647: ifeq +44 -> 691
    //   650: aload 18
    //   652: new 828	org/telegram/ui/Components/TypefaceSpan
    //   655: dup
    //   656: ldc_w 830
    //   659: invokestatic 834	org/telegram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   662: invokespecial 837	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;)V
    //   665: aload 20
    //   667: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   670: aload 20
    //   672: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   675: aload 20
    //   677: getfield 807	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   680: iadd
    //   681: bipush 33
    //   683: invokeinterface 695 5 0
    //   688: goto -333 -> 355
    //   691: aload 20
    //   693: instanceof 839
    //   696: ifeq +44 -> 740
    //   699: aload 18
    //   701: new 828	org/telegram/ui/Components/TypefaceSpan
    //   704: dup
    //   705: ldc_w 841
    //   708: invokestatic 834	org/telegram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   711: invokespecial 837	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;)V
    //   714: aload 20
    //   716: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   719: aload 20
    //   721: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   724: aload 20
    //   726: getfield 807	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   729: iadd
    //   730: bipush 33
    //   732: invokeinterface 695 5 0
    //   737: goto -382 -> 355
    //   740: aload 20
    //   742: instanceof 843
    //   745: ifne +11 -> 756
    //   748: aload 20
    //   750: instanceof 845
    //   753: ifeq +53 -> 806
    //   756: aload 18
    //   758: new 828	org/telegram/ui/Components/TypefaceSpan
    //   761: dup
    //   762: getstatic 851	android/graphics/Typeface:MONOSPACE	Landroid/graphics/Typeface;
    //   765: invokestatic 83	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   768: getfield 86	org/telegram/messenger/MessagesController:fontSize	I
    //   771: iconst_1
    //   772: isub
    //   773: i2f
    //   774: invokestatic 92	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   777: invokespecial 854	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;I)V
    //   780: aload 20
    //   782: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   785: aload 20
    //   787: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   790: aload 20
    //   792: getfield 807	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   795: iadd
    //   796: bipush 33
    //   798: invokeinterface 695 5 0
    //   803: goto -448 -> 355
    //   806: aload 20
    //   808: instanceof 856
    //   811: ifeq +64 -> 875
    //   814: aload 18
    //   816: new 858	org/telegram/ui/Components/URLSpanUserMention
    //   819: dup
    //   820: new 460	java/lang/StringBuilder
    //   823: dup
    //   824: invokespecial 461	java/lang/StringBuilder:<init>	()V
    //   827: ldc -93
    //   829: invokevirtual 465	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   832: aload 20
    //   834: checkcast 856	org/telegram/tgnet/TLRPC$TL_messageEntityMentionName
    //   837: getfield 859	org/telegram/tgnet/TLRPC$TL_messageEntityMentionName:user_id	I
    //   840: invokevirtual 468	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   843: invokevirtual 472	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   846: invokespecial 860	org/telegram/ui/Components/URLSpanUserMention:<init>	(Ljava/lang/String;)V
    //   849: aload 20
    //   851: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   854: aload 20
    //   856: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   859: aload 20
    //   861: getfield 807	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   864: iadd
    //   865: bipush 33
    //   867: invokeinterface 695 5 0
    //   872: goto -517 -> 355
    //   875: aload 20
    //   877: instanceof 779
    //   880: ifeq +67 -> 947
    //   883: aload 18
    //   885: new 858	org/telegram/ui/Components/URLSpanUserMention
    //   888: dup
    //   889: new 460	java/lang/StringBuilder
    //   892: dup
    //   893: invokespecial 461	java/lang/StringBuilder:<init>	()V
    //   896: ldc -93
    //   898: invokevirtual 465	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   901: aload 20
    //   903: checkcast 779	org/telegram/tgnet/TLRPC$TL_inputMessageEntityMentionName
    //   906: getfield 863	org/telegram/tgnet/TLRPC$TL_inputMessageEntityMentionName:user_id	Lorg/telegram/tgnet/TLRPC$InputUser;
    //   909: getfield 866	org/telegram/tgnet/TLRPC$InputUser:user_id	I
    //   912: invokevirtual 468	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   915: invokevirtual 472	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   918: invokespecial 860	org/telegram/ui/Components/URLSpanUserMention:<init>	(Ljava/lang/String;)V
    //   921: aload 20
    //   923: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   926: aload 20
    //   928: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   931: aload 20
    //   933: getfield 807	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   936: iadd
    //   937: bipush 33
    //   939: invokeinterface 695 5 0
    //   944: goto -589 -> 355
    //   947: iload 7
    //   949: ifne -594 -> 355
    //   952: aload_0
    //   953: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   956: getfield 203	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   959: aload 20
    //   961: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   964: aload 20
    //   966: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   969: aload 20
    //   971: getfield 807	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   974: iadd
    //   975: invokevirtual 870	java/lang/String:substring	(II)Ljava/lang/String;
    //   978: astore 21
    //   980: aload 20
    //   982: instanceof 872
    //   985: ifeq +40 -> 1025
    //   988: aload 18
    //   990: new 683	org/telegram/ui/Components/URLSpanBotCommand
    //   993: dup
    //   994: aload 21
    //   996: invokespecial 691	org/telegram/ui/Components/URLSpanBotCommand:<init>	(Ljava/lang/String;)V
    //   999: aload 20
    //   1001: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1004: aload 20
    //   1006: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1009: aload 20
    //   1011: getfield 807	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   1014: iadd
    //   1015: bipush 33
    //   1017: invokeinterface 695 5 0
    //   1022: goto -667 -> 355
    //   1025: aload 20
    //   1027: instanceof 874
    //   1030: ifne +11 -> 1041
    //   1033: aload 20
    //   1035: instanceof 876
    //   1038: ifeq +40 -> 1078
    //   1041: aload 18
    //   1043: new 697	org/telegram/ui/Components/URLSpanNoUnderline
    //   1046: dup
    //   1047: aload 21
    //   1049: invokespecial 698	org/telegram/ui/Components/URLSpanNoUnderline:<init>	(Ljava/lang/String;)V
    //   1052: aload 20
    //   1054: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1057: aload 20
    //   1059: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1062: aload 20
    //   1064: getfield 807	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   1067: iadd
    //   1068: bipush 33
    //   1070: invokeinterface 695 5 0
    //   1075: goto -720 -> 355
    //   1078: aload 20
    //   1080: instanceof 878
    //   1083: ifeq +59 -> 1142
    //   1086: aload 18
    //   1088: new 880	org/telegram/ui/Components/URLSpanReplacement
    //   1091: dup
    //   1092: new 460	java/lang/StringBuilder
    //   1095: dup
    //   1096: invokespecial 461	java/lang/StringBuilder:<init>	()V
    //   1099: ldc_w 882
    //   1102: invokevirtual 465	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1105: aload 21
    //   1107: invokevirtual 465	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1110: invokevirtual 472	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1113: invokespecial 883	org/telegram/ui/Components/URLSpanReplacement:<init>	(Ljava/lang/String;)V
    //   1116: aload 20
    //   1118: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1121: aload 20
    //   1123: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1126: aload 20
    //   1128: getfield 807	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   1131: iadd
    //   1132: bipush 33
    //   1134: invokeinterface 695 5 0
    //   1139: goto -784 -> 355
    //   1142: aload 20
    //   1144: instanceof 885
    //   1147: ifeq +110 -> 1257
    //   1150: aload 21
    //   1152: invokevirtual 888	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   1155: ldc_w 890
    //   1158: invokevirtual 893	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   1161: ifne +59 -> 1220
    //   1164: aload 18
    //   1166: new 797	android/text/style/URLSpan
    //   1169: dup
    //   1170: new 460	java/lang/StringBuilder
    //   1173: dup
    //   1174: invokespecial 461	java/lang/StringBuilder:<init>	()V
    //   1177: ldc_w 895
    //   1180: invokevirtual 465	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1183: aload 21
    //   1185: invokevirtual 465	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1188: invokevirtual 472	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1191: invokespecial 896	android/text/style/URLSpan:<init>	(Ljava/lang/String;)V
    //   1194: aload 20
    //   1196: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1199: aload 20
    //   1201: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1204: aload 20
    //   1206: getfield 807	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   1209: iadd
    //   1210: bipush 33
    //   1212: invokeinterface 695 5 0
    //   1217: goto -862 -> 355
    //   1220: aload 18
    //   1222: new 797	android/text/style/URLSpan
    //   1225: dup
    //   1226: aload 21
    //   1228: invokespecial 896	android/text/style/URLSpan:<init>	(Ljava/lang/String;)V
    //   1231: aload 20
    //   1233: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1236: aload 20
    //   1238: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1241: aload 20
    //   1243: getfield 807	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   1246: iadd
    //   1247: bipush 33
    //   1249: invokeinterface 695 5 0
    //   1254: goto -899 -> 355
    //   1257: aload 20
    //   1259: instanceof 898
    //   1262: ifeq -907 -> 355
    //   1265: aload 18
    //   1267: new 880	org/telegram/ui/Components/URLSpanReplacement
    //   1270: dup
    //   1271: aload 20
    //   1273: getfield 901	org/telegram/tgnet/TLRPC$MessageEntity:url	Ljava/lang/String;
    //   1276: invokespecial 883	org/telegram/ui/Components/URLSpanReplacement:<init>	(Ljava/lang/String;)V
    //   1279: aload 20
    //   1281: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1284: aload 20
    //   1286: getfield 810	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1289: aload 20
    //   1291: getfield 807	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   1294: iadd
    //   1295: bipush 33
    //   1297: invokeinterface 695 5 0
    //   1302: goto -947 -> 355
    //   1305: invokestatic 904	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   1308: ifeq +531 -> 1839
    //   1311: aload_0
    //   1312: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1315: getfield 120	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   1318: ifle +506 -> 1824
    //   1321: aload_0
    //   1322: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1325: getfield 307	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   1328: getfield 312	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   1331: ifne +16 -> 1347
    //   1334: aload_0
    //   1335: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1338: getfield 307	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   1341: getfield 907	org/telegram/tgnet/TLRPC$Peer:chat_id	I
    //   1344: ifeq +480 -> 1824
    //   1347: aload_0
    //   1348: invokevirtual 150	org/telegram/messenger/MessageObject:isOut	()Z
    //   1351: ifne +473 -> 1824
    //   1354: invokestatic 910	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   1357: ldc_w 911
    //   1360: invokestatic 92	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1363: isub
    //   1364: istore 7
    //   1366: aload_1
    //   1367: ifnull +10 -> 1377
    //   1370: aload_1
    //   1371: getfield 914	org/telegram/tgnet/TLRPC$User:bot	Z
    //   1374: ifne +52 -> 1426
    //   1377: aload_0
    //   1378: invokevirtual 315	org/telegram/messenger/MessageObject:isMegagroup	()Z
    //   1381: ifne +34 -> 1415
    //   1384: iload 7
    //   1386: istore 8
    //   1388: aload_0
    //   1389: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1392: getfield 918	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$TL_messageFwdHeader;
    //   1395: ifnull +41 -> 1436
    //   1398: iload 7
    //   1400: istore 8
    //   1402: aload_0
    //   1403: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1406: getfield 918	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$TL_messageFwdHeader;
    //   1409: getfield 921	org/telegram/tgnet/TLRPC$TL_messageFwdHeader:channel_id	I
    //   1412: ifeq +24 -> 1436
    //   1415: iload 7
    //   1417: istore 8
    //   1419: aload_0
    //   1420: invokevirtual 150	org/telegram/messenger/MessageObject:isOut	()Z
    //   1423: ifne +13 -> 1436
    //   1426: iload 7
    //   1428: ldc -26
    //   1430: invokestatic 92	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1433: isub
    //   1434: istore 8
    //   1436: new 923	android/text/StaticLayout
    //   1439: dup
    //   1440: aload_0
    //   1441: getfield 161	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   1444: getstatic 64	org/telegram/messenger/MessageObject:textPaint	Landroid/text/TextPaint;
    //   1447: iload 8
    //   1449: getstatic 929	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1452: fconst_1
    //   1453: fconst_0
    //   1454: iconst_0
    //   1455: invokespecial 932	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1458: astore_1
    //   1459: aload_0
    //   1460: aload_1
    //   1461: invokevirtual 935	android/text/StaticLayout:getHeight	()I
    //   1464: putfield 937	org/telegram/messenger/MessageObject:textHeight	I
    //   1467: aload_1
    //   1468: invokevirtual 940	android/text/StaticLayout:getLineCount	()I
    //   1471: istore 16
    //   1473: iload 16
    //   1475: i2f
    //   1476: ldc_w 941
    //   1479: fdiv
    //   1480: f2d
    //   1481: invokestatic 945	java/lang/Math:ceil	(D)D
    //   1484: d2i
    //   1485: istore 17
    //   1487: iconst_0
    //   1488: istore 10
    //   1490: fconst_0
    //   1491: fstore_2
    //   1492: iconst_0
    //   1493: istore 9
    //   1495: iload 9
    //   1497: iload 17
    //   1499: if_icmpge -1463 -> 36
    //   1502: bipush 10
    //   1504: iload 16
    //   1506: iload 10
    //   1508: isub
    //   1509: invokestatic 949	java/lang/Math:min	(II)I
    //   1512: istore 7
    //   1514: new 6	org/telegram/messenger/MessageObject$TextLayoutBlock
    //   1517: dup
    //   1518: invokespecial 950	org/telegram/messenger/MessageObject$TextLayoutBlock:<init>	()V
    //   1521: astore 18
    //   1523: iload 17
    //   1525: iconst_1
    //   1526: if_icmpne +419 -> 1945
    //   1529: aload 18
    //   1531: aload_1
    //   1532: putfield 954	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1535: aload 18
    //   1537: fconst_0
    //   1538: putfield 957	org/telegram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   1541: aload 18
    //   1543: iconst_0
    //   1544: putfield 960	org/telegram/messenger/MessageObject$TextLayoutBlock:charactersOffset	I
    //   1547: aload 18
    //   1549: aload_0
    //   1550: getfield 937	org/telegram/messenger/MessageObject:textHeight	I
    //   1553: putfield 963	org/telegram/messenger/MessageObject$TextLayoutBlock:height	I
    //   1556: iload 7
    //   1558: istore 11
    //   1560: aload_0
    //   1561: getfield 771	org/telegram/messenger/MessageObject:textLayoutBlocks	Ljava/util/ArrayList;
    //   1564: aload 18
    //   1566: invokevirtual 967	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1569: pop
    //   1570: fconst_0
    //   1571: fstore_3
    //   1572: aload 18
    //   1574: fconst_0
    //   1575: putfield 970	org/telegram/messenger/MessageObject$TextLayoutBlock:textXOffset	F
    //   1578: aload 18
    //   1580: getfield 954	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1583: iload 11
    //   1585: iconst_1
    //   1586: isub
    //   1587: invokevirtual 974	android/text/StaticLayout:getLineLeft	(I)F
    //   1590: fstore 4
    //   1592: aload 18
    //   1594: fload 4
    //   1596: putfield 970	org/telegram/messenger/MessageObject$TextLayoutBlock:textXOffset	F
    //   1599: fload 4
    //   1601: fstore_3
    //   1602: fconst_0
    //   1603: fstore 4
    //   1605: aload 18
    //   1607: getfield 954	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1610: iload 11
    //   1612: iconst_1
    //   1613: isub
    //   1614: invokevirtual 977	android/text/StaticLayout:getLineWidth	(I)F
    //   1617: fstore 5
    //   1619: fload 5
    //   1621: fstore 4
    //   1623: fload 4
    //   1625: f2d
    //   1626: invokestatic 945	java/lang/Math:ceil	(D)D
    //   1629: d2i
    //   1630: istore 14
    //   1632: iconst_0
    //   1633: istore 7
    //   1635: iload 9
    //   1637: iload 17
    //   1639: iconst_1
    //   1640: isub
    //   1641: if_icmpne +9 -> 1650
    //   1644: aload_0
    //   1645: iload 14
    //   1647: putfield 979	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   1650: fload 4
    //   1652: fload_3
    //   1653: fadd
    //   1654: f2d
    //   1655: invokestatic 945	java/lang/Math:ceil	(D)D
    //   1658: d2i
    //   1659: istore 15
    //   1661: iload 15
    //   1663: istore 13
    //   1665: fload_3
    //   1666: fconst_0
    //   1667: fcmpl
    //   1668: ifne +6 -> 1674
    //   1671: iconst_1
    //   1672: istore 7
    //   1674: iload 11
    //   1676: iconst_1
    //   1677: if_icmple +655 -> 2332
    //   1680: fconst_0
    //   1681: fstore_3
    //   1682: fconst_0
    //   1683: fstore 4
    //   1685: iconst_0
    //   1686: istore 12
    //   1688: iload 12
    //   1690: iload 11
    //   1692: if_icmpge +548 -> 2240
    //   1695: aload 18
    //   1697: getfield 954	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1700: iload 12
    //   1702: invokevirtual 977	android/text/StaticLayout:getLineWidth	(I)F
    //   1705: fstore 5
    //   1707: fload 5
    //   1709: fstore 6
    //   1711: fload 5
    //   1713: iload 8
    //   1715: bipush 100
    //   1717: iadd
    //   1718: i2f
    //   1719: fcmpl
    //   1720: ifle +8 -> 1728
    //   1723: iload 8
    //   1725: i2f
    //   1726: fstore 6
    //   1728: aload 18
    //   1730: getfield 954	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1733: iload 12
    //   1735: invokevirtual 974	android/text/StaticLayout:getLineLeft	(I)F
    //   1738: fstore 5
    //   1740: aload 18
    //   1742: aload 18
    //   1744: getfield 970	org/telegram/messenger/MessageObject$TextLayoutBlock:textXOffset	F
    //   1747: fload 5
    //   1749: invokestatic 982	java/lang/Math:min	(FF)F
    //   1752: putfield 970	org/telegram/messenger/MessageObject$TextLayoutBlock:textXOffset	F
    //   1755: fload 5
    //   1757: fconst_0
    //   1758: fcmpl
    //   1759: ifne +6 -> 1765
    //   1762: iconst_1
    //   1763: istore 7
    //   1765: fload_3
    //   1766: fload 6
    //   1768: invokestatic 985	java/lang/Math:max	(FF)F
    //   1771: fstore_3
    //   1772: fload 4
    //   1774: fload 6
    //   1776: fload 5
    //   1778: fadd
    //   1779: invokestatic 985	java/lang/Math:max	(FF)F
    //   1782: fstore 4
    //   1784: iload 14
    //   1786: fload 6
    //   1788: f2d
    //   1789: invokestatic 945	java/lang/Math:ceil	(D)D
    //   1792: d2i
    //   1793: invokestatic 987	java/lang/Math:max	(II)I
    //   1796: istore 14
    //   1798: iload 13
    //   1800: fload 6
    //   1802: fload 5
    //   1804: fadd
    //   1805: f2d
    //   1806: invokestatic 945	java/lang/Math:ceil	(D)D
    //   1809: d2i
    //   1810: invokestatic 987	java/lang/Math:max	(II)I
    //   1813: istore 13
    //   1815: iload 12
    //   1817: iconst_1
    //   1818: iadd
    //   1819: istore 12
    //   1821: goto -133 -> 1688
    //   1824: invokestatic 910	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   1827: ldc_w 988
    //   1830: invokestatic 92	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1833: isub
    //   1834: istore 7
    //   1836: goto -470 -> 1366
    //   1839: aload_0
    //   1840: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1843: getfield 120	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   1846: ifle +63 -> 1909
    //   1849: aload_0
    //   1850: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1853: getfield 307	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   1856: getfield 312	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   1859: ifne +16 -> 1875
    //   1862: aload_0
    //   1863: getfield 108	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1866: getfield 307	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   1869: getfield 907	org/telegram/tgnet/TLRPC$Peer:chat_id	I
    //   1872: ifeq +37 -> 1909
    //   1875: aload_0
    //   1876: invokevirtual 150	org/telegram/messenger/MessageObject:isOut	()Z
    //   1879: ifne +30 -> 1909
    //   1882: getstatic 992	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1885: getfield 997	android/graphics/Point:x	I
    //   1888: getstatic 992	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1891: getfield 1000	android/graphics/Point:y	I
    //   1894: invokestatic 949	java/lang/Math:min	(II)I
    //   1897: ldc_w 911
    //   1900: invokestatic 92	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1903: isub
    //   1904: istore 7
    //   1906: goto -540 -> 1366
    //   1909: getstatic 992	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1912: getfield 997	android/graphics/Point:x	I
    //   1915: getstatic 992	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1918: getfield 1000	android/graphics/Point:y	I
    //   1921: invokestatic 949	java/lang/Math:min	(II)I
    //   1924: ldc_w 988
    //   1927: invokestatic 92	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1930: isub
    //   1931: istore 7
    //   1933: goto -567 -> 1366
    //   1936: astore_1
    //   1937: ldc_w 646
    //   1940: aload_1
    //   1941: invokestatic 652	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1944: return
    //   1945: aload_1
    //   1946: iload 10
    //   1948: invokevirtual 1003	android/text/StaticLayout:getLineStart	(I)I
    //   1951: istore 11
    //   1953: aload_1
    //   1954: iload 10
    //   1956: iload 7
    //   1958: iadd
    //   1959: iconst_1
    //   1960: isub
    //   1961: invokevirtual 1006	android/text/StaticLayout:getLineEnd	(I)I
    //   1964: istore 12
    //   1966: iload 12
    //   1968: iload 11
    //   1970: if_icmpge +12 -> 1982
    //   1973: iload 9
    //   1975: iconst_1
    //   1976: iadd
    //   1977: istore 9
    //   1979: goto -484 -> 1495
    //   1982: aload 18
    //   1984: iload 11
    //   1986: putfield 960	org/telegram/messenger/MessageObject$TextLayoutBlock:charactersOffset	I
    //   1989: aload 18
    //   1991: new 923	android/text/StaticLayout
    //   1994: dup
    //   1995: aload_0
    //   1996: getfield 161	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   1999: iload 11
    //   2001: iload 12
    //   2003: invokeinterface 687 3 0
    //   2008: getstatic 64	org/telegram/messenger/MessageObject:textPaint	Landroid/text/TextPaint;
    //   2011: iload 8
    //   2013: getstatic 929	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   2016: fconst_1
    //   2017: fconst_0
    //   2018: iconst_0
    //   2019: invokespecial 932	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   2022: putfield 954	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   2025: aload 18
    //   2027: aload_1
    //   2028: iload 10
    //   2030: invokevirtual 1009	android/text/StaticLayout:getLineTop	(I)I
    //   2033: i2f
    //   2034: putfield 957	org/telegram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   2037: iload 9
    //   2039: ifeq +16 -> 2055
    //   2042: aload 18
    //   2044: aload 18
    //   2046: getfield 957	org/telegram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   2049: fload_2
    //   2050: fsub
    //   2051: f2i
    //   2052: putfield 963	org/telegram/messenger/MessageObject$TextLayoutBlock:height	I
    //   2055: aload 18
    //   2057: aload 18
    //   2059: getfield 963	org/telegram/messenger/MessageObject$TextLayoutBlock:height	I
    //   2062: aload 18
    //   2064: getfield 954	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   2067: aload 18
    //   2069: getfield 954	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   2072: invokevirtual 940	android/text/StaticLayout:getLineCount	()I
    //   2075: iconst_1
    //   2076: isub
    //   2077: invokevirtual 1012	android/text/StaticLayout:getLineBottom	(I)I
    //   2080: invokestatic 987	java/lang/Math:max	(II)I
    //   2083: putfield 963	org/telegram/messenger/MessageObject$TextLayoutBlock:height	I
    //   2086: aload 18
    //   2088: getfield 957	org/telegram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   2091: fstore_3
    //   2092: iload 7
    //   2094: istore 11
    //   2096: fload_3
    //   2097: fstore_2
    //   2098: iload 9
    //   2100: iload 17
    //   2102: iconst_1
    //   2103: isub
    //   2104: if_icmpne -544 -> 1560
    //   2107: iload 7
    //   2109: aload 18
    //   2111: getfield 954	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   2114: invokevirtual 940	android/text/StaticLayout:getLineCount	()I
    //   2117: invokestatic 987	java/lang/Math:max	(II)I
    //   2120: istore 11
    //   2122: aload_0
    //   2123: aload_0
    //   2124: getfield 937	org/telegram/messenger/MessageObject:textHeight	I
    //   2127: aload 18
    //   2129: getfield 957	org/telegram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   2132: aload 18
    //   2134: getfield 954	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   2137: invokevirtual 935	android/text/StaticLayout:getHeight	()I
    //   2140: i2f
    //   2141: fadd
    //   2142: f2i
    //   2143: invokestatic 987	java/lang/Math:max	(II)I
    //   2146: putfield 937	org/telegram/messenger/MessageObject:textHeight	I
    //   2149: fload_3
    //   2150: fstore_2
    //   2151: goto -591 -> 1560
    //   2154: astore 19
    //   2156: ldc_w 646
    //   2159: aload 19
    //   2161: invokestatic 652	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2164: fload_3
    //   2165: fstore_2
    //   2166: goto -606 -> 1560
    //   2169: astore 18
    //   2171: ldc_w 646
    //   2174: aload 18
    //   2176: invokestatic 652	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2179: goto -206 -> 1973
    //   2182: astore 19
    //   2184: ldc_w 646
    //   2187: aload 19
    //   2189: invokestatic 652	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2192: goto -590 -> 1602
    //   2195: astore 19
    //   2197: ldc_w 646
    //   2200: aload 19
    //   2202: invokestatic 652	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2205: goto -582 -> 1623
    //   2208: astore 19
    //   2210: ldc_w 646
    //   2213: aload 19
    //   2215: invokestatic 652	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2218: fconst_0
    //   2219: fstore 5
    //   2221: goto -514 -> 1707
    //   2224: astore 19
    //   2226: ldc_w 646
    //   2229: aload 19
    //   2231: invokestatic 652	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2234: fconst_0
    //   2235: fstore 5
    //   2237: goto -497 -> 1740
    //   2240: iload 7
    //   2242: ifeq +66 -> 2308
    //   2245: fload 4
    //   2247: fstore_3
    //   2248: fload_3
    //   2249: fstore 4
    //   2251: iload 9
    //   2253: iload 17
    //   2255: iconst_1
    //   2256: isub
    //   2257: if_icmpne +12 -> 2269
    //   2260: aload_0
    //   2261: iload 15
    //   2263: putfield 979	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   2266: fload_3
    //   2267: fstore 4
    //   2269: aload_0
    //   2270: aload_0
    //   2271: getfield 1014	org/telegram/messenger/MessageObject:textWidth	I
    //   2274: fload 4
    //   2276: f2d
    //   2277: invokestatic 945	java/lang/Math:ceil	(D)D
    //   2280: d2i
    //   2281: invokestatic 987	java/lang/Math:max	(II)I
    //   2284: putfield 1014	org/telegram/messenger/MessageObject:textWidth	I
    //   2287: iload 7
    //   2289: ifeq +9 -> 2298
    //   2292: aload 18
    //   2294: fconst_0
    //   2295: putfield 970	org/telegram/messenger/MessageObject$TextLayoutBlock:textXOffset	F
    //   2298: iload 10
    //   2300: iload 11
    //   2302: iadd
    //   2303: istore 10
    //   2305: goto -332 -> 1973
    //   2308: fload_3
    //   2309: fstore 4
    //   2311: iload 9
    //   2313: iload 17
    //   2315: iconst_1
    //   2316: isub
    //   2317: if_icmpne -48 -> 2269
    //   2320: aload_0
    //   2321: iload 14
    //   2323: putfield 979	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   2326: fload_3
    //   2327: fstore 4
    //   2329: goto -60 -> 2269
    //   2332: aload_0
    //   2333: aload_0
    //   2334: getfield 1014	org/telegram/messenger/MessageObject:textWidth	I
    //   2337: iload 8
    //   2339: iload 14
    //   2341: invokestatic 949	java/lang/Math:min	(II)I
    //   2344: invokestatic 987	java/lang/Math:max	(II)I
    //   2347: putfield 1014	org/telegram/messenger/MessageObject:textWidth	I
    //   2350: goto -63 -> 2287
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	2353	0	this	MessageObject
    //   0	2353	1	paramUser	TLRPC.User
    //   1491	675	2	f1	float
    //   1571	756	3	f2	float
    //   1590	738	4	f3	float
    //   1617	619	5	f4	float
    //   1709	92	6	f5	float
    //   70	2218	7	i	int
    //   66	2272	8	j	int
    //   63	2255	9	k	int
    //   267	2037	10	m	int
    //   553	1750	11	n	int
    //   567	1435	12	i1	int
    //   1663	151	13	i2	int
    //   1630	710	14	i3	int
    //   1659	603	15	i4	int
    //   1471	38	16	i5	int
    //   1485	832	17	i6	int
    //   255	15	18	localSpannable	Spannable
    //   444	822	18	localThrowable	Throwable
    //   1521	612	18	localTextLayoutBlock	TextLayoutBlock
    //   2169	124	18	localException1	Exception
    //   292	342	19	arrayOfURLSpan	android.text.style.URLSpan[]
    //   2154	6	19	localException2	Exception
    //   2182	6	19	localException3	Exception
    //   2195	6	19	localException4	Exception
    //   2208	6	19	localException5	Exception
    //   2224	6	19	localException6	Exception
    //   319	971	20	localMessageEntity	org.telegram.tgnet.TLRPC.MessageEntity
    //   978	249	21	str	String
    // Exception table:
    //   from	to	target	type
    //   429	441	444	java/lang/Throwable
    //   1436	1459	1936	java/lang/Exception
    //   2122	2149	2154	java/lang/Exception
    //   1989	2037	2169	java/lang/Exception
    //   2042	2055	2169	java/lang/Exception
    //   2055	2092	2169	java/lang/Exception
    //   1578	1599	2182	java/lang/Exception
    //   1605	1619	2195	java/lang/Exception
    //   1695	1707	2208	java/lang/Exception
    //   1728	1740	2224	java/lang/Exception
  }
  
  public static long getDialogId(TLRPC.Message paramMessage)
  {
    if ((paramMessage.dialog_id == 0L) && (paramMessage.to_id != null))
    {
      if (paramMessage.to_id.chat_id == 0) {
        break label71;
      }
      if (paramMessage.to_id.chat_id >= 0) {
        break label55;
      }
      paramMessage.dialog_id = AndroidUtilities.makeBroadcastId(paramMessage.to_id.chat_id);
    }
    for (;;)
    {
      return paramMessage.dialog_id;
      label55:
      paramMessage.dialog_id = (-paramMessage.to_id.chat_id);
      continue;
      label71:
      if (paramMessage.to_id.channel_id != 0) {
        paramMessage.dialog_id = (-paramMessage.to_id.channel_id);
      } else if (isOut(paramMessage)) {
        paramMessage.dialog_id = paramMessage.to_id.user_id;
      } else {
        paramMessage.dialog_id = paramMessage.from_id;
      }
    }
  }
  
  public static TLRPC.InputStickerSet getInputStickerSet(TLRPC.Message paramMessage)
  {
    TLRPC.DocumentAttribute localDocumentAttribute;
    if ((paramMessage.media != null) && (paramMessage.media.document != null))
    {
      paramMessage = paramMessage.media.document.attributes.iterator();
      while (paramMessage.hasNext())
      {
        localDocumentAttribute = (TLRPC.DocumentAttribute)paramMessage.next();
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker)) {
          if (!(localDocumentAttribute.stickerset instanceof TLRPC.TL_inputStickerSetEmpty)) {
            break label69;
          }
        }
      }
    }
    return null;
    label69:
    return localDocumentAttribute.stickerset;
  }
  
  public static TextPaint getTextPaint()
  {
    if (textPaint == null)
    {
      textPaint = new TextPaint(1);
      textPaint.setColor(-16777216);
      textPaint.linkColor = -14255946;
      textPaint.setTextSize(AndroidUtilities.dp(MessagesController.getInstance().fontSize));
    }
    return textPaint;
  }
  
  public static int getUnreadFlags(TLRPC.Message paramMessage)
  {
    int i = 0;
    if (!paramMessage.unread) {
      i = 0x0 | 0x1;
    }
    int j = i;
    if (!paramMessage.media_unread) {
      j = i | 0x2;
    }
    return j;
  }
  
  public static boolean isContentUnread(TLRPC.Message paramMessage)
  {
    return paramMessage.media_unread;
  }
  
  public static boolean isForwardedMessage(TLRPC.Message paramMessage)
  {
    return (paramMessage.flags & 0x4) != 0;
  }
  
  public static boolean isGifDocument(TLRPC.Document paramDocument)
  {
    return (paramDocument != null) && (paramDocument.thumb != null) && (paramDocument.mime_type != null) && ((paramDocument.mime_type.equals("image/gif")) || (isNewGifDocument(paramDocument)));
  }
  
  public static boolean isMediaEmpty(TLRPC.Message paramMessage)
  {
    return (paramMessage == null) || (paramMessage.media == null) || ((paramMessage.media instanceof TLRPC.TL_messageMediaEmpty)) || ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage));
  }
  
  public static boolean isMegagroup(TLRPC.Message paramMessage)
  {
    return (paramMessage.flags & 0x80000000) != 0;
  }
  
  public static boolean isMusicDocument(TLRPC.Document paramDocument)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    int i;
    if (paramDocument != null) {
      i = 0;
    }
    for (;;)
    {
      bool1 = bool2;
      if (i < paramDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio)) {
          break label58;
        }
        bool1 = bool2;
        if (!localDocumentAttribute.voice) {
          bool1 = true;
        }
      }
      return bool1;
      label58:
      i += 1;
    }
  }
  
  public static boolean isMusicMessage(TLRPC.Message paramMessage)
  {
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
      return isMusicDocument(paramMessage.media.webpage.document);
    }
    return (paramMessage.media != null) && (paramMessage.media.document != null) && (isMusicDocument(paramMessage.media.document));
  }
  
  public static boolean isNewGifDocument(TLRPC.Document paramDocument)
  {
    if ((paramDocument != null) && (paramDocument.mime_type != null) && (paramDocument.mime_type.equals("video/mp4")))
    {
      int i = 0;
      while (i < paramDocument.attributes.size())
      {
        if ((paramDocument.attributes.get(i) instanceof TLRPC.TL_documentAttributeAnimated)) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  public static boolean isOut(TLRPC.Message paramMessage)
  {
    return paramMessage.out;
  }
  
  public static boolean isStickerDocument(TLRPC.Document paramDocument)
  {
    if (paramDocument != null)
    {
      int i = 0;
      while (i < paramDocument.attributes.size())
      {
        if (((TLRPC.DocumentAttribute)paramDocument.attributes.get(i) instanceof TLRPC.TL_documentAttributeSticker)) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  public static boolean isStickerMessage(TLRPC.Message paramMessage)
  {
    return (paramMessage.media != null) && (paramMessage.media.document != null) && (isStickerDocument(paramMessage.media.document));
  }
  
  public static boolean isUnread(TLRPC.Message paramMessage)
  {
    return paramMessage.unread;
  }
  
  public static boolean isVideoDocument(TLRPC.Document paramDocument)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramDocument != null)
    {
      int k = 0;
      int i = 0;
      int j = 0;
      if (j < paramDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(j);
        int m;
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeVideo)) {
          m = 1;
        }
        for (;;)
        {
          j += 1;
          i = m;
          break;
          m = i;
          if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAnimated))
          {
            k = 1;
            m = i;
          }
        }
      }
      bool1 = bool2;
      if (i != 0)
      {
        bool1 = bool2;
        if (k == 0) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public static boolean isVideoMessage(TLRPC.Message paramMessage)
  {
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
      return isVideoDocument(paramMessage.media.webpage.document);
    }
    return (paramMessage.media != null) && (paramMessage.media.document != null) && (isVideoDocument(paramMessage.media.document));
  }
  
  public static boolean isVoiceDocument(TLRPC.Document paramDocument)
  {
    if (paramDocument != null)
    {
      int i = 0;
      while (i < paramDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio)) {
          return localDocumentAttribute.voice;
        }
        i += 1;
      }
    }
    return false;
  }
  
  public static boolean isVoiceMessage(TLRPC.Message paramMessage)
  {
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
      return isVoiceDocument(paramMessage.media.webpage.document);
    }
    return (paramMessage.media != null) && (paramMessage.media.document != null) && (isVoiceDocument(paramMessage.media.document));
  }
  
  private void measureInlineBotButtons()
  {
    this.wantedBotKeyboardWidth = 0;
    if (!(this.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup)) {}
    for (;;)
    {
      return;
      if (botButtonPaint == null)
      {
        botButtonPaint = new TextPaint(1);
        botButtonPaint.setTextSize(AndroidUtilities.dp(15.0F));
        botButtonPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      }
      int i = 0;
      while (i < this.messageOwner.reply_markup.rows.size())
      {
        TLRPC.TL_keyboardButtonRow localTL_keyboardButtonRow = (TLRPC.TL_keyboardButtonRow)this.messageOwner.reply_markup.rows.get(i);
        int k = 0;
        int n = localTL_keyboardButtonRow.buttons.size();
        int j = 0;
        while (j < n)
        {
          StaticLayout localStaticLayout = new StaticLayout(Emoji.replaceEmoji(((TLRPC.KeyboardButton)localTL_keyboardButtonRow.buttons.get(j)).text, botButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0F), false), botButtonPaint, AndroidUtilities.dp(2000.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
          int m = k;
          if (localStaticLayout.getLineCount() > 0) {
            m = Math.max(k, (int)Math.ceil(localStaticLayout.getLineWidth(0) - localStaticLayout.getLineLeft(0)) + AndroidUtilities.dp(4.0F));
          }
          j += 1;
          k = m;
        }
        this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, (AndroidUtilities.dp(12.0F) + k) * n + AndroidUtilities.dp(5.0F) * (n - 1));
        i += 1;
      }
    }
  }
  
  public static void setUnreadFlags(TLRPC.Message paramMessage, int paramInt)
  {
    boolean bool2 = true;
    if ((paramInt & 0x1) == 0)
    {
      bool1 = true;
      paramMessage.unread = bool1;
      if ((paramInt & 0x2) != 0) {
        break label34;
      }
    }
    label34:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      paramMessage.media_unread = bool1;
      return;
      bool1 = false;
      break;
    }
  }
  
  public boolean canDeleteMessage(TLRPC.Chat paramChat)
  {
    return true;
  }
  
  public boolean canEditMessage(TLRPC.Chat paramChat)
  {
    return canEditMessage(this.messageOwner, paramChat);
  }
  
  public void checkLayout()
  {
    if (!this.layoutCreated)
    {
      this.layoutCreated = true;
      TLRPC.User localUser = null;
      if (isFromUser()) {
        localUser = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
      }
      this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
      generateLayout(localUser);
    }
  }
  
  public void checkMediaExistance()
  {
    this.attachPathExists = false;
    this.mediaExists = false;
    if (this.type == 1) {
      if (FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize()) != null) {
        this.mediaExists = FileLoader.getPathToMessage(this.messageOwner).exists();
      }
    }
    Object localObject;
    do
    {
      do
      {
        do
        {
          return;
          if ((this.type != 8) && (this.type != 3) && (this.type != 9) && (this.type != 2) && (this.type != 14)) {
            break;
          }
          if ((this.messageOwner.attachPath != null) && (this.messageOwner.attachPath.length() > 0)) {
            this.attachPathExists = new File(this.messageOwner.attachPath).exists();
          }
        } while (this.attachPathExists);
        this.mediaExists = FileLoader.getPathToMessage(this.messageOwner).exists();
        return;
        localObject = getDocument();
        if (localObject != null)
        {
          this.mediaExists = FileLoader.getPathToAttach((TLObject)localObject).exists();
          return;
        }
      } while (this.type != 0);
      localObject = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
    } while ((localObject == null) || (localObject == null));
    this.mediaExists = FileLoader.getPathToAttach((TLObject)localObject, true).exists();
  }
  
  public void generateCaption()
  {
    if (this.caption != null) {}
    do
    {
      do
      {
        return;
      } while ((this.messageOwner.media == null) || (this.messageOwner.media.caption == null) || (this.messageOwner.media.caption.length() <= 0));
      this.caption = Emoji.replaceEmoji(this.messageOwner.media.caption, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
    } while (!containsUrls(this.caption));
    try
    {
      Linkify.addLinks((Spannable)this.caption, 5);
      addUsernamesAndHashtags(this.caption, true);
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
  
  public void generateLinkDescription()
  {
    if (this.linkDescription != null) {}
    while ((!(this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) || (!(this.messageOwner.media.webpage instanceof TLRPC.TL_webPage)) || (this.messageOwner.media.webpage.description == null)) {
      return;
    }
    this.linkDescription = Spannable.Factory.getInstance().newSpannable(this.messageOwner.media.webpage.description);
    if (containsUrls(this.linkDescription)) {}
    try
    {
      Linkify.addLinks((Spannable)this.linkDescription, 1);
      this.linkDescription = Emoji.replaceEmoji(this.linkDescription, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
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
  
  public void generatePinMessageText(TLRPC.User paramUser, TLRPC.Chat paramChat)
  {
    Object localObject = paramUser;
    TLRPC.Chat localChat = paramChat;
    if (paramUser == null)
    {
      localObject = paramUser;
      localChat = paramChat;
      if (paramChat == null)
      {
        if (this.messageOwner.from_id > 0) {
          paramUser = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
        }
        localObject = paramUser;
        localChat = paramChat;
        if (paramUser == null)
        {
          localChat = MessagesController.getInstance().getChat(Integer.valueOf(this.messageOwner.to_id.channel_id));
          localObject = paramUser;
        }
      }
    }
    if (this.replyMessageObject == null)
    {
      paramUser = LocaleController.getString("ActionPinnedNoText", 2131165276);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isMusic())
    {
      paramUser = LocaleController.getString("ActionPinnedMusic", 2131165275);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isVideo())
    {
      paramUser = LocaleController.getString("ActionPinnedVideo", 2131165280);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isGif())
    {
      paramUser = LocaleController.getString("ActionPinnedGif", 2131165274);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isVoice())
    {
      paramUser = LocaleController.getString("ActionPinnedVoice", 2131165281);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isSticker())
    {
      paramUser = LocaleController.getString("ActionPinnedSticker", 2131165278);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
    {
      paramUser = LocaleController.getString("ActionPinnedFile", 2131165272);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo))
    {
      paramUser = LocaleController.getString("ActionPinnedGeo", 2131165273);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
    {
      paramUser = LocaleController.getString("ActionPinnedContact", 2131165271);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
    {
      paramUser = LocaleController.getString("ActionPinnedPhoto", 2131165277);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageText != null) && (this.replyMessageObject.messageText.length() > 0))
    {
      paramChat = this.replyMessageObject.messageText;
      paramUser = paramChat;
      if (paramChat.length() > 20) {
        paramUser = paramChat.subSequence(0, 20) + "...";
      }
      paramUser = LocaleController.formatString("ActionPinnedText", 2131165279, new Object[] { Emoji.replaceEmoji(paramUser, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false) });
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    paramUser = LocaleController.getString("ActionPinnedNoText", 2131165276);
    if (localObject != null) {}
    for (;;)
    {
      this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
      return;
      localObject = localChat;
    }
  }
  
  public void generateThumbs(boolean paramBoolean)
  {
    if ((this.messageOwner instanceof TLRPC.TL_messageService)) {
      if ((this.messageOwner.action instanceof TLRPC.TL_messageActionChatEditPhoto))
      {
        if (paramBoolean) {
          break label52;
        }
        this.photoThumbs = new ArrayList(this.messageOwner.action.photo.sizes);
      }
    }
    label51:
    label52:
    label801:
    do
    {
      do
      {
        break label51;
        break label51;
        break label51;
        break label51;
        break label51;
        break label51;
        for (;;)
        {
          return;
          if ((this.photoThumbs != null) && (!this.photoThumbs.isEmpty()))
          {
            int i = 0;
            TLRPC.PhotoSize localPhotoSize1;
            int j;
            TLRPC.PhotoSize localPhotoSize2;
            while (i < this.photoThumbs.size())
            {
              localPhotoSize1 = (TLRPC.PhotoSize)this.photoThumbs.get(i);
              j = 0;
              if (j < this.messageOwner.action.photo.sizes.size())
              {
                localPhotoSize2 = (TLRPC.PhotoSize)this.messageOwner.action.photo.sizes.get(j);
                if ((localPhotoSize2 instanceof TLRPC.TL_photoSizeEmpty)) {}
                while (!localPhotoSize2.type.equals(localPhotoSize1.type))
                {
                  j += 1;
                  break;
                }
                localPhotoSize1.location = localPhotoSize2.location;
              }
              i += 1;
            }
            continue;
            if ((this.messageOwner.media == null) || ((this.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty))) {
              break;
            }
            if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
            {
              if ((!paramBoolean) || ((this.photoThumbs != null) && (this.photoThumbs.size() != this.messageOwner.media.photo.sizes.size())))
              {
                this.photoThumbs = new ArrayList(this.messageOwner.media.photo.sizes);
                return;
              }
              if ((this.photoThumbs == null) || (this.photoThumbs.isEmpty())) {
                break;
              }
              i = 0;
              while (i < this.photoThumbs.size())
              {
                localPhotoSize1 = (TLRPC.PhotoSize)this.photoThumbs.get(i);
                j = 0;
                if (j < this.messageOwner.media.photo.sizes.size())
                {
                  localPhotoSize2 = (TLRPC.PhotoSize)this.messageOwner.media.photo.sizes.get(j);
                  if ((localPhotoSize2 instanceof TLRPC.TL_photoSizeEmpty)) {}
                  while (!localPhotoSize2.type.equals(localPhotoSize1.type))
                  {
                    j += 1;
                    break;
                  }
                  localPhotoSize1.location = localPhotoSize2.location;
                }
                i += 1;
              }
              continue;
            }
            if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
            {
              if ((this.messageOwner.media.document.thumb instanceof TLRPC.TL_photoSizeEmpty)) {
                break;
              }
              if (!paramBoolean)
              {
                this.photoThumbs = new ArrayList();
                this.photoThumbs.add(this.messageOwner.media.document.thumb);
                return;
              }
              if ((this.photoThumbs == null) || (this.photoThumbs.isEmpty()) || (this.messageOwner.media.document.thumb == null)) {
                break;
              }
              localPhotoSize1 = (TLRPC.PhotoSize)this.photoThumbs.get(0);
              localPhotoSize1.location = this.messageOwner.media.document.thumb.location;
              localPhotoSize1.w = this.messageOwner.media.document.thumb.w;
              localPhotoSize1.h = this.messageOwner.media.document.thumb.h;
              return;
            }
            if (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) {
              break;
            }
            if (this.messageOwner.media.webpage.photo == null) {
              break label801;
            }
            if ((!paramBoolean) || (this.photoThumbs == null))
            {
              this.photoThumbs = new ArrayList(this.messageOwner.media.webpage.photo.sizes);
              return;
            }
            if (this.photoThumbs.isEmpty()) {
              break;
            }
            i = 0;
            while (i < this.photoThumbs.size())
            {
              localPhotoSize1 = (TLRPC.PhotoSize)this.photoThumbs.get(i);
              j = 0;
              if (j < this.messageOwner.media.webpage.photo.sizes.size())
              {
                localPhotoSize2 = (TLRPC.PhotoSize)this.messageOwner.media.webpage.photo.sizes.get(j);
                if ((localPhotoSize2 instanceof TLRPC.TL_photoSizeEmpty)) {}
                while (!localPhotoSize2.type.equals(localPhotoSize1.type))
                {
                  j += 1;
                  break;
                }
                localPhotoSize1.location = localPhotoSize2.location;
              }
              i += 1;
            }
          }
        }
      } while ((this.messageOwner.media.webpage.document == null) || ((this.messageOwner.media.webpage.document.thumb instanceof TLRPC.TL_photoSizeEmpty)));
      if (!paramBoolean)
      {
        this.photoThumbs = new ArrayList();
        this.photoThumbs.add(this.messageOwner.media.webpage.document.thumb);
        return;
      }
    } while ((this.photoThumbs == null) || (this.photoThumbs.isEmpty()) || (this.messageOwner.media.webpage.document.thumb == null));
    ((TLRPC.PhotoSize)this.photoThumbs.get(0)).location = this.messageOwner.media.webpage.document.thumb.location;
  }
  
  public int getApproximateHeight()
  {
    int j;
    if (this.type == 0)
    {
      j = this.textHeight;
      if (((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && ((this.messageOwner.media.webpage instanceof TLRPC.TL_webPage))) {}
      for (i = AndroidUtilities.dp(100.0F);; i = 0)
      {
        j += i;
        i = j;
        if (isReply()) {
          i = j + AndroidUtilities.dp(42.0F);
        }
        return i;
      }
    }
    if (this.type == 2) {
      return AndroidUtilities.dp(72.0F);
    }
    if (this.type == 12) {
      return AndroidUtilities.dp(71.0F);
    }
    if (this.type == 9) {
      return AndroidUtilities.dp(100.0F);
    }
    if (this.type == 4) {
      return AndroidUtilities.dp(114.0F);
    }
    if (this.type == 14) {
      return AndroidUtilities.dp(82.0F);
    }
    if (this.type == 10) {
      return AndroidUtilities.dp(30.0F);
    }
    if (this.type == 11) {
      return AndroidUtilities.dp(50.0F);
    }
    float f1;
    Object localObject;
    int k;
    if (this.type == 13)
    {
      float f2 = AndroidUtilities.displaySize.y * 0.4F;
      if (AndroidUtilities.isTablet()) {}
      for (f1 = AndroidUtilities.getMinTabletSide() * 0.5F;; f1 = AndroidUtilities.displaySize.x * 0.5F)
      {
        j = 0;
        int m = 0;
        localObject = this.messageOwner.media.document.attributes.iterator();
        TLRPC.DocumentAttribute localDocumentAttribute;
        do
        {
          k = j;
          i = m;
          if (!((Iterator)localObject).hasNext()) {
            break;
          }
          localDocumentAttribute = (TLRPC.DocumentAttribute)((Iterator)localObject).next();
        } while (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeImageSize));
        i = localDocumentAttribute.w;
        k = localDocumentAttribute.h;
        j = i;
        if (i == 0)
        {
          k = (int)f2;
          j = k + AndroidUtilities.dp(100.0F);
        }
        i = k;
        m = j;
        if (k > f2)
        {
          m = (int)(j * (f2 / k));
          i = (int)f2;
        }
        j = i;
        if (m > f1) {
          j = (int)(i * (f1 / m));
        }
        return j + AndroidUtilities.dp(14.0F);
      }
    }
    if (AndroidUtilities.isTablet())
    {
      i = (int)(AndroidUtilities.getMinTabletSide() * 0.7F);
      k = i + AndroidUtilities.dp(100.0F);
      j = i;
      if (i > AndroidUtilities.getPhotoSize()) {
        j = AndroidUtilities.getPhotoSize();
      }
      i = k;
      if (k > AndroidUtilities.getPhotoSize()) {
        i = AndroidUtilities.getPhotoSize();
      }
      localObject = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
      k = i;
      if (localObject != null)
      {
        f1 = ((TLRPC.PhotoSize)localObject).w / j;
        k = (int)(((TLRPC.PhotoSize)localObject).h / f1);
        j = k;
        if (k == 0) {
          j = AndroidUtilities.dp(100.0F);
        }
        if (j <= i) {
          break label583;
        }
        label522:
        if (isSecretPhoto()) {
          if (!AndroidUtilities.isTablet()) {
            break label607;
          }
        }
      }
    }
    label583:
    label607:
    for (int i = (int)(AndroidUtilities.getMinTabletSide() * 0.5F);; i = (int)(Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.5F))
    {
      k = i;
      return k + AndroidUtilities.dp(14.0F);
      i = (int)(Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.7F);
      break;
      i = j;
      if (j >= AndroidUtilities.dp(120.0F)) {
        break label522;
      }
      i = AndroidUtilities.dp(120.0F);
      break label522;
    }
  }
  
  public long getDialogId()
  {
    return getDialogId(this.messageOwner);
  }
  
  public TLRPC.Document getDocument()
  {
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) {
      return this.messageOwner.media.webpage.document;
    }
    if (this.messageOwner.media != null) {
      return this.messageOwner.media.document;
    }
    return null;
  }
  
  public String getDocumentName()
  {
    if ((this.messageOwner.media != null) && (this.messageOwner.media.document != null)) {
      return FileLoader.getDocumentFileName(this.messageOwner.media.document);
    }
    return "";
  }
  
  public int getDuration()
  {
    TLRPC.Document localDocument;
    int i;
    if (this.type == 0)
    {
      localDocument = this.messageOwner.media.webpage.document;
      i = 0;
    }
    for (;;)
    {
      if (i >= localDocument.attributes.size()) {
        break label79;
      }
      TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)localDocument.attributes.get(i);
      if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio))
      {
        return localDocumentAttribute.duration;
        localDocument = this.messageOwner.media.document;
        break;
      }
      i += 1;
    }
    label79:
    return 0;
  }
  
  public String getExtension()
  {
    Object localObject2 = getFileName();
    int i = ((String)localObject2).lastIndexOf('.');
    Object localObject1 = null;
    if (i != -1) {
      localObject1 = ((String)localObject2).substring(i + 1);
    }
    if (localObject1 != null)
    {
      localObject2 = localObject1;
      if (((String)localObject1).length() != 0) {}
    }
    else
    {
      localObject2 = this.messageOwner.media.document.mime_type;
    }
    localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = "";
    }
    return ((String)localObject1).toUpperCase();
  }
  
  public String getFileName()
  {
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) {
      return FileLoader.getAttachFileName(this.messageOwner.media.document);
    }
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
    {
      Object localObject = this.messageOwner.media.photo.sizes;
      if (((ArrayList)localObject).size() > 0)
      {
        localObject = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject, AndroidUtilities.getPhotoSize());
        if (localObject != null) {
          return FileLoader.getAttachFileName((TLObject)localObject);
        }
      }
    }
    else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage))
    {
      return FileLoader.getAttachFileName(this.messageOwner.media.webpage.document);
    }
    return "";
  }
  
  public int getFileType()
  {
    if (isVideo()) {
      return 2;
    }
    if (isVoice()) {
      return 1;
    }
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) {
      return 3;
    }
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) {
      return 0;
    }
    return 4;
  }
  
  public String getForwardedName()
  {
    if (this.messageOwner.fwd_from != null)
    {
      Object localObject;
      if (this.messageOwner.fwd_from.channel_id != 0)
      {
        localObject = MessagesController.getInstance().getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
        if (localObject != null) {
          return ((TLRPC.Chat)localObject).title;
        }
      }
      else if (this.messageOwner.fwd_from.from_id != 0)
      {
        localObject = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
        if (localObject != null) {
          return UserObject.getUserName((TLRPC.User)localObject);
        }
      }
    }
    return null;
  }
  
  public int getId()
  {
    return this.messageOwner.id;
  }
  
  public TLRPC.InputStickerSet getInputStickerSet()
  {
    return getInputStickerSet(this.messageOwner);
  }
  
  public String getMusicAuthor()
  {
    Object localObject1;
    int i;
    if (this.type == 0)
    {
      localObject1 = this.messageOwner.media.webpage.document;
      i = 0;
    }
    for (;;)
    {
      if (i >= ((TLRPC.Document)localObject1).attributes.size()) {
        break label320;
      }
      TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)((TLRPC.Document)localObject1).attributes.get(i);
      if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio))
      {
        if (localDocumentAttribute.voice) {
          if ((isOutOwner()) || ((this.messageOwner.fwd_from != null) && (this.messageOwner.fwd_from.from_id == UserConfig.getClientUserId()))) {
            localObject1 = LocaleController.getString("FromYou", 2131165771);
          }
        }
        Object localObject2;
        do
        {
          return (String)localObject1;
          localObject1 = this.messageOwner.media.document;
          break;
          localObject2 = null;
          localObject1 = null;
          if ((this.messageOwner.fwd_from != null) && (this.messageOwner.fwd_from.channel_id != 0)) {
            localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
          }
          while (localObject2 != null)
          {
            return UserObject.getUserName((TLRPC.User)localObject2);
            if ((this.messageOwner.fwd_from != null) && (this.messageOwner.fwd_from.from_id != 0)) {
              localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
            } else if (this.messageOwner.from_id < 0) {
              localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(-this.messageOwner.from_id));
            } else {
              localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
            }
          }
          if (localObject1 != null) {
            return ((TLRPC.Chat)localObject1).title;
          }
          localObject2 = localDocumentAttribute.performer;
          if (localObject2 == null) {
            break label303;
          }
          localObject1 = localObject2;
        } while (((String)localObject2).length() != 0);
        label303:
        return LocaleController.getString("AudioUnknownArtist", 2131165379);
      }
      i += 1;
    }
    label320:
    return "";
  }
  
  public String getMusicTitle()
  {
    Object localObject2;
    int i;
    if (this.type == 0)
    {
      localObject2 = this.messageOwner.media.webpage.document;
      i = 0;
    }
    for (;;)
    {
      if (i >= ((TLRPC.Document)localObject2).attributes.size()) {
        break label145;
      }
      Object localObject1 = (TLRPC.DocumentAttribute)((TLRPC.Document)localObject2).attributes.get(i);
      if ((localObject1 instanceof TLRPC.TL_documentAttributeAudio))
      {
        if (((TLRPC.DocumentAttribute)localObject1).voice) {
          localObject1 = LocaleController.formatDateAudio(this.messageOwner.date);
        }
        label110:
        do
        {
          String str;
          do
          {
            return (String)localObject1;
            localObject2 = this.messageOwner.media.document;
            break;
            str = ((TLRPC.DocumentAttribute)localObject1).title;
            if (str == null) {
              break label110;
            }
            localObject1 = str;
          } while (str.length() != 0);
          localObject2 = FileLoader.getDocumentFileName((TLRPC.Document)localObject2);
          if (localObject2 == null) {
            break label128;
          }
          localObject1 = localObject2;
        } while (((String)localObject2).length() != 0);
        label128:
        return LocaleController.getString("AudioUnknownTitle", 2131165380);
      }
      i += 1;
    }
    label145:
    return "";
  }
  
  public String getSecretTimeString()
  {
    if (!isSecretMedia()) {
      return null;
    }
    int i = this.messageOwner.ttl;
    if (this.messageOwner.destroyTime != 0) {
      i = Math.max(0, this.messageOwner.destroyTime - ConnectionsManager.getInstance().getCurrentTime());
    }
    if (i < 60) {
      return i + "s";
    }
    return i / 60 + "m";
  }
  
  public String getStickerEmoji()
  {
    Object localObject2 = null;
    int i = 0;
    for (;;)
    {
      Object localObject1 = localObject2;
      if (i < this.messageOwner.media.document.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)this.messageOwner.media.document.attributes.get(i);
        if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker)) {
          break label87;
        }
        localObject1 = localObject2;
        if (localDocumentAttribute.alt != null)
        {
          localObject1 = localObject2;
          if (localDocumentAttribute.alt.length() > 0) {
            localObject1 = localDocumentAttribute.alt;
          }
        }
      }
      return (String)localObject1;
      label87:
      i += 1;
    }
  }
  
  public String getStrickerChar()
  {
    if ((this.messageOwner.media != null) && (this.messageOwner.media.document != null))
    {
      Iterator localIterator = this.messageOwner.media.document.attributes.iterator();
      while (localIterator.hasNext())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)localIterator.next();
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker)) {
          return localDocumentAttribute.alt;
        }
      }
    }
    return null;
  }
  
  public int getUnradFlags()
  {
    return getUnreadFlags(this.messageOwner);
  }
  
  public boolean isContentUnread()
  {
    return this.messageOwner.media_unread;
  }
  
  public boolean isForwarded()
  {
    return isForwardedMessage(this.messageOwner);
  }
  
  public boolean isFromUser()
  {
    return (this.messageOwner.from_id > 0) && (!this.messageOwner.post);
  }
  
  public boolean isGif()
  {
    return ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) && (isGifDocument(this.messageOwner.media.document));
  }
  
  public boolean isMediaEmpty()
  {
    return isMediaEmpty(this.messageOwner);
  }
  
  public boolean isMegagroup()
  {
    return isMegagroup(this.messageOwner);
  }
  
  public boolean isMusic()
  {
    return isMusicMessage(this.messageOwner);
  }
  
  public boolean isNewGif()
  {
    return (this.messageOwner.media != null) && (isNewGifDocument(this.messageOwner.media.document));
  }
  
  public boolean isOut()
  {
    return this.messageOwner.out;
  }
  
  public boolean isOutOwner()
  {
    return (this.messageOwner.out) && (this.messageOwner.from_id > 0) && (!this.messageOwner.post);
  }
  
  public boolean isReply()
  {
    return ((this.replyMessageObject == null) || (!(this.replyMessageObject.messageOwner instanceof TLRPC.TL_messageEmpty))) && ((this.messageOwner.reply_to_msg_id != 0) || (this.messageOwner.reply_to_random_id != 0L)) && ((this.messageOwner.flags & 0x8) != 0);
  }
  
  public boolean isSecretMedia()
  {
    return ((this.messageOwner instanceof TLRPC.TL_message_secret)) && ((((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) && (this.messageOwner.ttl > 0) && (this.messageOwner.ttl <= 60)) || (isVoice()) || (isVideo()));
  }
  
  public boolean isSecretPhoto()
  {
    return ((this.messageOwner instanceof TLRPC.TL_message_secret)) && ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) && (this.messageOwner.ttl > 0) && (this.messageOwner.ttl <= 60);
  }
  
  public boolean isSendError()
  {
    return (this.messageOwner.send_state == 2) && (this.messageOwner.id < 0);
  }
  
  public boolean isSending()
  {
    return (this.messageOwner.send_state == 1) && (this.messageOwner.id < 0);
  }
  
  public boolean isSent()
  {
    return (this.messageOwner.send_state == 0) || (this.messageOwner.id > 0);
  }
  
  public boolean isSticker()
  {
    if (this.type != 1000) {
      return this.type == 13;
    }
    return isStickerMessage(this.messageOwner);
  }
  
  public boolean isUnread()
  {
    return this.messageOwner.unread;
  }
  
  public boolean isVideo()
  {
    return isVideoMessage(this.messageOwner);
  }
  
  public boolean isVoice()
  {
    return isVoiceMessage(this.messageOwner);
  }
  
  public boolean isWebpageDocument()
  {
    return ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && (this.messageOwner.media.webpage.document != null) && (!isGifDocument(this.messageOwner.media.webpage.document));
  }
  
  public CharSequence replaceWithLink(CharSequence paramCharSequence, String paramString, ArrayList<Integer> paramArrayList, AbstractMap<Integer, TLRPC.User> paramAbstractMap)
  {
    Object localObject1 = paramCharSequence;
    if (TextUtils.indexOf(paramCharSequence, paramString) >= 0)
    {
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder("");
      int i = 0;
      while (i < paramArrayList.size())
      {
        localObject1 = null;
        if (paramAbstractMap != null) {
          localObject1 = (TLRPC.User)paramAbstractMap.get(paramArrayList.get(i));
        }
        Object localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = MessagesController.getInstance().getUser((Integer)paramArrayList.get(i));
        }
        if (localObject2 != null)
        {
          localObject1 = UserObject.getUserName((TLRPC.User)localObject2);
          int j = localSpannableStringBuilder.length();
          if (localSpannableStringBuilder.length() != 0) {
            localSpannableStringBuilder.append(", ");
          }
          localSpannableStringBuilder.append((CharSequence)localObject1);
          localSpannableStringBuilder.setSpan(new URLSpanNoUnderlineBold("" + ((TLRPC.User)localObject2).id), j, ((String)localObject1).length() + j, 33);
        }
        i += 1;
      }
      localObject1 = TextUtils.replace(paramCharSequence, new String[] { paramString }, new CharSequence[] { localSpannableStringBuilder });
    }
    return (CharSequence)localObject1;
  }
  
  public CharSequence replaceWithLink(CharSequence paramCharSequence, String paramString, TLObject paramTLObject)
  {
    int j = TextUtils.indexOf(paramCharSequence, paramString);
    if (j >= 0)
    {
      String str;
      int i;
      if ((paramTLObject instanceof TLRPC.User))
      {
        str = UserObject.getUserName((TLRPC.User)paramTLObject);
        i = ((TLRPC.User)paramTLObject).id;
        paramTLObject = str;
      }
      for (;;)
      {
        paramCharSequence = new SpannableStringBuilder(TextUtils.replace(paramCharSequence, new String[] { paramString }, new String[] { paramTLObject }));
        paramCharSequence.setSpan(new URLSpanNoUnderlineBold("" + i), j, paramTLObject.length() + j, 33);
        return paramCharSequence;
        if ((paramTLObject instanceof TLRPC.Chat))
        {
          str = ((TLRPC.Chat)paramTLObject).title;
          i = -((TLRPC.Chat)paramTLObject).id;
          paramTLObject = str;
        }
        else
        {
          paramTLObject = "";
          i = 0;
        }
      }
    }
    return paramCharSequence;
  }
  
  public void setContentIsRead()
  {
    this.messageOwner.media_unread = false;
  }
  
  public void setIsRead()
  {
    this.messageOwner.unread = false;
  }
  
  public void setType()
  {
    int i = this.type;
    if (((this.messageOwner instanceof TLRPC.TL_message)) || ((this.messageOwner instanceof TLRPC.TL_messageForwarded_old2))) {
      if (isMediaEmpty())
      {
        this.type = 0;
        if ((this.messageText == null) || (this.messageText.length() == 0)) {
          this.messageText = "Empty message";
        }
      }
    }
    for (;;)
    {
      if ((i != 1000) && (i != this.type)) {
        generateThumbs(false);
      }
      return;
      if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) {
        this.type = 1;
      } else if (((this.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((this.messageOwner.media instanceof TLRPC.TL_messageMediaVenue))) {
        this.type = 4;
      } else if (isVideo()) {
        this.type = 3;
      } else if (isVoice()) {
        this.type = 2;
      } else if (isMusic()) {
        this.type = 14;
      } else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaContact)) {
        this.type = 12;
      } else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaUnsupported)) {
        this.type = 0;
      } else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) {
        if (this.messageOwner.media.document.mime_type != null)
        {
          if (isGifDocument(this.messageOwner.media.document)) {
            this.type = 8;
          } else if ((this.messageOwner.media.document.mime_type.equals("image/webp")) && (isSticker())) {
            this.type = 13;
          } else {
            this.type = 9;
          }
        }
        else
        {
          this.type = 9;
          continue;
          if ((this.messageOwner instanceof TLRPC.TL_messageService)) {
            if ((this.messageOwner.action instanceof TLRPC.TL_messageActionLoginUnknownLocation))
            {
              this.type = 0;
            }
            else if (((this.messageOwner.action instanceof TLRPC.TL_messageActionChatEditPhoto)) || ((this.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto)))
            {
              this.contentType = 1;
              this.type = 11;
            }
            else if ((this.messageOwner.action instanceof TLRPC.TL_messageEncryptedAction))
            {
              if (((this.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)) || ((this.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)))
              {
                this.contentType = 1;
                this.type = 10;
              }
              else
              {
                this.contentType = -1;
                this.type = -1;
              }
            }
            else if ((this.messageOwner.action instanceof TLRPC.TL_messageActionHistoryClear))
            {
              this.contentType = -1;
              this.type = -1;
            }
            else
            {
              this.contentType = 1;
              this.type = 10;
            }
          }
        }
      }
    }
  }
  
  public static class TextLayoutBlock
  {
    public int charactersOffset;
    public int height;
    public StaticLayout textLayout;
    public float textXOffset;
    public float textYOffset;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\MessageObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */