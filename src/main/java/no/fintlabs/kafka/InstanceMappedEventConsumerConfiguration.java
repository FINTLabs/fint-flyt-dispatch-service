package no.fintlabs.kafka;

import no.fintlabs.flyt.kafka.event.InstanceFlowEventConsumerFactoryService;
import no.fintlabs.kafka.error.InstanceDispatchingErrorHandlerService;
import no.fintlabs.kafka.event.topic.EventTopicNameParameters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
public class InstanceMappedEventConsumerConfiguration {

    @Bean
    public ConcurrentMessageListenerContainer<String, Object> instanceMappedEventConsumer(
            InstanceFlowEventConsumerFactoryService instanceFlowEventConsumerFactoryService,
            InstanceReadyForDispatchEventProducerService instanceReadyForDispatchEventProducerService,
            InstanceDispatchingErrorHandlerService instanceDispatchingErrorHandlerService
    ) {
        return instanceFlowEventConsumerFactoryService.createFactory(
                Object.class,
                instanceFlowConsumerRecord ->
                        instanceReadyForDispatchEventProducerService.publish(
                                instanceFlowConsumerRecord.getInstanceFlowHeaders(),
                                instanceFlowConsumerRecord.getConsumerRecord().value()
                        ),
                instanceDispatchingErrorHandlerService,
                false
        ).createContainer(
                EventTopicNameParameters.builder()
                        .eventName("instance-mapped")
                        .build()
        );
    }

}
