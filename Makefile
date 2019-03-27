
set_version:
	mvn versions:set -DnewVersion=${VERSION}

clean:
	mvn clean

build_parent:
	mvn clean install -pl .

build_commons:
	mvn clean install -pl :commons

build_server:
	mvn clean install -pl :server

build_client:
	mvn clean install -pl :client

build_client_wo_tests:
	mvn clean install -pl :client	-Dmaven.test.skip=true

build_docker_images:
	./docker-images/job.sh

build_temp_docker_images:
	VERSION=temp ./docker-images/build_selenoid_images.sh

run_test_env:
	docker-compose -f client/src/test/resources/docker-compose.yml down
	docker-compose -f client/src/test/resources/docker-compose.yml up -d

### Full project build
full_build: clean build_parent build_commons build_server build_temp_docker_images run_test_env build_client

### Full project build
full_build_wo_tests: clean build_parent build_commons build_server build_client_wo_tests

### Push Selenoid images
# Required environment parameter: VERSION
# Use version started with 'v' symbol (f.e. v0.3)
deploy_docker_images:
	./docker-images/build_and_deploy_selenoid_images.sh

### Full deploy
full_deploy:
	mvn deploy -pl .,commons,client -Dmaven.test.skip=true
