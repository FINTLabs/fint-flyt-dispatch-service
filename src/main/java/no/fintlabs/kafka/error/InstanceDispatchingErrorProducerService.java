package no.fintlabs.kafka.error;

import no.fintlabs.flyt.kafka.event.error.InstanceFlowErrorEventProducer;
import no.fintlabs.flyt.kafka.event.error.InstanceFlowErrorEventProducerRecord;
import no.fintlabs.flyt.kafka.headers.InstanceFlowHeaders;
import no.fintlabs.kafka.event.error.Error;
import no.fintlabs.kafka.event.error.ErrorCollection;
import no.fintlabs.kafka.event.error.topic.ErrorEventTopicNameParameters;
import no.fintlabs.kafka.event.error.topic.ErrorEventTopicService;
import org.springframework.stereotype.Service;

import static no.fintlabs.kafka.error.ErrorCode.GENERAL_SYSTEM_ERROR;


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

    public void publishGeneralSystemErrorEvent(InstanceFlowHeaders instanceFlowHeaders) {
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
