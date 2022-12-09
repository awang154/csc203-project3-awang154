import java.util.*;

import processing.core.PImage;
import processing.core.PApplet;

/**
 * This class contains many functions written in a procedural style.
 * You will reduce the size of this class over the next several weeks
 * by refactoring this codebase to follow an OOP style.
 */
public final class Functions {
    public static final Random rand = new Random();

    public static final int COLOR_MASK = 0xffffff;
    public static final int KEYED_IMAGE_MIN = 5;
    private static final int KEYED_RED_IDX = 2;
    private static final int KEYED_GREEN_IDX = 3;
    private static final int KEYED_BLUE_IDX = 4;

    public static final List<String> PATH_KEYS = new ArrayList<>(Arrays.asList("bridge", "dirt", "dirt_horiz", "dirt_vert_left", "dirt_vert_right", "dirt_bot_left_corner", "dirt_bot_right_up", "dirt_vert_left_bot"));

    public static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
    public static final int SAPLING_HEALTH_LIMIT = 5;

    public static final int PROPERTY_KEY = 0;
    public static final int PROPERTY_ID = 1;
    public static final int PROPERTY_COL = 2;
    public static final int PROPERTY_ROW = 3;
    public static final int ENTITY_NUM_PROPERTIES = 4;

    public static final String STUMP_KEY = "stump";
    public static final int STUMP_NUM_PROPERTIES = 0;

    public static final String SAPLING_KEY = "sapling";
    public static final int SAPLING_HEALTH = 0;
    public static final int SAPLING_NUM_PROPERTIES = 1;

    public static final String OBSTACLE_KEY = "obstacle";
    public static final int OBSTACLE_ANIMATION_PERIOD = 0;
    public static final int OBSTACLE_NUM_PROPERTIES = 1;

    public static final String DUDE_KEY = "dude";
    public static final int DUDE_ACTION_PERIOD = 0;
    public static final int DUDE_ANIMATION_PERIOD = 1;
    public static final int DUDE_LIMIT = 2;
    public static final int DUDE_NUM_PROPERTIES = 3;

    public static final String HOUSE_KEY = "house";
    public static final int HOUSE_NUM_PROPERTIES = 0;

    public static final String FAIRY_KEY = "fairy";
    public static final int FAIRY_ANIMATION_PERIOD = 0;
    public static final int FAIRY_ACTION_PERIOD = 1;
    public static final int FAIRY_NUM_PROPERTIES = 2;

    public static final String TREE_KEY = "tree";
    public static final int TREE_ANIMATION_PERIOD = 0;
    public static final int TREE_ACTION_PERIOD = 1;
    public static final int TREE_HEALTH = 2;
    public static final int TREE_NUM_PROPERTIES = 3;

    public static final double TREE_ANIMATION_MAX = 0.600;
    public static final double TREE_ANIMATION_MIN = 0.050;
    public static final double TREE_ACTION_MAX = 1.400;
    public static final double TREE_ACTION_MIN = 1.000;
    public static final int TREE_HEALTH_MAX = 3;
    public static final int TREE_HEALTH_MIN = 1;


    public static int returnRedIDX(){
        return KEYED_RED_IDX;
    }

    public static int returnGreenIDX(){
        return KEYED_GREEN_IDX;
    }

    public static int returnBlueIDX(){
        return KEYED_BLUE_IDX;
    }


    public static int getIntFromRange(int max, int min) {
        Random rand = new Random();
        return min + rand.nextInt(max-min);
    }

    public static double getNumFromRange(double max, double min) {
        Random rand = new Random();
        return min + rand.nextDouble() * (max - min);
    }




    public static void parseSapling(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == SAPLING_NUM_PROPERTIES) {
            int health = Integer.parseInt(properties[SAPLING_HEALTH]);
            Entity entity = Entity.createSapling(id, pt, imageStore.getImageList(imageStore, SAPLING_KEY), health);
            Entity.tryAddEntity(world, entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", SAPLING_KEY, SAPLING_NUM_PROPERTIES));
        }
    }

    public static void parseDude(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == DUDE_NUM_PROPERTIES) {
            Entity entity = Entity.createDudeNotFull(id, pt, Double.parseDouble(properties[DUDE_ACTION_PERIOD]), Double.parseDouble(properties[DUDE_ANIMATION_PERIOD]), Integer.parseInt(properties[DUDE_LIMIT]), imageStore.getImageList(imageStore, DUDE_KEY));
            Entity.tryAddEntity(world, entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", DUDE_KEY, DUDE_NUM_PROPERTIES));
        }
    }

