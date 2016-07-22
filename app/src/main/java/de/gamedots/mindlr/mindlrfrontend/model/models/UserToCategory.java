package de.gamedots.mindlr.mindlrfrontend.model.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Dirk on 25.04.16.
 */
@Table(name = "user_categories")
public class UserToCategory extends Model{

    @Column(name = "user", notNull = true)
    User user;

    @Column(name = "category", notNull = true)
    Category category;

    public UserToCategory() {
    }

    public UserToCategory(User user, Category category) {
        this.user = user;
        this.category = category;
    }

    public List<Category> getCategories(User user){
        return new Select()
                .from(Category.class)
                .innerJoin(UserToCategory.class)
                .on("categories.id = user_categories.id")
                .where("user_categories.user = ?", user.getId())
                .execute();
    }
}
