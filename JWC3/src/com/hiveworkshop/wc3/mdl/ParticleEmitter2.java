package com.hiveworkshop.wc3.mdl;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.hiveworkshop.wc3.gui.modeledit.CoordinateSystem;
import com.hiveworkshop.wc3.mdl.v2.visitor.IdObjectVisitor;
import com.hiveworkshop.wc3.mdx.Node;
import com.hiveworkshop.wc3.mdx.ParticleEmitter2Chunk;

/**
 * ParticleEmitter2 class, these are the things most people would think of as a particle emitter, I think. Blizzard
 * favored use of these over ParticleEmitters and I do too simply because I so often recycle data and there are more of
 * these to use.
 *
 * Eric Theller 3/10/2012 3:32 PM
 */
public class ParticleEmitter2 extends IdObject implements VisibilitySource {
	public static enum TimeDoubles {
		Speed, Variation, Latitude, Gravity, EmissionRate, Width, Length;
	}

	public static enum LoneDoubles {
		LifeSpan, TailLength, Time;
	}

	public static enum LoneInts {
		Rows, Columns, TextureID, ReplaceableId, PriorityPlane;
	}

	public static enum VertexData {
		Alpha, ParticleScaling, LifeSpanUVAnim, DecayUVAnim, TailUVAnim, TailDecayUVAnim;
	}

	static final String[] timeDoubleNames = { "Speed", "Variation", "Latitude", "Gravity", "EmissionRate", "Width",
			"Length" };
	double[] timeDoubleData = new double[timeDoubleNames.length];
	static final String[] loneDoubleNames = { "LifeSpan", "TailLength", "Time" };
	double[] loneDoubleData = new double[loneDoubleNames.length];
	static final String[] loneIntNames = { "Rows", "Columns", "TextureID", "ReplaceableId", "PriorityPlane" };
	int[] loneIntData = new int[loneIntNames.length];
	static final String[] knownFlagNames = { "DontInherit { Rotation }", "DontInherit { Translation }",
			"DontInherit { Scaling }", "SortPrimsFarZ", "Unshaded", "LineEmitter", "Unfogged", "ModelSpace", "XYQuad",
			"Squirt", "Additive", "Modulate2x", "Modulate", "AlphaKey", "Blend", "Tail", "Head", "Both" };
	boolean[] knownFlags = new boolean[knownFlagNames.length];
	static final String[] vertexDataNames = { "Alpha", "ParticleScaling", "LifeSpanUVAnim", "DecayUVAnim", "TailUVAnim",
			"TailDecayUVAnim" };
	Vertex[] vertexData = new Vertex[vertexDataNames.length];

	Vertex[] segmentColor = new Vertex[3];
	ArrayList<AnimFlag> animFlags = new ArrayList<>();

	ArrayList<String> unknownFlags = new ArrayList<>();

	Bitmap texture;

	private ParticleEmitter2() {

	}

	public ParticleEmitter2(final String name) {
		this.name = name;
	}

