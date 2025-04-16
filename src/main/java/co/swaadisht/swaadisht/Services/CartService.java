package co.swaadisht.swaadisht.Services;

import co.swaadisht.swaadisht.entities.Cart;

import java.util.List;

public interface CartService {

    public List<Cart> getCartByUser(Integer userId);

    Cart saveCart(Integer productId, Integer userId, boolean isCustomized, List<Integer> selectedIngredients, List<Integer> selectedToppings, Integer quantity, Integer selectedSizeId);

    boolean deleteProduct(int id);

    boolean deleteCartItem(int id);

    public void updateQuantity(String s, Integer cid);

    boolean isProductInCarts(int id);

    double calculateTotalOrderPrice(int id);

    void clearUserCart(int id);
}
