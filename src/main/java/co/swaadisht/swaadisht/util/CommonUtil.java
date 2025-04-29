package co.swaadisht.swaadisht.util;

import co.swaadisht.swaadisht.Services.UserService;
import co.swaadisht.swaadisht.entities.OrderAddress;
import co.swaadisht.swaadisht.entities.OrderItem;
import co.swaadisht.swaadisht.entities.ProductOrder;
import co.swaadisht.swaadisht.entities.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

@Component
public class CommonUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    public Boolean sendMail(String url, String reciepentEmail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper= new MimeMessageHelper(message);
        helper.setFrom("ecommercetesttaran@gmail.com", "Swaadisht");
        helper.setTo(reciepentEmail);
        String content = "<p>Greetings from Swaadisht, </p>"
                +"<p>You have requested to reset your password</p>"
                +"<p>click the link below to change the password: </p>"
                +"<p><a href=\""
                +url
                + "\">Change my password</a></p>";
        helper.setSubject("Password reset");
        helper.setText(content, true);
        mailSender.send(message);
        return true;
    }

    public static String generateUrl(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(), "");
    }

    String msg=null;

    public Boolean sendMailForProductOrder(ProductOrder order, String status) throws MessagingException, UnsupportedEncodingException {
        msg = "<p> Hello [[name]],</p>" +
                "<p>Thanku your order <b>[[orderStatus]]</b>.</p>" +
                "<p>Product Details : </p>" +
                "<p>Name : [[productName]]</p>" +
                "<p>Category : [[category]]</p>" +
                "<p>Quantity : [[qauntity]]</p>" +
                "<p>Price : [[price]]</p>" +
                "<p>Payment Type : [[paymentType]]</p>";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper= new MimeMessageHelper(message);
        helper.setFrom("ecommercetesttaran@gmail.com", "Shopping Cart");
        helper.setTo(order.getUser().getEmail());

//        OrderStatus[] values = OrderStatus.values();
//        for(OrderStatus status: values){
//            if(status.getId().equals(status)){
//
//            }
//        }

        msg = msg.replace("[[name]]", order.getUser().getName());
        msg = msg.replace("[[orderStatus]]", status);
//        msg=msg.replace("[[productName]]", order.getProduct().getName());
//        msg=msg.replace("[[category]]", order.getProduct().getCategory().getName());
//        msg=msg.replace("[[qauntity]]", order.getQuantity().toString());
//        msg=msg.replace("[[price]]", order.getPrice().toString());
        msg=msg.replace("[[paymentType]]", order.getPaymentType());

        helper.setSubject("Password order status");
        helper.setText(msg, true);
        mailSender.send(message);
        return true;
    }

    public User getLoggedInUserDetails(Principal p){
        String email = p.getName();
        User userDtls = userService.getUserByEmail(email);
        return userDtls;
    }

    public Boolean sendOrderConfirmationEmail(ProductOrder order, User user)
            throws MessagingException, UnsupportedEncodingException {

        String subject = "Your Order #" + order.getId() + " Confirmation";
        String senderName = "Swaadisht Team";

        String content = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                + ".container { max-width: 600px; margin: 0 auto; padding: 20px; }"
                + ".header { background-color: #f8f9fa; padding: 15px; text-align: center; }"
                + ".order-details { margin: 20px 0; }"
                + ".table { width: 100%; border-collapse: collapse; }"
                + ".table th, .table td { padding: 8px; text-align: left; border-bottom: 1px solid #ddd; }"
                + ".footer { margin-top: 20px; font-size: 0.9em; color: #777; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'>"
                + "<h2>Thank you for your order!</h2>"
                + "</div>"
                + "<div class='order-details'>"
                + "<p>Hello [[name]],</p>"
                + "<p>Your order has been successfully placed. Here are your order details:</p>"
                + "<h3>Order Summary</h3>"
                + "<table class='table'>"
                + "<tr><th>Order Number</th><td>[[orderId]]</td></tr>"
                + "<tr><th>Order Date</th><td>[[orderDate]]</td></tr>"
                + "<tr><th>Payment Method</th><td>[[paymentMethod]]</td></tr>"
                + "<tr><th>Delivery Address</th><td>[[address]]</td></tr>"
                + "<h3>Order Items</h3>"
                + "<table class='table'>"
                + "<tr><th>Product</th><th>Quantity</th><th>Price</th></tr>"
                + "[[orderItems]]"
                + "</table>"
                + "<tr><th>Discount Amount</th><td>₹[[discountAmount]]</td></tr>"
                + "<tr><th>Total Amount</th><td>₹[[totalAmount]]</td></tr>"
                + "</table>"
                + "</div>"
                + "<div class='footer'>"
                + "<p>We'll notify you when your order ships. If you have any questions, please contact our support team.</p>"
                + "<p>Best regards,<br>Swaadisht Team</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        // Replace placeholders with actual values
        content = content.replace("[[name]]", user.getName());
        content = content.replace("[[orderId]]", order.getOrderId());
        content = content.replace("[[orderDate]]", order.getOrderDate().toString());
        content = content.replace("[[paymentMethod]]", order.getPaymentType());
        content = content.replace("[[address]]", formatAddress(order.getOrderAddress()));
        content = content.replace("[[discountAmount]]", String.format("%.2f", order.getDiscountAmount()));
        content = content.replace("[[totalAmount]]", String.format("%.2f", order.getTotalPrice()));

        // Build order items table rows
        StringBuilder itemsBuilder = new StringBuilder();
        for (OrderItem item : order.getOrderItems()) {
            itemsBuilder.append("<tr>")
                    .append("<td>").append(item.getProduct().getName()).append("</td>")
                    .append("<td>").append(item.getQuantity()).append("</td>")
                    .append("<td>₹").append(String.format("%.2f", item.getSelectedSize().getPrice())).append("</td>")
                    .append("</tr>");
        }
        content = content.replace("[[orderItems]]", itemsBuilder.toString());

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("ecommercetesttaran@gmail.com", senderName);
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
        return true;
    }

    private String formatAddress(OrderAddress address) {
        return address.getAddress() + ", " + address.getCity() + ", "
                + address.getState() + " - " + address.getPincode();
    }
}
