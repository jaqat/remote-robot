package io.github.jaqat.remoterobot.server.utils;


import io.github.jaqat.remoterobot.common.Response;

import static io.github.jaqat.remoterobot.common.Operation.MOUSE_CLICK;

public class MouseUtil extends JavaRobotUtil {

    public Response mouseClick(int x, int y, int buttonMask) {
        return executeOperation(
                MOUSE_CLICK,
                () -> {
                    robot.mouseMove(x, y);
                    robot.mousePress(buttonMask);
                    robot.mouseRelease(buttonMask);
                    return null;
                },
                (result) -> String.format("Clicked to coordinates [%d : %d]", x, y),
                "Unable to mouse click."
        );
    }
}
