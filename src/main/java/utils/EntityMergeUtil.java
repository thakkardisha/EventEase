package utils;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntityMergeUtil {

    private static final Logger LOGGER = Logger.getLogger(EntityMergeUtil.class.getName());

    public static <T> void mergeNonNullFields(T existing, T updated) {
        if (existing == null || updated == null) {
            throw new IllegalArgumentException("Existing and updated entities cannot be null.");
        }

        Class<?> clazz = existing.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(updated);
                Class<?> type = field.getType();

                // CRITICAL FIX: Skip the primary key and serialVersionUID fields.
                // Merging the primary key (aId) causes the entity to become detached or mismanaged.
                if (field.getName().equals("aId") || field.getName().equals("serialVersionUID")) continue;


                // Skip nulls
                if (value == null) continue;

                // Skip empty strings
                if (value instanceof String && ((String) value).trim().isEmpty()) continue;

                // Skip default values for primitives
                if (type.isPrimitive()) {
                    if ((type.equals(int.class) && ((int) value) == 0)
                                || (type.equals(long.class) && ((long) value) == 0L)
                                || (type.equals(double.class) && ((double) value) == 0.0)
                                || (type.equals(float.class) && ((float) value) == 0.0f)
                                || (type.equals(boolean.class) && !((boolean) value))
                                || (type.equals(short.class) && ((short) value) == 0)
                                || (type.equals(byte.class) && ((byte) value) == 0)) {
                            continue;
                    }
                }

                // Merge only meaningful values
                field.set(existing, value);

            } catch (IllegalAccessException e) {
                LOGGER.log(Level.WARNING,
                        "Unable to access field: " + field.getName() + " for class " + clazz.getSimpleName(), e);
            }
        }
    }
}