package org.jbehavesupport.core.verification;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;

/**
 * Component for obtaining correct verifier. If only one Verifier is desired, it should be @Autowired
 */
public interface VerifierResolver {

    /**
     * Finds single registered bean by verifier name. Exactly one must exist otherwise {@link NoUniqueBeanDefinitionException} or {@link NoSuchBeanDefinitionException} is thrown.
     *
     * @param name Verifier name regards bean setup, or from {@link VerifierNames}
     * @return Single found verifier.
     */
    Verifier getVerifierByName(String name);

    /**
     * Finds single registered bean by verifier name, if name is empty, returns a specified default verifier. Exactly one must exist otherwise {@link NoUniqueBeanDefinitionException} or {@link NoSuchBeanDefinitionException}
     *
     * @param name Verifier name regards bean setup, or from {@link VerifierNames}
     * @param verifierIfNameEmpty Name of a default verifier
     * @return Single found verifier or the specified default verifier
     */
    Verifier getVerifierByName(String name, Verifier verifierIfNameEmpty);
}
