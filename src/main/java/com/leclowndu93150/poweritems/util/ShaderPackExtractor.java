package com.leclowndu93150.poweritems.util;

import com.leclowndu93150.poweritems.PowerItems;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;

public class ShaderPackExtractor {
    public static void extractShaderPack(String modId, String shaderFolderName) {
        try {
            Path gameDir = FMLPaths.GAMEDIR.get();
            Path shaderpacksDir = gameDir.resolve("shaderpacks");
            Path targetDir = shaderpacksDir.resolve(shaderFolderName);

            Path sourcePath;
            if (FMLEnvironment.production) {
                URL sourceUrl = ShaderPackExtractor.class.getClassLoader().getResource("assets/" + modId + "/" + shaderFolderName);
                URI sourceUri = sourceUrl.toURI();
                if (sourceUri.getScheme().equals("jar")) {
                    FileSystem fileSystem = FileSystems.newFileSystem(sourceUri, Collections.emptyMap());
                    sourcePath = fileSystem.getPath("/assets/" + modId + "/" + shaderFolderName);
                } else {
                    sourcePath = Paths.get(sourceUri);
                }
            } else {
                sourcePath = FMLLoader.getGamePath().resolve("src/main/resources/assets/" + modId + "/" + shaderFolderName);
            }

            if (Files.exists(targetDir)) {
                FileUtils.deleteDirectory(targetDir.toFile());
            }
            Files.createDirectories(targetDir);

            Files.walk(sourcePath)
                    .forEach(source -> {
                        try {
                            Path relative = sourcePath.relativize(source);
                            Path dest = targetDir.resolve(relative.toString());

                            if (Files.isDirectory(source)) {
                                Files.createDirectories(dest);
                            } else {
                                Files.createDirectories(dest.getParent());
                                Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
                            }
                        } catch (IOException e) {
                            PowerItems.LOGGER.error("Failed to copy file: " + source, e);
                        }
                    });

        } catch (Exception e) {
            PowerItems.LOGGER.error("Failed to extract shader pack", e);
        }
    }
}