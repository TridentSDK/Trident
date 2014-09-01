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

package com.mojang.api.http;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

public interface HttpClient {
    String post(URL url, HttpBody body, List<HttpHeader> headers) throws IOException;

    String post(URL url, Proxy proxy, HttpBody body, List<HttpHeader> headers) throws IOException;
}
