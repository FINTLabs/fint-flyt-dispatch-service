package no.fintlabs;

import no.fint.model.resource.arkiv.noark.SakResource;
import no.fintlabs.kafka.common.topic.TopicCleanupPolicyParameters;
import no.fintlabs.kafka.requestreply.RequestProducer;
import no.fintlabs.kafka.requestreply.RequestProducerConfiguration;
import no.fintlabs.kafka.requestreply.RequestProducerFactory;
import no.fintlabs.kafka.requestreply.RequestProducerRecord;
import no.fintlabs.kafka.requestreply.topic.ReplyTopicNameParameters;
import no.fintlabs.kafka.requestreply.topic.ReplyTopicService;
import no.fintlabs.kafka.requestreply.topic.RequestTopicNameParameters;
import no.fintlabs.model.Status;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class DispatchCaseRequestProducerService {

    private final RequestProducer<SakResource, Status> requestProducer;

    public DispatchCaseRequestProducerService(
            RequestProducerFactory requestProducerFactory,
            ReplyTopicService replyTopicService,
            @Value("${fint.kafka.application-id}") String applicationId
    ) {
        ReplyTopicNameParameters replyTopicNameParameters = ReplyTopicNameParameters.builder()
                .applicationId(applicationId)
                .resource("dispatch.case")
                .build();

        replyTopicService.ensureTopic(replyTopicNameParameters, 0, TopicCleanupPolicyParameters.builder().build());

        this.requestProducer = requestProducerFactory.createProducer(
                replyTopicNameParameters,
                SakResource.class,
                Status.class,
                RequestProducerConfiguration
                        .builder()
                        .defaultReplyTimeout(Duration.ofSeconds(30))
                        .build()
        );
    }

    public Status requestDispatchAndWaitForStatusReply(SakResource sakResource) {
        RequestTopicNameParameters requestTopicNameParameters = RequestTopicNameParameters.builder()
                .resource("dispatch.case")
                .build();
        return requestProducer.requestAndReceive(
                        RequestProducerRecord.<SakResource>builder()
                                .topicNameParameters(requestTopicNameParameters)
                                .value(sakResource)
                                .build()
                )
                .map(ConsumerRecord::value)
                .orElse(Status.FAILED);
    }

}
