abstract class thing {
    String objName = "";
    thing (String name){
        this.objName = name;
    }
}

public class Sprite extends thing{
    Sprite (String name){
        super(name);
    }
}
