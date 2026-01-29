package com.example.Tenpo.repository;

import com.example.Tenpo.model.entity.CallHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallHistoryRepository extends JpaRepository<CallHistory,Long> {

    Page<CallHistory> findAllByOrderByTimestampDesc(Pageable pageable);
}
