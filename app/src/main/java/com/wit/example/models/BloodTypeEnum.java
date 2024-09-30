package com.wit.example.models;

public enum BloodTypeEnum {

    ZERO(0, "0+"),
    ONE(1, "0-"),
    TWO(2, "A+"),
    THREE(3, "A-"),
    FOUR(4, "B+"),
    FIVE(5, "B-"),
    SIX(6, "AB+"),
    SEVEN(7, "AB-");

    private int index;
    private String text;

    BloodTypeEnum(int index, String text) {
        this.index = index;
        this.text = text;
    }

    public static String getText(int index) {
        for (BloodTypeEnum item : BloodTypeEnum.values()) {
            if (item.index == index) {
                return item.text;
            }
        }
        throw new IllegalArgumentException("Nincs ilyen számhoz tartozó érték: " + index);
    }

}
