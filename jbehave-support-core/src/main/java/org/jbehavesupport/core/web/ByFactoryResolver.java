package org.jbehavesupport.core.web;

import java.util.List;

public interface ByFactoryResolver {

    ByFactory resolveByFactory(String type);

    List<String> getRegisteredTypes();

}
