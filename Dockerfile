FROM yukoff/openjdk-32bit:8-jre-alpine

WORKDIR /app
COPY target/*.jar app.jar
COPY mecalib/ /usr/lib/ 
COPY src/main/java/meca/atlantique/heidenhain/*.py /app/src/main/java/meca/atlantique/heidenhain/

RUN apk add --update python3

# Définir le chemin des bibliothèques natives
ENV LD_LIBRARY_PATH=/usr/lib:$LD_LIBRARY_PATH

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
