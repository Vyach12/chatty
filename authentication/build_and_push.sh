./mvnw clean install
docker build . -t vyach12/authentication:0.0.2
docker push vyach12/authentication:0.0.2
