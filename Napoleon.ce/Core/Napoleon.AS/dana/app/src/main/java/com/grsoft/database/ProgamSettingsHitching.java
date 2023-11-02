package com.grsoft.database;

import android.hardware.Camera;

import com.grsoft.dataobjects.ProgramConfigData;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.CameraHelper;
import com.grsoft.util.Consts;

import java.util.List;

public class ProgamSettingsHitching extends Hitching {

    static final String PHOTO_RES = "photoRes";
    static final String GPS_FREQ = "gpsFrequience";
    static final String GPS_DIST = "gpsDistance";
    static final String GPS_SEND_BK= "dataSendInBackground";
    static final String GPS_SEND_INTERVAL = "gpsSendInterval";
    static final String GPS_WAIT = "waitGpsCoordOnRequest";
    static final String GPS_VALID = "gps_valid_in_org";

    CfgNpl config;

    public ProgamSettingsHitching() {
        super(ProgramConfigData.class, "ProgramSettings");
    }

    @Override
    public void onStart() {
        super.onStart();
        config = (CfgNpl)ConfigManager.getConfig();
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        ProgramConfigData ps = (ProgramConfigData) rawObject.createDataObject(ProgramConfigData.class);
        if(ps.type.equals("pda")) {
            int value = 0;
            try {
                value = Integer.parseInt(ps.value);
                if(ps.id.equals(PHOTO_RES)) {
                    setCameraRes(value);
                } else if(ps.id.equals(GPS_SEND_BK)) {
                    config.dataSendInBackground = value > 0;
                } else if(ps.id.equals(GPS_FREQ)) {
                    config.gpsFrequience = Consts.ONE_SECOND * value;
                } else if(ps.id.equals(GPS_DIST)) {
                    config.gpsDistance = value;
                } else if(ps.id.equals(GPS_SEND_INTERVAL)) {
                    config.gpsSendInterval = value;
                } else if(ps.id.equals(GPS_WAIT)) {
                    config.waitGpsCoordOnRequest = value;
                } else if(ps.id.equals(GPS_VALID)) {
                    config.gps_valid_in_org = Consts.ONE_SECOND * Consts.SEC_PER_MIN * value;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setCameraRes(int value) {
        try {
            Camera camera = Camera.open();
            if(camera != null) {
                Camera.Parameters cameraParameters = camera.getParameters();
                List<Camera.Size> picSize = CameraHelper.getSupportedPictureSizes(cameraParameters);
                Camera.Size cur = null;
                for(Camera.Size cs: picSize) {
                    if(cs.height < value && cs.width < value) {
                        if(cur == null || (cur.height < cs.height || cur.width < cs.width))
                            cur = cs;
                    }
                }
                if(cur != null) {
                    config.cameraHeight = cur.height;
                    config.cameraWidth = cur.width;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnd() {
        super.onEnd();
        ConfigManager.save();
    }
}
