#!/usr/bin/env bash
source docker-images/selenoid/functions.sh

echo "### Login to DockerHub ###"
echo $DOCKER_HUB_PASSWORD | docker login -u $DOCKER_HUB_USER_NAME --password-stdin

echo "### Build and deploy docker-images ###"
build_all_images
deploy_all_images
