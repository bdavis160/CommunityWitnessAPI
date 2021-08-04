#!/bin/bash

# Change into project directory
cd /home/aelric_pdx_edu/CommunityWitnessAPI

# Pull down changes from repository
sudo git pull origin main

# Install and restart server
sudo gradle installPackage
sudo systemctl daemon-reload
sudo systemctl restart CommunityWitnessAPI