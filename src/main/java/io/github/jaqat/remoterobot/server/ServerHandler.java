package io.github.jaqat.remoterobot.server;

import io.github.jaqat.remoterobot.server.utils.KeyUtil;
import io.github.jaqat.remoterobot.server.utils.MouseUtil;
import io.github.jaqat.remoterobot.common.Request;
import io.github.jaqat.remoterobot.common.Response;
import io.github.jaqat.remoterobot.server.utils.ScreenUtil;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class ServerHandler extends IoHandlerAdapter {
    private Response response;

    public void sessionOpened(IoSession session) {
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        session.setAttribute("Values: ");
    }

    public void messageReceived(IoSession session, Object requestObject) {
        Request request = (Request) requestObject;

        switch (request.getOperation()) {
            case KEY_PRESS:
                response = new KeyUtil().keyPress(
                        Integer.parseInt(request.getOperands().get(0).toString())
                );
                break;

            case MOUSE_CLICK:

                response = new MouseUtil().mouseClick(
                        Integer.parseInt(request.getOperands().get(0).toString()),
                        Integer.parseInt(request.getOperands().get(1).toString()),
                        Integer.parseInt(request.getOperands().get(2).toString())
                );
                break;

            case CAPTURE_SCREEN:
                if (request.getOperands().isEmpty()) {
                    response = new ScreenUtil().captureScreen();
                } else {
                    response = new ScreenUtil().captureScreen(
                            Integer.parseInt(request.getOperands().get(0).toString()),
                            Integer.parseInt(request.getOperands().get(1).toString()),
                            Integer.parseInt(request.getOperands().get(2).toString()),
                            Integer.parseInt(request.getOperands().get(3).toString())
                    );
                }
                break;


            case GET_PIXEL_COLOR:
                response = new ScreenUtil().getColor(
                        Integer.parseInt(request.getOperands().get(0).toString()),
                        Integer.parseInt(request.getOperands().get(1).toString())
                );
                break;

            default:
                throw new RuntimeException("Not supported operation: " + request.getOperation());
        }

        session.write(this.response);
        session.close(false);
        this.response = null;
    }

    public void sessionIdle(IoSession session, IdleStatus idleStatus) {
        session.close(true);
    }

    public void exceptionCaught(IoSession session, Throwable cause) {
        cause.printStackTrace();
        session.close(true);
    }
}
