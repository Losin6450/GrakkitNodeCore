package me.losin6450.core;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The type Java api.
 */
public class JavaAPI {

    /**
     * Type class.
     *
     * @param name the name
     * @return the class
     * @throws ClassNotFoundException the class not found exception
     */
    public Class<?> type(String name) throws ClassNotFoundException {
        return getClass().getClassLoader().loadClass(name);
    }

    /**
     * Extend class.
     *
     * @param superClass  the super class
     * @param interfacess the interfacess
     * @param handler     the handler
     * @return the class
     * @throws JavetException the javet exception
     */
    public Class<?> extend(Class<?> superClass, V8ValueArray interfacess, V8ValueObject handler) throws JavetException {
        ArrayList<Class<?>> interfaces = new ArrayList<>();
        for(Integer i : interfacess.getKeys()) {
            interfaces.add(interfacess.getObject(i));
        }
        HashMap<String, String> nhandler = new HashMap<>();
        for(String key : handler.getOwnPropertyNameStrings()){
            if(handler.get(key) instanceof V8ValueFunction){
                V8ValueFunction func = handler.get(key);
                nhandler.put(key, func.getSourceCode());
                JavetResourceUtils.safeClose(func);
            }
        }
        JavetResourceUtils.safeClose(handler, interfacess);
        DynamicType.Builder<?> builder = new ByteBuddy().subclass(superClass).implement(interfaces.toArray(new Class[0]));
        DynamicType.Unloaded unloaded = builder.method(ElementMatchers.any())
                .intercept(MethodDelegation.to(new Interceptor(nhandler)))
                .make();
        return unloaded.load(getClass().getClassLoader()).getLoaded();
    }
}