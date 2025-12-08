package utils;

import jakarta.persistence.Id;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntityMergeUtil {

    private static final Logger LOGGER = Logger.getLogger(EntityMergeUtil.class.getName());

    // All known primary keys across your entities
    private static final Set<String> PRIMARY_KEYS = new HashSet<>(Arrays.asList(
            "aId", "vId", "cId", "eId", "rId", "linkId", "imgId", "serialVersionUID"
    ));

    public static <T> void mergeNonNullFields(T existing, T updated) {
        if (existing == null || updated == null) {
            throw new IllegalArgumentException("Existing and updated entities cannot be null.");
        }

        Class<?> clazz = existing.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            try {
                // Skip fields annotated with @Id
                if (field.isAnnotationPresent(Id.class)) {
                    LOGGER.info("Skipping @Id field: " + field.getName());
                    continue;
                }

                // Skip known primary keys
                if (PRIMARY_KEYS.contains(field.getName())) {
                    LOGGER.info("Skipping primary key: " + field.getName());
                    continue;
                }

                // Skip collection fields (eventsCollection, artistSocialLinksCollection, etc.)
                if (field.getName().toLowerCase().contains("collection")) {
                    LOGGER.info("Skipping collection field: " + field.getName());
                    continue;
                }

                Object value = field.get(updated);

                // Skip nulls
                if (value == null) {
                    continue;
                }

                // Skip empty strings
                if (value instanceof String && ((String) value).trim().isEmpty()) {
                    continue;
                }

                // Skip default primitive values
                Class<?> type = field.getType();
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

                // Merge the value
                LOGGER.info("Merging field: " + field.getName() + " = " + value);
                field.set(existing, value);

            } catch (IllegalAccessException e) {
                LOGGER.log(Level.WARNING,
                        "Unable to access field: " + field.getName() + " for class " + clazz.getSimpleName(), e);
            }
        }
    }
}
