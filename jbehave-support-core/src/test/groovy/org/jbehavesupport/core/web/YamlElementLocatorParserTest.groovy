package org.jbehavesupport.core.web

import org.jbehavesupport.core.internal.web.WebElementRegistryImpl
import org.jbehavesupport.core.internal.web.YamlElementLocatorParser
import org.jbehavesupport.core.internal.web.by.ByFactoryResolverImpl
import org.jbehavesupport.core.internal.web.by.CssByFactory
import org.jbehavesupport.core.internal.web.by.XpathByFactory
import org.junit.Test

import static org.openqa.selenium.By.cssSelector
import static org.openqa.selenium.By.xpath

class YamlElementLocatorParserTest {

    @Test
    void shouldParseLocators() {
        def registry = new WebElementRegistryImpl()
        def cssFactory = new CssByFactory()
        def creatorResolver = new ByFactoryResolverImpl(Arrays.asList(new XpathByFactory(), cssFactory))
        def parser = new YamlElementLocatorParser(registry, creatorResolver, cssFactory)
        parser.init()

        parser.processEntry("p-g.el1", "#id1")
        parser.processEntry("p.el-2.css", "#id2")
        parser.processEntry("p.el3.fo-o", "#id3")
        parser.processEntry("p.el4.bar.css", "#id4")
        parser.processEntry("p.el5.xpath", "//@id='5'")

        assert registry.getLocator("p-g", "el1") == cssSelector("#id1")
        assert registry.getLocator("p", "el-2") == cssSelector("#id2")
        assert registry.getLocator("p", "el3.fo-o") == cssSelector("#id3")
        assert registry.getLocator("p", "el4.bar") == cssSelector("#id4")
        assert registry.getLocator("p", "el5") == xpath("//@id='5'")
    }

}
