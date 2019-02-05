ARG ORIGINAL_SELENOID_IMAGE
FROM selenoid/vnc:$ORIGINAL_SELENOID_IMAGE

USER root

RUN apt-get update && \
		apt-get -y install default-jre

COPY target/remote-robot-server.jar /remote-robot-server.jar
COPY docker-images/selenoid/start_remote_robot_server.sh /start_remote_robot_server.sh

EXPOSE 5555

### Modify original endtrypoint
RUN \
	# Remove 'wait' command at the end of original entrypoint.sh
	sed -i '/wait/d' /entrypoint.sh && \
	# Add commands to start RemoteRobotServer
	cat entrypoint.sh start_remote_robot_server.sh >out && mv out entrypoint.sh && \
	# Give permissions to execute
	chmod +x /entrypoint.sh

USER selenium

ENTRYPOINT ["/entrypoint.sh"]
