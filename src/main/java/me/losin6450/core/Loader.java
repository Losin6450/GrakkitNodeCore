package me.losin6450.core;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * The type Loader.
 */
public class Loader extends URLClassLoader {
    /**
     * Instantiates a new Loader.
     *
     * @param parent the parent
     */
    public Loader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    @Override
    public void addURL(URL location) {
        super.addURL(location);
    }
}