package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class AnimatedFileDrawable
  extends BitmapDrawable
  implements Animatable
{
  private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2, new ThreadPoolExecutor.DiscardPolicy());
  private static final Handler uiHandler = new Handler(Looper.getMainLooper());
  private boolean applyTransformation;
  private Bitmap backgroundBitmap;
  private BitmapShader backgroundShader;
  private RectF bitmapRect = new RectF();
  private boolean decoderCreated;
  private boolean destroyWhenDone;
  private final Rect dstRect = new Rect();
  private int invalidateAfter = 50;
  private volatile boolean isRecycled;
  private volatile boolean isRunning;
  private long lastFrameTime;
  private int lastTimeStamp;
  private Runnable loadFrameRunnable = new Runnable()
  {
    public void run()
    {
      if (!AnimatedFileDrawable.this.isRecycled) {
        if ((!AnimatedFileDrawable.this.decoderCreated) && (AnimatedFileDrawable.this.nativePtr == 0))
        {
          AnimatedFileDrawable.access$302(AnimatedFileDrawable.this, AnimatedFileDrawable.createDecoder(AnimatedFileDrawable.this.path.getAbsolutePath(), AnimatedFileDrawable.this.metaData));
          AnimatedFileDrawable.access$1402(AnimatedFileDrawable.this, true);
        }
      }
      for (;;)
      {
        try
        {
          Bitmap localBitmap = AnimatedFileDrawable.this.backgroundBitmap;
          if (localBitmap != null) {}
        }
        catch (Throwable localThrowable2)
        {
          FileLog.e("tmessages", localThrowable2);
          continue;
        }
        try
        {
          AnimatedFileDrawable.access$502(AnimatedFileDrawable.this, Bitmap.createBitmap(AnimatedFileDrawable.this.metaData[0], AnimatedFileDrawable.this.metaData[1], Bitmap.Config.ARGB_8888));
          if ((AnimatedFileDrawable.this.backgroundShader == null) && (AnimatedFileDrawable.this.backgroundBitmap != null) && (AnimatedFileDrawable.this.roundRadius != 0)) {
            AnimatedFileDrawable.access$902(AnimatedFileDrawable.this, new BitmapShader(AnimatedFileDrawable.this.backgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
          }
          if (AnimatedFileDrawable.this.backgroundBitmap != null) {
            AnimatedFileDrawable.getVideoFrame(AnimatedFileDrawable.this.nativePtr, AnimatedFileDrawable.this.backgroundBitmap, AnimatedFileDrawable.this.metaData);
          }
          AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnable);
          return;
        }
        catch (Throwable localThrowable1)
        {
          FileLog.e("tmessages", localThrowable1);
        }
      }
    }
  };
  private Runnable loadFrameTask;
  protected final Runnable mInvalidateTask = new Runnable()
  {
    public void run()
    {
      if (AnimatedFileDrawable.this.secondParentView != null) {
        AnimatedFileDrawable.this.secondParentView.invalidate();
      }
      while (AnimatedFileDrawable.this.parentView == null) {
        return;
      }
      AnimatedFileDrawable.this.parentView.invalidate();
    }
  };
  private final Runnable mStartTask = new Runnable()
  {
    public void run()
    {
      if (AnimatedFileDrawable.this.secondParentView != null) {
        AnimatedFileDrawable.this.secondParentView.invalidate();
      }
      while (AnimatedFileDrawable.this.parentView == null) {
        return;
      }
      AnimatedFileDrawable.this.parentView.invalidate();
    }
  };
  private final int[] metaData = new int[3];
  private volatile int nativePtr;
  private Bitmap nextRenderingBitmap;
  private BitmapShader nextRenderingShader;
  private View parentView = null;
  private File path;
  private boolean recycleWithSecond;
  private Bitmap renderingBitmap;
  private BitmapShader renderingShader;
  private int roundRadius;
  private RectF roundRect = new RectF();
  private float scaleX = 1.0F;
  private float scaleY = 1.0F;
  private View secondParentView = null;
  private Matrix shaderMatrix = new Matrix();
  private Runnable uiRunnable = new Runnable()
  {
    public void run()
    {
      if ((AnimatedFileDrawable.this.destroyWhenDone) && (AnimatedFileDrawable.this.nativePtr != 0))
      {
        AnimatedFileDrawable.destroyDecoder(AnimatedFileDrawable.this.nativePtr);
        AnimatedFileDrawable.access$302(AnimatedFileDrawable.this, 0);
      }
      if (AnimatedFileDrawable.this.nativePtr == 0) {
        if (AnimatedFileDrawable.this.backgroundBitmap != null)
        {
          AnimatedFileDrawable.this.backgroundBitmap.recycle();
          AnimatedFileDrawable.access$502(AnimatedFileDrawable.this, null);
        }
      }
      do
      {
        return;
        AnimatedFileDrawable.access$602(AnimatedFileDrawable.this, null);
        AnimatedFileDrawable.access$702(AnimatedFileDrawable.this, AnimatedFileDrawable.this.backgroundBitmap);
        AnimatedFileDrawable.access$802(AnimatedFileDrawable.this, AnimatedFileDrawable.this.backgroundShader);
        if (AnimatedFileDrawable.this.metaData[2] < AnimatedFileDrawable.this.lastTimeStamp) {
          AnimatedFileDrawable.access$1102(AnimatedFileDrawable.this, 0);
        }
        if (AnimatedFileDrawable.this.metaData[2] - AnimatedFileDrawable.this.lastTimeStamp != 0) {
          AnimatedFileDrawable.access$1202(AnimatedFileDrawable.this, AnimatedFileDrawable.this.metaData[2] - AnimatedFileDrawable.this.lastTimeStamp);
        }
        AnimatedFileDrawable.access$1102(AnimatedFileDrawable.this, AnimatedFileDrawable.this.metaData[2]);
        if (AnimatedFileDrawable.this.secondParentView != null)
        {
          AnimatedFileDrawable.this.secondParentView.invalidate();
          return;
        }
      } while (AnimatedFileDrawable.this.parentView == null);
      AnimatedFileDrawable.this.parentView.invalidate();
    }
  };
  
  public AnimatedFileDrawable(File paramFile, boolean paramBoolean)
  {
    this.path = paramFile;
    if (paramBoolean)
    {
      this.nativePtr = createDecoder(paramFile.getAbsolutePath(), this.metaData);
      this.decoderCreated = true;
    }
  }
  
  private static native int createDecoder(String paramString, int[] paramArrayOfInt);
  
  private static native void destroyDecoder(int paramInt);
  
  private static native int getVideoFrame(int paramInt, Bitmap paramBitmap, int[] paramArrayOfInt);
  
  protected static void runOnUiThread(Runnable paramRunnable)
  {
    if (Looper.myLooper() == uiHandler.getLooper())
    {
      paramRunnable.run();
      return;
    }
    uiHandler.post(paramRunnable);
  }
  
  private void scheduleNextGetFrame()
  {
    if ((this.loadFrameTask != null) || ((this.nativePtr == 0) && (this.decoderCreated)) || (this.destroyWhenDone)) {
      return;
    }
    Runnable localRunnable = this.loadFrameRunnable;
    this.loadFrameTask = localRunnable;
    postToDecodeQueue(localRunnable);
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (((this.nativePtr == 0) && (this.decoderCreated)) || (this.destroyWhenDone)) {}
    label47:
    label313:
    label435:
    label437:
    label484:
    label531:
    for (;;)
    {
      return;
      if (this.isRunning)
      {
        if ((this.renderingBitmap == null) && (this.nextRenderingBitmap == null)) {
          scheduleNextGetFrame();
        }
      }
      else
      {
        if (this.renderingBitmap == null) {
          break label435;
        }
        if (this.applyTransformation)
        {
          this.dstRect.set(getBounds());
          this.scaleX = (this.dstRect.width() / this.renderingBitmap.getWidth());
          this.scaleY = (this.dstRect.height() / this.renderingBitmap.getHeight());
          this.applyTransformation = false;
        }
        if (this.roundRadius == 0) {
          break label484;
        }
        int i = this.renderingBitmap.getWidth();
        int j = this.renderingBitmap.getHeight();
        float f = Math.max(this.scaleX, this.scaleY);
        if (this.renderingShader == null) {
          this.renderingShader = new BitmapShader(this.backgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }
        getPaint().setShader(this.renderingShader);
        this.roundRect.set(this.dstRect);
        this.shaderMatrix.reset();
        if (Math.abs(this.scaleX - this.scaleY) <= 1.0E-5F) {
          break label437;
        }
        int k = (int)Math.floor(this.dstRect.width() / f);
        int m = (int)Math.floor(this.dstRect.height() / f);
        this.bitmapRect.set((i - k) / 2, (j - m) / 2, k, m);
        this.shaderMatrix.setRectToRect(this.bitmapRect, this.roundRect, Matrix.ScaleToFit.START);
        this.renderingShader.setLocalMatrix(this.shaderMatrix);
        paramCanvas.drawRoundRect(this.roundRect, this.roundRadius, this.roundRadius, getPaint());
      }
      for (;;)
      {
        if (!this.isRunning) {
          break label531;
        }
        uiHandler.postDelayed(this.mInvalidateTask, this.invalidateAfter);
        return;
        if ((Math.abs(System.currentTimeMillis() - this.lastFrameTime) < this.invalidateAfter) || (this.nextRenderingBitmap == null)) {
          break label47;
        }
        scheduleNextGetFrame();
        this.renderingBitmap = this.nextRenderingBitmap;
        this.renderingShader = this.nextRenderingShader;
        this.nextRenderingBitmap = null;
        this.nextRenderingShader = null;
        this.lastFrameTime = System.currentTimeMillis();
        break label47;
        break;
        this.bitmapRect.set(0.0F, 0.0F, this.renderingBitmap.getWidth(), this.renderingBitmap.getHeight());
        this.shaderMatrix.setRectToRect(this.bitmapRect, this.roundRect, Matrix.ScaleToFit.FILL);
        break label313;
        paramCanvas.translate(this.dstRect.left, this.dstRect.top);
        paramCanvas.scale(this.scaleX, this.scaleY);
        paramCanvas.drawBitmap(this.renderingBitmap, 0.0F, 0.0F, getPaint());
      }
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      recycle();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public Bitmap getAnimatedBitmap()
  {
    if (this.renderingBitmap != null) {
      return this.renderingBitmap;
    }
    if (this.nextRenderingBitmap != null) {
      return this.nextRenderingBitmap;
    }
    return null;
  }
  
  public int getIntrinsicHeight()
  {
    if (this.decoderCreated) {
      return this.metaData[1];
    }
    return AndroidUtilities.dp(100.0F);
  }
  
  public int getIntrinsicWidth()
  {
    if (this.decoderCreated) {
      return this.metaData[0];
    }
    return AndroidUtilities.dp(100.0F);
  }
  
  public int getMinimumHeight()
  {
    if (this.decoderCreated) {
      return this.metaData[1];
    }
    return AndroidUtilities.dp(100.0F);
  }
  
  public int getMinimumWidth()
  {
    if (this.decoderCreated) {
      return this.metaData[0];
    }
    return AndroidUtilities.dp(100.0F);
  }
  
  public int getOpacity()
  {
    return -2;
  }
  
  public boolean hasBitmap()
  {
    return (this.nativePtr != 0) && ((this.renderingBitmap != null) || (this.nextRenderingBitmap != null));
  }
  
  public boolean isRunning()
  {
    return this.isRunning;
  }
  
  public AnimatedFileDrawable makeCopy()
  {
    AnimatedFileDrawable localAnimatedFileDrawable = new AnimatedFileDrawable(this.path, false);
    localAnimatedFileDrawable.metaData[0] = this.metaData[0];
    localAnimatedFileDrawable.metaData[1] = this.metaData[1];
    return localAnimatedFileDrawable;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    super.onBoundsChange(paramRect);
    this.applyTransformation = true;
  }
  
  protected void postToDecodeQueue(Runnable paramRunnable)
  {
    executor.execute(paramRunnable);
  }
  
  public void recycle()
  {
    if (this.secondParentView != null) {
      this.recycleWithSecond = true;
    }
    for (;;)
    {
      return;
      this.isRunning = false;
      this.isRecycled = true;
      if (this.loadFrameTask == null)
      {
        if (this.nativePtr != 0)
        {
          destroyDecoder(this.nativePtr);
          this.nativePtr = 0;
        }
        if (this.nextRenderingBitmap != null)
        {
          this.nextRenderingBitmap.recycle();
          this.nextRenderingBitmap = null;
        }
      }
      while (this.renderingBitmap != null)
      {
        this.renderingBitmap.recycle();
        this.renderingBitmap = null;
        return;
        this.destroyWhenDone = true;
      }
    }
  }
  
  public void setParentView(View paramView)
  {
    this.parentView = paramView;
  }
  
  public void setRoundRadius(int paramInt)
  {
    this.roundRadius = paramInt;
    getPaint().setFlags(1);
  }
  
  public void setSecondParentView(View paramView)
  {
    this.secondParentView = paramView;
    if ((paramView == null) && (this.recycleWithSecond)) {
      recycle();
    }
  }
  
  public void start()
  {
    if (this.isRunning) {
      return;
    }
    this.isRunning = true;
    if (this.renderingBitmap == null) {
      scheduleNextGetFrame();
    }
    runOnUiThread(this.mStartTask);
  }
  
  public void stop()
  {
    this.isRunning = false;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Components\AnimatedFileDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */