package com.hiveworkshop.wc3.mdl.v2;

import com.hiveworkshop.wc3.mdl.Bone;
import com.hiveworkshop.wc3.mdl.Camera;
import com.hiveworkshop.wc3.mdl.Geoset;
import com.hiveworkshop.wc3.mdl.IdObject;

public interface ModelViewStateListener {
	void geosetEditable(Geoset geoset);

	void geosetNotEditable(Geoset geoset);

	void geosetVisible(Geoset geoset);

	void geosetNotVisible(Geoset geoset);

	void idObjectVisible(IdObject bone);

	void idObjectNotVisible(Bone bone);

	void cameraVisible(Camera camera);

	void cameraNotVisible(Camera camera);

}