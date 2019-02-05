
.PHONY: build
build:
	mvn clean install -Dmaven.test.skip=true

test_env:
<<<<<<< HEAD
	docker-compose -f src/test/resources/docker-compose.yml down
=======
>>>>>>> 2536034... Temp (#1)
	docker-compose -f src/test/resources/docker-compose.yml up -d

run_test:
	mvn clean test
	mvn allure:serve
