package org.example.service;


import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import org.example.entity.UserAudit;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerService {
    private final ObjectMapper objectMapper;

    @Autowired
    private CqlSession session;

    @Autowired
    public KafkaConsumerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "audit", groupId = "audit")
    public void listenAuditMessages(String userAuditJson) {
        log.info("Получено сообщение от кафки: {}", userAuditJson);
        if (userAuditJson == null) {
            throw new IllegalArgumentException("Неверные данные");
        }
        try {
            UserAudit userAudit = objectMapper.readValue(userAuditJson, UserAudit.class);
            insertUserAudit(userAudit);
            log.info("Получено и сохранено сообщение аудита: {}", userAudit);
        } catch (JsonProcessingException e) {
            log.error("ошибка обработки кафки", e);
        }
    }

    private void insertUserAudit(UserAudit userAudit) {
        try {
            PreparedStatement insertStatement = session.prepare(
                    "INSERT INTO my_keyspace.user_audit (user_id, event_time, event_type) " +
                            "VALUES (?, ?, ?)"
            );
            BoundStatement boundStatement = insertStatement.bind(
                    userAudit.getId(),
                    userAudit.getEventTime(),
                    userAudit.getEventType().name()
            );
            session.execute(boundStatement);
        } catch (Exception e) {
            log.error("Ошибка при вставке сообщения аудита", e);
        }
    }
}
