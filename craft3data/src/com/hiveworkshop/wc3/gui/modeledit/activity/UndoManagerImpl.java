package com.hiveworkshop.wc3.gui.modeledit.activity;

import java.util.ArrayDeque;
import java.util.Deque;

import com.hiveworkshop.wc3.gui.modeledit.UndoAction;
import com.hiveworkshop.wc3.gui.modeledit.UndoHandler;

public final class UndoManagerImpl implements UndoManager {
	private final Deque<UndoAction> availableUndoActions;
	private final Deque<UndoAction> availableRedoActions;
	private final UndoHandler undoHandler;

	public UndoManagerImpl(final UndoHandler undoHandler) {
		this.undoHandler = undoHandler;
		this.availableUndoActions = new ArrayDeque<>();
		this.availableRedoActions = new ArrayDeque<>();
	}

	@Override
	public void undo() {
		final UndoAction action = availableUndoActions.pop();
		action.undo();
		availableRedoActions.push(action);
	}

	@Override
	public void redo() {
		final UndoAction action = availableRedoActions.pop();
		action.redo();
		availableUndoActions.push(action);
	}

	@Override
	public void pushAction(final UndoAction action) {
		availableUndoActions.push(action);
		availableRedoActions.clear();
		undoHandler.refreshUndo();
	}

	@Override
	public boolean isUndoListEmpty() {
		return availableUndoActions.isEmpty();
	}

	@Override
	public String getUndoText() {
		if (availableUndoActions.isEmpty()) {
			return "";
		}
		return availableUndoActions.peek().actionName();
	}

	@Override
	public String getRedoText() {
		if (availableRedoActions.isEmpty()) {
			return "";
		}
		return availableRedoActions.peek().actionName();
	}

	@Override
	public boolean isRedoListEmpty() {
		return availableRedoActions.isEmpty();
	}

}