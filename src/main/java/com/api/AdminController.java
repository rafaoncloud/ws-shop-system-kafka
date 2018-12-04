package com.api;

import com.api.data.Item;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminController {

    // Purchases Topic
    @RequestMapping(value = "/total-sold-items", produces = MediaType.APPLICATION_JSON_VALUE)
    public Integer totalSoldItems() {
        return 10;
    }

    // Purchases Topic
    @RequestMapping(value = "/units-sold-each-item", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Item> soldUnitsOfEachItem(@RequestParam Integer minutes){
        return null;
    }

    // Purchases Topic
    @RequestMapping(value = "/maximum-price-of-each-item-sold", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Item> maximumPriceOfEachItemSold(@RequestParam Integer minutes){
        return null;
    }


}
