package org.jbehavesupport.core.internal.verification;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.verification.Verifier;
import org.jbehavesupport.core.verification.VerifierResolver;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;

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

    public Verifier getVerifierByName(String name, Verifier verifierIfNameEmpty) {
        return hasText(name) ? getVerifierByName(name) : verifierIfNameEmpty;
    }

}
