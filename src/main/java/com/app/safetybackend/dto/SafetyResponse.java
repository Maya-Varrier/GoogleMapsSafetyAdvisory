package com.app.safetybackend.dto;

import com.app.safetybackend.entity.DangerousPlace;

import java.util.List;

public class SafetyResponse {

    private List<DangerousPlace> dangerousPlaces;
    private Metadata metadata;

    public SafetyResponse() {}

    public SafetyResponse(List<DangerousPlace> dangerousPlaces, Metadata metadata) {
        this.dangerousPlaces = dangerousPlaces;
        this.metadata = metadata;
    }

    public List<DangerousPlace> getDangerousPlaces() {
        return dangerousPlaces;
    }

    public void setDangerousPlaces(List<DangerousPlace> dangerousPlaces) {
        this.dangerousPlaces = dangerousPlaces;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    // ================= INNER CLASS =================
    public static class Metadata {
        private int dbCount;
        private int googleCount;

        public Metadata() {}

        public Metadata(int dbCount, int googleCount) {
            this.dbCount = dbCount;
            this.googleCount = googleCount;
        }

        public int getDbCount() {
            return dbCount;
        }

        public void setDbCount(int dbCount) {
            this.dbCount = dbCount;
        }

        public int getGoogleCount() {
            return googleCount;
        }

        public void setGoogleCount(int googleCount) {
            this.googleCount = googleCount;
        }
    }
}
