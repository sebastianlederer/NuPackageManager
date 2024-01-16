package de.dassit.nupama;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class Configuration {
        private final static Configuration instance = new Configuration();
        private final static String configDir = "/tmp/packagemanager";
        private final static String configFile = "config.properties";

        private Properties props = null;

        public static Configuration getInstance() {
                return instance;
        }

        public Configuration() {
                readConfig();
        }

        private void readConfig() {
                File f = new File(configDir, configFile);
                FileReader r = null;
                Logger logger = Logger.getGlobal();

                try {
                        r = new FileReader(f);
                        props = new Properties();
                        props.load(r);
                } catch (FileNotFoundException e) {
                        logger.warning(e.getMessage());
                } catch (IOException e) {
                        logger.warning(e.getMessage());
                } finally {
                        if (r != null) {
                                try {
                                        r.close();
                                } catch (IOException e) {
                                        logger.warning(e.getMessage());
                                }
                        }
                }
        }
        public String getConfigDir() {
            return configDir;
    }

    public String get(String name) {
            return props.getProperty(name);
    }

    public List<String> getMultiValue(String name) {
            ArrayList<String> result = new ArrayList<String>();

            String value = props.getProperty(name);
            if (value != null) {
                    String[] parts = value.split(",");

                    for (String s : parts) {
                            result.add(s.trim());
                    }
            }
            return result;
    }

}

