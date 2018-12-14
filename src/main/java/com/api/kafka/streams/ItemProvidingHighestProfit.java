package com.api.kafka.streams;

import com.api.KafkaShop;
import com.api.data.Item;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ItemProvidingHighestProfit {

    public static Item get() {
        List<Item> expenses = ExpensesLast5MinKS.get();
        List<Item> revenue = RevenueLast5MinKS.get();

        Item highestProfitProduct = null;
        int highestProfit = 0;
        for (Item productExpenses : expenses) {

            for (Item productRevenue : revenue) {
                if (productExpenses.getName().equalsIgnoreCase(productRevenue.getName())) {

                    int profit = productRevenue.getPrice() - productExpenses.getPrice();

                    if (profit >= highestProfit) {
                        highestProfit = profit;
                        highestProfitProduct = productRevenue;
                        highestProfitProduct.setPrice(highestProfit);
                    }
                }
            }
        }
        return highestProfitProduct;
    }
}
