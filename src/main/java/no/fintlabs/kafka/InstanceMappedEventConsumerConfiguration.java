package no.fintlabs.kafka;

import no.fintlabs.flyt.kafka.event.InstanceFlowEventConsumerFactoryService;
import no.fintlabs.kafka.error.InstanceDispatchingErrorProducerService;
import no.fintlabs.kafka.event.EventConsumerConfiguration;
import no.fintlabs.kafka.event.topic.EventTopicNameParameters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class InstanceMappedEventConsumerConfiguration {

    @Bean
    public ConcurrentMessageListenerContainer<String, Object> instanceMappedEventConsumer(
            InstanceFlowEventConsumerFactoryService instanceFlowEventConsumerFactoryService,
            InstanceReadyForDispatchEventProducerService instanceReadyForDispatchEventProducerService,
            InstanceDispatchingErrorProducerService instanceDispatchingErrorProducerService
    ) {
        return instanceFlowEventConsumerFactoryService.createRecordFactory(
                Object.class,
                instanceFlowConsumerRecord -> {
                    try {
                        instanceReadyForDispatchEventProducerService.publish(
                                instanceFlowConsumerRecord.getInstanceFlowHeaders(),
                                instanceFlowConsumerRecord.getConsumerRecord().value()
                        );
                    } catch (Exception e) {
                        instanceDispatchingErrorProducerService.publishGeneralSystemErrorEvent(
                                instanceFlowConsumerRecord.getInstanceFlowHeaders()
                        );
                    }
                },
                EventConsumerConfiguration
                        .builder()
                        .errorHandler(new DefaultErrorHandler(
                                new FixedBackOff(FixedBackOff.DEFAULT_INTERVAL, 0))
                        )
                        .build()
        ).createContainer(
                EventTopicNameParameters.builder()
                        .eventName("instance-mapped")
                        .build()
        );
    }

}
