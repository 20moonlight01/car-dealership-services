package ru.glebova.presentation.mapping.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.glebova.application.contracts.assembly.models.AssemblyOrderDto;
import ru.glebova.domain.storageprocesses.AssemblyOrder;

@Mapper(
        componentModel = "spring",
        uses = {CarMapper.class})
public interface AssemblyOrderMapper {
    @Mapping(source = "car", target = "carDto")
    AssemblyOrderDto toDto(AssemblyOrder order);
}
