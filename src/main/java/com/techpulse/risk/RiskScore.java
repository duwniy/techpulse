package com.techpulse.risk;

import com.techpulse.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "risk_scores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskScore {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer score = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel level = RiskLevel.GREEN;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> signals;

    @CreationTimestamp
    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;

    public enum RiskLevel {
        GREEN, AMBER, RED
    }
}
