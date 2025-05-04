package org.example.Service;

import org.example.Application;
import org.example.TypesOfEvent;
import org.example.entity.UserAudit;
import org.example.service.UserAuditService;
import com.datastax.oss.driver.api.core.cql.Row;
import com.github.dockerjava.api.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@SpringBootTest(classes = Application.class)
@Testcontainers
public class UserAuditServiceTest {

    @Autowired
    public UserAuditService userAuditService;

    @Container
    private static final CassandraContainer<?> cassandraContainer =
            new CassandraContainer<>("cassandra:4.1.3")
                    .withExposedPorts(9042)
                    .withReuse(true)
                    .waitingFor(Wait.forLogMessage(".*Created default superuser role 'cassandra'.*", 1));

    @DynamicPropertySource
    static void cassandraProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cassandra.contact-points",
                () -> cassandraContainer.getHost() + ":" + cassandraContainer.getMappedPort(9042));
        registry.add("spring.cassandra.local-datacenter", () -> "datacenter1");
        registry.add("spring.cassandra.keyspace-name", () -> "my_keyspace");
    }

    @BeforeAll
    static void checkContainer() {
        if (!cassandraContainer.isRunning()) {
            cassandraContainer.start();
        }
        System.out.println("Cassandra port: " + cassandraContainer.getMappedPort(9042));
    }


    @Test
    public void testCreateUser() {
        UserAudit user = new UserAudit(UUID.randomUUID(), Instant.now(), TypesOfEvent.CREATE);
        userAuditService.saveAudit(user);
        List<UserAudit> userAudit = userAuditService.findById(user.getId());
        Assertions.assertNotNull(user.getId());
        assertEquals(userAudit.size(), 1);
    }

    @Test
    public void testGetUserById() {
        UserAudit invalidUser = null;
        assertThrows(RuntimeException.class, () -> {
            userAuditService.saveAudit(invalidUser);
        });
    }
}
