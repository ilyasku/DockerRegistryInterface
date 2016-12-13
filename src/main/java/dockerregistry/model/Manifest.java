package dockerregistry.model;

import java.util.List;

public class Manifest {
    private Blob manifestBlob;
    private Blob configBlob;
    private List<Blob> layerBlobs;

    public Blob getManifestBlob() {
        return manifestBlob;
    }

    public void setManifestBlob(Blob manifestBlob) {
        this.manifestBlob = manifestBlob;
    }

    public Blob getConfigBlob() {
        return configBlob;
    }

    public void setConfigBlob(Blob configBlob) {
        this.configBlob = configBlob;
    }

    public List<Blob> getLayerBlobs() {
        return layerBlobs;
    }

    public void setLayerBlobs(List<Blob> layerBlobs) {
        this.layerBlobs = layerBlobs;
    }    
}
