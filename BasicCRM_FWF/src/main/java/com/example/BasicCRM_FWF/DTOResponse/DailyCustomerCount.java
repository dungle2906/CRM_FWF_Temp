package com.example.BasicCRM_FWF.DTOResponse;

import java.sql.Date; // hoặc java.time.LocalDate nếu bạn ép lại trong query

public class DailyCustomerCount {
    private Date date;
    private Long count;

    public DailyCustomerCount(Date date, Long count) {
        this.date = date;
        this.count = count;
    }

    public Date getDate() {
        return date;
    }

    public Long getCount() {
        return count;
    }
}
