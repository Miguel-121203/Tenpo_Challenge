package com.example.Tenpo.infraestructure.adapter.output.persistence.repository;

import com.example.Tenpo.infraestructure.adapter.output.persistence.entity.CallHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCallHistoryRepository extends JpaRepository<CallHistory,Long> {

    Page<CallHistory> findAllByOrderByTimestampDesc(Pageable pageable);
}
