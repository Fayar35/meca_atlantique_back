package meca.atlantique.spring.Services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import meca.atlantique.spring.Data.Demand;
import meca.atlantique.spring.Repositories.DemandRepository;

@Service
@AllArgsConstructor
public class DemandService {
    
    private final DemandRepository repository;
    
    @Transactional
    public List<Demand> getAll() {
        return repository.findAll();
    }

    @Transactional
    public Demand getById(Long id) {
        return repository.getReferenceById(id);
    }

    @Transactional
    public Demand add(Demand demand) {
        return repository.save(demand);
    }

    @Transactional
    public boolean has(Long id) {
        return repository.existsById(id);
    }

    @Transactional
    public void removeById(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void save(Demand demand) {
        repository.save(demand);
    }
}
