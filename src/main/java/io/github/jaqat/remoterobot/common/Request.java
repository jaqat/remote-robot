package io.github.jaqat.remoterobot.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Request implements Serializable {

    private Operation operation;
    private List<Object> operands;

    public Request(Operation operation, Object... operands) {
        this.operation = operation;
        this.operands = Arrays.asList(operands);
    }

    public Operation getOperation() {
        return operation;
    }

    public List<Object> getOperands() {
        return operands;
    }
}
