package de.ingrid.communication.authentication;

public class HttpProxyAuthenticationFactory implements IHttpProxyAuthenticationFactory {

    public IHttpProxyAuthenticator createBasicAuthenticator() {
        return new BasicSchemeConnector();
    }

}
