// SPDX-License-Identifier: Apache-2.0
package io.github.stavshamir.springwolf.asyncapi.scanners.channels.annotation;

import com.asyncapi.v2.binding.channel.ChannelBinding;
import com.asyncapi.v2.binding.message.MessageBinding;
import com.asyncapi.v2.binding.operation.OperationBinding;
import io.github.stavshamir.springwolf.asyncapi.scanners.channels.ChannelsScanner;
import io.github.stavshamir.springwolf.asyncapi.scanners.classes.ComponentClassScanner;
import io.github.stavshamir.springwolf.asyncapi.types.channel.operation.message.header.AsyncHeaders;
import io.github.stavshamir.springwolf.asyncapi.types.channel.operation.message.header.AsyncHeadersForSpringKafkaBuilder;
import io.github.stavshamir.springwolf.schemas.SchemasService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;
import java.util.Map;

import static io.github.stavshamir.springwolf.asyncapi.scanners.channels.annotation.SpringPayloadAnnotationTypeExtractor.getPayloadType;

@Slf4j
public class ClassLevelKafkaListenerScanner extends AbstractClassLevelListenerScanner<KafkaListener, KafkaHandler>
        implements ChannelsScanner, EmbeddedValueResolverAware {

    private StringValueResolver resolver;

    public ClassLevelKafkaListenerScanner(ComponentClassScanner componentClassScanner, SchemasService schemasService) {
        super(componentClassScanner, schemasService);
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected Class<KafkaListener> getListenerAnnotationClass() {
        return KafkaListener.class;
    }

    @Override
    protected Class<KafkaHandler> getHandlerAnnotationClass() {
        return KafkaHandler.class;
    }

    @Override
    protected String getChannelName(KafkaListener annotation) {
        return KafkaListenerUtil.getChannelName(annotation, resolver);
    }

    @Override
    protected Map<String, ? extends OperationBinding> buildOperationBinding(KafkaListener annotation) {
        return KafkaListenerUtil.buildOperationBinding(annotation, resolver);
    }

    @Override
    protected Map<String, ? extends ChannelBinding> buildChannelBinding(KafkaListener annotation) {
        return KafkaListenerUtil.buildChannelBinding();
    }

    @Override
    protected Map<String, ? extends MessageBinding> buildMessageBinding(Method method) {
        // Currently there is no interesting data in the KafkaListener annotation, but we keep it for the sake of
        // consistency in the code and in the serialized specification (always have at least an empty binding for kafka)
        return KafkaListenerUtil.buildMessageBinding();
    }

    @Override
    protected AsyncHeaders buildHeaders(Method method) {
        Class<?> payloadType = getPayloadType(method);
        return new AsyncHeadersForSpringKafkaBuilder("SpringKafkaDefaultHeaders-" + payloadType.getSimpleName())
                .withTypeIdHeader(payloadType.getTypeName())
                .build();
    }
}
