#!/usr/bin/env bash

echo "### Build and deploy docker-images ###"

echo "### Login to DockerHub ###"
echo $DOCKER_HUB_PASSWORD | docker login -u $DOCKER_HUB_USER_NAME --password-stdin

selenoid_images=(
	chrome_71.0
	chrome_72.0

	firefox_64.0
)

for base_image in "${selenoid_images[@]}"
do
	ORIGINAL_SELENOID_IMAGE=$base_image VERSION=${VERSION} docker-images/selenoid/build_and_deploy_image.sh
done
