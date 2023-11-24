package ca.hermeslogistics.itservices.petasosexpress;
/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
import java.util.Locale;

public class Product {
    private String name;
    private double price;
    private int id;
    private String producer;
    private String type;

    public Product(String name, int id, double price, String producer, String type) {
        this.name = name;
        this.id = id;
        this.price = price;
        this.producer = producer;
        this.type = type;
    }

    public String getDisplayName() {
        return name + " - $" + String.format(Locale.getDefault(), "%.2f", price);
    }

    public String getName() {
        return name;
    }

    public int getId(){return id;}

    public double getPrice() {
        return price;
    }

    public String getProducer() {
        return producer;
    }

    public String getType() {
        return type;
    }
    public boolean matchesQuery(String query) {
        query = query.toLowerCase(Locale.getDefault());
        return name.toLowerCase(Locale.getDefault()).contains(query)
                || (producer != null && producer.toLowerCase(Locale.getDefault()).contains(query))
                || (type != null && type.toLowerCase(Locale.getDefault()).contains(query));
    }
}

