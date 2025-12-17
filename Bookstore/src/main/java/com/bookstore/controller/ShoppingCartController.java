package com.bookstore.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookstore.domain.Book;
import com.bookstore.domain.CartItem;
import com.bookstore.domain.ShoppingCart;
import com.bookstore.domain.User;
import com.bookstore.service.BookService;
import com.bookstore.service.CartItemService;
import com.bookstore.service.ShoppingCartService;
import com.bookstore.service.UserService;

@Controller
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    
    private final UserService userService;
    private final CartItemService cartItemService;
    private final BookService bookService;
    private final ShoppingCartService shoppingCartService;

    @Autowired
    public ShoppingCartController(UserService userService, CartItemService cartItemService,
                                  BookService bookService, ShoppingCartService shoppingCartService) {
        this.userService = userService;
        this.cartItemService = cartItemService;
        this.bookService = bookService;
        this.shoppingCartService = shoppingCartService;
    }
    
    @GetMapping("/cart")
    public String shoppingCart(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        ShoppingCart shoppingCart = user.getShoppingCart();
        
        List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
        
        shoppingCartService.updateShoppingCart(shoppingCart);
        
        model.addAttribute("cartItemList", cartItemList);
        model.addAttribute("shoppingCart", shoppingCart);
        
        return "shoppingCart";
    }

    @PostMapping("/addItem")
    public String addItem(
            @ModelAttribute("book") Book book,
            @ModelAttribute("qty") String qty,
            Model model, Principal principal
    ) {
        User user = userService.findByUsername(principal.getName());
        book = bookService.findOne(book.getId());

        if (Integer.parseInt(qty) > book.getInStockNumber()) {
            model.addAttribute("notEnoughStock", true);
            return "forward:/bookDetail?id=" + book.getId();
        }

        CartItem cartItem = cartItemService.addBookToCartItem(book, user, Integer.parseInt(qty));
        model.addAttribute("addBookSuccess", true);

        return "forward:/bookDetail?id=" + book.getId();
    }

    @PostMapping("/updateCartItem")
    public String updateShoppingCart(
            @RequestParam("id") Long cartItemId,
            @RequestParam("qty") int qty
    ) {
        CartItem cartItem = cartItemService.findById(cartItemId);
        if (cartItem != null) {
            cartItem.setQty(qty);
            cartItemService.updateCartItem(cartItem);
        }

        return "redirect:/shoppingCart/cart";
    }

    @PostMapping("/removeItem")
    public String removeItem(@RequestParam("id") Long id) {
        CartItem cartItem = cartItemService.findById(id); // CHANGED
        if (cartItem != null) { // CHANGED
            cartItemService.removeCartItem(cartItem);
        }

        return "redirect:/shoppingCart/cart";
    }

}