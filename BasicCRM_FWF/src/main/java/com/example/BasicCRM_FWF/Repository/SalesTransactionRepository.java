package com.example.BasicCRM_FWF.Repository;

import com.example.BasicCRM_FWF.Model.SalesTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SalesTransactionRepository extends JpaRepository<SalesTransaction,Integer> {
    List<SalesTransaction> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
}
