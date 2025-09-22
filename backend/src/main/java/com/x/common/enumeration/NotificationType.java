package com.x.common.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationType {
    SOONOVER_DUE((byte) 1),
    OVERDUE((byte) 2);

    private final byte code;

    NotificationType(byte code) {
        this.code = code;
    }

    @JsonValue
    public byte getCode() {
        return code;
    }

    public static NotificationType fromCode(byte code) {
        for (NotificationType s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        throw new IllegalArgumentException("未知状态: " + code);
    }
}
