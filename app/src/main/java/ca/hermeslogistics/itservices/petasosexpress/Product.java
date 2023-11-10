package ca.hermeslogistics.itservices.petasosexpress;

import java.util.Locale;

public class Product {
    private String name;
    private double price;
    private String producer;
    private String type;

    public Product(String name, double price, String producer, String type) {
        this.name = name;
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

