package org.jbehavesupport.core.verification;

import org.jbehavesupport.core.internal.verification.VerifierNames;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;

/**
 * Component for obtaining correct verifier. If only one Verifier is desired, it should be @Autowired
 */
public interface VerifierResolver {

    /**
     * Finds single registered bean by verifier name. Exactly one must exist otherwise {@link NoUniqueBeanDefinitionException} or {@link NoSuchBeanDefinitionException} is thrown.
     * @param name Verifier name regards bean setup, or from {@link VerifierNames}
     * @return Single found verifier.
     */
    Verifier getVerifierByName(String name);
}
