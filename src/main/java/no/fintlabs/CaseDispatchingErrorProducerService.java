package no.fintlabs;

import no.fintlabs.flyt.kafka.event.error.InstanceFlowErrorEventProducer;
import no.fintlabs.flyt.kafka.event.error.InstanceFlowErrorEventProducerRecord;
import no.fintlabs.flyt.kafka.headers.InstanceFlowHeaders;
import no.fintlabs.kafka.event.error.Error;
import no.fintlabs.kafka.event.error.ErrorCollection;
import no.fintlabs.kafka.event.error.topic.ErrorEventTopicNameParameters;
import no.fintlabs.kafka.event.error.topic.ErrorEventTopicService;
import org.springframework.stereotype.Service;

import java.util.Map;

import static no.fintlabs.ErrorCode.CASE_DISPATCH_ERROR;
import static no.fintlabs.ErrorCode.GENERAL_SYSTEM_ERROR;

@Service
public class CaseDispatchingErrorProducerService {

    private final InstanceFlowErrorEventProducer errorEventProducer;
    private final ErrorEventTopicNameParameters errorEventTopicNameParameters;

    public CaseDispatchingErrorProducerService(
            InstanceFlowErrorEventProducer errorEventProducer,
            ErrorEventTopicService errorEventTopicService
    ) {
        this.errorEventProducer = errorEventProducer;
        errorEventTopicNameParameters = ErrorEventTopicNameParameters
                .builder()
                .errorEventName("case-dispatching-error")
                .build();
        errorEventTopicService.ensureTopic(errorEventTopicNameParameters, 0);
    }

    void publishCaseDispatchErrorEvent(InstanceFlowHeaders instanceFlowHeaders, CaseDispatchingException caseDispatchingException) {
        errorEventProducer.send(
                InstanceFlowErrorEventProducerRecord
                        .builder()
                        .topicNameParameters(errorEventTopicNameParameters)
                        .instanceFlowHeaders(instanceFlowHeaders)
                        .errorCollection(
                                new ErrorCollection(
                                        Error
                                                .builder()
                                                .errorCode(CASE_DISPATCH_ERROR.getCode())
                                                .args(Map.of("status", caseDispatchingException.getStatus().name()))
                                                .build()
                                )
                        )
                        .build()
        );
    }

    void publishGeneralSystemErrorEvent(InstanceFlowHeaders instanceFlowHeaders) {
        errorEventProducer.send(
                InstanceFlowErrorEventProducerRecord
                        .builder()
                        .topicNameParameters(errorEventTopicNameParameters)
                        .instanceFlowHeaders(instanceFlowHeaders)
                        .errorCollection(
                                new ErrorCollection(
                                        Error
                                                .builder()
                                                .errorCode(GENERAL_SYSTEM_ERROR.getCode())
                                                .build()
                                )
                        )
                        .build()
        );
    }

}
