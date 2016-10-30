package org.telegram.messenger;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Locale;

public class Emoji
{
  private static int bigImgSize = 0;
  private static final int[][] cols;
  private static int drawImgSize = 0;
  private static Bitmap[][] emojiBmp;
  private static boolean inited = false;
  private static boolean[][] loadingEmoji;
  private static Paint placeholderPaint;
  private static HashMap<CharSequence, DrawableInfo> rects = new HashMap();
  private static final int splitCount = 4;
  
  static
  {
    inited = false;
    emojiBmp = (Bitmap[][])Array.newInstance(Bitmap.class, new int[] { 5, 4 });
    loadingEmoji = (boolean[][])Array.newInstance(Boolean.TYPE, new int[] { 5, 4 });
    cols = new int[][] { { 11, 11, 11, 11 }, { 6, 6, 6, 6 }, { 9, 9, 9, 9 }, { 9, 9, 9, 9 }, { 8, 8, 8, 7 } };
    int i;
    float f;
    label224:
    int j;
    if (AndroidUtilities.density <= 1.0F)
    {
      i = 32;
      drawImgSize = AndroidUtilities.dp(20.0F);
      if (!AndroidUtilities.isTablet()) {
        break label420;
      }
      f = 40.0F;
      bigImgSize = AndroidUtilities.dp(f);
      j = 0;
    }
    for (;;)
    {
      if (j >= EmojiData.data.length) {
        break label433;
      }
      int m = (int)Math.ceil(EmojiData.data[j].length / 4.0F);
      int k = 0;
      for (;;)
      {
        if (k < EmojiData.data[j].length)
        {
          int n = k / m;
          int i1 = k - n * m;
          Rect localRect = new Rect(i1 % cols[j][n] * i, i1 / cols[j][n] * i, (i1 % cols[j][n] + 1) * i, (i1 / cols[j][n] + 1) * i);
          rects.put(EmojiData.data[j][k], new DrawableInfo(localRect, (byte)j, (byte)n));
          k += 1;
          continue;
          if (AndroidUtilities.density <= 1.5F)
          {
            i = 48;
            break;
          }
          if (AndroidUtilities.density <= 2.0F)
          {
            i = 64;
            break;
          }
          i = 64;
          break;
          label420:
          f = 32.0F;
          break label224;
        }
      }
      j += 1;
    }
    label433:
    placeholderPaint = new Paint();
    placeholderPaint.setColor(0);
  }
  
  public static String fixEmoji(String paramString)
  {
    int n = paramString.length();
    int k = 0;
    String str = paramString;
    int i;
    int j;
    int m;
    if (k < n)
    {
      i = str.charAt(k);
      if ((i >= 55356) && (i <= 55358)) {
        if ((i == 55356) && (k < n - 1))
        {
          j = str.charAt(k + 1);
          if ((j == 56879) || (j == 56324) || (j == 56858) || (j == 56703))
          {
            paramString = str.substring(0, k + 2) + "️" + str.substring(k + 2);
            m = n + 1;
            j = k + 2;
          }
        }
      }
    }
    for (;;)
    {
      k = j + 1;
      n = m;
      str = paramString;
      break;
      j = k + 1;
      m = n;
      paramString = str;
      continue;
      j = k + 1;
      m = n;
      paramString = str;
      continue;
      if (i == 8419) {
        return str;
      }
      j = k;
      m = n;
      paramString = str;
      if (i >= 8252)
      {
        j = k;
        m = n;
        paramString = str;
        if (i <= 12953)
        {
          j = k;
          m = n;
          paramString = str;
          if (EmojiData.emojiToFE0FMap.containsKey(Character.valueOf(i)))
          {
            paramString = str.substring(0, k + 1) + "️" + str.substring(k + 1);
            m = n + 1;
            j = k + 1;
          }
        }
      }
    }
  }
  
  public static Drawable getEmojiBigDrawable(String paramString)
  {
    paramString = getEmojiDrawable(paramString);
    if (paramString == null) {
      return null;
    }
    paramString.setBounds(0, 0, bigImgSize, bigImgSize);
    EmojiDrawable.access$102(paramString, true);
    return paramString;
  }
  
