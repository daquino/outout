package outout.util;

import java.lang.reflect.Field;

public class Nuke {
    public static void nukeFields(Object object)  throws Exception {
        for(Field field : object.getClass().getDeclaredFields()) {
            Class<?> clazz = field.getType();
            if(!clazz.isPrimitive()) {
                field.setAccessible(true);
                field.set(object, null);
            }
        }
    }
}
