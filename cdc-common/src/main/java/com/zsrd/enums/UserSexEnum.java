package com.zsrd.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum UserSexEnum {

    MAN(0, "男"), WOMAN(1, "女");

    UserSexEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    //标记数据库存的值是code
    @EnumValue
    @JsonValue
    private final int code;
    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
