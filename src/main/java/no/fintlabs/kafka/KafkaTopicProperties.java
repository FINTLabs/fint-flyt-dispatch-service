package no.fintlabs.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "fint.flyt.dispatch-service.kafka.topic")
public class KafkaTopicProperties {

    private long instanceProcessingEventsRetentionTimeMs;
}
