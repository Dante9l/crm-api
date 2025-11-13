package com.crm.query;

import com.crm.common.model.Query;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class OperLogQuery extends Query {

    @Min(1)
    private Integer page = 1;

    @Min(1)
    private Integer limit = 10;
    @Schema(description = "操作人")
    private String operName;

    @Schema(description = "业务操作时间段")
    private List<Timestamp> operTime;
    @Schema(description = "接口url")
    private String operUrl;
}
