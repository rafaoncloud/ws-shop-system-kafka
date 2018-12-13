package com.api;


import com.api.kafka.streams.MaximumPriceEachItemSoldKS;
import com.api.kafka.streams.SellsEachItemKS;
import com.api.kafka.streams.TotalSoldItemsKS;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    private final static int port = 9998;
    private final static String host="http://localhost/";

    public static void main(String[] args) {
        try {
            TotalSoldItemsKS.startKafkaStream();
            SellsEachItemKS.startKafkaStream();
            MaximumPriceEachItemSoldKS.startKafkaStream();

            //Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SpringApplication.run(Application.class, args);
    }
}
