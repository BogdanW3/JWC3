package com.hiveworkshop.wc3.mdl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.hiveworkshop.wc3.gui.modelviewer.AnimatedRenderEnvironment;

/**
 * For rendering. Copied from ghostwolf's stuff
 *
 * @param forced
 */
public final class RenderModel {
	private final MDL model;
	public static final double MAGIC_RENDER_SHOW_CONSTANT = 0.75;
	private final List<IdObject> sortedNodes = new ArrayList<>();
	private Quaternion inverseCameraRotation;
	private Quaternion inverseCameraRotationYSpin;
	private Quaternion inverseCameraRotationZSpin;
	private AnimatedRenderEnvironment animatedRenderEnvironment;

	private final Map<IdObject, RenderNode> objectToRenderNode = new HashMap<>();

	public RenderModel(final MDL model) {
		this.model = model;
	}

	public RenderNode getRenderNode(final IdObject idObject) {
		return objectToRenderNode.get(idObject);
	}

	public void refreshFromEditor(final AnimatedRenderEnvironment animatedRenderEnvironment,
			final Quaternion inverseCameraRotation, final Quaternion inverseCameraRotationYSpin,
			final Quaternion inverseCameraRotationZSpin) {
		this.animatedRenderEnvironment = animatedRenderEnvironment;
		this.inverseCameraRotation = inverseCameraRotation;
		this.inverseCameraRotationYSpin = inverseCameraRotationYSpin;
		this.inverseCameraRotationZSpin = inverseCameraRotationZSpin;
		sortedNodes.clear();
		setupHierarchy(null);
		for (final IdObject node : sortedNodes) {
			getRenderNode(node).refreshFromEditor();
		}
	}

	private void setupHierarchy(final IdObject parent) {
		for (final IdObject object : model.getIdObjects()) {
			if (object.getParent() == parent) {
				sortedNodes.add(object);
				RenderNode renderNode = objectToRenderNode.get(object);
				if (renderNode == null) {
					renderNode = new RenderNode(this, object);
					objectToRenderNode.put(object, renderNode);
				}
				setupHierarchy(object);
			}
		}
	}

	public void updateNodes(final boolean forced) {
		if (animatedRenderEnvironment == null || animatedRenderEnvironment.getCurrentAnimation() == null) {
			for (final IdObject idObject : sortedNodes) {
				getRenderNode(idObject).resetTransformation();
				getRenderNode(idObject).getWorldMatrix().setIdentity();
			}
			return;
		}
		for (final IdObject idObject : sortedNodes) {
			final RenderNode node = getRenderNode(idObject);
			final IdObject idObjectParent = idObject.getParent();
			final RenderNode parent = idObjectParent == null ? null : getRenderNode(idObjectParent);
			final boolean objectVisible = idObject
					.getRenderVisibility(animatedRenderEnvironment) >= MAGIC_RENDER_SHOW_CONSTANT;
			final boolean nodeVisible = forced || ((parent == null || parent.visible) && objectVisible);

			node.visible = nodeVisible;

			// Every node only needs to be updated if this is a forced update, or if both the parent node and the
			// generic object corresponding to this node are visible.
			// Incoming messy code for optimizations!
			// --- All copied from Ghostwolf
			if (nodeVisible) {
				boolean wasDirty = false;
				// TODO variants
				final Vector3f localLocation = node.localLocation;
				final Quaternion localRotation = node.localRotation;
				final Vector3f localScale = node.localScale;

				// Only update the local data if there is a need to
				if (forced || true /* variants */) {
					wasDirty = true;

					// Translation
					if (forced || true /* variants */) {
						final Vertex renderTranslation = idObject.getRenderTranslation(animatedRenderEnvironment);
						if (renderTranslation != null) {
							localLocation.x = (float) renderTranslation.x;
							localLocation.y = (float) renderTranslation.y;
							localLocation.z = (float) renderTranslation.z;
						}
					}

					// Rotation
					if (forced || true /* variants */) {
						final QuaternionRotation renderRotation = idObject.getRenderRotation(animatedRenderEnvironment);
						if (renderRotation != null) {
							localRotation.x = (float) renderRotation.a;
							localRotation.y = (float) renderRotation.b;
							localRotation.z = (float) renderRotation.c;
							localRotation.w = (float) renderRotation.d;
						}
					}

					// Scale
					if (forced || true /* variants */) {
						final Vertex renderScale = idObject.getRenderScale(animatedRenderEnvironment);
						if (renderScale != null) {
							localScale.x = (float) renderScale.x;
							localScale.y = (float) renderScale.y;
							localScale.z = (float) renderScale.z;
						}
					}
					node.dirty = true;
				}

				// Billboarding
				// If the instance is not attached to any scene, this is meaningless
				if (node.billboarded || node.billboardedX) {
					wasDirty = true;

					// Cancel the parent's rotation;
					if (parent != null) {
						localRotation.set(parent.inverseWorldRotation);
					} else {
						localRotation.setIdentity();
					}

					Quaternion.mul(localRotation, inverseCameraRotation, localRotation);

					// TODO face camera, TODO have a camera
				} else if (node.billboardedY) {
					wasDirty = true;

					// Cancel the parent's rotation;
					localRotation.setIdentity();
					Quaternion.mul(localRotation, inverseCameraRotationYSpin, localRotation);
					if (parent != null) {
						Quaternion.mul(localRotation, localRotation, parent.inverseWorldRotation);
					}

					// TODO face camera, TODO have a camera
				} else if (node.billboardedZ) {
					wasDirty = true;

					// Cancel the parent's rotation;
					if (parent != null) {
						localRotation.set(parent.inverseWorldRotation);
					} else {
						localRotation.setIdentity();
					}

					Quaternion.mul(localRotation, inverseCameraRotationZSpin, localRotation);

					// TODO face camera, TODO have a camera
				}

				final boolean wasReallyDirty = forced || wasDirty || (parent == null || parent.wasDirty);
				node.wasDirty = wasReallyDirty;

				// If this is a forced upate, or this node's local data was updated, or the parent node updated, do
				// a full world update.

				if (wasReallyDirty) {
					node.recalculateTransformation();
				}

				// If there is an instance object associated with this node, and the node is visible (which might
				// not be the case for a forced update!), update the object.
				// This includes attachments and emitters.

				// TODO instanced rendering in 2090
				// let object = node.object;
				if (objectVisible) {
					node.update();
				}

				node.updateChildren();
			}
		}
	}
}