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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class MaximumPriceEachItemSoldKS {

    public static final String INPUT_TOPIC = KafkaShop.MY_REPLY_TOPIC;
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
        KStream<String, String> purchases1 = builder.stream(INPUT_TOPIC + "1");
        KStream<String, String> purchases2 = builder.stream(INPUT_TOPIC + "1");
        KStream<String, String> purchases3 = builder.stream(INPUT_TOPIC + "1");

        KTable<String, String> purchasesTable1 = purchases1.outerJoin(purchases2, (left, right) -> {
                    Item purchase1 = KafkaShop.deserializeItemFromJSON(left);
                    Item purchase2 = KafkaShop.deserializeItemFromJSON(right);

                    if (purchase1.getName().equalsIgnoreCase(purchase2.getName())) {
                        if ((purchase2.getPrice() * purchase2.getAmount()) > (purchase1.getPrice() * purchase2.getAmount())) {
                            return KafkaShop.serializeItemToJSON(purchase2);
                        }
                    }
                    return KafkaShop.serializeItemToJSON(purchase1);
                }
                , null).outerJoin(purchases3, (left, right) -> {
                    Item purchase1 = KafkaShop.deserializeItemFromJSON(left);
                    Item purchase2 = KafkaShop.deserializeItemFromJSON(right);

                    if (purchase1.getName().equalsIgnoreCase(purchase2.getName())) {
                        if ((purchase2.getPrice() * purchase2.getAmount()) > (purchase1.getPrice() * purchase2.getAmount())) {
                            return KafkaShop.serializeItemToJSON(purchase2);
                        }
                    }
                    return KafkaShop.serializeItemToJSON(purchase1);
                }
                , null).groupByKey()
                .reduce((value1, value2) -> {
                    Item purchase1 = KafkaShop.deserializeItemFromJSON(value1);
                    Item purchase2 = KafkaShop.deserializeItemFromJSON(value2);

                    if (purchase1.getName().equalsIgnoreCase(purchase2.getName())) {
                        if ((purchase2.getPrice() * purchase2.getAmount()) > (purchase1.getPrice() * purchase2.getAmount())) {
                            return KafkaShop.serializeItemToJSON(purchase2);
                        }
                    }
                    return KafkaShop.serializeItemToJSON(purchase1);
                }, Materialized.as(TABLE_NAME));

        streams = new KafkaStreams(builder.build(), props);
        streams.start();

        isStarted = true;
    }

    public static List<Item> get() {
        ReadOnlyKeyValueStore<String, String> keyValueStore = streams.store(TABLE_NAME, QueryableStoreTypes.keyValueStore());

        List<Item> products = new ArrayList<>();
        KeyValueIterator<String, String> range = keyValueStore.all();

        while (range.hasNext()) {
            KeyValue<String, String> next = range.next();
            Item product = KafkaShop.deserializeItemFromJSON(next.value);

            System.out.println("count for " + next.key + ": " + next.value);
            products.add(product);
        }
        range.close();
        return products;
    }
}
