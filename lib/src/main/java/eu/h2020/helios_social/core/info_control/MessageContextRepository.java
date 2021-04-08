package eu.h2020.helios_social.core.info_control;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * MessageContextRepository class provides the access to the MessageContextDatabase, and
 * provides methods to manage and search the data in the database.
 */
public class MessageContextRepository {

    private final MessageContextDao mMessageContextDao;
    private final List<MessageContext> mMessageContexts;
    private final int maxSize; // max number of messages

    /**
     * Creates a MessageContextRepository
     * @param appContext
     */
    public MessageContextRepository(Context appContext) {
        this(appContext, 500);
    }

    public MessageContextRepository(Context appContext, int maxSize) {
        MessageContextDatabase db = MessageContextDatabase.getDatabase(appContext);
        this.mMessageContextDao = db.messageContextDao();
        this.maxSize = maxSize;
        this.mMessageContexts = new ArrayList<MessageContext>();

        MessageContextDatabase.databaseWriteExecutor.execute(() -> {
            List<MessageContext> messageContexts = mMessageContextDao.getMessages();
            mMessageContexts.addAll(messageContexts);
            if(mMessageContexts.size() > maxSize) {
                mMessageContexts.subList(maxSize, mMessageContexts.size()).clear();
                mMessageContextDao.shrinkDb(maxSize);
            }
        });
    }

    public List<MessageContext> getMessages() {
        return mMessageContexts;
    }

    public void insert(MessageContext messageContext) {
        mMessageContexts.add(messageContext);
        MessageContextDatabase.databaseWriteExecutor.execute(() -> {
            mMessageContextDao.insert(messageContext);
            if(mMessageContexts.size() > 1.1*maxSize) {
                mMessageContexts.subList(maxSize, mMessageContexts.size()).clear();
                mMessageContextDao.shrinkDb(maxSize);
            }
        });
    }

    public void delete(MessageContext messageContext) {
        mMessageContexts.remove(messageContext);
        MessageContextDatabase.databaseWriteExecutor.execute(() -> {
            mMessageContextDao.delete(messageContext);
        });
    }

    public void deleteAll() {
        mMessageContexts.clear();
        MessageContextDatabase.databaseWriteExecutor.execute(mMessageContextDao::deleteAll);
    }

    public List<MessageContext> getMessagesByContextFrom(String contextId, String from) {
        return mMessageContextDao.getMessagesByContextFrom(contextId, from);
    }

    public List<MessageContext> getMessagesByFrom(String from) {
        return mMessageContextDao.getMessagesByFrom(from);
    }

    public List<MessageContext> getMessagesByContext(String contextId) {
        return mMessageContextDao.getMessagesByContext(contextId);
    }

    public int getSize(String contextId, String from) {
        int size = 0;
        for (MessageContext messageContext : mMessageContexts) {
            if ((contextId == null || contextId.equals(messageContext.getContextID())) &&
                    (from == null || from.equals(messageContext.getFrom()))) {
                size++;
            }
        }
        return size;
    }

    public int getMedianReactionTime(String contextId, String from) {
        List<MessageContext> messageContexts = new ArrayList<MessageContext>();
        for (MessageContext messageContext : mMessageContexts) {
            if ((contextId == null || contextId.equals(messageContext.getContextID())) &&
                    (from == null || from.equals(messageContext.getFrom()))) {
                if (messageContext.getReactionTime() >= 0) {
                    messageContexts.add(messageContext);
                }
            }
        }
        if(messageContexts.size() > 0) {
            MessageContext[] messageContextArray = messageContexts.toArray(new MessageContext[0]);
            Arrays.sort(messageContextArray, new Comparator<MessageContext>() {
                @Override
                public int compare(MessageContext o1, MessageContext o2) {
                    return o1.getReactionTime() - o2.getReactionTime();
                }
            });
            int median;
            if (messageContextArray.length % 2 == 0)
                median = (messageContextArray[messageContextArray.length/2].getReactionTime() +
                        messageContextArray[messageContextArray.length/2 - 1].getReactionTime())/2;
            else
                median = messageContextArray[messageContextArray.length/2].getReactionTime();
            return median;
        }
        return -1;
    }

    public int getMedianImportance(String contextId, String from) {
        List<MessageContext> messageContexts = new ArrayList<MessageContext>();
        for (MessageContext messageContext : mMessageContexts) {
            if ((contextId == null || contextId.equals(messageContext.getContextID())) &&
                    (from == null || from.equals(messageContext.getFrom()))) {
                if (messageContext.getImportance() > 0) {
                    messageContexts.add(messageContext);
                }
            }
        }
        if(messageContexts.size() > 0) {
            MessageContext[] messageContextArray = messageContexts.toArray(new MessageContext[0]);
            Arrays.sort(messageContextArray, new Comparator<MessageContext>() {
                @Override
                public int compare(MessageContext o1, MessageContext o2) {
                    return o1.getImportance() - o2.getImportance();
                }
            });
            int median;
            if (messageContextArray.length % 2 == 0)
                median = (messageContextArray[messageContextArray.length/2].getImportance() +
                        messageContextArray[messageContextArray.length/2 - 1].getImportance())/2;
            else
                median = messageContextArray[messageContextArray.length/2].getImportance();
            return median;
        }
        return -1;
    }

    public double getMedianTrust(String contextId, String from) {
        List<MessageContext> messageContexts = new ArrayList<MessageContext>();
        for (MessageContext messageContext : mMessageContexts) {
            if ((contextId == null || contextId.equals(messageContext.getContextID())) &&
                    (from == null || from.equals(messageContext.getFrom()))) {
                if (messageContext.getTrust() >= 0.0) {
                    messageContexts.add(messageContext);
                }
            }
        }
        if(messageContexts.size() > 0) {
            MessageContext[] messageContextArray = messageContexts.toArray(new MessageContext[0]);
            Arrays.sort(messageContextArray, new Comparator<MessageContext>() {
                @Override
                public int compare(MessageContext o1, MessageContext o2) {
                    return Float.compare(o1.getTrust(), o2.getTrust());
                }
            });
            double median;
            if (messageContextArray.length % 2 == 0)
                median = (messageContextArray[messageContextArray.length/2].getTrust() +
                        messageContextArray[messageContextArray.length/2 - 1].getTrust())/2;
            else
                median = messageContextArray[messageContextArray.length/2].getTrust();
            return median;
        }
        return -1.0;
    }
    
}