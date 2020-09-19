#!/usr/bin/env sh

rm flowableserverless

mvn clean compile assembly:single
unzip -o target/flowable-serverless*.jar -d target/flowable-serverless

native-image -H:IncludeResources='META-INF/.*.json|org/apache/commons/logging/.*|org/flowable/eventregistry/db/mapping/mappings.xml'\
             --allow-incomplete-classpath\
             --initialize-at-run-time=org.springframework.core.io.VfsUtils \
             --report-unsupported-elements-at-runtime\
             --initialize-at-build-time\
             -H:+ReportExceptionStackTraces \
             -H:ReflectionConfigurationFiles=graal/app.json,graal/custom-reflect.json,graal/kafka.json\
              -H:+TraceClassInitialization \
             -Dio.netty.noUnsafe=true -H:+ReportUnsupportedElementsAtRuntime\
             -Dio.netty.leakDetection.level=DISABLED \
             -Dfile.encoding=UTF-8\
             -cp .::target/flowable-serverless org.flowable.sample.ServerlessApplication
mv org.flowable.sample.serverlessapplication flowableserverless
