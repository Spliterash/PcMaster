package ru.spliterash.pcmasterclient;

import javafx.scene.image.Image;
import lombok.SneakyThrows;
import lombok.Value;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.WeakHashMap;

public enum ImageManager {
    BROKEN("broken.png"),
    LOAD("loading.png"),
    PLUS("plus.png"),
    CLOSE("close.png");

    private final String path;
    private BufferedImage original;
    private Map<Size, Image> cache = new WeakHashMap<>();

    @SneakyThrows
    ImageManager(String s) {
        this.path = "images/" + s;
        URL resource = Main.getMain().getClass().getClassLoader().getResource(path);
        if (resource == null)
            throw new RuntimeException("Resource is null");
        InputStream stream = resource.openStream();
        original = ImageIO.read(stream);
    }

    public Image getResizedFx(int x, int y) {
        Size size = new Size(x, y);
        Image value = cache.get(size);
        if (value == null) {
            value = Utils.getImage(original, x, y);
            cache.put(size, value);
        }
        return value;
    }

    @Value
    private static class Size {
        int x, y;
    }
}
