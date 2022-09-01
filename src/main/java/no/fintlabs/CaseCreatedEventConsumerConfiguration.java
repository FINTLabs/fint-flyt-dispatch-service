package no.fintlabs;

import no.fint.model.resource.arkiv.noark.SakResource;
import no.fintlabs.flyt.kafka.event.InstanceFlowEventConsumerFactoryService;
import no.fintlabs.kafka.event.topic.EventTopicNameParameters;
import no.fintlabs.model.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
public class CaseCreatedEventConsumerConfiguration {

    @Bean
    public ConcurrentMessageListenerContainer<String, SakResource> newOrUpdatedCaseConsumer(
            InstanceFlowEventConsumerFactoryService instanceFlowEventConsumerFactoryService,
            DispatchCaseRequestProducerService dispatchCaseRequestProducerService,
            CaseDispatchedEventProducerService caseDispatchedEventProducerService,
            CaseDispatchingErrorHandlerService caseDispatchingErrorHandlerService
    ) {
        return instanceFlowEventConsumerFactoryService.createFactory(
                SakResource.class,
                consumerRecord -> {
                    Status statusReply = dispatchCaseRequestProducerService.requestDispatchAndWaitForStatusReply(
                            consumerRecord.getConsumerRecord().value()
                    );
                    if (statusReply == Status.ACCEPTED) {
                        caseDispatchedEventProducerService.publish(consumerRecord.getInstanceFlowHeaders());
                    } else {
                        throw new CaseDispatchingException(statusReply);
                    }
                },
                caseDispatchingErrorHandlerService,
                false
        ).createContainer(
                EventTopicNameParameters.builder()
                        .eventName("case-created")
                        .build()
        );
    }

}
