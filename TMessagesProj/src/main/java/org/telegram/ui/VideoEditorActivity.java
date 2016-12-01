package org.telegram.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.media.MediaCodecInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.VideoSeekBarView;
import org.telegram.ui.Components.VideoSeekBarView.SeekBarDelegate;
import org.telegram.ui.Components.VideoTimelineView;
import org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate;
import org.telegram.ui.Supergram.Theming.MihanTheme;

@TargetApi(16)
public class VideoEditorActivity
  extends BaseFragment
  implements TextureView.SurfaceTextureListener, NotificationCenter.NotificationCenterDelegate
{
  private long audioFramesSize = 0L;
  private int bitrate = 0;
  private CheckBox compressVideo = null;
  private View controlView = null;
  private boolean created = false;
  Context ctx;
  private VideoEditorActivityDelegate delegate;
  private TextView editedSizeTextView = null;
  private ImageView emojiButton;
  private long endTime = 0L;
  private long esimatedDuration = 0L;
  private int estimatedSize = 0;
  private float lastProgress = 0.0F;
  private EditText messageEditText;
  private boolean needSeek = false;
  private int originalBitrate = 0;
  private int originalHeight = 0;
  private long originalSize = 0L;
  private TextView originalSizeTextView = null;
  private int originalWidth = 0;
  private ImageView playButton = null;
  private boolean playerPrepared = false;
  private Runnable progressRunnable = new Runnable()
  {
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 16	org/telegram/ui/VideoEditorActivity$1:this$0	Lorg/telegram/ui/VideoEditorActivity;
      //   4: invokestatic 27	org/telegram/ui/VideoEditorActivity:access$000	(Lorg/telegram/ui/VideoEditorActivity;)Ljava/lang/Object;
      //   7: astore_3
      //   8: aload_3
      //   9: monitorenter
      //   10: aload_0
      //   11: getfield 16	org/telegram/ui/VideoEditorActivity$1:this$0	Lorg/telegram/ui/VideoEditorActivity;
      //   14: invokestatic 31	org/telegram/ui/VideoEditorActivity:access$100	(Lorg/telegram/ui/VideoEditorActivity;)Landroid/media/MediaPlayer;
      //   17: ifnull +48 -> 65
      //   20: aload_0
      //   21: getfield 16	org/telegram/ui/VideoEditorActivity$1:this$0	Lorg/telegram/ui/VideoEditorActivity;
      //   24: invokestatic 31	org/telegram/ui/VideoEditorActivity:access$100	(Lorg/telegram/ui/VideoEditorActivity;)Landroid/media/MediaPlayer;
      //   27: invokevirtual 37	android/media/MediaPlayer:isPlaying	()Z
      //   30: istore_2
      //   31: iload_2
      //   32: ifeq +33 -> 65
      //   35: iconst_1
      //   36: istore_1
      //   37: aload_3
      //   38: monitorexit
      //   39: iload_1
      //   40: ifne +51 -> 91
      //   43: aload_0
      //   44: getfield 16	org/telegram/ui/VideoEditorActivity$1:this$0	Lorg/telegram/ui/VideoEditorActivity;
      //   47: invokestatic 27	org/telegram/ui/VideoEditorActivity:access$000	(Lorg/telegram/ui/VideoEditorActivity;)Ljava/lang/Object;
      //   50: astore_3
      //   51: aload_3
      //   52: monitorenter
      //   53: aload_0
      //   54: getfield 16	org/telegram/ui/VideoEditorActivity$1:this$0	Lorg/telegram/ui/VideoEditorActivity;
      //   57: aconst_null
      //   58: invokestatic 41	org/telegram/ui/VideoEditorActivity:access$702	(Lorg/telegram/ui/VideoEditorActivity;Ljava/lang/Thread;)Ljava/lang/Thread;
      //   61: pop
      //   62: aload_3
      //   63: monitorexit
      //   64: return
      //   65: iconst_0
      //   66: istore_1
      //   67: goto -30 -> 37
      //   70: astore 4
      //   72: iconst_0
      //   73: istore_1
      //   74: ldc 43
      //   76: aload 4
      //   78: invokestatic 49	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   81: goto -44 -> 37
      //   84: astore 4
      //   86: aload_3
      //   87: monitorexit
      //   88: aload 4
      //   90: athrow
      //   91: new 10	org/telegram/ui/VideoEditorActivity$1$1
      //   94: dup
      //   95: aload_0
      //   96: invokespecial 52	org/telegram/ui/VideoEditorActivity$1$1:<init>	(Lorg/telegram/ui/VideoEditorActivity$1;)V
      //   99: invokestatic 58	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
      //   102: ldc2_w 59
      //   105: invokestatic 66	java/lang/Thread:sleep	(J)V
      //   108: goto -108 -> 0
      //   111: astore_3
      //   112: ldc 43
      //   114: aload_3
      //   115: invokestatic 49	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   118: goto -118 -> 0
      //   121: astore 4
      //   123: aload_3
      //   124: monitorexit
      //   125: aload 4
      //   127: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	128	0	this	1
      //   36	38	1	i	int
      //   30	2	2	bool	boolean
      //   111	13	3	localException1	Exception
      //   70	7	4	localException2	Exception
      //   84	5	4	localObject2	Object
      //   121	5	4	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   10	31	70	java/lang/Exception
      //   10	31	84	finally
      //   37	39	84	finally
      //   74	81	84	finally
      //   86	88	84	finally
      //   102	108	111	java/lang/Exception
      //   53	64	121	finally
      //   123	125	121	finally
    }
  };
  private int resultHeight = 0;
  private int resultWidth = 0;
  private int rotationValue = 0;
  private long startTime = 0L;
  private final Object sync = new Object();
  private View textContainerView = null;
  private LinearLayout textFieldContainer;
  private TextureView textureView = null;
  private Thread thread = null;
  private View videoContainerView = null;
  private float videoDuration = 0.0F;
  private long videoFramesSize = 0L;
  private String videoPath = null;
  private MediaPlayer videoPlayer = null;
  private VideoSeekBarView videoSeekBarView = null;
  private VideoTimelineView videoTimelineView = null;
  
  public VideoEditorActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.videoPath = paramBundle.getString("videoPath");
  }
  
  private int calculateEstimatedSize(float paramFloat)
  {
    int i = (int)((float)(this.audioFramesSize + this.videoFramesSize) * paramFloat);
    return i + i / 32768 * 16;
  }
  
  private void fixLayout()
  {
    if (this.fragmentView == null) {
      return;
    }
    this.fragmentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
    {
      public void onGlobalLayout()
      {
        VideoEditorActivity.this.fixLayoutInternal();
        if (VideoEditorActivity.this.fragmentView != null)
        {
          if (Build.VERSION.SDK_INT < 16) {
            VideoEditorActivity.this.fragmentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
          }
        }
        else {
          return;
        }
        VideoEditorActivity.this.fragmentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
      }
    });
  }
  
  private void fixLayoutInternal()
  {
    int j = 20;
    if (getParentActivity() == null) {
      return;
    }
    if ((!AndroidUtilities.isTablet()) && (getParentActivity().getResources().getConfiguration().orientation == 2))
    {
      localLayoutParams = (FrameLayout.LayoutParams)this.videoContainerView.getLayoutParams();
      localLayoutParams.topMargin = AndroidUtilities.dp(16.0F);
      localLayoutParams.bottomMargin = AndroidUtilities.dp(16.0F);
      localLayoutParams.width = (AndroidUtilities.displaySize.x / 3 - AndroidUtilities.dp(24.0F));
      localLayoutParams.leftMargin = AndroidUtilities.dp(16.0F);
      this.videoContainerView.setLayoutParams(localLayoutParams);
      localLayoutParams = (FrameLayout.LayoutParams)this.controlView.getLayoutParams();
      localLayoutParams.topMargin = AndroidUtilities.dp(16.0F);
      localLayoutParams.bottomMargin = 0;
      localLayoutParams.width = (AndroidUtilities.displaySize.x / 3 * 2 - AndroidUtilities.dp(32.0F));
      localLayoutParams.leftMargin = (AndroidUtilities.displaySize.x / 3 + AndroidUtilities.dp(16.0F));
      localLayoutParams.gravity = 48;
      this.controlView.setLayoutParams(localLayoutParams);
      localLayoutParams = (FrameLayout.LayoutParams)this.textContainerView.getLayoutParams();
      localLayoutParams.width = (AndroidUtilities.displaySize.x / 3 * 2 - AndroidUtilities.dp(32.0F));
      localLayoutParams.leftMargin = (AndroidUtilities.displaySize.x / 3 + AndroidUtilities.dp(16.0F));
      localLayoutParams.rightMargin = AndroidUtilities.dp(16.0F);
      localLayoutParams.bottomMargin = AndroidUtilities.dp(16.0F);
      this.textContainerView.setLayoutParams(localLayoutParams);
      fixVideoSize();
      this.videoTimelineView.clearFrames();
      return;
    }
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.videoContainerView.getLayoutParams();
    localLayoutParams.topMargin = AndroidUtilities.dp(16.0F);
    if (this.compressVideo.getVisibility() == 0)
    {
      i = 20;
      label307:
      localLayoutParams.bottomMargin = AndroidUtilities.dp(i + 260);
      localLayoutParams.width = -1;
      localLayoutParams.leftMargin = 0;
      this.videoContainerView.setLayoutParams(localLayoutParams);
      localLayoutParams = (FrameLayout.LayoutParams)this.controlView.getLayoutParams();
      localLayoutParams.topMargin = 0;
      localLayoutParams.leftMargin = 0;
      if (this.compressVideo.getVisibility() != 0) {
        break label465;
      }
    }
    label465:
    for (int i = j;; i = 0)
    {
      localLayoutParams.bottomMargin = AndroidUtilities.dp(i + 150);
      localLayoutParams.width = -1;
      localLayoutParams.gravity = 80;
      this.controlView.setLayoutParams(localLayoutParams);
      localLayoutParams = (FrameLayout.LayoutParams)this.textContainerView.getLayoutParams();
      localLayoutParams.width = -1;
      localLayoutParams.leftMargin = AndroidUtilities.dp(16.0F);
      localLayoutParams.rightMargin = AndroidUtilities.dp(16.0F);
      localLayoutParams.bottomMargin = AndroidUtilities.dp(16.0F);
      this.textContainerView.setLayoutParams(localLayoutParams);
      break;
      i = 0;
      break label307;
    }
  }
  
  private void fixVideoSize()
  {
    if ((this.fragmentView == null) || (getParentActivity() == null)) {}
    label57:
    label76:
    label101:
    label126:
    label238:
    label244:
    label342:
    label351:
    label360:
    label369:
    for (;;)
    {
      return;
      int i;
      int k;
      int j;
      int m;
      float f3;
      if (AndroidUtilities.isTablet())
      {
        i = AndroidUtilities.dp(472.0F);
        if (!AndroidUtilities.isTablet()) {
          break label244;
        }
        k = AndroidUtilities.dp(490.0F);
        if (this.compressVideo.getVisibility() != 0) {
          break label238;
        }
        j = 20;
        i -= AndroidUtilities.dp(j + 276);
        j = k;
        if ((this.rotationValue != 90) && (this.rotationValue != 270)) {
          break label342;
        }
        k = this.originalHeight;
        if ((this.rotationValue != 90) && (this.rotationValue != 270)) {
          break label351;
        }
        m = this.originalWidth;
        float f1 = j / k;
        float f2 = i / m;
        f3 = k / m;
        if (f1 <= f2) {
          break label360;
        }
        j = (int)(i * f3);
      }
      for (;;)
      {
        if (this.textureView == null) {
          break label369;
        }
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.textureView.getLayoutParams();
        localLayoutParams.width = j;
        localLayoutParams.height = i;
        localLayoutParams.leftMargin = 0;
        localLayoutParams.topMargin = 0;
        this.textureView.setLayoutParams(localLayoutParams);
        return;
        i = AndroidUtilities.displaySize.y - AndroidUtilities.statusBarHeight - ActionBar.getCurrentActionBarHeight();
        break;
        j = 0;
        break label57;
        if (getParentActivity().getResources().getConfiguration().orientation == 2)
        {
          j = AndroidUtilities.displaySize.x / 3 - AndroidUtilities.dp(24.0F);
          i -= AndroidUtilities.dp(32.0F);
          break label76;
        }
        k = AndroidUtilities.displaySize.x;
        if (this.compressVideo.getVisibility() == 0) {}
        for (j = 20;; j = 0)
        {
          i -= AndroidUtilities.dp(j + 276);
          j = k;
          break;
        }
        k = this.originalWidth;
        break label101;
        m = this.originalHeight;
        break label126;
        i = (int)(j / f3);
      }
    }
  }
  
  private void onPlayComplete()
  {
    if (this.playButton != null) {
      this.playButton.setImageResource(2130838199);
    }
    if ((this.videoSeekBarView != null) && (this.videoTimelineView != null)) {
      this.videoSeekBarView.setProgress(this.videoTimelineView.getLeftProgress());
    }
    try
    {
      if ((this.videoPlayer != null) && (this.videoTimelineView != null)) {
        this.videoPlayer.seekTo((int)(this.videoTimelineView.getLeftProgress() * this.videoDuration));
      }
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  private void play()
  {
    if ((this.videoPlayer == null) || (!this.playerPrepared)) {
      return;
    }
    if (this.videoPlayer.isPlaying())
    {
      this.videoPlayer.pause();
      this.playButton.setImageResource(2130838199);
      return;
    }
    try
    {
      this.playButton.setImageDrawable(null);
      this.lastProgress = 0.0F;
      if (this.needSeek)
      {
        this.videoPlayer.seekTo((int)(this.videoDuration * this.videoSeekBarView.getProgress()));
        this.needSeek = false;
      }
      this.videoPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener()
      {
        public void onSeekComplete(MediaPlayer paramAnonymousMediaPlayer)
        {
          float f2 = VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration;
          float f3 = VideoEditorActivity.this.videoTimelineView.getRightProgress() * VideoEditorActivity.this.videoDuration;
          float f1 = f2;
          if (f2 == f3) {
            f1 = f3 - 0.01F;
          }
          VideoEditorActivity.access$402(VideoEditorActivity.this, (VideoEditorActivity.this.videoPlayer.getCurrentPosition() - f1) / (f3 - f1));
          f1 = VideoEditorActivity.this.videoTimelineView.getRightProgress();
          f2 = VideoEditorActivity.this.videoTimelineView.getLeftProgress();
          VideoEditorActivity.access$402(VideoEditorActivity.this, VideoEditorActivity.this.videoTimelineView.getLeftProgress() + VideoEditorActivity.this.lastProgress * (f1 - f2));
          VideoEditorActivity.this.videoSeekBarView.setProgress(VideoEditorActivity.this.lastProgress);
        }
      });
      this.videoPlayer.start();
      synchronized (this.sync)
      {
        if (this.thread == null)
        {
          this.thread = new Thread(this.progressRunnable);
          this.thread.start();
        }
        return;
      }
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  private boolean processOpenVideo()
  {
    Object localObject2;
    int j;
    try
    {
      this.originalSize = new File(this.videoPath).length();
      localObject2 = new IsoFile(this.videoPath);
      localObject3 = Path.getPaths((Container)localObject2, "/moov/trak/");
      localObject1 = null;
      j = 1;
      i = 1;
      if (Path.getPath((Container)localObject2, "/moov/trak/mdia/minf/stbl/stsd/mp4a/") != null) {
        break label639;
      }
      i = 0;
    }
    catch (Exception localException1)
    {
      Object localObject3;
      Object localObject1;
      FileLog.e("tmessages", localException1);
      return false;
    }
    int i = j;
    if (Path.getPath((Container)localObject2, "/moov/trak/mdia/minf/stbl/stsd/avc1/") == null) {
      i = 0;
    }
    localObject3 = ((List)localObject3).iterator();
    for (;;)
    {
      if (!((Iterator)localObject3).hasNext()) {
        break label671;
      }
      localObject2 = (TrackBox)((Iterator)localObject3).next();
      l1 = 0L;
      l3 = 0L;
      l2 = l1;
      try
      {
        localObject4 = ((TrackBox)localObject2).getMediaBox();
        l2 = l1;
        localMediaHeaderBox = ((MediaBox)localObject4).getMediaHeaderBox();
        l2 = l1;
        localObject4 = ((MediaBox)localObject4).getMediaInformationBox().getSampleTableBox().getSampleSizeBox().getSampleSizes();
        l2 = l1;
        k = localObject4.length;
        j = 0;
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException2);
          l1 = l2;
          l2 = l3;
        }
        this.audioFramesSize += l1;
      }
      l2 = l1;
      this.videoDuration = ((float)localMediaHeaderBox.getDuration() / (float)localMediaHeaderBox.getTimescale());
      f1 = (float)(8L * l1);
      l2 = l1;
      f2 = this.videoDuration;
      l2 = (int)(f1 / f2);
      localObject2 = ((TrackBox)localObject2).getTrackHeaderBox();
      if ((((TrackHeaderBox)localObject2).getWidth() != 0.0D) && (((TrackHeaderBox)localObject2).getHeight() != 0.0D))
      {
        localObject1 = localObject2;
        j = (int)(l2 / 100000L * 100000L);
        this.bitrate = j;
        this.originalBitrate = j;
        if (this.bitrate > 900000) {
          this.bitrate = 900000;
        }
        this.videoFramesSize += l1;
      }
    }
    label600:
    label617:
    label639:
    label671:
    while (localException1 != null) {
      for (;;)
      {
        long l1;
        long l3;
        long l2;
        Object localObject4;
        MediaHeaderBox localMediaHeaderBox;
        int k;
        float f2;
        localObject2 = localException1.getMatrix();
        if (((Matrix)localObject2).equals(Matrix.ROTATE_90))
        {
          this.rotationValue = 90;
          j = (int)localException1.getWidth();
          this.originalWidth = j;
          this.resultWidth = j;
          j = (int)localException1.getHeight();
          this.originalHeight = j;
          this.resultHeight = j;
          if ((this.resultWidth > 640) || (this.resultHeight > 640)) {
            if (this.resultWidth <= this.resultHeight) {
              break label600;
            }
          }
        }
        for (float f1 = 640.0F / this.resultWidth;; f1 = 640.0F / j)
        {
          this.resultWidth = ((int)(this.resultWidth * f1));
          this.resultHeight = ((int)(this.resultHeight * f1));
          if (this.bitrate != 0)
          {
            this.bitrate = ((int)(this.bitrate * Math.max(0.5F, f1)));
            this.videoFramesSize = ((this.bitrate / 8 * this.videoDuration));
          }
          if (i != 0) {
            break label617;
          }
          if (this.resultWidth == this.originalWidth) {
            break label678;
          }
          if (this.resultHeight != this.originalHeight) {
            break label617;
          }
          break label678;
          if (((Matrix)localObject2).equals(Matrix.ROTATE_180))
          {
            this.rotationValue = 180;
            break;
          }
          if (!((Matrix)localObject2).equals(Matrix.ROTATE_270)) {
            break;
          }
          this.rotationValue = 270;
          break;
          j = this.resultHeight;
        }
        this.videoDuration *= 1000.0F;
        updateVideoOriginalInfo();
        updateVideoEditedInfo();
        return true;
        if (i != 0) {
          break;
        }
        return false;
        while (j < k)
        {
          l1 += localObject4[j];
          j += 1;
        }
      }
    }
    return false;
    label678:
    return false;
  }
  
  private void setPlayerSurface()
  {
    if ((this.textureView == null) || (!this.textureView.isAvailable()) || (this.videoPlayer == null)) {}
    for (;;)
    {
      return;
      try
      {
        Surface localSurface = new Surface(this.textureView.getSurfaceTexture());
        this.videoPlayer.setSurface(localSurface);
        if (this.playerPrepared)
        {
          this.videoPlayer.seekTo((int)(this.videoTimelineView.getLeftProgress() * this.videoDuration));
          return;
        }
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  private void updateVideoEditedInfo()
  {
    if (this.editedSizeTextView == null) {
      return;
    }
    this.esimatedDuration = (Math.ceil((this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()) * this.videoDuration));
    int i;
    int j;
    if ((this.compressVideo.getVisibility() == 8) || ((this.compressVideo.getVisibility() == 0) && (!this.compressVideo.isChecked()))) {
      if ((this.rotationValue == 90) || (this.rotationValue == 270))
      {
        i = this.originalHeight;
        if ((this.rotationValue != 90) && (this.rotationValue != 270)) {
          break label304;
        }
        j = this.originalWidth;
        label117:
        this.estimatedSize = ((int)((float)this.originalSize * ((float)this.esimatedDuration / this.videoDuration)));
        if (this.videoTimelineView.getLeftProgress() != 0.0F) {
          break label397;
        }
        this.startTime = -1L;
        label157:
        if (this.videoTimelineView.getRightProgress() != 1.0F) {
          break label421;
        }
      }
    }
    label304:
    label336:
    label389:
    label397:
    label421:
    for (this.endTime = -1L;; this.endTime = ((this.videoTimelineView.getRightProgress() * this.videoDuration) * 1000L))
    {
      String str1 = String.format("%dx%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j) });
      i = (int)(this.esimatedDuration / 1000L / 60L);
      String str2 = String.format("%d:%02d, ~%s", new Object[] { Integer.valueOf(i), Integer.valueOf((int)Math.ceil(this.esimatedDuration / 1000L) - i * 60), AndroidUtilities.formatFileSize(this.estimatedSize) });
      this.editedSizeTextView.setText(String.format("%s, %s", new Object[] { str1, str2 }));
      return;
      i = this.originalWidth;
      break;
      j = this.originalHeight;
      break label117;
      if ((this.rotationValue == 90) || (this.rotationValue == 270))
      {
        i = this.resultHeight;
        if ((this.rotationValue != 90) && (this.rotationValue != 270)) {
          break label389;
        }
      }
      for (j = this.resultWidth;; j = this.resultHeight)
      {
        this.estimatedSize = calculateEstimatedSize((float)this.esimatedDuration / this.videoDuration);
        break;
        i = this.resultWidth;
        break label336;
      }
      this.startTime = ((this.videoTimelineView.getLeftProgress() * this.videoDuration) * 1000L);
      break label157;
    }
  }
  
  private void updateVideoOriginalInfo()
  {
    if (this.originalSizeTextView == null) {
      return;
    }
    int i;
    if ((this.rotationValue == 90) || (this.rotationValue == 270))
    {
      i = this.originalHeight;
      if ((this.rotationValue != 90) && (this.rotationValue != 270)) {
        break label189;
      }
    }
    label189:
    for (int j = this.originalWidth;; j = this.originalHeight)
    {
      String str1 = String.format("%dx%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j) });
      long l = Math.ceil(this.videoDuration);
      i = (int)(l / 1000L / 60L);
      String str2 = String.format("%d:%02d, %s", new Object[] { Integer.valueOf(i), Integer.valueOf((int)Math.ceil(l / 1000L) - i * 60), AndroidUtilities.formatFileSize(this.originalSize) });
      this.originalSizeTextView.setText(String.format("%s, %s", new Object[] { str1, str2 }));
      return;
      i = this.originalWidth;
      break;
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackgroundColor(-13421773);
    this.actionBar.setItemsBackgroundColor(-12763843);
    this.actionBar.setBackButtonImage(2130837810);
    this.actionBar.setTitle(LocaleController.getString("EditVideo", 2131165640));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          VideoEditorActivity.this.finishFragment();
        }
        do
        {
          return;
          if (paramAnonymousInt == 1) {
            for (;;)
            {
              synchronized (VideoEditorActivity.this.sync)
              {
                MediaPlayer localMediaPlayer = VideoEditorActivity.this.videoPlayer;
                if (localMediaPlayer != null) {}
                try
                {
                  VideoEditorActivity.this.videoPlayer.stop();
                  VideoEditorActivity.this.videoPlayer.release();
                  VideoEditorActivity.access$102(VideoEditorActivity.this, null);
                  if (VideoEditorActivity.this.delegate != null)
                  {
                    if ((VideoEditorActivity.this.compressVideo.getVisibility() == 8) || ((VideoEditorActivity.this.compressVideo.getVisibility() == 0) && (!VideoEditorActivity.this.compressVideo.isChecked()))) {
                      VideoEditorActivity.this.delegate.didFinishEditVideo(VideoEditorActivity.this.videoPath, VideoEditorActivity.this.startTime, VideoEditorActivity.this.endTime, VideoEditorActivity.this.originalWidth, VideoEditorActivity.this.originalHeight, VideoEditorActivity.this.rotationValue, VideoEditorActivity.this.originalWidth, VideoEditorActivity.this.originalHeight, VideoEditorActivity.this.originalBitrate, VideoEditorActivity.this.estimatedSize, VideoEditorActivity.this.esimatedDuration, VideoEditorActivity.this.messageEditText.getText().toString());
                    }
                  }
                  else
                  {
                    VideoEditorActivity.this.finishFragment();
                    return;
                  }
                }
                catch (Exception localException)
                {
                  FileLog.e("tmessages", localException);
                  continue;
                }
              }
              VideoEditorActivity.this.delegate.didFinishEditVideo(VideoEditorActivity.this.videoPath, VideoEditorActivity.this.startTime, VideoEditorActivity.this.endTime, VideoEditorActivity.this.resultWidth, VideoEditorActivity.this.resultHeight, VideoEditorActivity.this.rotationValue, VideoEditorActivity.this.originalWidth, VideoEditorActivity.this.originalHeight, VideoEditorActivity.this.bitrate, VideoEditorActivity.this.estimatedSize, VideoEditorActivity.this.esimatedDuration, VideoEditorActivity.this.messageEditText.getText().toString());
            }
          }
        } while (paramAnonymousInt != 2);
        VideoEditorActivity.this.textFieldContainer.setVisibility(0);
        VideoEditorActivity.this.messageEditText.requestFocus();
        AndroidUtilities.showKeyboard(VideoEditorActivity.this.messageEditText);
      }
    });
    Object localObject1 = this.actionBar.createMenu();
    ((ActionBarMenu)localObject1).addItemWithWidth(2, 2130838033, AndroidUtilities.dp(56.0F));
    ((ActionBarMenu)localObject1).addItemWithWidth(1, 2130837844, AndroidUtilities.dp(56.0F));
    this.fragmentView = getParentActivity().getLayoutInflater().inflate(2130903124, null, false);
    this.originalSizeTextView = ((TextView)this.fragmentView.findViewById(2131624241));
    this.editedSizeTextView = ((TextView)this.fragmentView.findViewById(2131624243));
    this.videoContainerView = this.fragmentView.findViewById(2131624233);
    this.textContainerView = this.fragmentView.findViewById(2131624239);
    this.controlView = this.fragmentView.findViewById(2131624236);
    this.compressVideo = ((CheckBox)this.fragmentView.findViewById(2131624244));
    this.compressVideo.setText(LocaleController.getString("CompressVideo", 2131165557));
    localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
    Object localObject2 = this.compressVideo;
    int i;
    if ((this.originalHeight != this.resultHeight) || (this.originalWidth != this.resultWidth))
    {
      i = 0;
      ((CheckBox)localObject2).setVisibility(i);
      this.compressVideo.setChecked(((SharedPreferences)localObject1).getBoolean("compress_video", true));
      this.compressVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
      {
        public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
        {
          paramAnonymousCompoundButton = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
          paramAnonymousCompoundButton.putBoolean("compress_video", paramAnonymousBoolean);
          paramAnonymousCompoundButton.commit();
          VideoEditorActivity.this.updateVideoEditedInfo();
        }
      });
      if (Build.VERSION.SDK_INT >= 18) {}
    }
    for (;;)
    {
      try
      {
        localObject1 = MediaController.selectCodec("video/avc");
        if (localObject1 != null) {
          continue;
        }
        this.compressVideo.setVisibility(8);
      }
      catch (Exception localException)
      {
        this.compressVideo.setVisibility(8);
        FileLog.e("tmessages", localException);
        continue;
        if (MediaController.selectColorFormat(localException, "video/avc") != 0) {
          continue;
        }
        this.compressVideo.setVisibility(8);
        continue;
      }
      ((TextView)this.fragmentView.findViewById(2131624240)).setText(LocaleController.getString("OriginalVideo", 2131166129));
      ((TextView)this.fragmentView.findViewById(2131624242)).setText(LocaleController.getString("EditedVideo", 2131165642));
      this.videoTimelineView = ((VideoTimelineView)this.fragmentView.findViewById(2131624238));
      this.videoTimelineView.setVideoPath(this.videoPath);
      this.videoTimelineView.setDelegate(new VideoTimelineView.VideoTimelineViewDelegate()
      {
        public void onLeftProgressChanged(float paramAnonymousFloat)
        {
          if ((VideoEditorActivity.this.videoPlayer == null) || (!VideoEditorActivity.this.playerPrepared)) {
            return;
          }
          try
          {
            if (VideoEditorActivity.this.videoPlayer.isPlaying())
            {
              VideoEditorActivity.this.videoPlayer.pause();
              VideoEditorActivity.this.playButton.setImageResource(2130838199);
            }
            VideoEditorActivity.this.videoPlayer.setOnSeekCompleteListener(null);
            VideoEditorActivity.this.videoPlayer.seekTo((int)(VideoEditorActivity.this.videoDuration * paramAnonymousFloat));
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e("tmessages", localException);
            }
          }
          VideoEditorActivity.access$2702(VideoEditorActivity.this, true);
          VideoEditorActivity.this.videoSeekBarView.setProgress(VideoEditorActivity.this.videoTimelineView.getLeftProgress());
          VideoEditorActivity.this.updateVideoEditedInfo();
        }
        
        public void onRifhtProgressChanged(float paramAnonymousFloat)
        {
          if ((VideoEditorActivity.this.videoPlayer == null) || (!VideoEditorActivity.this.playerPrepared)) {
            return;
          }
          try
          {
            if (VideoEditorActivity.this.videoPlayer.isPlaying())
            {
              VideoEditorActivity.this.videoPlayer.pause();
              VideoEditorActivity.this.playButton.setImageResource(2130838199);
            }
            VideoEditorActivity.this.videoPlayer.setOnSeekCompleteListener(null);
            VideoEditorActivity.this.videoPlayer.seekTo((int)(VideoEditorActivity.this.videoDuration * paramAnonymousFloat));
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e("tmessages", localException);
            }
          }
          VideoEditorActivity.access$2702(VideoEditorActivity.this, true);
          VideoEditorActivity.this.videoSeekBarView.setProgress(VideoEditorActivity.this.videoTimelineView.getLeftProgress());
          VideoEditorActivity.this.updateVideoEditedInfo();
        }
      });
      this.videoSeekBarView = ((VideoSeekBarView)this.fragmentView.findViewById(2131624237));
      this.videoSeekBarView.delegate = new VideoSeekBarView.SeekBarDelegate()
      {
        public void onSeekBarDrag(float paramAnonymousFloat)
        {
          float f;
          if (paramAnonymousFloat < VideoEditorActivity.this.videoTimelineView.getLeftProgress())
          {
            f = VideoEditorActivity.this.videoTimelineView.getLeftProgress();
            VideoEditorActivity.this.videoSeekBarView.setProgress(f);
          }
          while ((VideoEditorActivity.this.videoPlayer == null) || (!VideoEditorActivity.this.playerPrepared))
          {
            return;
            f = paramAnonymousFloat;
            if (paramAnonymousFloat > VideoEditorActivity.this.videoTimelineView.getRightProgress())
            {
              f = VideoEditorActivity.this.videoTimelineView.getRightProgress();
              VideoEditorActivity.this.videoSeekBarView.setProgress(f);
            }
          }
          if (VideoEditorActivity.this.videoPlayer.isPlaying()) {
            try
            {
              VideoEditorActivity.this.videoPlayer.seekTo((int)(VideoEditorActivity.this.videoDuration * f));
              VideoEditorActivity.access$402(VideoEditorActivity.this, f);
              return;
            }
            catch (Exception localException)
            {
              FileLog.e("tmessages", localException);
              return;
            }
          }
          VideoEditorActivity.access$402(VideoEditorActivity.this, f);
          VideoEditorActivity.access$2702(VideoEditorActivity.this, true);
        }
      };
      this.playButton = ((ImageView)this.fragmentView.findViewById(2131624235));
      this.playButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          VideoEditorActivity.this.play();
        }
      });
      this.textureView = ((TextureView)this.fragmentView.findViewById(2131624234));
      this.textureView.setSurfaceTextureListener(this);
      updateVideoOriginalInfo();
      updateVideoEditedInfo();
      this.textFieldContainer = ((LinearLayout)this.fragmentView.findViewById(2131624245));
      this.textFieldContainer.setVisibility(8);
      localObject1 = new FrameLayout(paramContext);
      this.textFieldContainer.addView((View)localObject1, LayoutHelper.createLinear(0, -2, 1.0F));
      this.emojiButton = new ImageView(paramContext);
      this.emojiButton.setImageResource(2130837873);
      this.emojiButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      this.emojiButton.setPadding(AndroidUtilities.dp(4.0F), AndroidUtilities.dp(1.0F), 0, 0);
      ((FrameLayout)localObject1).addView(this.emojiButton, LayoutHelper.createFrame(48, 48, 83));
      this.messageEditText = new EditText(paramContext);
      this.messageEditText.setHint(LocaleController.getString("AddCaption", 2131165294));
      this.messageEditText.setMaxLines(4);
      this.messageEditText.setHorizontallyScrolling(false);
      this.messageEditText.setTextSize(1, 18.0F);
      this.messageEditText.setGravity(80);
      this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0F), 0, AndroidUtilities.dp(12.0F));
      this.messageEditText.setBackgroundDrawable(null);
      AndroidUtilities.clearCursorDrawable(this.messageEditText);
      this.messageEditText.setTextColor(-1);
      this.messageEditText.setHintTextColor(-1291845633);
      paramContext = new InputFilter.LengthFilter(140);
      this.messageEditText.setFilters(new InputFilter[] { paramContext });
      this.messageEditText.setTypeface(MihanTheme.getMihanTypeFace());
      ((FrameLayout)localObject1).addView(this.messageEditText, LayoutHelper.createFrame(-1, -2.0F, 83, 52.0F, 0.0F, 6.0F, 0.0F));
      return this.fragmentView;
      i = 8;
      break;
      localObject2 = ((MediaCodecInfo)localObject1).getName();
      if ((!((String)localObject2).equals("OMX.google.h264.encoder")) && (!((String)localObject2).equals("OMX.ST.VFM.H264Enc")) && (!((String)localObject2).equals("OMX.Exynos.avc.enc")) && (!((String)localObject2).equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER")) && (!((String)localObject2).equals("OMX.MARVELL.VIDEO.H264ENCODER")) && (!((String)localObject2).equals("OMX.k3.video.encoder.avc")) && (!((String)localObject2).equals("OMX.TI.DUCATI1.VIDEO.H264E"))) {
        continue;
      }
      this.compressVideo.setVisibility(8);
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.closeChats) {
      removeSelfFromStack();
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    fixLayout();
  }
  
  public boolean onFragmentCreate()
  {
    if (this.created) {
      return true;
    }
    if ((this.videoPath == null) || (!processOpenVideo())) {
      return false;
    }
    this.videoPlayer = new MediaPlayer();
    this.videoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
    {
      public void onCompletion(MediaPlayer paramAnonymousMediaPlayer)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            VideoEditorActivity.this.onPlayComplete();
          }
        });
      }
    });
    this.videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
    {
      public void onPrepared(MediaPlayer paramAnonymousMediaPlayer)
      {
        VideoEditorActivity.access$802(VideoEditorActivity.this, true);
        if ((VideoEditorActivity.this.videoTimelineView != null) && (VideoEditorActivity.this.videoPlayer != null)) {
          VideoEditorActivity.this.videoPlayer.seekTo((int)(VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration));
        }
      }
    });
    try
    {
      this.videoPlayer.setDataSource(this.videoPath);
      this.videoPlayer.prepareAsync();
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
      this.created = true;
      return super.onFragmentCreate();
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return false;
  }
  
  public void onFragmentDestroy()
  {
    if (this.videoTimelineView != null) {
      this.videoTimelineView.destroy();
    }
    if (this.videoPlayer != null) {}
    try
    {
      this.videoPlayer.stop();
      this.videoPlayer.release();
      this.videoPlayer = null;
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
      super.onFragmentDestroy();
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
  
  public void onResume()
  {
    super.onResume();
    fixLayoutInternal();
  }
  
  public void onSurfaceTextureAvailable(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
  {
    setPlayerSurface();
  }
  
  public boolean onSurfaceTextureDestroyed(SurfaceTexture paramSurfaceTexture)
  {
    if (this.videoPlayer == null) {
      return true;
    }
    this.videoPlayer.setDisplay(null);
    return true;
  }
  
  public void onSurfaceTextureSizeChanged(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2) {}
  
  public void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture) {}
  
  public void setDelegate(VideoEditorActivityDelegate paramVideoEditorActivityDelegate)
  {
    this.delegate = paramVideoEditorActivityDelegate;
  }
  
  public static abstract interface VideoEditorActivityDelegate
  {
    public abstract void didFinishEditVideo(String paramString1, long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, long paramLong3, long paramLong4, String paramString2);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\VideoEditorActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */