package no.fintlabs;

import no.fintlabs.kafka.common.topic.TopicCleanupPolicyParameters;
import no.fintlabs.kafka.requestreply.RequestProducer;
import no.fintlabs.kafka.requestreply.RequestProducerConfiguration;
import no.fintlabs.kafka.requestreply.RequestProducerFactory;
import no.fintlabs.kafka.requestreply.RequestProducerRecord;
import no.fintlabs.kafka.requestreply.topic.ReplyTopicNameParameters;
import no.fintlabs.kafka.requestreply.topic.ReplyTopicService;
import no.fintlabs.kafka.requestreply.topic.RequestTopicNameParameters;
import no.fintlabs.model.Status;
import no.fintlabs.model.mappedinstance.MappedInstance;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class DispatchInstanceRequestProducerService {

    private final RequestProducer<MappedInstance, Status> requestProducer;

    public DispatchInstanceRequestProducerService(
            RequestProducerFactory requestProducerFactory,
            ReplyTopicService replyTopicService,
            @Value("${fint.kafka.application-id}") String applicationId
    ) {
        ReplyTopicNameParameters replyTopicNameParameters = ReplyTopicNameParameters.builder()
                .applicationId(applicationId)
                .resource("dispatch-instance")
                .build();

        replyTopicService.ensureTopic(replyTopicNameParameters, 0, TopicCleanupPolicyParameters.builder().build());

        this.requestProducer = requestProducerFactory.createProducer(
                replyTopicNameParameters,
                MappedInstance.class,
                Status.class,
                RequestProducerConfiguration
                        .builder()
                        .defaultReplyTimeout(Duration.ofSeconds(30))
                        .build()
        );
    }

    public Status requestDispatchAndWaitForStatusReply(MappedInstance mappedInstance) {
        RequestTopicNameParameters requestTopicNameParameters = RequestTopicNameParameters.builder()
                .resource("dispatch-instance")
                .build();
        return requestProducer.requestAndReceive(
                        RequestProducerRecord.<MappedInstance>builder()
                                .topicNameParameters(requestTopicNameParameters)
                                .value(mappedInstance)
                                .build()
                )
                .map(ConsumerRecord::value)
                .orElse(Status.FAILED);
    }

}
