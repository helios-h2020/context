package eu.h2020.helios_social.core.context;

/**
 * ContextListener is an interface for tracking context active value changes.
 *
 * This interface includes the method contextChanged, which informs that the active value (true/false) of the
 * context was changed.
 * In order to register a context listener for a context, see the method <br/>
 * {@link eu.h2020.helios_social.core.context.Context#registerContextListener}.
 */
public interface ContextListener {

    /**
     * This method is called when context active value changed.
     *
     * @param active the context active value
     */
    void contextChanged(boolean active);
}
