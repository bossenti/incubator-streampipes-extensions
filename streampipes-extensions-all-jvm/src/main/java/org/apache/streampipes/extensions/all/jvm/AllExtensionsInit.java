/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.extensions.all.jvm;

import org.apache.streampipes.connect.adapters.coindesk.CoindeskBitcoinAdapter;
import org.apache.streampipes.connect.adapters.flic.FlicMQTTAdapter;
import org.apache.streampipes.connect.adapters.gdelt.GdeltAdapter;
import org.apache.streampipes.connect.adapters.iex.IexCloudNewsAdapter;
import org.apache.streampipes.connect.adapters.iex.IexCloudStockAdapter;
import org.apache.streampipes.connect.adapters.image.set.ImageSetAdapter;
import org.apache.streampipes.connect.adapters.image.stream.ImageStreamAdapter;
import org.apache.streampipes.connect.adapters.influxdb.InfluxDbSetAdapter;
import org.apache.streampipes.connect.adapters.influxdb.InfluxDbStreamAdapter;
import org.apache.streampipes.connect.adapters.iss.IssAdapter;
import org.apache.streampipes.connect.adapters.mysql.MySqlSetAdapter;
import org.apache.streampipes.connect.adapters.mysql.MySqlStreamAdapter;
import org.apache.streampipes.connect.adapters.netio.NetioMQTTAdapter;
import org.apache.streampipes.connect.adapters.netio.NetioRestAdapter;
import org.apache.streampipes.connect.adapters.opcua.OpcUaAdapter;
import org.apache.streampipes.connect.adapters.plc4x.modbus.Plc4xModbusAdapter;
import org.apache.streampipes.connect.adapters.plc4x.s7.Plc4xS7Adapter;
import org.apache.streampipes.connect.adapters.ros.RosBridgeAdapter;
import org.apache.streampipes.connect.adapters.simulator.machine.MachineDataStreamAdapter;
import org.apache.streampipes.connect.adapters.simulator.random.RandomDataSetAdapter;
import org.apache.streampipes.connect.adapters.simulator.random.RandomDataStreamAdapter;
import org.apache.streampipes.connect.adapters.slack.SlackAdapter;
import org.apache.streampipes.connect.adapters.ti.TISensorTag;
import org.apache.streampipes.connect.adapters.wikipedia.WikipediaEditedArticlesAdapter;
import org.apache.streampipes.connect.adapters.wikipedia.WikipediaNewArticlesAdapter;
import org.apache.streampipes.connect.init.AdapterDeclarerSingleton;
import org.apache.streampipes.connect.protocol.set.FileProtocol;
import org.apache.streampipes.connect.protocol.set.HttpProtocol;
import org.apache.streampipes.connect.protocol.stream.*;
import org.apache.streampipes.connect.protocol.stream.pulsar.PulsarProtocol;
import org.apache.streampipes.container.extensions.ExtensionsModelSubmitter;
import org.apache.streampipes.container.init.DeclarersSingleton;
import org.apache.streampipes.extensions.all.jvm.config.AllExtensionsConfig;
import org.apache.streampipes.processors.enricher.jvm.processor.jseval.JSEvalController;
import org.apache.streampipes.processors.enricher.jvm.processor.sizemeasure.SizeMeasureController;
import org.apache.streampipes.processors.filters.jvm.processor.compose.ComposeController;
import org.apache.streampipes.processors.filters.jvm.processor.enrich.MergeByEnrichController;
import org.apache.streampipes.processors.filters.jvm.processor.limit.RateLimitController;
import org.apache.streampipes.processors.filters.jvm.processor.merge.MergeByTimeController;
import org.apache.streampipes.processors.filters.jvm.processor.numericalfilter.NumericalFilterController;
import org.apache.streampipes.processors.filters.jvm.processor.numericaltextfilter.NumericalTextFilterController;
import org.apache.streampipes.processors.filters.jvm.processor.projection.ProjectionController;
import org.apache.streampipes.processors.filters.jvm.processor.textfilter.TextFilterController;
import org.apache.streampipes.processors.filters.jvm.processor.threshold.ThresholdDetectionController;
import org.apache.streampipes.processors.geo.jvm.jts.processor.latLngToGeo.LatLngToGeoController;
import org.apache.streampipes.processors.geo.jvm.jts.processor.setEPSG.SetEpsgController;
import org.apache.streampipes.processors.geo.jvm.processor.distancecalculator.DistanceCalculatorController;
import org.apache.streampipes.processors.geo.jvm.processor.geocoder.GoogleMapsGeocodingController;
import org.apache.streampipes.processors.geo.jvm.processor.revgeocoder.ReverseGeocodingController;
import org.apache.streampipes.processors.geo.jvm.processor.speed.SpeedCalculatorController;
import org.apache.streampipes.processors.geo.jvm.processor.staticdistancecalculator.StaticDistanceCalculatorController;
import org.apache.streampipes.processors.geo.jvm.processor.staticgeocoder.StaticGoogleMapsGeocodingController;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.genericclassification.GenericImageClassificationController;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.imagecropper.ImageCropperController;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.imageenrichment.ImageEnrichmentController;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.qrreader.QrCodeReaderController;
import org.apache.streampipes.processors.siddhi.frequency.FrequencyController;
import org.apache.streampipes.processors.siddhi.frequencychange.FrequencyChangeController;
import org.apache.streampipes.processors.siddhi.stop.StreamStopController;
import org.apache.streampipes.processors.siddhi.trend.TrendController;
import org.apache.streampipes.processors.textmining.jvm.processor.chunker.ChunkerController;
import org.apache.streampipes.processors.textmining.jvm.processor.language.LanguageDetectionController;
import org.apache.streampipes.processors.textmining.jvm.processor.namefinder.NameFinderController;
import org.apache.streampipes.processors.textmining.jvm.processor.partofspeech.PartOfSpeechController;
import org.apache.streampipes.processors.textmining.jvm.processor.sentencedetection.SentenceDetectionController;
import org.apache.streampipes.processors.textmining.jvm.processor.tokenizer.TokenizerController;
import org.apache.streampipes.processors.transformation.jvm.processor.array.count.CountArrayController;
import org.apache.streampipes.processors.transformation.jvm.processor.array.split.SplitArrayController;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.counter.BooleanCounterController;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.edge.SignalEdgeFilterController;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.inverter.BooleanInverterController;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.state.BooleanToStateController;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.timekeeping.BooleanTimekeepingController;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.timer.BooleanTimerController;
import org.apache.streampipes.processors.transformation.jvm.processor.csvmetadata.CsvMetadataEnrichmentController;
import org.apache.streampipes.processors.transformation.jvm.processor.state.buffer.StateBufferController;
import org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.buffer.StateBufferLabelerController;
import org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.number.NumberLabelerController;
import org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.counter.StringCounterController;
import org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.state.StringToStateController;
import org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.timer.StringTimerController;
import org.apache.streampipes.processors.transformation.jvm.processor.task.TaskDurationController;
import org.apache.streampipes.processors.transformation.jvm.processor.timestampextractor.TimestampExtractorController;
import org.apache.streampipes.processors.transformation.jvm.processor.transformtoboolean.TransformToBooleanController;
import org.apache.streampipes.processors.transformation.jvm.processor.value.change.ChangedValueDetectionController;
import org.apache.streampipes.processors.transformation.jvm.processor.value.duration.CalculateDurationController;
import org.apache.streampipes.sinks.brokers.jvm.bufferrest.BufferRestController;
import org.apache.streampipes.sinks.brokers.jvm.jms.JmsController;
import org.apache.streampipes.sinks.brokers.jvm.kafka.KafkaController;
import org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttController;
import org.apache.streampipes.sinks.brokers.jvm.pulsar.PulsarController;
import org.apache.streampipes.sinks.brokers.jvm.rabbitmq.RabbitMqController;
import org.apache.streampipes.sinks.brokers.jvm.rest.RestController;
import org.apache.streampipes.sinks.databases.jvm.couchdb.CouchDbController;
import org.apache.streampipes.sinks.databases.jvm.ditto.DittoController;
import org.apache.streampipes.sinks.databases.jvm.influxdb.InfluxDbController;
import org.apache.streampipes.sinks.databases.jvm.iotdb.IotDbController;
import org.apache.streampipes.sinks.databases.jvm.mysql.MysqlController;
import org.apache.streampipes.sinks.databases.jvm.opcua.UpcUaController;
import org.apache.streampipes.sinks.databases.jvm.postgresql.PostgreSqlController;
import org.apache.streampipes.sinks.databases.jvm.redis.RedisController;
import org.apache.streampipes.sinks.internal.jvm.dashboard.DashboardController;
import org.apache.streampipes.sinks.internal.jvm.datalake.DataLakeController;
import org.apache.streampipes.sinks.internal.jvm.notification.NotificationController;
import org.apache.streampipes.sinks.notifications.jvm.email.EmailController;
import org.apache.streampipes.sinks.notifications.jvm.onesignal.OneSignalController;
import org.apache.streampipes.sinks.notifications.jvm.slack.SlackNotificationController;
import org.apache.streampipes.sinks.notifications.jvm.telegram.TelegramController;


