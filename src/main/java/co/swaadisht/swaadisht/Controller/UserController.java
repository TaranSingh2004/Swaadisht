package co.swaadisht.swaadisht.Controller;

import co.swaadisht.swaadisht.Services.*;
import co.swaadisht.swaadisht.entities.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryServices categoryService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ShippingCalculatorService shippingService;


    @ModelAttribute
    public void getUserDetails(Principal p, Model m){
        if(p!=null){
            String email= p.getName();
            User user = userService.getUserByEmail(email);
            m.addAttribute("user", user);
        }
        List<Category> list = categoryService.getAllActiveCategory();
        m.addAttribute("categorys", list);
    }

    private User getLoggedInUserDetails(Principal p){
        String email = p.getName();
        User userDtls = userService.getUserByEmail(email);
        return userDtls;
    }

    @GetMapping("/")
    public String home() {
        return "user/home";
    }

    @GetMapping("/cart")
    public String cart(Principal p, Model m){
        User user = getLoggedInUserDetails(p);
        List<Cart> Cart = cartService.getCartByUser(user.getId());
        m.addAttribute("carts", Cart);
        m.addAttribute("NoProduct", Cart.isEmpty());
        if(!Cart.isEmpty()){
            m.addAttribute("totalOrderPrice", Cart.get(Cart.size()-1).getTotalOrderPrice());
        }
        return "user/cart1";
    }

    @PostMapping("/cart/add")
    public String addToCart(
            @RequestParam Integer productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            @RequestParam(defaultValue = "standard") String customizationType,
            @RequestParam(required = false) List<Integer> selectedIngredients,
            @RequestParam(required = false) List<Integer> selectedToppings,
            @RequestParam(required = false) Integer selectedSizeId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            boolean isCustomized = "customized".equals(customizationType);
            User user = userService.findByEmail(principal.getName());
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            // Handle null lists for non-customized items
            if (!isCustomized) {
                selectedIngredients = new ArrayList<>();
                selectedToppings = new ArrayList<>();
            }

            Cart cart = cartService.saveCart(productId, user.getId(), isCustomized,
                    selectedIngredients, selectedToppings, quantity, selectedSizeId);

            redirectAttributes.addFlashAttribute("success", "Item added to cart!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/product/" + productId;
    }

    @GetMapping("deleteCart/{id}")
    public String deleteCartItem(@PathVariable int id, HttpSession session) {
        try {
            cartService.deleteCartItem(id);
            session.setAttribute("succMsg", "Cart item removed successfully");
            session.removeAttribute("couponApplied");
            session.removeAttribute("couponMessage");
        } catch (Exception e) {
            session.setAttribute("errMsg", "Failed to remove cart item: " + e.getMessage());
        }
        return "redirect:/user/cart";
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateQuantity(@RequestParam String s, @RequestParam Integer cid, HttpSession session){
        cartService.updateQuantity(s, cid);
        session.removeAttribute("couponApplied");
        session.removeAttribute("couponMessage");
        return "redirect:/user/cart";
    }

    @PostMapping("/cart/applyCoupon")
    public String applyCoupon(@RequestParam("coupon") String coupon, HttpSession session) {
        if ("VEDANT10".equalsIgnoreCase(coupon.trim())) {
            session.setAttribute("couponApplied", true);
            session.setAttribute("couponMessage", "Coupon applied successfully! 10% discount added.");
        } else {
            session.setAttribute("couponMessage", "Invalid coupon code.");
        }
        return "redirect:/user/cart";
    }

    @GetMapping("/orders")
    public String orderPage(Principal p, Model m, HttpSession session) {
        User user = getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartByUser(user.getId());

        // Clear any existing shipping charge to force recalculation
        session.removeAttribute("shippingCharge");

        if (!user.getAddresses().isEmpty()) {
            // Get selected address (either from session or first address)
            Integer selectedAddressId = (Integer) session.getAttribute("selectedAddressId");
            OrderAddress selectedAddress = selectedAddressId != null ?
                    user.getAddresses().stream()
                            .filter(a -> a.getId().equals(selectedAddressId))
                            .findFirst()
                            .orElse(user.getAddresses().get(0)) :
                    user.getAddresses().get(0);

            m.addAttribute("selectedAddressId", selectedAddress.getId());

            // Always recalculate shipping for the selected address
            String originPincode = "121004";
            double shippingCharge = shippingService.calculateShippingCharge(
                    originPincode,
                    selectedAddress.getPincode()
            );
            session.setAttribute("shippingCharge", shippingCharge);
        }

        // Calculate order totals
        double subtotal = cartService.calculateTotalOrderPrice(user.getId());
        Double shippingCharge = (Double) session.getAttribute("shippingCharge");
        if (shippingCharge == null) shippingCharge = 0.0;

        Double discountAmount = (Double) session.getAttribute("discountAmount");
        double total = subtotal + shippingCharge - (discountAmount != null ? discountAmount : 0);

        // Add attributes to model
        m.addAttribute("carts", carts);
        m.addAttribute("subtotal", subtotal);
        m.addAttribute("shipping", shippingCharge);
        m.addAttribute("discountAmount", discountAmount);
        m.addAttribute("total", total);
        m.addAttribute("couponCode", session.getAttribute("couponCode"));
        m.addAttribute("user", user);

        return "user/order";
    }

    @PostMapping("/save-address")
    public String saveAddress(@Valid @ModelAttribute OrderAddress address,
                              BindingResult result,
                              Principal principal,
                              HttpSession session,
                              @RequestParam(required = false, defaultValue = "cart") String source) {

        if (result.hasErrors()) {
            session.setAttribute("errorMsg", "Please fill all address fields correctly");
            return "redirect:/user/" + source;
        }

        User user = getLoggedInUserDetails(principal);
        OrderAddress savedAddress = userService.saveAddress(user.getId(), address);

        if (savedAddress != null) {
            session.setAttribute("succMsg", "Address saved successfully");
            return "redirect:/user/" + source; // Redirect to orders page
        } else {
            session.setAttribute("errorMsg", "Failed to save address. Please try again.");
            return "redirect:/user/" + source;
        }
    }

    @PostMapping("/select-address")
    public String selectAddress(@RequestParam Integer addressId,
                                HttpSession session,
                                Principal principal) {
        try {
            User user = getLoggedInUserDetails(principal);
            OrderAddress address = userService.getAddressById(addressId);

            // Store selected address in session
            session.setAttribute("selectedAddressId", addressId);

            // Clear shipping charge to force recalculation
            session.removeAttribute("shippingCharge");

            return "redirect:/user/orders";
        } catch (Exception e) {
            // Handle error
            return "redirect:/user/orders";
        }
    }

    @PostMapping("/apply-coupon")
    public String applyCoupon(@RequestParam("coupon") String coupon, HttpSession session, Principal principal, RedirectAttributes redirectAttributes){
        User user = getLoggedInUserDetails(principal);
        double cartTotal = cartService.calculateTotalOrderPrice(user.getId());

        if (couponService.validateCoupon(coupon)) {
            double discountAmount = couponService.calculateDiscount(coupon, cartTotal);

            session.setAttribute("couponCode", coupon);
            session.setAttribute("discountAmount", discountAmount);

            redirectAttributes.addFlashAttribute("couponMessage", "Coupon applied successfully!");
            redirectAttributes.addFlashAttribute("couponApplied", true);
        } else {
            redirectAttributes.addFlashAttribute("couponMessage", "Invalid coupon code");
            redirectAttributes.addFlashAttribute("couponApplied", false);
        }

        return "redirect:/user/orders";
    }

    @PostMapping("/place-order")
    public String placeOrder(@RequestParam Integer addressId,
                             @RequestParam String paymentMethod,
                             Principal principal,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        User user = getLoggedInUserDetails(principal);

        if (addressId == null) {
            redirectAttributes.addFlashAttribute("error", "Please select a delivery address");
            return "redirect:/user/orders";
        }

        try {
            String couponCode = (String) session.getAttribute("couponCode");
            Double discountAmount = (Double) session.getAttribute("discountAmount");

            orderService.createOrder(
                    user.getId(),
                    addressId,
                    paymentMethod,
                    couponCode,
                    discountAmount
            );

            cartService.clearUserCart(user.getId());
            session.removeAttribute("couponCode");
            session.removeAttribute("discountAmount");

            // Redirect to order success page with order ID
            return "redirect:/user/order-success";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to place order: " + e.getMessage());
            return "redirect:/user/orders";
        }

    }

    @GetMapping("/order-success")
    public String orderSuccess(Principal principal, Model model) {
        User user = getLoggedInUserDetails(principal);

        return "user/order-success";
    }

    @PostMapping("/delete-address/{addressId}")
    public String deleteAddress(@PathVariable Integer addressId,
                                Principal principal,
                                RedirectAttributes redirectAttributes,
                                @RequestParam(required = false, defaultValue = "cart") String source) {
        try {
            User user = getLoggedInUserDetails(principal);
            addressService.deleteAddress(user.getId(), addressId);
            redirectAttributes.addFlashAttribute("success", "Address deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/" + source;
    }

    @GetMapping("/my-orders")
    public String viewUserOrders(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());
        List<ProductOrder> orders = orderService.getUserOrders(user.getId());
        model.addAttribute("orders", orders);
        return "user/my-orders"; // matches your HTML template name
    }

    @GetMapping("/cancel-order/{orderId}")
    public String cancelOrder(@PathVariable Long orderId,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(principal.getName());
            ProductOrder order = orderService.findById(orderId);
            if(order.getStatus().equals("CANCELLED") || order.getStatus().equals("OUT FOR DELIVERY") || order.getStatus().equals("DELIVERED")){
                return "redirect:/user/my-orders";
            }
            orderService.cancelOrder(orderId, user.getId());
            redirectAttributes.addFlashAttribute("success", "Order cancelled successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel order: " + e.getMessage());
        }
        return "redirect:/user/my-orders";
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "user/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute User user,
                                Principal principal,
                                HttpSession session) {
        try {
            User currentUser = userService.findByEmail(principal.getName());
            // Update only allowed fields
            currentUser.setName(user.getName());
            currentUser.setPhoneNumber(user.getPhoneNumber());

            User updatedUser = userService.updateUserProfile(currentUser);
            session.setAttribute("succMsg", "Profile updated successfully");
        } catch (Exception e) {
            session.setAttribute("errorMsg", "Profile update failed: " + e.getMessage());
        }
        return "redirect:/user/profile";
    }
}
