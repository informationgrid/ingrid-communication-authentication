package de.ingrid.communication.authentication;

public interface IHttpProxyAuthenticationFactory {

	IHttpProxyAuthenticator createBasicAuthenticator();
}
