#!/bin/sh
java -cp ../lib/${artifactId}-1.0.0.jar ${packageName}.${appClassName}SqlScript -tableDir ../sql -serviceDir ../sql

