
set_version:
	mvn versions:set -DnewVersion=${VERSION}

clean:
	mvn clean

build_commons:
	mvn clean install -pl :commons

build_server:
	mvn clean install -pl :server

build_client:
	mvn clean install -pl :client

build_docker_images:
	./docker-images/job.sh

build_temp_docker_images:
	VERSION=temp ./docker-images/build_selenoid_images.sh

run_test_env:
	docker-compose -f client/src/test/resources/docker-compose.yml down
	docker-compose -f client/src/test/resources/docker-compose.yml up -d

### Full project build
full_build: clean build_commons build_server build_temp_docker_images run_test_env build_client

### Push Selenoid images
# VERSION=X required !
deploy_docker_images:
	./docker-images/build_and_deploy_selenoid_images.sh

### Deploy Java libs
deploy_java_libs:
	mvn deploy -pl :client -Dmaven.test.skip=true
	mvn deploy -pl :commons
	mvn deploy -pl . -Dmaven.test.skip=true





