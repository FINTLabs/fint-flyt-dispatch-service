package no.fintlabs;

import no.fintlabs.flyt.kafka.event.error.InstanceFlowErrorEventProducer;
import no.fintlabs.flyt.kafka.event.error.InstanceFlowErrorEventProducerRecord;
import no.fintlabs.flyt.kafka.headers.InstanceFlowHeaders;
import no.fintlabs.kafka.event.error.Error;
import no.fintlabs.kafka.event.error.ErrorCollection;
import no.fintlabs.kafka.event.error.topic.ErrorEventTopicNameParameters;
import no.fintlabs.kafka.event.error.topic.ErrorEventTopicService;
import no.fintlabs.model.Status;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CaseDispatchErrorProducerService {

    private final InstanceFlowErrorEventProducer errorEventProducer;
    private final ErrorEventTopicNameParameters errorEventTopicNameParameters;

    public CaseDispatchErrorProducerService(
            InstanceFlowErrorEventProducer errorEventProducer,
            ErrorEventTopicService errorEventTopicService
    ) {
        this.errorEventProducer = errorEventProducer;
        errorEventTopicNameParameters = ErrorEventTopicNameParameters
                .builder()
                .errorEventName("case-dispatch")
                .build();
        errorEventTopicService.ensureTopic(errorEventTopicNameParameters, 0);
    }

    void sendDispatchError(InstanceFlowHeaders instanceFlowHeaders, Status status) {
        errorEventProducer.send(
                InstanceFlowErrorEventProducerRecord
                        .builder()
                        .topicNameParameters(errorEventTopicNameParameters)
                        .instanceFlowHeaders(instanceFlowHeaders)
                        .errorCollection(
                                new ErrorCollection(
                                        Error
                                                .builder()
                                                .errorCode("CASE_DISPATCH_ERROR")
                                                .args(Map.of("status", status.name()))
                                                .build()
                                )
                        )
                        .build()
        );
    }

}
