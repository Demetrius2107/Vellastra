package com.demetrius.vellastra.comment.application;

import com.demetrius.vellastra.comment.domain.comment.entity.Comment;
import com.demetrius.vellastra.comment.domain.comment.repository.CommentRepository;
import com.demetrius.vellastra.comment.interfaces.dto.CreateCommentRequest;
import com.demetrius.vellastra.common.exception.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CommentApplicationService}
 */
@ExtendWith(MockitoExtension.class)
class CommentApplicationServiceTest {

    @Mock
    private CommentRepository commentRepository;

    private CommentApplicationService commentApplicationService;

    @BeforeEach
    void setUp() {
        commentApplicationService = new CommentApplicationService(commentRepository);
    }

    @Test
    @DisplayName("create 应保存评论并返回ID")
    void create_shouldSave() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setArticleId(1L);
        request.setContent("好文章！");

        doAnswer(invocation -> {
            Comment c = invocation.getArgument(0);
            c.setId(1L);
            return null;
        }).when(commentRepository).save(any());

        Long id = commentApplicationService.create(request, 1L);
        assertEquals(1L, id);
    }

    @Test
    @DisplayName("delete 不存在时抛出异常")
    void delete_notFound_shouldThrow() {
        when(commentRepository.findById(99L)).thenReturn(null);
        assertThrows(BizException.class, () -> commentApplicationService.delete(99L));
    }
}