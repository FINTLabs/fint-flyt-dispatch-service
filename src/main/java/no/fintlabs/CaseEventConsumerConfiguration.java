package no.fintlabs;

import no.fint.model.resource.arkiv.noark.SakResource;
import no.fintlabs.flyt.kafka.event.InstanceFlowEventConsumerFactoryService;
import no.fintlabs.kafka.event.topic.EventTopicNameParameters;
import no.fintlabs.model.Dispatch;
import no.fintlabs.model.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
public class CaseEventConsumerConfiguration {

    @Bean
    public ConcurrentMessageListenerContainer<String, SakResource> newOrUpdatedCaseConsumer(
            InstanceFlowEventConsumerFactoryService instanceFlowEventConsumerFactoryService,
            DispatchRepository dispatchRepository,
            DispatchCaseRequestProducerService dispatchCaseRequestProducerService,
            CaseDispatchedEventProducerService caseDispatchedEventProducerService,
            CaseDispatchErrorProducerService caseDispatchErrorProducerService
    ) {
        return instanceFlowEventConsumerFactoryService.createFactory(
                SakResource.class,
                consumerRecord -> {
                    Dispatch dispatch = dispatchRepository.save(new Dispatch(Status.REQUESTED));

                    Status statusReply = dispatchCaseRequestProducerService.requestDispatchAndWaitForStatusReply(
                            consumerRecord.getConsumerRecord().value()
                    );

                    dispatch.setStatus(statusReply);
                    dispatchRepository.save(dispatch);

                    if (statusReply == Status.ACCEPTED) {
                        caseDispatchedEventProducerService.sendCaseDispatchedEvent(consumerRecord.getInstanceFlowHeaders());
                    } else {
                        caseDispatchErrorProducerService.sendDispatchError(consumerRecord.getInstanceFlowHeaders(), statusReply);
                    }

                },
                new CommonLoggingErrorHandler(),
                false
        ).createContainer(
                EventTopicNameParameters.builder()
                        .eventName("new-or-updated-case")
                        .build()
        );
    }

}
