package me.losin6450.core;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.converters.JavetBridgeConverter;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.reference.V8ValueFunction;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The type Script api.
 */
public class ScriptAPI {

    private Script instance;
    private HashMap<String, List<String>> listeners;

    /**
     * Instantiates a new Script api.
     *
     * @param instance the instance
     */
    public ScriptAPI(Script instance){
        this.instance = instance;
        this.listeners = new HashMap<>();
    }

    /**
     * Get meta string.
     *
     * @return the string
     */
    public String getMeta(){
        return this.instance.getMeta();
    }

    /**
     * Get root string.
     *
     * @return the string
     */
    public String getRoot(){
        return this.instance.getRoot();
    }

    /**
     * Add listener.
     *
     * @param event    the event
     * @param function the function
     * @throws JavetException the javet exception
     */
    public void addListener(String event, V8ValueFunction function) throws JavetException {
        this.listeners.computeIfAbsent(event, k -> new ArrayList<>());
        this.listeners.get(event).add(function.getSourceCode());
        JavetResourceUtils.safeClose(function);
    }

    /**
     * Emit.
     *
     * @param event the event
     * @param args  the args
     */
    public void emit(String event, Object[] args){
        this.listeners.get(event).forEach(listener -> {
            try {
                if(instance.getRuntime() != null && !instance.getRuntime().isClosed()){
                    V8ValueFunction func = instance.getRuntime().getExecutor(listener).execute();
                    func.call(null, args);
                    JavetResourceUtils.safeClose(func);
                } else {
                    NodeRuntime rt = ScriptManager.mainEngine.getV8Runtime();
                    JavetBridgeConverter converter = new JavetBridgeConverter();
                    rt.setConverter(converter);
                    rt.getGlobalObject().set("Java", new JavaAPI());
                    V8ValueFunction func = rt.getExecutor(listener).execute();
                    func.call(null, args);
                    JavetResourceUtils.safeClose(func, rt);
                }
            } catch (JavetException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Get config config.
     *
     * @return the config
     */
    public Config getConfig(){
        return instance.getConfig();
    }

    /**
     * Reload.
     *
     * @throws JavetException the javet exception
     */
    public void reload() throws JavetException {
        this.instance.close();
        this.instance.execute();
    }
}