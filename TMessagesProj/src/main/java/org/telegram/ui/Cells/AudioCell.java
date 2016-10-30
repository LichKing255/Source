package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.AudioEntry;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;

public class AudioCell
  extends FrameLayout
{
  private static Paint paint;
  private MediaController.AudioEntry audioEntry;
  private TextView authorTextView;
  private CheckBox checkBox;
  private AudioCellDelegate delegate;
  private TextView genreTextView;
  private boolean needDivider;
  private ImageView playButton;
  private TextView timeTextView;
  private TextView titleTextView;
  
  public AudioCell(Context paramContext)
  {
    super(paramContext);
    if (paint == null)
    {
      paint = new Paint();
      paint.setColor(-2500135);
      paint.setStrokeWidth(1.0F);
    }
    this.playButton = new ImageView(paramContext);
    this.playButton.setScaleType(ImageView.ScaleType.CENTER);
    Object localObject = this.playButton;
    int i;
    float f1;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label851;
      }
      f1 = 0.0F;
      label84:
      if (!LocaleController.isRTL) {
        break label857;
      }
      f2 = 13.0F;
      label93:
      addView((View)localObject, LayoutHelper.createFrame(46, 46.0F, i | 0x30, f1, 13.0F, f2, 0.0F));
      this.playButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (AudioCell.this.audioEntry != null)
          {
            if ((!MediaController.getInstance().isPlayingAudio(AudioCell.this.audioEntry.messageObject)) || (MediaController.getInstance().isAudioPaused())) {
              break label68;
            }
            MediaController.getInstance().pauseAudio(AudioCell.this.audioEntry.messageObject);
            AudioCell.this.playButton.setImageResource(2130837614);
          }
          label68:
          do
          {
            do
            {
              return;
              paramAnonymousView = new ArrayList();
              paramAnonymousView.add(AudioCell.this.audioEntry.messageObject);
            } while (!MediaController.getInstance().setPlaylist(paramAnonymousView, AudioCell.this.audioEntry.messageObject));
            AudioCell.this.playButton.setImageResource(2130837613);
          } while (AudioCell.this.delegate == null);
          AudioCell.this.delegate.startedPlayingAudio(AudioCell.this.audioEntry.messageObject);
        }
      });
      this.titleTextView = new TextView(paramContext);
      this.titleTextView.setTextColor(-14606047);
      this.titleTextView.setTextSize(1, 16.0F);
      this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.titleTextView.setLines(1);
      this.titleTextView.setMaxLines(1);
      this.titleTextView.setSingleLine(true);
      this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.titleTextView;
      if (!LocaleController.isRTL) {
        break label862;
      }
      i = 5;
      label223:
      ((TextView)localObject).setGravity(i | 0x30);
      localObject = this.titleTextView;
      if (!LocaleController.isRTL) {
        break label868;
      }
      i = 5;
      label248:
      if (!LocaleController.isRTL) {
        break label874;
      }
      f1 = 50.0F;
      label257:
      if (!LocaleController.isRTL) {
        break label880;
      }
      f2 = 72.0F;
      label266:
      addView((View)localObject, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 7.0F, f2, 0.0F));
      this.genreTextView = new TextView(paramContext);
      this.genreTextView.setTextColor(-7697782);
      this.genreTextView.setTextSize(1, 14.0F);
      this.genreTextView.setLines(1);
      this.genreTextView.setMaxLines(1);
      this.genreTextView.setSingleLine(true);
      this.genreTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.genreTextView;
      if (!LocaleController.isRTL) {
        break label886;
      }
      i = 5;
      label368:
      ((TextView)localObject).setGravity(i | 0x30);
      localObject = this.genreTextView;
      if (!LocaleController.isRTL) {
        break label892;
      }
      i = 5;
      label393:
      if (!LocaleController.isRTL) {
        break label898;
      }
      f1 = 50.0F;
      label402:
      if (!LocaleController.isRTL) {
        break label904;
      }
      f2 = 72.0F;
      label411:
      addView((View)localObject, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 28.0F, f2, 0.0F));
      this.genreTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.authorTextView = new TextView(paramContext);
      this.authorTextView.setTextColor(-7697782);
      this.authorTextView.setTextSize(1, 14.0F);
      this.authorTextView.setLines(1);
      this.authorTextView.setMaxLines(1);
      this.authorTextView.setSingleLine(true);
      this.authorTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.authorTextView;
      if (!LocaleController.isRTL) {
        break label910;
      }
      i = 5;
      label525:
      ((TextView)localObject).setGravity(i | 0x30);
      localObject = this.authorTextView;
      if (!LocaleController.isRTL) {
        break label916;
      }
      i = 5;
      label550:
      if (!LocaleController.isRTL) {
        break label922;
      }
      f1 = 50.0F;
      label559:
      if (!LocaleController.isRTL) {
        break label928;
      }
      f2 = 72.0F;
      label568:
      addView((View)localObject, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 44.0F, f2, 0.0F));
      this.authorTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.timeTextView = new TextView(paramContext);
      this.timeTextView.setTextColor(-6710887);
      this.timeTextView.setTextSize(1, 13.0F);
      this.timeTextView.setLines(1);
      this.timeTextView.setMaxLines(1);
      this.timeTextView.setSingleLine(true);
      this.timeTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.timeTextView;
      if (!LocaleController.isRTL) {
        break label934;
      }
      i = 3;
      label682:
      ((TextView)localObject).setGravity(i | 0x30);
      localObject = this.timeTextView;
      if (!LocaleController.isRTL) {
        break label940;
      }
      i = 3;
      label707:
      if (!LocaleController.isRTL) {
        break label946;
      }
      f1 = 18.0F;
      label716:
      if (!LocaleController.isRTL) {
        break label951;
      }
      f2 = 0.0F;
      label724:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 11.0F, f2, 0.0F));
      this.timeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.checkBox = new CheckBox(paramContext, 2130838105);
      this.checkBox.setVisibility(0);
      this.checkBox.setColor(-14043401);
      paramContext = this.checkBox;
      if (!LocaleController.isRTL) {
        break label957;
      }
      i = j;
      label805:
      if (!LocaleController.isRTL) {
        break label963;
      }
      f1 = 18.0F;
      label814:
      if (!LocaleController.isRTL) {
        break label968;
      }
    }
    label851:
    label857:
    label862:
    label868:
    label874:
    label880:
    label886:
    label892:
    label898:
    label904:
    label910:
    label916:
    label922:
    label928:
    label934:
    label940:
    label946:
    label951:
    label957:
    label963:
    label968:
    for (float f2 = 0.0F;; f2 = 18.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(22, 22.0F, i | 0x30, f1, 39.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      f1 = 13.0F;
      break label84;
      f2 = 0.0F;
      break label93;
      i = 3;
      break label223;
      i = 3;
      break label248;
      f1 = 72.0F;
      break label257;
      f2 = 50.0F;
      break label266;
      i = 3;
      break label368;
      i = 3;
      break label393;
      f1 = 72.0F;
      break label402;
      f2 = 50.0F;
      break label411;
      i = 3;
      break label525;
      i = 3;
      break label550;
      f1 = 72.0F;
      break label559;
      f2 = 50.0F;
      break label568;
      i = 5;
      break label682;
      i = 5;
      break label707;
      f1 = 0.0F;
      break label716;
      f2 = 18.0F;
      break label724;
      i = 5;
      break label805;
      f1 = 0.0F;
      break label814;
    }
  }
  
  public MediaController.AudioEntry getAudioEntry()
  {
    return this.audioEntry;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(AndroidUtilities.dp(72.0F), getHeight() - 1, getWidth(), getHeight() - 1, paint);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = AndroidUtilities.dp(72.0F);
    if (this.needDivider) {}
    for (paramInt2 = 1;; paramInt2 = 0)
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(paramInt2 + i, 1073741824));
      return;
    }
  }
  
  public void setAudio(MediaController.AudioEntry paramAudioEntry, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.audioEntry = paramAudioEntry;
    this.titleTextView.setText(this.audioEntry.title);
    this.genreTextView.setText(this.audioEntry.genre);
    this.authorTextView.setText(this.audioEntry.author);
    this.timeTextView.setText(String.format("%d:%02d", new Object[] { Integer.valueOf(this.audioEntry.duration / 60), Integer.valueOf(this.audioEntry.duration % 60) }));
    paramAudioEntry = this.playButton;
    int i;
    if ((MediaController.getInstance().isPlayingAudio(this.audioEntry.messageObject)) && (!MediaController.getInstance().isAudioPaused()))
    {
      i = 2130837613;
      paramAudioEntry.setImageResource(i);
      this.needDivider = paramBoolean1;
      if (paramBoolean1) {
        break label170;
      }
    }
    label170:
    for (paramBoolean1 = true;; paramBoolean1 = false)
    {
      setWillNotDraw(paramBoolean1);
      this.checkBox.setChecked(paramBoolean2, false);
      return;
      i = 2130837614;
      break;
    }
  }
  
  public void setChecked(boolean paramBoolean)
  {
    this.checkBox.setChecked(paramBoolean, true);
  }
  
  public void setDelegate(AudioCellDelegate paramAudioCellDelegate)
  {
    this.delegate = paramAudioCellDelegate;
  }
  
  public static abstract interface AudioCellDelegate
  {
    public abstract void startedPlayingAudio(MessageObject paramMessageObject);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Cells\AudioCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */