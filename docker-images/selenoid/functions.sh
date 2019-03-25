#!/usr/bin/env bash

selenoid_images=(
	chrome_71.0
	chrome_72.0

	firefox_64.0
)


function generate_image_name(){
	image_name='remoterobot/selenoid_'$1':'$2
}

#############################################
# Build docker image
# Parameters:
# $1 - tag of 'selenoid/vnc' image
# $2 - tag of remoterobot image
function build_image() {
	generate_image_name $1 $2
	echo "### Build '"$image_name"' Docker image..."
	docker build -f docker-images/selenoid/Dockerfile --build-arg ORIGINAL_SELENOID_IMAGE=$1 -t $image_name .
}

#############################################
# Deploy docker image
# Parameters:
# $1 - image to deploy
function deploy_image(){
	echo "### Deploy '"$image_name"' Docker image..."
	docker push $image_name
}

### Build all docker images
function build_all_images(){
	echo "### Build all docker images ###"
	for base_image in "${selenoid_images[@]}"
	do
		build_image $base_image $VERSION
	done
}

### Deploy all docker images
function deploy_all_images(){
	echo "### Deploy all docker images ###"
	for base_image in "${selenoid_images[@]}"
	do
		generate_image_name $base_image $VERSION
		deploy_image
	done
}




