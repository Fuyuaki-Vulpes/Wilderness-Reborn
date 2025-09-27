package com.fuyuaki.r_wilderness.data.generation.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.IModelProviderExtension;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class ModModelProvider implements DataProvider, IModelProviderExtension {
    // COPIED from Biomes O Plenty, Glitchcore & Vanilla Minecraft

    private final PackOutput.PathProvider blockStatePathProvider;
    private final PackOutput.PathProvider itemInfoPathProvider;
    private final PackOutput.PathProvider modelPathProvider;
    private final String modId;
    private final boolean autogenBlockItemInfos;

    public ModModelProvider(PackOutput output, String modId, boolean autogenBlockItemInfos) {
        this.blockStatePathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.itemInfoPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        this.modelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
        this.modId = modId;
        this.autogenBlockItemInfos = autogenBlockItemInfos;
    }

    public ModModelProvider(PackOutput output) {
        this(output, MODID, true);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        ItemInfoCollector itemInfoCollector = new ItemInfoCollector();
        BlockStateGeneratorCollector blockStateGeneratorCollector = new BlockStateGeneratorCollector();
        SimpleModelCollector simpleModelCollector = new SimpleModelCollector();

        new GenBlockModels(blockStateGeneratorCollector, itemInfoCollector, simpleModelCollector).run();

        new GenItemModels(itemInfoCollector, simpleModelCollector).run();

        itemInfoCollector.finalizeAndValidate();
        return CompletableFuture.allOf(
                blockStateGeneratorCollector.save(output, this.blockStatePathProvider),
                simpleModelCollector.save(output, this.modelPathProvider),
                itemInfoCollector.save(output, this.itemInfoPathProvider)
        );
    }

    static <T> CompletableFuture<?> saveAll(CachedOutput output, Function<T, Path> p_386455_, Map<T, ? extends Supplier<JsonElement>> generators) {
        return DataProvider.saveAll(output, Supplier::get, p_386455_, generators);
    }

    @Override
    public final String getName() {
        return "MTA Model Definitions";
    }

    static class BlockStateGeneratorCollector implements Consumer<BlockModelDefinitionGenerator> {
        private final Map<Block, BlockModelDefinitionGenerator> generators = new HashMap<>();

        public void accept(BlockModelDefinitionGenerator p_388748_) {
            Block block = p_388748_.block();
            BlockModelDefinitionGenerator blockstategenerator = this.generators.put(block, p_388748_);
            if (blockstategenerator != null) {
                throw new IllegalStateException("Duplicate blockstate value for " + block);
            }
        }

        public CompletableFuture<?> save(CachedOutput output, PackOutput.PathProvider pathProvider) {
            Map<Block, BlockModelDefinition> map = Maps.transformValues(this.generators, BlockModelDefinitionGenerator::create);
            Function<Block, Path> function = p_387598_ -> pathProvider.json(p_387598_.builtInRegistryHolder().key().location());
            return DataProvider.saveAll(output, BlockModelDefinition.CODEC, function, map);
        }
    }

    class ItemInfoCollector implements ItemModelOutput {
        private final Map<Item, ClientItem> itemInfos = new HashMap<>();
        private final Map<Item, Item> copies = new HashMap<>();

        @Override
        public void accept(Item item, ItemModel.Unbaked model) {
            this.register(item, new ClientItem(model, ClientItem.Properties.DEFAULT));
        }

        public void register(Item p_388205_, ClientItem p_388233_) {
            ClientItem clientitem = this.itemInfos.put(p_388205_, p_388233_);
            if (clientitem != null) {
                throw new IllegalStateException("Duplicate item model value for " + p_388205_);
            }
        }

        @Override
        public void copy(Item p_386920_, Item p_386789_) {
            this.copies.put(p_386789_, p_386920_);
        }

        public void finalizeAndValidate() {
            if (ModModelProvider.this.autogenBlockItemInfos) {
                BuiltInRegistries.ITEM.listElements().filter(ModModelProvider.this::isModded).forEach(p_388426_ -> {
                    if (!this.copies.containsKey(p_388426_)) {
                        if (p_388426_.value() instanceof BlockItem blockitem && !this.itemInfos.containsKey(blockitem)) {
                            ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(blockitem.getBlock());
                            this.accept(blockitem, ItemModelUtils.plainModel(resourcelocation));
                        }
                    }
                });
            }
            this.copies.forEach((p_386494_, p_386575_) -> {
                ClientItem clientitem = this.itemInfos.get(p_386575_);
                if (clientitem == null) {
                    throw new IllegalStateException("Missing donor: " + p_386575_ + " -> " + p_386494_);
                } else {
                    this.register(p_386494_, clientitem);
                }
            });
        }

        public CompletableFuture<?> save(CachedOutput output, PackOutput.PathProvider pathProvider) {
            return DataProvider.saveAll(
                    output, ClientItem.CODEC, p_388594_ -> pathProvider.json(p_388594_.builtInRegistryHolder().key().location()), this.itemInfos
            );
        }
    }

    static class SimpleModelCollector implements BiConsumer<ResourceLocation, ModelInstance> {
        private final Map<ResourceLocation, ModelInstance> models = new HashMap<>();

        public void accept(ResourceLocation location, ModelInstance instance) {
            Supplier<JsonElement> supplier = this.models.put(location, instance);
            if (supplier != null) {
                throw new IllegalStateException("Duplicate model value for " + location);
            }
        }

        public CompletableFuture<?> save(CachedOutput output, PackOutput.PathProvider pathProvider) {
            return saveAll(output, pathProvider::json, this.models);
        }
    }

    private boolean isModded(Holder.Reference<?> reference) {
        return reference.key().location().getNamespace().equals(this.modId);
    }
}