package com.fuyuaki.r_wilderness.client.model.builder;

import com.fuyuaki.r_wilderness.client.model.block.TreeBlockModelPart;
import com.fuyuaki.r_wilderness.client.model.block.TreeBlockStateModel;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import net.neoforged.neoforge.client.model.generators.blockstate.CustomBlockStateModelBuilder;
import net.neoforged.neoforge.client.model.generators.blockstate.UnbakedMutator;

public class TreeBlockStateModelBuilder extends CustomBlockStateModelBuilder {


    private TreeBlockModelPart.Unbaked model;

    public TreeBlockStateModelBuilder() {}



    @Override
    public CustomBlockStateModelBuilder with(VariantMutator variantMutator) {
        return this;
    }

    @Override
    public TreeBlockStateModelBuilder with(UnbakedMutator unbakedMutator) {
        TreeBlockStateModelBuilder builder = new TreeBlockStateModelBuilder();
        if (this.model != null){
            builder.model = unbakedMutator.apply(new TreeBlockStateModel.Unbaked(this.model)).model();
        }
        return builder;
    }

    @Override
    public CustomUnbakedBlockStateModel toUnbaked() {
        return new TreeBlockStateModel.Unbaked(this.model);
    }
}
