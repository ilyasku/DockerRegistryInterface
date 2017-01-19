package dockerregistry.model;

import java.util.List;

/**
 * The manifest of an image is a text file on the reigstry that defines on which
 * blobs an image depends; this is a plain object representing the same 
 * information.
 * The manifest discriminates between three kinds of blobs:
 * * The manifest itself is considered a blob.
 * * There is a single config blob.
 * * There is an arbitrary number of layer blobs.
 */
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
