package com.github.jaqat.remoterobot.server.utils;

@FunctionalInterface
public interface OperationExecution {

    /*
     Execute actions that can return some result object
     */
    Object execute() throws Exception;
}
