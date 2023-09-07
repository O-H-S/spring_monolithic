package com.ohs.monolithic.board;


import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter
public class Board {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;



    @OneToMany(fetch = FetchType.LAZY)
    private List<Post> posts;

    private LocalDateTime createDate;

    public void setTitle(String title) {
        this.title = title;

    }

    public void setCreateData(LocalDateTime now) {
        createDate = now;
    }

    public void setDescription(String desc) {
        description = desc;
    }
}