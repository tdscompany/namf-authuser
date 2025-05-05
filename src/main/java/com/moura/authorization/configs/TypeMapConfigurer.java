package com.moura.authorization.configs;

import org.modelmapper.ModelMapper;

public interface TypeMapConfigurer<S, D> {
    void configure(ModelMapper modelMapper);
}