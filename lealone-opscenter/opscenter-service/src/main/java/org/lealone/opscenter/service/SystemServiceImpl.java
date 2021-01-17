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
package org.lealone.opscenter.service;

import java.util.ArrayList;

import org.lealone.common.util.CamelCaseHelper;
import org.lealone.db.Database;
import org.lealone.db.LealoneDatabase;
import org.lealone.db.service.Service;
import org.lealone.db.service.ServiceMethod;
import org.lealone.db.table.Column;
import org.lealone.opscenter.service.generated.SystemService;
import org.lealone.orm.json.JsonArray;
import org.lealone.orm.json.JsonObject;

public class SystemServiceImpl implements SystemService {

    @Override
    public String loadServices(String serviceNames) {
        JsonArray ja = new JsonArray();
        Database db = LealoneDatabase.getInstance().getDatabase("opscenter");
        for (String serviceName : serviceNames.split(",")) {
            Service service = Service.getService(null, db, "public", serviceName);
            JsonArray jaMethods = new JsonArray();
            for (ServiceMethod serviceMethod : service.getServiceMethods()) {
                ArrayList<String> parameterNames = new ArrayList<>(serviceMethod.getParameters().size());
                for (Column c : serviceMethod.getParameters()) {
                    parameterNames.add(CamelCaseHelper.toCamelFromUnderscore(c.getName()));
                }
                JsonObject methodJson = new JsonObject();
                methodJson.put("methodName",
                        CamelCaseHelper.toCamelFromUnderscore(serviceMethod.getMethodName().toLowerCase()));
                if (parameterNames.isEmpty())
                    methodJson.put("parameterNames", new JsonArray());
                else
                    methodJson.put("parameterNames", new JsonArray(parameterNames));
                jaMethods.add(methodJson);
            }
            JsonObject serviceJson = new JsonObject();
            serviceJson.put("serviceName", serviceName);
            serviceJson.put("serviceMethods", jaMethods);
            ja.add(serviceJson);
        }
        return ja.encode();
    }

}
