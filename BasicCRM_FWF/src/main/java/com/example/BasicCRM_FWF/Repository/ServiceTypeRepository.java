package com.example.BasicCRM_FWF.Repository;

import com.example.BasicCRM_FWF.Model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Integer> {
    @Query(value = """
        SELECT * FROM service_type where service_name like :name LIMIT 1;
    """, nativeQuery = true)
    ServiceType findByName(@Param("name") String name);

    @Query(value = """
        SELECT * FROM service_type WHERE service_name LIKE :startname AND service_name LIKE :endname
    """, nativeQuery = true)
    ServiceType findByServiceName(@Param("startname") String startname, @Param("endname") String endname);

    @Query(value = """
        SELECT * FROM service_type WHERE service_code LIKE 'QT 1.1'
    """, nativeQuery = true)
    ServiceType findServiceTemp();
}
