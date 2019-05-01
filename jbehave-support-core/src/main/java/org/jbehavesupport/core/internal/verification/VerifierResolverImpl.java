package org.jbehavesupport.core.internal.verification;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.verification.Verifier;
import org.jbehavesupport.core.verification.VerifierResolver;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;

@RequiredArgsConstructor
public class VerifierResolverImpl implements VerifierResolver {

    private final List<Verifier> verifiers;

    public Verifier getVerifierByName(String name) {
        return verifiers.stream()
            .filter(v -> name.equals(v.name()))
            .reduce((a, b) -> {
                if (b != null) {
                    throw new NoUniqueBeanDefinitionException(Verifier.class, b.name());
                }
                return a;
            })
            .orElseThrow(() -> new NoSuchBeanDefinitionException(name));
    }

}
