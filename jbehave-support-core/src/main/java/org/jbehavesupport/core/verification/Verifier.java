package org.jbehavesupport.core.verification;

/**
 * API for assertions.
 */
public interface Verifier {

    /**
     * Symbolic name of verifier also represented by verifier column.
     *
     * @return verifier abbreviation
     */
    String name();

    /**
     * Throws exception according to verifier type
     *
     * @param actual   value to be checked
     * @param expected value to be compared against
     */
    void verify(Object actual, Object expected);
}
