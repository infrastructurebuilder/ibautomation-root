#!/bin/sh

BASE="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
if [ "$1" == "-version" ]
then
  cat $BASE/packer-version-output.txt
fi;
if [  "$1" == "build" ]
then
  exit 2
fi;
exit 0