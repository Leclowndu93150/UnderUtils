package com.leclowndu93150.poweritems.client.model.armor;

import com.leclowndu93150.poweritems.api.ArmorModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class NightVisionGogglesModel {
	public static LayerDefinition createBodyLayer() {
		return ArmorModel.createLayer(32, 32, parts -> parts.getHead()
				.addOrReplaceChild("head", CubeListBuilder.create()
						.texOffs(0, 0).addBox(4.0F, 1.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.75F)),
						PartPose.offset(0, 0, 0)
				)
		);
	}
}
