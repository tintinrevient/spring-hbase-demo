package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

@Configuration
public class HbaseConfig {

    @Bean
    public HbaseTemplate hbaseTemplate() {
        org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "localhost:2181");
        return new HbaseTemplate(configuration);
    }

}
