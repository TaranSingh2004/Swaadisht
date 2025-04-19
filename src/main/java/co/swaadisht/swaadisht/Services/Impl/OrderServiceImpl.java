package co.swaadisht.swaadisht.Services.Impl;

import co.swaadisht.swaadisht.Repository.CartRepository;
import co.swaadisht.swaadisht.Repository.OrderAddressRepository;
import co.swaadisht.swaadisht.Repository.ProductOrderRepository;
import co.swaadisht.swaadisht.Repository.UserRepository;
import co.swaadisht.swaadisht.Services.CartService;
import co.swaadisht.swaadisht.Services.OrderService;
import co.swaadisht.swaadisht.Services.ShippingCalculatorService;
import co.swaadisht.swaadisht.Services.UserService;
import co.swaadisht.swaadisht.entities.*;
import co.swaadisht.swaadisht.util.CommonUtil;
import co.swaadisht.swaadisht.util.OrderStatus;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductOrderRepository orderRepository;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderAddressRepository addressRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ShippingCalculatorService shippingService;

    @Autowired
    private UserService userService;

    private User getLoggedInUserDetails(Principal p){
        String email = p.getName();
        User userDtls = userService.getUserByEmail(email);
        return userDtls;
    }

    @Override
    @Transactional
    public void saveOrder(Integer userId, OrderRequest orderRequest) throws MessagingException, UnsupportedEncodingException {
        // Get user carts
        OrderAddress address = addressRepository.findById(orderRequest.getOrderAddress().getId()).orElseThrow();

        String originPincode = "121004"; // Your warehouse/store pincode
        String destinationPincode = address.getPincode();

        double shippingCharge = shippingService.calculateShippingCharge(originPincode, destinationPincode);
        List<Cart> carts = cartRepository.findByUserId(userId);
        for(Cart cart : carts){
            ProductOrder order = new ProductOrder();
            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(LocalDate.now());
//            order.setProduct(cart.getProduct());
//            order.setPrice((double) cart.getProduct().getDiscountPrice());
//            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());
            order.setStatus(OrderStatus.IN_PROGRESS.getName());
            order.setPaymentType(orderRequest.getPaymentType());
            order.setOrderAddress(orderRequest.getOrderAddress());
            ProductOrder savedOrder = orderRepository.save(order);
        }
    }

    @Override
    public List<ProductOrder> getOrdersByUsers(int userId) {
        List<ProductOrder> orders = orderRepository.findByUserId(userId);
        return orders;
    }

    @Override
    public void createOrder(int userId, Integer addressId, String paymentMethod,
                            String couponCode, Double discountAmount) {

        User user = userRepository.findById(userId).orElseThrow();
        OrderAddress address = addressRepository.findById(addressId).orElseThrow();

        // Calculate shipping
        String originPincode = "121004";
        String destinationPincode = address.getPincode();
        double shippingCharge = shippingService.calculateShippingCharge(originPincode, destinationPincode);

        // Get all cart items
        List<Cart> cartItems = cartRepository.findByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty. Cannot place order.");
        }

        // Create a single ProductOrder
        ProductOrder order = new ProductOrder();
//        order.setOrderId(UUID.randomUUID().toString());
        order.setOrderDate(LocalDate.now());
        order.setUser(user);
        order.setStatus(OrderStatus.IN_PROGRESS.getName());
        order.setPaymentType(paymentMethod);
        order.setOrderAddress(address);
        order.setCouponCode(couponCode);
        order.setShippingCharges(shippingCharge);
        order.setDiscountAmount(discountAmount);

        double totalOrderPrice = 0.0;
        double totalDiscount = 0.0;

        for (Cart cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice((double) cartItem.getProduct().getDiscountPrice());
            orderItem.setTotalPrice((double) (cartItem.getProduct().getDiscountPrice() * cartItem.getQuantity()));
            orderItem.setCustomised(cartItem.getProduct().isCustomizable());

            if (cartItem.getSelectedIngredients() != null) {
                orderItem.setSelectedIngredients(new ArrayList<>(cartItem.getSelectedIngredients()));
            }
            if (cartItem.getSelectedToppings() != null) {
                orderItem.setSelectedToppings(new ArrayList<>(cartItem.getSelectedToppings()));
            }
            if (cartItem.getSelectedSize() != null) {
                orderItem.setSelectedSize(cartItem.getSelectedSize());
            }
            orderItem.setTotalPrice(orderItem.getSelectedSize().getPrice() * orderItem.getQuantity());

            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
            totalOrderPrice += orderItem.getTotalPrice();

//            discountAmount += orderItem.getProduct().getDiscountPrice() - orderItem.getProduct().getDiscountPrice() * orderItem.getProduct().getDiscount();

            // Mark cart item as ordered
            cartItem.setOrdered(true);
            cartRepository.save(cartItem);
        }
        order.setPrice(totalOrderPrice);

        order.setTotalPrice(totalOrderPrice + shippingCharge);

        // Save the entire order with all order items
        orderRepository.save(order);

        // Clear the cart after all items are processed
        cartService.clearUserCart(userId);
    }


    @Override
    public ProductOrder getOrderByOrderIdAndUser(String orderId, User user) {
        return orderRepository.findByOrderIdAndUser(orderId, user);
    }

    @Override
    public double calculateTotalOrderPrice(int id) {
        return 0;
    }

}
