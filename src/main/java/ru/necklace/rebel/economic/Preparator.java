package ru.necklace.rebel.economic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Preparator {
    public static void main(String[] args) {
        Properties defaultProps = new Properties();
        defaultProps.put("key", "value");
        Object var2 = null;

        try {
            FileOutputStream out = new FileOutputStream("conf" + File.separator + "appProperties.properties");
            defaultProps.store(out, "---No Comment---");
            out.close();
        } catch (FileNotFoundException var4) {
            var4.printStackTrace();
        } catch (IOException var5) {
            var5.printStackTrace();
        }
    }
}
