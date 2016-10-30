package org.telegram.ui.Mihangram.Painting;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Mihangram.Theming.ColorPicker.ColorSelectorDialog;
import org.telegram.ui.Mihangram.Theming.ColorPicker.ColorSelectorDialog.OnColorChangedListener;
import org.telegram.ui.Mihangram.Theming.MihanTheme;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;

public class PaintActivity
  extends BaseFragment
{
  private int bgColor = -1;
  private Bitmap bitmap = null;
  private ChatActivity chatActivity;
  private long dialog_id;
  private DrawingView drawingView;
  private ImageButton eraserTButton;
  private ImageDrawingView imageDrawingView;
  private boolean isErase;
  private Paint mPaint;
  private TextView numPenSize;
  private String path = "";
  private int penColor = -15035368;
  private int penStroke = 12;
  private ImageButton penTButton;
  private MediaController.PhotoEntry photoEntry = null;
  private String randomName;
  
  public PaintActivity(ChatActivity paramChatActivity, long paramLong)
  {
    this.chatActivity = paramChatActivity;
    this.dialog_id = paramLong;
  }
  
  public PaintActivity(ChatActivity paramChatActivity, long paramLong, Bitmap paramBitmap)
  {
    this.chatActivity = paramChatActivity;
    this.dialog_id = paramLong;
    this.bitmap = paramBitmap;
  }
  
  private void clearPaintingFolder()
  {
    File localFile = new File(Environment.getExternalStorageDirectory() + "/Mihangram/Painting");
    if (localFile.isDirectory())
    {
      String[] arrayOfString = localFile.list();
      int i = 0;
      while (i < arrayOfString.length)
      {
        new File(localFile, arrayOfString[i]).delete();
        i += 1;
      }
    }
  }
  
  private void savePainting()
  {
    Object localObject1 = new Random();
    this.randomName = ("painting_" + String.valueOf(((Random)localObject1).nextInt(999999998) + 1) + ".jpg");
    if (this.bitmap == null)
    {
      this.drawingView.setDrawingCacheEnabled(true);
      localObject1 = this.drawingView.getDrawingCache();
    }
    for (;;)
    {
      Object localObject2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mihangram/Painting/");
      if (!((File)localObject2).exists()) {
        ((File)localObject2).mkdirs();
      }
      localObject2 = new File((File)localObject2, this.randomName);
      try
      {
        localObject2 = new FileOutputStream((File)localObject2);
        ((Bitmap)localObject1).compress(Bitmap.CompressFormat.JPEG, 100, (OutputStream)localObject2);
        if (this.bitmap == null)
        {
          this.drawingView.setDrawingCacheEnabled(false);
          this.drawingView.destroyDrawingCache();
          return;
          this.imageDrawingView.setDrawingCacheEnabled(true);
          localObject1 = this.imageDrawingView.getDrawingCache();
        }
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        for (;;)
        {
          localFileNotFoundException.printStackTrace();
        }
        this.imageDrawingView.setDrawingCacheEnabled(false);
        this.imageDrawingView.destroyDrawingCache();
      }
    }
  }
  
  private void sendPainting()
  {
    clearPaintingFolder();
    savePainting();
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0);
    long l = ((SharedPreferences)localObject).getLong("painting_did", 0L);
    if (this.dialog_id == 0L) {
      this.dialog_id = Long.valueOf(l).longValue();
    }
    this.path = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mihangram/Painting/" + this.randomName);
    ArrayList localArrayList = new ArrayList();
    this.photoEntry = new MediaController.PhotoEntry(0, 0, 0L, this.path, 0, false);
    localArrayList.add(this.photoEntry);
    PhotoViewer.getInstance().setParentActivity(getParentActivity());
    PhotoViewer.getInstance().openPhotoForSelect(localArrayList, 0, 2, new PhotoViewer.EmptyPhotoViewerProvider()
    {
      public void sendButtonPressed(int paramAnonymousInt)
      {
        if (PaintActivity.this.photoEntry.imagePath != null) {
          SendMessagesHelper.prepareSendingPhoto(PaintActivity.this.photoEntry.imagePath, null, PaintActivity.this.dialog_id, null, PaintActivity.this.photoEntry.caption);
        }
        while (PaintActivity.this.photoEntry.path == null) {
          return;
        }
        SendMessagesHelper.prepareSendingPhoto(PaintActivity.this.photoEntry.path, null, PaintActivity.this.dialog_id, null, PaintActivity.this.photoEntry.caption);
      }
    }, this.chatActivity);
    localObject = ((SharedPreferences)localObject).edit();
    ((SharedPreferences.Editor)localObject).remove("painting_did");
    ((SharedPreferences.Editor)localObject).commit();
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.MihanPainting, new Object[0]);
    finishFragment();
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837810);
    this.actionBar.setTitle(LocaleController.getString("Painting", 2131166664));
    this.actionBar.setAllowOverlayTitle(false);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1)
        {
          PaintActivity.this.finishFragment();
          return;
        }
        if (paramAnonymousInt == 1)
        {
          PaintActivity.this.sendPainting();
          return;
        }
        if (PaintActivity.this.bitmap == null)
        {
          PaintActivity.this.drawingView.clearDrawing();
          return;
        }
        PaintActivity.this.imageDrawingView.clearDrawing();
      }
    });
    paramContext = this.actionBar.createMenu();
    paramContext.addItemWithWidth(2, 2130837842, AndroidUtilities.dp(56.0F));
    paramContext.addItemWithWidth(1, 2130837844, AndroidUtilities.dp(56.0F));
    paramContext = new LinearLayout(getParentActivity());
    paramContext.setGravity(49);
    paramContext.setOrientation(1);
    paramContext.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
    this.fragmentView = paramContext;
    final Object localObject2 = new LinearLayout(getParentActivity());
    ((LinearLayout)localObject2).setOrientation(1);
    ((LinearLayout)localObject2).setPadding(AndroidUtilities.dp(7.0F), AndroidUtilities.dp(7.0F), AndroidUtilities.dp(7.0F), AndroidUtilities.dp(6.0F));
    ((LinearLayout)localObject2).setBackgroundColor(-2631721);
    paramContext.addView((View)localObject2, LayoutHelper.createLinear(-1, -2));
    final Object localObject1 = new LinearLayout(getParentActivity());
    ((LinearLayout)localObject2).addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
    this.penTButton = new ImageButton(getParentActivity());
    this.penTButton.setBackgroundResource(2130837652);
    this.penTButton.setScaleType(ImageView.ScaleType.CENTER);
    ((LinearLayout)localObject1).addView(this.penTButton, LayoutHelper.createLinear(30, 30, 3));
    this.penTButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PaintActivity.this.penTButton.setBackgroundResource(2130837652);
        PaintActivity.this.eraserTButton.setBackgroundResource(2130837765);
        PaintActivity.access$602(PaintActivity.this, false);
        PaintActivity.this.mPaint.setXfermode(null);
      }
    });
    this.eraserTButton = new ImageButton(getParentActivity());
    this.eraserTButton.setBackgroundResource(2130837765);
    this.eraserTButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    ((LinearLayout)localObject1).addView(this.eraserTButton, LayoutHelper.createLinear(30, 30, 3, 10, 0, 0, 0));
    this.eraserTButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PaintActivity.this.penTButton.setBackgroundResource(2130837653);
        PaintActivity.this.eraserTButton.setBackgroundResource(2130837764);
        PaintActivity.access$602(PaintActivity.this, true);
        PaintActivity.this.mPaint.setXfermode(null);
        PaintActivity.this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
      }
    });
    localObject2 = new Button(getParentActivity());
    ((Button)localObject2).setBackgroundColor(this.bgColor);
    ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(30, 30, 3, 10, 0, 0, 0));
    ((Button)localObject2).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ((LayoutInflater)PaintActivity.this.getParentActivity().getSystemService("layout_inflater")).inflate(2130903072, null, false);
        new ColorSelectorDialog(PaintActivity.this.getParentActivity(), new ColorSelectorDialog.OnColorChangedListener()
        {
          public void colorChanged(int paramAnonymous2Int)
          {
            PaintActivity.access$802(PaintActivity.this, paramAnonymous2Int);
            PaintActivity.4.this.val$backColorButton.setBackgroundColor(PaintActivity.this.bgColor);
            PaintActivity.this.setBackColor(PaintActivity.this.bgColor);
          }
        }, PaintActivity.this.bgColor, 0, 0, false).show();
      }
    });
    localObject2 = new Button(getParentActivity());
    ((Button)localObject2).setBackgroundColor(this.penColor);
    ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(30, 30, 3, 10, 0, 0, 0));
    ((Button)localObject2).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ((LayoutInflater)PaintActivity.this.getParentActivity().getSystemService("layout_inflater")).inflate(2130903072, null, false);
        new ColorSelectorDialog(PaintActivity.this.getParentActivity(), new ColorSelectorDialog.OnColorChangedListener()
        {
          public void colorChanged(int paramAnonymous2Int)
          {
            PaintActivity.access$902(PaintActivity.this, paramAnonymous2Int);
            PaintActivity.5.this.val$penColorButton.setBackgroundColor(PaintActivity.this.penColor);
            PaintActivity.this.setPenColor(PaintActivity.this.penColor);
          }
        }, PaintActivity.this.bgColor, 0, 0, false).show();
      }
    });
    localObject2 = new LinearLayout(getParentActivity());
    ((LinearLayout)localObject2).setGravity(19);
    ((LinearLayout)localObject2).setOrientation(0);
    ((LinearLayout)localObject2).setBackgroundColor(-2632749);
    ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(-1, -2));
    localObject1 = new SeekBar(getParentActivity());
    ((SeekBar)localObject1).setProgress(this.penStroke);
    ((SeekBar)localObject1).setMax(70);
    ((SeekBar)localObject1).incrementProgressBy(5);
    ((SeekBar)localObject1).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
    {
      public void onProgressChanged(SeekBar paramAnonymousSeekBar, int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        PaintActivity.access$1002(PaintActivity.this, paramAnonymousInt);
        PaintActivity.this.setPenStrokeWidth(PaintActivity.this.penStroke);
        localObject1.setProgress(PaintActivity.this.penStroke);
        PaintActivity.this.numPenSize.setText(String.valueOf(paramAnonymousInt));
      }
      
      public void onStartTrackingTouch(SeekBar paramAnonymousSeekBar) {}
      
      public void onStopTrackingTouch(SeekBar paramAnonymousSeekBar) {}
    });
    ((LinearLayout)localObject2).addView((View)localObject1, LayoutHelper.createLinear(170, -2));
    this.numPenSize = new TextView(getParentActivity());
    this.numPenSize.setTypeface(MihanTheme.getMihanTypeFace());
    this.numPenSize.setText("12");
    ((LinearLayout)localObject2).addView(this.numPenSize, LayoutHelper.createLinear(-2, -2));
    if (this.bitmap == null)
    {
      this.drawingView = new DrawingView(getParentActivity());
      this.drawingView.setBackgroundColor(this.bgColor);
      paramContext.addView(this.drawingView, LayoutHelper.createFrame(-1, -1.0F));
    }
    for (;;)
    {
      this.mPaint = new Paint();
      this.mPaint.setAntiAlias(true);
      this.mPaint.setDither(true);
      this.mPaint.setStyle(Paint.Style.STROKE);
      this.mPaint.setStrokeJoin(Paint.Join.ROUND);
      this.mPaint.setStrokeCap(Paint.Cap.ROUND);
      this.mPaint.setColor(this.penColor);
      this.mPaint.setStrokeWidth(this.penStroke);
      return paramContext;
      this.imageDrawingView = new ImageDrawingView(getParentActivity());
      this.imageDrawingView.setImageBitmap(this.bitmap);
      this.imageDrawingView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      paramContext.addView(this.imageDrawingView, LayoutHelper.createFrame(-2, -2.0F));
    }
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.swipeBackEnabled = false;
    return true;
  }
  
  public void setBackColor(int paramInt)
  {
    if (this.drawingView != null) {
      this.drawingView.setBackgroundColor(paramInt);
    }
  }
  
  public void setPenColor(int paramInt)
  {
    this.mPaint.setColor(paramInt);
  }
  
  public void setPenStrokeWidth(int paramInt)
  {
    this.mPaint.setStrokeWidth(paramInt);
  }
  
  private class DrawingView
    extends View
  {
    private static final float TOUCH_TOLERANCE = 4.0F;
    Context context;
    public int height;
    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private Canvas mCanvas;
    private Path mPath;
    private float mX;
    private float mY;
    public int width;
    
    public DrawingView(Context paramContext)
    {
      super();
      this.context = paramContext;
      this.mPath = new Path();
      this.mBitmapPaint = new Paint(4);
    }
    
    private void touch_move(float paramFloat1, float paramFloat2)
    {
      float f1 = Math.abs(paramFloat1 - this.mX);
      float f2 = Math.abs(paramFloat2 - this.mY);
      if ((f1 >= 4.0F) || (f2 >= 4.0F))
      {
        this.mPath.quadTo(this.mX, this.mY, (this.mX + paramFloat1) / 2.0F, (this.mY + paramFloat2) / 2.0F);
        this.mX = paramFloat1;
        this.mY = paramFloat2;
      }
    }
    
    private void touch_start(float paramFloat1, float paramFloat2)
    {
      this.mPath.reset();
      this.mPath.moveTo(paramFloat1, paramFloat2);
      this.mX = paramFloat1;
      this.mY = paramFloat2;
    }
    
    private void touch_up()
    {
      this.mPath.lineTo(this.mX, this.mY);
      this.mCanvas.drawPath(this.mPath, PaintActivity.this.mPaint);
      this.mPath.reset();
    }
    
    public void clearDrawing()
    {
      this.mBitmap.eraseColor(0);
      this.mPath.reset();
      invalidate();
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      super.onDraw(paramCanvas);
      paramCanvas.drawBitmap(this.mBitmap, 0.0F, 0.0F, this.mBitmapPaint);
      paramCanvas.drawPath(this.mPath, PaintActivity.this.mPaint);
    }
    
    protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
      this.mBitmap = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
      this.mCanvas = new Canvas(this.mBitmap);
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      switch (paramMotionEvent.getAction())
      {
      }
      for (;;)
      {
        return true;
        touch_start(f1, f2);
        invalidate();
        continue;
        touch_move(f1, f2);
        if (PaintActivity.this.isErase)
        {
          this.mPath.lineTo(this.mX, this.mY);
          this.mCanvas.drawPath(this.mPath, PaintActivity.this.mPaint);
          this.mPath.reset();
          this.mPath.moveTo(f1, f2);
        }
        invalidate();
        continue;
        touch_up();
        invalidate();
      }
    }
  }
  
  private class ImageDrawingView
    extends ImageView
  {
    private static final float TOUCH_TOLERANCE = 4.0F;
    Context context;
    public int height;
    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private Canvas mCanvas;
    private Path mPath;
    private float mX;
    private float mY;
    public int width;
    
    public ImageDrawingView(Context paramContext)
    {
      super();
      setAdjustViewBounds(true);
      this.context = paramContext;
      this.mPath = new Path();
      this.mBitmapPaint = new Paint(4);
    }
    
    private void touch_move(float paramFloat1, float paramFloat2)
    {
      float f1 = Math.abs(paramFloat1 - this.mX);
      float f2 = Math.abs(paramFloat2 - this.mY);
      if ((f1 >= 4.0F) || (f2 >= 4.0F))
      {
        this.mPath.quadTo(this.mX, this.mY, (this.mX + paramFloat1) / 2.0F, (this.mY + paramFloat2) / 2.0F);
        this.mX = paramFloat1;
        this.mY = paramFloat2;
      }
    }
    
    private void touch_start(float paramFloat1, float paramFloat2)
    {
      this.mPath.reset();
      this.mPath.moveTo(paramFloat1, paramFloat2);
      this.mX = paramFloat1;
      this.mY = paramFloat2;
    }
    
    private void touch_up()
    {
      this.mPath.lineTo(this.mX, this.mY);
      this.mCanvas.drawPath(this.mPath, PaintActivity.this.mPaint);
      this.mPath.reset();
    }
    
    public void clearDrawing()
    {
      this.mBitmap.eraseColor(0);
      this.mPath.reset();
      invalidate();
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      super.onDraw(paramCanvas);
      paramCanvas.drawBitmap(this.mBitmap, 0.0F, 0.0F, this.mBitmapPaint);
      paramCanvas.drawPath(this.mPath, PaintActivity.this.mPaint);
    }
    
    protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
      this.mBitmap = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
      this.mCanvas = new Canvas(this.mBitmap);
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      switch (paramMotionEvent.getAction())
      {
      }
      for (;;)
      {
        return true;
        touch_start(f1, f2);
        invalidate();
        continue;
        touch_move(f1, f2);
        if (PaintActivity.this.isErase)
        {
          this.mPath.lineTo(this.mX, this.mY);
          this.mCanvas.drawPath(this.mPath, PaintActivity.this.mPaint);
          this.mPath.reset();
          this.mPath.moveTo(f1, f2);
        }
        invalidate();
        continue;
        touch_up();
        invalidate();
      }
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\Painting\PaintActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */