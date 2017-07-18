/*
 * Copyright (C) 2002-2016 Sebastiano Vigna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.server.util;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * This class isn't ours but vilsol didn't like this
 * dependency so we decided to ctrl-c/v it instead.
 *
 * Here's what I changed:
 *  - No more docs
 *  - fernflower decompiler
 *  - anything I didn't use I removed
 *  - I copied some stuff from other classes
 *  - Pretty much yeah
 */
public class ShortOpenHashSet {

    int PRIMES[] = { 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 5, 5, 5, 5, 5, 5, 7, 7, 7,
            7, 7, 7, 7, 7, 7, 7, 7, 13, 13, 13, 13, 13, 13, 13, 13, 19, 19, 19, 19, 19,
            19, 19, 19, 19, 19, 19, 19, 31, 31, 31, 31, 31, 31, 31, 43, 43, 43, 43, 43,
            43, 43, 43, 61, 61, 61, 61, 61, 73, 73, 73, 73, 73, 73, 73, 103, 103, 109,
            109, 109, 109, 109, 139, 139, 151, 151, 151, 151, 181, 181, 193, 199, 199,
            199, 229, 241, 241, 241, 271, 283, 283, 313, 313, 313, 349, 349, 349, 349,
            421, 433, 463, 463, 463, 523, 523, 571, 601, 619, 661, 661, 661, 661, 661,
            823, 859, 883, 883, 883, 1021, 1063, 1093, 1153, 1153, 1231, 1321, 1321,
            1429, 1489, 1489, 1621, 1699, 1789, 1873, 1951, 2029, 2131, 2143, 2311,
            2383, 2383, 2593, 2731, 2803, 3001, 3121, 3259, 3391, 3583, 3673, 3919,
            4093, 4273, 4423, 4651, 4801, 5023, 5281, 5521, 5743, 5881, 6301, 6571,
            6871, 7129, 7489, 7759, 8089, 8539, 8863, 9283, 9721, 10141, 10531, 11071,
            11551, 12073, 12613, 13009, 13759, 14323, 14869, 15649, 16363, 17029,
            17839, 18541, 19471, 20233, 21193, 22159, 23059, 24181, 25171, 26263,
            27541, 28753, 30013, 31321, 32719, 34213, 35731, 37309, 38923, 40639,
            42463, 44281, 46309, 48313, 50461, 52711, 55051, 57529, 60091, 62299,
            65521, 68281, 71413, 74611, 77713, 81373, 84979, 88663, 92671, 96739,
            100801, 105529, 109849, 115021, 120079, 125509, 131011, 136861, 142873,
            149251, 155863, 162751, 169891, 177433, 185071, 193381, 202129, 211063,
            220021, 229981, 240349, 250969, 262111, 273643, 285841, 298411, 311713,
            325543, 339841, 355009, 370663, 386989, 404269, 422113, 440809, 460081,
            480463, 501829, 524221, 547399, 571603, 596929, 623353, 651019, 679909,
            709741, 741343, 774133, 808441, 844201, 881539, 920743, 961531, 1004119,
            1048573, 1094923, 1143283, 1193911, 1246963, 1302181, 1359733, 1420039,
            1482853, 1548541, 1616899, 1688413, 1763431, 1841293, 1922773, 2008081,
            2097133, 2189989, 2286883, 2388163, 2493853, 2604013, 2719669, 2840041,
            2965603, 3097123, 3234241, 3377191, 3526933, 3682363, 3845983, 4016041,
            4193803, 4379719, 4573873, 4776223, 4987891, 5208523, 5439223, 5680153,
            5931313, 6194191, 6468463, 6754879, 7053331, 7366069, 7692343, 8032639,
            8388451, 8759953, 9147661, 9552733, 9975193, 10417291, 10878619, 11360203,
            11863153, 12387841, 12936529, 13509343, 14107801, 14732413, 15384673,
            16065559, 16777141, 17519893, 18295633, 19105483, 19951231, 20834689,
            21757291, 22720591, 23726449, 24776953, 25873963, 27018853, 28215619,
            29464579, 30769093, 32131711, 33554011, 35039911, 36591211, 38211163,
            39903121, 41669479, 43514521, 45441199, 47452879, 49553941, 51747991,
            54039079, 56431513, 58930021, 61539091, 64263571, 67108669, 70079959,
            73182409, 76422793, 79806229, 83339383, 87029053, 90881083, 94906249,
            99108043, 103495879, 108077731, 112863013, 117860053, 123078019, 128526943,
            134217439, 140159911, 146365159, 152845393, 159612601, 166679173,
            174058849, 181765093, 189812341, 198216103, 206991601, 216156043,
            225726379, 235720159, 246156271, 257054491, 268435009, 280319203,
            292730833, 305691181, 319225021, 333358513, 348117151, 363529759,
            379624279, 396432481, 413983771, 432312511, 451452613, 471440161,
            492312523, 514109251, 536870839, 560640001, 585461743, 611382451,
            638450569, 666717199, 696235363, 727060069, 759249643, 792864871,
            827967631, 864625033, 902905501, 942880663, 984625531, 1028218189,
            1073741719, 1121280091, 1170923713, 1222764841, 1276901371, 1333434301,
            1392470281, 1454120779, 1518500173, 1585729993, 1655935399, 1729249999,
            1805811253, 1885761133, 1969251079, 2056437379, 2147482951 };

    protected transient short[] key;
    protected transient byte[] state;
    protected final float f;
    protected transient int p;
    protected transient int maxFill;
    protected transient int free;
    protected int count;
    protected transient int growthFactor;

