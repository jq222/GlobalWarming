package net.porillo.engine.api;

import lombok.Getter;
import net.porillo.GlobalWarming;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public abstract class Model {

    @Getter private final UUID worldId;
    @Getter private final String modelName;
    private Path modelsPath;

    public Model(UUID worldId, String modelName) {
        this.modelName = modelName;
        this.worldId = worldId;

        if (GlobalWarming.getInstance() != null) {
            this.modelsPath = GlobalWarming.getInstance().getDataFolder().toPath().resolve("models");
        } else {
            try {
                this.modelsPath = Paths.get(getClass().getResource("/models").toURI());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public Path getPath() {
        World world = Bukkit.getWorld(worldId);
        return this.modelsPath.resolve(world.getName()).resolve(modelName);
    }

    public String getContents() {
        createIfNotExists();

        try {
            return new String(Files.readAllBytes(getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void writeContents(String data) {
        clearFileForNewWrite();

        try {
            Files.write(getPath(), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createIfNotExists() {
        Path file = getPath();
        if (!Files.exists(file)) {
            GlobalWarming.getInstance().getLogger().info(String.format("Model: [%s] does not exist, creating.", modelName));

            try {
                // Copy resource from JAR to the correct path
                World world = Bukkit.getWorld(worldId);
                Files.createDirectories(modelsPath.resolve(world.getName()));
                Files.copy(GlobalWarming.getInstance().getResource(String.format("models/%s", modelName)), getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearFileForNewWrite() {
        Path file = getPath();
        try {
            if (Files.exists(file)) {
                Files.delete(file);
                Files.createFile(file);
            } else {
                Files.createFile(file);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public abstract void loadModel();
}
