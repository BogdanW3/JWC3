package com.hiveworkshop.rms.ui;

/*
<classpathentry kind="lib" path="jars/idw-gpl.jar"/>
<classpathentry kind="lib" path="jars/lwjgl-2.9.3.jar"/>
<classpathentry kind="lib" path="jars/lwjgl_util-2.9.3.jar"/>
<classpathentry kind="lib" path="jars/lwjgl-platform-2.9.3-natives-windows.jar"/>
<classpathentry kind="lib" path="jars/JTattoo-1.6.11.jar"/>
<classpathentry kind="lib" path="jars/rsyntaxtextarea-3.0.2.jar"/>
<classpathentry kind="lib" path="jars/miglayout-core-4.2.jar"/>
<classpathentry kind="lib" path="jars/miglayout-swing-4.2.jar"/>
<classpathentry kind="lib" path="jars/blp-iio-plugin.jar"/>
*/

import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.filesystem.GameDataFileSystem;
import com.hiveworkshop.rms.filesystem.sources.DataSourceDescriptor;
import com.hiveworkshop.rms.parsers.blp.BLPHandler;
import com.hiveworkshop.rms.parsers.mdlx.util.MdxUtils;
import com.hiveworkshop.rms.parsers.obj.Build;
import com.hiveworkshop.rms.parsers.slk.DataTable;
import com.hiveworkshop.rms.ui.application.MainPanel;
import com.hiveworkshop.rms.ui.browsers.jworldedit.WEString;
import com.hiveworkshop.rms.ui.browsers.model.ModelOptionPanel;
import com.hiveworkshop.rms.ui.browsers.unit.UnitOptionPanel;
import com.hiveworkshop.rms.ui.icons.RMSIcons;
import com.hiveworkshop.rms.ui.preferences.DataSourceChooserPanel;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.ui.preferences.SaveProfile;
import com.hiveworkshop.rms.ui.util.EditorDisplayManager;
import com.hiveworkshop.rms.ui.util.ExceptionPopup;
import com.owens.oobjloader.parser.Parse;
import net.infonode.gui.laf.InfoNodeLookAndFeel;
import net.infonode.gui.laf.InfoNodeLookAndFeelTheme;
import net.infonode.gui.laf.InfoNodeLookAndFeelThemes;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReterasModelStudioMain extends JFrame {
	public static final Image MAIN_PROGRAM_ICON = new ImageIcon(RMSIcons.loadProgramImage("retera.jpg"))
			.getImage();
	public static ReterasModelStudioMain frame;
	public static MainPanel panel;
	public static JMenuBar menuBar;

	public static MainPanel getPanel() {
		return panel;
	}

	public static void main(final String[] args) throws IOException {
		final boolean hasArgs = args.length >= 1;
		final List<String> startupModelPaths = new ArrayList<>();
		if (hasArgs) {
			if ((args.length > 1) && args[0].equals("-convert")) {
				final String path = args[1];
				final EditableModel model = MdxUtils.loadEditable(new File(path));
				if (path.toLowerCase().endsWith(".mdx")) {
					MdxUtils.saveMdl(model, new File(path.substring(0, path.lastIndexOf('.')) + ".mdl"));
				} else if (path.toLowerCase().endsWith(".mdl")) {
					MdxUtils.saveMdx(model, new File(path.substring(0, path.lastIndexOf('.')) + ".mdx"));
				} else {
					// Unfortunately obj convert does popups right now
					final Build builder = new Build();
					try {
						final Parse obj = new Parse(builder, path);
						final EditableModel mdl = builder.createMDL();
					} catch (final FileNotFoundException e) {
						ExceptionPopup.display(e);
						e.printStackTrace();
					} catch (final IOException e) {
						ExceptionPopup.display(e);
						e.printStackTrace();
					}
				}
			} else {
				if (args[0].endsWith(".mdx") || args[0].endsWith(".mdl") || args[0].endsWith(".blp")
						|| args[0].endsWith(".dds") || args[0].endsWith(".obj")) {
					for (final String arg : args) {
						startupModelPaths.add(arg);
					}
				}
			}
		}
		final boolean dataPromptForced = hasArgs && args[0].equals("-forcedataprompt");
		try {
			LwjglNativesLoader.load();
//		try {
//			MpqCodebase.get().loadMPQ(Paths.get("C:\\Users\\micro\\OneDrive\\Documents\\Warcraft III\\Maps\\Altered Melee\\(6)HFNeonCity.w3x"));
//		} catch (MPQException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
			// try {
			// new File("logs").mkdir();
			// System.setOut(new PrintStream(new File("logs/MatrixEater.log")));
			// System.setErr(new PrintStream(new File("logs/Errors.log")));
			// } catch (final FileNotFoundException e) {
			// e.printStackTrace();
			// ExceptionPopup.display(e);
			// }

			// IIORegistry registry = IIORegistry.getDefaultInstance();
			// registry.registerServiceProvider(
			// new com.realityinteractive.imageio.tga.TGAImageReaderSpi());
			final ProgramPreferences preferences = SaveProfile.get().getPreferences();
			switch (preferences.getTheme()) {
				case DARK:
					EditorDisplayManager.setupLookAndFeel();
					break;
				case HIFI:
					EditorDisplayManager.setupLookAndFeel("HiFi");
					break;
				case ACRYL:
					EditorDisplayManager.setupLookAndFeel("Acryl");
					break;
				case ALUMINIUM:
					EditorDisplayManager.setupLookAndFeel("Aluminium");
					break;
				case FOREST_GREEN:
					try {
						final InfoNodeLookAndFeelTheme theme = new InfoNodeLookAndFeelTheme("Retera Studio",
								new Color(44, 46, 20), new Color(116, 126, 36), new Color(44, 46, 20),
								new Color(220, 202, 132), new Color(116, 126, 36), new Color(220, 202, 132));
						theme.setShadingFactor(-0.8);
						theme.setDesktopColor(new Color(60, 82, 44));

						UIManager.setLookAndFeel(new InfoNodeLookAndFeel(theme));
					} catch (final UnsupportedLookAndFeelException e) {
						e.printStackTrace();
					}
					break;
				case WINDOWS:
					try {
						UIManager.put("desktop", new ColorUIResource(Color.WHITE));
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
						System.out.println(UIManager.getLookAndFeel());
					} catch (final UnsupportedLookAndFeelException e) {
						// handle exception
					} catch (final ClassNotFoundException e) {
						// handle exception
					} catch (final InstantiationException e) {
						// handle exception
					} catch (final IllegalAccessException e) {
						// handle exception
					}
					break;
				case WINDOWS_CLASSIC:
					try {
						UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
					} catch (final Exception exc) {
						try {
							UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
						} catch (final ClassNotFoundException e) {
							e.printStackTrace();
						} catch (final InstantiationException e) {
							e.printStackTrace();
						} catch (final IllegalAccessException e) {
							e.printStackTrace();
						} catch (final UnsupportedLookAndFeelException e) {
							e.printStackTrace();
						}
					}
					break;
				case JAVA_DEFAULT:
//				UIManager.getLookAndFeel().initialize();
//				UIManager.getLookAndFeel().getDefaults().put("TabbedPane.background", Color.GREEN);
//				UIManager.getLookAndFeel().getDefaults().put("InternalFrame.activeTitleBackground", Color.GREEN);
//				UIManager.getLookAndFeel().getDefaults().put("InternalFrame.activeTitleForeground", Color.GREEN);
//				UIManager.getLookAndFeel().getDefaults().put("InternalFrame.inactiveTitleBackground", Color.GREEN);
//				UIManager.getLookAndFeel().getDefaults().put("InternalFrame.inactiveTitleForeground", Color.GREEN);
//				UIManager.getLookAndFeel().getDefaults().put("Button.select", Color.GREEN);
//				UIManager.getLookAndFeel().getDefaults().put("Button.disabledText", Color.GREEN);
//				UIManager.getLookAndFeel().getDefaults().put("ScrollBar.background", Color.GREEN);
//				UIManager.getLookAndFeel().getDefaults().put("ScrollBar.shadow", Color.GREEN);
					break;
				case SOFT_GRAY:
					try {
						final InfoNodeLookAndFeelTheme softGrayTheme = InfoNodeLookAndFeelThemes.getSoftGrayTheme();
						UIManager.setLookAndFeel(new InfoNodeLookAndFeel(softGrayTheme));
					} catch (final Exception exc) {
						try {
							UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
						} catch (final ClassNotFoundException e) {
							e.printStackTrace();
						} catch (final InstantiationException e) {
							e.printStackTrace();
						} catch (final IllegalAccessException e) {
							e.printStackTrace();
						} catch (final UnsupportedLookAndFeelException e) {
							e.printStackTrace();
						}
					}

					break;
				case BLUE_ICE:
					try {
						final InfoNodeLookAndFeelTheme blueIceTheme = InfoNodeLookAndFeelThemes.getBlueIceTheme();
						UIManager.setLookAndFeel(new InfoNodeLookAndFeel(blueIceTheme));
					} catch (final Exception exc) {
						try {
							UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
						} catch (final ClassNotFoundException e) {
							e.printStackTrace();
						} catch (final InstantiationException e) {
							e.printStackTrace();
						} catch (final IllegalAccessException e) {
							e.printStackTrace();
						} catch (final UnsupportedLookAndFeelException e) {
							e.printStackTrace();
						}
					}

					break;
				case DARK_BLUE_GREEN:
					try {
						final InfoNodeLookAndFeelTheme darkBlueGreenTheme = InfoNodeLookAndFeelThemes
								.getDarkBlueGreenTheme();
						UIManager.setLookAndFeel(new InfoNodeLookAndFeel(darkBlueGreenTheme));
					} catch (final Exception exc) {
						try {
							UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
						} catch (final ClassNotFoundException e) {
							e.printStackTrace();
						} catch (final InstantiationException e) {
							e.printStackTrace();
						} catch (final IllegalAccessException e) {
							e.printStackTrace();
						} catch (final UnsupportedLookAndFeelException e) {
							e.printStackTrace();
						}
					}

					break;
				case GRAY:
					try {
						final InfoNodeLookAndFeelTheme grayTheme = InfoNodeLookAndFeelThemes.getGrayTheme();
						UIManager.setLookAndFeel(new InfoNodeLookAndFeel(grayTheme));
					} catch (final Exception exc) {
						try {
							UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
						} catch (final ClassNotFoundException e) {
							e.printStackTrace();
						} catch (final InstantiationException e) {
							e.printStackTrace();
						} catch (final IllegalAccessException e) {
							e.printStackTrace();
						} catch (final UnsupportedLookAndFeelException e) {
							e.printStackTrace();
						}
					}

					break;
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						final List<DataSourceDescriptor> dataSources = SaveProfile.get().getDataSources();
						if ((dataSources == null) || dataPromptForced) {
							final DataSourceChooserPanel dataSourceChooserPanel = new DataSourceChooserPanel(
									dataSources);
//							JF
							final JFrame jFrame = new JFrame("Retera Model Studio: Setup");
//							jFrame.setContentPane(dataSourceChooserPanel);
							jFrame.setUndecorated(true);
							jFrame.pack();
							jFrame.setSize(0, 0);
							jFrame.setLocationRelativeTo(null);
							jFrame.setIconImage(MAIN_PROGRAM_ICON);
							jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							jFrame.setVisible(true);
							try {
								if (JOptionPane.showConfirmDialog(jFrame, dataSourceChooserPanel,
										"Retera Model Studio: Setup", JOptionPane.OK_CANCEL_OPTION,
										JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
									return;
								}
							} finally {
								jFrame.setVisible(false);
							}
							SaveProfile.get().setDataSources(dataSourceChooserPanel.getDataSourceDescriptors());
							SaveProfile.save();
							GameDataFileSystem.refresh(SaveProfile.get().getDataSources());
							// cache priority order...
							UnitOptionPanel.dropRaceCache();
							DataTable.dropCache();
							ModelOptionPanel.dropCache();
							WEString.dropCache();
							BLPHandler.get().dropCache();
						}

						JPopupMenu.setDefaultLightWeightPopupEnabled(false);
						frame = new ReterasModelStudioMain("Retera Model Studio v0.04.2020.08.09 Nightly Build");
						panel.init();
						if (!startupModelPaths.isEmpty()) {
							for (final String path : startupModelPaths) {
								panel.openFile(new File(path));
							}
						}
					} catch (final Throwable th) {
						th.printStackTrace();
						ExceptionPopup.display(th);
						if (!dataPromptForced) {
							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										main(new String[]{"-forcedataprompt"});
									} catch (final IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}).start();
						} else {
							JOptionPane.showMessageDialog(null,
									"Retera Model Studio startup sequence has failed for two attempts. The program will now exit.",
									"Error", JOptionPane.ERROR_MESSAGE);
							System.exit(-1);
						}
					}
				}
			});
		} catch (final Throwable th) {
			th.printStackTrace();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					ExceptionPopup.display(th);
				}
			});
			if (!dataPromptForced) {
				main(new String[]{"-forcedataprompt"});
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(null,
								"Retera Model Studio startup sequence has failed for two attempts. The program will now exit.",
								"Error", JOptionPane.ERROR_MESSAGE);
						System.exit(-1);
					}
				});
			}
		}
	}

	public ReterasModelStudioMain(final String title) {
		super(title);
		// setDefaultCloseOperation(EXIT_ON_CLOSE);

		setBounds(0, 0, 1000, 650);
		panel = new MainPanel();
		setContentPane(panel);
		menuBar = panel.createMenuBar();
		setJMenuBar(menuBar);// MainFrame.class.getResource("ImageBin/DDChicken2.png")
		setIconImage(MAIN_PROGRAM_ICON);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				if (panel.closeAll()) {
					System.exit(0);
				}
			}
		});
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		setLocationRelativeTo(null);
		setVisible(true);
	}
}