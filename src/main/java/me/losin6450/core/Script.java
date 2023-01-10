package me.losin6450.core;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.converters.JavetBridgeConverter;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.utils.JavetResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Script.
 */
public class Script {

    /**
     * The enum Script type.
     */
    public enum ScriptType {
        /**
         * File script type.
         */
        FILE,
        /**
         * String script type.
         */
        STRING
    }

    private final ScriptType type;
    private String code;
    private NodeRuntime runtime;
    private List<Script> children;
    private IJavetEngine<NodeRuntime> engine;
    private final String meta;
    private final String root;
    private final Script parent;
    private ScriptAPI api;
    private Config config;

    /**
     * Instantiates a new Script.
     *
     * @param file   the file
     * @param meta   the meta
     * @param root   the root
     * @param parent the parent
     * @param config the config
     * @throws JavetException the javet exception
     * @throws IOException    the io exception
     */
    public Script(File file, String meta, String root, Script parent, Config config) throws JavetException, IOException {
        this.type = ScriptType.FILE;
        this.meta = meta;
        this.root = root;
        this.parent = parent;
        this.engine = ScriptManager.pool.getEngine();
        this.code = Files.readString(Path.of(file.toURI()));
        this.children = new ArrayList<Script>();
        this.config = config;
    }

    /**
     * Instantiates a new Script.
     *
     * @param code   the code
     * @param meta   the meta
     * @param root   the root
     * @param parent the parent
     * @param config the config
     * @throws JavetException the javet exception
     */
    public Script(String code, String meta, String root, Script parent, Config config) throws JavetException {
        this.type = ScriptType.STRING;
        this.meta = meta;
        this.root = root;
        this.parent = parent;
        this.engine = ScriptManager.pool.getEngine();
        this.code = code;
        this.children = new ArrayList<Script>();
        this.config = config;
    }

    /**
     * Execute.
     *
     * @throws JavetException the javet exception
     */
    public void execute() throws JavetException {
        if(this.runtime != null) return;
        this.runtime = engine.getV8Runtime();
        JavetBridgeConverter converter = new JavetBridgeConverter();
        this.runtime.setConverter(converter);
        this.runtime.getGlobalObject().set("Java", new JavaAPI());
        this.api = new ScriptAPI(this);
        this.runtime.getGlobalObject().set("me.losin6450.core.ScriptAPI", api);
        this.runtime.getExecutor(this.code).executeVoid();
    }

    /**
     * Close.
     */
    public void close(){
        if(this.runtime == null) return;
        this.children.forEach(Script::destroy);
        JavetResourceUtils.safeClose(runtime);
        this.runtime = null;
    }

    /**
     * Destroy.
     */
    public void destroy(){
        if(this.engine == null) return;
        if(this.parent != null) this.parent.children.remove(this);
        this.close();
        JavetResourceUtils.safeClose(this.engine);
        this.engine = null;
    }

    /**
     * Gets meta.
     *
     * @return the meta
     */
    public String getMeta() {
        return meta;
    }

    /**
     * Gets root.
     *
     * @return the root
     */
    public String getRoot() {
        return root;
    }

    /**
     * Gets api.
     *
     * @return the api
     */
    public ScriptAPI getApi() {
        return api;
    }

    /**
     * Gets runtime.
     *
     * @return the runtime
     */
    public NodeRuntime getRuntime() {
        return runtime;
    }

    /**
     * Gets config.
     *
     * @return the config
     */
    public Config getConfig() {
        return config;
    }
}