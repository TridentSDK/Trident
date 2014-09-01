/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mojang.api.profiles;

import com.google.gson.Gson;
import com.mojang.api.http.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class HttpProfileRepository implements ProfileRepository {

    // You're not allowed to request more than 100 profiles per go.
    private static final int PROFILES_PER_REQUEST = 100;

    private static final Gson gson = new Gson();
    private final String     agent;
    private final HttpClient client;

    public HttpProfileRepository(String agent) {
        this(agent, BasicHttpClient.getInstance());
    }

    public HttpProfileRepository(String agent, HttpClient client) {
        this.agent = agent;
        this.client = client;
    }

    private static HttpBody getHttpBody(String... namesBatch) {
        return new HttpBody(HttpProfileRepository.gson.toJson(namesBatch));
    }

    @Override
    public Profile[] findProfilesByNames(String... names) {
        List<Profile> profiles = new ArrayList<>();
        try {

            List<HttpHeader> headers = new ArrayList<>();
            headers.add(new HttpHeader("Content-Type", "application/json"));

            int namesCount = names.length;
            int start = 0;
            int i = 0;
            do {
                int end = HttpProfileRepository.PROFILES_PER_REQUEST * (i + 1);
                if (end > namesCount) {
                    end = namesCount;
                }
                String[] namesBatch = Arrays.copyOfRange(names, start, end);
                HttpBody body = HttpProfileRepository.getHttpBody(namesBatch);
                Profile[] result = this.post(this.getProfilesUrl(), body, headers);
                profiles.addAll(Arrays.asList(result));

                start = end;
                i++;
            } while (start < namesCount);
        } catch (Exception e) {
            // TODO: logging and allowing consumer to react?
        }

        return profiles.toArray(new Profile[profiles.size()]);
    }

    private URL getProfilesUrl() throws MalformedURLException {
        // To lookup Minecraft profiles, agent should be "minecraft"
        return new URL("https://api.mojang.com/profiles/" + this.agent);
    }

    private Profile[] post(URL url, HttpBody body, List<HttpHeader> headers) throws IOException {
        String response = this.client.post(url, body, headers);
        return HttpProfileRepository.gson.fromJson(response, Profile[].class);
    }
}
