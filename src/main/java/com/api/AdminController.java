package com.api;

import com.api.data.Item;
import com.api.kafka.consumer.TotalSoldItemsConsumer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
public class AdminController {

    // Purchases Topic
    @RequestMapping(value = "/total-sold-items", produces = MediaType.APPLICATION_JSON_VALUE)
    public Integer totalSoldItems() {
        return TotalSoldItemsConsumer.read();
    }

    // Purchases Topic
    @RequestMapping(value = "/units-sold-each-item", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Item> soldUnitsOfEachItem(@NotNull @RequestParam Integer minutes){
        return null;
    }

    // Purchases Topic
    @RequestMapping(value = "/maximum-price-of-each-item-sold", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Item> maximumPriceOfEachItemSold(@NotNull @RequestParam Integer minutes){
        return null;
    }

    // Shipments Topic
    @RequestMapping(value = "/average-number-shipments-each-item-sold", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Item> averageNumberShipmentsOfEachItemSold(){
        return null;
    }

    // Topic Purchases, Topic Shipments
    @RequestMapping(value = "/shop-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Item> shopRevenueExpensesProfit(){
        return null;
    }

    // Item providing the highest profit
    @RequestMapping(value = "/highest-profit-item", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Item> averageNumberShipmentsOfEachItemSold(@NotNull @RequestParam Integer minutes){
        return null;

    }

    // Item providing the highest profit
    @RequestMapping(value = "/item-average-sold-price", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Item> averageNumberShipmentsOfEachItemSold(@NotNull @RequestParam String item){
        return null;
    }
}
