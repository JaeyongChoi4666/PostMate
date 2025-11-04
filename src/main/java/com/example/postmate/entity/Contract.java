package com.example.postmate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "TBL_CONTRACT")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CON_NO")
    private Integer id;

    @Column(name = "CON_TITLE", length = 45, nullable = false)
    private String title;

    @Column(name = "CON_ST_DATE", nullable = false)
    private LocalDate startDate;

    @Column(name = "CON_ED_DATE", nullable = false)
    private LocalDate endDate;

    @Column(name = "CON_CATEGORY", length = 45)
    private String category;

    @Column(name = "PAYMENT")
    private Integer payment;

    @Column(name = "CON_CHANNEL", length = 45)
    private String channel;
}
