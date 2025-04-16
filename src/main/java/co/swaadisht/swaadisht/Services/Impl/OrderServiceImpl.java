package co.swaadisht.swaadisht.Services.Impl;

import co.swaadisht.swaadisht.Repository.CartRepository;
import co.swaadisht.swaadisht.Repository.OrderAddressRepository;
import co.swaadisht.swaadisht.Repository.ProductOrderRepository;
import co.swaadisht.swaadisht.Repository.UserRepository;
import co.swaadisht.swaadisht.Services.CartService;
import co.swaadisht.swaadisht.Services.OrderService;
import co.swaadisht.swaadisht.entities.*;
import co.swaadisht.swaadisht.util.CommonUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
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

    @Override
    public void saveOrder(Integer userId, OrderAddress orderAddress) throws MessagingException, UnsupportedEncodingException {

        List<Cart> carts = cartRepository.findByUserId(userId);

        for(Cart cart : carts){
            ProductOrder order = new ProductOrder();
            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(LocalDate.now());
            order.setProduct(cart.getProduct());
            order.setPrice((double) cart.getProduct().getDiscountPrice());
            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());

            OrderAddress address = new OrderAddress();
            address.setAddress(orderAddress.getAddress());
            address.setState(orderAddress.getState());
            address.setCity(orderAddress.getCity());
            address.setPincode(orderAddress.getPincode());

            order.setOrderAddress(address);

            ProductOrder saveOrder = orderRepository.save(order);

            commonUtil.sendMailForProductOrder(saveOrder, "success");
        }
    }

    @Override
    public List<ProductOrder> getOrdersByUsers(int userId) {
        List<ProductOrder> orders = orderRepository.findByUserId(userId);
        return orders;
    }

    @Override
    public ProductOrder createOrder(int id, Integer addressId, String paymentMethod, String couponCode, Double discountAmount) {
        User user = userRepository.findById(id).orElseThrow();
        OrderAddress address = addressRepository.findById(addressId).orElseThrow();
        List<Cart> cartItems = cartService.getCartByUser(id);

        // Calculate order total
        double subtotal = cartService.calculateTotalOrderPrice(id);
        double total = subtotal - (discountAmount != null ? discountAmount : 0);

        // Create and save order
        ProductOrder order = new ProductOrder();
        order.setUser(user);
        order.setOrderAddress(address);
        order.setPaymentType(paymentMethod);
        order.setCouponCode(couponCode);
        order.setDiscountAmount(discountAmount);
        order.setOriginalPrice(subtotal);
        order.setPrice(total);
        order.setOrderDate(LocalDate.now());
        order.setStatus("Pending");

        return orderRepository.save(order);
    }

    @Override
    public ProductOrder getOrderByOrderIdAndUser(String orderId, User user) {
        return orderRepository.findByOrderIdAndUser(orderId, user);
    }

}
