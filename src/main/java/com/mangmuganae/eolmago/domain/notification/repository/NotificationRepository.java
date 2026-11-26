package com.mangmuganae.eolmago.domain.notification.repository;

import com.mangmuganae.eolmago.domain.notification.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  @Query("select n from Notification n join User u on n.user = u and u.uuid = :uuid order by n.createdAt desc")
  List<Notification> findAllByUuid(@Param("uuid") String uuid);

}
