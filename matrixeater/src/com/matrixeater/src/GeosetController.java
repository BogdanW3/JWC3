package com.matrixeater.src;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hiveworkshop.wc3.gui.modeledit.MDLDisplay;

/**
 * ViewControl
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class GeosetController extends JPanel implements ActionListener, MouseListener {
	private static final Color BACKGROUND_SPECIAL_COLOR = Color.WHITE;// new
																		// Color(224,
																		// 176,
																		// 255);
	ArrayList<JCheckBox> visibleButtons = new ArrayList<>();
	ArrayList<JCheckBox> editableButtons = new ArrayList<>();
	int size = 0;
	MDLDisplay dispModel;
	JButton hideAll = new JButton("All Hidden");
	JButton showAll = new JButton("All Visible");
	JButton editableAll = new JButton("All Editable");
	JLabel title;
	private final boolean multiColumnConciseMode;

	public GeosetController(final MDLDisplay disp, final boolean multiColumnConciseMode) {
		super();
		dispModel = disp;
		this.multiColumnConciseMode = multiColumnConciseMode;
		setMDLDisplay(disp);
		hideAll.addActionListener(this);
		showAll.addActionListener(this);
		editableAll.addActionListener(this);
	}

	public void setMDLDisplay(final MDLDisplay disp) {
		removeAll();
		visibleButtons.clear();
		editableButtons.clear();
		if (disp != null) {
			final int nGeos = disp.getMDL().getGeosetsSize();
			if (!multiColumnConciseMode) {
				setLayout(new GridLayout(nGeos * 3 + 4, 1));
				add(title = new JLabel(disp.getMDL().getName().split("\\.")[0]));
				add(hideAll);
				add(showAll);
				add(editableAll);
				for (int i = 0; i < nGeos; i++) {
					add(new JLabel("Geoset " + (i + 1) + ":"));
					JCheckBox cb = new JCheckBox("Visible", disp.isGeosetVisible(i));
					cb.addActionListener(this);
					cb.addMouseListener(this);
					add(cb);
					visibleButtons.add(cb);
					cb = new JCheckBox("Editable", disp.isGeosetEditable(i));
					cb.addActionListener(this);
					cb.addMouseListener(this);
					editableButtons.add(cb);
					add(cb);
				}
			} else {
				setBackground(BACKGROUND_SPECIAL_COLOR);
				final GroupLayout layout = new GroupLayout(this);
				setLayout(layout);
				add(title = new JLabel(disp.getMDL().getName().split("\\.")[0]));
				add(hideAll);
				add(showAll);
				// add(editableAll);
				final ParallelGroup horizontalGroup = layout.createParallelGroup();
				horizontalGroup.addComponent(title).addComponent(hideAll).addComponent(editableAll);
				final SequentialGroup verticalGroup = layout.createSequentialGroup();
				verticalGroup.addComponent(title).addComponent(hideAll).addComponent(editableAll);

				final int nPerCol = 25;
				final ParallelGroup[] verticalGroupRows = new ParallelGroup[nPerCol];
				final int nCols = (nGeos - 1) / nPerCol + 1;
				final ParallelGroup[] horizontalGroupCols = new ParallelGroup[nCols];
				final SequentialGroup horizontalColumnsGroup = layout.createSequentialGroup();
				horizontalGroup.addGroup(horizontalColumnsGroup);
				for (int i = 0; i < nGeos; i++) {
					final JCheckBox cb = new JCheckBox((i + 1) + "",
							disp.isGeosetVisible(i) && disp.isGeosetEditable(i));
					cb.setBackground(BACKGROUND_SPECIAL_COLOR);
					final int row = i % nPerCol;
					final int col = i / nPerCol;
					ParallelGroup rowGroup = verticalGroupRows[row];
					if (rowGroup == null) {
						verticalGroupRows[row] = rowGroup = layout.createParallelGroup();
						verticalGroup.addGroup(rowGroup);
					}
					ParallelGroup colGroup = horizontalGroupCols[col];
					if (colGroup == null) {
						horizontalGroupCols[col] = colGroup = layout.createParallelGroup();
						horizontalColumnsGroup.addGroup(colGroup);
					}
					rowGroup.addComponent(cb);
					colGroup.addComponent(cb);
					cb.addActionListener(this);
					cb.addMouseListener(this);
					add(cb);
					visibleButtons.add(cb);
					editableButtons.add(cb);
				}

				layout.setHorizontalGroup(horizontalGroup);
				layout.setVerticalGroup(verticalGroup);
			}
			size = nGeos;
			dispModel = disp;
			// updateMDLDisplay();
			// frame.pack();
		}
		repaint();
	}

	public void updateBoxCount() {
		// setMDLDisplay(dispModel);
		final MDLDisplay disp = dispModel;
		if (disp != null) {
			final int nGeos = disp.getMDL().getGeosetsSize();
			if (nGeos > size) {
				if (!multiColumnConciseMode) {
					setLayout(new GridLayout(nGeos * 3 + 4, 1));
					title.setText(disp.getMDL().getName().split("\\.")[0]);
					for (int i = size; i < nGeos; i++) {
						add(new JLabel("Geoset " + (i + 1) + ":"));
						JCheckBox cb = new JCheckBox("Visible", disp.isGeosetVisible(i));
						cb.addActionListener(this);
						cb.setSelected(false);
						cb.setSelected(true);
						cb.addMouseListener(this);
						add(cb);
						visibleButtons.add(cb);
						cb = new JCheckBox("Editable", disp.isGeosetEditable(i));
						cb.addActionListener(this);
						cb.setSelected(false);
						cb.setSelected(true);
						cb.addMouseListener(this);
						editableButtons.add(cb);
						add(cb);
					}
				} else {
					final GroupLayout layout = new GroupLayout(this);
					setLayout(layout);
					title.setText(disp.getMDL().getName().split("\\.")[0]);

					// add(editableAll);
					final ParallelGroup horizontalGroup = layout.createParallelGroup();
					horizontalGroup.addComponent(title).addComponent(hideAll).addComponent(editableAll);
					final SequentialGroup verticalGroup = layout.createSequentialGroup();
					verticalGroup.addComponent(title).addComponent(hideAll).addComponent(editableAll);

					final int nPerCol = 25;
					final ParallelGroup[] verticalGroupRows = new ParallelGroup[nPerCol];
					final int nCols = (nGeos - 1) / nPerCol + 1;
					final ParallelGroup[] horizontalGroupCols = new ParallelGroup[nCols];
					final SequentialGroup horizontalColumnsGroup = layout.createSequentialGroup();
					horizontalGroup.addGroup(horizontalColumnsGroup);
					for (int i = 0; i < size; i++) {
						final JCheckBox cb = editableButtons.get(i);
						final int row = i % nPerCol;
						final int col = i / nPerCol;
						ParallelGroup rowGroup = verticalGroupRows[row];
						if (rowGroup == null) {
							verticalGroupRows[row] = rowGroup = layout.createParallelGroup();
							verticalGroup.addGroup(rowGroup);
						}
						ParallelGroup colGroup = horizontalGroupCols[col];
						if (colGroup == null) {
							horizontalGroupCols[col] = colGroup = layout.createParallelGroup();
							horizontalColumnsGroup.addGroup(colGroup);
						}
						rowGroup.addComponent(cb);
						colGroup.addComponent(cb);
					}
					for (int i = size; i < nGeos; i++) {
						final JCheckBox cb = new JCheckBox((i + 1) + "",
								disp.isGeosetVisible(i) && disp.isGeosetEditable(i));
						cb.setBackground(BACKGROUND_SPECIAL_COLOR);
						final int row = i % nPerCol;
						final int col = i / nPerCol;
						ParallelGroup rowGroup = verticalGroupRows[row];
						if (rowGroup == null) {
							verticalGroupRows[row] = rowGroup = layout.createParallelGroup();
							verticalGroup.addGroup(rowGroup);
						}
						ParallelGroup colGroup = horizontalGroupCols[col];
						if (colGroup == null) {
							horizontalGroupCols[col] = colGroup = layout.createParallelGroup();
							horizontalColumnsGroup.addGroup(colGroup);
						}
						rowGroup.addComponent(cb);
						colGroup.addComponent(cb);
						cb.addActionListener(this);
						cb.addMouseListener(this);
						add(cb);
						visibleButtons.add(cb);
						editableButtons.add(cb);
					}

					layout.setHorizontalGroup(horizontalGroup);
					layout.setVerticalGroup(verticalGroup);

				}
			} else if (size > nGeos) {
				for (int i = size - 1; i >= nGeos; i--) {
					remove(visibleButtons.get(i));
					visibleButtons.remove(visibleButtons.get(i));
					remove(this.getComponent(i * 3 + 4));
					remove(editableButtons.get(i));
					editableButtons.remove(editableButtons.get(i));
				}
				setLayout(new GridLayout(nGeos * 3 + 4, 1));
			}
			this.validate();
			size = nGeos;
		}
	}

	public void updateMDLDisplay() {
		for (int i = 0; i < visibleButtons.size(); i++) {
			final JCheckBox cb = visibleButtons.get(i);
			dispModel.makeGeosetVisible(i, cb.isSelected());
		}
		for (int i = 0; i < editableButtons.size(); i++) {
			final JCheckBox cb = editableButtons.get(i);
			dispModel.makeGeosetEditable(i, cb.isSelected());
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		for (int i = 0; i < visibleButtons.size(); i++) {
			final JCheckBox cb = visibleButtons.get(i);
			if (e.getSource() == cb) {
				dispModel.makeGeosetVisible(i, cb.isSelected());
				if (!cb.isSelected()) {
					editableButtons.get(i).setSelected(false);
					dispModel.makeGeosetEditable(i, false);
				}
			}
		}
		for (int i = 0; i < editableButtons.size(); i++) {
			final JCheckBox cb = editableButtons.get(i);
			if (e.getSource() == cb) {
				dispModel.makeGeosetEditable(i, cb.isSelected());
				if (cb.isSelected()) {
					visibleButtons.get(i).setSelected(true);
					dispModel.makeGeosetVisible(i, true);
				}
			}

		}
		if (e.getSource() == hideAll) {
			for (int i = 0; i < visibleButtons.size(); i++) {
				final JCheckBox cb = visibleButtons.get(i);
				if (cb.isSelected()) {
					cb.doClick();
				}
			}
		} else if (e.getSource() == showAll) {
			for (int i = 0; i < visibleButtons.size(); i++) {
				final JCheckBox cb = visibleButtons.get(i);
				if (!cb.isSelected()) {
					cb.doClick();
				}
			}
			for (int i = 0; i < editableButtons.size(); i++) {
				final JCheckBox cb = editableButtons.get(i);
				if (cb.isSelected()) {
					cb.doClick();
				}
			}
		} else if (e.getSource() == editableAll) {
			for (int i = 0; i < editableButtons.size(); i++) {
				final JCheckBox cb = editableButtons.get(i);
				if (!cb.isSelected()) {
					cb.doClick();
				}
			}
		}
		MainFrame.panel.repaint();
		if (dispModel.getUVPanel() != null) {
			dispModel.getUVPanel().repaint();
		}
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
		for (int i = 0; i < visibleButtons.size(); i++) {
			final JCheckBox cb = visibleButtons.get(i);
			if (e.getSource() == cb) {
				dispModel.highlightGeoset(i, true);
			}
		}
		for (int i = 0; i < editableButtons.size(); i++) {
			final JCheckBox cb = editableButtons.get(i);
			if (e.getSource() == cb) {
				dispModel.highlightGeoset(i, true);
			}
		}
		dispModel.notifyUpdate();
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		for (int i = 0; i < visibleButtons.size(); i++) {
			final JCheckBox cb = visibleButtons.get(i);
			if (e.getSource() == cb) {
				dispModel.highlightGeoset(i, false);
			}
		}
		for (int i = 0; i < editableButtons.size(); i++) {
			final JCheckBox cb = editableButtons.get(i);
			if (e.getSource() == cb) {
				dispModel.highlightGeoset(i, false);
			}
		}
		dispModel.notifyUpdate();
	}

	@Override
	public void mousePressed(final MouseEvent e) {

	}

	@Override
	public void mouseReleased(final MouseEvent e) {

	}

	@Override
	public void mouseClicked(final MouseEvent e) {

	}

	@Override
	public void paintComponent(final Graphics g) {
		updateBoxCount();
		super.paintComponent(g);
	}
}
