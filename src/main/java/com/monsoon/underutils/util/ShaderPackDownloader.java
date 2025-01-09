package com.monsoon.underutils.util;

import com.monsoon.underutils.UnderUtils;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;

public class ShaderPackDownloader {
    public static void downloadShaderPack() {
        try {
            Path shaderpacksDir = FMLPaths.GAMEDIR.get().resolve("shaderpacks");
            Path targetFile = shaderpacksDir.resolve("UnderShader.zip");

            if (!Files.exists(shaderpacksDir)) {
                Files.createDirectories(shaderpacksDir);
            }

            if (!Files.exists(targetFile)) {
                URL downloadUrl = new URL("https://github.com/Leclowndu93150/UnderUtils/raw/refs/heads/master/UnderShader.zip");
                FileUtils.copyURLToFile(downloadUrl, targetFile.toFile());
                UnderUtils.LOGGER.info("Successfully downloaded shader pack");
            }
        } catch (IOException e) {
            UnderUtils.LOGGER.error("Failed to download shader pack", e);
        }
    }
}