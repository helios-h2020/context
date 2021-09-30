package eu.h2020.helios_social.core.context;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Combination (Boolean) context local unit tests
 *
 * @see ContextAnd
 * @see ContextOr
 * @see ContextNot
 */
public class CombinationContextUnitTest {
    Context contextA = new Context("A", true);
    Context contextB = new Context("B", false);
    Context contextNotA = new ContextNot("NotA", contextA);
    Context contextNotB = new ContextNot("NotB", contextB);

    @Test
    public void contextNot_isCorrect() {
        assertFalse(contextNotA.isActive());
        assertTrue(contextNotB.isActive());
        Context contextNotNotA = new ContextNot("NotNotA", contextNotA);
        assertTrue(contextNotNotA.isActive());
        contextA.setActive(false);
        assertFalse(contextNotNotA.isActive());
    }

    @Test
    public void contextOr_isCorrect() {
        contextA.setActive(true);
        contextB.setActive(false);
        Context contextOr = new ContextOr("Or", contextA, contextB);
        assertTrue(contextOr.isActive());
        contextA.setActive(false);
        assertFalse(contextOr.isActive());
        Context contextNotAOrB = new ContextOr("NotAOrB", contextNotA, contextB);
        assertTrue(contextNotAOrB.isActive());
    }

    @Test
    public void contextAnd_isCorrect() {
        contextA.setActive(true);
        contextB.setActive(false);
        Context contextAnd = new ContextAnd("And", contextA, contextB);
        assertFalse(contextAnd.isActive());
        contextB.setActive(true);
        assertTrue(contextAnd.isActive());
        Context contextNotAAndB = new ContextAnd("NotAAndB", contextNotA, contextB);
        assertFalse(contextNotAAndB.isActive());
    }
}