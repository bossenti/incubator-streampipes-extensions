/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.connect.adapters.ti;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.adapter.model.pipeline.AdapterPipeline;
import org.streampipes.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.streampipes.connect.protocol.stream.MqttConfig;
import org.streampipes.connect.protocol.stream.MqttConsumer;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.model.AdapterType;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.sdk.StaticProperties;
import org.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.streampipes.sdk.helpers.Alternatives;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.SPSensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.streampipes.sdk.helpers.EpProperties.*;

public class TISensorTag extends SpecificDataStreamAdapter {

    private Logger logger = LoggerFactory.getLogger(TISensorTag.class);

    public static final String ID = "http://streampipes.org/adapter/specific/tisensortag";

    private static final String ACCESS_MODE = "access_mode";
    private static final String ANONYMOUS_ACCESS = "anonymous-alternative";
    private static final String USERNAME_ACCESS = "username-alternative";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";


    private static final String TIMESTAMP = "timestamp";
    private static final String AMBIENT_TEMP = "ambientTemp";
    private static final String OBJECT_TEMP = "objectTemp";
    private static final String HUMIDITY = "humidity";
    private static final String ACCELERATION_X = "accelX";
    private static final String ACCELERATION_Y = "accelY";
    private static final String ACCELERATION_Z = "accelZ";
    private static final String GYROSCOPE_X = "gyroX";
    private static final String GYROSCOPE_Y = "gyroY";
    private static final String GYROSCOPE_Z = "gyroZ";
    private static final String MAGNETOMETER_X = "magX";
    private static final String MAGNETOMETER_Y = "magY";
    private static final String MAGNETOMETER_Z = "magZ";
    private static final String LIGHT = "light";
    private static final String KEY_1 = "key1";
    private static final String KEY_2 = "key2";

    private MqttConsumer mqttConsumer;
    private MqttConfig mqttConfig;
    private Thread thread;

    public TISensorTag() {
        super();
    }

    public TISensorTag(SpecificAdapterStreamDescription adapterDescription, MqttConfig mqttConfig) {
        super(adapterDescription);
        this.mqttConfig = mqttConfig;
    }

    @Override
    public SpecificAdapterStreamDescription declareModel() {

        SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID, "TI Sensor Tag", "")
                .iconUrl("ti_sensor_tag.png")
                .category(AdapterType.Environment, AdapterType.OpenData)
                .requiredTextParameter(Labels.from("broker_url", "Broker URL",
                        "Example: tcp://test-server.com:1883 (Protocol required. Port required)"))
                .requiredAlternatives(Labels.from(ACCESS_MODE, "Access Mode", ""),
                        Alternatives.from(Labels.from(ANONYMOUS_ACCESS, "Unauthenticated", "")),
                        Alternatives.from(Labels.from(USERNAME_ACCESS, "Username/Password", ""),
                                StaticProperties.group(Labels.withId("username-group"),
                                        StaticProperties.stringFreeTextProperty(Labels.from(USERNAME,
                                                "Username", "")),
                                        StaticProperties.secretValue(Labels.from(PASSWORD,
                                                "Password", "")))))
                .requiredTextParameter(Labels.from("topic", "Topic","Example: test/topic"))
                .build();