    public ShortOpenHashSet(int n, float f) {
        this.growthFactor = 16;
        if(f > 0.0F && f <= 1.0F) {
            if(n < 0) {
                throw new IllegalArgumentException("Hash table size must be nonnegative");
            } else {
                int l = Arrays.binarySearch(this.PRIMES, (int)((float)n / f) + 1);
                if(l < 0) {
                    l = -l - 1;
                }

                this.free = this.PRIMES[this.p = l];
                this.f = f;
                this.maxFill = (int)((float)this.free * f);
                this.key = new short[this.free];
                this.state = new byte[this.free];
            }
        } else {
            throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
        }
    }

    public ShortOpenHashSet() {
        this(16, 0.75F);
    }

    public static void fill(byte[] array, byte value) {
        for(int i = array.length; i-- != 0; array[i] = value) {
        }
    }

    protected final int findInsertionPoint(short k) {
        short[] key = this.key;
        byte[] state = this.state;
        int n = key.length;
        int k2i = k & 2147483647;
        int h1 = k2i % n;
        int i;
        if(state[h1] == -1 && k != key[h1]) {
            i = k2i % (n - 2) + 1;

            do {
                h1 = (h1 + i) % n;
            } while(state[h1] == -1 && k != key[h1]);
        }

        if(state[h1] == 0) {
            return h1;
        } else if(state[h1] == -1) {
            return -h1 - 1;
        } else {
            i = h1;
            if(k != key[h1]) {
                int h2 = k2i % (n - 2) + 1;

                do {
                    h1 = (h1 + h2) % n;
                } while(state[h1] != 0 && k != key[h1]);
            }

            return state[h1] == -1?-h1 - 1:i;
        }
    }

    protected final int findKey(short k) {
        short[] key = this.key;
        byte[] state = this.state;
        int n = key.length;
        int k2i = k & 2147483647;
        int h1 = k2i % n;
        if(state[h1] != 0 && k != key[h1]) {
            int h2 = k2i % (n - 2) + 1;

            do {
                h1 = (h1 + h2) % n;
            } while(state[h1] != 0 && k != key[h1]);
        }

        return state[h1] == -1?h1:-1;
    }

    public boolean add(short k) {
        int i = this.findInsertionPoint(k);
        if(i < 0) {
            return false;
        } else {
            if(this.state[i] == 0) {
                --this.free;
            }

            this.state[i] = -1;
            this.key[i] = k;
            if(++this.count >= this.maxFill) {
                int newP;
                for(newP = Math.min(this.p + this.growthFactor, this.PRIMES.length - 1); this.PRIMES[newP] == this.PRIMES[this.p]; ++newP) {
                }

                this.rehash(newP);
            }

            if(this.free == 0) {
                this.rehash(this.p);
            }

            return true;
        }
    }

    public boolean remove(short k) {
        int i = this.findKey(k);
        if(i < 0) {
            return false;
        } else {
            this.state[i] = 1;
            --this.count;
            return true;
        }
    }

    public boolean contains(short k) {
        return this.findKey(k) >= 0;
    }

    public void clear() {
        if(this.free != this.state.length) {
            this.free = this.state.length;
            this.count = 0;
            fill(this.state, (byte) 0);
        }
    }

    public SetIterator iterator() {
        return new SetIterator();
    }

    protected void rehash(int newP) {
        int i = 0;
        int j = this.count;
        byte[] state = this.state;
        int newN = this.PRIMES[newP];
        short[] key = this.key;
        short[] newKey = new short[newN];

        byte[] newState;
        for(newState = new byte[newN]; j-- != 0; ++i) {
            while(state[i] != -1) {
                ++i;
            }

            short k = key[i];
            int k2i = k & 2147483647;
            int h1 = k2i % newN;
            if(newState[h1] != 0) {
                int h2 = k2i % (newN - 2) + 1;

                do {
                    h1 = (h1 + h2) % newN;
                } while(newState[h1] != 0);
            }

            newState[h1] = -1;
            newKey[h1] = k;
        }

        this.p = newP;
        this.free = newN - this.count;
        this.maxFill = (int)((float)newN * this.f);
        this.key = newKey;
        this.state = newState;
    }

    public int size() {
        return this.count;
    }

    public boolean isEmpty() {
        return this.count == 0;
    }

    public int hashCode() {
        int h = 0;
        int i = 0;

        for(int j = this.count; j-- != 0; ++i) {
            while(this.state[i] != -1) {
                ++i;
            }

            h += this.key[i];
        }

        return h;
    }

    public class SetIterator {
        int pos;
        int last;
        int c;

        private SetIterator() {
            this.pos = 0;
            this.last = -1;
            this.c = ShortOpenHashSet.this.count;
            byte[] state = ShortOpenHashSet.this.state;
            int n = state.length;
            if(this.c != 0) {
                while(this.pos < n && state[this.pos] != -1) {
                    ++this.pos;
                }
            }

        }

        public boolean hasNext() {
            return this.c != 0 && this.pos < ShortOpenHashSet.this.state.length;
        }

        public short nextShort() {
            byte[] state = ShortOpenHashSet.this.state;
            int n = state.length;
            if(!this.hasNext()) {
                throw new NoSuchElementException();
            } else {
                short retVal = ShortOpenHashSet.this.key[this.last = this.pos];
                if(--this.c != 0) {
                    do {
                        ++this.pos;
                    } while(this.pos < n && state[this.pos] != -1);
                }

                return retVal;
            }
        }

        public void remove() {
            if(this.last != -1 && ShortOpenHashSet.this.state[this.last] == -1) {
                ShortOpenHashSet.this.state[this.last] = 1;
                --ShortOpenHashSet.this.count;
            } else {
                throw new IllegalStateException();
            }
        }
    }
}