package com.example.BasicCRM_FWF.Repository;

import com.example.BasicCRM_FWF.Model.ServiceType;
import com.example.BasicCRM_FWF.Model.ServiceTypeTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceTypeTempRepository extends JpaRepository<ServiceTypeTemp,Long> {

    @Query(value = """
        SELECT * FROM service_type_temp
        WHERE LOWER(service_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        LIMIT 1
    """, nativeQuery = true)
    ServiceTypeTemp findByKeyword(@Param("keyword") String keyword);


    @Query(value = """
        SELECT * FROM service_type_temp where service_name like :name LIMIT 1;
    """, nativeQuery = true)
    ServiceTypeTemp findByName(@Param("name") String name);

    @Query(value = """
        SELECT * FROM service_type_temp WHERE service_name LIKE :startname AND service_name LIKE :endname
    """, nativeQuery = true)
    ServiceTypeTemp findByServiceName(@Param("startname") String startname, @Param("endname") String endname);

    @Query(value = """
        SELECT * FROM service_type_temp WHERE service_code LIKE 'QT 1.1'
    """, nativeQuery = true)
    ServiceTypeTemp findServiceTemp();

}
