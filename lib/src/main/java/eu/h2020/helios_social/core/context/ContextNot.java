package eu.h2020.helios_social.core.context;

import androidx.annotation.NonNull;

/**
 * This class is defined by an other context and this context is active when
 * the related context is inactive (NOT operation). The class extends the base class Context.<br/>
 *
 * The context active value is updated using the setActive method. The current value of the context
 * can always be checked using the isActive method of the context.</br>
 *
 * If the application needs to track the
 * changes in the active value of the context then the application should implement also
 * the ContextListener interface {@see eu.h2020.helios_social.core.context.ContextListener} and
 * register the context for the listener.
 */
public class ContextNot extends Context implements ContextListener {

    private final Context context;

    /**
     * Creates a ContextNot context
     * @param name the name of the context
     * @param context the related context
     */
    public ContextNot(String name, @NonNull Context context) {
        this(null, name, context);
    }

    /**
     * Creates a ContextNot context
     * @param id the identifier of the context
     * @param name the name of the context
     * @param context the related context
     */
    public ContextNot(String id, String name, @NonNull Context context) {

        super(id, name, !context.isActive());
        this.context = context;
        context.registerContextListener(this);
    }

    @Override
    public void contextChanged(boolean active) {
        setActive(!context.isActive());
    }
}
