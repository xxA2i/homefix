package com.example.homefix;

public class Master {
    private int id;
    private String name;
    private String specialty;
    private float rating;
    private float minPrice;
    private String description;

    // Конструктор без id (для создания новых мастеров)
    public Master(String name, String specialty, float rating, float minPrice, String description) {
        this.name = name;
        this.specialty = specialty;
        this.rating = rating;
        this.minPrice = minPrice;
        this.description = description;
    }

    // Конструктор с id (для загрузки из БД)
    public Master(int id, String name, String specialty, float rating, float minPrice, String description) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.rating = rating;
        this.minPrice = minPrice;
        this.description = description;
    }

    private int reviewCount;

    public Master(int id, String name, String specialty, float rating, float minPrice, String description, int reviewCount) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.rating = rating;
        this.minPrice = minPrice;
        this.description = description;
        this.reviewCount = reviewCount;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public float getRating() {
        return rating;
    }

    public float getMinPrice() {
        return minPrice;
    }

    public String getDescription() {
        return description;
    }
}