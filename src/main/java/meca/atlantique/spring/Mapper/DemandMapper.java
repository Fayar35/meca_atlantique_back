package meca.atlantique.spring.Mapper;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import meca.atlantique.spring.Data.Demand;
import meca.atlantique.spring.Data.DemandDto;
import meca.atlantique.spring.Data.DemandStatus;
import meca.atlantique.spring.Data.InterventionType;
import meca.atlantique.spring.Data.Priority;

@Component
@AllArgsConstructor
public class DemandMapper {

    public DemandDto toDTO(Demand demand) {
        DemandDto dto = new DemandDto();
        dto.setId(demand.getId());
        dto.setDateCreated(demand.getDateCreated());
        dto.setMachineName(demand.getMachineName());
        dto.setInterventionType(demand.getInterventionType().toString());
        dto.setDatePlanned(demand.getDatePlanned());
        dto.setDescription(demand.getDescription());
        dto.setStatus(demand.getStatus().toString());
        dto.setPriority(demand.getPriority().toString());
        return dto;
    }

    public Demand toEntity(DemandDto dto) {
        Demand demand = new Demand();
        if (dto.getId() != null) {
            demand.setId(dto.getId());
        }
        demand.setDateCreated(dto.getDateCreated());
        demand.setMachineName(dto.getMachineName());
        demand.setDatePlanned(dto.getDatePlanned());
        demand.setDescription(dto.getDescription());
        
        if (dto.getStatus() != null) {
            switch (dto.getStatus()) {
                case "WAITING": {
                    demand.setStatus(DemandStatus.WAITING);
                    break;
                }
                case "ONGOING": {
                    demand.setStatus(DemandStatus.ONGOING);
                    break;
                }
                case "FINISHED": {
                    demand.setStatus(DemandStatus.FINISHED);
                    break;
                }
            } 
        }

        if (dto.getInterventionType() != null) {
            switch (dto.getInterventionType()) {
                case "Correctif": {
                    demand.setInterventionType(InterventionType.CORRECTIF);
                    break;
                }
                case "Préventif": {
                    demand.setInterventionType(InterventionType.PREVENTIF);
                    break;
                }
                case "Amélioration": {
                    demand.setInterventionType(InterventionType.AMELIORATION);
                    break;
                }
            } 
        }
            
        if (dto.getPriority() != null) {
            switch (dto.getPriority()) {
                case "PETITE": {
                    demand.setPriority(Priority.PETITE);
                    break;
                }
                case "NORMALE": {
                    demand.setPriority(Priority.NORMALE);
                    break;
                }
                case "URGENT": {
                    demand.setPriority(Priority.URGENT);
                    break;
                }
            } 
        }

        return demand;
    }
}