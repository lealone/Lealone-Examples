#!/bin/sh
java -cp ../lib/lealone-petstore-1.0.0.jar org.lealone.examples.petstore.main.PetStoreSqlScript -tableDir ../sql -serviceDir ../sql -srcDir ../src