        description.setAppId(ID);
        return description;
    }

    @Override
    public void startAdapter() throws AdapterException {
        this.mqttConsumer = new MqttConsumer(this.mqttConfig, new EventProcessor(adapterPipeline));

        thread = new Thread(this.mqttConsumer);
        thread.start();
    }

    @Override
    public void stopAdapter() throws AdapterException {
        this.mqttConsumer.close();
    }

    @Override
    public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
        MqttConfig mqttConfig;
        StaticPropertyExtractor extractor =
                StaticPropertyExtractor.from(adapterDescription.getConfig(), new ArrayList<>());

        String brokerUrl = extractor.singleValueParameter("broker_url", String.class);
        String topic = extractor.singleValueParameter("topic", String.class);
        String selectedAlternative = extractor.selectedAlternativeInternalId("access_mode");

        if (selectedAlternative.equals(ANONYMOUS_ACCESS)) {
            mqttConfig = new MqttConfig(brokerUrl, topic);
        } else {
            String username = extractor.singleValueParameter(USERNAME, String.class);
            String password = extractor.secretValue(PASSWORD);
            mqttConfig = new MqttConfig(brokerUrl, topic, username, password);
        }

        return new TISensorTag(adapterDescription, mqttConfig);
    }

    @Override
    public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) {
        return GuessSchemaBuilder.create()
                .property(timestampProperty(TIMESTAMP))
                .property(doubleEp(Labels.from(AMBIENT_TEMP, "Ambient Temperature", ""),
                        AMBIENT_TEMP, SO.Number))
                .property(doubleEp(Labels.from(OBJECT_TEMP, "Object Temperature", ""),
                        OBJECT_TEMP, SO.Number))
                .property(doubleEp(Labels.from(HUMIDITY, "Humidity", ""),
                        HUMIDITY, SO.Number))
                .property(doubleEp(Labels.from(ACCELERATION_X, "Acceleration X", ""),
                        ACCELERATION_X, SPSensor.ACCELERATION_X))
                .property(doubleEp(Labels.from(ACCELERATION_Y, "Acceleration Y", ""),
                        ACCELERATION_Y, SPSensor.ACCELERATION_Y))
                .property(doubleEp(Labels.from(ACCELERATION_Z, "Acceleration Z", ""),
                        ACCELERATION_Z, SPSensor.ACCELERATION_Z))
                .property(doubleEp(Labels.from(GYROSCOPE_X, "Gyroscope X", ""),
                        GYROSCOPE_X, SO.Number))
                .property(doubleEp(Labels.from(GYROSCOPE_Y, "Gyroscope Y", ""),
                        GYROSCOPE_Y, SO.Number))
                .property(doubleEp(Labels.from(GYROSCOPE_Z, "Gyroscope Z", ""),
                        GYROSCOPE_Z, SO.Number))
                .property(doubleEp(Labels.from(MAGNETOMETER_X, "Magnetometer X", ""),
                        MAGNETOMETER_X, SO.Number))
                .property(doubleEp(Labels.from(MAGNETOMETER_Y, "Magnetometer Y", ""),
                        MAGNETOMETER_Y, SO.Number))
                .property(doubleEp(Labels.from(MAGNETOMETER_Z, "Magnetometer Z", ""),
                        MAGNETOMETER_Z, SO.Number))
                .property(doubleEp(Labels.from(LIGHT, "Light", ""),
                        LIGHT, SO.Number))
                .property(booleanEp(Labels.from(KEY_1, "Key 1", ""),
                        KEY_1, SO.Boolean))
                .property(booleanEp(Labels.from(KEY_2, "Key 2", ""),
                        KEY_2, SO.Boolean))
                .build();
    }

    @Override
    public String getId() {
        return ID;
    }

    private class EventProcessor implements InternalEventProcessor<byte[]> {
        private AdapterPipeline adapterPipeline;

        public EventProcessor(AdapterPipeline adapterpipeline) {
            this.adapterPipeline = adapterpipeline;
        }

        @Override
        public void onEvent(byte[] payload) {
            Map<String, Object> result = parseEvent(new String(payload));
            adapterPipeline.process(result);
        }
    }

    public static Map<String, Object> parseEvent(String s) {
       Map<String, Object> result = new HashMap<>();
       String[] lines = s.split("\n");
       for (String line : lines) {
           if (line.startsWith("\"")) {
              line = line.replaceAll(",", "").replaceAll("\"", "");
              String[] keyValue = line.split(":");

              // convert keys to boolean, other sensor values are doubles
              if (keyValue[0].startsWith("key")) {
                  result.put(keyValue[0], Double.parseDouble(keyValue[1]) == 1.0);
              } else {
                  result.put(keyValue[0], Double.parseDouble(keyValue[1]));
              }
           }
       }

       if (!result.containsKey("key1") || !result.containsKey("key2")) {
           result.put("key1", false);
           result.put("key2", false);
       }

       result.put(TIMESTAMP, System.currentTimeMillis());

       return result;
    }
}