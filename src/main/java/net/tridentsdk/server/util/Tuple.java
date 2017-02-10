package net.tridentsdk.server.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tuple<A, B> {
    
    private A a;
    private B b;
    
}
