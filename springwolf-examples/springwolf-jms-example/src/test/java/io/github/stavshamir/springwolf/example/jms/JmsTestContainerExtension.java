// SPDX-License-Identifier: Apache-2.0
package io.github.stavshamir.springwolf.example.jms;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

/**
 * JUnit5 extension to start the localstack testcontainers once
 * and keep it running until all test classes have been completed.
 */
public class JmsTestContainerExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private static volatile boolean started = false;

    static GenericContainer<?> activeMq =
            new GenericContainer<>(DockerImageName.parse("apache/activemq-artemis:2.31.2")).withExposedPorts(61616);

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        if (!started) {
            started = true;

            beforeAllOnce();

            // Ensure closeableResource {@see #close()} method is called
            extensionContext.getRoot().getStore(GLOBAL).put("any unique name", this);
        }
    }

    private static void beforeAllOnce() throws IOException, InterruptedException {
        activeMq.start();

        //        String brokerUrl = "tcp://localhost:" + activeMq.getMappedPort(61616);
        //        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        //        Connection connection = connectionFactory.createConnection();
        //        connection.start();
        //
        //        // Creating session for sending messages
        //        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //
        //        // Getting the queue
        //        var queue = session.createQueue("example-queue");
        //        var queue2 = session.createQueue("another-queue");

        // Creating the producer & consumer
        // session.createProducer(queue);
        // session.createConsumer(queue2);
    }

    @Override
    public void close() {
        activeMq.stop();
    }
}
