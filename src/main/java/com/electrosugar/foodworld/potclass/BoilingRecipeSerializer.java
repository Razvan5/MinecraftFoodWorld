package com.electrosugar.foodworld.potclass;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;
import java.util.Set;

public class BoilingRecipeSerializer<T extends AbstractPotRecipe> extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

    static int MAX_WIDTH = 3;
    static int MAX_HEIGHT = 3;
    public static void setCraftingSize(int width, int height) {
        if (MAX_WIDTH < width) MAX_WIDTH = width;
        if (MAX_HEIGHT < height) MAX_HEIGHT = height;
    }
    private final int cookingTime;
    private final BoilingRecipeSerializer.IFactory<T> factory;

    public BoilingRecipeSerializer(BoilingRecipeSerializer.IFactory<T> p_i50025_1_, int p_i50025_2_) {
        this.cookingTime = p_i50025_2_;
        this.factory = p_i50025_1_;
    }

    private static NonNullList<Ingredient> deserializeIngredients(String[] pattern, Map<String, Ingredient> keys, int patternWidth, int patternHeight) {
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(patternWidth * patternHeight, Ingredient.EMPTY);
        Set<String> set = Sets.newHashSet(keys.keySet());
        set.remove(" ");

        for(int i = 0; i < pattern.length; ++i) {
            for(int j = 0; j < pattern[i].length(); ++j) {
                String s = pattern[i].substring(j, j + 1);
                Ingredient ingredient = keys.get(s);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                }

                set.remove(s);
                nonnulllist.set(j + patternWidth * i, ingredient);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return nonnulllist;
        }
    }

    @VisibleForTesting
    static String[] shrink(String... toShrink) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for(int i1 = 0; i1 < toShrink.length; ++i1) {
            String s = toShrink[i1];
            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);
            j = Math.max(j, j1);
            if (j1 < 0) {
                if (k == i1) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (toShrink.length == l) {
            return new String[0];
        } else {
            String[] astring = new String[toShrink.length - l - k];

            for(int k1 = 0; k1 < astring.length; ++k1) {
                astring[k1] = toShrink[k1 + k].substring(i, j + 1);
            }

            return astring;
        }
    }

    private static int firstNonSpace(String str) {
        int i;
        for(i = 0; i < str.length() && str.charAt(i) == ' '; ++i) {
            ;
        }

        return i;
    }

    private static int lastNonSpace(String str) {
        int i;
        for(i = str.length() - 1; i >= 0 && str.charAt(i) == ' '; --i) {
            ;
        }

        return i;
    }

    private static String[] patternFromJson(JsonArray jsonArr) {
        String[] astring = new String[jsonArr.size()];
        if (astring.length > MAX_HEIGHT) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
        } else if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for(int i = 0; i < astring.length; ++i) {
                String s = JSONUtils.getString(jsonArr.get(i), "pattern[" + i + "]");
                if (s.length() > MAX_WIDTH) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
                }

                if (i > 0 && astring[0].length() != s.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                astring[i] = s;
            }

            return astring;
        }
    }

    /**
     * Returns a key json object as a Java HashMap.
     */
    private static Map<String, Ingredient> deserializeKey(JsonObject json) {
        Map<String, Ingredient> map = Maps.newHashMap();

        for(Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put(entry.getKey(), Ingredient.deserialize(entry.getValue()));
        }

        map.put(" ", Ingredient.EMPTY);
        return map;
    }

    public static ItemStack deserializeItem(JsonObject p_199798_0_) {
        String s = JSONUtils.getString(p_199798_0_, "item");
        Item item = Registry.ITEM.getValue(new ResourceLocation(s)).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown item '" + s + "'");
        });
        if (p_199798_0_.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            int i = JSONUtils.getInt(p_199798_0_, "count", 1);
            return net.minecraftforge.common.crafting.CraftingHelper.getItemStack(p_199798_0_, true);
        }
    }

    public static FluidStack deserializeFluidStack(JsonObject jsonObject) {
        String s = JSONUtils.getString(jsonObject, "fluid");
        Fluid fluid = Registry.FLUID.getValue(new ResourceLocation(s)).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown item '" + s + "'");
        });
        if (jsonObject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            int i = JSONUtils.getInt(jsonObject, "amount", 1);
            return new FluidStack(fluid,i);
        }
    }

    public T read(ResourceLocation recipeId, JsonObject json) {
        String groupName = JSONUtils.getString(json, "group", "");
        Map<String, Ingredient> map = BoilingRecipeSerializer.deserializeKey(JSONUtils.getJsonObject(json, "key"));
        String[] patterns = BoilingRecipeSerializer.shrink(BoilingRecipeSerializer.patternFromJson(JSONUtils.getJsonArray(json, "pattern")));
        int recipeWidth = patterns[0].length();
        int recipeHeight = patterns.length;
        NonNullList<Ingredient> ingredientList = BoilingRecipeSerializer.deserializeIngredients(patterns, map, recipeWidth, recipeHeight);
        //Forge: Check if primitive string to keep vanilla or a object which can contain a count field.
        if (!json.has("result")) throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
        ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));

        float experience = JSONUtils.getFloat(json, "experience", 0.0F);
        int cookingTime = JSONUtils.getInt(json, "cookingtime", this.cookingTime);
        //fluidTime is the burnTime of fluidItem/tag of fluidItems
        int fluidTime = JSONUtils.getInt(json,"fluidtime",100);
        //fluidItem is the item corresponding to the fluid for easy semi-vanilla automation
        String s = JSONUtils.getString(json, "fluiditem");
        Item item = Registry.ITEM.getValue(new ResourceLocation(s)).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown item '" + s + "'");
        });
        ItemStack fluidItem = new ItemStack(item) ;
