package com.github.jaqat.remoterobot.client;

import com.github.jaqat.remoterobot.commons.protocol.Request;
import com.github.jaqat.remoterobot.commons.protocol.Response;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class ClientHandler extends IoHandlerAdapter {
    private final Request request;
    private Response response;

    public ClientHandler(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return this.response;
    }

    @Override
    public void sessionOpened(IoSession session) {
        session.write(request);
    }

    @Override
    public void messageReceived(IoSession session, Object rawCommandResponse) {
        this.response = (Response)rawCommandResponse;
        System.out.println(response.getMessage());
    }

    public void exceptionCaught(IoSession session, Throwable throwable) {
        throwable.printStackTrace();
        session.close(true);
    }
}
