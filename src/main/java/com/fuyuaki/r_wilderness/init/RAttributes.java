package com.fuyuaki.r_wilderness.init;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;


public class RAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, RWildernessMod.MODID);

    public static final Holder<Attribute> BODY_INSULATION = registerAtt("body_insulation",
            () -> new PercentageAttribute(
                    "attributes.r_wilderness.body_insulation",
                    0.0,
                    0.0,
                    1.0
            ));



    private static Holder<Attribute> registerAtt(String name, Supplier<Attribute> type) {
        return ATTRIBUTES.register(name, type);
    }


    public static void init(IEventBus bus){
        ATTRIBUTES.register(bus);
    }

}
