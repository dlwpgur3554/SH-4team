package com.port4dev.controller; // (수정)

import com.port4dev.entity.Portfolio; // (수정)
import com.port4dev.repository.PortfolioRepository; // (수정)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@CrossOrigin(origins = "*") // CORS 허용 (프론트엔드와 포트 다를 때)
public class PortfolioController {

    @Autowired
    private PortfolioRepository repo;

    @GetMapping
    public List<Portfolio> getAll() {
        return repo.findAll();
    }

    @PostMapping
    public Portfolio create(@RequestBody Portfolio p) {
        return repo.save(p);
    }

    @GetMapping("/{id}")
    public Portfolio getOne(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Portfolio update(@PathVariable Long id, @RequestBody Portfolio p) {
        p.setId(id);
        return repo.save(p);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}