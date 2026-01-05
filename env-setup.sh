#!/usr/bin/bash

# ImageMagick
sudo apt update
sudo apt install -y fuse libfuse2 libfontconfig1 libx11-6 libharfbuzz0b libfribidi-dev
curl -OL https://imagemagick.org/archive/binaries/magick
chmod +x ./magick
sudo mv ./magick /usr/local/bin/magick
