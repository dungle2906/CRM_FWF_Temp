package com.example.BasicCRM_FWF.Repository;

import com.example.BasicCRM_FWF.Model.AppUsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUsageRecordRepository extends JpaRepository<AppUsageRecord, Long> {
}
