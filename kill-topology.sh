#!/bin/bash

docker exec -it storm_supervisor /apache-storm-1.2.2/bin/storm kill $1