	public ParticleEmitter2(final ParticleEmitter2Chunk.ParticleEmitter2 emitter) {
		final ParticleEmitter2 mdlEmitter = this;
		// debug print:
		if ((emitter.node.flags & 4096) != 4096) {
			System.err.println("MDX -> MDL error: A particle emitter '" + emitter.node.name
					+ "' not flagged as particle emitter in MDX!");
		}
		// System.out.println(emitter.node.name + ": " +
		// Integer.toBinaryString(emitter.node.flags));
		// ----- Convert Base NODE to "IDOBJECT" -----
		loadFrom(emitter.node);
		// ----- End Base NODE to "IDOBJECT" -----
		// System.out.println(attachment.node.name + ": " +
		// Integer.toBinaryString(attachment.unknownNull));
		final Node node = emitter.node;
		if (emitter.particleEmitter2Visibility != null) {
			add(new AnimFlag(emitter.particleEmitter2Visibility));
		}
		if (((node.flags >> 15) & 1) == 1) {
			add("Unshaded");
		}
		if (((node.flags >> 16) & 1) == 1) {
			add("SortPrimsFarZ");
		}
		if (((node.flags >> 17) & 1) == 1) {
			add("LineEmitter");
		}
		if (((node.flags >> 18) & 1) == 1) {
			add("Unfogged");
		}
		if (((node.flags >> 19) & 1) == 1) {
			add("ModelSpace");
		}
		if (((node.flags >> 20) & 1) == 1) {
			add("XYQuad");
		}
		if (emitter.particleEmitter2Speed != null) {
			add(new AnimFlag(emitter.particleEmitter2Speed));
		} else {
			setSpeed(emitter.speed);
		}
		setVariation(emitter.variation);
		if (emitter.particleEmitter2Latitude != null) {
			add(new AnimFlag(emitter.particleEmitter2Latitude));
		} else {
			setLatitude(emitter.latitude);
		}
		setGravity(emitter.gravity);
		setLifeSpan(emitter.lifespan);
		if (emitter.particleEmitter2EmissionRate != null) {
			add(new AnimFlag(emitter.particleEmitter2EmissionRate));
		} else {
			setEmissionRate(emitter.emissionRate);
		}
		if (emitter.particleEmitter2Length != null) {
			add(new AnimFlag(emitter.particleEmitter2Length));
		} else {
			setLength(emitter.length);
		}
		if (emitter.particleEmitter2Width != null) {
			add(new AnimFlag(emitter.particleEmitter2Width));
		} else {
			setWidth(emitter.width);
		}
		switch (emitter.filterMode) {
		case 0:
			add("Blend");
			break;
		case 1:
			add("Additive");
			break;
		case 2:
			add("Modulate");
			break;
		case 3:
			add("Modulate2x");
			break;
		case 4:
			add("AlphaKey");
			break;
		default:
			System.err.println("Unkown filter mode error");
			add("UnknownFilterMode");
			break;
		}
		setRows(emitter.rows);
		setColumns(emitter.columns);
		switch (emitter.headOrTail) {
		case 0:
			add("Head");
			break;
		case 1:
			add("Tail");
			break;
		case 2:
			add("Both");
			break;
		default:
			System.err.println("Unkown head or tail error");
			add("UnknownHeadOrTail");
			break;
		}
		setTailLength(emitter.tailLength);
		setTime(emitter.time);
		// SegmentColor - Inverse order for MDL!
		for (int i = 0; i < 3; i++) {
			setSegmentColor(i, new Vertex(emitter.segmentColor[i * 3 + 2], emitter.segmentColor[i * 3 + 1],
					emitter.segmentColor[i * 3 + 0]));
		}
		setAlpha(new Vertex((256 + emitter.segmentAlpha[0]) % 256, (256 + emitter.segmentAlpha[1]) % 256,
				(256 + emitter.segmentAlpha[2]) % 256));
		setParticleScaling(new Vertex(emitter.segmentScaling));
		setLifeSpanUVAnim(new Vertex(emitter.headIntervalStart, emitter.headIntervalEnd, emitter.headIntervalRepeat));
		setDecayUVAnim(new Vertex(emitter.headDecayIntervalStart, emitter.headDecayIntervalEnd,
				emitter.headDecayIntervalRepeat));
		setTailUVAnim(new Vertex(emitter.tailIntervalStart, emitter.tailIntervalEnd, emitter.tailIntervalRepeat));
		setTailDecayUVAnim(new Vertex(emitter.tailDecayIntervalStart, emitter.tailDecayIntervalEnd,
				emitter.tailDecayIntervalRepeat));
		setTextureID(emitter.textureId);
		if (emitter.squirt == 1) {
			add("Squirt");
		}
		setPriorityPlane(emitter.priorityPlane);
		setReplaceableId(emitter.replaceableId);

	}

	@Override
	public IdObject copy() {
		final ParticleEmitter2 x = new ParticleEmitter2();

		x.name = name;
		x.pivotPoint = new Vertex(pivotPoint);
		x.objectId = objectId;
		x.parentId = parentId;
		x.parent = parent;

		x.timeDoubleData = timeDoubleData.clone();
		x.loneDoubleData = loneDoubleData.clone();
		x.loneIntData = loneIntData.clone();
		x.knownFlags = knownFlags.clone();
		x.vertexData = vertexData.clone();
		x.segmentColor = segmentColor.clone();

		for (final AnimFlag af : animFlags) {
			x.animFlags.add(new AnimFlag(af));
		}
		unknownFlags = new ArrayList<>(x.unknownFlags);

		x.texture = texture;
		return x;
	}

	public void updateTextureRef(final ArrayList<Bitmap> textures) {
		texture = textures.get(getTextureId());
	}

	public int getTextureId() {
		return loneIntData[2];
	}

	public void setTextureId(final int id) {
		loneIntData[2] = id;
	}

