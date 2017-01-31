package it.flipb.theapp.plugin.config;

import it.flipb.theapp.plugin.YamlIoPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YamlIoPluginAutoConfiguration {
    @Bean
    YamlIoPlugin yamlIoPlugin() {
        return new YamlIoPlugin();
    }
}
