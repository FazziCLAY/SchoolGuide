package ru.fazziclay.schoolguide.app.global;

/**
 * <h1>Интерфейс для получения ключа локального кеша</h1>
 * Что-бы постоянно не скачивать файлы, у файла есть "глобальный ключ",
 * а на сервере есть отдельный файл с ключами, если ключ в локальном файле не совпадает, то скачиваем заного
 * @see GlobalManager
 * **/
public interface LocalCacheableData {
    /**
     * Выдать глобальный ключ данных
     * **/
    int getLocalCacheKay();
}
