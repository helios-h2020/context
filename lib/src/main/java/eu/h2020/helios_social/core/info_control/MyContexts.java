package eu.h2020.helios_social.core.info_control;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import eu.h2020.helios_social.core.context.Context;
import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;

/**
 * This class is a container of user contexts (MyContexts).
 * It provides methods to get, add and remove contexts from the container.
 * It also binds contexts to Contextual Ego Network.
 */
public class MyContexts {   

    private final List<Context> myContexts;
    private final ContextualEgoNetwork cen;

    /**
     * Creates a MyContexts instance
     * @param cen the contextual ego network
     */
    public MyContexts(ContextualEgoNetwork cen) {
        this.myContexts = new ArrayList<Context>();
        this.cen = cen;
    }

    /**
     * Adds a context into MyContexs and associates it with contextual ego network
     * @param context the context
     */
    public void add(@NonNull Context context) {
        myContexts.add(context);
        if (cen != null) {
            cen.getOrCreateContext(context);
        }
    }

    /**
     * Removes a context from MyContexts and from contextual ego network
     * @param context the context
     */
    public void remove(@NonNull Context context) {
        myContexts.remove(context);
        if(cen != null) {
            eu.h2020.helios_social.core.contextualegonetwork.Context cenContext = CenUtils.getCenContext(cen, context);
            if(cenContext != null) {
                cen.removeContext(cenContext);
            }
        }
    }

    /**
     * Returns context by given id
     * @param id the context ids
     * @return
     */
    public Context getContextById(@NonNull String id) {
        for(Context c: myContexts) {
            if(c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Returns currently active contexts
     * @return the Iterator to active contexts list
     */
    public List<Context> getActiveContexts() {
        ArrayList<Context> activeContexts = new ArrayList<Context>();
        for(Context c: myContexts) {
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
        return myContexts;
    }

    /**
     * Returns the associated contextual ego network
     * @return the ContextualEgoNetwork
     */
    public ContextualEgoNetwork getCen() {
        return cen;
    }

    // TODO   save() and restore() ?
}
