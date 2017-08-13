package com.hiveworkshop.wc3.gui.modeledit.useractions;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.hiveworkshop.wc3.gui.modeledit.CoordinateSystem;
import com.hiveworkshop.wc3.gui.modeledit.actions.newsys.ModelChangeListener;
import com.hiveworkshop.wc3.gui.modeledit.actions.newsys.ModelChangeNotifier;
import com.hiveworkshop.wc3.gui.modeledit.actions.newsys.ScaleComponentAction;
import com.hiveworkshop.wc3.gui.modeledit.selection.SelectionItem;
import com.hiveworkshop.wc3.gui.modeledit.selection.SelectionItemView;
import com.hiveworkshop.wc3.gui.modeledit.selection.SelectionListener;
import com.hiveworkshop.wc3.gui.modeledit.selection.SelectionManager;
import com.hiveworkshop.wc3.gui.modeledit.selection.edits.UniqueComponentSpecificScaling;
import com.hiveworkshop.wc3.gui.modeledit.useractions.MoverWidget.MoveDirection;
import com.hiveworkshop.wc3.gui.modeledit.useractions.RotatorWidget.RotateDirection;
import com.hiveworkshop.wc3.mdl.Vertex;

public class SelectAndRotateActivity extends AbstractSelectAndEditActivity
		implements SelectionListener, ModelChangeListener {
	private RotatorWidget moverWidget = null;
	private CursorManager cursorManager;
	private UndoManager undoManager;
	private Vertex moveActionVector;
	private Vertex moveActionPreviousVector;
	private RotateDirection currentDirection = null;
	private SelectionManager selectionManager;

	@Override
	protected void doMouseMove(final MouseEvent e, final CoordinateSystem coordinateSystem,
			final SelectionManager selectionManager) {
		if (moverWidget != null) {
			final RotateDirection directionByMouse = moverWidget.getDirectionByMouse(e.getPoint(), coordinateSystem);
			if (directionByMouse != RotatorWidget.RotateDirection.NONE) {
				cursorManager.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				moverWidget.setMoveDirection(directionByMouse);
			} else {
				// cursorManager.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}

	@Override
	protected void doDrag(final MouseEvent e, final CoordinateSystem coordinateSystem,
			final SelectionManager selectionManager, final Double startingClick, final Double endingClick) {
		moveActionPreviousVector.setTo(moveActionVector);
		moveActionVector.setCoord(coordinateSystem.getPortFirstXYZ(), endingClick.x - startingClick.x);
		moveActionVector.setCoord(coordinateSystem.getPortSecondXYZ(), endingClick.y - startingClick.y);
		final Vertex v = getCenter(selectionManager.getSelectableItems());
		double cxs = v.getCoord(coordinateSystem.getPortFirstXYZ());
		double cys = v.getCoord(coordinateSystem.getPortSecondXYZ());
		double czs = 0;
		double dxs = moveActionVector.x - cxs;
		double dys = moveActionVector.y - cys;
		final double dzs = 0;
		final double startDist = Math.sqrt(dxs * dxs + dys * dys);
		dxs = moveActionPreviousVector.x - cxs;
		dys = moveActionPreviousVector.y - cys;
		final double endDist = Math.sqrt(dxs * dxs + dys * dys);
		final double distRatio = endDist / startDist;
		cxs = v.getCoord((byte) 0);
		cys = v.getCoord((byte) 1);
		czs = v.getCoord((byte) 2);
		Vertex scaleValues;
		if (currentDirection == MoveDirection.BOTH) {
			scaleValues = new Vertex(distRatio, distRatio, distRatio);
		} else {
			scaleValues = new Vertex(1, 1, 1);
		}
		if (currentDirection != MoverWidget.MoveDirection.UP) {
			scaleValues.setCoord(coordinateSystem.getPortFirstXYZ(), distRatio);
		}
		if (currentDirection != MoverWidget.MoveDirection.RIGHT) {
			scaleValues.setCoord(coordinateSystem.getPortSecondXYZ(), distRatio);
		}
		if (currentDirection == MoveDirection.BOTH) {
			scaleValues.setCoord(coordinateSystem.getPortSecondXYZ(), distRatio);
		}
		final UniqueComponentSpecificScaling callback = new UniqueComponentSpecificScaling().resetValues((float) cxs,
				(float) cys, (float) czs, (float) (scaleValues.x), (float) (scaleValues.y), (float) (scaleValues.z));
		for (final SelectionItem item : selectionManager.getSelection()) {
			item.forEachComponent(callback);
		}
	}

	@Override
	protected void doEndAction(final MouseEvent e, final CoordinateSystem coordinateSystem,
			final SelectionManager selectionManager, final Double startingClick, final Double endingClick) {
		moveActionPreviousVector.setTo(moveActionVector);
		moveActionVector.setCoord(coordinateSystem.getPortFirstXYZ(), endingClick.x - startingClick.x);
		moveActionVector.setCoord(coordinateSystem.getPortSecondXYZ(), endingClick.y - startingClick.y);
		final Vertex v = getCenter(selectionManager.getSelectableItems());
		double cxs = v.getCoord(coordinateSystem.getPortFirstXYZ());
		double cys = v.getCoord(coordinateSystem.getPortSecondXYZ());
		double czs = 0;
		double dxs = moveActionVector.x - cxs;
		double dys = moveActionVector.y - cys;
		final double dzs = 0;
		final double startDist = Math.sqrt(dxs * dxs + dys * dys);
		dxs = moveActionPreviousVector.x - cxs;
		dys = moveActionPreviousVector.y - cys;
		final double endDist = Math.sqrt(dxs * dxs + dys * dys);
		final double distRatio = endDist / startDist;
		cxs = v.getCoord((byte) 0);
		cys = v.getCoord((byte) 1);
		czs = v.getCoord((byte) 2);
		Vertex scaleValues;
		if (currentDirection == MoveDirection.BOTH) {
			scaleValues = new Vertex(distRatio, distRatio, distRatio);
		} else {
			scaleValues = new Vertex(1, 1, 1);
		}
		if (currentDirection != MoverWidget.MoveDirection.UP) {
			scaleValues.setCoord(coordinateSystem.getPortFirstXYZ(), distRatio);
		}
		if (currentDirection != MoverWidget.MoveDirection.RIGHT) {
			scaleValues.setCoord(coordinateSystem.getPortSecondXYZ(), distRatio);
		}
		if (currentDirection == MoveDirection.BOTH) {
			scaleValues.setCoord(coordinateSystem.getPortSecondXYZ(), distRatio);
		}
		final UniqueComponentSpecificScaling callback = new UniqueComponentSpecificScaling().resetValues((float) cxs,
				(float) cys, (float) czs, (float) (scaleValues.x), (float) (scaleValues.y), (float) (scaleValues.z));
		for (final SelectionItem item : selectionManager.getSelection()) {
			item.forEachComponent(callback);
		}
		final ScaleComponentAction modifyAction = new ScaleComponentAction(selectionManager.getSelection(), (float) cxs,
				(float) cys, (float) czs, (float) (scaleValues.x), (float) (scaleValues.y), (float) (scaleValues.z));
		undoManager.pushAction(modifyAction);
	}

	@Override
	protected boolean doStartAction(final MouseEvent e, final CoordinateSystem coordinateSystem,
			final SelectionManager selectionManager, final Double startingClick) {
		boolean doAction = false;
		if (SwingUtilities.isRightMouseButton(e)) {
			currentDirection = MoveDirection.NONE;
			doAction = true;
		} else if ((moverWidget != null && (currentDirection = moverWidget.getDirectionByMouse(e.getPoint(),
				coordinateSystem)) != MoverWidget.MoveDirection.NONE)) {
			doAction = true;
		}
		if (doAction) {
			moveActionVector = new Vertex(0, 0, 0);
			moveActionPreviousVector = new Vertex(0, 0, 0);
			return true;
		}
		return false;
	}

	@Override
	protected void onReset(final SelectionManager selectionManager, final CursorManager cursorManager,
			final CoordinateSystem coordinateSystem, final UndoManager undoManager,
			final ModelChangeNotifier modelChangeNotifier) {
		this.selectionManager = selectionManager;
		modelChangeNotifier.subscribe(this);
		this.cursorManager = cursorManager;
		this.undoManager = undoManager;
		selectionManager.addSelectionListener(this);
	}

	@Override
	protected void onRender(final Graphics2D g, final CoordinateSystem coordinateSystem) {
		if (moverWidget != null) {
			moverWidget.render(g, coordinateSystem);
		}
	}

	@Override
	public void onSelectionChanged(final List<? extends SelectionItemView> previousSelection,
			final List<? extends SelectionItemView> newSelection) {
		if (newSelection.isEmpty()) {
			moverWidget = null;
		} else {
			moverWidget = new MoverWidget(getCenter(newSelection));
		}
	}

	@Override
	public void modelChanged() {
		final List<SelectionItem> selection = selectionManager.getSelection();
		moverWidget = new MoverWidget(getCenter(selection));
	}

	private Vertex getCenter(final List<? extends SelectionItemView> items) {
		final List<Vertex> centers = new ArrayList<>();
		for (final SelectionItemView item : items) {
			centers.add(item.getCenter());
		}
		return Vertex.centerOfGroup(centers);
	}
}