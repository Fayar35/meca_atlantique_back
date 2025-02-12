package meca.atlantique.spring.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import meca.atlantique.fanuc.FanucApi.ODBST_15;
import meca.atlantique.fanuc.FanucApi.ODBST_OTHER;
import meca.atlantique.spring.Data.ODBSTDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ODBSTMapper {
    
    ODBSTMapper INSTANCE = Mappers.getMapper(ODBSTMapper.class);

    ODBSTDto ODBST_15ToODBSTDto(ODBST_15 odbst);
    ODBSTDto ODBST_OTHERToODBSTDto(ODBST_OTHER odbst);
}
