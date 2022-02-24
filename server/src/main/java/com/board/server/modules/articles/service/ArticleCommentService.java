package com.board.server.modules.articles.service;

import com.board.server.common.PageUtils;
import com.board.server.common.PagingResponseDto;
import com.board.server.modules.articles.model.Article;
import com.board.server.modules.articles.model.ArticleComment;
import com.board.server.modules.articles.model.ArticleCommentRepository;
import com.board.server.modules.articles.model.ArticleCommentRequestDto;
import com.board.server.modules.articles.model.ArticleCommentResponseDto;
import com.board.server.modules.articles.model.ArticleRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ArticleCommentService {

    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleRepository articleRepository;
    private final PageUtils pageUtils;
    private final ModelMapper modelMapper;

    private static final int COMMENT_PAGE_SIZE = 10;
    private static final int COMMENT_SCALE_SIZE = 10;
    private final String INVALID_ARTICLE_MESSAGE = "존재하지 않는 게시글입니다.";
    private final String INVALID_ARTICLE_COMMENT_MESSAGE = "존재하지 않는 댓글입니다.";
    private final String INVALID_PAGE_MESSAGE = "존재하지 않는 페이지입니다.";

    @Transactional
    public ArticleCommentResponseDto addComment(
        Long articleId,
        ArticleCommentRequestDto.Post articleCommentRequestDto
    ) {
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new IllegalArgumentException(INVALID_ARTICLE_MESSAGE));

        ArticleComment articleComment = articleCommentRepository.save(
            articleCommentRequestDto.toEntity(article));

        return modelMapper.map(articleComment, ArticleCommentResponseDto.class);
    }

    public PagingResponseDto getComments(Long articleId, Integer page) {
        if (pageUtils.isInvalidPageValue(page)) {
            throw new IllegalArgumentException(INVALID_PAGE_MESSAGE);
        }

        Page<ArticleComment> articleCommentPage = articleCommentRepository.findAllByArticleId(
            articleId,
            pageUtils.getPageable(page, COMMENT_PAGE_SIZE, Direction.DESC, "createdAt")
        );

        return pageUtils.getCommonPagingResponseDto(articleCommentPage,
            getArticleCommentResponseDto(articleCommentPage), COMMENT_SCALE_SIZE);
    }

    private List<ArticleCommentResponseDto> getArticleCommentResponseDto(
        Page<ArticleComment> articleCommentPage) {

        return articleCommentPage.stream()
            .map(articleComment -> modelMapper.map(articleComment, ArticleCommentResponseDto.class))
            .collect(Collectors.toList());
    }

    @Transactional
    public ArticleCommentResponseDto updateComment(
        Long articleId,
        Long articleCommentId,
        ArticleCommentRequestDto.Put articleCommentRequestDto
    ) {
        ArticleComment articleComment = articleCommentRepository
            .findByArticleIdAndId(articleId, articleCommentId)
            .orElseThrow(() -> new IllegalArgumentException(INVALID_ARTICLE_COMMENT_MESSAGE));

        articleComment.update(articleCommentRequestDto);

        return modelMapper.map(articleComment, ArticleCommentResponseDto.class);
    }

    public ArticleCommentResponseDto deleteComment(Long articleId, Long articleCommentId) {
        ArticleComment articleComment = articleCommentRepository
            .findByArticleIdAndId(articleId, articleCommentId)
            .orElseThrow(() -> new IllegalArgumentException(INVALID_ARTICLE_COMMENT_MESSAGE));

        articleCommentRepository.deleteById(articleCommentId);

        return modelMapper.map(articleComment, ArticleCommentResponseDto.class);
    }
}
