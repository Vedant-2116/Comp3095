package ca.gbc.post.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name="t_post")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private Long author;
}
