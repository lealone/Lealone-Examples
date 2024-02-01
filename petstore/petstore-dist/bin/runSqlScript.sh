#!/bin/sh
java -cp ../lib/lealone-petstore-1.0.0.jar com.lealone.examples.petstore.main.PetStoreSqlScript -tableDir ../sql -serviceDir ../sql -srcDir ../src

