package no.fintlabs;

import no.fintlabs.flyt.kafka.InstanceFlowConsumerRecord;
import no.fintlabs.flyt.kafka.headers.InstanceFlowHeaders;
import no.fintlabs.flyt.kafka.requestreply.InstanceFlowRequestProducer;
import no.fintlabs.flyt.kafka.requestreply.InstanceFlowRequestProducerFactory;
import no.fintlabs.flyt.kafka.requestreply.InstanceFlowRequestProducerRecord;
import no.fintlabs.kafka.common.topic.TopicCleanupPolicyParameters;
import no.fintlabs.kafka.requestreply.RequestProducerConfiguration;
import no.fintlabs.kafka.requestreply.topic.ReplyTopicNameParameters;
import no.fintlabs.kafka.requestreply.topic.ReplyTopicService;
import no.fintlabs.kafka.requestreply.topic.RequestTopicNameParameters;
import no.fintlabs.model.Result;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class DispatchInstanceRequestProducerService {

    private final InstanceFlowRequestProducer<Object, Result> instanceFlowRequestProducer;

    public DispatchInstanceRequestProducerService(
            InstanceFlowRequestProducerFactory instanceFlowRequestProducerFactory,
            ReplyTopicService replyTopicService,
            @Value("${fint.kafka.application-id}") String applicationId
    ) {
        ReplyTopicNameParameters replyTopicNameParameters = ReplyTopicNameParameters.builder()
                .applicationId(applicationId)
                .resource("dispatch-instance")
                .build();

        replyTopicService.ensureTopic(replyTopicNameParameters, 0, TopicCleanupPolicyParameters.builder().build());

        this.instanceFlowRequestProducer = instanceFlowRequestProducerFactory.createProducer(
                replyTopicNameParameters,
                Object.class,
                Result.class,
                RequestProducerConfiguration
                        .builder()
                        .defaultReplyTimeout(Duration.ofSeconds(30))
                        .build()
        );
    }

    public Result requestDispatchAndWaitForStatusReply(InstanceFlowHeaders instanceFlowHeaders, Object mappedInstance) {
        RequestTopicNameParameters requestTopicNameParameters = RequestTopicNameParameters.builder()
                .resource("dispatch-instance")
                .build();
        return instanceFlowRequestProducer.requestAndReceive(
                        InstanceFlowRequestProducerRecord.builder()
                                .instanceFlowHeaders(instanceFlowHeaders)
                                .topicNameParameters(requestTopicNameParameters)
                                .value(mappedInstance)
                                .build()
                )
                .map(InstanceFlowConsumerRecord::getConsumerRecord)
                .map(ConsumerRecord::value)
                .orElseThrow(() -> new RuntimeException("No dispatch result received for instance with headers=" + instanceFlowHeaders));
    }

}
