package org.safebabe.mohistpro;

import net.minecraftforge.server.ServerMain;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class LicenseConfig {

    public static File license = new File("neptuneX/LICENSE.bin");

    public static String licenseCode = null;

    public static String getLicense() {
        if (licenseCode != null) return licenseCode;
        if (!license.exists()) return "";
        try {
            return Files.readString(license.toPath(), Charset.defaultCharset()).trim();
        } catch (IOException e) {
            System.out.println("Unable to read MohistPro license!");
            e.printStackTrace();
            return "";
        }
    }
}