  public static EmojiDrawable getEmojiDrawable(CharSequence paramCharSequence)
  {
    DrawableInfo localDrawableInfo = (DrawableInfo)rects.get(paramCharSequence);
    if (localDrawableInfo == null)
    {
      FileLog.e("tmessages", "No drawable for emoji " + paramCharSequence);
      return null;
    }
    paramCharSequence = new EmojiDrawable(localDrawableInfo);
    paramCharSequence.setBounds(0, 0, drawImgSize, drawImgSize);
    return paramCharSequence;
  }
  
  private static boolean inArray(char paramChar, char[] paramArrayOfChar)
  {
    int j = paramArrayOfChar.length;
    int i = 0;
    while (i < j)
    {
      if (paramArrayOfChar[i] == paramChar) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public static void invalidateAll(View paramView)
  {
    if ((paramView instanceof ViewGroup))
    {
      paramView = (ViewGroup)paramView;
      int i = 0;
      while (i < paramView.getChildCount())
      {
        invalidateAll(paramView.getChildAt(i));
        i += 1;
      }
    }
    if ((paramView instanceof TextView)) {
      paramView.invalidate();
    }
  }
  
  private static void loadEmoji(int paramInt1, final int paramInt2)
  {
    int i = 1;
    for (;;)
    {
      try
      {
        f = AndroidUtilities.density;
        if (f <= 1.0F)
        {
          f = 2.0F;
          i = 2;
          j = 4;
          if (j >= 6) {}
        }
        else
        {
          try
          {
            Object localObject1 = String.format(Locale.US, "v%d_emoji%.01fx_%d.jpg", new Object[] { Integer.valueOf(j), Float.valueOf(f), Integer.valueOf(paramInt1) });
            localObject1 = ApplicationLoader.applicationContext.getFileStreamPath((String)localObject1);
            if (((File)localObject1).exists()) {
              ((File)localObject1).delete();
            }
            localObject1 = String.format(Locale.US, "v%d_emoji%.01fx_a_%d.jpg", new Object[] { Integer.valueOf(j), Float.valueOf(f), Integer.valueOf(paramInt1) });
            localObject1 = ApplicationLoader.applicationContext.getFileStreamPath((String)localObject1);
            if (((File)localObject1).exists()) {
              ((File)localObject1).delete();
            }
            j += 1;
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
          }
          if (AndroidUtilities.density <= 1.5F)
          {
            f = 3.0F;
            i = 2;
            continue;
          }
          if (AndroidUtilities.density > 2.0F) {
            break label519;
          }
          f = 2.0F;
          continue;
        }
        final Object localObject2 = String.format(Locale.US, "v7_emoji%.01fx_%d_%d.jpg", new Object[] { Float.valueOf(f), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
        File localFile = ApplicationLoader.applicationContext.getFileStreamPath((String)localObject2);
        if (!localFile.exists())
        {
          localObject2 = ApplicationLoader.applicationContext.getAssets().open("emoji/" + (String)localObject2);
          AndroidUtilities.copyFile((InputStream)localObject2, localFile);
          ((InputStream)localObject2).close();
        }
        localObject2 = new BitmapFactory.Options();
        ((BitmapFactory.Options)localObject2).inJustDecodeBounds = true;
        BitmapFactory.decodeFile(localFile.getAbsolutePath(), (BitmapFactory.Options)localObject2);
        int j = ((BitmapFactory.Options)localObject2).outWidth / i;
        int k = ((BitmapFactory.Options)localObject2).outHeight / i;
        int m = j * 4;
        localObject2 = Bitmap.createBitmap(j, k, Bitmap.Config.ARGB_8888);
        Utilities.loadBitmap(localFile.getAbsolutePath(), (Bitmap)localObject2, i, j, k, m);
        Object localObject3 = String.format(Locale.US, "v7_emoji%.01fx_a_%d_%d.jpg", new Object[] { Float.valueOf(f), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
        localFile = ApplicationLoader.applicationContext.getFileStreamPath((String)localObject3);
        if (!localFile.exists())
        {
          localObject3 = ApplicationLoader.applicationContext.getAssets().open("emoji/" + (String)localObject3);
          AndroidUtilities.copyFile((InputStream)localObject3, localFile);
          ((InputStream)localObject3).close();
        }
        Utilities.loadBitmap(localFile.getAbsolutePath(), (Bitmap)localObject2, i, j, k, m);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            Emoji.emojiBmp[this.val$page][paramInt2] = localObject2;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.emojiDidLoaded, new Object[0]);
          }
        });
        return;
      }
      catch (Throwable localThrowable)
      {
        FileLog.e("tmessages", "Error loading emoji", localThrowable);
        return;
      }
      label519:
      float f = 2.0F;
    }
  }
  
  public static CharSequence replaceEmoji(CharSequence paramCharSequence, Paint.FontMetricsInt paramFontMetricsInt, int paramInt, boolean paramBoolean)
  {
    if ((paramCharSequence == null) || (paramCharSequence.length() == 0)) {
      return paramCharSequence;
    }
    Spannable localSpannable;
    long l2;
    int i3;
    int i2;
    int i4;
    int i6;
    StringBuilder localStringBuilder;
    int i9;
    int i5;
    int n;
    if ((!paramBoolean) && ((paramCharSequence instanceof Spannable)))
    {
      localSpannable = (Spannable)paramCharSequence;
      l2 = 0L;
      i3 = 0;
      i2 = -1;
      i4 = 0;
      i6 = 0;
      localStringBuilder = new StringBuilder(16);
      i9 = paramCharSequence.length();
      i5 = 0;
      n = 0;
      if (n >= i9) {
        break label735;
      }
    }
    label106:
    int i7;
    try
    {
      i = paramCharSequence.charAt(n);
      if (i < 55356) {
        break label772;
      }
      if (i <= 55358) {
        break label819;
      }
    }
    catch (Exception paramFontMetricsInt)
    {
      int i;
      FileLog.e("tmessages", paramFontMetricsInt);
      return paramCharSequence;
    }
    localStringBuilder.append(i);
    int k = i4 + 1;
    long l1 = l2 << 16 | i;
    int i1 = i5;
    break label836;
    label138:
    int j;
    if (i5 < 3)
    {
      i7 = i2;
      i1 = n;
      i4 = k;
      if (n + 1 < i9)
      {
        j = paramCharSequence.charAt(n + 1);
        if (i5 != 1) {
          break label918;
        }
        i7 = i2;
        i1 = n;
        i4 = k;
        if (j == 8205)
        {
          localStringBuilder.append(j);
          i1 = n + 1;
          i4 = k + 1;
          i7 = 0;
        }
      }
    }
    for (;;)
    {
      i5 += 1;
      i2 = i7;
      n = i1;
      k = i4;
      break label138;
      localSpannable = Spannable.Factory.getInstance().newSpannable(paramCharSequence.toString());
      break;
      label264:
      int m;
      char c;
      if ((l2 > 0L) && ((0xF000 & j) == 53248))
      {
        localStringBuilder.append(j);
        k = i4 + 1;
        l1 = 0L;
        i1 = 1;
        m = i2;
      }
      else
      {
        if (j != 8419) {
          break label883;
        }
        l1 = l2;
        i1 = i5;
        m = i2;
        k = i4;
        if (n > 0)
        {
          c = paramCharSequence.charAt(i6);
          if ((c < '0') || (c > '9')) {
            break label850;
          }
          label366:
          m = i6;
          k = n - i6 + 1;
          localStringBuilder.append(c);
          localStringBuilder.append(j);
          i1 = 1;
          l1 = l2;
        }
      }
      for (;;)
      {
        label405:
        if (EmojiData.dataCharsMap.containsKey(Character.valueOf(j)))
        {
          m = i2;
          if (i2 == -1) {
            m = n;
          }
          k = i4 + 1;
          localStringBuilder.append(j);
          i1 = 1;
          l1 = l2;
        }
        label735:
        label772:
        label819:
        label836:
        label850:
        label883:
        do
        {
          l1 = l2;
          i1 = i5;
          m = i2;
          k = i4;
          if (i2 != -1)
          {
            localStringBuilder.setLength(0);
            m = -1;
            k = 0;
            i1 = 0;
            l1 = l2;
            break label836;
            i5 = i2;
            i7 = i3;
            int i8 = n;
            i4 = m;
            i1 = k;
            if (i2 != 0)
            {
              i2 = n;
              i1 = k;
              if (n + 2 < i9)
              {
                i2 = n;
                i1 = k;
                if (paramCharSequence.charAt(n + 1) == 55356)
                {
                  i2 = n;
                  i1 = k;
                  if (paramCharSequence.charAt(n + 2) >= 57339)
                  {
                    i2 = n;
                    i1 = k;
                    if (paramCharSequence.charAt(n + 2) <= 57343)
                    {
                      localStringBuilder.append(paramCharSequence.subSequence(n + 1, n + 3));
                      i1 = k + 2;
                      i2 = n + 2;
                    }
                  }
                }
              }
              EmojiDrawable localEmojiDrawable = getEmojiDrawable(localStringBuilder.subSequence(0, localStringBuilder.length()));
              k = i3;
              if (localEmojiDrawable != null)
              {
                localSpannable.setSpan(new EmojiSpan(localEmojiDrawable, 0, paramInt, paramFontMetricsInt), m, m + i1, 33);
                k = i3 + 1;
              }
              i1 = 0;
              i4 = -1;
              localStringBuilder.setLength(0);
              i5 = 0;
              i8 = i2;
              i7 = k;
            }
            if (i7 >= 50) {
              return localSpannable;
            }
            n = i8 + 1;
            l2 = l1;
            i3 = i7;
            i2 = i4;
            i4 = i1;
            break;
            if ((l2 == 0L) || ((0xFFFFFFFF00000000 & l2) != 0L) || ((0xFFFF & l2) != 55356L) || (j < 56806) || (j > 56831)) {
              break label264;
            }
            m = i2;
            if (i2 != -1) {
              break label106;
            }
            m = n;
            break label106;
          }
          do
          {
            i6 = n;
            i5 = 0;
            i2 = i1;
            break;
            if (c == '#') {
              break label366;
            }
            l1 = l2;
            i1 = i5;
            m = i2;
            k = i4;
          } while (c != '*');
          break label366;
          if ((j == 169) || (j == 174)) {
            break label405;
          }
        } while ((j < 8252) || (j > 12953));
      }
      label918:
      i7 = i2;
      i1 = n;
      i4 = k;
      if (j >= 65024)
      {
        i7 = i2;
        i1 = n;
        i4 = k;
        if (j <= 65039)
        {
          i1 = n + 1;
          i4 = k + 1;
          i7 = i2;
        }
      }
    }
  }
  
  private static class DrawableInfo
  {
    public byte page;
    public byte page2;
    public Rect rect;
    
    public DrawableInfo(Rect paramRect, byte paramByte1, byte paramByte2)
    {
      this.rect = paramRect;
      this.page = paramByte1;
      this.page2 = paramByte2;
    }
  }
  
  public static class EmojiDrawable
    extends Drawable
  {
    private static Paint paint = new Paint(2);
    private static Rect rect = new Rect();
    private boolean fullSize = false;
    private Emoji.DrawableInfo info;
    
    public EmojiDrawable(Emoji.DrawableInfo paramDrawableInfo)
    {
      this.info = paramDrawableInfo;
    }
    
    public void draw(Canvas paramCanvas)
    {
      if (Emoji.emojiBmp[this.info.page][this.info.page2] == null)
      {
        if (Emoji.loadingEmoji[this.info.page][this.info.page2] != 0) {
          return;
        }
        Emoji.loadingEmoji[this.info.page][this.info.page2] = 1;
        Utilities.globalQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            Emoji.loadEmoji(Emoji.EmojiDrawable.this.info.page, Emoji.EmojiDrawable.this.info.page2);
            Emoji.loadingEmoji[Emoji.EmojiDrawable.this.info.page][Emoji.EmojiDrawable.this.info.page2] = 0;
          }
        });
        paramCanvas.drawRect(getBounds(), Emoji.placeholderPaint);
        return;
      }
      if (this.fullSize) {}
      for (Rect localRect = getDrawRect();; localRect = getBounds())
      {
        paramCanvas.drawBitmap(Emoji.emojiBmp[this.info.page][this.info.page2], this.info.rect, localRect, paint);
        return;
      }
    }
    
