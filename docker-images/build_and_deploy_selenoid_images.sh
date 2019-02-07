#!/usr/bin/env bash

echo "### Build and deploy docker-images ###"

selenoid_images=(
	chrome_71.0
	chrome_72.0

	firefox_64.0
)

for base_image in "${selenoid_images[@]}"
do
	ORIGINAL_SELENOID_IMAGE=$base_image VERSION=${VERSION} docker-images/selenoid/build_and_deploy_selenoid_images.sh
done
