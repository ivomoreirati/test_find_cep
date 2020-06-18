FROM openjdk:11.0.7-jre-slim-buster
EXPOSE 8080
ADD /target/netshoes_test_find_cep.jar netshoes_test_find_cep.jar
ENTRYPOINT ["java","-jar","netshoes_test_find_cep.jar"]