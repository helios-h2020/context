package eu.h2020.helios_social.core.info_control;

import androidx.annotation.NonNull;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import eu.h2020.helios_social.core.context.Context;
import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;

/**
 * This class is a container of user contexts (MyContexts).
 * It provides methods to get, add and remove contexts from the container.
 * It also binds contexts to Contextual Ego Network.
 */
public class MyContexts {   

    private final Map<String,Context> myContexts;
    private final ContextualEgoNetwork cen;
    private final MyContextsDao myContextsDao;

    /**
     * Creates a MyContexts instance
     * @param cen the contextual ego network (or null)
     * @param myContextsDb MyContextsDatabase (or null)
     */
    public MyContexts(ContextualEgoNetwork cen, MyContextsDatabase myContextsDb) {
        this.myContexts = new ConcurrentHashMap<>();
        this.cen = cen;
        if(myContextsDb !=null) {
            this.myContextsDao = myContextsDb.myContextsDao();
            readContexts(true);
        } else {
            this.myContextsDao = null;
        }
    }

    /**
     * Read all contexts from the database
     * @param wait if true, wait for read completing
     */
    private void readContexts(boolean wait) {
        MyContextsDatabase.databaseWriteExecutor.execute(() -> {
            List<MyContextsEntity> myContextsEntities = myContextsDao.getContexts();
            int prevSize = myContextsEntities.size() + 1;
            while(myContextsEntities.size() > 0 && myContextsEntities.size() < prevSize) {
                List<MyContextsEntity> incompleteEntities = new ArrayList<>();
                for (MyContextsEntity contextEntity : myContextsEntities) {
                    Context context = null;
                    try {
                        context = contextEntity.getContext(myContexts);
                    } catch (Exception e) {
                    }
                    if (context != null) {
                        myContexts.put(context.getId(), context);
                    } else {
                        incompleteEntities.add(contextEntity);
                    }
                }
                prevSize = myContextsEntities.size();
                myContextsEntities = incompleteEntities;
            }
        });
        if(wait) {
            try {
                MyContextsDatabase.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds a context into MyContexts and associates it with contextual ego network
     * @param context the context
     */
    public void add(@NonNull Context context) {
        if(myContexts.get(context.getId()) == null) {
            myContexts.put(context.getId(), context);
            if (myContextsDao != null) {
                MyContextsDatabase.databaseWriteExecutor.execute(() -> {
                    try {
                        myContextsDao.add(new MyContextsEntity(context));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            if (cen != null) {
                cen.getOrCreateContext(context.getId());
            }
        }
    }

    /**
     * Updates MyContextsDatabase with updates in given context
     * @param context the context
     */
    public void update(@NonNull Context context) {
        MyContextsDatabase.databaseWriteExecutor.execute(() -> {
            try {
                myContextsDao.update(new MyContextsEntity(context));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Removes a context from MyContexts and from contextual ego network
     * @param context the context
     */
    public void remove(@NonNull Context context) {
        if(myContexts.get(context.getId()) != null) {
            myContexts.remove(context.getId());
            if (myContextsDao != null) {
                MyContextsDatabase.databaseWriteExecutor.execute(() -> {
                    MyContextsEntity myContextsEntity = myContextsDao.getContextById(context.getId());
                    if (myContextsEntity != null) {
                        myContextsDao.remove(myContextsEntity);
                    }
                });
            }
            if (cen != null) {
                eu.h2020.helios_social.core.contextualegonetwork.Context cenContext = CenUtils.getCenContext(cen, context.getId());
                if (cenContext != null) {
                    cen.removeContext(cenContext);
                }
            }
        }
    }

    /**
     * Removes all contexts from MyContexts and from contextual ego network
     */
    public void removeAll() {
        if (cen != null) {
            for(String contextId : myContexts.keySet()) {
                eu.h2020.helios_social.core.contextualegonetwork.Context cenContext = CenUtils.getCenContext(cen, contextId);
                if (cenContext != null) {
                    cen.removeContext(cenContext);
                }
            }
        }
        myContexts.clear();
        MyContextsDatabase.databaseWriteExecutor.execute(myContextsDao::removeAll);
    }

    /**
     * Sets context active
     * @param context the context
     * @param active the value (boolean)
     */
    public void setActive(@NonNull Context context, boolean active) {
        if(context.isActive() != active) {
            context.setActive(active);
            update(context);
        }
    }

    /**
     * Returns context by given id
     * @param id the context ids
     * @return the context
     */
    public Context getContextById(@NonNull String id) {
        return myContexts.get(id);
    }

    /**
     * Returns currently active contexts
     * @return the Iterator to active contexts list
     */
    public List<Context> getActiveContexts() {
        ArrayList<Context> activeContexts = new ArrayList<Context>();
        for(Context c: myContexts.values()) {
            if(c.isActive()) {
                activeContexts.add(c);
            }
        }
        return activeContexts;
    }

    /**
     * Returns all the contexts in MyContexts
     * @return the list of contexts
     */
    public List<Context> getContexts() {
        return new ArrayList<>(myContexts.values());
    }

    /**
     * Returns the number of contexts in MyContexts
     * @return the number of contexts
     */
    public int size() { return myContexts.size(); }

    /**
     * Returns the associated contextual ego network
     * @return the ContextualEgoNetwork
     */
    public ContextualEgoNetwork getCen() {
        return cen;
    }
}
