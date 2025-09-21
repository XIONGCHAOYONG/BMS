package com.x.common.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LendStatus {

    UNRETURNED((byte)0),//未还

    RETURNED((byte)1),//已还

    OVERDUE((byte)2), //逾期

    SOON_OVERDUE((byte)3), //即将逾期

    RE_LEND((byte)4);   //续借中
    private final byte code; // 存数据库的值

    LendStatus(byte code) {
        this.code = code;
    }

    @JsonValue
    public byte getCode() {
        return code;
    }
    public static LendStatus fromCode(byte code) {
        for (LendStatus s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        throw new IllegalArgumentException("未知状态: " + code);
    }
}
