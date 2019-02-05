#!/usr/bin/env bash

IMAGE=remoterobot/selenoid_${ORIGINAL_SELENOID_IMAGE}:${VERSION}

echo "### Build '"${IMAGE}"' Docker image..."
docker build -f docker-images/selenoid/Dockerfile --build-arg ORIGINAL_SELENOID_IMAGE=${ORIGINAL_SELENOID_IMAGE} -t ${IMAGE} .

echo "### Deploy '"${IMAGE}"' Docker image..."
docker push ${IMAGE}
