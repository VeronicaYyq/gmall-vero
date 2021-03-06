package com.atguigu.gmall.cart.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.cart.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("cart")
@RestController
public class CartController {
    @Autowired
    private CartService cartService;
    @PostMapping
    public Resp<Object> insertToCart(@RequestBody Cart cart){
        cartService.insertToCart(cart);

        return Resp.ok(null);
    }
    @GetMapping
    public Resp<List<Cart>> queryCarts() {

        List<Cart> carts = this.cartService.queryCarts();

        return Resp.ok(carts);
    }
    @PostMapping("update")
    public Resp<Object> updateCart(@RequestBody Cart cart) {

        this.cartService.updateCart(cart);

        return Resp.ok(null);
    }
    @PostMapping("{skuId}")
    public Resp<Object> deleteCart(@PathVariable("skuId")Long skuId){

        this.cartService.deleteCart(skuId);

        return Resp.ok(null);
    }
}
