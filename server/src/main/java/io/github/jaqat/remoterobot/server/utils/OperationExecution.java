package io.github.jaqat.remoterobot.server.utils;

@FunctionalInterface
public interface OperationExecution {

    /**
     * Execute actions that can return some result object
     * @return action's result
     * @throws Exception
     */
    Object execute() throws Exception;
}
