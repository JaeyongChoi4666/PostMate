package com.example.postmate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "TBL_SCHEDULE")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCH_NO")
    private Integer id;

    @Column(name = "SCH_TITLE", length = 45, nullable = false)
    private String title;

    @Column(name = "SCH_ST_DATE", nullable = false)
    private LocalDate startDate;

    @Column(name = "SCH_ED_DATE", nullable = false)
    private LocalDate endDate;

    @Column(name = "SCH_CATEGORY")
    private Integer category;

    @Column(name = "SCH_STATE")
    private Integer state;

    @Column(name = "PAYMENT")
    private Integer payment;

    @Column(name = "SCH_MEMO", length = 45)
    private String memo;
}
