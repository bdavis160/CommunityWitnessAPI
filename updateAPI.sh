#!/bin/bash

# Change into project directory
cd ~/CommunityWitnessAPI

# Pull down changes from repository
git pull origin main

# Install and restart server
sudo ./gradlew installPackage
sudo systemctl daemon-reload
sudo systemctl restart CommunityWitnessAPI
