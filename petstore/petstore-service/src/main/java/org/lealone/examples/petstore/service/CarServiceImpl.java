/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lealone.examples.petstore.service;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lealone.examples.petstore.dal.model.Item;
import org.lealone.examples.petstore.service.generated.CarService;
import org.lealone.orm.json.JsonArray;
import org.lealone.orm.json.JsonObject;

public class CarServiceImpl implements CarService {

    public static class Car {
        CopyOnWriteArrayList<String> items = new CopyOnWriteArrayList<>();
    }

    private final ConcurrentHashMap<String, Car> cars = new ConcurrentHashMap<>();

    @Override
    public void addItem(String carId, String itemId) {
        Car car = new Car();
        Car old = cars.putIfAbsent(carId, car);
        if (old != null)
            car = old;
        car.items.add(itemId);
    }

    @Override
    public void removeItem(String carId, String itemId) {
        Car car = cars.get(carId);
        if (car != null)
            car.items.remove(itemId);
    }

    @Override
    public void update(String carId, String itemId, Integer quantity) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getItems(String carId) {
        JsonObject json = new JsonObject();
        Car car = cars.get(carId);
        if (car == null) {
            json.put("items", new JsonArray());
        } else {
            int size = car.items.size();
            ArrayList<Item> items = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                Item item = Item.dao.where().itemid.eq(car.items.get(i)).findOne();
                items.add(item);
            }
            json.put("items", new JsonArray(items));
        }
        return json.encode();
    }

}
