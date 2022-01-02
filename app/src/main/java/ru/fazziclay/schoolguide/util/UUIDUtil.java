package ru.fazziclay.schoolguide.util;

import java.util.UUID;

public class UUIDUtil {
    /**
     * @return Случайный UUID который не будет содержатся в исключениях
     * @param exclusions исключения которые нельзя генерировать
     * @see java.util.UUID
     * **/
    public static UUID generateUUID(UUID[] exclusions) {
        UUID uuid = UUID.randomUUID();
        while (true) {
            boolean contains = false;
            for (UUID exclusion : exclusions) {
                if (uuid.equals(exclusion)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                uuid = UUID.randomUUID();
            } else {
                break;
            }
        }
        return uuid;
    }
}
