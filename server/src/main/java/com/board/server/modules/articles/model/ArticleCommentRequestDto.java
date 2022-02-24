package com.board.server.modules.articles.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ArticleCommentRequestDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post {
        private String content;

        private String createdBy;

        public ArticleComment toEntity(Article article) {
            return ArticleComment.builder()
                .article(article)
                .content(this.content)
                .createdBy(this.createdBy)
                .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Put {
        private String content;

        private String modifiedBy;
    }
}
