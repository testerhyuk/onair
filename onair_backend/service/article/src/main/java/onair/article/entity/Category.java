package onair.article.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Category {
    ELECTRONICS,
    CLOTHING,
    BEAUTY,
    HOME,
    ETC;

    @JsonCreator
    public static Category from(String value) {
        return Category.valueOf(value.toUpperCase());
    }
}
