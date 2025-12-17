package com.bookstore.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookstore.domain.*;
import com.bookstore.service.*;
import com.bookstore.utility.MailConstructor;
import com.bookstore.utility.USConstants;

@Controller
public class CheckoutController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailConstructor mailConstructor;

    @Autowired
    private UserService userService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ShippingAddressService shippingAddressService;

    @Autowired
    private BillingAddressService billingAddressService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserShippingService userShippingService;

    @Autowired
    private UserPaymentService userPaymentService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/checkout")
    public String checkout(@RequestParam("id") Long cartId,
                           @RequestParam(value = "missingRequiredField", required = false) boolean missingRequiredField,
                           Model model, Principal principal) {
        if (principal == null) {
            System.out.println("⚠️  Principal is NULL. User not authenticated.");
            return "redirect:/login";
        }

        System.out.println("✅ Principal: " + principal.getName());
        User user = userService.findByUsername(principal.getName());

        if (cartId != user.getShoppingCart().getId()) {
            return "badRequestPage";
        }

        List<CartItem> cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());

        if (cartItemList.isEmpty()) {
            model.addAttribute("emptyCart", true);
            return "forward:/shoppingCart/cart";
        }

        if (cartItemList.stream().anyMatch(cartItem -> cartItem.getBook().getInStockNumber() < cartItem.getQty())) {
            model.addAttribute("notEnoughStock", true);
            return "forward:/shoppingCart/cart";
        }

        List<UserShipping> userShippingList = user.getUserShippingList();
        List<UserPayment> userPaymentList = user.getUserPaymentList();

        model.addAttribute("userShippingList", userShippingList);
        model.addAttribute("userPaymentList", userPaymentList);
        model.addAttribute("emptyPaymentList", userPaymentList.isEmpty());
        model.addAttribute("emptyShippingList", userShippingList.isEmpty());

        ShoppingCart shoppingCart = user.getShoppingCart();

        // Create fresh instances inside the method
        ShippingAddress shippingAddress = new ShippingAddress();
        BillingAddress billingAddress = new BillingAddress();
        Payment payment = new Payment();

        userShippingList.stream()
                .filter(UserShipping::isUserShippingDefault)
                .findFirst()
                .ifPresent(userShipping -> shippingAddressService.setByUserShipping(userShipping, shippingAddress));

        userPaymentList.stream()
                .filter(UserPayment::isDefaultPayment)
                .findFirst()
                .ifPresent(userPayment -> {
                    paymentService.setByUserPayment(userPayment, payment);
                    billingAddressService.setByUserBilling(userPayment.getUserBilling(), billingAddress);
                });

        model.addAttribute("shippingAddress", shippingAddress);
        model.addAttribute("payment", payment);
        model.addAttribute("billingAddress", billingAddress);
        model.addAttribute("cartItemList", cartItemList);
        model.addAttribute("shoppingCart", shoppingCart);

        List<String> stateList = new ArrayList<>(USConstants.listOfUSStatesCode);
        Collections.sort(stateList);
        model.addAttribute("stateList", stateList);

        model.addAttribute("classActiveShipping", true);
        model.addAttribute("missingRequiredField", missingRequiredField);

        return "checkout";
    }

    @PostMapping("/checkout")
    public String checkoutPost(@ModelAttribute("shippingAddress") ShippingAddress shippingAddress,
                               @ModelAttribute("billingAddress") BillingAddress billingAddress,
                               @ModelAttribute("payment") Payment payment,
                               @ModelAttribute("billingSameAsShipping") String billingSameAsShipping,
                               @ModelAttribute("shippingMethod") String shippingMethod,
                               Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        ShoppingCart shoppingCart = user.getShoppingCart();

        List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
        model.addAttribute("cartItemList", cartItemList);

        if (Boolean.parseBoolean(billingSameAsShipping)) {
            billingAddress.setBillingAddressName(shippingAddress.getShippingAddressName());
            billingAddress.setBillingAddressStreet1(shippingAddress.getShippingAddressStreet1());
            billingAddress.setBillingAddressStreet2(shippingAddress.getShippingAddressStreet2());
            billingAddress.setBillingAddressCity(shippingAddress.getShippingAddressCity());
            billingAddress.setBillingAddressState(shippingAddress.getShippingAddressState());
            billingAddress.setBillingAddressCountry(shippingAddress.getShippingAddressCountry());
            billingAddress.setBillingAddressZipcode(shippingAddress.getShippingAddressZipcode());
        }

        if (shippingAddress.getShippingAddressStreet1().isEmpty()
                || shippingAddress.getShippingAddressCity().isEmpty()
                || shippingAddress.getShippingAddressState().isEmpty()
                || shippingAddress.getShippingAddressName().isEmpty()
                || shippingAddress.getShippingAddressZipcode().isEmpty()
                || payment.getCardNumber().isEmpty()
                || payment.getCvc() == 0 || billingAddress.getBillingAddressStreet1().isEmpty()
                || billingAddress.getBillingAddressCity().isEmpty()
                || billingAddress.getBillingAddressState().isEmpty()
                || billingAddress.getBillingAddressName().isEmpty()
                || billingAddress.getBillingAddressZipcode().isEmpty())
            return "redirect:/checkout?id=" + shoppingCart.getId() + "&missingRequiredField=true";

        Order order = orderService.createOrder(shoppingCart, shippingAddress, billingAddress, payment, shippingMethod, user);

        mailSender.send(mailConstructor.constructOrderConfirmationEmail(user, order, Locale.ENGLISH));

        shoppingCartService.clearShoppingCart(shoppingCart);

        LocalDate today = LocalDate.now();
        LocalDate estimatedDeliveryDate = shippingMethod.equals("groundShipping") ? today.plusDays(5) : today.plusDays(3);

        model.addAttribute("estimatedDeliveryDate", estimatedDeliveryDate);

        return "orderSubmittedPage";
    }

    @GetMapping("/setShippingAddress")
    public String setShippingAddress(@RequestParam("userShippingId") Long userShippingId, Principal principal,
                                     Model model) {
        User user = userService.findByUsername(principal.getName());
        UserShipping userShipping = userShippingService.findById(userShippingId);

        if (userShipping.getUser().getId() != user.getId()) {
            return "badRequestPage";
        } else {
            ShippingAddress shippingAddress = new ShippingAddress();
            shippingAddressService.setByUserShipping(userShipping, shippingAddress);

            Payment payment = new Payment();
            BillingAddress billingAddress = new BillingAddress();

            List<CartItem> cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());

            model.addAttribute("shippingAddress", shippingAddress);
            model.addAttribute("payment", payment);
            model.addAttribute("billingAddress", billingAddress);
            model.addAttribute("cartItemList", cartItemList);
            model.addAttribute("shoppingCart", user.getShoppingCart());

            List<String> stateList = new ArrayList<>(USConstants.listOfUSStatesCode);
            Collections.sort(stateList);
            model.addAttribute("stateList", stateList);

            List<UserShipping> userShippingList = user.getUserShippingList();
            List<UserPayment> userPaymentList = user.getUserPaymentList();

            model.addAttribute("userShippingList", userShippingList);
            model.addAttribute("userPaymentList", userPaymentList);
            model.addAttribute("classActiveShipping", true);
            model.addAttribute("emptyPaymentList", userPaymentList.isEmpty());
            model.addAttribute("emptyShippingList", false);

            return "checkout";
        }
    }

    @GetMapping("/setPaymentMethod")
    public String setPaymentMethod(@RequestParam("userPaymentId") Long userPaymentId, Principal principal,
                                   Model model) {
        User user = userService.findByUsername(principal.getName());
        UserPayment userPayment = userPaymentService.findById(userPaymentId);
        UserBilling userBilling = userPayment.getUserBilling();

        if (userPayment.getUser().getId() != user.getId()) {
            return "badRequestPage";
        } else {
            Payment payment = new Payment();
            paymentService.setByUserPayment(userPayment, payment);

            BillingAddress billingAddress = new BillingAddress();
            billingAddressService.setByUserBilling(userBilling, billingAddress);

            ShippingAddress shippingAddress = new ShippingAddress();

            List<CartItem> cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());

            model.addAttribute("shippingAddress", shippingAddress);
            model.addAttribute("payment", payment);
            model.addAttribute("billingAddress", billingAddress);
            model.addAttribute("cartItemList", cartItemList);
            model.addAttribute("shoppingCart", user.getShoppingCart());

            List<String> stateList = new ArrayList<>(USConstants.listOfUSStatesCode);
            Collections.sort(stateList);
            model.addAttribute("stateList", stateList);

            List<UserShipping> userShippingList = user.getUserShippingList();
            List<UserPayment> userPaymentList = user.getUserPaymentList();

            model.addAttribute("userShippingList", userShippingList);
            model.addAttribute("userPaymentList", userPaymentList);
            model.addAttribute("classActivePayment", true);
            model.addAttribute("emptyPaymentList", false);
            model.addAttribute("emptyShippingList", userShippingList.isEmpty());

            return "checkout";
        }
    }
}
