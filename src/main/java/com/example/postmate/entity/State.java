package com.example.postmate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_STATE")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STATE_NO")
    private Integer id;

    @Column(name = "STATE_NAME", length = 45, nullable = false)
    private String name;

    @Column(name = "STATE_COLOR", length = 45, nullable = false)
    private String color;

    @JoinColumn(name = "UPPER_CATE_NO", referencedColumnName = "CATE_NO")
    private Integer upperCateNo;
}
