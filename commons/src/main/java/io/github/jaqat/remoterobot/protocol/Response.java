package io.github.jaqat.remoterobot.protocol;

import java.io.Serializable;

public class Response implements Serializable {

    private Operation operation;
    private boolean success;
    private String message;
    private Object resultObject;

    public Response(Operation operation) {
        this.operation = operation;
    }

    public Response withMessage(final String message) {
        this.message = message;
        return this;
    }

    public Response withResultObject(final Object resultObject) {
        this.resultObject = resultObject;
        return this;
    }

    public Response withSuccess(final boolean successFlag) {
        this.success = successFlag;
        return this;
    }

    public Operation getOperation() {
        return operation;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getResultObject() {
        return resultObject;
    }
}
