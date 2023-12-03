package ca.hermeslogistics.itservices.petasosexpress;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ProductTest {

    private Product product;

    @Before
    public void setUp() {
        product = new Product("Apple", 101, 0.99, "FruitCorp", "Fruit");
    }

    @Test
    public void getDisplayName() {
        String expectedDisplayName = "Apple - $0.99";
        assertEquals(expectedDisplayName, product.getDisplayName());
    }

    @Test
    public void getCartName() {
        product.setQuantity(5);
        String expectedCartName = "Apple - $0.99 (Qty: 5)";
        assertEquals(expectedCartName, product.getCartName());
    }

    @Test
    public void matchesQuery_ValidQuery() {
        assertTrue(product.matchesQuery("apple"));
        assertTrue(product.matchesQuery("FruitCorp"));
        assertTrue(product.matchesQuery("fruit"));
    }

    @Test
    public void matchesQuery_InvalidQuery() {
        assertFalse(product.matchesQuery("banana"));
    }

    @Test
    public void setAndGetQuantity() {
        product.setQuantity(10);
        assertEquals(10, product.getQuantity());
    }
    @Test
    public void getName() {
        assertNotNull(product.getName());
        assertEquals("Apple", product.getName());
    }
    @Test
    public void getId() {
        assertEquals(101, product.getId());
    }
    @Test
    public void getPrice() {
        assertEquals(0.99, product.getPrice(), 0.001);
    }

}
