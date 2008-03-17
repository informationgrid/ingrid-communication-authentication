package de.ingrid.communication.authentication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BasicSchemeConnector implements IHttpProxyAuthenticator {

    private static Log LOG = LogFactory.getLog(BasicSchemeConnector.class);

    private static final String CRLF = "\r\n";

    private static final String ACCEPT_MESSAGE_HTTP_1_1 = "HTTP/1.1 200 OK";

    private static final String ACCEPT_MESSAGE_HTTP_1_0 = "HTTP/1.0 200 OK";

    public boolean connect(Socket socket, String host, int port) throws IOException {
        DataInputStream dataInput = createInput(socket);
        DataOutputStream dataOutput = createOutput(socket);
        StringBuffer errorBuffer = new StringBuffer();
        String command = createConnectCommand(host, port);
        errorBuffer.append(command);
        dataOutput.write(command.getBytes());
        dataOutput.flush();
        boolean success = readMessageFromHttpProxy(dataInput, errorBuffer);
        if (!success) {
            if (LOG.isErrorEnabled()) {
                LOG.error(errorBuffer);
            }
        }
        return success;
    }

    public boolean connect(Socket socket, String host, int port, String userName, String password) throws IOException {
        DataInputStream dataInput = createInput(socket);
        DataOutputStream dataOutput = createOutput(socket);
        StringBuffer errorBuffer = new StringBuffer();
        String command = createConnectCommand(host, port, userName, password);
        errorBuffer.append(command);
        dataOutput.write(command.getBytes());
        dataOutput.flush();
        boolean success = readMessageFromHttpProxy(dataInput, errorBuffer);
        if (!success) {
            if (LOG.isErrorEnabled()) {
                LOG.error(errorBuffer);
            }
        }
        return success;
    }

    private DataOutputStream createOutput(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(outputStream, 65535));
        return dataOutput;
    }

    private DataInputStream createInput(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        DataInputStream dataInput = new DataInputStream(new BufferedInputStream(inputStream, 65535));
        return dataInput;
    }

    private String createConnectCommand(String host, int port, String proxyUser, String proxyPassword) {
        StringBuffer builder = new StringBuffer();
        builder.append("CONNECT " + host + ":" + port + " HTTP/1.1" + CRLF);
        builder.append("HOST: " + host + ":" + port + CRLF);
        appendAuthenticationTokens(proxyUser, proxyPassword, builder);
        builder.append(CRLF);
        return builder.toString();
    }

    private String createConnectCommand(String host, int port) {
        StringBuffer builder = new StringBuffer();
        builder.append("CONNECT " + host + ":" + port + " HTTP/1.1" + CRLF);
        builder.append("HOST: " + host + ":" + port + CRLF);
        builder.append(CRLF);
        return builder.toString();
    }

    private void appendAuthenticationTokens(String proxyUser, String proxyPassword, StringBuffer builder) {
        String authString = proxyUser + ":" + proxyPassword;
        String auth = "Basic " + new String(Base64.encodeBase64(authString.getBytes()));
        builder.append(("Proxy-Authorization: " + auth + CRLF));
    }

    private boolean readMessageFromHttpProxy(DataInputStream dataInput, StringBuffer errorBuffer) throws IOException {
        boolean ret = false;
        byte[] buffer = new byte[1024];
        while ((dataInput.read(buffer, 0, buffer.length)) != -1) {
            String readedString = new String(buffer);
            errorBuffer.append(readedString);
            if (readedString.toLowerCase().indexOf(ACCEPT_MESSAGE_HTTP_1_0.toLowerCase()) > -1) {
                ret = true;
                break;
            } else if (readedString.toLowerCase().indexOf(ACCEPT_MESSAGE_HTTP_1_1.toLowerCase()) > -1) {
                ret = true;
                break;
            }
            buffer = new byte[1024];
        }
        return ret;
    }

}
