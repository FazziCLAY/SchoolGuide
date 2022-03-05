package ru.fazziclay.schoolguide.util;

import java.util.UUID;

/**
 * Набор утилит для UUIDшников
 * **/
public class UUIDUtil {
    private static final int MAX_REGENERATE_ITERATIONS = 1000;

    /**
     * @return Случайный UUID который не будет содержатся в исключениях
     * @param exclusions исключения которые нельзя генерировать
     * @see java.util.UUID
     * **/
    public static UUID generateUUID(UUID[] exclusions) {
        UUID uuid = UUID.randomUUID();
        int i = 0;
        while (true) {
            boolean contains = false;
            for (UUID exclusion : exclusions) {
                if (uuid.equals(exclusion)) {
                    contains = true;
                    break;
                }
            }
            if (contains) {
                uuid = UUID.randomUUID();
            } else {
                break;
            }
            i++;
            if (i >= MAX_REGENERATE_ITERATIONS) {
                throw new RuntimeException("Error while regenerating uuid. Iterations count >= " + i);
            }
        }
        return uuid;
    }

    /**
     * Случайный UUID
     * @see UUID
     * **/
    public static UUID generateUUID() {
        return UUID.randomUUID();
    }
}
