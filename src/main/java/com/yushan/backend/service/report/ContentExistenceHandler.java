package com.yushan.backend.service.report;

import com.yushan.backend.entity.Comment;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.enums.ReportContentType;
import com.yushan.backend.exception.ResourceNotFoundException;
import com.yushan.backend.service.CommentService;
import com.yushan.backend.service.NovelService;
import org.springframework.stereotype.Component;

@Component
public class ContentExistenceHandler extends ValidationHandler {

    private final NovelService novelService;
    private final CommentService commentService;

    public ContentExistenceHandler(NovelService novelService, CommentService commentService) {
        this.novelService = novelService;
        this.commentService = commentService;
    }

    @Override
    protected void doHandle(ReportContext context) {
        ReportContentType contentType = context.getContentType();
        if (contentType == ReportContentType.NOVEL) {
            Novel novel = novelService.getNovelEntity(context.getContentId());
            context.setNovel(novel);
            return;
        }

        if (contentType == ReportContentType.COMMENT) {
            Comment comment = commentService.getCommentEntity(context.getContentId());
            context.setComment(comment);
            return;
        }

        throw new ResourceNotFoundException("Unsupported content type");
    }
}


