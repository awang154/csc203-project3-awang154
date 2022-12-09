import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PImage;

public final class ImageStore {
    public Map<String, List<PImage>> images;
    public List<PImage> defaultImages;

    public ImageStore(PImage defaultImage) {
        this.images = new HashMap<>();
        defaultImages = new LinkedList<>();
        defaultImages.add(defaultImage);
    }

    public static void nextImage(Entity entity) {
        entity.imageIndex = entity.imageIndex + 1;
    }

    public static PImage getCurrentImage(Object object) {
        if (object instanceof Background background) {
            return background.images.get(background.imageIndex);
        } else if (object instanceof Entity entity) {
            return entity.images.get(entity.imageIndex % entity.images.size());
        } else {
            throw new UnsupportedOperationException(String.format("getCurrentImage not supported for %s", object));
        }
    }

    public static List<PImage> getImageList(ImageStore imageStore, String key) {
        return imageStore.images.getOrDefault(key, imageStore.defaultImages);
    }

    public static void processImageLine(Map<String, List<PImage>> images, String line, PApplet screen) {
        String[] attrs = line.split("\\s");
        if (attrs.length >= 2) {
            String key = attrs[0];
            PImage img = screen.loadImage(attrs[1]);
            if (img != null && img.width != -1) {
                List<PImage> imgs = getImages(images, key);
                imgs.add(img);

                if (attrs.length >= Functions.KEYED_IMAGE_MIN) {
                    int r = Integer.parseInt(attrs[Functions.returnRedIDX()]);
                    int g = Integer.parseInt(attrs[Functions.returnGreenIDX()]);
                    int b = Integer.parseInt(attrs[Functions.returnBlueIDX()]);
                    Functions.setAlpha(img, screen.color(r, g, b), 0);
                }
            }
        }
    }

    public static List<PImage> getImages(Map<String, List<PImage>> images, String key) {
        return images.computeIfAbsent(key, k -> new LinkedList<>());
    }
}