	public static ParticleEmitter2 read(final BufferedReader mdl) {
		String line = MDLReader.nextLine(mdl);
		if (line.contains("ParticleEmitter2")) {
			final ParticleEmitter2 pe = new ParticleEmitter2();
			pe.setName(MDLReader.readName(line));
			MDLReader.mark(mdl);
			line = MDLReader.nextLine(mdl);
			while ((!line.contains("}") || line.contains("},") || line.contains("\t}"))
					&& !line.equals("COMPLETED PARSING")) {
				boolean foundType = false;
				if (line.contains("ObjectId")) {
					// JOptionPane.showMessageDialog(null,"Read an object id!");
					pe.objectId = MDLReader.readInt(line);
					foundType = true;
					// JOptionPane.showMessageDialog(null,"ObjectId from line:
					// "+line);
				} else if (line.contains("Parent")) {
					pe.parentId = MDLReader.splitToInts(line)[0];
					foundType = true;
					// JOptionPane.showMessageDialog(null,"Parent from line:
					// "+line);
					// lit.parent = mdlr.getIdObject(lit.parentId);
				} else if (line.contains("SegmentColor")) {
					boolean reading = true;
					foundType = true;
					// JOptionPane.showMessageDialog(null,"SegmentColor from
					// line: "+line);
					for (int i = 0; reading && i < 3; i++) {
						line = MDLReader.nextLine(mdl);
						if (line.contains("Color")) {
							pe.segmentColor[i] = Vertex.parseText(line);
						} else {
							reading = false;
							MDLReader.reset(mdl);
							line = MDLReader.nextLine(mdl);
						}
					}
					line = MDLReader.nextLine(mdl);
				}
				for (int i = 0; i < vertexDataNames.length && !foundType; i++) {
					if (line.contains("\t" + vertexDataNames[i] + " ")) {
						foundType = true;
						pe.vertexData[i] = Vertex.parseText(line);
						// JOptionPane.showMessageDialog(null,vertexDataNames[i]+"
						// from line: "+line);
					}
				}
				for (int i = 0; i < loneDoubleNames.length && !foundType; i++) {
					if (line.contains(loneDoubleNames[i])) {
						foundType = true;
						pe.loneDoubleData[i] = MDLReader.readDouble(line);
						// JOptionPane.showMessageDialog(null,loneDoubleNames[i]+"
						// from line: "+line);
					}
				}
				for (int i = 0; i < loneIntNames.length && !foundType; i++) {
					if (line.contains(loneIntNames[i])) {
						foundType = true;
						pe.loneIntData[i] = MDLReader.readInt(line);
						// JOptionPane.showMessageDialog(null,loneIntNames[i]+"
						// from line: "+line);
					}
				}
				for (int i = 0; i < knownFlagNames.length && !foundType; i++) {
					if (line.contains(knownFlagNames[i])) {
						foundType = true;
						pe.knownFlags[i] = true;
						// JOptionPane.showMessageDialog(null,knownFlagNames[i]+"
						// from line: "+line);
					}
				}
				for (int i = 0; i < timeDoubleNames.length && !foundType; i++) {
					if (line.contains(timeDoubleNames[i])) {
						foundType = true;
						// JOptionPane.showMessageDialog(null,timeDoubleNames[i]+"
						// from line: "+line);
						if (line.contains("static")) {
							pe.timeDoubleData[i] = MDLReader.readDouble(line);
						} else {
							MDLReader.reset(mdl);
							pe.animFlags.add(AnimFlag.read(mdl));
						}
					}
				}
				if (!foundType && (line.contains("Visibility") || line.contains("Rotation")
						|| line.contains("Translation") || line.contains("Scaling"))) {
					MDLReader.reset(mdl);
					pe.animFlags.add(AnimFlag.read(mdl));
					foundType = true;
					// JOptionPane.showMessageDialog(null,"AnimFlag from line:
					// "+line);
				}
				if (!foundType) {
					// JOptionPane.showMessageDialog(null,"Particle emitter 2
					// did not recognize data at: "+line+"\nThis is probably not
					// a major issue?");
					pe.unknownFlags.add(MDLReader.readFlag(line));
				}
				MDLReader.mark(mdl);
				line = MDLReader.nextLine(mdl);
			}
			return pe;
		} else {
			JOptionPane.showMessageDialog(MDLReader.getDefaultContainer(),
					"Unable to parse ParticleEmitter2: Missing or unrecognized open statement.");
		}
		return null;
	}

