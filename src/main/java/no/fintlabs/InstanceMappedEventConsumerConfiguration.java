package no.fintlabs;

import no.fintlabs.exceptions.InstanceDispatchDeclinedException;
import no.fintlabs.exceptions.InstanceDispatchFailedException;
import no.fintlabs.flyt.kafka.event.InstanceFlowEventConsumerFactoryService;
import no.fintlabs.kafka.event.topic.EventTopicNameParameters;
import no.fintlabs.model.Result;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
public class InstanceMappedEventConsumerConfiguration {

    @Bean
    public ConcurrentMessageListenerContainer<String, Object> instanceMappedEventConsumer(
            InstanceFlowEventConsumerFactoryService instanceFlowEventConsumerFactoryService,
            DispatchInstanceRequestProducerService dispatchInstanceRequestProducerService,
            InstanceDispatchedEventProducerService instanceDispatchedEventProducerService,
            InstanceDispatchingErrorHandlerService instanceDispatchingErrorHandlerService
    ) {
        return instanceFlowEventConsumerFactoryService.createFactory(
                Object.class,
                instanceFlowConsumerRecord -> {
                    Result result = dispatchInstanceRequestProducerService.requestDispatchAndWaitForStatusReply(
                            instanceFlowConsumerRecord.getInstanceFlowHeaders(),
                            instanceFlowConsumerRecord.getConsumerRecord().value()
                    );
                    switch (result.getStatus()) {
                        case ACCEPTED -> instanceDispatchedEventProducerService.publish(
                                instanceFlowConsumerRecord.getInstanceFlowHeaders()
                                        .toBuilder()
                                        .archiveInstanceId(result.getArchiveCaseId())
                                        .build());
                        case DECLINED -> throw new InstanceDispatchDeclinedException(result.getErrorMessage());
                        case FAILED -> throw new InstanceDispatchFailedException();
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
