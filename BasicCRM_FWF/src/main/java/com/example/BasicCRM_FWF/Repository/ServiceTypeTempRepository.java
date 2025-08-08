package com.example.BasicCRM_FWF.Repository;

import com.example.BasicCRM_FWF.Model.ServiceType;
import com.example.BasicCRM_FWF.Model.ServiceTypeTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceTypeTempRepository extends JpaRepository<ServiceTypeTemp,Long> {

}