	@Override
	public void printTo(final PrintWriter writer) {
		// Remember to update the ids of things before using this
		// -- uses objectId value of idObject superclass
		// -- uses parentId value of idObject superclass
		// -- uses the parent (java Object reference) of idObject superclass
		// -- uses the TextureID value
		final ArrayList<AnimFlag> pAnimFlags = new ArrayList<>(this.animFlags);
		writer.println(MDLReader.getClassName(this.getClass()) + " \"" + getName() + "\" {");
		if (objectId != -1) {
			writer.println("\tObjectId " + objectId + ",");
		}
		if (parentId != -1) {
			writer.println("\tParent " + parentId + ",\t// \"" + parent.getName() + "\"");
		}
		for (int i = 0; i < 9; i++) {
			if (knownFlags[i]) {
				writer.println("\t" + knownFlagNames[i] + ",");
			}
		}
		String currentFlag = "";
		for (int i = 0; i < 4; i++) {
			currentFlag = timeDoubleNames[i];
			if (timeDoubleData[i] != 0) {
				writer.println("\tstatic " + currentFlag + " " + MDLReader.doubleToString(timeDoubleData[i]) + ",");
			} else {
				boolean set = false;
				for (int a = 0; a < pAnimFlags.size() && !set; a++) {
					if (pAnimFlags.get(a).getName().equals(currentFlag)) {
						pAnimFlags.get(a).printTo(writer, 1);
						pAnimFlags.remove(a);
						set = true;
					}
				}
				if (!set) {
					writer.println("\tstatic " + currentFlag + " " + MDLReader.doubleToString(timeDoubleData[i]) + ",");
				}
			}
		}
		currentFlag = "Visibility";
		for (int i = 0; i < pAnimFlags.size(); i++) {
			if (pAnimFlags.get(i).getName().equals(currentFlag)) {
				pAnimFlags.get(i).printTo(writer, 1);
				pAnimFlags.remove(i);
			}
		}
		for (int i = 9; i < 10; i++) {
			if (knownFlags[i]) {
				writer.println("\t" + knownFlagNames[i] + ",");
			}
		}
		for (int i = 0; i < 1; i++) {
			writer.println("\t" + loneDoubleNames[i] + " " + MDLReader.doubleToString(loneDoubleData[i]) + ",");
		}
		for (int i = 4; i < 7; i++) {
			currentFlag = timeDoubleNames[i];
			if (timeDoubleData[i] != 0) {
				writer.println("\tstatic " + currentFlag + " " + MDLReader.doubleToString(timeDoubleData[i]) + ",");
			} else {
				boolean set = false;
				for (int a = 0; a < pAnimFlags.size() && !set; a++) {
					if (pAnimFlags.get(a).getName().equals(currentFlag)) {
						pAnimFlags.get(a).printTo(writer, 1);
						pAnimFlags.remove(a);
						set = true;
					}
				}
				if (!set) {
					writer.println("\tstatic " + currentFlag + " " + MDLReader.doubleToString(timeDoubleData[i]) + ",");
				}
			}
		}
		for (int i = 10; i < 15; i++) {
			if (knownFlags[i]) {
				writer.println("\t" + knownFlagNames[i] + ",");
			}
		}
		for (int i = 0; i < 2; i++) {
			writer.println("\t" + loneIntNames[i] + " " + loneIntData[i] + ",");
		}
		for (int i = 15; i < 18; i++) {
			if (knownFlags[i]) {
				writer.println("\t" + knownFlagNames[i] + ",");
			}
		}
		for (int i = 1; i < 3; i++) {
			writer.println("\t" + loneDoubleNames[i] + " " + MDLReader.doubleToString(loneDoubleData[i]) + ",");
		}
		writer.println("\tSegmentColor {");
		for (int i = 0; i < segmentColor.length; i++) {
			writer.println("\t\tColor " + segmentColor[i].toString() + ",");
		}
		writer.println("\t},");
		for (int i = 0; i < vertexData.length; i++) {
			writer.println("\t" + vertexDataNames[i] + " " + vertexData[i].toStringLessSpace() + ",");
		}
		for (int i = 2; i < 3; i++) {
			writer.println("\t" + loneIntNames[i] + " " + loneIntData[i] + ",");
		}
		for (int i = 3; i < 5; i++) {
			if (loneIntData[i] != 0) {
				writer.println("\t" + loneIntNames[i] + " " + loneIntData[i] + ",");
			}
		}
		for (int i = pAnimFlags.size() - 1; i >= 0; i--) {
			if (pAnimFlags.get(i).getName().equals("Translation")) {
				pAnimFlags.get(i).printTo(writer, 1);
				pAnimFlags.remove(i);
			}
		}
		for (int i = pAnimFlags.size() - 1; i >= 0; i--) {
			if (pAnimFlags.get(i).getName().equals("Rotation")) {
				pAnimFlags.get(i).printTo(writer, 1);
				pAnimFlags.remove(i);
			}
		}
		for (int i = pAnimFlags.size() - 1; i >= 0; i--) {
			if (pAnimFlags.get(i).getName().equals("Scaling")) {
				pAnimFlags.get(i).printTo(writer, 1);
				pAnimFlags.remove(i);
			}
		}
		for (int i = 0; i < unknownFlags.size(); i++) {
			writer.println("\t" + unknownFlags.get(i) + ",");
		}
		writer.println("}");
	}

