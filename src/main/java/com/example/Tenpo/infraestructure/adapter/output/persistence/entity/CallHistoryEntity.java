package com.example.Tenpo.infraestructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "call_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String endpoint;

    @Column(columnDefinition = "TEXT")
    private String parameters;

    @Column(columnDefinition = "TEXT")
    private String response;

    @Column(columnDefinition = "TEXT")
    private String error;
}
