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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public Cart saveCart(Integer productId,
                         Integer userId,
                         boolean isCustomized,
                         List<Integer> selectedIngredientIds,
                         List<Integer> selectedToppingIds,
                         Integer quantity) {

        if (productId == null || userId == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        User userDtls = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Cart cart = cartRepository.findByProductAndUser(product, userDtls);

        if (cart == null) {
            cart = new Cart();
            cart.setProduct(product);
            cart.setUser(userDtls);
            cart.setQuantity(quantity);
            cart.setCustomized(isCustomized);

            if (isCustomized) {
                List<CustomizationIngredient> ingredients = ingredientRepository.findAllById(selectedIngredientIds);
                List<Toppings> toppings = toppingRepository.findAllById(selectedToppingIds);

                // Validate customizations
                if (!product.getAvailableIngredients().containsAll(ingredients) ||
                        !product.getAvailableToppings().containsAll(toppings)) {
                    throw new IllegalArgumentException("Invalid customization selection");
                }

                cart.setSelectedIngredients(ingredients);
                cart.setSelectedToppings(toppings);
            }
        } else {
            cart.setQuantity(cart.getQuantity() + quantity);
        }

        return cartRepository.save(cart);
    }

    @Override
    public boolean deleteProduct(int id) {
        Cart cart = cartRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not deleted"));
        if(ObjectUtils.isEmpty(cart)){
            cartRepository.delete(cart);
            return true;
        }
        return false;
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
}
