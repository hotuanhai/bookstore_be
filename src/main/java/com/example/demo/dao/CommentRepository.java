package com.example.demo.dao;

import com.example.demo.dto.CommentDto;
import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("""
            SELECT new com.example.demo.dto.CommentDto(
                c.id,
                b.id,
                new com.example.demo.dto.UserSummaryDto(u.id, p.displayName, p.avatarUrl),
                c.content
            )
            FROM Comment c
            JOIN c.book b
            JOIN c.user u
            JOIN u.profile p
            WHERE b.id = :bookId
            """)
    List<CommentDto> findCommentsByBook(@Param("bookId") Long bookId);

    @Query("""
                SELECT new com.example.demo.dto.CommentDto(
                    c.id,
                    b.id,
                    new com.example.demo.dto.UserSummaryDto(u.id, p.displayName, p.avatarUrl),
                    c.content
                )
                FROM Comment c
                JOIN c.book b
                JOIN c.user u
                JOIN u.profile p
                WHERE u.id = :userId
            """)
    List<CommentDto> findCommentsByUser(@Param("userId") Long userId);
}
