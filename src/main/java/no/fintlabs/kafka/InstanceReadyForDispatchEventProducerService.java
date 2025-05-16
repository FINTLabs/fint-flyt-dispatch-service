package no.fintlabs.kafka;

import no.fintlabs.flyt.kafka.event.InstanceFlowEventProducer;
import no.fintlabs.flyt.kafka.event.InstanceFlowEventProducerFactory;
import no.fintlabs.flyt.kafka.event.InstanceFlowEventProducerRecord;
import no.fintlabs.flyt.kafka.headers.InstanceFlowHeaders;
import no.fintlabs.kafka.event.topic.EventTopicNameParameters;
import no.fintlabs.kafka.event.topic.EventTopicService;
import org.springframework.stereotype.Service;

@Service
public class InstanceReadyForDispatchEventProducerService {

    private final InstanceFlowEventProducer<Object> eventProducer;
    private final EventTopicNameParameters eventTopicNameParameters;

    public InstanceReadyForDispatchEventProducerService(
            InstanceFlowEventProducerFactory eventProducerFactory,
            EventTopicService eventTopicService
    ) {
        this.eventProducer = eventProducerFactory.createProducer(Object.class);
        eventTopicNameParameters = EventTopicNameParameters
                .builder()
                .eventName("instance-ready-for-dispatch")
                .build();
        eventTopicService.ensureTopic(eventTopicNameParameters, 345600000);
    }

    public void publish(InstanceFlowHeaders instanceFlowHeaders, Object instance) {
        eventProducer.send(
                InstanceFlowEventProducerRecord
                        .builder()
                        .topicNameParameters(eventTopicNameParameters)
                        .instanceFlowHeaders(instanceFlowHeaders)
                        .value(instance)
                        .build()
        );
    }

}
