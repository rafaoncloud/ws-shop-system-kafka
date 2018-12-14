package com.api.kafka.streams;

import com.api.KafkaShop;
import com.api.data.Item;
import org.apache.kafka.common.protocol.types.Field;
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

public class RevenueLast5MinKS {

    public static final String INPUT_TOPIC = KafkaShop.MY_REPLY_STATISTICS_TOPIC;
    public static final String TABLE_NAME = "table-revenue-last-5-min";

    public static boolean isStarted;
    public static KafkaStreams streams;

    public static void startKafkaStream() throws Exception {

        if (isStarted)
            throw new Exception("An Instance is already Running");

        java.util.Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "revenue-table-last-5-min");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> purchases = builder.stream(INPUT_TOPIC);

        KTable<String, String> purchasesTable = purchases.groupByKey()
                .reduce(new Reducer<String>() {
                    /* adder */
                    @Override
                    public String apply(String aggValue, String newValue) {

                        Item aggItem = KafkaShop.deserializeItemFromJSON(aggValue);
                        Item newItem = KafkaShop.deserializeItemFromJSON(newValue);

                        int revenue = aggItem.getPrice() * aggItem.getAmount();
                        revenue += newItem.getPrice() * newItem.getAmount();
                        return KafkaShop.serializeItemToJSON(new Item(0,aggItem.getName(),revenue,1));
                    }
                }, Materialized.as(TABLE_NAME));
        streams = new KafkaStreams(builder.build(), props);
        streams.start();

        isStarted = true;
    }

    public static List<Item> get() {
        ReadOnlyKeyValueStore<String, String> keyValueStore =
                streams.store(TABLE_NAME, QueryableStoreTypes.keyValueStore());

        List<Item> products = new ArrayList<>();
        KeyValueIterator<String, String> range = keyValueStore.all();

        while (range.hasNext()) {
            KeyValue<String, String> next = range.next();
            Item product = KafkaShop.deserializeItemFromJSON(next.value);

            System.out.println("count for " + product.getName() + ": " + product.getAmount());
            products.add(product);
        }
        range.close();

        return products;
    }
}
