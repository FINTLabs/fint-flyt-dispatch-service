package no.fintlabs;

import no.fintlabs.exceptions.InstanceDispatchDeclinedException;
import no.fintlabs.flyt.kafka.event.error.InstanceFlowErrorEventProducer;
import no.fintlabs.flyt.kafka.event.error.InstanceFlowErrorEventProducerRecord;
import no.fintlabs.flyt.kafka.headers.InstanceFlowHeaders;
import no.fintlabs.kafka.event.error.Error;
import no.fintlabs.kafka.event.error.ErrorCollection;
import no.fintlabs.kafka.event.error.topic.ErrorEventTopicNameParameters;
import no.fintlabs.kafka.event.error.topic.ErrorEventTopicService;
import org.springframework.stereotype.Service;

import java.util.Map;

import static no.fintlabs.ErrorCode.GENERAL_SYSTEM_ERROR;
import static no.fintlabs.ErrorCode.INSTANCE_DISPATCH_DECLINED_ERROR;

@Service
public class InstanceDispatchingErrorProducerService {

    private final InstanceFlowErrorEventProducer errorEventProducer;
    private final ErrorEventTopicNameParameters errorEventTopicNameParameters;

    public InstanceDispatchingErrorProducerService(
            InstanceFlowErrorEventProducer errorEventProducer,
            ErrorEventTopicService errorEventTopicService
    ) {
        this.errorEventProducer = errorEventProducer;
        errorEventTopicNameParameters = ErrorEventTopicNameParameters
                .builder()
                .errorEventName("instance-dispatching-error")
                .build();
        errorEventTopicService.ensureTopic(errorEventTopicNameParameters, 0);
    }

    void publishInstanceDispatchDeclinedErrorEvent(InstanceFlowHeaders instanceFlowHeaders, InstanceDispatchDeclinedException instanceDispatchDeclinedException) {
        errorEventProducer.send(
                InstanceFlowErrorEventProducerRecord
                        .builder()
                        .topicNameParameters(errorEventTopicNameParameters)
                        .instanceFlowHeaders(instanceFlowHeaders)
                        .errorCollection(
                                new ErrorCollection(
                                        Error
                                                .builder()
                                                .errorCode(INSTANCE_DISPATCH_DECLINED_ERROR.getCode())
                                                .args(Map.of("errorMessage", instanceDispatchDeclinedException.getErrorMessage()))
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
