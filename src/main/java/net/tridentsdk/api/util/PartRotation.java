/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.api.util;

/**
 * Represents the rotation of an Armor Stand part
 *
 * @author TridentSDK Team
 */
public class PartRotation {
    private int rotX;
    private int rotY;
    private int rotZ;

    /**
     * @param rotX Rotation value on the X plane
     * @param rotY Rotation value on the Y plane
     * @param rotZ Rotation value on the Z plane
     */
    public PartRotation(int rotX, int rotY, int rotZ) {
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
    }

    /**
     * Get the rotation on the X plane
     *
     * @return Integer rotation on the X plane
     */
    public int getRotX() {
        return this.rotX;
    }

    /**
     * Set the rotation on the X plane
     *
     * @param rotX rotation on the X plane
     */
    public void setRotX(int rotX) {
        this.rotX = rotX;
    }

    /**
     * Get the rotation on the Y plane
     *
     * @return Integer rotation on the Y plane
     */
    public int getRotY() {
        return this.rotY;
    }

    /**
     * Set the rotation on the Y plane
     *
     * @param rotY rotation on the Y plane
     */
    public void setRotY(int rotY) {
        this.rotY = rotY;
    }

    /**
     * Get the rotation on the Z plane
     *
     * @return Integer rotation on the Z plane
     */
    public int getRotZ() {
        return this.rotZ;
    }

    /**
     * Set the rotation on the Z plane
     *
     * @param rotZ rotation on the Z plane
     */
    public void setRotZ(int rotZ) {
        this.rotZ = rotZ;
    }
}
