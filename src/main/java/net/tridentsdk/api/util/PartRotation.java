/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
