package org.example.service;

import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import org.example.TypesOfEvent;
import org.example.entity.UserAudit;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserAuditService {

    @Autowired
    private CqlSession session;

    public void saveAudit(UserAudit userAudit) {
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
    }

    public List<UserAudit> findById(UUID id) {
        PreparedStatement selectStatement = session.prepare(
                "SELECT * FROM my_keyspace.user_audit WHERE user_id = ?"
        );
        BoundStatement boundStatement = selectStatement.bind(id);
        ResultSet resultSet = session.execute(boundStatement);
        List<UserAudit> events = new ArrayList<>();
        for (Row row : resultSet) {
            events.add(new UserAudit(
                    row.getUuid("user_id"),
                    row.getInstant("event_time"),
                    (TypesOfEvent) row.getObject("event_type")));
        }
        return events;
    }
}