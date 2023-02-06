package io.specmesh.demo;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
import io.specmesh.apiparser.AsyncApiParser;
import io.specmesh.kafka.Clients;
import io.specmesh.kafka.DockerKafkaEnvironment;
import io.specmesh.kafka.KafkaApiSpec;
import io.specmesh.kafka.KafkaEnvironment;
import io.specmesh.kafka.Provisioner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import specmesh.examples.schema_demo.model.UserInfoOuterClass.UserInfo;

class ProtobufSpecFunctionalTest {

    private static final KafkaApiSpec API_SPEC =
            loadSpecFromClassPath("/specmesh-examples-schema_demo-api.yaml");

    @RegisterExtension
    private static final KafkaEnvironment KAFKA_ENV = DockerKafkaEnvironment.builder().build();

    @BeforeAll
    public static void provisionCluster() {
        try (Admin adminClient = AdminClient.create(clientProperties())) {
            final SchemaRegistryClient schemaRegistryClient =
                    new CachedSchemaRegistryClient(KAFKA_ENV.schemeRegistryServer(), 10);

            Provisioner.provisionTopics(API_SPEC, adminClient);
            Provisioner.provisionSchemas(
                    API_SPEC, "../api/build/resources/main", schemaRegistryClient);
        }
    }

    @Test
    void shouldProduceAndConsume() throws Exception {
        final String userInfoTopic = domainTopicFullName("_public.user_info");
        final UserInfo userSam =
                UserInfo.newBuilder()
                        .setFullName("Sam Fellow")
                        .setEmail("hello-sam@bahamas.island")
                        .setAge(52)
                        .build();

        try (Consumer<Long, UserInfo> consumer = domainConsumer();
                Producer<Long, UserInfo> producer = domainProducer()) {

            consumer.subscribe(List.of(userInfoTopic));

            producer.send(new ProducerRecord<>(userInfoTopic, 1000L, userSam)).get(60, SECONDS);

            final ConsumerRecords<Long, UserInfo> consumerRecords =
                    consumer.poll(Duration.ofSeconds(30));
            assertThat(consumerRecords.count(), is(1));
            assertThat(consumerRecords.iterator().next().value(), is(userSam));
        }
    }

    private Producer<Long, UserInfo> domainProducer() {
        final Map<String, Object> properties =
                Clients.producerProperties(
                        API_SPEC.id(),
                        "my-service-id",
                        KAFKA_ENV.kafkaBootstrapServers(),
                        KAFKA_ENV.schemeRegistryServer(),
                        LongSerializer.class,
                        KafkaProtobufSerializer.class,
                        true,
                        clientProperties());
        return new KafkaProducer<>(properties);
    }

    private Consumer<Long, UserInfo> domainConsumer() {
        final Map<String, Object> properties =
                Clients.consumerProperties(
                        API_SPEC.id(),
                        "my-service-id",
                        KAFKA_ENV.kafkaBootstrapServers(),
                        KAFKA_ENV.schemeRegistryServer(),
                        LongDeserializer.class,
                        KafkaProtobufDeserializer.class,
                        true,
                        clientProperties());
        properties.put(
                KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE,
                UserInfo.class.getName());
        return new KafkaConsumer<>(properties);
    }

    @SuppressWarnings("SameParameterValue")
    private static String domainTopicFullName(final String name) {
        final List<NewTopic> domainTopics = API_SPEC.listDomainOwnedTopics();
        return domainTopics.stream()
                .filter(topic -> topic.name().endsWith(name))
                .findFirst()
                .orElseThrow()
                .name();
    }

    private static Map<String, Object> clientProperties() {
        return Map.of(
                CommonClientConfigs.CLIENT_ID_CONFIG,
                UUID.randomUUID().toString(),
                CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,
                KAFKA_ENV.kafkaBootstrapServers());
    }

    @SuppressWarnings("SameParameterValue")
    private static KafkaApiSpec loadSpecFromClassPath(final String spec) {
        try (InputStream s = ProtobufSpecFunctionalTest.class.getResourceAsStream(spec)) {
            if (s == null) {
                throw new FileNotFoundException("Resource not found: " + spec);
            }
            return new KafkaApiSpec(new AsyncApiParser().loadResource(s));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load api spec: " + spec, e);
        }
    }
}
