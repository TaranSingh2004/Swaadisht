package co.swaadisht.swaadisht.Services;

import co.swaadisht.swaadisht.entities.OrderAddress;
import co.swaadisht.swaadisht.entities.OrderRequest;
import co.swaadisht.swaadisht.entities.ProductOrder;
import co.swaadisht.swaadisht.entities.User;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface OrderService {

    public void saveOrder(Integer userId, OrderRequest orderAddress) throws MessagingException, UnsupportedEncodingException;

    public List<ProductOrder> getOrdersByUsers(int userId);

    void createOrder(int id, Integer addressId, String paymentMethod, String couponCode, Double discountAmount);

    ProductOrder getOrderByOrderIdAndUser(String orderId, User user);

    double calculateTotalOrderPrice(int id);


//    ProductOrder createOrder(int id, Integer addressId, String paymentMethod, String couponCode, Double discountAmount);

}
