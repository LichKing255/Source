package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AccelerateInterpolator;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.FileDownloadProgressListener;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineMessage;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.RadialProgress;

public class ContextLinkCell
  extends View
  implements MediaController.FileDownloadProgressListener
{
  private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
  private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
  private static final int DOCUMENT_ATTACH_TYPE_GEO = 8;
  private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
  private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
  private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
  private static final int DOCUMENT_ATTACH_TYPE_PHOTO = 7;
  private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
  private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
  private static TextPaint descriptionTextPaint;
  private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5F);
  private static Paint paint;
  private static Drawable shadowDrawable;
  private static TextPaint titleTextPaint;
  private int TAG;
  private boolean buttonPressed;
  private int buttonState;
  private ContextLinkCellDelegate delegate;
  private StaticLayout descriptionLayout;
  private int descriptionY = AndroidUtilities.dp(27.0F);
  private TLRPC.Document documentAttach;
  private int documentAttachType;
  private boolean drawLinkImageView;
  private TLRPC.BotInlineResult inlineResult;
  private long lastUpdateTime;
  private LetterDrawable letterDrawable;
  private ImageReceiver linkImageView;
  private StaticLayout linkLayout;
  private int linkY;
  private boolean mediaWebpage;
  private boolean needDivider;
  private boolean needShadow;
  private RadialProgress radialProgress;
  private float scale;
  private boolean scaled;
  private long time = 0L;
  private StaticLayout titleLayout;
  private int titleY = AndroidUtilities.dp(7.0F);
  
  public ContextLinkCell(Context paramContext)
  {
    super(paramContext);
    if (titleTextPaint == null)
    {
      titleTextPaint = new TextPaint(1);
      titleTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      titleTextPaint.setColor(-14606047);
      titleTextPaint.setTextSize(AndroidUtilities.dp(15.0F));
      descriptionTextPaint = new TextPaint(1);
      descriptionTextPaint.setTextSize(AndroidUtilities.dp(13.0F));
      descriptionTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      paint = new Paint();
      paint.setColor(-2500135);
      paint.setStrokeWidth(1.0F);
    }
    this.linkImageView = new ImageReceiver(this);
    this.letterDrawable = new LetterDrawable();
    this.radialProgress = new RadialProgress(this);
    this.TAG = MediaController.getInstance().generateObserverTag();
  }
  
  private Drawable getDrawableForCurrentState()
  {
    if (this.buttonState == 1) {
      return Theme.photoStatesDrawables[5][0];
    }
    return null;
  }
  
  public TLRPC.Document getDocument()
  {
    return this.documentAttach;
  }
  
  public int getObserverTag()
  {
    return this.TAG;
  }
  
  public TLRPC.BotInlineResult getResult()
  {
    return this.inlineResult;
  }
  
  public boolean isSticker()
  {
    return this.documentAttachType == 6;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if ((this.drawLinkImageView) && (this.linkImageView.onAttachedToWindow())) {
      updateButtonState(false);
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.drawLinkImageView) {
      this.linkImageView.onDetachedFromWindow();
    }
    MediaController.getInstance().removeLoadingFileObserver(this);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    float f;
    label76:
    label131:
    int i;
    int j;
    int k;
    int m;
    label321:
    long l2;
    if (this.titleLayout != null)
    {
      paramCanvas.save();
      if (LocaleController.isRTL)
      {
        f = 8.0F;
        paramCanvas.translate(AndroidUtilities.dp(f), this.titleY);
        this.titleLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
    }
    else
    {
      if (this.descriptionLayout != null)
      {
        descriptionTextPaint.setColor(-7697782);
        paramCanvas.save();
        if (!LocaleController.isRTL) {
          break label616;
        }
        f = 8.0F;
        paramCanvas.translate(AndroidUtilities.dp(f), this.descriptionY);
        this.descriptionLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      if (this.linkLayout != null)
      {
        descriptionTextPaint.setColor(-14255946);
        paramCanvas.save();
        if (!LocaleController.isRTL) {
          break label624;
        }
        f = 8.0F;
        paramCanvas.translate(AndroidUtilities.dp(f), this.linkY);
        this.linkLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      if (this.mediaWebpage) {
        break label997;
      }
      if ((this.inlineResult == null) || (!this.inlineResult.type.equals("file"))) {
        break label632;
      }
      i = Theme.inlineDocDrawable.getIntrinsicWidth();
      j = Theme.inlineDocDrawable.getIntrinsicHeight();
      k = this.linkImageView.getImageX() + (AndroidUtilities.dp(52.0F) - i) / 2;
      m = this.linkImageView.getImageY() + (AndroidUtilities.dp(52.0F) - j) / 2;
      paramCanvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + AndroidUtilities.dp(52.0F), this.linkImageView.getImageY() + AndroidUtilities.dp(52.0F), LetterDrawable.paint);
      Theme.inlineDocDrawable.setBounds(k, m, k + i, m + j);
      Theme.inlineDocDrawable.draw(paramCanvas);
      if (this.drawLinkImageView)
      {
        paramCanvas.save();
        if (((this.scaled) && (this.scale != 0.8F)) || ((!this.scaled) && (this.scale != 1.0F)))
        {
          long l1 = System.currentTimeMillis();
          l2 = l1 - this.lastUpdateTime;
          this.lastUpdateTime = l1;
          if ((!this.scaled) || (this.scale == 0.8F)) {
            break label1172;
          }
          this.scale -= (float)l2 / 400.0F;
          if (this.scale < 0.8F) {
            this.scale = 0.8F;
          }
          label439:
          invalidate();
        }
        paramCanvas.scale(this.scale, this.scale, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        this.linkImageView.draw(paramCanvas);
        paramCanvas.restore();
      }
      if ((this.mediaWebpage) && ((this.documentAttachType == 7) || (this.documentAttachType == 2))) {
        this.radialProgress.draw(paramCanvas);
      }
      if ((this.needDivider) && (!this.mediaWebpage))
      {
        if (!LocaleController.isRTL) {
          break label1205;
        }
        paramCanvas.drawLine(0.0F, getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, paint);
      }
    }
    for (;;)
    {
      if ((this.needShadow) && (shadowDrawable != null))
      {
        shadowDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(3.0F));
        shadowDrawable.draw(paramCanvas);
      }
      return;
      f = AndroidUtilities.leftBaseline;
      break;
      label616:
      f = AndroidUtilities.leftBaseline;
      break label76;
      label624:
      f = AndroidUtilities.leftBaseline;
      break label131;
      label632:
      if ((this.inlineResult != null) && ((this.inlineResult.type.equals("audio")) || (this.inlineResult.type.equals("voice"))))
      {
        i = Theme.inlineAudioDrawable.getIntrinsicWidth();
        j = Theme.inlineAudioDrawable.getIntrinsicHeight();
        k = this.linkImageView.getImageX() + (AndroidUtilities.dp(52.0F) - i) / 2;
        m = this.linkImageView.getImageY() + (AndroidUtilities.dp(52.0F) - j) / 2;
        paramCanvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + AndroidUtilities.dp(52.0F), this.linkImageView.getImageY() + AndroidUtilities.dp(52.0F), LetterDrawable.paint);
        Theme.inlineAudioDrawable.setBounds(k, m, k + i, m + j);
        Theme.inlineAudioDrawable.draw(paramCanvas);
        break label321;
      }
      if ((this.inlineResult != null) && ((this.inlineResult.type.equals("venue")) || (this.inlineResult.type.equals("geo"))))
      {
        i = Theme.inlineLocationDrawable.getIntrinsicWidth();
        j = Theme.inlineLocationDrawable.getIntrinsicHeight();
        k = this.linkImageView.getImageX() + (AndroidUtilities.dp(52.0F) - i) / 2;
        m = this.linkImageView.getImageY() + (AndroidUtilities.dp(52.0F) - j) / 2;
        paramCanvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + AndroidUtilities.dp(52.0F), this.linkImageView.getImageY() + AndroidUtilities.dp(52.0F), LetterDrawable.paint);
        Theme.inlineLocationDrawable.setBounds(k, m, k + i, m + j);
        Theme.inlineLocationDrawable.draw(paramCanvas);
        break label321;
      }
      this.letterDrawable.draw(paramCanvas);
      break label321;
      label997:
      if ((this.inlineResult == null) || ((!(this.inlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaGeo)) && (!(this.inlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue)))) {
        break label321;
      }
      i = Theme.inlineLocationDrawable.getIntrinsicWidth();
      j = Theme.inlineLocationDrawable.getIntrinsicHeight();
      k = this.linkImageView.getImageX() + (this.linkImageView.getImageWidth() - i) / 2;
      m = this.linkImageView.getImageY() + (this.linkImageView.getImageHeight() - j) / 2;
      paramCanvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + this.linkImageView.getImageWidth(), this.linkImageView.getImageY() + this.linkImageView.getImageHeight(), LetterDrawable.paint);
      Theme.inlineLocationDrawable.setBounds(k, m, k + i, m + j);
      Theme.inlineLocationDrawable.draw(paramCanvas);
      break label321;
      label1172:
      this.scale += (float)l2 / 400.0F;
      if (this.scale <= 1.0F) {
        break label439;
      }
      this.scale = 1.0F;
      break label439;
      label1205:
      paramCanvas.drawLine(AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, paint);
    }
  }
  
  public void onFailedDownload(String paramString)
  {
    updateButtonState(false);
  }
  
  @SuppressLint({"DrawAllocation"})
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    this.drawLinkImageView = false;
    this.descriptionLayout = null;
    this.titleLayout = null;
    this.linkLayout = null;
    this.linkY = AndroidUtilities.dp(27.0F);
    if ((this.inlineResult == null) && (this.documentAttach == null))
    {
      setMeasuredDimension(AndroidUtilities.dp(100.0F), AndroidUtilities.dp(100.0F));
      return;
    }
    int i1 = View.MeasureSpec.getSize(paramInt1);
    int i = i1 - AndroidUtilities.dp(AndroidUtilities.leftBaseline) - AndroidUtilities.dp(8.0F);
    Object localObject9 = null;
    Object localObject8 = null;
    Object localObject1 = null;
    Object localObject4 = null;
    Object localObject7 = null;
    Object localObject3;
    if (this.documentAttach != null)
    {
      localObject3 = new ArrayList();
      ((ArrayList)localObject3).add(this.documentAttach.thumb);
    }
    int j;
    for (;;)
    {
      if ((this.mediaWebpage) || (this.inlineResult == null) || (this.inlineResult.title != null)) {}
      try
      {
        j = (int)Math.ceil(titleTextPaint.measureText(this.inlineResult.title));
        this.titleLayout = new StaticLayout(TextUtils.ellipsize(Emoji.replaceEmoji(this.inlineResult.title.replace('\n', ' '), titleTextPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0F), false), titleTextPaint, Math.min(j, i), TextUtils.TruncateAt.END), titleTextPaint, i + AndroidUtilities.dp(4.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        this.letterDrawable.setTitle(this.inlineResult.title);
        if (this.inlineResult.description == null) {}
      }
      catch (Exception localException2)
      {
        try
        {
          this.descriptionLayout = ChatMessageCell.generateStaticLayout(Emoji.replaceEmoji(this.inlineResult.description, descriptionTextPaint.getFontMetricsInt(), AndroidUtilities.dp(13.0F), false), descriptionTextPaint, i, i, 0, 3);
          if (this.descriptionLayout.getLineCount() > 0) {
            this.linkY = (this.descriptionY + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1) + AndroidUtilities.dp(1.0F));
          }
          if (this.inlineResult.url == null) {}
        }
        catch (Exception localException2)
        {
          try
          {
            for (;;)
            {
              j = (int)Math.ceil(descriptionTextPaint.measureText(this.inlineResult.url));
              this.linkLayout = new StaticLayout(TextUtils.ellipsize(this.inlineResult.url.replace('\n', ' '), descriptionTextPaint, Math.min(j, i), TextUtils.TruncateAt.MIDDLE), descriptionTextPaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
              this.documentAttachType = 0;
              localObject10 = null;
              if (this.documentAttach == null) {
                break label1336;
              }
              if (!MessageObject.isGifDocument(this.documentAttach)) {
                break label1279;
              }
              this.documentAttachType = 2;
              localObject1 = this.documentAttach.thumb;
              localObject5 = localObject8;
              localObject6 = localObject10;
              if (this.inlineResult != null)
              {
                localObject3 = localObject7;
                if (this.inlineResult.content_url != null)
                {
                  localObject3 = localObject7;
                  if (this.inlineResult.type != null)
                  {
                    if (!this.inlineResult.type.startsWith("gif")) {
                      break label1436;
                    }
                    localObject3 = localObject7;
                    if (this.documentAttachType != 2)
                    {
                      localObject3 = this.inlineResult.content_url;
                      this.documentAttachType = 2;
                    }
                  }
                }
                localObject4 = localObject3;
                if (localObject3 == null)
                {
                  localObject4 = localObject3;
                  if (this.inlineResult.thumb_url != null) {
                    localObject4 = this.inlineResult.thumb_url;
                  }
                }
              }
              localObject7 = localObject4;
              if (localObject4 == null)
              {
                localObject7 = localObject4;
                if (localObject1 == null)
                {
                  localObject7 = localObject4;
                  if (localObject5 == null) {
                    if (!(this.inlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue))
                    {
                      localObject7 = localObject4;
                      if (!(this.inlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaGeo)) {}
                    }
                    else
                    {
                      double d1 = this.inlineResult.send_message.geo.lat;
                      double d2 = this.inlineResult.send_message.geo._long;
                      localObject7 = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:small|%f,%f&sensor=false", new Object[] { Double.valueOf(d1), Double.valueOf(d2), Integer.valueOf(Math.min(2, (int)Math.ceil(AndroidUtilities.density))), Double.valueOf(d1), Double.valueOf(d2) });
                    }
                  }
                }
              }
              j = 0;
              int n = 0;
              m = n;
              k = j;
              if (this.documentAttach != null)
              {
                i = 0;
                m = n;
                k = j;
                if (i < this.documentAttach.attributes.size())
                {
                  localObject3 = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(i);
                  if ((!(localObject3 instanceof TLRPC.TL_documentAttributeImageSize)) && (!(localObject3 instanceof TLRPC.TL_documentAttributeVideo))) {
                    break label1486;
                  }
                  k = ((TLRPC.DocumentAttribute)localObject3).w;
                  m = ((TLRPC.DocumentAttribute)localObject3).h;
                }
              }
              if (k != 0)
              {
                i = m;
                j = k;
                if (m != 0) {}
              }
              else
              {
                if (localObject1 == null) {
                  break label1495;
                }
                if (localObject5 != null) {
                  ((TLRPC.PhotoSize)localObject5).size = -1;
                }
                j = ((TLRPC.PhotoSize)localObject1).w;
                i = ((TLRPC.PhotoSize)localObject1).h;
              }
              if (j != 0)
              {
                k = i;
                if (i != 0) {}
              }
              else
              {
                k = AndroidUtilities.dp(80.0F);
                j = k;
              }
              if ((this.documentAttach != null) || (localObject1 != null) || (localObject7 != null))
              {
                localObject4 = "52_52_b";
                if (!this.mediaWebpage) {
                  break label1594;
                }
                i = (int)(j / (k / AndroidUtilities.dp(80.0F)));
                if (this.documentAttachType != 2) {
                  break label1531;
                }
                localObject3 = String.format(Locale.US, "%d_%d_b", new Object[] { Integer.valueOf((int)(i / AndroidUtilities.density)), Integer.valueOf(80) });
                localObject4 = localObject3;
                localObject8 = this.linkImageView;
                if (this.documentAttachType != 6) {
                  break label1602;
                }
                bool = true;
                ((ImageReceiver)localObject8).setAspectFit(bool);
                if (this.documentAttachType != 2) {
                  break label1659;
                }
                if (this.documentAttach == null) {
                  break label1614;
                }
                localObject4 = this.linkImageView;
                localObject5 = this.documentAttach;
                if (localObject1 == null) {
                  break label1608;
                }
                localObject1 = ((TLRPC.PhotoSize)localObject1).location;
                ((ImageReceiver)localObject4).setImage((TLObject)localObject5, null, (TLRPC.FileLocation)localObject1, (String)localObject3, this.documentAttach.size, (String)localObject6, false);
                this.drawLinkImageView = true;
              }
              if (!this.mediaWebpage) {
                break label1765;
              }
              setBackgroundDrawable(null);
              paramInt2 = View.MeasureSpec.getSize(paramInt2);
              paramInt1 = paramInt2;
              if (paramInt2 == 0) {
                paramInt1 = AndroidUtilities.dp(100.0F);
              }
              setMeasuredDimension(i1, paramInt1);
              paramInt2 = (i1 - AndroidUtilities.dp(24.0F)) / 2;
              i = (paramInt1 - AndroidUtilities.dp(24.0F)) / 2;
              this.radialProgress.setProgressRect(paramInt2, i, AndroidUtilities.dp(24.0F) + paramInt2, AndroidUtilities.dp(24.0F) + i);
              this.linkImageView.setImageCoords(0, 0, i1, paramInt1);
              return;
              localObject3 = localObject1;
              if (this.inlineResult == null) {
                break;
              }
              localObject3 = localObject1;
              if (this.inlineResult.photo == null) {
                break;
              }
              localObject3 = new ArrayList(this.inlineResult.photo.sizes);
              break;
              localException1 = localException1;
              FileLog.e("tmessages", localException1);
            }
            localException2 = localException2;
            FileLog.e("tmessages", localException2);
          }
          catch (Exception localException3)
          {
            Object localObject5;
            Object localObject6;
            for (;;)
            {
              Object localObject10;
              int m;
              int k;
              boolean bool;
              FileLog.e("tmessages", localException3);
              continue;
              label1279:
              if (MessageObject.isStickerDocument(this.documentAttach))
              {
                this.documentAttachType = 6;
                localObject2 = this.documentAttach.thumb;
                localObject6 = "webp";
                localObject5 = localObject8;
              }
              else
              {
                localObject2 = this.documentAttach.thumb;
                localObject6 = localObject10;
                localObject5 = localObject8;
                continue;
                label1336:
                localObject6 = localObject10;
                localObject2 = localObject9;
                localObject5 = localObject8;
                if (this.inlineResult != null)
                {
                  localObject6 = localObject10;
                  localObject2 = localObject9;
                  localObject5 = localObject8;
                  if (this.inlineResult.photo != null)
                  {
                    this.documentAttachType = 7;
                    localObject8 = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject3, AndroidUtilities.getPhotoSize(), true);
                    localObject3 = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject3, 80);
                    localObject6 = localObject10;
                    localObject2 = localObject8;
                    localObject5 = localObject3;
                    if (localObject3 == localObject8)
                    {
                      localObject5 = null;
                      localObject6 = localObject10;
                      localObject2 = localObject8;
                      continue;
                      label1436:
                      localObject3 = localObject7;
                      if (this.inlineResult.type.equals("photo"))
                      {
                        localObject4 = this.inlineResult.thumb_url;
                        localObject3 = localObject4;
                        if (localObject4 == null)
                        {
                          localObject3 = this.inlineResult.content_url;
                          continue;
                          label1486:
                          i += 1;
                          continue;
                          label1495:
                          i = m;
                          j = k;
                          if (this.inlineResult != null)
                          {
                            j = this.inlineResult.w;
                            i = this.inlineResult.h;
                            continue;
                            label1531:
                            localObject3 = String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf((int)(i / AndroidUtilities.density)), Integer.valueOf(80) });
                            localObject4 = (String)localObject3 + "_b";
                            continue;
                            label1594:
                            localObject3 = "52_52";
                            continue;
                            label1602:
                            bool = false;
                            continue;
                            label1608:
                            localObject2 = null;
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
            label1614:
            localObject4 = this.linkImageView;
            if (localObject2 != null) {}
            for (Object localObject2 = ((TLRPC.PhotoSize)localObject2).location;; localObject2 = null)
            {
              ((ImageReceiver)localObject4).setImage(null, (String)localObject7, null, null, (TLRPC.FileLocation)localObject2, (String)localObject3, -1, (String)localObject6, true);
              break;
            }
            label1659:
            if (localObject2 != null)
            {
              localObject7 = this.linkImageView;
              localObject8 = ((TLRPC.PhotoSize)localObject2).location;
              if (localObject5 != null) {}
              for (localObject5 = ((TLRPC.PhotoSize)localObject5).location;; localObject5 = null)
              {
                ((ImageReceiver)localObject7).setImage((TLObject)localObject8, (String)localObject3, (TLRPC.FileLocation)localObject5, (String)localObject4, ((TLRPC.PhotoSize)localObject2).size, (String)localObject6, false);
                break;
              }
            }
            localObject8 = this.linkImageView;
            if (localObject5 != null) {}
            for (localObject2 = ((TLRPC.PhotoSize)localObject5).location;; localObject2 = null)
            {
              ((ImageReceiver)localObject8).setImage(null, (String)localObject7, (String)localObject3, null, (TLRPC.FileLocation)localObject2, (String)localObject4, -1, (String)localObject6, true);
              break;
            }
            label1765:
            setBackgroundResource(2130837932);
            i = 0;
            paramInt2 = i;
            if (this.titleLayout != null)
            {
              paramInt2 = i;
              if (this.titleLayout.getLineCount() != 0) {
                paramInt2 = 0 + this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
              }
            }
            i = paramInt2;
            if (this.descriptionLayout != null)
            {
              i = paramInt2;
              if (this.descriptionLayout.getLineCount() != 0) {
                i = paramInt2 + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
              }
            }
            paramInt2 = i;
            if (this.linkLayout != null)
            {
              paramInt2 = i;
              if (this.linkLayout.getLineCount() > 0) {
                paramInt2 = i + this.linkLayout.getLineBottom(this.linkLayout.getLineCount() - 1);
              }
            }
            paramInt2 = Math.max(AndroidUtilities.dp(52.0F), paramInt2);
            i = View.MeasureSpec.getSize(paramInt1);
            j = Math.max(AndroidUtilities.dp(68.0F), AndroidUtilities.dp(16.0F) + paramInt2);
            if (!this.needDivider) {
              break label2022;
            }
          }
        }
      }
    }
    paramInt2 = 1;
    setMeasuredDimension(i, paramInt2 + j);
    paramInt2 = AndroidUtilities.dp(52.0F);
    if (LocaleController.isRTL) {}
    for (paramInt1 = View.MeasureSpec.getSize(paramInt1) - AndroidUtilities.dp(8.0F) - paramInt2;; paramInt1 = AndroidUtilities.dp(8.0F))
    {
      this.letterDrawable.setBounds(paramInt1, AndroidUtilities.dp(8.0F), paramInt1 + paramInt2, AndroidUtilities.dp(60.0F));
      this.linkImageView.setImageCoords(paramInt1, AndroidUtilities.dp(8.0F), paramInt2, paramInt2);
      return;
      label2022:
      paramInt2 = 0;
      break;
    }
  }
  
  public void onProgressDownload(String paramString, float paramFloat)
  {
    this.radialProgress.setProgress(paramFloat, true);
    if (this.buttonState != 1) {
      updateButtonState(false);
    }
  }
  
  public void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean) {}
  
  public void onSuccessDownload(String paramString)
  {
    this.radialProgress.setProgress(1.0F, true);
    updateButtonState(true);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && ((paramMotionEvent.getAction() == 0) || (paramMotionEvent.getAction() == 2))) {
      getBackground().setHotspot(paramMotionEvent.getX(), paramMotionEvent.getY());
    }
    if ((this.mediaWebpage) || (this.delegate == null) || (this.inlineResult == null))
    {
      bool2 = super.onTouchEvent(paramMotionEvent);
      return bool2;
    }
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    boolean bool2 = false;
    AndroidUtilities.dp(48.0F);
    boolean bool1 = bool2;
    if (this.inlineResult != null)
    {
      bool1 = bool2;
      if (this.inlineResult.content_url != null)
      {
        bool1 = bool2;
        if (this.inlineResult.content_url.length() > 0)
        {
          if (paramMotionEvent.getAction() != 0) {
            break label189;
          }
          bool1 = bool2;
          if (this.letterDrawable.getBounds().contains(i, j))
          {
            this.buttonPressed = true;
            bool1 = true;
          }
        }
      }
    }
    for (;;)
    {
      bool2 = bool1;
      if (bool1) {
        break;
      }
      return super.onTouchEvent(paramMotionEvent);
      label189:
      bool1 = bool2;
      if (this.buttonPressed) {
        if (paramMotionEvent.getAction() == 1)
        {
          this.buttonPressed = false;
          playSoundEffect(0);
          this.delegate.didPressedImage(this);
          bool1 = bool2;
        }
        else if (paramMotionEvent.getAction() == 3)
        {
          this.buttonPressed = false;
          bool1 = bool2;
        }
        else
        {
          bool1 = bool2;
          if (paramMotionEvent.getAction() == 2)
          {
            bool1 = bool2;
            if (!this.letterDrawable.getBounds().contains(i, j))
            {
              this.buttonPressed = false;
              bool1 = bool2;
            }
          }
        }
      }
    }
  }
  
  public void setDelegate(ContextLinkCellDelegate paramContextLinkCellDelegate)
  {
    this.delegate = paramContextLinkCellDelegate;
  }
  
  public void setGif(TLRPC.Document paramDocument, boolean paramBoolean)
  {
    this.needDivider = paramBoolean;
    this.needShadow = false;
    this.inlineResult = null;
    this.documentAttach = paramDocument;
    this.mediaWebpage = true;
    requestLayout();
    updateButtonState(false);
  }
  
  public void setLink(TLRPC.BotInlineResult paramBotInlineResult, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this.needDivider = paramBoolean2;
    this.needShadow = paramBoolean3;
    if ((this.needShadow) && (shadowDrawable == null)) {
      shadowDrawable = getContext().getResources().getDrawable(2130837802);
    }
    this.inlineResult = paramBotInlineResult;
    if ((this.inlineResult != null) && (this.inlineResult.document != null)) {}
    for (this.documentAttach = this.inlineResult.document;; this.documentAttach = null)
    {
      this.mediaWebpage = paramBoolean1;
      requestLayout();
      updateButtonState(false);
      return;
    }
  }
  
  public void setScaled(boolean paramBoolean)
  {
    this.scaled = paramBoolean;
    this.lastUpdateTime = System.currentTimeMillis();
    invalidate();
  }
  
  public boolean showingBitmap()
  {
    return this.linkImageView.getBitmap() != null;
  }
  
  public void updateButtonState(boolean paramBoolean)
  {
    if (!this.mediaWebpage) {
      return;
    }
    String str = null;
    Object localObject = null;
    if (this.inlineResult != null) {
      if ((this.inlineResult.document instanceof TLRPC.TL_document))
      {
        str = FileLoader.getAttachFileName(this.inlineResult.document);
        localObject = FileLoader.getPathToAttach(this.inlineResult.document);
      }
    }
    while (str == null)
    {
      this.radialProgress.setBackground(null, false, false);
      return;
      if ((this.inlineResult.photo instanceof TLRPC.TL_photo))
      {
        localObject = FileLoader.getClosestPhotoSizeWithSize(this.inlineResult.photo.sizes, AndroidUtilities.getPhotoSize(), true);
        str = FileLoader.getAttachFileName((TLObject)localObject);
        localObject = FileLoader.getPathToAttach((TLObject)localObject);
      }
      else if (this.inlineResult.content_url != null)
      {
        str = Utilities.MD5(this.inlineResult.content_url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content_url, "jpg");
        localObject = new File(FileLoader.getInstance().getDirectory(4), str);
      }
      else if (this.inlineResult.thumb_url != null)
      {
        str = Utilities.MD5(this.inlineResult.thumb_url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.thumb_url, "jpg");
        localObject = new File(FileLoader.getInstance().getDirectory(4), str);
        continue;
        if (this.documentAttach != null)
        {
          str = FileLoader.getAttachFileName(this.documentAttach);
          localObject = FileLoader.getPathToAttach(this.documentAttach);
        }
      }
    }
    if ((((File)localObject).exists()) && (((File)localObject).length() == 0L)) {
      ((File)localObject).delete();
    }
    if (!((File)localObject).exists())
    {
      MediaController.getInstance().addLoadingFileObserver(str, this);
      this.buttonState = 1;
      localObject = ImageLoader.getInstance().getFileProgress(str);
      if (localObject != null) {}
      for (float f = ((Float)localObject).floatValue();; f = 0.0F)
      {
        this.radialProgress.setProgress(f, false);
        this.radialProgress.setBackground(getDrawableForCurrentState(), true, paramBoolean);
        invalidate();
        return;
      }
    }
    MediaController.getInstance().removeLoadingFileObserver(this);
    this.buttonState = -1;
    this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
    invalidate();
  }
  
  public static abstract interface ContextLinkCellDelegate
  {
    public abstract void didPressedImage(ContextLinkCell paramContextLinkCell);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Cells\ContextLinkCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */