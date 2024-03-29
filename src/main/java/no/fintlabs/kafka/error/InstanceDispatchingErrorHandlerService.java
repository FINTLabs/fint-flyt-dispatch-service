package no.fintlabs.kafka.error;

import no.fintlabs.flyt.kafka.InstanceFlowErrorHandler;
import no.fintlabs.flyt.kafka.headers.InstanceFlowHeaders;
import no.fintlabs.flyt.kafka.headers.InstanceFlowHeadersMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

@Service
public class InstanceDispatchingErrorHandlerService extends InstanceFlowErrorHandler {

    private final InstanceDispatchingErrorProducerService instanceDispatchingErrorProducerService;

    protected InstanceDispatchingErrorHandlerService(
            InstanceFlowHeadersMapper instanceFlowHeadersMapper,
            InstanceDispatchingErrorProducerService instanceDispatchingErrorProducerService
    ) {
        super(instanceFlowHeadersMapper);
        this.instanceDispatchingErrorProducerService = instanceDispatchingErrorProducerService;
    }

    @Override
    public void handleInstanceFlowRecord(Throwable cause, InstanceFlowHeaders instanceFlowHeaders, ConsumerRecord<?, ?> consumerRecord) {
        instanceDispatchingErrorProducerService.publishGeneralSystemErrorEvent(instanceFlowHeaders);
    }

}
