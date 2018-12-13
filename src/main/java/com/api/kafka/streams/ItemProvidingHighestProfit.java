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
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ItemProvidingHighestProfit {

    public static final String INPUT_TOPIC = KafkaShop.PURCHASES_TOPIC;
    public static final String TABLE_NAME = "table-each-item-sold";

    public static boolean isStarted;
    public static KafkaStreams streams;

    public static void startKafkaStream() throws Exception {

        if (isStarted)
            throw new Exception("An Instance is already Running");

        java.util.Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "shop-system-each-item");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> purchases = builder.stream(INPUT_TOPIC);

        KTable<String, Long> purchasesTable = purchases.groupByKey()
                .count(Materialized.as(TABLE_NAME));

        streams = new KafkaStreams(builder.build(), props);
        streams.start();


        isStarted = true;
    }

    public static Item get(){
        ReadOnlyKeyValueStore<String, Long> keyValueStore = streams.store(TABLE_NAME, QueryableStoreTypes.keyValueStore());

        Item highestProfitProduct = null;

        List<Item> products = new ArrayList<>();
        KeyValueIterator<String, Long> range = keyValueStore.all();

        while (range.hasNext()) {
            KeyValue<String, Long> next = range.next();
            Item product = new Item(0,next.key,0,Math.toIntExact(next.value));

            System.out.println("count for " + product.getName() + ": " + product.getAmount());
            products.add(product);
        }
        range.close();
        return highestProfitProduct;
    }
}
