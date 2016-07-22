package de.gamedots.mindlr.mindlrfrontend.model.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Dirk on 24.04.16.
 */
@Table(name = "categories")
public class Category extends Model {

    @Column(name = "remote_id", unique = true, notNull = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long server_id;

    @Column(name = "name", length = 100, unique = true)
    String name;

    @Column(name = "display_name", length = 100, unique = true)
    String display_name;

    public Category() {
    }

    public Category(String name, String display_name) {
        this.name = name;
        this.display_name = display_name;
    }
}