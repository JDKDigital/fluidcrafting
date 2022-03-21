package cy.jdkdigital.fluidcrafting.common.block;

import net.minecraft.world.level.block.Block;

public class Tank extends Block {
    private int size;

    public Tank(int size, Properties properties) {
        super(properties);
        this.size = size;
    }
}
