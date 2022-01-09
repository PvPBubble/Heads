package io.fazal.heads.category;

import io.fazal.heads.Main;

import java.util.ArrayList;
import java.util.List;

public class CategoryManager {

    private static CategoryManager instance;
    private List<Category> categories;

    public CategoryManager() {
        categories = new ArrayList<>();
        for (String key : Main.getInstance().getConfig().getConfigurationSection("Categories").getKeys(false)) {
            categories.add(new Category(key));
        }
    }

    public static CategoryManager getInstance() {
        if (instance == null) {
            synchronized (CategoryManager.class) {
                if (instance == null) {
                    instance = new CategoryManager();
                }
            }
        }
        return instance;
    }

    public List<Category> getCategories() {
        return categories;
    }
}