package org.telegram.messenger.exoplayer.extractor.mp4;

import android.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.GaplessInfo;
import org.telegram.messenger.exoplayer.util.Ac3Util;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.CodecSpecificDataUtil;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.NalUnitUtil.SpsData;
import org.telegram.messenger.exoplayer.util.ParsableBitArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;

final class AtomParsers
{
  private static int findEsdsPosition(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    int i = paramParsableByteArray.getPosition();
    while (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      int j = paramParsableByteArray.readInt();
      if (j > 0) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkArgument(bool, "childAtomSize should be positive");
        if (paramParsableByteArray.readInt() != Atom.TYPE_esds) {
          break;
        }
        return i;
      }
      i += j;
    }
    return -1;
  }
  
  private static void parseAudioSampleEntry(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong, String paramString, boolean paramBoolean, StsdData paramStsdData, int paramInt5)
  {
    paramParsableByteArray.setPosition(paramInt2 + 8);
    int i = 0;
    int m;
    int n;
    int j;
    int k;
    label91:
    Object localObject1;
    label105:
    Object localObject2;
    label118:
    label146:
    label188:
    Object localObject3;
    if (paramBoolean)
    {
      paramParsableByteArray.skipBytes(8);
      i = paramParsableByteArray.readUnsignedShort();
      paramParsableByteArray.skipBytes(6);
      if ((i != 0) && (i != 1)) {
        break label324;
      }
      m = paramParsableByteArray.readUnsignedShort();
      paramParsableByteArray.skipBytes(6);
      n = paramParsableByteArray.readUnsignedFixedPoint1616();
      j = m;
      k = n;
      if (i == 1)
      {
        paramParsableByteArray.skipBytes(16);
        k = n;
        j = m;
      }
      localObject1 = null;
      if (paramInt1 != Atom.TYPE_ac_3) {
        break label361;
      }
      localObject1 = "audio/ac3";
      localObject2 = null;
      i = paramParsableByteArray.getPosition();
      m = j;
      if (i - paramInt2 >= paramInt3) {
        break label753;
      }
      paramParsableByteArray.setPosition(i);
      i1 = paramParsableByteArray.readInt();
      if (i1 <= 0) {
        break label473;
      }
      bool = true;
      Assertions.checkArgument(bool, "childAtomSize should be positive");
      i2 = paramParsableByteArray.readInt();
      if ((paramInt1 != Atom.TYPE_mp4a) && (paramInt1 != Atom.TYPE_enca)) {
        break label572;
      }
      n = -1;
      if (i2 != Atom.TYPE_esds) {
        break label479;
      }
      j = i;
      if (j == -1) {
        break label513;
      }
      localObject2 = parseEsdsFromParent(paramParsableByteArray, j);
      localObject1 = (String)((Pair)localObject2).first;
      localObject2 = (byte[])((Pair)localObject2).second;
      localObject3 = localObject1;
      n = m;
      j = k;
      localObject4 = localObject2;
      if ("audio/mp4a-latm".equals(localObject1))
      {
        localObject3 = CodecSpecificDataUtil.parseAacAudioSpecificConfig((byte[])localObject2);
        j = ((Integer)((Pair)localObject3).first).intValue();
        n = ((Integer)((Pair)localObject3).second).intValue();
        localObject4 = localObject2;
        localObject3 = localObject1;
      }
    }
    for (;;)
    {
      i += i1;
      localObject1 = localObject3;
      m = n;
      k = j;
      localObject2 = localObject4;
      break label118;
      paramParsableByteArray.skipBytes(16);
      break;
      label324:
      if (i != 2) {
        break label614;
      }
      paramParsableByteArray.skipBytes(16);
      k = (int)Math.round(paramParsableByteArray.readDouble());
      j = paramParsableByteArray.readUnsignedIntToInt();
      paramParsableByteArray.skipBytes(20);
      break label91;
      label361:
      if (paramInt1 == Atom.TYPE_ec_3)
      {
        localObject1 = "audio/eac3";
        break label105;
      }
      if (paramInt1 == Atom.TYPE_dtsc)
      {
        localObject1 = "audio/vnd.dts";
        break label105;
      }
      if ((paramInt1 == Atom.TYPE_dtsh) || (paramInt1 == Atom.TYPE_dtsl))
      {
        localObject1 = "audio/vnd.dts.hd";
        break label105;
      }
      if (paramInt1 == Atom.TYPE_dtse)
      {
        localObject1 = "audio/vnd.dts.hd;profile=lbr";
        break label105;
      }
      if (paramInt1 == Atom.TYPE_samr)
      {
        localObject1 = "audio/3gpp";
        break label105;
      }
      if (paramInt1 == Atom.TYPE_sawb)
      {
        localObject1 = "audio/amr-wb";
        break label105;
      }
      if ((paramInt1 != Atom.TYPE_lpcm) && (paramInt1 != Atom.TYPE_sowt)) {
        break label105;
      }
      localObject1 = "audio/raw";
      break label105;
      label473:
      bool = false;
      break label146;
      label479:
      j = n;
      if (!paramBoolean) {
        break label188;
      }
      j = n;
      if (i2 != Atom.TYPE_wave) {
        break label188;
      }
      j = findEsdsPosition(paramParsableByteArray, i, i1);
      break label188;
      label513:
      localObject3 = localObject1;
      n = m;
      j = k;
      localObject4 = localObject2;
      if (i2 == Atom.TYPE_sinf)
      {
        paramStsdData.trackEncryptionBoxes[paramInt5] = parseSinfFromParent(paramParsableByteArray, i, i1);
        localObject3 = localObject1;
        n = m;
        j = k;
        localObject4 = localObject2;
      }
    }
    label572:
    if ((paramInt1 == Atom.TYPE_ac_3) && (i2 == Atom.TYPE_dac3))
    {
      paramParsableByteArray.setPosition(i + 8);
      paramStsdData.mediaFormat = Ac3Util.parseAc3AnnexFFormat(paramParsableByteArray, Integer.toString(paramInt4), paramLong, paramString);
    }
    label614:
    label753:
    while (localObject1 == null)
    {
      int i2;
      do
      {
        do
        {
          int i1;
          boolean bool;
          return;
          if ((paramInt1 == Atom.TYPE_ec_3) && (i2 == Atom.TYPE_dec3))
          {
            paramParsableByteArray.setPosition(i + 8);
            paramStsdData.mediaFormat = Ac3Util.parseEAc3AnnexFFormat(paramParsableByteArray, Integer.toString(paramInt4), paramLong, paramString);
            return;
          }
          if ((paramInt1 == Atom.TYPE_dtsc) || (paramInt1 == Atom.TYPE_dtse) || (paramInt1 == Atom.TYPE_dtsh)) {
            break;
          }
          localObject3 = localObject1;
          n = m;
          j = k;
          localObject4 = localObject2;
        } while (paramInt1 != Atom.TYPE_dtsl);
        localObject3 = localObject1;
        n = m;
        j = k;
        Object localObject4 = localObject2;
      } while (i2 != Atom.TYPE_ddts);
      paramStsdData.mediaFormat = MediaFormat.createAudioFormat(Integer.toString(paramInt4), (String)localObject1, -1, -1, paramLong, m, k, null, paramString);
      return;
    }
    if ("audio/raw".equals(localObject1))
    {
      paramInt1 = 2;
      localObject3 = Integer.toString(paramInt4);
      if (localObject2 != null) {
        break label814;
      }
    }
    label814:
    for (paramParsableByteArray = null;; paramParsableByteArray = Collections.singletonList(localObject2))
    {
      paramStsdData.mediaFormat = MediaFormat.createAudioFormat((String)localObject3, (String)localObject1, -1, -1, paramLong, m, k, paramParsableByteArray, paramString, paramInt1);
      return;
      paramInt1 = -1;
      break;
    }
  }
  
  private static AvcCData parseAvcCFromParent(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.setPosition(paramInt + 8 + 4);
    int i = (paramParsableByteArray.readUnsignedByte() & 0x3) + 1;
    if (i == 3) {
      throw new IllegalStateException();
    }
    ArrayList localArrayList = new ArrayList();
    float f = 1.0F;
    int j = paramParsableByteArray.readUnsignedByte() & 0x1F;
    paramInt = 0;
    while (paramInt < j)
    {
      localArrayList.add(NalUnitUtil.parseChildNalUnit(paramParsableByteArray));
      paramInt += 1;
    }
    int k = paramParsableByteArray.readUnsignedByte();
    paramInt = 0;
    while (paramInt < k)
    {
      localArrayList.add(NalUnitUtil.parseChildNalUnit(paramParsableByteArray));
      paramInt += 1;
    }
    if (j > 0)
    {
      paramParsableByteArray = new ParsableBitArray((byte[])localArrayList.get(0));
      paramParsableByteArray.setPosition((i + 1) * 8);
      f = NalUnitUtil.parseSpsNalUnit(paramParsableByteArray).pixelWidthAspectRatio;
    }
    return new AvcCData(localArrayList, i, f);
  }
  
  private static Pair<long[], long[]> parseEdts(Atom.ContainerAtom paramContainerAtom)
  {
    if (paramContainerAtom != null)
    {
      paramContainerAtom = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_elst);
      if (paramContainerAtom != null) {}
    }
    else
    {
      return Pair.create(null, null);
    }
    paramContainerAtom = paramContainerAtom.data;
    paramContainerAtom.setPosition(8);
    int j = Atom.parseFullAtomVersion(paramContainerAtom.readInt());
    int k = paramContainerAtom.readUnsignedIntToInt();
    long[] arrayOfLong1 = new long[k];
    long[] arrayOfLong2 = new long[k];
    int i = 0;
    while (i < k)
    {
      if (j == 1)
      {
        l = paramContainerAtom.readUnsignedLongToLong();
        arrayOfLong1[i] = l;
        if (j != 1) {
          break label125;
        }
      }
      label125:
      for (long l = paramContainerAtom.readLong();; l = paramContainerAtom.readInt())
      {
        arrayOfLong2[i] = l;
        if (paramContainerAtom.readShort() == 1) {
          break label135;
        }
        throw new IllegalArgumentException("Unsupported media rate.");
        l = paramContainerAtom.readUnsignedInt();
        break;
      }
      label135:
      paramContainerAtom.skipBytes(2);
      i += 1;
    }
    return Pair.create(arrayOfLong1, arrayOfLong2);
  }
  
  private static Pair<String, byte[]> parseEsdsFromParent(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.setPosition(paramInt + 8 + 4);
    paramParsableByteArray.skipBytes(1);
    parseExpandableClassSize(paramParsableByteArray);
    paramParsableByteArray.skipBytes(2);
    paramInt = paramParsableByteArray.readUnsignedByte();
    if ((paramInt & 0x80) != 0) {
      paramParsableByteArray.skipBytes(2);
    }
    if ((paramInt & 0x40) != 0) {
      paramParsableByteArray.skipBytes(paramParsableByteArray.readUnsignedShort());
    }
    if ((paramInt & 0x20) != 0) {
      paramParsableByteArray.skipBytes(2);
    }
    paramParsableByteArray.skipBytes(1);
    parseExpandableClassSize(paramParsableByteArray);
    Object localObject;
    switch (paramParsableByteArray.readUnsignedByte())
    {
    default: 
      localObject = null;
    case 107: 
    case 32: 
    case 33: 
    case 35: 
    case 64: 
    case 102: 
    case 103: 
    case 104: 
    case 165: 
    case 166: 
      for (;;)
      {
        paramParsableByteArray.skipBytes(12);
        paramParsableByteArray.skipBytes(1);
        paramInt = parseExpandableClassSize(paramParsableByteArray);
        byte[] arrayOfByte = new byte[paramInt];
        paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
        return Pair.create(localObject, arrayOfByte);
        return Pair.create("audio/mpeg", null);
        localObject = "video/mp4v-es";
        continue;
        localObject = "video/avc";
        continue;
        localObject = "video/hevc";
        continue;
        localObject = "audio/mp4a-latm";
        continue;
        localObject = "audio/ac3";
        continue;
        localObject = "audio/eac3";
      }
    case 169: 
    case 172: 
      return Pair.create("audio/vnd.dts", null);
    }
    return Pair.create("audio/vnd.dts.hd", null);
  }
  
  private static int parseExpandableClassSize(ParsableByteArray paramParsableByteArray)
  {
    int j = paramParsableByteArray.readUnsignedByte();
    for (int i = j & 0x7F; (j & 0x80) == 128; i = i << 7 | j & 0x7F) {
      j = paramParsableByteArray.readUnsignedByte();
    }
    return i;
  }
  
  private static int parseHdlr(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(16);
    return paramParsableByteArray.readInt();
  }
  
  private static Pair<List<byte[]>, Integer> parseHvcCFromParent(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.setPosition(paramInt + 8 + 21);
    int m = paramParsableByteArray.readUnsignedByte();
    int n = paramParsableByteArray.readUnsignedByte();
    paramInt = 0;
    int k = paramParsableByteArray.getPosition();
    int i = 0;
    int i1;
    int j;
    int i2;
    while (i < n)
    {
      paramParsableByteArray.skipBytes(1);
      i1 = paramParsableByteArray.readUnsignedShort();
      j = 0;
      while (j < i1)
      {
        i2 = paramParsableByteArray.readUnsignedShort();
        paramInt += i2 + 4;
        paramParsableByteArray.skipBytes(i2);
        j += 1;
      }
      i += 1;
    }
    paramParsableByteArray.setPosition(k);
    byte[] arrayOfByte = new byte[paramInt];
    k = 0;
    i = 0;
    while (i < n)
    {
      paramParsableByteArray.skipBytes(1);
      i1 = paramParsableByteArray.readUnsignedShort();
      j = 0;
      while (j < i1)
      {
        i2 = paramParsableByteArray.readUnsignedShort();
        System.arraycopy(NalUnitUtil.NAL_START_CODE, 0, arrayOfByte, k, NalUnitUtil.NAL_START_CODE.length);
        k += NalUnitUtil.NAL_START_CODE.length;
        System.arraycopy(paramParsableByteArray.data, paramParsableByteArray.getPosition(), arrayOfByte, k, i2);
        k += i2;
        paramParsableByteArray.skipBytes(i2);
        j += 1;
      }
      i += 1;
    }
    if (paramInt == 0) {}
    for (paramParsableByteArray = null;; paramParsableByteArray = Collections.singletonList(arrayOfByte)) {
      return Pair.create(paramParsableByteArray, Integer.valueOf((m & 0x3) + 1));
    }
  }
  
  private static GaplessInfo parseIlst(ParsableByteArray paramParsableByteArray)
  {
    while (paramParsableByteArray.bytesLeft() > 0)
    {
      int i = paramParsableByteArray.getPosition() + paramParsableByteArray.readInt();
      if (paramParsableByteArray.readInt() == Atom.TYPE_DASHES)
      {
        String str2 = null;
        String str1 = null;
        String str3 = null;
        while (paramParsableByteArray.getPosition() < i)
        {
          int j = paramParsableByteArray.readInt() - 12;
          int k = paramParsableByteArray.readInt();
          paramParsableByteArray.skipBytes(4);
          if (k == Atom.TYPE_mean)
          {
            str2 = paramParsableByteArray.readString(j);
          }
          else if (k == Atom.TYPE_name)
          {
            str1 = paramParsableByteArray.readString(j);
          }
          else if (k == Atom.TYPE_data)
          {
            paramParsableByteArray.skipBytes(4);
            str3 = paramParsableByteArray.readString(j - 4);
          }
          else
          {
            paramParsableByteArray.skipBytes(j);
          }
        }
        if ((str1 != null) && (str3 != null) && ("com.apple.iTunes".equals(str2))) {
          return GaplessInfo.createFromComment(str1, str3);
        }
      }
      else
      {
        paramParsableByteArray.setPosition(i);
      }
    }
    return null;
  }
  
  private static Pair<Long, String> parseMdhd(ParsableByteArray paramParsableByteArray)
  {
    int j = 8;
    paramParsableByteArray.setPosition(8);
    int k = Atom.parseFullAtomVersion(paramParsableByteArray.readInt());
    if (k == 0) {}
    for (int i = 8;; i = 16)
    {
      paramParsableByteArray.skipBytes(i);
      long l = paramParsableByteArray.readUnsignedInt();
      i = j;
      if (k == 0) {
        i = 4;
      }
      paramParsableByteArray.skipBytes(i);
      i = paramParsableByteArray.readUnsignedShort();
      return Pair.create(Long.valueOf(l), "" + (char)((i >> 10 & 0x1F) + 96) + (char)((i >> 5 & 0x1F) + 96) + (char)((i & 0x1F) + 96));
    }
  }
  
  private static GaplessInfo parseMetaAtom(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.skipBytes(12);
    ParsableByteArray localParsableByteArray = new ParsableByteArray();
    while (paramParsableByteArray.bytesLeft() >= 8)
    {
      int i = paramParsableByteArray.readInt() - 8;
      if (paramParsableByteArray.readInt() == Atom.TYPE_ilst)
      {
        localParsableByteArray.reset(paramParsableByteArray.data, paramParsableByteArray.getPosition() + i);
        localParsableByteArray.setPosition(paramParsableByteArray.getPosition());
        GaplessInfo localGaplessInfo = parseIlst(localParsableByteArray);
        if (localGaplessInfo != null) {
          return localGaplessInfo;
        }
      }
      paramParsableByteArray.skipBytes(i);
    }
    return null;
  }
  
  private static long parseMvhd(ParsableByteArray paramParsableByteArray)
  {
    int i = 8;
    paramParsableByteArray.setPosition(8);
    if (Atom.parseFullAtomVersion(paramParsableByteArray.readInt()) == 0) {}
    for (;;)
    {
      paramParsableByteArray.skipBytes(i);
      return paramParsableByteArray.readUnsignedInt();
      i = 16;
    }
  }
  
  private static float parsePaspFromParent(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.setPosition(paramInt + 8);
    paramInt = paramParsableByteArray.readUnsignedIntToInt();
    int i = paramParsableByteArray.readUnsignedIntToInt();
    return paramInt / i;
  }
  
  private static TrackEncryptionBox parseSchiFromParent(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    boolean bool = true;
    int i = paramInt1 + 8;
    while (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      int j = paramParsableByteArray.readInt();
      if (paramParsableByteArray.readInt() == Atom.TYPE_tenc)
      {
        paramParsableByteArray.skipBytes(4);
        paramInt1 = paramParsableByteArray.readInt();
        if (paramInt1 >> 8 == 1) {}
        for (;;)
        {
          byte[] arrayOfByte = new byte[16];
          paramParsableByteArray.readBytes(arrayOfByte, 0, arrayOfByte.length);
          return new TrackEncryptionBox(bool, paramInt1 & 0xFF, arrayOfByte);
          bool = false;
        }
      }
      i += j;
    }
    return null;
  }
  
  private static TrackEncryptionBox parseSinfFromParent(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    int i = paramInt1 + 8;
    TrackEncryptionBox localTrackEncryptionBox = null;
    if (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      int j = paramParsableByteArray.readInt();
      int k = paramParsableByteArray.readInt();
      if (k == Atom.TYPE_frma) {
        paramParsableByteArray.readInt();
      }
      for (;;)
      {
        i += j;
        break;
        if (k == Atom.TYPE_schm)
        {
          paramParsableByteArray.skipBytes(4);
          paramParsableByteArray.readInt();
          paramParsableByteArray.readInt();
        }
        else if (k == Atom.TYPE_schi)
        {
          localTrackEncryptionBox = parseSchiFromParent(paramParsableByteArray, i, j);
        }
      }
    }
    return localTrackEncryptionBox;
  }
  
  public static TrackSampleTable parseStbl(Track paramTrack, Atom.ContainerAtom paramContainerAtom)
    throws ParserException
  {
    Object localObject6 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stsz).data;
    boolean bool = false;
    Object localObject2 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stco);
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      bool = true;
      localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_co64);
    }
    localObject2 = ((Atom.LeafAtom)localObject1).data;
    Object localObject3 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stsc).data;
    Object localObject7 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stts).data;
    localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stss);
    if (localObject1 != null)
    {
      localObject1 = ((Atom.LeafAtom)localObject1).data;
      paramContainerAtom = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_ctts);
      if (paramContainerAtom == null) {
        break label167;
      }
    }
    int i13;
    int i14;
    label167:
    for (paramContainerAtom = paramContainerAtom.data;; paramContainerAtom = null)
    {
      ((ParsableByteArray)localObject6).setPosition(12);
      i13 = ((ParsableByteArray)localObject6).readUnsignedIntToInt();
      i14 = ((ParsableByteArray)localObject6).readUnsignedIntToInt();
      if (i14 != 0) {
        break label172;
      }
      return new TrackSampleTable(new long[0], new int[0], 0, new long[0], new int[0]);
      localObject1 = null;
      break;
    }
    label172:
    ChunkIterator localChunkIterator = new ChunkIterator((ParsableByteArray)localObject3, (ParsableByteArray)localObject2, bool);
    ((ParsableByteArray)localObject7).setPosition(12);
    int i1 = ((ParsableByteArray)localObject7).readUnsignedIntToInt() - 1;
    int i4 = ((ParsableByteArray)localObject7).readUnsignedIntToInt();
    int i2 = ((ParsableByteArray)localObject7).readUnsignedIntToInt();
    int i3 = 0;
    int k = 0;
    int n = 0;
    if (paramContainerAtom != null)
    {
      paramContainerAtom.setPosition(12);
      k = paramContainerAtom.readUnsignedIntToInt();
    }
    int i = -1;
    int j = 0;
    if (localObject1 != null)
    {
      ((ParsableByteArray)localObject1).setPosition(12);
      j = ((ParsableByteArray)localObject1).readUnsignedIntToInt();
      i = ((ParsableByteArray)localObject1).readUnsignedIntToInt() - 1;
    }
    if ((i13 != 0) && ("audio/raw".equals(paramTrack.mediaFormat.mimeType)) && (i1 == 0) && (k == 0) && (j == 0)) {}
    Object localObject4;
    Object localObject5;
    long l2;
    long l1;
    int i6;
    int i5;
    for (int m = 1;; m = 0)
    {
      i7 = 0;
      if (m != 0) {
        break label861;
      }
      localObject4 = new long[i14];
      localObject5 = new int[i14];
      localObject3 = new long[i14];
      localObject2 = new int[i14];
      l2 = 0L;
      l1 = 0L;
      i6 = 0;
      i8 = 0;
      m = n;
      i5 = j;
      n = i6;
      j = i3;
      i6 = i;
      i3 = i8;
      i = i7;
      if (i3 >= i14) {
        break label724;
      }
      while (n == 0)
      {
        Assertions.checkState(localChunkIterator.moveNext());
        l1 = localChunkIterator.offset;
        n = localChunkIterator.numSamples;
      }
    }
    int i9 = j;
    int i8 = k;
    int i7 = m;
    if (paramContainerAtom != null)
    {
      while ((j == 0) && (k > 0))
      {
        j = paramContainerAtom.readUnsignedIntToInt();
        m = paramContainerAtom.readInt();
        k -= 1;
      }
      i9 = j - 1;
      i7 = m;
      i8 = k;
    }
    localObject4[i3] = l1;
    if (i13 == 0)
    {
      j = ((ParsableByteArray)localObject6).readUnsignedIntToInt();
      label495:
      localObject5[i3] = j;
      k = i;
      if (localObject5[i3] > i) {
        k = localObject5[i3];
      }
      localObject3[i3] = (i7 + l2);
      if (localObject1 != null) {
        break label719;
      }
    }
    label719:
    for (i = 1;; i = 0)
    {
      localObject2[i3] = i;
      int i11 = i6;
      m = i5;
      if (i3 == i6)
      {
        localObject2[i3] = 1;
        i = i5 - 1;
        i11 = i6;
        m = i;
        if (i > 0)
        {
          i11 = ((ParsableByteArray)localObject1).readUnsignedIntToInt() - 1;
          m = i;
        }
      }
      l2 += i2;
      i = i4 - 1;
      j = i;
      int i12 = i1;
      int i10 = i2;
      if (i == 0)
      {
        j = i;
        i12 = i1;
        i10 = i2;
        if (i1 > 0)
        {
          j = ((ParsableByteArray)localObject7).readUnsignedIntToInt();
          i10 = ((ParsableByteArray)localObject7).readUnsignedIntToInt();
          i12 = i1 - 1;
        }
      }
      l1 += localObject5[i3];
      n -= 1;
      i3 += 1;
      i = k;
      i6 = i11;
      i4 = j;
      j = i9;
      i5 = m;
      i1 = i12;
      k = i8;
      i2 = i10;
      m = i7;
      break;
      j = i13;
      break label495;
    }
    label724:
    if (i5 == 0)
    {
      bool = true;
      Assertions.checkArgument(bool);
      if (i4 != 0) {
        break label837;
      }
      bool = true;
      label745:
      Assertions.checkArgument(bool);
      if (n != 0) {
        break label843;
      }
      bool = true;
      label758:
      Assertions.checkArgument(bool);
      if (i1 != 0) {
        break label849;
      }
      bool = true;
      label771:
      Assertions.checkArgument(bool);
      if (k != 0) {
        break label855;
      }
      bool = true;
      label784:
      Assertions.checkArgument(bool);
      localObject1 = localObject5;
      paramContainerAtom = (Atom.ContainerAtom)localObject4;
    }
    for (;;)
    {
      if (paramTrack.editListDurations != null) {
        break label963;
      }
      Util.scaleLargeTimestampsInPlace((long[])localObject3, 1000000L, paramTrack.timescale);
      return new TrackSampleTable(paramContainerAtom, (int[])localObject1, i, (long[])localObject3, (int[])localObject2);
      bool = false;
      break;
      label837:
      bool = false;
      break label745;
      label843:
      bool = false;
      break label758;
      label849:
      bool = false;
      break label771;
      label855:
      bool = false;
      break label784;
      label861:
      paramContainerAtom = new long[localChunkIterator.length];
      localObject1 = new int[localChunkIterator.length];
      while (localChunkIterator.moveNext())
      {
        paramContainerAtom[localChunkIterator.index] = localChunkIterator.offset;
        localObject1[localChunkIterator.index] = localChunkIterator.numSamples;
      }
      localObject2 = FixedSampleSizeRechunker.rechunk(i13, paramContainerAtom, (int[])localObject1, i2);
      paramContainerAtom = ((FixedSampleSizeRechunker.Results)localObject2).offsets;
      localObject1 = ((FixedSampleSizeRechunker.Results)localObject2).sizes;
      i = ((FixedSampleSizeRechunker.Results)localObject2).maximumSize;
      localObject3 = ((FixedSampleSizeRechunker.Results)localObject2).timestamps;
      localObject2 = ((FixedSampleSizeRechunker.Results)localObject2).flags;
    }
    label963:
    if ((paramTrack.editListDurations.length == 1) && (paramTrack.editListDurations[0] == 0L))
    {
      j = 0;
      while (j < localObject3.length)
      {
        localObject3[j] = Util.scaleLargeTimestamp(localObject3[j] - paramTrack.editListMediaTimes[0], 1000000L, paramTrack.timescale);
        j += 1;
      }
      return new TrackSampleTable(paramContainerAtom, (int[])localObject1, i, (long[])localObject3, (int[])localObject2);
    }
    j = 0;
    n = 0;
    k = 0;
    m = 0;
    if (m < paramTrack.editListDurations.length)
    {
      l1 = paramTrack.editListMediaTimes[m];
      i3 = k;
      i2 = j;
      i1 = n;
      if (l1 != -1L)
      {
        l2 = Util.scaleLargeTimestamp(paramTrack.editListDurations[m], paramTrack.timescale, paramTrack.movieTimescale);
        i3 = Util.binarySearchCeil((long[])localObject3, l1, true, true);
        i1 = Util.binarySearchCeil((long[])localObject3, l1 + l2, true, false);
        i2 = j + (i1 - i3);
        if (n == i3) {
          break label1179;
        }
      }
      label1179:
      for (j = 1;; j = 0)
      {
        i3 = k | j;
        m += 1;
        k = i3;
        j = i2;
        n = i1;
        break;
      }
    }
    if (j != i14)
    {
      m = 1;
      i2 = k | m;
      if (i2 == 0) {
        break label1476;
      }
      localObject4 = new long[j];
      label1210:
      if (i2 == 0) {
        break label1482;
      }
      localObject5 = new int[j];
      label1220:
      if (i2 == 0) {
        break label1489;
      }
      i = 0;
      label1227:
      if (i2 == 0) {
        break label1492;
      }
      localObject6 = new int[j];
      label1237:
      localObject7 = new long[j];
      l1 = 0L;
      j = 0;
      k = 0;
    }
    for (;;)
    {
      if (k >= paramTrack.editListDurations.length) {
        break label1521;
      }
      l2 = paramTrack.editListMediaTimes[k];
      long l3 = paramTrack.editListDurations[k];
      n = i;
      i1 = j;
      if (l2 != -1L)
      {
        long l4 = Util.scaleLargeTimestamp(l3, paramTrack.timescale, paramTrack.movieTimescale);
        m = Util.binarySearchCeil((long[])localObject3, l2, true, true);
        i3 = Util.binarySearchCeil((long[])localObject3, l2 + l4, true, false);
        if (i2 != 0)
        {
          n = i3 - m;
          System.arraycopy(paramContainerAtom, m, localObject4, j, n);
          System.arraycopy(localObject1, m, localObject5, j, n);
          System.arraycopy(localObject2, m, localObject6, j, n);
        }
        for (;;)
        {
          n = i;
          i1 = j;
          if (m >= i3) {
            break;
          }
          localObject7[j] = (Util.scaleLargeTimestamp(l1, 1000000L, paramTrack.movieTimescale) + Util.scaleLargeTimestamp(localObject3[m] - l2, 1000000L, paramTrack.timescale));
          n = i;
          if (i2 != 0)
          {
            n = i;
            if (localObject5[j] > i) {
              n = localObject1[m];
            }
          }
          j += 1;
          m += 1;
          i = n;
        }
        m = 0;
        break;
        label1476:
        localObject4 = paramContainerAtom;
        break label1210;
        label1482:
        localObject5 = localObject1;
        break label1220;
        label1489:
        break label1227;
        label1492:
        localObject6 = localObject2;
        break label1237;
      }
      l1 += l3;
      k += 1;
      i = n;
      j = i1;
    }
    label1521:
    k = 0;
    j = 0;
    if ((j < localObject6.length) && (k == 0))
    {
      if ((localObject6[j] & 0x1) != 0) {}
      for (m = 1;; m = 0)
      {
        k |= m;
        j += 1;
        break;
      }
    }
    if (k == 0) {
      throw new ParserException("The edited sample sequence does not contain a sync sample.");
    }
    return new TrackSampleTable((long[])localObject4, (int[])localObject5, i, (long[])localObject7, (int[])localObject6);
  }
  
  private static StsdData parseStsd(ParsableByteArray paramParsableByteArray, int paramInt1, long paramLong, int paramInt2, String paramString, boolean paramBoolean)
  {
    paramParsableByteArray.setPosition(12);
    int j = paramParsableByteArray.readInt();
    StsdData localStsdData = new StsdData(j);
    int i = 0;
    if (i < j)
    {
      int k = paramParsableByteArray.getPosition();
      int m = paramParsableByteArray.readInt();
      boolean bool;
      label53:
      int n;
      if (m > 0)
      {
        bool = true;
        Assertions.checkArgument(bool, "childAtomSize should be positive");
        n = paramParsableByteArray.readInt();
        if ((n != Atom.TYPE_avc1) && (n != Atom.TYPE_avc3) && (n != Atom.TYPE_encv) && (n != Atom.TYPE_mp4v) && (n != Atom.TYPE_hvc1) && (n != Atom.TYPE_hev1) && (n != Atom.TYPE_s263)) {
          break label162;
        }
        parseVideoSampleEntry(paramParsableByteArray, k, m, paramInt1, paramLong, paramInt2, localStsdData, i);
      }
      for (;;)
      {
        paramParsableByteArray.setPosition(k + m);
        i += 1;
        break;
        bool = false;
        break label53;
        label162:
        if ((n == Atom.TYPE_mp4a) || (n == Atom.TYPE_enca) || (n == Atom.TYPE_ac_3) || (n == Atom.TYPE_ec_3) || (n == Atom.TYPE_dtsc) || (n == Atom.TYPE_dtse) || (n == Atom.TYPE_dtsh) || (n == Atom.TYPE_dtsl) || (n == Atom.TYPE_samr) || (n == Atom.TYPE_sawb) || (n == Atom.TYPE_lpcm) || (n == Atom.TYPE_sowt)) {
          parseAudioSampleEntry(paramParsableByteArray, n, k, m, paramInt1, paramLong, paramString, paramBoolean, localStsdData, i);
        } else if (n == Atom.TYPE_TTML) {
          localStsdData.mediaFormat = MediaFormat.createTextFormat(Integer.toString(paramInt1), "application/ttml+xml", -1, paramLong, paramString);
        } else if (n == Atom.TYPE_tx3g) {
          localStsdData.mediaFormat = MediaFormat.createTextFormat(Integer.toString(paramInt1), "application/x-quicktime-tx3g", -1, paramLong, paramString);
        } else if (n == Atom.TYPE_wvtt) {
          localStsdData.mediaFormat = MediaFormat.createTextFormat(Integer.toString(paramInt1), "application/x-mp4vtt", -1, paramLong, paramString);
        } else if (n == Atom.TYPE_stpp) {
          localStsdData.mediaFormat = MediaFormat.createTextFormat(Integer.toString(paramInt1), "application/ttml+xml", -1, paramLong, paramString, 0L);
        }
      }
    }
    return localStsdData;
  }
  
  private static TkhdData parseTkhd(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(8);
    int i1 = Atom.parseFullAtomVersion(paramParsableByteArray.readInt());
    int i;
    int n;
    int m;
    label55:
    int j;
    label57:
    int k;
    long l1;
    if (i1 == 0)
    {
      i = 8;
      paramParsableByteArray.skipBytes(i);
      n = paramParsableByteArray.readInt();
      paramParsableByteArray.skipBytes(4);
      m = 1;
      int i2 = paramParsableByteArray.getPosition();
      if (i1 != 0) {
        break label172;
      }
      i = 4;
      j = 0;
      k = m;
      if (j < i)
      {
        if (paramParsableByteArray.data[(i2 + j)] == -1) {
          break label178;
        }
        k = 0;
      }
      if (k == 0) {
        break label185;
      }
      paramParsableByteArray.skipBytes(i);
      l1 = -1L;
      paramParsableByteArray.skipBytes(16);
      i = paramParsableByteArray.readInt();
      j = paramParsableByteArray.readInt();
      paramParsableByteArray.skipBytes(4);
      k = paramParsableByteArray.readInt();
      m = paramParsableByteArray.readInt();
      if ((i != 0) || (j != 65536) || (k != -65536) || (m != 0)) {
        break label224;
      }
      i = 90;
    }
    for (;;)
    {
      return new TkhdData(n, l1, i);
      i = 16;
      break;
      label172:
      i = 8;
      break label55;
      label178:
      j += 1;
      break label57;
      label185:
      if (i1 == 0) {}
      for (long l2 = paramParsableByteArray.readUnsignedInt();; l2 = paramParsableByteArray.readUnsignedLongToLong())
      {
        l1 = l2;
        if (l2 != 0L) {
          break;
        }
        l1 = -1L;
        break;
      }
      label224:
      if ((i == 0) && (j == -65536) && (k == 65536) && (m == 0)) {
        i = 270;
      } else if ((i == -65536) && (j == 0) && (k == 0) && (m == -65536)) {
        i = 180;
      } else {
        i = 0;
      }
    }
  }
  
  public static Track parseTrak(Atom.ContainerAtom paramContainerAtom, Atom.LeafAtom paramLeafAtom, long paramLong, boolean paramBoolean)
  {
    Object localObject = paramContainerAtom.getContainerAtomOfType(Atom.TYPE_mdia);
    int i = parseHdlr(((Atom.ContainerAtom)localObject).getLeafAtomOfType(Atom.TYPE_hdlr).data);
    if ((i != Track.TYPE_soun) && (i != Track.TYPE_vide) && (i != Track.TYPE_text) && (i != Track.TYPE_sbtl) && (i != Track.TYPE_subt)) {
      return null;
    }
    TkhdData localTkhdData = parseTkhd(paramContainerAtom.getLeafAtomOfType(Atom.TYPE_tkhd).data);
    long l1 = paramLong;
    if (paramLong == -1L) {
      l1 = localTkhdData.duration;
    }
    long l2 = parseMvhd(paramLeafAtom.data);
    if (l1 == -1L) {}
    for (paramLong = -1L;; paramLong = Util.scaleLargeTimestamp(l1, 1000000L, l2))
    {
      Atom.ContainerAtom localContainerAtom = ((Atom.ContainerAtom)localObject).getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl);
      paramLeafAtom = parseMdhd(((Atom.ContainerAtom)localObject).getLeafAtomOfType(Atom.TYPE_mdhd).data);
      localObject = parseStsd(localContainerAtom.getLeafAtomOfType(Atom.TYPE_stsd).data, localTkhdData.id, paramLong, localTkhdData.rotationDegrees, (String)paramLeafAtom.second, paramBoolean);
      paramContainerAtom = parseEdts(paramContainerAtom.getContainerAtomOfType(Atom.TYPE_edts));
      if (((StsdData)localObject).mediaFormat != null) {
        break;
      }
      return null;
    }
    return new Track(localTkhdData.id, i, ((Long)paramLeafAtom.first).longValue(), l2, paramLong, ((StsdData)localObject).mediaFormat, ((StsdData)localObject).trackEncryptionBoxes, ((StsdData)localObject).nalUnitLengthFieldLength, (long[])paramContainerAtom.first, (long[])paramContainerAtom.second);
  }
  
  public static GaplessInfo parseUdta(Atom.LeafAtom paramLeafAtom, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (;;)
    {
      return null;
      paramLeafAtom = paramLeafAtom.data;
      paramLeafAtom.setPosition(8);
      while (paramLeafAtom.bytesLeft() >= 8)
      {
        int i = paramLeafAtom.readInt();
        if (paramLeafAtom.readInt() == Atom.TYPE_meta)
        {
          paramLeafAtom.setPosition(paramLeafAtom.getPosition() - 8);
          paramLeafAtom.setLimit(paramLeafAtom.getPosition() + i);
          return parseMetaAtom(paramLeafAtom);
        }
        paramLeafAtom.skipBytes(i - 8);
      }
    }
  }
  
  private static void parseVideoSampleEntry(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, int paramInt3, long paramLong, int paramInt4, StsdData paramStsdData, int paramInt5)
  {
    paramParsableByteArray.setPosition(paramInt1 + 8);
    paramParsableByteArray.skipBytes(24);
    int m = paramParsableByteArray.readUnsignedShort();
    int n = paramParsableByteArray.readUnsignedShort();
    int j = 0;
    float f = 1.0F;
    paramParsableByteArray.skipBytes(50);
    Object localObject3 = null;
    int i = paramParsableByteArray.getPosition();
    Object localObject4 = null;
    int i2;
    int i1;
    if (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      i2 = paramParsableByteArray.getPosition();
      i1 = paramParsableByteArray.readInt();
      if ((i1 != 0) || (paramParsableByteArray.getPosition() - paramInt1 != paramInt2)) {}
    }
    else
    {
      if (localObject4 != null) {
        break label498;
      }
      return;
    }
    boolean bool;
    label105:
    int i3;
    label134:
    Object localObject1;
    Object localObject2;
    int k;
    if (i1 > 0)
    {
      bool = true;
      Assertions.checkArgument(bool, "childAtomSize should be positive");
      i3 = paramParsableByteArray.readInt();
      if (i3 != Atom.TYPE_avcC) {
        break label239;
      }
      if (localObject4 != null) {
        break label233;
      }
      bool = true;
      Assertions.checkState(bool);
      localObject3 = "video/avc";
      AvcCData localAvcCData = parseAvcCFromParent(paramParsableByteArray, i2);
      localObject4 = localAvcCData.initializationData;
      paramStsdData.nalUnitLengthFieldLength = localAvcCData.nalUnitLengthFieldLength;
      localObject1 = localObject3;
      localObject2 = localObject4;
      k = j;
      if (j == 0)
      {
        f = localAvcCData.pixelWidthAspectRatio;
        k = j;
        localObject2 = localObject4;
        localObject1 = localObject3;
      }
    }
    for (;;)
    {
      i += i1;
      localObject4 = localObject1;
      localObject3 = localObject2;
      j = k;
      break;
      bool = false;
      break label105;
      label233:
      bool = false;
      break label134;
      label239:
      if (i3 == Atom.TYPE_hvcC)
      {
        if (localObject4 == null) {}
        for (bool = true;; bool = false)
        {
          Assertions.checkState(bool);
          localObject1 = "video/hevc";
          localObject3 = parseHvcCFromParent(paramParsableByteArray, i2);
          localObject2 = (List)((Pair)localObject3).first;
          paramStsdData.nalUnitLengthFieldLength = ((Integer)((Pair)localObject3).second).intValue();
          k = j;
          break;
        }
      }
      if (i3 == Atom.TYPE_d263)
      {
        if (localObject4 == null) {}
        for (bool = true;; bool = false)
        {
          Assertions.checkState(bool);
          localObject1 = "video/3gpp";
          localObject2 = localObject3;
          k = j;
          break;
        }
      }
      if (i3 == Atom.TYPE_esds)
      {
        if (localObject4 == null) {}
        for (bool = true;; bool = false)
        {
          Assertions.checkState(bool);
          localObject2 = parseEsdsFromParent(paramParsableByteArray, i2);
          localObject1 = (String)((Pair)localObject2).first;
          localObject2 = Collections.singletonList(((Pair)localObject2).second);
          k = j;
          break;
        }
      }
      if (i3 == Atom.TYPE_sinf)
      {
        paramStsdData.trackEncryptionBoxes[paramInt5] = parseSinfFromParent(paramParsableByteArray, i2, i1);
        localObject1 = localObject4;
        localObject2 = localObject3;
        k = j;
      }
      else
      {
        localObject1 = localObject4;
        localObject2 = localObject3;
        k = j;
        if (i3 == Atom.TYPE_pasp)
        {
          f = parsePaspFromParent(paramParsableByteArray, i2);
          k = 1;
          localObject1 = localObject4;
          localObject2 = localObject3;
        }
      }
    }
    label498:
    paramStsdData.mediaFormat = MediaFormat.createVideoFormat(Integer.toString(paramInt3), (String)localObject4, -1, -1, paramLong, m, n, (List)localObject3, paramInt4, f);
  }
  
  private static final class AvcCData
  {
    public final List<byte[]> initializationData;
    public final int nalUnitLengthFieldLength;
    public final float pixelWidthAspectRatio;
    
    public AvcCData(List<byte[]> paramList, int paramInt, float paramFloat)
    {
      this.initializationData = paramList;
      this.nalUnitLengthFieldLength = paramInt;
      this.pixelWidthAspectRatio = paramFloat;
    }
  }
  
  private static final class ChunkIterator
  {
    private final ParsableByteArray chunkOffsets;
    private final boolean chunkOffsetsAreLongs;
    public int index;
    public final int length;
    private int nextSamplesPerChunkChangeIndex;
    public int numSamples;
    public long offset;
    private int remainingSamplesPerChunkChanges;
    private final ParsableByteArray stsc;
    
    public ChunkIterator(ParsableByteArray paramParsableByteArray1, ParsableByteArray paramParsableByteArray2, boolean paramBoolean)
    {
      this.stsc = paramParsableByteArray1;
      this.chunkOffsets = paramParsableByteArray2;
      this.chunkOffsetsAreLongs = paramBoolean;
      paramParsableByteArray2.setPosition(12);
      this.length = paramParsableByteArray2.readUnsignedIntToInt();
      paramParsableByteArray1.setPosition(12);
      this.remainingSamplesPerChunkChanges = paramParsableByteArray1.readUnsignedIntToInt();
      if (paramParsableByteArray1.readInt() == 1) {}
      for (paramBoolean = bool;; paramBoolean = false)
      {
        Assertions.checkState(paramBoolean, "first_chunk must be 1");
        this.index = -1;
        return;
      }
    }
    
    public boolean moveNext()
    {
      int i = this.index + 1;
      this.index = i;
      if (i == this.length) {
        return false;
      }
      long l;
      if (this.chunkOffsetsAreLongs)
      {
        l = this.chunkOffsets.readUnsignedLongToLong();
        this.offset = l;
        if (this.index == this.nextSamplesPerChunkChangeIndex)
        {
          this.numSamples = this.stsc.readUnsignedIntToInt();
          this.stsc.skipBytes(4);
          i = this.remainingSamplesPerChunkChanges - 1;
          this.remainingSamplesPerChunkChanges = i;
          if (i <= 0) {
            break label116;
          }
        }
      }
      label116:
      for (i = this.stsc.readUnsignedIntToInt() - 1;; i = -1)
      {
        this.nextSamplesPerChunkChangeIndex = i;
        return true;
        l = this.chunkOffsets.readUnsignedInt();
        break;
      }
    }
  }
  
  private static final class StsdData
  {
    public MediaFormat mediaFormat;
    public int nalUnitLengthFieldLength;
    public final TrackEncryptionBox[] trackEncryptionBoxes;
    
    public StsdData(int paramInt)
    {
      this.trackEncryptionBoxes = new TrackEncryptionBox[paramInt];
      this.nalUnitLengthFieldLength = -1;
    }
  }
  
  private static final class TkhdData
  {
    private final long duration;
    private final int id;
    private final int rotationDegrees;
    
    public TkhdData(int paramInt1, long paramLong, int paramInt2)
    {
      this.id = paramInt1;
      this.duration = paramLong;
      this.rotationDegrees = paramInt2;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\extractor\mp4\AtomParsers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */