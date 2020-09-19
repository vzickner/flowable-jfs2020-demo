/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.sample;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.eventregistry.impl.EventRegistryEngineConfiguration;
import org.flowable.eventregistry.impl.pipeline.DefaultOutboundEventProcessingPipeline;
import org.flowable.eventregistry.impl.serialization.EventPayloadToJsonStringSerializer;
import org.flowable.eventregistry.model.EventModel;
import org.flowable.eventregistry.model.EventPayload;
import org.flowable.eventregistry.model.KafkaOutboundChannelModel;
import org.flowable.eventregistry.spring.kafka.KafkaOperationsOutboundEventChannelAdapter;
import org.flowable.serverless.ServerlessUtil;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

public class ServerlessApplication {

    public static void main(String[] args) {
        ProcessEngine processEngine = ServerlessUtil.initializeProcessEngineForBpmnModel(commandContext -> {
            EventRegistryEngineConfiguration eventRegistryEngineConfiguration = CommandContextUtil.getEventRegistryEngineConfiguration(commandContext);
            configureEventModel(eventRegistryEngineConfiguration);
            configureChannel(eventRegistryEngineConfiguration);
            return ScanFitnessCard.createScanFitnessCardBpmnModel();
        });
        String cardId = null;
        String gateId = null;
        if (args.length > 1) {
            cardId = args[0];
            gateId = args[1];
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("cardId", cardId);
        variables.put("gateId", gateId);
        String processInstanceId = processEngine.getRuntimeService()
                .startProcessInstanceById(ServerlessUtil.PROCESS_DEFINITION_ID, variables)
                .getId();
        System.out.println("[Graal] - new process instance " + processInstanceId + " started.");
    }

    private static void configureEventModel(EventRegistryEngineConfiguration eventRegistryEngineConfiguration) {
        EventModel eventModel = new EventModel();
        EventPayload customerId = new EventPayload("customerId", "string");
        customerId.setCorrelationParameter(true);
        eventModel.setPayload(Arrays.asList(
                new EventPayload("cardId", "string"),
                customerId,
                new EventPayload("gateId", "string")
        ));
        eventModel.setKey("cardScanEvent");
        eventModel.setName("Card Scan Event");
        ServerlessUtil.deployEventDefinition(eventRegistryEngineConfiguration, eventModel);
    }

    private static void configureChannel(EventRegistryEngineConfiguration eventRegistryEngineConfiguration) {
        KafkaOutboundChannelModel kafkaOutboundChannelModel = new KafkaOutboundChannelModel();
        kafkaOutboundChannelModel.setKey("fitnessCardScanChannel");
        kafkaOutboundChannelModel.setOutboundEventProcessingPipeline(
                new DefaultOutboundEventProcessingPipeline(
                        new EventPayloadToJsonStringSerializer()
                )
        );
        HashMap<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Collections.singletonList("localhost:9092"));
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        DefaultKafkaProducerFactory<Object, Object> defaultKafkaProducerFactory = new DefaultKafkaProducerFactory<>(
                configProps
        );
        kafkaOutboundChannelModel.setSerializerType("json");
        kafkaOutboundChannelModel.setOutboundEventChannelAdapter(
                new KafkaOperationsOutboundEventChannelAdapter(new KafkaTemplate<>(defaultKafkaProducerFactory), "card-scanned-topic", "key")
        );
        ServerlessUtil.deployChannelDefinition(eventRegistryEngineConfiguration, kafkaOutboundChannelModel);
    }

}
