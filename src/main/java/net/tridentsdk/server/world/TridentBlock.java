package net.tridentsdk.server.world;

import net.tridentsdk.base.Block;
import net.tridentsdk.base.Position;
import net.tridentsdk.base.Substance;

public class TridentBlock implements Block {
    
    private final Position position;
    private Substance substance;
    
    protected TridentBlock(Position position, Substance substance){
        this.position = position;
        this.substance = substance;
    }
    
    @Override
    public Position position(){
        return position;
    }
    
    @Override
    public Substance substance(){
        return substance;
    }
    
    @Override
    public void setSubstance(Substance substance){
        this.substance = substance;
    
        ((TridentChunk) position.chunk()).set(position.chunkRelative(), substance, (byte) 0);
    }
    
}
