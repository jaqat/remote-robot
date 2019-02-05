
.PHONY: build
build:
	mvn clean install -Dmaven.test.skip=true

test_env:
	docker-compose -f selenoid-utils/src/test/resources/docker-compose.yml up -d

run_test:
	mvn clean test
	mvn allure:serve
