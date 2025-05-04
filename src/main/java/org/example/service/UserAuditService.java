package org.example.service;

import org.example.entity.UserAudit;
import org.example.statement.UserAuditStatement;
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

    @Autowired
    private UserAuditStatement userAuditStatement;

    public void saveAudit(UserAudit userAudit) {
        BoundStatement boundStatement = userAuditStatement.getInsertStatement().bind(
                userAudit.getId(),
                userAudit.getEventTime(),
                userAudit.getEventType().name()
        );
        session.execute(boundStatement);
    }

    public List<Row> findById(UUID id) {
        BoundStatement boundStatement = userAuditStatement.getSelectStatement().bind(id);
        ResultSet resultSet = session.execute(boundStatement);
        List<Row> events = new ArrayList<>();
        for (Row row : resultSet) {
            events.add(row);
        }
        return events;
    }
}