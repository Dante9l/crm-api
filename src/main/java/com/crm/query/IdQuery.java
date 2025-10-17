package com.crm.query;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author ASUS
 */
@Data
public class IdQuery {

    @NotNull(message = "id不能为空")
    private Integer id;
}