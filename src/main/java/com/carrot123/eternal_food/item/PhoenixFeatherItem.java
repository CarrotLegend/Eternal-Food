package com.carrot123.eternal_food.item;

import com.carrot123.eternal_food.EternalFood;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public final class PhoenixFeatherItem extends Item implements ICurioItem {
    public static final String CHARM_SLOT = "charm";
    public static final double MAX_HEALTH_BONUS = 0.20D;
    public static final UUID MAX_HEALTH_MODIFIER_UUID =
            UUID.fromString("4cfe86bb-f7d2-46a5-b918-64bceec78518");

    public PhoenixFeatherItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE)
                .fireResistant());
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return slotContext != null
                && CHARM_SLOT.equals(slotContext.identifier())
                && !slotContext.cosmetic();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext,
            UUID slotUuid,
            ItemStack stack
    ) {
        if (slotContext == null
                || slotContext.cosmetic()
                || !CHARM_SLOT.equals(slotContext.identifier())) {
            return ImmutableMultimap.of();
        }
        return ImmutableMultimap.of(
                Attributes.MAX_HEALTH,
                new AttributeModifier(
                        MAX_HEALTH_MODIFIER_UUID,
                        EternalFood.MOD_ID + ":phoenix_feather_max_health",
                        MAX_HEALTH_BONUS,
                        AttributeModifier.Operation.MULTIPLY_TOTAL)
        );
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        tooltip.add(Component.translatable(
                "tooltip.eternal_food.phoenix_feather.rebirth").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(
                "tooltip.eternal_food.phoenix_feather.cooldown").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean canBeHurtBy(net.minecraft.world.damagesource.DamageSource source) {
        return !source.is(DamageTypeTags.IS_EXPLOSION) && super.canBeHurtBy(source);
    }
}

