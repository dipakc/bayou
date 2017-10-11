#!/bin/bash

# Copyright 2017 Rice University
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

OS="$(uname)"

if [ $OS == "Linux" ]
then

	apt-get update
	apt-get install openjdk-8-jre python3-pip

elif [ $OS == "Darwin" ] # Darwin for Mac OS X
then
	brew update
	brew install python3
	brew cask install java
else
	echo "Unknown OS."
	exit
fi

pip3 install 'flask'
pip3 install 'tensorflow==1.2'
pip3 install 'scikit-learn==0.19'
pip3 install 'scipy'
