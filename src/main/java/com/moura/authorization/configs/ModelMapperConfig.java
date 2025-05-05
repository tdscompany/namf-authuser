package com.moura.authorization.configs;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper(
            List<Converter<?, ?>> converters,
            List<TypeMapConfigurer<?, ?>> configurers
    ) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        converters.forEach(modelMapper::addConverter);
        configurers.forEach(configurer -> configurer.configure(modelMapper));

        return modelMapper;
    }
}