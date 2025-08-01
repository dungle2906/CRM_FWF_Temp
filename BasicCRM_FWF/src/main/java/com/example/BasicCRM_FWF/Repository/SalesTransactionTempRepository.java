package com.example.BasicCRM_FWF.Repository;

import com.example.BasicCRM_FWF.Model.SalesTransactionTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesTransactionTempRepository extends JpaRepository<SalesTransactionTemp, Integer> {
}
