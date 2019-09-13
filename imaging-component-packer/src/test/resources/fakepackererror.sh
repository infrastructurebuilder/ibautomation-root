#!/bin/sh

BASE="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
if [ "$1" == "-version" ]
then
  exit 1
fi;
if [  "$1" == "build" ]
then
  exit 2
fi;
exit 0