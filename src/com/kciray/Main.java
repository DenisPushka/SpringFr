package com.kciray;


import org.springframework.beans.factory.BeanFactory;

public class Main {
    public static void main(String[] args) {
        var beanFactory = new BeanFactory();
        beanFactory.instantiate("com.kciray");
        var productService = (ProductService) beanFactory.getBean("productService");
        beanFactory.populateProperties();
        System.out.println(productService.getPromotionsService());
    }
}
