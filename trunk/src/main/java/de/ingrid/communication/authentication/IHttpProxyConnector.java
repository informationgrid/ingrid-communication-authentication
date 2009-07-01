package de.ingrid.communication.authentication;

import java.io.IOException;
import java.net.Socket;

public interface IHttpProxyConnector {

    boolean connect(Socket socket, String host, int port) throws IOException;

    boolean connect(Socket socket, String host, int port, String userName, String password) throws IOException;
}
