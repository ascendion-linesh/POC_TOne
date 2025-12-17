package com.bookstore.service.impl;

import com.bookstore.domain.CartItem;
import com.bookstore.domain.ShoppingCart;
import com.bookstore.repository.ShoppingCartRepository;
import com.bookstore.service.CartItemService;
import com.bookstore.service.ShoppingCartService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final CartItemService cartItemService;
    private final ShoppingCartRepository shoppingCartRepository;

    public ShoppingCartServiceImpl(@NonNull CartItemService cartItemService,
                                   @NonNull ShoppingCartRepository shoppingCartRepository) {
        this.cartItemService = cartItemService;
        this.shoppingCartRepository = shoppingCartRepository;
    }

    @Override
    @NonNull
    public ShoppingCart updateShoppingCart(@NonNull ShoppingCart shoppingCart) {
        BigDecimal cartTotal = BigDecimal.ZERO;

        List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);

        for (CartItem cartItem : cartItemList) {
            if (cartItem.getBook().getInStockNumber() > 0) {
                cartItemService.updateCartItem(cartItem);
                cartTotal = cartTotal.add(cartItem.getSubtotal());
            }
        }

        shoppingCart.setGrandTotal(cartTotal);

        shoppingCartRepository.save(shoppingCart);

        return shoppingCart;
    }

    @Override
    public void clearShoppingCart(@NonNull ShoppingCart shoppingCart) {
        List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);

        for (CartItem cartItem : cartItemList) {
            cartItem.setShoppingCart(null);
            cartItemService.save(cartItem);
        }

        shoppingCart.setGrandTotal(BigDecimal.ZERO);

        shoppingCartRepository.save(shoppingCart);
    }
}
