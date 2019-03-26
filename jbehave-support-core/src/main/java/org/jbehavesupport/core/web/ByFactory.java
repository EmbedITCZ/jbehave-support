package org.jbehavesupport.core.web;

import org.openqa.selenium.By;

public interface ByFactory {

    By getBy(String value);

    String getType();

}
