package no.fintlabs;

import no.fintlabs.flyt.kafka.event.InstanceFlowEventConsumerFactoryService;
import no.fintlabs.kafka.event.topic.EventTopicNameParameters;
import no.fintlabs.model.Result;
import no.fintlabs.model.Status;
import no.fintlabs.model.mappedinstance.MappedInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
public class InstanceMappedEventConsumerConfiguration {

    @Bean
    public ConcurrentMessageListenerContainer<String, MappedInstance> instanceMappedEventConsumer(
            InstanceFlowEventConsumerFactoryService instanceFlowEventConsumerFactoryService,
            DispatchInstanceRequestProducerService dispatchInstanceRequestProducerService,
            InstanceDispatchedEventProducerService instanceDispatchedEventProducerService,
            InstanceDispatchingErrorHandlerService instanceDispatchingErrorHandlerService
    ) {
        return instanceFlowEventConsumerFactoryService.createFactory(
                MappedInstance.class,
                consumerRecord -> {
                    Result result = dispatchInstanceRequestProducerService.requestDispatchAndWaitForStatusReply(
                            consumerRecord.getConsumerRecord().value()
                    );
                    if (result.getStatus() == Status.ACCEPTED) {
                        instanceDispatchedEventProducerService.publish(
                                consumerRecord.getInstanceFlowHeaders()
                                        .toBuilder()
                                        .archiveInstanceId(result.getArchiveCaseId())
                                        .build()
                        );
                    } else {
                        throw new InstanceDispatchingException(result.getStatus());
                    }
                },
                instanceDispatchingErrorHandlerService,
                false
        ).createContainer(
                EventTopicNameParameters.builder()
                        .eventName("instance-mapped")
                        .build()
        );
    }

}
