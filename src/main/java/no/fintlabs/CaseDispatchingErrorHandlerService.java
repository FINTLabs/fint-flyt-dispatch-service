package no.fintlabs;

import no.fintlabs.flyt.kafka.InstanceFlowErrorHandler;
import no.fintlabs.flyt.kafka.headers.InstanceFlowHeaders;
import no.fintlabs.flyt.kafka.headers.InstanceFlowHeadersMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

@Service
public class CaseDispatchingErrorHandlerService extends InstanceFlowErrorHandler {

    private final CaseDispatchingErrorProducerService caseDispatchingErrorProducerService;

    protected CaseDispatchingErrorHandlerService(
            InstanceFlowHeadersMapper instanceFlowHeadersMapper,
            CaseDispatchingErrorProducerService caseDispatchingErrorProducerService
    ) {
        super(instanceFlowHeadersMapper);
        this.caseDispatchingErrorProducerService = caseDispatchingErrorProducerService;
    }

    @Override
    public void handleInstanceFlowRecord(Throwable cause, InstanceFlowHeaders instanceFlowHeaders, ConsumerRecord<?, ?> consumerRecord) {
        if (cause instanceof CaseDispatchingException) {
            caseDispatchingErrorProducerService.publishCaseDispatchErrorEvent(instanceFlowHeaders, (CaseDispatchingException) cause);
        } else {
            caseDispatchingErrorProducerService.publishGeneralSystemErrorEvent(instanceFlowHeaders);
        }
    }

}
