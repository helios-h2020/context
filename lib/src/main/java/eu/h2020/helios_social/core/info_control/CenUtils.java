package eu.h2020.helios_social.core.info_control;

import java.util.ArrayList;
import java.util.List;

import eu.h2020.helios_social.core.context.Context;

import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;
import eu.h2020.helios_social.core.contextualegonetwork.Node;

/**
 * Static utility methods for Contextual Ego Network.
 */
public class CenUtils {

    /**
     * Associates context with contextual ego network
     * @param cen
     * @param context
     * @return
     */
    public static eu.h2020.helios_social.core.contextualegonetwork.Context addContext(ContextualEgoNetwork cen, Context context) {
        return cen.getOrCreateContext(context);
    }

    /**
     * Gets contexts of an Alter of cen
     * @param cen
     * @param alter
     * @return
     */
    public static List<Context> getContexts(ContextualEgoNetwork cen, String alter) {
        ArrayList<Context> contexts = new ArrayList<Context>();
        List<eu.h2020.helios_social.core.contextualegonetwork.Context> cenContexts = cen.getContexts();
        for(eu.h2020.helios_social.core.contextualegonetwork.Context cenContext : cenContexts) {
            List<Node> nodes = cenContext.getNodes();
            for(Node node : nodes) {
                if(node.getId().equals(alter)) {
                    Object context = cenContext.getData();
                    if(context != null && context instanceof Context) {
                        contexts.add((Context)context);
                    }
                }
            }
        }
        return contexts; // contexts.iterator();
    }

    /**
     * Checks if a context is associated with cen
     * @param cen
     * @param context
     * @return
     */
    public static boolean hasContext(ContextualEgoNetwork cen, Context context) {
        List<eu.h2020.helios_social.core.contextualegonetwork.Context> cenContexts = cen.getContexts();
        for(eu.h2020.helios_social.core.contextualegonetwork.Context cenContext : cenContexts) {
            if (context.equals(cenContext.getData())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets contextual layer of cen on the basis of the related context of the layer
     */
    public static eu.h2020.helios_social.core.contextualegonetwork.Context getCenContext(ContextualEgoNetwork cen, Context context) {
        List<eu.h2020.helios_social.core.contextualegonetwork.Context> cenContexts = cen.getContexts();
        for(eu.h2020.helios_social.core.contextualegonetwork.Context cenContext : cenContexts) {
            if (context.equals(cenContext.getData())) {
                return cenContext;
            }
        }
        return null;
    }

    /**
     * Gets the trust value of an Alter in a Context
     * @param cen the Contextual Ego Network
     * @param alter the Alter node
     * @param context the Context
     * @return the trust value
     */
    public static double getTrust(ContextualEgoNetwork cen, String alter, Context context) {
        //  trustManager.getTrust(cenContext, alterNode);     // TODO.   CEN does not currently provide getTrust method?  Should use directly trustmanager
        return -1.0;
    }

}
