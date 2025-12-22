package org.example.storeapplication.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class ItemDTO {

    private UUID id;
    private Integer version;

    private String name;
    private Integer available;

    private BigDecimal price;

    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}
