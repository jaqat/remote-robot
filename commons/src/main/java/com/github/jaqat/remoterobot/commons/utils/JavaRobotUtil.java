package com.github.jaqat.remoterobot.commons.utils;

import com.github.jaqat.remoterobot.commons.protocol.Operation;
import com.github.jaqat.remoterobot.commons.protocol.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.function.Function;

public class JavaRobotUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(JavaRobotUtil.class);

    protected Robot robot = null;

    public JavaRobotUtil() {
        try {
            this.robot = new Robot();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Response executeOperation(Operation operation, OperationActions actions, Function<Object, String> successMessage, String errorMessage){
        Response response = new Response(operation);
        try{
            Object result = actions.execute();
            response.withSuccess(true)
                    .withMessage(successMessage.apply(result))
                    .withResultObject(result);
        } catch (Exception e){
            response.withSuccess(false)
                    .withMessage(errorMessage + String.format("Exception occurred: %s (%s)", e.getClass(), e.getMessage()));
        }
        if (response.isSuccess()){
            LOGGER.info(response.getMessage());
        } else {
            LOGGER.error(response.getMessage());
        }
        return response;
    }


}
