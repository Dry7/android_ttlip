package dry7.ttlip20092015.Models;

/**
 * Created by Andrey Shilov on 20.09.2015.
 */
public class Product {
    private String id_product;
    private String price;
    private String name;
    private String description;
    private String image;

    public Product(String id_product, String price, String name, String description, String image)
    {
        this.id_product = id_product;
        this.price = price;
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public String getId()
    {
        return this.id_product;
    }

    public String getPrice()
    {
        return this.price;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getImage()
    {
        return this.image;
    }
}