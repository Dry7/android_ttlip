package dry7.ttlip20092015.Models;

/**
 * Created by Andrey Shilov on 20.09.2015.
 */
public class Category {
    private String id_category;
    private String id_parent;
    private String name;
    private String date_upd;
    private String image;

    public Category(String id_category, String id_parent, String name, String date_upd, String image) {
        this.id_category = id_category;
        this.id_parent = id_parent;
        this.name = name;
        this.date_upd = date_upd;
        this.image = image;
    }

    public String getId()
    {
        return this.id_category;
    }

    public String getParent()
    {
        return this.id_parent;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDateUpd()
    {
        return this.date_upd;
    }

    public String getImage()
    {
        return this.image;
    }
}
