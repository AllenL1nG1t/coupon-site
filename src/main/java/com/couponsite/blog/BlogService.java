package com.couponsite.blog;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BlogService {

    private final BlogPostRepository blogPostRepository;

    public BlogService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    public List<BlogPostDto> listPublished() {
        return blogPostRepository.findByPublishedTrueOrderByCreatedAtDesc().stream().map(this::toDto).toList();
    }

    public List<BlogPostDto> listAll() {
        return blogPostRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toDto).toList();
    }

    public long count() {
        return blogPostRepository.count();
    }

    @Transactional
    public BlogPostDto upsert(BlogPostUpsertRequest request) {
        BlogPost post = request.id() == null
            ? new BlogPost()
            : blogPostRepository.findById(request.id()).orElseThrow(() -> new IllegalArgumentException("Blog not found"));

        post.setTitle(nonBlankOrDefault(request.title(), "Untitled"));
        post.setSummary(nonBlankOrDefault(request.summary(), ""));
        post.setContent(nonBlankOrDefault(request.content(), ""));
        post.setCoverImageUrl(nonBlankOrDefault(request.coverImageUrl(), "/logos/default.svg"));
        post.setPublished(request.published());
        return toDto(blogPostRepository.save(post));
    }

    @Transactional
    public void delete(Long id) {
        blogPostRepository.deleteById(id);
    }

    private BlogPostDto toDto(BlogPost post) {
        return new BlogPostDto(
            post.getId(),
            post.getTitle(),
            post.getSummary(),
            post.getContent(),
            post.getCoverImageUrl(),
            post.isPublished(),
            post.getCreatedAt(),
            post.getUpdatedAt()
        );
    }

    private String nonBlankOrDefault(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }
}