	// VisibilitySource methods
	@Override
	public void setVisibilityFlag(final AnimFlag flag) {
		int count = 0;
		int index = 0;
		for (int i = 0; i < animFlags.size(); i++) {
			final AnimFlag af = animFlags.get(i);
			if (af.getName().equals("Visibility") || af.getName().equals("Alpha")) {
				count++;
				index = i;
				animFlags.remove(af);
			}
		}
		if (flag != null) {
			animFlags.add(index, flag);
		}
		if (count > 1) {
			JOptionPane.showMessageDialog(null,
					"Some visiblity animation data was lost unexpectedly during overwrite in " + getName() + ".");
		}
	}

	@Override
	public AnimFlag getVisibilityFlag() {
		int count = 0;
		AnimFlag output = null;
		for (final AnimFlag af : animFlags) {
			if (af.getName().equals("Visibility") || af.getName().equals("Alpha")) {
				count++;
				output = af;
			}
		}
		if (count > 1) {
			JOptionPane.showMessageDialog(null,
					"Some visiblity animation data was lost unexpectedly during retrieval in " + getName() + ".");
		}
		return output;
	}

	@Override
	public String visFlagName() {
		return "Visibility";
	}

	@Override
	public void flipOver(final byte axis) {
		final String currentFlag = "Rotation";
		for (int i = 0; i < animFlags.size(); i++) {
			final AnimFlag flag = animFlags.get(i);
			flag.flipOver(axis);
		}
	}

	public double getSpeed() {
		return timeDoubleData[TimeDoubles.Speed.ordinal()];
	}

	public void setSpeed(final double speed) {
		timeDoubleData[TimeDoubles.Speed.ordinal()] = speed;
	}

	public double getVariation() {
		return timeDoubleData[TimeDoubles.Variation.ordinal()];
	}

	public void setVariation(final double variation) {
		timeDoubleData[TimeDoubles.Variation.ordinal()] = variation;
	}

	public double getLatitude() {
		return timeDoubleData[TimeDoubles.Latitude.ordinal()];
	}

	public void setLatitude(final double latitude) {
		timeDoubleData[TimeDoubles.Latitude.ordinal()] = latitude;
	}

	public double getGravity() {
		return timeDoubleData[TimeDoubles.Gravity.ordinal()];
	}

	public void setGravity(final double gravity) {
		timeDoubleData[TimeDoubles.Gravity.ordinal()] = gravity;
	}

	public double getEmissionRate() {
		return timeDoubleData[TimeDoubles.EmissionRate.ordinal()];
	}

	public void setEmissionRate(final double emissionRate) {
		timeDoubleData[TimeDoubles.EmissionRate.ordinal()] = emissionRate;
	}

	public double getWidth() {
		return timeDoubleData[TimeDoubles.Width.ordinal()];
	}

	public void setWidth(final double width) {
		timeDoubleData[TimeDoubles.Width.ordinal()] = width;
	}

	public double getLength() {
		return timeDoubleData[TimeDoubles.Length.ordinal()];
	}

	public void setLength(final double length) {
		timeDoubleData[TimeDoubles.Length.ordinal()] = length;
	}

	public double getLifeSpan() {
		return loneDoubleData[LoneDoubles.LifeSpan.ordinal()];
	}

	public void setLifeSpan(final double lifeSpan) {
		loneDoubleData[LoneDoubles.LifeSpan.ordinal()] = lifeSpan;
	}

	public double getTailLength() {
		return loneDoubleData[LoneDoubles.TailLength.ordinal()];
	}

	public void setTailLength(final double tailLength) {
		loneDoubleData[LoneDoubles.TailLength.ordinal()] = tailLength;
	}

	public double getTime() {
		return loneDoubleData[LoneDoubles.Time.ordinal()];
	}

	public void setTime(final double time) {
		loneDoubleData[LoneDoubles.Time.ordinal()] = time;
	}

