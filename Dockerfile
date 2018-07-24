FROM openjdk:8-jre

RUN curl --silent "https://api.github.com/repos/0xERR0R/meterNG/releases/latest" | grep browser_download_url | cut -d '"' -f 4 | xargs curl -L -o /opt/meterNG.jar --url

WORKDIR /appdata
VOLUME /appdata

ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -jar -Xms32m -Xmx64m /opt/meterNG.jar
