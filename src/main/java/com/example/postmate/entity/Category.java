package com.example.postmate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_category")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATE_NO")
    private Integer id;

    @Column(name = "CATE_NAME", length = 45, nullable = false)
    private String name;
}
