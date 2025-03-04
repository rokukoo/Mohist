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

package com.mohistmc.bukkit.nms.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mohistmc.bukkit.nms.model.ClassMapping;
import com.mohistmc.bukkit.nms.remappers.ClassRemapperSupplier;
import com.mohistmc.bukkit.nms.remappers.MohistJarMapping;
import com.mohistmc.bukkit.nms.remappers.MohistJarRemapper;
import com.mohistmc.bukkit.nms.remappers.MohistSuperClassRemapper;
import com.mohistmc.bukkit.nms.remappers.ReflectMethodRemapper;
import com.mohistmc.bukkit.nms.remappers.ReflectRemapper;
import net.md_5.specialsource.InheritanceMap;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.transformer.MavenShade;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pyz
 * @date 2019/6/30 11:50 PM
 */
public class RemapUtils {

    public static MohistJarMapping jarMapping;
    public static MohistJarRemapper jarRemapper;
    private static final List<Remapper> remappers = new ArrayList<>();
    public static Map<String, String> relocations = new HashMap<>();

    public static void init() {
        jarMapping = new MohistJarMapping();
        jarMapping.packages.put("org/bukkit/craftbukkit/libs/it/unimi/dsi/fastutil/", "it/unimi/dsi/fastutil/");
        jarMapping.packages.put("org/bukkit/craftbukkit/libs/jline/", "jline/");
        jarMapping.packages.put("org/bukkit/craftbukkit/libs/org/apache/commons/", "org/apache/commons/");
        jarMapping.packages.put("org/bukkit/craftbukkit/libs/org/objectweb/asm/", "org/objectweb/asm/");
        jarMapping.setInheritanceMap(getGlobalInheritanceMap());
        try {
            jarMapping.loadMappings(
                    new BufferedReader(new InputStreamReader(RemapUtils.class.getClassLoader().getResourceAsStream("mappings/spigot2srg.srg"))),
                    new MavenShade(relocations),
                    null, false);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        jarRemapper = new MohistJarRemapper(jarMapping);
        remappers.add(jarRemapper);
        remappers.add(new ReflectRemapper());
        jarMapping.initFastMethodMapping(jarRemapper);
        ReflectMethodRemapper.init();

        try {
            Class.forName("com.mohistmc.bukkit.nms.proxy.ProxyMethodHandlesLookup");
        } catch (ClassNotFoundException e) {
            e.fillInStackTrace();
        }
    }


    public static byte[] remapFindClass(byte[] bs) {
        ClassReader reader = new ClassReader(bs); // Turn from bytes into a visitor
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, ClassReader.EXPAND_FRAMES);
        for (Remapper remapper : remappers) {

            ClassNode container = new ClassNode();
            ClassRemapper classRemapper;
            if (remapper instanceof ClassRemapperSupplier) {
                classRemapper = ((ClassRemapperSupplier) remapper).getClassRemapper(container);
            } else {
                classRemapper = new ClassRemapper(container, remapper);
            }
            classNode.accept(classRemapper);
            classNode = container;
        }
        MohistSuperClassRemapper.init(classNode);
        ClassWriter writer = new ClassWriter(0);
        classNode.accept(writer);
        return writer.toByteArray();

    }

    public static String map(String typeName) {
        typeName = mapPackage(typeName);
        return jarMapping.classes.getOrDefault(typeName, typeName);
    }

    public static String reverseMap(String typeName) {
        ClassMapping mapping = jarMapping.byNMSInternalName.get(typeName);
        return mapping == null ? typeName : mapping.getNmsSrcName();
    }

    public static String reverseMap(Class<?> clazz) {
        ClassMapping mapping = jarMapping.byMCPName.get(clazz.getName());
        return mapping == null ? ASMUtils.toInternalName(clazz) : mapping.getNmsSrcName();
    }

    public static String mapPackage(String typeName) {
        for (Map.Entry<String, String> entry : jarMapping.packages.entrySet()) {
            String prefix = entry.getKey();
            if (typeName.startsWith(prefix)) {
                return entry.getValue() + typeName.substring(prefix.length());
            }
        }
        return typeName;
    }

    public static String remapMethodDesc(String methodDescriptor) {
        Type rt = Type.getReturnType(methodDescriptor);
        Type[] ts = Type.getArgumentTypes(methodDescriptor);
        rt = Type.getType(ASMUtils.toDescriptorV2(map(ASMUtils.getInternalName(rt))));
        for (int i = 0; i < ts.length; i++) {
            ts[i] = Type.getType(ASMUtils.toDescriptorV2(map(ASMUtils.getInternalName(ts[i]))));
        }
        return Type.getMethodType(rt, ts).getDescriptor();
    }

    public static String mapMethodName(Class<?> clazz, String name, MethodType methodType) {
        return mapMethodName(clazz, name, methodType.parameterArray());
    }

    public static String mapMethodName(Class<?> type, String name, Class<?>... parameterTypes) {
        return jarMapping.fastMapMethodName(type, name, parameterTypes);
    }

    public static String inverseMapMethodName(Class<?> type, String name, Class<?>... parameterTypes) {
        return jarMapping.fastReverseMapMethodName(type, name, parameterTypes);
    }

    public static String mapFieldName(Class<?> type, String fieldName) {
        String key = reverseMap(type) + "/" + fieldName;
        String mapped = jarMapping.fields.get(key);
        if (mapped == null) {
            Class<?> superClass = type.getSuperclass();
            if (superClass != null) {
                mapped = mapFieldName(superClass, fieldName);
            }
        }
        return mapped != null ? mapped : fieldName;
    }

    public static String inverseMapFieldName(Class<?> type, String fieldName) {
        return jarMapping.fastReverseMapFieldName(type, fieldName);
    }

    public static String inverseMapName(Class<?> clazz) {
        ClassMapping mapping = jarMapping.byMCPName.get(clazz.getName());
        return mapping == null ? clazz.getName() : mapping.getNmsName();
    }

    public static String inverseMapSimpleName(Class<?> clazz) {
        ClassMapping mapping = jarMapping.byMCPName.get(clazz.getName());
        return mapping == null ? clazz.getSimpleName() : mapping.getNmsSimpleName();
    }

    public static boolean needRemap(String className){
        return className.startsWith("net.minecraft.");
    }

    // Cauldron start
    private static InheritanceMap globalInheritanceMap = null;

    /**
     * Get the inheritance map for remapping all plugins
     */
    public static InheritanceMap getGlobalInheritanceMap() {
        if (globalInheritanceMap == null) {
            Map<String, String> relocationsCurrent = new HashMap<>();
            JarMapping currentMappings = new JarMapping();

            try {
                currentMappings.loadMappings(
                        new BufferedReader(new InputStreamReader(RemapUtils.class.getClassLoader().getResourceAsStream("mappings/spigot2srg.srg"))),
                        new MavenShade(relocationsCurrent),
                        null, false);
            } catch (IOException ex) {
                ex.fillInStackTrace();
                throw new RuntimeException(ex);
            }

            BiMap<String, String> inverseClassMap = HashBiMap.create(currentMappings.classes).inverse();
            globalInheritanceMap = new InheritanceMap();

            BufferedReader reader = new BufferedReader(new InputStreamReader(RemapUtils.class.getClassLoader().getResourceAsStream("mappings/inheritanceMap.txt")));

            try {
                globalInheritanceMap.load(reader, inverseClassMap);
            } catch (IOException ignored) {
            }
            System.out.println("Loaded inheritance map of " + globalInheritanceMap.size() + " classes");
        }

        return globalInheritanceMap;
    }
    // Cauldron end
}
