package org.jbehavesupport.core.internal.web.by;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.web.ByFactory;
import org.jbehavesupport.core.web.ByFactoryResolver;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;

@RequiredArgsConstructor
public class ByFactoryResolverImpl implements ByFactoryResolver {

    private final List<ByFactory> byFactories;

    @Override
    public ByFactory resolveByFactory(String type) {
        return byFactories.stream()
            .filter(c -> c.getType().equals(type))
            .reduce((a, b) -> {
                if (b != null) {
                    throw new NoUniqueBeanDefinitionException(ByFactory.class, b.getType());
                }
                return a;
            })
            .orElseThrow(() -> new IllegalArgumentException("No ByFactory found for given name [" + type + "]."));
    }

    @Override
    public List<String> getRegisteredTypes() {
        return byFactories.stream()
            .map(ByFactory::getType)
            .collect(Collectors.toList());
    }

}
