package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.view.ViewParent;
import android.view.ViewStructure;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.FileDownloadProgressListener;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.TextLayoutBlock;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRequestGeoLocation;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonUrl;
import org.telegram.tgnet.TLRPC.TL_messageFwdHeader;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_userEmpty;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.RadialProgress;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.ui.Components.SeekBarWaveform;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Supergram.Theming.MihanTheme;
import org.telegram.ui.PhotoViewer;

public class ChatMessageCell
  extends BaseCell
  implements SeekBar.SeekBarDelegate, ImageReceiver.ImageReceiverDelegate, MediaController.FileDownloadProgressListener
{
  private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
  private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
  private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
  private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
  private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
  private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
  private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
  private static TextPaint audioPerformerPaint;
  private static TextPaint audioTimePaint;
  private static TextPaint audioTitlePaint;
  private static TextPaint botButtonPaint;
  private static Paint botProgressPaint;
  private static TextPaint contactNamePaint;
  private static TextPaint contactPhonePaint;
  private static Paint deleteProgressPaint;
  private static Paint docBackPaint;
  private static TextPaint docNamePaint;
  private static TextPaint durationPaint;
  private static TextPaint forwardNamePaint;
  private static TextPaint infoPaint;
  private static TextPaint locationAddressPaint;
  private static TextPaint locationTitlePaint;
  private static TextPaint namePaint;
  private static Paint replyLinePaint;
  private static TextPaint replyNamePaint;
  private static TextPaint replyTextPaint;
  private static Drawable statusDrawable;
  private static TextPaint timePaint;
  private static Paint urlPaint;
  private static Paint urlSelectionPaint;
  private int TAG;
  private boolean allowAssistant;
  private StaticLayout authorLayout;
  private int authorX;
  private int availableTimeWidth;
  private AvatarDrawable avatarDrawable;
  private ImageReceiver avatarImage;
  private boolean avatarPressed;
  private int backgroundWidth = 100;
  private ArrayList<BotButton> botButtons = new ArrayList();
  private HashMap<String, BotButton> botButtonsByData = new HashMap();
  private int buttonPressed;
  private int buttonState;
  private int buttonX;
  private int buttonY;
  private boolean cancelLoading;
  private int captionHeight;
  private StaticLayout captionLayout;
  private int captionX;
  private int captionY;
  private AvatarDrawable contactAvatarDrawable;
  private Drawable currentBackgroundDrawable;
  private TLRPC.Chat currentChat;
  private TLRPC.Chat currentForwardChannel;
  private String currentForwardNameString;
  private TLRPC.User currentForwardUser;
  private MessageObject currentMessageObject;
  private String currentNameString;
  private TLRPC.FileLocation currentPhoto;
  private String currentPhotoFilter;
  private String currentPhotoFilterThumb;
  private TLRPC.PhotoSize currentPhotoObject;
  private TLRPC.PhotoSize currentPhotoObjectThumb;
  private TLRPC.FileLocation currentReplyPhoto;
  private String currentTimeString;
  private String currentUrl;
  private TLRPC.User currentUser;
  private TLRPC.User currentViaBotUser;
  private String currentViewsString;
  private ChatMessageCellDelegate delegate;
  private RectF deleteProgressRect = new RectF();
  private StaticLayout descriptionLayout;
  private int descriptionX;
  private int descriptionY;
  private boolean directReplyPressed;
  private boolean disallowLongPress;
  private StaticLayout docTitleLayout;
  private int docTitleOffsetX;
  private TLRPC.Document documentAttach;
  private int documentAttachType;
  private boolean drawBackground = true;
  private boolean drawDirectReply;
  private boolean drawForwardedName;
  private boolean drawImageButton;
  private boolean drawName;
  private boolean drawNameLayout;
  private boolean drawPhotoImage;
  private boolean drawShareButton;
  private boolean drawTime = true;
  private StaticLayout durationLayout;
  private int durationWidth;
  private int firstVisibleBlockNum;
  private boolean forwardBotPressed;
  private boolean forwardName;
  private float[] forwardNameOffsetX = new float[2];
  private boolean forwardNamePressed;
  private int forwardNameX;
  private int forwardNameY;
  private StaticLayout[] forwardedNameLayout = new StaticLayout[2];
  private int forwardedNameWidth;
  private boolean hasLinkPreview;
  private boolean imagePressed;
  private StaticLayout infoLayout;
  private int infoWidth;
  private boolean isAvatarVisible;
  public boolean isChat;
  private boolean isCheckPressed = true;
  private boolean isHighlighted;
  private boolean isPressed;
  private boolean isSmallImage;
  private int keyboardHeight;
  private int lastDeleteDate;
  private int lastSendState;
  private String lastTimeString;
  private int lastViewsCount;
  private int lastVisibleBlockNum;
  private int layoutHeight;
  private int layoutWidth;
  private int linkBlockNum;
  private int linkPreviewHeight;
  private boolean linkPreviewPressed;
  private int linkSelectionBlockNum;
  private boolean mediaBackground;
  private int mediaOffsetY;
  private StaticLayout nameLayout;
  private float nameOffsetX;
  private int nameWidth;
  private float nameX;
  private float nameY;
  private int namesOffset;
  private boolean needNewVisiblePart;
  private boolean needReplyImage;
  private boolean otherPressed;
  private int otherX;
  private int otherY;
  private StaticLayout performerLayout;
  private int performerX;
  private ImageReceiver photoImage;
  private boolean photoNotSet;
  private int pressedBotButton;
  private ClickableSpan pressedLink;
  private int pressedLinkType;
  private RadialProgress radialProgress;
  private RectF rect = new RectF();
  private ImageReceiver replyImageReceiver;
  private StaticLayout replyNameLayout;
  private float replyNameOffset;
  private int replyNameWidth;
  private boolean replyPressed;
  private int replyStartX;
  private int replyStartY;
  private StaticLayout replyTextLayout;
  private float replyTextOffset;
  private int replyTextWidth;
  private Rect scrollRect = new Rect();
  private SeekBar seekBar;
  private SeekBarWaveform seekBarWaveform;
  private int seekBarX;
  private int seekBarY;
  private boolean sharePressed;
  private int shareStartX;
  private int shareStartY;
  private StaticLayout siteNameLayout;
  private StaticLayout songLayout;
  private int songX;
  private int substractBackgroundHeight;
  private int textX;
  private int textY;
  private int timeAudioX;
  private StaticLayout timeLayout;
  private int timeTextWidth;
  private int timeWidth;
  private int timeWidthAudio;
  private int timeX;
  private StaticLayout titleLayout;
  private int titleX;
  private int totalHeight;
  private int totalVisibleBlocksCount;
  private ArrayList<LinkPath> urlPath = new ArrayList();
  private ArrayList<LinkPath> urlPathCache = new ArrayList();
  private ArrayList<LinkPath> urlPathSelection = new ArrayList();
  private boolean useSeekBarWaweform;
  private int viaNameWidth;
  private int viaWidth;
  private StaticLayout videoInfoLayout;
  private StaticLayout viewsLayout;
  private int viewsTextWidth;
  private boolean wasLayout;
  private int widthForButtons;
  
  public ChatMessageCell(Context paramContext)
  {
    super(paramContext);
    if (infoPaint == null)
    {
      infoPaint = new TextPaint(1);
      infoPaint.setTextSize(AndroidUtilities.dp(12.0F));
      infoPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      docNamePaint = new TextPaint(1);
      docNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      docNamePaint.setTextSize(AndroidUtilities.dp(15.0F));
      docBackPaint = new Paint(1);
      deleteProgressPaint = new Paint(1);
      deleteProgressPaint.setColor(-1776928);
      botProgressPaint = new Paint(1);
      botProgressPaint.setColor(-1);
      botProgressPaint.setStrokeCap(Paint.Cap.ROUND);
      botProgressPaint.setStyle(Paint.Style.STROKE);
      botProgressPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
      locationTitlePaint = new TextPaint(1);
      locationTitlePaint.setTextSize(AndroidUtilities.dp(15.0F));
      locationTitlePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      locationAddressPaint = new TextPaint(1);
      locationAddressPaint.setTextSize(AndroidUtilities.dp(13.0F));
      urlPaint = new Paint();
      urlPaint.setColor(862104035);
      urlSelectionPaint = new Paint();
      urlSelectionPaint.setColor(1717742051);
      audioTimePaint = new TextPaint(1);
      audioTimePaint.setTextSize(AndroidUtilities.dp(12.0F));
      audioTimePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      audioTitlePaint = new TextPaint(1);
      audioTitlePaint.setTextSize(AndroidUtilities.dp(16.0F));
      audioTitlePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      audioPerformerPaint = new TextPaint(1);
      audioPerformerPaint.setTextSize(AndroidUtilities.dp(15.0F));
      audioPerformerPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      botButtonPaint = new TextPaint(1);
      botButtonPaint.setTextSize(AndroidUtilities.dp(15.0F));
      botButtonPaint.setColor(-1);
      botButtonPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      contactNamePaint = new TextPaint(1);
      contactNamePaint.setTextSize(AndroidUtilities.dp(15.0F));
      contactNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      contactPhonePaint = new TextPaint(1);
      contactPhonePaint.setTextSize(AndroidUtilities.dp(13.0F));
      contactPhonePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      durationPaint = new TextPaint(1);
      durationPaint.setTextSize(AndroidUtilities.dp(12.0F));
      durationPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      durationPaint.setColor(-1);
      timePaint = new TextPaint(1);
      timePaint.setTextSize(AndroidUtilities.dp(12.0F));
      timePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      namePaint = new TextPaint(1);
      namePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      namePaint.setTextSize(AndroidUtilities.dp(14.0F));
      forwardNamePaint = new TextPaint(1);
      forwardNamePaint.setTextSize(AndroidUtilities.dp(14.0F));
      replyNamePaint = new TextPaint(1);
      replyNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      replyNamePaint.setTextSize(AndroidUtilities.dp(14.0F));
      replyTextPaint = new TextPaint(1);
      replyTextPaint.setTextSize(AndroidUtilities.dp(14.0F));
      replyTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      replyTextPaint.linkColor = -14255946;
      replyLinePaint = new Paint();
      statusDrawable = getResources().getDrawable(2130838133);
    }
    this.avatarImage = new ImageReceiver(this);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0F));
    this.avatarDrawable = new AvatarDrawable();
    this.replyImageReceiver = new ImageReceiver(this);
    this.TAG = MediaController.getInstance().generateObserverTag();
    this.contactAvatarDrawable = new AvatarDrawable();
    this.photoImage = new ImageReceiver(this);
    this.photoImage.setDelegate(this);
    this.radialProgress = new RadialProgress(this);
    this.seekBar = new SeekBar(paramContext);
    this.seekBar.setDelegate(this);
    this.seekBarWaveform = new SeekBarWaveform(paramContext);
    this.seekBarWaveform.setDelegate(this);
    this.seekBarWaveform.setParentView(this);
    this.radialProgress = new RadialProgress(this);
  }
  
  private void calcBackgroundWidth(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((this.hasLinkPreview) || (paramInt1 - this.currentMessageObject.lastLineWidth < paramInt2))
    {
      this.totalHeight += AndroidUtilities.dp(14.0F);
      this.backgroundWidth = (Math.max(paramInt3, this.currentMessageObject.lastLineWidth) + AndroidUtilities.dp(31.0F));
      this.backgroundWidth = Math.max(this.backgroundWidth, this.timeWidth + AndroidUtilities.dp(31.0F));
      return;
    }
    paramInt1 = paramInt3 - this.currentMessageObject.lastLineWidth;
    if ((paramInt1 >= 0) && (paramInt1 <= paramInt2))
    {
      this.backgroundWidth = (paramInt3 + paramInt2 - paramInt1 + AndroidUtilities.dp(31.0F));
      return;
    }
    this.backgroundWidth = (Math.max(paramInt3, this.currentMessageObject.lastLineWidth + paramInt2) + AndroidUtilities.dp(31.0F));
  }
  
  private boolean checkAudioMotionEvent(MotionEvent paramMotionEvent)
  {
    boolean bool2;
    if ((this.documentAttachType != 3) && (this.documentAttachType != 5)) {
      bool2 = false;
    }
    int i;
    boolean bool1;
    label159:
    label192:
    do
    {
      return bool2;
      i = (int)paramMotionEvent.getX();
      int j = (int)paramMotionEvent.getY();
      if (this.useSeekBarWaweform)
      {
        bool1 = this.seekBarWaveform.onTouch(paramMotionEvent.getAction(), paramMotionEvent.getX() - this.seekBarX - AndroidUtilities.dp(13.0F), paramMotionEvent.getY() - this.seekBarY);
        if (!bool1) {
          break label192;
        }
        if ((this.useSeekBarWaweform) || (paramMotionEvent.getAction() != 0)) {
          break label159;
        }
        getParent().requestDisallowInterceptTouchEvent(true);
      }
      for (;;)
      {
        this.disallowLongPress = true;
        invalidate();
        return bool1;
        bool1 = this.seekBar.onTouch(paramMotionEvent.getAction(), paramMotionEvent.getX() - this.seekBarX, paramMotionEvent.getY() - this.seekBarY);
        break;
        if ((this.useSeekBarWaweform) && (!this.seekBarWaveform.isStartDraging()) && (paramMotionEvent.getAction() == 1)) {
          didPressedButton(true);
        }
      }
      int k = AndroidUtilities.dp(36.0F);
      if ((this.buttonState == 0) || (this.buttonState == 1) || (this.buttonState == 2)) {
        if ((i >= this.buttonX - AndroidUtilities.dp(12.0F)) && (i <= this.buttonX - AndroidUtilities.dp(12.0F) + this.backgroundWidth) && (j >= this.namesOffset + this.mediaOffsetY) && (j <= this.layoutHeight)) {
          i = 1;
        }
      }
      for (;;)
      {
        if (paramMotionEvent.getAction() == 0)
        {
          bool2 = bool1;
          if (i == 0) {
            break;
          }
          this.buttonPressed = 1;
          invalidate();
          this.radialProgress.swapBackground(getDrawableForCurrentState());
          return true;
          i = 0;
          continue;
          if ((i >= this.buttonX) && (i <= this.buttonX + k) && (j >= this.buttonY) && (j <= this.buttonY + k)) {}
          for (i = 1;; i = 0) {
            break;
          }
        }
      }
      bool2 = bool1;
    } while (this.buttonPressed == 0);
    if (paramMotionEvent.getAction() == 1)
    {
      this.buttonPressed = 0;
      playSoundEffect(0);
      didPressedButton(true);
      invalidate();
    }
    for (;;)
    {
      this.radialProgress.swapBackground(getDrawableForCurrentState());
      return bool1;
      if (paramMotionEvent.getAction() == 3)
      {
        this.buttonPressed = 0;
        invalidate();
      }
      else if ((paramMotionEvent.getAction() == 2) && (i == 0))
      {
        this.buttonPressed = 0;
        invalidate();
      }
    }
  }
  
  private boolean checkBotButtonMotionEvent(MotionEvent paramMotionEvent)
  {
    if (this.botButtons.isEmpty()) {}
    label206:
    do
    {
      for (;;)
      {
        return false;
        int k = (int)paramMotionEvent.getX();
        int m = (int)paramMotionEvent.getY();
        if (paramMotionEvent.getAction() != 0) {
          break;
        }
        int i;
        int j;
        if (this.currentMessageObject.isOutOwner())
        {
          i = getMeasuredWidth() - this.widthForButtons - AndroidUtilities.dp(10.0F);
          j = 0;
        }
        for (;;)
        {
          if (j >= this.botButtons.size()) {
            break label206;
          }
          paramMotionEvent = (BotButton)this.botButtons.get(j);
          int n = paramMotionEvent.y + this.layoutHeight - AndroidUtilities.dp(2.0F);
          if ((k >= paramMotionEvent.x + i) && (k <= paramMotionEvent.x + i + paramMotionEvent.width) && (m >= n) && (m <= paramMotionEvent.height + n))
          {
            this.pressedBotButton = j;
            invalidate();
            return true;
            i = this.currentBackgroundDrawable.getBounds().left;
            if (this.mediaBackground) {}
            for (float f = 1.0F;; f = 7.0F)
            {
              i += AndroidUtilities.dp(f);
              break;
            }
          }
          j += 1;
        }
      }
    } while ((paramMotionEvent.getAction() != 1) || (this.pressedBotButton == -1));
    playSoundEffect(0);
    this.delegate.didPressedBotButton(this, ((BotButton)this.botButtons.get(this.pressedBotButton)).button);
    this.pressedBotButton = -1;
    invalidate();
    return false;
  }
  
  private boolean checkCaptionMotionEvent(MotionEvent paramMotionEvent)
  {
    if ((!(this.currentMessageObject.caption instanceof Spannable)) || (this.captionLayout == null)) {
      return false;
    }
    int i;
    int j;
    if ((paramMotionEvent.getAction() == 0) || (((this.linkPreviewPressed) || (this.pressedLink != null)) && (paramMotionEvent.getAction() == 1)))
    {
      i = (int)paramMotionEvent.getX();
      j = (int)paramMotionEvent.getY();
      if ((i < this.captionX) || (i > this.captionX + this.backgroundWidth) || (j < this.captionY) || (j > this.captionY + this.captionHeight)) {
        break label392;
      }
      if (paramMotionEvent.getAction() != 0) {
        break label359;
      }
    }
    for (;;)
    {
      try
      {
        i -= this.captionX;
        int k = this.captionY;
        j = this.captionLayout.getLineForVertical(j - k);
        k = this.captionLayout.getOffsetForHorizontal(j, i);
        float f = this.captionLayout.getLineLeft(j);
        if ((f <= i) && (this.captionLayout.getLineWidth(j) + f >= i))
        {
          paramMotionEvent = (Spannable)this.currentMessageObject.caption;
          Object localObject = (ClickableSpan[])paramMotionEvent.getSpans(k, k, ClickableSpan.class);
          j = 0;
          if (localObject.length == 0) {
            break label400;
          }
          i = j;
          if (localObject.length != 0)
          {
            i = j;
            if ((localObject[0] instanceof URLSpanBotCommand))
            {
              i = j;
              if (!URLSpanBotCommand.enabled) {
                break label400;
              }
            }
          }
          if (i == 0)
          {
            this.pressedLink = localObject[0];
            this.pressedLinkType = 3;
            resetUrlPaths(false);
            try
            {
              localObject = obtainNewUrlPath(false);
              i = paramMotionEvent.getSpanStart(this.pressedLink);
              ((LinkPath)localObject).setCurrentLayout(this.captionLayout, i, 0.0F);
              this.captionLayout.getSelectionPath(i, paramMotionEvent.getSpanEnd(this.pressedLink), (Path)localObject);
              invalidate();
              return true;
            }
            catch (Exception paramMotionEvent)
            {
              FileLog.e("tmessages", paramMotionEvent);
              continue;
            }
          }
        }
        return false;
      }
      catch (Exception paramMotionEvent)
      {
        FileLog.e("tmessages", paramMotionEvent);
      }
      for (;;)
      {
        label359:
        if (this.pressedLinkType == 3)
        {
          this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, false);
          resetPressedLink(3);
          return true;
          label392:
          resetPressedLink(3);
        }
      }
      label400:
      i = 1;
    }
  }
  
  private boolean checkLinkPreviewMotionEvent(MotionEvent paramMotionEvent)
  {
    if ((this.currentMessageObject.type != 0) || (!this.hasLinkPreview)) {
      return false;
    }
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    if ((i >= this.textX) && (i <= this.textX + this.backgroundWidth) && (j >= this.textY + this.currentMessageObject.textHeight) && (j <= this.textY + this.currentMessageObject.textHeight + this.linkPreviewHeight + AndroidUtilities.dp(8.0F)))
    {
      if (paramMotionEvent.getAction() != 0) {
        break label557;
      }
      if ((this.documentAttachType != 1) && (this.drawPhotoImage) && (this.photoImage.isInsideImage(i, j)))
      {
        if ((this.drawImageButton) && (this.buttonState != -1) && (i >= this.buttonX) && (i <= this.buttonX + AndroidUtilities.dp(48.0F)) && (j >= this.buttonY) && (j <= this.buttonY + AndroidUtilities.dp(48.0F)))
        {
          this.buttonPressed = 1;
          return true;
        }
        this.linkPreviewPressed = true;
        paramMotionEvent = this.currentMessageObject.messageOwner.media.webpage;
        if ((this.documentAttachType == 2) && (this.buttonState == -1) && (MediaController.getInstance().canAutoplayGifs()) && ((this.photoImage.getAnimation() == null) || (!TextUtils.isEmpty(paramMotionEvent.embed_url))))
        {
          this.linkPreviewPressed = false;
          return false;
        }
        return true;
      }
      if ((this.descriptionLayout == null) || (j < this.descriptionY)) {}
    }
    for (;;)
    {
      try
      {
        i -= this.textX + AndroidUtilities.dp(10.0F) + this.descriptionX;
        int k = this.descriptionY;
        j = this.descriptionLayout.getLineForVertical(j - k);
        k = this.descriptionLayout.getOffsetForHorizontal(j, i);
        float f = this.descriptionLayout.getLineLeft(j);
        if ((f <= i) && (this.descriptionLayout.getLineWidth(j) + f >= i))
        {
          paramMotionEvent = (Spannable)this.currentMessageObject.linkDescription;
          Object localObject = (ClickableSpan[])paramMotionEvent.getSpans(k, k, ClickableSpan.class);
          j = 0;
          if (localObject.length == 0) {
            break label947;
          }
          i = j;
          if (localObject.length != 0)
          {
            i = j;
            if ((localObject[0] instanceof URLSpanBotCommand))
            {
              i = j;
              if (!URLSpanBotCommand.enabled) {
                break label947;
              }
            }
          }
          if (i == 0)
          {
            this.pressedLink = localObject[0];
            this.linkBlockNum = -10;
            this.pressedLinkType = 2;
            resetUrlPaths(false);
            try
            {
              localObject = obtainNewUrlPath(false);
              i = paramMotionEvent.getSpanStart(this.pressedLink);
              ((LinkPath)localObject).setCurrentLayout(this.descriptionLayout, i, 0.0F);
              this.descriptionLayout.getSelectionPath(i, paramMotionEvent.getSpanEnd(this.pressedLink), (Path)localObject);
              invalidate();
              return true;
            }
            catch (Exception paramMotionEvent)
            {
              FileLog.e("tmessages", paramMotionEvent);
              continue;
            }
          }
        }
        return false;
      }
      catch (Exception paramMotionEvent)
      {
        FileLog.e("tmessages", paramMotionEvent);
      }
      for (;;)
      {
        label557:
        if (paramMotionEvent.getAction() == 1) {
          if ((this.pressedLinkType == 2) || (this.buttonPressed != 0) || (this.linkPreviewPressed))
          {
            if (this.buttonPressed != 0)
            {
              if (paramMotionEvent.getAction() == 1)
              {
                this.buttonPressed = 0;
                playSoundEffect(0);
                didPressedButton(false);
                invalidate();
              }
              else if (paramMotionEvent.getAction() == 3)
              {
                this.buttonPressed = 0;
                invalidate();
              }
            }
            else
            {
              if (this.pressedLink != null)
              {
                if ((this.pressedLink instanceof URLSpan)) {
                  Browser.openUrl(getContext(), ((URLSpan)this.pressedLink).getURL());
                }
                for (;;)
                {
                  resetPressedLink(2);
                  break;
                  this.pressedLink.onClick(this);
                }
              }
              if (this.drawImageButton) {
                if (this.documentAttachType == 2) {
                  if (this.buttonState == -1) {
                    if (MediaController.getInstance().canAutoplayGifs()) {
                      this.delegate.didPressedImage(this);
                    }
                  }
                }
              }
              for (;;)
              {
                resetPressedLink(2);
                return true;
                this.buttonState = 2;
                this.currentMessageObject.audioProgress = 1.0F;
                this.photoImage.setAllowStartAnimation(false);
                this.photoImage.stopAnimation();
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
                playSoundEffect(0);
                continue;
                if ((this.buttonState == 2) || (this.buttonState == 0))
                {
                  didPressedButton(false);
                  playSoundEffect(0);
                  continue;
                  if (this.buttonState == -1)
                  {
                    this.delegate.didPressedImage(this);
                    playSoundEffect(0);
                    continue;
                    paramMotionEvent = this.currentMessageObject.messageOwner.media.webpage;
                    if (paramMotionEvent != null) {
                      if ((Build.VERSION.SDK_INT >= 16) && (!TextUtils.isEmpty(paramMotionEvent.embed_url))) {
                        this.delegate.needOpenWebView(paramMotionEvent.embed_url, paramMotionEvent.site_name, paramMotionEvent.description, paramMotionEvent.url, paramMotionEvent.embed_width, paramMotionEvent.embed_height);
                      } else {
                        Browser.openUrl(getContext(), paramMotionEvent.url);
                      }
                    }
                  }
                }
              }
            }
          }
          else {
            resetPressedLink(2);
          }
        }
      }
      label947:
      i = 1;
    }
  }
  
  private boolean checkNeedDrawShareButton(MessageObject paramMessageObject)
  {
    boolean bool2 = true;
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
    boolean bool1;
    if (paramMessageObject.type == 13) {
      bool1 = false;
    }
    label209:
    label232:
    do
    {
      do
      {
        do
        {
          return bool1;
          if ((paramMessageObject.messageOwner.fwd_from == null) || (paramMessageObject.messageOwner.fwd_from.channel_id == 0)) {
            break;
          }
          bool1 = bool2;
        } while (!paramMessageObject.isOut());
        if (!paramMessageObject.isFromUser()) {
          break label232;
        }
        TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id));
        if ((localUser == null) || (!localUser.bot)) {
          break label209;
        }
        if (!((SharedPreferences)localObject).getBoolean("direct_bot", false)) {
          break;
        }
        bool1 = bool2;
      } while (!paramMessageObject.isOut());
      while ((!((SharedPreferences)localObject).getBoolean("direct_contact", false)) || (paramMessageObject.isOut()))
      {
        if ((!paramMessageObject.isMegagroup()) || (paramMessageObject.isOut())) {
          break label310;
        }
        localObject = MessagesController.getInstance().getChat(Integer.valueOf(paramMessageObject.messageOwner.to_id.channel_id));
        if ((localObject != null) && (((TLRPC.Chat)localObject).username != null) && (((TLRPC.Chat)localObject).username.length() > 0) && (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact)))
        {
          bool1 = bool2;
          if (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) {
            break;
          }
        }
        return false;
      }
      return true;
      if ((paramMessageObject.messageOwner.from_id >= 0) && (!paramMessageObject.messageOwner.post)) {
        break;
      }
      if ((paramMessageObject.messageOwner.to_id.channel_id == 0) || (((paramMessageObject.messageOwner.via_bot_id != 0) || (paramMessageObject.messageOwner.reply_to_msg_id != 0)) && (paramMessageObject.type == 13))) {
        break label312;
      }
      bool1 = bool2;
    } while (((SharedPreferences)localObject).getBoolean("direct_channel", true));
    label310:
    label312:
    while ((!((SharedPreferences)localObject).getBoolean("direct_group", true)) || (paramMessageObject.isOut())) {
      return false;
    }
    return true;
  }
  
  private boolean checkOtherButtonMotionEvent(MotionEvent paramMotionEvent)
  {
    if ((this.documentAttachType != 1) && (this.currentMessageObject.type != 12) && (this.documentAttachType != 5) && (this.documentAttachType != 4) && (this.documentAttachType != 2) && (this.currentMessageObject.type != 8)) {}
    do
    {
      int i;
      int j;
      do
      {
        return false;
        i = (int)paramMotionEvent.getX();
        j = (int)paramMotionEvent.getY();
        if (paramMotionEvent.getAction() != 0) {
          break;
        }
      } while ((i < this.otherX - AndroidUtilities.dp(20.0F)) || (i > this.otherX + AndroidUtilities.dp(20.0F)) || (j < this.otherY - AndroidUtilities.dp(4.0F)) || (j > this.otherY + AndroidUtilities.dp(30.0F)));
      this.otherPressed = true;
      return true;
    } while ((paramMotionEvent.getAction() != 1) || (!this.otherPressed));
    this.otherPressed = false;
    playSoundEffect(0);
    this.delegate.didPressedOther(this);
    return false;
  }
  
  private boolean checkPhotoImageMotionEvent(MotionEvent paramMotionEvent)
  {
    boolean bool2;
    if ((!this.drawPhotoImage) && (this.documentAttachType != 1)) {
      bool2 = false;
    }
    label406:
    do
    {
      boolean bool1;
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
                return bool2;
                int i = (int)paramMotionEvent.getX();
                int j = (int)paramMotionEvent.getY();
                bool2 = false;
                boolean bool3 = false;
                bool1 = false;
                if (paramMotionEvent.getAction() != 0) {
                  break;
                }
                if ((this.buttonState != -1) && (i >= this.buttonX) && (i <= this.buttonX + AndroidUtilities.dp(48.0F)) && (j >= this.buttonY) && (j <= this.buttonY + AndroidUtilities.dp(48.0F)))
                {
                  this.buttonPressed = 1;
                  invalidate();
                  bool1 = true;
                }
                for (;;)
                {
                  bool2 = bool1;
                  if (!this.imagePressed) {
                    break;
                  }
                  if (!this.currentMessageObject.isSecretPhoto()) {
                    break label406;
                  }
                  this.imagePressed = false;
                  return bool1;
                  if (this.documentAttachType == 1)
                  {
                    bool1 = bool2;
                    if (i >= this.photoImage.getImageX())
                    {
                      bool1 = bool2;
                      if (i <= this.photoImage.getImageX() + this.backgroundWidth - AndroidUtilities.dp(50.0F))
                      {
                        bool1 = bool2;
                        if (j >= this.photoImage.getImageY())
                        {
                          bool1 = bool2;
                          if (j <= this.photoImage.getImageY() + this.photoImage.getImageHeight())
                          {
                            this.imagePressed = true;
                            bool1 = true;
                          }
                        }
                      }
                    }
                  }
                  else if (this.currentMessageObject.type == 13)
                  {
                    bool1 = bool2;
                    if (this.currentMessageObject.getInputStickerSet() == null) {}
                  }
                  else
                  {
                    bool2 = bool3;
                    if (i >= this.photoImage.getImageX())
                    {
                      bool2 = bool3;
                      if (i <= this.photoImage.getImageX() + this.backgroundWidth)
                      {
                        bool2 = bool3;
                        if (j >= this.photoImage.getImageY())
                        {
                          bool2 = bool3;
                          if (j <= this.photoImage.getImageY() + this.photoImage.getImageHeight())
                          {
                            this.imagePressed = true;
                            bool2 = true;
                          }
                        }
                      }
                    }
                    bool1 = bool2;
                    if (this.currentMessageObject.type == 12)
                    {
                      bool1 = bool2;
                      if (MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id)) == null)
                      {
                        this.imagePressed = false;
                        bool1 = false;
                      }
                    }
                  }
                }
                if (this.currentMessageObject.isSendError())
                {
                  this.imagePressed = false;
                  return false;
                }
                bool2 = bool1;
              } while (this.currentMessageObject.type != 8);
              bool2 = bool1;
            } while (this.buttonState != -1);
            bool2 = bool1;
          } while (!MediaController.getInstance().canAutoplayGifs());
          bool2 = bool1;
        } while (this.photoImage.getAnimation() != null);
        this.imagePressed = false;
        return false;
        bool2 = bool1;
      } while (paramMotionEvent.getAction() != 1);
      if (this.buttonPressed == 1)
      {
        this.buttonPressed = 0;
        playSoundEffect(0);
        didPressedButton(false);
        this.radialProgress.swapBackground(getDrawableForCurrentState());
        invalidate();
        return false;
      }
      bool2 = bool1;
    } while (!this.imagePressed);
    this.imagePressed = false;
    if ((this.buttonState == -1) || (this.buttonState == 2) || (this.buttonState == 3))
    {
      playSoundEffect(0);
      didClickedImage();
    }
    for (;;)
    {
      invalidate();
      return false;
      if ((this.buttonState == 0) && (this.documentAttachType == 1))
      {
        playSoundEffect(0);
        didPressedButton(false);
      }
    }
  }
  
  private boolean checkTextBlockMotionEvent(MotionEvent paramMotionEvent)
  {
    if ((this.currentMessageObject.type != 0) || (this.currentMessageObject.textLayoutBlocks == null) || (this.currentMessageObject.textLayoutBlocks.isEmpty()) || (!(this.currentMessageObject.messageText instanceof Spannable))) {
      return false;
    }
    int k;
    int i;
    int m;
    int j;
    if ((paramMotionEvent.getAction() == 0) || ((paramMotionEvent.getAction() == 1) && (this.pressedLinkType == 1)))
    {
      k = (int)paramMotionEvent.getX();
      i = (int)paramMotionEvent.getY();
      if ((k < this.textX) || (i < this.textY) || (k > this.textX + this.currentMessageObject.textWidth) || (i > this.textY + this.currentMessageObject.textHeight)) {
        break label912;
      }
      m = i - this.textY;
      j = 0;
      i = 0;
      if ((i < this.currentMessageObject.textLayoutBlocks.size()) && (((MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i)).textYOffset <= m)) {}
    }
    for (;;)
    {
      Object localObject2;
      try
      {
        Object localObject1 = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(j);
        i = k - (this.textX - (int)Math.ceil(((MessageObject.TextLayoutBlock)localObject1).textXOffset));
        k = (int)(m - ((MessageObject.TextLayoutBlock)localObject1).textYOffset);
        k = ((MessageObject.TextLayoutBlock)localObject1).textLayout.getLineForVertical(k);
        m = ((MessageObject.TextLayoutBlock)localObject1).textLayout.getOffsetForHorizontal(k, i) + ((MessageObject.TextLayoutBlock)localObject1).charactersOffset;
        float f = ((MessageObject.TextLayoutBlock)localObject1).textLayout.getLineLeft(k);
        if ((f <= i) && (((MessageObject.TextLayoutBlock)localObject1).textLayout.getLineWidth(k) + f >= i))
        {
          Spannable localSpannable = (Spannable)this.currentMessageObject.messageText;
          localObject2 = (ClickableSpan[])localSpannable.getSpans(m, m, ClickableSpan.class);
          k = 0;
          if (localObject2.length == 0) {
            break label920;
          }
          i = k;
          if (localObject2.length != 0)
          {
            i = k;
            if ((localObject2[0] instanceof URLSpanBotCommand))
            {
              i = k;
              if (!URLSpanBotCommand.enabled) {
                break label920;
              }
            }
          }
          if (i == 0)
          {
            if (paramMotionEvent.getAction() != 0) {
              break label876;
            }
            this.pressedLink = localObject2[0];
            this.linkBlockNum = j;
            this.pressedLinkType = 1;
            resetUrlPaths(false);
            try
            {
              paramMotionEvent = obtainNewUrlPath(false);
              k = localSpannable.getSpanStart(this.pressedLink) - ((MessageObject.TextLayoutBlock)localObject1).charactersOffset;
              m = localSpannable.getSpanEnd(this.pressedLink);
              i = ((MessageObject.TextLayoutBlock)localObject1).textLayout.getText().length();
              paramMotionEvent.setCurrentLayout(((MessageObject.TextLayoutBlock)localObject1).textLayout, k, 0.0F);
              ((MessageObject.TextLayoutBlock)localObject1).textLayout.getSelectionPath(k, m - ((MessageObject.TextLayoutBlock)localObject1).charactersOffset, paramMotionEvent);
              if (m < ((MessageObject.TextLayoutBlock)localObject1).charactersOffset + i) {
                break label925;
              }
              i = j + 1;
              if (i >= this.currentMessageObject.textLayoutBlocks.size()) {
                break label925;
              }
              paramMotionEvent = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i);
              n = paramMotionEvent.textLayout.getText().length();
              localObject2 = (ClickableSpan[])localSpannable.getSpans(paramMotionEvent.charactersOffset, paramMotionEvent.charactersOffset, ClickableSpan.class);
              if ((localObject2 == null) || (localObject2.length == 0)) {
                break label925;
              }
              if (localObject2[0] == this.pressedLink) {
                continue;
              }
            }
            catch (Exception paramMotionEvent)
            {
              int n;
              FileLog.e("tmessages", paramMotionEvent);
              continue;
            }
            if (i >= 0)
            {
              paramMotionEvent = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i);
              j = paramMotionEvent.textLayout.getText().length();
              localObject1 = (ClickableSpan[])localSpannable.getSpans(paramMotionEvent.charactersOffset + j - 1, paramMotionEvent.charactersOffset + j - 1, ClickableSpan.class);
              if ((localObject1 != null) && (localObject1.length != 0))
              {
                localObject1 = localObject1[0];
                localObject2 = this.pressedLink;
                if (localObject1 == localObject2) {
                  continue;
                }
              }
            }
            invalidate();
            return true;
            j = i;
            i += 1;
            break;
            localObject2 = obtainNewUrlPath(false);
            ((LinkPath)localObject2).setCurrentLayout(paramMotionEvent.textLayout, 0, paramMotionEvent.height);
            paramMotionEvent.textLayout.getSelectionPath(0, m - paramMotionEvent.charactersOffset, (Path)localObject2);
            if (m < ((MessageObject.TextLayoutBlock)localObject1).charactersOffset + n - 1) {
              break label925;
            }
            i += 1;
            continue;
            localObject1 = obtainNewUrlPath(false);
            j = localSpannable.getSpanStart(this.pressedLink) - paramMotionEvent.charactersOffset;
            ((LinkPath)localObject1).setCurrentLayout(paramMotionEvent.textLayout, j, -paramMotionEvent.height);
            paramMotionEvent.textLayout.getSelectionPath(j, localSpannable.getSpanEnd(this.pressedLink) - paramMotionEvent.charactersOffset, (Path)localObject1);
            if (j >= 0) {
              continue;
            }
            i -= 1;
            continue;
          }
        }
        return false;
      }
      catch (Exception paramMotionEvent)
      {
        FileLog.e("tmessages", paramMotionEvent);
      }
      for (;;)
      {
        label876:
        if (localObject2[0] == this.pressedLink)
        {
          this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, false);
          resetPressedLink(1);
          return true;
          label912:
          resetPressedLink(1);
        }
      }
      label920:
      i = 1;
      continue;
      label925:
      if (k < 0) {
        i = j - 1;
      }
    }
  }
  
  private int createDocumentLayout(int paramInt, MessageObject paramMessageObject)
  {
    if (paramMessageObject.type == 0) {}
    for (this.documentAttach = paramMessageObject.messageOwner.media.webpage.document; this.documentAttach == null; this.documentAttach = paramMessageObject.messageOwner.media.document) {
      return 0;
    }
    int j;
    int i;
    Object localObject1;
    if (MessageObject.isVoiceDocument(this.documentAttach))
    {
      this.documentAttachType = 3;
      int k = 0;
      j = 0;
      i = k;
      if (j < this.documentAttach.attributes.size())
      {
        localObject1 = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(j);
        if ((localObject1 instanceof TLRPC.TL_documentAttributeAudio)) {
          i = ((TLRPC.DocumentAttribute)localObject1).duration;
        }
      }
      else
      {
        this.availableTimeWidth = (paramInt - AndroidUtilities.dp(94.0F) - (int)Math.ceil(audioTimePaint.measureText("00:00")));
        measureTime(paramMessageObject);
        j = AndroidUtilities.dp(174.0F);
        k = this.timeWidth;
        if (!this.hasLinkPreview) {
          this.backgroundWidth = Math.min(paramInt, AndroidUtilities.dp(10.0F) * i + (j + k));
        }
        if (!paramMessageObject.isOutOwner()) {
          break label275;
        }
        paramInt = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0).getInt("theme_rfname_color", -11162801);
        i = MihanTheme.getLighterColor(paramInt, 0.3F);
        this.seekBarWaveform.setColors(i, paramInt, -5644906);
        this.seekBar.setColors(i, paramInt, -5644906);
      }
      for (;;)
      {
        this.seekBarWaveform.setMessageObject(paramMessageObject);
        return 0;
        j += 1;
        break;
        label275:
        paramInt = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0).getInt("theme_lfname_color", -12940081);
        i = MihanTheme.getLighterColor(paramInt, 0.3F);
        this.seekBarWaveform.setColors(i, paramInt, -4399384);
        this.seekBar.setColors(i, paramInt, -4399384);
      }
    }
    if (MessageObject.isMusicDocument(this.documentAttach))
    {
      this.documentAttachType = 5;
      if (paramMessageObject.isOutOwner())
      {
        i = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0).getInt("theme_rfname_color", -11162801);
        j = MihanTheme.getLighterColor(i, 0.3F);
        this.seekBar.setColors(j, i, -5644906);
        paramInt -= AndroidUtilities.dp(86.0F);
        this.songLayout = new StaticLayout(TextUtils.ellipsize(paramMessageObject.getMusicTitle().replace('\n', ' '), audioTitlePaint, paramInt - AndroidUtilities.dp(12.0F), TextUtils.TruncateAt.END), audioTitlePaint, paramInt, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        if (this.songLayout.getLineCount() > 0) {
          this.songX = (-(int)Math.ceil(this.songLayout.getLineLeft(0)));
        }
        this.performerLayout = new StaticLayout(TextUtils.ellipsize(paramMessageObject.getMusicAuthor().replace('\n', ' '), audioPerformerPaint, paramInt, TextUtils.TruncateAt.END), audioPerformerPaint, paramInt, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        if (this.performerLayout.getLineCount() > 0) {
          this.performerX = (-(int)Math.ceil(this.performerLayout.getLineLeft(0)));
        }
        j = 0;
        paramInt = 0;
      }
      for (;;)
      {
        i = j;
        if (paramInt < this.documentAttach.attributes.size())
        {
          paramMessageObject = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(paramInt);
          if ((paramMessageObject instanceof TLRPC.TL_documentAttributeAudio)) {
            i = paramMessageObject.duration;
          }
        }
        else
        {
          paramInt = (int)Math.ceil(audioTimePaint.measureText(String.format("%d:%02d / %d:%02d", new Object[] { Integer.valueOf(i / 60), Integer.valueOf(i % 60), Integer.valueOf(i / 60), Integer.valueOf(i % 60) })));
          this.availableTimeWidth = (this.backgroundWidth - AndroidUtilities.dp(94.0F) - paramInt);
          return paramInt;
          i = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0).getInt("theme_lfname_color", -12940081);
          j = MihanTheme.getLighterColor(i, 0.3F);
          this.seekBar.setColors(j, i, -4399384);
          break;
        }
        paramInt += 1;
      }
    }
    if (MessageObject.isVideoDocument(this.documentAttach))
    {
      this.documentAttachType = 4;
      j = 0;
      i = 0;
      for (;;)
      {
        paramInt = j;
        if (i < this.documentAttach.attributes.size())
        {
          paramMessageObject = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(i);
          if ((paramMessageObject instanceof TLRPC.TL_documentAttributeVideo)) {
            paramInt = paramMessageObject.duration;
          }
        }
        else
        {
          i = paramInt / 60;
          paramMessageObject = "Video, " + String.format("%d:%02d, %s", new Object[] { Integer.valueOf(i), Integer.valueOf(paramInt - i * 60), AndroidUtilities.formatFileSize(this.documentAttach.size) });
          this.infoWidth = ((int)Math.ceil(infoPaint.measureText(paramMessageObject)));
          this.infoLayout = new StaticLayout(paramMessageObject, infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
          return 0;
        }
        i += 1;
      }
    }
    boolean bool;
    Object localObject2;
    Layout.Alignment localAlignment;
    TextUtils.TruncateAt localTruncateAt;
    if (((this.documentAttach.mime_type != null) && (this.documentAttach.mime_type.toLowerCase().startsWith("image/"))) || (((this.documentAttach.thumb instanceof TLRPC.TL_photoSize)) && (!(this.documentAttach.thumb.location instanceof TLRPC.TL_fileLocationUnavailable))))
    {
      bool = true;
      this.drawPhotoImage = bool;
      i = paramInt;
      if (!this.drawPhotoImage) {
        i = paramInt + AndroidUtilities.dp(30.0F);
      }
      this.documentAttachType = 1;
      localObject2 = FileLoader.getDocumentFileName(this.documentAttach);
      if (localObject2 != null)
      {
        localObject1 = localObject2;
        if (((String)localObject2).length() != 0) {}
      }
      else
      {
        localObject1 = LocaleController.getString("AttachDocument", 2131165371);
      }
      localObject2 = docNamePaint;
      localAlignment = Layout.Alignment.ALIGN_NORMAL;
      localTruncateAt = TextUtils.TruncateAt.MIDDLE;
      if (!this.drawPhotoImage) {
        break label1192;
      }
    }
    label1192:
    for (paramInt = 2;; paramInt = 1)
    {
      this.docTitleLayout = StaticLayoutEx.createStaticLayout((CharSequence)localObject1, (TextPaint)localObject2, i, localAlignment, 1.0F, 0.0F, false, localTruncateAt, i, paramInt);
      this.docTitleOffsetX = Integer.MIN_VALUE;
      if ((this.docTitleLayout == null) || (this.docTitleLayout.getLineCount() <= 0)) {
        break label1421;
      }
      j = 0;
      paramInt = 0;
      while (paramInt < this.docTitleLayout.getLineCount())
      {
        j = Math.max(j, (int)Math.ceil(this.docTitleLayout.getLineWidth(paramInt)));
        this.docTitleOffsetX = Math.max(this.docTitleOffsetX, (int)Math.ceil(-this.docTitleLayout.getLineLeft(paramInt)));
        paramInt += 1;
      }
      bool = false;
      break;
    }
    paramInt = Math.min(i, j);
    for (;;)
    {
      localObject1 = AndroidUtilities.formatFileSize(this.documentAttach.size) + " " + FileLoader.getDocumentExtension(this.documentAttach);
      this.infoWidth = Math.min(i - AndroidUtilities.dp(30.0F), (int)Math.ceil(infoPaint.measureText((String)localObject1)));
      localObject1 = TextUtils.ellipsize((CharSequence)localObject1, infoPaint, this.infoWidth, TextUtils.TruncateAt.END);
      try
      {
        if (this.infoWidth < 0) {
          this.infoWidth = AndroidUtilities.dp(10.0F);
        }
        this.infoLayout = new StaticLayout((CharSequence)localObject1, infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      }
      catch (Exception localException)
      {
        for (;;)
        {
          label1421:
          FileLog.e("tmessages", localException);
          continue;
          this.photoImage.setImageBitmap((BitmapDrawable)null);
        }
      }
      if (this.drawPhotoImage)
      {
        this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(paramMessageObject.photoThumbs, AndroidUtilities.getPhotoSize());
        this.photoImage.setNeedsQualityThumb(true);
        this.photoImage.setShouldGenerateQualityThumb(true);
        this.photoImage.setParentMessageObject(paramMessageObject);
        if (this.currentPhotoObject == null) {
          break;
        }
        this.currentPhotoFilter = "86_86_b";
        this.photoImage.setImage(null, null, null, null, this.currentPhotoObject.location, this.currentPhotoFilter, 0, null, true);
      }
      return paramInt;
      paramInt = i;
      this.docTitleOffsetX = 0;
    }
  }
  
  private void didClickedImage()
  {
    if ((this.currentMessageObject.type == 1) || (this.currentMessageObject.type == 13)) {
      if (this.buttonState == -1) {
        this.delegate.didPressedImage(this);
      }
    }
    label41:
    do
    {
      do
      {
        do
        {
          do
          {
            break label41;
            break label41;
            do
            {
              return;
            } while (this.buttonState != 0);
            didPressedButton(false);
            return;
            if (this.currentMessageObject.type == 12)
            {
              localObject = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id));
              this.delegate.didPressedUserAvatar(this, (TLRPC.User)localObject);
              return;
            }
            if (this.currentMessageObject.type != 8) {
              break;
            }
            if (this.buttonState == -1)
            {
              if (MediaController.getInstance().canAutoplayGifs())
              {
                this.delegate.didPressedImage(this);
                return;
              }
              this.buttonState = 2;
              this.currentMessageObject.audioProgress = 1.0F;
              this.photoImage.setAllowStartAnimation(false);
              this.photoImage.stopAnimation();
              this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
              invalidate();
              return;
            }
          } while ((this.buttonState != 2) && (this.buttonState != 0));
          didPressedButton(false);
          return;
          if (this.documentAttachType != 4) {
            break;
          }
        } while ((this.buttonState != 0) && (this.buttonState != 3));
        didPressedButton(false);
        return;
        if (this.currentMessageObject.type == 4)
        {
          this.delegate.didPressedImage(this);
          return;
        }
        if (this.documentAttachType != 1) {
          break;
        }
      } while (this.buttonState != -1);
      this.delegate.didPressedImage(this);
      return;
    } while ((this.documentAttachType != 2) || (this.buttonState != -1));
    Object localObject = this.currentMessageObject.messageOwner.media.webpage;
    if ((Build.VERSION.SDK_INT >= 16) && (((TLRPC.WebPage)localObject).embed_url != null) && (((TLRPC.WebPage)localObject).embed_url.length() != 0))
    {
      this.delegate.needOpenWebView(((TLRPC.WebPage)localObject).embed_url, ((TLRPC.WebPage)localObject).site_name, ((TLRPC.WebPage)localObject).description, ((TLRPC.WebPage)localObject).url, ((TLRPC.WebPage)localObject).embed_width, ((TLRPC.WebPage)localObject).embed_height);
      return;
    }
    Browser.openUrl(getContext(), ((TLRPC.WebPage)localObject).url);
  }
  
  private void didPressedButton(boolean paramBoolean)
  {
    if (this.buttonState == 0) {
      if ((this.documentAttachType == 3) || (this.documentAttachType == 5)) {
        if (this.delegate.needPlayAudio(this.currentMessageObject))
        {
          this.buttonState = 1;
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
          invalidate();
        }
      }
    }
    label451:
    label732:
    do
    {
      do
      {
        do
        {
          return;
          this.cancelLoading = false;
          this.radialProgress.setProgress(0.0F, false);
          if (this.currentMessageObject.type == 1)
          {
            localImageReceiver = this.photoImage;
            localObject = this.currentPhotoObject.location;
            str = this.currentPhotoFilter;
            if (this.currentPhotoObjectThumb != null)
            {
              localFileLocation = this.currentPhotoObjectThumb.location;
              localImageReceiver.setImage((TLObject)localObject, str, localFileLocation, this.currentPhotoFilter, this.currentPhotoObject.size, null, false);
            }
          }
          for (;;)
          {
            this.buttonState = 1;
            this.radialProgress.setBackground(getDrawableForCurrentState(), true, paramBoolean);
            invalidate();
            return;
            localFileLocation = null;
            break;
            if (this.currentMessageObject.type == 8)
            {
              this.currentMessageObject.audioProgress = 2.0F;
              localImageReceiver = this.photoImage;
              localObject = this.currentMessageObject.messageOwner.media.document;
              if (this.currentPhotoObject != null) {}
              for (localFileLocation = this.currentPhotoObject.location;; localFileLocation = null)
              {
                localImageReceiver.setImage((TLObject)localObject, null, localFileLocation, this.currentPhotoFilter, this.currentMessageObject.messageOwner.media.document.size, null, false);
                break;
              }
            }
            if (this.currentMessageObject.type == 9)
            {
              FileLoader.getInstance().loadFile(this.currentMessageObject.messageOwner.media.document, false, false);
            }
            else if (this.documentAttachType == 4)
            {
              FileLoader.getInstance().loadFile(this.documentAttach, true, false);
            }
            else
            {
              if ((this.currentMessageObject.type != 0) || (this.documentAttachType == 0)) {
                break label451;
              }
              if (this.documentAttachType == 2)
              {
                this.photoImage.setImage(this.currentMessageObject.messageOwner.media.webpage.document, null, this.currentPhotoObject.location, this.currentPhotoFilter, this.currentMessageObject.messageOwner.media.webpage.document.size, null, false);
                this.currentMessageObject.audioProgress = 2.0F;
              }
              else if (this.documentAttachType == 1)
              {
                FileLoader.getInstance().loadFile(this.currentMessageObject.messageOwner.media.webpage.document, false, false);
              }
            }
          }
          ImageReceiver localImageReceiver = this.photoImage;
          Object localObject = this.currentPhotoObject.location;
          String str = this.currentPhotoFilter;
          if (this.currentPhotoObjectThumb != null) {}
          for (TLRPC.FileLocation localFileLocation = this.currentPhotoObjectThumb.location;; localFileLocation = null)
          {
            localImageReceiver.setImage((TLObject)localObject, str, localFileLocation, this.currentPhotoFilterThumb, 0, null, false);
            break;
          }
          if (this.buttonState != 1) {
            break label732;
          }
          if ((this.documentAttachType != 3) && (this.documentAttachType != 5)) {
            break;
          }
        } while (!MediaController.getInstance().pauseAudio(this.currentMessageObject));
        this.buttonState = 0;
        this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
        invalidate();
        return;
        if ((this.currentMessageObject.isOut()) && (this.currentMessageObject.isSending()))
        {
          this.delegate.didPressedCancelSendButton(this);
          return;
        }
        this.cancelLoading = true;
        if ((this.documentAttachType == 4) || (this.documentAttachType == 1)) {
          FileLoader.getInstance().cancelLoadFile(this.documentAttach);
        }
        for (;;)
        {
          this.buttonState = 0;
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
          invalidate();
          return;
          if ((this.currentMessageObject.type == 0) || (this.currentMessageObject.type == 1) || (this.currentMessageObject.type == 8)) {
            this.photoImage.cancelLoadImage();
          } else if (this.currentMessageObject.type == 9) {
            FileLoader.getInstance().cancelLoadFile(this.currentMessageObject.messageOwner.media.document);
          }
        }
        if (this.buttonState == 2)
        {
          if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
          {
            this.radialProgress.setProgress(0.0F, false);
            FileLoader.getInstance().loadFile(this.documentAttach, true, false);
            this.buttonState = 4;
            this.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
            invalidate();
            return;
          }
          this.photoImage.setAllowStartAnimation(true);
          this.photoImage.startAnimation();
          this.currentMessageObject.audioProgress = 0.0F;
          this.buttonState = -1;
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
          return;
        }
        if (this.buttonState == 3)
        {
          this.delegate.didPressedImage(this);
          return;
        }
      } while ((this.buttonState != 4) || ((this.documentAttachType != 3) && (this.documentAttachType != 5)));
      if (((!this.currentMessageObject.isOut()) || (!this.currentMessageObject.isSending())) && (!this.currentMessageObject.isSendError())) {
        break;
      }
    } while (this.delegate == null);
    this.delegate.didPressedCancelSendButton(this);
    return;
    FileLoader.getInstance().cancelLoadFile(this.documentAttach);
    this.buttonState = 2;
    this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
    invalidate();
  }
  
  private void drawContent(Canvas paramCanvas)
  {
    if ((this.needNewVisiblePart) && (this.currentMessageObject.type == 0))
    {
      getLocalVisibleRect(this.scrollRect);
      setVisiblePart(this.scrollRect.top, this.scrollRect.bottom - this.scrollRect.top);
      this.needNewVisiblePart = false;
    }
    this.photoImage.setPressed(isDrawSelectedBackground());
    Object localObject1 = this.photoImage;
    boolean bool2;
    label184:
    int n;
    int i4;
    int i1;
    int m;
    int i2;
    int i3;
    if (!PhotoViewer.getInstance().isShowingImage(this.currentMessageObject))
    {
      bool1 = true;
      ((ImageReceiver)localObject1).setVisible(bool1, false);
      this.radialProgress.setHideCurrentDrawable(false);
      this.radialProgress.setProgressColor(-1);
      bool1 = false;
      bool2 = false;
      if ((this.currentMessageObject.type != 0) || (this.currentMessageObject.textLayoutBlocks == null) || (this.currentMessageObject.textLayoutBlocks.isEmpty())) {
        break label2582;
      }
      if (!this.currentMessageObject.isOutOwner()) {
        break label928;
      }
      this.textX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0F));
      this.textY = (AndroidUtilities.dp(10.0F) + this.namesOffset);
      if (this.firstVisibleBlockNum >= 0)
      {
        i = this.firstVisibleBlockNum;
        if ((i <= this.lastVisibleBlockNum) && (i < this.currentMessageObject.textLayoutBlocks.size())) {
          break label952;
        }
      }
      bool1 = bool2;
      if (!this.hasLinkPreview) {
        break label1443;
      }
      n = this.textY + this.currentMessageObject.textHeight + AndroidUtilities.dp(8.0F);
      i4 = this.textX + AndroidUtilities.dp(1.0F);
      j = n;
      i1 = 0;
      localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
      k = ((SharedPreferences)localObject1).getInt("theme_rfname_color", -11162801);
      m = ((SharedPreferences)localObject1).getInt("theme_lfname_color", -12940081);
      i2 = ((SharedPreferences)localObject1).getInt("theme_rtext_color", -16777216);
      i3 = ((SharedPreferences)localObject1).getInt("theme_ltext_color", -16777216);
      localObject1 = replyLinePaint;
      if (!this.currentMessageObject.isOutOwner()) {
        break label1156;
      }
      i = k;
      label373:
      ((Paint)localObject1).setColor(i);
      paramCanvas.drawRect(i4, j - AndroidUtilities.dp(3.0F), AndroidUtilities.dp(2.0F) + i4, this.linkPreviewHeight + j + AndroidUtilities.dp(3.0F), replyLinePaint);
      i = j;
      if (this.siteNameLayout != null)
      {
        localObject1 = replyNamePaint;
        if (!this.currentMessageObject.isOutOwner()) {
          break label1162;
        }
        label447:
        ((TextPaint)localObject1).setColor(k);
        paramCanvas.save();
        paramCanvas.translate(AndroidUtilities.dp(10.0F) + i4, j - AndroidUtilities.dp(3.0F));
        this.siteNameLayout.draw(paramCanvas);
        paramCanvas.restore();
        i = j + this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
      }
      replyNamePaint.setColor(-16777216);
      localObject1 = replyTextPaint;
      if (!this.currentMessageObject.isOutOwner()) {
        break label1169;
      }
    }
    label928:
    label952:
    label1156:
    label1162:
    label1169:
    for (int j = i2;; j = i3)
    {
      ((TextPaint)localObject1).setColor(j);
      k = i;
      j = i1;
      if (this.titleLayout != null)
      {
        j = i;
        if (i != n) {
          j = i + AndroidUtilities.dp(2.0F);
        }
        i = j - AndroidUtilities.dp(1.0F);
        paramCanvas.save();
        paramCanvas.translate(AndroidUtilities.dp(10.0F) + i4 + this.titleX, j - AndroidUtilities.dp(3.0F));
        this.titleLayout.draw(paramCanvas);
        paramCanvas.restore();
        k = j + this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
        j = i;
      }
      m = k;
      i = j;
      if (this.authorLayout != null)
      {
        m = k;
        if (k != n) {
          m = k + AndroidUtilities.dp(2.0F);
        }
        i = j;
        if (j == 0) {
          i = m - AndroidUtilities.dp(1.0F);
        }
        paramCanvas.save();
        paramCanvas.translate(AndroidUtilities.dp(10.0F) + i4 + this.authorX, m - AndroidUtilities.dp(3.0F));
        this.authorLayout.draw(paramCanvas);
        paramCanvas.restore();
        m += this.authorLayout.getLineBottom(this.authorLayout.getLineCount() - 1);
      }
      j = m;
      k = i;
      if (this.descriptionLayout == null) {
        break label1215;
      }
      k = m;
      if (m != n) {
        k = m + AndroidUtilities.dp(2.0F);
      }
      j = i;
      if (i == 0) {
        j = k - AndroidUtilities.dp(1.0F);
      }
      this.descriptionY = (k - AndroidUtilities.dp(3.0F));
      paramCanvas.save();
      paramCanvas.translate(AndroidUtilities.dp(10.0F) + i4 + this.descriptionX, this.descriptionY);
      if ((this.pressedLink == null) || (this.linkBlockNum != -10)) {
        break label1176;
      }
      i = 0;
      while (i < this.urlPath.size())
      {
        paramCanvas.drawPath((Path)this.urlPath.get(i), urlPaint);
        i += 1;
      }
      bool1 = false;
      break;
      this.textX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(17.0F));
      break label184;
      localObject1 = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i);
      paramCanvas.save();
      paramCanvas.translate(this.textX - (int)Math.ceil(((MessageObject.TextLayoutBlock)localObject1).textXOffset), this.textY + ((MessageObject.TextLayoutBlock)localObject1).textYOffset);
      if ((this.pressedLink != null) && (i == this.linkBlockNum))
      {
        j = 0;
        while (j < this.urlPath.size())
        {
          paramCanvas.drawPath((Path)this.urlPath.get(j), urlPaint);
          j += 1;
        }
      }
      if ((i == this.linkSelectionBlockNum) && (!this.urlPathSelection.isEmpty()))
      {
        j = 0;
        while (j < this.urlPathSelection.size())
        {
          paramCanvas.drawPath((Path)this.urlPathSelection.get(j), urlSelectionPaint);
          j += 1;
        }
      }
      try
      {
        ((MessageObject.TextLayoutBlock)localObject1).textLayout.draw(paramCanvas);
        paramCanvas.restore();
        i += 1;
      }
      catch (Exception localException1)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException1);
        }
      }
      i = m;
      break label373;
      k = m;
      break label447;
    }
    label1176:
    this.descriptionLayout.draw(paramCanvas);
    paramCanvas.restore();
    int i = k + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
    int k = j;
    j = i;
    label1215:
    boolean bool1 = bool2;
    if (this.drawPhotoImage)
    {
      i = j;
      if (j != n) {
        i = j + AndroidUtilities.dp(2.0F);
      }
      if (!this.isSmallImage) {
        break label2445;
      }
      this.photoImage.setImageCoords(this.backgroundWidth + i4 - AndroidUtilities.dp(81.0F), k, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
      bool2 = this.photoImage.draw(paramCanvas);
      bool1 = bool2;
      if (this.videoInfoLayout != null)
      {
        i = this.photoImage.getImageX() + this.photoImage.getImageWidth() - AndroidUtilities.dp(8.0F) - this.durationWidth;
        j = this.photoImage.getImageY() + this.photoImage.getImageHeight() - AndroidUtilities.dp(19.0F);
        Theme.timeBackgroundDrawable.setBounds(i - AndroidUtilities.dp(4.0F), j - AndroidUtilities.dp(1.5F), this.durationWidth + i + AndroidUtilities.dp(4.0F), AndroidUtilities.dp(14.5F) + j);
        Theme.timeBackgroundDrawable.draw(paramCanvas);
        paramCanvas.save();
        paramCanvas.translate(i, j);
        this.videoInfoLayout.draw(paramCanvas);
        paramCanvas.restore();
        bool1 = bool2;
      }
    }
    label1443:
    this.drawTime = true;
    label1448:
    label1494:
    long l1;
    long l2;
    float f;
    if ((this.buttonState == -1) && (this.currentMessageObject.isSecretPhoto()))
    {
      i = 4;
      if (this.currentMessageObject.messageOwner.destroyTime != 0)
      {
        if (!this.currentMessageObject.isOutOwner()) {
          break label2613;
        }
        i = 6;
      }
      setDrawableBounds(Theme.photoStatesDrawables[i][this.buttonPressed], this.buttonX, this.buttonY);
      Theme.photoStatesDrawables[i][this.buttonPressed].setAlpha((int)(255.0F * (1.0F - this.radialProgress.getAlpha())));
      Theme.photoStatesDrawables[i][this.buttonPressed].draw(paramCanvas);
      if ((!this.currentMessageObject.isOutOwner()) && (this.currentMessageObject.messageOwner.destroyTime != 0))
      {
        l1 = System.currentTimeMillis();
        l2 = ConnectionsManager.getInstance().getTimeDifference() * 1000;
        f = (float)Math.max(0L, this.currentMessageObject.messageOwner.destroyTime * 1000L - (l1 + l2)) / (this.currentMessageObject.messageOwner.ttl * 1000.0F);
        paramCanvas.drawArc(this.deleteProgressRect, -90.0F, -360.0F * f, true, deleteProgressPaint);
        if (f != 0.0F)
        {
          i = AndroidUtilities.dp(2.0F);
          invalidate((int)this.deleteProgressRect.left - i, (int)this.deleteProgressRect.top - i, (int)this.deleteProgressRect.right + i * 2, (int)this.deleteProgressRect.bottom + i * 2);
        }
        updateSecretTimeText(this.currentMessageObject);
      }
    }
    Object localObject2;
    if ((this.documentAttachType == 2) || (this.currentMessageObject.type == 8))
    {
      if (this.photoImage.getVisible())
      {
        localObject2 = Theme.docMenuDrawable[3];
        i = this.photoImage.getImageX() + this.photoImage.getImageWidth() - AndroidUtilities.dp(14.0F);
        this.otherX = i;
        j = this.photoImage.getImageY() + AndroidUtilities.dp(8.1F);
        this.otherY = j;
        setDrawableBounds((Drawable)localObject2, i, j);
        Theme.docMenuDrawable[3].draw(paramCanvas);
        MihanTheme.setColorFilter(Theme.docMenuDrawable[3], -1);
        if (this.infoLayout != null)
        {
          infoPaint.setColor(-1);
          localObject2 = Theme.timeBackgroundDrawable;
          i = this.photoImage.getImageX();
          j = AndroidUtilities.dp(4.0F);
          k = this.photoImage.getImageY();
          m = AndroidUtilities.dp(4.0F);
          n = this.infoWidth;
          setDrawableBounds((Drawable)localObject2, j + i, m + k, AndroidUtilities.dp(8.0F) + n, AndroidUtilities.dp(16.5F));
          Theme.timeBackgroundDrawable.draw(paramCanvas);
          paramCanvas.save();
          paramCanvas.translate(this.photoImage.getImageX() + AndroidUtilities.dp(8.0F), this.photoImage.getImageY() + AndroidUtilities.dp(5.5F));
          this.infoLayout.draw(paramCanvas);
          paramCanvas.restore();
        }
      }
      label1988:
      if ((this.currentMessageObject.type != 1) && (this.documentAttachType != 4)) {
        break label3760;
      }
      if (this.photoImage.getVisible())
      {
        if ((this.currentMessageObject.type == 1) || (this.documentAttachType == 4))
        {
          localObject2 = Theme.docMenuDrawable[3];
          i = this.photoImage.getImageX() + this.photoImage.getImageWidth() - AndroidUtilities.dp(14.0F);
          this.otherX = i;
          j = this.photoImage.getImageY() + AndroidUtilities.dp(8.1F);
          this.otherY = j;
          setDrawableBounds((Drawable)localObject2, i, j);
          Theme.docMenuDrawable[3].draw(paramCanvas);
          MihanTheme.setColorFilter(Theme.docMenuDrawable[3], -1);
        }
        if ((this.infoLayout == null) || ((this.buttonState != 1) && (this.buttonState != 0) && (this.buttonState != 3) && (!this.currentMessageObject.isSecretPhoto()))) {
          break label3611;
        }
        infoPaint.setColor(-1);
        localObject2 = Theme.timeBackgroundDrawable;
        i = this.photoImage.getImageX();
        j = AndroidUtilities.dp(4.0F);
        k = this.photoImage.getImageY();
        m = AndroidUtilities.dp(4.0F);
        n = this.infoWidth;
        setDrawableBounds((Drawable)localObject2, j + i, m + k, AndroidUtilities.dp(8.0F) + n, AndroidUtilities.dp(16.5F));
        Theme.timeBackgroundDrawable.draw(paramCanvas);
        paramCanvas.save();
        paramCanvas.translate(this.photoImage.getImageX() + AndroidUtilities.dp(8.0F), this.photoImage.getImageY() + AndroidUtilities.dp(5.5F));
        this.infoLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
    }
    label2299:
    label2445:
    label2582:
    label2613:
    Object localObject4;
    for (;;)
    {
      if (this.captionLayout != null)
      {
        paramCanvas.save();
        if ((this.currentMessageObject.type == 1) || (this.documentAttachType == 4) || (this.currentMessageObject.type == 8))
        {
          i = this.photoImage.getImageX() + AndroidUtilities.dp(5.0F);
          this.captionX = i;
          f = i;
          i = this.photoImage.getImageY() + this.photoImage.getImageHeight() + AndroidUtilities.dp(6.0F);
          this.captionY = i;
          paramCanvas.translate(f, i);
          if (this.pressedLink != null) {
            i = 0;
          }
        }
        else
        {
          for (;;)
          {
            if (i < this.urlPath.size())
            {
              paramCanvas.drawPath((Path)this.urlPath.get(i), urlPaint);
              i += 1;
              continue;
              this.photoImage.setImageCoords(AndroidUtilities.dp(10.0F) + i4, i, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
              if (!this.drawImageButton) {
                break;
              }
              i = AndroidUtilities.dp(48.0F);
              this.buttonX = ((int)(this.photoImage.getImageX() + (this.photoImage.getImageWidth() - i) / 2.0F));
              this.buttonY = ((int)(this.photoImage.getImageY() + (this.photoImage.getImageHeight() - i) / 2.0F));
              this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(48.0F), this.buttonY + AndroidUtilities.dp(48.0F));
              break;
              if (!this.drawPhotoImage) {
                break label1448;
              }
              bool1 = this.photoImage.draw(paramCanvas);
              this.drawTime = this.photoImage.getVisible();
              break label1448;
              i = 5;
              break label1494;
              if (this.documentAttachType == 5)
              {
                localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
                if (this.currentMessageObject.isOutOwner())
                {
                  i = ((SharedPreferences)localObject2).getInt("theme_rfname_color", -11162801);
                  j = ((SharedPreferences)localObject2).getInt("theme_rtext_color", -16777216);
                  k = ((SharedPreferences)localObject2).getInt("theme_rtime_color", -9391780);
                  audioTitlePaint.setColor(i);
                  audioPerformerPaint.setColor(j);
                  audioTimePaint.setColor(k);
                  localObject4 = this.radialProgress;
                  if ((isDrawSelectedBackground()) || (this.buttonPressed != 0))
                  {
                    i = -2820676;
                    label2739:
                    ((RadialProgress)localObject4).setProgressColor(i);
                    this.radialProgress.draw(paramCanvas);
                    paramCanvas.save();
                    paramCanvas.translate(this.timeAudioX + this.songX, AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
                    this.songLayout.draw(paramCanvas);
                    paramCanvas.restore();
                    paramCanvas.save();
                    if (!MediaController.getInstance().isPlayingAudio(this.currentMessageObject)) {
                      break label3129;
                    }
                    paramCanvas.translate(this.seekBarX, this.seekBarY);
                    this.seekBar.draw(paramCanvas);
                    label2841:
                    paramCanvas.restore();
                    paramCanvas.save();
                    paramCanvas.translate(this.timeAudioX, AndroidUtilities.dp(57.0F) + this.namesOffset + this.mediaOffsetY);
                    this.durationLayout.draw(paramCanvas);
                    paramCanvas.restore();
                    i = ((SharedPreferences)localObject2).getInt("theme_rfname_color", -11162801);
                    j = ((SharedPreferences)localObject2).getInt("theme_lfname_color", -12940081);
                    localObject2 = Theme.docMenuDrawable[0];
                    if (!this.currentMessageObject.isOutOwner()) {
                      break label3171;
                    }
                    label2934:
                    MihanTheme.setColorFilter((Drawable)localObject2, i);
                    i = this.buttonX;
                    j = this.backgroundWidth;
                    if (this.currentMessageObject.type != 0) {
                      break label3177;
                    }
                  }
                }
                label3129:
                label3171:
                label3177:
                for (f = 58.0F;; f = 48.0F)
                {
                  i = j + i - AndroidUtilities.dp(f);
                  this.otherX = i;
                  j = this.buttonY - AndroidUtilities.dp(5.0F);
                  this.otherY = j;
                  setDrawableBounds((Drawable)localObject2, i, j);
                  ((Drawable)localObject2).draw(paramCanvas);
                  break;
                  i = -1048610;
                  break label2739;
                  i = ((SharedPreferences)localObject2).getInt("theme_lfname_color", -12940081);
                  j = ((SharedPreferences)localObject2).getInt("theme_ltext_color", -16777216);
                  k = ((SharedPreferences)localObject2).getInt("theme_ltime_color", -6182221);
                  audioTitlePaint.setColor(i);
                  audioPerformerPaint.setColor(j);
                  audioTimePaint.setColor(k);
                  localObject4 = this.radialProgress;
                  if ((isDrawSelectedBackground()) || (this.buttonPressed != 0)) {}
                  for (i = -1902337;; i = -1)
                  {
                    ((RadialProgress)localObject4).setProgressColor(i);
                    break;
                  }
                  paramCanvas.translate(this.timeAudioX + this.performerX, AndroidUtilities.dp(35.0F) + this.namesOffset + this.mediaOffsetY);
                  this.performerLayout.draw(paramCanvas);
                  break label2841;
                  i = j;
                  break label2934;
                }
              }
              if (this.documentAttachType != 3) {
                break label1988;
              }
              if (this.currentMessageObject.isOutOwner())
              {
                i = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0).getInt("theme_rtime_color", -9391780);
                audioTimePaint.setColor(i);
                localObject2 = this.radialProgress;
                if ((isDrawSelectedBackground()) || (this.buttonPressed != 0))
                {
                  i = -2820676;
                  label3255:
                  ((RadialProgress)localObject2).setProgressColor(i);
                  this.radialProgress.draw(paramCanvas);
                  paramCanvas.save();
                  if (!this.useSeekBarWaweform) {
                    break label3580;
                  }
                  paramCanvas.translate(this.seekBarX + AndroidUtilities.dp(13.0F), this.seekBarY);
                  this.seekBarWaveform.draw(paramCanvas);
                  label3310:
                  paramCanvas.restore();
                  paramCanvas.save();
                  paramCanvas.translate(this.timeAudioX, AndroidUtilities.dp(44.0F) + this.namesOffset + this.mediaOffsetY);
                  this.durationLayout.draw(paramCanvas);
                  paramCanvas.restore();
                  if ((this.currentMessageObject.type == 0) || (this.currentMessageObject.messageOwner.to_id.channel_id != 0) || (!this.currentMessageObject.isContentUnread())) {
                    break label1988;
                  }
                  localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
                  i = ((SharedPreferences)localObject2).getInt("theme_rfname_color", -11162801);
                  j = ((SharedPreferences)localObject2).getInt("theme_lfname_color", -12940081);
                  localObject2 = docBackPaint;
                  if (!this.currentMessageObject.isOutOwner()) {
                    break label3605;
                  }
                }
              }
              for (;;)
              {
                ((Paint)localObject2).setColor(i);
                paramCanvas.drawCircle(this.timeAudioX + this.timeWidthAudio + AndroidUtilities.dp(6.0F), AndroidUtilities.dp(51.0F) + this.namesOffset + this.mediaOffsetY, AndroidUtilities.dp(3.0F), docBackPaint);
                break;
                i = -1048610;
                break label3255;
                i = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0).getInt("theme_ltime_color", -6182221);
                audioTimePaint.setColor(i);
                localObject2 = this.radialProgress;
                if ((isDrawSelectedBackground()) || (this.buttonPressed != 0)) {}
                for (i = -1902337;; i = -1)
                {
                  ((RadialProgress)localObject2).setProgressColor(i);
                  break;
                }
                label3580:
                paramCanvas.translate(this.seekBarX, this.seekBarY);
                this.seekBar.draw(paramCanvas);
                break label3310;
                label3605:
                i = j;
              }
              label3611:
              if (this.infoLayout == null) {
                break label2299;
              }
              infoPaint.setColor(-1);
              localObject2 = Theme.timeBackgroundDrawable;
              i = this.photoImage.getImageX();
              j = AndroidUtilities.dp(4.0F);
              k = this.photoImage.getImageY();
              m = AndroidUtilities.dp(4.0F);
              n = this.infoWidth;
              setDrawableBounds((Drawable)localObject2, j + i, m + k, AndroidUtilities.dp(8.0F) + n, AndroidUtilities.dp(16.5F));
              Theme.timeBackgroundDrawable.draw(paramCanvas);
              paramCanvas.save();
              paramCanvas.translate(this.photoImage.getImageX() + AndroidUtilities.dp(8.0F), this.photoImage.getImageY() + AndroidUtilities.dp(5.5F));
              this.infoLayout.draw(paramCanvas);
              paramCanvas.restore();
              break label2299;
              label3760:
              if (this.currentMessageObject.type == 4)
              {
                if (this.docTitleLayout == null) {
                  break label2299;
                }
                if (this.currentMessageObject.isOutOwner())
                {
                  locationTitlePaint.setColor(-11162801);
                  localObject2 = locationAddressPaint;
                  if (isDrawSelectedBackground()) {}
                  for (;;)
                  {
                    ((TextPaint)localObject2).setColor(-10112933);
                    paramCanvas.save();
                    paramCanvas.translate(this.docTitleOffsetX + this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(10.0F), this.photoImage.getImageY() + AndroidUtilities.dp(8.0F));
                    this.docTitleLayout.draw(paramCanvas);
                    paramCanvas.restore();
                    if (this.infoLayout == null) {
                      break;
                    }
                    paramCanvas.save();
                    paramCanvas.translate(this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(10.0F), this.photoImage.getImageY() + this.docTitleLayout.getLineBottom(this.docTitleLayout.getLineCount() - 1) + AndroidUtilities.dp(13.0F));
                    this.infoLayout.draw(paramCanvas);
                    paramCanvas.restore();
                    break;
                  }
                }
                locationTitlePaint.setColor(-11625772);
                localObject2 = locationAddressPaint;
                if (isDrawSelectedBackground()) {}
                for (i = -7752511;; i = -6182221)
                {
                  ((TextPaint)localObject2).setColor(i);
                  break;
                }
              }
              if (this.currentMessageObject.type != 12) {
                break label2299;
              }
              localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
              i = ((SharedPreferences)localObject2).getInt("theme_rfname_color", -11162801);
              j = ((SharedPreferences)localObject2).getInt("theme_lfname_color", -12940081);
              m = ((SharedPreferences)localObject2).getInt("theme_rtext_color", -16777216);
              n = ((SharedPreferences)localObject2).getInt("theme_ltext_color", -16777216);
              localObject2 = contactNamePaint;
              if (this.currentMessageObject.isOutOwner())
              {
                k = i;
                label4112:
                ((TextPaint)localObject2).setColor(k);
                localObject2 = contactPhonePaint;
                if (!this.currentMessageObject.isOutOwner()) {
                  break label4366;
                }
                k = m;
                label4138:
                ((TextPaint)localObject2).setColor(k);
                if (this.titleLayout != null)
                {
                  paramCanvas.save();
                  paramCanvas.translate(this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(9.0F), AndroidUtilities.dp(16.0F) + this.namesOffset);
                  this.titleLayout.draw(paramCanvas);
                  paramCanvas.restore();
                }
                if (this.docTitleLayout != null)
                {
                  paramCanvas.save();
                  paramCanvas.translate(this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(9.0F), AndroidUtilities.dp(39.0F) + this.namesOffset);
                  this.docTitleLayout.draw(paramCanvas);
                  paramCanvas.restore();
                }
                localObject2 = Theme.docMenuDrawable[0];
                if (!this.currentMessageObject.isOutOwner()) {
                  break label4373;
                }
              }
              for (;;)
              {
                MihanTheme.setColorFilter((Drawable)localObject2, i);
                i = this.photoImage.getImageX() + this.backgroundWidth - AndroidUtilities.dp(48.0F);
                this.otherX = i;
                j = this.photoImage.getImageY() - AndroidUtilities.dp(5.0F);
                this.otherY = j;
                setDrawableBounds((Drawable)localObject2, i, j);
                ((Drawable)localObject2).draw(paramCanvas);
                break;
                k = j;
                break label4112;
                label4366:
                k = n;
                break label4138;
                label4373:
                i = j;
              }
              i = this.currentBackgroundDrawable.getBounds().left;
              if (this.currentMessageObject.isOutOwner()) {}
              for (f = 11.0F;; f = 17.0F)
              {
                i = AndroidUtilities.dp(f) + i;
                this.captionX = i;
                f = i;
                i = this.totalHeight - this.captionHeight - AndroidUtilities.dp(10.0F);
                this.captionY = i;
                paramCanvas.translate(f, i);
                break;
              }
            }
          }
        }
      }
    }
    try
    {
      this.captionLayout.draw(paramCanvas);
      paramCanvas.restore();
      if (this.documentAttachType == 1)
      {
        localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
        if (!this.currentMessageObject.isOutOwner()) {
          break label5328;
        }
        i = ((SharedPreferences)localObject2).getInt("theme_rfname_color", -11162801);
        docNamePaint.setColor(i);
        infoPaint.setColor(i);
        docBackPaint.setColor(i);
        MihanTheme.setColorFilter(Theme.docMenuDrawable[3], i);
        localObject2 = Theme.docMenuDrawable[1];
        MihanTheme.setColorFilter((Drawable)localObject2, i);
        if (!this.drawPhotoImage) {
          break label5597;
        }
        if (this.currentMessageObject.type != 0) {
          break label5402;
        }
        i = this.photoImage.getImageX() + this.backgroundWidth - AndroidUtilities.dp(56.0F);
        this.otherX = i;
        j = this.photoImage.getImageY() + AndroidUtilities.dp(1.0F);
        this.otherY = j;
        setDrawableBounds((Drawable)localObject2, i, j);
        k = this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(10.0F);
        j = this.photoImage.getImageY() + AndroidUtilities.dp(8.0F);
        m = this.photoImage.getImageY() + this.docTitleLayout.getLineBottom(this.docTitleLayout.getLineCount() - 1) + AndroidUtilities.dp(13.0F);
        if ((this.buttonState >= 0) && (this.buttonState < 4))
        {
          if (bool1) {
            break label5500;
          }
          i = this.buttonState;
          if (this.buttonState != 0) {
            break label5465;
          }
          if (!this.currentMessageObject.isOutOwner()) {
            break label5459;
          }
          i = 7;
          localObject4 = this.radialProgress;
          localObject5 = Theme.photoStatesDrawables[i];
          if ((!isDrawSelectedBackground()) && (this.buttonPressed == 0)) {
            break label5495;
          }
          i = 1;
          ((RadialProgress)localObject4).swapBackground(localObject5[i]);
        }
        if (bool1) {
          break label5567;
        }
        this.rect.set(this.photoImage.getImageX(), this.photoImage.getImageY(), this.photoImage.getImageX() + this.photoImage.getImageWidth(), this.photoImage.getImageY() + this.photoImage.getImageHeight());
        paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(3.0F), AndroidUtilities.dp(3.0F), docBackPaint);
        if (!this.currentMessageObject.isOutOwner()) {
          break label5531;
        }
        localObject4 = this.radialProgress;
        if (!isDrawSelectedBackground()) {
          break label5524;
        }
        i = -3806041;
        ((RadialProgress)localObject4).setProgressColor(i);
        i = m;
        ((Drawable)localObject2).draw(paramCanvas);
      }
    }
    catch (Exception localException3)
    {
      try
      {
        if (this.docTitleLayout != null)
        {
          paramCanvas.save();
          paramCanvas.translate(this.docTitleOffsetX + k, j);
          this.docTitleLayout.draw(paramCanvas);
          paramCanvas.restore();
        }
      }
      catch (Exception localException3)
      {
        try
        {
          for (;;)
          {
            Object localObject5;
            if (this.infoLayout != null)
            {
              paramCanvas.save();
              paramCanvas.translate(k, i);
              this.infoLayout.draw(paramCanvas);
              paramCanvas.restore();
            }
            if ((this.drawImageButton) && (this.photoImage.getVisible())) {
              this.radialProgress.draw(paramCanvas);
            }
            if (this.botButtons.isEmpty()) {
              return;
            }
            if (!this.currentMessageObject.isOutOwner()) {
              break;
            }
            i = getMeasuredWidth() - this.widthForButtons - AndroidUtilities.dp(10.0F);
            j = 0;
            for (;;)
            {
              if (j >= this.botButtons.size()) {
                return;
              }
              localObject4 = (BotButton)this.botButtons.get(j);
              m = ((BotButton)localObject4).y + this.layoutHeight - AndroidUtilities.dp(2.0F);
              localObject5 = Theme.systemDrawable;
              if (j != this.pressedBotButton) {
                break;
              }
              localObject2 = Theme.colorPressedFilter;
              ((Drawable)localObject5).setColorFilter((ColorFilter)localObject2);
              Theme.systemDrawable.setBounds(((BotButton)localObject4).x + i, m, ((BotButton)localObject4).x + i + ((BotButton)localObject4).width, ((BotButton)localObject4).height + m);
              Theme.systemDrawable.draw(paramCanvas);
              paramCanvas.save();
              paramCanvas.translate(((BotButton)localObject4).x + i + AndroidUtilities.dp(5.0F), (AndroidUtilities.dp(44.0F) - ((BotButton)localObject4).title.getLineBottom(((BotButton)localObject4).title.getLineCount() - 1)) / 2 + m);
              ((BotButton)localObject4).title.draw(paramCanvas);
              paramCanvas.restore();
              if (!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonUrl)) {
                break label5876;
              }
              k = ((BotButton)localObject4).x;
              n = ((BotButton)localObject4).width;
              i1 = AndroidUtilities.dp(3.0F);
              i2 = Theme.botLink.getIntrinsicWidth();
              setDrawableBounds(Theme.botLink, k + n - i1 - i2 + i, AndroidUtilities.dp(3.0F) + m);
              Theme.botLink.draw(paramCanvas);
              j += 1;
            }
            localException2 = localException2;
            FileLog.e("tmessages", localException2);
            continue;
            label5328:
            j = localException2.getInt("theme_lfname_color", -12940081);
            docNamePaint.setColor(j);
            infoPaint.setColor(j);
            docBackPaint.setColor(j);
            Object localObject3 = Theme.docMenuDrawable;
            if (isDrawSelectedBackground()) {}
            for (i = 2;; i = 0)
            {
              localObject3 = localObject3[i];
              MihanTheme.setColorFilter((Drawable)localObject3, j);
              break;
            }
            label5402:
            i = this.photoImage.getImageX() + this.backgroundWidth - AndroidUtilities.dp(40.0F);
            this.otherX = i;
            j = this.photoImage.getImageY() + AndroidUtilities.dp(1.0F);
            this.otherY = j;
            setDrawableBounds((Drawable)localObject3, i, j);
            continue;
            label5459:
            i = 10;
            continue;
            label5465:
            if (this.buttonState == 1)
            {
              if (this.currentMessageObject.isOutOwner()) {}
              for (i = 8;; i = 11) {
                break;
              }
              label5495:
              i = 0;
              continue;
              label5500:
              this.radialProgress.swapBackground(Theme.photoStatesDrawables[this.buttonState][this.buttonPressed]);
              continue;
              label5524:
              i = -2427453;
              continue;
              label5531:
              localObject4 = this.radialProgress;
              if (isDrawSelectedBackground()) {}
              for (i = -3413258;; i = -1314571)
              {
                ((RadialProgress)localObject4).setProgressColor(i);
                i = m;
                break;
              }
              label5567:
              if (this.buttonState == -1) {
                this.radialProgress.setHideCurrentDrawable(true);
              }
              this.radialProgress.setProgressColor(-1);
              i = m;
              continue;
              label5597:
              i = this.buttonX;
              j = this.backgroundWidth;
              if (this.currentMessageObject.type == 0)
              {
                f = 58.0F;
                label5622:
                i = j + i - AndroidUtilities.dp(f);
                this.otherX = i;
                j = this.buttonY - AndroidUtilities.dp(5.0F);
                this.otherY = j;
                setDrawableBounds((Drawable)localObject3, i, j);
                k = this.buttonX + AndroidUtilities.dp(53.0F);
                j = this.buttonY + AndroidUtilities.dp(4.0F);
                m = this.buttonY + AndroidUtilities.dp(27.0F);
                if (!this.currentMessageObject.isOutOwner()) {
                  break label5764;
                }
                localObject4 = this.radialProgress;
                if ((!isDrawSelectedBackground()) && (this.buttonPressed == 0)) {
                  break label5757;
                }
              }
              label5757:
              for (i = -2820676;; i = -1048610)
              {
                ((RadialProgress)localObject4).setProgressColor(i);
                i = m;
                break;
                f = 48.0F;
                break label5622;
              }
              label5764:
              localObject4 = this.radialProgress;
              if ((isDrawSelectedBackground()) || (this.buttonPressed != 0)) {}
              for (i = -1902337;; i = -1)
              {
                ((RadialProgress)localObject4).setProgressColor(i);
                i = m;
                break;
              }
              localException3 = localException3;
              FileLog.e("tmessages", localException3);
            }
          }
        }
        catch (Exception localException4)
        {
          label5876:
          label6039:
          do
          {
            do
            {
              for (;;)
              {
                FileLog.e("tmessages", localException4);
                continue;
                i = this.currentBackgroundDrawable.getBounds().left;
                if (this.mediaBackground) {}
                for (f = 1.0F;; f = 7.0F)
                {
                  i += AndroidUtilities.dp(f);
                  break;
                }
                PorterDuffColorFilter localPorterDuffColorFilter = Theme.colorFilter;
                continue;
                if (!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonSwitchInline)) {
                  break;
                }
                k = ((BotButton)localObject4).x;
                n = ((BotButton)localObject4).width;
                i1 = AndroidUtilities.dp(3.0F);
                i2 = Theme.botInline.getIntrinsicWidth();
                setDrawableBounds(Theme.botInline, k + n - i1 - i2 + i, AndroidUtilities.dp(3.0F) + m);
                Theme.botInline.draw(paramCanvas);
              }
            } while ((!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonCallback)) && (!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation)));
            if (((!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonCallback)) || (!SendMessagesHelper.getInstance().isSendingCallback(this.currentMessageObject, ((BotButton)localObject4).button))) && ((!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation)) || (!SendMessagesHelper.getInstance().isSendingCurrentLocation(this.currentMessageObject, ((BotButton)localObject4).button)))) {
              break;
            }
            k = 1;
          } while ((k == 0) && ((k != 0) || (((BotButton)localObject4).progressAlpha == 0.0F)));
          botProgressPaint.setAlpha(Math.min(255, (int)(((BotButton)localObject4).progressAlpha * 255.0F)));
          n = ((BotButton)localObject4).x + ((BotButton)localObject4).width - AndroidUtilities.dp(12.0F) + i;
          this.rect.set(n, AndroidUtilities.dp(4.0F) + m, AndroidUtilities.dp(8.0F) + n, AndroidUtilities.dp(12.0F) + m);
          paramCanvas.drawArc(this.rect, ((BotButton)localObject4).angle, 220.0F, false, botProgressPaint);
          invalidate((int)this.rect.left - AndroidUtilities.dp(2.0F), (int)this.rect.top - AndroidUtilities.dp(2.0F), (int)this.rect.right + AndroidUtilities.dp(2.0F), (int)this.rect.bottom + AndroidUtilities.dp(2.0F));
          l1 = System.currentTimeMillis();
          if (Math.abs(((BotButton)localObject4).lastUpdateTime - System.currentTimeMillis()) < 1000L)
          {
            l2 = l1 - ((BotButton)localObject4).lastUpdateTime;
            BotButton.access$716((BotButton)localObject4, (float)(360L * l2) / 2000.0F);
            BotButton.access$720((BotButton)localObject4, ((BotButton)localObject4).angle / 360 * 360);
            if (k == 0) {
              break label6352;
            }
            if (((BotButton)localObject4).progressAlpha < 1.0F)
            {
              BotButton.access$616((BotButton)localObject4, (float)l2 / 200.0F);
              if (((BotButton)localObject4).progressAlpha > 1.0F) {
                BotButton.access$602((BotButton)localObject4, 1.0F);
              }
            }
          }
          for (;;)
          {
            BotButton.access$802((BotButton)localObject4, l1);
            break;
            k = 0;
            break label6039;
            label6352:
            if (((BotButton)localObject4).progressAlpha > 0.0F)
            {
              BotButton.access$624((BotButton)localObject4, (float)l2 / 200.0F);
              if (((BotButton)localObject4).progressAlpha < 0.0F) {
                BotButton.access$602((BotButton)localObject4, 0.0F);
              }
            }
          }
        }
      }
    }
  }
  
  public static StaticLayout generateStaticLayout(CharSequence paramCharSequence, TextPaint paramTextPaint, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(paramCharSequence);
    int j = 0;
    StaticLayout localStaticLayout = new StaticLayout(paramCharSequence, paramTextPaint, paramInt2, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
    int i = 0;
    int m = paramInt1;
    int k;
    if (i < paramInt3)
    {
      localStaticLayout.getLineDirections(i);
      if ((localStaticLayout.getLineLeft(i) != 0.0F) || (localStaticLayout.isRtlCharAt(localStaticLayout.getLineStart(i))) || (localStaticLayout.isRtlCharAt(localStaticLayout.getLineEnd(i)))) {
        paramInt1 = paramInt2;
      }
      k = localStaticLayout.getLineEnd(i);
      if (k == paramCharSequence.length()) {
        m = paramInt1;
      }
    }
    else
    {
      label119:
      return StaticLayoutEx.createStaticLayout(localSpannableStringBuilder, paramTextPaint, m, Layout.Alignment.ALIGN_NORMAL, 1.0F, AndroidUtilities.dp(1.0F), false, TextUtils.TruncateAt.END, m, paramInt4);
    }
    m = k - 1;
    if (localSpannableStringBuilder.charAt(m + j) == ' ')
    {
      localSpannableStringBuilder.replace(m + j, m + j + 1, "\n");
      k = j;
    }
    for (;;)
    {
      m = paramInt1;
      if (i == localStaticLayout.getLineCount() - 1) {
        break label119;
      }
      m = paramInt1;
      if (i == paramInt4 - 1) {
        break label119;
      }
      i += 1;
      j = k;
      break;
      k = j;
      if (localSpannableStringBuilder.charAt(m + j) != '\n')
      {
        localSpannableStringBuilder.insert(m + j, "\n");
        k = j + 1;
      }
    }
  }
  
  private Drawable getDrawableForCurrentState()
  {
    int i = 3;
    int k = 1;
    int j = 0;
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    PorterDuffColorFilter localPorterDuffColorFilter = new PorterDuffColorFilter(((SharedPreferences)localObject).getInt("theme_rfname_color", -11162801), PorterDuff.Mode.SRC_IN);
    Theme.fileStatesDrawable[0][0].setColorFilter(localPorterDuffColorFilter);
    Theme.fileStatesDrawable[0][1].setColorFilter(localPorterDuffColorFilter);
    Theme.fileStatesDrawable[1][0].setColorFilter(localPorterDuffColorFilter);
    Theme.fileStatesDrawable[1][1].setColorFilter(localPorterDuffColorFilter);
    Theme.fileStatesDrawable[2][0].setColorFilter(localPorterDuffColorFilter);
    Theme.fileStatesDrawable[2][1].setColorFilter(localPorterDuffColorFilter);
    Theme.fileStatesDrawable[3][0].setColorFilter(localPorterDuffColorFilter);
    Theme.fileStatesDrawable[3][1].setColorFilter(localPorterDuffColorFilter);
    Theme.fileStatesDrawable[4][0].setColorFilter(localPorterDuffColorFilter);
    Theme.fileStatesDrawable[4][1].setColorFilter(localPorterDuffColorFilter);
    localObject = new PorterDuffColorFilter(((SharedPreferences)localObject).getInt("theme_lfname_color", -12940081), PorterDuff.Mode.MULTIPLY);
    Theme.fileStatesDrawable[5][0].setColorFilter((ColorFilter)localObject);
    Theme.fileStatesDrawable[5][1].setColorFilter((ColorFilter)localObject);
    Theme.fileStatesDrawable[6][0].setColorFilter((ColorFilter)localObject);
    Theme.fileStatesDrawable[6][1].setColorFilter((ColorFilter)localObject);
    Theme.fileStatesDrawable[7][0].setColorFilter((ColorFilter)localObject);
    Theme.fileStatesDrawable[7][1].setColorFilter((ColorFilter)localObject);
    Theme.fileStatesDrawable[8][0].setColorFilter((ColorFilter)localObject);
    Theme.fileStatesDrawable[8][1].setColorFilter((ColorFilter)localObject);
    Theme.fileStatesDrawable[9][0].setColorFilter((ColorFilter)localObject);
    Theme.fileStatesDrawable[9][1].setColorFilter((ColorFilter)localObject);
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
    {
      if (this.buttonState == -1) {
        return null;
      }
      this.radialProgress.setAlphaForPrevious(false);
      localObject = Theme.fileStatesDrawable;
      if (this.currentMessageObject.isOutOwner())
      {
        i = this.buttonState;
        localObject = localObject[i];
        if ((!isDrawSelectedBackground()) && (this.buttonPressed == 0)) {
          break label407;
        }
      }
      label407:
      for (i = 1;; i = 0)
      {
        return localObject[i];
        i = this.buttonState + 5;
        break;
      }
    }
    if ((this.documentAttachType == 1) && (!this.drawPhotoImage))
    {
      this.radialProgress.setAlphaForPrevious(false);
      if (this.buttonState == -1)
      {
        localObject = Theme.fileStatesDrawable;
        if (this.currentMessageObject.isOutOwner()) {}
        for (;;)
        {
          return localObject[i][0];
          i = 8;
        }
      }
      if (this.buttonState == 0)
      {
        localObject = Theme.fileStatesDrawable;
        if (this.currentMessageObject.isOutOwner()) {}
        for (i = 2;; i = 7) {
          return localObject[i][0];
        }
      }
      if (this.buttonState == 1)
      {
        localObject = Theme.fileStatesDrawable;
        if (this.currentMessageObject.isOutOwner()) {}
        for (i = 4;; i = 9) {
          return localObject[i][0];
        }
      }
    }
    else
    {
      this.radialProgress.setAlphaForPrevious(true);
      if ((this.buttonState >= 0) && (this.buttonState < 4))
      {
        if (this.documentAttachType == 1)
        {
          i = this.buttonState;
          if (this.buttonState == 0) {
            if (this.currentMessageObject.isOutOwner()) {
              i = 7;
            }
          }
          while (this.buttonState != 1) {
            for (;;)
            {
              localObject = Theme.photoStatesDrawables[i];
              if (!isDrawSelectedBackground())
              {
                i = j;
                if (this.buttonPressed == 0) {}
              }
              else
              {
                i = 1;
              }
              return localObject[i];
              i = 10;
            }
          }
          if (this.currentMessageObject.isOutOwner()) {}
          for (i = 8;; i = 11) {
            break;
          }
        }
        return Theme.photoStatesDrawables[this.buttonState][this.buttonPressed];
      }
      if ((this.buttonState == -1) && (this.documentAttachType == 1))
      {
        localObject = Theme.photoStatesDrawables;
        if (this.currentMessageObject.isOutOwner())
        {
          i = 9;
          localObject = localObject[i];
          if (!isDrawSelectedBackground()) {
            break label742;
          }
        }
        label742:
        for (i = k;; i = 0)
        {
          return localObject[i];
          i = 12;
          break;
        }
      }
    }
    return null;
  }
  
  private int getMaxNameWidth()
  {
    if (this.documentAttachType == 6)
    {
      if (AndroidUtilities.isTablet()) {
        if ((this.isChat) && (!this.currentMessageObject.isOutOwner()) && (this.currentMessageObject.isFromUser())) {
          i = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(42.0F);
        }
      }
      for (;;)
      {
        return i - this.backgroundWidth - AndroidUtilities.dp(57.0F);
        i = AndroidUtilities.getMinTabletSide();
        continue;
        if ((this.isChat) && (!this.currentMessageObject.isOutOwner()) && (this.currentMessageObject.isFromUser())) {
          i = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(42.0F);
        } else {
          i = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
        }
      }
    }
    int i = this.backgroundWidth;
    if (this.mediaBackground) {}
    for (float f = 22.0F;; f = 31.0F) {
      return i - AndroidUtilities.dp(f);
    }
  }
  
  private boolean intersect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if (paramFloat1 <= paramFloat3) {
      if (paramFloat2 < paramFloat3) {}
    }
    while (paramFloat1 <= paramFloat4)
    {
      return true;
      return false;
    }
    return false;
  }
  
  private boolean isDrawSelectedBackground()
  {
    return ((isPressed()) && (this.isCheckPressed)) || ((!this.isCheckPressed) && (this.isPressed)) || (this.isHighlighted);
  }
  
  private boolean isPhotoDataChanged(MessageObject paramMessageObject)
  {
    if ((paramMessageObject.type == 0) || (paramMessageObject.type == 14)) {
      return false;
    }
    if (paramMessageObject.type == 4)
    {
      if (this.currentUrl == null) {
        return true;
      }
      double d1 = paramMessageObject.messageOwner.media.geo.lat;
      double d2 = paramMessageObject.messageOwner.media.geo._long;
      if (!String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=100x100&maptype=roadmap&scale=%d&markers=color:red|size:mid|%f,%f&sensor=false", new Object[] { Double.valueOf(d1), Double.valueOf(d2), Integer.valueOf(Math.min(2, (int)Math.ceil(AndroidUtilities.density))), Double.valueOf(d1), Double.valueOf(d2) }).equals(this.currentUrl)) {
        return true;
      }
    }
    else
    {
      if ((this.currentPhotoObject == null) || ((this.currentPhotoObject.location instanceof TLRPC.TL_fileLocationUnavailable))) {
        return true;
      }
      if ((this.currentMessageObject != null) && (this.photoNotSet) && (FileLoader.getPathToMessage(this.currentMessageObject.messageOwner).exists())) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isUserDataChanged()
  {
    boolean bool2 = false;
    if ((this.currentMessageObject != null) && (!this.hasLinkPreview) && (this.currentMessageObject.messageOwner.media != null) && ((this.currentMessageObject.messageOwner.media.webpage instanceof TLRPC.TL_webPage))) {}
    label160:
    label197:
    label618:
    label647:
    label649:
    label664:
    for (;;)
    {
      return true;
      if ((this.currentMessageObject == null) || ((this.currentUser == null) && (this.currentChat == null))) {
        return false;
      }
      if ((this.lastSendState == this.currentMessageObject.messageOwner.send_state) && (this.lastDeleteDate == this.currentMessageObject.messageOwner.destroyTime) && (this.lastViewsCount == this.currentMessageObject.messageOwner.views))
      {
        Object localObject2 = null;
        Object localObject1 = null;
        Object localObject3;
        Object localObject4;
        if (this.currentMessageObject.isFromUser())
        {
          localObject3 = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
          localObject4 = null;
          localObject2 = localObject4;
          if (this.isAvatarVisible)
          {
            if ((localObject3 == null) || (((TLRPC.User)localObject3).photo == null)) {
              break label618;
            }
            localObject2 = ((TLRPC.User)localObject3).photo.photo_small;
          }
          if (((this.replyTextLayout == null) && (this.currentMessageObject.replyMessageObject != null)) || ((this.currentPhoto == null) && (localObject2 != null)) || ((this.currentPhoto != null) && (localObject2 == null)) || ((this.currentPhoto != null) && (localObject2 != null) && ((this.currentPhoto.local_id != ((TLRPC.FileLocation)localObject2).local_id) || (this.currentPhoto.volume_id != ((TLRPC.FileLocation)localObject2).volume_id)))) {
            break label647;
          }
          localObject4 = null;
          localObject2 = localObject4;
          if (this.currentMessageObject.replyMessageObject != null)
          {
            TLRPC.PhotoSize localPhotoSize = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.replyMessageObject.photoThumbs, 80);
            localObject2 = localObject4;
            if (localPhotoSize != null)
            {
              localObject2 = localObject4;
              if (this.currentMessageObject.replyMessageObject.type != 13) {
                localObject2 = localPhotoSize.location;
              }
            }
          }
          if ((this.currentReplyPhoto == null) && (localObject2 != null)) {
            continue;
          }
          localObject4 = null;
          localObject2 = localObject4;
          if (this.drawName)
          {
            localObject2 = localObject4;
            if (this.isChat)
            {
              localObject2 = localObject4;
              if (!this.currentMessageObject.isOutOwner())
              {
                if (localObject3 == null) {
                  break label649;
                }
                localObject2 = UserObject.getUserName((TLRPC.User)localObject3);
              }
            }
          }
        }
        for (;;)
        {
          if (((this.currentNameString == null) && (localObject2 != null)) || ((this.currentNameString != null) && (localObject2 == null)) || ((this.currentNameString != null) && (localObject2 != null) && (!this.currentNameString.equals(localObject2)))) {
            break label664;
          }
          if (!this.drawForwardedName) {
            break label666;
          }
          localObject1 = this.currentMessageObject.getForwardedName();
          boolean bool1;
          if (((this.currentForwardNameString != null) || (localObject1 == null)) && ((this.currentForwardNameString == null) || (localObject1 != null)))
          {
            bool1 = bool2;
            if (this.currentForwardNameString != null)
            {
              bool1 = bool2;
              if (localObject1 != null)
              {
                bool1 = bool2;
                if (this.currentForwardNameString.equals(localObject1)) {}
              }
            }
          }
          else
          {
            bool1 = true;
          }
          return bool1;
          if (this.currentMessageObject.messageOwner.from_id < 0)
          {
            localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(-this.currentMessageObject.messageOwner.from_id));
            localObject3 = localObject2;
            break label160;
          }
          localObject3 = localObject2;
          if (!this.currentMessageObject.messageOwner.post) {
            break label160;
          }
          localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(this.currentMessageObject.messageOwner.to_id.channel_id));
          localObject3 = localObject2;
          break label160;
          localObject2 = localObject4;
          if (localObject1 == null) {
            break label197;
          }
          localObject2 = localObject4;
          if (((TLRPC.Chat)localObject1).photo == null) {
            break label197;
          }
          localObject2 = ((TLRPC.Chat)localObject1).photo.photo_small;
          break label197;
          break;
          localObject2 = localObject4;
          if (localObject1 != null) {
            localObject2 = ((TLRPC.Chat)localObject1).title;
          }
        }
      }
    }
    label666:
    return false;
  }
  
  private void measureTime(MessageObject paramMessageObject)
  {
    int i;
    TLRPC.User localUser;
    int j;
    Object localObject;
    if ((!paramMessageObject.isOutOwner()) && (paramMessageObject.messageOwner.from_id > 0) && (paramMessageObject.messageOwner.post))
    {
      i = 1;
      localUser = MessagesController.getInstance().getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id));
      j = i;
      if (i != 0)
      {
        j = i;
        if (localUser == null) {
          j = 0;
        }
      }
      localObject = null;
      if (this.currentMessageObject.isFromUser()) {
        localObject = MessagesController.getInstance().getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id));
      }
      if ((paramMessageObject.messageOwner.via_bot_id != 0) || (paramMessageObject.messageOwner.via_bot_name != null) || ((localObject != null) && (((TLRPC.User)localObject).bot)) || ((paramMessageObject.messageOwner.flags & 0x8000) == 0)) {
        break label498;
      }
      localObject = LocaleController.getString("EditedMessage", 2131165641) + " " + LocaleController.getInstance().formatterDay.format(paramMessageObject.messageOwner.date * 1000L);
      label194:
      if (j == 0) {
        break label524;
      }
    }
    label498:
    label524:
    for (this.currentTimeString = (", " + (String)localObject);; this.currentTimeString = ((String)localObject))
    {
      i = (int)Math.ceil(timePaint.measureText(this.currentTimeString));
      this.timeWidth = i;
      this.timeTextWidth = i;
      if ((paramMessageObject.messageOwner.flags & 0x400) != 0)
      {
        this.currentViewsString = String.format("%s", new Object[] { LocaleController.formatShortNumber(Math.max(1, paramMessageObject.messageOwner.views), null) });
        this.viewsTextWidth = ((int)Math.ceil(timePaint.measureText(this.currentViewsString)));
        this.timeWidth += this.viewsTextWidth + Theme.viewsCountDrawable[0].getIntrinsicWidth() + AndroidUtilities.dp(10.0F);
      }
      if (j != 0)
      {
        if (this.availableTimeWidth == 0) {
          this.availableTimeWidth = AndroidUtilities.dp(1000.0F);
        }
        localObject = ContactsController.formatName(localUser.first_name, localUser.last_name).replace('\n', ' ');
        j = this.availableTimeWidth - this.timeWidth;
        int k = (int)Math.ceil(timePaint.measureText((CharSequence)localObject, 0, ((CharSequence)localObject).length()));
        paramMessageObject = (MessageObject)localObject;
        i = k;
        if (k > j)
        {
          paramMessageObject = TextUtils.ellipsize((CharSequence)localObject, timePaint, j, TextUtils.TruncateAt.END);
          i = j;
        }
        this.currentTimeString = (paramMessageObject + this.currentTimeString);
        this.timeTextWidth += i;
        this.timeWidth += i;
      }
      return;
      i = 0;
      break;
      localObject = LocaleController.getInstance().formatterDay.format(paramMessageObject.messageOwner.date * 1000L);
      break label194;
    }
  }
  
  private LinkPath obtainNewUrlPath(boolean paramBoolean)
  {
    LinkPath localLinkPath;
    if (!this.urlPathCache.isEmpty())
    {
      localLinkPath = (LinkPath)this.urlPathCache.get(0);
      this.urlPathCache.remove(0);
    }
    while (paramBoolean)
    {
      this.urlPathSelection.add(localLinkPath);
      return localLinkPath;
      localLinkPath = new LinkPath();
    }
    this.urlPath.add(localLinkPath);
    return localLinkPath;
  }
  
  private void resetPressedLink(int paramInt)
  {
    if ((this.pressedLink == null) || ((this.pressedLinkType != paramInt) && (paramInt != -1))) {
      return;
    }
    resetUrlPaths(false);
    this.pressedLink = null;
    this.pressedLinkType = -1;
    invalidate();
  }
  
  private void resetUrlPaths(boolean paramBoolean)
  {
    if (paramBoolean) {
      if (!this.urlPathSelection.isEmpty()) {}
    }
    while (this.urlPath.isEmpty())
    {
      return;
      this.urlPathCache.addAll(this.urlPathSelection);
      this.urlPathSelection.clear();
      return;
    }
    this.urlPathCache.addAll(this.urlPath);
    this.urlPath.clear();
  }
  
  /* Error */
  private void setMessageObjectInternal(MessageObject paramMessageObject)
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4: getfield 1889	org/telegram/tgnet/TLRPC$Message:flags	I
    //   7: sipush 1024
    //   10: iand
    //   11: ifeq +44 -> 55
    //   14: aload_0
    //   15: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   18: invokevirtual 1557	org/telegram/messenger/MessageObject:isContentUnread	()Z
    //   21: ifeq +1919 -> 1940
    //   24: aload_0
    //   25: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   28: invokevirtual 879	org/telegram/messenger/MessageObject:isOut	()Z
    //   31: ifne +1909 -> 1940
    //   34: invokestatic 887	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   37: aload_0
    //   38: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   41: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   44: iconst_0
    //   45: invokevirtual 1963	org/telegram/messenger/MessagesController:addToViewsQueue	(Lorg/telegram/tgnet/TLRPC$Message;Z)V
    //   48: aload_0
    //   49: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   52: invokevirtual 1966	org/telegram/messenger/MessageObject:setContentIsRead	()V
    //   55: aload_0
    //   56: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   59: invokevirtual 882	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   62: ifeq +1913 -> 1975
    //   65: aload_0
    //   66: invokestatic 887	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   69: aload_0
    //   70: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   73: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   76: getfield 890	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   79: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   82: invokevirtual 900	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   85: putfield 1814	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   88: aload_0
    //   89: getfield 1734	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   92: ifeq +84 -> 176
    //   95: aload_1
    //   96: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   99: ifne +77 -> 176
    //   102: aload_1
    //   103: invokevirtual 882	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   106: ifeq +70 -> 176
    //   109: aload_0
    //   110: iconst_1
    //   111: putfield 1830	org/telegram/ui/Cells/ChatMessageCell:isAvatarVisible	Z
    //   114: aload_0
    //   115: getfield 1814	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   118: ifnull +1947 -> 2065
    //   121: aload_0
    //   122: getfield 1814	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   125: getfield 1834	org/telegram/tgnet/TLRPC$User:photo	Lorg/telegram/tgnet/TLRPC$UserProfilePhoto;
    //   128: ifnull +1929 -> 2057
    //   131: aload_0
    //   132: aload_0
    //   133: getfield 1814	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   136: getfield 1834	org/telegram/tgnet/TLRPC$User:photo	Lorg/telegram/tgnet/TLRPC$UserProfilePhoto;
    //   139: getfield 1839	org/telegram/tgnet/TLRPC$UserProfilePhoto:photo_small	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   142: putfield 1846	org/telegram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   145: aload_0
    //   146: getfield 433	org/telegram/ui/Cells/ChatMessageCell:avatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   149: aload_0
    //   150: getfield 1814	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   153: invokevirtual 1970	org/telegram/ui/Components/AvatarDrawable:setInfo	(Lorg/telegram/tgnet/TLRPC$User;)V
    //   156: aload_0
    //   157: getfield 424	org/telegram/ui/Cells/ChatMessageCell:avatarImage	Lorg/telegram/messenger/ImageReceiver;
    //   160: aload_0
    //   161: getfield 1846	org/telegram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   164: ldc_w 1972
    //   167: aload_0
    //   168: getfield 433	org/telegram/ui/Cells/ChatMessageCell:avatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   171: aconst_null
    //   172: iconst_0
    //   173: invokevirtual 1975	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Ljava/lang/String;Z)V
    //   176: aload_0
    //   177: aload_1
    //   178: invokespecial 1073	org/telegram/ui/Cells/ChatMessageCell:measureTime	(Lorg/telegram/messenger/MessageObject;)V
    //   181: aload_0
    //   182: iconst_0
    //   183: putfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   186: aconst_null
    //   187: astore 7
    //   189: aconst_null
    //   190: astore 8
    //   192: aload_1
    //   193: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   196: getfield 949	org/telegram/tgnet/TLRPC$Message:via_bot_id	I
    //   199: ifeq +1944 -> 2143
    //   202: invokestatic 887	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   205: aload_1
    //   206: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   209: getfield 949	org/telegram/tgnet/TLRPC$Message:via_bot_id	I
    //   212: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   215: invokevirtual 900	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   218: astore 9
    //   220: aload 8
    //   222: astore 5
    //   224: aload 7
    //   226: astore 6
    //   228: aload 9
    //   230: ifnull +115 -> 345
    //   233: aload 8
    //   235: astore 5
    //   237: aload 7
    //   239: astore 6
    //   241: aload 9
    //   243: getfield 1976	org/telegram/tgnet/TLRPC$User:username	Ljava/lang/String;
    //   246: ifnull +99 -> 345
    //   249: aload 8
    //   251: astore 5
    //   253: aload 7
    //   255: astore 6
    //   257: aload 9
    //   259: getfield 1976	org/telegram/tgnet/TLRPC$User:username	Ljava/lang/String;
    //   262: invokevirtual 937	java/lang/String:length	()I
    //   265: ifle +80 -> 345
    //   268: new 1163	java/lang/StringBuilder
    //   271: dup
    //   272: invokespecial 1164	java/lang/StringBuilder:<init>	()V
    //   275: ldc_w 1978
    //   278: invokevirtual 1170	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   281: aload 9
    //   283: getfield 1976	org/telegram/tgnet/TLRPC$User:username	Ljava/lang/String;
    //   286: invokevirtual 1170	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   289: invokevirtual 1181	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   292: astore 6
    //   294: ldc_w 1980
    //   297: iconst_1
    //   298: anewarray 1152	java/lang/Object
    //   301: dup
    //   302: iconst_0
    //   303: aload 6
    //   305: aastore
    //   306: invokestatic 1156	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   309: invokestatic 1984	org/telegram/messenger/AndroidUtilities:replaceTags	(Ljava/lang/String;)Landroid/text/SpannableStringBuilder;
    //   312: astore 5
    //   314: aload_0
    //   315: getstatic 396	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   318: aload 5
    //   320: iconst_0
    //   321: aload 5
    //   323: invokeinterface 1033 1 0
    //   328: invokevirtual 1941	android/text/TextPaint:measureText	(Ljava/lang/CharSequence;II)F
    //   331: f2d
    //   332: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   335: d2i
    //   336: putfield 1986	org/telegram/ui/Cells/ChatMessageCell:viaWidth	I
    //   339: aload_0
    //   340: aload 9
    //   342: putfield 1988	org/telegram/ui/Cells/ChatMessageCell:currentViaBotUser	Lorg/telegram/tgnet/TLRPC$User;
    //   345: aload_0
    //   346: getfield 1859	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   349: ifeq +1909 -> 2258
    //   352: aload_0
    //   353: getfield 1734	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   356: ifeq +1902 -> 2258
    //   359: aload_0
    //   360: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   363: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   366: ifne +1892 -> 2258
    //   369: iconst_1
    //   370: istore_3
    //   371: aload_1
    //   372: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   375: getfield 871	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$TL_messageFwdHeader;
    //   378: ifnull +12 -> 390
    //   381: aload_1
    //   382: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   385: bipush 14
    //   387: if_icmpne +1876 -> 2263
    //   390: aload 6
    //   392: ifnull +1871 -> 2263
    //   395: iconst_1
    //   396: istore_2
    //   397: iload_3
    //   398: ifne +7 -> 405
    //   401: iload_2
    //   402: ifeq +2033 -> 2435
    //   405: aload_0
    //   406: iconst_1
    //   407: putfield 1990	org/telegram/ui/Cells/ChatMessageCell:drawNameLayout	Z
    //   410: aload_0
    //   411: aload_0
    //   412: invokespecial 1992	org/telegram/ui/Cells/ChatMessageCell:getMaxNameWidth	()I
    //   415: putfield 1994	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   418: aload_0
    //   419: getfield 1994	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   422: ifge +13 -> 435
    //   425: aload_0
    //   426: ldc_w 1995
    //   429: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   432: putfield 1994	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   435: iload_3
    //   436: ifeq +1863 -> 2299
    //   439: aload_0
    //   440: getfield 1814	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   443: ifnull +1825 -> 2268
    //   446: aload_0
    //   447: aload_0
    //   448: getfield 1814	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   451: invokestatic 1865	org/telegram/messenger/UserObject:getUserName	(Lorg/telegram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   454: putfield 1867	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   457: aload_0
    //   458: getfield 1867	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   461: bipush 10
    //   463: bipush 32
    //   465: invokevirtual 1115	java/lang/String:replace	(CC)Ljava/lang/String;
    //   468: astore 7
    //   470: getstatic 391	org/telegram/ui/Cells/ChatMessageCell:namePaint	Landroid/text/TextPaint;
    //   473: astore 8
    //   475: aload_0
    //   476: getfield 1994	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   479: istore 4
    //   481: iload_2
    //   482: ifeq +1827 -> 2309
    //   485: aload_0
    //   486: getfield 1986	org/telegram/ui/Cells/ChatMessageCell:viaWidth	I
    //   489: istore_3
    //   490: aload 7
    //   492: aload 8
    //   494: iload 4
    //   496: iload_3
    //   497: isub
    //   498: i2f
    //   499: getstatic 1121	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   502: invokestatic 1125	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   505: astore 8
    //   507: aload 8
    //   509: astore 7
    //   511: iload_2
    //   512: ifeq +194 -> 706
    //   515: aload_0
    //   516: getstatic 391	org/telegram/ui/Cells/ChatMessageCell:namePaint	Landroid/text/TextPaint;
    //   519: aload 8
    //   521: iconst_0
    //   522: aload 8
    //   524: invokeinterface 1033 1 0
    //   529: invokevirtual 1941	android/text/TextPaint:measureText	(Ljava/lang/CharSequence;II)F
    //   532: f2d
    //   533: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   536: d2i
    //   537: putfield 1997	org/telegram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   540: aload_0
    //   541: getfield 1997	org/telegram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   544: ifeq +18 -> 562
    //   547: aload_0
    //   548: aload_0
    //   549: getfield 1997	org/telegram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   552: ldc_w 963
    //   555: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   558: iadd
    //   559: putfield 1997	org/telegram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   562: aload_0
    //   563: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   566: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   569: bipush 13
    //   571: if_icmpne +1743 -> 2314
    //   574: iconst_m1
    //   575: istore_2
    //   576: aload_0
    //   577: getfield 1867	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   580: invokevirtual 937	java/lang/String:length	()I
    //   583: ifle +1755 -> 2338
    //   586: new 1678	android/text/SpannableStringBuilder
    //   589: dup
    //   590: ldc_w 1999
    //   593: iconst_2
    //   594: anewarray 1152	java/lang/Object
    //   597: dup
    //   598: iconst_0
    //   599: aload 8
    //   601: aastore
    //   602: dup
    //   603: iconst_1
    //   604: aload 6
    //   606: aastore
    //   607: invokestatic 1156	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   610: invokespecial 1681	android/text/SpannableStringBuilder:<init>	(Ljava/lang/CharSequence;)V
    //   613: astore 7
    //   615: aload 7
    //   617: new 2001	org/telegram/ui/Components/TypefaceSpan
    //   620: dup
    //   621: getstatic 2007	android/graphics/Typeface:DEFAULT	Landroid/graphics/Typeface;
    //   624: iconst_0
    //   625: iload_2
    //   626: invokespecial 2010	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;II)V
    //   629: aload 8
    //   631: invokeinterface 1033 1 0
    //   636: iconst_1
    //   637: iadd
    //   638: aload 8
    //   640: invokeinterface 1033 1 0
    //   645: iconst_4
    //   646: iadd
    //   647: bipush 33
    //   649: invokevirtual 2014	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   652: aload 7
    //   654: new 2001	org/telegram/ui/Components/TypefaceSpan
    //   657: dup
    //   658: ldc_w 312
    //   661: invokestatic 316	org/telegram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   664: iconst_0
    //   665: iload_2
    //   666: invokespecial 2010	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;II)V
    //   669: aload 8
    //   671: invokeinterface 1033 1 0
    //   676: iconst_5
    //   677: iadd
    //   678: aload 7
    //   680: invokevirtual 2015	android/text/SpannableStringBuilder:length	()I
    //   683: bipush 33
    //   685: invokevirtual 2014	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   688: aload 7
    //   690: getstatic 391	org/telegram/ui/Cells/ChatMessageCell:namePaint	Landroid/text/TextPaint;
    //   693: aload_0
    //   694: getfield 1994	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   697: i2f
    //   698: getstatic 1121	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   701: invokestatic 1125	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   704: astore 7
    //   706: aload_0
    //   707: new 286	android/text/StaticLayout
    //   710: dup
    //   711: aload 7
    //   713: getstatic 391	org/telegram/ui/Cells/ChatMessageCell:namePaint	Landroid/text/TextPaint;
    //   716: aload_0
    //   717: getfield 1994	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   720: fconst_2
    //   721: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   724: iadd
    //   725: getstatic 1131	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   728: fconst_1
    //   729: fconst_0
    //   730: iconst_0
    //   731: invokespecial 1134	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   734: putfield 2017	org/telegram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   737: aload_0
    //   738: getfield 2017	org/telegram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   741: ifnull +1673 -> 2414
    //   744: aload_0
    //   745: getfield 2017	org/telegram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   748: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   751: ifle +1663 -> 2414
    //   754: aload_0
    //   755: aload_0
    //   756: getfield 2017	org/telegram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   759: iconst_0
    //   760: invokevirtual 670	android/text/StaticLayout:getLineWidth	(I)F
    //   763: f2d
    //   764: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   767: d2i
    //   768: putfield 1994	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   771: aload_1
    //   772: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   775: bipush 13
    //   777: if_icmpeq +18 -> 795
    //   780: aload_0
    //   781: aload_0
    //   782: getfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   785: ldc_w 1437
    //   788: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   791: iadd
    //   792: putfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   795: aload_0
    //   796: aload_0
    //   797: getfield 2017	org/telegram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   800: iconst_0
    //   801: invokevirtual 667	android/text/StaticLayout:getLineLeft	(I)F
    //   804: putfield 2019	org/telegram/ui/Cells/ChatMessageCell:nameOffsetX	F
    //   807: aload_0
    //   808: getfield 1867	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   811: invokevirtual 937	java/lang/String:length	()I
    //   814: ifne +8 -> 822
    //   817: aload_0
    //   818: aconst_null
    //   819: putfield 1867	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   822: aload_0
    //   823: aconst_null
    //   824: putfield 2021	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   827: aload_0
    //   828: aconst_null
    //   829: putfield 1874	org/telegram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   832: aload_0
    //   833: aconst_null
    //   834: putfield 2023	org/telegram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/telegram/tgnet/TLRPC$Chat;
    //   837: aload_0
    //   838: getfield 288	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   841: iconst_0
    //   842: aconst_null
    //   843: aastore
    //   844: aload_0
    //   845: getfield 288	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   848: iconst_1
    //   849: aconst_null
    //   850: aastore
    //   851: aload_0
    //   852: iconst_0
    //   853: putfield 2025	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   856: aload_0
    //   857: getfield 1869	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   860: ifeq +512 -> 1372
    //   863: aload_1
    //   864: invokevirtual 2028	org/telegram/messenger/MessageObject:isForwarded	()Z
    //   867: ifeq +505 -> 1372
    //   870: aload_1
    //   871: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   874: getfield 871	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$TL_messageFwdHeader;
    //   877: getfield 876	org/telegram/tgnet/TLRPC$TL_messageFwdHeader:channel_id	I
    //   880: ifeq +26 -> 906
    //   883: aload_0
    //   884: invokestatic 887	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   887: aload_1
    //   888: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   891: getfield 871	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$TL_messageFwdHeader;
    //   894: getfield 876	org/telegram/tgnet/TLRPC$TL_messageFwdHeader:channel_id	I
    //   897: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   900: invokevirtual 927	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   903: putfield 2023	org/telegram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/telegram/tgnet/TLRPC$Chat;
    //   906: aload_1
    //   907: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   910: getfield 871	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$TL_messageFwdHeader;
    //   913: getfield 2029	org/telegram/tgnet/TLRPC$TL_messageFwdHeader:from_id	I
    //   916: ifeq +26 -> 942
    //   919: aload_0
    //   920: invokestatic 887	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   923: aload_1
    //   924: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   927: getfield 871	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$TL_messageFwdHeader;
    //   930: getfield 2029	org/telegram/tgnet/TLRPC$TL_messageFwdHeader:from_id	I
    //   933: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   936: invokevirtual 900	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   939: putfield 2021	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   942: aload_0
    //   943: getfield 2021	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   946: ifnonnull +10 -> 956
    //   949: aload_0
    //   950: getfield 2023	org/telegram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/telegram/tgnet/TLRPC$Chat;
    //   953: ifnull +419 -> 1372
    //   956: aload_0
    //   957: getfield 2023	org/telegram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/telegram/tgnet/TLRPC$Chat;
    //   960: ifnull +1507 -> 2467
    //   963: aload_0
    //   964: getfield 2021	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   967: ifnull +1486 -> 2453
    //   970: aload_0
    //   971: ldc_w 2031
    //   974: iconst_2
    //   975: anewarray 1152	java/lang/Object
    //   978: dup
    //   979: iconst_0
    //   980: aload_0
    //   981: getfield 2023	org/telegram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/telegram/tgnet/TLRPC$Chat;
    //   984: getfield 1883	org/telegram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   987: aastore
    //   988: dup
    //   989: iconst_1
    //   990: aload_0
    //   991: getfield 2021	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   994: invokestatic 1865	org/telegram/messenger/UserObject:getUserName	(Lorg/telegram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   997: aastore
    //   998: invokestatic 1156	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   1001: putfield 1874	org/telegram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   1004: aload_0
    //   1005: aload_0
    //   1006: invokespecial 1992	org/telegram/ui/Cells/ChatMessageCell:getMaxNameWidth	()I
    //   1009: putfield 2025	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1012: getstatic 394	org/telegram/ui/Cells/ChatMessageCell:forwardNamePaint	Landroid/text/TextPaint;
    //   1015: new 1163	java/lang/StringBuilder
    //   1018: dup
    //   1019: invokespecial 1164	java/lang/StringBuilder:<init>	()V
    //   1022: ldc_w 2033
    //   1025: ldc_w 2034
    //   1028: invokestatic 1224	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1031: invokevirtual 1170	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1034: ldc_w 1240
    //   1037: invokevirtual 1170	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1040: invokevirtual 1181	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1043: invokevirtual 1067	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   1046: f2d
    //   1047: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   1050: d2i
    //   1051: istore_2
    //   1052: aload_0
    //   1053: getfield 1874	org/telegram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   1056: bipush 10
    //   1058: bipush 32
    //   1060: invokevirtual 1115	java/lang/String:replace	(CC)Ljava/lang/String;
    //   1063: getstatic 396	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   1066: aload_0
    //   1067: getfield 2025	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1070: iload_2
    //   1071: isub
    //   1072: aload_0
    //   1073: getfield 1986	org/telegram/ui/Cells/ChatMessageCell:viaWidth	I
    //   1076: isub
    //   1077: i2f
    //   1078: getstatic 1121	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1081: invokestatic 1125	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1084: astore 7
    //   1086: aload 5
    //   1088: ifnull +1400 -> 2488
    //   1091: aload_0
    //   1092: getstatic 394	org/telegram/ui/Cells/ChatMessageCell:forwardNamePaint	Landroid/text/TextPaint;
    //   1095: new 1163	java/lang/StringBuilder
    //   1098: dup
    //   1099: invokespecial 1164	java/lang/StringBuilder:<init>	()V
    //   1102: ldc_w 2033
    //   1105: ldc_w 2034
    //   1108: invokestatic 1224	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1111: invokevirtual 1170	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1114: ldc_w 1240
    //   1117: invokevirtual 1170	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1120: aload 7
    //   1122: invokevirtual 1944	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1125: invokevirtual 1181	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1128: invokevirtual 1067	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   1131: f2d
    //   1132: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   1135: d2i
    //   1136: putfield 1997	org/telegram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   1139: ldc_w 2036
    //   1142: iconst_3
    //   1143: anewarray 1152	java/lang/Object
    //   1146: dup
    //   1147: iconst_0
    //   1148: ldc_w 2033
    //   1151: ldc_w 2034
    //   1154: invokestatic 1224	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1157: aastore
    //   1158: dup
    //   1159: iconst_1
    //   1160: aload 7
    //   1162: aastore
    //   1163: dup
    //   1164: iconst_2
    //   1165: aload 6
    //   1167: aastore
    //   1168: invokestatic 1156	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   1171: invokestatic 1984	org/telegram/messenger/AndroidUtilities:replaceTags	(Ljava/lang/String;)Landroid/text/SpannableStringBuilder;
    //   1174: astore 5
    //   1176: aload 5
    //   1178: getstatic 394	org/telegram/ui/Cells/ChatMessageCell:forwardNamePaint	Landroid/text/TextPaint;
    //   1181: aload_0
    //   1182: getfield 2025	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1185: i2f
    //   1186: getstatic 1121	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1189: invokestatic 1125	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1192: astore 5
    //   1194: aload_0
    //   1195: getfield 288	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1198: iconst_1
    //   1199: new 286	android/text/StaticLayout
    //   1202: dup
    //   1203: aload 5
    //   1205: getstatic 394	org/telegram/ui/Cells/ChatMessageCell:forwardNamePaint	Landroid/text/TextPaint;
    //   1208: aload_0
    //   1209: getfield 2025	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1212: fconst_2
    //   1213: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1216: iadd
    //   1217: getstatic 1131	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1220: fconst_1
    //   1221: fconst_0
    //   1222: iconst_0
    //   1223: invokespecial 1134	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1226: aastore
    //   1227: ldc_w 2038
    //   1230: ldc_w 2039
    //   1233: invokestatic 1224	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1236: invokestatic 1984	org/telegram/messenger/AndroidUtilities:replaceTags	(Ljava/lang/String;)Landroid/text/SpannableStringBuilder;
    //   1239: getstatic 394	org/telegram/ui/Cells/ChatMessageCell:forwardNamePaint	Landroid/text/TextPaint;
    //   1242: aload_0
    //   1243: getfield 2025	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1246: i2f
    //   1247: getstatic 1121	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1250: invokestatic 1125	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1253: astore 5
    //   1255: aload_0
    //   1256: getfield 288	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1259: iconst_0
    //   1260: new 286	android/text/StaticLayout
    //   1263: dup
    //   1264: aload 5
    //   1266: getstatic 394	org/telegram/ui/Cells/ChatMessageCell:forwardNamePaint	Landroid/text/TextPaint;
    //   1269: aload_0
    //   1270: getfield 2025	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1273: fconst_2
    //   1274: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1277: iadd
    //   1278: getstatic 1131	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1281: fconst_1
    //   1282: fconst_0
    //   1283: iconst_0
    //   1284: invokespecial 1134	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1287: aastore
    //   1288: aload_0
    //   1289: aload_0
    //   1290: getfield 288	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1293: iconst_0
    //   1294: aaload
    //   1295: iconst_0
    //   1296: invokevirtual 670	android/text/StaticLayout:getLineWidth	(I)F
    //   1299: f2d
    //   1300: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   1303: d2i
    //   1304: aload_0
    //   1305: getfield 288	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1308: iconst_1
    //   1309: aaload
    //   1310: iconst_0
    //   1311: invokevirtual 670	android/text/StaticLayout:getLineWidth	(I)F
    //   1314: f2d
    //   1315: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   1318: d2i
    //   1319: invokestatic 497	java/lang/Math:max	(II)I
    //   1322: putfield 2025	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1325: aload_0
    //   1326: getfield 290	org/telegram/ui/Cells/ChatMessageCell:forwardNameOffsetX	[F
    //   1329: iconst_0
    //   1330: aload_0
    //   1331: getfield 288	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1334: iconst_0
    //   1335: aaload
    //   1336: iconst_0
    //   1337: invokevirtual 667	android/text/StaticLayout:getLineLeft	(I)F
    //   1340: fastore
    //   1341: aload_0
    //   1342: getfield 290	org/telegram/ui/Cells/ChatMessageCell:forwardNameOffsetX	[F
    //   1345: iconst_1
    //   1346: aload_0
    //   1347: getfield 288	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1350: iconst_1
    //   1351: aaload
    //   1352: iconst_0
    //   1353: invokevirtual 667	android/text/StaticLayout:getLineLeft	(I)F
    //   1356: fastore
    //   1357: aload_0
    //   1358: aload_0
    //   1359: getfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1362: ldc_w 550
    //   1365: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1368: iadd
    //   1369: putfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1372: aload_1
    //   1373: invokevirtual 2042	org/telegram/messenger/MessageObject:isReply	()Z
    //   1376: ifeq +559 -> 1935
    //   1379: aload_0
    //   1380: aload_0
    //   1381: getfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1384: ldc_w 1738
    //   1387: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1390: iadd
    //   1391: putfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1394: aload_1
    //   1395: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   1398: ifeq +27 -> 1425
    //   1401: aload_1
    //   1402: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   1405: bipush 13
    //   1407: if_icmpne +1129 -> 2536
    //   1410: aload_0
    //   1411: aload_0
    //   1412: getfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1415: ldc_w 1738
    //   1418: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1421: isub
    //   1422: putfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1425: aload_0
    //   1426: invokespecial 1992	org/telegram/ui/Cells/ChatMessageCell:getMaxNameWidth	()I
    //   1429: istore_3
    //   1430: iload_3
    //   1431: istore_2
    //   1432: aload_1
    //   1433: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   1436: bipush 13
    //   1438: if_icmpeq +12 -> 1450
    //   1441: iload_3
    //   1442: ldc_w 588
    //   1445: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1448: isub
    //   1449: istore_2
    //   1450: aconst_null
    //   1451: astore 8
    //   1453: aload_1
    //   1454: getfield 1844	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1457: ifnull +1268 -> 2725
    //   1460: aload_1
    //   1461: getfield 1844	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1464: getfield 1246	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   1467: bipush 80
    //   1469: invokestatic 1253	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   1472: astore 5
    //   1474: aload 5
    //   1476: ifnull +40 -> 1516
    //   1479: aload_1
    //   1480: getfield 1844	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1483: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   1486: bipush 13
    //   1488: if_icmpeq +28 -> 1516
    //   1491: aload_1
    //   1492: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   1495: bipush 13
    //   1497: if_icmpne +9 -> 1506
    //   1500: invokestatic 1732	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   1503: ifeq +13 -> 1516
    //   1506: aload_1
    //   1507: getfield 1844	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1510: invokevirtual 2045	org/telegram/messenger/MessageObject:isSecretMedia	()Z
    //   1513: ifeq +1041 -> 2554
    //   1516: aload_0
    //   1517: getfield 435	org/telegram/ui/Cells/ChatMessageCell:replyImageReceiver	Lorg/telegram/messenger/ImageReceiver;
    //   1520: aconst_null
    //   1521: checkcast 614	android/graphics/drawable/Drawable
    //   1524: invokevirtual 1278	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   1527: aload_0
    //   1528: iconst_0
    //   1529: putfield 2047	org/telegram/ui/Cells/ChatMessageCell:needReplyImage	Z
    //   1532: aconst_null
    //   1533: astore 5
    //   1535: aload_1
    //   1536: getfield 1844	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1539: invokevirtual 882	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   1542: ifeq +1056 -> 2598
    //   1545: invokestatic 887	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   1548: aload_1
    //   1549: getfield 1844	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1552: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1555: getfield 890	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   1558: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1561: invokevirtual 900	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   1564: astore 6
    //   1566: aload 6
    //   1568: ifnull +10 -> 1578
    //   1571: aload 6
    //   1573: invokestatic 1865	org/telegram/messenger/UserObject:getUserName	(Lorg/telegram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   1576: astore 5
    //   1578: aload 5
    //   1580: ifnull +1139 -> 2719
    //   1583: aload 5
    //   1585: bipush 10
    //   1587: bipush 32
    //   1589: invokevirtual 1115	java/lang/String:replace	(CC)Ljava/lang/String;
    //   1592: getstatic 396	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   1595: iload_2
    //   1596: i2f
    //   1597: getstatic 1121	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1600: invokestatic 1125	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1603: astore 5
    //   1605: aload 5
    //   1607: astore 6
    //   1609: iload_2
    //   1610: istore_3
    //   1611: aload 8
    //   1613: astore 7
    //   1615: aload_1
    //   1616: getfield 1844	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1619: getfield 1005	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   1622: ifnull +109 -> 1731
    //   1625: aload 5
    //   1627: astore 6
    //   1629: iload_2
    //   1630: istore_3
    //   1631: aload 8
    //   1633: astore 7
    //   1635: aload_1
    //   1636: getfield 1844	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1639: getfield 1005	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   1642: invokeinterface 1033 1 0
    //   1647: ifle +84 -> 1731
    //   1650: aload_1
    //   1651: getfield 1844	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1654: getfield 1005	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   1657: invokeinterface 2048 1 0
    //   1662: astore 6
    //   1664: aload 6
    //   1666: astore_1
    //   1667: aload 6
    //   1669: invokevirtual 937	java/lang/String:length	()I
    //   1672: sipush 150
    //   1675: if_icmple +13 -> 1688
    //   1678: aload 6
    //   1680: iconst_0
    //   1681: sipush 150
    //   1684: invokevirtual 2052	java/lang/String:substring	(II)Ljava/lang/String;
    //   1687: astore_1
    //   1688: aload_1
    //   1689: bipush 10
    //   1691: bipush 32
    //   1693: invokevirtual 1115	java/lang/String:replace	(CC)Ljava/lang/String;
    //   1696: getstatic 398	org/telegram/ui/Cells/ChatMessageCell:replyTextPaint	Landroid/text/TextPaint;
    //   1699: invokevirtual 2056	android/text/TextPaint:getFontMetricsInt	()Landroid/graphics/Paint$FontMetricsInt;
    //   1702: ldc_w 392
    //   1705: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1708: iconst_0
    //   1709: invokestatic 2062	org/telegram/messenger/Emoji:replaceEmoji	(Ljava/lang/CharSequence;Landroid/graphics/Paint$FontMetricsInt;IZ)Ljava/lang/CharSequence;
    //   1712: getstatic 398	org/telegram/ui/Cells/ChatMessageCell:replyTextPaint	Landroid/text/TextPaint;
    //   1715: iload_2
    //   1716: i2f
    //   1717: getstatic 1121	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1720: invokestatic 1125	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1723: astore 7
    //   1725: iload_2
    //   1726: istore_3
    //   1727: aload 5
    //   1729: astore 6
    //   1731: aload 6
    //   1733: astore_1
    //   1734: aload 6
    //   1736: ifnonnull +13 -> 1749
    //   1739: ldc_w 2064
    //   1742: ldc_w 2065
    //   1745: invokestatic 1224	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1748: astore_1
    //   1749: aload_0
    //   1750: new 286	android/text/StaticLayout
    //   1753: dup
    //   1754: aload_1
    //   1755: getstatic 396	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   1758: iload_3
    //   1759: ldc_w 1528
    //   1762: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1765: iadd
    //   1766: getstatic 1131	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1769: fconst_1
    //   1770: fconst_0
    //   1771: iconst_0
    //   1772: invokespecial 1134	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1775: putfield 2067	org/telegram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   1778: aload_0
    //   1779: getfield 2067	org/telegram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   1782: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   1785: ifle +55 -> 1840
    //   1788: aload_0
    //   1789: getfield 2067	org/telegram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   1792: iconst_0
    //   1793: invokevirtual 670	android/text/StaticLayout:getLineWidth	(I)F
    //   1796: f2d
    //   1797: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   1800: d2i
    //   1801: istore 4
    //   1803: aload_0
    //   1804: getfield 2047	org/telegram/ui/Cells/ChatMessageCell:needReplyImage	Z
    //   1807: ifeq +880 -> 2687
    //   1810: bipush 44
    //   1812: istore_2
    //   1813: aload_0
    //   1814: iload_2
    //   1815: bipush 12
    //   1817: iadd
    //   1818: i2f
    //   1819: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1822: iload 4
    //   1824: iadd
    //   1825: putfield 2069	org/telegram/ui/Cells/ChatMessageCell:replyNameWidth	I
    //   1828: aload_0
    //   1829: aload_0
    //   1830: getfield 2067	org/telegram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   1833: iconst_0
    //   1834: invokevirtual 667	android/text/StaticLayout:getLineLeft	(I)F
    //   1837: putfield 2071	org/telegram/ui/Cells/ChatMessageCell:replyNameOffset	F
    //   1840: aload 7
    //   1842: ifnull +93 -> 1935
    //   1845: aload_0
    //   1846: new 286	android/text/StaticLayout
    //   1849: dup
    //   1850: aload 7
    //   1852: getstatic 398	org/telegram/ui/Cells/ChatMessageCell:replyTextPaint	Landroid/text/TextPaint;
    //   1855: iload_3
    //   1856: ldc_w 1528
    //   1859: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1862: iadd
    //   1863: getstatic 1131	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1866: fconst_1
    //   1867: fconst_0
    //   1868: iconst_0
    //   1869: invokespecial 1134	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1872: putfield 1841	org/telegram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   1875: aload_0
    //   1876: getfield 1841	org/telegram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   1879: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   1882: ifle +53 -> 1935
    //   1885: aload_0
    //   1886: getfield 1841	org/telegram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   1889: iconst_0
    //   1890: invokevirtual 670	android/text/StaticLayout:getLineWidth	(I)F
    //   1893: f2d
    //   1894: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   1897: d2i
    //   1898: istore_3
    //   1899: aload_0
    //   1900: getfield 2047	org/telegram/ui/Cells/ChatMessageCell:needReplyImage	Z
    //   1903: ifeq +800 -> 2703
    //   1906: bipush 44
    //   1908: istore_2
    //   1909: aload_0
    //   1910: iload_2
    //   1911: bipush 12
    //   1913: iadd
    //   1914: i2f
    //   1915: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1918: iload_3
    //   1919: iadd
    //   1920: putfield 2073	org/telegram/ui/Cells/ChatMessageCell:replyTextWidth	I
    //   1923: aload_0
    //   1924: aload_0
    //   1925: getfield 1841	org/telegram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   1928: iconst_0
    //   1929: invokevirtual 667	android/text/StaticLayout:getLineLeft	(I)F
    //   1932: putfield 2075	org/telegram/ui/Cells/ChatMessageCell:replyTextOffset	F
    //   1935: aload_0
    //   1936: invokevirtual 2078	org/telegram/ui/Cells/ChatMessageCell:requestLayout	()V
    //   1939: return
    //   1940: aload_0
    //   1941: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1944: getfield 2081	org/telegram/messenger/MessageObject:viewsReloaded	Z
    //   1947: ifne -1892 -> 55
    //   1950: invokestatic 887	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   1953: aload_0
    //   1954: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1957: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1960: iconst_1
    //   1961: invokevirtual 1963	org/telegram/messenger/MessagesController:addToViewsQueue	(Lorg/telegram/tgnet/TLRPC$Message;Z)V
    //   1964: aload_0
    //   1965: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1968: iconst_1
    //   1969: putfield 2081	org/telegram/messenger/MessageObject:viewsReloaded	Z
    //   1972: goto -1917 -> 55
    //   1975: aload_0
    //   1976: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1979: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1982: getfield 890	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   1985: ifge +30 -> 2015
    //   1988: aload_0
    //   1989: invokestatic 887	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   1992: aload_0
    //   1993: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1996: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1999: getfield 890	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   2002: ineg
    //   2003: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2006: invokevirtual 927	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   2009: putfield 1816	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2012: goto -1924 -> 88
    //   2015: aload_0
    //   2016: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2019: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2022: getfield 946	org/telegram/tgnet/TLRPC$Message:post	Z
    //   2025: ifeq -1937 -> 88
    //   2028: aload_0
    //   2029: invokestatic 887	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   2032: aload_0
    //   2033: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2036: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2039: getfield 920	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   2042: getfield 923	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   2045: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2048: invokevirtual 927	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   2051: putfield 1816	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2054: goto -1966 -> 88
    //   2057: aload_0
    //   2058: aconst_null
    //   2059: putfield 1846	org/telegram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2062: goto -1917 -> 145
    //   2065: aload_0
    //   2066: getfield 1816	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2069: ifnull +49 -> 2118
    //   2072: aload_0
    //   2073: getfield 1816	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2076: getfield 1877	org/telegram/tgnet/TLRPC$Chat:photo	Lorg/telegram/tgnet/TLRPC$ChatPhoto;
    //   2079: ifnull +31 -> 2110
    //   2082: aload_0
    //   2083: aload_0
    //   2084: getfield 1816	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2087: getfield 1877	org/telegram/tgnet/TLRPC$Chat:photo	Lorg/telegram/tgnet/TLRPC$ChatPhoto;
    //   2090: getfield 1880	org/telegram/tgnet/TLRPC$ChatPhoto:photo_small	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2093: putfield 1846	org/telegram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2096: aload_0
    //   2097: getfield 433	org/telegram/ui/Cells/ChatMessageCell:avatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   2100: aload_0
    //   2101: getfield 1816	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2104: invokevirtual 2084	org/telegram/ui/Components/AvatarDrawable:setInfo	(Lorg/telegram/tgnet/TLRPC$Chat;)V
    //   2107: goto -1951 -> 156
    //   2110: aload_0
    //   2111: aconst_null
    //   2112: putfield 1846	org/telegram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2115: goto -19 -> 2096
    //   2118: aload_0
    //   2119: aconst_null
    //   2120: putfield 1846	org/telegram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2123: aload_0
    //   2124: getfield 433	org/telegram/ui/Cells/ChatMessageCell:avatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   2127: aload_1
    //   2128: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2131: getfield 890	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   2134: aconst_null
    //   2135: aconst_null
    //   2136: iconst_0
    //   2137: invokevirtual 2087	org/telegram/ui/Components/AvatarDrawable:setInfo	(ILjava/lang/String;Ljava/lang/String;Z)V
    //   2140: goto -1984 -> 156
    //   2143: aload 8
    //   2145: astore 5
    //   2147: aload 7
    //   2149: astore 6
    //   2151: aload_1
    //   2152: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2155: getfield 1886	org/telegram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   2158: ifnull -1813 -> 345
    //   2161: aload 8
    //   2163: astore 5
    //   2165: aload 7
    //   2167: astore 6
    //   2169: aload_1
    //   2170: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2173: getfield 1886	org/telegram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   2176: invokevirtual 937	java/lang/String:length	()I
    //   2179: ifle -1834 -> 345
    //   2182: new 1163	java/lang/StringBuilder
    //   2185: dup
    //   2186: invokespecial 1164	java/lang/StringBuilder:<init>	()V
    //   2189: ldc_w 1978
    //   2192: invokevirtual 1170	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2195: aload_1
    //   2196: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2199: getfield 1886	org/telegram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   2202: invokevirtual 1170	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2205: invokevirtual 1181	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2208: astore 6
    //   2210: ldc_w 1980
    //   2213: iconst_1
    //   2214: anewarray 1152	java/lang/Object
    //   2217: dup
    //   2218: iconst_0
    //   2219: aload 6
    //   2221: aastore
    //   2222: invokestatic 1156	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   2225: invokestatic 1984	org/telegram/messenger/AndroidUtilities:replaceTags	(Ljava/lang/String;)Landroid/text/SpannableStringBuilder;
    //   2228: astore 5
    //   2230: aload_0
    //   2231: getstatic 396	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   2234: aload 5
    //   2236: iconst_0
    //   2237: aload 5
    //   2239: invokeinterface 1033 1 0
    //   2244: invokevirtual 1941	android/text/TextPaint:measureText	(Ljava/lang/CharSequence;II)F
    //   2247: f2d
    //   2248: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   2251: d2i
    //   2252: putfield 1986	org/telegram/ui/Cells/ChatMessageCell:viaWidth	I
    //   2255: goto -1910 -> 345
    //   2258: iconst_0
    //   2259: istore_3
    //   2260: goto -1889 -> 371
    //   2263: iconst_0
    //   2264: istore_2
    //   2265: goto -1868 -> 397
    //   2268: aload_0
    //   2269: getfield 1816	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2272: ifnull +17 -> 2289
    //   2275: aload_0
    //   2276: aload_0
    //   2277: getfield 1816	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2280: getfield 1883	org/telegram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   2283: putfield 1867	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   2286: goto -1829 -> 457
    //   2289: aload_0
    //   2290: ldc_w 2089
    //   2293: putfield 1867	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   2296: goto -1839 -> 457
    //   2299: aload_0
    //   2300: ldc_w 2091
    //   2303: putfield 1867	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   2306: goto -1849 -> 457
    //   2309: iconst_0
    //   2310: istore_3
    //   2311: goto -1821 -> 490
    //   2314: aload_0
    //   2315: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2318: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   2321: ifeq +10 -> 2331
    //   2324: ldc_w 1082
    //   2327: istore_2
    //   2328: goto -1752 -> 576
    //   2331: ldc_w 1103
    //   2334: istore_2
    //   2335: goto -7 -> 2328
    //   2338: new 1678	android/text/SpannableStringBuilder
    //   2341: dup
    //   2342: ldc_w 2093
    //   2345: iconst_1
    //   2346: anewarray 1152	java/lang/Object
    //   2349: dup
    //   2350: iconst_0
    //   2351: aload 6
    //   2353: aastore
    //   2354: invokestatic 1156	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   2357: invokespecial 1681	android/text/SpannableStringBuilder:<init>	(Ljava/lang/CharSequence;)V
    //   2360: astore 7
    //   2362: aload 7
    //   2364: new 2001	org/telegram/ui/Components/TypefaceSpan
    //   2367: dup
    //   2368: getstatic 2007	android/graphics/Typeface:DEFAULT	Landroid/graphics/Typeface;
    //   2371: iconst_0
    //   2372: iload_2
    //   2373: invokespecial 2010	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;II)V
    //   2376: iconst_0
    //   2377: iconst_4
    //   2378: bipush 33
    //   2380: invokevirtual 2014	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   2383: aload 7
    //   2385: new 2001	org/telegram/ui/Components/TypefaceSpan
    //   2388: dup
    //   2389: ldc_w 312
    //   2392: invokestatic 316	org/telegram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   2395: iconst_0
    //   2396: iload_2
    //   2397: invokespecial 2010	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;II)V
    //   2400: iconst_4
    //   2401: aload 7
    //   2403: invokevirtual 2015	android/text/SpannableStringBuilder:length	()I
    //   2406: bipush 33
    //   2408: invokevirtual 2014	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   2411: goto -1723 -> 688
    //   2414: aload_0
    //   2415: iconst_0
    //   2416: putfield 1994	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   2419: goto -1612 -> 807
    //   2422: astore 7
    //   2424: ldc_w 711
    //   2427: aload 7
    //   2429: invokestatic 717	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2432: goto -1625 -> 807
    //   2435: aload_0
    //   2436: aconst_null
    //   2437: putfield 1867	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   2440: aload_0
    //   2441: aconst_null
    //   2442: putfield 2017	org/telegram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   2445: aload_0
    //   2446: iconst_0
    //   2447: putfield 1994	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   2450: goto -1628 -> 822
    //   2453: aload_0
    //   2454: aload_0
    //   2455: getfield 2023	org/telegram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2458: getfield 1883	org/telegram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   2461: putfield 1874	org/telegram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   2464: goto -1460 -> 1004
    //   2467: aload_0
    //   2468: getfield 2021	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   2471: ifnull -1467 -> 1004
    //   2474: aload_0
    //   2475: aload_0
    //   2476: getfield 2021	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   2479: invokestatic 1865	org/telegram/messenger/UserObject:getUserName	(Lorg/telegram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   2482: putfield 1874	org/telegram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   2485: goto -1481 -> 1004
    //   2488: ldc_w 2095
    //   2491: iconst_2
    //   2492: anewarray 1152	java/lang/Object
    //   2495: dup
    //   2496: iconst_0
    //   2497: ldc_w 2033
    //   2500: ldc_w 2034
    //   2503: invokestatic 1224	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2506: aastore
    //   2507: dup
    //   2508: iconst_1
    //   2509: aload 7
    //   2511: aastore
    //   2512: invokestatic 1156	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   2515: invokestatic 1984	org/telegram/messenger/AndroidUtilities:replaceTags	(Ljava/lang/String;)Landroid/text/SpannableStringBuilder;
    //   2518: astore 5
    //   2520: goto -1344 -> 1176
    //   2523: astore 5
    //   2525: ldc_w 711
    //   2528: aload 5
    //   2530: invokestatic 717	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2533: goto -1161 -> 1372
    //   2536: aload_0
    //   2537: aload_0
    //   2538: getfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   2541: ldc_w 1527
    //   2544: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2547: iadd
    //   2548: putfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   2551: goto -1126 -> 1425
    //   2554: aload_0
    //   2555: aload 5
    //   2557: getfield 1207	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2560: putfield 1857	org/telegram/ui/Cells/ChatMessageCell:currentReplyPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2563: aload_0
    //   2564: getfield 435	org/telegram/ui/Cells/ChatMessageCell:replyImageReceiver	Lorg/telegram/messenger/ImageReceiver;
    //   2567: aload 5
    //   2569: getfield 1207	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2572: ldc_w 1972
    //   2575: aconst_null
    //   2576: aconst_null
    //   2577: iconst_1
    //   2578: invokevirtual 1975	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Ljava/lang/String;Z)V
    //   2581: aload_0
    //   2582: iconst_1
    //   2583: putfield 2047	org/telegram/ui/Cells/ChatMessageCell:needReplyImage	Z
    //   2586: iload_2
    //   2587: ldc_w 1554
    //   2590: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2593: isub
    //   2594: istore_2
    //   2595: goto -1063 -> 1532
    //   2598: aload_1
    //   2599: getfield 1844	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2602: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2605: getfield 890	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   2608: ifge +40 -> 2648
    //   2611: invokestatic 887	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   2614: aload_1
    //   2615: getfield 1844	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2618: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2621: getfield 890	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   2624: ineg
    //   2625: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2628: invokevirtual 927	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   2631: astore 6
    //   2633: aload 6
    //   2635: ifnull -1057 -> 1578
    //   2638: aload 6
    //   2640: getfield 1883	org/telegram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   2643: astore 5
    //   2645: goto -1067 -> 1578
    //   2648: invokestatic 887	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   2651: aload_1
    //   2652: getfield 1844	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2655: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2658: getfield 920	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   2661: getfield 923	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   2664: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2667: invokevirtual 927	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   2670: astore 6
    //   2672: aload 6
    //   2674: ifnull -1096 -> 1578
    //   2677: aload 6
    //   2679: getfield 1883	org/telegram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   2682: astore 5
    //   2684: goto -1106 -> 1578
    //   2687: iconst_0
    //   2688: istore_2
    //   2689: goto -876 -> 1813
    //   2692: astore_1
    //   2693: ldc_w 711
    //   2696: aload_1
    //   2697: invokestatic 717	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2700: goto -860 -> 1840
    //   2703: iconst_0
    //   2704: istore_2
    //   2705: goto -796 -> 1909
    //   2708: astore_1
    //   2709: ldc_w 711
    //   2712: aload_1
    //   2713: invokestatic 717	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2716: goto -781 -> 1935
    //   2719: aconst_null
    //   2720: astore 5
    //   2722: goto -1117 -> 1605
    //   2725: aconst_null
    //   2726: astore 6
    //   2728: iload_2
    //   2729: istore_3
    //   2730: aload 8
    //   2732: astore 7
    //   2734: goto -1003 -> 1731
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	2737	0	this	ChatMessageCell
    //   0	2737	1	paramMessageObject	MessageObject
    //   396	2333	2	i	int
    //   370	2360	3	j	int
    //   479	1346	4	k	int
    //   222	2297	5	localObject1	Object
    //   2523	45	5	localException1	Exception
    //   2643	78	5	str	String
    //   226	2501	6	localObject2	Object
    //   187	2215	7	localObject3	Object
    //   2422	88	7	localException2	Exception
    //   2732	1	7	localObject4	Object
    //   190	2541	8	localObject5	Object
    //   218	123	9	localUser	TLRPC.User
    // Exception table:
    //   from	to	target	type
    //   706	795	2422	java/lang/Exception
    //   795	807	2422	java/lang/Exception
    //   2414	2419	2422	java/lang/Exception
    //   1194	1372	2523	java/lang/Exception
    //   1749	1810	2692	java/lang/Exception
    //   1813	1840	2692	java/lang/Exception
    //   1845	1906	2708	java/lang/Exception
    //   1909	1935	2708	java/lang/Exception
  }
  
  private void updateSecretTimeText(MessageObject paramMessageObject)
  {
    if ((paramMessageObject == null) || (paramMessageObject.isOut())) {}
    do
    {
      return;
      paramMessageObject = paramMessageObject.getSecretTimeString();
    } while (paramMessageObject == null);
    this.infoWidth = ((int)Math.ceil(infoPaint.measureText(paramMessageObject)));
    this.infoLayout = new StaticLayout(TextUtils.ellipsize(paramMessageObject, infoPaint, this.infoWidth, TextUtils.TruncateAt.END), infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
    invalidate();
  }
  
  private void updateWaveform()
  {
    if ((this.currentMessageObject == null) || (this.documentAttachType != 3)) {}
    for (;;)
    {
      return;
      int i = 0;
      while (i < this.documentAttach.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(i);
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio))
        {
          if ((localDocumentAttribute.waveform == null) || (localDocumentAttribute.waveform.length == 0)) {
            MediaController.getInstance().generateWaveform(this.currentMessageObject);
          }
          if (localDocumentAttribute.waveform != null) {}
          for (boolean bool = true;; bool = false)
          {
            this.useSeekBarWaweform = bool;
            this.seekBarWaveform.setWaveform(localDocumentAttribute.waveform);
            return;
          }
        }
        i += 1;
      }
    }
  }
  
  public void didSetImage(ImageReceiver paramImageReceiver, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((this.currentMessageObject != null) && (paramBoolean1) && (!paramBoolean2) && (!this.currentMessageObject.mediaExists) && (!this.currentMessageObject.attachPathExists))
    {
      this.currentMessageObject.mediaExists = true;
      updateButtonState(true);
    }
  }
  
  public void downloadAudioIfNeed()
  {
    if ((this.documentAttachType != 3) || (this.documentAttach.size >= 5242880)) {}
    while (this.buttonState != 2) {
      return;
    }
    FileLoader.getInstance().loadFile(this.documentAttach, true, false);
    this.buttonState = 4;
    this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
  }
  
  public MessageObject getMessageObject()
  {
    return this.currentMessageObject;
  }
  
  public int getObserverTag()
  {
    return this.TAG;
  }
  
  public ImageReceiver getPhotoImage()
  {
    return this.photoImage;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.avatarImage.onAttachedToWindow();
    this.replyImageReceiver.onAttachedToWindow();
    if (this.drawPhotoImage)
    {
      if (this.photoImage.onAttachedToWindow()) {
        updateButtonState(false);
      }
      return;
    }
    updateButtonState(false);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.avatarImage.onDetachedFromWindow();
    this.replyImageReceiver.onDetachedFromWindow();
    this.photoImage.onDetachedFromWindow();
    MediaController.getInstance().removeLoadingFileObserver(this);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.currentMessageObject == null) {
      return;
    }
    if (!this.wasLayout)
    {
      requestLayout();
      return;
    }
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    if (this.isAvatarVisible)
    {
      this.avatarImage.setRoundRadius(AndroidUtilities.dp(localSharedPreferences.getInt("theme_chat_group_aradius", 21)));
      this.avatarImage.draw(paramCanvas);
    }
    label139:
    Object localObject;
    int k;
    int m;
    int i;
    label167:
    int n;
    int i1;
    int j;
    label189:
    Drawable localDrawable;
    if (this.mediaBackground)
    {
      timePaint.setColor(-1);
      if (!this.currentMessageObject.isOutOwner()) {
        break label974;
      }
      if (!isDrawSelectedBackground()) {
        break label905;
      }
      if (this.mediaBackground) {
        break label895;
      }
      this.currentBackgroundDrawable = Theme.backgroundDrawableOutSelected;
      MihanTheme.setColorFilter(this.currentBackgroundDrawable, MihanTheme.getLighterDarkerColor(localSharedPreferences.getInt("theme_rbubble_color", -1048610)));
      localObject = this.currentBackgroundDrawable;
      k = this.layoutWidth;
      m = this.backgroundWidth;
      if (this.mediaBackground) {
        break label952;
      }
      i = 0;
      n = AndroidUtilities.dp(1.0F);
      i1 = this.backgroundWidth;
      if (!this.mediaBackground) {
        break label963;
      }
      j = 0;
      setDrawableBounds((Drawable)localObject, k - m - i, n, i1 - j, this.layoutHeight - AndroidUtilities.dp(2.0F));
      if ((this.drawBackground) && (this.currentBackgroundDrawable != null)) {
        this.currentBackgroundDrawable.draw(paramCanvas);
      }
      drawContent(paramCanvas);
      if (!this.drawShareButton) {
        break label1306;
      }
      localDrawable = Theme.shareDrawable;
      if (!this.sharePressed) {
        break label1274;
      }
      localObject = Theme.colorPressedFilter;
      label270:
      localDrawable.setColorFilter((ColorFilter)localObject);
      if (!this.currentMessageObject.isOutOwner()) {
        break label1282;
      }
      this.shareStartX = (this.currentBackgroundDrawable.getBounds().left - AndroidUtilities.dp(8.0F) - Theme.shareDrawable.getIntrinsicWidth());
      label315:
      localObject = Theme.shareDrawable;
      i = this.shareStartX;
      j = this.layoutHeight - AndroidUtilities.dp(41.0F);
      this.shareStartY = j;
      setDrawableBounds((Drawable)localObject, i, j);
      Theme.shareDrawable.draw(paramCanvas);
      setDrawableBounds(Theme.shareIconDrawable, this.shareStartX + AndroidUtilities.dp(9.0F), this.shareStartY + AndroidUtilities.dp(9.0F));
      Theme.shareIconDrawable.draw(paramCanvas);
      label398:
      if ((this.drawNameLayout) && (this.nameLayout != null))
      {
        paramCanvas.save();
        if (this.currentMessageObject.type != 13) {
          break label1528;
        }
        namePaint.setColor(-1);
        if (!this.currentMessageObject.isOutOwner()) {
          break label1503;
        }
        this.nameX = AndroidUtilities.dp(28.0F);
        label457:
        this.nameY = (this.layoutHeight - AndroidUtilities.dp(38.0F));
        Theme.systemDrawable.setColorFilter(Theme.colorFilter);
        Theme.systemDrawable.setBounds((int)this.nameX - AndroidUtilities.dp(12.0F), (int)this.nameY - AndroidUtilities.dp(5.0F), (int)this.nameX + AndroidUtilities.dp(12.0F) + this.nameWidth, (int)this.nameY + AndroidUtilities.dp(22.0F));
        Theme.systemDrawable.draw(paramCanvas);
        paramCanvas.translate(this.nameX, this.nameY);
        this.nameLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      if ((!this.drawForwardedName) || (this.forwardedNameLayout[0] == null) || (this.forwardedNameLayout[1] == null)) {
        break label1798;
      }
      if (!this.drawNameLayout) {
        break label1718;
      }
      i = 19;
      label608:
      this.forwardNameY = AndroidUtilities.dp(i + 10);
      if (!this.currentMessageObject.isOutOwner()) {
        break label1724;
      }
      forwardNamePaint.setColor(localSharedPreferences.getInt("theme_rfname_color", -11162801));
      this.forwardNameX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0F));
    }
    for (;;)
    {
      i = 0;
      while (i < 2)
      {
        paramCanvas.save();
        paramCanvas.translate(this.forwardNameX - this.forwardNameOffsetX[i], this.forwardNameY + AndroidUtilities.dp(16.0F) * i);
        this.forwardedNameLayout[i].draw(paramCanvas);
        paramCanvas.restore();
        i += 1;
      }
      if (this.currentMessageObject.isOutOwner())
      {
        localObject = this.currentMessageObject;
        MessageObject.getTextPaint().setColor(localSharedPreferences.getInt("theme_rtext_color", -16777216));
        localObject = this.currentMessageObject;
        MessageObject.getTextPaint().linkColor = localSharedPreferences.getInt("theme_rlink_color", -14255946);
        timePaint.setColor(localSharedPreferences.getInt("theme_rtime_color", -9391780));
        break;
      }
      localObject = this.currentMessageObject;
      MessageObject.getTextPaint().setColor(localSharedPreferences.getInt("theme_ltext_color", -16777216));
      localObject = this.currentMessageObject;
      MessageObject.getTextPaint().linkColor = localSharedPreferences.getInt("theme_llink_color", -14255946);
      timePaint.setColor(localSharedPreferences.getInt("theme_ltime_color", -6182221));
      break;
      label895:
      this.currentBackgroundDrawable = Theme.backgroundMediaDrawableOutSelected;
      break label139;
      label905:
      if (!this.mediaBackground)
      {
        this.currentBackgroundDrawable = Theme.backgroundDrawableOut;
        MihanTheme.setColorFilter(this.currentBackgroundDrawable, localSharedPreferences.getInt("theme_rbubble_color", -1048610));
        break label139;
      }
      this.currentBackgroundDrawable = Theme.backgroundMediaDrawableOut;
      break label139;
      label952:
      i = AndroidUtilities.dp(9.0F);
      break label167;
      label963:
      j = AndroidUtilities.dp(3.0F);
      break label189;
      label974:
      if (isDrawSelectedBackground()) {
        if (!this.mediaBackground)
        {
          this.currentBackgroundDrawable = Theme.backgroundDrawableInSelected;
          MihanTheme.setColorFilter(this.currentBackgroundDrawable, MihanTheme.getLighterDarkerColor(localSharedPreferences.getInt("theme_lbubble_color", -1)));
          label1016:
          if ((!this.isChat) || (!this.currentMessageObject.isFromUser())) {
            break label1182;
          }
          localObject = this.currentBackgroundDrawable;
          if (this.mediaBackground) {
            break label1164;
          }
          i = 3;
          label1049:
          j = AndroidUtilities.dp(i + 48);
          k = AndroidUtilities.dp(1.0F);
          m = this.backgroundWidth;
          if (!this.mediaBackground) {
            break label1171;
          }
        }
      }
      label1164:
      label1171:
      for (i = 0;; i = AndroidUtilities.dp(3.0F))
      {
        setDrawableBounds((Drawable)localObject, j, k, m - i, this.layoutHeight - AndroidUtilities.dp(2.0F));
        break;
        this.currentBackgroundDrawable = Theme.backgroundMediaDrawableInSelected;
        break label1016;
        if (!this.mediaBackground)
        {
          this.currentBackgroundDrawable = Theme.backgroundDrawableIn;
          MihanTheme.setColorFilter(this.currentBackgroundDrawable, localSharedPreferences.getInt("theme_lbubble_color", -1));
          break label1016;
        }
        this.currentBackgroundDrawable = Theme.backgroundMediaDrawableIn;
        break label1016;
        i = 9;
        break label1049;
      }
      label1182:
      localObject = this.currentBackgroundDrawable;
      if (!this.mediaBackground)
      {
        i = AndroidUtilities.dp(3.0F);
        label1203:
        k = AndroidUtilities.dp(1.0F);
        m = this.backgroundWidth;
        if (!this.mediaBackground) {
          break label1263;
        }
      }
      label1263:
      for (j = 0;; j = AndroidUtilities.dp(3.0F))
      {
        setDrawableBounds((Drawable)localObject, i, k, m - j, this.layoutHeight - AndroidUtilities.dp(2.0F));
        break;
        i = AndroidUtilities.dp(9.0F);
        break label1203;
      }
      label1274:
      localObject = Theme.colorFilter;
      break label270;
      label1282:
      this.shareStartX = (this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(8.0F));
      break label315;
      label1306:
      if ((!this.drawDirectReply) || (this.currentMessageObject.isOutOwner())) {
        break label398;
      }
      localDrawable = Theme.shareDrawable;
      if (this.directReplyPressed)
      {
        localObject = Theme.colorPressedFilter;
        label1340:
        localDrawable.setColorFilter((ColorFilter)localObject);
        if (!this.currentMessageObject.isOutOwner()) {
          break label1479;
        }
      }
      label1479:
      for (this.shareStartX = (this.currentBackgroundDrawable.getBounds().left - AndroidUtilities.dp(8.0F) - Theme.shareDrawable.getIntrinsicWidth());; this.shareStartX = (this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(8.0F)))
      {
        localObject = Theme.shareDrawable;
        i = this.shareStartX;
        j = this.layoutHeight - AndroidUtilities.dp(41.0F);
        this.shareStartY = j;
        setDrawableBounds((Drawable)localObject, i, j);
        Theme.shareDrawable.draw(paramCanvas);
        setDrawableBounds(Theme.directReplyIconDrawable, this.shareStartX + AndroidUtilities.dp(9.0F), this.shareStartY + AndroidUtilities.dp(9.0F));
        Theme.directReplyIconDrawable.draw(paramCanvas);
        break;
        localObject = Theme.colorFilter;
        break label1340;
      }
      label1503:
      this.nameX = (this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(22.0F));
      break label457;
      label1528:
      if ((this.mediaBackground) || (this.currentMessageObject.isOutOwner()))
      {
        this.nameX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0F) - this.nameOffsetX);
        label1572:
        if (this.currentUser == null) {
          break label1679;
        }
        if (!localSharedPreferences.getBoolean("theme_set_gmcolor", false)) {
          break label1660;
        }
        i = localSharedPreferences.getInt("theme_member_color", MihanTheme.getThemeColor());
        namePaint.setColor(i);
      }
      for (;;)
      {
        this.nameY = AndroidUtilities.dp(10.0F);
        break;
        this.nameX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(17.0F) - this.nameOffsetX);
        break label1572;
        label1660:
        namePaint.setColor(AvatarDrawable.getNameColorForId(this.currentUser.id));
        continue;
        label1679:
        if (this.currentChat != null) {
          namePaint.setColor(AvatarDrawable.getNameColorForId(this.currentChat.id));
        } else {
          namePaint.setColor(AvatarDrawable.getNameColorForId(0));
        }
      }
      label1718:
      i = 0;
      break label608;
      label1724:
      forwardNamePaint.setColor(localSharedPreferences.getInt("theme_lfname_color", -12940081));
      if (this.mediaBackground) {
        this.forwardNameX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0F));
      } else {
        this.forwardNameX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(17.0F));
      }
    }
    label1798:
    if (this.currentMessageObject.isReply())
    {
      if (this.currentMessageObject.type != 13) {
        break label2987;
      }
      replyLinePaint.setColor(-1);
      replyNamePaint.setColor(-1);
      replyTextPaint.setColor(-1);
      if (!this.currentMessageObject.isOutOwner()) {
        break label2957;
      }
      this.replyStartX = AndroidUtilities.dp(23.0F);
      label1861:
      this.replyStartY = (this.layoutHeight - AndroidUtilities.dp(58.0F));
      if (this.nameLayout != null) {
        this.replyStartY -= AndroidUtilities.dp(31.0F);
      }
      j = Math.max(this.replyNameWidth, this.replyTextWidth);
      if (!this.needReplyImage) {
        break label2981;
      }
      i = 44;
      label1922:
      i = AndroidUtilities.dp(i + 14);
      Theme.systemDrawable.setColorFilter(Theme.colorFilter);
      Theme.systemDrawable.setBounds(this.replyStartX - AndroidUtilities.dp(7.0F), this.replyStartY - AndroidUtilities.dp(6.0F), this.replyStartX - AndroidUtilities.dp(7.0F) + (j + i), this.replyStartY + AndroidUtilities.dp(41.0F));
      Theme.systemDrawable.draw(paramCanvas);
      paramCanvas.drawRect(this.replyStartX, this.replyStartY, this.replyStartX + AndroidUtilities.dp(2.0F), this.replyStartY + AndroidUtilities.dp(35.0F), replyLinePaint);
      if (this.needReplyImage)
      {
        this.replyImageReceiver.setImageCoords(this.replyStartX + AndroidUtilities.dp(10.0F), this.replyStartY, AndroidUtilities.dp(35.0F), AndroidUtilities.dp(35.0F));
        this.replyImageReceiver.draw(paramCanvas);
      }
      float f1;
      float f2;
      if (this.replyNameLayout != null)
      {
        paramCanvas.save();
        f1 = this.replyStartX;
        f2 = this.replyNameOffset;
        if (!this.needReplyImage) {
          break label3346;
        }
        i = 44;
        label2128:
        paramCanvas.translate(AndroidUtilities.dp(i + 10) + (f1 - f2), this.replyStartY);
        this.replyNameLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      if (this.replyTextLayout != null)
      {
        paramCanvas.save();
        f1 = this.replyStartX;
        f2 = this.replyTextOffset;
        if (!this.needReplyImage) {
          break label3352;
        }
        i = 44;
        label2197:
        paramCanvas.translate(AndroidUtilities.dp(i + 10) + (f1 - f2), this.replyStartY + AndroidUtilities.dp(19.0F));
        this.replyTextLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
    }
    if ((this.drawTime) || (!this.mediaBackground))
    {
      if (!this.mediaBackground) {
        break label3563;
      }
      if (this.currentMessageObject.type != 13) {
        break label3358;
      }
      localObject = Theme.timeStickerBackgroundDrawable;
      label2277:
      j = this.timeX;
      k = AndroidUtilities.dp(4.0F);
      m = this.layoutHeight;
      n = AndroidUtilities.dp(27.0F);
      i1 = this.timeWidth;
      if (!this.currentMessageObject.isOutOwner()) {
        break label3366;
      }
      i = 20;
      label2325:
      setDrawableBounds((Drawable)localObject, j - k, m - n, i1 + AndroidUtilities.dp(i + 8), AndroidUtilities.dp(17.0F));
      ((Drawable)localObject).draw(paramCanvas);
      i = 0;
      if ((this.currentMessageObject.messageOwner.flags & 0x400) != 0)
      {
        j = (int)(this.timeWidth - this.timeLayout.getLineWidth(0));
        if (!this.currentMessageObject.isSending()) {
          break label3372;
        }
        i = j;
        if (!this.currentMessageObject.isOutOwner())
        {
          setDrawableBounds(Theme.clockMediaDrawable, this.timeX + AndroidUtilities.dp(11.0F), this.layoutHeight - AndroidUtilities.dp(13.0F) - Theme.clockMediaDrawable.getIntrinsicHeight());
          Theme.clockMediaDrawable.draw(paramCanvas);
          i = j;
        }
      }
      label2473:
      paramCanvas.save();
      paramCanvas.translate(this.timeX + i, this.layoutHeight - AndroidUtilities.dp(11.3F) - this.timeLayout.getHeight());
      this.timeLayout.draw(paramCanvas);
      paramCanvas.restore();
      if (this.currentMessageObject.isOutOwner())
      {
        i = 0;
        j = 0;
        k = 0;
        m = 0;
        if ((int)(this.currentMessageObject.getDialogId() >> 32) != 1) {
          break label4102;
        }
        n = 1;
        label2562:
        if (!this.currentMessageObject.isSending()) {
          break label4108;
        }
        i = 0;
        j = 0;
        k = 1;
        m = 0;
        label2584:
        if (k != 0)
        {
          if (this.mediaBackground) {
            break label4174;
          }
          setDrawableBounds(Theme.clockDrawable, this.layoutWidth - AndroidUtilities.dp(18.5F) - Theme.clockDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.5F) - Theme.clockDrawable.getIntrinsicHeight());
          Theme.clockDrawable.draw(paramCanvas);
        }
        label2646:
        if (n == 0) {
          break label4280;
        }
        if ((i != 0) || (j != 0))
        {
          if (this.mediaBackground) {
            break label4227;
          }
          setDrawableBounds(Theme.broadcastDrawable, this.layoutWidth - AndroidUtilities.dp(20.5F) - Theme.broadcastDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.0F) - Theme.broadcastDrawable.getIntrinsicHeight());
          Theme.broadcastDrawable.draw(paramCanvas);
        }
        label2718:
        if (m != 0)
        {
          if (this.mediaBackground) {
            break label4661;
          }
          setDrawableBounds(Theme.errorDrawable, this.layoutWidth - AndroidUtilities.dp(18.0F) - Theme.errorDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(7.0F) - Theme.errorDrawable.getIntrinsicHeight());
          Theme.errorDrawable.draw(paramCanvas);
        }
      }
    }
    label2780:
    if ((ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getBoolean("chat_contact_status", false)) && (statusDrawable != null) && (!this.currentMessageObject.isOutOwner()) && (this.isAvatarVisible))
    {
      i = getMeasuredHeight() - statusDrawable.getMinimumHeight() - AndroidUtilities.dp(0.0F);
      j = AndroidUtilities.dp(35.0F);
      statusDrawable.setBounds(j, i, statusDrawable.getIntrinsicWidth() + j, statusDrawable.getIntrinsicHeight() + i);
      if (((this.currentUser == null) || (this.currentUser.status == null) || (this.currentUser.status.expires <= ConnectionsManager.getInstance().getCurrentTime())) && (!MessagesController.getInstance().onlinePrivacy.containsKey(Integer.valueOf(this.currentUser.id)))) {
        break label4714;
      }
      ((GradientDrawable)statusDrawable).setColor(-14032632);
    }
    for (;;)
    {
      statusDrawable.draw(paramCanvas);
      return;
      label2957:
      this.replyStartX = (this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(17.0F));
      break label1861;
      label2981:
      i = 0;
      break label1922;
      label2987:
      if (this.currentMessageObject.isOutOwner())
      {
        replyLinePaint.setColor(localSharedPreferences.getInt("theme_rfname_color", -11162801));
        replyNamePaint.setColor(localSharedPreferences.getInt("theme_rfname_color", -11162801));
        if ((this.currentMessageObject.replyMessageObject != null) && (this.currentMessageObject.replyMessageObject.type == 0))
        {
          replyTextPaint.setColor(localSharedPreferences.getInt("theme_rtext_color", -16777216));
          label3077:
          this.replyStartX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(12.0F));
          label3098:
          if ((!this.drawForwardedName) || (this.forwardedNameLayout[0] == null)) {
            break label3334;
          }
          i = 36;
          label3118:
          if ((!this.drawNameLayout) || (this.nameLayout == null)) {
            break label3340;
          }
        }
      }
      label3310:
      label3334:
      label3340:
      for (j = 20;; j = 0)
      {
        this.replyStartY = AndroidUtilities.dp(j + (i + 12));
        break;
        replyTextPaint.setColor(localSharedPreferences.getInt("theme_rtext_color", -16777216));
        break label3077;
        replyLinePaint.setColor(localSharedPreferences.getInt("theme_lfname_color", -12940081));
        replyNamePaint.setColor(localSharedPreferences.getInt("theme_lfname_color", -12940081));
        if ((this.currentMessageObject.replyMessageObject != null) && (this.currentMessageObject.replyMessageObject.type == 0)) {
          replyTextPaint.setColor(localSharedPreferences.getInt("theme_ltext_color", -16777216));
        }
        for (;;)
        {
          if (!this.mediaBackground) {
            break label3310;
          }
          this.replyStartX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(12.0F));
          break;
          replyTextPaint.setColor(localSharedPreferences.getInt("theme_ltext_color", -16777216));
        }
        this.replyStartX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(18.0F));
        break label3098;
        i = 0;
        break label3118;
      }
      label3346:
      i = 0;
      break label2128;
      label3352:
      i = 0;
      break label2197;
      label3358:
      localObject = Theme.timeBackgroundDrawable;
      break label2277;
      label3366:
      i = 0;
      break label2325;
      label3372:
      if (this.currentMessageObject.isSendError())
      {
        i = j;
        if (this.currentMessageObject.isOutOwner()) {
          break label2473;
        }
        setDrawableBounds(Theme.errorDrawable, this.timeX + AndroidUtilities.dp(11.0F), this.layoutHeight - AndroidUtilities.dp(12.5F) - Theme.errorDrawable.getIntrinsicHeight());
        Theme.errorDrawable.draw(paramCanvas);
        i = j;
        break label2473;
      }
      localObject = Theme.viewsMediaCountDrawable;
      setDrawableBounds((Drawable)localObject, this.timeX, this.layoutHeight - AndroidUtilities.dp(9.5F) - this.timeLayout.getHeight());
      ((Drawable)localObject).draw(paramCanvas);
      i = j;
      if (this.viewsLayout == null) {
        break label2473;
      }
      paramCanvas.save();
      paramCanvas.translate(this.timeX + ((Drawable)localObject).getIntrinsicWidth() + AndroidUtilities.dp(3.0F), this.layoutHeight - AndroidUtilities.dp(11.3F) - this.timeLayout.getHeight());
      this.viewsLayout.draw(paramCanvas);
      paramCanvas.restore();
      i = j;
      break label2473;
      label3563:
      i = 0;
      if ((this.currentMessageObject.messageOwner.flags & 0x400) != 0)
      {
        j = (int)(this.timeWidth - this.timeLayout.getLineWidth(0));
        if (!this.currentMessageObject.isSending()) {
          break label3748;
        }
        i = j;
        if (!this.currentMessageObject.isOutOwner())
        {
          localObject = Theme.clockChannelDrawable;
          if (!isDrawSelectedBackground()) {
            break label3742;
          }
          i = 1;
          label3639:
          localObject = localObject[i];
          setDrawableBounds((Drawable)localObject, this.timeX + AndroidUtilities.dp(11.0F), this.layoutHeight - AndroidUtilities.dp(8.5F) - ((Drawable)localObject).getIntrinsicHeight());
          ((Drawable)localObject).draw(paramCanvas);
          i = j;
        }
      }
      for (;;)
      {
        paramCanvas.save();
        paramCanvas.translate(this.timeX + i, this.layoutHeight - AndroidUtilities.dp(6.5F) - this.timeLayout.getHeight());
        this.timeLayout.draw(paramCanvas);
        paramCanvas.restore();
        break;
        label3742:
        i = 0;
        break label3639;
        label3748:
        if (!this.currentMessageObject.isSendError()) {
          break label3822;
        }
        i = j;
        if (!this.currentMessageObject.isOutOwner())
        {
          setDrawableBounds(Theme.errorDrawable, this.timeX + AndroidUtilities.dp(11.0F), this.layoutHeight - AndroidUtilities.dp(6.5F) - Theme.errorDrawable.getIntrinsicHeight());
          Theme.errorDrawable.draw(paramCanvas);
          i = j;
        }
      }
      label3822:
      if (!this.currentMessageObject.isOutOwner())
      {
        localObject = Theme.viewsCountDrawable;
        if (isDrawSelectedBackground())
        {
          i = 1;
          label3847:
          setDrawableBounds(localObject[i], this.timeX, this.layoutHeight - AndroidUtilities.dp(4.5F) - this.timeLayout.getHeight());
          k = localSharedPreferences.getInt("theme_ltime_color", -6182221);
          localObject = Theme.viewsCountDrawable;
          if (!isDrawSelectedBackground()) {
            break label4027;
          }
          i = 1;
          label3909:
          MihanTheme.setColorFilter(localObject[i], k);
          localObject = Theme.viewsCountDrawable;
          if (!isDrawSelectedBackground()) {
            break label4033;
          }
          i = 1;
          label3934:
          localObject[i].draw(paramCanvas);
        }
      }
      for (;;)
      {
        i = j;
        if (this.viewsLayout == null) {
          break;
        }
        paramCanvas.save();
        paramCanvas.translate(this.timeX + Theme.viewsOutCountDrawable.getIntrinsicWidth() + AndroidUtilities.dp(3.0F), this.layoutHeight - AndroidUtilities.dp(6.5F) - this.timeLayout.getHeight());
        this.viewsLayout.draw(paramCanvas);
        paramCanvas.restore();
        i = j;
        break;
        i = 0;
        break label3847;
        label4027:
        i = 0;
        break label3909;
        label4033:
        i = 0;
        break label3934;
        setDrawableBounds(Theme.viewsOutCountDrawable, this.timeX, this.layoutHeight - AndroidUtilities.dp(4.5F) - this.timeLayout.getHeight());
        i = localSharedPreferences.getInt("theme_rtime_color", -9391780);
        MihanTheme.setColorFilter(Theme.viewsOutCountDrawable, i);
        Theme.viewsOutCountDrawable.draw(paramCanvas);
      }
      label4102:
      n = 0;
      break label2562;
      label4108:
      if (this.currentMessageObject.isSendError())
      {
        i = 0;
        j = 0;
        k = 0;
        m = 1;
        break label2584;
      }
      if (!this.currentMessageObject.isSent()) {
        break label2584;
      }
      if (!this.currentMessageObject.isUnread()) {}
      for (i = 1;; i = 0)
      {
        j = 1;
        k = 0;
        m = 0;
        break;
      }
      label4174:
      setDrawableBounds(Theme.clockMediaDrawable, this.layoutWidth - AndroidUtilities.dp(22.0F) - Theme.clockMediaDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(12.5F) - Theme.clockMediaDrawable.getIntrinsicHeight());
      Theme.clockMediaDrawable.draw(paramCanvas);
      break label2646;
      label4227:
      setDrawableBounds(Theme.broadcastMediaDrawable, this.layoutWidth - AndroidUtilities.dp(24.0F) - Theme.broadcastMediaDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(13.0F) - Theme.broadcastMediaDrawable.getIntrinsicHeight());
      Theme.broadcastMediaDrawable.draw(paramCanvas);
      break label2718;
      label4280:
      if (j != 0)
      {
        if (this.mediaBackground) {
          break label4504;
        }
        if (i == 0) {
          break label4458;
        }
        setDrawableBounds(Theme.checkDrawable, this.layoutWidth - AndroidUtilities.dp(22.5F) - Theme.checkDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.0F) - Theme.checkDrawable.getIntrinsicHeight());
      }
      for (;;)
      {
        j = localSharedPreferences.getInt("theme_rtime_color", -9391780);
        MihanTheme.setColorFilter(Theme.checkDrawable, j);
        Theme.checkDrawable.draw(paramCanvas);
        label4370:
        if (i == 0) {
          break label4560;
        }
        if (this.mediaBackground) {
          break label4608;
        }
        setDrawableBounds(Theme.halfCheckDrawable, this.layoutWidth - AndroidUtilities.dp(18.0F) - Theme.halfCheckDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.0F) - Theme.halfCheckDrawable.getIntrinsicHeight());
        i = localSharedPreferences.getInt("theme_rtime_color", -9391780);
        MihanTheme.setColorFilter(Theme.halfCheckDrawable, i);
        Theme.halfCheckDrawable.draw(paramCanvas);
        break;
        label4458:
        setDrawableBounds(Theme.checkDrawable, this.layoutWidth - AndroidUtilities.dp(18.5F) - Theme.checkDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.0F) - Theme.checkDrawable.getIntrinsicHeight());
      }
      label4504:
      if (i != 0) {
        setDrawableBounds(Theme.checkMediaDrawable, this.layoutWidth - AndroidUtilities.dp(26.3F) - Theme.checkMediaDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(12.5F) - Theme.checkMediaDrawable.getIntrinsicHeight());
      }
      for (;;)
      {
        Theme.checkMediaDrawable.draw(paramCanvas);
        break label4370;
        label4560:
        break;
        setDrawableBounds(Theme.checkMediaDrawable, this.layoutWidth - AndroidUtilities.dp(21.5F) - Theme.checkMediaDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(12.5F) - Theme.checkMediaDrawable.getIntrinsicHeight());
      }
      label4608:
      setDrawableBounds(Theme.halfCheckMediaDrawable, this.layoutWidth - AndroidUtilities.dp(21.5F) - Theme.halfCheckMediaDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(12.5F) - Theme.halfCheckMediaDrawable.getIntrinsicHeight());
      Theme.halfCheckMediaDrawable.draw(paramCanvas);
      break label2718;
      label4661:
      setDrawableBounds(Theme.errorDrawable, this.layoutWidth - AndroidUtilities.dp(20.5F) - Theme.errorDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(11.5F) - Theme.errorDrawable.getIntrinsicHeight());
      Theme.errorDrawable.draw(paramCanvas);
      break label2780;
      break;
      label4714:
      if ((this.currentUser == null) || (this.currentUser.status == null) || (this.currentUser.status.expires == 0) || (UserObject.isDeleted(this.currentUser)) || ((this.currentUser instanceof TLRPC.TL_userEmpty))) {
        ((GradientDrawable)statusDrawable).setColor(-16777216);
      } else {
        ((GradientDrawable)statusDrawable).setColor(-3355444);
      }
    }
  }
  
  public void onFailedDownload(String paramString)
  {
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5)) {}
    for (boolean bool = true;; bool = false)
    {
      updateButtonState(bool);
      return;
    }
  }
  
  @SuppressLint({"DrawAllocation"})
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.currentMessageObject == null)
    {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    label184:
    label197:
    label242:
    label393:
    Object localObject;
    if ((paramBoolean) || (!this.wasLayout))
    {
      this.layoutWidth = getMeasuredWidth();
      this.layoutHeight = (getMeasuredHeight() - this.substractBackgroundHeight);
      if (this.timeTextWidth < 0) {
        this.timeTextWidth = AndroidUtilities.dp(10.0F);
      }
      if ((ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getBoolean("persian_date", false)) && (LocaleController.isRTL))
      {
        this.timeLayout = new StaticLayout(this.currentTimeString, timePaint, this.timeTextWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        if (this.mediaBackground) {
          break label677;
        }
        if (this.currentMessageObject.isOutOwner()) {
          break label654;
        }
        paramInt2 = this.backgroundWidth;
        paramInt3 = AndroidUtilities.dp(9.0F);
        paramInt4 = this.timeWidth;
        if ((!this.isChat) || (!this.currentMessageObject.isFromUser())) {
          break label649;
        }
        paramInt1 = AndroidUtilities.dp(48.0F);
        this.timeX = (paramInt1 + (paramInt2 - paramInt3 - paramInt4));
        if ((this.currentMessageObject.messageOwner.flags & 0x400) == 0) {
          break label774;
        }
        this.viewsLayout = new StaticLayout(this.currentViewsString, timePaint, this.viewsTextWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        if (this.isAvatarVisible) {
          this.avatarImage.setImageCoords(AndroidUtilities.dp(6.0F), this.layoutHeight - AndroidUtilities.dp(44.0F), AndroidUtilities.dp(42.0F), AndroidUtilities.dp(42.0F));
        }
        this.wasLayout = true;
      }
    }
    else
    {
      if (this.currentMessageObject.type == 0) {
        this.textY = (AndroidUtilities.dp(10.0F) + this.namesOffset);
      }
      if (this.documentAttachType != 3) {
        break label875;
      }
      if (!this.currentMessageObject.isOutOwner()) {
        break label782;
      }
      this.seekBarX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(57.0F));
      this.buttonX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(14.0F));
      this.timeAudioX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(67.0F));
      if (this.hasLinkPreview)
      {
        this.seekBarX += AndroidUtilities.dp(10.0F);
        this.buttonX += AndroidUtilities.dp(10.0F);
        this.timeAudioX += AndroidUtilities.dp(10.0F);
      }
      localObject = this.seekBarWaveform;
      paramInt2 = this.backgroundWidth;
      if (!this.hasLinkPreview) {
        break label865;
      }
      paramInt1 = 10;
      label466:
      ((SeekBarWaveform)localObject).setSize(paramInt2 - AndroidUtilities.dp(paramInt1 + 92), AndroidUtilities.dp(30.0F));
      localObject = this.seekBar;
      paramInt2 = this.backgroundWidth;
      if (!this.hasLinkPreview) {
        break label870;
      }
    }
    label649:
    label654:
    label677:
    label774:
    label782:
    label865:
    label870:
    for (paramInt1 = 10;; paramInt1 = 0)
    {
      ((SeekBar)localObject).setSize(paramInt2 - AndroidUtilities.dp(paramInt1 + 72), AndroidUtilities.dp(30.0F));
      this.seekBarY = (AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
      this.buttonY = (AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
      this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0F), this.buttonY + AndroidUtilities.dp(44.0F));
      updateAudioProgress();
      return;
      this.timeLayout = new StaticLayout(this.currentTimeString, timePaint, this.timeTextWidth + AndroidUtilities.dp(6.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      break;
      paramInt1 = 0;
      break label184;
      this.timeX = (this.layoutWidth - this.timeWidth - AndroidUtilities.dp(38.5F));
      break label197;
      if (!this.currentMessageObject.isOutOwner())
      {
        paramInt2 = this.backgroundWidth;
        paramInt3 = AndroidUtilities.dp(4.0F);
        paramInt4 = this.timeWidth;
        if ((this.isChat) && (this.currentMessageObject.isFromUser())) {}
        for (paramInt1 = AndroidUtilities.dp(48.0F);; paramInt1 = 0)
        {
          this.timeX = (paramInt1 + (paramInt2 - paramInt3 - paramInt4));
          break;
        }
      }
      this.timeX = (this.layoutWidth - this.timeWidth - AndroidUtilities.dp(42.0F));
      break label197;
      this.viewsLayout = null;
      break label242;
      if ((this.isChat) && (this.currentMessageObject.isFromUser()))
      {
        this.seekBarX = AndroidUtilities.dp(114.0F);
        this.buttonX = AndroidUtilities.dp(71.0F);
        this.timeAudioX = AndroidUtilities.dp(124.0F);
        break label393;
      }
      this.seekBarX = AndroidUtilities.dp(66.0F);
      this.buttonX = AndroidUtilities.dp(23.0F);
      this.timeAudioX = AndroidUtilities.dp(76.0F);
      break label393;
      paramInt1 = 0;
      break label466;
    }
    label875:
    if (this.documentAttachType == 5)
    {
      if (this.currentMessageObject.isOutOwner())
      {
        this.seekBarX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(56.0F));
        this.buttonX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(14.0F));
        this.timeAudioX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(67.0F));
        if (this.hasLinkPreview)
        {
          this.seekBarX += AndroidUtilities.dp(10.0F);
          this.buttonX += AndroidUtilities.dp(10.0F);
          this.timeAudioX += AndroidUtilities.dp(10.0F);
        }
        localObject = this.seekBar;
        paramInt2 = this.backgroundWidth;
        if (!this.hasLinkPreview) {
          break label1212;
        }
      }
      label1212:
      for (paramInt1 = 10;; paramInt1 = 0)
      {
        ((SeekBar)localObject).setSize(paramInt2 - AndroidUtilities.dp(paramInt1 + 65), AndroidUtilities.dp(30.0F));
        this.seekBarY = (AndroidUtilities.dp(29.0F) + this.namesOffset + this.mediaOffsetY);
        this.buttonY = (AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
        this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0F), this.buttonY + AndroidUtilities.dp(44.0F));
        updateAudioProgress();
        return;
        if ((this.isChat) && (this.currentMessageObject.isFromUser()))
        {
          this.seekBarX = AndroidUtilities.dp(113.0F);
          this.buttonX = AndroidUtilities.dp(71.0F);
          this.timeAudioX = AndroidUtilities.dp(124.0F);
          break;
        }
        this.seekBarX = AndroidUtilities.dp(65.0F);
        this.buttonX = AndroidUtilities.dp(23.0F);
        this.timeAudioX = AndroidUtilities.dp(76.0F);
        break;
      }
    }
    if ((this.documentAttachType == 1) && (!this.drawPhotoImage))
    {
      if (this.currentMessageObject.isOutOwner()) {
        this.buttonX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(14.0F));
      }
      for (;;)
      {
        if (this.hasLinkPreview) {
          this.buttonX += AndroidUtilities.dp(10.0F);
        }
        this.buttonY = (AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
        this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0F), this.buttonY + AndroidUtilities.dp(44.0F));
        this.photoImage.setImageCoords(this.buttonX - AndroidUtilities.dp(10.0F), this.buttonY - AndroidUtilities.dp(10.0F), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
        return;
        if ((this.isChat) && (this.currentMessageObject.isFromUser())) {
          this.buttonX = AndroidUtilities.dp(71.0F);
        } else {
          this.buttonX = AndroidUtilities.dp(23.0F);
        }
      }
    }
    if (this.currentMessageObject.type == 12)
    {
      if (this.currentMessageObject.isOutOwner()) {
        paramInt1 = this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(14.0F);
      }
      for (;;)
      {
        this.photoImage.setImageCoords(paramInt1, AndroidUtilities.dp(13.0F) + this.namesOffset, AndroidUtilities.dp(44.0F), AndroidUtilities.dp(44.0F));
        return;
        if ((this.isChat) && (this.currentMessageObject.isFromUser())) {
          paramInt1 = AndroidUtilities.dp(72.0F);
        } else {
          paramInt1 = AndroidUtilities.dp(23.0F);
        }
      }
    }
    if (this.currentMessageObject.isOutOwner()) {
      if (this.mediaBackground) {
        paramInt1 = this.layoutWidth - this.backgroundWidth - AndroidUtilities.dp(3.0F);
      }
    }
    for (;;)
    {
      this.photoImage.setImageCoords(paramInt1, this.photoImage.getImageY(), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
      this.buttonX = ((int)(paramInt1 + (this.photoImage.getImageWidth() - AndroidUtilities.dp(48.0F)) / 2.0F));
      this.buttonY = ((int)(AndroidUtilities.dp(7.0F) + (this.photoImage.getImageHeight() - AndroidUtilities.dp(48.0F)) / 2.0F) + this.namesOffset);
      this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(48.0F), this.buttonY + AndroidUtilities.dp(48.0F));
      this.deleteProgressRect.set(this.buttonX + AndroidUtilities.dp(3.0F), this.buttonY + AndroidUtilities.dp(3.0F), this.buttonX + AndroidUtilities.dp(45.0F), this.buttonY + AndroidUtilities.dp(45.0F));
      return;
      paramInt1 = this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(6.0F);
      continue;
      if ((this.isChat) && (this.currentMessageObject.isFromUser())) {
        paramInt1 = AndroidUtilities.dp(63.0F);
      } else {
        paramInt1 = AndroidUtilities.dp(15.0F);
      }
    }
  }
  
  protected void onLongPress()
  {
    if ((this.pressedLink instanceof URLSpanNoUnderline))
    {
      if (!((URLSpanNoUnderline)this.pressedLink).getURL().startsWith("/")) {
        break label77;
      }
      this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, true);
    }
    label77:
    do
    {
      return;
      if ((this.pressedLink instanceof URLSpan))
      {
        this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, true);
        return;
      }
      resetPressedLink(-1);
      if ((this.buttonPressed != 0) || (this.pressedBotButton != -1))
      {
        this.buttonPressed = 0;
        this.pressedBotButton = -1;
        invalidate();
      }
    } while (this.delegate == null);
    this.delegate.didLongPressed(this);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), this.totalHeight + this.keyboardHeight);
  }
  
  public void onProgressDownload(String paramString, float paramFloat)
  {
    this.radialProgress.setProgress(paramFloat, true);
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5)) {
      if (this.buttonState != 4) {
        updateButtonState(false);
      }
    }
    while (this.buttonState == 1) {
      return;
    }
    updateButtonState(false);
  }
  
  public void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean)
  {
    this.radialProgress.setProgress(paramFloat, true);
  }
  
  public void onProvideStructure(ViewStructure paramViewStructure)
  {
    super.onProvideStructure(paramViewStructure);
    if ((this.allowAssistant) && (Build.VERSION.SDK_INT >= 23))
    {
      if ((this.currentMessageObject.messageText == null) || (this.currentMessageObject.messageText.length() <= 0)) {
        break label57;
      }
      paramViewStructure.setText(this.currentMessageObject.messageText);
    }
    label57:
    while ((this.currentMessageObject.caption == null) || (this.currentMessageObject.caption.length() <= 0)) {
      return;
    }
    paramViewStructure.setText(this.currentMessageObject.caption);
  }
  
  public void onSeekBarDrag(float paramFloat)
  {
    if (this.currentMessageObject == null) {
      return;
    }
    this.currentMessageObject.audioProgress = paramFloat;
    MediaController.getInstance().seekToProgress(this.currentMessageObject, paramFloat);
  }
  
  public void onSuccessDownload(String paramString)
  {
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
    {
      updateButtonState(true);
      updateWaveform();
    }
    for (;;)
    {
      return;
      this.radialProgress.setProgress(1.0F, true);
      if (this.currentMessageObject.type == 0)
      {
        if ((this.documentAttachType == 2) && (this.currentMessageObject.audioProgress != 1.0F))
        {
          this.buttonState = 2;
          didPressedButton(true);
          return;
        }
        if (!this.photoNotSet)
        {
          updateButtonState(true);
          return;
        }
        setMessageObject(this.currentMessageObject);
        return;
      }
      if ((!this.photoNotSet) || ((this.currentMessageObject.type == 8) && (this.currentMessageObject.audioProgress != 1.0F)))
      {
        if ((this.currentMessageObject.type != 8) || (this.currentMessageObject.audioProgress == 1.0F)) {
          break label184;
        }
        this.photoNotSet = false;
        this.buttonState = 2;
        didPressedButton(true);
      }
      while (this.photoNotSet)
      {
        setMessageObject(this.currentMessageObject);
        return;
        label184:
        updateButtonState(true);
      }
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool3;
    if ((this.currentMessageObject == null) || (!this.delegate.canPerformActions())) {
      bool3 = super.onTouchEvent(paramMotionEvent);
    }
    boolean bool2;
    float f1;
    float f2;
    label782:
    label946:
    label1139:
    label1330:
    label1412:
    label1570:
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
                                  do
                                  {
                                    do
                                    {
                                      boolean bool1;
                                      do
                                      {
                                        do
                                        {
                                          return bool3;
                                          this.disallowLongPress = false;
                                          bool2 = checkTextBlockMotionEvent(paramMotionEvent);
                                          bool1 = bool2;
                                          if (!bool2) {
                                            bool1 = checkOtherButtonMotionEvent(paramMotionEvent);
                                          }
                                          bool2 = bool1;
                                          if (!bool1) {
                                            bool2 = checkLinkPreviewMotionEvent(paramMotionEvent);
                                          }
                                          bool1 = bool2;
                                          if (!bool2) {
                                            bool1 = checkCaptionMotionEvent(paramMotionEvent);
                                          }
                                          bool2 = bool1;
                                          if (!bool1) {
                                            bool2 = checkAudioMotionEvent(paramMotionEvent);
                                          }
                                          bool1 = bool2;
                                          if (!bool2) {
                                            bool1 = checkPhotoImageMotionEvent(paramMotionEvent);
                                          }
                                          bool2 = bool1;
                                          if (!bool1) {
                                            bool2 = checkBotButtonMotionEvent(paramMotionEvent);
                                          }
                                          if (paramMotionEvent.getAction() == 3)
                                          {
                                            this.buttonPressed = 0;
                                            this.pressedBotButton = -1;
                                            this.linkPreviewPressed = false;
                                            this.otherPressed = false;
                                            this.imagePressed = false;
                                            bool2 = false;
                                            resetPressedLink(-1);
                                          }
                                          if ((!this.disallowLongPress) && (bool2) && (paramMotionEvent.getAction() == 0)) {
                                            startCheckLongPress();
                                          }
                                          if ((paramMotionEvent.getAction() != 0) && (paramMotionEvent.getAction() != 2)) {
                                            cancelCheckLongPress();
                                          }
                                          bool3 = bool2;
                                        } while (bool2);
                                        f1 = paramMotionEvent.getX();
                                        f2 = paramMotionEvent.getY();
                                        if (paramMotionEvent.getAction() != 0) {
                                          break label782;
                                        }
                                        if (this.delegate == null) {
                                          break;
                                        }
                                        bool3 = bool2;
                                      } while (!this.delegate.canPerformActions());
                                      if ((this.isAvatarVisible) && (this.avatarImage.isInsideImage(f1, f2)))
                                      {
                                        this.avatarPressed = true;
                                        bool1 = true;
                                      }
                                      for (;;)
                                      {
                                        bool3 = bool1;
                                        if (!bool1) {
                                          break;
                                        }
                                        startCheckLongPress();
                                        return bool1;
                                        if ((this.drawForwardedName) && (this.forwardedNameLayout[0] != null) && (f1 >= this.forwardNameX) && (f1 <= this.forwardNameX + this.forwardedNameWidth) && (f2 >= this.forwardNameY) && (f2 <= this.forwardNameY + AndroidUtilities.dp(32.0F)))
                                        {
                                          if ((this.viaWidth != 0) && (f1 >= this.forwardNameX + this.viaNameWidth + AndroidUtilities.dp(4.0F))) {
                                            this.forwardBotPressed = true;
                                          }
                                          for (;;)
                                          {
                                            bool1 = true;
                                            break;
                                            this.forwardNamePressed = true;
                                          }
                                        }
                                        if ((this.drawNameLayout) && (this.nameLayout != null) && (this.viaWidth != 0) && (f1 >= this.nameX + this.viaNameWidth) && (f1 <= this.nameX + this.viaNameWidth + this.viaWidth) && (f2 >= this.nameY - AndroidUtilities.dp(4.0F)) && (f2 <= this.nameY + AndroidUtilities.dp(20.0F)))
                                        {
                                          this.forwardBotPressed = true;
                                          bool1 = true;
                                        }
                                        else if ((this.currentMessageObject.isReply()) && (f1 >= this.replyStartX) && (f1 <= this.replyStartX + Math.max(this.replyNameWidth, this.replyTextWidth)) && (f2 >= this.replyStartY) && (f2 <= this.replyStartY + AndroidUtilities.dp(35.0F)))
                                        {
                                          this.replyPressed = true;
                                          bool1 = true;
                                        }
                                        else if ((this.drawShareButton) && (f1 >= this.shareStartX) && (f1 <= this.shareStartX + AndroidUtilities.dp(40.0F)) && (f2 >= this.shareStartY) && (f2 <= this.shareStartY + AndroidUtilities.dp(32.0F)))
                                        {
                                          this.sharePressed = true;
                                          bool1 = true;
                                          invalidate();
                                        }
                                        else
                                        {
                                          bool1 = bool2;
                                          if (this.drawDirectReply)
                                          {
                                            bool1 = bool2;
                                            if (f1 >= this.shareStartX)
                                            {
                                              bool1 = bool2;
                                              if (f1 <= this.shareStartX + AndroidUtilities.dp(40.0F))
                                              {
                                                bool1 = bool2;
                                                if (f2 >= this.shareStartY)
                                                {
                                                  bool1 = bool2;
                                                  if (f2 <= this.shareStartY + AndroidUtilities.dp(32.0F))
                                                  {
                                                    this.directReplyPressed = true;
                                                    bool1 = true;
                                                    invalidate();
                                                  }
                                                }
                                              }
                                            }
                                          }
                                        }
                                      }
                                      if (paramMotionEvent.getAction() != 2) {
                                        cancelCheckLongPress();
                                      }
                                      if (!this.avatarPressed) {
                                        break label946;
                                      }
                                      if (paramMotionEvent.getAction() != 1) {
                                        break;
                                      }
                                      this.avatarPressed = false;
                                      playSoundEffect(0);
                                      bool3 = bool2;
                                    } while (this.delegate == null);
                                    if (this.currentUser != null)
                                    {
                                      this.delegate.didPressedUserAvatar(this, this.currentUser);
                                      return bool2;
                                    }
                                    bool3 = bool2;
                                  } while (this.currentChat == null);
                                  this.delegate.didPressedChannelAvatar(this, this.currentChat, 0);
                                  return bool2;
                                  if (paramMotionEvent.getAction() == 3)
                                  {
                                    this.avatarPressed = false;
                                    return bool2;
                                  }
                                  bool3 = bool2;
                                } while (paramMotionEvent.getAction() != 2);
                                bool3 = bool2;
                              } while (!this.isAvatarVisible);
                              bool3 = bool2;
                            } while (this.avatarImage.isInsideImage(f1, f2));
                            this.avatarPressed = false;
                            return bool2;
                            if (!this.forwardNamePressed) {
                              break label1139;
                            }
                            if (paramMotionEvent.getAction() != 1) {
                              break;
                            }
                            this.forwardNamePressed = false;
                            playSoundEffect(0);
                            bool3 = bool2;
                          } while (this.delegate == null);
                          if (this.currentForwardChannel != null)
                          {
                            this.delegate.didPressedChannelAvatar(this, this.currentForwardChannel, this.currentMessageObject.messageOwner.fwd_from.channel_post);
                            return bool2;
                          }
                          bool3 = bool2;
                        } while (this.currentForwardUser == null);
                        this.delegate.didPressedUserAvatar(this, this.currentForwardUser);
                        return bool2;
                        if (paramMotionEvent.getAction() == 3)
                        {
                          this.forwardNamePressed = false;
                          return bool2;
                        }
                        bool3 = bool2;
                      } while (paramMotionEvent.getAction() != 2);
                      if ((f1 < this.forwardNameX) || (f1 > this.forwardNameX + this.forwardedNameWidth) || (f2 < this.forwardNameY)) {
                        break;
                      }
                      bool3 = bool2;
                    } while (f2 <= this.forwardNameY + AndroidUtilities.dp(32.0F));
                    this.forwardNamePressed = false;
                    return bool2;
                    if (!this.forwardBotPressed) {
                      break label1412;
                    }
                    if (paramMotionEvent.getAction() != 1) {
                      break;
                    }
                    this.forwardBotPressed = false;
                    playSoundEffect(0);
                    bool3 = bool2;
                  } while (this.delegate == null);
                  ChatMessageCellDelegate localChatMessageCellDelegate = this.delegate;
                  if (this.currentViaBotUser != null) {}
                  for (paramMotionEvent = this.currentViaBotUser.username;; paramMotionEvent = this.currentMessageObject.messageOwner.via_bot_name)
                  {
                    localChatMessageCellDelegate.didPressedViaBot(this, paramMotionEvent);
                    return bool2;
                  }
                  if (paramMotionEvent.getAction() == 3)
                  {
                    this.forwardBotPressed = false;
                    return bool2;
                  }
                  bool3 = bool2;
                } while (paramMotionEvent.getAction() != 2);
                if ((!this.drawForwardedName) || (this.forwardedNameLayout[0] == null)) {
                  break label1330;
                }
                if ((f1 < this.forwardNameX) || (f1 > this.forwardNameX + this.forwardedNameWidth) || (f2 < this.forwardNameY)) {
                  break;
                }
                bool3 = bool2;
              } while (f2 <= this.forwardNameY + AndroidUtilities.dp(32.0F));
              this.forwardBotPressed = false;
              return bool2;
              if ((f1 < this.nameX + this.viaNameWidth) || (f1 > this.nameX + this.viaNameWidth + this.viaWidth) || (f2 < this.nameY - AndroidUtilities.dp(4.0F))) {
                break;
              }
              bool3 = bool2;
            } while (f2 <= this.nameY + AndroidUtilities.dp(20.0F));
            this.forwardBotPressed = false;
            return bool2;
            if (!this.replyPressed) {
              break label1570;
            }
            if (paramMotionEvent.getAction() != 1) {
              break;
            }
            this.replyPressed = false;
            playSoundEffect(0);
            bool3 = bool2;
          } while (this.delegate == null);
          this.delegate.didPressedReplyMessage(this, this.currentMessageObject.messageOwner.reply_to_msg_id);
          return bool2;
          if (paramMotionEvent.getAction() == 3)
          {
            this.replyPressed = false;
            return bool2;
          }
          bool3 = bool2;
        } while (paramMotionEvent.getAction() != 2);
        if ((f1 < this.replyStartX) || (f1 > this.replyStartX + Math.max(this.replyNameWidth, this.replyTextWidth)) || (f2 < this.replyStartY)) {
          break;
        }
        bool3 = bool2;
      } while (f2 <= this.replyStartY + AndroidUtilities.dp(35.0F));
      this.replyPressed = false;
      return bool2;
      if (this.sharePressed)
      {
        if (paramMotionEvent.getAction() == 1)
        {
          this.sharePressed = false;
          playSoundEffect(0);
          if (this.delegate != null) {
            this.delegate.didPressedShare(this);
          }
        }
        for (;;)
        {
          invalidate();
          return bool2;
          if (paramMotionEvent.getAction() == 3) {
            this.sharePressed = false;
          } else if ((paramMotionEvent.getAction() == 2) && ((f1 < this.shareStartX) || (f1 > this.shareStartX + AndroidUtilities.dp(40.0F)) || (f2 < this.shareStartY) || (f2 > this.shareStartY + AndroidUtilities.dp(32.0F)))) {
            this.sharePressed = false;
          }
        }
      }
      bool3 = bool2;
    } while (!this.directReplyPressed);
    if (paramMotionEvent.getAction() == 1)
    {
      this.directReplyPressed = false;
      playSoundEffect(0);
      if (this.delegate != null) {
        this.delegate.didPressedDirectReply(this);
      }
    }
    for (;;)
    {
      invalidate();
      return bool2;
      if (paramMotionEvent.getAction() == 3) {
        this.directReplyPressed = false;
      } else if ((paramMotionEvent.getAction() == 2) && ((f1 < this.shareStartX) || (f1 > this.shareStartX + AndroidUtilities.dp(40.0F)) || (f2 < this.shareStartY) || (f2 > this.shareStartY + AndroidUtilities.dp(32.0F)))) {
        this.directReplyPressed = false;
      }
    }
  }
  
  public void setAllowAssistant(boolean paramBoolean)
  {
    this.allowAssistant = paramBoolean;
  }
  
  public void setCheckPressed(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.isCheckPressed = paramBoolean1;
    this.isPressed = paramBoolean2;
    this.radialProgress.swapBackground(getDrawableForCurrentState());
    if (this.useSeekBarWaweform) {
      this.seekBarWaveform.setSelected(isDrawSelectedBackground());
    }
    for (;;)
    {
      invalidate();
      return;
      this.seekBar.setSelected(isDrawSelectedBackground());
    }
  }
  
  public void setDelegate(ChatMessageCellDelegate paramChatMessageCellDelegate)
  {
    this.delegate = paramChatMessageCellDelegate;
  }
  
  public void setHighlighted(boolean paramBoolean)
  {
    if (this.isHighlighted == paramBoolean) {
      return;
    }
    this.isHighlighted = paramBoolean;
    this.radialProgress.swapBackground(getDrawableForCurrentState());
    if (this.useSeekBarWaweform) {
      this.seekBarWaveform.setSelected(isDrawSelectedBackground());
    }
    for (;;)
    {
      invalidate();
      return;
      this.seekBar.setSelected(isDrawSelectedBackground());
    }
  }
  
  public void setHighlightedText(String paramString)
  {
    if ((this.currentMessageObject.messageOwner.message == null) || (this.currentMessageObject == null) || (this.currentMessageObject.type != 0) || (TextUtils.isEmpty(this.currentMessageObject.messageText)) || (paramString == null)) {
      if (!this.urlPathSelection.isEmpty())
      {
        this.linkSelectionBlockNum = -1;
        resetUrlPaths(true);
        invalidate();
      }
    }
    for (;;)
    {
      return;
      int k = TextUtils.indexOf(this.currentMessageObject.messageOwner.message.toLowerCase(), paramString.toLowerCase());
      if (k == -1)
      {
        if (!this.urlPathSelection.isEmpty())
        {
          this.linkSelectionBlockNum = -1;
          resetUrlPaths(true);
          invalidate();
        }
      }
      else
      {
        int j = k + paramString.length();
        int i = 0;
        while (i < this.currentMessageObject.textLayoutBlocks.size())
        {
          paramString = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i);
          if ((k >= paramString.charactersOffset) && (k < paramString.charactersOffset + paramString.textLayout.getText().length()))
          {
            this.linkSelectionBlockNum = i;
            resetUrlPaths(true);
            for (;;)
            {
              try
              {
                Object localObject = obtainNewUrlPath(true);
                int m = paramString.textLayout.getText().length();
                ((LinkPath)localObject).setCurrentLayout(paramString.textLayout, k, 0.0F);
                paramString.textLayout.getSelectionPath(k, j - paramString.charactersOffset, (Path)localObject);
                if (j >= paramString.charactersOffset + m)
                {
                  i += 1;
                  if (i < this.currentMessageObject.textLayoutBlocks.size())
                  {
                    localObject = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i);
                    k = ((MessageObject.TextLayoutBlock)localObject).textLayout.getText().length();
                    LinkPath localLinkPath = obtainNewUrlPath(true);
                    localLinkPath.setCurrentLayout(((MessageObject.TextLayoutBlock)localObject).textLayout, 0, ((MessageObject.TextLayoutBlock)localObject).height);
                    ((MessageObject.TextLayoutBlock)localObject).textLayout.getSelectionPath(0, j - ((MessageObject.TextLayoutBlock)localObject).charactersOffset, localLinkPath);
                    m = paramString.charactersOffset;
                    if (j >= m + k - 1) {
                      continue;
                    }
                  }
                }
              }
              catch (Exception paramString)
              {
                FileLog.e("tmessages", paramString);
                continue;
              }
              invalidate();
              return;
              i += 1;
            }
          }
          i += 1;
        }
      }
    }
  }
  
  /* Error */
  public void setMessageObject(MessageObject paramMessageObject)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   4: ifnull +17 -> 21
    //   7: aload_0
    //   8: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   11: invokevirtual 2512	org/telegram/messenger/MessageObject:getId	()I
    //   14: aload_1
    //   15: invokevirtual 2512	org/telegram/messenger/MessageObject:getId	()I
    //   18: if_icmpeq +1688 -> 1706
    //   21: iconst_1
    //   22: istore 18
    //   24: aload_0
    //   25: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   28: aload_1
    //   29: if_acmpne +10 -> 39
    //   32: aload_1
    //   33: getfield 2515	org/telegram/messenger/MessageObject:forceUpdate	Z
    //   36: ifeq +1676 -> 1712
    //   39: iconst_1
    //   40: istore 8
    //   42: aload_0
    //   43: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   46: aload_1
    //   47: if_acmpne +1671 -> 1718
    //   50: aload_0
    //   51: invokespecial 2517	org/telegram/ui/Cells/ChatMessageCell:isUserDataChanged	()Z
    //   54: ifne +10 -> 64
    //   57: aload_0
    //   58: getfield 1800	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   61: ifeq +1657 -> 1718
    //   64: iconst_1
    //   65: istore 26
    //   67: iload 8
    //   69: ifne +16 -> 85
    //   72: iload 26
    //   74: ifne +11 -> 85
    //   77: aload_0
    //   78: aload_1
    //   79: invokespecial 2519	org/telegram/ui/Cells/ChatMessageCell:isPhotoDataChanged	(Lorg/telegram/messenger/MessageObject;)Z
    //   82: ifeq +11951 -> 12033
    //   85: aload_0
    //   86: aload_1
    //   87: putfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   90: aload_0
    //   91: aload_1
    //   92: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   95: getfield 1821	org/telegram/tgnet/TLRPC$Message:send_state	I
    //   98: putfield 1818	org/telegram/ui/Cells/ChatMessageCell:lastSendState	I
    //   101: aload_0
    //   102: aload_1
    //   103: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   106: getfield 1451	org/telegram/tgnet/TLRPC$Message:destroyTime	I
    //   109: putfield 1823	org/telegram/ui/Cells/ChatMessageCell:lastDeleteDate	I
    //   112: aload_0
    //   113: aload_1
    //   114: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   117: getfield 1828	org/telegram/tgnet/TLRPC$Message:views	I
    //   120: putfield 1825	org/telegram/ui/Cells/ChatMessageCell:lastViewsCount	I
    //   123: aload_0
    //   124: iconst_0
    //   125: putfield 1757	org/telegram/ui/Cells/ChatMessageCell:isPressed	Z
    //   128: aload_0
    //   129: iconst_1
    //   130: putfield 280	org/telegram/ui/Cells/ChatMessageCell:isCheckPressed	Z
    //   133: aload_0
    //   134: iconst_0
    //   135: putfield 1830	org/telegram/ui/Cells/ChatMessageCell:isAvatarVisible	Z
    //   138: aload_0
    //   139: iconst_0
    //   140: putfield 2144	org/telegram/ui/Cells/ChatMessageCell:wasLayout	Z
    //   143: aload_0
    //   144: aload_0
    //   145: aload_1
    //   146: invokespecial 2521	org/telegram/ui/Cells/ChatMessageCell:checkNeedDrawShareButton	(Lorg/telegram/messenger/MessageObject;)Z
    //   149: putfield 2160	org/telegram/ui/Cells/ChatMessageCell:drawShareButton	Z
    //   152: aload_0
    //   153: iconst_0
    //   154: putfield 2218	org/telegram/ui/Cells/ChatMessageCell:drawDirectReply	Z
    //   157: aload_0
    //   158: aconst_null
    //   159: putfield 2067	org/telegram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   162: aload_0
    //   163: aconst_null
    //   164: putfield 1841	org/telegram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   167: aload_0
    //   168: iconst_0
    //   169: putfield 2069	org/telegram/ui/Cells/ChatMessageCell:replyNameWidth	I
    //   172: aload_0
    //   173: iconst_0
    //   174: putfield 2073	org/telegram/ui/Cells/ChatMessageCell:replyTextWidth	I
    //   177: aload_0
    //   178: iconst_0
    //   179: putfield 1986	org/telegram/ui/Cells/ChatMessageCell:viaWidth	I
    //   182: aload_0
    //   183: iconst_0
    //   184: putfield 1997	org/telegram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   187: aload_0
    //   188: aconst_null
    //   189: putfield 1857	org/telegram/ui/Cells/ChatMessageCell:currentReplyPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   192: aload_0
    //   193: aconst_null
    //   194: putfield 1814	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   197: aload_0
    //   198: aconst_null
    //   199: putfield 1816	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   202: aload_0
    //   203: aconst_null
    //   204: putfield 1988	org/telegram/ui/Cells/ChatMessageCell:currentViaBotUser	Lorg/telegram/tgnet/TLRPC$User;
    //   207: aload_0
    //   208: iconst_0
    //   209: putfield 1990	org/telegram/ui/Cells/ChatMessageCell:drawNameLayout	Z
    //   212: aload_0
    //   213: iconst_m1
    //   214: invokespecial 724	org/telegram/ui/Cells/ChatMessageCell:resetPressedLink	(I)V
    //   217: aload_1
    //   218: iconst_0
    //   219: putfield 2515	org/telegram/messenger/MessageObject:forceUpdate	Z
    //   222: aload_0
    //   223: iconst_0
    //   224: putfield 740	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   227: aload_0
    //   228: iconst_0
    //   229: putfield 482	org/telegram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   232: aload_0
    //   233: iconst_0
    //   234: putfield 647	org/telegram/ui/Cells/ChatMessageCell:linkPreviewPressed	Z
    //   237: aload_0
    //   238: iconst_0
    //   239: putfield 562	org/telegram/ui/Cells/ChatMessageCell:buttonPressed	I
    //   242: aload_0
    //   243: iconst_m1
    //   244: putfield 610	org/telegram/ui/Cells/ChatMessageCell:pressedBotButton	I
    //   247: aload_0
    //   248: iconst_0
    //   249: putfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   252: aload_0
    //   253: iconst_0
    //   254: putfield 558	org/telegram/ui/Cells/ChatMessageCell:mediaOffsetY	I
    //   257: aload_0
    //   258: iconst_0
    //   259: putfield 504	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   262: aload_0
    //   263: aconst_null
    //   264: putfield 1043	org/telegram/ui/Cells/ChatMessageCell:documentAttach	Lorg/telegram/tgnet/TLRPC$Document;
    //   267: aload_0
    //   268: aconst_null
    //   269: putfield 782	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   272: aload_0
    //   273: aconst_null
    //   274: putfield 1404	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   277: aload_0
    //   278: aconst_null
    //   279: putfield 1434	org/telegram/ui/Cells/ChatMessageCell:videoInfoLayout	Landroid/text/StaticLayout;
    //   282: aload_0
    //   283: aconst_null
    //   284: putfield 1386	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   287: aload_0
    //   288: aconst_null
    //   289: putfield 1408	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   292: aload_0
    //   293: aconst_null
    //   294: putfield 645	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   297: aload_0
    //   298: aconst_null
    //   299: putfield 1235	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   302: aload_0
    //   303: iconst_0
    //   304: putfield 746	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   307: aload_0
    //   308: aconst_null
    //   309: putfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   312: aload_0
    //   313: aconst_null
    //   314: putfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   317: aload_0
    //   318: aconst_null
    //   319: putfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   322: aload_0
    //   323: aconst_null
    //   324: putfield 1185	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   327: aload_0
    //   328: iconst_0
    //   329: putfield 1287	org/telegram/ui/Cells/ChatMessageCell:cancelLoading	Z
    //   332: aload_0
    //   333: iconst_m1
    //   334: putfield 552	org/telegram/ui/Cells/ChatMessageCell:buttonState	I
    //   337: aload_0
    //   338: aconst_null
    //   339: putfield 1762	org/telegram/ui/Cells/ChatMessageCell:currentUrl	Ljava/lang/String;
    //   342: aload_0
    //   343: iconst_0
    //   344: putfield 1800	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   347: aload_0
    //   348: iconst_1
    //   349: putfield 282	org/telegram/ui/Cells/ChatMessageCell:drawBackground	Z
    //   352: aload_0
    //   353: iconst_0
    //   354: putfield 1859	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   357: aload_0
    //   358: iconst_0
    //   359: putfield 515	org/telegram/ui/Cells/ChatMessageCell:useSeekBarWaweform	Z
    //   362: aload_0
    //   363: iconst_0
    //   364: putfield 1869	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   367: aload_0
    //   368: iconst_0
    //   369: putfield 623	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   372: aload_0
    //   373: iconst_0
    //   374: putfield 1069	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   377: aload_0
    //   378: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   381: iconst_0
    //   382: invokevirtual 1258	org/telegram/messenger/ImageReceiver:setNeedsQualityThumb	(Z)V
    //   385: aload_0
    //   386: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   389: iconst_0
    //   390: invokevirtual 1261	org/telegram/messenger/ImageReceiver:setShouldGenerateQualityThumb	(Z)V
    //   393: aload_0
    //   394: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   397: aconst_null
    //   398: invokevirtual 1264	org/telegram/messenger/ImageReceiver:setParentMessageObject	(Lorg/telegram/messenger/MessageObject;)V
    //   401: aload_0
    //   402: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   405: ldc_w 1378
    //   408: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   411: invokevirtual 428	org/telegram/messenger/ImageReceiver:setRoundRadius	(I)V
    //   414: iload 8
    //   416: ifeq +18 -> 434
    //   419: aload_0
    //   420: iconst_0
    //   421: putfield 1370	org/telegram/ui/Cells/ChatMessageCell:firstVisibleBlockNum	I
    //   424: aload_0
    //   425: iconst_0
    //   426: putfield 1372	org/telegram/ui/Cells/ChatMessageCell:lastVisibleBlockNum	I
    //   429: aload_0
    //   430: iconst_1
    //   431: putfield 1329	org/telegram/ui/Cells/ChatMessageCell:needNewVisiblePart	Z
    //   434: aload_1
    //   435: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   438: ifne +6374 -> 6812
    //   441: aload_0
    //   442: iconst_1
    //   443: putfield 1869	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   446: invokestatic 1732	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   449: ifeq +1325 -> 1774
    //   452: aload_0
    //   453: getfield 1734	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   456: ifeq +1268 -> 1724
    //   459: aload_1
    //   460: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   463: ifne +1261 -> 1724
    //   466: aload_1
    //   467: invokevirtual 882	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   470: ifeq +1254 -> 1724
    //   473: invokestatic 1737	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   476: ldc_w 2522
    //   479: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   482: isub
    //   483: istore 9
    //   485: aload_0
    //   486: iconst_1
    //   487: putfield 1859	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   490: aload_0
    //   491: aload_1
    //   492: invokespecial 1073	org/telegram/ui/Cells/ChatMessageCell:measureTime	(Lorg/telegram/messenger/MessageObject;)V
    //   495: aload_0
    //   496: getfield 500	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   499: ldc_w 1528
    //   502: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   505: iadd
    //   506: istore 8
    //   508: iload 8
    //   510: istore 19
    //   512: aload_1
    //   513: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   516: ifeq +14 -> 530
    //   519: iload 8
    //   521: ldc_w 2271
    //   524: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   527: iadd
    //   528: istore 19
    //   530: aload_1
    //   531: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   534: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   537: instanceof 2524
    //   540: ifeq +1349 -> 1889
    //   543: aload_1
    //   544: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   547: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   550: getfield 763	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   553: instanceof 1812
    //   556: ifeq +1333 -> 1889
    //   559: iconst_1
    //   560: istore 27
    //   562: aload_0
    //   563: iload 27
    //   565: putfield 482	org/telegram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   568: aload_0
    //   569: iload 9
    //   571: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   574: aload_0
    //   575: getfield 482	org/telegram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   578: ifne +15 -> 593
    //   581: iload 9
    //   583: aload_1
    //   584: getfield 489	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   587: isub
    //   588: iload 19
    //   590: if_icmpge +1305 -> 1895
    //   593: aload_0
    //   594: aload_0
    //   595: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   598: aload_1
    //   599: getfield 489	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   602: invokestatic 497	java/lang/Math:max	(II)I
    //   605: ldc_w 498
    //   608: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   611: iadd
    //   612: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   615: aload_0
    //   616: aload_0
    //   617: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   620: aload_0
    //   621: getfield 500	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   624: ldc_w 498
    //   627: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   630: iadd
    //   631: invokestatic 497	java/lang/Math:max	(II)I
    //   634: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   637: aload_0
    //   638: aload_0
    //   639: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   642: ldc_w 498
    //   645: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   648: isub
    //   649: putfield 1069	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   652: aload_0
    //   653: aload_1
    //   654: invokespecial 2526	org/telegram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/telegram/messenger/MessageObject;)V
    //   657: aload_0
    //   658: aload_1
    //   659: getfield 1008	org/telegram/messenger/MessageObject:textWidth	I
    //   662: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   665: aload_0
    //   666: aload_1
    //   667: getfield 735	org/telegram/messenger/MessageObject:textHeight	I
    //   670: ldc_w 2527
    //   673: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   676: iadd
    //   677: aload_0
    //   678: getfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   681: iadd
    //   682: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   685: aload_0
    //   686: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   689: aload_0
    //   690: getfield 1994	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   693: invokestatic 497	java/lang/Math:max	(II)I
    //   696: aload_0
    //   697: getfield 2025	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   700: invokestatic 497	java/lang/Math:max	(II)I
    //   703: aload_0
    //   704: getfield 2069	org/telegram/ui/Cells/ChatMessageCell:replyNameWidth	I
    //   707: invokestatic 497	java/lang/Math:max	(II)I
    //   710: aload_0
    //   711: getfield 2073	org/telegram/ui/Cells/ChatMessageCell:replyTextWidth	I
    //   714: invokestatic 497	java/lang/Math:max	(II)I
    //   717: istore 12
    //   719: iconst_0
    //   720: istore 13
    //   722: aload_0
    //   723: getfield 482	org/telegram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   726: ifeq +6062 -> 6788
    //   729: invokestatic 1732	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   732: ifeq +1253 -> 1985
    //   735: aload_1
    //   736: invokevirtual 882	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   739: ifeq +1231 -> 1970
    //   742: aload_0
    //   743: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   746: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   749: getfield 920	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   752: getfield 923	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   755: ifne +19 -> 774
    //   758: aload_0
    //   759: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   762: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   765: getfield 920	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   768: getfield 2530	org/telegram/tgnet/TLRPC$Peer:chat_id	I
    //   771: ifeq +1199 -> 1970
    //   774: aload_0
    //   775: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   778: invokevirtual 879	org/telegram/messenger/MessageObject:isOut	()Z
    //   781: ifne +1189 -> 1970
    //   784: invokestatic 1737	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   787: ldc_w 2522
    //   790: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   793: isub
    //   794: istore 8
    //   796: aload_0
    //   797: getfield 2160	org/telegram/ui/Cells/ChatMessageCell:drawShareButton	Z
    //   800: ifne +14 -> 814
    //   803: iload 8
    //   805: istore 10
    //   807: aload_0
    //   808: getfield 2218	org/telegram/ui/Cells/ChatMessageCell:drawDirectReply	Z
    //   811: ifeq +14 -> 825
    //   814: iload 8
    //   816: ldc_w 960
    //   819: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   822: isub
    //   823: istore 10
    //   825: aload_1
    //   826: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   829: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   832: getfield 763	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   835: checkcast 1812	org/telegram/tgnet/TLRPC$TL_webPage
    //   838: astore 29
    //   840: iload 10
    //   842: istore 8
    //   844: aload 29
    //   846: getfield 2531	org/telegram/tgnet/TLRPC$TL_webPage:site_name	Ljava/lang/String;
    //   849: ifnull +56 -> 905
    //   852: iload 10
    //   854: istore 8
    //   856: aload 29
    //   858: getfield 2534	org/telegram/tgnet/TLRPC$TL_webPage:photo	Lorg/telegram/tgnet/TLRPC$Photo;
    //   861: ifnull +44 -> 905
    //   864: iload 10
    //   866: istore 8
    //   868: aload 29
    //   870: getfield 2531	org/telegram/tgnet/TLRPC$TL_webPage:site_name	Ljava/lang/String;
    //   873: invokevirtual 1191	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   876: ldc_w 2536
    //   879: invokevirtual 1798	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   882: ifeq +23 -> 905
    //   885: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   888: getfield 1750	android/graphics/Point:y	I
    //   891: iconst_3
    //   892: idiv
    //   893: aload_0
    //   894: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   897: getfield 1008	org/telegram/messenger/MessageObject:textWidth	I
    //   900: invokestatic 497	java/lang/Math:max	(II)I
    //   903: istore 8
    //   905: ldc_w 588
    //   908: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   911: istore 24
    //   913: iconst_3
    //   914: istore 14
    //   916: iconst_0
    //   917: istore 10
    //   919: iconst_0
    //   920: istore 15
    //   922: iload 8
    //   924: iload 24
    //   926: isub
    //   927: istore 23
    //   929: aload_0
    //   930: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   933: getfield 1246	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   936: ifnonnull +19 -> 955
    //   939: aload 29
    //   941: getfield 2534	org/telegram/tgnet/TLRPC$TL_webPage:photo	Lorg/telegram/tgnet/TLRPC$Photo;
    //   944: ifnull +11 -> 955
    //   947: aload_0
    //   948: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   951: iconst_1
    //   952: invokevirtual 2539	org/telegram/messenger/MessageObject:generateThumbs	(Z)V
    //   955: aload 29
    //   957: getfield 2540	org/telegram/tgnet/TLRPC$TL_webPage:description	Ljava/lang/String;
    //   960: ifnull +1128 -> 2088
    //   963: aload 29
    //   965: getfield 2542	org/telegram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   968: ifnull +1120 -> 2088
    //   971: aload 29
    //   973: getfield 2542	org/telegram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   976: ldc_w 2544
    //   979: invokevirtual 1798	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   982: ifne +31 -> 1013
    //   985: aload 29
    //   987: getfield 2542	org/telegram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   990: ldc_w 2546
    //   993: invokevirtual 1798	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   996: ifne +17 -> 1013
    //   999: aload 29
    //   1001: getfield 2542	org/telegram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   1004: ldc_w 2548
    //   1007: invokevirtual 1798	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1010: ifeq +1078 -> 2088
    //   1013: aload_0
    //   1014: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1017: getfield 1246	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   1020: ifnull +1068 -> 2088
    //   1023: iconst_1
    //   1024: istore 27
    //   1026: aload_0
    //   1027: iload 27
    //   1029: putfield 1421	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   1032: iload 12
    //   1034: istore 8
    //   1036: iload 13
    //   1038: istore 11
    //   1040: aload 29
    //   1042: getfield 2531	org/telegram/tgnet/TLRPC$TL_webPage:site_name	Ljava/lang/String;
    //   1045: ifnull +203 -> 1248
    //   1048: iload 10
    //   1050: istore 15
    //   1052: iload 12
    //   1054: istore 8
    //   1056: getstatic 396	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   1059: aload 29
    //   1061: getfield 2531	org/telegram/tgnet/TLRPC$TL_webPage:site_name	Ljava/lang/String;
    //   1064: invokevirtual 1067	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   1067: f2d
    //   1068: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   1071: d2i
    //   1072: istore 11
    //   1074: iload 10
    //   1076: istore 15
    //   1078: iload 12
    //   1080: istore 8
    //   1082: aload_0
    //   1083: new 286	android/text/StaticLayout
    //   1086: dup
    //   1087: aload 29
    //   1089: getfield 2531	org/telegram/tgnet/TLRPC$TL_webPage:site_name	Ljava/lang/String;
    //   1092: getstatic 396	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   1095: iload 11
    //   1097: iload 23
    //   1099: invokestatic 1077	java/lang/Math:min	(II)I
    //   1102: getstatic 1131	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1105: fconst_1
    //   1106: fconst_0
    //   1107: iconst_0
    //   1108: invokespecial 1134	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1111: putfield 1386	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   1114: iload 10
    //   1116: istore 15
    //   1118: iload 12
    //   1120: istore 8
    //   1122: aload_0
    //   1123: getfield 1386	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   1126: aload_0
    //   1127: getfield 1386	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   1130: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   1133: iconst_1
    //   1134: isub
    //   1135: invokevirtual 1402	android/text/StaticLayout:getLineBottom	(I)I
    //   1138: istore 11
    //   1140: iload 10
    //   1142: istore 15
    //   1144: iload 12
    //   1146: istore 8
    //   1148: aload_0
    //   1149: aload_0
    //   1150: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1153: iload 11
    //   1155: iadd
    //   1156: putfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1159: iload 10
    //   1161: istore 15
    //   1163: iload 12
    //   1165: istore 8
    //   1167: aload_0
    //   1168: aload_0
    //   1169: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1172: iload 11
    //   1174: iadd
    //   1175: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1178: iconst_0
    //   1179: iload 11
    //   1181: iadd
    //   1182: istore 10
    //   1184: iload 10
    //   1186: istore 15
    //   1188: iload 12
    //   1190: istore 8
    //   1192: aload_0
    //   1193: getfield 1386	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   1196: invokevirtual 2551	android/text/StaticLayout:getWidth	()I
    //   1199: istore 11
    //   1201: iload 10
    //   1203: istore 15
    //   1205: iload 12
    //   1207: istore 8
    //   1209: iload 12
    //   1211: iload 11
    //   1213: iload 24
    //   1215: iadd
    //   1216: invokestatic 497	java/lang/Math:max	(II)I
    //   1219: istore 12
    //   1221: iload 10
    //   1223: istore 15
    //   1225: iload 12
    //   1227: istore 8
    //   1229: iconst_0
    //   1230: iload 11
    //   1232: iload 24
    //   1234: iadd
    //   1235: invokestatic 497	java/lang/Math:max	(II)I
    //   1238: istore 11
    //   1240: iload 12
    //   1242: istore 8
    //   1244: iload 10
    //   1246: istore 15
    //   1248: iconst_0
    //   1249: istore 21
    //   1251: iconst_0
    //   1252: istore 16
    //   1254: iconst_0
    //   1255: istore 20
    //   1257: iconst_0
    //   1258: istore 17
    //   1260: iload 8
    //   1262: istore 10
    //   1264: iload 11
    //   1266: istore 13
    //   1268: iload 14
    //   1270: istore 12
    //   1272: iload 20
    //   1274: istore 14
    //   1276: aload 29
    //   1278: getfield 2552	org/telegram/tgnet/TLRPC$TL_webPage:title	Ljava/lang/String;
    //   1281: ifnull +942 -> 2223
    //   1284: aload_0
    //   1285: ldc_w 2553
    //   1288: putfield 1406	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   1291: aload_0
    //   1292: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1295: ifeq +29 -> 1324
    //   1298: aload_0
    //   1299: aload_0
    //   1300: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1303: fconst_2
    //   1304: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1307: iadd
    //   1308: putfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1311: aload_0
    //   1312: aload_0
    //   1313: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1316: fconst_2
    //   1317: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1320: iadd
    //   1321: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1324: iconst_0
    //   1325: istore 20
    //   1327: aload_0
    //   1328: getfield 1421	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   1331: ifeq +11 -> 1342
    //   1334: aload 29
    //   1336: getfield 2540	org/telegram/tgnet/TLRPC$TL_webPage:description	Ljava/lang/String;
    //   1339: ifnonnull +772 -> 2111
    //   1342: aload_0
    //   1343: aload 29
    //   1345: getfield 2552	org/telegram/tgnet/TLRPC$TL_webPage:title	Ljava/lang/String;
    //   1348: getstatic 396	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   1351: iload 23
    //   1353: getstatic 1131	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1356: fconst_1
    //   1357: fconst_1
    //   1358: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1361: i2f
    //   1362: iconst_0
    //   1363: getstatic 1121	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1366: iload 23
    //   1368: iconst_4
    //   1369: invokestatic 1233	org/telegram/ui/Components/StaticLayoutEx:createStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZLandroid/text/TextUtils$TruncateAt;II)Landroid/text/StaticLayout;
    //   1372: putfield 1404	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   1375: iconst_3
    //   1376: istore 10
    //   1378: iload 8
    //   1380: istore 12
    //   1382: iload 11
    //   1384: istore 16
    //   1386: iload 21
    //   1388: istore 13
    //   1390: aload_0
    //   1391: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   1394: aload_0
    //   1395: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   1398: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   1401: iconst_1
    //   1402: isub
    //   1403: invokevirtual 1402	android/text/StaticLayout:getLineBottom	(I)I
    //   1406: istore 14
    //   1408: iload 8
    //   1410: istore 12
    //   1412: iload 11
    //   1414: istore 16
    //   1416: iload 21
    //   1418: istore 13
    //   1420: aload_0
    //   1421: aload_0
    //   1422: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1425: iload 14
    //   1427: iadd
    //   1428: putfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1431: iload 8
    //   1433: istore 12
    //   1435: iload 11
    //   1437: istore 16
    //   1439: iload 21
    //   1441: istore 13
    //   1443: aload_0
    //   1444: aload_0
    //   1445: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1448: iload 14
    //   1450: iadd
    //   1451: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1454: iconst_0
    //   1455: istore 21
    //   1457: iload 17
    //   1459: istore 14
    //   1461: iload 8
    //   1463: istore 12
    //   1465: iload 11
    //   1467: istore 16
    //   1469: iload 14
    //   1471: istore 13
    //   1473: iload 21
    //   1475: aload_0
    //   1476: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   1479: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   1482: if_icmpge +1328 -> 2810
    //   1485: iload 8
    //   1487: istore 12
    //   1489: iload 11
    //   1491: istore 16
    //   1493: iload 14
    //   1495: istore 13
    //   1497: aload_0
    //   1498: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   1501: iload 21
    //   1503: invokevirtual 667	android/text/StaticLayout:getLineLeft	(I)F
    //   1506: f2i
    //   1507: istore 25
    //   1509: iload 25
    //   1511: ifeq +6 -> 1517
    //   1514: iconst_1
    //   1515: istore 14
    //   1517: iload 8
    //   1519: istore 12
    //   1521: iload 11
    //   1523: istore 16
    //   1525: iload 14
    //   1527: istore 13
    //   1529: aload_0
    //   1530: getfield 1406	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   1533: ldc_w 2553
    //   1536: if_icmpne +624 -> 2160
    //   1539: iload 8
    //   1541: istore 12
    //   1543: iload 11
    //   1545: istore 16
    //   1547: iload 14
    //   1549: istore 13
    //   1551: aload_0
    //   1552: iload 25
    //   1554: ineg
    //   1555: putfield 1406	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   1558: iload 25
    //   1560: ifeq +1217 -> 2777
    //   1563: iload 8
    //   1565: istore 12
    //   1567: iload 11
    //   1569: istore 16
    //   1571: iload 14
    //   1573: istore 13
    //   1575: aload_0
    //   1576: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   1579: invokevirtual 2551	android/text/StaticLayout:getWidth	()I
    //   1582: iload 25
    //   1584: isub
    //   1585: istore 17
    //   1587: iload 21
    //   1589: iload 20
    //   1591: if_icmplt +35 -> 1626
    //   1594: iload 17
    //   1596: istore 22
    //   1598: iload 25
    //   1600: ifeq +49 -> 1649
    //   1603: iload 17
    //   1605: istore 22
    //   1607: iload 8
    //   1609: istore 12
    //   1611: iload 11
    //   1613: istore 16
    //   1615: iload 14
    //   1617: istore 13
    //   1619: aload_0
    //   1620: getfield 1421	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   1623: ifeq +26 -> 1649
    //   1626: iload 8
    //   1628: istore 12
    //   1630: iload 11
    //   1632: istore 16
    //   1634: iload 14
    //   1636: istore 13
    //   1638: iload 17
    //   1640: ldc_w 2554
    //   1643: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1646: iadd
    //   1647: istore 22
    //   1649: iload 8
    //   1651: istore 12
    //   1653: iload 11
    //   1655: istore 16
    //   1657: iload 14
    //   1659: istore 13
    //   1661: iload 8
    //   1663: iload 22
    //   1665: iload 24
    //   1667: iadd
    //   1668: invokestatic 497	java/lang/Math:max	(II)I
    //   1671: istore 8
    //   1673: iload 8
    //   1675: istore 12
    //   1677: iload 11
    //   1679: istore 16
    //   1681: iload 14
    //   1683: istore 13
    //   1685: iload 11
    //   1687: iload 22
    //   1689: iload 24
    //   1691: iadd
    //   1692: invokestatic 497	java/lang/Math:max	(II)I
    //   1695: istore 11
    //   1697: iload 21
    //   1699: iconst_1
    //   1700: iadd
    //   1701: istore 21
    //   1703: goto -242 -> 1461
    //   1706: iconst_0
    //   1707: istore 18
    //   1709: goto -1685 -> 24
    //   1712: iconst_0
    //   1713: istore 8
    //   1715: goto -1673 -> 42
    //   1718: iconst_0
    //   1719: istore 26
    //   1721: goto -1654 -> 67
    //   1724: aload_1
    //   1725: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1728: getfield 920	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   1731: getfield 923	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   1734: ifeq +34 -> 1768
    //   1737: aload_1
    //   1738: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   1741: ifne +27 -> 1768
    //   1744: iconst_1
    //   1745: istore 27
    //   1747: aload_0
    //   1748: iload 27
    //   1750: putfield 1859	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   1753: invokestatic 1737	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   1756: ldc_w 2555
    //   1759: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1762: isub
    //   1763: istore 9
    //   1765: goto -1275 -> 490
    //   1768: iconst_0
    //   1769: istore 27
    //   1771: goto -24 -> 1747
    //   1774: aload_0
    //   1775: getfield 1734	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   1778: ifeq +49 -> 1827
    //   1781: aload_1
    //   1782: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   1785: ifne +42 -> 1827
    //   1788: aload_1
    //   1789: invokevirtual 882	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   1792: ifeq +35 -> 1827
    //   1795: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1798: getfield 1747	android/graphics/Point:x	I
    //   1801: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1804: getfield 1750	android/graphics/Point:y	I
    //   1807: invokestatic 1077	java/lang/Math:min	(II)I
    //   1810: ldc_w 2522
    //   1813: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1816: isub
    //   1817: istore 9
    //   1819: aload_0
    //   1820: iconst_1
    //   1821: putfield 1859	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   1824: goto -1334 -> 490
    //   1827: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1830: getfield 1747	android/graphics/Point:x	I
    //   1833: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1836: getfield 1750	android/graphics/Point:y	I
    //   1839: invokestatic 1077	java/lang/Math:min	(II)I
    //   1842: ldc_w 2555
    //   1845: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1848: isub
    //   1849: istore 9
    //   1851: aload_1
    //   1852: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1855: getfield 920	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   1858: getfield 923	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   1861: ifeq +22 -> 1883
    //   1864: aload_1
    //   1865: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   1868: ifne +15 -> 1883
    //   1871: iconst_1
    //   1872: istore 27
    //   1874: aload_0
    //   1875: iload 27
    //   1877: putfield 1859	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   1880: goto -1390 -> 490
    //   1883: iconst_0
    //   1884: istore 27
    //   1886: goto -12 -> 1874
    //   1889: iconst_0
    //   1890: istore 27
    //   1892: goto -1330 -> 562
    //   1895: aload_0
    //   1896: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   1899: aload_1
    //   1900: getfield 489	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   1903: isub
    //   1904: istore 8
    //   1906: iload 8
    //   1908: iflt +34 -> 1942
    //   1911: iload 8
    //   1913: iload 19
    //   1915: if_icmpgt +27 -> 1942
    //   1918: aload_0
    //   1919: aload_0
    //   1920: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   1923: iload 19
    //   1925: iadd
    //   1926: iload 8
    //   1928: isub
    //   1929: ldc_w 498
    //   1932: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1935: iadd
    //   1936: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   1939: goto -1302 -> 637
    //   1942: aload_0
    //   1943: aload_0
    //   1944: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   1947: aload_1
    //   1948: getfield 489	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   1951: iload 19
    //   1953: iadd
    //   1954: invokestatic 497	java/lang/Math:max	(II)I
    //   1957: ldc_w 498
    //   1960: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1963: iadd
    //   1964: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   1967: goto -1330 -> 637
    //   1970: invokestatic 1737	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   1973: ldc_w 2555
    //   1976: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1979: isub
    //   1980: istore 8
    //   1982: goto -1186 -> 796
    //   1985: aload_1
    //   1986: invokevirtual 882	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   1989: ifeq +72 -> 2061
    //   1992: aload_0
    //   1993: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1996: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1999: getfield 920	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   2002: getfield 923	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   2005: ifne +19 -> 2024
    //   2008: aload_0
    //   2009: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2012: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2015: getfield 920	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   2018: getfield 2530	org/telegram/tgnet/TLRPC$Peer:chat_id	I
    //   2021: ifeq +40 -> 2061
    //   2024: aload_0
    //   2025: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2028: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   2031: ifne +30 -> 2061
    //   2034: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   2037: getfield 1747	android/graphics/Point:x	I
    //   2040: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   2043: getfield 1750	android/graphics/Point:y	I
    //   2046: invokestatic 1077	java/lang/Math:min	(II)I
    //   2049: ldc_w 2522
    //   2052: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2055: isub
    //   2056: istore 8
    //   2058: goto -1262 -> 796
    //   2061: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   2064: getfield 1747	android/graphics/Point:x	I
    //   2067: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   2070: getfield 1750	android/graphics/Point:y	I
    //   2073: invokestatic 1077	java/lang/Math:min	(II)I
    //   2076: ldc_w 2555
    //   2079: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2082: isub
    //   2083: istore 8
    //   2085: goto -1289 -> 796
    //   2088: iconst_0
    //   2089: istore 27
    //   2091: goto -1065 -> 1026
    //   2094: astore 28
    //   2096: ldc_w 711
    //   2099: aload 28
    //   2101: invokestatic 717	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2104: iload 13
    //   2106: istore 11
    //   2108: goto -860 -> 1248
    //   2111: iconst_3
    //   2112: istore 20
    //   2114: aload_0
    //   2115: aload 29
    //   2117: getfield 2552	org/telegram/tgnet/TLRPC$TL_webPage:title	Ljava/lang/String;
    //   2120: getstatic 396	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   2123: iload 23
    //   2125: iload 23
    //   2127: ldc_w 2554
    //   2130: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2133: isub
    //   2134: iconst_3
    //   2135: iconst_4
    //   2136: invokestatic 2557	org/telegram/ui/Cells/ChatMessageCell:generateStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;IIII)Landroid/text/StaticLayout;
    //   2139: putfield 1404	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   2142: aload_0
    //   2143: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   2146: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   2149: istore 10
    //   2151: iconst_3
    //   2152: iload 10
    //   2154: isub
    //   2155: istore 10
    //   2157: goto -779 -> 1378
    //   2160: iload 8
    //   2162: istore 12
    //   2164: iload 11
    //   2166: istore 16
    //   2168: iload 14
    //   2170: istore 13
    //   2172: aload_0
    //   2173: aload_0
    //   2174: getfield 1406	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   2177: iload 25
    //   2179: ineg
    //   2180: invokestatic 497	java/lang/Math:max	(II)I
    //   2183: putfield 1406	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   2186: goto -628 -> 1558
    //   2189: astore 28
    //   2191: iload 16
    //   2193: istore 11
    //   2195: iload 12
    //   2197: istore 8
    //   2199: ldc_w 711
    //   2202: aload 28
    //   2204: invokestatic 717	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2207: iload 13
    //   2209: istore 14
    //   2211: iload 10
    //   2213: istore 12
    //   2215: iload 11
    //   2217: istore 13
    //   2219: iload 8
    //   2221: istore 10
    //   2223: iconst_0
    //   2224: istore 8
    //   2226: iconst_0
    //   2227: istore 16
    //   2229: iconst_0
    //   2230: istore 17
    //   2232: iconst_0
    //   2233: istore 11
    //   2235: aload 29
    //   2237: getfield 2560	org/telegram/tgnet/TLRPC$TL_webPage:author	Ljava/lang/String;
    //   2240: ifnull +9841 -> 12081
    //   2243: aload_0
    //   2244: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2247: ifeq +29 -> 2276
    //   2250: aload_0
    //   2251: aload_0
    //   2252: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2255: fconst_2
    //   2256: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2259: iadd
    //   2260: putfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2263: aload_0
    //   2264: aload_0
    //   2265: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2268: fconst_2
    //   2269: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2272: iadd
    //   2273: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2276: iload 12
    //   2278: iconst_3
    //   2279: if_icmpne +546 -> 2825
    //   2282: aload_0
    //   2283: getfield 1421	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   2286: ifeq +11 -> 2297
    //   2289: aload 29
    //   2291: getfield 2540	org/telegram/tgnet/TLRPC$TL_webPage:description	Ljava/lang/String;
    //   2294: ifnonnull +531 -> 2825
    //   2297: aload_0
    //   2298: new 286	android/text/StaticLayout
    //   2301: dup
    //   2302: aload 29
    //   2304: getfield 2560	org/telegram/tgnet/TLRPC$TL_webPage:author	Ljava/lang/String;
    //   2307: getstatic 396	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   2310: iload 23
    //   2312: getstatic 1131	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   2315: fconst_1
    //   2316: fconst_0
    //   2317: iconst_0
    //   2318: invokespecial 1134	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   2321: putfield 1408	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   2324: iload 12
    //   2326: istore 8
    //   2328: iload 17
    //   2330: istore 16
    //   2332: iload 10
    //   2334: istore 12
    //   2336: aload_0
    //   2337: getfield 1408	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   2340: aload_0
    //   2341: getfield 1408	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   2344: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   2347: iconst_1
    //   2348: isub
    //   2349: invokevirtual 1402	android/text/StaticLayout:getLineBottom	(I)I
    //   2352: istore 20
    //   2354: iload 17
    //   2356: istore 16
    //   2358: iload 10
    //   2360: istore 12
    //   2362: aload_0
    //   2363: aload_0
    //   2364: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2367: iload 20
    //   2369: iadd
    //   2370: putfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2373: iload 17
    //   2375: istore 16
    //   2377: iload 10
    //   2379: istore 12
    //   2381: aload_0
    //   2382: aload_0
    //   2383: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2386: iload 20
    //   2388: iadd
    //   2389: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2392: iload 17
    //   2394: istore 16
    //   2396: iload 10
    //   2398: istore 12
    //   2400: aload_0
    //   2401: getfield 1408	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   2404: iconst_0
    //   2405: invokevirtual 667	android/text/StaticLayout:getLineLeft	(I)F
    //   2408: f2i
    //   2409: istore 20
    //   2411: iload 17
    //   2413: istore 16
    //   2415: iload 10
    //   2417: istore 12
    //   2419: aload_0
    //   2420: iload 20
    //   2422: ineg
    //   2423: putfield 1410	org/telegram/ui/Cells/ChatMessageCell:authorX	I
    //   2426: iload 20
    //   2428: ifeq +445 -> 2873
    //   2431: iload 17
    //   2433: istore 16
    //   2435: iload 10
    //   2437: istore 12
    //   2439: aload_0
    //   2440: getfield 1408	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   2443: invokevirtual 2551	android/text/StaticLayout:getWidth	()I
    //   2446: iload 20
    //   2448: isub
    //   2449: istore 17
    //   2451: iconst_1
    //   2452: istore 11
    //   2454: iload 11
    //   2456: istore 16
    //   2458: iload 10
    //   2460: istore 12
    //   2462: iload 10
    //   2464: iload 17
    //   2466: iload 24
    //   2468: iadd
    //   2469: invokestatic 497	java/lang/Math:max	(II)I
    //   2472: istore 10
    //   2474: iload 11
    //   2476: istore 16
    //   2478: iload 10
    //   2480: istore 12
    //   2482: iload 13
    //   2484: iload 17
    //   2486: iload 24
    //   2488: iadd
    //   2489: invokestatic 497	java/lang/Math:max	(II)I
    //   2492: istore 17
    //   2494: iload 17
    //   2496: istore 16
    //   2498: iload 11
    //   2500: istore 12
    //   2502: iload 8
    //   2504: istore 11
    //   2506: iload 10
    //   2508: istore 8
    //   2510: aload 29
    //   2512: getfield 2540	org/telegram/tgnet/TLRPC$TL_webPage:description	Ljava/lang/String;
    //   2515: ifnull +473 -> 2988
    //   2518: iload 10
    //   2520: istore 13
    //   2522: aload_0
    //   2523: iconst_0
    //   2524: putfield 786	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   2527: iload 10
    //   2529: istore 13
    //   2531: aload_0
    //   2532: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2535: invokevirtual 2563	org/telegram/messenger/MessageObject:generateLinkDescription	()V
    //   2538: iload 10
    //   2540: istore 13
    //   2542: aload_0
    //   2543: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2546: ifeq +37 -> 2583
    //   2549: iload 10
    //   2551: istore 13
    //   2553: aload_0
    //   2554: aload_0
    //   2555: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2558: fconst_2
    //   2559: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2562: iadd
    //   2563: putfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2566: iload 10
    //   2568: istore 13
    //   2570: aload_0
    //   2571: aload_0
    //   2572: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2575: fconst_2
    //   2576: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2579: iadd
    //   2580: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2583: iconst_0
    //   2584: istore 8
    //   2586: iload 11
    //   2588: iconst_3
    //   2589: if_icmpne +341 -> 2930
    //   2592: iload 10
    //   2594: istore 13
    //   2596: aload_0
    //   2597: getfield 1421	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   2600: ifne +330 -> 2930
    //   2603: iload 10
    //   2605: istore 13
    //   2607: aload_0
    //   2608: aload_1
    //   2609: getfield 789	org/telegram/messenger/MessageObject:linkDescription	Ljava/lang/CharSequence;
    //   2612: getstatic 398	org/telegram/ui/Cells/ChatMessageCell:replyTextPaint	Landroid/text/TextPaint;
    //   2615: iload 23
    //   2617: getstatic 1131	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   2620: fconst_1
    //   2621: fconst_1
    //   2622: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2625: i2f
    //   2626: iconst_0
    //   2627: getstatic 1121	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   2630: iload 23
    //   2632: bipush 6
    //   2634: invokestatic 1233	org/telegram/ui/Components/StaticLayoutEx:createStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZLandroid/text/TextUtils$TruncateAt;II)Landroid/text/StaticLayout;
    //   2637: putfield 782	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   2640: iload 8
    //   2642: istore 11
    //   2644: iload 10
    //   2646: istore 13
    //   2648: aload_0
    //   2649: getfield 782	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   2652: aload_0
    //   2653: getfield 782	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   2656: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   2659: iconst_1
    //   2660: isub
    //   2661: invokevirtual 1402	android/text/StaticLayout:getLineBottom	(I)I
    //   2664: istore 8
    //   2666: iload 10
    //   2668: istore 13
    //   2670: aload_0
    //   2671: aload_0
    //   2672: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2675: iload 8
    //   2677: iadd
    //   2678: putfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2681: iload 10
    //   2683: istore 13
    //   2685: aload_0
    //   2686: aload_0
    //   2687: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2690: iload 8
    //   2692: iadd
    //   2693: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2696: iconst_0
    //   2697: istore 17
    //   2699: iconst_0
    //   2700: istore 8
    //   2702: iload 10
    //   2704: istore 13
    //   2706: iload 8
    //   2708: aload_0
    //   2709: getfield 782	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   2712: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   2715: if_icmpge +9381 -> 12096
    //   2718: iload 10
    //   2720: istore 13
    //   2722: aload_0
    //   2723: getfield 782	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   2726: iload 8
    //   2728: invokevirtual 667	android/text/StaticLayout:getLineLeft	(I)F
    //   2731: f2d
    //   2732: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   2735: d2i
    //   2736: istore 20
    //   2738: iload 20
    //   2740: ifeq +28 -> 2768
    //   2743: iconst_1
    //   2744: istore 17
    //   2746: iload 10
    //   2748: istore 13
    //   2750: aload_0
    //   2751: getfield 786	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   2754: ifne +1999 -> 4753
    //   2757: iload 10
    //   2759: istore 13
    //   2761: aload_0
    //   2762: iload 20
    //   2764: ineg
    //   2765: putfield 786	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   2768: iload 8
    //   2770: iconst_1
    //   2771: iadd
    //   2772: istore 8
    //   2774: goto -72 -> 2702
    //   2777: iload 8
    //   2779: istore 12
    //   2781: iload 11
    //   2783: istore 16
    //   2785: iload 14
    //   2787: istore 13
    //   2789: aload_0
    //   2790: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   2793: iload 21
    //   2795: invokevirtual 670	android/text/StaticLayout:getLineWidth	(I)F
    //   2798: f2d
    //   2799: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   2802: dstore_2
    //   2803: dload_2
    //   2804: d2i
    //   2805: istore 17
    //   2807: goto -1220 -> 1587
    //   2810: iload 10
    //   2812: istore 12
    //   2814: iload 8
    //   2816: istore 10
    //   2818: iload 11
    //   2820: istore 13
    //   2822: goto -599 -> 2223
    //   2825: aload_0
    //   2826: aload 29
    //   2828: getfield 2560	org/telegram/tgnet/TLRPC$TL_webPage:author	Ljava/lang/String;
    //   2831: getstatic 396	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   2834: iload 23
    //   2836: iload 23
    //   2838: ldc_w 2554
    //   2841: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2844: isub
    //   2845: iload 12
    //   2847: iconst_1
    //   2848: invokestatic 2557	org/telegram/ui/Cells/ChatMessageCell:generateStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;IIII)Landroid/text/StaticLayout;
    //   2851: putfield 1408	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   2854: aload_0
    //   2855: getfield 1408	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   2858: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   2861: istore 8
    //   2863: iload 12
    //   2865: iload 8
    //   2867: isub
    //   2868: istore 8
    //   2870: goto -542 -> 2328
    //   2873: iload 17
    //   2875: istore 16
    //   2877: iload 10
    //   2879: istore 12
    //   2881: aload_0
    //   2882: getfield 1408	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   2885: iconst_0
    //   2886: invokevirtual 670	android/text/StaticLayout:getLineWidth	(I)F
    //   2889: f2d
    //   2890: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   2893: dstore_2
    //   2894: dload_2
    //   2895: d2i
    //   2896: istore 17
    //   2898: goto -444 -> 2454
    //   2901: astore 28
    //   2903: iload 12
    //   2905: istore 8
    //   2907: ldc_w 711
    //   2910: aload 28
    //   2912: invokestatic 717	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2915: iload 8
    //   2917: istore 11
    //   2919: iload 16
    //   2921: istore 12
    //   2923: iload 13
    //   2925: istore 16
    //   2927: goto -421 -> 2506
    //   2930: iload 11
    //   2932: istore 8
    //   2934: iload 10
    //   2936: istore 13
    //   2938: aload_0
    //   2939: aload_1
    //   2940: getfield 789	org/telegram/messenger/MessageObject:linkDescription	Ljava/lang/CharSequence;
    //   2943: getstatic 398	org/telegram/ui/Cells/ChatMessageCell:replyTextPaint	Landroid/text/TextPaint;
    //   2946: iload 23
    //   2948: iload 23
    //   2950: ldc_w 2554
    //   2953: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2956: isub
    //   2957: iload 11
    //   2959: bipush 6
    //   2961: invokestatic 2557	org/telegram/ui/Cells/ChatMessageCell:generateStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;IIII)Landroid/text/StaticLayout;
    //   2964: putfield 782	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   2967: iload 8
    //   2969: istore 11
    //   2971: goto -327 -> 2644
    //   2974: astore 28
    //   2976: ldc_w 711
    //   2979: aload 28
    //   2981: invokestatic 717	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2984: iload 13
    //   2986: istore 8
    //   2988: aload 29
    //   2990: getfield 2542	org/telegram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   2993: ifnull +2066 -> 5059
    //   2996: aload 29
    //   2998: getfield 2542	org/telegram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   3001: ldc_w 2544
    //   3004: invokevirtual 1798	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3007: ifne +31 -> 3038
    //   3010: aload 29
    //   3012: getfield 2542	org/telegram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   3015: ldc_w 2546
    //   3018: invokevirtual 1798	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3021: ifne +17 -> 3038
    //   3024: aload 29
    //   3026: getfield 2542	org/telegram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   3029: ldc_w 2548
    //   3032: invokevirtual 1798	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3035: ifeq +2024 -> 5059
    //   3038: iconst_1
    //   3039: istore 10
    //   3041: iload 10
    //   3043: istore 13
    //   3045: iload 10
    //   3047: ifeq +44 -> 3091
    //   3050: aload_0
    //   3051: getfield 782	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   3054: ifnull +29 -> 3083
    //   3057: iload 10
    //   3059: istore 13
    //   3061: aload_0
    //   3062: getfield 782	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   3065: ifnull +26 -> 3091
    //   3068: iload 10
    //   3070: istore 13
    //   3072: aload_0
    //   3073: getfield 782	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   3076: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   3079: iconst_1
    //   3080: if_icmpne +11 -> 3091
    //   3083: iconst_0
    //   3084: istore 13
    //   3086: aload_0
    //   3087: iconst_0
    //   3088: putfield 1421	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   3091: iload 13
    //   3093: ifeq +1972 -> 5065
    //   3096: ldc_w 747
    //   3099: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3102: istore 10
    //   3104: aload 29
    //   3106: getfield 2564	org/telegram/tgnet/TLRPC$TL_webPage:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   3109: ifnull +2942 -> 6051
    //   3112: aload 29
    //   3114: getfield 2564	org/telegram/tgnet/TLRPC$TL_webPage:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   3117: astore 28
    //   3119: aload 28
    //   3121: invokestatic 2567	org/telegram/messenger/MessageObject:isGifDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   3124: ifeq +1963 -> 5087
    //   3127: invokestatic 441	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   3130: invokevirtual 766	org/telegram/messenger/MediaController:canAutoplayGifs	()Z
    //   3133: ifne +8 -> 3141
    //   3136: aload_1
    //   3137: fconst_1
    //   3138: putfield 817	org/telegram/messenger/MessageObject:audioProgress	F
    //   3141: aload_0
    //   3142: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   3145: astore 30
    //   3147: aload_1
    //   3148: getfield 817	org/telegram/messenger/MessageObject:audioProgress	F
    //   3151: fconst_1
    //   3152: fcmpl
    //   3153: ifeq +1919 -> 5072
    //   3156: iconst_1
    //   3157: istore 27
    //   3159: aload 30
    //   3161: iload 27
    //   3163: invokevirtual 820	org/telegram/messenger/ImageReceiver:setAllowStartAnimation	(Z)V
    //   3166: aload_0
    //   3167: aload 28
    //   3169: getfield 1200	org/telegram/tgnet/TLRPC$Document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3172: putfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3175: aload_0
    //   3176: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3179: ifnull +148 -> 3327
    //   3182: aload_0
    //   3183: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3186: getfield 2570	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   3189: ifeq +13 -> 3202
    //   3192: aload_0
    //   3193: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3196: getfield 2573	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   3199: ifne +128 -> 3327
    //   3202: iconst_0
    //   3203: istore 11
    //   3205: iload 11
    //   3207: aload 28
    //   3209: getfield 1053	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3212: invokevirtual 591	java/util/ArrayList:size	()I
    //   3215: if_icmpge +58 -> 3273
    //   3218: aload 28
    //   3220: getfield 1053	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3223: iload 11
    //   3225: invokevirtual 595	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   3228: checkcast 1055	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   3231: astore 30
    //   3233: aload 30
    //   3235: instanceof 2575
    //   3238: ifne +11 -> 3249
    //   3241: aload 30
    //   3243: instanceof 1161
    //   3246: ifeq +1832 -> 5078
    //   3249: aload_0
    //   3250: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3253: aload 30
    //   3255: getfield 2576	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   3258: putfield 2570	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   3261: aload_0
    //   3262: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3265: aload 30
    //   3267: getfield 2577	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   3270: putfield 2573	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   3273: aload_0
    //   3274: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3277: getfield 2570	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   3280: ifeq +13 -> 3293
    //   3283: aload_0
    //   3284: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3287: getfield 2573	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   3290: ifne +37 -> 3327
    //   3293: aload_0
    //   3294: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3297: astore 28
    //   3299: aload_0
    //   3300: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3303: astore 30
    //   3305: ldc_w 2578
    //   3308: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3311: istore 11
    //   3313: aload 30
    //   3315: iload 11
    //   3317: putfield 2573	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   3320: aload 28
    //   3322: iload 11
    //   3324: putfield 2570	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   3327: aload_0
    //   3328: iconst_2
    //   3329: putfield 504	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3332: iload 8
    //   3334: istore 11
    //   3336: iload 9
    //   3338: istore 12
    //   3340: iload 12
    //   3342: istore 9
    //   3344: aload_0
    //   3345: getfield 504	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3348: iconst_5
    //   3349: if_icmpeq +598 -> 3947
    //   3352: aload_0
    //   3353: getfield 504	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3356: iconst_3
    //   3357: if_icmpeq +590 -> 3947
    //   3360: aload_0
    //   3361: getfield 504	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3364: iconst_1
    //   3365: if_icmpeq +582 -> 3947
    //   3368: aload_0
    //   3369: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3372: ifnull +3372 -> 6744
    //   3375: aload 29
    //   3377: getfield 2542	org/telegram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   3380: ifnull +2806 -> 6186
    //   3383: aload 29
    //   3385: getfield 2542	org/telegram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   3388: ldc_w 2579
    //   3391: invokevirtual 1798	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3394: ifne +48 -> 3442
    //   3397: aload 29
    //   3399: getfield 2542	org/telegram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   3402: ldc_w 2580
    //   3405: invokevirtual 1798	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3408: ifeq +12 -> 3420
    //   3411: aload_0
    //   3412: getfield 504	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3415: bipush 6
    //   3417: if_icmpne +25 -> 3442
    //   3420: aload 29
    //   3422: getfield 2542	org/telegram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   3425: ldc_w 2582
    //   3428: invokevirtual 1798	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3431: ifne +11 -> 3442
    //   3434: aload_0
    //   3435: getfield 504	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3438: iconst_4
    //   3439: if_icmpne +2747 -> 6186
    //   3442: iconst_1
    //   3443: istore 27
    //   3445: aload_0
    //   3446: iload 27
    //   3448: putfield 746	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   3451: aload_0
    //   3452: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3455: ifeq +29 -> 3484
    //   3458: aload_0
    //   3459: aload_0
    //   3460: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3463: fconst_2
    //   3464: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3467: iadd
    //   3468: putfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3471: aload_0
    //   3472: aload_0
    //   3473: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   3476: fconst_2
    //   3477: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3480: iadd
    //   3481: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   3484: iload 10
    //   3486: istore 8
    //   3488: aload_0
    //   3489: getfield 504	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3492: bipush 6
    //   3494: if_icmpne +20 -> 3514
    //   3497: invokestatic 1732	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   3500: ifeq +2692 -> 6192
    //   3503: invokestatic 1737	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   3506: i2f
    //   3507: ldc_w 2583
    //   3510: fmul
    //   3511: f2i
    //   3512: istore 8
    //   3514: iload 11
    //   3516: iload 8
    //   3518: iload 24
    //   3520: iadd
    //   3521: invokestatic 497	java/lang/Math:max	(II)I
    //   3524: istore 12
    //   3526: aload_0
    //   3527: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3530: iconst_m1
    //   3531: putfield 1294	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   3534: aload_0
    //   3535: getfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3538: ifnull +11 -> 3549
    //   3541: aload_0
    //   3542: getfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3545: iconst_m1
    //   3546: putfield 1294	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   3549: iload 13
    //   3551: ifeq +2658 -> 6209
    //   3554: iload 8
    //   3556: istore 11
    //   3558: iload 8
    //   3560: istore 10
    //   3562: iload 11
    //   3564: istore 8
    //   3566: aload_0
    //   3567: getfield 1421	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   3570: ifeq +2791 -> 6361
    //   3573: ldc_w 979
    //   3576: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3579: iload 15
    //   3581: iadd
    //   3582: aload_0
    //   3583: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3586: if_icmple +46 -> 3632
    //   3589: aload_0
    //   3590: aload_0
    //   3591: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   3594: ldc_w 979
    //   3597: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3600: iload 15
    //   3602: iadd
    //   3603: aload_0
    //   3604: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3607: isub
    //   3608: ldc_w 738
    //   3611: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3614: iadd
    //   3615: iadd
    //   3616: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   3619: aload_0
    //   3620: ldc_w 979
    //   3623: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3626: iload 15
    //   3628: iadd
    //   3629: putfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3632: aload_0
    //   3633: aload_0
    //   3634: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3637: ldc_w 738
    //   3640: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3643: isub
    //   3644: putfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3647: aload_0
    //   3648: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   3651: iconst_0
    //   3652: iconst_0
    //   3653: iload 10
    //   3655: iload 8
    //   3657: invokevirtual 1429	org/telegram/messenger/ImageReceiver:setImageCoords	(IIII)V
    //   3660: aload_0
    //   3661: getstatic 1781	java/util/Locale:US	Ljava/util/Locale;
    //   3664: ldc_w 2585
    //   3667: iconst_2
    //   3668: anewarray 1152	java/lang/Object
    //   3671: dup
    //   3672: iconst_0
    //   3673: iload 10
    //   3675: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3678: aastore
    //   3679: dup
    //   3680: iconst_1
    //   3681: iload 8
    //   3683: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3686: aastore
    //   3687: invokestatic 1794	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   3690: putfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   3693: aload_0
    //   3694: getstatic 1781	java/util/Locale:US	Ljava/util/Locale;
    //   3697: ldc_w 2587
    //   3700: iconst_2
    //   3701: anewarray 1152	java/lang/Object
    //   3704: dup
    //   3705: iconst_0
    //   3706: iload 10
    //   3708: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3711: aastore
    //   3712: dup
    //   3713: iconst_1
    //   3714: iload 8
    //   3716: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3719: aastore
    //   3720: invokestatic 1794	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   3723: putfield 1306	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   3726: aload_0
    //   3727: getfield 504	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3730: bipush 6
    //   3732: if_icmpne +2667 -> 6399
    //   3735: aload_0
    //   3736: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   3739: astore 30
    //   3741: aload_0
    //   3742: getfield 1043	org/telegram/ui/Cells/ChatMessageCell:documentAttach	Lorg/telegram/tgnet/TLRPC$Document;
    //   3745: astore 31
    //   3747: aload_0
    //   3748: getfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   3751: astore 32
    //   3753: aload_0
    //   3754: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3757: ifnull +2636 -> 6393
    //   3760: aload_0
    //   3761: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3764: getfield 1207	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   3767: astore 28
    //   3769: aload 30
    //   3771: aload 31
    //   3773: aconst_null
    //   3774: aload 32
    //   3776: aconst_null
    //   3777: aload 28
    //   3779: ldc_w 2589
    //   3782: aload_0
    //   3783: getfield 1043	org/telegram/ui/Cells/ChatMessageCell:documentAttach	Lorg/telegram/tgnet/TLRPC$Document;
    //   3786: getfield 1174	org/telegram/tgnet/TLRPC$Document:size	I
    //   3789: ldc_w 2591
    //   3792: iconst_1
    //   3793: invokevirtual 1272	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   3796: aload_0
    //   3797: iconst_1
    //   3798: putfield 740	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   3801: iload 12
    //   3803: istore 11
    //   3805: aload 29
    //   3807: getfield 2542	org/telegram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   3810: ifnull +127 -> 3937
    //   3813: iload 12
    //   3815: istore 11
    //   3817: aload 29
    //   3819: getfield 2542	org/telegram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   3822: ldc_w 2593
    //   3825: invokevirtual 1798	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3828: ifeq +109 -> 3937
    //   3831: iload 12
    //   3833: istore 11
    //   3835: aload 29
    //   3837: getfield 2594	org/telegram/tgnet/TLRPC$TL_webPage:duration	I
    //   3840: ifeq +97 -> 3937
    //   3843: aload 29
    //   3845: getfield 2594	org/telegram/tgnet/TLRPC$TL_webPage:duration	I
    //   3848: bipush 60
    //   3850: idiv
    //   3851: istore 8
    //   3853: ldc_w 2596
    //   3856: iconst_2
    //   3857: anewarray 1152	java/lang/Object
    //   3860: dup
    //   3861: iconst_0
    //   3862: iload 8
    //   3864: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3867: aastore
    //   3868: dup
    //   3869: iconst_1
    //   3870: aload 29
    //   3872: getfield 2594	org/telegram/tgnet/TLRPC$TL_webPage:duration	I
    //   3875: iload 8
    //   3877: bipush 60
    //   3879: imul
    //   3880: isub
    //   3881: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3884: aastore
    //   3885: invokestatic 1156	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   3888: astore 28
    //   3890: aload_0
    //   3891: getstatic 387	org/telegram/ui/Cells/ChatMessageCell:durationPaint	Landroid/text/TextPaint;
    //   3894: aload 28
    //   3896: invokevirtual 1067	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   3899: f2d
    //   3900: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   3903: d2i
    //   3904: putfield 1436	org/telegram/ui/Cells/ChatMessageCell:durationWidth	I
    //   3907: aload_0
    //   3908: new 286	android/text/StaticLayout
    //   3911: dup
    //   3912: aload 28
    //   3914: getstatic 387	org/telegram/ui/Cells/ChatMessageCell:durationPaint	Landroid/text/TextPaint;
    //   3917: aload_0
    //   3918: getfield 1436	org/telegram/ui/Cells/ChatMessageCell:durationWidth	I
    //   3921: getstatic 1131	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   3924: fconst_1
    //   3925: fconst_0
    //   3926: iconst_0
    //   3927: invokespecial 1134	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   3930: putfield 1434	org/telegram/ui/Cells/ChatMessageCell:videoInfoLayout	Landroid/text/StaticLayout;
    //   3933: iload 12
    //   3935: istore 11
    //   3937: aload_0
    //   3938: iload 9
    //   3940: iload 19
    //   3942: iload 11
    //   3944: invokespecial 2598	org/telegram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   3947: aload_0
    //   3948: getfield 645	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   3951: ifnonnull +219 -> 4170
    //   3954: aload_1
    //   3955: getfield 641	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   3958: ifnull +212 -> 4170
    //   3961: aload_1
    //   3962: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   3965: bipush 13
    //   3967: if_icmpeq +203 -> 4170
    //   3970: aload_0
    //   3971: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   3974: ldc_w 498
    //   3977: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3980: isub
    //   3981: istore 9
    //   3983: aload_0
    //   3984: new 286	android/text/StaticLayout
    //   3987: dup
    //   3988: aload_1
    //   3989: getfield 641	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   3992: invokestatic 2187	org/telegram/messenger/MessageObject:getTextPaint	()Landroid/text/TextPaint;
    //   3995: iload 9
    //   3997: ldc_w 588
    //   4000: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4003: isub
    //   4004: getstatic 1131	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   4007: fconst_1
    //   4008: fconst_0
    //   4009: iconst_0
    //   4010: invokespecial 1134	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   4013: putfield 645	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4016: aload_0
    //   4017: getfield 645	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4020: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   4023: ifle +147 -> 4170
    //   4026: aload_0
    //   4027: getfield 500	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   4030: istore 10
    //   4032: aload_1
    //   4033: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   4036: ifeq +7912 -> 11948
    //   4039: ldc_w 960
    //   4042: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4045: istore 8
    //   4047: aload_0
    //   4048: aload_0
    //   4049: getfield 645	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4052: invokevirtual 2259	android/text/StaticLayout:getHeight	()I
    //   4055: putfield 655	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   4058: aload_0
    //   4059: aload_0
    //   4060: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4063: aload_0
    //   4064: getfield 655	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   4067: ldc_w 1568
    //   4070: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4073: iadd
    //   4074: iadd
    //   4075: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4078: aload_0
    //   4079: getfield 645	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4082: aload_0
    //   4083: getfield 645	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4086: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   4089: iconst_1
    //   4090: isub
    //   4091: invokevirtual 670	android/text/StaticLayout:getLineWidth	(I)F
    //   4094: fstore 6
    //   4096: aload_0
    //   4097: getfield 645	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4100: aload_0
    //   4101: getfield 645	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4104: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   4107: iconst_1
    //   4108: isub
    //   4109: invokevirtual 667	android/text/StaticLayout:getLineLeft	(I)F
    //   4112: fstore 7
    //   4114: iload 9
    //   4116: ldc_w 738
    //   4119: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4122: isub
    //   4123: i2f
    //   4124: fload 6
    //   4126: fload 7
    //   4128: fadd
    //   4129: fsub
    //   4130: iload 10
    //   4132: iload 8
    //   4134: iadd
    //   4135: i2f
    //   4136: fcmpg
    //   4137: ifge +33 -> 4170
    //   4140: aload_0
    //   4141: aload_0
    //   4142: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4145: ldc_w 392
    //   4148: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4151: iadd
    //   4152: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4155: aload_0
    //   4156: aload_0
    //   4157: getfield 655	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   4160: ldc_w 392
    //   4163: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4166: iadd
    //   4167: putfield 655	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   4170: aload_0
    //   4171: getfield 273	org/telegram/ui/Cells/ChatMessageCell:botButtons	Ljava/util/ArrayList;
    //   4174: invokevirtual 1958	java/util/ArrayList:clear	()V
    //   4177: iload 18
    //   4179: ifeq +10 -> 4189
    //   4182: aload_0
    //   4183: getfield 278	org/telegram/ui/Cells/ChatMessageCell:botButtonsByData	Ljava/util/HashMap;
    //   4186: invokevirtual 2599	java/util/HashMap:clear	()V
    //   4189: aload_1
    //   4190: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4193: getfield 2603	org/telegram/tgnet/TLRPC$Message:reply_markup	Lorg/telegram/tgnet/TLRPC$ReplyMarkup;
    //   4196: instanceof 2605
    //   4199: ifeq +7845 -> 12044
    //   4202: aload_1
    //   4203: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4206: getfield 2603	org/telegram/tgnet/TLRPC$Message:reply_markup	Lorg/telegram/tgnet/TLRPC$ReplyMarkup;
    //   4209: getfield 2610	org/telegram/tgnet/TLRPC$ReplyMarkup:rows	Ljava/util/ArrayList;
    //   4212: invokevirtual 591	java/util/ArrayList:size	()I
    //   4215: istore 13
    //   4217: ldc_w 747
    //   4220: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4223: iload 13
    //   4225: imul
    //   4226: fconst_1
    //   4227: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4230: iadd
    //   4231: istore 8
    //   4233: aload_0
    //   4234: iload 8
    //   4236: putfield 2410	org/telegram/ui/Cells/ChatMessageCell:keyboardHeight	I
    //   4239: aload_0
    //   4240: iload 8
    //   4242: putfield 2367	org/telegram/ui/Cells/ChatMessageCell:substractBackgroundHeight	I
    //   4245: aload_0
    //   4246: aload_0
    //   4247: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   4250: putfield 587	org/telegram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   4253: iconst_0
    //   4254: istore 8
    //   4256: aload_1
    //   4257: getfield 2613	org/telegram/messenger/MessageObject:wantedBotKeyboardWidth	I
    //   4260: aload_0
    //   4261: getfield 587	org/telegram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   4264: if_icmple +74 -> 4338
    //   4267: aload_0
    //   4268: getfield 1734	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   4271: ifeq +7696 -> 11967
    //   4274: aload_1
    //   4275: invokevirtual 882	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   4278: ifeq +7689 -> 11967
    //   4281: aload_1
    //   4282: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   4285: ifne +7682 -> 11967
    //   4288: ldc_w 2614
    //   4291: fstore 6
    //   4293: fload 6
    //   4295: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4298: ineg
    //   4299: istore 8
    //   4301: invokestatic 1732	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   4304: ifeq +7671 -> 11975
    //   4307: iload 8
    //   4309: invokestatic 1737	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   4312: iadd
    //   4313: istore 8
    //   4315: aload_0
    //   4316: aload_0
    //   4317: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   4320: aload_1
    //   4321: getfield 2613	org/telegram/messenger/MessageObject:wantedBotKeyboardWidth	I
    //   4324: iload 8
    //   4326: invokestatic 1077	java/lang/Math:min	(II)I
    //   4329: invokestatic 497	java/lang/Math:max	(II)I
    //   4332: putfield 587	org/telegram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   4335: iconst_1
    //   4336: istore 8
    //   4338: iconst_0
    //   4339: istore 10
    //   4341: iconst_0
    //   4342: istore 9
    //   4344: iload 9
    //   4346: iload 13
    //   4348: if_icmpge +7679 -> 12027
    //   4351: aload_1
    //   4352: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4355: getfield 2603	org/telegram/tgnet/TLRPC$Message:reply_markup	Lorg/telegram/tgnet/TLRPC$ReplyMarkup;
    //   4358: getfield 2610	org/telegram/tgnet/TLRPC$ReplyMarkup:rows	Ljava/util/ArrayList;
    //   4361: iload 9
    //   4363: invokevirtual 595	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   4366: checkcast 2616	org/telegram/tgnet/TLRPC$TL_keyboardButtonRow
    //   4369: astore 28
    //   4371: aload 28
    //   4373: getfield 2619	org/telegram/tgnet/TLRPC$TL_keyboardButtonRow:buttons	Ljava/util/ArrayList;
    //   4376: invokevirtual 591	java/util/ArrayList:size	()I
    //   4379: istore 11
    //   4381: aload_0
    //   4382: getfield 587	org/telegram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   4385: istore 12
    //   4387: ldc_w 1527
    //   4390: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4393: istore 14
    //   4395: iload 8
    //   4397: ifne +7601 -> 11998
    //   4400: aload_0
    //   4401: getfield 623	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   4404: ifeq +7594 -> 11998
    //   4407: fconst_0
    //   4408: fstore 6
    //   4410: iload 12
    //   4412: iload 14
    //   4414: iload 11
    //   4416: iconst_1
    //   4417: isub
    //   4418: imul
    //   4419: isub
    //   4420: fload 6
    //   4422: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4425: isub
    //   4426: fconst_2
    //   4427: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4430: isub
    //   4431: iload 11
    //   4433: idiv
    //   4434: istore 14
    //   4436: iconst_0
    //   4437: istore 11
    //   4439: iload 11
    //   4441: aload 28
    //   4443: getfield 2619	org/telegram/tgnet/TLRPC$TL_keyboardButtonRow:buttons	Ljava/util/ArrayList;
    //   4446: invokevirtual 591	java/util/ArrayList:size	()I
    //   4449: if_icmpge +7569 -> 12018
    //   4452: new 14	org/telegram/ui/Cells/ChatMessageCell$BotButton
    //   4455: dup
    //   4456: aload_0
    //   4457: aconst_null
    //   4458: invokespecial 2622	org/telegram/ui/Cells/ChatMessageCell$BotButton:<init>	(Lorg/telegram/ui/Cells/ChatMessageCell;Lorg/telegram/ui/Cells/ChatMessageCell$1;)V
    //   4461: astore 29
    //   4463: aload 29
    //   4465: aload 28
    //   4467: getfield 2619	org/telegram/tgnet/TLRPC$TL_keyboardButtonRow:buttons	Ljava/util/ArrayList;
    //   4470: iload 11
    //   4472: invokevirtual 595	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   4475: checkcast 2624	org/telegram/tgnet/TLRPC$KeyboardButton
    //   4478: invokestatic 2628	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$402	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;Lorg/telegram/tgnet/TLRPC$KeyboardButton;)Lorg/telegram/tgnet/TLRPC$KeyboardButton;
    //   4481: pop
    //   4482: aload 29
    //   4484: invokestatic 630	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$400	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)Lorg/telegram/tgnet/TLRPC$KeyboardButton;
    //   4487: getfield 2631	org/telegram/tgnet/TLRPC$KeyboardButton:data	[B
    //   4490: invokestatic 2637	org/telegram/messenger/Utilities:bytesToHex	([B)Ljava/lang/String;
    //   4493: astore 30
    //   4495: aload_0
    //   4496: getfield 278	org/telegram/ui/Cells/ChatMessageCell:botButtonsByData	Ljava/util/HashMap;
    //   4499: aload 30
    //   4501: invokevirtual 2640	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4504: checkcast 14	org/telegram/ui/Cells/ChatMessageCell$BotButton
    //   4507: astore 31
    //   4509: aload 31
    //   4511: ifnull +7495 -> 12006
    //   4514: aload 29
    //   4516: aload 31
    //   4518: invokestatic 1635	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$600	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)F
    //   4521: invokestatic 1667	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$602	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;F)F
    //   4524: pop
    //   4525: aload 29
    //   4527: aload 31
    //   4529: invokestatic 1639	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$700	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)I
    //   4532: invokestatic 2643	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$702	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   4535: pop
    //   4536: aload 29
    //   4538: aload 31
    //   4540: invokestatic 1644	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$800	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)J
    //   4543: invokestatic 1671	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$802	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;J)J
    //   4546: pop2
    //   4547: aload_0
    //   4548: getfield 278	org/telegram/ui/Cells/ChatMessageCell:botButtonsByData	Ljava/util/HashMap;
    //   4551: aload 30
    //   4553: aload 29
    //   4555: invokevirtual 2647	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   4558: pop
    //   4559: aload 29
    //   4561: ldc_w 1527
    //   4564: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4567: iload 14
    //   4569: iadd
    //   4570: iload 11
    //   4572: imul
    //   4573: invokestatic 2650	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$102	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   4576: pop
    //   4577: aload 29
    //   4579: ldc_w 747
    //   4582: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4585: iload 9
    //   4587: imul
    //   4588: ldc_w 1527
    //   4591: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4594: iadd
    //   4595: invokestatic 2653	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$002	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   4598: pop
    //   4599: aload 29
    //   4601: iload 14
    //   4603: invokestatic 2656	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$202	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   4606: pop
    //   4607: aload 29
    //   4609: ldc_w 1554
    //   4612: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4615: invokestatic 2659	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$302	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   4618: pop
    //   4619: aload 29
    //   4621: new 286	android/text/StaticLayout
    //   4624: dup
    //   4625: aload 29
    //   4627: invokestatic 630	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$400	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)Lorg/telegram/tgnet/TLRPC$KeyboardButton;
    //   4630: getfield 2662	org/telegram/tgnet/TLRPC$KeyboardButton:text	Ljava/lang/String;
    //   4633: getstatic 380	org/telegram/ui/Cells/ChatMessageCell:botButtonPaint	Landroid/text/TextPaint;
    //   4636: invokevirtual 2056	android/text/TextPaint:getFontMetricsInt	()Landroid/graphics/Paint$FontMetricsInt;
    //   4639: ldc_w 323
    //   4642: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4645: iconst_0
    //   4646: invokestatic 2062	org/telegram/messenger/Emoji:replaceEmoji	(Ljava/lang/CharSequence;Landroid/graphics/Paint$FontMetricsInt;IZ)Ljava/lang/CharSequence;
    //   4649: getstatic 380	org/telegram/ui/Cells/ChatMessageCell:botButtonPaint	Landroid/text/TextPaint;
    //   4652: iload 14
    //   4654: ldc_w 588
    //   4657: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4660: isub
    //   4661: i2f
    //   4662: getstatic 1121	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   4665: invokestatic 1125	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   4668: getstatic 380	org/telegram/ui/Cells/ChatMessageCell:botButtonPaint	Landroid/text/TextPaint;
    //   4671: iload 14
    //   4673: ldc_w 588
    //   4676: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4679: isub
    //   4680: getstatic 2665	android/text/Layout$Alignment:ALIGN_CENTER	Landroid/text/Layout$Alignment;
    //   4683: fconst_1
    //   4684: fconst_0
    //   4685: iconst_0
    //   4686: invokespecial 1134	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   4689: invokestatic 2669	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$902	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;Landroid/text/StaticLayout;)Landroid/text/StaticLayout;
    //   4692: pop
    //   4693: aload_0
    //   4694: getfield 273	org/telegram/ui/Cells/ChatMessageCell:botButtons	Ljava/util/ArrayList;
    //   4697: aload 29
    //   4699: invokevirtual 1950	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   4702: pop
    //   4703: iload 10
    //   4705: istore 12
    //   4707: iload 11
    //   4709: aload 28
    //   4711: getfield 2619	org/telegram/tgnet/TLRPC$TL_keyboardButtonRow:buttons	Ljava/util/ArrayList;
    //   4714: invokevirtual 591	java/util/ArrayList:size	()I
    //   4717: iconst_1
    //   4718: isub
    //   4719: if_icmpne +21 -> 4740
    //   4722: iload 10
    //   4724: aload 29
    //   4726: invokestatic 602	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$100	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)I
    //   4729: aload 29
    //   4731: invokestatic 605	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$200	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)I
    //   4734: iadd
    //   4735: invokestatic 497	java/lang/Math:max	(II)I
    //   4738: istore 12
    //   4740: iload 11
    //   4742: iconst_1
    //   4743: iadd
    //   4744: istore 11
    //   4746: iload 12
    //   4748: istore 10
    //   4750: goto -311 -> 4439
    //   4753: iload 10
    //   4755: istore 13
    //   4757: aload_0
    //   4758: aload_0
    //   4759: getfield 786	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   4762: iload 20
    //   4764: ineg
    //   4765: invokestatic 497	java/lang/Math:max	(II)I
    //   4768: putfield 786	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   4771: goto -2003 -> 2768
    //   4774: iload 10
    //   4776: istore 13
    //   4778: iload 10
    //   4780: istore 8
    //   4782: iload 20
    //   4784: aload_0
    //   4785: getfield 782	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   4788: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   4791: if_icmpge -1803 -> 2988
    //   4794: iload 10
    //   4796: istore 13
    //   4798: aload_0
    //   4799: getfield 782	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   4802: iload 20
    //   4804: invokevirtual 667	android/text/StaticLayout:getLineLeft	(I)F
    //   4807: f2d
    //   4808: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   4811: d2i
    //   4812: istore 22
    //   4814: iload 22
    //   4816: ifne +23 -> 4839
    //   4819: iload 10
    //   4821: istore 13
    //   4823: aload_0
    //   4824: getfield 786	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   4827: ifeq +12 -> 4839
    //   4830: iload 10
    //   4832: istore 13
    //   4834: aload_0
    //   4835: iconst_0
    //   4836: putfield 786	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   4839: iload 22
    //   4841: ifeq +172 -> 5013
    //   4844: iload 10
    //   4846: istore 13
    //   4848: aload_0
    //   4849: getfield 782	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   4852: invokevirtual 2551	android/text/StaticLayout:getWidth	()I
    //   4855: iload 22
    //   4857: isub
    //   4858: istore 8
    //   4860: iload 20
    //   4862: iload 11
    //   4864: if_icmplt +36 -> 4900
    //   4867: iload 8
    //   4869: istore 21
    //   4871: iload 11
    //   4873: ifeq +42 -> 4915
    //   4876: iload 8
    //   4878: istore 21
    //   4880: iload 22
    //   4882: ifeq +33 -> 4915
    //   4885: iload 10
    //   4887: istore 13
    //   4889: iload 8
    //   4891: istore 21
    //   4893: aload_0
    //   4894: getfield 1421	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   4897: ifeq +18 -> 4915
    //   4900: iload 10
    //   4902: istore 13
    //   4904: iload 8
    //   4906: ldc_w 2554
    //   4909: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4912: iadd
    //   4913: istore 21
    //   4915: iload 16
    //   4917: istore 8
    //   4919: iload 16
    //   4921: iload 21
    //   4923: iload 24
    //   4925: iadd
    //   4926: if_icmpge +58 -> 4984
    //   4929: iload 14
    //   4931: ifeq +24 -> 4955
    //   4934: iload 10
    //   4936: istore 13
    //   4938: aload_0
    //   4939: aload_0
    //   4940: getfield 1406	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   4943: iload 21
    //   4945: iload 24
    //   4947: iadd
    //   4948: iload 16
    //   4950: isub
    //   4951: iadd
    //   4952: putfield 1406	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   4955: iload 12
    //   4957: ifeq +7145 -> 12102
    //   4960: iload 10
    //   4962: istore 13
    //   4964: aload_0
    //   4965: aload_0
    //   4966: getfield 1410	org/telegram/ui/Cells/ChatMessageCell:authorX	I
    //   4969: iload 21
    //   4971: iload 24
    //   4973: iadd
    //   4974: iload 16
    //   4976: isub
    //   4977: iadd
    //   4978: putfield 1410	org/telegram/ui/Cells/ChatMessageCell:authorX	I
    //   4981: goto +7121 -> 12102
    //   4984: iload 10
    //   4986: istore 13
    //   4988: iload 10
    //   4990: iload 21
    //   4992: iload 24
    //   4994: iadd
    //   4995: invokestatic 497	java/lang/Math:max	(II)I
    //   4998: istore 10
    //   5000: iload 20
    //   5002: iconst_1
    //   5003: iadd
    //   5004: istore 20
    //   5006: iload 8
    //   5008: istore 16
    //   5010: goto -236 -> 4774
    //   5013: iload 17
    //   5015: ifeq +19 -> 5034
    //   5018: iload 10
    //   5020: istore 13
    //   5022: aload_0
    //   5023: getfield 782	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   5026: invokevirtual 2551	android/text/StaticLayout:getWidth	()I
    //   5029: istore 8
    //   5031: goto +7081 -> 12112
    //   5034: iload 10
    //   5036: istore 13
    //   5038: aload_0
    //   5039: getfield 782	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   5042: iload 20
    //   5044: invokevirtual 670	android/text/StaticLayout:getLineWidth	(I)F
    //   5047: f2d
    //   5048: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   5051: dstore_2
    //   5052: dload_2
    //   5053: d2i
    //   5054: istore 8
    //   5056: goto +7056 -> 12112
    //   5059: iconst_0
    //   5060: istore 10
    //   5062: goto -2021 -> 3041
    //   5065: iload 23
    //   5067: istore 10
    //   5069: goto -1965 -> 3104
    //   5072: iconst_0
    //   5073: istore 27
    //   5075: goto -1916 -> 3159
    //   5078: iload 11
    //   5080: iconst_1
    //   5081: iadd
    //   5082: istore 11
    //   5084: goto -1879 -> 3205
    //   5087: aload 28
    //   5089: invokestatic 1159	org/telegram/messenger/MessageObject:isVideoDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   5092: ifeq +183 -> 5275
    //   5095: aload_0
    //   5096: aload 28
    //   5098: getfield 1200	org/telegram/tgnet/TLRPC$Document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5101: putfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5104: aload_0
    //   5105: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5108: ifnull +140 -> 5248
    //   5111: aload_0
    //   5112: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5115: getfield 2570	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   5118: ifeq +13 -> 5131
    //   5121: aload_0
    //   5122: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5125: getfield 2573	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   5128: ifne +120 -> 5248
    //   5131: iconst_0
    //   5132: istore 11
    //   5134: iload 11
    //   5136: aload 28
    //   5138: getfield 1053	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   5141: invokevirtual 591	java/util/ArrayList:size	()I
    //   5144: if_icmpge +50 -> 5194
    //   5147: aload 28
    //   5149: getfield 1053	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   5152: iload 11
    //   5154: invokevirtual 595	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   5157: checkcast 1055	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   5160: astore 30
    //   5162: aload 30
    //   5164: instanceof 1161
    //   5167: ifeq +99 -> 5266
    //   5170: aload_0
    //   5171: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5174: aload 30
    //   5176: getfield 2576	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   5179: putfield 2570	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   5182: aload_0
    //   5183: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5186: aload 30
    //   5188: getfield 2577	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   5191: putfield 2573	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   5194: aload_0
    //   5195: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5198: getfield 2570	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   5201: ifeq +13 -> 5214
    //   5204: aload_0
    //   5205: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5208: getfield 2573	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   5211: ifne +37 -> 5248
    //   5214: aload_0
    //   5215: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5218: astore 28
    //   5220: aload_0
    //   5221: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5224: astore 30
    //   5226: ldc_w 2578
    //   5229: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5232: istore 11
    //   5234: aload 30
    //   5236: iload 11
    //   5238: putfield 2573	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   5241: aload 28
    //   5243: iload 11
    //   5245: putfield 2570	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   5248: aload_0
    //   5249: iconst_0
    //   5250: aload_1
    //   5251: invokespecial 2671	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   5254: pop
    //   5255: iload 9
    //   5257: istore 12
    //   5259: iload 8
    //   5261: istore 11
    //   5263: goto -1923 -> 3340
    //   5266: iload 11
    //   5268: iconst_1
    //   5269: iadd
    //   5270: istore 11
    //   5272: goto -138 -> 5134
    //   5275: aload 28
    //   5277: invokestatic 2674	org/telegram/messenger/MessageObject:isStickerDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   5280: ifeq +188 -> 5468
    //   5283: aload_0
    //   5284: aload 28
    //   5286: getfield 1200	org/telegram/tgnet/TLRPC$Document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5289: putfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5292: aload_0
    //   5293: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5296: ifnull +140 -> 5436
    //   5299: aload_0
    //   5300: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5303: getfield 2570	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   5306: ifeq +13 -> 5319
    //   5309: aload_0
    //   5310: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5313: getfield 2573	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   5316: ifne +120 -> 5436
    //   5319: iconst_0
    //   5320: istore 11
    //   5322: iload 11
    //   5324: aload 28
    //   5326: getfield 1053	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   5329: invokevirtual 591	java/util/ArrayList:size	()I
    //   5332: if_icmpge +50 -> 5382
    //   5335: aload 28
    //   5337: getfield 1053	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   5340: iload 11
    //   5342: invokevirtual 595	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   5345: checkcast 1055	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   5348: astore 30
    //   5350: aload 30
    //   5352: instanceof 2575
    //   5355: ifeq +104 -> 5459
    //   5358: aload_0
    //   5359: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5362: aload 30
    //   5364: getfield 2576	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   5367: putfield 2570	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   5370: aload_0
    //   5371: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5374: aload 30
    //   5376: getfield 2577	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   5379: putfield 2573	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   5382: aload_0
    //   5383: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5386: getfield 2570	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   5389: ifeq +13 -> 5402
    //   5392: aload_0
    //   5393: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5396: getfield 2573	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   5399: ifne +37 -> 5436
    //   5402: aload_0
    //   5403: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5406: astore 30
    //   5408: aload_0
    //   5409: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5412: astore 31
    //   5414: ldc_w 2578
    //   5417: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5420: istore 11
    //   5422: aload 31
    //   5424: iload 11
    //   5426: putfield 2573	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   5429: aload 30
    //   5431: iload 11
    //   5433: putfield 2570	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   5436: aload_0
    //   5437: aload 28
    //   5439: putfield 1043	org/telegram/ui/Cells/ChatMessageCell:documentAttach	Lorg/telegram/tgnet/TLRPC$Document;
    //   5442: aload_0
    //   5443: bipush 6
    //   5445: putfield 504	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   5448: iload 9
    //   5450: istore 12
    //   5452: iload 8
    //   5454: istore 11
    //   5456: goto -2116 -> 3340
    //   5459: iload 11
    //   5461: iconst_1
    //   5462: iadd
    //   5463: istore 11
    //   5465: goto -143 -> 5322
    //   5468: aload_0
    //   5469: iload 9
    //   5471: iload 19
    //   5473: iload 8
    //   5475: invokespecial 2598	org/telegram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   5478: iload 9
    //   5480: istore 12
    //   5482: iload 8
    //   5484: istore 11
    //   5486: aload 28
    //   5488: invokestatic 2674	org/telegram/messenger/MessageObject:isStickerDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   5491: ifne -2151 -> 3340
    //   5494: aload_0
    //   5495: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   5498: ldc_w 960
    //   5501: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5504: iload 9
    //   5506: iadd
    //   5507: if_icmpge +16 -> 5523
    //   5510: aload_0
    //   5511: ldc_w 960
    //   5514: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5517: iload 9
    //   5519: iadd
    //   5520: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   5523: aload 28
    //   5525: invokestatic 1048	org/telegram/messenger/MessageObject:isVoiceDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   5528: ifeq +94 -> 5622
    //   5531: aload_0
    //   5532: aload_0
    //   5533: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   5536: ldc_w 588
    //   5539: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5542: isub
    //   5543: aload_1
    //   5544: invokespecial 2671	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   5547: pop
    //   5548: aload_0
    //   5549: aload_0
    //   5550: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   5553: getfield 735	org/telegram/messenger/MessageObject:textHeight	I
    //   5556: ldc_w 738
    //   5559: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5562: iadd
    //   5563: aload_0
    //   5564: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5567: iadd
    //   5568: putfield 558	org/telegram/ui/Cells/ChatMessageCell:mediaOffsetY	I
    //   5571: aload_0
    //   5572: aload_0
    //   5573: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5576: ldc_w 1554
    //   5579: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5582: iadd
    //   5583: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5586: aload_0
    //   5587: aload_0
    //   5588: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5591: ldc_w 1554
    //   5594: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5597: iadd
    //   5598: putfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5601: aload_0
    //   5602: iload 9
    //   5604: iload 19
    //   5606: iload 8
    //   5608: invokespecial 2598	org/telegram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   5611: iload 9
    //   5613: istore 12
    //   5615: iload 8
    //   5617: istore 11
    //   5619: goto -2279 -> 3340
    //   5622: aload 28
    //   5624: invokestatic 1107	org/telegram/messenger/MessageObject:isMusicDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   5627: ifeq +225 -> 5852
    //   5630: aload_0
    //   5631: aload_0
    //   5632: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   5635: ldc_w 588
    //   5638: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5641: isub
    //   5642: aload_1
    //   5643: invokespecial 2671	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   5646: istore 11
    //   5648: aload_0
    //   5649: aload_0
    //   5650: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   5653: getfield 735	org/telegram/messenger/MessageObject:textHeight	I
    //   5656: ldc_w 738
    //   5659: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5662: iadd
    //   5663: aload_0
    //   5664: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5667: iadd
    //   5668: putfield 558	org/telegram/ui/Cells/ChatMessageCell:mediaOffsetY	I
    //   5671: aload_0
    //   5672: aload_0
    //   5673: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5676: ldc_w 1570
    //   5679: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5682: iadd
    //   5683: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5686: aload_0
    //   5687: aload_0
    //   5688: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5691: ldc_w 1570
    //   5694: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5697: iadd
    //   5698: putfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5701: iload 9
    //   5703: ldc_w 1108
    //   5706: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5709: isub
    //   5710: istore 12
    //   5712: iload 8
    //   5714: iload 11
    //   5716: iload 24
    //   5718: iadd
    //   5719: ldc_w 1061
    //   5722: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5725: iadd
    //   5726: invokestatic 497	java/lang/Math:max	(II)I
    //   5729: istore 9
    //   5731: iload 9
    //   5733: istore 8
    //   5735: aload_0
    //   5736: getfield 1136	org/telegram/ui/Cells/ChatMessageCell:songLayout	Landroid/text/StaticLayout;
    //   5739: ifnull +46 -> 5785
    //   5742: iload 9
    //   5744: istore 8
    //   5746: aload_0
    //   5747: getfield 1136	org/telegram/ui/Cells/ChatMessageCell:songLayout	Landroid/text/StaticLayout;
    //   5750: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   5753: ifle +32 -> 5785
    //   5756: iload 9
    //   5758: i2f
    //   5759: aload_0
    //   5760: getfield 1136	org/telegram/ui/Cells/ChatMessageCell:songLayout	Landroid/text/StaticLayout;
    //   5763: iconst_0
    //   5764: invokevirtual 670	android/text/StaticLayout:getLineWidth	(I)F
    //   5767: iload 24
    //   5769: i2f
    //   5770: fadd
    //   5771: ldc_w 1108
    //   5774: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5777: i2f
    //   5778: fadd
    //   5779: invokestatic 2677	java/lang/Math:max	(FF)F
    //   5782: f2i
    //   5783: istore 8
    //   5785: iload 8
    //   5787: istore 11
    //   5789: aload_0
    //   5790: getfield 1146	org/telegram/ui/Cells/ChatMessageCell:performerLayout	Landroid/text/StaticLayout;
    //   5793: ifnull +46 -> 5839
    //   5796: iload 8
    //   5798: istore 11
    //   5800: aload_0
    //   5801: getfield 1146	org/telegram/ui/Cells/ChatMessageCell:performerLayout	Landroid/text/StaticLayout;
    //   5804: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   5807: ifle +32 -> 5839
    //   5810: iload 8
    //   5812: i2f
    //   5813: aload_0
    //   5814: getfield 1146	org/telegram/ui/Cells/ChatMessageCell:performerLayout	Landroid/text/StaticLayout;
    //   5817: iconst_0
    //   5818: invokevirtual 670	android/text/StaticLayout:getLineWidth	(I)F
    //   5821: iload 24
    //   5823: i2f
    //   5824: fadd
    //   5825: ldc_w 1108
    //   5828: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5831: i2f
    //   5832: fadd
    //   5833: invokestatic 2677	java/lang/Math:max	(FF)F
    //   5836: f2i
    //   5837: istore 11
    //   5839: aload_0
    //   5840: iload 12
    //   5842: iload 19
    //   5844: iload 11
    //   5846: invokespecial 2598	org/telegram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   5849: goto -2509 -> 3340
    //   5852: aload_0
    //   5853: aload_0
    //   5854: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   5857: ldc_w 2678
    //   5860: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5863: isub
    //   5864: aload_1
    //   5865: invokespecial 2671	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   5868: pop
    //   5869: aload_0
    //   5870: iconst_1
    //   5871: putfield 746	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   5874: aload_0
    //   5875: getfield 740	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   5878: ifeq +73 -> 5951
    //   5881: aload_0
    //   5882: aload_0
    //   5883: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5886: ldc_w 1995
    //   5889: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5892: iadd
    //   5893: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5896: aload_0
    //   5897: aload_0
    //   5898: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5901: ldc_w 1108
    //   5904: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5907: iadd
    //   5908: putfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5911: aload_0
    //   5912: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   5915: iconst_0
    //   5916: aload_0
    //   5917: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5920: aload_0
    //   5921: getfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   5924: iadd
    //   5925: ldc_w 1108
    //   5928: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5931: ldc_w 1108
    //   5934: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5937: invokevirtual 1429	org/telegram/messenger/ImageReceiver:setImageCoords	(IIII)V
    //   5940: iload 9
    //   5942: istore 12
    //   5944: iload 8
    //   5946: istore 11
    //   5948: goto -2608 -> 3340
    //   5951: aload_0
    //   5952: aload_0
    //   5953: getfield 484	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   5956: getfield 735	org/telegram/messenger/MessageObject:textHeight	I
    //   5959: ldc_w 738
    //   5962: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5965: iadd
    //   5966: aload_0
    //   5967: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5970: iadd
    //   5971: putfield 558	org/telegram/ui/Cells/ChatMessageCell:mediaOffsetY	I
    //   5974: aload_0
    //   5975: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   5978: iconst_0
    //   5979: aload_0
    //   5980: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5983: aload_0
    //   5984: getfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   5987: iadd
    //   5988: ldc_w 392
    //   5991: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5994: isub
    //   5995: ldc_w 1570
    //   5998: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6001: ldc_w 1570
    //   6004: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6007: invokevirtual 1429	org/telegram/messenger/ImageReceiver:setImageCoords	(IIII)V
    //   6010: aload_0
    //   6011: aload_0
    //   6012: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6015: ldc_w 2679
    //   6018: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6021: iadd
    //   6022: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6025: aload_0
    //   6026: aload_0
    //   6027: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6030: ldc_w 979
    //   6033: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6036: iadd
    //   6037: putfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6040: iload 9
    //   6042: istore 12
    //   6044: iload 8
    //   6046: istore 11
    //   6048: goto -2708 -> 3340
    //   6051: aload 29
    //   6053: getfield 2534	org/telegram/tgnet/TLRPC$TL_webPage:photo	Lorg/telegram/tgnet/TLRPC$Photo;
    //   6056: ifnull +104 -> 6160
    //   6059: aload 29
    //   6061: getfield 2542	org/telegram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   6064: ifnull +103 -> 6167
    //   6067: aload 29
    //   6069: getfield 2542	org/telegram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   6072: ldc_w 2579
    //   6075: invokevirtual 1798	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   6078: ifeq +89 -> 6167
    //   6081: iconst_1
    //   6082: istore 27
    //   6084: aload_0
    //   6085: iload 27
    //   6087: putfield 746	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   6090: aload_1
    //   6091: getfield 1246	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   6094: astore 28
    //   6096: aload_0
    //   6097: getfield 746	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   6100: ifeq +73 -> 6173
    //   6103: invokestatic 1249	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   6106: istore 11
    //   6108: aload_0
    //   6109: getfield 746	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   6112: ifne +68 -> 6180
    //   6115: iconst_1
    //   6116: istore 27
    //   6118: aload_0
    //   6119: aload 28
    //   6121: iload 11
    //   6123: iload 27
    //   6125: invokestatic 2682	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;IZ)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6128: putfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6131: aload_0
    //   6132: aload_1
    //   6133: getfield 1246	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   6136: bipush 80
    //   6138: invokestatic 1253	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6141: putfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6144: aload_0
    //   6145: getfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6148: aload_0
    //   6149: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6152: if_acmpne +8 -> 6160
    //   6155: aload_0
    //   6156: aconst_null
    //   6157: putfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6160: iload 8
    //   6162: istore 11
    //   6164: goto -2820 -> 3344
    //   6167: iconst_0
    //   6168: istore 27
    //   6170: goto -86 -> 6084
    //   6173: iload 10
    //   6175: istore 11
    //   6177: goto -69 -> 6108
    //   6180: iconst_0
    //   6181: istore 27
    //   6183: goto -65 -> 6118
    //   6186: iconst_0
    //   6187: istore 27
    //   6189: goto -2744 -> 3445
    //   6192: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   6195: getfield 1747	android/graphics/Point:x	I
    //   6198: i2f
    //   6199: ldc_w 2583
    //   6202: fmul
    //   6203: f2i
    //   6204: istore 8
    //   6206: goto -2692 -> 3514
    //   6209: aload_0
    //   6210: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6213: getfield 2570	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   6216: istore 11
    //   6218: aload_0
    //   6219: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6222: getfield 2573	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   6225: istore 10
    //   6227: iload 11
    //   6229: i2f
    //   6230: iload 8
    //   6232: fconst_2
    //   6233: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6236: isub
    //   6237: i2f
    //   6238: fdiv
    //   6239: fstore 6
    //   6241: iload 11
    //   6243: i2f
    //   6244: fload 6
    //   6246: fdiv
    //   6247: f2i
    //   6248: istore 11
    //   6250: iload 10
    //   6252: i2f
    //   6253: fload 6
    //   6255: fdiv
    //   6256: f2i
    //   6257: istore 13
    //   6259: aload 29
    //   6261: getfield 2531	org/telegram/tgnet/TLRPC$TL_webPage:site_name	Ljava/lang/String;
    //   6264: ifnull +59 -> 6323
    //   6267: iload 13
    //   6269: istore 8
    //   6271: iload 11
    //   6273: istore 10
    //   6275: aload 29
    //   6277: getfield 2531	org/telegram/tgnet/TLRPC$TL_webPage:site_name	Ljava/lang/String;
    //   6280: ifnull -2714 -> 3566
    //   6283: iload 13
    //   6285: istore 8
    //   6287: iload 11
    //   6289: istore 10
    //   6291: aload 29
    //   6293: getfield 2531	org/telegram/tgnet/TLRPC$TL_webPage:site_name	Ljava/lang/String;
    //   6296: invokevirtual 1191	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   6299: ldc_w 2536
    //   6302: invokevirtual 1798	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   6305: ifne -2739 -> 3566
    //   6308: iload 13
    //   6310: istore 8
    //   6312: iload 11
    //   6314: istore 10
    //   6316: aload_0
    //   6317: getfield 504	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   6320: ifne -2754 -> 3566
    //   6323: iload 13
    //   6325: istore 8
    //   6327: iload 11
    //   6329: istore 10
    //   6331: iload 13
    //   6333: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   6336: getfield 1750	android/graphics/Point:y	I
    //   6339: iconst_3
    //   6340: idiv
    //   6341: if_icmple -2775 -> 3566
    //   6344: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   6347: getfield 1750	android/graphics/Point:y	I
    //   6350: iconst_3
    //   6351: idiv
    //   6352: istore 8
    //   6354: iload 11
    //   6356: istore 10
    //   6358: goto -2792 -> 3566
    //   6361: aload_0
    //   6362: aload_0
    //   6363: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6366: ldc_w 300
    //   6369: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6372: iload 8
    //   6374: iadd
    //   6375: iadd
    //   6376: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6379: aload_0
    //   6380: aload_0
    //   6381: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6384: iload 8
    //   6386: iadd
    //   6387: putfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6390: goto -2743 -> 3647
    //   6393: aconst_null
    //   6394: astore 28
    //   6396: goto -2627 -> 3769
    //   6399: aload_0
    //   6400: getfield 504	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   6403: iconst_4
    //   6404: if_icmpne +29 -> 6433
    //   6407: aload_0
    //   6408: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6411: aconst_null
    //   6412: aconst_null
    //   6413: aload_0
    //   6414: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6417: getfield 1207	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6420: aload_0
    //   6421: getfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   6424: iconst_0
    //   6425: aconst_null
    //   6426: iconst_0
    //   6427: invokevirtual 1297	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   6430: goto -2634 -> 3796
    //   6433: aload_0
    //   6434: getfield 504	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   6437: iconst_2
    //   6438: if_icmpne +119 -> 6557
    //   6441: aload_1
    //   6442: getfield 2115	org/telegram/messenger/MessageObject:mediaExists	Z
    //   6445: istore 27
    //   6447: aload 29
    //   6449: getfield 2564	org/telegram/tgnet/TLRPC$TL_webPage:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   6452: invokestatic 2686	org/telegram/messenger/FileLoader:getAttachFileName	(Lorg/telegram/tgnet/TLObject;)Ljava/lang/String;
    //   6455: astore 28
    //   6457: iload 27
    //   6459: ifne +25 -> 6484
    //   6462: invokestatic 441	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   6465: bipush 32
    //   6467: invokevirtual 2689	org/telegram/messenger/MediaController:canDownloadMedia	(I)Z
    //   6470: ifne +14 -> 6484
    //   6473: invokestatic 1300	org/telegram/messenger/FileLoader:getInstance	()Lorg/telegram/messenger/FileLoader;
    //   6476: aload 28
    //   6478: invokevirtual 2692	org/telegram/messenger/FileLoader:isLoadingFile	(Ljava/lang/String;)Z
    //   6481: ifeq +45 -> 6526
    //   6484: aload_0
    //   6485: iconst_0
    //   6486: putfield 1800	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   6489: aload_0
    //   6490: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6493: aload 29
    //   6495: getfield 2564	org/telegram/tgnet/TLRPC$TL_webPage:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   6498: aconst_null
    //   6499: aload_0
    //   6500: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6503: getfield 1207	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6506: aload_0
    //   6507: getfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   6510: aload 29
    //   6512: getfield 2564	org/telegram/tgnet/TLRPC$TL_webPage:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   6515: getfield 1174	org/telegram/tgnet/TLRPC$Document:size	I
    //   6518: aconst_null
    //   6519: iconst_0
    //   6520: invokevirtual 1297	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   6523: goto -2727 -> 3796
    //   6526: aload_0
    //   6527: iconst_1
    //   6528: putfield 1800	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   6531: aload_0
    //   6532: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6535: aconst_null
    //   6536: aconst_null
    //   6537: aload_0
    //   6538: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6541: getfield 1207	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6544: aload_0
    //   6545: getfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   6548: iconst_0
    //   6549: aconst_null
    //   6550: iconst_0
    //   6551: invokevirtual 1297	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   6554: goto -2758 -> 3796
    //   6557: aload_1
    //   6558: getfield 2115	org/telegram/messenger/MessageObject:mediaExists	Z
    //   6561: istore 27
    //   6563: aload_0
    //   6564: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6567: invokestatic 2686	org/telegram/messenger/FileLoader:getAttachFileName	(Lorg/telegram/tgnet/TLObject;)Ljava/lang/String;
    //   6570: astore 28
    //   6572: iload 27
    //   6574: ifne +24 -> 6598
    //   6577: invokestatic 441	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   6580: iconst_1
    //   6581: invokevirtual 2689	org/telegram/messenger/MediaController:canDownloadMedia	(I)Z
    //   6584: ifne +14 -> 6598
    //   6587: invokestatic 1300	org/telegram/messenger/FileLoader:getInstance	()Lorg/telegram/messenger/FileLoader;
    //   6590: aload 28
    //   6592: invokevirtual 2692	org/telegram/messenger/FileLoader:isLoadingFile	(Ljava/lang/String;)Z
    //   6595: ifeq +72 -> 6667
    //   6598: aload_0
    //   6599: iconst_0
    //   6600: putfield 1800	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   6603: aload_0
    //   6604: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6607: astore 30
    //   6609: aload_0
    //   6610: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6613: getfield 1207	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6616: astore 31
    //   6618: aload_0
    //   6619: getfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   6622: astore 32
    //   6624: aload_0
    //   6625: getfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6628: ifnull +33 -> 6661
    //   6631: aload_0
    //   6632: getfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6635: getfield 1207	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6638: astore 28
    //   6640: aload 30
    //   6642: aload 31
    //   6644: aload 32
    //   6646: aload 28
    //   6648: aload_0
    //   6649: getfield 1306	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   6652: iconst_0
    //   6653: aconst_null
    //   6654: iconst_0
    //   6655: invokevirtual 1297	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   6658: goto -2862 -> 3796
    //   6661: aconst_null
    //   6662: astore 28
    //   6664: goto -24 -> 6640
    //   6667: aload_0
    //   6668: iconst_1
    //   6669: putfield 1800	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   6672: aload_0
    //   6673: getfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6676: ifnull +54 -> 6730
    //   6679: aload_0
    //   6680: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6683: aconst_null
    //   6684: aconst_null
    //   6685: aload_0
    //   6686: getfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6689: getfield 1207	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6692: getstatic 1781	java/util/Locale:US	Ljava/util/Locale;
    //   6695: ldc_w 2587
    //   6698: iconst_2
    //   6699: anewarray 1152	java/lang/Object
    //   6702: dup
    //   6703: iconst_0
    //   6704: iload 10
    //   6706: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   6709: aastore
    //   6710: dup
    //   6711: iconst_1
    //   6712: iload 8
    //   6714: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   6717: aastore
    //   6718: invokestatic 1794	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   6721: iconst_0
    //   6722: aconst_null
    //   6723: iconst_0
    //   6724: invokevirtual 1297	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   6727: goto -2931 -> 3796
    //   6730: aload_0
    //   6731: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6734: aconst_null
    //   6735: checkcast 614	android/graphics/drawable/Drawable
    //   6738: invokevirtual 1278	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   6741: goto -2945 -> 3796
    //   6744: aload_0
    //   6745: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6748: aconst_null
    //   6749: checkcast 614	android/graphics/drawable/Drawable
    //   6752: invokevirtual 1278	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   6755: aload_0
    //   6756: aload_0
    //   6757: getfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6760: ldc_w 1528
    //   6763: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6766: isub
    //   6767: putfield 737	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6770: aload_0
    //   6771: aload_0
    //   6772: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6775: ldc_w 963
    //   6778: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6781: iadd
    //   6782: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6785: goto -2848 -> 3937
    //   6788: aload_0
    //   6789: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6792: aconst_null
    //   6793: checkcast 614	android/graphics/drawable/Drawable
    //   6796: invokevirtual 1278	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   6799: aload_0
    //   6800: iload 9
    //   6802: iload 19
    //   6804: iload 12
    //   6806: invokespecial 2598	org/telegram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   6809: goto -2862 -> 3947
    //   6812: aload_1
    //   6813: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   6816: bipush 12
    //   6818: if_icmpne +665 -> 7483
    //   6821: aload_0
    //   6822: iconst_0
    //   6823: putfield 1859	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   6826: aload_0
    //   6827: iconst_1
    //   6828: putfield 1869	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   6831: aload_0
    //   6832: iconst_1
    //   6833: putfield 740	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   6836: aload_0
    //   6837: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6840: ldc_w 1751
    //   6843: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6846: invokevirtual 428	org/telegram/messenger/ImageReceiver:setRoundRadius	(I)V
    //   6849: invokestatic 1732	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   6852: ifeq +485 -> 7337
    //   6855: invokestatic 1737	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   6858: istore 8
    //   6860: aload_0
    //   6861: getfield 1734	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   6864: ifeq +465 -> 7329
    //   6867: aload_1
    //   6868: invokevirtual 882	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   6871: ifeq +458 -> 7329
    //   6874: aload_1
    //   6875: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   6878: ifne +451 -> 7329
    //   6881: ldc_w 2693
    //   6884: fstore 6
    //   6886: aload_0
    //   6887: iload 8
    //   6889: fload 6
    //   6891: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6894: isub
    //   6895: ldc_w 2694
    //   6898: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6901: invokestatic 1077	java/lang/Math:min	(II)I
    //   6904: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   6907: aload_0
    //   6908: aload_0
    //   6909: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   6912: ldc_w 498
    //   6915: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6918: isub
    //   6919: putfield 1069	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   6922: aload_1
    //   6923: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   6926: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   6929: getfield 992	org/telegram/tgnet/TLRPC$MessageMedia:user_id	I
    //   6932: istore 8
    //   6934: invokestatic 887	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   6937: iload 8
    //   6939: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   6942: invokevirtual 900	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   6945: astore 30
    //   6947: aload_0
    //   6948: invokespecial 1992	org/telegram/ui/Cells/ChatMessageCell:getMaxNameWidth	()I
    //   6951: ldc_w 2695
    //   6954: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6957: isub
    //   6958: istore 8
    //   6960: iload 8
    //   6962: ifge +5116 -> 12078
    //   6965: ldc_w 588
    //   6968: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6971: istore 8
    //   6973: aconst_null
    //   6974: astore 28
    //   6976: aconst_null
    //   6977: astore 29
    //   6979: aload 30
    //   6981: ifnull +34 -> 7015
    //   6984: aload 29
    //   6986: astore 28
    //   6988: aload 30
    //   6990: getfield 1834	org/telegram/tgnet/TLRPC$User:photo	Lorg/telegram/tgnet/TLRPC$UserProfilePhoto;
    //   6993: ifnull +13 -> 7006
    //   6996: aload 30
    //   6998: getfield 1834	org/telegram/tgnet/TLRPC$User:photo	Lorg/telegram/tgnet/TLRPC$UserProfilePhoto;
    //   7001: getfield 1839	org/telegram/tgnet/TLRPC$UserProfilePhoto:photo_small	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   7004: astore 28
    //   7006: aload_0
    //   7007: getfield 449	org/telegram/ui/Cells/ChatMessageCell:contactAvatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   7010: aload 30
    //   7012: invokevirtual 1970	org/telegram/ui/Components/AvatarDrawable:setInfo	(Lorg/telegram/tgnet/TLRPC$User;)V
    //   7015: aload_0
    //   7016: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   7019: astore 31
    //   7021: aload 30
    //   7023: ifnull +380 -> 7403
    //   7026: aload_0
    //   7027: getfield 449	org/telegram/ui/Cells/ChatMessageCell:contactAvatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   7030: astore 29
    //   7032: aload 31
    //   7034: aload 28
    //   7036: ldc_w 1972
    //   7039: aload 29
    //   7041: aconst_null
    //   7042: iconst_0
    //   7043: invokevirtual 1975	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Ljava/lang/String;Z)V
    //   7046: aload_1
    //   7047: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   7050: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   7053: getfield 2698	org/telegram/tgnet/TLRPC$MessageMedia:phone_number	Ljava/lang/String;
    //   7056: astore 28
    //   7058: aload 28
    //   7060: ifnull +374 -> 7434
    //   7063: aload 28
    //   7065: invokevirtual 937	java/lang/String:length	()I
    //   7068: ifeq +366 -> 7434
    //   7071: invokestatic 2703	org/telegram/PhoneFormat/PhoneFormat:getInstance	()Lorg/telegram/PhoneFormat/PhoneFormat;
    //   7074: aload 28
    //   7076: invokevirtual 2706	org/telegram/PhoneFormat/PhoneFormat:format	(Ljava/lang/String;)Ljava/lang/String;
    //   7079: astore 28
    //   7081: aload_1
    //   7082: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   7085: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   7088: getfield 2707	org/telegram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   7091: aload_1
    //   7092: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   7095: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   7098: getfield 2708	org/telegram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   7101: invokestatic 1938	org/telegram/messenger/ContactsController:formatName	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   7104: bipush 10
    //   7106: bipush 32
    //   7108: invokevirtual 1115	java/lang/String:replace	(CC)Ljava/lang/String;
    //   7111: astore 30
    //   7113: aload 30
    //   7115: astore 29
    //   7117: aload 30
    //   7119: invokeinterface 1033 1 0
    //   7124: ifne +7 -> 7131
    //   7127: aload 28
    //   7129: astore 29
    //   7131: aload_0
    //   7132: new 286	android/text/StaticLayout
    //   7135: dup
    //   7136: aload 29
    //   7138: getstatic 383	org/telegram/ui/Cells/ChatMessageCell:contactNamePaint	Landroid/text/TextPaint;
    //   7141: iload 8
    //   7143: i2f
    //   7144: getstatic 1121	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   7147: invokestatic 1125	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   7150: getstatic 383	org/telegram/ui/Cells/ChatMessageCell:contactNamePaint	Landroid/text/TextPaint;
    //   7153: iload 8
    //   7155: fconst_2
    //   7156: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7159: iadd
    //   7160: getstatic 1131	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   7163: fconst_1
    //   7164: fconst_0
    //   7165: iconst_0
    //   7166: invokespecial 1134	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   7169: putfield 1404	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   7172: aload_0
    //   7173: new 286	android/text/StaticLayout
    //   7176: dup
    //   7177: aload 28
    //   7179: bipush 10
    //   7181: bipush 32
    //   7183: invokevirtual 1115	java/lang/String:replace	(CC)Ljava/lang/String;
    //   7186: getstatic 385	org/telegram/ui/Cells/ChatMessageCell:contactPhonePaint	Landroid/text/TextPaint;
    //   7189: iload 8
    //   7191: i2f
    //   7192: getstatic 1121	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   7195: invokestatic 1125	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   7198: getstatic 385	org/telegram/ui/Cells/ChatMessageCell:contactPhonePaint	Landroid/text/TextPaint;
    //   7201: iload 8
    //   7203: fconst_2
    //   7204: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7207: iadd
    //   7208: getstatic 1131	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   7211: fconst_1
    //   7212: fconst_0
    //   7213: iconst_0
    //   7214: invokespecial 1134	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   7217: putfield 1235	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   7220: aload_0
    //   7221: aload_1
    //   7222: invokespecial 2526	org/telegram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/telegram/messenger/MessageObject;)V
    //   7225: aload_0
    //   7226: getfield 1869	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   7229: ifeq +219 -> 7448
    //   7232: aload_1
    //   7233: invokevirtual 2028	org/telegram/messenger/MessageObject:isForwarded	()Z
    //   7236: ifeq +212 -> 7448
    //   7239: aload_0
    //   7240: aload_0
    //   7241: getfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   7244: ldc_w 1527
    //   7247: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7250: iadd
    //   7251: putfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   7254: aload_0
    //   7255: ldc_w 2709
    //   7258: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7261: aload_0
    //   7262: getfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   7265: iadd
    //   7266: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   7269: aload_0
    //   7270: getfield 1235	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   7273: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   7276: ifle -3329 -> 3947
    //   7279: aload_0
    //   7280: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7283: ldc_w 2695
    //   7286: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7289: isub
    //   7290: aload_0
    //   7291: getfield 1235	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   7294: iconst_0
    //   7295: invokevirtual 670	android/text/StaticLayout:getLineWidth	(I)F
    //   7298: f2d
    //   7299: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   7302: d2i
    //   7303: isub
    //   7304: aload_0
    //   7305: getfield 500	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   7308: if_icmpge -3361 -> 3947
    //   7311: aload_0
    //   7312: aload_0
    //   7313: getfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   7316: ldc_w 738
    //   7319: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7322: iadd
    //   7323: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   7326: goto -3379 -> 3947
    //   7329: ldc_w 979
    //   7332: fstore 6
    //   7334: goto -448 -> 6886
    //   7337: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   7340: getfield 1747	android/graphics/Point:x	I
    //   7343: istore 8
    //   7345: aload_0
    //   7346: getfield 1734	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   7349: ifeq +46 -> 7395
    //   7352: aload_1
    //   7353: invokevirtual 882	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   7356: ifeq +39 -> 7395
    //   7359: aload_1
    //   7360: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   7363: ifne +32 -> 7395
    //   7366: ldc_w 2693
    //   7369: fstore 6
    //   7371: aload_0
    //   7372: iload 8
    //   7374: fload 6
    //   7376: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7379: isub
    //   7380: ldc_w 2694
    //   7383: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7386: invokestatic 1077	java/lang/Math:min	(II)I
    //   7389: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7392: goto -485 -> 6907
    //   7395: ldc_w 979
    //   7398: fstore 6
    //   7400: goto -29 -> 7371
    //   7403: getstatic 2712	org/telegram/ui/ActionBar/Theme:contactDrawable	[Landroid/graphics/drawable/Drawable;
    //   7406: astore 29
    //   7408: aload_1
    //   7409: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   7412: ifeq +16 -> 7428
    //   7415: iconst_1
    //   7416: istore 9
    //   7418: aload 29
    //   7420: iload 9
    //   7422: aaload
    //   7423: astore 29
    //   7425: goto -393 -> 7032
    //   7428: iconst_0
    //   7429: istore 9
    //   7431: goto -13 -> 7418
    //   7434: ldc_w 2714
    //   7437: ldc_w 2715
    //   7440: invokestatic 1224	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   7443: astore 28
    //   7445: goto -364 -> 7081
    //   7448: aload_0
    //   7449: getfield 1990	org/telegram/ui/Cells/ChatMessageCell:drawNameLayout	Z
    //   7452: ifeq -198 -> 7254
    //   7455: aload_1
    //   7456: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   7459: getfield 952	org/telegram/tgnet/TLRPC$Message:reply_to_msg_id	I
    //   7462: ifne -208 -> 7254
    //   7465: aload_0
    //   7466: aload_0
    //   7467: getfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   7470: ldc_w 624
    //   7473: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7476: iadd
    //   7477: putfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   7480: goto -226 -> 7254
    //   7483: aload_1
    //   7484: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   7487: iconst_2
    //   7488: if_icmpne +173 -> 7661
    //   7491: aload_0
    //   7492: iconst_1
    //   7493: putfield 1869	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   7496: invokestatic 1732	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   7499: ifeq +96 -> 7595
    //   7502: invokestatic 1737	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   7505: istore 8
    //   7507: aload_0
    //   7508: getfield 1734	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   7511: ifeq +76 -> 7587
    //   7514: aload_1
    //   7515: invokevirtual 882	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   7518: ifeq +69 -> 7587
    //   7521: aload_1
    //   7522: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   7525: ifne +62 -> 7587
    //   7528: ldc_w 2693
    //   7531: fstore 6
    //   7533: aload_0
    //   7534: iload 8
    //   7536: fload 6
    //   7538: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7541: isub
    //   7542: ldc_w 2694
    //   7545: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7548: invokestatic 1077	java/lang/Math:min	(II)I
    //   7551: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7554: aload_0
    //   7555: aload_0
    //   7556: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7559: aload_1
    //   7560: invokespecial 2671	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   7563: pop
    //   7564: aload_0
    //   7565: aload_1
    //   7566: invokespecial 2526	org/telegram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/telegram/messenger/MessageObject;)V
    //   7569: aload_0
    //   7570: ldc_w 2709
    //   7573: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7576: aload_0
    //   7577: getfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   7580: iadd
    //   7581: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   7584: goto -3637 -> 3947
    //   7587: ldc_w 979
    //   7590: fstore 6
    //   7592: goto -59 -> 7533
    //   7595: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   7598: getfield 1747	android/graphics/Point:x	I
    //   7601: istore 8
    //   7603: aload_0
    //   7604: getfield 1734	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   7607: ifeq +46 -> 7653
    //   7610: aload_1
    //   7611: invokevirtual 882	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   7614: ifeq +39 -> 7653
    //   7617: aload_1
    //   7618: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   7621: ifne +32 -> 7653
    //   7624: ldc_w 2693
    //   7627: fstore 6
    //   7629: aload_0
    //   7630: iload 8
    //   7632: fload 6
    //   7634: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7637: isub
    //   7638: ldc_w 2694
    //   7641: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7644: invokestatic 1077	java/lang/Math:min	(II)I
    //   7647: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7650: goto -96 -> 7554
    //   7653: ldc_w 979
    //   7656: fstore 6
    //   7658: goto -29 -> 7629
    //   7661: aload_1
    //   7662: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   7665: bipush 14
    //   7667: if_icmpne +168 -> 7835
    //   7670: invokestatic 1732	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   7673: ifeq +96 -> 7769
    //   7676: invokestatic 1737	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   7679: istore 8
    //   7681: aload_0
    //   7682: getfield 1734	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   7685: ifeq +76 -> 7761
    //   7688: aload_1
    //   7689: invokevirtual 882	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   7692: ifeq +69 -> 7761
    //   7695: aload_1
    //   7696: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   7699: ifne +62 -> 7761
    //   7702: ldc_w 2693
    //   7705: fstore 6
    //   7707: aload_0
    //   7708: iload 8
    //   7710: fload 6
    //   7712: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7715: isub
    //   7716: ldc_w 2694
    //   7719: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7722: invokestatic 1077	java/lang/Math:min	(II)I
    //   7725: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7728: aload_0
    //   7729: aload_0
    //   7730: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7733: aload_1
    //   7734: invokespecial 2671	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   7737: pop
    //   7738: aload_0
    //   7739: aload_1
    //   7740: invokespecial 2526	org/telegram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/telegram/messenger/MessageObject;)V
    //   7743: aload_0
    //   7744: ldc_w 2716
    //   7747: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7750: aload_0
    //   7751: getfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   7754: iadd
    //   7755: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   7758: goto -3811 -> 3947
    //   7761: ldc_w 979
    //   7764: fstore 6
    //   7766: goto -59 -> 7707
    //   7769: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   7772: getfield 1747	android/graphics/Point:x	I
    //   7775: istore 8
    //   7777: aload_0
    //   7778: getfield 1734	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   7781: ifeq +46 -> 7827
    //   7784: aload_1
    //   7785: invokevirtual 882	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   7788: ifeq +39 -> 7827
    //   7791: aload_1
    //   7792: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   7795: ifne +32 -> 7827
    //   7798: ldc_w 2693
    //   7801: fstore 6
    //   7803: aload_0
    //   7804: iload 8
    //   7806: fload 6
    //   7808: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7811: isub
    //   7812: ldc_w 2694
    //   7815: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7818: invokestatic 1077	java/lang/Math:min	(II)I
    //   7821: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7824: goto -96 -> 7728
    //   7827: ldc_w 979
    //   7830: fstore 6
    //   7832: goto -29 -> 7803
    //   7835: aload_1
    //   7836: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   7839: getfield 871	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$TL_messageFwdHeader;
    //   7842: ifnull +506 -> 8348
    //   7845: aload_1
    //   7846: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   7849: bipush 13
    //   7851: if_icmpeq +497 -> 8348
    //   7854: iconst_1
    //   7855: istore 27
    //   7857: aload_0
    //   7858: iload 27
    //   7860: putfield 1869	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   7863: aload_1
    //   7864: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   7867: bipush 9
    //   7869: if_icmpeq +485 -> 8354
    //   7872: iconst_1
    //   7873: istore 27
    //   7875: aload_0
    //   7876: iload 27
    //   7878: putfield 623	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   7881: aload_0
    //   7882: iconst_1
    //   7883: putfield 746	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   7886: aload_0
    //   7887: iconst_1
    //   7888: putfield 740	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   7891: iconst_0
    //   7892: istore 11
    //   7894: iconst_0
    //   7895: istore 13
    //   7897: iconst_0
    //   7898: istore 16
    //   7900: iconst_0
    //   7901: istore 17
    //   7903: iconst_0
    //   7904: istore 12
    //   7906: aload_1
    //   7907: getfield 817	org/telegram/messenger/MessageObject:audioProgress	F
    //   7910: fconst_2
    //   7911: fcmpl
    //   7912: ifeq +26 -> 7938
    //   7915: invokestatic 441	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   7918: invokevirtual 766	org/telegram/messenger/MediaController:canAutoplayGifs	()Z
    //   7921: ifne +17 -> 7938
    //   7924: aload_1
    //   7925: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   7928: bipush 8
    //   7930: if_icmpne +8 -> 7938
    //   7933: aload_1
    //   7934: fconst_1
    //   7935: putfield 817	org/telegram/messenger/MessageObject:audioProgress	F
    //   7938: aload_0
    //   7939: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   7942: astore 28
    //   7944: aload_1
    //   7945: getfield 817	org/telegram/messenger/MessageObject:audioProgress	F
    //   7948: fconst_0
    //   7949: fcmpl
    //   7950: ifne +410 -> 8360
    //   7953: iconst_1
    //   7954: istore 27
    //   7956: aload 28
    //   7958: iload 27
    //   7960: invokevirtual 820	org/telegram/messenger/ImageReceiver:setAllowStartAnimation	(Z)V
    //   7963: aload_0
    //   7964: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   7967: aload_1
    //   7968: invokevirtual 975	org/telegram/messenger/MessageObject:isSecretPhoto	()Z
    //   7971: invokevirtual 2719	org/telegram/messenger/ImageReceiver:setForcePreview	(Z)V
    //   7974: aload_1
    //   7975: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   7978: bipush 9
    //   7980: if_icmpne +512 -> 8492
    //   7983: invokestatic 1732	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   7986: ifeq +388 -> 8374
    //   7989: invokestatic 1737	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   7992: istore 8
    //   7994: aload_0
    //   7995: getfield 1734	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   7998: ifeq +368 -> 8366
    //   8001: aload_1
    //   8002: invokevirtual 882	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   8005: ifeq +361 -> 8366
    //   8008: aload_1
    //   8009: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   8012: ifne +354 -> 8366
    //   8015: ldc_w 2693
    //   8018: fstore 6
    //   8020: aload_0
    //   8021: iload 8
    //   8023: fload 6
    //   8025: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8028: isub
    //   8029: ldc_w 2694
    //   8032: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8035: invokestatic 1077	java/lang/Math:min	(II)I
    //   8038: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8041: aload_0
    //   8042: aload_1
    //   8043: invokespecial 2521	org/telegram/ui/Cells/ChatMessageCell:checkNeedDrawShareButton	(Lorg/telegram/messenger/MessageObject;)Z
    //   8046: ifeq +18 -> 8064
    //   8049: aload_0
    //   8050: aload_0
    //   8051: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8054: ldc_w 960
    //   8057: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8060: isub
    //   8061: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8064: aload_0
    //   8065: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8068: ldc_w 2720
    //   8071: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8074: isub
    //   8075: istore 9
    //   8077: aload_0
    //   8078: iload 9
    //   8080: aload_1
    //   8081: invokespecial 2671	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   8084: pop
    //   8085: iload 9
    //   8087: istore 8
    //   8089: aload_1
    //   8090: getfield 641	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   8093: invokestatic 780	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   8096: ifne +14 -> 8110
    //   8099: iload 9
    //   8101: ldc_w 1108
    //   8104: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8107: iadd
    //   8108: istore 8
    //   8110: aload_0
    //   8111: getfield 740	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   8114: ifeq +326 -> 8440
    //   8117: ldc_w 1108
    //   8120: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8123: istore 10
    //   8125: ldc_w 1108
    //   8128: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8131: istore 11
    //   8133: aload_0
    //   8134: iload 8
    //   8136: putfield 1069	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   8139: iload 12
    //   8141: istore 14
    //   8143: iload 11
    //   8145: istore 8
    //   8147: iload 10
    //   8149: istore 9
    //   8151: aload_0
    //   8152: getfield 740	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   8155: ifne +115 -> 8270
    //   8158: iload 12
    //   8160: istore 14
    //   8162: iload 11
    //   8164: istore 8
    //   8166: iload 10
    //   8168: istore 9
    //   8170: aload_1
    //   8171: getfield 641	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   8174: invokestatic 780	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   8177: ifeq +93 -> 8270
    //   8180: iload 12
    //   8182: istore 14
    //   8184: iload 11
    //   8186: istore 8
    //   8188: iload 10
    //   8190: istore 9
    //   8192: aload_0
    //   8193: getfield 1185	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   8196: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   8199: ifle +71 -> 8270
    //   8202: aload_0
    //   8203: aload_1
    //   8204: invokespecial 1073	org/telegram/ui/Cells/ChatMessageCell:measureTime	(Lorg/telegram/messenger/MessageObject;)V
    //   8207: iload 12
    //   8209: istore 14
    //   8211: iload 11
    //   8213: istore 8
    //   8215: iload 10
    //   8217: istore 9
    //   8219: aload_0
    //   8220: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8223: ldc_w 2522
    //   8226: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8229: isub
    //   8230: aload_0
    //   8231: getfield 1185	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   8234: iconst_0
    //   8235: invokevirtual 670	android/text/StaticLayout:getLineWidth	(I)F
    //   8238: f2d
    //   8239: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   8242: d2i
    //   8243: isub
    //   8244: aload_0
    //   8245: getfield 500	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   8248: if_icmpge +22 -> 8270
    //   8251: iload 11
    //   8253: ldc_w 738
    //   8256: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8259: iadd
    //   8260: istore 8
    //   8262: iload 10
    //   8264: istore 9
    //   8266: iload 12
    //   8268: istore 14
    //   8270: aload_0
    //   8271: aload_1
    //   8272: invokespecial 2526	org/telegram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/telegram/messenger/MessageObject;)V
    //   8275: aload_0
    //   8276: getfield 1869	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   8279: ifeq +3634 -> 11913
    //   8282: aload_0
    //   8283: aload_0
    //   8284: getfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8287: ldc_w 1527
    //   8290: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8293: iadd
    //   8294: putfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8297: aload_0
    //   8298: invokevirtual 541	org/telegram/ui/Cells/ChatMessageCell:invalidate	()V
    //   8301: aload_0
    //   8302: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   8305: iconst_0
    //   8306: ldc_w 624
    //   8309: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8312: aload_0
    //   8313: getfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8316: iadd
    //   8317: iload 9
    //   8319: iload 8
    //   8321: invokevirtual 1429	org/telegram/messenger/ImageReceiver:setImageCoords	(IIII)V
    //   8324: aload_0
    //   8325: ldc_w 392
    //   8328: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8331: iload 8
    //   8333: iadd
    //   8334: aload_0
    //   8335: getfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8338: iadd
    //   8339: iload 14
    //   8341: iadd
    //   8342: putfield 491	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   8345: goto -4398 -> 3947
    //   8348: iconst_0
    //   8349: istore 27
    //   8351: goto -494 -> 7857
    //   8354: iconst_0
    //   8355: istore 27
    //   8357: goto -482 -> 7875
    //   8360: iconst_0
    //   8361: istore 27
    //   8363: goto -407 -> 7956
    //   8366: ldc_w 979
    //   8369: fstore 6
    //   8371: goto -351 -> 8020
    //   8374: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   8377: getfield 1747	android/graphics/Point:x	I
    //   8380: istore 8
    //   8382: aload_0
    //   8383: getfield 1734	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   8386: ifeq +46 -> 8432
    //   8389: aload_1
    //   8390: invokevirtual 882	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   8393: ifeq +39 -> 8432
    //   8396: aload_1
    //   8397: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   8400: ifne +32 -> 8432
    //   8403: ldc_w 2693
    //   8406: fstore 6
    //   8408: aload_0
    //   8409: iload 8
    //   8411: fload 6
    //   8413: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8416: isub
    //   8417: ldc_w 2694
    //   8420: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8423: invokestatic 1077	java/lang/Math:min	(II)I
    //   8426: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8429: goto -388 -> 8041
    //   8432: ldc_w 979
    //   8435: fstore 6
    //   8437: goto -29 -> 8408
    //   8440: ldc_w 1570
    //   8443: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8446: istore 10
    //   8448: ldc_w 1570
    //   8451: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8454: istore 11
    //   8456: aload_1
    //   8457: getfield 641	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   8460: invokestatic 780	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   8463: ifeq +21 -> 8484
    //   8466: ldc_w 1560
    //   8469: fstore 6
    //   8471: iload 8
    //   8473: fload 6
    //   8475: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8478: iadd
    //   8479: istore 8
    //   8481: goto -348 -> 8133
    //   8484: ldc_w 425
    //   8487: fstore 6
    //   8489: goto -18 -> 8471
    //   8492: aload_1
    //   8493: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   8496: iconst_4
    //   8497: if_icmpne +600 -> 9097
    //   8500: aload_1
    //   8501: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8504: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   8507: getfield 1766	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   8510: getfield 1772	org/telegram/tgnet/TLRPC$GeoPoint:lat	D
    //   8513: dstore_2
    //   8514: aload_1
    //   8515: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8518: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   8521: getfield 1766	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   8524: getfield 1775	org/telegram/tgnet/TLRPC$GeoPoint:_long	D
    //   8527: dstore 4
    //   8529: aload_1
    //   8530: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8533: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   8536: getfield 2721	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   8539: ifnull +443 -> 8982
    //   8542: aload_1
    //   8543: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8546: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   8549: getfield 2721	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   8552: invokevirtual 937	java/lang/String:length	()I
    //   8555: ifle +427 -> 8982
    //   8558: invokestatic 1732	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   8561: ifeq +347 -> 8908
    //   8564: invokestatic 1737	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   8567: istore 8
    //   8569: aload_0
    //   8570: getfield 1734	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   8573: ifeq +327 -> 8900
    //   8576: aload_1
    //   8577: invokevirtual 882	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   8580: ifeq +320 -> 8900
    //   8583: aload_1
    //   8584: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   8587: ifne +313 -> 8900
    //   8590: ldc_w 2693
    //   8593: fstore 6
    //   8595: aload_0
    //   8596: iload 8
    //   8598: fload 6
    //   8600: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8603: isub
    //   8604: ldc_w 2694
    //   8607: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8610: invokestatic 1077	java/lang/Math:min	(II)I
    //   8613: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8616: aload_0
    //   8617: aload_1
    //   8618: invokespecial 2521	org/telegram/ui/Cells/ChatMessageCell:checkNeedDrawShareButton	(Lorg/telegram/messenger/MessageObject;)Z
    //   8621: ifeq +18 -> 8639
    //   8624: aload_0
    //   8625: aload_0
    //   8626: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8629: ldc_w 960
    //   8632: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8635: isub
    //   8636: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8639: aload_0
    //   8640: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8643: ldc_w 2722
    //   8646: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8649: isub
    //   8650: istore 8
    //   8652: aload_0
    //   8653: aload_1
    //   8654: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8657: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   8660: getfield 2721	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   8663: getstatic 361	org/telegram/ui/Cells/ChatMessageCell:locationTitlePaint	Landroid/text/TextPaint;
    //   8666: iload 8
    //   8668: getstatic 1131	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   8671: fconst_1
    //   8672: fconst_0
    //   8673: iconst_0
    //   8674: getstatic 1121	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   8677: iload 8
    //   8679: iconst_2
    //   8680: invokestatic 1233	org/telegram/ui/Components/StaticLayoutEx:createStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZLandroid/text/TextUtils$TruncateAt;II)Landroid/text/StaticLayout;
    //   8683: putfield 1235	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   8686: aload_0
    //   8687: getfield 1235	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   8690: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   8693: istore 9
    //   8695: aload_1
    //   8696: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8699: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   8702: getfield 2725	org/telegram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   8705: ifnull +269 -> 8974
    //   8708: aload_1
    //   8709: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8712: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   8715: getfield 2725	org/telegram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   8718: invokevirtual 937	java/lang/String:length	()I
    //   8721: ifle +253 -> 8974
    //   8724: aload_0
    //   8725: aload_1
    //   8726: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8729: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   8732: getfield 2725	org/telegram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   8735: getstatic 363	org/telegram/ui/Cells/ChatMessageCell:locationAddressPaint	Landroid/text/TextPaint;
    //   8738: iload 8
    //   8740: getstatic 1131	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   8743: fconst_1
    //   8744: fconst_0
    //   8745: iconst_0
    //   8746: getstatic 1121	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   8749: iload 8
    //   8751: iconst_3
    //   8752: iconst_3
    //   8753: iload 9
    //   8755: isub
    //   8756: invokestatic 1077	java/lang/Math:min	(II)I
    //   8759: invokestatic 1233	org/telegram/ui/Components/StaticLayoutEx:createStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZLandroid/text/TextUtils$TruncateAt;II)Landroid/text/StaticLayout;
    //   8762: putfield 1185	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   8765: aload_0
    //   8766: iconst_0
    //   8767: putfield 623	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   8770: aload_0
    //   8771: iload 8
    //   8773: putfield 1069	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   8776: ldc_w 1108
    //   8779: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8782: istore 9
    //   8784: ldc_w 1108
    //   8787: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8790: istore 8
    //   8792: aload_0
    //   8793: getstatic 1781	java/util/Locale:US	Ljava/util/Locale;
    //   8796: ldc_w 2727
    //   8799: iconst_5
    //   8800: anewarray 1152	java/lang/Object
    //   8803: dup
    //   8804: iconst_0
    //   8805: dload_2
    //   8806: invokestatic 1788	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   8809: aastore
    //   8810: dup
    //   8811: iconst_1
    //   8812: dload 4
    //   8814: invokestatic 1788	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   8817: aastore
    //   8818: dup
    //   8819: iconst_2
    //   8820: iconst_2
    //   8821: getstatic 1791	org/telegram/messenger/AndroidUtilities:density	F
    //   8824: f2d
    //   8825: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   8828: d2i
    //   8829: invokestatic 1077	java/lang/Math:min	(II)I
    //   8832: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   8835: aastore
    //   8836: dup
    //   8837: iconst_3
    //   8838: dload_2
    //   8839: invokestatic 1788	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   8842: aastore
    //   8843: dup
    //   8844: iconst_4
    //   8845: dload 4
    //   8847: invokestatic 1788	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   8850: aastore
    //   8851: invokestatic 1794	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   8854: putfield 1762	org/telegram/ui/Cells/ChatMessageCell:currentUrl	Ljava/lang/String;
    //   8857: aload_0
    //   8858: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   8861: astore 29
    //   8863: aload_0
    //   8864: getfield 1762	org/telegram/ui/Cells/ChatMessageCell:currentUrl	Ljava/lang/String;
    //   8867: astore 30
    //   8869: aload_1
    //   8870: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   8873: ifeq +216 -> 9089
    //   8876: getstatic 2730	org/telegram/ui/ActionBar/Theme:geoOutDrawable	Landroid/graphics/drawable/Drawable;
    //   8879: astore 28
    //   8881: aload 29
    //   8883: aload 30
    //   8885: aconst_null
    //   8886: aload 28
    //   8888: aconst_null
    //   8889: iconst_0
    //   8890: invokevirtual 2733	org/telegram/messenger/ImageReceiver:setImage	(Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Ljava/lang/String;I)V
    //   8893: iload 12
    //   8895: istore 14
    //   8897: goto -627 -> 8270
    //   8900: ldc_w 979
    //   8903: fstore 6
    //   8905: goto -310 -> 8595
    //   8908: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   8911: getfield 1747	android/graphics/Point:x	I
    //   8914: istore 8
    //   8916: aload_0
    //   8917: getfield 1734	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   8920: ifeq +46 -> 8966
    //   8923: aload_1
    //   8924: invokevirtual 882	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   8927: ifeq +39 -> 8966
    //   8930: aload_1
    //   8931: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   8934: ifne +32 -> 8966
    //   8937: ldc_w 2693
    //   8940: fstore 6
    //   8942: aload_0
    //   8943: iload 8
    //   8945: fload 6
    //   8947: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8950: isub
    //   8951: ldc_w 2694
    //   8954: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8957: invokestatic 1077	java/lang/Math:min	(II)I
    //   8960: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8963: goto -347 -> 8616
    //   8966: ldc_w 979
    //   8969: fstore 6
    //   8971: goto -29 -> 8942
    //   8974: aload_0
    //   8975: aconst_null
    //   8976: putfield 1185	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   8979: goto -214 -> 8765
    //   8982: aload_0
    //   8983: ldc_w 2734
    //   8986: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8989: putfield 1069	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   8992: ldc_w 1660
    //   8995: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8998: istore 9
    //   9000: ldc_w 1995
    //   9003: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9006: istore 8
    //   9008: aload_0
    //   9009: ldc_w 300
    //   9012: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9015: iload 9
    //   9017: iadd
    //   9018: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   9021: aload_0
    //   9022: getstatic 1781	java/util/Locale:US	Ljava/util/Locale;
    //   9025: ldc_w 2736
    //   9028: iconst_5
    //   9029: anewarray 1152	java/lang/Object
    //   9032: dup
    //   9033: iconst_0
    //   9034: dload_2
    //   9035: invokestatic 1788	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   9038: aastore
    //   9039: dup
    //   9040: iconst_1
    //   9041: dload 4
    //   9043: invokestatic 1788	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   9046: aastore
    //   9047: dup
    //   9048: iconst_2
    //   9049: iconst_2
    //   9050: getstatic 1791	org/telegram/messenger/AndroidUtilities:density	F
    //   9053: f2d
    //   9054: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   9057: d2i
    //   9058: invokestatic 1077	java/lang/Math:min	(II)I
    //   9061: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   9064: aastore
    //   9065: dup
    //   9066: iconst_3
    //   9067: dload_2
    //   9068: invokestatic 1788	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   9071: aastore
    //   9072: dup
    //   9073: iconst_4
    //   9074: dload 4
    //   9076: invokestatic 1788	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   9079: aastore
    //   9080: invokestatic 1794	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   9083: putfield 1762	org/telegram/ui/Cells/ChatMessageCell:currentUrl	Ljava/lang/String;
    //   9086: goto -229 -> 8857
    //   9089: getstatic 2739	org/telegram/ui/ActionBar/Theme:geoInDrawable	Landroid/graphics/drawable/Drawable;
    //   9092: astore 28
    //   9094: goto -213 -> 8881
    //   9097: aload_1
    //   9098: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   9101: bipush 13
    //   9103: if_icmpne +542 -> 9645
    //   9106: aload_0
    //   9107: iconst_0
    //   9108: putfield 282	org/telegram/ui/Cells/ChatMessageCell:drawBackground	Z
    //   9111: iconst_0
    //   9112: istore 10
    //   9114: iload 13
    //   9116: istore 9
    //   9118: iload 11
    //   9120: istore 8
    //   9122: iload 10
    //   9124: aload_1
    //   9125: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9128: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   9131: getfield 1044	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   9134: getfield 1053	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   9137: invokevirtual 591	java/util/ArrayList:size	()I
    //   9140: if_icmpge +48 -> 9188
    //   9143: aload_1
    //   9144: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9147: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   9150: getfield 1044	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   9153: getfield 1053	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   9156: iload 10
    //   9158: invokevirtual 595	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   9161: checkcast 1055	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   9164: astore 28
    //   9166: aload 28
    //   9168: instanceof 2575
    //   9171: ifeq +281 -> 9452
    //   9174: aload 28
    //   9176: getfield 2576	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   9179: istore 8
    //   9181: aload 28
    //   9183: getfield 2577	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   9186: istore 9
    //   9188: invokestatic 1732	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   9191: ifeq +270 -> 9461
    //   9194: invokestatic 1737	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   9197: i2f
    //   9198: ldc_w 2740
    //   9201: fmul
    //   9202: fstore 6
    //   9204: fload 6
    //   9206: fstore 7
    //   9208: iload 9
    //   9210: istore 10
    //   9212: iload 8
    //   9214: istore 9
    //   9216: iload 8
    //   9218: ifne +19 -> 9237
    //   9221: fload 7
    //   9223: f2i
    //   9224: istore 10
    //   9226: iload 10
    //   9228: ldc_w 1995
    //   9231: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9234: iadd
    //   9235: istore 9
    //   9237: iload 10
    //   9239: i2f
    //   9240: fload 6
    //   9242: iload 9
    //   9244: i2f
    //   9245: fdiv
    //   9246: fmul
    //   9247: f2i
    //   9248: istore 8
    //   9250: fload 6
    //   9252: f2i
    //   9253: istore 9
    //   9255: iload 8
    //   9257: istore 11
    //   9259: iload 9
    //   9261: istore 10
    //   9263: iload 8
    //   9265: i2f
    //   9266: fload 7
    //   9268: fcmpl
    //   9269: ifle +21 -> 9290
    //   9272: iload 9
    //   9274: i2f
    //   9275: fload 7
    //   9277: iload 8
    //   9279: i2f
    //   9280: fdiv
    //   9281: fmul
    //   9282: f2i
    //   9283: istore 10
    //   9285: fload 7
    //   9287: f2i
    //   9288: istore 11
    //   9290: aload_0
    //   9291: bipush 6
    //   9293: putfield 504	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   9296: aload_0
    //   9297: iload 10
    //   9299: ldc_w 392
    //   9302: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9305: isub
    //   9306: putfield 1069	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   9309: aload_0
    //   9310: ldc_w 300
    //   9313: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9316: iload 10
    //   9318: iadd
    //   9319: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   9322: aload_0
    //   9323: aload_1
    //   9324: getfield 1246	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   9327: bipush 80
    //   9329: invokestatic 1253	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9332: putfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9335: aload_1
    //   9336: getfield 2118	org/telegram/messenger/MessageObject:attachPathExists	Z
    //   9339: ifeq +157 -> 9496
    //   9342: aload_0
    //   9343: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   9346: astore 29
    //   9348: aload_1
    //   9349: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9352: getfield 2743	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   9355: astore 30
    //   9357: getstatic 1781	java/util/Locale:US	Ljava/util/Locale;
    //   9360: ldc_w 2585
    //   9363: iconst_2
    //   9364: anewarray 1152	java/lang/Object
    //   9367: dup
    //   9368: iconst_0
    //   9369: iload 10
    //   9371: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   9374: aastore
    //   9375: dup
    //   9376: iconst_1
    //   9377: iload 11
    //   9379: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   9382: aastore
    //   9383: invokestatic 1794	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   9386: astore 31
    //   9388: aload_0
    //   9389: getfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9392: ifnull +98 -> 9490
    //   9395: aload_0
    //   9396: getfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9399: getfield 1207	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   9402: astore 28
    //   9404: aload 29
    //   9406: aconst_null
    //   9407: aload 30
    //   9409: aload 31
    //   9411: aconst_null
    //   9412: aload 28
    //   9414: ldc_w 2589
    //   9417: aload_1
    //   9418: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9421: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   9424: getfield 1044	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   9427: getfield 1174	org/telegram/tgnet/TLRPC$Document:size	I
    //   9430: ldc_w 2591
    //   9433: iconst_1
    //   9434: invokevirtual 1272	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   9437: iload 12
    //   9439: istore 14
    //   9441: iload 11
    //   9443: istore 8
    //   9445: iload 10
    //   9447: istore 9
    //   9449: goto -1179 -> 8270
    //   9452: iload 10
    //   9454: iconst_1
    //   9455: iadd
    //   9456: istore 10
    //   9458: goto -344 -> 9114
    //   9461: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   9464: getfield 1747	android/graphics/Point:x	I
    //   9467: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   9470: getfield 1750	android/graphics/Point:y	I
    //   9473: invokestatic 1077	java/lang/Math:min	(II)I
    //   9476: i2f
    //   9477: ldc_w 2583
    //   9480: fmul
    //   9481: fstore 6
    //   9483: fload 6
    //   9485: fstore 7
    //   9487: goto -279 -> 9208
    //   9490: aconst_null
    //   9491: astore 28
    //   9493: goto -89 -> 9404
    //   9496: iload 12
    //   9498: istore 14
    //   9500: iload 11
    //   9502: istore 8
    //   9504: iload 10
    //   9506: istore 9
    //   9508: aload_1
    //   9509: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9512: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   9515: getfield 1044	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   9518: getfield 2745	org/telegram/tgnet/TLRPC$Document:id	J
    //   9521: lconst_0
    //   9522: lcmp
    //   9523: ifeq -1253 -> 8270
    //   9526: aload_0
    //   9527: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   9530: astore 29
    //   9532: aload_1
    //   9533: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9536: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   9539: getfield 1044	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   9542: astore 30
    //   9544: getstatic 1781	java/util/Locale:US	Ljava/util/Locale;
    //   9547: ldc_w 2585
    //   9550: iconst_2
    //   9551: anewarray 1152	java/lang/Object
    //   9554: dup
    //   9555: iconst_0
    //   9556: iload 10
    //   9558: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   9561: aastore
    //   9562: dup
    //   9563: iconst_1
    //   9564: iload 11
    //   9566: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   9569: aastore
    //   9570: invokestatic 1794	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   9573: astore 31
    //   9575: aload_0
    //   9576: getfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9579: ifnull +60 -> 9639
    //   9582: aload_0
    //   9583: getfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9586: getfield 1207	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   9589: astore 28
    //   9591: aload 29
    //   9593: aload 30
    //   9595: aconst_null
    //   9596: aload 31
    //   9598: aconst_null
    //   9599: aload 28
    //   9601: ldc_w 2589
    //   9604: aload_1
    //   9605: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9608: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   9611: getfield 1044	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   9614: getfield 1174	org/telegram/tgnet/TLRPC$Document:size	I
    //   9617: ldc_w 2591
    //   9620: iconst_1
    //   9621: invokevirtual 1272	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   9624: iload 12
    //   9626: istore 14
    //   9628: iload 11
    //   9630: istore 8
    //   9632: iload 10
    //   9634: istore 9
    //   9636: goto -1366 -> 8270
    //   9639: aconst_null
    //   9640: astore 28
    //   9642: goto -51 -> 9591
    //   9645: invokestatic 1732	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   9648: ifeq +1284 -> 10932
    //   9651: invokestatic 1737	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   9654: i2f
    //   9655: ldc_w 2746
    //   9658: fmul
    //   9659: f2i
    //   9660: istore 8
    //   9662: iload 8
    //   9664: istore 10
    //   9666: iload 8
    //   9668: ldc_w 1995
    //   9671: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9674: iadd
    //   9675: istore 11
    //   9677: iload 10
    //   9679: istore 13
    //   9681: iload 8
    //   9683: istore 9
    //   9685: aload_0
    //   9686: aload_1
    //   9687: invokespecial 2521	org/telegram/ui/Cells/ChatMessageCell:checkNeedDrawShareButton	(Lorg/telegram/messenger/MessageObject;)Z
    //   9690: ifeq +25 -> 9715
    //   9693: iload 10
    //   9695: ldc_w 960
    //   9698: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9701: isub
    //   9702: istore 13
    //   9704: iload 8
    //   9706: ldc_w 960
    //   9709: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9712: isub
    //   9713: istore 9
    //   9715: iload 9
    //   9717: istore 14
    //   9719: iload 9
    //   9721: invokestatic 1249	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   9724: if_icmple +8 -> 9732
    //   9727: invokestatic 1249	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   9730: istore 14
    //   9732: iload 11
    //   9734: istore 12
    //   9736: iload 11
    //   9738: invokestatic 1249	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   9741: if_icmple +8 -> 9749
    //   9744: invokestatic 1249	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   9747: istore 12
    //   9749: aload_1
    //   9750: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   9753: iconst_1
    //   9754: if_icmpne +1208 -> 10962
    //   9757: aload_1
    //   9758: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9761: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   9764: getfield 2747	org/telegram/tgnet/TLRPC$MessageMedia:photo	Lorg/telegram/tgnet/TLRPC$Photo;
    //   9767: getfield 2752	org/telegram/tgnet/TLRPC$Photo:sizes	Ljava/util/ArrayList;
    //   9770: invokestatic 1249	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   9773: invokestatic 1253	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9776: astore 28
    //   9778: new 1163	java/lang/StringBuilder
    //   9781: dup
    //   9782: invokespecial 1164	java/lang/StringBuilder:<init>	()V
    //   9785: ldc_w 2754
    //   9788: invokevirtual 1170	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   9791: aload 28
    //   9793: getfield 1294	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   9796: i2l
    //   9797: invokestatic 1178	org/telegram/messenger/AndroidUtilities:formatFileSize	(J)Ljava/lang/String;
    //   9800: invokevirtual 1170	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   9803: invokevirtual 1181	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   9806: astore 28
    //   9808: aload_0
    //   9809: getstatic 294	org/telegram/ui/Cells/ChatMessageCell:infoPaint	Landroid/text/TextPaint;
    //   9812: aload 28
    //   9814: invokevirtual 1067	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   9817: f2d
    //   9818: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   9821: d2i
    //   9822: putfield 1183	org/telegram/ui/Cells/ChatMessageCell:infoWidth	I
    //   9825: aload_0
    //   9826: new 286	android/text/StaticLayout
    //   9829: dup
    //   9830: aload 28
    //   9832: getstatic 294	org/telegram/ui/Cells/ChatMessageCell:infoPaint	Landroid/text/TextPaint;
    //   9835: aload_0
    //   9836: getfield 1183	org/telegram/ui/Cells/ChatMessageCell:infoWidth	I
    //   9839: getstatic 1131	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   9842: fconst_1
    //   9843: fconst_0
    //   9844: iconst_0
    //   9845: invokespecial 1134	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   9848: putfield 1185	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   9851: aload_0
    //   9852: aload_1
    //   9853: invokespecial 1509	org/telegram/ui/Cells/ChatMessageCell:updateSecretTimeText	(Lorg/telegram/messenger/MessageObject;)V
    //   9856: aload_0
    //   9857: aload_1
    //   9858: getfield 1246	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   9861: bipush 80
    //   9863: invokestatic 1253	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9866: putfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9869: aload_1
    //   9870: getfield 641	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   9873: ifnull +8 -> 9881
    //   9876: aload_0
    //   9877: iconst_0
    //   9878: putfield 623	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   9881: aload_0
    //   9882: aload_1
    //   9883: getfield 1246	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   9886: invokestatic 1249	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   9889: invokestatic 1253	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9892: putfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9895: iconst_0
    //   9896: istore 9
    //   9898: iconst_0
    //   9899: istore 8
    //   9901: aload_0
    //   9902: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9905: ifnull +19 -> 9924
    //   9908: aload_0
    //   9909: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9912: aload_0
    //   9913: getfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9916: if_acmpne +8 -> 9924
    //   9919: aload_0
    //   9920: aconst_null
    //   9921: putfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9924: aload_0
    //   9925: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9928: ifnull +112 -> 10040
    //   9931: aload_0
    //   9932: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9935: getfield 2570	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   9938: i2f
    //   9939: iload 14
    //   9941: i2f
    //   9942: fdiv
    //   9943: fstore 6
    //   9945: aload_0
    //   9946: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9949: getfield 2570	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   9952: i2f
    //   9953: fload 6
    //   9955: fdiv
    //   9956: f2i
    //   9957: istore 9
    //   9959: aload_0
    //   9960: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9963: getfield 2573	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   9966: i2f
    //   9967: fload 6
    //   9969: fdiv
    //   9970: f2i
    //   9971: istore 8
    //   9973: iload 9
    //   9975: istore 10
    //   9977: iload 9
    //   9979: ifne +11 -> 9990
    //   9982: ldc_w 2578
    //   9985: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9988: istore 10
    //   9990: iload 8
    //   9992: istore 11
    //   9994: iload 8
    //   9996: ifne +11 -> 10007
    //   9999: ldc_w 2578
    //   10002: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10005: istore 11
    //   10007: iload 11
    //   10009: iload 12
    //   10011: if_icmple +1110 -> 11121
    //   10014: iload 11
    //   10016: i2f
    //   10017: fstore 6
    //   10019: iload 12
    //   10021: istore 8
    //   10023: fload 6
    //   10025: iload 8
    //   10027: i2f
    //   10028: fdiv
    //   10029: fstore 6
    //   10031: iload 10
    //   10033: i2f
    //   10034: fload 6
    //   10036: fdiv
    //   10037: f2i
    //   10038: istore 9
    //   10040: iload 9
    //   10042: ifeq +16 -> 10058
    //   10045: iload 8
    //   10047: istore 10
    //   10049: iload 9
    //   10051: istore 11
    //   10053: iload 8
    //   10055: ifne +160 -> 10215
    //   10058: iload 8
    //   10060: istore 10
    //   10062: iload 9
    //   10064: istore 11
    //   10066: aload_1
    //   10067: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   10070: bipush 8
    //   10072: if_icmpne +143 -> 10215
    //   10075: iconst_0
    //   10076: istore 15
    //   10078: iload 8
    //   10080: istore 10
    //   10082: iload 9
    //   10084: istore 11
    //   10086: iload 15
    //   10088: aload_1
    //   10089: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   10092: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   10095: getfield 1044	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   10098: getfield 1053	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   10101: invokevirtual 591	java/util/ArrayList:size	()I
    //   10104: if_icmpge +111 -> 10215
    //   10107: aload_1
    //   10108: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   10111: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   10114: getfield 1044	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   10117: getfield 1053	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   10120: iload 15
    //   10122: invokevirtual 595	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   10125: checkcast 1055	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   10128: astore 28
    //   10130: aload 28
    //   10132: instanceof 2575
    //   10135: ifne +11 -> 10146
    //   10138: aload 28
    //   10140: instanceof 1161
    //   10143: ifeq +1148 -> 11291
    //   10146: aload 28
    //   10148: getfield 2576	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   10151: i2f
    //   10152: iload 14
    //   10154: i2f
    //   10155: fdiv
    //   10156: fstore 6
    //   10158: aload 28
    //   10160: getfield 2576	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   10163: i2f
    //   10164: fload 6
    //   10166: fdiv
    //   10167: f2i
    //   10168: istore 8
    //   10170: aload 28
    //   10172: getfield 2577	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   10175: i2f
    //   10176: fload 6
    //   10178: fdiv
    //   10179: f2i
    //   10180: istore 9
    //   10182: iload 9
    //   10184: iload 12
    //   10186: if_icmple +1023 -> 11209
    //   10189: iload 9
    //   10191: i2f
    //   10192: fstore 6
    //   10194: iload 12
    //   10196: istore 10
    //   10198: fload 6
    //   10200: iload 10
    //   10202: i2f
    //   10203: fdiv
    //   10204: fstore 6
    //   10206: iload 8
    //   10208: i2f
    //   10209: fload 6
    //   10211: fdiv
    //   10212: f2i
    //   10213: istore 11
    //   10215: iload 11
    //   10217: ifeq +12 -> 10229
    //   10220: iload 10
    //   10222: istore 9
    //   10224: iload 10
    //   10226: ifne +15 -> 10241
    //   10229: ldc_w 2578
    //   10232: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10235: istore 9
    //   10237: iload 9
    //   10239: istore 11
    //   10241: iload 11
    //   10243: istore 8
    //   10245: aload_1
    //   10246: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   10249: iconst_3
    //   10250: if_icmpne +36 -> 10286
    //   10253: iload 11
    //   10255: istore 8
    //   10257: iload 11
    //   10259: aload_0
    //   10260: getfield 1183	org/telegram/ui/Cells/ChatMessageCell:infoWidth	I
    //   10263: ldc_w 1602
    //   10266: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10269: iadd
    //   10270: if_icmpge +16 -> 10286
    //   10273: aload_0
    //   10274: getfield 1183	org/telegram/ui/Cells/ChatMessageCell:infoWidth	I
    //   10277: ldc_w 1602
    //   10280: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10283: iadd
    //   10284: istore 8
    //   10286: aload_0
    //   10287: iload 13
    //   10289: ldc_w 392
    //   10292: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10295: isub
    //   10296: putfield 1069	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   10299: aload_0
    //   10300: aload_1
    //   10301: invokespecial 1073	org/telegram/ui/Cells/ChatMessageCell:measureTime	(Lorg/telegram/messenger/MessageObject;)V
    //   10304: aload_0
    //   10305: getfield 500	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   10308: istore 11
    //   10310: aload_1
    //   10311: invokevirtual 582	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   10314: ifeq +986 -> 11300
    //   10317: bipush 20
    //   10319: istore 10
    //   10321: iload 11
    //   10323: iload 10
    //   10325: bipush 14
    //   10327: iadd
    //   10328: i2f
    //   10329: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10332: iadd
    //   10333: istore 15
    //   10335: iload 8
    //   10337: istore 10
    //   10339: iload 8
    //   10341: iload 15
    //   10343: if_icmpge +7 -> 10350
    //   10346: iload 15
    //   10348: istore 10
    //   10350: aload_1
    //   10351: invokevirtual 975	org/telegram/messenger/MessageObject:isSecretPhoto	()Z
    //   10354: ifeq +24 -> 10378
    //   10357: invokestatic 1732	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   10360: ifeq +946 -> 11306
    //   10363: invokestatic 1737	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   10366: i2f
    //   10367: ldc_w 2583
    //   10370: fmul
    //   10371: f2i
    //   10372: istore 9
    //   10374: iload 9
    //   10376: istore 10
    //   10378: iload 10
    //   10380: istore 12
    //   10382: iload 9
    //   10384: istore 13
    //   10386: aload_0
    //   10387: ldc_w 300
    //   10390: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10393: iload 10
    //   10395: iadd
    //   10396: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   10399: aload_0
    //   10400: getfield 623	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   10403: ifne +18 -> 10421
    //   10406: aload_0
    //   10407: aload_0
    //   10408: getfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   10411: ldc_w 1568
    //   10414: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10417: iadd
    //   10418: putfield 284	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   10421: iload 16
    //   10423: istore 8
    //   10425: aload_1
    //   10426: getfield 641	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   10429: ifnull +186 -> 10615
    //   10432: iload 17
    //   10434: istore 11
    //   10436: aload_0
    //   10437: new 286	android/text/StaticLayout
    //   10440: dup
    //   10441: aload_1
    //   10442: getfield 641	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   10445: invokestatic 2187	org/telegram/messenger/MessageObject:getTextPaint	()Landroid/text/TextPaint;
    //   10448: iload 12
    //   10450: ldc_w 588
    //   10453: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10456: isub
    //   10457: getstatic 1131	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   10460: fconst_1
    //   10461: fconst_0
    //   10462: iconst_0
    //   10463: invokespecial 1134	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   10466: putfield 645	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   10469: iload 16
    //   10471: istore 8
    //   10473: iload 17
    //   10475: istore 11
    //   10477: aload_0
    //   10478: getfield 645	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   10481: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   10484: ifle +131 -> 10615
    //   10487: iload 17
    //   10489: istore 11
    //   10491: aload_0
    //   10492: aload_0
    //   10493: getfield 645	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   10496: invokevirtual 2259	android/text/StaticLayout:getHeight	()I
    //   10499: putfield 655	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   10502: iload 17
    //   10504: istore 11
    //   10506: iconst_0
    //   10507: aload_0
    //   10508: getfield 655	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   10511: ldc_w 1568
    //   10514: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10517: iadd
    //   10518: iadd
    //   10519: istore 14
    //   10521: iload 14
    //   10523: istore 11
    //   10525: aload_0
    //   10526: getfield 645	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   10529: aload_0
    //   10530: getfield 645	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   10533: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   10536: iconst_1
    //   10537: isub
    //   10538: invokevirtual 670	android/text/StaticLayout:getLineWidth	(I)F
    //   10541: fstore 6
    //   10543: iload 14
    //   10545: istore 11
    //   10547: aload_0
    //   10548: getfield 645	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   10551: aload_0
    //   10552: getfield 645	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   10555: invokevirtual 1139	android/text/StaticLayout:getLineCount	()I
    //   10558: iconst_1
    //   10559: isub
    //   10560: invokevirtual 667	android/text/StaticLayout:getLineLeft	(I)F
    //   10563: fstore 7
    //   10565: iload 14
    //   10567: istore 8
    //   10569: iload 14
    //   10571: istore 11
    //   10573: iload 12
    //   10575: ldc_w 738
    //   10578: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10581: isub
    //   10582: i2f
    //   10583: fload 6
    //   10585: fload 7
    //   10587: fadd
    //   10588: fsub
    //   10589: iload 15
    //   10591: i2f
    //   10592: fcmpg
    //   10593: ifge +22 -> 10615
    //   10596: iload 14
    //   10598: istore 11
    //   10600: ldc_w 392
    //   10603: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10606: istore 8
    //   10608: iload 14
    //   10610: iload 8
    //   10612: iadd
    //   10613: istore 8
    //   10615: aload_0
    //   10616: getstatic 1781	java/util/Locale:US	Ljava/util/Locale;
    //   10619: ldc_w 2585
    //   10622: iconst_2
    //   10623: anewarray 1152	java/lang/Object
    //   10626: dup
    //   10627: iconst_0
    //   10628: iload 10
    //   10630: i2f
    //   10631: getstatic 1791	org/telegram/messenger/AndroidUtilities:density	F
    //   10634: fdiv
    //   10635: f2i
    //   10636: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   10639: aastore
    //   10640: dup
    //   10641: iconst_1
    //   10642: iload 9
    //   10644: i2f
    //   10645: getstatic 1791	org/telegram/messenger/AndroidUtilities:density	F
    //   10648: fdiv
    //   10649: f2i
    //   10650: invokestatic 896	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   10653: aastore
    //   10654: invokestatic 1794	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   10657: putfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   10660: aload_1
    //   10661: getfield 1246	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   10664: ifnull +14 -> 10678
    //   10667: aload_1
    //   10668: getfield 1246	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   10671: invokevirtual 591	java/util/ArrayList:size	()I
    //   10674: iconst_1
    //   10675: if_icmpgt +20 -> 10695
    //   10678: aload_1
    //   10679: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   10682: iconst_3
    //   10683: if_icmpeq +12 -> 10695
    //   10686: aload_1
    //   10687: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   10690: bipush 8
    //   10692: if_icmpne +37 -> 10729
    //   10695: aload_1
    //   10696: invokevirtual 975	org/telegram/messenger/MessageObject:isSecretPhoto	()Z
    //   10699: ifeq +654 -> 11353
    //   10702: aload_0
    //   10703: new 1163	java/lang/StringBuilder
    //   10706: dup
    //   10707: invokespecial 1164	java/lang/StringBuilder:<init>	()V
    //   10710: aload_0
    //   10711: getfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   10714: invokevirtual 1170	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   10717: ldc_w 2756
    //   10720: invokevirtual 1170	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   10723: invokevirtual 1181	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   10726: putfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   10729: iconst_0
    //   10730: istore 9
    //   10732: aload_1
    //   10733: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   10736: iconst_3
    //   10737: if_icmpeq +12 -> 10749
    //   10740: aload_1
    //   10741: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   10744: bipush 8
    //   10746: if_icmpne +6 -> 10752
    //   10749: iconst_1
    //   10750: istore 9
    //   10752: aload_0
    //   10753: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10756: ifnull +26 -> 10782
    //   10759: iload 9
    //   10761: ifne +21 -> 10782
    //   10764: aload_0
    //   10765: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10768: getfield 1294	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   10771: ifne +11 -> 10782
    //   10774: aload_0
    //   10775: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10778: iconst_m1
    //   10779: putfield 1294	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   10782: aload_1
    //   10783: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   10786: iconst_1
    //   10787: if_icmpne +722 -> 11509
    //   10790: aload_0
    //   10791: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10794: ifnull +689 -> 11483
    //   10797: iconst_1
    //   10798: istore 10
    //   10800: aload_0
    //   10801: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10804: invokestatic 2686	org/telegram/messenger/FileLoader:getAttachFileName	(Lorg/telegram/tgnet/TLObject;)Ljava/lang/String;
    //   10807: astore 28
    //   10809: aload_1
    //   10810: getfield 2115	org/telegram/messenger/MessageObject:mediaExists	Z
    //   10813: ifeq +570 -> 11383
    //   10816: invokestatic 441	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   10819: aload_0
    //   10820: invokevirtual 2141	org/telegram/messenger/MediaController:removeLoadingFileObserver	(Lorg/telegram/messenger/MediaController$FileDownloadProgressListener;)V
    //   10823: iload 10
    //   10825: ifne +24 -> 10849
    //   10828: invokestatic 441	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   10831: iconst_1
    //   10832: invokevirtual 2689	org/telegram/messenger/MediaController:canDownloadMedia	(I)Z
    //   10835: ifne +14 -> 10849
    //   10838: invokestatic 1300	org/telegram/messenger/FileLoader:getInstance	()Lorg/telegram/messenger/FileLoader;
    //   10841: aload 28
    //   10843: invokevirtual 2692	org/telegram/messenger/FileLoader:isLoadingFile	(Ljava/lang/String;)Z
    //   10846: ifeq +561 -> 11407
    //   10849: aload_0
    //   10850: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   10853: astore 29
    //   10855: aload_0
    //   10856: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10859: getfield 1207	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   10862: astore 30
    //   10864: aload_0
    //   10865: getfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   10868: astore 31
    //   10870: aload_0
    //   10871: getfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10874: ifnull +515 -> 11389
    //   10877: aload_0
    //   10878: getfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10881: getfield 1207	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   10884: astore 28
    //   10886: aload_0
    //   10887: getfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   10890: astore 32
    //   10892: iload 9
    //   10894: ifeq +501 -> 11395
    //   10897: iconst_0
    //   10898: istore 9
    //   10900: aload 29
    //   10902: aload 30
    //   10904: aload 31
    //   10906: aload 28
    //   10908: aload 32
    //   10910: iload 9
    //   10912: aconst_null
    //   10913: iconst_0
    //   10914: invokevirtual 1297	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   10917: iload 8
    //   10919: istore 14
    //   10921: iload 13
    //   10923: istore 8
    //   10925: iload 12
    //   10927: istore 9
    //   10929: goto -2659 -> 8270
    //   10932: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   10935: getfield 1747	android/graphics/Point:x	I
    //   10938: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   10941: getfield 1750	android/graphics/Point:y	I
    //   10944: invokestatic 1077	java/lang/Math:min	(II)I
    //   10947: i2f
    //   10948: ldc_w 2746
    //   10951: fmul
    //   10952: f2i
    //   10953: istore 8
    //   10955: iload 8
    //   10957: istore 10
    //   10959: goto -1293 -> 9666
    //   10962: aload_1
    //   10963: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   10966: iconst_3
    //   10967: if_icmpne +37 -> 11004
    //   10970: aload_0
    //   10971: iconst_0
    //   10972: aload_1
    //   10973: invokespecial 2671	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   10976: pop
    //   10977: aload_0
    //   10978: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   10981: iconst_1
    //   10982: invokevirtual 1258	org/telegram/messenger/ImageReceiver:setNeedsQualityThumb	(Z)V
    //   10985: aload_0
    //   10986: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   10989: iconst_1
    //   10990: invokevirtual 1261	org/telegram/messenger/ImageReceiver:setShouldGenerateQualityThumb	(Z)V
    //   10993: aload_0
    //   10994: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   10997: aload_1
    //   10998: invokevirtual 1264	org/telegram/messenger/ImageReceiver:setParentMessageObject	(Lorg/telegram/messenger/MessageObject;)V
    //   11001: goto -1132 -> 9869
    //   11004: aload_1
    //   11005: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   11008: bipush 8
    //   11010: if_icmpne -1141 -> 9869
    //   11013: new 1163	java/lang/StringBuilder
    //   11016: dup
    //   11017: invokespecial 1164	java/lang/StringBuilder:<init>	()V
    //   11020: ldc_w 2758
    //   11023: invokevirtual 1170	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   11026: aload_1
    //   11027: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   11030: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   11033: getfield 1044	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   11036: getfield 1174	org/telegram/tgnet/TLRPC$Document:size	I
    //   11039: i2l
    //   11040: invokestatic 1178	org/telegram/messenger/AndroidUtilities:formatFileSize	(J)Ljava/lang/String;
    //   11043: invokevirtual 1170	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   11046: invokevirtual 1181	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   11049: astore 28
    //   11051: aload_0
    //   11052: getstatic 294	org/telegram/ui/Cells/ChatMessageCell:infoPaint	Landroid/text/TextPaint;
    //   11055: aload 28
    //   11057: invokevirtual 1067	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   11060: f2d
    //   11061: invokestatic 1020	java/lang/Math:ceil	(D)D
    //   11064: d2i
    //   11065: putfield 1183	org/telegram/ui/Cells/ChatMessageCell:infoWidth	I
    //   11068: aload_0
    //   11069: new 286	android/text/StaticLayout
    //   11072: dup
    //   11073: aload 28
    //   11075: getstatic 294	org/telegram/ui/Cells/ChatMessageCell:infoPaint	Landroid/text/TextPaint;
    //   11078: aload_0
    //   11079: getfield 1183	org/telegram/ui/Cells/ChatMessageCell:infoWidth	I
    //   11082: getstatic 1131	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   11085: fconst_1
    //   11086: fconst_0
    //   11087: iconst_0
    //   11088: invokespecial 1134	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   11091: putfield 1185	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   11094: aload_0
    //   11095: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11098: iconst_1
    //   11099: invokevirtual 1258	org/telegram/messenger/ImageReceiver:setNeedsQualityThumb	(Z)V
    //   11102: aload_0
    //   11103: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11106: iconst_1
    //   11107: invokevirtual 1261	org/telegram/messenger/ImageReceiver:setShouldGenerateQualityThumb	(Z)V
    //   11110: aload_0
    //   11111: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11114: aload_1
    //   11115: invokevirtual 1264	org/telegram/messenger/ImageReceiver:setParentMessageObject	(Lorg/telegram/messenger/MessageObject;)V
    //   11118: goto -1249 -> 9869
    //   11121: iload 11
    //   11123: istore 8
    //   11125: iload 10
    //   11127: istore 9
    //   11129: iload 11
    //   11131: ldc_w 2759
    //   11134: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11137: if_icmpge -1097 -> 10040
    //   11140: ldc_w 2759
    //   11143: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11146: istore 11
    //   11148: aload_0
    //   11149: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11152: getfield 2573	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   11155: i2f
    //   11156: iload 11
    //   11158: i2f
    //   11159: fdiv
    //   11160: fstore 6
    //   11162: iload 11
    //   11164: istore 8
    //   11166: iload 10
    //   11168: istore 9
    //   11170: aload_0
    //   11171: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11174: getfield 2570	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   11177: i2f
    //   11178: fload 6
    //   11180: fdiv
    //   11181: iload 14
    //   11183: i2f
    //   11184: fcmpg
    //   11185: ifge -1145 -> 10040
    //   11188: aload_0
    //   11189: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11192: getfield 2570	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   11195: i2f
    //   11196: fload 6
    //   11198: fdiv
    //   11199: f2i
    //   11200: istore 9
    //   11202: iload 11
    //   11204: istore 8
    //   11206: goto -1166 -> 10040
    //   11209: iload 9
    //   11211: istore 10
    //   11213: iload 8
    //   11215: istore 11
    //   11217: iload 9
    //   11219: ldc_w 2759
    //   11222: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11225: if_icmpge -1010 -> 10215
    //   11228: ldc_w 2759
    //   11231: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11234: istore 9
    //   11236: aload 28
    //   11238: getfield 2577	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   11241: i2f
    //   11242: iload 9
    //   11244: i2f
    //   11245: fdiv
    //   11246: fstore 6
    //   11248: iload 9
    //   11250: istore 10
    //   11252: iload 8
    //   11254: istore 11
    //   11256: aload 28
    //   11258: getfield 2576	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   11261: i2f
    //   11262: fload 6
    //   11264: fdiv
    //   11265: iload 14
    //   11267: i2f
    //   11268: fcmpg
    //   11269: ifge -1054 -> 10215
    //   11272: aload 28
    //   11274: getfield 2576	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   11277: i2f
    //   11278: fload 6
    //   11280: fdiv
    //   11281: f2i
    //   11282: istore 11
    //   11284: iload 9
    //   11286: istore 10
    //   11288: goto -1073 -> 10215
    //   11291: iload 15
    //   11293: iconst_1
    //   11294: iadd
    //   11295: istore 15
    //   11297: goto -1219 -> 10078
    //   11300: iconst_0
    //   11301: istore 10
    //   11303: goto -982 -> 10321
    //   11306: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   11309: getfield 1747	android/graphics/Point:x	I
    //   11312: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   11315: getfield 1750	android/graphics/Point:y	I
    //   11318: invokestatic 1077	java/lang/Math:min	(II)I
    //   11321: i2f
    //   11322: ldc_w 2583
    //   11325: fmul
    //   11326: f2i
    //   11327: istore 9
    //   11329: iload 9
    //   11331: istore 10
    //   11333: goto -955 -> 10378
    //   11336: astore 28
    //   11338: ldc_w 711
    //   11341: aload 28
    //   11343: invokestatic 717	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   11346: iload 11
    //   11348: istore 8
    //   11350: goto -735 -> 10615
    //   11353: aload_0
    //   11354: new 1163	java/lang/StringBuilder
    //   11357: dup
    //   11358: invokespecial 1164	java/lang/StringBuilder:<init>	()V
    //   11361: aload_0
    //   11362: getfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   11365: invokevirtual 1170	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   11368: ldc_w 2761
    //   11371: invokevirtual 1170	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   11374: invokevirtual 1181	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   11377: putfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   11380: goto -651 -> 10729
    //   11383: iconst_0
    //   11384: istore 10
    //   11386: goto -563 -> 10823
    //   11389: aconst_null
    //   11390: astore 28
    //   11392: goto -506 -> 10886
    //   11395: aload_0
    //   11396: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11399: getfield 1294	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   11402: istore 9
    //   11404: goto -504 -> 10900
    //   11407: aload_0
    //   11408: iconst_1
    //   11409: putfield 1800	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   11412: aload_0
    //   11413: getfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11416: ifnull +41 -> 11457
    //   11419: aload_0
    //   11420: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11423: aconst_null
    //   11424: aconst_null
    //   11425: aload_0
    //   11426: getfield 1293	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11429: getfield 1207	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   11432: aload_0
    //   11433: getfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   11436: iconst_0
    //   11437: aconst_null
    //   11438: iconst_0
    //   11439: invokevirtual 1297	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   11442: iload 8
    //   11444: istore 14
    //   11446: iload 13
    //   11448: istore 8
    //   11450: iload 12
    //   11452: istore 9
    //   11454: goto -3184 -> 8270
    //   11457: aload_0
    //   11458: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11461: aconst_null
    //   11462: checkcast 614	android/graphics/drawable/Drawable
    //   11465: invokevirtual 1278	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   11468: iload 8
    //   11470: istore 14
    //   11472: iload 13
    //   11474: istore 8
    //   11476: iload 12
    //   11478: istore 9
    //   11480: goto -3210 -> 8270
    //   11483: aload_0
    //   11484: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11487: aconst_null
    //   11488: checkcast 1274	android/graphics/drawable/BitmapDrawable
    //   11491: invokevirtual 1278	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   11494: iload 8
    //   11496: istore 14
    //   11498: iload 13
    //   11500: istore 8
    //   11502: iload 12
    //   11504: istore 9
    //   11506: goto -3236 -> 8270
    //   11509: aload_1
    //   11510: getfield 728	org/telegram/messenger/MessageObject:type	I
    //   11513: bipush 8
    //   11515: if_icmpne +339 -> 11854
    //   11518: aload_1
    //   11519: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   11522: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   11525: getfield 1044	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   11528: invokestatic 2686	org/telegram/messenger/FileLoader:getAttachFileName	(Lorg/telegram/tgnet/TLObject;)Ljava/lang/String;
    //   11531: astore 28
    //   11533: iconst_0
    //   11534: istore 9
    //   11536: aload_1
    //   11537: getfield 2118	org/telegram/messenger/MessageObject:attachPathExists	Z
    //   11540: ifeq +135 -> 11675
    //   11543: invokestatic 441	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   11546: aload_0
    //   11547: invokevirtual 2141	org/telegram/messenger/MediaController:removeLoadingFileObserver	(Lorg/telegram/messenger/MediaController$FileDownloadProgressListener;)V
    //   11550: iconst_1
    //   11551: istore 9
    //   11553: aload_1
    //   11554: invokevirtual 1312	org/telegram/messenger/MessageObject:isSending	()Z
    //   11557: ifne +233 -> 11790
    //   11560: iload 9
    //   11562: ifne +41 -> 11603
    //   11565: invokestatic 441	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   11568: bipush 32
    //   11570: invokevirtual 2689	org/telegram/messenger/MediaController:canDownloadMedia	(I)Z
    //   11573: ifeq +19 -> 11592
    //   11576: aload_1
    //   11577: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   11580: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   11583: getfield 1044	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   11586: invokestatic 2764	org/telegram/messenger/MessageObject:isNewGifDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   11589: ifne +14 -> 11603
    //   11592: invokestatic 1300	org/telegram/messenger/FileLoader:getInstance	()Lorg/telegram/messenger/FileLoader;
    //   11595: aload 28
    //   11597: invokevirtual 2692	org/telegram/messenger/FileLoader:isLoadingFile	(Ljava/lang/String;)Z
    //   11600: ifeq +190 -> 11790
    //   11603: iload 9
    //   11605: iconst_1
    //   11606: if_icmpne +100 -> 11706
    //   11609: aload_0
    //   11610: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11613: astore 30
    //   11615: aload_1
    //   11616: invokevirtual 995	org/telegram/messenger/MessageObject:isSendError	()Z
    //   11619: ifeq +69 -> 11688
    //   11622: aconst_null
    //   11623: astore 28
    //   11625: aload_0
    //   11626: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11629: ifnull +71 -> 11700
    //   11632: aload_0
    //   11633: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11636: getfield 1207	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   11639: astore 29
    //   11641: aload 30
    //   11643: aconst_null
    //   11644: aload 28
    //   11646: aconst_null
    //   11647: aconst_null
    //   11648: aload 29
    //   11650: aload_0
    //   11651: getfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   11654: iconst_0
    //   11655: aconst_null
    //   11656: iconst_0
    //   11657: invokevirtual 1272	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   11660: iload 8
    //   11662: istore 14
    //   11664: iload 13
    //   11666: istore 8
    //   11668: iload 12
    //   11670: istore 9
    //   11672: goto -3402 -> 8270
    //   11675: aload_1
    //   11676: getfield 2115	org/telegram/messenger/MessageObject:mediaExists	Z
    //   11679: ifeq -126 -> 11553
    //   11682: iconst_2
    //   11683: istore 9
    //   11685: goto -132 -> 11553
    //   11688: aload_1
    //   11689: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   11692: getfield 2743	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   11695: astore 28
    //   11697: goto -72 -> 11625
    //   11700: aconst_null
    //   11701: astore 29
    //   11703: goto -62 -> 11641
    //   11706: aload_0
    //   11707: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11710: astore 29
    //   11712: aload_1
    //   11713: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   11716: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   11719: getfield 1044	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   11722: astore 30
    //   11724: aload_0
    //   11725: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11728: ifnull +56 -> 11784
    //   11731: aload_0
    //   11732: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11735: getfield 1207	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   11738: astore 28
    //   11740: aload 29
    //   11742: aload 30
    //   11744: aconst_null
    //   11745: aload 28
    //   11747: aload_0
    //   11748: getfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   11751: aload_1
    //   11752: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   11755: getfield 757	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   11758: getfield 1044	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   11761: getfield 1174	org/telegram/tgnet/TLRPC$Document:size	I
    //   11764: aconst_null
    //   11765: iconst_0
    //   11766: invokevirtual 1297	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   11769: iload 8
    //   11771: istore 14
    //   11773: iload 13
    //   11775: istore 8
    //   11777: iload 12
    //   11779: istore 9
    //   11781: goto -3511 -> 8270
    //   11784: aconst_null
    //   11785: astore 28
    //   11787: goto -47 -> 11740
    //   11790: aload_0
    //   11791: iconst_1
    //   11792: putfield 1800	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   11795: aload_0
    //   11796: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11799: astore 29
    //   11801: aload_0
    //   11802: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11805: ifnull +43 -> 11848
    //   11808: aload_0
    //   11809: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11812: getfield 1207	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   11815: astore 28
    //   11817: aload 29
    //   11819: aconst_null
    //   11820: aconst_null
    //   11821: aload 28
    //   11823: aload_0
    //   11824: getfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   11827: iconst_0
    //   11828: aconst_null
    //   11829: iconst_0
    //   11830: invokevirtual 1297	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   11833: iload 8
    //   11835: istore 14
    //   11837: iload 13
    //   11839: istore 8
    //   11841: iload 12
    //   11843: istore 9
    //   11845: goto -3575 -> 8270
    //   11848: aconst_null
    //   11849: astore 28
    //   11851: goto -34 -> 11817
    //   11854: aload_0
    //   11855: getfield 451	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11858: astore 29
    //   11860: aload_0
    //   11861: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11864: ifnull +43 -> 11907
    //   11867: aload_0
    //   11868: getfield 1255	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11871: getfield 1207	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   11874: astore 28
    //   11876: aload 29
    //   11878: aconst_null
    //   11879: aconst_null
    //   11880: aload 28
    //   11882: aload_0
    //   11883: getfield 1268	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   11886: iconst_0
    //   11887: aconst_null
    //   11888: iconst_0
    //   11889: invokevirtual 1297	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   11892: iload 8
    //   11894: istore 14
    //   11896: iload 13
    //   11898: istore 8
    //   11900: iload 12
    //   11902: istore 9
    //   11904: goto -3634 -> 8270
    //   11907: aconst_null
    //   11908: astore 28
    //   11910: goto -34 -> 11876
    //   11913: aload_0
    //   11914: getfield 1990	org/telegram/ui/Cells/ChatMessageCell:drawNameLayout	Z
    //   11917: ifeq -3620 -> 8297
    //   11920: aload_1
    //   11921: getfield 751	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   11924: getfield 952	org/telegram/tgnet/TLRPC$Message:reply_to_msg_id	I
    //   11927: ifne -3630 -> 8297
    //   11930: aload_0
    //   11931: aload_0
    //   11932: getfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   11935: ldc_w 624
    //   11938: invokestatic 306	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11941: iadd
    //   11942: putfield 556	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   11945: goto -3648 -> 8297
    //   11948: iconst_0
    //   11949: istore 8
    //   11951: goto -7904 -> 4047
    //   11954: astore 28
    //   11956: ldc_w 711
    //   11959: aload 28
    //   11961: invokestatic 717	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   11964: goto -7794 -> 4170
    //   11967: ldc_w 588
    //   11970: fstore 6
    //   11972: goto -7679 -> 4293
    //   11975: iload 8
    //   11977: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   11980: getfield 1747	android/graphics/Point:x	I
    //   11983: getstatic 1742	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   11986: getfield 1750	android/graphics/Point:y	I
    //   11989: invokestatic 1077	java/lang/Math:min	(II)I
    //   11992: iadd
    //   11993: istore 8
    //   11995: goto -7680 -> 4315
    //   11998: ldc_w 1568
    //   12001: fstore 6
    //   12003: goto -7593 -> 4410
    //   12006: aload 29
    //   12008: invokestatic 1472	java/lang/System:currentTimeMillis	()J
    //   12011: invokestatic 1671	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$802	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;J)J
    //   12014: pop2
    //   12015: goto -7468 -> 4547
    //   12018: iload 9
    //   12020: iconst_1
    //   12021: iadd
    //   12022: istore 9
    //   12024: goto -7680 -> 4344
    //   12027: aload_0
    //   12028: iload 10
    //   12030: putfield 587	org/telegram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   12033: aload_0
    //   12034: invokespecial 2436	org/telegram/ui/Cells/ChatMessageCell:updateWaveform	()V
    //   12037: aload_0
    //   12038: iload 26
    //   12040: invokevirtual 2121	org/telegram/ui/Cells/ChatMessageCell:updateButtonState	(Z)V
    //   12043: return
    //   12044: aload_0
    //   12045: iconst_0
    //   12046: putfield 2367	org/telegram/ui/Cells/ChatMessageCell:substractBackgroundHeight	I
    //   12049: aload_0
    //   12050: iconst_0
    //   12051: putfield 2410	org/telegram/ui/Cells/ChatMessageCell:keyboardHeight	I
    //   12054: goto -21 -> 12033
    //   12057: astore 28
    //   12059: iload 12
    //   12061: istore 10
    //   12063: goto -9156 -> 2907
    //   12066: astore 28
    //   12068: iconst_3
    //   12069: istore 10
    //   12071: iload 16
    //   12073: istore 13
    //   12075: goto -9876 -> 2199
    //   12078: goto -5105 -> 6973
    //   12081: iload 12
    //   12083: istore 11
    //   12085: iload 8
    //   12087: istore 12
    //   12089: iload 13
    //   12091: istore 16
    //   12093: goto -9587 -> 2506
    //   12096: iconst_0
    //   12097: istore 20
    //   12099: goto -7325 -> 4774
    //   12102: iload 21
    //   12104: iload 24
    //   12106: iadd
    //   12107: istore 8
    //   12109: goto -7125 -> 4984
    //   12112: goto -7252 -> 4860
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	12115	0	this	ChatMessageCell
    //   0	12115	1	paramMessageObject	MessageObject
    //   2802	6266	2	d1	double
    //   8527	548	4	d2	double
    //   4094	7908	6	f1	float
    //   4112	6474	7	f2	float
    //   40	12068	8	i	int
    //   483	11540	9	j	int
    //   805	11265	10	k	int
    //   1038	11046	11	m	int
    //   717	11371	12	n	int
    //   720	11370	13	i1	int
    //   914	10981	14	i2	int
    //   920	10376	15	i3	int
    //   1252	10840	16	i4	int
    //   1258	9245	17	i5	int
    //   22	4156	18	i6	int
    //   510	6293	19	i7	int
    //   1255	10843	20	i8	int
    //   1249	10858	21	i9	int
    //   1596	3285	22	i10	int
    //   927	4139	23	i11	int
    //   911	11196	24	i12	int
    //   1507	671	25	i13	int
    //   65	11974	26	bool1	boolean
    //   560	7802	27	bool2	boolean
    //   2094	6	28	localException1	Exception
    //   2189	14	28	localException2	Exception
    //   2901	10	28	localException3	Exception
    //   2974	6	28	localException4	Exception
    //   3117	8156	28	localObject1	Object
    //   11336	6	28	localException5	Exception
    //   11390	519	28	localObject2	Object
    //   11954	6	28	localException6	Exception
    //   12057	1	28	localException7	Exception
    //   12066	1	28	localException8	Exception
    //   838	11169	29	localObject3	Object
    //   3145	8598	30	localObject4	Object
    //   3745	7160	31	localObject5	Object
    //   3751	7158	32	str	String
    // Exception table:
    //   from	to	target	type
    //   1056	1074	2094	java/lang/Exception
    //   1082	1114	2094	java/lang/Exception
    //   1122	1140	2094	java/lang/Exception
    //   1148	1159	2094	java/lang/Exception
    //   1167	1178	2094	java/lang/Exception
    //   1192	1201	2094	java/lang/Exception
    //   1209	1221	2094	java/lang/Exception
    //   1229	1240	2094	java/lang/Exception
    //   1390	1408	2189	java/lang/Exception
    //   1420	1431	2189	java/lang/Exception
    //   1443	1454	2189	java/lang/Exception
    //   1473	1485	2189	java/lang/Exception
    //   1497	1509	2189	java/lang/Exception
    //   1529	1539	2189	java/lang/Exception
    //   1551	1558	2189	java/lang/Exception
    //   1575	1587	2189	java/lang/Exception
    //   1619	1626	2189	java/lang/Exception
    //   1638	1649	2189	java/lang/Exception
    //   1661	1673	2189	java/lang/Exception
    //   1685	1697	2189	java/lang/Exception
    //   2172	2186	2189	java/lang/Exception
    //   2789	2803	2189	java/lang/Exception
    //   2243	2276	2901	java/lang/Exception
    //   2282	2297	2901	java/lang/Exception
    //   2297	2324	2901	java/lang/Exception
    //   2825	2863	2901	java/lang/Exception
    //   2522	2527	2974	java/lang/Exception
    //   2531	2538	2974	java/lang/Exception
    //   2542	2549	2974	java/lang/Exception
    //   2553	2566	2974	java/lang/Exception
    //   2570	2583	2974	java/lang/Exception
    //   2596	2603	2974	java/lang/Exception
    //   2607	2640	2974	java/lang/Exception
    //   2648	2666	2974	java/lang/Exception
    //   2670	2681	2974	java/lang/Exception
    //   2685	2696	2974	java/lang/Exception
    //   2706	2718	2974	java/lang/Exception
    //   2722	2738	2974	java/lang/Exception
    //   2750	2757	2974	java/lang/Exception
    //   2761	2768	2974	java/lang/Exception
    //   2938	2967	2974	java/lang/Exception
    //   4757	4771	2974	java/lang/Exception
    //   4782	4794	2974	java/lang/Exception
    //   4798	4814	2974	java/lang/Exception
    //   4823	4830	2974	java/lang/Exception
    //   4834	4839	2974	java/lang/Exception
    //   4848	4860	2974	java/lang/Exception
    //   4893	4900	2974	java/lang/Exception
    //   4904	4915	2974	java/lang/Exception
    //   4938	4955	2974	java/lang/Exception
    //   4964	4981	2974	java/lang/Exception
    //   4988	5000	2974	java/lang/Exception
    //   5022	5031	2974	java/lang/Exception
    //   5038	5052	2974	java/lang/Exception
    //   10436	10469	11336	java/lang/Exception
    //   10477	10487	11336	java/lang/Exception
    //   10491	10502	11336	java/lang/Exception
    //   10506	10521	11336	java/lang/Exception
    //   10525	10543	11336	java/lang/Exception
    //   10547	10565	11336	java/lang/Exception
    //   10573	10596	11336	java/lang/Exception
    //   10600	10608	11336	java/lang/Exception
    //   3970	4047	11954	java/lang/Exception
    //   4047	4170	11954	java/lang/Exception
    //   2336	2354	12057	java/lang/Exception
    //   2362	2373	12057	java/lang/Exception
    //   2381	2392	12057	java/lang/Exception
    //   2400	2411	12057	java/lang/Exception
    //   2419	2426	12057	java/lang/Exception
    //   2439	2451	12057	java/lang/Exception
    //   2462	2474	12057	java/lang/Exception
    //   2482	2494	12057	java/lang/Exception
    //   2881	2894	12057	java/lang/Exception
    //   1284	1324	12066	java/lang/Exception
    //   1327	1342	12066	java/lang/Exception
    //   1342	1375	12066	java/lang/Exception
    //   2114	2151	12066	java/lang/Exception
  }
  
  public void setPressed(boolean paramBoolean)
  {
    super.setPressed(paramBoolean);
    this.radialProgress.swapBackground(getDrawableForCurrentState());
    if (this.useSeekBarWaweform) {
      this.seekBarWaveform.setSelected(isDrawSelectedBackground());
    }
    for (;;)
    {
      invalidate();
      return;
      this.seekBar.setSelected(isDrawSelectedBackground());
    }
  }
  
  public void setVisiblePart(int paramInt1, int paramInt2)
  {
    if ((this.currentMessageObject == null) || (this.currentMessageObject.textLayoutBlocks == null)) {}
    int j;
    int k;
    int i;
    label85:
    label200:
    do
    {
      return;
      int i2 = paramInt1 - this.textY;
      int m = -1;
      j = -1;
      k = 0;
      i = 0;
      paramInt1 = 0;
      float f;
      int i1;
      int n;
      if ((paramInt1 >= this.currentMessageObject.textLayoutBlocks.size()) || (((MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(paramInt1)).textYOffset > i2))
      {
        paramInt1 = i;
        i = m;
        if (paramInt1 >= this.currentMessageObject.textLayoutBlocks.size()) {
          continue;
        }
        MessageObject.TextLayoutBlock localTextLayoutBlock = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(paramInt1);
        f = localTextLayoutBlock.textYOffset;
        if (!intersect(f, localTextLayoutBlock.height + f, i2, i2 + paramInt2)) {
          break label200;
        }
        j = i;
        if (i == -1) {
          j = paramInt1;
        }
        m = paramInt1;
        i1 = k + 1;
        n = j;
      }
      do
      {
        paramInt1 += 1;
        k = i1;
        i = n;
        j = m;
        break label85;
        i = paramInt1;
        paramInt1 += 1;
        break;
        i1 = k;
        n = i;
        m = j;
      } while (f <= i2);
    } while ((this.lastVisibleBlockNum == j) && (this.firstVisibleBlockNum == i) && (this.totalVisibleBlocksCount == k));
    this.lastVisibleBlockNum = j;
    this.firstVisibleBlockNum = i;
    this.totalVisibleBlocksCount = k;
    invalidate();
  }
  
  public void updateAudioProgress()
  {
    if ((this.currentMessageObject == null) || (this.documentAttach == null)) {
      return;
    }
    int m;
    label74:
    Object localObject;
    if (this.useSeekBarWaweform)
    {
      if (!this.seekBarWaveform.isDragging()) {
        this.seekBarWaveform.setProgress(this.currentMessageObject.audioProgress);
      }
      m = 0;
      k = 0;
      if (this.documentAttachType != 3) {
        break label277;
      }
      if (MediaController.getInstance().isPlayingAudio(this.currentMessageObject)) {
        break label266;
      }
      j = 0;
      i = k;
      if (j < this.documentAttach.attributes.size())
      {
        localObject = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(j);
        if (!(localObject instanceof TLRPC.TL_documentAttributeAudio)) {
          break label259;
        }
      }
    }
    label259:
    label266:
    for (int i = ((TLRPC.DocumentAttribute)localObject).duration;; i = this.currentMessageObject.audioProgressSec)
    {
      localObject = String.format("%02d:%02d", new Object[] { Integer.valueOf(i / 60), Integer.valueOf(i % 60) });
      if ((this.lastTimeString == null) || ((this.lastTimeString != null) && (!this.lastTimeString.equals(localObject))))
      {
        this.lastTimeString = ((String)localObject);
        this.timeWidthAudio = ((int)Math.ceil(audioTimePaint.measureText((String)localObject)));
        this.durationLayout = new StaticLayout((CharSequence)localObject, audioTimePaint, this.timeWidthAudio, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      }
      invalidate();
      return;
      if (this.seekBar.isDragging()) {
        break;
      }
      this.seekBar.setProgress(this.currentMessageObject.audioProgress);
      break;
      j += 1;
      break label74;
    }
    label277:
    int k = 0;
    int j = 0;
    for (;;)
    {
      i = m;
      if (j < this.documentAttach.attributes.size())
      {
        localObject = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(j);
        if ((localObject instanceof TLRPC.TL_documentAttributeAudio)) {
          i = ((TLRPC.DocumentAttribute)localObject).duration;
        }
      }
      else
      {
        j = k;
        if (MediaController.getInstance().isPlayingAudio(this.currentMessageObject)) {
          j = this.currentMessageObject.audioProgressSec;
        }
        localObject = ", " + AndroidUtilities.formatFileSize(this.documentAttach.size);
        localObject = String.format("%d:%02d / %d:%02d", new Object[] { Integer.valueOf(j / 60), Integer.valueOf(j % 60), Integer.valueOf(i / 60), Integer.valueOf(i % 60) }) + (String)localObject;
        if ((this.lastTimeString != null) && ((this.lastTimeString == null) || (this.lastTimeString.equals(localObject)))) {
          break;
        }
        this.lastTimeString = ((String)localObject);
        i = (int)Math.ceil(audioTimePaint.measureText((String)localObject));
        this.durationLayout = new StaticLayout((CharSequence)localObject, audioTimePaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        break;
      }
      j += 1;
    }
  }
  
  public void updateButtonState(boolean paramBoolean)
  {
    Object localObject1 = null;
    boolean bool = false;
    if (this.currentMessageObject.type == 1) {
      if (this.currentPhotoObject != null) {}
    }
    Object localObject2;
    label375:
    float f;
    label459:
    label476:
    label482:
    label487:
    label499:
    label625:
    label686:
    do
    {
      do
      {
        return;
        localObject1 = FileLoader.getAttachFileName(this.currentPhotoObject);
        bool = this.currentMessageObject.mediaExists;
        while ((localObject1 == null) || (((String)localObject1).length() == 0))
        {
          this.radialProgress.setBackground(null, false, false);
          return;
          if ((this.currentMessageObject.type == 8) || (this.documentAttachType == 4) || (this.currentMessageObject.type == 9) || (this.documentAttachType == 3) || (this.documentAttachType == 5))
          {
            if (this.currentMessageObject.attachPathExists)
            {
              localObject1 = this.currentMessageObject.messageOwner.attachPath;
              bool = true;
            }
            else if ((!this.currentMessageObject.isSendError()) || (this.documentAttachType == 3) || (this.documentAttachType == 5))
            {
              localObject1 = this.currentMessageObject.getFileName();
              bool = this.currentMessageObject.mediaExists;
            }
          }
          else if (this.documentAttachType != 0)
          {
            localObject1 = FileLoader.getAttachFileName(this.documentAttach);
            bool = this.currentMessageObject.mediaExists;
          }
          else if (this.currentPhotoObject != null)
          {
            localObject1 = FileLoader.getAttachFileName(this.currentPhotoObject);
            bool = this.currentMessageObject.mediaExists;
          }
        }
        int i;
        if ((this.currentMessageObject.messageOwner.params != null) && (this.currentMessageObject.messageOwner.params.containsKey("query_id")))
        {
          i = 1;
          if ((this.documentAttachType != 3) && (this.documentAttachType != 5)) {
            break label686;
          }
          if (((!this.currentMessageObject.isOut()) || (!this.currentMessageObject.isSending())) && ((!this.currentMessageObject.isSendError()) || (i == 0))) {
            break label499;
          }
          MediaController.getInstance().addLoadingFileObserver(this.currentMessageObject.messageOwner.attachPath, this.currentMessageObject, this);
          this.buttonState = 4;
          localObject1 = this.radialProgress;
          localObject2 = getDrawableForCurrentState();
          if (i != 0) {
            break label476;
          }
          bool = true;
          ((RadialProgress)localObject1).setBackground((Drawable)localObject2, bool, paramBoolean);
          if (i != 0) {
            break label487;
          }
          localObject2 = ImageLoader.getInstance().getFileProgress(this.currentMessageObject.messageOwner.attachPath);
          localObject1 = localObject2;
          if (localObject2 == null)
          {
            localObject1 = localObject2;
            if (SendMessagesHelper.getInstance().isSendingMessage(this.currentMessageObject.getId())) {
              localObject1 = Float.valueOf(1.0F);
            }
          }
          localObject2 = this.radialProgress;
          if (localObject1 == null) {
            break label482;
          }
          f = ((Float)localObject1).floatValue();
          ((RadialProgress)localObject2).setProgress(f, false);
        }
        for (;;)
        {
          updateAudioProgress();
          return;
          i = 0;
          break;
          bool = false;
          break label375;
          f = 0.0F;
          break label459;
          this.radialProgress.setProgress(0.0F, false);
          continue;
          if (bool)
          {
            MediaController.getInstance().removeLoadingFileObserver(this);
            bool = MediaController.getInstance().isPlayingAudio(this.currentMessageObject);
            if ((!bool) || ((bool) && (MediaController.getInstance().isAudioPaused()))) {}
            for (this.buttonState = 0;; this.buttonState = 1)
            {
              this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
              break;
            }
          }
          MediaController.getInstance().addLoadingFileObserver((String)localObject1, this.currentMessageObject, this);
          if (FileLoader.getInstance().isLoadingFile((String)localObject1)) {
            break label625;
          }
          this.buttonState = 2;
          this.radialProgress.setProgress(0.0F, paramBoolean);
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
        }
        this.buttonState = 4;
        localObject1 = ImageLoader.getInstance().getFileProgress((String)localObject1);
        if (localObject1 != null) {
          this.radialProgress.setProgress(((Float)localObject1).floatValue(), paramBoolean);
        }
        for (;;)
        {
          this.radialProgress.setBackground(getDrawableForCurrentState(), true, paramBoolean);
          break;
          this.radialProgress.setProgress(0.0F, paramBoolean);
        }
        if ((this.currentMessageObject.type != 0) || (this.documentAttachType == 1) || (this.documentAttachType == 4)) {
          break;
        }
      } while ((this.currentPhotoObject == null) || (!this.drawImageButton));
      if (!bool)
      {
        MediaController.getInstance().addLoadingFileObserver((String)localObject1, this.currentMessageObject, this);
        f = 0.0F;
        bool = false;
        if (!FileLoader.getInstance().isLoadingFile((String)localObject1))
        {
          if ((!this.cancelLoading) && (((this.documentAttachType == 0) && (MediaController.getInstance().canDownloadMedia(1))) || ((this.documentAttachType == 2) && (MediaController.getInstance().canDownloadMedia(32))))) {
            bool = true;
          }
          for (this.buttonState = 1;; this.buttonState = 0)
          {
            this.radialProgress.setProgress(f, false);
            this.radialProgress.setBackground(getDrawableForCurrentState(), bool, paramBoolean);
            invalidate();
            return;
          }
        }
        bool = true;
        this.buttonState = 1;
        localObject1 = ImageLoader.getInstance().getFileProgress((String)localObject1);
        if (localObject1 != null) {}
        for (f = ((Float)localObject1).floatValue();; f = 0.0F) {
          break;
        }
      }
      MediaController.getInstance().removeLoadingFileObserver(this);
      if ((this.documentAttachType == 2) && (!this.photoImage.isAllowStartAnimation())) {}
      for (this.buttonState = 2;; this.buttonState = -1)
      {
        this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
        invalidate();
        return;
      }
      if ((!this.currentMessageObject.isOut()) || (!this.currentMessageObject.isSending())) {
        break;
      }
    } while ((this.currentMessageObject.messageOwner.attachPath == null) || (this.currentMessageObject.messageOwner.attachPath.length() <= 0));
    MediaController.getInstance().addLoadingFileObserver(this.currentMessageObject.messageOwner.attachPath, this.currentMessageObject, this);
    if ((this.currentMessageObject.messageOwner.attachPath == null) || (!this.currentMessageObject.messageOwner.attachPath.startsWith("http")))
    {
      bool = true;
      localObject1 = this.currentMessageObject.messageOwner.params;
      if ((this.currentMessageObject.messageOwner.message == null) || (localObject1 == null) || ((!((HashMap)localObject1).containsKey("url")) && (!((HashMap)localObject1).containsKey("bot")))) {
        break label1212;
      }
      bool = false;
      this.buttonState = -1;
      label1105:
      this.radialProgress.setBackground(getDrawableForCurrentState(), bool, paramBoolean);
      if (!bool) {
        break label1225;
      }
      localObject2 = ImageLoader.getInstance().getFileProgress(this.currentMessageObject.messageOwner.attachPath);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = localObject2;
        if (SendMessagesHelper.getInstance().isSendingMessage(this.currentMessageObject.getId())) {
          localObject1 = Float.valueOf(1.0F);
        }
      }
      localObject2 = this.radialProgress;
      if (localObject1 == null) {
        break label1220;
      }
      f = ((Float)localObject1).floatValue();
      label1194:
      ((RadialProgress)localObject2).setProgress(f, false);
    }
    for (;;)
    {
      invalidate();
      return;
      bool = false;
      break;
      label1212:
      this.buttonState = 1;
      break label1105;
      label1220:
      f = 0.0F;
      break label1194;
      label1225:
      this.radialProgress.setProgress(0.0F, false);
    }
    if ((this.currentMessageObject.messageOwner.attachPath != null) && (this.currentMessageObject.messageOwner.attachPath.length() != 0)) {
      MediaController.getInstance().removeLoadingFileObserver(this);
    }
    if (!bool)
    {
      MediaController.getInstance().addLoadingFileObserver((String)localObject1, this.currentMessageObject, this);
      f = 0.0F;
      bool = false;
      if (!FileLoader.getInstance().isLoadingFile((String)localObject1))
      {
        if ((!this.cancelLoading) && (((this.currentMessageObject.type == 1) && (MediaController.getInstance().canDownloadMedia(1))) || ((this.currentMessageObject.type == 8) && (MediaController.getInstance().canDownloadMedia(32)) && (MessageObject.isNewGifDocument(this.currentMessageObject.messageOwner.media.document))))) {
          bool = true;
        }
        for (this.buttonState = 1;; this.buttonState = 0)
        {
          this.radialProgress.setBackground(getDrawableForCurrentState(), bool, paramBoolean);
          this.radialProgress.setProgress(f, false);
          invalidate();
          return;
        }
      }
      bool = true;
      this.buttonState = 1;
      localObject1 = ImageLoader.getInstance().getFileProgress((String)localObject1);
      if (localObject1 != null) {}
      for (f = ((Float)localObject1).floatValue();; f = 0.0F) {
        break;
      }
    }
    MediaController.getInstance().removeLoadingFileObserver(this);
    if ((this.currentMessageObject.type == 8) && (!this.photoImage.isAllowStartAnimation())) {
      this.buttonState = 2;
    }
    for (;;)
    {
      this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
      if (this.photoNotSet) {
        setMessageObject(this.currentMessageObject);
      }
      invalidate();
      return;
      if (this.documentAttachType == 4) {
        this.buttonState = 3;
      } else {
        this.buttonState = -1;
      }
    }
  }
  
  private class BotButton
  {
    private int angle;
    private TLRPC.KeyboardButton button;
    private int height;
    private long lastUpdateTime;
    private float progressAlpha;
    private StaticLayout title;
    private int width;
    private int x;
    private int y;
    
    private BotButton() {}
  }
  
  public static abstract interface ChatMessageCellDelegate
  {
    public abstract boolean canPerformActions();
    
    public abstract void didLongPressed(ChatMessageCell paramChatMessageCell);
    
    public abstract void didPressedBotButton(ChatMessageCell paramChatMessageCell, TLRPC.KeyboardButton paramKeyboardButton);
    
    public abstract void didPressedCancelSendButton(ChatMessageCell paramChatMessageCell);
    
    public abstract void didPressedChannelAvatar(ChatMessageCell paramChatMessageCell, TLRPC.Chat paramChat, int paramInt);
    
    public abstract void didPressedDirectReply(ChatMessageCell paramChatMessageCell);
    
    public abstract void didPressedImage(ChatMessageCell paramChatMessageCell);
    
    public abstract void didPressedOther(ChatMessageCell paramChatMessageCell);
    
    public abstract void didPressedReplyMessage(ChatMessageCell paramChatMessageCell, int paramInt);
    
    public abstract void didPressedShare(ChatMessageCell paramChatMessageCell);
    
    public abstract void didPressedUrl(MessageObject paramMessageObject, ClickableSpan paramClickableSpan, boolean paramBoolean);
    
    public abstract void didPressedUserAvatar(ChatMessageCell paramChatMessageCell, TLRPC.User paramUser);
    
    public abstract void didPressedViaBot(ChatMessageCell paramChatMessageCell, String paramString);
    
    public abstract void needOpenWebView(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2);
    
    public abstract boolean needPlayAudio(MessageObject paramMessageObject);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Cells\ChatMessageCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */