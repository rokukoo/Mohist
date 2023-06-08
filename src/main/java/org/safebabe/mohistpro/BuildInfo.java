package org.safebabe.mohistpro;

import cpw.mods.modlauncher.TransformingClassLoader;

import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class BuildInfo {

    public static String getVersion() {
        try {
            TransformingClassLoader cl = (TransformingClassLoader) BuildInfo.class.getClassLoader();
            URL url = cl.getResource("META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(url.openStream());
            Attributes attr = manifest.getMainAttributes();

            return attr.getValue("Project-Version");
        } catch (IOException e) {
            return "null";
        }
    }

    public static String getCommitId() {
        try {
            TransformingClassLoader cl = (TransformingClassLoader) BuildInfo.class.getClassLoader();
            URL url = cl.getResource("META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(url.openStream());
            Attributes attr = manifest.getMainAttributes();

            return attr.getValue("Commit-Hash");
        } catch (IOException e) {
            return "null";
        }
    }

}