public class AllExtensionsInit extends ExtensionsModelSubmitter {

    public static void main (String[] args) {
        DeclarersSingleton.getInstance()
                // streampipes-processors-enricher-jvm
                .add(new SizeMeasureController())
                .add(new JSEvalController())
                // streampipes-processors-filters-jvm
                .add(new NumericalFilterController())
                .add(new ThresholdDetectionController())
                .add(new TextFilterController())
                .add(new ProjectionController())
                .add(new MergeByEnrichController())
                .add(new MergeByTimeController())
                .add(new RateLimitController())
                .add(new ComposeController())
                .add(new NumericalTextFilterController())
                // streampipes-processors-filers-siddhi
                .add(new TrendController())
                .add(new StreamStopController())
                .add(new FrequencyController())
                .add(new FrequencyChangeController())
                // streampipes-processors-geo-jvm
                .add(new DistanceCalculatorController())
                .add(new GoogleMapsGeocodingController())
                .add(new StaticGoogleMapsGeocodingController())
                .add(new ReverseGeocodingController())
                .add(new SetEpsgController())
                .add(new LatLngToGeoController())
                .add(new SpeedCalculatorController())
                .add(new StaticDistanceCalculatorController())
                // streampipes-processors-image-processing-jvm
                .add(new ImageEnrichmentController())
                .add(new ImageCropperController())
                .add(new QrCodeReaderController())
                .add(new GenericImageClassificationController())
                // streampipes-processors-text-mining-jvm
                .add(new LanguageDetectionController())
                .add(new TokenizerController())
                .add(new PartOfSpeechController())
                .add(new ChunkerController())
                .add(new NameFinderController())
                .add(new SentenceDetectionController())
                // streampipes-processors-transformation-jvm
                .add(new CountArrayController())
                .add(new SplitArrayController())
                .add(new CalculateDurationController())
                .add(new ChangedValueDetectionController())
                .add(new TimestampExtractorController())
                .add(new BooleanCounterController())
                .add(new BooleanInverterController())
                .add(new BooleanTimekeepingController())
                .add(new BooleanTimerController())
                .add(new StateBufferController())
                .add(new StateBufferLabelerController())
                .add(new StringToStateController())
                .add(new SignalEdgeFilterController())
                .add(new BooleanToStateController())
                .add(new CsvMetadataEnrichmentController())
                .add(new TaskDurationController())
                .add(new BooleanInverterController())
                .add(new TransformToBooleanController())
                .add(new StringCounterController())
                .add(new StringTimerController())
                .add(new NumberLabelerController())
                // streampipes-sinks-brokers-jvm
                .add(new KafkaController())
                .add(new JmsController())
                .add(new RestController())
                .add(new BufferRestController())
                .add(new RabbitMqController())
                .add(new PulsarController())
                .add(new MqttController())
                // streampipes-sinks-databases-jvm
                .add(new CouchDbController())
                .add(new InfluxDbController())
                .add(new UpcUaController())
                .add(new PostgreSqlController())
                .add(new IotDbController())
                .add(new DittoController())
                .add(new RedisController())
                .add(new MysqlController())
                // streampipes-sinks-internal-jvm
                .add(new NotificationController())
                .add(new DataLakeController())
                .add(new DashboardController())
                // streampipes-sinks-notifications-jvm
                .add(new EmailController())
                .add(new TelegramController())
                .add(new OneSignalController())
                .add(new SlackNotificationController());

        AdapterDeclarerSingleton.getInstance()
                // Protocols
                .add(new FileProtocol())
                .add(new HttpProtocol())
                .add(new FileStreamProtocol())
                .add(new HDFSProtocol())
                .add(new KafkaProtocol())
                .add(new MqttProtocol())
                .add(new HttpStreamProtocol())
                .add(new PulsarProtocol())
                // Specific Adapters
                .add(new GdeltAdapter())
                .add(new CoindeskBitcoinAdapter())
                .add(new IexCloudNewsAdapter())
                .add(new IexCloudStockAdapter())
                .add(new MySqlStreamAdapter())
                .add(new MySqlSetAdapter())
                .add(new RandomDataSetAdapter())
                .add(new RandomDataStreamAdapter())
                .add(new MachineDataStreamAdapter())
                .add(new SlackAdapter())
                .add(new WikipediaEditedArticlesAdapter())
                .add(new WikipediaNewArticlesAdapter())
                .add(new RosBridgeAdapter())
                .add(new OpcUaAdapter())
                .add(new InfluxDbStreamAdapter())
                .add(new InfluxDbSetAdapter())
                .add(new TISensorTag())
                .add(new NetioRestAdapter())
                .add(new NetioMQTTAdapter())
                .add(new Plc4xS7Adapter())
                .add(new Plc4xModbusAdapter())
                .add(new ImageStreamAdapter())
                .add(new ImageSetAdapter())
                .add(new IssAdapter())
                .add(new FlicMQTTAdapter());

        new AllExtensionsInit().init(AllExtensionsConfig.INSTANCE);
    }
}
