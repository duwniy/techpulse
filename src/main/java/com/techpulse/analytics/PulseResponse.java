package com.techpulse.analytics;

import com.techpulse.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pulse_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PulseResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "workload_rating")
    private Integer workloadRating;

    @Column(name = "open_text")
    private String openText;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
