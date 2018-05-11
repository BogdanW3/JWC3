package com.hiveworkshop.wc3.mdl;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hiveworkshop.wc3.gui.modeledit.CoordinateSystem;
import com.hiveworkshop.wc3.mdl.v2.visitor.IdObjectVisitor;
import com.hiveworkshop.wc3.mdx.Node;

/**
 * Write a description of class ObjectId here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public abstract class IdObject implements Named {
	public static final int DEFAULT_CLICK_RADIUS = 8;

	public static enum NodeFlags {
		DONTINHERIT_TRANSLATION("DontInherit { Translation }"),
		DONTINHERIT_SCALING("DontInherit { Scaling }"),
		DONTINHERIT_ROTATION("DontInherit { Rotation }"),
		BILLBOARDED("Billboarded"),
		BILLBOARD_LOCK_X("BillboardLockX"),
		BILLBOARD_LOCK_Y("BillboardLockY"),
		BILLBOARD_LOCK_Z("BillboardLockZ"),
		CAMERA_ANCHORED("CameraAnchored");

		String mdlText;

		NodeFlags(final String str) {
			this.mdlText = str;
		}

		public String getMdlText() {
			return mdlText;
		}

		public static NodeFlags fromId(final int id) {
			return values()[id];
		}
	}

	String name;
	Vertex pivotPoint;
	int objectId = -1;
	int parentId = -1;
	IdObject parent;

	public void setName(final String text) {
		name = text;
	}

	@Override
	public String getName() {
		return name;
	}

	public IdObject() {

	}

	public IdObject(final IdObject host) {
		name = host.name;
		pivotPoint = host.pivotPoint;
		objectId = host.objectId;
		parentId = host.parentId;
		parent = host.parent;
	}

	public static IdObject read(final BufferedReader mdl) {
		return null;
	}

	public abstract void printTo(PrintWriter writer);

	public void setPivotPoint(final Vertex p) {
		pivotPoint = p;
	}

	public void setParent(final IdObject p) {
		parent = p;
	}

	public IdObject copy() {
		return null;
	}

	public boolean childOf(final IdObject other) {
		if (parent != null) {
			if (parent == other) {
				return true;
			} else {
				return parent.childOf(other);
			}
		}
		return false;
	}

	public abstract double getClickRadius(CoordinateSystem coordinateSystem);

	public boolean parentOf(final IdObject other, final HashMap<IdObject, ArrayList<IdObject>> childMap) {
		final ArrayList<IdObject> children = childMap.get(this);
		if (children != null) {
			if (children.contains(other)) {
				return true;
			} else {
				boolean deepChild = false;
				for (int i = 0; !deepChild && i < children.size(); i++) {
					deepChild = children.get(i).parentOf(other, childMap);
				}
				return deepChild;
			}
		}
		return false;
	}

	public ArrayList<IdObject> getAllChildren(final HashMap<IdObject, ArrayList<IdObject>> childMap) {
		final ArrayList<IdObject> children = childMap.get(this);
		final ArrayList<IdObject> allChildren = new ArrayList<>();
		if (children != null) {
			for (int i = 0; i < children.size(); i++) {
				final IdObject child = children.get(i);
				if (!allChildren.contains(child)) {
					allChildren.add(child);
					allChildren.addAll(child.getAllChildren(childMap));
				}
			}
		}

		return allChildren;
	}

	public abstract void flipOver(byte axis);

	/**
	 *
	 *
	 * @return The Object ID
	 * @deprecated Note that all object IDs are deleted and regenerated at save
	 */
	@Deprecated
	public int getObjectId() {
		return objectId;
	}

	/**
	 * @param objectId
	 *            New object ID value
	 * @deprecated Note that all object IDs are deleted and regenerated at save
	 */
	@Deprecated
	public void setObjectId(final int objectId) {
		this.objectId = objectId;
	}

	/**
	 * @return Parent ID
	 * @deprecated Note that all object IDs are deleted and regenerated at save
	 */
	@Deprecated
	public int getParentId() {
		return parentId;
	}

	/**
	 * @param parentId
	 *            new Parent ID
	 * @deprecated IF UNSURE, YOU SHOULD USE setParent(), note that all object IDs are deleted and regenerated at save
	 */
	@Deprecated
	public void setParentId(final int parentId) {
		this.parentId = parentId;
	}

	protected void loadFrom(final Node node) {
		// ----- Convert Base NODE to "IDOBJECT" -----
		name = node.name;
		parentId = node.parentId;
		objectId = node.objectId;
		int shift = 0;
		for (final IdObject.NodeFlags flag : IdObject.NodeFlags.values()) {
			if (((node.flags >> shift) & 1) == 1) {
				add(flag.getMdlText());
			}
			shift++;
		}
		// translations next
		if (node.geosetTranslation != null) {
			add(new AnimFlag(node.geosetTranslation));
		}
		if (node.geosetScaling != null) {
			add(new AnimFlag(node.geosetScaling));
		}
		if (node.geosetRotation != null) {
			add(new AnimFlag(node.geosetRotation));
		}
		// ----- End Base NODE to "IDOBJECT" -----
	}

	public Vertex getPivotPoint() {
		return pivotPoint;
	}

	public IdObject getParent() {
		return parent;
	}

	public abstract void add(AnimFlag af);

	public abstract void add(String flag);

	public abstract List<String> getFlags();

	public abstract List<AnimFlag> getAnimFlags();

	public abstract void apply(IdObjectVisitor visitor);
}