    public Rect getDrawRect()
    {
      Rect localRect = getBounds();
      int k = localRect.centerX();
      int j = localRect.centerY();
      localRect = rect;
      if (this.fullSize)
      {
        i = Emoji.bigImgSize;
        localRect.left = (k - i / 2);
        localRect = rect;
        if (!this.fullSize) {
          break label133;
        }
        i = Emoji.bigImgSize;
        label60:
        localRect.right = (i / 2 + k);
        localRect = rect;
        if (!this.fullSize) {
          break label140;
        }
        i = Emoji.bigImgSize;
        label86:
        localRect.top = (j - i / 2);
        localRect = rect;
        if (!this.fullSize) {
          break label147;
        }
      }
      label133:
      label140:
      label147:
      for (int i = Emoji.bigImgSize;; i = Emoji.drawImgSize)
      {
        localRect.bottom = (i / 2 + j);
        return rect;
        i = Emoji.drawImgSize;
        break;
        i = Emoji.drawImgSize;
        break label60;
        i = Emoji.drawImgSize;
        break label86;
      }
    }
    
    public Emoji.DrawableInfo getDrawableInfo()
    {
      return this.info;
    }
    
    public int getOpacity()
    {
      return -2;
    }
    
    public void setAlpha(int paramInt) {}
    
