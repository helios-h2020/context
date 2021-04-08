package eu.h2020.helios_social.core.context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * This is the base class for HELIOS contexts.
 * This class provides base methods for context implementations.
 * A context includes a type, a state (active or inactive) and possible several attributes that are
 * used to define and detect the context. Values of context attributes may be determined
 * explicitly (e.g. a given static value) or implicitly (e.g. by an external context source, sensor value).
 * To listen the changes in the active value of a context, context listeners (see the {@link eu.h2020.helios_social.core.context.ContextListener} interface)
 * can be registered for the context.
 * New context types can be created by extending this class.
 */
public class Context {
	private final String id;
	private String name;
	private boolean active;
	private final List<ContextAttribute> attributes;
	private final List<ContextListener> listeners;

	/**
	 * Creates a context
	 * @param id the identifier of this context.
	 * @param name the name of this context
	 * @param active is this context active
	 */
	public Context(final String id, String name, boolean active) {
		// if given id == null, generates a new id for the context
		this.id = (id == null) ? UUID.randomUUID().toString() : id;
		this.name = name;
		this.active = active;
		attributes = new ArrayList<ContextAttribute>();
		listeners = new ArrayList<ContextListener>();
	}

	/**
	 * Creates a context
	 * @param name the name of this context
	 * @param active is this context active
	 */
	public Context(String name, boolean active) {
		this(null, name, active);
	}

	/**
	 * Gets identifier of this context
	 * @return the identifier of this context
	 */
	public final String getId() { return id; }

	/**
	 * Is context active?
	 * @return active the active value(boolean)
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Gets name of this context
	 * @return name the name of this context
	 */
	public String getName() { return name; }

	/**
	 * Sets context name
	 * @param name the name of this context
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets context active value
	 * @param active the active value
	 */
	public void setActive(boolean active) {
		if(active != this.active) {
			this.active = active;
			for (ContextListener listener : listeners) {
				listener.contextChanged(active);
			}
		}
	}

	/**
	 * Adds attribute for the context
	 * @param attr the attribute
	 */
	@Deprecated
	public void addAttribute(ContextAttribute attr) {
		attributes.add(attr);
	}

	/**
	 * Removes context attribute
	 * @param attr the attribute
	 */
	@Deprecated
	public void removeAttribute(ContextAttribute attr) {
		attributes.remove(attr);
	}

	/**
	 * Gets all the attributes related to this context
	 * @return the attributes
	 */
	@Deprecated
	public Iterator<ContextAttribute> getAttributes() {
		return attributes.iterator();
	}

	/**
	 * Registers a listener (ContextListener) for this context.
	 * The ContextListener will then receive changes in the context active value.
	 * @param listener the ContextListener
	 */
	public void registerContextListener(ContextListener listener) {
		listeners.add(listener);
	}

	/**
	 * Unregisters a ContextListener.
	 * @param listener the ContextListener
	 */
	public void unregisterContextListener(ContextListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Gets all the listeners related to this context
	 * @return the listeners
	 */
	public Iterator<ContextListener> getContextListeners() {
		return listeners.iterator();
	}
}
