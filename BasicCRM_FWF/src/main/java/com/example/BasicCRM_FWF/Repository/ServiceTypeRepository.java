package com.example.BasicCRM_FWF.Repository;

import com.example.BasicCRM_FWF.Model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Integer> {
    @Query(value = """
        SELECT * FROM temp_crm.service_type where service_name like :name LIMIT 1;
    """, nativeQuery = true)
    ServiceType findByName(@Param("name") String name);
}
