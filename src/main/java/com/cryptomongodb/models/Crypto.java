package com.cryptomongodb.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Crypto {

    @Id
    String ID;
    private double lprice;
    private String curr1;
    private String curr2;
    private LocalDateTime createdAt;

    public Crypto(double lprice, String curr1, String curr2, LocalDateTime createdAt) {
        this.lprice = lprice;
        this.curr1 = curr1;
        this.curr2 = curr2;
        this.createdAt = createdAt;
    }
}
