package com.api.kafka.streams;

import com.api.KafkaShop;
import com.api.data.Item;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class MaximumPriceEachItemSoldKS {

    public static final String INPUT_TOPIC = KafkaShop.MY_REPLY_STATISTICS_TOPIC;
    public static final String TABLE_NAME = "table-maximum-price";

    public static boolean isStarted;
    public static KafkaStreams streams;

    public static void startKafkaStream() throws Exception {

        if (isStarted)
            throw new Exception("An Instance is already Running");

        java.util.Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "shop-system-maximum-price");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> purchases = builder.stream(INPUT_TOPIC);

        //KTable<String, String> purchasesTable =
        purchases.groupByKey()
                .windowedBy(TimeWindows.of(TimeUnit.MINUTES.toMillis(5 * 60000)))
                .reduce(new Reducer<String>() {
                    /* adder */
                    @Override
                    public String apply(String aggValue, String newValue) {

                        Item aggItem = KafkaShop.deserializeItemFromJSON(aggValue);
                        Item newItem = KafkaShop.deserializeItemFromJSON(newValue);

                        //if (purchase1.getName().equalsIgnoreCase(purchase2.getName())) {
                        if ((aggItem.getPrice() * aggItem.getAmount()) > (newItem.getPrice() * newItem.getAmount())) {
                            return KafkaShop.serializeItemToJSON(aggItem);
                        } else {
                            return KafkaShop.serializeItemToJSON(newItem);
                        }
                        //}
                    }
                }, Materialized.as(TABLE_NAME));

        streams = new KafkaStreams(builder.build(), props);
        streams.start();

        isStarted = true;
    }

    public static List<Item> get() {
        // WINDOWED STORE
        ReadOnlyWindowStore<String, String> keyValueStore =
                streams.store(TABLE_NAME, QueryableStoreTypes.<String, String>windowStore());

        List<Item> products = new ArrayList<>();
        KeyValueIterator<Windowed<String>, String> range = keyValueStore.all();

        while (range.hasNext()) {
            KeyValue<Windowed<String>, String> next = range.next();
            Item product = KafkaShop.deserializeItemFromJSON(next.value);

            System.out.println("count for " + product.getName() + ": " + product.getAmount());
            products.add(product);
        }
        range.close();
        return products;
    }
}
