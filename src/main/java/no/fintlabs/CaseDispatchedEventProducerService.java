package no.fintlabs;

import no.fintlabs.flyt.kafka.event.InstanceFlowEventProducer;
import no.fintlabs.flyt.kafka.event.InstanceFlowEventProducerFactory;
import no.fintlabs.flyt.kafka.event.InstanceFlowEventProducerRecord;
import no.fintlabs.flyt.kafka.headers.InstanceFlowHeaders;
import no.fintlabs.kafka.event.topic.EventTopicNameParameters;
import no.fintlabs.kafka.event.topic.EventTopicService;
import org.springframework.stereotype.Service;

@Service
public class CaseDispatchedEventProducerService {

    private final InstanceFlowEventProducer<Object> eventProducer;
    private final EventTopicNameParameters eventTopicNameParameters;

    public CaseDispatchedEventProducerService(
            InstanceFlowEventProducerFactory eventProducerFactory,
            EventTopicService eventTopicService
    ) {
        this.eventProducer = eventProducerFactory.createProducer(Object.class);
        eventTopicNameParameters = EventTopicNameParameters
                .builder()
                .eventName("case-dispatched")
                .build();
        eventTopicService.ensureTopic(eventTopicNameParameters, 0);
    }

    public void sendCaseDispatchedEvent(InstanceFlowHeaders instanceFlowHeaders) {
        eventProducer.send(
                InstanceFlowEventProducerRecord
                        .builder()
                        .topicNameParameters(eventTopicNameParameters)
                        .instanceFlowHeaders(instanceFlowHeaders)
                        .value(null)
                        .build()
        );
    }

}
