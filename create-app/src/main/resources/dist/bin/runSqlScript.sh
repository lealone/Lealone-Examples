#!/bin/sh
java -cp ../lib/${artifactId}-1.0.0.jar ${packageName}.Run${appNameCamel}SqlScript -tableDir ../sql -serviceDir ../sql

