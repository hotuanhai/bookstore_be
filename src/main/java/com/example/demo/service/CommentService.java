package com.example.demo.service;

import com.example.demo.dao.BookRepository;
import com.example.demo.dao.CommentRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.dto.CommentDto;
import com.example.demo.dto.UserSummaryDto;
import com.example.demo.entity.Comment;
import com.example.demo.entity.book.Book;
import com.example.demo.entity.user.User;
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.exception.CommentNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.request.CommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentDto addComment(CommentRequest request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found with id " + request.getBookId()));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Comment comment = Comment.builder()
                .book(book)
                .user(user)
                .content(request.getContent())
                .build();

        Comment saved = commentRepository.save(comment);
        return toDto(saved);
    }

    @Transactional
    public CommentDto updateComment(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));
        comment.setContent(newContent);

        Comment saved = commentRepository.save(comment);
        return toDto(saved);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException("Comment not found");
        }
        commentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByUser(Long userId) {
        return commentRepository.findCommentsByUser(userId);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByBook(Long bookId) {
        return commentRepository.findCommentsByBook(bookId);
    }

    //utils
    private CommentDto toDto(Comment comment) {
        User user = comment.getUser();

        UserSummaryDto userSummary = UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getProfile().getDisplayName())
                .avatar(user.getProfile().getAvatarUrl())
                .build();

        return CommentDto.builder()
                .id(comment.getId())
                .bookId(comment.getBook().getId())
                .user(userSummary)
                .content(comment.getContent())
                .build();
    }
}
