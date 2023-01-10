package me.losin6450.core;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * The type Config.
 */
public class Config {
    /**
     * The enum Config type.
     */
    public enum ConfigType {
        /**
         * Json config type.
         */
        JSON,
        /**
         * Yaml config type.
         */
        YAML
    }

    private final ConfigType type;
    private final String root;
    private final String name;
    private final boolean member;
    private JsonObject object;
    private HashMap map;

    /**
     * Instantiates a new Config.
     *
     * @param type   the type
     * @param root   the root
     * @param name   the name
     * @param member the member
     * @throws IOException the io exception
     */
    public Config(ConfigType type, String root, String name, boolean member) throws IOException {
        this.type = type;
        this.root = root;
        this.name = name;
        this.member = member;
        load();
    }


    /**
     * Get t.
     *
     * @param <T> the type parameter
     * @param key the key
     * @return the t
     */
    public <T> T get(String key){
        if(type == ConfigType.JSON){
            return (T) object.get(key);
        } else if (type == ConfigType.YAML){
            return (T) map.get(key);
        }
        return null;
    }

    /**
     * Get main string.
     *
     * @return the string
     */
    public String getMain(){
        if(type == ConfigType.JSON){
            if(member){
                return object.get("grakkit").asObject().getString("main", ScriptManager.defaultMain);
            } else {
                return object.getString("main", ScriptManager.defaultMain);
            }
        } else if (type == ConfigType.YAML){
            if(member){
                return (String) ((HashMap) map.get("grakkit")).get("main");
            } else {
                return (String) map.get("main");
            }
        }
        return null;
    }

    /**
     * Load.
     *
     * @throws IOException the io exception
     */
    public void load() throws IOException {
        if(type == ConfigType.JSON){
            this.object = Json.parse(Files.newBufferedReader(Paths.get(new File(root, name).toURI()))).asObject();
        } else if (type == ConfigType.YAML){
            this.map = new Yaml().load(Files.newBufferedReader(Paths.get(new File(root, name).toURI())));
        }
    }
}
