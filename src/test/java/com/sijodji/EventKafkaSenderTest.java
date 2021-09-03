package com.sijodji;

import com.sijodji.config.AnotherEventReceiver;
import com.sijodji.config.SomeEventReceiver;
import com.sijodji.context.annotation.EnableKafkaEvents;
import com.sijodji.model.AnotherEvent;
import com.sijodji.model.SomeEvent;
import com.sijodji.transfer.BasicEventKafkaSender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@EmbeddedKafka(partitions = 1)
@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest
@EnableKafkaEvents
class EventKafkaSenderTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private BasicEventKafkaSender<SomeEvent> someEventBasicEventKafkaSender;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private BasicEventKafkaSender<AnotherEvent> anotherEventBasicEventKafkaSender;
    @Autowired
    private SomeEventReceiver someEventReceiver;
    @Autowired
    private AnotherEventReceiver anotherEventReceiver;

    @Test
    void send() throws InterruptedException {
        SomeEvent someEvent = SomeEvent.builder()
                .uuid(UUID.randomUUID())
                .data("some data")
                .build();

        AnotherEvent anotherData = AnotherEvent.builder()
                .uuid(UUID.randomUUID())
                .anotherData("another data")
                .build();

        someEventBasicEventKafkaSender.send(someEvent);
        anotherEventBasicEventKafkaSender.send(anotherData);
        Thread.sleep(5000);

        Assertions.assertTrue(someEventReceiver.getEventConsumedMarker().wasReceived());
        Assertions.assertEquals(someEvent, someEventReceiver.getEventConsumedMarker().getPayload());

        Assertions.assertTrue(anotherEventReceiver.getEventConsumedMarker().wasReceived());
        Assertions.assertEquals(anotherData, anotherEventReceiver.getEventConsumedMarker().getPayload());
    }
}
