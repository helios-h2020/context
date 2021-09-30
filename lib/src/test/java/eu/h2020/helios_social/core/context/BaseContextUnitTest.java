package eu.h2020.helios_social.core.context;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Base context local unit tests
 *
 * @see Context
 */
public class BaseContextUnitTest {

    @Test
    public void contextName() {
        Context context = new Context("testContext", false);
        assertEquals(context.getName(), "testContext");
    }

    @Test
    public void contextId() {
        Context context = new Context("testContext", false);
        assertNotNull(context.getId());
        String id = "contextID123";
        context = new Context(id, "testContext", false);
        assertEquals(context.getId(), id);
    }

    @Test
    public void contextIsActive() {
        Context context = new Context("testContext", true);
        assertTrue(context.isActive());
        context = new Context("testContext", false);
        assertFalse(context.isActive());
        context.setActive(false);
        assertFalse(context.isActive());
        context.setActive(true);
        assertTrue(context.isActive());
    }

    @Test
    public void contextRegisterListener() {
        Context context = new Context("testContext", true);
        ContextListener listener = new ContextListener() {
            @Override
            public void contextChanged(boolean active) {
            }
        };
        context.registerContextListener(listener);

        Iterator<ContextListener> listeners = context.getContextListeners();
        while(listeners.hasNext()) {
            assertEquals(listeners.next(), listener);
        }

        context.unregisterContextListener(listener);
        listeners = context.getContextListeners();
        assertFalse(listeners.hasNext());
    }

    @Test
    public void contextListener() {
        final Context context = new Context("testContext", false);

        ContextListener listener = new ContextListener() {
            @Override
            public void contextChanged(boolean active) {
                assertEquals(context.isActive(), active);
            }
        };
        context.registerContextListener(listener);

        context.setActive(true);  // this should call the ContextListener's method contextChanged
        context.setActive(false);
    }

}