package com.github.jaqat.remoterobot.commons.utils;

@FunctionalInterface
public interface OperationActions {

    /*
     Execute actions that can return some result object
     */
    Object execute() throws Exception;
}
