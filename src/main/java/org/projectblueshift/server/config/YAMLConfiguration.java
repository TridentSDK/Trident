package org.projectblueshift.server.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class YamlConfiguration {

    private Yaml yaml;

    public YamlConfiguration(String string) {
        yaml = new Yaml();
        yaml.load(string);
    }

    public YamlConfiguration(InputStream stream) {
        yaml = new Yaml();
        yaml.load(stream);
    }

}
