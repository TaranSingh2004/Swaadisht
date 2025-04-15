package co.swaadisht.swaadisht.Services.Impl;

import co.swaadisht.swaadisht.Repository.*;
import co.swaadisht.swaadisht.Services.CartService;
import co.swaadisht.swaadisht.Services.CustomizationIngredientService;
import co.swaadisht.swaadisht.Services.ToppingService;
import co.swaadisht.swaadisht.entities.*;
import co.swaadisht.swaadisht.helpers.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl  implements CartService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomizationIngredientRepository ingredientRepository;

    @Autowired
    private ToppingRepository toppingRepository;

    @Override
    @Transactional
    public Cart saveCart(Integer productId, Integer userId, boolean isCustomized,
                         List<Integer> selectedIngredientIds, List<Integer> selectedToppingIds,
                         Integer quantity) {

        // Validate inputs
        if (productId == null || userId == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Find existing carts for this product/user combination
        List<Cart> existingCarts = cartRepository.findByProductAndUserAndCustomizationStatus(
                productId, userId, isCustomized);

        // Try to find matching cart with same customizations
        Cart matchingCart = findMatchingCart(existingCarts, selectedIngredientIds, selectedToppingIds);

        if (matchingCart == null) {
            // Create new cart item
            matchingCart = new Cart();
            matchingCart.setProduct(product);
            matchingCart.setUser(user);
            matchingCart.setQuantity(quantity);
            matchingCart.setCustomized(isCustomized);

            if (isCustomized) {
                // Handle ingredients
                List<CustomizationIngredient> ingredients = ingredientRepository.findAllById(selectedIngredientIds);
                List<Toppings> toppings = toppingRepository.findAllById(selectedToppingIds);

                // Validate customizations
                validateCustomizations(product, ingredients, toppings);

                matchingCart.setSelectedIngredients(ingredients);
                matchingCart.setSelectedToppings(toppings);
            }
        } else {
            // Update quantity of existing cart item
            matchingCart.setQuantity(matchingCart.getQuantity() + quantity);
        }

        return cartRepository.save(matchingCart);
    }

    private Cart findMatchingCart(List<Cart> existingCarts,
                                  List<Integer> selectedIngredientIds,
                                  List<Integer> selectedToppingIds) {
        for (Cart cart : existingCarts) {
            // For non-customized items, any match is fine
            if (!cart.isCustomized()) {
                return cart;
            }

            // For customized items, check if ingredients and toppings match exactly
            boolean ingredientsMatch = cart.getSelectedIngredients().stream()
                    .map(CustomizationIngredient::getId)
                    .collect(Collectors.toSet())
                    .equals(new HashSet<>(selectedIngredientIds));

            boolean toppingsMatch = cart.getSelectedToppings().stream()
                    .map(Toppings::getId)
                    .collect(Collectors.toSet())
                    .equals(new HashSet<>(selectedToppingIds));

            if (ingredientsMatch && toppingsMatch) {
                return cart;
            }
        }
        return null;
    }

    private void validateCustomizations(Product product,
                                        List<CustomizationIngredient> ingredients,
                                        List<Toppings> toppings) {
        // Check all ingredients are available for this product
        Set<Integer> availableIngredientIds = product.getAvailableIngredients().stream()
                .map(CustomizationIngredient::getId)
                .collect(Collectors.toSet());

        for (CustomizationIngredient ingredient : ingredients) {
            if (!availableIngredientIds.contains(ingredient.getId())) {
                throw new IllegalArgumentException("Invalid ingredient selection");
            }
        }

        // Check all toppings are available for this product
        Set<Integer> availableToppingIds = product.getAvailableToppings().stream()
                .map(Toppings::getId)
                .collect(Collectors.toSet());

        for (Toppings topping : toppings) {
            if (!availableToppingIds.contains(topping.getId())) {
                throw new IllegalArgumentException("Invalid topping selection");
            }
        }
    }

    @Override
    @Transactional
    public boolean deleteProduct(int id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        // Clear associations first
        cart.getSelectedIngredients().clear();
        cart.getSelectedToppings().clear();
        cartRepository.saveAndFlush(cart); // Ensure changes are flushed

        // Now delete the cart
        cartRepository.delete(cart);
        return true;
    }

    @Override
    public List<Cart> getCartByUser(Integer userId) {
        List<Cart> cart = cartRepository.findByUserId(userId);
        Double totalOrderPrice = 0.0;
        List<Cart> updatedCarts = new ArrayList<>();
        for(Cart c : cart){
            Double totalPrice= (double) (c.getProduct().getDiscountPrice()*c.getQuantity());
            c.setTotalPrice(totalPrice);
            totalOrderPrice+=totalPrice;
            c.setTotalOrderPrice(totalOrderPrice);
            updatedCarts.add(c);
        }
        return updatedCarts;
    }

    public boolean deleteCartItem(int cartId) {
        try {
            // First, find the cart item
            Optional<Cart> cartOptional = cartRepository.findById(cartId);
            if (cartOptional.isPresent()) {
                // Delete the cart item
                cartRepository.delete(cartOptional.get());
                return true;
            }
            return false;
        } catch (Exception e) {
            // Log the error if needed
            throw new RuntimeException("Failed to delete cart item: " + e.getMessage());
        }
    }

    @Override
    public void updateQuantity(String s, Integer cid) {
        Cart cart = cartRepository.findById(cid).get();
        int updateQuantity;
        if(s.equalsIgnoreCase("de")){
            updateQuantity = cart.getQuantity()-1;
            if(updateQuantity<=0) cartRepository.delete(cart);
            else{
                cart.setQuantity(updateQuantity);
                cartRepository.save(cart);
            }
        } else {
            updateQuantity = cart.getQuantity()+1;
            cart.setQuantity(updateQuantity);
            cartRepository.save(cart);
        }
    }

    public boolean isProductInCarts(int productId) {
        return cartRepository.existsByProductId(productId);
    }

    @Override
    public double calculateTotalOrderPrice(int id) {
        List<Cart> cartItems = cartRepository.findByUserIdAndOrderedFalse(id);

        if (cartItems == null || cartItems.isEmpty()) {
            return 0.0;
        }

        return cartItems.stream()
                .mapToDouble(cart -> {
                    double productPrice = cart.getProduct().getDiscountPrice() * cart.getQuantity();
                    cart.setTotalPrice(productPrice); // Update the cart item's total price
                    return productPrice;
                })
                .sum();
    }

    @Override
    public void clearUserCart(int id) {
        cartRepository.deleteByUserIdAndOrderedFalse(id);
    }
}
