package eu.h2020.helios_social.core.context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is an abstract base class for context attributes.
 * A context attribute includes a name and a value.
 * It can also be related to one or more context sources,
 * and the value of the attribute may be determined
 * by the sources (i.e. sensors or other data sources).
 */
public abstract class ContextAttribute {

	private final String attributeName;
	private final List<ContextSource> sources;

	/**
	 * Creates an context attribute
	 * @param attributeName the name of this context attribute
	 */
	public ContextAttribute(String attributeName) {
		this.attributeName = attributeName;
		this.sources = new ArrayList<ContextSource>();
	}

	/**
	 * Gets name of a context attribute
	 * @return the name of this attribute
	 */
	public String getName() {
		return attributeName;
	}

	/**
	 * Gets value of this context attribute.
	 * This is an abstract method that should be implemented by the upper classes.
	 * @return the value of this attribute
	 */
	public abstract Object getValue();

	/**
	 * Gets all the context sources
	 * @return the context sources
	 */
	public Iterator<ContextSource> getContextSources() {
		return sources.iterator();
	}

	/**
	 * Adds a new context source
	 * @param source the context source
	 */
	public void addContextSource(ContextSource source) {
		sources.add(source);
	}

	/**
	 * Removes a context source
	 * @param source the context source
	 */
	public void removeContextSource(ContextSource source) {
		sources.remove(source);
	}
}