	public int getRows() {
		return loneIntData[LoneInts.Rows.ordinal()];
	}

	public void setRows(final int rows) {
		loneIntData[LoneInts.Rows.ordinal()] = rows;
	}

	public int getColumns() {
		return loneIntData[LoneInts.Columns.ordinal()];
	}

	public void setColumns(final int columns) {
		loneIntData[LoneInts.Columns.ordinal()] = columns;
	}

	public int getTextureID() {
		return loneIntData[LoneInts.TextureID.ordinal()];
	}

	public void setTextureID(final int textureID) {
		loneIntData[LoneInts.TextureID.ordinal()] = textureID;
	}

	public int getReplaceableId() {
		return loneIntData[LoneInts.ReplaceableId.ordinal()];
	}

	public void setReplaceableId(final int replaceableId) {
		loneIntData[LoneInts.ReplaceableId.ordinal()] = replaceableId;
	}

	public int getPriorityPlane() {
		return loneIntData[LoneInts.PriorityPlane.ordinal()];
	}

	public void setPriorityPlane(final int priorityPlane) {
		loneIntData[LoneInts.PriorityPlane.ordinal()] = priorityPlane;
	}

	public Vertex getAlpha() {
		return vertexData[VertexData.Alpha.ordinal()];
	}

	public void setAlpha(final Vertex alpha) {
		vertexData[VertexData.Alpha.ordinal()] = alpha;
	}

	public Vertex getParticleScaling() {
		return vertexData[VertexData.ParticleScaling.ordinal()];
	}

	public void setParticleScaling(final Vertex particleScaling) {
		vertexData[VertexData.ParticleScaling.ordinal()] = particleScaling;
	}

	public Vertex getLifeSpanUVAnim() {
		return vertexData[VertexData.LifeSpanUVAnim.ordinal()];
	}

	public void setLifeSpanUVAnim(final Vertex lifeSpanUVAnim) {
		vertexData[VertexData.LifeSpanUVAnim.ordinal()] = lifeSpanUVAnim;
	}

	public Vertex getDecayUVAnim() {
		return vertexData[VertexData.DecayUVAnim.ordinal()];
	}

	public void setDecayUVAnim(final Vertex decayUVAnim) {
		vertexData[VertexData.DecayUVAnim.ordinal()] = decayUVAnim;
	}

	public Vertex getTailUVAnim() {
		return vertexData[VertexData.TailUVAnim.ordinal()];
	}

	public void setTailUVAnim(final Vertex tailUVAnim) {
		vertexData[VertexData.TailUVAnim.ordinal()] = tailUVAnim;
	}

	public Vertex getTailDecayUVAnim() {
		return vertexData[VertexData.TailDecayUVAnim.ordinal()];
	}

	public void setTailDecayUVAnim(final Vertex tailDecayUVAnim) {
		vertexData[VertexData.TailDecayUVAnim.ordinal()] = tailDecayUVAnim;
	}

	@Override
	public void add(final String flag) {
		boolean isKnownFlag = false;
		for (int i = 0; i < knownFlagNames.length && !isKnownFlag; i++) {
			if (knownFlagNames[i].equals(flag)) {
				knownFlags[i] = true;
				isKnownFlag = true;
			}
		}
		if (!isKnownFlag) {
			unknownFlags.add(flag);
		}
	}

	@Override
	public void add(final AnimFlag af) {
		animFlags.add(af);
	}

	public void setSegmentColor(final int index, final Vertex color) {
		segmentColor[index] = color;
	}

	public Vertex getSegmentColor(final int index) {
		return segmentColor[index];
	}

	public int getSegmentColorCount() {
		return segmentColor.length;
	}

	public void setTexture(final Bitmap texture) {
		this.texture = texture;
	}

	@Override
	public List<String> getFlags() {
		final List<String> flags = new ArrayList<>(unknownFlags);
		for (int i = 0; i < knownFlags.length; i++) {
			if (knownFlags[i]) {
				flags.add(knownFlagNames[i]);
			}
		}
		return flags;
	}

	@Override
	public ArrayList<AnimFlag> getAnimFlags() {
		return animFlags;
	}

	@Override
	public void apply(final IdObjectVisitor visitor) {
		visitor.particleEmitter2(this);
	}

	@Override
	public double getClickRadius(final CoordinateSystem coordinateSystem) {
		return DEFAULT_CLICK_RADIUS / CoordinateSystem.Util.getZoom(coordinateSystem);
	}
}
