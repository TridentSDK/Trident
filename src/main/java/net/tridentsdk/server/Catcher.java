/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
package net.tridentsdk.server;

import net.tridentsdk.command.logger.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class Catcher extends PrintStream {
    
    public Catcher(boolean error, Logger LOGGER){
        super(new OutputStream() {
    
            /**
             * StringBuilder can write 100k characters to itself in 1ms, which is
             * faster than System.out and System.err can print to the terminal
             */
            private StringBuilder stack = new StringBuilder();
            
            @Override
            public void write(int b) throws IOException{
                if(b == 10 || b == 13){
                    String result = stack.toString();
                    
                    if(!result.trim().isEmpty()){
                        if(error){
                            LOGGER.error(result);
                        }else{
                            LOGGER.log(result);
                        }
                    }
                    
                    stack = new StringBuilder();
                    return;
                }
                
                stack.append((char) b);
            }
            
        });
    }
    
}
