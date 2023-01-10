package me.losin6450.core;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.converters.JavetBridgeConverter;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.reference.V8ValueFunction;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * The type Interceptor.
 */
public class Interceptor {

    private HashMap<String, String> handler;

    /**
     * Instantiates a new Interceptor.
     *
     * @param handler the handler
     */
    public Interceptor(HashMap<String, String> handler){
        this.handler = handler;
    }

    /**
     * Intercept object.
     *
     * @param self      the self
     * @param arguments the arguments
     * @param method    the method
     * @return the object
     * @throws InvocationTargetException the invocation target exception
     * @throws IllegalAccessException    the illegal access exception
     * @throws JavetException            the javet exception
     */
    @RuntimeType
    public Object intercept(@This Object self, @AllArguments Object[] arguments, @Origin Method method) throws InvocationTargetException, IllegalAccessException, JavetException {
        String name = method.getName();
        if(handler.containsKey(name)){
            NodeRuntime runtime = ScriptManager.mainEngine.getV8Runtime();
            runtime.setConverter(new JavetBridgeConverter());
            runtime.getGlobalObject().set("Java", new JavaAPI());
            V8ValueFunction function = runtime.getExecutor(handler.get(name)).execute();
            Object value = function.call(null, self, arguments);
            JavetResourceUtils.safeClose(runtime, function);
            return value;
        } else {
            return method.invoke(self, arguments);
        }
    }
}