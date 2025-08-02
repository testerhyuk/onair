package onair.article.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Category {
    POLITICS,
    ECONOMIC,
    ENTERTAINMENT,
    WORLD,
    SOCIETY;

    @JsonCreator
    public static Category from(String value) {
        return Category.valueOf(value.toUpperCase());
    }
}
