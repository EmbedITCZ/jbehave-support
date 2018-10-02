package org.jbehavesupport.core.test.app.crash;

import java.security.PublicKey;

import org.crsh.auth.AuthenticationPlugin;
import org.crsh.plugin.CRaSHPlugin;

/**
 * Sadly CRaSH seems to have a problem with bouncy castle classes and public key auth fails all the time even for correct keys.
 * As a workaround this class just accepts all connections for any key used.
 */
public class DummyKeyAuthenticationPlugin extends CRaSHPlugin<AuthenticationPlugin> implements AuthenticationPlugin<PublicKey> {

    public DummyKeyAuthenticationPlugin() {
    }

    public String getName() {
        return "dummy-key";
    }

    public DummyKeyAuthenticationPlugin getImplementation() {
        return this;
    }

    public Class<PublicKey> getCredentialType() {
        return PublicKey.class;
    }

    public void init() {

    }

    public boolean authenticate(String username, PublicKey credential) throws Exception {
        return true;
    }
}
