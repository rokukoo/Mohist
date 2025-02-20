/*
 * Mohist - MohistMC
 * Copyright (C) 2018-2023.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.mohistmc.libraries;

import com.mohistmc.MohistMCStart;
import com.mohistmc.action.v_1_19_R2;
import com.mohistmc.config.MohistConfigUtil;
import com.mohistmc.network.download.DownloadSource;
import com.mohistmc.network.download.UpdateUtils;
import com.mohistmc.util.JarLoader;
import com.mohistmc.util.JarTool;
import com.mohistmc.util.MD5Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultLibraries {
    public static final HashMap<String, String> fail = new HashMap<>();
    public static final AtomicLong allSize = new AtomicLong(); // global
    public static final String MAVENURL;

    static {
        try {
            MAVENURL = DownloadSource.get().getUrl();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String libUrl(File lib) {
        return MAVENURL + "libraries/" + lib.getAbsolutePath().replaceAll("\\\\", "/").split("/libraries/")[1];
    }

    public static void run() throws Exception {
        System.out.println(MohistMCStart.i18n.get("libraries.checking.start"));
        LinkedHashMap<File, String> libs = getDefaultLibs();
        AtomicLong currentSize = new AtomicLong();
        Set<File> defaultLibs = new LinkedHashSet<>();
        for (File lib : libs.keySet()) {
            v_1_19_R2.loadedLibsPaths.add(lib.getAbsolutePath());
            if (lib.exists() && MohistConfigUtil.yml.getStringList("libraries_black_list").contains(lib.getName())) {
                continue;
            }
            if (lib.exists() && MD5Util.getMd5(lib).equals(libs.get(lib))) {
                currentSize.addAndGet(lib.length());
                continue;
            }
            defaultLibs.add(lib);
        }
        for (File lib : defaultLibs) {
            lib.getParentFile().mkdirs();

            String u = libUrl(lib);
            System.out.println(MohistMCStart.i18n.get("libraries.global.percentage", Math.round((float) (currentSize.get() * 100) / allSize.get()) + "%")); //Global percentage
            try {
                UpdateUtils.downloadFile(u, lib, libs.get(lib));
                JarLoader.loadJar(lib.toPath());
                currentSize.addAndGet(lib.length());
                fail.remove(u.replace(MAVENURL, ""));
            } catch (Exception e) {
                if (e.getMessage() != null && !"md5".equals(e.getMessage())) {
                    System.out.println(MohistMCStart.i18n.get("file.download.nook", u));
                    lib.delete();
                }
                fail.put(u.replace(MAVENURL, ""), lib.getAbsolutePath());
            }
        }
        /*FINISHED | RECHECK IF A FILE FAILED*/
        if (!fail.isEmpty()) {
            run();
        } else {
            System.out.println(MohistMCStart.i18n.get("libraries.check.end"));
        }
    }

    public static LinkedHashMap<File, String> getDefaultLibs() throws Exception {
        LinkedHashMap<File, String> temp = new LinkedHashMap<>();
        BufferedReader b = new BufferedReader(new InputStreamReader(Objects.requireNonNull(DefaultLibraries.class.getClassLoader().getResourceAsStream("libraries.txt"))));
        String str;
        while ((str = b.readLine()) != null) {
            String[] s = str.split("\\|");
            temp.put(new File(JarTool.getJarDir() + "/" + s[0]), s[1]);
            allSize.addAndGet(Long.parseLong(s[2]));
        }
        b.close();
        return temp;
    }
}