    public void setColorFilter(ColorFilter paramColorFilter) {}
  }
  
  public static class EmojiSpan
    extends ImageSpan
  {
    private Paint.FontMetricsInt fontMetrics = null;
    private int size = AndroidUtilities.dp(20.0F);
    
    public EmojiSpan(Emoji.EmojiDrawable paramEmojiDrawable, int paramInt1, int paramInt2, Paint.FontMetricsInt paramFontMetricsInt)
    {
      super(paramInt1);
      this.fontMetrics = paramFontMetricsInt;
      if (paramFontMetricsInt != null)
      {
        this.size = (Math.abs(this.fontMetrics.descent) + Math.abs(this.fontMetrics.ascent));
        if (this.size == 0) {
          this.size = AndroidUtilities.dp(20.0F);
        }
      }
    }
    
    public int getSize(Paint paramPaint, CharSequence paramCharSequence, int paramInt1, int paramInt2, Paint.FontMetricsInt paramFontMetricsInt)
    {
      Paint.FontMetricsInt localFontMetricsInt = paramFontMetricsInt;
      if (paramFontMetricsInt == null) {
        localFontMetricsInt = new Paint.FontMetricsInt();
      }
      if (this.fontMetrics == null)
      {
        paramInt1 = super.getSize(paramPaint, paramCharSequence, paramInt1, paramInt2, localFontMetricsInt);
        paramInt2 = AndroidUtilities.dp(8.0F);
        int i = AndroidUtilities.dp(10.0F);
        localFontMetricsInt.top = (-i - paramInt2);
        localFontMetricsInt.bottom = (i - paramInt2);
        localFontMetricsInt.ascent = (-i - paramInt2);
        localFontMetricsInt.leading = 0;
        localFontMetricsInt.descent = (i - paramInt2);
        return paramInt1;
      }
      if (localFontMetricsInt != null)
      {
        localFontMetricsInt.ascent = this.fontMetrics.ascent;
        localFontMetricsInt.descent = this.fontMetrics.descent;
        localFontMetricsInt.top = this.fontMetrics.top;
        localFontMetricsInt.bottom = this.fontMetrics.bottom;
      }
      if (getDrawable() != null) {
        getDrawable().setBounds(0, 0, this.size, this.size);
      }
      return this.size;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\Emoji.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */