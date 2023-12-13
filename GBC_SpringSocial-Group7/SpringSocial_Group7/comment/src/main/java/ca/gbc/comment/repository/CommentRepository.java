package ca.gbc.comment.repository;

import ca.gbc.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByContent(String s);

    @Query("SELECT c FROM Comment c WHERE c.postId = :postId")
    List<Comment> findCommentsByPostId(@Param("postId") Long postId);
}