//      ItemStack fluidItem = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "fluiditem"));
        //this is only used for the ResourceLocation texture and som other future versions :(
        FluidStack fluidStack = BoilingRecipeSerializer.deserializeFluidStack(JSONUtils.getJsonObject(json,"fluidstack"));
        return this.factory.create(recipeId, groupName ,recipeWidth, recipeHeight, ingredientList, itemstack, experience, cookingTime, fluidTime, fluidItem,fluidStack);
    }

    public T read(ResourceLocation recipeId, PacketBuffer buffer) {
        int recipeWidth = buffer.readVarInt();
        int recipeHeight = buffer.readVarInt();
        String groupName = buffer.readString(32767);
        NonNullList<Ingredient> ingredientNonNullList = NonNullList.withSize(recipeWidth * recipeHeight, Ingredient.EMPTY);

        for(int k = 0; k < ingredientNonNullList.size(); ++k) {
            ingredientNonNullList.set(k, Ingredient.read(buffer));
        }

        ItemStack resultItems = buffer.readItemStack();
        float xpAmount = buffer.readFloat();
        int cookingTime = buffer.readVarInt();
        int fluidTime = buffer.readVarInt();
        ItemStack fluidItem = buffer.readItemStack();
        FluidStack fluidStack = buffer.readFluidStack();
        return this.factory.create(recipeId, groupName,recipeWidth,recipeHeight, ingredientNonNullList, resultItems, xpAmount, cookingTime, fluidTime, fluidItem,fluidStack);
    }

    public void write(PacketBuffer buffer, T recipe) {
        buffer.writeVarInt(recipe.recipeWidth);
        buffer.writeVarInt(recipe.recipeHeight);
        buffer.writeString(recipe.group);

        for(Ingredient ingredient : recipe.ingredientList) {
            ingredient.write(buffer);
        }
        buffer.writeItemStack(recipe.result);
        buffer.writeFloat(recipe.experience);
        buffer.writeVarInt(recipe.cookTime);
        buffer.writeVarInt(recipe.fluidTime);
        buffer.writeItemStack(recipe.fluidItem);
        buffer.writeFluidStack(recipe.fluidStack);
    }

    interface IFactory<T extends AbstractPotRecipe> {
        T create(ResourceLocation type, String id, int recipeWidth, int recipeHeight, NonNullList<Ingredient> ingredientNonNullList, ItemStack boilingResult, float xpAmount, int fuelTime, int fluidTime, ItemStack fluidItem,FluidStack fluidStack);
    }
}
