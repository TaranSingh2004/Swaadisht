package co.swaadisht.swaadisht.Services;

import co.swaadisht.swaadisht.entities.OrderAddress;
import co.swaadisht.swaadisht.entities.OrderRequest;
import co.swaadisht.swaadisht.entities.ProductOrder;
import co.swaadisht.swaadisht.entities.User;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface OrderService {

    public void saveOrder(Integer userId, OrderRequest orderAddress) throws MessagingException, UnsupportedEncodingException;

    public List<ProductOrder> getOrdersByUsers(int userId);

    void createOrder(int id, Integer addressId, String paymentMethod, String couponCode, Double discountAmount);

    ProductOrder getOrderByOrderIdAndUser(String orderId, User user);

    double calculateTotalOrderPrice(int id);

    List<ProductOrder> getUserOrders(int id);

    void cancelOrder(Long orderId, int id);

    Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize);

    ProductOrder updateOrderStatus(Integer id, String status);

    ProductOrder getOrdersByOrderId(String orderId);

    ProductOrder findById(Long orderId);


//    ProductOrder createOrder(int id, Integer addressId, String paymentMethod, String couponCode, Double discountAmount);

}
