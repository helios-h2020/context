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
        if(cen != null) {
            return cen.getOrCreateContext(contextId);
        }
        return null;
    }

    /**
     * Gets contexts of an Alter of cen
     * @param cen
     * param myContexts
     * @param alter
     * @return the list of contexts
     */
    public static List<Context> getContexts(ContextualEgoNetwork cen, MyContexts myContexts, String alter) {
        ArrayList<Context> contexts = new ArrayList<>();
        if(cen != null) {
            List<eu.h2020.helios_social.core.contextualegonetwork.Context> cenContexts = cen.getContexts();
            for (eu.h2020.helios_social.core.contextualegonetwork.Context cenContext : cenContexts) {
                List<Node> nodes = cenContext.getNodes();
                for (Node node : nodes) {
                    if (node.getId().equals(alter)) {
                        Object contextId = cenContext.getData();
                        if (contextId != null && contextId instanceof String) {
                            Context context = myContexts.getContextById((String) contextId);
                            if (context != null) {
                                contexts.add((Context) context);
                            }
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
     * @return boolean
     */
    public static boolean hasContext(ContextualEgoNetwork cen, String contextId) {
        if(cen != null) {
            List<eu.h2020.helios_social.core.contextualegonetwork.Context> cenContexts = cen.getContexts();
            for (eu.h2020.helios_social.core.contextualegonetwork.Context cenContext : cenContexts) {
                if (contextId.equals(cenContext.getData())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets contextual layer of cen on the basis of the related context of the layer
     */
    public static eu.h2020.helios_social.core.contextualegonetwork.Context getCenContext(ContextualEgoNetwork cen, String contextId) {
        if(cen != null) {
            List<eu.h2020.helios_social.core.contextualegonetwork.Context> cenContexts = cen.getContexts();
            for (eu.h2020.helios_social.core.contextualegonetwork.Context cenContext : cenContexts) {
                if (contextId.equals(cenContext.getData())) {
                    return cenContext;
                }
            }
        }
        return null;
    }

    /**
     * Adds alter to cen context
     * @param alterId
     * @param contextId
     */
    public static void addAlter(ContextualEgoNetwork cen, String alterId, String contextId) {
        if(cen != null) {
            eu.h2020.helios_social.core.contextualegonetwork.Context cenContext = CenUtils.getCenContext(cen, contextId);
            if(cenContext != null) {
                eu.h2020.helios_social.core.contextualegonetwork.Node alterNode = cen.getOrCreateNode(alterId);
                if(alterNode != null) {
                    cenContext.getOrAddEdge(cen.getEgo(), alterNode);
                }
            }
        }
    }

    /**
     * Removes alter from cen context
     * @param alterId
     * @param contextId
     */
    public static void removeAlter(ContextualEgoNetwork cen, String alterId, String contextId) {
        if(cen != null) {
            eu.h2020.helios_social.core.contextualegonetwork.Context cenContext = CenUtils.getCenContext(cen, contextId);
            if(cenContext != null) {
                eu.h2020.helios_social.core.contextualegonetwork.Node alterNode = cen.getOrCreateNode(alterId);
                if(alterNode != null) {
                    cenContext.removeEdge(cen.getEgo(), alterNode);
                }
            }
        }
    }
}
