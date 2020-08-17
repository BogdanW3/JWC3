package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;

import com.etheller.warsmash.parsers.mdlx.mdl.MdlTokenInputStream;
import com.etheller.warsmash.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class MdlxLight extends MdlxGenericObject {
	public int type = -1;
	public float[] attenuation = new float[2];
	public float[] color = new float[3];
	public float intensity = 0;
	public float[] ambientColor = new float[3];
	public float ambientIntensity = 0;

	public MdlxLight() {
		super(0x200);
	}

	@Override
	public void readMdx(final LittleEndianDataInputStream stream, final int version) throws IOException {
		final long size = ParseUtils.readUInt32(stream);

		super.readMdx(stream, version);

		this.type = stream.readInt(); // UInt32 in JS
		ParseUtils.readFloatArray(stream, this.attenuation);
		ParseUtils.readFloatArray(stream, this.color);
		this.intensity = stream.readFloat();
		ParseUtils.readFloatArray(stream, this.ambientColor);
		this.ambientIntensity = stream.readFloat();

		readTimelines(stream, size - this.getByteLength(version));
	}

	@Override
	public void writeMdx(final LittleEndianDataOutputStream stream, final int version) throws IOException {
		ParseUtils.writeUInt32(stream, getByteLength(version));

		super.writeMdx(stream, version);

		ParseUtils.writeUInt32(stream, this.type);
		ParseUtils.writeFloatArray(stream, this.attenuation);
		ParseUtils.writeFloatArray(stream, this.color);
		stream.writeFloat(this.intensity);
		ParseUtils.writeFloatArray(stream, this.ambientColor);
		stream.writeFloat(this.ambientIntensity);

		writeNonGenericAnimationChunks(stream);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) throws IOException {
		for (final String token : super.readMdlGeneric(stream)) {
			switch (token) {
			case MdlUtils.TOKEN_OMNIDIRECTIONAL:
				this.type = 0;
				break;
			case MdlUtils.TOKEN_DIRECTIONAL:
				this.type = 1;
				break;
			case MdlUtils.TOKEN_AMBIENT:
				this.type = 2;
				break;
			case MdlUtils.TOKEN_STATIC_ATTENUATION_START:
				this.attenuation[0] = stream.readFloat();
				break;
			case MdlUtils.TOKEN_ATTENUATION_START:
				readTimeline(stream, AnimationMap.KLAS);
				break;
			case MdlUtils.TOKEN_STATIC_ATTENUATION_END:
				this.attenuation[1] = stream.readFloat();
				break;
			case MdlUtils.TOKEN_ATTENUATION_END:
				readTimeline(stream, AnimationMap.KLAE);
				break;
			case MdlUtils.TOKEN_STATIC_INTENSITY:
				this.intensity = stream.readFloat();
				break;
			case MdlUtils.TOKEN_INTENSITY:
				readTimeline(stream, AnimationMap.KLAI);
				break;
			case MdlUtils.TOKEN_STATIC_COLOR:
				stream.readColor(this.color);
				break;
			case MdlUtils.TOKEN_COLOR:
				readTimeline(stream, AnimationMap.KLAC);
				break;
			case MdlUtils.TOKEN_STATIC_AMB_INTENSITY:
				this.ambientIntensity = stream.readFloat();
				break;
			case MdlUtils.TOKEN_AMB_INTENSITY:
				readTimeline(stream, AnimationMap.KLBI);
				break;
			case MdlUtils.TOKEN_STATIC_AMB_COLOR:
				stream.readColor(this.ambientColor);
				break;
			case MdlUtils.TOKEN_AMB_COLOR:
				readTimeline(stream, AnimationMap.KLBC);
				break;
			case MdlUtils.TOKEN_VISIBILITY:
				readTimeline(stream, AnimationMap.KLAV);
				break;
			default:
				throw new IllegalStateException("Unknown token in Light: " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) throws IOException {
		stream.startObjectBlock(MdlUtils.TOKEN_LIGHT, this.name);
		writeGenericHeader(stream);

		switch (this.type) {
		case 0:
			stream.writeFlag(MdlUtils.TOKEN_OMNIDIRECTIONAL);
			break;
		case 1:
			stream.writeFlag(MdlUtils.TOKEN_DIRECTIONAL);
			break;
		case 2:
			stream.writeFlag(MdlUtils.TOKEN_AMBIENT);
			break;
		default:
			throw new IllegalStateException("Unable to save Light of type: " + this.type);
		}

		if (!writeTimeline(stream, AnimationMap.KLAS)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_ATTENUATION_START, this.attenuation[0]);
		}

		if (!writeTimeline(stream, AnimationMap.KLAE)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_ATTENUATION_END, this.attenuation[1]);
		}

		if (!writeTimeline(stream, AnimationMap.KLAI)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_INTENSITY, this.intensity);
		}

		if (!writeTimeline(stream, AnimationMap.KLAC)) {
			stream.writeColor(MdlUtils.TOKEN_STATIC_COLOR, this.color);
		}

		if (!writeTimeline(stream, AnimationMap.KLBI)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_AMB_INTENSITY, this.ambientIntensity);
		}

		if (!writeTimeline(stream, AnimationMap.KLBC)) {
			stream.writeColor(MdlUtils.TOKEN_STATIC_AMB_COLOR, this.ambientColor);
		}

		writeTimeline(stream, AnimationMap.KLAV);

		writeGenericTimelines(stream);
		stream.endBlock();
	}

	@Override
	public long getByteLength(final int version) {
		return 48 + super.getByteLength(version);
	}
}
