FROM arm64v8/openjdk:9-jdk 

COPY . /src
WORKDIR /src

# Download the gcloud package:
RUN curl https://dl.google.com/dl/cloudsdk/release/google-cloud-sdk.tar.gz > /tmp/google-cloud-sdk.tar.gz

# Install the package:
RUN mkdir -p /usr/local/gcloud \
  	&& tar -C /usr/local/gcloud -xvf /tmp/google-cloud-sdk.tar.gz \
  	&& /usr/local/gcloud/google-cloud-sdk/install.sh
  	
ENV PATH $PATH:/usr/local/gcloud/google-cloud-sdk/bin

ENV PORT=8080 \
    K_SERVICE=dev \
    K_CONFIGURATION=dev \
    K_REVISION=dev-00001 \
    GOOGLE_APPLICATION_CREDENTIALS=/src/credentials.json
    
VOLUME $GOOGLE_APPLICATION_CREDENTIALS:/src/credentials.json:ro

CMD ["java","-jar","cs1660-mapreduce.jar"]