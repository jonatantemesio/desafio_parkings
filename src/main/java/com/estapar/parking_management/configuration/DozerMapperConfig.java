package com.estapar.parking_management.configuration;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DozerMapperConfig {
    @Bean
    public Mapper dozerBeanMapper() {
        return DozerBeanMapperBuilder.buildDefault();
    }
}
