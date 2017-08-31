package com.hiveworkshop.wc3.gui.modeledit.manipulator.activity;

import java.awt.geom.Point2D.Double;

import com.hiveworkshop.wc3.gui.modeledit.CoordinateSystem;
import com.hiveworkshop.wc3.gui.modeledit.manipulator.ModelEditor;
import com.hiveworkshop.wc3.gui.modeledit.manipulator.actions.RotateAction;
import com.hiveworkshop.wc3.gui.modeledit.selection.SelectionView;
import com.hiveworkshop.wc3.gui.modeledit.useractions.UndoActionListener;
import com.hiveworkshop.wc3.mdl.Vertex;

public class BetterRotateVerticalActivityListener extends AbstractBetterActivityListener {
	private final ModelEditor modelEditor;
	private final UndoActionListener undoManager;
	private final SelectionView selectionView;

	public BetterRotateVerticalActivityListener(final ModelEditor modelEditor, final UndoActionListener undoManager,
			final SelectionView selectionView) {
		this.modelEditor = modelEditor;
		this.undoManager = undoManager;
		this.selectionView = selectionView;
	}

	@Override
	public void update(final Double mouseStart, final Double mouseEnd, final byte dim1, final byte dim2) {
		final Vertex center = selectionView.getCenter();
		final double radians = computeRotateRadians(mouseStart, mouseEnd, center, dim1, dim2);
		modelEditor.rotate2d(center.x, center.y, center.z, radians, CoordinateSystem.Util.getUnusedXYZ(dim1, dim2),
				dim2);
	}

	@Override
	public void finish(final Double mouseStart, final Double mouseEnd, final byte dim1, final byte dim2) {
		update(mouseStart, mouseEnd, dim1, dim2);
		final Vertex center = selectionView.getCenter();
		final double radians = computeRotateRadians(activityStart, mouseEnd, center, dim1, dim2);
		undoManager.pushAction(
				new RotateAction(modelEditor, center, radians, CoordinateSystem.Util.getUnusedXYZ(dim1, dim2), dim2));
	}

	private double computeRotateRadians(final Double startingClick, final Double endingClick, final Vertex center,
			final byte portFirstXYZ, final byte portSecondXYZ) {
		final double radius = selectionView.getCircumscribedSphereRadius(center);
		final double deltaAngle = (endingClick.x - startingClick.x) / radius;
		return deltaAngle;
	}

}