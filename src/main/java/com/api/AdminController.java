package com.api;

import com.api.data.Item;
import com.api.kafka.streams.MaximumPriceEachItemSoldKS;
import com.api.kafka.streams.SellsEachItemKS;
import com.api.kafka.streams.TotalSoldItemsKS;
import org.springframework.http.MediaType;
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
        return TotalSoldItemsKS.get();
    }

    // Purchases Topic
    @RequestMapping(value = "/units-sold-each-item", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Item> soldUnitsOfEachItemLast5Minutes(){
        return SellsEachItemKS.get();
    }

    // Purchases Topic
    @RequestMapping(value = "/maximum-price-of-each-item-sold", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Item> maximumPriceOfEachItemSoldLast5Minutes(){
        return MaximumPriceEachItemSoldKS.get();
    }

    // Shipments Topic
    @RequestMapping(value = "/average-number-shipments-each-item-sold", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Item> averageNumberShipmentsOfEachItemSold(){
        return null;
    }

    // Topic Purchases, Topic Shipments
    @RequestMapping(value = "/shop-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public String shopRevenueExpensesProfit(){
        return null;
    }

    // Item providing the highest profit
    @RequestMapping(value = "/highest-profit-item", produces = MediaType.APPLICATION_JSON_VALUE)
    public Item itemHighestProfitLast5Minutes(){
        return null;

    }

    // Item providing the highest profit
    @RequestMapping(value = "/item-average-sold-price", produces = MediaType.APPLICATION_JSON_VALUE)
    public int itemAverageSoldPrice(@NotNull @RequestParam String initialproduct,
                                    @NotNull @RequestParam String endProduct){
        return 10;
    }
}
