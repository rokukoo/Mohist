package org.safebabe.mohistpro;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class LicenseConfig {

    public static File license = new File("neptuneX/LICENSE.txt");

    public static String getLicense() {
        if (!license.exists()) return null;
        try {
            return Files.readString(license.toPath(), Charset.defaultCharset()).trim();
        } catch (IOException e) {
            System.out.println("Unable to read MohistPro license!");
            e.printStackTrace();
            return null;
        }
    }
}
