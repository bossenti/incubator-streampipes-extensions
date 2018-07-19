/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.processors.textmining.flink.processor.language;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.textmining.flink.config.TextMiningFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class LanguageDetectionController extends FlinkDataProcessorDeclarer<LanguageDetectionParameters> {

  private static final String RESOURCE_ID = "strings.languagedetection";
  private static final String PE_ID = "org.streampipes.processors.textmining.flink.languagedetection";

  private static final String DETECTION_FIELD_KEY = "detectionField";
  private static final String LANGUAGE_KEY = "language";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(getLabel(PE_ID))
            .category(DataProcessorType.ENRICH_TEXT)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithUnaryMapping(
                            EpRequirements.stringReq(),
                            getLabel(DETECTION_FIELD_KEY),
                            PropertyScope.NONE)
                    .build())
            .outputStrategy(OutputStrategies.append(EpProperties.stringEp(
                    getLabel(LANGUAGE_KEY),
                    "language",
                    "http://schema.org/language")))
            .build();
  }

  private Label getLabel(String id) {
    return Labels.fromResources(RESOURCE_ID, id);
  }

  @Override
  public FlinkDataProcessorRuntime<LanguageDetectionParameters> getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
    String fieldName = extractor.mappingPropertyValue(DETECTION_FIELD_KEY);

    return new LanguageDetectionProgram(new LanguageDetectionParameters(graph, fieldName), TextMiningFlinkConfig.INSTANCE.getDebug());
  }
}