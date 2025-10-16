package com.x.utils;

import redis.clients.jedis.Jedis;

public class RedisUtil {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 6379;

    public static void set(String key, String value, int expireSeconds) {
        try (Jedis jedis = new Jedis(HOST, PORT)) {
            jedis.setex(key, expireSeconds, value);
        }
    }

    public static String get(String key) {
        try (Jedis jedis = new Jedis(HOST, PORT)) {
            return jedis.get(key);
        }
    }

    public static void delete(String key) {
        try (Jedis jedis = new Jedis(HOST, PORT)) {
            jedis.del(key);
        }
    }
}
