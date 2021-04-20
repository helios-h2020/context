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
     * @param contextId
     * @return
     */
    public static eu.h2020.helios_social.core.contextualegonetwork.Context addContext(ContextualEgoNetwork cen, String contextId) {
        return cen.getOrCreateContext(contextId);
    }

    /**
     * Gets contexts of an Alter of cen
     * @param cen
     * param myContexts
     * @param alter
     * @return
     */
    public static List<Context> getContexts(ContextualEgoNetwork cen, MyContexts myContexts, String alter) {
        ArrayList<Context> contexts = new ArrayList<Context>();
        List<eu.h2020.helios_social.core.contextualegonetwork.Context> cenContexts = cen.getContexts();
        for(eu.h2020.helios_social.core.contextualegonetwork.Context cenContext : cenContexts) {
            List<Node> nodes = cenContext.getNodes();
            for(Node node : nodes) {
                if(node.getId().equals(alter)) {
                    Object contextId = cenContext.getData();
                    if(contextId != null && contextId instanceof String) {
                        Context context = myContexts.getContextById((String)contextId);
                        if(context != null) {
                            contexts.add((Context) context);
                        }
                    }
                }
            }
        }
        return contexts;
    }

    /**
     * Checks if a context is associated with cen
     * @param cen
     * @param contextId
     * @return
     */
    public static boolean hasContext(ContextualEgoNetwork cen, String contextId) {
        List<eu.h2020.helios_social.core.contextualegonetwork.Context> cenContexts = cen.getContexts();
        for(eu.h2020.helios_social.core.contextualegonetwork.Context cenContext : cenContexts) {
            if (contextId.equals(cenContext.getData())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets contextual layer of cen on the basis of the related context of the layer
     */
    public static eu.h2020.helios_social.core.contextualegonetwork.Context getCenContext(ContextualEgoNetwork cen, String contextId) {
        List<eu.h2020.helios_social.core.contextualegonetwork.Context> cenContexts = cen.getContexts();
        for(eu.h2020.helios_social.core.contextualegonetwork.Context cenContext : cenContexts) {
            if (contextId.equals(cenContext.getData())) {
                return cenContext;
            }
        }
        return null;
    }

}
