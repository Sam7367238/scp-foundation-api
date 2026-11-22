package org.playground.scpapi.department;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    @Mapping(target = "mediaUuid", expression = "java(department.getMedia() != null ? department.getMedia().getUuid() : null)")
    DepartmentDto toDto(Department department);
}
