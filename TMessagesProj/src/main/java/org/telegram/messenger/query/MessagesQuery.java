package org.telegram.messenger.query;

import android.text.Spannable;
import android.text.TextUtils;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_channels_getMessages;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messages_getMessages;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.Components.URLSpanUserMention;

public class MessagesQuery
{
  private static MessageObject broadcastPinnedMessage(final TLRPC.Message paramMessage, ArrayList<TLRPC.User> paramArrayList, final ArrayList<TLRPC.Chat> paramArrayList1, final boolean paramBoolean1, boolean paramBoolean2)
  {
    final HashMap localHashMap = new HashMap();
    int i = 0;
    while (i < paramArrayList.size())
    {
      localObject = (TLRPC.User)paramArrayList.get(i);
      localHashMap.put(Integer.valueOf(((TLRPC.User)localObject).id), localObject);
      i += 1;
    }
    final Object localObject = new HashMap();
    i = 0;
    while (i < paramArrayList1.size())
    {
      TLRPC.Chat localChat = (TLRPC.Chat)paramArrayList1.get(i);
      ((HashMap)localObject).put(Integer.valueOf(localChat.id), localChat);
      i += 1;
    }
    if (paramBoolean2) {
      return new MessageObject(paramMessage, localHashMap, (AbstractMap)localObject, false);
    }
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        MessagesController.getInstance().putUsers(this.val$users, paramBoolean1);
        MessagesController.getInstance().putChats(paramArrayList1, paramBoolean1);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didLoadedPinnedMessage, new Object[] { new MessageObject(paramMessage, localHashMap, localObject, false) });
      }
    });
    return null;
  }
  
  private static void broadcastReplyMessages(final ArrayList<TLRPC.Message> paramArrayList, final HashMap<Integer, ArrayList<MessageObject>> paramHashMap, ArrayList<TLRPC.User> paramArrayList1, final ArrayList<TLRPC.Chat> paramArrayList2, final long paramLong, final boolean paramBoolean)
  {
    final HashMap localHashMap = new HashMap();
    int i = 0;
    while (i < paramArrayList1.size())
    {
      localObject = (TLRPC.User)paramArrayList1.get(i);
      localHashMap.put(Integer.valueOf(((TLRPC.User)localObject).id), localObject);
      i += 1;
    }
    final Object localObject = new HashMap();
    i = 0;
    while (i < paramArrayList2.size())
    {
      TLRPC.Chat localChat = (TLRPC.Chat)paramArrayList2.get(i);
      ((HashMap)localObject).put(Integer.valueOf(localChat.id), localChat);
      i += 1;
    }
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        MessagesController.getInstance().putUsers(this.val$users, paramBoolean);
        MessagesController.getInstance().putChats(paramArrayList2, paramBoolean);
        int j = 0;
        int i = 0;
        while (i < paramArrayList.size())
        {
          Object localObject = (TLRPC.Message)paramArrayList.get(i);
          ArrayList localArrayList = (ArrayList)paramHashMap.get(Integer.valueOf(((TLRPC.Message)localObject).id));
          if (localArrayList != null)
          {
            localObject = new MessageObject((TLRPC.Message)localObject, localHashMap, localObject, false);
            j = 0;
            while (j < localArrayList.size())
            {
              MessageObject localMessageObject = (MessageObject)localArrayList.get(j);
              localMessageObject.replyMessageObject = ((MessageObject)localObject);
              if ((localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage)) {
                localMessageObject.generatePinMessageText(null, null);
              }
              j += 1;
            }
            j = 1;
          }
          i += 1;
        }
        if (j != 0) {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.didLoadedReplyMessages, new Object[] { Long.valueOf(paramLong) });
        }
      }
    });
  }
  
  public static ArrayList<TLRPC.MessageEntity> getEntities(CharSequence paramCharSequence)
  {
    Object localObject;
    if (paramCharSequence == null) {
      localObject = null;
    }
    Spannable localSpannable;
    URLSpanUserMention[] arrayOfURLSpanUserMention;
    do
    {
      do
      {
        do
        {
          return (ArrayList<TLRPC.MessageEntity>)localObject;
          localArrayList = null;
          localObject = localArrayList;
        } while (!(paramCharSequence instanceof Spannable));
        localSpannable = (Spannable)paramCharSequence;
        arrayOfURLSpanUserMention = (URLSpanUserMention[])localSpannable.getSpans(0, paramCharSequence.length(), URLSpanUserMention.class);
        localObject = localArrayList;
      } while (arrayOfURLSpanUserMention == null);
      localObject = localArrayList;
    } while (arrayOfURLSpanUserMention.length <= 0);
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    for (;;)
    {
      localObject = localArrayList;
      if (i >= arrayOfURLSpanUserMention.length) {
        break;
      }
      localObject = new TLRPC.TL_inputMessageEntityMentionName();
      ((TLRPC.TL_inputMessageEntityMentionName)localObject).user_id = MessagesController.getInputUser(Utilities.parseInt(arrayOfURLSpanUserMention[i].getURL()).intValue());
      if (((TLRPC.TL_inputMessageEntityMentionName)localObject).user_id != null)
      {
        ((TLRPC.TL_inputMessageEntityMentionName)localObject).offset = localSpannable.getSpanStart(arrayOfURLSpanUserMention[i]);
        ((TLRPC.TL_inputMessageEntityMentionName)localObject).length = (Math.min(localSpannable.getSpanEnd(arrayOfURLSpanUserMention[i]), paramCharSequence.length()) - ((TLRPC.TL_inputMessageEntityMentionName)localObject).offset);
        if (paramCharSequence.charAt(((TLRPC.TL_inputMessageEntityMentionName)localObject).offset + ((TLRPC.TL_inputMessageEntityMentionName)localObject).length - 1) == ' ') {
          ((TLRPC.TL_inputMessageEntityMentionName)localObject).length -= 1;
        }
        localArrayList.add(localObject);
      }
      i += 1;
    }
  }
  
  public static MessageObject loadPinnedMessage(int paramInt1, final int paramInt2, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          MessagesQuery.loadPinnedMessageInternal(this.val$channelId, paramInt2, false);
        }
      });
      return null;
    }
    return loadPinnedMessageInternal(paramInt1, paramInt2, true);
  }
  
  private static MessageObject loadPinnedMessageInternal(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    long l1 = paramInt2;
    long l2 = paramInt1;
    Object localObject2 = null;
    try
    {
      ArrayList localArrayList1 = new ArrayList();
      ArrayList localArrayList2 = new ArrayList();
      ArrayList localArrayList3 = new ArrayList();
      ArrayList localArrayList4 = new ArrayList();
      SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid = %d", new Object[] { Long.valueOf(l1 | l2 << 32) }), new Object[0]);
      Object localObject1 = localObject2;
      NativeByteBuffer localNativeByteBuffer;
      if (localSQLiteCursor.next())
      {
        localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
        localObject1 = localObject2;
        if (localNativeByteBuffer != null)
        {
          localObject1 = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
          localNativeByteBuffer.reuse();
          ((TLRPC.Message)localObject1).id = localSQLiteCursor.intValue(1);
          ((TLRPC.Message)localObject1).date = localSQLiteCursor.intValue(2);
          ((TLRPC.Message)localObject1).dialog_id = (-paramInt1);
          MessagesStorage.addUsersAndChatsFromMessage((TLRPC.Message)localObject1, localArrayList3, localArrayList4);
        }
      }
      localSQLiteCursor.dispose();
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM chat_pinned WHERE uid = %d", new Object[] { Integer.valueOf(paramInt1) }), new Object[0]);
        localObject2 = localObject1;
        if (localSQLiteCursor.next())
        {
          localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
          localObject2 = localObject1;
          if (localNativeByteBuffer != null)
          {
            localObject2 = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            localNativeByteBuffer.reuse();
            if (((TLRPC.Message)localObject2).id == paramInt2) {
              break label343;
            }
            localObject2 = null;
          }
        }
      }
      for (;;)
      {
        localSQLiteCursor.dispose();
        if (localObject2 != null) {
          break;
        }
        localObject1 = new TLRPC.TL_channels_getMessages();
        ((TLRPC.TL_channels_getMessages)localObject1).channel = MessagesController.getInputChannel(paramInt1);
        ((TLRPC.TL_channels_getMessages)localObject1).id.add(Integer.valueOf(paramInt2));
        ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            int j = 0;
            int i = j;
            if (paramAnonymousTL_error == null)
            {
              paramAnonymousTLObject = (TLRPC.messages_Messages)paramAnonymousTLObject;
              i = j;
              if (!paramAnonymousTLObject.messages.isEmpty())
              {
                ImageLoader.saveMessagesThumbs(paramAnonymousTLObject.messages);
                MessagesQuery.broadcastPinnedMessage((TLRPC.Message)paramAnonymousTLObject.messages.get(0), paramAnonymousTLObject.users, paramAnonymousTLObject.chats, false, false);
                MessagesStorage.getInstance().putUsersAndChats(paramAnonymousTLObject.users, paramAnonymousTLObject.chats, true, true);
                MessagesQuery.savePinnedMessage((TLRPC.Message)paramAnonymousTLObject.messages.get(0));
                i = 1;
              }
            }
            if (i == 0) {
              MessagesStorage.getInstance().updateChannelPinnedMessage(this.val$channelId, 0);
            }
          }
        });
        break label452;
        label343:
        ((TLRPC.Message)localObject2).dialog_id = (-paramInt1);
        MessagesStorage.addUsersAndChatsFromMessage((TLRPC.Message)localObject2, localArrayList3, localArrayList4);
      }
      return null;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
      break label452;
      if (paramBoolean) {
        return broadcastPinnedMessage((TLRPC.Message)localObject2, localArrayList1, localArrayList2, true, paramBoolean);
      }
      if (!localArrayList3.isEmpty()) {
        MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", localArrayList3), localArrayList1);
      }
      if (!localArrayList4.isEmpty()) {
        MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", localArrayList4), localArrayList2);
      }
      broadcastPinnedMessage((TLRPC.Message)localObject2, localArrayList1, localArrayList2, true, false);
    }
  }
  
  public static void loadReplyMessagesForMessages(ArrayList<MessageObject> paramArrayList, final long paramLong)
  {
    ArrayList localArrayList3;
    final HashMap localHashMap;
    StringBuilder localStringBuilder;
    final int i;
    MessageObject localMessageObject;
    Object localObject;
    ArrayList localArrayList2;
    ArrayList localArrayList1;
    if ((int)paramLong == 0)
    {
      localArrayList3 = new ArrayList();
      localHashMap = new HashMap();
      localStringBuilder = new StringBuilder();
      i = 0;
      while (i < paramArrayList.size())
      {
        localMessageObject = (MessageObject)paramArrayList.get(i);
        if ((localMessageObject.isReply()) && (localMessageObject.replyMessageObject == null))
        {
          localObject = Long.valueOf(localMessageObject.messageOwner.reply_to_random_id);
          if (localStringBuilder.length() > 0) {
            localStringBuilder.append(',');
          }
          localStringBuilder.append(localObject);
          localArrayList2 = (ArrayList)localHashMap.get(localObject);
          localArrayList1 = localArrayList2;
          if (localArrayList2 == null)
          {
            localArrayList1 = new ArrayList();
            localHashMap.put(localObject, localArrayList1);
          }
          localArrayList1.add(localMessageObject);
          if (!localArrayList3.contains(localObject)) {
            localArrayList3.add(localObject);
          }
        }
        i += 1;
      }
      if (!localArrayList3.isEmpty()) {}
    }
    do
    {
      return;
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            Object localObject1 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", new Object[] { TextUtils.join(",", this.val$replyMessages) }), new Object[0]);
            Object localObject2;
            int i;
            while (((SQLiteCursor)localObject1).next())
            {
              localObject2 = ((SQLiteCursor)localObject1).byteBufferValue(0);
              if (localObject2 != null)
              {
                Object localObject3 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject2, ((NativeByteBuffer)localObject2).readInt32(false), false);
                ((NativeByteBuffer)localObject2).reuse();
                ((TLRPC.Message)localObject3).id = ((SQLiteCursor)localObject1).intValue(1);
                ((TLRPC.Message)localObject3).date = ((SQLiteCursor)localObject1).intValue(2);
                ((TLRPC.Message)localObject3).dialog_id = paramLong;
                localObject2 = (ArrayList)this.val$replyMessageRandomOwners.remove(Long.valueOf(((SQLiteCursor)localObject1).longValue(3)));
                if (localObject2 != null)
                {
                  localObject3 = new MessageObject((TLRPC.Message)localObject3, null, null, false);
                  i = 0;
                  while (i < ((ArrayList)localObject2).size())
                  {
                    MessageObject localMessageObject = (MessageObject)((ArrayList)localObject2).get(i);
                    localMessageObject.replyMessageObject = ((MessageObject)localObject3);
                    localMessageObject.messageOwner.reply_to_msg_id = ((MessageObject)localObject3).getId();
                    i += 1;
                  }
                }
              }
            }
            ((SQLiteCursor)localObject1).dispose();
            if (!this.val$replyMessageRandomOwners.isEmpty())
            {
              localObject1 = this.val$replyMessageRandomOwners.entrySet().iterator();
              while (((Iterator)localObject1).hasNext())
              {
                localObject2 = (ArrayList)((Map.Entry)((Iterator)localObject1).next()).getValue();
                i = 0;
                while (i < ((ArrayList)localObject2).size())
                {
                  ((MessageObject)((ArrayList)localObject2).get(i)).messageOwner.reply_to_random_id = 0L;
                  i += 1;
                }
              }
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didLoadedReplyMessages, new Object[] { Long.valueOf(MessagesQuery.5.this.val$dialogId) });
              }
            });
            return;
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
          }
        }
      });
      return;
      localArrayList3 = new ArrayList();
      localHashMap = new HashMap();
      localStringBuilder = new StringBuilder();
      i = 0;
      int j = 0;
      while (j < paramArrayList.size())
      {
        localMessageObject = (MessageObject)paramArrayList.get(j);
        int k = i;
        if (localMessageObject.getId() > 0)
        {
          k = i;
          if (localMessageObject.isReply())
          {
            k = i;
            if (localMessageObject.replyMessageObject == null)
            {
              localObject = Integer.valueOf(localMessageObject.messageOwner.reply_to_msg_id);
              long l2 = ((Integer)localObject).intValue();
              long l1 = l2;
              if (localMessageObject.messageOwner.to_id.channel_id != 0)
              {
                l1 = l2 | localMessageObject.messageOwner.to_id.channel_id << 32;
                i = localMessageObject.messageOwner.to_id.channel_id;
              }
              if (localStringBuilder.length() > 0) {
                localStringBuilder.append(',');
              }
              localStringBuilder.append(l1);
              localArrayList2 = (ArrayList)localHashMap.get(localObject);
              localArrayList1 = localArrayList2;
              if (localArrayList2 == null)
              {
                localArrayList1 = new ArrayList();
                localHashMap.put(localObject, localArrayList1);
              }
              localArrayList1.add(localMessageObject);
              k = i;
              if (!localArrayList3.contains(localObject))
              {
                localArrayList3.add(localObject);
                k = i;
              }
            }
          }
        }
        j += 1;
        i = k;
      }
    } while (localArrayList3.isEmpty());
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        do
        {
          ArrayList localArrayList2;
          ArrayList localArrayList3;
          ArrayList localArrayList4;
          ArrayList localArrayList5;
          try
          {
            ArrayList localArrayList1 = new ArrayList();
            localArrayList2 = new ArrayList();
            localArrayList3 = new ArrayList();
            localArrayList4 = new ArrayList();
            localArrayList5 = new ArrayList();
            SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", new Object[] { this.val$stringBuilder.toString() }), new Object[0]);
            while (localSQLiteCursor.next())
            {
              NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
              if (localNativeByteBuffer != null)
              {
                TLRPC.Message localMessage = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                localNativeByteBuffer.reuse();
                localMessage.id = localSQLiteCursor.intValue(1);
                localMessage.date = localSQLiteCursor.intValue(2);
                localMessage.dialog_id = paramLong;
                MessagesStorage.addUsersAndChatsFromMessage(localMessage, localArrayList4, localArrayList5);
                localArrayList1.add(localMessage);
                localHashMap.remove(Integer.valueOf(localMessage.id));
              }
            }
            localSQLiteCursor.dispose();
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
            return;
          }
          if (!localArrayList4.isEmpty()) {
            MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", localArrayList4), localArrayList2);
          }
          if (!localArrayList5.isEmpty()) {
            MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", localArrayList5), localArrayList3);
          }
          MessagesQuery.broadcastReplyMessages(localException, i, localArrayList2, localArrayList3, paramLong, true);
        } while (localHashMap.isEmpty());
        if (this.val$channelIdFinal != 0)
        {
          localObject = new TLRPC.TL_channels_getMessages();
          ((TLRPC.TL_channels_getMessages)localObject).channel = MessagesController.getInputChannel(this.val$channelIdFinal);
          ((TLRPC.TL_channels_getMessages)localObject).id = localHashMap;
          ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
          {
            public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
            {
              if (paramAnonymous2TL_error == null)
              {
                paramAnonymous2TLObject = (TLRPC.messages_Messages)paramAnonymous2TLObject;
                ImageLoader.saveMessagesThumbs(paramAnonymous2TLObject.messages);
                MessagesQuery.broadcastReplyMessages(paramAnonymous2TLObject.messages, MessagesQuery.6.this.val$replyMessageOwners, paramAnonymous2TLObject.users, paramAnonymous2TLObject.chats, MessagesQuery.6.this.val$dialogId, false);
                MessagesStorage.getInstance().putUsersAndChats(paramAnonymous2TLObject.users, paramAnonymous2TLObject.chats, true, true);
                MessagesQuery.saveReplyMessages(MessagesQuery.6.this.val$replyMessageOwners, paramAnonymous2TLObject.messages);
              }
            }
          });
          return;
        }
        Object localObject = new TLRPC.TL_messages_getMessages();
        ((TLRPC.TL_messages_getMessages)localObject).id = localHashMap;
        ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
          {
            if (paramAnonymous2TL_error == null)
            {
              paramAnonymous2TLObject = (TLRPC.messages_Messages)paramAnonymous2TLObject;
              ImageLoader.saveMessagesThumbs(paramAnonymous2TLObject.messages);
              MessagesQuery.broadcastReplyMessages(paramAnonymous2TLObject.messages, MessagesQuery.6.this.val$replyMessageOwners, paramAnonymous2TLObject.users, paramAnonymous2TLObject.chats, MessagesQuery.6.this.val$dialogId, false);
              MessagesStorage.getInstance().putUsersAndChats(paramAnonymous2TLObject.users, paramAnonymous2TLObject.chats, true, true);
              MessagesQuery.saveReplyMessages(MessagesQuery.6.this.val$replyMessageOwners, paramAnonymous2TLObject.messages);
            }
          }
        });
      }
    });
  }
  
  private static void savePinnedMessage(TLRPC.Message paramMessage)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.getInstance().getDatabase().beginTransaction();
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO chat_pinned VALUES(?, ?, ?)");
          NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(this.val$result.getObjectSize());
          this.val$result.serializeToStream(localNativeByteBuffer);
          localSQLitePreparedStatement.requery();
          localSQLitePreparedStatement.bindInteger(1, this.val$result.to_id.channel_id);
          localSQLitePreparedStatement.bindInteger(2, this.val$result.id);
          localSQLitePreparedStatement.bindByteBuffer(3, localNativeByteBuffer);
          localSQLitePreparedStatement.step();
          localNativeByteBuffer.reuse();
          localSQLitePreparedStatement.dispose();
          MessagesStorage.getInstance().getDatabase().commitTransaction();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  private static void saveReplyMessages(final HashMap<Integer, ArrayList<MessageObject>> paramHashMap, ArrayList<TLRPC.Message> paramArrayList)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        for (;;)
        {
          int i;
          try
          {
            MessagesStorage.getInstance().getDatabase().beginTransaction();
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("UPDATE messages SET replydata = ? WHERE mid = ?");
            i = 0;
            if (i < this.val$result.size())
            {
              Object localObject = (TLRPC.Message)this.val$result.get(i);
              ArrayList localArrayList = (ArrayList)paramHashMap.get(Integer.valueOf(((TLRPC.Message)localObject).id));
              if (localArrayList != null)
              {
                NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(((TLRPC.Message)localObject).getObjectSize());
                ((TLRPC.Message)localObject).serializeToStream(localNativeByteBuffer);
                int j = 0;
                if (j < localArrayList.size())
                {
                  localObject = (MessageObject)localArrayList.get(j);
                  localSQLitePreparedStatement.requery();
                  long l2 = ((MessageObject)localObject).getId();
                  long l1 = l2;
                  if (((MessageObject)localObject).messageOwner.to_id.channel_id != 0) {
                    l1 = l2 | ((MessageObject)localObject).messageOwner.to_id.channel_id << 32;
                  }
                  localSQLitePreparedStatement.bindByteBuffer(1, localNativeByteBuffer);
                  localSQLitePreparedStatement.bindLong(2, l1);
                  localSQLitePreparedStatement.step();
                  j += 1;
                  continue;
                }
                localNativeByteBuffer.reuse();
              }
            }
            else
            {
              localSQLitePreparedStatement.dispose();
              MessagesStorage.getInstance().getDatabase().commitTransaction();
              return;
            }
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
            return;
          }
          i += 1;
        }
      }
    });
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\query\MessagesQuery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */