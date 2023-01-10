package me.losin6450.core;

import com.caoccao.javet.values.reference.V8ValueFunction;

/**
 * The interface Server adapter.
 */
public interface ServerAdapter {

    /**
     * Command.
     *
     * @param namespace         the namespace
     * @param name              the name
     * @param aliases           the aliases
     * @param permission        the permission
     * @param permissionMessage the permission message
     * @param executor          the executor
     * @param tabCompleter      the tab completer
     */
    void command(String namespace, String name, String[] aliases, String permission, String permissionMessage, V8ValueFunction executor, V8ValueFunction tabCompleter);
}
