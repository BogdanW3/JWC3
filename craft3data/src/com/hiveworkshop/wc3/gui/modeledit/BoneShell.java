package com.hiveworkshop.wc3.gui.modeledit;

import java.util.ArrayList;
import java.util.List;

import com.hiveworkshop.wc3.mdl.Bone;

public class BoneShell {
	final Bone bone;
	Bone importBone;
	String modelName;
	BonePanel panel;
	boolean showClass = false;

	public BoneShell(final Bone b) {
		bone = b;
	}

	public void setImportBone(final Bone b) {
		importBone = b;
	}

	@Override
	public String toString() {
		if (showClass) {
			if (modelName == null) {
				return bone.getClass().getSimpleName() + " \"" + bone.getName() + "\"";
			} else {
				return modelName + ": " + bone.getClass().getSimpleName() + " \"" + bone.getName() + "\"";
			}
		} else {
			if (modelName == null) {
				return bone.getName();
			} else {
				return modelName + ": " + bone.getName();
			}
		}
	}

	public static List<Bone> toBonesList(final List<BoneShell> boneShells) {
		final List<Bone> bones = new ArrayList<>();
		for (final BoneShell bs : boneShells) {
			bones.add(bs.bone);
		}
		return bones;
	}
}