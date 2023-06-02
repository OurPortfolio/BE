package com.sparta.ourportfolio.common.config;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public Trie<String, List<Long>> trie() {
        return new PatriciaTrie<>();
    }

}
