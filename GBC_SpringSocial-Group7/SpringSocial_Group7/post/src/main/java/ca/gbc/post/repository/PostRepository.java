package ca.gbc.post.repository;

import ca.gbc.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTitle(String s);

    List<Post> findByAuthor(Long author);
}
