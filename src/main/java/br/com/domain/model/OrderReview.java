package br.com.domain.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_reviews")
public class OrderReview extends PanacheEntityBase {

    @Id
    @Column(name = "review_id")
    public String reviewId;

    @Column(name = "order_id")
    public String orderId;

    @Column(name = "review_score")
    public Integer reviewScore;

    @Column(name = "review_comment_title")
    public String reviewCommentTitle;

    @Column(name = "review_comment_message", columnDefinition = "TEXT")
    public String reviewCommentMessage;

    @Column(name = "review_creation_date")
    public LocalDateTime reviewCreationDate;

    @Column(name = "review_answer_timestamp")
    public LocalDateTime reviewAnswerTimestamp;
}
