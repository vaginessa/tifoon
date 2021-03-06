package com.elixlogic.tifoon.domain.mapper;

import com.elixlogic.tifoon.domain.mapper.scanner.TargetPortScannerJobMapper;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {
    private ModelMapper modelMapper;

    public DtoMapper() {
        modelMapper = new ModelMapper();
        configure();
    }

    private void configure() {
        // register converters
        modelMapper.addConverter(new TargetPortScannerJobMapper());
    }

    public <S,T> T map(@NonNull S _source, @NonNull Class<T> _destination) {
       return modelMapper.map(_source, _destination);
    }
}