    public static void parseFairy(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == FAIRY_NUM_PROPERTIES) {
            Entity entity = Entity.createFairy(id, pt, Double.parseDouble(properties[FAIRY_ACTION_PERIOD]), Double.parseDouble(properties[FAIRY_ANIMATION_PERIOD]), imageStore.getImageList(imageStore, FAIRY_KEY));
            Entity.tryAddEntity(world, entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", FAIRY_KEY, FAIRY_NUM_PROPERTIES));
        }
    }

    public static void parseTree(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == TREE_NUM_PROPERTIES) {
            Entity entity = Entity.createTree(id, pt, Double.parseDouble(properties[TREE_ACTION_PERIOD]), Double.parseDouble(properties[TREE_ANIMATION_PERIOD]), Integer.parseInt(properties[TREE_HEALTH]), imageStore.getImageList(imageStore, TREE_KEY));
            Entity.tryAddEntity(world, entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", TREE_KEY, TREE_NUM_PROPERTIES));
        }
    }

    public static void parseObstacle(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Entity entity = Entity.createObstacle(id, pt, Double.parseDouble(properties[OBSTACLE_ANIMATION_PERIOD]), imageStore.getImageList(imageStore, OBSTACLE_KEY));
            Entity.tryAddEntity(world, entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", OBSTACLE_KEY, OBSTACLE_NUM_PROPERTIES));
        }
    }

    public static void parseHouse(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == HOUSE_NUM_PROPERTIES) {
            Entity entity = Entity.createHouse(id, pt, imageStore.getImageList(imageStore, HOUSE_KEY));
            Entity.tryAddEntity(world, entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", HOUSE_KEY, HOUSE_NUM_PROPERTIES));
        }
    }
    public static void parseStump(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == STUMP_NUM_PROPERTIES) {
            Entity entity = Entity.createStump(id, pt, imageStore.getImageList(imageStore, STUMP_KEY));
            Entity.tryAddEntity(world, entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", STUMP_KEY, STUMP_NUM_PROPERTIES));
        }
    }




    public static void load(WorldModel world, Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        parseSaveFile(world, saveFile, imageStore, defaultBackground);
        if(world.background == null){
            world.background = new Background[world.numRows][world.numCols];
            for (Background[] row : world.background)
                Arrays.fill(row, defaultBackground);
        }
        if(world.occupancy == null){
            world.occupancy = new Entity[world.numRows][world.numCols];
            world.entities = new HashSet<>();
        }
    }
    public static void parseSaveFile(WorldModel world, Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        String lastHeader = "";
        int headerLine = 0;
        int lineCounter = 0;
        while(saveFile.hasNextLine()){
            lineCounter++;
            String line = saveFile.nextLine().strip();
            if(line.endsWith(":")){
                headerLine = lineCounter;
                lastHeader = line;
                switch (line){
                    case "Backgrounds:" -> world.background = new Background[world.numRows][world.numCols];
                    case "Entities:" -> {
                        world.occupancy = new Entity[world.numRows][world.numCols];
                        world.entities = new HashSet<>();
                    }
                }
            }else{
                switch (lastHeader){
                    case "Rows:" -> world.numRows = Integer.parseInt(line);
                    case "Cols:" -> world.numCols = Integer.parseInt(line);
                    case "Backgrounds:" -> Background.parseBackgroundRow(world, line, lineCounter-headerLine-1, imageStore);
                    case "Entities:" -> Entity.parseEntity(world, line, imageStore);
                }
            }
        }
    }




    /*
      Called with color for which alpha should be set and alpha value.
      setAlpha(img, color(255, 255, 255), 0));
    */
    public static void setAlpha(PImage img, int maskColor, int alpha) {
        int alphaValue = alpha << 24;
        int nonAlpha = maskColor & COLOR_MASK;
        img.format = PApplet.ARGB;
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            if ((img.pixels[i] & COLOR_MASK) == nonAlpha) {
                img.pixels[i] = alphaValue | nonAlpha;
            }
        }
        img.updatePixels();
    }



    public static boolean contains(Viewport viewport, Point p) {
        return p.y >= viewport.row && p.y < viewport.row + viewport.numRows && p.x >= viewport.col && p.x < viewport.col + viewport.numCols;
    }



    public static void loadImages(Scanner in, ImageStore imageStore, PApplet screen) {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                ImageStore.processImageLine(imageStore.images, in.nextLine(), screen);
            } catch (NumberFormatException e) {
                System.out.printf("Image format error on line %d\n", lineNumber);
            }
            lineNumber++;
        }
    }
}
